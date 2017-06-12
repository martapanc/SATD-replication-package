diff --git a/bin/jmeter.properties b/bin/jmeter.properties
index d1e6670a3..1c01191f2 100644
--- a/bin/jmeter.properties
+++ b/bin/jmeter.properties
@@ -1,1089 +1,1089 @@
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
 
 # Icon definitions
 # default:
 #jmeter.icons=org/apache/jmeter/images/icon.properties
 # alternate:
 #jmeter.icons=org/apache/jmeter/images/icon_1.properties
 
 #Components to not display in JMeter GUI (GUI class name or static label)
 # These elements are deprecated: HTML Parameter Mask,HTTP User Parameter Modifier, Webservice (SOAP) Request
 not_in_menu=org.apache.jmeter.protocol.http.modifier.gui.ParamModifierGui, HTTP User Parameter Modifier, org.apache.jmeter.protocol.http.control.gui.WebServiceSamplerGui
 
 # Number of items in undo history
 # The bigger it is, the more it consumes memory
 #undo.history.size=25
 
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
 #jmeter.save.saveservice.samplerData=false
 #jmeter.save.saveservice.responseHeaders=false
 #jmeter.save.saveservice.requestHeaders=false
 #jmeter.save.saveservice.encoding=false
 #jmeter.save.saveservice.bytes=true
 #jmeter.save.saveservice.url=false
 #jmeter.save.saveservice.filename=false
 #jmeter.save.saveservice.hostname=false
-#jmeter.save.saveservice.thread_counts=false
+#jmeter.save.saveservice.thread_counts=true
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
diff --git a/bin/testfiles/jmeter-batch.properties b/bin/testfiles/jmeter-batch.properties
index af01c9398..0785967f8 100644
--- a/bin/testfiles/jmeter-batch.properties
+++ b/bin/testfiles/jmeter-batch.properties
@@ -1,23 +1,26 @@
 ################################################################################
 # Apache JMeter Property file for batch runs
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
 
 # Ensure log is empty by default
 log_level.jmeter=WARN
 # Revert to original default mode
 mode=Standard
+
+# Since JMeter 2.12, defaults for this property is true
+jmeter.save.saveservice.thread_counts=false
\ No newline at end of file
diff --git a/bin/testfiles/jmetertest.properties b/bin/testfiles/jmetertest.properties
index 89404dcfe..afb424c54 100644
--- a/bin/testfiles/jmetertest.properties
+++ b/bin/testfiles/jmetertest.properties
@@ -1,173 +1,175 @@
 ################################################################################
 # Apache JMeter Property file - used for unit tests only
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
 #language=de
 
 #Paths to search for classes (";" must be the separator)
 #search_paths=null
 
 # Netscape HTTP Cookie file
 cookies=cookies
 
 #File format for saved test files.  
 # JMeter 2.1+ uses a new format for JMX and JTL files - using XStream.
 # JMeter 2.1.2+ has a new shorter format for JMX files. 
 # Set value to 2.0 or 2.1 to save to old formats
 # 
 # Save test plans and test logs in 2.0 format
 #file_format=2.0
 # Just test plans (jmx)
 #file_format.testplan=2.1
 # Just test logs (jtl)
 #file_format.testlog=2.0
 
 # Authorization
 authorization=authorization
 
 #Working directory
 user.dir=.
 
 # XML Reader(Parser) - Must implement SAX 2 specs
 xml.parser=org.apache.xerces.parsers.SAXParser
 
 #Classname of the ssl provider to be used (to enable testing of https urls)
 #And the package name where Stream Handlers can be found
 #These provided defaults can be uncommented, and they will work if you are using
 #Sun's JSSE implementation.
 
 #ssl.provider=com.sun.net.ssl.internal.ssl.Provider
 #ssl.pkgs=com.sun.net.ssl.internal.www.protocol
 
 #The location of the truststore (trusted certificates) and keystore ( if other than the default.
 #you can uncomment this and change the path to the correct location.
 #javax.net.ssl.trustStore=/path/to/cacerts
 #javax.net.ssl.keyStore=/path/to/keystore
 
 #The password to your keystore
 #javax.net.ssl.keyStorePassword=password
 
 
 #Flag for whether to output debug messages to System.err
 #To enable it, set the value to "all"  Note, for it to work with
 #JSSE, it needs to be done from the Java command (i.e. -Djavax.net.debug=all)
 #javax.net.debug=all
 
 #Classname of the Swing default UI
 #Installed Look and Feel classes on Windows are:
 #  Metal   = javax.swing.plaf.metal.MetalLookAndFeel
 #  Motif   = com.sun.java.swing.plaf.motif.MotifLookAndFeel
 #  Windows = com.sun.java.swing.plaf.windows.WindowsLookAndFeel
 ## Let LAF be picked up from default 
 ## (otherwise can cause problems for Eclipse JUnit GUI mode)
 #jmeter.laf=javax.swing.plaf.metal.MetalLookAndFeel
 #jmeter.laf=com.sun.java.swing.plaf.motif.MotifLookAndFeel
 
 #icons -> moved to program code
 #timer.tree.icon=timer.gif
 #listener.tree.icon=ear.gif
 #bench.tree.icon=clipboard.gif
 #thread.tree.icon=thread.gif
 #control.tree.icon=knob.gif
 #plan.tree.icon=beaker.gif
 #config.tree.icon=leafnode.gif
 
 # Remote Hosts - comma delimited
 remote_hosts=127.0.0.1
 
 #Components to not display in JMeter GUI
 not_in_menu=Remote Method Configuration,JNDI Configuration,JNDI Lookup Configuration,JNDI Request,Default Controller,org.apache.jmeter.control.DynamicController, org.apache.jmeter.protocol.http.control.Cookie,org.apache.jmeter.protocol.http.control.Authorization,org.apache.jmeter.config.LoginConfig,Header,org.apache.jmeter.protocol.http.config.MultipartUrlConfig
 
 #Logging levels for the logging categories in JMeter.  Correct values are FATAL_ERROR, ERROR, WARN, INFO, and DEBUG
 # To set the log level for a package or individual class, use:
 # log_level.[package_name].[classname]=[PRIORITY_LEVEL]
 # But omit "org.apache" from the package name.  The classname is optional.  Further examples below.
 
 log_level.jmeter=INFO
 #log_level.jmeter.junit=DEBUG
 #log_level.jmeter.engine=WARN
 #log_level.jmeter.gui=WARN
 #log_level.jmeter.testelement=DEBUG
 #log_level.jmeter.util=WARN
 #log_level.jmeter.util.classfinder=WARN
 #log_level.jmeter.test=DEBUG
 #log_level.jmeter.protocol.http=DEBUG
 #log_level.jmeter.protocol.ftp=WARN
 #log_level.jmeter.protocol.jdbc=WARN
 #log_level.jmeter.protocol.java=WARN
 #log_level.jmeter.testelements.property=DEBUG
 log_level.jorphan=INFO
 #log_level.jorphan.reflect.ClassFinder=DEBUG
 
 #Log file for log messages.
 # You can specify a different log file for different categories via:
 # log_file.[category]=[filename]
 # category is equivalent to the package/class names described above
 
 # Combined log file (for jmeter and jorphan)
 log_file=jmeter-test.log
 
 # Or define separate logs if required:
 #log_file.jorphan=jorphan.log
 #log_file.jmeter=jmeter.log
 
 
 #---------------------------------------------------------------------------
 # Results file configuration
 #---------------------------------------------------------------------------
 
 # For testing, output is changed to CSV and variable fields
 # (timestamp and elased) are suppressed
 
 # This section helps determine how result data will be saved.
 # The commented out values are the defaults.
 
 # legitimate values: xml, csv, db.  Only xml and csv are currently supported.
 jmeter.save.saveservice.output_format=csv
 
 # Define true to save the output files in TestSaveService.java
 #testsaveservice.saveout=true
 
 # true when field should be saved; false otherwise
 
 # assertion_results_failure_message only affects CSV output
 #jmeter.save.saveservice.assertion_results_failure_message=true
 #jmeter.save.saveservice.data_type=true
 #jmeter.save.saveservice.label=true
 #jmeter.save.saveservice.response_code=true
 #jmeter.save.saveservice.response_data=false
 #jmeter.save.saveservice.response_message=true
 #jmeter.save.saveservice.successful=true
 #jmeter.save.saveservice.thread_name=true
 jmeter.save.saveservice.time=false
