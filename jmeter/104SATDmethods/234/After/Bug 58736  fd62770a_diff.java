diff --git a/bin/jmeter.properties b/bin/jmeter.properties
index 0da25104e..06d2250d1 100644
--- a/bin/jmeter.properties
+++ b/bin/jmeter.properties
@@ -33,1254 +33,1249 @@
 
 
 # Additional locale(s) to add to the displayed list.
 # The current default list is: en, fr, de, no, es, tr, ja, zh_CN, zh_TW, pl, pt_BR
 # [see JMeterMenuBar#makeLanguageMenu()]
 # The entries are a comma-separated list of language names
 #locales.add=zu
 
 # Netscape HTTP Cookie file
 cookies=cookies
 
 #---------------------------------------------------------------------------
 # File format configuration for JMX and JTL files
 #---------------------------------------------------------------------------
 
 # Properties:
 # file_format          - affects both JMX and JTL files
 # file_format.testplan - affects JMX files only
 # file_format.testlog  - affects JTL files only
 #
 # Possible values are:
 # 2.1 - initial format using XStream
 # 2.2 - updated format using XStream, with shorter names
 
 # N.B. format 2.0 (Avalon) is no longer supported
 
 #---------------------------------------------------------------------------
 # XML Parser
 #---------------------------------------------------------------------------
 
 # XML Reader(Parser) - Must implement SAX 2 specs
 xml.parser=org.apache.xerces.parsers.SAXParser
 
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
 # The original behaviour can be enabled by setting the JMeter property:
 #https.sessioncontext.shared=true
 
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
 # You can either use a full class name, as shown above,
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
 
 # Error/Fatal Log count display
 # defaults to true
 #jmeter.errorscounter.display=true
 
 # Max characters kept in LoggerPanel, default to 80000 chars
 # O means no limit
 #jmeter.loggerpanel.maxlength=80000
 
 # Toolbar display
 # default:
 #jmeter.toolbar.display=true
 # Toolbar icon definitions
 #jmeter.toolbar.icons=org/apache/jmeter/images/toolbar/icons-toolbar.properties
 # Toolbar list
 #jmeter.toolbar=new,open,close,save,save_as_testplan,|,cut,copy,paste,|,expand,collapse,toggle,|,test_start,test_stop,test_shutdown,|,test_start_remote_all,test_stop_remote_all,test_shutdown_remote_all,|,test_clear,test_clear_all,|,search,search_reset,|,function_helper,help
 # Toolbar icons default size: 22x22. Available sizes are: 22x22, 32x32, 48x48
 #jmeter.toolbar.icons.size=22x22
 
 # Icon definitions
 # default:
 #jmeter.icons=org/apache/jmeter/images/icon.properties
 # alternate:
 #jmeter.icons=org/apache/jmeter/images/icon_1.properties
 
 #Components to not display in JMeter GUI (GUI class name or static label)
 # These elements are deprecated: HTML Parameter Mask,HTTP User Parameter Modifier, Webservice (SOAP) Request
 not_in_menu=org.apache.jmeter.protocol.http.modifier.gui.ParamModifierGui, HTTP User Parameter Modifier, org.apache.jmeter.protocol.http.control.gui.WebServiceSamplerGui
 
 # Number of items in undo history
 # Feature is disabled by default (0)
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
 #http.java.sampler.retries=10
 # 0 now means don't retry connection (in 2.3 and before it meant no tries at all!)
 
 #---------------------------------------------------------------------------
 # Commons HTTPClient configuration
 #---------------------------------------------------------------------------
 
 # define a properties file for overriding Commons HttpClient parameters
 # See: http://hc.apache.org/httpclient-3.x/preference-api.html
 # Uncomment this line if you put anything in httpclient.parameters file
 #httpclient.parameters.file=httpclient.parameters
 
 
 # define a properties file for overriding Apache HttpClient parameters
 # See: TBA
 # Uncomment this line if you put anything in hc.parameters file
 #hc.parameters.file=hc.parameters
 
 # Following properties apply to both Commons and Apache HttpClient
 
 # set the socket timeout (or use the parameter http.socket.timeout) 
 # for AJP Sampler and HttpClient3 implementation.
 # Note for HttpClient3 implementation it is better to use GUI to set timeout 
 # or use http.socket.timeout in httpclient.parameters
 # Value is in milliseconds
 #httpclient.timeout=0
 # 0 == no timeout
 
 # Set the http version (defaults to 1.1)
 #httpclient.version=1.0 (or use the parameter http.protocol.version)
 
 # Define characters per second > 0 to emulate slow connections
 #httpclient.socket.http.cps=0
 #httpclient.socket.https.cps=0
 
 #Enable loopback protocol
 #httpclient.loopback=true
 
 # Define the local host address to be used for multi-homed hosts
 #httpclient.localaddress=1.2.3.4
 
 # AuthManager Kerberos configuration
 # Name of application module used in jaas.conf
 #kerberos_jaas_application=JMeter  
 
 # Should ports be stripped from urls before constructing SPNs
 # for spnego authentication
 #kerberos.spnego.strip_port=true
 
 #         Sample logging levels for Commons HttpClient
 #
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
 
 
 #         Apache Commons HttpClient logging examples
 #
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
 
 # Number of retries to attempt (default 0)
 #httpclient4.retrycount=0
 
 # Idle connection timeout (ms) to apply if the server does not send
 # Keep-Alive headers (default 0 = no Keep-Alive)
 #httpclient4.idletimeout=0
 # Note: this is currently an experimental fix
 
 #---------------------------------------------------------------------------
 # Apache HttpComponents HTTPClient configuration (HTTPClient 3.1)
 #---------------------------------------------------------------------------
 
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
 #jmeter.save.saveservice.assertion_results_failure_message=false
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
 #jmeter.save.saveservice.connect_time=false
 #jmeter.save.saveservice.samplerData=false
 #jmeter.save.saveservice.responseHeaders=false
 #jmeter.save.saveservice.requestHeaders=false
 #jmeter.save.saveservice.encoding=false
 #jmeter.save.saveservice.bytes=true
 #jmeter.save.saveservice.url=false
 #jmeter.save.saveservice.filename=false
 #jmeter.save.saveservice.hostname=false
 #jmeter.save.saveservice.thread_counts=true
 #jmeter.save.saveservice.sample_count=false
 #jmeter.save.saveservice.idle_time=false
 
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
 #jmeter.save.saveservice.print_field_names=false
 
 # Optional list of JMeter variable names whose values are to be saved in the result data files.
 # Use commas to separate the names. For example:
 #sample_variables=SESSION_ID,REFERENCE
 # N.B. The current implementation saves the values in XML as attributes,
 # so the names must be valid XML names.
 # Versions of JMeter after 2.3.2 send the variable to all servers
 # to ensure that the correct data is available at the client.
 
 # Optional xml processing instruction for line 2 of the file:
 #jmeter.save.saveservice.xml_pi=<?xml-stylesheet type="text/xsl" href="../extras/jmeter-results-detail-report_21.xsl"?>
 
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
 HTTPResponse.parsers=htmlParser wmlParser
 # for each parser, there should be a parser.types and a parser.className property
 
 #---------------------------------------------------------------------------
 # HTML Parser configuration
 #---------------------------------------------------------------------------
 
 # Define the HTML parser to be used.
 # Default parser:
 # This new parser (since 2.10) should perform better than all others
 # see https://bz.apache.org/bugzilla/show_bug.cgi?id=55632
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser
 
 # Other parsers:
 # Default parser before 2.10
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.HtmlParserHTMLParser
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
 
 # Max number of PreparedStatements per Connection for PreparedStatement cache
 #jdbcsampler.maxopenpreparedstatements=100
 
 # String used to indicate a null value
 #jdbcsampler.nullmarker=]NULL[
 
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
 # Define the following property to automatically start a summariser with that name
 # (applies to non-GUI mode only)
 #summariser.name=summary
 #
 # interval between summaries (in seconds) default 30 seconds
 #summariser.interval=30
 #
 # Write messages to log file
 #summariser.log=true
 #
 # Write messages to System.out
 #summariser.out=true
 
 
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
 # Aggregate Report and Aggregate Graph - configuration
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
 # to guarantee a stable ordering (if more results then this limit are retruned
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
 
 # Maximum redirects to follow in a single sequence (default 5)
 #httpsampler.max_redirects=5
 # Maximum frame/iframe nesting depth (default 5)
 #httpsampler.max_frame_depth=5
 # Maximum await termination timeout (secs) when concurrent download embedded resources (default 60)
 #httpsampler.await_termination_timeout=60
 # Revert to BUG 51939 behaviour (no separate container for embedded resources) by setting the following false:
 #httpsampler.separate.container=true
 
 # If embedded resources download fails due to missing resources or other reasons, if this property is true
 # Parent sample will not be marked as failed 
 #httpsampler.ignore_failed_embedded_resources=false
 
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
 
 # Ability to switch to Nashorn as default Javascript Engine used by IfController and __javaScript function
 # JMeter works as following:
 # - JDK < 8 : Rhino
 # - JDK >= 8 and javascript.use_rhino=false: Nashorn
 # If you want to use Nashorn on JDK8, set this property to false
 #javascript.use_rhino=true
 
