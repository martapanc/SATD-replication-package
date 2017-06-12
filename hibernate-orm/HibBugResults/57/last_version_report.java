@Override
  public String sqlConstraintString(
    Dialect dialect,
    String constraintName,
    String defaultCatalog,
    String defaultSchema) {
  StringBuffer buf = new StringBuffer(
      dialect.getAddUniqueConstraintString( constraintName )
  ).append( '(' );
  Iterator iter = getColumnIterator();
  boolean nullable = false;
  while ( iter.hasNext() ) {
    Column column = (Column) iter.next();
    if ( !nullable && column.isNullable() ) nullable = true;
    buf.append( column.getQuotedName( dialect ) );
    if ( iter.hasNext() ) buf.append( ", " );
  }
  return !nullable || dialect.supportsNotNullUnique() ? buf.append( ')' ).toString() : null;
}
