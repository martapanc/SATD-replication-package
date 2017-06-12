File path: code/core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
Comment: ODO: copy/paste from recreate()
Initial commit id: d8d6d82e
Final commit id: 129c0f13
   Bugs between [       0]:

   Bugs after [      41]:
c893577efc HHH-5393 : MappingException when @MapKeyColumn refers to a column mapped in embeddable map value
253820a289 HHH-10874 - @Where annotation is not processed with "Extra-lazy" loading for bidirectional collections
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
3571218183 HHH-10133 - CatalogSeparator of dialect metadata not used in runtime, just in schema tool
1376b12ca9 HHH-10073 Not propagating XML node names configured in mappings
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups
7308e14fed HHH-9803 - Checkstyle fix ups
288f490418 HHH-9761 - Make native APIs typed; HHH-9790 - Remove deprecated methods from Session and SessionFactory
b476094d43 HHH-9747 - Import initial reworking of transaction handling (based on JdbcSession work)
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
460e966214 HHH-9455 : Unnecessary select count query in some cases
420296fd26 HHH-9204 HHH-9205 : Restore AbstractCollectionPersister.doProcessQueuedOps() removed by HHH-9078; deprecate method added by HHH-9078
1ec115c0d6 HHH-9078 correct OrderColumn indexes for inverse, extra lazy collections
8fe5460ec0 HHH-8741 - More checkstyle cleanups
cd590470c0 HHH-8741 - More checkstyle cleanups
8ec17e68e7 HHH-8741 - More checkstyle cleanups
ed4fafeb50 HHH-8722 HHH-8723 : Reorg AbstractLoadPlanBuildingAssociationVisitationStrategy and add Any support
9938937fe7 HHH-8637 - Downcasting with TREAT operator should also filter results by the specified Type
c607e30051 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
18079f346d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC) - Initial reworking to remove SQL references (for reuse in Search, OGM, etc) and to split out conceptual "from clause" and "select clause" into different structures (see QuerySpaces)
dc7cdf9d88 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
bcd6185809 HHH-4910 The collection cache is evicted if a related collection element is inserted, removed or updated
f1f8600b54 HHH-8083 @OrderColumn not updated on @OneToMany cascade
b846fa35b5 HHH-7841 - Redesign Loader
043d618c03 HHH-7841 - Redesign Loader
6a388b754c HHH-8159 - Apply fixups indicated by analysis tools
ba1b02ed22 HHH-7841 - Redesign Loader
3d332371bd HHH-7841 - Redesign Loader
a102bf2c31 HHH-7841 - Redesign Loader
dc193c32c5 HHH-7984 Handle Oracle statements on release
bdca6dc1e1 HHH-5732 @OrderColumn not updated if @OneToMany has mappedby defined
9ce5c32dd7 HHH-7902 Replace JDBC proxies with a set of contracts/helpers
7cecc68fb1 HHH-1775
30ea167c41 HHH-7359 Added a new method to MessageHelper to intelligently handle property-ref issues in logging.  Rolled it out to as many MessageHelper users as possible.
5327ac5396 HHH-7545 : Aliases for a collection key and element column can collide causing one to be excluded
05dcc209ae HHH-2394  Implemented @SqlFragmentAlias
dbff4c1839 HHH-2394 Got filters working on sub-classes.
d51a0d0c78 HHH-4394 - @OrderBy usage on a joined classes (when using join table) produces incorred SQL syntax.
6c7379c38f HHH-6817 Logging of strings containing the percent character broken
bec88716d6 HHH-6791 tiny improvement, in favor of java auto-box instead of create new instance

Start block index: 1305
End block index: 1402
	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
			throws HibernateException {

		if ( !isInverse && isRowInsertEnabled() ) {

			if ( log.isDebugEnabled() ) {
				log.debug( 
						"Inserting rows of collection: " + 
						MessageHelper.collectionInfoString( this, id, getFactory() ) 
					);
			}

			try {
				//insert all the new entries
				collection.preInsert( this );
				Iterator entries = collection.entries( this );
				Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
				boolean callable = isInsertCallable();
				boolean useBatch = expectation.canBeBatched();
				String sql = getSQLInsertRowString();
				int i = 0;
				int count = 0;
				while ( entries.hasNext() ) {
					int offset = 1;
					Object entry = entries.next();
					PreparedStatement st = null;
					if ( collection.needsInserting( entry, i, elementType ) ) {

						if ( useBatch ) {
							if ( st == null ) {
								if ( callable ) {
									st = session.getBatcher().prepareBatchCallableStatement( sql );
								}
								else {
									st = session.getBatcher().prepareBatchStatement( sql );
								}
							}
						}
						else {
							if ( callable ) {
								st = session.getBatcher().prepareCallableStatement( sql );
							}
							else {
								st = session.getBatcher().prepareStatement( sql );
							}
						}

						try {
							offset += expectation.prepare( st );
							//TODO: copy/paste from recreate()
							offset = writeKey( st, id, offset, session );
							if ( hasIdentifier ) {
								offset = writeIdentifier( st, collection.getIdentifier(entry, i), offset, session );
							}
							if ( hasIndex /*&& !indexIsFormula*/ ) {
								offset = writeIndex( st, collection.getIndex(entry, i, this), offset, session );
							}
							writeElement(st, collection.getElement(entry), offset, session );

							if ( useBatch ) {
								session.getBatcher().addToBatch( expectation );
							}
							else {
								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
							}
							collection.afterRowInsert( this, entry, i );
							count++;
						}
						catch ( SQLException sqle ) {
							if ( useBatch ) {
								session.getBatcher().abortBatch( sqle );
							}
							throw sqle;
						}
						finally {
							if ( !useBatch ) {
								session.getBatcher().closeStatement( st );
							}
						}
					}
					i++;
				}
				if ( log.isDebugEnabled() ) {
					log.debug( "done inserting rows: " + count + " inserted" );
				}
			}
			catch ( SQLException sqle ) {
				throw JDBCExceptionHelper.convert(
				        sqlExceptionConverter,
				        sqle,
				        "could not insert collection rows: " + 
				        MessageHelper.collectionInfoString( this, id, getFactory() ),
				        getSQLInsertRowString()
					);
			}

		}
	}