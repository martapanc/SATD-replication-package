diff --git a/bin/jmeter.properties b/bin/jmeter.properties
index 059e0e30c..dcfa1a767 100644
--- a/bin/jmeter.properties
+++ b/bin/jmeter.properties
@@ -1,813 +1,817 @@
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
 
 # List of protocols to enable (unlikely to be needed):
 #https.socket.protocols=SSLv2Hello SSLv3 TLSv1
 
+# Control if we allow reuse of cached SSL context between iterations
+# set the value to 'false' to reset the SSL context each iteration
+#https.use.cached.ssl.context=true
+
 #---------------------------------------------------------------------------
 # Look and Feel configuration
 #---------------------------------------------------------------------------
 
 #Classname of the Swing default UI
 #Installed Look and Feel classes on Windows are:
 #  Metal   = javax.swing.plaf.metal.MetalLookAndFeel
 #  Motif   = com.sun.java.swing.plaf.motif.MotifLookAndFeel
 #  Windows = com.sun.java.swing.plaf.windows.WindowsLookAndFeel
 #
 #  Or one of the strings "System" or "CrossPlatform" which means
 #  JMeter will use the corresponding string returned by UIManager.get<name>LookAndFeelClassName()
 
 # LAF can be overridden by os.name (lowercased, spaces replaced by '_')
 # Sample os.name LAF:
 #jmeter.laf.windows_xp=javax.swing.plaf.metal.MetalLookAndFeel
 
 # Failing that, the OS family = os.name, but only up to first space:
 # Sample OS family LAF:
 #jmeter.laf.windows=javax.swing.plaf.metal.MetalLookAndFeel
 
 # Failing that:
 # default LAF
 jmeter.laf=javax.swing.plaf.metal.MetalLookAndFeel
 
 # Failing that, JMeter uses the CrossPlatform LAF.
 
 # Mac apparently looks better with the System LAF
 jmeter.laf.mac=System
 
 # Icon definitions
 # default:
 #jmeter.icons=org/apache/jmeter/images/icon.properties
 # alternate:
 #jmeter.icons=org/apache/jmeter/images/icon_1.properties
 
 #Components to not display in JMeter GUI (GUI class name or static label)
 # These elements are deprecated: HTML Parameter Mask,HTTP User Parameter Modifier
 not_in_menu=HTML Parameter Mask,HTTP User Parameter Modifier
 
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
 #         Logging Configuration
 #---------------------------------------------------------------------------
 
 # Note: JMeter uses Avalon (Excalibur) LogKit
 
 # Logging Format
 # see http://avalon.apache.org/logkit/api/org/apache/log/format/PatternFormatter.html
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
 #httpclient.parameters.file=httpclient.parameters
 
 
 # define a properties file for overriding Apache HttpClient parameters
 # See: TBA
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
 
 #---------------------------------------------------------------------------
 # Results file configuration
 #---------------------------------------------------------------------------
 
 # This section helps determine how result data will be saved.
 # The commented out values are the defaults.
 
 # legitimate values: xml, csv, db.  Only xml and csv are currently supported.
 #jmeter.save.saveservice.output_format=xml
 
 
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
 #jmeter.save.saveservice.timestamp_format=yyyy/MM/dd HH:mm:ss.SSSS
 
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
 
 #---------------------------------------------------------------------------
 # Settings that affect SampleResults
 #---------------------------------------------------------------------------
 
 # Save the start time stamp instead of the end
 # This also affects the timestamp stored in result files
 sampleresult.timestamp.start=true
 
 # Whether to use System.nanoTime() - otherwise only use System.currentTimeMillis()
 #sampleresult.useNanoTime=true
 
 #---------------------------------------------------------------------------
 # Upgrade property
 #---------------------------------------------------------------------------
 
 # File that holds a record of name changes for backward compatibility issues
 upgrade_properties=/bin/upgrade.properties
 
 #---------------------------------------------------------------------------
 # JMeter Proxy recorder configuration
 #---------------------------------------------------------------------------
 
 # If the proxy detects a gap of at least 1s (default) between HTTP requests,
 # it assumes that the user has clicked a new URL
 #proxy.pause=1000
 
 # Add numeric prefix to Sampler names (default false)
 #proxy.number.requests=true
 
 # Change the default HTTP Sampler
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
 # JMeter Proxy configuration
 #---------------------------------------------------------------------------
 # use command-line flags for user-name and password
 #http.proxyDomain=NTLM domain, if required by HTTPClient sampler
 
 # SSL configuration
 #proxy.cert.directory=.
 #proxy.cert.file=proxyserver.jks
 #proxy.cert.type=JKS
 #proxy.cert.keystorepass=password
 #proxy.cert.keypassword=password
 #proxy.cert.factory=SunX509
 #proxy.ssl.protocol=SSLv3
 
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
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.HtmlParserHTMLParser
 # Other parsers:
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.JTidyHTMLParser
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.RegexpHTMLParser
 #
 
 htmlParser.types=text/html application/xhtml+xml application/xml text/xml
 
 #---------------------------------------------------------------------------
 # WML Parser configuration
 #---------------------------------------------------------------------------
 
 wmlParser.className=org.apache.jmeter.protocol.http.parser.RegexpHTMLParser
 
 wmlParser.types=text/vnd.wap.wml 
 
 #---------------------------------------------------------------------------
 # Remote batching configuration
 #---------------------------------------------------------------------------
 
 # Remote batching support
 # default is Standard, which returns each sample
 # Hold retains samples until end of test (may need lots of memory)
 # Batch returns samples in batches
 # Statistical returns sample stats
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
 #hold_samples=true
 #num_sample_threshold=100
 #
 # Value is in milliseconds
 #time_threshold=60000
 
 # To set the Monitor Health Visualiser buffer size, enter the desired value
 # monitor.buffer.size=800
 
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
 # status.prefix and suffix = strings that enclose the status response code
 #tcp.status.prefix=Status=
 #tcp.status.suffix=.
 #
 # status.properties = property file to convert codes to messages
 #tcp.status.properties=mytestfiles/tcpstatus.properties
 
 #---------------------------------------------------------------------------
 # Summariser configuration (mainly applies to non-GUI mode)
 #---------------------------------------------------------------------------
 
 # Summariser settings
 #
 # Define the following property to automatically start a summariser with that name
 # (applies to non-GUI mode ony)
 #summariser.name=summary
 #
 # interval between summaries (in seconds) default 3 minutes
 #summariser.interval=180
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
 #time.YMDHMD=yyyyMMdd-HHmmss
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
 #mirror.server.port=8080
 
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
 # If this is disabled, it may be necessary to kill the JVM externally
 #jmeterengine.stopfail.system.exit=true
 
 # If running non-GUI, then JMeter listens on the following port for a shutdown message.
 # To disable, set the port to 1000 or less.
 #jmeterengine.nongui.port=4445
 #
 # If the initial port is busy, keep trying until this port is reached
 # (to disable searching, set the value less than or equal to the .port property)
 #jmeterengine.nongui.maxport=4455
 
 #Should JMeter expand the tree when loading a test plan?
 #onload.expandtree=true
 
 # Maximum size of HTML page that can be displayed; default=200 * 1024
 # Set to 0 to disable the size check
 #view.results.tree.max_size=0
 
 #JMS options
 # Enable the following property to stop JMS Point-to-Point Sampler from using
 # the properties java.naming.security.[principal|credentials] when creating the queue connection
 #JMSSampler.useSecurity.properties=false
 
 #---------------------------------------------------------------------------
 # Classpath configuration
 #---------------------------------------------------------------------------
 
 # List of paths (separated by ;) to search for additional JMeter extension classes
 # - for example new GUI elements and samplers
 # These are in addition to lib/ext. Do not use this for utility jars.
 #search_paths=/app1/lib;/app2/lib
 
 # Users can define additional classpath items by setting the property below
 # - for example, utility jars or JUnit test cases
 #
 # Use the default separator for the host version of Java
 # Paths with spaces may cause problems for the JVM
 #user.classpath=../classes;../jars/jar1.jar
 
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
\ No newline at end of file
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
index ddd916ea6..793412bee 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
@@ -1,254 +1,262 @@
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
 import java.net.URL;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 
 /**
  * Base class for HTTP implementations used by the HTTPSamplerProxy sampler.
  */
 public abstract class HTTPAbstractImpl implements Interruptible, HTTPConstantsInterface {
 
     protected final HTTPSamplerBase testElement;
 
     protected HTTPAbstractImpl(HTTPSamplerBase testElement){
         this.testElement = testElement;
     }
 
     protected abstract HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth);
 
     // Allows HTTPSamplerProxy to call threadFinished; subclasses can override if necessary
     protected void threadFinished() {
     }
 
     // Provide access to HTTPSamplerBase methods
     
     /**
      * Invokes {@link HTTPSamplerBase#errorResult(Throwable, HTTPSampleResult)}
      */
     protected HTTPSampleResult errorResult(Throwable t, HTTPSampleResult res) {
         return testElement.errorResult(t, res);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getArguments()}
      */
     protected Arguments getArguments() {
         return testElement.getArguments();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getAuthManager()}
      */
     protected AuthManager getAuthManager() {
         return testElement.getAuthManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getAutoRedirects()}
      */
     protected boolean getAutoRedirects() {
         return testElement.getAutoRedirects();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getCacheManager()}
      */
     protected CacheManager getCacheManager() {
         return testElement.getCacheManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getConnectTimeout()}
      */
     protected int getConnectTimeout() {
         return testElement.getConnectTimeout();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getContentEncoding()}
      */
     protected String getContentEncoding() {
         return testElement.getContentEncoding();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getCookieManager()}
      */
     protected CookieManager getCookieManager() {
         return testElement.getCookieManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getHeaderManager()}
      */
     protected HeaderManager getHeaderManager() {
         return testElement.getHeaderManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getHTTPFiles()}
      */
     protected HTTPFileArg[] getHTTPFiles() {
         return testElement.getHTTPFiles();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getIpSource()}
      */
     protected String getIpSource() {
         return testElement.getIpSource();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyHost()}
      */
     protected String getProxyHost() {
         return testElement.getProxyHost();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyPass()}
      */
     protected String getProxyPass() {
         return testElement.getProxyPass();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyPortInt()}
      */
     protected int getProxyPortInt() {
         return testElement.getProxyPortInt();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyUser()}
      */
     protected String getProxyUser() {
         return testElement.getProxyUser();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getResponseTimeout()}
      */
     protected int getResponseTimeout() {
         return testElement.getResponseTimeout();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getSendFileAsPostBody()}
      */
     protected boolean getSendFileAsPostBody() {
         return testElement.getSendFileAsPostBody();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getSendParameterValuesAsPostBody()}
      */
     protected boolean getSendParameterValuesAsPostBody() {
         return testElement.getSendParameterValuesAsPostBody();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getUseKeepAlive()}
      */
     protected boolean getUseKeepAlive() {
         return testElement.getUseKeepAlive();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getUseMultipartForPost()}
      */
     protected boolean getUseMultipartForPost() {
         return testElement.getUseMultipartForPost();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getDoBrowserCompatibleMultipart()}
      */
     protected boolean getDoBrowserCompatibleMultipart() {
         return testElement.getDoBrowserCompatibleMultipart();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#hasArguments()}
      */
     protected boolean hasArguments() {
         return testElement.hasArguments();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#isMonitor()}
      */
     protected boolean isMonitor() {
         return testElement.isMonitor();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#isSuccessCode(int)}
      */
     protected boolean isSuccessCode(int errorLevel) {
         return testElement.isSuccessCode(errorLevel);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, int)}
      */
     protected byte[] readResponse(SampleResult res, InputStream instream,
             int responseContentLength) throws IOException {
         return testElement.readResponse(res, instream, responseContentLength);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, int)}
      */
     protected byte[] readResponse(SampleResult res, BufferedInputStream in,
             int contentLength) throws IOException {
         return testElement.readResponse(res, in, contentLength);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#resultProcessing(boolean, int, HTTPSampleResult)}
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect,
             int frameDepth, HTTPSampleResult res) {
         return testElement.resultProcessing(areFollowingRedirect, frameDepth, res);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#setUseKeepAlive(boolean)}
      */
     protected void setUseKeepAlive(boolean b) {
         testElement.setUseKeepAlive(b);
     }
 
+    /**
+     * Called by testIterationStart if the SSL Context was reset.
+     * 
+     * This implementation does nothing.
+     */
+    protected void notifySSLContextWasReset() {
+        // NOOP
+    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
index e146d63ab..d8059c351 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
@@ -74,1030 +74,1047 @@ import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.LoopbackHttpClientSocketFactory;
 import org.apache.jmeter.protocol.http.util.SlowHttpClientSocketFactory;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * HTTP sampler using Apache (Jakarta) Commons HttpClient 3.1.
  */
 public class HTTPHC3Impl extends HTTPHCAbstractImpl {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String HTTP_AUTHENTICATION_PREEMPTIVE = "http.authentication.preemptive"; // $NON-NLS-1$
 
     private static final boolean canSetPreEmptive; // OK to set pre-emptive auth?
 
     private static final ThreadLocal<Map<HostConfiguration, HttpClient>> httpClients = 
         new ThreadLocal<Map<HostConfiguration, HttpClient>>(){
         @Override
         protected Map<HostConfiguration, HttpClient> initialValue() {
             return new HashMap<HostConfiguration, HttpClient>();
         }
     };
 
     // Needs to be accessible by HTTPSampler2
     volatile HttpClient savedClient;
 
     static {
         if (CPS_HTTP > 0) {
             log.info("Setting up HTTP SlowProtocol, cps="+CPS_HTTP);
             Protocol.registerProtocol(PROTOCOL_HTTP,
                     new Protocol(PROTOCOL_HTTP,new SlowHttpClientSocketFactory(CPS_HTTP),DEFAULT_HTTP_PORT));
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
 
         log.debug("Start : sample " + urlStr);
         log.debug("method " + method);
 
         HttpMethodBase httpMethod = null;
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(urlStr); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
 
         res.sampleStart(); // Count the retries as well in the time
         try {
             // May generate IllegalArgumentException
             if (method.equals(POST)) {
                 httpMethod = new PostMethod(urlStr);
             } else if (method.equals(PUT)){
                 httpMethod = new PutMethod(urlStr);
             } else if (method.equals(HEAD)){
                 httpMethod = new HeadMethod(urlStr);
             } else if (method.equals(TRACE)){
                 httpMethod = new TraceMethod(urlStr);
             } else if (method.equals(OPTIONS)){
                 httpMethod = new OptionsMethod(urlStr);
             } else if (method.equals(DELETE)){
                 httpMethod = new DeleteMethod(urlStr);
             } else if (method.equals(GET)){
                 httpMethod = new GetMethod(urlStr);
             } else {
                 throw new IllegalArgumentException("Unexpected method: "+method);
             }
 
             final CacheManager cacheManager = getCacheManager();
             if (cacheManager != null && GET.equalsIgnoreCase(method)) {
                if (cacheManager.inCache(url)) {
                    res.sampleEnd();
                    res.setResponseNoContent();
                    res.setSuccessful(true);
                    return res;
                }
             }
 
             // Set any default request headers
             setDefaultRequestHeaders(httpMethod);
 
             // Setup connection
             HttpClient client = setupConnection(url, httpMethod, res);
             savedClient = client;
 
             // Handle the various methods
             if (method.equals(POST)) {
                 String postBody = sendPostData((PostMethod)httpMethod);
                 res.setQueryString(postBody);
             } else if (method.equals(PUT)) {
                 String putBody = sendPutData((PutMethod)httpMethod);
                 res.setQueryString(putBody);
             }
 
             int statusCode = client.executeMethod(httpMethod);
 
             // Needs to be done after execute to pick up all the headers
             res.setRequestHeaders(getConnectionHeaders(httpMethod));
 
             // Request sent. Now get the response:
             InputStream instream = httpMethod.getResponseBodyAsStream();
 
             if (instream != null) {// will be null for HEAD
                 instream = new CountingInputStream(instream);
                 try {
                     Header responseHeader = httpMethod.getResponseHeader(HEADER_CONTENT_ENCODING);
                     if (responseHeader!= null && ENCODING_GZIP.equals(responseHeader.getValue())) {
                         InputStream tmpInput = new GZIPInputStream(instream); // tmp inputstream needs to have a good counting
                         res.setResponseData(readResponse(res, tmpInput, (int) httpMethod.getResponseContentLength()));                        
                     } else {
                         res.setResponseData(readResponse(res, instream, (int) httpMethod.getResponseContentLength()));
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
             Header h = httpMethod.getResponseHeader(HEADER_CONTENT_TYPE);
             if (h != null)// Can be missing, e.g. on redirect
             {
                 ct = h.getValue();
                 res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                 res.setEncodingAndType(ct);
             }
 
             res.setResponseHeaders(getResponseHeaders(httpMethod));
             if (res.isRedirect()) {
                 final Header headerLocation = httpMethod.getResponseHeader(HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header");
                 }
                 res.setRedirectLocation(headerLocation.getValue());
             }
 
             // record some sizes to allow HTTPSampleResult.getBytes() with different options
             if (instream != null) {
                 res.setBodySize(((CountingInputStream) instream).getCount());
             }
             res.setHeadersSize(calculateHeadersSize(httpMethod));
             if (log.isDebugEnabled()) {
                 log.debug("Response headersSize=" + res.getHeadersSize() + " bodySize=" + res.getBodySize()
                         + " Total=" + (res.getHeadersSize() + res.getBodySize()));
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
         } catch (IllegalArgumentException e)// e.g. some kinds of invalid URL
         {
             res.sampleEnd();
             HTTPSampleResult err = errorResult(e, res);
             err.setSampleLabel("Error: " + url.toString());
             return err;
         } catch (IOException e) {
             res.sampleEnd();
             HTTPSampleResult err = errorResult(e, res);
             err.setSampleLabel("Error: " + url.toString());
             return err;
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
         for (int i = 0; i < rh.length; i++) {
             headerSize += (rh[i]).toString().length(); // already include the \r\n
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
             schema = PROTOCOL_HTTP;
         }
 
         if (PROTOCOL_HTTPS.equalsIgnoreCase(schema)){
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
         if (localAddress != null){
             hc.setLocalAddress(localAddress);
         } else {
             final String ipSource = getIpSource();
             if (ipSource.length() > 0) {// Use special field ip source address (for pseudo 'ip spoofing')
                 InetAddress inetAddr = InetAddress.getByName(ipSource);
                 hc.setLocalAddress(inetAddr);
             }
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
 
         if ( httpClient == null )
         {
             httpClient = new HttpClient(new SimpleHttpConnectionManager());
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
             httpMethod.setRequestHeader(HEADER_CONNECTION, KEEP_ALIVE);
         } else {
             httpMethod.setRequestHeader(HEADER_CONNECTION, CONNECTION_CLOSE);
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
         org.apache.commons.httpclient.Header rh[] = method.getResponseHeaders();
         headerBuf.append(method.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (int i = 0; i < rh.length; i++) {
             String key = rh[i].getName();
             headerBuf.append(key);
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(rh[i].getValue());
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
                 method.setRequestHeader(HEADER_COOKIE, cookieHeader);
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
                 PropertyIterator i = headers.iterator();
                 while (i.hasNext()) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                        i.next().getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // This helps with SoapSampler hack too
                     // TODO - what other headers are not allowed?
                     if (! HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         if (HEADER_HOST.equalsIgnoreCase(n)) {
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
         for(int i = 0; i < requestHeaders.length; i++) {
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if(!HEADER_COOKIE.equalsIgnoreCase(requestHeaders[i].getName())) {
                 hdrs.append(requestHeaders[i].getName());
                 hdrs.append(": "); // $NON-NLS-1$
                 hdrs.append(requestHeaders[i].getValue());
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
         HTTPFileArg files[] = getHTTPFiles();
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(getUseMultipartForPost()) {
             // If a content encoding is specified, we use that as the
             // encoding of any parameter values
             String contentEncoding = getContentEncoding();
             if(contentEncoding != null && contentEncoding.length() == 0) {
                 contentEncoding = null;
             }
 
             final boolean browserCompatible = getDoBrowserCompatibleMultipart();
             // We don't know how many entries will be skipped
             ArrayList<PartBase> partlist = new ArrayList<PartBase>();
             // Create the parts
             // Add any parameters
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 String parameterName = arg.getName();
                 if (arg.isSkippable(parameterName)){
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
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 File inputFile = new File(file.getPath());
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
             post.setRequestHeader(HEADER_CONTENT_TYPE, multiPartContentType);
 
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
             Header contentTypeHeader = post.getRequestHeader(HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
             // If there are no arguments, we can send a file as the body of the request
             // TODO: needs a multiple file upload scenerio
             if(!hasArguments() && getSendFileAsPostBody()) {
                 // If getSendFileAsPostBody returned true, it's sure that file is not null
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         post.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
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
                 if(contentEncoding != null && contentEncoding.trim().length() > 0) {
                     post.getParams().setContentCharset(contentEncoding);
                     haveContentEncoding = true;
                 } else if (contentEncoding != null && contentEncoding.trim().length() == 0){
                     contentEncoding=null;
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
                             post.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                              // TODO - is this the correct default?
                             post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBody = new StringBuilder();
                     PropertyIterator args = getArguments().iterator();
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         String value;
                         if (haveContentEncoding){
                             value = arg.getEncodedValue(contentEncoding);
                         } else {
                             value = arg.getEncodedValue();
                         }
                         postBody.append(value);
                     }
                     StringRequestEntity requestEntity = new StringRequestEntity(postBody.toString(), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), contentEncoding);
                     post.setRequestEntity(requestEntity);
                 }
                 else {
                     // It is a normal post request, with parameter names and values
 
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                     // Add the parameters
                     PropertyIterator args = getArguments().iterator();
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
                             String urlContentEncoding = contentEncoding;
                             if(urlContentEncoding == null || urlContentEncoding.length() == 0) {
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
         post.setRequestHeader(HEADER_CONTENT_LENGTH, Long.toString(post.getRequestEntity().getContentLength()));
 
         return postedBody.toString();
     }
 
     /**
      * Set up the PUT data
      */
     private String sendPutData(PutMethod put) throws IOException {
         // Buffer to hold the put body, except file content
         StringBuilder putBody = new StringBuilder(1000);
         boolean hasPutBody = false;
 
         // Check if the header manager had a content type header
         // This allows the user to specify his own content-type for a POST request
         Header contentTypeHeader = put.getRequestHeader(HEADER_CONTENT_TYPE);
         boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
         HTTPFileArg files[] = getHTTPFiles();
 
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasPutBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(files[0].getPath()),null);
             put.setRequestEntity(fileRequestEntity);
 
             // We just add placeholder text for file content
             putBody.append("<actual file content, not shown here>");
         }
         // If none of the arguments have a name specified, we
         // just send all the values as the put body
         else if(getSendParameterValuesAsPostBody()) {
             hasPutBody = true;
 
             // If a content encoding is specified, we set it as http parameter, so that
             // the post body will be encoded in the specified content encoding
             final String contentEncoding = getContentEncoding();
             boolean haveContentEncoding = false;
             if(contentEncoding != null && contentEncoding.trim().length() > 0) {
                 put.getParams().setContentCharset(contentEncoding);
                 haveContentEncoding = true;
             }
 
             // Just append all the parameter values, and use that as the post body
             StringBuilder putBodyContent = new StringBuilder();
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 String value = null;
                 if (haveContentEncoding){
                     value = arg.getEncodedValue(contentEncoding);
                 } else {
                     value = arg.getEncodedValue();
                 }
                 putBodyContent.append(value);
             }
             String contentTypeValue = null;
             if(hasContentTypeHeader) {
                 contentTypeValue = put.getRequestHeader(HEADER_CONTENT_TYPE).getValue();
             }
             StringRequestEntity requestEntity = new StringRequestEntity(putBodyContent.toString(), contentTypeValue, put.getRequestCharSet());
             put.setRequestEntity(requestEntity);
         }
         // Check if we have any content to send for body
         if(hasPutBody) {
             // If the request entity is repeatable, we can send it first to
             // our own stream, so we can return it
             if(put.getRequestEntity().isRepeatable()) {
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 put.getRequestEntity().writeRequest(bos);
                 bos.flush();
                 // We get the posted bytes using the charset that was used to create them
                 putBody.append(new String(bos.toByteArray(),put.getRequestCharSet()));
                 bos.close();
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
                     put.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                 }
             }
             // Set the content length
             put.setRequestHeader(HEADER_CONTENT_LENGTH, Long.toString(put.getRequestEntity().getContentLength()));
             return putBody.toString();
         }
         return null;
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
             Header hdr[] = method.getResponseHeaders(HEADER_SET_COOKIE);
             for (int i = 0; i < hdr.length; i++) {
                 cookieManager.addCookieFromHeader(hdr[i].getValue(),u);
             }
         }
     }
 
 
     @Override
     public void threadFinished() {
         log.debug("Thread Finished");
 
+        closeThreadLocalConnections();
+    }
+
+
+    /**
+     * 
+     */
+    private void closeThreadLocalConnections() {
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
 
+    /** 
+     * {@inheritDoc}
+     * This implementation closes all local connections.
+     */
+    @Override
+    protected void notifySSLContextWasReset() {
+        log.debug("freeThreadConnections called");
+        closeThreadLocalConnections();
+    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
index e69d83af3..c763c0b5d 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
@@ -50,1024 +50,1037 @@ import org.apache.http.auth.NTCredentials;
 import org.apache.http.auth.UsernamePasswordCredentials;
 import org.apache.http.client.CredentialsProvider;
 import org.apache.http.client.HttpClient;
 import org.apache.http.client.HttpRequestRetryHandler;
 import org.apache.http.client.entity.UrlEncodedFormEntity;
 import org.apache.http.client.methods.HttpDelete;
 import org.apache.http.client.methods.HttpGet;
 import org.apache.http.client.methods.HttpHead;
 import org.apache.http.client.methods.HttpOptions;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.client.methods.HttpPut;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.http.client.methods.HttpTrace;
 import org.apache.http.client.methods.HttpUriRequest;
 import org.apache.http.client.params.ClientPNames;
 import org.apache.http.client.protocol.ResponseContentEncoding;
 import org.apache.http.conn.params.ConnRoutePNames;
 import org.apache.http.conn.scheme.Scheme;
 import org.apache.http.conn.scheme.SchemeRegistry;
 import org.apache.http.entity.FileEntity;
 import org.apache.http.entity.StringEntity;
 import org.apache.http.entity.mime.FormBodyPart;
 import org.apache.http.entity.mime.HttpMultipartMode;
 import org.apache.http.entity.mime.MultipartEntity;
 import org.apache.http.entity.mime.content.FileBody;
 import org.apache.http.entity.mime.content.StringBody;
 import org.apache.http.impl.client.AbstractHttpClient;
 import org.apache.http.impl.client.DefaultHttpClient;
 import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
 import org.apache.http.message.BasicNameValuePair;
 import org.apache.http.params.BasicHttpParams;
 import org.apache.http.params.CoreConnectionPNames;
 import org.apache.http.params.CoreProtocolPNames;
 import org.apache.http.params.DefaultedHttpParams;
 import org.apache.http.params.HttpParams;
 import org.apache.http.protocol.BasicHttpContext;
 import org.apache.http.protocol.ExecutionContext;
 import org.apache.http.protocol.HttpContext;
 
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HC4TrustAllSSLSocketFactory;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.SlowHC4SSLSocketFactory;
 import org.apache.jmeter.protocol.http.util.SlowHC4SocketFactory;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HTTP Sampler using Apache HttpClient 4.x.
  *
  */
 public class HTTPHC4Impl extends HTTPHCAbstractImpl {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** retry count to be used; defaults to 0 = disable retries */
     private static final int RETRY_COUNT = JMeterUtils.getPropDefault("httpclient4.retrycount", 0);
 
     private static final String CONTEXT_METRICS = "jmeter_metrics"; // TODO hack, to be removed later
 
     private static final HttpResponseInterceptor METRICS_SAVER = new HttpResponseInterceptor(){
         public void process(HttpResponse response, HttpContext context)
                 throws HttpException, IOException {
             HttpConnection conn = (HttpConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION);
             HttpConnectionMetrics metrics = conn.getMetrics();
             context.setAttribute(CONTEXT_METRICS, metrics);
         }
     };
 
     private static final ThreadLocal<Map<HttpClientKey, HttpClient>> HTTPCLIENTS = 
         new ThreadLocal<Map<HttpClientKey, HttpClient>>(){
         @Override
         protected Map<HttpClientKey, HttpClient> initialValue() {
             return new HashMap<HttpClientKey, HttpClient>();
         }
     };
 
     // Scheme used for slow HTTP sockets. Cannot be set as a default, because must be set on an HttpClient instance.
     private static final Scheme SLOW_HTTP;
     
     // We always want to override the HTTPS scheme, because we want to trust all certificates and hosts
     private static final Scheme HTTPS_SCHEME;
 
     /*
      * Create a set of default parameters from the ones initially created.
      * This allows the defaults to be overridden if necessary from the properties file.
      */
     private static final HttpParams DEFAULT_HTTP_PARAMS;
     
     static {
         
         // TODO use new setDefaultHttpParams(HttpParams params) static method when 4.1 is available
         final DefaultHttpClient dhc = new DefaultHttpClient();
         DEFAULT_HTTP_PARAMS = dhc.getParams(); // Get the default params
         dhc.getConnectionManager().shutdown(); // Tidy up
         
         // Process Apache HttpClient parameters file
         String file=JMeterUtils.getProperty("hc.parameters.file"); // $NON-NLS-1$
         if (file != null) {
             HttpClientDefaultParameters.load(file, DEFAULT_HTTP_PARAMS);
         }
 
         // Set up HTTP scheme override if necessary
         if (CPS_HTTP > 0) {
             log.info("Setting up HTTP SlowProtocol, cps="+CPS_HTTP);
             SLOW_HTTP = new Scheme(PROTOCOL_HTTP, DEFAULT_HTTP_PORT, new SlowHC4SocketFactory(CPS_HTTP));
         } else {
             SLOW_HTTP = null;
         }
         
         // We always want to override the HTTPS scheme
         Scheme https = null;
         if (CPS_HTTPS > 0) {
             log.info("Setting up HTTPS SlowProtocol, cps="+CPS_HTTPS);
             try {
                 https = new Scheme(PROTOCOL_HTTPS, DEFAULT_HTTPS_PORT, new SlowHC4SSLSocketFactory(CPS_HTTPS));
             } catch (GeneralSecurityException e) {
                 log.warn("Failed to initialise SLOW_HTTPS scheme, cps="+CPS_HTTPS, e);
             }
         } else {
             log.info("Setting up HTTPS TrustAll scheme");
             try {
                 https = new Scheme(PROTOCOL_HTTPS, DEFAULT_HTTPS_PORT, new HC4TrustAllSSLSocketFactory());
             } catch (GeneralSecurityException e) {
                 log.warn("Failed to initialise HTTPS TrustAll scheme", e);
             }
         }
         HTTPS_SCHEME = https;
         if (localAddress != null){
             DEFAULT_HTTP_PARAMS.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
         }
         
     }
 
     private volatile HttpUriRequest currentRequest; // Accessed from multiple threads
 
     protected HTTPHC4Impl(HTTPSamplerBase testElement) {
         super(testElement);
     }
 
     @Override
     protected HTTPSampleResult sample(URL url, String method,
             boolean areFollowingRedirect, int frameDepth) {
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(url.toString()); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
 
         HttpClient httpClient = setupClient(url);
         
         HttpRequestBase httpRequest = null;
         try {
             URI uri = url.toURI();
             if (method.equals(POST)) {
                 httpRequest = new HttpPost(uri);
             } else if (method.equals(PUT)) {
                 httpRequest = new HttpPut(uri);
             } else if (method.equals(HEAD)) {
                 httpRequest = new HttpHead(uri);
             } else if (method.equals(TRACE)) {
                 httpRequest = new HttpTrace(uri);
             } else if (method.equals(OPTIONS)) {
                 httpRequest = new HttpOptions(uri);
             } else if (method.equals(DELETE)) {
                 httpRequest = new HttpDelete(uri);
             } else if (method.equals(GET)) {
                 httpRequest = new HttpGet(uri);
             } else {
                 throw new IllegalArgumentException("Unexpected method: "+method);
             }
             setupRequest(url, httpRequest, res); // can throw IOException
         } catch (Exception e) {
             res.sampleStart();
             res.sampleEnd();
             HTTPSampleResult err = errorResult(e, res);
             err.setSampleLabel("Error: " + url.toString());
             return err;
         }
 
         HttpContext localContext = new BasicHttpContext();
 
         res.sampleStart();
 
         final CacheManager cacheManager = getCacheManager();
         if (cacheManager != null && GET.equalsIgnoreCase(method)) {
            if (cacheManager.inCache(url)) {
                res.sampleEnd();
                res.setResponseNoContent();
                res.setSuccessful(true);
                return res;
            }
         }
 
         try {
             currentRequest = httpRequest;
             // Handle the various methods
             if (method.equals(POST)) {
                 String postBody = sendPostData((HttpPost)httpRequest);
                 res.setQueryString(postBody);
             } else if (method.equals(PUT)) {
                 String putBody = sendPutData((HttpPut)httpRequest);
                 res.setQueryString(putBody);
             }
             HttpResponse httpResponse = httpClient.execute(httpRequest, localContext); // perform the sample
 
             // Needs to be done after execute to pick up all the headers
             res.setRequestHeaders(getConnectionHeaders((HttpRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST)));
 
             Header contentType = httpResponse.getLastHeader(HEADER_CONTENT_TYPE);
             if (contentType != null){
                 String ct = contentType.getValue();
                 res.setContentType(ct);
                 res.setEncodingAndType(ct);                    
             }
             HttpEntity entity = httpResponse.getEntity();
             if (entity != null) {
                 InputStream instream = entity.getContent();
                 res.setResponseData(readResponse(res, instream, (int) entity.getContentLength()));
             }
             
             res.sampleEnd(); // Done with the sampling proper.
             currentRequest = null;
 
             // Now collect the results into the HTTPSampleResult:
             StatusLine statusLine = httpResponse.getStatusLine();
             int statusCode = statusLine.getStatusCode();
             res.setResponseCode(Integer.toString(statusCode));
             res.setResponseMessage(statusLine.getReasonPhrase());
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseHeaders(getResponseHeaders(httpResponse));
             if (res.isRedirect()) {
                 final Header headerLocation = httpResponse.getLastHeader(HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header");
                 }
                 res.setRedirectLocation(headerLocation.getValue());
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
             if (log.isDebugEnabled()) {
                 log.debug("ResponseHeadersSize=" + res.getHeadersSize() + " Content-Length=" + res.getBodySize()
                         + " Total=" + (res.getHeadersSize() + res.getBodySize()));
             }
 
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST);
                 HttpHost target = (HttpHost) localContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
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
             res.sampleEnd();
             HTTPSampleResult err = errorResult(e, res);
             err.setSampleLabel("Error: " + url.toString());
             return err;
         } catch (RuntimeException e) {
             res.sampleEnd();
             HTTPSampleResult err = errorResult(e, res);
             err.setSampleLabel("Error: " + url.toString());
             return err;
         } finally {
             currentRequest = null;
         }
         return res;
     }
 
     /**
      * Holder class for all fields that define an HttpClient instance;
      * used as the key to the ThreadLocal map of HttpClient instances.
      */
     private static final class HttpClientKey {
 
         private final URL url;
         private final boolean hasProxy;
         private final String proxyHost;
         private final int proxyPort;
         private final String proxyUser;
         private final String proxyPass;
         
         private final int hashCode; // Always create hash because we will always need it
 
         public HttpClientKey(URL url, boolean b, String proxyHost,
                 int proxyPort, String proxyUser, String proxyPass) {
             this.url = url;
             this.hasProxy = b;
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
             hash = hash*31 + url.toString().hashCode();
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
                 this.url.toString().equals(other.url.toString());                
             }
             // No proxy, so don't check proxy fields
             return 
                 this.hasProxy == other.hasProxy &&
                 this.url.toString().equals(other.url.toString())
             ;
             
         }
 
         @Override
         public int hashCode(){
             return hashCode;
         }
     }
 
     private HttpClient setupClient(URL url) {
 
         Map<HttpClientKey, HttpClient> map = HTTPCLIENTS.get();
         
         final String host = url.getHost();
         final String proxyHost = getProxyHost();
         final int proxyPort = getProxyPortInt();
 
         boolean useStaticProxy = isStaticProxy(host);
         boolean useDynamicProxy = isDynamicProxy(proxyHost, proxyPort);
 
         // Lookup key - must agree with all the values used to create the HttpClient.
         HttpClientKey key = new HttpClientKey(url, (useStaticProxy || useDynamicProxy), 
                 useDynamicProxy ? proxyHost : PROXY_HOST,
                 useDynamicProxy ? proxyPort : PROXY_PORT,
                 useDynamicProxy ? getProxyUser() : PROXY_USER,
                 useDynamicProxy ? getProxyPass() : PROXY_PASS);
         
         HttpClient httpClient = map.get(key);
 
         if (httpClient == null){ // One-time init for this client
 
             HttpParams clientParams = new DefaultedHttpParams(new BasicHttpParams(), DEFAULT_HTTP_PARAMS);
             
             httpClient = new DefaultHttpClient(clientParams){
                 @Override
                 protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
                     return new DefaultHttpRequestRetryHandler(RETRY_COUNT, false); // set retry count
                 }
             };
             ((AbstractHttpClient) httpClient).addResponseInterceptor(new ResponseContentEncoding());
             ((AbstractHttpClient) httpClient).addResponseInterceptor(METRICS_SAVER); // HACK
             
             // Override the defualt schemes as necessary
             SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
 
             if (SLOW_HTTP != null){
                 schemeRegistry.register(SLOW_HTTP);
             }
 
             if (HTTPS_SCHEME != null){
                 schemeRegistry.register(HTTPS_SCHEME);
             }
 
             // Set up proxy details
             if (useDynamicProxy){
                 HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 String proxyUser = getProxyUser();
                 if (proxyUser.length() > 0) {
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(proxyHost, proxyPort),
                             new UsernamePasswordCredentials(proxyUser, getProxyPass()));
                 }
             } else if (useStaticProxy) {
                 HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 if (PROXY_USER.length() > 0)
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(PROXY_HOST, PROXY_PORT),
                             new UsernamePasswordCredentials(PROXY_USER, PROXY_PASS));
             }
             
             if (log.isDebugEnabled()) {
                 log.debug("Created new HttpClient: @"+System.identityHashCode(httpClient));
             }
 
             map.put(key, httpClient); // save the agent for next time round
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Reusing the HttpClient: @"+System.identityHashCode(httpClient));
             }
         }
 
         // TODO - should this be done when the client is created?
         // If so, then the details need to be added as part of HttpClientKey
         setConnectionAuthorization(httpClient, url, getAuthManager());
 
         return httpClient;
     }
 
     private void setupRequest(URL url, HttpRequestBase httpRequest, HTTPSampleResult res)
         throws IOException {
 
     HttpParams requestParams = httpRequest.getParams();
     
     // Set up the local address if one exists
     final String ipSource = getIpSource();
     if (ipSource.length() > 0) {// Use special field ip source address (for pseudo 'ip spoofing')
         InetAddress inetAddr = InetAddress.getByName(ipSource);
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
         httpRequest.setHeader(HEADER_CONNECTION, KEEP_ALIVE);
     } else {
         httpRequest.setHeader(HEADER_CONNECTION, CONNECTION_CLOSE);
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
      * @return string containing the headers, one per line
      */
     private String getResponseHeaders(HttpResponse response) {
         StringBuilder headerBuf = new StringBuilder();
         Header[] rh = response.getAllHeaders();
         headerBuf.append(response.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (int i = 0; i < rh.length; i++) {
             headerBuf.append(rh[i].getName());
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(rh[i].getValue());
             headerBuf.append("\n"); // $NON-NLS-1$
         }
         return headerBuf.toString();
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
     private String setConnectionCookie(HttpRequest request, URL url, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(url);
             if (cookieHeader != null) {
                 request.setHeader(HEADER_COOKIE, cookieHeader);
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
     private void setConnectionHeaders(HttpRequestBase request, URL url, HeaderManager headerManager, CacheManager cacheManager) {
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 PropertyIterator i = headers.iterator();
                 while (i.hasNext()) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                        i.next().getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // TODO - what other headers are not allowed?
                     if (! HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         if (HEADER_HOST.equalsIgnoreCase(n)) {
                             int port = url.getPort();
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
      * Get all the request headers for the <code>HttpMethod</code>
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @return the headers as a string
      */
     private String getConnectionHeaders(HttpRequest method) {
         // Get all the request headers
         StringBuilder hdrs = new StringBuilder(100);
         Header[] requestHeaders = method.getAllHeaders();
         for(int i = 0; i < requestHeaders.length; i++) {
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if(!HEADER_COOKIE.equalsIgnoreCase(requestHeaders[i].getName())) {
                 hdrs.append(requestHeaders[i].getName());
                 hdrs.append(": "); // $NON-NLS-1$
                 hdrs.append(requestHeaders[i].getValue());
                 hdrs.append("\n"); // $NON-NLS-1$
             }
         }
 
         return hdrs.toString();
     }
 
     private void setConnectionAuthorization(HttpClient client, URL url, AuthManager authManager) {
         CredentialsProvider credentialsProvider = 
             ((AbstractHttpClient) client).getCredentialsProvider();
         if (authManager != null) {
             Authorization auth = authManager.getAuthForURL(url);
             if (auth != null) {
                     String username = auth.getUser();
                     String realm = auth.getRealm();
                     String domain = auth.getDomain();
                     if (log.isDebugEnabled()){
                         log.debug(username + " > D="+domain+" R="+realm);
                     }
                     credentialsProvider.setCredentials(
                             new AuthScope(url.getHost(), url.getPort(), realm.length()==0 ? null : realm),
                             new NTCredentials(username, auth.getPass(), localHost, domain));
             } else {
                 credentialsProvider.clear();
             }
         } else {
             credentialsProvider.clear();            
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
     private String sendPostData(HttpPost post)  throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         HTTPFileArg files[] = getHTTPFiles();
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(getUseMultipartForPost()) {
             // If a content encoding is specified, we use that as the
             // encoding of any parameter values
             String contentEncoding = getContentEncoding();
             if(contentEncoding != null && contentEncoding.length() == 0) {
                 contentEncoding = null;
             }
 
             // Write the request to our own stream
             MultipartEntity multiPart = new MultipartEntity(
                     getDoBrowserCompatibleMultipart() ? HttpMultipartMode.BROWSER_COMPATIBLE : HttpMultipartMode.STRICT);
             // Create the parts
             // Add any parameters
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                String parameterName = arg.getName();
                if (arg.isSkippable(parameterName)){
                    continue;
                }
                FormBodyPart formPart;
                StringBody stringBody = new StringBody(arg.getValue(),
                        Charset.forName(contentEncoding == null ? "US-ASCII" : contentEncoding));
                formPart = new FormBodyPart(arg.getName(), stringBody);                   
                multiPart.addPart(formPart);
             }
 
             // Add any files
             // Cannot retrieve parts once added to the MultiPartEntity, so have to save them here.
             ViewableFileBody[] fileBodies = new ViewableFileBody[files.length];
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 fileBodies[i] = new ViewableFileBody(new File(file.getPath()), file.getMimeType());
                 multiPart.addPart(file.getParamName(),fileBodies[i]);
             }
 
             post.setEntity(multiPart);
 
             if (multiPart.isRepeatable()){
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = true;
                 }
                 multiPart.writeTo(bos);
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = false;
                 }
                 bos.flush();
                 // We get the posted bytes using the encoding used to create it
                 postedBody.append(new String(bos.toByteArray(),
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
             Header contentTypeHeader = post.getFirstHeader(HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
             // If there are no arguments, we can send a file as the body of the request
             // TODO: needs a multiple file upload scenerio
             if(!hasArguments() && getSendFileAsPostBody()) {
                 // If getSendFileAsPostBody returned true, it's sure that file is not null
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         post.setHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         post.setHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
 
                 FileEntity fileRequestEntity = new FileEntity(new File(file.getPath()),null);
                 post.setEntity(fileRequestEntity);
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>");
             } else {
                 // In a post request which is not multipart, we only support
                 // parameters, no file upload is allowed
 
                 // If a content encoding is specified, we set it as http parameter, so that
                 // the post body will be encoded in the specified content encoding
                 String contentEncoding = getContentEncoding();
                 boolean haveContentEncoding = false;
                 if(contentEncoding != null && contentEncoding.trim().length() > 0) {
                     post.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, contentEncoding);
                     haveContentEncoding = true;
                 } else if (contentEncoding != null && contentEncoding.trim().length() == 0){
                     contentEncoding=null;
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
                             post.setHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                              // TODO - is this the correct default?
                             post.setHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBody = new StringBuilder();
                     PropertyIterator args = getArguments().iterator();
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         String value;
                         if (haveContentEncoding){
                             value = arg.getEncodedValue(contentEncoding);
                         } else {
                             value = arg.getEncodedValue();
                         }
                         postBody.append(value);
                     }
                     StringEntity requestEntity = new StringEntity(postBody.toString(), post.getFirstHeader(HEADER_CONTENT_TYPE).getValue(), contentEncoding);
                     post.setEntity(requestEntity);
                     postedBody.append(postBody.toString()); // TODO OK?
                 } else {
                     // It is a normal post request, with parameter names and values
 
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         post.setHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                     // Add the parameters
                     PropertyIterator args = getArguments().iterator();
                     List <NameValuePair> nvps = new ArrayList <NameValuePair>();
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
                         if (contentEncoding != null) {
                             postedBody.append(new String(bos.toByteArray(), contentEncoding));
                         } else {
                             postedBody.append(new String(bos.toByteArray()));
                         }
                         bos.close();
                     }  else {
                         postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                     }
                 }
 
 //                // If the request entity is repeatable, we can send it first to
 //                // our own stream, so we can return it
 //                if(post.getEntity().isRepeatable()) {
 //                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
 //                    post.getEntity().writeTo(bos);
 //                    bos.flush();
 //                    // We get the posted bytes using the encoding used to create it
 //                    if (contentEncoding != null) {
 //                        postedBody.append(new String(bos.toByteArray(), contentEncoding));
 //                    } else {
 //                        postedBody.append(new String(bos.toByteArray()));
 //                    }
 //                    bos.close();
 //                }
 //                else {
 //                    postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
 //                }
             }
         }
         return postedBody.toString();
     }
 
     // TODO - implementation not fully tested
     private String sendPutData(HttpPut put) throws IOException {
         // Buffer to hold the put body, except file content
         StringBuilder putBody = new StringBuilder(1000);
         boolean hasPutBody = false;
 
         // Check if the header manager had a content type header
         // This allows the user to specify his own content-type
         Header contentTypeHeader = put.getFirstHeader(HEADER_CONTENT_TYPE);
         boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
 
         // Check for local contentEncoding override
         final String contentEncoding = getContentEncoding();
         boolean haveContentEncoding = (contentEncoding != null && contentEncoding.trim().length() > 0);
         
         HttpParams putParams = put.getParams();
         HTTPFileArg files[] = getHTTPFiles();
 
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasPutBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             FileEntity fileRequestEntity = new FileEntity(new File(files[0].getPath()),null);
             put.setEntity(fileRequestEntity);
 
             // We just add placeholder text for file content
             putBody.append("<actual file content, not shown here>");
         }
         // If none of the arguments have a name specified, we
         // just send all the values as the put body
         else if(getSendParameterValuesAsPostBody()) {
             hasPutBody = true;
 
             // If a content encoding is specified, we set it as http parameter, so that
             // the post body will be encoded in the specified content encoding
             if(haveContentEncoding) {
                 putParams.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,contentEncoding);
             }
 
             // Just append all the parameter values, and use that as the post body
             StringBuilder putBodyContent = new StringBuilder();
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 String value = null;
                 if (haveContentEncoding){
                     value = arg.getEncodedValue(contentEncoding);
                 } else {
                     value = arg.getEncodedValue();
                 }
                 putBodyContent.append(value);
             }
             String contentTypeValue = null;
             if(hasContentTypeHeader) {
                 contentTypeValue = put.getFirstHeader(HEADER_CONTENT_TYPE).getValue();
             }
             StringEntity requestEntity = new StringEntity(putBodyContent.toString(), contentTypeValue, 
                     (String) putParams.getParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET));
             put.setEntity(requestEntity);
         }
         // Check if we have any content to send for body
         if(hasPutBody) {
             // If the request entity is repeatable, we can send it first to
             // our own stream, so we can return it
             if(put.getEntity().isRepeatable()) {
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 put.getEntity().writeTo(bos);
                 bos.flush();
                 // We get the posted bytes using the charset that was used to create them
                 putBody.append(new String(bos.toByteArray(),
                         (String) putParams.getParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET)));
                 bos.close();
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
                     put.setHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                 }
             }
             return putBody.toString();
         }
         return null;
     }
 
     private void saveConnectionCookies(HttpResponse method, URL u, CookieManager cookieManager) {
         if (cookieManager != null) {
             Header[] hdrs = method.getHeaders(HEADER_SET_COOKIE);
             for (Header hdr : hdrs) {
                 cookieManager.addCookieFromHeader(hdr.getValue(),u);
             }
         }
     }
 
     @Override
     public void threadFinished() {
         log.debug("Thread Finished");
+        closeThreadLocalConnections();
+    }
+
+    /**
+     * 
+     */
+    private void closeThreadLocalConnections() {
         // Does not need to be synchronised, as all access is from same thread
         Map<HttpClientKey, HttpClient> map = HTTPCLIENTS.get();
         if ( map != null ) {
             for ( HttpClient cl : map.values() ) {
                 cl.getConnectionManager().shutdown();
             }
             map.clear();
         }
     }
 
     public boolean interrupt() {
         HttpUriRequest request = currentRequest;
         if (request != null) {
             currentRequest = null;
             try {
                 request.abort();
             } catch (UnsupportedOperationException e) {
                 log.warn("Could not abort pending request", e);
             }
         }
         return request != null;
     }
-
+    
+    /** {@inheritDoc} */
+    @Override
+    protected void notifySSLContextWasReset() {
+        log.debug("closeThreadLocalConnections called");
+        closeThreadLocalConnections();
+    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
index e4ebcab68..ab80ebb51 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
@@ -1,76 +1,85 @@
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
 
 
 import java.io.IOException;
 import java.net.URL;
 
 import org.apache.commons.httpclient.HttpClient;
 import org.apache.commons.httpclient.methods.PostMethod;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.samplers.Interruptible;
 
 /**
  * A sampler which understands all the parts necessary to read statistics about
  * HTTP requests, including cookies and authentication.
  * This sampler uses HttpClient 3.1.
  *
  */
 public class HTTPSampler2 extends HTTPSamplerBase implements Interruptible {
 
     private static final long serialVersionUID = 240L;
 
     private final transient HTTPHC3Impl hc;
     
     public HTTPSampler2(){
         hc = new HTTPHC3Impl(this);
     }
 
     public boolean interrupt() {
         return hc.interrupt();
     }
 
     @Override
     protected HTTPSampleResult sample(URL u, String method,
             boolean areFollowingRedirect, int depth) {
         return hc.sample(u, method, areFollowingRedirect, depth);
     }
 
     // Methods needed by subclasses to get access to the implementation
     protected HttpClient setupConnection(URL url, PostMethod httpMethod, HTTPSampleResult res) 
         throws IOException {
         return hc.setupConnection(url, httpMethod, res);
     }
 
     protected void saveConnectionCookies(PostMethod httpMethod, URL url,
             CookieManager cookieManager) {
         hc.saveConnectionCookies(httpMethod, url, cookieManager);
    }
 
     protected String getResponseHeaders(PostMethod httpMethod) {
         return hc.getResponseHeaders(httpMethod);
     }
 
     protected String getConnectionHeaders(PostMethod httpMethod) {
         return hc.getConnectionHeaders(httpMethod);
     }
 
     protected void setSavedClient(HttpClient savedClient) {
         hc.savedClient = savedClient;
     }
+
+    /**
+     * {@inheritDoc}
+     * This implementation forwards to the implementation class.
+     */
+    @Override
+    protected void notifySSLContextWasReset() {
+        hc.notifySSLContextWasReset();
+    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index d4905a51a..75012d991 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1,1681 +1,1704 @@
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
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URI;
 import java.net.URL;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.Future;
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.HTMLParseException;
 import org.apache.jmeter.protocol.http.parser.HTMLParser;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.HTTPFileArgs;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
+import org.apache.jmeter.util.JsseSSLManager;
+import org.apache.jmeter.util.SSLManager;
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
     implements TestListener, ThreadListener, HTTPConstantsInterface {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     //+ JMX names - do not change
     public static final String ARGUMENTS = "HTTPsampler.Arguments"; // $NON-NLS-1$
 
     public static final String AUTH_MANAGER = "HTTPSampler.auth_manager"; // $NON-NLS-1$
 
     public static final String COOKIE_MANAGER = "HTTPSampler.cookie_manager"; // $NON-NLS-1$
 
     public static final String CACHE_MANAGER = "HTTPSampler.cache_manager"; // $NON-NLS-1$
 
     public static final String HEADER_MANAGER = "HTTPSampler.header_manager"; // $NON-NLS-1$
 
     public static final String DOMAIN = "HTTPSampler.domain"; // $NON-NLS-1$
 
     public static final String PORT = "HTTPSampler.port"; // $NON-NLS-1$
 
     public static final String PROXYHOST = "HTTPSampler.proxyHost"; // $NON-NLS-1$
 
     public static final String PROXYPORT = "HTTPSampler.proxyPort"; // $NON-NLS-1$
 
     public static final String PROXYUSER = "HTTPSampler.proxyUser"; // $NON-NLS-1$
 
     public static final String PROXYPASS = "HTTPSampler.proxyPass"; // $NON-NLS-1$
 
     public static final String CONNECT_TIMEOUT = "HTTPSampler.connect_timeout"; // $NON-NLS-1$
 
     public static final String RESPONSE_TIMEOUT = "HTTPSampler.response_timeout"; // $NON-NLS-1$
 
     public static final String METHOD = "HTTPSampler.method"; // $NON-NLS-1$
 
     public static final String CONTENT_ENCODING = "HTTPSampler.contentEncoding"; // $NON-NLS-1$
 
     public static final String IMPLEMENTATION = "HTTPSampler.implementation"; // $NON-NLS-1$
 
     public static final String PATH = "HTTPSampler.path"; // $NON-NLS-1$
 
     public static final String FOLLOW_REDIRECTS = "HTTPSampler.follow_redirects"; // $NON-NLS-1$
 
     public static final String AUTO_REDIRECTS = "HTTPSampler.auto_redirects"; // $NON-NLS-1$
 
     public static final String PROTOCOL = "HTTPSampler.protocol"; // $NON-NLS-1$
 
     private static final String PROTOCOL_FILE = "file"; // $NON-NLS-1$
 
     private static final String DEFAULT_PROTOCOL = PROTOCOL_HTTP;
 
     public static final String URL = "HTTPSampler.URL"; // $NON-NLS-1$
 
     /**
      * IP source to use - does not apply to Java HTTP implementation currently
      */
     public static final String IP_SOURCE = "HTTPSampler.ipSource"; // $NON-NLS-1$
 
     public static final String USE_KEEPALIVE = "HTTPSampler.use_keepalive"; // $NON-NLS-1$
 
     public static final String DO_MULTIPART_POST = "HTTPSampler.DO_MULTIPART_POST"; // $NON-NLS-1$
 
     public static final String BROWSER_COMPATIBLE_MULTIPART  = "HTTPSampler.BROWSER_COMPATIBLE_MULTIPART"; // $NON-NLS-1$
     
     public static final String CONCURRENT_DWN = "HTTPSampler.concurrentDwn"; // $NON-NLS-1$
     
     public static final String CONCURRENT_POOL = "HTTPSampler.concurrentPool"; // $NON-NLS-1$
 
     private static final String CONCURRENT_POOL_DEFAULT = "4"; // default for concurrent pool (do not change)
 
     //- JMX names
 
     public static final boolean BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT = false; // The default setting to be used (i.e. historic)
     
     private static final long KEEPALIVETIME = 0; // for Thread Pool for resources but no need to use a special value?
     
     private static final long AWAIT_TERMINATION_TIMEOUT = 
         JMeterUtils.getPropDefault("httpsampler.await_termination_timeout", 60); // $NON-NLS-1$ // default value: 60 secs 
     
     public static final int CONCURRENT_POOL_SIZE = 4; // Default concurrent pool size for download embedded resources
     
     
     public static final String DEFAULT_METHOD = GET; // $NON-NLS-1$
     // Supported methods:
     private static final String [] METHODS = {
         DEFAULT_METHOD, // i.e. GET
         POST,
         HEAD,
         PUT,
         OPTIONS,
         TRACE,
         DELETE,
         };
 
     private static final List<String> METHODLIST = Collections.unmodifiableList(Arrays.asList(METHODS));
 
     // @see mergeFileProperties
     // Must be private, as the file list needs special handling
     private final static String FILE_ARGS = "HTTPsampler.Files"; // $NON-NLS-1$
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
     private static final String MD5 = "HTTPSampler.md5"; // $NON-NLS-1$
 
     /** A number to indicate that the port has not been set. */
     public static final int UNSPECIFIED_PORT = 0;
     public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$
     // TODO - change to use URL version? Will this affect test plans?
 
     /** If the port is not present in a URL, getPort() returns -1 */
     public static final int URL_UNSPECIFIED_PORT = -1;
     public static final String URL_UNSPECIFIED_PORT_AS_STRING = "-1"; // $NON-NLS-1$
 
     protected static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
 
     protected static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";
 
     private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
 
     private static final String QRY_SEP = "&"; // $NON-NLS-1$
 
     private static final String QRY_PFX = "?"; // $NON-NLS-1$
 
     protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 5); // $NON-NLS-1$
 
     protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$
 
 
     // Derive the mapping of content types to parsers
     private static final Map<String, String> parsersForType = new HashMap<String, String>();
     // Not synch, but it is not modified after creation
 
     private static final String RESPONSE_PARSERS= // list of parsers
         JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
 
+	// Control reuse of cached SSL Context in subsequent iterations
+	private static final boolean USE_CACHED_SSL_CONTEXT = 
+	        JMeterUtils.getPropDefault("https.use.cached.ssl.context", true);//$NON-NLS-1$
+    
     static{
         String []parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
         for (int i=0;i<parsers.length;i++){
             final String parser = parsers[i];
             String classname=JMeterUtils.getProperty(parser+".className");//$NON-NLS-1$
             if (classname == null){
                 log.info("Cannot find .className property for "+parser+", using default");
                 classname="";
             }
             String typelist=JMeterUtils.getProperty(parser+".types");//$NON-NLS-1$
             if (typelist != null){
                 String []types=JOrphanUtils.split(typelist, " " , true);
                 for (int j=0;j<types.length;j++){
                     final String type = types[j];
                     log.info("Parser for "+type+" is "+classname);
                     parsersForType.put(type,classname);
                 }
             } else {
                 log.warn("Cannot find .types property for "+parser);
             }
         }
         if (parsers.length==0){ // revert to previous behaviour
             parsersForType.put("text/html", ""); //$NON-NLS-1$ //$NON-NLS-2$
             log.info("No response parsers defined: text/html only will be scanned for embedded resources");
         }
+        
+		log.info("Reuse SSL session context on subsequent iterations: "
+				+ USE_CACHED_SSL_CONTEXT);
     }
 
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static boolean REMOVESLASHDOTDOT = JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
 
     ////////////////////// Variables //////////////////////
 
     private boolean dynamicPath = false;// Set false if spaces are already encoded
 
 
 
     ////////////////////// Code ///////////////////////////
 
     public HTTPSamplerBase() {
         setArguments(new Arguments());
     }
 
     /**
      * Determine if the file should be sent as the entire Post body,
      * i.e. without any additional wrapping
      *
      * @return true if specified file is to be sent as the body,
      * i.e. FileField is blank
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
      * as the post body
      *
      * @return true if none of the parameters have a name specified
      */
     public boolean getSendParameterValuesAsPostBody() {
         boolean noArgumentsHasName = true;
         PropertyIterator args = getArguments().iterator();
         while (args.hasNext()) {
             HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
             if(arg.getName() != null && arg.getName().length() > 0) {
                 noArgumentsHasName = false;
                 break;
             }
         }
         return noArgumentsHasName;
     }
 
     /**
      * Determine if we should use multipart/form-data or
      * application/x-www-form-urlencoded for the post
      *
      * @return true if multipart/form-data should be used and method is POST
      */
     public boolean getUseMultipartForPost(){
         // We use multipart if we have been told so, or files are present
         // and the files should not be send as the post body
         HTTPFileArg[] files = getHTTPFiles();
         if(POST.equals(getMethod()) && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()))) {
             return true;
         }
         return false;
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
         if (protocol == null || protocol.length() == 0 ) {
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
      * Sets the Path attribute of the UrlConfig object Also calls parseArguments
      * to extract and store any query arguments
      *
      * @param path
      *            The new Path value
      * @param contentEncoding
      *            The encoding used for the querystring parameter values
      */
     public void setPath(String path, String contentEncoding) {
         if (GET.equals(getMethod()) || DELETE.equals(getMethod())) {
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
         if (dynamicPath) {
             return encodeSpaces(p);
         }
         return p;
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
 
     public void setContentEncoding(String value) {
         setProperty(CONTENT_ENCODING, value);
     }
 
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
      */
     public void addEncodedArgument(String name, String value) {
         this.addEncodedArgument(name, value, ARG_VAL_SEP);
     }
 
     public void addEncodedArgument(String name, String value, String metaData, String contentEncoding) {
         if (log.isDebugEnabled()){
             log.debug("adding argument: name: " + name + " value: " + value + " metaData: " + metaData + " contentEncoding: " + contentEncoding);
         }
 
         HTTPArgument arg = null;
         if(contentEncoding != null) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         }
         else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded = null;
         if(contentEncoding != null) {
             try {
                 valueEncoded = arg.getEncodedValue(contentEncoding);
             }
             catch (UnsupportedEncodingException e) {
                 log.warn("Unable to get encoded value using encoding " + contentEncoding);
                 valueEncoded = arg.getEncodedValue();
             }
         }
         else {
             valueEncoded = arg.getEncodedValue();
         }
         // If there is no difference, we mark it as not needing encoding
         if (arg.getName().equals(arg.getEncodedName()) && arg.getValue().equals(valueEncoded)) {
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
     public void clearTestElementChildren(){
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
     public static int getDefaultPort(String protocol,int port){
         if (port==URL_UNSPECIFIED_PORT){
             return
                 protocol.equalsIgnoreCase(PROTOCOL_HTTP)  ? DEFAULT_HTTP_PORT :
                 protocol.equalsIgnoreCase(PROTOCOL_HTTPS) ? DEFAULT_HTTPS_PORT :
                     port;
         }
         return port;
     }
 
     /**
      * Get the port number from the port string, allowing for trailing blanks.
      *
      * @return port number or UNSPECIFIED_PORT (== 0)
      */
     public int getPortIfSpecified() {
         String port_s = getPropertyAsString(PORT, UNSPECIFIED_PORT_AS_STRING);
         try {
             return Integer.parseInt(port_s.trim());
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
         if (port == UNSPECIFIED_PORT ||
                 (PROTOCOL_HTTP.equalsIgnoreCase(protocol) && port == DEFAULT_HTTP_PORT) ||
                 (PROTOCOL_HTTPS.equalsIgnoreCase(protocol) && port == DEFAULT_HTTPS_PORT)) {
             return true;
         }
         return false;
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
             if (PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return DEFAULT_HTTPS_PORT;
             }
             if (!PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: "+prot);
                 // TODO - should this return something else?
             }
             return DEFAULT_HTTP_PORT;
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
 
     public void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
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
                 for (int i=0; i < value.getHeaders().size(); i++) {
                     log.debug("    " + value.getHeader(i).getName() + "=" + value.getHeader(i).getValue());
                 }
             }
         }
         setProperty(new TestElementProperty(HEADER_MANAGER, value));
     }
 
     public HeaderManager getHeaderManager() {
         return (HeaderManager) getProperty(HEADER_MANAGER).getObjectValue();
     }
 
     public void setCookieManager(CookieManager value) {
         CookieManager mgr = getCookieManager();
         if (mgr != null) {
             log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(COOKIE_MANAGER, value));
     }
 
     public CookieManager getCookieManager() {
         return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
     }
 
     public void setCacheManager(CacheManager value) {
         CacheManager mgr = getCacheManager();
         if (mgr != null) {
             log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(CACHE_MANAGER, value));
     }
 
     public CacheManager getCacheManager() {
         return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
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
         return getPropertyAsString(EMBEDDED_URL_RE,"");
     }
 
     public void setEmbeddedUrlRE(String regex) {
         setProperty(new StringProperty(EMBEDDED_URL_RE, regex));
     }
 
     /**
      * Obtain a result that will help inform the user that an error has occured
      * during sampling, and how long it took to detect the error.
      *
      * @param e
      *            Exception representing the error.
      * @param res
      *            SampleResult
      * @return a sampling result useful to inform the user about the exception.
      */
     protected HTTPSampleResult errorResult(Throwable e, HTTPSampleResult res) {
         res.setSampleLabel("Error: " + res.getSampleLabel());
         res.setDataType(SampleResult.TEXT);
         ByteArrayOutputStream text = new ByteArrayOutputStream(200);
         e.printStackTrace(new PrintStream(text));
         res.setResponseData(text.toByteArray());
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": "+e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": "+e.getMessage());
         res.setSuccessful(false);
         res.setMonitor(this.isMonitor());
         return res;
     }
 
     private static final String HTTP_PREFIX = PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
     /**
      * Get the URL, built from its component parts.
      *
      * <p>
      * As a special case, if the path starts with "http[s]://",
      * then the path is assumed to be the entire URL.
      * </p>
      *
      * @return The URL to be requested by this sampler.
      * @throws MalformedURLException
      */
     public URL getUrl() throws MalformedURLException {
         StringBuilder pathAndQuery = new StringBuilder(100);
         String path = this.getPath();
         // Hack to allow entire URL to be provided in host field
         if (path.startsWith(HTTP_PREFIX)
          || path.startsWith(HTTPS_PREFIX)){
             return new URL(path);
         }
         if (!path.startsWith("/")){ // $NON-NLS-1$
             pathAndQuery.append("/"); // $NON-NLS-1$
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if(GET.equals(getMethod()) || DELETE.equals(getMethod())) {
             // Get the query string encoded in specified encoding
             // If no encoding is specified by user, we will get it
             // encoded in UTF-8, which is what the HTTP spec says
             String queryString = getQueryString(getContentEncoding());
             if(queryString.length() > 0) {
                 if (path.indexOf(QRY_PFX) > -1) {// Already contains a prefix
                     pathAndQuery.append(QRY_SEP);
                 } else {
                     pathAndQuery.append(QRY_PFX);
                 }
                 pathAndQuery.append(queryString);
             }
         }
         // If default port for protocol is used, we do not include port in URL
         if(isProtocolDefaultPort()) {
             return new URL(getProtocol(), getDomain(), pathAndQuery.toString());
         }
         return new URL(getProtocol(), getDomain(), getPort(), pathAndQuery.toString());
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
          // Check if the sampler has a specified content encoding
          if(contentEncoding == null || contentEncoding.trim().length() == 0) {
              // We use the encoding which should be used according to the HTTP spec, which is UTF-8
              contentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
          }
         StringBuilder buf = new StringBuilder();
         PropertyIterator iter = getArguments().iterator();
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
                 log.warn("Unexpected argument type: "+objectValue.getClass().getName());
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
             }
             catch(UnsupportedEncodingException e) {
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
      *            the query string
      * @param contentEncoding -
      *            the content encoding of the query string. The query string might
      *            actually be the post body of a http post request.
      */
     public void parseArguments(String queryString, String contentEncoding) {
         String[] args = JOrphanUtils.split(queryString, QRY_SEP);
         for (int i = 0; i < args.length; i++) {
             // need to handle four cases:
             // - string contains name=value
             // - string contains name=
             // - string contains name
             // - empty string
 
             String metaData; // records the existance of an equal sign
             String name;
             String value;
             int length = args[i].length();
             int endOfNameIndex = args[i].indexOf(ARG_VAL_SEP);
             if (endOfNameIndex != -1) {// is there a separator?
                 // case of name=value, name=
                 metaData = ARG_VAL_SEP;
                 name = args[i].substring(0, endOfNameIndex);
                 value = args[i].substring(endOfNameIndex + 1, length);
             } else {
                 metaData = "";
                 name=args[i];
                 value="";
             }
             if (name.length() > 0) {
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if(contentEncoding != null) {
                     addEncodedArgument(name, value, metaData, contentEncoding);
                 }
                 else {
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
             if(POST.equals(getMethod()) || PUT.equals(getMethod())) {
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
             if (PROTOCOL_FILE.equalsIgnoreCase(getProtocol())){
                 res = fileSample(new URI(PROTOCOL_FILE,getPath(),null));
             } else {
                 res = sample(getUrl(), getMethod(), false, 0);
             }
             res.setSampleLabel(getName());
             return res;
         } catch (Exception e) {
             return errorResult(e, new HTTPSampleResult());
         }
     }
 
     private HTTPSampleResult fileSample(URI uri) throws IOException {
 
         //String urlStr = uri.toString();
 
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
         res.setHTTPMethod(GET); // Dummy
         res.setURL(uri.toURL());
         res.setSampleLabel(uri.toString());
         FileInputStream fis = null;
         res.sampleStart();
         try {
             byte[] responseData;
             StringBuilder ctb=new StringBuilder("text/html"); // $NON-NLS-1$
             fis = new FileInputStream(getPath());
             String contentEncoding = getContentEncoding();
             if (contentEncoding.length() > 0) {
                 ctb.append("; charset="); // $NON-NLS-1$
                 ctb.append(contentEncoding);
             }
             responseData = IOUtils.toByteArray(fis);
             res.sampleEnd();
             res.setResponseData(responseData);
             res.setResponseCodeOK();
             res.setResponseMessageOK();
             res.setSuccessful(true);
             String ct = ctb.toString();
             res.setContentType(ct);
             res.setEncodingAndType(ct);
         } finally {
             IOUtils.closeQuietly(fis);
         }
 
         //res.setResponseHeaders("");
 
         return res;
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
      * @return results of the sampling
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
             if (responseData.length > 0){  // Bug 39205
                 String parserName = getParserClass(res);
                 if(parserName != null)
                 {
                     final HTMLParser parser =
                         parserName.length() > 0 ? // we have a name
                         HTMLParser.getParser(parserName)
                         :
                         HTMLParser.getParser(); // we don't; use the default parser
                     urls = parser.getEmbeddedResourceURLs(responseData, res.getURL());
                 }
             }
         } catch (HTMLParseException e) {
             // Don't break the world just because this failed:
             res.addSubResult(errorResult(e, res));
             res.setSuccessful(false);
         }
 
         // Iterate through the URLs and download each image:
         if (urls != null && urls.hasNext()) {
             if (container == null) {
                 container = new HTTPSampleResult(res);
                 container.addRawSubResult(res);
             }
             res = container;
 
             // Get the URL matcher
             String re=getEmbeddedUrlRE();
             Perl5Matcher localMatcher = null;
             Pattern pattern = null;
             if (re.length()>0){
                 try {
                     pattern = JMeterUtils.getPattern(re);
                     localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                 } catch (MalformedCachePatternException e) {
                     log.warn("Ignoring embedded URL match string: "+e.getMessage());
                 }
             }
             
             // For concurrent get resources
             final List<Callable<HTTPSampleResult>> liste = new ArrayList<Callable<HTTPSampleResult>>();
             
             while (urls.hasNext()) {
                 Object binURL = urls.next(); // See catch clause below
                 try {
                     URL url = (URL) binURL;
                     if (url == null) {
                         log.warn("Null URL detected (should not happen)");
                     } else {
                         String urlstr = url.toString();
                         String urlStrEnc=encodeSpaces(urlstr);
                         if (!urlstr.equals(urlStrEnc)){// There were some spaces in the URL
                             try {
                                 url = new URL(urlStrEnc);
                             } catch (MalformedURLException e) {
                                 res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), res));
                                 res.setSuccessful(false);
                                 continue;
                             }
                         }
                         // I don't think localMatcher can be null here, but check just in case
                         if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
                             continue; // we have a pattern and the URL does not match, so skip it
                         }
                         
                         if (isConcurrentDwn()) {
                             // if concurrent download emb. resources, add to a list for async gets later
                             liste.add(new ASyncSample(url, GET, false, frameDepth + 1));
                         } else {
                             // default: serial download embedded resources
                             HTTPSampleResult binRes = sample(url, GET, false, frameDepth + 1);
                             res.addSubResult(binRes);
                             res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
                         }
 
                     }
                 } catch (ClassCastException e) { // TODO can this happen?
                     res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), res));
                     res.setSuccessful(false);
                     continue;
                 }
             }
             
             // IF for download concurrent embedded resources
             if (isConcurrentDwn()) {
                 int poolSize = CONCURRENT_POOL_SIZE; // init with default value
                 try {
                     poolSize = Integer.parseInt(getConcurrentPool());
                 } catch (NumberFormatException nfe) {
                     log.warn("Concurrent download resources selected, "// $NON-NLS-1$
                             + "but pool size value is bad. Use default value");// $NON-NLS-1$
                 }
                 // Thread pool Executor to get resources 
                 // use a LinkedBlockingQueue, note: max pool size doesn't effect
                 final ThreadPoolExecutor exec = new ThreadPoolExecutor(
                         poolSize, poolSize, KEEPALIVETIME, TimeUnit.SECONDS,
                         new LinkedBlockingQueue<Runnable>());
 
                 try {
                     // sample all resources with threadpool
                     final List<Future<HTTPSampleResult>> retExec = exec.invokeAll(liste);
                     // call normal shutdown (wait ending all tasks)
                     exec.shutdown();
                     // put a timeout if tasks couldn't terminate
                     exec.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
 
                     // add result to main sampleResult
                     for (Future<HTTPSampleResult> future : retExec) {
                         final HTTPSampleResult binRes = future.get();
                         res.addSubResult(binRes);
                         res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
                     }
                 } catch (InterruptedException ie) {
                     log.warn("Interruped fetching embedded resources", ie); // $NON-NLS-1$
                 } catch (ExecutionException ee) {
                     log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
                 }
             }
         }
         return res;
     }
 
     /*
      * @param res HTTPSampleResult to check
      * @return parser class name (may be "") or null if entry does not exist
      */
     private String getParserClass(HTTPSampleResult res) {
         final String ct = res.getMediaType();
         return parsersForType.get(ct);
     }
 
     // TODO: make static?
     protected String encodeSpaces(String path) {
         return JOrphanUtils.replaceAllChars(path, ' ', "%20"); // $NON-NLS-1$
     }
 
     /**
      * {@inheritDoc}
      */
     public void testEnded() {
         dynamicPath = false;
     }
 
     /**
      * {@inheritDoc}
      */
     public void testEnded(String host) {
         testEnded();
     }
 
-    /**
-     * {@inheritDoc}
-     */
-    public void testIterationStart(LoopIterationEvent event) {
-    }
+	/**
+	 * {@inheritDoc}
+	 */
+	public void testIterationStart(LoopIterationEvent event) {
+		if (!USE_CACHED_SSL_CONTEXT) {
+			JsseSSLManager sslMgr = (JsseSSLManager) SSLManager.getInstance();
+			sslMgr.resetContext();
+			notifySSLContextWasReset();
+		}
+	}
+
+	/**
+	 * Called by testIterationStart if the SSL Context was reset.
+	 * 
+	 * This implementation does nothing.
+	 */
+	protected void notifySSLContextWasReset() {
+		// NOOP
+	}
 
     /**
      * {@inheritDoc}
      */
     public void testStarted() {
         JMeterProperty pathP = getProperty(PATH);
         log.debug("path property is a " + pathP.getClass().getName());
         log.debug("path beginning value = " + pathP.getStringValue());
         if (pathP instanceof StringProperty && pathP.getStringValue().length() > 0) {
             log.debug("Encoding spaces in path");
             pathP.setObjectValue(encodeSpaces(pathP.getStringValue()));
             dynamicPath = false;
         } else {
             log.debug("setting dynamic path to true");
             dynamicPath = true;
         }
         log.debug("path ending value = " + pathP.getStringValue());
     }
 
     /**
      * {@inheritDoc}
      */
     public void testStarted(String host) {
         testStarted();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object clone() {
         HTTPSamplerBase base = (HTTPSamplerBase) super.clone();
         base.dynamicPath = dynamicPath;
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
             // Browsers seem to tolerate Location headers with spaces,
             // replacing them automatically with %20. We want to emulate
             // this behaviour.
             String location = lastRes.getRedirectLocation(); 
             if (REMOVESLASHDOTDOT) {
                 location = ConversionUtils.removeSlashDotDot(location);
             }
             location = encodeSpaces(location);
             try {
                 lastRes = sample(ConversionUtils.makeRelativeURL(lastRes.getURL(), location), GET, true, frameDepth);
             } catch (MalformedURLException e) {
                 lastRes = errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             }
             if (lastRes.getSubResults() != null && lastRes.getSubResults().length > 0) {
                 SampleResult[] subs = lastRes.getSubResults();
                 for (int i = 0; i < subs.length; i++) {
                     totalRes.addSubResult(subs[i]);
                 }
             } else {
                 // Only add sample if it is a sample of valid url redirect, i.e. that
                 // we have actually sampled the URL
                 if(!invalidRedirectUrl) {
                     totalRes.addSubResult(lastRes);
                 }
             }
 
             if (!lastRes.isRedirect()) {
                 break;
             }
         }
         if (redirect >= MAX_REDIRECTS) {
             lastRes = errorResult(new IOException("Exceeeded maximum number of redirects: " + MAX_REDIRECTS), lastRes);
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
      * Follow redirects and download page resources if appropriate. this works,
      * but the container stuff here is what's doing it. followRedirects() is
      * actually doing the work to make sure we have only one container to make
      * this work more naturally, I think this method - sample() - needs to take
      * an HTTPSamplerResult container parameter instead of a
      * boolean:areFollowingRedirect.
      *
      * @param areFollowingRedirect
      * @param frameDepth
      * @param res
      * @return the sample result
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect, int frameDepth, HTTPSampleResult res) {
         boolean wasRedirected = false;
         if (!areFollowingRedirect) {
             if (res.isRedirect()) {
                 log.debug("Location set to - " + res.getRedirectLocation());
 
                 if (getFollowRedirects()) {
                     res = followRedirects(res, frameDepth);
                     areFollowingRedirect = true;
                     wasRedirected = true;
                 }
             }
         }
         if (isImageParser() && (HTTPSampleResult.TEXT).equals(res.getDataType()) && res.isSuccessful()) {
             if (frameDepth > MAX_FRAME_DEPTH) {
                 res.addSubResult(errorResult(new Exception("Maximum frame/iframe nesting depth exceeded."), res));
             } else {
                 // Only download page resources if we were not redirected.
                 // If we were redirected, the page resources have already been
                 // downloaded for the sample made for the redirected url
                 if(!wasRedirected) {
                     HTTPSampleResult container = (HTTPSampleResult) (areFollowingRedirect ? res.getParent() : res);
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
      * @return whether in range 200-399 or not
      */
     protected boolean isSuccessCode(int code){
         return (code >= 200 && code <= 399);
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
         if (value.getHTTPFileArgCount() > 0){
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
 
     public int getHTTPFileCount(){
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
             for(int i=0; i < files.length; i++){
                 HTTPFileArg file = files[i];
                 if (file.isNotEmpty()){
                     fileArgs.addHTTPFileArg(file);
                 }
             }
         }
         setHTTPFileArgs(fileArgs);
     }
 
     public static String[] getValidMethodsAsArray(){
         return METHODLIST.toArray(new String[0]);
     }
 
     public static boolean isSecure(String protocol){
         return PROTOCOL_HTTPS.equalsIgnoreCase(protocol);
     }
 
     public static boolean isSecure(URL url){
         return isSecure(url.getProtocol());
     }
 
     // Implement these here, to avoid re-implementing for sub-classes
     // (previously these were implemented in all TestElements)
     public void threadStarted(){
     }
 
     public void threadFinished(){
     }
 
     /**
      * Read response from the input stream, converting to MD5 digest if the useMD5 property is set.
      *
      * For the MD5 case, the result byte count is set to the size of the original response.
      * 
      * Closes the inputStream (unless there was an error)
      * 
      * @param sampleResult
      * @param in input stream
      * @param length expected input length or zero
      * @return the response or the MD5 of the response
      * @throws IOException
      */
     public byte[] readResponse(SampleResult sampleResult, InputStream in, int length) throws IOException {
 
         byte[] readBuffer = getThreadContext().getReadBuffer();
         int bufferSize=32;// Enough for MD5
 
         MessageDigest md=null;
         boolean asMD5 = useMD5();
         if (asMD5) {
             try {
                 md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
             } catch (NoSuchAlgorithmException e) {
                 log.error("Should not happen - could not find MD5 digest", e);
                 asMD5=false;
             }
         } else {
             if (length <= 0) {// may also happen if long value > int.max
                 bufferSize = 4 * 1024;
             } else {
                 bufferSize = length;
             }
         }
         ByteArrayOutputStream w = new ByteArrayOutputStream(bufferSize);
         int bytesRead = 0;
         int totalBytes = 0;
         boolean first = true;
         while ((bytesRead = in.read(readBuffer)) > -1) {
             if (first) {
                 sampleResult.latencyEnd();
                 first = false;
             }
             if (asMD5 && md != null) {
                 md.update(readBuffer, 0 , bytesRead);
                 totalBytes += bytesRead;
             } else {
                 w.write(readBuffer, 0, bytesRead);
             }
         }
         if (first){ // Bug 46838 - if there was no data, still need to set latency
             sampleResult.latencyEnd();
         }
         in.close();
         w.flush();
         if (asMD5 && md != null) {
             byte[] md5Result = md.digest();
             w.write(JOrphanUtils.baToHexBytes(md5Result)); 
             sampleResult.setBytes(totalBytes);
         }
         w.close();
         return w.toByteArray();
     }
 
     /**
      * JMeter 2.3.1 and earlier only had fields for one file on the GUI:
      * - FILE_NAME
      * - FILE_FIELD
      * - MIMETYPE
      * These were stored in their own individual properties.
      *
      * Version 2.3.3 introduced a list of files, each with their own path, name and mimetype.
      *
      * In order to maintain backwards compatibility of test plans, the 3 original properties
      * were retained; additional file entries are stored in an HTTPFileArgs class.
      * The HTTPFileArgs class was only present if there is more than 1 file; this means that
      * such test plans are backward compatible.
      *
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
         if(oldStyleFile.isNotEmpty()) { // OK, we have an old-style file definition
             allFileArgs.addHTTPFileArg(oldStyleFile); // save it
             // Now deal with any additional file arguments
             if(fileArgs != null) {
                 HTTPFileArg[] infiles = fileArgs.asArray();
                 for (int i = 0; i < infiles.length; i++){
                     allFileArgs.addHTTPFileArg(infiles[i]);
                 }
             }
         } else {
             if(fileArgs != null) { // for new test plans that don't have FILE/PARAM/MIME properties
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
      */
     public void setIpSource(String value) {
         setProperty(IP_SOURCE, value, "");
     }
 
     /**
      * get IP source to use - does not apply to Java HTTP implementation currently
      */
     public String getIpSource() {
         return getPropertyAsString(IP_SOURCE,"");
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
         return getPropertyAsString(CONCURRENT_POOL,CONCURRENT_POOL_DEFAULT);
     }
 
     public void setConcurrentPool(String poolSize) {
         setProperty(CONCURRENT_POOL, poolSize, CONCURRENT_POOL_DEFAULT);
     }
 
     /**
      * Callable class to sample asynchronously resources embedded
      *
      */
     public class ASyncSample implements Callable<HTTPSampleResult> {
         final private URL url;
         final private String method;
         final private boolean areFollowingRedirect;
         final private int depth;
 
         public ASyncSample(URL url, String method,
                 boolean areFollowingRedirect, int depth){
             this.url = url;
             this.method = method;
             this.areFollowingRedirect = areFollowingRedirect;
             this.depth = depth;
         }
 
         public HTTPSampleResult call() {
             return sample(url, method, areFollowingRedirect, depth);
         }
     }
 }
 
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerProxy.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerProxy.java
index 4c0ba2fdd..848bec447 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerProxy.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerProxy.java
@@ -1,81 +1,92 @@
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
 
 import java.net.URL;
 
 import org.apache.jmeter.samplers.Interruptible;
 
 /**
  * Proxy class that dispatches to the appropriate HTTP sampler.
  * <p>
  * This class is stored in the test plan, and holds all the configuration settings.
  * The actual implementation is created at run-time, and is passed a reference to this class
  * so it can get access to all the settings stored by HTTPSamplerProxy.
  */
 public final class HTTPSamplerProxy extends HTTPSamplerBase implements Interruptible {
 
     private static final long serialVersionUID = 1L;
 
     private transient HTTPAbstractImpl impl;
     
     public HTTPSamplerProxy(){
         super();
     }
     
     /**
      * Convenience method used to initialise the implementation.
      * 
      * @param impl the implementation to use.
      */
     public HTTPSamplerProxy(String impl){
         super();
         setImplementation(impl);
     }
         
     /** {@inheritDoc} */
     @Override
     protected HTTPSampleResult sample(URL u, String method, boolean areFollowingRedirect, int depth) {
         if (impl == null) { // Not called from multiple threads, so this is OK
             try {
                 impl = HTTPSamplerFactory.getImplementation(getImplementation(), this);
             } catch (Exception ex) {
                 return errorResult(ex, new HTTPSampleResult());
             }
         }
         return impl.sample(u, method, areFollowingRedirect, depth);
     }
 
     // N.B. It's not possible to forward threadStarted() to the implementation class.
     // This is because Config items are not processed until later, and HTTPDefaults may define the implementation
 
     @Override
     public void threadFinished(){
         if (impl != null){
             impl.threadFinished(); // Forward to sampler
         }
     }
 
     public boolean interrupt() {
         if (impl != null) {
             return impl.interrupt(); // Forward to sampler
         }
         return false;
     }
+
+    /**
+     * {@inheritDoc}
+     * This implementation forwards to the implementation class.
+     */
+    @Override
+    protected void notifySSLContextWasReset() {
+        if (impl != null) {
+            impl.notifySSLContextWasReset();
+        }
+    }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 8d5732a89..a9797edbc 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,172 +1,173 @@
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
 	<author email="dev AT jakarta.apache.org">JMeter developers</author>     
 	<title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 <!--  ===================  -->
 
 <h1>Version 2.5.1</h1>
 
 <h2>Summary of main changes</h2>
 
 <ul>
 </ul>
 
 
 <!--  ========================= End of summary ===================================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode. 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>If Controller may make that JMeter starts a infinite running when the If condition is always
 false from the first iteration.</p>
 
 <p>
 The menu item Options / Choose Language does not change all the displayed text to the new language.
 [The behaviour has improved, but language change is still not fully working]
 To override the default local language fully, set the JMeter property "language" before starting JMeter. 
 </p>
 
 <h2>Incompatible changes</h2>
 
 <p>
 The HttpClient4 sampler as implemented in version 2.5 used a retry count of 3.
 As this can hide server errors, JMeter now sets the retry count to 0 to prevent any automatic retries.
 This can be overridden by setting the JMeter property <b>httpclient4.retrycount</b>.
 </p>
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Fix HttpClient 4 sampler so it reuses HttpClient instances and connections where possible.</li>
 <li>Bug 51750 - Retrieve all embedded resources doesn't follow IFRAME</li>
 <li>Change the default so the HttpClient 4 sampler does not retry</li>
 <li>Bug 51752 - HTTP Cache is broken when using "Retrieve all embedded resources" with concurrent pool</li>
 <li>Bug 39219 - HTTP Server: You can't stop it after File->Open</li>
 <li>Bug 51775 - Port number duplicates in Host header when capturing by HttpClient (3.1 and 4.x)</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 50424 - Web Methods drop down list box inconsistent</li>
 <li>Bug 43293 - Java Request fields not cleared when creating new sampler</li>
 <li>Bug 51830 - Webservice Soap Request triggers too many popups when Webservice WSDL URL is down</li>
 <li>WebService(SOAP) request - add a connect timeout to get the wsdl used to populate Web Methods when server doesn't response</li>
 <li>Bug 51841 - JMS : If an error occurs in ReceiveSubscriber constructor or Publisher, then Connections will stay open</li>
 <li>Bug 51691 - Authorization does not work for JMS Publisher and JMS Subscriber</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>If Controller - Fixed two regressions introduced by bug 50032 (see bug 50618 too)</li>
 <li>If Controller - Catches a StackOverflowError when a condition returns always false (after at least one iteration with return true) See bug 50618</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Assertions</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 48943 - Functions are invoked additional times when used in combination with a Config Element</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li>WebService(SOAP) request - add I18N for some labels</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 51831 - Cannot disable UDP server or change the maximum UDP port</li>
 <li>Bug 51821 - Add short-cut for Enabling / Disabling (sub)tree or branches in test plan.</li>
 <li>Bug 47921 - Variables not released for GC after JMeterThread exits.</li>
 <li>Bug 51839 - "... end of run" printed prematurely</li>
 </ul>
 
 <!-- ==================================================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
+<li>Bug 51380 - Control reuse of cached SSL Context from iteration to iteration</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li>Beanshell Sampler now supports Interruptible interface</li>
 <li>Bug 51605 - WebService(SOAP) Request - WebMethod field value changes surreptitiously for all the requests when a value is selected in a request</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 42246 - Need for a 'auto-scroll' option in "View Results Tree" and "Assertion Results"</li>
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
 <li>Bug 51822 - (part 1) save 1 invocation of GuiPackage#getCurrentGui</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Bug 49976 - FormCharSetFinder visibility is default instead of public. </li>
 </ul>
 
 </section> 
 </body> 
 </document>
diff --git a/xdocs/usermanual/component_reference.xml b/xdocs/usermanual/component_reference.xml
index cce9ec6a9..89455a79d 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -1,1156 +1,1163 @@
 <?xml version="1.0"?>
 <!-- 
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
  -->
 <!DOCTYPE document
 [
 <!ENTITY sect-num '18'>
 ]>
 <document index="yes" index-level-2="yes" index-numbers="no" colbreak="&sect-num;.4"
   prev="boss.html" next="functions.html" date="$Date$">
 
 <properties>
   <title>User's Manual: Component Reference</title>
 </properties>
 
 <body>
 
 <!--
 	Because this is an XML document, all tags must be properly closed, including ones
 	which are passed unchanged into the HTML output, e.g. <br/>, not just <br>.
 	
 	Unfortunately Java does not currently allow for this - it outputs the trailing > -
 	which messes up the Help display. 
 	To avoid these artefacts, use the form <br></br>, which Java does seem to handle OK.
 
  -->
 <section name="&sect-num;.1 Samplers" anchor="samplers">
 <description>
 	<p>
 	Samplers perform the actual work of JMeter.
 	Each sampler (except Test Action) generates one or more sample results.
 	The sample results have various attributes (success/fail, elapsed time, data size etc) and can be viewed in the various listeners.
 	</p>
 </description>
 <component name="FTP Request" index="&sect-num;.1.1" width="499" height="292" screenshot="ftptest/ftp-request.png">
 <description>
 This controller lets you send an FTP "retrieve file" or "upload file" request to an FTP server.
 If you are going to send multiple requests to the same FTP server, consider
 using a <complink name="FTP Request Defaults"/> Configuration
 Element so you do not have to enter the same information for each FTP Request Generative
 Controller. When downloading a file, it can be stored on disk (Local File) or in the Response Data, or both.
 <p>
 Latency is set to the time it takes to login (versions of JMeter after 2.3.1).
 </p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server Name or IP" required="Yes">Domain name or IP address of the FTP server.</property>
         <property name="Port" required="No">Port to use. If this is  >0, then this specific port is used, otherwise JMeter uses the default FTP port.</property>
         <property name="Remote File:" required="Yes">File to retrieve or name of destination file to upload.</property>
         <property name="Local File:" required="Yes, if uploading (*)">File to upload, or destination for downloads (defaults to remote file name).</property>
         <property name="Local File Contents:" required="Yes, if uploading (*)">Provides the contents for the upload, overrides the Local File property.</property>
         <property name="get(RETR) / put(STOR)" required="Yes">Whether to retrieve or upload a file.</property>
         <property name="Use Binary mode ?" required="Yes">Check this to use Binary mode (default Ascii)</property>
         <property name="Save File in Response ?" required="Yes, if downloading">
         Whether to store contents of retrieved file in response data.
         If the mode is Ascii, then the contents will be visible in the Tree View Listener.
         </property>
         <property name="Username" required="Usually">FTP account username.</property>
         <property name="Password" required="Usually">FTP account password. N.B. This will be visible in the test plan.</property>
 </properties>
 <links>
         <link href="test_plan.html#assertions">Assertions</link>
         <complink name="FTP Request Defaults"/>
         <link href="build-ftp-test-plan.html">Building an FTP Test Plan</link>
 </links>
 
 </component>
 
 <component name="HTTP Request" index="&sect-num;.1.2" screenshot="http-request.png">
 
 <description>
         <p>This sampler lets you send an HTTP/HTTPS request to a web server.  It
         also lets you control whether or not JMeter parses HTML files for images and
         other embedded resources and sends HTTP requests to retrieve them.
         The following types of embedded resource are retrieved:</p>
         <ul>
         <li>images</li>
         <li>applets</li>
         <li>stylesheets</li>
         <li>external scripts</li>
         <li>frames, iframes</li>
         <li>background images (body, table, TD, TR)</li>
         <li>background sound</li>
         </ul>
         <p>
         The default parser is htmlparser.
         This can be changed by using the property "htmlparser.classname" - see jmeter.properties for details.
         </p>
         <p>If you are going to send multiple requests to the same web server, consider
         using an <complink name="HTTP Request Defaults"/>
         Configuration Element so you do not have to enter the same information for each
         HTTP Request.</p>
 
         <p>Or, instead of manually adding HTTP Requests, you may want to use
         JMeter's <complink name="HTTP Proxy Server"/> to create
         them.  This can save you time if you have a lot of HTTP requests or requests with many
         parameters.</p>
 
         <p><b>There are two different screens for defining the samplers:</b>
         <ul>
         <li>AJP/1.3 Sampler - uses the Tomcat mod_jk protocol (allows testing of Tomcat in AJP mode without needing Apache httpd)
         The AJP Sampler does not support multiple file upload; only the first file will be used.
         </li>
         <li>HTTP Request - this has an implementation drop-down box, which selects the HTTP protocol implementation to be used:</li>
         <ul>
         <li>Java - uses the HTTP implementation provided by the JVM. 
         This has some limitations in comparison with the HttpClient implementations - see below.</li>
         <li>HTTPClient3.1 - uses Apache Commons HttpClient 3.1. 
         This is no longer being developed, and support for this may be dropped in a future JMeter release.</li>
         <li>HTTPClient4 - uses Apache HttpComponents HttpClient 4.x.</li>
         </ul>
         </ul>
          </p>
          <p>The Java HTTP implementation has some limitations:</p>
          <ul>
          <li>There is no control over how connections are re-used. 
          When a connection is released by JMeter, it may or may not be re-used by the same thread.</li>
          <li>The API is best suited to single-threaded usage - various settings (e.g. proxy) 
          are defined via system properties, and therefore apply to all connections.</li>
          <li>There is a bug in the handling of HTTPS via a Proxy (the CONNECT is not handled correctly).
          See Java bugs 6226610 and 6208335.
          </li>
          <li>It does not support virtual hosts.</li>
          </ul>
          <p>Note: the FILE protocol is intended for testing puposes only. 
          It is handled by the same code regardless of which HTTP Sampler is used.</p>
         <p>If the request requires server or proxy login authorization (i.e. where a browser would create a pop-up dialog box),
          you will also have to add an <complink name="HTTP Authorization Manager"/> Configuration Element.
          For normal logins (i.e. where the user enters login information in a form), you will need to work out what the form submit button does,
          and create an HTTP request with the appropriate method (usually POST) 
          and the appropriate parameters from the form definition. 
          If the page uses HTTP, you can use the JMeter Proxy to capture the login sequence.
         </p>
         <p>
         In versions of JMeter up to 2.2, only a single SSL context was used for all threads and samplers.
         This did not generate the proper load for multiple users.
         A separate SSL context is now used for each thread.
         To revert to the original behaviour, set the JMeter property:
 <pre>
 https.sessioncontext.shared=true
 </pre>
+        By default, the SSL context is retained for the duration of the test.
+        In versions of JMeter from 2.5.1, the SSL session can be optionally reset for each test iteration.
+        To enable this, set the JMeter property:
+<pre>
+https.use.cached.ssl.context=false
+</pre>
+        Note: this does not apply to the Java HTTP implementation.
         </p>
         <p>
         JMeter defaults to the SSL protocol level TLS.
         If the server needs a different level, e.g. SSLv3, change the JMeter property, for example:
 <pre>
 https.default.protocol=SSLv3
 </pre> 
         </p>
         <p>
         JMeter also allows one to enable additional protocols, by changing the property <tt>https.socket.protocols</tt>.
         </p>
         <p>If the request uses cookies, then you will also need an
         <complink name="HTTP Cookie Manager"/>.  You can
         add either of these elements to the Thread Group or the HTTP Request.  If you have
         more than one HTTP Request that needs authorizations or cookies, then add the
         elements to the Thread Group.  That way, all HTTP Request controllers will share the
         same Authorization Manager and Cookie Manager elements.</p>
 
         <p>If the request uses a technique called "URL Rewriting" to maintain sessions,
         then see section
         <a href="build-adv-web-test-plan.html#session_url_rewriting">6.1 Handling User Sessions With URL Rewriting</a>
         for additional configuration steps.</p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server" required="Yes, unless provided by HTTP Request Defaults">
             Domain name or IP address of the web server. e.g. www.example.com. [Do not include the http:// prefix.]
             Note: in JMeter 2.5 (and later) if the "Host" header is defined in a Header Manager, then this will be used
             as the virtual host name.
         </property>
         <property name="Port" required="No">Port the web server is listening to. Default: 80</property>
         <property name="Connect Timeout" required="No">Connection Timeout. Number of milliseconds to wait for a connection to open.</property>
         <property name="Response Timeout" required="No">Response Timeout. Number of milliseconds to wait for a response.</property>
         <property name="Server (proxy)" required="No">Hostname or IP address of a proxy server to perform request. [Do not include the http:// prefix.]</property>
         <property name="Port" required="No, unless proxy hostname is specified">Port the proxy server is listening to.</property>
         <property name="Username" required="No">(Optional) username for proxy server.</property>
         <property name="Password" required="No">(Optional) password for proxy server.</property>
         <property name="Implementation" required="No">Java, HttpClient3.1, HttpClient4. 
         If not specified (and not defined by HTTP Request Defaults), the default depends on the value of the JMeter property
         <code>jmeter.httpsampler</code>, failing that, the Java implementation is used.</property>
         <property name="Protocol" required="No">HTTP, HTTPS or FILE. Default: HTTP</property>
         <property name="Method" required="Yes">GET, POST, HEAD, TRACE, OPTIONS, PUT, DELETE</property>
         <property name="Content Encoding" required="No">Content encoding to be used (for POST and FILE)</property>
 		<property name="Redirect Automatically" required="No">
 		Sets the underlying http protocol handler to automatically follow redirects,
 		so they are not seen by JMeter, and thus will not appear as samples.
 		Should only be used for GET and HEAD requests.
 		The HttpClient sampler will reject attempts to use it for POST or PUT.
 		<b>Warning: see below for information on cookie and header handling.</b>
         </property>
 		<property name="Follow Redirects" required="No">
 		This only has any effect if "Redirect Automatically" is not enabled.
 		If set, the JMeter sampler will check if the response is a redirect and follow it if so.
 		The initial redirect and further responses will appear as additional samples.
         The URL and data fields of the parent sample will be taken from the final (non-redirected)
         sample, but the parent byte count and elapsed time include all samples.
         The latency is taken from the initial response (versions of JMeter after 2.3.4 - previously it was zero).
 		Note that the HttpClient sampler may log the following message:<br/>
 		"Redirect requested but followRedirects is disabled"<br/>
 		This can be ignored.
         <br/>
         In versions after 2.3.4, JMeter will collapse paths of the form '/../segment' in
         both absolute and relative redirect URLs. For example http://host/one/../two => http://host/two.
         If necessary, this behaviour can be suppressed by setting the JMeter property
         <code>httpsampler.redirect.removeslashdotdot=false</code>
 		</property>
 		<property name="Use KeepAlive" required="No">JMeter sets the Connection: keep-alive header. This does not work properly with the default HTTP implementation, as connection re-use is not under user-control. 
                   It does work with the Jakarta httpClient implementation.</property>
         <property name="Use multipart/form-data for HTTP POST" required="No">
         Use a multipart/form-data or application/x-www-form-urlencoded post request
         </property>
         <property name="Browser-compatible headers" required="No">
         When using multipart/form-data, this suppresses the Content-Type and 
         Content-Transfer-Encoding headers; only the Content-Disposition header is sent.
         </property>
         <property name="Path" required="Yes">The path to resource (for example, /servlets/myServlet). If the
 resource requires query string parameters, add them below in the
 "Send Parameters With the Request" section.
 <b>
 As a special case, if the path starts with "http://" or "https://" then this is used as the full URL.
 </b>
 In this case, the server, port and protocol are ignored; parameters are also ignored for GET and DELETE methods.
 </property>
         <property name="Send Parameters With the Request" required="No">The query string will
         be generated from the list of parameters you provide.  Each parameter has a <i>name</i> and
         <i>value</i>, the options to encode the parameter, and an option to include or exclude an equals sign (some applications
         don't expect an equals when the value is the empty string).  The query string will be generated in the correct fashion, depending on
         the choice of "Method" you made (ie if you chose GET or DELETE, the query string will be
         appended to the URL, if POST or PUT, then it will be sent separately).  Also, if you are
         sending a file using a multipart form, the query string will be created using the
         multipart form specifications.
         <b>See below for some further information on parameter handling.</b>
         <p>
         Additionally, you can specify whether each parameter should be URL encoded.  If you are not sure what this
         means, it is probably best to select it.  If your values contain characters such as &amp;amp; or spaces, or
         question marks, then encoding is usually required.</p></property>
         <property name="File Path:" required="No">Name of the file to send.  If left blank, JMeter
         does not send a file, if filled in, JMeter automatically sends the request as
         a multipart form request.
         <p>
         If it is a POST or PUT request and there is a single file whose 'name' attribute (below) is omitted, 
         then the file is sent as the entire body
         of the request, i.e. no wrappers are added. This allows arbitrary bodies to be sent. This functionality is present for POST requests
         after version 2.2, and also for PUT requests after version 2.3.
         <b>See below for some further information on parameter handling.</b>
         </p>
         </property>
         <property name="Parameter name:" required="No">Value of the "name" web request parameter.</property>
         <property name="MIME Type" required="No">MIME type (for example, text/plain).
         If it is a POST or PUT request and either the 'name' atribute (below) are omitted or the request body is
         constructed from parameter values only, then the value of this field is used as the value of the
         content-type request header.
         </property>
         <property name="Retrieve All Embedded Resources from HTML Files" required="No">Tell JMeter to parse the HTML file
 and send HTTP/HTTPS requests for all images, Java applets, JavaScript files, CSSs, etc. referenced in the file.
         See below for more details.
         </property>
         <property name="Use as monitor" required="No">For use with the <complink name="Monitor Results"/> listener.</property>
        <property name="Save response as MD5 hash?" required="No">
        If this is selected, then the response is not stored in the sample result.
        Instead, the 32 character MD5 hash of the data is calculated and stored instead.
        This is intended for testing large amounts of data.
        </property>
         <property name="Embedded URLs must match:" required="No">
         If present, this must be a regular expression that is used to match against any embedded URLs found.
         So if you only want to download embedded resources from http://example.com/, use the expression:
         http://example\.com/.*
         </property>
         <property name="Use concurrent pool" required="No">Use a pool of concurrent connections to get embedded resources.</property>
         <property name="Size" required="No">Pool size for concurrent connections used to get embedded resources.</property>
         <property name="Source IP address:" required="No">
         [Only for HTTP Request HTTPClient] 
         Override the default local IP address for this sample.
         The JMeter host must have multiple IP addresses (i.e. IP aliases or network interfaces). 
         If the property <b>httpclient.localaddress</b> is defined, that is used for all HttpClient requests.
         </property>
 </properties>
 <p>
 <b>N.B.</b> when using Automatic Redirection, cookies are only sent for the initial URL.
 This can cause unexpected behaviour for web-sites that redirect to a local server.
 E.g. if www.example.com redirects to www.example.co.uk.
 In this case the server will probably return cookies for both URLs, but JMeter will only see the cookies for the last
 host, i.e. www.example.co.uk. If the next request in the test plan uses www.example.com, 
 rather than www.example.co.uk, it will not get the correct cookies.
 Likewise, Headers are sent for the initial request, and won't be sent for the redirect.
 This is generally only a problem for manually created test plans,
 as a test plan created using a recorder would continue from the redirected URL.
 </p>
 <p>
 <b>Parameter Handling:</b><br></br>
 For the POST and PUT method, if there is no file to send, and the name(s) of the parameter(s) are omitted,
 then the body is created by concatenating all the value(s) of the parameters.
 This allows arbitrary bodies to be sent.
 The values are encoded if the encoding flag is set (versions of JMeter after 2.3).
 See also the MIME Type above how you can control the content-type request header that is sent.
 <br></br>
 For other methods, if the name of the parameter is missing,
 then the parameter is ignored. This allows the use of optional parameters defined by variables.
 (versions of JMeter after 2.3)
 </p>
 <p>
 <b>Method Handling:</b><br></br>
 The POST and PUT request methods work similarly, except that the PUT method does not support multipart requests.
 The PUT method body must be provided as one of the following:
 <ul>
 <li>define the body as a file</li>
 <li>define the body as parameter value(s) with no name</li>
 </ul>
 If you define any parameters with a name in either the sampler or Http
 defaults then nothing is sent.
 The GET and DELETE request methods work similarly to each other.
 </p>
 <p>Upto and including JMeter 2.1.1, only responses with the content-type "text/html" were scanned for
 embedded resources. Other content-types were assumed to be something other than HTML.
 JMeter 2.1.2 introduces the a new property <b>HTTPResponse.parsers</b>, which is a list of parser ids,
  e.g. <b>htmlParser</b> and <b>wmlParser</b>. For each id found, JMeter checks two further properties:</p>
  <ul>
  <li>id.types - a list of content types</li>
  <li>id.className - the parser to be used to extract the embedded resources</li>
  </ul>
  <p>See jmeter.properties file for the details of the settings. 
  If the HTTPResponse.parser property is not set, JMeter reverts to the previous behaviour,
  i.e. only text/html responses will be scanned</p>
 <b>Emulating slow connections (HttpClient only):</b><br></br>
 The HttpClient version of the sampler supports emulation of slow connections; see the following entries in jmeter.properties:
 <pre>
 # Define characters per second > 0 to emulate slow connections
 #httpclient.socket.http.cps=0
 #httpclient.socket.https.cps=0
 </pre>
 <p><b>Response size calculation</b><br></br>
 Optional properties to allow change the method to get response size:<br></br>
 <ul><li>Gets the real network size in bytes for the body response
 <pre>sampleresult.getbytes.body_real_size=true</pre></li>
 <li>Add HTTP headers to full response size
 <pre>sampleresult.getbytes.headers_size=true</pre></li></ul>
 <note>Versions of JMeter before 2.5 returns only data response size (uncompressed if request uses gzip/defate mode).
 <br></br>To return to settings before version 2.5, set the two properties to false.</note>
 </p>
 <p>
 <b>Retry handling</b><br></br>
 In version 2.5 of JMeter, the HttpClient4 sampler used the default retry count, which was 3.
 As this can hide server errors, JMeter now sets the retry count to 0 to prevent any automatic retries.
 This can be overridden by setting the JMeter property <b>httpclient4.retrycount</b>.
 </p>
 <links>
         <link href="test_plan.html#assertions">Assertion</link>
         <link href="build-web-test-plan.html">Building a Web Test Plan</link>
         <link href="build-adv-web-test-plan.html">Building an Advanced Web Test Plan</link>
         <complink name="HTTP Authorization Manager"/>
         <complink name="HTTP Cookie Manager"/>
         <complink name="HTTP Header Manager"/>
         <complink name="HTML Link Parser"/>
         <complink name="HTTP Proxy Server"/>
         <complink name="HTTP Request Defaults"/>
         <link href="build-adv-web-test-plan.html#session_url_rewriting">HTTP Requests and Session ID's: URL Rewriting</link>
 </links>
 
 </component>
 
 <component name="JDBC Request" index="&sect-num;.1.3"  width="466" height="334" screenshot="jdbctest/jdbc-request.png">
 
 <description><p>This sampler lets you send an JDBC Request (an SQL query) to a database.</p>
 <p>Before using this you need to set up a
 <complink name="JDBC Connection Configuration"/> Configuration element
 </p>
 <p>
 If the Variable Names list is provided, then for each row returned by a Select statement, the variables are set up
 with the value of the corresponding column (if a variable name is provided), and the count of rows is also set up.
 For example, if the Select statement returns 2 rows of 3 columns, and the variable list is <code>A,,C</code>,
 then the following variables will be set up:
 <pre>
 A_#=2 (number of rows)
 A_1=column 1, row 1
 A_2=column 1, row 2
 C_#=2 (number of rows)
 C_1=column 3, row 1
 C_2=column 3, row 2
 </pre>
 If the Select statement returns zero rows, then the A_# and C_# variables would be set to 0, and no other variables would be set.
 </p>
 <p>
 Old variables are cleared if necessary - e.g. if the first select retrieves 6 rows and a second select returns only 3 rows,
 the additional variables for rows 4, 5 and 6 will be removed.
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 		<property name="Variable Name" required="Yes">
 		Name of the JMeter variable that the connection pool is bound to.
 		This must agree with the 'Variable Name' field of a JDBC Connection Configuration.
 		</property>
 		<property name="Query Type" required="Yes">Set this according to the statement type:
 		    <ul>
 		    <li>Select Statement</li>
 		    <li>Update Statement - use this for Inserts as well</li>
 		    <li>Callable Statement</li>
 		    <li>Prepared Select Statement</li>
 		    <li>Prepared Update Statement - use this for Inserts as well</li>
 		    <li>Commit</li>
 		    <li>Rollback</li>
 		    <li>Autocommit(false)</li>
 		    <li>Autocommit(true)</li>
 		    <li>Edit - this should be a variable reference that evaluates to one of the above</li>
 		    </ul>
 		</property>
         <property name="SQL Query" required="Yes">
         SQL query.
         Do not enter a trailing semi-colon.
         There is generally no need to use { and } to enclose Callable statements;
         however they mey be used if the database uses a non-standard syntax.
         [The JDBC driver automatically converts the statement if necessary when it is enclosed in {}].
         For example:
         <ul>
         <li>select * from t_customers where id=23</li>
         <li>CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (null,?, ?, null, null, null)
         <ul>
         <li>Parameter values: tablename,filename</li>
         <li>Parameter types:  VARCHAR,VARCHAR</li>
         </ul>
         </li>
         The second example assumes you are using Apache Derby.
         </ul>
         </property>
         <property name="Parameter values" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of parameter values. Use ]NULL[ to indicate a NULL parameter.
         (If required, the null string can be changed by defining the property "jdbcsampler.nullmarker".)
         <br></br>
         The list must be enclosed in double-quotes if any of the values contain a comma or double-quote,
         and any embedded double-quotes must be doubled-up, for example:
         <pre>"Dbl-Quote: "" and Comma: ,"</pre>
         There must be as many values as there are placeholders in the statement.
         </property>
         <property name="Parameter types" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of SQL parameter types (e.g. INTEGER, DATE, VARCHAR, DOUBLE).
         These are defined as fields in the class java.sql.Types, see for example:
         <a href="http://download.oracle.com/javase/1.5.0/docs/api/java/sql/Types.html">Javadoc for java.sql.Types</a>.
         [Note: JMeter will use whatever types are defined by the runtime JVM, 
         so if you are running on a different JVM, be sure to check the appropriate document]
         If the callable statement has INOUT or OUT parameters, then these must be indicated by prefixing the
         appropriate parameter types, e.g. instead of "INTEGER", use "INOUT INTEGER".
         If not specified, "IN" is assumed, i.e. "DATE" is the same as "IN DATE".
         <br></br>
         If the type is not one of the fields found in java.sql.Types, versions of JMeter after 2.3.2 also
         accept the corresponding integer number, e.g. since INTEGER == 4, you can use "INOUT 4".
         <br></br>
         There must be as many types as there are placeholders in the statement.
         </property>
         <property name="Variable Names" required="No">Comma-separated list of variable names to hold values returned by Select statements</property>
         <property name="Result Variable Name" required="No">
         If specified, this will create an Object variable containing a list of row maps.
         Each map contains the column name as the key and the column data as the value. Usage:<br></br>
         <code>columnValue = vars.getObject("resultObject").get(0).get("Column Name");</code>
         </property>
 </properties>
 
 <links>
         <link href="build-db-test-plan.html">Building a Database Test Plan</link>
         <complink name="JDBC Connection Configuration"/>
 </links>
 <note>Versions of JMeter after 2.3.2 use UTF-8 as the character encoding. Previously the platform default was used.</note>
 </component>
 
 <component name="Java Request" index="&sect-num;.1.4"  width="406" height="307" screenshot="java_request.png">
 
 <description><p>This sampler lets you control a java class that implements the
 <b><code>org.apache.jmeter.protocol.java.sampler.JavaSamplerClient</code></b> interface.
 By writing your own implementation of this interface,
 you can use JMeter to harness multiple threads, input parameter control, and
 data collection.</p>
 <p>The pull-down menu provides the list of all such implementations found by
 JMeter in its classpath.  The parameters can then be specified in the
 table below - as defined by your implementation.  Two simple examples (JavaTest and SleepTest) are provided.
 </p>
 <p>
 The JavaTest example sampler can be useful for checking test plans, because it allows one to set
 values in almost all the fields. These can then be used by Assertions, etc.
 The fields allow variables to be used, so the values of these can readily be seen.
 </p>
 </description>
 
 <note>The Add/Delete buttons don't serve any purpose at present.</note>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="Classname" required="Yes">The specific implementation of
         the JavaSamplerClient interface to be sampled.</property>
         <property name="Send Parameters with Request" required="No">A list of
         arguments that will be passed to the sampled class.  All arguments
         are sent as Strings.</property>
         </properties>
 </component>
 
 <p>The sleep time is calculated as follows:</p>
 <pre>
 SleepTime is in milliseconds
 SleepMask is used to add a "random" element to the time:
 totalSleepTime = SleepTime + (System.currentTimeMillis() % SleepMask)
 </pre>
 <component name="SOAP/XML-RPC Request" index="&sect-num;.1.5"  width="474" height="236" screenshot="soap_sampler.png">
 
 <description><p>This sampler lets you send a SOAP request to a webservice.  It can also be
 used to send XML-RPC over HTTP.  It creates an HTTP POST request, with the specified XML as the
 POST content. 
 To change the "Content-type" from the default of "text/xml", use a HeaderManager. 
 Note that the sampler will use all the headers from the HeaderManager.
 If a SOAP action is specified, that will override any SOAPaction in the HeaderManager.
 The primary difference between the soap sampler and
 webservice sampler, is the soap sampler uses raw post and does not require conformance to
 SOAP 1.1.</p>
 <note>For versions of JMeter later than 2.2, the sampler no longer uses chunked encoding by default.<br/>
 For screen input, it now always uses the size of the data.<br/>
 File input uses the file length as determined by Java.<br/>
 On some OSes this may not work for all files, in which case add a child Header Manager
 with Content-Length set to the actual length of the file.<br/>
 Or set Content-Length to -1 to force chunked encoding.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="URL" required="Yes">The URL to direct the SOAP request to.</property>
         <property name="Send SOAP action" required="No">Send a SOAP action header? (overrides the Header Manager)</property>
         <property name="Soap/XML-RPC Data" required="No">The Soap XML message, or XML-RPC instructions.
         Not used if the filename is provided.
         </property>
         <property name="Filename" required="No">If specified, then the contents of the file are sent, and the Data field is ignored</property>
         </properties>
 
 </component>
 
 <component name="WebService(SOAP) Request" index="&sect-num;.1.6"  width="460" height="781" screenshot="webservice_sampler.png">
 <description><p>This sampler has been tested with IIS Webservice running .NET 1.0 and .NET 1.1.
  It has been tested with SUN JWSDP, IBM webservices, Axis and gSoap toolkit for C/C++.
  The sampler uses Apache SOAP driver to serialize the message and set the header
  with the correct SOAPAction. Right now the sampler doesn't support automatic WSDL
  handling, since Apache SOAP currently does not provide support for it. Both IBM
  and SUN provide WSDL drivers. There are 3 options for the post data: text area,
  external file, or directory. If you want the sampler to randomly select a message,
  use the directory. Otherwise, use the text area or a file. The if either the
  file or path are set, it will not use the message in the text area. If you need
  to test a soap service that uses different encoding, use the file or path. If you
  paste the message in to text area, it will not retain the encoding and will result
  in errors. Save your message to a file with the proper encoding, and the sampler
  will read it as java.io.FileInputStream.</p>
  <p>An important note on the sampler is it will automatically use the proxy host
  and port passed to JMeter from command line, if those fields in the sampler are
  left blank. If a sampler has values in the proxy host and port text field, it
  will use the ones provided by the user. This behavior may not be what users
  expect.</p>
  <p>By default, the webservice sampler sets SOAPHTTPConnection.setMaintainSession
  (true). If you need to maintain the session, add a blank Header Manager. The
  sampler uses the Header Manager to store the SOAPHTTPConnection object, since
  the version of apache soap does not provide a easy way to get and set the cookies.</p>
  <p><b>Note:</b> If you are using CSVDataSet, do not check "Memory Cache". If memory
  cache is checked, it will not iterate to the next value. That means all the requests
  will use the first value.</p>
  <p>Make sure you use &amp;lt;soap:Envelope rather than &amp;lt;Envelope. For example:</p>
  <pre>
 &amp;lt;?xml version="1.0" encoding="utf-8"?>
 &amp;lt;soap:Envelope 
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
 xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
 &amp;lt;soap:Body>
 &amp;lt;foo xmlns="http://clients-xlmns"/>
 &amp;lt;/soap:Body>
 &amp;lt;/soap:Envelope>
 </pre>
 <note>The SOAP library that is used does not support SOAP 1.2, only SOAP 1.1. 
 Also the library does not provide access to the HTTP response code (e.g. 200) or message (e.g. OK). 
 To get round this, versions of JMeter after 2.3.2 check the returned message length.
 If this is zero, then the request is marked as failed.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="WSDL URL" required="No">The WSDL URL with the service description.
         Versions of JMeter after 2.3.1 support the file: protocol for local WSDL files.
         </property>
         <property name="Web Methods" required="No">Will be populated from the WSDL when the Load WSDL button is pressed.
         Select one of the methods and press the Configure button to populate the Protocol, Server, Port, Path and SOAPAction fields. 
         </property>
         <property name="Protocol" required="Yes">HTTP or HTTPS are acceptable protocol.</property>
         <property name="Server Name or IP" required="Yes">The hostname or IP address.</property>
         <property name="Port Number" required="Yes">Port Number.</property>
         <property name="Path" required="Yes">Path for the webservice.</property>
         <property name="SOAPAction" required="Yes">The SOAPAction defined in the webservice description or WSDL.</property>
         <property name="Soap/XML-RPC Data" required="Yes">The Soap XML message</property>
         <property name="Soap file" required="No">File containing soap message</property>
         <property name="Message Folder" required="No">Folder containing soap files</property>
         <property name="Memory cache" required="Yes">
         When using external files, setting this causes the file to be processed once and caches the result.
         This may use a lot of memory if there are many different large files.
         </property>
         <property name="Use HTTP Proxy" required="No">Check box if http proxy should be used</property>
         <property name="Proxy Host" required="No">Proxy hostname</property>
         <property name="Proxy Port" required="No">Proxy host port</property>
         </properties>
 
 </component>
 
 <component name="LDAP Request" index="&sect-num;.1.7" screenshot="ldap_request.png">
   <description>This Sampler lets you send a different Ldap request(Add, Modify, Delete and Search) to an LDAP server.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> The same way the <complink name="Login Config Element"/> also using for Login and password.
   </description>
 
   <p>There are two ways to create test cases for testing an LDAP Server.</p>
   <ol><li>Inbuilt Test cases.</li>
     <li>User defined Test cases.</li></ol>
 
     <p>There are four test scenarios of testing LDAP. The tests are given below:</p>
     <ol>
       <li>Add Test</li>
       <ol><li>Inbuilt test :
         <p>This will add a pre-defined entry in the LDAP Server and calculate
           the execution time. After execution of the test, the created entry will be
           deleted from the LDAP
           Server.</p></li>
           <li>User defined test :
             <p>This will add the entry in the LDAP Server. User has to enter all the
               attributes in the table.The entries are collected from the table to add. The
               execution time is calculated. The created entry will not be deleted after the
               test.</p></li></ol>
 
               <li>Modify Test</li>
               <ol><li>Inbuilt test :
                 <p>This will create a pre-defined entry first, then will modify the
                   created	entry in the LDAP Server.And calculate the execution time. After
                   execution
                   of the test, the created entry will be deleted from the LDAP Server.</p></li>
                   <li>User defined test
                     <p>This will modify the entry in the LDAP Server. User has to enter all the
                       attributes in the table. The entries are collected from the table to modify.
                       The execution time is calculated. The entry will not be deleted from the LDAP
                       Server.</p></li></ol>
 
                       <li>Search Test</li>
                       <ol><li>Inbuilt test :
                         <p>This will create the entry first, then will search if the attributes
                           are available. It calculates the execution time of the search query. At the
                           end of  the execution,created entry will be deleted from the LDAP Server.</p></li>
                           <li>User defined test
                             <p>This will search the user defined entry(Search filter) in the Search
                               base (again, defined by the user). The entries should be available in the LDAP
                               Server. The execution time is  calculated.</p></li></ol>
 
                               <li>Delete Test</li>
                               <ol><li>Inbuilt test :
                                 <p>This will create a pre-defined entry first, then it will be deleted
                                   from the LDAP Server. The execution time is calculated.</p></li>
 
                                   <li>User defined test
                                     <p>This will delete the user-defined entry in the LDAP Server. The entries
                                       should be available in the LDAP Server. The execution time is calculated.</p></li></ol></ol>
                                       <properties>
                                         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
                                         <property name="Server Name or IP" required="Yes">Domain name or IP address of the LDAP server.
                                           JMeter assumes the LDAP server is listening on the default port(389).</property>
                                           <property name="Port" required="Yes">default port(389).</property>
                                           <property name="root DN" required="Yes">DN for the server to communicate</property>
                                           <property name="Username" required="Usually">LDAP server username.</property>
                                           <property name="Password" required="Usually">LDAP server password.</property>
                                           <property name="Entry DN" required="Yes">the name of the context to create or Modify; may not be empty Example: do you want to add cn=apache,ou=test
                                             you have to add in table name=cn, value=apache
                                           </property>
                                           <property name="Delete" required="Yes">the name of the context to Delete; may not be empty</property>
                                           <property name="Search base" required="Yes">the name of the context or object to search</property>
                                           <property name="Search filter" required="Yes"> the filter expression to use for the search; may not be null</property>
                                           <property name="add test" required="Yes"> this name, value pair to added in the given context object</property>
                                           <property name="modify test" required="Yes"> this name, value pair to add or modify in the given context object</property>
                                       </properties>
 
                                       <links>
                                         <link href="build-ldap-test-plan.html">Building an Ldap Test Plan</link>
                                         <complink name="LDAP Request Defaults"/>
                                       </links>
 
 </component>
 
 <component name="LDAP Extended Request" index="&sect-num;.1.8" screenshot="ldapext_request.png">
   <description>This Sampler can send all 8 different LDAP request to an LDAP server. It is an extended version of the LDAP sampler,
   therefore it is harder to configure, but can be made much closer resembling a real LDAP session.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Extended Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> </description>
 
    <p>There are nine test operations defined. These operations are given below:</p>
     <ol>
       <li><b>Thread bind</b></li>
       <p>Any LDAP request is part of an LDAP session, so the first thing that should be done is starting a session to the LDAP server.
        For starting this session a thread bind is used, which is equal to the LDAP "bind" operation.
        The user is requested to give a username (Distinguished name) and password, 
        which will be used to initiate a session.
        When no password, or the wrong password is specified, an anonymous session is started. Take care,
        omitting the password will not fail this test, a wrong password will. </p>
      <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
      <property name="Servername" required="Yes">The name (or IP-address) of the LDAP server.</property>
      <property name="Port" required="No">The port number that the LDAP server is listening to. If this is omitted 
      JMeter assumes the LDAP server is listening on the default port(389).</property>
      <property name="DN" required="No">The distinguished name of the base object that will be used for any subsequent operation. 
      It can be used as a starting point for all operations. You cannot start any operation on a higher level than this DN!</property>
      <property name="Username" required="No">Full distinguished name of the user as which you want to bind.</property>
      <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind. 
      If is is incorrect, the sampler will return an error and revert to an anonymous bind.</property>
     </properties>
  <br />       
       <li><b>Thread unbind</b></li>
       <p>This is simply the operation to end a session. 
       It is equal to the LDAP "unbind" operation.</p>
      <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
     </properties>
      
  <br />       
       <li><b>Single bind/unbind</b></li>
 		<p> This is a combination of the LDAP "bind" and "unbind" operations.
 		It can be used for an authentication request/password check for any user. It will open an new session, just to
 		check the validity of the user/password combination, and end the session again.</p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Username" required="Yes">Full distinguished name of the user as which you want to bind.</property>
      <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind. 
      If is is incorrect, the sampler will return an error.</property>
      </properties>
 		
  <br />       
       <li><b>Rename entry</b></li>
        <p>This is the LDAP "moddn" operation. It can be used to rename an entry, but 
        also for moving an entry or a complete subtree to a different place in 
        the LDAP tree.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Old entry name" required="Yes">The current distinguished name of the object you want to rename or move, 
       relative to the given DN in the thread bind operation.</property>
      <property name="New distinguished name" required="Yes">The new distinguished name of the object you want to rename or move, 
       relative to the given DN in the thread bind operation.</property>
      </properties>
        
  <br />       
         <li><b>Add test</b></li>
        <p>This is the ldap "add" operation. It can be used to add any kind of 
        object to the LDAP server.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry DN" required="Yes">Distinguished name of the object you want to add, relative to the given DN in the thread bind operation.</property>
      <property name="Add test" required="Yes">A list of attributes and their values you want to use for the object.
      If you need to add a multiple value attribute, you need to add the same attribute with their respective 
      values several times to the list.</property>
      </properties>
        
  <br />       
       <li><b>Delete test</b></li>
        <p> This is the LDAP "delete" operation, it can be used to delete an 
        object from the LDAP tree </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Delete" required="Yes">Distinguished name of the object you want to delete, relative to the given DN in the thread bind operation.</property>
       </properties>
        
  <br />       
       <li><b>Search test</b></li>
        <p>This is the LDAP "search" operation, and will be used for defining searches.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Search base" required="No">Distinguished name of the subtree you want your 
       search to look in, relative to the given DN in the thread bind operation.</property>
       <property name="Search Filter" required="Yes">searchfilter, must be specified in LDAP syntax.</property>
       <property name="Scope" required="No">Use 0 for baseobject-, 1 for onelevel- and 2 for a subtree search. (Default=0)</property>
       <property name="Size Limit" required="No">Specify the maximum number of results you want back from the server. (default=0, which means no limit.) When the sampler hits the maximum number of results, it will fail with errorcode 4</property>
       <property name="Time Limit" required="No">Specify the maximum amount of (cpu)time (in miliseconds) that the server can spend on your search. Take care, this does not say anything about the responsetime. (default is 0, which means no limit)</property>
       <property name="Attributes" required="No">Specify the attributes you want to have returned, seperated by a semicolon. An empty field will return all attributes</property>
       <property name="Return object" required="No">Whether the object will be returned (true) or not (false). Default=false</property>
       <property name="Dereference aliases" required="No">If true, it will dereference aliases, if false, it will not follow them (default=false)</property>
      </properties>
 
  <br />       
       <li><b>Modification test</b></li>
        <p>This is the LDAP "modify" operation. It can be used to modify an object. It
        can be used to add, delete or replace values of an attribute. </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry name" required="Yes">Distinguished name of the object you want to modify, relative 
       to the given DN in the thread bind operation</property>
      <property name="Modification test" required="Yes">The attribute-value-opCode triples. The opCode can be any 
      valid LDAP operationCode (add, delete/remove or replace). If you don't specify a value with a delete operation,
      all values of the given attribute will be deleted. If you do specify a value in a delete operation, only 
      the given value will be deleted. If this value is non-existent, the sampler will fail the test.</property>
      </properties>
        
  <br />       
       <li><b>Compare</b></li>
        <p>This is the LDAP "compare" operation. It can be used to compare the value 
        of a given attribute with some already known value. In reality this is mostly 
        used to check whether a given person is a member of some group. In such a case
         you can compare the DN of the user as a given value, with the values in the
          attribute "member" of an object of the type groupOfNames.
          If the compare operation fails, this test fails with errorcode 49.</p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry DN" required="Yes">The current distinguished name of the object of 
       which you want  to compare an attribute, relative to the given DN in the thread bind operation.</property>
      <property name="Compare filter" required="Yes">In the form "attribute=value"</property>
      </properties>
     </ol>
        
     <links>
       <link href="build-ldapext-test-plan.html">Building an LDAP Test Plan</link>
       <complink name="LDAP Extended Request Defaults"/>
     </links>
 
 </component>
 
 
 
 
 <component name="Access Log Sampler" index="&sect-num;.1.9"  width="582" height="301" screenshot="accesslogsampler.png">
 <center><h2>(Alpha Code)</h2></center>
 <description><p>AccessLogSampler was designed to read access logs and generate http requests.
 For those not familiar with the access log, it is the log the webserver maintains of every
 request it accepted. This means the every image and html file. The current implementation
 is complete, but some features have not been enabled. There is a filter for the access
 log parser, but I haven't figured out how to link to the pre-processor. Once I do, changes
 to the sampler will be made to enable that functionality.</p>
 <p>Tomcat uses the common format for access logs. This means any webserver that uses the
 common log format can use the AccessLogSampler. Server that use common log format include:
 Tomcat, Resin, Weblogic, and SunOne. Common log format looks
 like this:</p>
 <p>127.0.0.1 - - [21/Oct/2003:05:37:21 -0500] "GET /index.jsp?%2Findex.jsp= HTTP/1.1" 200 8343</p>
 <p>The current implemenation of the parser only looks at the text within the quotes.
 Everything else is stripped out and igored. For example, the response code is completely
 ignored by the parser. For the future, it might be nice to filter out entries that
 do not have a response code of 200. Extending the sampler should be fairly simple. There
 are two interfaces you have to implement.</p>
 <p>org.apache.jmeter.protocol.http.util.accesslog.LogParser</p>
 <p>org.apache.jmeter.protocol.http.util.accesslog.Generator</p>
 <p>The current implementation of AccessLogSampler uses the generator to create a new
 HTTPSampler. The servername, port and get images are set by AccessLogSampler. Next,
 the parser is called with integer 1, telling it to parse one entry. After that,
 HTTPSampler.sample() is called to make the request.
 <code>
 <pre>
             samp = (HTTPSampler) GENERATOR.generateRequest();
             samp.setDomain(this.getDomain());
             samp.setPort(this.getPort());
             samp.setImageParser(this.isImageParser());
             PARSER.parse(1);
             res = samp.sample();
             res.setSampleLabel(samp.toString());
 </pre>
 </code>
 The required methods in LogParser are: setGenerator(Generator) and parse(int).
 Classes implementing Generator interface should provide concrete implementation
 for all the methods. For an example of how to implement either interface, refer to
 StandardGenerator and TCLogParser.
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server" required="Yes">Domain name or IP address of the web server.</property>
         <property name="Port" required="No (defaults to 80)">Port the web server is listening to.</property>
         <property name="Log parser class" required="Yes (default provided)">The log parser class is responsible for parsing the logs.</property>
         <property name="Filter" required="No">The filter class is used to filter out certain lines.</property>
         <property name="Location of log file" required="Yes">The location of the access log file.</property>
 </properties>
 <p>
 The TCLogParser processes the access log independently for each thread.
 The SharedTCLogParser and OrderPreservingLogParser share access to the file, 
 i.e. each thread gets the next entry in the log.
 </p>
 <p>
 The SessionFilter is intended to handle Cookies across threads. 
 It does not filter out any entries, but modifies the cookie manager so that the cookies for a given IP are
 processed by a single thread at a time. If two threads try to process samples from the same client IP address,
 then one will be forced to wait until the other has completed.
 </p>
 <p>
 The LogFilter is intended to allow access log entries to be filtered by filename and regex,
 as well as allowing for the replacement of file extensions. However, it is not currently possible
 to configure this via the GUI, so it cannot really be used.
 </p>
 </component>
 
 <component name="BeanShell Sampler" index="&sect-num;.1.10"  width="592" height="303" screenshot="beanshellsampler.png">
 	<description><p>This sampler allows you to write a sampler using the BeanShell scripting language.		
 </p><p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the ThreadListener and TestListener interface methods.
 These must be defined in the initialisation file.
 See the file BeanShellListeners.bshrc for example definitions.
 </p>
 <p>
 From JMeter version 2.5.1, the BeanShell sampler also supports the Interruptible interface.
 The interrupt() method can be defined in the script or the init file.
 </p>
 	</description>
 <properties>
 	<property name="Name" required="No">Descriptive name for this controller that is shown in the tree.
     The name is stored in the script variable Label</property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     This is intended for use with script files; for scripts defined in the GUI, you can use whatever
     variable and function references you need within the script itself.
 	The parameters are stored in the following variables:
 	<ul>
 		<li>Parameters - string containing the parameters as a single variable</li>
 	    <li>bsh.args - String array containing parameters, split on white-space</li>
 	</ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run.
     The file name is stored in the script variable FileName</property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. 
     The return value (if not null) is stored as the sampler result.</property>
 </properties>
 <p>
 N.B. Each Sampler instance has its own BeanShell interpeter,
 and Samplers are only called from a single thread
 </p><p>
 If the property "beanshell.sampler.init" is defined, it is passed to the Interpreter
 as the name of a sourced file.
 This can be used to define common methods and variables. 
 There is a sample init file in the bin directory: BeanShellSampler.bshrc.
 </p><p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 		<p>Before invoking the script, some variables are set up in the BeanShell interpreter:
 			</p>
 				<p>The contents of the Parameters field is put into the variable "Parameters".
 			The string is also split into separate tokens using a single space as the separator, and the resulting list
 			is stored in the String array bsh.args.</p>
 			<p>The full list of BeanShell variables that is set up is as follows:</p>
 		<ul>
 		<li>log - the Logger</li>
 		<li>Label - the Sampler label</li>
 		<li>FileName - the file name, if any</li>
 		<li>Parameters - text from the Parameters field</li>
 		<li>bsh.args - the parameters, split as described above</li>
 		<li>SampleResult - pointer to the current SampleResult</li>
 			<li>ResponseCode = 200</li>
 			<li>ResponseMessage = "OK"</li>
 			<li>IsSuccess = true</li>
 			<li>ctx - JMeterContext</li>
 			<li>vars - JMeterVariables  - e.g. vars.get("VAR1"); vars.put("VAR2","value"); vars.remove("VAR3"); vars.putObject("OBJ1",new Object());</li>
             <li>props - JMeterProperties - e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
 		</ul>
 		<p>When the script completes, control is returned to the Sampler, and it copies the contents
 			of the following script variables into the corresponding variables in the SampleResult:</p>
 			<ul>
 			<li>ResponseCode - for example 200</li>
 			<li>ResponseMessage - for example "OK"</li>
 			<li>IsSuccess - true/false</li>
 			</ul>
 			<p>The SampleResult ResponseData is set from the return value of the script.
 			Since version 2.1.2, if the script returns null, it can set the response directly, by using the method 
 			SampleResult.setResponseData(data), where data is either a String or a byte array.
 			The data type defaults to "text", but can be set to binary by using the method
 			SampleResult.setDataType(SampleResult.BINARY).
 			</p>
 			<p>The SampleResult variable gives the script full access to all the fields and
 				methods in the SampleResult. For example, the script has access to the methods
 				setStopThread(boolean) and setStopTest(boolean).
 				
 				Here is a simple (not very useful!) example script:</p>
 				
 <pre>
 if (bsh.args[0].equalsIgnoreCase("StopThread")) {
     log.info("Stop Thread detected!");
     SampleResult.setStopThread(true);
 }
 return "Data from sample with Label "+Label;
 //or, since version 2.1.2
 SampleResult.setResponseData("My data");
 return null;
 </pre>
 <p>Another example:<br></br> ensure that the property <b>beanshell.sampler.init=BeanShellSampler.bshrc</b> is defined in jmeter.properties. 
 The following script will show the values of all the variables in the ResponseData field:
 </p>
 <pre>
 return getVariables();
 </pre>
 <p>
 For details on the methods available for the various classes (JMeterVariables, SampleResult etc) please check the Javadoc or the source code.
 Beware however that misuse of any methods can cause subtle faults that may be difficult to find ...
 </p>
 </component>
 
 
 <component name="BSF Sampler" index="&sect-num;.1.11"  width="622" height="267" screenshot="bsfsampler.png">
 	<description><p>This sampler allows you to write a sampler using a BSF scripting language.<br></br>
 		See the <a href="http://jakarta.apache.org/bsf/index.html">Apache Bean Scripting Framework</a>
 		website for details of the languages supported.
 		You may need to download the appropriate jars for the language; they should be put in the JMeter <b>lib</b> directory.
 		</p>
 		<p>By default, JMeter supports the following languages:</p>
 		<ul>
 		<li>javascript</li>
         <li>jexl (JMeter version 2.3.2 and later)</li>
         <li>xslt</li>
 		</ul>
 	</description>
 <properties>
 	<property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 	<property name="Scripting Language" required="Yes">Name of the BSF scripting language to be used.
 	N.B. Not all the languages in the drop-down list are supported by default.
 	The following are supported: jexl, javascript, xslt.
 	Others may be available if the appropriate jar is installed in the JMeter lib directory.
 	</property>
 	<property name="Script File" required="No">Name of a file to be used as a BSF script</property>
 	<property name="Parameters" required="No">List of parameters to be passed to the script file or the script.</property>
 	<property name="Script" required="Yes (unless script file is provided)">Script to be passed to BSF language</property>
 </properties>
 <p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 <p>
 Before invoking the script, some variables are set up.
 Note that these are BSF variables - i.e. they can be used directly in the script.
 </p>
 <ul>
 <li>log - the Logger</li>
 <li>Label - the Sampler label</li>
 <li>FileName - the file name, if any</li>
 <li>Parameters - text from the Parameters field</li>
 <li>args - the parameters, split as described above</li>
 <li>SampleResult - pointer to the current SampleResult</li>
 <li>sampler - pointer to current Sampler</li>
 <li>ctx - JMeterContext</li>
 <li>vars - JMeterVariables  - e.g. vars.get("VAR1"); vars.put("VAR2","value"); vars.remove("VAR3"); vars.putObject("OBJ1",new Object());</li>
 <li>props - JMeterProperties - e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
 <li>OUT - System.out - e.g. OUT.println("message")</li>
 </ul>
 <p>
 The SampleResult ResponseData is set from the return value of the script.
 If the script returns null, it can set the response directly, by using the method 
 SampleResult.setResponseData(data), where data is either a String or a byte array.
 The data type defaults to "text", but can be set to binary by using the method
 SampleResult.setDataType(SampleResult.BINARY).
 </p>
 <p>
 The SampleResult variable gives the script full access to all the fields and
 methods in the SampleResult. For example, the script has access to the methods
 setStopThread(boolean) and setStopTest(boolean).
 </p>
 <p>
 Unlike the Beanshell Sampler, the BSF Sampler does not set the ResponseCode, ResponseMessage and sample status via script variables.
 Currently the only way to changes these is via the SampleResult methods:
 <ul>
 <li>SampleResult.setSuccessful(true/false)</li>
 <li>SampleResult.setResponseCode("code")</li>
 <li>SampleResult.setResponseMessage("message")</li>
 </ul>
 </p>
 </component>
 
 <component name="JSR223 Sampler" index="&sect-num;.1.11.1">
 <description>
 <p>
 The JSR223 Sampler allows JSR223 script code to be used to perform a sample.
 For details, see <complink name="BSF Sampler"/>.
 </p>
 </description>
 </component>
 
 <component name="TCP Sampler" index="&sect-num;.1.12"  width="743" height="357" screenshot="tcpsampler.png">
 	<description>
 		<p>
 		The TCP Sampler opens a TCP/IP connection to the specified server.
 		It then sends the text, and waits for a response.
 		<br></br>
 		If "Re-use connection" is selected, connections are shared between Samplers in the same thread,
 		provided that the exact same host name string and port are used. 
 		Different hosts/port combinations will use different connections, as will different threads. 
 		<br></br>
 		If an error is detected - or "Re-use connection" is not selected - the socket is closed. 
 		Another socket will be reopened on the next sample.
 		<br></br>
 		The following properties can be used to control its operation:
 		</p>
 		<ul>
 			<li>tcp.status.prefix - text that precedes a status number</li>
 			<li>tcp.status.suffix - text that follows a status number</li>
 			<li>tcp.status.properties - name of property file to convert status codes to messages</li>
 			<li>tcp.handler - Name of TCP Handler class (default TCPClientImpl) - only used if not specified on the GUI</li>
 		</ul>
 		The class that handles the connection is defined by the GUI, failing that the property tcp.handler. 
 		If not found, the class is then searched for in the package org.apache.jmeter.protocol.tcp.sampler.
 		<p>
 		Users can provide their own implementation.
 		The class must extend org.apache.jmeter.protocol.tcp.sampler.TCPClient.
 		</p>
 		<p>
 		The following implementations are currently provided.
 		<ul>
 		<li>TCPClientImpl</li>
         <li>BinaryTCPClientImpl</li>
         <li>LengthPrefixedBinaryTCPClientImpl</li>
 		</ul>
 		The implementations behave as follows:
 		</p>
 		<p><b>TCPClientImpl</b><br></br>
 		This implementation is fairly basic.
         When reading the response, it reads until the end of line byte, if this is defined
         by setting the property <b>tcp.eolByte</b>, otherwise until the end of the input stream.
         </p>
         <p><b>BinaryTCPClientImpl</b><br></br>
         This implementation converts the GUI input, which must be a hex-encoded string, into binary,
         and performs the reverse when reading the response.
         When reading the response, it reads until the end of message byte, if this is defined
         by setting the property <b>tcp.BinaryTCPClient.eomByte</b>, otherwise until the end of the input stream.
         </p>
         <p><b>LengthPrefixedBinaryTCPClientImpl</b><br></br>
         This implementation extends BinaryTCPClientImpl by prefixing the binary message data with a binary length byte.
         The length prefix defaults to 2 bytes.
         This can be changed by setting the property <b>tcp.binarylength.prefix.length</b>.
         </p>
         <p><b>Timeout handling</b>
         If the timeout is set, the read will be terminated when this expires. 
         So if you are using an eolByte/eomByte, make sure the timeout is sufficiently long,
         otherwise the read will be terminated early.    
 		</p>
 		<p><b>Response handling</b>
 		<br></br>
 		If tcp.status.prefix is defined, then the response message is searched for the text following
 		that up to the suffix. If any such text is found, it is used to set the response code.
 		The response message is then fetched from the properties file (if provided).
 		<br></br>
