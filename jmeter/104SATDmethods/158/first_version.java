/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jmeter.protocol.jms.sampler;

import java.util.Enumeration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestListener;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.engine.event.LoopIterationEvent;

import org.apache.jmeter.protocol.jms.Utils;
import org.apache.jmeter.protocol.jms.control.gui.JMSSubscriberGui;
import org.apache.jmeter.protocol.jms.client.ClientPool;
import org.apache.jmeter.protocol.jms.client.OnMessageSubscriber;
import org.apache.jmeter.protocol.jms.client.ReceiveSubscriber;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * This class implements the JMS Subcriber sampler
 */
public class SubscriberSampler extends BaseJMSSampler implements Interruptible, TestListener, ThreadListener, MessageListener {

    private static final long serialVersionUID = 240L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    // Default wait (ms) for a message if timeouts are not enabled
    // This is the maximimum time the sampler can be blocked.
    private static final long DEFAULT_WAIT = 500L;

    // No need to synch/ - only used by sampler and ClientPool (which does its own synch)
    private transient ReceiveSubscriber SUBSCRIBER = null;

    /*
     * We use a LinkedBlockingQueue (rather than a ConcurrentLinkedQueue) because it has a
     * poll-with-wait method that avoids the need to use a polling loop.
     */
    private transient LinkedBlockingQueue<Message> queue;
    
    private transient volatile boolean interrupted = false;

    private transient long timeout;

    private boolean useReceive;

    // This will be null iff initialisation succeeeds.
    private transient Exception exceptionDuringInit;

    // Don't change the string, as it is used in JMX files
    private static final String CLIENT_CHOICE = "jms.client_choice"; // $NON-NLS-1$
    private static final String TIMEOUT = "jms.timeout"; // $NON-NLS-1$
    private static final String TIMEOUT_DEFAULT = "";

    public SubscriberSampler() {
    }

    public void testEnded(String test) {
        testEnded();
    }

    public void testStarted(String test) {
        testStarted();
    }

    /**
     * testEnded is called by Jmeter's engine.
     * Clears the client pool.
     */
    public void testEnded() {
        log.debug("SubscriberSampler.testEnded called");
        ClientPool.clearClient();
    }

    /**
     * {@inheritDoc}
     */
    public void testStarted() {
    }

    /**
     * {@inheritDoc}
     */
    public void testIterationStart(LoopIterationEvent event) {
    }

    /**
     * Create the OnMessageSubscriber client and set the sampler as the message
     * listener.
     * @throws JMSException 
     * @throws NamingException 
     *
     */
    private void initListenerClient() throws JMSException, NamingException {
        OnMessageSubscriber sub = (OnMessageSubscriber) ClientPool.get(this);
        if (sub == null) {
            sub = new OnMessageSubscriber(getUseJNDIPropertiesAsBoolean(), getJNDIInitialContextFactory(),
                    getProviderUrl(), getConnectionFactory(), getDestination(), 
                    isUseAuth(), getUsername(), getPassword());
            queue = new LinkedBlockingQueue<Message>();
            sub.setMessageListener(this);
            sub.start();
            ClientPool.addClient(sub);
            ClientPool.put(this, sub);
            log.debug("SubscriberSampler.initListenerClient called");
            log.debug("loop count " + getIterations());
        }
    }

    /**
     * Create the ReceiveSubscriber client for the sampler.
     * @throws NamingException 
     * @throws JMSException 
     */
    private void initReceiveClient() throws NamingException, JMSException {
        SUBSCRIBER = new ReceiveSubscriber(getUseJNDIPropertiesAsBoolean(),
                getJNDIInitialContextFactory(), getProviderUrl(), getConnectionFactory(), getDestination(),
                isUseAuth(), getUsername(), getPassword());
        SUBSCRIBER.start();
        ClientPool.addClient(SUBSCRIBER);
        log.debug("SubscriberSampler.initReceiveClient called");
    }

