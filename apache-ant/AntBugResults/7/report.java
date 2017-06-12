File path: src/main/org/apache/tools/ant/util/DOMElementWriter.java
Comment: FIXME: Is "No Namespace is Empty Namespace" really OK?
Initial commit id: cc6786e6
Final commit id: b7d1e9bd
   Bugs between [       3]:
313479bb3 try to make OOM in <junit> less likely.  PR 45536.  Submitted by Steve Loughran who decided not to commit it himself :-)
9cde786c6 use a better approach to encode CDATA end markers in CDATA sections as suggested by Mark Lassau and Wikipedia.  PR 49404
f8f45e9f0 unconditionally encode & as &amp; in DOMElementWriter.  PR 49404
   Bugs after [       0]:


Start block index: 552
End block index: 559
    private static String getNamespaceURI(Node n) {
        String uri = n.getNamespaceURI();
        if (uri == null) {
            // FIXME: Is "No Namespace is Empty Namespace" really OK?
            uri = "";
        }
        return uri;
    }