-# (2.14) JMeterThread behaviour has changed: it now implements sampleStarted/sampleStopped
-# This is necessary for the test element Sample Timeout
-# Uncomment the following line to revert to the original behaviour (Sample Timeout will no longer work)
-#JMeterThread.sampleStarted=false
-
 # Number of milliseconds to wait for a thread to stop
 #jmeterengine.threadstop.wait=5000
 
 #Whether to invoke System.exit(0) in server exit code after stopping RMI
 #jmeterengine.remote.system.exit=false
 
 # Whether to call System.exit(1) on failure to stop threads in non-GUI mode.
 # This only takes effect if the test was explictly requested to stop.
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
 
 # Set this to false to disable the use of JSyntaxTextArea for the Console Logger panel 
 #loggerpanel.usejsyntaxtext=true
 
 # Maximum size of HTML page that can be displayed; default=200 * 1024
 # Set to 0 to disable the size check and display the whole response
 #view.results.tree.max_size=204800
 
 # Order of Renderers in View Results Tree
 # Note full class names should be used for non jmeter core renderers
 # For JMeter core renderers, class names start with . and are automatically
 # prefixed with org.apache.jmeter.visualizers
 view.results.tree.renderers_order=.RenderAsText,.RenderAsRegexp,.RenderAsCssJQuery,.RenderAsXPath,.RenderAsHTML,.RenderAsHTMLWithEmbedded,.RenderAsDocument,.RenderAsJSON,.RenderAsXML
 
 # Maximum size of Document that can be parsed by Tika engine; defaut=10 * 1024 * 1024 (10MB)
 # Set to 0 to disable the size check
 #document.max_size=0
 
 #JMS options
 # Enable the following property to stop JMS Point-to-Point Sampler from using
 # the properties java.naming.security.[principal|credentials] when creating the queue connection
 #JMSSampler.useSecurity.properties=false
 
 # Set the following value to true in order to skip the delete confirmation dialogue
 #confirm.delete.skip=false
 
 # Used by Webservice Sampler (SOAP)
 # Size of Document Cache
 #soap.document_cache=50
 
 # Used by JSR223 elements
 # Size of compiled scripts cache
 #jsr223.compiled_scripts_cache_size=100
 
 #---------------------------------------------------------------------------
 # Classpath configuration
 #---------------------------------------------------------------------------
 
 # List of paths (separated by ;) to search for additional JMeter plugin classes,
 # for example new GUI elements and samplers.
 # A path item can either be a jar file or a directory.
 # Any jar file in such a directory will be automatically included,
 # jar files in sub directories are ignored.
 # The given value is in addition to any jars found in the lib/ext directory.
 # Do not use this for utility or plugin dependency jars.
 #search_paths=/app1/lib;/app2/lib
 
 # List of paths that JMeter will search for utility and plugin dependency classes.
 # Use your platform path separator to separate multiple paths.
 # A path item can either be a jar file or a directory.
 # Any jar file in such a directory will be automatically included,
 # jar files in sub directories are ignored.
 # The given value is in addition to any jars found in the lib directory.
 # All entries will be added to the class path of the system class loader
 # and also to the path of the JMeter internal loader.
 # Paths with spaces may cause problems for the JVM
 #user.classpath=../classes;../lib;../app1/jar1.jar;../app2/jar2.jar
 
 # List of paths (separated by ;) that JMeter will search for utility
 # and plugin dependency classes.
 # A path item can either be a jar file or a directory.
 # Any jar file in such a directory will be automatically included,
 # jar files in sub directories are ignored.
 # The given value is in addition to any jars found in the lib directory
 # or given by the user.classpath property.
 # All entries will be added to the path of the JMeter internal loader only.
 # For plugin dependencies using plugin_dependency_paths should be preferred over
 # user.classpath.
 #plugin_dependency_paths=../dependencies/lib;../app1/jar1.jar;../app2/jar2.jar
 
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
 # Reporting configuration
 #---------------------------------------------------------------------------
 
 # Sets the satisfaction threshold for the APDEX calculation (in milliseconds).
 #jmeter.reportgenerator.apdex_statisfied_threshold=500
 
 # Sets the tolerance threshold for the APDEX calculation (in milliseconds).
 #jmeter.reportgenerator.apdex_tolerated_threshold=1500
 
 # Sets the filter for samples to keep for graphs and statistics generation.
 # A comma separated list of samples names, empty string means no filtering
 #jmeter.reportgenerator.sample_filter=
 
 # Sets the temporary directory used by the generation processus if it needs file I/O operations.
 #jmeter.reportgenerator.temp_dir=temp
 
 # Sets the size of the sliding window used by percentile evaluation.
 # Caution : higher value provides a better accurency but needs more memory.
 #jmeter.reportgenerator.statistic_window = 200000
 
 # Defines the overall granularity for over time graphs
 jmeter.reportgenerator.overall_granularity=60000
 
 # Response Time Percentiles graph definition
 jmeter.reportgenerator.graph.responseTimePercentiles.classname=org.apache.jmeter.report.processor.graph.impl.ResponseTimePercentilesGraphConsumer
 jmeter.reportgenerator.graph.responseTimePercentiles.title=Response Time Percentiles
 
 # Response Time Distribution graph definition
 jmeter.reportgenerator.graph.responseTimeDistribution.classname=org.apache.jmeter.report.processor.graph.impl.ResponseTimeDistributionGraphConsumer
 jmeter.reportgenerator.graph.responseTimeDistribution.title=Response Time Distribution
 jmeter.reportgenerator.graph.responseTimeDistribution.property.set_granularity=1000
 
 # Active Threads Over Time graph definition
 jmeter.reportgenerator.graph.activeThreadsOverTime.classname=org.apache.jmeter.report.processor.graph.impl.ActiveThreadsGraphConsumer
 jmeter.reportgenerator.graph.activeThreadsOverTime.title=Active Threads Over Time
 jmeter.reportgenerator.graph.activeThreadsOverTime.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Response Time Per Sample graph definition
 jmeter.reportgenerator.graph.responseTimePerSample.classname=org.apache.jmeter.report.processor.graph.impl.ResponseTimePerSampleGraphConsumer
 jmeter.reportgenerator.graph.responseTimePerSample.title=Response Time Per Sample
 
 # Time VS Threads graph definition
 jmeter.reportgenerator.graph.timeVsThreads.classname=org.apache.jmeter.report.processor.graph.impl.TimeVSThreadGraphConsumer
 jmeter.reportgenerator.graph.timeVsThreads.title=Time VS Threads
 
 # Bytes Throughput Over Time graph definition
 jmeter.reportgenerator.graph.bytesThroughputOverTime.classname=org.apache.jmeter.report.processor.graph.impl.BytesThroughputGraphConsumer
 jmeter.reportgenerator.graph.bytesThroughputOverTime.title=Bytes Throughput Over Time
 jmeter.reportgenerator.graph.bytesThroughputOverTime.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Response Time Over Time graph definition
 jmeter.reportgenerator.graph.responseTimesOverTime.classname=org.apache.jmeter.report.processor.graph.impl.ResponseTimeOverTimeGraphConsumer
 jmeter.reportgenerator.graph.responseTimesOverTime.title=Response Time Over Time
 jmeter.reportgenerator.graph.responseTimesOverTime.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Latencies Over Time graph definition
 jmeter.reportgenerator.graph.latenciesOverTime.classname=org.apache.jmeter.report.processor.graph.impl.LatencyOverTimeGraphConsumer
 jmeter.reportgenerator.graph.latenciesOverTime.title=Latencies Over Time
 jmeter.reportgenerator.graph.latenciesOverTime.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Response Time Vs Request graph definition
 jmeter.reportgenerator.graph.responseTimeVsRequest.classname=org.apache.jmeter.report.processor.graph.impl.ResponseTimeVSRequestGraphConsumer
 jmeter.reportgenerator.graph.responseTimeVsRequest.title=Response Time Vs Request
 jmeter.reportgenerator.graph.responseTimeVsRequest.exclude_controllers=true
 jmeter.reportgenerator.graph.responseTimeVsRequest.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Latencies Vs Request graph definition
 jmeter.reportgenerator.graph.latencyVsRequest.classname=org.apache.jmeter.report.processor.graph.impl.LatencyVSRequestGraphConsumer
 jmeter.reportgenerator.graph.latencyVsRequest.title=Latencies Vs Request
 jmeter.reportgenerator.graph.latencyVsRequest.exclude_controllers=true
 jmeter.reportgenerator.graph.latencyVsRequest.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Hits Per Second graph definition
 jmeter.reportgenerator.graph.hitsPerSecond.classname=org.apache.jmeter.report.processor.graph.impl.HitsPerSecondGraphConsumer
 jmeter.reportgenerator.graph.hitsPerSecond.title=Hits Per Second
 jmeter.reportgenerator.graph.hitsPerSecond.exclude_controllers=true
 jmeter.reportgenerator.graph.hitsPerSecond.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Codes Per Second graph definition
 jmeter.reportgenerator.graph.codesPerSecond.classname=org.apache.jmeter.report.processor.graph.impl.CodesPerSecondGraphConsumer
 jmeter.reportgenerator.graph.codesPerSecond.title=Codes Per Second
 jmeter.reportgenerator.graph.codesPerSecond.exclude_controllers=true
 jmeter.reportgenerator.graph.codesPerSecond.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Transactions Per Second graph definition
 jmeter.reportgenerator.graph.transactionsPerSecond.classname=org.apache.jmeter.report.processor.graph.impl.TransactionsPerSecondGraphConsumer
 jmeter.reportgenerator.graph.transactionsPerSecond.title=Transactions Per Second
 jmeter.reportgenerator.graph.transactionsPerSecond.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # HTML Export
 jmeter.reportgenerator.exporter.html.classname=org.apache.jmeter.report.dashboard.HtmlTemplateExporter
 
 # Sets the source directory of templated files from which the html pages are generated.
 #jmeter.reportgenerator.exporter.html.property.template_dir=report-template
 
 # Sets the destination directory for generated html pages.
 #jmeter.reportgenerator.exporter.html.property.output_dir=report-output
 
 # Indicates which graph series are filtered (regular expression)
 #jmeter.reportgenerator.exporter.html.series_filter=
 
 # Indicates whether series filter apply only on sample series
 #jmeter.reportgenerator.exporter.html.filters_only_sample_series=false
 
 # Indicates whether only controller samples are displayed on graphs that support it.
 #jmeter.reportgenerator.exporter.html.show_controllers_only=false
 
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
 # Path must be relative to jmeter root folder
 #template.files=/bin/templates/templates.xml
