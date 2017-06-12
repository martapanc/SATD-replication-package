diff --git a/hibernate-core/src/main/java/org/hibernate/StoredProcedureCall.java b/hibernate-core/src/main/java/org/hibernate/StoredProcedureCall.java
index 4d5bfd1e8e..34c4c9c7db 100644
--- a/hibernate-core/src/main/java/org/hibernate/StoredProcedureCall.java
+++ b/hibernate-core/src/main/java/org/hibernate/StoredProcedureCall.java
@@ -1,197 +1,198 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate;
 
 import javax.persistence.ParameterMode;
 import javax.persistence.TemporalType;
 import java.util.List;
 
+import org.hibernate.internal.StoredProcedureCallImpl;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public interface StoredProcedureCall extends BasicQueryContract, SynchronizeableQuery {
 	@Override
 	StoredProcedureCall addSynchronizedQuerySpace(String querySpace);
 
 	@Override
 	StoredProcedureCall addSynchronizedEntityName(String entityName) throws MappingException;
 
 	@Override
 	StoredProcedureCall addSynchronizedEntityClass(Class entityClass) throws MappingException;
 
 	/**
 	 * Get the name of the stored procedure to be called.
 	 *
 	 * @return The procedure name.
 	 */
 	public String getProcedureName();
 
 
 	/**
 	 * Register a positional parameter.
 	 * All positional parameters must be registered.
 	 *
 	 * @param position parameter position
 	 * @param type type of the parameter
 	 * @param mode parameter mode
 	 *
 	 * @return the same query instance
 	 */
 	StoredProcedureCall registerStoredProcedureParameter(
 			int position,
 			Class type,
 			ParameterMode mode);
 
 	/**
 	 * Register a named parameter.
 	 * When using parameter names, all parameters must be registered
 	 * in the order in which they occur in the parameter list of the
 	 * stored procedure.
 	 *
 	 * @param parameterName name of the parameter as registered or
 	 * <p/>
 	 * specified in metadata
 	 * @param type type of the parameter
 	 * @param mode parameter mode
 	 *
 	 * @return the same query instance
 	 */
 	StoredProcedureCall registerStoredProcedureParameter(
 			String parameterName,
 			Class type,
 			ParameterMode mode);
 
 	/**
 	 * Retrieve all registered parameters.
 	 *
 	 * @return The (immutable) list of all registered parameters.
 	 */
 	public List<StoredProcedureParameter> getRegisteredParameters();
 
 	/**
 	 * Retrieve parameter registered by name.
 	 *
 	 * @param name The name under which the parameter of interest was registered.
 	 *
 	 * @return The registered parameter.
 	 */
 	public StoredProcedureParameter getRegisteredParameter(String name);
 	public StoredProcedureParameter getRegisteredParameter(int position);
 
 	public StoredProcedureOutputs getOutputs();
 
 	/**
 	 * Describes a parameter registered with the stored procedure.  Parameters can be either named or positional
 	 * as the registration mechanism.  Named and positional should not be mixed.
 	 */
 	public static interface StoredProcedureParameter<T> {
 		/**
 		 * The name under which this parameter was registered.  Can be {@code null} which should indicate that
 		 * positional registration was used (and therefore {@link #getPosition()} should return non-null.
 		 *
 		 * @return The name;
 		 */
 		public String getName();
 
 		/**
 		 * The position at which this parameter was registered.  Can be {@code null} which should indicate that
 		 * named registration was used (and therefore {@link #getName()} should return non-null.
 		 *
 		 * @return The name;
 		 */
 		public Integer getPosition();
 
 		/**
 		 * Obtain the Java type of parameter.  This is used to guess the Hibernate type (unless {@link #setHibernateType}
 		 * is called explicitly).
 		 *
 		 * @return The parameter Java type.
 		 */
 		public Class<T> getType();
 
 		/**
 		 * Retrieves the parameter "mode" which describes how the parameter is defined in the actual database procedure
 		 * definition (is it an INPUT parameter?  An OUTPUT parameter? etc).
 		 *
 		 * @return The parameter mode.
 		 */
 		public ParameterMode getMode();
 
 		/**
 		 * Set the Hibernate mapping type for this parameter.
 		 *
 		 * @param type The Hibernate mapping type.
 		 */
 		public void setHibernateType(Type type);
 
 		/**
 		 * Retrieve the binding associated with this parameter.  The binding is only relevant for INPUT parameters.  Can
 		 * return {@code null} if nothing has been bound yet.  To bind a value to the parameter use one of the
 		 * {@link #bindValue} methods.
 		 *
 		 * @return The parameter binding
 		 */
 		public StoredProcedureParameterBind getParameterBind();
 
 		/**
 		 * Bind a value to the parameter.  How this value is bound to the underlying JDBC CallableStatement is
 		 * totally dependent on the Hibernate type.
 		 *
 		 * @param value The value to bind.
 		 */
 		public void bindValue(T value);
 
 		/**
 		 * Bind a value to the parameter, using just a specified portion of the DATE/TIME value.  It is illegal to call
 		 * this form if the parameter is not DATE/TIME type.  The Hibernate type is circumvented in this case and
 		 * an appropriate "precision" Type is used instead.
 		 *
 		 * @param value The value to bind
 		 * @param explicitTemporalType An explicitly supplied TemporalType.
 		 */
 		public void bindValue(T value, TemporalType explicitTemporalType);
 	}
 
 	/**
 	 * Describes an input value binding for any IN/INOUT parameters.
 	 */
 	public static interface StoredProcedureParameterBind<T> {
 		/**
 		 * Retrieves the bound value.
 		 *
 		 * @return The bound value.
 		 */
 		public T getValue();
 
 		/**
 		 * If {@code <T>} represents a DATE/TIME type value, JPA usually allows specifying the particular parts of
 		 * the DATE/TIME value to be bound.  This value represents the particular part the user requested to be bound.
 		 *
 		 * @return The explicitly supplied TemporalType.
 		 */
 		public TemporalType getExplicitTemporalType();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index 75d3c83074..650db2cfc8 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -469,1821 +469,1878 @@ public abstract class Dialect implements ConversionContext {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeNClob( original, target, session );
 			}
 		}
 	};
 
 	/**
 	 * Merge strategy based on creating a new LOB locator.
 	 */
 	protected static final LobMergeStrategy NEW_LOCATOR_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createBlob( ArrayHelper.EMPTY_BYTE_ARRAY )
 						: lobCreator.createBlob( original.getBinaryStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createClob( "" )
 						: lobCreator.createClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createNClob( "" )
 						: lobCreator.createNClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 			}
 		}
 	};
 
 	public LobMergeStrategy getLobMergeStrategy() {
 		return NEW_LOCATOR_LOB_MERGE_STRATEGY;
 	}
 
 
 	// hibernate type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated with the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} type code
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getHibernateTypeName(int code) throws HibernateException {
 		String result = hibernateTypeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No Hibernate type mapping for java.sql.Types code: " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated
 	 * with the given {@link java.sql.Types} typecode with the given storage
 	 * specification parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getHibernateTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					"No Hibernate type mapping for java.sql.Types code: " +
 					code +
 					", length: " +
 					length
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code and maximum column length.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, long capacity, String name) {
 		hibernateTypeNames.put( code, capacity, name);
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, String name) {
 		hibernateTypeNames.put( code, name);
 	}
 
 
 	// function support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerFunction(String name, SQLFunction function) {
 		sqlFunctions.put( name, function );
 	}
 
 	/**
 	 * Retrieves a map of the dialect's registered functions
 	 * (functionName => {@link org.hibernate.dialect.function.SQLFunction}).
 	 *
 	 * @return The map of registered functions.
 	 */
 	public final Map<String, SQLFunction> getFunctions() {
 		return sqlFunctions;
 	}
 
 
 	// keyword support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerKeyword(String word) {
 		sqlKeywords.add(word);
 	}
 
 	public Set<String> getKeywords() {
 		return sqlKeywords;
 	}
 
 
 	// native identifier generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The class (which implements {@link org.hibernate.id.IdentifierGenerator})
 	 * which acts as this dialects native generation strategy.
 	 * <p/>
 	 * Comes into play whenever the user specifies the native generator.
 	 *
 	 * @return The native generator class.
 	 */
 	public Class getNativeIdentifierGeneratorClass() {
 		if ( supportsIdentityColumns() ) {
 			return IdentityGenerator.class;
 		}
 		else if ( supportsSequences() ) {
 			return SequenceGenerator.class;
 		}
 		else {
 			return TableHiLoGenerator.class;
 		}
 	}
 
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support identity column key generation?
 	 *
 	 * @return True if IDENTITY columns are supported; false otherwise.
 	 */
 	public boolean supportsIdentityColumns() {
 		return false;
 	}
 
 	/**
 	 * Does the dialect support some form of inserting and selecting
 	 * the generated IDENTITY value all in the same statement.
 	 *
 	 * @return True if the dialect supports selecting the just
 	 * generated IDENTITY in the insert statement.
 	 */
 	public boolean supportsInsertSelectIdentity() {
 		return false;
 	}
 
 	/**
 	 * Whether this dialect have an Identity clause added to the data type or a
 	 * completely separate identity data type
 	 *
 	 * @return boolean
 	 */
 	public boolean hasDataTypeInIdentityColumn() {
 		return true;
 	}
 
 	/**
 	 * Provided we {@link #supportsInsertSelectIdentity}, then attach the
 	 * "select identity" clause to the  insert statement.
 	 *  <p/>
 	 * Note, if {@link #supportsInsertSelectIdentity} == false then
 	 * the insert-string should be returned without modification.
 	 *
 	 * @param insertString The insert command
 	 * @return The insert command with any necessary identity select
 	 * clause attached.
 	 */
 	public String appendIdentitySelectToInsert(String insertString) {
 		return insertString;
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value for a particular table
 	 *
 	 * @param table The table into which the insert was done
 	 * @param column The PK column.
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
 		return getIdentitySelectString();
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value.
 	 *
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentitySelectString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY of
 	 * a particular type.
 	 *
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentityColumnString(int type) throws MappingException {
 		return getIdentityColumnString();
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY.
 	 *
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentityColumnString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The keyword used to insert a generated value into an identity column (or null).
 	 * Need if the dialect does not support inserts that specify no column values.
 	 *
 	 * @return The appropriate keyword.
 	 */
 	public String getIdentityInsertString() {
 		return null;
 	}
 
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support sequences?
 	 *
 	 * @return True if sequences supported; false otherwise.
 	 */
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support "pooled" sequences.  Not aware of a better
 	 * name for this.  Essentially can we specify the initial and increment values?
 	 *
 	 * @return True if such "pooled" sequences are supported; false otherwise.
 	 * @see #getCreateSequenceStrings(String, int, int)
 	 * @see #getCreateSequenceString(String, int, int)
 	 */
 	public boolean supportsPooledSequences() {
 		return false;
 	}
 
 	/**
 	 * Generate the appropriate select statement to to retrieve the next value
 	 * of a sequence.
 	 * <p/>
 	 * This should be a "stand alone" select statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return String The "nextval" select string.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Generate the select expression fragment that will retrieve the next
 	 * value of a sequence as part of another (typically DML) statement.
 	 * <p/>
 	 * This differs from {@link #getSequenceNextValString(String)} in that this
 	 * should return an expression usable within another statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return The "nextval" fragment.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * The multiline script used to create a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 * @deprecated Use {@link #getCreateSequenceString(String, int, int)} instead
 	 */
 	@Deprecated
     public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName ) };
 	}
 
 	/**
 	 * An optional multi-line form for databases which {@link #supportsPooledSequences()}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getCreateSequenceStrings(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName, initialValue, incrementSize ) };
 	}
 
 	/**
 	 * Typically dialects which support sequences can create a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getCreateSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can create a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to create
 	 * a sequence should instead override {@link #getCreateSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Overloaded form of {@link #getCreateSequenceString(String)}, additionally
 	 * taking the initial value and increment size to be applied to the sequence
 	 * definition.
 	 * </p>
 	 * The default definition is to suffix {@link #getCreateSequenceString(String)}
 	 * with the string: " start with {initialValue} increment by {incrementSize}" where
 	 * {initialValue} and {incrementSize} are replacement placeholders.  Generally
 	 * dialects should only need to override this method if different key phrases
 	 * are used to apply the allocation information.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		if ( supportsPooledSequences() ) {
 			return getCreateSequenceString( sequenceName ) + " start with " + initialValue + " increment by " + incrementSize;
 		}
 		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
 	}
 
 	/**
 	 * The multiline script used to drop a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getDropSequenceStrings(String sequenceName) throws MappingException {
 		return new String[]{getDropSequenceString( sequenceName )};
 	}
 
 	/**
 	 * Typically dialects which support sequences can drop a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getDropSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can drop a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to drop
 	 * a sequence should instead override {@link #getDropSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getDropSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Get the select command used retrieve the names of all sequences.
 	 *
 	 * @return The select command; or null if sequences are not supported.
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 */
 	public String getQuerySequencesString() {
 		return null;
 	}
 
 
 	// GUID support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the command used to select a GUID from the underlying database.
 	 * <p/>
 	 * Optional operation.
 	 *
 	 * @return The appropriate command.
 	 */
 	public String getSelectGUIDString() {
 		throw new UnsupportedOperationException( getClass().getName() + " does not support GUIDs" );
 	}
 
 
 	// limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this dialect support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come before other parameters
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
 	 * of a total number of returned rows?
 	 * <p/>
 	 * This is easiest understood via an example.  Consider you have a table
 	 * with 20 rows, but you only want to retrieve rows number 11 through 20.
 	 * Generally, a limit with offset would say that the offset = 11 and the
 	 * limit = 10 (we only want 10 rows at a time); this is specifying the
 	 * total number of returned rows.  Some dialects require that we instead
 	 * specify offset = 11 and limit = 20, where 20 is the "last" row we want
 	 * relative to offset (i.e. total number of rows = 20 - 11 = 9)
 	 * <p/>
 	 * So essentially, is limit relative from offset?  Or is limit absolute?
 	 *
 	 * @return True if limit is relative from offset; false otherwise.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	/**
 	 * Generally, if there is no limit applied to a Hibernate query we do not apply any limits
 	 * to the SQL query.  This option forces that the limit be written to the SQL query.
 	 *
 	 * @return True to force limit into SQL query even if none specified in Hibernate query; false otherwise.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean forceLimitUsage() {
 		return false;
 	}
 
 	/**
 	 * Given a limit and an offset, apply the limit clause to the query.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param offset The offset of the limit
 	 * @param limit The limit of the limit ;)
 	 * @return The modified query statement with the limit applied.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public String getLimitString(String query, int offset, int limit) {
 		return getLimitString( query, ( offset > 0 || forceLimitUsage() )  );
 	}
 
 	/**
 	 * Apply s limit clause to the query.
 	 * <p/>
 	 * Typically dialects utilize {@link #supportsVariableLimit() variable}
 	 * limit clauses when they support limits.  Thus, when building the
 	 * select command we do not actually need to know the limit or the offest
 	 * since we will just be using placeholders.
 	 * <p/>
 	 * Here we do still pass along whether or not an offset was specified
 	 * so that dialects not supporting offsets can generate proper exceptions.
 	 * In general, dialects will override one or the other of this method and
 	 * {@link #getLimitString(String, int, int)}.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param hasOffset Is the query requesting an offset?
 	 * @return the modified SQL
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	protected String getLimitString(String query, boolean hasOffset) {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName());
 	}
 
 	/**
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
 	 * NOTE: what gets passed into {@link #getLimitString(String,int,int)} is the zero-based offset.  Dialects which
 	 * do not {@link #supportsVariableLimit} should take care to perform any needed first-row-conversion calls prior
 	 * to injecting the limit values into the SQL string.
 	 *
 	 * @param zeroBasedFirstResult The user-supplied, zero-based first row offset.
 	 * @return The corresponding db/dialect specific offset.
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 	/**
 	 * Build delegate managing LIMIT clause.
 	 *
 	 * @param sql SQL query.
 	 * @param selection Selection criteria. {@code null} in case of unlimited number of rows.
 	 * @return LIMIT clause delegate.
 	 */
 	public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
 		return new LegacyLimitHandler( this, sql, selection );
 	}
 
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Informational metadata about whether this dialect is known to support
 	 * specifying timeouts for requested lock acquisitions.
 	 *
 	 * @return True is this dialect supports specifying lock timeouts.
 	 */
 	public boolean supportsLockTimeouts() {
 		return true;
 
 	}
 
 	/**
 	 * If this dialect supports specifying lock timeouts, are those timeouts
 	 * rendered into the <tt>SQL</tt> string as parameters.  The implication
 	 * is that Hibernate will need to bind the timeout value as a parameter
 	 * in the {@link java.sql.PreparedStatement}.  If true, the param position
 	 * is always handled as the last parameter; if the dialect specifies the
 	 * lock timeout elsewhere in the <tt>SQL</tt> statement then the timeout
 	 * value should be directly rendered into the statement and this method
 	 * should return false.
 	 *
 	 * @return True if the lock timeout is rendered into the <tt>SQL</tt>
 	 * string as a parameter; false otherwise.
 	 */
 	public boolean isLockTimeoutParameterized() {
 		return false;
 	}
 
 	/**
 	 * Get a strategy instance which knows how to acquire a database-level lock
 	 * of the specified mode for this dialect.
 	 *
 	 * @param lockable The persister for the entity to be locked.
 	 * @param lockMode The type of lock to be acquired.
 	 * @return The appropriate locking strategy.
 	 * @since 3.2
 	 */
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
         switch ( lockMode ) {
             case PESSIMISTIC_FORCE_INCREMENT:
                 return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
             case PESSIMISTIC_WRITE:
                 return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
             case PESSIMISTIC_READ:
                 return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
             case OPTIMISTIC:
                 return new OptimisticLockingStrategy( lockable, lockMode );
             case OPTIMISTIC_FORCE_INCREMENT:
                 return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
             default:
                 return new SelectLockingStrategy( lockable, lockMode );
         }
 	}
 
 	/**
 	 * Given LockOptions (lockMode, timeout), determine the appropriate for update fragment to use.
 	 *
 	 * @param lockOptions contains the lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockOptions lockOptions) {
         LockMode lockMode = lockOptions.getLockMode();
         return getForUpdateString( lockMode, lockOptions.getTimeOut() );
 	}
 
     @SuppressWarnings( {"deprecation"})
 	private String getForUpdateString(LockMode lockMode, int timeout){
        switch ( lockMode ) {
             case UPGRADE:
                 return getForUpdateString();
             case PESSIMISTIC_READ:
                 return getReadLockString( timeout );
             case PESSIMISTIC_WRITE:
                 return getWriteLockString( timeout );
             case UPGRADE_NOWAIT:
             case FORCE:
             case PESSIMISTIC_FORCE_INCREMENT:
                 return getForUpdateNowaitString();
             default:
                 return "";
         }
     }
 
 	/**
 	 * Given a lock mode, determine the appropriate for update fragment to use.
 	 *
 	 * @param lockMode The lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockMode lockMode) {
 		return getForUpdateString( lockMode, LockOptions.WAIT_FOREVER );
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire locks
 	 * for this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE</tt> clause string.
 	 */
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getWriteLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getReadLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 
 	/**
 	 * Is <tt>FOR UPDATE OF</tt> syntax supported?
 	 *
 	 * @return True if the database supports <tt>FOR UPDATE OF</tt> syntax;
 	 * false otherwise.
 	 */
 	public boolean forUpdateOfColumns() {
 		// by default we report no support
 		return false;
 	}
 
 	/**
 	 * Does this dialect support <tt>FOR UPDATE</tt> in conjunction with
 	 * outer joined rows?
 	 *
 	 * @return True if outer joined rows can be locked via <tt>FOR UPDATE</tt>.
 	 */
 	public boolean supportsOuterJoinForUpdate() {
 		return true;
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	public String getForUpdateString(String aliases) {
 		// by default we simply return the getForUpdateString() result since
 		// the default is to say no support for "FOR UPDATE OF ..."
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @param lockOptions the lock options to apply
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		LockMode lockMode = lockOptions.getLockMode();
 		Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
 		while ( itr.hasNext() ) {
 			// seek the highest lock mode
 			final Map.Entry<String, LockMode>entry = itr.next();
 			final LockMode lm = entry.getValue();
 			if ( lm.greaterThan(lockMode) ) {
 				lockMode = lm;
 			}
 		}
 		lockOptions.setLockMode( lockMode );
 		return getForUpdateString( lockOptions );
 	}
 
 	/**
 	 * Retrieves the <tt>FOR UPDATE NOWAIT</tt> syntax specific to this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString() {
 		// by default we report no support for NOWAIT lock semantics
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list NOWAIT</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF colunm_list NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString( aliases );
 	}
 
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param mode The lock mode to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 * @deprecated use {@code appendLockHint(LockOptions,String)} instead
 	 */
 	@Deprecated
 	public String appendLockHint(LockMode mode, String tableName) {
 		return appendLockHint( new LockOptions( mode ), tableName );
 	}
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param lockOptions The lock options to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 */
 	public String appendLockHint(LockOptions lockOptions, String tableName){
 		return tableName;
 	}
 
 	/**
 	 * Modifies the given SQL by applying the appropriate updates for the specified
 	 * lock modes and key columns.
 	 * <p/>
 	 * The behavior here is that of an ANSI SQL <tt>SELECT FOR UPDATE</tt>.  This
 	 * method is really intended to allow dialects which do not support
 	 * <tt>SELECT FOR UPDATE</tt> to achieve this in their own fashion.
 	 *
 	 * @param sql the SQL string to modify
 	 * @param aliasedLockOptions lock options indexed by aliased table names.
 	 * @param keyColumnNames a map of key columns indexed by aliased table names.
 	 * @return the modified SQL string.
 	 */
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
 		return sql + new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString();
 	}
 
 
 	// table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Command used to create a table.
 	 *
 	 * @return The command used to create a table.
 	 */
 	public String getCreateTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Slight variation on {@link #getCreateTableString}.  Here, we have the
 	 * command used to create a table when there is no primary key and
 	 * duplicate rows are expected.
 	 * <p/>
 	 * Most databases do not care about the distinction; originally added for
 	 * Teradata support which does care.
 	 *
 	 * @return The command used to create a multiset table.
 	 */
 	public String getCreateMultisetTableString() {
 		return getCreateTableString();
 	}
 
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support temporary tables?
 	 *
 	 * @return True if temp tables are supported; false otherwise.
 	 */
 	public boolean supportsTemporaryTables() {
 		return false;
 	}
 
 	/**
 	 * Generate a temporary table name given the base table.
 	 *
 	 * @param baseTableName The table name from which to base the temp table name.
 	 * @return The generated temp table name.
 	 */
 	public String generateTemporaryTableName(String baseTableName) {
 		return "HT_" + baseTableName;
 	}
 
 	/**
 	 * Command used to create a temporary table.
 	 *
 	 * @return The command used to create a temporary table.
 	 */
 	public String getCreateTemporaryTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Get any fragments needing to be postfixed to the command for
 	 * temporary table creation.
 	 *
 	 * @return Any required postfix.
 	 */
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
 	/**
 	 * Command used to drop a temporary table.
 	 *
 	 * @return The command used to drop a temporary table.
 	 */
 	public String getDropTemporaryTableString() {
 		return "drop table";
 	}
 
 	/**
 	 * Does the dialect require that temporary table DDL statements occur in
 	 * isolation from other statements?  This would be the case if the creation
 	 * would cause any current transaction to get committed implicitly.
 	 * <p/>
 	 * JDBC defines a standard way to query for this information via the
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}
 	 * method.  However, that does not distinguish between temporary table
 	 * DDL and other forms of DDL; MySQL, for example, reports DDL causing a
 	 * transaction commit via its driver, even though that is not the case for
 	 * temporary table DDL.
 	 * <p/>
 	 * Possible return values and their meanings:<ul>
 	 * <li>{@link Boolean#TRUE} - Unequivocally, perform the temporary table DDL
 	 * in isolation.</li>
 	 * <li>{@link Boolean#FALSE} - Unequivocally, do <b>not</b> perform the
 	 * temporary table DDL in isolation.</li>
 	 * <li><i>null</i> - defer to the JDBC driver response in regards to
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}</li>
 	 * </ul>
 	 *
 	 * @return see the result matrix above.
 	 */
 	public Boolean performTemporaryTableDDLInIsolation() {
 		return null;
 	}
 
 	/**
 	 * Do we need to drop the temporary table after use?
 	 *
 	 * @return True if the table should be dropped.
 	 */
 	public boolean dropTemporaryTableAfterUse() {
 		return true;
 	}
 
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
-	 * Registers an OUT parameter which will be returning a
-	 * {@link java.sql.ResultSet}.  How this is accomplished varies greatly
-	 * from DB to DB, hence its inclusion (along with {@link #getResultSet}) here.
+	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
+	 * returning {@link java.sql.ResultSet} *by position*.  Pre-Java 8, registering such ResultSet-returning
+	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
-	 * @param position The bind position at which to register the OUT param.
+	 * @param position The bind position at which to register the output param.
+	 *
 	 * @return The number of (contiguous) bind positions used.
-	 * @throws SQLException Indicates problems registering the OUT param.
+	 *
+	 * @throws SQLException Indicates problems registering the param.
 	 */
 	public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 				" does not support resultsets via stored procedures"
 			);
 	}
 
 	/**
+	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
+	 * returning {@link java.sql.ResultSet} *by name*.  Pre-Java 8, registering such ResultSet-returning
+	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
+	 *
+	 * @param statement The callable statement.
+	 * @param name The parameter name (for drivers which support named parameters).
+	 *
+	 * @return The number of (contiguous) bind positions used.
+	 *
+	 * @throws SQLException Indicates problems registering the param.
+	 */
+	public int registerResultSetOutParameter(CallableStatement statement, String name) throws SQLException {
+		throw new UnsupportedOperationException(
+				getClass().getName() +
+						" does not support resultsets via stored procedures"
+		);
+	}
+
+	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @return The extracted result set.
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 				" does not support resultsets via stored procedures"
 			);
 	}
 
