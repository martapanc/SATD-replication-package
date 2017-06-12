diff --git a/src/core/org/apache/jmeter/samplers/AsynchSampleSender.java b/src/core/org/apache/jmeter/samplers/AsynchSampleSender.java
index 622064ff3..9e94c7bc4 100644
--- a/src/core/org/apache/jmeter/samplers/AsynchSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/AsynchSampleSender.java
@@ -1,169 +1,171 @@
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
  * 
  */
 
 package org.apache.jmeter.samplers;
 
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.rmi.RemoteException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.concurrent.ArrayBlockingQueue;
 import java.util.concurrent.BlockingQueue;
 
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Sends samples in a separate Thread and in Batch mode
  */
 public class AsynchSampleSender extends AbstractSampleSender implements Serializable {
 
-    private static final long serialVersionUID = 251L;
+    private static final long serialVersionUID = 252L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AsynchSampleSender.class);
 
     // Create unique object as marker for end of queue
     private static transient final SampleEvent FINAL_EVENT = new SampleEvent();
 
     private static final int DEFAULT_QUEUE_SIZE = 100;
     
     private static final int SERVER_CONFIGURED_CAPACITY = JMeterUtils.getPropDefault("asynch.batch.queue.size", DEFAULT_QUEUE_SIZE); // $NON-NLS-1$
     
     private final int clientConfiguredCapacity = JMeterUtils.getPropDefault("asynch.batch.queue.size", DEFAULT_QUEUE_SIZE); // $NON-NLS-1$
 
     // created by client 
     private final RemoteSampleListener listener;
 
     private transient BlockingQueue<SampleEvent> queue; // created by server in readResolve method
     
     private transient long queueWaits; // how many times we had to wait to queue a sample
     
     private transient long queueWaitTime; // how long we had to wait (nanoSeconds)
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public AsynchSampleSender(){
         this(null);
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
 
     // Created by SampleSenderFactory
     protected AsynchSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
-        log.info("Using Asynch Remote Sampler for this test run, queue size "+getCapacity());  // client log file
+        if (log.isInfoEnabled()) {
+            log.info("Using Asynch Remote Sampler for this test run, queue size: {}", getCapacity());  // client log file
+        }
     }
     
     /**
      * Processed by the RMI server code.
      *
      * @return this
      * @throws ObjectStreamException never
      */
     protected Object readResolve() throws ObjectStreamException{
         int capacity = getCapacity();
-        log.info("Using batch queue size (asynch.batch.queue.size): " + capacity); // server log file
+        log.info("Using batch queue size (asynch.batch.queue.size): {}", capacity); // server log file
         queue = new ArrayBlockingQueue<>(capacity);
         Worker worker = new Worker(queue, listener);
         worker.setDaemon(true);
         worker.start();
         return this;
     }
 
     /**
      * @return capacity
      */
     private int getCapacity() {
         return isClientConfigured() ? 
                 clientConfiguredCapacity : SERVER_CONFIGURED_CAPACITY;
     }
     
     @Override
     public void testEnded(String host) {
-        log.debug("Test Ended on " + host);
+        log.debug("Test Ended on {}", host);
         try {
             listener.testEnded(host);
             queue.put(FINAL_EVENT);
         } catch (Exception ex) {
-            log.warn("testEnded(host)"+ex);
+            log.warn("testEnded(host)", ex);
         }
         if (queueWaits > 0) {
-            log.info("QueueWaits: "+queueWaits+"; QueueWaitTime: "+queueWaitTime+" (nanoseconds)");            
+            log.info("QueueWaits: {}; QueueWaitTime: {} (nanoseconds)", queueWaits, queueWaitTime);
         }
     }
 
     @Override
     public void sampleOccurred(SampleEvent e) {
         try {
             if (!queue.offer(e)){ // we failed to add the element first time
                 queueWaits++;
                 long t1 = System.nanoTime();
                 queue.put(e);
                 long t2 = System.nanoTime();
                 queueWaitTime += t2-t1;
             }
         } catch (Exception err) {
             log.error("sampleOccurred; failed to queue the sample", err);
         }
     }
 
     private static class Worker extends Thread {
         
         private final BlockingQueue<SampleEvent> queue;
         
         private final RemoteSampleListener listener;
         
         private Worker(BlockingQueue<SampleEvent> q, RemoteSampleListener l){
             queue = q;
             listener = l;
         }
 
         @Override
         public void run() {
             try {
                 boolean eof = false;
                 while (!eof) {
                     List<SampleEvent> l = new ArrayList<>();
                     SampleEvent e = queue.take();
                     while (!(eof = (e == FINAL_EVENT)) && e != null) { // try to process as many as possible
                         l.add(e);
                         e = queue.poll(); // returns null if nothing on queue currently
                     }
                     int size = l.size();
                     if (size > 0) {
                         try {
                             listener.processBatch(l);
                         } catch (RemoteException err) {
                             if (err.getCause() instanceof java.net.ConnectException){
                                 throw new JMeterError("Could not return sample",err);
                             }
                             log.error("Failed to return sample", err);
                         }
                     }
                 }
             } catch (InterruptedException e) {
                 Thread.currentThread().interrupt();
             }
             log.debug("Worker ended");
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/BatchSampleSender.java b/src/core/org/apache/jmeter/samplers/BatchSampleSender.java
index 190e95686..a2de98a92 100644
--- a/src/core/org/apache/jmeter/samplers/BatchSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/BatchSampleSender.java
@@ -1,212 +1,209 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
-import org.apache.jmeter.util.JMeterUtils;
-import org.apache.log.Logger;
-import org.apache.jorphan.logging.LoggingManager;
-
-import java.util.List;
-import java.util.ArrayList;
-import java.rmi.RemoteException;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
+import java.rmi.RemoteException;
+import java.util.ArrayList;
+import java.util.List;
+
+import org.apache.jmeter.util.JMeterUtils;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Implements batch reporting for remote testing.
  *
  */
 public class BatchSampleSender extends AbstractSampleSender implements Serializable {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BatchSampleSender.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private static final int DEFAULT_NUM_SAMPLE_THRESHOLD = 100;
 
     private static final long DEFAULT_TIME_THRESHOLD = 60000L;
 
     // Static fields are resolved on the server
     private static final int NUM_SAMPLES_THRESHOLD = 
         JMeterUtils.getPropDefault("num_sample_threshold", DEFAULT_NUM_SAMPLE_THRESHOLD); // $NON-NLS-1$
 
     private static final long TIME_THRESHOLD_MS =
         JMeterUtils.getPropDefault("time_threshold", DEFAULT_TIME_THRESHOLD); // $NON-NLS-1$
 
     // instance fields are copied from the client instance
     private final int clientConfiguredNumSamplesThreshold = 
             JMeterUtils.getPropDefault("num_sample_threshold", DEFAULT_NUM_SAMPLE_THRESHOLD); // $NON-NLS-1$
 
     private final long clientConfiguredTimeThresholdMs =
             JMeterUtils.getPropDefault("time_threshold", DEFAULT_TIME_THRESHOLD); // $NON-NLS-1$
 
     private final RemoteSampleListener listener;
 
     private final List<SampleEvent> sampleStore = new ArrayList<>();
 
     // Server-only work item
     private transient long batchSendTime = -1;
 
     // Configuration items, set up by readResolve
     private transient volatile int numSamplesThreshold;
 
     private transient volatile long timeThresholdMs;
 
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public BatchSampleSender(){
         this(null);
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
     /**
      * Constructor
      *
      * @param listener
      *            that the List of sample events will be sent to.
      */
     // protected added: Bug 50008 - allow BatchSampleSender to be subclassed
     protected BatchSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         if (isClientConfigured()) {
-            log.info("Using batching (client settings) for this run."
-                    + " Thresholds: num=" + clientConfiguredNumSamplesThreshold
-                    + ", time=" + clientConfiguredTimeThresholdMs);
+            log.info("Using batching (client settings) for this run. Thresholds: num={}, time={}",
+                    clientConfiguredNumSamplesThreshold, clientConfiguredTimeThresholdMs);
         } else {
             log.info("Using batching (server settings) for this run.");
         }
     }
 
    /**
     * @return the listener
     */
     // added: Bug 50008 - allow BatchSampleSender to be subclassed
    protected RemoteSampleListener getListener() {
        return listener;
    }
 
    /**
     * @return the sampleStore
     */
    // added: Bug 50008 - allow BatchSampleSender to be subclassed
    protected List<SampleEvent> getSampleStore() {
        return sampleStore;
    }
 
     /**
      * Checks if any sample events are still present in the sampleStore and
      * sends them to the listener. Informs the listener of the testended.
      *
      * @param host
      *            the host that the test has ended on.
      */
     @Override
     public void testEnded(String host) {
-        log.info("Test Ended on " + host);
+        log.info("Test Ended on {}", host);
         try {
             if (sampleStore.size() != 0) {
                 listener.processBatch(sampleStore);
                 sampleStore.clear();
             }
             listener.testEnded(host);
         } catch (RemoteException err) {
             log.error("testEnded(host)", err);
         }
     }
 
     /**
      * Stores sample events untill either a time or sample threshold is
      * breached. Both thresholds are reset if one fires. If only one threshold
      * is set it becomes the only value checked against. When a threhold is
      * breached the list of sample events is sent to a listener where the event
      * are fired locally.
      *
      * @param e
      *            a Sample Event
      */
     @Override
     public void sampleOccurred(SampleEvent e) {
         List<SampleEvent> clonedStore = null;
         synchronized (sampleStore) {
             sampleStore.add(e);
             final int sampleCount = sampleStore.size();
 
             boolean sendNow = false;            
             if (numSamplesThreshold != -1) {
                 if (sampleCount >= numSamplesThreshold) {
                     sendNow = true;
                 }
             }
 
             long now = 0;
             if (timeThresholdMs != -1) {
                 now = System.currentTimeMillis();
                 // Checking for and creating initial timestamp to check against
                 if (batchSendTime == -1) {
                     this.batchSendTime = now + timeThresholdMs;
                 }
                 if (batchSendTime < now && sampleCount > 0) {
                     sendNow = true;
                 }
             }
 
             if (sendNow){
                 @SuppressWarnings("unchecked") // OK because sampleStore is of type ArrayList<SampleEvent>
                 final ArrayList<SampleEvent> clone = (ArrayList<SampleEvent>)((ArrayList<SampleEvent>)sampleStore).clone();
                 clonedStore = clone;
                 sampleStore.clear();
                 if (timeThresholdMs != -1) {
                     this.batchSendTime = now + timeThresholdMs;
                 }
             }
         } // synchronized(sampleStore)
         
         if (clonedStore != null){
             try {
                 log.debug("Firing sample");
                 listener.processBatch(clonedStore);
                 clonedStore.clear();
             } catch (RemoteException err) {
                 log.error("sampleOccurred", err);
             }  
         }
     }
     
     /**
      * Processed by the RMI server code; acts as testStarted().
      *
      * @return this
      * @throws ObjectStreamException
      *             never
      */
     private Object readResolve() throws ObjectStreamException{
         if (isClientConfigured()) {
             numSamplesThreshold = clientConfiguredNumSamplesThreshold;
             timeThresholdMs = clientConfiguredTimeThresholdMs;
         } else {
             numSamplesThreshold =  NUM_SAMPLES_THRESHOLD;
             timeThresholdMs = TIME_THRESHOLD_MS;
         }
-        log.info("Using batching for this run."
-                + " Thresholds: num=" + numSamplesThreshold
-                + ", time=" + timeThresholdMs); 
+        log.info("Using batching for this run. Thresholds: num={}, time={}", numSamplesThreshold, timeThresholdMs);
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java b/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java
index c59096dbe..3db4978f7 100644
--- a/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java
@@ -1,143 +1,143 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.rmi.RemoteException;
 
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The standard remote sample reporting should be more friendly to the main purpose of
  * remote testing - which is scalability.  To increase scalability, this class strips out the
  * response data before sending.
  *
  *
  */
 public class DataStrippingSampleSender extends AbstractSampleSender implements Serializable {
 
-    private static final long serialVersionUID = -5556040298982085715L;
+    private static final long serialVersionUID = 1L;
 
     /** empty array which can be returned instead of null */
     private static final byte[] EMPTY_BA = new byte[0];
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(DataStrippingSampleSender.class);
 
     private static final boolean DEFAULT_STRIP_ALSO_ON_ERROR = true;
     
     private static final boolean SERVER_CONFIGURED_STRIP_ALSO_ON_ERROR = 
             JMeterUtils.getPropDefault("sample_sender_strip_also_on_error", DEFAULT_STRIP_ALSO_ON_ERROR); // $NON-NLS-1$
 
     // instance fields are copied from the client instance
     private final boolean clientConfiguredStripAlsoOnError = 
             JMeterUtils.getPropDefault("sample_sender_strip_also_on_error", DEFAULT_STRIP_ALSO_ON_ERROR); // $NON-NLS-1$
     
     
     private final RemoteSampleListener listener;
     private final SampleSender decoratedSender;
     // Configuration items, set up by readResolve
     private transient volatile boolean stripAlsoOnError;
 
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public DataStrippingSampleSender(){
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
         listener = null;
         decoratedSender = null;
     }
 
     DataStrippingSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         decoratedSender = null;
         log.info("Using DataStrippingSampleSender for this run");
     }
 
     DataStrippingSampleSender(SampleSender decorate)
     {
         this.decoratedSender = decorate;
         this.listener = null;
         log.info("Using DataStrippingSampleSender for this run");
     }
 
     @Override
     public void testEnded(String host) {
-        log.info("Test Ended on " + host);
+        log.info("Test Ended on {}", host);
         if(decoratedSender != null) { 
             decoratedSender.testEnded(host);
         }
     }
 
     @Override
     public void sampleOccurred(SampleEvent event) {
         //Strip the response data before writing, but only for a successful request.
         SampleResult result = event.getResult();
         if(stripAlsoOnError || result.isSuccessful()) {
             // Compute bytes before stripping
             stripResponse(result);
             // see Bug 57449
             for (SampleResult subResult : result.getSubResults()) {
                 stripResponse(subResult);                
             }
         }
         if(decoratedSender == null)
         {
             try {
                 listener.sampleOccurred(event);
             } catch (RemoteException e) {
-                log.error("Error sending sample result over network ",e);
+                log.error("Error sending sample result over network", e);
             }
         }
         else
         {
             decoratedSender.sampleOccurred(event);
         }
     }
 
     /**
      * Strip response but fill in bytes field.
      * @param result {@link SampleResult}
      */
     private void stripResponse(SampleResult result) {
         result.setBytes(result.getBytesAsLong());
         result.setResponseData(EMPTY_BA);
     }
 
     /**
      * Processed by the RMI server code; acts as testStarted().
      *
      * @return this
      * @throws ObjectStreamException
      *             never
      */
     private Object readResolve() throws ObjectStreamException{
         if (isClientConfigured()) {
             stripAlsoOnError = clientConfiguredStripAlsoOnError;
         } else {
             stripAlsoOnError = SERVER_CONFIGURED_STRIP_ALSO_ON_ERROR;
         }
-        log.info("Using DataStrippingSampleSender for this run with stripAlsoOnError:"+stripAlsoOnError);
+        log.info("Using DataStrippingSampleSender for this run with stripAlsoOnError: {}", stripAlsoOnError);
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/DiskStoreSampleSender.java b/src/core/org/apache/jmeter/samplers/DiskStoreSampleSender.java
index 48c070075..057e60992 100644
--- a/src/core/org/apache/jmeter/samplers/DiskStoreSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/DiskStoreSampleSender.java
@@ -1,174 +1,176 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
-import org.apache.log.Logger;
-import org.apache.commons.io.IOUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.jorphan.util.JMeterError;
-
 import java.io.EOFException;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.ObjectStreamException;
 import java.io.OutputStream;
 import java.io.Serializable;
 import java.rmi.RemoteException;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.TimeUnit;
 
+import org.apache.commons.io.IOUtils;
+import org.apache.jorphan.util.JMeterError;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
 /**
  * Version of HoldSampleSender that stores the samples on disk as a serialised stream.
  */
 
 public class DiskStoreSampleSender extends AbstractSampleSender implements Serializable {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(DiskStoreSampleSender.class);
 
-    private static final long serialVersionUID = 252L;
+    private static final long serialVersionUID = 253L;
 
     private final RemoteSampleListener listener;
 
     private transient volatile ObjectOutputStream oos;
     private transient volatile File temporaryFile;
     private transient volatile ExecutorService singleExecutor;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public DiskStoreSampleSender(){
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
         listener = null;
     }
 
     DiskStoreSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         log.info("Using DiskStoreSampleSender for this test run"); // client log file
     }
 
     @Override
     public void testEnded(String host) {
-        log.info("Test Ended on " + host);
+        log.info("Test Ended on {}", host);
         singleExecutor.submit(new Runnable(){
             @Override
             public void run() {
                 try {
                     oos.close(); // ensure output is flushed
                 } catch (IOException e) {
-                    log.error("Failed to close data file ", e);
+                    log.error("Failed to close data file.", e);
                 }                
             }});
         singleExecutor.shutdown(); // finish processing samples
         try {
             if (!singleExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                 log.error("Executor did not terminate in a timely fashion");
             }
-        } catch (InterruptedException e1) {
-            log.error("Executor did not terminate in a timely fashion", e1);
+        } catch (InterruptedException e) {
+            log.error("Executor did not terminate in a timely fashion", e);
             Thread.currentThread().interrupt();
         }
         ObjectInputStream ois = null;
         try {
             ois = new ObjectInputStream(new FileInputStream(temporaryFile));
             Object obj = null;
             while((obj = ois.readObject()) != null) {
                 if (obj instanceof SampleEvent) {
                     try {
                         listener.sampleOccurred((SampleEvent) obj);
                     } catch (RemoteException err) {
                         if (err.getCause() instanceof java.net.ConnectException){
                             throw new JMeterError("Could not return sample",err);
                         }
                         log.error("returning sample", err);
                     }
                 } else {
-                    log.error("Unexpected object type found in data file "+obj.getClass().getName());
+                    log.error("Unexpected object type found in data file. {}", obj.getClass());
                 }
             }                    
         } catch (EOFException err) {
             // expected
         } catch (IOException | ClassNotFoundException err) {
             log.error("returning sample", err);
         } finally {
             try {
                 listener.testEnded(host);
             } catch (RemoteException e) {
                 log.error("returning sample", e);
             }
             IOUtils.closeQuietly(ois);
             if(!temporaryFile.delete()) {
-                log.warn("Could not delete file:"+temporaryFile.getAbsolutePath());
+                if (log.isWarnEnabled()) {
+                    log.warn("Could not delete file: {}", temporaryFile.getAbsolutePath());
+                }
             }
         }
     }
 
     @Override
     public void sampleOccurred(final SampleEvent e) {
         // sampleOccurred is called from multiple threads; not safe to write from multiple threads.
         // also decouples the file IO from sample generation
         singleExecutor.submit(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         oos.writeObject(e);
                     } catch (IOException err) {
                         log.error("sampleOccurred", err);
                     }                
                 }
             
             }
         );
     }
 
     /**
      * Processed by the RMI server code; acts as testStarted().
      *
      * @return this
      * @throws ObjectStreamException
      *             never
      */
     // TODO should errors be thrown back through RMI?
     private Object readResolve() throws ObjectStreamException{
         log.info("Using DiskStoreSampleSender for this test run"); // server log file
         singleExecutor = Executors.newSingleThreadExecutor();
         try {
             temporaryFile = File.createTempFile("SerialisedSampleSender", ".ser");
             temporaryFile.deleteOnExit();
             singleExecutor.submit(new Runnable(){
                 @Override
                 public void run() {
                     OutputStream anOutputStream;
                     try {
                         anOutputStream = new FileOutputStream(temporaryFile);
                         oos = new ObjectOutputStream(anOutputStream);
                     } catch (IOException e) {
                         log.error("Failed to create output Stream", e);
                     }
                 }});
         } catch (IOException e) {
             log.error("Failed to create output file", e);
         }
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/HoldSampleSender.java b/src/core/org/apache/jmeter/samplers/HoldSampleSender.java
index e46747469..5b7ff943e 100644
--- a/src/core/org/apache/jmeter/samplers/HoldSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/HoldSampleSender.java
@@ -1,89 +1,90 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.util.concurrent.ConcurrentLinkedQueue;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
 
 /**
  * Lars-Erik Helander provided the idea (and original implementation) for the
  * caching functionality (sampleStore).
  */
 public class HoldSampleSender extends AbstractSampleSender implements Serializable {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HoldSampleSender.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private final ConcurrentLinkedQueue<SampleEvent> sampleStore = new ConcurrentLinkedQueue<>();
 
     private final RemoteSampleListener listener;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public HoldSampleSender(){
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
         listener = null;
     }
 
     HoldSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         log.warn("Using HoldSampleSender for this test run, ensure you have configured enough memory (-Xmx) for your test"); // client        
     }
 
     @Override
     public void testEnded(String host) {
-        log.info("Test Ended on " + host);
+        log.info("Test Ended on {}", host);
         try {
             for (SampleEvent se : sampleStore) {
                 listener.sampleOccurred(se);
             }
             listener.testEnded(host);
             sampleStore.clear();
         } catch (Error | RuntimeException ex) { // NOSONAR We want to have errors logged in log file
             log.error("testEnded(host)", ex);
             throw ex;
         } catch (Exception ex) { 
             log.error("testEnded(host)", ex);
         }
 
     }
 
     @Override
     public void sampleOccurred(SampleEvent e) {
         sampleStore.add(e);
     }
 
     /**
      * Processed by the RMI server code; acts as testStarted().
      *
      * @return this
      * @throws ObjectStreamException
      *             never
      */
     protected Object readResolve() throws ObjectStreamException{
         log.warn("Using HoldSampleSender for this test run, ensure you have configured enough memory (-Xmx) for your test"); // server        
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/RemoteListenerWrapper.java b/src/core/org/apache/jmeter/samplers/RemoteListenerWrapper.java
index 6dca978b4..43b38a1d3 100644
--- a/src/core/org/apache/jmeter/samplers/RemoteListenerWrapper.java
+++ b/src/core/org/apache/jmeter/samplers/RemoteListenerWrapper.java
@@ -1,125 +1,125 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 import java.rmi.RemoteException;
 
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestStateListener;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  *
  * Lars-Erik Helander provided the idea (and original implementation) for the
  * caching functionality (sampleStore).
  */
 public class RemoteListenerWrapper extends AbstractTestElement implements SampleListener, TestStateListener, Serializable,
         NoThreadClone {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RemoteListenerWrapper.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private final RemoteSampleListener listener;
 
     private final SampleSender sender;
 
     public RemoteListenerWrapper(RemoteSampleListener l) {
         listener = l;
         // Get appropriate sender class governed by the behaviour set in the JMeter property
         sender = SampleSenderFactory.getInstance(listener);
     }
 
     public RemoteListenerWrapper() // TODO: not used - make private?
     {
         listener = null;
         sender = null;
     }
 
     @Override
     public void testStarted() {
         log.debug("Test Started()");
         try {
             listener.testStarted();
         } catch (Error | RuntimeException ex) { // NOSONAR We want to have errors logged in log file
             log.error("testStarted()", ex);
             throw ex;
         } catch (Exception ex) {
             log.error("testStarted()", ex);
         }
     }
 
     @Override
     public void testEnded() {
         sender.testEnded();
     }
 
     @Override
     public void testStarted(String host) {
-        log.debug("Test Started on " + host);
+        log.debug("Test Started on {}", host);
         try {
             listener.testStarted(host);
         } catch (Error | RuntimeException ex) { // NOSONAR We want to have errors logged in log file
-            log.error("testStarted(host) on "+host, ex);
+            log.error("testStarted(host) on {}", host, ex);
             throw ex;
         } catch(Exception ex) {
-            log.error("testStarted(host) on "+host, ex);
+            log.error("testStarted(host) on {}", host, ex);
         }
     }
 
     @Override
     public void testEnded(String host) {
         sender.testEnded(host);
     }
 
     @Override
     public void sampleOccurred(SampleEvent e) {
         sender.sampleOccurred(e);
     }
 
     // Note that sampleStarted() and sampleStopped() is not made to appear
     // in synch with sampleOccured() when replaying held samples.
     // For now this is not critical since sampleStarted() and sampleStopped()
     // is not used, but it may become an issue in the future. Then these
     // events must also be stored so that replay of all events may occur and
     // in the right order. Each stored event must then be tagged with something
     // that lets you distinguish between occured, started and ended.
 
     @Override
     public void sampleStarted(SampleEvent e) {
         log.debug("Sample started");
         try {
             listener.sampleStarted(e);
         } catch (RemoteException err) {
             log.error("sampleStarted", err);
         }
     }
 
     @Override
     public void sampleStopped(SampleEvent e) {
         log.debug("Sample stopped");
         try {
             listener.sampleStopped(e);
         } catch (RemoteException err) {
             log.error("sampleStopped", err);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/RemoteSampleListenerWrapper.java b/src/core/org/apache/jmeter/samplers/RemoteSampleListenerWrapper.java
index e4205735c..93af80ec2 100644
--- a/src/core/org/apache/jmeter/samplers/RemoteSampleListenerWrapper.java
+++ b/src/core/org/apache/jmeter/samplers/RemoteSampleListenerWrapper.java
@@ -1,74 +1,73 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 import java.rmi.RemoteException;
 
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.testelement.AbstractTestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
- * @version $Revision$
+ * 
  */
-
 public class RemoteSampleListenerWrapper extends AbstractTestElement implements SampleListener, Serializable,
         NoThreadClone {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RemoteSampleListenerWrapper.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private RemoteSampleListener listener;
 
     public RemoteSampleListenerWrapper(RemoteSampleListener l) {
         listener = l;
     }
 
     public RemoteSampleListenerWrapper() {
     }
 
     @Override
     public void sampleOccurred(SampleEvent e) {
         try {
             listener.sampleOccurred(e);
         } catch (RemoteException err) {
-            log.error("", err); // $NON-NLS-1$
+            log.error("RemoteException while handling sample occurred event.", err); // $NON-NLS-1$
         }
     }
 
     @Override
     public void sampleStarted(SampleEvent e) {
         try {
             listener.sampleStarted(e);
         } catch (RemoteException err) {
-            log.error("", err); // $NON-NLS-1$
+            log.error("RemoteException while handling sample started event.", err); // $NON-NLS-1$
         }
     }
 
     @Override
     public void sampleStopped(SampleEvent e) {
         try {
             listener.sampleStopped(e);
         } catch (RemoteException err) {
-            log.error("", err); // $NON-NLS-1$
+            log.error("RemoteException while handling sample stopped event.", err); // $NON-NLS-1$
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/RemoteTestListenerWrapper.java b/src/core/org/apache/jmeter/samplers/RemoteTestListenerWrapper.java
index d6e43c5fb..dfcb35f32 100644
--- a/src/core/org/apache/jmeter/samplers/RemoteTestListenerWrapper.java
+++ b/src/core/org/apache/jmeter/samplers/RemoteTestListenerWrapper.java
@@ -1,85 +1,84 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestStateListener;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
- * @version $Revision$
  */
 public class RemoteTestListenerWrapper extends AbstractTestElement implements TestStateListener, Serializable, NoThreadClone {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RemoteTestListenerWrapper.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private final RemoteSampleListener listener;
 
     public RemoteTestListenerWrapper() {
         log.warn("Only intended for use in testing");
         listener = null;
     }
 
     public RemoteTestListenerWrapper(RemoteSampleListener l) {
         listener = l;
     }
 
     @Override
     public void testStarted() {
         try {
             listener.testStarted();
         } catch (Exception ex) {
-            log.error("", ex); // $NON-NLS-1$
+            log.error("Exception on testStarted.", ex); // $NON-NLS-1$
         }
 
     }
 
     @Override
     public void testEnded() {
         try {
             listener.testEnded();
         } catch (Exception ex) {
-            log.error("", ex); // $NON-NLS-1$
+            log.error("Exception on testEnded.", ex); // $NON-NLS-1$
         }
     }
 
     @Override
     public void testStarted(String host) {
         try {
             listener.testStarted(host);
         } catch (Exception ex) {
-            log.error("", ex); // $NON-NLS-1$
+            log.error("Exception on testStarted on host {}", host, ex); // $NON-NLS-1$
         }
     }
 
     @Override
     public void testEnded(String host) {
         try {
             listener.testEnded(host);
         } catch (Exception ex) {
-            log.error("", ex); // $NON-NLS-1$
+            log.error("Exception on testEnded on host {}", host, ex); // $NON-NLS-1$
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/samplers/SampleEvent.java b/src/core/org/apache/jmeter/samplers/SampleEvent.java
index b00b9a315..bd2674434 100644
--- a/src/core/org/apache/jmeter/samplers/SampleEvent.java
+++ b/src/core/org/apache/jmeter/samplers/SampleEvent.java
@@ -1,229 +1,231 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 import java.util.Arrays;
-    
+
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Packages information regarding the target of a sample event, such as the
  * result from that event and the thread group it ran in.
  */
 public class SampleEvent implements Serializable {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SampleEvent.class);
 
-    private static final long serialVersionUID = 232L;
+    private static final long serialVersionUID = 233L;
 
     /** The property {@value} is used to define additional variables to be saved */
     public static final String SAMPLE_VARIABLES = "sample_variables"; // $NON-NLS-1$
 
     public static final String HOSTNAME;
 
     // List of variable names to be saved in JTL files
     private static volatile String[] variableNames = new String[0];
 
     // The values. Entries may be null, but there will be the correct number.
     private final String[] values;
 
     // The hostname cannot change during a run, so safe to cache it just once
     static {
         HOSTNAME=JMeterUtils.getLocalHostName();
         initSampleVariables();
     }
 
     /**
      * Set up the additional variable names to be saved
      * from the value in the {@link #SAMPLE_VARIABLES} property
      */
     public static void initSampleVariables() {
         String vars = JMeterUtils.getProperty(SAMPLE_VARIABLES);
         variableNames=vars != null ? vars.split(",") : new String[0];
-        log.info("List of sample_variables: " + Arrays.toString(variableNames));
+        if (log.isInfoEnabled()) {
+            log.info("List of sample_variables: {}", Arrays.toString(variableNames));
+        }
     }
 
     private final SampleResult result;
 
     private final String threadGroup; // TODO appears to duplicate the threadName field in SampleResult
 
     private final String hostname;
 
     private final boolean isTransactionSampleEvent;
 
     /**
      * Constructor used for Unit tests only. Uses <code>null</code> for the
      * associated {@link SampleResult} and the <code>threadGroup</code>-name.
      */
     public SampleEvent() {
         this(null, null);
     }
 
     /**
      * Creates SampleEvent without saving any variables.
      * <p>
      * Use by {@link org.apache.jmeter.protocol.http.proxy.ProxyControl
      * ProxyControl} and {@link StatisticalSampleSender}.
      *
      * @param result
      *            The SampleResult to be associated with this event
      * @param threadGroup
      *            The name of the thread, the {@link SampleResult} was recorded
      */
     public SampleEvent(SampleResult result, String threadGroup) {
         this(result, threadGroup, HOSTNAME, false);
     }
 
     /**
      * Constructor used for normal samples, saves variable values if any are
      * defined.
      *
      * @param result
      *            The SampleResult to be associated with this event
      * @param threadGroup
      *            The name of the thread, the {@link SampleResult} was recorded
      * @param jmvars
      *            the {@link JMeterVariables} of the thread, the
      *            {@link SampleResult} was recorded
      */
     public SampleEvent(SampleResult result, String threadGroup, JMeterVariables jmvars) {
         this(result, threadGroup, jmvars, false);
     }
 
     /**
      * Only intended for use when loading results from a file.
      *
      * @param result
      *            The SampleResult to be associated with this event
      * @param threadGroup
      *            The name of the thread, the {@link SampleResult} was recorded
      * @param hostname
      *            The name of the host, for which the {@link SampleResult} was
      *            recorded
      */
     public SampleEvent(SampleResult result, String threadGroup, String hostname) {
        this(result, threadGroup, hostname, false);
     }
     
     private SampleEvent(SampleResult result, String threadGroup, String hostname, boolean isTransactionSampleEvent) {
         this.result = result;
         this.threadGroup = threadGroup;
         this.hostname = hostname;
         this.values = new String[variableNames.length];
         this.isTransactionSampleEvent = isTransactionSampleEvent;
     }
 
     /**
      * @param result
      *            The SampleResult to be associated with this event
      * @param threadGroup
      *            The name of the thread, the {@link SampleResult} was recorded
      * @param jmvars
      *            the {@link JMeterVariables} of the thread, the
      *            {@link SampleResult} was recorded
      * @param isTransactionSampleEvent
      *            Flag whether this event is an transaction sample event
      */
     public SampleEvent(SampleResult result, String threadGroup, JMeterVariables jmvars, boolean isTransactionSampleEvent) {
         this(result, threadGroup, HOSTNAME, isTransactionSampleEvent);
         saveVars(jmvars);
     }
 
     private void saveVars(JMeterVariables vars){
         for(int i = 0; i < variableNames.length; i++){
             values[i] = vars.get(variableNames[i]);
         }
     }
 
     /**
      * Get the number of defined variables
      * 
      * @return the number of variables defined
      */
     public static int getVarCount(){
         return variableNames.length;
     }
 
     /**
      * Get the nth variable name (zero-based)
      * 
      * @param i
      *            specifies which variable name should be returned (zero-based)
      * @return the variable name of the nth variable
      */
     public static String getVarName(int i){
         return variableNames[i];
     }
 
     /**
      * Get the nth variable value (zero-based)
      * 
      * @param i
      *            specifies which variable value should be returned (zero-based)
      * @return the value of the nth variable
      * @throws JMeterError
      *             when an invalid index <code>i</code> was given
      */
     public String getVarValue(int i){
         try {
             return values[i];
         } catch (ArrayIndexOutOfBoundsException e) {
             throw new JMeterError("Check the sample_variable settings!", e);
         }
     }
 
     /**
      * Get the {@link SampleResult} associated with this event
      * 
      * @return the associated {@link SampleResult}
      */
     public SampleResult getResult() {
         return result;
     }
 
     /**
      * Get the name of the thread group for which this event was recorded
      * 
      * @return the name of the thread group
      */
     public String getThreadGroup() {
         return threadGroup;
     }
 
     /**
      * Get the name of the host for which this event was recorded
      * 
      * @return the name of the host
      */
     public String getHostname() {
         return hostname;
     }
 
     /**
      * @return the isTransactionSampleEvent
      */
     public boolean isTransactionSampleEvent() {
         return isTransactionSampleEvent;
     }
 
 }
diff --git a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
index e055b672a..ae67ef3e8 100644
--- a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
+++ b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
@@ -1,972 +1,972 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 /*
  * Created on Sep 7, 2004
  */
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 import java.util.Objects;
 import java.util.Properties;
 
 import org.apache.commons.lang3.CharUtils;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /*
  * N.B. to add a new field, remember the following
  * - static _xyz
  * - instance xyz=_xyz
  * - clone s.xyz = xyz (perhaps)
  * - setXyz(boolean)
  * - saveXyz()
  * - add Xyz to SAVE_CONFIG_NAMES list
  * - update SampleSaveConfigurationConverter to add new fields to marshall() and shouldSerialiseMember()
  * - update ctor SampleSaveConfiguration(boolean value) to set the value if it is a boolean property
  * - update SampleResultConverter and/or HTTPSampleConverter
  * - update CSVSaveService: CSV_XXXX, makeResultFromDelimitedString, printableFieldNamesToString, static{}
  * - update messages.properties to add save_xyz entry
  * - update jmeter.properties to add new property
  * - update listeners.xml to add new property, CSV and XML names etc.
  * - take screenshot sample_result_config.png
  * - update listeners.xml and component_reference.xml with new dimensions (might not change)
  */
 
 /**
  * Holds details of which sample attributes to save.
  *
  * The pop-up dialogue for this is created by the class SavePropertyDialog, which assumes:
  * <p>
  * For each field <em>XXX</em>
  * <ul>
  *  <li>methods have the signature "boolean save<em>XXX</em>()"</li>
  *  <li>a corresponding "void set<em>XXX</em>(boolean)" method</li>
  *  <li>messages.properties contains the key save_<em>XXX</em></li>
  * </ul>
  */
 public class SampleSaveConfiguration implements Cloneable, Serializable {
-    private static final long serialVersionUID = 7L;
+    private static final long serialVersionUID = 8L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SampleSaveConfiguration.class);
 
     // ---------------------------------------------------------------------
     // PROPERTY FILE CONSTANTS
     // ---------------------------------------------------------------------
 
     /** Indicates that the results file should be in XML format. * */
     private static final String XML = "xml"; // $NON_NLS-1$
 
     /** Indicates that the results file should be in CSV format. * */
     private static final String CSV = "csv"; // $NON_NLS-1$
 
     /** Indicates that the results should be stored in a database. * */
     //NOTUSED private static final String DATABASE = "db"; // $NON_NLS-1$
 
     /** A properties file indicator for true. * */
     private static final String TRUE = "true"; // $NON_NLS-1$
 
     /** A properties file indicator for false. * */
     private static final String FALSE = "false"; // $NON_NLS-1$
 
     /** A properties file indicator for milliseconds. * */
     public static final String MILLISECONDS = "ms"; // $NON_NLS-1$
 
     /** A properties file indicator for none. * */
     public static final String NONE = "none"; // $NON_NLS-1$
 
     /** A properties file indicator for the first of a series. * */
     private static final String FIRST = "first"; // $NON_NLS-1$
 
     /** A properties file indicator for all of a series. * */
     private static final String ALL = "all"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which assertion results should be
      * saved.
      **************************************************************************/
     public static final String ASSERTION_RESULTS_FAILURE_MESSAGE_PROP =
         "jmeter.save.saveservice.assertion_results_failure_message";  // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which assertion results should be
      * saved.
      **************************************************************************/
     private static final String ASSERTION_RESULTS_PROP = "jmeter.save.saveservice.assertion_results"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which delimiter should be used when
      * saving in a delimited values format.
      **************************************************************************/
     public static final String DEFAULT_DELIMITER_PROP = "jmeter.save.saveservice.default_delimiter"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which format should be used when
      * saving the results, e.g., xml or csv.
      **************************************************************************/
     private static final String OUTPUT_FORMAT_PROP = "jmeter.save.saveservice.output_format"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether field names should be printed
      * to a delimited file.
      **************************************************************************/
     private static final String PRINT_FIELD_NAMES_PROP = "jmeter.save.saveservice.print_field_names"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the data type should be
      * saved.
      **************************************************************************/
     private static final String SAVE_DATA_TYPE_PROP = "jmeter.save.saveservice.data_type"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the label should be saved.
      **************************************************************************/
     private static final String SAVE_LABEL_PROP = "jmeter.save.saveservice.label"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the response code should be
      * saved.
      **************************************************************************/
     private static final String SAVE_RESPONSE_CODE_PROP = "jmeter.save.saveservice.response_code"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the response data should be
      * saved.
      **************************************************************************/
     private static final String SAVE_RESPONSE_DATA_PROP = "jmeter.save.saveservice.response_data"; // $NON_NLS-1$
 
     private static final String SAVE_RESPONSE_DATA_ON_ERROR_PROP = "jmeter.save.saveservice.response_data.on_error"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the response message should
      * be saved.
      **************************************************************************/
     private static final String SAVE_RESPONSE_MESSAGE_PROP = "jmeter.save.saveservice.response_message"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the success indicator should
      * be saved.
      **************************************************************************/
     private static final String SAVE_SUCCESSFUL_PROP = "jmeter.save.saveservice.successful"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the thread name should be
      * saved.
      **************************************************************************/
     private static final String SAVE_THREAD_NAME_PROP = "jmeter.save.saveservice.thread_name"; // $NON_NLS-1$
 
     // Save bytes read
     private static final String SAVE_BYTES_PROP = "jmeter.save.saveservice.bytes"; // $NON_NLS-1$
     
     // Save bytes written
     private static final String SAVE_SENT_BYTES_PROP = "jmeter.save.saveservice.sent_bytes"; // $NON_NLS-1$
 
     // Save URL
     private static final String SAVE_URL_PROP = "jmeter.save.saveservice.url"; // $NON_NLS-1$
 
     // Save fileName for ResultSaver
     private static final String SAVE_FILENAME_PROP = "jmeter.save.saveservice.filename"; // $NON_NLS-1$
 
     // Save hostname for ResultSaver
     private static final String SAVE_HOSTNAME_PROP = "jmeter.save.saveservice.hostname"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the time should be saved.
      **************************************************************************/
     private static final String SAVE_TIME_PROP = "jmeter.save.saveservice.time"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property giving the format of the time stamp
      **************************************************************************/
     private static final String TIME_STAMP_FORMAT_PROP = "jmeter.save.saveservice.timestamp_format"; // $NON_NLS-1$
 
     private static final String SUBRESULTS_PROP      = "jmeter.save.saveservice.subresults"; // $NON_NLS-1$
     private static final String ASSERTIONS_PROP      = "jmeter.save.saveservice.assertions"; // $NON_NLS-1$
     private static final String LATENCY_PROP         = "jmeter.save.saveservice.latency"; // $NON_NLS-1$
     private static final String CONNECT_TIME_PROP    = "jmeter.save.saveservice.connect_time"; // $NON_NLS-1$
     private static final String SAMPLERDATA_PROP     = "jmeter.save.saveservice.samplerData"; // $NON_NLS-1$
     private static final String RESPONSEHEADERS_PROP = "jmeter.save.saveservice.responseHeaders"; // $NON_NLS-1$
     private static final String REQUESTHEADERS_PROP  = "jmeter.save.saveservice.requestHeaders"; // $NON_NLS-1$
     private static final String ENCODING_PROP        = "jmeter.save.saveservice.encoding"; // $NON_NLS-1$
 
 
     // optional processing instruction for line 2; e.g.
     // <?xml-stylesheet type="text/xsl" href="../extras/jmeter-results-detail-report_21.xsl"?>
     private static final String XML_PI               = "jmeter.save.saveservice.xml_pi"; // $NON_NLS-1$
 
     private static final String SAVE_THREAD_COUNTS   = "jmeter.save.saveservice.thread_counts"; // $NON_NLS-1$
 
     private static final String SAVE_SAMPLE_COUNT    = "jmeter.save.saveservice.sample_count"; // $NON_NLS-1$
 
     private static final String SAVE_IDLE_TIME       = "jmeter.save.saveservice.idle_time"; // $NON_NLS-1$
     
     // Defaults from properties:
     private static final boolean TIME;
     private static final boolean TIMESTAMP;
     private static final boolean SUCCESS;
     private static final boolean LABEL;
     private static final boolean CODE;
     private static final boolean MESSAGE;
     private static final boolean THREAD_NAME;
     private static final boolean IS_XML;
     private static final boolean RESPONSE_DATA;
     private static final boolean DATATYPE;
     private static final boolean ENCODING;
     private static final boolean ASSERTIONS;
     private static final boolean LATENCY;
     private static final boolean CONNECT_TIME;
     private static final boolean SUB_RESULTS;
     private static final boolean SAMPLER_DATA;
     private static final boolean FIELD_NAMES;
     private static final boolean RESPONSE_HEADERS;
     private static final boolean REQUEST_HEADERS;
 
     private static final boolean RESPONSE_DATA_ON_ERROR;
 
     private static final boolean SAVE_ASSERTION_RESULTS_FAILURE_MESSAGE;
 
     private static final String TIMESTAMP_FORMAT;
 
     private static final int ASSERTIONS_RESULT_TO_SAVE;
 
     // TODO turn into method?
     public static final int SAVE_NO_ASSERTIONS = 0;
 
     public static final int SAVE_FIRST_ASSERTION = SAVE_NO_ASSERTIONS + 1;
 
     public static final int SAVE_ALL_ASSERTIONS = SAVE_FIRST_ASSERTION + 1;
 
     private static final boolean PRINT_MILLISECONDS;
 
     private static final boolean BYTES;
     
     private static final boolean SENT_BYTES;
 
     private static final boolean URL;
 
     private static final boolean FILE_NAME;
 
     private static final boolean HOST_NAME;
 
     private static final boolean THREAD_COUNTS;
 
     private static final boolean SAMPLE_COUNT;
 
     private static final DateFormat DATE_FORMATTER;
 
     /**
      * The string used to separate fields when stored to disk, for example, the
      * comma for CSV files.
      */
     private static final String DELIMITER;
 
     private static final boolean IDLE_TIME;
 
     public static final String DEFAULT_DELIMITER = ","; // $NON_NLS-1$
 
     /**
      * Read in the properties having to do with saving from a properties file.
      */
     static {
         Properties props = JMeterUtils.getJMeterProperties();
 
         SUB_RESULTS      = TRUE.equalsIgnoreCase(props.getProperty(SUBRESULTS_PROP, TRUE));
         ASSERTIONS      = TRUE.equalsIgnoreCase(props.getProperty(ASSERTIONS_PROP, TRUE));
         LATENCY         = TRUE.equalsIgnoreCase(props.getProperty(LATENCY_PROP, TRUE));
         CONNECT_TIME     = TRUE.equalsIgnoreCase(props.getProperty(CONNECT_TIME_PROP, TRUE));
         SAMPLER_DATA     = TRUE.equalsIgnoreCase(props.getProperty(SAMPLERDATA_PROP, FALSE));
         RESPONSE_HEADERS = TRUE.equalsIgnoreCase(props.getProperty(RESPONSEHEADERS_PROP, FALSE));
         REQUEST_HEADERS  = TRUE.equalsIgnoreCase(props.getProperty(REQUESTHEADERS_PROP, FALSE));
         ENCODING        = TRUE.equalsIgnoreCase(props.getProperty(ENCODING_PROP, FALSE));
 
         String dlm = JMeterUtils.getDelimiter(props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER));
         char ch = dlm.charAt(0);
 
         if (CharUtils.isAsciiAlphanumeric(ch) || ch == CSVSaveService.QUOTING_CHAR){
             throw new JMeterError("Delimiter '"+ch+"' must not be alphanumeric or "+CSVSaveService.QUOTING_CHAR+".");
         }
 
         if (ch != '\t' && !CharUtils.isAsciiPrintable(ch)){
             throw new JMeterError("Delimiter (code "+(int)ch+") must be printable.");
         }
 
         DELIMITER = dlm;
 
         FIELD_NAMES = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, TRUE));
 
         DATATYPE = TRUE.equalsIgnoreCase(props.getProperty(SAVE_DATA_TYPE_PROP, TRUE));
 
         LABEL = TRUE.equalsIgnoreCase(props.getProperty(SAVE_LABEL_PROP, TRUE));
 
         CODE = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_CODE_PROP, TRUE));
 
         RESPONSE_DATA = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_PROP, FALSE));
 
         RESPONSE_DATA_ON_ERROR = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_ON_ERROR_PROP, FALSE));
 
         MESSAGE = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_MESSAGE_PROP, TRUE));
 
         SUCCESS = TRUE.equalsIgnoreCase(props.getProperty(SAVE_SUCCESSFUL_PROP, TRUE));
 
         THREAD_NAME = TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_NAME_PROP, TRUE));
 
         BYTES = TRUE.equalsIgnoreCase(props.getProperty(SAVE_BYTES_PROP, TRUE));
         
         SENT_BYTES = TRUE.equalsIgnoreCase(props.getProperty(SAVE_SENT_BYTES_PROP, TRUE));
 
         URL = TRUE.equalsIgnoreCase(props.getProperty(SAVE_URL_PROP, FALSE));
 
         FILE_NAME = TRUE.equalsIgnoreCase(props.getProperty(SAVE_FILENAME_PROP, FALSE));
 
         HOST_NAME = TRUE.equalsIgnoreCase(props.getProperty(SAVE_HOSTNAME_PROP, FALSE));
 
         TIME = TRUE.equalsIgnoreCase(props.getProperty(SAVE_TIME_PROP, TRUE));
 
         TIMESTAMP_FORMAT = props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);
 
         PRINT_MILLISECONDS = MILLISECONDS.equalsIgnoreCase(TIMESTAMP_FORMAT);
 
         // Prepare for a pretty date
         // FIXME Can TIMESTAMP_FORMAT be null ? it does not appear to me .
         if (!PRINT_MILLISECONDS && !NONE.equalsIgnoreCase(TIMESTAMP_FORMAT) && (TIMESTAMP_FORMAT != null)) {
             DATE_FORMATTER = new SimpleDateFormat(TIMESTAMP_FORMAT);
         } else {
             DATE_FORMATTER = null;
         }
 
         TIMESTAMP = !NONE.equalsIgnoreCase(TIMESTAMP_FORMAT);// reversed compare allows for null
 
         SAVE_ASSERTION_RESULTS_FAILURE_MESSAGE = TRUE.equalsIgnoreCase(props.getProperty(
                 ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, TRUE));
 
         String whichAssertionResults = props.getProperty(ASSERTION_RESULTS_PROP, NONE);
         if (NONE.equals(whichAssertionResults)) {
             ASSERTIONS_RESULT_TO_SAVE = SAVE_NO_ASSERTIONS;
         } else if (FIRST.equals(whichAssertionResults)) {
             ASSERTIONS_RESULT_TO_SAVE = SAVE_FIRST_ASSERTION;
         } else if (ALL.equals(whichAssertionResults)) {
             ASSERTIONS_RESULT_TO_SAVE = SAVE_ALL_ASSERTIONS;
         } else {
             ASSERTIONS_RESULT_TO_SAVE = 0;
         }
 
         String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, CSV);
 
         if (XML.equals(howToSave)) {
             IS_XML = true;
         } else {
             if (!CSV.equals(howToSave)) {
-                log.warn(OUTPUT_FORMAT_PROP + " has unexepected value: '" + howToSave + "' - assuming 'csv' format");
+                log.warn("{} has unexepected value: '{}' - assuming 'csv' format", OUTPUT_FORMAT_PROP, howToSave);
             }
             IS_XML = false;
         }
 
         THREAD_COUNTS=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, TRUE));
 
         SAMPLE_COUNT=TRUE.equalsIgnoreCase(props.getProperty(SAVE_SAMPLE_COUNT, FALSE));
 
         IDLE_TIME=TRUE.equalsIgnoreCase(props.getProperty(SAVE_IDLE_TIME, TRUE));
     }
 
     private static final SampleSaveConfiguration STATIC_SAVE_CONFIGURATION = new SampleSaveConfiguration();
 
     // N.B. Remember to update the equals and hashCode methods when adding new variables.
 
     // Initialise values from properties
     private boolean time = TIME;
     private boolean latency = LATENCY;
     private boolean connectTime=CONNECT_TIME;
     private boolean timestamp = TIMESTAMP;
     private boolean success = SUCCESS;
     private boolean label = LABEL;
     private boolean code = CODE;
     private boolean message = MESSAGE;
     private boolean threadName = THREAD_NAME;
     private boolean dataType = DATATYPE;
     private boolean encoding = ENCODING;
     private boolean assertions = ASSERTIONS;
     private boolean subresults = SUB_RESULTS;
     private boolean responseData = RESPONSE_DATA;
     private boolean samplerData = SAMPLER_DATA;
     private boolean xml = IS_XML;
     private boolean fieldNames = FIELD_NAMES;
     private boolean responseHeaders = RESPONSE_HEADERS;
     private boolean requestHeaders = REQUEST_HEADERS;
     private boolean responseDataOnError = RESPONSE_DATA_ON_ERROR;
 
     private boolean saveAssertionResultsFailureMessage = SAVE_ASSERTION_RESULTS_FAILURE_MESSAGE;
 
     private boolean url = URL;
     private boolean bytes = BYTES;
     private boolean sentBytes = SENT_BYTES;
     private boolean fileName = FILE_NAME;
 
     private boolean hostname = HOST_NAME;
 
     private boolean threadCounts = THREAD_COUNTS;
 
     private boolean sampleCount = SAMPLE_COUNT;
 
     private boolean idleTime = IDLE_TIME;
 
     // Does not appear to be used (yet)
     private int assertionsResultsToSave = ASSERTIONS_RESULT_TO_SAVE;
 
 
     // Don't save this, as it is derived from the time format
     private boolean printMilliseconds = PRINT_MILLISECONDS;
 
     /** A formatter for the time stamp. */
     private transient DateFormat formatter = DATE_FORMATTER;
     /* Make transient as we don't want to save the SimpleDataFormat class
      * Also, there's currently no way to change the value via the GUI, so changing it
      * later means editting the JMX, or recreating the Listener.
      */
 
     // Don't save this, as not settable via GUI
     private String delimiter = DELIMITER;
 
     // Don't save this - only needed for processing CSV headers currently
     private transient int varCount = 0;
 
     
     public SampleSaveConfiguration() {
     }
 
     /**
      * Alternate constructor for use by CsvSaveService
      *
      * @param value initial setting for boolean fields used in Config dialogue
      */
     public SampleSaveConfiguration(boolean value) {
         assertions = value;
         bytes = value;
         code = value;
         connectTime = value;
         dataType = value;
         encoding = value;
         fieldNames = value;
         fileName = value;
         hostname = value;
         idleTime = value;
         label = value;
         latency = value;
         message = value;
         printMilliseconds = PRINT_MILLISECONDS;//is derived from properties only
         requestHeaders = value;
         responseData = value;
         responseDataOnError = value;
         responseHeaders = value;
         sampleCount = value;
         samplerData = value;
         saveAssertionResultsFailureMessage = value;
         sentBytes = value;
         subresults = value;
         success = value;
         threadCounts = value;
         threadName = value;
         time = value;
         timestamp = value;
         url = value;
         xml = value;
     }
     
     public int getVarCount() { // Only for use by CSVSaveService
         return varCount;
     }
 
     public void setVarCount(int varCount) { // Only for use by CSVSaveService
         this.varCount = varCount;
     }
 
     // Give access to initial configuration
     public static SampleSaveConfiguration staticConfig() {
         return STATIC_SAVE_CONFIGURATION;
     }
 
     // for test code only
     static final String CONFIG_GETTER_PREFIX = "save";  // $NON-NLS-1$
 
     /**
      * Convert a config name to the method name of the getter.
      * The getter method returns a boolean.
      * @param configName the config name
      * @return the getter method name
      */
     public static final String getterName(String configName) {
         return CONFIG_GETTER_PREFIX + configName;
     }
 
     // for test code only
     static final String CONFIG_SETTER_PREFIX = "set";  // $NON-NLS-1$
 
     /**
      * Convert a config name to the method name of the setter
      * The setter method requires a boolean parameter.
      * @param configName the config name
      * @return the setter method name
      */
     public static final String setterName(String configName) {
         return CONFIG_SETTER_PREFIX + configName;
     }
 
     /**
      * List of saveXXX/setXXX(boolean) methods which is used to build the Sample Result Save Configuration dialog.
      * New method names should be added at the end so that existing layouts are not affected.
      */
     // The current order is derived from http://jmeter.apache.org/usermanual/listeners.html#csvlogformat
     // TODO this may not be the ideal order; fix further and update the screenshot(s)
     public static final List<String> SAVE_CONFIG_NAMES = Collections.unmodifiableList(Arrays.asList(new String[]{
         "AsXml",
         "FieldNames", // CSV
         "Timestamp",
         "Time", // elapsed
         "Label",
         "Code", // Response Code
         "Message", // Response Message
         "ThreadName",
         "DataType",
         "Success",
         "AssertionResultsFailureMessage",
         "Bytes",
         "SentBytes",
         "ThreadCounts", // grpThreads and allThreads
         "Url",
         "FileName",
         "Latency",
         "ConnectTime",
         "Encoding",
         "SampleCount", // Sample and Error Count
         "Hostname",
         "IdleTime",
         "RequestHeaders", // XML
         "SamplerData", // XML
         "ResponseHeaders", // XML
         "ResponseData", // XML
         "Subresults", // XML
         "Assertions", // XML
     }));
 
     private Object readResolve(){
        formatter = DATE_FORMATTER;
        return this;
     }
 
     @Override
     public Object clone() {
         try {
             SampleSaveConfiguration clone = (SampleSaveConfiguration)super.clone();
             if(this.formatter != null) {
                 clone.formatter = (SimpleDateFormat)this.formatter.clone();
             }
             return clone;
         }
         catch(CloneNotSupportedException e) {
             throw new RuntimeException("Should not happen",e);
         }
     }
 
     @Override
     public boolean equals(Object obj) {
         if(this == obj) {
             return true;
         }
         if((obj == null) || (obj.getClass() != this.getClass())) {
             return false;
         }
         // We know we are comparing to another SampleSaveConfiguration
         SampleSaveConfiguration s = (SampleSaveConfiguration)obj;
         boolean primitiveValues = s.time == time &&
             s.latency == latency &&
             s.connectTime == connectTime &&
             s.timestamp == timestamp &&
             s.success == success &&
             s.label == label &&
             s.code == code &&
             s.message == message &&
             s.threadName == threadName &&
             s.dataType == dataType &&
             s.encoding == encoding &&
             s.assertions == assertions &&
             s.subresults == subresults &&
             s.responseData == responseData &&
             s.samplerData == samplerData &&
             s.xml == xml &&
             s.fieldNames == fieldNames &&
             s.responseHeaders == responseHeaders &&
             s.requestHeaders == requestHeaders &&
             s.assertionsResultsToSave == assertionsResultsToSave &&
             s.saveAssertionResultsFailureMessage == saveAssertionResultsFailureMessage &&
             s.printMilliseconds == printMilliseconds &&
             s.responseDataOnError == responseDataOnError &&
             s.url == url &&
             s.bytes == bytes &&
             s.sentBytes == sentBytes &&
             s.fileName == fileName &&
             s.hostname == hostname &&
             s.sampleCount == sampleCount &&
             s.idleTime == idleTime &&
             s.threadCounts == threadCounts;
 
         boolean stringValues = false;
         if(primitiveValues) {
             stringValues = Objects.equals(delimiter, s.delimiter);
         }
         boolean complexValues = false;
         if(primitiveValues && stringValues) {
             complexValues = Objects.equals(formatter, s.formatter);
         }
 
         return primitiveValues && stringValues && complexValues;
     }
 
     @Override
     public int hashCode() {
         int hash = 7;
         hash = 31 * hash + (time ? 1 : 0);
         hash = 31 * hash + (latency ? 1 : 0);
         hash = 31 * hash + (connectTime ? 1 : 0);
         hash = 31 * hash + (timestamp ? 1 : 0);
         hash = 31 * hash + (success ? 1 : 0);
         hash = 31 * hash + (label ? 1 : 0);
         hash = 31 * hash + (code ? 1 : 0);
         hash = 31 * hash + (message ? 1 : 0);
         hash = 31 * hash + (threadName ? 1 : 0);
         hash = 31 * hash + (dataType ? 1 : 0);
         hash = 31 * hash + (encoding ? 1 : 0);
         hash = 31 * hash + (assertions ? 1 : 0);
         hash = 31 * hash + (subresults ? 1 : 0);
         hash = 31 * hash + (responseData ? 1 : 0);
         hash = 31 * hash + (samplerData ? 1 : 0);
         hash = 31 * hash + (xml ? 1 : 0);
         hash = 31 * hash + (fieldNames ? 1 : 0);
         hash = 31 * hash + (responseHeaders ? 1 : 0);
         hash = 31 * hash + (requestHeaders ? 1 : 0);
         hash = 31 * hash + assertionsResultsToSave;
         hash = 31 * hash + (saveAssertionResultsFailureMessage ? 1 : 0);
         hash = 31 * hash + (printMilliseconds ? 1 : 0);
         hash = 31 * hash + (responseDataOnError ? 1 : 0);
         hash = 31 * hash + (url ? 1 : 0);
         hash = 31 * hash + (bytes ? 1 : 0);
         hash = 31 * hash + (sentBytes ? 1 : 0);
         hash = 31 * hash + (fileName ? 1 : 0);
         hash = 31 * hash + (hostname ? 1 : 0);
         hash = 31 * hash + (threadCounts ? 1 : 0);
         hash = 31 * hash + (delimiter != null  ? delimiter.hashCode() : 0);
         hash = 31 * hash + (formatter != null  ? formatter.hashCode() : 0);
         hash = 31 * hash + (sampleCount ? 1 : 0);
         hash = 31 * hash + (idleTime ? 1 : 0);
 
         return hash;
     }
 
     ///////////////////// Start of standard save/set access methods /////////////////////
 
     public boolean saveResponseHeaders() {
         return responseHeaders;
     }
 
     public void setResponseHeaders(boolean r) {
         responseHeaders = r;
     }
 
     public boolean saveRequestHeaders() {
         return requestHeaders;
     }
 
     public void setRequestHeaders(boolean r) {
         requestHeaders = r;
     }
 
     public boolean saveAssertions() {
         return assertions;
     }
 
     public void setAssertions(boolean assertions) {
         this.assertions = assertions;
     }
 
     public boolean saveCode() {
         return code;
     }
 
     public void setCode(boolean code) {
         this.code = code;
     }
 
     public boolean saveDataType() {
         return dataType;
     }
 
     public void setDataType(boolean dataType) {
         this.dataType = dataType;
     }
 
     public boolean saveEncoding() {
         return encoding;
     }
 
     public void setEncoding(boolean encoding) {
         this.encoding = encoding;
     }
 
     public boolean saveLabel() {
         return label;
     }
 
     public void setLabel(boolean label) {
         this.label = label;
     }
 
     public boolean saveLatency() {
         return latency;
     }
 
     public void setLatency(boolean latency) {
         this.latency = latency;
     }
 
     public boolean saveConnectTime() {
         return connectTime;
     }
 
     public void setConnectTime(boolean connectTime) {
         this.connectTime = connectTime;
     }
 
     public boolean saveMessage() {
         return message;
     }
 
     public void setMessage(boolean message) {
         this.message = message;
     }
 
     public boolean saveResponseData(SampleResult res) {
         return responseData || TestPlan.getFunctionalMode() || (responseDataOnError && !res.isSuccessful());
     }
 
     public boolean saveResponseData()
     {
         return responseData;
     }
 
     public void setResponseData(boolean responseData) {
         this.responseData = responseData;
     }
 
     public boolean saveSamplerData(SampleResult res) {
         return samplerData || TestPlan.getFunctionalMode() // as per 2.0 branch
                 || (responseDataOnError && !res.isSuccessful());
     }
 
     public boolean saveSamplerData()
     {
         return samplerData;
     }
 
     public void setSamplerData(boolean samplerData) {
         this.samplerData = samplerData;
     }
 
     public boolean saveSubresults() {
         return subresults;
     }
 
     public void setSubresults(boolean subresults) {
         this.subresults = subresults;
     }
 
     public boolean saveSuccess() {
         return success;
     }
 
     public void setSuccess(boolean success) {
         this.success = success;
     }
 
     public boolean saveThreadName() {
         return threadName;
     }
 
     public void setThreadName(boolean threadName) {
         this.threadName = threadName;
     }
 
     public boolean saveTime() {
         return time;
     }
 
     public void setTime(boolean time) {
         this.time = time;
     }
 
     public boolean saveTimestamp() {
         return timestamp;
     }
 
     public void setTimestamp(boolean timestamp) {
         this.timestamp = timestamp;
     }
 
     public boolean saveAsXml() {
         return xml;
     }
 
     public void setAsXml(boolean xml) {
         this.xml = xml;
     }
 
     public boolean saveFieldNames() {
         return fieldNames;
     }
 
     public void setFieldNames(boolean printFieldNames) {
         this.fieldNames = printFieldNames;
     }
 
     public boolean saveUrl() {
         return url;
     }
 
     public void setUrl(boolean save) {
         this.url = save;
     }
 
     public boolean saveBytes() {
         return bytes;
     }
 
     public void setBytes(boolean save) {
         this.bytes = save;
     }
     
     public boolean saveSentBytes() {
         return sentBytes;
     }
 
     public void setSentBytes(boolean save) {
         this.sentBytes = save;
     }
 
     public boolean saveFileName() {
         return fileName;
     }
 
     public void setFileName(boolean save) {
         this.fileName = save;
     }
 
     public boolean saveAssertionResultsFailureMessage() {
         return saveAssertionResultsFailureMessage;
     }
 
     public void setAssertionResultsFailureMessage(boolean b) {
         saveAssertionResultsFailureMessage = b;
     }
 
     public boolean saveThreadCounts() {
         return threadCounts;
     }
 
     public void setThreadCounts(boolean save) {
         this.threadCounts = save;
     }
 
     public boolean saveSampleCount() {
         return sampleCount;
     }
 
     public void setSampleCount(boolean save) {
         this.sampleCount = save;
     }
 
     ///////////////// End of standard field accessors /////////////////////
 
     /**
      * Intended for use by CsvSaveService (and test cases)
      * @param fmt
      *            format of the date to be saved. If <code>null</code>
      *            milliseconds since epoch will be printed
      */
     public void setFormatter(DateFormat fmt){
         printMilliseconds = fmt == null; // maintain relationship
         formatter = fmt;
     }
 
     public boolean printMilliseconds() {
         return printMilliseconds;
     }
 
     public DateFormat formatter() {
         return formatter;
     }
 
     public int assertionsResultsToSave() {
         return assertionsResultsToSave;
     }
 
     public String getDelimiter() {
         return delimiter;
     }
 
     public String getXmlPi() {
         return JMeterUtils.getJMeterProperties().getProperty(XML_PI, ""); // Defaults to empty;
     }
 
     // Used by old Save service
     public void setDelimiter(String delim) {
         delimiter=delim;
     }
 
     // Used by SampleSaveConfigurationConverter.unmarshall()
     public void setDefaultDelimiter() {
         delimiter=DELIMITER;
     }
 
     // Used by SampleSaveConfigurationConverter.unmarshall()
     public void setDefaultTimeStampFormat() {
         printMilliseconds=PRINT_MILLISECONDS;
         formatter=DATE_FORMATTER;
     }
 
     public boolean saveHostname(){
         return hostname;
     }
 
     public void setHostname(boolean save){
         hostname = save;
     }
 
     public boolean saveIdleTime() {
         return idleTime;
     }
 
     public void setIdleTime(boolean save) {
         idleTime = save;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/SampleSenderFactory.java b/src/core/org/apache/jmeter/samplers/SampleSenderFactory.java
index b5b4e7d8d..8c0c347b8 100644
--- a/src/core/org/apache/jmeter/samplers/SampleSenderFactory.java
+++ b/src/core/org/apache/jmeter/samplers/SampleSenderFactory.java
@@ -1,110 +1,114 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
 import java.lang.reflect.Constructor;
 
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class SampleSenderFactory {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SampleSenderFactory.class);
 
     private static final String MODE_STANDARD = "Standard"; // $NON-NLS-1$
 
     private static final String MODE_HOLD = "Hold"; // $NON-NLS-1$
 
     private static final String MODE_BATCH = "Batch"; // $NON-NLS-1$
 
     private static final String MODE_STATISTICAL = "Statistical"; // $NON-NLS-1$
 
     private static final String MODE_STRIPPED = "Stripped"; // $NON-NLS-1$
 
     private static final String MODE_STRIPPED_BATCH = "StrippedBatch"; // $NON-NLS-1$
 
     private static final String MODE_ASYNCH = "Asynch"; // $NON-NLS-1$
 
     private static final String MODE_STRIPPED_ASYNCH = "StrippedAsynch"; // $NON-NLS-1$
 
     private static final String MODE_DISKSTORE = "DiskStore"; // $NON-NLS-1$
 
     private static final String MODE_STRIPPED_DISKSTORE = "StrippedDiskStore"; // $NON-NLS-1$
 
     /**
      * Checks for the Jmeter property mode and returns the required class.
      *
      * @param listener
      * @return the appropriate class. Standard Jmeter functionality,
      *         hold_samples until end of test or batch samples.
      */
     static SampleSender getInstance(RemoteSampleListener listener) {
         // Support original property name
         final boolean holdSamples = JMeterUtils.getPropDefault("hold_samples", false); // $NON-NLS-1$
 
         // Extended property name
         final String type = JMeterUtils.getPropDefault("mode", MODE_STRIPPED_BATCH); // $NON-NLS-1$
         
         if (holdSamples || type.equalsIgnoreCase(MODE_HOLD)) {
             if(holdSamples) {
-                log.warn("Property hold_samples is deprecated and will be removed in upcomping version, use mode="+MODE_HOLD +" instead");
+                log.warn(
+                        "Property hold_samples is deprecated and will be removed in upcomping version, use mode={} instead",
+                        MODE_HOLD);
             }
             HoldSampleSender h = new HoldSampleSender(listener);
             return h;
         } else if (type.equalsIgnoreCase(MODE_BATCH)) {
             BatchSampleSender b = new BatchSampleSender(listener);
             return b;
         }  else if(type.equalsIgnoreCase(MODE_STRIPPED_BATCH)) {
             return new DataStrippingSampleSender(new BatchSampleSender(listener));
         } else if (type.equalsIgnoreCase(MODE_STATISTICAL)) {
             StatisticalSampleSender s = new StatisticalSampleSender(listener);
             return s;
         } else if (type.equalsIgnoreCase(MODE_STANDARD)) {
             StandardSampleSender s = new StandardSampleSender(listener);
             return s;
         } else if(type.equalsIgnoreCase(MODE_STRIPPED)){
             return new DataStrippingSampleSender(listener);
         } else if(type.equalsIgnoreCase(MODE_ASYNCH)){
             return new AsynchSampleSender(listener);
         } else if(type.equalsIgnoreCase(MODE_STRIPPED_ASYNCH)) {
             return new DataStrippingSampleSender(new AsynchSampleSender(listener));
         } else if(type.equalsIgnoreCase(MODE_DISKSTORE)){
             return new DiskStoreSampleSender(listener);
         } else if(type.equalsIgnoreCase(MODE_STRIPPED_DISKSTORE)){
             return new DataStrippingSampleSender(new DiskStoreSampleSender(listener));
         } else {
             // should be a user provided class name
             SampleSender s = null;
             try {
                 Class<?> clazz = Class.forName(type);
                 Constructor<?> cons = clazz.getConstructor(new Class[] {RemoteSampleListener.class});
                 s = (SampleSender) cons.newInstance(new Object [] {listener});
             } catch (Exception e) {
                 // houston we have a problem !!
-                log.error("Unable to create a sample sender from class:'"+type+"', search for mode property in jmeter.properties for correct configuration options");
+                log.error(
+                        "Unable to create a sample sender from class:'{}', search for mode property in jmeter.properties for correct configuration options",
+                        type);
                 throw new IllegalArgumentException("Unable to create a sample sender from mode or class:'"
                         +type+"', search for mode property in jmeter.properties for correct configuration options, message:"+e.getMessage(), e);
             }
 
             return s;
         }
 
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/StandardSampleSender.java b/src/core/org/apache/jmeter/samplers/StandardSampleSender.java
index 3af8cd925..430a54fe0 100644
--- a/src/core/org/apache/jmeter/samplers/StandardSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/StandardSampleSender.java
@@ -1,87 +1,87 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
-import org.apache.log.Logger;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.jorphan.util.JMeterError;
-
-import java.rmi.RemoteException;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
+import java.rmi.RemoteException;
+
+import org.apache.jorphan.util.JMeterError;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Default behaviour for remote testing.
  */
 
 public class StandardSampleSender extends AbstractSampleSender implements Serializable {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(StandardSampleSender.class);
 
     private final RemoteSampleListener listener;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public StandardSampleSender(){
         this.listener = null;
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
 
     StandardSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         log.info("Using StandardSampleSender for this test run");        
     }
 
     @Override
     public void testEnded(String host) {
-        log.info("Test Ended on " + host);
+        log.info("Test Ended on {}", host);
         try {
             listener.testEnded(host);
         } catch (RemoteException ex) {
-            log.warn("testEnded(host)"+ex);
+            log.warn("testEnded(host)", ex);
         }
     }
 
     @Override
     public void sampleOccurred(SampleEvent e) {
         try {
             listener.sampleOccurred(e);
         } catch (RemoteException err) {
             if (err.getCause() instanceof java.net.ConnectException){
                 throw new JMeterError("Could not return sample",err);
             }
             log.error("sampleOccurred", err);
         }
     }
 
     /**
      * Processed by the RMI server code; acts as testStarted().
      *
      * @return this
      * @throws ObjectStreamException
      *             never
      */
     private Object readResolve() throws ObjectStreamException{
         log.info("Using StandardSampleSender for this test run");        
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/StatisticalSampleSender.java b/src/core/org/apache/jmeter/samplers/StatisticalSampleSender.java
index 5d729b47b..67ce94f22 100644
--- a/src/core/org/apache/jmeter/samplers/StatisticalSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/StatisticalSampleSender.java
@@ -1,226 +1,226 @@
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
  *
  */
 
 package org.apache.jmeter.samplers;
 
-import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
-
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.rmi.RemoteException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
+import org.apache.jmeter.util.JMeterUtils;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
 /**
  * Implements batch reporting for remote testing.
  *
  */
 public class StatisticalSampleSender extends AbstractSampleSender implements Serializable {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(StatisticalSampleSender.class);
 
     private static final int DEFAULT_NUM_SAMPLE_THRESHOLD = 100;
 
     private static final long DEFAULT_TIME_THRESHOLD = 60000L;
 
     // Static fields are set by the server when the class is constructed
 
     private static final int NUM_SAMPLES_THRESHOLD = JMeterUtils.getPropDefault(
             "num_sample_threshold", DEFAULT_NUM_SAMPLE_THRESHOLD);
 
     private static final long TIME_THRESHOLD_MS = JMeterUtils.getPropDefault("time_threshold",
             DEFAULT_TIME_THRESHOLD);
 
     // should the samples be aggregated on thread name or thread group (default) ?
     private static boolean KEY_ON_THREADNAME = JMeterUtils.getPropDefault("key_on_threadname", false);
 
     // Instance fields are constructed by the client when the instance is create in the test plan
     // and the field values are then transferred to the server copy by RMI serialisation/deserialisation
 
     private final int clientConfiguredNumSamplesThreshold = JMeterUtils.getPropDefault(
             "num_sample_threshold", DEFAULT_NUM_SAMPLE_THRESHOLD);
 
     private final long clientConfiguredTimeThresholdMs = JMeterUtils.getPropDefault("time_threshold",
             DEFAULT_TIME_THRESHOLD);
 
     // should the samples be aggregated on thread name or thread group (default) ?
     private final boolean clientConfiguredKeyOnThreadName = JMeterUtils.getPropDefault("key_on_threadname", false);
 
     private final RemoteSampleListener listener;
 
     private final List<SampleEvent> sampleStore = new ArrayList<>();
 
     //@GuardedBy("sampleStore") TODO perhaps use ConcurrentHashMap ?
     private final Map<String, StatisticalSampleResult> sampleTable = new HashMap<>();
 
     // Settings; readResolve sets these from the server/client values as appropriate
     // TODO would be nice to make these final; not 100% sure volatile is needed as not changed after creation
     private transient volatile int numSamplesThreshold;
 
     private transient volatile long timeThresholdMs;
 
     private transient volatile boolean keyOnThreadName;
 
 
     // variables maintained by server code
     // @GuardedBy("sampleStore")
     private transient int sampleCount; // maintain separate count of samples for speed
 
     private transient long batchSendTime = -1; // @GuardedBy("sampleStore")
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public StatisticalSampleSender(){
         this(null);
         log.warn("Constructor only intended for use in testing");
     }
 
     /**
      * Constructor, only called by client code.
      *
      * @param listener that the List of sample events will be sent to.
      */
     StatisticalSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         if (isClientConfigured()) {
-            log.info("Using StatisticalSampleSender (client settings) for this run."
-                    + " Thresholds: num=" + clientConfiguredNumSamplesThreshold
-                    + ", time=" + clientConfiguredTimeThresholdMs
-                    + ". Key uses ThreadName: " + clientConfiguredKeyOnThreadName);
+            log.info(
+                    "Using StatisticalSampleSender (client settings) for this run."
+                            + " Thresholds: num={}, time={}. Key uses ThreadName: {}",
+                    clientConfiguredNumSamplesThreshold, clientConfiguredTimeThresholdMs,
+                    clientConfiguredKeyOnThreadName);
         } else {
             log.info("Using StatisticalSampleSender (server settings) for this run.");
         }
     }
 
     /**
      * Checks if any sample events are still present in the sampleStore and
      * sends them to the listener. Informs the listener that the test ended.
      *
      * @param host the hostname that the test has ended on.
      */
     @Override
     public void testEnded(String host) {
-        log.info("Test Ended on " + host);
+        log.info("Test Ended on {}", host);
         try {
             if (sampleStore.size() != 0) {
                 sendBatch();
             }
             listener.testEnded(host);
         } catch (RemoteException err) {
             log.warn("testEnded(hostname)", err);
         }
     }
 
     /**
      * Stores sample events until either a time or sample threshold is
      * breached. Both thresholds are reset if one fires. If only one threshold
      * is set it becomes the only value checked against. When a threshold is
      * breached the list of sample events is sent to a listener where the event
      * are fired locally.
      *
      * @param e a Sample Event
      */
     @Override
     public void sampleOccurred(SampleEvent e) {
         synchronized (sampleStore) {
             // Locate the statistical sample collector
             String key = StatisticalSampleResult.getKey(e, keyOnThreadName);
             StatisticalSampleResult statResult = sampleTable.get(key);
             if (statResult == null) {
                 statResult = new StatisticalSampleResult(e.getResult());
                 // store the new statistical result collector
                 sampleTable.put(key, statResult);
                 // add a new wrapper sampleevent
                 sampleStore
                         .add(new SampleEvent(statResult, e.getThreadGroup()));
             }
             statResult.add(e.getResult());
             sampleCount++;
             boolean sendNow = false;
             if (numSamplesThreshold != -1) {
                 if (sampleCount >= numSamplesThreshold) {
                     sendNow = true;
                 }
             }
 
             long now = 0;
             if (timeThresholdMs != -1) {
                 now = System.currentTimeMillis();
                 // Checking for and creating initial timestamp to check against
                 if (batchSendTime == -1) {
                     this.batchSendTime = now + timeThresholdMs;
                 }
                 if (batchSendTime < now) {
                     sendNow = true;
                 }
             }
             if (sendNow) {
                 try {
-                    if (log.isDebugEnabled()) {
-                        log.debug("Firing sample");
-                    }
+                    log.debug("Firing sample");
                     sendBatch();
                     if (timeThresholdMs != -1) {
                         this.batchSendTime = now + timeThresholdMs;
                     }
                 } catch (RemoteException err) {
                     log.warn("sampleOccurred", err);
                 }
             }
         } // synchronized(sampleStore)
     }
 
     private void sendBatch() throws RemoteException {
         if (sampleStore.size() > 0) {
             listener.processBatch(sampleStore);
             sampleStore.clear();
             sampleTable.clear();
             sampleCount = 0;
         }
     }
 
     /**
      * Processed by the RMI server code; acts as testStarted().
      * @return this
      * @throws ObjectStreamException never
      */
     private Object readResolve() throws ObjectStreamException{
         if (isClientConfigured()) {
             numSamplesThreshold = clientConfiguredNumSamplesThreshold;
             timeThresholdMs = clientConfiguredTimeThresholdMs;
             keyOnThreadName = clientConfiguredKeyOnThreadName;
         } else {
             numSamplesThreshold = NUM_SAMPLES_THRESHOLD;
             timeThresholdMs = TIME_THRESHOLD_MS;
             keyOnThreadName = KEY_ON_THREADNAME;
         }
-        log.info("Using StatisticalSampleSender for this run."
-                + (isClientConfigured() ? " Client config: " : " Server config: ")
-                + " Thresholds: num=" + numSamplesThreshold
-                + ", time=" + timeThresholdMs
-                + ". Key uses ThreadName: " + keyOnThreadName);
+        if (log.isInfoEnabled()) {
+            log.info(
+                    "Using StatisticalSampleSender for this run. {} config: Thresholds: num={}, time={}. Key uses ThreadName: {}",
+                    (isClientConfigured() ? "Client" : "Server"), numSamplesThreshold, timeThresholdMs,
+                    keyOnThreadName);
+        }
         return this;
     }
 }
