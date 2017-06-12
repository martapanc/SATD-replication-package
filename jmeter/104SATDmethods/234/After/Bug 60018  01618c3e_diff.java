diff --git a/bin/jmeter.properties b/bin/jmeter.properties
index 638eba09d..21e7fa66a 100644
--- a/bin/jmeter.properties
+++ b/bin/jmeter.properties
@@ -256,1000 +256,1009 @@ remote_hosts=127.0.0.1
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
 view.results.tree.renderers_order=.RenderAsText,.RenderAsRegexp,.RenderAsCssJQuery,.RenderAsXPath,.RenderAsHTML,.RenderAsHTMLWithEmbedded,.RenderAsHTMLFormated,.RenderAsDocument,.RenderAsJSON,.RenderAsXML
 
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
+
+
+#
+# Apply a factor on computed pauses by the following Timers:
+# - Gaussian Random Timer
+# - Uniform Random Timer
+# - Poisson Random Timer
+#
+#timer.factor=1.0f
\ No newline at end of file
diff --git a/src/components/org/apache/jmeter/timers/RandomTimer.java b/src/components/org/apache/jmeter/timers/RandomTimer.java
index 365d0d3ca..7c6887cf7 100644
--- a/src/components/org/apache/jmeter/timers/RandomTimer.java
+++ b/src/components/org/apache/jmeter/timers/RandomTimer.java
@@ -1,73 +1,81 @@
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
 
 package org.apache.jmeter.timers;
 
 import java.io.Serializable;
 import java.util.Random;
 import java.util.concurrent.ThreadLocalRandom;
 
 import org.apache.jmeter.testelement.property.DoubleProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 
 /**
  * This class implements a random timer with its own panel and fields for value
  * update and user interaction. Since this class does not define the delay()
  * method, is abstract and must be extended to provide full functionality.
  *
  */
