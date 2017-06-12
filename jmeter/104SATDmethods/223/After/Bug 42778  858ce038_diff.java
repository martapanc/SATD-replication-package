diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index a76d46a58..30ed135a9 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,64 +1,83 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * 
  */
 
 package org.apache.jmeter.control;
 
 import java.io.Serializable;
 
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Transaction Controller to measure transaction times
  * 
  */
 public class TransactionController extends GenericController implements Controller, Serializable {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
     transient private TransactionSampler transactionSampler;
 
 	/**
 	 * @see org.apache.jmeter.control.Controller#next()
 	 */
 	public Sampler next() {
         // Check if transaction is done
         if(transactionSampler != null && transactionSampler.isTransactionDone()) {
-            log.debug("End of transaction");
+        	if (log.isDebugEnabled()) {
+                log.debug("End of transaction " + getName());
+        	}
             // This transaction is done
             transactionSampler = null;
             return null;
         }
         
         // Check if it is the start of a new transaction
 		if (isFirst()) // must be the start of the subtree
 		{
-		    log.debug("Start of transaction");
+        	if (log.isDebugEnabled()) {
+		        log.debug("Start of transaction " + getName());
+        	}
 		    transactionSampler = new TransactionSampler(this, getName());
 		}
 
         // Sample the children of the transaction
 		Sampler subSampler = super.next();
         transactionSampler.setSubSampler(subSampler);
         // If we do not get any sub samplers, the transaction is done
         if (subSampler == null) {
             transactionSampler.setTransactionDone();
         }
         return transactionSampler;
 	}
+	
+	protected Sampler nextIsAController(Controller controller) throws NextIsNullException {
+		Sampler returnValue;
+		Sampler sampler = controller.next();
+		if (sampler == null) {
+			currentReturnedNull(controller);
+			// We need to call the super.next, instead of this.next, which is done in GenericController,
+			// because if we call this.next(), it will return the TransactionSampler, and we do not want that.
+			// We need to get the next real sampler or controller
+			returnValue = super.next();
+		} else {
+			returnValue = sampler;
+		}
+		return returnValue;
+	}
 }
diff --git a/src/core/org/apache/jmeter/control/TransactionSampler.java b/src/core/org/apache/jmeter/control/TransactionSampler.java
index eadde50d8..1f99997da 100644
--- a/src/core/org/apache/jmeter/control/TransactionSampler.java
+++ b/src/core/org/apache/jmeter/control/TransactionSampler.java
@@ -1,113 +1,114 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * 
  */
 
 /*
  *  N.B. Although this is a type of sampler, it is only used by the transaction controller,
  *  and so is in the control package
 */
 package org.apache.jmeter.control;
 
 
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 
 /**
  * Transaction Controller to measure transaction times
  * 
  */
 public class TransactionSampler extends AbstractSampler {
   private boolean transactionDone = false;
 
     private TransactionController transactionController; 
     
 	private Sampler subSampler;
 
 	private SampleResult transactionSampleResult;
 
 	private int calls = 0;
 
 	private int noFailingSamples = 0;
 
 	public TransactionSampler(){
 		//log.warn("Constructor only intended for use in testing");
 	}
 
 	public TransactionSampler(TransactionController controller, String name) {
         transactionController = controller;
+		setName(name); // ensure name is available for debugging
 		transactionSampleResult = new SampleResult();
 		transactionSampleResult.setSampleLabel(name);
 		// Assume success
 		transactionSampleResult.setSuccessful(true);
 		transactionSampleResult.sampleStart();
 	}
 
     /**
      * One cannot sample the TransactionSample directly.
      */
 	public SampleResult sample(Entry e) {
         // It is the JMeterThread which knows how to sample a
         // real sampler
         return null;
 	}
     
     public Sampler getSubSampler() {
         return subSampler;
     }
     
     public SampleResult getTransactionResult() {
         return transactionSampleResult;
     }
     
     public TransactionController getTransactionController() {
         return transactionController;
     }
 
     public boolean isTransactionDone() {
         return transactionDone;
     }
     
     public void addSubSamplerResult(SampleResult res) {
         // Another subsample for the transaction
         calls++;
         // The transaction fails if any sub sample fails
         if (!res.isSuccessful()) {
             transactionSampleResult.setSuccessful(false);
             noFailingSamples++;
         }
         // Add the sub result to the transaction result
         transactionSampleResult.addSubResult(res);
     }
 
 	protected void setTransactionDone() {
 		this.transactionDone = true;
 		// Set the overall status for the transaction sample
 		// TODO: improve, e.g. by adding counts to the SampleResult class
 		transactionSampleResult.setResponseMessage("Number of samples in transaction : "
 						+ calls + ", number of failing samples : "
 						+ noFailingSamples);
 		if (transactionSampleResult.isSuccessful()) {
 			transactionSampleResult.setResponseCodeOK();
 		}
 	}
 
 	protected void setSubSampler(Sampler subSampler) {
 		this.subSampler = subSampler;
 	}
 }