    /**
     * sample method will check which client it should use and call the
     * appropriate client specific sample method.
     *
     * @return the appropriate sample result
     */
    @Override
    public SampleResult sample() {
        SampleResult result = new SampleResult();
        result.setDataType(SampleResult.TEXT);
        result.setSampleLabel(getName());
        result.sampleStart();
        if (exceptionDuringInit != null) {
            result.sampleEnd();
            result.setSuccessful(false);
            result.setResponseCode("000");
            result.setResponseMessage(exceptionDuringInit.toString());
            return result; 
        }
        if (useReceive) {
            return sampleWithReceive(result);
        } else {
            return sampleWithListener(result);
        }
    }

    /**
     * sample will block until messages are received
     * @param result 
     *
     * @return the sample result
     */
    private SampleResult sampleWithListener(SampleResult result) {
        StringBuilder buffer = new StringBuilder();
        StringBuilder propBuffer = new StringBuilder();

        int loop = getIterationCount();
        int read = 0;

        long until = 0L;
        long now = System.currentTimeMillis();
        if (timeout > 0) {
            until = timeout + now; 
        }
        while (!interrupted
                && (until == 0 || now < until)
                && read < loop) {
            try {
                Message msg = queue.poll(calculateWait(until, now), TimeUnit.MILLISECONDS);
                if (msg != null) {
                    read++;
                    extractContent(buffer, propBuffer, msg);
                }
            } catch (InterruptedException e) {
                // Ignored
            }
            now = System.currentTimeMillis();
        }
        result.sampleEnd();
       
        if (getReadResponseAsBoolean()) {
            result.setResponseData(buffer.toString().getBytes());
        } else {
            result.setBytes(buffer.toString().getBytes().length);
        }
        result.setResponseHeaders(propBuffer.toString());
        result.setDataType(SampleResult.TEXT);
        if (read == 0) {
            result.setResponseCode("404"); // Not found
            result.setSuccessful(false);
        } else { // TODO set different status if not enough messages found?
            result.setResponseCodeOK();
            result.setSuccessful(true);
        }
        result.setResponseMessage(read + " messages received");
        result.setSamplerData(loop + " messages expected");
        result.setSampleCount(read);

        return result;
    }

    /**
     * Sample method uses the ReceiveSubscriber client instead of onMessage
     * approach.
     * @param result 
     *
     * @return the sample result
     */
    private SampleResult sampleWithReceive(SampleResult result) {
        StringBuilder buffer = new StringBuilder();
        StringBuilder propBuffer = new StringBuilder();
        
        int loop = getIterationCount();
        int read = 0;

        long until = 0L;
        long now = System.currentTimeMillis();
        if (timeout > 0) {
            until = timeout + now; 
        }
        while (!interrupted
                && (until == 0 || now < until)
                && read < loop) {
            Message msg;
            try {
                msg = SUBSCRIBER.getMessage(calculateWait(until, now));
                if (msg != null){
                    read++;
                    extractContent(buffer, propBuffer, msg);
                }
            } catch (JMSException e) {
                log.warn("Error "+e.toString());
            }
            now = System.currentTimeMillis();
        }
        result.sampleEnd();
        result.setResponseMessage(read + " samples messages received");
        if (getReadResponseAsBoolean()) {
            result.setResponseData(buffer.toString().getBytes());
        } else {
            result.setBytes(buffer.toString().getBytes().length);
        }
        result.setResponseHeaders(propBuffer.toString());
        if (read == 0) {
            result.setResponseCode("404"); // Not found
            result.setSuccessful(false);
        } else { // TODO set different status if not enough messages found?
            result.setResponseCodeOK();
            result.setSuccessful(true);
        }
        result.setResponseMessage(read + " message(s) received successfully");
        result.setSamplerData(loop + " messages expected");
        result.setSampleCount(read);

        return result;
    }

