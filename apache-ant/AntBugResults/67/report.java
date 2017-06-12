File path: src/main/org/apache/tools/ant/taskdefs/ExecuteJava.java
Comment: XXX is the following really necessary? it is in the same thread group...
Initial commit id: 5aeca1e0
Final commit id: 13000c1a
   Bugs between [       1]:
da4edf3f9 Validate system properties before running a Java command, PR 34725
   Bugs after [       0]:


Start block index: 112
End block index: 198
    public void execute(Project project) throws BuildException {
        final String classname = javaCommand.getExecutable();

        AntClassLoader loader = null;
        try {
            if (sysProperties != null) {
                sysProperties.setSystem();
            }
            Class target = null;
            if (classpath == null) {
                target = Class.forName(classname);
            } else {
                loader = project.createClassLoader(classpath);
                loader.setParent(project.getCoreLoader());
                loader.setParentFirst(false);
                loader.addJavaLibraries();
                loader.setIsolated(true);
                loader.setThreadContextLoader();
                loader.forceLoadClass(classname);
                target = Class.forName(classname, true, loader);
            }
            main = target.getMethod("main", new Class[] {String[].class});
            if (main == null) {
                throw new BuildException("Could not find main() method in "
                                         + classname);
            }
            if ((main.getModifiers() & Modifier.STATIC) == 0) {
                throw new BuildException("main() method in " + classname
                    + " is not declared static");
            }
            if (timeout == null) {
                run();
            } else {
                thread = new Thread(this, "ExecuteJava");
                Task currentThreadTask
                    = project.getThreadTask(Thread.currentThread());
                // XXX is the following really necessary? it is in the same thread group...
                project.registerThreadTask(thread, currentThreadTask);
                // if we run into a timeout, the run-away thread shall not
                // make the VM run forever - if no timeout occurs, Ant's
                // main thread will still be there to let the new thread
                // finish
                thread.setDaemon(true);
                Watchdog w = new Watchdog(timeout.longValue());
                w.addTimeoutObserver(this);
                synchronized (this) {
                    thread.start();
                    w.start();
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    if (timedOut) {
                        project.log("Timeout: sub-process interrupted",
                                    Project.MSG_WARN);
                    } else {
                        thread = null;
                        w.stop();
                    }
                }
            }
            if (caught != null) {
                throw caught;
            }
        } catch (ClassNotFoundException e) {
            throw new BuildException("Could not find " + classname + "."
                                     + " Make sure you have it in your"
                                     + " classpath");
        } catch (SecurityException e) {
            throw e;
        } catch (ThreadDeath e) {
            // XXX could perhaps also call thread.stop(); not sure if anyone cares
            throw e;
        } catch (Throwable e) {
            throw new BuildException(e);
        } finally {
            if (loader != null) {
                loader.resetThreadContextLoader();
                loader.cleanup();
                loader = null;
            }
            if (sysProperties != null) {
                sysProperties.restoreSystem();
            }
        }
    }