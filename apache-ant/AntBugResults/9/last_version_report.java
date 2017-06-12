    public void execute() throws BuildException {
        Vector savedTransaction = (Vector) transactions.clone();
        String savedSqlCommand = sqlCommand;

        sqlCommand = sqlCommand.trim();

        try {
            if (srcFile == null && sqlCommand.length() == 0 
                && filesets.isEmpty()) { 
                if (transactions.size() == 0) {
                    throw new BuildException("Source file or fileset, "
                                             + "transactions or sql statement "
                                             + "must be set!", location);
                }
            }
        
           	if (srcFile != null && !srcFile.exists()) {
		   	 	throw new BuildException("Source file does not exist!", location);
			}

            // deal with the filesets
            for (int i = 0; i < filesets.size(); i++) {
                FileSet fs = (FileSet) filesets.elementAt(i);
                DirectoryScanner ds = fs.getDirectoryScanner(project);
                File srcDir = fs.getDir(project);
                
                String[] srcFiles = ds.getIncludedFiles();
                
                // Make a transaction for each file
                for (int j = 0 ; j < srcFiles.length ; j++) {
                    Transaction t = createTransaction();
                    t.setSrc(new File(srcDir, srcFiles[j]));
                }
            }
            
            // Make a transaction group for the outer command
            Transaction t = createTransaction();
            t.setSrc(srcFile);
            t.addText(sqlCommand);
			conn = getConnection();
			if (!isValidRdbms(conn)) {
				return;
			}
            try {
                statement = conn.createStatement();

            
                PrintStream out = System.out;
                try {
                    if (output != null) {
                        log("Opening PrintStream to output file " + output, 
                            Project.MSG_VERBOSE);
                        out = new PrintStream(
                                  new BufferedOutputStream(
                                      new FileOutputStream(output
                                                           .getAbsolutePath(),
                                                           append)));
                    }
                    
                    // Process all transactions
                    for (Enumeration e = transactions.elements(); 
                         e.hasMoreElements();) {
                       
                        ((Transaction) e.nextElement()).runTransaction(out);
                        if (!isAutocommit()) {
                            log("Commiting transaction", Project.MSG_VERBOSE);
                            conn.commit();
                        }
                    }
                } finally {
                    if (out != null && out != System.out) {
                        out.close();
                    }
                } 
            } catch (IOException e){
                if (!isAutocommit() && conn != null && onError.equals("abort")) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {}
                }
                throw new BuildException(e, location);
            } catch (SQLException e){
                if (!isAutocommit() && conn != null && onError.equals("abort")) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {}
                }
                throw new BuildException(e, location);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {}
            }
            
            log(goodSql + " of " + totalSql + 
                " SQL statements executed successfully");
        } finally {
            transactions = savedTransaction;
            sqlCommand = savedSqlCommand;
        }
    }