+	/**
+	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
+	 * extract the {@link java.sql.ResultSet}.
+	 *
+	 * @param statement The callable statement.
+	 * @param position The bind position at which to register the output param.
+	 *
+	 * @return The extracted result set.
+	 *
+	 * @throws SQLException Indicates problems extracting the result set.
+	 */
+	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
+		throw new UnsupportedOperationException(
+				getClass().getName() +
+						" does not support resultsets via stored procedures"
+		);
+	}
+
+	/**
+	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
+	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
+	 *
+	 * @param statement The callable statement.
+	 * @param name The parameter name (for drivers which support named parameters).
+	 *
+	 * @return The extracted result set.
+	 *
+	 * @throws SQLException Indicates problems extracting the result set.
+	 */
+	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
+		throw new UnsupportedOperationException(
+				getClass().getName() +
+						" does not support resultsets via stored procedures"
+		);
+	}
+
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support a way to retrieve the database's current
 	 * timestamp value?
 	 *
 	 * @return True if the current timestamp can be retrieved; false otherwise.
 	 */
 	public boolean supportsCurrentTimestampSelection() {
 		return false;
 	}
 
 	/**
 	 * Should the value returned by {@link #getCurrentTimestampSelectString}
 	 * be treated as callable.  Typically this indicates that JDBC escape
 	 * syntax is being used...
 	 *
 	 * @return True if the {@link #getCurrentTimestampSelectString} return
 	 * is callable; false otherwise.
 	 */
 	public boolean isCurrentTimestampSelectStringCallable() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * Retrieve the command used to retrieve the current timestamp from the
 	 * database.
 	 *
 	 * @return The command.
 	 */
 	public String getCurrentTimestampSelectString() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * The name of the database-specific SQL function for retrieving the
 	 * current timestamp.
 	 *
 	 * @return The function name.
 	 */
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 
 	// SQLException support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
 	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.
 	 * <p/>
 	 * The preferred method is to not override this method; if possible,
 	 * {@link #buildSQLExceptionConversionDelegate()} should be overridden
 	 * instead.
 	 *
 	 * If this method is not overridden, the default SQLExceptionConverter
 	 * implementation executes 3 SQLException converter delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>the vendor-specific delegate returned by {@link #buildSQLExceptionConversionDelegate()};
 	 *         (it is strongly recommended that specific Dialect implementations
 	 *         override {@link #buildSQLExceptionConversionDelegate()})</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * If this method is overridden, it is strongly recommended that the
 	 * returned {@link SQLExceptionConverter} interpret SQL errors based on
 	 * vendor-specific error codes rather than the SQLState since the
 	 * interpretation is more accurate when using vendor-specific ErrorCodes.
 	 *
 	 * @return The Dialect's preferred SQLExceptionConverter, or null to
 	 * indicate that the default {@link SQLExceptionConverter} should be used.
 	 *
 	 * @see {@link #buildSQLExceptionConversionDelegate()}
 	 * @deprecated {@link #buildSQLExceptionConversionDelegate()} should be
 	 * overridden instead.
 	 */
 	@Deprecated
 	public SQLExceptionConverter buildSQLExceptionConverter() {
 		return null;
 	}
 
 	/**
 	 * Build an instance of a {@link SQLExceptionConversionDelegate} for
 	 * interpreting dialect-specific error or SQLState codes.
 	 * <p/>
 	 * When {@link #buildSQLExceptionConverter} returns null, the default 
 	 * {@link SQLExceptionConverter} is used to interpret SQLState and
 	 * error codes. If this method is overridden to return a non-null value,
 	 * the default {@link SQLExceptionConverter} will use the returned
 	 * {@link SQLExceptionConversionDelegate} in addition to the following 
 	 * standard delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * It is strongly recommended that specific Dialect implementations override this
 	 * method, since interpretation of a SQL error is much more accurate when based on
 	 * the a vendor-specific ErrorCode rather than the SQLState.
 	 * <p/>
 	 * Specific Dialects may override to return whatever is most appropriate for that vendor.
 	 */
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return null;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			return null;
 		}
 	};
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	// union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Given a {@link java.sql.Types} type code, determine an appropriate
 	 * null value to use in a select clause.
 	 * <p/>
 	 * One thing to consider here is that certain databases might
 	 * require proper casting for the nulls here since the select here
 	 * will be part of a UNION/UNION ALL.
 	 *
 	 * @param sqlType The {@link java.sql.Types} type code.
 	 * @return The appropriate select clause value fragment.
 	 */
 	public String getSelectClauseNullString(int sqlType) {
 		return "null";
 	}
 
 	/**
 	 * Does this dialect support UNION ALL, which is generally a faster
 	 * variant of UNION?
 	 *
 	 * @return True if UNION ALL is supported; false otherwise.
 	 */
 	public boolean supportsUnionAll() {
 		return false;
 	}
 
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	/**
 	 * Create a {@link org.hibernate.sql.JoinFragment} strategy responsible
 	 * for handling this dialect's variations in how joins are handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.JoinFragment} strategy.
 	 */
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
 
 	/**
 	 * Create a {@link org.hibernate.sql.CaseFragment} strategy responsible
 	 * for handling this dialect's variations in how CASE statements are
 	 * handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.CaseFragment} strategy.
 	 */
 	public CaseFragment createCaseFragment() {
 		return new ANSICaseFragment();
 	}
 
 	/**
 	 * The fragment used to insert a row without specifying any column values.
 	 * This is not possible on some databases.
 	 *
 	 * @return The appropriate empty values clause.
 	 */
 	public String getNoColumnsInsertString() {
 		return "values ( )";
 	}
 
 	/**
 	 * The name of the SQL function that transforms a string to
 	 * lowercase
 	 *
 	 * @return The dialect-specific lowercase function.
 	 */
 	public String getLowercaseFunction() {
 		return "lower";
 	}
 
 	/**
 	 * The name of the SQL function that can do case insensitive <b>like</b> comparison.
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
 	 * @return {@code true} if the underlying Database supports case insensitive like comparison, {@code false} otherwise.
 	 * The default is {@code false}.
 	 */
 	public boolean supportsCaseInsensitiveLike(){
 		return false;
 	}
 
 	/**
 	 * Meant as a means for end users to affect the select strings being sent
 	 * to the database and perhaps manipulate them in some fashion.
 	 * <p/>
 	 * The recommend approach is to instead use
 	 * {@link org.hibernate.Interceptor#onPrepareStatement(String)}.
 	 *
 	 * @param select The select command
 	 * @return The mutated select command, or the same as was passed in.
 	 */
 	public String transformSelectString(String select) {
 		return select;
 	}
 
 	/**
 	 * What is the maximum length Hibernate can use for generated aliases?
 	 *
 	 * @return The maximum length.
 	 */
 	public int getMaxAliasLength() {
 		return 10;
 	}
 
 	/**
 	 * The SQL literal value to which this database maps boolean values.
 	 *
 	 * @param bool The boolean value
 	 * @return The appropriate SQL literal.
 	 */
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "1" : "0";
 	}
 
 
 	// identifier quoting support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The character specific to this dialect used to begin a quoted identifier.
 	 *
 	 * @return The dialect's specific open quote character.
 	 */
 	public char openQuote() {
 		return '"';
 	}
 
 	/**
 	 * The character specific to this dialect used to close a quoted identifier.
 	 *
 	 * @return The dialect's specific close quote character.
 	 */
 	public char closeQuote() {
 		return '"';
 	}
 
 	/**
 	 * Apply dialect-specific quoting.
 	 * <p/>
 	 * By default, the incoming value is checked to see if its first character
 	 * is the back-tick (`).  If so, the dialect specific quoting is applied.
 	 *
 	 * @param name The value to be quoted.
 	 * @return The quoted (or unmodified, if not starting with back-tick) value.
 	 * @see #openQuote()
 	 * @see #closeQuote()
 	 */
 	public final String quote(String name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		if ( name.charAt( 0 ) == '`' ) {
 			return openQuote() + name.substring( 1, name.length() - 1 ) + closeQuote();
 		}
 		else {
 			return name;
 		}
 	}
 
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
 	 *
 	 * @return True if we support altering of tables; false otherwise.
 	 */
 	public boolean hasAlterTable() {
 		return true;
 	}
 
 	/**
 	 * Do we need to drop constraints before dropping tables in this dialect?
 	 *
 	 * @return True if constraints must be dropped prior to dropping
 	 * the table; false otherwise.
 	 */
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	/**
 	 * Do we need to qualify index names with the schema name?
 	 *
 	 * @return boolean
 	 */
 	public boolean qualifyIndexName() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
 	 *
 	 * @return boolean
 	 */
 	public boolean supportsUnique() {
 		return true;
 	}
 
     /**
      * Does this dialect support adding Unique constraints via create and alter table ?
      * @return boolean
      */
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
 	    return true;
 	}
 
 	/**
 	 * The syntax used to add a column to a table (optional).
 	 *
 	 * @return The "add column" fragment.
 	 */
 	public String getAddColumnString() {
 		throw new UnsupportedOperationException( "No add column syntax supported by " + getClass().getName() );
 	}
 
 	public String getDropForeignKeyString() {
 		return " drop constraint ";
 	}
 
 	public String getTableTypeString() {
 		// grrr... for differentiation of mysql storage engines
 		return "";
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 *
 	 * @param constraintName The FK constraint name.
 	 * @param foreignKey The names of the columns comprising the FK
 	 * @param referencedTable The table referenced by the FK
 	 * @param primaryKey The explicit columns in the referencedTable referenced
 	 * by this FK.
 	 * @param referencesPrimaryKey if false, constraint should be
 	 * explicit about which column names the constraint refers to
 	 *
 	 * @return the "add FK" fragment
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( constraintName )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
 	/**
 	 * The syntax used to add a primary key constraint to a table.
 	 *
 	 * @param constraintName The name of the PK constraint.
 	 * @return The "add PK" fragment
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " primary key ";
 	}
 
     /**
      * The syntax used to add a unique constraint to a table.
      *
      * @param constraintName The name of the unique constraint.
      * @return The "add unique" fragment
      */
     public String getAddUniqueConstraintString(String constraintName) {
         return " add constraint " + constraintName + " unique ";
     }
 
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return false;
 	}
 
 	/**
 	 * The keyword used to specify a nullable column.
 	 *
 	 * @return String
 	 */
 	public String getNullColumnString() {
 		return "";
 	}
 
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	public String getTableComment(String comment) {
 		return "";
 	}
 
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support column-level check constraints?
 	 *
 	 * @return True if column-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsColumnCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support table-level check constraints?
 	 *
 	 * @return True if table-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsTableCheck() {
 		return true;
 	}
 
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	public boolean supportsNotNullUnique() {
 		return true;
 	}
 
 	/**
 	 * Completely optional cascading drop clause
 	 *
 	 * @return String
 	 */
 	public String getCascadeConstraintsString() {
 		return "";
 	}
 
 	/**
 	 * @return Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
 	 */
 	public String getCrossJoinSeparator() {
 		return " cross join ";
 	}
 
 	public ColumnAliasExtractor getColumnAliasExtractor() {
 		return ColumnAliasExtractor.COLUMN_LABEL_EXTRACTOR;
 	}
 
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support empty IN lists?
 	 * <p/>
 	 * For example, is [where XYZ in ()] a supported construct?
 	 *
 	 * @return True if empty in lists are supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsEmptyInList() {
 		return true;
 	}
 
 	/**
 	 * Are string comparisons implicitly case insensitive.
 	 * <p/>
 	 * In other words, does [where 'XYZ' = 'xyz'] resolve to true?
 	 *
 	 * @return True if comparisons are case insensitive.
 	 * @since 3.2
 	 */
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	/**
 	 * Is this dialect known to support what ANSI-SQL terms "row value
 	 * constructor" syntax; sometimes called tuple syntax.
 	 * <p/>
 	 * Basically, does it support syntax like
 	 * "... where (FIRST_NAME, LAST_NAME) = ('Steve', 'Ebersole') ...".
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntax() {
 		// return false here, as most databases do not properly support this construct...
 		return false;
 	}
 
 	/**
 	 * If the dialect supports {@link #supportsRowValueConstructorSyntax() row values},
 	 * does it offer such support in IN lists as well?
 	 * <p/>
 	 * For example, "... where (FIRST_NAME, LAST_NAME) IN ( (?, ?), (?, ?) ) ..."
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax in the IN list; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return false;
 	}
 
 	/**
 	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
 	 * {@link java.sql.PreparedStatement#setBinaryStream}).
 	 *
 	 * @return True if BLOBs and CLOBs should be bound using stream operations.
 	 * @since 3.2
 	 */
 	public boolean useInputStreamToInsertBlob() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support parameters within the <tt>SELECT</tt> clause of
 	 * <tt>INSERT ... SELECT ...</tt> statements?
 	 *
 	 * @return True if this is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect require that references to result variables
 	 * (i.e, select expresssion aliases) in an ORDER BY clause be
 	 * replaced by column positions (1-origin) as defined
 	 * by the select clause?
 
 	 * @return true if result variable references in the ORDER BY
 	 *              clause should be replaced by column positions;
 	 *         false otherwise.
 	 */
 	public boolean replaceResultVariableInOrderByClauseWithPosition() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect require that parameters appearing in the <tt>SELECT</tt> clause be wrapped in <tt>cast()</tt>
 	 * calls to tell the db parser the expected type.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support asking the result set its positioning
 	 * information on forward only cursors.  Specifically, in the case of
 	 * scrolling fetches, Hibernate needs to use
 	 * {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst}.  Certain drivers do not
 	 * allow access to these methods for forward only cursors.
 	 * <p/>
 	 * NOTE : this is highly driver dependent!
 	 *
 	 * @return True if methods like {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst} are supported for forward
 	 * only cursors; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support definition of cascade delete constraints
 	 * which can cause circular chains?
 	 *
 	 * @return True if circular cascade delete constraints are supported; false
 	 * otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return true;
 	}
 
 	/**
 	 * Are subselects supported as the left-hand-side (LHS) of
 	 * IN-predicates.
 	 * <p/>
 	 * In other words, is syntax like "... <subquery> IN (1, 2, 3) ..." supported?
 	 *
 	 * @return True if subselects can appear as the LHS of an in-predicate;
 	 * false otherwise.
 	 * @since 3.2
 	 */
 	public boolean  supportsSubselectAsInPredicateLHS() {
 		return true;
 	}
 
 	/**
 	 * Expected LOB usage pattern is such that I can perform an insert
 	 * via prepared statement with a parameter binding for a LOB value
 	 * without crazy casting to JDBC driver implementation-specific classes...
 	 * <p/>
 	 * Part of the trickiness here is the fact that this is largely
 	 * driver dependent.  For example, Oracle (which is notoriously bad with
 	 * LOB support in their drivers historically) actually does a pretty good
 	 * job with LOB support as of the 10.2.x versions of their drivers...
 	 *
 	 * @return True if normal LOB usage patterns can be used with this driver;
 	 * false if driver-specific hookiness needs to be applied.
 	 * @since 3.2
 	 */
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support propagating changes to LOB
 	 * values back to the database?  Talking about mutating the
 	 * internal value of the locator as opposed to supplying a new
 	 * locator instance...
 	 * <p/>
 	 * For BLOBs, the internal value might be changed by:
 	 * {@link java.sql.Blob#setBinaryStream},
 	 * {@link java.sql.Blob#setBytes(long, byte[])},
 	 * {@link java.sql.Blob#setBytes(long, byte[], int, int)},
 	 * or {@link java.sql.Blob#truncate(long)}.
 	 * <p/>
 	 * For CLOBs, the internal value might be changed by:
 	 * {@link java.sql.Clob#setAsciiStream(long)},
 	 * {@link java.sql.Clob#setCharacterStream(long)},
 	 * {@link java.sql.Clob#setString(long, String)},
 	 * {@link java.sql.Clob#setString(long, String, int, int)},
 	 * or {@link java.sql.Clob#truncate(long)}.
 	 * <p/>
 	 * NOTE : I do not know the correct answer currently for
 	 * databases which (1) are not part of the cruise control process
 	 * or (2) do not {@link #supportsExpectedLobUsagePattern}.
 	 *
 	 * @return True if the changes are propagated back to the
 	 * database; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsLobValueChangePropogation() {
 		// todo : pretty sure this is the same as the java.sql.DatabaseMetaData.locatorsUpdateCopy method added in JDBC 4, see HHH-6046
 		return true;
 	}
 
 	/**
 	 * Is it supported to materialize a LOB locator outside the transaction in
 	 * which it was created?
 	 * <p/>
 	 * Again, part of the trickiness here is the fact that this is largely
 	 * driver dependent.
 	 * <p/>
 	 * NOTE: all database I have tested which {@link #supportsExpectedLobUsagePattern()}
 	 * also support the ability to materialize a LOB outside the owning transaction...
 	 *
 	 * @return True if unbounded materialization is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support referencing the table being mutated in
 	 * a subquery.  The "table being mutated" is the table referenced in
 	 * an UPDATE or a DELETE query.  And so can that table then be
 	 * referenced in a subquery of said UPDATE/DELETE query.
 	 * <p/>
 	 * For example, would the following two syntaxes be supported:<ul>
 	 * <li>delete from TABLE_A where ID not in ( select ID from TABLE_A )</li>
 	 * <li>update TABLE_A set NON_ID = 'something' where ID in ( select ID from TABLE_A)</li>
 	 * </ul>
 	 *
 	 * @return True if this dialect allows references the mutating table from
 	 * a subquery.
 	 */
 	public boolean supportsSubqueryOnMutatingTable() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support an exists statement in the select clause?
 	 *
 	 * @return True if exists checks are allowed in the select clause; false otherwise.
 	 */
 	public boolean supportsExistsInSelect() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return false;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support using a JDBC bind parameter as an argument
 	 * to a function or procedure call?
 	 *
 	 * @return Returns {@code true} if the database supports accepting bind params as args, {@code false} otherwise. The
 	 * default is {@code true}.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean supportsBindAsCallableArgument() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support `count(a,b)`?
 	 *
 	 * @return True if the database supports counting tuples; false otherwise.
 	 */
 	public boolean supportsTupleCounts() {
 		return false;
 	}
 
     /**
      * Does this dialect support `count(distinct a,b)`?
      *
      * @return True if the database supports counting distinct tuples; false otherwise.
      */
 	public boolean supportsTupleDistinctCounts() {
 		// oddly most database in fact seem to, so true is the default.
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
index 4c69d5c7bf..ef9b9f85c8 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
@@ -1,446 +1,469 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.engine.jdbc.internal;
 
 import java.lang.reflect.InvocationTargetException;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
 import org.hibernate.engine.jdbc.spi.SchemaNameResolver;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.exception.internal.SQLExceptionTypeDelegate;
 import org.hibernate.exception.internal.SQLStateConversionDelegate;
 import org.hibernate.exception.internal.StandardSQLExceptionConverter;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
+import org.hibernate.service.jdbc.cursor.internal.StandardRefCursorSupport;
 import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * Standard implementation of the {@link JdbcServices} contract
  *
  * @author Steve Ebersole
  */
 public class JdbcServicesImpl implements JdbcServices, ServiceRegistryAwareService, Configurable {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JdbcServicesImpl.class.getName());
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	private Dialect dialect;
 	private ConnectionProvider connectionProvider;
 	private SqlStatementLogger sqlStatementLogger;
 	private SqlExceptionHelper sqlExceptionHelper;
 	private ExtractedDatabaseMetaData extractedMetaDataSupport;
 	private LobCreatorBuilder lobCreatorBuilder;
 
 	@Override
 	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	public void configure(Map configValues) {
 		final JdbcConnectionAccess jdbcConnectionAccess = buildJdbcConnectionAccess( configValues );
 		final DialectFactory dialectFactory = serviceRegistry.getService( DialectFactory.class );
 
 		Dialect dialect = null;
 		LobCreatorBuilder lobCreatorBuilder = null;
 
+		boolean metaSupportsRefCursors = false;
+		boolean metaSupportsNamedParams = false;
 		boolean metaSupportsScrollable = false;
 		boolean metaSupportsGetGeneratedKeys = false;
 		boolean metaSupportsBatchUpdates = false;
 		boolean metaReportsDDLCausesTxnCommit = false;
 		boolean metaReportsDDLInTxnSupported = true;
 		String extraKeywordsString = "";
 		int sqlStateType = -1;
 		boolean lobLocatorUpdateCopy = false;
 		String catalogName = null;
 		String schemaName = null;
 		LinkedHashSet<TypeInfo> typeInfoSet = new LinkedHashSet<TypeInfo>();
 
 		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
 		// The need for it is intended to be alleviated with future development, thus it is
 		// not defined as an Environment constant...
 		//
 		// it is used to control whether we should consult the JDBC metadata to determine
 		// certain Settings default values; it is useful to *not* do this when the database
 		// may not be available (mainly in tools usage).
 		boolean useJdbcMetadata = ConfigurationHelper.getBoolean( "hibernate.temp.use_jdbc_metadata_defaults", configValues, true );
 		if ( useJdbcMetadata ) {
 			try {
 				Connection connection = jdbcConnectionAccess.obtainConnection();
 				try {
 					DatabaseMetaData meta = connection.getMetaData();
 					if(LOG.isDebugEnabled()) {
 						LOG.debugf( "Database ->\n" + "       name : %s\n" + "    version : %s\n" + "      major : %s\n" + "      minor : %s",
 									meta.getDatabaseProductName(),
 									meta.getDatabaseProductVersion(),
 									meta.getDatabaseMajorVersion(),
 									meta.getDatabaseMinorVersion()
 						);
 						LOG.debugf( "Driver ->\n" + "       name : %s\n" + "    version : %s\n" + "      major : %s\n" + "      minor : %s",
 									meta.getDriverName(),
 									meta.getDriverVersion(),
 									meta.getDriverMajorVersion(),
 									meta.getDriverMinorVersion()
 						);
 						LOG.debugf( "JDBC version : %s.%s", meta.getJDBCMajorVersion(), meta.getJDBCMinorVersion() );
 					}
 
+					metaSupportsRefCursors = StandardRefCursorSupport.supportsRefCursors( meta );
+					metaSupportsNamedParams = meta.supportsNamedParameters();
 					metaSupportsScrollable = meta.supportsResultSetType( ResultSet.TYPE_SCROLL_INSENSITIVE );
 					metaSupportsBatchUpdates = meta.supportsBatchUpdates();
 					metaReportsDDLCausesTxnCommit = meta.dataDefinitionCausesTransactionCommit();
 					metaReportsDDLInTxnSupported = !meta.dataDefinitionIgnoredInTransactions();
 					metaSupportsGetGeneratedKeys = meta.supportsGetGeneratedKeys();
 					extraKeywordsString = meta.getSQLKeywords();
 					sqlStateType = meta.getSQLStateType();
 					lobLocatorUpdateCopy = meta.locatorsUpdateCopy();
 					typeInfoSet.addAll( TypeInfoExtracter.extractTypeInfo( meta ) );
 
 					dialect = dialectFactory.buildDialect( configValues, connection );
 
 					catalogName = connection.getCatalog();
 					SchemaNameResolver schemaNameResolver = determineExplicitSchemaNameResolver( configValues );
 					if ( schemaNameResolver == null ) {
 // todo : add dialect method
 //						schemaNameResolver = dialect.getSchemaNameResolver();
 					}
 					if ( schemaNameResolver != null ) {
 						schemaName = schemaNameResolver.resolveSchemaName( connection );
 					}
 					lobCreatorBuilder = new LobCreatorBuilder( configValues, connection );
 				}
 				catch ( SQLException sqle ) {
 					LOG.unableToObtainConnectionMetadata( sqle.getMessage() );
 				}
 				finally {
 					if ( connection != null ) {
 						jdbcConnectionAccess.releaseConnection( connection );
 					}
 				}
 			}
 			catch ( SQLException sqle ) {
 				LOG.unableToObtainConnectionToQueryMetadata( sqle.getMessage() );
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 			catch ( UnsupportedOperationException uoe ) {
 				// user supplied JDBC connections
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 		}
 		else {
 			dialect = dialectFactory.buildDialect( configValues, null );
 		}
 
 		final boolean showSQL = ConfigurationHelper.getBoolean( Environment.SHOW_SQL, configValues, false );
 		final boolean formatSQL = ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, configValues, false );
 
 		this.dialect = dialect;
 		this.lobCreatorBuilder = (
 				lobCreatorBuilder == null ?
 						new LobCreatorBuilder( configValues, null ) :
 						lobCreatorBuilder
 		);
 
 		this.sqlStatementLogger =  new SqlStatementLogger( showSQL, formatSQL );
 
 		this.extractedMetaDataSupport = new ExtractedDatabaseMetaDataImpl(
+				metaSupportsRefCursors,
+				metaSupportsNamedParams,
 				metaSupportsScrollable,
 				metaSupportsGetGeneratedKeys,
 				metaSupportsBatchUpdates,
 				metaReportsDDLInTxnSupported,
 				metaReportsDDLCausesTxnCommit,
 				parseKeywords( extraKeywordsString ),
 				parseSQLStateType( sqlStateType ),
 				lobLocatorUpdateCopy,
 				schemaName,
 				catalogName,
 				typeInfoSet
 		);
 
 		SQLExceptionConverter sqlExceptionConverter = dialect.buildSQLExceptionConverter();
 		if ( sqlExceptionConverter == null ) {
 			final StandardSQLExceptionConverter converter = new StandardSQLExceptionConverter();
 			sqlExceptionConverter = converter;
 			converter.addDelegate( dialect.buildSQLExceptionConversionDelegate() );
 			converter.addDelegate( new SQLExceptionTypeDelegate( dialect ) );
 			// todo : vary this based on extractedMetaDataSupport.getSqlStateType()
 			converter.addDelegate( new SQLStateConversionDelegate( dialect ) );
 		}
 		this.sqlExceptionHelper = new SqlExceptionHelper( sqlExceptionConverter );
 	}
 
 	private JdbcConnectionAccess buildJdbcConnectionAccess(Map configValues) {
 		final MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( configValues );
 
 		if ( MultiTenancyStrategy.NONE == multiTenancyStrategy ) {
 			connectionProvider = serviceRegistry.getService( ConnectionProvider.class );
 			return new ConnectionProviderJdbcConnectionAccess( connectionProvider );
 		}
 		else {
 			connectionProvider = null;
 			final MultiTenantConnectionProvider multiTenantConnectionProvider = serviceRegistry.getService( MultiTenantConnectionProvider.class );
 			return new MultiTenantConnectionProviderJdbcConnectionAccess( multiTenantConnectionProvider );
 		}
 	}
 
 	private static class ConnectionProviderJdbcConnectionAccess implements JdbcConnectionAccess {
 		private final ConnectionProvider connectionProvider;
 
 		public ConnectionProviderJdbcConnectionAccess(ConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.closeConnection( connection );
 		}
 	}
 
 	private static class MultiTenantConnectionProviderJdbcConnectionAccess implements JdbcConnectionAccess {
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		public MultiTenantConnectionProviderJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getAnyConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.releaseAnyConnection( connection );
 		}
 	}
 
 
 	// todo : add to Environment
 	public static final String SCHEMA_NAME_RESOLVER = "hibernate.schema_name_resolver";
 
 	private SchemaNameResolver determineExplicitSchemaNameResolver(Map configValues) {
 		Object setting = configValues.get( SCHEMA_NAME_RESOLVER );
 		if ( SchemaNameResolver.class.isInstance( setting ) ) {
 			return (SchemaNameResolver) setting;
 		}
 
 		String resolverClassName = (String) setting;
 		if ( resolverClassName != null ) {
 			try {
 				Class resolverClass = ReflectHelper.classForName( resolverClassName, getClass() );
 				return (SchemaNameResolver) ReflectHelper.getDefaultConstructor( resolverClass ).newInstance();
 			}
 			catch ( ClassNotFoundException e ) {
 				LOG.unableToLocateConfiguredSchemaNameResolver( resolverClassName, e.toString() );
 			}
 			catch ( InvocationTargetException e ) {
 				LOG.unableToInstantiateConfiguredSchemaNameResolver( resolverClassName, e.getTargetException().toString() );
 			}
 			catch ( Exception e ) {
 				LOG.unableToInstantiateConfiguredSchemaNameResolver( resolverClassName, e.toString() );
 			}
 		}
 		return null;
 	}
 
 	private Set<String> parseKeywords(String extraKeywordsString) {
 		Set<String> keywordSet = new HashSet<String>();
 		keywordSet.addAll( Arrays.asList( extraKeywordsString.split( "," ) ) );
 		return keywordSet;
 	}
 
 	private ExtractedDatabaseMetaData.SQLStateType parseSQLStateType(int sqlStateType) {
 		switch ( sqlStateType ) {
 			case DatabaseMetaData.sqlStateSQL99 : {
 				return ExtractedDatabaseMetaData.SQLStateType.SQL99;
 			}
 			case DatabaseMetaData.sqlStateXOpen : {
 				return ExtractedDatabaseMetaData.SQLStateType.XOpen;
 			}
 			default : {
 				return ExtractedDatabaseMetaData.SQLStateType.UNKOWN;
 			}
 		}
 	}
 
 	private static class ExtractedDatabaseMetaDataImpl implements ExtractedDatabaseMetaData {
+		private final boolean supportsRefCursors;
+		private final boolean supportsNamedParameters;
 		private final boolean supportsScrollableResults;
 		private final boolean supportsGetGeneratedKeys;
 		private final boolean supportsBatchUpdates;
 		private final boolean supportsDataDefinitionInTransaction;
 		private final boolean doesDataDefinitionCauseTransactionCommit;
 		private final Set<String> extraKeywords;
 		private final SQLStateType sqlStateType;
 		private final boolean lobLocatorUpdateCopy;
 		private final String connectionSchemaName;
 		private final String connectionCatalogName;
 		private final LinkedHashSet<TypeInfo> typeInfoSet;
 
 		private ExtractedDatabaseMetaDataImpl(
+				boolean supportsRefCursors,
+				boolean supportsNamedParameters,
 				boolean supportsScrollableResults,
 				boolean supportsGetGeneratedKeys,
 				boolean supportsBatchUpdates,
 				boolean supportsDataDefinitionInTransaction,
 				boolean doesDataDefinitionCauseTransactionCommit,
 				Set<String> extraKeywords,
 				SQLStateType sqlStateType,
 				boolean lobLocatorUpdateCopy,
 				String connectionSchemaName,
 				String connectionCatalogName,
 				LinkedHashSet<TypeInfo> typeInfoSet) {
+			this.supportsRefCursors = supportsRefCursors;
+			this.supportsNamedParameters = supportsNamedParameters;
 			this.supportsScrollableResults = supportsScrollableResults;
 			this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
 			this.supportsBatchUpdates = supportsBatchUpdates;
 			this.supportsDataDefinitionInTransaction = supportsDataDefinitionInTransaction;
 			this.doesDataDefinitionCauseTransactionCommit = doesDataDefinitionCauseTransactionCommit;
 			this.extraKeywords = extraKeywords;
 			this.sqlStateType = sqlStateType;
 			this.lobLocatorUpdateCopy = lobLocatorUpdateCopy;
 			this.connectionSchemaName = connectionSchemaName;
 			this.connectionCatalogName = connectionCatalogName;
 			this.typeInfoSet = typeInfoSet;
 		}
 
 		@Override
+		public boolean supportsRefCursors() {
+			return supportsRefCursors;
+		}
+
+		@Override
+		public boolean supportsNamedParameters() {
+			return supportsNamedParameters;
+		}
+
+		@Override
 		public boolean supportsScrollableResults() {
 			return supportsScrollableResults;
 		}
 
 		@Override
 		public boolean supportsGetGeneratedKeys() {
 			return supportsGetGeneratedKeys;
 		}
 
 		@Override
 		public boolean supportsBatchUpdates() {
 			return supportsBatchUpdates;
 		}
 
 		@Override
 		public boolean supportsDataDefinitionInTransaction() {
 			return supportsDataDefinitionInTransaction;
 		}
 
 		@Override
 		public boolean doesDataDefinitionCauseTransactionCommit() {
 			return doesDataDefinitionCauseTransactionCommit;
 		}
 
 		@Override
 		public Set<String> getExtraKeywords() {
 			return extraKeywords;
 		}
 
 		@Override
 		public SQLStateType getSqlStateType() {
 			return sqlStateType;
 		}
 
 		@Override
 		public boolean doesLobLocatorUpdateCopy() {
 			return lobLocatorUpdateCopy;
 		}
 
 		@Override
 		public String getConnectionSchemaName() {
 			return connectionSchemaName;
 		}
 
 		@Override
 		public String getConnectionCatalogName() {
 			return connectionCatalogName;
 		}
 
 		@Override
 		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
 			return typeInfoSet;
 		}
 	}
 
 	@Override
 	public ConnectionProvider getConnectionProvider() {
 		return connectionProvider;
 	}
 
 	@Override
 	public SqlStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
 	@Override
 	public SqlExceptionHelper getSqlExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	@Override
 	public Dialect getDialect() {
 		return dialect;
 	}
 
 	@Override
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
 		return extractedMetaDataSupport;
 	}
 
 	@Override
 	public LobCreator getLobCreator(LobCreationContext lobCreationContext) {
 		return lobCreatorBuilder.buildLobCreator( lobCreationContext );
 	}
 
 	@Override
 	public ResultSetWrapper getResultSetWrapper() {
 		return ResultSetWrapperImpl.INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java
index 6d61608fc8..df2b6c699f 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java
@@ -1,144 +1,160 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.engine.jdbc.spi;
 
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 import org.hibernate.engine.jdbc.internal.TypeInfo;
 
 /**
  * Information extracted from {@link java.sql.DatabaseMetaData} regarding what the JDBC driver reports as
  * being supported or not.  Obviously {@link java.sql.DatabaseMetaData} reports many things, these are a few in
  * which we have particular interest.
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public interface ExtractedDatabaseMetaData {
 
 	public enum SQLStateType {
 		XOpen,
 		SQL99,
 		UNKOWN
 	}
 
 	/**
+	 * Does the driver report supporting named parameters?
+	 *
+	 * @return {@code true} indicates the driver reported true; {@code false} indicates the driver reported false
+	 * or that the driver could not be asked.
+	 */
+	public boolean supportsNamedParameters();
+
+	/**
+	 * Does the driver report supporting REF_CURSORs?
+	 *
+	 * @return {@code true} indicates the driver reported true; {@code false} indicates the driver reported false
+	 * or that the driver could not be asked.
+	 */
+	public boolean supportsRefCursors();
+
+	/**
 	 * Did the driver report to supporting scrollable result sets?
 	 *
 	 * @return True if the driver reported to support {@link java.sql.ResultSet#TYPE_SCROLL_INSENSITIVE}.
 	 *
 	 * @see java.sql.DatabaseMetaData#supportsResultSetType
 	 */
 	public boolean supportsScrollableResults();
 
 	/**
 	 * Did the driver report to supporting retrieval of generated keys?
 	 *
 	 * @return True if the if the driver reported to support calls to {@link java.sql.Statement#getGeneratedKeys}
 	 *
 	 * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys
 	 */
 	public boolean supportsGetGeneratedKeys();
 
 	/**
 	 * Did the driver report to supporting batched updates?
 	 *
 	 * @return True if the driver supports batched updates
 	 *
 	 * @see java.sql.DatabaseMetaData#supportsBatchUpdates
 	 */
 	public boolean supportsBatchUpdates();
 
 	/**
 	 * Did the driver report to support performing DDL within transactions?
 	 *
 	 * @return True if the drivers supports DDL statements within transactions.
 	 *
 	 * @see java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions
 	 */
 	public boolean supportsDataDefinitionInTransaction();
 
 	/**
 	 * Did the driver report to DDL statements performed within a transaction performing an implicit commit of the
 	 * transaction.
 	 *
 	 * @return True if the driver/database performs an implicit commit of transaction when DDL statement is
 	 * performed
 	 *
 	 * @see java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
 	 */
 	public boolean doesDataDefinitionCauseTransactionCommit();
 
 	/**
 	 * Get the list of extra keywords (beyond standard SQL92 keywords) reported by the driver.
 	 *
 	 * @return The extra keywords used by this database.
 	 *
 	 * @see java.sql.DatabaseMetaData#getSQLKeywords()
 	 */
 	public Set<String> getExtraKeywords();
 
 	/**
 	 * Retrieve the type of codes the driver says it uses for {@code SQLState}.  They might follow either
 	 * the X/Open standard or the SQL92 standard.
 	 *
 	 * @return The SQLState strategy reportedly used by this driver/database.
 	 *
 	 * @see java.sql.DatabaseMetaData#getSQLStateType()
 	 */
 	public SQLStateType getSqlStateType();
 
 	/**
 	 * Did the driver report that updates to a LOB locator affect a copy of the LOB?
 	 *
 	 * @return True if updates to the state of a LOB locator update only a copy.
 	 *
 	 * @see java.sql.DatabaseMetaData#locatorsUpdateCopy()
 	 */
 	public boolean doesLobLocatorUpdateCopy();
 
 	/**
 	 * Retrieve the name of the schema in effect when we connected to the database.
 	 *
 	 * @return The schema name
 	 */
 	public String getConnectionSchemaName();
 
 	/**
 	 * Retrieve the name of the catalog in effect when we connected to the database.
 	 *
 	 * @return The catalog name
 	 */
 	public String getConnectionCatalogName();
 
 	/**
 	 * Set of type info reported by the driver.
 	 *
 	 * @return The type information obtained from the driver.
 	 *
 	 * @see java.sql.DatabaseMetaData#getTypeInfo()
 	 */
 	public LinkedHashSet<TypeInfo> getTypeInfoSet();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureCallImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureCallImpl.java
index ee93509086..b56c2a549d 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureCallImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureCallImpl.java
@@ -1,555 +1,608 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.internal;
 
 import javax.persistence.ParameterMode;
 import javax.persistence.TemporalType;
 import java.sql.CallableStatement;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Date;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.StoredProcedureCall;
 import org.hibernate.StoredProcedureOutputs;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.ResultSetMappingDefinition;
+import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.service.jdbc.cursor.spi.RefCursorSupport;
 import org.hibernate.type.DateType;
 import org.hibernate.type.ProcedureParameterExtractionAware;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public class StoredProcedureCallImpl extends AbstractBasicQueryContractImpl implements StoredProcedureCall {
 	private static final Logger log = Logger.getLogger( StoredProcedureCallImpl.class );
 
 	private final String procedureName;
 	private final NativeSQLQueryReturn[] queryReturns;
 
 	private TypeOfParameter typeOfParameters = TypeOfParameter.UNKNOWN;
 	private List<StoredProcedureParameterImplementor> registeredParameters = new ArrayList<StoredProcedureParameterImplementor>();
 
 	private Set<String> synchronizedQuerySpaces;
 
 	@SuppressWarnings("unchecked")
 	public StoredProcedureCallImpl(SessionImplementor session, String procedureName) {
 		this( session, procedureName, (List) null );
 	}
 
 	public StoredProcedureCallImpl(SessionImplementor session, String procedureName, List<NativeSQLQueryReturn> queryReturns) {
 		super( session );
 		this.procedureName = procedureName;
 
 		if ( queryReturns == null || queryReturns.isEmpty() ) {
 			this.queryReturns = new NativeSQLQueryReturn[0];
 		}
 		else {
 			this.queryReturns = queryReturns.toArray( new NativeSQLQueryReturn[ queryReturns.size() ] );
 		}
 	}
 
 	public StoredProcedureCallImpl(SessionImplementor session, String procedureName, Class... resultClasses) {
 		this( session, procedureName, collectQueryReturns( resultClasses ) );
 	}
 
 	private static List<NativeSQLQueryReturn> collectQueryReturns(Class[] resultClasses) {
 		if ( resultClasses == null || resultClasses.length == 0 ) {
 			return null;
 		}
 
 		List<NativeSQLQueryReturn> queryReturns = new ArrayList<NativeSQLQueryReturn>( resultClasses.length );
 		int i = 1;
 		for ( Class resultClass : resultClasses ) {
 			queryReturns.add( new NativeSQLQueryRootReturn( "alias" + i, resultClass.getName(), LockMode.READ ) );
 			i++;
 		}
 		return queryReturns;
 	}
 
 	public StoredProcedureCallImpl(SessionImplementor session, String procedureName, String... resultSetMappings) {
 		this( session, procedureName, collectQueryReturns( session, resultSetMappings ) );
 	}
 
 	private static List<NativeSQLQueryReturn> collectQueryReturns(SessionImplementor session, String[] resultSetMappings) {
 		if ( resultSetMappings == null || resultSetMappings.length == 0 ) {
 			return null;
 		}
 
 		List<NativeSQLQueryReturn> queryReturns = new ArrayList<NativeSQLQueryReturn>( resultSetMappings.length );
 		for ( String resultSetMapping : resultSetMappings ) {
 			ResultSetMappingDefinition mapping = session.getFactory().getResultSetMapping( resultSetMapping );
 			if ( mapping == null ) {
 				throw new MappingException( "Unknown SqlResultSetMapping [" + resultSetMapping + "]" );
 			}
 			queryReturns.addAll( Arrays.asList( mapping.getQueryReturns() ) );
 		}
 		return queryReturns;
 	}
 
 //	public StoredProcedureCallImpl(
 //			SessionImplementor session,
 //			String procedureName,
 //			List<StoredProcedureParameter> parameters) {
 //		// this form is intended for named stored procedure calls.
 //		// todo : introduce a NamedProcedureCallDefinition object to hold all needed info and pass that in here; will help with EM.addNamedQuery as well..
 //		this( session, procedureName );
 //		for ( StoredProcedureParameter parameter : parameters ) {
 //			registerParameter( (StoredProcedureParameterImplementor) parameter );
 //		}
 //	}
 
 	@Override
 	public String getProcedureName() {
 		return procedureName;
 	}
 
 	NativeSQLQueryReturn[] getQueryReturns() {
 		return queryReturns;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public StoredProcedureCall registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
 		registerParameter( new PositionalStoredProcedureParameter( this, position, mode, type ) );
 		return this;
 	}
 
 	private void registerParameter(StoredProcedureParameterImplementor parameter) {
 		if ( StringHelper.isNotEmpty( parameter.getName() ) ) {
-			if ( typeOfParameters == TypeOfParameter.POSITIONAL ) {
-				throw new QueryException( "Cannot mix named and positional parameters" );
-			}
-			typeOfParameters = TypeOfParameter.NAMED;
-			registeredParameters.add( parameter );
+			prepareForNamedParameters();
 		}
 		else if ( parameter.getPosition() != null ) {
-			if ( typeOfParameters == TypeOfParameter.NAMED ) {
-				throw new QueryException( "Cannot mix named and positional parameters" );
-			}
-			typeOfParameters = TypeOfParameter.POSITIONAL;
-			registeredParameters.add( parameter.getPosition(), parameter );
+			prepareForPositionalParameters();
 		}
 		else {
 			throw new IllegalArgumentException( "Given parameter did not define name nor position [" + parameter + "]" );
 		}
+		registeredParameters.add( parameter );
+	}
+
+	private void prepareForPositionalParameters() {
+		if ( typeOfParameters == TypeOfParameter.NAMED ) {
+			throw new QueryException( "Cannot mix named and positional parameters" );
+		}
+		typeOfParameters = TypeOfParameter.POSITIONAL;
+	}
+
+	private void prepareForNamedParameters() {
+		if ( typeOfParameters == TypeOfParameter.POSITIONAL ) {
+			throw new QueryException( "Cannot mix named and positional parameters" );
+		}
+		if ( typeOfParameters == null ) {
+			// protect to only do this check once
+			final ExtractedDatabaseMetaData databaseMetaData = session().getTransactionCoordinator()
+					.getJdbcCoordinator()
+					.getLogicalConnection()
+					.getJdbcServices()
+					.getExtractedMetaDataSupport();
+			if ( ! databaseMetaData.supportsNamedParameters() ) {
+				throw new QueryException(
+						"Named stored procedure parameters used, but JDBC driver does not support named parameters"
+				);
+			}
+			typeOfParameters = TypeOfParameter.NAMED;
+		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public StoredProcedureCall registerStoredProcedureParameter(String name, Class type, ParameterMode mode) {
 		registerParameter( new NamedStoredProcedureParameter( this, name, mode, type ) );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
-	public List getRegisteredParameters() {
-		return registeredParameters;
+	public List<StoredProcedureParameter> getRegisteredParameters() {
+		return new ArrayList<StoredProcedureParameter>( registeredParameters );
 	}
 
 	@Override
 	public StoredProcedureParameterImplementor getRegisteredParameter(String name) {
 		if ( typeOfParameters != TypeOfParameter.NAMED ) {
 			throw new IllegalArgumentException( "Names were not used to register parameters with this stored procedure call" );
 		}
 		for ( StoredProcedureParameterImplementor parameter : registeredParameters ) {
 			if ( name.equals( parameter.getName() ) ) {
 				return parameter;
 			}
 		}
-		throw new IllegalArgumentException( "Could not locate parameter registered under that name [" + name + "]" );	}
+		throw new IllegalArgumentException( "Could not locate parameter registered under that name [" + name + "]" );
+	}
 
 	@Override
 	public StoredProcedureParameterImplementor getRegisteredParameter(int position) {
 		try {
 			return registeredParameters.get( position );
 		}
 		catch ( Exception e ) {
 			throw new QueryException( "Could not locate parameter registered using that position [" + position + "]" );
 		}
 	}
 
 	@Override
 	public StoredProcedureOutputs getOutputs() {
 
 		// todo : going to need a very specialized Loader for this.
 		// or, might be a good time to look at splitting Loader up into:
 		//		1) building statement objects
 		//		2) executing statement objects
 		//		3) processing result sets
 
 		// for now assume there are no resultClasses nor mappings defined..
 		// 	TOTAL PROOF-OF-CONCEPT!!!!!!
 
 		final StringBuilder buffer = new StringBuilder().append( "{call " )
 				.append( procedureName )
 				.append( "(" );
 		String sep = "";
 		for ( StoredProcedureParameterImplementor parameter : registeredParameters ) {
 			for ( int i = 0; i < parameter.getSqlTypes().length; i++ ) {
 				buffer.append( sep ).append( "?" );
 				sep = ",";
 			}
 		}
 		buffer.append( ")}" );
 
 		try {
 			final CallableStatement statement = session().getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getLogicalConnection()
 					.getShareableConnectionProxy()
 					.prepareCall( buffer.toString() );
 
 			// prepare parameters
 			int i = 1;
 			for ( StoredProcedureParameterImplementor parameter : registeredParameters ) {
 				if ( parameter == null ) {
 					throw new QueryException( "Registered stored procedure parameters had gaps" );
 				}
 
 				parameter.prepare( statement, i );
 				i += parameter.getSqlTypes().length;
 			}
 
 			return new StoredProcedureOutputsImpl( this, statement );
 		}
 		catch (SQLException e) {
 			throw session().getFactory().getSQLExceptionHelper().convert(
 					e,
 					"Error preparing CallableStatement",
 					getProcedureName()
 			);
 		}
 	}
 
 
 	@Override
 	public Type[] getReturnTypes() throws HibernateException {
 		throw new NotYetImplementedException();
 	}
 
 	protected Set<String> synchronizedQuerySpaces() {
 		if ( synchronizedQuerySpaces == null ) {
 			synchronizedQuerySpaces = new HashSet<String>();
 		}
 		return synchronizedQuerySpaces;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Collection<String> getSynchronizedQuerySpaces() {
 		if ( synchronizedQuerySpaces == null ) {
 			return Collections.emptySet();
 		}
 		else {
 			return Collections.unmodifiableSet( synchronizedQuerySpaces );
 		}
 	}
 
 	public Set<String> getSynchronizedQuerySpacesSet() {
 		return (Set<String>) getSynchronizedQuerySpaces();
 	}
 
 	@Override
 	public StoredProcedureCallImpl addSynchronizedQuerySpace(String querySpace) {
 		synchronizedQuerySpaces().add( querySpace );
 		return this;
 	}
 
 	@Override
 	public StoredProcedureCallImpl addSynchronizedEntityName(String entityName) {
 		addSynchronizedQuerySpaces( session().getFactory().getEntityPersister( entityName ) );
 		return this;
 	}
 
 	protected void addSynchronizedQuerySpaces(EntityPersister persister) {
 		synchronizedQuerySpaces().addAll( Arrays.asList( (String[]) persister.getQuerySpaces() ) );
 	}
 
 	@Override
 	public StoredProcedureCallImpl addSynchronizedEntityClass(Class entityClass) {
 		addSynchronizedQuerySpaces( session().getFactory().getEntityPersister( entityClass.getName() ) );
 		return this;
 	}
 
 	public QueryParameters buildQueryParametersObject() {
 		QueryParameters qp = super.buildQueryParametersObject();
 		// both of these are for documentation purposes, they are actually handled directly...
 		qp.setAutoDiscoverScalarTypes( true );
 		qp.setCallable( true );
 		return qp;
 	}
 
+	public StoredProcedureParameterImplementor[] collectRefCursorParameters() {
+		List<StoredProcedureParameterImplementor> refCursorParams = new ArrayList<StoredProcedureParameterImplementor>();
+		for ( StoredProcedureParameterImplementor param : registeredParameters ) {
+			if ( param.getMode() == ParameterMode.REF_CURSOR ) {
+				refCursorParams.add( param );
+			}
+		}
+		return refCursorParams.toArray( new StoredProcedureParameterImplementor[refCursorParams.size()] );
+	}
+
 	/**
 	 * Ternary logic enum
 	 */
 	private static enum TypeOfParameter {
 		NAMED,
 		POSITIONAL,
 		UNKNOWN
 	}
 
 	protected static interface StoredProcedureParameterImplementor<T> extends StoredProcedureParameter<T> {
 		public void prepare(CallableStatement statement, int i) throws SQLException;
 
 		public int[] getSqlTypes();
 
 		public T extract(CallableStatement statement);
 	}
 
 	public static abstract class AbstractStoredProcedureParameterImpl<T> implements StoredProcedureParameterImplementor<T> {
 		private final StoredProcedureCallImpl procedureCall;
 
 		private final ParameterMode mode;
 		private final Class<T> type;
 
 		private int startIndex;
 		private Type hibernateType;
 		private int[] sqlTypes;
 
 		private StoredProcedureParameterBindImpl bind;
 
 		protected AbstractStoredProcedureParameterImpl(
 				StoredProcedureCallImpl procedureCall,
 				ParameterMode mode,
 				Class<T> type) {
 			this.procedureCall = procedureCall;
 			this.mode = mode;
 			this.type = type;
 
 			setHibernateType( session().getFactory().getTypeResolver().heuristicType( type.getName() ) );
 		}
 
 		@Override
 		public String getName() {
 			return null;
 		}
 
 		@Override
 		public Integer getPosition() {
 			return null;
 		}
 
 		@Override
 		public Class<T> getType() {
 			return type;
 		}
 
 		@Override
 		public ParameterMode getMode() {
 			return mode;
 		}
 
 		@Override
 		public void setHibernateType(Type type) {
 			if ( type == null ) {
 				throw new IllegalArgumentException( "Type cannot be null" );
 			}
 			this.hibernateType = type;
 			this.sqlTypes = hibernateType.sqlTypes( session().getFactory() );
 		}
 
 		protected SessionImplementor session() {
 			return procedureCall.session();
 		}
 
 		@Override
 		public void prepare(CallableStatement statement, int startIndex) throws SQLException {
 			if ( mode == ParameterMode.REF_CURSOR ) {
 				throw new NotYetImplementedException( "Support for REF_CURSOR parameters not yet supported" );
 			}
 
 			this.startIndex = startIndex;
 			if ( mode == ParameterMode.IN || mode == ParameterMode.INOUT || mode == ParameterMode.OUT ) {
 				if ( mode == ParameterMode.INOUT || mode == ParameterMode.OUT ) {
 					if ( sqlTypes.length > 1 ) {
 						if ( ProcedureParameterExtractionAware.class.isInstance( hibernateType )
 								&& ( (ProcedureParameterExtractionAware) hibernateType ).canDoExtraction() ) {
 							// the type can handle multi-param extraction...
 						}
 						else {
 							// it cannot...
 							throw new UnsupportedOperationException(
 									"Type [" + hibernateType + "] does support multi-parameter value extraction"
 							);
 						}
 					}
 					for ( int i = 0; i < sqlTypes.length; i++ ) {
 						statement.registerOutParameter( startIndex + i, sqlTypes[i] );
 					}
 				}
 
 				if ( mode == ParameterMode.INOUT || mode == ParameterMode.IN ) {
 					if ( bind == null || bind.getValue() == null ) {
 						log.debugf(
 								"Stored procedure [%s] IN/INOUT parameter [%s] not bound; assuming procedure defines default value",
 								procedureCall.getProcedureName(),
 								this
 						);
 					}
 					else {
 						final Type typeToUse;
 						if ( bind.getExplicitTemporalType() != null && bind.getExplicitTemporalType() == TemporalType.TIMESTAMP ) {
 							typeToUse = hibernateType;
 						}
 						else if ( bind.getExplicitTemporalType() != null && bind.getExplicitTemporalType() == TemporalType.DATE ) {
 							typeToUse = DateType.INSTANCE;
 						}
 						else {
 							typeToUse = hibernateType;
 						}
 						typeToUse.nullSafeSet( statement, bind.getValue(), startIndex, session() );
 					}
 				}
 			}
+			else {
+				// we have a REF_CURSOR type param
+				if ( procedureCall.typeOfParameters == TypeOfParameter.NAMED ) {
+					session().getFactory().getServiceRegistry()
+							.getService( RefCursorSupport.class )
+							.registerRefCursorParameter( statement, getName() );
+				}
+				else {
+					session().getFactory().getServiceRegistry()
+							.getService( RefCursorSupport.class )
+							.registerRefCursorParameter( statement, getPosition() );
+				}
+			}
 		}
 
 		public int[] getSqlTypes() {
 			return sqlTypes;
 		}
 
 		@Override
 		public StoredProcedureParameterBind getParameterBind() {
 			return bind;
 		}
 
 		@Override
 		public void bindValue(T value) {
 			this.bind = new StoredProcedureParameterBindImpl<T>( value );
 		}
 
 		@Override
 		public void bindValue(T value, TemporalType explicitTemporalType) {
 			if ( explicitTemporalType != null ) {
 				if ( ! isDateTimeType() ) {
 					throw new IllegalArgumentException( "TemporalType should not be specified for non date/time type" );
 				}
 			}
 			this.bind = new StoredProcedureParameterBindImpl<T>( value, explicitTemporalType );
 		}
 
 		private boolean isDateTimeType() {
 			return Date.class.isAssignableFrom( type )
 					|| Calendar.class.isAssignableFrom( type );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T extract(CallableStatement statement) {
+			if ( mode == ParameterMode.IN ) {
+				throw new QueryException( "IN parameter not valid for output extraction" );
+			}
+			else if ( mode == ParameterMode.REF_CURSOR ) {
+				throw new QueryException( "REF_CURSOR parameters should be accessed via results" );
+			}
+
 			try {
 				if ( ProcedureParameterExtractionAware.class.isInstance( hibernateType ) ) {
 					return (T) ( (ProcedureParameterExtractionAware) hibernateType ).extract( statement, startIndex, session() );
 				}
 				else {
 					return (T) statement.getObject( startIndex );
 				}
 			}
 			catch (SQLException e) {
 				throw procedureCall.session().getFactory().getSQLExceptionHelper().convert(
 						e,
 						"Unable to extract OUT/INOUT parameter value"
 				);
 			}
 		}
 	}
 
 	public static class StoredProcedureParameterBindImpl<T> implements StoredProcedureParameterBind<T> {
 		private final T value;
 		private final TemporalType explicitTemporalType;
 
 		public StoredProcedureParameterBindImpl(T value) {
 			this( value, null );
 		}
 
 		public StoredProcedureParameterBindImpl(T value, TemporalType explicitTemporalType) {
 			this.value = value;
 			this.explicitTemporalType = explicitTemporalType;
 		}
 
 		@Override
 		public T getValue() {
 			return value;
 		}
 
 		@Override
 		public TemporalType getExplicitTemporalType() {
 			return explicitTemporalType;
 		}
 	}
 
 	public static class NamedStoredProcedureParameter<T> extends AbstractStoredProcedureParameterImpl<T> {
 		private final String name;
 
 		public NamedStoredProcedureParameter(
 				StoredProcedureCallImpl procedureCall,
 				String name,
 				ParameterMode mode,
 				Class<T> type) {
 			super( procedureCall, mode, type );
 			this.name = name;
 		}
 
 		@Override
 		public String getName() {
 			return name;
 		}
 	}
 
 	public static class PositionalStoredProcedureParameter<T> extends AbstractStoredProcedureParameterImpl<T> {
 		private final Integer position;
 
 		public PositionalStoredProcedureParameter(
 				StoredProcedureCallImpl procedureCall,
 				Integer position,
 				ParameterMode mode,
 				Class<T> type) {
 			super( procedureCall, mode, type );
 			this.position = position;
 		}
 
 		@Override
 		public Integer getPosition() {
 			return position;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureOutputsImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureOutputsImpl.java
index f36a7075ad..590b8b8c75 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureOutputsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureOutputsImpl.java
@@ -1,283 +1,317 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.internal;
 
+import javax.persistence.ParameterMode;
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
+import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.JDBCException;
+import org.hibernate.StoredProcedureCall.StoredProcedureParameter;
 import org.hibernate.StoredProcedureOutputs;
 import org.hibernate.StoredProcedureResultSetReturn;
 import org.hibernate.StoredProcedureReturn;
 import org.hibernate.StoredProcedureUpdateCountReturn;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.internal.StoredProcedureCallImpl.StoredProcedureParameterImplementor;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.loader.custom.Return;
 import org.hibernate.loader.custom.sql.SQLQueryReturnProcessor;
+import org.hibernate.service.jdbc.cursor.spi.RefCursorSupport;
 
 /**
  * @author Steve Ebersole
  */
 public class StoredProcedureOutputsImpl implements StoredProcedureOutputs {
 	private final StoredProcedureCallImpl procedureCall;
 	private final CallableStatement callableStatement;
 
+	private final StoredProcedureParameterImplementor[] refCursorParameters;
 	private final CustomLoaderExtension loader;
 
 	private CurrentReturnDescriptor currentReturnDescriptor;
+
 	private boolean executed = false;
+	private int refCursorParamIndex = 0;
 
 	StoredProcedureOutputsImpl(StoredProcedureCallImpl procedureCall, CallableStatement callableStatement) {
 		this.procedureCall = procedureCall;
 		this.callableStatement = callableStatement;
 
+		this.refCursorParameters = procedureCall.collectRefCursorParameters();
 		// For now...
 		this.loader = buildSpecializedCustomLoader( procedureCall );
 	}
 
 	@Override
 	public Object getOutputParameterValue(String name) {
 		return procedureCall.getRegisteredParameter( name ).extract( callableStatement );
 	}
 
 	@Override
 	public Object getOutputParameterValue(int position) {
 		return procedureCall.getRegisteredParameter( position ).extract( callableStatement );
 	}
 
 	@Override
 	public boolean hasMoreReturns() {
 		if ( currentReturnDescriptor == null ) {
 			final boolean isResultSet;
 
 			if ( executed ) {
 				try {
 					isResultSet = callableStatement.getMoreResults();
 				}
 				catch (SQLException e) {
 					throw convert( e, "Error calling CallableStatement.getMoreResults" );
 				}
 			}
 			else {
 				try {
 					isResultSet = callableStatement.execute();
 				}
 				catch (SQLException e) {
 					throw convert( e, "Error calling CallableStatement.execute" );
 				}
 				executed = true;
 			}
 
 			int updateCount = -1;
 			if ( ! isResultSet ) {
 				try {
 					updateCount = callableStatement.getUpdateCount();
 				}
 				catch (SQLException e) {
 					throw convert( e, "Error calling CallableStatement.getUpdateCount" );
 				}
 			}
 
-			currentReturnDescriptor = new CurrentReturnDescriptor( isResultSet, updateCount );
+			currentReturnDescriptor = new CurrentReturnDescriptor( isResultSet, updateCount, refCursorParamIndex );
 		}
 
 		return hasMoreResults( currentReturnDescriptor );
 	}
 
 	private boolean hasMoreResults(CurrentReturnDescriptor descriptor) {
-		return currentReturnDescriptor.isResultSet || currentReturnDescriptor.updateCount >= 0;
+		return descriptor.isResultSet
+				|| descriptor.updateCount >= 0
+				|| descriptor.refCursorParamIndex < refCursorParameters.length;
 	}
 
 	@Override
 	public StoredProcedureReturn getNextReturn() {
 		if ( currentReturnDescriptor == null ) {
 			if ( executed ) {
 				throw new IllegalStateException( "Unexpected condition" );
 			}
 			else {
 				throw new IllegalStateException( "hasMoreReturns() not called before getNextReturn()" );
 			}
 		}
 
 		if ( ! hasMoreResults( currentReturnDescriptor ) ) {
 			throw new IllegalStateException( "Results have been exhausted" );
 		}
 
 		CurrentReturnDescriptor copyReturnDescriptor = currentReturnDescriptor;
 		currentReturnDescriptor = null;
 
 		if ( copyReturnDescriptor.isResultSet ) {
 			try {
 				return new ResultSetReturn( this, callableStatement.getResultSet() );
 			}
 			catch (SQLException e) {
 				throw convert( e, "Error calling CallableStatement.getResultSet" );
 			}
 		}
-		else {
+		else if ( copyReturnDescriptor.updateCount >= 0 ) {
 			return new UpdateCountReturn( this, copyReturnDescriptor.updateCount );
 		}
+		else {
+			this.refCursorParamIndex++;
+			ResultSet resultSet;
+			int refCursorParamIndex = copyReturnDescriptor.refCursorParamIndex;
+			StoredProcedureParameterImplementor refCursorParam = refCursorParameters[refCursorParamIndex];
+			if ( refCursorParam.getName() != null ) {
+				resultSet = procedureCall.session().getFactory().getServiceRegistry()
+						.getService( RefCursorSupport.class )
+						.getResultSet( callableStatement, refCursorParam.getName() );
+			}
+			else {
+				resultSet = procedureCall.session().getFactory().getServiceRegistry()
+						.getService( RefCursorSupport.class )
+						.getResultSet( callableStatement, refCursorParam.getPosition() );
+			}
+			return new ResultSetReturn( this, resultSet );
+		}
 	}
 
 	protected JDBCException convert(SQLException e, String message) {
-		return procedureCall.session().getFactory().getSQLExceptionHelper().convert( e, message, procedureCall.getProcedureName() );
+		return procedureCall.session().getFactory().getSQLExceptionHelper().convert(
+				e,
+				message,
+				procedureCall.getProcedureName()
+		);
 	}
 
 	private static class CurrentReturnDescriptor {
 		private final boolean isResultSet;
 		private final int updateCount;
+		private final int refCursorParamIndex;
 
-		private CurrentReturnDescriptor(boolean resultSet, int updateCount) {
-			isResultSet = resultSet;
+		private CurrentReturnDescriptor(boolean isResultSet, int updateCount, int refCursorParamIndex) {
+			this.isResultSet = isResultSet;
 			this.updateCount = updateCount;
+			this.refCursorParamIndex = refCursorParamIndex;
 		}
 	}
 
 	private static class ResultSetReturn implements StoredProcedureResultSetReturn {
 		private final StoredProcedureOutputsImpl storedProcedureOutputs;
 		private final ResultSet resultSet;
 
 		public ResultSetReturn(StoredProcedureOutputsImpl storedProcedureOutputs, ResultSet resultSet) {
 			this.storedProcedureOutputs = storedProcedureOutputs;
 			this.resultSet = resultSet;
 		}
 
 		@Override
 		public boolean isResultSet() {
 			return true;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public List getResultList() {
 			try {
 				return storedProcedureOutputs.loader.processResultSet( resultSet );
 			}
 			catch (SQLException e) {
 				throw storedProcedureOutputs.convert( e, "Error calling ResultSet.next" );
 			}
 		}
 
 		@Override
 		public Object getSingleResult() {
 			List results = getResultList();
 			if ( results == null || results.isEmpty() ) {
 				return null;
 			}
 			else {
 				return results.get( 0 );
 			}
 		}
 	}
 
 	private class UpdateCountReturn implements StoredProcedureUpdateCountReturn {
 		private final StoredProcedureOutputsImpl storedProcedureOutputs;
 		private final int updateCount;
 
 		public UpdateCountReturn(StoredProcedureOutputsImpl storedProcedureOutputs, int updateCount) {
 			this.storedProcedureOutputs = storedProcedureOutputs;
 			this.updateCount = updateCount;
 		}
 
 		@Override
 		public int getUpdateCount() {
 			return updateCount;
 		}
 
 		@Override
 		public boolean isResultSet() {
 			return false;
 		}
 	}
 
 	private static CustomLoaderExtension buildSpecializedCustomLoader(final StoredProcedureCallImpl procedureCall) {
 		final SQLQueryReturnProcessor processor = new SQLQueryReturnProcessor(
 				procedureCall.getQueryReturns(),
 				procedureCall.session().getFactory()
 		);
 		processor.process();
 		final List<Return> customReturns = processor.generateCustomReturns( false );
 
 		CustomQuery customQuery = new CustomQuery() {
 			@Override
 			public String getSQL() {
 				return procedureCall.getProcedureName();
 			}
 
 			@Override
 			public Set<String> getQuerySpaces() {
 				return procedureCall.getSynchronizedQuerySpacesSet();
 			}
 
 			@Override
 			public Map getNamedParameterBindPoints() {
 				// no named parameters in terms of embedded in the SQL string
 				return null;
 			}
 
 			@Override
 			public List<Return> getCustomQueryReturns() {
 				return customReturns;
 			}
 		};
 
 		return new CustomLoaderExtension(
 				customQuery,
 				procedureCall.buildQueryParametersObject(),
 				procedureCall.session()
 		);
 	}
 
 	private static class CustomLoaderExtension extends CustomLoader {
 		private QueryParameters queryParameters;
 		private SessionImplementor session;
 
 		public CustomLoaderExtension(
 				CustomQuery customQuery,
 				QueryParameters queryParameters,
 				SessionImplementor session) {
 			super( customQuery, session.getFactory() );
 			this.queryParameters = queryParameters;
 			this.session = session;
 		}
 
 		public List processResultSet(ResultSet resultSet) throws SQLException {
 			super.autoDiscoverTypes( resultSet );
 			return super.processResultSet(
 					resultSet,
 					queryParameters,
 					session,
 					true,
 					null,
 					Integer.MAX_VALUE
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index a74d06a9e2..38d3d3b480 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,507 +1,513 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.mapping;
 
 import javax.persistence.AttributeConverter;
 import java.lang.reflect.TypeVariable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BasicExtractor;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
 	private static final Logger log = Logger.getLogger( SimpleValue.class );
 
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final Mappings mappings;
 
 	private final List columns = new ArrayList();
 	private String typeName;
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private Properties typeParameters;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDefinition jpaAttributeConverterDefinition;
 	private Type type;
 
 	public SimpleValue(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public SimpleValue(Mappings mappings, Table table) {
 		this( mappings );
 		this.table = table;
 	}
 
 	public Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) columns.add(column);
 		column.setValue(this);
 		column.setTypeIndex( columns.size()-1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add(formula);
 	}
 	
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) return true;
 		}
 		return false;
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 	public Iterator getColumnIterator() {
 		return columns.iterator();
 	}
 	public List getConstraintColumns() {
 		return columns;
 	}
 	public String getTypeName() {
 		return typeName;
 	}
 	public void setTypeName(String type) {
 		this.typeName = type;
 	}
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void createForeignKey() throws MappingException {}
 
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 		
 		Properties params = new Properties();
 		
 		//if the hibernate-mapping did not specify a schema/catalog, use the defaults
 		//specified by properties - but note that if the schema/catalog were specified
 		//in hibernate-mapping, or as params, they will already be initialized and
 		//will override the values set here (they are in identifierGeneratorProperties)
 		if ( defaultSchema!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.SCHEMA, defaultSchema);
 		}
 		if ( defaultCatalog!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.CATALOG, defaultCatalog);
 		}
 		
 		//pass the entity-name, if not a collection-id
 		if (rootClass!=null) {
 			params.setProperty( IdentifierGenerator.ENTITY_NAME, rootClass.getEntityName() );
 			params.setProperty( IdentifierGenerator.JPA_ENTITY_NAME, rootClass.getJpaEntityName() );
 		}
 		
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getTable().getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 		
 		//pass the column name (a generated id almost always has a single column)
 		String columnName = ( (Column) getColumnIterator().next() ).getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.PK, columnName );
 		
 		if (rootClass!=null) {
 			StringBuilder tables = new StringBuilder();
 			Iterator iter = rootClass.getIdentityTables().iterator();
 			while ( iter.hasNext() ) {
 				Table table= (Table) iter.next();
 				tables.append( table.getQuotedName(dialect) );
 				if ( iter.hasNext() ) tables.append(", ");
 			}
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
 		}
 		else {
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
 		}
 
 		if (identifierGeneratorProperties!=null) {
 			params.putAll(identifierGeneratorProperties);
 		}
 
 		// TODO : we should pass along all settings once "config lifecycle" is hashed out...
 		params.put(
 				Environment.PREFER_POOLED_VALUES_LO,
 				mappings.getConfigurationProperties().getProperty( Environment.PREFER_POOLED_VALUES_LO, "false" )
 		);
 
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 		
 	}
 
 	public boolean isUpdateable() {
 		//needed to satisfy KeyValue
 		return true;
 	}
 	
 	public FetchMode getFetchMode() {
 		return FetchMode.SELECT;
 	}
 
 	public Properties getIdentifierGeneratorProperties() {
 		return identifierGeneratorProperties;
 	}
 
 	public String getNullValue() {
 		return nullValue;
 	}
 
 	public Table getTable() {
 		return table;
 	}
 
 	/**
 	 * Returns the identifierGeneratorStrategy.
 	 * @return String
 	 */
 	public String getIdentifierGeneratorStrategy() {
 		return identifierGeneratorStrategy;
 	}
 	
 	public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy )
 				.equals( IdentityGenerator.class );
 	}
 
 	/**
 	 * Sets the identifierGeneratorProperties.
 	 * @param identifierGeneratorProperties The identifierGeneratorProperties to set
 	 */
 	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
 		this.identifierGeneratorProperties = identifierGeneratorProperties;
 	}
 
 	/**
 	 * Sets the identifierGeneratorStrategy.
 	 * @param identifierGeneratorStrategy The identifierGeneratorStrategy to set
 	 */
 	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
 		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
 	}
 
 	/**
 	 * Sets the nullValue.
 	 * @param nullValue The nullValue to set
 	 */
 	public void setNullValue(String nullValue) {
 		this.nullValue = nullValue;
 	}
 
 	public String getForeignKeyName() {
 		return foreignKeyName;
 	}
 
 	public void setForeignKeyName(String foreignKeyName) {
 		this.foreignKeyName = foreignKeyName;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return alternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean unique) {
 		this.alternateUniqueKey = unique;
 	}
 
 	public boolean isNullable() {
 		if ( hasFormula() ) return true;
 		boolean nullable = true;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			if ( !( (Column) iter.next() ).isNullable() ) {
 				nullable = false;
 				return nullable; //shortcut
 			}
 		}
 		return nullable;
 	}
 
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getColumnSpan()==getType().getColumnSpan(mapping);
 	}
 
 	public Type getType() throws MappingException {
 		if ( type != null ) {
 			return type;
 		}
 
 		if ( typeName == null ) {
 			throw new MappingException( "No type name" );
 		}
 
 		Type result = mappings.getTypeResolver().heuristicType( typeName, typeParameters );
 		if ( result == null ) {
 			String msg = "Could not determine type for: " + typeName;
 			if ( table != null ) {
 				msg += ", at table: " + table.getName();
 			}
 			if ( columns!=null && columns.size()>0 ) {
 				msg += ", for columns: " + columns;
 			}
 			throw new MappingException( msg );
 		}
 
 		return result;
 	}
 
 	@SuppressWarnings("unchecked")
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
 		// NOTE : this is called as the last piece in setting SimpleValue type information, and implementations
 		// rely on that fact, using it as a signal that all information it is going to get is defined at this point...
 
 		if ( typeName != null ) {
 			// assume either (a) explicit type was specified or (b) determine was already performed
 			return;
 		}
 
 		if ( type != null ) {
 			return;
 		}
 
 		if ( jpaAttributeConverterDefinition == null ) {
 			// this is here to work like legacy.  This should change when we integrate with metamodel to
 			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
 			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
 			if ( className == null ) {
 				throw new MappingException( "you must specify types for a dynamic entity: " + propertyName );
 			}
 			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName ).getName();
 			return;
 		}
 
 		// we had an AttributeConverter...
 
 		// todo : we should validate the number of columns present
 		// todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 		//		then we can "play them against each other" in terms of determining proper typing
 		// todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
 
 		// AttributeConverter works totally in memory, meaning it converts between one Java representation (the entity
 		// attribute representation) and another (the value bound into JDBC statements or extracted from results).
 		// However, the Hibernate Type system operates at the lower level of actually dealing with those JDBC objects.
 		// So even though we have an AttributeConverter, we still need to "fill out" the rest of the BasicType
 		// data.  For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.  For the SqlTypeDescriptor portion we use the
 		// "database column representation" part of the AttributeConverter to resolve the "recommended" JDBC type-code
 		// and use that type-code to resolve the SqlTypeDescriptor to use.
 		final Class entityAttributeJavaType = jpaAttributeConverterDefinition.getEntityAttributeType();
 		final Class databaseColumnJavaType = jpaAttributeConverterDefinition.getDatabaseColumnType();
 		final int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 
 		final JavaTypeDescriptor javaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// the adapter here injects the AttributeConverter calls into the binding/extraction process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				jpaAttributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptor
 		);
 
 		final String name = "BasicType adapter for AttributeConverter<" + entityAttributeJavaType + "," + databaseColumnJavaType + ">";
 		type = new AbstractSingleColumnStandardBasicType( sqlTypeDescriptorAdapter, javaTypeDescriptor ) {
 			@Override
 			public String getName() {
 				return name;
 			}
 		};
 		log.debug( "Created : " + name );
 
 		// todo : cache the BasicType we just created in case that AttributeConverter is applied multiple times.
 	}
 
 	private Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
 	}
 
 	public boolean isTypeSpecified() {
 		return typeName!=null;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 	
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + columns.toString() + ')';
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		int i = 0;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable s = (Selectable) iter.next();
 			result[i++] = !s.isFormula();
 		}
 		return result;
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		return getColumnInsertability();
 	}
 
 	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition jpaAttributeConverterDefinition) {
 		this.jpaAttributeConverterDefinition = jpaAttributeConverterDefinition;
 	}
 
 	public static class AttributeConverterSqlTypeDescriptorAdapter implements SqlTypeDescriptor {
 		private final AttributeConverter converter;
 		private final SqlTypeDescriptor delegate;
 
 		public AttributeConverterSqlTypeDescriptorAdapter(AttributeConverter converter, SqlTypeDescriptor delegate) {
 			this.converter = converter;
 			this.delegate = delegate;
 		}
 
 		@Override
 		public int getSqlType() {
 			return delegate.getSqlType();
 		}
 
 		@Override
 		public boolean canBeRemapped() {
 			return delegate.canBeRemapped();
 		}
 
 		@Override
 		public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			final ValueBinder realBinder = delegate.getBinder( javaTypeDescriptor );
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				@SuppressWarnings("unchecked")
 				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 						throws SQLException {
 					realBinder.bind( st, converter.convertToDatabaseColumn( value ), index, options );
 				}
 			};
 		}
 
 		@Override
 		public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			final ValueExtractor realExtractor = delegate.getExtractor( javaTypeDescriptor );
 			return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( rs, name, options ) );
 				}
 
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
 						throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, index, options ) );
 				}
+
+				@Override
+				@SuppressWarnings("unchecked")
+				protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, new String[] {name}, options ) );
+				}
 			};
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
index 08e3446e00..8d630f0953 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
@@ -1,87 +1,89 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.service;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.transaction.internal.TransactionFactoryInitiator;
 import org.hibernate.id.factory.internal.MutableIdentifierGeneratorFactoryInitiator;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.internal.PersisterFactoryInitiator;
 import org.hibernate.service.config.internal.ConfigurationServiceInitiator;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryInitiator;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.service.jdbc.connections.internal.MultiTenantConnectionProviderInitiator;
