diff --git a/src/components/org/apache/jmeter/visualizers/backend/SamplerMetric.java b/src/components/org/apache/jmeter/visualizers/backend/SamplerMetric.java
index dfb16a102..dc998a0b8 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/SamplerMetric.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/SamplerMetric.java
@@ -1,229 +1,255 @@
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
 
 package org.apache.jmeter.visualizers.backend;
 
 import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
+import org.apache.jmeter.control.TransactionController;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Sampler metric
  * @since 2.13
  */
 public class SamplerMetric {
     private static final int SLIDING_WINDOW_SIZE = JMeterUtils.getPropDefault("backend_metrics_window", 100); //$NON-NLS-1$
     
     // Response times for OK samples
     // Limit to sliding window of SLIDING_WINDOW_SIZE values 
     private DescriptiveStatistics okResponsesStats = new DescriptiveStatistics(SLIDING_WINDOW_SIZE);
     // Response times for KO samples
     // Limit to sliding window of SLIDING_WINDOW_SIZE values 
     private DescriptiveStatistics koResponsesStats = new DescriptiveStatistics(SLIDING_WINDOW_SIZE);
     // Response times for All samples
     // Limit to sliding window of SLIDING_WINDOW_SIZE values 
     private DescriptiveStatistics allResponsesStats = new DescriptiveStatistics(SLIDING_WINDOW_SIZE);
     private int successes;
     private int failures;
+    private int hits;
     /**
      * 
      */
     public SamplerMetric() {
     }
 
     /**
      * Add a {@link SampleResult} to be used in the statistics
      * @param result {@link SampleResult} to be used
      */
     public synchronized void add(SampleResult result) {
         if(result.isSuccessful()) {
             successes+=result.getSampleCount()-result.getErrorCount();
         } else {
             failures+=result.getErrorCount();
         }
         long time = result.getTime();
         allResponsesStats.addValue(time);
         if(result.isSuccessful()) {
             // Should we also compute KO , all response time ?
             // only take successful requests for time computing
             okResponsesStats.addValue(time);
         }else {
             koResponsesStats.addValue(time);
         }
+        addHits(result);
+    }
+
+    /**
+     * Compute hits from res
+     * @param res {@link SampleResult}
+     */
+    private void addHits(SampleResult res) {     
+        SampleResult[] subResults = res.getSubResults();
+        if (!TransactionController.isFromTransactionController(res)) {
+            hits += 1;                 
+        }
+        for (int i = 0; i < subResults.length; i++) {            
+            addHits(subResults[i]);
+        }
     }
     
     /**
      * Reset metric except for percentile related data
      */
     public synchronized void resetForTimeInterval() {
         // We don't clear responsesStats nor usersStats as it will slide as per my understanding of 
         // http://commons.apache.org/proper/commons-math/userguide/stat.html
         successes = 0;
         failures = 0;
+        hits = 0;
     }
 
     /**
      * Get the number of total requests for the current time slot
      * 
      * @return number of total requests
      */
     public int getTotal() {
         return successes+failures;
     }
     
     /**
      * Get the number of successful requests for the current time slot
      * 
      * @return number of successful requests
      */
     public int getSuccesses() {
         return successes;
     }
 
     /**
      * Get the number of failed requests for the current time slot
      * 
      * @return number of failed requests
      */
     public int getFailures() {
         return failures;
     }
 
     /**
      * Get the maximal elapsed time for requests within sliding window
      * 
      * @return the maximal elapsed time, or <code>0</code> if no requests have
      *         been added yet
      */
     public double getOkMaxTime() {
         return okResponsesStats.getMax();
     }
 
     /**
      * Get the minimal elapsed time for requests within sliding window
      * 
      * @return the minTime, or {@link Long#MAX_VALUE} if no requests have been
      *         added yet
      */
     public double getOkMinTime() {
         return okResponsesStats.getMin();
     }
     
     /**
      * Get the arithmetic mean of the stored values
      * 
      * @return The arithmetic mean of the stored values
      */
     public double getOkMean() {
         return okResponsesStats.getMean();
     }
     
     /**
      * Returns an estimate for the requested percentile of the stored values.
      * 
      * @param percentile
      *            the requested percentile (scaled from 0 - 100)
      * @return Returns an estimate for the requested percentile of the stored
      *         values.
      */
     public double getOkPercentile(double percentile) {
         return okResponsesStats.getPercentile(percentile);
     }
 
     /**
      * Get the maximal elapsed time for requests within sliding window
      * 
      * @return the maximal elapsed time, or <code>0</code> if no requests have
      *         been added yet
      */
     public double getKoMaxTime() {
         return koResponsesStats.getMax();
     }
 
     /**
      * Get the minimal elapsed time for requests within sliding window
      * 
      * @return the minTime, or {@link Long#MAX_VALUE} if no requests have been
      *         added yet
      */
     public double getKoMinTime() {
         return koResponsesStats.getMin();
     }
     
     /**
      * Get the arithmetic mean of the stored values
      * 
      * @return The arithmetic mean of the stored values
      */
     public double getKoMean() {
         return koResponsesStats.getMean();
     }
     
     /**
      * Returns an estimate for the requested percentile of the stored values.
      * 
      * @param percentile
      *            the requested percentile (scaled from 0 - 100)
      * @return Returns an estimate for the requested percentile of the stored
      *         values.
      */
     public double getKoPercentile(double percentile) {
         return koResponsesStats.getPercentile(percentile);
     }
     
     /**
      * Get the maximal elapsed time for requests within sliding window
      * 
      * @return the maximal elapsed time, or <code>0</code> if no requests have
      *         been added yet
      */
     public double getAllMaxTime() {
         return allResponsesStats.getMax();
     }
 
     /**
      * Get the minimal elapsed time for requests within sliding window
      * 
      * @return the minTime, or {@link Long#MAX_VALUE} if no requests have been
      *         added yet
      */
     public double getAllMinTime() {
         return allResponsesStats.getMin();
     }
     
     /**
      * Get the arithmetic mean of the stored values
      * 
      * @return The arithmetic mean of the stored values
      */
     public double getAllMean() {
         return allResponsesStats.getMean();
     }
     
     /**
      * Returns an estimate for the requested percentile of the stored values.
      * 
      * @param percentile
      *            the requested percentile (scaled from 0 - 100)
      * @return Returns an estimate for the requested percentile of the stored
      *         values.
      */
     public double getAllPercentile(double percentile) {
         return allResponsesStats.getPercentile(percentile);
     }
+
+    /**
+     * Returns hits to server
+     * @return the hits
+     */
+    public int getHits() {
+        return hits;
+    }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/backend/graphite/GraphiteBackendListenerClient.java b/src/components/org/apache/jmeter/visualizers/backend/graphite/GraphiteBackendListenerClient.java
index b1123d3eb..7a9fb4985 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/graphite/GraphiteBackendListenerClient.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/graphite/GraphiteBackendListenerClient.java
@@ -1,300 +1,304 @@
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
 
 package org.apache.jmeter.visualizers.backend.graphite;
 
 import java.text.DecimalFormat;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.Executors;
 import java.util.concurrent.ScheduledExecutorService;
 import java.util.concurrent.ScheduledFuture;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.visualizers.backend.AbstractBackendListenerClient;
 import org.apache.jmeter.visualizers.backend.BackendListenerContext;
 import org.apache.jmeter.visualizers.backend.SamplerMetric;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Graphite based Listener using Pickle Protocol
  * @see <a href="http://graphite.readthedocs.org/en/latest/overview.html">Graphite Overview</a>
  * @since 2.13
  */
 public class GraphiteBackendListenerClient extends AbstractBackendListenerClient implements Runnable {
     private static final int DEFAULT_PLAINTEXT_PROTOCOL_PORT = 2003;
     private static final String TEST_CONTEXT_NAME = "test";
     private static final String ALL_CONTEXT_NAME = "all";
 
     private static final Logger LOGGER = LoggingManager.getLoggerForClass();
     private static final String DEFAULT_METRICS_PREFIX = "jmeter."; //$NON-NLS-1$
     private static final String CUMULATED_METRICS = "__cumulated__"; //$NON-NLS-1$
     // User Metrics
     private static final String METRIC_MAX_ACTIVE_THREADS = "maxAT"; //$NON-NLS-1$
     private static final String METRIC_MIN_ACTIVE_THREADS = "minAT"; //$NON-NLS-1$
     private static final String METRIC_MEAN_ACTIVE_THREADS = "meanAT"; //$NON-NLS-1$
     private static final String METRIC_STARTED_THREADS = "startedT"; //$NON-NLS-1$
     private static final String METRIC_FINISHED_THREADS = "endedT"; //$NON-NLS-1$
     
     // Response time Metrics
     private static final String METRIC_SEPARATOR = "."; //$NON-NLS-1$
     private static final String METRIC_OK_PREFIX = "ok"; //$NON-NLS-1$
     private static final String METRIC_KO_PREFIX = "ko"; //$NON-NLS-1$
-    private static final String METRIC_ALL_PREFIX = "a";
-
+    private static final String METRIC_ALL_PREFIX = "a"; //$NON-NLS-1$
+    private static final String METRIC_HITS_PREFIX = "h"; //$NON-NLS-1$
     
     private static final String METRIC_COUNT = "count"; //$NON-NLS-1$
     private static final String METRIC_MIN_RESPONSE_TIME = "min"; //$NON-NLS-1$
     private static final String METRIC_MAX_RESPONSE_TIME = "max"; //$NON-NLS-1$
     private static final String METRIC_PERCENTILE = "pct"; //$NON-NLS-1$
     
     private static final String METRIC_OK_COUNT             = METRIC_OK_PREFIX+METRIC_SEPARATOR+METRIC_COUNT;
     private static final String METRIC_OK_MIN_RESPONSE_TIME = METRIC_OK_PREFIX+METRIC_SEPARATOR+METRIC_MIN_RESPONSE_TIME;
     private static final String METRIC_OK_MAX_RESPONSE_TIME = METRIC_OK_PREFIX+METRIC_SEPARATOR+METRIC_MAX_RESPONSE_TIME;
     private static final String METRIC_OK_PERCENTILE_PREFIX = METRIC_OK_PREFIX+METRIC_SEPARATOR+METRIC_PERCENTILE;
 
     private static final String METRIC_KO_COUNT             = METRIC_KO_PREFIX+METRIC_SEPARATOR+METRIC_COUNT;
     private static final String METRIC_KO_MIN_RESPONSE_TIME = METRIC_KO_PREFIX+METRIC_SEPARATOR+METRIC_MIN_RESPONSE_TIME;
     private static final String METRIC_KO_MAX_RESPONSE_TIME = METRIC_KO_PREFIX+METRIC_SEPARATOR+METRIC_MAX_RESPONSE_TIME;
     private static final String METRIC_KO_PERCENTILE_PREFIX = METRIC_KO_PREFIX+METRIC_SEPARATOR+METRIC_PERCENTILE;
 
     private static final String METRIC_ALL_COUNT             = METRIC_ALL_PREFIX+METRIC_SEPARATOR+METRIC_COUNT;
     private static final String METRIC_ALL_MIN_RESPONSE_TIME = METRIC_ALL_PREFIX+METRIC_SEPARATOR+METRIC_MIN_RESPONSE_TIME;
     private static final String METRIC_ALL_MAX_RESPONSE_TIME = METRIC_ALL_PREFIX+METRIC_SEPARATOR+METRIC_MAX_RESPONSE_TIME;
     private static final String METRIC_ALL_PERCENTILE_PREFIX = METRIC_ALL_PREFIX+METRIC_SEPARATOR+METRIC_PERCENTILE;
 
+    private static final String METRIC_ALL_HITS_COUNT        = METRIC_HITS_PREFIX+METRIC_SEPARATOR+METRIC_COUNT;
+
     private static final long ONE_SECOND = 1L;
     private static final int MAX_POOL_SIZE = 1;
     private static final String DEFAULT_PERCENTILES = "90;95;99";
     private static final String SEPARATOR = ";"; //$NON-NLS-1$
     private static final Object LOCK = new Object();
 
     private String graphiteHost;
     private int graphitePort;
     private boolean summaryOnly;
     private String rootMetricsPrefix;
     private String samplersList = ""; //$NON-NLS-1$
     private Set<String> samplersToFilter;
     private Map<String, Float> okPercentiles;
     private Map<String, Float> koPercentiles;
     private Map<String, Float> allPercentiles;
     
 
     private GraphiteMetricsSender graphiteMetricsManager;
 
     private ScheduledExecutorService scheduler;
     private ScheduledFuture<?> timerHandle;
     
     public GraphiteBackendListenerClient() {
         super();
     }    
 
     @Override
     public void run() {
         sendMetrics();
     }
 
     /**
      * Send metrics to Graphite
      */
     protected void sendMetrics() {
         // Need to convert millis to seconds for Graphite
         long timestampInSeconds = TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
         synchronized (LOCK) {
             for (Map.Entry<String, SamplerMetric> entry : getMetricsPerSampler().entrySet()) {
                 SamplerMetric metric = entry.getValue();
                 if(entry.getKey().equals(CUMULATED_METRICS)) {
                     addMetrics(timestampInSeconds, ALL_CONTEXT_NAME, metric);
                 } else {
                     addMetrics(timestampInSeconds, AbstractGraphiteMetricsSender.sanitizeString(entry.getKey()), metric);                
                 }
                 // We are computing on interval basis so cleanup
                 metric.resetForTimeInterval();
             }
         }        
         graphiteMetricsManager.addMetric(timestampInSeconds, TEST_CONTEXT_NAME, METRIC_MIN_ACTIVE_THREADS, Integer.toString(getUserMetrics().getMinActiveThreads()));
         graphiteMetricsManager.addMetric(timestampInSeconds, TEST_CONTEXT_NAME, METRIC_MAX_ACTIVE_THREADS, Integer.toString(getUserMetrics().getMaxActiveThreads()));
         graphiteMetricsManager.addMetric(timestampInSeconds, TEST_CONTEXT_NAME, METRIC_MEAN_ACTIVE_THREADS, Integer.toString(getUserMetrics().getMeanActiveThreads()));
         graphiteMetricsManager.addMetric(timestampInSeconds, TEST_CONTEXT_NAME, METRIC_STARTED_THREADS, Integer.toString(getUserMetrics().getStartedThreads()));
         graphiteMetricsManager.addMetric(timestampInSeconds, TEST_CONTEXT_NAME, METRIC_FINISHED_THREADS, Integer.toString(getUserMetrics().getFinishedThreads()));
 
         graphiteMetricsManager.writeAndSendMetrics();
     }
 
 
     /**
      * Add request metrics to metrics manager.
      * Note if total number of requests is 0, no response time metrics are sent.
      * @param timestampInSeconds long
      * @param contextName String
      * @param metric {@link SamplerMetric}
      */
     private void addMetrics(long timestampInSeconds, String contextName, SamplerMetric metric) {
         graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_OK_COUNT, Integer.toString(metric.getSuccesses()));
         graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_KO_COUNT, Integer.toString(metric.getFailures()));
         graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_ALL_COUNT, Integer.toString(metric.getTotal()));
