File path: src/core/org/apache/jmeter/control/TransactionController.java
Comment: TODO could these be done earlier (or just once?)
Initial commit id: 3b869c76
Final commit id: cf1c0dc6
   Bugs between [       1]:
cf1c0dc65 Bug 41913 - TransactionController now creates samples as sub-samples of the transaction
   Bugs after [      22]:
5f0651b4a Bug 60564 - Migrating LogKit to SLF4J - core, core/control, core/engine/ClientJMeterEngine Contributed by Woonsan Ko This closes #269 Bugzilla Id: 60564
caaf9e666 Bug 53039 - HTTP Request : Be able to handle responses which size exceeds 2147483647 bytes Bugzilla Id: 53039
bac01a627 Bug 60229 - Add a new metric : sent_bytes Bugzilla Id: 60229
6c9d00ae1 Bug 59067 - JMeter fails to iterate over Controllers that are children of a TransactionController having "Generate parent sample" checked after an assertion error occurs on a Thread Group with "Start Next Thread Loop" Bugzilla Id: 59067
1058659d6 Bug 58122 - GraphiteBackendListener : Add Server Hits metric This resolves #26 Bugzilla Id: 58122
65bd9c284 Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
07d60f60f Bug 56811 - "Start Next Thread Loop" in Result Status Action Handler or on Thread Group and "Go to next Loop iteration" in Test Action behave incorrectly with TransactionController that has "Generate Parent Sampler" checked Bugzilla Id: 56811
aa77e7b86 Bug 56160 - StackOverflowError when using WhileController within IfController Bugzilla Id: 56160
a6696aa5d Bug 55816 - Transaction Controller with "Include duration of timer..." unchecked does not ignore processing time of last child sampler Bugzilla Id: 55816
1710a70a1 Bug 52265 - Code:Transient fields not set by deserialization Bugzilla Id: 52265
03ea5d70c Bug 52968 - Option Start Next Loop in Thread Group does not mark parent Transaction Sampler in error when an error occurs
2c316251e Bug 52296 - TransactionController with Child ThrouputController : Getting ERROR sampleEnd called twice java.lang.Throwable: Invalid call sequence when TPC does not run sample
f27a8aacf Bug 52296 - Getting ERROR sampleEnd called twice java.lang.Throwable: Invalid call sequence Bug 52296 - TransactionController with Child ThrouputController : Getting ERROR sampleEnd called twice java.lang.Throwable: Invalid call sequence when TPC does not run sample
3143c47e2 Bug 52296 - Getting ERROR sampleEnd called twice java.lang.Throwable: Invalid call sequence
6572ccd24 Bug 51876 - Functionnality to search in Samplers TreeView
6d25bd5a1 Bug 51876 - Functionnality to search in Samplers TreeView
df78199ca Bug 50032 - Fixed a new regression introduced by bug 50032 when Transaction Controller is a child of If Controller
0c9eab399 Bug 50134 - TransactionController : Reports bad response time when it contains other TransactionControllers
12b53ca48 Bug 41418 - Exclude timer duration from Transaction Controller runtime in report
34c29868e Bug 47909 - TransactionController should sum the latency
54e1cef5d Bug 47385 - TransactionController should set AllThreads and GroupThreads
858ce0385 Bug 42778 - Transaction Controller skips sample (NPE)

Start block index: 108
End block index: 155
    public Sampler next()
    {
		Sampler returnValue = null;
    	if (isFirst()) // must be the start of the subtree
    	{
    		log_debug("+++++++++++++++++++++++++++++");
    		calls = 0;
    		res = new SampleResult();
    		res.sampleStart();
    	}
    	
    	calls++;
    	
    	returnValue = super.next();

        if (returnValue == null) // Must be the end of the controller
        {
			log_debug("-----------------------------"+calls);
        	if (res == null){
        		log_debug("already called");
        	} else {
				res.sampleEnd();
				res.setSuccessful(true);
				res.setSampleLabel(getName());
				res.setResponseCode("200");
				res.setResponseMessage("Called: "+calls);
				res.setThreadName(threadName);
        	
				//TODO could these be done earlier (or just once?)
				threadContext = JMeterContextService.getContext();
				threadVars = threadContext.getVariables();
				
				SamplePackage pack = (SamplePackage)
				              threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
				if (pack == null)
				{
					log.warn("Could not fetch SamplePackage");
				}
				else 
				{
					lnf.notifyListeners(new SampleEvent(res,getName()),pack.getSampleListeners());
				}
				res=null;
        	}
        }

        return returnValue;
    }
