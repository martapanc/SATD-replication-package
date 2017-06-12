File path: code/core/src/main/java/org/hibernate/type/AbstractBynaryType.java
Comment: is this really necessary?
Initial commit id: d8d6d82e
Final commit id: 7308e14f
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 46
End block index: 79
	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {

		if ( Environment.useStreamsForBinary() ) {

			InputStream inputStream = rs.getBinaryStream(name);

			if (inputStream==null) return toExternalFormat( null ); // is this really necessary?

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2048);
			byte[] buffer = new byte[2048];

			try {
				while (true) {
					int amountRead = inputStream.read(buffer);
					if (amountRead == -1) {
						break;
					}
					outputStream.write(buffer, 0, amountRead);
				}

				inputStream.close();
				outputStream.close();
			}
			catch (IOException ioe) {
				throw new HibernateException( "IOException occurred reading a binary value", ioe );
			}

			return toExternalFormat( outputStream.toByteArray() );

		}
		else {
			return toExternalFormat( rs.getBytes(name) );
		}
	}
