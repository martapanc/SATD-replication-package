File path: code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
Comment: TBD should this be varbinary($1)?
Initial commit id: d8d6d82e
Final commit id: 5fc70fc5
   Bugs between [       0]:

   Bugs after [      10]:
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
aa3f913857 HHH-11194 - Add setting to allow enabling legacy 4.x LimitHandler behavior (removed delegation).
eec01edcca HHH-10876 - DefaultIdentifierGeneratorFactory does not consider the hibernate.id.new_generator_mappings setting
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
2731fe541a HHH-100084 - Refactor Identity Column support methods into IdentityColumnSupport interface
11ae0f72c8 HHH-9166 handle nested exceptions with TemplatedViolatedConstraintNameExtracter
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
ae43670290 HHH-9724 - More complete "temp table" coverage and allow Dialects to influence which strategy is used
06b6135a11 HHH-9724 - More complete "temp table" coverage and allow Dialects to influence which strategy is used - initial work
4615ae1018 - HHH-9324: Avoids creation of LimitHandler instances for every query.

Start block index: 200
End block index: 360
	protected final void commonRegistration() {
		// Note: For object <-> SQL datatype mappings see:
		//	 Configuration Manager | Advanced | SQL | System DDL Datatype Mappings
		//
		//	TBD	registerColumnType(Types.BINARY,        "binary($1)");
		// changed 08-11-2005, jsl
		registerColumnType( Types.BINARY, "varbinary($1)" );
		registerColumnType( Types.BIGINT, "BigInt" );
		registerColumnType( Types.BIT, "bit" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.DECIMAL, "decimal" );
		registerColumnType( Types.DOUBLE, "double" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );	// binary %Stream
		registerColumnType( Types.LONGVARCHAR, "longvarchar" );		// character %Stream
		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
		registerColumnType( Types.REAL, "real" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TINYINT, "tinyint" );
		// TBD should this be varbinary($1)?
		//		registerColumnType(Types.VARBINARY,     "binary($1)");
		registerColumnType( Types.VARBINARY, "longvarbinary" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.BLOB, "longvarbinary" );
		registerColumnType( Types.CLOB, "longvarchar" );

		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
		//getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);

		getDefaultProperties().setProperty( Environment.USE_SQL_COMMENTS, "false" );

		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
		registerFunction( "acos", new StandardJDBCEscapeFunction( "acos", Hibernate.DOUBLE ) );
		registerFunction( "%alphaup", new StandardSQLFunction( "%alphaup", Hibernate.STRING ) );
		registerFunction( "ascii", new StandardSQLFunction( "ascii", Hibernate.STRING ) );
		registerFunction( "asin", new StandardJDBCEscapeFunction( "asin", Hibernate.DOUBLE ) );
		registerFunction( "atan", new StandardJDBCEscapeFunction( "atan", Hibernate.DOUBLE ) );
		registerFunction( "bit_length", new SQLFunctionTemplate( Hibernate.INTEGER, "($length(?1)*8)" ) );
		// hibernate impelemnts cast in Dialect.java
		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", Hibernate.INTEGER ) );
		registerFunction( "char", new StandardJDBCEscapeFunction( "char", Hibernate.CHARACTER ) );
		registerFunction( "character_length", new StandardSQLFunction( "character_length", Hibernate.INTEGER ) );
		registerFunction( "char_length", new StandardSQLFunction( "char_length", Hibernate.INTEGER ) );
		registerFunction( "cos", new StandardJDBCEscapeFunction( "cos", Hibernate.DOUBLE ) );
		registerFunction( "cot", new StandardJDBCEscapeFunction( "cot", Hibernate.DOUBLE ) );
		registerFunction( "coalesce", new VarArgsSQLFunction( "coalesce(", ",", ")" ) );
		registerFunction( "concat", new VarArgsSQLFunction( Hibernate.STRING, "", "||", "" ) );
		registerFunction( "convert", new ConvertFunction() );
		registerFunction( "curdate", new StandardJDBCEscapeFunction( "curdate", Hibernate.DATE ) );
		registerFunction( "current_date", new NoArgSQLFunction( "current_date", Hibernate.DATE, false ) );
		registerFunction( "current_time", new NoArgSQLFunction( "current_time", Hibernate.TIME, false ) );
		registerFunction(
				"current_timestamp", new ConditionalParenthesisFunction( "current_timestamp", Hibernate.TIMESTAMP )
		);
		registerFunction( "curtime", new StandardJDBCEscapeFunction( "curtime", Hibernate.TIME ) );
		registerFunction( "database", new StandardJDBCEscapeFunction( "database", Hibernate.STRING ) );
		registerFunction( "dateadd", new VarArgsSQLFunction( Hibernate.TIMESTAMP, "dateadd(", ",", ")" ) );
		registerFunction( "datediff", new VarArgsSQLFunction( Hibernate.INTEGER, "datediff(", ",", ")" ) );
		registerFunction( "datename", new VarArgsSQLFunction( Hibernate.STRING, "datename(", ",", ")" ) );
		registerFunction( "datepart", new VarArgsSQLFunction( Hibernate.INTEGER, "datepart(", ",", ")" ) );
		registerFunction( "day", new StandardSQLFunction( "day", Hibernate.INTEGER ) );
		registerFunction( "dayname", new StandardJDBCEscapeFunction( "dayname", Hibernate.STRING ) );
		registerFunction( "dayofmonth", new StandardJDBCEscapeFunction( "dayofmonth", Hibernate.INTEGER ) );
		registerFunction( "dayofweek", new StandardJDBCEscapeFunction( "dayofweek", Hibernate.INTEGER ) );
		registerFunction( "dayofyear", new StandardJDBCEscapeFunction( "dayofyear", Hibernate.INTEGER ) );
		// is it necessary to register %exact since it can only appear in a where clause?
		registerFunction( "%exact", new StandardSQLFunction( "%exact", Hibernate.STRING ) );
		registerFunction( "exp", new StandardJDBCEscapeFunction( "exp", Hibernate.DOUBLE ) );
		registerFunction( "%external", new StandardSQLFunction( "%external", Hibernate.STRING ) );
		registerFunction( "$extract", new VarArgsSQLFunction( Hibernate.INTEGER, "$extract(", ",", ")" ) );
		registerFunction( "$find", new VarArgsSQLFunction( Hibernate.INTEGER, "$find(", ",", ")" ) );
		registerFunction( "floor", new StandardSQLFunction( "floor", Hibernate.INTEGER ) );
		registerFunction( "getdate", new StandardSQLFunction( "getdate", Hibernate.TIMESTAMP ) );
		registerFunction( "hour", new StandardJDBCEscapeFunction( "hour", Hibernate.INTEGER ) );
		registerFunction( "ifnull", new VarArgsSQLFunction( "ifnull(", ",", ")" ) );
		registerFunction( "%internal", new StandardSQLFunction( "%internal" ) );
		registerFunction( "isnull", new VarArgsSQLFunction( "isnull(", ",", ")" ) );
		registerFunction( "isnumeric", new StandardSQLFunction( "isnumeric", Hibernate.INTEGER ) );
		registerFunction( "lcase", new StandardJDBCEscapeFunction( "lcase", Hibernate.STRING ) );
		registerFunction( "left", new StandardJDBCEscapeFunction( "left", Hibernate.STRING ) );
		registerFunction( "len", new StandardSQLFunction( "len", Hibernate.INTEGER ) );
		registerFunction( "length", new StandardSQLFunction( "length", Hibernate.INTEGER ) );
		registerFunction( "$length", new VarArgsSQLFunction( "$length(", ",", ")" ) );
		// aggregate functions shouldn't be registered, right?
		//registerFunction( "list", new StandardSQLFunction("list",Hibernate.STRING) );
		// stopped on $list
		registerFunction( "$list", new VarArgsSQLFunction( "$list(", ",", ")" ) );
		registerFunction( "$listdata", new VarArgsSQLFunction( "$listdata(", ",", ")" ) );
		registerFunction( "$listfind", new VarArgsSQLFunction( "$listfind(", ",", ")" ) );
		registerFunction( "$listget", new VarArgsSQLFunction( "$listget(", ",", ")" ) );
		registerFunction( "$listlength", new StandardSQLFunction( "$listlength", Hibernate.INTEGER ) );
		registerFunction( "locate", new StandardSQLFunction( "$FIND", Hibernate.INTEGER ) );
		registerFunction( "log", new StandardJDBCEscapeFunction( "log", Hibernate.DOUBLE ) );
		registerFunction( "log10", new StandardJDBCEscapeFunction( "log", Hibernate.DOUBLE ) );
		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
		registerFunction( "minute", new StandardJDBCEscapeFunction( "minute", Hibernate.INTEGER ) );
		registerFunction( "mod", new StandardJDBCEscapeFunction( "mod", Hibernate.DOUBLE ) );
		registerFunction( "month", new StandardJDBCEscapeFunction( "month", Hibernate.INTEGER ) );
		registerFunction( "monthname", new StandardJDBCEscapeFunction( "monthname", Hibernate.STRING ) );
		registerFunction( "now", new StandardJDBCEscapeFunction( "monthname", Hibernate.TIMESTAMP ) );
		registerFunction( "nullif", new VarArgsSQLFunction( "nullif(", ",", ")" ) );
		registerFunction( "nvl", new NvlFunction() );
		registerFunction( "%odbcin", new StandardSQLFunction( "%odbcin" ) );
		registerFunction( "%odbcout", new StandardSQLFunction( "%odbcin" ) );
		registerFunction( "%pattern", new VarArgsSQLFunction( Hibernate.STRING, "", "%pattern", "" ) );
		registerFunction( "pi", new StandardJDBCEscapeFunction( "pi", Hibernate.DOUBLE ) );
		registerFunction( "$piece", new VarArgsSQLFunction( Hibernate.STRING, "$piece(", ",", ")" ) );
		registerFunction( "position", new VarArgsSQLFunction( Hibernate.INTEGER, "position(", " in ", ")" ) );
		registerFunction( "power", new VarArgsSQLFunction( Hibernate.STRING, "power(", ",", ")" ) );
		registerFunction( "quarter", new StandardJDBCEscapeFunction( "quarter", Hibernate.INTEGER ) );
		registerFunction( "repeat", new VarArgsSQLFunction( Hibernate.STRING, "repeat(", ",", ")" ) );
		registerFunction( "replicate", new VarArgsSQLFunction( Hibernate.STRING, "replicate(", ",", ")" ) );
		registerFunction( "right", new StandardJDBCEscapeFunction( "right", Hibernate.STRING ) );
		registerFunction( "round", new VarArgsSQLFunction( Hibernate.FLOAT, "round(", ",", ")" ) );
		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", Hibernate.STRING ) );
		registerFunction( "second", new StandardJDBCEscapeFunction( "second", Hibernate.INTEGER ) );
		registerFunction( "sign", new StandardSQLFunction( "sign", Hibernate.INTEGER ) );
		registerFunction( "sin", new StandardJDBCEscapeFunction( "sin", Hibernate.DOUBLE ) );
		registerFunction( "space", new StandardSQLFunction( "space", Hibernate.STRING ) );
		registerFunction( "%sqlstring", new VarArgsSQLFunction( Hibernate.STRING, "%sqlstring(", ",", ")" ) );
		registerFunction( "%sqlupper", new VarArgsSQLFunction( Hibernate.STRING, "%sqlupper(", ",", ")" ) );
		registerFunction( "sqrt", new StandardJDBCEscapeFunction( "SQRT", Hibernate.DOUBLE ) );
		registerFunction( "%startswith", new VarArgsSQLFunction( Hibernate.STRING, "", "%startswith", "" ) );
		// below is for Cache' that don't have str in 2007.1 there is str and we register str directly
		registerFunction( "str", new SQLFunctionTemplate( Hibernate.STRING, "cast(?1 as char varying)" ) );
		registerFunction( "string", new VarArgsSQLFunction( Hibernate.STRING, "string(", ",", ")" ) );
		// note that %string is deprecated
		registerFunction( "%string", new VarArgsSQLFunction( Hibernate.STRING, "%string(", ",", ")" ) );
		registerFunction( "substr", new VarArgsSQLFunction( Hibernate.STRING, "substr(", ",", ")" ) );
		registerFunction( "substring", new VarArgsSQLFunction( Hibernate.STRING, "substring(", ",", ")" ) );
		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", Hibernate.TIMESTAMP, false ) );
		registerFunction( "tan", new StandardJDBCEscapeFunction( "tan", Hibernate.DOUBLE ) );
		registerFunction( "timestampadd", new StandardJDBCEscapeFunction( "timestampadd", Hibernate.DOUBLE ) );
		registerFunction( "timestampdiff", new StandardJDBCEscapeFunction( "timestampdiff", Hibernate.DOUBLE ) );
		registerFunction( "tochar", new VarArgsSQLFunction( Hibernate.STRING, "tochar(", ",", ")" ) );
		registerFunction( "to_char", new VarArgsSQLFunction( Hibernate.STRING, "to_char(", ",", ")" ) );
		registerFunction( "todate", new VarArgsSQLFunction( Hibernate.STRING, "todate(", ",", ")" ) );
		registerFunction( "to_date", new VarArgsSQLFunction( Hibernate.STRING, "todate(", ",", ")" ) );
		registerFunction( "tonumber", new StandardSQLFunction( "tonumber" ) );
		registerFunction( "to_number", new StandardSQLFunction( "tonumber" ) );
		// TRIM(end_keyword string-expression-1 FROM string-expression-2)
		// use Hibernate implementation "From" is one of the parameters they pass in position ?3
		//registerFunction( "trim", new SQLFunctionTemplate(Hibernate.STRING, "trim(?1 ?2 from ?3)") );
		registerFunction( "truncate", new StandardJDBCEscapeFunction( "truncate", Hibernate.STRING ) );
		registerFunction( "ucase", new StandardJDBCEscapeFunction( "ucase", Hibernate.STRING ) );
		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
		// %upper is deprecated
		registerFunction( "%upper", new StandardSQLFunction( "%upper" ) );
		registerFunction( "user", new StandardJDBCEscapeFunction( "user", Hibernate.STRING ) );
		registerFunction( "week", new StandardJDBCEscapeFunction( "user", Hibernate.INTEGER ) );
		registerFunction( "xmlconcat", new VarArgsSQLFunction( Hibernate.STRING, "xmlconcat(", ",", ")" ) );
		registerFunction( "xmlelement", new VarArgsSQLFunction( Hibernate.STRING, "xmlelement(", ",", ")" ) );
		// xmlforest requires a new kind of function constructor
		registerFunction( "year", new StandardJDBCEscapeFunction( "year", Hibernate.INTEGER ) );
	}