+        graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_ALL_HITS_COUNT, Integer.toString(metric.getHits()));
+
         // See https://bz.apache.org/bugzilla/show_bug.cgi?id=57350
         if(metric.getTotal() > 0) { 
             if(metric.getSuccesses()>0) {
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_OK_MIN_RESPONSE_TIME, Double.toString(metric.getOkMinTime()));
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_OK_MAX_RESPONSE_TIME, Double.toString(metric.getOkMaxTime()));
                 for (Map.Entry<String, Float> entry : okPercentiles.entrySet()) {
                     graphiteMetricsManager.addMetric(timestampInSeconds, contextName, 
                             entry.getKey(), 
                             Double.toString(metric.getOkPercentile(entry.getValue().floatValue())));            
                 }
             } 
             if(metric.getFailures()>0) {
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_KO_MIN_RESPONSE_TIME, Double.toString(metric.getKoMinTime()));
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_KO_MAX_RESPONSE_TIME, Double.toString(metric.getKoMaxTime()));
                 for (Map.Entry<String, Float> entry : koPercentiles.entrySet()) {
                     graphiteMetricsManager.addMetric(timestampInSeconds, contextName, 
                             entry.getKey(), 
                             Double.toString(metric.getKoPercentile(entry.getValue().floatValue())));            
                 }   
             }
             if(metric.getTotal()>0) {
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_ALL_MIN_RESPONSE_TIME, Double.toString(metric.getAllMinTime()));
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_ALL_MAX_RESPONSE_TIME, Double.toString(metric.getAllMaxTime()));
                 for (Map.Entry<String, Float> entry : allPercentiles.entrySet()) {
                     graphiteMetricsManager.addMetric(timestampInSeconds, contextName, 
                             entry.getKey(), 
                             Double.toString(metric.getAllPercentile(entry.getValue().floatValue())));            
                 }   
                 
             }
         }
     }
 
     /**
      * @return the samplersList
      */
     public String getSamplersList() {
         return samplersList;
     }
 
     /**
      * @param samplersList the samplersList to set
      */
     public void setSamplersList(String samplersList) {
         this.samplersList = samplersList;
     }
 
     @Override
     public void handleSampleResults(List<SampleResult> sampleResults,
             BackendListenerContext context) {
         synchronized (LOCK) {
             for (SampleResult sampleResult : sampleResults) {
                 getUserMetrics().add(sampleResult);
                 if(!summaryOnly && samplersToFilter.contains(sampleResult.getSampleLabel())) {
                     SamplerMetric samplerMetric = getSamplerMetric(sampleResult.getSampleLabel());
                     samplerMetric.add(sampleResult);
                 }
                 SamplerMetric cumulatedMetrics = getSamplerMetric(CUMULATED_METRICS);
                 cumulatedMetrics.add(sampleResult);                    
             }
         }
     }
 
     @Override
     public void setupTest(BackendListenerContext context) throws Exception {
         String graphiteMetricsSenderClass = context.getParameter("graphiteMetricsSender");
         
         graphiteHost = context.getParameter("graphiteHost");
         graphitePort = context.getIntParameter("graphitePort", DEFAULT_PLAINTEXT_PROTOCOL_PORT);
         summaryOnly = context.getBooleanParameter("summaryOnly", true);
         samplersList = context.getParameter("samplersList", "");
         rootMetricsPrefix = context.getParameter("rootMetricsPrefix", DEFAULT_METRICS_PREFIX);
         String percentilesAsString = context.getParameter("percentiles", DEFAULT_METRICS_PREFIX);
         String[]  percentilesStringArray = percentilesAsString.split(SEPARATOR);
         okPercentiles = new HashMap<String, Float>(percentilesStringArray.length);
         koPercentiles = new HashMap<String, Float>(percentilesStringArray.length);
         allPercentiles = new HashMap<String, Float>(percentilesStringArray.length);
         DecimalFormat format = new DecimalFormat("0.##");
         for (int i = 0; i < percentilesStringArray.length; i++) {
             if(!StringUtils.isEmpty(percentilesStringArray[i].trim())) {
                 try {
                     Float percentileValue = Float.parseFloat(percentilesStringArray[i].trim());
                     okPercentiles.put(
                             METRIC_OK_PERCENTILE_PREFIX+AbstractGraphiteMetricsSender.sanitizeString(format.format(percentileValue)),
                             percentileValue);
                     koPercentiles.put(
                             METRIC_KO_PERCENTILE_PREFIX+AbstractGraphiteMetricsSender.sanitizeString(format.format(percentileValue)),
                             percentileValue);
                     allPercentiles.put(
                             METRIC_ALL_PERCENTILE_PREFIX+AbstractGraphiteMetricsSender.sanitizeString(format.format(percentileValue)),
                             percentileValue);
 
                 } catch(Exception e) {
                     LOGGER.error("Error parsing percentile:'"+percentilesStringArray[i]+"'", e);
                 }
             }
         }
         Class<?> clazz = Class.forName(graphiteMetricsSenderClass);
         this.graphiteMetricsManager = (GraphiteMetricsSender) clazz.newInstance();
         graphiteMetricsManager.setup(graphiteHost, graphitePort, rootMetricsPrefix);
         String[] samplers = samplersList.split(SEPARATOR);
         samplersToFilter = new HashSet<String>();
         for (String samplerName : samplers) {
             samplersToFilter.add(samplerName);
         }
         scheduler = Executors.newScheduledThreadPool(MAX_POOL_SIZE);
         // Don't change this as metrics are per second
         this.timerHandle = scheduler.scheduleAtFixedRate(this, ONE_SECOND, ONE_SECOND, TimeUnit.SECONDS);
     }
 
     @Override
     public void teardownTest(BackendListenerContext context) throws Exception {
         boolean cancelState = timerHandle.cancel(false);
         if(LOGGER.isDebugEnabled()) {
             LOGGER.debug("Canceled state:"+cancelState);
         }
         scheduler.shutdown();
         try {
             scheduler.awaitTermination(30, TimeUnit.SECONDS);
         } catch (InterruptedException e) {
             LOGGER.error("Error waiting for end of scheduler");
         }
         // Send last set of data before ending
         sendMetrics();
         
         samplersToFilter.clear();
         graphiteMetricsManager.destroy();
         super.teardownTest(context);
     }
 
     @Override
     public Arguments getDefaultParameters() {
         Arguments arguments = new Arguments();
         arguments.addArgument("graphiteMetricsSender", TextGraphiteMetricsSender.class.getName());
         arguments.addArgument("graphiteHost", "");
         arguments.addArgument("graphitePort", Integer.toString(DEFAULT_PLAINTEXT_PROTOCOL_PORT));
         arguments.addArgument("rootMetricsPrefix", DEFAULT_METRICS_PREFIX);
         arguments.addArgument("summaryOnly", "true");
         arguments.addArgument("samplersList", "");
         arguments.addArgument("percentiles", DEFAULT_PERCENTILES);
         return arguments;
     }
 }
diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index c1a16d21f..1754afadf 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,323 +1,337 @@
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
 
 package org.apache.jmeter.control;
 
 import java.io.Serializable;
 
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterThread;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.threads.ListenerNotifier;
 import org.apache.jmeter.threads.SamplePackage;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Transaction Controller to measure transaction times
  *
  * There are two different modes for the controller:
  * - generate additional total sample after nested samples (as in JMeter 2.2)
  * - generate parent sampler containing the nested samples
  *
  */
 public class TransactionController extends GenericController implements SampleListener, Controller, Serializable {
+    /**
+     * Used to identify Transaction Controller Parent Sampler
+     */
+    static final String NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX = "Number of samples in transaction : ";
+
     private static final long serialVersionUID = 233L;
     
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     private static final String PARENT = "TransactionController.parent";// $NON-NLS-1$
 
     private static final String INCLUDE_TIMERS = "TransactionController.includeTimers";// $NON-NLS-1$
     
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final boolean DEFAULT_VALUE_FOR_INCLUDE_TIMERS = true; // default true for compatibility
 
     /**
      * Only used in parent Mode
      */
     private transient TransactionSampler transactionSampler;
     
     /**
      * Only used in NON parent Mode
      */
     private transient ListenerNotifier lnf;
 
     /**
      * Only used in NON parent Mode
      */
     private transient SampleResult res;
     
     /**
      * Only used in NON parent Mode
      */
     private transient int calls;
     
     /**
      * Only used in NON parent Mode
      */
     private transient int noFailingSamples;
 
     /**
      * Cumulated pause time to excluse timer and post/pre processor times
      * Only used in NON parent Mode
      */
     private transient long pauseTime;
 
     /**
      * Previous end time
      * Only used in NON parent Mode
      */
     private transient long prevEndTime;
 
     /**
      * Creates a Transaction Controller
      */
     public TransactionController() {
         lnf = new ListenerNotifier();
     }
 
     @Override
     protected Object readResolve(){
         super.readResolve();
         lnf = new ListenerNotifier();
         return this;
     }
 
     public void setParent(boolean _parent){
         setProperty(new BooleanProperty(PARENT, _parent));
     }
 
     public boolean isParent(){
         return getPropertyAsBoolean(PARENT);
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#next()
      */
     @Override
     public Sampler next(){
         if (isParent()){
             return next1();
         }
         return next2();
     }
 
 ///////////////// Transaction Controller - parent ////////////////
 
     private Sampler next1() {
         // Check if transaction is done
         if(transactionSampler != null && transactionSampler.isTransactionDone()) {
             if (log.isDebugEnabled()) {
                 log.debug("End of transaction " + getName());
             }
             // This transaction is done
             transactionSampler = null;
             return null;
         }
 
         // Check if it is the start of a new transaction
         if (isFirst()) // must be the start of the subtree
         {
             if (log.isDebugEnabled()) {
                 log.debug("Start of transaction " + getName());
             }
             transactionSampler = new TransactionSampler(this, getName());
         }
 
         // Sample the children of the transaction
         Sampler subSampler = super.next();
         transactionSampler.setSubSampler(subSampler);
         // If we do not get any sub samplers, the transaction is done
         if (subSampler == null) {
             transactionSampler.setTransactionDone();
         }
         return transactionSampler;
     }
 
     @Override
     protected Sampler nextIsAController(Controller controller) throws NextIsNullException {
         if (!isParent()) {
             return super.nextIsAController(controller);
         }
         Sampler returnValue;
         Sampler sampler = controller.next();
         if (sampler == null) {
             currentReturnedNull(controller);
             // We need to call the super.next, instead of this.next, which is done in GenericController,
             // because if we call this.next(), it will return the TransactionSampler, and we do not want that.
             // We need to get the next real sampler or controller
             returnValue = super.next();
         } else {
             returnValue = sampler;
         }
         return returnValue;
     }
 
 ////////////////////// Transaction Controller - additional sample //////////////////////////////
 
     private Sampler next2() {
         if (isFirst()) // must be the start of the subtree
         {
             calls = 0;
             noFailingSamples = 0;
             res = new SampleResult();
             res.setSampleLabel(getName());
             // Assume success
             res.setSuccessful(true);
             res.sampleStart();
             prevEndTime = res.getStartTime();//???
             pauseTime = 0;
         }
         boolean isLast = current==super.subControllersAndSamplers.size();
         Sampler returnValue = super.next();
         if (returnValue == null && isLast) // Must be the end of the controller
         {
             if (res != null) {
                 // See BUG 55816
                 if (!isIncludeTimers()) {
                     long processingTimeOfLastChild = res.currentTimeInMillis() - prevEndTime;
                     pauseTime += processingTimeOfLastChild;
                 }
                 res.setIdleTime(pauseTime+res.getIdleTime());
                 res.sampleEnd();
-                res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
+                res.setResponseMessage(TransactionController.NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX + calls + ", number of failing samples : " + noFailingSamples);
                 if(res.isSuccessful()) {
                     res.setResponseCodeOK();
                 }
                 notifyListeners();
             }
         }
         else {
             // We have sampled one of our children
             calls++;
         }
 
         return returnValue;
     }
+    
+    /**
+     * @param res {@link SampleResult}
+     * @return true if res is the ParentSampler transactions
+     */
+    public static final boolean isFromTransactionController(SampleResult res) {
+        return res.getResponseMessage() != null && 
+                res.getResponseMessage().startsWith(TransactionController.NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX);
+    }
 
     /**
      * @see org.apache.jmeter.control.GenericController#triggerEndOfLoop()
      */
     @Override
     public void triggerEndOfLoop() {
         if(!isParent()) {
             if (res != null) {
                 res.setIdleTime(pauseTime+res.getIdleTime());
                 res.sampleEnd();
                 res.setSuccessful(TRUE.equals(JMeterContextService.getContext().getVariables().get(JMeterThread.LAST_SAMPLE_OK)));
-                res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
+                res.setResponseMessage(TransactionController.NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX + calls + ", number of failing samples : " + noFailingSamples);
                 notifyListeners();
             }
         } else {
             Sampler subSampler = transactionSampler.getSubSampler();
             // See Bug 56811
             // triggerEndOfLoop is called when error occurs to end Main Loop
             // in this case normal workflow doesn't happen, so we need 
             // to notify the childs of TransactionController and 
             // update them with SubSamplerResult
             if(subSampler instanceof TransactionSampler) {
                 TransactionSampler tc = (TransactionSampler) subSampler;
                 tc.getTransactionController().triggerEndOfLoop();
                 transactionSampler.addSubSamplerResult(tc.getTransactionResult());
             }
             transactionSampler.setTransactionDone();
             // This transaction is done
             transactionSampler = null;
         }
         super.triggerEndOfLoop();
     }
 
     /**
      * Create additional SampleEvent in NON Parent Mode
      */
     protected void notifyListeners() {
         // TODO could these be done earlier (or just once?)
         JMeterContext threadContext = getThreadContext();
         JMeterVariables threadVars = threadContext.getVariables();
         SamplePackage pack = (SamplePackage) threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
         if (pack == null) {
             // If child of TransactionController is a ThroughputController and TPC does
             // not sample its children, then we will have this
             // TODO Should this be at warn level ?
             log.warn("Could not fetch SamplePackage");
         } else {
             SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars, true);
             // We must set res to null now, before sending the event for the transaction,
             // so that we can ignore that event in our sampleOccured method
             res = null;
             lnf.notifyListeners(event, pack.getSampleListeners());
         }
     }
 
     @Override
     public void sampleOccurred(SampleEvent se) {
         if (!isParent()) {
             // Check if we are still sampling our children
             if(res != null && !se.isTransactionSampleEvent()) {
                 SampleResult sampleResult = se.getResult();
                 res.setThreadName(sampleResult.getThreadName());
                 res.setBytes(res.getBytes() + sampleResult.getBytes());
                 if (!isIncludeTimers()) {// Accumulate waiting time for later
                     pauseTime += sampleResult.getEndTime() - sampleResult.getTime() - prevEndTime;
                     prevEndTime = sampleResult.getEndTime();
                 }
                 if(!sampleResult.isSuccessful()) {
                     res.setSuccessful(false);
                     noFailingSamples++;
                 }
                 res.setAllThreads(sampleResult.getAllThreads());
                 res.setGroupThreads(sampleResult.getGroupThreads());
                 res.setLatency(res.getLatency() + sampleResult.getLatency());
                 res.setConnectTime(res.getConnectTime() + sampleResult.getConnectTime());
             }
         }
     }
 
     @Override
     public void sampleStarted(SampleEvent e) {
     }
 
     @Override
     public void sampleStopped(SampleEvent e) {
     }
 
     /**
      * Whether to include timers and pre/post processor time in overall sample.
      * @param includeTimers Flag whether timers and pre/post processor should be included in overall sample
      */
     public void setIncludeTimers(boolean includeTimers) {
         setProperty(INCLUDE_TIMERS, includeTimers, DEFAULT_VALUE_FOR_INCLUDE_TIMERS);
     }
 
     /**
      * Whether to include timer and pre/post processor time in overall sample.
      *
      * @return boolean (defaults to true for backwards compatibility)
      */
     public boolean isIncludeTimers() {
         return getPropertyAsBoolean(INCLUDE_TIMERS, DEFAULT_VALUE_FOR_INCLUDE_TIMERS);
     }
 }
