10/report.java
Satd-method: //   public static Node addNode(Cache cache, Fqn fqn, boolean localOnly, boolean resident) throws CacheException {
********************************************
********************************************
10/After/ [HHH-4519 ec31e277_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
// 
+      // We update whether or not the region is valid. Other nodes
+      // may have already restored the region so they need to
+      // be informed of the change.
+      // We update whether or not the region is valid. Other nodes
+      // may have already restored the region so they need to
+      // be informed of the change.
-      return true; // TODO this is suspect
+      // Reads are non-blocking in Infinispan, so not sure of the necessity of passing ZERO_LOCK_ACQUISITION_TIMEOUT
-      // Here we don't want to suspend the tx. If we do:
-      // 1) We might be caching query results that reflect uncommitted
-      // changes. No tx == no WL on cache node, so other threads
-      // can prematurely see those query results
-      // 2) No tx == immediate replication. More overhead, plus we
-      // spread issue #1 above around the cluster
+         // Here we don't want to suspend the tx. If we do:
+         // 1) We might be caching query results that reflect uncommitted
+         // changes. No tx == no WL on cache node, so other threads
+         // can prematurely see those query results
+         // 2) No tx == immediate replication. More overhead, plus we
+         // spread issue #1 above around the cluster
-      // Add a zero (or quite low) timeout option so we don't block.
-      // Ignore any TimeoutException. Basically we forego caching the
-      // query result in order to avoid blocking.
-      // Reads are done with suspended tx, so they should not hold the
-      // lock for long.  Not caching the query result is OK, since
-      // any subsequent read will just see the old result with its
-      // out-of-date timestamp; that result will be discarded and the
-      // db query performed again.
+         // Add a zero (or quite low) timeout option so we don't block.
+         // Ignore any TimeoutException. Basically we forego caching the
+         // query result in order to avoid blocking.
+         // Reads are done with suspended tx, so they should not hold the
+         // lock for long.  Not caching the query result is OK, since
+         // any subsequent read will just see the old result with its
+         // out-of-date timestamp; that result will be discarded and the
+         // db query performed again.
+         // ignore it
+         // ignore it
-         // ignore it
-         // ignore it
-         // ignore!
-      // Wait for async propagation

Lines added containing method: 23. Lines removed containing method: 19. Tot = 42
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setResident
* getChild
* setCacheModeLocal
* getRoot
* addChild
********************************************
********************************************
10/After/ [HHH-4520 abc165ea_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
// 
+   // ----------------------------------------------------------------- Public
+      // Invalidate any pending puts
+      // Record when this occurred to invalidate later naked puts
+      // Don't let recentRemovals map become a memory leak
+            if (removalsQueue.size() > 1) { // we have at least one as we
+               // just added it
+               // Oops; removed the wrong timestamp; restore it
+                  // else we hit a race and need to loop to try again
+      // Guard against memory leaks
+   // -------------------------------------------------------------- Protected
+   // ---------------------------------------------------------------- Private
+         // Clean items out of the basic queue
+               // Potential leak; move to the overaged queued
+               // Don't waste time on very recent items
+               // Don't spend too much time getting nowhere
+               // Move on to the next item
+         // Process the overage queue until we find an item to clean
+         // or an incomplete item that hasn't aged out
+      // We've found a pendingPut that never happened; clean it up
+                  // Oops. Restore it.
+               // initial put
+            // 2nd put; need a map
+         // If copying causes issues, provide a lazily loaded Map
+      // Start with a removal so the "isPutValid" calls will fail if
+      // any of the concurrent activity isn't handled properly
+      // Do the registration + isPutValid calls
+      // Start with a regionRemoval so we can confirm at the end that all
+      // registrations have been cleaned out
+      // ppq = [1,2(c),3,4,5,6(c)]
+      // Sleep past "pendingPutRecentPeriod"
+      // White box -- should have cleaned out 2 (completed) but
+      // not gotten to 6 (also removed)
+      // ppq = [1,3,4,5,6(c),7]
+      // Sleep past "pendingPutOveragePeriod"
+      // White box -- should have cleaned out 6 (completed) and
+      // moved 1, 3, 4 and 5 to overage queue
+      // oppq = [1,3,4,5] ppq = [7,8]
+      // Sleep past "maxPendingPutDelay"
+      // White box -- should have cleaned out 1 (overage) and
+      // moved 7 to overage queue
+      // oppq = [3(c),4,5,7] ppq=[8]
+      // Sleep past "maxPendingPutDelay"
+      // White box -- should have cleaned out 3 (completed)
+      // and 4 (overage) and moved 8 to overage queue
+      // We now have 5,7,8 in overage and 7tx in pending
+      // oppq = [5,7,8] ppq=[7tx]
+      // Validate that only expected items can do puts, thus indirectly
+      // proving the others have been cleaned out of pendingPuts map
+      // 5 was overage, so should have been cleaned
+      // 7 was overage, so should have been cleaned
+         // TODO Auto-generated method stub
+         // TODO Auto-generated method stub
+         // TODO Auto-generated method stub
-          // expected behavior here
-      // cleanup
+         // cleanup
-      // cleanup
+         // cleanup
+//         setRollbackOnly();
+//         fail("failed to query DB; exception=" + e);
+         // DualNodeJtaTransactionManagerImpl.cleanupTransactions();
+         // DualNodeJtaTransactionManagerImpl.cleanupTransactionManagers();
+      // setup
+      // check that cache was hit
+      // read everyone's contacts
+//   /**
+//    * TODO: This will fail until ISPN-??? has been fixed.
+//    * 
+//    * @throws Exception
+//    */
+//   public void testManyUsers() throws Throwable {
+//      try {
+//         // setup - create users
+//         for (int i = 0; i < USER_COUNT; i++) {
+//            Customer customer = createCustomer(0);
+//            getCustomerIDs().add(customer.getId());
+//         }
+//         assertEquals("failed to create enough Customers", USER_COUNT, getCustomerIDs().size());
+//         final ExecutorService executor = Executors.newFixedThreadPool(USER_COUNT);
+//         CyclicBarrier barrier = new CyclicBarrier(USER_COUNT + 1);
+//         List<Future<Void>> futures = new ArrayList<Future<Void>>(USER_COUNT);
+//         for (Integer customerId : getCustomerIDs()) {
+//            Future<Void> future = executor.submit(new UserRunner(customerId, barrier));
+//            futures.add(future);
+//            Thread.sleep(LAUNCH_INTERVAL_MILLIS); // rampup
+//         }
+////         barrier.await(); // wait for all threads to be ready
+//         barrier.await(45, TimeUnit.SECONDS); // wait for all threads to finish
+//         log.info("All threads finished, let's shutdown the executor and check whether any exceptions were reported");
+//         for (Future<Void> future : futures) future.get();
+//         log.info("All future gets checked");
+//      } catch (Throwable t) {
+//         log.error("Error running test", t);
+//         throw t;
+//      }
+//   }
+         // assuming contact is persisted via cascade from customer
+         // explicitly delete Contact because hbm has no 'DELETE_ORPHAN' cascade?
+         // getEnvironment().getSessionFactory().getCurrentSession().delete(contact); //appears to
+         // not be needed
+         // assuming contact is persisted via cascade from customer
+         // name this thread for easier log tracing
+//            barrier.await();
+               // read everyone's contacts
+            // rollback current transaction if any
+            // really should not happen since above methods all follow begin-commit-rollback pattern
+            // try {
+            // if
+            // (DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestUtil.LOCAL).getTransaction()
+            // != null) {
+            // DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestUtil.LOCAL).rollback();
+            // }
+            // } catch (SystemException ex) {
+            // throw new RuntimeException("failed to rollback tx", ex);
+            // }
-            // ignored
+               // ignored
-               // ignored
+                  // ignored

Lines added containing method: 114. Lines removed containing method: 5. Tot = 119
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setResident
* getChild
* setCacheModeLocal
* getRoot
* addChild
********************************************
********************************************
10/After/ HHH-5616  8beaccc7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
// 
+    // http://jira.codehaus.org/browse/GRADLE-739
+//    environment['jgroups.bind_addr'] = $jgroupsBindAddress
+    // quite a few failures and the old maven module disabled these tests as well

Lines added containing method: 3. Lines removed containing method: 0. Tot = 3
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setResident
* getChild
* setCacheModeLocal
* getRoot
* addChild
********************************************
********************************************
