diff --git a/bin/jmeter.properties b/bin/jmeter.properties
index b518658ef..1ebc45c94 100644
--- a/bin/jmeter.properties
+++ b/bin/jmeter.properties
@@ -1,1050 +1,1056 @@
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
 
 
 #Preferred GUI language. Comment out to use the JVM default locale's language.
 #language=en
 
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
 # see https://issues.apache.org/bugzilla/show_bug.cgi?id=54759
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
 # See https://issues.apache.org/bugzilla/show_bug.cgi?id=52026 for details
 # N.B. the laf can be defined in user.properties.
 
 # LoggerPanel display
 # default to false
 #jmeter.loggerpanel.display=false
 
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
 
 # Icon definitions
 # default:
 #jmeter.icons=org/apache/jmeter/images/icon.properties
 # alternate:
 #jmeter.icons=org/apache/jmeter/images/icon_1.properties
 
 #Components to not display in JMeter GUI (GUI class name or static label)
 # These elements are deprecated: HTML Parameter Mask,HTTP User Parameter Modifier, Webservice (SOAP) Request
 not_in_menu=org.apache.jmeter.protocol.http.modifier.gui.ParamModifierGui, HTTP User Parameter Modifier, org.apache.jmeter.protocol.http.control.gui.WebServiceSamplerGui
 
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
 #log_level.jmeter.util.classfinder=WARN
 #log_level.jmeter.test=DEBUG
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
 
 # Idle connection timeout (ms) to apply if the server does not send Keep-Alive headers
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
 #jmeter.save.saveservice.samplerData=false
 #jmeter.save.saveservice.responseHeaders=false
 #jmeter.save.saveservice.requestHeaders=false
 #jmeter.save.saveservice.encoding=false
 #jmeter.save.saveservice.bytes=true
 #jmeter.save.saveservice.url=false
 #jmeter.save.saveservice.filename=false
 #jmeter.save.saveservice.hostname=false
 #jmeter.save.saveservice.thread_counts=false
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
 
 # If the recorder detects a gap of at least 1s (default) between HTTP requests,
 # it assumes that the user has clicked a new URL
 #proxy.pause=1000
 
 # Add numeric prefix to Sampler names (default false)
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
 #proxy.ssl.protocol=SSLv3
 
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
 # see https://issues.apache.org/bugzilla/show_bug.cgi?id=55632
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
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.RegexpHTMLParser
 
 htmlParser.types=text/html application/xhtml+xml application/xml text/xml
 
 #---------------------------------------------------------------------------
 # WML Parser configuration
 #---------------------------------------------------------------------------
 
 wmlParser.className=org.apache.jmeter.protocol.http.parser.RegexpHTMLParser
 
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
 
 # (2.0.3) JMeterThread behaviour has been changed to set the started flag before
 # the controllers are initialised. This is so controllers can access variables earlier. 
 # In case this causes problems, the previous behaviour can be restored by uncommenting
 # the following line.
 #jmeterthread.startearlier=false
 
 # (2.2.1) JMeterThread behaviour has changed so that PostProcessors are run in forward order
 # (as they appear in the test plan) rather than reverse order as previously.
 # Uncomment the following line to revert to the original behaviour
 #jmeterthread.reversePostProcessors=true
 
 # (2.2) StandardJMeterEngine behaviour has been changed to notify the listeners after
 # the running version is enabled. This is so they can access variables. 
 # In case this causes problems, the previous behaviour can be restored by uncommenting
 # the following line.
 #jmeterengine.startlistenerslater=false
 
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
 
 # Maximum size of HTML page that can be displayed; default=200 * 1024
 # Set to 0 to disable the size check
 #view.results.tree.max_size=0
 
+# Order of Renderers in View Results Tree
+# Note full class names should be used for non jmeter core renderers
+# For JMeter core renderers, class names start with . and are automatically
+# prefixed with org.apache.jmeter.visualizers
+view.results.tree.renderers_order=.RenderAsText,.RenderAsRegexp,.RenderAsCssJQuery,.RenderAsXPath,.RenderAsHTML,.RenderAsHTMLWithEmbedded,.RenderAsDocument,.RenderAsJSON,.RenderAsXML
+
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
 # Do not use this for utility ir plugin dependecy jars.
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
diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index a63970b13..3c6b3cbc1 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,427 +1,451 @@
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
 
 /**
  *
  */
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.ItemEvent;
 import java.awt.event.ItemListener;
 import java.io.IOException;
 import java.util.Collections;
