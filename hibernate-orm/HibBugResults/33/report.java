File path: code/core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
Comment: todo : what else to do here?
Initial commit id: d8d6d82e
Final commit id: 82d2ef4b
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 102
End block index: 138
		public Object intercept(
				Object obj,
		        Method method,
		        Object[] args,
		        MethodProxy proxy) throws Throwable {
			String name = method.getName();
			if ( "toString".equals( name ) ) {
				return proxiedClassName + "@" + System.identityHashCode( obj );
			}
			else if ( "equals".equals( name ) ) {
				return args[0] instanceof Factory && ( ( Factory ) args[0] ).getCallback( 0 ) == this
						? Boolean.TRUE
			            : Boolean.FALSE;
			}
			else if ( "hashCode".equals( name ) ) {
				return new Integer( System.identityHashCode( obj ) );
			}
			boolean hasGetterSignature = method.getParameterTypes().length == 0 && method.getReturnType() != null;
			boolean hasSetterSignature = method.getParameterTypes().length == 1 && ( method.getReturnType() == null || method.getReturnType() == void.class );
			if ( name.startsWith( "get" ) && hasGetterSignature ) {
				String propName = name.substring( 3 );
				return data.get( propName );
			}
			else if ( name.startsWith( "is" ) && hasGetterSignature ) {
				String propName = name.substring( 2 );
				return data.get( propName );
			}
			else if ( name.startsWith( "set" ) && hasSetterSignature) {
				String propName = name.substring( 3 );
				data.put( propName, args[0] );
				return null;
			}
			else {
				// todo : what else to do here?
				return null;
			}
		}
