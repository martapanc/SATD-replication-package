File path: proposal/embed/src/java/org/apache/tools/ant/PropertyHelper.java
Comment: Experimental/Testing
Initial commit id: 87912cbc
Final commit id: 6ecafdd4
   Bugs between [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 194
End block index: 209
    public Object getPropertyHook(String ns, String name, boolean user) {
        if( getNext() != null ) {
            Object o=getNext().getPropertyHook(ns, name, user);
            if( o!= null ) return o;
        }
        // Experimental/Testing, will be removed
        if( name.startsWith( "toString:" )) {
            name=name.substring( "toString:".length());
            Object v=project.getReference( name );
            if( v==null ) return null;
            return v.toString();
        }


        return null;
    }
