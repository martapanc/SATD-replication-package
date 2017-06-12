File path: src/org/jruby/ast/EncodingNode.java
Comment: / FIXME: We should be getting this from the runtime rather than assume none?
Initial commit id: 89812419
Final commit id: 52192272
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 63
End block index: 67
    public IRubyObject interpret(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
        // FIXME: We should be getting this from the runtime rather than assume none?
        //return runtime.getEncodingService().getEncoding(runtime.getDefaultExternalEncoding());
        return runtime.getEncodingService().getEncoding(KCode.NONE.getEncoding());
    }
