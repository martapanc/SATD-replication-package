diff --git a/src/core/org/apache/jmeter/report/core/CsvSampleReader.java b/src/core/org/apache/jmeter/report/core/CsvSampleReader.java
index e3acaf6b0..ce3a8adfb 100644
--- a/src/core/org/apache/jmeter/report/core/CsvSampleReader.java
+++ b/src/core/org/apache/jmeter/report/core/CsvSampleReader.java
@@ -1,215 +1,217 @@
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
 package org.apache.jmeter.report.core;
 
 import java.io.BufferedReader;
 import java.io.Closeable;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.UnsupportedEncodingException;
 import java.nio.charset.StandardCharsets;
 
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Reader class for reading CSV files.
  * <p>
  * Handles {@link SampleMetadata} reading and sample extraction.
  * </p>
  * 
  * @since 3.0
  */
 public class CsvSampleReader implements Closeable{
 
     private static final Logger LOG = LoggingManager.getLoggerForClass();
     private static final int BUF_SIZE = 10000;
 
     private static final String CHARSET = SaveService.getFileEncoding(StandardCharsets.UTF_8.displayName());
 
     private static final char DEFAULT_SEPARATOR =
-            JMeterUtils.getPropDefault("jmeter.save.saveservice.default_delimiter", ",").charAt(0); //$NON-NLS-1$ //$NON-NLS-2$
+            // We cannot use JMeterUtils#getPropDefault as it applies a trim on value
+            JMeterUtils.getDelimiter(
+                    JMeterUtils.getJMeterProperties().getProperty(SampleSaveConfiguration.DEFAULT_DELIMITER_PROP, SampleSaveConfiguration.DEFAULT_DELIMITER)).charAt(0);
 
     private File file;
 
     private BufferedReader reader;
 
     private char separator;
 
     private long row;
 
     private SampleMetadata metadata;
 
     private int columnCount;
 
     private Sample lastSampleRead;
 
     /**
      * Instantiates a new csv sample reader.
      *
      * @param inputFile
      *            the input file (must not be {@code null})
      * @param separator
      *            the separator
      * @param useSaveSampleCfg
      *            indicates whether the reader uses jmeter
      *            SampleSaveConfiguration to define metadata
      */
     public CsvSampleReader(File inputFile, char separator, boolean useSaveSampleCfg) {
         this(inputFile, null, separator, useSaveSampleCfg);
     }
 
     /**
      * Instantiates a new csv sample reader.
      *
      * @param inputFile
      *            the input file (must not be {@code null})
      * @param metadata
      *            the metadata
      */
     public CsvSampleReader(File inputFile, SampleMetadata metadata) {
         this(inputFile, metadata, DEFAULT_SEPARATOR, false);
     }
 
     private CsvSampleReader(File inputFile, SampleMetadata metadata,
             char separator, boolean useSaveSampleCfg) {
         if (!(inputFile.isFile() && inputFile.canRead())) {
             throw new IllegalArgumentException(inputFile.getAbsolutePath()
                     + " does not exist or is not readable");
         }
         this.file = inputFile;
         try {
             this.reader = new BufferedReader(new InputStreamReader(
                     new FileInputStream(file), CHARSET), BUF_SIZE);
         } catch (FileNotFoundException | UnsupportedEncodingException ex) {
             throw new SampleException("Could not create file reader !", ex);
         }
         if (metadata == null) {
             this.metadata = readMetadata(separator, useSaveSampleCfg);
         } else {
             this.metadata = metadata;
         }
         this.columnCount = this.metadata.getColumnCount();
         this.separator = this.metadata.getSeparator();
         this.row = 0;
         this.lastSampleRead = nextSample();
     }
 
     private SampleMetadata readMetadata(char separator, boolean useSaveSampleCfg) {
         try {
             SampleMetadata result;
             // Read first line
             String line = reader.readLine();
             if(line == null) {
                 throw new IllegalArgumentException("File is empty");
             }
             // When we can use sample save config and there is no header in csv
             // file
             if (useSaveSampleCfg
                     && CSVSaveService.getSampleSaveConfiguration(line,
                             file.getAbsolutePath()) == null) {
                 // Build metadata from default save config
                 LOG.warn("File '"+file.getAbsolutePath()+"' does not contain the field names header, "
                         + "ensure the jmeter.save.saveservice.* properties are the same as when the CSV file was created or the file may be read incorrectly");
                 System.err.println("File '"+file.getAbsolutePath()+"' does not contain the field names header, "
                         + "ensure the jmeter.save.saveservice.* properties are the same as when the CSV file was created or the file may be read incorrectly");
                 result = new SampleMetadata(
                         SampleSaveConfiguration.staticConfig());
 
             } else {
                 // Build metadata from headers
                 result = new SampleMetaDataParser(separator).parse(line);
             }
             return result;
         } catch (Exception e) {
             throw new SampleException("Could not read metadata !", e);
         }
     }
 
     /**
      * Gets the metadata.
      *
      * @return the metadata
      */
     public SampleMetadata getMetadata() {
         return metadata;
     }
 
     private Sample nextSample() {
         String[] data;
         try {
             data = CSVSaveService.csvReadFile(reader, separator);
             Sample sample = null;
             if (data.length > 0) {
                 if (data.length != columnCount) {
                     throw new SampleException("Mismatch between expected number of columns:"+columnCount+" and columns in CSV file:"+data.length+
                             ", check your jmeter.save.saveservice.* configuration");
                 }
                 sample = new Sample(row, metadata, data);
             }
             return sample;
         } catch (IOException e) {
             throw new SampleException("Could not read sample <" + row + ">", e);
         }
     }
 
     /**
      * Gets next sample from the file.
      *
      * @return the sample
      */
     public Sample readSample() {
         Sample out = lastSampleRead;
         lastSampleRead = nextSample();
         return out;
     }
 
     /**
      * Gets next sample from file but keep the reading file position.
      *
      * @return the sample
      */
     public Sample peek() {
         return lastSampleRead;
     }
 
     /**
      * Indicates whether the file contains more samples
      *
      * @return true, if the file contains more samples
      */
     public boolean hasNext() {
         return lastSampleRead != null;
     }
 
     /**
      * Close the reader.
      */
     @Override
     public void close() {
         JOrphanUtils.closeQuietly(reader);
     }
 }
diff --git a/src/core/org/apache/jmeter/report/dashboard/ReportGenerator.java b/src/core/org/apache/jmeter/report/dashboard/ReportGenerator.java
index 9cb56f147..846b20d70 100644
--- a/src/core/org/apache/jmeter/report/dashboard/ReportGenerator.java
+++ b/src/core/org/apache/jmeter/report/dashboard/ReportGenerator.java
@@ -1,570 +1,571 @@
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
 package org.apache.jmeter.report.dashboard;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.Map;
 import java.util.Properties;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.apache.commons.io.FileUtils;
 import org.apache.jmeter.report.config.ConfigurationException;
 import org.apache.jmeter.report.config.ExporterConfiguration;
 import org.apache.jmeter.report.config.GraphConfiguration;
 import org.apache.jmeter.report.config.ReportGeneratorConfiguration;
 import org.apache.jmeter.report.core.ControllerSamplePredicate;
 import org.apache.jmeter.report.core.ConvertException;
 import org.apache.jmeter.report.core.Converters;
 import org.apache.jmeter.report.core.Sample;
 import org.apache.jmeter.report.core.SampleException;
 import org.apache.jmeter.report.core.SamplePredicate;
 import org.apache.jmeter.report.core.SampleSelector;
 import org.apache.jmeter.report.core.StringConverter;
 import org.apache.jmeter.report.processor.AbstractSampleConsumer;
 import org.apache.jmeter.report.processor.AggregateConsumer;
 import org.apache.jmeter.report.processor.ApdexSummaryConsumer;
 import org.apache.jmeter.report.processor.ApdexThresholdsInfo;
 import org.apache.jmeter.report.processor.CsvFileSampleSource;
 import org.apache.jmeter.report.processor.ErrorsSummaryConsumer;
 import org.apache.jmeter.report.processor.FilterConsumer;
 import org.apache.jmeter.report.processor.MaxAggregator;
 import org.apache.jmeter.report.processor.MinAggregator;
 import org.apache.jmeter.report.processor.NormalizerSampleConsumer;
 import org.apache.jmeter.report.processor.RequestsSummaryConsumer;
 import org.apache.jmeter.report.processor.SampleContext;
 import org.apache.jmeter.report.processor.SampleSource;
 import org.apache.jmeter.report.processor.StatisticsSummaryConsumer;
 import org.apache.jmeter.report.processor.ThresholdSelector;
 import org.apache.jmeter.report.processor.graph.AbstractGraphConsumer;
 import org.apache.jmeter.reporters.ResultCollector;