    /**
     * Calculate the wait time, will never be more than DEFAULT_WAIT.
     * 
     * @param until target end time or 0 if timeouts not active
     * @param now current time
     * @return wait time
     */
    private long calculateWait(long until, long now) {
        if (until == 0) return DEFAULT_WAIT; // Timeouts not active
        long wait = until - now; // How much left
        return wait > DEFAULT_WAIT ? DEFAULT_WAIT : wait;
    }

    private void extractContent(StringBuilder buffer, StringBuilder propBuffer,
            Message msg) {
        if (msg != null) {
            try {
                if (msg instanceof TextMessage){
                    buffer.append(((TextMessage) msg).getText());
                } else if (msg instanceof MapMessage){
                    MapMessage mapm = (MapMessage) msg;
                    @SuppressWarnings("unchecked") // MapNames are Strings
                    Enumeration<String> enumb = mapm.getMapNames();
                    while(enumb.hasMoreElements()){
                        String name = enumb.nextElement();
                        Object obj = mapm.getObject(name);
                        buffer.append(name);
                        buffer.append(",");
                        buffer.append(obj.getClass().getCanonicalName());
                        buffer.append(",");
                        buffer.append(obj);
                        buffer.append("\n");
                    }
                }
                Utils.messageProperties(propBuffer, msg);
            } catch (JMSException e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * The sampler implements MessageListener directly and sets itself as the
     * listener with the MessageConsumer.
     */
    public void onMessage(Message message) {
        if (!queue.offer(message)){
            log.warn("Could not add message to queue");
        }
    }

    // ----------- get/set methods ------------------- //
    /**
     * Set the client choice. There are two options: ReceiveSusbscriber and
     * OnMessageSubscriber.
     */
    public void setClientChoice(String choice) {
        setProperty(CLIENT_CHOICE, choice);
    }

    /**
     * Return the client choice.
     *
     * @return the client choice, either RECEIVE_RSC or ON_MESSAGE_RSC
     */
    public String getClientChoice() {
        String choice = getPropertyAsString(CLIENT_CHOICE);
        // Convert the old test plan entry (which is the language dependent string) to the resource name
        if (choice.equals(RECEIVE_STR)){
            choice = JMSSubscriberGui.RECEIVE_RSC;
        } else if (!choice.equals(JMSSubscriberGui.RECEIVE_RSC)){
            choice = JMSSubscriberGui.ON_MESSAGE_RSC;
        }
        return choice;
    }

    public String getTimeout(){
        return getPropertyAsString(TIMEOUT, TIMEOUT_DEFAULT);
    }

    public long getTimeoutAsLong(){
        return getPropertyAsLong(TIMEOUT, 0L);
    }

    public void setTimeout(String timeout){
        setProperty(TIMEOUT, timeout, TIMEOUT_DEFAULT);        
    }

    /**
     * Handle an interrupt of the test.
     */
    public boolean interrupt() {
        boolean oldvalue = interrupted;
        interrupted = true;   // so we break the loops in SampleWithListener and SampleWithReceive
        return !oldvalue;
    }

    // This was the old value that was checked for
    private final static String RECEIVE_STR = JMeterUtils.getResString(JMSSubscriberGui.RECEIVE_RSC); // $NON-NLS-1$

    public void threadFinished() {
    }

    public void threadStarted() {
        timeout = getTimeoutAsLong();
        interrupted = false;
        exceptionDuringInit = null;
        useReceive = getClientChoice().equals(JMSSubscriberGui.RECEIVE_RSC);
        if (useReceive) {
            try {
                initReceiveClient();
            } catch (NamingException e) {
                exceptionDuringInit = e;
            } catch (JMSException e) {
                exceptionDuringInit = e;
            }
        } else {
            try {
                initListenerClient();
            } catch (JMSException e) {
                exceptionDuringInit = e;
            } catch (NamingException e) {
                exceptionDuringInit = e;
            }
        }
        if (exceptionDuringInit != null){
            log.error("Could not initialise client",exceptionDuringInit);
        }
    }
}