diff --git a/src/core/org/apache/jmeter/control/TransactionSampler.java b/src/core/org/apache/jmeter/control/TransactionSampler.java
index f3f79a51c..c6cc2dca2 100644
--- a/src/core/org/apache/jmeter/control/TransactionSampler.java
+++ b/src/core/org/apache/jmeter/control/TransactionSampler.java
@@ -1,156 +1,156 @@
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
 
 /*
  *  N.B. Although this is a type of sampler, it is only used by the transaction controller,
  *  and so is in the control package
 */
 package org.apache.jmeter.control;
 
 
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.TestElement;
 
 /**
  * Transaction Sampler class to measure transaction times
  * (not exposed a a GUI class, as it is only used internally by TransactionController in Generate Parent sample mode)
  */
 public class TransactionSampler extends AbstractSampler {
     private static final long serialVersionUID = 240L;
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<String>(
             Arrays.asList(new String[]{
                     "org.apache.jmeter.config.gui.SimpleConfigGui"}));
 
     private boolean transactionDone = false;
 
     private TransactionController transactionController;
 
     private Sampler subSampler;
 
     private SampleResult transactionSampleResult;
 
     private int calls = 0;
 
     private int noFailingSamples = 0;
 
     private int totalTime = 0;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public TransactionSampler(){
         //log.warn("Constructor only intended for use in testing");
     }
 
     public TransactionSampler(TransactionController controller, String name) {
         transactionController = controller;
         setName(name); // ensure name is available for debugging
         transactionSampleResult = new SampleResult();
         transactionSampleResult.setSampleLabel(name);
         // Assume success
         transactionSampleResult.setSuccessful(true);
         transactionSampleResult.sampleStart();
     }
 
     /**
      * One cannot sample the TransactionSampler directly.
      */
     @Override
     public SampleResult sample(Entry e) {
         throw new RuntimeException("Cannot sample TransactionSampler directly");
         // It is the JMeterThread which knows how to sample a real sampler
     }
 
     public Sampler getSubSampler() {
         return subSampler;
     }
 
     public SampleResult getTransactionResult() {
         return transactionSampleResult;
     }
 
     public TransactionController getTransactionController() {
         return transactionController;
     }
 
     public boolean isTransactionDone() {
         return transactionDone;
     }
 
     public void addSubSamplerResult(SampleResult res) {
         // Another subsample for the transaction
         calls++;
         
         // Set Response code of transaction
         if (noFailingSamples == 0) {
             transactionSampleResult.setResponseCode(res.getResponseCode());
         }
 
         // The transaction fails if any sub sample fails
         if (!res.isSuccessful()) {
             transactionSampleResult.setSuccessful(false);
             noFailingSamples++;
         }
         // Add the sub result to the transaction result
         transactionSampleResult.addSubResult(res);
         // Add current time to total for later use (exclude pause time)
         totalTime += res.getTime();
     }
 
     protected void setTransactionDone() {
         this.transactionDone = true;
         // Set the overall status for the transaction sample
         // TODO: improve, e.g. by adding counts to the SampleResult class
-        transactionSampleResult.setResponseMessage("Number of samples in transaction : "
+        transactionSampleResult.setResponseMessage(TransactionController.NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX
                         + calls + ", number of failing samples : "
                         + noFailingSamples);
         if (transactionSampleResult.isSuccessful()) {
             transactionSampleResult.setResponseCodeOK();
         }
         // Bug 50080 (not include pause time when generate parent)
         if (!transactionController.isIncludeTimers()) {
             long end = transactionSampleResult.currentTimeInMillis();
             transactionSampleResult.setIdleTime(end
                     - transactionSampleResult.getStartTime() - totalTime);
             transactionSampleResult.setEndTime(end);
         }
     }
 
     protected void setSubSampler(Sampler subSampler) {
         this.subSampler = subSampler;
     }
 
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 53817bdff..aac6c540b 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,262 +1,264 @@
 <?xml version="1.0"?> 
 <!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 -->
 <document>   
 <properties>     
     <author email="dev AT jmeter.apache.org">JMeter developers</author>     
     <title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.14 =================== -->
 
 <h1>Version 2.14</h1>
 
 Summary
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Known bugs">Known bugs</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <!-- <ch_category>Improvements</ch_category> -->
 <!-- <ch_title>Sample title</ch_title>
 <p>
 <ul>
 <li>Sample text</li>
 </ul>
 </p>
 
 <ch_title>Sample title</ch_title>
 <p>Sample text</p>
 <figure width="691" height="215" image="changes/2.10/18_https_test_script_recorder.png"></figure>
  -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>In RandomTimer class, protected instance timer has been replaced by getTimer() protected method, this is related to <bugzilla>58100</bugzilla>. This may impact 3rd party plugins.</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57696</bug>HTTP Request : Improve responseMessage when resource download fails. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>57995</bug>Use FileServer for HTTP Request files. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>57928</bug>Add ability to define protocol (http/https) to AccessLogSampler GUI. Contributed by Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58041</bug>Tree View Listener should show sample data type</li>
+<li><bug>58122</bug>GraphiteBackendListener : Add Server Hits metric. Partly based on a patch from Amol Moye (amol.moye at thomsonreuters.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>57913</bug>Automated backups of last saved JMX files. Contributed by Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li><bug>57988</bug>Shortcuts (Ctrl+1 .. Ctrl+9) to quick add elements into test plan. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 <li><bug>58100</bug>Performance enhancements : Replace Random by ThreadLocalRandom.</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to tika-core and tika-parsers 1.9 (from 1.7)</li>
 <li>Updated to commons-math3 3.5 (from 3.4.1)</li>
 <li>Updated to commons-pool2 2.4.1 (from 2.3)</li>
 <li><bug>57981</bug>Require a minimum of Java 7.</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57806</bug>"audio/x-mpegurl" mime type is erroneously considered as binary by ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>57858</bug>Don't call sampleEnd twice in HTTPHC4Impl when a RuntimeException or an IOException occurs in the sample method.</li>
     <li><bug>57921</bug>HTTP/1.1 without keep-alive "Connection" response header no longer uses infinite keep-alive.</li>
     <li><bug>57956</bug>The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin.</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>58013</bug>Enable all protocols that are enabled on the default SSLContext for usage with the SMTP Sampler.</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58033</bug> SampleResultConverter should note that it cannot record non-TEXT data</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bug>57825</bug>__Random function fails if min value is equal to max value (regression related to <bugzilla>54453</bugzilla>)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54826</bug>Don't fail on long strings in JSON responses when displaying them as JSON in View Results Tree.</li>
     <li><bug>57734</bug>Maven transient dependencies are incorrect for 2.13</li>
     <li><bug>57821</bug>Command-line option "-X --remoteexit" doesn't work since 2.13 (regression related to <bugzilla>57500</bugzilla>)</li>
     <li><bug>57731</bug>TESTSTART.MS has always the value of the first Test started in Server mode in NON GUI Distributed testing</li>
     <li><bug>58016</bug> Error type casting using external SSL Provider. Contributed by Kirill Yankov (myworkpostbox at gmail.com)</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li>Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
 <li>Kirill Yankov (myworkpostbox at gmail.com)</li>
+<li>Amol Moye (amol.moye at thomsonreuters.com)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs =================== -->
  
 <ch_section>Known bugs</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 the total number of threads only applies to a locally run test, otherwise it will show 0 (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <pre>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(...) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error: <i>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</i>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing 
 the Java <b>jdk.certpath.disabledAlgorithms</b> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <pre>JAVA_HOME/jre/lib/security/java.security</pre>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "px" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
diff --git a/xdocs/usermanual/realtime-results.xml b/xdocs/usermanual/realtime-results.xml
index f1a66eadc..28a492aa9 100644
--- a/xdocs/usermanual/realtime-results.xml
+++ b/xdocs/usermanual/realtime-results.xml
@@ -1,180 +1,182 @@
 <?xml version="1.0"?>
 <!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 -->
 
 <!DOCTYPE document[
 <!ENTITY sect-num '16'>
 ]>
 
 <document prev="remote-test.html" next="best-practices.html" id="$Id: realtime-results.xml 1457581 2013-03-17 22:58:08Z sebb $">
 
 <properties>
   <author email="p.mouawad at ubik-ingenierie.com">Philippe Mouawad</author>
   <title>User's Manual: Live Statistics</title>
 </properties>
 
 <body>
 
 <section name="&sect-num;. Real-time results" anchor="realtime-results">
 <p>Since JMeter 2.13 you can get realtime results sent to a backend through the 
 <a href="component_reference.html#Backend_Listener">Backend Listener</a> using potentially any backend (JDBC, JMS, Webservice...) implementing <a href="../api/org/apache/jmeter/visualizers/backend/AbstractBackendListenerClient.html" >AbstractBackendListenerClient</a>.<br/>
 JMeter ships with a GraphiteBackendListenerClient which allows you to send metrics to a Graphite Backend.<br/>
 This feature provides:
 <ul>
 <li>Live results</li>
 <li>Nice graphs for metrics</li>
 <li>Ability to compare 2 or more load tests</li>
 <li>Storing monitoring data as long as JMeter results in the same backend</li>
 <li>...</li>
 </ul>
 In this document we will present the configuration setup to graph and historize the data in 2 different backends:
 <ul>
 <li>InfluxDB</li>
 <li>Graphite</li>
 </ul>
 </p>
 <subsection name="&sect-num;.1 Metrics exposed" anchor="metrics">
     <subsection name="&sect-num;.1.1 Thread/Virtual Users metrics" anchor="metrics-threads">
     <p>
     Threads metrics are the following:
     </p>
     <dl>
       <dh><code>&lt;rootMetricsPrefix&gt;.test.minAT</code></dh><dd>Min active threads</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.test.maxAT</code></dh><dd>Max active threads</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.test.meanAT</code></dh><dd>Mean active threads</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.test.startedT</code></dh><dd>Started threads</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.test.endedT</code></dh><dd>Finished threads</dd>
     </dl>
     </subsection>
     <subsection name="&sect-num;.1.2 Response times metrics" anchor="metrics-response-times">
-    <p>Response times metrics are the following:</p>
+    <p>Response related metrics are the following:</p>
     <dl>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.ok.count</code></dh>
       <dd>Number of successful responses for sampler name</dd>
+      <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.h.count</code></dh>
+      <dd>Server hits per seconds, this metric cumulates Sample Result and Sub results (if using Transaction Controller, "Generate parent sampler" should be unchecked)</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.ok.min</code></dh>
       <dd>Min response time for successful responses of sampler name</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.ok.max</code></dh>
       <dd>Max response time for successful responses of sampler name</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.ok.pct&lt;percentileValue&gt;</code></dh>
       <dd>Percentile computed for successful responses of sampler name. You can input as many percentiles as you want (3 or 4 being a reasonable value).<br/>
       When percentile contains a comma for example "<code>99.9</code>", dot is sanitized by "<code>_</code>" leading to <code>99_9</code>.   
       By default listener computes percentiles 90%, 95% and 99%</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.ko.count</code></dh>
       <dd>Number of failed responses for sampler name</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.ko.min</code></dh>
       <dd>Min response time for failed responses of sampler name</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.ko.max</code></dh>
       <dd>Max response time for failed responses of sampler name</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.ko.pct&lt;percentileValue&gt;</code></dh>
       <dd>Percentile computed for failed responses of sampler name. You can input as many percentiles as you want (3 or 4 being a reasonable value).<br/>
       When percentile contains a comma for example "<code>99.9</code>", dot is sanitized by "<code>_</code>" leading to <code>99_9</code>.
       By default listener computes percentiles 90%, 95% and 99%</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.a.count</code></dh>
       <dd>Number of responses for sampler name</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.a.min</code></dh>
       <dd>Min response time for responses of sampler name</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.a.max</code></dh>
       <dd>Max response time for responses of sampler name</dd>
       <dh><code>&lt;rootMetricsPrefix&gt;.&lt;samplerName&gt;.a.pct&lt;percentileValue&gt;</code></dh>
       <dd>Percentile computed for responses of sampler name. You can input as many percentiles as you want (3 or 4 being a reasonable value).<br/>
       When percentile contains a comma for example "<code>99.9</code>", dot is sanitized by "<code>_</code>" leading to <code>99_9</code>.
       By default listener computes percentiles 90%, 95% and 99%</dd>
     </dl>
     <p>
     By default JMeter sends only metrics for all samplers using "<code>all</code>" as samplerName.
     </p>
     </subsection>
 </subsection>
 <subsection name="&sect-num;.2 JMeter configuration" anchor="jmeter-configuration">
     <p>
     To make JMeter send metrics to backend add a <a href="./component_reference.html#Backend_Listener" >BackendListener</a> using the GraphiteBackendListenerClient.
     </p>
     <figure width="902" height="341" image="backend_listener.png">Graphite configuration</figure>
 </subsection>
 
 <subsection name="&sect-num;.2 InfluxDB" anchor="influxdb">
 <p>InfluxDB is an open-source, distributed,time-series database that allows to 
 easily store metrics.
 Installation and configuration is very easy, read this for more details <a href="http://influxdb.com/docs/v0.8/introduction/installation.html" target="_blank" >InfluxDB documentation</a>.<br/>
 InfluxDB data can be easily viewed in a browser through either <a href="https://github.com/hakobera/influga"  target="_blank">Influga</a> or <a href="http://grafana.org/"  target="_blank">Grafana</a>.
 We will use Grafana in this case.
 </p>
     <subsection name="&sect-num;.2.1 InfluxDB graphite listener configuration" anchor="influxdb_configuration">
     <p>To enable Graphite listener in InfluxDB, edit files <code>/opt/influxdb/shared/config.toml</code> or <code>/usr/local/etc/influxdb.conf</code>,
     find "<code>input_plugins.graphite</code>" and set this:
     </p>
     <source>
 # Configure the graphite api
 [input_plugins.graphite]
 enabled = true
 address = "0.0.0.0" # If not set, is actually set to bind-address.
 port = 2003
 database = "jmeter"  # store graphite data in this database
 # udp_enabled = true # enable udp interface on the same port as the tcp interface
     </source>
     </subsection>
     <subsection name="&sect-num;.2.2 InfluxDB database configuration" anchor="influxdb_db_configuration">
     <p>Connect to InfluxDB admin console and create two databases:
     <ul>
         <li>grafana : Used by Grafana to store the dashboards we will create</li>
         <li>jmeter : Used by InfluxDB to store the data sent to Graphite Listener as per database="jmeter" config
         element in <code>influxdb.conf</code> or <code>config.toml</code></li>
     </ul>
     </p>
     </subsection>
     <subsection name="&sect-num;.2.3 Grafana configuration" anchor="grafana_configuration">
     <p>
     Installing grafana is just a matter of putting the unzipped bundle behind an Apache HTTP server.<br/>
     Read <a href="http://grafana.org/docs/" targer="_blank">documentation</a> for more details.
     Open <code>config.js</code> file and find <code>datasources</code> element, and edit it like this:<br/>
     </p>
     <source>
 datasources: {
   influxdb: {
     type: 'influxdb',
     url: "http://localhost:8086/db/jmeter",
     username: 'root',
     password: 'root',
   },
   grafana: {
     type: 'influxdb',
     url: "http://localhost:8086/db/grafana",
     username: 'root',
     password: 'root',
     grafanaDB: true
   },
 },
     </source>
     <note>
     Note that grafana has "<code>grafanaDB:true</code>". Also note that here we use <code>root</code> user for simplicity
     It is better to dedicate a special user with less rights.
     </note>
     Here is the kind of dashboard that you could obtain:
     <figure width="1265" height="581" image="grafana_dashboard.png">Grafana dashboard</figure> 
     
     </subsection>
 </subsection>
 
 <subsection name="&sect-num;.3 Graphite" anchor="graphite">
 <p>TODO.</p>
 </subsection>
 
 
 </section>
 
 </body>
 </document>