+import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * The class ReportGenerator provides a way to generate all the templated files
  * of the plugin.
  * 
  * @since 3.0
  */
 public class ReportGenerator {
     private static final String REPORTGENERATOR_PROPERTIES = "reportgenerator.properties";
 
     private static final Logger LOG = LoggingManager.getLoggerForClass();
 
     private static final boolean CSV_OUTPUT_FORMAT = "csv"
             .equalsIgnoreCase(JMeterUtils.getPropDefault(
                     "jmeter.save.saveservice.output_format", "csv"));
 
     private static final char CSV_DEFAULT_SEPARATOR =
-            JMeterUtils.getPropDefault("jmeter.save.saveservice.default_delimiter", ",").charAt(0); //$NON-NLS-1$ //$NON-NLS-2$
+            // We cannot use JMeterUtils#getPropDefault as it applies a trim on value
+            JMeterUtils.getDelimiter(
+                    JMeterUtils.getJMeterProperties().getProperty(SampleSaveConfiguration.DEFAULT_DELIMITER_PROP, SampleSaveConfiguration.DEFAULT_DELIMITER)).charAt(0);
 
     private static final String INVALID_CLASS_FMT = "Class name \"%s\" is not valid.";
     private static final String INVALID_EXPORT_FMT = "Data exporter \"%s\" is unable to export data.";
     private static final String NOT_SUPPORTED_CONVERTION_FMT = "Not supported conversion to \"%s\"";
 
     public static final String NORMALIZER_CONSUMER_NAME = "normalizer";
     public static final String BEGIN_DATE_CONSUMER_NAME = "beginDate";
     public static final String END_DATE_CONSUMER_NAME = "endDate";
     public static final String NAME_FILTER_CONSUMER_NAME = "nameFilter";
     public static final String DATE_RANGE_FILTER_CONSUMER_NAME = "dateRangeFilter";
     public static final String APDEX_SUMMARY_CONSUMER_NAME = "apdexSummary";
     public static final String ERRORS_SUMMARY_CONSUMER_NAME = "errorsSummary";
     public static final String REQUESTS_SUMMARY_CONSUMER_NAME = "requestsSummary";
     public static final String STATISTICS_SUMMARY_CONSUMER_NAME = "statisticsSummary";
     public static final String START_INTERVAL_CONTROLLER_FILTER_CONSUMER_NAME = "startIntervalControlerFilter";
 
     private static final Pattern POTENTIAL_CAMEL_CASE_PATTERN = Pattern.compile("_(.)");
 
     private final File testFile;
     private final ReportGeneratorConfiguration configuration;
 
     /**
      * ResultCollector used
      */
     private final ResultCollector resultCollector;
 
     /**
      * Instantiates a new report generator.
      *
      * @param resultsFile
      *            the test results file
      * @param resultCollector
      *            Can be null, used if generation occurs at end of test
      * @throws ConfigurationException when loading configuration from file fails
      */
     public ReportGenerator(String resultsFile, ResultCollector resultCollector)
             throws ConfigurationException {
         if (!CSV_OUTPUT_FORMAT) {
             throw new IllegalArgumentException(
                     "Report generation requires csv output format, check 'jmeter.save.saveservice.output_format' property");
         }
 
         LOG.info("ReportGenerator will use for Parsing the separator:'"+CSV_DEFAULT_SEPARATOR+"'");
 
         File file = new File(resultsFile);
         if (resultCollector == null) {
             if (!(file.isFile() && file.canRead())) {
                 throw new IllegalArgumentException(String.format(
                         "Cannot read test results file : %s", file));
             }
             LOG.info("Will only generate report from results file:"
                     + resultsFile);
         } else {
             if (file.exists() && file.length() > 0) {
                 throw new IllegalArgumentException("Results file:"
                         + resultsFile + " is not empty");
             }
             LOG.info("Will generate report at end of test from  results file:"
                     + resultsFile);
         }
         this.resultCollector = resultCollector;
         this.testFile = file;
         final Properties merged = new Properties();
         File rgp = new File(JMeterUtils.getJMeterBinDir(), REPORTGENERATOR_PROPERTIES);
         if(LOG.isInfoEnabled()) {
             LOG.info("Reading report generator properties from:"+rgp.getAbsolutePath());
         }
         merged.putAll(loadProps(rgp));
         if(LOG.isInfoEnabled()) {
             LOG.info("Merging with JMeter properties");
         }
         merged.putAll(JMeterUtils.getJMeterProperties());
         configuration = ReportGeneratorConfiguration.loadFromProperties(merged);
     }
 
     private static Properties loadProps(File file) {
         final Properties props = new Properties();
         try (FileInputStream inStream = new FileInputStream(file)) {
             props.load(inStream);
         } catch (IOException e) {
             LOG.error("Problem loading properties from file ", e);
             System.err.println("Problem loading properties " + e);
         }
         return props;
     }
 
     /**
      * <p>
      * Gets the name of property setter from the specified key.
      * </p>
      * <p>
      * E.g : with key set_granularity, returns setGranularity (camel case)
      * </p>
      * 
      * @param propertyKey
      *            the property key
      * @return the name of the property setter
      */
     private static String getSetterName(String propertyKey) {
         Matcher matcher = POTENTIAL_CAMEL_CASE_PATTERN.matcher(propertyKey);
         StringBuffer buffer = new StringBuffer(); // Unfortunately Matcher does not support StringBuilder
         while (matcher.find()) {
             matcher.appendReplacement(buffer, matcher.group(1).toUpperCase());
         }
         matcher.appendTail(buffer);
         return buffer.toString();
     }
 
     /**
      * Generate dashboard reports using the data from the specified CSV File.
      *
      * @throws GenerationException
      *             when the generation failed
      */
     public void generate() throws GenerationException {
 
         if (resultCollector != null) {
             LOG.info("Flushing result collector before report Generation");
             resultCollector.flushFile();
         }
         LOG.debug("Start report generation");
 
         File tmpDir = configuration.getTempDirectory();
         boolean tmpDirCreated = createTempDir(tmpDir);
 
         // Build consumers chain
         SampleContext sampleContext = new SampleContext();
         sampleContext.setWorkingDirectory(tmpDir);
-        SampleSource source = new CsvFileSampleSource(testFile, JMeterUtils
-                .getPropDefault("jmeter.save.saveservice.default_delimiter",
-                        ",").charAt(0));
+        SampleSource source = new CsvFileSampleSource(testFile, CSV_DEFAULT_SEPARATOR);
         source.setSampleContext(sampleContext);
 
         NormalizerSampleConsumer normalizer = new NormalizerSampleConsumer();
         normalizer.setName(NORMALIZER_CONSUMER_NAME);
         
         FilterConsumer dateRangeConsumer = createFilterByDateRange();
         dateRangeConsumer.addSampleConsumer(createBeginDateConsumer());
         dateRangeConsumer.addSampleConsumer(createEndDateConsumer());
 
         FilterConsumer nameFilter = createNameFilter();
 
         FilterConsumer excludeControllerFilter = createExcludeControllerFilter();
 
         nameFilter.addSampleConsumer(excludeControllerFilter);
 
         dateRangeConsumer.addSampleConsumer(nameFilter);
         
         normalizer.addSampleConsumer(dateRangeConsumer);
         
         source.addSampleConsumer(normalizer);
 
         // Get graph configurations
         Map<String, GraphConfiguration> graphConfigurations = configuration
                 .getGraphConfigurations();
 
         // Process configuration to build graph consumers
         for (Map.Entry<String, GraphConfiguration> entryGraphCfg : graphConfigurations
                 .entrySet()) {
             addGraphConsumer(nameFilter, excludeControllerFilter,
                     entryGraphCfg);
         }
 
         // Generate data
         LOG.debug("Start samples processing");
         try {
             source.run();
         } catch (SampleException ex) {
             throw new GenerationException("Error while processing samples:"+ex.getMessage(), ex);
         }
         LOG.debug("End of samples processing");
 
         LOG.debug("Start data exporting");
 
         // Process configuration to build data exporters
         for (Map.Entry<String, ExporterConfiguration> entry : configuration
                 .getExportConfigurations().entrySet()) {
             LOG.info("Exporting data using exporter:'"
                 +entry.getKey()+"' of className:'"+entry.getValue().getClassName()+"'");
             exportData(sampleContext, entry.getKey(), entry.getValue());
         }
 
         LOG.debug("End of data exporting");
 
         removeTempDir(tmpDir, tmpDirCreated);
 
         LOG.debug("End of report generation");
 
     }
 
     /**
      * @return {@link FilterConsumer} that filter data based on date range
      */
     private FilterConsumer createFilterByDateRange() {
         FilterConsumer dateRangeFilter = new FilterConsumer();
         dateRangeFilter.setName(DATE_RANGE_FILTER_CONSUMER_NAME);
         dateRangeFilter.setSamplePredicate(new SamplePredicate() {
 
             @Override
             public boolean matches(Sample sample) {
                 long sampleStartTime = sample.getStartTime();
                 if(configuration.getStartDate() != null) {
                     if((sampleStartTime >= configuration.getStartDate().getTime())) {
                         if(configuration.getEndDate() != null) {
                             return sampleStartTime <= configuration.getEndDate().getTime();                             
                         } else {
                             return true;                            
                         }
                     }
                     return false;
                 } else {
                     if(configuration.getEndDate() != null) {
                         return sampleStartTime <= configuration.getEndDate().getTime(); 
                     } else {
                         return true;                            
                     }
                 }
             }
         });     
         return dateRangeFilter;
     }
 
     private void removeTempDir(File tmpDir, boolean tmpDirCreated) {
         if (tmpDirCreated) {
             try {
                 FileUtils.deleteDirectory(tmpDir);
             } catch (IOException ex) {
                 LOG.warn(String.format(
                         "Cannot delete created temporary directory \"%s\".",
                         tmpDir), ex);
             }
         }
     }
 
     private boolean createTempDir(File tmpDir) throws GenerationException {
         boolean tmpDirCreated = false;
         if (!tmpDir.exists()) {
             tmpDirCreated = tmpDir.mkdir();
             if (!tmpDirCreated) {
                 String message = String.format(
                         "Cannot create temporary directory \"%s\".", tmpDir);
                 LOG.error(message);
                 throw new GenerationException(message);
             }
         }
         return tmpDirCreated;
     }
 
     private void addGraphConsumer(FilterConsumer nameFilter,
             FilterConsumer excludeControllerFilter,
             Map.Entry<String, GraphConfiguration> entryGraphCfg)
             throws GenerationException {
         String graphName = entryGraphCfg.getKey();
         GraphConfiguration graphConfiguration = entryGraphCfg.getValue();
 
         // Instantiate the class from the classname
         String className = graphConfiguration.getClassName();
         try {
             Class<?> clazz = Class.forName(className);
             Object obj = clazz.newInstance();
             AbstractGraphConsumer graph = (AbstractGraphConsumer) obj;
             graph.setName(graphName);
             
             // Set the graph title
             graph.setTitle(graphConfiguration.getTitle());
 
             // Set graph properties using reflection
             Method[] methods = clazz.getMethods();
             for (Map.Entry<String, String> entryProperty : graphConfiguration
                     .getProperties().entrySet()) {
                 String propertyName = entryProperty.getKey();
                 String propertyValue = entryProperty.getValue();
                 String setterName = getSetterName(propertyName);
 
                 setProperty(className, obj, methods, propertyName,
                         propertyValue, setterName);
             }
 
             // Choose which entry point to use to plug the graph
             AbstractSampleConsumer entryPoint = graphConfiguration
                     .excludesControllers() ? excludeControllerFilter
                     : nameFilter;
             entryPoint.addSampleConsumer(graph);
         } catch (ClassNotFoundException | IllegalAccessException
                 | InstantiationException | ClassCastException ex) {
             String error = String.format(INVALID_CLASS_FMT, className);
             LOG.error(error, ex);
             throw new GenerationException(error, ex);
         }
     }
 
     private void exportData(SampleContext sampleContext, String exporterName,
             ExporterConfiguration exporterConfiguration)
             throws GenerationException {
         // Instantiate the class from the classname
         String className = exporterConfiguration.getClassName();
         try {
             Class<?> clazz = Class.forName(className);
             Object obj = clazz.newInstance();
             DataExporter exporter = (DataExporter) obj;
             exporter.setName(exporterName);
 
             // Export data
             exporter.export(sampleContext, testFile, configuration);
         } catch (ClassNotFoundException | IllegalAccessException
                 | InstantiationException | ClassCastException ex) {
             String error = String.format(INVALID_CLASS_FMT, className);
             LOG.error(error, ex);
             throw new GenerationException(error, ex);
         } catch (ExportException ex) {
             String error = String.format(INVALID_EXPORT_FMT, exporterName);
             LOG.error(error, ex);
             throw new GenerationException(error, ex);
         }
     }
 
     private ErrorsSummaryConsumer createErrorsSummaryConsumer() {
         ErrorsSummaryConsumer errorsSummaryConsumer = new ErrorsSummaryConsumer();
         errorsSummaryConsumer.setName(ERRORS_SUMMARY_CONSUMER_NAME);
         return errorsSummaryConsumer;
     }
 
     private FilterConsumer createExcludeControllerFilter() {
         FilterConsumer excludeControllerFilter = new FilterConsumer();
         excludeControllerFilter
                 .setName(START_INTERVAL_CONTROLLER_FILTER_CONSUMER_NAME);
         excludeControllerFilter
                 .setSamplePredicate(new ControllerSamplePredicate());
         excludeControllerFilter.setReverseFilter(true);
         excludeControllerFilter.addSampleConsumer(createErrorsSummaryConsumer());
         return excludeControllerFilter;
     }
 
     private StatisticsSummaryConsumer createStatisticsSummaryConsumer() {
         StatisticsSummaryConsumer statisticsSummaryConsumer = new StatisticsSummaryConsumer();
         statisticsSummaryConsumer.setName(STATISTICS_SUMMARY_CONSUMER_NAME);
         statisticsSummaryConsumer.setHasOverallResult(true);
         return statisticsSummaryConsumer;
     }
 
     private RequestsSummaryConsumer createRequestsSummaryConsumer() {
         RequestsSummaryConsumer requestsSummaryConsumer = new RequestsSummaryConsumer();
         requestsSummaryConsumer.setName(REQUESTS_SUMMARY_CONSUMER_NAME);
         return requestsSummaryConsumer;
     }
 
     private ApdexSummaryConsumer createApdexSummaryConsumer() {
         ApdexSummaryConsumer apdexSummaryConsumer = new ApdexSummaryConsumer();
         apdexSummaryConsumer.setName(APDEX_SUMMARY_CONSUMER_NAME);
         apdexSummaryConsumer.setHasOverallResult(true);
         apdexSummaryConsumer.setThresholdSelector(new ThresholdSelector() {
 
             @Override
             public ApdexThresholdsInfo select(String sampleName) {
                 ApdexThresholdsInfo info = new ApdexThresholdsInfo();
                 info.setSatisfiedThreshold(configuration
                         .getApdexSatisfiedThreshold());
                 info.setToleratedThreshold(configuration
                         .getApdexToleratedThreshold());
                 return info;
             }
         });
         return apdexSummaryConsumer;
     }
 
     /**
      * @return a {@link FilterConsumer} that filters samplers based on their name
      */
     private FilterConsumer createNameFilter() {
         FilterConsumer nameFilter = new FilterConsumer();
         nameFilter.setName(NAME_FILTER_CONSUMER_NAME);
         nameFilter.setSamplePredicate(new SamplePredicate() {
 
             @Override
             public boolean matches(Sample sample) {
                 // Get filtered samples from configuration
                 Pattern filteredSamplesPattern = configuration
                         .getFilteredSamplesPattern();
                 // Sample is kept if no filter is set 
                 // or if its name matches the filter pattern
                 return filteredSamplesPattern == null 
                         || filteredSamplesPattern.matcher(sample.getName()).matches();
             }
         });
         nameFilter.addSampleConsumer(createApdexSummaryConsumer());
         nameFilter.addSampleConsumer(createRequestsSummaryConsumer());
         nameFilter.addSampleConsumer(createStatisticsSummaryConsumer());
         return nameFilter;
     }
 
     /**
      * @return Consumer that compute the end date of the test
      */
     private AggregateConsumer createEndDateConsumer() {
         AggregateConsumer endDateConsumer = new AggregateConsumer(
                 new MaxAggregator(), new SampleSelector<Double>() {
 
                     @Override
                     public Double select(Sample sample) {
                         return Double.valueOf(sample.getEndTime());
                     }
                 });
         endDateConsumer.setName(END_DATE_CONSUMER_NAME);
         return endDateConsumer;
     }
 
     /**
      * @return Consumer that compute the begining date of the test
      */
     private AggregateConsumer createBeginDateConsumer() {
         AggregateConsumer beginDateConsumer = new AggregateConsumer(
                 new MinAggregator(), new SampleSelector<Double>() {
 
                     @Override
                     public Double select(Sample sample) {
                         return Double.valueOf(sample.getStartTime());
                     }
                 });
         beginDateConsumer.setName(BEGIN_DATE_CONSUMER_NAME);
         return beginDateConsumer;
     }
 
     /**
      * Try to set a property on an object by reflection.
      *
      * @param className
      *            name of the objects class
      * @param obj
      *            the object on which the property should be set
      * @param methods
      *            methods of the object which will be search for the property
      *            setter
      * @param propertyName
      *            name of the property to be set
      * @param propertyValue
      *            value to be set
      * @param setterName
      *            name of the property setter that should be used to set the
      *            property
      * @throws IllegalAccessException
      *             if reflection throws an IllegalAccessException
      * @throws GenerationException
      *             if conversion of the property value fails or reflection
      *             throws an InvocationTargetException
      */
     private void setProperty(String className, Object obj, Method[] methods,
             String propertyName, String propertyValue, String setterName)
             throws IllegalAccessException, GenerationException {
         try {
             int i = 0;
             while (i < methods.length) {
                 Method method = methods[i];
                 if (method.getName().equals(setterName)) {
                     Class<?>[] parameterTypes = method
                             .getParameterTypes();
                     if (parameterTypes.length == 1) {
                         Class<?> parameterType = parameterTypes[0];
                         if (parameterType
                                 .isAssignableFrom(String.class)) {
                             method.invoke(obj, propertyValue);
                         } else {
                             StringConverter<?> converter = Converters
                                     .getConverter(parameterType);
                             if (converter == null) {
                                 throw new GenerationException(
                                         String.format(
                                                 NOT_SUPPORTED_CONVERTION_FMT,
                                                 parameterType
                                                         .getName()));
                             }
                             method.invoke(obj, converter
                                     .convert(propertyValue));
                         }
                         return;
                     }
                 }
                 i++;
             }
             LOG.warn(String
                         .format("\"%s\" is not a valid property for class \"%s\", skip it",
                                 propertyName, className));
         } catch (InvocationTargetException | ConvertException ex) {
             String message = String
                     .format("Cannot assign \"%s\" to property \"%s\" (mapped as \"%s\"), skip it",
                             propertyValue, propertyName, setterName);
             LOG.error(message, ex);
             throw new GenerationException(message, ex);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
index 8af24fcb8..a7118694c 100644
--- a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
+++ b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
@@ -1,923 +1,916 @@
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
 import java.util.Properties;
 
 import org.apache.commons.lang3.CharUtils;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.log.Logger;
 
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
  *
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
     private static final long serialVersionUID = 7L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
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
-    private static final String DEFAULT_DELIMITER_PROP = "jmeter.save.saveservice.default_delimiter"; // $NON_NLS-1$
+    public static final String DEFAULT_DELIMITER_PROP = "jmeter.save.saveservice.default_delimiter"; // $NON_NLS-1$
 
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
     // N.B. Remember to update the equals and hashCode methods when adding new variables.
 
     // Initialise values from properties
     private boolean time = _time, latency = _latency, connectTime=_connectTime, timestamp = _timestamp, success = _success, label = _label,
             code = _code, message = _message, threadName = _threadName, dataType = _dataType, encoding = _encoding,
             assertions = _assertions, subresults = _subresults, responseData = _responseData,
             samplerData = _samplerData, xml = _xml, fieldNames = _fieldNames, responseHeaders = _responseHeaders,
             requestHeaders = _requestHeaders, responseDataOnError = _responseDataOnError;
 
     private boolean saveAssertionResultsFailureMessage = _saveAssertionResultsFailureMessage;
 
     private boolean url = _url, bytes = _bytes , fileName = _fileName;
 
     private boolean hostname = _hostname;
 
     private boolean threadCounts = _threadCounts;
 
     private boolean sampleCount = _sampleCount;
 
     private boolean idleTime = _idleTime;
 
     // Does not appear to be used (yet)
     private int assertionsResultsToSave = _assertionsResultsToSave;
 
 
     // Don't save this, as it is derived from the time format
     private boolean printMilliseconds = _printMilliseconds;
 
     /** A formatter for the time stamp. */
     private transient DateFormat formatter = _formatter;
     /* Make transient as we don't want to save the SimpleDataFormat class
      * Also, there's currently no way to change the value via the GUI, so changing it
      * later means editting the JMX, or recreating the Listener.
      */
 
     // Defaults from properties:
     private static final boolean _time, _timestamp, _success, _label, _code, _message, _threadName, _xml,
             _responseData, _dataType, _encoding, _assertions, _latency, _connectTime, _subresults, _samplerData, _fieldNames,
             _responseHeaders, _requestHeaders;
 
     private static final boolean _responseDataOnError;
 
     private static final boolean _saveAssertionResultsFailureMessage;
 
     private static final String _timeStampFormat;
 
     private static final int _assertionsResultsToSave;
 
     // TODO turn into method?
     public static final int SAVE_NO_ASSERTIONS = 0;
 
     public static final int SAVE_FIRST_ASSERTION = SAVE_NO_ASSERTIONS + 1;
 
     public static final int SAVE_ALL_ASSERTIONS = SAVE_FIRST_ASSERTION + 1;
 
     private static final boolean _printMilliseconds;
 
     private static final boolean _bytes;
 
     private static final boolean _url;
 
     private static final boolean _fileName;
 
     private static final boolean _hostname;
 
     private static final boolean _threadCounts;
 
     private static final boolean _sampleCount;
 
     private static final DateFormat _formatter;
 
     /**
      * The string used to separate fields when stored to disk, for example, the
      * comma for CSV files.
      */
     private static final String _delimiter;
 
     private static final boolean _idleTime;
 
-    private static final String DEFAULT_DELIMITER = ","; // $NON_NLS-1$
+    public static final String DEFAULT_DELIMITER = ","; // $NON_NLS-1$
 
     /**
      * Read in the properties having to do with saving from a properties file.
      */
     static {
         Properties props = JMeterUtils.getJMeterProperties();
 
         _subresults      = TRUE.equalsIgnoreCase(props.getProperty(SUBRESULTS_PROP, TRUE));
         _assertions      = TRUE.equalsIgnoreCase(props.getProperty(ASSERTIONS_PROP, TRUE));
         _latency         = TRUE.equalsIgnoreCase(props.getProperty(LATENCY_PROP, TRUE));
         _connectTime     = TRUE.equalsIgnoreCase(props.getProperty(CONNECT_TIME_PROP, FALSE));
         _samplerData     = TRUE.equalsIgnoreCase(props.getProperty(SAMPLERDATA_PROP, FALSE));
         _responseHeaders = TRUE.equalsIgnoreCase(props.getProperty(RESPONSEHEADERS_PROP, FALSE));
         _requestHeaders  = TRUE.equalsIgnoreCase(props.getProperty(REQUESTHEADERS_PROP, FALSE));
         _encoding        = TRUE.equalsIgnoreCase(props.getProperty(ENCODING_PROP, FALSE));
 
-        String dlm = props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER);
-        if (dlm.equals("\\t")) {// Make it easier to enter a tab (can use \<tab> but that is awkward)
-            dlm="\t";
-        }
-
-        if (dlm.length() != 1){
-            throw new JMeterError("Delimiter '"+dlm+"' must be of length 1.");
-        }
+        String dlm = JMeterUtils.getDelimiter(props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER));
         char ch = dlm.charAt(0);
 
         if (CharUtils.isAsciiAlphanumeric(ch) || ch == CSVSaveService.QUOTING_CHAR){
             throw new JMeterError("Delimiter '"+ch+"' must not be alphanumeric or "+CSVSaveService.QUOTING_CHAR+".");
         }
 
         if (ch != '\t' && !CharUtils.isAsciiPrintable(ch)){
             throw new JMeterError("Delimiter (code "+(int)ch+") must be printable.");
         }
 
         _delimiter = dlm;
 
         _fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, TRUE));
 
         _dataType = TRUE.equalsIgnoreCase(props.getProperty(SAVE_DATA_TYPE_PROP, TRUE));
 
         _label = TRUE.equalsIgnoreCase(props.getProperty(SAVE_LABEL_PROP, TRUE));
 
         _code = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_CODE_PROP, TRUE));
 
         _responseData = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_PROP, FALSE));
 
         _responseDataOnError = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_ON_ERROR_PROP, FALSE));
 
         _message = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_MESSAGE_PROP, TRUE));
 
         _success = TRUE.equalsIgnoreCase(props.getProperty(SAVE_SUCCESSFUL_PROP, TRUE));
 
         _threadName = TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_NAME_PROP, TRUE));
 
         _bytes = TRUE.equalsIgnoreCase(props.getProperty(SAVE_BYTES_PROP, TRUE));
 
         _url = TRUE.equalsIgnoreCase(props.getProperty(SAVE_URL_PROP, FALSE));
 
         _fileName = TRUE.equalsIgnoreCase(props.getProperty(SAVE_FILENAME_PROP, FALSE));
 
         _hostname = TRUE.equalsIgnoreCase(props.getProperty(SAVE_HOSTNAME_PROP, FALSE));
 
         _time = TRUE.equalsIgnoreCase(props.getProperty(SAVE_TIME_PROP, TRUE));
 
         _timeStampFormat = props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);
 
         _printMilliseconds = MILLISECONDS.equalsIgnoreCase(_timeStampFormat);
 
         // Prepare for a pretty date
         // FIXME Can _timeStampFormat be null ? it does not appear to me .
         if (!_printMilliseconds && !NONE.equalsIgnoreCase(_timeStampFormat) && (_timeStampFormat != null)) {
             _formatter = new SimpleDateFormat(_timeStampFormat);
         } else {
             _formatter = null;
         }
 
         _timestamp = !NONE.equalsIgnoreCase(_timeStampFormat);// reversed compare allows for null
 
         _saveAssertionResultsFailureMessage = TRUE.equalsIgnoreCase(props.getProperty(
                 ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, TRUE));
 
         String whichAssertionResults = props.getProperty(ASSERTION_RESULTS_PROP, NONE);
         if (NONE.equals(whichAssertionResults)) {
             _assertionsResultsToSave = SAVE_NO_ASSERTIONS;
         } else if (FIRST.equals(whichAssertionResults)) {
             _assertionsResultsToSave = SAVE_FIRST_ASSERTION;
         } else if (ALL.equals(whichAssertionResults)) {
             _assertionsResultsToSave = SAVE_ALL_ASSERTIONS;
         } else {
             _assertionsResultsToSave = 0;
         }
 
         String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, CSV);
 
         if (XML.equals(howToSave)) {
             _xml = true;
         } else {
             if (!CSV.equals(howToSave)) {
                 log.warn(OUTPUT_FORMAT_PROP + " has unexepected value: '" + howToSave + "' - assuming 'csv' format");
             }
             _xml = false;
         }
 
         _threadCounts=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, TRUE));
 
         _sampleCount=TRUE.equalsIgnoreCase(props.getProperty(SAVE_SAMPLE_COUNT, FALSE));
 
         _idleTime=TRUE.equalsIgnoreCase(props.getProperty(SAVE_IDLE_TIME, TRUE));
     }
 
     // Don't save this, as not settable via GUI
     private String delimiter = _delimiter;
 
     // Don't save this - only needed for processing CSV headers currently
     private transient int varCount = 0;
 
     private static final SampleSaveConfiguration _static = new SampleSaveConfiguration();
 
     public int getVarCount() { // Only for use by CSVSaveService
         return varCount;
     }
 
     public void setVarCount(int varCount) { // Only for use by CSVSaveService
         this.varCount = varCount;
     }
 
     // Give access to initial configuration
     public static SampleSaveConfiguration staticConfig() {
         return _static;
     }
 
     // for test code only
     static final String CONFIG_GETTER_PREFIX = "save";  // $NON-NLS-1$
 
     /**
      * Convert a config name to the method name of the getter.
      * The getter method returns a boolean.
      * @param configName
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
      * @param configName
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
         printMilliseconds = _printMilliseconds;//is derived from properties only
         requestHeaders = value;
         responseData = value;
         responseDataOnError = value;
         responseHeaders = value;
         sampleCount = value;
         samplerData = value;
         saveAssertionResultsFailureMessage = value;
         subresults = value;
         success = value;
         threadCounts = value;
         threadName = value;
         time = value;
         timestamp = value;
         url = value;
         xml = value;
     }
 
     private Object readResolve(){
        formatter = _formatter;
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
             s.fileName == fileName &&
             s.hostname == hostname &&
             s.sampleCount == sampleCount &&
             s.idleTime == idleTime &&
             s.threadCounts == threadCounts;
 
         boolean stringValues = false;
         if(primitiveValues) {
             stringValues = s.delimiter == delimiter || (delimiter != null && delimiter.equals(s.delimiter));
         }
         boolean complexValues = false;
         if(primitiveValues && stringValues) {
             complexValues = s.formatter == formatter || (formatter != null && formatter.equals(s.formatter));
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
         printMilliseconds = (fmt == null); // maintain relationship
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
         delimiter=_delimiter;
     }
 
     // Used by SampleSaveConfigurationConverter.unmarshall()
     public void setDefaultTimeStampFormat() {
         printMilliseconds=_printMilliseconds;
         formatter=_formatter;
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
diff --git a/src/core/org/apache/jmeter/util/JMeterUtils.java b/src/core/org/apache/jmeter/util/JMeterUtils.java
index 12f7a401e..50ca5a156 100644
--- a/src/core/org/apache/jmeter/util/JMeterUtils.java
+++ b/src/core/org/apache/jmeter/util/JMeterUtils.java
@@ -1,1412 +1,1430 @@
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
 
 package org.apache.jmeter.util;
 
 import java.awt.Dimension;
 import java.awt.HeadlessException;
 import java.awt.event.ActionListener;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.lang.reflect.InvocationTargetException;
 import java.net.InetAddress;
 import java.net.URL;
 import java.net.UnknownHostException;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.MissingResourceException;
 import java.util.Properties;
 import java.util.ResourceBundle;
 import java.util.Vector;
 import java.util.concurrent.ThreadLocalRandom;
 
 import javax.swing.ImageIcon;
 import javax.swing.JButton;
 import javax.swing.JComboBox;
 import javax.swing.JOptionPane;
 import javax.swing.JTable;
 import javax.swing.SwingUtilities;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.test.UnitTestManager;
+import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.PatternCacheLRU;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 import org.xml.sax.XMLReader;
 
 /**
  * This class contains the static utility methods used by JMeter.
  *
  */
 public class JMeterUtils implements UnitTestManager {
     private static final Logger log = LoggingManager.getLoggerForClass();
     
     // Note: cannot use a static variable here, because that would be processed before the JMeter properties
     // have been defined (Bug 52783)
     private static class LazyPatternCacheHolder {
         public static final PatternCacheLRU INSTANCE = new PatternCacheLRU(
                 getPropDefault("oro.patterncache.size",1000), // $NON-NLS-1$
                 new Perl5Compiler());
     }
 
     private static final String EXPERT_MODE_PROPERTY = "jmeter.expertMode"; // $NON-NLS-1$
     
     private static final String ENGLISH_LANGUAGE = Locale.ENGLISH.getLanguage();
 
     private static volatile Properties appProperties;
 
     private static final Vector<LocaleChangeListener> localeChangeListeners = new Vector<>();
 
     private static volatile Locale locale;
 
     private static volatile ResourceBundle resources;
 
     // What host am I running on?
 
     //@GuardedBy("this")
     private static String localHostIP = null;
     //@GuardedBy("this")
     private static String localHostName = null;
     //@GuardedBy("this")
     private static String localHostFullName = null;
 
     private static volatile boolean ignoreResorces = false; // Special flag for use in debugging resources
 
     private static final ThreadLocal<Perl5Matcher> localMatcher = new ThreadLocal<Perl5Matcher>() {
         @Override
         protected Perl5Matcher initialValue() {
             return new Perl5Matcher();
         }
     };
 
     /**
      * Gets Perl5Matcher for this thread.
      * @return the {@link Perl5Matcher} for this thread
      */
     public static Perl5Matcher getMatcher() {
         return localMatcher.get();
     }
 
     /**
      * This method is used by the init method to load the property file that may
      * even reside in the user space, or in the classpath under
      * org.apache.jmeter.jmeter.properties.
      *
      * The method also initialises logging and sets up the default Locale
      *
      * TODO - perhaps remove?
      * [still used
      *
      * @param file
      *            the file to load
      * @return the Properties from the file
      * @see #getJMeterProperties()
      * @see #loadJMeterProperties(String)
      * @see #initLogging()
      * @see #initLocale()
      */
     public static Properties getProperties(String file) {
         loadJMeterProperties(file);
         initLogging();
         initLocale();
         return appProperties;
     }
 
     /**
      * Initialise JMeter logging
      */
     public static void initLogging() {
         LoggingManager.initializeLogging(appProperties);
     }
 
     /**
      * Initialise the JMeter Locale
      */
     public static void initLocale() {
         String loc = appProperties.getProperty("language"); // $NON-NLS-1$
         if (loc != null) {
             String []parts = JOrphanUtils.split(loc,"_");// $NON-NLS-1$
             if (parts.length==2) {
                 setLocale(new Locale(parts[0], parts[1]));
             } else {
                 setLocale(new Locale(loc, "")); // $NON-NLS-1$
             }
 
         } else {
             setLocale(Locale.getDefault());
         }
     }
 
 
     /**
      * Load the JMeter properties file; if not found, then
      * default to "org/apache/jmeter/jmeter.properties" from the classpath
      *
      * <p>
      * c.f. loadProperties
      *
      * @param file Name of the file from which the JMeter properties should be loaded
      */
     public static void loadJMeterProperties(String file) {
         Properties p = new Properties(System.getProperties());
         InputStream is = null;
         try {
             File f = new File(file);
             is = new FileInputStream(f);
             p.load(is);
         } catch (IOException e) {
             try {
                 is =
                     ClassLoader.getSystemResourceAsStream("org/apache/jmeter/jmeter.properties"); // $NON-NLS-1$
                 if (is == null) {
                     throw new RuntimeException("Could not read JMeter properties file:"+file);
                 }
                 p.load(is);
             } catch (IOException ex) {
                 // JMeter.fail("Could not read internal resource. " +
                 // "Archive is broken.");
             }
         } finally {
             JOrphanUtils.closeQuietly(is);
         }
         appProperties = p;
     }
 
     /**
      * This method loads a property file that may reside in the user space, or
      * in the classpath
      *
      * @param file
      *            the file to load
      * @return the Properties from the file, may be null (e.g. file not found)
      */
     public static Properties loadProperties(String file) {
         return loadProperties(file, null);
     }
 
     /**
      * This method loads a property file that may reside in the user space, or
      * in the classpath
      *
      * @param file
      *            the file to load
      * @param defaultProps a set of default properties
      * @return the Properties from the file; if it could not be processed, the defaultProps are returned.
      */
     public static Properties loadProperties(String file, Properties defaultProps) {
         Properties p = new Properties(defaultProps);
         InputStream is = null;
         try {
             File f = new File(file);
             is = new FileInputStream(f);
             p.load(is);
         } catch (IOException e) {
             try {
                 final URL resource = JMeterUtils.class.getClassLoader().getResource(file);
                 if (resource == null) {
                     log.warn("Cannot find " + file);
                     return defaultProps;
                 }
                 is = resource.openStream();
                 if (is == null) {
                     log.warn("Cannot open " + file);
                     return defaultProps;
                 }
                 p.load(is);
             } catch (IOException ex) {
                 log.warn("Error reading " + file + " " + ex.toString());
                 return defaultProps;
             }
         } finally {
             JOrphanUtils.closeQuietly(is);
         }
         return p;
     }
 
     public static PatternCacheLRU getPatternCache() {
         return LazyPatternCacheHolder.INSTANCE;
     }
 
     /**
      * Get a compiled expression from the pattern cache (READ_ONLY).
      *
      * @param expression regular expression to be looked up
      * @return compiled pattern
      *
      * @throws MalformedCachePatternException (Runtime)
      * This should be caught for expressions that may vary (e.g. user input)
      *
      */
     public static Pattern getPattern(String expression) throws MalformedCachePatternException {
         return getPattern(expression, Perl5Compiler.READ_ONLY_MASK);
     }
 
     /**
      * Get a compiled expression from the pattern cache.
      *
      * @param expression RE
      * @param options e.g. {@link Perl5Compiler#READ_ONLY_MASK READ_ONLY_MASK}
      * @return compiled pattern
      *
      * @throws MalformedCachePatternException (Runtime)
      * This should be caught for expressions that may vary (e.g. user input)
      *
      */
     public static Pattern getPattern(String expression, int options) throws MalformedCachePatternException {
         return LazyPatternCacheHolder.INSTANCE.getPattern(expression, options);
     }
 
     @Override
     public void initializeProperties(String file) {
         System.out.println("Initializing Properties: " + file);
         getProperties(file);
     }
 
     /**
      * Convenience method for
      * {@link ClassFinder#findClassesThatExtend(String[], Class[], boolean)}
      * with the option to include inner classes in the search set to false
      * and the path list is derived from JMeterUtils.getSearchPaths().
      *
      * @param superClass - single class to search for
      * @return List of Strings containing discovered class names.
      * @throws IOException when the used {@link ClassFinder} throws one while searching for the class
      */
     public static List<String> findClassesThatExtend(Class<?> superClass)
         throws IOException {
         return ClassFinder.findClassesThatExtend(getSearchPaths(), new Class[]{superClass}, false);
     }
 
     /**
      * Generate a list of paths to search.
      * The output array always starts with
      * JMETER_HOME/lib/ext
      * and is followed by any paths obtained from the "search_paths" JMeter property.
      * 
      * @return array of path strings
      */
     public static String[] getSearchPaths() {
         String p = JMeterUtils.getPropDefault("search_paths", null); // $NON-NLS-1$
         String[] result = new String[1];
 
         if (p != null) {
             String[] paths = p.split(";"); // $NON-NLS-1$
             result = new String[paths.length + 1];
             System.arraycopy(paths, 0, result, 1, paths.length);
         }
         result[0] = getJMeterHome() + "/lib/ext"; // $NON-NLS-1$
         return result;
     }
 
     /**
      * Provide random numbers
      *
      * @param r -
      *            the upper bound (exclusive)
      * @return a random <code>int</code>
      */
     public static int getRandomInt(int r) {
         return ThreadLocalRandom.current().nextInt(r);
     }
 
     /**
      * Changes the current locale: re-reads resource strings and notifies
      * listeners.
      *
      * @param loc -
      *            new locale
      */
     public static void setLocale(Locale loc) {
         log.info("Setting Locale to " + loc.toString());
         /*
          * See bug 29920. getBundle() defaults to the property file for the
          * default Locale before it defaults to the base property file, so we
          * need to change the default Locale to ensure the base property file is
          * found.
          */
         Locale def = null;
         boolean isDefault = false; // Are we the default language?
         if (loc.getLanguage().equals(ENGLISH_LANGUAGE)) {
             isDefault = true;
             def = Locale.getDefault();
             // Don't change locale from en_GB to en
             if (!def.getLanguage().equals(ENGLISH_LANGUAGE)) {
                 Locale.setDefault(Locale.ENGLISH);
             } else {
                 def = null; // no need to reset Locale
             }
         }
         if (loc.toString().equals("ignoreResources")){ // $NON-NLS-1$
             log.warn("Resource bundles will be ignored");
             ignoreResorces = true;
             // Keep existing settings
         } else {
             ignoreResorces = false;
             ResourceBundle resBund = ResourceBundle.getBundle("org.apache.jmeter.resources.messages", loc); // $NON-NLS-1$
             resources = resBund;
             locale = loc;
             final Locale resBundLocale = resBund.getLocale();
             if (isDefault || resBundLocale.equals(loc)) {// language change worked
             // Check if we at least found the correct language:
             } else if (resBundLocale.getLanguage().equals(loc.getLanguage())) {
                 log.info("Could not find resources for '"+loc.toString()+"', using '"+resBundLocale.toString()+"'");
             } else {
                 log.error("Could not find resources for '"+loc.toString()+"'");
             }
         }
         notifyLocaleChangeListeners();
         /*
          * Reset Locale if necessary so other locales are properly handled
          */
         if (def != null) {
             Locale.setDefault(def);
         }
     }
 
     /**
      * Gets the current locale.
      *
      * @return current locale
      */
     public static Locale getLocale() {
         return locale;
     }
 
     public static void addLocaleChangeListener(LocaleChangeListener listener) {
         localeChangeListeners.add(listener);
     }
 
     public static void removeLocaleChangeListener(LocaleChangeListener listener) {
         localeChangeListeners.remove(listener);
     }
 
     /**
      * Notify all listeners interested in locale changes.
      *
      */
     private static void notifyLocaleChangeListeners() {
         LocaleChangeEvent event = new LocaleChangeEvent(JMeterUtils.class, locale);
         @SuppressWarnings("unchecked") // clone will produce correct type
         // TODO but why do we need to clone the list?
         // ANS: to avoid possible ConcurrentUpdateException when unsubscribing
         // Could perhaps avoid need to clone by using a modern concurrent list
         Vector<LocaleChangeListener> listeners = (Vector<LocaleChangeListener>) localeChangeListeners.clone();
         for (LocaleChangeListener listener : listeners) {
             listener.localeChanged(event);
         }
     }
 
     /**
      * Gets the resource string for this key.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @return the resource string if the key is found; otherwise, return
      *         "[res_key="+key+"]"
      */
     public static String getResString(String key) {
         return getResStringDefault(key, RES_KEY_PFX + key + "]"); // $NON-NLS-1$
     }
     
     /**
      * Gets the resource string for this key in Locale.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @param forcedLocale Force a particular locale
      * @return the resource string if the key is found; otherwise, return
      *         "[res_key="+key+"]"
      * @since 2.7
      */
     public static String getResString(String key, Locale forcedLocale) {
         return getResStringDefault(key, RES_KEY_PFX + key + "]", // $NON-NLS-1$
                 forcedLocale); 
     }
 
     public static final String RES_KEY_PFX = "[res_key="; // $NON-NLS-1$
 
     /**
      * Gets the resource string for this key.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @param defaultValue -
      *            the default value
      *
      * @return the resource string if the key is found; otherwise, return the
      *         default
      * @deprecated Only intended for use in development; use
      *             getResString(String) normally
      */
     @Deprecated
     public static String getResString(String key, String defaultValue) {
         return getResStringDefault(key, defaultValue);
     }
 
     /*
      * Helper method to do the actual work of fetching resources; allows
      * getResString(S,S) to be deprecated without affecting getResString(S);
      */
     private static String getResStringDefault(String key, String defaultValue) {
         return getResStringDefault(key, defaultValue, null);
     }
     /*
      * Helper method to do the actual work of fetching resources; allows
      * getResString(S,S) to be deprecated without affecting getResString(S);
      */
     private static String getResStringDefault(String key, String defaultValue, Locale forcedLocale) {
         if (key == null) {
             return null;
         }
         // Resource keys cannot contain spaces, and are forced to lower case
         String resKey = key.replace(' ', '_'); // $NON-NLS-1$ // $NON-NLS-2$
         resKey = resKey.toLowerCase(java.util.Locale.ENGLISH);
         String resString = null;
         try {
             ResourceBundle bundle = resources;
             if(forcedLocale != null) {
                 bundle = ResourceBundle.getBundle("org.apache.jmeter.resources.messages", forcedLocale); // $NON-NLS-1$
             }
             if (bundle.containsKey(resKey)) {
                 resString = bundle.getString(resKey);
             } else {
                 log.warn("ERROR! Resource string not found: [" + resKey + "]");
                 resString = defaultValue;                
             }
             if (ignoreResorces ){ // Special mode for debugging resource handling
                 return "["+key+"]";
             }
         } catch (MissingResourceException mre) {
             if (ignoreResorces ){ // Special mode for debugging resource handling
                 return "[?"+key+"?]";
             }
             log.warn("ERROR! Resource string not found: [" + resKey + "]", mre);
             resString = defaultValue;
         }
         return resString;
     }
 
     /**
      * To get I18N label from properties file
      * 
      * @param key
      *            in messages.properties
      * @return I18N label without (if exists) last colon ':' and spaces
      */
     public static String getParsedLabel(String key) {
         String value = JMeterUtils.getResString(key);
         return value.replaceFirst("(?m)\\s*?:\\s*$", ""); // $NON-NLS-1$ $NON-NLS-2$
     }
     
     /**
      * Get the locale name as a resource.
      * Does not log an error if the resource does not exist.
      * This is needed to support additional locales, as they won't be in existing messages files.
      *
      * @param locale name
      * @return the locale display name as defined in the current Locale or the original string if not present
      */
     public static String getLocaleString(String locale){
         // All keys in messages.properties are lowercase (historical reasons?)
         String resKey = locale.toLowerCase(java.util.Locale.ENGLISH);
         if (resources.containsKey(resKey)) {
             return resources.getString(resKey);
         }
         return locale;
     }
     /**
      * This gets the currently defined appProperties. It can only be called
      * after the {@link #getProperties(String)} or {@link #loadJMeterProperties(String)} 
      * method has been called.
      *
      * @return The JMeterProperties value, 
      *         may be null if {@link #loadJMeterProperties(String)} has not been called
      * @see #getProperties(String)
      * @see #loadJMeterProperties(String)
      */
     public static Properties getJMeterProperties() {
         return appProperties;
     }
 
     /**
      * This looks for the requested image in the classpath under
      * org.apache.jmeter.images.&lt;name&gt;
      *
      * @param name
      *            Description of Parameter
      * @return The Image value
      */
     public static ImageIcon getImage(String name) {
         try {
             URL url = JMeterUtils.class.getClassLoader().getResource(
                     "org/apache/jmeter/images/" + name.trim());
             if(url != null) {
                 return new ImageIcon(url); // $NON-NLS-1$
             } else {
                 log.warn("no icon for " + name);
                 return null;                
             }
         } catch (NoClassDefFoundError | InternalError e) {// Can be returned by headless hosts
             log.info("no icon for " + name + " " + e.getMessage());
             return null;
         }
     }
 
     /**
      * This looks for the requested image in the classpath under
      * org.apache.jmeter.images.<em>&lt;name&gt;</em>, and also sets the description
      * of the image, which is useful if the icon is going to be placed
      * on the clipboard.
      *
      * @param name
      *            the name of the image
      * @param description
      *            the description of the image
      * @return The Image value
      */
     public static ImageIcon getImage(String name, String description) {
         ImageIcon icon = getImage(name);
         if(icon != null) {
             icon.setDescription(description);
         }
         return icon;
     }
 
     public static String getResourceFileAsText(String name) {
         BufferedReader fileReader = null;
         try {
             String lineEnd = System.getProperty("line.separator"); // $NON-NLS-1$
             InputStream is = JMeterUtils.class.getClassLoader().getResourceAsStream(name);
             if(is != null) {
                 fileReader = new BufferedReader(new InputStreamReader(is));
                 StringBuilder text = new StringBuilder();
                 String line;
                 while ((line = fileReader.readLine()) != null) {
                     text.append(line);
                     text.append(lineEnd);
                 }
                 // Done by finally block: fileReader.close();
                 return text.toString();
             } else {
                 return ""; // $NON-NLS-1$                
             }
         } catch (IOException e) {
             return ""; // $NON-NLS-1$
         } finally {
             IOUtils.closeQuietly(fileReader);
         }
     }
 
     /**
      * Creates the vector of Timers plugins.
      *
      * @param properties
      *            Description of Parameter
      * @return The Timers value
      * @deprecated (3.0) not used + pre-java 1.2 collection
      */
     @Deprecated
     public static Vector<Object> getTimers(Properties properties) {
         return instantiate(getVector(properties, "timer."), // $NON-NLS-1$
                 "org.apache.jmeter.timers.Timer"); // $NON-NLS-1$
     }
 
     /**
      * Creates the vector of visualizer plugins.
      *
      * @param properties
      *            Description of Parameter
      * @return The Visualizers value
      * @deprecated (3.0) not used + pre-java 1.2 collection
      */
     @Deprecated
     public static Vector<Object> getVisualizers(Properties properties) {
         return instantiate(getVector(properties, "visualizer."), // $NON-NLS-1$
                 "org.apache.jmeter.visualizers.Visualizer"); // $NON-NLS-1$
     }
 
     /**
      * Creates a vector of SampleController plugins.
      *
      * @param properties
      *            The properties with information about the samplers
      * @return The Controllers value
      * @deprecated (3.0) not used + pre-java 1.2 collection
      */
     // TODO - does not appear to be called directly
     @Deprecated
     public static Vector<Object> getControllers(Properties properties) {
         String name = "controller."; // $NON-NLS-1$
         Vector<Object> v = new Vector<>();
         Enumeration<?> names = properties.keys();
         while (names.hasMoreElements()) {
             String prop = (String) names.nextElement();
             if (prop.startsWith(name)) {
                 Object o = instantiate(properties.getProperty(prop),
                         "org.apache.jmeter.control.SamplerController"); // $NON-NLS-1$
                 v.addElement(o);
             }
         }
         return v;
     }
 
     /**
      * Create a string of class names for a particular SamplerController
      *
      * @param properties
      *            The properties with info about the samples.
      * @param name
      *            The name of the sampler controller.
      * @return The TestSamples value
      * @deprecated (3.0) not used
      */
     @Deprecated
     public static String[] getTestSamples(Properties properties, String name) {
         Vector<String> vector = getVector(properties, name + ".testsample"); // $NON-NLS-1$
         return vector.toArray(new String[vector.size()]);
     }
 
     /**
      * Create an instance of an org.xml.sax.Parser based on the default props.
      *
      * @return The XMLParser value
      * @deprecated (3.0) was only called by UserParameterXMLParser.getXMLParameters which has been removed in 3.0
      */
     @Deprecated
     public static XMLReader getXMLParser() {
         final String parserName = getPropDefault("xml.parser", // $NON-NLS-1$
                 "org.apache.xerces.parsers.SAXParser");  // $NON-NLS-1$
         return (XMLReader) instantiate(parserName,
                 "org.xml.sax.XMLReader"); // $NON-NLS-1$
     }
 
     /**
      * Creates the vector of alias strings.
      * <p>
      * The properties will be filtered by all values starting with
      * <code>alias.</code>. The matching entries will be used for the new
      * {@link Hashtable} while the prefix <code>alias.</code> will be stripped
      * of the keys.
      *
      * @param properties
      *            the input values
      * @return The Alias value
      * @deprecated (3.0) not used
      */
     @Deprecated
     public static Hashtable<String, String> getAlias(Properties properties) {
         return getHashtable(properties, "alias."); // $NON-NLS-1$
     }
 
     /**
      * Creates a vector of strings for all the properties that start with a
      * common prefix.
      *
      * @param properties
      *            Description of Parameter
      * @param name
      *            Description of Parameter
      * @return The Vector value
      */
     public static Vector<String> getVector(Properties properties, String name) {
         Vector<String> v = new Vector<>();
         Enumeration<?> names = properties.keys();
         while (names.hasMoreElements()) {
             String prop = (String) names.nextElement();
             if (prop.startsWith(name)) {
                 v.addElement(properties.getProperty(prop));
             }
         }
         return v;
     }
 
     /**
      * Creates a table of strings for all the properties that start with a
      * common prefix.
      * <p>
      * So if you have {@link Properties} <code>prop</code> with two entries, say
      * <ul>
      * <li>this.test</li>
      * <li>that.something</li>
      * </ul>
      * And would call this method with a <code>prefix</code> <em>this</em>, the
      * result would be a new {@link Hashtable} with one entry, which key would
      * be <em>test</em>.
      *
      * @param properties
      *            input to search
      * @param prefix
      *            to match against properties
      * @return a Hashtable where the keys are the original matching keys with
      *         the prefix removed
      * @deprecated (3.0) not used
      */
     @Deprecated
     public static Hashtable<String, String> getHashtable(Properties properties, String prefix) {
         Hashtable<String, String> t = new Hashtable<>();
         Enumeration<?> names = properties.keys();
         final int length = prefix.length();
         while (names.hasMoreElements()) {
             String prop = (String) names.nextElement();
             if (prop.startsWith(prefix)) {
                 t.put(prop.substring(length), properties.getProperty(prop));
             }
         }
         return t;
     }
 
     /**
      * Get a int value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static int getPropDefault(String propName, int defaultVal) {
         int ans;
         try {
             ans = Integer.parseInt(appProperties.getProperty(propName, Integer.toString(defaultVal)).trim());
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching int property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a boolean value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static boolean getPropDefault(String propName, boolean defaultVal) {
         boolean ans;
         try {
             String strVal = appProperties.getProperty(propName, Boolean.toString(defaultVal)).trim();
             if (strVal.equalsIgnoreCase("true") || strVal.equalsIgnoreCase("t")) { // $NON-NLS-1$  // $NON-NLS-2$
                 ans = true;
             } else if (strVal.equalsIgnoreCase("false") || strVal.equalsIgnoreCase("f")) { // $NON-NLS-1$  // $NON-NLS-2$
                 ans = false;
             } else {
                 ans = Integer.parseInt(strVal) == 1;
             }
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching boolean property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a long value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static long getPropDefault(String propName, long defaultVal) {
         long ans;
         try {
             ans = Long.parseLong(appProperties.getProperty(propName, Long.toString(defaultVal)).trim());
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching long property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a String value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
-     * @return The PropDefault value
+     * @return The PropDefault value applying a trim on it
      */
     public static String getPropDefault(String propName, String defaultVal) {
         String ans = defaultVal;
         try 
         {
             String value = appProperties.getProperty(propName, defaultVal);
             if(value != null) {
                 ans = value.trim();
             }
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching String property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
     
     /**
      * Get the value of a JMeter property.
      *
      * @param propName
      *            the name of the property.
      * @return the value of the JMeter property, or null if not defined
      */
     public static String getProperty(String propName) {
         String ans = null;
         try {
             ans = appProperties.getProperty(propName);
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching String property:'"+propName+"'");
             ans = null;
         }
         return ans;
     }
 
     /**
      * Set a String value
      *
      * @param propName
      *            the name of the property.
      * @param propValue
      *            the value of the property
      * @return the previous value of the property
      */
     public static Object setProperty(String propName, String propValue) {
         return appProperties.setProperty(propName, propValue);
     }
 
     /**
      * Sets the selection of the JComboBox to the Object 'name' from the list in
      * namVec.
      * NOTUSED?
      * @param properties not used at the moment
      * @param combo {@link JComboBox} to work on
      * @param namVec List of names, which are displayed in <code>combo</code>
      * @param name Name, that is to be selected. It has to be in <code>namVec</code>
      */
     @Deprecated
     public static void selJComboBoxItem(Properties properties, JComboBox<?> combo, Vector<?> namVec, String name) {
         int idx = namVec.indexOf(name);
         combo.setSelectedIndex(idx);
         // Redisplay.
         combo.updateUI();
     }
 
     /**
      * Instatiate an object and guarantee its class.
      *
      * @param className
      *            The name of the class to instantiate.
      * @param impls
      *            The name of the class it must be an instance of
      * @return an instance of the class, or null if instantiation failed or the class did not implement/extend as required
      * @deprecated (3.0) not used out of this class
      */
     // TODO probably not needed
     @Deprecated
     public static Object instantiate(String className, String impls) {
         if (className != null) {
             className = className.trim();
         }
 
         if (impls != null) {
             impls = impls.trim();
         }
 
         try {
             Class<?> c = Class.forName(impls);
             try {
                 Class<?> o = Class.forName(className);
                 Object res = o.newInstance();
                 if (c.isInstance(res)) {
                     return res;
                 }
                 throw new IllegalArgumentException(className + " is not an instance of " + impls);
             } catch (ClassNotFoundException e) {
                 log.error("Error loading class " + className + ": class is not found");
             } catch (IllegalAccessException e) {
                 log.error("Error loading class " + className + ": does not have access");
             } catch (InstantiationException e) {
                 log.error("Error loading class " + className + ": could not instantiate");
             } catch (NoClassDefFoundError e) {
                 log.error("Error loading class " + className + ": couldn't find class " + e.getMessage());
             }
         } catch (ClassNotFoundException e) {
             log.error("Error loading class " + impls + ": was not found.");
         }
         return null;
     }
 
     /**
      * Instantiate a vector of classes
      *
      * @param v
      *            Description of Parameter
      * @param className
      *            Description of Parameter
      * @return Description of the Returned Value
      * @deprecated (3.0) not used out of this class
      */
     @Deprecated
     public static Vector<Object> instantiate(Vector<String> v, String className) {
         Vector<Object> i = new Vector<>();
         try {
             Class<?> c = Class.forName(className);
             Enumeration<String> elements = v.elements();
             while (elements.hasMoreElements()) {
                 String name = elements.nextElement();
                 try {
                     Object o = Class.forName(name).newInstance();
                     if (c.isInstance(o)) {
                         i.addElement(o);
                     }
                 } catch (ClassNotFoundException e) {
                     log.error("Error loading class " + name + ": class is not found");
                 } catch (IllegalAccessException e) {
                     log.error("Error loading class " + name + ": does not have access");
                 } catch (InstantiationException e) {
                     log.error("Error loading class " + name + ": could not instantiate");
                 } catch (NoClassDefFoundError e) {
                     log.error("Error loading class " + name + ": couldn't find class " + e.getMessage());
                 }
             }
         } catch (ClassNotFoundException e) {
             log.error("Error loading class " + className + ": class is not found");
         }
         return i;
     }
 
     /**
      * Create a button with the netscape style
      *
      * @param name
      *            Description of Parameter
      * @param listener
      *            Description of Parameter
      * @return Description of the Returned Value
      * @deprecated (3.0) not used
      */
     @Deprecated
     public static JButton createButton(String name, ActionListener listener) {
         JButton button = new JButton(getImage(name + ".on.gif")); // $NON-NLS-1$
         button.setDisabledIcon(getImage(name + ".off.gif")); // $NON-NLS-1$
         button.setRolloverIcon(getImage(name + ".over.gif")); // $NON-NLS-1$
         button.setPressedIcon(getImage(name + ".down.gif")); // $NON-NLS-1$
         button.setActionCommand(name);
         button.addActionListener(listener);
         button.setRolloverEnabled(true);
         button.setFocusPainted(false);
         button.setBorderPainted(false);
         button.setOpaque(false);
         button.setPreferredSize(new Dimension(24, 24));
         return button;
     }
 
     /**
      * Create a button with the netscape style
      *
      * @param name
      *            Description of Parameter
      * @param listener
      *            Description of Parameter
      * @return Description of the Returned Value
      * @deprecated (3.0) not used
      */
     @Deprecated
     public static JButton createSimpleButton(String name, ActionListener listener) {
         JButton button = new JButton(getImage(name + ".gif")); // $NON-NLS-1$
         button.setActionCommand(name);
         button.addActionListener(listener);
         button.setFocusPainted(false);
         button.setBorderPainted(false);
         button.setOpaque(false);
         button.setPreferredSize(new Dimension(25, 25));
         return button;
     }
 
 
     /**
      * Report an error through a dialog box.
      * Title defaults to "error_title" resource string
      * @param errorMsg - the error message.
      */
     public static void reportErrorToUser(String errorMsg) {
         reportErrorToUser(errorMsg, JMeterUtils.getResString("error_title")); // $NON-NLS-1$
     }
 
     /**
      * Report an error through a dialog box.
      *
      * @param errorMsg - the error message.
      * @param titleMsg - title string
      */
     public static void reportErrorToUser(String errorMsg, String titleMsg) {
         if (errorMsg == null) {
             errorMsg = "Unknown error - see log file";
             log.warn("Unknown error", new Throwable("errorMsg == null"));
         }
         GuiPackage instance = GuiPackage.getInstance();
         if (instance == null) {
             System.out.println(errorMsg);
             return; // Done
         }
         try {
             JOptionPane.showMessageDialog(instance.getMainFrame(),
                     errorMsg,
                     titleMsg,
                     JOptionPane.ERROR_MESSAGE);
         } catch (HeadlessException e) {
             log.warn("reportErrorToUser(\"" + errorMsg + "\") caused", e);
         }
     }
 
     /**
      * Finds a string in an array of strings and returns the
      *
      * @param array
      *            Array of strings.
      * @param value
      *            String to compare to array values.
      * @return Index of value in array, or -1 if not in array.
      * @deprecated (3.0) not used
      */
     //TODO - move to JOrphanUtils?
     @Deprecated
     public static int findInArray(String[] array, String value) {
         int count = -1;
         int index = -1;
         if (array != null && value != null) {
             while (++count < array.length) {
                 if (array[count] != null && array[count].equals(value)) {
                     index = count;
                     break;
                 }
             }
         }
         return index;
     }
 
     /**
      * Takes an array of strings and a tokenizer character, and returns a string
      * of all the strings concatenated with the tokenizer string in between each
      * one.
      *
      * @param splittee
      *            Array of Objects to be concatenated.
      * @param splitChar
      *            Object to unsplit the strings with.
      * @return Array of all the tokens.
      */
     //TODO - move to JOrphanUtils?
     public static String unsplit(Object[] splittee, Object splitChar) {
         StringBuilder retVal = new StringBuilder();
         int count = -1;
         while (++count < splittee.length) {
             if (splittee[count] != null) {
                 retVal.append(splittee[count]);
             }
             if (count + 1 < splittee.length && splittee[count + 1] != null) {
                 retVal.append(splitChar);
             }
         }
         return retVal.toString();
     }
 
     // End Method
 
     /**
      * Takes an array of strings and a tokenizer character, and returns a string
      * of all the strings concatenated with the tokenizer string in between each
      * one.
      *
      * @param splittee
      *            Array of Objects to be concatenated.
      * @param splitChar
      *            Object to unsplit the strings with.
      * @param def
      *            Default value to replace null values in array.
      * @return Array of all the tokens.
      */
     //TODO - move to JOrphanUtils?
     public static String unsplit(Object[] splittee, Object splitChar, String def) {
         StringBuilder retVal = new StringBuilder();
         int count = -1;
         while (++count < splittee.length) {
             if (splittee[count] != null) {
                 retVal.append(splittee[count]);
             } else {
                 retVal.append(def);
             }
             if (count + 1 < splittee.length) {
                 retVal.append(splitChar);
             }
         }
         return retVal.toString();
     }
 
     /**
      * Get the JMeter home directory - does not include the trailing separator.
      *
      * @return the home directory
      */
     public static String getJMeterHome() {
         return jmDir;
     }
 
     /**
      * Get the JMeter bin directory - does not include the trailing separator.
      *
      * @return the bin directory
      */
     public static String getJMeterBinDir() {
         return jmBin;
     }
 
     public static void setJMeterHome(String home) {
         jmDir = home;
         jmBin = jmDir + File.separator + "bin"; // $NON-NLS-1$
     }
 
     // TODO needs to be synch? Probably not changed after threads have started
     private static String jmDir; // JMeter Home directory (excludes trailing separator)
     private static String jmBin; // JMeter bin directory (excludes trailing separator)
 
 
     /**
      * Gets the JMeter Version.
      *
      * @return the JMeter version string
      */
     public static String getJMeterVersion() {
         return JMeterVersion.getVERSION();
     }
 
     /**
      * Gets the JMeter copyright.
      *
      * @return the JMeter copyright string
      */
     public static String getJMeterCopyright() {
         return JMeterVersion.getCopyRight();
     }
 
     /**
      * Determine whether we are in 'expert' mode. Certain features may be hidden
      * from user's view unless in expert mode.
      *
      * @return true iif we're in expert mode
      */
     public static boolean isExpertMode() {
         return JMeterUtils.getPropDefault(EXPERT_MODE_PROPERTY, false);
     }
 
     /**
      * Find a file in the current directory or in the JMeter bin directory.
      *
      * @param fileName the name of the file to find
      * @return File object
      */
     public static File findFile(String fileName){
         File f =new File(fileName);
         if (!f.exists()){
             f=new File(getJMeterBinDir(),fileName);
         }
         return f;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getHostAddress()
      *
      * @return String representation of local IP address
      */
     public static synchronized String getLocalHostIP(){
         if (localHostIP == null) {
             getLocalHostDetails();
         }
         return localHostIP;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getHostName()
      *
      * @return local host name
      */
     public static synchronized String getLocalHostName(){
         if (localHostName == null) {
             getLocalHostDetails();
         }
         return localHostName;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getCanonicalHostName()
      *
      * @return local host name in canonical form
      */
     public static synchronized String getLocalHostFullName(){
         if (localHostFullName == null) {
             getLocalHostDetails();
         }
         return localHostFullName;
     }
 
     private static void getLocalHostDetails(){
         InetAddress localHost=null;
         try {
             localHost = InetAddress.getLocalHost();
         } catch (UnknownHostException e1) {
             log.error("Unable to get local host IP address.", e1);
             return; // TODO - perhaps this should be a fatal error?
         }
         localHostIP=localHost.getHostAddress();
         localHostName=localHost.getHostName();
         localHostFullName=localHost.getCanonicalHostName();
     }
     
     /**
      * Split line into name/value pairs and remove colon ':'
      * 
      * @param headers
      *            multi-line string headers
      * @return a map name/value for each header
      */
     public static LinkedHashMap<String, String> parseHeaders(String headers) {
         LinkedHashMap<String, String> linkedHeaders = new LinkedHashMap<>();
         String[] list = headers.split("\n"); // $NON-NLS-1$
         for (String header : list) {
             int colon = header.indexOf(':'); // $NON-NLS-1$
             if (colon <= 0) {
                 linkedHeaders.put(header, ""); // Empty value // $NON-NLS-1$
             } else {
                 linkedHeaders.put(header.substring(0, colon).trim(), header
                         .substring(colon + 1).trim());
             }
         }
         return linkedHeaders;
     }
 
     /**
      * Run the runnable in AWT Thread if current thread is not AWT thread
      * otherwise runs call {@link SwingUtilities#invokeAndWait(Runnable)}
      * @param runnable {@link Runnable}
      */
     public static void runSafe(Runnable runnable) {
         runSafe(true, runnable);
     }
 
     /**
      * Run the runnable in AWT Thread if current thread is not AWT thread
      * otherwise runs call {@link SwingUtilities#invokeAndWait(Runnable)}
      * @param synchronous flag, whether we will wait for the AWT Thread to finish its job.
      * @param runnable {@link Runnable}
      */
     public static void runSafe(boolean synchronous, Runnable runnable) {
         if(SwingUtilities.isEventDispatchThread()) {
             runnable.run();
         } else {
             if (synchronous) {
                 try {
                     SwingUtilities.invokeAndWait(runnable);
                 } catch (InterruptedException e) {
                     log.warn("Interrupted in thread "
                             + Thread.currentThread().getName(), e);
                 } catch (InvocationTargetException e) {
                     throw new Error(e);
                 }
             } else {
                 SwingUtilities.invokeLater(runnable);
             }
         }
     }
 
     /**
      * Help GC by triggering GC and finalization
      */
     public static void helpGC() {
         System.gc();
         System.runFinalization();
     }
 
     /**
      * Hack to make matcher clean the two internal buffers it keeps in memory which size is equivalent to 
      * the unzipped page size
      * @param matcher {@link Perl5Matcher}
      * @param pattern Pattern
      */
     public static void clearMatcherMemory(Perl5Matcher matcher, Pattern pattern) {
         try {
             if (pattern != null) {
                 matcher.matches("", pattern); // $NON-NLS-1$
             }
         } catch (Exception e) {
             // NOOP
         }
     }
 
     /**
      * Provide info, whether we run in HiDPI mode
      * @return {@code true} if we run in HiDPI mode, {@code false} otherwise
      */
     public static boolean getHiDPIMode() {
         return JMeterUtils.getPropDefault("jmeter.hidpi.mode", false);  // $NON-NLS-1$
     }
 
     /**
      * Provide info about the HiDPI scale factor
      * @return the factor by which we should scale elements for HiDPI mode
      */
     public static double getHiDPIScaleFactor() {
         return Double.parseDouble(JMeterUtils.getPropDefault("jmeter.hidpi.scale.factor", "1.0"));  // $NON-NLS-1$  $NON-NLS-2$
     }
 
     /**
      * Apply HiDPI mode management to {@link JTable}
      * @param table the {@link JTable} which should be adapted for HiDPI mode
      */
     public static void applyHiDPI(JTable table) {
         if (JMeterUtils.getHiDPIMode()) {
             table.setRowHeight((int) Math.round(table.getRowHeight() * JMeterUtils.getHiDPIScaleFactor()));
         }
     }
 
+    /**
+     * Return delimiterValue handling the TAB case
+     * @param delimiterValue Delimited value 
+     * @return String delimited modified to handle correctly tab
+     * @throws JMeterError if delimiterValue has a length different from 1
+     */
+    public static String getDelimiter(String delimiterValue) {
+        if (delimiterValue.equals("\\t")) {// Make it easier to enter a tab (can use \<tab> but that is awkward)
+            delimiterValue="\t";
+        }
+
+        if (delimiterValue.length() != 1){
+            throw new JMeterError("Delimiter '"+delimiterValue+"' must be of length 1.");
+        }
+        return delimiterValue;
+    }
+
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 65117e872..3fd26c4c5 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,334 +1,336 @@
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
 <!DOCTYPE document
 [
 <!ENTITY hellip   "&#x02026;" >
 <!ENTITY vellip   "&#x022EE;" >
 ]>
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
 
 
 <!--  =================== 3.1 =================== -->
 
 <h1>Version 3.1</h1>
 
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
 
 <ch_category>Sample category</ch_category>
 <ch_title>Sample title</ch_title>
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>A cache for CSS Parsing of URLs has been introduced in this version, it is enabled by default. It is controlled by property <code>css.parser.cache.size</code>. It can be disabled by setting its value to 0. See <bugzilla>59885</bugzilla></li>
     <li>ThroughputController defaults have changed. Now defaults are Percent Executions which is global and no more per user. See <bugzilla>60023</bugzilla></li>
     <li>Since 3.1 version, HTML report ignores Empty Transaction controller (possibly generated by If Controller or Throughput Controller) when computing metrics. This provides more accurate metrics</li>
     <li>Since 3.1 version, Summariser ignores SampleResults generated by TransactionController when computing the live statistics, see <bugzilla>60109</bugzilla></li>
     <li>Since 3.1 version, when using Stripped modes (by default StrippedBatch is used) , response will be stripped also for failing SampleResults, you can revert this to previous behaviour by setting <code>sample_sender_strip_also_on_error=false</code> in user.properties, see <bugzilla>60137</bugzilla></li>
 </ul>
 
 <h3>Deprecated and removed elements</h3>
 <ul>
     <li>Sample removed element</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59882</bug>Reduce memory allocations for better throughput. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com) through <pr>217</pr> and <pr>228</pr></li>
     <li><bug>59885</bug>Optimize css parsing for embedded resources download by introducing a cache. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com) through <pr>219</pr></li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><pr>211</pr>Differentiate the timing for JDBC Sampler. Use latency and connect time. Contributed by Thomas Peyrard (thomas.peyrard at murex.com)</li>
     <li><bug>59620</bug>Fix button action in "JMS Publisher -> Random File from folder specified below" to allow to select a directory</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>59351</bug>Improve log/error/message for IncludeController. Partly contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>60023</bug>ThroughputController : Make "Percent Executions" and global the default values. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60082</bug>Validation mode : Be able to force Throughput Controller to run as if it was set to 100%</li>
     <li><bug>59329</bug>Trim spaces in input filename in CSVDataSet.</li>
     <li><bug>59349</bug>Trim spaces in input filename in IncludeController.</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>59953</bug>GraphiteBackendListener : Add Average metric. Partly contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
     <li><bug>59975</bug>View Results Tree : Text renderer annoyingly scrolls down when content is bulky. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60109</bug>Summariser : Make it ignore TC generated SampleResult in its summary computations</li>
     <li><bug>59948</bug>Add a formated and sane HTML source code render to View Results Tree</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>59845</bug>Log messages about JSON Path mismatches at <code>debug</code> level instead of <code>error</code>.</li>
     <li><pr>212</pr>Allow multiple selection and delete in HTTP Authorization Manager. Based on a patch by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><bug>59816</bug><pr>213</pr>Allow multiple selection and delete in HTTP Header Manager.
     Based on a patch by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><bug>59967</bug>CSS/JQuery Extractor : Allow empty default value. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>59974</bug>Response Assertion : Add button "Add from clipboard". Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60050</bug>CSV Data Set : Make it clear in the logs when a thread will exit due to this configuration</li>
     <li><bug>59962</bug>Cache Manager does not update expires date when response code is 304.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>59963</bug>New Function <code>__RandomFromMultipleVars</code>: Ability to compute a random value from values of 1 or more variables. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>59991</bug>New function __groovy to evaluate Groovy Script. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
     <li><pr>214</pr>Add spanish translation for delayed starting of threads. Contributed by Asier Lostalé (asier.lostale at openbravo.com).</li>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
     <li><bug>59954</bug>Web Report/Dashboard : Add average metric</li>
     <li><bug>59956</bug>Web Report / Dashboard : Add ability to generate a graph for a range of data</li>
     <li><bug>60065</bug>Report / Dashboard : Improve Dashboard Error Summary by adding response message to "Type of error". Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60079</bug>Report / Dashboard : Add a new "Response Time Overview" graph</li>
     <li><bug>60080</bug>Report / Dashboard : Add a new "Connect Time Over Time " graph. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60091</bug>Report / Dashboard : Have a new report containing min/max and percentiles graphs.</li>
     <li><bug>60108</bug>Report / Dashboard : In Requests Summary rounding is too aggressive</li>
     <li><bug>60098</bug>Report / Dashboard : Reduce default value for "jmeter.reportgenerator.statistic_window" to reduce memory impact</li>
     <li><bug>60115</bug>Add date format property for start/end date filter into Report generator</li>
 </ul>
 <h3>General</h3>
 <ul>
     <li><bug>59803</bug>Use <code>isValid()</code> method from jdbc driver, if no validationQuery
     is given in JDBC Connection Configuration.</li>
     <li><bug>59918</bug>Ant generated HTML report is broken (extras folder)</li>
     <li><bug>57493</bug>Create a documentation page for properties</li>
     <li><bug>59924</bug>The log level of XXX package is set to DEBUG if <code>log_level.XXXX</code> property value contains spaces, same for __log function</li>
     <li><bug>59777</bug>Extract slf4j binding into its own jar and make it a jmeter lib</li>
     <li><bug>60085</bug>Remove cache for prepared statements, as it didn't work with the current jdbc pool implementation and current jdbc drivers should support caching of prepared statements themselves.</li>
     <li><bug>60137</bug>In Distributed testing when using StrippedXXXX modes strip response also on error</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li>Updated to jsoup-1.9.2 (from 1.8.3)</li>
     <li>Updated to ph-css 4.1.5 (from 4.1.4)</li>
     <li>Updated to tika-core and tika-parsers 1.13 (from 1.12)</li>
     <li><pr>215</pr>Reduce duplicated code by using the newly added method <code>GuiUtils#cancelEditing</code>.
     Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><pr>218</pr>Misc cleanup. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><pr>216</pr>Re-use pattern when possible. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>58888</bug>HTTP(S) Test Script Recorder (ProxyControl) does not add TestElement's returned by SamplerCreator createChildren ()</li>
     <li><bug>59902</bug>Https handshake failure when setting <code>httpclient.socket.https.cps</code> property</li>
     <li><bug>60084</bug>JMeter 3.0 embedded resource URL is silently encoded</li>
  </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>59113</bug>JDBC Connection Configuration : Transaction Isolation level not correctly set if constant used instead of numerical</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>59712</bug>Display original query in RequestView when decoding fails. Based on a patch by
          Teemu Vesala (teemu.vesala at qentinel.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>59964</bug>JSR223 Test Element : Cache compiled script if available is not correctly reset. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>59609</bug>Format extracted JSON Objects in JSON Post Processor correctly as JSON.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>59400</bug>Get rid of UnmarshalException on stopping when <code>-X</code> option is used.</li>
     <li><bug>59607</bug>JMeter crashes when reading large test plan (greater than 2g). Based on fix by Felix Draxler (felix.draxler at sap.com)</li>
     <li><bug>59621</bug>Error count in report dashboard is one off.</li>
     <li><bug>59657</bug>Only set font in JSyntaxTextArea, when property <code>jsyntaxtextarea.font.family</code> is set.</li>
     <li><bug>59720</bug>Batch test file comparisons fail on Windows as XML files are generated as EOL=LF</li>
     <li>Code cleanups. Patches by Graham Russell (graham at ham1.co.uk)</li>
     <li><bug>59722</bug>Use StandardCharsets to reduce the possibility of misspelling Charset names.</li>
     <li><bug>59723</bug>Use jmeter.properties for testing whenever possible</li>
     <li><bug>59726</bug>Unit test to check that CSV header text and sample format don't change unexpectedly</li>
     <li><bug>59889</bug>Change encoding to UTF-8 in reports for dashboard.</li>
     <li><bug>60053</bug>In Non GUI mode, a Stacktrace is shown at end of test while report is being generated</li>
     <li><bug>60049</bug>When using Timers with high delays or Constant Throughput Timer with low throughput, Scheduler may take a lot of time to exit, same for Shutdown test </li>
     <li><bug>60089</bug>Report / Dashboard : Bytes throughput Over Time has reversed Sent and Received bytes. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60090</bug>Report / Dashboard : Empty Transaction Controller should not count in metrics</li>
     <li><bug>60103</bug>Report / Dashboard : Requests summary includes Transaction Controller leading to wrong percentage</li>
     <li><bug>60105</bug>Report / Dashboard : Report requires Transaction Controller "generate parent sample" option to be checked , fix related issues</li>
     <li><bug>60107</bug>Report / Dashboard : In StatisticSummary, TransactionController SampleResult makes Total line wrong</li>
     <li><bug>60110</bug>Report / Dashboard : In Response Time Percentiles, slider is useless</li>
     <li><bug>60135</bug>Report / Dashboard : Active Threads Over Time should be in OverTime section</li>
+    <li><bug>60125</bug>Report / Dashboard : Dashboard cannot be generated if the default delimiter is <code>\t</code>. Based on a report from Tamas Szabadi (tamas.szabadi at rightside.co)</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 </p>
 <ul>
 <li>Felix Draxler (felix.draxler at sap.com)</li>
 <li>Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li>Graham Russell (graham at ham1.co.uk)</li>
 <li>Teemu Vesala (teemu.vesala at qentinel.com)</li>
 <li>Asier Lostalé (asier.lostale at openbravo.com)</li>
 <li>Thomas Peyrard (thomas.peyrard at murex.com)</li>
 <li>Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
+<li>Tamas Szabadi (tamas.szabadi at rightside.co)</li>
 </ul>
 <p>We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:</p>
 <ul>
 </ul>
 <p>
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
  
 <ch_section>Known problems and workarounds</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 the total number of threads only applies to a locally run test, otherwise it will show <code>0</code> (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <source>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </source>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <source>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </source>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error:
 <source>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</source>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing 
 the Java <code>jdk.certpath.disabledAlgorithms</code> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <source>JAVA_HOME/jre/lib/security/java.security</source>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 
 <li>
 JTable selection with keyboard (<keycombo><keysym>SHIFT</keysym><keysym>up/down</keysym></keycombo>) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a> 
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