+import org.hibernate.service.jdbc.cursor.internal.RefCursorSupportInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.service.jmx.internal.JmxServiceInitiator;
 import org.hibernate.service.jndi.internal.JndiServiceInitiator;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractorInitiator;
 
 /**
  * Central definition of the standard set of service initiators defined by Hibernate.
  * 
  * @author Steve Ebersole
  */
 public class StandardServiceInitiators {
 	public static List<BasicServiceInitiator> LIST = buildStandardServiceInitiatorList();
 
 	private static List<BasicServiceInitiator> buildStandardServiceInitiatorList() {
 		final List<BasicServiceInitiator> serviceInitiators = new ArrayList<BasicServiceInitiator>();
 
 		serviceInitiators.add( ConfigurationServiceInitiator.INSTANCE );
 		serviceInitiators.add( ImportSqlCommandExtractorInitiator.INSTANCE );
 
 		serviceInitiators.add( JndiServiceInitiator.INSTANCE );
 		serviceInitiators.add( JmxServiceInitiator.INSTANCE );
 
 		serviceInitiators.add( PersisterClassResolverInitiator.INSTANCE );
 		serviceInitiators.add( PersisterFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( ConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( MultiTenantConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( DialectResolverInitiator.INSTANCE );
 		serviceInitiators.add( DialectFactoryInitiator.INSTANCE );
 		serviceInitiators.add( BatchBuilderInitiator.INSTANCE );
 		serviceInitiators.add( JdbcServicesInitiator.INSTANCE );
+		serviceInitiators.add( RefCursorSupportInitiator.INSTANCE );
 
 		serviceInitiators.add( MutableIdentifierGeneratorFactoryInitiator.INSTANCE);
 
 		serviceInitiators.add( JtaPlatformInitiator.INSTANCE );
 		serviceInitiators.add( TransactionFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( SessionFactoryServiceRegistryFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( RegionFactoryInitiator.INSTANCE );
 
 		return Collections.unmodifiableList( serviceInitiators );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/cursor/internal/RefCursorSupportInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/cursor/internal/RefCursorSupportInitiator.java
new file mode 100644
index 0000000000..7c7cc4fe11
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/cursor/internal/RefCursorSupportInitiator.java
@@ -0,0 +1,47 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.service.jdbc.cursor.internal;
+
+import java.util.Map;
+
+import org.hibernate.service.jdbc.cursor.spi.RefCursorSupport;
+import org.hibernate.service.spi.BasicServiceInitiator;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+
+/**
+ * @author Steve Ebersole
+ */
+public class RefCursorSupportInitiator implements BasicServiceInitiator<RefCursorSupport> {
+	public static final RefCursorSupportInitiator INSTANCE = new RefCursorSupportInitiator();
+
+	@Override
+	public RefCursorSupport initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+		return new StandardRefCursorSupport();
+	}
+
+	@Override
+	public Class<RefCursorSupport> getServiceInitiated() {
+		return RefCursorSupport.class;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/cursor/internal/StandardRefCursorSupport.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/cursor/internal/StandardRefCursorSupport.java
new file mode 100644
index 0000000000..cb48b29468
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/cursor/internal/StandardRefCursorSupport.java
@@ -0,0 +1,233 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.service.jdbc.cursor.internal;
+
+import java.lang.reflect.InvocationTargetException;
+import java.lang.reflect.Method;
+import java.sql.CallableStatement;
+import java.sql.DatabaseMetaData;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.service.jdbc.cursor.spi.RefCursorSupport;
+import org.hibernate.service.spi.InjectService;
+
+/**
+ * @author Steve Ebersole
+ */
+public class StandardRefCursorSupport implements RefCursorSupport {
+	private static final Logger log = Logger.getLogger( StandardRefCursorSupport.class );
+
+	private JdbcServices jdbcServices;
+
+	@InjectService
+	@SuppressWarnings("UnusedDeclaration")
+	public void injectJdbcServices(JdbcServices jdbcServices) {
+		this.jdbcServices = jdbcServices;
+	}
+
+	@Override
+	public void registerRefCursorParameter(CallableStatement statement, int position) {
+		if ( jdbcServices.getExtractedMetaDataSupport().supportsRefCursors() ) {
+			try {
+				statement.registerOutParameter( position, refCursorTypeCode() );
+			}
+			catch (SQLException e) {
+				throw jdbcServices.getSqlExceptionHelper().convert( e, "Error registering REF_CURSOR parameter [" + position + "]" );
+			}
+		}
+		else {
+			try {
+				jdbcServices.getDialect().registerResultSetOutParameter( statement, position );
+			}
+			catch (SQLException e) {
+				throw jdbcServices.getSqlExceptionHelper().convert( e, "Error asking dialect to register ref cursor parameter [" + position + "]" );
+			}
+		}
+	}
+
+	@Override
+	public void registerRefCursorParameter(CallableStatement statement, String name) {
+		if ( jdbcServices.getExtractedMetaDataSupport().supportsRefCursors() ) {
+			try {
+				statement.registerOutParameter( name, refCursorTypeCode() );
+			}
+			catch (SQLException e) {
+				throw jdbcServices.getSqlExceptionHelper().convert( e, "Error registering REF_CURSOR parameter [" + name + "]" );
+			}
+		}
+		else {
+			try {
+				jdbcServices.getDialect().registerResultSetOutParameter( statement, name );
+			}
+			catch (SQLException e) {
+				throw jdbcServices.getSqlExceptionHelper().convert( e, "Error asking dialect to register ref cursor parameter [" + name + "]" );
+			}
+		}
+	}
+
+	@Override
+	public ResultSet getResultSet(CallableStatement statement, int position) {
+		if ( jdbcServices.getExtractedMetaDataSupport().supportsRefCursors() ) {
+			try {
+				return (ResultSet) getResultSetByPositionMethod().invoke( statement, position, ResultSet.class );
+			}
+			catch (InvocationTargetException e) {
+				if ( e.getTargetException() instanceof SQLException ) {
+					throw jdbcServices.getSqlExceptionHelper().convert(
+							(SQLException) e.getTargetException(),
+							"Error extracting REF_CURSOR parameter [" + position + "]"
+					);
+				}
+				else {
+					throw new HibernateException( "Unexpected error extracting REF_CURSOR parameter [" + position + "]", e.getTargetException() );
+				}
+			}
+			catch (Exception e) {
+				throw new HibernateException( "Unexpected error extracting REF_CURSOR parameter [" + position + "]", e );
+			}
+		}
+		else {
+			try {
+				return jdbcServices.getDialect().getResultSet( statement, position );
+			}
+			catch (SQLException e) {
+				throw jdbcServices.getSqlExceptionHelper().convert(
+						e,
+						"Error asking dialect to extract ResultSet from CallableStatement parameter [" + position + "]"
+				);
+			}
+		}
+	}
+
+	@Override
+	public ResultSet getResultSet(CallableStatement statement, String name) {
+		if ( jdbcServices.getExtractedMetaDataSupport().supportsRefCursors() ) {
+			try {
+				return (ResultSet) getResultSetByNameMethod().invoke( statement, name, ResultSet.class );
+			}
+			catch (InvocationTargetException e) {
+				if ( e.getTargetException() instanceof SQLException ) {
+					throw jdbcServices.getSqlExceptionHelper().convert(
+							(SQLException) e.getTargetException(),
+							"Error extracting REF_CURSOR parameter [" + name + "]"
+					);
+				}
+				else {
+					throw new HibernateException( "Unexpected error extracting REF_CURSOR parameter [" + name + "]", e.getTargetException() );
+				}
+			}
+			catch (Exception e) {
+				throw new HibernateException( "Unexpected error extracting REF_CURSOR parameter [" + name + "]", e );
+			}
+		}
+		else {
+			try {
+				return jdbcServices.getDialect().getResultSet( statement, name );
+			}
+			catch (SQLException e) {
+				throw jdbcServices.getSqlExceptionHelper().convert(
+						e,
+						"Error asking dialect to extract ResultSet from CallableStatement parameter [" + name + "]"
+				);
+			}
+		}
+	}
+
+	@SuppressWarnings("UnnecessaryUnboxing")
+	public static boolean supportsRefCursors(DatabaseMetaData meta) {
+		// Standard JDBC REF_CURSOR support was not added until Java 8, so we need to use reflection to attempt to
+		// access these fields/methods...
+		try {
+			return ( (Boolean) meta.getClass().getMethod( "supportsRefCursors" ).invoke( null ) ).booleanValue();
+		}
+		catch (NoSuchMethodException e) {
+			log.trace( "JDBC DatabaseMetaData class does not define supportsRefCursors method..." );
+		}
+		catch (Exception e) {
+			log.debug( "Unexpected error trying to gauge level of JDBC REF_CURSOR support : " + e.getMessage() );
+		}
+		return false;
+	}
+
+
+	private static Integer refCursorTypeCode;
+
+	@SuppressWarnings("UnnecessaryUnboxing")
+	private int refCursorTypeCode() {
+		if ( refCursorTypeCode == null ) {
+			try {
+				refCursorTypeCode = (Integer) Types.class.getField( "REF_CURSOR" ).get( null );
+			}
+			catch (NoSuchFieldException e) {
+				throw new HibernateException( "java.sql.Types class does not define REF_CURSOR field..." );
+			}
+			catch (IllegalAccessException e) {
+				throw new HibernateException( "Unexpected error trying to determine REF_CURSOR field value : " + e.getMessage() );
+			}
+		}
+		return refCursorTypeCode.intValue();
+	}
+
+
+	private static Method getResultSetByPositionMethod;
+
+	private Method getResultSetByPositionMethod() {
+		if ( getResultSetByPositionMethod == null ) {
+			try {
+				getResultSetByPositionMethod = CallableStatement.class.getMethod( "getObject", int.class, Class.class );
+			}
+			catch (NoSuchMethodException e) {
+				throw new HibernateException( "CallableStatement class does not define getObject(int,Class) method" );
+			}
+			catch (Exception e) {
+				throw new HibernateException( "Unexpected error trying to access CallableStatement#getObject(int,Class)" );
+			}
+		}
+		return getResultSetByPositionMethod;
+	}
+
+
+	private static Method getResultSetByNameMethod;
+
+	private Method getResultSetByNameMethod() {
+		if ( getResultSetByNameMethod == null ) {
+			try {
+				getResultSetByNameMethod = CallableStatement.class.getMethod( "getObject", String.class, Class.class );
+			}
+			catch (NoSuchMethodException e) {
+				throw new HibernateException( "CallableStatement class does not define getObject(String,Class) method" );
+			}
+			catch (Exception e) {
+				throw new HibernateException( "Unexpected error trying to access CallableStatement#getObject(String,Class)" );
+			}
+		}
+		return getResultSetByNameMethod;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/cursor/spi/RefCursorSupport.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/cursor/spi/RefCursorSupport.java
new file mode 100644
index 0000000000..75babaaba8
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/cursor/spi/RefCursorSupport.java
@@ -0,0 +1,73 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.service.jdbc.cursor.spi;
+
+import java.sql.CallableStatement;
+import java.sql.ResultSet;
+
+import org.hibernate.service.Service;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface RefCursorSupport extends Service {
+	/**
+	 * Register a parameter capable of returning a {@link java.sql.ResultSet} *by position*.
+	 *
+	 * @param statement The callable statement.
+	 * @param position The bind position at which to register the output param.
+	 */
+	public void registerRefCursorParameter(CallableStatement statement, int position);
+
+	/**
+	 * Register a parameter capable of returning a {@link java.sql.ResultSet} *by name*.
+	 *
+	 * @param statement The callable statement.
+	 * @param name The parameter name (for drivers which support named parameters).
+	 */
+	public void registerRefCursorParameter(CallableStatement statement, String name);
+
+	/**
+	 * Given a callable statement previously processed by {@link #registerRefCursorParameter(java.sql.CallableStatement, int)},
+	 * extract the {@link java.sql.ResultSet}.
+	 *
+	 *
+	 * @param statement The callable statement.
+	 * @param position The bind position at which to register the output param.
+	 *
+	 * @return The extracted result set.
+	 */
+	public ResultSet getResultSet(CallableStatement statement, int position);
+
+	/**
+	 * Given a callable statement previously processed by {@link #registerRefCursorParameter(java.sql.CallableStatement, String)},
+	 * extract the {@link java.sql.ResultSet}.
+	 *
+	 * @param statement The callable statement.
+	 * @param name The parameter name (for drivers which support named parameters).
+	 *
+	 * @return The extracted result set.
+	 */
+	public ResultSet getResultSet(CallableStatement statement, String name);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
index f2c452c70c..0d3de31466 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
@@ -1,418 +1,445 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.dom4j.Node;
 
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.MutabilityPlan;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Convenience base class for {@link BasicType} implementations
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractStandardBasicType<T>
 		implements BasicType, StringRepresentableType<T>, XmlRepresentableType<T>, ProcedureParameterExtractionAware<T> {
 
 	private static final Size DEFAULT_SIZE = new Size( 19, 2, 255, Size.LobMultiplier.NONE ); // to match legacy behavior
 	private final Size dictatedSize = new Size();
 
 	private final SqlTypeDescriptor sqlTypeDescriptor;
 	private final JavaTypeDescriptor<T> javaTypeDescriptor;
 
 	public AbstractStandardBasicType(SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<T> javaTypeDescriptor) {
 		this.sqlTypeDescriptor = sqlTypeDescriptor;
 		this.javaTypeDescriptor = javaTypeDescriptor;
 	}
 
 	public T fromString(String string) {
 		return javaTypeDescriptor.fromString( string );
 	}
 
 	public String toString(T value) {
 		return javaTypeDescriptor.toString( value );
 	}
 
 	public T fromStringValue(String xml) throws HibernateException {
 		return fromString( xml );
 	}
 
 	public String toXMLString(T value, SessionFactoryImplementor factory) throws HibernateException {
 		return toString( value );
 	}
 
 	public T fromXMLString(String xml, Mapping factory) throws HibernateException {
 		return StringHelper.isEmpty( xml ) ? null : fromStringValue( xml );
 	}
 
 	protected MutabilityPlan<T> getMutabilityPlan() {
 		return javaTypeDescriptor.getMutabilityPlan();
 	}
 
 	protected T getReplacement(T original, T target, SessionImplementor session) {
 		if ( !isMutable() ) {
 			return original;
 		}
 		else if ( isEqual( original, target ) ) {
 			return original;
 		}
 		else {
 			return deepCopy( original );
 		}
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return value == null ? ArrayHelper.FALSE : ArrayHelper.TRUE;
 	}
 
 	public String[] getRegistrationKeys() {
 		return registerUnderJavaType()
 				? new String[] { getName(), javaTypeDescriptor.getJavaTypeClass().getName() }
 				: new String[] { getName() };
 	}
 
 	protected boolean registerUnderJavaType() {
 		return false;
 	}
 
 	protected static Size getDefaultSize() {
 		return DEFAULT_SIZE;
 	}
 
 	protected Size getDictatedSize() {
 		return dictatedSize;
 	}
 
 
 	// final implementations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public final JavaTypeDescriptor<T> getJavaTypeDescriptor() {
 		return javaTypeDescriptor;
 	}
 
 	public final SqlTypeDescriptor getSqlTypeDescriptor() {
 		return sqlTypeDescriptor;
 	}
 
 	public final Class getReturnedClass() {
 		return javaTypeDescriptor.getJavaTypeClass();
 	}
 
 	public final int getColumnSpan(Mapping mapping) throws MappingException {
 		return sqlTypes( mapping ).length;
 	}
 
 	public final int[] sqlTypes(Mapping mapping) throws MappingException {
 		return new int[] { sqlTypeDescriptor.getSqlType() };
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return new Size[] { getDictatedSize() };
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return new Size[] { getDefaultSize() };
 	}
 
 	public final boolean isAssociationType() {
 		return false;
 	}
 
 	public final boolean isCollectionType() {
 		return false;
 	}
 
 	public final boolean isComponentType() {
 		return false;
 	}
 
 	public final boolean isEntityType() {
 		return false;
 	}
 
 	public final boolean isAnyType() {
 		return false;
 	}
 
 	public final boolean isXMLElement() {
 		return false;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isSame(Object x, Object y) {
 		return isEqual( x, y );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
 		return isEqual( x, y );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(Object one, Object another) {
 		return javaTypeDescriptor.areEqual( (T) one, (T) another );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final int getHashCode(Object x) {
 		return javaTypeDescriptor.extractHashCode( (T) x );
 	}
 
 	public final int getHashCode(Object x, SessionFactoryImplementor factory) {
 		return getHashCode( x );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final int compare(Object x, Object y) {
 		return javaTypeDescriptor.getComparator().compare( (T) x, (T) y );
 	}
 
 	public final boolean isDirty(Object old, Object current, SessionImplementor session) {
 		return isDirty( old, current );
 	}
 
 	public final boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return checkable[0] && isDirty( old, current );
 	}
 
 	protected final boolean isDirty(Object old, Object current) {
 		return !isSame( old, current );
 	}
 
 	public final boolean isModified(
 			Object oldHydratedState,
 			Object currentState,
 			boolean[] checkable,
 			SessionImplementor session) {
 		return isDirty( oldHydratedState, currentState );
 	}
 
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws SQLException {
 		return nullSafeGet( rs, names[0], session );
 	}
 
 	public final Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws SQLException {
 		return nullSafeGet( rs, name, session );
 	}
 
 	public final T nullSafeGet(ResultSet rs, String name, final SessionImplementor session) throws SQLException {
 		// todo : have SessionImplementor extend WrapperOptions
 		final WrapperOptions options = new WrapperOptions() {
 			public boolean useStreamForLobBinding() {
 				return Environment.useStreamsForBinary();
 			}
 
 			public LobCreator getLobCreator() {
 				return Hibernate.getLobCreator( session );
 			}
 
 			public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 				final SqlTypeDescriptor remapped = sqlTypeDescriptor.canBeRemapped()
 						? session.getFactory().getDialect().remapSqlTypeDescriptor( sqlTypeDescriptor )
 						: sqlTypeDescriptor;
 				return remapped == null ? sqlTypeDescriptor : remapped;
 			}
 		};
 
 		return nullSafeGet( rs, name, options );
 	}
 
 	protected final T nullSafeGet(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 		return remapSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract( rs, name, options );
 	}
 
 	public Object get(ResultSet rs, String name, SessionImplementor session) throws HibernateException, SQLException {
 		return nullSafeGet( rs, name, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			final SessionImplementor session) throws SQLException {
 		// todo : have SessionImplementor extend WrapperOptions
 		final WrapperOptions options = new WrapperOptions() {
 			public boolean useStreamForLobBinding() {
 				return Environment.useStreamsForBinary();
 			}
 
 			public LobCreator getLobCreator() {
 				return Hibernate.getLobCreator( session );
 			}
 
 			public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 				final SqlTypeDescriptor remapped = sqlTypeDescriptor.canBeRemapped()
 						? session.getFactory().getDialect().remapSqlTypeDescriptor( sqlTypeDescriptor )
 						: sqlTypeDescriptor;
 				return remapped == null ? sqlTypeDescriptor : remapped;
 			}
 		};
 
 		nullSafeSet( st, value, index, options );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected final void nullSafeSet(PreparedStatement st, Object value, int index, WrapperOptions options) throws SQLException {
 		remapSqlTypeDescriptor( options ).getBinder( javaTypeDescriptor ).bind( st, ( T ) value, index, options );
 	}
 
 	protected SqlTypeDescriptor remapSqlTypeDescriptor(WrapperOptions options) {
 		return options.remapSqlTypeDescriptor( sqlTypeDescriptor );
 	}
 
 	public void set(PreparedStatement st, T value, int index, SessionImplementor session) throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		return javaTypeDescriptor.extractLoggableRepresentation( (T) value );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) {
 		node.setText( toString( (T) value ) );
 	}
 
 	public final Object fromXMLNode(Node xml, Mapping factory) {
 		return fromString( xml.getText() );
 	}
 
 	public final boolean isMutable() {
 		return getMutabilityPlan().isMutable();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return deepCopy( (T) value );
 	}
 
 	protected final T deepCopy(T value) {
 		return getMutabilityPlan().deepCopy( value );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return getMutabilityPlan().disassemble( (T) value );
 	}
 
 	public final Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
 		return getMutabilityPlan().assemble( cached );
 	}
 
 	public final void beforeAssemble(Serializable cached, SessionImplementor session) {
 	}
 
 	public final Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return nullSafeGet(rs, names, session, owner);
 	}
 
 	public final Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return value;
 	}
 
 	public final Object semiResolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return value;
 	}
 
 	public final Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache) {
 		return getReplacement( (T) original, (T) target, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache,
 			ForeignKeyDirection foreignKeyDirection) {
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT == foreignKeyDirection
 				? getReplacement( (T) original, (T) target, session )
 				: target;
 	}
 
 	@Override
 	public boolean canDoExtraction() {
 		return true;
 	}
 
 	@Override
 	public T extract(CallableStatement statement, int startIndex, final SessionImplementor session) throws SQLException {
 		// todo : have SessionImplementor extend WrapperOptions
 		final WrapperOptions options = new WrapperOptions() {
 			public boolean useStreamForLobBinding() {
 				return Environment.useStreamsForBinary();
 			}
 
 			public LobCreator getLobCreator() {
 				return Hibernate.getLobCreator( session );
 			}
 
 			public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 				final SqlTypeDescriptor remapped = sqlTypeDescriptor.canBeRemapped()
 						? session.getFactory().getDialect().remapSqlTypeDescriptor( sqlTypeDescriptor )
 						: sqlTypeDescriptor;
 				return remapped == null ? sqlTypeDescriptor : remapped;
 			}
 		};
 
-		return remapSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract( statement, startIndex, options );
+		return remapSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract(
+				statement,
+				startIndex,
+				options
+		);
+	}
+
+	@Override
+	public T extract(CallableStatement statement, String[] paramNames, final SessionImplementor session) throws SQLException {
+		// todo : have SessionImplementor extend WrapperOptions
+		final WrapperOptions options = new WrapperOptions() {
+			public boolean useStreamForLobBinding() {
+				return Environment.useStreamsForBinary();
+			}
+
+			public LobCreator getLobCreator() {
+				return Hibernate.getLobCreator( session );
+			}
+
+			public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
+				final SqlTypeDescriptor remapped = sqlTypeDescriptor.canBeRemapped()
+						? session.getFactory().getDialect().remapSqlTypeDescriptor( sqlTypeDescriptor )
+						: sqlTypeDescriptor;
+				return remapped == null ? sqlTypeDescriptor : remapped;
+			}
+		};
+
+		return remapSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract( statement, paramNames, options );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
index 9c69f10b61..1a4295cc30 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
@@ -1,775 +1,805 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.tuple.component.ComponentTuplizer;
 
 /**
  * Handles "component" mappings
  *
  * @author Gavin King
  */
 public class ComponentType extends AbstractType implements CompositeType, ProcedureParameterExtractionAware {
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyNullability;
 	protected final int propertySpan;
 	private final CascadeStyle[] cascade;
 	private final FetchMode[] joinedFetch;
 	private final boolean isKey;
 
 	protected final EntityMode entityMode;
 	protected final ComponentTuplizer componentTuplizer;
 
 	public ComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
 		this.typeScope = typeScope;
 		// for now, just "re-flatten" the metamodel since this is temporary stuff anyway (HHH-1907)
 		this.isKey = metamodel.isKey();
 		this.propertySpan = metamodel.getPropertySpan();
 		this.propertyNames = new String[ propertySpan ];
 		this.propertyTypes = new Type[ propertySpan ];
 		this.propertyNullability = new boolean[ propertySpan ];
 		this.cascade = new CascadeStyle[ propertySpan ];
 		this.joinedFetch = new FetchMode[ propertySpan ];
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			StandardProperty prop = metamodel.getProperty( i );
 			this.propertyNames[i] = prop.getName();
 			this.propertyTypes[i] = prop.getType();
 			this.propertyNullability[i] = prop.isNullable();
 			this.cascade[i] = prop.getCascadeStyle();
 			this.joinedFetch[i] = prop.getFetchMode();
 		}
 
 		this.entityMode = metamodel.getEntityMode();
 		this.componentTuplizer = metamodel.getComponentTuplizer();
 	}
 
 	public boolean isKey() {
 		return isKey;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	public ComponentTuplizer getComponentTuplizer() {
 		return componentTuplizer;
 	}
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		int span = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			span += propertyTypes[i].getColumnSpan( mapping );
 		}
 		return span;
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		int[] sqlTypes = new int[getColumnSpan( mapping )];
 		int n = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int[] subtypes = propertyTypes[i].sqlTypes( mapping );
 			for ( int j = 0; j < subtypes.length; j++ ) {
 				sqlTypes[n++] = subtypes[j];
 			}
 		}
 		return sqlTypes;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
 		int soFar = 0;
 		for ( Type propertyType : propertyTypes ) {
 			final Size[] propertySizes = propertyType.dictatedSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
 		int soFar = 0;
 		for ( Type propertyType : propertyTypes ) {
 			final Size[] propertySizes = propertyType.defaultSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 
 	@Override
     public final boolean isComponentType() {
 		return true;
 	}
 
 	public Class getReturnedClass() {
 		return componentTuplizer.getMappedClass();
 	}
 
 	@Override
     public boolean isSame(Object x, Object y) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isSame( xvalues[i], yvalues[i] ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public boolean isEqual(Object x, Object y)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i] ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i], factory ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public int compare(Object x, Object y) {
 		if ( x == y ) {
 			return 0;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int propertyCompare = propertyTypes[i].compare( xvalues[i], yvalues[i] );
 			if ( propertyCompare != 0 ) {
 				return propertyCompare;
 			}
 		}
 		return 0;
 	}
 
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	@Override
     public int getHashCode(Object x) {
 		int result = 17;
 		Object[] values = getPropertyValues( x, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = values[i];
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y );
 			}
 		}
 		return result;
 	}
 
 	@Override
     public int getHashCode(Object x, SessionFactoryImplementor factory) {
 		int result = 17;
 		Object[] values = getPropertyValues( x, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = values[i];
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y, factory );
 			}
 		}
 		return result;
 	}
 
 	@Override
     public boolean isDirty(Object x, Object y, SessionImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < xvalues.length; i++ ) {
 			if ( propertyTypes[i].isDirty( xvalues[i], yvalues[i], session ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public boolean isDirty(Object x, Object y, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		int loc = 0;
 		for ( int i = 0; i < xvalues.length; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len <= 1 ) {
 				final boolean dirty = ( len == 0 || checkable[loc] ) &&
 				                      propertyTypes[i].isDirty( xvalues[i], yvalues[i], session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			else {
 				boolean[] subcheckable = new boolean[len];
 				System.arraycopy( checkable, loc, subcheckable, 0, len );
 				final boolean dirty = propertyTypes[i].isDirty( xvalues[i], yvalues[i], subcheckable, session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			loc += len;
 		}
 		return false;
 	}
 
 	@Override
     public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 
 		if ( current == null ) {
 			return old != null;
 		}
 		if ( old == null ) {
 			return current != null;
 		}
 		Object[] currentValues = getPropertyValues( current, session );
 		Object[] oldValues = ( Object[] ) old;
 		int loc = 0;
 		for ( int i = 0; i < currentValues.length; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			boolean[] subcheckable = new boolean[len];
 			System.arraycopy( checkable, loc, subcheckable, 0, len );
 			if ( propertyTypes[i].isModified( oldValues[i], currentValues[i], subcheckable, session ) ) {
 				return true;
 			}
 			loc += len;
 		}
 		return false;
 
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( hydrate( rs, names, session, owner ), session, owner );
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int begin, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 			begin += propertyTypes[i].getColumnSpan( session.getFactory() );
 		}
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int begin,
 			boolean[] settable,
 			SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len == 0 ) {
 				//noop
 			}
 			else if ( len == 1 ) {
 				if ( settable[loc] ) {
 					propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 					begin++;
 				}
 			}
 			else {
 				boolean[] subsettable = new boolean[len];
 				System.arraycopy( settable, loc, subsettable, 0, len );
 				propertyTypes[i].nullSafeSet( st, subvalues[i], begin, subsettable, session );
 				begin += ArrayHelper.countTrue( subsettable );
 			}
 			loc += len;
 		}
 	}
 
 	private Object[] nullSafeGetValues(Object value, EntityMode entityMode) throws HibernateException {
 		if ( value == null ) {
 			return new Object[propertySpan];
 		}
 		else {
 			return getPropertyValues( value, entityMode );
 		}
 	}
 
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	public Object getPropertyValue(Object component, int i, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValue( component, i, entityMode );
 	}
 
 	public Object getPropertyValue(Object component, int i, EntityMode entityMode)
 			throws HibernateException {
 		return componentTuplizer.getPropertyValue( component, i );
 	}
 
 	public Object[] getPropertyValues(Object component, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValues( component, entityMode );
 	}
 
 	public Object[] getPropertyValues(Object component, EntityMode entityMode)
 			throws HibernateException {
 		return componentTuplizer.getPropertyValues( component );
 	}
 
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
 			throws HibernateException {
 		componentTuplizer.setPropertyValues( component, values );
 	}
 
 	public Type[] getSubtypes() {
 		return propertyTypes;
 	}
 
 	public String getName() {
 		return "component" + ArrayHelper.toString( propertyNames );
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		Map result = new HashMap();
 		if ( entityMode == null ) {
 			throw new ClassCastException( value.getClass().getName() );
 		}
 		Object[] values = getPropertyValues( value, entityMode );
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			result.put( propertyNames[i], propertyTypes[i].toLoggableString( values[i], factory ) );
 		}
 		return StringHelper.unqualify( getName() ) + result.toString();
 	}
 
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	public Object deepCopy(Object component, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( component == null ) {
 			return null;
 		}
 
 		Object[] values = getPropertyValues( component, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			values[i] = propertyTypes[i].deepCopy( values[i], factory );
 		}
 
 		Object result = instantiate( entityMode );
 		setPropertyValues( result, values, entityMode );
 
 		//not absolutely necessary, but helps for some
 		//equals()/hashCode() implementations
 		if ( componentTuplizer.hasParentProperty() ) {
 			componentTuplizer.setParent( result, componentTuplizer.getParent( component ), factory );
 		}
 
 		return result;
 	}
 
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache)
 			throws HibernateException {
 
 		if ( original == null ) {
 			return null;
 		}
 		//if ( original == target ) return target;
 
 		final Object result = target == null
 				? instantiate( owner, session )
 				: target;
 
 		Object[] values = TypeHelper.replace(
 				getPropertyValues( original, entityMode ),
 				getPropertyValues( result, entityMode ),
 				propertyTypes,
 				session,
 				owner,
 				copyCache
 		);
 
 		setPropertyValues( result, values, entityMode );
 		return result;
 	}
 
 	@Override
     public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache,
 			ForeignKeyDirection foreignKeyDirection)
 			throws HibernateException {
 
 		if ( original == null ) {
 			return null;
 		}
 		//if ( original == target ) return target;
 
 		final Object result = target == null ?
 				instantiate( owner, session ) :
 				target;
 
 		Object[] values = TypeHelper.replace(
 				getPropertyValues( original, entityMode ),
 				getPropertyValues( result, entityMode ),
 				propertyTypes,
 				session,
 				owner,
 				copyCache,
 				foreignKeyDirection
 		);
 
 		setPropertyValues( result, values, entityMode );
 		return result;
 	}
 
 	/**
 	 * This method does not populate the component parent
 	 */
 	public Object instantiate(EntityMode entityMode) throws HibernateException {
 		return componentTuplizer.instantiate();
 	}
 
 	public Object instantiate(Object parent, SessionImplementor session)
 			throws HibernateException {
 
 		Object result = instantiate( entityMode );
 
 		if ( componentTuplizer.hasParentProperty() && parent != null ) {
 			componentTuplizer.setParent(
 					result,
 					session.getPersistenceContext().proxyFor( parent ),
 					session.getFactory()
 			);
 		}
 
 		return result;
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return cascade[i];
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	@Override
     public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			Object[] values = getPropertyValues( value, entityMode );
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				values[i] = propertyTypes[i].disassemble( values[i], session, owner );
 			}
 			return values;
 		}
 	}
 
 	@Override
     public Object assemble(Serializable object, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( object == null ) {
 			return null;
 		}
 		else {
 			Object[] values = ( Object[] ) object;
 			Object[] assembled = new Object[values.length];
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				assembled[i] = propertyTypes[i].assemble( ( Serializable ) values[i], session, owner );
 			}
 			Object result = instantiate( owner, session );
 			setPropertyValues( result, assembled, entityMode );
 			return result;
 		}
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return joinedFetch[i];
 	}
 
 	@Override
     public Object hydrate(
 			final ResultSet rs,
 			final String[] names,
 			final SessionImplementor session,
 			final Object owner)
 			throws HibernateException, SQLException {
 
 		int begin = 0;
 		boolean notNull = false;
 		Object[] values = new Object[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int length = propertyTypes[i].getColumnSpan( session.getFactory() );
 			String[] range = ArrayHelper.slice( names, begin, length ); //cache this
 			Object val = propertyTypes[i].hydrate( rs, range, session, owner );
 			if ( val == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[i] = val;
 			begin += length;
 		}
 
 		return notNull ? values : null;
 	}
 
 	@Override
     public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value != null ) {
 			Object result = instantiate( owner, session );
 			Object[] values = ( Object[] ) value;
 			Object[] resolvedValues = new Object[values.length]; //only really need new array during semiresolve!
 			for ( int i = 0; i < values.length; i++ ) {
 				resolvedValues[i] = propertyTypes[i].resolve( values[i], session, owner );
 			}
 			setPropertyValues( result, resolvedValues, entityMode );
 			return result;
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
     public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//note that this implementation is kinda broken
 		//for components with many-to-one associations
 		return resolve( value, session, owner );
 	}
 
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	@Override
     public boolean isXMLElement() {
 		return true;
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		replaceNode( node, ( Element ) value );
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value == null ) {
 			return result;
 		}
 		Object[] values = getPropertyValues( value, EntityMode.POJO ); //TODO!!!!!!!
 		int loc = 0;
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			boolean[] propertyNullness = propertyTypes[i].toColumnNullness( values[i], mapping );
 			System.arraycopy( propertyNullness, 0, result, loc, propertyNullness.length );
 			loc += propertyNullness.length;
 		}
 		return result;
 	}
 
 	public boolean isEmbedded() {
 		return false;
 	}
 
 	public int getPropertyIndex(String name) {
 		String[] names = getPropertyNames();
 		for ( int i = 0, max = names.length; i < max; i++ ) {
 			if ( names[i].equals( name ) ) {
 				return i;
 			}
 		}
 		throw new PropertyNotFoundException(
 				"Unable to locate property named " + name + " on " + getReturnedClass().getName()
 		);
 	}
 
 	private Boolean canDoExtraction;
 
 	@Override
 	public boolean canDoExtraction() {
 		if ( canDoExtraction == null ) {
 			canDoExtraction = determineIfProcedureParamExtractionCanBePerformed();
 		}
 		return canDoExtraction;
 	}
 
 	private boolean determineIfProcedureParamExtractionCanBePerformed() {
 		for ( Type propertyType : propertyTypes ) {
 			if ( ! ProcedureParameterExtractionAware.class.isInstance( propertyType ) ) {
 				return false;
 			}
 			if ( ! ( (ProcedureParameterExtractionAware) propertyType ).canDoExtraction() ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException {
 		Object[] values = new Object[propertySpan];
 
 		int currentIndex = startIndex;
 		boolean notNull = false;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			// we know this cast is safe from canDoExtraction
 			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[i];
 			final Object value = propertyType.extract( statement, currentIndex, session );
 			if ( value == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[i] = value;
 			currentIndex += propertyType.getColumnSpan( session.getFactory() );
 		}
 
 		if ( ! notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
+
+	@Override
+	public Object extract(CallableStatement statement, String[] paramNames, SessionImplementor session) throws SQLException {
+		// for this form to work all sub-property spans must be one (1)...
+
+		Object[] values = new Object[propertySpan];
+
+		int indx = 0;
+		boolean notNull = false;
+		for ( String paramName : paramNames ) {
+			// we know this cast is safe from canDoExtraction
+			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[indx];
+			final Object value = propertyType.extract( statement, new String[] { paramName }, session );
+			if ( value == null ) {
+				if ( isKey ) {
+					return null; //different nullability rules for pk/fk
+				}
+			}
+			else {
+				notNull = true;
+			}
+			values[indx] = value;
+		}
+
+		if ( ! notNull ) {
+			values = null;
+		}
+
+		return resolve( values, session, null );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java b/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java
index e199d8881a..dfe639cd46 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java
@@ -1,96 +1,101 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.UUID;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BasicExtractor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Specialized type mapping for {@link UUID} and the Postgres UUID data type (which is mapped as OTHER in its
  * JDBC driver).
  *
  * @author Steve Ebersole
  * @author David Driscoll
  */
 public class PostgresUUIDType extends AbstractSingleColumnStandardBasicType<UUID> {
 	public static final PostgresUUIDType INSTANCE = new PostgresUUIDType();
 
 	public PostgresUUIDType() {
 		super( PostgresUUIDSqlTypeDescriptor.INSTANCE, UUIDTypeDescriptor.INSTANCE );
 	}
 
 	public String getName() {
 		return "pg-uuid";
 	}
 
 	public static class PostgresUUIDSqlTypeDescriptor implements SqlTypeDescriptor {
 		public static final PostgresUUIDSqlTypeDescriptor INSTANCE = new PostgresUUIDSqlTypeDescriptor();
 
 		public int getSqlType() {
 			// ugh
 			return Types.OTHER;
 		}
 
 		@Override
 		public boolean canBeRemapped() {
 			return true;
 		}
 
 		public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 					st.setObject( index, javaTypeDescriptor.unwrap( value, UUID.class, options ), getSqlType() );
 				}
 			};
 		}
 
 		public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 			return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 				@Override
 				protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 					return javaTypeDescriptor.wrap( rs.getObject( name ), options );
 				}
 
 				@Override
 				protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 					return javaTypeDescriptor.wrap( statement.getObject( index ), options );
 				}
+
+				@Override
+				protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+					return javaTypeDescriptor.wrap( statement.getObject( name ), options );
+				}
 			};
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ProcedureParameterExtractionAware.java b/hibernate-core/src/main/java/org/hibernate/type/ProcedureParameterExtractionAware.java
index d4530cbf07..7357a42563 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ProcedureParameterExtractionAware.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ProcedureParameterExtractionAware.java
@@ -1,58 +1,72 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type;
 
 import java.sql.CallableStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Optional {@link Type} contract for implementations that are aware of how to extract values from
  * store procedure OUT/INOUT parameters.
  *
  * @author Steve Ebersole
  */
 public interface ProcedureParameterExtractionAware<T> extends Type {
 	/**
 	 * Can the given instance of this type actually perform the parameter value extractions?
 	 *
 	 * @return {@code true} indicates that @{link #extract} calls will not fail due to {@link IllegalStateException}.
 	 */
 	public boolean canDoExtraction();
 
 	/**
 	 * Perform the extraction
 	 *
 	 * @param statement The CallableStatement from which to extract the parameter value(s).
 	 * @param startIndex The parameter index from which to start extracting; assumes the values (if multiple) are contiguous
 	 * @param session The originating session
 	 *
 	 * @return The extracted value.
 	 *
 	 * @throws SQLException Indicates an issue calling into the CallableStatement
 	 * @throws IllegalStateException Thrown if this method is called on instances that return {@code false} for {@link #canDoExtraction}
 	 */
 	public T extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException;
+
+	/**
+	 * Perform the extraction
+	 *
+	 * @param statement The CallableStatement from which to extract the parameter value(s).
+	 * @param paramNames The parameter names.
+	 * @param session The originating session
+	 *
+	 * @return The extracted value.
+	 *
+	 * @throws SQLException Indicates an issue calling into the CallableStatement
+	 * @throws IllegalStateException Thrown if this method is called on instances that return {@code false} for {@link #canDoExtraction}
+	 */
+	public T extract(CallableStatement statement, String[] paramNames, SessionImplementor session) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java
index 4e99135566..cc8ea1d627 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java
@@ -1,50 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 /**
  * Contract for extracting value via JDBC (from {@link ResultSet} or as output param from {@link CallableStatement}).
  *
  * @author Steve Ebersole
  */
 public interface ValueExtractor<X> {
 	/**
 	 * Extract value from result set
 	 *
 	 * @param rs The result set from which to extract the value
 	 * @param name The name by which to extract the value from the result set
 	 * @param options The options
 	 *
 	 * @return The extracted value
 	 *
 	 * @throws SQLException Indicates a JDBC error occurred.
 	 */
 	public X extract(ResultSet rs, String name, WrapperOptions options) throws SQLException;
 
 	public X extract(CallableStatement rs, int index, WrapperOptions options) throws SQLException;
+
+	public X extract(CallableStatement statement, String[] paramNames, WrapperOptions options) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
index 792f39d2f8..7f0f939db9 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
@@ -1,123 +1,158 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Convenience base implementation of {@link org.hibernate.type.descriptor.ValueExtractor}
  *
  * @author Steve Ebersole
  */
 public abstract class BasicExtractor<J> implements ValueExtractor<J> {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, BasicExtractor.class.getName() );
 
 	private final JavaTypeDescriptor<J> javaDescriptor;
 	private final SqlTypeDescriptor sqlDescriptor;
 
 	public BasicExtractor(JavaTypeDescriptor<J> javaDescriptor, SqlTypeDescriptor sqlDescriptor) {
 		this.javaDescriptor = javaDescriptor;
 		this.sqlDescriptor = sqlDescriptor;
 	}
 
 	public JavaTypeDescriptor<J> getJavaDescriptor() {
 		return javaDescriptor;
 	}
 
 	public SqlTypeDescriptor getSqlDescriptor() {
 		return sqlDescriptor;
 	}
 
 	@Override
 	public J extract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 		final J value = doExtract( rs, name, options );
 		if ( value == null || rs.wasNull() ) {
 			LOG.tracev( "Found [null] as column [{0}]", name );
 			return null;
 		}
 		else {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Found [{0}] as column [{1}]", getJavaDescriptor().extractLoggableRepresentation( value ), name );
 			}
 			return value;
 		}
 	}
 
 	/**
 	 * Perform the extraction.
 	 * <p/>
 	 * Called from {@link #extract}.  Null checking of the value (as well as consulting {@link ResultSet#wasNull}) is
 	 * done there.
 	 *
 	 * @param rs The result set
 	 * @param name The value name in the result set
 	 * @param options The binding options
 	 *
 	 * @return The extracted value.
 	 *
 	 * @throws SQLException Indicates a problem access the result set
 	 */
 	protected abstract J doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException;
 
 	@Override
 	public J extract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 		final J value = doExtract( statement, index, options );
 		if ( value == null || statement.wasNull() ) {
 			LOG.tracev( "Found [null] as procedure output  parameter [{0}]", index );
 			return null;
 		}
 		else {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Found [{0}] as procedure output parameter [{1}]", getJavaDescriptor().extractLoggableRepresentation( value ), index );
 			}
 			return value;
 		}
 	}
 
 	/**
 	 * Perform the extraction.
 	 * <p/>
 	 * Called from {@link #extract}.  Null checking of the value (as well as consulting {@link ResultSet#wasNull}) is
 	 * done there.
 	 *
 	 * @param statement The callable statement containing the output parameter
 	 * @param index The index (position) of the output parameter
 	 * @param options The binding options
 	 *
 	 * @return The extracted value.
 	 *
 	 * @throws SQLException Indicates a problem accessing the parameter value
 	 */
 	protected abstract J doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException;