+import java.util.HashMap;
 import java.util.List;
+import java.util.Map;
 
 import javax.swing.ComboBoxModel;
 import javax.swing.DefaultComboBoxModel;
 import javax.swing.ImageIcon;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTree;
 import javax.swing.event.TreeSelectionEvent;
 import javax.swing.event.TreeSelectionListener;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.DefaultTreeModel;
 import javax.swing.tree.TreePath;
 import javax.swing.tree.TreeSelectionModel;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Base for ViewResults
  *
  */
 public class ViewResultsFullVisualizer extends AbstractVisualizer
 implements ActionListener, TreeSelectionListener, Clearable, ItemListener {
 
     private static final long serialVersionUID = 7338676747296593842L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final Color SERVER_ERROR_COLOR = Color.red;
 
     public static final Color CLIENT_ERROR_COLOR = Color.blue;
 
     public static final Color REDIRECT_COLOR = Color.green;
 
     private  JSplitPane mainSplit;
 
     private DefaultMutableTreeNode root;
 
     private DefaultTreeModel treeModel;
 
     private JTree jTree;
 
     private Component leftSide;
 
     private JTabbedPane rightSide;
 
     private JComboBox selectRenderPanel;
 
     private int selectedTab;
 
     protected static final String COMBO_CHANGE_COMMAND = "change_combo"; // $NON-NLS-1$
 
     private static final ImageIcon imageSuccess = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.success",  //$NON-NLS-1$
                     "icon_success_sml.gif")); //$NON-NLS-1$
 
     private static final ImageIcon imageFailure = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.failure",  //$NON-NLS-1$
                     "icon_warning_sml.gif")); //$NON-NLS-1$
 
     // Maximum size that we will display
     private static final int MAX_DISPLAY_SIZE =
         JMeterUtils.getPropDefault("view.results.tree.max_size", 200 * 1024); // $NON-NLS-1$
 
