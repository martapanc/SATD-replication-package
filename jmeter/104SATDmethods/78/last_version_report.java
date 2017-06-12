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