+# Since JMeter 2.12, defaults for this property is true
+jmeter.save.saveservice.thread_counts=false
 
 # legitimate values: none, ms, or a format suitable for SimpleDateFormat
 #jmeter.save.saveservice.timestamp_format=none
 #jmeter.save.saveservice.timestamp_format=MM/dd/yy HH:mm:ss
 
 # legitimate values: none, first, all
 #jmeter.save.saveservice.assertion_results=none
 
 # For use with Comma-separated value (CSV) files or other formats
 # where the fields' values are separated by specified delimiters.
 #jmeter.save.saveservice.default_delimiter=,
 #jmeter.save.saveservice.print_field_names=true
 
 # File that holds a record of name changes for backward compatibility issues
 upgrade_properties=/bin/upgrade.properties
diff --git a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
index 37fcea02d..24f5bf160 100644
--- a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
+++ b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
@@ -1,837 +1,837 @@
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
  * - update SampleSaveConfigurationConverter to add new fields to marshall() and shouldSerialiseMember()
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
  * For each field XXX
  * - methods have the signature "boolean saveXXX()"
  * - a corresponding "void setXXX(boolean)" method
  * - messages.properties contains the key save_XXX
  *
  *
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
     private static final String MILLISECONDS = "ms"; // $NON_NLS-1$
 
     /** A properties file indicator for none. * */
     private static final String NONE = "none"; // $NON_NLS-1$
 
     /** A properties file indicator for the first of a series. * */
     private static final String FIRST = "first"; // $NON_NLS-1$
 
     /** A properties file indicator for all of a series. * */
     private static final String ALL = "all"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which assertion results should be
      * saved.
      **************************************************************************/
     private static final String ASSERTION_RESULTS_FAILURE_MESSAGE_PROP =
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
     private static final String DEFAULT_DELIMITER_PROP = "jmeter.save.saveservice.default_delimiter"; // $NON_NLS-1$
 
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
     private boolean time = _time, latency = _latency, timestamp = _timestamp, success = _success, label = _label,
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
             _responseData, _dataType, _encoding, _assertions, _latency, _subresults, _samplerData, _fieldNames,
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
 
     private static final String DEFAULT_DELIMITER = ","; // $NON_NLS-1$
 
     /**
      * Read in the properties having to do with saving from a properties file.
      */
     static {
         Properties props = JMeterUtils.getJMeterProperties();
 
         _subresults      = TRUE.equalsIgnoreCase(props.getProperty(SUBRESULTS_PROP, TRUE));
         _assertions      = TRUE.equalsIgnoreCase(props.getProperty(ASSERTIONS_PROP, TRUE));
         _latency         = TRUE.equalsIgnoreCase(props.getProperty(LATENCY_PROP, TRUE));
         _samplerData     = TRUE.equalsIgnoreCase(props.getProperty(SAMPLERDATA_PROP, FALSE));
         _responseHeaders = TRUE.equalsIgnoreCase(props.getProperty(RESPONSEHEADERS_PROP, FALSE));
         _requestHeaders  = TRUE.equalsIgnoreCase(props.getProperty(REQUESTHEADERS_PROP, FALSE));
         _encoding        = TRUE.equalsIgnoreCase(props.getProperty(ENCODING_PROP, FALSE));
 
         String dlm = props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER);
         if (dlm.equals("\\t")) {// Make it easier to enter a tab (can use \<tab> but that is awkward)
             dlm="\t";
         }
 
         if (dlm.length() != 1){
             throw new JMeterError("Delimiter '"+dlm+"' must be of length 1.");
         }
         char ch = dlm.charAt(0);
 
         if (CharUtils.isAsciiAlphanumeric(ch) || ch == CSVSaveService.QUOTING_CHAR){
             throw new JMeterError("Delimiter '"+ch+"' must not be alphanumeric or "+CSVSaveService.QUOTING_CHAR+".");
         }
 
         if (ch != '\t' && !CharUtils.isAsciiPrintable(ch)){
             throw new JMeterError("Delimiter (code "+(int)ch+") must be printable.");
         }
 
         _delimiter = dlm;
 
         _fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, FALSE));
 
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
         if (!_printMilliseconds && !NONE.equalsIgnoreCase(_timeStampFormat) && (_timeStampFormat != null)) {
             _formatter = new SimpleDateFormat(_timeStampFormat);
         } else {
             _formatter = null;
         }
 
         _timestamp = !NONE.equalsIgnoreCase(_timeStampFormat);// reversed compare allows for null
 
         _saveAssertionResultsFailureMessage = TRUE.equalsIgnoreCase(props.getProperty(
                 ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, FALSE));
 
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
 
