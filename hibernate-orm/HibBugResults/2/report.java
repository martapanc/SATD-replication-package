File path: code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
Comment: O: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 1007
End block index: 1098
	public static void bindColumns(final Element node, final SimpleValue simpleValue,
			final boolean isNullable, final boolean autoColumn, final String propertyPath,
			final Mappings mappings) throws MappingException {

		Table table = simpleValue.getTable();

		// COLUMN(S)
		Attribute columnAttribute = node.attribute( "column" );
		if ( columnAttribute == null ) {
			Iterator iter = node.elementIterator();
			int count = 0;
			while ( iter.hasNext() ) {
				Element columnElement = (Element) iter.next();
				if ( columnElement.getName().equals( "column" ) ) {
					Column column = new Column();
					column.setValue( simpleValue );
					column.setTypeIndex( count++ );
					bindColumn( columnElement, column, isNullable );
					String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
							columnElement.attributeValue( "name" ), propertyPath
					);
					column.setName( mappings.getNamingStrategy().columnName(
						logicalColumnName ) );
					if ( table != null ) {
						table.addColumn( column ); // table=null -> an association
						                           // - fill it in later
						//TODO fill in the mappings for table == null
						mappings.addColumnBinding( logicalColumnName, column, table );
					}


					simpleValue.addColumn( column );
					// column index
					bindIndex( columnElement.attribute( "index" ), table, column, mappings );
					bindIndex( node.attribute( "index" ), table, column, mappings );
					//column unique-key
					bindUniqueKey( columnElement.attribute( "unique-key" ), table, column, mappings );
					bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
				}
				else if ( columnElement.getName().equals( "formula" ) ) {
					Formula formula = new Formula();
					formula.setFormula( columnElement.getText() );
					simpleValue.addFormula( formula );
				}
			}
		}
		else {
			if ( node.elementIterator( "column" ).hasNext() ) {
				throw new MappingException(
					"column attribute may not be used together with <column> subelement" );
			}
			if ( node.elementIterator( "formula" ).hasNext() ) {
				throw new MappingException(
					"column attribute may not be used together with <formula> subelement" );
			}

			Column column = new Column();
			column.setValue( simpleValue );
			bindColumn( node, column, isNullable );
			String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
					columnAttribute.getValue(), propertyPath
			);
			column.setName( mappings.getNamingStrategy().columnName( logicalColumnName ) );
			if ( table != null ) {
				table.addColumn( column ); // table=null -> an association - fill
				                           // it in later
				//TODO fill in the mappings for table == null
				mappings.addColumnBinding( logicalColumnName, column, table );
			}
			simpleValue.addColumn( column );
			bindIndex( node.attribute( "index" ), table, column, mappings );
			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
		}

		if ( autoColumn && simpleValue.getColumnSpan() == 0 ) {
			Column column = new Column();
			column.setValue( simpleValue );
			bindColumn( node, column, isNullable );
			column.setName( mappings.getNamingStrategy().propertyToColumnName( propertyPath ) );
			String logicalName = mappings.getNamingStrategy().logicalColumnName( null, propertyPath );
			mappings.addColumnBinding( logicalName, column, table );
			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
			 * slightly higer level in the stack (to get all the information we need)
			 * Right now HbmBinder does not support the
			 */
			simpleValue.getTable().addColumn( column );
			simpleValue.addColumn( column );
			bindIndex( node.attribute( "index" ), table, column, mappings );
			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
		}

	}
