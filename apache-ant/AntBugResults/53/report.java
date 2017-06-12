File path: src/testcases/org/apache/tools/ant/taskdefs/ExecuteWatchdogTest.java
Comment: not very nice but will do the job
Initial commit id: da10e54d
Final commit id: b5057787
   Bugs between [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 162
End block index: 169
				public void run(){
					try {
						process.waitFor();
					} catch(InterruptedException e){
						// not very nice but will do the job
						fail("process interrupted in thread");
					}
				}
