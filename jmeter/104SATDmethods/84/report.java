File path: src/core/org/apache/jmeter/util/BeanShellClient.java
Comment: TODO Why empty block ?
Initial commit id: 914e90e0c
Final commit id: 73acbaf48
   Bugs between [       1]:
f41a28e93 Bug 57084 - BeanShellClient: Close socket after usage. Bugzilla Id: 57084
   Bugs after [       0]:


Start block index: 98
End block index: 113
        public void run(){
            System.out.println("Reading responses from server ...");
            int x = 0;
            try {
                while ((x = is.read()) > -1) {
                    char c = (char) x;
                    System.out.print(c);
                }
            } catch (IOException e) {
                // TODO Why empty block ?
            } finally {
                System.out.println("... disconnected from server.");
                JOrphanUtils.closeQuietly(is);
            }

        }

*********************** Method when SATD was removed **************************

@Override
public void run(){
    System.out.println("Reading responses from server ...");
    int x = 0;
    try {
        while ((x = is.read()) > -1) {
            char c = (char) x;
            System.out.print(c);
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        System.out.println("... disconnected from server.");
    }
}
