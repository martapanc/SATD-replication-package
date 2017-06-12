	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
			throws HibernateException {
		List<SchemaUpdateScript> scripts = generateSchemaUpdateScriptList( dialect, databaseMetadata );
		return SchemaUpdateScript.toStringArray( scripts );
	}