+    // default display order
+    private static final String VIEWERS_ORDER =
+        JMeterUtils.getPropDefault("view.results.tree.renderers_order", ""); // $NON-NLS-1$ //$NON-NLS-2$
+
     private ResultRenderer resultsRender = null;
 
     private TreeSelectionEvent lastSelectionEvent;
 
     private JCheckBox autoScrollCB;
 
     /**
      * Constructor
      */
     public ViewResultsFullVisualizer() {
         super();
         init();
     }
 
     /** {@inheritDoc} */
     @Override
     public void add(final SampleResult sample) {
         JMeterUtils.runSafe(new Runnable() {
             @Override
             public void run() {
                 updateGui(sample);
             }
         });
     }
 
     /**
      * Update the visualizer with new data.
      */
     private synchronized void updateGui(SampleResult res) {
         // Add sample
         DefaultMutableTreeNode currNode = new DefaultMutableTreeNode(res);
         treeModel.insertNodeInto(currNode, root, root.getChildCount());
         addSubResults(currNode, res);
         // Add any assertion that failed as children of the sample node
         AssertionResult assertionResults[] = res.getAssertionResults();
         int assertionIndex = currNode.getChildCount();
         for (int j = 0; j < assertionResults.length; j++) {
             AssertionResult item = assertionResults[j];
 
             if (item.isFailure() || item.isError()) {
                 DefaultMutableTreeNode assertionNode = new DefaultMutableTreeNode(item);
                 treeModel.insertNodeInto(assertionNode, currNode, assertionIndex++);
             }
         }
 
         if (root.getChildCount() == 1) {
             jTree.expandPath(new TreePath(root));
         }
         if (autoScrollCB.isSelected() && root.getChildCount() > 1) {
             jTree.scrollPathToVisible(new TreePath(new Object[] { root,
                     treeModel.getChild(root, root.getChildCount() - 1) }));
         }
     }
 
     private void addSubResults(DefaultMutableTreeNode currNode, SampleResult res) {
         SampleResult[] subResults = res.getSubResults();
 
         int leafIndex = 0;
 
         for (int i = 0; i < subResults.length; i++) {
             SampleResult child = subResults[i];
 
             if (log.isDebugEnabled()) {
                 log.debug("updateGui1 : child sample result - " + child);
             }
             DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(child);
 
             treeModel.insertNodeInto(leafNode, currNode, leafIndex++);
             addSubResults(leafNode, child);
             // Add any assertion that failed as children of the sample node
             AssertionResult assertionResults[] = child.getAssertionResults();
             int assertionIndex = leafNode.getChildCount();
             for (int j = 0; j < assertionResults.length; j++) {
                 AssertionResult item = assertionResults[j];
 
                 if (item.isFailure() || item.isError()) {
                     DefaultMutableTreeNode assertionNode = new DefaultMutableTreeNode(item);
                     treeModel.insertNodeInto(assertionNode, leafNode, assertionIndex++);
                 }
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void clearData() {
         while (root.getChildCount() > 0) {
             // the child to be removed will always be 0 'cos as the nodes are
             // removed the nth node will become (n-1)th
             treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(0));
         }
         resultsRender.clearData();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getLabelResource() {
         return "view_results_tree_title"; // $NON-NLS-1$
     }
 
     /**
      * Initialize this visualizer
      */
     protected void init() {
         log.debug("init() - pass");
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         leftSide = createLeftPanel();
         // Prepare the common tab
         rightSide = new JTabbedPane();
 
         // Create the split pane
         mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, rightSide);
         add(mainSplit, BorderLayout.CENTER);
         // init right side with first render
         resultsRender.setRightSide(rightSide);
         resultsRender.init();
     }
 
     /** {@inheritDoc} */
     @Override
     public void valueChanged(TreeSelectionEvent e) {
         lastSelectionEvent = e;
         DefaultMutableTreeNode node = null;
         synchronized (this) {
             node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
         }
 
         if (node != null) {
             // to restore last tab used
             if (rightSide.getTabCount() > selectedTab) {
                 resultsRender.setLastSelectedTab(rightSide.getSelectedIndex());
             }
             Object userObject = node.getUserObject();
             resultsRender.setSamplerResult(userObject);
             resultsRender.setupTabPane(); // Processes Assertions
             // display a SampleResult
             if (userObject instanceof SampleResult) {
                 SampleResult sampleResult = (SampleResult) userObject;
                 if (isTextDataType(sampleResult)){
                     resultsRender.renderResult(sampleResult);
                 } else {
                     byte[] responseBytes = sampleResult.getResponseData();
                     if (responseBytes != null) {
                         resultsRender.renderImage(sampleResult);
                     }
                 }
             }
         }
     }
 
     /**
      * @param sampleResult SampleResult
      * @return true if sampleResult is text or has empty content type
      */
     protected static boolean isTextDataType(SampleResult sampleResult) {
         return (SampleResult.TEXT).equals(sampleResult.getDataType())
                 || StringUtils.isEmpty(sampleResult.getDataType());
     }
 
     private synchronized Component createLeftPanel() {
         SampleResult rootSampleResult = new SampleResult();
         rootSampleResult.setSampleLabel("Root");
         rootSampleResult.setSuccessful(true);
         root = new DefaultMutableTreeNode(rootSampleResult);
 
         treeModel = new DefaultTreeModel(root);
         jTree = new JTree(treeModel);
         jTree.setCellRenderer(new ResultsNodeRenderer());
         jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
         jTree.addTreeSelectionListener(this);
         jTree.setRootVisible(false);
         jTree.setShowsRootHandles(true);
         JScrollPane treePane = new JScrollPane(jTree);
         treePane.setPreferredSize(new Dimension(200, 300));
 
         VerticalPanel leftPane = new VerticalPanel();
         leftPane.add(treePane, BorderLayout.CENTER);
-        VerticalPanel leftDownPane = new VerticalPanel();
-        leftDownPane.add(createComboRender(), BorderLayout.NORTH);
+        leftPane.add(createComboRender(), BorderLayout.NORTH);
         autoScrollCB = new JCheckBox(JMeterUtils.getResString("view_results_autoscroll")); // $NON-NLS-1$
         autoScrollCB.setSelected(false);
         autoScrollCB.addItemListener(this);
-        leftDownPane.add(autoScrollCB, BorderLayout.SOUTH);
-        leftPane.add(leftDownPane, BorderLayout.SOUTH);
+        leftPane.add(autoScrollCB, BorderLayout.SOUTH);
         return leftPane;
     }
 
     /**
      * Create the drop-down list to changer render
      * @return List of all render (implement ResultsRender)
      */
     private Component createComboRender() {
         ComboBoxModel nodesModel = new DefaultComboBoxModel();
         // drop-down list for renderer
         selectRenderPanel = new JComboBox(nodesModel);
         selectRenderPanel.setActionCommand(COMBO_CHANGE_COMMAND);
         selectRenderPanel.addActionListener(this);
 
         // if no results render in jmeter.properties, load Standard (default)
         List<String> classesToAdd = Collections.<String>emptyList();
         try {
             classesToAdd = JMeterUtils.findClassesThatExtend(ResultRenderer.class);
         } catch (IOException e1) {
             // ignored
         }
         String textRenderer = JMeterUtils.getResString("view_results_render_text"); // $NON-NLS-1$
         Object textObject = null;
+        Map<String, ResultRenderer> map = new HashMap<String, ResultRenderer>(classesToAdd.size());
         for (String clazz : classesToAdd) {
             try {
                 // Instantiate render classes
                 final ResultRenderer renderer = (ResultRenderer) Class.forName(clazz).newInstance();
                 if (textRenderer.equals(renderer.toString())){
                     textObject=renderer;
                 }
                 renderer.setBackgroundColor(getBackground());
-                selectRenderPanel.addItem(renderer);
+                map.put(renderer.getClass().getName(), renderer);
             } catch (Exception e) {
-                log.warn("Error in load result render:" + clazz, e);
+                log.warn("Error loading result renderer:" + clazz, e);
             }
         }
+        if(VIEWERS_ORDER.length()>0) {
+            String[] keys = VIEWERS_ORDER.split(",");
+            for (String key : keys) {
+                if(key.startsWith(".")) {
+                    key = "org.apache.jmeter.visualizers"+key; //$NON-NLS-1$
+                }
+                ResultRenderer renderer = map.remove(key);
+                if(renderer != null) {
+                    selectRenderPanel.addItem(renderer);
+                } else {
+                    log.warn("Missing (check spelling error in renderer name) or already added(check doublon) " +
+                    		"result renderer, check property 'view.results.tree.renderers_order', renderer name:'"+key+"'");
+                }
+            }
+        }
+        // Add remaining (plugins or missed in property)
+        for (ResultRenderer renderer : map.values()) {
+            selectRenderPanel.addItem(renderer);
+        }
         nodesModel.setSelectedItem(textObject); // preset to "Text" option
         return selectRenderPanel;
     }
 
     /** {@inheritDoc} */
     @Override
     public void actionPerformed(ActionEvent event) {
         String command = event.getActionCommand();
         if (COMBO_CHANGE_COMMAND.equals(command)) {
             JComboBox jcb = (JComboBox) event.getSource();
 
             if (jcb != null) {
                 resultsRender = (ResultRenderer) jcb.getSelectedItem();
                 if (rightSide != null) {
                     // to restore last selected tab (better user-friendly)
                     selectedTab = rightSide.getSelectedIndex();
                     // Remove old right side
                     mainSplit.remove(rightSide);
 
                     // create and add a new right side
                     rightSide = new JTabbedPane();
                     mainSplit.add(rightSide);
                     resultsRender.setRightSide(rightSide);
                     resultsRender.setLastSelectedTab(selectedTab);
                     log.debug("selectedTab=" + selectedTab);
                     resultsRender.init();
                     // To display current sampler result before change
                     this.valueChanged(lastSelectionEvent);
                 }
             }
         }
     }
 
     public static String getResponseAsString(SampleResult res) {
         String response = null;
         if (isTextDataType(res)) {
             // Showing large strings can be VERY costly, so we will avoid
             // doing so if the response
             // data is larger than 200K. TODO: instead, we could delay doing
             // the result.setText
             // call until the user chooses the "Response data" tab. Plus we
             // could warn the user
             // if this happens and revert the choice if he doesn't confirm
             // he's ready to wait.
             int len = res.getResponseData().length;
             if (MAX_DISPLAY_SIZE > 0 && len > MAX_DISPLAY_SIZE) {
                 StringBuilder builder = new StringBuilder(MAX_DISPLAY_SIZE+100);
                 builder.append(JMeterUtils.getResString("view_results_response_too_large_message")) //$NON-NLS-1$
                     .append(len).append(" > Max: ").append(MAX_DISPLAY_SIZE)
                     .append(", ").append(JMeterUtils.getResString("view_results_response_partial_message")) // $NON-NLS-1$
                     .append("\n").append(res.getResponseDataAsString().substring(0, MAX_DISPLAY_SIZE)).append("\n...");
                 response = builder.toString();
             } else {
                 response = res.getResponseDataAsString();
             }
         }
         return response;
     }
 
     private static class ResultsNodeRenderer extends DefaultTreeCellRenderer {
         private static final long serialVersionUID = 4159626601097711565L;
 
         @Override
         public Component getTreeCellRendererComponent(JTree tree, Object value,
                 boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {
             super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
             boolean failure = true;
             Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
             if (userObject instanceof SampleResult) {
                 failure = !(((SampleResult) userObject).isSuccessful());
             } else if (userObject instanceof AssertionResult) {
                 AssertionResult assertion = (AssertionResult) userObject;
                 failure = assertion.isError() || assertion.isFailure();
             }
 
             // Set the status for the node
             if (failure) {
                 this.setForeground(Color.red);
                 this.setIcon(imageFailure);
             } else {
                 this.setIcon(imageSuccess);
             }
             return this;
         }
     }
 
     /**
      * Handler for Checkbox
      */
     @Override
     public void itemStateChanged(ItemEvent e) {
         // NOOP state is held by component
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index d2ee67093..c937111ec 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,234 +1,236 @@
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
 
 
 <!--  =================== 2.12 =================== -->
 
 <h1>Version 2.12</h1>
 
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
 
 
 <!--  =================== Known bugs =================== -->
 
 
 <ch_section>Known bugs</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </li>
 
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
 With Java 1.6 and Gnome 3 on Linux systems, the JMeter menu may not work correctly (shift between mouse's click and the menu). 
 This is a known Java bug (see  <bugzilla>54477 </bugzilla>). 
 A workaround is to use a Java 7 runtime (OpenJDK or Oracle JDK).
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
 </ul>
 
 <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55998</bugzilla> - HTTP recording – Replacing port value by user defined variable does not work</li>
 <li><bugzilla>56178</bugzilla> - keytool error: Invalid escaped character in AVA: - some characters must be escaped</li>
 <li><bugzilla>56222</bugzilla> - NPE if jmeter.httpclient.strict_rfc2616=true and location is not absolute</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>55977</bugzilla> - JDBC pook keepalive flooding</li>
 <li><bugzilla>55999</bugzilla> - Scroll bar on jms point-to-point sampler does not work when content exceeds display</li>
 <li><bugzilla>56198</bugzilla> - JMSSampler : NullPointerException is thrown when JNDI underlying implementation of JMS provider does not comply with Context.getEnvironment contract</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>56162</bugzilla> -  HTTP Cache Manager should not cache PUT/POST etc.</li>
+<li><bugzilla>56227</bugzilla> - AssertionGUI : NPE in assertion on mouse selection</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li><bugzilla>56111</bugzilla> - "comments" in german translation is not correct</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>56059</bugzilla> - Older TestBeans incompatible with 2.11 when using TextAreaEditor</li>
 <li><bugzilla>56080</bugzilla> - Conversion error com.thoughtworks.xstream.converters.ConversionException with Java 8 Early Access Build</li>
 <li><bugzilla>56182</bugzilla> - Can't trigger bsh script using bshclient.jar; socket is closed unexpectedly </li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55959</bugzilla> - improve error message when Test Script Recorder fails due to I/O problem</li>
 <li><bugzilla>52013</bugzilla> -  Test Script Recorder's Child View Results Tree does not take into account Test Script Recorder excluded/included URLs. Based on report and analysis of James Liang</li>
 <li><bugzilla>56119</bugzilla> - File uploads fail every other attempt using timers. Enable idle timeouts for servers that don't send Keep-Alive headers.</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>Bug 56033</bugzilla> - Add Connection timeout and Read timeout to SMTP Sampler</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
+<li><bugzilla>56228</bugzilla> - View Results Tree : Improve ergonomy by changing placement of Renderers and allowing custom ordering</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
-<li><bugzilla>56227</bugzilla> - AssertionGUI : NPE in assertion on mouse selection</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>21695</bugzilla> - Unix jmeter start script assumes it is on PATH, not a link</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to commons-lang3 3.2.1 (from 3.1)</li>
 <li>Updated to commons-codec 1.9 (from 1.8)</li>
 <li>Updated to tika 1.5 (from 1.4)</li>
 <li>Updated to xercesImpl 2.11.0 (from 2.9.1)</li>
 <li>Updated to xml-apis 1.4.01 (from 1.3.04)</li>
 <li>Updated to xstream 1.4.6 (from 1.4.4)</li>
 </ul>
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li>James Liang (jliang at andera.com)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
+<li>Oliver LLoyd (email at oliverlloyd.com)</li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
