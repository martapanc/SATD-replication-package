File path: src/core/org/apache/jmeter/threads/JMeterThread.java
Comment: TODO: remove this useless Entry parameter
Initial commit id: 90ce7f4a7
Final commit id: 7d8faded6
   Bugs between [      34]:
7d8faded6 Sonar : Fix errors, code smells and false-positive Also use Timer.isModifiable() introduced within Bug 60018 Bugzilla Id: 60018
01618c3e6 Bug 60018 - Timer : Add a factor to apply on pauses Bugzilla Id: 60018
b32997c38 Bug 60049 - When using Timers with high delays or Constant Throughput Timer with low throughput, Scheduler may take a lot of time to exit, same for Shutdown test Comment code Bugzilla Id: 60049
3fd2896a6 Bug 60049 - When using Timers with high delays or Constant Throughput Timer with low throughput, Scheduler may take a lot of time to exit, same for Shutdown test Bugzilla Id: 60049
482e1edb1 Bug 60050 - CSV Data Set : Make it clear in the logs when a thread will exit due to this configuration Bugzilla Id: 60050
5f87f3092 Bug 59882 - Reduce memory allocations for better throughput Based on PR 217 contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)
28c3e8d2e Bug 59133 - Regression in custom Thread Group in nightly build before 3.0 (6 march 2016) #resolve #164 https://github.com/apache/jmeter/pull/164 Bugzilla Id: 59133
6c9d00ae1 Bug 59067 - JMeter fails to iterate over Controllers that are children of a TransactionController having "Generate parent sample" checked after an assertion error occurs on a Thread Group with "Start Next Thread Loop" Bugzilla Id: 59067
fd62770a0 Bug 58736 - Add Sample Timeout support Ensure sampleEnded is called Drop property  JMeterThread.sampleStarted, users who don't want to use the feature won't add the Sample Timeout element Bugzilla Id: 58736
cbdd5614d Bug 58728 - Drop old behavioural properties Bugzilla Id: 58728
373a03821 bug 52968 added a call to processSampler in order to mark the transaction sampler in error and generate the associated sampler result. Add an explanation for that and extract that code in a dedicated method to make clear that the triggerEndOfLoopOnParentControllers will not process any new sampler #resolve #56
6cb0db932 Bug 58726 - Remove the jmeterthread.startearlier parameter #resolve #58 Bugzilla Id: 58726
e6b1b0acc Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
32f301947 Bug 54268 - Improve memory and CPU usage Improve CPU by caching response as String instead of computing it for each post processor Improve memory by eagerly cleaning the cached field Bugzilla Id: 54268
e417a04bf Bug 54267 - Start Next Thread Loop setting doesn't work in custom thread groups Bugzilla Id: 54267
faa9e3ca0 Bug 54204 - Result Status Action Handler : Add start next thread loop option Bugzilla Id: 54204
95d97c944 Bug 53418 - New OnDemandThreadGroup that creates threads when needed instead of creating them on Test startup Bugzilla Id: 53418
03ea5d70c Bug 52968 - Option Start Next Loop in Thread Group does not mark parent Transaction Sampler in error when an error occurs
ec46abc7a Bug 45839 - Test Action : Allow premature exit from a loop
0a63e84b2 Bug 52330 - With next-Loop-On-Error after error samples are not executed in next loop
c64a5b2bc Fix to Start Next Loop broken feature, fixes following issues: - Bug 51865 - Infinite loop inside thread group does not work properly if "Start next loop after a Sample error" option set - Bug 51868 - A lot of exceptions in jmeter.log while using option "Start next loop" for thread - Bug 51866 - Counter under loop doesnt work properly if "Start next loop on error" option set for thread group
708a7949f Bug 51880 - The shutdown command is not working if I invoke it before all the thread are started Part2 - fix to startUp delay (previous fix was to rampUp delay)
77babfc75 Bug 51888 - Occasional deadlock when stopping a testplan
dfdf1dbc8 Bug 51880 - The shutdown command is not working if I invoke it before all the thread are started.
b56c8c975 Bug 47921 - Variables not released for GC after JMeterThread exits.
65a69f812 Since add Start Next Loop behavior ([Bug 30563] Thread Group should have a start next loop option on Sample Error), some new bugs on transaction controller with option Generate as parent enabled are come. Fix.
17c919ff8 Bug 30563 - Thread Group should have a start next loop option on Sample Error
ca8e0c22b Bug 48749 - Allowing custom Thread Groups
f3bca638d Bug 45903 - allow Assertions to apply to sub-samples - generic implementation (specific assertions to follow)
9a3d4075a Bug 43430 - Count of active threads is incorrect for remote samples
cf1c0dc65 Bug 41913 - TransactionController now creates samples as sub-samples of the transaction
be00b0cb0 Bug 41140 - run PostProcessor in forward order
0415393d9 Bug 38391 - use long to accumulate delays
51329310a Bug 34739 - Enhance constant Throughput timer Fix time calculation so per-thread works better
   Bugs after [       4]:
3fa818235 Bug 60812 - JMeterThread does not honor contract of JMeterStopTestNowException Bugzilla Id: 60812
520166ca4 Bug 60797 - TestAction in pause mode can last beyond configured duration of test Bugzilla Id: 60797
ea7682133 Bug 60564 - Migrating LogKit to SLF4J - core/testelement,threads,util,visualizers package Contributed by Woonsan Ko This closes #273 Bugzilla Id: 60564
bd3b94bb5 Bug 60530 - Add API to create JMeter threads while test is running Based on a contribution by Logan Mauzaize & Maxime Chassagneux Bugzilla Id: 60530

Start block index: 246
End block index: 322
    public void run()
    {
        try
        {
            threadContext = JMeterContextService.getContext();
            threadContext.setVariables(threadVars);
            threadContext.setThreadNum(getThreadNum());
            testTree.traverse(compiler);
            running = true;
            //listeners = controller.getListeners();

            if (scheduler)
            {
                //set the scheduler to start
                startScheduler();
            }

			rampUpDelay();

            log.info("Thread " + Thread.currentThread().getName() + " started");
            controller.initialize();
            controller.addIterationListener(new IterationListener());
            threadContext.setSamplingStarted(true);
            while (running)
            {
                Sampler sam;
                while (running && (sam=controller.next())!=null)
                {
                    try
                    {
                        threadContext.setCurrentSampler(sam);
                        SamplePackage pack = compiler.configureSampler(sam);

                        //Hack: save the package for any transaction controllers
                        threadContext.getVariables().putObject(PACKAGE_OBJECT,pack);

                        delay(pack.getTimers());
                        Sampler sampler= pack.getSampler();
                        if (sampler instanceof TestBean) ((TestBean)sampler).prepare();
                        SampleResult result = sampler.sample(null); // TODO: remove this useless Entry parameter
                        result.setThreadName(threadName);
                        threadContext.setPreviousResult(result);
                        runPostProcessors(pack.getPostProcessors());
                        checkAssertions(pack.getAssertions(), result);
                        notifyListeners(pack.getSampleListeners(), result);
                        compiler.done(pack);
                        if (result.isStopThread() || (!result.isSuccessful() && onErrorStopThread)){
                        	stopThread();
                        }
                        if (result.isStopTest() || (!result.isSuccessful() && onErrorStopTest)){
                        	stopTest();
                        }
                        if (scheduler)
                        {
                            //checks the scheduler to stop the iteration
                            stopScheduler();
                        }

                    }
                    catch (Exception e)
                    {
                        log.error("", e);
                    }
                }
                if (controller.isDone())
                {
                    running = false;
                }
            }
        }
        finally
        {
            threadContext.clear();
            log.info("Thread " + threadName + " is done");
            monitor.threadFinished(this);
        }
    }

*********************** Method when SATD was removed **************************

    @Override
    public void run() {
        // threadContext is not thread-safe, so keep within thread
        JMeterContext threadContext = JMeterContextService.getContext();
        LoopIterationListener iterationListener = null;

        try {
            iterationListener = initRun(threadContext);
            while (running) {
                Sampler sam = threadGroupLoopController.next();
                while (running && sam != null) {
                    processSampler(sam, null, threadContext);
                    threadContext.cleanAfterSample();

                    // restart of the next loop
                    // - was requested through threadContext
                    // - or the last sample failed AND the onErrorStartNextLoop option is enabled
                    if(threadContext.isRestartNextLoop()
                            || (onErrorStartNextLoop
                                    && !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK))))
                    {
                        if(log.isDebugEnabled() && onErrorStartNextLoop && !threadContext.isRestartNextLoop()) {
                                log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
                        }

                        triggerEndOfLoopOnParentControllers(sam, threadContext);
                        sam = null;
                        threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
                        threadContext.setRestartNextLoop(false);
                    }
                    else {
                        sam = threadGroupLoopController.next();
                    }
                }

                if (threadGroupLoopController.isDone()) {
                    running = false;
                    log.info("Thread is done: " + threadName);
                }
            }
        }
        // Might be found by contoller.next()
        catch (JMeterStopTestException e) { // NOSONAR
            log.info("Stopping Test: " + e.toString());
            stopTest();
        }
        catch (JMeterStopTestNowException e) { // NOSONAR
            log.info("Stopping Test Now: " + e.toString());
            stopTestNow();
        } catch (JMeterStopThreadException e) { // NOSONAR
            log.info("Stop Thread seen for thread " + getThreadName()+", reason:"+ e.toString());
        } catch (Exception | JMeterError e) {
            log.error("Test failed!", e);
        } catch (ThreadDeath e) {
            throw e; // Must not ignore this one
        } finally {
            currentSampler = null; // prevent any further interrupts
            try {
                interruptLock.lock();  // make sure current interrupt is finished, prevent another starting yet
                threadContext.clear();
                log.info("Thread finished: " + threadName);
                threadFinished(iterationListener);
                monitor.threadFinished(this); // Tell the monitor we are done
                JMeterContextService.removeContext(); // Remove the ThreadLocal entry
            }
            finally {
                interruptLock.unlock(); // Allow any pending interrupt to complete (OK because currentSampler == null)
            }
        }
    }
