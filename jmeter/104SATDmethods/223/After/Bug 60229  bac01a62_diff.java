diff --git a/bin/jmeter.properties b/bin/jmeter.properties
index a6a767697..014a0fa99 100644
--- a/bin/jmeter.properties
+++ b/bin/jmeter.properties
@@ -1,1270 +1,1271 @@
 ################################################################################
 # Apache JMeter Property file
 ################################################################################
 
 ##   Licensed to the Apache Software Foundation (ASF) under one or more
 ##   contributor license agreements.  See the NOTICE file distributed with
 ##   this work for additional information regarding copyright ownership.
 ##   The ASF licenses this file to You under the Apache License, Version 2.0
 ##   (the "License"); you may not use this file except in compliance with
 ##   the License.  You may obtain a copy of the License at
 ## 
 ##       http://www.apache.org/licenses/LICENSE-2.0
 ## 
 ##   Unless required by applicable law or agreed to in writing, software
 ##   distributed under the License is distributed on an "AS IS" BASIS,
 ##   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ##   See the License for the specific language governing permissions and
 ##   limitations under the License.
 
 ################################################################################
 #
 #                      THIS FILE SHOULD NOT BE MODIFIED
 #
 # This avoids having to re-apply the modifications when upgrading JMeter
 # Instead only user.properties should be modified:
 # 1/ copy the property you want to modify to user.properties from jmeter.properties
 # 2/ Change its value there
 #
 ################################################################################
 
 #Preferred GUI language. Comment out to use the JVM default locale's language.
 #language=en
 
 
 # Additional locale(s) to add to the displayed list.
 # The current default list is: en, fr, de, no, es, tr, ja, zh_CN, zh_TW, pl, pt_BR
 # [see JMeterMenuBar#makeLanguageMenu()]
 # The entries are a comma-separated list of language names
 #locales.add=zu
 
 
 #---------------------------------------------------------------------------
 # XML Parser
 #---------------------------------------------------------------------------
 
 # Path to a Properties file containing Namespace mapping in the form
 # prefix=Namespace
 # Example:
 # ns=http://biz.aol.com/schema/2006-12-18
 #xpath.namespace.config=
 
 #---------------------------------------------------------------------------
 # SSL configuration
 #---------------------------------------------------------------------------
 
 ## SSL System properties are now in system.properties
 
 # JMeter no longer converts javax.xxx property entries in this file into System properties.
 # These must now be defined in the system.properties file or on the command-line.
 # The system.properties file gives more flexibility.
 
 # By default, SSL session contexts are now created per-thread, rather than being shared.
 # The original behaviour can be enabled by setting the JMeter property to true
 #https.sessioncontext.shared=false
 
 # Be aware that https default protocol may vary depending on the version of JVM
 # See https://blogs.oracle.com/java-platform-group/entry/diagnosing_tls_ssl_and_https
 # See https://bz.apache.org/bugzilla/show_bug.cgi?id=58236
 # Default HTTPS protocol level:
 #https.default.protocol=TLS
 # This may need to be changed here (or in user.properties) to:
 #https.default.protocol=SSLv3
 
 # List of protocols to enable. You may have to select only a subset if you find issues with target server.
 # This is needed when server does not support Socket version negotiation, this can lead to:
 # javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated
 # java.net.SocketException: Connection reset
 # see https://bz.apache.org/bugzilla/show_bug.cgi?id=54759
 #https.socket.protocols=SSLv2Hello SSLv3 TLSv1
 
 # Control if we allow reuse of cached SSL context between iterations
 # set the value to 'false' to reset the SSL context each iteration
 #https.use.cached.ssl.context=true
 
 # Start and end index to be used with keystores with many entries
 # The default is to use entry 0, i.e. the first
 #https.keyStoreStartIndex=0
 #https.keyStoreEndIndex=0
 
 #---------------------------------------------------------------------------
 # Look and Feel configuration
 #---------------------------------------------------------------------------
 
 #Classname of the Swing default UI
 #
 # The LAF classnames that are available are now displayed as ToolTip text
 # when hovering over the Options/Look and Feel selection list.
 #
 # You can either use a full class name, as shown below,
 # or one of the strings "System" or "CrossPlatform" which means
 #  JMeter will use the corresponding string returned by UIManager.get<name>LookAndFeelClassName()
 
 # LAF can be overridden by os.name (lowercased, spaces replaced by '_')
 # Sample os.name LAF:
 #jmeter.laf.windows_xp=javax.swing.plaf.metal.MetalLookAndFeel
 
 # Failing that, the OS family = os.name, but only up to first space:
 # Sample OS family LAF:
 #jmeter.laf.windows=com.sun.java.swing.plaf.windows.WindowsLookAndFeel
 
 # Mac apparently looks better with the System LAF
 jmeter.laf.mac=System
 
 # Failing that, the JMeter default laf can be defined:
 #jmeter.laf=System
 
 # If none of the above jmeter.laf properties are defined, JMeter uses the CrossPlatform LAF.
 # This is because the CrossPlatform LAF generally looks better than the System LAF.
 # See https://bz.apache.org/bugzilla/show_bug.cgi?id=52026 for details
 # N.B. the laf can be defined in user.properties.
 
 # LoggerPanel display
 # default to false
 #jmeter.loggerpanel.display=false
 
 # Enable LogViewer Panel to receive log event even if closed
 # Enabled since 2.12
 # Note this has some impact on performances, but as GUI mode must
 # not be used for Load Test it is acceptable
 #jmeter.loggerpanel.enable_when_closed=true
 
 # Max characters kept in LoggerPanel, default to 80000 chars
 # 0 means no limit
 #jmeter.loggerpanel.maxlength=80000
 
 # HiDPI mode (default: false)
 # Activate a 'pseudo'-hidpi mode. Allows to increase size of some UI elements
 # which are not correctly managed by JVM with high resolution screens in Linux or Windows
 #jmeter.hidpi.mode=false
 # To enable pseudo-hidpi mode change to true
 #jmeter.hidpi.mode=true
 # HiDPI scale factor
 #jmeter.hidpi.scale.factor=1.0
 # Suggested value for HiDPI
 #jmeter.hidpi.scale.factor=2.0
 
 # Toolbar display
 # Toolbar icon definitions
 #jmeter.toolbar.icons=org/apache/jmeter/images/toolbar/icons-toolbar.properties
 # Toolbar list
 #jmeter.toolbar=new,open,close,save,save_as_testplan,|,cut,copy,paste,|,expand,collapse,toggle,|,test_start,test_stop,test_shutdown,|,test_start_remote_all,test_stop_remote_all,test_shutdown_remote_all,|,test_clear,test_clear_all,|,search,search_reset,|,function_helper,help
 # Toolbar icons default size: 22x22. Available sizes are: 22x22, 32x32, 48x48
 #jmeter.toolbar.icons.size=22x22
 # Suggested value for HiDPI
 #jmeter.toolbar.icons.size=48x48
 
 # Icon definitions
 # default:
 #jmeter.icons=org/apache/jmeter/images/icon.properties
 # alternate:
 #jmeter.icons=org/apache/jmeter/images/icon_1.properties
 # Historical icon set (deprecated)
 #jmeter.icons=org/apache/jmeter/images/icon_old.properties
 
 # Tree icons default size: 19x19. Available sizes are: 19x19, 24x24, 32x32, 48x48
 # Useful for HiDPI display (see below)
 #jmeter.tree.icons.size=19x19
 # Suggested value for HiDPI screen like 3200x1800:
 #jmeter.tree.icons.size=32x32
 
 #Components to not display in JMeter GUI (GUI class name or static label)
 # These elements are deprecated and will be removed in next version: MongoDB Script, MongoDB Source Config, Monitor Results
 not_in_menu=org.apache.jmeter.protocol.mongodb.sampler.MongoScriptSampler, org.apache.jmeter.protocol.mongodb.config.MongoSourceElement,org.apache.jmeter.visualizers.MonitorHealthVisualizer
 
 # Number of items in undo history
 # Feature is disabled by default (0) due to known and not fixed bugs:
 # https://bz.apache.org/bugzilla/show_bug.cgi?id=57043
 # https://bz.apache.org/bugzilla/show_bug.cgi?id=57039
 # https://bz.apache.org/bugzilla/show_bug.cgi?id=57040
 # Set it to a number > 0 (25 can be a good default)
 # The bigger it is, the more it consumes memory
 #undo.history.size=0
 
 # Hotkeys to add JMeter components, will add elements when you press Ctrl+0 .. Ctrl+9 (Command+0 .. Command+9 on Mac)
 gui.quick_0=ThreadGroupGui
 gui.quick_1=HttpTestSampleGui
 gui.quick_2=RegexExtractorGui
 gui.quick_3=AssertionGui
 gui.quick_4=ConstantTimerGui
 gui.quick_5=TestActionGui
 gui.quick_6=JSR223PostProcessor
 gui.quick_7=JSR223PreProcessor
 gui.quick_8=DebugSampler
 gui.quick_9=ViewResultsFullVisualizer
 
 
 #---------------------------------------------------------------------------
 # JMX Backup configuration
 #---------------------------------------------------------------------------
 #Enable auto backups of the .jmx file when a test plan is saved.
 #When enabled, before the .jmx is saved, it will be backed up to the directory pointed
 #by the jmeter.gui.action.save.backup_directory property (see below). Backup file names are built
 #after the jmx file being saved. For example, saving test-plan.jmx will create a test-plan-000012.jmx
 #in the backup directory provided that the last created backup file is test-plan-000011.jmx.
 #Default value is true indicating that auto backups are enabled
 #jmeter.gui.action.save.backup_on_save=true
 
 #Set the backup directory path where JMX backups will be created upon save in the GUI.
 #If not set (what it defaults to) then backup files will be created in
 #a sub-directory of the JMeter base installation. The default directory is ${JMETER_HOME}/backups
 #If set and the directory does not exist, it will be created.
 #jmeter.gui.action.save.backup_directory=
 
 #Set the maximum time (in hours) that backup files should be preserved since the save time.
 #By default no expiration time is set which means we keep backups for ever.
 #jmeter.gui.action.save.keep_backup_max_hours=0
 
 #Set the maximum number of backup files that should be preserved. By default 10 backups will be preserved.
 #Setting this to zero will cause the backups to not being deleted (unless keep_backup_max_hours is set to a non zero value)
 #jmeter.gui.action.save.keep_backup_max_count=10
 
 
 #---------------------------------------------------------------------------
 # Remote hosts and RMI configuration
 #---------------------------------------------------------------------------
 
 # Remote Hosts - comma delimited
 remote_hosts=127.0.0.1
 #remote_hosts=localhost:1099,localhost:2010
 
 # RMI port to be used by the server (must start rmiregistry with same port)
 #server_port=1099
 
 # To change the port to (say) 1234:
 # On the server(s)
 # - set server_port=1234
 # - start rmiregistry with port 1234
 # On Windows this can be done by:
 # SET SERVER_PORT=1234
 # JMETER-SERVER
 #
 # On Unix:
 # SERVER_PORT=1234 jmeter-server
 #
 # On the client:
 # - set remote_hosts=server:1234
 
 # Parameter that controls the RMI port used by the RemoteSampleListenerImpl (The Controler)
 # Default value is 0 which means port is randomly assigned
 # You may need to open Firewall port on the Controller machine
 #client.rmi.localport=0
 
 # When distributed test is starting, there may be several attempts to initialize
 # remote engines. By default, only single try is made. Increase following property
 # to make it retry for additional times
 #client.tries=1
 
 # If there is initialization retries, following property sets delay between attempts
 #client.retries_delay=5000
 
 # When all initialization tries was made, test will fail if some remote engines are failed
 # Set following property to true to ignore failed nodes and proceed with test 
 #client.continue_on_fail=false
 
 # To change the default port (1099) used to access the server:
 #server.rmi.port=1234
 
 # To use a specific port for the JMeter server engine, define
 # the following property before starting the server:
 #server.rmi.localport=4000
 
 # From JMeter 2.3.1, the jmeter server creates the RMI registry as part of the server process.
 # To stop the server creating the RMI registry:
 #server.rmi.create=false
 
 # From JMeter 2.3.1, define the following property to cause JMeter to exit after the first test
 #server.exitaftertest=true
 
 #---------------------------------------------------------------------------
 #         Include Controller
 #---------------------------------------------------------------------------
 
 # Prefix used by IncludeController when building file name
 #includecontroller.prefix=
 
 #---------------------------------------------------------------------------
 #         Logging Configuration
 #---------------------------------------------------------------------------
 
 # Note: JMeter uses Avalon (Excalibur) LogKit
 
 # Logging Format
 # see http://excalibur.apache.org/apidocs/org/apache/log/format/PatternFormatter.html
 
 #
 # Default format:
 #log_format=%{time:yyyy/MM/dd HH:mm:ss} %5.5{priority} - %{category}: %{message} %{throwable}
 # \n is automatically added to the end of the string
 #
 # Predefined formats in the JMeter LoggingManager:
 #log_format_type=default
 #log_format_type=thread_prefix
 #log_format_type=thread_suffix
 # default is as above
 # thread_prefix adds the thread name as a prefix to the category
 # thread_suffix adds the thread name as a suffix to the category
 # Note that thread name is not included by default, as it requires extra processing.
 #
 # To change the logging format, define either log_format_type or log_format
 # If both are defined, the type takes precedence
 # Note that these properties cannot be defined using the -J or -D JMeter
 # command-line flags, as the format will have already been determined by then
 # However, they can be defined as JVM properties
 
 #Logging levels for the logging categories in JMeter.  Correct values are FATAL_ERROR, ERROR, WARN, INFO, and DEBUG
 # To set the log level for a package or individual class, use:
 # log_level.[package_name].[classname]=[PRIORITY_LEVEL]
 # But omit "org.apache" from the package name.  The classname is optional.  Further examples below.
 
 log_level.jmeter=INFO
 log_level.jmeter.junit=DEBUG
 #log_level.jmeter.control=DEBUG
 #log_level.jmeter.testbeans=DEBUG
 #log_level.jmeter.engine=DEBUG
 #log_level.jmeter.threads=DEBUG
 #log_level.jmeter.gui=WARN
 #log_level.jmeter.testelement=DEBUG
 #log_level.jmeter.util=WARN
 #log_level.jmeter.protocol.http=DEBUG
 # For CookieManager, AuthManager etc:
 #log_level.jmeter.protocol.http.control=DEBUG
 #log_level.jmeter.protocol.ftp=WARN
 #log_level.jmeter.protocol.jdbc=DEBUG
 #log_level.jmeter.protocol.java=WARN
 #log_level.jmeter.testelements.property=DEBUG
 log_level.jorphan=INFO
 	
 
 #Log file for log messages.
 # You can specify a different log file for different categories via:
 # log_file.[category]=[filename]
 # category is equivalent to the package/class names described above
 
 # Combined log file (for jmeter and jorphan)
 #log_file=jmeter.log
 # To redirect logging to standard output, try the following:
 # (it will probably report an error, but output will be to stdout)
 #log_file=
 
 # Or define separate logs if required:
 #log_file.jorphan=jorphan.log
 #log_file.jmeter=jmeter.log
 
 # If the filename contains  paired single-quotes, then the name is processed
 # as a SimpleDateFormat format applied to the current date, for example:
 #log_file='jmeter_'yyyyMMddHHmmss'.tmp'
 
 # N.B. When JMeter starts, it sets the system property:
 #    org.apache.commons.logging.Log
 # to
 #    org.apache.commons.logging.impl.LogKitLogger
 # if not already set. This causes Apache and Commons HttpClient to use the same logging as JMeter
 
 # Further logging configuration
 # Excalibur logging provides the facility to configure logging using
 # configuration files written in XML. This allows for such features as
 # log file rotation which are not supported directly by JMeter.
 #
 # If such a file specified, it will be applied to the current logging
 # hierarchy when that has been created.
 # 
 #log_config=logkit.xml
 
 #---------------------------------------------------------------------------
 # HTTP Java configuration
 #---------------------------------------------------------------------------
 
 # Number of connection retries performed by HTTP Java sampler before giving up
 # 0 means no retry since version 3.0
 #http.java.sampler.retries=0
 
 #---------------------------------------------------------------------------
 # Following properties apply to both Commons and Apache HttpClient
 #---------------------------------------------------------------------------
 
 # set the socket timeout (or use the parameter http.socket.timeout) 
 # for AJP Sampler and HttpClient3 implementation.
 # Note for HttpClient3 implementation it is better to use GUI to set timeout 
 # or use http.socket.timeout in httpclient.parameters
 # Value is in milliseconds
 #httpclient.timeout=0
 # 0 == no timeout
 
 # Set the http version (defaults to 1.1)
 #httpclient.version=1.1 (or use the parameter http.protocol.version)
 
 # Define characters per second > 0 to emulate slow connections
 #httpclient.socket.http.cps=0
 #httpclient.socket.https.cps=0
 
 #Enable loopback protocol
 #httpclient.loopback=true
 
 # Define the local host address to be used for multi-homed hosts
 #httpclient.localaddress=1.2.3.4
 
 #---------------------------------------------------------------------------
 # AuthManager Kerberos configuration
 #---------------------------------------------------------------------------
 
 # AuthManager Kerberos configuration
 # Name of application module used in jaas.conf
 #kerberos_jaas_application=JMeter  
 
 # Should ports be stripped from urls before constructing SPNs
 # for SPNEGO authentication
 #kerberos.spnego.strip_port=true
 
 #---------------------------------------------------------------------------
 # Sample logging levels for Commons HttpClient
 #---------------------------------------------------------------------------
 
 # Commons HttpClient Logging information can be found at:
 # http://hc.apache.org/httpclient-3.x/logging.html
 
 # Note that full category names are used, i.e. must include the org.apache.
 # Info level produces no output:
 #log_level.org.apache.commons.httpclient=debug
 # Might be useful:
 #log_level.org.apache.commons.httpclient.Authenticator=trace 
 
 # Show headers only
 #log_level.httpclient.wire.header=debug
 
 # Full wire debug produces a lot of output; consider using separate file:
 #log_level.httpclient.wire=debug
 #log_file.httpclient=httpclient.log
 
 #---------------------------------------------------------------------------
 # Apache HttpClient logging examples
 #---------------------------------------------------------------------------
 
 # Enable header wire + context logging - Best for Debugging
 #log_level.org.apache.http=DEBUG
 #log_level.org.apache.http.wire=ERROR
 
 # Enable full wire + context logging
 #log_level.org.apache.http=DEBUG
 
 # Enable context logging for connection management
 #log_level.org.apache.http.impl.conn=DEBUG
 
 # Enable context logging for connection management / request execution
 #log_level.org.apache.http.impl.conn=DEBUG
 #log_level.org.apache.http.impl.client=DEBUG
 #log_level.org.apache.http.client=DEBUG
 
 #---------------------------------------------------------------------------
 # Apache HttpComponents HTTPClient configuration (HTTPClient4)
 #---------------------------------------------------------------------------
 
 # define a properties file for overriding Apache HttpClient parameters
 # Uncomment this line if you put anything in hc.parameters file
 #hc.parameters.file=hc.parameters
 
 # Number of retries to attempt (default 0)
 #httpclient4.retrycount=0
 
 # Idle connection timeout (Milliseconds) to apply if the server does not send
 # Keep-Alive headers (default 0)
 # Set this > 0 to compensate for servers that don't send a Keep-Alive header
 # If <= 0, idle timeout will only apply if the server sends a Keep-Alive header
 #httpclient4.idletimeout=0
 
 # Check connections if the elapsed time (Milliseconds) since the last 
 # use of the connection exceed this value
 #httpclient4.validate_after_inactivity=2000
 
 # TTL (in Milliseconds) represents an absolute value. 
 # No matter what, the connection will not be re-used beyond its TTL. 
 #httpclient4.time_to_live=2000
 
 # Max size in bytes of PUT body to retain in result sampler. Bigger results will be clipped.
 #httpclient4.max_body_retain_size=32768
 
 #---------------------------------------------------------------------------
 # Apache HttpComponents Commons HTTPClient configuration (HTTPClient 3.1)
 #                            DEPRECATED
 #---------------------------------------------------------------------------
 
 # define a properties file for overriding Commons HttpClient parameters
 # See: http://hc.apache.org/httpclient-3.x/preference-api.html
 # Uncomment this line if you put anything in httpclient.parameters file
 #httpclient.parameters.file=httpclient.parameters
 
 # Number of retries to attempt (default 0)
 #httpclient3.retrycount=0
 
 #---------------------------------------------------------------------------
 # HTTP Cache Manager configuration
 #---------------------------------------------------------------------------
 #
 # Space or comma separated list of methods that can be cached
 #cacheable_methods=GET
 # N.B. This property is currently a temporary solution for Bug 56162
 
 # Since 2.12, JMeter does not create anymore a Sample Result with 204 response 
 # code for a resource found in cache which is inline with what browser do.
 #cache_manager.cached_resource_mode=RETURN_NO_SAMPLE
 
 # You can choose between 3 modes:
 # RETURN_NO_SAMPLE (default)
 # RETURN_200_CACHE
 # RETURN_CUSTOM_STATUS
 
 # Those mode have the following behaviours:
 # RETURN_NO_SAMPLE : this mode returns no Sample Result, it has no additional configuration
 # RETURN_200_CACHE : this mode will return Sample Result with response code to 200 and response message to "(ex cache)", you can modify response message by setting 
 # RETURN_200_CACHE.message=(ex cache)
 # RETURN_CUSTOM_STATUS : This mode lets you select what response code and message you want to return, if you use this mode you need to set those properties
 # RETURN_CUSTOM_STATUS.code=
 # RETURN_CUSTOM_STATUS.message=
 
 #---------------------------------------------------------------------------
 # Results file configuration
 #---------------------------------------------------------------------------
 
 # This section helps determine how result data will be saved.
 # The commented out values are the defaults.
 
 # legitimate values: xml, csv, db.  Only xml and csv are currently supported.
 #jmeter.save.saveservice.output_format=csv
 
 
 # true when field should be saved; false otherwise
 
 # assertion_results_failure_message only affects CSV output
 #jmeter.save.saveservice.assertion_results_failure_message=true
 #
 # legitimate values: none, first, all
 #jmeter.save.saveservice.assertion_results=none
 #
 #jmeter.save.saveservice.data_type=true
 #jmeter.save.saveservice.label=true
 #jmeter.save.saveservice.response_code=true
 # response_data is not currently supported for CSV output
 #jmeter.save.saveservice.response_data=false
 # Save ResponseData for failed samples
 #jmeter.save.saveservice.response_data.on_error=false
 #jmeter.save.saveservice.response_message=true
 #jmeter.save.saveservice.successful=true
 #jmeter.save.saveservice.thread_name=true
 #jmeter.save.saveservice.time=true
 #jmeter.save.saveservice.subresults=true
 #jmeter.save.saveservice.assertions=true
 #jmeter.save.saveservice.latency=true
 #jmeter.save.saveservice.connect_time=true
 #jmeter.save.saveservice.samplerData=false
 #jmeter.save.saveservice.responseHeaders=false
 #jmeter.save.saveservice.requestHeaders=false
 #jmeter.save.saveservice.encoding=false
 #jmeter.save.saveservice.bytes=true
+#jmeter.save.saveservice.sent_bytes=true
 #jmeter.save.saveservice.url=false
 #jmeter.save.saveservice.filename=false
 #jmeter.save.saveservice.hostname=false
 #jmeter.save.saveservice.thread_counts=true
 #jmeter.save.saveservice.sample_count=false
 #jmeter.save.saveservice.idle_time=true
 
 # Timestamp format - this only affects CSV output files
 # legitimate values: none, ms, or a format suitable for SimpleDateFormat
 #jmeter.save.saveservice.timestamp_format=ms
 #jmeter.save.saveservice.timestamp_format=yyyy/MM/dd HH:mm:ss.SSS
 
 # For use with Comma-separated value (CSV) files or other formats
 # where the fields' values are separated by specified delimiters.
 # Default:
 #jmeter.save.saveservice.default_delimiter=,
 # For TAB, since JMeter 2.3 one can use:
 #jmeter.save.saveservice.default_delimiter=\t
 
 # Only applies to CSV format files:
 # Print field names as first line in CSV
 #jmeter.save.saveservice.print_field_names=true
 
 # Optional list of JMeter variable names whose values are to be saved in the result data files.
 # Use commas to separate the names. For example:
 #sample_variables=SESSION_ID,REFERENCE
 # N.B. The current implementation saves the values in XML as attributes,
 # so the names must be valid XML names.
 # Versions of JMeter after 2.3.2 send the variable to all servers
 # to ensure that the correct data is available at the client.
 
 # Optional xml processing instruction for line 2 of the file:
 # Example:
 #jmeter.save.saveservice.xml_pi=<?xml-stylesheet type="text/xsl" href="../extras/jmeter-results-detail-report.xsl"?>
 # Default value:
 #jmeter.save.saveservice.xml_pi=
 
 # Prefix used to identify filenames that are relative to the current base
 #jmeter.save.saveservice.base_prefix=~/
 
 # AutoFlush on each line written in XML or CSV output
 # Setting this to true will result in less test results data loss in case of Crash
 # but with impact on performances, particularly for intensive tests (low or no pauses)
 # Since JMeter 2.10, this is false by default
 #jmeter.save.saveservice.autoflush=false
 
 #---------------------------------------------------------------------------
 # Settings that affect SampleResults
 #---------------------------------------------------------------------------
 
 # Save the start time stamp instead of the end
 # This also affects the timestamp stored in result files
 sampleresult.timestamp.start=true
 
 # Whether to use System.nanoTime() - otherwise only use System.currentTimeMillis()
 #sampleresult.useNanoTime=true
 
 # Use a background thread to calculate the nanoTime offset
 # Set this to <= 0 to disable the background thread
 #sampleresult.nanoThreadSleep=5000
 
 #---------------------------------------------------------------------------
 # Upgrade property
 #---------------------------------------------------------------------------
 
 # File that holds a record of name changes for backward compatibility issues
 upgrade_properties=/bin/upgrade.properties
 
 #---------------------------------------------------------------------------
 # JMeter Test Script recorder configuration
 #
 # N.B. The element was originally called the Proxy recorder, which is why the
 # properties have the prefix "proxy".
 #---------------------------------------------------------------------------
 
 # If the recorder detects a gap of at least 5s (default) between HTTP requests,
 # it assumes that the user has clicked a new URL
 #proxy.pause=5000
 
 # Add numeric prefix to Sampler names (default true)
 #proxy.number.requests=true
 
 # List of URL patterns that will be added to URL Patterns to exclude
 # Separate multiple lines with ;
 #proxy.excludes.suggested=.*\\.(bmp|css|js|gif|ico|jpe?g|png|swf|woff)
 
 # Change the default HTTP Sampler (currently HttpClient4)
 # Java:
 #jmeter.httpsampler=HTTPSampler
 #or
 #jmeter.httpsampler=Java
 #
 # Apache HTTPClient:
 #jmeter.httpsampler=HTTPSampler2
 #or
 #jmeter.httpsampler=HttpClient3.1
 #
 # HttpClient4.x
 #jmeter.httpsampler=HttpClient4
 
 # By default JMeter tries to be more lenient with RFC2616 redirects and allows
 # relative paths.
 # If you want to test strict conformance, set this value to true
 # When the property is true, JMeter follows http://tools.ietf.org/html/rfc3986#section-5.2
 #jmeter.httpclient.strict_rfc2616=false
 
 # Default content-type include filter to use
 #proxy.content_type_include=text/html|text/plain|text/xml
 # Default content-type exclude filter to use
 #proxy.content_type_exclude=image/.*|text/css|application/.*
 
 # Default headers to remove from Header Manager elements
 # (Cookie and Authorization are always removed)
 #proxy.headers.remove=If-Modified-Since,If-None-Match,Host
 
 # Binary content-type handling
 # These content-types will be handled by saving the request in a file:
 #proxy.binary.types=application/x-amf,application/x-java-serialized-object
 # The files will be saved in this directory:
 #proxy.binary.directory=user.dir
 # The files will be created with this file filesuffix:
 #proxy.binary.filesuffix=.binary
 
 #---------------------------------------------------------------------------
 # Test Script Recorder certificate configuration
 #---------------------------------------------------------------------------
 
 #proxy.cert.directory=<JMeter bin directory>
 #proxy.cert.file=proxyserver.jks
 #proxy.cert.type=JKS
 #proxy.cert.keystorepass=password
 #proxy.cert.keypassword=password
 #proxy.cert.factory=SunX509
 # define this property if you wish to use your own keystore
 #proxy.cert.alias=<none>
 # The default validity for certificates created by JMeter
 #proxy.cert.validity=7
 # Use dynamic key generation (if supported by JMeter/JVM)
 # If false, will revert to using a single key with no certificate
 #proxy.cert.dynamic_keys=true
 
 #---------------------------------------------------------------------------
 # Test Script Recorder miscellaneous configuration
 #---------------------------------------------------------------------------
 
 # Whether to attempt disabling of samples that resulted from redirects
 # where the generated samples use auto-redirection
 #proxy.redirect.disabling=true
 
 # SSL configuration
 #proxy.ssl.protocol=TLS
 
 #---------------------------------------------------------------------------
 # JMeter Proxy configuration
 #---------------------------------------------------------------------------
 # use command-line flags for user-name and password
 #http.proxyDomain=NTLM domain, if required by HTTPClient sampler
 
 #---------------------------------------------------------------------------
 # HTTPSampleResponse Parser configuration
 #---------------------------------------------------------------------------
 
 # Space-separated list of parser groups
 HTTPResponse.parsers=htmlParser wmlParser cssParser
 # for each parser, there should be a parser.types and a parser.className property
 
 # CSS Parser based on ph-css
 cssParser.className=org.apache.jmeter.protocol.http.parser.CssParser
 cssParser.types=text/css
 
 # CSS parser LRU cache size
 # This cache stores the URLs found in a CSS to avoid continuously parsing the CSS
 # By default the cache size is 400
 # It can be disabled by setting its value to 0
 #css.parser.cache.size=400
 
 #---------------------------------------------------------------------------
 # HTML Parser configuration
 #---------------------------------------------------------------------------
 
 # Define the HTML parser to be used.
 # Default parser:
 # This new parser (since 2.10) should perform better than all others
 # see https://bz.apache.org/bugzilla/show_bug.cgi?id=55632
 # Do not comment this property
 htmlParser.className=org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser
 
 # Other parsers:
 # Default parser before 2.10
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.JTidyHTMLParser
 # Note that Regexp extractor may detect references that have been commented out.
 # In many cases it will work OK, but you should be aware that it may generate 
 # additional references.
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.RegexpHTMLParser
 # This parser is based on JSoup, it should be the most accurate but less performant
 # than LagartoBasedHtmlParser
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.JsoupBasedHtmlParser
 
 #Used by HTTPSamplerBase to associate htmlParser with content types below 
 htmlParser.types=text/html application/xhtml+xml application/xml text/xml
 
 #---------------------------------------------------------------------------
 # WML Parser configuration
 #---------------------------------------------------------------------------
 
 wmlParser.className=org.apache.jmeter.protocol.http.parser.RegexpHTMLParser
 
 #Used by HTTPSamplerBase to associate wmlParser with content types below 
 wmlParser.types=text/vnd.wap.wml 
 
 #---------------------------------------------------------------------------
 # Remote batching configuration
 #---------------------------------------------------------------------------
 # How is Sample sender implementations configured:
 # - true (default) means client configuration will be used
 # - false means server configuration will be used
 #sample_sender_client_configured=true
 
 # By default when Stripping modes are used JMeter since 3.1 will strip 
 # response even for SampleResults in error.
 # If you want to revert to previous behaviour (no stripping of Responses in error) 
 # set this property to false
 #sample_sender_strip_also_on_error=true
 
 # Remote batching support
 # Since JMeter 2.9, default is MODE_STRIPPED_BATCH, which returns samples in
 # batch mode (every 100 samples or every minute by default)
 # Note also that MODE_STRIPPED_BATCH strips response data from SampleResult, so if you need it change to
 # another mode
 # Hold retains samples until end of test (may need lots of memory)
 # Batch returns samples in batches
 # Statistical returns sample summary statistics
 # hold_samples was originally defined as a separate property,
 # but can now also be defined using mode=Hold
 # mode can also be the class name of an implementation of org.apache.jmeter.samplers.SampleSender
 #mode=Standard
 #mode=Batch
 #mode=Hold
 #mode=Statistical
 #Set to true to key statistical samples on threadName rather than threadGroup
 #key_on_threadname=false
 #mode=Stripped
 #mode=StrippedBatch
 #mode=org.example.load.MySampleSender
 #
 #num_sample_threshold=100
 # Value is in milliseconds
 #time_threshold=60000
 #
 # Asynchronous sender; uses a queue and background worker process to return the samples
 #mode=Asynch
 # default queue size
 #asynch.batch.queue.size=100
 # Same as Asynch but strips response data from SampleResult
 #mode=StrippedAsynch
 #
 # DiskStore: as for Hold mode, but serialises the samples to disk, rather than saving in memory
 #mode=DiskStore
 # Same as DiskStore but strips response data from SampleResult
 #mode=StrippedDiskStore
 # Note: the mode is currently resolved on the client; 
 # other properties (e.g. time_threshold) are resolved on the server.
 
 # To set the Monitor Health Visualiser buffer size, enter the desired value
 # monitor.buffer.size=800
 
 #---------------------------------------------------------------------------
 # JDBC Request configuration
 #---------------------------------------------------------------------------
 
 # String used to indicate a null value
 #jdbcsampler.nullmarker=]NULL[
 #
 # Max size of BLOBs and CLOBs to store in JDBC sampler. Result will be cut off
 #jdbcsampler.max_retain_result_size=65536
 
 #---------------------------------------------------------------------------
 # OS Process Sampler configuration
 #---------------------------------------------------------------------------
 # Polling to see if process has finished its work, used when a timeout is configured on sampler
 #os_sampler.poll_for_timeout=100
 
 #---------------------------------------------------------------------------
 # TCP Sampler configuration
 #---------------------------------------------------------------------------
 
 # The default handler class
 #tcp.handler=TCPClientImpl
 #
 # eolByte = byte value for end of line
 # set this to a value outside the range -128 to +127 to skip eol checking
 #tcp.eolByte=1000
 #
 # TCP Charset, used by org.apache.jmeter.protocol.tcp.sampler.TCPClientImpl
 # default to Platform defaults charset as returned by Charset.defaultCharset().name()
 #tcp.charset=
 #
 # status.prefix and suffix = strings that enclose the status response code
 #tcp.status.prefix=Status=
 #tcp.status.suffix=.
 #
 # status.properties = property file to convert codes to messages
 #tcp.status.properties=mytestfiles/tcpstatus.properties
 
 # The length prefix used by LengthPrefixedBinaryTCPClientImpl implementation
 # defaults to 2 bytes.
 #tcp.binarylength.prefix.length=2
 
 #---------------------------------------------------------------------------
 # Summariser - Generate Summary Results - configuration (mainly applies to non-GUI mode)
 #---------------------------------------------------------------------------
 #
 # Comment the following property to disable the default non-GUI summariser
 # [or change the value to rename it]
 # (applies to non-GUI mode only)
 summariser.name=summary
 #
 # interval between summaries (in seconds) default 30 seconds
 #summariser.interval=30
 #
 # Write messages to log file
 #summariser.log=true
 #
 # Write messages to System.out
 #summariser.out=true
 
 # Ignore SampleResults generated by TransactionControllers
 # defaults to true 
 #summariser.ignore_transaction_controller_sample_result=true
 
 
 #---------------------------------------------------------------------------
 # Aggregate Report and Aggregate Graph - configuration
 #---------------------------------------------------------------------------
 #
 # Percentiles to display in reports
 # Can be float value between 0 and 100
 # First percentile to display, defaults to 90%
 #aggregate_rpt_pct1=90
 # Second percentile to display, defaults to 95%
 #aggregate_rpt_pct2=95
 # Second percentile to display, defaults to 99%
 #aggregate_rpt_pct3=99
 
 #---------------------------------------------------------------------------
 # BackendListener - configuration
 #---------------------------------------------------------------------------
 #
 # Backend metrics sliding window size for Percentiles, Min, Max
 #backend_metrics_window=100
 
 #---------------------------------------------------------------------------
 # BeanShell configuration
 #---------------------------------------------------------------------------
 
 # BeanShell Server properties
 #
 # Define the port number as non-zero to start the http server on that port
 #beanshell.server.port=9000
 # The telnet server will be started on the next port
 
 #
 # Define the server initialisation file
 beanshell.server.file=../extras/startup.bsh
 
 #
 # Define a file to be processed at startup
 # This is processed using its own interpreter.
 #beanshell.init.file=
 
 #
 # Define the intialisation files for BeanShell Sampler, Function and other BeanShell elements
 # N.B. Beanshell test elements do not share interpreters.
 #      Each element in each thread has its own interpreter.
 #      This is retained between samples.
 #beanshell.sampler.init=BeanShellSampler.bshrc
 #beanshell.function.init=BeanShellFunction.bshrc
 #beanshell.assertion.init=BeanShellAssertion.bshrc
 #beanshell.listener.init=etc
 #beanshell.postprocessor.init=etc
 #beanshell.preprocessor.init=etc
 #beanshell.timer.init=etc
 
 # The file BeanShellListeners.bshrc contains sample definitions
 # of Test and Thread Listeners.
 
 #---------------------------------------------------------------------------
 # Groovy function
 #---------------------------------------------------------------------------
 
 #Path to Groovy file containing utility functions to make available to __g function
 #groovy.utilities=
 
 # Example 
 #groovy.utilities=bin/utility.groovy
 
 #---------------------------------------------------------------------------
 # MailerModel configuration
 #---------------------------------------------------------------------------
 
 # Number of successful samples before a message is sent
 #mailer.successlimit=2
 #
 # Number of failed samples before a message is sent
 #mailer.failurelimit=2
 
 #---------------------------------------------------------------------------
 # CSVRead configuration
 #---------------------------------------------------------------------------
 
 # CSVRead delimiter setting (default ",")
 # Make sure that there are no trailing spaces or tabs after the delimiter
 # characters, or these will be included in the list of valid delimiters
 #csvread.delimiter=,
 #csvread.delimiter=;
 #csvread.delimiter=!
 #csvread.delimiter=~
 # The following line has a tab after the =
 #csvread.delimiter=	
 
 #---------------------------------------------------------------------------
 # __time() function configuration
 #
 # The properties below can be used to redefine the default formats
 #---------------------------------------------------------------------------
 #time.YMD=yyyyMMdd
 #time.HMS=HHmmss
 #time.YMDHMS=yyyyMMdd-HHmmss
 #time.USER1=
 #time.USER2=
 
 #---------------------------------------------------------------------------
 # CSV DataSet configuration
 #---------------------------------------------------------------------------
 
 # String to return at EOF (if recycle not used)
 #csvdataset.eofstring=<EOF>
 
 #---------------------------------------------------------------------------
 # LDAP Sampler configuration
 #---------------------------------------------------------------------------
 # Maximum number of search results returned by a search that will be sorted
 # to guarantee a stable ordering (if more results then this limit are returned
 # then no sorting is done). Set to 0 to turn off all sorting, in which case
 # "Equals" response assertions will be very likely to fail against search results.
 #
 #ldapsampler.max_sorted_results=1000
  
 # Number of characters to log for each of three sections (starting matching section, diff section,
 #   ending matching section where not all sections will appear for all diffs) diff display when an Equals
 #   assertion fails. So a value of 100 means a maximum of 300 characters of diff text will be displayed
 #   (+ a number of extra characters like "..." and "[[["/"]]]" which are used to decorate it).
 #assertion.equals_section_diff_len=100
 # test written out to log to signify start/end of diff delta
 #assertion.equals_diff_delta_start=[[[
 #assertion.equals_diff_delta_end=]]]
 
 #---------------------------------------------------------------------------
 # Miscellaneous configuration
 #---------------------------------------------------------------------------
 
 # If defined, then start the mirror server on the port
 #mirror.server.port=8081
 
 # ORO PatternCacheLRU size
 #oro.patterncache.size=1000
 
 #TestBeanGui
 #
 #propertyEditorSearchPath=null
 
 # Turn expert mode on/off: expert mode will show expert-mode beans and properties
 #jmeter.expertMode=true
 
 # Maximum redirects to follow in a single sequence (default 20)
 #httpsampler.max_redirects=20
 # Maximum frame/iframe nesting depth (default 5)
 #httpsampler.max_frame_depth=5
 
 # Revert to BUG 51939 behaviour (no separate container for embedded resources) by setting the following false:
 #httpsampler.separate.container=true
 
 # If embedded resources download fails due to missing resources or other reasons, if this property is true
 # Parent sample will not be marked as failed 
 #httpsampler.ignore_failed_embedded_resources=false
 
 #keep alive time for the parallel download threads (in seconds)
 #httpsampler.parallel_download_thread_keepalive_inseconds=60
 
 # Don't keep the embedded resources response data : just keep the size and the md5
 # default to false
 #httpsampler.embedded_resources_use_md5=false
 
 # List of extra HTTP methods that should be available in select box
 #httpsampler.user_defined_methods=VERSION-CONTROL,REPORT,CHECKOUT,CHECKIN,UNCHECKOUT,MKWORKSPACE,UPDATE,LABEL,MERGE,BASELINE-CONTROL,MKACTIVITY
 
 # The encoding to be used if none is provided (default ISO-8859-1)
 #sampleresult.default.encoding=ISO-8859-1
 
 # Network response size calculation method
 # Use real size: number of bytes for response body return by webserver
 # (i.e. the network bytes received for response)
 # if set to false, the (uncompressed) response data size will used (default before 2.5)
 # Include headers: add the headers size in real size
 #sampleresult.getbytes.body_real_size=true
 #sampleresult.getbytes.headers_size=true
 
 # CookieManager behaviour - should cookies with null/empty values be deleted?
 # Default is true. Use false to revert to original behaviour
 #CookieManager.delete_null_cookies=true
 
 # CookieManager behaviour - should variable cookies be allowed?
 # Default is true. Use false to revert to original behaviour
 #CookieManager.allow_variable_cookies=true
 
 # CookieManager behaviour - should Cookies be stored as variables?
 # Default is false
 #CookieManager.save.cookies=false
 
 # CookieManager behaviour - prefix to add to cookie name before storing it as a variable
 # Default is COOKIE_; to remove the prefix, define it as one or more spaces
 #CookieManager.name.prefix=
  
 # CookieManager behaviour - check received cookies are valid before storing them?
 # Default is true. Use false to revert to previous behaviour
 #CookieManager.check.cookies=true
 
 # Netscape HTTP Cookie file
 cookies=cookies
 
 # Ability to switch to Nashorn as default Javascript Engine used by IfController and __javaScript function
 # JMeter works as following:
 # - JDK < 8 : Rhino
 # - JDK >= 8 and javascript.use_rhino=false: Nashorn
 # If you want to use Nashorn on JDK8, set this property to false
 #javascript.use_rhino=true
 
 # Number of milliseconds to wait for a thread to stop
 #jmeterengine.threadstop.wait=5000
 
 #Whether to invoke System.exit(0) in server exit code after stopping RMI
 #jmeterengine.remote.system.exit=false
 
 # Whether to call System.exit(1) on failure to stop threads in non-GUI mode.
 # This only takes effect if the test was explicitly requested to stop.
 # If this is disabled, it may be necessary to kill the JVM externally
 #jmeterengine.stopfail.system.exit=true
 
 # Whether to force call System.exit(0) at end of test in non-GUI mode, even if
 # there were no failures and the test was not explicitly asked to stop.
 # Without this, the JVM may never exit if there are other threads spawned by
 # the test which never exit.
 #jmeterengine.force.system.exit=false
 
 # How long to pause (in ms) in the daemon thread before reporting that the JVM has failed to exit.
 # If the value is <= 0, the JMeter does not start the daemon thread 
 #jmeter.exit.check.pause=2000
 
 # If running non-GUI, then JMeter listens on the following port for a shutdown message.
 # To disable, set the port to 1000 or less.
 #jmeterengine.nongui.port=4445
 #
 # If the initial port is busy, keep trying until this port is reached
 # (to disable searching, set the value less than or equal to the .port property)
 #jmeterengine.nongui.maxport=4455
 
 # How often to check for shutdown during ramp-up (milliseconds)
 #jmeterthread.rampup.granularity=1000
 
 #Should JMeter expand the tree when loading a test plan?
 # default value is false since JMeter 2.7
 #onload.expandtree=false
 
 #JSyntaxTextArea configuration
 #jsyntaxtextarea.wrapstyleword=true
 #jsyntaxtextarea.linewrap=true
 #jsyntaxtextarea.codefolding=true
 # Set 0 to disable undo feature in JSyntaxTextArea
 #jsyntaxtextarea.maxundos=50
 # Change the font on the (JSyntax) Text Areas. (Useful for HiDPI screens)
 #jsyntaxtextarea.font.family=Hack
 #jsyntaxtextarea.font.size=14
 
 # Set this to false to disable the use of JSyntaxTextArea for the Console Logger panel 
 #loggerpanel.usejsyntaxtext=true
 
 # Maximum size of HTML page that can be displayed; default=10 mbytes
 # Set to 0 to disable the size check and display the whole response
 #view.results.tree.max_size=10485760
 
 # Order of Renderers in View Results Tree
 # Note full class names should be used for non jmeter core renderers
 # For JMeter core renderers, class names start with . and are automatically
 # prefixed with org.apache.jmeter.visualizers
 view.results.tree.renderers_order=.RenderAsText,.RenderAsRegexp,.RenderAsCssJQuery,.RenderAsXPath,.RenderAsHTML,.RenderAsHTMLWithEmbedded,.RenderAsHTMLFormatted,.RenderAsDocument,.RenderAsJSON,.RenderAsXML
 
 # Maximum size of Document that can be parsed by Tika engine; defaut=10 * 1024 * 1024 (10MB)
 # Set to 0 to disable the size check
 #document.max_size=0
 
 #JMS options
 # Enable the following property to stop JMS Point-to-Point Sampler from using
 # the properties java.naming.security.[principal|credentials] when creating the queue connection
 #JMSSampler.useSecurity.properties=false
 
 # Set the following value to true in order to skip the delete confirmation dialogue
 #confirm.delete.skip=false
 
 # Used by JSR223 elements
 # Size of compiled scripts cache
 #jsr223.compiled_scripts_cache_size=100
 
 #---------------------------------------------------------------------------
 # Classpath configuration
 #---------------------------------------------------------------------------
 
 # List of directories (separated by ;) to search for additional JMeter plugin classes,
 # for example new GUI elements and samplers.
 # Any jar file in such a directory will be automatically included,
 # jar files in sub directories are ignored.
 # The given value is in addition to any jars found in the lib/ext directory.
 # Do not use this for utility or plugin dependency jars.
 #search_paths=/app1/lib;/app2/lib
 
 # List of directories that JMeter will search for utility and plugin dependency classes.
 # Use your platform path separator to separate multiple paths.
 # Any jar file in such a directory will be automatically included,
 # jar files in sub directories are ignored.
 # The given value is in addition to any jars found in the lib directory.
 # All entries will be added to the class path of the system class loader
 # and also to the path of the JMeter internal loader.
 # Paths with spaces may cause problems for the JVM
 #user.classpath=../classes;../lib
 
 # List of directories (separated by ;) that JMeter will search for utility
 # and plugin dependency classes.
 # Any jar file in such a directory will be automatically included,
 # jar files in sub directories are ignored.
 # The given value is in addition to any jars found in the lib directory
 # or given by the user.classpath property.
 # All entries will be added to the path of the JMeter internal loader only.
 # For plugin dependencies this property should be used instead of user.classpath.
 #plugin_dependency_paths=../dependencies/lib;../app1/;../app2/
 
 # Classpath finder
 # ================
 # The classpath finder currently needs to load every single JMeter class to find
 # the classes it needs.
 # For non-GUI mode, it's only necessary to scan for Function classes, but all classes
 # are still loaded.
 # All current Function classes include ".function." in their name,
 # and none include ".gui." in the name, so the number of unwanted classes loaded can be
 # reduced by checking for these. However, if a valid function class name does not match
 # these restrictions, it will not be loaded. If problems are encountered, then comment
 # or change the following properties:
 classfinder.functions.contain=.functions.
 classfinder.functions.notContain=.gui.
 
 
 #---------------------------------------------------------------------------
 # Additional property files to load
 #---------------------------------------------------------------------------
 
 # Should JMeter automatically load additional JMeter properties?
 # File name to look for (comment to disable)
 user.properties=user.properties
 
 # Should JMeter automatically load additional system properties?
 # File name to look for (comment to disable)
 system.properties=system.properties
 
 # Comma separated list of files that contain reference to templates and their description
 # Path must be relative to JMeter root folder
 #template.files=/bin/templates/templates.xml
 
 
 #---------------------------------------------------------------------------
 # Thread Group Validation feature
 #---------------------------------------------------------------------------
 
 # Validation is the name of the feature used to rapidly validate a Thread Group runs fine
 # Default implementation is org.apache.jmeter.gui.action.validation.TreeClonerForValidation
 # It runs validation without timers, with 1 thread, 1 iteration and Startup Delay set to 0
 # You can implement your own policy that must extend org.apache.jmeter.engine.TreeCloner
 # JMeter will instantiate it and use it to create the Tree used to run validation on Thread Group
 #testplan_validation.tree_cloner_class=org.apache.jmeter.validation.ComponentTreeClonerForValidation
 
 # Number of threads to use to validate a Thread Group
 #testplan_validation.nb_threads_per_thread_group=1
 
 # Ignore timers when validating the thread group of plan
 #testplan_validation.ignore_timers=true
 
 # Number of iterations to use to validate a Thread Group
 #testplan_validation.number_iterations=1
 
 # Force throuput controllers that work in percentage mode to be a 100%
 # Disabled by default
 #testplan_validation.tpc_force_100_pct=false
 
 
 #
 # Apply a factor on computed pauses by the following Timers:
 # - Gaussian Random Timer
 # - Uniform Random Timer
 # - Poisson Random Timer
 #
 #timer.factor=1.0f
diff --git a/src/components/org/apache/jmeter/visualizers/SamplerResultTab.java b/src/components/org/apache/jmeter/visualizers/SamplerResultTab.java
index 20481c3ee..a279e3b84 100644
--- a/src/components/org/apache/jmeter/visualizers/SamplerResultTab.java
+++ b/src/components/org/apache/jmeter/visualizers/SamplerResultTab.java
@@ -1,566 +1,567 @@
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
  * distributed  under the  License is distributed on an "AS IS" BASIS,
  * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
  * implied.
  *
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Component;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.LinkedHashMap;
 import java.util.Map.Entry;
 import java.util.Set;
 
 import javax.swing.BorderFactory;
 import javax.swing.Icon;
 import javax.swing.ImageIcon;
 import javax.swing.JEditorPane;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTable;
 import javax.swing.JTextPane;
 import javax.swing.SwingConstants;
 import javax.swing.table.TableCellRenderer;
 import javax.swing.table.TableColumn;
 import javax.swing.text.BadLocationException;
 import javax.swing.text.Style;
 import javax.swing.text.StyleConstants;
 import javax.swing.text.StyledDocument;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.TextBoxDialoger.TextBoxDoubleClick;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.reflect.Functor;
 
 /**
  * Right side in View Results Tree
  *
  */
 public abstract class SamplerResultTab implements ResultRenderer {
 
     // N.B. these are not multi-threaded, so don't make it static
     private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // ISO format $NON-NLS-1$
 
     private static final String NL = "\n"; // $NON-NLS-1$
 
     public static final Color SERVER_ERROR_COLOR = Color.red;
 
     public static final Color CLIENT_ERROR_COLOR = Color.blue;
 
     public static final Color REDIRECT_COLOR = Color.green;
 
     protected static final String TEXT_COMMAND = "text"; // $NON-NLS-1$
     
     protected static final String REQUEST_VIEW_COMMAND = "change_request_view"; // $NON-NLS-1$
 
     private static final String STYLE_SERVER_ERROR = "ServerError"; // $NON-NLS-1$
 
     private static final String STYLE_CLIENT_ERROR = "ClientError"; // $NON-NLS-1$
 
     private static final String STYLE_REDIRECT = "Redirect"; // $NON-NLS-1$
 
     private JTextPane stats;
 
     /** Response Data pane */
     private JPanel resultsPane;
     
     /** Contains results; contained in resultsPane */
     protected JScrollPane resultsScrollPane;
     
     /** Response Data shown here */
     protected JEditorPane results;
 
     private JLabel imageLabel;
 
     /** request pane content */
     private RequestPanel requestPanel;
 
     /** holds the tabbed panes */
     protected JTabbedPane rightSide;
 
     private int lastSelectedTab;
 
     private Object userObject = null; // Could be SampleResult or AssertionResult
 
     private SampleResult sampleResult = null;
 
     private AssertionResult assertionResult = null;
 
     protected SearchTextExtension searchTextExtension;
 
     private JPanel searchPanel = null;
 
     protected boolean activateSearchExtension = true; // most current subclasses can process text
 
     private Color backGround;
     
     private static final String[] COLUMNS_RESULT = new String[] {
             " ", // one space for blank header // $NON-NLS-1$ 
             " " }; // one space for blank header  // $NON-NLS-1$
 
     private static final String[] COLUMNS_HEADERS = new String[] {
             "view_results_table_headers_key", // $NON-NLS-1$
             "view_results_table_headers_value" }; // $NON-NLS-1$
 
     private static final String[] COLUMNS_FIELDS = new String[] {
             "view_results_table_fields_key", // $NON-NLS-1$
             "view_results_table_fields_value" }; // $NON-NLS-1$
 
     private ObjectTableModel resultModel = null;
 
     private ObjectTableModel resHeadersModel = null;
 
     private ObjectTableModel resFieldsModel = null;
 
     private JTable tableResult = null;
 
     private JTable tableResHeaders = null;
 
     private JTable tableResFields = null;
 
     private JTabbedPane tabbedResult = null;
 
     private JScrollPane paneRaw = null;
     
     private JSplitPane paneParsed = null;
     
     // to save last select tab (raw/parsed)
     private int lastResultTabIndex= 0; 
 
     // Result column renderers
     private static final TableCellRenderer[] RENDERERS_RESULT = new TableCellRenderer[] {
             null, // Key
             null, // Value
     };
 
     // Response headers column renderers
     private static final TableCellRenderer[] RENDERERS_HEADERS = new TableCellRenderer[] {
             null, // Key
             null, // Value
     };
 
     // Response fields column renderers
     private static final TableCellRenderer[] RENDERERS_FIELDS = new TableCellRenderer[] {
             null, // Key
             null, // Value
     };
 
     public SamplerResultTab() {
         // create tables
         resultModel = new ObjectTableModel(COLUMNS_RESULT, RowResult.class, // The object used for each row
                 new Functor[] {
                         new Functor("getKey"), // $NON-NLS-1$
                         new Functor("getValue") }, // $NON-NLS-1$
                 new Functor[] {
                         null, null }, new Class[] {
                         String.class, String.class }, false);
         resHeadersModel = new ObjectTableModel(COLUMNS_HEADERS,
                 RowResult.class, // The object used for each row
                 new Functor[] {
                         new Functor("getKey"), // $NON-NLS-1$
                         new Functor("getValue") }, // $NON-NLS-1$
                 new Functor[] {
                         null, null }, new Class[] {
                         String.class, String.class }, false);
         resFieldsModel = new ObjectTableModel(COLUMNS_FIELDS, RowResult.class, // The object used for each row
                 new Functor[] {
                         new Functor("getKey"), // $NON-NLS-1$
                         new Functor("getValue") }, // $NON-NLS-1$
                 new Functor[] {
                         null, null }, new Class[] {
                         String.class, String.class }, false);
     }
 
     @Override
     public void clearData() {
         results.setText("");// Response Data // $NON-NLS-1$
         requestPanel.clearData();// Request Data // $NON-NLS-1$
         stats.setText(""); // Sampler result // $NON-NLS-1$
         resultModel.clearData();
         resHeadersModel.clearData();
         resFieldsModel.clearData();
     }
 
     @Override
     public void init() {
         rightSide.addTab(JMeterUtils.getResString("view_results_tab_sampler"), createResponseMetadataPanel()); // $NON-NLS-1$
         // Create the panels for the other tabs
         requestPanel = new RequestPanel();
         resultsPane = createResponseDataPanel();
     }
 
     @Override
     @SuppressWarnings("boxing")
     public void setupTabPane() {
         // Clear all data before display a new
         this.clearData();
         StyledDocument statsDoc = stats.getStyledDocument();
         try {
             if (userObject instanceof SampleResult) {
                 sampleResult = (SampleResult) userObject;
                 // We are displaying a SampleResult
                 setupTabPaneForSampleResult();
                 requestPanel.setSamplerResult(sampleResult);                
 
                 final String samplerClass = sampleResult.getClass().getName();
                 String typeResult = samplerClass.substring(1 + samplerClass.lastIndexOf('.'));
                 
                 StringBuilder statsBuff = new StringBuilder(200);
                 statsBuff.append(JMeterUtils.getResString("view_results_thread_name")).append(sampleResult.getThreadName()).append(NL); //$NON-NLS-1$
                 String startTime = dateFormat.format(new Date(sampleResult.getStartTime()));
                 statsBuff.append(JMeterUtils.getResString("view_results_sample_start")).append(startTime).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_load_time")).append(sampleResult.getTime()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_connect_time")).append(sampleResult.getConnectTime()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_latency")).append(sampleResult.getLatency()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_size_in_bytes")).append(sampleResult.getBytes()).append(NL); //$NON-NLS-1$
+                statsBuff.append(JMeterUtils.getResString("view_results_sent_bytes")).append(sampleResult.getSentBytes()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_size_headers_in_bytes")).append(sampleResult.getHeadersSize()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_size_body_in_bytes")).append(sampleResult.getBodySize()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_sample_count")).append(sampleResult.getSampleCount()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_error_count")).append(sampleResult.getErrorCount()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_datatype")).append(sampleResult.getDataType()).append(NL); //$NON-NLS-1$
                 statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
                 statsBuff.setLength(0); // reset for reuse
 
                 String responseCode = sampleResult.getResponseCode();
 
                 int responseLevel = 0;
                 if (responseCode != null) {
                     try {
                         responseLevel = Integer.parseInt(responseCode) / 100;
                     } catch (NumberFormatException numberFormatException) {
                         // no need to change the foreground color
                     }
                 }
 
                 Style style = null;
                 switch (responseLevel) {
                 case 3:
                     style = statsDoc.getStyle(STYLE_REDIRECT);
                     break;
                 case 4:
                     style = statsDoc.getStyle(STYLE_CLIENT_ERROR);
                     break;
                 case 5:
                     style = statsDoc.getStyle(STYLE_SERVER_ERROR);
                     break;
                 default: // quieten Findbugs
                     break; // default - do nothing
                 }
 
                 statsBuff.append(JMeterUtils.getResString("view_results_response_code")).append(responseCode).append(NL); //$NON-NLS-1$
                 statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), style);
                 statsBuff.setLength(0); // reset for reuse
 
                 // response message label
                 String responseMsgStr = sampleResult.getResponseMessage();
 
                 statsBuff.append(JMeterUtils.getResString("view_results_response_message")).append(responseMsgStr).append(NL); //$NON-NLS-1$
                 statsBuff.append(NL);
                 statsBuff.append(JMeterUtils.getResString("view_results_response_headers")).append(NL); //$NON-NLS-1$
                 statsBuff.append(sampleResult.getResponseHeaders()).append(NL);
                 statsBuff.append(NL);
                 statsBuff.append(typeResult + " "+ JMeterUtils.getResString("view_results_fields")).append(NL); //$NON-NLS-1$ $NON-NLS-2$
                 statsBuff.append("ContentType: ").append(sampleResult.getContentType()).append(NL); //$NON-NLS-1$
                 statsBuff.append("DataEncoding: ").append(sampleResult.getDataEncodingNoDefault()).append(NL); //$NON-NLS-1$
                 statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
                 statsBuff = null; // Done
                 
                 // Tabbed results: fill table
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_thread_name"), sampleResult.getThreadName())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_sample_start"), startTime)); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_load_time"), sampleResult.getTime())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_latency"), sampleResult.getLatency())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_size_in_bytes"), sampleResult.getBytes())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_size_headers_in_bytes"), sampleResult.getHeadersSize())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_size_body_in_bytes"), sampleResult.getBodySize())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_sample_count"), sampleResult.getSampleCount())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_error_count"), sampleResult.getErrorCount())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_response_code"), responseCode)); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_response_message"), responseMsgStr)); //$NON-NLS-1$
                 
                 // Parsed response headers
                 LinkedHashMap<String, String> lhm = JMeterUtils.parseHeaders(sampleResult.getResponseHeaders());
                 Set<Entry<String, String>> keySet = lhm.entrySet();
                 for (Entry<String, String> entry : keySet) {
                     resHeadersModel.addRow(new RowResult(entry.getKey(), entry.getValue()));
                 }
                 
                 // Fields table
                 resFieldsModel.addRow(new RowResult("Type Result ", typeResult)); //$NON-NLS-1$
                 //not sure needs I18N?
                 resFieldsModel.addRow(new RowResult("ContentType", sampleResult.getContentType())); //$NON-NLS-1$
                 resFieldsModel.addRow(new RowResult("DataEncoding", sampleResult.getDataEncodingNoDefault())); //$NON-NLS-1$
                 
                 // Reset search
                 if (activateSearchExtension) {
                     searchTextExtension.resetTextToFind();
                 }
 
             } else if (userObject instanceof AssertionResult) {
                 assertionResult = (AssertionResult) userObject;
 
                 // We are displaying an AssertionResult
                 setupTabPaneForAssertionResult();
 
                 StringBuilder statsBuff = new StringBuilder(100);
                 statsBuff.append(JMeterUtils.getResString("view_results_assertion_error")).append(assertionResult.isError()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_assertion_failure")).append(assertionResult.isFailure()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_assertion_failure_message")).append(assertionResult.getFailureMessage()).append(NL); //$NON-NLS-1$
                 statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
             }
             stats.setCaretPosition(1);
         } catch (BadLocationException exc) {
             stats.setText(exc.getLocalizedMessage());
         }
     }
 
     private void setupTabPaneForSampleResult() {
         // restore tabbed pane parsed if needed
         if (tabbedResult.getTabCount() < 2) { 
             tabbedResult.insertTab(JMeterUtils.getResString("view_results_table_result_tab_parsed"), null, paneParsed, null, 1); //$NON-NLS-1$
             tabbedResult.setSelectedIndex(lastResultTabIndex); // select last tab 
         }
         // Set the title for the first tab
         rightSide.setTitleAt(0, JMeterUtils.getResString("view_results_tab_sampler")); //$NON-NLS-1$
         // Add the other tabs if not present
         if(rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_request")) < 0) { // $NON-NLS-1$
             rightSide.addTab(JMeterUtils.getResString("view_results_tab_request"), requestPanel.getPanel()); // $NON-NLS-1$
         }
         if(rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_response")) < 0) { // $NON-NLS-1$
             rightSide.addTab(JMeterUtils.getResString("view_results_tab_response"), resultsPane); // $NON-NLS-1$
         }
         // restore last selected tab
         if (lastSelectedTab < rightSide.getTabCount()) {
             rightSide.setSelectedIndex(lastSelectedTab);
         }
     }
     
     private void setupTabPaneForAssertionResult() {
         // Remove the other (parsed) tab if present
         if (tabbedResult.getTabCount() >= 2) {
             lastResultTabIndex = tabbedResult.getSelectedIndex();
             int parsedTabIndex = tabbedResult.indexOfTab(JMeterUtils.getResString("view_results_table_result_tab_parsed")); // $NON-NLS-1$
             if(parsedTabIndex >= 0) {
                 tabbedResult.removeTabAt(parsedTabIndex);
             }
         }
         // Set the title for the first tab
         rightSide.setTitleAt(0, JMeterUtils.getResString("view_results_tab_assertion")); //$NON-NLS-1$
         // Remove the other tabs if present
         int requestTabIndex = rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_request")); // $NON-NLS-1$
         if(requestTabIndex >= 0) {
             rightSide.removeTabAt(requestTabIndex);
         }
         int responseTabIndex = rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_response")); // $NON-NLS-1$
         if(responseTabIndex >= 0) {
             rightSide.removeTabAt(responseTabIndex);
         }
     }
 
     private Component createResponseMetadataPanel() {
         stats = new JTextPane();
         stats.setEditable(false);
         stats.setBackground(backGround);
 
         // Add styles to use for different types of status messages
         StyledDocument doc = (StyledDocument) stats.getDocument();
 
         Style style = doc.addStyle(STYLE_REDIRECT, null);
         StyleConstants.setForeground(style, REDIRECT_COLOR);
 
         style = doc.addStyle(STYLE_CLIENT_ERROR, null);
         StyleConstants.setForeground(style, CLIENT_ERROR_COLOR);
 
         style = doc.addStyle(STYLE_SERVER_ERROR, null);
         StyleConstants.setForeground(style, SERVER_ERROR_COLOR);
 
         paneRaw = GuiUtils.makeScrollPane(stats);
         paneRaw.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
 
         // Set up the 1st table Result with empty headers
         tableResult = new JTable(resultModel);
         JMeterUtils.applyHiDPI(tableResult);
         tableResult.setToolTipText(JMeterUtils.getResString("textbox_tooltip_cell")); // $NON-NLS-1$
         tableResult.addMouseListener(new TextBoxDoubleClick(tableResult));
         setFirstColumnPreferredSize(tableResult);
         RendererUtils.applyRenderers(tableResult, RENDERERS_RESULT);
 
         // Set up the 2nd table 
         tableResHeaders = new JTable(resHeadersModel);
         JMeterUtils.applyHiDPI(tableResHeaders);
         tableResHeaders.setToolTipText(JMeterUtils.getResString("textbox_tooltip_cell")); // $NON-NLS-1$
         tableResHeaders.addMouseListener(new TextBoxDoubleClick(tableResHeaders));
         setFirstColumnPreferredSize(tableResHeaders);
         tableResHeaders.getTableHeader().setDefaultRenderer(
                 new HeaderAsPropertyRenderer());
         RendererUtils.applyRenderers(tableResHeaders, RENDERERS_HEADERS);
 
         // Set up the 3rd table 
         tableResFields = new JTable(resFieldsModel);
         JMeterUtils.applyHiDPI(tableResFields);
         tableResFields.setToolTipText(JMeterUtils.getResString("textbox_tooltip_cell")); // $NON-NLS-1$
         tableResFields.addMouseListener(new TextBoxDoubleClick(tableResFields));
         setFirstColumnPreferredSize(tableResFields);
         tableResFields.getTableHeader().setDefaultRenderer(
                 new HeaderAsPropertyRenderer());
         RendererUtils.applyRenderers(tableResFields, RENDERERS_FIELDS);
 
         // Prepare the Results tabbed pane
         tabbedResult = new JTabbedPane(SwingConstants.BOTTOM);
 
         // Create the split pane
         JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                 GuiUtils.makeScrollPane(tableResHeaders),
                 GuiUtils.makeScrollPane(tableResFields));
         topSplit.setOneTouchExpandable(true);
         topSplit.setResizeWeight(0.80); // set split ratio
         topSplit.setBorder(null); // see bug jdk 4131528
 
         paneParsed = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                 GuiUtils.makeScrollPane(tableResult), topSplit);
         paneParsed.setOneTouchExpandable(true);
         paneParsed.setResizeWeight(0.40); // set split ratio
         paneParsed.setBorder(null); // see bug jdk 4131528
 
         // setup bottom tabs, first Raw, second Parsed
         tabbedResult.addTab(JMeterUtils.getResString("view_results_table_result_tab_raw"), paneRaw); //$NON-NLS-1$
         tabbedResult.addTab(JMeterUtils.getResString("view_results_table_result_tab_parsed"), paneParsed); //$NON-NLS-1$
 
         // Hint to background color on bottom tabs (grey, not blue)
         JPanel panel = new JPanel(new BorderLayout());
         panel.add(tabbedResult);
         return panel;
     }
 
     private JPanel createResponseDataPanel() {
         results = new JEditorPane();
         results.setEditable(false);
 
         resultsScrollPane = GuiUtils.makeScrollPane(results);
         imageLabel = new JLabel();
 
         JPanel panel = new JPanel(new BorderLayout());
         panel.add(resultsScrollPane, BorderLayout.CENTER);
 
         if (activateSearchExtension) {
             // Add search text extension
             searchTextExtension = new SearchTextExtension();
             searchTextExtension.init(panel);
             searchPanel = searchTextExtension.createSearchTextExtensionPane();
             searchTextExtension.setResults(results);
             searchPanel.setVisible(true);
             panel.add(searchPanel, BorderLayout.PAGE_END);
         }
 
         return panel;
     }
 
     private void showImage(Icon image) {
         imageLabel.setIcon(image);
         resultsScrollPane.setViewportView(imageLabel);
     }
 
     @Override
     public synchronized void setSamplerResult(Object sample) {
         userObject = sample;
     }
 
     @Override
     public synchronized void setRightSide(JTabbedPane side) {
         rightSide = side;
     }
 
     @Override
     public void setLastSelectedTab(int index) {
         lastSelectedTab = index;
     }
 
     @Override
     public void renderImage(SampleResult sampleResult) {
         byte[] responseBytes = sampleResult.getResponseData();
         if (responseBytes != null) {
             showImage(new ImageIcon(responseBytes)); //TODO implement other non-text types
         }
     }
 
     @Override
     public void setBackgroundColor(Color backGround){
         this.backGround = backGround;
     }
     
     private void setFirstColumnPreferredSize(JTable table) {
         TableColumn column = table.getColumnModel().getColumn(0);
         column.setMaxWidth(300);
         column.setPreferredWidth(180);
     }
     
     /**
      * For model table
      */
     public static class RowResult {
         private String key;
 
         private Object value;
 
         public RowResult(String key, Object value) {
             this.key = key;
             this.value = value;
         }
 
         /**
          * @return the key
          */
         public synchronized String getKey() {
             return key;
         }
 
         /**
          * @param key
          *            the key to set
          */
         public synchronized void setKey(String key) {
             this.key = key;
         }
 
         /**
          * @return the value
          */
         public synchronized Object getValue() {
             return value;
         }
 
         /**
          * @param value
          *            the value to set
          */
         public synchronized void setValue(Object value) {
             this.value = value;
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/TableVisualizer.java b/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
index 1bf071ead..142c81e24 100644
--- a/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
@@ -1,346 +1,349 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.FlowLayout;
 import java.text.Format;
 import java.text.SimpleDateFormat;
 
 import javax.swing.BorderFactory;
 import javax.swing.ImageIcon;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.JTextField;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.table.TableCellRenderer;
 
 import org.apache.jmeter.JMeter;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.Calculator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.gui.RightAlignRenderer;
 import org.apache.jorphan.gui.layout.VerticalLayout;
 import org.apache.jorphan.reflect.Functor;
 
 /**
  * This class implements a statistical analyser that calculates both the average
  * and the standard deviation of the sampling process. The samples are displayed
  * in a JTable, and the statistics are displayed at the bottom of the table.
  *
  * created March 10, 2002
  *
  */
 public class TableVisualizer extends AbstractVisualizer implements Clearable {
 
     private static final long serialVersionUID = 240L;
 
     private static final String iconSize = JMeterUtils.getPropDefault(JMeter.TREE_ICON_SIZE, JMeter.DEFAULT_TREE_ICON_SIZE);
 
     // Note: the resource string won't respond to locale-changes,
     // however this does not matter as it is only used when pasting to the clipboard
     private static final ImageIcon imageSuccess = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.success",  //$NON-NLS-1$
                                        "vrt/" + iconSize + "/security-high-2.png"),    //$NON-NLS-1$ $NON-NLS-2$
             JMeterUtils.getResString("table_visualizer_success")); //$NON-NLS-1$
 
     private static final ImageIcon imageFailure = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.failure",  //$NON-NLS-1$
                                        "vrt/" + iconSize + "/security-low-2.png"),    //$NON-NLS-1$ $NON-NLS-2$
             JMeterUtils.getResString("table_visualizer_warning")); //$NON-NLS-1$
 
     private static final String[] COLUMNS = new String[] {
             "table_visualizer_sample_num",  // $NON-NLS-1$
             "table_visualizer_start_time",  // $NON-NLS-1$
             "table_visualizer_thread_name", // $NON-NLS-1$
             "sampler_label",                // $NON-NLS-1$
             "table_visualizer_sample_time", // $NON-NLS-1$
             "table_visualizer_status",      // $NON-NLS-1$
             "table_visualizer_bytes",       // $NON-NLS-1$
+            "table_visualizer_sent_bytes",       // $NON-NLS-1$
             "table_visualizer_latency",     // $NON-NLS-1$
             "table_visualizer_connect"};    // $NON-NLS-1$
 
     private ObjectTableModel model = null;
 
     private JTable table = null;
 
     private JTextField dataField = null;
 
     private JTextField averageField = null;
 
     private JTextField deviationField = null;
 
     private JTextField noSamplesField = null;
 
     private JScrollPane tableScrollPanel = null;
 
     private JCheckBox autoscroll = null;
 
     private JCheckBox childSamples = null;
 
     private transient Calculator calc = new Calculator();
 
     private Format format = new SimpleDateFormat("HH:mm:ss.SSS"); //$NON-NLS-1$
 
     // Column renderers
     private static final TableCellRenderer[] RENDERERS =
         new TableCellRenderer[]{
             new RightAlignRenderer(), // Sample number (string)
             new RightAlignRenderer(), // Start Time
             null, // Thread Name
             null, // Label
             null, // Sample Time
             null, // Status
             null, // Bytes
         };
 
     /**
      * Constructor for the TableVisualizer object.
      */
     public TableVisualizer() {
         super();
         model = new ObjectTableModel(COLUMNS,
                 TableSample.class,         // The object used for each row
                 new Functor[] {
                 new Functor("getSampleNumberString"),  // $NON-NLS-1$
                 new Functor("getStartTimeFormatted",   // $NON-NLS-1$
                         new Object[]{format}),
                 new Functor("getThreadName"),          // $NON-NLS-1$
                 new Functor("getLabel"),               // $NON-NLS-1$
                 new Functor("getElapsed"),             // $NON-NLS-1$
                 new SampleSuccessFunctor("isSuccess"), // $NON-NLS-1$
                 new Functor("getBytes"),               // $NON-NLS-1$
+                new Functor("getSentBytes"),               // $NON-NLS-1$
                 new Functor("getLatency"),             // $NON-NLS-1$
                 new Functor("getConnectTime") },       // $NON-NLS-1$
-                new Functor[] { null, null, null, null, null, null, null, null, null },
+                new Functor[] { null, null, null, null, null, null, null, null, null, null },
                 new Class[] {
-                String.class, String.class, String.class, String.class, Long.class, ImageIcon.class, Long.class, Long.class, Long.class });
+                String.class, String.class, String.class, String.class, Long.class, ImageIcon.class, Long.class, Long.class, Long.class, Long.class });
         init();
     }
 
     public static boolean testFunctors(){
         TableVisualizer instance = new TableVisualizer();
         return instance.model.checkFunctors(null,instance.getClass());
     }
 
 
     @Override
     public String getLabelResource() {
         return "view_results_in_table"; // $NON-NLS-1$
     }
 
     protected synchronized void updateTextFields(SampleResult res) {
         noSamplesField.setText(Long.toString(calc.getCount()));
         if(res.getSampleCount() > 0) {
             dataField.setText(Long.toString(res.getTime()/res.getSampleCount()));
         } else {
             dataField.setText("0");
         }
         averageField.setText(Long.toString((long) calc.getMean()));
         deviationField.setText(Long.toString((long) calc.getStandardDeviation()));
     }
 
     @Override
     public void add(final SampleResult res) {
         JMeterUtils.runSafe(false, new Runnable() {
             @Override
             public void run() {
                 if (childSamples.isSelected()) {
                     SampleResult[] subResults = res.getSubResults();
                     if (subResults.length > 0) {
                         for (SampleResult sr : subResults) {
                             add(sr);
                         }
                         return;
                     }
                 }
                 synchronized (calc) {
                     calc.addSample(res);
                     int count = calc.getCount();
                     TableSample newS = new TableSample(
                             count, 
                             res.getSampleCount(), 
                             res.getStartTime(), 
                             res.getThreadName(), 
                             res.getSampleLabel(),
                             res.getTime(),
                             res.isSuccessful(),
                             res.getBytes(),
+                            res.getSentBytes(),
                             res.getLatency(),
                             res.getConnectTime()
                             );
                     model.addRow(newS);
                 }
                 updateTextFields(res);
                 if (autoscroll.isSelected()) {
                     table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
                 }
             }
         });
     }
 
     @Override
     public synchronized void clearData() {
         model.clearData();
         calc.clear();
         noSamplesField.setText("0"); // $NON-NLS-1$
         dataField.setText("0"); // $NON-NLS-1$
         averageField.setText("0"); // $NON-NLS-1$
         deviationField.setText("0"); // $NON-NLS-1$
         repaint();
     }
 
     @Override
     public String toString() {
         return "Show the samples in a table";
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         mainPanel.setBorder(margin);
         mainPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
 
         // NAME
         mainPanel.add(makeTitlePanel());
 
         // Set up the table itself
         table = new JTable(model);
         JMeterUtils.applyHiDPI(table);
         table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         // table.getTableHeader().setReorderingAllowed(false);
         RendererUtils.applyRenderers(table, RENDERERS);
 
         tableScrollPanel = new JScrollPane(table);
         tableScrollPanel.setViewportBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
 
         autoscroll = new JCheckBox(JMeterUtils.getResString("view_results_autoscroll")); //$NON-NLS-1$
 
         childSamples = new JCheckBox(JMeterUtils.getResString("view_results_childsamples")); //$NON-NLS-1$
 
         // Set up footer of table which displays numerics of the graphs
         JPanel dataPanel = new JPanel();
         JLabel dataLabel = new JLabel(JMeterUtils.getResString("graph_results_latest_sample")); // $NON-NLS-1$
         dataLabel.setForeground(Color.black);
         dataField = new JTextField(5);
         dataField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         dataField.setEditable(false);
         dataField.setForeground(Color.black);
         dataField.setBackground(getBackground());
         dataPanel.add(dataLabel);
         dataPanel.add(dataField);
 
         JPanel averagePanel = new JPanel();
         JLabel averageLabel = new JLabel(JMeterUtils.getResString("graph_results_average")); // $NON-NLS-1$
         averageLabel.setForeground(Color.blue);
         averageField = new JTextField(5);
         averageField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         averageField.setEditable(false);
         averageField.setForeground(Color.blue);
         averageField.setBackground(getBackground());
         averagePanel.add(averageLabel);
         averagePanel.add(averageField);
 
         JPanel deviationPanel = new JPanel();
         JLabel deviationLabel = new JLabel(JMeterUtils.getResString("graph_results_deviation")); // $NON-NLS-1$
         deviationLabel.setForeground(Color.red);
         deviationField = new JTextField(5);
         deviationField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         deviationField.setEditable(false);
         deviationField.setForeground(Color.red);
         deviationField.setBackground(getBackground());
         deviationPanel.add(deviationLabel);
         deviationPanel.add(deviationField);
 
         JPanel noSamplesPanel = new JPanel();
         JLabel noSamplesLabel = new JLabel(JMeterUtils.getResString("graph_results_no_samples")); // $NON-NLS-1$
 
         noSamplesField = new JTextField(8);
         noSamplesField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         noSamplesField.setEditable(false);
         noSamplesField.setForeground(Color.black);
         noSamplesField.setBackground(getBackground());
         noSamplesPanel.add(noSamplesLabel);
         noSamplesPanel.add(noSamplesField);
 
         JPanel tableInfoPanel = new JPanel();
         tableInfoPanel.setLayout(new FlowLayout());
         tableInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
 
         tableInfoPanel.add(noSamplesPanel);
         tableInfoPanel.add(dataPanel);
         tableInfoPanel.add(averagePanel);
         tableInfoPanel.add(deviationPanel);
 
         JPanel tableControlsPanel = new JPanel(new BorderLayout());
         tableControlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         JPanel jp = new HorizontalPanel();
         jp.add(autoscroll);
         jp.add(childSamples);
         tableControlsPanel.add(jp, BorderLayout.WEST);
         tableControlsPanel.add(tableInfoPanel, BorderLayout.CENTER);
 
         // Set up the table with footer
         JPanel tablePanel = new JPanel();
 
         tablePanel.setLayout(new BorderLayout());
         tablePanel.add(tableScrollPanel, BorderLayout.CENTER);
         tablePanel.add(tableControlsPanel, BorderLayout.SOUTH);
 
         // Add the main panel and the graph
         this.add(mainPanel, BorderLayout.NORTH);
         this.add(tablePanel, BorderLayout.CENTER);
     }
 
     public static class SampleSuccessFunctor extends Functor {
         public SampleSuccessFunctor(String methodName) {
             super(methodName);
         }
 
         @Override
         public Object invoke(Object pInvokee) {
             Boolean success = (Boolean) super.invoke(pInvokee);
 
             if (success != null) {
                 if (success.booleanValue()) {
                     return imageSuccess;
                 } else {
                     return imageFailure;
                 }
             } else {
                 return null;
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index 7a8621189..d81854033 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,365 +1,366 @@
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
     /**
      * Used to identify Transaction Controller Parent Sampler
      */
     static final String NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX = "Number of samples in transaction : ";
 
     private static final long serialVersionUID = 233L;
     
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     private static final String GENERATE_PARENT_SAMPLE = "TransactionController.parent";// $NON-NLS-1$
 
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
 
     /**
      * @param generateParent flag whether a parent sample should be generated.
      */
     public void setGenerateParentSample(boolean generateParent) {
         setProperty(new BooleanProperty(GENERATE_PARENT_SAMPLE, generateParent));
     }
 
     /**
      * @return {@code true} if a parent sample will be generated
      */
     public boolean isGenerateParentSample() {
         return getPropertyAsBoolean(GENERATE_PARENT_SAMPLE);
     }
 
     /**
      * @deprecated use {@link TransactionController#isGenerateParentSample()}
      *             instead
      * @return {@code true} if a parent sample will be generated
      */
     @Deprecated
     public boolean isParent() {
         return isGenerateParentSample();
     }
 
     /**
      * @deprecated use
      *             {@link TransactionController#setGenerateParentSample(boolean)}
      *             instead
      * @param _parent
      *            flag whether a parent sample should be generated
      */
     @Deprecated
     public void setParent(boolean _parent) {
         setGenerateParentSample(_parent);
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#next()
      */
     @Override
     public Sampler next(){
         if (isGenerateParentSample()){
             return nextWithTransactionSampler();
         }
         return nextWithoutTransactionSampler();
     }
 
 ///////////////// Transaction Controller - parent ////////////////
 
     private Sampler nextWithTransactionSampler() {
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
         if (!isGenerateParentSample()) {
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
 
     private Sampler nextWithoutTransactionSampler() {
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
                 res.setResponseMessage(TransactionController.NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX + calls + ", number of failing samples : " + noFailingSamples);
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
     
     /**
      * @param res {@link SampleResult}
      * @return true if res is the ParentSampler transactions
      */
     public static boolean isFromTransactionController(SampleResult res) {
         return res.getResponseMessage() != null && 
                 res.getResponseMessage().startsWith(
                         TransactionController.NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX);
     }
 
     /**
      * @see org.apache.jmeter.control.GenericController#triggerEndOfLoop()
      */
     @Override
     public void triggerEndOfLoop() {
         if(!isGenerateParentSample()) {
             if (res != null) {
                 res.setIdleTime(pauseTime + res.getIdleTime());
                 res.sampleEnd();
                 res.setSuccessful(TRUE.equals(JMeterContextService.getContext().getVariables().get(JMeterThread.LAST_SAMPLE_OK)));
                 res.setResponseMessage(TransactionController.NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX + calls + ", number of failing samples : " + noFailingSamples);
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
         if (!isGenerateParentSample()) {
             // Check if we are still sampling our children
             if(res != null && !se.isTransactionSampleEvent()) {
                 SampleResult sampleResult = se.getResult();
                 res.setThreadName(sampleResult.getThreadName());
                 res.setBytes(res.getBytes() + sampleResult.getBytes());
+                res.setSentBytes(res.getSentBytes() + sampleResult.getSentBytes());
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
diff --git a/src/core/org/apache/jmeter/report/core/Sample.java b/src/core/org/apache/jmeter/report/core/Sample.java
index dfa1890b8..96afa0f22 100644
--- a/src/core/org/apache/jmeter/report/core/Sample.java
+++ b/src/core/org/apache/jmeter/report/core/Sample.java
@@ -1,325 +1,324 @@
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
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Represents a sample read from a CSV source.
  * <p>
  * Getters with a string parameter are implemented for convenience but should be
  * avoided as they are inefficient
  * </p>
  * 
  * @since 3.0
  */
 public class Sample {
 
     private static final String ERROR_ON_SAMPLE = "Error in sample at line:";
 
     private static final String CONTROLLER_PATTERN = "Number of samples in transaction";
     
     private static final String EMPTY_CONTROLLER_PATTERN = "Number of samples in transaction : 0";
 
     private final boolean storesStartTimeStamp;
     private final SampleMetadata metadata;
     private final String[] data;
     private final long row;
 
     /**
      * Build a sample from a string array
      * 
      * @param row
      *            the row number in the CSV source from which this sample is
      *            built
      * @param metadata
      *            The sample metadata (contains column names)
      * @param data
      *            The sample data as a string array
      */
     public Sample(long row, SampleMetadata metadata, String... data) {
         this.row = row;
         this.metadata = metadata;
         this.data = data;
         this.storesStartTimeStamp = JMeterUtils.getPropDefault("sampleresult.timestamp.start", false);
     }
 
     /**
      * @return the row number from the CSV source from which this sample has
      *         been built.
      */
     public long getSampleRow() {
         return row;
     }
 
     /**
      * Gets the data stored in the column with the specified rank.
      * 
      * @param index
      *            the rank of the column
      * @return the data of the column
      */
     public String getData(int index) {
         return data[index];
     }
 
     /**
      * Gets the data stored in the column with the specified name.
      * 
      * @param name
      *            the name of the column
      * @return the data of the column
      */
     public String getData(String name) {
         return data[metadata.ensureIndexOf(name)];
     }
 
     /**
      * Gets the data of the column matching the specified rank and converts it
      * to an alternative type.
      * 
      * @param clazz
      *            the target class of the data
      * @param index
      *            the rank of the column
      * @param fieldName Field name
      * @return the converted value of the data
      */
     public <TData> TData getData(Class<TData> clazz, int index, String fieldName) {
         try {
             return Converters.convert(clazz, data[index]);
         } catch (ConvertException ex) {
             throw new SampleException(ERROR_ON_SAMPLE + (row+1) + " converting field:"+fieldName+" at column:"+index+" to:"+clazz.getName()+", fieldValue:'"+data[index]+"'", ex);
         }
     }
 
     /**
      * Gets the data of the column matching the specified name and converts it
      * to an alternative type.
      * 
      * @param clazz
      *            the target class of the data
      * @param name
      *            the name of the column
      * @return the converted value of the data
      */
     public <TData> TData getData(Class<TData> clazz, String name) {
         return getData(clazz, metadata.ensureIndexOf(name), name);
     }
 
     /*
      * (non-Javadoc)
      * 
      * @see java.lang.Object#toString()
      */
     @Override
     public String toString() {
         return StringUtils.join(data, metadata.getSeparator());
     }
 
     /**
      * Gets the time stamp stored in the sample.
      *
      * @return the time stamp
      */
     public long getTimestamp() {
         return getData(long.class, CSVSaveService.TIME_STAMP).longValue();
     }
 
     /**
      * Gets the elapsed time stored in the sample.
      *
      * @return the elapsed time stored in the sample
      */
     public long getElapsedTime() {
         return getData(long.class, CSVSaveService.CSV_ELAPSED).longValue();
     }
 
     /**
      * <p>
      * Gets the start time of the sample.
      * </p>
      * <p>
      * Start time depends on sampleresult.timestamp.start property :
      * </p>
      * <ul>
      * <li>If the property is true, this method returns the time stamp stored in
      * the sample.</li>
      * <li>If the property is false, this method returns the time stamp stored
      * in the sample minus the elapsed time.</li>
      * </ul>
      *
      * @return the start time
      */
     public long getStartTime() {
         return storesStartTimeStamp ? getTimestamp() : getTimestamp() - getElapsedTime();
     }
 
     /**
      * <p>
      * Gets the end time of the sample.
      * </p>
      * <p>
      * End time depends on jmeter.timestamp.start property :
      * </p>
      * <ul>
      * <li>If the property is true, this method returns the time stamp recorded
      * in the sample plus the elapsed time.</li>
      * <li>If the property is false, this method returns the time stamp
      * recorded.</li>
      * </ul>
      * 
      * @return the end time
      */
     public long getEndTime() {
         return storesStartTimeStamp ? getTimestamp() + getElapsedTime() : getTimestamp();
     }
 
     /**
      * Gets the response code stored in the sample.
      *
      * @return the response code stored in the sample
      */
     public String getResponseCode() {
         return getData(CSVSaveService.RESPONSE_CODE);
     }
 
     /**
      * Gets the failure message stored in the sample.
      *
      * @return the failure message stored in the sample
      */
     public String getFailureMessage() {
         return getData(CSVSaveService.FAILURE_MESSAGE);
     }
 
     /**
      * Gets the name stored in the sample.
      *
      * @return the name stored in the sample
      */
     public String getName() {
         return getData(CSVSaveService.LABEL);
     }
 
     /**
      * Gets the response message stored in the sample.
      *
      * @return the response message stored in the sample
      */
     public String getResponseMessage() {
         return getData(CSVSaveService.RESPONSE_MESSAGE);
     }
 
     /**
      * Gets the latency stored in the sample.
      *
      * @return the latency stored in the sample
      */
     public long getLatency() {
         return getData(long.class, CSVSaveService.CSV_LATENCY).longValue();
     }
     
     /**
      * Gets the connect time stored in the sample.
      *
      * @return the connect time stored in the sample
      */
     public long getConnectTime() {
         return getData(long.class, CSVSaveService.CSV_CONNECT_TIME).longValue();
     }
 
     /**
      * Gets the success status stored in the sample.
      *
      * @return the success status stored in the sample
      */
     public boolean getSuccess() {
         return getData(boolean.class, CSVSaveService.SUCCESSFUL).booleanValue();
     }
 
     /**
      * Gets the number of received bytes stored in the sample.
      *
      * @return the number of received bytes stored in the sample
      */
     public int getReceivedBytes() {
         return getData(int.class, CSVSaveService.CSV_BYTES).intValue();
     }
 
     /**
      * Gets the number of sent bytes stored in the sample.
      *
      * @return the number of sent bytes stored in the sample
      */
-    public int getSentBytes() {
-        // TODO To implement when metric is available
-        return 0;
+    public long getSentBytes() {
+        return getData(long.class, CSVSaveService.CSV_SENT_BYTES).longValue();
     }
 
     /**
      * Gets the number of threads in the group of this sample.
      *
      * @return the number of threads in the group of this sample
      */
     public int getGroupThreads() {
         return getData(int.class, CSVSaveService.CSV_THREAD_COUNT1).intValue();
     }
 
     /**
      * Gets the overall number of threads.
      *
      * @return the overall number of threads
      */
     public int getAllThreads() {
         return getData(int.class, CSVSaveService.CSV_THREAD_COUNT2).intValue();
     }
 
     /**
      * Gets the thread name stored in the sample.
      *
      * @return the thread name stored in the sample
      */
     public String getThreadName() {
         return getData(CSVSaveService.THREAD_NAME);
     }
 
     /**
      * Checks if this sample is a controller.
      *
      * @return {@code true}, if this sample is a controller; otherwise
      *         {@code false}
      */
     public boolean isController() {
         String message = getResponseMessage();
         return message != null && message.startsWith(CONTROLLER_PATTERN);
     }
     
     /**
      * Checks if this sample is an empty controller.
      *
      * @return {@code true}, if this sample is a controller; otherwise
      *         {@code false}
      */
     public boolean isEmptyController() {
         String message = getResponseMessage();
         return message != null && message.startsWith(EMPTY_CONTROLLER_PATTERN);
     }
 }
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index 5c8d3e85b..702f70e8d 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,1364 +1,1367 @@
 #   Licensed to the Apache Software Foundation (ASF) under one or more
 #   contributor license agreements.  See the NOTICE file distributed with
 #   this work for additional information regarding copyright ownership.
 #   The ASF licenses this file to You under the Apache License, Version 2.0
 #   (the "License"); you may not use this file except in compliance with
 #   the License.  You may obtain a copy of the License at
 # 
 #       http://www.apache.org/licenses/LICENSE-2.0
 # 
 #   Unless required by applicable law or agreed to in writing, software
 #   distributed under the License is distributed on an "AS IS" BASIS,
 #   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #   See the License for the specific language governing permissions and
 #   limitations under the License.
 
 # Warning: JMeterUtils.getResString() replaces space with '_'
 # and converts keys to lowercase before lookup
 # => All keys in this file must also be lower case or they won't match
 #
 
 # Please add new entries in alphabetical order
 
 about=About Apache JMeter
 active_threads_tooltip=Running threads
 add=Add
 add_as_child=Add as Child
 add_from_clipboard=Add from Clipboard
 add_from_suggested_excludes=Add suggested Excludes
 add_parameter=Add Variable
 add_pattern=Add Pattern\:
 add_test=Add Test
 add_user=Add User
 add_value=Add Value
 addtest=Add test
 aggregate_graph=Statistical Graphs
 aggregate_graph_choose_color=Choose color
 aggregate_graph_choose_foreground_color=Foreground color
 aggregate_graph_color_bar=Color\:
 aggregate_graph_column=Column\:
 aggregate_graph_column_selection=Column label selection\:
 aggregate_graph_column_settings=Column settings
 aggregate_graph_columns_to_display=Columns to display\:
 aggregate_graph_dimension=Graph size
 aggregate_graph_display=Display Graph
 aggregate_graph_draw_outlines=Draw outlines bar?
 aggregate_graph_dynamic_size=Dynamic graph size
 aggregate_graph_font=Font\:
 aggregate_graph_height=Height\:
 aggregate_graph_increment_scale=Increment scale\:
 aggregate_graph_legend=Legend
 aggregate_graph_legend.placement.bottom=Bottom
 aggregate_graph_legend.placement.left=Left
 aggregate_graph_legend.placement.right=Right
 aggregate_graph_legend.placement.top=Top
 aggregate_graph_legend_placement=Placement\:
 aggregate_graph_max_length_xaxis_label=Max length of x-axis label\:
 aggregate_graph_ms=Milliseconds
 aggregate_graph_no_values_to_graph=No values to graph
 aggregate_graph_number_grouping=Show number grouping?
 aggregate_graph_response_time=Response Time
 aggregate_graph_save=Save Graph
 aggregate_graph_save_table=Save Table Data
 aggregate_graph_save_table_header=Save Table Header
 aggregate_graph_size=Size\:
 aggregate_graph_style=Style\:
 aggregate_graph_sync_with_name=Synchronize with name
 aggregate_graph_tab_graph=Graph
 aggregate_graph_tab_settings=Settings
 aggregate_graph_title=Aggregate Graph
 aggregate_graph_title_group=Title
 aggregate_graph_use_group_name=Include group name in label?
 aggregate_graph_user_title=Graph title\:
 aggregate_graph_value_font=Value font\:
 aggregate_graph_value_labels_vertical=Value labels vertical?
 aggregate_graph_width=Width\:
 aggregate_graph_xaxis_group=X Axis
 aggregate_graph_yaxis_group=Y Axis (milli-seconds)
 aggregate_graph_yaxis_max_value=Scale maximum value\:
 aggregate_report=Aggregate Report
 aggregate_report_xx_pct1_line={0}% Line
 aggregate_report_xx_pct2_line={0}% Line
 aggregate_report_xx_pct3_line={0}% Line
 aggregate_report_90=90%
 aggregate_report_bandwidth=KB/sec
 aggregate_report_count=# Samples
 aggregate_report_error=Error
 aggregate_report_error%=Error %
 aggregate_report_max=Max
 aggregate_report_median=Median
 aggregate_report_min=Min
 aggregate_report_rate=Throughput
 aggregate_report_stddev=Std. Dev.
 aggregate_report_total_label=TOTAL
 ajp_sampler_title=AJP/1.3 Sampler
 als_message=Note\: The Access Log Parser is generic in design and allows you to plugin
 als_message2=your own parser. To do so, implement the LogParser, add the jar to the
 als_message3=/lib directory and enter the class in the sampler.
 analyze=Analyze Data File...
 anchor_modifier_title=HTML Link Parser
 appearance=Look and Feel
 argument_must_not_be_negative=The Argument must not be negative\!
 arguments_panel_title=Command parameters
 assertion_assume_success=Ignore Status
 assertion_body_resp=Response Body
 assertion_code_resp=Response Code
 assertion_contains=Contains
 assertion_equals=Equals
 assertion_headers=Response Headers
 assertion_matches=Matches
 assertion_message_resp=Response Message
 assertion_network_size=Full Response
 assertion_not=Not
 assertion_pattern_match_rules=Pattern Matching Rules
 assertion_patterns_to_test=Patterns to Test
 assertion_regex_empty_default_value=Use empty default value
 assertion_resp_field=Response Field to Test
 assertion_resp_size_field=Response Size Field to Test
 assertion_substring=Substring
 assertion_text_document=Document (text)
 assertion_text_resp=Text Response
 assertion_textarea_label=Assertions\:
 assertion_title=Response Assertion
 assertion_url_samp=URL Sampled
 assertion_visualizer_title=Assertion Results
 attribute=Attribute
 attribute_field=Attribute\:
 attrs=Attributes
 auth_base_url=Base URL
 auth_manager_clear_per_iter=Clear auth on each iteration?
 auth_manager_options=Options
 auth_manager_title=HTTP Authorization Manager
 auths_stored=Authorizations Stored in the Authorization Manager
 average=Average
 average_bytes=Avg. Bytes
 backend_listener=Backend Listener
 backend_listener_classname=Backend Listener implementation
 backend_listener_paramtable=Parameters
 backend_listener_queue_size=Async Queue size
 bind=Thread Bind
 bouncy_castle_unavailable_message=The jars for bouncy castle are unavailable, please add them to your classpath.
 browse=Browse...
 bsf_sampler_title=BSF Sampler
 bsf_script=Script to run (variables: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=Script file to run
 bsf_script_language=Scripting language\:
 bsf_script_parameters=Parameters to pass to script/file\:
 bsh_assertion_script=Script (see below for variables that are defined)
 bsh_assertion_script_variables=The following variables are defined for the script:\nRead/Write: Failure, FailureMessage, SampleResult, vars, props, log.\nReadOnly: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=BeanShell Assertion
 bsh_function_expression=Expression to evaluate
 bsh_sampler_title=BeanShell Sampler
 bsh_script=Script (see below for variables that are defined)
 bsh_script_file=Script file
 bsh_script_parameters=Parameters (-> String Parameters and String []bsh.args)
 bsh_script_reset_interpreter=Reset bsh.Interpreter before each call
 bsh_script_variables=The following variables are defined for the script\:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=I'm busy testing, please stop the test before changing settings
 cache_manager_size=Max Number of elements in cache
 cache_manager_title=HTTP Cache Manager
 cache_session_id=Cache Session Id?
 cancel=Cancel
 cancel_exit_to_save=There are test items that have not been saved.  Do you wish to save before exiting?
 cancel_new_from_template=There are test items that have not been saved.  Do you wish to save before creating a test plan from selected template?
 cancel_new_to_save=There are test items that have not been saved.  Do you wish to save before clearing the test plan?
 cancel_revert_project=There are test items that have not been saved.  Do you wish to revert to the previously saved test plan?
 change_parent=Change Controller
 char_value=Unicode character number (decimal or 0xhex)
 check_return_code_title=Check Return Code
 choose_function=Choose a function
 choose_language=Choose Language
 clear=Clear
 clear_all=Clear All
 clear_cache_each_iteration=Clear cache each iteration
 clear_cache_per_iter=Clear cache each iteration?
 clear_cookies_per_iter=Clear cookies each iteration?
 clipboard_node_read_error=An error occurred while copying node
 close=Close
 closeconnection=Close connection
 collapse_tooltip=Click to open / collapse
 column_delete_disallowed=Deleting this column is not permitted
 column_number=Column number of CSV file | next | *alias
 command_config_box_title=Command to Execute
 command_config_std_streams_title=Standard streams (files)
 command_field_title=Command:
 compare=Compare
 comparefilt=Compare filter
 comparison_differ_content=Responses differ in content
 comparison_differ_time=Responses differ in response time by more than 
 comparison_invalid_node=Invalid Node 
 comparison_regex_string=Regex String
 comparison_regex_substitution=Substitution
 comparison_response_time=Response Time: 
 comparison_unit=\ ms
 comparison_visualizer_title=Comparison Assertion Visualizer
 config_element=Config Element
 config_save_settings=Configure
 confirm=Confirm
 constant_throughput_timer_memo=Add a delay between sampling to attain constant throughput
 constant_timer_delay=Thread Delay (in milliseconds)\:
 constant_timer_memo=Add a constant delay between sampling
 constant_timer_title=Constant Timer
 content_encoding=Content encoding\:
 controller=Controller
 cookie_implementation_choose=Implementation:
 cookie_manager_policy=Cookie Policy:
 cookie_manager_title=HTTP Cookie Manager
 cookie_options=Options
 cookies_stored=User-Defined Cookies
 copy=Copy
 counter_config_title=Counter
 counter_per_user=Track counter independently for each user
 counter_reset_per_tg_iteration=Reset counter on each Thread Group Iteration
 countlim=Size limit
 critical_section_controller_label=Lock name
 critical_section_controller_title=Critical Section Controller
 cssjquery_attribute=Attribute\:
 cssjquery_empty_default_value=Use empty default value
 cssjquery_tester_error=An error occured evaluating expression:{0}, error:{1}
 cssjquery_impl=CSS/JQuery implementation\:
 cssjquery_render_no_text=Data response result isn't text.
 cssjquery_tester_button_test=Test
 cssjquery_tester_field=Selector\:
 cssjquery_tester_title=CSS/JQuery Tester
 csvread_file_file_name=CSV file to get values from | *alias
 cut=Cut
 cut_paste_function=Copy and paste function string
 database_conn_pool_max_usage=Max Usage For Each Connection\:
 database_conn_pool_props=Database Connection Pool
 database_conn_pool_size=Number of Connections in Pool\:
 database_conn_pool_title=JDBC Database Connection Pool Defaults
 database_driver_class=Driver Class\:
 database_login_title=JDBC Database Login Defaults
 database_sql_query_string=SQL Query String\:
 database_sql_query_title=JDBC SQL Query Defaults
 database_testing_title=JDBC Request
 database_url=JDBC URL\:
 database_url_jdbc_props=Database URL and JDBC Driver
 ddn=DN
 de=German
 debug_off=Disable debug
 debug_on=Enable debug
 default_parameters=Default Parameters
 default_value_field=Default Value\:
 delay=Startup delay (seconds)
 delayed_start=Delay Thread creation until needed
 delete=Delete
 delete_parameter=Delete Variable
 delete_test=Delete Test
 delete_user=Delete User
 deltest=Deletion test
 deref=Dereference aliases
 description=Description
 detail=Detail
 directory_field_title=Working directory:
 disable=Disable
 distribution_graph_title=Distribution Graph (DEPRECATED)
 distribution_note1=The graph will update every 10 samples
 dn=DN
 dns_cache_manager_title=DNS Cache Manager
 dns_hostname_or_ip=Hostname or IP address
 dns_servers=DNS Servers
 domain=Domain
 done=Done
 down=Down
 duplicate=Duplicate
 duration=Duration (seconds)
 duration_assertion_duration_test=Duration to Assert
 duration_assertion_failure=The operation lasted too long\: It took {0} milliseconds, but should not have lasted longer than {1} milliseconds.
 duration_assertion_input_error=Please enter a valid positive integer.
 duration_assertion_label=Duration in milliseconds\:
 duration_assertion_title=Duration Assertion
 duration_tooltip=Elapsed time of current running Test
 edit=Edit
 email_results_title=Email Results
 en=English
 enable=Enable
 encode=URL Encode
 encode?=Encode?
 encoded_value=URL Encoded Value
 endtime=End Time  
 entry_dn=Entry DN
 entrydn=Entry DN
 environment_panel_title=Environment Variables
 eolbyte=End of line(EOL) byte value: 
 error_indicator_tooltip=Show the number of errors in log, click to open Log Viewer panel
 error_loading_help=Error loading help page
 error_occurred=Error Occurred
 error_title=Error
 es=Spanish
 escape_html_string=String to escape
 eval_name_param=Text containing variable and function references
 evalvar_name_param=Name of variable
 example_data=Sample Data
 example_title=Example Sampler
 exit=Exit
 find_target_element=Find target element
 expected_return_code_title=Expected Return Code: 
 expiration=Expiration
 expression_field=CSS/JQuery expression\:
 field_name=Field name
 file=File
 file_already_in_use=That file is already in use
 file_visualizer_append=Append to Existing Data File
 file_visualizer_auto_flush=Automatically Flush After Each Data Sample
 file_visualizer_browse=Browse...
 file_visualizer_close=Close
 file_visualizer_file_options=File Options
 file_visualizer_filename=Filename
 file_visualizer_flush=Flush
 file_visualizer_missing_filename=No output filename specified.
 file_visualizer_open=Open
 file_visualizer_output_file=Write results to file / Read from file
 file_visualizer_submit_data=Include Submitted Data
 file_visualizer_title=File Reporter
 file_visualizer_verbose=Verbose Output
 filename=File Name
 follow_redirects=Follow Redirects
 follow_redirects_auto=Redirect Automatically
 font.sansserif=Sans Serif
 font.serif=Serif
 fontstyle.bold=Bold
 fontstyle.italic=Italic
 fontstyle.normal=Normal
 foreach_controller_title=ForEach Controller
 foreach_end_index=End index for loop (inclusive)
 foreach_input=Input variable prefix
 foreach_output=Output variable name
 foreach_start_index=Start index for loop (exclusive)
 foreach_use_separator=Add "_" before number ?
 format=Number format
 fr=French
 ftp_binary_mode=Use Binary mode ?
 ftp_get=get(RETR)
 ftp_local_file=Local File:
 ftp_local_file_contents=Local File Contents:
 ftp_put=put(STOR)
 ftp_remote_file=Remote File:
 ftp_sample_title=FTP Request Defaults
 ftp_save_response_data=Save File in Response ?
 ftp_testing_title=FTP Request
 function_dialog_menu_item=Function Helper Dialog
 function_helper_title=Function Helper
 function_name_param=Name of variable in which to store the result (required)
 function_name_paropt=Name of variable in which to store the result (optional)
 function_params=Function Parameters
 functional_mode=Functional Test Mode (i.e. save Response Data and Sampler Data)
 functional_mode_explanation=Selecting Functional Test Mode may adversely affect performance.
 gaussian_timer_delay=Constant Delay Offset (in milliseconds)\:
 gaussian_timer_memo=Adds a random delay with a gaussian distribution
 gaussian_timer_range=Deviation (in milliseconds)\:
 gaussian_timer_title=Gaussian Random Timer
 generate=Generate
 generator=Name of Generator class
 generator_cnf_msg=Could not find the generator class. Please make sure you place your jar file in the /lib directory.
 generator_illegal_msg=Could not access the generator class due to IllegalAccessException.
 generator_instantiate_msg=Could not create an instance of the generator parser. Please make sure the generator implements Generator interface.
 graph_apply_filter=Apply filter
 graph_choose_graphs=Graphs to Display
 graph_full_results_title=Graph Full Results
 graph_pointshape_circle=Circle
 graph_pointshape_diamond=Diamond
 graph_pointshape_none=None
 graph_pointshape_square=Square
 graph_pointshape_triangle=Triangle
 graph_resp_time_interval_label=Interval (ms):
 graph_resp_time_interval_reload=Apply interval
 graph_resp_time_not_enough_data=Unable to graph, not enough data
 graph_resp_time_series_selection=Sampler label selection:
 graph_resp_time_settings_line=Line settings
 graph_resp_time_settings_pane=Graph settings
 graph_resp_time_shape_label=Shape point:
 graph_resp_time_stroke_width=Stroke width:
 graph_resp_time_title=Response Time Graph
 graph_resp_time_title_label=Graph title:
 graph_resp_time_xaxis_time_format=Time format (SimpleDateFormat):
 graph_results_average=Average
 graph_results_data=Data
 graph_results_deviation=Deviation
 graph_results_latest_sample=Latest Sample
 graph_results_median=Median
 graph_results_ms=ms
 graph_results_no_samples=No of Samples
 graph_results_throughput=Throughput
 graph_results_title=Graph Results
 groovy_function_expression=Expression to evaluate
 grouping_add_separators=Add separators between groups
 grouping_in_controllers=Put each group in a new controller
 grouping_in_transaction_controllers=Put each group in a new transaction controller
 grouping_mode=Grouping\:
 grouping_no_groups=Do not group samplers
 grouping_store_first_only=Store 1st sampler of each group only
 header_manager_title=HTTP Header Manager
 headers_stored=Headers Stored in the Header Manager
 heap_dump=Create a heap dump
 help=Help
 help_node=What's this node?
 html_assertion_file=Write JTidy report to file
 html_assertion_label=HTML Assertion
 html_assertion_title=HTML Assertion
 html_extractor_title=CSS/JQuery Extractor
 html_extractor_type=CSS/JQuery Extractor Implementation
 http_implementation=Implementation:
 http_response_code=HTTP response code
 http_url_rewriting_modifier_title=HTTP URL Re-writing Modifier
 http_user_parameter_modifier=HTTP User Parameter Modifier
 httpmirror_max_pool_size=Max number of Threads:
 httpmirror_max_queue_size=Max queue size:
 httpmirror_settings=Settings
 httpmirror_title=HTTP Mirror Server
 id_prefix=ID Prefix
 id_suffix=ID Suffix
 if_controller_evaluate_all=Evaluate for all children?
 if_controller_expression=Interpret Condition as Variable Expression?
 if_controller_label=Condition (default Javascript)
 if_controller_title=If Controller
 ignore_subcontrollers=Ignore sub-controller blocks
 include_controller=Include Controller
 include_equals=Include Equals?
 include_path=Include Test Plan
 increment=Increment
 infinite=Forever
 initial_context_factory=Initial Context Factory
 insert_after=Insert After
 insert_before=Insert Before
 insert_parent=Insert Parent
 interleave_control_title=Interleave Controller
 interleave_accross_threads=Interleave accross threads
 intsum_param_1=First int to add.
 intsum_param_2=Second int to add - further ints can be summed by adding further arguments.
 invalid_data=Invalid data
 invalid_mail=Error occurred sending the e-mail
 invalid_mail_address=One or more invalid e-mail addresses detected
 invalid_mail_server=Problem contacting the e-mail server (see JMeter log file)
 invalid_variables=Invalid variables
 iteration_counter_arg_1=TRUE, for each user to have own counter, FALSE for a global counter
 iterator_num=Loop Count\:
 ja=Japanese
 jar_file=Jar Files
 java_request=Java Request
 java_request_defaults=Java Request Defaults
 javascript_expression=JavaScript expression to evaluate
 jexl_expression=JEXL expression to evaluate
 jms_auth_required=Required
 jms_bytes_message=Bytes Message
 jms_client_caption=Receiver client uses MessageConsumer.receive() to listen for message.
 jms_client_caption2=MessageListener uses onMessage(Message) interface to listen for new messages.
 jms_client_id=Client ID
 jms_client_type=Client
 jms_communication_style=Communication style
 jms_concrete_connection_factory=Concrete Connection Factory
 jms_config=Message source
 jms_config_title=JMS Configuration
 jms_connection_factory=Connection Factory
 jms_correlation_title=Use alternate fields for message correlation
 jms_dest_setup=Setup
 jms_dest_setup_dynamic=Each sample
 jms_dest_setup_static=At startup
 jms_durable_subscription_id=Durable Subscription ID
 jms_expiration=Expiration (ms)
 jms_file=File
 jms_initial_context_factory=Initial Context Factory
 jms_itertions=Number of samples to aggregate
 jms_jndi_defaults_title=JNDI Default Configuration
 jms_jndi_props=JNDI Properties
 jms_map_message=Map Message
 jms_message_title=Message properties
 jms_message_type=Message Type
 jms_msg_content=Content
 jms_object_message=Object Message
 jms_point_to_point=JMS Point-to-Point
 jms_priority=Priority (0-9)
 jms_properties=JMS Properties
 jms_properties_name=Name
 jms_properties_title=JMS Properties
 jms_properties_type=Class of value
 jms_properties_value=Value
 jms_props=JMS Properties
 jms_provider_url=Provider URL
 jms_publisher=JMS Publisher
 jms_pwd=Password
 jms_queue=Queue
 jms_queue_connection_factory=QueueConnection Factory
 jms_queueing=JMS Resources
 jms_random_file=Path of folder containing random files suffixed with .dat for bytes messages, .txt or .obj for text and Object messages
 jms_receive_queue=JNDI name Receive queue
 jms_request=Request Only
 jms_requestreply=Request Response
 jms_sample_title=JMS Default Request
 jms_selector=JMS Selector
 jms_send_queue=JNDI name Request queue
 jms_separator=Separator
 jms_stop_between_samples=Stop between samples?
 jms_store_response=Store Response
 jms_subscriber_on_message=Use MessageListener.onMessage()
 jms_subscriber_receive=Use MessageConsumer.receive()
 jms_subscriber_title=JMS Subscriber
 jms_testing_title=Messaging Request
 jms_text_area=Text Message or Object Message serialized to XML by XStream
 jms_text_message=Text Message
 jms_timeout=Timeout (ms)
 jms_topic=Destination
 jms_use_auth=Use Authorization?
 jms_use_file=From file
 jms_use_non_persistent_delivery=Use non-persistent delivery mode?
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File from folder specified below
 jms_use_req_msgid_as_correlid=Use Request Message Id
 jms_use_res_msgid_as_correlid=Use Response Message Id
 jms_use_text=Textarea
 jms_user=User
 jndi_config_title=JNDI Configuration
 jndi_lookup_name=Remote Interface
 jndi_lookup_title=JNDI Lookup Configuration
 jndi_method_button_invoke=Invoke
 jndi_method_button_reflect=Reflect
 jndi_method_home_name=Home Method Name
 jndi_method_home_parms=Home Method Parameters
 jndi_method_name=Method Configuration
 jndi_method_remote_interface_list=Remote Interfaces
 jndi_method_remote_name=Remote Method Name
 jndi_method_remote_parms=Remote Method Parameters
 jndi_method_title=Remote Method Configuration
 jndi_testing_title=JNDI Request
 jndi_url_jndi_props=JNDI Properties
 jsonpath_renderer=JSON Path Tester
 jsonpath_tester_title=JSON Path Tester
 jsonpath_tester_field=JSON Path Expression
 jsonpath_tester_button_test=Test
 jsonpath_render_no_text=No Text
 json_post_processor_title=JSON Extractor
 jsonpp_variable_names=Variable names
 jsonpp_json_path_expressions=JSON Path expressions
 jsonpp_default_values=Default Values
 jsonpp_match_numbers=Match Numbers
 jsonpp_compute_concat=Compute concatenation var (suffix _ALL)
 jsonpp_error_number_arguments_mismatch_error=Mismatch between number of variables, json expressions and default values
 junit_append_error=Append assertion errors
 junit_append_exception=Append runtime exceptions
 junit_constructor_error=Unable to create an instance of the class
 junit_constructor_string=Constructor String Label
 junit_create_instance_per_sample=Create a new instance per sample
 junit_do_setup_teardown=Do not call setUp and tearDown
 junit_error_code=Error Code
 junit_error_default_code=9999
 junit_error_default_msg=An unexpected error occured
 junit_error_msg=Error Message
 junit_failure_code=Failure Code
 junit_failure_default_code=0001
 junit_failure_default_msg=Test failed
 junit_failure_msg=Failure Message
 junit_junit4=Search for JUnit 4 annotations (instead of JUnit 3)
 junit_pkg_filter=Package Filter
 junit_request=JUnit Request
 junit_request_defaults=JUnit Request Defaults
 junit_success_code=Success Code
 junit_success_default_code=1000
 junit_success_default_msg=Test successful
 junit_success_msg=Success Message
 junit_test_config=JUnit Test Parameters
 junit_test_method=Test Method
 language_change_test_running=A Test is currently running, stop or shutdown test to change language
 language_change_title=Test Running
 ldap_argument_list=LDAPArgument List
 ldap_connto=Connection timeout (in milliseconds)
 ldap_parse_results=Parse the search results ?
 ldap_sample_title=LDAP Request Defaults
 ldap_search_baseobject=Perform baseobject search
 ldap_search_onelevel=Perform onelevel search
 ldap_search_subtree=Perform subtree search
 ldap_secure=Use Secure LDAP Protocol ?
 ldap_testing_title=LDAP Request
 ldapext_sample_title=LDAP Extended Request Defaults
 ldapext_testing_title=LDAP Extended Request
 library=Library
 load=Load
 log_errors_only=Errors
 log_file=Location of log File
 log_function_comment=Additional comment (optional)
 log_function_level=Log level (default INFO) or OUT or ERR
 log_function_string=String to be logged
 log_function_string_ret=String to be logged (and returned)
 log_function_throwable=Throwable text (optional)
 log_only=Log/Display Only:
 log_parser=Name of Log Parser class
 log_parser_cnf_msg=Could not find the class. Please make sure you place your jar file in the /lib directory.
 log_parser_illegal_msg=Could not access the class due to IllegalAccessException.
 log_parser_instantiate_msg=Could not create an instance of the log parser. Please make sure the parser implements LogParser interface.
 log_sampler=Tomcat Access Log Sampler
 log_success_only=Successes
 logic_controller_title=Simple Controller
 login_config=Login Configuration
 login_config_element=Login Config Element
 longsum_param_1=First long to add
 longsum_param_2=Second long to add - further longs can be summed by adding further arguments.
 loop_controller_title=Loop Controller
 looping_control=Looping Control
 lower_bound=Lower Bound
 mail_reader_account=Username:
 mail_reader_all_messages=All
 mail_reader_delete=Delete messages from the server
 mail_reader_folder=Folder:
 mail_reader_header_only=Fetch headers only
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_port=Server Port (optional):
 mail_reader_server=Server Host:
 mail_reader_server_type=Protocol (e.g. pop3, imaps):
 mail_reader_storemime=Store the message using MIME (raw)
 mail_reader_title=Mail Reader Sampler
 mail_sent=Mail sent successfully
 mailer_addressees=Addressee(s): 
 mailer_attributes_panel=Mailing attributes
 mailer_connection_security=Connection security: 
 mailer_error=Couldn't send mail. Please correct any misentries.
 mailer_failure_limit=Failure Limit: 
 mailer_failure_subject=Failure Subject: 
 mailer_failures=Failures: 
 mailer_from=From: 
 mailer_host=Host: 
 mailer_login=Login: 
 mailer_msg_title_error=Error
 mailer_msg_title_information=Information
 mailer_password=Password: 
 mailer_port=Port: 
 mailer_string=E-Mail Notification
 mailer_success_limit=Success Limit: 
 mailer_success_subject=Success Subject: 
 mailer_test_mail=Test Mail
 mailer_title_message=Message
 mailer_title_settings=Mailer settings
 mailer_title_smtpserver=SMTP server
 mailer_visualizer_title=Mailer Visualizer
 match_num_field=Match No. (0 for Random)\: 
 max=Maximum
 maximum_param=The maximum value allowed for a range of values
 md5hex_assertion_failure=Error asserting MD5 sum : got {0} but should have been {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex to Assert
 md5hex_assertion_title=MD5Hex Assertion
 mechanism=Mechanism
 menu_assertions=Assertions
 menu_close=Close
 menu_collapse_all=Collapse All
 menu_config_element=Config Element
 menu_edit=Edit
 menu_expand_all=Expand All
 menu_fragments=Test Fragment
 menu_generative_controller=Sampler
 menu_listener=Listener
 menu_logger_panel=Log Viewer 
 menu_logic_controller=Logic Controller
 menu_merge=Merge
 menu_modifiers=Modifiers
 menu_non_test_elements=Non-Test Elements
 menu_open=Open
 menu_post_processors=Post Processors
 menu_pre_processors=Pre Processors
 menu_response_based_modifiers=Response Based Modifiers
 menu_search=Search
 menu_search_reset=Reset Search
 menu_tables=Table
 menu_threads=Threads (Users)
 menu_timer=Timer
 menu_toolbar=Toolbar
 metadata=MetaData
 method=Method\:
 mimetype=Mimetype
 minimum_param=The minimum value allowed for a range of values
 minute=minute
 modddn=Old entry name
 modification_controller_title=Modification Controller
 modification_manager_title=Modification Manager
 modify_test=Modify Test
 modtest=Modification test
 module_controller_module_to_run=Module To Run 
 module_controller_title=Module Controller
 module_controller_warning=Could not find module: 
 monitor_equation_active=Active:  (busy/max) > 25%
 monitor_equation_dead=Dead:  no response
 monitor_equation_healthy=Healthy:  (busy/max) < 25%
 monitor_equation_load=Load:  ( (busy / max) * 50) + ( (used memory / max memory) * 50)
 monitor_equation_warning=Warning:  (busy/max) > 67%
 monitor_health_tab_title=Health
 monitor_health_title=Monitor Results (DEPRECATED)
 monitor_is_title=Use as Monitor (DEPRECATED)
 monitor_label_left_bottom=0 %
 monitor_label_left_middle=50 %
 monitor_label_left_top=100 %
 monitor_label_prefix=Connection Prefix
 monitor_label_right_active=Active
 monitor_label_right_dead=Dead
 monitor_label_right_healthy=Healthy
 monitor_label_right_warning=Warning
 monitor_legend_health=Health
 monitor_legend_load=Load
 monitor_legend_memory_per=Memory % (used/total)
 monitor_legend_thread_per=Thread % (busy/max)
 monitor_load_factor_mem=50
 monitor_load_factor_thread=50
 monitor_performance_servers=Servers
 monitor_performance_tab_title=Performance
 monitor_performance_title=Performance Graph
 name=Name\:
 new=New
 newdn=New distinguished name
 next=Next
 no=Norwegian
 notify_child_listeners_fr=Notify Child Listeners of filtered samplers
 number_of_threads=Number of Threads (users)\:
 obsolete_test_element=This test element is obsolete
 once_only_controller_title=Once Only Controller
 opcode=opCode
 open=Open...
 option=Options
 optional_tasks=Optional Tasks
 paramtable=Send Parameters With the Request\:
 password=Password
 paste=Paste
 paste_insert=Paste As Insert
 path=Path\:
 path_extension_choice=Path Extension (use ";" as separator)
 path_extension_dont_use_equals=Do not use equals in path extension (Intershop Enfinity compatibility)
 path_extension_dont_use_questionmark=Do not use questionmark in path extension (Intershop Enfinity compatibility)
 patterns_to_exclude=URL Patterns to Exclude
 patterns_to_include=URL Patterns to Include
 pkcs12_desc=PKCS 12 Key (*.p12)
 pl=Polish
 poisson_timer_delay=Constant Delay Offset (in milliseconds)\:
 poisson_timer_memo=Adds a random delay with a poisson distribution
 poisson_timer_range=Lambda (in milliseconds)\:
 poisson_timer_title=Poisson Random Timer
 port=Port\:
 post_as_parameters=Parameters
 post_body=Body Data
 post_body_raw=Body Data
 post_files_upload=Files Upload
 post_thread_group_title=tearDown Thread Group
 previous=Previous
 property_as_field_label={0}\:
 property_default_param=Default value
 property_edit=Edit
 property_editor.value_is_invalid_message=The text you just entered is not a valid value for this property.\nThe property will be reverted to its previous value.
 property_editor.value_is_invalid_title=Invalid input
 property_name_param=Name of property
 property_returnvalue_param=Return Original Value of property (default false) ?
 property_tool_tip=<html>{0}</html>
 property_undefined=Undefined
 property_value_param=Value of property
 property_visualiser_title=Property Display
 protocol=Protocol [http]\:
 protocol_java_border=Java class
 protocol_java_classname=Classname\:
 protocol_java_config_tile=Configure Java Sample
 protocol_java_test_title=Java Testing
 provider_url=Provider URL
 proxy_assertions=Add Assertions
 proxy_cl_error=If specifying a proxy server, host and port must be given
 proxy_cl_wrong_target_cl=Target Controller is configured to "Use Recording Controller" but no such controller exists, \nensure you add a Recording Controller as child of Thread Group node to start recording correctly
 proxy_content_type_exclude=Exclude\:
 proxy_content_type_filter=Content-type filter
 proxy_content_type_include=Include\:
 proxy_daemon_bind_error=Could not create script recorder - port in use. Choose another port.
 proxy_daemon_error=Could not create script recorder - see log for details
 proxy_daemon_error_from_clipboard=from clipboard
 proxy_daemon_error_not_retrieve=Could not add retrieve
 proxy_daemon_error_read_args=Could not add read arguments from clipboard\:
 proxy_daemon_msg_check_details=Please check the details below when installing the certificate in the browser
 proxy_daemon_msg_created_in_bin=created in JMeter bin directory
 proxy_daemon_msg_install_as_in_doc=You can install it following instructions in Component Reference documentation (see Installing the JMeter CA certificate for HTTPS recording paragraph)
 proxy_daemon_msg_rootca_cert=Root CA certificate\:
 proxy_domains=HTTPS Domains \:
 proxy_domains_dynamic_mode_tooltip=List of domain names for HTTPS url, ex. jmeter.apache.org or wildcard domain like *.apache.org. Use comma as separator. 
 proxy_domains_dynamic_mode_tooltip_java6=To activate this field, use a Java 7+ runtime environment
 proxy_general_settings=Global Settings
 proxy_headers=Capture HTTP Headers
 proxy_prefix_http_sampler_name=Prefix\:
 proxy_regex=Regex matching
 proxy_sampler_settings=HTTP Sampler settings
 proxy_sampler_type=Type\:
 proxy_separators=Add Separators
 proxy_settings_port_error_digits=Only digits allowed
 proxy_settings_port_error_invalid_data=Invalid data
 proxy_target=Target Controller\:
 proxy_test_plan_content=Test plan content
 proxy_title=HTTP(S) Test Script Recorder
 pt_br=Portugese (Brazilian)
 ramp_up=Ramp-Up Period (in seconds)\:
 random_control_title=Random Controller
 random_order_control_title=Random Order Controller
 random_multi_result_source_variable=Source Variable(s) (use | as separator)
 random_multi_result_target_variable=Target Variable
 random_string_chars_to_use=Chars to use for random string generation
 random_string_length=Random string length
 realm=Realm
 record_controller_clear_samples=Clear all the recorded samples
 record_controller_title=Recording Controller
 redo=Redo
 ref_name_field=Reference Name\:
 regex_extractor_title=Regular Expression Extractor
 regex_field=Regular Expression\:
 regex_params_names_field=Parameter names regexp group number
 regex_params_ref_name_field=Regular Expression Reference Name
 regex_params_title=RegEx User Parameters
 regex_params_values_field=Parameter values regex group number
 regex_source=Field to check
 regex_src_body=Body
 regex_src_body_as_document=Body as a Document
 regex_src_body_unescaped=Body (unescaped)
 regex_src_hdrs=Response Headers
 regex_src_hdrs_req=Request Headers
 regex_src_url=URL
 regexfunc_param_1=Regular expression used to search previous sample - or variable.
 regexfunc_param_2=Template for the replacement string, using groups from the regular expression.  Format is $[group]$.  Example $1$.
 regexfunc_param_3=Which match to use.  An integer 1 or greater, RAND to indicate JMeter should randomly choose, A float, or ALL indicating all matches should be used ([1])
 regexfunc_param_4=Between text.  If ALL is selected, the between text will be used to generate the results ([""])
 regexfunc_param_5=Default text.  Used instead of the template if the regular expression finds no matches ([""])
 regexfunc_param_7=Input variable name containing the text to be parsed ([previous sample])
 regexp_render_no_text=Data response result isn't text.
 regexp_tester_button_test=Test
 regexp_tester_field=Regular expression\:
 regexp_tester_title=RegExp Tester
 remote_error_init=Error initialising remote server
 remote_error_starting=Error starting remote server
 remote_exit=Remote Exit
 remote_exit_all=Remote Exit All
 remote_shut=Remote Shutdown
 remote_shut_all=Remote Shutdown All
 remote_start=Remote Start
 remote_start_all=Remote Start All
 remote_stop=Remote Stop
 remote_stop_all=Remote Stop All
 remove=Remove
 remove_confirm_msg=Are you sure you want remove the selected element(s)?
 remove_confirm_title=Confirm remove?
 rename=Rename entry
 report=Report
 report_bar_chart=Bar Chart
 report_bar_graph_url=URL
 report_base_directory=Base Directory
 report_chart_caption=Chart Caption
 report_chart_x_axis=X Axis
 report_chart_x_axis_label=Label for X Axis
 report_chart_y_axis=Y Axis
 report_chart_y_axis_label=Label for Y Axis
 report_line_graph=Line Graph
 report_line_graph_urls=Include URLs
 report_output_directory=Output Directory for Report
 report_page=Report Page
 report_page_element=Page Element
 report_page_footer=Page Footer
 report_page_header=Page Header
 report_page_index=Create Page Index
 report_page_intro=Page Introduction
 report_page_style_url=Stylesheet url
 report_page_title=Page Title
 report_pie_chart=Pie Chart
 report_plan=Report Plan
 report_select=Select
 report_summary=Report Summary
 report_table=Report Table
 report_writer=Report Writer
 report_writer_html=HTML Report Writer
 reportgenerator_summary_apdex_apdex=Apdex
 reportgenerator_summary_apdex_samplers=Label
 reportgenerator_summary_apdex_satisfied=T (Toleration threshold)  
 reportgenerator_summary_apdex_tolerated=F (Frustration threshold)
 reportgenerator_summary_errors_count=Number of errors
 reportgenerator_summary_errors_rate_all=% in all samples
 reportgenerator_summary_errors_rate_error=% in errors
 reportgenerator_summary_errors_type=Type of error
 reportgenerator_summary_statistics_count=#Samples
 reportgenerator_summary_statistics_error_count=KO
 reportgenerator_summary_statistics_error_percent=Error %
 reportgenerator_summary_statistics_kbytes=KB/sec
 reportgenerator_summary_statistics_label=Label
 reportgenerator_summary_statistics_max=Max
 reportgenerator_summary_statistics_mean=Average response time
 reportgenerator_summary_statistics_min=Min
 reportgenerator_summary_statistics_percentile_fmt=%dth pct
 reportgenerator_summary_statistics_throughput=Throughput
 reportgenerator_summary_total=Total
 request_data=Request Data
 reset=Reset
 reset_gui=Reset Gui
 response_save_as_md5=Save response as MD5 hash?
 response_time_distribution_satisfied_label=Requests having \\nresponse time <= {0}ms
 response_time_distribution_tolerated_label= Requests having \\nresponse time > {0}ms and <= {1}ms
 response_time_distribution_untolerated_label=Requests having \\nresponse time > {0}ms
 response_time_distribution_failed_label=Requests in error
 restart=Restart
 resultaction_title=Result Status Action Handler
 resultsaver_addtimestamp=Add timestamp
 resultsaver_errors=Save Failed Responses only
 resultsaver_numberpadlen=Minimum Length of sequence number
 resultsaver_prefix=Filename prefix\:
 resultsaver_skipautonumber=Don't add number to prefix
 resultsaver_skipsuffix=Don't add suffix
 resultsaver_success=Save Successful Responses only
 resultsaver_title=Save Responses to a file
 resultsaver_variable=Variable Name:
 retobj=Return object
 return_code_config_box_title=Return Code Configuration
 reuseconnection=Re-use connection
 revert_project=Revert
 revert_project?=Revert project?
 root=Root
 root_title=Root
 run=Run
 run_threadgroup=Start
 run_threadgroup_no_timers=Start no pauses
 running_test=Running test
 runtime_controller_title=Runtime Controller
 runtime_seconds=Runtime (seconds)
 sample_result_save_configuration=Sample Result Save Configuration
 sample_scope=Apply to:
 sample_scope_all=Main sample and sub-samples
 sample_scope_children=Sub-samples only
 sample_scope_parent=Main sample only
 sample_scope_variable=JMeter Variable
 sampler_label=Label
 sampler_on_error_action=Action to be taken after a Sampler error
 sampler_on_error_continue=Continue
 sampler_on_error_start_next_loop=Start Next Thread Loop
 sampler_on_error_stop_test=Stop Test
 sampler_on_error_stop_test_now=Stop Test Now
 sampler_on_error_stop_thread=Stop Thread
 sample_timeout_memo=Interrupt the sampler if it times out
 sample_timeout_timeout=Sample timeout (in milliseconds)\:
 sample_timeout_title=Sample Timeout
 save=Save
 save?=Save?
 save_all_as=Save Test Plan as
 save_as=Save Selection As...
 save_as_error=More than one item selected!
 save_as_image=Save Node As Image
 save_as_image_all=Save Screen As Image
 save_as_test_fragment=Save as Test Fragment
 save_as_test_fragment_error=One of the selected nodes cannot be put inside a Test Fragment
 save_assertionresultsfailuremessage=Save Assertion Failure Message
 save_assertions=Save Assertion Results (XML)
 save_asxml=Save As XML
 save_bytes=Save byte count
 save_code=Save Response Code
 save_datatype=Save Data Type
 save_encoding=Save Encoding
 save_fieldnames=Save Field Names (CSV)
 save_filename=Save Response Filename
 save_graphics=Save Graph
 save_hostname=Save Hostname
 save_idletime=Save Idle Time
 save_label=Save Label
 save_latency=Save Latency
 save_connecttime=Save Connect Time
 save_message=Save Response Message
 save_overwrite_existing_file=The selected file already exists, do you want to overwrite it?
 save_requestheaders=Save Request Headers (XML)
 save_responsedata=Save Response Data (XML)
 save_responseheaders=Save Response Headers (XML)
 save_samplecount=Save Sample and Error Counts
 save_samplerdata=Save Sampler Data (XML)
+save_sentbytes=Save sent bytes count
 save_subresults=Save Sub Results (XML)
 save_success=Save Success
 save_threadcounts=Save Active Thread Counts
 save_threadname=Save Thread Name
 save_time=Save Elapsed Time
 save_timestamp=Save Time Stamp
 save_url=Save URL
 save_workbench=Save WorkBench
 sbind=Single bind/unbind
 scheduler=Scheduler
 scheduler_configuration=Scheduler Configuration
 scope=Scope
 search=Search
 search_base=Search base
 search_expand=Search & Expand
 search_filter=Search Filter
 search_test=Search Test
 search_text_button_close=Close
 search_text_button_find=Find
 search_text_button_next=Find next
 search_text_chkbox_case=Case sensitive
 search_text_chkbox_regexp=Regular exp.
 search_text_field=Search: 
 search_text_msg_not_found=Text not found
 search_text_title_not_found=Not found
 search_tree_title=Search Tree
 searchbase=Search base
 searchfilter=Search Filter
 searchtest=Search test
 second=second
 secure=Secure
 send_file=Send Files With the Request\:
 send_file_browse=Browse...
 send_file_filename_label=File Path
 send_file_mime_label=MIME Type
 send_file_param_name_label=Parameter Name
 server=Server Name or IP\:
 servername=Servername \:
 session_argument_name=Session Argument Name
 setup_thread_group_title=setUp Thread Group
 should_save=You should save your test plan before running it.  \nIf you are using supporting data files (ie, for CSV Data Set or _StringFromFile), \nthen it is particularly important to first save your test script. \nDo you want to save your test plan first?
 shutdown=Shutdown
 simple_config_element=Simple Config Element
 simple_data_writer_title=Simple Data Writer
 size_assertion_comparator_error_equal=been equal to
 size_assertion_comparator_error_greater=been greater than
 size_assertion_comparator_error_greaterequal=been greater or equal to
 size_assertion_comparator_error_less=been less than
 size_assertion_comparator_error_lessequal=been less than or equal to
 size_assertion_comparator_error_notequal=not been equal to
 size_assertion_comparator_label=Type of Comparison
 size_assertion_failure=The result was the wrong size\: It was {0} bytes, but should have {1} {2} bytes.
 size_assertion_input_error=Please enter a valid positive integer.
 size_assertion_label=Size in bytes\:
 size_assertion_size_test=Size to Assert
 size_assertion_title=Size Assertion
 smime_assertion_issuer_dn=Issuer distinguished name
 smime_assertion_message_position=Execute assertion on message at position
 smime_assertion_not_signed=Message not signed
 smime_assertion_signature=Signature
 smime_assertion_signer=Signer certificate
 smime_assertion_signer_by_file=Certificate file
 smime_assertion_signer_constraints=Check values
 smime_assertion_signer_dn=Signer distinguished name
 smime_assertion_signer_email=Signer email address
 smime_assertion_signer_no_check=No check
 smime_assertion_signer_serial=Serial Number
 smime_assertion_title=SMIME Assertion
 smime_assertion_verify_signature=Verify signature
 smtp_additional_settings=Additional Settings
 smtp_attach_file=Attach file(s):
 smtp_attach_file_tooltip=Separate multiple files with ";"
 smtp_auth_settings=Auth settings
 smtp_bcc=Address To BCC:
 smtp_cc=Address To CC:
 smtp_default_port=(Defaults: SMTP:25, SSL:465, StartTLS:587)
 smtp_eml=Send .eml:
 smtp_enabledebug=Enable debug logging?
 smtp_enforcestarttls=Enforce StartTLS
 smtp_enforcestarttls_tooltip=<html><b>Enforces</b> the server to use StartTLS.<br />If not selected and the SMTP-Server doesn't support StartTLS, <br />a normal SMTP-Connection will be used as fallback instead. <br /><i>Please note</i> that this checkbox creates a file in "/tmp/", <br />so this will cause problems under windows.</html>
 smtp_from=Address From:
 smtp_header_add=Add Header
 smtp_header_name=Header Name
 smtp_header_remove=Remove
 smtp_header_value=Header Value
 smtp_mail_settings=Mail settings
 smtp_message=Message:
 smtp_message_settings=Message settings
 smtp_messagesize=Calculate message size
 smtp_password=Password:
 smtp_plainbody=Send plain body (i.e. not multipart/mixed)
 smtp_replyto=Address Reply-To:
 smtp_sampler_title=SMTP Sampler
 smtp_security_settings=Security settings
 smtp_server=Server:
 smtp_server_connection_timeout=Connection timeout:
 smtp_server_port=Port:
 smtp_server_settings=Server settings
 smtp_server_timeout=Read timeout:
 smtp_server_timeouts_settings=Timeouts (milliseconds)
 smtp_subject=Subject:
 smtp_suppresssubj=Suppress Subject Header
 smtp_timestamp=Include timestamp in subject
 smtp_to=Address To:
 smtp_trustall=Trust all certificates
 smtp_trustall_tooltip=<html><b>Enforces</b> JMeter to trust all certificates, whatever CA it comes from.</html>
 smtp_truststore=Local truststore:
 smtp_truststore_tooltip=<html>The pathname of the truststore.<br />Relative paths are resolved against the current directory.<br />Failing that, against the directory containing the test script (JMX file)</html>
 smtp_useauth=Use Auth
 smtp_usenone=Use no security features
 smtp_username=Username:
 smtp_usessl=Use SSL
 smtp_usestarttls=Use StartTLS
 smtp_usetruststore=Use local truststore
 smtp_usetruststore_tooltip=<html>Allows JMeter to use a local truststore.</html>
 soap_action=Soap Action
 soap_data_title=Soap/XML-RPC Data
 soap_sampler_file_invalid=Filename references a missing or unreadable file\:
 soap_sampler_title=SOAP/XML-RPC Request
 soap_send_action=Send SOAPAction: 
 solinger=SO_LINGER:
 spline_visualizer_average=Average
 spline_visualizer_incoming=Incoming
 spline_visualizer_maximum=Maximum
 spline_visualizer_minimum=Minimum
 spline_visualizer_title=Spline Visualizer (DEPRECATED)
 spline_visualizer_waitingmessage=Waiting for samples
 split_function_separator=String to split on. Default is , (comma).
 split_function_string=String to split
 ssl_alias_prompt=Please type your preferred alias
 ssl_alias_select=Select your alias for the test
 ssl_alias_title=Client Alias
 ssl_error_title=Key Store Problem
 ssl_pass_prompt=Please type your password
 ssl_pass_title=KeyStore Password
 ssl_port=SSL Port
 sslmanager=SSL Manager
 start=Start
 start_no_timers=Start no pauses
 starttime=Start Time
 stop=Stop
 stopping_test=Shutting down all test threads. You can see number of active threads in the upper right corner of GUI. Please be patient. 
 stopping_test_failed=One or more test threads won't exit; see log file.
 stopping_test_host=Host
 stopping_test_title=Stopping Test
 string_from_file_encoding=File encoding if not the platform default (opt)
 string_from_file_file_name=Enter path (absolute or relative) to file
 string_from_file_seq_final=Final file sequence number (opt)
 string_from_file_seq_start=Start file sequence number (opt)
 summariser_title=Generate Summary Results
 summary_report=Summary Report
 switch_controller_label=Switch Value
 switch_controller_title=Switch Controller
 system_sampler_stderr=Standard error (stderr):
 system_sampler_stdin=Standard input (stdin):
 system_sampler_stdout=Standard output (stdout):
 system_sampler_title=OS Process Sampler
 table_visualizer_bytes=Bytes
 table_visualizer_latency=Latency
 table_visualizer_connect=Connect Time(ms)
 table_visualizer_sample_num=Sample #
 table_visualizer_sample_time=Sample Time(ms)
+table_visualizer_sent_bytes=Sent Bytes
 table_visualizer_start_time=Start Time
 table_visualizer_status=Status
 table_visualizer_success=Success
 table_visualizer_thread_name=Thread Name
 table_visualizer_warning=Warning
 target_server=Target Server
 tcp_classname=TCPClient classname\:
 tcp_config_title=TCP Sampler Config
 tcp_nodelay=Set NoDelay
 tcp_port=Port Number\:
 tcp_request_data=Text to send
 tcp_sample_title=TCP Sampler
 tcp_timeout=Timeout (milliseconds)\:
 teardown_on_shutdown=Run tearDown Thread Groups after shutdown of main threads
 template_choose=Select Template
 template_create_from=Create
 template_field=Template\:
 template_load?=Load template ?
 template_menu=Templates...
 template_merge_from=Merge
 template_reload=Reload templates
 template_title=Templates
 test=Test
 test_action_action=Action
 test_action_duration=Duration (milliseconds)
 test_action_pause=Pause
 test_action_restart_next_loop=Go to next loop iteration
 test_action_stop=Stop
 test_action_stop_now=Stop Now
 test_action_target=Target
 test_action_target_test=All Threads
 test_action_target_thread=Current Thread
 test_action_title=Test Action
 test_configuration=Test Configuration
 test_fragment_title=Test Fragment
 test_plan=Test Plan
 test_plan_classpath_browse=Add directory or jar to classpath
 testconfiguration=Test Configuration
 testplan.serialized=Run Thread Groups consecutively (i.e. run groups one at a time)
 testplan_comments=Comments\:
 testt=Test
 textbox_cancel=Cancel
 textbox_close=Close
 textbox_save_close=Save & Close
 textbox_title_edit=Edit text
 textbox_title_view=View text
 textbox_tooltip_cell=Double click to view/edit
 thread_delay_properties=Thread Delay Properties
 thread_group_title=Thread Group
 thread_properties=Thread Properties
 threadgroup=Thread Group
 throughput_control_bynumber_label=Total Executions
 throughput_control_bypercent_label=Percent Executions
 throughput_control_perthread_label=Per User
 throughput_control_title=Throughput Controller
 throughput_control_tplabel=Throughput
 time_format=Format string for SimpleDateFormat (optional)
 timelim=Time limit
 timeout_config_box_title=Timeout configuration
 timeout_title=Timeout (ms)
 toggle=Toggle
 toolbar_icon_set_not_found=The file description of toolbar icon set is not found. See logs.
 total_threads_tooltip=Total number of threads to run
 tr=Turkish
 transaction_controller_include_timers=Include duration of timer and pre-post processors in generated sample
 transaction_controller_parent=Generate parent sample
 transaction_controller_title=Transaction Controller
 transform_into_variable=Replace values with variables
 unbind=Thread Unbind
 undo=Undo
 unescape_html_string=String to unescape
 unescape_string=String containing Java escapes
 uniform_timer_delay=Constant Delay Offset (in milliseconds)\:
 uniform_timer_memo=Adds a random delay with a uniform distribution
 uniform_timer_range=Random Delay Maximum (in milliseconds)\:
 uniform_timer_title=Uniform Random Timer
 up=Up
 update=Update
 update_per_iter=Update Once Per Iteration
 upload=File Upload
 upper_bound=Upper Bound
 url=URL
 url_config_get=GET
 url_config_http=HTTP
 url_config_https=HTTPS
 url_config_post=POST
 url_config_protocol=Protocol\:
 url_config_title=HTTP Request Defaults
 url_full_config_title=UrlFull Sample
 url_multipart_config_title=HTTP Multipart Request Defaults
 urldecode_string=String with URL encoded chars to decode
 urlencode_string=String to encode in URL encoded chars
 use_custom_dns_resolver=Use custom DNS resolver
 use_expires=Use Cache-Control/Expires header when processing GET requests
 use_keepalive=Use KeepAlive
 use_multipart_for_http_post=Use multipart/form-data for POST
 use_multipart_mode_browser=Browser-compatible headers
 use_recording_controller=Use Recording Controller
 use_system_dns_resolver=Use system DNS resolver
 user=User
 user_defined_test=User Defined Test
 user_defined_variables=User Defined Variables
 user_param_mod_help_note=(Do not change this.  Instead, modify the file of that name in JMeter's /bin directory)
 user_parameters_table=Parameters
 user_parameters_title=User Parameters
 userdn=Username
 username=Username
 userpw=Password
 validate_threadgroup=Validate
 value=Value
 value_to_quote_meta=Value to escape from ORO Regexp meta chars
 var_name=Reference Name
 variable_name_param=Name of variable (may include variable and function references)
 view_graph_tree_title=View Graph Tree
 view_results_assertion_error=Assertion error: 
 view_results_assertion_failure=Assertion failure: 
 view_results_assertion_failure_message=Assertion failure message: 
 view_results_autoscroll=Scroll automatically?
 view_results_childsamples=Child samples?
 view_results_datatype=Data type ("text"|"bin"|""): 
 view_results_desc=Shows the text results of sampling in tree form
 view_results_error_count=Error Count: 
 view_results_fields=fields:
 view_results_in_table=View Results in Table
 view_results_latency=Latency: 
 view_results_connect_time=Connect Time: 
 view_results_load_time=Load time: 
 view_results_render=Render: 
 view_results_render_document=Document
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML (download resources)
 view_results_render_html_formatted=HTML Source Formatted
 view_results_render_json=JSON
 view_results_render_text=Text
 view_results_render_xml=XML
 view_results_request_headers=Request Headers:
 view_results_response_code=Response code: 
 view_results_response_headers=Response headers:
 view_results_response_message=Response message: 
 view_results_response_missing_tika=Missing tika-app.jar in classpath. Unable to convert to plain text this kind of document.\nDownload the tika-app-x.x.jar file from http://tika.apache.org/download.html\nAnd put the file in <JMeter>/lib directory.
 view_results_response_partial_message=Start of message:
 view_results_response_too_large_message=Response too large to be displayed. Size: 
 view_results_sample_count=Sample Count: 
 view_results_sample_start=Sample Start: 
 view_results_search_pane=Search pane
+view_results_sent_bytes=Sent bytes:
 view_results_size_body_in_bytes=Body size in bytes: 
 view_results_size_headers_in_bytes=Headers size in bytes: 
 view_results_size_in_bytes=Size in bytes: 
 view_results_tab_assertion=Assertion result
 view_results_tab_request=Request
 view_results_tab_response=Response data
 view_results_tab_sampler=Sampler result
 view_results_table_fields_key=Additional field
 view_results_table_fields_value=Value
 view_results_table_headers_key=Response header
 view_results_table_headers_value=Value
 view_results_table_request_headers_key=Request header
 view_results_table_request_headers_value=Value
 view_results_table_request_http_cookie=Cookie
 view_results_table_request_http_host=Host
 view_results_table_request_http_method=Method
 view_results_table_request_http_nohttp=No HTTP Sample
 view_results_table_request_http_path=Path
 view_results_table_request_http_port=Port
 view_results_table_request_http_protocol=Protocol
 view_results_table_request_params_key=Parameter name
 view_results_table_request_params_value=Value
 view_results_table_request_raw_nodata=No data to display
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=Raw
 view_results_table_result_tab_parsed=Parsed
 view_results_table_result_tab_raw=Raw
 view_results_thread_name=Thread Name: 
 view_results_title=View Results
 view_results_tree_title=View Results Tree
 warning=Warning!
 web_cannot_convert_parameters_to_raw=Cannot convert parameters to Body Data \nbecause one of the parameters has a name
 web_cannot_switch_tab=You cannot switch because data cannot be converted\n to target Tab data, empty data to switch
 web_parameters_lost_message=Switching to Body Data will convert the parameters.\nParameter table will be cleared when you select\nanother node or save the test plan.\nOK to proceeed?
 web_proxy_server_title=Proxy Server
 web_request=HTTP Request
 web_server=Web Server
 web_server_client=Client implementation:
 web_server_domain=Server Name or IP\:
 web_server_port=Port Number\:
 web_server_timeout_connect=Connect:
 web_server_timeout_response=Response:
 web_server_timeout_title=Timeouts (milliseconds)
 web_testing2_title=HTTP Request HTTPClient
 web_testing_basic=Basic
 web_testing_advanced=Advanced
 web_testing_concurrent_download=Parallel downloads. Number:
 web_testing_embedded_url_pattern=URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources
 web_testing_retrieve_title=Embedded Resources from HTML Files
 web_testing_source_ip=Source address
 web_testing_source_ip_device=Device
 web_testing_source_ip_device_ipv4=Device IPv4
 web_testing_source_ip_device_ipv6=Device IPv6
 web_testing_source_ip_hostname=IP/Hostname
 web_testing_title=HTTP Request
 while_controller_label=Condition (function or variable)
 while_controller_title=While Controller
 workbench_title=WorkBench
 xml_assertion_title=XML Assertion
 xml_download_dtds=Fetch external DTDs
 xml_namespace_button=Use Namespaces
 xml_tolerant_button=Use Tidy (tolerant parser)
 xml_validate_button=Validate XML
 xml_whitespace_button=Ignore Whitespace
 xmlschema_assertion_label=File Name:
 xmlschema_assertion_title=XML Schema Assertion
 xpath_assertion_button=Validate
 xpath_assertion_check=Check XPath Expression
 xpath_assertion_error=Error with XPath
 xpath_assertion_failed=Invalid XPath Expression
 xpath_assertion_label=XPath
 xpath_assertion_negate=True if nothing matches
 xpath_assertion_option=XML Parsing Options
 xpath_assertion_test=XPath Assertion 
 xpath_assertion_tidy=Try and tidy up the input
 xpath_assertion_title=XPath Assertion
 xpath_assertion_valid=Valid XPath Expression
 xpath_assertion_validation=Validate the XML against the DTD
 xpath_assertion_whitespace=Ignore whitespace
 xpath_expression=XPath expression to match against
 xpath_extractor_fragment=Return entire XPath fragment instead of text content?
 xpath_extractor_query=XPath query:
 xpath_extractor_title=XPath Extractor
 xpath_file_file_name=XML file to get values from 
 xpath_tester=XPath Tester
 xpath_tester_button_test=Test
 xpath_tester_field=XPath expression
 xpath_tester_fragment=Return entire XPath fragment instead of text content?
 xpath_tester_no_text=Data response result isn't text.
 xpath_tester_title=XPath Tester
 xpath_tidy_quiet=Quiet
 xpath_tidy_report_errors=Report errors
 xpath_tidy_show_warnings=Show warnings
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
 zh_tw=Chinese (Traditional)
diff --git a/src/core/org/apache/jmeter/resources/messages_fr.properties b/src/core/org/apache/jmeter/resources/messages_fr.properties
index ad78d92f9..4b6e0be0b 100644
--- a/src/core/org/apache/jmeter/resources/messages_fr.properties
+++ b/src/core/org/apache/jmeter/resources/messages_fr.properties
@@ -1,1349 +1,1352 @@
 #   Licensed to the Apache Software Foundation (ASF) under one or more
 #   contributor license agreements.  See the NOTICE file distributed with
 #   this work for additional information regarding copyright ownership.
 #   The ASF licenses this file to You under the Apache License, Version 2.0
 #   (the "License"); you may not use this file except in compliance with
 #   the License.  You may obtain a copy of the License at
 # 
 #       http://www.apache.org/licenses/LICENSE-2.0
 # 
 #   Unless required by applicable law or agreed to in writing, software
 #   distributed under the License is distributed on an "AS IS" BASIS,
 #   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #   See the License for the specific language governing permissions and
 #   limitations under the License.
 
 #Stored by I18NEdit, may be edited!
 about=A propos de JMeter
 active_threads_tooltip=Unit\u00E9s actives
 add=Ajouter
 add_as_child=Ajouter en tant qu'enfant
 add_from_clipboard=Ajouter depuis Presse-papier
 add_from_suggested_excludes=Ajouter exclusions propos\u00E9es
 add_parameter=Ajouter un param\u00E8tre
 add_pattern=Ajouter un motif \:
 add_test=Ajout
 add_user=Ajouter un utilisateur
 add_value=Ajouter valeur
 addtest=Ajout
 aggregate_graph=Graphique des statistiques
 aggregate_graph_choose_color=Choisir couleur
 aggregate_graph_choose_foreground_color=Couleur valeur
 aggregate_graph_color_bar=Couleur \:
 aggregate_graph_column=Colonne
 aggregate_graph_column_selection=S\u00E9lection de colonnes par libell\u00E9 \:
 aggregate_graph_column_settings=Param\u00E8tres colonne
 aggregate_graph_columns_to_display=Colonnes \u00E0 afficher \:
 aggregate_graph_dimension=Taille graphique
 aggregate_graph_display=G\u00E9n\u00E9rer le graphique
 aggregate_graph_draw_outlines=Bordure de barre ?
 aggregate_graph_dynamic_size=Taille de graphique dynamique
 aggregate_graph_font=Police \:
 aggregate_graph_height=Hauteur \:
 aggregate_graph_increment_scale=Intervalle \u00E9chelle \:
 aggregate_graph_legend=L\u00E9gende
 aggregate_graph_legend.placement.bottom=Bas
 aggregate_graph_legend.placement.left=Gauche
 aggregate_graph_legend.placement.right=Droite
 aggregate_graph_legend.placement.top=Haut
 aggregate_graph_legend_placement=Position \:
 aggregate_graph_max_length_xaxis_label=Longueur maximum du libell\u00E9 de l'axe des abscisses \:
 aggregate_graph_ms=Millisecondes
 aggregate_graph_no_values_to_graph=Pas de valeurs pour le graphique
 aggregate_graph_number_grouping=S\u00E9parateur de milliers ?
 aggregate_graph_response_time=Temps de r\u00E9ponse
 aggregate_graph_save=Enregistrer le graphique
 aggregate_graph_save_table=Enregistrer le tableau de donn\u00E9es
 aggregate_graph_save_table_header=Inclure l'ent\u00EAte du tableau
 aggregate_graph_size=Taille \:
 aggregate_graph_style=Style \:
 aggregate_graph_sync_with_name=Synchroniser avec nom
 aggregate_graph_tab_graph=Graphique
 aggregate_graph_tab_settings=Param\u00E8tres
 aggregate_graph_title=Graphique agr\u00E9g\u00E9
 aggregate_graph_title_group=Titre
 aggregate_graph_use_group_name=Ajouter le nom du groupe aux libell\u00E9s
 aggregate_graph_user_title=Titre du graphique \:
 aggregate_graph_value_font=Police de la valeur \:
 aggregate_graph_value_labels_vertical=Libell\u00E9 de valeurs vertical ?
 aggregate_graph_width=Largeur \:
 aggregate_graph_xaxis_group=Abscisses
 aggregate_graph_yaxis_group=Ordonn\u00E9es (milli-secondes)
 aggregate_graph_yaxis_max_value=Echelle maximum \:
 aggregate_report=Rapport agr\u00E9g\u00E9
 aggregate_report_bandwidth=Ko/sec
 aggregate_report_count=\# Echantillons
 aggregate_report_error=Erreur
 aggregate_report_error%=% Erreur
 aggregate_report_max=Max
 aggregate_report_median=M\u00E9diane
 aggregate_report_min=Min
 aggregate_report_rate=D\u00E9bit
 aggregate_report_stddev=Ecart type
 aggregate_report_total_label=TOTAL
 aggregate_report_xx_pct1_line={0}% centile
 aggregate_report_xx_pct2_line={0}% centile
 aggregate_report_xx_pct3_line={0}% centile
 ajp_sampler_title=Requ\u00EAte AJP/1.3
 als_message=Note \: Le parseur de log d'acc\u00E8s est g\u00E9n\u00E9rique et vous permet de se brancher \u00E0 
 als_message2=votre propre parseur. Pour se faire, impl\u00E9menter le LogParser, ajouter le jar au 
 als_message3=r\u00E9pertoire /lib et entrer la classe (fichier .class) dans l'\u00E9chantillon (sampler).
 analyze=En train d'analyser le fichier de donn\u00E9es
 anchor_modifier_title=Analyseur de lien HTML
 appearance=Apparence
 argument_must_not_be_negative=L'argument ne peut pas \u00EAtre n\u00E9gatif \!
 arguments_panel_title=Param\u00E8tres de commande
 assertion_assume_success=Ignorer le statut
 assertion_body_resp=Corps de r\u00E9ponse
 assertion_code_resp=Code de r\u00E9ponse
 assertion_contains=Contient (exp. r\u00E9guli\u00E8re)
 assertion_equals=Est \u00E9gale \u00E0 (texte brut)
 assertion_headers=Ent\u00EAtes de r\u00E9ponse
 assertion_matches=Correspond \u00E0 (exp. r\u00E9guli\u00E8re)
 assertion_message_resp=Message de r\u00E9ponse
 assertion_network_size=R\u00E9ponse compl\u00E8te
 assertion_not=Inverser
 assertion_pattern_match_rules=Type de correspondance du motif
 assertion_patterns_to_test=Motifs \u00E0 tester
 assertion_regex_empty_default_value=Utiliser la cha\u00EEne vide comme valeur par d\u00E9faut
 assertion_resp_field=Section de r\u00E9ponse \u00E0 tester
 assertion_resp_size_field=Taille \u00E0 v\u00E9rifier sur
 assertion_substring=Contient (texte brut)
 assertion_text_document=Document (texte)
 assertion_text_resp=Texte de r\u00E9ponse
 assertion_textarea_label=Assertions \:
 assertion_title=Assertion R\u00E9ponse
 assertion_url_samp=URL Echantillon
 assertion_visualizer_title=R\u00E9cepteur d'assertions
 attribute=Attribut \:
 attribute_field=Attribut \:
 attrs=Attributs
 auth_base_url=URL de base
 auth_manager_clear_per_iter=R\u00E9authentifier \u00E0 chaque it\u00E9ration ?
 auth_manager_options=Options
 auth_manager_title=Gestionnaire d'autorisation HTTP
 auths_stored=Autorisations stock\u00E9es
 average=Moyenne
 average_bytes=Moy. octets
 backend_listener=R\u00E9cepteur asynchrone
 backend_listener_classname=Impl\u00E9mentation du r\u00E9cepteur asynchrone
 backend_listener_paramtable=Param\u00E8tres
 backend_listener_queue_size=Taille de la queue
 bind=Connexion de l'unit\u00E9
 bouncy_castle_unavailable_message=Les jars de bouncycastle sont indisponibles, ajoutez les au classpath.
 browse=Parcourir...
 bsf_sampler_title=Echantillon BSF
 bsf_script=Script \u00E0 lancer (variables\: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=Fichier script \u00E0 lancer \:
 bsf_script_language=Langage de script \:
 bsf_script_parameters=Param\u00E8tres \u00E0 passer au script/fichier \:
 bsh_assertion_script=Script (IO\: Failure[Message], Response. IN\: Response[Data|Code|Message|Headers], RequestHeaders, Sample[Label|rData])
 bsh_assertion_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nEn lecture/\u00E9criture \: Failure, FailureMessage, SampleResult, vars, props, log.\nEn lecture seule \: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=Assertion BeanShell
 bsh_function_expression=Expression \u00E0 \u00E9valuer
 bsh_sampler_title=Echantillon BeanShell
 bsh_script=Script (voir ci-dessous pour les variables qui sont d\u00E9finies)
 bsh_script_file=Fichier script \:
 bsh_script_parameters=Param\u00E8tres  (-> String Parameters et String []bsh.args)
 bsh_script_reset_interpreter=R\u00E9initialiser l'interpr\u00E9teur bsh avant chaque appel
 bsh_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=Je suis occup\u00E9 \u00E0 tester, veuillez arr\u00EAter le test avant de changer le param\u00E8trage
 cache_manager_size=Nombre maximum d'\u00E9l\u00E9ments dans le cache
 cache_manager_title=Gestionnaire de cache HTTP
 cache_session_id=Identifiant de session de cache ?
 cancel=Annuler
 cancel_exit_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de sortir ?
 cancel_new_from_template=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de charger le mod\u00E8le ?
 cancel_new_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de nettoyer le plan de test ?
 cancel_revert_project=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Annuler les changements et revenir \u00E0 la derni\u00E8re sauvegarde du plan de test ?
 change_parent=Changer le contr\u00F4leur
 char_value=Caract\u00E8re num\u00E9rique Unicode (d\u00E9cimal or 0xhex)
 check_return_code_title=V\u00E9rifier le code retour
 choose_function=Choisir une fonction
 choose_language=Choisir une langue
 clear=Nettoyer
 clear_all=Nettoyer tout
 clear_cache_each_iteration=Vider le cache \u00E0 chaque it\u00E9ration ?
 clear_cache_per_iter=Nettoyer le cache \u00E0 chaque it\u00E9ration ?
 clear_cookies_per_iter=Nettoyer les cookies \u00E0 chaque it\u00E9ration ?
 clipboard_node_read_error=Une erreur est survenue lors de la copie du noeud
 close=Fermer
 closeconnection=Fermer la connexion
 collapse_tooltip=Cliquer pour ouvrir / r\u00E9duire
 column_delete_disallowed=Supprimer cette colonne n'est pas possible
 column_number=Num\u00E9ro de colonne du fichier CSV | next | *alias
 command_config_box_title=Commande \u00E0 ex\u00E9cuter
 command_config_std_streams_title=Flux standard (fichiers)
 command_field_title=Commande \:
 compare=Comparaison
 comparefilt=Filtre de comparaison
 comparison_differ_content=Le contenu des r\u00E9ponses est diff\u00E9rent.
 comparison_differ_time=La diff\u00E9rence du temps de r\u00E9ponse diff\u00E8re de plus de 
 comparison_invalid_node=Noeud invalide 
 comparison_regex_string=Expression r\u00E9guli\u00E8re
 comparison_regex_substitution=Substitution
 comparison_response_time=Temps de r\u00E9ponse \: 
 comparison_unit=ms
 comparison_visualizer_title=R\u00E9cepteur d'assertions de comparaison
 config_element=El\u00E9ment de configuration
 config_save_settings=Configurer
 confirm=Confirmer
 constant_throughput_timer_memo=Ajouter un d\u00E9lai entre les \u00E9chantillions pour obtenir un d\u00E9bit constant
 constant_timer_delay=D\u00E9lai d'attente (en millisecondes) \:
 constant_timer_memo=Ajouter un d\u00E9lai fixe entre les \u00E9chantillions de test
 constant_timer_title=Compteur de temps fixe
 content_encoding=Encodage contenu \:
 controller=Contr\u00F4leur
 cookie_implementation_choose=Impl\u00E9mentation \:
 cookie_manager_policy=Politique des cookies \:
 cookie_manager_title=Gestionnaire de cookies HTTP
 cookie_options=Options
 cookies_stored=Cookies stock\u00E9s
 copy=Copier
 counter_config_title=Compteur
 counter_per_user=Suivre le compteur ind\u00E9pendamment pour chaque unit\u00E9 de test
 counter_reset_per_tg_iteration=R\u00E9initialiser le compteur \u00E0 chaque it\u00E9ration du groupe d'unit\u00E9s
 countlim=Limiter le nombre d'\u00E9l\u00E9ments retourn\u00E9s \u00E0
 critical_section_controller_label=Nom du verrou
 critical_section_controller_title=Contr\u00F4leur Section critique
 cssjquery_attribute=Attribut
 cssjquery_empty_default_value=Utiliser la cha\u00EEne vide comme valeur par d\u00E9faut
 cssjquery_impl=Impl\u00E9mentation CSS/JQuery\:
 cssjquery_render_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 cssjquery_tester_button_test=Tester
 cssjquery_tester_error=Une erreur s''est produite lors de l''\u00E9valuation de l''expression\:{0}, erreur\:{1}
 cssjquery_tester_field=S\u00E9lecteur\:
 cssjquery_tester_title=Testeur CSS/JQuery
 csvread_file_file_name=Fichier CSV pour obtenir les valeurs de | *alias
 cut=Couper
 cut_paste_function=Fonction de copier/coller de cha\u00EEne de caract\u00E8re
 database_conn_pool_max_usage=Utilisation max pour chaque connexion\:
 database_conn_pool_props=Pool de connexions \u221A\u2020 la base de donn\u221A\u00A9es
 database_conn_pool_size=Nombre de Connexions dans le Pool\:
 database_conn_pool_title=Valeurs par d\u00E9faut du Pool de connexions JDBC
 database_driver_class=Classe du Driver\:
 database_login_title=Valeurs par d\u00E9faut de la base de donn\u221A\u00A9es JDBC
 database_sql_query_string=Requ\u00EAte SQL \:
 database_sql_query_title=Requ\u00EAte SQL JDBC par d\u00E9faut
 database_testing_title=Requ\u221A\u2122te JDBC
 database_url=URL JDBC\:
 database_url_jdbc_props=URL et driver JDBC de la base de donn\u221A\u00A9es
 ddn=DN \:
 de=Allemand
 debug_off=D\u00E9sactiver le d\u00E9bogage
 debug_on=Activer le d\u00E9bogage
 default_parameters=Param\u00E8tres par d\u00E9faut
 default_value_field=Valeur par d\u00E9faut \:
 delay=D\u00E9lai avant d\u00E9marrage (secondes) \:
 delayed_start=Cr\u00E9er les unit\u00E9s seulement quand n\u00E9cessaire
 delete=Supprimer
 delete_parameter=Supprimer le param\u00E8tre
 delete_test=Suppression
 delete_user=Supprimer l'utilisateur
 deltest=Suppression
 deref=D\u00E9r\u00E9f\u00E9rencement des alias
 description=Description
 detail=D\u00E9tail
 directory_field_title=R\u00E9pertoire d'ex\u00E9cution \:
 disable=D\u00E9sactiver
 distribution_graph_title=Graphique de distribution (DEPRECATED)
 distribution_note1=Ce graphique se mettra \u00E0 jour tous les 10 \u00E9chantillons
 dn=Racine DN \:
 dns_cache_manager_title=Gestionnaire de cache DNS
 dns_hostname_or_ip=Nom de machine ou adresse IP
 dns_servers=Serveurs DNS
 domain=Domaine \:
 done=Fait
 down=Descendre
 duplicate=Dupliquer
 duration=Dur\u00E9e (secondes) \:
 duration_assertion_duration_test=Dur\u00E9e maximale \u00E0 v\u00E9rifier
 duration_assertion_failure=L''op\u00E9ration a dur\u00E9e trop longtemps\: cela a pris {0} millisecondes, mais n''aurait pas d\u00FB durer plus de {1} millisecondes.
 duration_assertion_input_error=Veuillez entrer un entier positif valide.
 duration_assertion_label=Dur\u00E9e en millisecondes \:
 duration_assertion_title=Assertion Dur\u00E9e
 duration_tooltip=Temps pass\u00E9 depuis le d\u00E9but du test en cours
 edit=Editer
 email_results_title=R\u00E9sultat d'email
 en=Anglais
 enable=Activer
 encode=URL Encoder
 encode?=Encodage
 encoded_value=Valeur de l'URL encod\u00E9e
 endtime=Date et heure de fin \:
 entry_dn=Entr\u00E9e DN \:
 entrydn=Entr\u00E9e DN
 environment_panel_title=Variables d'environnement
 eolbyte=Valeur byte de l'indicateur de fin de ligne (EOL)\: 
 error_indicator_tooltip=Affiche le nombre d'erreurs dans le journal(log), cliquer pour afficher la console.
 error_loading_help=Erreur au chargement de la page d'aide
 error_occurred=Une erreur est survenue
 error_title=Erreur
 es=Espagnol
 escape_html_string=Cha\u00EEne d'\u00E9chappement
 eval_name_param=Variable contenant du texte et r\u00E9f\u00E9rences de fonctions
 evalvar_name_param=Nom de variable
 example_data=Exemple de donn\u00E9e
 example_title=Echantillon exemple
 exit=Quitter
 expected_return_code_title=Code retour attendu \: 
 expiration=Expiration
 expression_field=Expression CSS/JQuery \:
 field_name=Nom du champ
 file=Fichier
 file_already_in_use=Ce fichier est d\u00E9j\u00E0 utilis\u00E9
 file_visualizer_append=Concat\u00E9ner au fichier de donn\u00E9es existant
 file_visualizer_auto_flush=Vider automatiquement apr\u00E8s chaque echantillon de donn\u00E9es
 file_visualizer_browse=Parcourir...
 file_visualizer_close=Fermer
 file_visualizer_file_options=Options de fichier
 file_visualizer_filename=Nom du fichier \: 
 file_visualizer_flush=Vider
 file_visualizer_missing_filename=Aucun fichier de sortie sp\u00E9cifi\u00E9.
 file_visualizer_open=Ouvrir...
 file_visualizer_output_file=\u00C9crire les r\u00E9sultats dans un fichier ou lire les r\u00E9sultats depuis un fichier CSV / JTL
 file_visualizer_submit_data=Inclure les donn\u00E9es envoy\u00E9es
 file_visualizer_title=Rapporteur de fichier
 file_visualizer_verbose=Sortie verbeuse
 filename=Nom de fichier \: 
 find_target_element=Trouver l'\u00E9l\u00E9ment cible
 follow_redirects=Suivre les redirect.
 follow_redirects_auto=Rediriger automat.
 font.sansserif=Sans Serif
 font.serif=Serif
 fontstyle.bold=Gras
 fontstyle.italic=Italique
 fontstyle.normal=Normal
 foreach_controller_title=Contr\u00F4leur Pour chaque (ForEach)
 foreach_end_index=Indice de fin de la boucle (inclus)
 foreach_input=Pr\u00E9fixe de la variable d'entr\u00E9e \:
 foreach_output=Nom de la variable de sortie \:
 foreach_start_index=Indice de d\u00E9but de la boucle(exclus)
 foreach_use_separator=Ajouter un soulign\u00E9 "_" avant le nombre ?
 format=Format du nombre \:
 fr=Fran\u00E7ais
 ftp_binary_mode=Utiliser le mode binaire ?
 ftp_get=R\u00E9cup\u00E9rer (get)
 ftp_local_file=Fichier local \:
 ftp_local_file_contents=Contenus fichier local \:
 ftp_put=D\u00E9poser (put)
 ftp_remote_file=Fichier distant \:
 ftp_sample_title=Param\u00E8tres FTP par d\u00E9faut
 ftp_save_response_data=Enregistrer le fichier dans la r\u00E9ponse ?
 ftp_testing_title=Requ\u00EAte FTP
 function_dialog_menu_item=Assistant de fonctions
 function_helper_title=Assistant de fonctions
 function_name_param=Nom de la fonction. Utilis\u00E9 pour stocker les valeurs \u00E0 utiliser ailleurs dans la plan de test
 function_name_paropt=Nom de variable dans laquelle le r\u00E9sultat sera stock\u00E9 (optionnel)
 function_params=Param\u00E8tres de la fonction
 functional_mode=Mode de test fonctionnel
 functional_mode_explanation=S\u00E9lectionner le mode de test fonctionnel uniquement si vous avez besoin\nd'enregistrer les donn\u00E9es re\u00E7ues du serveur dans un fichier \u00E0 chaque requ\u00EAte. \n\nS\u00E9lectionner cette option affecte consid\u00E9rablement les performances.
 gaussian_timer_delay=D\u00E9lai de d\u00E9calage bas\u00E9 gaussian (en millisecondes) \:
 gaussian_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution gaussienne
 gaussian_timer_range=D\u00E9viation (en millisecondes) \:
 gaussian_timer_title=Compteur de temps al\u00E9atoire gaussien
 generate=G\u00E9n\u00E9rer
 generator=Nom de la classe g\u00E9n\u00E9ratrice
 generator_cnf_msg=N'a pas p\u00FB trouver la classe g\u00E9n\u00E9ratrice. Assurez-vous que vous avez plac\u00E9 votre fichier jar dans le r\u00E9pertoire /lib
 generator_illegal_msg=N'a pas p\u00FB acc\u00E9der \u00E0 la classes g\u00E9n\u00E9ratrice \u00E0 cause d'une IllegalAccessException.
 generator_instantiate_msg=N'a pas p\u00FB cr\u00E9er une instance du parseur g\u00E9n\u00E9rateur. Assurez-vous que le g\u00E9n\u00E9rateur impl\u00E9mente l'interface Generator.
 graph_apply_filter=Appliquer le filtre
 graph_choose_graphs=Graphique \u00E0 afficher
 graph_full_results_title=Graphique de r\u00E9sultats complets
 graph_pointshape_circle=Cercle
 graph_pointshape_diamond=Diamant
 graph_pointshape_none=Aucun
 graph_pointshape_square=Carr\u00E9
 graph_pointshape_triangle=Triangle
 graph_resp_time_interval_label=Interval (ms) \:
 graph_resp_time_interval_reload=Appliquer l'interval
 graph_resp_time_not_enough_data=Impossible de dessiner le graphique, pas assez de donn\u00E9es
 graph_resp_time_series_selection=S\u00E9lection des \u00E9chantillons par libell\u00E9 \:
 graph_resp_time_settings_line=Param\u00E9tres de la courbe
 graph_resp_time_settings_pane=Param\u00E9tres du graphique
 graph_resp_time_shape_label=Forme de la jonction \:
 graph_resp_time_stroke_width=Largeur de ligne \:
 graph_resp_time_title=Graphique \u00E9volution temps de r\u00E9ponses
 graph_resp_time_title_label=Titre du graphique \:  
 graph_resp_time_xaxis_time_format=Formatage heure (SimpleDateFormat) \:
 graph_results_average=Moyenne
 graph_results_data=Donn\u00E9es
 graph_results_deviation=Ecart type
 graph_results_latest_sample=Dernier \u00E9chantillon
 graph_results_median=M\u00E9diane
 graph_results_ms=ms
 graph_results_no_samples=Nombre d'\u00E9chantillons
 graph_results_throughput=D\u00E9bit
 graph_results_title=Graphique de r\u00E9sultats
 groovy_function_expression=Expression \u00E0 \u00E9valuer
 grouping_add_separators=Ajouter des s\u00E9parateurs entre les groupes
 grouping_in_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur
 grouping_in_transaction_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur de transaction
 grouping_mode=Grouper \:
 grouping_no_groups=Ne pas grouper les \u00E9chantillons
 grouping_store_first_only=Stocker le 1er \u00E9chantillon pour chaque groupe uniquement
 header_manager_title=Gestionnaire d'ent\u00EAtes HTTP
 headers_stored=Ent\u00EAtes stock\u00E9es
 heap_dump=Cr\u00E9er une image disque de la m\u00E9moire (heap dump)
 help=Aide
 help_node=Quel est ce noeud ?
 html_assertion_file=Ecrire un rapport JTidy dans un fichier
 html_assertion_label=Assertion HTML
 html_assertion_title=Assertion HTML
 html_extractor_title=Extracteur CSS/JQuery
 html_extractor_type=Impl\u00E9mentation de l'extracteur CSS/JQuery
 http_implementation=Impl\u00E9mentation \:
 http_response_code=Code de r\u00E9ponse HTTP
 http_url_rewriting_modifier_title=Transcripteur d'URL HTTP
 http_user_parameter_modifier=Modificateur de param\u00E8tre utilisateur HTTP
 httpmirror_max_pool_size=Taille maximum du pool d'unit\u00E9s \:
 httpmirror_max_queue_size=Taille maximum de la file d'attente \:
 httpmirror_settings=Param\u00E8tres
 httpmirror_title=Serveur HTTP miroir
 id_prefix=Pr\u00E9fixe d'ID
 id_suffix=Suffixe d'ID
 if_controller_evaluate_all=Evaluer pour tous les fils ?
 if_controller_expression=Interpr\u00E9ter la condition comme une expression
 if_controller_label=Condition (d\u00E9faut Javascript) \:
 if_controller_title=Contr\u00F4leur Si (If)
 ignore_subcontrollers=Ignorer les sous-blocs de contr\u00F4leurs
 include_controller=Contr\u00F4leur Inclusion
 include_equals=Inclure \u00E9gale ?
 include_path=Plan de test \u00E0 inclure
 increment=Incr\u00E9ment \:
 infinite=Infini
 initial_context_factory=Fabrique de contexte initiale
 insert_after=Ins\u00E9rer apr\u00E8s
 insert_before=Ins\u00E9rer avant
 insert_parent=Ins\u00E9rer en tant que parent
 interleave_control_title=Contr\u00F4leur Interleave
 interleave_accross_threads=Alterne en prenant en compte toutes les unit\u00E9s
 intsum_param_1=Premier entier \u00E0 ajouter
 intsum_param_2=Deuxi\u00E8me entier \u00E0 ajouter - les entier(s) suivants peuvent \u00EAtre ajout\u00E9(s) avec les arguments suivants.
 invalid_data=Donn\u00E9e invalide
 invalid_mail=Une erreur est survenue lors de l'envoi de l'email
 invalid_mail_address=Une ou plusieurs adresse(s) invalide(s) ont \u00E9t\u00E9 d\u00E9tect\u00E9e(s)
 invalid_mail_server=Le serveur de mail est inconnu (voir le fichier de journalisation JMeter)
 invalid_variables=Variables invalides
 iteration_counter_arg_1=TRUE, pour que chaque utilisateur ait son propre compteur, FALSE pour un compteur global
 iterator_num=Nombre d'it\u00E9rations \:
 ja=Japonais
 jar_file=Fichiers .jar
 java_request=Requ\u00EAte Java
 java_request_defaults=Requ\u00EAte Java par d\u00E9faut
 javascript_expression=Expression JavaScript \u00E0 \u00E9valuer
 jexl_expression=Expression JEXL \u00E0 \u00E9valuer
 jms_auth_required=Obligatoire
 jms_bytes_message=Message binaire
 jms_client_caption=Le client r\u00E9cepteur utilise MessageConsumer.receive () pour \u00E9couter les messages.
 jms_client_caption2=MessageListener utilise l'interface onMessage(Message) pour \u00E9couter les nouveaux messages.
 jms_client_id=ID du Client
 jms_client_type=Client
 jms_communication_style=Type de communication \: 
 jms_concrete_connection_factory=Fabrique de connexion 
 jms_config=Source du message \:
 jms_config_title=Configuration JMS
 jms_connection_factory=Fabrique de connexion
 jms_correlation_title=Champs alternatifs pour la correspondance de message
 jms_dest_setup=Evaluer
 jms_dest_setup_dynamic=A chaque \u00E9chantillon
 jms_dest_setup_static=Au d\u00E9marrage
 jms_durable_subscription_id=ID d'abonnement durable
 jms_expiration=Expiration (ms)
 jms_file=Fichier
 jms_initial_context_factory=Fabrique de connexion initiale
 jms_itertions=Nombre d'\u00E9chantillons \u00E0 agr\u00E9ger
 jms_jndi_defaults_title=Configuration JNDI par d\u00E9faut
 jms_jndi_props=Propri\u00E9t\u00E9s JNDI
 jms_map_message=Message Map
 jms_message_title=Propri\u00E9t\u00E9s du message
 jms_message_type=Type de message \: 
 jms_msg_content=Contenu
 jms_object_message=Message Object
 jms_point_to_point=Requ\u00EAte JMS Point-\u00E0-point
 jms_priority=Priorit\u00E9 (0-9)
 jms_properties=Propri\u00E9t\u00E9s JMS
 jms_properties_name=Nom
 jms_properties_title=Propri\u00E9t\u00E9s JMS
 jms_properties_type=Classe de la Valeur
 jms_properties_value=Valeur
 jms_props=Propri\u00E9t\u00E9s JMS
 jms_provider_url=URL du fournisseur
 jms_publisher=Requ\u00EAte JMS Publication
 jms_pwd=Mot de passe
 jms_queue=File
 jms_queue_connection_factory=Fabrique QueueConnection
 jms_queueing=Ressources JMS
 jms_random_file=Dossier contenant des fichiers al\u00E9atoires (suffix\u00E9s par .dat pour un message binaire, .txt ou .obj pour un message texte ou un objet)
 jms_receive_queue=Nom JNDI de la file d'attente Receive 
 jms_request=Requ\u00EAte seule
 jms_requestreply=Requ\u00EAte R\u00E9ponse
 jms_sample_title=Requ\u00EAte JMS par d\u00E9faut
 jms_selector=S\u00E9lecteur JMS
 jms_send_queue=Nom JNDI de la file d'attente Request
 jms_separator=S\u00E9parateur
 jms_stop_between_samples=Arr\u00EAter entre les \u00E9chantillons ?
 jms_store_response=Stocker la r\u00E9ponse
 jms_subscriber_on_message=Utiliser MessageListener.onMessage()
 jms_subscriber_receive=Utiliser MessageConsumer.receive()
 jms_subscriber_title=Requ\u00EAte JMS Abonnement
 jms_testing_title=Messagerie Request
 jms_text_area=Message texte ou Message Objet s\u00E9rialis\u00E9 en XML par XStream
 jms_text_message=Message texte
 jms_timeout=D\u00E9lai (ms)
 jms_topic=Destination
 jms_use_auth=Utiliser l'authentification ?
 jms_use_file=Depuis un fichier
 jms_use_non_persistent_delivery=Utiliser un mode de livraison non persistant ?
 jms_use_properties_file=Utiliser le fichier jndi.properties
 jms_use_random_file=Fichier al\u00E9atoire
 jms_use_req_msgid_as_correlid=Utiliser l'ID du message Request
 jms_use_res_msgid_as_correlid=Utiliser l'ID du message Response
 jms_use_text=Zone de texte (ci-dessous)
 jms_user=Utilisateur
 jndi_config_title=Configuration JNDI
 jndi_lookup_name=Interface remote
 jndi_lookup_title=Configuration Lookup JNDI 
 jndi_method_button_invoke=Invoquer
 jndi_method_button_reflect=R\u00E9flection
 jndi_method_home_name=Nom de la m\u00E9thode home
 jndi_method_home_parms=Param\u00E8tres de la m\u00E9thode home
 jndi_method_name=Configuration m\u00E9thode
 jndi_method_remote_interface_list=Interfaces remote
 jndi_method_remote_name=Nom m\u00E9thodes remote
 jndi_method_remote_parms=Param\u00E8tres m\u00E9thode remote
 jndi_method_title=Configuration m\u00E9thode remote
 jndi_testing_title=Requ\u00EAte JNDI
 jndi_url_jndi_props=Propri\u00E9t\u00E9s JNDI
 json_post_processor_title=Extracteur JSON
 jsonpath_render_no_text=Pas de Texte
 jsonpath_renderer=Testeur JSON Path
 jsonpath_tester_button_test=Tester
 jsonpath_tester_field=Expression JSON Path
 jsonpath_tester_title=Testeur JSON Path
 jsonpp_compute_concat=Calculer la variable de concat\u00E9nation (suffix _ALL)
 jsonpp_default_values=Valeure par d\u00E9fault
 jsonpp_error_number_arguments_mismatch_error=D\u00E9calage entre nombre de variables, expressions et valeurs par d\u00E9faut
 jsonpp_json_path_expressions=Expressions JSON Path
 jsonpp_match_numbers=Nombre de correspondances
 jsonpp_variable_names=Noms des variables
 junit_append_error=Concat\u00E9ner les erreurs d'assertion
 junit_append_exception=Concat\u00E9ner les exceptions d'ex\u00E9cution
 junit_constructor_error=Impossible de cr\u00E9er une instance de la classe
 junit_constructor_string=Libell\u00E9 de cha\u00EEne Constructeur
 junit_create_instance_per_sample=Cr\u00E9er une nouvelle instance pour chaque \u00E9chantillon
 junit_do_setup_teardown=Ne pas appeler setUp et tearDown
 junit_error_code=Code d'erreur
 junit_error_default_msg=Une erreur inattendue est survenue
 junit_error_msg=Message d'erreur
 junit_failure_code=Code d'\u00E9chec
 junit_failure_default_msg=Test \u00E9chou\u00E9
 junit_failure_msg=Message d'\u00E9chec
 junit_junit4=Rechercher les annotations JUnit 4 (au lieu de JUnit 3)
 junit_pkg_filter=Filtre de paquets
 junit_request=Requ\u00EAte JUnit
 junit_request_defaults=Requ\u00EAte par d\u00E9faut JUnit
 junit_success_code=Code de succ\u00E8s
 junit_success_default_msg=Test r\u00E9ussi
 junit_success_msg=Message de succ\u00E8s
 junit_test_config=Param\u00E8tres Test JUnit
 junit_test_method=M\u00E9thode de test
 language_change_test_running=Un test est en cours, arr\u00EAtez le avant de changer la langue
 language_change_title=Test en cours
 ldap_argument_list=Liste d'arguments LDAP
 ldap_connto=D\u00E9lai d'attente de connexion (millisecondes)
 ldap_parse_results=Examiner les r\u00E9sultats de recherche ?
 ldap_sample_title=Requ\u00EAte LDAP par d\u00E9faut
 ldap_search_baseobject=Effectuer une recherche 'baseobject'
 ldap_search_onelevel=Effectuer une recherche 'onelevel'
 ldap_search_subtree=Effectuer une recherche 'subtree'
 ldap_secure=Utiliser le protocole LDAP s\u00E9curis\u00E9 (ldaps) ?
 ldap_testing_title=Requ\u00EAte LDAP
 ldapext_sample_title=Requ\u00EAte LDAP \u00E9tendue par d\u00E9faut
 ldapext_testing_title=Requ\u00EAte LDAP \u00E9tendue
 library=Librairie
 load=Charger
 log_errors_only=Erreurs
 log_file=Emplacement du fichier de journal (log)
 log_function_comment=Commentaire (facultatif)
 log_function_level=Niveau de journalisation (INFO par d\u00E9faut), OUT ou ERR
 log_function_string=Cha\u00EEne \u00E0 tracer
 log_function_string_ret=Cha\u00EEne \u00E0 tracer (et \u00E0 retourner)
 log_function_throwable=Texte de l'exception Throwable (optionnel)
 log_only=Uniquement \:
 log_parser=Nom de la classe de parseur des journaux (log)
 log_parser_cnf_msg=N'a pas p\u00FB trouver cette classe. Assurez-vous que vous avez plac\u00E9 votre fichier jar dans le r\u00E9pertoire /lib
 log_parser_illegal_msg=N'a pas p\u00FB acc\u00E9der \u00E0 la classe \u00E0 cause d'une exception IllegalAccessException.
 log_parser_instantiate_msg=N'a pas p\u00FB cr\u00E9er une instance du parseur de log. Assurez-vous que le parseur impl\u00E9mente l'interface LogParser.
 log_sampler=Echantillon Journaux d'acc\u00E8s Tomcat
 log_success_only=Succ\u00E8s
 logic_controller_title=Contr\u00F4leur Simple
 login_config=Configuration Identification
 login_config_element=Configuration Identification 
 longsum_param_1=Premier long \u221A\u2020 ajouter
 longsum_param_2=Second long \u221A\u2020 ajouter - les autres longs pourront \u221A\u2122tre cumul\u221A\u00A9s en ajoutant d'autres arguments.
 loop_controller_title=Contr\u00F4leur Boucle
 looping_control=Contr\u00F4le de boucle
 lower_bound=Borne Inf\u00E9rieure
 mail_reader_account=Nom utilisateur \:
 mail_reader_all_messages=Tous
 mail_reader_delete=Supprimer les messages du serveur
 mail_reader_folder=Dossier \:
 mail_reader_header_only=R\u00E9cup\u00E9rer seulement les ent\u00EAtes
 mail_reader_num_messages=Nombre de message \u00E0 r\u00E9cup\u00E9rer \:
 mail_reader_password=Mot de passe \:
 mail_reader_port=Port (optionnel) \:
 mail_reader_server=Serveur \:
 mail_reader_server_type=Protocole (ex. pop3, imaps) \:
 mail_reader_storemime=Stocker le message en utilisant MIME (brut)
 mail_reader_title=Echantillon Lecteur d'email
 mail_sent=Email envoy\u00E9 avec succ\u00E8s
 mailer_addressees=Destinataire(s) \: 
 mailer_attributes_panel=Attributs de courrier
 mailer_connection_security=S\u00E9curit\u00E9 connexion \: 
 mailer_error=N'a pas p\u00FB envoyer l'email. Veuillez corriger les erreurs de saisie.
 mailer_failure_limit=Limite d'\u00E9chec \: 
 mailer_failure_subject=Sujet Echec \: 
 mailer_failures=Nombre d'\u00E9checs \: 
 mailer_from=Exp\u00E9diteur \: 
 mailer_host=Serveur \: 
 mailer_login=Identifiant \: 
 mailer_msg_title_error=Erreur
 mailer_msg_title_information=Information
 mailer_password=Mot de passe \: 
 mailer_port=Port \: 
 mailer_string=Notification d'email
 mailer_success_limit=Limite de succ\u00E8s \: 
 mailer_success_subject=Sujet Succ\u00E8s \: 
 mailer_test_mail=Tester email
 mailer_title_message=Message
 mailer_title_settings=Param\u00E8tres
 mailer_title_smtpserver=Serveur SMTP
 mailer_visualizer_title=R\u00E9cepteur Notification Email
 match_num_field=R\u00E9cup\u00E9rer la Ni\u00E8me corresp. (0 \: Al\u00E9atoire) \: 
 max=Maximum \:
 maximum_param=La valeur maximum autoris\u00E9e pour un \u00E9cart de valeurs
 md5hex_assertion_failure=Erreur de v\u00E9rification de la somme MD5 \: obtenu {0} mais aurait d\u00FB \u00EAtre {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex \u00E0 v\u00E9rifier
 md5hex_assertion_title=Assertion MD5Hex
 mechanism=M\u00E9canisme
 menu_assertions=Assertions
 menu_close=Fermer
 menu_collapse_all=R\u00E9duire tout
 menu_config_element=Configurations
 menu_edit=Editer
 menu_expand_all=Etendre tout
 menu_fragments=Fragment d'\u00E9l\u00E9ments
 menu_generative_controller=Echantillons
 menu_listener=R\u00E9cepteurs
 menu_logger_panel=Afficher la console 
 menu_logic_controller=Contr\u00F4leurs Logiques
 menu_merge=Fusionner...
 menu_modifiers=Modificateurs
 menu_non_test_elements=El\u00E9ments hors test
 menu_open=Ouvrir...
 menu_post_processors=Post-Processeurs
 menu_pre_processors=Pr\u00E9-Processeurs
 menu_response_based_modifiers=Modificateurs bas\u00E9s sur la r\u00E9ponse
 menu_search=Rechercher
 menu_search_reset=Effacer la recherche
 menu_tables=Table
 menu_threads=Moteurs d'utilisateurs
 menu_timer=Compteurs de temps
 menu_toolbar=Barre d'outils
 metadata=M\u00E9ta-donn\u00E9es
 method=M\u00E9thode \:
 mimetype=Type MIME
 minimum_param=La valeur minimale autoris\u00E9e pour l'\u00E9cart de valeurs
 minute=minute
 modddn=Ancienne valeur
 modification_controller_title=Contr\u00F4leur Modification
 modification_manager_title=Gestionnaire Modification
 modify_test=Modification
 modtest=Modification
 module_controller_module_to_run=Module \u00E0 ex\u00E9cuter \:
 module_controller_title=Contr\u00F4leur Module
 module_controller_warning=Ne peut pas trouver le module \:
 monitor_equation_active=Activit\u00E9 \:  (occup\u00E9e/max) > 25%
 monitor_equation_dead=Mort \: pas de r\u00E9ponse
 monitor_equation_healthy=Sant\u00E9 \:  (occup\u00E9e/max) < 25%
 monitor_equation_load=Charge \:  ((occup\u00E9e / max) * 50) + ((m\u00E9moire utilis\u00E9e / m\u00E9moire maximum) * 50)
 monitor_equation_warning=Attention \:  (occup\u00E9/max) > 67%
 monitor_health_tab_title=Sant\u00E9
 monitor_health_title=Moniteur de connecteurs (DEPRECATED)
 monitor_is_title=Utiliser comme moniteur (DEPRECATED)
 monitor_label_prefix=Pr\u00E9fixe de connecteur \:
 monitor_label_right_active=Actif
 monitor_label_right_dead=Mort
 monitor_label_right_healthy=Sant\u00E9
 monitor_label_right_warning=Attention
 monitor_legend_health=Sant\u00E9
 monitor_legend_load=Charge
 monitor_legend_memory_per=M\u00E9moire % (utilis\u00E9e/total)
 monitor_legend_thread_per=Unit\u00E9 % (occup\u00E9/max)
 monitor_performance_servers=Serveurs
 monitor_performance_tab_title=Performance
 monitor_performance_title=Graphique de performance
 name=Nom \:
 new=Nouveau
 newdn=Nouveau DN
 next=Suivant
 no=Norv\u00E9gien
 notify_child_listeners_fr=Notifier les r\u00E9cepteurs fils des \u00E9chantillons filtr\u00E9s
 number_of_threads=Nombre d'unit\u00E9s (utilisateurs) \:
 obsolete_test_element=Cet \u00E9l\u00E9ment de test est obsol\u00E8te
 once_only_controller_title=Contr\u00F4leur Ex\u00E9cution unique
 opcode=Code d'op\u00E9ration
 open=Ouvrir...
 option=Options
 optional_tasks=T\u00E2ches optionnelles
 paramtable=Envoyer les param\u00E8tres avec la requ\u00EAte \:
 password=Mot de passe \:
 paste=Coller
 paste_insert=Coller ins\u00E9rer
 path=Chemin \:
 path_extension_choice=Extension de chemin (utiliser ";" comme separateur)
 path_extension_dont_use_equals=Ne pas utiliser \u00E9gale dans l'extension de chemin (Compatibilit\u00E9 Intershop Enfinity)
 path_extension_dont_use_questionmark=Ne pas utiliser le point d'interrogation dans l'extension du chemin (Compatiblit\u00E9 Intershop Enfinity)
 patterns_to_exclude=URL \: motifs \u00E0 exclure
 patterns_to_include=URL \: motifs \u00E0 inclure
 pkcs12_desc=Clef PKCS 12 (*.p12)
 pl=Polonais
 poisson_timer_delay=D\u00E9lai de d\u00E9calage bas\u00E9 sur la loi de poisson (en millisecondes) \:
 poisson_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution de type Poisson
 poisson_timer_range=D\u00E9viation (en millisecondes) \:
 poisson_timer_title=Compteur de temps al\u00E9atoire selon la loi de Poisson 
 port=Port \:
 post_as_parameters=Param\u00E8tres
 post_body=Corps de la requ\u00EAte
 post_body_raw=Donn\u00E9es de la requ\u00EAte
 post_files_upload=T\u00E9l\u00E9chargement de fichiers
 post_thread_group_title=Groupe d'unit\u00E9s de fin
 previous=Pr\u00E9c\u00E9dent
 property_as_field_label={0}\:
 property_default_param=Valeur par d\u00E9faut
 property_edit=Editer
 property_editor.value_is_invalid_message=Le texte que vous venez d'entrer n'a pas une valeur valide pour cette propri\u00E9t\u00E9.\nLa propri\u00E9t\u00E9 va revenir \u00E0 sa valeur pr\u00E9c\u00E9dente.
 property_editor.value_is_invalid_title=Texte saisi invalide
 property_name_param=Nom de la propri\u00E9t\u00E9
 property_returnvalue_param=Revenir \u00E0 la valeur originale de la propri\u00E9t\u00E9 (d\u00E9faut non) ?
 property_tool_tip=<html>{0}</html>
 property_undefined=Non d\u00E9fini
 property_value_param=Valeur de propri\u00E9t\u00E9
 property_visualiser_title=Afficheur de propri\u00E9t\u00E9s
 protocol=Protocole [http] \:
 protocol_java_border=Classe Java
 protocol_java_classname=Nom de classe \:
 protocol_java_config_tile=Configurer \u00E9chantillon Java
 protocol_java_test_title=Test Java
 provider_url=Provider URL
 proxy_assertions=Ajouter une Assertion R\u00E9ponse
 proxy_cl_error=Si un serveur proxy est sp\u00E9cifi\u00E9, h\u00F4te et port doivent \u00EAtre donn\u00E9
 proxy_cl_wrong_target_cl=Le contr\u00F4leur cible est configur\u00E9 en mode "Utiliser un contr\u00F4leur enregistreur" \nmais aucun contr\u00F4leur de ce type n'existe, assurez vous de l'ajouter comme fils \nde Groupe d'unit\u00E9s afin de pouvoir d\u00E9marrer l'enregisteur
 proxy_content_type_exclude=Exclure \:
 proxy_content_type_filter=Filtre de type de contenu
 proxy_content_type_include=Inclure \:
 proxy_daemon_bind_error=Impossible de lancer le serveur proxy, le port est d\u00E9j\u00E0 utilis\u00E9. Choisissez un autre port.
 proxy_daemon_error=Impossible de lancer le serveur proxy, voir le journal pour plus de d\u00E9tails
 proxy_daemon_error_from_clipboard=depuis le presse-papier
 proxy_daemon_error_not_retrieve=Impossible d'ajouter
 proxy_daemon_error_read_args=Impossible de lire les arguments depuis le presse-papiers \:
 proxy_daemon_msg_check_details=Svp, v\u00E9rifier les d\u00E9tails ci-dessous lors de l'installation du certificat dans le navigateur
 proxy_daemon_msg_created_in_bin=cr\u00E9\u00E9 dans le r\u00E9pertoire bin de JMeter
 proxy_daemon_msg_install_as_in_doc=Vous pouvez l'installer en suivant les instructions de la documentation Component Reference (voir Installing the JMeter CA certificate for HTTPS recording paragraph)
 proxy_daemon_msg_rootca_cert=Certificat AC ra\u00E7ine \:
 proxy_domains=Domaines HTTPS \:
 proxy_domains_dynamic_mode_tooltip=Liste de noms de domaine pour les url HTTPS, ex. jmeter.apache.org ou les domaines wildcard comme *.apache.org. Utiliser la virgule comme s\u00E9parateur. 
 proxy_domains_dynamic_mode_tooltip_java6=Pour activer ce champ, utiliser un environnement d'ex\u00E9cution Java 7+
 proxy_general_settings=Param\u00E8tres g\u00E9n\u00E9raux
 proxy_headers=Capturer les ent\u00EAtes HTTP
 proxy_prefix_http_sampler_name=Pr\u00E9fixe \:
 proxy_regex=Correspondance des variables par regex ?
 proxy_sampler_settings=Param\u00E8tres Echantillon HTTP
 proxy_sampler_type=Type \:
 proxy_separators=Ajouter des s\u00E9parateurs
 proxy_settings_port_error_digits=Seuls les chiffres sont autoris\u00E9s.
 proxy_settings_port_error_invalid_data=Donn\u00E9es invalides
 proxy_target=Contr\u00F4leur Cible \:
 proxy_test_plan_content=Param\u00E8tres du plan de test
 proxy_title=Enregistreur script de test HTTP(S)
 pt_br=Portugais (Br\u00E9sil)
 ramp_up=Dur\u00E9e de mont\u00E9e en charge (en secondes) \:
 random_control_title=Contr\u00F4leur Al\u00E9atoire
 random_order_control_title=Contr\u00F4leur d'Ordre al\u00E9atoire
 random_multi_result_source_variable=Variable(s) source (separateur |)
 random_multi_result_target_variable=Variable cible
 random_string_chars_to_use=Caract\u00E8res \u00E0 utiliser pour la g\u00E9n\u00E9ration de la cha\u00EEne al\u00E9atoire
 random_string_length=Longueur de cha\u00EEne al\u00E9atoire
 realm=Univers (realm)
 record_controller_clear_samples=Supprimer tous les \u00E9chantillons
 record_controller_title=Contr\u00F4leur Enregistreur
 redo=R\u00E9tablir
 ref_name_field=Nom de r\u00E9f\u00E9rence \:
 regex_extractor_title=Extracteur Expression r\u00E9guli\u00E8re
 regex_field=Expression r\u00E9guli\u00E8re \:
 regex_params_names_field=Num\u00E9ro du groupe de la Regex pour les noms des param\u00E8tres
 regex_params_ref_name_field=Nom de la r\u00E9f\u00E9rence de la Regex
 regex_params_title=Param\u00E8tres utilisateurs bas\u00E9s sur RegEx
 regex_params_values_field=Num\u00E9ro du groupe de la Regex pour les valeurs des param\u00E8tres
 regex_source=Port\u00E9e
 regex_src_body=Corps
 regex_src_body_as_document=Corps en tant que Document
 regex_src_body_unescaped=Corps (non \u00E9chapp\u00E9)
 regex_src_hdrs=Ent\u00EAtes (R\u00E9ponse)
 regex_src_hdrs_req=Ent\u00EAtes (Requ\u00EAte)
 regex_src_url=URL
 regexfunc_param_1=Expression r\u00E9guli\u00E8re utilis\u00E9e pour chercher les r\u00E9sultats de la requ\u00EAte pr\u00E9c\u00E9dente.
 regexfunc_param_2=Canevas pour la ch\u00EEne de caract\u00E8re de remplacement, utilisant des groupes d'expressions r\u00E9guli\u00E8res. Le format est  $[group]$.  Exemple $1$.
 regexfunc_param_3=Quelle correspondance utiliser. Un entier 1 ou plus grand, RAND pour indiquer que JMeter doit choisir al\u00E9atoirement , A d\u00E9cimal, ou ALL indique que toutes les correspondances doivent \u00EAtre utilis\u00E9es
 regexfunc_param_4=Entre le texte. Si ALL est s\u00E9lectionn\u00E9, l'entre-texte sera utilis\u00E9 pour g\u00E9n\u00E9rer les r\u00E9sultats ([""])
 regexfunc_param_5=Text par d\u00E9faut. Utilis\u00E9 \u00E0 la place du canevas si l'expression r\u00E9guli\u00E8re ne trouve pas de correspondance
 regexfunc_param_7=Variable en entr\u221A\u00A9e contenant le texte \u221A\u2020 parser ([\u221A\u00A9chantillon pr\u221A\u00A9c\u221A\u00A9dent])
 regexp_render_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 regexp_tester_button_test=Tester
 regexp_tester_field=Expression r\u00E9guli\u00E8re \:
 regexp_tester_title=Testeur de RegExp
 remote_error_init=Erreur lors de l'initialisation du serveur distant
 remote_error_starting=Erreur lors du d\u221A\u00A9marrage du serveur distant
 remote_exit=Sortie distante
 remote_exit_all=Sortie distante de tous
 remote_shut=Extinction \u00E0 distance
 remote_shut_all=Extinction \u00E0 distance de tous
 remote_start=D\u00E9marrage distant
 remote_start_all=D\u00E9marrage distant de tous
 remote_stop=Arr\u00EAt distant
 remote_stop_all=Arr\u00EAt distant de tous
 remove=Supprimer
 remove_confirm_msg=Etes-vous s\u00FBr de vouloir supprimer ce(s) \u00E9l\u00E9ment(s) ?
 remove_confirm_title=Confirmer la suppression ?
 rename=Renommer une entr\u00E9e
 report=Rapport
 report_bar_chart=Graphique \u221A\u2020 barres
 report_bar_graph_url=URL
 report_base_directory=R\u221A\u00A9pertoire de Base
 report_chart_caption=L\u221A\u00A9gende du graph
 report_chart_x_axis=Axe X
 report_chart_x_axis_label=Libell\u221A\u00A9 de l'Axe X
 report_chart_y_axis=Axe Y
 report_chart_y_axis_label=Libell\u221A\u00A9 de l'Axe Y
 report_line_graph=Graphique Lin\u221A\u00A9aire
 report_line_graph_urls=Inclure les URLs
 report_output_directory=R\u221A\u00A9pertoire de sortie du rapport
 report_page=Page de Rapport
 report_page_element=Page Element
 report_page_footer=Pied de page
 report_page_header=Ent\u221A\u2122te de Page
 report_page_index=Cr\u221A\u00A9er la Page d'Index
 report_page_intro=Page d'Introduction
 report_page_style_url=Url de la feuille de style
 report_page_title=Titre de la Page
 report_pie_chart=Camembert
 report_plan=Plan du rapport
 report_select=Selectionner
 report_summary=Rapport r\u221A\u00A9sum\u221A\u00A9
 report_table=Table du Rapport
 report_writer=R\u221A\u00A9dacteur du Rapport
 report_writer_html=R\u221A\u00A9dacteur de rapport HTML
 reportgenerator_summary_apdex_apdex=Apdex
 reportgenerator_summary_apdex_samplers=Libell\u00E9
 reportgenerator_summary_apdex_satisfied=T (Seuil de tol\u00E9rance)
 reportgenerator_summary_apdex_tolerated=F (Seuil de frustration)
 reportgenerator_summary_errors_count=Nombre d'erreurs
 reportgenerator_summary_errors_rate_all=% de tous les \u00E9chantillons
 reportgenerator_summary_errors_rate_error=% des erreurs
 reportgenerator_summary_errors_type=Type d'erreur
 reportgenerator_summary_statistics_count=\#Echantillons
 reportgenerator_summary_statistics_error_count=KO
 reportgenerator_summary_statistics_error_percent=% Erreur
 reportgenerator_summary_statistics_kbytes=Ko/sec
 reportgenerator_summary_statistics_label=Libell\u00E9
 reportgenerator_summary_statistics_max=Max
 reportgenerator_summary_statistics_mean=Temps moyen
 reportgenerator_summary_statistics_min=Min
 reportgenerator_summary_statistics_percentile_fmt=%d%% centile
 reportgenerator_summary_statistics_throughput=D\u00E9bit
 reportgenerator_summary_total=Total
 request_data=Donn\u00E9e requ\u00EAte
 reset=R\u00E9initialiser
 reset_gui=R\u00E9initialiser l'\u00E9l\u00E9ment
 response_save_as_md5=R\u00E9ponse en empreinte MD5
 response_time_distribution_satisfied_label=Requ\u00EAtes \\ntemps de r\u00E9ponse <= {0}ms
 response_time_distribution_tolerated_label=Requ\u00EAtes \\ntemps de r\u00E9ponse > {0}ms et <= {1}ms
 response_time_distribution_untolerated_label=Requ\u00EAtes \\ntemps de r\u00E9ponse > {0}ms
 response_time_distribution_failed_label=Requ\u00EAtes en erreur
 restart=Red\u00E9marrer
 resultaction_title=Op\u00E9rateur R\u00E9sultats Action
 resultsaver_addtimestamp=Ajouter un timestamp
 resultsaver_errors=Enregistrer seulement les r\u00E9ponses en \u00E9checs
 resultsaver_numberpadlen=Taille minimale du num\u00E9ro de s\u00E9quence
 resultsaver_prefix=Pr\u00E9fixe du nom de fichier \: 
 resultsaver_skipautonumber=Ne pas ajouter de nombre au pr\u00E9fixe
 resultsaver_skipsuffix=Ne pas ajouter de suffixe
 resultsaver_success=Enregistrer seulement les r\u00E9ponses en succ\u00E8s
 resultsaver_title=Sauvegarder les r\u00E9ponses vers un fichier
 resultsaver_variable=Nom de variable \:
 retobj=Retourner les objets
 return_code_config_box_title=Configuration du code retour
 reuseconnection=R\u00E9-utiliser la connexion
 revert_project=Annuler les changements
 revert_project?=Annuler les changements sur le projet ?
 root=Racine
 root_title=Racine
 run=Lancer
 run_threadgroup=Lancer
 run_threadgroup_no_timers=Lancer sans pauses
 running_test=Lancer test
 runtime_controller_title=Contr\u00F4leur Dur\u00E9e d'ex\u00E9cution
 runtime_seconds=Temps d'ex\u00E9cution (secondes) \:
 sample_result_save_configuration=Sauvegarder la configuration de la sauvegarde des \u00E9chantillons
 sample_scope=Appliquer sur
 sample_scope_all=L'\u00E9chantillon et ses ressources li\u00E9es
 sample_scope_children=Les ressources li\u00E9es
 sample_scope_parent=L'\u00E9chantillon
 sample_scope_variable=Une variable \:
 sample_timeout_memo=Interrompre l'\u00E9chantillon si le d\u00E9lai est d\u00E9pass\u00E9
 sample_timeout_timeout=D\u00E9lai d'attente avant interruption (en millisecondes) \: 
 sample_timeout_title=Compteur Interruption
 sampler_label=Libell\u00E9
 sampler_on_error_action=Action \u00E0 suivre apr\u00E8s une erreur d'\u00E9chantillon
 sampler_on_error_continue=Continuer
 sampler_on_error_start_next_loop=D\u00E9marrer it\u00E9ration suivante
 sampler_on_error_stop_test=Arr\u00EAter le test
 sampler_on_error_stop_test_now=Arr\u00EAter le test imm\u00E9diatement
 sampler_on_error_stop_thread=Arr\u00EAter l'unit\u00E9
 save=Enregistrer le plan de test
 save?=Enregistrer ?
 save_all_as=Enregistrer le plan de test sous...
 save_as=Enregistrer sous...
 save_as_error=Au moins un \u00E9l\u00E9ment doit \u00EAtre s\u00E9lectionn\u00E9 \!
 save_as_image=Enregistrer en tant qu'image sous...
 save_as_image_all=Enregistrer l'\u00E9cran en tant qu'image...
 save_as_test_fragment=Enregistrer comme Fragment de Test
 save_as_test_fragment_error=Au moins un \u00E9l\u00E9ment ne peut pas \u00EAtre plac\u00E9 sous un Fragment de Test
 save_assertionresultsfailuremessage=Messages d'erreur des assertions
 save_assertions=R\u00E9sultats des assertions (XML)
 save_asxml=Enregistrer au format XML
 save_bytes=Nombre d'octets
 save_code=Code de r\u00E9ponse HTTP
 save_connecttime=Temps \u00E9tablissement connexion
 save_datatype=Type de donn\u00E9es
 save_encoding=Encodage
 save_fieldnames=Libell\u00E9 des colonnes (CSV)
 save_filename=Nom de fichier de r\u00E9ponse
 save_graphics=Enregistrer le graphique
 save_hostname=Nom d'h\u00F4te
 save_idletime=Temps d'inactivit\u00E9
 save_label=Libell\u00E9
 save_latency=Latence
 save_message=Message de r\u00E9ponse
 save_overwrite_existing_file=Le fichier s\u00E9lectionn\u00E9 existe d\u00E9j\u00E0, voulez-vous l'\u00E9craser ?
 save_requestheaders=Ent\u00EAtes de requ\u00EAte (XML)
 save_responsedata=Donn\u00E9es de r\u00E9ponse (XML)
 save_responseheaders=Ent\u00EAtes de r\u00E9ponse (XML)
 save_samplecount=Nombre d'\u00E9chantillon et d'erreur
 save_samplerdata=Donn\u00E9es d'\u00E9chantillon (XML)
+save_sentbytes=Nombre d'octets envoy\u00E9s
 save_subresults=Sous r\u00E9sultats (XML)
 save_success=Succ\u00E8s
 save_threadcounts=Nombre d'unit\u00E9s actives
 save_threadname=Nom d'unit\u00E9
 save_time=Temps \u00E9coul\u00E9
 save_timestamp=Horodatage
 save_url=URL
 save_workbench=Sauvegarder le plan de travail
 sbind=Simple connexion/d\u00E9connexion
 scheduler=Programmateur de d\u00E9marrage
 scheduler_configuration=Configuration du programmateur
 scope=Port\u00E9e
 search=Rechercher
 search_base=Base de recherche
 search_expand=Rechercher & D\u00E9plier
 search_filter=Filtre de recherche
 search_test=Recherche
 search_text_button_close=Fermer
 search_text_button_find=Rechercher
 search_text_button_next=Suivant
 search_text_chkbox_case=Consid\u00E9rer la casse
 search_text_chkbox_regexp=Exp. reguli\u00E8re
 search_text_field=Rechercher \:
 search_text_msg_not_found=Texte non trouv\u00E9
 search_text_title_not_found=Pas trouv\u00E9
 search_tree_title=Rechercher dans l'arbre
 searchbase=Base de recherche
 searchfilter=Filtre de recherche
 searchtest=Recherche
 second=seconde
 secure=S\u00E9curis\u00E9 \:
 send_file=Envoyer un fichier avec la requ\u00EAte \:
 send_file_browse=Parcourir...
 send_file_filename_label=Chemin du fichier
 send_file_mime_label=Type MIME
 send_file_param_name_label=Nom du param\u00E8tre
 server=Nom ou IP du serveur \:
 servername=Nom du serveur \:
 session_argument_name=Nom des arguments de la session
 setup_thread_group_title=Groupe d'unit\u00E9s de d\u00E9but
 should_save=Vous devez enregistrer le plan de test avant de le lancer.  \nSi vous utilisez des fichiers de donn\u00E9es (i.e. Source de donn\u00E9es CSV ou la fonction _StringFromFile), \nalors c'est particuli\u00E8rement important d'enregistrer d'abord votre script de test. \nVoulez-vous enregistrer maintenant votre plan de test ?
 shutdown=Eteindre
 simple_config_element=Configuration Simple
 simple_data_writer_title=Enregistreur de donn\u00E9es
 size_assertion_comparator_error_equal=est \u00E9gale \u00E0
 size_assertion_comparator_error_greater=est plus grand que
 size_assertion_comparator_error_greaterequal=est plus grand ou \u00E9gale \u00E0
 size_assertion_comparator_error_less=est inf\u00E9rieur \u00E0
 size_assertion_comparator_error_lessequal=est inf\u00E9rieur ou \u00E9gale \u00E0
 size_assertion_comparator_error_notequal=n'est pas \u00E9gale \u00E0
 size_assertion_comparator_label=Type de comparaison
 size_assertion_failure=Le r\u00E9sultat n''a pas la bonne taille \: il \u00E9tait de {0} octet(s), mais aurait d\u00FB \u00EAtre de {1} {2} octet(s).
 size_assertion_input_error=Entrer un entier positif valide svp.
 size_assertion_label=Taille en octets \:
 size_assertion_size_test=Taille \u00E0 v\u00E9rifier
 size_assertion_title=Assertion Taille
 smime_assertion_issuer_dn=Nom unique de l'\u00E9metteur \: 
 smime_assertion_message_position=V\u00E9rifier l'assertion sur le message \u00E0 partir de la position
 smime_assertion_not_signed=Message non sign\u00E9
 smime_assertion_signature=Signature
 smime_assertion_signer=Certificat signataire
 smime_assertion_signer_by_file=Fichier du certificat \: 
 smime_assertion_signer_constraints=V\u00E9rifier les valeurs \:
 smime_assertion_signer_dn=Nom unique du signataire \: 
 smime_assertion_signer_email=Adresse courriel du signataire \: 
 smime_assertion_signer_no_check=Pas de v\u00E9rification
 smime_assertion_signer_serial=Num\u00E9ro de s\u00E9rie \: 
 smime_assertion_title=Assertion SMIME
 smime_assertion_verify_signature=V\u00E9rifier la signature
 smtp_additional_settings=Param\u00E8tres suppl\u00E9mentaires
 smtp_attach_file=Fichier(s) attach\u00E9(s) \:
 smtp_attach_file_tooltip=S\u00E9parer les fichiers par le point-virgule ";"
 smtp_auth_settings=Param\u00E8tres d'authentification
 smtp_bcc=Adresse en copie cach\u00E9e (Bcc) \:
 smtp_cc=Adresse en copie (CC) \:
 smtp_default_port=(D\u00E9fauts \: SMTP \: 25, SSL \: 465, StartTLS \: 587)
 smtp_eml=Envoyer un message .eml \:
 smtp_enabledebug=Activer les traces de d\u00E9bogage ?
 smtp_enforcestarttls=Forcer le StartTLS
 smtp_enforcestarttls_tooltip=<html><b>Force</b> le serveur a utiliser StartTLS.<br />Si il n'est pas s\u00E9lectionn\u00E9 et que le serveur SMTP ne supporte pas StartTLS, <br />une connexion SMTP normale sera utilis\u00E9e \u00E0 la place. <br /><i>Merci de noter</i> que la case \u00E0 cocher cr\u00E9\u00E9e un fichier dans /tmp/, <br />donc cela peut poser des probl\u00E8mes sous Windows.</html>
 smtp_from=Adresse exp\u00E9diteur (From) \:
 smtp_header_add=Ajouter une ent\u00EAte
 smtp_header_name=Nom d'ent\u00EAte
 smtp_header_remove=Supprimer
 smtp_header_value=Valeur d'ent\u00EAte
 smtp_mail_settings=Param\u00E8tres du courriel
 smtp_message=Message \:
 smtp_message_settings=Param\u00E8tres du message
 smtp_messagesize=Calculer la taille du message
 smtp_password=Mot de passe \:
 smtp_plainbody=Envoyer le message en texte (i.e. sans multipart/mixed)
 smtp_replyto=Adresse de r\u00E9ponse (Reply-To) \:
 smtp_sampler_title=Requ\u00EAte SMTP
 smtp_security_settings=Param\u00E8tres de s\u00E9curit\u00E9
 smtp_server=Serveur \:
 smtp_server_connection_timeout=D\u00E9lai d'attente de connexion \:
 smtp_server_port=Port \:
 smtp_server_settings=Param\u00E8tres du serveur
 smtp_server_timeout=D\u00E9lai d'attente de r\u00E9ponse \:
 smtp_server_timeouts_settings=D\u00E9lais d'attente (milli-secondes)
 smtp_subject=Sujet \:
 smtp_suppresssubj=Supprimer l'ent\u00EAte Sujet (Subject)
 smtp_timestamp=Ajouter un horodatage dans le sujet
 smtp_to=Adresse destinataire (To) \:
 smtp_trustall=Faire confiance \u00E0 tous les certificats
 smtp_trustall_tooltip=<html><b>Forcer</b> JMeter \u00E0 faire confiance \u00E0 tous les certificats, quelque soit l'autorit\u00E9 de certification du certificat.</html>
 smtp_truststore=Coffre de cl\u00E9s local \:
 smtp_truststore_tooltip=<html>Le chemin du coffre de confiance.<br />Les chemins relatifs sont d\u00E9termin\u00E9s \u00E0 partir du r\u00E9pertoire courant.<br />En cas d'\u00E9chec, c'est le r\u00E9pertoire contenant le script JMX qui est utilis\u00E9.</html>
 smtp_useauth=Utiliser l'authentification
 smtp_usenone=Pas de fonctionnalit\u00E9 de s\u00E9curit\u00E9
 smtp_username=Identifiant \:
 smtp_usessl=Utiliser SSL
 smtp_usestarttls=Utiliser StartTLS
 smtp_usetruststore=Utiliser le coffre de confiance local
 smtp_usetruststore_tooltip=<html>Autoriser JMeter \u00E0 utiliser le coffre de confiance local.</html>
 soap_action=Action Soap
 soap_data_title=Donn\u00E9es Soap/XML-RPC
 soap_sampler_file_invalid=Le nom de fichier r\u00E9f\u00E9rence un fichier absent ou sans droits de lecture\:
 soap_sampler_title=Requ\u00EAte SOAP/XML-RPC
 soap_send_action=Envoyer l'action SOAP \:
 solinger=SO_LINGER\:
 spline_visualizer_average=Moyenne \:
 spline_visualizer_incoming=Entr\u00E9e \:
 spline_visualizer_maximum=Maximum \:
 spline_visualizer_minimum=Minimum \:
 spline_visualizer_title=Moniteur de courbe (spline)  (DEPRECATED)
 spline_visualizer_waitingmessage=En attente de r\u00E9sultats d'\u00E9chantillons
 split_function_separator=S\u00E9parateur utilis\u00E9 pour scinder le texte. Par d\u00E9faut , (virgule) est utilis\u00E9.
 split_function_string=Texte \u00E0 scinder
 ssl_alias_prompt=Veuillez entrer votre alias pr\u00E9f\u00E9r\u00E9
 ssl_alias_select=S\u00E9lectionner votre alias pour le test
 ssl_alias_title=Alias du client
 ssl_error_title=Probl\u00E8me de KeyStore
 ssl_pass_prompt=Entrer votre mot de passe
 ssl_pass_title=Mot de passe KeyStore
 ssl_port=Port SSL
 sslmanager=Gestionnaire SSL
 start=Lancer
 start_no_timers=Lancer sans pauses
 starttime=Date et heure de d\u00E9marrage \:
 stop=Arr\u00EAter
 stopping_test=Arr\u00EAt de toutes les unit\u00E9s de tests en cours. Le nombre d'unit\u00E9s actives est visible dans le coin haut droit de l'interface. Soyez patient, merci. 
 stopping_test_failed=Au moins une unit\u00E9 non arr\u00EAt\u00E9e; voir le journal.
 stopping_test_host=H\u00F4te
 stopping_test_title=En train d'arr\u00EAter le test
 string_from_file_encoding=Encodage du fichier (optionnel)
 string_from_file_file_name=Entrer le chemin (absolu ou relatif) du fichier
 string_from_file_seq_final=Nombre final de s\u00E9quence de fichier
 string_from_file_seq_start=D\u00E9marer le nombre de s\u00E9quence de fichier
 summariser_title=G\u00E9n\u00E9rer les resultats consolid\u00E9s
 summary_report=Rapport consolid\u00E9
 switch_controller_label=Aller vers le num\u00E9ro d'\u00E9l\u00E9ment (ou nom) subordonn\u00E9 \:
 switch_controller_title=Contr\u00F4leur Aller \u00E0
 system_sampler_stderr=Erreur standard (stderr) \:
 system_sampler_stdin=Entr\u00E9e standard (stdin) \:
 system_sampler_stdout=Sortie standard (stdout) \:
 system_sampler_title=Appel de processus syst\u00E8me
 table_visualizer_bytes=Octets
 table_visualizer_connect=\u00C9tabl. Conn.(ms)
 table_visualizer_latency=Latence
 table_visualizer_sample_num=Echantillon \#
 table_visualizer_sample_time=Temps (ms)
+table_visualizer_sent_bytes=Octets envoy\u00E9s
 table_visualizer_start_time=Heure d\u00E9but
 table_visualizer_status=Statut
 table_visualizer_success=Succ\u00E8s
 table_visualizer_thread_name=Nom d'unit\u00E9
 table_visualizer_warning=Alerte
 target_server=Serveur cible
 tcp_classname=Nom de classe TCPClient \:
 tcp_config_title=Param\u00E8tres TCP par d\u00E9faut
 tcp_nodelay=D\u00E9finir aucun d\u00E9lai (NoDelay)
 tcp_port=Num\u00E9ro de port \:
 tcp_request_data=Texte \u00E0 envoyer \:
 tcp_sample_title=Requ\u00EAte TCP
 tcp_timeout=Expiration (millisecondes) \:
 teardown_on_shutdown=Ex\u00E9cuter le Groupe d'unit\u00E9s de fin m\u00EAme apr\u00E8s un arr\u00EAt manuel des Groupes d'unit\u00E9s principaux
 template_choose=Choisir le mod\u00E8le
 template_create_from=Cr\u00E9er
 template_field=Canevas \:
 template_load?=Charger le mod\u00E8le ?
 template_menu=Mod\u00E8les...
 template_merge_from=Fusionner
 template_reload=Recharger les mod\u00E8les
 template_title=Mod\u00E8les
 test=Test
 test_action_action=Action \:
 test_action_duration=Dur\u00E9e (millisecondes) \:
 test_action_pause=Mettre en pause
 test_action_restart_next_loop=Passer \u00E0 l'it\u00E9ration suivante de la boucle
 test_action_stop=Arr\u00EAter
 test_action_stop_now=Arr\u00EAter imm\u00E9diatement
 test_action_target=Cible \:
 test_action_target_test=Toutes les unit\u00E9s
 test_action_target_thread=Unit\u00E9 courante
 test_action_title=Action test
 test_configuration=Type de test
 test_fragment_title=Fragment d'\u00E9l\u00E9ments
 test_plan=Plan de test
 test_plan_classpath_browse=Ajouter un r\u00E9pertoire ou un fichier 'jar' au 'classpath'
 testconfiguration=Tester la configuration
 testplan.serialized=Lancer les groupes d'unit\u00E9s en s\u00E9rie (c'est-\u00E0-dire \: lance un groupe \u00E0 la fois)
 testplan_comments=Commentaires \:
 testt=Test
 textbox_cancel=Annuler
 textbox_close=Fermer
 textbox_save_close=Enregistrer & Fermer
 textbox_title_edit=Editer texte
 textbox_title_view=Voir texte
 textbox_tooltip_cell=Double clic pour voir/editer
 thread_delay_properties=Propri\u00E9t\u00E9s de temporisation de l'unit\u00E9
 thread_group_title=Groupe d'unit\u00E9s
 thread_properties=Propri\u00E9t\u00E9s du groupe d'unit\u00E9s
 threadgroup=Groupe d'unit\u00E9s
 throughput_control_bynumber_label=Ex\u00E9cutions totales
 throughput_control_bypercent_label=Pourcentage d'ex\u00E9cution
 throughput_control_perthread_label=Par utilisateur
 throughput_control_title=Contr\u00F4leur D\u00E9bit
 throughput_control_tplabel=D\u00E9bit \:
 time_format=Chaine de formatage sur le mod\u00E8le SimpleDateFormat (optionnel)
 timelim=Limiter le temps de r\u00E9ponses \u00E0 (ms)
 timeout_config_box_title=Configuration du d\u00E9lai d'expiration
 timeout_title=D\u00E9lai expiration (ms)
 toggle=Permuter
 toolbar_icon_set_not_found=Le fichier de description des ic\u00F4nes de la barre d'outils n'est pas trouv\u00E9. Voir les journaux.
 total_threads_tooltip=Nombre total d'Unit\u00E9s \u00E0 lancer
 tr=Turc
 transaction_controller_include_timers=Inclure la dur\u00E9e des compteurs de temps et pre/post processeurs dans le calcul du temps
 transaction_controller_parent=G\u00E9n\u00E9rer en \u00E9chantillon parent
 transaction_controller_title=Contr\u00F4leur Transaction
 transform_into_variable=Remplacer les valeurs par des variables
 unbind=D\u00E9connexion de l'unit\u00E9
 undo=Annuler
 unescape_html_string=Cha\u00EEne \u00E0 \u00E9chapper
 unescape_string=Cha\u00EEne de caract\u00E8res contenant des\u00E9chappements Java
 uniform_timer_delay=D\u00E9lai de d\u00E9calage constant (en millisecondes) \:
 uniform_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution uniforme
 uniform_timer_range=D\u00E9viation al\u00E9atoire maximum (en millisecondes) \:
 uniform_timer_title=Compteur de temps al\u00E9atoire uniforme
 up=Monter
 update=Mettre \u00E0 jour
 update_per_iter=Mettre \u00E0 jour une fois par it\u00E9ration
 upload=Fichier \u00E0 uploader
 upper_bound=Borne sup\u00E9rieure
 url=URL
 url_config_get=GET
 url_config_http=HTTP
 url_config_https=HTTPS
 url_config_post=POST
 url_config_protocol=Protocole \:
 url_config_title=Param\u00E8tres HTTP par d\u00E9faut
 url_full_config_title=Echantillon d'URL complet
 url_multipart_config_title=Requ\u00EAte HTTP Multipart par d\u00E9faut
 urldecode_string=Cha\u00EEne de style URL \u00E0 d\u00E9coder
 urlencode_string=Cha\u00EEne de caract\u00E8res \u00E0 encoder en style URL
 use_custom_dns_resolver=Utiliser un r\u00E9solveur DNS personnalis\u00E9
 use_expires=Utiliser les ent\u00EAtes Cache-Control/Expires lors du traitement des requ\u00EAtes GET
 use_keepalive=Connexion persist.
 use_multipart_for_http_post=Multipart/form-data
 use_multipart_mode_browser=Ent\u00EAtes compat. navigateur
 use_recording_controller=Utiliser un contr\u00F4leur enregistreur
 use_system_dns_resolver=Utiliser le r\u00E9solveur DNS syst\u00E8me (JVM)
 user=Utilisateur
 user_defined_test=Test d\u00E9fini par l'utilisateur
 user_defined_variables=Variables pr\u00E9-d\u00E9finies
 user_param_mod_help_note=(Ne pas changer. A la place, modifier le fichier de ce nom dans le r\u00E9pertoire /bin de JMeter)
 user_parameters_table=Param\u00E8tres
 user_parameters_title=Param\u00E8tres Utilisateur
 userdn=Identifiant
 username=Nom d'utilisateur \:
 userpw=Mot de passe
 validate_threadgroup=Valider
 value=Valeur \:
 value_to_quote_meta=Valeur \u00E0 \u00E9chapper des caract\u00E8res sp\u00E9ciaux utilis\u00E8s par ORO Regexp
 var_name=Nom de r\u00E9f\u00E9rence \:
 variable_name_param=Nom de variable (peut inclure une r\u00E9f\u00E9rence de variable ou fonction)
 view_graph_tree_title=Voir le graphique en arbre
 view_results_assertion_error=Erreur d'assertion \: 
 view_results_assertion_failure=Echec d'assertion \: 
 view_results_assertion_failure_message=Message d'\u00E9chec d'assertion \: 
 view_results_autoscroll=D\u00E9filement automatique ?
 view_results_childsamples=Echantillons enfants?
 view_results_connect_time=Temps \u00E9tablissement connexion \: 
 view_results_datatype=Type de donn\u00E9es ("text"|"bin"|"")\: 
 view_results_desc=Affiche les r\u00E9sultats d'un \u00E9chantillon dans un arbre de r\u00E9sultats
 view_results_error_count=Compteur erreur\: 
 view_results_fields=champs \:
 view_results_in_table=Tableau de r\u00E9sultats
 view_results_latency=Latence \: 
 view_results_load_time=Temps de r\u00E9ponse \: 
 view_results_render=Rendu \: 
 view_results_render_document=Document
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML et ressources
 view_results_render_html_formatted=Code source HTML Format\u00E9
 view_results_render_json=JSON
 view_results_render_text=Texte brut
 view_results_render_xml=XML
 view_results_request_headers=Ent\u00EAtes de requ\u00EAte \:
 view_results_response_code=Code de retour \: 
 view_results_response_headers=Ent\u00EAtes de r\u00E9ponse \:
 view_results_response_message=Message de retour \: 
 view_results_response_missing_tika=Manque l'archive tika-app.jar dans le classpath. Impossible de convertir en texte ce type de document.\nT\u00E9l\u00E9charger le fichier tika-app-x.x.jar depuis http\://tika.apache.org/download.html\nPuis ajouter ce fichier dans le r\u00E9pertoire <JMeter>/lib
 view_results_response_partial_message=D\u00E9but du message\:
 view_results_response_too_large_message=R\u00E9ponse d\u00E9passant la taille maximale d'affichage. Taille \: 
 view_results_sample_count=Compteur \u00E9chantillon \: 
 view_results_sample_start=Date d\u00E9but \u00E9chantillon \: 
 view_results_search_pane=Volet recherche 
+view_results_sent_bytes=Octets envoy\u00E9s:
 view_results_size_body_in_bytes=Taille du corps en octets \: 
 view_results_size_headers_in_bytes=Taille de l'ent\u00EAte en octets \: 
 view_results_size_in_bytes=Taille en octets \: 
 view_results_tab_assertion=R\u00E9sultats d'assertion
 view_results_tab_request=Requ\u00EAte
 view_results_tab_response=Donn\u00E9es de r\u00E9ponse
 view_results_tab_sampler=R\u00E9sultat de l'\u00E9chantillon
 view_results_table_fields_key=Champ suppl\u00E9mentaire
 view_results_table_fields_value=Valeur
 view_results_table_headers_key=Ent\u00EAtes de r\u00E9ponse
 view_results_table_headers_value=Valeur
 view_results_table_request_headers_key=Ent\u00EAtes de requ\u00EAte
 view_results_table_request_headers_value=Valeur
 view_results_table_request_http_cookie=Cookie
 view_results_table_request_http_host=H\u00F4te
 view_results_table_request_http_method=M\u00E9thode
 view_results_table_request_http_nohttp=N'est pas un \u00E9chantillon HTTP
 view_results_table_request_http_path=Chemin
 view_results_table_request_http_port=Port
 view_results_table_request_http_protocol=Protocole
 view_results_table_request_params_key=Nom de param\u00E8tre
 view_results_table_request_params_value=Valeur
 view_results_table_request_raw_nodata=Pas de donn\u00E9es \u00E0 afficher
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=Brut
 view_results_table_result_tab_parsed=D\u00E9cod\u00E9
 view_results_table_result_tab_raw=Brut
 view_results_thread_name=Nom d'unit\u00E9 \: 
 view_results_title=Voir les r\u00E9sultats
 view_results_tree_title=Arbre de r\u00E9sultats
 warning=Attention \!
 web_cannot_convert_parameters_to_raw=Ne peut pas convertir les param\u00E8tres en Donn\u00E9es POST brutes\ncar l'un des param\u00E8tres a un nom.
 web_cannot_switch_tab=Vous ne pouvez pas basculer car ces donn\u00E9es ne peuvent \u00EAtre converties.\nVider les donn\u00E9es pour basculer.
 web_parameters_lost_message=Basculer vers les Donn\u00E9es POST brutes va convertir en format brut\net perdre le format tabulaire quand vous s\u00E9lectionnerez un autre noeud\nou \u00E0 la sauvegarde du plan de test, \u00EAtes-vous s\u00FBr ?
 web_proxy_server_title=Requ\u00EAte via un serveur proxy
 web_request=Requ\u00EAte HTTP
 web_server=Serveur web
 web_server_client=Impl\u00E9mentation client \:
 web_server_domain=Nom ou adresse IP \:
 web_server_port=Port \:
 web_server_timeout_connect=Connexion \:
 web_server_timeout_response=R\u00E9ponse \:
 web_server_timeout_title=D\u00E9lai expiration (ms)
 web_testing2_title=Requ\u00EAte HTTP HTTPClient
 web_testing_advanced=Avanc\u00E9e
 web_testing_basic=Basique
 web_testing_concurrent_download=T\u00E9l\u00E9chargements en parall\u00E8le. Nombre \:
 web_testing_embedded_url_pattern=Les URL \u00E0 inclure doivent correspondre \u00E0 \:
 web_testing_retrieve_images=R\u00E9cup\u00E9rer les ressources incluses
 web_testing_retrieve_title=Ressources incluses dans les pages HTML
 web_testing_source_ip=Adresse source
 web_testing_source_ip_device=Interface
 web_testing_source_ip_device_ipv4=Interface IPv4
 web_testing_source_ip_device_ipv6=Interface IPv6
 web_testing_source_ip_hostname=IP/Nom d'h\u00F4te
 web_testing_title=Requ\u00EAte HTTP
 while_controller_label=Condition (fonction ou variable) \:
 while_controller_title=Contr\u00F4leur Tant Que
 workbench_title=Plan de travail
 xml_assertion_title=Assertion XML
 xml_download_dtds=R\u00E9cup\u00E9rer les DTD externes
 xml_namespace_button=Utiliser les espaces de noms
 xml_tolerant_button=Utiliser Tidy (analyseur tol\u00E9rant)
 xml_validate_button=Validation XML
 xml_whitespace_button=Ignorer les espaces
 xmlschema_assertion_label=Nom de fichier \: 
 xmlschema_assertion_title=Assertion Sch\u00E9ma XML
 xpath_assertion_button=Valider
 xpath_assertion_check=V\u00E9rifier l'expression XPath
 xpath_assertion_error=Erreur avec XPath
 xpath_assertion_failed=Expression XPath invalide
 xpath_assertion_label=XPath
 xpath_assertion_negate=Vrai si aucune correspondance trouv\u00E9e
 xpath_assertion_option=Options d'analyse XML
 xpath_assertion_test=V\u00E9rificateur XPath
 xpath_assertion_tidy=Essayer et nettoyer l'entr\u00E9e
 xpath_assertion_title=Assertion XPath
 xpath_assertion_valid=Expression XPath valide
 xpath_assertion_validation=Valider le code XML \u00E0 travers le fichier DTD
 xpath_assertion_whitespace=Ignorer les espaces
 xpath_expression=Expression XPath de correspondance
 xpath_extractor_fragment=Retourner le fragment XPath entier au lieu du contenu
 xpath_extractor_query=Requ\u00EAte XPath \:
 xpath_extractor_title=Extracteur XPath
 xpath_file_file_name=Fichier XML contenant les valeurs
 xpath_tester=Testeur XPath
 xpath_tester_button_test=Tester
 xpath_tester_field=Expression XPath
 xpath_tester_fragment=Retourner le fragment XPath entier au lieu du contenu ?
 xpath_tester_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 xpath_tester_title=Testeur XPath
 xpath_tidy_quiet=Silencieux
 xpath_tidy_report_errors=Rapporter les erreurs
 xpath_tidy_show_warnings=Afficher les alertes
 you_must_enter_a_valid_number=Vous devez entrer un nombre valide
 zh_cn=Chinois (simplifi\u00E9)
 zh_tw=Chinois (traditionnel)
diff --git a/src/core/org/apache/jmeter/samplers/SampleResult.java b/src/core/org/apache/jmeter/samplers/SampleResult.java
index 9c321c9ba..dc1717ab3 100644
--- a/src/core/org/apache/jmeter/samplers/SampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/SampleResult.java
@@ -1,1448 +1,1467 @@
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
 import java.io.UnsupportedEncodingException;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.nio.charset.Charset;
 import java.nio.charset.StandardCharsets;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 // For unit tests, @see TestSampleResult
 
 /**
  * This is a nice packaging for the various information returned from taking a
  * sample of an entry.
  *
  */
 public class SampleResult implements Serializable, Cloneable, Searchable {
 
     private static final long serialVersionUID = 241L;
 
     // Needs to be accessible from Test code
     static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * The default encoding to be used if not overridden.
      * The value is ISO-8859-1.
      */
     public static final String DEFAULT_HTTP_ENCODING = StandardCharsets.ISO_8859_1.name();
 
     // Bug 33196 - encoding ISO-8859-1 is only suitable for Western countries
     // However the suggested System.getProperty("file.encoding") is Cp1252 on
     // Windows
     // So use a new property with the original value as default
     // needs to be accessible from test code
     /**
      * The default encoding to be used to decode the responseData byte array.
      * The value is defined by the property "sampleresult.default.encoding"
      * with a default of DEFAULT_HTTP_ENCODING if that is not defined.
      */
     protected static final String DEFAULT_ENCODING
             = JMeterUtils.getPropDefault("sampleresult.default.encoding", // $NON-NLS-1$
             DEFAULT_HTTP_ENCODING);
 
     /* The default used by {@link #setResponseData(String, String)} */
     private static final String DEFAULT_CHARSET = Charset.defaultCharset().name();
 
     /**
      * Data type value ({@value}) indicating that the response data is text.
      *
      * @see #getDataType
      * @see #setDataType(java.lang.String)
      */
     public static final String TEXT = "text"; // $NON-NLS-1$
 
     /**
      * Data type value ({@value}) indicating that the response data is binary.
      *
      * @see #getDataType
      * @see #setDataType(java.lang.String)
      */
     public static final String BINARY = "bin"; // $NON-NLS-1$
 
     /** empty array which can be returned instead of null */
     public static final byte[] EMPTY_BA = new byte[0];
 
     private static final SampleResult[] EMPTY_SR = new SampleResult[0];
 
     private static final AssertionResult[] EMPTY_AR = new AssertionResult[0];
     
     private static final boolean GETBYTES_BODY_REALSIZE = 
         JMeterUtils.getPropDefault("sampleresult.getbytes.body_real_size", true); // $NON-NLS-1$
 
     private static final boolean GETBYTES_HEADERS_SIZE = 
         JMeterUtils.getPropDefault("sampleresult.getbytes.headers_size", true); // $NON-NLS-1$
     
     private static final boolean GETBYTES_NETWORK_SIZE =
             GETBYTES_HEADERS_SIZE && GETBYTES_BODY_REALSIZE;
 
     private SampleSaveConfiguration saveConfig;
 
     private SampleResult parent = null;
 
     /**
      * @param propertiesToSave
      *            The propertiesToSave to set.
      */
     public void setSaveConfig(SampleSaveConfiguration propertiesToSave) {
         this.saveConfig = propertiesToSave;
     }
 
     public SampleSaveConfiguration getSaveConfig() {
         return saveConfig;
     }
 
     private byte[] responseData = EMPTY_BA;
 
     private String responseCode = "";// Never return null
 
     private String label = "";// Never return null
 
     /** Filename used by ResultSaver */
     private String resultFileName = "";
 
     /** The data used by the sampler */
     private String samplerData;
 
     private String threadName = ""; // Never return null
 
     private String responseMessage = "";
 
     private String responseHeaders = ""; // Never return null
 
     private String contentType = ""; // e.g. text/html; charset=utf-8
 
     private String requestHeaders = "";
 
     // TODO timeStamp == 0 means either not yet initialised or no stamp available (e.g. when loading a results file)
     /** the time stamp - can be start or end */
     private long timeStamp = 0;
 
     private long startTime = 0;
 
     private long endTime = 0;
 
     private long idleTime = 0;// Allow for non-sample time
 
     /** Start of pause (if any) */
     private long pauseTime = 0;
 
     private List<AssertionResult> assertionResults;
 
     private List<SampleResult> subResults;
 
     /**
      * The data type of the sample
      * @see #getDataType()
      * @see #setDataType(String)
      * @see #TEXT
      * @see #BINARY
      */
     private String dataType=""; // Don't return null if not set
 
     private boolean success;
 
     //@GuardedBy("this"")
     /** files that this sample has been saved in */
     /** In Non GUI mode and when best config is used, size never exceeds 1, 
      * but as a compromise set it to 3 
      */
     private final Set<String> files = new HashSet<>(3);
 
     private String dataEncoding;// (is this really the character set?) e.g.
                                 // ISO-8895-1, UTF-8
 
     /** elapsed time */
     private long elapsedTime = 0;
 
     /** time to first response */
     private long latency = 0;
 
     /**
      * time to end connecting
      */
     private long connectTime = 0;
 
     /** Should thread start next iteration ? */
     private boolean startNextThreadLoop = false;
 
     /** Should thread terminate? */
     private boolean stopThread = false;
 
     /** Should test terminate? */
     private boolean stopTest = false;
 
     /** Should test terminate abruptly? */
     private boolean stopTestNow = false;
 
     /** Is the sampler acting as a monitor? */
     private boolean isMonitor = false;
 
     private int sampleCount = 1;
 
     private int bytes = 0; // Allows override of sample size in case sampler does not want to store all the data
     
     private int headersSize = 0;
     
     private int bodySize = 0;
 
     /** Currently active threads in this thread group */
     private volatile int groupThreads = 0;
 
     /** Currently active threads in all thread groups */
     private volatile int allThreads = 0;
 
     // TODO do contentType and/or dataEncoding belong in HTTPSampleResult instead?
 
     private static final boolean startTimeStamp
         = JMeterUtils.getPropDefault("sampleresult.timestamp.start", false);  // $NON-NLS-1$
 
     // Allow read-only access from test code
     static final boolean USENANOTIME
     = JMeterUtils.getPropDefault("sampleresult.useNanoTime", true);  // $NON-NLS-1$
 
     // How long between checks of nanotime; default 5000ms; set to <=0 to disable the thread
     private static final long NANOTHREAD_SLEEP = 
             JMeterUtils.getPropDefault("sampleresult.nanoThreadSleep", 5000);  // $NON-NLS-1$;
 
     static {
         if (startTimeStamp) {
             log.info("Note: Sample TimeStamps are START times");
         } else {
             log.info("Note: Sample TimeStamps are END times");
         }
         log.info("sampleresult.default.encoding is set to " + DEFAULT_ENCODING);
         log.info("sampleresult.useNanoTime="+USENANOTIME);
         log.info("sampleresult.nanoThreadSleep="+NANOTHREAD_SLEEP);
 
         if (USENANOTIME && NANOTHREAD_SLEEP > 0) {
             // Make sure we start with a reasonable value
             NanoOffset.nanoOffset = System.currentTimeMillis() - SampleResult.sampleNsClockInMs();
             NanoOffset nanoOffset = new NanoOffset();
             nanoOffset.setDaemon(true);
             nanoOffset.setName("NanoOffset");
             nanoOffset.start();
         }
     }
 
 
     private final long nanoTimeOffset;
 
     // Allow testcode access to the settings
     final boolean useNanoTime;
     
     final long nanoThreadSleep;
     
     /**
      * Cache for responseData as string to avoid multiple computations
      */
     private volatile transient String responseDataAsString;
-    
+
+    private long sentBytes;
+
     private long initOffset(){
         if (useNanoTime){
             return nanoThreadSleep > 0 ? NanoOffset.getNanoOffset() : System.currentTimeMillis() - sampleNsClockInMs();
         } else {
             return Long.MIN_VALUE;
         }
     }
 
     public SampleResult() {
         this(USENANOTIME, NANOTHREAD_SLEEP);
     }
 
     // Allow test code to change the default useNanoTime setting
     SampleResult(boolean nanoTime) {
         this(nanoTime, NANOTHREAD_SLEEP);
     }
 
     // Allow test code to change the default useNanoTime and nanoThreadSleep settings
     SampleResult(boolean nanoTime, long nanoThreadSleep) {
         this.elapsedTime = 0;
         this.useNanoTime = nanoTime;
         this.nanoThreadSleep = nanoThreadSleep;
         this.nanoTimeOffset = initOffset();
     }
 
     /**
      * Copy constructor.
      * 
      * @param res existing sample result
      */
     public SampleResult(SampleResult res) {
         this();
         allThreads = res.allThreads;//OK
         assertionResults = res.assertionResults;// TODO ??
         bytes = res.bytes;
         headersSize = res.headersSize;
         bodySize = res.bodySize;
         contentType = res.contentType;//OK
         dataEncoding = res.dataEncoding;//OK
         dataType = res.dataType;//OK
         endTime = res.endTime;//OK
         // files is created automatically, and applies per instance
         groupThreads = res.groupThreads;//OK
         idleTime = res.idleTime;
         isMonitor = res.isMonitor;
         label = res.label;//OK
         latency = res.latency;
         connectTime = res.connectTime;
         location = res.location;//OK
         parent = res.parent; // TODO ??
         pauseTime = res.pauseTime;
         requestHeaders = res.requestHeaders;//OK
         responseCode = res.responseCode;//OK
         responseData = res.responseData;//OK
         responseDataAsString = null;
         responseHeaders = res.responseHeaders;//OK
         responseMessage = res.responseMessage;//OK
         // Don't copy this; it is per instance resultFileName = res.resultFileName;
         sampleCount = res.sampleCount;
         samplerData = res.samplerData;
         saveConfig = res.saveConfig;
+        sentBytes = res.sentBytes;
         startTime = res.startTime;//OK
         stopTest = res.stopTest;
         stopTestNow = res.stopTestNow;
         stopThread = res.stopThread;
         startNextThreadLoop = res.startNextThreadLoop;
         subResults = res.subResults; // TODO ??
         success = res.success;//OK
         threadName = res.threadName;//OK
         elapsedTime = res.elapsedTime;
         timeStamp = res.timeStamp;
     }
 
     public boolean isStampedAtStart() {
         return startTimeStamp;
     }
 
     /**
      * Create a sample with a specific elapsed time but don't allow the times to
      * be changed later
      *
      * (only used by HTTPSampleResult)
      *
      * @param elapsed
      *            time
      * @param atend
      *            create the sample finishing now, else starting now
      */
     protected SampleResult(long elapsed, boolean atend) {
         this();
         long now = currentTimeInMillis();
         if (atend) {
             setTimes(now - elapsed, now);
         } else {
             setTimes(now, now + elapsed);
         }
     }
 
     /**
      * Create a sample with specific start and end times for test purposes, but
      * don't allow the times to be changed later
      *
      * (used by StatVisualizerModel.Test)
      *
      * @param start
      *            start time in milliseconds since unix epoch
      * @param end
      *            end time in milliseconds since unix epoch
      * @return sample with given start and end time
      */
     public static SampleResult createTestSample(long start, long end) {
         SampleResult res = new SampleResult();
         res.setStartTime(start);
         res.setEndTime(end);
         return res;
     }
 
     /**
      * Create a sample with a specific elapsed time for test purposes, but don't
      * allow the times to be changed later
      *
      * @param elapsed
      *            - desired elapsed time in milliseconds
      * @return sample that starts 'now' and ends <code>elapsed</code> milliseconds later
      */
     public static SampleResult createTestSample(long elapsed) {
         long now = System.currentTimeMillis();
         return createTestSample(now, now + elapsed);
     }
 
     /**
      * Allow users to create a sample with specific timestamp and elapsed times
      * for cloning purposes, but don't allow the times to be changed later
      *
      * Currently used by CSVSaveService and
      * StatisticalSampleResult
      *
      * @param stamp
      *            this may be a start time or an end time (both in
      *            milliseconds)
      * @param elapsed
      *            time in milliseconds
      */
     public SampleResult(long stamp, long elapsed) {
         this();
         stampAndTime(stamp, elapsed);
     }
 
     private static long sampleNsClockInMs() {
         return System.nanoTime() / 1000000;
     }
 
     /**
      * Helper method to get 1 ms resolution timing.
      * 
      * @return the current time in milliseconds
      * @throws RuntimeException
      *             when <code>useNanoTime</code> is <code>true</code> but
      *             <code>nanoTimeOffset</code> is not set
      */
     public long currentTimeInMillis() {
         if (useNanoTime){
             if (nanoTimeOffset == Long.MIN_VALUE){
                 throw new RuntimeException("Invalid call; nanoTimeOffset as not been set");
             }
             return sampleNsClockInMs() + nanoTimeOffset;            
         }
         return System.currentTimeMillis();
     }
 
     // Helper method to maintain timestamp relationships
     private void stampAndTime(long stamp, long elapsed) {
         if (startTimeStamp) {
             startTime = stamp;
             endTime = stamp + elapsed;
         } else {
             startTime = stamp - elapsed;
             endTime = stamp;
         }
         timeStamp = stamp;
         elapsedTime = elapsed;
     }
 
     /**
      * For use by SaveService only.
      * 
      * @param stamp
      *            this may be a start time or an end time (both in milliseconds)
      * @param elapsed
      *            time in milliseconds
      * @throws RuntimeException
      *             when <code>startTime</code> or <code>endTime</code> has been
      *             set already
      */
     public void setStampAndTime(long stamp, long elapsed) {
         if (startTime != 0 || endTime != 0){
             throw new RuntimeException("Calling setStampAndTime() after start/end times have been set");
         }
         stampAndTime(stamp, elapsed);
     }
 
     /**
      * Set the "marked" flag to show that the result has been written to the file.
      *
      * @param filename the name of the file
      * @return <code>true</code> if the result was previously marked
      */
     public synchronized boolean markFile(String filename) {
         return !files.add(filename);
     }
 
     public String getResponseCode() {
         return responseCode;
     }
 
     private static final String OK_CODE = Integer.toString(HttpURLConnection.HTTP_OK);
     private static final String OK_MSG = "OK"; // $NON-NLS-1$
 
     /**
      * Set response code to OK, i.e. "200"
      *
      */
     public void setResponseCodeOK(){
         responseCode=OK_CODE;
     }
 
     public void setResponseCode(String code) {
         responseCode = code;
     }
 
     public boolean isResponseCodeOK(){
         return responseCode.equals(OK_CODE);
     }
     public String getResponseMessage() {
         return responseMessage;
     }
 
     public void setResponseMessage(String msg) {
         responseMessage = msg;
     }
 
     public void setResponseMessageOK() {
         responseMessage = OK_MSG;
     }
 
     /**
      * Set result statuses OK - shorthand method to set:
      * <ul>
      * <li>ResponseCode</li>
      * <li>ResponseMessage</li>
      * <li>Successful status</li>
      * </ul>
      */
     public void setResponseOK(){
         setResponseCodeOK();
         setResponseMessageOK();
         setSuccessful(true);
     }
 
     public String getThreadName() {
         return threadName;
     }
 
     public void setThreadName(String threadName) {
         this.threadName = threadName;
     }
 
     /**
      * Get the sample timestamp, which may be either the start time or the end time.
      *
      * @see #getStartTime()
      * @see #getEndTime()
      *
      * @return timeStamp in milliseconds
      */
     public long getTimeStamp() {
         return timeStamp;
     }
 
     public String getSampleLabel() {
         return label;
     }
 
     /**
      * Get the sample label for use in summary reports etc.
      *
      * @param includeGroup whether to include the thread group name
      * @return the label
      */
     public String getSampleLabel(boolean includeGroup) {
         if (includeGroup) {
             StringBuilder sb = new StringBuilder(threadName.substring(0,threadName.lastIndexOf(' '))); //$NON-NLS-1$
             return sb.append(":").append(label).toString(); //$NON-NLS-1$
         }
         return label;
     }
 
     public void setSampleLabel(String label) {
         this.label = label;
     }
 
     public void addAssertionResult(AssertionResult assertResult) {
         if (assertionResults == null) {
             assertionResults = new ArrayList<>();
         }
         assertionResults.add(assertResult);
     }
 
     /**
      * Gets the assertion results associated with this sample.
      *
      * @return an array containing the assertion results for this sample.
      *         Returns empty array if there are no assertion results.
      */
     public AssertionResult[] getAssertionResults() {
         if (assertionResults == null) {
             return EMPTY_AR;
         }
         return assertionResults.toArray(new AssertionResult[assertionResults.size()]);
     }
 
     /**
      * Add a subresult and adjust the parent byte count and end-time.
      * 
      * @param subResult
      *            the {@link SampleResult} to be added
      */
     public void addSubResult(SampleResult subResult) {
         if(subResult == null) {
             // see https://bz.apache.org/bugzilla/show_bug.cgi?id=54778
             return;
         }
         String tn = getThreadName();
         if (tn.length()==0) {
             tn=Thread.currentThread().getName();//TODO do this more efficiently
             this.setThreadName(tn);
         }
         subResult.setThreadName(tn); // TODO is this really necessary?
 
         // Extend the time to the end of the added sample
         setEndTime(Math.max(getEndTime(), subResult.getEndTime() + nanoTimeOffset - subResult.nanoTimeOffset)); // Bug 51855
         // Include the byte count for the added sample
         setBytes(getBytes() + subResult.getBytes());
+        setSentBytes(getSentBytes() + subResult.getSentBytes());
         setHeadersSize(getHeadersSize() + subResult.getHeadersSize());
         setBodySize(getBodySize() + subResult.getBodySize());
         addRawSubResult(subResult);
     }
     
     /**
      * Add a subresult to the collection without updating any parent fields.
      * 
      * @param subResult
      *            the {@link SampleResult} to be added
      */
     public void addRawSubResult(SampleResult subResult){
         storeSubResult(subResult);
     }
 
     /**
      * Add a subresult read from a results file.
      * <p>
      * As for {@link SampleResult#addSubResult(SampleResult)
      * addSubResult(SampleResult)}, except that the fields don't need to be
      * accumulated
      *
      * @param subResult
      *            the {@link SampleResult} to be added
      */
     public void storeSubResult(SampleResult subResult) {
         if (subResults == null) {
             subResults = new ArrayList<>();
         }
         subResults.add(subResult);
         subResult.setParent(this);
     }
 
     /**
      * Gets the subresults associated with this sample.
      *
      * @return an array containing the subresults for this sample. Returns an
      *         empty array if there are no subresults.
      */
     public SampleResult[] getSubResults() {
         if (subResults == null) {
             return EMPTY_SR;
         }
         return subResults.toArray(new SampleResult[subResults.size()]);
     }
 
     /**
      * Sets the responseData attribute of the SampleResult object.
      *
      * If the parameter is null, then the responseData is set to an empty byte array.
      * This ensures that getResponseData() can never be null.
      *
      * @param response
      *            the new responseData value
      */
     public void setResponseData(byte[] response) {
         responseDataAsString = null;
         responseData = response == null ? EMPTY_BA : response;
     }
 
     /**
      * Sets the responseData attribute of the SampleResult object.
      * Should only be called after setting the dataEncoding (if necessary)
      *
      * @param response
      *            the new responseData value (String)
      *
      * @deprecated - only intended for use from BeanShell code
      */
     @Deprecated
     public void setResponseData(String response) {
         responseDataAsString = null;
         try {
             responseData = response.getBytes(getDataEncodingWithDefault());
         } catch (UnsupportedEncodingException e) {
             log.warn("Could not convert string, using default encoding. "+e.getLocalizedMessage());
             responseData = response.getBytes(); // N.B. default charset is used deliberately here
         }
     }
 
     /**
      * Sets the encoding and responseData attributes of the SampleResult object.
      *
      * @param response the new responseData value (String)
      * @param encoding the encoding to set and then use (if null, use platform default)
      *
      */
     public void setResponseData(final String response, final String encoding) {
         responseDataAsString = null;
         String encodeUsing = encoding != null? encoding : DEFAULT_CHARSET;
         try {
             responseData = response.getBytes(encodeUsing);
             setDataEncoding(encodeUsing);
         } catch (UnsupportedEncodingException e) {
             log.warn("Could not convert string using '"+encodeUsing+
                     "', using default encoding: "+DEFAULT_CHARSET,e);
             responseData = response.getBytes(); // N.B. default charset is used deliberately here
             setDataEncoding(DEFAULT_CHARSET);
         }
     }
 
     /**
      * Gets the responseData attribute of the SampleResult object.
      * <p>
      * Note that some samplers may not store all the data, in which case
      * getResponseData().length will be incorrect.
      *
      * Instead, always use {@link #getBytes()} to obtain the sample result byte count.
      * </p>
      * @return the responseData value (cannot be null)
      */
     public byte[] getResponseData() {
         return responseData;
     }
 
     /**
      * Gets the responseData of the SampleResult object as a String
      *
      * @return the responseData value as a String, converted according to the encoding
      */
     public String getResponseDataAsString() {
         try {
             if(responseDataAsString == null) {
                 responseDataAsString= new String(responseData,getDataEncodingWithDefault());
             }
             return responseDataAsString;
         } catch (UnsupportedEncodingException e) {
             log.warn("Using platform default as "+getDataEncodingWithDefault()+" caused "+e);
             return new String(responseData); // N.B. default charset is used deliberately here
         }
     }
 
     public void setSamplerData(String s) {
         samplerData = s;
     }
 
     public String getSamplerData() {
         return samplerData;
     }
 
     /**
      * Get the time it took this sample to occur.
      *
      * @return elapsed time in milliseonds
      *
      */
     public long getTime() {
         return elapsedTime;
     }
 
     public boolean isSuccessful() {
         return success;
     }
 
     /**
      * Sets the data type of the sample.
      * @param dataType String containing {@link #BINARY} or {@link #TEXT}
      * @see #BINARY
      * @see #TEXT
      */
     public void setDataType(String dataType) {
         this.dataType = dataType;
     }
 
     /**
      * Returns the data type of the sample.
      * 
      * @return String containing {@link #BINARY} or {@link #TEXT} or the empty string
      * @see #BINARY
      * @see #TEXT
      */
     public String getDataType() {
         return dataType;
     }
 
     /**
      * Extract and save the DataEncoding and DataType from the parameter provided.
      * Does not save the full content Type.
      * @see #setContentType(String) which should be used to save the full content-type string
      *
      * @param ct - content type (may be null)
      */
     public void setEncodingAndType(String ct){
         if (ct != null) {
             // Extract charset and store as DataEncoding
             // N.B. The meta tag:
             // <META http-equiv="content-type" content="text/html; charset=foobar">
             // is now processed by HTTPSampleResult#getDataEncodingWithDefault
             final String CS_PFX = "charset="; // $NON-NLS-1$
             int cset = ct.toLowerCase(java.util.Locale.ENGLISH).indexOf(CS_PFX);
             if (cset >= 0) {
                 String charSet = ct.substring(cset + CS_PFX.length());
                 // handle: ContentType: text/plain; charset=ISO-8859-1; format=flowed
                 int semiColon = charSet.indexOf(';');
                 if (semiColon >= 0) {
                     charSet=charSet.substring(0, semiColon);
                 }
                 // Check for quoted string
                 if (charSet.startsWith("\"")||charSet.startsWith("\'")){ // $NON-NLS-1$
                     setDataEncoding(charSet.substring(1, charSet.length()-1)); // remove quotes
                 } else {
                     setDataEncoding(charSet);
                 }
             }
             if (isBinaryType(ct)) {
                 setDataType(BINARY);
             } else {
                 setDataType(TEXT);
             }
         }
     }
 
     // List of types that are known to be binary
     private static final String[] BINARY_TYPES = {
         "image/",       //$NON-NLS-1$
         "audio/",       //$NON-NLS-1$
         "video/",       //$NON-NLS-1$
         };
 
     // List of types that are known to be ascii, although they may appear to be binary
     private static final String[] NON_BINARY_TYPES = {
         "audio/x-mpegurl",  //$NON-NLS-1$ (HLS Media Manifest)
         "video/f4m"         //$NON-NLS-1$ (Flash Media Manifest)
         };
 
     /*
      * Determine if content-type is known to be binary, i.e. not displayable as text.
      *
      * @param ct content type
      * @return true if content-type is of type binary.
      */
     private static boolean isBinaryType(String ct){
         for (String entry : NON_BINARY_TYPES){
             if (ct.startsWith(entry)){
                 return false;
             }
         }
         for (String binaryType : BINARY_TYPES) {
             if (ct.startsWith(binaryType)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Sets the successful attribute of the SampleResult object.
      *
      * @param success
      *            the new successful value
      */
     public void setSuccessful(boolean success) {
         this.success = success;
     }
 
     /**
      * Returns the display name.
      *
      * @return display name of this sample result
      */
     @Override
     public String toString() {
         return getSampleLabel();
     }
 
     /**
      * Returns the dataEncoding or the default if no dataEncoding was provided.
      * 
      * @return the value of the dataEncoding or DEFAULT_ENCODING
      */
     public String getDataEncodingWithDefault() {
         return getDataEncodingWithDefault(DEFAULT_ENCODING);
     }
 
     /**
      * Returns the dataEncoding or the default if no dataEncoding was provided.
      * 
      * @param defaultEncoding the default to be applied
      * @return the value of the dataEncoding or the provided default
      */
     protected String getDataEncodingWithDefault(String defaultEncoding) {
         if (dataEncoding != null && dataEncoding.length() > 0) {
             return dataEncoding;
         }
         return defaultEncoding;
     }
 
     /**
      * Returns the dataEncoding. May be null or the empty String.
      * @return the value of the dataEncoding
      */
     public String getDataEncodingNoDefault() {
         return dataEncoding;
     }
 
     /**
      * Sets the dataEncoding.
      *
      * @param dataEncoding
      *            the dataEncoding to set, e.g. ISO-8895-1, UTF-8
      */
     public void setDataEncoding(String dataEncoding) {
         this.dataEncoding = dataEncoding;
     }
 
     /**
      * @return whether to stop the test
      */
     public boolean isStopTest() {
         return stopTest;
     }
 
     /**
      * @return whether to stop the test now
      */
     public boolean isStopTestNow() {
         return stopTestNow;
     }
 
     /**
      * @return whether to stop this thread
      */
     public boolean isStopThread() {
         return stopThread;
     }
 
     public void setStopTest(boolean b) {
         stopTest = b;
     }
 
     public void setStopTestNow(boolean b) {
         stopTestNow = b;
     }
 
     public void setStopThread(boolean b) {
         stopThread = b;
     }
 
     /**
      * @return the request headers
      */
     public String getRequestHeaders() {
         return requestHeaders;
     }
 
     /**
      * @return the response headers
      */
     public String getResponseHeaders() {
         return responseHeaders;
     }
 
     /**
      * @param string -
      *            request headers
      */
     public void setRequestHeaders(String string) {
         requestHeaders = string;
     }
 
     /**
      * @param string -
      *            response headers
      */
     public void setResponseHeaders(String string) {
         responseHeaders = string;
     }
 
     /**
      * @return the full content type - e.g. text/html [;charset=utf-8 ]
      */
     public String getContentType() {
         return contentType;
     }
 
     /**
      * Get the media type from the Content Type
      * @return the media type - e.g. text/html (without charset, if any)
      */
     public String getMediaType() {
         return JOrphanUtils.trim(contentType," ;").toLowerCase(java.util.Locale.ENGLISH);
     }
 
     /**
      * Stores the content-type string, e.g. <code>text/xml; charset=utf-8</code>
      * @see #setEncodingAndType(String) which can be used to extract the charset.
      *
      * @param string the content-type to be set
      */
     public void setContentType(String string) {
         contentType = string;
     }
 
     /**
      * @return idleTime
      */
     public long getIdleTime() {
         return idleTime;
     }
 
     /**
      * @return the end time
      */
     public long getEndTime() {
         return endTime;
     }
 
     /**
      * @return the start time
      */
     public long getStartTime() {
         return startTime;
     }
 
     /*
      * Helper methods N.B. setStartTime must be called before setEndTime
      *
      * setStartTime is used by HTTPSampleResult to clone the parent sampler and
      * allow the original start time to be kept
      */
     protected final void setStartTime(long start) {
         startTime = start;
         if (startTimeStamp) {
             timeStamp = startTime;
         }
     }
 
     public void setEndTime(long end) {
         endTime = end;
         if (!startTimeStamp) {
             timeStamp = endTime;
         }
         if (startTime == 0) {
             log.error("setEndTime must be called after setStartTime", new Throwable("Invalid call sequence"));
             // TODO should this throw an error?
         } else {
             elapsedTime = endTime - startTime - idleTime;
         }
     }
 
     /**
      * Set idle time pause.
      * For use by SampleResultConverter/CSVSaveService.
      * @param idle long
      */
     public void setIdleTime(long idle) {
         idleTime = idle;
     }
 
     private void setTimes(long start, long end) {
         setStartTime(start);
         setEndTime(end);
     }
 
     /**
      * Record the start time of a sample
      *
      */
     public void sampleStart() {
         if (startTime == 0) {
             setStartTime(currentTimeInMillis());
         } else {
             log.error("sampleStart called twice", new Throwable("Invalid call sequence"));
         }
     }
 
     /**
      * Record the end time of a sample and calculate the elapsed time
      *
      */
     public void sampleEnd() {
         if (endTime == 0) {
             setEndTime(currentTimeInMillis());
         } else {
             log.error("sampleEnd called twice", new Throwable("Invalid call sequence"));
         }
     }
 
     /**
      * Pause a sample
      *
      */
     public void samplePause() {
         if (pauseTime != 0) {
             log.error("samplePause called twice", new Throwable("Invalid call sequence"));
         }
         pauseTime = currentTimeInMillis();
     }
 
     /**
      * Resume a sample
      *
      */
     public void sampleResume() {
         if (pauseTime == 0) {
             log.error("sampleResume without samplePause", new Throwable("Invalid call sequence"));
         }
         idleTime += currentTimeInMillis() - pauseTime;
         pauseTime = 0;
     }
 
     /**
      * When a Sampler is working as a monitor
      *
      * @param monitor
      *            flag whether this sampler is working as a monitor
      */
     public void setMonitor(boolean monitor) {
         isMonitor = monitor;
     }
 
     /**
      * If the sampler is a monitor, method will return true.
      *
      * @return true if the sampler is a monitor
      */
     public boolean isMonitor() {
         return isMonitor;
     }
 
     /**
      * The statistical sample sender aggregates several samples to save on
      * transmission costs.
      * 
      * @param count number of samples represented by this instance
      */
     public void setSampleCount(int count) {
         sampleCount = count;
     }
 
     /**
      * return the sample count. by default, the value is 1.
      *
      * @return the sample count
      */
     public int getSampleCount() {
         return sampleCount;
     }
 
     /**
      * Returns the count of errors.
      *
      * @return 0 - or 1 if the sample failed
      * 
      * TODO do we need allow for nested samples?
      */
     public int getErrorCount(){
         return success ? 0 : 1;
     }
 
     public void setErrorCount(int i){// for reading from CSV files
         // ignored currently
     }
 
     /*
      * TODO: error counting needs to be sorted out.
      *
      * At present the Statistical Sampler tracks errors separately
      * It would make sense to move the error count here, but this would
      * mean lots of changes.
      * It's also tricky maintaining the count - it can't just be incremented/decremented
      * when the success flag is set as this may be done multiple times.
      * The work-round for now is to do the work in the StatisticalSampleResult,
      * which overrides this method.
      * Note that some JMS samplers also create samples with > 1 sample count
      * Also the Transaction Controller probably needs to be changed to do
      * proper sample and error accounting.
      * The purpose of this work-round is to allow at least minimal support for
      * errors in remote statistical batch mode.
      *
      */
     /**
      * In the event the sampler does want to pass back the actual contents, we
      * still want to calculate the throughput. The bytes are the bytes of the
      * response data.
      *
      * @param length
      *            the number of bytes of the response data for this sample
      */
     public void setBytes(int length) {
         bytes = length;
     }
 
     /**
+     * 
+     * @param sentBytesCount long sent bytes
+     */
+    public void setSentBytes(long sentBytesCount) {
+        sentBytes = sentBytesCount;
+    }
+
+    /**
+     * @return the sentBytes
+     */
+    public long getSentBytes() {
+        return sentBytes;
+    }
+    
+    /**
      * return the bytes returned by the response.
      *
      * @return byte count
      */
     public int getBytes() {
         if (GETBYTES_NETWORK_SIZE) {
             int tmpSum = this.getHeadersSize() + this.getBodySize();
             return tmpSum == 0 ? bytes : tmpSum;
         } else if (GETBYTES_HEADERS_SIZE) {
             return this.getHeadersSize();
         } else if (GETBYTES_BODY_REALSIZE) {
             return this.getBodySize();
         }
         return bytes == 0 ? responseData.length : bytes;
     }
 
     /**
      * @return Returns the latency.
      */
     public long getLatency() {
         return latency;
     }
 
     /**
      * Set the time to the first response
      *
      */
     public void latencyEnd() {
         latency = currentTimeInMillis() - startTime - idleTime;
     }
 
     /**
      * This is only intended for use by SampleResultConverter!
      *
      * @param latency
      *            The latency to set.
      */
     public void setLatency(long latency) {
         this.latency = latency;
     }
 
     /**
      * @return Returns the connect time.
      */
     public long getConnectTime() {
         return connectTime;
     }
 
     /**
      * Set the time to the end of connecting
      */
     public void connectEnd() {
         connectTime = currentTimeInMillis() - startTime - idleTime;
     }
 
     /**
      * This is only intended for use by SampleResultConverter!
      *
      * @param time The connect time to set.
      */
     public void setConnectTime(long time) {
         this.connectTime = time;
     }
 
     /**
      * This is only intended for use by SampleResultConverter!
      *
      * @param timeStamp
      *            The timeStamp to set.
      */
     public void setTimeStamp(long timeStamp) {
         this.timeStamp = timeStamp;
     }
 
     private URL location;
 
     public void setURL(URL location) {
         this.location = location;
     }
 
     public URL getURL() {
         return location;
     }
 
     /**
      * Get a String representation of the URL (if defined).
      *
      * @return ExternalForm of URL, or empty string if url is null
      */
     public String getUrlAsString() {
         return location == null ? "" : location.toExternalForm();
     }
 
     /**
      * @return Returns the parent.
      */
     public SampleResult getParent() {
         return parent;
     }
 
     /**
      * @param parent
      *            The parent to set.
      */
     public void setParent(SampleResult parent) {
         this.parent = parent;
     }
 
     public String getResultFileName() {
         return resultFileName;
     }
 
     public void setResultFileName(String resultFileName) {
         this.resultFileName = resultFileName;
     }
 
     public int getGroupThreads() {
         return groupThreads;
     }
 
     public void setGroupThreads(int n) {
         this.groupThreads = n;
     }
 
     public int getAllThreads() {
         return allThreads;
     }
 
     public void setAllThreads(int n) {
         this.allThreads = n;
     }
 
     // Bug 47394
     /**
      * Allow custom SampleSenders to drop unwanted assertionResults
      */
     public void removeAssertionResults() {
         this.assertionResults = null;
     }
 
     /**
      * Allow custom SampleSenders to drop unwanted subResults
      */
     public void removeSubResults() {
         this.subResults = null;
     }
     
     /**
      * Set the headers size in bytes
      * 
      * @param size
      *            the number of bytes of the header
      */
     public void setHeadersSize(int size) {
         this.headersSize = size;
     }
     
     /**
      * Get the headers size in bytes
      * 
      * @return the headers size
      */
     public int getHeadersSize() {
         return headersSize;
     }
 
     /**
      * @return the body size in bytes
      */
     public int getBodySize() {
         return bodySize == 0 ? responseData.length : bodySize;
     }
 
     /**
      * @param bodySize the body size to set
      */
     public void setBodySize(int bodySize) {
         this.bodySize = bodySize;
     }
 
     private static class NanoOffset extends Thread {
 
         private static volatile long nanoOffset; 
 
         static long getNanoOffset() {
             return nanoOffset;
         }
 
         @Override
         public void run() {
             // Wait longer than a clock pulse (generally 10-15ms)
             getOffset(30L); // Catch an early clock pulse to reduce slop.
             while(true) {
                 getOffset(NANOTHREAD_SLEEP); // Can now afford to wait a bit longer between checks
             }
             
         }
 
         private void getOffset(long wait) {
             try {
                 TimeUnit.MILLISECONDS.sleep(wait);
                 long clock = System.currentTimeMillis();
                 long nano = SampleResult.sampleNsClockInMs();
                 nanoOffset = clock - nano;
             } catch (InterruptedException ignore) {
                 // ignored
             }
         }
         
     }
 
     /**
      * @return the startNextThreadLoop
      */
     public boolean isStartNextThreadLoop() {
         return startNextThreadLoop;
     }
 
     /**
      * @param startNextThreadLoop the startNextLoop to set
      */
     public void setStartNextThreadLoop(boolean startNextThreadLoop) {
         this.startNextThreadLoop = startNextThreadLoop;
     }
 
     /**
      * Clean up cached data
      */
     public void cleanAfterSample() {
         this.responseDataAsString = null;
     }
 
     @Override
     public Object clone() {
         try {
             return super.clone();
         } catch (CloneNotSupportedException e) {
             throw new IllegalStateException("This should not happen");
         }
     }
 
     @Override
     public List<String> getSearchableTokens() throws Exception {
         List<String> datasToSearch = new ArrayList<>(4);
         datasToSearch.add(getSampleLabel());
         datasToSearch.add(getResponseDataAsString());
         datasToSearch.add(getRequestHeaders());
         datasToSearch.add(getResponseHeaders());
         return datasToSearch;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
index 6aecda7c9..17dab915b 100644
--- a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
+++ b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
@@ -1,916 +1,935 @@
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
+    
+    // Save bytes written
+    private static final String SAVE_SENT_BYTES_PROP = "jmeter.save.saveservice.sent_bytes"; // $NON_NLS-1$
 
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
 
-    private boolean url = _url, bytes = _bytes , fileName = _fileName;
+    private boolean url = _url, bytes = _bytes , sentBytes = _sentBytes, fileName = _fileName;
 
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
+    
+    private static final boolean _sentBytes;
 
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
 
     public static final String DEFAULT_DELIMITER = ","; // $NON_NLS-1$
 
     /**
      * Read in the properties having to do with saving from a properties file.
      */
     static {
         Properties props = JMeterUtils.getJMeterProperties();
 
         _subresults      = TRUE.equalsIgnoreCase(props.getProperty(SUBRESULTS_PROP, TRUE));
         _assertions      = TRUE.equalsIgnoreCase(props.getProperty(ASSERTIONS_PROP, TRUE));
         _latency         = TRUE.equalsIgnoreCase(props.getProperty(LATENCY_PROP, TRUE));
         _connectTime     = TRUE.equalsIgnoreCase(props.getProperty(CONNECT_TIME_PROP, TRUE));
         _samplerData     = TRUE.equalsIgnoreCase(props.getProperty(SAMPLERDATA_PROP, FALSE));
         _responseHeaders = TRUE.equalsIgnoreCase(props.getProperty(RESPONSEHEADERS_PROP, FALSE));
         _requestHeaders  = TRUE.equalsIgnoreCase(props.getProperty(REQUESTHEADERS_PROP, FALSE));
         _encoding        = TRUE.equalsIgnoreCase(props.getProperty(ENCODING_PROP, FALSE));
 
         String dlm = JMeterUtils.getDelimiter(props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER));
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
+        
+        _sentBytes = TRUE.equalsIgnoreCase(props.getProperty(SAVE_SENT_BYTES_PROP, TRUE));
 
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
+        "SentBytes",
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
+        sentBytes = value;
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
+            s.sentBytes == sentBytes &&
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
+        hash = 31 * hash + (sentBytes ? 1 : 0);
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
+    
+    public boolean saveSentBytes() {
+        return sentBytes;
+    }
+
+    public void setSentBytes(boolean save) {
+        this.sentBytes = save;
+    }
 
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
diff --git a/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java b/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
index 82c23b5cf..8fc39d684 100644
--- a/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
@@ -1,137 +1,138 @@
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
 
 /**
  * Aggregates sample results for use by the Statistical remote batch mode.
  * Samples are aggregated by the key defined by getKey().
  * TODO: merge error count into parent class?
  */
 public class StatisticalSampleResult extends SampleResult implements
         Serializable {
 
     private static final long serialVersionUID = 240L;
 
     private int errorCount;
 
     // Need to maintain our own elapsed timer to ensure more accurate aggregation
     private long elapsed;
 
     public StatisticalSampleResult(){// May be called by XStream
     }
 
     /**
      * Allow CsvSaveService to generate a suitable result when sample/error counts have been saved.
      *
      * @deprecated Needs to be replaced when multiple sample results are sorted out
      *
      * @param stamp this may be a start time or an end time (both in milliseconds)
      * @param elapsed time in milliseconds
      */
     @Deprecated
     public StatisticalSampleResult(long stamp, long elapsed) {
         super(stamp, elapsed);
         this.elapsed = elapsed;
     }
 
     /**
      * Create a statistical sample result from an ordinary sample result.
      * 
      * @param res the sample result 
      */
     public StatisticalSampleResult(SampleResult res) {
         // Copy data that is shared between samples (i.e. the key items):
         setSampleLabel(res.getSampleLabel());
         
         setThreadName(res.getThreadName());
 
         setSuccessful(true); // Assume result is OK
         setSampleCount(0); // because we add the sample count in later
         elapsed = 0;
     }
 
     public void add(SampleResult res) {
         // Add Sample Counter
         setSampleCount(getSampleCount() + res.getSampleCount());
 
         setBytes(getBytes() + res.getBytes());
+        setSentBytes(getSentBytes() + res.getSentBytes());
 
         // Add Error Counter
         if (!res.isSuccessful()) {
             errorCount++;
             this.setSuccessful(false);
         }
 
         // Set start/end times
         if (getStartTime()==0){ // Bug 40954 - ensure start time gets started!
             this.setStartTime(res.getStartTime());
         } else {
             this.setStartTime(Math.min(getStartTime(), res.getStartTime()));
         }
         this.setEndTime(Math.max(getEndTime(), res.getEndTime()));
 
         setLatency(getLatency()+ res.getLatency());
         setConnectTime(getConnectTime()+ res.getConnectTime());
 
         elapsed += res.getTime();
     }
 
     @Override
     public long getTime() {
         return elapsed;
     }
 
     @Override
     public long getTimeStamp() {
         return getEndTime();
     }
 
     @Override
     public int getErrorCount() {// Overrides SampleResult
         return errorCount;
     }
 
     @Override
     public void setErrorCount(int e) {// for reading CSV files
         errorCount = e;
     }
 
     /**
      * Generates the key to be used for aggregating samples as follows:<br>
      * <code>sampleLabel</code> "-" <code>[threadName|threadGroup]</code>
      * <p>
      * N.B. the key should agree with the fixed items that are saved in the sample.
      *
      * @param event sample event whose key is to be calculated
      * @param keyOnThreadName true if key should use thread name, otherwise use thread group
      * @return the key to use for aggregating samples
      */
     public static String getKey(SampleEvent event, boolean keyOnThreadName) {
         StringBuilder sb = new StringBuilder(80);
         sb.append(event.getResult().getSampleLabel());
         if (keyOnThreadName){
             sb.append('-').append(event.getResult().getThreadName());
         } else {
             sb.append('-').append(event.getThreadGroup());
         }
         return sb.toString();
     }
 }
diff --git a/src/core/org/apache/jmeter/save/CSVSaveService.java b/src/core/org/apache/jmeter/save/CSVSaveService.java
index b8be1847a..33785a6d7 100644
--- a/src/core/org/apache/jmeter/save/CSVSaveService.java
+++ b/src/core/org/apache/jmeter/save/CSVSaveService.java
@@ -1,1156 +1,1173 @@
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
 
 package org.apache.jmeter.save;
 
 import java.io.BufferedReader;
 import java.io.CharArrayWriter;
 import java.io.FileInputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.StringReader;
 import java.nio.charset.StandardCharsets;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 import javax.swing.table.DefaultTableModel;
 
 import org.apache.commons.collections.map.LinkedMap;
 import org.apache.commons.lang3.CharUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.samplers.StatisticalSampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Visualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * This class provides a means for saving/reading test results as CSV files.
  */
 // For unit tests, @see TestCSVSaveService
 public final class CSVSaveService {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // ---------------------------------------------------------------------
     // XML RESULT FILE CONSTANTS AND FIELD NAME CONSTANTS
     // ---------------------------------------------------------------------
 
     public static final String DATA_TYPE = "dataType"; // $NON-NLS-1$
     public static final String FAILURE_MESSAGE = "failureMessage"; // $NON-NLS-1$
     public static final String LABEL = "label"; // $NON-NLS-1$
     public static final String RESPONSE_CODE = "responseCode"; // $NON-NLS-1$
     public static final String RESPONSE_MESSAGE = "responseMessage"; // $NON-NLS-1$
     public static final String SUCCESSFUL = "success"; // $NON-NLS-1$
     public static final String THREAD_NAME = "threadName"; // $NON-NLS-1$
     public static final String TIME_STAMP = "timeStamp"; // $NON-NLS-1$
 
     // ---------------------------------------------------------------------
     // ADDITIONAL CSV RESULT FILE CONSTANTS AND FIELD NAME CONSTANTS
     // ---------------------------------------------------------------------
 
     public static final String CSV_ELAPSED = "elapsed"; // $NON-NLS-1$
     public static final String CSV_BYTES = "bytes"; // $NON-NLS-1$
+    public static final String CSV_SENT_BYTES = "sentBytes"; // $NON-NLS-1$
     public static final String CSV_THREAD_COUNT1 = "grpThreads"; // $NON-NLS-1$
     public static final String CSV_THREAD_COUNT2 = "allThreads"; // $NON-NLS-1$
     public static final String CSV_SAMPLE_COUNT = "SampleCount"; // $NON-NLS-1$
     public static final String CSV_ERROR_COUNT = "ErrorCount"; // $NON-NLS-1$
     public static final String CSV_URL = "URL"; // $NON-NLS-1$
     public static final String CSV_FILENAME = "Filename"; // $NON-NLS-1$
     public static final String CSV_LATENCY = "Latency"; // $NON-NLS-1$
     public static final String CSV_CONNECT_TIME = "Connect"; // $NON-NLS-1$
     public static final String CSV_ENCODING = "Encoding"; // $NON-NLS-1$
     public static final String CSV_HOSTNAME = "Hostname"; // $NON-NLS-1$
     public static final String CSV_IDLETIME = "IdleTime"; // $NON-NLS-1$
 
     // Used to enclose variable name labels, to distinguish from any of the
     // above labels
     private static final String VARIABLE_NAME_QUOTE_CHAR = "\""; // $NON-NLS-1$
 
     // Initial config from properties
     static private final SampleSaveConfiguration _saveConfig = SampleSaveConfiguration
             .staticConfig();
 
     // Date formats to try if the time format does not parse as milliseconds
     private static final String[] DATE_FORMAT_STRINGS = {
         "yyyy/MM/dd HH:mm:ss.SSS",  // $NON-NLS-1$
         "yyyy/MM/dd HH:mm:ss",  // $NON-NLS-1$
         "yyyy-MM-dd HH:mm:ss.SSS",  // $NON-NLS-1$
         "yyyy-MM-dd HH:mm:ss",  // $NON-NLS-1$
 
         "MM/dd/yy HH:mm:ss"  // $NON-NLS-1$ (for compatibility, this is the original default)
         };
 
     private static final String LINE_SEP = System.getProperty("line.separator"); // $NON-NLS-1$
 
     /**
      * Private constructor to prevent instantiation.
      */
     private CSVSaveService() {
     }
 
     /**
      * Read Samples from a file; handles quoted strings.
      * 
      * @param filename
      *            input file
      * @param visualizer
      *            where to send the results
      * @param resultCollector
      *            the parent collector
      * @throws IOException
      *             when the file referenced by <code>filename</code> can't be
      *             read correctly
      */
     public static void processSamples(String filename, Visualizer visualizer,
             ResultCollector resultCollector) throws IOException {
         BufferedReader dataReader = null;
         final boolean errorsOnly = resultCollector.isErrorLogging();
         final boolean successOnly = resultCollector.isSuccessOnlyLogging();
         try {
             dataReader = new BufferedReader(new InputStreamReader(
                     new FileInputStream(filename), SaveService.getFileEncoding(StandardCharsets.UTF_8.name())));
             dataReader.mark(400);// Enough to read the header column names
             // Get the first line, and see if it is the header
             String line = dataReader.readLine();
             if (line == null) {
                 throw new IOException(filename + ": unable to read header line");
             }
             long lineNumber = 1;
             SampleSaveConfiguration saveConfig = CSVSaveService
                     .getSampleSaveConfiguration(line, filename);
             if (saveConfig == null) {// not a valid header
                 log.info(filename
                         + " does not appear to have a valid header. Using default configuration.");
                 saveConfig = (SampleSaveConfiguration) resultCollector
                         .getSaveConfig().clone(); // may change the format later
                 dataReader.reset(); // restart from beginning
                 lineNumber = 0;
             }
             String[] parts;
             final char delim = saveConfig.getDelimiter().charAt(0);
             // TODO: does it matter that an empty line will terminate the loop?
             // CSV output files should never contain empty lines, so probably
             // not
             // If so, then need to check whether the reader is at EOF
             while ((parts = csvReadFile(dataReader, delim)).length != 0) {
                 lineNumber++;
                 SampleEvent event = CSVSaveService.makeResultFromDelimitedString(parts, saveConfig, lineNumber);
                 if (event != null) {
                     final SampleResult result = event.getResult();
                     if (ResultCollector.isSampleWanted(result.isSuccessful(),
                             errorsOnly, successOnly)) {
                         visualizer.add(result);
                     }
                 }
             }
         } finally {
             JOrphanUtils.closeQuietly(dataReader);
         }
     }
 
     /**
      * Make a SampleResult given a set of tokens
      * 
      * @param parts
      *            tokens parsed from the input
      * @param saveConfig
      *            the save configuration (may be updated)
      * @param lineNumber the line number (for error reporting)
      * @return the sample result
      * 
      * @throws JMeterError
      */
     private static SampleEvent makeResultFromDelimitedString(
             final String[] parts, 
             final SampleSaveConfiguration saveConfig, // may be updated
             final long lineNumber) {
 
         SampleResult result = null;
         String hostname = "";// $NON-NLS-1$
         long timeStamp = 0;
         long elapsed = 0;
         String text = null;
         String field = null; // Save the name for error reporting
         int i = 0;
         try {
             if (saveConfig.saveTimestamp()) {
                 field = TIME_STAMP;
                 text = parts[i++];
                 if (saveConfig.printMilliseconds()) {
                     try {
                         timeStamp = Long.parseLong(text); // see if this works
                     } catch (NumberFormatException e) { // it did not, let's try some other formats
                         log.warn(e.toString());
                         boolean foundMatch = false;
                         for(String fmt : DATE_FORMAT_STRINGS) {
                             SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);
                             dateFormat.setLenient(false);
                             try {
                                 Date stamp = dateFormat.parse(text);
                                 timeStamp = stamp.getTime();
                                 // method is only ever called from one thread at a time
                                 // so it's OK to use a static DateFormat
                                 log.warn("Setting date format to: " + fmt);
                                 saveConfig.setFormatter(dateFormat);
                                 foundMatch = true;
                                 break;
                             } catch (ParseException e1) {
                                 log.info(text+" did not match "+fmt);
                             }
                         }
                         if (!foundMatch) {
                             throw new ParseException("No date-time format found matching "+text,-1);
                         }
                     }
                 } else if (saveConfig.formatter() != null) {
                     Date stamp = saveConfig.formatter().parse(text);
                     timeStamp = stamp.getTime();
                 } else { // can this happen?
                     final String msg = "Unknown timestamp format";
                     log.warn(msg);
                     throw new JMeterError(msg);
                 }
             }
 
             if (saveConfig.saveTime()) {
                 field = CSV_ELAPSED;
                 text = parts[i++];
                 elapsed = Long.parseLong(text);
             }
 
             if (saveConfig.saveSampleCount()) {
                 result = new StatisticalSampleResult(timeStamp, elapsed);
             } else {
                 result = new SampleResult(timeStamp, elapsed);
             }
 
             if (saveConfig.saveLabel()) {
                 field = LABEL;
                 text = parts[i++];
                 result.setSampleLabel(text);
             }
             if (saveConfig.saveCode()) {
                 field = RESPONSE_CODE;
                 text = parts[i++];
                 result.setResponseCode(text);
             }
 
             if (saveConfig.saveMessage()) {
                 field = RESPONSE_MESSAGE;
                 text = parts[i++];
                 result.setResponseMessage(text);
             }
 
             if (saveConfig.saveThreadName()) {
                 field = THREAD_NAME;
                 text = parts[i++];
                 result.setThreadName(text);
             }
 
             if (saveConfig.saveDataType()) {
                 field = DATA_TYPE;
                 text = parts[i++];
                 result.setDataType(text);
             }
 
             if (saveConfig.saveSuccess()) {
                 field = SUCCESSFUL;
                 text = parts[i++];
                 result.setSuccessful(Boolean.valueOf(text).booleanValue());
             }
 
             if (saveConfig.saveAssertionResultsFailureMessage()) {
                 i++;
                 // TODO - should this be restored?
             }
 
             if (saveConfig.saveBytes()) {
                 field = CSV_BYTES;
                 text = parts[i++];
                 result.setBytes(Integer.parseInt(text));
             }
+            
+            if (saveConfig.saveSentBytes()) {
+                field = CSV_SENT_BYTES;
+                text = parts[i++];
+                result.setSentBytes(Long.parseLong(text));
+            }
 
             if (saveConfig.saveThreadCounts()) {
                 field = CSV_THREAD_COUNT1;
                 text = parts[i++];
                 result.setGroupThreads(Integer.parseInt(text));
 
                 field = CSV_THREAD_COUNT2;
                 text = parts[i++];
                 result.setAllThreads(Integer.parseInt(text));
             }
 
             if (saveConfig.saveUrl()) {
                 i++;
                 // TODO: should this be restored?
             }
 
             if (saveConfig.saveFileName()) {
                 field = CSV_FILENAME;
                 text = parts[i++];
                 result.setResultFileName(text);
             }
             if (saveConfig.saveLatency()) {
                 field = CSV_LATENCY;
                 text = parts[i++];
                 result.setLatency(Long.parseLong(text));
             }
 
             if (saveConfig.saveEncoding()) {
                 field = CSV_ENCODING;
                 text = parts[i++];
                 result.setEncodingAndType(text);
             }
 
             if (saveConfig.saveSampleCount()) {
                 field = CSV_SAMPLE_COUNT;
                 text = parts[i++];
                 result.setSampleCount(Integer.parseInt(text));
                 field = CSV_ERROR_COUNT;
                 text = parts[i++];
                 result.setErrorCount(Integer.parseInt(text));
             }
 
             if (saveConfig.saveHostname()) {
                 field = CSV_HOSTNAME;
                 hostname = parts[i++];
             }
 
             if (saveConfig.saveIdleTime()) {
                 field = CSV_IDLETIME;
                 text = parts[i++];
                 result.setIdleTime(Long.parseLong(text));
             }
             if (saveConfig.saveConnectTime()) {
                 field = CSV_CONNECT_TIME;
                 text = parts[i++];
                 result.setConnectTime(Long.parseLong(text));
             }
 
             if (i + saveConfig.getVarCount() < parts.length) {
                 log.warn("Line: " + lineNumber + ". Found " + parts.length
                         + " fields, expected " + i
                         + ". Extra fields have been ignored.");
             }
 
         } catch (NumberFormatException | ParseException e) {
             log.warn("Error parsing field '" + field + "' at line "
                     + lineNumber + " " + e);
             throw new JMeterError(e);
         } catch (ArrayIndexOutOfBoundsException e) {
             log.warn("Insufficient columns to parse field '" + field
                     + "' at line " + lineNumber);
             throw new JMeterError(e);
         }
         return new SampleEvent(result, "", hostname);
     }
 
     /**
      * Generates the field names for the output file
      * 
      * @return the field names as a string
      */
     public static String printableFieldNamesToString() {
         return printableFieldNamesToString(_saveConfig);
     }
 
     /**
      * Generates the field names for the output file
      * 
      * @param saveConfig
      *            the configuration of what is to be saved
      * @return the field names as a string
      */
     public static String printableFieldNamesToString(
             SampleSaveConfiguration saveConfig) {
         StringBuilder text = new StringBuilder();
         String delim = saveConfig.getDelimiter();
 
         if (saveConfig.saveTimestamp()) {
             text.append(TIME_STAMP);
             text.append(delim);
         }
 
         if (saveConfig.saveTime()) {
             text.append(CSV_ELAPSED);
             text.append(delim);
         }
 
         if (saveConfig.saveLabel()) {
             text.append(LABEL);
             text.append(delim);
         }
 
         if (saveConfig.saveCode()) {
             text.append(RESPONSE_CODE);
             text.append(delim);
         }
 
         if (saveConfig.saveMessage()) {
             text.append(RESPONSE_MESSAGE);
             text.append(delim);
         }
 
         if (saveConfig.saveThreadName()) {
             text.append(THREAD_NAME);
             text.append(delim);
         }
 
         if (saveConfig.saveDataType()) {
             text.append(DATA_TYPE);
             text.append(delim);
         }
 
         if (saveConfig.saveSuccess()) {
             text.append(SUCCESSFUL);
             text.append(delim);
         }
 
         if (saveConfig.saveAssertionResultsFailureMessage()) {
             text.append(FAILURE_MESSAGE);
             text.append(delim);
         }
 
         if (saveConfig.saveBytes()) {
             text.append(CSV_BYTES);
             text.append(delim);
         }
+        
+        if (saveConfig.saveSentBytes()) {
+            text.append(CSV_SENT_BYTES);
+            text.append(delim);
+        }
 
         if (saveConfig.saveThreadCounts()) {
             text.append(CSV_THREAD_COUNT1);
             text.append(delim);
             text.append(CSV_THREAD_COUNT2);
             text.append(delim);
         }
 
         if (saveConfig.saveUrl()) {
             text.append(CSV_URL);
             text.append(delim);
         }
 
         if (saveConfig.saveFileName()) {
             text.append(CSV_FILENAME);
             text.append(delim);
         }
 
         if (saveConfig.saveLatency()) {
             text.append(CSV_LATENCY);
             text.append(delim);
         }
 
         if (saveConfig.saveEncoding()) {
             text.append(CSV_ENCODING);
             text.append(delim);
         }
 
         if (saveConfig.saveSampleCount()) {
             text.append(CSV_SAMPLE_COUNT);
             text.append(delim);
             text.append(CSV_ERROR_COUNT);
             text.append(delim);
         }
 
         if (saveConfig.saveHostname()) {
             text.append(CSV_HOSTNAME);
             text.append(delim);
         }
 
         if (saveConfig.saveIdleTime()) {
             text.append(CSV_IDLETIME);
             text.append(delim);
         }
 
         if (saveConfig.saveConnectTime()) {
             text.append(CSV_CONNECT_TIME);
             text.append(delim);
         }
 
         for (int i = 0; i < SampleEvent.getVarCount(); i++) {
             text.append(VARIABLE_NAME_QUOTE_CHAR);
             text.append(SampleEvent.getVarName(i));
             text.append(VARIABLE_NAME_QUOTE_CHAR);
             text.append(delim);
         }
 
         String resultString = null;
         int size = text.length();
         int delSize = delim.length();
 
         // Strip off the trailing delimiter
         if (size >= delSize) {
             resultString = text.substring(0, size - delSize);
         } else {
             resultString = text.toString();
         }
         return resultString;
     }
 
     // Map header names to set() methods
     private static final LinkedMap headerLabelMethods = new LinkedMap();
 
     // These entries must be in the same order as columns are saved/restored.
 
     static {
         headerLabelMethods.put(TIME_STAMP, new Functor("setTimestamp"));
         headerLabelMethods.put(CSV_ELAPSED, new Functor("setTime"));
         headerLabelMethods.put(LABEL, new Functor("setLabel"));
         headerLabelMethods.put(RESPONSE_CODE, new Functor("setCode"));
         headerLabelMethods.put(RESPONSE_MESSAGE, new Functor("setMessage"));
         headerLabelMethods.put(THREAD_NAME, new Functor("setThreadName"));
         headerLabelMethods.put(DATA_TYPE, new Functor("setDataType"));
         headerLabelMethods.put(SUCCESSFUL, new Functor("setSuccess"));
         headerLabelMethods.put(FAILURE_MESSAGE, new Functor(
                 "setAssertionResultsFailureMessage"));
         headerLabelMethods.put(CSV_BYTES, new Functor("setBytes"));
+        headerLabelMethods.put(CSV_SENT_BYTES, new Functor("setSentBytes"));
         // Both these are needed in the list even though they set the same
         // variable
         headerLabelMethods.put(CSV_THREAD_COUNT1,
                 new Functor("setThreadCounts"));
         headerLabelMethods.put(CSV_THREAD_COUNT2,
                 new Functor("setThreadCounts"));
         headerLabelMethods.put(CSV_URL, new Functor("setUrl"));
         headerLabelMethods.put(CSV_FILENAME, new Functor("setFileName"));
         headerLabelMethods.put(CSV_LATENCY, new Functor("setLatency"));
         headerLabelMethods.put(CSV_ENCODING, new Functor("setEncoding"));
         // Both these are needed in the list even though they set the same
         // variable
         headerLabelMethods.put(CSV_SAMPLE_COUNT, new Functor("setSampleCount"));
         headerLabelMethods.put(CSV_ERROR_COUNT, new Functor("setSampleCount"));
         headerLabelMethods.put(CSV_HOSTNAME, new Functor("setHostname"));
         headerLabelMethods.put(CSV_IDLETIME, new Functor("setIdleTime"));
         headerLabelMethods.put(CSV_CONNECT_TIME, new Functor("setConnectTime"));
     }
 
     /**
      * Parse a CSV header line
      * 
      * @param headerLine
      *            from CSV file
      * @param filename
      *            name of file (for log message only)
      * @return config corresponding to the header items found or null if not a
      *         header line
      */
     public static SampleSaveConfiguration getSampleSaveConfiguration(
             String headerLine, String filename) {
         String[] parts = splitHeader(headerLine, _saveConfig.getDelimiter()); // Try
                                                                               // default
                                                                               // delimiter
 
         String delim = null;
 
         if (parts == null) {
             Perl5Matcher matcher = JMeterUtils.getMatcher();
             PatternMatcherInput input = new PatternMatcherInput(headerLine);
             Pattern pattern = JMeterUtils.getPatternCache()
             // This assumes the header names are all single words with no spaces
             // word followed by 0 or more repeats of (non-word char + word)
             // where the non-word char (\2) is the same
             // e.g. abc|def|ghi but not abd|def~ghi
                     .getPattern("\\w+((\\W)\\w+)?(\\2\\w+)*(\\2\"\\w+\")*", // $NON-NLS-1$
                             // last entries may be quoted strings
                             Perl5Compiler.READ_ONLY_MASK);
             if (matcher.matches(input, pattern)) {
                 delim = matcher.getMatch().group(2);
                 parts = splitHeader(headerLine, delim);// now validate the
                                                        // result
             }
         }
 
         if (parts == null) {
             return null; // failed to recognise the header
         }
 
         // We know the column names all exist, so create the config
         SampleSaveConfiguration saveConfig = new SampleSaveConfiguration(false);
 
         int varCount = 0;
         for (String label : parts) {
             if (isVariableName(label)) {
                 varCount++;
             } else {
                 Functor set = (Functor) headerLabelMethods.get(label);
                 set.invoke(saveConfig, new Boolean[]{Boolean.TRUE});
             }
         }
 
         if (delim != null) {
             log.warn("Default delimiter '" + _saveConfig.getDelimiter()
                     + "' did not work; using alternate '" + delim
                     + "' for reading " + filename);
             saveConfig.setDelimiter(delim);
         }
 
         saveConfig.setVarCount(varCount);
 
         return saveConfig;
     }
 
     private static String[] splitHeader(String headerLine, String delim) {
         String[] parts = headerLine.split("\\Q" + delim);// $NON-NLS-1$
         int previous = -1;
         // Check if the line is a header
         for (int i = 0; i < parts.length; i++) {
             final String label = parts[i];
             // Check for Quoted variable names
             if (isVariableName(label)) {
                 previous = Integer.MAX_VALUE; // they are always last
                 continue;
             }
             int current = headerLabelMethods.indexOf(label);
             if (current == -1) {
                 log.warn("Unknown column name " + label);
                 return null; // unknown column name
             }
             if (current <= previous) {
                 log.warn("Column header number " + (i + 1) + " name " + label
                         + " is out of order.");
                 return null; // out of order
             }
             previous = current;
         }
         return parts;
     }
 
     /**
      * Check if the label is a variable name, i.e. is it enclosed in
      * double-quotes?
      * 
      * @param label
      *            column name from CSV file
      * @return if the label is enclosed in double-quotes
      */
     private static boolean isVariableName(final String label) {
         return label.length() > 2 && label.startsWith(VARIABLE_NAME_QUOTE_CHAR)
                 && label.endsWith(VARIABLE_NAME_QUOTE_CHAR);
     }
 
     /**
      * Method will save aggregate statistics as CSV. For now I put it here. Not
      * sure if it should go in the newer SaveService instead of here. if we ever
      * decide to get rid of this class, we'll need to move this method to the
      * new save service.
      * 
      * @param data
      *            List of data rows
      * @param writer
      *            output file
      * @throws IOException
      *             when writing to <code>writer</code> fails
      */
     public static void saveCSVStats(List<?> data, FileWriter writer)
             throws IOException {
         saveCSVStats(data, writer, null);
     }
 
     /**
      * Method will save aggregate statistics as CSV. For now I put it here. Not
      * sure if it should go in the newer SaveService instead of here. if we ever
      * decide to get rid of this class, we'll need to move this method to the
      * new save service.
      * 
      * @param data
      *            List of data rows
      * @param writer
      *            output file
      * @param headers
      *            header names (if non-null)
      * @throws IOException
      *             when writing to <code>writer</code> fails
      */
     public static void saveCSVStats(List<?> data, FileWriter writer,
             String[] headers) throws IOException {
         final char DELIM = ',';
         final char[] SPECIALS = new char[] { DELIM, QUOTING_CHAR };
         if (headers != null) {
             for (int i = 0; i < headers.length; i++) {
                 if (i > 0) {
                     writer.write(DELIM);
                 }
                 writer.write(quoteDelimiters(headers[i], SPECIALS));
             }
             writer.write(LINE_SEP);
         }
         for (Object o : data) {
             List<?> row = (List<?>) o;
             for (int idy = 0; idy < row.size(); idy++) {
                 if (idy > 0) {
                     writer.write(DELIM);
                 }
                 Object item = row.get(idy);
                 writer.write(quoteDelimiters(String.valueOf(item), SPECIALS));
             }
             writer.write(LINE_SEP);
         }
     }
 
     /**
      * Method saves aggregate statistics (with header names) as CSV from a table
      * model. Same as {@link #saveCSVStats(List, FileWriter, String[])} except
      * that there is no need to create a List containing the data.
      * 
      * @param model
      *            table model containing the data
      * @param writer
      *            output file
      * @throws IOException
      *             when writing to <code>writer</code> fails
      */
     public static void saveCSVStats(DefaultTableModel model, FileWriter writer)
             throws IOException {
         saveCSVStats(model, writer, true);
     }
 
     /**
      * Method saves aggregate statistics as CSV from a table model. Same as
      * {@link #saveCSVStats(List, FileWriter, String[])} except that there is no
      * need to create a List containing the data.
      * 
      * @param model
      *            table model containing the data
      * @param writer
      *            output file
      * @param saveHeaders
      *            whether or not to save headers
      * @throws IOException
      *             when writing to <code>writer</code> fails
      */
     public static void saveCSVStats(DefaultTableModel model, FileWriter writer,
             boolean saveHeaders) throws IOException {
         final char DELIM = ',';
         final char[] SPECIALS = new char[] { DELIM, QUOTING_CHAR };
         final int columns = model.getColumnCount();
         final int rows = model.getRowCount();
         if (saveHeaders) {
             for (int i = 0; i < columns; i++) {
                 if (i > 0) {
                     writer.write(DELIM);
                 }
                 writer.write(quoteDelimiters(model.getColumnName(i), SPECIALS));
             }
             writer.write(LINE_SEP);
         }
         for (int row = 0; row < rows; row++) {
             for (int column = 0; column < columns; column++) {
                 if (column > 0) {
                     writer.write(DELIM);
                 }
                 Object item = model.getValueAt(row, column);
                 writer.write(quoteDelimiters(String.valueOf(item), SPECIALS));
             }
             writer.write(LINE_SEP);
         }
     }
 
     /**
      * Convert a result into a string, where the fields of the result are
      * separated by the default delimiter.
      * 
      * @param event
      *            the sample event to be converted
      * @return the separated value representation of the result
      */
     public static String resultToDelimitedString(SampleEvent event) {
         return resultToDelimitedString(event, event.getResult().getSaveConfig()
                 .getDelimiter());
     }
     
     /*
      * Class to handle generating the delimited string. - adds the delimiter
      * if not the first call - quotes any strings that require it
      */
     static final class StringQuoter {
         private final StringBuilder sb;
         private final char[] specials;
         private boolean addDelim;
 
         public StringQuoter(char delim) {
             sb = new StringBuilder(150);
             specials = new char[] { delim, QUOTING_CHAR, CharUtils.CR,
                     CharUtils.LF };
             addDelim = false; // Don't add delimiter first time round
         }
 
         private void addDelim() {
             if (addDelim) {
                 sb.append(specials[0]);
             } else {
                 addDelim = true;
             }
         }
 
         // These methods handle parameters that could contain delimiters or
         // quotes:
         public void append(String s) {
             addDelim();
             // if (s == null) return;
             sb.append(quoteDelimiters(s, specials));
         }
 
         public void append(Object obj) {
             append(String.valueOf(obj));
         }
 
         // These methods handle parameters that cannot contain delimiters or
         // quotes
         public void append(int i) {
             addDelim();
             sb.append(i);
         }
 
         public void append(long l) {
             addDelim();
             sb.append(l);
         }
 
         public void append(boolean b) {
             addDelim();
             sb.append(b);
         }
 
         @Override
         public String toString() {
             return sb.toString();
         }
     }
 
     /**
      * Convert a result into a string, where the fields of the result are
      * separated by a specified String.
      * 
      * @param event
      *            the sample event to be converted
      * @param delimiter
      *            the separation string
      * @return the separated value representation of the result
      */
     public static String resultToDelimitedString(SampleEvent event,
             final String delimiter) {
         StringQuoter text = new StringQuoter(delimiter.charAt(0));
 
         SampleResult sample = event.getResult();
         SampleSaveConfiguration saveConfig = sample.getSaveConfig();
 
         if (saveConfig.saveTimestamp()) {
             if (saveConfig.printMilliseconds()) {
                 text.append(sample.getTimeStamp());
             } else if (saveConfig.formatter() != null) {
                 String stamp = saveConfig.formatter().format(
                         new Date(sample.getTimeStamp()));
                 text.append(stamp);
             }
         }
 
         if (saveConfig.saveTime()) {
             text.append(sample.getTime());
         }
 
         if (saveConfig.saveLabel()) {
             text.append(sample.getSampleLabel());
         }
 
         if (saveConfig.saveCode()) {
             text.append(sample.getResponseCode());
         }
 
         if (saveConfig.saveMessage()) {
             text.append(sample.getResponseMessage());
         }
 
         if (saveConfig.saveThreadName()) {
             text.append(sample.getThreadName());
         }
 
         if (saveConfig.saveDataType()) {
             text.append(sample.getDataType());
         }
 
         if (saveConfig.saveSuccess()) {
             text.append(sample.isSuccessful());
         }
 
         if (saveConfig.saveAssertionResultsFailureMessage()) {
             String message = null;
             AssertionResult[] results = sample.getAssertionResults();
 
             if (results != null) {
                 // Find the first non-null message
                 for (AssertionResult result : results) {
                     message = result.getFailureMessage();
                     if (message != null) {
                         break;
                     }
                 }
             }
 
             if (message != null) {
                 text.append(message);
             } else {
                 text.append(""); // Need to append something so delimiter is
                                  // added
             }
         }
 
         if (saveConfig.saveBytes()) {
             text.append(sample.getBytes());
         }
+        
+        if (saveConfig.saveSentBytes()) {
+            text.append(sample.getSentBytes());
+        }
 
         if (saveConfig.saveThreadCounts()) {
             text.append(sample.getGroupThreads());
             text.append(sample.getAllThreads());
         }
         if (saveConfig.saveUrl()) {
             text.append(sample.getURL());
         }
 
         if (saveConfig.saveFileName()) {
             text.append(sample.getResultFileName());
         }
 
         if (saveConfig.saveLatency()) {
             text.append(sample.getLatency());
         }
 
         if (saveConfig.saveEncoding()) {
             text.append(sample.getDataEncodingWithDefault());
         }
 
         if (saveConfig.saveSampleCount()) {
             // Need both sample and error count to be any use
             text.append(sample.getSampleCount());
             text.append(sample.getErrorCount());
         }
 
         if (saveConfig.saveHostname()) {
             text.append(event.getHostname());
         }
 
         if (saveConfig.saveIdleTime()) {
             text.append(event.getResult().getIdleTime());
         }
 
         if (saveConfig.saveConnectTime()) {
             text.append(sample.getConnectTime());
         }
 
         for (int i = 0; i < SampleEvent.getVarCount(); i++) {
             text.append(event.getVarValue(i));
         }
 
         return text.toString();
     }
 
     // =================================== CSV quote/unquote handling
     // ==============================
 
     /*
      * Private versions of what might eventually be part of Commons-CSV or
      * Commons-Lang/Io...
      */
 
     /**
      * <p> Returns a <code>String</code> value for a character-delimited column
      * value enclosed in the quote character, if required. </p>
      * 
      * <p> If the value contains a special character, then the String value is
      * returned enclosed in the quote character. </p>
      * 
      * <p> Any quote characters in the value are doubled up. </p>
      * 
      * <p> If the value does not contain any special characters, then the String
      * value is returned unchanged. </p>
      * 
      * <p> N.B. The list of special characters includes the quote character.
      * </p>
      * 
      * @param input the input column String, may be null (without enclosing
      * delimiters)
      * 
      * @param specialChars special characters; second one must be the quote
      * character
      * 
      * @return the input String, enclosed in quote characters if the value
      * contains a special character, <code>null</code> for null string input
      */
     public static String quoteDelimiters(String input, char[] specialChars) {
         if (StringUtils.containsNone(input, specialChars)) {
             return input;
         }
         StringBuilder buffer = new StringBuilder(input.length() + 10);
         final char quote = specialChars[1];
         buffer.append(quote);
         for (int i = 0; i < input.length(); i++) {
             char c = input.charAt(i);
             if (c == quote) {
                 buffer.append(quote); // double the quote char
             }
             buffer.append(c);
         }
         buffer.append(quote);
         return buffer.toString();
     }
 
     // State of the parser
     private enum ParserState {INITIAL, PLAIN, QUOTED, EMBEDDEDQUOTE}
 
     public static final char QUOTING_CHAR = '"';
 
     /**
      * Reads from file and splits input into strings according to the delimiter,
      * taking note of quoted strings.
      * <p>
      * Handles DOS (CRLF), Unix (LF), and Mac (CR) line-endings equally.
      * <p>
      * A blank line - or a quoted blank line - both return an array containing
      * a single empty String.
      * @param infile
      *            input file - must support mark(1)
      * @param delim
      *            delimiter (e.g. comma)
      * @return array of strings, will be empty if there is no data, i.e. if the input is at EOF.
      * @throws IOException
      *             also for unexpected quote characters
      */
     public static String[] csvReadFile(BufferedReader infile, char delim)
             throws IOException {
         int ch;
         ParserState state = ParserState.INITIAL;
         List<String> list = new ArrayList<>();
         CharArrayWriter baos = new CharArrayWriter(200);
         boolean push = false;
         while (-1 != (ch = infile.read())) {
             push = false;
             switch (state) {
             case INITIAL:
                 if (ch == QUOTING_CHAR) {
                     state = ParserState.QUOTED;
                 } else if (isDelimOrEOL(delim, ch)) {
                     push = true;
                 } else {
                     baos.write(ch);
                     state = ParserState.PLAIN;
                 }
                 break;
             case PLAIN:
                 if (ch == QUOTING_CHAR) {
                     baos.write(ch);
                     throw new IOException(
                             "Cannot have quote-char in plain field:["
                                     + baos.toString() + "]");
                 } else if (isDelimOrEOL(delim, ch)) {
                     push = true;
                     state = ParserState.INITIAL;
                 } else {
                     baos.write(ch);
                 }
                 break;
             case QUOTED:
                 if (ch == QUOTING_CHAR) {
                     state = ParserState.EMBEDDEDQUOTE;
                 } else {
                     baos.write(ch);
                 }
                 break;
             case EMBEDDEDQUOTE:
                 if (ch == QUOTING_CHAR) {
                     baos.write(QUOTING_CHAR); // doubled quote => quote
                     state = ParserState.QUOTED;
                 } else if (isDelimOrEOL(delim, ch)) {
                     push = true;
                     state = ParserState.INITIAL;
                 } else {
                     baos.write(QUOTING_CHAR);
                     throw new IOException(
                             "Cannot have single quote-char in quoted field:["
                                     + baos.toString() + "]");
                 }
                 break;
             default:
                 throw new IllegalStateException("Unexpected state " + state);
             } // switch(state)
             if (push) {
                 if (ch == '\r') {// Remove following \n if present
                     infile.mark(1);
                     if (infile.read() != '\n') {
                         infile.reset(); // did not find \n, put the character
                                         // back
                     }
                 }
                 String s = baos.toString();
                 list.add(s);
                 baos.reset();
             }
             if ((ch == '\n' || ch == '\r') && state != ParserState.QUOTED) {
                 break;
             }
         } // while not EOF
         if (ch == -1) {// EOF (or end of string) so collect any remaining data
             if (state == ParserState.QUOTED) {
                 throw new IOException("Missing trailing quote-char in quoted field:[\""
                         + baos.toString() + "]");
             }
             // Do we have some data, or a trailing empty field?
             if (baos.size() > 0 // we have some data
                     || push // we've started a field
                     || state == ParserState.EMBEDDEDQUOTE // Just seen ""
             ) {
                 list.add(baos.toString());
             }
         }
         return list.toArray(new String[list.size()]);
     }
 
     private static boolean isDelimOrEOL(char delim, int ch) {
         return ch == delim || ch == '\n' || ch == '\r';
     }
 
     /**
      * Reads from String and splits into strings according to the delimiter,
      * taking note of quoted strings.
      * 
      * Handles DOS (CRLF), Unix (LF), and Mac (CR) line-endings equally.
      * 
      * @param line
      *            input line - not {@code null}
      * @param delim
      *            delimiter (e.g. comma)
      * @return array of strings
      * @throws IOException
      *             also for unexpected quote characters
      */
     public static String[] csvSplitString(String line, char delim)
             throws IOException {
         return csvReadFile(new BufferedReader(new StringReader(line)), delim);
     }
 }
diff --git a/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java b/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
index f5c92d432..9fb9a91a0 100644
--- a/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
+++ b/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
@@ -1,479 +1,484 @@
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
 
 package org.apache.jmeter.save.converters;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.UnsupportedEncodingException;
 import java.net.URL;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.Converter;
 import org.apache.log.Logger;
 
 import com.thoughtworks.xstream.converters.MarshallingContext;
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 import com.thoughtworks.xstream.mapper.Mapper;
 
 /**
  * XStream Converter for the SampleResult class
  */
 public class SampleResultConverter extends AbstractCollectionConverter {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String JAVA_LANG_STRING = "java.lang.String"; //$NON-NLS-1$
     private static final String ATT_CLASS = "class"; //$NON-NLS-1$
 
     // Element tags. Must be unique. Keep sorted.
     protected static final String TAG_COOKIES           = "cookies";          //$NON-NLS-1$
     protected static final String TAG_METHOD            = "method";           //$NON-NLS-1$
     protected static final String TAG_QUERY_STRING      = "queryString";      //$NON-NLS-1$
     protected static final String TAG_REDIRECT_LOCATION = "redirectLocation"; //$NON-NLS-1$
     protected static final String TAG_REQUEST_HEADER    = "requestHeader";    //$NON-NLS-1$
 
     //NOT USED protected   static final String TAG_URL               = "requestUrl";       //$NON-NLS-1$
 
     protected static final String TAG_RESPONSE_DATA     = "responseData";     //$NON-NLS-1$
     protected static final String TAG_RESPONSE_HEADER   = "responseHeader";   //$NON-NLS-1$
     protected static final String TAG_SAMPLER_DATA      = "samplerData";      //$NON-NLS-1$
     protected static final String TAG_RESPONSE_FILE     = "responseFile";     //$NON-NLS-1$
 
     // samplerData attributes. Must be unique. Keep sorted by string value.
     // Ensure the Listener documentation is updated when new attributes are added
     private static final String ATT_BYTES             = "by"; //$NON-NLS-1$
+    private static final String ATT_SENT_BYTES        = "sby"; //$NON-NLS-1$
     private static final String ATT_DATA_ENCODING     = "de"; //$NON-NLS-1$
     private static final String ATT_DATA_TYPE         = "dt"; //$NON-NLS-1$
     private static final String ATT_ERROR_COUNT       = "ec"; //$NON-NLS-1$
     private static final String ATT_HOSTNAME          = "hn"; //$NON-NLS-1$
     private static final String ATT_LABEL             = "lb"; //$NON-NLS-1$
     private static final String ATT_LATENCY           = "lt"; //$NON-NLS-1$
     private static final String ATT_CONNECT_TIME      = "ct"; //$NON-NLS-1$
 
     private static final String ATT_ALL_THRDS         = "na"; //$NON-NLS-1$
     private static final String ATT_GRP_THRDS         = "ng"; //$NON-NLS-1$
 
     // N.B. Originally the response code was saved with the code "rs"
     // but retrieved with the code "rc". Changed to always use "rc", but
     // allow for "rs" when restoring values.
     private static final String ATT_RESPONSE_CODE     = "rc"; //$NON-NLS-1$
     private static final String ATT_RESPONSE_MESSAGE  = "rm"; //$NON-NLS-1$
     private static final String ATT_RESPONSE_CODE_OLD = "rs"; //$NON-NLS-1$
 
     private static final String ATT_SUCCESS           = "s";  //$NON-NLS-1$
     private static final String ATT_SAMPLE_COUNT      = "sc"; //$NON-NLS-1$
     private static final String ATT_TIME              = "t";  //$NON-NLS-1$
     private static final String ATT_IDLETIME          = "it"; //$NON-NLS-1$
     private static final String ATT_THREADNAME        = "tn"; //$NON-NLS-1$
     private static final String ATT_TIME_STAMP        = "ts"; //$NON-NLS-1$
 
     /**
      * Returns the converter version; used to check for possible
      * incompatibilities
      * 
      * @return the version of this converter
      */
     public static String getVersion() {
         return "$Revision$"; //$NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) { // superclass does not use types
         return SampleResult.class.equals(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
         SampleResult res = (SampleResult) obj;
         SampleSaveConfiguration save = res.getSaveConfig();
         setAttributes(writer, context, res, save);
         saveAssertions(writer, context, res, save);
         saveSubResults(writer, context, res, save);
         saveResponseHeaders(writer, context, res, save);
         saveRequestHeaders(writer, context, res, save);
         saveResponseData(writer, context, res, save);
         saveSamplerData(writer, context, res, save);
     }
 
     /**
      * Save the data of the sample result to a stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveSamplerData(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveSamplerData(res)) {
             writeString(writer, TAG_SAMPLER_DATA, res.getSamplerData());
         }
         if (save.saveUrl()) {
             final URL url = res.getURL();
             if (url != null) {
                 writeItem(url, context, writer);
             }
         }
     }
 
     /**
      * Save the response from the sample result into the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveResponseData(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveResponseData(res)) {
             writer.startNode(TAG_RESPONSE_DATA);
             writer.addAttribute(ATT_CLASS, JAVA_LANG_STRING);
             try {
                 if (SampleResult.TEXT.equals(res.getDataType())){
                     writer.setValue(new String(res.getResponseData(), res.getDataEncodingWithDefault()));
                 } else {
                     writer.setValue("Non-TEXT response data, cannot record: (" + res.getDataType() + ")");                    
                 }
                 // Otherwise don't save anything - no point
             } catch (UnsupportedEncodingException e) {
                 writer.setValue("Unsupported encoding in response data, cannot record: " + e);
             }
             writer.endNode();
         }
         if (save.saveFileName()){
             writer.startNode(TAG_RESPONSE_FILE);
             writer.addAttribute(ATT_CLASS, JAVA_LANG_STRING);
             writer.setValue(res.getResultFileName());
             writer.endNode();
         }
     }
 
     /**
      * Save request headers from the sample result into the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveRequestHeaders(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveRequestHeaders()) {
             writeString(writer, TAG_REQUEST_HEADER, res.getRequestHeaders());
         }
     }
 
     /**
      * Save response headers from sample result into the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveResponseHeaders(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveResponseHeaders()) {
             writeString(writer, TAG_RESPONSE_HEADER, res.getResponseHeaders());
         }
     }
 
     /**
      * Save sub results from sample result into the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveSubResults(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveSubresults()) {
             SampleResult[] subResults = res.getSubResults();
             for (SampleResult subResult : subResults) {
                 subResult.setSaveConfig(save);
                 writeItem(subResult, context, writer);
             }
         }
     }
 
     /**
      * Save assertion results from the sample result into the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveAssertions(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveAssertions()) {
             AssertionResult[] assertionResults = res.getAssertionResults();
             for (AssertionResult assertionResult : assertionResults) {
                 writeItem(assertionResult, context, writer);
             }
         }
     }
 
     /**
      * Save attributes of the sample result to the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void setAttributes(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveTime()) {
             writer.addAttribute(ATT_TIME, Long.toString(res.getTime()));
         }
         if (save.saveIdleTime()) {
             writer.addAttribute(ATT_IDLETIME, Long.toString(res.getIdleTime()));
         }
         if (save.saveLatency()) {
             writer.addAttribute(ATT_LATENCY, Long.toString(res.getLatency()));
         }
         if (save.saveConnectTime()) {
             writer.addAttribute(ATT_CONNECT_TIME, Long.toString(res.getConnectTime()));
         }
         if (save.saveTimestamp()) {
             writer.addAttribute(ATT_TIME_STAMP, Long.toString(res.getTimeStamp()));
         }
         if (save.saveSuccess()) {
             writer.addAttribute(ATT_SUCCESS, Boolean.toString(res.isSuccessful()));
         }
         if (save.saveLabel()) {
             writer.addAttribute(ATT_LABEL, ConversionHelp.encode(res.getSampleLabel()));
         }
         if (save.saveCode()) {
             writer.addAttribute(ATT_RESPONSE_CODE, ConversionHelp.encode(res.getResponseCode()));
         }
         if (save.saveMessage()) {
             writer.addAttribute(ATT_RESPONSE_MESSAGE, ConversionHelp.encode(res.getResponseMessage()));
         }
         if (save.saveThreadName()) {
             writer.addAttribute(ATT_THREADNAME, ConversionHelp.encode(res.getThreadName()));
         }
         if (save.saveDataType()) {
             writer.addAttribute(ATT_DATA_TYPE, ConversionHelp.encode(res.getDataType()));
         }
         if (save.saveEncoding()) {
             writer.addAttribute(ATT_DATA_ENCODING, ConversionHelp.encode(res.getDataEncodingNoDefault()));
         }
         if (save.saveBytes()) {
             writer.addAttribute(ATT_BYTES, String.valueOf(res.getBytes()));
         }
+        if (save.saveSentBytes()) {
+            writer.addAttribute(ATT_SENT_BYTES, String.valueOf(res.getSentBytes()));
+        }
         if (save.saveSampleCount()){
             writer.addAttribute(ATT_SAMPLE_COUNT, String.valueOf(res.getSampleCount()));
             writer.addAttribute(ATT_ERROR_COUNT, String.valueOf(res.getErrorCount()));
         }
         if (save.saveThreadCounts()){
            writer.addAttribute(ATT_GRP_THRDS, String.valueOf(res.getGroupThreads()));
            writer.addAttribute(ATT_ALL_THRDS, String.valueOf(res.getAllThreads()));
         }
         SampleEvent event = (SampleEvent) context.get(SaveService.SAMPLE_EVENT_OBJECT);
         if (event != null) {
             if (save.saveHostname()){
                 writer.addAttribute(ATT_HOSTNAME, event.getHostname());
             }
             for (int i = 0; i < SampleEvent.getVarCount(); i++){
                writer.addAttribute(SampleEvent.getVarName(i), ConversionHelp.encode(event.getVarValue(i)));
             }
         }
     }
 
     /**
      * Write a tag with a content of <code>value</code> to the
      * <code>writer</code>
      * 
      * @param writer
      *            writer to write the tag into
      * @param tag
      *            name of the tag to use
      * @param value
      *            content for tag
      */
     protected void writeString(HierarchicalStreamWriter writer, String tag, String value) {
         if (value != null) {
             writer.startNode(tag);
             writer.addAttribute(ATT_CLASS, JAVA_LANG_STRING);
             writer.setValue(value);
             writer.endNode();
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
         SampleResult res = (SampleResult) createCollection(context.getRequiredType());
         retrieveAttributes(reader, context, res);
         while (reader.hasMoreChildren()) {
             reader.moveDown();
             Object subItem = readItem(reader, context, res);
             retrieveItem(reader, context, res, subItem);
             reader.moveUp();
         }
 
         // If we have a file, but no data, then read the file
         String resultFileName = res.getResultFileName();
         if (resultFileName.length()>0
         &&  res.getResponseData().length == 0) {
             readFile(resultFileName,res);
         }
         return res;
     }
 
     /**
      *
      * @param reader stream from which the objects should be read
      * @param context context for xstream to allow nested objects
      * @param res sample result into which the information should be retrieved
      * @param subItem sub item which should be added into <code>res</code>
      * @return <code>true</code> if the item was processed (for HTTPResultConverter)
      */
     protected boolean retrieveItem(HierarchicalStreamReader reader, UnmarshallingContext context, SampleResult res,
             Object subItem) {
         String nodeName = reader.getNodeName();
         if (subItem instanceof AssertionResult) {
             res.addAssertionResult((AssertionResult) subItem);
         } else if (subItem instanceof SampleResult) {
             res.storeSubResult((SampleResult) subItem);
         } else if (nodeName.equals(TAG_RESPONSE_HEADER)) {
             res.setResponseHeaders((String) subItem);
         } else if (nodeName.equals(TAG_REQUEST_HEADER)) {
             res.setRequestHeaders((String) subItem);
         } else if (nodeName.equals(TAG_RESPONSE_DATA)) {
             final String responseData = (String) subItem;
             if (responseData.length() > 0) {
                 final String dataEncoding = res.getDataEncodingWithDefault();
                 try {
                     res.setResponseData(responseData.getBytes(dataEncoding));
                 } catch (UnsupportedEncodingException e) {
                     res.setResponseData(("Can't support the char set: " + dataEncoding), null);
                     res.setDataType(SampleResult.TEXT);
                 }
             }
         } else if (nodeName.equals(TAG_SAMPLER_DATA)) {
             res.setSamplerData((String) subItem);
         } else if (nodeName.equals(TAG_RESPONSE_FILE)) {
             res.setResultFileName((String) subItem);
         // Don't try restoring the URL TODO: why not?
         } else {
             return false;
         }
         return true;
     }
 
     /**
      * @param reader stream to read objects from
      * @param context context for xstream to allow nested objects
      * @param res sample result on which the attributes should be set
      */
     protected void retrieveAttributes(HierarchicalStreamReader reader, UnmarshallingContext context, SampleResult res) {
         res.setSampleLabel(ConversionHelp.decode(reader.getAttribute(ATT_LABEL)));
         res.setDataEncoding(ConversionHelp.decode(reader.getAttribute(ATT_DATA_ENCODING)));
         res.setDataType(ConversionHelp.decode(reader.getAttribute(ATT_DATA_TYPE)));
         String oldrc=reader.getAttribute(ATT_RESPONSE_CODE_OLD);
         if (oldrc!=null) {
             res.setResponseCode(ConversionHelp.decode(oldrc));
         } else {
             res.setResponseCode(ConversionHelp.decode(reader.getAttribute(ATT_RESPONSE_CODE)));
         }
         res.setResponseMessage(ConversionHelp.decode(reader.getAttribute(ATT_RESPONSE_MESSAGE)));
         res.setSuccessful(Converter.getBoolean(reader.getAttribute(ATT_SUCCESS), true));
         res.setThreadName(ConversionHelp.decode(reader.getAttribute(ATT_THREADNAME)));
         res.setStampAndTime(Converter.getLong(reader.getAttribute(ATT_TIME_STAMP)),
                 Converter.getLong(reader.getAttribute(ATT_TIME)));
         res.setIdleTime(Converter.getLong(reader.getAttribute(ATT_IDLETIME)));
         res.setLatency(Converter.getLong(reader.getAttribute(ATT_LATENCY)));
         res.setConnectTime(Converter.getLong(reader.getAttribute(ATT_CONNECT_TIME)));
         res.setBytes(Converter.getInt(reader.getAttribute(ATT_BYTES)));
+        res.setSentBytes(Converter.getLong(reader.getAttribute(ATT_SENT_BYTES)));
         res.setSampleCount(Converter.getInt(reader.getAttribute(ATT_SAMPLE_COUNT),1)); // default is 1
         res.setErrorCount(Converter.getInt(reader.getAttribute(ATT_ERROR_COUNT),0)); // default is 0
         res.setGroupThreads(Converter.getInt(reader.getAttribute(ATT_GRP_THRDS)));
         res.setAllThreads(Converter.getInt(reader.getAttribute(ATT_ALL_THRDS)));
     }
 
     protected void readFile(String resultFileName, SampleResult res) {
         File in = new File(resultFileName);
         try (FileInputStream fis = new FileInputStream(in);
                 BufferedInputStream bis = new BufferedInputStream(fis)){
             ByteArrayOutputStream outstream = new ByteArrayOutputStream(res.getBytes());
             byte[] buffer = new byte[4096];
             int len;
             while ((len = bis.read(buffer)) > 0) {
                 outstream.write(buffer, 0, len);
             }
             outstream.close();
             res.setResponseData(outstream.toByteArray());
         } catch (IOException e) {
             log.warn(e.getLocalizedMessage());
         } 
     }
 
 
     /**
      * @param arg0 the mapper
      */
     public SampleResultConverter(Mapper arg0) {
         super(arg0);
     }
 }
diff --git a/src/core/org/apache/jmeter/save/converters/SampleSaveConfigurationConverter.java b/src/core/org/apache/jmeter/save/converters/SampleSaveConfigurationConverter.java
index fb0c0d34b..82fea7927 100644
--- a/src/core/org/apache/jmeter/save/converters/SampleSaveConfigurationConverter.java
+++ b/src/core/org/apache/jmeter/save/converters/SampleSaveConfigurationConverter.java
@@ -1,176 +1,179 @@
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
 
 package org.apache.jmeter.save.converters;
 
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 
 import com.thoughtworks.xstream.converters.MarshallingContext;
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
 import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
 import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
 import com.thoughtworks.xstream.core.JVM;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 import com.thoughtworks.xstream.mapper.Mapper;
 import com.thoughtworks.xstream.mapper.MapperWrapper;
 
 /*
  * Allow new fields to be added to the SampleSaveConfiguration without
  * changing the output JMX file unless it is necessary.
  *
  * TODO work out how to make shouldSerializeMember() conditionally return true.
  */
 public class SampleSaveConfigurationConverter  extends ReflectionConverter {
 
     private static final ReflectionProvider rp;
 
     static {
         ReflectionProvider tmp;
         try {
             tmp = JVM.newReflectionProvider();
         } catch (NullPointerException e) {// Bug in above method
             tmp = new PureJavaReflectionProvider();
         }
         rp = tmp;
     }
 
     private static final String TRUE = "true"; // $NON-NLS-1$
 
     // N.B. These must agree with the new member names in SampleSaveConfiguration
     private static final String NODE_FILENAME = "fileName"; // $NON-NLS-1$
     private static final String NODE_HOSTNAME = "hostname"; // $NON-NLS-1$
     private static final String NODE_URL = "url"; // $NON-NLS-1$
     private static final String NODE_BYTES = "bytes"; // $NON-NLS-1$
+    private static final String NODE_SENT_BYTES = "sentBytes"; // $NON-NLS-1$
     private static final String NODE_THREAD_COUNT = "threadCounts"; // $NON-NLS-1$
     private static final String NODE_SAMPLE_COUNT = "sampleCount"; // $NON-NLS-1$
     private static final String NODE_IDLE_TIME = "idleTime"; // $NON-NLS-1$
     private static final String NODE_CONNECT_TIME = "connectTime"; // $NON-NLS-1$
 
     // Additional member names which are currently not written out
     private static final String NODE_DELIMITER = "delimiter"; // $NON-NLS-1$
     private static final String NODE_PRINTMS = "printMilliseconds"; // $NON-NLS-1$
 
 
     static class MyWrapper extends MapperWrapper{
 
         public MyWrapper(Mapper wrapped) {
             super(wrapped);
         }
 
         /** {@inheritDoc} */
         @Override
         public boolean shouldSerializeMember(
                 @SuppressWarnings("rawtypes") // superclass does not use types
                 Class definedIn, 
                 String fieldName) {
             if (SampleSaveConfiguration.class != definedIn) { return true; }
             // These are new fields; not saved unless true
             // This list MUST agree with the list in the marshall() method below
             if (fieldName.equals(NODE_BYTES)) { return false; }
+            if (fieldName.equals(NODE_SENT_BYTES)) { return false; }
             if (fieldName.equals(NODE_URL)) { return false; }
             if (fieldName.equals(NODE_FILENAME)) { return false; }
             if (fieldName.equals(NODE_HOSTNAME)) { return false; }
             if (fieldName.equals(NODE_THREAD_COUNT)) { return false; }
             if (fieldName.equals(NODE_SAMPLE_COUNT)) { return false; }
             if (fieldName.equals(NODE_IDLE_TIME)) { return false; }
             if (fieldName.equals(NODE_CONNECT_TIME)) { return false; }
 
             // These fields are not currently saved or restored
             if (fieldName.equals(NODE_DELIMITER)) { return false; }
             if (fieldName.equals(NODE_PRINTMS)) { return false; }
             return true;
         }
     }
 
     public SampleSaveConfigurationConverter(Mapper arg0) {
         super(new MyWrapper(arg0),rp);
     }
 
     /**
      * Returns the converter version; used to check for possible
      * incompatibilities
      * 
      * @return the version of this converter
      */
     public static String getVersion() {
         return "$Revision$"; // $NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) {
         return SampleSaveConfiguration.class.equals(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
         super.marshal(obj, writer, context); // Save most things
 
         SampleSaveConfiguration prop = (SampleSaveConfiguration) obj;
 
         // Save the new fields - but only if they are true
         // This list MUST agree with the list in MyWrapper#shouldSerializeMember()
         createNode(writer,prop.saveBytes(),NODE_BYTES);
+        createNode(writer,prop.saveSentBytes(),NODE_SENT_BYTES);
         createNode(writer,prop.saveUrl(),NODE_URL);
         createNode(writer,prop.saveFileName(),NODE_FILENAME);
         createNode(writer,prop.saveHostname(),NODE_HOSTNAME);
         createNode(writer,prop.saveThreadCounts(),NODE_THREAD_COUNT);
         createNode(writer,prop.saveSampleCount(),NODE_SAMPLE_COUNT);
         createNode(writer,prop.saveIdleTime(),NODE_IDLE_TIME);
         createNode(writer, prop.saveConnectTime(), NODE_CONNECT_TIME);
     }
 
     // Helper method to simplify marshall routine. Save if and only if true.
     private void createNode(HierarchicalStreamWriter writer, boolean save, String node) {
         if (!save) {
             return;
         }
         writer.startNode(node);
         writer.setValue(TRUE);
         writer.endNode();
     }
 
     /** {@inheritDoc} */
     @Override
     public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
         final Class<SampleSaveConfiguration> thisClass = SampleSaveConfiguration.class;
         final Class<?> requiredType = context.getRequiredType();
         if (requiredType != thisClass) {
             throw new IllegalArgumentException("Unexpected class: "+requiredType.getName());
         }
         // The default for missing tags is false, so preset all the fields accordingly
         SampleSaveConfiguration result = new SampleSaveConfiguration(false);
         // Now pick up any tags from the input file
         while (reader.hasMoreChildren()) {
             reader.moveDown();
             String nn = reader.getNodeName();
             if (!"formatter".equals(nn)){// Skip formatter (if present) bug 42674 $NON-NLS-1$
                 String fieldName = mapper.realMember(thisClass, nn);
                 java.lang.reflect.Field field = reflectionProvider.getField(thisClass,fieldName);
                 Class<?> type = field.getType();
                 Object value = unmarshallField(context, result, type, field);
                 reflectionProvider.writeField(result, nn, value, thisClass);
             }
             reader.moveUp();
         }
         return result;
     }
 }
diff --git a/src/core/org/apache/jmeter/visualizers/TableSample.java b/src/core/org/apache/jmeter/visualizers/TableSample.java
index fac3e8176..fa54da5f3 100644
--- a/src/core/org/apache/jmeter/visualizers/TableSample.java
+++ b/src/core/org/apache/jmeter/visualizers/TableSample.java
@@ -1,154 +1,164 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import java.io.Serializable;
 import java.text.Format;
 import java.util.Date;
 
 /**
  * Class to hold data for the TableVisualiser.
  */
 public class TableSample implements Serializable, Comparable<TableSample> {
     private static final long serialVersionUID = 240L;
 
     private final long totalSamples;
     
     private final int sampleCount; // number of samples in this entry
 
     private final long startTime;
 
     private final String threadName;
     
     private final String label;
 
     private final long elapsed;
 
     private final boolean success;
 
     private final long bytes;
+    
+    private final long sentBytes;
 
     private final long latency;
 
     private final long connect;
 
     /**
      * @deprecated for unit test code only
      */
     @Deprecated
     public TableSample() {
-        this(0, 1, 0, "", "", 0, true, 0, 0, 0);
+        this(0, 1, 0, "", "", 0, true, 0, 0, 0, 0);
     }
 
     public TableSample(long totalSamples, int sampleCount, long startTime, String threadName,
             String label,
-            long elapsed, boolean success, long bytes, long latency, long connect) {
+            long elapsed, boolean success, long bytes, long sentBytes, long latency, long connect) {
         this.totalSamples = totalSamples;
         this.sampleCount = sampleCount;
         this.startTime = startTime;
         this.threadName = threadName;
         this.label = label;
         // SampleCount can be equal to 0, see SubscriberSampler#sample
         this.elapsed = (sampleCount > 0) ? elapsed/sampleCount : 0;
         this.bytes =  (sampleCount > 0) ? bytes/sampleCount : 0;
+        this.sentBytes = (sampleCount > 0) ? sentBytes/sampleCount : 0;
         this.success = success;
         this.latency = latency;
         this.connect = connect;
     }
 
     // The following getters may appear not to be used - however they are invoked via the Functor class
 
     public long getBytes() {
         return bytes;
     }
 
     public String getSampleNumberString(){
         StringBuilder sb = new StringBuilder();
         if (sampleCount > 1) {
             sb.append(totalSamples-sampleCount+1);
             sb.append('-');
         }
         sb.append(totalSamples);
         return sb.toString();
     }
 
     public long getElapsed() {
         return elapsed;
     }
 
     public boolean isSuccess() {
         return success;
     }
 
     public long getStartTime() {
         return startTime;
     }
 
     /**
      * @param format the format to be used on the time
      * @return the start time using the specified format
      * Intended for use from Functors
      */
     public String getStartTimeFormatted(Format format) {
         return format.format(new Date(getStartTime()));
     }
 
     public String getThreadName() {
         return threadName;
     }
 
     public String getLabel() {
         return label;
     }
 
     @Override
     public int compareTo(TableSample o) {
         TableSample oo = o;
         return ((totalSamples - oo.totalSamples) < 0 ? -1 : (totalSamples == oo.totalSamples ? 0 : 1));
     }
 
     // TODO should equals and hashCode depend on field other than count?
     
     @Override
     public boolean equals(Object o){
         return (
                 (o instanceof TableSample) &&
                 (this.compareTo((TableSample) o) == 0)
                 );
     }
 
     @Override
     public int hashCode(){
         return (int)(totalSamples ^ (totalSamples >>> 32));
     }
 
     /**
      * @return the latency
      */
     public long getLatency() {
         return latency;
     }
 
     /**
      * @return the conneect time
      */
     public long getConnectTime() {
         return connect;
     }
+
+    /**
+     * @return the sentBytes
+     */
+    public long getSentBytes() {
+        return sentBytes;
+    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
index 91797cba4..af8966191 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
@@ -1,1441 +1,1442 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.InetAddress;
 import java.net.URI;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.nio.charset.Charset;
 import java.security.PrivilegedActionException;
 import java.security.PrivilegedExceptionAction;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.TimeUnit;
 import java.util.regex.Pattern;
 
 import javax.security.auth.Subject;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.io.input.BoundedInputStream;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.http.Header;
 import org.apache.http.HttpConnection;
 import org.apache.http.HttpConnectionMetrics;
 import org.apache.http.HttpEntity;
 import org.apache.http.HttpException;
 import org.apache.http.HttpHost;
 import org.apache.http.HttpRequest;
 import org.apache.http.HttpRequestInterceptor;
 import org.apache.http.HttpResponse;
 import org.apache.http.HttpResponseInterceptor;
 import org.apache.http.NameValuePair;
 import org.apache.http.StatusLine;
 import org.apache.http.auth.AuthScope;
 import org.apache.http.auth.Credentials;
 import org.apache.http.auth.NTCredentials;
 import org.apache.http.client.ClientProtocolException;
 import org.apache.http.client.CredentialsProvider;
 import org.apache.http.client.HttpClient;
 import org.apache.http.client.HttpRequestRetryHandler;
 import org.apache.http.client.config.CookieSpecs;
 import org.apache.http.client.config.RequestConfig;
 import org.apache.http.client.entity.UrlEncodedFormEntity;
 import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
 import org.apache.http.client.methods.HttpGet;
 import org.apache.http.client.methods.HttpHead;
 import org.apache.http.client.methods.HttpOptions;
 import org.apache.http.client.methods.HttpPatch;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.client.methods.HttpPut;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.http.client.methods.HttpTrace;
 import org.apache.http.client.methods.HttpUriRequest;
 import org.apache.http.client.params.ClientPNames;
 import org.apache.http.client.protocol.HttpClientContext;
 import org.apache.http.client.protocol.ResponseContentEncoding;
 import org.apache.http.conn.ConnectionKeepAliveStrategy;
 import org.apache.http.conn.DnsResolver;
 import org.apache.http.conn.params.ConnRoutePNames;
 import org.apache.http.conn.scheme.PlainSocketFactory;
 import org.apache.http.conn.scheme.Scheme;
 import org.apache.http.conn.scheme.SchemeRegistry;
 import org.apache.http.entity.ContentType;
 import org.apache.http.entity.FileEntity;
 import org.apache.http.entity.StringEntity;
 import org.apache.http.entity.mime.FormBodyPart;
 import org.apache.http.entity.mime.FormBodyPartBuilder;
 import org.apache.http.entity.mime.MIME;
 import org.apache.http.entity.mime.MultipartEntityBuilder;
 import org.apache.http.entity.mime.content.FileBody;
 import org.apache.http.entity.mime.content.StringBody;
 import org.apache.http.impl.client.AbstractHttpClient;
 import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
 import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
 import org.apache.http.impl.client.DefaultHttpClient;
 import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
 import org.apache.http.impl.conn.SystemDefaultDnsResolver;
 import org.apache.http.message.BasicNameValuePair;
 import org.apache.http.message.BufferedHeader;
 import org.apache.http.params.BasicHttpParams;
 import org.apache.http.params.CoreConnectionPNames;
 import org.apache.http.params.CoreProtocolPNames;
 import org.apache.http.params.DefaultedHttpParams;
 import org.apache.http.params.HttpParams;
 import org.apache.http.params.SyncBasicHttpParams;
 import org.apache.http.protocol.BasicHttpContext;
 import org.apache.http.protocol.HTTP;
 import org.apache.http.protocol.HttpContext;
 import org.apache.http.protocol.HttpCoreContext;
 import org.apache.http.util.CharArrayBuffer;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.SlowHC4SocketFactory;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.JsseSSLManager;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HTTP Sampler using Apache HttpClient 4.x.
  *
  */
 public class HTTPHC4Impl extends HTTPHCAbstractImpl {
 
     private static final int MAX_BODY_RETAIN_SIZE = JMeterUtils.getPropDefault("httpclient4.max_body_retain_size", 32 * 1024);
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** retry count to be used (default 0); 0 = disable retries */
     private static final int RETRY_COUNT = JMeterUtils.getPropDefault("httpclient4.retrycount", 0);
 
     /** Idle timeout to be applied to connections if no Keep-Alive header is sent by the server (default 0 = disable) */
     private static final int IDLE_TIMEOUT = JMeterUtils.getPropDefault("httpclient4.idletimeout", 0);
     
     private static final int VALIDITY_AFTER_INACTIVITY_TIMEOUT = JMeterUtils.getPropDefault("httpclient4.validate_after_inactivity", 2000);
     
     private static final int TIME_TO_LIVE = JMeterUtils.getPropDefault("httpclient4.time_to_live", 2000);
 
     private static final String CONTEXT_METRICS = "jmeter_metrics"; // TODO hack for metrics related to HTTPCLIENT-1081, to be removed later
     
     private static final Pattern PORT_PATTERN = Pattern.compile("\\d+"); // only used in .matches(), no need for anchors
 
     private static final ConnectionKeepAliveStrategy IDLE_STRATEGY = new DefaultConnectionKeepAliveStrategy(){
         @Override
         public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
             long duration = super.getKeepAliveDuration(response, context);
             if (duration <= 0 && IDLE_TIMEOUT > 0) {// none found by the superclass
                 if(log.isDebugEnabled()) {
                     log.debug("Setting keepalive to " + IDLE_TIMEOUT);
                 }
                 return IDLE_TIMEOUT;
             } 
             return duration; // return the super-class value
         }
         
     };
 
     /**
      * Special interceptor made to keep metrics when connection is released for some method like HEAD
      * Otherwise calling directly ((HttpConnection) localContext.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getMetrics();
      * would throw org.apache.http.impl.conn.ConnectionShutdownException
      * See <a href="https://bz.apache.org/jira/browse/HTTPCLIENT-1081">HTTPCLIENT-1081</a>
      */
     private static final HttpResponseInterceptor METRICS_SAVER = new HttpResponseInterceptor(){
         @Override
         public void process(HttpResponse response, HttpContext context)
                 throws HttpException, IOException {
             HttpConnectionMetrics metrics = ((HttpConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getMetrics();
             context.setAttribute(CONTEXT_METRICS, metrics);
         }
     };
     private static final HttpRequestInterceptor METRICS_RESETTER = new HttpRequestInterceptor() {
         @Override
         public void process(HttpRequest request, HttpContext context)
                 throws HttpException, IOException {
             HttpConnectionMetrics metrics = ((HttpConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getMetrics();
             metrics.reset();
         }
     };
 
 
     /**
      * Headers to save
      */
     private static final String[] HEADERS_TO_SAVE = new String[]{
                     "content-length",
                     "content-encoding",
                     "content-md5"
             };
     
     /**
      * Custom implementation that backups headers related to Compressed responses 
      * that HC core {@link ResponseContentEncoding} removes after uncompressing
      * See Bug 59401
      */
     private static final HttpResponseInterceptor RESPONSE_CONTENT_ENCODING = new ResponseContentEncoding() {
         @Override
         public void process(HttpResponse response, HttpContext context)
                 throws HttpException, IOException {
             ArrayList<Header[]> headersToSave = null;
             
             final HttpEntity entity = response.getEntity();
             final HttpClientContext clientContext = HttpClientContext.adapt(context);
             final RequestConfig requestConfig = clientContext.getRequestConfig();
             // store the headers if necessary
             if (requestConfig.isContentCompressionEnabled() && entity != null && entity.getContentLength() != 0) {
                 final Header ceheader = entity.getContentEncoding();
                 if (ceheader != null) {
                     headersToSave = new ArrayList<>(3);
                     for(String name : HEADERS_TO_SAVE) {
                         Header[] hdr = response.getHeaders(name); // empty if none
                         headersToSave.add(hdr);
                     }
                 }
             }
 
             // Now invoke original parent code
             super.process(response, clientContext);
             // Should this be in a finally ? 
             if(headersToSave != null) {
                 for (Header[] headers : headersToSave) {
                     for (Header headerToRestore : headers) {
                         if (response.containsHeader(headerToRestore.getName())) {
                             break;
                         }
                         response.addHeader(headerToRestore);
                     }
                 }
             }
         }
     };
     
     /**
      * 1 HttpClient instance per combination of (HttpClient,HttpClientKey)
      */
     private static final ThreadLocal<Map<HttpClientKey, HttpClient>> HTTPCLIENTS_CACHE_PER_THREAD_AND_HTTPCLIENTKEY = 
         new InheritableThreadLocal<Map<HttpClientKey, HttpClient>>(){
         @Override
         protected Map<HttpClientKey, HttpClient> initialValue() {
             return new HashMap<>();
         }
     };
 
     // Scheme used for slow HTTP sockets. Cannot be set as a default, because must be set on an HttpClient instance.
     private static final Scheme SLOW_HTTP;
     
     /*
      * Create a set of default parameters from the ones initially created.
      * This allows the defaults to be overridden if necessary from the properties file.
      */
     private static final HttpParams DEFAULT_HTTP_PARAMS;
 
     private static final String USER_TOKEN = "__jmeter.USER_TOKEN__"; //$NON-NLS-1$
     
     static final String SAMPLER_RESULT_TOKEN = "__jmeter.SAMPLER_RESULT__"; //$NON-NLS-1$
     
     private static final String HTTPCLIENT_TOKEN = "__jmeter.HTTPCLIENT_TOKEN__";
 
     static {
         log.info("HTTP request retry count = "+RETRY_COUNT);
 
         DEFAULT_HTTP_PARAMS = new SyncBasicHttpParams(); // Could we drop the Sync here?
         DEFAULT_HTTP_PARAMS.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
         DEFAULT_HTTP_PARAMS.setIntParameter(ClientPNames.MAX_REDIRECTS, HTTPSamplerBase.MAX_REDIRECTS);
         DefaultHttpClient.setDefaultHttpParams(DEFAULT_HTTP_PARAMS);
         
         // Process Apache HttpClient parameters file
         String file=JMeterUtils.getProperty("hc.parameters.file"); // $NON-NLS-1$
         if (file != null) {
             HttpClientDefaultParameters.load(file, DEFAULT_HTTP_PARAMS);
         }
 
         // Set up HTTP scheme override if necessary
         if (CPS_HTTP > 0) {
             log.info("Setting up HTTP SlowProtocol, cps="+CPS_HTTP);
             SLOW_HTTP = new Scheme(HTTPConstants.PROTOCOL_HTTP, HTTPConstants.DEFAULT_HTTP_PORT, new SlowHC4SocketFactory(CPS_HTTP));
         } else {
             SLOW_HTTP = null;
         }
         
         if (localAddress != null){
             DEFAULT_HTTP_PARAMS.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
         }
         
     }
 
     private volatile HttpUriRequest currentRequest; // Accessed from multiple threads
 
     private volatile boolean resetSSLContext;
 
     protected HTTPHC4Impl(HTTPSamplerBase testElement) {
         super(testElement);
     }
 
     public static final class HttpDelete extends HttpEntityEnclosingRequestBase {
 
         public HttpDelete(final URI uri) {
             super();
             setURI(uri);
         }
 
         @Override
         public String getMethod() {
             return HTTPConstants.DELETE;
         }
     }
     
     @Override
     protected HTTPSampleResult sample(URL url, String method,
             boolean areFollowingRedirect, int frameDepth) {
 
         if (log.isDebugEnabled()) {
             log.debug("Start : sample " + url.toString());
             log.debug("method " + method+ " followingRedirect " + areFollowingRedirect + " depth " + frameDepth);            
         }
 
         HTTPSampleResult res = createSampleResult(url, method);
 
         HttpClient httpClient = setupClient(url, res);
 
         HttpRequestBase httpRequest = null;
         try {
             URI uri = url.toURI();
             if (method.equals(HTTPConstants.POST)) {
                 httpRequest = new HttpPost(uri);
             } else if (method.equals(HTTPConstants.GET)) {
                 httpRequest = new HttpGet(uri);
             } else if (method.equals(HTTPConstants.PUT)) {
                 httpRequest = new HttpPut(uri);
             } else if (method.equals(HTTPConstants.HEAD)) {
                 httpRequest = new HttpHead(uri);
             } else if (method.equals(HTTPConstants.TRACE)) {
                 httpRequest = new HttpTrace(uri);
             } else if (method.equals(HTTPConstants.OPTIONS)) {
                 httpRequest = new HttpOptions(uri);
             } else if (method.equals(HTTPConstants.DELETE)) {
                 httpRequest = new HttpDelete(uri);
             } else if (method.equals(HTTPConstants.PATCH)) {
                 httpRequest = new HttpPatch(uri);
             } else if (HttpWebdav.isWebdavMethod(method)) {
                 httpRequest = new HttpWebdav(method, uri);
             } else {
                 throw new IllegalArgumentException("Unexpected method: '"+method+"'");
             }
             setupRequest(url, httpRequest, res); // can throw IOException
         } catch (Exception e) {
             res.sampleStart();
             res.sampleEnd();
             errorResult(e, res);
             return res;
         }
 
         HttpContext localContext = new BasicHttpContext();
         setupClientContextBeforeSample(localContext);
         
         res.sampleStart();
 
         final CacheManager cacheManager = getCacheManager();
         if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
            if (cacheManager.inCache(url)) {
                return updateSampleResultForResourceInCache(res);
            }
         }
 
         try {
             currentRequest = httpRequest;
             handleMethod(method, res, httpRequest, localContext);
             // store the SampleResult in LocalContext to compute connect time
             localContext.setAttribute(SAMPLER_RESULT_TOKEN, res);
             // perform the sample
             HttpResponse httpResponse = 
                     executeRequest(httpClient, httpRequest, localContext, url);
 
             // Needs to be done after execute to pick up all the headers
             final HttpRequest request = (HttpRequest) localContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
             extractClientContextAfterSample(localContext);
             // We've finished with the request, so we can add the LocalAddress to it for display
             final InetAddress localAddr = (InetAddress) httpRequest.getParams().getParameter(ConnRoutePNames.LOCAL_ADDRESS);
             if (localAddr != null) {
                 request.addHeader(HEADER_LOCAL_ADDRESS, localAddr.toString());
             }
             res.setRequestHeaders(getConnectionHeaders(request));
 
             Header contentType = httpResponse.getLastHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             if (contentType != null){
                 String ct = contentType.getValue();
                 res.setContentType(ct);
                 res.setEncodingAndType(ct);                    
             }
             HttpEntity entity = httpResponse.getEntity();
             if (entity != null) {
                 res.setResponseData(readResponse(res, entity.getContent(), (int) entity.getContentLength()));
             }
             
             res.sampleEnd(); // Done with the sampling proper.
             currentRequest = null;
 
             // Now collect the results into the HTTPSampleResult:
             StatusLine statusLine = httpResponse.getStatusLine();
             int statusCode = statusLine.getStatusCode();
             res.setResponseCode(Integer.toString(statusCode));
             res.setResponseMessage(statusLine.getReasonPhrase());
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseHeaders(getResponseHeaders(httpResponse, localContext));
             if (res.isRedirect()) {
                 final Header headerLocation = httpResponse.getLastHeader(HTTPConstants.HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header in redirect for " + httpRequest.getRequestLine());
                 }
                 String redirectLocation = headerLocation.getValue();
                 res.setRedirectLocation(redirectLocation);
             }
 
             // record some sizes to allow HTTPSampleResult.getBytes() with different options
             HttpConnectionMetrics  metrics = (HttpConnectionMetrics) localContext.getAttribute(CONTEXT_METRICS);
             long headerBytes = 
                 res.getResponseHeaders().length()   // condensed length (without \r)
               + httpResponse.getAllHeaders().length // Add \r for each header
               + 1 // Add \r for initial header
               + 2; // final \r\n before data
             long totalBytes = metrics.getReceivedBytesCount();
             res.setHeadersSize((int) headerBytes);
             res.setBodySize((int)(totalBytes - headerBytes));
+            res.setSentBytes(metrics.getSentBytesCount());
             if (log.isDebugEnabled()) {
                 log.debug("ResponseHeadersSize=" + res.getHeadersSize() + " Content-Length=" + res.getBodySize()
                         + " Total=" + (res.getHeadersSize() + res.getBodySize()));
             }
 
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
                 HttpHost target = (HttpHost) localContext.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
                 URI redirectURI = req.getURI();
                 if (redirectURI.isAbsolute()){
                     res.setURL(redirectURI.toURL());
                 } else {
                     res.setURL(new URL(new URL(target.toURI()),redirectURI.toString()));
                 }
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(httpResponse, res.getURL(), getCookieManager());
 
             // Save cache information
             if (cacheManager != null){
                 cacheManager.saveDetails(httpResponse, res);
             }
 
             // Follow redirects and download page resources if appropriate:
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
         } catch (IOException e) {
             log.debug("IOException", e);
             if (res.getEndTime() == 0) {
                 res.sampleEnd();
             }
            // pick up headers if failed to execute the request
             if (res.getRequestHeaders() != null) {
                 log.debug("Overwriting request old headers: " + res.getRequestHeaders());
             }
             res.setRequestHeaders(getConnectionHeaders((HttpRequest) localContext.getAttribute(HttpCoreContext.HTTP_REQUEST)));
             errorResult(e, res);
             return res;
         } catch (RuntimeException e) {
             log.debug("RuntimeException", e);
             if (res.getEndTime() == 0) {
                 res.sampleEnd();
             }
             errorResult(e, res);
             return res;
         } finally {
             currentRequest = null;
             JMeterContextService.getContext().getSamplerContext().remove(HTTPCLIENT_TOKEN);
         }
         return res;
     }
 
     /**
      * Store in JMeter Variables the UserToken so that the SSL context is reused
      * See <a href="https://bz.apache.org/bugzilla/show_bug.cgi?id=57804">Bug 57804</a>
      * @param localContext {@link HttpContext}
      */
     private void extractClientContextAfterSample(HttpContext localContext) {
         Object userToken = localContext.getAttribute(HttpClientContext.USER_TOKEN);
         if(userToken != null) {
             if(log.isDebugEnabled()) {
                 log.debug("Extracted from HttpContext user token:"+userToken+", storing it as JMeter variable:"+USER_TOKEN);
             }
             // During recording JMeterContextService.getContext().getVariables() is null
             JMeterVariables jMeterVariables = JMeterContextService.getContext().getVariables();
             if (jMeterVariables != null) {
                 jMeterVariables.putObject(USER_TOKEN, userToken); 
             }
         }
     }
 
     /**
      * Configure the UserToken so that the SSL context is reused
      * See <a href="https://bz.apache.org/bugzilla/show_bug.cgi?id=57804">Bug 57804</a>
      * @param localContext {@link HttpContext}
      */
     private void setupClientContextBeforeSample(HttpContext localContext) {
         Object userToken = null;
         // During recording JMeterContextService.getContext().getVariables() is null
         JMeterVariables jMeterVariables = JMeterContextService.getContext().getVariables();
         if(jMeterVariables != null) {
             userToken = jMeterVariables.getObject(USER_TOKEN);            
         }
         if(userToken != null) {
             if(log.isDebugEnabled()) {
                 log.debug("Found user token:"+userToken+" as JMeter variable:"+USER_TOKEN+", storing it in HttpContext");
             }
             localContext.setAttribute(HttpClientContext.USER_TOKEN, userToken);
         } else {
             // It would be better to create a ClientSessionManager that would compute this value
             // for now it can be Thread.currentThread().getName() but must be changed when we would change 
             // the Thread per User model
             String userId = Thread.currentThread().getName();
             if(log.isDebugEnabled()) {
                 log.debug("Storing in HttpContext the user token:"+userId);
             }
             localContext.setAttribute(HttpClientContext.USER_TOKEN, userId);
         }
     }
 
     /**
      * Calls {@link #sendPostData(HttpPost)} if method is <code>POST</code> and
      * {@link #sendEntityData(HttpEntityEnclosingRequestBase)} if method is
      * <code>PUT</code> or <code>PATCH</code>
      * <p>
      * Field HTTPSampleResult#queryString of result is modified in the 2 cases
      * 
      * @param method
      *            String HTTP method
      * @param result
      *            {@link HTTPSampleResult}
      * @param httpRequest
      *            {@link HttpRequestBase}
      * @param localContext
      *            {@link HttpContext}
      * @throws IOException
      *             when posting data fails due to I/O
      */
     protected void handleMethod(String method, HTTPSampleResult result,
             HttpRequestBase httpRequest, HttpContext localContext) throws IOException {
         // Handle the various methods
         if (httpRequest instanceof HttpPost) {
             String postBody = sendPostData((HttpPost)httpRequest);
             result.setQueryString(postBody);
         } else if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
             String entityBody = sendEntityData((HttpEntityEnclosingRequestBase) httpRequest);
             result.setQueryString(entityBody);
         }
     }
 
     /**
      * Create HTTPSampleResult filling url, method and SampleLabel.
      * Monitor field is computed calling isMonitor()
      * @param url URL
      * @param method HTTP Method
      * @return {@link HTTPSampleResult}
      */
     protected HTTPSampleResult createSampleResult(URL url, String method) {
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(url.toString()); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
         
         return res;
     }
 
     /**
      * Execute request either as is or under PrivilegedAction 
      * if a Subject is available for url
      * @param httpClient the {@link HttpClient} to be used to execute the httpRequest
      * @param httpRequest the {@link HttpRequest} to be executed
      * @param localContext th {@link HttpContext} to be used for execution
      * @param url the target url (will be used to look up a possible subject for the execution)
      * @return the result of the execution of the httpRequest
      * @throws IOException
      * @throws ClientProtocolException
      */
     private HttpResponse executeRequest(final HttpClient httpClient,
             final HttpRequestBase httpRequest, final HttpContext localContext, final URL url)
             throws IOException, ClientProtocolException {
         AuthManager authManager = getAuthManager();
         if (authManager != null) {
             Subject subject = authManager.getSubjectForUrl(url);
             if(subject != null) {
                 try {
                     return Subject.doAs(subject,
                             new PrivilegedExceptionAction<HttpResponse>() {
     
                                 @Override
                                 public HttpResponse run() throws Exception {
                                     return httpClient.execute(httpRequest,
                                             localContext);
                                 }
                             });
                 } catch (PrivilegedActionException e) {
                     log.error(
                             "Can't execute httpRequest with subject:"+subject,
                             e);
                     throw new RuntimeException("Can't execute httpRequest with subject:"+subject, e);
                 }
             }
         }
         return httpClient.execute(httpRequest, localContext);
     }
 
     /**
      * Holder class for all fields that define an HttpClient instance;
      * used as the key to the ThreadLocal map of HttpClient instances.
      */
     private static final class HttpClientKey {
 
         private final String target; // protocol://[user:pass@]host:[port]
         private final boolean hasProxy;
         private final String proxyHost;
         private final int proxyPort;
         private final String proxyUser;
         private final String proxyPass;
         
         private final int hashCode; // Always create hash because we will always need it
 
         /**
          * @param url URL Only protocol and url authority are used (protocol://[user:pass@]host:[port])
          * @param hasProxy has proxy
          * @param proxyHost proxy host
          * @param proxyPort proxy port
          * @param proxyUser proxy user
          * @param proxyPass proxy password
          */
         public HttpClientKey(URL url, boolean hasProxy, String proxyHost,
                 int proxyPort, String proxyUser, String proxyPass) {
             // N.B. need to separate protocol from authority otherwise http://server would match https://erver (<= sic, not typo error)
             // could use separate fields, but simpler to combine them
             this.target = url.getProtocol()+"://"+url.getAuthority();
             this.hasProxy = hasProxy;
             this.proxyHost = proxyHost;
             this.proxyPort = proxyPort;
             this.proxyUser = proxyUser;
             this.proxyPass = proxyPass;
             this.hashCode = getHash();
         }
         
         private int getHash() {
             int hash = 17;
             hash = hash*31 + (hasProxy ? 1 : 0);
             if (hasProxy) {
                 hash = hash*31 + getHash(proxyHost);
                 hash = hash*31 + proxyPort;
                 hash = hash*31 + getHash(proxyUser);
                 hash = hash*31 + getHash(proxyPass);
             }
             hash = hash*31 + target.hashCode();
             return hash;
         }
 
         // Allow for null strings
         private int getHash(String s) {
             return s == null ? 0 : s.hashCode(); 
         }
         
         @Override
         public boolean equals (Object obj){
             if (this == obj) {
                 return true;
             }
             if (!(obj instanceof HttpClientKey)) {
                 return false;
             }
             HttpClientKey other = (HttpClientKey) obj;
             if (this.hasProxy) { // otherwise proxy String fields may be null
                 return 
                 this.hasProxy == other.hasProxy &&
                 this.proxyPort == other.proxyPort &&
                 this.proxyHost.equals(other.proxyHost) &&
                 this.proxyUser.equals(other.proxyUser) &&
                 this.proxyPass.equals(other.proxyPass) &&
                 this.target.equals(other.target);
             }
             // No proxy, so don't check proxy fields
             return 
                 this.hasProxy == other.hasProxy &&
                 this.target.equals(other.target);
         }
 
         @Override
         public int hashCode(){
             return hashCode;
         }
 
         // For debugging
         @Override
         public String toString() {
             StringBuilder sb = new StringBuilder();
             sb.append(target);
             if (hasProxy) {
                 sb.append(" via ");
                 sb.append(proxyUser);
                 sb.append('@');
                 sb.append(proxyHost);
                 sb.append(':');
                 sb.append(proxyPort);
             }
             return sb.toString();
         }
     }
 
     private HttpClient setupClient(URL url, SampleResult res) {
 
         Map<HttpClientKey, HttpClient> mapHttpClientPerHttpClientKey = HTTPCLIENTS_CACHE_PER_THREAD_AND_HTTPCLIENTKEY.get();
         
         final String host = url.getHost();
         String proxyHost = getProxyHost();
         int proxyPort = getProxyPortInt();
         String proxyPass = getProxyPass();
         String proxyUser = getProxyUser();
 
         // static proxy is the globally define proxy eg command line or properties
         boolean useStaticProxy = isStaticProxy(host);
         // dynamic proxy is the proxy defined for this sampler
         boolean useDynamicProxy = isDynamicProxy(proxyHost, proxyPort);
         boolean useProxy = useStaticProxy || useDynamicProxy;
         
         // if both dynamic and static are used, the dynamic proxy has priority over static
         if(!useDynamicProxy) {
             proxyHost = PROXY_HOST;
             proxyPort = PROXY_PORT;
             proxyUser = PROXY_USER;
             proxyPass = PROXY_PASS;
         }
 
         // Lookup key - must agree with all the values used to create the HttpClient.
         HttpClientKey key = new HttpClientKey(url, useProxy, proxyHost, proxyPort, proxyUser, proxyPass);
         
         HttpClient httpClient = null;
         boolean concurrentDwn = this.testElement.isConcurrentDwn();
         if(concurrentDwn) {
             httpClient = (HttpClient) JMeterContextService.getContext().getSamplerContext().get(HTTPCLIENT_TOKEN);
         }
         
         if (httpClient == null) {
             httpClient = mapHttpClientPerHttpClientKey.get(key);
         }
 
         if (httpClient != null && resetSSLContext && HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(url.getProtocol())) {
             ((AbstractHttpClient) httpClient).clearRequestInterceptors(); 
             ((AbstractHttpClient) httpClient).clearResponseInterceptors(); 
             httpClient.getConnectionManager().closeIdleConnections(1L, TimeUnit.MICROSECONDS);
             httpClient = null;
             JsseSSLManager sslMgr = (JsseSSLManager) SSLManager.getInstance();
             sslMgr.resetContext();
             resetSSLContext = false;
         }
 
         if (httpClient == null) { // One-time init for this client
 
             HttpParams clientParams = new DefaultedHttpParams(new BasicHttpParams(), DEFAULT_HTTP_PARAMS);
 
             DnsResolver resolver = this.testElement.getDNSResolver();
             if (resolver == null) {
                 resolver = SystemDefaultDnsResolver.INSTANCE;
             }
             MeasuringConnectionManager connManager = new MeasuringConnectionManager(
                     createSchemeRegistry(), 
                     resolver, 
                     TIME_TO_LIVE,
                     VALIDITY_AFTER_INACTIVITY_TIMEOUT);
             
             // Modern browsers use more connections per host than the current httpclient default (2)
             // when using parallel download the httpclient and connection manager are shared by the downloads threads
             // to be realistic JMeter must set an higher value to DefaultMaxPerRoute
             if(concurrentDwn) {
                 try {
                     int maxConcurrentDownloads = Integer.parseInt(this.testElement.getConcurrentPool());
                     connManager.setDefaultMaxPerRoute(Math.max(maxConcurrentDownloads, connManager.getDefaultMaxPerRoute()));                
                 } catch (NumberFormatException nfe) {
                    // no need to log -> will be done by the sampler
                 }
             }
             
             httpClient = new DefaultHttpClient(connManager, clientParams) {
                 @Override
                 protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
                     return new DefaultHttpRequestRetryHandler(RETRY_COUNT, false); // set retry count
                 }
             };
             
             if (IDLE_TIMEOUT > 0) {
                 ((AbstractHttpClient) httpClient).setKeepAliveStrategy(IDLE_STRATEGY );
             }
             // see https://issues.apache.org/jira/browse/HTTPCORE-397
             ((AbstractHttpClient) httpClient).setReuseStrategy(DefaultClientConnectionReuseStrategy.INSTANCE);
             ((AbstractHttpClient) httpClient).addResponseInterceptor(RESPONSE_CONTENT_ENCODING);
             ((AbstractHttpClient) httpClient).addResponseInterceptor(METRICS_SAVER); // HACK
             ((AbstractHttpClient) httpClient).addRequestInterceptor(METRICS_RESETTER); 
             
             // Override the default schemes as necessary
             SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
 
             if (SLOW_HTTP != null){
                 schemeRegistry.register(SLOW_HTTP);
             }
 
             // Set up proxy details
             if(useProxy) {
 
                 HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 
                 if (proxyUser.length() > 0) {                   
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(proxyHost, proxyPort),
                             new NTCredentials(proxyUser, proxyPass, localHost, PROXY_DOMAIN));
                 }
             }
 
             // Bug 52126 - we do our own cookie handling
             clientParams.setParameter(ClientPNames.COOKIE_POLICY, CookieSpecs.IGNORE_COOKIES);
 
             if (log.isDebugEnabled()) {
                 log.debug("Created new HttpClient: @"+System.identityHashCode(httpClient) + " " + key.toString());
             }
 
             mapHttpClientPerHttpClientKey.put(key, httpClient); // save the agent for next time round
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Reusing the HttpClient: @"+System.identityHashCode(httpClient) + " " + key.toString());
             }
         }
 
         if(concurrentDwn) {
             JMeterContextService.getContext().getSamplerContext().put(HTTPCLIENT_TOKEN, httpClient);
         }
 
         // TODO - should this be done when the client is created?
         // If so, then the details need to be added as part of HttpClientKey
         setConnectionAuthorization(httpClient, url, getAuthManager(), key);
 
         return httpClient;
     }
 
     /**
      * Setup LazySchemeSocketFactory
      * @see "https://bz.apache.org/bugzilla/show_bug.cgi?id=58099"
      */
     private static SchemeRegistry createSchemeRegistry() {
         final SchemeRegistry registry = new SchemeRegistry();
         registry.register(
                 new Scheme("http", 80, PlainSocketFactory.getSocketFactory())); //$NON-NLS-1$
         registry.register(
                 new Scheme("https", 443, new LazySchemeSocketFactory())); //$NON-NLS-1$
         return registry;
     }
 
     /**
      * Setup following elements on httpRequest:
      * <ul>
      * <li>ConnRoutePNames.LOCAL_ADDRESS enabling IP-SPOOFING</li>
      * <li>Socket and connection timeout</li>
      * <li>Redirect handling</li>
      * <li>Keep Alive header or Connection Close</li>
      * <li>Calls setConnectionHeaders to setup headers</li>
      * <li>Calls setConnectionCookie to setup Cookie</li>
      * </ul>
      * 
      * @param url
      *            {@link URL} of the request
      * @param httpRequest
      *            http request for the request
      * @param res
      *            sample result to set cookies on
      * @throws IOException
      *             if hostname/ip to use could not be figured out
      */
     protected void setupRequest(URL url, HttpRequestBase httpRequest, HTTPSampleResult res)
         throws IOException {
 
     HttpParams requestParams = httpRequest.getParams();
     
     // Set up the local address if one exists
     final InetAddress inetAddr = getIpSourceAddress();
     if (inetAddr != null) {// Use special field ip source address (for pseudo 'ip spoofing')
         requestParams.setParameter(ConnRoutePNames.LOCAL_ADDRESS, inetAddr);
     } else if (localAddress != null){
         requestParams.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
     } else { // reset in case was set previously
         requestParams.removeParameter(ConnRoutePNames.LOCAL_ADDRESS);
     }
 
     int rto = getResponseTimeout();
     if (rto > 0){
         requestParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, rto);
     }
 
     int cto = getConnectTimeout();
     if (cto > 0){
         requestParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, cto);
     }
 
     requestParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, getAutoRedirects());
     
     // a well-behaved browser is supposed to send 'Connection: close'
     // with the last request to an HTTP server. Instead, most browsers
     // leave it to the server to close the connection after their
     // timeout period. Leave it to the JMeter user to decide.
     if (getUseKeepAlive()) {
         httpRequest.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.KEEP_ALIVE);
     } else {
         httpRequest.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.CONNECTION_CLOSE);
     }
 
     setConnectionHeaders(httpRequest, url, getHeaderManager(), getCacheManager());
 
     String cookies = setConnectionCookie(httpRequest, url, getCookieManager());
 
     if (res != null) {
         res.setCookies(cookies);
     }
 
 }
 
     
     /**
      * Set any default request headers to include
      *
      * @param request the HttpRequest to be used
      */
     protected void setDefaultRequestHeaders(HttpRequest request) {
      // Method left empty here, but allows subclasses to override
     }
 
     /**
      * Gets the ResponseHeaders
      *
      * @param response
      *            containing the headers
      * @param localContext {@link HttpContext}
      * @return string containing the headers, one per line
      */
     private String getResponseHeaders(HttpResponse response, HttpContext localContext) {
         Header[] rh = response.getAllHeaders();
 
         StringBuilder headerBuf = new StringBuilder(40 * (rh.length+1));
         headerBuf.append(response.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (Header responseHeader : rh) {
             writeResponseHeader(headerBuf, responseHeader);
         }
         return headerBuf.toString();
     }
 
     /**
      * Write responseHeader to headerBuffer in an optimized way
      * @param headerBuffer {@link StringBuilder}
      * @param responseHeader {@link Header}
      */
     private void writeResponseHeader(StringBuilder headerBuffer, Header responseHeader) {
         if(responseHeader instanceof BufferedHeader) {
             CharArrayBuffer buffer = ((BufferedHeader)responseHeader).getBuffer();
             headerBuffer.append(buffer.buffer(), 0, buffer.length()).append('\n'); // $NON-NLS-1$;
         }
         else {
             headerBuffer.append(responseHeader.getName())
             .append(": ") // $NON-NLS-1$
             .append(responseHeader.getValue())
             .append('\n'); // $NON-NLS-1$
         }
     }
 
     /**
      * Extracts all the required cookies for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in.
      *
      * @param request <code>HttpRequest</code> for the request
      * @param url <code>URL</code> of the request
      * @param cookieManager the <code>CookieManager</code> containing all the cookies
      * @return a String containing the cookie details (for the response)
      * May be null
      */
     protected String setConnectionCookie(HttpRequest request, URL url, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(url);
             if (cookieHeader != null) {
                 request.setHeader(HTTPConstants.HEADER_COOKIE, cookieHeader);
             }
         }
         return cookieHeader;
     }
     
     /**
      * Extracts all the required non-cookie headers for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in
      *
      * @param request
      *            <code>HttpRequest</code> which represents the request
      * @param url
      *            <code>URL</code> of the URL request
      * @param headerManager
      *            the <code>HeaderManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      * @param cacheManager the CacheManager (may be null)
      */
     protected void setConnectionHeaders(HttpRequestBase request, URL url, HeaderManager headerManager, CacheManager cacheManager) {
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 for (JMeterProperty jMeterProperty : headers) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                             jMeterProperty.getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // TODO - what other headers are not allowed?
                     if (! HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         if (HTTPConstants.HEADER_HOST.equalsIgnoreCase(n)) {
                             int port = getPortFromHostHeader(v, url.getPort());
                             v = v.replaceFirst(":\\d+$",""); // remove any port specification // $NON-NLS-1$ $NON-NLS-2$
                             if (port != -1) {
                                 if (port == url.getDefaultPort()) {
                                     port = -1; // no need to specify the port if it is the default
                                 }
                             }
                             request.getParams().setParameter(ClientPNames.VIRTUAL_HOST, new HttpHost(v, port));
                         } else {
                             request.addHeader(n, v);
                         }
                     }
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(url, request);
         }
     }
 
     /**
      * Get port from the value of the Host header, or return the given
      * defaultValue
      *
      * @param hostHeaderValue
      *            value of the http Host header
      * @param defaultValue
      *            value to be used, when no port could be extracted from
      *            hostHeaderValue
      * @return integer representing the port for the host header
      */
     private int getPortFromHostHeader(String hostHeaderValue, int defaultValue) {
         String[] hostParts = hostHeaderValue.split(":");
         if (hostParts.length > 1) {
             String portString = hostParts[hostParts.length - 1];
             if (PORT_PATTERN.matcher(portString).matches()) {
                 return Integer.parseInt(portString);
             }
         }
         return defaultValue;
     }
 
     /**
      * Get all the request headers for the <code>HttpMethod</code>
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @return the headers as a string
      */
     private String getConnectionHeaders(HttpRequest method) {
         if(method != null) {
             // Get all the request headers
             StringBuilder hdrs = new StringBuilder(150);
             Header[] requestHeaders = method.getAllHeaders();
             for (Header requestHeader : requestHeaders) {
                 // Exclude the COOKIE header, since cookie is reported separately in the sample
                 if (!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(requestHeader.getName())) {
                     writeResponseHeader(hdrs, requestHeader);
                 }
             }
     
             return hdrs.toString();
         }
         return ""; ////$NON-NLS-1$
     }
 
     /**
      * Setup credentials for url AuthScope but keeps Proxy AuthScope credentials
      * @param client HttpClient
      * @param url URL
      * @param authManager {@link AuthManager}
      * @param key key
      */
     private void setConnectionAuthorization(HttpClient client, URL url, AuthManager authManager, HttpClientKey key) {
         CredentialsProvider credentialsProvider = 
             ((AbstractHttpClient) client).getCredentialsProvider();
         if (authManager != null) {
             if(authManager.hasAuthForURL(url)) {
                 authManager.setupCredentials(client, url, credentialsProvider, localHost);
             } else {
                 credentialsProvider.clear();
             }
         } else {
             Credentials credentials = null;
             AuthScope authScope = null;
             if(key.hasProxy && !StringUtils.isEmpty(key.proxyUser)) {
                 authScope = new AuthScope(key.proxyHost, key.proxyPort);
                 credentials = credentialsProvider.getCredentials(authScope);
             }
             credentialsProvider.clear(); 
             if(credentials != null) {
                 credentialsProvider.setCredentials(authScope, credentials);
             }
         }
     }
 
     // Helper class so we can generate request data without dumping entire file contents
     private static class ViewableFileBody extends FileBody {
         private boolean hideFileData;
         
         public ViewableFileBody(File file, String mimeType) {
             super(file, mimeType);
             hideFileData = false;
         }
 
         @Override
         public void writeTo(final OutputStream out) throws IOException {
             if (hideFileData) {
                 out.write("<actual file content, not shown here>".getBytes());// encoding does not really matter here
             } else {
                 super.writeTo(out);
             }
         }
     }
 
     // TODO needs cleaning up
     /**
      * 
      * @param post {@link HttpPost}
      * @return String posted body if computable
      * @throws IOException if sending the data fails due to I/O
      */
     protected String sendPostData(HttpPost post)  throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         HTTPFileArg[] files = getHTTPFiles();
 
         final String contentEncoding = getContentEncodingOrNull();
         final boolean haveContentEncoding = contentEncoding != null;
 
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(getUseMultipartForPost()) {
             // If a content encoding is specified, we use that as the
             // encoding of any parameter values
             Charset charset = null;
             if(haveContentEncoding) {
                 charset = Charset.forName(contentEncoding);
             } else {
                 charset = MIME.DEFAULT_CHARSET;
             }
             
             if(log.isDebugEnabled()) {
                 log.debug("Building multipart with:getDoBrowserCompatibleMultipart():"+
                         getDoBrowserCompatibleMultipart()+
                         ", with charset:"+charset+
                         ", haveContentEncoding:"+haveContentEncoding);
             }
             // Write the request to our own stream
             MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                     .setCharset(charset);
             if(getDoBrowserCompatibleMultipart()) {
                 multipartEntityBuilder.setLaxMode();
             } else {
                 multipartEntityBuilder.setStrictMode();
             }
             // Create the parts
             // Add any parameters
             for (JMeterProperty jMeterProperty : getArguments()) {
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 String parameterName = arg.getName();
                 if (arg.isSkippable(parameterName)) {
                     continue;
                 }
                 StringBody stringBody = new StringBody(arg.getValue(), ContentType.create("text/plain", charset));
                 FormBodyPart formPart = FormBodyPartBuilder.create(
                         parameterName, stringBody).build();
                 multipartEntityBuilder.addPart(formPart);
             }
 
             // Add any files
             // Cannot retrieve parts once added to the MultiPartEntity, so have to save them here.
             ViewableFileBody[] fileBodies = new ViewableFileBody[files.length];
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 
                 File reservedFile = FileServer.getFileServer().getResolvedFile(file.getPath());
                 fileBodies[i] = new ViewableFileBody(reservedFile, file.getMimeType());
                 multipartEntityBuilder.addPart(file.getParamName(), fileBodies[i] );
             }
 
             HttpEntity entity = multipartEntityBuilder.build();
             post.setEntity(entity);
 
             if (entity.isRepeatable()){
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = true;
                 }
                 entity.writeTo(bos);
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = false;
                 }
                 bos.flush();
                 // We get the posted bytes using the encoding used to create it
                 postedBody.append(bos.toString(
                         contentEncoding == null ? "US-ASCII" // $NON-NLS-1$ this is the default used by HttpClient
                         : contentEncoding));
                 bos.close();
             } else {
                 postedBody.append("<Multipart was not repeatable, cannot view what was sent>"); // $NON-NLS-1$
             }
 
 //            // Set the content type TODO - needed?
 //            String multiPartContentType = multiPart.getContentType().getValue();
 //            post.setHeader(HEADER_CONTENT_TYPE, multiPartContentType);
 
         } else { // not multipart
             // Check if the header manager had a content type header
             // This allows the user to specify his own content-type for a POST request
             Header contentTypeHeader = post.getFirstHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
             // If there are no arguments, we can send a file as the body of the request
             // TODO: needs a multiple file upload scenerio
             if(!hasArguments() && getSendFileAsPostBody()) {
                 // If getSendFileAsPostBody returned true, it's sure that file is not null
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
 
                 FileEntity fileRequestEntity = new FileEntity(new File(file.getPath()),(ContentType) null);// TODO is null correct?
                 post.setEntity(fileRequestEntity);
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>");
             } else {
                 // In a post request which is not multipart, we only support
                 // parameters, no file upload is allowed
 
                 // If a content encoding is specified, we set it as http parameter, so that
                 // the post body will be encoded in the specified content encoding
                 if(haveContentEncoding) {
                     post.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, contentEncoding);
                 }
 
                 // If none of the arguments have a name specified, we
                 // just send all the values as the post body
                 if(getSendParameterValuesAsPostBody()) {
                     // Allow the mimetype of the file to control the content type
                     // This is not obvious in GUI if you are not uploading any files,
                     // but just sending the content of nameless parameters
                     // TODO: needs a multiple file upload scenerio
                     if(!hasContentTypeHeader) {
                         HTTPFileArg file = files.length > 0? files[0] : null;
                         if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                             post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                              // TODO - is this the correct default?
                             post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBody = new StringBuilder();
                     for (JMeterProperty jMeterProperty : getArguments()) {
                         HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                         // Note: if "Encoded?" is not selected, arg.getEncodedValue is equivalent to arg.getValue
                         if (haveContentEncoding) {
                             postBody.append(arg.getEncodedValue(contentEncoding));
                         } else {
                             postBody.append(arg.getEncodedValue());
                         }
                     }
                     // Let StringEntity perform the encoding
                     StringEntity requestEntity = new StringEntity(postBody.toString(), contentEncoding);
                     post.setEntity(requestEntity);
                     postedBody.append(postBody.toString());
                 } else {
                     // It is a normal post request, with parameter names and values
 
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                     // Add the parameters
                     PropertyIterator args = getArguments().iterator();
                     List <NameValuePair> nvps = new ArrayList<>();
                     String urlContentEncoding = contentEncoding;
                     if(urlContentEncoding == null || urlContentEncoding.length() == 0) {
                         // Use the default encoding for urls
                         urlContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
                     }
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         // The HTTPClient always urlencodes both name and value,
                         // so if the argument is already encoded, we have to decode
                         // it before adding it to the post request
                         String parameterName = arg.getName();
                         if (arg.isSkippable(parameterName)){
                             continue;
                         }
                         String parameterValue = arg.getValue();
                         if(!arg.isAlwaysEncoded()) {
                             // The value is already encoded by the user
                             // Must decode the value now, so that when the
                             // httpclient encodes it, we end up with the same value
                             // as the user had entered.
                             parameterName = URLDecoder.decode(parameterName, urlContentEncoding);
                             parameterValue = URLDecoder.decode(parameterValue, urlContentEncoding);
                         }
                         // Add the parameter, httpclient will urlencode it
                         nvps.add(new BasicNameValuePair(parameterName, parameterValue));
                     }
                     UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, urlContentEncoding);
                     post.setEntity(entity);
                     if (entity.isRepeatable()){
                         ByteArrayOutputStream bos = new ByteArrayOutputStream();
                         post.getEntity().writeTo(bos);
                         bos.flush();
                         // We get the posted bytes using the encoding used to create it
                         postedBody.append(bos.toString(contentEncoding != null?contentEncoding:SampleResult.DEFAULT_HTTP_ENCODING));
                         
                         bos.close();
                     }  else {
                         postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                     }
                 }
             }
         }
         return postedBody.toString();
     }
 
     // TODO merge put and post methods as far as possible.
     // e.g. post checks for multipart form/files, and if not, invokes sendData(HttpEntityEnclosingRequestBase)
 
 
     /**
      * Creates the entity data to be sent.
      * <p>
      * If there is a file entry with a non-empty MIME type we use that to
      * set the request Content-Type header, otherwise we default to whatever
      * header is present from a Header Manager.
      * <p>
      * If the content charset {@link #getContentEncoding()} is null or empty 
      * we use the HC4 default provided by {@link HTTP#DEF_CONTENT_CHARSET} which is
      * ISO-8859-1.
      * 
      * @param entity to be processed, e.g. PUT or PATCH
      * @return the entity content, may be empty
      * @throws  UnsupportedEncodingException for invalid charset name
      * @throws IOException cannot really occur for ByteArrayOutputStream methods
      */
     protected String sendEntityData( HttpEntityEnclosingRequestBase entity) throws IOException {
         // Buffer to hold the entity body
         StringBuilder entityBody = new StringBuilder(1000);
         boolean hasEntityBody = false;
 
         final HTTPFileArg[] files = getHTTPFiles();
         // Allow the mimetype of the file to control the content type
         // This is not obvious in GUI if you are not uploading any files,
         // but just sending the content of nameless parameters
         final HTTPFileArg file = files.length > 0? files[0] : null;
         String contentTypeValue = null;
         if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
             contentTypeValue = file.getMimeType();
             entity.setHeader(HEADER_CONTENT_TYPE, contentTypeValue); // we provide the MIME type here
         }
 
         // Check for local contentEncoding (charset) override; fall back to default for content body
         // we do this here rather so we can use the same charset to retrieve the data
         final String charset = getContentEncoding(HTTP.DEF_CONTENT_CHARSET.name());
 
         // Only create this if we are overriding whatever default there may be
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasEntityBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             File reservedFile = FileServer.getFileServer().getResolvedFile(files[0].getPath());
             FileEntity fileRequestEntity = new FileEntity(reservedFile); // no need for content-type here
             entity.setEntity(fileRequestEntity);
         }
         // If none of the arguments have a name specified, we
         // just send all the values as the entity body
         else if(getSendParameterValuesAsPostBody()) {
             hasEntityBody = true;
 
             // Just append all the parameter values, and use that as the entity body
             StringBuilder entityBodyContent = new StringBuilder();
             for (JMeterProperty jMeterProperty : getArguments()) {
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 // Note: if "Encoded?" is not selected, arg.getEncodedValue is equivalent to arg.getValue
                 if (charset != null) {
                     entityBodyContent.append(arg.getEncodedValue(charset));
                 } else {
                     entityBodyContent.append(arg.getEncodedValue());
                 }
             }
             StringEntity requestEntity = new StringEntity(entityBodyContent.toString(), charset);
             entity.setEntity(requestEntity);
         }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 2648559a7..d9170e974 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,367 +1,368 @@
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
 <!ENTITY rarr     "&#x02192;" >
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
     <li>A cache for CSS Parsing of URLs has been introduced in this version, it is enabled by default. It is controlled by property <code>css.parser.cache.size</code>. It can be disabled by setting its value to <code>0</code>. See <bugzilla>59885</bugzilla></li>
     <li>ThroughputController defaults have changed. Now defaults are Percent Executions which is global and no more per user. See <bugzilla>60023</bugzilla></li>
     <li>Since 3.1 version, HTML report ignores Empty Transaction controller (possibly generated by If Controller or Throughput Controller) when computing metrics. This provides more accurate metrics</li>
     <li>Since 3.1 version, Summariser ignores SampleResults generated by TransactionController when computing the live statistics, see <bugzilla>60109</bugzilla></li>
     <li>Since 3.1 version, when using Stripped modes (by default StrippedBatch is used) , response will be stripped also for failing SampleResults, you can revert this to previous behaviour by setting <code>sample_sender_strip_also_on_error=false</code> in <code>user.properties</code>, see <bugzilla>60137</bugzilla></li>
     <li>Since 3.1 version, <code>jmeter.save.saveservice.connect_time</code> property value is <code>true</code>, meaning CSV file for results will contain an additional column containing connection time, see <bugzilla>60106</bugzilla></li>
     <li>Since 3.1 version, Random Timer subclasses (Gaussian Random Timer, Uniform Random Timer and Poisson Random Timer) implement interface <code><a href="./api/org/apache/jmeter/timers/ModifiableTimer.html">org.apache.jmeter.timers.ModifiableTimer</a></code></li>
     <li>Since 3.1 version, if you don't select any language in JSR223 Test Elements, Apache Groovy language will be used. See <bugzilla>59945</bugzilla></li>
     <li>Since 3.1 version, CSV DataSet now trims variable names to avoid issues due to spaces between variables names when configuring CSV DataSet. This should not have any impact for you unless you use space at the begining or end of your variable names. See <bugzilla>60221</bugzilla></li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <ul>
     <li><bug>60222</bug>Remove deprecated elements Distribution Graph, Spline Visualizer</li>
     <li><bug>60224</bug>Deprecate <code><a href="./usermanual/component_reference.html#Monitor_Results_(DEPRECATED)">Monitor Results</a></code> listener. It will be dropped in next version</li>
     <li><bug>60225</bug>Drop deprecated <code>__jexl</code> function, jexl support in BSF and dependency on <code>commons-jexl-1.1.jar</code> This function can be easily replaced with <code><a href="./usermanual/functions.html#__jexl3">__jexl3</a></code> function</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59882</bug>Reduce memory allocations for better throughput. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com) through <pr>217</pr> and <pr>228</pr></li>
     <li><bug>59885</bug>Optimize css parsing for embedded resources download by introducing a cache. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com) through <pr>219</pr></li>
     <li><bug>60092</bug>Add shortened version of the PUT body to sampler result.</li>
+    <li><bug>60229</bug>Add a new metric : sent_bytes. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><pr>211</pr>Differentiate the timing for JDBC Sampler. Use latency and connect time. Contributed by Thomas Peyrard (thomas.peyrard at murex.com)</li>
     <li><bug>59620</bug>Fix button action in "JMS Publisher &rarr; Random File from folder specified below" to allow to select a directory</li>
     <li><bug>60066</bug>Handle CLOBs and BLOBs and limit them if necessary when storing them in result sampler.</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>59351</bug>Improve log/error/message for IncludeController. Partly contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>60023</bug>ThroughputController : Make "Percent Executions" and global the default values. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60082</bug>Validation mode : Be able to force Throughput Controller to run as if it was set to 100%</li>
     <li><bug>59349</bug>Trim spaces in input filename in IncludeController.</li>
     <li><bug>60081</bug>Interleave Controller : Add an option to alternate across threads</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>59953</bug>GraphiteBackendListener : Add Average metric. Partly contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
     <li><bug>59975</bug>View Results Tree : Text renderer annoyingly scrolls down when content is bulky. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60109</bug>Summariser : Make it ignore TC generated SampleResult in its summary computations</li>
     <li><bug>59948</bug>Add a formatted and sane HTML source code render to View Results Tree</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>59845</bug>Log messages about JSON Path mismatches at <code>debug</code> level instead of <code>error</code>.</li>
     <li><pr>212</pr>Allow multiple selection and delete in HTTP Authorization Manager. Based on a patch by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><bug>59816</bug><pr>213</pr>Allow multiple selection and delete in HTTP Header Manager. Based on a patch by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><bug>59967</bug>CSS/JQuery Extractor : Allow empty default value. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>59974</bug>Response Assertion : Add button "<code>Add from clipboard</code>". Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60050</bug>CSV Data Set : Make it clear in the logs when a thread will exit due to this configuration</li>
     <li><bug>59962</bug>Cache Manager does not update expires date when response code is <code>304</code>.</li>
     <li><bug>60018</bug>Timer : Add a factor to apply on pauses. Partly based on a patch by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60203</bug>Use more available space for textarea in XPath Assertion.</li>
     <li><bug>60220</bug>Rename JSON Path Post Processor to JSON Extractor</li>
     <li><bug>60221</bug>CSV DataSet : trim variable names</li>
     <li><bug>59329</bug>Trim spaces in input filename in CSVDataSet.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>59963</bug>New Function <code>__RandomFromMultipleVars</code>: Ability to compute a random value from values of one or more variables. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>59991</bug>New function <code>__groovy</code> to evaluate Groovy Script. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
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
     <li><bug>60098</bug>Report / Dashboard : Reduce default value for "<code>jmeter.reportgenerator.statistic_window</code>" to reduce memory impact</li>
     <li><bug>60115</bug>Add date format property for start/end date filter into Report generator</li>
     <li><bug>60171</bug>Report / Dashboard : Active Threads Over Time should stack lines to give the total amount of threads running</li>
 </ul>
 <h3>General</h3>
 <ul>
     <li><bug>59803</bug>Use <code>isValid()</code> method from JDBC driver, if no <code>validationQuery</code>
     is given in JDBC Connection Configuration.</li>
     <li><bug>57493</bug>Create a documentation page for properties</li>
     <li><bug>59924</bug>The log level of <em>XXX</em> package is set to <code>DEBUG</code> if <code>log_level.<em>XXXX</em></code> property value contains spaces, same for <code>__log</code> function</li>
     <li><bug>59777</bug>Extract SLF4J binding into its own jar and make it a JMeter lib.
     <note>If you get a warning about multiple SLF4J bindings on startup. Remove either the Apache JMeter provided binding
           <code>lib/ApacheJMeter_slf4j_logkit.jar</code>, or all of the other reported bindings.
           For more information you can have a look at <a href="http://www.slf4j.org/codes.html#multiple_bindings">SLF4Js own info page.</a></note>
     </li>
     <li><bug>60085</bug>Remove cache for prepared statements, as it didn't work with the current JDBC pool implementation and current JDBC drivers should support caching of prepared statements themselves.</li>
     <li><bug>60137</bug>In Distributed testing when using StrippedXXXX modes strip response also on error</li>
     <li><bug>60106</bug>Settings defaults : Switch "<code>jmeter.save.saveservice.connect_time</code>" to true (after 3.0)</li>
     <li><pr>229</pr> tiny memory allocation improvements. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><bug>59945</bug>For all JSR223 elements, if script language has not been chosen on the UI, the script will be interpreted as a groovy script.</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li>Updated to jsoup-1.9.2 (from 1.8.3)</li>
     <li>Updated to ph-css 4.1.5 (from 4.1.4)</li>
     <li>Updated to tika-core and tika-parsers 1.13 (from 1.12)</li>
     <li>Updated to commons-io 2.5 (from 2.4)</li>
     <li>Updated to commons-net 3.5 (from 3.4)</li>
     <li>Updated to groovy 2.4.7 (from 2.4.6)</li>
     <li>Updated to httpcore 4.4.5 (from 4.4.4)</li>
     <li>Updated to slf4j-api 1.7.21 (from 1.7.13)</li>
     <li>Updated to rsyntaxtextarea-2.6.0 (from 2.5.8)</li>
     <li>Updated to xstream 1.4.9 (from 1.4.8)</li>
     <li>Updated to jodd 3.7.1 (from 3.6.7.jar)</li>
     <li>Updated to xmlgraphics-commons 2.1 (from 2.0.1)</li>
     <li><pr>215</pr>Reduce duplicated code by using the newly added method <code>GuiUtils#cancelEditing</code>.
     Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><pr>218</pr>Misc cleanup. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><pr>216</pr>Re-use pattern when possible. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>58888</bug>HTTP(S) Test Script Recorder (ProxyControl) does not add TestElement's returned by <code>SamplerCreator#createChildren()</code></li>
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
     <li><bug>59723</bug>Use <code>jmeter.properties</code> for testing whenever possible</li>
     <li><bug>59726</bug>Unit test to check that CSV header text and sample format don't change unexpectedly</li>
     <li><bug>59889</bug>Change encoding to UTF-8 in reports for dashboard.</li>
     <li><bug>60053</bug>In Non GUI mode, a Stacktrace is shown at end of test while report is being generated</li>
     <li><bug>60049</bug>When using Timers with high delays or Constant Throughput Timer with low throughput, Scheduler may take a lot of time to exit, same for Shutdown test </li>
     <li><bug>60089</bug>Report / Dashboard : Bytes throughput Over Time has reversed Sent and Received bytes. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60090</bug>Report / Dashboard : Empty Transaction Controller should not count in metrics</li>
     <li><bug>60103</bug>Report / Dashboard : Requests summary includes Transaction Controller leading to wrong percentage</li>
     <li><bug>60105</bug>Report / Dashboard : Report requires Transaction Controller "<code>generate parent sample</code>" option to be checked , fix related issues</li>
     <li><bug>60107</bug>Report / Dashboard : In StatisticSummary, TransactionController SampleResult makes Total line wrong</li>
     <li><bug>60110</bug>Report / Dashboard : In Response Time Percentiles, slider is useless</li>
     <li><bug>60135</bug>Report / Dashboard : Active Threads Over Time should be in OverTime section</li>
     <li><bug>60125</bug>Report / Dashboard : Dashboard cannot be generated if the default delimiter is <code>\t</code>. Based on a report from Tamas Szabadi (tamas.szabadi at rightside.co)</li>
     <li><bug>59439</bug>Report / Dashboard : AbstractOverTimeGraphConsumer.createGroupInfos() should be abstract</li>
     <li><bug>59918</bug>Ant generated HTML report is broken (extras folder)</li>
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
 <li>Tamas Szabadi (tamas.szabadi at rightside.co)</li>
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
diff --git a/xdocs/usermanual/listeners.xml b/xdocs/usermanual/listeners.xml
index 4c550d6df..5679b5a5c 100644
--- a/xdocs/usermanual/listeners.xml
+++ b/xdocs/usermanual/listeners.xml
@@ -1,524 +1,527 @@
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
 <!ENTITY sect-num '14'>
 <!ENTITY hellip   "&#x02026;" >
 <!ENTITY vellip   "&#x022EE;" >
 ]>
 
 <document prev="build-monitor-test-plan.html" next="remote-test.html" id="$Id$">
 
 <properties>
   <title>User's Manual: Listeners</title>
 </properties>
 
 <body>
 
 <section name="&sect-num;. Introduction to listeners" anchor="intro">
 <p>A listener is a component that shows the results of the
 samples. The results can be shown in a tree, tables, graphs or simply written to a log
 file. To view the contents of a response from any given sampler, add either of the Listeners "<code>View
 Results Tree</code>" or "<code>View Results in table</code>" to a test plan. To view the response time graphically, add
 graph results.
 The <complink name="listeners">Listeners</complink> 
 section of the components page has full descriptions of all the listeners.</p>
 
 <note>
 Different listeners display the response information in different ways.
 However, they all write the same raw data to the output file - if one is specified.
 </note>
 <p>
 The "<code>Configure</code>" button can be used to specify which fields to write to the file, and whether to
 write it as CSV or XML.
 CSV files are much smaller than XML files, so use CSV if you are generating lots of samples.
 </p>
 <p>
 The file name can be specified using either a relative or an absolute path name.
 Relative paths are resolved relative to the current working directory (which defaults to the <code>bin/</code> directory).
 JMeter also supports paths relative to the directory containing the current test plan (JMX file).
 If the path name begins with "<code>~/</code>" (or whatever is in the <code>jmeter.save.saveservice.base_prefix</code> JMeter property),
 then the path is assumed to be relative to the JMX file location.
 </p>
 <p>
 If you only wish to record certain samples, add the Listener as a child of the sampler.
 Or you can use a Simple Controller to group a set of samplers, and add the Listener to that.
 The same filename can be used by multiple samplers - but make sure they all use the same configuration!
 </p>
 </section>
 
 <section name="&sect-num;.1 Default Configuration" anchor="defaults">
 <p>
 The default items to be saved can be defined in the <code>jmeter.properties</code> (or <code>user.properties</code>) file.
 The properties are used as the initial settings for the Listener Config pop-up, and are also
 used for the log file specified by the <code>-l</code> command-line flag (commonly used for non-GUI test runs).
 </p>
 <p>To change the default format, find the following line in <code>jmeter.properties</code>:</p>
 <source>jmeter.save.saveservice.output_format=</source>
 <p>
 The information to be saved is configurable.  For maximum information, choose "<code>xml</code>" as the format and specify "<code>Functional Test Mode</code>" on the Test Plan element.  If this box is not checked, the default saved
 data includes a time stamp (the number of milliseconds since midnight,
 January 1, 1970 UTC), the data type, the thread name, the label, the
 response time, message, and code, and a success indicator.  If checked, all information, including the full response data will be logged.</p>
 <p>
 The following example indicates how to set
 properties to get a vertical bar ("<code>|</code>") delimited format that will
 output results like:.</p>
 <source>
 timeStamp|time|label|responseCode|threadName|dataType|success|failureMessage
 02/06/03 08:21:42|1187|Home|200|Thread Group-1|text|true|
 02/06/03 08:21:42|47|Login|200|Thread Group-1|text|false|Test Failed: 
     expected to contain: password etc.
 </source>
 <p>
 The corresponding <code>jmeter.properties</code> that need to be set are shown below.  One oddity
 in this example is that the <code>output_format</code> is set to <code>csv</code>, which
 typically
 indicates comma-separated values.  However, the <code>default_delimiter</code> was
 set to be a vertical bar instead of a comma, so the csv tag is a
 misnomer in this case. (Think of CSV as meaning character separated values)</p>
 <source>
 jmeter.save.saveservice.output_format=csv
 jmeter.save.saveservice.assertion_results_failure_message=true
 jmeter.save.saveservice.default_delimiter=|
 </source>
 <p>
 The full set of properties that affect result file output is shown below.
 </p>
 <source>
 #---------------------------------------------------------------------------
 # Results file configuration
 #---------------------------------------------------------------------------
 
 # This section helps determine how result data will be saved.
 # The commented out values are the defaults.
 
 # legitimate values: xml, csv, db.  Only xml and csv are currently supported.
 #jmeter.save.saveservice.output_format=csv
 
 
 # true when field should be saved; false otherwise
 
 # assertion_results_failure_message only affects CSV output
 #jmeter.save.saveservice.assertion_results_failure_message=true
 #
 # legitimate values: none, first, all
 #jmeter.save.saveservice.assertion_results=none
 #
 #jmeter.save.saveservice.data_type=true
 #jmeter.save.saveservice.label=true
 #jmeter.save.saveservice.response_code=true
 # response_data is not currently supported for CSV output
 #jmeter.save.saveservice.response_data=false
 # Save ResponseData for failed samples
 #jmeter.save.saveservice.response_data.on_error=false
 #jmeter.save.saveservice.response_message=true
 #jmeter.save.saveservice.successful=true
 #jmeter.save.saveservice.thread_name=true
 #jmeter.save.saveservice.time=true
 #jmeter.save.saveservice.subresults=true
 #jmeter.save.saveservice.assertions=true
 #jmeter.save.saveservice.latency=true
 #jmeter.save.saveservice.connect_time=true
 #jmeter.save.saveservice.samplerData=false
 #jmeter.save.saveservice.responseHeaders=false
 #jmeter.save.saveservice.requestHeaders=false
 #jmeter.save.saveservice.encoding=false
 #jmeter.save.saveservice.bytes=true
+#jmeter.save.saveservice.sent_bytes=true
 #jmeter.save.saveservice.url=false
 #jmeter.save.saveservice.filename=false
 #jmeter.save.saveservice.hostname=false
 #jmeter.save.saveservice.thread_counts=true
 #jmeter.save.saveservice.sample_count=false
 #jmeter.save.saveservice.idle_time=true
 
 # Timestamp format - this only affects CSV output files
 # legitimate values: none, ms, or a format suitable for SimpleDateFormat
 #jmeter.save.saveservice.timestamp_format=ms
 #jmeter.save.saveservice.timestamp_format=yyyy/MM/dd HH:mm:ss.SSS
 
 # For use with Comma-separated value (CSV) files or other formats
 # where the fields' values are separated by specified delimiters.
 # Default:
 #jmeter.save.saveservice.default_delimiter=,
 # For TAB, since JMeter 2.3 one can use:
 #jmeter.save.saveservice.default_delimiter=\t
 
 # Only applies to CSV format files:
 # Print field names as first line in CSV
 #jmeter.save.saveservice.print_field_names=true
 
 # Optional list of JMeter variable names whose values are to be saved in the result data files.
 # Use commas to separate the names. For example:
 #sample_variables=SESSION_ID,REFERENCE
 # N.B. The current implementation saves the values in XML as attributes,
 # so the names must be valid XML names.
 # JMeter sends the variable to all servers
 # to ensure that the correct data is available at the client.
 
 # Optional xml processing instruction for line 2 of the file:
 #jmeter.save.saveservice.xml_pi=&lt;?xml-stylesheet type="text/xsl" href="sample.xsl"?&gt;
 
 # Prefix used to identify filenames that are relative to the current base
 #jmeter.save.saveservice.base_prefix=~/
 
 # AutoFlush on each line written in XML or CSV output
 # Setting this to true will result in less test results data loss in case of Crash
 # but with impact on performances, particularly for intensive tests (low or no pauses)
 # Since JMeter 2.10, this is false by default
 #jmeter.save.saveservice.autoflush=false
 
 # Put the start time stamp in logs instead of the end
 sampleresult.timestamp.start=true
 
 # Whether to use System.nanoTime() - otherwise only use System.currentTimeMillis()
 #sampleresult.useNanoTime=true
 
 # Use a background thread to calculate the nanoTime offset
 # Set this to &lt;= 0 to disable the background thread
 #sampleresult.nanoThreadSleep=5000
 </source>
 <p>
 The date format to be used for the <code>timestamp_format</code> is described in <a
 HREF="http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html">
 <b>SimpleDateFormat</b></a>.
 The timestamp format is used for both writing and reading files.
 If the format is set to "<code>ms</code>", and the column does not parse as a long integer,
 JMeter (2.9+) will try the following formats:
 <ul>
 <li><code>yyyy/MM/dd HH:mm:ss.SSS</code></li>
 <li><code>yyyy/MM/dd HH:mm:ss</code></li>
 <li><code>yyyy-MM-dd HH:mm:ss.SSS</code></li>
 <li><code>yyyy-MM-dd HH:mm:ss</code></li>
 <li><code>MM/dd/yy HH:mm:ss</code> (this is for compatibility with previous versions; it is not recommended as a format)</li>
 </ul> 
 Matching is now also strict (non-lenient).
 JMeter 2.8 and earlier used lenient mode which could result in timestamps with incorrect dates 
 (times were usually correct).</p>
 <subsection name="&sect-num;.1.1 Sample Variables" anchor="sample_variables">
 <p>
 JMeter supports the <code>sample_variables</code> 
 property to define a list of additional JMeter variables which are to be saved with
 each sample in the JTL files. The values are written to CSV files as additional columns,
 and as additional attributes in XML files. See above for an example.
 </p>
 </subsection>
 
 <subsection name="&sect-num;.1.2 Sample Result Save Configuration" anchor="sample_configuration">
 <p>
 Listeners can be configured to save different items to the result log files (JTL) by using the Config popup as shown below.
 The defaults are defined as described in  the <a href="#defaults">Listener Default Configuration</a> section above.
 Items with (CSV) after the name only apply to the CSV format; items with (XML) only apply to XML format.
 CSV format cannot currently be used to save any items that include line-breaks.
 </p>
 <figure image="sample_result_config.png"><br/><b>Configuration dialogue</b></figure>
 </subsection>
 <p>
 Note that cookies, method and the query string are saved as part of the "<code>Sampler Data</code>" option.
 </p>
 </section>
 
 <section name="&sect-num;.2 non-GUI (batch) test runs" anchor="batch">
 <p>
 When running in non-GUI mode, the <code>-l</code> flag can be used to create a top-level listener for the test run.
 This is in addition to any Listeners defined in the test plan.
 The configuration of this listener is controlled by entries in the file <code>jmeter.properties</code>
 as described in the previous section.
 </p>
 <p>
 This feature can be used to specify different data and log files for each test run, for example:
 </p>
 <source>
 jmeter -n -t testplan.jmx -l testplan_01.jtl -j testplan_01.log
 jmeter -n -t testplan.jmx -l testplan_02.jtl -j testplan_02.log
 </source>
 <p>
 Note that JMeter logging messages are written to the file <code>jmeter.log</code> by default.
 This file is recreated each time, so if you want to keep the log files for each run,
 you will need to rename it using the <code>-j</code> option as above. 
 </p>
 <p>JMeter supports variables in the log file name.
 If the filename contains  paired single-quotes, then the name is processed
 as a <code>SimpleDateFormat</code> format applied to the current date, for example:
 <code>log_file='jmeter_'yyyyMMddHHmmss'.tmp'</code>.
 This can be used to generate a unique name for each test run.
 </p>
 </section>
 
 <section name="&sect-num;.3 Resource usage" anchor="resources">
 <note> Listeners can use a lot of memory if there are a lot of samples.</note>
 <p>
 Most of the listeners currently keep a copy of every sample they display, apart from:
 </p>
 <ul>
 <li>Simple Data Writer</li>
 <li>BeanShell/BSF Listener</li>
 <li>Mailer Visualizer</li>
 <li>Monitor Results</li>
 <li>Summary Report</li>
 </ul>
 <p>
 The following Listeners no longer need to keep copies of every single sample.
 Instead, samples with the same elapsed time are aggregated.
 Less memory is now needed, especially if most samples only take a second or two at most.
 </p>
 <ul>
 <li>Aggregate Report</li>
 <li>Aggregate Graph</li>
 </ul>
 <p>To minimize the amount of memory needed, use the Simple Data Writer, and use the CSV format.</p>
 </section>
 
 <section name="&sect-num;.4 CSV Log format" anchor="csvlogformat">
 <p>
 The CSV log format depends on which data items are selected in the configuration.
 Only the specified data items are recorded in the file.
 The order of appearance of columns is fixed, and is as follows:
 </p>
 <ul>
 <li><code>timeStamp</code> - in milliseconds since 1/1/1970</li>
 <li><code>elapsed</code> - in milliseconds</li>
 <li><code>label</code> - sampler label</li>
 <li><code>responseCode</code> - e.g. <code>200</code>, <code>404</code></li>
 <li><code>responseMessage</code> - e.g. <code>OK</code></li>
 <li><code>threadName</code></li>
 <li><code>dataType</code> - e.g. <code>text</code></li>
 <li><code>success</code> - <code>true</code> or <code>false</code></li>
 <li><code>failureMessage</code> - if any</li>
 <li><code>bytes</code> - number of bytes in the sample</li>
+<li><code>sentBytes</code> - number of bytes sent for the sample</li>
 <li><code>grpThreads</code> - number of active threads in this thread group</li>
 <li><code>allThreads</code> - total number of active threads in all groups</li>
 <li><code>URL</code></li>
 <li><code>Filename</code> - if <code>Save Response to File</code> was used</li>
 <li><code>latency</code> - time to first response</li>
 <li><code>connect</code> - time to establish connection</li>
 <li><code>encoding</code></li>
 <li><code>SampleCount</code> - number of samples (1, unless multiple samples are aggregated)</li>
 <li><code>ErrorCount</code> - number of errors (0 or 1, unless multiple samples are aggregated)</li>
 <li><code>Hostname</code> - where the sample was generated</li>
 <li><code>IdleTime</code> - number of milliseconds of 'Idle' time (normally 0)</li>
 <li><code>Variables</code>, if specified</li>
 </ul>
 
 </section>
 
 <section name="&sect-num;.5 XML Log format 2.1" anchor="xmlformat2.1">
 <p>
 The format of the updated XML (2.1) is as follows (line breaks will be different):
 </p>
 <source>
 &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 &lt;testResults version="1.2"&gt;
 
 -- HTTP Sample, with nested samples 
 
 &lt;httpSample t="1392" lt="351" ts="1144371014619" s="true" 
      lb="HTTP Request" rc="200" rm="OK" 
      tn="Listen 1-1" dt="text" de="iso-8859-1" by="12407"&gt;
   &lt;httpSample t="170" lt="170" ts="1144371015471" s="true" 
         lb="http://www.apache.org/style/style.css" rc="200" rm="OK" 
         tn="Listen 1-1" dt="text" de="ISO-8859-1" by="1002"&gt;
     &lt;responseHeader class="java.lang.String"&gt;HTTP/1.1 200 OK
 Date: Fri, 07 Apr 2006 00:50:14 GMT
 &vellip;
 Content-Type: text/css
 &lt;/responseHeader&gt;
     &lt;requestHeader class="java.lang.String"&gt;MyHeader: MyValue&lt;/requestHeader&gt;
     &lt;responseData class="java.lang.String"&gt;body, td, th {
     font-size: 95%;
     font-family: Arial, Geneva, Helvetica, sans-serif;
     color: black;
     background-color: white;
 }
 &vellip;
 &lt;/responseData&gt;
     &lt;cookies class="java.lang.String"&gt;&lt;/cookies&gt;
     &lt;method class="java.lang.String"&gt;GET&lt;/method&gt;
     &lt;queryString class="java.lang.String"&gt;&lt;/queryString&gt;
     &lt;url&gt;http://www.apache.org/style/style.css&lt;/url&gt;
   &lt;/httpSample&gt;
   &lt;httpSample t="200" lt="180" ts="1144371015641" s="true" 
      lb="http://www.apache.org/images/asf_logo_wide.gif" 
      rc="200" rm="OK" tn="Listen 1-1" dt="bin" de="ISO-8859-1" by="5866"&gt;
     &lt;responseHeader class="java.lang.String"&gt;HTTP/1.1 200 OK
 Date: Fri, 07 Apr 2006 00:50:14 GMT
 &vellip;
 Content-Type: image/gif
 &lt;/responseHeader&gt;
     &lt;requestHeader class="java.lang.String"&gt;MyHeader: MyValue&lt;/requestHeader&gt;
     &lt;responseData class="java.lang.String"&gt;http://www.apache.org/asf.gif&lt;/responseData&gt;
       &lt;responseFile class="java.lang.String"&gt;Mixed1.html&lt;/responseFile&gt;
     &lt;cookies class="java.lang.String"&gt;&lt;/cookies&gt;
     &lt;method class="java.lang.String"&gt;GET&lt;/method&gt;
     &lt;queryString class="java.lang.String"&gt;&lt;/queryString&gt;
     &lt;url&gt;http://www.apache.org/asf.gif&lt;/url&gt;
   &lt;/httpSample&gt;
   &lt;responseHeader class="java.lang.String"&gt;HTTP/1.1 200 OK
 Date: Fri, 07 Apr 2006 00:50:13 GMT
 &vellip;
 Content-Type: text/html; charset=ISO-8859-1
 &lt;/responseHeader&gt;
   &lt;requestHeader class="java.lang.String"&gt;MyHeader: MyValue&lt;/requestHeader&gt;
   &lt;responseData class="java.lang.String"&gt;&lt;!DOCTYPE html PUBLIC &quot;-//W3C//DTD XHTML 1.0 Transitional//EN&quot;
                &quot;http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd&quot;&gt;
 &vellip;
 &lt;html&gt;
  &lt;head&gt;
 &vellip;
  &lt;/head&gt;
  &lt;body&gt;        
 &vellip;
  &lt;/body&gt;
 &lt;/html&gt;
 &lt;/responseData&gt;
   &lt;cookies class="java.lang.String"&gt;&lt;/cookies&gt;
   &lt;method class="java.lang.String"&gt;GET&lt;/method&gt;
   &lt;queryString class="java.lang.String"&gt;&lt;/queryString&gt;
   &lt;url&gt;http://www.apache.org/&lt;/url&gt;
 &lt;/httpSample&gt;
 
 -- non HTTP Sample
 
 &lt;sample t="0" lt="0" ts="1144372616082" s="true" lb="Example Sampler"
     rc="200" rm="OK" tn="Listen 1-1" dt="text" de="ISO-8859-1" by="10"&gt;
   &lt;responseHeader class="java.lang.String"&gt;&lt;/responseHeader&gt;
   &lt;requestHeader class="java.lang.String"&gt;&lt;/requestHeader&gt;
   &lt;responseData class="java.lang.String"&gt;Listen 1-1&lt;/responseData&gt;
   &lt;responseFile class="java.lang.String"&gt;Mixed2.unknown&lt;/responseFile&gt;
   &lt;samplerData class="java.lang.String"&gt;ssssss&lt;/samplerData&gt;
 &lt;/sample&gt;
 
 &lt;/testResults&gt;
 </source>
 <p>
 Note that the sample node name may be either "<code>sample</code>" or "<code>httpSample</code>".
 </p>
 </section>
 
 <section name="&sect-num;.6 XML Log format 2.2" anchor="xmlformat2.2">
 <p>
 The format of the JTL files is identical for 2.2 and 2.1. Format 2.2 only affects JMX files.
 </p>
 </section>
 
 <section name="&sect-num;.7 Sample Attributes" anchor="attributes">
 <p>
 The sample attributes have the following meaning:
 </p>
 <table>
 <tr><th>Attribute</th><th>Content</th></tr>
 <tr><td><code>by</code></td><td>Bytes</td></tr>
+<tr><td><code>sby</code></td><td>Sent Bytes</td></tr>
 <tr><td><code>de</code></td><td>Data encoding</td></tr>
 <tr><td><code>dt</code></td><td>Data type</td></tr>
 <tr><td><code>ec</code></td><td>Error count (0 or 1, unless multiple samples are aggregated)</td></tr>
 <tr><td><code>hn</code></td><td>Hostname where the sample was generated</td></tr>
 <tr><td><code>it</code></td><td>Idle Time = time not spent sampling (milliseconds) (generally 0)</td></tr>
 <tr><td><code>lb</code></td><td>Label</td></tr>
 <tr><td><code>lt</code></td><td>Latency = time to initial response (milliseconds) - not all samplers support this</td></tr>
 <tr><td><code>ct</code></td><td>Connect Time = time to establish the connection (milliseconds) - not all samplers support this</td></tr>
 <tr><td><code>na</code></td><td>Number of active threads for all thread groups</td></tr>
 <tr><td><code>ng</code></td><td>Number of active threads in this group</td></tr>
 <tr><td><code>rc</code></td><td>Response Code (e.g. <code>200</code>)</td></tr>
 <tr><td><code>rm</code></td><td>Response Message (e.g. <code>OK</code>)</td></tr>
 <tr><td> <code>s</code></td><td>Success flag (<code>true</code>/<code>false</code>)</td></tr>
 <tr><td><code>sc</code></td><td>Sample count (1, unless multiple samples are aggregated)</td></tr>
 <tr><td> <code>t</code></td><td>Elapsed time (milliseconds)</td></tr>
 <tr><td><code>tn</code></td><td>Thread Name</td></tr>
 <tr><td><code>ts</code></td><td>timeStamp (milliseconds since midnight Jan 1, 1970 UTC)</td></tr>
 <tr><td><code>varname</code></td><td>Value of the named variable</td></tr>
 </table>
 
 <note>
 JMeter allows additional variables to be saved with the test plan.
 Currently, the variables are saved as additional attributes.
 The testplan variable name is used as the attribute name.
 See <a href="#sample_variables">Sample variables</a> (above) for more information.
 </note>
 </section>
 
 <section name="&sect-num;.8 Saving response data" anchor="saving">
 <p>
 As shown above, the response data can be saved in the XML log file if required.
 However, this can make the file rather large, and the text has to be encoded so
 that it is still valid XML. Also, images cannot be included.
 Only sample responses with the type <code>TEXT</code> can be saved.
 <br/>
 Another solution is to use the Post-Processor <complink name="Save_Responses_to_a_file">Save Responses to a file</complink>.
 This generates a new file for each sample, and saves the file name with the sample.
 The file name can then be included in the sample log output.
 The data will be retrieved from the file if necessary when the sample log file is reloaded.
 </p>
 </section>
 <section name="&sect-num;.9 Loading (reading) response data" anchor="loading">
 <p>To view an existing results file, you can use the File "<code>Browse&hellip;</code>" button to select a file.
 If necessary, just create a dummy testplan with the appropriate Listener in it.
 </p>
 <p>Results can be read from XML or CSV format files.
 When reading from CSV results files, the header (if present) is used to determine which fields were saved.
 <b>In order to interpret a header-less CSV file correctly, the appropriate JMeter properties must be set.</b>
 </p>
 <note>
 JMeter does not clear any current data before loading the new file thus allowing files to be merged.
 If you want to clear the current data, use the menu item:
   <menuchoice>
     <guimenuitem>Run</guimenuitem>
     <guimenuitem>Clear</guimenuitem>
     <shortcut>
       <keycombo>
         <keysym>Ctrl</keysym>
         <keysym>Shift</keysym>
         <keysym>E</keysym>
       </keycombo>
     </shortcut>
   </menuchoice>
 or
   <menuchoice>
     <guimenuitem>Run</guimenuitem>
     <guimenuitem>Clear All</guimenuitem>
     <shortcut>
       <keycombo>
         <keysym>Ctrl</keysym>
         <keysym>E</keysym>
       </keycombo>
     </shortcut>
   </menuchoice>
 before loading the file.
 </note>
 </section>
 <section name="&sect-num;.10 Saving Listener GUI data" anchor="screencap">
 <p>JMeter is capable of saving any listener as a PNG file. To do so, select the
 listener in the left panel. Click
   <menuchoice>
     <guimenuitem>Edit</guimenuitem>
     <guimenuitem>Save Node As Image</guimenuitem>
   </menuchoice>.
 A file dialog will
 appear. Enter the desired name and save the listener.
 </p>
 <p>
 The Listeners which generate output as tables can also be saved using Copy/Paste.
 Select the desired cells in the table, and use the OS Copy short-cut (normally <keycombo><keysym>Ctrl</keysym><keysym>C</keysym></keycombo>).
 The data will be saved to the clipboard, from where it can be pasted into another application,
 e.g. a spreadsheet or text editor.
 </p>
 <figure image="save_image.png">Figure 1 - <menuchoice><guimenuitem>Edit</guimenuitem><guimenuitem>Save Node As Image</guimenuitem></menuchoice></figure>
 
 </section>
 </body>
 </document>