-public abstract class RandomTimer extends ConstantTimer implements Timer, Serializable {
+public abstract class RandomTimer extends ConstantTimer implements ModifiableTimer, Serializable {
     private static final long serialVersionUID = 241L;
 
     public static final String RANGE = "RandomTimer.range";
 
     /**
      * No-arg constructor.
      */
     public RandomTimer() {
     }
 
     /**
      * Set the range value.
      */
     @Override
     public void setRange(double range) {
         setProperty(new DoubleProperty(RANGE, range));
     }
 
     public void setRange(String range) {
         setProperty(new StringProperty(RANGE, range));
     }
 
     /**
      * Get the range value.
      *
      * @return double
      */
     @Override
     public double getRange() {
         return this.getPropertyAsDouble(RANGE);
     }
     
     /**
      * @return {@link Random} Thread local Random
      */
     protected Random getRandom() {
         return ThreadLocalRandom.current();
     }
+
+    /**
+     * @see org.apache.jmeter.timers.ModifiableTimer#isModifiable()
+     */
+    @Override
+    public boolean isModifiable() {
+        return true;
+    }
 }
diff --git a/src/core/org/apache/jmeter/threads/JMeterThread.java b/src/core/org/apache/jmeter/threads/JMeterThread.java
index 33f365fd5..ff275bbfe 100644
--- a/src/core/org/apache/jmeter/threads/JMeterThread.java
+++ b/src/core/org/apache/jmeter/threads/JMeterThread.java
@@ -1,960 +1,981 @@
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
+import org.apache.jmeter.timers.ModifiableTimer;
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
 
     public static final String PACKAGE_OBJECT = "JMeterThread.pack"; // $NON-NLS-1$
 
     public static final String LAST_SAMPLE_OK = "JMeterThread.last_sample_ok"; // $NON-NLS-1$
 
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     /** How often to check for shutdown during ramp-up, default 1000ms */
     private static final int RAMPUP_GRANULARITY =
             JMeterUtils.getPropDefault("jmeterthread.rampup.granularity", 1000); // $NON-NLS-1$
 
+    private static final float TIMER_FACTOR = JMeterUtils.getPropDefault("timer.factor", 1.0f);
+
+    /**
+     * 1 as float
+     */
+    private static final float ONE_AS_FLOAT = 1.0f;
+
     private final Controller threadGroupLoopController;
 
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
         threadGroupLoopController = (Controller) testTree.getArray()[0];
         SearchByClass<TestIterationListener> threadListenerSearcher = new SearchByClass<>(TestIterationListener.class); // TL - IS
         test.traverse(threadListenerSearcher);
         testIterationStartListeners = threadListenerSearcher.getSearchResults();
         SearchByClass<SampleMonitor> sampleMonitorSearcher = new SearchByClass<>(SampleMonitor.class);
         test.traverse(sampleMonitorSearcher);
         sampleMonitors = sampleMonitorSearcher.getSearchResults();
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
             while (running) {
                 Sampler sam = threadGroupLoopController.next();
                 while (running && sam != null) {
                     processSampler(sam, null, threadContext);
                     threadContext.cleanAfterSample();
                     
                     // restart of the next loop 
                     // - was requested through threadContext
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
                         sam = null;
                         threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
                         threadContext.setRestartNextLoop(false);
                     }
                     else {
                         sam = threadGroupLoopController.next();
                     }
                 }
                 
                 if (threadGroupLoopController.isDone()) {
                     running = false;
                     log.info("Thread is done: " + threadName);
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
             log.info("Stop Thread seen for thread " + getThreadName()+", reason:"+ e.toString());
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
         TransactionSampler transactionSampler = null;
         if(sam instanceof TransactionSampler) {
             transactionSampler = (TransactionSampler) sam;
         }
 
         Sampler realSampler = findRealSampler(sam);
         if(realSampler == null) {
             throw new IllegalStateException("Got null subSampler calling findRealSampler for:"+sam.getName()+", sam:"+sam);
         }
         // Find parent controllers of current sampler
         FindTestElementsUpToRootTraverser pathToRootTraverser = new FindTestElementsUpToRootTraverser(realSampler);
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
      * Find the Real sampler (Not TransactionSampler) that really generated an error
      * The Sampler provided is not always the "real" one, it can be a TransactionSampler, 
      * if there are some other controllers (SimpleController or other implementations) between this TransactionSampler and the real sampler, 
      * triggerEndOfLoop will not be called for those controllers leaving them in "ugly" state.
      * the following method will try to find the sampler that really generate an error
      * @param sampler
      * @return {@link Sampler}
      */
     private Sampler findRealSampler(Sampler sampler) {
         Sampler realSampler = sampler;
         while(realSampler instanceof TransactionSampler) {
             realSampler = ((TransactionSampler) realSampler).getSubSampler();
         }
         return realSampler;
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
      * Broadcast the result to the sample listeners
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
         if(!sampleMonitors.isEmpty()) {
             for(SampleMonitor monitor : sampleMonitors) {
                 monitor.sampleStarting(sampler);
             }
         }
         SampleResult result = null;
         try {
             result = sampler.sample(null); // TODO: remove this useless Entry parameter
         } finally {
             if(!sampleMonitors.isEmpty()) {
                 for(SampleMonitor monitor : sampleMonitors) {
                     monitor.sampleEnded(sampler);
                 }
             }
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
         
         threadGroupLoopController.initialize();
         IterationListener iterationListener = new IterationListener();
         threadGroupLoopController.addIterationListener(iterationListener);
 
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
             threadGroupLoopController.removeIterationListener(iterationListener);
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
                     SampleResult[] children = parent.getSubResults();
                     boolean childError = false;
                     for (SampleResult childSampleResult : children) {
                         processAssertion(childSampleResult, assertion);
                         if (!childSampleResult.isSuccessful()) {
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
         long totalDelay = 0;
         for (Timer timer : timers) {
             TestBeanHelper.prepare((TestElement) timer);
-            totalDelay += timer.delay();
+            long delay = timer.delay();
+            if(TIMER_FACTOR != ONE_AS_FLOAT && 
+                    // TODO Improve this with Optional methods when migration to Java8 is completed 
+                    ((timer instanceof ModifiableTimer) 
+                            && ((ModifiableTimer)timer).isModifiable())) {
+                if(log.isDebugEnabled()) {
+                    log.debug("Applying TIMER_FACTOR:"
+                            +TIMER_FACTOR + " on timer:"
+                            +((TestElement)timer).getName()
+                            + " for thread:"+getThreadName());
+                }
+                delay = Math.round(delay * TIMER_FACTOR);
+            }
+            totalDelay += delay;
         }
         if (totalDelay > 0) {
             try {
                 if(scheduler) {
                     // We reduce pause to ensure end of test is not delayed by a sleep ending after test scheduled end
                     // See Bug 60049
                     long now = System.currentTimeMillis();
                     if(now + totalDelay > endTime) {
                         totalDelay = endTime - now;
                     }
                 }
                 TimeUnit.MILLISECONDS.sleep(totalDelay);
             } catch (InterruptedException e) {
                 log.warn("The delay timer was interrupted - probably did not wait as long as intended.");
             }
         }
     }
 
     void notifyTestListeners() {
         threadVars.incIteration();
         for (TestIterationListener listener : testIterationStartListeners) {
             if (listener instanceof TestElement) {
                 listener.testIterationStart(new LoopIterationEvent(threadGroupLoopController, threadVars.getIteration()));
                 ((TestElement) listener).recoverRunningVersion();
             } else {
                 listener.testIterationStart(new LoopIterationEvent(threadGroupLoopController, threadVars.getIteration()));
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
diff --git a/src/core/org/apache/jmeter/timers/ModifiableTimer.java b/src/core/org/apache/jmeter/timers/ModifiableTimer.java
new file mode 100644
index 000000000..276c12a12
--- /dev/null
+++ b/src/core/org/apache/jmeter/timers/ModifiableTimer.java
@@ -0,0 +1,32 @@
+/*
+ * Licensed to the Apache Software Foundation (ASF) under one or more
+ * contributor license agreements.  See the NOTICE file distributed with
+ * this work for additional information regarding copyright ownership.
+ * The ASF licenses this file to You under the Apache License, Version 2.0
+ * (the "License"); you may not use this file except in compliance with
+ * the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ *
+ */
+
+package org.apache.jmeter.timers;
+
+
+/**
+ * This interface identifies Modifiable timers to which a factor can be applied
+ * @since 3.1
+ */
+public interface ModifiableTimer extends Timer {
+
+    /**
+     * @return true if factor can be applied to it
+     */
+    boolean isModifiable();
+}
diff --git a/src/core/org/apache/jmeter/util/JMeterUtils.java b/src/core/org/apache/jmeter/util/JMeterUtils.java
index 50ca5a156..4bb760c5a 100644
--- a/src/core/org/apache/jmeter/util/JMeterUtils.java
+++ b/src/core/org/apache/jmeter/util/JMeterUtils.java
@@ -1,1430 +1,1450 @@
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
 import org.apache.jorphan.util.JMeterError;
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
+    
+    /**
+     * Get a float value with default if not present.
+     *
+     * @param propName
+     *            the name of the property.
+     * @param defaultVal
+     *            the default value.
+     * @return The PropDefault value
+     */
+    public static float getPropDefault(String propName, float defaultVal) {
+        float ans;
+        try {
+            ans = Float.parseFloat(appProperties.getProperty(propName, Float.toString(defaultVal)).trim());
+        } catch (Exception e) {
+            log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching float property:'"+propName+"', defaulting to:"+defaultVal);
+            ans = defaultVal;
+        }
+        return ans;
+    }
 
     /**
      * Get a String value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value applying a trim on it
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
 
     /**
      * Return delimiterValue handling the TAB case
      * @param delimiterValue Delimited value 
      * @return String delimited modified to handle correctly tab
      * @throws JMeterError if delimiterValue has a length different from 1
      */
     public static String getDelimiter(String delimiterValue) {
         if (delimiterValue.equals("\\t")) {// Make it easier to enter a tab (can use \<tab> but that is awkward)
             delimiterValue="\t";
         }
 
         if (delimiterValue.length() != 1){
             throw new JMeterError("Delimiter '"+delimiterValue+"' must be of length 1.");
         }
         return delimiterValue;
     }
 
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 433a98685..0f0ef3edc 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,347 +1,349 @@
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
     <li>Since 3.1 version, <code>jmeter.save.saveservice.connect_time</code> property value is true, meaning CSV file for results will contain an additional column containing connection time, see <bugzilla>60106</bugzilla></li>
+    <li>Since 3.1 version, Random Timer subclasses (Gaussian Random Timer, Uniform Random Timer and Poisson Random Timer) implement interface <code><a href="./api/org/apache/jmeter/timers/ModifiableTimer.html">org.apache.jmeter.timers.ModifiableTimer</a></code></li>
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
+    <li><bug>60018</bug>Timer : Add a factor to apply on pauses. Partly based on a patch by Ubik Load Pack (support at ubikloadpack.com)</li>
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
     <li><bug>60106</bug>Settings defaults : Switch "jmeter.save.saveservice.connect_time" to true (after 3.0)</li>
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
     <li><bug>60125</bug>Report / Dashboard : Dashboard cannot be generated if the default delimiter is <code>\t</code>. Based on a report from Tamas Szabadi (tamas.szabadi at rightside.co)</li>
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
diff --git a/xdocs/usermanual/component_reference.xml b/xdocs/usermanual/component_reference.xml
index 1b7954c3a..251bb166a 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -3934,2001 +3934,2010 @@ Note also that the cookie name must be unique - if a second cookie is defined wi
 cookie table entries.</property>
   <property name="Save As Button" required="N/A">
   Save the current cookie table to a file (does not save any cookies extracted from HTTP Responses).
   </property>
 </properties>
 
 </component>
 
 <component name="HTTP Request Defaults" index="&sect-num;.4.7" width="879" height="469" 
          screenshot="http-config/http-request-defaults.png">
 <description><p>This element lets you set default values that your HTTP Request controllers use.  For example, if you are
 creating a Test Plan with 25 HTTP Request controllers and all of the requests are being sent to the same server,
 you could add a single HTTP Request Defaults element with the "<code>Server Name or IP</code>" field filled in.  Then, when
 you add the 25 HTTP Request controllers, leave the "<code>Server Name or IP</code>" field empty.  The controllers will inherit
 this field value from the HTTP Request Defaults element.</p>
 <note>
 All port values are treated equally; a sampler that does not specify a port will use the HTTP Request Defaults port, if one is provided.
 </note>
 </description>
 <figure width="881" height="256" image="http-config/http-request-defaults-advanced-tab.png">HTTP Request Advanced config fields</figure>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="Server" required="No">Domain name or IP address of the web server. e.g. <code>www.example.com</code>. [Do not include the <code>http://</code> prefix.</property>
         <property name="Port" required="No">Port the web server is listening to.</property>
         <property name="Connect Timeout" required="No">Connection Timeout. Number of milliseconds to wait for a connection to open.</property>
         <property name="Response Timeout" required="No">Response Timeout. Number of milliseconds to wait for a response.</property>
         <property name="Implementation" required="No"><code>Java</code>, <code>HttpClient3.1 (DEPRECATED SINCE 3.0)</code>, <code>HttpClient4</code>. 
         If not specified the default depends on the value of the JMeter property
         <code>jmeter.httpsampler</code>, failing that, the <code>Java</code> implementation is used.</property>
         <property name="Protocol" required="No"><code>HTTP</code> or <code>HTTPS</code>.</property>
         <property name="Content encoding" required="No">The encoding to be used for the request.</property>
         <property name="Path" required="No">The path to resource (for example, <code>/servlets/myServlet</code>). If the
         resource requires query string parameters, add them below in the "<code>Send Parameters With the Request</code>" section.
         Note that the path is the default for the full path, not a prefix to be applied to paths
         specified on the HTTP Request screens.
         </property>
         <property name="Send Parameters With the Request" required="No">The query string will
         be generated from the list of parameters you provide.  Each parameter has a <i>name</i> and
         <i>value</i>.  The query string will be generated in the correct fashion, depending on
         the choice of "<code>Method</code>" you made (i.e. if you chose <code>GET</code>, the query string will be
         appended to the URL, if <code>POST</code>, then it will be sent separately).  Also, if you are
         sending a file using a multipart form, the query string will be created using the
         multipart form specifications.</property>
         <property name="Server (proxy)" required="No">Hostname or IP address of a proxy server to perform request. [Do not include the <code>http://</code> prefix.]</property>
         <property name="Port" required="No, unless proxy hostname is specified">Port the proxy server is listening to.</property>
         <property name="Username" required="No">(Optional) username for proxy server.</property>
         <property name="Password" required="No">(Optional) password for proxy server. (N.B. this is stored unencrypted in the test plan)</property>
         <property name="Retrieve All Embedded Resources from HTML Files" required="No">Tell JMeter to parse the HTML file
 and send HTTP/HTTPS requests for all images, Java applets, JavaScript files, CSSs, etc. referenced in the file.
         </property>
         <property name="Use concurrent pool" required="No">Use a pool of concurrent connections to get embedded resources.</property>
         <property name="Size" required="No">Pool size for concurrent connections used to get embedded resources.</property>
         <property name="Embedded URLs must match:" required="No">
         If present, this must be a regular expression that is used to match against any embedded URLs found.
         So if you only want to download embedded resources from <code>http://example.com/</code>, use the expression:
         <code>http://example\.com/.*</code>
         </property>
 </properties>
 <note>
 Note: radio buttons only have two states - on or off.
 This makes it impossible to override settings consistently
 - does off mean off, or does it mean use the current default?
 JMeter uses the latter (otherwise defaults would not work at all).
 So if the button is off, then a later element can set it on,
 but if the button is on, a later element cannot set it off.
 </note>
 </component>
 
 <component name="HTTP Header Manager" index="&sect-num;.4.8"  width="767" height="239" screenshot="http-config/http-header-manager.png">
 <description>
 <p>The Header Manager lets you add or override HTTP request headers.</p>
 <p>
 <b>JMeter now supports multiple Header Managers</b>. The header entries are merged to form the list for the sampler.
 If an entry to be merged matches an existing header name, it replaces the previous entry,
 unless the entry value is empty, in which case any existing entry is removed.
 This allows one to set up a default set of headers, and apply adjustments to particular samplers. 
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Name (Header)" required="No (You should have at least one, however)">Name of the request header.
         Two common request headers you may want to experiment with
 are "<code>User-Agent</code>" and "<code>Referer</code>".</property>
   <property name="Value" required="No (You should have at least one, however)">Request header value.</property>
   <property name="Add Button" required="N/A">Add an entry to the header table.</property>
   <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
   <property name="Load Button" required="N/A">Load a previously saved header table and add the entries to the existing
 header table entries.</property>
   <property name="Save As Button" required="N/A">Save the current header table to a file.</property>
 </properties>
 
 <example title="Header Manager example" anchor="header_manager_example">
 
 <p><a href="../demos/HeaderManagerTestPlan.jmx">Download</a> this example.  In this example, we created a Test Plan
 that tells JMeter to override the default "<code>User-Agent</code>" request header and use a particular Internet Explorer agent string
 instead. (see figures 12 and 13).</p>
 
 <figure width="247" height="121" image="http-config/header-manager-example1a.png">Figure 12 - Test Plan</figure>
 <figure image="http-config/header-manager-example1b.png">Figure 13 - Header Manager Control Panel</figure>
 </example>
 
 </component>
 
 <component name="Java Request Defaults" index="&sect-num;.4.9"  width="685" height="373" screenshot="java_defaults.png">
 <description><p>The Java Request Defaults component lets you set default values for Java testing.  See the <complink name="Java Request" />.</p>
 </description>
 
 </component>
 
 <component name="JDBC Connection Configuration" index="&sect-num;.4.10" 
                  width="474" height="458" screenshot="jdbc-config/jdbc-conn-config.png">
     <description>Creates a database connection (used by <complink name="JDBC Request"/>Sampler)
      from the supplied JDBC Connection settings. The connection may be optionally pooled between threads.
      Otherwise each thread gets its own connection.
      The connection configuration name is used by the JDBC Sampler to select the appropriate
      connection.
      The used pool is DBCP, see <a href="https://commons.apache.org/proper/commons-dbcp/configuration.html" >BasicDataSource Configuration Parameters</a>
     </description>
     <properties>
         <property name="Name" required="No">Descriptive name for the connection configuration that is shown in the tree.</property>
         <property name="Variable Name" required="Yes">The name of the variable the connection is tied to.  
         Multiple connections can be used, each tied to a different variable, allowing JDBC Samplers
         to select the appropriate connection.
         <note>Each name must be different. If there are two configuration elements using the same name,
         only one will be saved. JMeter logs a message if a duplicate name is detected.</note>
         </property>
         <property name="Max Number of Connections" required="Yes">
         Maximum number of connections allowed in the pool.
         In most cases, <b>set this to zero (0)</b>.
         This means that each thread will get its own pool with a single connection in it, i.e.
         the connections are not shared between threads.
         <br />
         If you really want to use shared pooling (why?), then set the max count to the same as the number of threads
         to ensure threads don't wait on each other.
         </property>
         <property name="Max Wait (ms)" required="Yes">Pool throws an error if the timeout period is exceeded in the 
         process of trying to retrieve a connection, see <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getMaxWaitMillis--" >BasicDataSource.html#getMaxWaitMillis</a></property>
         <property name="Time Between Eviction Runs (ms)" required="Yes">The number of milliseconds to sleep between runs of the idle object evictor thread. When non-positive, no idle object evictor thread will be run. (Defaults to "<code>60000</code>", 1 minute).
         See <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getTimeBetweenEvictionRunsMillis--" >BasicDataSource.html#getTimeBetweenEvictionRunsMillis</a></property>
         <property name="Auto Commit" required="Yes">Turn auto commit on or off for the connections.</property>
         <property name="Test While Idle" required="Yes">Test idle connections of the pool, see <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getTestWhileIdle--">BasicDataSource.html#getTestWhileIdle</a>. 
         Validation Query will be used to test it.</property>
         <property name="Soft Min Evictable Idle Time(ms)" required="Yes">Minimum amount of time a connection may sit idle in the pool before it is eligible for eviction by the idle object evictor, with the extra condition that at least <code>minIdle</code> connections remain in the pool.
         See <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getSoftMinEvictableIdleTimeMillis--">BasicDataSource.html#getSoftMinEvictableIdleTimeMillis</a>.
         Defaults to 5000 (5 seconds)
         </property>
         <property name="Validation Query" required="No">A simple query used to determine if the database is still responding.
         This defaults to the '<code>isValid()</code>' method of the jdbc driver, which is suitable for many databases.
         However some may require a different query; for example Oracle something like '<code>SELECT 1 FROM DUAL</code>' could be used.
         Note this validation query is used on pool creation to validate it even if "<code>Test While Idle</code>" suggests query would only be used on idle connections.
         This is DBCP behaviour.
         </property>
         <property name="Database URL" required="Yes">JDBC Connection string for the database.</property>
         <property name="JDBC Driver class" required="Yes">Fully qualified name of driver class. (Must be in
         JMeter's classpath - easiest to copy <code>.jar</code> file into JMeter's <code>/lib</code> directory).</property>
         <property name="Username" required="No">Name of user to connect as.</property>
         <property name="Password" required="No">Password to connect with. (N.B. this is stored unencrypted in the test plan)</property>
     </properties>
 <p>Different databases and JDBC drivers require different JDBC settings. 
 The Database URL and JDBC Driver class are defined by the provider of the JDBC implementation.</p>
 <p>Some possible settings are shown below. Please check the exact details in the JDBC driver documentation.</p>
 
 <p>
 If JMeter reports <code>No suitable driver</code>, then this could mean either:
 </p>
 <ul>
 <li>The driver class was not found. In this case, there will be a log message such as <code>DataSourceElement: Could not load driver: {classname} java.lang.ClassNotFoundException: {classname}</code></li>
 <li>The driver class was found, but the class does not support the connection string. This could be because of a syntax error in the connection string, or because the wrong classname was used.</li>
 </ul>
 If the database server is not running or is not accessible, then JMeter will report a <code>java.net.ConnectException</code>.
 <table>
 <tr><th>Database</th><th>Driver class</th><th>Database URL</th></tr>
 <tr><td>MySQL</td><td><code>com.mysql.jdbc.Driver</code></td><td><code>jdbc:mysql://host[:port]/dbname</code></td></tr>
 <tr><td>PostgreSQL</td><td><code>org.postgresql.Driver</code></td><td><code>jdbc:postgresql:{dbname}</code></td></tr>
 <tr><td>Oracle</td><td><code>oracle.jdbc.OracleDriver</code></td><td><code>jdbc:oracle:thin:@//host:port/service</code>
 OR<br/><code>jdbc:oracle:thin:@(description=(address=(host={mc-name})(protocol=tcp)(port={port-no}))(connect_data=(sid={sid})))</code></td></tr>
 <tr><td>Ingres (2006)</td><td><code>ingres.jdbc.IngresDriver</code></td><td><code>jdbc:ingres://host:port/db[;attr=value]</code></td></tr>
 <tr><td>SQL Server (MS JDBC driver)</td><td><code>com.microsoft.sqlserver.jdbc.SQLServerDriver</code></td><td><code>jdbc:sqlserver://host:port;DatabaseName=dbname</code></td></tr>
 <tr><td>Apache Derby</td><td><code>org.apache.derby.jdbc.ClientDriver</code></td><td><code>jdbc:derby://server[:port]/databaseName[;URLAttributes=value[;&hellip;]]</code></td></tr>
 </table>
 <note>The above may not be correct - please check the relevant JDBC driver documentation.</note>
 </component>
 
 
 <component name="Keystore Configuration" index="&sect-num;.4.11"  width="441" height="189" screenshot="keystore_config.png">
 <description><p>The Keystore Config Element lets you configure how Keystore will be loaded and which keys it will use.
 This component is typically used in HTTPS scenarios where you don't want to take into account keystore initialization into account in response time.</p>
 <p>To use this element, you need to setup first a Java Key Store with the client certificates you want to test, to do that:
 </p>
 <ol>
 <li>Create your certificates either with Java <code>keytool</code> utility or through your PKI</li>
 <li>If created by PKI, import your keys in Java Key Store by converting them to a format acceptable by JKS</li>
 <li>Then reference the keystore file through the two JVM properties (or add them in <code>system.properties</code>):
     <ul>
         <li><code>-Djavax.net.ssl.keyStore=path_to_keystore</code></li>
         <li><code>-Djavax.net.ssl.keyStorePassword=password_of_keystore</code></li>
     </ul>
 </li>
 </ol>
 </description>
 
 <properties>
   <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Preload" required="Yes">Wether or not to preload Keystore. Setting it to <code>true</code> is usually the best option.</property>
   <property name="Variable name holding certificate alias" required="False">Variable name that will contain the alias to use for authentication by client certificate. Variable value will be filled from CSV Data Set for example. In the screenshot, "<code>certificat_ssl</code>" will also be a variable in CSV Data Set.</property>
   <property name="Alias Start Index" required="Yes">The index of the first key to use in Keystore, 0-based.</property>
   <property name="Alias End Index" required="Yes">The index of the last key to use in Keystore, 0-based. When using "<code>Variable name holding certificate alias</code>" ensure it is large enough so that all keys are loaded at startup.</property>
 </properties>
 <note>
 To make JMeter use more than one certificate you need to ensure that:
 <ul>
 <li><code>https.use.cached.ssl.context=false</code> is set in <code>jmeter.properties</code> or <code>user.properties</code></li>
 <li>You use either HTTPClient 4 (ADVISED) or HTTPClient 3.1 (DEPRECATED SINCE 3.0) implementations for HTTP Request</li>
 </ul>
 </note>
 </component>
 
 <component name="Login Config Element" index="&sect-num;.4.12"  width="459" height="126" screenshot="login-config.png">
 <description><p>The Login Config Element lets you add or override username and password settings in samplers that use username and password as part of their setup.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Username" required="No">The default username to use.</property>
   <property name="Password" required="No">The default password to use. (N.B. this is stored unencrypted in the test plan)</property>
 </properties>
 
 </component>
 
 <component name="LDAP Request Defaults" index="&sect-num;.4.13"  width="689" height="232" screenshot="ldap_defaults.png">
 <description><p>The LDAP Request Defaults component lets you set default values for LDAP testing.  See the <complink name="LDAP Request"/>.</p>
 </description>
 
 </component>
 
 <component name="LDAP Extended Request Defaults" index="&sect-num;.4.14"  width="686" height="184" screenshot="ldapext_defaults.png">
 <description><p>The LDAP Extended Request Defaults component lets you set default values for extended LDAP testing.  See the <complink name="LDAP Extended Request"/>.</p>
 </description>
 
 </component>
 
 <component name="TCP Sampler Config" index="&sect-num;.4.15"  width="826" height="450" screenshot="tcpsamplerconfig.png">
 <description>
         <p>
     The TCP Sampler Config provides default data for the TCP Sampler
     </p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="TCPClient classname" required="No">Name of the TCPClient class. Defaults to the property <code>tcp.handler</code>, failing that <code>TCPClientImpl</code>.</property>
   <property name="ServerName or IP" required="">Name or IP of TCP server</property>
   <property name="Port Number" required="">Port to be used</property>
   <property name="Re-use connection" required="Yes">If selected, the connection is kept open. Otherwise it is closed when the data has been read.</property>
   <property name="Close connection" required="Yes">If selected, the connection will be closed after running the sampler.</property>  
   <property name="SO_LINGER" required="No">Enable/disable <code>SO_LINGER</code> with the specified linger time in seconds when a socket is created. If you set "<code>SO_LINGER</code>" value as <code>0</code>, you may prevent large numbers of sockets sitting around with a <code>TIME_WAIT</code> status.</property>
   <property name="End of line(EOL) byte value" required="No">Byte value for end of line, set this to a value outside the range <code>-128</code> to <code>+127</code> to skip eol checking. You may set this in <code>jmeter.properties</code> file as well with the <code>tcp.eolByte</code> property. If you set this in TCP Sampler Config and in <code>jmeter.properties</code> file at the same time, the setting value in the TCP Sampler Config will be used.</property>
   <property name="Connect Timeout" required="No">Connect Timeout (milliseconds, 0 disables).</property>
   <property name="Response Timeout" required="No">Response Timeout (milliseconds, 0 disables).</property>
   <property name="Set Nodelay" required="">Should the nodelay property be set?</property>
   <property name="Text to Send" required="">Text to be sent</property>
 </properties>
 </component>
 
 <component name="User Defined Variables" index="&sect-num;.4.16"  width="741" height="266" screenshot="user_defined_variables.png">
 <description><p>The User Defined Variables element lets you define an <b>initial set of variables</b>, just as in the <complink name="Test Plan" />.
 <note>
 Note that all the UDV elements in a test plan - no matter where they are - are processed at the start.
 </note>
 So you cannot reference variables which are defined as part of a test run, e.g. in a Post-Processor.
 </p>
 <p>
 <b>
 UDVs should not be used with functions that generate different results each time they are called.
 Only the result of the first function call will be saved in the variable. 
 </b>
 However, UDVs can be used with functions such as <code>__P()</code>, for example:
 </p>
 <source>
 HOST      ${__P(host,localhost)} 
 </source>
 <p>
 which would define the variable "<code>HOST</code>" to have the value of the JMeter property "<code>host</code>", defaulting to "<code>localhost</code>" if not defined.
 </p>
 <p>
 For defining variables during a test run, see <complink name="User Parameters"/>.
 UDVs are processed in the order they appear in the Plan, from top to bottom.
 </p>
 <p>
 For simplicity, it is suggested that UDVs are placed only at the start of a Thread Group
 (or perhaps under the Test Plan itself).
 </p>
 <p>
 Once the Test Plan and all UDVs have been processed, the resulting set of variables is
 copied to each thread to provide the initial set of variables.
 </p>
 <p>
 If a runtime element such as a User Parameters Pre-Processor or Regular Expression Extractor defines a variable
 with the same name as one of the UDV variables, then this will replace the initial value, and all other test
 elements in the thread will see the updated value.
 </p>
 </description>
 <note>
 If you have more than one Thread Group, make sure you use different names for different values, as UDVs are shared between Thread Groups.
 Also, the variables are not available for use until after the element has been processed, 
 so you cannot reference variables that are defined in the same element.
 You can reference variables defined in earlier UDVs or on the Test Plan. 
 </note>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="User Defined Variables" required="">Variable name/value pairs. The string under the "<code>Name</code>"
       column is what you'll need to place inside the brackets in <code>${&hellip;}</code> constructs to use the variables later on. The
       whole <code>${&hellip;}</code> will then be replaced by the string in the "<code>Value</code>" column.</property>
 </properties>
 </component>
 
 <component name="Random Variable" index="&sect-num;.4.17"  width="495" height="286" screenshot="random_variable.png">
 <description>
 <p>
 The Random Variable Config Element is used to generate random numeric strings and store them in variable for use later.
 It's simpler than using <complink name="User Defined Variables"/> together with the <code>__Random()</code> function.
 </p>
 <p>
 The output variable is constructed by using the random number generator,
 and then the resulting number is formatted using the format string.
 The number is calculated using the formula <code>minimum+Random.nextInt(maximum-minimum+1)</code>.
 <code>Random.nextInt()</code> requires a positive integer.
 This means that <code>maximum-minimum</code> - i.e. the range - must be less than <code>2147483647</code>,
 however the <code>minimum</code> and <code>maximum</code> values can be any <code>long</code> values so long as the range is OK.
 </p>
 </description>
 
 <properties>
   <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree.</property>
   <property name="Variable Name" required="Yes">The name of the variable in which to store the random string.</property>
   <property name="Format String" required="No">The <code>java.text.DecimalFormat</code> format string to be used. 
   For example "<code>000</code>" which will generate numbers with at least 3 digits, 
   or "<code>USER_000</code>" which will generate output of the form <code>USER_nnn</code>. 
   If not specified, the default is to generate the number using <code>Long.toString()</code></property>
   <property name="Minimum Value" required="Yes">The minimum value (<code>long</code>) of the generated random number.</property>
   <property name="Maximum Value" required="Yes">The maximum value (<code>long</code>) of the generated random number.</property>
   <property name="Random Seed" required="No">The seed for the random number generator. Default is the current time in milliseconds. 
   If you use the same seed value with Per Thread set to <code>true</code>, you will get the same value for each Thread as per 
   <a href="http://docs.oracle.com/javase/7/docs/api/java/util/Random.html" >Random</a> class.
   </property>
   <property name="Per Thread(User)?" required="Yes">If <code>False</code>, the generator is shared between all threads in the thread group.
   If <code>True</code>, then each thread has its own random generator.</property>
 </properties>
 
 </component>
 
 <component name="Counter" index="&sect-num;.4.18"  width="404" height="262" screenshot="counter.png">
 <description><p>Allows the user to create a counter that can be referenced anywhere
 in the Thread Group.  The counter config lets the user configure a starting point, a maximum,
 and the increment.  The counter will loop from the start to the max, and then start over
 with the start, continuing on like that until the test is ended.  </p>
 <p>The counter uses a long to store the value, so the range is from <code>-2^63</code> to <code>2^63-1</code>.</p>
 </description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Start" required="Yes">The starting number for the counter.  The counter will equal this
         number during the first iteration.</property>
         <property name="Increment" required="Yes">How much to increment the counter by after each
         iteration.</property>
         <property name="Maximum" required="No">If the counter exceeds the maximum, then it is reset to the <code>Start</code> value.
         Default is <code>Long.MAX_VALUE</code>
         </property>
         <property name="Format" required="No">Optional format, e.g. <code>000</code> will format as <code>001</code>, <code>002</code>, etc. 
         This is passed to <code>DecimalFormat</code>, so any valid formats can be used.
         If there is a problem interpreting the format, then it is ignored.
     [The default format is generated using <code>Long.toString()</code>]
         </property>
         <property name="Reference Name" required="Yes">This controls how you refer to this value in other elements.  Syntax is
         as in <a href="functions.html">user-defined values</a>: <code>$(reference_name}</code>.</property>
         <property name="Track Counter Independently for each User" required="No">In other words, is this a global counter, or does each user get their
         own counter?  If unchecked, the counter is global (i.e., user #1 will get value "<code>1</code>", and user #2 will get value "<code>2</code>" on
         the first iteration).  If checked, each user has an independent counter.</property>
         <property name="Reset counter on each Thread Group Iteration" required="No">This option is only available when counter is tracked per User, if checked, 
         counter will be reset to <code>Start</code> value on each Thread Group iteration. This can be useful when Counter is inside a Loop Controller.</property>
 </properties>
 </component>
 
 <component name="Simple Config Element" index="&sect-num;.4.19"  width="627" height="282" screenshot="simple_config_element.png">
 <description><p>The Simple Config Element lets you add or override arbitrary values in samplers.  You can choose the name of the value
 and the value itself.  Although some adventurous users might find a use for this element, it's here primarily for developers as a basic
 GUI that they can use while developing new JMeter components.</p>
 </description>
 
 <properties>
         <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree. </property>
   <property name="Parameter Name" required="Yes">The name of each parameter.  These values are internal to JMeter's workings and
   are not generally documented.  Only those familiar with the code will know these values.</property>
   <property name="Parameter Value" required="Yes">The value to apply to that parameter.</property>
 </properties>
 
 </component>
 
 
 <component name="MongoDB Source Config (DEPRECATED)" index="&sect-num;.4.20" 
                  width="1233" height="618" screenshot="mongodb-source-config.png">
     <description>Creates a MongoDB connection (used by <complink name="MongoDB Script"/>Sampler)
      from the supplied Connection settings. Each thread gets its own connection.
      The connection configuration name is used by the JDBC Sampler to select the appropriate
      connection.
      <p>
      You can then access <code>com.mongodb.DB</code> object in Beanshell or JSR223 Test Elements through the element <a href="../api/org/apache/jmeter/protocol/mongodb/config/MongoDBHolder.html">MongoDBHolder</a> 
      using this code</p>
      
     <source>
 import com.mongodb.DB;
 import org.apache.jmeter.protocol.mongodb.config.MongoDBHolder;
 DB db = MongoDBHolder.getDBFromSource("value of property MongoDB Source",
             "value of property Database Name");
 &hellip;
     </source>
     </description>
     <properties>
         <property name="Name" required="No">Descriptive name for the connection configuration that is shown in the tree.</property>
         <property name="Server Address List" required="Yes">Mongo DB Servers</property>
         <property name="MongoDB Source" required="Yes">The name of the variable the connection is tied to.  
         <note>Each name must be different. If there are two configuration elements using the same name, only one will be saved.</note>
         </property>
         
         <property name="Keep Trying" required="No">
             If <code>true</code>, the driver will keep trying to connect to the same server in case that the socket cannot be established.<br/>
             There is maximum amount of time to keep retrying, which is 15s by default.<br/>This can be useful to avoid some exceptions being thrown when a server is down temporarily by blocking the operations.
             <br/>It can also be useful to smooth the transition to a new master (so that a new master is elected within the retry time).<br/>
             <note>Note that when using this flag
               <ul>
                 <li>for a replica set, the driver will try to connect to the old master for that time, instead of failing over to the new one right away </li>
                 <li>this does not prevent exception from being thrown in read/write operations on the socket, which must be handled by application.</li>
               </ul>
               Even if this flag is false, the driver already has mechanisms to automatically recreate broken connections and retry the read operations.
             </note>
             Default is <code>false</code>.
         </property>
         <property name="Maximum connections per host" required="No"></property>
         <property name="Connection timeout" required="No">
             The connection timeout in milliseconds.<br/>It is used solely when establishing a new connection <code>Socket.connect(java.net.SocketAddress, int)</code><br/>Default is <code>0</code> and means no timeout.
         </property>
         <property name="Maximum retry time" required="No">
             The maximum amount of time in milliseconds to spend retrying to open connection to the same server.<br/>Default is <code>0</code>, which means to use the default 15s if <code>autoConnectRetry</code> is on.
         </property>
         <property name="Maximum wait time" required="No">
             The maximum wait time in milliseconds that a thread may wait for a connection to become available.<br/>Default is <code>120,000</code>.
         </property>
         <property name="Socket timeout" required="No">
             The socket timeout in milliseconds It is used for I/O socket read and write operations <code>Socket.setSoTimeout(int)</code><br/>Default is <code>0</code> and means no timeout.
         </property>
         <property name="Socket keep alive" required="No">
             This flag controls the socket keep alive feature that keeps a connection alive through firewalls <code>Socket.setKeepAlive(boolean)</code><br/>
             Default is <code>false</code>.
         </property>
         <property name="ThreadsAllowedToBlockForConnectionMultiplier" required="No">
         This multiplier, multiplied with the connectionsPerHost setting, gives the maximum number of threads that may be waiting for a connection to become available from the pool.<br/>
         All further threads will get an exception right away.<br/>
         For example if <code>connectionsPerHost</code> is <code>10</code> and <code>threadsAllowedToBlockForConnectionMultiplier</code> is <code>5</code>, then up to 50 threads can wait for a connection.<br/>
         Default is <code>5</code>.
         </property>
         <property name="Write Concern : Safe" required="No">
             If <code>true</code> the driver will use a <code>WriteConcern</code> of <code>WriteConcern.SAFE</code> for all operations.<br/>
             If <code>w</code>, <code>wtimeout</code>, <code>fsync</code> or <code>j</code> are specified, this setting is ignored.<br/>
             Default is <code>false</code>.
         </property>
         <property name="Write Concern : Fsync" required="No">
             The <code>fsync</code> value of the global <code>WriteConcern</code>.<br/>
             Default is <code>false</code>.
         </property>
         <property name="Write Concern : Wait for Journal" required="No">
             The <code>j</code> value of the global <code>WriteConcern</code>.<br/>
             Default is <code>false</code>.
         </property>
         <property name="Write Concern : Wait for servers" required="No">
             The <code>w</code> value of the global <code>WriteConcern</code>.<br/>Default is <code>0</code>.
         </property>
         <property name="Write Concern : Wait timeout" required="No">
             The <code>wtimeout</code> value of the global <code>WriteConcern</code>.<br/>Default is <code>0</code>.
         </property>
         <property name="Write Concern : Continue on error" required="No">
             If batch inserts should continue after the first error
         </property>
     </properties>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.5 Assertions" anchor="assertions">
 <description>
     <p>
     Assertions are used to perform additional checks on samplers, and are processed after <b>every sampler</b>
     in the same scope.
     To ensure that an Assertion is applied only to a particular sampler, add it as a child of the sampler.
     </p>
     <note>
     Note: Unless documented otherwise, Assertions are not applied to sub-samples (child samples) -
     only to the parent sample.
     In the case of BSF and BeanShell Assertions, the script can retrieve sub-samples using the method
     <code>prev.getSubResults()</code> which returns an array of SampleResults.
     The array will be empty if there are none.
     </note>
     <p>
     Assertions can be applied to either the main sample, the sub-samples or both. 
     The default is to apply the assertion to the main sample only.
     If the Assertion supports this option, then there will be an entry on the GUI which looks like the following:
     </p>
     <figure width="658" height="54" image="assertion/assertionscope.png">Assertion Scope</figure>
     or the following
     <figure width="841" height="55" image="assertion/assertionscopevar.png">Assertion Scope</figure>
     <p>
     If a sub-sampler fails and the main sample is successful,
     then the main sample will be set to failed status and an Assertion Result will be added.
     If the JMeter variable option is used, it is assumed to relate to the main sample, and
     any failure will be applied to the main sample only.
     </p>
     <note>
     The variable <code>JMeterThread.last_sample_ok</code> is updated to
     "<code>true</code>" or "<code>false</code>" after all assertions for a sampler have been run.
      </note>
 </description>
 <component name="Response Assertion" index="&sect-num;.5.1" anchor="basic_assertion"  width="921" height="423" screenshot="assertion/assertion.png">
 
 <description><p>The response assertion control panel lets you add pattern strings to be compared against various
     fields of the response.
     The pattern strings are:
     </p>
     <ul>
     <li><code>Contains</code>, <code>Matches</code>: Perl5-style regular expressions</li>
     <li><code>Equals</code>, <code>Substring</code>: plain text, case-sensitive</li>
     </ul>
     <p>
     A summary of the pattern matching characters can be found at <a href="http://jakarta.apache.org/oro/api/org/apache/oro/text/regex/package-summary.html">ORO Perl5 regular expressions.</a>
     </p>
     <p>You can also choose whether the strings will be expected
 to <b>match</b> the entire response, or if the response is only expected to <b>contain</b> the
 pattern. You can attach multiple assertions to any controller for additional flexibility.</p>
 <p>Note that the pattern string should not include the enclosing delimiters, 
     i.e. use <code>Price: \d+</code> not <code>/Price: \d+/</code>.
     </p>
     <p>
     By default, the pattern is in multi-line mode, which means that the "<code>.</code>" meta-character does not match newline.
     In multi-line mode, "<code>^</code>" and "<code>$</code>" match the start or end of any line anywhere within the string 
     - not just the start and end of the entire string. Note that <code>\s</code> does match new-line.
     Case is also significant. To override these settings, one can use the <i>extended regular expression</i> syntax.
     For example:
 </p>
 <dl>
 <dt><code>(?i)</code></dt><dd>ignore case</dd>
 <dt><code>(?s)</code></dt><dd>treat target as single line, i.e. "<code>.</code>" matches new-line</dd>
 <dt><code>(?is)</code></dt><dd>both the above</dd>
 </dl>
 These can be used anywhere within the expression and remain in effect until overridden. E.g.
 <dl>
 <dt><code>(?i)apple(?-i) Pie</code></dt><dd>matches "<code>ApPLe Pie</code>", but not "<code>ApPLe pIe</code>"</dd>
 <dt><code>(?s)Apple.+?Pie</code></dt><dd>matches <code>Apple</code> followed by <code>Pie</code>, which may be on a subsequent line.</dd>
 <dt><code>Apple(?s).+?Pie</code></dt><dd>same as above, but it's probably clearer to use the <code>(?s)</code> at the start.</dd>
 </dl>
 
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - assertion only applies to the main sample</li>
         <li><code>Sub-samples only</code> - assertion only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - assertion applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         </property>
         <property name="Response Field to Test" required="Yes">Instructs JMeter which field of the Response to test.
         <ul>
         <li><code>Text Response</code> - the response text from the server, i.e. the body, excluding any HTTP headers.</li>
         <li><code>Document (text)</code> - the extract text from various type of documents via Apache Tika (see <complink name="View Results Tree"/> Document view section).</li>
         <li><code>URL sampled</code></li>
         <li><code>Response Code</code> - e.g. <code>200</code></li>
         <li><code>Response Message</code> - e.g. <code>OK</code></li>
         <li><code>Response Headers</code>, including Set-Cookie headers (if any)</li>
         </ul>
                 </property>
         <property name="Ignore status" required="Yes">Instructs JMeter to set the status to success initially. 
                 <p>
                 The overall success of the sample is determined by combining the result of the
                 assertion with the existing Response status.
                 When the <code>Ignore Status</code> checkbox is selected, the Response status is forced
                 to successful before evaluating the Assertion.
                 </p>
                 HTTP Responses with statuses in the <code>4xx</code> and <code>5xx</code> ranges are normally
                 regarded as unsuccessful. 
                 The "<code>Ignore status</code>" checkbox can be used to set the status successful before performing further checks.
                 Note that this will have the effect of clearing any previous assertion failures,
                 so make sure that this is only set on the first assertion.
         </property>
         <property name="Pattern Matching Rules" required="Yes">Indicates how the text being tested
         is checked against the pattern.
         <ul>
         <li><code>Contains</code> - true if the text contains the regular expression pattern</li>
         <li><code>Matches</code> - true if the whole text matches the regular expression pattern</li>
         <li><code>Equals</code> - true if the whole text equals the pattern string (case-sensitive)</li>
         <li><code>Substring</code> - true if the text contains the pattern string (case-sensitive)</li>
         </ul>
         <code>Equals</code> and <code>Substring</code> patterns are plain strings, not regular expressions.
         <code>NOT</code> may also be selected to invert the result of the check.</property>
         <property name="Patterns to Test" required="Yes">A list of patterns to
         be tested.  
         Each pattern is tested separately. 
         If a pattern fails, then further patterns are not checked.
         There is no difference between setting up
         one Assertion with multiple patterns and setting up multiple Assertions with one
         pattern each (assuming the other options are the same).
         <note>However, when the <code>Ignore Status</code> checkbox is selected, this has the effect of cancelling any
         previous assertion failures - so make sure that the <code>Ignore Status</code> checkbox is only used on
         the first Assertion.</note>
         </property>
 </properties>
 <p>
     The pattern is a Perl5-style regular expression, but without the enclosing brackets.
 </p>
 <example title="Assertion Examples" anchor="assertion_examples">
 <center>
 <figure image="assertion/example1a.png" width="266" height="117">Figure 14 - Test Plan</figure>
 <figure image="assertion/example1b.png" width="920" height="451">Figure 15 - Assertion Control Panel with Pattern</figure>
 <figure image="assertion/example1c-pass.png" width="801" height="230">Figure 16 - Assertion Listener Results (Pass)</figure>
 <figure image="assertion/example1c-fail.png" width="800" height="233">Figure 17 - Assertion Listener Results (Fail)</figure>
 </center>
 </example>
 
 
 </component>
 
 <component name="Duration Assertion" index="&sect-num;.5.2"  width="606" height="187" screenshot="duration_assertion.png">
 <description><p>The Duration Assertion tests that each response was received within a given amount
 of time.  Any response that takes longer than the given number of milliseconds (specified by the
 user) is marked as a failed response.</p></description>
 
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Duration in Milliseconds" required="Yes">The maximum number of milliseconds
         each response is allowed before being marked as failed.</property>
 </properties>
 </component>
 
 <component name="Size Assertion" index="&sect-num;.5.3"  width="732" height="358" screenshot="size_assertion.png">
 <description><p>The Size Assertion tests that each response contains the right number of bytes in it.  You can specify that
 the size be equal to, greater than, less than, or not equal to a given number of bytes.</p>
 <note>An empty response is treated as being 0 bytes rather than reported as an error.</note>
 </description>
 
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - assertion only applies to the main sample</li>
         <li><code>Sub-samples only</code> - assertion only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - assertion applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         </property>
         <property name="Size in bytes" required="Yes">The number of bytes to use in testing the size of the response (or value of the JMeter variable).</property>
         <property name="Type of Comparison" required="Yes">Whether to test that the response is equal to, greater than, less than,
         or not equal to, the number of bytes specified.</property>
 
 </properties>
 </component>
 
 <component name="XML Assertion" index="&sect-num;.5.4"  width="470" height="85" screenshot="xml_assertion.png">
 <description><p>The XML Assertion tests that the response data consists of a formally correct XML document.  It does not
 validate the XML based on a DTD or schema or do any further validation.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 
 </properties>
 </component>
 
 <component name="BeanShell Assertion" index="&sect-num;.5.5"  width="849" height="633" screenshot="beanshell_assertion.png">
 <description><p>The BeanShell Assertion allows the user to perform assertion checking using a BeanShell script.
 </p>
 <p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p><p>
 Note that a different Interpreter is used for each independent occurrence of the assertion
 in each thread in a test script, but the same Interpreter is used for subsequent invocations.
 This means that variables persist across calls to the assertion.
 </p>
 <p>
 All Assertions are called from the same thread as the sampler.
 </p>
 <p>
 If the property "<code>beanshell.assertion.init</code>" is defined, it is passed to the Interpreter
 as the name of a sourced file. This can be used to define common methods and variables.
 There is a sample init file in the <code>bin</code> directory: <code>BeanShellAssertion.bshrc</code>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> methods.
 These should be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 </description>
 
 <properties>
     <property name="Name" required="">Descriptive name for this element that is shown in the tree.
     The name is stored in the script variable <code>Label</code></property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>bsh.args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run. This overrides the script.
     The file name is stored in the script variable <code>FileName</code></property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. The return value is ignored.</property>
 </properties>
 <p>There's a <a href="../demos/BeanShellAssertion.bsh">sample script</a> you can try.</p>
 <p>
 Before invoking the script, some variables are set up in the BeanShell interpreter.
 These are strings unless otherwise noted:
 </p>
 <ul>
   <li><code>log</code> - the <a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a> Object. (e.g.) <code>log.warn("Message"[,Throwable])</code></li>
   <li><code>SampleResult</code> - the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a> Object; read-write</li>
   <li><code>Response</code> - the response Object; read-write</li>
   <li><code>Failure</code> - boolean; read-write; used to set the Assertion status</li>
   <li><code>FailureMessage</code> - String; read-write; used to set the Assertion message</li>
   <li><code>ResponseData</code> - the response body (byte [])</li>
   <li><code>ResponseCode</code> - e.g. <code>200</code></li>
   <li><code>ResponseMessage</code> - e.g. <code>OK</code></li>
   <li><code>ResponseHeaders</code> - contains the HTTP headers</li>
   <li><code>RequestHeaders</code> - contains the HTTP headers sent to the server</li>
   <li><code>SampleLabel</code></li>
   <li><code>SamplerData</code> - data that was sent to the server</li>
   <li><code>ctx</code> - <a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a></li>
   <li><code>vars</code> - <a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>  - e.g. 
     <source>vars.get("VAR1");
 vars.put("VAR2","value");
 vars.putObject("OBJ1",new Object());</source></li>
   <li><code>props</code> - JMeterProperties (class <code>java.util.Properties</code>) - e.g.
     <source>props.get("START.HMS");
 props.put("PROP1","1234");</source></li>
 </ul>
 <p>The following methods of the Response object may be useful:</p>
 <ul>
     <li><code>setStopThread(boolean)</code></li>
     <li><code>setStopTest(boolean)</code></li>
     <li><code>String getSampleLabel()</code></li>
     <li><code>setSampleLabel(String)</code></li>
 </ul>
 </component>
 
 <component name="MD5Hex Assertion" index="&sect-num;.5.6" width="398" height="130" screenshot="assertion/MD5HexAssertion.png">
 <description><p>The MD5Hex Assertion allows the user to check the MD5 hash of the response data.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="MD5 sum" required="Yes">32 hex digits representing the MD5 hash (case not significant)</property>
 
 </properties>
 </component>
 
 <component name="HTML Assertion" index="&sect-num;.5.7"  width="505" height="341" screenshot="assertion/HTMLAssertion.png">
 <description><p>The HTML Assertion allows the user to check the HTML syntax of the response data using JTidy.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="doctype" required="Yes"><code>omit</code>, <code>auto</code>, <code>strict</code> or <code>loose</code></property>
 <property name="Format" required="Yes"><code>HTML</code>, <code>XHTML</code> or <code>XML</code></property>
 <property name="Errors only" required="Yes">Only take note of errors?</property>
 <property name="Error threshold" required="Yes">Number of errors allowed before classing the response as failed</property>
 <property name="Warning threshold" required="Yes">Number of warnings allowed before classing the response as failed</property>
 <property name="Filename" required="No">Name of file to which report is written</property>
 
 </properties>
 </component>
 <component name="XPath Assertion" index="&sect-num;.5.8"  width="800" height="317" screenshot="xpath_assertion.png">
 <description><p>The XPath Assertion tests a document for well formedness, has the option
 of validating against a DTD, or putting the document through JTidy and testing for an
 XPath.  If that XPath exists, the Assertion is true.  Using "<code>/</code>" will match any well-formed
 document, and is the default XPath Expression. 
 The assertion also supports boolean expressions, such as "<code>count(//*error)=2</code>".
 See <a href="http://www.w3.org/TR/xpath">http://www.w3.org/TR/xpath</a> for more information
 on XPath.
 </p>
 Some sample expressions:
 <ul>
 <li><code>//title[text()='Text to match']</code> - matches <code>&lt;text&gt;Text to match&lt;/text&gt;</code> anywhere in the response</li>
 <li><code>/title[text()='Text to match']</code> - matches <code>&lt;text&gt;Text to match&lt;/text&gt;</code> at root level in the response</li>
 </ul>
 </description>
 
 <properties>
 <property name="Name"        required="No">Descriptive name for this element that is shown in the tree.</property>
 <property name="Use Tidy (tolerant parser)"    required="Yes">Use Tidy, i.e. be tolerant of XML/HTML errors</property>
 <property name="Quiet"    required="If Tidy is selected">Sets the Tidy Quiet flag</property>
 <property name="Report Errors"    required="If Tidy is selected">If a Tidy error occurs, then set the Assertion accordingly</property>
 <property name="Show warnings"    required="If Tidy is selected">Sets the Tidy showWarnings option</property>
 <property name="Use Namespaces"    required="If Tidy is not selected">Should namespaces be honoured?</property>
 <property name="Validate XML"    required="If Tidy is not selected">Check the document against its schema.</property>
 <property name="Ignore Whitespace"  required="If Tidy is not selected">Ignore Element Whitespace.</property>
 <property name="Fetch External DTDs"  required="If Tidy is not selected">If selected, external DTDs are fetched.</property>
 <property name="XPath Assertion"    required="Yes">XPath to match in the document.</property>
 <property name="True if nothing matches"    required="No">True if a XPath expression is not matched</property>
 </properties>
 <note>
 The non-tolerant parser can be quite slow, as it may need to download the DTD etc.
 </note>
 <note>
 As a work-round for namespace limitations of the Xalan XPath parser implementation on which JMeter is based,
 you can provide a Properties file which contains mappings for the namespace prefixes: 
 <source>
 prefix1=Full Namespace 1
 prefix2=Full Namespace 2
 &hellip;
 </source>
 
 You reference this file in <code>jmeter.properties</code> file using the property:
     <source>xpath.namespace.config</source>
 </note>
 </component>
 <component name="XML Schema Assertion" index="&sect-num;.5.9"  width="472" height="132" screenshot="assertion/XMLSchemaAssertion.png">
 <description><p>The XML Schema Assertion allows the user to validate a response against an XML Schema.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="File Name" required="Yes">Specify XML Schema File Name</property>
 </properties>
 </component>
 
 <component name="BSF Assertion" index="&sect-num;.5.10"  width="847" height="634" screenshot="bsf_assertion.png">
 <description>
 <p>
 The BSF Assertion allows BSF script code to be used to check the status of the previous sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The BSF language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>
 The script (or file) is processed using the <code>BSFEngine.exec()</code> method, which does not return a value.
 </p>
 <p>The following variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>Label</code> - the String Label</li>
 <li><code>Filename</code> - the script file name (if any)</li>
 <li><code>Parameters</code> - the parameters (as a String)</li>
 <li><code>args</code> - the parameters as a String array (split on whitespace)</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables:
 <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");
 </source></li>
 <li><code>props</code> - (JMeterProperties - class <code>java.util.Properties</code>) - e.g.
 <source>
 props.get("START.HMS");
 props.put("PROP1","1234");
 </source></li>
 <li><code>SampleResult</code>, <code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult (if any)</li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>) - gives access to the current sampler</li>
 <li><code>OUT</code> - <code>System.out</code> - e.g. <code>OUT.println("message")</code></li>
 <li><code>AssertionResult</code> - the assertion result</li>
 </ul>
 <p>
 The script can check various aspects of the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>.
 If an error is detected, the script should use <code>AssertionResult.setFailureMessage("message")</code> and <code>AssertionResult.setFailure(true)</code>.
 </p>
 <p>For further details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JSR223 Assertion" index="&sect-num;.5.11">
 <description>
 <p>
 The JSR223 Assertion allows JSR223 script code to be used to check the status of the previous sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The JSR223 language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script compilation caching" required="No">Unique String across Test Plan that JMeter will use to cache result of Script compilation if language used supports <code>Compilable</code> interface (Groovy is one of these, java, beanshell and javascript are not)
     <note>See note in JSR223 Sampler Java System property if you're using Groovy without checking this option</note>
     </property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>The following variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>Label</code> - the String Label</li>
 <li><code>Filename</code> - the script file name (if any)</li>
 <li><code>Parameters</code> - the parameters (as a String)</li>
 <li><code>args</code> - the parameters as a String array (split on whitespace)</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables:
 <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");
 </source></li>
 <li><code>props</code> - (JMeterProperties - class <code>java.util.Properties</code>) - e.g.
 <source>
 props.get("START.HMS");
 props.put("PROP1","1234");
 </source></li>
 <li><code>SampleResult</code>, <code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult (if any)</li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>) - gives access to the current sampler</li>
 <li><code>OUT</code> - <code>System.out</code> - e.g. <code>OUT.println("message")</code></li>
 <li><code>AssertionResult</code> - the assertion result</li>
 </ul>
 <p>
 The script can check various aspects of the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>.
 If an error is detected, the script should use <code>AssertionResult.setFailureMessage("message")</code> and <code>AssertionResult.setFailure(true)</code>.
 </p>
 <p>For further details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="Compare Assertion" index="&sect-num;.5.12"  width="580" height="302" screenshot="assertion/compare.png">
 <description>
 <note>
 Compare Assertion <b>must not be used</b> during load test as it consumes a lot of resources (memory and CPU). Use it only for either functional testing or 
 during Test Plan debugging and Validation.
 </note>
 
 The Compare Assertion can be used to compare sample results within its scope.
 Either the contents or the elapsed time can be compared, and the contents can be filtered before comparison.
 The assertion comparisons can be seen in the <complink name="Comparison Assertion Visualizer"/>.
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Compare Content" required="Yes">Whether or not to compare the content (response data)</property>
     <property name="Compare Time" required="Yes">If the value is &ge;0, then check if the response time difference is no greater than the value.
     I.e. if the value is <code>0</code>, then the response times must be exactly equal.</property>
     <property name="Comparison Filters" required="No">Filters can be used to remove strings from the content comparison.
     For example, if the page has a time-stamp, it might be matched with: "<code>Time: \d\d:\d\d:\d\d</code>" and replaced with a dummy fixed time "<code>Time: HH:MM:SS</code>".
     </property>
 </properties>
 </component>
 
 <component name="SMIME Assertion" index="&sect-num;.5.13"  width="471" height="428" screenshot="assertion/smime.png">
 <description>
 The SMIME Assertion can be used to evaluate the sample results from the Mail Reader Sampler.
 This assertion verifies if the body of a mime message is signed or not. The signature can also be verified against a specific signer certificate.
 As this is a functionality that is not necessarily needed by most users, additional jars need to be downloaded and added to <code>JMETER_HOME/lib</code>:<br></br> 
 <ul>
 <li><code>bcmail-xxx.jar</code> (BouncyCastle SMIME/CMS)</li>
 <li><code>bcprov-xxx.jar</code> (BouncyCastle Provider)</li>
 </ul>
 These need to be <a href="http://www.bouncycastle.org/latest_releases.html">downloaded from BouncyCastle.</a>
 <p>
 If using the <complink name="Mail Reader Sampler">Mail Reader Sampler</complink>, 
 please ensure that you select "<code>Store the message using MIME (raw)</code>" otherwise the Assertion won't be able to process the message correctly.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Verify Signature" required="Yes">If selected, the assertion will verify if it is a valid signature according to the parameters defined in the <code>Signer Certificate</code> box.</property>
     <property name="Message not signed" required="Yes">Whether or not to expect a signature in the message</property>
     <property name="Signer Cerificate" required="Yes">"<code>No Check</code>" means that it will not perform signature verification. "<code>Check values</code>" is used to verify the signature against the inputs provided. And "<code>Certificate file</code>" will perform the verification against a specific certificate file.</property>
     <property name="Message Position" required="Yes">
     The Mail sampler can retrieve multiple messages in a single sample.
     Use this field to specify which message will be checked.
     Messages are numbered from <code>0</code>, so <code>0</code> means the first message.
     Negative numbers count from the LAST message; <code>-1</code> means LAST, <code>-2</code> means penultimate etc.
     </property>
 </properties>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.6 Timers" anchor="timers">
 <description>
-    <br></br>
+    <note>
+    Since version 3.1, a new feature (in Beta mode as of JMeter 3.1 and subject to changes) has been implemented which provides the following feature.<br/>
+    You can apply a multiplication factor on the sleep delays computed by Random timer by setting property <code>timer.factor=float number</code> where float number is a decimal positive number.<br/>
+    JMeter will multiply this factor by the computed sleep delay. This feature can be used by:
+    <ul> 
+        <li><complink name="Gaussian Random Timer"/></li>
+        <li><complink name="Poisson Random Timer"/></li>
+        <li><complink name="Uniform Random Timer"/></li>
+    </ul> 
+    </note>
     <note>
     Note that timers are processed <b>before</b> each sampler in the scope in which they are found;
     if there are several timers in the same scope, <b>all</b> the timers will be processed <b>before
     each</b> sampler.
     <br></br>
     Timers are only processed in conjunction with a sampler.
     A timer which is not in the same scope as a sampler will not be processed at all.
     <br></br>
     To apply a timer to a single sampler, add the timer as a child element of the sampler.
     The timer will be applied before the sampler is executed.
     To apply a timer after a sampler, either add it to the next sampler, or add it as the
     child of a <complink name="Test Action"/> Sampler.
     </note>
 </description>
 <component name="Constant Timer" index="&sect-num;.6.1" anchor="constant" width="372" height="100" screenshot="timers/constant_timer.png">
 <description>
 <p>If you want to have each thread pause for the same amount of time between
 requests, use this timer.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree.</property>
         <property name="Thread Delay" required="Yes">Number of milliseconds to pause.</property>
 </properties>
 </component>
 
 <component name="Gaussian Random Timer" index="&sect-num;.6.2" width="372" height="156" screenshot="timers/gauss_random_timer.png">
 
 <description><p>This timer pauses each thread request for a random amount of time, with most
 of the time intervals occurring near a particular value.  
 The total delay is the sum of the Gaussian distributed value (with mean <code>0.0</code> and standard deviation <code>1.0</code>) times
 the deviation value you specify, and the offset value.
 Another way to explain it, in Gaussian Random Timer, the variation around constant offset has a gaussian curve distribution. 
 
 </p></description>
 
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree</property>
   <property name="Deviation" required="Yes">Deviation in milliseconds.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <component name="Uniform Random Timer" index="&sect-num;.6.3" width="372" height="157" screenshot="timers/uniform_random_timer.png">
 
 <description><p>This timer pauses each thread request for a random amount of time, with
 each time interval having the same probability of occurring. The total delay
 is the sum of the random value and the offset value.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Random Delay Maximum" required="Yes">Maximum random number of milliseconds to
 pause.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <component name="Constant Throughput Timer" index="&sect-num;.6.4" width="636" height="146" screenshot="timers/constant_throughput_timer.png">
 
 <description><p>This timer introduces variable pauses, calculated to keep the total throughput (in terms of samples per minute) as close as possible to a give figure. Of course the throughput will be lower if the server is not capable of handling it, or if other timers or time-consuming test elements prevent it.</p>
 <p>
 N.B. although the Timer is called the Constant Throughput timer, the throughput value does not need to be constant.
 It can be defined in terms of a variable or function call, and the value can be changed during a test.
 The value can be changed in various ways:
 </p>
 <ul>
 <li>using a counter variable</li>
 <li>using a JavaScript or BeanShell function to provide a changing value</li>
 <li>using the remote BeanShell server to change a JMeter property</li>
 </ul>
 <p>See <a href="best-practices.html">Best Practices</a> for further details.
 <note>
 Note that the throughput value should not be changed too often during a test
 - it will take a while for the new value to take effect.
 </note>
 </p>
 </description>
 <properties>
   <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Target Throughput" required="Yes">Throughput we want the timer to try to generate.</property>
   <property name="Calculate Throughput based on" required="Yes">
    <ul>
     <li><code>this thread only</code> - each thread will try to maintain the target throughput. The overall throughput will be proportional to the number of active threads.</li>
     <li><code>all active threads in current thread group</code> - the target throughput is divided amongst all the active threads in the group. 
     Each thread will delay as needed, based on when it last ran.</li>
     <li><code>all active threads</code> - the target throughput is divided amongst all the active threads in all Thread Groups.
     Each thread will delay as needed, based on when it last ran.
     In this case, each other Thread Group will need a Constant Throughput timer with the same settings.</li>
     <li><code>all active threads in current thread group (shared)</code> - as above, but each thread is delayed based on when any thread in the group last ran.</li>
     <li><code>all active threads (shared)</code> - as above; each thread is delayed based on when any thread last ran.</li>
    </ul>
   </property>
   <p>The shared and non-shared algorithms both aim to generate the desired throughput, and will produce similar results.<br/>
   The shared algorithm should generate a more accurate overall transaction rate.<br/>
   The non-shared algorithm should generate a more even spread of transactions across threads.</p>
 </properties>
 </component>
 
 <component name="Synchronizing Timer" index="&sect-num;.6.5" width="410" height="145" screenshot="timers/sync_timer.png">
 
 <description>
 <p>
 The purpose of the SyncTimer is to block threads until X number of threads have been blocked, and
 then they are all released at once.  A SyncTimer can thus create large instant loads at various
 points of the test plan.
 </p>
 </description>
 
 <properties>
   <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Number of Simultaneous Users to Group by" required="Yes">Number of threads to release at once. Setting it to <code>0</code> is equivalent to setting it to Number of threads in Thread Group.</property>
   <property name="Timeout in milliseconds" required="No">If set to <code>0</code>, Timer will wait for the number of threads to reach the value in "<code>Number of Simultaneous Users to Group</code>". If superior to <code>0</code>, then timer will wait at max "<code>Timeout in milliseconds</code>" for the number of Threads. If after the timeout interval the number of users waiting is not reached, timer will stop waiting. Defaults to <code>0</code></property>
 </properties>
 <note>
 If timeout in milliseconds is set to <code>0</code> and number of threads never reaches "<code>Number of Simultaneous Users to Group by</code>" then Test will pause infinitely.
 Only a forced stop will stop it. Setting Timeout in milliseconds is an option to consider in this case.
 </note>
 <note>
 Synchronizing timer blocks only within one JVM, so if using Distributed testing ensure you never set "<code>Number of Simultaneous Users to Group by</code>" to a value superior to the number of users
 of its containing Thread group considering 1 injector only.
 </note>
 
 </component>
 
 <component name="BeanShell Timer" index="&sect-num;.6.6"  width="846" height="636" screenshot="timers/beanshell_timer.png">
 <description>
 <p>
 The BeanShell Timer can be used to generate a delay.
 </p>
 <p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> methods.
 These should be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.
     The name is stored in the script variable <code>Label</code></property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>bsh.args</code> - String array containing parameters, split on white-space</li>
     </ul>
     </property>
     <property name="Script file" required="No">
     A file containing the BeanShell script to run.
     The file name is stored in the script variable <code>FileName</code>
      The return value is used as the number of milliseconds to wait.
      </property>
     <property name="Script" required="Yes (unless script file is provided)">
         The BeanShell script. The return value is used as the number of milliseconds to wait.
     </property>
 </properties>
 <p>Before invoking the script, some variables are set up in the BeanShell interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 </source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous <code>SampleResult</code> (if any)</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 <p>If the property <code>beanshell.timer.init</code> is defined, this is used to load an initialisation file, which can be used to define methods etc. for use in the BeanShell script.</p>
 </component>
 
 
 <component name="BSF Timer" index="&sect-num;.6.7"  width="844" height="636" screenshot="timers/bsf_timer.png">
 <description>
 <p>
 The BSF Timer can be used to generate a delay using a BSF scripting language.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="ScriptLanguage" required="Yes">
         The scripting language to be used.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul>
     </property>
     <property name="Script file" required="No">
     A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property
      The return value is converted to a long integer and used as the number of milliseconds to wait.
      </property>
     <property name="Script" required="Yes (unless script file is provided)">
         The script. The return value is used as the number of milliseconds to wait.
     </property>
 </properties>
 <p>Before invoking the script, some variables are set up in the script interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)  - the current Sampler</li>
 <li><code>Label</code> - the name of the Timer</li>
 <li><code>FileName</code> - the file name (if any)</li>
 <li><code>OUT</code> - System.out</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JSR223 Timer" index="&sect-num;.6.8">
 <description>
 <p>
 The JSR223 Timer can be used to generate a delay using a JSR223 scripting language,
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="ScriptLanguage" required="Yes">
         The scripting language to be used.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul>
     </property>
     <property name="Script file" required="No">
     A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property
      The return value is converted to a long integer and used as the number of milliseconds to wait.
      </property>
     <property name="Script compilation caching" required="No">Unique String across Test Plan that JMeter will use to cache result of Script compilation if language used supports <code>Compilable</code> interface (Groovy is one of these, java, beanshell and javascript are not)
     <note>See note in JSR223 Sampler Java System property if you're using Groovy without checking this option</note>
     </property>
     <property name="Script" required="Yes (unless script file is provided)">
         The script. The return value is used as the number of milliseconds to wait.
     </property>
 </properties>
 <p>Before invoking the script, some variables are set up in the script interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>) - the current Sampler</li>
 <li><code>Label</code> - the name of the Timer</li>
 <li><code>FileName</code> - the file name (if any)</li>
 <li><code>OUT</code> - System.out</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="Poisson Random Timer" index="&sect-num;.6.9" width="341" height="182" screenshot="timers/poisson_random_timer.png">
 
 <description><p>This timer pauses each thread request for a random amount of time, with most
 of the time intervals occurring near a particular value.  The total delay is the
 sum of the Poisson distributed value, and the offset value.</p></description>
 
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree</property>
   <property name="Lambda" required="Yes">Lambda value in milliseconds.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.7 Pre Processors" anchor="preprocessors">
     <description>
     <br></br>
         Preprocessors are used to modify the Samplers in their scope.
     <br></br>
     </description>
 <component name="HTML Link Parser" index="&sect-num;.7.1" width="373" height="79" screenshot="html_link_parser.png" anchor="html_link_parser">
 <description>
 <p>This modifier parses HTML response from the server and extracts
 links and forms.  A URL test sample that passes through this modifier will be examined to
 see if it &quot;matches&quot; any of the links or forms extracted
 from the immediately previous response.  It would then replace the values in the URL
 test sample with appropriate values from the matching link or form.  Perl-type regular
 expressions are used to find matches.</p>
 </description>
 <note>
 Matches are performed using <code>protocol</code>, <code>host</code>, <code>path</code> and <code>parameter names</code>.
 The target sampler cannot contain parameters that are not in the response links.
 </note>
 <note>
 If using distributed testing, ensure you switch mode (see <code>jmeter.properties</code>) so that it's not a stripping one, see <bugzilla>56376</bugzilla>
 </note>
 
 <example title="Spidering Example" anchor="spider_example">
 <p>Consider a simple example: let's say you wanted JMeter to &quot;spider&quot; through your site,
 hitting link after link parsed from the HTML returned from your server (this is not
 actually the most useful thing to do, but it serves as a good example).  You would create
 a <complink name="Simple Controller"/>, and add the &quot;HTML Link Parser&quot; to it.  Then, create an
 HTTP Request, and set the domain to &quot;<code>.*</code>&quot;, and the path likewise. This will
 cause your test sample to match with any link found on the returned pages.  If you wanted to
 restrict the spidering to a particular domain, then change the domain value
 to the one you want.  Then, only links to that domain will be followed.
 </p>
 </example>
 
 <example title="Poll Example" anchor="poll_example">
 <p>A more useful example: given a web polling application, you might have a page with
 several poll options as radio buttons for the user to select.  Let's say the values
 of the poll options are very dynamic - maybe user generated.  If you wanted JMeter to
 test the poll, you could either create test samples with hardcoded values chosen, or you
 could let the HTML Link Parser parse the form, and insert a random poll option into
 your URL test sample.  To do this, follow the above example, except, when configuring
 your Web Test controller's URL options, be sure to choose &quot;<code>POST</code>&quot; as the
 method.  Put in hard-coded values for the <code>domain</code>, <code>path</code>, and any additional form parameters.
 Then, for the actual radio button parameter, put in the name (let's say it's called &quot;<code>poll_choice</code>&quot;),
 and then &quot;<code>.*</code>&quot; for the value of that parameter.  When the modifier examines
 this URL test sample, it will find that it &quot;matches&quot; the poll form (and
 it shouldn't match any other form, given that you've specified all the other aspects of
 the URL test sample), and it will replace your form parameters with the matching
 parameters from the form.  Since the regular expression &quot;<code>.*</code>&quot; will match with
 anything, the modifier will probably have a list of radio buttons to choose from.  It
 will choose at random, and replace the value in your URL test sample.  Each time through
 the test, a new random value will be chosen.</p>
 
 <figure width="1250" height="493" image="modification.png">Figure 18 - Online Poll Example</figure>
 
 <note>One important thing to remember is that you must create a test sample immediately
 prior that will return an HTML page with the links and forms that are relevant to
 your dynamic test sample.</note>
 </example>
 
 </component>
 
 <component name="HTTP URL Re-writing Modifier" index="&sect-num;.7.2"  width="579" height="239" screenshot="url_rewriter.png">
 <description><p>This modifier works similarly to the HTML Link Parser, except it has a specific purpose for which
 it is easier to use than the HTML Link Parser, and more efficient.  For web applications that
 use URL Re-writing to store session ids instead of cookies, this element can be attached at the
 ThreadGroup level, much like the <complink name="HTTP Cookie Manager"/>.  Simply give it the name
 of the session id parameter, and it will find it on the page and add the argument to every
 request of that ThreadGroup.</p>
 <p>Alternatively, this modifier can be attached to select requests and it will modify only them.
 Clever users will even determine that this modifier can be used to grab values that elude the
 <complink name="HTML Link Parser"/>.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name given to this element in the test tree.</property>
         <property name="Session Argument Name" required="Yes">The name of the parameter to grab from
         previous response.  This modifier will find the parameter anywhere it exists on the page, and
         grab the value assigned to it, whether it's in an HREF or a form.</property>
         <property name="Path Extension" required="No">Some web apps rewrite URLs by appending
         a semi-colon plus the session id parameter.  Check this box if that is so.</property>
         <property name="Do not use equals in path extension" required="No">Some web apps rewrite URLs without using an &quot;<code>=</code>&quot; sign between the parameter name and value (such as Intershop Enfinity).</property>
         <property name="Do not use questionmark in path extension" required="No">Prevents the query string to end up in the path extension (such as Intershop Enfinity).</property>
         <property name="Cache Session Id?" required="Yes">
         Should the value of the session Id be saved for later use when the session Id is not present?
         </property>
         <property name="URL Encode" required="No">
         URL Encode value when writing parameter
         </property>
 </properties>
 
 <note>
 If using distributed testing, ensure you switch mode (see <code>jmeter.properties</code>) so that it's not a stripping one, see <bugzilla>56376</bugzilla>.
 </note>
 
 </component>
 
 <component name="User Parameters" index="&sect-num;.7.5"  width="703" height="303" screenshot="user_params.png">
 <description><p>Allows the user to specify values for User Variables specific to individual threads.</p>
 <p>User Variables can also be specified in the Test Plan but not specific to individual threads. This panel allows
 you to specify a series of values for any User Variable. For each thread, the variable will be assigned one of the values from the series
 in sequence. If there are more threads than values, the values get re-used. For example, this can be used to assign a distinct
 user id to be used by each thread. User variables can be referenced in any field of any jMeter Component.</p>
 
 <p>The variable is specified by clicking the <code>Add Variable</code> button in the bottom of the panel and filling in the Variable name in the '<code>Name:</code>' column.
 To add a new value to the series, click the '<code>Add User</code>' button and fill in the desired value in the newly added column.</p>
 
 <p>Values can be accessed in any test component in the same thread group, using the <a href="functions.html">function syntax</a>: <code>${variable}</code>.</p>
 <p>See also the <complink name="CSV Data Set Config"/> element, which is more suitable for large numbers of parameters</p>
 </description>
 
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Update Once Per Iteration" required="Yes">A flag to indicate whether the User Parameters element
         should update its variables only once per iteration.  if you embed functions into the UP, then you may need greater
         control over how often the values of the variables are updated.  Keep this box checked to ensure the values are
         updated each time through the UP's parent controller.  Uncheck the box, and the UP will update the parameters for 
         every sample request made within its <a href="test_plan.html#scoping_rules">scope</a>.</property>
         
 </properties>
 </component>
 
 <component name="BeanShell PreProcessor" index="&sect-num;.7.7"  width="845" height="633" screenshot="beanshell_preprocessor.png">
 <description>
 <p>
 The BeanShell PreProcessor allows arbitrary code to be applied before taking a sample.
 </p>
 <p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> methods.
 These should be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.
     The name is stored in the script variable <code>Label</code></property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>bsh.args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run.
     The file name is stored in the script variable <code>FileName</code></property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script. The return value is ignored.</property>
 </properties>
 <p>Before invoking the script, some variables are set up in the BeanShell interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult (if any)</li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)- gives access to the current sampler</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 <p>If the property <code>beanshell.preprocessor.init</code> is defined, this is used to load an initialisation file, which can be used to define methods etc. for use in the BeanShell script.</p>
 </component>
 
 <component name="BSF PreProcessor" index="&sect-num;.7.8"  width="844" height="632" screenshot="bsf_preprocessor.png">
 <description>
 <p>
 The BSF PreProcessor allows BSF script code to be applied before taking a sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The BSF language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>
 The script (or file) is processed using the <code>BSFEngine.exec()</code> method, which does not return a value.
 </p>
 <p>The following BSF variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>Label</code> - the String Label</li>
 <li><code>FileName</code> - the script file name (if any)</li>
 <li><code>Parameters</code> - the parameters (as a String)</li>
 <li><code>args</code> - the parameters as a String array (split on whitespace)</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)- gives access to the current sampler</li>
 <li><code>OUT</code> - System.out - e.g. <code>OUT.println("message")</code></li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JSR223 PreProcessor" index="&sect-num;.7.8">
 <description>
 <p>
 The JSR223 PreProcessor allows JSR223 script code to be applied before taking a sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The JSR223 language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script compilation caching" required="No">Unique String across Test Plan that JMeter will use to cache result of Script compilation if language used supports <code>Compilable</code> interface (Groovy is one of these, java, beanshell and javascript are not)
     <note>See note in JSR223 Sampler Java System property if you're using Groovy without checking this option</note>
     </property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>The following JSR223 variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>Label</code> - the String Label</li>
 <li><code>FileName</code> - the script file name (if any)</li>
 <li><code>Parameters</code> - the parameters (as a String)</li>
 <li><code>args</code> - the parameters as a String array (split on whitespace)</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)- gives access to the current sampler</li>
 <li><code>OUT</code> - System.out - e.g. <code>OUT.println("message")</code></li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JDBC PreProcessor" index="&sect-num;.7.9">
 <description>
 <p>
 The JDBC PreProcessor enables you to run some SQL statement just before a sample runs.
 This can be useful if your JDBC Sample requires some data to be in DataBase and you cannot compute this in a setup Thread group.
 For details, see <complink name="JDBC Request"/>.
 </p>
 <p>
 See the following Test plan:
 </p>
 <links>
         <link href="../demos/JDBC-Pre-Post-Processor.jmx">Test Plan using JDBC Pre/Post Processor</link>
 </links>
 <p>
 In the linked test plan, "<code>Create Price Cut-Off</code>" JDBC PreProcessor calls a stored procedure to create a Price Cut-Off in Database,
 this one will be used by "<code>Calculate Price cut off</code>".
 </p>
 <figure width="818" height="394" image="jdbc-pre-processor.png">Create Price Cut-Off Preprocessor</figure>
 
 </description>
 </component>
 
 <component name="RegEx User Parameters" index="&sect-num;.7.10"  width="727" height="138" screenshot="regex_user_params.png">
     <description><p>Allows to specify dynamic values for HTTP parameters extracted from another HTTP Request using regular expressions.
             RegEx User Parameters are specific to individual threads.</p>
         <p>This component allows you to specify reference name of a regular expression that extracts names and values of HTTP request parameters. 
             Regular expression group numbers must be specified for parameter's name and also for parameter's value.
             Replacement will only occur for parameters in the Sampler that uses this RegEx User Parameters which name matches </p>
     </description>
     
     <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Regular Expression Reference Name" required="Yes">Name of a reference to a regular expression</property>
         <property name="Parameter names regexp group number" required="Yes">Group number of regular expression used to extract parameter names</property>
         <property name="Parameter values regex group number" required="Yes">Group number of regular expression used to extract parameter values</property>
     </properties>
     
     <example title="Regexp Example" anchor="regex_user_param_example">
     <p>Suppose we have a request which returns a form with 3 input parameters and we want to extract the value of 2 of them to inject them in next request</p>
     <ol>
       <li>Create Post Processor Regular Expression for first HTTP Request
         <ul>
           <li><code>refName</code> - set name of a regular expression Expression (<code>listParams</code>)</li>
           <li><code>regular expression</code> - expression that will extract input names and input values attributes
             <br/>
             Ex: <code>input name="([^"]+?)" value="([^"]+?)"</code></li>
           <li><code>template</code> - would be empty</li>
           <li><code>match nr</code> - <code>-1</code> (in order to iterate through all the possible matches)</li>
         </ul>
       </li>
       <li>Create Pre Processor RegEx User Parameters for second HTTP Request
         <ul>
           <li><code>refName</code> - set the same reference name of a regular expression, would be <code>listParams</code> in our example</li>
           <li><code>parameter names group number</code> - group number of regular expression for parameter names, would be <code>1</code> in our example</li>
           <li><code>parameter values group number</code> - group number of regular expression for parameter values, would be <code>2</code> in our example</li>
         </ul>
       </li>
     </ol>
     </example>
     
     <p>See also the <complink name="Regular Expression Extractor"/> element, which is used to extract parametes names and values</p>
 </component>
 <links>
         <link href="../demos/RegEx-User-Parameters.jmx">Test Plan showing how to use RegEx User Parameters</link>
 </links>
 
 <component name="Sample Timeout" index="&sect-num;.7.11" anchor="interrupt" width="316" height="138" screenshot="sample_timeout.png">
 <description>
 <note>BETA CODE - the test element may be moved or replaced in a future release</note>
 <p>This Pre-Processor schedules a timer task to interrupt a sample if it takes too long to complete.
 The timeout is ignored if it is zero or negative.
 For this to work, the sampler must implement Interruptible. 
 The following samplers are known to do so:<br></br>
 AJP, BeanShell, FTP, HTTP, Soap, AccessLog, MailReader, JMS Subscriber, TCPSampler, TestAction, JavaSampler
 </p>
 <p>
 The test element is intended for use where individual timeouts such as Connection Timeout or Response Timeout are insufficient,
 or where the Sampler does not support timeouts.
 The timeout should be set sufficiently long so that it is not triggered in normal tests, but short enough that it interrupts samples
 that are stuck.
 </p>
 <p>
 [By default, JMeter uses a Callable to interrupt the sampler.
 This executes in the same thread as the timer, so if the interrupt takes a long while, 
 it may delay the processing of subsequent timeouts.
 This is not expected to be a problem, but if necessary the property <code>InterruptTimer.useRunnable</code>
 can be set to <code>true</code> to use a separate Runnable thread instead of the Callable.]
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree.</property>
         <property name="Sample Timeout" required="Yes">If the sample takes longer to complete, it will be interrupted.</property>
 </properties>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.8 Post-Processors" anchor="postprocessors">
     <description>
     <p>
         As the name suggests, Post-Processors are applied after samplers. Note that they are
         applied to <b>all</b> the samplers in the same scope, so to ensure that a post-processor
         is applied only to a particular sampler, add it as a child of the sampler.
     </p>
     <note>
     Note: Unless documented otherwise, Post-Processors are not applied to sub-samples (child samples) -
     only to the parent sample.
     In the case of BSF and BeanShell post-processors, the script can retrieve sub-samples using the method
     <code>prev.getSubResults()</code> which returns an array of SampleResults.
     The array will be empty if there are none.
     </note>
     <p>
     Post-Processors are run before Assertions, so they do not have access to any Assertion Results, nor will
     the sample status reflect the results of any Assertions. If you require access to Assertion Results, try
     using a Listener instead. Also note that the variable <code>JMeterThread.last_sample_ok</code> is set to "<code>true</code>" or "<code>false</code>"
     after all Assertions have been run.
     </p>
     </description>
 <component name="Regular Expression Extractor" index="&sect-num;.8.1"  width="1127" height="277" screenshot="regex_extractor.png">
 <description><p>Allows the user to extract values from a server response using a Perl-type regular expression.  As a post-processor,
 this element will execute after each Sample request in its scope, applying the regular expression, extracting the requested values,
 generate the template string, and store the result into the given variable name.</p></description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - only applies to the main sample</li>
         <li><code>Sub-samples only</code> - only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         Matching is applied to all qualifying samples in turn.
         For example if there is a main sample and 3 sub-samples, each of which contains a single match for the regex,
         (i.e. 4 matches in total).
         For match number = <code>3</code>, Sub-samples only, the extractor will match the 3<sup>rd</sup> sub-sample.
         For match number = <code>3</code>, Main sample and sub-samples, the extractor will match the 2<sup>nd</sup> sub-sample (1<sup>st</sup> match is main sample).
         For match number = <code>0</code> or negative, all qualifying samples will be processed.
         For match number > <code>0</code>, matching will stop as soon as enough matches have been found.
         </property>
         <property name="Field to check" required="Yes">
         The following fields can be checked:
         <ul>
         <li><code>Body</code> - the body of the response, e.g. the content of a web-page (excluding headers)</li>
         <li><code>Body (unescaped)</code> - the body of the response, with all Html escape codes replaced.
         Note that Html escapes are processed without regard to context, so some incorrect substitutions
         may be made.
         <note>Note that this option highly impacts performances, so use it only when absolutely necessary and be aware of its impacts</note>
         </li>
         <li><code>Body as a Document</code> - the extract text from various type of documents via Apache Tika (see <complink name="View Results Tree"/> Document view section).
         <note>Note that the Body as a Document option can impact performances, so ensure it is OK for your test</note>
         </li>
         <li><code>Request Headers</code> - may not be present for non-HTTP samples</li>
         <li><code>Response Headers</code> - may not be present for non-HTTP samples</li>
         <li><code>URL</code></li>
         <li><code>Response Code</code> - e.g. <code>200</code></li>
         <li><code>Response Message</code> - e.g. <code>OK</code></li>
         </ul>
         Headers can be useful for HTTP samples; it may not be present for other sample types.
         </property>
         <property name="Reference Name" required="Yes">The name of the JMeter variable in which to store the result.  Also note that each group is stored as <code>[refname]_g#</code>, where <code>[refname]</code> is the string you entered as the reference name, and <code>#</code> is the group number, where group <code>0</code> is the entire match, group <code>1</code> is the match from the first set of parentheses, etc.</property>
         <property name="Regular Expression" required="Yes">The regular expression used to parse the response data. 
         This must contain at least one set of parentheses "<code>()</code>" to capture a portion of the string, unless using the group <code>$0$</code>.
         Do not enclose the expression in <code>/ /</code> - unless of course you want to match these characters as well.
         </property>
         <property name="Template" required="Yes">The template used to create a string from the matches found.  This is an arbitrary string
         with special elements to refer to groups within the regular expression.  The syntax to refer to a group is: '<code>$1$</code>' to refer to
         group <code>1</code>, '<code>$2$</code>' to refer to group <code>2</code>, etc. <code>$0$</code> refers to whatever the entire expression matches.</property>
         <property name="Match No." required="Yes">Indicates which match to use.  The regular expression may match multiple times.  
             <ul>
                 <li>Use a value of zero to indicate JMeter should choose a match at random.</li>
                 <li>A positive number N means to select the n<sup>th</sup> match.</li>
                 <li> Negative numbers are used in conjunction with the <complink name="ForEach Controller"/> - see below.</li>
             </ul>
         </property>
         <property name="Default Value" required="No, but recommended">
         If the regular expression does not match, then the reference variable will be set to the default value.
         This is particularly useful for debugging tests. If no default is provided, then it is difficult to tell
         whether the regular expression did not match, or the RE element was not processed or maybe the wrong variable
         is being used.
         <p>
         However, if you have several test elements that set the same variable, 
         you may wish to leave the variable unchanged if the expression does not match.
         In this case, remove the default value once debugging is complete.
         </p> 
         </property>
         <property name="Use empty default value" required="No">
         If the checkbox is checked and <code>Default Value</code> is empty, then JMeter will set the variable to empty string instead of not setting it.
         Thus when you will for example use <code>${var}</code> (if <code>Reference Name</code> is var) in your Test Plan, if the extracted value is not found then 
         <code>${var}</code> will be equal to empty string instead of containing <code>${var}</code> which may be useful if extracted value is optional.
         </property>
 </properties>
 <p>
     If the match number is set to a non-negative number, and a match occurs, the variables are set as follows:
 </p>
     <ul>
         <li><code>refName</code> - the value of the template</li>
         <li><code>refName_g<em>n</em></code>, where <code>n</code>=<code>0</code>,<code>1</code>,<code>2</code> - the groups for the match</li>
         <li><code>refName_g</code> - the number of groups in the Regex (excluding <code>0</code>)</li>
     </ul>
 <p>
     If no match occurs, then the <code>refName</code> variable is set to the default (unless this is absent). 
     Also, the following variables are removed:
 </p>
     <ul>
         <li><code>refName_g0</code></li>
         <li><code>refName_g1</code></li>
         <li><code>refName_g</code></li>
     </ul>
 <p>
     If the match number is set to a negative number, then all the possible matches in the sampler data are processed.
     The variables are set as follows:
 </p>
     <ul>
         <li><code>refName_matchNr</code> - the number of matches found; could be <code>0</code></li>
         <li><code>refName_<em>n</em></code>, where <code>n</code> = <code>1</code>, <code>2</code>, <code>3</code> etc. - the strings as generated by the template</li>
         <li><code>refName_<em>n</em>_g<em>m</em></code>, where <code>m</code>=<code>0</code>, <code>1</code>, <code>2</code> - the groups for match <code>n</code></li>
         <li><code>refName</code> - always set to the default value</li>
         <li><code>refName_g<em>n</em></code> - not set</li>
     </ul>
 <p>
     Note that the <code>refName</code> variable is always set to the default value in this case, 
     and the associated group variables are not set.
 </p>
     <p>See also <complink name="Response Assertion"/> for some examples of how to specify modifiers,
     and <a href="regular_expressions.html"> for further information on JMeter regular expressions.</a></p>
 </component>
 
 <component name="CSS/JQuery Extractor" index="&sect-num;.8.2"  width="826" height="276" screenshot="css_extractor_attr.png">
 <description><p>Allows the user to extract values from a server response using a CSS/JQuery selector like syntax.  As a post-processor,
 this element will execute after each Sample request in its scope, applying the CSS/JQuery expression, extracting the requested nodes,
 extracting the node as text or attribute value and store the result into the given variable name.</p></description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - only applies to the main sample</li>
         <li><code>Sub-samples only</code> - only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         Matching is applied to all qualifying samples in turn.
         For example if there is a main sample and 3 sub-samples, each of which contains a single match for the regex,
         (i.e. 4 matches in total).
         For match number = <code>3</code>, Sub-samples only, the extractor will match the 3<sup>rd</sup> sub-sample.
         For match number = <code>3</code>, Main sample and sub-samples, the extractor will match the 2<sup>nd</sup> sub-sample (1<sup>st</sup> match is main sample).
         For match number = <code>0</code> or negative, all qualifying samples will be processed.
         For match number > <code>0</code>, matching will stop as soon as enough matches have been found.
         </property>
         <property name="CSS/JQuery extractor Implementation" required="False">
         2 Implementations for CSS/JQuery based syntax are supported:
         <ul>
                 <li><a href="http://jsoup.org/" >JSoup</a></li>
                 <li><a href="http://jodd.org/doc/lagarto/index.html" >Jodd-Lagarto (CSSelly)</a></li>
         </ul>
         If selector is set to empty, default implementation(JSoup) will be used.
         </property>
         <property name="Reference Name" required="Yes">The name of the JMeter variable in which to store the result.</property>
         <property name="CSS/JQuery expression" required="Yes">The CSS/JQuery selector used to select nodes from the response data. 
         Selector, selectors combination and pseudo-selectors are supported, examples:
         <ul>
             <li><code>E[foo]</code> - an <code>E</code> element with a "<code>foo</code>" attribute</li>
             <li><code>ancestor child</code> - child elements that descend from ancestor, e.g. <code>.body p</code> finds <code>p</code> elements anywhere under a block with class "<code>body</code>"</li>
             <li><code>:lt(n)</code> - find elements whose sibling index (i.e. its position in the DOM tree relative to its parent) is less than <code>n</code>; e.g. <code>td:lt(3)</code></li>
             <li><code>:contains(text)</code> - find elements that contain the given <code>text</code>. The search is case-insensitive; e.g. <code>p:contains(jsoup)</code></li>
             <li>&hellip;</li>
         </ul>
         For more details on syntax, see:
             <ul>
                 <li><a href="http://jsoup.org/cookbook/extracting-data/selector-syntax" >JSoup</a></li>
                 <li><a href="http://jodd.org/doc/csselly/" >Jodd-Lagarto (CSSelly)</a></li>
             </ul>
         </property>
         <property name="Attribute" required="false">
             Name of attribute (as per HTML syntax) to extract from nodes that matched the selector. If empty, then the combined text of this element and all its children will be returned.<br/>
             This is the equivalent <a href="http://jsoup.org/apidocs/org/jsoup/nodes/Node.html#attr%28java.lang.String%29">Element#attr(name)</a> function for JSoup if an atttribute is set.<br/>
             <figure width="826" height="275" image="css_extractor_attr.png">CSS Extractor with attribute value set</figure><br/>
             If empty this is the equivalent of <a href="http://jsoup.org/apidocs/org/jsoup/nodes/Element.html#text%28%29">Element#text()</a> function for JSoup if not value is set for attribute.
             <figure width="825" height="275" image="css_extractor_noattr.png">CSS Extractor with no attribute set</figure>
         </property>
         <property name="Match No." required="Yes">Indicates which match to use.  The CSS/JQuery selector may match multiple times.  
             <ul>
                 <li>Use a value of zero to indicate JMeter should choose a match at random.</li>
                 <li>A positive number <code>N</code> means to select the n<sup>th</sup> match.</li>
                 <li> Negative numbers are used in conjunction with the <complink name="ForEach Controller"/> - see below.</li>
             </ul>
         </property>
         <property name="Default Value" required="No, but recommended">
         If the expression does not match, then the reference variable will be set to the default value.
         This is particularly useful for debugging tests. If no default is provided, then it is difficult to tell
         whether the expression did not match, or the CSS/JQuery element was not processed or maybe the wrong variable
         is being used.
         <p>
         However, if you have several test elements that set the same variable, 
         you may wish to leave the variable unchanged if the expression does not match.
         In this case, remove the default value once debugging is complete.
         </p> 
         </property>
         <property name="Use empty default value" required="No">
         If the checkbox is checked and <code>Default Value</code> is empty, then JMeter will set the variable to empty string instead of not setting it.
         Thus when you will for example use <code>${var}</code> (if <code>Reference Name</code> is var) in your Test Plan, if the extracted value is not found then 
         <code>${var}</code> will be equal to empty string instead of containing <code>${var}</code> which may be useful if extracted value is optional.
         </property>
 </properties>
 <p>
     If the match number is set to a non-negative number, and a match occurs, the variables are set as follows:
 </p>
     <ul>
         <li><code>refName</code> - the value of the template</li>
     </ul>
 <p>
     If no match occurs, then the <code>refName</code> variable is set to the default (unless this is absent). 
 </p>
 <p>
     If the match number is set to a negative number, then all the possible matches in the sampler data are processed.
     The variables are set as follows:
 </p>
     <ul>
         <li><code>refName_matchNr</code> - the number of matches found; could be <code>0</code></li>
         <li><code>refName_n</code>, where <code>n</code> = <code>1</code>, <code>2</code>, <code>3</code>, etc. - the strings as generated by the template</li>
         <li><code>refName</code> - always set to the default value</li>
     </ul>
 <p>
     Note that the refName variable is always set to the default value in this case.
 </p>
 </component>
 
 <component name="XPath Extractor" index="&sect-num;.8.3"  width="729" height="317" screenshot="xpath_extractor.png">
     <description>This test element allows the user to extract value(s) from 
         structured response - XML or (X)HTML - using XPath
         query language.
    </description>
    <properties>
        <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
        <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - only applies to the main sample</li>
         <li><code>Sub-samples only</code> - only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         XPath matching is applied to all qualifying samples in turn, and all the matching results will be returned.
        </property>
        <property name="Use Tidy (tolerant parser)" required="Yes">If checked use Tidy to parse HTML response into XHTML.
        <ul>
            <li>"<code>Use Tidy</code>" should be checked on for HTML response. Such response is converted to valid XHTML (XML compatible HTML) using Tidy</li>
            <li>"<code>Use Tidy</code>" should be unchecked for both XHTML or XML response (for example RSS)</li>
        </ul>
        </property>
 <property name="Quiet"  required="If Tidy is selected">Sets the Tidy Quiet flag</property>
 <property name="Report Errors"  required="If Tidy is selected">If a Tidy error occurs, then set the Assertion accordingly</property>
 <property name="Show warnings"  required="If Tidy is selected">Sets the Tidy showWarnings option</property>
 <property name="Use Namespaces" required="If Tidy is not selected">
         If checked, then the XML parser will use namespace resolution.
         Note that currently only namespaces declared on the root element will be recognised.
         A later version of JMeter may support user-definition of additional workspace names.
         Meanwhile, a work-round is to replace: 
         <source>//mynamespace:tagname</source>
         by
         <source>//*[local-name()='tagname' and namespace-uri()='uri-for-namespace']</source>
         where "<code>uri-for-namespace</code>" is the uri for the "<code>mynamespace</code>" namespace.
         (not applicable if Tidy is selected)
 </property>
     <property name="Validate XML"   required="If Tidy is not selected">Check the document against its schema.</property>
     <property name="Ignore Whitespace"  required="If Tidy is not selected">Ignore Element Whitespace.</property>
     <property name="Fetch External DTDs"  required="If Tidy is not selected">If selected, external DTDs are fetched.</property>
     <property name="Return entire XPath fragment instead of text content?" required="Yes">
     If selected, the fragment will be returned rather than the text content.<br></br>
     For example <code>//title</code> would return "<code>&lt;title&gt;Apache JMeter&lt;/title&gt;</code>" rather than "<code>Apache JMeter</code>".<br></br>
     In this case, <code>//title/text()</code> would return "<code>Apache JMeter</code>".
     </property>
     <property name="Reference Name" required="Yes">The name of the JMeter variable in which to store the result.</property>
     <property name="XPath Query" required="Yes">Element query in XPath language. Can return more than one match.</property>
     <property name="Default Value" required="">Default value returned when no match found. 
     It is also returned if the node has no value and the fragment option is not selected.</property>
    </properties>
    <p>To allow for use in a <complink name="ForEach Controller"/>, the following variables are set on return:</p>
    <ul>
    <li><code>refName</code> - set to first (or only) match; if no match, then set to default</li>
    <li><code>refName_matchNr</code> - set to number of matches (may be <code>0</code>)</li>
    <li><code>refName_n</code> - <code>n</code>=<code>1</code>, <code>2</code>, <code>3</code>, etc. Set to the 1<sup>st</sup>, 2<sup>nd</sup> 3<sup>rd</sup> match etc. 
    </li>
    </ul>
    <note>Note: The next <code>refName_n</code> variable is set to <code>null</code> - e.g. if there are 2 matches, then <code>refName_3</code> is set to <code>null</code>,
    and if there are no matches, then <code>refName_1</code> is set to <code>null</code>.
    </note>
    <p>XPath is query language targeted primarily for XSLT transformations. However it is useful as generic query language for structured data too. See 
        <a href="http://www.topxml.com/xsl/xpathref.asp">XPath Reference</a> or <a href="http://www.w3.org/TR/xpath">XPath specification</a> for more information. Here are few examples:
    </p>
   <dl> 
    <dt><code>/html/head/title</code></dt>
      <dd>extracts title element from HTML response</dd>
    <dt><code>/book/page[2]</code></dt>
      <dd>extracts 2<sup>nd</sup> page from a book</dd>
    <dt><code>/book/page</code></dt>
      <dd>extracts all pages from a book</dd>
      <dt><code>//form[@name='countryForm']//select[@name='country']/option[text()='Czech Republic'])/@value</code></dt>
     <dd>extracts value attribute of option element that match text '<code>Czech Republic</code>'
         inside of select element with name attribute  '<code>country</code>' inside of
         form with name attribute '<code>countryForm</code>'</dd>
  </dl>
  <note>When "<code>Use Tidy</code>" is checked on - resulting XML document may slightly differ from original HTML response:
      <ul>
          <li>All elements and attribute names are converted to lowercase</li>
          <li>Tidy attempts to correct improperly nested elements. For example - original (incorrect) <code>ul/font/li</code> becomes correct <code>ul/li/font</code></li>
      </ul>
      See <a href="http://jtidy.sf.net">Tidy homepage</a> for more information.
  </note>
  
 </component>
 
 <component name="Result Status Action Handler" index="&sect-num;.8.4"  width="613" height="133" screenshot="resultstatusactionhandler.png">
    <description>This test element allows the user to stop the thread or the whole test if the relevant sampler failed.
    </description>
    <properties>
    <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
    <property name="Action to be taken after a Sampler error">
    Determines what happens if a sampler error occurs, either because the sample itself failed or an assertion failed.
    The possible choices are:
    <ul>
    <li><code>Continue</code> - ignore the error and continue with the test</li>
    <li><code>Start next thread loop</code> - does not execute samplers following the sampler in error for the current iteration and restarts the loop on next iteration</li>
    <li><code>Stop Thread</code> - current thread exits</li>
    <li><code>Stop Test</code> - the entire test is stopped at the end of any current samples.</li>
    <li><code>Stop Test Now</code> - the entire test is stopped abruptly. Any current samplers are interrupted if possible.</li>
    </ul>
    </property>
    </properties>
 </component>
 
 <component name="BeanShell PostProcessor"  index="&sect-num;.8.5"  width="847" height="633" screenshot="beanshell_postprocessor.png">
 <description>
 <p>
 The BeanShell PreProcessor allows arbitrary code to be applied after taking a sample.
 </p>
 <p>BeanShell Post-Processor no longer ignores samples with zero-length result data</p>
 <p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> methods.
 These should be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.
     The name is stored in the script variable <code>Label</code></property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
