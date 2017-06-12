diff --git a/bin/jmeter.properties b/bin/jmeter.properties
index a432ce82e..af8bd4ffb 100644
--- a/bin/jmeter.properties
+++ b/bin/jmeter.properties
@@ -39,1234 +39,1244 @@
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
 # Only available with HttpClient4
 #jmeter.save.saveservice.sent_bytes=true
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
 
+# Max size of bytes stored in memory per SampleResult
+# Ensure you don't exceed max capacity of a Java Array and remember 
+# that the higher it is, the higher JMeter will consume heap
+# Defaults to 10MB
+#httpsampler.max_bytes_to_store_per_request=10485760
+
+# Max size of buffer in bytes used when reading responses
+# Defaults to 64k
+#httpsampler.max_buffer_size=66560
+
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
diff --git a/src/components/org/apache/jmeter/assertions/SMIMEAssertion.java b/src/components/org/apache/jmeter/assertions/SMIMEAssertion.java
index 0ca284c23..9f874bcbc 100644
--- a/src/components/org/apache/jmeter/assertions/SMIMEAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/SMIMEAssertion.java
@@ -1,375 +1,375 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayInputStream;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.math.BigInteger;
 import java.security.GeneralSecurityException;
 import java.security.Security;
 import java.security.cert.CertificateException;
 import java.security.cert.CertificateFactory;
 import java.security.cert.X509Certificate;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 
 import javax.mail.MessagingException;
 import javax.mail.Session;
 import javax.mail.internet.MimeMessage;
 import javax.mail.internet.MimeMultipart;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
 import org.bouncycastle.asn1.x500.RDN;
 import org.bouncycastle.asn1.x500.X500Name;
 import org.bouncycastle.asn1.x500.style.BCStyle;
 import org.bouncycastle.asn1.x500.style.IETFUtils;
 import org.bouncycastle.asn1.x509.Extension;
 import org.bouncycastle.asn1.x509.GeneralName;
 import org.bouncycastle.asn1.x509.GeneralNames;
 import org.bouncycastle.cert.X509CertificateHolder;
 import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
 import org.bouncycastle.cms.CMSException;
 import org.bouncycastle.cms.SignerInformation;
 import org.bouncycastle.cms.SignerInformationStore;
 import org.bouncycastle.cms.SignerInformationVerifier;
 import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
 import org.bouncycastle.jce.provider.BouncyCastleProvider;
 import org.bouncycastle.mail.smime.SMIMEException;
 import org.bouncycastle.mail.smime.SMIMESignedParser;
 import org.bouncycastle.operator.OperatorCreationException;
 import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
 import org.bouncycastle.util.Store;
 
 /**
  * Helper class which isolates the BouncyCastle code.
  */
 class SMIMEAssertion {
 
     // Use the name of the test element, otherwise cannot enable/disable debug from the GUI
     private static final Logger log = LoggingManager.getLoggerForShortName(SMIMEAssertionTestElement.class.getName());
 
     SMIMEAssertion() {
         super();
     }
 
     public static AssertionResult getResult(SMIMEAssertionTestElement testElement, SampleResult response, String name) {
         checkForBouncycastle();
         AssertionResult res = new AssertionResult(name);
         try {
             MimeMessage msg = null;
             final int msgPos = testElement.getSpecificMessagePositionAsInt();
             if (msgPos < 0){ // means counting from end
                 SampleResult[] subResults = response.getSubResults();
                 final int pos = subResults.length + msgPos;
                 if (log.isDebugEnabled()) {
                     log.debug("Getting message number: "+pos+" of "+subResults.length);
                 }
                 msg = getMessageFromResponse(response,pos);
             } else {
                 if (log.isDebugEnabled()) {
                     log.debug("Getting message number: "+msgPos);
                 }
                 msg = getMessageFromResponse(response, msgPos);
             }
             
             SMIMESignedParser s = null;
             if (log.isDebugEnabled()) {
                 log.debug("Content-type: "+msg.getContentType());
             }
             if (msg.isMimeType("multipart/signed")) { // $NON-NLS-1$
                 MimeMultipart multipart = (MimeMultipart) msg.getContent();
                 s = new SMIMESignedParser(new BcDigestCalculatorProvider(), multipart);
             } else if (msg.isMimeType("application/pkcs7-mime") // $NON-NLS-1$
                     || msg.isMimeType("application/x-pkcs7-mime")) { // $NON-NLS-1$
                 s = new SMIMESignedParser(new BcDigestCalculatorProvider(), msg);
             }
 
             if (null != s) {
                 log.debug("Found signature");
 
                 if (testElement.isNotSigned()) {
                     res.setFailure(true);
                     res.setFailureMessage("Mime message is signed");
                 } else if (testElement.isVerifySignature() || !testElement.isSignerNoCheck()) {
                     res = verifySignature(testElement, s, name);
                 }
 
             } else {
                 log.debug("Did not find signature");
                 if (!testElement.isNotSigned()) {
                     res.setFailure(true);
                     res.setFailureMessage("Mime message is not signed");
                 }
             }
 
         } catch (MessagingException e) {
             String msg = "Cannot parse mime msg: " + e.getMessage();
             log.warn(msg, e);
             res.setFailure(true);
             res.setFailureMessage(msg);
         } catch (CMSException e) {
             res.setFailure(true);
             res.setFailureMessage("Error reading the signature: "
                     + e.getMessage());
         } catch (SMIMEException e) {
             res.setFailure(true);
             res.setFailureMessage("Cannot extract signed body part from signature: "
                     + e.getMessage());
         } catch (IOException e) { // should never happen
             log.error("Cannot read mime message content: " + e.getMessage(), e);
             res.setError(true);
             res.setFailureMessage(e.getMessage());
         }
 
         return res;
     }
 
     private static AssertionResult verifySignature(SMIMEAssertionTestElement testElement, SMIMESignedParser s, String name)
             throws CMSException {
         AssertionResult res = new AssertionResult(name);
 
         try {
             Store certs = s.getCertificates();
             SignerInformationStore signers = s.getSignerInfos();
             Iterator<?> signerIt = signers.getSigners().iterator();
 
             if (signerIt.hasNext()) {
 
                 SignerInformation signer = (SignerInformation) signerIt.next();
                 Iterator<?> certIt = certs.getMatches(signer.getSID()).iterator();
 
                 if (certIt.hasNext()) {
                     // the signer certificate
                     X509CertificateHolder cert = (X509CertificateHolder) certIt.next();
 
                     if (testElement.isVerifySignature()) {
 
                         SignerInformationVerifier verifier = null;
                         try {
                             verifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC")
                                     .build(cert);
                         } catch (OperatorCreationException e) {
                             log.error("Can't create a provider", e);
                         }
                         if (verifier == null || !signer.verify(verifier)) {
                             res.setFailure(true);
                             res.setFailureMessage("Signature is invalid");
                         }
                     }
 
                     if (testElement.isSignerCheckConstraints()) {
                         StringBuilder failureMessage = new StringBuilder();
 
                         String serial = testElement.getSignerSerial();
                         if (!JOrphanUtils.isBlank(serial)) {
                             BigInteger serialNbr = readSerialNumber(serial);
                             if (!serialNbr.equals(cert.getSerialNumber())) {
                                 res.setFailure(true);
                                 failureMessage
                                         .append("Serial number ")
                                         .append(serialNbr)
                                         .append(" does not match serial from signer certificate: ")
                                         .append(cert.getSerialNumber()).append("\n");
                             }
                         }
 
                         String email = testElement.getSignerEmail();
                         if (!JOrphanUtils.isBlank(email)) {
                             List<String> emailFromCert = getEmailFromCert(cert);
                             if (!emailFromCert.contains(email)) {
                                 res.setFailure(true);
                                 failureMessage
                                         .append("Email address \"")
                                         .append(email)
                                         .append("\" not present in signer certificate\n");
                             }
 
                         }
 
                         String subject = testElement.getSignerDn();
                         if (subject.length() > 0) {
                             final X500Name certPrincipal = cert.getSubject();
                             log.debug("DN from cert: " + certPrincipal.toString());
                             X500Name principal = new X500Name(subject);
                             log.debug("DN from assertion: " + principal.toString());
                             if (!principal.equals(certPrincipal)) {
                                 res.setFailure(true);
                                 failureMessage
                                         .append("Distinguished name of signer certificate does not match \"")
                                         .append(subject).append("\"\n");
                             }
                         }
 
                         String issuer = testElement.getIssuerDn();
                         if (issuer.length() > 0) {
                             final X500Name issuerX500Name = cert.getIssuer();
                             log.debug("IssuerDN from cert: " + issuerX500Name.toString());
                             X500Name principal = new X500Name(issuer);
                             log.debug("IssuerDN from assertion: " + principal);
                             if (!principal.equals(issuerX500Name)) {
                                 res.setFailure(true);
                                 failureMessage
                                         .append("Issuer distinguished name of signer certificate does not match \"")
                                         .append(subject).append("\"\n");
                             }
                         }
 
                         if (failureMessage.length() > 0) {
                             res.setFailureMessage(failureMessage.toString());
                         }
                     }
 
                     if (testElement.isSignerCheckByFile()) {
                         CertificateFactory cf = CertificateFactory
                                 .getInstance("X.509");
                         X509CertificateHolder certFromFile;
                         InputStream inStream = null;
                         try {
                             inStream = new BufferedInputStream(new FileInputStream(testElement.getSignerCertFile()));
                             certFromFile = new JcaX509CertificateHolder((X509Certificate) cf.generateCertificate(inStream));
                         } finally {
                             IOUtils.closeQuietly(inStream);
                         }
 
                         if (!certFromFile.equals(cert)) {
                             res.setFailure(true);
                             res.setFailureMessage("Signer certificate does not match certificate "
                                             + testElement.getSignerCertFile());
                         }
                     }
 
                 } else {
                     res.setFailure(true);
                     res.setFailureMessage("No signer certificate found in signature");
                 }
 
             }
 
             // TODO support multiple signers
             if (signerIt.hasNext()) {
                 log.warn("SMIME message contains multiple signers! Checking multiple signers is not supported.");
             }
 
         } catch (GeneralSecurityException e) {
             log.error(e.getMessage(), e);
             res.setError(true);
             res.setFailureMessage(e.getMessage());
         } catch (FileNotFoundException e) {
             res.setFailure(true);
             res.setFailureMessage("certificate file not found: " + e.getMessage());
         }
 
         return res;
     }
 
     /**
      * extracts a MIME message from the SampleResult
      */
     private static MimeMessage getMessageFromResponse(SampleResult response,
             int messageNumber) throws MessagingException {
         SampleResult[] subResults = response.getSubResults();
 
         if (messageNumber >= subResults.length || messageNumber < 0) {
             throw new MessagingException("Message number not present in results: "+messageNumber);
         }
 
         final SampleResult sampleResult = subResults[messageNumber];
         if (log.isDebugEnabled()) {
-            log.debug("Bytes: "+sampleResult.getBytes()+" CT: "+sampleResult.getContentType());
+            log.debug("Bytes: "+sampleResult.getBytesAsLong()+" CT: "+sampleResult.getContentType());
         }
         byte[] data = sampleResult.getResponseData();
         Session session = Session.getDefaultInstance(new Properties());
         MimeMessage msg = new MimeMessage(session, new ByteArrayInputStream(data));
 
         log.debug("msg.getSize() = " + msg.getSize());
         return msg;
     }
 
     /**
      * Convert the value of <code>serialString</code> into a BigInteger. Strings
      * starting with 0x or 0X are parsed as hex numbers, otherwise as decimal
      * number.
      * 
      * @param serialString
      *            the String representation of the serial Number
      * @return the BitInteger representation of the serial Number
      */
     private static BigInteger readSerialNumber(String serialString) {
         if (serialString.startsWith("0x") || serialString.startsWith("0X")) { // $NON-NLS-1$  // $NON-NLS-2$
             return new BigInteger(serialString.substring(2), 16);
         } 
         return new BigInteger(serialString);
     }
 
     /**
      * Extract email addresses from a certificate
      * 
      * @param cert the X509 certificate holder
      * @return a List of all email addresses found
      * @throws CertificateException
      */
     private static List<String> getEmailFromCert(X509CertificateHolder cert)
             throws CertificateException {
         List<String> res = new ArrayList<>();
 
         X500Name subject = cert.getSubject();
         for (RDN emails : subject.getRDNs(BCStyle.EmailAddress)) {
             for (AttributeTypeAndValue emailAttr: emails.getTypesAndValues()) {
                 log.debug("Add email from RDN: " + IETFUtils.valueToString(emailAttr.getValue()));
                 res.add(IETFUtils.valueToString(emailAttr.getValue()));
             }
         }
 
         Extension subjectAlternativeNames = cert
                 .getExtension(Extension.subjectAlternativeName);
         if (subjectAlternativeNames != null) {
             for (GeneralName name : GeneralNames.getInstance(
                     subjectAlternativeNames.getParsedValue()).getNames()) {
                 if (name.getTagNo() == GeneralName.rfc822Name) {
                     String email = IETFUtils.valueToString(name.getName());
                     log.debug("Add email from subjectAlternativeName: " + email);
                     res.add(email);
                 }
             }
         }
 
         return res;
     }
 
     /**
      * Check if the Bouncycastle jce provider is installed and dynamically load
      * it, if needed;
      */
     private static void checkForBouncycastle() {
         if (null == Security.getProvider("BC")) { // $NON-NLS-1$
             Security.addProvider(new BouncyCastleProvider());
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/SizeAssertion.java b/src/components/org/apache/jmeter/assertions/SizeAssertion.java
index ec75501ad..8dcbbc3a2 100644
--- a/src/components/org/apache/jmeter/assertions/SizeAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/SizeAssertion.java
@@ -1,264 +1,264 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.Serializable;
 import java.text.MessageFormat;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedAssertion;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.util.JMeterUtils;
 
 //@see org.apache.jmeter.assertions.SizeAssertionTest for unit tests
 
 /**
  * Checks if the results of a Sample matches a particular size.
  * 
  */
 public class SizeAssertion extends AbstractScopedAssertion implements Serializable, Assertion {
 
     private static final long serialVersionUID = 241L;
 
     // Static int to signify the type of logical comparator to assert
     public static final int EQUAL = 1;
 
     public static final int NOTEQUAL = 2;
 
     public static final int GREATERTHAN = 3;
 
     public static final int LESSTHAN = 4;
 
     public static final int GREATERTHANEQUAL = 5;
 
     public static final int LESSTHANEQUAL = 6;
 
     /** Key for storing assertion-information in the jmx-file. */
     private static final String SIZE_KEY = "SizeAssertion.size"; // $NON-NLS-1$
 
     private static final String OPERATOR_KEY = "SizeAssertion.operator"; // $NON-NLS-1$
     
     private static final String TEST_FIELD = "Assertion.test_field";  // $NON-NLS-1$
 
     private static final String RESPONSE_NETWORK_SIZE = "SizeAssertion.response_network_size"; // $NON-NLS-1$
 
     private static final String RESPONSE_HEADERS = "SizeAssertion.response_headers"; // $NON-NLS-1$
 
     private static final String RESPONSE_BODY = "SizeAssertion.response_data"; // $NON-NLS-1$
 
     private static final String RESPONSE_CODE = "SizeAssertion.response_code"; // $NON-NLS-1$
 
     private static final String RESPONSE_MESSAGE = "SizeAssertion.response_message"; // $NON-NLS-1$
 
     /**
      * Returns the result of the Assertion. 
      * Here it checks the Sample responseData length.
      */
     @Override
     public AssertionResult getResult(SampleResult response) {
         AssertionResult result = new AssertionResult(getName());
         result.setFailure(false);
         long resultSize=0;
         if (isScopeVariable()){
             String variableName = getVariableName();
             String value = getThreadContext().getVariables().get(variableName);
             try {
-                resultSize = Integer.parseInt(value);
+                resultSize = Long.parseLong(value);
             } catch (NumberFormatException e) {
                 result.setFailure(true);
                 result.setFailureMessage("Error parsing variable name: "+variableName+" value: "+value);
                 return result;
             }
         } else if (isTestFieldResponseHeaders()) {
             resultSize = response.getHeadersSize();
         }  else if (isTestFieldResponseBody()) {
-            resultSize = response.getBodySize();
+            resultSize = response.getBodySizeAsLong();
         } else if (isTestFieldResponseCode()) {
             resultSize = response.getResponseCode().length();
         } else if (isTestFieldResponseMessage()) {
             resultSize = response.getResponseMessage().length();
         } else {
-            resultSize = response.getBytes();
+            resultSize = response.getBytesAsLong();
         }
         // is the Sample the correct size?
         final String msg = compareSize(resultSize);
         if (msg.length() > 0) {
             result.setFailure(true);
             Object[] arguments = { Long.valueOf(resultSize), msg, Long.valueOf(getAllowedSize()) };
             String message = MessageFormat.format(
                     JMeterUtils.getResString("size_assertion_failure"), arguments); //$NON-NLS-1$
             result.setFailureMessage(message);
         }
         return result;
     }
 
     /**
      * Returns the size in bytes to be asserted.
      * @return The allowed size
      */
     public String getAllowedSize() {
         return getPropertyAsString(SIZE_KEY);
     }
 
     /**
      Set the operator used for the assertion. Has to be one of
      <dl>
      * <dt>EQUAL</dt><dd>1</dd>
      * <dt>NOTEQUAL</dt><dd>2</dd>
      * <dt>GREATERTHAN</dt><dd>3</dd>
      * <dt>LESSTHAN</dt><dd>4</dd>
      * <dt>GREATERTHANEQUAL</dt><dd>5</dd>
      * <dt>LESSTHANEQUAL</dt><dd>6</dd>
      * </dl>
      * @param operator The operator to be used in the assertion
      */
     public void setCompOper(int operator) {
         setProperty(new IntegerProperty(OPERATOR_KEY, operator));
 
     }
 
     /**
      * Returns the operator to be asserted. 
      * <dl>
      * <dt>EQUAL</dt><dd>1</dd>
      * <dt>NOTEQUAL</dt><dd>2</dd>
      * <dt>GREATERTHAN</dt><dd>3</dd>
      * <dt>LESSTHAN</dt><dd>4</dd>
      * <dt>GREATERTHANEQUAL</dt><dd>5</dd>
      * <dt>LESSTHANEQUAL</dt><dd>6</dd>
      * </dl>
      * @return The operator used for the assertion
      */
 
     public int getCompOper() {
         return getPropertyAsInt(OPERATOR_KEY);
     }
 
     /**
      * Set the size that shall be asserted.
      * 
      * @param size a number of bytes. 
      */
     public void setAllowedSize(String size) {
             setProperty(SIZE_KEY, size);
     }
 
     /**
      * Set the size that should be used in the assertion
      * @param size The number of bytes
      */
     public void setAllowedSize(long size) {
         setProperty(SIZE_KEY, Long.toString(size));
     }
 
     /**
      * Compares the size of a return result to the set allowed size using a
      * logical comparator set in setLogicalComparator().
      * 
      * Possible values are: equal, not equal, greater than, less than, greater
      * than equal, less than equal.
      * 
      */
     private String compareSize(long resultSize) {
         String comparatorErrorMessage;
         long allowedSize = Long.parseLong(getAllowedSize());
         boolean result = false;
         int comp = getCompOper();
         switch (comp) {
         case EQUAL:
             result = (resultSize == allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_equal"); //$NON-NLS-1$
             break;
         case NOTEQUAL:
             result = (resultSize != allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_notequal"); //$NON-NLS-1$
             break;
         case GREATERTHAN:
             result = (resultSize > allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_greater"); //$NON-NLS-1$
             break;
         case LESSTHAN:
             result = (resultSize < allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_less"); //$NON-NLS-1$
             break;
         case GREATERTHANEQUAL:
             result = (resultSize >= allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_greaterequal"); //$NON-NLS-1$
             break;
         case LESSTHANEQUAL:
             result = (resultSize <= allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_lessequal"); //$NON-NLS-1$
             break;
         default:
             result = false;
             comparatorErrorMessage = "ERROR - invalid condition";
             break;
         }
         return result ? "" : comparatorErrorMessage;
     }
     
     private void setTestField(String testField) {
         setProperty(TEST_FIELD, testField);
     }
 
     public void setTestFieldNetworkSize(){
         setTestField(RESPONSE_NETWORK_SIZE);
     }
     
     public void setTestFieldResponseHeaders(){
         setTestField(RESPONSE_HEADERS);
     }
     
     public void setTestFieldResponseBody(){
         setTestField(RESPONSE_BODY);
     }
     
     public void setTestFieldResponseCode(){
         setTestField(RESPONSE_CODE);
     }
     
     public void setTestFieldResponseMessage(){
         setTestField(RESPONSE_MESSAGE);
     }
 
     public String getTestField() {
         return getPropertyAsString(TEST_FIELD);
     }
 
     public boolean isTestFieldNetworkSize(){
         return RESPONSE_NETWORK_SIZE.equals(getTestField());
     }
 
     public boolean isTestFieldResponseHeaders(){
         return RESPONSE_HEADERS.equals(getTestField());
     }
     
     public boolean isTestFieldResponseBody(){
         return RESPONSE_BODY.equals(getTestField());
     }
 
     public boolean isTestFieldResponseCode(){
         return RESPONSE_CODE.equals(getTestField());
     }
 
     public boolean isTestFieldResponseMessage(){
         return RESPONSE_MESSAGE.equals(getTestField());
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/SamplerResultTab.java b/src/components/org/apache/jmeter/visualizers/SamplerResultTab.java
index 67c7e3f05..04be97e42 100644
--- a/src/components/org/apache/jmeter/visualizers/SamplerResultTab.java
+++ b/src/components/org/apache/jmeter/visualizers/SamplerResultTab.java
@@ -1,569 +1,569 @@
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
-                statsBuff.append(JMeterUtils.getResString("view_results_size_in_bytes")).append(sampleResult.getBytes()).append(NL); //$NON-NLS-1$
+                statsBuff.append(JMeterUtils.getResString("view_results_size_in_bytes")).append(sampleResult.getBytesAsLong()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_sent_bytes")).append(sampleResult.getSentBytes()).append(NL); //$NON-NLS-1$
                 statsBuff.append(JMeterUtils.getResString("view_results_size_headers_in_bytes")).append(sampleResult.getHeadersSize()).append(NL); //$NON-NLS-1$
-                statsBuff.append(JMeterUtils.getResString("view_results_size_body_in_bytes")).append(sampleResult.getBodySize()).append(NL); //$NON-NLS-1$
+                statsBuff.append(JMeterUtils.getResString("view_results_size_body_in_bytes")).append(sampleResult.getBodySizeAsLong()).append(NL); //$NON-NLS-1$
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
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_connect_time"), sampleResult.getConnectTime())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_latency"), sampleResult.getLatency())); //$NON-NLS-1$
-                resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_size_in_bytes"), sampleResult.getBytes())); //$NON-NLS-1$
+                resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_size_in_bytes"), sampleResult.getBytesAsLong())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_sent_bytes"),sampleResult.getSentBytes())); //$NON-NLS-1$
                 resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_size_headers_in_bytes"), sampleResult.getHeadersSize())); //$NON-NLS-1$
-                resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_size_body_in_bytes"), sampleResult.getBodySize())); //$NON-NLS-1$
+                resultModel.addRow(new RowResult(JMeterUtils.getParsedLabel("view_results_size_body_in_bytes"), sampleResult.getBodySizeAsLong())); //$NON-NLS-1$
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
index 142c81e24..d240ff165 100644
--- a/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
@@ -1,349 +1,349 @@
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
             "table_visualizer_sent_bytes",       // $NON-NLS-1$
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
                 new Functor("getSentBytes"),               // $NON-NLS-1$
                 new Functor("getLatency"),             // $NON-NLS-1$
                 new Functor("getConnectTime") },       // $NON-NLS-1$
                 new Functor[] { null, null, null, null, null, null, null, null, null, null },
                 new Class[] {
                 String.class, String.class, String.class, String.class, Long.class, ImageIcon.class, Long.class, Long.class, Long.class, Long.class });
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
-                            res.getBytes(),
+                            res.getBytesAsLong(),
                             res.getSentBytes(),
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
index d81854033..4a6ab3af5 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,366 +1,366 @@
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
-                res.setBytes(res.getBytes() + sampleResult.getBytes());
+                res.setBytes(res.getBytesAsLong() + sampleResult.getBytesAsLong());
                 res.setSentBytes(res.getSentBytes() + sampleResult.getSentBytes());
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
index 96afa0f22..51c25c24d 100644
--- a/src/core/org/apache/jmeter/report/core/Sample.java
+++ b/src/core/org/apache/jmeter/report/core/Sample.java
@@ -1,324 +1,324 @@
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
-    public int getReceivedBytes() {
-        return getData(int.class, CSVSaveService.CSV_BYTES).intValue();
+    public long getReceivedBytes() {
+        return getData(long.class, CSVSaveService.CSV_BYTES).longValue();
     }
 
     /**
      * Gets the number of sent bytes stored in the sample.
      *
      * @return the number of sent bytes stored in the sample
      */
     public long getSentBytes() {
         return getData(long.class, CSVSaveService.CSV_SENT_BYTES).longValue();
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
diff --git a/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java b/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java
index 8a5ec069f..6f22840f8 100644
--- a/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java
@@ -1,140 +1,140 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * The standard remote sample reporting should be more friendly to the main purpose of
  * remote testing - which is scalability.  To increase scalability, this class strips out the
  * response data before sending.
  *
  *
  */
 public class DataStrippingSampleSender extends AbstractSampleSender implements Serializable {
 
     private static final long serialVersionUID = -5556040298982085715L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
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
         log.info("Test Ended on " + host);
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
                 log.error("Error sending sample result over network ",e);
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
-        result.setBytes(result.getBytes());
+        result.setBytes(result.getBytesAsLong());
         result.setResponseData(SampleResult.EMPTY_BA);
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
         log.info("Using DataStrippingSampleSender for this run with stripAlsoOnError:"+stripAlsoOnError);
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/SampleResult.java b/src/core/org/apache/jmeter/samplers/SampleResult.java
index dc1717ab3..56a2e453a 100644
--- a/src/core/org/apache/jmeter/samplers/SampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/SampleResult.java
@@ -1,1467 +1,1501 @@
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
 
-    private int bytes = 0; // Allows override of sample size in case sampler does not want to store all the data
+    private long bytes = 0; // Allows override of sample size in case sampler does not want to store all the data
     
     private int headersSize = 0;
     
-    private int bodySize = 0;
+    private long bodySize = 0;
 
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
     private long sentBytes;
 
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
         sentBytes = res.sentBytes;
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
-        setBytes(getBytes() + subResult.getBytes());
+        setBytes(getBytesAsLong() + subResult.getBytesAsLong());
         setSentBytes(getSentBytes() + subResult.getSentBytes());
         setHeadersSize(getHeadersSize() + subResult.getHeadersSize());
-        setBodySize(getBodySize() + subResult.getBodySize());
+        setBodySize(getBodySizeAsLong() + subResult.getBodySizeAsLong());
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
+    
     /**
      * In the event the sampler does want to pass back the actual contents, we
      * still want to calculate the throughput. The bytes are the bytes of the
      * response data.
      *
      * @param length
      *            the number of bytes of the response data for this sample
      */
-    public void setBytes(int length) {
+    public void setBytes(long length) {
         bytes = length;
     }
-
+    
+    /**
+     * In the event the sampler does want to pass back the actual contents, we
+     * still want to calculate the throughput. The bytes are the bytes of the
+     * response data.
+     *
+     * @param length
+     *            the number of bytes of the response data for this sample
+     * @deprecated use setBytes(long)
+     */
+    @Deprecated 
+    public void setBytes(int length) {
+        setBytes((long) length);
+    }
+    
     /**
      * 
      * @param sentBytesCount long sent bytes
      */
     public void setSentBytes(long sentBytesCount) {
         sentBytes = sentBytesCount;
     }
 
     /**
      * @return the sentBytes
      */
     public long getSentBytes() {
         return sentBytes;
     }
     
     /**
      * return the bytes returned by the response.
      *
      * @return byte count
+     * @deprecated use getBytesAsLong 
      */
+    @Deprecated
     public int getBytes() {
+        return (int) getBytesAsLong();
+    }
+
+    /**
+     * return the bytes returned by the response.
+     *
+     * @return byte count
+     */
+    public long getBytesAsLong() {
         if (GETBYTES_NETWORK_SIZE) {
-            int tmpSum = this.getHeadersSize() + this.getBodySize();
+            long tmpSum = this.getHeadersSize() + this.getBodySizeAsLong();
             return tmpSum == 0 ? bytes : tmpSum;
         } else if (GETBYTES_HEADERS_SIZE) {
             return this.getHeadersSize();
         } else if (GETBYTES_BODY_REALSIZE) {
-            return this.getBodySize();
+            return this.getBodySizeAsLong();
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
+    @Deprecated
     public int getBodySize() {
+        return (int) getBodySizeAsLong();
+    }
+    
+    /**
+     * @return the body size in bytes
+     */
+    public long getBodySizeAsLong() {
         return bodySize == 0 ? responseData.length : bodySize;
     }
 
     /**
      * @param bodySize the body size to set
      */
-    public void setBodySize(int bodySize) {
+    public void setBodySize(long bodySize) {
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
diff --git a/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java b/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
index 8fc39d684..3d8076245 100644
--- a/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
@@ -1,138 +1,138 @@
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
 
-        setBytes(getBytes() + res.getBytes());
+        setBytes(getBytesAsLong() + res.getBytesAsLong());
         setSentBytes(getSentBytes() + res.getSentBytes());
 
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
index 33785a6d7..a2ea03f2b 100644
--- a/src/core/org/apache/jmeter/save/CSVSaveService.java
+++ b/src/core/org/apache/jmeter/save/CSVSaveService.java
@@ -1,1173 +1,1173 @@
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
     public static final String CSV_SENT_BYTES = "sentBytes"; // $NON-NLS-1$
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
-                result.setBytes(Integer.parseInt(text));
+                result.setBytes(Long.parseLong(text));
             }
-            
+
             if (saveConfig.saveSentBytes()) {
                 field = CSV_SENT_BYTES;
                 text = parts[i++];
                 result.setSentBytes(Long.parseLong(text));
             }
 
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
-        
+
         if (saveConfig.saveSentBytes()) {
             text.append(CSV_SENT_BYTES);
             text.append(delim);
         }
 
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
         headerLabelMethods.put(CSV_SENT_BYTES, new Functor("setSentBytes"));
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
-            text.append(sample.getBytes());
+            text.append(sample.getBytesAsLong());
         }
-        
+
         if (saveConfig.saveSentBytes()) {
             text.append(sample.getSentBytes());
         }
 
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
index 9fb9a91a0..726c59594 100644
--- a/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
+++ b/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
@@ -1,484 +1,483 @@
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
     private static final String ATT_SENT_BYTES        = "sby"; //$NON-NLS-1$
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
-            writer.addAttribute(ATT_BYTES, String.valueOf(res.getBytes()));
+            writer.addAttribute(ATT_BYTES, String.valueOf(res.getBytesAsLong()));
         }
         if (save.saveSentBytes()) {
             writer.addAttribute(ATT_SENT_BYTES, String.valueOf(res.getSentBytes()));
         }
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
-        res.setBytes(Converter.getInt(reader.getAttribute(ATT_BYTES)));
+        res.setBytes(Converter.getLong(reader.getAttribute(ATT_BYTES)));
         res.setSentBytes(Converter.getLong(reader.getAttribute(ATT_SENT_BYTES)));
         res.setSampleCount(Converter.getInt(reader.getAttribute(ATT_SAMPLE_COUNT),1)); // default is 1
         res.setErrorCount(Converter.getInt(reader.getAttribute(ATT_ERROR_COUNT),0)); // default is 0
         res.setGroupThreads(Converter.getInt(reader.getAttribute(ATT_GRP_THRDS)));
         res.setAllThreads(Converter.getInt(reader.getAttribute(ATT_ALL_THRDS)));
     }
 
     protected void readFile(String resultFileName, SampleResult res) {
         File in = new File(resultFileName);
         try (FileInputStream fis = new FileInputStream(in);
                 BufferedInputStream bis = new BufferedInputStream(fis)){
-            ByteArrayOutputStream outstream = new ByteArrayOutputStream(res.getBytes());
+            ByteArrayOutputStream outstream = new ByteArrayOutputStream(4096);
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
 
-
     /**
      * @param arg0 the mapper
      */
     public SampleResultConverter(Mapper arg0) {
         super(arg0);
     }
 }
diff --git a/src/core/org/apache/jmeter/util/Calculator.java b/src/core/org/apache/jmeter/util/Calculator.java
index fdee4c397..5be94522c 100644
--- a/src/core/org/apache/jmeter/util/Calculator.java
+++ b/src/core/org/apache/jmeter/util/Calculator.java
@@ -1,225 +1,225 @@
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
 
 import org.apache.jmeter.samplers.SampleResult;
 
 /**
  * Class to calculate various items that don't require all previous results to be saved:
  * <ul>
  *   <li>mean = average</li>
  *   <li>standard deviation</li>
  *   <li>minimum</li>
  *   <li>maximum</li>
  * </ul>
  */
 public class Calculator {
 
     private double sum = 0;
 
     private double sumOfSquares = 0;
 
     private double mean = 0;
 
     private double deviation = 0;
 
     private int count = 0;
 
     private long bytes = 0;
 
     private long maximum = Long.MIN_VALUE;
 
     private long minimum = Long.MAX_VALUE;
 
     private int errors = 0;
 
     private final String label;
 
     public Calculator() {
         this("");
     }
 
     public Calculator(String label) {
         this.label = label;
     }
 
     public void clear() {
         maximum = Long.MIN_VALUE;
         minimum = Long.MAX_VALUE;
         sum = 0;
         sumOfSquares = 0;
         mean = 0;
         deviation = 0;
         count = 0;
     }
 
     /**
      * Add the value for (possibly multiple) samples.
      * Updates the count, sum, min, max, sumOfSqaures, mean and deviation.
      * 
      * @param newValue the total value for all the samples.
      * @param sampleCount number of samples included in the value
      */
     private void addValue(long newValue, int sampleCount) {
         count += sampleCount;
         double currentVal = newValue;
         sum += currentVal;
         if (sampleCount > 1){
             minimum=Math.min(newValue/sampleCount, minimum);
             maximum=Math.max(newValue/sampleCount, maximum);
             // For n values in an aggregate sample the average value = (val/n)
             // So need to add n * (val/n) * (val/n) = val * val / n
             sumOfSquares += (currentVal * currentVal) / (sampleCount);
         } else { // no point dividing by 1
             minimum=Math.min(newValue, minimum);
             maximum=Math.max(newValue, maximum);
             sumOfSquares += currentVal * currentVal;
         }
         // Calculate each time, as likely to be called for each add
         mean = sum / count;
         deviation = Math.sqrt((sumOfSquares / count) - (mean * mean));
     }
 
 
     public void addBytes(long newValue) {
         bytes += newValue;
     }
 
     private long startTime = 0;
     private long elapsedTime = 0;
 
     /**
      * Add details for a sample result, which may consist of multiple samples.
      * Updates the number of bytes read, error count, startTime and elapsedTime
      * @param res the sample result; might represent multiple values
      */
     public void addSample(SampleResult res) {
-        addBytes(res.getBytes());
+        addBytes(res.getBytesAsLong());
         addValue(res.getTime(),res.getSampleCount());
         errors+=res.getErrorCount(); // account for multiple samples
         if (startTime == 0){ // not yet intialised
             startTime=res.getStartTime();
         } else {
             startTime = Math.min(startTime, res.getStartTime());
         }
         elapsedTime = Math.max(elapsedTime, res.getEndTime()-startTime);
     }
 
 
     public long getTotalBytes() {
         return bytes;
     }
 
 
     public double getMean() {
         return mean;
     }
 
     public Number getMeanAsNumber() {
         return Long.valueOf((long) mean);
     }
 
     public double getStandardDeviation() {
         return deviation;
     }
 
     public long getMin() {
         return minimum;
     }
 
     public long getMax() {
         return maximum;
     }
 
     public int getCount() {
         return count;
     }
 
     public String getLabel() {
         return label;
     }
 
     /**
      * Returns the raw double value of the percentage of samples with errors
      * that were recorded. (Between 0.0 and 1.0)
      *
      * @return the raw double value of the percentage of samples with errors
      *         that were recorded.
      */
     public double getErrorPercentage() {
         double rval = 0.0;
 
         if (count == 0) {
             return (rval);
         }
         rval = (double) errors / (double) count;
         return (rval);
     }
 
     /**
      * Returns the throughput associated to this sampler in requests per second.
      * May be slightly skewed because it takes the timestamps of the first and
      * last samples as the total time passed, and the test may actually have
      * started before that start time and ended after that end time.
      *
      * @return throughput associated to this sampler in requests per second
      */
     public double getRate() {
         if (elapsedTime == 0) {
             return 0.0;
         }
 
         return ((double) count / (double) elapsedTime ) * 1000;
     }
 
     /**
      * calculates the average page size, which means divide the bytes by number
      * of samples.
      *
      * @return average page size in bytes
      */
     public double getAvgPageBytes() {
         if (count > 0 && bytes > 0) {
             return (double) bytes / count;
         }
         return 0.0;
     }
 
     /**
      * Throughput in bytes / second
      *
      * @return throughput in bytes/second
      */
     public double getBytesPerSecond() {
         if (elapsedTime > 0) {
             return bytes / ((double) elapsedTime / 1000); // 1000 = millisecs/sec
         }
         return 0.0;
     }
 
     /**
      * Throughput in kilobytes / second
      *
      * @return Throughput in kilobytes / second
      */
     public double getKBPerSecond() {
         return getBytesPerSecond() / 1024; // 1024=bytes per kb
     }
 
 }
diff --git a/src/core/org/apache/jmeter/visualizers/SamplingStatCalculator.java b/src/core/org/apache/jmeter/visualizers/SamplingStatCalculator.java
index 964cb376c..a4f98ddde 100644
--- a/src/core/org/apache/jmeter/visualizers/SamplingStatCalculator.java
+++ b/src/core/org/apache/jmeter/visualizers/SamplingStatCalculator.java
@@ -1,296 +1,296 @@
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
 
 import java.util.Map;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jorphan.math.StatCalculatorLong;
 
 /**
  * Aggregate sample data container. Just instantiate a new instance of this
  * class, and then call {@link #addSample(SampleResult)} a few times, and pull
  * the stats out with whatever methods you prefer.
  *
  */
 public class SamplingStatCalculator {
     private final StatCalculatorLong calculator = new StatCalculatorLong();
 
     private double maxThroughput;
 
     private long firstTime;
 
     private String label;
 
     private volatile Sample currentSample;
 
     public SamplingStatCalculator(){ // Only for use by test code
         this("");
     }
 
     public SamplingStatCalculator(String label) {
         this.label = label;
         init();
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         firstTime = Long.MAX_VALUE;
         calculator.clear();
         maxThroughput = Double.MIN_VALUE;
         currentSample = new Sample();
     }
 
     /**
      * Clear the counters (useful for differential stats)
      *
      */
     public synchronized void clear() {
         init();
     }
 
     public Sample getCurrentSample() {
         return currentSample;
     }
 
     /**
      * Get the elapsed time for the samples
      *
      * @return how long the samples took
      */
     public long getElapsed() {
         if (getCurrentSample().getEndTime() == 0) {
             return 0;// No samples collected ...
         }
         return getCurrentSample().getEndTime() - firstTime;
     }
 
     /**
      * Returns the throughput associated to this sampler in requests per second.
      * May be slightly skewed because it takes the timestamps of the first and
      * last samples as the total time passed, and the test may actually have
      * started before that start time and ended after that end time.
      *
      * @return throughput associated with this sampler per second
      */
     public double getRate() {
         if (calculator.getCount() == 0) {
             return 0.0; // Better behaviour when howLong=0 or lastTime=0
         }
 
         return getCurrentSample().getThroughput();
     }
 
     /**
      * Throughput in bytes / second
      *
      * @return throughput in bytes/second
      */
     public double getBytesPerSecond() {
         // Code duplicated from getPageSize()
         double rate = 0;
         if (this.getElapsed() > 0 && calculator.getTotalBytes() > 0) {
             rate = calculator.getTotalBytes() / ((double) this.getElapsed() / 1000);
         }
         if (rate < 0) {
             rate = 0;
         }
         return rate;
     }
 
     /**
      * Throughput in kilobytes / second
      *
      * @return Throughput in kilobytes / second
      */
     public double getKBPerSecond() {
         return getBytesPerSecond() / 1024; // 1024=bytes per kb
     }
 
     /**
      * calculates the average page size, which means divide the bytes by number
      * of samples.
      *
      * @return average page size in bytes (0 if sample count is zero)
      */
     public double getAvgPageBytes() {
         long count = calculator.getCount();
         if (count == 0) {
             return 0;
         }
         return calculator.getTotalBytes() / (double) count;
     }
 
     /**
      * @return the label of this component
      */
     public String getLabel() {
         return label;
     }
 
     /**
      * Records a sample.
      *
      * @param res
      *            the sample to record
      * @return newly created sample with current statistics
      *
      */
     public Sample addSample(SampleResult res) {
         long rtime, cmean, cstdv, cmedian, cpercent, eCount, endTime;
         double throughput;
         boolean rbool;
         synchronized (calculator) {
             calculator.addValue(res.getTime(), res.getSampleCount());
-            calculator.addBytes(res.getBytes());
+            calculator.addBytes(res.getBytesAsLong());
             setStartTime(res);
             eCount = getCurrentSample().getErrorCount();
             eCount += res.getErrorCount();
             endTime = getEndTime(res);
             long howLongRunning = endTime - firstTime;
             throughput = ((double) calculator.getCount() / (double) howLongRunning) * 1000.0;
             if (throughput > maxThroughput) {
                 maxThroughput = throughput;
             }
 
             rtime = res.getTime();
             cmean = (long)calculator.getMean();
             cstdv = (long)calculator.getStandardDeviation();
             cmedian = calculator.getMedian().longValue();
             cpercent = calculator.getPercentPoint( 0.500 ).longValue();
 // TODO cpercent is the same as cmedian here - why? and why pass it to "distributionLine"?
             rbool = res.isSuccessful();
         }
 
         long count = calculator.getCount();
         Sample s =
             new Sample( null, rtime, cmean, cstdv, cmedian, cpercent, throughput, eCount, rbool, count, endTime );
         currentSample = s;
         return s;
     }
 
     private long getEndTime(SampleResult res) {
         long endTime = res.getEndTime();
         long lastTime = getCurrentSample().getEndTime();
         if (lastTime < endTime) {
             lastTime = endTime;
         }
         return lastTime;
     }
 
     /**
      * @param res
      */
     private void setStartTime(SampleResult res) {
         long startTime = res.getStartTime();
         if (firstTime > startTime) {
             // this is our first sample, set the start time to current timestamp
             firstTime = startTime;
         }
     }
 
     /**
      * Returns the raw double value of the percentage of samples with errors
      * that were recorded. (Between 0.0 and 1.0)
      *
      * @return the raw double value of the percentage of samples with errors
      *         that were recorded.
      */
     public double getErrorPercentage() {
         double rval = 0.0;
 
         if (calculator.getCount() == 0) {
             return rval;
         }
         rval = (double) getCurrentSample().getErrorCount() / (double) calculator.getCount();
         return rval;
     }
 
     /**
      * For debugging purposes, only.
      */
     @Override
     public String toString() {
         StringBuilder mySB = new StringBuilder();
 
         mySB.append("Samples: " + this.getCount() + "  ");
         mySB.append("Avg: " + this.getMean() + "  ");
         mySB.append("Min: " + this.getMin() + "  ");
         mySB.append("Max: " + this.getMax() + "  ");
         mySB.append("Error Rate: " + this.getErrorPercentage() + "  ");
         mySB.append("Sample Rate: " + this.getRate());
         return mySB.toString();
     }
 
     /**
      * @return errorCount
      */
     public long getErrorCount() {
         return getCurrentSample().getErrorCount();
     }
 
     /**
      * @return Returns the maxThroughput.
      */
     public double getMaxThroughput() {
         return maxThroughput;
     }
 
     public Map<Number, Number[]> getDistribution() {
         return calculator.getDistribution();
     }
 
     public Number getPercentPoint(double percent) {
         return calculator.getPercentPoint(percent);
     }
 
     public long getCount() {
         return calculator.getCount();
     }
 
     public Number getMax() {
         return calculator.getMax();
     }
 
     public double getMean() {
         return calculator.getMean();
     }
 
     public Number getMeanAsNumber() {
         return Long.valueOf((long) calculator.getMean());
     }
 
     public Number getMedian() {
         return calculator.getMedian();
     }
 
     public Number getMin() {
         if (calculator.getMin().longValue() < 0) {
             return Long.valueOf(0);
         }
         return calculator.getMin();
     }
 
     public Number getPercentPoint(float percent) {
         return calculator.getPercentPoint(percent);
     }
 
     public double getStandardDeviation() {
         return calculator.getStandardDeviation();
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
index 9f7147274..7ae113e48 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
@@ -1,512 +1,566 @@
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
 
 import java.io.BufferedInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.Inet4Address;
 import java.net.Inet6Address;
 import java.net.InetAddress;
 import java.net.InterfaceAddress;
 import java.net.NetworkInterface;
 import java.net.SocketException;
 import java.net.URL;
 import java.net.UnknownHostException;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase.SourceType;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Base class for HTTP implementations used by the HTTPSamplerProxy sampler.
  */
 public abstract class HTTPAbstractImpl implements Interruptible, HTTPConstantsInterface {
     private enum CachedResourceMode {
         RETURN_200_CACHE(),
         RETURN_NO_SAMPLE(),
         RETURN_CUSTOM_STATUS()
     }
 
     /**
      * If true create a SampleResult with emply content and 204 response code 
      */
     private static final CachedResourceMode CACHED_RESOURCE_MODE = 
             CachedResourceMode.valueOf(
                     JMeterUtils.getPropDefault("cache_manager.cached_resource_mode", //$NON-NLS-1$
                     CachedResourceMode.RETURN_NO_SAMPLE.toString()));
     
     /**
      * SampleResult message when resource was in cache and mode is RETURN_200_CACHE
      */
     private static final String RETURN_200_CACHE_MESSAGE =
             JMeterUtils.getPropDefault("RETURN_200_CACHE.message","(ex cache)");//$NON-NLS-1$ $NON-NLS-2$
 
     /**
      * Custom response code for cached resource
      */
     private static final String RETURN_CUSTOM_STATUS_CODE = 
             JMeterUtils.getProperty("RETURN_CUSTOM_STATUS.code");//$NON-NLS-1$
 
     /**
      * Custom response message for cached resource
      */
     private static final String RETURN_CUSTOM_STATUS_MESSAGE = 
             JMeterUtils.getProperty("RETURN_CUSTOM_STATUS.message"); //$NON-NLS-1$
 
     protected final HTTPSamplerBase testElement;
 
     protected HTTPAbstractImpl(HTTPSamplerBase testElement){
         this.testElement = testElement;
     }
 
     protected abstract HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth);
 
     // Allows HTTPSamplerProxy to call threadFinished; subclasses can override if necessary
     protected void threadFinished() {
     }
 
     // Allows HTTPSamplerProxy to call notifyFirstSampleAfterLoopRestart; subclasses can override if necessary
     protected void notifyFirstSampleAfterLoopRestart() {
     }
 
     // Provide access to HTTPSamplerBase methods
     
     /**
      * Populates the provided HTTPSampleResult with details from the Exception.
      * Does not create a new instance, so should not be used directly to add a
      * subsample.
      * <p>
      * See {@link HTTPSamplerBase#errorResult(Throwable, HTTPSampleResult)}
      * 
      * @param t
      *            Exception representing the error.
      * @param res
      *            SampleResult to be modified
      * @return the modified sampling result containing details of the Exception.
      *         Invokes
      */
     protected HTTPSampleResult errorResult(Throwable t, HTTPSampleResult res) {
         return testElement.errorResult(t, res);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getArguments()}
      *
      * @return the arguments of the associated test element
      */
     protected Arguments getArguments() {
         return testElement.getArguments();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getAuthManager()}
      *
      * @return the {@link AuthManager} of the associated test element
      */
     protected AuthManager getAuthManager() {
         return testElement.getAuthManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getAutoRedirects()}
      *
      * @return flag whether to do auto redirects
      */
     protected boolean getAutoRedirects() {
         return testElement.getAutoRedirects();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getCacheManager()}
      *
      * @return the {@link CacheManager} of the associated test element
      */
     protected CacheManager getCacheManager() {
         return testElement.getCacheManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getConnectTimeout()}
      *
      * @return the connect timeout of the associated test element
      */
     protected int getConnectTimeout() {
         return testElement.getConnectTimeout();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getContentEncoding()}
      * @return the encoding of the content, i.e. its charset name
      */
     protected String getContentEncoding() {
         return testElement.getContentEncoding();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getCookieManager()}
      *
      * @return the {@link CookieManager} of the associated test element
      */
     protected CookieManager getCookieManager() {
         return testElement.getCookieManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getHeaderManager()}
      *
      * @return the {@link HeaderManager} of the associated test element
      */
     protected HeaderManager getHeaderManager() {
         return testElement.getHeaderManager();
     }
 
     /**
      * 
      * Get the collection of files as a list.
      * The list is built up from the filename/filefield/mimetype properties,
      * plus any additional entries saved in the FILE_ARGS property.
      * <p>
      * If there are no valid file entries, then an empty list is returned.
      * <p>
      * Invokes {@link HTTPSamplerBase#getHTTPFiles()}
      *
      * @return an array of file arguments (never <code>null</code>)
      */
     protected HTTPFileArg[] getHTTPFiles() {
         return testElement.getHTTPFiles();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getIpSource()}
      *
      * @return the configured ip source for the associated test element
      */
     protected String getIpSource() {
         return testElement.getIpSource();
     }
 
     /**
      * Gets the IP source address (IP spoofing) if one has been provided.
      * 
      * @return the IP source address to use (or <code>null</code>, if none provided or the device address could not be found)
      * @throws UnknownHostException if the hostname/ip for {@link #getIpSource()} could not be resolved or not interface was found for it
      * @throws SocketException if an I/O error occurs
      */
     protected InetAddress getIpSourceAddress() throws UnknownHostException, SocketException {
         final String ipSource = getIpSource();
         if (ipSource.trim().length() > 0) {
             Class<? extends InetAddress> ipClass = null;
             final SourceType sourceType = HTTPSamplerBase.SourceType.values()[testElement.getIpSourceType()];
             switch (sourceType) {
             case DEVICE:
                 ipClass = InetAddress.class;
                 break;
             case DEVICE_IPV4:
                 ipClass = Inet4Address.class;
                 break;
             case DEVICE_IPV6:
                 ipClass = Inet6Address.class;
                 break;
             case HOSTNAME:
             default:
                 return InetAddress.getByName(ipSource);
             }
 
             NetworkInterface net = NetworkInterface.getByName(ipSource);
             if (net != null) {
                 for (InterfaceAddress ia : net.getInterfaceAddresses()) {
                     final InetAddress inetAddr = ia.getAddress();
                     if (ipClass.isInstance(inetAddr)) {
                         return inetAddr;
                     }
                 }
                 throw new UnknownHostException("Interface " + ipSource
                         + " does not have address of type " + ipClass.getSimpleName());
             }
             throw new UnknownHostException("Cannot find interface " + ipSource);
         }
         return null; // did not want to spoof the IP address
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyHost()}
      *
      * @return the configured host to use as a proxy
      */
     protected String getProxyHost() {
         return testElement.getProxyHost();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyPass()}
      *
      * @return the configured password to use for the proxy
      */
     protected String getProxyPass() {
         return testElement.getProxyPass();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyPortInt()}
      *
      * @return the configured port to use for the proxy
      */
     protected int getProxyPortInt() {
         return testElement.getProxyPortInt();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyUser()}
      *
      * @return the configured user to use for the proxy
      */
     protected String getProxyUser() {
         return testElement.getProxyUser();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getResponseTimeout()}
      *
      * @return the configured timeout for responses
      */
     protected int getResponseTimeout() {
         return testElement.getResponseTimeout();
     }
 
     /**
      * Determine whether to send a file as the entire body of an
      * entity enclosing request such as POST, PUT or PATCH.
      * 
      * Invokes {@link HTTPSamplerBase#getSendFileAsPostBody()}
      *
      * @return flag whether to send a file as POST, PUT or PATCH
      */
     protected boolean getSendFileAsPostBody() {
         return testElement.getSendFileAsPostBody();
     }
 
     /**
      * Determine whether to send concatenated parameters as the entire body of an
      * entity enclosing request such as POST, PUT or PATCH.
      * 
      * Invokes {@link HTTPSamplerBase#getSendParameterValuesAsPostBody()}
      *
      * @return flag whether to send concatenated parameters as the entire body
      */
     protected boolean getSendParameterValuesAsPostBody() {
         return testElement.getSendParameterValuesAsPostBody();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getUseKeepAlive()}
      *
      * @return flag whether to use keep-alive for requests
      */
     protected boolean getUseKeepAlive() {
         return testElement.getUseKeepAlive();
     }
 
     /**
      * Determine if we should use <code>multipart/form-data</code> or
      * <code>application/x-www-form-urlencoded</code> for the post
      * <p>
      * Invokes {@link HTTPSamplerBase#getUseMultipartForPost()}
      *
      * @return <code>true</code> if <code>multipart/form-data</code> should be
      *         used and method is POST
      */
     protected boolean getUseMultipartForPost() {
         return testElement.getUseMultipartForPost();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getDoBrowserCompatibleMultipart()}
      *
      * @return flag whether we should do browser compatible multiparts
      */
     protected boolean getDoBrowserCompatibleMultipart() {
         return testElement.getDoBrowserCompatibleMultipart();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#hasArguments()}
      *
      * @return flag whether we have arguments to send
      */
     protected boolean hasArguments() {
         return testElement.hasArguments();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#isMonitor()}
      *
      * @return flag whether monitor is enabled
      */
     protected boolean isMonitor() {
         return testElement.isMonitor();
     }
 
     /**
      * Determine if the HTTP status code is successful or not i.e. in range 200
      * to 399 inclusive
      * <p>
      * Invokes {@link HTTPSamplerBase#isSuccessCode(int)}
      *
      * @param errorLevel
      *            status code to check
      * @return whether in range 200-399 or not
      */
     protected boolean isSuccessCode(int errorLevel) {
         return testElement.isSuccessCode(errorLevel);
     }
 
     /**
      * Read response from the input stream, converting to MD5 digest if the
      * useMD5 property is set.
      * <p>
      * For the MD5 case, the result byte count is set to the size of the
      * original response.
      * <p>
      * Closes the inputStream
      * <p>
      * Invokes
-     * {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, int)}
+     * {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, long)}
      * 
      * @param res
      *            sample to store information about the response into
      * @param instream
      *            input stream from which to read the response
      * @param responseContentLength
      *            expected input length or zero
      * @return the response or the MD5 of the response
      * @throws IOException
      *             if reading the result fails
      */
     protected byte[] readResponse(SampleResult res, InputStream instream,
             int responseContentLength) throws IOException {
+        return readResponse(res, instream, (long)responseContentLength);
+    }
+    /**
+     * Read response from the input stream, converting to MD5 digest if the
+     * useMD5 property is set.
+     * <p>
+     * For the MD5 case, the result byte count is set to the size of the
+     * original response.
+     * <p>
+     * Closes the inputStream
+     * <p>
+     * Invokes
+     * {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, long)}
+     * 
+     * @param res
+     *            sample to store information about the response into
+     * @param instream
+     *            input stream from which to read the response
+     * @param responseContentLength
+     *            expected input length or zero
+     * @return the response or the MD5 of the response
+     * @throws IOException
+     *             if reading the result fails
+     */
+    protected byte[] readResponse(SampleResult res, InputStream instream,
+            long responseContentLength) throws IOException {
         return testElement.readResponse(res, instream, responseContentLength);
     }
 
     /**
      * Read response from the input stream, converting to MD5 digest if the
      * useMD5 property is set.
      * <p>
      * For the MD5 case, the result byte count is set to the size of the
      * original response.
      * <p>
      * Closes the inputStream
      * <p>
-     * Invokes {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, int)}
+     * Invokes {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, long)}
      * 
      * @param res
      *            sample to store information about the response into
      * @param in
      *            input stream from which to read the response
      * @param contentLength
      *            expected input length or zero
      * @return the response or the MD5 of the response
      * @throws IOException
      *             when reading the result fails
+     * @deprecated use {@link HTTPAbstractImpl#readResponse(SampleResult, BufferedInputStream, long)
      */
+    @Deprecated
     protected byte[] readResponse(SampleResult res, BufferedInputStream in,
             int contentLength) throws IOException {
         return testElement.readResponse(res, in, contentLength);
     }
+    
+    /**
+     * Read response from the input stream, converting to MD5 digest if the
+     * useMD5 property is set.
+     * <p>
+     * For the MD5 case, the result byte count is set to the size of the
+     * original response.
+     * <p>
+     * Closes the inputStream
+     * <p>
+     * Invokes {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, long)}
+     * 
+     * @param res
+     *            sample to store information about the response into
+     * @param in
+     *            input stream from which to read the response
+     * @param contentLength
+     *            expected input length or zero
+     * @return the response or the MD5 of the response
+     * @throws IOException
+     *             when reading the result fails
+     */
+    protected byte[] readResponse(SampleResult res, BufferedInputStream in,
+            long contentLength) throws IOException {
+        return testElement.readResponse(res, in, contentLength);
+    }
 
     /**
      * Follow redirects and download page resources if appropriate. this works,
      * but the container stuff here is what's doing it. followRedirects() is
      * actually doing the work to make sure we have only one container to make
      * this work more naturally, I think this method - sample() - needs to take
      * an HTTPSamplerResult container parameter instead of a
      * boolean:areFollowingRedirect.
      * <p>
      * Invokes
      * {@link HTTPSamplerBase#resultProcessing(boolean, int, HTTPSampleResult)}
      *
      * @param areFollowingRedirect
      *            flag whether we are getting a redirect target
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @param res
      *            sample result to process
      * @return the sample result
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect,
             int frameDepth, HTTPSampleResult res) {
         return testElement.resultProcessing(areFollowingRedirect, frameDepth, res);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#setUseKeepAlive(boolean)}
      *
      * @param b flag whether to use keep-alive for requests
      */
     protected void setUseKeepAlive(boolean b) {
         testElement.setUseKeepAlive(b);
     }
 
     /**
      * Called by testIterationStart if the SSL Context was reset.
      * 
      * This implementation does nothing.
      * @deprecated ** unused since r1489189. **
      */
     @Deprecated
     protected void notifySSLContextWasReset() {
         // NOOP
     }
     
     /**
      * Update HTTPSampleResult for a resource in cache
      * @param res {@link HTTPSampleResult}
      * @return HTTPSampleResult
      */
     protected HTTPSampleResult updateSampleResultForResourceInCache(HTTPSampleResult res) {
         switch (CACHED_RESOURCE_MODE) {
             case RETURN_NO_SAMPLE:
                 return null;
             case RETURN_200_CACHE:
                 res.sampleEnd();
                 res.setResponseCodeOK();
                 res.setResponseMessage(RETURN_200_CACHE_MESSAGE);
                 res.setSuccessful(true);
                 return res;
             case RETURN_CUSTOM_STATUS:
                 res.sampleEnd();
                 res.setResponseCode(RETURN_CUSTOM_STATUS_CODE);
                 res.setResponseMessage(RETURN_CUSTOM_STATUS_MESSAGE);
                 res.setSuccessful(true);
                 return res;
             default:
                 // Cannot happen
                 throw new IllegalStateException("Unknown CACHED_RESOURCE_MODE");
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPFileImpl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPFileImpl.java
index 4ad6f546e..dd2d596eb 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPFileImpl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPFileImpl.java
@@ -1,87 +1,101 @@
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
 
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.net.URLConnection;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
+import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * HTTP Sampler which can read from file: URLs
  */
 public class HTTPFileImpl extends HTTPAbstractImpl {
+    private static final int MAX_BYTES_TO_STORE_PER_REQUEST =
+            JMeterUtils.getPropDefault("httpsampler.max_bytes_to_store_per_request", 10 * 1024 *1024); // $NON-NLS-1$ // default value: 10MB
 
     protected HTTPFileImpl(HTTPSamplerBase base) {
         super(base);
     }
 
     @Override
     public boolean interrupt() {
         return false;
     }
 
     @Override
     protected HTTPSampleResult sample(URL url, String method,
             boolean areFollowingRedirect, int frameDepth) {
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setHTTPMethod(HTTPConstants.GET); // Dummy
         res.setURL(url);
         res.setSampleLabel(url.toString());
         InputStream is = null;
         res.sampleStart();
-        try {
+        int bufferSize = 4096;
+        try ( org.apache.commons.io.output.ByteArrayOutputStream bos = new org.apache.commons.io.output.ByteArrayOutputStream(bufferSize) ) {
             byte[] responseData;
             URLConnection conn = url.openConnection();
             is = conn.getInputStream();
-            responseData = IOUtils.toByteArray(is);
+            byte[] readBuffer = new byte[bufferSize];
+            int bytesReadInBuffer = 0;
+            long totalBytes = 0;
+            while ((bytesReadInBuffer = is.read(readBuffer)) > -1) {
+                if(totalBytes+bytesReadInBuffer<=MAX_BYTES_TO_STORE_PER_REQUEST) {
+                    bos.write(readBuffer, 0, bytesReadInBuffer);
+                }
+                totalBytes += bytesReadInBuffer;
+            }
+            responseData = bos.toByteArray();
             res.sampleEnd();
             res.setResponseData(responseData);
+            res.setBodySize(totalBytes);
             res.setResponseCodeOK();
             res.setResponseMessageOK();
             res.setSuccessful(true);
             StringBuilder ctb=new StringBuilder("text/html"); // $NON-NLS-1$
             // TODO can this be obtained from the file somehow?
             String contentEncoding = getContentEncoding();
             if (contentEncoding.length() > 0) {
                 ctb.append("; charset="); // $NON-NLS-1$
                 ctb.append(contentEncoding);
             }
             String ct = ctb.toString();
             res.setContentType(ct);
             res.setEncodingAndType(ct);
 
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
             return res;
         } catch (FileNotFoundException e) {
             return errorResult(e, res);
         } catch (IOException e) {
             return errorResult(e, res);
         } finally {
             IOUtils.closeQuietly(is);
         }
 
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
index 093bd3f5c..ee72e1bf5 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
@@ -1,1143 +1,1143 @@
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
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.InetAddress;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
 import org.apache.commons.httpclient.Header;
 import org.apache.commons.httpclient.HostConfiguration;
 import org.apache.commons.httpclient.HttpClient;
 import org.apache.commons.httpclient.HttpConnectionManager;
 import org.apache.commons.httpclient.HttpMethod;
 import org.apache.commons.httpclient.HttpMethodBase;
 import org.apache.commons.httpclient.HttpState;
 import org.apache.commons.httpclient.HttpVersion;
 import org.apache.commons.httpclient.NTCredentials;
 import org.apache.commons.httpclient.ProtocolException;
 import org.apache.commons.httpclient.SimpleHttpConnectionManager;
 import org.apache.commons.httpclient.auth.AuthScope;
 import org.apache.commons.httpclient.cookie.CookiePolicy;
 import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
 import org.apache.commons.httpclient.methods.FileRequestEntity;
 import org.apache.commons.httpclient.methods.GetMethod;
 import org.apache.commons.httpclient.methods.HeadMethod;
 import org.apache.commons.httpclient.methods.OptionsMethod;
 import org.apache.commons.httpclient.methods.PostMethod;
 import org.apache.commons.httpclient.methods.PutMethod;
 import org.apache.commons.httpclient.methods.StringRequestEntity;
 import org.apache.commons.httpclient.methods.TraceMethod;
 import org.apache.commons.httpclient.methods.multipart.FilePart;
 import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
 import org.apache.commons.httpclient.methods.multipart.Part;
 import org.apache.commons.httpclient.methods.multipart.PartBase;
 import org.apache.commons.httpclient.methods.multipart.StringPart;
 import org.apache.commons.httpclient.params.DefaultHttpParams;
 import org.apache.commons.httpclient.params.HttpClientParams;
 import org.apache.commons.httpclient.params.HttpMethodParams;
 import org.apache.commons.httpclient.params.HttpParams;
 import org.apache.commons.httpclient.protocol.Protocol;
 import org.apache.commons.io.input.CountingInputStream;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.LoopbackHttpClientSocketFactory;
 import org.apache.jmeter.protocol.http.util.SlowHttpClientSocketFactory;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.JsseSSLManager;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * HTTP sampler using Apache (Jakarta) Commons HttpClient 3.1.
  * @deprecated since 3.0, will be removed in next version
  */
 @Deprecated
 public class HTTPHC3Impl extends HTTPHCAbstractImpl {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** retry count to be used (default 1); 0 = disable retries */
     private static final int RETRY_COUNT = JMeterUtils.getPropDefault("httpclient3.retrycount", 0);
 
     private static final String HTTP_AUTHENTICATION_PREEMPTIVE = "http.authentication.preemptive"; // $NON-NLS-1$
 
     private static final boolean canSetPreEmptive; // OK to set pre-emptive auth?
 
     private static final ThreadLocal<Map<HostConfiguration, HttpClient>> httpClients = 
         new ThreadLocal<Map<HostConfiguration, HttpClient>>(){
         @Override
         protected Map<HostConfiguration, HttpClient> initialValue() {
             return new HashMap<>();
         }
     };
 
     // Needs to be accessible by HTTPSampler2
     volatile HttpClient savedClient;
 
     private volatile boolean resetSSLContext;
 
     static {
         log.info("HTTP request retry count = "+RETRY_COUNT);
         if (CPS_HTTP > 0) {
             log.info("Setting up HTTP SlowProtocol, cps="+CPS_HTTP);
             Protocol.registerProtocol(HTTPConstants.PROTOCOL_HTTP,
                     new Protocol(HTTPConstants.PROTOCOL_HTTP,new SlowHttpClientSocketFactory(CPS_HTTP),HTTPConstants.DEFAULT_HTTP_PORT));
         }
 
         // Now done in JsseSSLManager (which needs to register the protocol)
 //        cps =
 //            JMeterUtils.getPropDefault("httpclient.socket.https.cps", 0); // $NON-NLS-1$
 //
 //        if (cps > 0) {
 //            log.info("Setting up HTTPS SlowProtocol, cps="+cps);
 //            Protocol.registerProtocol(PROTOCOL_HTTPS,
 //                    new Protocol(PROTOCOL_HTTPS,new SlowHttpClientSocketFactory(cps),DEFAULT_HTTPS_PORT));
 //        }
 
         // Set default parameters as needed
         HttpParams params = DefaultHttpParams.getDefaultParams();
         params.setIntParameter("http.protocol.max-redirects", HTTPSamplerBase.MAX_REDIRECTS); //$NON-NLS-1$
         // Process Commons HttpClient parameters file
         String file=JMeterUtils.getProperty("httpclient.parameters.file"); // $NON-NLS-1$
         if (file != null) {
             HttpClientDefaultParameters.load(file, params);
         }
 
         // If the pre-emptive parameter is undefined, then we can set it as needed
         // otherwise we should do what the user requested.
         canSetPreEmptive =  params.getParameter(HTTP_AUTHENTICATION_PREEMPTIVE) == null;
 
         // Handle old-style JMeter properties
         try {
             params.setParameter(HttpMethodParams.PROTOCOL_VERSION, HttpVersion.parse("HTTP/"+HTTP_VERSION));
         } catch (ProtocolException e) {
             log.warn("Problem setting protocol version "+e.getLocalizedMessage());
         }
 
         if (SO_TIMEOUT >= 0){
             params.setIntParameter(HttpMethodParams.SO_TIMEOUT, SO_TIMEOUT);
         }
 
         // This must be done last, as must not be overridden
         params.setParameter(HttpMethodParams.COOKIE_POLICY,CookiePolicy.IGNORE_COOKIES);
         // We do our own cookie handling
 
         if (USE_LOOPBACK){
             LoopbackHttpClientSocketFactory.setup();
         }
     }
 
     protected HTTPHC3Impl(HTTPSamplerBase base) {
         super(base);
     }
 
 
     /**
      * Samples the URL passed in and stores the result in
      * <code>HTTPSampleResult</code>, following redirects and downloading
      * page resources as appropriate.
      * <p>
      * When getting a redirect target, redirects are not followed and resources
      * are not downloaded. The caller will take care of this.
      *
      * @param url
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling
      */
     @Override
     protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {
 
         String urlStr = url.toString();
 
         if (log.isDebugEnabled()) {
             log.debug("Start : sample " + urlStr);
             log.debug("method " + method+ " followingRedirect " + areFollowingRedirect + " depth " + frameDepth);            
         }
 
         HttpMethodBase httpMethod = null;
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(urlStr); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
 
         res.sampleStart(); // Count the retries as well in the time
         try {
             // May generate IllegalArgumentException
             if (method.equals(HTTPConstants.POST)) {
                 httpMethod = new PostMethod(urlStr);
             } else if (method.equals(HTTPConstants.GET)){
                 httpMethod = new GetMethod(urlStr);
             } else if (method.equals(HTTPConstants.PUT)){
                 httpMethod = new PutMethod(urlStr);
             } else if (method.equals(HTTPConstants.HEAD)){
                 httpMethod = new HeadMethod(urlStr);
             } else if (method.equals(HTTPConstants.TRACE)){
                 httpMethod = new TraceMethod(urlStr);
             } else if (method.equals(HTTPConstants.OPTIONS)){
                 httpMethod = new OptionsMethod(urlStr);
             } else if (method.equals(HTTPConstants.DELETE)){
                 httpMethod = new EntityEnclosingMethod(urlStr) {
                     @Override
                     public String getName() { // HC3.1 does not have the method
                         return HTTPConstants.DELETE;
                     }
                 };
             } else if (method.equals(HTTPConstants.PATCH)){
                 httpMethod = new EntityEnclosingMethod(urlStr) {
                     @Override
                     public String getName() { // HC3.1 does not have the method
                         return HTTPConstants.PATCH;
                     }
                 };
             } else {
                 throw new IllegalArgumentException("Unexpected method: '"+method+"'");
             }
 
             final CacheManager cacheManager = getCacheManager();
             if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
                if (cacheManager.inCache(url)) {
                    return updateSampleResultForResourceInCache(res);
                }
             }
 
             // Set any default request headers
             setDefaultRequestHeaders(httpMethod);
 
             // Setup connection
             HttpClient client = setupConnection(url, httpMethod, res);
             savedClient = client;
 
             // Handle the various methods
             if (method.equals(HTTPConstants.POST)) {
                 String postBody = sendPostData((PostMethod)httpMethod);
                 res.setQueryString(postBody);
             } else if (method.equals(HTTPConstants.PUT) || method.equals(HTTPConstants.PATCH) 
                     || method.equals(HTTPConstants.DELETE)) {
                 String putBody = sendEntityData((EntityEnclosingMethod) httpMethod);
                 res.setQueryString(putBody);
             }
 
             int statusCode = client.executeMethod(httpMethod);
 
             // We've finished with the request, so we can add the LocalAddress to it for display
             final InetAddress localAddr = client.getHostConfiguration().getLocalAddress();
             if (localAddr != null) {
                 httpMethod.addRequestHeader(HEADER_LOCAL_ADDRESS, localAddr.toString());
             }
             // Needs to be done after execute to pick up all the headers
             res.setRequestHeaders(getConnectionHeaders(httpMethod));
 
             // Request sent. Now get the response:
             InputStream instream = httpMethod.getResponseBodyAsStream();
 
             if (instream != null) {// will be null for HEAD
                 instream = new CountingInputStream(instream);
                 try {
                     Header responseHeader = httpMethod.getResponseHeader(HTTPConstants.HEADER_CONTENT_ENCODING);
                     if (responseHeader!= null && HTTPConstants.ENCODING_GZIP.equals(responseHeader.getValue())) {
                         InputStream tmpInput = new GZIPInputStream(instream); // tmp inputstream needs to have a good counting
-                        res.setResponseData(readResponse(res, tmpInput, (int) httpMethod.getResponseContentLength()));                        
+                        res.setResponseData(readResponse(res, tmpInput, httpMethod.getResponseContentLength()));                        
                     } else {
-                        res.setResponseData(readResponse(res, instream, (int) httpMethod.getResponseContentLength()));
+                        res.setResponseData(readResponse(res, instream, httpMethod.getResponseContentLength()));
                     }
                 } finally {
                     JOrphanUtils.closeQuietly(instream);
                 }
             }
 
             res.sampleEnd();
             // Done with the sampling proper.
 
             // Now collect the results into the HTTPSampleResult:
 
             res.setSampleLabel(httpMethod.getURI().toString());
             // Pick up Actual path (after redirects)
 
             res.setResponseCode(Integer.toString(statusCode));
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseMessage(httpMethod.getStatusText());
 
             String ct = null;
             Header h = httpMethod.getResponseHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             if (h != null)// Can be missing, e.g. on redirect
             {
                 ct = h.getValue();
                 res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                 res.setEncodingAndType(ct);
             }
 
             res.setResponseHeaders(getResponseHeaders(httpMethod));
             if (res.isRedirect()) {
                 final Header headerLocation = httpMethod.getResponseHeader(HTTPConstants.HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header");
                 }
                 String redirectLocation = headerLocation.getValue();
                 res.setRedirectLocation(redirectLocation); // in case sanitising fails
             }
 
             // record some sizes to allow HTTPSampleResult.getBytes() with different options
             if (instream != null) {
-                res.setBodySize(((CountingInputStream) instream).getCount());
+                res.setBodySize(((CountingInputStream) instream).getByteCount());
             }
             res.setHeadersSize(calculateHeadersSize(httpMethod));
             if (log.isDebugEnabled()) {
-                log.debug("Response headersSize=" + res.getHeadersSize() + " bodySize=" + res.getBodySize()
-                        + " Total=" + (res.getHeadersSize() + res.getBodySize()));
+                log.debug("Response headersSize=" + res.getHeadersSize() + " bodySize=" + res.getBodySizeAsLong()
+                        + " Total=" + (res.getHeadersSize() + res.getBodySizeAsLong()));
             }
             
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 res.setURL(new URL(httpMethod.getURI().toString()));
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(httpMethod, res.getURL(), getCookieManager());
 
             // Save cache information
             if (cacheManager != null){
                 cacheManager.saveDetails(httpMethod, res);
             }
 
             // Follow redirects and download page resources if appropriate:
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
             log.debug("End : sample");
             return res;
         } catch (IllegalArgumentException // e.g. some kinds of invalid URL
                 | IOException e) { 
             res.sampleEnd();
             // pick up headers if failed to execute the request
             // httpMethod can be null if method is unexpected
             if(httpMethod != null) {
                 res.setRequestHeaders(getConnectionHeaders(httpMethod));
             }
             errorResult(e, res);
             return res;
         } finally {
             savedClient = null;
             if (httpMethod != null) {
                 httpMethod.releaseConnection();
             }
         }
     }
     
     /**
      * Calculate response headers size
      * 
      * @return the size response headers (in bytes)
      */
     private static int calculateHeadersSize(HttpMethodBase httpMethod) {
         int headerSize = httpMethod.getStatusLine().toString().length()+2; // add a \r\n
         Header[] rh = httpMethod.getResponseHeaders();
         for (Header responseHeader : rh) {
             headerSize += responseHeader.toString().length(); // already include the \r\n
         }
         headerSize += 2; // last \r\n before response data
         return headerSize;
     }
 
     /**
      * Returns an <code>HttpConnection</code> fully ready to attempt
      * connection. This means it sets the request method (GET or POST), headers,
      * cookies, and authorization for the URL request.
      * <p>
      * The request infos are saved into the sample result if one is provided.
      *
      * @param u
      *            <code>URL</code> of the URL request
      * @param httpMethod
      *            GET/PUT/HEAD etc
      * @param res
      *            sample result to save request infos to
      * @return <code>HttpConnection</code> ready for .connect
      * @exception IOException
      *                if an I/O Exception occurs
      */
     protected HttpClient setupConnection(URL u, HttpMethodBase httpMethod, HTTPSampleResult res) throws IOException {
 
         String urlStr = u.toString();
 
         org.apache.commons.httpclient.URI uri = new org.apache.commons.httpclient.URI(urlStr,false);
 
         String schema = uri.getScheme();
         if ((schema == null) || (schema.length()==0)) {
             schema = HTTPConstants.PROTOCOL_HTTP;
         }
 
         final boolean isHTTPS = HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(schema);
         if (isHTTPS){
             SSLManager.getInstance(); // ensure the manager is initialised
             // we don't currently need to do anything further, as this sets the default https protocol
         }
 
         Protocol protocol = Protocol.getProtocol(schema);
 
         String host = uri.getHost();
         int port = uri.getPort();
 
         /*
          *  We use the HostConfiguration as the key to retrieve the HttpClient,
          *  so need to ensure that any items used in its equals/hashcode methods are
          *  not changed after use, i.e.:
          *  host, port, protocol, localAddress, proxy
          *
         */
         HostConfiguration hc = new HostConfiguration();
         hc.setHost(host, port, protocol); // All needed to ensure re-usablility
 
         // Set up the local address if one exists
         final InetAddress inetAddr = getIpSourceAddress();
         if (inetAddr != null) {// Use special field ip source address (for pseudo 'ip spoofing')
             hc.setLocalAddress(inetAddr);
         } else {
             hc.setLocalAddress(localAddress); // null means use the default
         }
 
         final String proxyHost = getProxyHost();
         final int proxyPort = getProxyPortInt();
 
         boolean useStaticProxy = isStaticProxy(host);
         boolean useDynamicProxy = isDynamicProxy(proxyHost, proxyPort);
 
         if (useDynamicProxy){
             hc.setProxy(proxyHost, proxyPort);
             useStaticProxy = false; // Dynamic proxy overrules static proxy
         } else if (useStaticProxy) {
             if (log.isDebugEnabled()){
                 log.debug("Setting proxy: "+PROXY_HOST+":"+PROXY_PORT);
             }
             hc.setProxy(PROXY_HOST, PROXY_PORT);
         }
 
         Map<HostConfiguration, HttpClient> map = httpClients.get();
         // N.B. HostConfiguration.equals() includes proxy settings in the compare.
         HttpClient httpClient = map.get(hc);
 
         if (httpClient != null && resetSSLContext && isHTTPS) {
             httpClient.getHttpConnectionManager().closeIdleConnections(-1000);
             httpClient = null;
             JsseSSLManager sslMgr = (JsseSSLManager) SSLManager.getInstance();
             sslMgr.resetContext();
             resetSSLContext = false;
         }
 
         if ( httpClient == null )
         {
             httpClient = new HttpClient(new SimpleHttpConnectionManager());
             httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
                     new DefaultHttpMethodRetryHandler(RETRY_COUNT, false));
             if (log.isDebugEnabled()) {
                 log.debug("Created new HttpClient: @"+System.identityHashCode(httpClient));
             }
             httpClient.setHostConfiguration(hc);
             map.put(hc, httpClient);
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Reusing the HttpClient: @"+System.identityHashCode(httpClient));
             }
         }
 
         // Set up any required Proxy credentials
         if (useDynamicProxy){
             String user = getProxyUser();
             if (user.length() > 0){
                 httpClient.getState().setProxyCredentials(
                         new AuthScope(proxyHost,proxyPort,null,AuthScope.ANY_SCHEME),
                         new NTCredentials(user,getProxyPass(),localHost,PROXY_DOMAIN)
                     );
             } else {
                 httpClient.getState().clearProxyCredentials();
             }
         } else {
             if (useStaticProxy) {
                 if (PROXY_USER.length() > 0){
                     httpClient.getState().setProxyCredentials(
                         new AuthScope(PROXY_HOST,PROXY_PORT,null,AuthScope.ANY_SCHEME),
                         new NTCredentials(PROXY_USER,PROXY_PASS,localHost,PROXY_DOMAIN)
                     );
                 }
             } else {
                 httpClient.getState().clearProxyCredentials();
             }
         }
 
         int rto = getResponseTimeout();
         if (rto > 0){
             httpMethod.getParams().setSoTimeout(rto);
         }
 
         int cto = getConnectTimeout();
         if (cto > 0){
             httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(cto);
         }
 
 
         // Allow HttpClient to handle the redirects:
         httpMethod.setFollowRedirects(getAutoRedirects());
 
         // a well-behaved browser is supposed to send 'Connection: close'
         // with the last request to an HTTP server. Instead, most browsers
         // leave it to the server to close the connection after their
         // timeout period. Leave it to the JMeter user to decide.
         if (getUseKeepAlive()) {
             httpMethod.setRequestHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.KEEP_ALIVE);
         } else {
             httpMethod.setRequestHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.CONNECTION_CLOSE);
         }
 
         setConnectionHeaders(httpMethod, u, getHeaderManager(), getCacheManager());
         String cookies = setConnectionCookie(httpMethod, u, getCookieManager());
 
         setConnectionAuthorization(httpClient, u, getAuthManager());
 
         if (res != null) {
             res.setCookies(cookies);
         }
 
         return httpClient;
     }
 
     /**
      * Set any default request headers to include
      *
      * @param httpMethod the HttpMethod used for the request
      */
     protected void setDefaultRequestHeaders(HttpMethod httpMethod) {
         // Method left empty here, but allows subclasses to override
     }
 
     /**
      * Gets the ResponseHeaders
      *
      * @param method the method used to perform the request
      * @return string containing the headers, one per line
      */
     protected String getResponseHeaders(HttpMethod method) {
         StringBuilder headerBuf = new StringBuilder();
         org.apache.commons.httpclient.Header[] rh = method.getResponseHeaders();
         headerBuf.append(method.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (Header responseHeader : rh) {
             String key = responseHeader.getName();
             headerBuf.append(key);
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(responseHeader.getValue());
             headerBuf.append("\n"); // $NON-NLS-1$
         }
         return headerBuf.toString();
     }
 
     /**
      * Extracts all the required cookies for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in.
      *
      * @param method <code>HttpMethod</code> for the request
      * @param u <code>URL</code> of the request
      * @param cookieManager the <code>CookieManager</code> containing all the cookies
      * @return a String containing the cookie details (for the response)
      * May be null
      */
     private String setConnectionCookie(HttpMethod method, URL u, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(u);
             if (cookieHeader != null) {
                 method.setRequestHeader(HTTPConstants.HEADER_COOKIE, cookieHeader);
             }
         }
         return cookieHeader;
     }
 
     /**
      * Extracts all the required non-cookie headers for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @param u
      *            <code>URL</code> of the URL request
      * @param headerManager
      *            the <code>HeaderManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      * @param cacheManager the CacheManager (may be null)
      */
     private void setConnectionHeaders(HttpMethod method, URL u, HeaderManager headerManager, CacheManager cacheManager) {
         // Set all the headers from the HeaderManager
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 for (JMeterProperty jMeterProperty : headers) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                             jMeterProperty.getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // This helps with SoapSampler hack too
                     // TODO - what other headers are not allowed?
                     if (! HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         if (HTTPConstants.HEADER_HOST.equalsIgnoreCase(n)) {
                             v = v.replaceFirst(":\\d+$",""); // remove any port specification // $NON-NLS-1$ $NON-NLS-2$
                             method.getParams().setVirtualHost(v);
                         } else {
                             method.addRequestHeader(n, v);
                         }
                     }
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(u, method);
         }
     }
 
     /**
      * Get all the request headers for the <code>HttpMethod</code>
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @return the headers as a string
      */
     protected String getConnectionHeaders(HttpMethod method) {
         // Get all the request headers
         StringBuilder hdrs = new StringBuilder(100);
         Header[] requestHeaders = method.getRequestHeaders();
         for (Header requestHeader : requestHeaders) {
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if (!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(requestHeader.getName())) {
                 hdrs.append(requestHeader.getName());
                 hdrs.append(": "); // $NON-NLS-1$
                 hdrs.append(requestHeader.getValue());
                 hdrs.append("\n"); // $NON-NLS-1$
             }
         }
 
         return hdrs.toString();
     }
 
 
     /**
      * Extracts all the required authorization for that particular URL request
      * and sets it in the <code>HttpMethod</code> passed in.
      *
      * @param client the HttpClient object
      *
      * @param u
      *            <code>URL</code> of the URL request
      * @param authManager
      *            the <code>AuthManager</code> containing all the authorisations for
      *            this <code>UrlConfig</code>
      */
     private void setConnectionAuthorization(HttpClient client, URL u, AuthManager authManager) {
         HttpState state = client.getState();
         if (authManager != null) {
             HttpClientParams params = client.getParams();
             Authorization auth = authManager.getAuthForURL(u);
             if (auth != null) {
                     String username = auth.getUser();
                     String realm = auth.getRealm();
                     String domain = auth.getDomain();
                     if (log.isDebugEnabled()){
                         log.debug(username + " >  D="+ username + " D="+domain+" R="+realm);
                     }
                     state.setCredentials(
                             new AuthScope(u.getHost(),u.getPort(),
                                     realm.length()==0 ? null : realm //"" is not the same as no realm
                                     ,AuthScope.ANY_SCHEME),
                             // NT Includes other types of Credentials
                             new NTCredentials(
                                     username,
                                     auth.getPass(),
                                     localHost,
                                     domain
                             ));
                     // We have credentials - should we set pre-emptive authentication?
                     if (canSetPreEmptive){
                         log.debug("Setting Pre-emptive authentication");
                         params.setAuthenticationPreemptive(true);
                     }
             } else {
                 state.clearCredentials();
                 if (canSetPreEmptive){
                     params.setAuthenticationPreemptive(false);
                 }
             }
         } else {
             state.clearCredentials();
         }
     }
 
 
     /*
      * Send POST data from <code>Entry</code> to the open connection.
      *
      * @param connection
      *            <code>URLConnection</code> where POST data should be sent
      * @return a String show what was posted. Will not contain actual file upload content
      * @exception IOException
      *                if an I/O exception occurs
      */
     private String sendPostData(PostMethod post) throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         HTTPFileArg[] files = getHTTPFiles();
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(getUseMultipartForPost()) {
             // If a content encoding is specified, we use that as the
             // encoding of any parameter values
             String contentEncoding = getContentEncoding();
             if(isNullOrEmptyTrimmed(contentEncoding)) {
                 contentEncoding = null;
             }
 
             final boolean browserCompatible = getDoBrowserCompatibleMultipart();
             // We don't know how many entries will be skipped
             List<PartBase> partlist = new ArrayList<>();
             // Create the parts
             // Add any parameters
             for (JMeterProperty jMeterProperty : getArguments()) {
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 String parameterName = arg.getName();
                 if (arg.isSkippable(parameterName)) {
                     continue;
                 }
                 StringPart part = new StringPart(arg.getName(), arg.getValue(), contentEncoding);
                 if (browserCompatible) {
                     part.setTransferEncoding(null);
                     part.setContentType(null);
                 }
                 partlist.add(part);
             }
 
             // Add any files
             for (HTTPFileArg file : files) {
                 File inputFile = FileServer.getFileServer().getResolvedFile(file.getPath());
                 // We do not know the char set of the file to be uploaded, so we set it to null
                 ViewableFilePart filePart = new ViewableFilePart(file.getParamName(), inputFile, file.getMimeType(), null);
                 filePart.setCharSet(null); // We do not know what the char set of the file is
                 partlist.add(filePart);
             }
 
             // Set the multipart for the post
             int partNo = partlist.size();
             Part[] parts = partlist.toArray(new Part[partNo]);
             MultipartRequestEntity multiPart = new MultipartRequestEntity(parts, post.getParams());
             post.setRequestEntity(multiPart);
 
             // Set the content type
             String multiPartContentType = multiPart.getContentType();
             post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, multiPartContentType);
 
             // If the Multipart is repeatable, we can send it first to
             // our own stream, without the actual file content, so we can return it
             if(multiPart.isRepeatable()) {
                 // For all the file multiparts, we must tell it to not include
                 // the actual file content
                 for(int i = 0; i < partNo; i++) {
                     if(parts[i] instanceof ViewableFilePart) {
                         ((ViewableFilePart) parts[i]).setHideFileData(true); // .sendMultipartWithoutFileContent(bos);
                     }
                 }
                 // Write the request to our own stream
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 multiPart.writeRequest(bos);
                 bos.flush();
                 // We get the posted bytes using the encoding used to create it
                 postedBody.append(new String(bos.toByteArray(),
                         contentEncoding == null ? "US-ASCII" // $NON-NLS-1$ this is the default used by HttpClient
                         : contentEncoding));
                 bos.close();
 
                 // For all the file multiparts, we must revert the hiding of
                 // the actual file content
                 for(int i = 0; i < partNo; i++) {
                     if(parts[i] instanceof ViewableFilePart) {
                         ((ViewableFilePart) parts[i]).setHideFileData(false);
                     }
                 }
             }
             else {
                 postedBody.append("<Multipart was not repeatable, cannot view what was sent>"); // $NON-NLS-1$
             }
         }
         else {
             // Check if the header manager had a content type header
             // This allows the user to specify his own content-type for a POST request
             Header contentTypeHeader = post.getRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
             // If there are no arguments, we can send a file as the body of the request
             // TODO: needs a multiple file upload scenerio
             if(!hasArguments() && getSendFileAsPostBody()) {
                 // If getSendFileAsPostBody returned true, it's sure that file is not null
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
 
                 FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(file.getPath()),null);
                 post.setRequestEntity(fileRequestEntity);
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>");
             }
             else {
                 // In a post request which is not multipart, we only support
                 // parameters, no file upload is allowed
 
                 // If a content encoding is specified, we set it as http parameter, so that
                 // the post body will be encoded in the specified content encoding
                 String contentEncoding = getContentEncoding();
                 boolean haveContentEncoding = false;
                 if(isNullOrEmptyTrimmed(contentEncoding)) {
                     contentEncoding=null;                    
                 } else {
                     post.getParams().setContentCharset(contentEncoding);
                     haveContentEncoding = true;                    
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
                             post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                              // TODO - is this the correct default?
                             post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBody = new StringBuilder();
                     for (JMeterProperty jMeterProperty : getArguments()) {
                         HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                         String value;
                         if (haveContentEncoding) {
                             value = arg.getEncodedValue(contentEncoding);
                         } else {
                             value = arg.getEncodedValue();
                         }
                         postBody.append(value);
                     }
                     StringRequestEntity requestEntity = new StringRequestEntity(postBody.toString(), post.getRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE).getValue(), contentEncoding);
                     post.setRequestEntity(requestEntity);
                 }
                 else {
                     // It is a normal post request, with parameter names and values
 
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                     // Add the parameters
                     for (JMeterProperty jMeterProperty : getArguments()) {
                         HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                         // The HTTPClient always urlencodes both name and value,
                         // so if the argument is already encoded, we have to decode
                         // it before adding it to the post request
                         String parameterName = arg.getName();
                         if (arg.isSkippable(parameterName)) {
                             continue;
                         }
                         String parameterValue = arg.getValue();
                         if (!arg.isAlwaysEncoded()) {
                             // The value is already encoded by the user
                             // Must decode the value now, so that when the
                             // httpclient encodes it, we end up with the same value
                             // as the user had entered.
                             String urlContentEncoding = contentEncoding;
                             if (urlContentEncoding == null || urlContentEncoding.length() == 0) {
                                 // Use the default encoding for urls
                                 urlContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
                             }
                             parameterName = URLDecoder.decode(parameterName, urlContentEncoding);
                             parameterValue = URLDecoder.decode(parameterValue, urlContentEncoding);
                         }
                         // Add the parameter, httpclient will urlencode it
                         post.addParameter(parameterName, parameterValue);
                     }
 
 /*
 //                    // Alternative implementation, to make sure that HTTPSampler and HTTPSampler2
 //                    // sends the same post body.
 //
 //                    // Only include the content char set in the content-type header if it is not
 //                    // an APPLICATION_X_WWW_FORM_URLENCODED content type
 //                    String contentCharSet = null;
 //                    if(!post.getRequestHeader(HEADER_CONTENT_TYPE).getValue().equals(APPLICATION_X_WWW_FORM_URLENCODED)) {
 //                        contentCharSet = post.getRequestCharSet();
 //                    }
 //                    StringRequestEntity requestEntity = new StringRequestEntity(getQueryString(contentEncoding), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), contentCharSet);
 //                    post.setRequestEntity(requestEntity);
 */
                 }
 
                 // If the request entity is repeatable, we can send it first to
                 // our own stream, so we can return it
                 if(post.getRequestEntity().isRepeatable()) {
                     ByteArrayOutputStream bos = new ByteArrayOutputStream();
                     post.getRequestEntity().writeRequest(bos);
                     bos.flush();
                     // We get the posted bytes using the encoding used to create it
                     postedBody.append(new String(bos.toByteArray(),post.getRequestCharSet()));
                     bos.close();
                 }
                 else {
                     postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                 }
             }
         }
         // Set the content length
         post.setRequestHeader(HTTPConstants.HEADER_CONTENT_LENGTH, Long.toString(post.getRequestEntity().getContentLength()));
 
         return postedBody.toString();
     }
 
     /**
      * Set up the PUT/PATCH/DELETE data
      */
     private String sendEntityData(EntityEnclosingMethod put) throws IOException {
         // Buffer to hold the put body, except file content
         StringBuilder putBody = new StringBuilder(1000);
         boolean hasPutBody = false;
 
         // Check if the header manager had a content type header
         // This allows the user to specify his own content-type for a POST request
         Header contentTypeHeader = put.getRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE);
         boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
         HTTPFileArg[] files = getHTTPFiles();
 
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasPutBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             File reservedFile = FileServer.getFileServer().getResolvedFile(files[0].getPath());
             FileRequestEntity fileRequestEntity = new FileRequestEntity(reservedFile,null);
             put.setRequestEntity(fileRequestEntity);
         }
         // If none of the arguments have a name specified, we
         // just send all the values as the put body
         else if(getSendParameterValuesAsPostBody()) {
             hasPutBody = true;
 
             // If a content encoding is specified, we set it as http parameter, so that
             // the post body will be encoded in the specified content encoding
             String contentEncoding = getContentEncoding();
             boolean haveContentEncoding = false;
             if(isNullOrEmptyTrimmed(contentEncoding)) {
                 contentEncoding = null;
             } else {
                 put.getParams().setContentCharset(contentEncoding);
                 haveContentEncoding = true;
             }
 
             // Just append all the parameter values, and use that as the post body
             StringBuilder putBodyContent = new StringBuilder();
             for (JMeterProperty jMeterProperty : getArguments()) {
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 String value = null;
                 if (haveContentEncoding) {
                     value = arg.getEncodedValue(contentEncoding);
                 } else {
                     value = arg.getEncodedValue();
                 }
                 putBodyContent.append(value);
             }
             String contentTypeValue = null;
             if(hasContentTypeHeader) {
                 contentTypeValue = put.getRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE).getValue();
             }
             StringRequestEntity requestEntity = new StringRequestEntity(putBodyContent.toString(), contentTypeValue, put.getRequestCharSet());
             put.setRequestEntity(requestEntity);
         }
         // Check if we have any content to send for body
         if(hasPutBody) {
             // If the request entity is repeatable, we can send it first to
             // our own stream, so we can return it
             if(put.getRequestEntity().isRepeatable()) {
                 putBody.append("<actual file content, not shown here>");
             }
             else {
                 putBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
             }
             if(!hasContentTypeHeader) {
                 // Allow the mimetype of the file to control the content type
                 // This is not obvious in GUI if you are not uploading any files,
                 // but just sending the content of nameless parameters
                 // TODO: needs a multiple file upload scenerio
                 HTTPFileArg file = files.length > 0? files[0] : null;
                 if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                     put.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                 }
             }
             // Set the content length
             put.setRequestHeader(HTTPConstants.HEADER_CONTENT_LENGTH, Long.toString(put.getRequestEntity().getContentLength()));
         }
         return putBody.toString();
     }
 
     /**
      * Class extending FilePart, so that we can send placeholder text
      * instead of the actual file content
      */
     private static class ViewableFilePart extends FilePart {
         private boolean hideFileData;
 
         public ViewableFilePart(String name, File file, String contentType, String charset) throws FileNotFoundException {
             super(name, file, contentType, charset);
             this.hideFileData = false;
         }
 
         public void setHideFileData(boolean hideFileData) {
             this.hideFileData = hideFileData;
         }
 
         @Override
         protected void sendData(OutputStream out) throws IOException {
             // Check if we should send only placeholder text for the
             // file content, or the real file content
             if(hideFileData) {
                 out.write("<actual file content, not shown here>".getBytes());// encoding does not really matter here
             }
             else {
                 super.sendData(out);
             }
         }
     }
 
     /**
      * From the <code>HttpMethod</code>, store all the "set-cookie" key-pair
      * values in the cookieManager of the <code>UrlConfig</code>.
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @param u
      *            <code>URL</code> of the URL request
      * @param cookieManager
      *            the <code>CookieManager</code> containing all the cookies
      */
     protected void saveConnectionCookies(HttpMethod method, URL u, CookieManager cookieManager) {
         if (cookieManager != null) {
             Header[] hdr = method.getResponseHeaders(HTTPConstants.HEADER_SET_COOKIE);
             for (Header responseHeader : hdr) {
                 cookieManager.addCookieFromHeader(responseHeader.getValue(), u);
             }
         }
     }
 
 
     @Override
     protected void threadFinished() {
         log.debug("Thread Finished");
 
         closeThreadLocalConnections();
     }
 
     @Override
     protected void notifyFirstSampleAfterLoopRestart() {
         log.debug("notifyFirstSampleAfterLoopRestart");
         resetSSLContext = !USE_CACHED_SSL_CONTEXT;
     }
 
     /**
      * 
      */
     private void closeThreadLocalConnections() {
         // Does not need to be synchronised, as all access is from same thread
         Map<HostConfiguration, HttpClient> map = httpClients.get();
 
         if ( map != null ) {
             for (HttpClient cl : map.values())
             {
                 // Can cause NPE in HttpClient 3.1
                 //((SimpleHttpConnectionManager)cl.getHttpConnectionManager()).shutdown();// Closes the connection
                 // Revert to original method:
                 cl.getHttpConnectionManager().closeIdleConnections(-1000);// Closes the connection
             }
             map.clear();
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean interrupt() {
         HttpClient client = savedClient;
         if (client != null) {
             savedClient = null;
             // TODO - not sure this is the best method
             final HttpConnectionManager httpConnectionManager = client.getHttpConnectionManager();
             if (httpConnectionManager instanceof SimpleHttpConnectionManager) {// Should be true
                 ((SimpleHttpConnectionManager)httpConnectionManager).shutdown();
             }
         }
         return client != null;
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
index af8966191..a5f39b72f 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
@@ -1,1445 +1,1445 @@
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
-                res.setResponseData(readResponse(res, entity.getContent(), (int) entity.getContentLength()));
+                res.setResponseData(readResponse(res, entity.getContent(), entity.getContentLength()));
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
-            res.setHeadersSize((int) headerBytes);
-            res.setBodySize((int)(totalBytes - headerBytes));
+            res.setHeadersSize((int)headerBytes);
+            res.setBodySize(totalBytes - headerBytes);
             res.setSentBytes(metrics.getSentBytesCount());
             if (log.isDebugEnabled()) {
-                log.debug("ResponseHeadersSize=" + res.getHeadersSize() + " Content-Length=" + res.getBodySize()
-                        + " Total=" + (res.getHeadersSize() + res.getBodySize()));
+                log.debug("ResponseHeadersSize=" + res.getHeadersSize() + " Content-Length=" + res.getBodySizeAsLong()
+                        + " Total=" + (res.getHeadersSize() + res.getBodySizeAsLong()));
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
         // Check if we have any content to send for body
         if(hasEntityBody) {
             // If the request entity is repeatable, we can send it first to
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java
index f573d6064..40f3e8d21 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java
@@ -1,652 +1,652 @@
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
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.BufferedInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.BindException;
 import java.net.HttpURLConnection;
 import java.net.InetSocketAddress;
 import java.net.Proxy;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.List;
 import java.util.Map;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.commons.io.input.CountingInputStream;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * A sampler which understands all the parts necessary to read statistics about
  * HTTP requests, including cookies and authentication.
  *
  */
 public class HTTPJavaImpl extends HTTPAbstractImpl {
     private static final boolean OBEY_CONTENT_LENGTH =
         JMeterUtils.getPropDefault("httpsampler.obey_contentlength", false); // $NON-NLS-1$
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final int MAX_CONN_RETRIES =
         JMeterUtils.getPropDefault("http.java.sampler.retries" // $NON-NLS-1$
                 ,0); // Maximum connection retries
 
     static {
         log.info("Maximum connection retries = "+MAX_CONN_RETRIES); // $NON-NLS-1$
         // Temporary copies, so can set the final ones
     }
 
     private static final byte[] NULL_BA = new byte[0];// can share these
 
     /** Handles writing of a post or put request */
     private transient PostWriter postOrPutWriter;
 
     private volatile HttpURLConnection savedConn;
 
     protected HTTPJavaImpl(HTTPSamplerBase base) {
         super(base);
     }
 
     /**
      * Set request headers in preparation to opening a connection.
      *
      * @param conn
      *            <code>URLConnection</code> to set headers on
      * @exception IOException
      *                if an I/O exception occurs
      */
     protected void setPostHeaders(URLConnection conn) throws IOException {
         postOrPutWriter = new PostWriter();
         postOrPutWriter.setHeaders(conn, testElement);
     }
 
     private void setPutHeaders(URLConnection conn) throws IOException {
         postOrPutWriter = new PutWriter();
         postOrPutWriter.setHeaders(conn, testElement);
     }
 
     /**
      * Send POST data from <code>Entry</code> to the open connection.
      * This also handles sending data for PUT requests
      *
      * @param connection
      *            <code>URLConnection</code> where POST data should be sent
      * @return a String show what was posted. Will not contain actual file upload content
      * @exception IOException
      *                if an I/O exception occurs
      */
     protected String sendPostData(URLConnection connection) throws IOException {
         return postOrPutWriter.sendPostData(connection, testElement);
     }
 
     private String sendPutData(URLConnection connection) throws IOException {
         return postOrPutWriter.sendPostData(connection, testElement);
     }
 
     /**
      * Returns an <code>HttpURLConnection</code> fully ready to attempt
      * connection. This means it sets the request method (GET or POST), headers,
      * cookies, and authorization for the URL request.
      * <p>
      * The request infos are saved into the sample result if one is provided.
      *
      * @param u
      *            <code>URL</code> of the URL request
      * @param method
      *            GET, POST etc
      * @param res
      *            sample result to save request infos to
      * @return <code>HttpURLConnection</code> ready for .connect
      * @exception IOException
      *                if an I/O Exception occurs
      */
     protected HttpURLConnection setupConnection(URL u, String method, HTTPSampleResult res) throws IOException {
         SSLManager sslmgr = null;
         if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(u.getProtocol())) {
             try {
                 sslmgr=SSLManager.getInstance(); // N.B. this needs to be done before opening the connection
             } catch (Exception e) {
                 log.warn("Problem creating the SSLManager: ", e);
             }
         }
 
         final HttpURLConnection conn;
         final String proxyHost = getProxyHost();
         final int proxyPort = getProxyPortInt();
         if (proxyHost.length() > 0 && proxyPort > 0){
             Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
             //TODO - how to define proxy authentication for a single connection?
             // It's not clear if this is possible
 //            String user = getProxyUser();
 //            if (user.length() > 0){
 //                Authenticator auth = new ProxyAuthenticator(user, getProxyPass());
 //            }
             conn = (HttpURLConnection) u.openConnection(proxy);
         } else {
             conn = (HttpURLConnection) u.openConnection();
         }
 
         // Update follow redirects setting just for this connection
         conn.setInstanceFollowRedirects(getAutoRedirects());
 
         int cto = getConnectTimeout();
         if (cto > 0){
             conn.setConnectTimeout(cto);
         }
 
         int rto = getResponseTimeout();
         if (rto > 0){
             conn.setReadTimeout(rto);
         }
 
         if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(u.getProtocol())) {
             try {
                 if (null != sslmgr){
                     sslmgr.setContext(conn); // N.B. must be done after opening connection
                 }
             } catch (Exception e) {
                 log.warn("Problem setting the SSLManager for the connection: ", e);
             }
         }
 
         // a well-behaved browser is supposed to send 'Connection: close'
         // with the last request to an HTTP server. Instead, most browsers
         // leave it to the server to close the connection after their
         // timeout period. Leave it to the JMeter user to decide.
         if (getUseKeepAlive()) {
             conn.setRequestProperty(HTTPConstants.HEADER_CONNECTION, HTTPConstants.KEEP_ALIVE);
         } else {
             conn.setRequestProperty(HTTPConstants.HEADER_CONNECTION, HTTPConstants.CONNECTION_CLOSE);
         }
 
         conn.setRequestMethod(method);
         setConnectionHeaders(conn, u, getHeaderManager(), getCacheManager());
         String cookies = setConnectionCookie(conn, u, getCookieManager());
 
         setConnectionAuthorization(conn, u, getAuthManager());
 
         if (method.equals(HTTPConstants.POST)) {
             setPostHeaders(conn);
         } else if (method.equals(HTTPConstants.PUT)) {
             setPutHeaders(conn);
         }
 
         if (res != null) {
             res.setRequestHeaders(getConnectionHeaders(conn));
             res.setCookies(cookies);
         }
 
         return conn;
     }
 
     /**
      * Reads the response from the URL connection.
      *
      * @param conn
      *            URL from which to read response
      * @param res
      *            {@link SampleResult} to read response into
      * @return response content
      * @exception IOException
      *                if an I/O exception occurs
      */
     protected byte[] readResponse(HttpURLConnection conn, SampleResult res) throws IOException {
         BufferedInputStream in;
 
-        final int contentLength = conn.getContentLength();
+        final long contentLength = conn.getContentLength();
         if ((contentLength == 0)
             && OBEY_CONTENT_LENGTH) {
             log.info("Content-Length: 0, not reading http-body");
             res.setResponseHeaders(getResponseHeaders(conn));
             res.latencyEnd();
             return NULL_BA;
         }
 
         // works OK even if ContentEncoding is null
         boolean gzipped = HTTPConstants.ENCODING_GZIP.equals(conn.getContentEncoding());
         InputStream instream = null;
         try {
             instream = new CountingInputStream(conn.getInputStream());
             if (gzipped) {
                 in = new BufferedInputStream(new GZIPInputStream(instream));
             } else {
                 in = new BufferedInputStream(instream);
             }
         } catch (IOException e) {
             if (! (e.getCause() instanceof FileNotFoundException))
             {
                 log.error("readResponse: "+e.toString());
                 Throwable cause = e.getCause();
                 if (cause != null){
                     log.error("Cause: "+cause);
                     if(cause instanceof Error) {
                         throw (Error)cause;
                     }
                 }
             }
             // Normal InputStream is not available
             InputStream errorStream = conn.getErrorStream();
             if (errorStream == null) {
                 log.info("Error Response Code: "+conn.getResponseCode()+", Server sent no Errorpage");
                 res.setResponseHeaders(getResponseHeaders(conn));
                 res.latencyEnd();
                 return NULL_BA;
             }
 
             log.info("Error Response Code: "+conn.getResponseCode());
 
             if (gzipped) {
                 in = new BufferedInputStream(new GZIPInputStream(errorStream));
             } else {
                 in = new BufferedInputStream(errorStream);
             }
         } catch (Exception e) {
             log.error("readResponse: "+e.toString());
             Throwable cause = e.getCause();
             if (cause != null){
                 log.error("Cause: "+cause);
                 if(cause instanceof Error) {
                     throw (Error)cause;
                 }
             }
             in = new BufferedInputStream(conn.getErrorStream());
         }
         // N.B. this closes 'in'
         byte[] responseData = readResponse(res, in, contentLength);
         if (instream != null) {
-            res.setBodySize(((CountingInputStream) instream).getCount());
+            res.setBodySize(((CountingInputStream) instream).getByteCount());
             instream.close();
         }
         return responseData;
     }
 
     /**
      * Gets the ResponseHeaders from the URLConnection
      *
      * @param conn
      *            connection from which the headers are read
      * @return string containing the headers, one per line
      */
     protected String getResponseHeaders(HttpURLConnection conn) {
         StringBuilder headerBuf = new StringBuilder();
         headerBuf.append(conn.getHeaderField(0));// Leave header as is
         // headerBuf.append(conn.getHeaderField(0).substring(0, 8));
         // headerBuf.append(" ");
         // headerBuf.append(conn.getResponseCode());
         // headerBuf.append(" ");
         // headerBuf.append(conn.getResponseMessage());
         headerBuf.append("\n"); //$NON-NLS-1$
 
         String hfk;
         for (int i = 1; (hfk=conn.getHeaderFieldKey(i)) != null; i++) {
             headerBuf.append(hfk);
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(conn.getHeaderField(i));
             headerBuf.append("\n"); // $NON-NLS-1$
         }
         return headerBuf.toString();
     }
 
     /**
      * Extracts all the required cookies for that particular URL request and
      * sets them in the <code>HttpURLConnection</code> passed in.
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param cookieManager
      *            the <code>CookieManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      */
     private String setConnectionCookie(HttpURLConnection conn, URL u, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(u);
             if (cookieHeader != null) {
                 conn.setRequestProperty(HTTPConstants.HEADER_COOKIE, cookieHeader);
             }
         }
         return cookieHeader;
     }
 
     /**
      * Extracts all the required headers for that particular URL request and
      * sets them in the <code>HttpURLConnection</code> passed in
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param headerManager
      *            the <code>HeaderManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      * @param cacheManager the CacheManager (may be null)
      */
     private void setConnectionHeaders(HttpURLConnection conn, URL u, HeaderManager headerManager, CacheManager cacheManager) {
         // Add all the headers from the HeaderManager
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 for (JMeterProperty jMeterProperty : headers) {
                     Header header = (Header) jMeterProperty.getObjectValue();
                     String n = header.getName();
                     String v = header.getValue();
                     conn.addRequestProperty(n, v);
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(conn, u);
         }
     }
 
     /**
      * Get all the headers for the <code>HttpURLConnection</code> passed in
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @return the headers as a string
      */
     private String getConnectionHeaders(HttpURLConnection conn) {
         // Get all the request properties, which are the headers set on the connection
         StringBuilder hdrs = new StringBuilder(100);
         Map<String, List<String>> requestHeaders = conn.getRequestProperties();
         for(Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
             String headerKey=entry.getKey();
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if(!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(headerKey)) {
                 // value is a List of Strings
                 for (String value : entry.getValue()){
                     hdrs.append(headerKey);
                     hdrs.append(": "); // $NON-NLS-1$
                     hdrs.append(value);
                     hdrs.append("\n"); // $NON-NLS-1$
                 }
             }
         }
         return hdrs.toString();
     }
 
     /**
      * Extracts all the required authorization for that particular URL request
      * and sets it in the <code>HttpURLConnection</code> passed in.
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param authManager
      *            the <code>AuthManager</code> containing all the cookies for
      *            this <code>UrlConfig</code>
      */
     private void setConnectionAuthorization(HttpURLConnection conn, URL u, AuthManager authManager) {
         if (authManager != null) {
             Authorization auth = authManager.getAuthForURL(u);
             if (auth != null) {
                 conn.setRequestProperty(HTTPConstants.HEADER_AUTHORIZATION, auth.toBasicHeader());
             }
         }
     }
 
     /**
      * Samples the URL passed in and stores the result in
      * <code>HTTPSampleResult</code>, following redirects and downloading
      * page resources as appropriate.
      * <p>
      * When getting a redirect target, redirects are not followed and resources
      * are not downloaded. The caller will take care of this.
      *
      * @param url
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling
      */
     @Override
     protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {
         HttpURLConnection conn = null;
 
         String urlStr = url.toString();
         if (log.isDebugEnabled()) {
             log.debug("Start : sample " + urlStr);
             log.debug("method " + method+ " followingRedirect " + areFollowingRedirect + " depth " + frameDepth);            
         }
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(urlStr);
         res.setURL(url);
         res.setHTTPMethod(method);
 
         res.sampleStart(); // Count the retries as well in the time
 
         // Check cache for an entry with an Expires header in the future
         final CacheManager cacheManager = getCacheManager();
         if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
            if (cacheManager.inCache(url)) {
                return updateSampleResultForResourceInCache(res);
            }
         }
 
         try {
             // Sampling proper - establish the connection and read the response:
             // Repeatedly try to connect:
             int retry = -1;
             // Start with -1 so tries at least once, and retries at most MAX_CONN_RETRIES times
             for (; retry < MAX_CONN_RETRIES; retry++) {
                 try {
                     conn = setupConnection(url, method, res);
                     // Attempt the connection:
                     savedConn = conn;
                     conn.connect();
                     break;
                 } catch (BindException e) {
                     if (retry >= MAX_CONN_RETRIES) {
                         log.error("Can't connect after "+retry+" retries, "+e);
                         throw e;
                     }
                     log.debug("Bind exception, try again");
                     if (conn!=null) {
                         savedConn = null; // we don't want interrupt to try disconnection again
                         conn.disconnect();
                     }
                     setUseKeepAlive(false);
                 } catch (IOException e) {
                     log.debug("Connection failed, giving up");
                     throw e;
                 }
             }
             if (retry > MAX_CONN_RETRIES) {
                 // This should never happen, but...
                 throw new BindException();
             }
             // Nice, we've got a connection. Finish sending the request:
             if (method.equals(HTTPConstants.POST)) {
                 String postBody = sendPostData(conn);
                 res.setQueryString(postBody);
             } else if (method.equals(HTTPConstants.PUT)) {
                 String putBody = sendPutData(conn);
                 res.setQueryString(putBody);
             }
             // Request sent. Now get the response:
             byte[] responseData = readResponse(conn, res);
 
             res.sampleEnd();
             // Done with the sampling proper.
 
             // Now collect the results into the HTTPSampleResult:
 
             res.setResponseData(responseData);
 
             int errorLevel = conn.getResponseCode();
             String respMsg = conn.getResponseMessage();
             String hdr=conn.getHeaderField(0);
             if (hdr == null) {
                 hdr="(null)";  // $NON-NLS-1$
             }
             if (errorLevel == -1){// Bug 38902 - sometimes -1 seems to be returned unnecessarily
                 if (respMsg != null) {// Bug 41902 - NPE
                     try {
                         errorLevel = Integer.parseInt(respMsg.substring(0, 3));
                         log.warn("ResponseCode==-1; parsed "+respMsg+ " as "+errorLevel);
                       } catch (NumberFormatException e) {
                         log.warn("ResponseCode==-1; could not parse "+respMsg+" hdr: "+hdr);
                       }
                 } else {
                     respMsg=hdr; // for result
                     log.warn("ResponseCode==-1 & null ResponseMessage. Header(0)= "+hdr);
                 }
             }
             if (errorLevel == -1) {
                 res.setResponseCode("(null)"); // $NON-NLS-1$
             } else {
                 res.setResponseCode(Integer.toString(errorLevel));
             }
             res.setSuccessful(isSuccessCode(errorLevel));
 
             if (respMsg == null) {// has been seen in a redirect
                 respMsg=hdr; // use header (if possible) if no message found
             }
             res.setResponseMessage(respMsg);
 
             String ct = conn.getContentType();
             if (ct != null){
                 res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                 res.setEncodingAndType(ct);
             }
 
             String responseHeaders = getResponseHeaders(conn);
             res.setResponseHeaders(responseHeaders);
             if (res.isRedirect()) {
                 res.setRedirectLocation(conn.getHeaderField(HTTPConstants.HEADER_LOCATION));
             }
             
             // record headers size to allow HTTPSampleResult.getBytes() with different options
             res.setHeadersSize(responseHeaders.replaceAll("\n", "\r\n") // $NON-NLS-1$ $NON-NLS-2$
                     .length() + 2); // add 2 for a '\r\n' at end of headers (before data) 
             if (log.isDebugEnabled()) {
-                log.debug("Response headersSize=" + res.getHeadersSize() + " bodySize=" + res.getBodySize()
-                        + " Total=" + (res.getHeadersSize() + res.getBodySize()));
+                log.debug("Response headersSize=" + res.getHeadersSize() + " bodySize=" + res.getBodySizeAsLong()
+                        + " Total=" + (res.getHeadersSize() + res.getBodySizeAsLong()));
             }
             
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 res.setURL(conn.getURL());
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(conn, url, getCookieManager());
 
             // Save cache information
             if (cacheManager != null){
                 cacheManager.saveDetails(conn, res);
             }
 
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
             log.debug("End : sample");
             return res;
         } catch (IOException e) {
             res.sampleEnd();
             savedConn = null; // we don't want interrupt to try disconnection again
             // We don't want to continue using this connection, even if KeepAlive is set
             if (conn != null) { // May not exist
                 conn.disconnect();
             }
             conn=null; // Don't process again
             return errorResult(e, res);
         } finally {
             // calling disconnect doesn't close the connection immediately,
             // but indicates we're through with it. The JVM should close
             // it when necessary.
             savedConn = null; // we don't want interrupt to try disconnection again
             disconnect(conn); // Disconnect unless using KeepAlive
         }
     }
 
     protected void disconnect(HttpURLConnection conn) {
         if (conn != null) {
             String connection = conn.getHeaderField(HTTPConstants.HEADER_CONNECTION);
             String protocol = conn.getHeaderField(0);
             if ((connection == null && (protocol == null || !protocol.startsWith(HTTPConstants.HTTP_1_1)))
                     || (connection != null && connection.equalsIgnoreCase(HTTPConstants.CONNECTION_CLOSE))) {
                 conn.disconnect();
             } // TODO ? perhaps note connection so it can be disconnected at end of test?
         }
     }
 
     /**
      * From the <code>HttpURLConnection</code>, store all the "set-cookie"
      * key-pair values in the cookieManager of the <code>UrlConfig</code>.
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param cookieManager
      *            the <code>CookieManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      */
     private void saveConnectionCookies(HttpURLConnection conn, URL u, CookieManager cookieManager) {
         if (cookieManager != null) {
             for (int i = 1; conn.getHeaderFieldKey(i) != null; i++) {
                 if (conn.getHeaderFieldKey(i).equalsIgnoreCase(HTTPConstants.HEADER_SET_COOKIE)) {
                     cookieManager.addCookieFromHeader(conn.getHeaderField(i), u);
                 }
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean interrupt() {
         HttpURLConnection conn = savedConn;
         if (conn != null) {
             savedConn = null;
             conn.disconnect();
         }
         return conn != null;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index 0dd4b1ea2..c3d543648 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1,2014 +1,2024 @@
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
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.Future;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.Cookie;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.DNSCacheManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.BaseParser;
 import org.apache.jmeter.protocol.http.parser.LinkExtractorParseException;
 import org.apache.jmeter.protocol.http.parser.LinkExtractorParser;
 import org.apache.jmeter.protocol.http.sampler.ResourcesDownloader.AsynSamplerResultHolder;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.DirectAccessByteArrayOutputStream;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.HTTPFileArgs;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * Common constants and methods for HTTP samplers
  *
  */
 public abstract class HTTPSamplerBase extends AbstractSampler
     implements TestStateListener, TestIterationListener, ThreadListener, HTTPConstantsInterface {
 
     private static final long serialVersionUID = 241L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
             Arrays.asList(
                     "org.apache.jmeter.config.gui.LoginConfigGui",
                     "org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui",
                     "org.apache.jmeter.protocol.http.gui.HeaderPanel",
                     "org.apache.jmeter.protocol.http.control.DNSCacheManager",
                     "org.apache.jmeter.protocol.http.gui.DNSCachePanel",
                     "org.apache.jmeter.protocol.http.gui.AuthPanel",
                     "org.apache.jmeter.protocol.http.gui.CacheManagerGui",
                     "org.apache.jmeter.protocol.http.gui.CookiePanel"
             ));
 
     //+ JMX names - do not change
     public static final String ARGUMENTS = "HTTPsampler.Arguments"; // $NON-NLS-1$
 
     public static final String AUTH_MANAGER = "HTTPSampler.auth_manager"; // $NON-NLS-1$
 
     public static final String COOKIE_MANAGER = "HTTPSampler.cookie_manager"; // $NON-NLS-1$
 
     public static final String CACHE_MANAGER = "HTTPSampler.cache_manager"; // $NON-NLS-1$
 
     public static final String HEADER_MANAGER = "HTTPSampler.header_manager"; // $NON-NLS-1$
 
     public static final String DNS_CACHE_MANAGER = "HTTPSampler.dns_cache_manager"; // $NON-NLS-1$
 
     public static final String DOMAIN = "HTTPSampler.domain"; // $NON-NLS-1$
 
     public static final String PORT = "HTTPSampler.port"; // $NON-NLS-1$
 
     public static final String PROXYHOST = "HTTPSampler.proxyHost"; // $NON-NLS-1$
 
     public static final String PROXYPORT = "HTTPSampler.proxyPort"; // $NON-NLS-1$
 
     public static final String PROXYUSER = "HTTPSampler.proxyUser"; // $NON-NLS-1$
 
     public static final String PROXYPASS = "HTTPSampler.proxyPass"; // $NON-NLS-1$
 
     public static final String CONNECT_TIMEOUT = "HTTPSampler.connect_timeout"; // $NON-NLS-1$
 
     public static final String RESPONSE_TIMEOUT = "HTTPSampler.response_timeout"; // $NON-NLS-1$
 
     public static final String METHOD = "HTTPSampler.method"; // $NON-NLS-1$
 
     /** This is the encoding used for the content, i.e. the charset name, not the header "Content-Encoding" */
     public static final String CONTENT_ENCODING = "HTTPSampler.contentEncoding"; // $NON-NLS-1$
 
     public static final String IMPLEMENTATION = "HTTPSampler.implementation"; // $NON-NLS-1$
 
     public static final String PATH = "HTTPSampler.path"; // $NON-NLS-1$
 
     public static final String FOLLOW_REDIRECTS = "HTTPSampler.follow_redirects"; // $NON-NLS-1$
 
     public static final String AUTO_REDIRECTS = "HTTPSampler.auto_redirects"; // $NON-NLS-1$
 
     public static final String PROTOCOL = "HTTPSampler.protocol"; // $NON-NLS-1$
 
     static final String PROTOCOL_FILE = "file"; // $NON-NLS-1$
 
     private static final String DEFAULT_PROTOCOL = HTTPConstants.PROTOCOL_HTTP;
 
     public static final String URL = "HTTPSampler.URL"; // $NON-NLS-1$
 
     /**
      * IP source to use - does not apply to Java HTTP implementation currently
      */
     public static final String IP_SOURCE = "HTTPSampler.ipSource"; // $NON-NLS-1$
 
     public static final String IP_SOURCE_TYPE = "HTTPSampler.ipSourceType"; // $NON-NLS-1$
 
     public static final String USE_KEEPALIVE = "HTTPSampler.use_keepalive"; // $NON-NLS-1$
 
     public static final String DO_MULTIPART_POST = "HTTPSampler.DO_MULTIPART_POST"; // $NON-NLS-1$
 
     public static final String BROWSER_COMPATIBLE_MULTIPART  = "HTTPSampler.BROWSER_COMPATIBLE_MULTIPART"; // $NON-NLS-1$
 
     public static final String CONCURRENT_DWN = "HTTPSampler.concurrentDwn"; // $NON-NLS-1$
 
     public static final String CONCURRENT_POOL = "HTTPSampler.concurrentPool"; // $NON-NLS-1$
 
     public static final int CONCURRENT_POOL_SIZE = 6; // Default concurrent pool size for download embedded resources
 
     private static final String CONCURRENT_POOL_DEFAULT = Integer.toString(CONCURRENT_POOL_SIZE); // default for concurrent pool
 
     private static final String USER_AGENT = "User-Agent"; // $NON-NLS-1$
 
     //- JMX names
 
     public static final boolean BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT = false; // The default setting to be used (i.e. historic)
 
+    private static final int MAX_BYTES_TO_STORE_PER_REQUEST =
+            JMeterUtils.getPropDefault("httpsampler.max_bytes_to_store_per_request", 10 * 1024 *1024); // $NON-NLS-1$ // default value: 10MB
+
+    private static final int MAX_BUFFER_SIZE = 
+            JMeterUtils.getPropDefault("httpsampler.max_buffer_size", 65 * 1024); // $NON-NLS-1$
+
     private static final boolean IGNORE_FAILED_EMBEDDED_RESOURCES =
             JMeterUtils.getPropDefault("httpsampler.ignore_failed_embedded_resources", false); // $NON-NLS-1$ // default value: false
 
     private static final boolean IGNORE_EMBEDDED_RESOURCES_DATA =
             JMeterUtils.getPropDefault("httpsampler.embedded_resources_use_md5", false); // $NON-NLS-1$ // default value: false
 
     public enum SourceType {
         HOSTNAME("web_testing_source_ip_hostname"), //$NON-NLS-1$
         DEVICE("web_testing_source_ip_device"), //$NON-NLS-1$
         DEVICE_IPV4("web_testing_source_ip_device_ipv4"), //$NON-NLS-1$
         DEVICE_IPV6("web_testing_source_ip_device_ipv6"); //$NON-NLS-1$
 
         public final String propertyName;
         SourceType(String propertyName) {
             this.propertyName = propertyName;
         }
     }
 
     public static final int SOURCE_TYPE_DEFAULT = HTTPSamplerBase.SourceType.HOSTNAME.ordinal();
 
     // Use for ComboBox Source Address Type. Preserve order (specially with localization)
     public static String[] getSourceTypeList() {
         final SourceType[] types = SourceType.values();
         final String[] displayStrings = new String[types.length];
         for(int i = 0; i < types.length; i++) {
             displayStrings[i] = JMeterUtils.getResString(types[i].propertyName);
         }
         return displayStrings;
     }
 
     public static final String DEFAULT_METHOD = HTTPConstants.GET; // $NON-NLS-1$
 
     private static final List<String> METHODLIST;
     static {
         List<String> defaultMethods = new ArrayList<>(Arrays.asList(
             DEFAULT_METHOD, // i.e. GET
             HTTPConstants.POST,
             HTTPConstants.HEAD,
             HTTPConstants.PUT,
             HTTPConstants.OPTIONS,
             HTTPConstants.TRACE,
             HTTPConstants.DELETE,
             HTTPConstants.PATCH,
             HTTPConstants.PROPFIND,
             HTTPConstants.PROPPATCH,
             HTTPConstants.MKCOL,
             HTTPConstants.COPY,
             HTTPConstants.MOVE,
             HTTPConstants.LOCK,
             HTTPConstants.UNLOCK,
             HTTPConstants.REPORT,
             HTTPConstants.MKCALENDAR,
             HTTPConstants.SEARCH
         ));
         String userDefinedMethods = JMeterUtils.getPropDefault(
                 "httpsampler.user_defined_methods", "");
         if (StringUtils.isNotBlank(userDefinedMethods)) {
             defaultMethods.addAll(Arrays.asList(userDefinedMethods.split("\\s*,\\s*")));
         }
         METHODLIST = Collections.unmodifiableList(defaultMethods);
     }
 
     // @see mergeFileProperties
     // Must be private, as the file list needs special handling
     private static final String FILE_ARGS = "HTTPsampler.Files"; // $NON-NLS-1$
     // MIMETYPE is kept for backward compatibility with old test plans
     private static final String MIMETYPE = "HTTPSampler.mimetype"; // $NON-NLS-1$
     // FILE_NAME is kept for backward compatibility with old test plans
     private static final String FILE_NAME = "HTTPSampler.FILE_NAME"; // $NON-NLS-1$
     /* Shown as Parameter Name on the GUI */
     // FILE_FIELD is kept for backward compatibility with old test plans
     private static final String FILE_FIELD = "HTTPSampler.FILE_FIELD"; // $NON-NLS-1$
 
     public static final String CONTENT_TYPE = "HTTPSampler.CONTENT_TYPE"; // $NON-NLS-1$
 
     // IMAGE_PARSER now really means EMBEDDED_PARSER
     public static final String IMAGE_PARSER = "HTTPSampler.image_parser"; // $NON-NLS-1$
 
     // Embedded URLs must match this RE (if provided)
     public static final String EMBEDDED_URL_RE = "HTTPSampler.embedded_url_re"; // $NON-NLS-1$
 
     public static final String MONITOR = "HTTPSampler.monitor"; // $NON-NLS-1$
 
     // Store MD5 hash instead of storing response
     public static final String MD5 = "HTTPSampler.md5"; // $NON-NLS-1$
 
     /** A number to indicate that the port has not been set. */
     public static final int UNSPECIFIED_PORT = 0;
     public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$
     // TODO - change to use URL version? Will this affect test plans?
 
     /** If the port is not present in a URL, getPort() returns -1 */
     public static final int URL_UNSPECIFIED_PORT = -1;
     public static final String URL_UNSPECIFIED_PORT_AS_STRING = "-1"; // $NON-NLS-1$
 
     protected static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
 
     protected static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";
 
     public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw"; // TODO - belongs elsewhere
 
     public static final boolean POST_BODY_RAW_DEFAULT = false;
 
     private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
 
     private static final String QRY_SEP = "&"; // $NON-NLS-1$
 
     private static final String QRY_PFX = "?"; // $NON-NLS-1$
 
     protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 20); // $NON-NLS-1$
 
     protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$
 
 
     // Derive the mapping of content types to parsers
     private static final Map<String, String> PARSERS_FOR_CONTENT_TYPE = new HashMap<>();
     // Not synch, but it is not modified after creation
 
     private static final String RESPONSE_PARSERS = // list of parsers
             JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
 
     static {
         String[] parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
         for (final String parser : parsers) {
             String classname = JMeterUtils.getProperty(parser + ".className");//$NON-NLS-1$
             if (classname == null) {
                 log.error("Cannot find .className property for " + parser+", ensure you set property:'" + parser + ".className'");
                 continue;
             }
             String typeList = JMeterUtils.getProperty(parser + ".types");//$NON-NLS-1$
             if (typeList != null) {
                 String[] types = JOrphanUtils.split(typeList, " ", true);
                 for (final String type : types) {
                     log.info("Parser for " + type + " is " + classname);
                     PARSERS_FOR_CONTENT_TYPE.put(type, classname);
                 }
             } else {
                 log.warn("Cannot find .types property for " + parser
                         + ", as a consequence parser will not be used, to make it usable, define property:'"
                         + parser + ".types'");
             }
         }
     }
 
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static final boolean REMOVESLASHDOTDOT =
             JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
 
     ////////////////////// Code ///////////////////////////
 
     public HTTPSamplerBase() {
         setArguments(new Arguments());
     }
 
     /**
      * Determine if the file should be sent as the entire Content body,
      * i.e. without any additional wrapping.
      *
      * @return true if specified file is to be sent as the body,
      * i.e. there is a single file entry which has a non-empty path and
      * an empty Parameter name.
      */
     public boolean getSendFileAsPostBody() {
         // If there is one file with no parameter name, the file will
         // be sent as post body.
         HTTPFileArg[] files = getHTTPFiles();
         return (files.length == 1)
                 && (files[0].getPath().length() > 0)
                 && (files[0].getParamName().length() == 0);
     }
 
     /**
      * Determine if none of the parameters have a name, and if that
      * is the case, it means that the parameter values should be sent
      * as the entity body
      *
      * @return true if none of the parameters have a name specified
      */
     public boolean getSendParameterValuesAsPostBody() {
         if (getPostBodyRaw()) {
             return true;
         } else {
             boolean noArgumentsHasName = true;
             for (JMeterProperty jMeterProperty : getArguments()) {
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 if (arg.getName() != null && arg.getName().length() > 0) {
                     noArgumentsHasName = false;
                     break;
                 }
             }
             return noArgumentsHasName;
         }
     }
 
     /**
      * Determine if we should use multipart/form-data or
      * application/x-www-form-urlencoded for the post
      *
      * @return true if multipart/form-data should be used and method is POST
      */
     public boolean getUseMultipartForPost() {
         // We use multipart if we have been told so, or files are present
         // and the files should not be send as the post body
         HTTPFileArg[] files = getHTTPFiles();
         return HTTPConstants.POST.equals(getMethod())
                 && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()));
     }
 
     public void setProtocol(String value) {
         setProperty(PROTOCOL, value.toLowerCase(java.util.Locale.ENGLISH));
     }
 
     /**
      * Gets the protocol, with default.
      *
      * @return the protocol
      */
     public String getProtocol() {
         String protocol = getPropertyAsString(PROTOCOL);
         if (protocol == null || protocol.length() == 0) {
             return DEFAULT_PROTOCOL;
         }
         return protocol;
     }
 
     /**
      * Sets the Path attribute of the UrlConfig object Also calls parseArguments
      * to extract and store any query arguments
      *
      * @param path
      *            The new Path value
      */
     public void setPath(String path) {
         // We know that URL arguments should always be encoded in UTF-8 according to spec
         setPath(path, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Sets the PATH property; if the request is a GET or DELETE (and the path
      * does not start with http[s]://) it also calls {@link #parseArguments(String, String)}
      * to extract and store any query arguments.
      *
      * @param path
      *            The new Path value
      * @param contentEncoding
      *            The encoding used for the querystring parameter values
      */
     public void setPath(String path, String contentEncoding) {
         boolean fullUrl = path.startsWith(HTTP_PREFIX) || path.startsWith(HTTPS_PREFIX);
         boolean getOrDelete = HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod());
         if (!fullUrl && getOrDelete) {
             int index = path.indexOf(QRY_PFX);
             if (index > -1) {
                 setProperty(PATH, path.substring(0, index));
                 // Parse the arguments in querystring, assuming specified encoding for values
                 parseArguments(path.substring(index + 1), contentEncoding);
             } else {
                 setProperty(PATH, path);
             }
         } else {
             setProperty(PATH, path);
         }
     }
 
     public String getPath() {
         String p = getPropertyAsString(PATH);
         return encodeSpaces(p);
     }
 
     public void setFollowRedirects(boolean value) {
         setProperty(new BooleanProperty(FOLLOW_REDIRECTS, value));
     }
 
     public boolean getFollowRedirects() {
         return getPropertyAsBoolean(FOLLOW_REDIRECTS);
     }
 
     public void setAutoRedirects(boolean value) {
         setProperty(new BooleanProperty(AUTO_REDIRECTS, value));
     }
 
     public boolean getAutoRedirects() {
         return getPropertyAsBoolean(AUTO_REDIRECTS);
     }
 
     public void setMethod(String value) {
         setProperty(METHOD, value);
     }
 
     public String getMethod() {
         return getPropertyAsString(METHOD);
     }
 
     /**
      * Sets the value of the encoding to be used for the content.
      *
      * @param charsetName the name of the encoding to be used
      */
     public void setContentEncoding(String charsetName) {
         setProperty(CONTENT_ENCODING, charsetName);
     }
 
     /**
      *
      * @return the encoding of the content, i.e. its charset name
      */
     public String getContentEncoding() {
         return getPropertyAsString(CONTENT_ENCODING);
     }
 
     public void setUseKeepAlive(boolean value) {
         setProperty(new BooleanProperty(USE_KEEPALIVE, value));
     }
 
     public boolean getUseKeepAlive() {
         return getPropertyAsBoolean(USE_KEEPALIVE);
     }
 
     public void setDoMultipartPost(boolean value) {
         setProperty(new BooleanProperty(DO_MULTIPART_POST, value));
     }
 
     public boolean getDoMultipartPost() {
         return getPropertyAsBoolean(DO_MULTIPART_POST, false);
     }
 
     public void setDoBrowserCompatibleMultipart(boolean value) {
         setProperty(BROWSER_COMPATIBLE_MULTIPART, value, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public boolean getDoBrowserCompatibleMultipart() {
         return getPropertyAsBoolean(BROWSER_COMPATIBLE_MULTIPART, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public void setMonitor(String value) {
         this.setProperty(MONITOR, value);
     }
 
     public void setMonitor(boolean truth) {
         this.setProperty(MONITOR, truth);
     }
 
     public String getMonitor() {
         return this.getPropertyAsString(MONITOR);
     }
 
     public boolean isMonitor() {
         return this.getPropertyAsBoolean(MONITOR);
     }
 
     public void setImplementation(String value) {
         this.setProperty(IMPLEMENTATION, value);
     }
 
     public String getImplementation() {
         return this.getPropertyAsString(IMPLEMENTATION);
     }
 
     public boolean useMD5() {
         return this.getPropertyAsBoolean(MD5, false);
     }
 
     public void setMD5(boolean truth) {
         this.setProperty(MD5, truth, false);
     }
 
     /**
      * Add an argument which has already been encoded
      *
      * @param name name of the argument
      * @param value value of the argument
      */
     public void addEncodedArgument(String name, String value) {
         this.addEncodedArgument(name, value, ARG_VAL_SEP);
     }
 
     /**
      * Creates an HTTPArgument and adds it to the current set {@link #getArguments()} of arguments.
      *
      * @param name - the parameter name
      * @param value - the parameter value
      * @param metaData - normally just '='
      * @param contentEncoding - the encoding, may be null
      */
     public void addEncodedArgument(String name, String value, String metaData, String contentEncoding) {
         if (log.isDebugEnabled()) {
             log.debug("adding argument: name: " + name + " value: " + value + " metaData: " + metaData + " contentEncoding: " + contentEncoding);
         }
 
         HTTPArgument arg;
         final boolean nonEmptyEncoding = !StringUtils.isEmpty(contentEncoding);
         if (nonEmptyEncoding) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         } else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded;
         if (nonEmptyEncoding) {
             try {
                 valueEncoded = arg.getEncodedValue(contentEncoding);
             } catch (UnsupportedEncodingException e) {
                 log.warn("Unable to get encoded value using encoding " + contentEncoding);
                 valueEncoded = arg.getEncodedValue();
             }
         } else {
             valueEncoded = arg.getEncodedValue();
         }
         // If there is no difference, we mark it as not needing encoding
         if (arg.getName().equals(arg.getEncodedName())
                 && arg.getValue().equals(valueEncoded)) {
             arg.setAlwaysEncoded(false);
         }
         this.getArguments().addArgument(arg);
     }
 
     public void addEncodedArgument(String name, String value, String metaData) {
         this.addEncodedArgument(name, value, metaData, null);
     }
 
     public void addNonEncodedArgument(String name, String value, String metadata) {
         HTTPArgument arg = new HTTPArgument(name, value, metadata, false);
         arg.setAlwaysEncoded(false);
         this.getArguments().addArgument(arg);
     }
 
     public void addArgument(String name, String value) {
         this.getArguments().addArgument(new HTTPArgument(name, value));
     }
 
     public void addArgument(String name, String value, String metadata) {
         this.getArguments().addArgument(new HTTPArgument(name, value, metadata));
     }
 
     public boolean hasArguments() {
         return getArguments().getArgumentCount() > 0;
     }
 
     @Override
     public void addTestElement(TestElement el) {
         if (el instanceof CookieManager) {
             setCookieManager((CookieManager) el);
         } else if (el instanceof CacheManager) {
             setCacheManager((CacheManager) el);
         } else if (el instanceof HeaderManager) {
             setHeaderManager((HeaderManager) el);
         } else if (el instanceof AuthManager) {
             setAuthManager((AuthManager) el);
         } else if (el instanceof DNSCacheManager) {
             setDNSResolver((DNSCacheManager) el);
         } else {
             super.addTestElement(el);
         }
     }
 
     /**
      * {@inheritDoc}
      * <p>
      * Clears the Header Manager property so subsequent loops don't keep merging more elements
      */
     @Override
     public void clearTestElementChildren() {
         removeProperty(HEADER_MANAGER);
     }
 
     public void setPort(int value) {
         setProperty(new IntegerProperty(PORT, value));
     }
 
     /**
      * Get the port number for a URL, applying defaults if necessary.
      * (Called by CookieManager.)
      * @param protocol from {@link URL#getProtocol()}
      * @param port number from {@link URL#getPort()}
      * @return the default port for the protocol
      */
     public static int getDefaultPort(String protocol, int port) {
         if (port == URL_UNSPECIFIED_PORT) {
             if (protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTP)) {
                 return HTTPConstants.DEFAULT_HTTP_PORT;
             } else if (protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTPS)) {
                 return HTTPConstants.DEFAULT_HTTPS_PORT;
             }
         }
         return port;
     }
 
     /**
      * Get the port number from the port string, allowing for trailing blanks.
      *
      * @return port number or UNSPECIFIED_PORT (== 0)
      */
     public int getPortIfSpecified() {
         String portAsString = getPropertyAsString(PORT);
         if(portAsString == null || portAsString.isEmpty()) {
             return UNSPECIFIED_PORT;
         }
         
         try {
             return Integer.parseInt(portAsString.trim());
         } catch (NumberFormatException e) {
             return UNSPECIFIED_PORT;
         }
     }
 
     /**
      * Tell whether the default port for the specified protocol is used
      *
      * @return true if the default port number for the protocol is used, false otherwise
      */
     public boolean isProtocolDefaultPort() {
         final int port = getPortIfSpecified();
         final String protocol = getProtocol();
         boolean isDefaultHTTPPort = HTTPConstants.PROTOCOL_HTTP
                 .equalsIgnoreCase(protocol)
                 && port == HTTPConstants.DEFAULT_HTTP_PORT;
         boolean isDefaultHTTPSPort = HTTPConstants.PROTOCOL_HTTPS
                 .equalsIgnoreCase(protocol)
                 && port == HTTPConstants.DEFAULT_HTTPS_PORT;
         return port == UNSPECIFIED_PORT ||
                 isDefaultHTTPPort ||
                 isDefaultHTTPSPort;
     }
 
     /**
      * Get the port; apply the default for the protocol if necessary.
      *
      * @return the port number, with default applied if required.
      */
     public int getPort() {
         final int port = getPortIfSpecified();
         if (port == UNSPECIFIED_PORT) {
             String prot = getProtocol();
             if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return HTTPConstants.DEFAULT_HTTPS_PORT;
             }
             if (!HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: " + prot);
                 // TODO - should this return something else?
             }
             return HTTPConstants.DEFAULT_HTTP_PORT;
         }
         return port;
     }
 
     public void setDomain(String value) {
         setProperty(DOMAIN, value);
     }
 
     public String getDomain() {
         return getPropertyAsString(DOMAIN);
     }
 
     public void setConnectTimeout(String value) {
         setProperty(CONNECT_TIMEOUT, value, "");
     }
 
     public int getConnectTimeout() {
         return getPropertyAsInt(CONNECT_TIMEOUT, 0);
     }
 
     public void setResponseTimeout(String value) {
         setProperty(RESPONSE_TIMEOUT, value, "");
     }
 
     public int getResponseTimeout() {
         return getPropertyAsInt(RESPONSE_TIMEOUT, 0);
     }
 
     public String getProxyHost() {
         return getPropertyAsString(PROXYHOST);
     }
 
     public int getProxyPortInt() {
         return getPropertyAsInt(PROXYPORT, 0);
     }
 
     public String getProxyUser() {
         return getPropertyAsString(PROXYUSER);
     }
 
     public String getProxyPass() {
         return getPropertyAsString(PROXYPASS);
     }
 
     // gets called from ctor, so has to be final
     public final void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
 
     /**
      * @param value Boolean that indicates body will be sent as is
      */
     public void setPostBodyRaw(boolean value) {
         setProperty(POST_BODY_RAW, value, POST_BODY_RAW_DEFAULT);
     }
 
     /**
      * @return boolean that indicates body will be sent as is
      */
     public boolean getPostBodyRaw() {
         return getPropertyAsBoolean(POST_BODY_RAW, POST_BODY_RAW_DEFAULT);
     }
 
     public void setAuthManager(AuthManager value) {
         AuthManager mgr = getAuthManager();
         if (mgr != null) {
             log.warn("Existing AuthManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(AUTH_MANAGER, value));
     }
 
     public AuthManager getAuthManager() {
         return (AuthManager) getProperty(AUTH_MANAGER).getObjectValue();
     }
 
     public void setHeaderManager(HeaderManager value) {
         HeaderManager mgr = getHeaderManager();
         if (mgr != null) {
             value = mgr.merge(value, true);
             if (log.isDebugEnabled()) {
                 log.debug("Existing HeaderManager '" + mgr.getName() + "' merged with '" + value.getName() + "'");
                 for (int i = 0; i < value.getHeaders().size(); i++) {
                     log.debug("    " + value.getHeader(i).getName() + "=" + value.getHeader(i).getValue());
                 }
             }
         }
         setProperty(new TestElementProperty(HEADER_MANAGER, value));
     }
 
     public HeaderManager getHeaderManager() {
         return (HeaderManager) getProperty(HEADER_MANAGER).getObjectValue();
     }
 
     // private method to allow AsyncSample to reset the value without performing checks
     private void setCookieManagerProperty(CookieManager value) {
         setProperty(new TestElementProperty(COOKIE_MANAGER, value));
     }
 
     public void setCookieManager(CookieManager value) {
         CookieManager mgr = getCookieManager();
         if (mgr != null) {
             log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setCookieManagerProperty(value);
     }
 
     public CookieManager getCookieManager() {
         return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
     }
 
     // private method to allow AsyncSample to reset the value without performing checks
     private void setCacheManagerProperty(CacheManager value) {
         setProperty(new TestElementProperty(CACHE_MANAGER, value));
     }
 
     public void setCacheManager(CacheManager value) {
         CacheManager mgr = getCacheManager();
         if (mgr != null) {
             log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setCacheManagerProperty(value);
     }
 
     public CacheManager getCacheManager() {
         return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
     }
 
     public DNSCacheManager getDNSResolver() {
         return (DNSCacheManager) getProperty(DNS_CACHE_MANAGER).getObjectValue();
     }
 
     public void setDNSResolver(DNSCacheManager cacheManager) {
         DNSCacheManager mgr = getDNSResolver();
         if (mgr != null) {
             log.warn("Existing DNSCacheManager " + mgr.getName() + " superseded by " + cacheManager.getName());
         }
         setProperty(new TestElementProperty(DNS_CACHE_MANAGER, cacheManager));
     }
 
     public boolean isImageParser() {
         return getPropertyAsBoolean(IMAGE_PARSER, false);
     }
 
     public void setImageParser(boolean parseImages) {
         setProperty(IMAGE_PARSER, parseImages, false);
     }
 
     /**
      * Get the regular expression URLs must match.
      *
      * @return regular expression (or empty) string
      */
     public String getEmbeddedUrlRE() {
         return getPropertyAsString(EMBEDDED_URL_RE, "");
     }
 
     public void setEmbeddedUrlRE(String regex) {
         setProperty(new StringProperty(EMBEDDED_URL_RE, regex));
     }
 
     /**
      * Populates the provided HTTPSampleResult with details from the Exception.
      * Does not create a new instance, so should not be used directly to add a subsample.
      *
      * @param e
      *            Exception representing the error.
      * @param res
      *            SampleResult to be modified
      * @return the modified sampling result containing details of the Exception.
      */
     protected HTTPSampleResult errorResult(Throwable e, HTTPSampleResult res) {
         res.setSampleLabel(res.getSampleLabel());
         res.setDataType(SampleResult.TEXT);
         ByteArrayOutputStream text = new ByteArrayOutputStream(200);
         e.printStackTrace(new PrintStream(text));
         res.setResponseData(text.toByteArray());
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": " + e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": " + e.getMessage());
         res.setSuccessful(false);
         res.setMonitor(this.isMonitor());
         return res;
     }
 
     private static final String HTTP_PREFIX = HTTPConstants.PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = HTTPConstants.PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
     // Bug 51939
     private static final boolean SEPARATE_CONTAINER =
             JMeterUtils.getPropDefault("httpsampler.separate.container", true); // $NON-NLS-1$
 
+
     /**
      * Get the URL, built from its component parts.
      *
      * <p>
      * As a special case, if the path starts with "http[s]://",
      * then the path is assumed to be the entire URL.
      * </p>
      *
      * @return The URL to be requested by this sampler.
      * @throws MalformedURLException if url is malformed
      */
     public URL getUrl() throws MalformedURLException {
         StringBuilder pathAndQuery = new StringBuilder(100);
         String path = this.getPath();
         // Hack to allow entire URL to be provided in host field
         if (path.startsWith(HTTP_PREFIX)
                 || path.startsWith(HTTPS_PREFIX)) {
             return new URL(path);
         }
         String domain = getDomain();
         String protocol = getProtocol();
         if (PROTOCOL_FILE.equalsIgnoreCase(protocol)) {
             domain = null; // allow use of relative file URLs
         } else {
             // HTTP URLs must be absolute, allow file to be relative
             if (!path.startsWith("/")) { // $NON-NLS-1$
                 pathAndQuery.append('/'); // $NON-NLS-1$
             }
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if (HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod())) {
             // Get the query string encoded in specified encoding
             // If no encoding is specified by user, we will get it
             // encoded in UTF-8, which is what the HTTP spec says
             String queryString = getQueryString(getContentEncoding());
             if (queryString.length() > 0) {
                 if (path.contains(QRY_PFX)) {// Already contains a prefix
                     pathAndQuery.append(QRY_SEP);
                 } else {
                     pathAndQuery.append(QRY_PFX);
                 }
                 pathAndQuery.append(queryString);
             }
         }
         // If default port for protocol is used, we do not include port in URL
         if (isProtocolDefaultPort()) {
             return new URL(protocol, domain, pathAndQuery.toString());
         }
         return new URL(protocol, domain, getPort(), pathAndQuery.toString());
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using
      * UTF-8 to encode the URL
      *
      * @return the QueryString value
      */
     public String getQueryString() {
         // We use the encoding which should be used according to the HTTP spec, which is UTF-8
         return getQueryString(EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using the
      * specified encoding to encode the parameter values put into the URL
      *
      * @param contentEncoding the encoding to use for encoding parameter values
      * @return the QueryString value
      */
     public String getQueryString(String contentEncoding) {
         
         CollectionProperty arguments = getArguments().getArguments();
         // Optimisation : avoid building useless objects if empty arguments
         if(arguments.size() == 0) {
             return "";
         }
         
         // Check if the sampler has a specified content encoding
         if (JOrphanUtils.isBlank(contentEncoding)) {
             // We use the encoding which should be used according to the HTTP spec, which is UTF-8
             contentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
         }
         
         StringBuilder buf = new StringBuilder(arguments.size() * 15);
         PropertyIterator iter = arguments.iterator();
         boolean first = true;
         while (iter.hasNext()) {
             HTTPArgument item = null;
             /*
              * N.B. Revision 323346 introduced the ClassCast check, but then used iter.next()
              * to fetch the item to be cast, thus skipping the element that did not cast.
              * Reverted to work more like the original code, but with the check in place.
              * Added a warning message so can track whether it is necessary
              */
             Object objectValue = iter.next().getObjectValue();
             try {
                 item = (HTTPArgument) objectValue;
             } catch (ClassCastException e) {
                 log.warn("Unexpected argument type: " + objectValue.getClass().getName());
                 item = new HTTPArgument((Argument) objectValue);
             }
             final String encodedName = item.getEncodedName();
             if (encodedName.length() == 0) {
                 continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
             }
             if (!first) {
                 buf.append(QRY_SEP);
             } else {
                 first = false;
             }
             buf.append(encodedName);
             if (item.getMetaData() == null) {
                 buf.append(ARG_VAL_SEP);
             } else {
                 buf.append(item.getMetaData());
             }
 
             // Encode the parameter value in the specified content encoding
             try {
                 buf.append(item.getEncodedValue(contentEncoding));
             } catch(UnsupportedEncodingException e) {
                 log.warn("Unable to encode parameter in encoding " + contentEncoding + ", parameter value not included in query string");
             }
         }
         return buf.toString();
     }
 
     // Mark Walsh 2002-08-03, modified to also parse a parameter name value
     // string, where string contains only the parameter name and no equal sign.
     /**
      * This method allows a proxy server to send over the raw text from a
      * browser's output stream to be parsed and stored correctly into the
      * UrlConfig object.
      *
      * For each name found, addArgument() is called
      *
      * @param queryString -
      *            the query string, might be the post body of a http post request.
      * @param contentEncoding -
      *            the content encoding of the query string;
      *            if non-null then it is used to decode the
      */
     public void parseArguments(String queryString, String contentEncoding) {
         String[] args = JOrphanUtils.split(queryString, QRY_SEP);
         final boolean isDebug = log.isDebugEnabled();
         for (String arg : args) {
             if (isDebug) {
                 log.debug("Arg: " + arg);
             }
             // need to handle four cases:
             // - string contains name=value
             // - string contains name=
             // - string contains name
             // - empty string
 
             String metaData; // records the existence of an equal sign
             String name;
             String value;
             int length = arg.length();
             int endOfNameIndex = arg.indexOf(ARG_VAL_SEP);
             if (endOfNameIndex != -1) {// is there a separator?
                 // case of name=value, name=
                 metaData = ARG_VAL_SEP;
                 name = arg.substring(0, endOfNameIndex);
                 value = arg.substring(endOfNameIndex + 1, length);
             } else {
                 metaData = "";
                 name = arg;
                 value = "";
             }
             if (name.length() > 0) {
                 if (isDebug) {
                     log.debug("Name: " + name + " Value: " + value + " Metadata: " + metaData);
                 }
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if (!StringUtils.isEmpty(contentEncoding)) {
                     addEncodedArgument(name, value, metaData, contentEncoding);
                 } else {
                     // If we do not know the encoding, we just use the encoded value
                     // The browser has already done the encoding, so save the values as is
                     addNonEncodedArgument(name, value, metaData);
                 }
             }
         }
     }
 
     public void parseArguments(String queryString) {
         // We do not know the content encoding of the query string
         parseArguments(queryString, null);
     }
 
     @Override
     public String toString() {
         try {
             StringBuilder stringBuffer = new StringBuilder();
             stringBuffer.append(this.getUrl().toString());
             // Append body if it is a post or put
             if (HTTPConstants.POST.equals(getMethod()) || HTTPConstants.PUT.equals(getMethod())) {
                 stringBuffer.append("\nQuery Data: ");
                 stringBuffer.append(getQueryString());
             }
             return stringBuffer.toString();
         } catch (MalformedURLException e) {
             return "";
         }
     }
 
     /**
      * Do a sampling and return its results.
      *
      * @param e
      *            <code>Entry</code> to be sampled
      * @return results of the sampling
      */
     @Override
     public SampleResult sample(Entry e) {
         return sample();
     }
 
     /**
      * Perform a sample, and return the results
      *
      * @return results of the sampling
      */
     public SampleResult sample() {
         SampleResult res = null;
         try {
             res = sample(getUrl(), getMethod(), false, 0);
             if (res != null) {
                 res.setSampleLabel(getName());
             }
             return res;
         } catch (Exception e) {
             return errorResult(e, new HTTPSampleResult());
         }
     }
 
     /**
      * Samples the URL passed in and stores the result in
      * <code>HTTPSampleResult</code>, following redirects and downloading
      * page resources as appropriate.
      * <p>
      * When getting a redirect target, redirects are not followed and resources
      * are not downloaded. The caller will take care of this.
      *
      * @param u
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param depth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling, can be null if u is in CacheManager
      */
     protected abstract HTTPSampleResult sample(URL u,
             String method, boolean areFollowingRedirect, int depth);
 
     /**
      * Download the resources of an HTML page.
      *
      * @param res
      *            result of the initial request - must contain an HTML response
      * @param container
      *            for storing the results, if any
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return res if no resources exist, otherwise the "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
         Iterator<URL> urls = null;
         try {
             final byte[] responseData = res.getResponseData();
             if (responseData.length > 0) {  // Bug 39205
                 final LinkExtractorParser parser = getParser(res);
                 if (parser != null) {
                     String userAgent = getUserAgent(res);
                     urls = parser.getEmbeddedResourceURLs(userAgent, responseData, res.getURL(), res.getDataEncodingWithDefault());
                 }
             }
         } catch (LinkExtractorParseException e) {
             // Don't break the world just because this failed:
             res.addSubResult(errorResult(e, new HTTPSampleResult(res)));
             setParentSampleSuccess(res, false);
         }
 
         // Iterate through the URLs and download each image:
         if (urls != null && urls.hasNext()) {
             if (container == null) {
                 container = new HTTPSampleResult(res);
                 container.addRawSubResult(res);
             }
             res = container;
 
             // Get the URL matcher
             String re = getEmbeddedUrlRE();
             Perl5Matcher localMatcher = null;
             Pattern pattern = null;
             if (re.length() > 0) {
                 try {
                     pattern = JMeterUtils.getPattern(re);
                     localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                 } catch (MalformedCachePatternException e) {
                     log.warn("Ignoring embedded URL match string: " + e.getMessage());
                 }
             }
 
             // For concurrent get resources
             final List<Callable<AsynSamplerResultHolder>> list = new ArrayList<>();
 
             int maxConcurrentDownloads = CONCURRENT_POOL_SIZE; // init with default value
             boolean isConcurrentDwn = isConcurrentDwn();
             if (isConcurrentDwn) {
                 try {
                     maxConcurrentDownloads = Integer.parseInt(getConcurrentPool());
                 } catch (NumberFormatException nfe) {
                     log.warn("Concurrent download resources selected, "// $NON-NLS-1$
                             + "but pool size value is bad. Use default value");// $NON-NLS-1$
                 }
 
                 // if the user choose a number of parallel downloads of 1
                 // no need to use another thread, do the sample on the current thread
                 if (maxConcurrentDownloads == 1) {
                     log.warn("Number of parallel downloads set to 1, (sampler name=" + getName()+")");
                     isConcurrentDwn = false;
                 }
             }
 
             while (urls.hasNext()) {
                 Object binURL = urls.next(); // See catch clause below
                 try {
                     URL url = (URL) binURL;
                     if (url == null) {
                         log.warn("Null URL detected (should not happen)");
                     } else {
                         try {
                             url = escapeIllegalURLCharacters(url);
                         } catch (Exception e) {
                             res.addSubResult(errorResult(new Exception(url.toString() + " is not a correct URI"), new HTTPSampleResult(res)));
                             setParentSampleSuccess(res, false);
                             continue;
                         }
                         // I don't think localMatcher can be null here, but check just in case
                         if (pattern != null && localMatcher != null && !localMatcher.matches(url.toString(), pattern)) {
                             continue; // we have a pattern and the URL does not match, so skip it
                         }
                         try {
                             url = url.toURI().normalize().toURL();
                         } catch (MalformedURLException | URISyntaxException e) {
                             res.addSubResult(errorResult(new Exception(url.toString() + " URI can not be normalized", e), new HTTPSampleResult(res)));
                             setParentSampleSuccess(res, false);
                             continue;
                         }
 
                         if (isConcurrentDwn) {
                             // if concurrent download emb. resources, add to a list for async gets later
                             list.add(new ASyncSample(url, HTTPConstants.GET, false, frameDepth + 1, getCookieManager(), this));
                         } else {
                             // default: serial download embedded resources
                             HTTPSampleResult binRes = sample(url, HTTPConstants.GET, false, frameDepth + 1);
                             res.addSubResult(binRes);
                             setParentSampleSuccess(res, res.isSuccessful() && (binRes == null || binRes.isSuccessful()));
                         }
                     }
                 } catch (ClassCastException e) { // TODO can this happen?
                     res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), new HTTPSampleResult(res)));
                     setParentSampleSuccess(res, false);
                 }
             }
 
             // IF for download concurrent embedded resources
             if (isConcurrentDwn && !list.isEmpty()) {
 
                 ResourcesDownloader resourcesDownloader = ResourcesDownloader.getInstance();
 
                 try {
                     // sample all resources
                     final List<Future<AsynSamplerResultHolder>> retExec =
                             resourcesDownloader.invokeAllAndAwaitTermination(maxConcurrentDownloads, list);
                     CookieManager cookieManager = getCookieManager();
                     // add result to main sampleResult
                     for (Future<AsynSamplerResultHolder> future : retExec) {
                         // this call will not block as the futures return by invokeAllAndAwaitTermination
                         //   are either done or cancelled
                         AsynSamplerResultHolder binRes = future.get();
                         if (cookieManager != null) {
                             CollectionProperty cookies = binRes.getCookies();
                             for (JMeterProperty jMeterProperty : cookies) {
                                 Cookie cookie = (Cookie) jMeterProperty.getObjectValue();
                                 cookieManager.add(cookie);
                             }
                         }
                         res.addSubResult(binRes.getResult());
                         setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));
                     }
                 } catch (InterruptedException ie) {
                     log.warn("Interrupted fetching embedded resources", ie); // $NON-NLS-1$
                 } catch (ExecutionException ee) {
                     log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
                 }
             }
         }
         return res;
     }
 
     /**
      * Gets parser from {@link HTTPSampleResult#getMediaType()}.
      * Returns null if no parser defined for it
      * @param res {@link HTTPSampleResult}
      * @return {@link LinkExtractorParser}
      * @throws LinkExtractorParseException
      */
     private LinkExtractorParser getParser(HTTPSampleResult res)
             throws LinkExtractorParseException {
         String parserClassName =
                 PARSERS_FOR_CONTENT_TYPE.get(res.getMediaType());
         if (!StringUtils.isEmpty(parserClassName)) {
             return BaseParser.getParser(parserClassName);
         }
         return null;
     }
     
     /**
      * @param url URL to escape
      * @return escaped url
      */
     private URL escapeIllegalURLCharacters(java.net.URL url) {
         if (url == null || url.getProtocol().equals("file")) {
             return url;
         }
         try {
             return ConversionUtils.sanitizeUrl(url).toURL();
         } catch (Exception e1) {
             log.error("Error escaping URL:'" + url + "', message:" + e1.getMessage());
             return url;
         }
     }
 
     /**
      * Extract User-Agent header value
      * @param sampleResult HTTPSampleResult
      * @return User Agent part
      */
     private String getUserAgent(HTTPSampleResult sampleResult) {
         String res = sampleResult.getRequestHeaders();
         int index = res.indexOf(USER_AGENT);
         if (index >= 0) {
             // see HTTPHC3Impl#getConnectionHeaders
             // see HTTPHC4Impl#getConnectionHeaders
             // see HTTPJavaImpl#getConnectionHeaders
             //': ' is used by JMeter to fill-in requestHeaders, see getConnectionHeaders
             final String userAgentPrefix = USER_AGENT+": ";
             String userAgentHdr = res.substring(
                     index+userAgentPrefix.length(),
                     res.indexOf('\n',// '\n' is used by JMeter to fill-in requestHeaders, see getConnectionHeaders
                             index+userAgentPrefix.length()+1));
             return userAgentHdr.trim();
         } else {
             if (log.isInfoEnabled()) {
                 log.info("No user agent extracted from requestHeaders:" + res);
             }
             return null;
         }
     }
 
     /**
      * Set parent successful attribute based on IGNORE_FAILED_EMBEDDED_RESOURCES parameter
      * @param res {@link HTTPSampleResult}
      * @param initialValue boolean
      */
     private void setParentSampleSuccess(HTTPSampleResult res, boolean initialValue) {
         if (!IGNORE_FAILED_EMBEDDED_RESOURCES) {
             res.setSuccessful(initialValue);
             if (!initialValue) {
                 StringBuilder detailedMessage = new StringBuilder(80);
                 detailedMessage.append("Embedded resource download error:"); //$NON-NLS-1$
                 for (SampleResult subResult : res.getSubResults()) {
                     HTTPSampleResult httpSampleResult = (HTTPSampleResult) subResult;
                     if (!httpSampleResult.isSuccessful()) {
                         detailedMessage.append(httpSampleResult.getURL())
                                 .append(" code:") //$NON-NLS-1$
                                 .append(httpSampleResult.getResponseCode())
                                 .append(" message:") //$NON-NLS-1$
                                 .append(httpSampleResult.getResponseMessage())
                                 .append(", "); //$NON-NLS-1$
                     }
                 }
                 res.setResponseMessage(detailedMessage.toString()); //$NON-NLS-1$
             }
         }
     }
 
     // TODO: make static?
     protected String encodeSpaces(String path) {
         return JOrphanUtils.replaceAllChars(path, ' ', "%20"); // $NON-NLS-1$
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded(String host) {
         testEnded();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted() {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted(String host) {
         testStarted();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object clone() {
         HTTPSamplerBase base = (HTTPSamplerBase) super.clone();
         return base;
     }
 
     /**
      * Iteratively download the redirect targets of a redirect response.
      * <p>
      * The returned result will contain one subsample for each request issued,
      * including the original one that was passed in. It will be an
      * HTTPSampleResult that should mostly look as if the final destination of
      * the redirect chain had been obtained in a single shot.
      *
      * @param res
      *            result of the initial request - must be a redirect response
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult followRedirects(HTTPSampleResult res, int frameDepth) {
         HTTPSampleResult totalRes = new HTTPSampleResult(res);
         totalRes.addRawSubResult(res);
         HTTPSampleResult lastRes = res;
 
         int redirect;
         for (redirect = 0; redirect < MAX_REDIRECTS; redirect++) {
             boolean invalidRedirectUrl = false;
             String location = lastRes.getRedirectLocation();
             if (log.isDebugEnabled()) {
                 log.debug("Initial location: " + location);
             }
             if (REMOVESLASHDOTDOT) {
                 location = ConversionUtils.removeSlashDotDot(location);
             }
             // Browsers seem to tolerate Location headers with spaces,
             // replacing them automatically with %20. We want to emulate
             // this behaviour.
             location = encodeSpaces(location);
             if (log.isDebugEnabled()) {
                 log.debug("Location after /. and space transforms: " + location);
             }
             // Change all but HEAD into GET (Bug 55450)
             String method = lastRes.getHTTPMethod();
             method = computeMethodForRedirect(method, res.getResponseCode());
 
             try {
                 URL url = ConversionUtils.makeRelativeURL(lastRes.getURL(), location);
                 url = ConversionUtils.sanitizeUrl(url).toURL();
                 if (log.isDebugEnabled()) {
                     log.debug("Location as URL: " + url.toString());
                 }
                 HTTPSampleResult tempRes = sample(url, method, true, frameDepth);
                 if (tempRes != null) {
                     lastRes = tempRes;
                 } else {
                     // Last url was in cache so tempRes is null
                     break;
                 }
             } catch (MalformedURLException | URISyntaxException e) {
                 errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             }
             if (lastRes.getSubResults() != null && lastRes.getSubResults().length > 0) {
                 SampleResult[] subs = lastRes.getSubResults();
                 for (SampleResult sub : subs) {
                     totalRes.addSubResult(sub);
                 }
             } else {
                 // Only add sample if it is a sample of valid url redirect, i.e. that
                 // we have actually sampled the URL
                 if (!invalidRedirectUrl) {
                     totalRes.addSubResult(lastRes);
                 }
             }
 
             if (!lastRes.isRedirect()) {
                 break;
             }
         }
         if (redirect >= MAX_REDIRECTS) {
             lastRes = errorResult(new IOException("Exceeded maximum number of redirects: " + MAX_REDIRECTS), new HTTPSampleResult(lastRes));
             totalRes.addSubResult(lastRes);
         }
 
         // Now populate the any totalRes fields that need to
         // come from lastRes:
         totalRes.setSampleLabel(totalRes.getSampleLabel() + "->" + lastRes.getSampleLabel());
         // The following three can be discussed: should they be from the
         // first request or from the final one? I chose to do it this way
         // because that's what browsers do: they show the final URL of the
         // redirect chain in the location field.
         totalRes.setURL(lastRes.getURL());
         totalRes.setHTTPMethod(lastRes.getHTTPMethod());
         totalRes.setQueryString(lastRes.getQueryString());
         totalRes.setRequestHeaders(lastRes.getRequestHeaders());
 
         totalRes.setResponseData(lastRes.getResponseData());
         totalRes.setResponseCode(lastRes.getResponseCode());
         totalRes.setSuccessful(lastRes.isSuccessful());
         totalRes.setResponseMessage(lastRes.getResponseMessage());
         totalRes.setDataType(lastRes.getDataType());
         totalRes.setResponseHeaders(lastRes.getResponseHeaders());
         totalRes.setContentType(lastRes.getContentType());
         totalRes.setDataEncoding(lastRes.getDataEncodingNoDefault());
         return totalRes;
     }
 
     /**
      * See <a href="http://tools.ietf.org/html/rfc2616#section-10.3">RFC2616#section-10.3</a>
      * JMeter conforms currently to HttpClient 4.5.2 supported RFC
      * TODO Update when migrating to HttpClient 5.X
      * @param initialMethod the initial HTTP Method
      * @param responseCode String response code
      * @return the new HTTP Method as per RFC
      */
     private String computeMethodForRedirect(String initialMethod, String responseCode) {
         if (!HTTPConstants.HEAD.equalsIgnoreCase(initialMethod)) {
             return HTTPConstants.GET;
         }
         return initialMethod;
     }
 
     /**
      * Follow redirects and download page resources if appropriate. this works,
      * but the container stuff here is what's doing it. followRedirects() is
      * actually doing the work to make sure we have only one container to make
      * this work more naturally, I think this method - sample() - needs to take
      * an HTTPSamplerResult container parameter instead of a
      * boolean:areFollowingRedirect.
      *
      * @param areFollowingRedirect flag whether we are getting a redirect target
      * @param frameDepth Depth of this target in the frame structure. Used only to prevent infinite recursion.
      * @param res sample result to process
      * @return the sample result
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect, int frameDepth, HTTPSampleResult res) {
         boolean wasRedirected = false;
         if (!areFollowingRedirect && res.isRedirect()) {
             if(log.isDebugEnabled()) {
                 log.debug("Location set to - " + res.getRedirectLocation());
             }
 
             if (getFollowRedirects()) {
                 res = followRedirects(res, frameDepth);
                 areFollowingRedirect = true;
                 wasRedirected = true;
             }
         }
         
         if (res.isSuccessful() && SampleResult.TEXT.equals(res.getDataType()) && isImageParser() ) {
             if (frameDepth > MAX_FRAME_DEPTH) {
                 HTTPSampleResult errSubResult = new HTTPSampleResult(res);
                 errSubResult.removeSubResults();
                 res.addSubResult(errorResult(new Exception("Maximum frame/iframe nesting depth exceeded."), errSubResult));
             } else {
                 // Only download page resources if we were not redirected.
                 // If we were redirected, the page resources have already been
                 // downloaded for the sample made for the redirected url
                 // otherwise, use null so the container is created if necessary unless
                 // the flag is false, in which case revert to broken 2.1 behaviour
                 // Bug 51939 -  https://bz.apache.org/bugzilla/show_bug.cgi?id=51939
                 if (!wasRedirected) {
                     HTTPSampleResult container = (HTTPSampleResult) (
                             areFollowingRedirect ? res.getParent() : SEPARATE_CONTAINER ? null : res);
                     res = downloadPageResources(res, container, frameDepth);
                 }
             }
         }
         return res;
     }
 
     /**
      * Determine if the HTTP status code is successful or not
      * i.e. in range 200 to 399 inclusive
      *
      * @param code status code to check
      * @return whether in range 200-399 or not
      */
     protected boolean isSuccessCode(int code) {
         return code >= 200 && code <= 399;
     }
 
     protected static String encodeBackSlashes(String value) {
         StringBuilder newValue = new StringBuilder();
         for (int i = 0; i < value.length(); i++) {
             char charAt = value.charAt(i);
             if (charAt == '\\') { // $NON-NLS-1$
                 newValue.append("\\\\"); // $NON-NLS-1$
             } else {
                 newValue.append(charAt);
             }
         }
         return newValue.toString();
     }
 
     /*
      * Method to set files list to be uploaded.
      *
      * @param value
      *   HTTPFileArgs object that stores file list to be uploaded.
      */
     private void setHTTPFileArgs(HTTPFileArgs value) {
         if (value.getHTTPFileArgCount() > 0) {
             setProperty(new TestElementProperty(FILE_ARGS, value));
         } else {
             removeProperty(FILE_ARGS); // no point saving an empty list
         }
     }
 
     /*
      * Method to get files list to be uploaded.
      */
     private HTTPFileArgs getHTTPFileArgs() {
         return (HTTPFileArgs) getProperty(FILE_ARGS).getObjectValue();
     }
 
     /**
      * Get the collection of files as a list.
      * The list is built up from the filename/filefield/mimetype properties,
      * plus any additional entries saved in the FILE_ARGS property.
      *
      * If there are no valid file entries, then an empty list is returned.
      *
      * @return an array of file arguments (never null)
      */
     public HTTPFileArg[] getHTTPFiles() {
         final HTTPFileArgs fileArgs = getHTTPFileArgs();
         return fileArgs == null ? new HTTPFileArg[] {} : fileArgs.asArray();
     }
 
     public int getHTTPFileCount() {
         return getHTTPFiles().length;
     }
     /**
      * Saves the list of files.
      * The first file is saved in the Filename/field/mimetype properties.
      * Any additional files are saved in the FILE_ARGS array.
      *
      * @param files list of files to save
      */
     public void setHTTPFiles(HTTPFileArg[] files) {
         HTTPFileArgs fileArgs = new HTTPFileArgs();
         // Weed out the empty files
         if (files.length > 0) {
             for (HTTPFileArg file : files) {
                 if (file.isNotEmpty()) {
                     fileArgs.addHTTPFileArg(file);
                 }
             }
         }
         setHTTPFileArgs(fileArgs);
     }
 
     public static String[] getValidMethodsAsArray() {
         return METHODLIST.toArray(new String[METHODLIST.size()]);
     }
 
     public static boolean isSecure(String protocol) {
         return HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol);
     }
 
     public static boolean isSecure(URL url) {
         return isSecure(url.getProtocol());
     }
 
     // Implement these here, to avoid re-implementing for sub-classes
     // (previously these were implemented in all TestElements)
     @Override
     public void threadStarted() {
     }
 
     @Override
     public void threadFinished() {
     }
 
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         // NOOP to provide based empty impl and avoid breaking existing implementations
     }
 
     /**
      * Read response from the input stream, converting to MD5 digest if the useMD5 property is set.
      * <p>
      * For the MD5 case, the result byte count is set to the size of the original response.
      * <p>
      * Closes the inputStream
      *
      * @param sampleResult sample to store information about the response into
      * @param in input stream from which to read the response
      * @param length expected input length or zero
      * @return the response or the MD5 of the response
      * @throws IOException if reading the result fails
      */
-    public byte[] readResponse(SampleResult sampleResult, InputStream in, int length) throws IOException {
+    public byte[] readResponse(SampleResult sampleResult, InputStream in, long length) throws IOException {
         
         OutputStream w = null;
         try {
             byte[] readBuffer = new byte[8192]; // 8kB is the (max) size to have the latency ('the first packet')
             int bufferSize = 32;// Enough for MD5
 
             MessageDigest md = null;
             boolean knownResponseLength = length > 0;// may also happen if long value > int.max
             if (useMD5()) {
                 try {
                     md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
                 } catch (NoSuchAlgorithmException e) {
                     log.error("Should not happen - could not find MD5 digest", e);
                 }
             } else {
                 if (!knownResponseLength) {
                     bufferSize = 4 * 1024;
                 } else {
-                    bufferSize = length;
+                    bufferSize = (int) Math.min(MAX_BUFFER_SIZE, length);
                 }
             }
             
             
-            int bytesRead = 0;
-            int totalBytes = 0;
+            int bytesReadInBuffer = 0;
+            long totalBytes = 0;
             boolean first = true;
-            while ((bytesRead = in.read(readBuffer)) > -1) {
+            while ((bytesReadInBuffer = in.read(readBuffer)) > -1) {
                 if (first) {
                     sampleResult.latencyEnd();
                     first = false;
                     if(md == null) {
-                        if(knownResponseLength) {
-                            w = new DirectAccessByteArrayOutputStream(bufferSize);
+                        if(!knownResponseLength) {
+                            w = new org.apache.commons.io.output.ByteArrayOutputStream(bufferSize);
                         }
                         else {
-                            w = new org.apache.commons.io.output.ByteArrayOutputStream(bufferSize);
+                            w = new DirectAccessByteArrayOutputStream(bufferSize);
                         }
                     }
                 }
                 
                 if (md == null) {
-                    w.write(readBuffer, 0, bytesRead);
+                    if(totalBytes+bytesReadInBuffer<=MAX_BYTES_TO_STORE_PER_REQUEST) {
+                        w.write(readBuffer, 0, bytesReadInBuffer);
+                    } 
                 } else {
-                    md.update(readBuffer, 0, bytesRead);
-                    totalBytes += bytesRead;
+                    md.update(readBuffer, 0, bytesReadInBuffer);
                 }
+                totalBytes += bytesReadInBuffer;
             }
             
             if (first) { // Bug 46838 - if there was no data, still need to set latency
                 sampleResult.latencyEnd();
                 return new byte[0];
             }
             
-            if (md != null) {
+            if (md == null) {
+                return toByteArray(w);
+            } else {
                 byte[] md5Result = md.digest();
                 sampleResult.setBytes(totalBytes);
-                return JOrphanUtils.baToHexBytes(md5Result);
+                return JOrphanUtils.baToHexBytes(md5Result);                
             }
             
-            return toByteArray(w);
         } finally {
             IOUtils.closeQuietly(in);
             IOUtils.closeQuietly(w);
         }
     }
 
     /**
      * Optimized method to get byte array from {@link OutputStream}
      * @param w {@link OutputStream}
      * @return byte array
      */
     private byte[] toByteArray(OutputStream w) {
         if(w instanceof DirectAccessByteArrayOutputStream) {
             return ((DirectAccessByteArrayOutputStream) w).toByteArray();
         }
         
         if(w instanceof org.apache.commons.io.output.ByteArrayOutputStream) {
             return ((org.apache.commons.io.output.ByteArrayOutputStream) w).toByteArray();
         }
         
         log.warn("Unknown stream type " + w.getClass());
         
         return null;
     }
 
     /**
      * JMeter 2.3.1 and earlier only had fields for one file on the GUI:
      * <ul>
      *   <li>FILE_NAME</li>
      *   <li>FILE_FIELD</li>
      *   <li>MIMETYPE</li>
      * </ul>
      * These were stored in their own individual properties.
      * <p>
      * Version 2.3.3 introduced a list of files, each with their own path, name and mimetype.
      * <p>
      * In order to maintain backwards compatibility of test plans, the 3 original properties
      * were retained; additional file entries are stored in an HTTPFileArgs class.
      * The HTTPFileArgs class was only present if there is more than 1 file; this means that
      * such test plans are backward compatible.
      * <p>
      * Versions after 2.3.4 dispense with the original set of 3 properties.
      * Test plans that use them are converted to use a single HTTPFileArgs list.
      *
      * @see HTTPSamplerBaseConverter
      */
     void mergeFileProperties() {
         JMeterProperty fileName = getProperty(FILE_NAME);
         JMeterProperty paramName = getProperty(FILE_FIELD);
         JMeterProperty mimeType = getProperty(MIMETYPE);
         HTTPFileArg oldStyleFile = new HTTPFileArg(fileName, paramName, mimeType);
 
         HTTPFileArgs fileArgs = getHTTPFileArgs();
 
         HTTPFileArgs allFileArgs = new HTTPFileArgs();
         if (oldStyleFile.isNotEmpty()) { // OK, we have an old-style file definition
             allFileArgs.addHTTPFileArg(oldStyleFile); // save it
             // Now deal with any additional file arguments
             if (fileArgs != null) {
                 HTTPFileArg[] infiles = fileArgs.asArray();
                 for (HTTPFileArg infile : infiles) {
                     allFileArgs.addHTTPFileArg(infile);
                 }
             }
         } else {
             if (fileArgs != null) { // for new test plans that don't have FILE/PARAM/MIME properties
                 allFileArgs = fileArgs;
             }
         }
         // Updated the property lists
         setHTTPFileArgs(allFileArgs);
         removeProperty(FILE_FIELD);
         removeProperty(FILE_NAME);
         removeProperty(MIMETYPE);
     }
 
     /**
      * set IP source to use - does not apply to Java HTTP implementation currently
      *
      * @param value IP source to use
      */
     public void setIpSource(String value) {
         setProperty(IP_SOURCE, value, "");
     }
 
     /**
      * get IP source to use - does not apply to Java HTTP implementation currently
      *
      * @return IP source to use
      */
     public String getIpSource() {
         return getPropertyAsString(IP_SOURCE, "");
     }
 
     /**
      * set IP/address source type to use
      *
      * @param value type of the IP/address source
      */
     public void setIpSourceType(int value) {
         setProperty(IP_SOURCE_TYPE, value, SOURCE_TYPE_DEFAULT);
     }
 
     /**
      * get IP/address source type to use
      *
      * @return address source type
      */
     public int getIpSourceType() {
         return getPropertyAsInt(IP_SOURCE_TYPE, SOURCE_TYPE_DEFAULT);
     }
 
     /**
      * Return if used a concurrent thread pool to get embedded resources.
      *
      * @return true if used
      */
     public boolean isConcurrentDwn() {
         return getPropertyAsBoolean(CONCURRENT_DWN, false);
     }
 
     public void setConcurrentDwn(boolean concurrentDwn) {
         setProperty(CONCURRENT_DWN, concurrentDwn, false);
     }
 
     /**
      * Get the pool size for concurrent thread pool to get embedded resources.
      *
      * @return the pool size
      */
     public String getConcurrentPool() {
         return getPropertyAsString(CONCURRENT_POOL, CONCURRENT_POOL_DEFAULT);
     }
 
     public void setConcurrentPool(String poolSize) {
         setProperty(CONCURRENT_POOL, poolSize, CONCURRENT_POOL_DEFAULT);
     }
 
 
     /**
      * Callable class to sample asynchronously resources embedded
      *
      */
     private static class ASyncSample implements Callable<AsynSamplerResultHolder> {
         private final URL url;
         private final String method;
         private final boolean areFollowingRedirect;
         private final int depth;
         private final HTTPSamplerBase sampler;
         private final JMeterContext jmeterContextOfParentThread;
 
         ASyncSample(URL url, String method,
                 boolean areFollowingRedirect, int depth,  CookieManager cookieManager, HTTPSamplerBase base) {
             this.url = url;
             this.method = method;
             this.areFollowingRedirect = areFollowingRedirect;
             this.depth = depth;
             this.sampler = (HTTPSamplerBase) base.clone();
             // We don't want to use CacheManager clone but the parent one, and CacheManager is Thread Safe
             CacheManager cacheManager = base.getCacheManager();
             if (cacheManager != null) {
                 this.sampler.setCacheManagerProperty(cacheManager.createCacheManagerProxy());
             }
 
             if (cookieManager != null) {
                 CookieManager clonedCookieManager = (CookieManager) cookieManager.clone();
                 this.sampler.setCookieManagerProperty(clonedCookieManager);
             }
             this.sampler.setMD5(this.sampler.useMD5() || IGNORE_EMBEDDED_RESOURCES_DATA);
             this.jmeterContextOfParentThread = JMeterContextService.getContext();
         }
 
         @Override
         public AsynSamplerResultHolder call() {
             JMeterContextService.replaceContext(jmeterContextOfParentThread);
             HTTPSampleResult httpSampleResult = sampler.sample(url, method, areFollowingRedirect, depth);
             if (sampler.getCookieManager() != null) {
                 CollectionProperty cookies = sampler.getCookieManager().getCookies();
                 return new AsynSamplerResultHolder(httpSampleResult, cookies);
             } else {
                 return new AsynSamplerResultHolder(httpSampleResult, new CollectionProperty());
             }
         }
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
diff --git a/test/src/org/apache/jmeter/samplers/TestSampleResult.java b/test/src/org/apache/jmeter/samplers/TestSampleResult.java
index 4f4228004..b58244897 100644
--- a/test/src/org/apache/jmeter/samplers/TestSampleResult.java
+++ b/test/src/org/apache/jmeter/samplers/TestSampleResult.java
@@ -1,347 +1,347 @@
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
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 import java.io.StringWriter;
 
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.util.Calculator;
 import org.apache.log.LogTarget;
 import org.apache.log.format.Formatter;
 import org.apache.log.format.RawFormatter;
 import org.apache.log.output.io.WriterTarget;
 import org.junit.Test;
 
 // TODO need more tests - particularly for the new functions
 
 public class TestSampleResult {
 
         @Test
         public void testElapsedTrue() throws Exception {
             SampleResult res = new SampleResult(true);
 
             // Check sample increments OK
             res.sampleStart();
             Thread.sleep(110); // Needs to be greater than the minimum to allow for boundary errors
             res.sampleEnd();
             long time = res.getTime();
             if(time < 100){
                 fail("Sample time should be >=100, actual "+time);
             }
         }
 
         @Test
         public void testElapsedFalse() throws Exception {
             SampleResult res = new SampleResult(false);
 
             // Check sample increments OK
             res.sampleStart();
             Thread.sleep(110); // Needs to be greater than the minimum to allow for boundary errors
             res.sampleEnd();
             long time = res.getTime();
             if(time < 100){
                 fail("Sample time should be >=100, actual "+time);
             }
         }
 
         @Test
         public void testPauseFalse() throws Exception {
             SampleResult res = new SampleResult(false);
             // Check sample increments OK
             res.sampleStart();
             Thread.sleep(100);
             res.samplePause();
 
             Thread.sleep(200);
 
             // Re-increment
             res.sampleResume();
             Thread.sleep(100);
             res.sampleEnd();
             long sampleTime = res.getTime();
             if ((sampleTime < 180) || (sampleTime > 290)) {
                 fail("Accumulated time (" + sampleTime + ") was not between 180 and 290 ms");
             }
         }
 
         @Test
         public void testPauseTrue() throws Exception {
             SampleResult res = new SampleResult(true);
             // Check sample increments OK
             res.sampleStart();
             Thread.sleep(100);
             res.samplePause();
 
             Thread.sleep(200);
 
             // Re-increment
             res.sampleResume();
             Thread.sleep(100);
             res.sampleEnd();
             long sampleTime = res.getTime();
             if ((sampleTime < 180) || (sampleTime > 290)) {
                 fail("Accumulated time (" + sampleTime + ") was not between 180 and 290 ms");
             }
         }
 
         private static final Formatter fmt = new RawFormatter();
 
         private StringWriter wr = null;
 
         private void divertLog() {// N.B. This needs to divert the log for SampleResult
             wr = new StringWriter(1000);
             LogTarget[] lt = { new WriterTarget(wr, fmt) };
             SampleResult.log.setLogTargets(lt);
         }
 
         @Test
         public void testPause2True() throws Exception {
             divertLog();
             SampleResult res = new SampleResult(true);
             res.sampleStart();
             res.samplePause();
             assertEquals(0, wr.toString().length());
             res.samplePause();
             assertFalse(wr.toString().length() == 0);
         }
 
         @Test
         public void testPause2False() throws Exception {
             divertLog();
             SampleResult res = new SampleResult(false);
             res.sampleStart();
             res.samplePause();
             assertEquals(0, wr.toString().length());
             res.samplePause();
             assertFalse(wr.toString().length() == 0);
         }
         
         @Test
         public void testByteCount() throws Exception {
             SampleResult res = new SampleResult();
             
             res.sampleStart();
             res.setBytes(100);
             res.setSampleLabel("sample of size 100 bytes");
             res.sampleEnd();
-            assertEquals(100, res.getBytes());
+            assertEquals(100, res.getBytesAsLong());
             assertEquals("sample of size 100 bytes", res.getSampleLabel());
         }
 
         @Test
         public void testSubResultsTrue() throws Exception {
             testSubResults(true, 0);
         }
 
         @Test
         public void testSubResultsTrueThread() throws Exception {
             testSubResults(true, 500L, 0);
         }
 
         @Test
         public void testSubResultsFalse() throws Exception {
             testSubResults(false, 0);
         }
 
         @Test
         public void testSubResultsFalseThread() throws Exception {
             testSubResults(false, 500L, 0);
         }
 
         @Test
         public void testSubResultsTruePause() throws Exception {
             testSubResults(true, 100);
         }
 
         @Test
         public void testSubResultsTruePauseThread() throws Exception {
             testSubResults(true, 500L, 100);
         }
 
         @Test
         public void testSubResultsFalsePause() throws Exception {
             testSubResults(false, 100);
         }
 
         @Test
         public void testSubResultsFalsePauseThread() throws Exception {
             testSubResults(false, 500L, 100);
         }
 
         // temp test case for exploring settings
         public void xtestUntilFail() throws Exception {
             while(true) {
                 testSubResultsTruePause();
                 testSubResultsFalsePause();
             }
         }
 
         private void testSubResults(boolean nanoTime, long pause) throws Exception {
             testSubResults(nanoTime, 0L, pause); // Don't use nanoThread
         }
 
         private void testSubResults(boolean nanoTime, long nanoThreadSleep, long pause) throws Exception {
             // This test tries to emulate a http sample, with two
             // subsamples, representing images that are downloaded for the
             // page representing the first sample.
 
             // Sample that will get two sub results, simulates a web page load 
             SampleResult parent = new SampleResult(nanoTime, nanoThreadSleep);            
 
             JMeterTestCase.assertPrimitiveEquals(nanoTime, parent.useNanoTime);
             assertEquals(nanoThreadSleep, parent.nanoThreadSleep);
 
             long beginTest = parent.currentTimeInMillis();
 
             parent.sampleStart();
             Thread.sleep(100);
             parent.setBytes(300);
             parent.setSampleLabel("Parent Sample");
             parent.setSuccessful(true);
             parent.sampleEnd();
             long parentElapsed = parent.getTime();
 
             // Sample with no sub results, simulates an image download
             SampleResult child1 = new SampleResult(nanoTime);            
             child1.sampleStart();
             Thread.sleep(100);
             child1.setBytes(100);
             child1.setSampleLabel("Child1 Sample");
             child1.setSuccessful(true);
             child1.sampleEnd();
             long child1Elapsed = child1.getTime();
 
             assertTrue(child1.isSuccessful());
-            assertEquals(100, child1.getBytes());
+            assertEquals(100, child1.getBytesAsLong());
             assertEquals("Child1 Sample", child1.getSampleLabel());
             assertEquals(1, child1.getSampleCount());
             assertEquals(0, child1.getSubResults().length);
 
             long actualPause = 0;
             if (pause > 0) {
                 long t1 = parent.currentTimeInMillis();
                 Thread.sleep(pause);
                 actualPause = parent.currentTimeInMillis() - t1;
             }
 
             // Sample with no sub results, simulates an image download 
             SampleResult child2 = new SampleResult(nanoTime);            
             child2.sampleStart();
             Thread.sleep(100);
             child2.setBytes(200);
             child2.setSampleLabel("Child2 Sample");
             child2.setSuccessful(true);
             child2.sampleEnd();
             long child2Elapsed = child2.getTime();
 
             assertTrue(child2.isSuccessful());
-            assertEquals(200, child2.getBytes());
+            assertEquals(200, child2.getBytesAsLong());
             assertEquals("Child2 Sample", child2.getSampleLabel());
             assertEquals(1, child2.getSampleCount());
             assertEquals(0, child2.getSubResults().length);
             
             // Now add the subsamples to the sample
             parent.addSubResult(child1);
             parent.addSubResult(child2);
             assertTrue(parent.isSuccessful());
-            assertEquals(600, parent.getBytes());
+            assertEquals(600, parent.getBytesAsLong());
             assertEquals("Parent Sample", parent.getSampleLabel());
             assertEquals(1, parent.getSampleCount());
             assertEquals(2, parent.getSubResults().length);
             long parentElapsedTotal = parent.getTime();
             
             long overallTime = parent.currentTimeInMillis() - beginTest;
 
             long sumSamplesTimes = parentElapsed + child1Elapsed + actualPause + child2Elapsed;
             
             /*
              * Parent elapsed total should be no smaller than the sum of the individual samples.
              * It may be greater by the timer granularity.
              */
             
             long diff = parentElapsedTotal - sumSamplesTimes;
             long maxDiff = nanoTime ? 3 : 16; // TimeMillis has granularity of 10-20
             if (diff < 0 || diff > maxDiff) {
                 fail("ParentElapsed: " + parentElapsedTotal + " - " + " sum(samples): " + sumSamplesTimes
                         + " = " + diff + " not in [0," + maxDiff + "]; nanotime=" + nanoTime);
             }
 
             /**
              * The overall time to run the test must be no less than, 
              * and may be greater (but not much greater) than the parent elapsed time
              */
             
             diff = overallTime - parentElapsedTotal;
             if (diff < 0 || diff > maxDiff) {
                 fail("TestElapsed: " + overallTime + " - " + " ParentElapsed: " + parentElapsedTotal
                         + " = " + diff + " not in [0," + maxDiff + "]; nanotime="+nanoTime);
             }
             
             // Check that calculator gets the correct statistics from the sample
             Calculator calculator = new Calculator();
             calculator.addSample(parent);
             assertEquals(600, calculator.getTotalBytes());
             assertEquals(1, calculator.getCount());
             assertEquals(1d / (parentElapsedTotal / 1000d), calculator.getRate(),0.0001d); // Allow for some margin of error
             // Check that the throughput uses the time elapsed for the sub results
             assertFalse(1d / (parentElapsed / 1000d) <= calculator.getRate());
         }
 
         // TODO some more invalid sequence tests needed
         
         @Test
         public void testEncodingAndType() throws Exception {
             // check default
             SampleResult res = new SampleResult();
             assertEquals(SampleResult.DEFAULT_ENCODING,res.getDataEncodingWithDefault());
             assertEquals("DataType should be blank","",res.getDataType());
             assertNull(res.getDataEncodingNoDefault());
             
             // check null changes nothing
             res.setEncodingAndType(null);
             assertEquals(SampleResult.DEFAULT_ENCODING,res.getDataEncodingWithDefault());
             assertEquals("DataType should be blank","",res.getDataType());
             assertNull(res.getDataEncodingNoDefault());
 
             // check no charset
             res.setEncodingAndType("text/html");
             assertEquals(SampleResult.DEFAULT_ENCODING,res.getDataEncodingWithDefault());
             assertEquals("text",res.getDataType());
             assertNull(res.getDataEncodingNoDefault());
 
             // Check unquoted charset
             res.setEncodingAndType("text/html; charset=aBcd");
             assertEquals("aBcd",res.getDataEncodingWithDefault());
             assertEquals("aBcd",res.getDataEncodingNoDefault());
             assertEquals("text",res.getDataType());
 
             // Check quoted charset
             res.setEncodingAndType("text/html; charset=\"aBCd\"");
             assertEquals("aBCd",res.getDataEncodingWithDefault());
             assertEquals("aBCd",res.getDataEncodingNoDefault());
             assertEquals("text",res.getDataType());         
         }
 }
 
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index b198e8577..6d9f0b3e1 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,368 +1,376 @@
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
 
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>A cache for CSS Parsing of URLs has been introduced in this version, it is enabled by default. It is controlled by property <code>css.parser.cache.size</code>. It can be disabled by setting its value to <code>0</code>. See <bugzilla>59885</bugzilla></li>
     <li>ThroughputController defaults have changed. Now defaults are Percent Executions which is global and no more per user. See <bugzilla>60023</bugzilla></li>
     <li>Since version 3.1, HTML report ignores empty <code>Transaction Controller</code> (possibly generated by <code>If Controller</code> or <code>Throughput Controller</code>) when computing metrics. This provides more accurate metrics</li>
     <li>Since version 3.1, Summariser ignores SampleResults generated by <code>Transaction Controller</code> when computing the live statistics, see <bugzilla>60109</bugzilla></li>
     <li>Since version 3.1, when using Stripped modes (by default <code>StrippedBatch</code> is used), response will be stripped also for failing SampleResults, you can revert this to previous behaviour by setting <code>sample_sender_strip_also_on_error=false</code> in <code>user.properties</code>, see <bugzilla>60137</bugzilla></li>
     <li>Since version 3.1, <code>jmeter.save.saveservice.connect_time</code> property value is <code>true</code>, meaning CSV file for results will contain an additional column containing connection time, see <bugzilla>60106</bugzilla></li>
     <li>Since version 3.1, Random Timer subclasses (Gaussian Random Timer, Uniform Random Timer and Poisson Random Timer) implement interface <code><a href="./api/org/apache/jmeter/timers/ModifiableTimer.html">org.apache.jmeter.timers.ModifiableTimer</a></code></li>
     <li>Since version 3.1, if you don't select any language in JSR223 Test Elements, Apache Groovy language will be used. See <bugzilla>59945</bugzilla></li>
     <li>Since version 3.1, CSV DataSet now trims variable names to avoid issues due to spaces between variables names when configuring CSV DataSet. This should not have any impact for you unless you use space at the begining or end of your variable names. See <bugzilla>60221</bugzilla></li>
+    <li>Since version 3.1, HTTP Request is able when using HttpClient4 (default) implementation to handle responses bigger than <code>2147483647</code>. To allow this 2 properties have been introduced:
+    <ul>
+        <li><code>httpsampler.max_bytes_to_store_per_request</code> (defaults to 10MB)will control what is held in memory, by default JMeter will only keep in memory the first 10MB of a response. If you have responses larger than this value and use assertions that are after the first 10MB, then you must increase this value</li>
+        <li><code>httpsampler.max_buffer_size</code> will control the buffer used to read the data. Previously JMeter used a buffer equal to Content-Length header which could lead to failures and make JMeter less resistant to faulty applications, but note this may impact response times and give slightly different results
+         than previous versions if your application returned a Content-Length header higher than current default value (65KB) </li>
+    </ul>
+    See <bugzilla>53039</bugzilla></li>
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
     <li><bug>60229</bug>Add a new metric : sent_bytes. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
+    <li><bug>53039</bug>HTTP Request : Be able to handle responses which size exceeds <code>2147483647</code> bytes</li>
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
     <li><bug>59963</bug>New function <code>__RandomFromMultipleVars</code>: Ability to compute a random value from values of one or more variables. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
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
