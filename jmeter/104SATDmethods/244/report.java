File path: src/protocol/http/org/apache/jmeter/protocol/http/config/gui/MultipartUrlConfigGui.java
Comment: not currently used
Initial commit id: 0431342f
Final commit id: 962c7faa
   Bugs between [       4]:
564705a40 Bug 59060 - HTTP Request GUI : Move File Upload to a new Tab to have more space for parameters and prevent incoherent configuration Contributed by Benoit Wiart #resolve #140 https://github.com/apache/jmeter/pull/140 Bugzilla Id: 59060
2ed95f964 Bug 55606 - Use JSyntaxtTextArea for Http Request, JMS Test Elements Reverting crappy code Bugzilla Id: 55606
f3cdb589a Bug 55606 - Use JSyntaxtTextArea for Http Request, JMS Test Elements Fix test case failure in Headless mode Bugzilla Id: 55606
4b9cb415a Bug 51861 - Improve HTTP Request GUI to better show parameters without name (GWT RPC requests for example)
   Bugs after [       0]:


Start block index: 49
End block index: 53
	// not currently used
    public MultipartUrlConfigGui(boolean value) {
        super(value);
        init();
    }
