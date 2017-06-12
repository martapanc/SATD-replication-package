File path: core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
Comment: todo : we can remove this once the deprecated ctor can be made private...
Initial commit id: 1851bffc
Final commit id: a6ca833e
   Bugs between [       1]:
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
   Bugs after [       2]:
34c2839dcc HHH-5616 - Switch to Gradle for builds
dc00c4dcde HHH-5163 : ClassCastException caching results using ResultTransformer

Start block index: 81
End block index: 84
	public boolean equals(Object obj) {
		// todo : we can remove this once the deprecated ctor can be made private...
		return DistinctRootEntityResultTransformer.class.isInstance( obj );
	}
