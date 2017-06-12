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

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.apache.jmeter.protocol.jms.Utils;
import org.apache.jmeter.protocol.jms.client.ReceiveSubscriber;
import org.apache.jmeter.protocol.jms.control.gui.JMSSubscriberGui;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * This class implements the JMS Subcriber sampler.
 * It supports both receive and onMessage strategies via the ReceiveSubscriber class.
 * 
 */
// TODO: do we need to implement any kind of connection pooling?
// If so, which connections should be shared?
// Should threads share connections to the same destination?
// What about cross-thread sharing?

// Note: originally the code did use the ClientPool to "share" subscribers, however since the
// key was "this" and each sampler is unique - nothing was actually shared.

public class SubscriberSampler extends BaseJMSSampler implements Interruptible, ThreadListener {

    private static final long serialVersionUID = 240L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    // Default wait (ms) for a message if timeouts are not enabled
    // This is the maximimum time the sampler can be blocked.
    private static final long DEFAULT_WAIT = 500L;

    // No need to synch/ - only used by sampler and ClientPool (which does its own synch)
    private transient ReceiveSubscriber SUBSCRIBER = null;

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

    /**
     * Create the OnMessageSubscriber client and set the sampler as the message
     * listener.
     * @throws JMSException 
     * @throws NamingException 
     *
     */
    private void initListenerClient() throws JMSException, NamingException {
        SUBSCRIBER = new ReceiveSubscriber(0, getUseJNDIPropertiesAsBoolean(), getJNDIInitialContextFactory(),
                    getProviderUrl(), getConnectionFactory(), getDestination(), 
                    isUseAuth(), getUsername(), getPassword());
        log.debug("SubscriberSampler.initListenerClient called");
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
        log.debug("SubscriberSampler.initReceiveClient called");
    }

    /**
     * sample method will check which client it should use and call the
     * appropriate client specific sample method.
     *
     * @return the appropriate sample result
     */
    // TODO - should we call start() and stop()?
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
     * Initialise the thread-local variables.
     * <br/>
     * {@inheritDoc}
     */
    public void threadStarted() {
        timeout = getTimeoutAsLong();
        interrupted = false;
        exceptionDuringInit = null;
        useReceive = getClientChoice().equals(JMSSubscriberGui.RECEIVE_RSC);
        if (useReceive) {
            try {
                initReceiveClient();
                SUBSCRIBER.start();
            } catch (NamingException e) {
                exceptionDuringInit = e;
            } catch (JMSException e) {
                exceptionDuringInit = e;
            }
        } else {
            try {
                initListenerClient();
                SUBSCRIBER.start();
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

    /**
     * Close subscriber.
     * <br/>
     * {@inheritDoc}
     */
    public void threadFinished() {
        SUBSCRIBER.close();
    }

    /**
     * Handle an interrupt of the test.
     */
    public boolean interrupt() {
        boolean oldvalue = interrupted;
        interrupted = true;   // so we break the loops in SampleWithListener and SampleWithReceive
        return !oldvalue;
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

    // This was the old value that was checked for
    private final static String RECEIVE_STR = JMeterUtils.getResString(JMSSubscriberGui.RECEIVE_RSC); // $NON-NLS-1$

}