+
+	@Override
+	public J extract(CallableStatement statement, String[] paramNames, WrapperOptions options) throws SQLException {
+		if ( paramNames.length > 1 ) {
+			throw new IllegalArgumentException( "Basic value extraction cannot handle multiple output parameters" );
+		}
+		final String paramName = paramNames[0];
+		final J value = doExtract( statement, paramName, options );
+		if ( value == null || statement.wasNull() ) {
+			LOG.tracev( "Found [null] as procedure output  parameter [{0}]", paramName );
+			return null;
+		}
+		else {
+			if ( LOG.isTraceEnabled() ) {
+				LOG.tracev( "Found [{0}] as procedure output parameter [{1}]", getJavaDescriptor().extractLoggableRepresentation( value ), paramName );
+			}
+			return value;
+		}
+	}
+
+	/**
+	 * Perform the extraction.
+	 * <p/>
+	 * Called from {@link #extract}.  Null checking of the value (as well as consulting {@link ResultSet#wasNull}) is
+	 * done there.
+	 *
+	 * @param statement The callable statement containing the output parameter
+	 * @param name The output parameter name
+	 * @param options The binding options
+	 *
+	 * @return The extracted value.
+	 *
+	 * @throws SQLException Indicates a problem accessing the parameter value
+	 */
+	protected abstract J doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
index ff2080f645..34189657dc 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
@@ -1,83 +1,88 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#BIGINT BIGINT} handling.
  *
  * @author Steve Ebersole
  */
 public class BigIntTypeDescriptor implements SqlTypeDescriptor {
 	public static final BigIntTypeDescriptor INSTANCE = new BigIntTypeDescriptor();
 
 	public BigIntTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.BIGINT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setLong( index, javaTypeDescriptor.unwrap( value, Long.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getLong( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getLong( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getLong( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
index 5111c21e2f..b729e18a93 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
@@ -1,85 +1,90 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#BIT BIT} handling.
  * <p/>
  * Note that JDBC is very specific about its use of the type BIT to mean a single binary digit, whereas
  * SQL defines BIT having a parameterized length.
  *
  * @author Steve Ebersole
  */
 public class BitTypeDescriptor implements SqlTypeDescriptor {
 	public static final BitTypeDescriptor INSTANCE = new BitTypeDescriptor();
 
 	public BitTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	public int getSqlType() {
 		return Types.BIT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBoolean( index, javaTypeDescriptor.unwrap( value, Boolean.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBoolean( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBoolean( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBoolean( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
index 2f6280c328..5a60c0a9a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
@@ -1,149 +1,154 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.Blob;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.BinaryStream;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#BLOB BLOB} handling.
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public abstract class BlobTypeDescriptor implements SqlTypeDescriptor {
 
 	private BlobTypeDescriptor() {
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.BLOB;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBlob( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBlob( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBlob( name ), options );
+			}
 		};
 	}
 
 	protected abstract <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
 
 	public <X> BasicBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return getBlobBinder( javaTypeDescriptor );
 	}
 
 	public static final BlobTypeDescriptor DEFAULT =
 			new BlobTypeDescriptor() {
 				{
 					SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 				}
 
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 							if ( options.useStreamForLobBinding() ) {
 								STREAM_BINDING.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 							else if ( byte[].class.isInstance( value ) ) {
 								// performance shortcut for binding BLOB data in byte[] format
 								PRIMITIVE_ARRAY_BINDING.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 							else {
 								BLOB_BINDING.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 						}
 					};
 				}
 			};
 
 	public static final BlobTypeDescriptor PRIMITIVE_ARRAY_BINDING =
 			new BlobTypeDescriptor() {
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						public void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							st.setBytes( index, javaTypeDescriptor.unwrap( value, byte[].class, options ) );
 						}
 					};
 				}
 			};
 
 	public static final BlobTypeDescriptor BLOB_BINDING =
 			new BlobTypeDescriptor() {
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							st.setBlob( index, javaTypeDescriptor.unwrap( value, Blob.class, options ) );
 						}
 					};
 				}
 			};
 
 	public static final BlobTypeDescriptor STREAM_BINDING =
 			new BlobTypeDescriptor() {
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							final BinaryStream binaryStream = javaTypeDescriptor.unwrap( value, BinaryStream.class, options );
 							st.setBinaryStream( index, binaryStream.getInputStream(), binaryStream.getLength() );
 						}
 					};
 				}
 			};
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java
index a9cb2b889c..0b1a6802f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java
@@ -1,80 +1,85 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link java.sql.Types#BOOLEAN BOOLEAN} handling.
  *
  * @author Steve Ebersole
  */
 public class BooleanTypeDescriptor implements SqlTypeDescriptor {
 	public static final BooleanTypeDescriptor INSTANCE = new BooleanTypeDescriptor();
 
 	public BooleanTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	public int getSqlType() {
 		return Types.BOOLEAN;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBoolean( index, javaTypeDescriptor.unwrap( value, Boolean.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBoolean( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBoolean( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBoolean( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
index 2acadc8fbf..ac87c7038b 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
@@ -1,126 +1,131 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.Clob;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.CharacterStream;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#CLOB CLOB} handling.
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public abstract class ClobTypeDescriptor implements SqlTypeDescriptor {
 	@Override
 	public int getSqlType() {
 		return Types.CLOB;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getClob( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getClob( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getClob( name ), options );
+			}
 		};
 	}
 
 
 	protected abstract <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return getClobBinder( javaTypeDescriptor );
 	}
 
 	public static final ClobTypeDescriptor DEFAULT =
 			new ClobTypeDescriptor() {
 				{
 					SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 				}
 
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 							if ( options.useStreamForLobBinding() ) {
 								STREAM_BINDING.getClobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 							else {
 								CLOB_BINDING.getClobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 						}
 					};
 				}
 			};
 
 	public static final ClobTypeDescriptor CLOB_BINDING =
 			new ClobTypeDescriptor() {
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							st.setClob( index, javaTypeDescriptor.unwrap( value, Clob.class, options ) );
 						}
 					};
 				}
 			};
 
 	public static final ClobTypeDescriptor STREAM_BINDING =
 			new ClobTypeDescriptor() {
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							final CharacterStream characterStream = javaTypeDescriptor.unwrap( value, CharacterStream.class, options );
 							st.setCharacterStream( index, characterStream.getReader(), characterStream.getLength() );
 						}
 					};
 				}
 			};
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
index 6c64e7c9e3..a73189ea71 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
@@ -1,84 +1,89 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.Date;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#DATE DATE} handling.
  *
  * @author Steve Ebersole
  */
 public class DateTypeDescriptor implements SqlTypeDescriptor {
 	public static final DateTypeDescriptor INSTANCE = new DateTypeDescriptor();
 
 	public DateTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.DATE;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setDate( index, javaTypeDescriptor.unwrap( value, Date.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getDate( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getDate( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getDate( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
index a168100960..58a756f2cc 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
@@ -1,84 +1,89 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.math.BigDecimal;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#DECIMAL DECIMAL} handling.
  *
  * @author Steve Ebersole
  */
 public class DecimalTypeDescriptor implements SqlTypeDescriptor {
 	public static final DecimalTypeDescriptor INSTANCE = new DecimalTypeDescriptor();
 
 	public DecimalTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.DECIMAL;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBigDecimal( index, javaTypeDescriptor.unwrap( value, BigDecimal.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBigDecimal( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBigDecimal( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBigDecimal( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
index 0adf43cf3d..e16c11d262 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
@@ -1,83 +1,88 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#DOUBLE DOUBLE} handling.
  *
  * @author Steve Ebersole
  */
 public class DoubleTypeDescriptor implements SqlTypeDescriptor {
 	public static final DoubleTypeDescriptor INSTANCE = new DoubleTypeDescriptor();
 
 	public DoubleTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.DOUBLE;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setDouble( index, javaTypeDescriptor.unwrap( value, Double.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getDouble( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getDouble( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getDouble( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
index f93ed0c0ab..baa42023bf 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
@@ -1,83 +1,88 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#INTEGER INTEGER} handling.
  *
  * @author Steve Ebersole
  */
 public class IntegerTypeDescriptor implements SqlTypeDescriptor {
 	public static final IntegerTypeDescriptor INSTANCE = new IntegerTypeDescriptor();
 
 	public IntegerTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.INTEGER;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setInt( index, javaTypeDescriptor.unwrap( value, Integer.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getInt( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getInt( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getInt( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NClobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NClobTypeDescriptor.java
index 108927d929..6295c7d106 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NClobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NClobTypeDescriptor.java
@@ -1,125 +1,130 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.NClob;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.CharacterStream;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#NCLOB NCLOB} handling.
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public abstract class NClobTypeDescriptor implements SqlTypeDescriptor {
 	@Override
 	public int getSqlType() {
 		return Types.NCLOB;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getNClob( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getNClob( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getNClob( name ), options );
+			}
 		};
 	}
 
 
 	protected abstract <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return getClobBinder( javaTypeDescriptor );
 	}
 
 	public static final ClobTypeDescriptor DEFAULT =
 			new ClobTypeDescriptor() {
 				{
 					SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 				}
 
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 							if ( options.useStreamForLobBinding() ) {
 								STREAM_BINDING.getClobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 							else {
 								CLOB_BINDING.getClobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 						}
 					};
 				}
 			};
 
 	public static final ClobTypeDescriptor CLOB_BINDING =
 			new ClobTypeDescriptor() {
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							st.setNClob( index, javaTypeDescriptor.unwrap( value, NClob.class, options ) );
 						}
 					};
 				}
 			};
 
 	public static final ClobTypeDescriptor STREAM_BINDING =
 			new ClobTypeDescriptor() {
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							final CharacterStream characterStream = javaTypeDescriptor.unwrap( value, CharacterStream.class, options );
 							st.setCharacterStream( index, characterStream.getReader(), characterStream.getLength() );
 						}
 					};
 				}
 			};
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NVarcharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NVarcharTypeDescriptor.java
index d1dfe1a363..f29df7062a 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NVarcharTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NVarcharTypeDescriptor.java
@@ -1,83 +1,88 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#NVARCHAR NVARCHAR} handling.
  *
  * @author Steve Ebersole
  */
 public class NVarcharTypeDescriptor implements SqlTypeDescriptor {
 	public static final NVarcharTypeDescriptor INSTANCE = new NVarcharTypeDescriptor();
 
 	public NVarcharTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.NVARCHAR;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setNString( index, javaTypeDescriptor.unwrap( value, String.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getNString( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getNString( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getNString( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
index eb859d51ae..18c193d4bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
@@ -1,83 +1,88 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#REAL REAL} handling.
  *
  * @author Steve Ebersole
  */
 public class RealTypeDescriptor implements SqlTypeDescriptor {
 	public static final RealTypeDescriptor INSTANCE = new RealTypeDescriptor();
 
 	public RealTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.REAL;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setFloat( index, javaTypeDescriptor.unwrap( value, Float.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getFloat( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getFloat( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getFloat( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
index 454bcc2dcc..9e7e226e08 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
@@ -1,83 +1,88 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#SMALLINT SMALLINT} handling.
  *
  * @author Steve Ebersole
  */
 public class SmallIntTypeDescriptor implements SqlTypeDescriptor {
 	public static final SmallIntTypeDescriptor INSTANCE = new SmallIntTypeDescriptor();
 
 	public SmallIntTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.SMALLINT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setShort( index, javaTypeDescriptor.unwrap( value, Short.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getShort( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getShort( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getShort( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptorRegistry.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptorRegistry.java
index 6744c3c770..d9bceb0387 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptorRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptorRegistry.java
@@ -1,152 +1,156 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.type.descriptor.JdbcTypeNameMapper;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Basically a map from JDBC type code (int) -> {@link SqlTypeDescriptor}
  *
  * @author Steve Ebersole
  */
 public class SqlTypeDescriptorRegistry {
 	public static final SqlTypeDescriptorRegistry INSTANCE = new SqlTypeDescriptorRegistry();
 
 	private static final Logger log = Logger.getLogger( SqlTypeDescriptorRegistry.class );
 
 	private ConcurrentHashMap<Integer,SqlTypeDescriptor> descriptorMap = new ConcurrentHashMap<Integer, SqlTypeDescriptor>();
 
 	@SuppressWarnings("UnnecessaryBoxing")
 	public void addDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 		descriptorMap.put( Integer.valueOf( sqlTypeDescriptor.getSqlType() ), sqlTypeDescriptor );
 	}
 
 	@SuppressWarnings("UnnecessaryBoxing")
 	public SqlTypeDescriptor getDescriptor(int jdbcTypeCode) {
 		SqlTypeDescriptor descriptor = descriptorMap.get( Integer.valueOf( jdbcTypeCode ) );
 		if ( descriptor != null ) {
 			return descriptor;
 		}
 
 		if ( JdbcTypeNameMapper.isStandardTypeCode( jdbcTypeCode ) ) {
 			log.debugf(
 					"A standard JDBC type code [%s] was not defined in SqlTypeDescriptorRegistry",
 					jdbcTypeCode
 			);
 		}
 
 		// see if the typecode is part of a known type family...
 		JdbcTypeFamilyInformation.Family family = JdbcTypeFamilyInformation.INSTANCE.locateJdbcTypeFamilyByTypeCode( jdbcTypeCode );
 		if ( family != null ) {
 			for ( int potentialAlternateTypeCode : family.getTypeCodes() ) {
 				if ( potentialAlternateTypeCode != jdbcTypeCode ) {
 					final SqlTypeDescriptor potentialAlternateDescriptor = descriptorMap.get( Integer.valueOf( potentialAlternateTypeCode ) );
 					if ( potentialAlternateDescriptor != null ) {
 						// todo : add a SqlTypeDescriptor.canBeAssignedFrom method...
 						return potentialAlternateDescriptor;
 					}
 
 					if ( JdbcTypeNameMapper.isStandardTypeCode( potentialAlternateTypeCode ) ) {
 						log.debugf(
 								"A standard JDBC type code [%s] was not defined in SqlTypeDescriptorRegistry",
 								potentialAlternateTypeCode
 						);
 					}
 				}
 			}
 		}
 
 		// finally, create a new descriptor mapping to getObject/setObject for this type code...
 		final ObjectSqlTypeDescriptor fallBackDescriptor = new ObjectSqlTypeDescriptor( jdbcTypeCode );
 		addDescriptor( fallBackDescriptor );
 		return fallBackDescriptor;
 	}
 
 	public static class ObjectSqlTypeDescriptor implements SqlTypeDescriptor {
 		private final int jdbcTypeCode;
 
 		public ObjectSqlTypeDescriptor(int jdbcTypeCode) {
 			this.jdbcTypeCode = jdbcTypeCode;
 		}
 
 		@Override
 		public int getSqlType() {
 			return jdbcTypeCode;
 		}
 
 		@Override
 		public boolean canBeRemapped() {
 			return true;
 		}
 
 		@Override
 		public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			if ( Serializable.class.isAssignableFrom( javaTypeDescriptor.getJavaTypeClass() ) ) {
 				return VarbinaryTypeDescriptor.INSTANCE.getBinder( javaTypeDescriptor );
 			}
 
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 						throws SQLException {
 					st.setObject( index, value, jdbcTypeCode );
 				}
 			};
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public ValueExtractor getExtractor(JavaTypeDescriptor javaTypeDescriptor) {
 			if ( Serializable.class.isAssignableFrom( javaTypeDescriptor.getJavaTypeClass() ) ) {
 				return VarbinaryTypeDescriptor.INSTANCE.getExtractor( javaTypeDescriptor );
 			}
 
 			return new BasicExtractor( javaTypeDescriptor, this ) {
 				@Override
 				protected Object doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 					return rs.getObject( name );
 				}
 
 				@Override
-				protected Object doExtract(CallableStatement statement, int index, WrapperOptions options)
-						throws SQLException {
+				protected Object doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 					return statement.getObject( index );
 				}
+
+				@Override
+				protected Object doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+					return statement.getObject( name );
+				}
 			};
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
index 5a279d8177..d14aeb4a09 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
@@ -1,84 +1,89 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Time;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#TIME TIME} handling.
  *
  * @author Steve Ebersole
  */
 public class TimeTypeDescriptor implements SqlTypeDescriptor {
 	public static final TimeTypeDescriptor INSTANCE = new TimeTypeDescriptor();
 
 	public TimeTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.TIME;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setTime( index, javaTypeDescriptor.unwrap( value, Time.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getTime( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getTime( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getTime( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
index bf36bd66f2..e71d7f17ba 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
@@ -1,84 +1,89 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Timestamp;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#TIMESTAMP TIMESTAMP} handling.
  *
  * @author Steve Ebersole
  */
 public class TimestampTypeDescriptor implements SqlTypeDescriptor {
 	public static final TimestampTypeDescriptor INSTANCE = new TimestampTypeDescriptor();
 
 	public TimestampTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.TIMESTAMP;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setTimestamp( index, javaTypeDescriptor.unwrap( value, Timestamp.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getTimestamp( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getTimestamp( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getTimestamp( name ), options );
+			}
 		};
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
index a5955c73f0..ec202f10c0 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
@@ -1,86 +1,91 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#TINYINT TINYINT} handling.
  * <p/>
  * Note that <tt>JDBC</tt> states that TINYINT should be mapped to either byte or short, but points out
  * that using byte can in fact lead to loss of data.
  *
  * @author Steve Ebersole
  */
 public class TinyIntTypeDescriptor implements SqlTypeDescriptor {
 	public static final TinyIntTypeDescriptor INSTANCE = new TinyIntTypeDescriptor();
 
 	public TinyIntTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.TINYINT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setByte( index, javaTypeDescriptor.unwrap( value, Byte.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getByte( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getByte( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getByte( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
index 8ed51fd617..1d070b363e 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
@@ -1,80 +1,85 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#VARBINARY VARBINARY} handling.
  *
  * @author Steve Ebersole
  */
 public class VarbinaryTypeDescriptor implements SqlTypeDescriptor {
 	public static final VarbinaryTypeDescriptor INSTANCE = new VarbinaryTypeDescriptor();
 
 	public VarbinaryTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	public int getSqlType() {
 		return Types.VARBINARY;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBytes( index, javaTypeDescriptor.unwrap( value, byte[].class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBytes( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBytes( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBytes( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
index edb98150d9..5b5221b147 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
@@ -1,83 +1,88 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#VARCHAR VARCHAR} handling.
  *
  * @author Steve Ebersole
  */
 public class VarcharTypeDescriptor implements SqlTypeDescriptor {
 	public static final VarcharTypeDescriptor INSTANCE = new VarcharTypeDescriptor();
 
 	public VarcharTypeDescriptor() {
 		SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.VARCHAR;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setString( index, javaTypeDescriptor.unwrap( value, String.class, options ) );
 			}
 		};
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getString( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getString( index ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getString( name ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java b/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java
index a7b4017613..a84e7dcf29 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java
@@ -1,153 +1,163 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.test.common;
 
 import java.util.Collections;
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.internal.ResultSetWrapperImpl;
 import org.hibernate.engine.jdbc.internal.TypeInfo;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.Stoppable;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 
 
 /**
  * Implementation of the {@link JdbcServices} contract for use by these
  * tests.
  *
  * @author Steve Ebersole
  */
 public class BasicTestingJdbcServiceImpl implements JdbcServices {
 	private ConnectionProvider connectionProvider;
 	private Dialect dialect;
 	private SqlStatementLogger sqlStatementLogger;
 	private SqlExceptionHelper exceptionHelper;
 	private final ExtractedDatabaseMetaData metaDataSupport = new MetaDataSupportImpl();
 	private final ResultSetWrapper resultSetWrapper = ResultSetWrapperImpl.INSTANCE;
 
 	public void start() {
 	}
 
 	public void stop() {
 		release();
 	}
 
 	public void prepare(boolean allowAggressiveRelease) {
 		connectionProvider = ConnectionProviderBuilder.buildConnectionProvider( allowAggressiveRelease );
 		dialect = ConnectionProviderBuilder.getCorrespondingDialect();
 		sqlStatementLogger = new SqlStatementLogger( true, false );
 		exceptionHelper = new SqlExceptionHelper();
 
 	}
 
 	public void release() {
 		if ( connectionProvider instanceof Stoppable ) {
 			( (Stoppable) connectionProvider ).stop();
 		}
 	}
 
 	public ConnectionProvider getConnectionProvider() {
 		return connectionProvider;
 	}
 
 	public Dialect getDialect() {
 		return dialect;
 	}
 
 	public LobCreator getLobCreator(LobCreationContext lobCreationContext) {
 		return null;
 	}
 
 	public ResultSetWrapper getResultSetWrapper() {
 		return null;
 	}
 
 	public SqlStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
 	public SqlExceptionHelper getSqlExceptionHelper() {
 		return exceptionHelper;
 	}
 
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
 		return metaDataSupport;
 	}
 
 	private static class MetaDataSupportImpl implements ExtractedDatabaseMetaData {
+		@Override
+		public boolean supportsRefCursors() {
+			return false;
+		}
+
+		@Override
+		public boolean supportsNamedParameters() {
+			return false;
+		}
+
 		public boolean supportsScrollableResults() {
 			return false;
 		}
 
 		public boolean supportsGetGeneratedKeys() {
 			return false;
 		}
 
 		public boolean supportsBatchUpdates() {
 			return false;
 		}
 
 		public boolean supportsDataDefinitionInTransaction() {
 			return false;
 		}
 
 		public boolean doesDataDefinitionCauseTransactionCommit() {
 			return false;
 		}
 
 		public Set<String> getExtraKeywords() {
 			return Collections.emptySet();
 		}
 
 		public SQLStateType getSqlStateType() {
 			return SQLStateType.UNKOWN;
 		}
 
 		public boolean doesLobLocatorUpdateCopy() {
 			return false;
 		}
 
 		public String getConnectionSchemaName() {
 			return null;
 		}
 
 		public String getConnectionCatalogName() {
 			return null;
 		}
 
 		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
 			return null;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/typeoverride/StoredPrefixedStringType.java b/hibernate-core/src/test/java/org/hibernate/test/typeoverride/StoredPrefixedStringType.java
index 322a777a5b..18601dd2eb 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/typeoverride/StoredPrefixedStringType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/typeoverride/StoredPrefixedStringType.java
@@ -1,118 +1,127 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.test.typeoverride;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.StringType;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BasicExtractor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;
 
 /**
  *
  * @author Gail Badner
  */
 public class StoredPrefixedStringType
 		extends AbstractSingleColumnStandardBasicType<String>
 		implements DiscriminatorType<String> {
 
 	public static final String PREFIX = "PRE:";
 
 	public static final SqlTypeDescriptor PREFIXED_VARCHAR_TYPE_DESCRIPTOR =
 			new VarcharTypeDescriptor() {
 				public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 							String stringValue = javaTypeDescriptor.unwrap( value, String.class, options );
 							st.setString( index, PREFIX + stringValue );
 						}
 					};
 				}
 
 				public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 							String stringValue = rs.getString( name );
 							if ( ! stringValue.startsWith( PREFIX ) ) {
 								throw new AssertionFailure( "Value read from resultset does not have prefix." );
 							}
 							return javaTypeDescriptor.wrap( stringValue.substring( PREFIX.length() ), options );
 						}
 
 						@Override
 						protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
 								throws SQLException {
 							String stringValue = statement.getString( index );
 							if ( ! stringValue.startsWith( PREFIX ) ) {
 								throw new AssertionFailure( "Value read from procedure output param does not have prefix." );
 							}
 							return javaTypeDescriptor.wrap( stringValue.substring( PREFIX.length() ), options );
 						}
+
+						@Override
+						protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+							String stringValue = statement.getString( name );
+							if ( ! stringValue.startsWith( PREFIX ) ) {
+								throw new AssertionFailure( "Value read from procedure output param does not have prefix." );
+							}
+							return javaTypeDescriptor.wrap( stringValue.substring( PREFIX.length() ), options );
+						}
 					};
 				}
 			};
 
 
 	public static final StoredPrefixedStringType INSTANCE = new StoredPrefixedStringType();
 
 	public StoredPrefixedStringType() {
 		super( PREFIXED_VARCHAR_TYPE_DESCRIPTOR, StringType.INSTANCE.getJavaTypeDescriptor() );
 	}
 
 	public String getName() {
 		return StringType.INSTANCE.getName();
 	}
 
 	@Override
 	protected boolean registerUnderJavaType() {
 		return true;
 	}
 
 	public String objectToSQLString(String value, Dialect dialect) throws Exception {
 		return StringType.INSTANCE.objectToSQLString( value, dialect );
 	}
 
 	public String stringToObject(String xml) throws Exception {
 		return StringType.INSTANCE.stringToObject( xml );
 	}
 
 	public String toString(String value) {
 		return StringType.INSTANCE.toString( value );
 	}
 }
\ No newline at end of file