diff --git a/src/core/org/apache/jmeter/threads/JMeterThread.java b/src/core/org/apache/jmeter/threads/JMeterThread.java
index b0c883be6..9816831f1 100644
--- a/src/core/org/apache/jmeter/threads/JMeterThread.java
+++ b/src/core/org/apache/jmeter/threads/JMeterThread.java
@@ -1,938 +1,934 @@
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
 
 package org.apache.jmeter.threads;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.apache.jmeter.assertions.Assertion;
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.control.TransactionSampler;
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleMonitor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testbeans.TestBeanHelper;
 import org.apache.jmeter.testelement.AbstractScopedAssertion;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.HashTreeTraverser;
 import org.apache.jorphan.collections.SearchByClass;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopTestException;
 import org.apache.jorphan.util.JMeterStopTestNowException;
 import org.apache.jorphan.util.JMeterStopThreadException;
 import org.apache.log.Logger;
 
 /**
  * The JMeter interface to the sampling process, allowing JMeter to see the
  * timing, add listeners for sampling events and to stop the sampling process.
  *
  */
 public class JMeterThread implements Runnable, Interruptible {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final boolean IMPLEMENTS_SAMPLE_STARTED = JMeterUtils.getPropDefault("JMeterThread.sampleStarted", true);
     static {
         log.info("IMPLEMENTS_SAMPLE_STARTED="+IMPLEMENTS_SAMPLE_STARTED);
     }
 
     public static final String PACKAGE_OBJECT = "JMeterThread.pack"; // $NON-NLS-1$
 
     public static final String LAST_SAMPLE_OK = "JMeterThread.last_sample_ok"; // $NON-NLS-1$
 
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     /** How often to check for shutdown during ramp-up, default 1000ms */
     private static final int RAMPUP_GRANULARITY =
             JMeterUtils.getPropDefault("jmeterthread.rampup.granularity", 1000); // $NON-NLS-1$
 
     private final Controller controller;
 
     private final HashTree testTree;
 
     private final TestCompiler compiler;
 
     private final JMeterThreadMonitor monitor;
 
     private final JMeterVariables threadVars;
 
     // Note: this is only used to implement TestIterationListener#testIterationStart
     // Since this is a frequent event, it makes sense to create the list once rather than scanning each time
     // The memory used will be released when the thread finishes
     private final Collection<TestIterationListener> testIterationStartListeners;
 
     private final Collection<SampleMonitor> sampleMonitors;
 
     private final ListenerNotifier notifier;
 
     /*
      * The following variables are set by StandardJMeterEngine.
      * This is done before start() is called, so the values will be published to the thread safely
      * TODO - consider passing them to the constructor, so that they can be made final
      * (to avoid adding lots of parameters, perhaps have a parameter wrapper object.
      */
     private String threadName;
 
     private int initialDelay = 0;
 
     private int threadNum = 0;
 
     private long startTime = 0;
 
     private long endTime = 0;
 
     private boolean scheduler = false;
     // based on this scheduler is enabled or disabled
 
     // Gives access to parent thread threadGroup
     private AbstractThreadGroup threadGroup;
 
     private StandardJMeterEngine engine = null; // For access to stop methods.
 
     /*
      * The following variables may be set/read from multiple threads.
      */
     private volatile boolean running; // may be set from a different thread
 
     private volatile boolean onErrorStopTest;
 
     private volatile boolean onErrorStopTestNow;
 
     private volatile boolean onErrorStopThread;
 
     private volatile boolean onErrorStartNextLoop;
 
     private volatile Sampler currentSampler;
 
     private final ReentrantLock interruptLock = new ReentrantLock(); // ensure that interrupt cannot overlap with shutdown
 
     public JMeterThread(HashTree test, JMeterThreadMonitor monitor, ListenerNotifier note) {
         this.monitor = monitor;
         threadVars = new JMeterVariables();
         testTree = test;
         compiler = new TestCompiler(testTree);
         controller = (Controller) testTree.getArray()[0];
         SearchByClass<TestIterationListener> threadListenerSearcher = new SearchByClass<>(TestIterationListener.class); // TL - IS
         test.traverse(threadListenerSearcher);
         testIterationStartListeners = threadListenerSearcher.getSearchResults();
-        if (IMPLEMENTS_SAMPLE_STARTED) {
-            SearchByClass<SampleMonitor> sampleMonitorSearcher = new SearchByClass<>(SampleMonitor.class);
-            test.traverse(sampleMonitorSearcher);
-            sampleMonitors = sampleMonitorSearcher.getSearchResults();
-        } else {
-            sampleMonitors = null;
-        }
+        SearchByClass<SampleMonitor> sampleMonitorSearcher = new SearchByClass<>(SampleMonitor.class);
+        test.traverse(sampleMonitorSearcher);
+        sampleMonitors = sampleMonitorSearcher.getSearchResults();
         notifier = note;
         running = true;
     }
 
     public void setInitialContext(JMeterContext context) {
         threadVars.putAll(context.getVariables());
     }
 
     /**
      * Enable the scheduler for this JMeterThread.
      *
      * @param sche
      *            flag whether the scheduler should be enabled
      */
     public void setScheduled(boolean sche) {
         this.scheduler = sche;
     }
 
     /**
      * Set the StartTime for this Thread.
      *
      * @param stime the StartTime value.
      */
     public void setStartTime(long stime) {
         startTime = stime;
     }
 
     /**
      * Get the start time value.
      *
      * @return the start time value.
      */
     public long getStartTime() {
         return startTime;
     }
 
     /**
      * Set the EndTime for this Thread.
      *
      * @param etime
      *            the EndTime value.
      */
     public void setEndTime(long etime) {
         endTime = etime;
     }
 
     /**
      * Get the end time value.
      *
      * @return the end time value.
      */
     public long getEndTime() {
         return endTime;
     }
 
     /**
      * Check if the scheduled time is completed.
      */
     private void stopSchedulerIfNeeded() {
         long now = System.currentTimeMillis();
         long delay = now - endTime;
         if ((delay >= 0)) {
             running = false;
             log.info("Stopping because end time detected by thread: " + threadName);
         }
     }
 
     /**
      * Wait until the scheduled start time if necessary
      *
      */
     private void startScheduler() {
         long delay = (startTime - System.currentTimeMillis());
         delayBy(delay, "startScheduler");
     }
 
     public void setThreadName(String threadName) {
         this.threadName = threadName;
     }
 
     @Override
     public void run() {
         // threadContext is not thread-safe, so keep within thread
         JMeterContext threadContext = JMeterContextService.getContext();
         LoopIterationListener iterationListener = null;
 
         try {
             iterationListener = initRun(threadContext);
             Sampler sam = controller.next();
             while (running && sam != null) {
                 processSampler(sam, null, threadContext);
                 threadContext.cleanAfterSample();
                 
                 // restart of the next loop 
                 // - was request through threadContext
                 // - or the last sample failed AND the onErrorStartNextLoop option is enabled
                 if(threadContext.isRestartNextLoop()
                         || (onErrorStartNextLoop
                                 && !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK)))) 
                 {
                     
                     if(log.isDebugEnabled()) {
                         if(onErrorStartNextLoop
                                 && !threadContext.isRestartNextLoop()) {
                             log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
                         }
                     }
                     
                     triggerEndOfLoopOnParentControllers(sam, threadContext);
                     threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
                     threadContext.setRestartNextLoop(false);
                     sam = null;
                 }
                 
                 if (sam == null && controller.isDone()) {
                     running = false;
                     log.info("Thread is done: " + threadName);
                 }
                 else {
                     sam = controller.next();
                 }
             }
         }
         // Might be found by contoller.next()
         catch (JMeterStopTestException e) {
             log.info("Stopping Test: " + e.toString());
             stopTest();
         }
         catch (JMeterStopTestNowException e) {
             log.info("Stopping Test Now: " + e.toString());
             stopTestNow();
         } catch (JMeterStopThreadException e) {
             log.info("Stop Thread seen: " + e.toString());
         } catch (Exception e) {
             log.error("Test failed!", e);
         } catch (ThreadDeath e) {
             throw e; // Must not ignore this one
         } catch (Error e) {// Make sure errors are output to the log file
             log.error("Test failed!", e);
         } finally {
             currentSampler = null; // prevent any further interrupts
             try {
                 interruptLock.lock();  // make sure current interrupt is finished, prevent another starting yet
                 threadContext.clear();
                 log.info("Thread finished: " + threadName);
                 threadFinished(iterationListener);
                 monitor.threadFinished(this); // Tell the monitor we are done
                 JMeterContextService.removeContext(); // Remove the ThreadLocal entry
             }
             finally {
                 interruptLock.unlock(); // Allow any pending interrupt to complete (OK because currentSampler == null)
             }
         }
     }
 
     /**
      * Trigger end of loop on parent controllers up to Thread Group
      * @param sam Sampler Base sampler
      * @param threadContext 
      */
     private void triggerEndOfLoopOnParentControllers(Sampler sam, JMeterContext threadContext) {
         // Find parent controllers of current sampler
         FindTestElementsUpToRootTraverser pathToRootTraverser = null;
         TransactionSampler transactionSampler = null;
         if(sam instanceof TransactionSampler) {
             transactionSampler = (TransactionSampler) sam;
             pathToRootTraverser = new FindTestElementsUpToRootTraverser(transactionSampler.getTransactionController());
         } else {
             pathToRootTraverser = new FindTestElementsUpToRootTraverser(sam);
         }
         testTree.traverse(pathToRootTraverser);
         
         // Trigger end of loop condition on all parent controllers of current sampler
         List<Controller> controllersToReinit = pathToRootTraverser.getControllersToRoot();
         for (Controller parentController : controllersToReinit) {
             if(parentController instanceof AbstractThreadGroup) {
                 AbstractThreadGroup tg = (AbstractThreadGroup) parentController;
                 tg.startNextLoop();
             } else {
                 parentController.triggerEndOfLoop();
             }
         }
         
         // bug 52968
         // When using Start Next Loop option combined to TransactionController.
         // if an error occurs in a Sample (child of TransactionController) 
         // then we still need to report the Transaction in error (and create the sample result)
         if(transactionSampler != null) {
             SamplePackage transactionPack = compiler.configureTransactionSampler(transactionSampler);
             doEndTransactionSampler(transactionSampler, null, transactionPack, threadContext);
         }
     }
 
     /**
      * Process the current sampler, handling transaction samplers.
      *
      * @param current sampler
      * @param parent sampler
      * @param threadContext
      * @return SampleResult if a transaction was processed
      */
     
     private SampleResult processSampler(Sampler current, Sampler parent, JMeterContext threadContext) {
         SampleResult transactionResult = null;
         try {
             // Check if we are running a transaction
             TransactionSampler transactionSampler = null;
             if(current instanceof TransactionSampler) {
                 transactionSampler = (TransactionSampler) current;
             }
             // Find the package for the transaction
             SamplePackage transactionPack = null;
             if(transactionSampler != null) {
                 transactionPack = compiler.configureTransactionSampler(transactionSampler);
 
                 // Check if the transaction is done
                 if(transactionSampler.isTransactionDone()) {
                     transactionResult = doEndTransactionSampler(transactionSampler, 
                             parent, 
                             transactionPack,
                             threadContext);
                     // Transaction is done, we do not have a sampler to sample
                     current = null;
                 }
                 else {
                     Sampler prev = current;
                     // It is the sub sampler of the transaction that will be sampled
                     current = transactionSampler.getSubSampler();
                     if (current instanceof TransactionSampler) {
                         SampleResult res = processSampler(current, prev, threadContext);// recursive call
                         threadContext.setCurrentSampler(prev);
                         current = null;
                         if (res != null) {
                             transactionSampler.addSubSamplerResult(res);
                         }
                     }
                 }
             }
 
             // Check if we have a sampler to sample
             if(current != null) {
                 executeSamplePackage(current, transactionSampler, transactionPack, threadContext);
             }
             
             if (scheduler) {
                 // checks the scheduler to stop the iteration
                 stopSchedulerIfNeeded();
             }
         } catch (JMeterStopTestException e) {
             log.info("Stopping Test: " + e.toString());
             stopTest();
         } catch (JMeterStopThreadException e) {
             log.info("Stopping Thread: " + e.toString());
             stopThread();
         } catch (Exception e) {
             if (current != null) {
                 log.error("Error while processing sampler '"+current.getName()+"' :", e);
             } else {
                 log.error("", e);
             }
         }
         return transactionResult;
     }
 
     /*
      * Execute the sampler with its pre/post processors, timers, assertions
      * Brodcast the result to the sample listeners
      */
     private void executeSamplePackage(Sampler current,
             TransactionSampler transactionSampler,
             SamplePackage transactionPack,
             JMeterContext threadContext) {
         
         threadContext.setCurrentSampler(current);
         // Get the sampler ready to sample
         SamplePackage pack = compiler.configureSampler(current);
         runPreProcessors(pack.getPreProcessors());
 
         // Hack: save the package for any transaction controllers
         threadVars.putObject(PACKAGE_OBJECT, pack);
 
         delay(pack.getTimers());
         Sampler sampler = pack.getSampler();
         sampler.setThreadContext(threadContext);
         // TODO should this set the thread names for all the subsamples?
         // might be more efficient than fetching the name elsewhere
         sampler.setThreadName(threadName);
         TestBeanHelper.prepare(sampler);
 
         // Perform the actual sample
         currentSampler = sampler;
-        if (IMPLEMENTS_SAMPLE_STARTED) {
-            for(SampleMonitor monitor : sampleMonitors) {
-                monitor.sampleStarting();
-            }                    
+        for(SampleMonitor monitor : sampleMonitors) {
+            monitor.sampleStarting();
         }
-        SampleResult result = sampler.sample(null); // TODO: remove this useless Entry parameter
-        if (IMPLEMENTS_SAMPLE_STARTED) {
+        SampleResult result = null;
+        try {
+            result = sampler.sample(null); // TODO: remove this useless Entry parameter
+        } finally {
             for(SampleMonitor monitor : sampleMonitors) {
                 monitor.sampleEnded();
-            }                    
+            }            
         }
         currentSampler = null;
 
         // If we got any results, then perform processing on the result
         if (result != null) {
             int nbActiveThreadsInThreadGroup = threadGroup.getNumberOfThreads();
             int nbTotalActiveThreads = JMeterContextService.getNumberOfThreads();
             result.setGroupThreads(nbActiveThreadsInThreadGroup);
             result.setAllThreads(nbTotalActiveThreads);
             result.setThreadName(threadName);
             SampleResult[] subResults = result.getSubResults();
             if(subResults != null) {
                 for (SampleResult subResult : subResults) {
                     subResult.setGroupThreads(nbActiveThreadsInThreadGroup);
                     subResult.setAllThreads(nbTotalActiveThreads);
                     subResult.setThreadName(threadName);
                 }
             }
             threadContext.setPreviousResult(result);
             runPostProcessors(pack.getPostProcessors());
             checkAssertions(pack.getAssertions(), result, threadContext);
             // Do not send subsamples to listeners which receive the transaction sample
             List<SampleListener> sampleListeners = getSampleListeners(pack, transactionPack, transactionSampler);
             notifyListeners(sampleListeners, result);
             compiler.done(pack);
             // Add the result as subsample of transaction if we are in a transaction
             if(transactionSampler != null) {
                 transactionSampler.addSubSamplerResult(result);
             }
 
             // Check if thread or test should be stopped
             if (result.isStopThread() || (!result.isSuccessful() && onErrorStopThread)) {
                 stopThread();
             }
             if (result.isStopTest() || (!result.isSuccessful() && onErrorStopTest)) {
                 stopTest();
             }
             if (result.isStopTestNow() || (!result.isSuccessful() && onErrorStopTestNow)) {
                 stopTestNow();
             }
             if(result.isStartNextThreadLoop()) {
                 threadContext.setRestartNextLoop(true);
             }
         } else {
             compiler.done(pack); // Finish up
         }
     }
 
     private SampleResult doEndTransactionSampler(
                             TransactionSampler transactionSampler, 
                             Sampler parent,
                             SamplePackage transactionPack,
                             JMeterContext threadContext) {
         SampleResult transactionResult;
         // Get the transaction sample result
         transactionResult = transactionSampler.getTransactionResult();
         transactionResult.setThreadName(threadName);
         transactionResult.setGroupThreads(threadGroup.getNumberOfThreads());
         transactionResult.setAllThreads(JMeterContextService.getNumberOfThreads());
 
         // Check assertions for the transaction sample
         checkAssertions(transactionPack.getAssertions(), transactionResult, threadContext);
         // Notify listeners with the transaction sample result
         if (!(parent instanceof TransactionSampler)) {
             notifyListeners(transactionPack.getSampleListeners(), transactionResult);
         }
         compiler.done(transactionPack);
         return transactionResult;
     }
 
     /**
      * Get the SampleListeners for the sampler. Listeners who receive transaction sample
      * will not be in this list.
      *
      * @param samplePack
      * @param transactionPack
      * @param transactionSampler
      * @return the listeners who should receive the sample result
      */
     private List<SampleListener> getSampleListeners(SamplePackage samplePack, SamplePackage transactionPack, TransactionSampler transactionSampler) {
         List<SampleListener> sampleListeners = samplePack.getSampleListeners();
         // Do not send subsamples to listeners which receive the transaction sample
         if(transactionSampler != null) {
             List<SampleListener> onlySubSamplerListeners = new ArrayList<>();
             List<SampleListener> transListeners = transactionPack.getSampleListeners();
             for(SampleListener listener : sampleListeners) {
                 // Check if this instance is present in transaction listener list
                 boolean found = false;
                 for(SampleListener trans : transListeners) {
                     // Check for the same instance
                     if(trans == listener) {
                         found = true;
                         break;
                     }
                 }
                 if(!found) {
                     onlySubSamplerListeners.add(listener);
                 }
             }
             sampleListeners = onlySubSamplerListeners;
         }
         return sampleListeners;
     }
 
     /**
      * @param threadContext
      * @return the iteration listener 
      */
     private IterationListener initRun(JMeterContext threadContext) {
         threadContext.setVariables(threadVars);
         threadContext.setThreadNum(getThreadNum());
         threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
         threadContext.setThread(this);
         threadContext.setThreadGroup(threadGroup);
         threadContext.setEngine(engine);
         testTree.traverse(compiler);
         if (scheduler) {
             // set the scheduler to start
             startScheduler();
         }
 
         rampUpDelay(); // TODO - how to handle thread stopped here
         log.info("Thread started: " + Thread.currentThread().getName());
         /*
          * Setting SamplingStarted before the controllers are initialised allows
          * them to access the running values of functions and variables (however
          * it does not seem to help with the listeners)
          */
         threadContext.setSamplingStarted(true);
         
         controller.initialize();
         IterationListener iterationListener = new IterationListener();
         controller.addIterationListener(iterationListener);
 
         threadStarted();
         return iterationListener;
     }
 
     private void threadStarted() {
         JMeterContextService.incrNumberOfThreads();
         threadGroup.incrNumberOfThreads();
         GuiPackage gp =GuiPackage.getInstance();
         if (gp != null) {// check there is a GUI
             gp.getMainFrame().updateCounts();
         }
         ThreadListenerTraverser startup = new ThreadListenerTraverser(true);
         testTree.traverse(startup); // call ThreadListener.threadStarted()
     }
 
     private void threadFinished(LoopIterationListener iterationListener) {
         ThreadListenerTraverser shut = new ThreadListenerTraverser(false);
         testTree.traverse(shut); // call ThreadListener.threadFinished()
         JMeterContextService.decrNumberOfThreads();
         threadGroup.decrNumberOfThreads();
         GuiPackage gp = GuiPackage.getInstance();
         if (gp != null){// check there is a GUI
             gp.getMainFrame().updateCounts();
         }
         if (iterationListener != null) { // probably not possible, but check anyway
             controller.removeIterationListener(iterationListener);
         }
     }
 
     // N.B. This is only called at the start and end of a thread, so there is not
     // necessary to cache the search results, thus saving memory
     private static class ThreadListenerTraverser implements HashTreeTraverser {
         private final boolean isStart;
 
         private ThreadListenerTraverser(boolean start) {
             isStart = start;
         }
 
         @Override
         public void addNode(Object node, HashTree subTree) {
             if (node instanceof ThreadListener) {
                 ThreadListener tl = (ThreadListener) node;
                 if (isStart) {
                     tl.threadStarted();
                 } else {
                     tl.threadFinished();
                 }
             }
         }
 
         @Override
         public void subtractNode() {
         }
 
         @Override
         public void processPath() {
         }
     }
 
     public String getThreadName() {
         return threadName;
     }
 
     public void stop() { // Called by StandardJMeterEngine, TestAction and AccessLogSampler
         running = false;
         log.info("Stopping: " + threadName);
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean interrupt(){
         try {
             interruptLock.lock();
             Sampler samp = currentSampler; // fetch once; must be done under lock
             if (samp instanceof Interruptible){ // (also protects against null)
                 log.warn("Interrupting: " + threadName + " sampler: " +samp.getName());
                 try {
                     boolean found = ((Interruptible)samp).interrupt();
                     if (!found) {
                         log.warn("No operation pending");
                     }
                     return found;
                 } catch (Exception e) {
                     log.warn("Caught Exception interrupting sampler: "+e.toString());
                 }
             } else if (samp != null){
                 log.warn("Sampler is not Interruptible: "+samp.getName());
             }
         } finally {
             interruptLock.unlock();            
         }
         return false;
     }
 
     private void stopTest() {
         running = false;
         log.info("Stop Test detected by thread: " + threadName);
         if (engine != null) {
             engine.askThreadsToStop();
         }
     }
 
     private void stopTestNow() {
         running = false;
         log.info("Stop Test Now detected by thread: " + threadName);
         if (engine != null) {
             engine.stopTest();
         }
     }
 
     private void stopThread() {
         running = false;
         log.info("Stop Thread detected by thread: " + threadName);
     }
 
     private void checkAssertions(List<Assertion> assertions, SampleResult parent, JMeterContext threadContext) {
         for (Assertion assertion : assertions) {
             TestBeanHelper.prepare((TestElement) assertion);
             if (assertion instanceof AbstractScopedAssertion){
                 AbstractScopedAssertion scopedAssertion = (AbstractScopedAssertion) assertion;
                 String scope = scopedAssertion.fetchScope();
                 if (scopedAssertion.isScopeParent(scope) || scopedAssertion.isScopeAll(scope) || scopedAssertion.isScopeVariable(scope)){
                     processAssertion(parent, assertion);
                 }
                 if (scopedAssertion.isScopeChildren(scope) || scopedAssertion.isScopeAll(scope)){
                     SampleResult children[] = parent.getSubResults();
                     boolean childError = false;
                     for (int i=0;i <children.length; i++){
                         processAssertion(children[i], assertion);
                         if (!children[i].isSuccessful()){
                             childError = true;
                         }
                     }
                     // If parent is OK, but child failed, add a message and flag the parent as failed
                     if (childError && parent.isSuccessful()) {
                         AssertionResult assertionResult = new AssertionResult(((AbstractTestElement)assertion).getName());
                         assertionResult.setResultForFailure("One or more sub-samples failed");
                         parent.addAssertionResult(assertionResult);
                         parent.setSuccessful(false);
                     }
                 }
             } else {
                 processAssertion(parent, assertion);
             }
         }
         threadContext.getVariables().put(LAST_SAMPLE_OK, Boolean.toString(parent.isSuccessful()));
     }
 
     private void processAssertion(SampleResult result, Assertion assertion) {
         AssertionResult assertionResult;
         try {
             assertionResult = assertion.getResult(result);
         } catch (ThreadDeath e) {
             throw e;
         } catch (Error e) {
             log.error("Error processing Assertion ",e);
             assertionResult = new AssertionResult("Assertion failed! See log file.");
             assertionResult.setError(true);
             assertionResult.setFailureMessage(e.toString());
         } catch (Exception e) {
             log.error("Exception processing Assertion ",e);
             assertionResult = new AssertionResult("Assertion failed! See log file.");
             assertionResult.setError(true);
             assertionResult.setFailureMessage(e.toString());
         }
         result.setSuccessful(result.isSuccessful() && !(assertionResult.isError() || assertionResult.isFailure()));
         result.addAssertionResult(assertionResult);
     }
 
     private void runPostProcessors(List<PostProcessor> extractors) {
         for (PostProcessor ex : extractors) {
             TestBeanHelper.prepare((TestElement) ex);
             ex.process();
         }
     }
 
     private void runPreProcessors(List<PreProcessor> preProcessors) {
         for (PreProcessor ex : preProcessors) {
             if (log.isDebugEnabled()) {
                 log.debug("Running preprocessor: " + ((AbstractTestElement) ex).getName());
             }
             TestBeanHelper.prepare((TestElement) ex);
             ex.process();
         }
     }
 
     private void delay(List<Timer> timers) {
         long sum = 0;
         for (Timer timer : timers) {
             TestBeanHelper.prepare((TestElement) timer);
             sum += timer.delay();
         }
         if (sum > 0) {
             try {
                 TimeUnit.MILLISECONDS.sleep(sum);
             } catch (InterruptedException e) {
                 log.warn("The delay timer was interrupted - probably did not wait as long as intended.");
             }
         }
     }
 
     void notifyTestListeners() {
         threadVars.incIteration();
         for (TestIterationListener listener : testIterationStartListeners) {
             if (listener instanceof TestElement) {
                 listener.testIterationStart(new LoopIterationEvent(controller, threadVars.getIteration()));
                 ((TestElement) listener).recoverRunningVersion();
             } else {
                 listener.testIterationStart(new LoopIterationEvent(controller, threadVars.getIteration()));
             }
         }
     }
 
     private void notifyListeners(List<SampleListener> listeners, SampleResult result) {
         SampleEvent event = new SampleEvent(result, threadGroup.getName(), threadVars);
         notifier.notifyListeners(event, listeners);
 
     }
 
     /**
      * Set rampup delay for JMeterThread Thread
      * @param delay Rampup delay for JMeterThread
      */
     public void setInitialDelay(int delay) {
         initialDelay = delay;
     }
 
     /**
      * Initial delay if ramp-up period is active for this threadGroup.
      */
     private void rampUpDelay() {
         delayBy(initialDelay, "RampUp");
     }
 
     /**
      * Wait for delay with RAMPUP_GRANULARITY
      * @param delay delay in ms
      * @param type Delay type
      */
     protected final void delayBy(long delay, String type) {
         if (delay > 0) {
             long start = System.currentTimeMillis();
             long end = start + delay;
             long now=0;
             long pause = RAMPUP_GRANULARITY;
             while(running && (now = System.currentTimeMillis()) < end) {
                 long togo = end - now;
                 if (togo < pause) {
                     pause = togo;
                 }
                 try {
                     TimeUnit.MILLISECONDS.sleep(pause); // delay between checks
                 } catch (InterruptedException e) {
                     if (running) { // Don't bother reporting stop test interruptions
                         log.warn(type+" delay for "+threadName+" was interrupted. Waited "+(now - start)+" milli-seconds out of "+delay);
                     }
                     break;
                 }
             }
         }
     }
 
     /**
      * Returns the threadNum.
      *
      * @return the threadNum
      */
     public int getThreadNum() {
         return threadNum;
     }
 
     /**
      * Sets the threadNum.
      *
      * @param threadNum
      *            the threadNum to set
      */
     public void setThreadNum(int threadNum) {
         this.threadNum = threadNum;
     }
 
     private class IterationListener implements LoopIterationListener {
         /**
          * {@inheritDoc}
          */
         @Override
         public void iterationStart(LoopIterationEvent iterEvent) {
             notifyTestListeners();
         }
     }
 
     /**
      * Save the engine instance for access to the stop methods
      *
      * @param engine the engine which is used
      */
     public void setEngine(StandardJMeterEngine engine) {
         this.engine = engine;
     }
 
     /**
      * Should Test stop on sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStopTest(boolean b) {
         onErrorStopTest = b;
     }
 
     /**
      * Should Test stop abruptly on sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStopTestNow(boolean b) {
         onErrorStopTestNow = b;
     }
 
     /**
      * Should Thread stop on Sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStopThread(boolean b) {
         onErrorStopThread = b;
     }
 
     /**
      * Should Thread start next loop on Sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStartNextLoop(boolean b) {
         onErrorStartNextLoop = b;
     }
 
     public void setThreadGroup(AbstractThreadGroup group) {
         this.threadGroup = group;
     }
 
 }
