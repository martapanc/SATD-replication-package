diff --git a/src/core/org/apache/jmeter/plugin/PluginManager.java b/src/core/org/apache/jmeter/plugin/PluginManager.java
index 479cf3772..d6ddfc880 100644
--- a/src/core/org/apache/jmeter/plugin/PluginManager.java
+++ b/src/core/org/apache/jmeter/plugin/PluginManager.java
@@ -1,73 +1,73 @@
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
 
 package org.apache.jmeter.plugin;
 
 import java.net.URL;
 
 import javax.swing.ImageIcon;
 
 import org.apache.jmeter.gui.GUIFactory;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public final class PluginManager {
     private static final PluginManager instance = new PluginManager();
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
 
     private PluginManager() {
     }
 
     /**
      * Installs a plugin.
      *
      * @param plugin
      *            the plugin to install
      * @param useGui
      *            indication of whether or not the gui will be used
      */
     public static void install(JMeterPlugin plugin, boolean useGui) {
         if (useGui) {
             instance.installPlugin(plugin);
         }
     }
 
     private void installPlugin(JMeterPlugin plugin) {
         String[][] icons = plugin.getIconMappings();
         ClassLoader classloader = plugin.getClass().getClassLoader();
 
         for (String[] icon : icons) {
             URL resource = classloader.getResource(icon[1].trim());
 
             if (resource == null) {
-                log.warn("Can't find icon for " + icon[0] + " - " + icon[1]);
+                log.warn("Can't find icon for {} - {}", icon[0], icon[1]);
             } else {
                 GUIFactory.registerIcon(icon[0], new ImageIcon(resource));
                 if (icon.length > 2 && icon[2] != null) {
                     URL resource2 = classloader.getResource(icon[2].trim());
                     if (resource2 == null) {
-                        log.info("Can't find disabled icon for " + icon[0] + " - " + icon[2]);
+                        log.info("Can't find disabled icon for {} - {}", icon[0], icon[2]);
                     } else {
                         GUIFactory.registerDisabledIcon(icon[0], new ImageIcon(resource2));
                     }
                 }
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/report/config/ReportGeneratorConfiguration.java b/src/core/org/apache/jmeter/report/config/ReportGeneratorConfiguration.java
index e92cfc0ec..803f0f555 100644
--- a/src/core/org/apache/jmeter/report/config/ReportGeneratorConfiguration.java
+++ b/src/core/org/apache/jmeter/report/config/ReportGeneratorConfiguration.java
@@ -1,778 +1,762 @@
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
 package org.apache.jmeter.report.config;
 
 import java.io.File;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Properties;
 import java.util.regex.Pattern;
 
-import jodd.props.Props;
-
 import org.apache.commons.lang3.StringUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
+import jodd.props.Props;
 
 /**
  * The class ReportGeneratorConfiguration describes the configuration of the
  * report generator.
  *
  * @since 3.0
  */
 public class ReportGeneratorConfiguration {
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ReportGeneratorConfiguration.class);
 
     private static final String RANGE_DATE_FORMAT_DEFAULT = "yyyyMMddHHmmss"; //$NON-NLS-1$
 
     public static final char KEY_DELIMITER = '.';
     public static final String REPORT_GENERATOR_KEY_PREFIX = "jmeter.reportgenerator";
     
     public static final String REPORT_GENERATOR_KEY_RANGE_DATE_FORMAT = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "date_format";
     
     public static final String REPORT_GENERATOR_GRAPH_KEY_PREFIX = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "graph";
     public static final String REPORT_GENERATOR_EXPORTER_KEY_PREFIX = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "exporter";
 
     // Temporary directory
     private static final String REPORT_GENERATOR_KEY_TEMP_DIR = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "temp_dir";
     private static final File REPORT_GENERATOR_KEY_TEMP_DIR_DEFAULT = new File(
             "temp");
 
     // Apdex Satisfied Threshold
     private static final String REPORT_GENERATOR_KEY_APDEX_SATISFIED_THRESHOLD = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "apdex_satisfied_threshold";
     private static final Long REPORT_GENERATOR_KEY_APDEX_SATISFIED_THRESHOLD_DEFAULT = Long.valueOf(500L);
 
     // Apdex Tolerated Threshold
     private static final String REPORT_GENERATOR_KEY_APDEX_TOLERATED_THRESHOLD = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "apdex_tolerated_threshold";
     private static final Long REPORT_GENERATOR_KEY_APDEX_TOLERATED_THRESHOLD_DEFAULT = Long.valueOf(1500L);
 
     // Exclude Transaction Controller from Top5 Errors by Sampler consumer
     private static final String REPORT_GENERATOR_KEY_EXCLUDE_TC_FROM_TOP5_ERRORS_BY_SAMPLER = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "exclude_tc_from_top5_errors_by_sampler";
 
     // Sample Filter
     private static final String REPORT_GENERATOR_KEY_SAMPLE_FILTER = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "sample_filter";
 
     // report title
     private static final String REPORT_GENERATOR_KEY_REPORT_TITLE = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "report_title";
     
     // start date for which report must be generated
     private static final String REPORT_GENERATOR_KEY_START_DATE = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "start_date";
     
     // end date for which report must be generated
     private static final String REPORT_GENERATOR_KEY_END_DATE = REPORT_GENERATOR_KEY_PREFIX
             + KEY_DELIMITER + "end_date";
 
-    private static final String LOAD_EXPORTER_FMT = "Load configuration for exporter \"%s\"";
-    private static final String LOAD_GRAPH_FMT = "Load configuration for graph \"%s\"";
-    private static final String INVALID_KEY_FMT = "Invalid property \"%s\", skip it.";
-    private static final String NOT_FOUND_PROPERTY_FMT = "Property \"%s\" not found, using default value \"%s\" instead.";
 
     // Required graph properties
     // Exclude controllers
     public static final String GRAPH_KEY_EXCLUDE_CONTROLLERS = "exclude_controllers";
     public static final Boolean GRAPH_KEY_EXCLUDE_CONTROLLERS_DEFAULT = Boolean.FALSE;
 
     // Title
     public static final String GRAPH_KEY_TITLE = "title";
     public static final String GRAPH_KEY_TITLE_DEFAULT = "";
 
     // Required exporter properties
     // Filters only sample series ?
     public static final String EXPORTER_KEY_FILTERS_ONLY_SAMPLE_SERIES = "filters_only_sample_series";
     public static final Boolean EXPORTER_KEY_FILTERS_ONLY_SAMPLE_SERIES_DEFAULT = Boolean.TRUE;
 
     // Series filter
     public static final String EXPORTER_KEY_SERIES_FILTER = "series_filter";
     public static final String EXPORTER_KEY_SERIES_FILTER_DEFAULT = "";
 
     // Show controllers only
     public static final String EXPORTER_KEY_SHOW_CONTROLLERS_ONLY = "show_controllers_only";
     public static final Boolean EXPORTER_KEY_SHOW_CONTROLLERS_ONLY_DEFAULT = Boolean.FALSE;
 
     // Optional exporter properties
     public static final String EXPORTER_KEY_GRAPH_EXTRA_OPTIONS = "graph_options";
 
     // Sub configuration keys
     public static final String SUBCONF_KEY_CLASSNAME = "classname";
     public static final String SUBCONF_KEY_PROPERTY = "property";
 
-    private static final String START_LOADING_MSG = "Report generator properties loading";
-    private static final String END_LOADING_MSG = "End of report generator properties loading";
-    private static final String REQUIRED_PROPERTY_FMT = "Use \"%s\" value for required property \"%s\"";
-    private static final String OPTIONAL_PROPERTY_FMT = "Use \"%s\" value for optional property \"%s\"";
-
     private static final class ExporterConfigurationFactory implements
             SubConfigurationFactory<ExporterConfiguration> {
         private final Props props;
 
         private ExporterConfigurationFactory(Props props) {
             this.props = props;
         }
 
         @Override
         public ExporterConfiguration createSubConfiguration() {
             return new ExporterConfiguration();
         }
 
         @Override
         public void initialize(String exportId,
                 ExporterConfiguration exportConfiguration)
                 throws ConfigurationException {
-            LOG.debug(String.format(LOAD_EXPORTER_FMT, exportId));
+            log.debug("Load configuration for exporter '{}'", exportId);
 
             // Get the property defining the class name
             String className = getRequiredProperty(
                     props,
                     getExporterPropertyKey(exportId,
                             SUBCONF_KEY_CLASSNAME), "",
                     String.class);
-            if(LOG.isDebugEnabled()) {
-                LOG.debug("Using class:'"+className+"'"+" for exporter:'"+exportId+"'");
-            }
+            log.debug("Using class:'{}' for exporter:'{}'", className, exportId);
             exportConfiguration.setClassName(className);
 
             // Get the property defining whether only sample series
             // are filtered
             boolean filtersOnlySampleSeries = getRequiredProperty(
                     props,
                     getExporterPropertyKey(exportId,
                             EXPORTER_KEY_FILTERS_ONLY_SAMPLE_SERIES),
                     EXPORTER_KEY_FILTERS_ONLY_SAMPLE_SERIES_DEFAULT,
                     Boolean.class).booleanValue();
             exportConfiguration
                     .filtersOnlySampleSeries(filtersOnlySampleSeries);
 
             // Get the property defining the series filter
             String seriesFilter = getRequiredProperty(
                     props,
                     getExporterPropertyKey(exportId,
                             EXPORTER_KEY_SERIES_FILTER),
                     EXPORTER_KEY_SERIES_FILTER_DEFAULT,
                     String.class);
             exportConfiguration.setSeriesFilter(seriesFilter);
 
             // Get the property defining whether only controllers
             // series are shown
             boolean showControllerSeriesOnly = getRequiredProperty(
                     props,
                     getExporterPropertyKey(exportId,
                             EXPORTER_KEY_SHOW_CONTROLLERS_ONLY),
                     EXPORTER_KEY_SHOW_CONTROLLERS_ONLY_DEFAULT,
                     Boolean.class).booleanValue();
             exportConfiguration
                     .showControllerSeriesOnly(showControllerSeriesOnly);
 
             // Load graph extra properties
             Map<String, SubConfiguration> graphExtraConfigurations = exportConfiguration
                     .getGraphExtraConfigurations();
             loadSubConfiguration(
                     graphExtraConfigurations,
                     props,
                     getSubConfigurationPropertyKey(
                             REPORT_GENERATOR_EXPORTER_KEY_PREFIX,
                             exportId,
                             EXPORTER_KEY_GRAPH_EXTRA_OPTIONS),
                     true,
                     new SubConfigurationFactory<SubConfiguration>() {
 
                         @Override
                         public SubConfiguration createSubConfiguration() {
                             return new SubConfiguration();
                         }
 
                         @Override
                         public void initialize(String subConfId,
                                 SubConfiguration subConfiguration) {
                             // do nothing
                         }
                     });
         }
     }
 
     private static final class GraphConfigurationFactory implements
             SubConfigurationFactory<GraphConfiguration> {
         private final Props props;
 
         private GraphConfigurationFactory(Props props) {
             this.props = props;
         }
 
         @Override
         public GraphConfiguration createSubConfiguration() {
             return new GraphConfiguration();
         }
 
         @Override
         public void initialize(String graphId,
                 GraphConfiguration graphConfiguration)
                 throws ConfigurationException {
-            LOG.debug(String.format(LOAD_GRAPH_FMT, graphId));
+            log.debug("Load configuration for graph '{}'", graphId);
 
             // Get the property defining whether the graph have to
             // filter controller samples
             boolean excludeControllers = getRequiredProperty(
                     props,
                     getGraphPropertyKey(graphId,
                             GRAPH_KEY_EXCLUDE_CONTROLLERS),
                     GRAPH_KEY_EXCLUDE_CONTROLLERS_DEFAULT,
                     Boolean.class).booleanValue();
             graphConfiguration
                     .setExcludeControllers(excludeControllers);
 
             // Get the property defining the title of the graph
             String title = getRequiredProperty(props,
                     getGraphPropertyKey(graphId, GRAPH_KEY_TITLE),
                     GRAPH_KEY_TITLE_DEFAULT, String.class);
             graphConfiguration.setTitle(title);
 
             // Get the property defining the class name
             String className = getRequiredProperty(
                     props,
                     getGraphPropertyKey(graphId,
                             SUBCONF_KEY_CLASSNAME), "",
                     String.class);
-            if(LOG.isDebugEnabled()) {
-                LOG.debug("Using class:'"+className+"' for graph:'"+title+"' with id:'"+graphId+"'");
-            }
+            log.debug("Using class:'{}' for graph:'{}' with id:'{}'", className, title, graphId);
             graphConfiguration.setClassName(className);
 
         }
     }
 
     /**
      * A factory for creating SubConfiguration objects.
      *
      * @param <T>
      *            the generic type
      */
     private interface SubConfigurationFactory<T extends SubConfiguration> {
         T createSubConfiguration();
 
         void initialize(String subConfId, T subConfiguration)
                 throws ConfigurationException;
     }
     private String reportTitle;
     private Date startDate;
     private Date endDate;
     private String sampleFilter;
     private File tempDirectory;
     private long apdexSatisfiedThreshold;
     private long apdexToleratedThreshold;
     private Pattern filteredSamplesPattern;
     private boolean ignoreTCFromTop5ErrorsBySampler;
     private Map<String, ExporterConfiguration> exportConfigurations = new HashMap<>();
     private Map<String, GraphConfiguration> graphConfigurations = new HashMap<>();
 
     /**
      * Gets the overall sample filter.
      *
      * @return the overall sample filter
      */
     public final String getSampleFilter() {
         return sampleFilter;
     }
 
     /**
      * Sets the overall sample filter.
      *
      * @param sampleFilter
      *            the new overall sample filter
      */
     public final void setSampleFilter(String sampleFilter) {
         this.sampleFilter = sampleFilter;
     }
 
     /**
      * Gets the temporary directory.
      *
      * @return the temporary directory
      */
     public final File getTempDirectory() {
         return tempDirectory;
     }
 
     /**
      * Sets the temporary directory.
      *
      * @param tempDirectory
      *            the temporary directory to set
      */
     public final void setTempDirectory(File tempDirectory) {
         this.tempDirectory = tempDirectory;
     }
 
     /**
      * Gets the apdex satisfied threshold.
      *
      * @return the apdex satisfied threshold
      */
     public final long getApdexSatisfiedThreshold() {
         return apdexSatisfiedThreshold;
     }
 
     /**
      * Sets the apdex satisfied threshold.
      *
      * @param apdexSatisfiedThreshold
      *            the apdex satisfied threshold to set
      */
     public final void setApdexSatisfiedThreshold(long apdexSatisfiedThreshold) {
         this.apdexSatisfiedThreshold = apdexSatisfiedThreshold;
     }
 
     /**
      * Gets the apdex tolerated threshold.
      *
      * @return the apdex tolerated threshold
      */
     public final long getApdexToleratedThreshold() {
         return apdexToleratedThreshold;
     }
 
     /**
      * Sets the apdex tolerated threshold.
      *
      * @param apdexToleratedThreshold
      *            the apdex tolerated threshold to set
      */
     public final void setApdexToleratedThreshold(long apdexToleratedThreshold) {
         this.apdexToleratedThreshold = apdexToleratedThreshold;
     }
 
     /**
      * Gets the export configurations.
      *
      * @return the export configurations
      */
     public final Map<String, ExporterConfiguration> getExportConfigurations() {
         return exportConfigurations;
     }
 
     /**
      * Gets the graph configurations.
      *
      * @return the graph configurations
      */
     public final Map<String, GraphConfiguration> getGraphConfigurations() {
         return graphConfigurations;
     }
 
     /**
      * Gets the sub configuration property prefix from the specified key
      * prefix and sub configuration identifier.
      *
      * @param keyPrefix
      *            the key prefix
      * @param subConfId
      *            the sub configuration identifier
      * @return the sub configuration property prefix
      */
     public static String getSubConfigurationPropertyPrefix(String keyPrefix,
             String subConfId) {
         return keyPrefix + KEY_DELIMITER + subConfId;
     }
 
     /**
      * Gets the sub configuration property key from the specified key
      * prefix, sub configuration identifier and property name.
      *
      * @param keyPrefix
      *            the key prefix
      * @param subConfId
      *            the sub configuration identifier
      * @param propertyName
      *            the property name
      * @return the sub configuration property key
      */
     public static String getSubConfigurationPropertyKey(String keyPrefix,
             String subConfId, String propertyName) {
         return getSubConfigurationPropertyPrefix(keyPrefix, subConfId)
                 + KEY_DELIMITER + propertyName;
     }
 
     /**
      * Gets the exporter property key from the specified identifier and property
      * name.
      *
      * @param exporterId
      *            the exporter identifier
      * @param propertyName
      *            the property name
      * @return the exporter property key
      */
     public static String getExporterPropertyKey(String exporterId,
             String propertyName) {
         return getSubConfigurationPropertyPrefix(
                 REPORT_GENERATOR_EXPORTER_KEY_PREFIX, exporterId)
                 + KEY_DELIMITER + propertyName;
     }
 
     /**
      * Gets the graph property key from the specified identifier and property
      * name.
      *
      * @param graphId
      *            the graph identifier
      * @param propertyName
      *            the property name
      * @return the graph property key
      */
     public static String getGraphPropertyKey(String graphId, String propertyName) {
         return getSubConfigurationPropertyPrefix(
                 REPORT_GENERATOR_GRAPH_KEY_PREFIX, graphId)
                 + KEY_DELIMITER
                 + propertyName;
     }
 
     /**
      * Gets the property matching the specified key in the properties and casts
      * it. Returns a default value is the key is not found.
      *
      * @param <TProperty>
      *            the target type
      * @param props
      *            the properties
      * @param key
      *            the key of the property
      * @param defaultValue
      *            the default value
      * @param clazz
      *            the target class
      * @return the property
      * @throws ConfigurationException
      *             thrown when the property cannot be cast to the specified type
      */
     private static <TProperty> TProperty getProperty(Props props, String key,
             TProperty defaultValue, Class<TProperty> clazz)
             throws ConfigurationException {
         String value = props.getValue(key);
         if (value == null) {
-            LOG.info(String.format(NOT_FOUND_PROPERTY_FMT, key,
-                    defaultValue));
+            log.info("Property '{}' not found, using default value '{}' instead.", key, defaultValue);
             return defaultValue;
         }
         return ConfigurationUtils.convert(value, clazz);
     }
 
     private static <TProperty> TProperty getOptionalProperty(Props props,
             String key, Class<TProperty> clazz) throws ConfigurationException {
         TProperty property = getProperty(props, key, null, clazz);
         if (property != null) {
-            LOG.debug(String.format(OPTIONAL_PROPERTY_FMT, property, key));
+            log.debug("Use '{}' value for optional property '{}'", property, key);
         }
         return property;
     }
 
     private static <TProperty> TProperty getRequiredProperty(Props props,
             String key, TProperty defaultValue, Class<TProperty> clazz)
             throws ConfigurationException {
         TProperty property = getProperty(props, key, defaultValue, clazz);
-        LOG.debug(String.format(REQUIRED_PROPERTY_FMT, property, key));
+        log.debug("Use '{}' value for required property '{}'", property, key);
         return property;
     }
 
     /**
      * * Initialize sub configuration items. This function iterates over
      * properties and find each direct sub properties with the specified prefix
      * 
      * <p>
      * E.g. :
      * </p>
      * 
      * <p>
      * With properties :
      * <ul>
      * <li>jmeter.reportgenerator.graph.graph1.title</li>
      * <li>jmeter.reportgenerator.graph.graph1.min_abscissa</li>
      * <li>jmeter.reportgenerator.graph.graph2.title</li>
      * </ul>
      * </p>
      * <p>
      * And prefix : jmeter.reportgenerator.graph
      * </p>
      * 
      * <p>
      * The function creates 2 sub configuration items : graph1 and graph2
      * </p>
      *
      * @param <TSubConf>
      *            the generic type
      * @param subConfigurations
      *            the sub configurations
      * @param props
      *            the props
      * @param propertyPrefix
      *            the property prefix
      * @param factory
      *            the factory
      * @param noPropertyKey
      *            indicates whether extra properties are prefixed with the
      *            SUBCONF_KEY_PROPERTY
      * @throws ConfigurationException
      *             the configuration exception
      */
     private static <TSubConf extends SubConfiguration> void loadSubConfiguration(
             Map<String, TSubConf> subConfigurations, Props props,
             String propertyPrefix, boolean noPropertyKey,
             SubConfigurationFactory<TSubConf> factory)
             throws ConfigurationException {
 
         for (Map.Entry<String, Object> entry : props.innerMap(propertyPrefix)
                 .entrySet()) {
             String key = entry.getKey();
             int index = key.indexOf(KEY_DELIMITER);
             if (index > 0) {
                 String name = key.substring(0, index);
                 TSubConf subConfiguration = subConfigurations.get(name);
                 if (subConfiguration == null) {
                     subConfiguration = factory.createSubConfiguration();
                     subConfigurations.put(name, subConfiguration);
                 }
             } else {
-                LOG.warn(String.format(INVALID_KEY_FMT, key));
+                log.warn("Invalid property '{}', skip it.", key);
             }
         }
 
         // Load sub configurations
         for (Map.Entry<String, TSubConf> entry : subConfigurations.entrySet()) {
             String subConfId = entry.getKey();
             final TSubConf subConfiguration = entry.getValue();
 
             // Load specific properties
             factory.initialize(subConfId, subConfiguration);
 
             // Load extra properties
             Map<String, Object> extraKeys = props
                     .innerMap(noPropertyKey ? getSubConfigurationPropertyPrefix(
                             propertyPrefix, subConfId)
                             : getSubConfigurationPropertyKey(propertyPrefix,
                                     subConfId, SUBCONF_KEY_PROPERTY));
             Map<String, String> extraProperties = subConfiguration
                     .getProperties();
             for (Map.Entry<String, Object> entryProperty : extraKeys.entrySet()) {
                 extraProperties.put(entryProperty.getKey(),
                         (String) entryProperty.getValue());
             }
         }
     }
 
     /**
      * Load a configuration from the specified properties.
      *
      * @param properties
      *            the properties
      * @return the report generator configuration
      * @throws ConfigurationException
      *             when mandatory properties are missing
      */
     public static ReportGeneratorConfiguration loadFromProperties(
             Properties properties) throws ConfigurationException {
 
-        LOG.debug(START_LOADING_MSG);
+        log.debug("Report generator properties loading");
 
         ReportGeneratorConfiguration configuration = new ReportGeneratorConfiguration();
 
         // Use jodd.Props to ease property handling
         final Props props = new Props();
-        if(LOG.isDebugEnabled()) {
-            LOG.debug("Loading properties:\r\n"+properties);
-        }
+        log.debug("Loading properties:\r\n{}", properties);
         props.load(properties);
 
         // Load temporary directory property
         final File tempDirectory = getRequiredProperty(props,
                 REPORT_GENERATOR_KEY_TEMP_DIR,
                 REPORT_GENERATOR_KEY_TEMP_DIR_DEFAULT, File.class);
         configuration.setTempDirectory(tempDirectory);
 
         // Load apdex statisfied threshold
         final long apdexSatisfiedThreshold = getRequiredProperty(props,
                 REPORT_GENERATOR_KEY_APDEX_SATISFIED_THRESHOLD,
                 REPORT_GENERATOR_KEY_APDEX_SATISFIED_THRESHOLD_DEFAULT,
                 long.class).longValue();
         configuration.setApdexSatisfiedThreshold(apdexSatisfiedThreshold);
 
         // Load apdex tolerated threshold
         final long apdexToleratedThreshold = getRequiredProperty(props,
                 REPORT_GENERATOR_KEY_APDEX_TOLERATED_THRESHOLD,
                 REPORT_GENERATOR_KEY_APDEX_TOLERATED_THRESHOLD_DEFAULT,
                 long.class).longValue();
         configuration.setApdexToleratedThreshold(apdexToleratedThreshold);
 
         final boolean ignoreTCFromTop5ErrorsBySampler = getRequiredProperty(
                 props, 
                 REPORT_GENERATOR_KEY_EXCLUDE_TC_FROM_TOP5_ERRORS_BY_SAMPLER,
                 Boolean.TRUE,
                 Boolean.class).booleanValue();
         configuration.setIgnoreTCFromTop5ErrorsBySampler(ignoreTCFromTop5ErrorsBySampler);
         
         // Load sample filter
         final String sampleFilter = getOptionalProperty(props,
                 REPORT_GENERATOR_KEY_SAMPLE_FILTER, String.class);
         configuration.setSampleFilter(sampleFilter);
 
         final String reportTitle = getOptionalProperty(props,
                 REPORT_GENERATOR_KEY_REPORT_TITLE, String.class);
         configuration.setReportTitle(reportTitle);
 
         Date reportStartDate = null;
         Date reportEndDate = null;
         final String startDateValue = getOptionalProperty(props,
                 REPORT_GENERATOR_KEY_START_DATE, String.class);
         final String endDateValue = getOptionalProperty(props,
                 REPORT_GENERATOR_KEY_END_DATE, String.class);
 
         String rangeDateFormat = getOptionalProperty(props, REPORT_GENERATOR_KEY_RANGE_DATE_FORMAT, String.class);
         if (StringUtils.isEmpty(rangeDateFormat)) {
             rangeDateFormat = RANGE_DATE_FORMAT_DEFAULT;
         }
         SimpleDateFormat dateFormat = new SimpleDateFormat(rangeDateFormat, Locale.ENGLISH);
 
         try {
             if(!StringUtils.isEmpty(startDateValue)) {
                 reportStartDate = dateFormat.parse(startDateValue);
                 configuration.setStartDate(reportStartDate);
             }
         } catch (ParseException e) {
-            LOG.error("Error parsing property " + REPORT_GENERATOR_KEY_START_DATE + " with value: " + startDateValue
-                    + " using format: " + rangeDateFormat, e);
+            log.error("Error parsing property {} with value: {} using format: {}", REPORT_GENERATOR_KEY_START_DATE,
+                    startDateValue, rangeDateFormat, e);
         }
         try {
             if(!StringUtils.isEmpty(endDateValue)) {
                 reportEndDate = dateFormat.parse(endDateValue);
                 configuration.setEndDate(reportEndDate);
             }
         } catch (ParseException e) {
-            LOG.error("Error parsing property " + REPORT_GENERATOR_KEY_END_DATE + " with value: " + endDateValue 
-                    + " using format: " + rangeDateFormat, e);
+            log.error("Error parsing property {} with value: {} using format: {}", REPORT_GENERATOR_KEY_END_DATE,
+                    endDateValue, rangeDateFormat, e);
         }
         
-        LOG.info("Will use date range start date: " + startDateValue + ", end date: " + endDateValue);
+        log.info("Will use date range start date: {}, end date: {}", startDateValue, endDateValue);
 
         // Find graph identifiers and load a configuration for each
         final Map<String, GraphConfiguration> graphConfigurations = configuration
                 .getGraphConfigurations();
         loadSubConfiguration(graphConfigurations, props,
                 REPORT_GENERATOR_GRAPH_KEY_PREFIX, false,
                 new GraphConfigurationFactory(props));
 
         if (graphConfigurations.isEmpty()) {
-            LOG.info("No graph configuration found.");
+            log.info("No graph configuration found.");
         }
 
         // Find exporter identifiers and load a configuration for each
         final Map<String, ExporterConfiguration> exportConfigurations = configuration
                 .getExportConfigurations();
         loadSubConfiguration(exportConfigurations, props,
                 REPORT_GENERATOR_EXPORTER_KEY_PREFIX, false,
                 new ExporterConfigurationFactory(props));
 
         if (exportConfigurations.isEmpty()) {
-            LOG.warn("No export configuration found. No report will be generated.");
+            log.warn("No export configuration found. No report will be generated.");
         }
 
-        LOG.debug(END_LOADING_MSG);
+        log.debug("End of report generator properties loading");
 
         return configuration;
     }
 
     /**
      * @return the reportTitle
      */
     public String getReportTitle() {
         return reportTitle;
     }
 
     /**
      * @param reportTitle the reportTitle to set
      */
     public void setReportTitle(String reportTitle) {
         this.reportTitle = reportTitle;
     }
 
     /**
      * @return the filteredSamplesPattern
      */
     public Pattern getFilteredSamplesPattern() {
         if(StringUtils.isEmpty(sampleFilter)) {
             return null;
         }
         if(filteredSamplesPattern == null) {
             filteredSamplesPattern = Pattern.compile(sampleFilter);
         }
         return filteredSamplesPattern;
     }
 
     /**
      * @return the start date to use to generate the report
      */
     public Date getStartDate() {
         return startDate;
     }
 
     /**
      * @param startDate the start date to use to generate the report
      */
     public void setStartDate(Date startDate) {
         this.startDate = startDate;
     }
 
     /**
      * @return the end date to use to generate the report
      */
     public Date getEndDate() {
         return endDate;
     }
 
     /**
      * @param endDate the end date to use to generate the report
      */
     public void setEndDate(Date endDate) {
         this.endDate = endDate;
     }
 
     /**
      * @return the ignoreTCFromTop5ErrorsBySampler
      */
     public boolean isIgnoreTCFromTop5ErrorsBySampler() {
         return ignoreTCFromTop5ErrorsBySampler;
     }
 
     /**
      * @param ignoreTCFromTop5ErrorsBySampler the ignoreTCFromTop5ErrorsBySampler to set
      */
     public void setIgnoreTCFromTop5ErrorsBySampler(
             boolean ignoreTCFromTop5ErrorsBySampler) {
         this.ignoreTCFromTop5ErrorsBySampler = ignoreTCFromTop5ErrorsBySampler;
     }
 }
diff --git a/src/core/org/apache/jmeter/report/core/AbstractSampleWriter.java b/src/core/org/apache/jmeter/report/core/AbstractSampleWriter.java
index ff8c2e173..bff441557 100644
--- a/src/core/org/apache/jmeter/report/core/AbstractSampleWriter.java
+++ b/src/core/org/apache/jmeter/report/core/AbstractSampleWriter.java
@@ -1,130 +1,131 @@
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
 
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.OutputStream;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.io.UnsupportedEncodingException;
 import java.io.Writer;
 import java.nio.charset.StandardCharsets;
 
 import org.apache.commons.lang3.Validate;
 import org.apache.jmeter.save.SaveService;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Base class for implementing sample writer.<br>
  * <p>
  * Handles buffering and output writer replacement.<br>
  * </p>
  * <p>
  * When a writer is set on the sample writer any previous writer is flushed and
  * closed before beeing replaced by the new one.
  * </p>
  * 
  * @since 3.0
  */
 public abstract class AbstractSampleWriter extends SampleWriter {
 
     private static final int BUF_SIZE = 10000;
 
     private static final String CHARSET = SaveService.getFileEncoding(StandardCharsets.UTF_8.displayName());
     
-    private static org.apache.log.Logger log = LoggingManager.getLoggerForClass();
+    private static Logger log = LoggerFactory.getLogger(AbstractSampleWriter.class);
 
     /** output writer to write samples to */
     protected PrintWriter writer;
 
     /**
      * Set the new writer on which samples will be written by this sample writer.<br>
      * If any writer exists on the sample writer, it is flushed and closed before
      * being replaced by the new one.
      * 
      * @param newWriter
      *            The destination writer where samples will be written by this
      *            sample writer
      */
     public void setWriter(Writer newWriter) {
         Validate.notNull(newWriter, "writer must not be null."); // NOSONAR
 
         if (this.writer != null) {
             // flush and close previous writer
             JOrphanUtils.closeQuietly(this.writer);
         }
         this.writer = new PrintWriter(new BufferedWriter(newWriter, BUF_SIZE), false);
     }
 
     /**
      * Instructs this sample writer to write samples on the specified output
      * with UTG-8 encoding. The encoding can be overriden by the user through
      * {@link SaveService#getFileEncoding(String)}
      * 
      * @param out
      *            The output stream on which sample should be written
      */
     public void setOutputStream(OutputStream out) {
         Validate.notNull(out, "out must not be null."); // NOSONAR
 
         try {
             setWriter(new OutputStreamWriter(out, CHARSET));
         } catch (UnsupportedEncodingException e) {
-            log.warn("Unsupported CHARSET: " + CHARSET, e);
+            log.warn("Unsupported CHARSET: {}", CHARSET, e);
         }
     }
 
     /**
      * Set the destination file in which this sample writer will write samples
      * 
      * @param output
      *            The ouput file that will receive samples written by this
      *            sample writter
      */
     public void setOutputFile(File output) {
         FileOutputStream fos = null;
         try {
             fos = new FileOutputStream(output); // NOSONAR
         } catch (Exception e) {
             throw new SampleException(e.getMessage(), e);
         }
         setOutputStream(fos);
     }
 
     /**
      * This method is guaranted to not throw any exception. If writer is already
      * closed then does nothing.<br>
      * Any buffered data is flushed by this method.
      */
     @Override
     public void close() {
         JOrphanUtils.closeQuietly(writer);
         this.writer = null;
     }
 
     /**
      * flush writer.
      * Only used for Tests
      */
     void flush() {
         writer.flush();
     }
 }
diff --git a/src/core/org/apache/jmeter/report/core/CsvSampleReader.java b/src/core/org/apache/jmeter/report/core/CsvSampleReader.java
index f43c4c8d7..4f68039b3 100644
--- a/src/core/org/apache/jmeter/report/core/CsvSampleReader.java
+++ b/src/core/org/apache/jmeter/report/core/CsvSampleReader.java
@@ -1,217 +1,221 @@
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
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Reader class for reading CSV files.
  * <p>
  * Handles {@link SampleMetadata} reading and sample extraction.
  * </p>
  * 
  * @since 3.0
  */
 public class CsvSampleReader implements Closeable{
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(CsvSampleReader.class);
     private static final int BUF_SIZE = 1024 * 1024;
 
     private static final String CHARSET = SaveService.getFileEncoding(StandardCharsets.UTF_8.displayName());
 
     private static final char DEFAULT_SEPARATOR =
             // We cannot use JMeterUtils#getPropDefault as it applies a trim on value
             JMeterUtils.getDelimiter(
                     JMeterUtils.getJMeterProperties().getProperty(SampleSaveConfiguration.DEFAULT_DELIMITER_PROP, SampleSaveConfiguration.DEFAULT_DELIMITER)).charAt(0);
 
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
-                LOG.warn("File '"+file.getAbsolutePath()+"' does not contain the field names header, "
-                        + "ensure the jmeter.save.saveservice.* properties are the same as when the CSV file was created or the file may be read incorrectly");
+                if (log.isWarnEnabled()) {
+                    log.warn(
+                            "File '{}' does not contain the field names header, "
+                                    + "ensure the jmeter.save.saveservice.* properties are the same as when the CSV file was created or the file may be read incorrectly",
+                            file.getAbsolutePath());
+                }
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
diff --git a/src/core/org/apache/jmeter/report/dashboard/HtmlTemplateExporter.java b/src/core/org/apache/jmeter/report/dashboard/HtmlTemplateExporter.java
index b80af1b73..bdfa1b69f 100644
--- a/src/core/org/apache/jmeter/report/dashboard/HtmlTemplateExporter.java
+++ b/src/core/org/apache/jmeter/report/dashboard/HtmlTemplateExporter.java
@@ -1,497 +1,498 @@
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
 import java.io.IOException;
 import java.nio.file.Files;
 import java.util.Map;
 import java.util.TimeZone;
 import java.util.regex.Pattern;
 import java.util.regex.PatternSyntaxException;
 
 import org.apache.commons.io.FileUtils;
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.commons.lang3.Validate;
 import org.apache.jmeter.JMeter;
 import org.apache.jmeter.report.config.ConfigurationException;
 import org.apache.jmeter.report.config.ExporterConfiguration;
 import org.apache.jmeter.report.config.GraphConfiguration;
 import org.apache.jmeter.report.config.ReportGeneratorConfiguration;
 import org.apache.jmeter.report.config.SubConfiguration;
 import org.apache.jmeter.report.core.DataContext;
 import org.apache.jmeter.report.core.TimeHelper;
 import org.apache.jmeter.report.processor.ListResultData;
 import org.apache.jmeter.report.processor.MapResultData;
 import org.apache.jmeter.report.processor.ResultData;
 import org.apache.jmeter.report.processor.ResultDataVisitor;
 import org.apache.jmeter.report.processor.SampleContext;
 import org.apache.jmeter.report.processor.ValueResultData;
 import org.apache.jmeter.report.processor.graph.AbstractGraphConsumer;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import freemarker.template.Configuration;
 import freemarker.template.TemplateExceptionHandler;
 
 /**
  * The class HtmlTemplateExporter provides a data exporter that generates and
  * processes template files using freemarker.
  * 
  * @since 3.0
  */
 public class HtmlTemplateExporter extends AbstractDataExporter {
 
     /** Format used for non null check of parameters. */
     private static final String MUST_NOT_BE_NULL = "%s must not be null";
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HtmlTemplateExporter.class);
 
     public static final String DATA_CTX_REPORT_TITLE = "reportTitle";
     public static final String DATA_CTX_TESTFILE = "testFile";
     public static final String DATA_CTX_BEGINDATE = "beginDate";
     public static final String DATA_CTX_ENDDATE = "endDate";
     public static final String DATA_CTX_TIMEZONE = "timeZone";
     public static final String DATA_CTX_TIMEZONE_OFFSET = "timeZoneOffset";
     public static final String DATA_CTX_OVERALL_FILTER = "overallFilter";
     public static final String DATA_CTX_SHOW_CONTROLLERS_ONLY = "showControllersOnly";
     public static final String DATA_CTX_RESULT = "result";
     public static final String DATA_CTX_EXTRA_OPTIONS = "extraOptions";
     public static final String DATA_CTX_SERIES_FILTER = "seriesFilter";
     public static final String DATA_CTX_FILTERS_ONLY_SAMPLE_SERIES = "filtersOnlySampleSeries";
 
     public static final String TIMESTAMP_FORMAT_MS = "ms";
     private static final String INVALID_TEMPLATE_DIRECTORY_FMT = "\"%s\" is not a valid template directory";
     private static final String INVALID_PROPERTY_CONFIG_FMT = "Wrong property \"%s\" in \"%s\" export configuration";
     private static final String EMPTY_GRAPH_FMT = "The graph \"%s\" will be empty : %s";
 
     // Template directory
     private static final String TEMPLATE_DIR = "template_dir";
     private static final String TEMPLATE_DIR_NAME_DEFAULT = "report-template";
 
     // Output directory
     private static final String OUTPUT_DIR = "output_dir";
     // Default output folder name
     private static final String OUTPUT_DIR_NAME_DEFAULT = "report-output";
 
     /**
      * Adds to context the value surrounding it with quotes
      * @param key Key
      * @param value Value
      * @param context {@link DataContext}
      */
     private void addToContext(String key, Object value, DataContext context) {
         if (value instanceof String) {
             value = '"' + (String) value + '"';
         }
         context.put(key, value);
     }
 
     /**
      * This class allows to customize data before exporting them
      *
      */
     private interface ResultCustomizer {
         ResultData customizeResult(ResultData result);
     }
 
     /**
      * This class allows to inject graph_options properties to the exported data
      *
      */
     private class ExtraOptionsResultCustomizer implements ResultCustomizer {
         private SubConfiguration extraOptions;
 
         /**
          * Sets the extra options to inject in the result data
          * 
          * @param extraOptions
          */
         public final void setExtraOptions(SubConfiguration extraOptions) {
             this.extraOptions = extraOptions;
         }
 
         /*
          * (non-Javadoc)
          * 
          * @see org.apache.jmeter.report.dashboard.HtmlTemplateExporter.
          * ResultCustomizer#customizeResult(org.apache.jmeter.report.processor.
          * ResultData)
          */
         @Override
         public ResultData customizeResult(ResultData result) {
             MapResultData customizedResult = new MapResultData();
             customizedResult.setResult(DATA_CTX_RESULT, result);
             if (extraOptions != null) {
                 MapResultData extraResult = new MapResultData();
                 for (Map.Entry<String, String> extraEntry : extraOptions
                         .getProperties().entrySet()) {
                     extraResult.setResult(extraEntry.getKey(),
                             new ValueResultData(extraEntry.getValue()));
                 }
                 customizedResult.setResult(DATA_CTX_EXTRA_OPTIONS, extraResult);
             }
             return customizedResult;
         }
 
     }
 
     /**
      * This class allows to check exported data
      *
      */
     private interface ResultChecker {
         boolean checkResult(DataContext dataContext, ResultData result);
     }
 
     /**
      * This class allows to detect empty graphs
      *
      */
     private class EmptyGraphChecker implements ResultChecker {
 
         private final boolean filtersOnlySampleSeries;
         private final boolean showControllerSeriesOnly;
         private final Pattern filterPattern;
 
         private boolean excludesControllers;
         private String graphId;
 
         public final void setExcludesControllers(boolean excludesControllers) {
             this.excludesControllers = excludesControllers;
         }
 
         public final void setGraphId(String graphId) {
             this.graphId = graphId;
         }
 
         /**
          * Instantiates a new EmptyGraphChecker.
          * 
          * @param filtersOnlySampleSeries
          * @param showControllerSeriesOnly
          * @param filterPattern
          */
         public EmptyGraphChecker(boolean filtersOnlySampleSeries,
                 boolean showControllerSeriesOnly, Pattern filterPattern) {
             this.filtersOnlySampleSeries = filtersOnlySampleSeries;
             this.showControllerSeriesOnly = showControllerSeriesOnly;
             this.filterPattern = filterPattern;
         }
 
         /*
          * (non-Javadoc)
          * 
          * @see
          * org.apache.jmeter.report.dashboard.HtmlTemplateExporter.ResultChecker
          * #checkResult( org.apache.jmeter.report.core.DataContext dataContext, org.apache.jmeter.report.processor.ResultData)
          */
         @Override
         public boolean checkResult(DataContext dataContext, ResultData result) {
             Boolean supportsControllerDiscrimination = findValue(Boolean.class,
                     AbstractGraphConsumer.RESULT_SUPPORTS_CONTROLLERS_DISCRIMINATION,
                     result);
 
             if (supportsControllerDiscrimination.booleanValue() && showControllerSeriesOnly
                     && excludesControllers) {
                 // Exporter shows controller series only
                 // whereas the current graph support controller
                 // discrimination and excludes
                 // controllers
-                LOG.warn(ReportGeneratorConfiguration.EXPORTER_KEY_SHOW_CONTROLLERS_ONLY
-                        + " is set while the graph "+graphId+" excludes controllers.");
+                log.warn("{} is set while the graph {} excludes controllers.",
+                        ReportGeneratorConfiguration.EXPORTER_KEY_SHOW_CONTROLLERS_ONLY, graphId);
                 return false;
             } else {
                 if (filterPattern != null) {
                     // Detect whether none series matches
                     // the series filter.
                     ResultData seriesResult = findData(
                             AbstractGraphConsumer.RESULT_SERIES, result);
                     if (seriesResult instanceof ListResultData) {
 
                         // Try to find at least one pattern matching
                         ListResultData seriesList = (ListResultData) seriesResult;
                         int count = seriesList.getSize();
                         int index = 0;
                         boolean matches = false;
                         while (index < count && !matches) {
                             ResultData currentResult = seriesList.get(index);
                             if (currentResult instanceof MapResultData) {
                                 MapResultData seriesData = (MapResultData) currentResult;
                                 String name = findValue(String.class,
                                         AbstractGraphConsumer.RESULT_SERIES_NAME,
                                         seriesData);
 
                                 // Is the current series a controller series ?
                                 boolean isController = findValue(Boolean.class,
                                         AbstractGraphConsumer.RESULT_SERIES_IS_CONTROLLER,
                                         seriesData).booleanValue();
 
                                 matches = filterPattern.matcher(name).matches();
                                 if (matches) {
                                     // If the name matches pattern, other
                                     // properties can discard the series
                                     matches = !filtersOnlySampleSeries
                                             || !supportsControllerDiscrimination.booleanValue()
                                             || isController
                                             || !showControllerSeriesOnly;
                                 } else {
                                     // If the name does not match the pattern,
                                     // other properties can hold the series
                                     matches = filtersOnlySampleSeries
                                             && !supportsControllerDiscrimination.booleanValue();
                                 }
                             }
                             index++;
                         }
                         if (!matches) {
                             // None series matches the pattern
-                            LOG.warn("No serie matches the series_filter:"
-                                    + ReportGeneratorConfiguration.EXPORTER_KEY_SERIES_FILTER + " in graph:"+graphId);
+                            log.warn("No serie matches the series_filter: {} in graph: {}",
+                                    ReportGeneratorConfiguration.EXPORTER_KEY_SERIES_FILTER, graphId);
                             return false;
                         }
                     }
                 }
             }
             return true;
         }
     }
 
     private <TVisit> void addResultToContext(String resultKey,
             Map<String, Object> storage, DataContext dataContext,
             ResultDataVisitor<TVisit> visitor) {
         addResultToContext(resultKey, storage, dataContext, visitor, null,
                 null);
     }
 
     private <TVisit> void addResultToContext(String resultKey,
             Map<String, Object> storage, DataContext dataContext,
             ResultDataVisitor<TVisit> visitor, ResultCustomizer customizer,
             ResultChecker checker) {
         Object data = storage.get(resultKey);
         if (data instanceof ResultData) {
             ResultData result = (ResultData) data;
             if (checker != null) {
                 checker.checkResult(dataContext, result);
             }
             if (customizer != null) {
                 result = customizer.customizeResult(result);
             }
             dataContext.put(resultKey, result.accept(visitor));
         }
     }
 
     private long formatTimestamp(String key, DataContext context) {
         // FIXME Why convert to double then long (rounding ?)
         double result = Double.parseDouble((String) context.get(key));
         long timestamp = (long) result;
         // Quote the string to respect Json spec.
         context.put(key, '"' + TimeHelper.formatTimeStamp(timestamp) + '"');
         return timestamp;
     }
 
     private <TProperty> TProperty getPropertyFromConfig(SubConfiguration cfg,
             String property, TProperty defaultValue, Class<TProperty> clazz)
                     throws ExportException {
         try {
             return cfg.getProperty(property, defaultValue, clazz);
         } catch (ConfigurationException ex) {
             throw new ExportException(String.format(INVALID_PROPERTY_CONFIG_FMT,
                     property, getName()), ex);
         }
     }
 
     /*
      * (non-Javadoc)
      * 
      * @see
      * org.apache.jmeter.report.dashboard.DataExporter#Export(org.apache.jmeter
      * .report.processor.SampleContext,
      * org.apache.jmeter.report.config.ReportGeneratorConfiguration)
      */
     @Override
     public void export(SampleContext context, File file,
             ReportGeneratorConfiguration configuration) throws ExportException {
         Validate.notNull(context, MUST_NOT_BE_NULL, "context");
         Validate.notNull(file, MUST_NOT_BE_NULL, "file");
         Validate.notNull(configuration, MUST_NOT_BE_NULL, "configuration");
 
-        LOG.debug("Start template processing");
+        log.debug("Start template processing");
 
         // Create data context and populate it
         DataContext dataContext = new DataContext();
 
         // Get the configuration of the current exporter
         final ExporterConfiguration exportCfg = configuration
                 .getExportConfigurations().get(getName());
 
         // Get template directory property value
         File templateDirectory = getPropertyFromConfig(exportCfg, TEMPLATE_DIR,
                 new File(JMeterUtils.getJMeterBinDir(), TEMPLATE_DIR_NAME_DEFAULT), File.class);
         if (!templateDirectory.isDirectory()) {
             String message = String.format(INVALID_TEMPLATE_DIRECTORY_FMT,
                     templateDirectory.getAbsolutePath());
-            LOG.error(message);
+            log.error(message);
             throw new ExportException(message);
         }
 
         // Get output directory property value
         File outputDir = getPropertyFromConfig(exportCfg, OUTPUT_DIR,
                 new File(JMeterUtils.getJMeterBinDir(), OUTPUT_DIR_NAME_DEFAULT), File.class);
         String globallyDefinedOutputDir = JMeterUtils.getProperty(JMeter.JMETER_REPORT_OUTPUT_DIR_PROPERTY);
         if(!StringUtils.isEmpty(globallyDefinedOutputDir)) {
             outputDir = new File(globallyDefinedOutputDir);
         }
         
         JOrphanUtils.canSafelyWriteToFolder(outputDir);
 
-        LOG.info("Will generate dashboard in folder:" + outputDir.getAbsolutePath());
+        if (log.isInfoEnabled()) {
+            log.info("Will generate dashboard in folder: {}", outputDir.getAbsolutePath());
+        }
 
         // Add the flag defining whether only sample series are filtered to the
         // context
         final boolean filtersOnlySampleSeries = exportCfg
                 .filtersOnlySampleSeries();
         addToContext(DATA_CTX_FILTERS_ONLY_SAMPLE_SERIES,
                 Boolean.valueOf(filtersOnlySampleSeries), dataContext);
 
         // Add the series filter to the context
         final String seriesFilter = exportCfg.getSeriesFilter();
         Pattern filterPattern = null;
         if (StringUtils.isNotBlank(seriesFilter)) {
             try {
                 filterPattern = Pattern.compile(seriesFilter);
             } catch (PatternSyntaxException ex) {
-                LOG.error(String.format("Invalid series filter: \"%s\", %s",
-                        seriesFilter, ex.getDescription()));
+                log.error("Invalid series filter: '{}', {}", seriesFilter, ex.getDescription());
             }
         }
         addToContext(DATA_CTX_SERIES_FILTER, seriesFilter, dataContext);
 
         // Add the flag defining whether only controller series are displayed
         final boolean showControllerSeriesOnly = exportCfg
                 .showControllerSeriesOnly();
         addToContext(DATA_CTX_SHOW_CONTROLLERS_ONLY,
                 Boolean.valueOf(showControllerSeriesOnly), dataContext);
 
         JsonizerVisitor jsonizer = new JsonizerVisitor();
         Map<String, Object> storedData = context.getData();
 
         // Add begin date consumer result to the data context
         addResultToContext(ReportGenerator.BEGIN_DATE_CONSUMER_NAME, storedData,
                 dataContext, jsonizer);
 
         // Add end date summary consumer result to the data context
         addResultToContext(ReportGenerator.END_DATE_CONSUMER_NAME, storedData,
                 dataContext, jsonizer);
 
         // Add Apdex summary consumer result to the data context
         addResultToContext(ReportGenerator.APDEX_SUMMARY_CONSUMER_NAME,
                 storedData, dataContext, jsonizer);
 
         // Add errors summary consumer result to the data context
         addResultToContext(ReportGenerator.ERRORS_SUMMARY_CONSUMER_NAME,
                 storedData, dataContext, jsonizer);
 
         // Add requests summary consumer result to the data context
         addResultToContext(ReportGenerator.REQUESTS_SUMMARY_CONSUMER_NAME,
                 storedData, dataContext, jsonizer);
 
         // Add statistics summary consumer result to the data context
         addResultToContext(ReportGenerator.STATISTICS_SUMMARY_CONSUMER_NAME,
                 storedData, dataContext, jsonizer);
 
         // Add Top 5 errors by sampler consumer result to the data context
         addResultToContext(ReportGenerator.TOP5_ERRORS_BY_SAMPLER_CONSUMER_NAME,
                 storedData, dataContext, jsonizer);
 
         // Collect graph results from sample context and transform them into
         // Json strings to inject in the data context
         ExtraOptionsResultCustomizer customizer = new ExtraOptionsResultCustomizer();
         EmptyGraphChecker checker = new EmptyGraphChecker(
                 filtersOnlySampleSeries, showControllerSeriesOnly,
                 filterPattern);
         for (Map.Entry<String, GraphConfiguration> graphEntry : configuration
                 .getGraphConfigurations().entrySet()) {
             final String graphId = graphEntry.getKey();
             final GraphConfiguration graphConfiguration = graphEntry.getValue();
             final SubConfiguration extraOptions = exportCfg
                     .getGraphExtraConfigurations().get(graphId);
 
             // Initialize customizer and checker
             customizer.setExtraOptions(extraOptions);
             checker.setExcludesControllers(
                     graphConfiguration.excludesControllers());
             checker.setGraphId(graphId);
 
             // Export graph data
             addResultToContext(graphId, storedData, dataContext, jsonizer,
                     customizer, checker);
         }
 
         // Replace the begin date with its formatted string and store the old
         // timestamp
         long oldTimestamp = formatTimestamp(
                 ReportGenerator.BEGIN_DATE_CONSUMER_NAME, dataContext);
 
         // Replace the end date with its formatted string
         formatTimestamp(ReportGenerator.END_DATE_CONSUMER_NAME, dataContext);
 
         // Add time zone offset (that matches the begin date) to the context
         TimeZone timezone = TimeZone.getDefault();
         addToContext(DATA_CTX_TIMEZONE_OFFSET,
                 Integer.valueOf(timezone.getOffset(oldTimestamp)), dataContext);
 
         // Add report title to the context
         if(!StringUtils.isEmpty(configuration.getReportTitle())) {
             dataContext.put(DATA_CTX_REPORT_TITLE, StringEscapeUtils.escapeHtml4(configuration.getReportTitle()));
         }
 
         // Add the test file name to the context
         addToContext(DATA_CTX_TESTFILE, file.getName(), dataContext);
 
         // Add the overall filter property to the context
         addToContext(DATA_CTX_OVERALL_FILTER, configuration.getSampleFilter(),
                 dataContext);
 
         // Walk template directory to copy files and process templated ones
         Configuration templateCfg = new Configuration(
                 Configuration.getVersion());
         try {
             templateCfg.setDirectoryForTemplateLoading(templateDirectory);
             templateCfg.setTemplateExceptionHandler(
                     TemplateExceptionHandler.RETHROW_HANDLER);
-            LOG.info(
-                    "Report will be generated in:" + outputDir.getAbsolutePath()
-                            + ", creating folder structure");
+            if (log.isInfoEnabled()) {
+                log.info("Report will be generated in: {}, creating folder structure", outputDir.getAbsolutePath());
+            }
             FileUtils.forceMkdir(outputDir);
             TemplateVisitor visitor = new TemplateVisitor(
                     templateDirectory.toPath(), outputDir.toPath(), templateCfg,
                     dataContext);
             Files.walkFileTree(templateDirectory.toPath(), visitor);
         } catch (IOException ex) {
             throw new ExportException("Unable to process template files.", ex);
         }
 
-        LOG.debug("End of template processing");
+        log.debug("End of template processing");
 
     }
 
 }
diff --git a/src/core/org/apache/jmeter/report/dashboard/ReportGenerator.java b/src/core/org/apache/jmeter/report/dashboard/ReportGenerator.java
index be55c33e8..64573e765 100644
--- a/src/core/org/apache/jmeter/report/dashboard/ReportGenerator.java
+++ b/src/core/org/apache/jmeter/report/dashboard/ReportGenerator.java
@@ -1,555 +1,551 @@
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
 import org.apache.jmeter.report.core.SampleException;
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
 import org.apache.jmeter.report.processor.SampleConsumer;
 import org.apache.jmeter.report.processor.SampleContext;
 import org.apache.jmeter.report.processor.SampleSource;
 import org.apache.jmeter.report.processor.StatisticsSummaryConsumer;
 import org.apache.jmeter.report.processor.Top5ErrorsBySamplerConsumer;
 import org.apache.jmeter.report.processor.graph.AbstractGraphConsumer;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The class ReportGenerator provides a way to generate all the templated files
  * of the plugin.
  * 
  * @since 3.0
  */
 public class ReportGenerator {
     private static final String REPORTGENERATOR_PROPERTIES = "reportgenerator.properties";
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ReportGenerator.class);
 
     private static final boolean CSV_OUTPUT_FORMAT = "csv"
             .equalsIgnoreCase(JMeterUtils.getPropDefault(
                     "jmeter.save.saveservice.output_format", "csv"));
 
     private static final char CSV_DEFAULT_SEPARATOR =
             // We cannot use JMeterUtils#getPropDefault as it applies a trim on value
             JMeterUtils.getDelimiter(
                     JMeterUtils.getJMeterProperties().getProperty(SampleSaveConfiguration.DEFAULT_DELIMITER_PROP, SampleSaveConfiguration.DEFAULT_DELIMITER)).charAt(0);
 
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
     public static final String TOP5_ERRORS_BY_SAMPLER_CONSUMER_NAME = "top5ErrorsBySampler";
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
 
-        LOG.info("ReportGenerator will use for Parsing the separator:'"+CSV_DEFAULT_SEPARATOR+"'");
+        log.info("ReportGenerator will use for Parsing the separator: '{}'", CSV_DEFAULT_SEPARATOR);
 
         File file = new File(resultsFile);
         if (resultCollector == null) {
             if (!(file.isFile() && file.canRead())) {
                 throw new IllegalArgumentException(String.format(
                         "Cannot read test results file : %s", file));
             }
-            LOG.info("Will only generate report from results file:"
-                    + resultsFile);
+            log.info("Will only generate report from results file: {}", resultsFile);
         } else {
             if (file.exists() && file.length() > 0) {
                 throw new IllegalArgumentException("Results file:"
                         + resultsFile + " is not empty");
             }
-            LOG.info("Will generate report at end of test from  results file:"
-                    + resultsFile);
+            log.info("Will generate report at end of test from  results file: {}", resultsFile);
         }
         this.resultCollector = resultCollector;
         this.testFile = file;
         final Properties merged = new Properties();
         File rgp = new File(JMeterUtils.getJMeterBinDir(), REPORTGENERATOR_PROPERTIES);
-        if(LOG.isInfoEnabled()) {
-            LOG.info("Reading report generator properties from:"+rgp.getAbsolutePath());
+        if(log.isInfoEnabled()) {
+            log.info("Reading report generator properties from: {}", rgp.getAbsolutePath());
         }
         merged.putAll(loadProps(rgp));
-        if(LOG.isInfoEnabled()) {
-            LOG.info("Merging with JMeter properties");
-        }
+        log.info("Merging with JMeter properties");
         merged.putAll(JMeterUtils.getJMeterProperties());
         configuration = ReportGeneratorConfiguration.loadFromProperties(merged);
     }
 
     private static Properties loadProps(File file) {
         final Properties props = new Properties();
         try (FileInputStream inStream = new FileInputStream(file)) {
             props.load(inStream);
         } catch (IOException e) {
-            LOG.error("Problem loading properties from file ", e);
-            System.err.println("Problem loading properties " + e); // NOSONAR
+            log.error("Problem loading properties from file.", e);
+            System.err.println("Problem loading properties. " + e); // NOSONAR
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
         StringBuffer buffer = new StringBuffer(); // NOSONAR Unfortunately Matcher does not support StringBuilder
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
-            LOG.info("Flushing result collector before report Generation");
+            log.info("Flushing result collector before report Generation");
             resultCollector.flushFile();
         }
-        LOG.debug("Start report generation");
+        log.debug("Start report generation");
 
         File tmpDir = configuration.getTempDirectory();
         boolean tmpDirCreated = createTempDir(tmpDir);
 
         // Build consumers chain
         SampleContext sampleContext = new SampleContext();
         sampleContext.setWorkingDirectory(tmpDir);
         SampleSource source = new CsvFileSampleSource(testFile, CSV_DEFAULT_SEPARATOR);
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
-        LOG.debug("Start samples processing");
+        log.debug("Start samples processing");
         try {
             source.run(); // NOSONAR
         } catch (SampleException ex) {
             throw new GenerationException("Error while processing samples:"+ex.getMessage(), ex);
         }
-        LOG.debug("End of samples processing");
+        log.debug("End of samples processing");
 
-        LOG.debug("Start data exporting");
+        log.debug("Start data exporting");
 
         // Process configuration to build data exporters
-        for (Map.Entry<String, ExporterConfiguration> entry : configuration
-                .getExportConfigurations().entrySet()) {
-            LOG.info("Exporting data using exporter:'"
-                +entry.getKey()+"' of className:'"+entry.getValue().getClassName()+"'");
-            exportData(sampleContext, entry.getKey(), entry.getValue());
+        String key;
+        ExporterConfiguration value;
+        for (Map.Entry<String, ExporterConfiguration> entry : configuration.getExportConfigurations().entrySet()) {
+            key = entry.getKey();
+            value = entry.getValue();
+            if (log.isInfoEnabled()) {
+                log.info("Exporting data using exporter:'{}' of className:'{}'", key, value.getClassName());
+            }
+            exportData(sampleContext, key, value);
         }
 
-        LOG.debug("End of data exporting");
+        log.debug("End of data exporting");
 
         removeTempDir(tmpDir, tmpDirCreated);
 
-        LOG.debug("End of report generation");
+        log.debug("End of report generation");
 
     }
 
     /**
      * @return {@link FilterConsumer} that filter data based on date range
      */
     private FilterConsumer createFilterByDateRange() {
         FilterConsumer dateRangeFilter = new FilterConsumer();
         dateRangeFilter.setName(DATE_RANGE_FILTER_CONSUMER_NAME);
         dateRangeFilter.setSamplePredicate(sample -> {
                 long sampleStartTime = sample.getStartTime();
                 if(configuration.getStartDate() != null) {
                     if(sampleStartTime >= configuration.getStartDate().getTime()) {
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
             });     
         return dateRangeFilter;
     }
 
     private void removeTempDir(File tmpDir, boolean tmpDirCreated) {
         if (tmpDirCreated) {
             try {
                 FileUtils.deleteDirectory(tmpDir);
             } catch (IOException ex) {
-                LOG.warn(String.format(
-                        "Cannot delete created temporary directory \"%s\".",
-                        tmpDir), ex);
+                log.warn("Cannot delete created temporary directory, '{}'.", tmpDir, ex);
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
-                LOG.error(message);
+                log.error(message);
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
-            LOG.error(error, ex);
+            log.error(error, ex);
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
-            LOG.error(error, ex);
+            log.error(error, ex);
             throw new GenerationException(error, ex);
         } catch (ExportException ex) {
             String error = String.format(INVALID_EXPORT_FMT, exporterName);
-            LOG.error(error, ex);
+            log.error(error, ex);
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
 
     private SampleConsumer createTop5ErrorsConsumer(ReportGeneratorConfiguration configuration) {
         Top5ErrorsBySamplerConsumer top5ErrorsBySamplerConsumer = new Top5ErrorsBySamplerConsumer();
         top5ErrorsBySamplerConsumer.setName(TOP5_ERRORS_BY_SAMPLER_CONSUMER_NAME);
         top5ErrorsBySamplerConsumer.setHasOverallResult(true);
         top5ErrorsBySamplerConsumer.setIgnoreTransactionController(configuration.isIgnoreTCFromTop5ErrorsBySampler());
         return top5ErrorsBySamplerConsumer;
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
         apdexSummaryConsumer.setThresholdSelector(sampleName -> {
                 ApdexThresholdsInfo info = new ApdexThresholdsInfo();
                 info.setSatisfiedThreshold(configuration
                         .getApdexSatisfiedThreshold());
                 info.setToleratedThreshold(configuration
                         .getApdexToleratedThreshold());
                 return info;
         });
         return apdexSummaryConsumer;
     }
 
     /**
      * @return a {@link FilterConsumer} that filters samplers based on their name
      */
     private FilterConsumer createNameFilter() {
         FilterConsumer nameFilter = new FilterConsumer();
         nameFilter.setName(NAME_FILTER_CONSUMER_NAME);
         nameFilter.setSamplePredicate(sample -> {
                 // Get filtered samples from configuration
                 Pattern filteredSamplesPattern = configuration
                         .getFilteredSamplesPattern();
                 // Sample is kept if no filter is set 
                 // or if its name matches the filter pattern
                 return filteredSamplesPattern == null 
                         || filteredSamplesPattern.matcher(sample.getName()).matches();
         });
         nameFilter.addSampleConsumer(createApdexSummaryConsumer());
         nameFilter.addSampleConsumer(createRequestsSummaryConsumer());
         nameFilter.addSampleConsumer(createStatisticsSummaryConsumer());
         nameFilter.addSampleConsumer(createTop5ErrorsConsumer(configuration));
         return nameFilter;
     }
 
     /**
      * @return Consumer that compute the end date of the test
      */
     private AggregateConsumer createEndDateConsumer() {
         AggregateConsumer endDateConsumer = new AggregateConsumer(
                 new MaxAggregator(), sample -> Double.valueOf(sample.getEndTime()));
         endDateConsumer.setName(END_DATE_CONSUMER_NAME);
         return endDateConsumer;
     }
 
     /**
      * @return Consumer that compute the begining date of the test
      */
     private AggregateConsumer createBeginDateConsumer() {
         AggregateConsumer beginDateConsumer = new AggregateConsumer(
                 new MinAggregator(), sample -> Double.valueOf(sample.getStartTime()));
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
-            LOG.warn(String
-                        .format("\"%s\" is not a valid property for class \"%s\", skip it",
-                                propertyName, className));
+            log.warn("'{}' is not a valid property for class '{}', skip it", propertyName, className);
         } catch (InvocationTargetException | ConvertException ex) {
             String message = String
                     .format("Cannot assign \"%s\" to property \"%s\" (mapped as \"%s\"), skip it",
                             propertyValue, propertyName, setterName);
-            LOG.error(message, ex);
+            log.error(message, ex);
             throw new GenerationException(message, ex);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/report/processor/NormalizerSampleConsumer.java b/src/core/org/apache/jmeter/report/processor/NormalizerSampleConsumer.java
index 1c1ebe04e..72b8c6808 100644
--- a/src/core/org/apache/jmeter/report/processor/NormalizerSampleConsumer.java
+++ b/src/core/org/apache/jmeter/report/processor/NormalizerSampleConsumer.java
@@ -1,127 +1,127 @@
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
 package org.apache.jmeter.report.processor;
 
 import java.text.SimpleDateFormat;
 import java.util.Date;
 
 import org.apache.jmeter.report.core.Sample;
 import org.apache.jmeter.report.core.SampleException;
 import org.apache.jmeter.report.core.SampleMetadata;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Consume samples using the JMeter timestamp property (defaulting to {@link SampleSaveConfiguration#MILLISECONDS}) and reproduce them as a long
  * value (for faster treatment later in the consuming chain).
  * 
  * @since 3.0
  */
 public class NormalizerSampleConsumer extends AbstractSampleConsumer {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(NormalizerSampleConsumer.class);
 
     private static final String TIMESTAMP_FORMAT = 
             JMeterUtils.getPropDefault(
                     "jmeter.save.saveservice.timestamp_format", // $NON-NLS-1$
                     SampleSaveConfiguration.MILLISECONDS);
 
     private static final String PARSE_TIMESTAMP_EXCEPTION_MESSAGE = 
             "Could not parse timeStamp <%s> using format defined by property jmeter.save.saveservice.timestamp_format=%s on sample %s ";
 
     /**
      * index of the timeStamp column
      */
     private int timestamp;
 
     /**
      * is format ms
      */
     private boolean isMillisFormat;
     
     /**
      * null if format is isMillisFormat is true
      */
     private final SimpleDateFormat dateFormat = createFormatter();
 
     private SampleMetadata sampleMetadata;
 
     @Override
     public void startConsuming() {
         sampleMetadata = getConsumedMetadata(0);
         timestamp = sampleMetadata.ensureIndexOf(CSVSaveService.TIME_STAMP);
         super.setProducedMetadata(sampleMetadata, 0);
         startProducing();
     }
 
     /**
      * @return null if format is ms or a SimpleDateFormat
      * @throws SampleException is format is none
      */
     private SimpleDateFormat createFormatter() {
         if(SampleSaveConfiguration.NONE.equalsIgnoreCase(TIMESTAMP_FORMAT)) {
             throw new SampleException("'none' format for 'jmeter.save.saveservice.timestamp_format' property is not accepted for report generation");
         }
-        log.info("Using format:"+TIMESTAMP_FORMAT+" to parse timeStamp field");
+        log.info("Using format, '{}', to parse timeStamp field", TIMESTAMP_FORMAT);
         
         isMillisFormat = SampleSaveConfiguration.MILLISECONDS.equalsIgnoreCase(TIMESTAMP_FORMAT);
         SimpleDateFormat formatter = null;
         // Prepare for a pretty date
         if (!isMillisFormat) {
             formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
         } 
         return formatter;
     }
 
     @Override
     public void consume(Sample s, int channel) {
         Date date = null;
         try {
             String tStr = s.getData(timestamp);
             if(isMillisFormat) {
                 date = new Date(Long.parseLong(tStr));
             } else {
                 date = dateFormat.parse(tStr);                    
             }
         } catch (Exception e) {
             throw new SampleException(String.format(
                     PARSE_TIMESTAMP_EXCEPTION_MESSAGE, s.getData(timestamp),
                     TIMESTAMP_FORMAT, s.toString()), e);
         }
         long time = date.getTime();
         int cc = sampleMetadata.getColumnCount();
         String[] data = new String[cc];
         for (int i = 0; i < cc; i++) {
             if (i == timestamp) {
                 data[i] = Long.toString(time);
             } else {
                 data[i] = s.getData(i);
             }
         }
         Sample rewritten = new Sample(s.getSampleRow(), sampleMetadata, data);
         super.produce(rewritten, 0);
     }
 
     @Override
     public void stopConsuming() {
         super.stopProducing();
     }
 }
diff --git a/src/core/org/apache/jmeter/report/processor/graph/AbstractVersusRequestsGraphConsumer.java b/src/core/org/apache/jmeter/report/processor/graph/AbstractVersusRequestsGraphConsumer.java
index 19ac0a6bc..37f995d46 100644
--- a/src/core/org/apache/jmeter/report/processor/graph/AbstractVersusRequestsGraphConsumer.java
+++ b/src/core/org/apache/jmeter/report/processor/graph/AbstractVersusRequestsGraphConsumer.java
@@ -1,363 +1,361 @@
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
 package org.apache.jmeter.report.processor.graph;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.HashMap;
 
 import org.apache.commons.io.FileUtils;
 import org.apache.jmeter.report.core.CsvSampleReader;
 import org.apache.jmeter.report.core.CsvSampleWriter;
 import org.apache.jmeter.report.core.Sample;
 import org.apache.jmeter.report.core.SampleBuilder;
 import org.apache.jmeter.report.core.SampleException;
 import org.apache.jmeter.report.core.SampleMetadata;
 import org.apache.jmeter.report.processor.AbstractSampleConsumer;
 import org.apache.jmeter.report.processor.MapResultData;
 import org.apache.jmeter.report.processor.ValueResultData;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The class AbstractOverTimeGraphConsumer provides a base class for over time
  * graphs.
  *
  * @since 3.0
  */
 public abstract class AbstractVersusRequestsGraphConsumer extends
         AbstractGraphConsumer {
     private static final Long ONE = Long.valueOf(1L);
     public static final String RESULT_CTX_GRANULARITY = "granularity";
     public static final String TIME_INTERVAL_LABEL = "Interval";
 
     private long granularity;
 
     /**
      * The embedded time count consumer is used to buffer (disk storage) and tag
      * samples with the number of samples in the same interval.
      */
     private final TimeCountConsumer embeddedConsumer;
 
     /**
      * Gets the granularity.
      *
      * @return the granularity
      */
     public long getGranularity() {
         return granularity;
     }
 
     /**
      * Sets the granularity.
      *
      * @param granularity
      *            the granularity to set
      */
     // Must be final because called by ctor
     public final void setGranularity(long granularity) {
         this.granularity = granularity;
     }
 
     /**
      * Instantiates a new abstract over time graph consumer.
      */
     protected AbstractVersusRequestsGraphConsumer() {
         embeddedConsumer = new TimeCountConsumer(this);
         setGranularity(1L);
     }
 
     /*
      * (non-Javadoc)
      * 
      * @see
      * org.apache.jmeter.report.processor.graph.AbstractGraphConsumer#startConsuming
      * ()
      */
     @Override
     public void startConsuming() {
         embeddedConsumer.startConsuming();
     }
 
     private void startConsumingBase() {
         super.startConsuming();
     }
 
     /*
      * (non-Javadoc)
      * 
      * @see
      * org.apache.jmeter.report.processor.AbstractSampleConsumer#setConsumedMetadata
      * (org.apache.jmeter.report.core.SampleMetadata, int)
      */
     @Override
     public void setConsumedMetadata(SampleMetadata sampleMetadata, int channel) {
         embeddedConsumer.setConsumedMetadata(sampleMetadata, channel);
     }
 
     private void setConsumedMetadataBase(SampleMetadata sampleMetadata,
             int channel) {
         super.setConsumedMetadata(sampleMetadata, channel);
     }
 
     /*
      * (non-Javadoc)
      * 
      * @see
      * org.apache.jmeter.report.processor.graph.AbstractGraphConsumer#consume
      * (org.apache.jmeter.report.core.Sample, int)
      */
     @Override
     public void consume(Sample sample, int channel) {
         embeddedConsumer.consume(sample, channel);
     }
 
     private void consumeBase(Sample sample, int channel) {
         super.consume(sample, channel);
     }
 
     /*
      * (non-Javadoc)
      * 
      * @see
      * org.apache.jmeter.report.processor.graph.AbstractGraphConsumer#stopConsuming
      * ()
      */
     @Override
     public void stopConsuming() {
         embeddedConsumer.stopConsuming();
     }
 
     public void stopConsumingBase() {
         super.stopConsuming();
     }
 
     /*
      * (non-Javadoc)
      * 
      * @see org.apache.jmeter.report.processor.graph.AbstractGraphConsumer#
      * initializeExtraResults(org.apache.jmeter.report.processor.MapResultData)
      */
     @Override
     protected void initializeExtraResults(MapResultData parentResult) {
         parentResult.setResult(RESULT_CTX_GRANULARITY, new ValueResultData(
                 Long.valueOf(granularity)));
     }
 
     private static class TimeCountConsumer extends AbstractSampleConsumer {
 
-        private static final Logger log = LoggingManager.getLoggerForClass();
+        private static final Logger log = LoggerFactory.getLogger(TimeCountConsumer.class);
 
         private class FileInfo {
             private final File file;
             private final CsvSampleWriter writer;
 
             /**
              * Instantiates a new file info.
              *
              * @param file
              *            the file
              * @param metadata
              *            the metadata
              */
             public FileInfo(File file, SampleMetadata metadata) {
                 this.file = file;
                 this.writer = new CsvSampleWriter(file, metadata);
             }
 
             /**
              * Gets the file.
              *
              * @return the file
              */
             public File getFile() {
                 return file;
             }
 
             /**
              * Gets the sample writer.
              *
              * @return the sample writer
              */
             public CsvSampleWriter getWriter() {
                 return writer;
             }
         }
 
         // Collection of sample builders for channels
         private ArrayList<SampleBuilder> builders = new ArrayList<>();
         private ArrayList<FileInfo> fileInfos = new ArrayList<>();
         private HashMap<Long, Long> counts = new HashMap<>();
         boolean createdWorkDir = false;
         private final AbstractVersusRequestsGraphConsumer parent;
 
         public TimeCountConsumer(AbstractVersusRequestsGraphConsumer parent) {
             this.parent = parent;
         }
 
         private Long getTimeInterval(Sample sample) {
             long time = sample.getEndTime();
             return Long.valueOf(time - (time % parent.getGranularity()));
         }
 
         // Adds a new field in the sample metadata for each channel
         private void initProducedMetadata() {
             builders.clear();
             int channelCount = getConsumedChannelCount();
             for (int i = 0; i < channelCount; i++) {
                 // Get the metadata for the current channel
                 SampleMetadata consumedMetadata = getConsumedMetadata(i);
 
                 // Copy metadata to an array
                 int colCount = consumedMetadata.getColumnCount();
                 String[] names = new String[colCount + 1];
                 for (int j = 0; j < colCount; j++) {
                     names[j] = consumedMetadata.getColumnName(j);
                 }
 
                 // Add the new field
                 names[colCount] = TIME_INTERVAL_LABEL;
 
                 // Build the produced metadata from the array
                 SampleMetadata producedMetadata = new SampleMetadata(
                         consumedMetadata.getSeparator(), names);
 
                 // Add a sample builder for the current channel
                 builders.add(new SampleBuilder(producedMetadata));
                 parent.setConsumedMetadataBase(producedMetadata, i);
             }
         }
 
         private Sample createIndexedSample(Sample sample, int channel,
                 double count) {
             SampleBuilder builder = builders.get(channel);
             SampleMetadata metadata = builder.getMetadata();
             int colCount = metadata.getColumnCount();
             for (int i = 0; i < colCount - 1; i++) {
                 builder.add(sample.getData(i));
             }
             builder.add(String.valueOf(count));
             return builder.build();
         }
 
         @Override
         public void startConsuming() {
 
             // Handle the working directory
             File workDir = parent.getWorkingDirectory();
             createdWorkDir = false;
             if (!workDir.exists()) {
                 createdWorkDir = workDir.mkdir();
                 if (!createdWorkDir) {
                     String message = String.format(
                             "Cannot create working directory \"%s\"",
                             workDir);
                     log.error(message);
                     throw new SampleException(message);
                 }
             }
 
             // Create a temporary file by channel to buffer samples
             int channelsCount = getConsumedChannelCount();
             for (int i = 0; i < channelsCount; i++) {
                 try {
                     File tmpFile = File.createTempFile(parent.getName(), "-"
                             + String.valueOf(i), workDir);
                     tmpFile.deleteOnExit();
                     fileInfos.add(new FileInfo(tmpFile, getConsumedMetadata(i)));
                 } catch (IOException ex) {
                     String message = String.format(
                             "Cannot create temporary file for channel #%d", Integer.valueOf(i));
                     log.error(message, ex);
                     throw new SampleException(message, ex);
                 }
             }
 
             // Override produced metadata
             initProducedMetadata();
         }
 
         /*
          * (non-Javadoc)
          * 
          * @see
          * org.apache.jmeter.report.processor.SampleConsumer#consume(org.apache.
          * jmeter.report.core.Sample, int)
          */
         @Override
         public void consume(Sample sample, int channel) {
             // Count sample depending on time interval
             Long time = getTimeInterval(sample);
             Long count = counts.get(time);
             if (count != null) {
                 counts.put(time, Long.valueOf(count.longValue() + 1));
             } else {
                 counts.put(time, ONE);
             }
             fileInfos.get(channel).getWriter().write(sample);
         }
 
         @Override
         public void stopConsuming() {
 
             // Ask parent to start consumption
             parent.startConsumingBase();
 
             // Propagate tagged samples to parent
             int channelsCount = getConsumedChannelCount();
             for (int i = 0; i < channelsCount; i++) {
                 FileInfo fileInfo = fileInfos.get(i);
 
                 // Clean the writer
                 CsvSampleWriter writer = fileInfo.getWriter();
                 writer.close();
 
                 // Create a reader and use it to get the buffered samples
                 File file = fileInfo.getFile();
                 try (CsvSampleReader reader = new CsvSampleReader(file,
                         getConsumedMetadata(i))) {
                     while (reader.hasNext()) {
                         Sample sample = reader.readSample();
                         // Ask parent to consume the altered sample
                         parent.consumeBase(
                                 createIndexedSample(sample, i,
                                         counts.get(getTimeInterval(sample)).longValue()
                                                 % parent.getGranularity()), i);
                     }
                 } finally {
                     file.delete();
                 }
             }
 
             if (createdWorkDir) {
                 File workingDir = parent.getWorkingDirectory();
                 try {
                     FileUtils.deleteDirectory(workingDir);
                 } catch (IOException e) {
-                    log.warn(String.format(
-                            "Cannot delete created temporary directory \"%s\"",
-                            workingDir), e);
+                    log.warn("Cannot delete created temporary directory, '{}'", workingDir, e);
                 }
             }
 
             // Ask parent to stop consumption
             parent.stopConsumingBase();
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/reporters/ResultAction.java b/src/core/org/apache/jmeter/reporters/ResultAction.java
index 059401e8d..5635cf4ca 100644
--- a/src/core/org/apache/jmeter/reporters/ResultAction.java
+++ b/src/core/org/apache/jmeter/reporters/ResultAction.java
@@ -1,94 +1,93 @@
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
 
 package org.apache.jmeter.reporters;
 
 import java.io.Serializable;
 
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.OnErrorTestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * ResultAction - take action based on the status of the last Result
  *
  */
 public class ResultAction extends OnErrorTestElement implements Serializable, SampleListener {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ResultAction.class);
 
     /*
      * Constructor is initially called once for each occurrence in the test plan
      * For GUI, several more instances are created Then clear is called at start
      * of test Called several times during test startup The name will not
      * necessarily have been set at this point.
      */
     public ResultAction() {
         super();
-        // log.debug(Thread.currentThread().getName());
-        // System.out.println(">> "+me+" "+this.getName()+"
-        // "+Thread.currentThread().getName());
     }
 
     /**
      * Examine the sample(s) and take appropriate action
      *
      * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
      */
     @Override
     public void sampleOccurred(SampleEvent e) {
         SampleResult s = e.getResult();
-        log.debug(s.getSampleLabel() + " OK? " + s.isSuccessful());
+        if (log.isDebugEnabled()) {
+            log.debug("{} OK? {}", s.getSampleLabel(), s.isSuccessful());
+        }
         if (!s.isSuccessful()) {
             if (isStopTestNow()) {
                 s.setStopTestNow(true);
             }
             if (isStopTest()) {
                 s.setStopTest(true);
             }
             if (isStopThread()) {
                 s.setStopThread(true);
             }
             if (isStartNextThreadLoop()) {
                s.setStartNextThreadLoop(true);
             }
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void sampleStarted(SampleEvent e) {
         // not used
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void sampleStopped(SampleEvent e) {
         // not used
     }
 
 }
diff --git a/src/core/org/apache/jmeter/reporters/ResultCollector.java b/src/core/org/apache/jmeter/reporters/ResultCollector.java
index 26f672410..eec8fc112 100644
--- a/src/core/org/apache/jmeter/reporters/ResultCollector.java
+++ b/src/core/org/apache/jmeter/reporters/ResultCollector.java
@@ -1,667 +1,679 @@
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
 
 package org.apache.jmeter.reporters;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.io.RandomAccessFile;
 import java.io.Serializable;
 import java.nio.charset.StandardCharsets;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.ObjectProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Visualizer;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This class handles all saving of samples.
  * The class must be thread-safe because it is shared between threads (NoThreadClone).
  */
 public class ResultCollector extends AbstractListenerElement implements SampleListener, Clearable, Serializable,
         TestStateListener, Remoteable, NoThreadClone {
     /**
      * Keep track of the file writer and the configuration,
      * as the instance used to close them is not the same as the instance that creates
      * them. This means one cannot use the saved PrintWriter or use getSaveConfig()
      */
     private static class FileEntry{
         final PrintWriter pw;
         final SampleSaveConfiguration config;
         FileEntry(PrintWriter _pw, SampleSaveConfiguration _config){
             pw =_pw;
             config = _config;
         }
     }
     
     private static final class ShutdownHook implements Runnable {
 
         @Override
         public void run() {
             log.info("Shutdown hook started");
             synchronized (LOCK) {
                 flushFileOutput();                    
             }
             log.info("Shutdown hook ended");
         }     
     }
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ResultCollector.class);
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
     // This string is used to identify local test runs, so must not be a valid host name
     private static final String TEST_IS_LOCAL = "*local*"; // $NON-NLS-1$
 
     private static final String TESTRESULTS_START = "<testResults>"; // $NON-NLS-1$
 
     private static final String TESTRESULTS_START_V1_1_PREVER = "<testResults version=\"";  // $NON-NLS-1$
 
     private static final String TESTRESULTS_START_V1_1_POSTVER="\">"; // $NON-NLS-1$
 
     private static final String TESTRESULTS_END = "</testResults>"; // $NON-NLS-1$
 
     // we have to use version 1.0, see bug 59973
     private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; // $NON-NLS-1$
 
     private static final int MIN_XML_FILE_LEN = XML_HEADER.length() + TESTRESULTS_START.length()
             + TESTRESULTS_END.length();
 
     public static final String FILENAME = "filename"; // $NON-NLS-1$
 
     private static final String SAVE_CONFIG = "saveConfig"; // $NON-NLS-1$
 
     private static final String ERROR_LOGGING = "ResultCollector.error_logging"; // $NON-NLS-1$
 
     private static final String SUCCESS_ONLY_LOGGING = "ResultCollector.success_only_logging"; // $NON-NLS-1$
 
     /** AutoFlush on each line */
     private static final boolean SAVING_AUTOFLUSH = JMeterUtils.getPropDefault("jmeter.save.saveservice.autoflush", false); //$NON-NLS-1$
 
     // Static variables
 
     // Lock used to guard static mutable variables
     private static final Object LOCK = new Object();
 
     //@GuardedBy("LOCK")
     private static final Map<String, FileEntry> files = new HashMap<>();
 
     /**
      * Shutdown Hook that ensures PrintWriter is flushed is CTRL+C or kill is called during a test
      */
     //@GuardedBy("LOCK")
     private static Thread shutdownHook;
 
 
     /**
      * The instance count is used to keep track of whether any tests are currently running.
      * It's not possible to use the constructor or threadStarted etc as tests may overlap
      * e.g. a remote test may be started,
      * and then a local test started whilst the remote test is still running.
      */
     //@GuardedBy("LOCK")
     private static int instanceCount; // Keep track of how many instances are active
 
     // Instance variables (guarded by volatile)
 
     private transient volatile PrintWriter out;
 
     /**
      * Is a test running ?
      */
     private volatile boolean inTest = false;
 
     private volatile boolean isStats = false;
 
     /** the summarizer to which this result collector will forward the samples */
     private volatile Summariser summariser;
     
     /**
      * No-arg constructor.
      */
     public ResultCollector() {
         this(null);
     }
 
     /**
      * Constructor which sets the used {@link Summariser}
      * @param summer The {@link Summariser} to use
      */
     public ResultCollector(Summariser summer) {
         setErrorLogging(false);
         setSuccessOnlyLogging(false);
         setProperty(new ObjectProperty(SAVE_CONFIG, new SampleSaveConfiguration()));
         summariser = summer;
     }
 
     // Ensure that the sample save config is not shared between copied nodes
     // N.B. clone only seems to be used for client-server tests
     @Override
     public Object clone(){
         ResultCollector clone = (ResultCollector) super.clone();
         clone.setSaveConfig((SampleSaveConfiguration)clone.getSaveConfig().clone());
         // Unfortunately AbstractTestElement does not call super.clone()
         clone.summariser = this.summariser;
         return clone;
     }
 
     private void setFilenameProperty(String f) {
         setProperty(FILENAME, f);
     }
 
     /**
      * Get the filename of the file this collector uses
      * 
      * @return The name of the file
      */
     public String getFilename() {
         return getPropertyAsString(FILENAME);
     }
 
     /**
      * Get the state of error logging
      * 
      * @return Flag whether errors should be logged
      */
     public boolean isErrorLogging() {
         return getPropertyAsBoolean(ERROR_LOGGING);
     }
 
     /**
      * Sets error logging flag
      * 
      * @param errorLogging
      *            The flag whether errors should be logged
      */
     public final void setErrorLogging(boolean errorLogging) {
         setProperty(new BooleanProperty(ERROR_LOGGING, errorLogging));
     }
 
     /**
      * Sets the flag whether only successful samples should be logged
      * 
      * @param value
      *            The flag whether only successful samples should be logged
      */
     public final void setSuccessOnlyLogging(boolean value) {
         if (value) {
             setProperty(new BooleanProperty(SUCCESS_ONLY_LOGGING, true));
         } else {
             removeProperty(SUCCESS_ONLY_LOGGING);
         }
     }
 
     /**
      * Get the state of successful only logging
      * 
      * @return Flag whether only successful samples should be logged
      */
     public boolean isSuccessOnlyLogging() {
         return getPropertyAsBoolean(SUCCESS_ONLY_LOGGING,false);
     }
 
     /**
      * Decides whether or not to a sample is wanted based on:
      * <ul>
      * <li>errorOnly</li>
      * <li>successOnly</li>
      * <li>sample success</li>
      * </ul>
      * Should only be called for single samples.
      *
      * @param success is sample successful
      * @return whether to log/display the sample
      */
     public boolean isSampleWanted(boolean success){
         boolean errorOnly = isErrorLogging();
         boolean successOnly = isSuccessOnlyLogging();
         return isSampleWanted(success, errorOnly, successOnly);
     }
 
     /**
      * Decides whether or not to a sample is wanted based on:
      * <ul>
      * <li>errorOnly</li>
      * <li>successOnly</li>
      * <li>sample success</li>
      * </ul>
      * This version is intended to be called by code that loops over many samples;
      * it is cheaper than fetching the settings each time.
      * @param success status of sample
      * @param errorOnly if errors only wanted
      * @param successOnly if success only wanted
      * @return whether to log/display the sample
      */
     public static boolean isSampleWanted(boolean success, boolean errorOnly,
             boolean successOnly) {
         return (!errorOnly && !successOnly) ||
                (success && successOnly) ||
                (!success && errorOnly);
         // successOnly and errorOnly cannot both be set
     }
     /**
      * Sets the filename attribute of the ResultCollector object.
      *
      * @param f
      *            the new filename value
      */
     public void setFilename(String f) {
         if (inTest) {
             return;
         }
         setFilenameProperty(f);
     }
 
     @Override
     public void testEnded(String host) {
         synchronized(LOCK){
             instanceCount--;
             if (instanceCount <= 0) {
                 // No need for the hook now
                 // Bug 57088 - prevent (im?)possible NPE
                 if (shutdownHook != null) {
                     Runtime.getRuntime().removeShutdownHook(shutdownHook);
                 } else {
-                    log.warn("Should not happen: shutdownHook==null, instanceCount=" + instanceCount);
+                    log.warn("Should not happen: shutdownHook==null, instanceCount={}", instanceCount);
                 }
                 finalizeFileOutput();
                 inTest = false;
             }
         }
 
         if(summariser != null) {
             summariser.testEnded(host);
         }
     }
 
     @Override
     public void testStarted(String host) {
         synchronized(LOCK){
             if (instanceCount == 0) { // Only add the hook once
                 shutdownHook = new Thread(new ShutdownHook());
                 Runtime.getRuntime().addShutdownHook(shutdownHook);
             }
             instanceCount++;
             try {
                 initializeFileOutput();
                 if (getVisualizer() != null) {
                     this.isStats = getVisualizer().isStats();
                 }
             } catch (Exception e) {
-                log.error("", e);
+                log.error("Exception occurred while initializing file output.", e);
             }
         }
         inTest = true;
 
         if(summariser != null) {
             summariser.testStarted(host);
         }
     }
 
     @Override
     public void testEnded() {
         testEnded(TEST_IS_LOCAL);
     }
 
     @Override
     public void testStarted() {
         testStarted(TEST_IS_LOCAL);
     }
 
     /**
      * Loads an existing sample data (JTL) file.
      * This can be one of:
      * <ul>
      *   <li>XStream format</li>
      *   <li>CSV format</li>
      * </ul>
      *
      */
     public void loadExistingFile() {
         final Visualizer visualizer = getVisualizer();
         if (visualizer == null) {
             return; // No point reading the file if there's no visualiser
         }
         boolean parsedOK = false;
         String filename = getFilename();
         File file = new File(filename);
         if (file.exists()) {
             BufferedReader dataReader = null;
             BufferedInputStream bufferedInputStream = null;
             try {
                 dataReader = new BufferedReader(new FileReader(file)); // TODO Charset ?
                 // Get the first line, and see if it is XML
                 String line = dataReader.readLine();
                 dataReader.close();
                 dataReader = null;
                 if (line == null) {
-                    log.warn(filename+" is empty");
+                    log.warn("{} is empty", filename);
                 } else {
                     if (!line.startsWith("<?xml ")){// No, must be CSV //$NON-NLS-1$
                         CSVSaveService.processSamples(filename, visualizer, this);
                         parsedOK = true;
                     } else { // We are processing XML
                         try { // Assume XStream
                             bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                             SaveService.loadTestResults(bufferedInputStream,
                                     new ResultCollectorHelper(this, visualizer));
                             parsedOK = true;
                         } catch (Exception e) {
-                            log.warn("Failed to load " + filename + " using XStream. Error was: " + e);
+                            if (log.isWarnEnabled()) {
+                                log.warn("Failed to load {} using XStream. Error was: {}", filename, e.toString());
+                            }
                         }
                     }
                 }
             } catch (IOException | JMeterError | RuntimeException | OutOfMemoryError e) {
                 // FIXME Why do we catch OOM ?
-                log.warn("Problem reading JTL file: " + file);
+                log.warn("Problem reading JTL file: {}", file);
             } finally {
                 JOrphanUtils.closeQuietly(dataReader);
                 JOrphanUtils.closeQuietly(bufferedInputStream);
                 if (!parsedOK) {
                     GuiPackage.showErrorMessage(
                                 "Error loading results file - see log file",
                                 "Result file loader");
                 }
             }
         } else {
             GuiPackage.showErrorMessage(
                     "Error loading results file - could not open file",
                     "Result file loader");
         }
     }
 
     private static void writeFileStart(PrintWriter writer, SampleSaveConfiguration saveConfig) {
         if (saveConfig.saveAsXml()) {
             writer.print(XML_HEADER);
             // Write the EOL separately so we generate LF line ends on Unix and Windows
             writer.print("\n"); // $NON-NLS-1$
             String pi=saveConfig.getXmlPi();
             if (pi.length() > 0) {
                 writer.println(pi);
             }
             // Can't do it as a static initialisation, because SaveService
             // is being constructed when this is called
             writer.print(TESTRESULTS_START_V1_1_PREVER);
             writer.print(SaveService.getVERSION());
             writer.print(TESTRESULTS_START_V1_1_POSTVER);
             // Write the EOL separately so we generate LF line ends on Unix and Windows
             writer.print("\n"); // $NON-NLS-1$
         } else if (saveConfig.saveFieldNames()) {
             writer.println(CSVSaveService.printableFieldNamesToString(saveConfig));
         }
     }
 
     private static void writeFileEnd(PrintWriter pw, SampleSaveConfiguration saveConfig) {
         if (saveConfig.saveAsXml()) {
             pw.print("\n"); // $NON-NLS-1$
             pw.print(TESTRESULTS_END);
             pw.print("\n");// Added in version 1.1 // $NON-NLS-1$
         }
     }
 
     private static PrintWriter getFileWriter(String filename, SampleSaveConfiguration saveConfig)
             throws IOException {
         if (filename == null || filename.length() == 0) {
             return null;
         }
         filename = FileServer.resolveBaseRelativeName(filename);
         FileEntry fe = files.get(filename);
         PrintWriter writer = null;
         boolean trimmed = true;
 
         if (fe == null) {
             if (saveConfig.saveAsXml()) {
                 trimmed = trimLastLine(filename);
             } else {
                 trimmed = new File(filename).exists();
             }
             // Find the name of the directory containing the file
             // and create it - if there is one
             File pdir = new File(filename).getParentFile();
             if (pdir != null) {
                 // returns false if directory already exists, so need to check again
                 if(pdir.mkdirs()){
-                    log.info("Folder "+pdir.getAbsolutePath()+" was created");
+                    if (log.isInfoEnabled()) {
+                        log.info("Folder at {} was created", pdir.getAbsolutePath());
+                    }
                 } // else if might have been created by another process so not a problem
                 if (!pdir.exists()){
-                    log.warn("Error creating directories for "+pdir.toString());
+                    log.warn("Error creating directories for {}", pdir);
                 }
             }
             writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(filename,
                     trimmed)), SaveService.getFileEncoding(StandardCharsets.UTF_8.name())), SAVING_AUTOFLUSH);
-            log.debug("Opened file: "+filename);
+            log.debug("Opened file: {}", filename);
             files.put(filename, new FileEntry(writer, saveConfig));
         } else {
             writer = fe.pw;
         }
         if (!trimmed) {
             writeFileStart(writer, saveConfig);
         }
         return writer;
     }
 
     // returns false if the file did not contain the terminator
     private static boolean trimLastLine(String filename) {
         try (RandomAccessFile raf = new RandomAccessFile(filename, "rw")){ // $NON-NLS-1$
             long len = raf.length();
             if (len < MIN_XML_FILE_LEN) {
                 return false;
             }
             raf.seek(len - TESTRESULTS_END.length() - 10);// TODO: may not work on all OSes?
             String line;
             long pos = raf.getFilePointer();
             int end = 0;
             while ((line = raf.readLine()) != null)// reads to end of line OR end of file
             {
                 end = line.indexOf(TESTRESULTS_END);
                 if (end >= 0) // found the string
                 {
                     break;
                 }
                 pos = raf.getFilePointer();
             }
             if (line == null) {
-                log.warn("Unexpected EOF trying to find XML end marker in " + filename);
+                log.warn("Unexpected EOF trying to find XML end marker in {}", filename);
                 return false;
             }
             raf.setLength(pos + end);// Truncate the file
         } catch (FileNotFoundException e) {
             return false;
         } catch (IOException e) {
-            log.warn("Error trying to find XML terminator " + e.toString());
+            if (log.isWarnEnabled()) {
+                log.warn("Error trying to find XML terminator. {}", e.toString());
+            }
             return false;
         }
         return true;
     }
 
     @Override
     public void sampleStarted(SampleEvent e) {
     }
 
     @Override
     public void sampleStopped(SampleEvent e) {
     }
 
     /**
      * When a test result is received, display it and save it.
      *
      * @param event
      *            the sample event that was received
      */
     @Override
     public void sampleOccurred(SampleEvent event) {
         SampleResult result = event.getResult();
 
         if (isSampleWanted(result.isSuccessful())) {
             sendToVisualizer(result);
             if (out != null && !isResultMarked(result) && !this.isStats) {
                 SampleSaveConfiguration config = getSaveConfig();
                 result.setSaveConfig(config);
                 try {
                     if (config.saveAsXml()) {
                         SaveService.saveSampleResult(event, out);
                     } else { // !saveAsXml
                         String savee = CSVSaveService.resultToDelimitedString(event);
                         out.println(savee);
                     }
                 } catch (Exception err) {
                     log.error("Error trying to record a sample", err); // should throw exception back to caller
                 }
             }
         }
 
         if(summariser != null) {
             summariser.sampleOccurred(event);
         }
     }
 
     protected final void sendToVisualizer(SampleResult r) {
         if (getVisualizer() != null) {
             getVisualizer().add(r);
         }
     }
 
     /**
      * recordStats is used to save statistics generated by visualizers
      *
      * @param e The data to save
      * @throws IOException when data writing fails
      */
     // Used by: MonitorHealthVisualizer.add(SampleResult res)
     public void recordStats(TestElement e) throws IOException {
         if (out != null) {
             SaveService.saveTestElement(e, out);
         }
     }
 
     /**
      * Checks if the sample result is marked or not, and marks it
      * @param res - the sample result to check
      * @return <code>true</code> if the result was marked
      */
     private boolean isResultMarked(SampleResult res) {
         String filename = getFilename();
         return res.markFile(filename);
     }
 
     private void initializeFileOutput() throws IOException {
 
         String filename = getFilename();
         if (filename != null) {
             if (out == null) {
                 try {
                     out = getFileWriter(filename, getSaveConfig());
                 } catch (FileNotFoundException e) {
                     out = null;
                 }
             }
         }
     }
 
     /**
      * Flush PrintWriter to synchronize file contents
      */
     public void flushFile() {
         if (out != null) {
             log.info("forced flush through ResultCollector#flushFile");
             out.flush();
         }
     }
 
     /**
      * Flush PrintWriter, called by Shutdown Hook to ensure no data is lost
      */
     private static void flushFileOutput() {
-        for(Map.Entry<String,ResultCollector.FileEntry> me : files.entrySet()){
-            log.debug("Flushing: "+me.getKey());
-            FileEntry fe = me.getValue();
-            fe.pw.flush();
-            if (fe.pw.checkError()){
-                log.warn("Problem detected during use of "+me.getKey());
+        String key;
+        ResultCollector.FileEntry value;
+        for(Map.Entry<String, ResultCollector.FileEntry> me : files.entrySet()) {
+            key = me.getKey();
+            value = me.getValue();
+            log.debug("Flushing: {}", key);
+            value.pw.flush();
+            if (value.pw.checkError()){
+                log.warn("Problem detected during use of {}", key);
             }
         }
     }
     
     private void finalizeFileOutput() {
-        for(Map.Entry<String,ResultCollector.FileEntry> me : files.entrySet()){
-            log.debug("Closing: "+me.getKey());
-            FileEntry fe = me.getValue();
-            writeFileEnd(fe.pw, fe.config);
-            fe.pw.close();
-            if (fe.pw.checkError()){
-                log.warn("Problem detected during use of "+me.getKey());
+        String key;
+        ResultCollector.FileEntry value;
+        for(Map.Entry<String, ResultCollector.FileEntry> me : files.entrySet()) {
+            key = me.getKey();
+            value = me.getValue();
+            log.debug("Closing: {}", key);
+            writeFileEnd(value.pw, value.config);
+            value.pw.close();
+            if (value.pw.checkError()){
+                log.warn("Problem detected during use of {}", key);
             }
         }
         files.clear();
         out = null;
     }
 
     /**
      * @return Returns the saveConfig.
      */
     public SampleSaveConfiguration getSaveConfig() {
         try {
             return (SampleSaveConfiguration) getProperty(SAVE_CONFIG).getObjectValue();
         } catch (ClassCastException e) {
             setSaveConfig(new SampleSaveConfiguration());
             return getSaveConfig();
         }
     }
 
     /**
      * @param saveConfig
      *            The saveConfig to set.
      */
     public void setSaveConfig(SampleSaveConfiguration saveConfig) {
         getProperty(SAVE_CONFIG).setObjectValue(saveConfig);
     }
 
     // This is required so that
     // @see org.apache.jmeter.gui.tree.JMeterTreeModel.getNodesOfType()
     // can find the Clearable nodes - the userObject has to implement the interface.
     @Override
     public void clearData() {
     }
 }
diff --git a/src/core/org/apache/jmeter/reporters/ResultSaver.java b/src/core/org/apache/jmeter/reporters/ResultSaver.java
index 37ef5db10..a4c17cbff 100644
--- a/src/core/org/apache/jmeter/reporters/ResultSaver.java
+++ b/src/core/org/apache/jmeter/reporters/ResultSaver.java
@@ -1,289 +1,289 @@
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
 
 package org.apache.jmeter.reporters;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.Serializable;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.threads.JMeterContextService;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Save Result responseData to a set of files
  *
  *
  * This is mainly intended for validation tests
  *
  */
 public class ResultSaver extends AbstractTestElement implements Serializable, SampleListener {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ResultSaver.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private static final Object LOCK = new Object();
 
     private static final String TIMESTAMP_FORMAT = "yyyyMMdd-HHmm_"; // $NON-NLS-1$
 
     // File name sequence number
     //@GuardedBy("LOCK")
     private static long sequenceNumber = 0;
 
     //@GuardedBy("LOCK")
     private static String timeStamp;
 
     //@GuardedBy("LOCK")
     private static int numberPadLength;
 
     //+ JMX property names; do not change
 
     public static final String FILENAME = "FileSaver.filename"; // $NON-NLS-1$
 
     public static final String VARIABLE_NAME = "FileSaver.variablename"; // $NON-NLS-1$
 
     public static final String ERRORS_ONLY = "FileSaver.errorsonly"; // $NON-NLS-1$
 
     public static final String SUCCESS_ONLY = "FileSaver.successonly"; // $NON-NLS-1$
 
     public static final String SKIP_AUTO_NUMBER = "FileSaver.skipautonumber"; // $NON-NLS-1$
 
     public static final String SKIP_SUFFIX = "FileSaver.skipsuffix"; // $NON-NLS-1$
 
     public static final String ADD_TIMESTAMP = "FileSaver.addTimstamp"; // $NON-NLS-1$
 
     public static final String NUMBER_PAD_LENGTH = "FileSaver.numberPadLen"; // $NON-NLS-1$
 
     //- JMX property names
 
     /**
      * Constructor is initially called once for each occurrence in the test plan
      * For GUI, several more instances are created Then clear is called at start
      * of test Called several times during test startup The name will not
      * necessarily have been set at this point.
      */
     public ResultSaver() {
         super();
     }
 
     /**
      * Constructor for use during startup (intended for non-GUI use) 
      * @param name of summariser
      */
     public ResultSaver(String name) {
         this();
         setName(name);
     }
 
     /**
      * @return next number accross all instances
      */
     private static long nextNumber() {
         synchronized(LOCK) {
             return ++sequenceNumber;
         }
     }
     
     /**
      * This is called once for each occurrence in the test plan, before the
      * start of the test. The super.clear() method clears the name (and all
      * other properties), so it is called last.
      */
     @Override
     public void clear() {
         synchronized(LOCK){
             sequenceNumber = 0;
             if (getAddTimeStamp()) {
                 DateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT);
                 timeStamp = format.format(new Date());
             } else {
                 timeStamp = "";
             }
             numberPadLength=getNumberPadLen();
         }
         super.clear();
     }
 
     /**
      * Saves the sample result (and any sub results) in files
      *
      * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
      */
     @Override
     public void sampleOccurred(SampleEvent e) {
       processSample(e.getResult(), new Counter());
    }
 
    /**
     * Recurse the whole (sub)result hierarchy.
     *
     * @param s Sample result
     * @param c sample counter
     */
    private void processSample(SampleResult s, Counter c) {
        saveSample(s, c.num++);
        SampleResult[] sampleResults = s.getSubResults();
        for (SampleResult sampleResult : sampleResults) {
            processSample(sampleResult, c);
        }
     }
 
     /**
      * @param s SampleResult to save
      * @param num number to append to variable (if >0)
      */
     private void saveSample(SampleResult s, int num) {
         // Should we save the sample?
         if (s.isSuccessful()){
             if (getErrorsOnly()){
                 return;
             }
         } else {
             if (getSuccessOnly()){
                 return;
             }
         }
 
         String fileName = makeFileName(s.getContentType(), getSkipAutoNumber(), getSkipSuffix());
-        if(log.isDebugEnabled()) {
-            log.debug("Saving " + s.getSampleLabel() + " in " + fileName);
+        if (log.isDebugEnabled()) {
+            log.debug("Saving {} in {}", s.getSampleLabel(), fileName);
         }
         s.setResultFileName(fileName);// Associate sample with file name
         String variable = getVariableName();
         if (variable.length()>0){
             if (num > 0) {
                 StringBuilder sb = new StringBuilder(variable);
                 sb.append(num);
                 variable=sb.toString();
             }
             JMeterContextService.getContext().getVariables().put(variable, fileName);
         }
         File out = new File(fileName);
         try (FileOutputStream fos = new FileOutputStream(out)){
             JOrphanUtils.write(s.getResponseData(), fos); // chunk the output if necessary
-        } catch (FileNotFoundException e1) {
-            log.error("Error creating sample file for " + s.getSampleLabel(), e1);
-        } catch (IOException e1) {
-            log.error("Error saving sample " + s.getSampleLabel(), e1);
+        } catch (FileNotFoundException e) {
+            log.error("Error creating sample file for {}", s.getSampleLabel(), e);
+        } catch (IOException e) {
+            log.error("Error saving sample {}", s.getSampleLabel(), e);
         }
     }
 
     /**
      * @param contentType Content type
      * @param skipAutoNumber Skip auto number
      * @param skipSuffix Skip suffix
      * @return fileName composed of fixed prefix, a number, and a suffix derived
      *         from the contentType e.g. Content-Type:
      *         text/html;charset=ISO-8859-1
      */
     private String makeFileName(String contentType, boolean skipAutoNumber, boolean skipSuffix) {
         StringBuilder sb = new StringBuilder(FileServer.resolveBaseRelativeName(getFilename()));
         sb.append(timeStamp); // may be the empty string
         if (!skipAutoNumber){
             String number = Long.toString(nextNumber());
             for(int i=number.length(); i < numberPadLength; i++) {
                 sb.append('0');
             }
             sb.append(number);
         }
         if (!skipSuffix){
             sb.append('.');
             if (contentType != null) {
                 int i = contentType.indexOf('/'); // $NON-NLS-1$
                 if (i != -1) {
                     int j = contentType.indexOf(';'); // $NON-NLS-1$
                     if (j != -1) {
                         sb.append(contentType.substring(i + 1, j));
                     } else {
                         sb.append(contentType.substring(i + 1));
                     }
                 } else {
                     sb.append("unknown");
                 }
             } else {
                 sb.append("unknown");
             }
         }
         return sb.toString();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void sampleStarted(SampleEvent e) {
         // not used
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void sampleStopped(SampleEvent e) {
         // not used
     }
 
     private String getFilename() {
         return getPropertyAsString(FILENAME);
     }
 
     private String getVariableName() {
         return getPropertyAsString(VARIABLE_NAME,""); // $NON-NLS-1$
     }
 
     private boolean getErrorsOnly() {
         return getPropertyAsBoolean(ERRORS_ONLY);
     }
 
     private boolean getSkipAutoNumber() {
         return getPropertyAsBoolean(SKIP_AUTO_NUMBER);
     }
 
     private boolean getSkipSuffix() {
         return getPropertyAsBoolean(SKIP_SUFFIX);
     }
 
     private boolean getSuccessOnly() {
         return getPropertyAsBoolean(SUCCESS_ONLY);
     }
 
     private boolean getAddTimeStamp() {
         return getPropertyAsBoolean(ADD_TIMESTAMP);
     }
 
     private int getNumberPadLen() {
         return getPropertyAsInt(NUMBER_PAD_LENGTH, 0);
     }
 
     // Mutable int to keep track of sample count
     private static class Counter{
         int num;
     }
 }
diff --git a/src/core/org/apache/jmeter/reporters/Summariser.java b/src/core/org/apache/jmeter/reporters/Summariser.java
index 03793eade..9d7c49a56 100644
--- a/src/core/org/apache/jmeter/reporters/Summariser.java
+++ b/src/core/org/apache/jmeter/reporters/Summariser.java
@@ -1,381 +1,384 @@
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
 
 package org.apache.jmeter.reporters;
 
 import java.io.Serializable;
 import java.text.DecimalFormat;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.apache.jmeter.control.TransactionController;
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterContextService.ThreadCounts;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Generate a summary of the test run so far to the log file and/or standard
  * output. Both running and differential totals are shown. Output is generated
  * every n seconds (default 30 seconds (property summariser.interval)) on the appropriate time boundary, so that
  * multiple test runs on the same time will be synchronised.
  *
  * This is mainly intended for batch (non-GUI) runs
  * FIXME : Docs below are outdated, need fixing
  * Note that the {@link SummariserRunningSample} start and end times relate to the samples,
  * not the reporting interval.
  *
  * Since the first sample in a delta is likely to have started in the previous reporting interval,
  * this means that the delta interval is likely to be longer than the reporting interval.
  *
  * Also, the sum of the delta intervals will be larger than the overall elapsed time.
  *
  * Data is accumulated according to the test element name.
  *
  */
 public class Summariser extends AbstractTestElement
     implements Serializable, SampleListener, TestStateListener, NoThreadClone, Remoteable {
 
     /*
      * N.B. NoThreadClone is used to ensure that the testStarted() methods will share the same
      * instance as the sampleOccured() methods, so the testStarted() method can fetch the
      * Totals accumulator object for the samples to be stored in.
      */
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Summariser.class);
 
     /** interval between summaries (in seconds) default 30 seconds */
     private static final long INTERVAL = JMeterUtils.getPropDefault("summariser.interval", 30); //$NON-NLS-1$
 
     /** Write messages to log file ? */
     private static final boolean TOLOG = JMeterUtils.getPropDefault("summariser.log", true); //$NON-NLS-1$
 
     /** Write messages to System.out ? */
     private static final boolean TOOUT = JMeterUtils.getPropDefault("summariser.out", true); //$NON-NLS-1$
-    
+
     /** Ignore TC generated SampleResult in summary */
     private static final boolean IGNORE_TC_GENERATED_SAMPLERESULT = JMeterUtils.getPropDefault("summariser.ignore_transaction_controller_sample_result", true); //$NON-NLS-1$
 
     /*
      * Ensure that a report is not skipped if we are slightly late in checking
      * the time.
      */
     private static final int INTERVAL_WINDOW = 5; // in seconds
 
     /**
      * Lock used to protect ACCUMULATORS update + INSTANCE_COUNT update
      */
     private static final Object LOCK = new Object();
 
     /*
      * This map allows summarisers with the same name to contribute to the same totals.
      */
     //@GuardedBy("LOCK") - needed to ensure consistency between this and INSTANCE_COUNT
     private static final Map<String, Totals> ACCUMULATORS = new ConcurrentHashMap<>();
 
     //@GuardedBy("LOCK")
     private static int INSTANCE_COUNT; // number of active tests
 
     /*
      * Cached copy of Totals for this instance.
      * The variables do not need to be synchronised,
      * as they are not shared between threads
      * However the contents do need to be synchronized.
      */
     //@GuardedBy("myTotals")
     private transient Totals myTotals = null;
 
     // Name of the accumulator. Set up by testStarted().
     private transient String myName;
 
     /*
      * Constructor is initially called once for each occurrence in the test plan.
      * For GUI, several more instances are created.
      * Then clear is called at start of test.
      * Called several times during test startup.
      * The name will not necessarily have been set at this point.
      */
     public Summariser() {
         super();
         synchronized (LOCK) {
             ACCUMULATORS.clear();
             INSTANCE_COUNT=0;
         }
     }
 
     /**
      * Constructor for use during startup (intended for non-GUI use)
      *
      * @param name of summariser
      */
     public Summariser(String name) {
         this();
         setName(name);
     }
 
     /*
      * Contains the items needed to collect stats for a summariser
      *
      */
     private static class Totals {
 
         /** Time of last summary (to prevent double reporting) */
         private long last = 0;
 
         private final SummariserRunningSample delta = new SummariserRunningSample("DELTA");
 
         private final SummariserRunningSample total = new SummariserRunningSample("TOTAL");
 
         /**
          * Add the delta values to the total values and clear the delta
          */
         private void moveDelta() {
             total.addSample(delta);
             delta.clear();
         }
     }
 
     /**
      * Accumulates the sample in two SampleResult objects - one for running
      * totals, and the other for deltas.
      *
      * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
      */
     @Override
     @SuppressWarnings("SynchronizeOnNonFinalField")
     public void sampleOccurred(SampleEvent e) {
         SampleResult s = e.getResult();
         if(IGNORE_TC_GENERATED_SAMPLERESULT && TransactionController.isFromTransactionController(s)) {
             return;
         }
 
         long now = System.currentTimeMillis() / 1000;// in seconds
 
         SummariserRunningSample myDelta = null;
         SummariserRunningSample myTotal = null;
         boolean reportNow = false;
 
         /*
          * Have we reached the reporting boundary?
          * Need to allow for a margin of error, otherwise can miss the slot.
          * Also need to check we've not hit the window already
          */
         
         synchronized (myTotals) {
             if (s != null) {
                 myTotals.delta.addSample(s);
             }
 
             if ((now > myTotals.last + INTERVAL_WINDOW) && (now % INTERVAL <= INTERVAL_WINDOW)) {
                 reportNow = true;
 
                 // copy the data to minimise the synch time
                 myDelta = new SummariserRunningSample(myTotals.delta);
                 myTotals.moveDelta();
                 myTotal = new SummariserRunningSample(myTotals.total);
 
                 myTotals.last = now; // stop double-reporting
             }
         }
         if (reportNow) {
-            writeToLog(format(myName, myDelta, "+"));
+            formatAndWriteToLog(myName, myDelta, "+");
 
             // Only if we have updated them
             if (myTotal != null && myDelta != null &&myTotal.getNumSamples() != myDelta.getNumSamples()) { // NOSONAR
-                writeToLog(format(myName, myTotal, "="));
+                formatAndWriteToLog(myName, myTotal, "=");
             }
         }
     }
 
     private static StringBuilder longToSb(StringBuilder sb, long l, int len) {
         sb.setLength(0);
         sb.append(l);
         return JOrphanUtils.rightAlign(sb, len);
     }
 
     private static StringBuilder doubleToSb(DecimalFormat dfDouble, StringBuilder sb, double d, int len, int frac) {
         sb.setLength(0);
         dfDouble.setMinimumFractionDigits(frac);
         dfDouble.setMaximumFractionDigits(frac);
         sb.append(dfDouble.format(d));
         return JOrphanUtils.rightAlign(sb, len);
     }
 
-    /**
-     * Formats summariserRunningSample
-     * @param name Summariser name
-     * @param summariserRunningSample {@link SummariserRunningSample}
-     * @param type Type of summariser (difference or total)
-     * @return the summary information
-     */
-    private static String format(String name, SummariserRunningSample summariserRunningSample, String type) {
-        DecimalFormat dfDouble = new DecimalFormat("#0.0"); // $NON-NLS-1$
-        StringBuilder tmp = new StringBuilder(20); // for intermediate use
-        StringBuilder sb = new StringBuilder(140); // output line buffer
-        sb.append(name);
-        sb.append(' ');
-        sb.append(type);
-        sb.append(' ');
-        sb.append(longToSb(tmp, summariserRunningSample.getNumSamples(), 6));
-        sb.append(" in ");
-        long elapsed = summariserRunningSample.getElapsed();
-        long elapsedSec = (elapsed + 500) / 1000; // rounded seconds
-        sb.append(JOrphanUtils.formatDuration(elapsedSec));
-        sb.append(" = ");
-        if (elapsed > 0) {
-            sb.append(doubleToSb(dfDouble, tmp, summariserRunningSample.getRate(), 6, 1));
-        } else {
-            sb.append("******");// Rate is effectively infinite
-        }
-        sb.append("/s Avg: ");
-        sb.append(longToSb(tmp, summariserRunningSample.getAverage(), 5));
-        sb.append(" Min: ");
-        sb.append(longToSb(tmp, summariserRunningSample.getMin(), 5));
-        sb.append(" Max: ");
-        sb.append(longToSb(tmp, summariserRunningSample.getMax(), 5));
-        sb.append(" Err: ");
-        sb.append(longToSb(tmp, summariserRunningSample.getErrorCount(), 5));
-        sb.append(" (");
-        sb.append(summariserRunningSample.getErrorPercentageString());
-        sb.append(')');
-        if ("+".equals(type)) {
-            ThreadCounts tc = JMeterContextService.getThreadCounts();
-            sb.append(" Active: ");
-            sb.append(tc.activeThreads);
-            sb.append(" Started: ");
-            sb.append(tc.startedThreads);
-            sb.append(" Finished: ");
-            sb.append(tc.finishedThreads);
-        }
-        return sb.toString();
-    }
-
     /** {@inheritDoc} */
     @Override
     public void sampleStarted(SampleEvent e) {
         // not used
     }
 
     /** {@inheritDoc} */
     @Override
     public void sampleStopped(SampleEvent e) {
         // not used
     }
 
     /*
      * The testStarted/testEnded methods are called at the start and end of a test.
      *
      * However, when a test is run on multiple nodes, there is no guarantee that all the
      * testStarted() methods will be called before all the threadStart() or sampleOccurred()
      * methods for other threads - nor that testEnded() will only be called after all
      * sampleOccurred() calls. The ordering is only guaranteed within a single test.
      *
      */
 
 
     /** {@inheritDoc} */
     @Override
     public void testStarted() {
         testStarted("local");
     }
 
     /** {@inheritDoc} */
     @Override
     public void testEnded() {
         testEnded("local");
     }
 
     /**
      * Called once for each Summariser in the test plan.
      * There may be more than one summariser with the same name,
      * however they will all be called before the test proper starts.
      * <p>
      * However, note that this applies to a single test only.
      * When running in client-server mode, testStarted() may be
      * invoked after sampleOccurred().
      * <p>
      * {@inheritDoc}
      */
     @Override
     public void testStarted(String host) {
         synchronized (LOCK) {
             myName = getName();
             myTotals = ACCUMULATORS.get(myName);
             if (myTotals == null){
                 myTotals = new Totals();
                 ACCUMULATORS.put(myName, myTotals);
             }
             INSTANCE_COUNT++;
         }
     }
 
     /**
      * Called from a different thread as testStarted() but using the same instance.
      * So synch is needed to fetch the accumulator, and the myName field will already be set up.
      * <p>
      * {@inheritDoc}
      */
     @Override
     public void testEnded(String host) {
         Set<Entry<String, Totals>> totals = null;
         synchronized (LOCK) {
             INSTANCE_COUNT--;
             if (INSTANCE_COUNT <= 0){
                 totals = ACCUMULATORS.entrySet();
             }
         }
         if (totals == null) {// We're not done yet
             return;
         }
         for(Map.Entry<String, Totals> entry : totals){
             String name = entry.getKey();
             Totals total = entry.getValue();
             total.delta.setEndTime(); // ensure delta has correct end time
             // Only print final delta if there were some samples in the delta
             // and there has been at least one sample reported previously
             if (total.delta.getNumSamples() > 0 && total.total.getNumSamples() >  0) {
-                writeToLog(format(name, total.delta, "+"));
+                formatAndWriteToLog(name, total.delta, "+");
             }
             total.moveDelta(); // This will update the total endTime
-            writeToLog(format(name, total.total, "="));
+            formatAndWriteToLog(name, total.total, "=");
         }
     }
 
-    private void writeToLog(String str) {
-        if (TOLOG) {
-            log.info(str);
+    private void formatAndWriteToLog(String name, SummariserRunningSample summariserRunningSample, String type) {
+        if (TOOUT || (TOLOG && log.isInfoEnabled())) {
+            String formattedMessage = format(name, summariserRunningSample, type);
+            if (TOLOG) {
+                log.info(formattedMessage);
+            }
+            if (TOOUT) {
+                System.out.println(formattedMessage);
+            }
+        }
+    }
+
+    /**
+     * Formats summariserRunningSample
+     * @param name Summariser name
+     * @param summariserRunningSample {@link SummariserRunningSample}
+     * @param type Type of summariser (difference or total)
+     * @return the summary information
+     */
+    private static String format(String name, SummariserRunningSample summariserRunningSample, String type) {
+        DecimalFormat dfDouble = new DecimalFormat("#0.0"); // $NON-NLS-1$
+        StringBuilder tmp = new StringBuilder(20); // for intermediate use
+        StringBuilder sb = new StringBuilder(140); // output line buffer
+        sb.append(name);
+        sb.append(' ');
+        sb.append(type);
+        sb.append(' ');
+        sb.append(longToSb(tmp, summariserRunningSample.getNumSamples(), 6));
+        sb.append(" in ");
+        long elapsed = summariserRunningSample.getElapsed();
+        long elapsedSec = (elapsed + 500) / 1000; // rounded seconds
+        sb.append(JOrphanUtils.formatDuration(elapsedSec));
+        sb.append(" = ");
+        if (elapsed > 0) {
+            sb.append(doubleToSb(dfDouble, tmp, summariserRunningSample.getRate(), 6, 1));
+        } else {
+            sb.append("******");// Rate is effectively infinite
         }
-        if (TOOUT) {
-            System.out.println(str);
+        sb.append("/s Avg: ");
+        sb.append(longToSb(tmp, summariserRunningSample.getAverage(), 5));
+        sb.append(" Min: ");
+        sb.append(longToSb(tmp, summariserRunningSample.getMin(), 5));
+        sb.append(" Max: ");
+        sb.append(longToSb(tmp, summariserRunningSample.getMax(), 5));
+        sb.append(" Err: ");
+        sb.append(longToSb(tmp, summariserRunningSample.getErrorCount(), 5));
+        sb.append(" (");
+        sb.append(summariserRunningSample.getErrorPercentageString());
+        sb.append(')');
+        if ("+".equals(type)) {
+            ThreadCounts tc = JMeterContextService.getThreadCounts();
+            sb.append(" Active: ");
+            sb.append(tc.activeThreads);
+            sb.append(" Started: ");
+            sb.append(tc.startedThreads);
+            sb.append(" Finished: ");
+            sb.append(tc.finishedThreads);
         }
+        return sb.toString();
     }
 
 }
