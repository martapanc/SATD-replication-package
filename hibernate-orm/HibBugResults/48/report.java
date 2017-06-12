File path: code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
Comment: TODO: better to degrade to lazy="false" if uninstrumented
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:

Start block index: 1502
End block index: 1516
   private static void initLaziness(
       Element node,
       ToOne fetchable,
       Mappings mappings,
       boolean defaultLazy
   ) {
     if ( "no-proxy".equals( node.attributeValue( "lazy" ) ) ) {
       fetchable.setUnwrapProxy(true);
       fetchable.setLazy(true);
       //TODO: better to degrade to lazy="false" if uninstrumented
     }
     else {
       initLaziness(node, fetchable, mappings, "proxy", defaultLazy);
     }
   }
