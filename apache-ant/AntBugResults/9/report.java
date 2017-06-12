File path: src/main/org/apache/tools/ant/taskdefs/SQLExec.java
Comment: check first that it is not already loaded otherwise
Initial commit id: 672481d0
Final commit id: 600b5034
   Bugs between [       0]:

   Bugs after [      21]:
7dbdd6081 outputencoding for sql, patch by Miroslav Zaťko.  PR 39541
4e4a35935 rowcountproperty for <sql>.  PR 40923
cfb2e0108 errorproperty and warningproperty for <sql>.  Submitted by Andrew Stevens.  PR 38807
2f46b6af9 most likely fix PR 46480
f97926a10 Allow more control over CSV output.  PR 35627.
eaffd9d71 allow SQLWarnings to stop <sql>.  PR 41836.
1a8500873 new showWarnings attribute that makes <sql> display SQLWarnings - if any.  PR 41836
e1f227ae3 work on PR 26459 - in progress (fighting with the tests and my environment)
352396620 Allow ant to continue even if <sql> fails to connect to the database.  PR 36712.
543148ca1 fix resultset und update count logic in sql.  PRs 32168 and 36265.
72e471f0b Allow subclasses to modify the connection and statement instances used by <sql> or to access the cached instances.  PR 27178.  Submitted by Mike Davis
02b3a7d13 sql's onerror='stop' shouldn't make the task fail.  PR 24668.  Based on a patch by Dante Briones.
0df2b1de3 Minor updates based on the input of Dave Brosius pr: 39320
9a672c032 A bug in SQLExec would prevent the execution of trailing, non-semicolon-delimited statements.  Bugzilla Report 37764. Submitted by Dave Brosius.
f358c00ad Fix performance issue with StringBuffer.toString() on JDK 5. PR: 37169
be45954b9 Avoid calling getResultSet() multiple times when printing just in case Informix does not like it. This was per suggestion of Bohuslav Roztocil. PR: 27162
2a1a857ad Fix for SQLExec when used with Informix. PR: 27162 Obtained from: Bohuslav Roztocil
3396e7c32 Remove direct call to deprecated project, location, tasktype Task field, replaced by an accessor way into tasks. Remove too some unused variable declaration and some unused imports. PR: 22515 Submitted by: Emmanuel Feller ( Emmanuel dot Feller at free dot fr)
740ed5fbf Allow Result Sets and Errors to be processed properly in SQL task with multiple statements PR: 21594 Submitted by: Jeff Bohanek (jeff dot bohanek at msi dot com)
f82a8c58d Log all statements in <sql> PR: 20309
2270580b7 option to preserve formatting in inline SQL, bug ID http://nagoya.apache.org/bugzilla/show_bug.cgi?id=10719

Start block index: 401
End block index: 556
    public void execute() throws BuildException {
        sqlCommand = sqlCommand.trim();

        if (srcFile == null && sqlCommand.length()==0 && filesets.isEmpty()) { 
            if (transactions.size() == 0) {
                throw new BuildException("Source file or fileset, transactions or sql statement must be set!", location);
            }
        } else { 
            // deal with the filesets
            for (int i=0; i<filesets.size(); i++) {
                FileSet fs = (FileSet) filesets.elementAt(i);
                DirectoryScanner ds = fs.getDirectoryScanner(project);
                File srcDir = fs.getDir(project);

                String[] srcFiles = ds.getIncludedFiles();

                // Make a transaction for each file
                for ( int j=0 ; j<srcFiles.length ; j++ ) {
                    Transaction t = createTransaction();
                    t.setSrc(new File(srcDir, srcFiles[j]));
                }
            }

            // Make a transaction group for the outer command
            Transaction t = createTransaction();
            t.setSrc(srcFile);
            t.addText(sqlCommand);
        }

        if (driver == null) {
            throw new BuildException("Driver attribute must be set!", location);
        }
        if (userId == null) {
            throw new BuildException("User Id attribute must be set!", location);
        }
        if (password == null) {
            throw new BuildException("Password attribute must be set!", location);
        }
        if (url == null) {
            throw new BuildException("Url attribute must be set!", location);
        }
        if (srcFile != null && !srcFile.exists()) {
            throw new BuildException("Source file does not exist!", location);
        }
        Driver driverInstance = null;
        try {
            Class dc;
            if (classpath != null) {
                // check first that it is not already loaded otherwise
                // consecutive runs seems to end into an OutOfMemoryError
                // or it fails when there is a native library to load
                // several times.
                // this is far from being perfect but should work in most cases.
                synchronized (loaderMap){
                    if (caching){
                        loader = (AntClassLoader)loaderMap.get(driver);
                    }
                    if (loader == null){
                        log( "Loading " + driver + " using AntClassLoader with classpath " + classpath,
                             Project.MSG_VERBOSE );
                        loader = new AntClassLoader(project, classpath);
                        if (caching){
                            loaderMap.put(driver, loader);
                        }
                    } else {
                        log("Loading " + driver + " using a cached AntClassLoader.",
                                Project.MSG_VERBOSE);
                    }
                }
                dc = loader.loadClass(driver);
            }
            else {
                log("Loading " + driver + " using system loader.", Project.MSG_VERBOSE);
                dc = Class.forName(driver);
            }
            driverInstance = (Driver) dc.newInstance();
        }catch(ClassNotFoundException e){
            throw new BuildException("Class Not Found: JDBC driver " + driver + " could not be loaded", location);
        }catch(IllegalAccessException e){
            throw new BuildException("Illegal Access: JDBC driver " + driver + " could not be loaded", location);
        }catch(InstantiationException e) {
            throw new BuildException("Instantiation Exception: JDBC driver " + driver + " could not be loaded", location);
        }

        try{
            log("connecting to " + url, Project.MSG_VERBOSE );
            Properties info = new Properties();
            info.put("user", userId);
            info.put("password", password);
            conn = driverInstance.connect(url, info);

            if (conn == null) {
                // Driver doesn't understand the URL
                throw new SQLException("No suitable Driver for "+url);
            }

            if (!isValidRdbms(conn)) return;

            conn.setAutoCommit(autocommit);

            statement = conn.createStatement();

            
            PrintStream out = System.out;
            try {
                if (output != null) {
                    log("Opening PrintStream to output file " + output, Project.MSG_VERBOSE);
                    out = new PrintStream(new BufferedOutputStream(new FileOutputStream(output)));
                }
                        
                // Process all transactions
                for (Enumeration e = transactions.elements(); 
                     e.hasMoreElements();) {
                       
                    ((Transaction) e.nextElement()).runTransaction(out);
                    if (!autocommit) {
                        log("Commiting transaction", Project.MSG_VERBOSE);
                        conn.commit();
                    }
                }
            }
            finally {
                if (out != null && out != System.out) {
                    out.close();
                }
            }
        } catch(IOException e){
            if (!autocommit && conn != null && onError.equals("abort")) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            throw new BuildException(e, location);
        } catch(SQLException e){
            if (!autocommit && conn != null && onError.equals("abort")) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            throw new BuildException(e, location);
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {}
        }
          
        log(goodSql + " of " + totalSql + 
            " SQL statements executed successfully");
    }
