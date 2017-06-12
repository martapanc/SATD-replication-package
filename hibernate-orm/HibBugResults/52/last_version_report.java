	public void setName(String name) {
        /* Envers passes 'name' parameter wrapped with '`' signs if quotation required. Set 'quoted' property accordingly. */
		if (
			name.charAt(0)=='`' ||
			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1
		) {
			quoted=true;
			this.name=name.substring( 1, name.length()-1 );
		}
		else {
			this.name = name;
		}
	}