-        _threadCounts=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, FALSE));
+        _threadCounts=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, TRUE));
 
         _sampleCount=TRUE.equalsIgnoreCase(props.getProperty(SAVE_SAMPLE_COUNT, FALSE));
 
         _idleTime=TRUE.equalsIgnoreCase(props.getProperty(SAVE_IDLE_TIME, FALSE));
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
 
     public SampleSaveConfiguration() {
     }
 
     /**
      * Alternate constructor for use by OldSaveService
      *
      * @param value initial setting for boolean fields used in Config dialogue
      */
     public SampleSaveConfiguration(boolean value) {
         assertions = value;
         bytes = value;
         code = value;
         dataType = value;
         encoding = value;
         fieldNames = value;
         fileName = value;
         hostname = value;
         label = value;
         latency = value;
         message = value;
         printMilliseconds = _printMilliseconds;//is derived from properties only
         requestHeaders = value;
         responseData = value;
         responseDataOnError = value;
         responseHeaders = value;
         samplerData = value;
         saveAssertionResultsFailureMessage = value;
         subresults = value;
         success = value;
         threadCounts = value;
         sampleCount = value;
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
      * Only intended for use by OldSaveService (and test cases)
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
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 1c29bad05..4354f0e7f 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,363 +1,365 @@
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
 
 <ch_title>Java 8 support</ch_title>
 <p>
 Now, JMeter 2.12 is compliant with Java 8.
 </p>
 
 <ch_category>New Elements</ch_category>
 <ch_title>Critical Section Controller</ch_title>
 <p>The Critical Section Controller allow to serialize the execution of a section in your tree. 
 Only one instance of the section will be executed at the same time during the test.</p>
 <figure width="683" height="240" image="changes/2.12/01_critical_section_controller.png"></figure>
 
 <ch_title>DNS Cache Manager</ch_title>
 <p>The new configuration element <b>DNS Cache Manager</b> allow to improve the testing of CDN (Content Delivery Network) 
 and/or DNS load balancing.</p>
 <figure width="573" height="359" image="changes/2.12/02_dns_cache_manager.png"></figure>
 
 <ch_category>Core Improvements</ch_category>
 <ch_title>Better handling of embedded resources</ch_title>
 <p>When download embedded resources is checked, JMeter now uses User Agent header to download or not resources embedded within conditionnal comments as per <a href="http://msdn.microsoft.com/en-us/library/ms537512%28v=vs.85%29.aspx" target="_blank">About conditional comments</a>.</p>
 
 <ch_title>Ability to customize Cache Manager (Browser cache simulation) handling of cached resources</ch_title>
 <p>You can now configure the behaviour of JMeter when a resource is found in Cache, this can be controlled with <i>cache_manager.cached_resource_mode</i> property</p>
 <figure width="1024" height="314" image="changes/2.12/12_cache_resource_mode.png"></figure>
 
 
 <ch_title>JMS Publisher / JMS Point-to-Point</ch_title>
 <p> Add JMSPriority and JMSExpiration fields for these samplers.</p>
 <figure width="901" height="277" image="changes/2.12/04_jms_publisher.png"></figure>
 
 <figure width="900" height="294" image="changes/2.12/05_jms_point_to_point.png"></figure>
 
 <ch_title>Mail Reader Sampler</ch_title>
 <p>You can now specify the number of messages that want you retrieve (before all messages were retrieved). 
 In addition, you can fetch only the message header now.</p>
 <figure width="814" height="416" image="changes/2.12/03_mail_reader_sampler.png"></figure>
 
 <ch_title>SMTP Sampler</ch_title>
 <p>Adding the Connection timeout and the Read timeout to the <b>SMTP Sampler.</b></p>
 <figure width="796" height="192" image="changes/2.12/06_smtp_sampler.png"></figure>
 
 <ch_title>Synchronizing Timer </ch_title>
 <p>Adding a timeout to define the maximum time to waiting of the group of virtual users.</p>
 <figure width="546" height="144" image="changes/2.12/09_synchronizing_timer.png"></figure>
 
 <ch_category>GUI Improvements</ch_category>
 
 <ch_title>Undo/Redo support</ch_title>
 <p>Undo / Redo has been introduced and allows user to undo/redo changes made on Test Plan Tree</p>
 <figure width="1024" height="56" image="changes/2.12/10_undo_redo.png"></figure>
 
 <ch_title>View Results Tree</ch_title>
 <p>Improve the ergonomics of View Results Tree by changing placement of Renderers and allowing custom ordering 
 (with the property <i>view.results.tree.renderers_order</i>).</p>
 <figure width="900" height="329" image="changes/2.12/07_view_results_tree.png"></figure>
 
 <ch_title>Response Time Graph</ch_title>
 <p>Adding the ability for the <b>Response Time Graph</b> listener to save/restore format its settings in/from the jmx file.</p>
 <figure width="997" height="574" image="changes/2.12/08_response_time_graph.png"></figure>
 
 <ch_title>Log Viewer</ch_title>
 <p>Starting with this version, jmeter logs can be viewed in GUI by clicking on Warning icon in the upper right corner. This will unfold the Log Viewer panel and show logs.</p>
 <figure width="1024" height="437" image="changes/2.12/11_log_viewer.png"></figure>
 
 
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
 <li>Since JMeter 2.12, Mail Reader Sampler will show 1 for number of samples instead of number of messages retrieved, see <bugzilla>56539</bugzilla></li>
 <li>Since JMeter 2.12, when using Cache Manager, if resource is found in cache no SampleResult will be created, in previous version a SampleResult with empty content and 204 return code was returned, see <bugzilla>54778</bugzilla>.
 You can choose between different ways to handle this case, see cache_manager.cached_resource_mode in jmeter.properties.</li>
 <li>Since JMeter 2.12, Log Viewer will no more clear logs when closed and will have logs available even if closed. See <bugzilla>56920</bugzilla>. Read <a href="./usermanual/hints_and_tips.html#debug_logging">Hints and Tips &gt; Enabling Debug logging</a>
 for details on configuring this component.</li>
 </ul>
 
 <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55998</bugzilla> - HTTP recording – Replacing port value by user defined variable does not work</li>
 <li><bugzilla>56178</bugzilla> - keytool error: Invalid escaped character in AVA: - some characters must be escaped</li>
 <li><bugzilla>56222</bugzilla> - NPE if jmeter.httpclient.strict_rfc2616=true and location is not absolute</li>
 <li><bugzilla>56263</bugzilla> - DefaultSamplerCreator should set BrowserCompatible Multipart true</li>
 <li><bugzilla>56231</bugzilla> - Move redirect location processing from HC3/HC4 samplers to HTTPSamplerBase#followRedirects()</li>
 <li><bugzilla>56207</bugzilla> - URLs get encoded on redirects in HC3.1 &amp; HC4 samplers</li>
 <li><bugzilla>56303</bugzilla> - The width of target controller's combo list should be set to the current panel size, not on label size of the controllers</li>
 <li><bugzilla>54778</bugzilla> - HTTP Sampler should not return 204 when resource is found in Cache, make it configurable with new property cache_manager.cached_resource_mode</li> 
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>55977</bugzilla> - JDBC pool keepalive flooding</li>
 <li><bugzilla>55999</bugzilla> - Scroll bar on jms point-to-point sampler does not work when content exceeds display</li>
 <li><bugzilla>56198</bugzilla> - JMSSampler : NullPointerException is thrown when JNDI underlying implementation of JMS provider does not comply with Context.getEnvironment contract</li>
 <li><bugzilla>56428</bugzilla> - MailReaderSampler - should it use mail.pop3s.* properties?</li>
 <li><bugzilla>46932</bugzilla> - Alias given in select statement is not used as column header in response data for a JDBC request.Based on report and analysis of Nicola Ambrosetti</li>
 <li><bugzilla>56539</bugzilla> - Mail reader sampler: When Number of messages to retrieve is superior to 1, Number of samples should only show 1 not the number of messages retrieved</li>
 <li><bugzilla>56809</bugzilla> - JMSSampler closes InitialContext too early. Contributed by Bradford Hovinen (hovinen at gmail.com)</li>
 <li><bugzilla>56761</bugzilla> - JMeter tries to stop already stopped JMS connection and displays "The connection is closed"</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>56243</bugzilla> - Foreach works incorrectly with indexes on subsequent iterations </li>
 <li><bugzilla>56276</bugzilla> - Loop controller becomes broken once loop count evaluates to zero </li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>56706</bugzilla> - SampleResult#getResponseDataAsString() does not use encoding in response body impacting PostProcessors and ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>56162</bugzilla> -  HTTP Cache Manager should not cache PUT/POST etc.</li>
 <li><bugzilla>56227</bugzilla> - AssertionGUI : NPE in assertion on mouse selection</li>
 <li><bugzilla>41319</bugzilla> - URLRewritingModifier : Allow Parameter value to be url encoded</li>
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
 <li><bugzilla>56360</bugzilla> - HashTree and ListedHashTree fail to compile with Java 8</li>
 <li><bugzilla>56419</bugzilla> - Jmeter silently fails to save results</li>
 <li><bugzilla>56662</bugzilla> - Save as xml in a listener is not remembered</li>
 <li><bugzilla>56367</bugzilla> - JMeter 2.11 on maven central triggers a not existing dependency rsyntaxtextarea 2.5.1, upgrade to 2.5.3</li>
 <li><bugzilla>56743</bugzilla> - Wrong mailing list archives on mail2.xml. Contributed by Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>56763</bugzilla> - Removing the Oracle icons, not used by JMeter (and missing license)</li>
 <li><bugzilla>54100</bugzilla> - Switching languages fails to preserve toolbar button states (enabled/disabled)</li>
 <li><bugzilla>54648</bugzilla> - JMeter GUI on OS X crashes when using CMD+C (keyboard shortcut or UI menu entry) on an element from the tree</li>
 <li><bugzilla>56962</bugzilla> - JMS GUIs should disable all fields affected by jndi.properties checkbox</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55959</bugzilla> - Improve error message when Test Script Recorder fails due to I/O problem</li>
 <li><bugzilla>52013</bugzilla> - Test Script Recorder's Child View Results Tree does not take into account Test Script Recorder excluded/included URLs. Based on report and analysis of James Liang</li>
 <li><bugzilla>56119</bugzilla> - File uploads fail every other attempt using timers. Enable idle timeouts for servers that don't send Keep-Alive headers.</li>
 <li><bugzilla>56272</bugzilla> - MirrorServer should support query parameters for status and redirects</li>
 <li><bugzilla>56772</bugzilla> - Handle IE Conditional comments when parsing embedded resources</li>
+<li><bugzilla>57026</bugzilla> - HTTP(S) Test Script Recorder : Better default settings. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>56033</bugzilla> - Add Connection timeout and Read timeout to SMTP Sampler</li>
 <li><bugzilla>56429</bugzilla> - MailReaderSampler - no need to fetch all Messages if not all wanted</li>
 <li><bugzilla>56427</bugzilla> - MailReaderSampler enhancement: read message header only</li>
 <li><bugzilla>56510</bugzilla> - JMS Publisher/Point to Point: Add JMSPriority and JMSExpiration</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>56728</bugzilla> - New Critical Section Controller to serialize blocks of a Test. Based partly on a patch contributed by Mikhail Epikhin(epihin-m at yandex.ru)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>56228</bugzilla> - View Results Tree : Improve ergonomy by changing placement of Renderers and allowing custom ordering</li>
 <li><bugzilla>56349</bugzilla> - "summary" is a bad name for a Generate Summary Results component, documentation clarified</li>
 <li><bugzilla>56769</bugzilla> - Adds the ability for the Response Time Graph listener to save/restore format settings in/from the jmx file</li>
+<li><bugzilla>57025</bugzilla> - SaveService : Better defaults, save thread counts by default</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>56691</bugzilla> - Synchronizing Timer : Add timeout on waiting</li>
 <li><bugzilla>56701</bugzilla> - HTTP Authorization Manager/ Kerberos Authentication: add port to SPN when server port is neither 80 nor 443. Based on patches from Dan Haughey (dan.haughey at swinton.co.uk) and Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>56841</bugzilla> - New configuration element: DNS Cache Manager to improve the testing of CDN. Based on patch from Dzmitry Kashlach (dzmitrykashlach at gmail.com), and contributed by BlazeMeter Ltd.</li>
 <li><bugzilla>52061</bugzilla> - Allow access to Request Headers in Regex Extractor. Based on patch from Dzmitry Kashlach (dzmitrykashlach at gmail.com), and contributed by BlazeMeter Ltd.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>56708</bugzilla> - __jexl2 doesn't scale with multiple CPU cores. Based on analysis and patch contributed by Mikhail Epikhin(epihin-m at yandex.ru)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>21695</bugzilla> - Unix jmeter start script assumes it is on PATH, not a link</li>
 <li><bugzilla>56292</bugzilla> - Add the check of the Java's version in startup files and disable some options when is Java v8 engine</li>
 <li><bugzilla>56298</bugzilla> - JSR223 language display does not show which engine will be used</li>
 <li><bugzilla>56455</bugzilla> - Batch files: drop support for non-NT Windows shell scripts</li>
 <li><bugzilla>56807</bugzilla> - Ability to force flush of ResultCollector file. Contributed by Andrey Pohilko (apc4 at ya.ru)</li>
 <li><bugzilla>56921</bugzilla> - Templates : Improve Recording template to ignore embedded resources case and URL parameters. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>42248</bugzilla> - Undo-redo support on Test Plan tree modification. Developed by Andrey Pohilko (apc4 at ya.ru) and contributed by BlazeMeter Ltd. Additional contribution by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>56920</bugzilla> - LogViewer : Make it receive all log events even when it is closed. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to commons-lang3 3.3.2 (from 3.1)</li>
 <li>Updated to commons-codec 1.9 (from 1.8)</li>
 <li>Updated to commons-logging 1.2 (from 1.1.3)</li>
 <li>Updated to tika 1.6 (from 1.4)</li>
 <li>Updated to xercesImpl 2.11.0 (from 2.9.1)</li>
 <li>Updated to xml-apis 1.4.01 (from 1.3.04)</li>
 <li>Updated to xstream 1.4.7 (from 1.4.4)</li>
 <li>Updated to jodd 3.6 (from 3.4.10)</li>
 <li>Updated to rsyntaxtextarea 2.5.3 (from 2.5.1)</li>
 <li>Updated xalan and serializer to 2.7.2 (from 2.7.1)</li>
 </ul>
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li>James Liang (jliang at andera.com)</li>
 <li>Emmanuel Bourg (ebourg at apache.org)</li>
 <li>Nicola Ambrosetti (ambrosetti.nicola at gmail.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li>Dan Haughey (dan.haughey at swinton.co.uk)</li>
 <li>Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li>Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li>Andrey Pohilko (apc4 at ya.ru)</li>
 <li>Bradford Hovinen (hovinen at gmail.com)</li>
 <li><a href="http://blazemeter.com">BlazeMeter Ltd.</a></li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Oliver LLoyd (email at oliverlloyd.com) for his help on <bugzilla>56119</bugzilla></li>
 <li>Vladimir Ryabtsev (greatvovan at gmail.com) for his help on <bugzilla>56243</bugzilla> and <bugzilla>56276</bugzilla></li>
 <li>Adrian Speteanu (asp.adieu at gmail.com) and Matt Kilbride (matt.kilbride at gmail.com) for their feedback and tests on <bugzilla>54648</bugzilla></li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
diff --git a/xdocs/usermanual/listeners.xml b/xdocs/usermanual/listeners.xml
index 94ed27e35..7e6b59e3f 100644
--- a/xdocs/usermanual/listeners.xml
+++ b/xdocs/usermanual/listeners.xml
@@ -1,495 +1,495 @@
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
 ]>
 
 <document prev="build-monitor-test-plan.html" next="remote-test.html" id="$Id$">
 
 <properties>
   <title>User's Manual: Listeners</title>
 </properties>
 
 <body>
 
 <section name="&sect-num;. Introduction to listeners" anchor="intro">
 <p>A listener is a component that shows the results of the
 samples. The results can be shown in a tree, tables, graphs or simply written to a log
 file. To view the contents of a response from any given sampler, add either of the Listeners "View
 Results Tree" or "View Results in table" to a test plan. To view the response time graphically, add
 graph results, spline results or distribution graph. 
 The <complink name="listeners">Listeners</complink> 
 section of the components page has full descriptions of all the listeners.</p>
 
 <note>
 Different listeners display the response information in different ways. 
 However, they all write the same raw data to the output file - if one is specified.
 </note>
 <p>
 The "Configure" button can be used to specify which fields to write to the file, and whether to 
 write it as CSV or XML. 
 CSV files are much smaller than XML files, so use CSV if you are generating lots of samples.
 </p>
 <p>
 The file name can be specified using either a relative or an absolute path name.
 Relative paths are resolved relative to the current working directory (which defaults to the bin/ directory).
 Versions of JMeter after 2.4 also support paths relative to the directory containing the current test plan (JMX file).
 If the path name begins with "~/" (or whatever is in the jmeter.save.saveservice.base_prefix JMeter property),
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
 The default items to be saved can be defined in the jmeter.properties (or user.properties) file.
 The properties are used as the initial settings for the Listener Config pop-up, and are also
 used for the log file specified by the -l command-line flag (commonly used for non-GUI test runs).
 </p>
 <p>To change the default format, find the following line in jmeter.properties:</p>
 <p>jmeter.save.saveservice.output_format=</p>
 <p>
 The information to be saved is configurable.  For maximum information, choose "xml" as the format and specify "Functional Test Mode" on the Test Plan element.  If this box is not checked, the default saved
 data includes a time stamp (the number of milliseconds since midnight,
 January 1, 1970 UTC), the data type, the thread name, the label, the
 response time, message, and code, and a success indicator.  If checked, all information, including the full response data will be logged.</p>
 <p>
 The following example indicates how to set
 properties to get a vertical bar ("|") delimited format that will
 output results like:.</p>
 <p>
 <code>
 <pre>
 timeStamp|time|label|responseCode|threadName|dataType|success|failureMessage
 02/06/03 08:21:42|1187|Home|200|Thread Group-1|text|true|
 02/06/03 08:21:42|47|Login|200|Thread Group-1|text|false|Test Failed: 
     expected to contain: password etc.
 </pre>
 </code></p>
 <p>
 The corresponding jmeter.properties that need to be set are shown below.  One oddity
 in this example is that the output_format is set to csv, which
 typically
 indicates comma-separated values.  However, the default_delimiter was
 set to be a vertical bar instead of a comma, so the csv tag is a
 misnomer in this case. (Think of CSV as meaning character separated values)</p>
 <p>
 <code>
 <pre>
 jmeter.save.saveservice.output_format=csv
 jmeter.save.saveservice.assertion_results_failure_message=true
 jmeter.save.saveservice.default_delimiter=|
 </pre>
 </code>
 <p>
 The full set of properties that affect result file output is shown below.
 </p>
 <code>
 <pre>
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
-#jmeter.save.saveservice.thread_counts=false
+#jmeter.save.saveservice.thread_counts=true
 #jmeter.save.saveservice.sample_count=false
 #jmeter.save.saveservice.idle_time=false
 
 # Timestamp format
 # legitimate values: none, ms, or a format suitable for SimpleDateFormat
 #jmeter.save.saveservice.timestamp_format=ms
 #jmeter.save.saveservice.timestamp_format=yyyy/MM/dd HH:mm:ss.SSS
 
 # Put the start time stamp in logs instead of the end
 sampleresult.timestamp.start=true
 
 # Whether to use System.nanoTime() - otherwise only use System.currentTimeMillis()
 #sampleresult.useNanoTime=true
 
 # Use a background thread to calculate the nanoTime offset
 # Set this to &lt;= 0 to disable the background thread
 #sampleresult.nanoThreadSleep=5000
 
 # legitimate values: none, first, all
 #jmeter.save.saveservice.assertion_results=none
 
 # For use with Comma-separated value (CSV) files or other formats
 # where the fields' values are separated by specified delimiters.
 # Default:
 #jmeter.save.saveservice.default_delimiter=,
 # For TAB, since JMeter 2.3 one can use:
 #jmeter.save.saveservice.default_delimiter=\t
 
 #jmeter.save.saveservice.print_field_names=false
 
 # Optional list of JMeter variable names whose values are to be saved in the result data files.
 # Use commas to separate the names. For example:
 #sample_variables=SESSION_ID,REFERENCE
 # N.B. The current implementation saves the values in XML as attributes,
 # so the names must be valid XML names.
 # Versions of JMeter after 2.3.2 send the variable to all servers
 # to ensure that the correct data is available at the client.
 
 # Optional xml processing instruction for line 2 of the file:
 #jmeter.save.saveservice.xml_pi=&amp;lt;?xml-stylesheet type="text/xsl" href="sample.xsl"?>
 
 # Prefix used to identify filenames that are relative to the current base
 #jmeter.save.saveservice.base_prefix=~/
 </pre>
 </code></p>
 <p>
 The date format to be used for the timestamp_format is described in <a
 HREF="http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html">
 <b>SimpleDateFormat</b></a>.
 The timestamp format is used for both writing and reading files.
 If the format is set to "ms", and the column does not parse as a long integer,
 JMeter (2.9+) will try the following formats:
 <ul>
 <li>yyyy/MM/dd HH:mm:ss.SSS</li>
 <li>yyyy/MM/dd HH:mm:ss</li>
 <li>yyyy-MM-dd HH:mm:ss.SSS</li>
 <li>yyyy-MM-dd HH:mm:ss</li>
 <li>MM/dd/yy HH:mm:ss (this is for compatibility with previous versions; it is not recommended as a format)</li>
 </ul> 
 Matching is now also strict (non-lenient).
 JMeter 2.8 and earlier used lenient mode which could result in timestamps with incorrect dates 
 (times were usually correct).</p>
 <subsection name="&sect-num;.1.1 Sample Variables" anchor="sample_variables">
 <p>
 JMeter supports the <b>sample_variables</b> 
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
 Note that cookies, method and the query string are saved as part of the "Sampler Data" option.
 </p>
 </section>
 
 <section name="&sect-num;.2 non-GUI (batch) test runs" anchor="batch">
 <p>
 When running in non-GUI mode, the -l flag can be used to create a top-level listener for the test run.
 This is in addition to any Listeners defined in the test plan.
 The configuration of this listener is controlled by entries in the file jmeter.properties
 as described in the previous section.
 </p>
 <p>
 This feature can be used to specify different data and log files for each test run, for example:
 <pre>
 jmeter -n -t testplan.jmx -l testplan_01.jtl -j testplan_01.log
 jmeter -n -t testplan.jmx -l testplan_02.jtl -j testplan_02.log
 </pre>
 </p>
 <p>
 Note that JMeter logging messages are written to the file <b>jmeter.log</b> by default.
 This file is recreated each time, so if you want to keep the log files for each run, 
 you will need to rename it using the -j option as above. The -j option was added in version 2.3.
 </p>
 <p>Versions of JMeter after 2.3.1 support variables in the log file name.
 If the filename contains  paired single-quotes, then the name is processed
 as a SimpleDateFormat format applied to the current date, for example:
 <b>log_file='jmeter_'yyyyMMddHHmmss'.tmp'</b>. 
 This can be used to generate a unique name for each test run.
 </p>
 </section>
 
 <section name="&sect-num;.3 Resource usage" anchor="resources">
 <p><b>Listeners can use a lot of memory if there are a lot of samples.</b>
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
 <li>Distribution Graph</li>
 </ul>
 <p>To minimise the amount of memory needed, use the Simple Data Writer, and use the CSV format.</p>
 </section>
 
 <section name="&sect-num;.4 CSV Log format" anchor="csvlogformat">
 <p>
 The CSV log format depends on which data items are selected in the configuration.
 Only the specified data items are recorded in the file.
 The order of appearance of columns is fixed, and is as follows:
 </p>
 <ul>
 <li>timeStamp - in milliseconds since 1/1/1970</li>
 <li>elapsed - in milliseconds</li>
 <li>label - sampler label</li>
 <li>responseCode - e.g. 200, 404</li>
 <li>responseMessage - e.g. OK</li>
 <li>threadName</li>
 <li>dataType - e.g. text</li>
 <li>success - true or false</li>
 <li>failureMessage - if any</li>
 <li>bytes - number of bytes in the sample</li>
 <li>grpThreads - number of active threads in this thread group</li>
 <li>allThreads - total number of active threads in all groups</li>
 <li>URL</li>
 <li>Filename - if Save Response to File was used</li>
 <li>latency - time to first response</li>
 <li>encoding</li>
 <li>SampleCount - number of samples (1, unless multiple samples are aggregated)</li>
 <li>ErrorCount - number of errors (0 or 1, unless multiple samples are aggregated)</li>
 <li>Hostname where the sample was generated</li>
 <li>IdleTime - number of milliseconds of 'Idle' time (normally 0)</li>
 <li>Variables, if specified</li>
 </ul>
 
 </section>
 
 <section name="&sect-num;.5 XML Log format 2.1" anchor="xmlformat2.1">
 <p>
 The format of the updated XML (2.1) is as follows (line breaks will be different):
 </p>
 <pre>
 &amp;lt;?xml version="1.0" encoding="UTF-8"?>
 &amp;lt;testResults version="1.2">
 
 -- HTTP Sample, with nested samples 
 
 &amp;lt;httpSample t="1392" lt="351" ts="1144371014619" s="true" 
      lb="HTTP Request" rc="200" rm="OK" 
      tn="Listen 1-1" dt="text" de="iso-8859-1" by="12407">
   &amp;lt;httpSample t="170" lt="170" ts="1144371015471" s="true" 
         lb="http://www.apache.org/style/style.css" rc="200" rm="OK" 
         tn="Listen 1-1" dt="text" de="ISO-8859-1" by="1002">
     &amp;lt;responseHeader class="java.lang.String">HTTP/1.1 200 OK
 Date: Fri, 07 Apr 2006 00:50:14 GMT
 ...
 Content-Type: text/css
 &amp;lt;/responseHeader>
     &amp;lt;requestHeader class="java.lang.String">MyHeader: MyValue&amp;lt;/requestHeader>
     &amp;lt;responseData class="java.lang.String">body, td, th {
     font-size: 95%;
     font-family: Arial, Geneva, Helvetica, sans-serif;
     color: black;
     background-color: white;
 }
 ...
 &amp;lt;/responseData>
     &amp;lt;cookies class="java.lang.String">&amp;lt;/cookies>
     &amp;lt;method class="java.lang.String">GET&amp;lt;/method>
     &amp;lt;queryString class="java.lang.String">&amp;lt;/queryString>
     &amp;lt;url>http://www.apache.org/style/style.css&amp;lt;/url>
   &amp;lt;/httpSample>
   &amp;lt;httpSample t="200" lt="180" ts="1144371015641" s="true" 
      lb="http://www.apache.org/images/asf_logo_wide.gif" 
      rc="200" rm="OK" tn="Listen 1-1" dt="bin" de="ISO-8859-1" by="5866">
     &amp;lt;responseHeader class="java.lang.String">HTTP/1.1 200 OK
 Date: Fri, 07 Apr 2006 00:50:14 GMT
 ...
 Content-Type: image/gif
 &amp;lt;/responseHeader>
     &amp;lt;requestHeader class="java.lang.String">MyHeader: MyValue&amp;lt;/requestHeader>
     &amp;lt;responseData class="java.lang.String">http://www.apache.org/asf.gif&amp;lt;/responseData>
       &amp;lt;responseFile class="java.lang.String">Mixed1.html&amp;lt;/responseFile>
     &amp;lt;cookies class="java.lang.String">&amp;lt;/cookies>
     &amp;lt;method class="java.lang.String">GET&amp;lt;/method>
     &amp;lt;queryString class="java.lang.String">&amp;lt;/queryString>
     &amp;lt;url>http://www.apache.org/asf.gif&amp;lt;/url>
   &amp;lt;/httpSample>
   &amp;lt;responseHeader class="java.lang.String">HTTP/1.1 200 OK
 Date: Fri, 07 Apr 2006 00:50:13 GMT
 ...
 Content-Type: text/html; charset=ISO-8859-1
 &amp;lt;/responseHeader>
   &amp;lt;requestHeader class="java.lang.String">MyHeader: MyValue&amp;lt;/requestHeader>
   &amp;lt;responseData class="java.lang.String">&lt;!DOCTYPE html PUBLIC &quot;-//W3C//DTD XHTML 1.0 Transitional//EN&quot;
                &quot;http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd&quot;&gt;
 ...
 &amp;amp;lt;html&amp;amp;gt;
  &amp;amp;lt;head&amp;amp;gt;
 ...
  &amp;amp;lt;/head&amp;amp;gt;
  &amp;amp;lt;body&amp;amp;gt;        
 ...
  &amp;amp;lt;/body&amp;amp;gt;
 &amp;amp;lt;/html&amp;amp;gt;
 &amp;lt;/responseData>
   &amp;lt;cookies class="java.lang.String">&amp;lt;/cookies>
   &amp;lt;method class="java.lang.String">GET&amp;lt;/method>
   &amp;lt;queryString class="java.lang.String">&amp;lt;/queryString>
   &amp;lt;url>http://www.apache.org/&amp;lt;/url>
 &amp;lt;/httpSample>
 
 -- nonHTTPP Sample
 
 &amp;lt;sample t="0" lt="0" ts="1144372616082" s="true" lb="Example Sampler"
     rc="200" rm="OK" tn="Listen 1-1" dt="text" de="ISO-8859-1" by="10">
   &amp;lt;responseHeader class="java.lang.String">&amp;lt;/responseHeader>
   &amp;lt;requestHeader class="java.lang.String">&amp;lt;/requestHeader>
   &amp;lt;responseData class="java.lang.String">Listen 1-1&amp;lt;/responseData>
   &amp;lt;responseFile class="java.lang.String">Mixed2.unknown&amp;lt;/responseFile>
   &amp;lt;samplerData class="java.lang.String">ssssss&amp;lt;/samplerData>
 &amp;lt;/sample>
 
 &amp;lt;/testResults>
 </pre>
 <p>
 Note that the sample node name may be either "sample" or "httpSample".
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
 <tr><td>by</td><td>Bytes</td></tr>
 <tr><td>de</td><td>Data encoding</td></tr>
 <tr><td>dt</td><td>Data type</td></tr>
 <tr><td>ec</td><td>Error count (0 or 1, unless multiple samples are aggregated)</td></tr>
 <tr><td>hn</td><td>Hostname where the sample was generated</td></tr>
 <tr><td>it</td><td>Idle Time = time not spent sampling (milliseconds) (generally 0)</td></tr>
 <tr><td>lb</td><td>Label</td></tr>
 <tr><td>lt</td><td>Latency = time to initial response (milliseconds) - not all samplers support this</td></tr>
 <tr><td>na</td><td>Number of active threads for all thread groups</td></tr>
 <tr><td>ng</td><td>Number of active threads in this group</td></tr>
 <tr><td>rc</td><td>Response Code (e.g. 200)</td></tr>
 <tr><td>rm</td><td>Response Message (e.g. OK)</td></tr>
 <tr><td> s</td><td>Success flag (true/false)</td></tr>
 <tr><td>sc</td><td>Sample count (1, unless multiple samples are aggregated)</td></tr>
 <tr><td> t</td><td>Elapsed time (milliseconds)</td></tr>
 <tr><td>tn</td><td>Thread Name</td></tr>
 <tr><td>ts</td><td>timeStamp (milliseconds since midnight Jan 1, 1970 UTC)</td></tr>
 <tr><td>varname</td><td>Value of the named variable (versions of JMeter after 2.3.1)</td></tr>
 </table>
 <p>
 Versions 2.1 and 2.1.1 of JMeter saved the Response Code as "rs", but read it back expecting to find "rc".
 This has been corrected so that it is always saved as "rc"; either "rc" or "rs" can be read.
 </p>
 <note>
 Versions of JMeter after 2.3.1 allow additional variables to be saved with the test plan.
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
 <br/>
 Another solution is to use the Post-Processor <complink name="Save_Responses_to_a_file">Save Responses to a file</complink>.
 This generates a new file for each sample, and saves the file name with the sample.
 The file name can then be included in the sample log output.
 The data will be retrieved from the file if necessary when the sample log file is reloaded.
 </p>
 </section>
 <section name="&sect-num;.9 Loading (reading) response data" anchor="loading">
 <p>To view an existing results file, you can use the File "Browse..." button to select a file.
 If necessary, just create a dummy testplan with the appropriate Listener in it.
 </p>
 <p>Results can be read from XML or CSV format files.
 When reading from CSV results files, the header (if present) is used to determine which fields were saved.
 <b>In order to interpret a header-less CSV file correctly, the appropriate JMeter properties must be set.</b>
 </p>
 <note>
 Versions of JMeter up to 2.3.2 used to clear any current data before loading the new file.
 This is no longer done, thus allowing files to be merged.
 If the previous behaviour is required, 
 use the menu item Run/Clear (Ctrl+Shift+E) or Run/Clear All (Ctrl+E) before loading the file.
 </note>
 </section>
 <section name="&sect-num;.10 Saving Listener GUI data" anchor="screencap">
 <p>JMeter is capable of saving any listener as a PNG file. To do so, select the
 listener in the left panel. Click <b>Edit</b> &gt; <b>Save As Image</b>. A file dialog will
 appear. Enter the desired name and save the listener.
 </p>
 <p>
 The Listeners which generate output as tables can also be saved using Copy/Paste.
 Select the desired cells in the table, and use the OS Copy short-cut (normally Control+C).
 The data will be saved to the clipboard, from where it can be pasted into another application,
 e.g. a spreadsheet or text editor.
 </p>
 <figure image="save_image.png">Figure 1 - Edit &gt; Save As Image</figure>
 
 </section>
 </body>
 </document>
