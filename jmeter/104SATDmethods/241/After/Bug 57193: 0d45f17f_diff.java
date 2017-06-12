diff --git a/src/components/org/apache/jmeter/visualizers/Spline3.java b/src/components/org/apache/jmeter/visualizers/Spline3.java
index ae0699ff9..333541772 100644
--- a/src/components/org/apache/jmeter/visualizers/Spline3.java
+++ b/src/components/org/apache/jmeter/visualizers/Spline3.java
@@ -1,423 +1,423 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /*
  * TODO : - implement ImageProducer interface - suggestions ;-)
  */
 
 /**
  * This class implements the representation of an interpolated Spline curve.
  * <P>
  * The curve described by such an object interpolates an arbitrary number of
  * fixed points called <I>nodes</I>. The distance between two nodes should
  * currently be constant. This is about to change in a later version but it can
  * last a while as it's not really needed. Nevertheless, if you need the
  * feature, just <a href="mailto:norguet@bigfoot.com?subject=Spline3eq">write me
  * a note</a> and I'll write it asap.
  * <P>
  * The interpolated Spline curve can't be described by an polynomial analytic
  * equation, the degree of which would be as high as the number of nodes, which
  * would cause extreme oscillations of the curve on the edges.
  * <P>
  * The solution is to split the curve accross a lot of little <I>intervals</I> :
  * an interval starts at one node and ends at the next one. Then, the
  * interpolation is done on each interval, according to the following conditions :
  * <OL>
  * <LI>the interpolated curve is degree 3 : it's a cubic curve ;
  * <LI>the interpolated curve contains the two points delimiting the interval.
  * This condition obviously implies the curve is continuous ;
  * <LI>the interpolated curve has a smooth slope : the curvature has to be the
  * same on the left and the right sides of each node ;
  * <LI>the curvature of the global curve is 0 at both edges.
  * </OL>
  * Every part of the global curve is represented by a cubic (degree-3)
  * polynomial, the coefficients of which have to be computed in order to meet
  * the above conditions.
  * <P>
  * This leads to a n-unknow n-equation system to resolve. One can resolve an
  * equation system by several manners ; this class uses the Jacobi iterative
  * method, particularly well adapted to this situation, as the diagonal of the
  * system matrix is strong compared to the other elements. This implies the
  * algorithm always converges ! This is not the case of the Gauss-Seidel
  * algorithm, which is quite faster (it uses intermediate results of each
  * iteration to speed up the convergence) but it doesn't converge in all the
  * cases or it converges to a wrong value. This is not acceptable and that's why
  * the Jacobi method is safer. Anyway, the gain of speed is about a factor of 3
  * but, for a 100x100 system, it means 10 ms instead of 30 ms, which is a pretty
  * good reason not to explore the question any further :)
  * <P>
  * Here is a little piece of code showing how to use this class :
  *
- * <PRE> // ... float[] nodes = {3F, 2F, 4F, 1F, 2.5F, 5F, 3F}; Spline3 curve =
+ * <PRE>// ... float[] nodes = {3F, 2F, 4F, 1F, 2.5F, 5F, 3F}; Spline3 curve =
  * new Spline3(nodes); // ... public void paint(Graphics g) { int[] plot =
- * curve.getPlots(); for (int i = 1; i < n; i++) { g.drawLine(i - 1, plot[i -
+ * curve.getPlots(); for (int i = 1; i &lt; n; i++) { g.drawLine(i - 1, plot[i -
  * 1], i, plot[i]); } } // ...
  *
  * </PRE>
  *
  */
 public class Spline3 {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     protected float[][] _coefficients;
 
     protected float[][] _A;
 
     protected float[] _B;
 
     protected float[] _r;
 
     protected float[] _rS;
 
     protected int _m; // number of nodes
 
     protected int _n; // number of non extreme nodes (_m-2)
 
     static final protected float DEFAULT_PRECISION = (float) 1E-1;
 
     static final protected int DEFAULT_MAX_ITERATIONS = 100;
 
     protected float _minPrecision = DEFAULT_PRECISION;
 
     protected int _maxIterations = DEFAULT_MAX_ITERATIONS;
 
     /**
      * Creates a new Spline curve by calculating the coefficients of each part
      * of the curve, i.e. by resolving the equation system implied by the
      * interpolation condition on every interval.
      *
      * @param r
      *            an array of float containing the vertical coordinates of the
      *            nodes to interpolate ; the vertical coordinates start at 0 and
      *            are equidistant with a step of 1.
      */
     public Spline3(float[] r) {
         int n = r.length;
 
         // the number of nodes is defined by the length of r
         this._m = n;
         // grab the nodes
         this._r = new float[n];
         System.arraycopy(r, 0, _r, 0, _r.length);
         // the number of non extreme nodes is the number of intervals
         // minus 1, i.e. the length of r minus 2
         this._n = n - 2;
         // computes interpolation coefficients
         try {
             long startTime = System.currentTimeMillis();
 
             this.interpolation();
             if (log.isDebugEnabled()) {
                 long endTime = System.currentTimeMillis();
                 long elapsedTime = endTime - startTime;
 
                 if (log.isDebugEnabled()) {
                     log.debug("New Spline curve interpolated in ");
                     log.debug(elapsedTime + " ms");
                 }
             }
         } catch (Exception e) {
             log.error("Error when interpolating : ", e);
         }
 
     }
 
     /**
      * Computes the coefficients of the Spline interpolated curve, on each
      * interval. The matrix system to resolve is <CODE>AX=B</CODE>
      */
     protected void interpolation() {
         // creation of the interpolation structure
         _rS = new float[_m];
         _B = new float[_n];
         _A = new float[_n][_n];
         _coefficients = new float[_n + 1][4];
         // local variables
         int i = 0, j = 0;
 
         // initialize system structures (just to be safe)
         for (i = 0; i < _n; i++) {
             _B[i] = 0;
             for (j = 0; j < _n; j++) {
                 _A[i][j] = 0;
             }
             for (j = 0; j < 4; j++) {
                 _coefficients[i][j] = 0;
             }
         }
         for (i = 0; i < _n; i++) {
             _rS[i] = 0;
         }
         // initialize the diagonal of the system matrix (A) to 4
         for (i = 0; i < _n; i++) {
             _A[i][i] = 4;
         }
         // initialize the two minor diagonals of A to 1
         for (i = 1; i < _n; i++) {
             _A[i][i - 1] = 1;
             _A[i - 1][i] = 1;
         }
         // initialize B
         for (i = 0; i < _n; i++) {
             _B[i] = 6 * (_r[i + 2] - 2 * _r[i + 1] + _r[i]);
         }
         // Jacobi system resolving
         this.jacobi(); // results are stored in _rS
         // computes the coefficients (di, ci, bi, ai) from the results
         for (i = 0; i < _n + 1; i++) {
             // di (degree 0)
             _coefficients[i][0] = _r[i];
             // ci (degree 1)
             _coefficients[i][1] = _r[i + 1] - _r[i] - (_rS[i + 1] + 2 * _rS[i]) / 6;
             // bi (degree 2)
             _coefficients[i][2] = _rS[i] / 2;
             // ai (degree 3)
             _coefficients[i][3] = (_rS[i + 1] - _rS[i]) / 6;
         }
     }
 
     /**
      * Resolves the equation system by a Jacobi algorithm. The use of the slower
      * Jacobi algorithm instead of Gauss-Seidel is choosen here because Jacobi
      * is assured of to be convergent for this particular equation system, as
      * the system matrix has a strong diagonal.
      */
     protected void jacobi() {
         // local variables
         int i = 0, j = 0, iterations = 0;
         // intermediate arrays
         float[] newX = new float[_n];
         float[] oldX = new float[_n];
 
         // Jacobi convergence test
         if (!converge()) {
             if (log.isDebugEnabled()) {
                 log.debug("Warning : equation system resolving is unstable");
             }
         }
         // init newX and oldX arrays to 0
         for (i = 0; i < _n; i++) {
             newX[i] = 0;
             oldX[i] = 0;
         }
         // main iteration
         while ((this.precision(oldX, newX) > this._minPrecision) && (iterations < this._maxIterations)) {
             System.arraycopy(oldX, 0, newX, 0, _n);
             for (i = 0; i < _n; i++) {
                 newX[i] = _B[i];
                 for (j = 0; j < i; j++) {
                     newX[i] = newX[i] - (_A[i][j] * oldX[j]);
                 }
                 for (j = i + 1; j < _n; j++) {
                     newX[i] = newX[i] - (_A[i][j] * oldX[j]);
                 }
                 newX[i] = newX[i] / _A[i][i];
             }
             iterations++;
         }
         if (this.precision(oldX, newX) < this._minPrecision) {
             if (log.isDebugEnabled()) {
                 log.debug("Minimal precision (");
                 log.debug(this._minPrecision + ") reached after ");
                 log.debug(iterations + " iterations");
             }
         } else if (iterations > this._maxIterations) {
             if (log.isDebugEnabled()) {
                 log.debug("Maximal number of iterations (");
                 log.debug(this._maxIterations + ") reached");
                 log.debug("Warning : precision is only ");
                 log.debug("" + this.precision(oldX, newX));
                 log.debug(", divergence is possible");
             }
         }
         System.arraycopy(newX, 0, _rS, 1, _n);
     }
 
     /**
      * Test if the Jacobi resolution of the equation system converges. It's OK
      * if A has a strong diagonal.
      */
     protected boolean converge() {
         boolean converge = true;
         int i = 0, j = 0;
         float lineSum = 0F;
 
         for (i = 0; i < _n; i++) {
             if (converge) {
                 lineSum = 0;
                 for (j = 0; j < _n; j++) {
                     lineSum = lineSum + Math.abs(_A[i][j]);
                 }
                 lineSum = lineSum - Math.abs(_A[i][i]);
                 if (lineSum > Math.abs(_A[i][i])) {
                     converge = false;
                     break;
                 }
             }
         }
         return converge;
     }
 
     /**
      * Computes the current precision reached.
      */
     protected float precision(float[] oldX, float[] newX) {
         float N = 0F, D = 0F, erreur = 0F;
         int i = 0;
 
         for (i = 0; i < _n; i++) {
             N = N + Math.abs(newX[i] - oldX[i]);
             D = D + Math.abs(newX[i]);
         }
         if (D != 0F) {
             erreur = N / D;
         } else {
             erreur = Float.MAX_VALUE;
         }
         return erreur;
     }
 
     /**
      * Computes a (vertical) Y-axis value of the global curve.
      *
      * @param t
      *            abscissa
      * @return computed ordinate
      */
     public float value(float t) {
         int i = 0, splineNumber = 0;
         float abscissa = 0F, result = 0F;
 
         // verify t belongs to the curve (range [0, _m-1])
         if ((t < 0) || (t > (_m - 1))) {
             if (log.isDebugEnabled()) {
                 log.debug("Warning : abscissa " + t + " out of bounds [0, " + (_m - 1) + "]");
             }
             // silent error, consider the curve is constant outside its range
             if (t < 0) {
                 t = 0;
             } else {
                 t = _m - 1;
             }
         }
         // seek the good interval for t and get the piece of curve on it
         splineNumber = (int) Math.floor(t);
         if (t == (_m - 1)) {
             // the upper limit of the curve range belongs by definition
             // to the last interval
             splineNumber--;
         }
         // computes the value of the curve at the pecified abscissa
         // and relative to the beginning of the right piece of Spline curve
         abscissa = t - splineNumber;
         // the polynomial calculation is done by the (fast) Euler method
         for (i = 0; i < 4; i++) {
             result = result * abscissa;
             result = result + _coefficients[splineNumber][3 - i];
         }
         return result;
     }
 
     /**
      * Manual check of the curve at the interpolated points.
      */
     public void debugCheck() {
         int i = 0;
 
         for (i = 0; i < _m; i++) {
             log.info("Point " + i + " : ");
             log.info(_r[i] + " =? " + value(i));
         }
     }
 
     /**
      * Computes drawable plots from the curve for a given draw space. The values
      * returned are drawable vertically and from the <B>bottom</B> of a Panel.
      *
      * @param width
      *            width within the plots have to be computed
      * @param height
      *            height within the plots are expected to be drawed
      * @return drawable plots within the limits defined, in an array of int (as
      *         many int as the value of the <CODE>width</CODE> parameter)
      */
     public int[] getPlots(int width, int height) {
         int[] plot = new int[width];
         // computes auto-scaling and absolute plots
         float[] y = new float[width];
         float max = java.lang.Integer.MIN_VALUE;
         float min = java.lang.Integer.MAX_VALUE;
 
         for (int i = 0; i < width; i++) {
             y[i] = value(((float) i) * (_m - 1) / width);
             if (y[i] < min) {
                 min = y[i];
             }
 
             if (y[i] > max) {
                 max = y[i];
             }
         }
         if (min < 0) {
             min = 0; // shouldn't draw negative values
         }
         // computes relative auto-scaled plots to fit in the specified area
         for (int i = 0; i < width; i++) {
             plot[i] = Math.round(((y[i] - min) * (height - 1)) / (max - min));
         }
         return plot;
     }
 
     public void setPrecision(float precision) {
         this._minPrecision = precision;
     }
 
     public float getPrecision() {
         return this._minPrecision;
     }
 
     public void setToDefaultPrecision() {
         this._minPrecision = DEFAULT_PRECISION;
     }
 
     public float getDefaultPrecision() {
         return DEFAULT_PRECISION;
     }
 
     public void setMaxIterations(int iterations) {
         this._maxIterations = iterations;
     }
 
     public int getMaxIterations() {
         return this._maxIterations;
     }
 
     public void setToDefaultMaxIterations() {
         this._maxIterations = DEFAULT_MAX_ITERATIONS;
     }
 
     public int getDefaultMaxIterations() {
         return DEFAULT_MAX_ITERATIONS;
     }
 
 }
diff --git a/src/core/org/apache/jmeter/control/Controller.java b/src/core/org/apache/jmeter/control/Controller.java
index ddb2d76b3..1056144ee 100644
--- a/src/core/org/apache/jmeter/control/Controller.java
+++ b/src/core/org/apache/jmeter/control/Controller.java
@@ -1,70 +1,70 @@
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
 
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.TestElement;
 
 /**
  * This interface is used by JMeterThread in the following manner:
- *
- * while (running && (sampler = controller.next()) != null)
+ * <p>
+ * <code>while (running &amp;&amp; (sampler = controller.next()) != null)</code>
  */
 public interface Controller extends TestElement {
     /**
      * Delivers the next Sampler or null
      *
      * @return org.apache.jmeter.samplers.Sampler or null
      */
     Sampler next();
 
     /**
      * Indicates whether the Controller is done delivering Samplers for the rest
      * of the test.
      *
      * When the top-level controller returns true to JMeterThread,
      * the thread is complete.
      *
      * @return boolean
      */
     boolean isDone();
 
     /**
      * Controllers have to notify listeners of when they begin an iteration
      * through their sub-elements.
      */
     void addIterationListener(LoopIterationListener listener);
 
     /**
      * Called to initialize a controller at the beginning of a test iteration.
      */
     void initialize();
 
     /**
      * Unregister IterationListener
      * @param iterationListener {@link LoopIterationListener}
      */
     void removeIterationListener(LoopIterationListener iterationListener);
 
     /**
      * Trigger end of loop condition on controller (used by Start Next Loop feature)
      */
     void triggerEndOfLoop();
 }
diff --git a/src/core/org/apache/jmeter/control/GenericController.java b/src/core/org/apache/jmeter/control/GenericController.java
index ba41984f8..4751b4815 100644
--- a/src/core/org/apache/jmeter/control/GenericController.java
+++ b/src/core/org/apache/jmeter/control/GenericController.java
@@ -1,431 +1,431 @@
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
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.threads.TestCompiler;
 import org.apache.jmeter.threads.TestCompilerHelper;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * <p>
  * This class is the basis for all the controllers.
  * It also implements SimpleController.
  * </p>
  * <p>
  * The main entry point is next(), which is called by by JMeterThread as follows:
  * </p>
  * <p>
- * <code>while (running && (sampler = controller.next()) != null)</code>
+ * <code>while (running &amp;&amp; (sampler = controller.next()) != null)</code>
  * </p>
  */
 public class GenericController extends AbstractTestElement implements Controller, Serializable, TestCompilerHelper {
 
     private static final long serialVersionUID = 234L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private transient LinkedList<LoopIterationListener> iterationListeners =
         new LinkedList<LoopIterationListener>();
 
     // Only create the map if it is required
     private transient ConcurrentMap<TestElement, Object> children = 
             TestCompiler.IS_USE_STATIC_SET ? null : new ConcurrentHashMap<TestElement, Object>();
 
     private static final Object DUMMY = new Object();
 
     // May be replaced by RandomOrderController
     protected transient List<TestElement> subControllersAndSamplers =
         new ArrayList<TestElement>();
 
     /**
      * Index of current sub controller or sampler
      */
     protected transient int current;
 
     /**
      * TODO document this
      */
     private transient int iterCount;
     
     /**
      * Controller has ended
      */
     private transient boolean done;
     
     /**
      * First sampler or sub-controller
      */
     private transient boolean first;
 
     /**
      * Creates a Generic Controller
      */
     public GenericController() {
     }
 
     @Override
     public void initialize() {
         resetCurrent();
         resetIterCount();
         done = false; // TODO should this use setDone()?
         first = true; // TODO should this use setFirst()?        
         initializeSubControllers();
     }
 
     /**
      * (re)Initializes sub controllers
      * See Bug 50032
      */
     protected void initializeSubControllers() {
         for (TestElement te : subControllersAndSamplers) {
             if(te instanceof GenericController) {
                 ((Controller) te).initialize();
             }
         }
     }
 
     /**
      * Resets the controller (called after execution of last child of controller):
      * <ul>
      * <li>resetCurrent() (i.e. current=0)</li>
      * <li>increment iteration count</li>
      * <li>sets first=true</li>
      * <li>recoverRunningVersion() to set the controller back to the initial state</li>
      * </ul>
      *
      */
     protected void reInitialize() {
         resetCurrent();
         incrementIterCount();
         setFirst(true);
         recoverRunningVersion();
     }
 
     /**
      * <p>
      * Determines the next sampler to be processed.
      * </p>
      *
      * <p>
      * If isDone, returns null.
      * </p>
      *
      * <p>
      * Gets the list element using current pointer.
      * If this is null, calls {@link #nextIsNull()}.
      * </p>
      *
      * <p>
      * If the list element is a sampler, calls {@link #nextIsASampler(Sampler)},
      * otherwise calls {@link #nextIsAController(Controller)}
      * </p>
      *
      * <p>
      * If any of the called methods throws NextIsNullException, returns null,
      * otherwise the value obtained above is returned.
      * </p>
      *
      * @return the next sampler or null
      */
     @Override
     public Sampler next() {
         fireIterEvents();
         if (log.isDebugEnabled()) {
             log.debug("Calling next on: " + this.getClass().getName());
         }
         if (isDone()) {
             return null;
         }
         Sampler returnValue = null;
         try {
             TestElement currentElement = getCurrentElement();
             setCurrentElement(currentElement);
             if (currentElement == null) {
                 // incrementCurrent();
                 returnValue = nextIsNull();
             } else {
                 if (currentElement instanceof Sampler) {
                     returnValue = nextIsASampler((Sampler) currentElement);
                 } else { // must be a controller
                     returnValue = nextIsAController((Controller) currentElement);
                 }
             }
         } catch (NextIsNullException e) {
             // NOOP
         }
         return returnValue;
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#isDone()
      */
     @Override
     public boolean isDone() {
         return done;
     }
 
     protected void setDone(boolean done) {
         this.done = done;
     }
 
     /**
      * @return true if it's the controller is returning the first of its children
      */
     protected boolean isFirst() {
         return first;
     }
 
     /**
      * If b is true, it means first is reset which means Controller has executed all its children 
      * @param b
      */
     public void setFirst(boolean b) {
         first = b;
     }
 
     /**
      * Called by next() if the element is a Controller,
      * and returns the next sampler from the controller.
      * If this is null, then updates the current pointer and makes recursive call to next().
      * @param controller
      * @return the next sampler
      * @throws NextIsNullException
      */
     protected Sampler nextIsAController(Controller controller) throws NextIsNullException {
         Sampler sampler = controller.next();
         if (sampler == null) {
             currentReturnedNull(controller);
             sampler = next();
         }
         return sampler;
     }
 
     /**
      * Increment the current pointer and return the element.
      * Called by next() if the element is a sampler.
      * (May be overriden by sub-classes).
      *
      * @param element
      * @return input element
      * @throws NextIsNullException
      */
     protected Sampler nextIsASampler(Sampler element) throws NextIsNullException {
         incrementCurrent();
         return element;
     }
 
     /**
      * Called by next() when getCurrentElement() returns null.
      * Reinitialises the controller.
      *
      * @return null (always, for this class)
      * @throws NextIsNullException
      */
     protected Sampler nextIsNull() throws NextIsNullException {
         reInitialize();
         return null;
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void triggerEndOfLoop() {
         reInitialize();
     }
 
     /**
      * Called to re-initialize a index of controller's elements (Bug 50032)
      * @deprecated replaced by GeneriController#initializeSubControllers
      */
     protected void reInitializeSubController() {
         initializeSubControllers();
     }
     
     /**
      * If the controller is done, remove it from the list,
      * otherwise increment to next entry in list.
      *
      * @param c controller
      */
     protected void currentReturnedNull(Controller c) {
         if (c.isDone()) {
             removeCurrentElement();
         } else {
             incrementCurrent();
         }
     }
 
     /**
      * Gets the SubControllers attribute of the GenericController object
      *
      * @return the SubControllers value
      */
     protected List<TestElement> getSubControllers() {
         return subControllersAndSamplers;
     }
 
     private void addElement(TestElement child) {
         subControllersAndSamplers.add(child);
     }
 
     /**
      * Empty implementation - does nothing.
      *
      * @param currentElement
      * @throws NextIsNullException
      */
     protected void setCurrentElement(TestElement currentElement) throws NextIsNullException {
     }
 
     /**
      * <p>
      * Gets the element indicated by the <code>current</code> index, if one exists,
      * from the <code>subControllersAndSamplers</code> list.
      * </p>
      * <p>
      * If the <code>subControllersAndSamplers</code> list is empty,
      * then set done = true, and throw NextIsNullException.
      * </p>
      * @return the current element - or null if current index too large
      * @throws NextIsNullException if list is empty
      */
     protected TestElement getCurrentElement() throws NextIsNullException {
         if (current < subControllersAndSamplers.size()) {
             return subControllersAndSamplers.get(current);
         }
         if (subControllersAndSamplers.size() == 0) {
             setDone(true);
             throw new NextIsNullException();
         }
         return null;
     }
 
     protected void removeCurrentElement() {
         subControllersAndSamplers.remove(current);
     }
 
     /**
      * Increments the current pointer; called by currentReturnedNull to move the
      * controller on to its next child.
      */
     protected void incrementCurrent() {
         current++;
     }
 
     protected void resetCurrent() {
         current = 0;
     }
 
     @Override
     public void addTestElement(TestElement child) {
         if (child instanceof Controller || child instanceof Sampler) {
             addElement(child);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public final boolean addTestElementOnce(TestElement child){
         if (children.putIfAbsent(child, DUMMY) == null) {
             addTestElement(child);
             return true;
         }
         return false;
     }
 
     @Override
     public void addIterationListener(LoopIterationListener lis) {
         /*
          * A little hack - add each listener to the start of the list - this
          * ensures that the thread running the show is the first listener and
          * can modify certain values before other listeners are called.
          */
         iterationListeners.addFirst(lis);
     }
     
     /**
      * Remove listener
      */
     @Override
     public void removeIterationListener(LoopIterationListener iterationListener) {
         for (Iterator<LoopIterationListener> iterator = iterationListeners.iterator(); iterator.hasNext();) {
             LoopIterationListener listener = iterator.next();
             if(listener == iterationListener)
             {
                 iterator.remove();
                 break; // can only match once
             }
         }
     }
 
     protected void fireIterEvents() {
         if (isFirst()) {
             fireIterationStart();
             first = false; // TODO - should this use setFirst() ?
         }
     }
 
     protected void fireIterationStart() {
         LoopIterationEvent event = new LoopIterationEvent(this, getIterCount());
         for (LoopIterationListener item : iterationListeners) {
             item.iterationStart(event);
         }
     }
 
     protected int getIterCount() {
         return iterCount;
     }
 
     protected void incrementIterCount() {
         iterCount++;
     }
 
     protected void resetIterCount() {
         iterCount = 0;
     }
     
     protected Object readResolve(){
         iterationListeners =
                 new LinkedList<LoopIterationListener>();
         children = 
                 TestCompiler.IS_USE_STATIC_SET ? null : new ConcurrentHashMap<TestElement, Object>();
         
         subControllersAndSamplers =
                 new ArrayList<TestElement>();
 
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/control/IfController.java b/src/core/org/apache/jmeter/control/IfController.java
index 49cc446ed..88e9dac80 100644
--- a/src/core/org/apache/jmeter/control/IfController.java
+++ b/src/core/org/apache/jmeter/control/IfController.java
@@ -1,209 +1,209 @@
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
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.mozilla.javascript.Context;
 import org.mozilla.javascript.Scriptable;
 
 /**
  *
  *
  * This is a Conditional Controller; it will execute the set of statements
  * (samplers/controllers, etc) while the 'condition' is true.
  * <p>
  * In a programming world - this is equivalant of :
  * <pre>
  * if (condition) {
  *          statements ....
  *          }
  * </pre>
  * In JMeter you may have :
  * <pre> 
  * Thread-Group (set to loop a number of times or indefinitely,
  *    ... Samplers ... (e.g. Counter )
  *    ... Other Controllers ....
- *    ... IfController ( condition set to something like - ${counter}<10)
+ *    ... IfController ( condition set to something like - ${counter} &lt 10)
  *       ... statements to perform if condition is true
  *       ...
  *    ... Other Controllers /Samplers }
  * </pre>
  */
 
 // for unit test code @see TestIfController
 
 public class IfController extends GenericController implements Serializable {
 
     private static final Logger logger = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final String CONDITION = "IfController.condition"; //$NON-NLS-1$
 
     private static final String EVALUATE_ALL = "IfController.evaluateAll"; //$NON-NLS-1$
 
     private static final String USE_EXPRESSION = "IfController.useExpression"; //$NON-NLS-1$
 
     /**
      * constructor
      */
     public IfController() {
         super();
     }
 
     /**
      * constructor
      */
     public IfController(String condition) {
         super();
         this.setCondition(condition);
     }
 
     /**
-     * Condition Accessor - this is gonna be like ${count}<10
+     * Condition Accessor - this is gonna be like <code>${count} &lt; 10</code>
      */
     public void setCondition(String condition) {
         setProperty(new StringProperty(CONDITION, condition));
     }
 
     /**
-     * Condition Accessor - this is gonna be like ${count}<10
+     * Condition Accessor - this is gonna be like <code>${count} &lt; 10</code>
      */
     public String getCondition() {
         return getPropertyAsString(CONDITION);
     }
 
     /**
      * evaluate the condition clause log error if bad condition
      */
     private boolean evaluateCondition(String cond) {
         logger.debug("    getCondition() : [" + cond + "]");
 
         String resultStr = "";
         boolean result = false;
 
         // now evaluate the condition using JavaScript
         Context cx = Context.enter();
         try {
             Scriptable scope = cx.initStandardObjects(null);
             Object cxResultObject = cx.evaluateString(scope, cond
             /** * conditionString ** */
             , "<cmd>", 1, null);
             resultStr = Context.toString(cxResultObject);
 
             if (resultStr.equals("false")) { //$NON-NLS-1$
                 result = false;
             } else if (resultStr.equals("true")) { //$NON-NLS-1$
                 result = true;
             } else {
                 throw new Exception(" BAD CONDITION :: " + cond + " :: expected true or false");
             }
 
             logger.debug("    >> evaluate Condition -  [ " + cond + "] results is  [" + result + "]");
         } catch (Exception e) {
             logger.error(getName()+": error while processing "+ "[" + cond + "]\n", e);
         } finally {
             Context.exit();
         }
 
         return result;
     }
 
     private static boolean evaluateExpression(String cond) {
         return cond.equalsIgnoreCase("true"); // $NON-NLS-1$
     }
 
     /**
      * This is overriding the parent method. IsDone indicates whether the
      * termination condition is reached. I.e. if the condition evaluates to
      * False - then isDone() returns TRUE
      */
     @Override
     public boolean isDone() {
         // boolean result = true;
         // try {
         // result = !evaluateCondition();
         // } catch (Exception e) {
         // logger.error(e.getMessage(), e);
         // }
         // setDone(true);
         // return result;
         // setDone(false);
         return false;
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#next()
      */
     @Override
     public Sampler next() {
         // We should only evalute the condition if it is the first
         // time ( first "iteration" ) we are called.
         // For subsequent calls, we are inside the IfControllerGroup,
         // so then we just pass the control to the next item inside the if control
         boolean result = true;
         if(isEvaluateAll() || isFirst()) {
             result = isUseExpression() ? 
                     evaluateExpression(getCondition())
                     :
                     evaluateCondition(getCondition());
         }
 
         if (result) {
             return super.next();
         }
         // If-test is false, need to re-initialize indexes
         try {
             initializeSubControllers();
             return nextIsNull();
         } catch (NextIsNullException e1) {
             return null;
         }
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void triggerEndOfLoop() {
         super.initializeSubControllers();
         super.triggerEndOfLoop();
     }
 
     public boolean isEvaluateAll() {
         return getPropertyAsBoolean(EVALUATE_ALL,false);
     }
 
     public void setEvaluateAll(boolean b) {
         setProperty(EVALUATE_ALL,b);
     }
 
     public boolean isUseExpression() {
         return getPropertyAsBoolean(USE_EXPRESSION, false);
     }
 
     public void setUseExpression(boolean selected) {
         setProperty(USE_EXPRESSION, selected, false);
     }
 }
diff --git a/src/core/org/apache/jmeter/util/SlowSocket.java b/src/core/org/apache/jmeter/util/SlowSocket.java
index 586b91e6c..f936fe347 100644
--- a/src/core/org/apache/jmeter/util/SlowSocket.java
+++ b/src/core/org/apache/jmeter/util/SlowSocket.java
@@ -1,110 +1,110 @@
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
 
 package org.apache.jmeter.util;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
 import java.net.Socket;
 import java.net.SocketAddress;
 import java.net.UnknownHostException;
 
 /**
  * "Slow"  (non-SSL) socket implementation to emulate dial-up modems etc
  */
 public class SlowSocket extends Socket {
 
     private final int CPS; // Characters per second to emulate
 
     public SlowSocket(final int cps, String host, int port, InetAddress localAddress, int localPort, int timeout) throws IOException {
         super();
         if (cps <=0) {
             throw new IllegalArgumentException("Speed (cps) <= 0");
         }
         CPS=cps;
         // This sequence is borrowed from:
         // org.apache.commons.httpclient.protocol.ReflectionSocketFactory.createSocket
         SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
         SocketAddress remoteaddr = new InetSocketAddress(host, port);
         bind(localaddr);
         connect(remoteaddr, timeout);
     }
 
     /**
      *
      * @param cps characters per second
      * @param host hostname
      * @param port port
      * @param localAddr local address
      * @param localPort local port
      *
      * @throws IOException
-     * @throws IllegalArgumentException if cps <=0
+     * @throws IllegalArgumentException if cps &lt;= 0
      */
     public SlowSocket(int cps, String host, int port, InetAddress localAddr, int localPort) throws IOException {
         super(host, port, localAddr, localPort);
         if (cps <=0) {
             throw new IllegalArgumentException("Speed (cps) <= 0");
         }
         CPS=cps;
     }
 
     /**
      *
      * @param cps characters per second
      * @param host hostname
      * @param port port
      *
      * @throws UnknownHostException
      * @throws IOException
-     * @throws IllegalArgumentException if cps <=0
+     * @throws IllegalArgumentException if cps &lt;= 0
      */
     public SlowSocket(int cps, String host, int port) throws UnknownHostException, IOException {
         super(host, port);
         if (cps <=0) {
             throw new IllegalArgumentException("Speed (cps) <= 0");
         }
         CPS=cps;
     }
 
     /**
      * Added for use by SlowHC4SocketFactory.
      * 
      * @param cps
      */
     public SlowSocket(int cps) {
         super();
         CPS = cps;
     }
 
     // Override so we can intercept the stream
     @Override
     public OutputStream getOutputStream() throws IOException {
         return new SlowOutputStream(super.getOutputStream(), CPS);
     }
 
     // Override so we can intercept the stream
     @Override
     public InputStream getInputStream() throws IOException {
         return new SlowInputStream(super.getInputStream(), CPS);
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/EscapeHtml.java b/src/functions/org/apache/jmeter/functions/EscapeHtml.java
index 694c6d5de..460040d6f 100644
--- a/src/functions/org/apache/jmeter/functions/EscapeHtml.java
+++ b/src/functions/org/apache/jmeter/functions/EscapeHtml.java
@@ -1,93 +1,93 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * <p>Function which escapes the characters in a <code>String</code> using HTML entities.</p>
  *
  * <p>
  * For example:
  * </p> 
- * <p><code>"bread" & "butter"</code></p>
+ * <p><code>"bread" &amp; "butter"</code></p>
  * becomes:
  * <p>
  * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
  * </p>
  *
  * <p>Supports all known HTML 4.0 entities.
  * Note that the commonly used apostrophe escape character (&amp;apos;)
  * is not a legal entity and so is not supported). </p>
  * 
  * @see StringEscapeUtils#escapeHtml4(String) (Commons Lang)
  * @since 2.3.3
  */
 public class EscapeHtml extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__escapeHtml"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("escape_html_string")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public EscapeHtml() {
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         String rawString = ((CompoundVariable) values[0]).execute();
         return StringEscapeUtils.escapeHtml4(rawString);
 
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1);
         values = parameters.toArray();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/io/TextFile.java b/src/jorphan/org/apache/jorphan/io/TextFile.java
index a578669c5..4a0b0eb89 100644
--- a/src/jorphan/org/apache/jorphan/io/TextFile.java
+++ b/src/jorphan/org/apache/jorphan/io/TextFile.java
@@ -1,209 +1,209 @@
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
 
 package org.apache.jorphan.io;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStreamWriter;
 import java.io.Reader;
 import java.io.Writer;
 
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Utility class to handle text files as a single lump of text.
  * <p>
  * Note this is just as memory-inefficient as handling a text file can be. Use
  * with restraint.
  *
  * @version $Revision$
  */
 public class TextFile extends File {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * File encoding. null means use the platform's default.
      */
     private String encoding = null;
 
     /**
      * Create a TextFile object to handle the named file with the given
      * encoding.
      *
      * @param filename
-     *            File to be read & written through this object.
+     *            File to be read and written through this object.
      * @param encoding
-     *            Encoding to be used when reading & writing this file.
+     *            Encoding to be used when reading and writing this file.
      */
     public TextFile(File filename, String encoding) {
         super(filename.toString());
         setEncoding(encoding);
     }
 
     /**
      * Create a TextFile object to handle the named file with the platform
      * default encoding.
      *
      * @param filename
-     *            File to be read & written through this object.
+     *            File to be read and written through this object.
      */
     public TextFile(File filename) {
         super(filename.toString());
     }
 
     /**
      * Create a TextFile object to handle the named file with the platform
      * default encoding.
      *
      * @param filename
-     *            Name of the file to be read & written through this object.
+     *            Name of the file to be read and written through this object.
      */
     public TextFile(String filename) {
         super(filename);
     }
 
     /**
      * Create a TextFile object to handle the named file with the given
      * encoding.
      *
      * @param filename
-     *            Name of the file to be read & written through this object.
+     *            Name of the file to be read and written through this object.
      * @param encoding
-     *            Encoding to be used when reading & writing this file.
+     *            Encoding to be used when reading and writing this file.
      */
     public TextFile(String filename, String encoding) {
         super(filename);
         setEncoding(encoding);
     }
 
     /**
      * Create the file with the given string as content -- or replace it's
      * content with the given string if the file already existed.
      *
      * @param body
      *            New content for the file.
      */
     public void setText(String body) {
         Writer writer = null;
         try {
             if (encoding == null) {
                 writer = new FileWriter(this);
             } else {
                 writer = new OutputStreamWriter(new FileOutputStream(this), encoding);
             }
             writer.write(body);
             writer.flush();
         } catch (IOException ioe) {
             log.error("", ioe);
         } finally {
             JOrphanUtils.closeQuietly(writer);
         }
     }
 
     /**
      * Read the whole file content and return it as a string.
      *
      * @return the content of the file
      */
     public String getText() {
         String lineEnd = System.getProperty("line.separator"); //$NON-NLS-1$
         StringBuilder sb = new StringBuilder();
         Reader reader = null;
         BufferedReader br = null;
         try {
             if (encoding == null) {
                 reader = new FileReader(this);
             } else {
                 reader = new InputStreamReader(new FileInputStream(this), encoding);
             }
             br = new BufferedReader(reader);
             String line = "NOTNULL"; //$NON-NLS-1$
             while (line != null) {
                 line = br.readLine();
                 if (line != null) {
                     sb.append(line + lineEnd);
                 }
             }
         } catch (IOException ioe) {
             log.error("", ioe); //$NON-NLS-1$
         } finally {
             JOrphanUtils.closeQuietly(br); // closes reader as well
         }
 
         return sb.toString();
     }
 
     /**
-     * @return Encoding being used to read & write this file.
+     * @return Encoding being used to read and write this file.
      */
     public String getEncoding() {
         return encoding;
     }
 
     /**
      * @param string
-     *            Encoding to be used to read & write this file.
+     *            Encoding to be used to read and write this file.
      */
     public void setEncoding(String string) {
         encoding = string;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public int hashCode() {
         final int prime = 31;
         int result = super.hashCode();
         result = prime * result
                 + ((encoding == null) ? 0 : encoding.hashCode());
         return result;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public boolean equals(Object obj) {
         if (this == obj) {
             return true;
         }
         if (!super.equals(obj)) {
             return false;
         }
         if (!(obj instanceof TextFile)) {
             return false;
         }
         TextFile other = (TextFile) obj;
         if (encoding == null) {
             return other.encoding == null;
         } 
         return encoding.equals(other.encoding);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
index 901cec44d..4f173d40f 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
@@ -1,760 +1,762 @@
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
 
 package org.apache.jmeter.protocol.http.config.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.FlowLayout;
 import java.awt.Font;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JPasswordField;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextField;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.JSyntaxTextArea;
 import org.apache.jmeter.gui.util.JTextScrollPane;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 
 /**
  * Basic URL / HTTP Request configuration:
- * - host and port
- * - connect and response timeouts
- * - path, method, encoding, parameters
- * - redirects & keepalive
+ * <ul>
+ * <li>host and port</li>
+ * <li>connect and response timeouts</li>
+ * <li>path, method, encoding, parameters</li>
+ * <li>redirects and keepalive</li>
+ * </ul>
  */
 public class UrlConfigGui extends JPanel implements ChangeListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final int TAB_PARAMETERS = 0;
     
     private static final int TAB_RAW_BODY = 1;
     
     private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
 
     private HTTPArgumentsPanel argsPanel;
 
     private JTextField domain;
 
     private JTextField port;
 
     private JTextField proxyHost;
 
     private JTextField proxyPort;
 
     private JTextField proxyUser;
 
     private JPasswordField proxyPass;
 
     private JTextField connectTimeOut;
 
     private JTextField responseTimeOut;
 
     private JTextField protocol;
 
     private JTextField contentEncoding;
 
     private JTextField path;
 
     private JCheckBox followRedirects;
 
     private JCheckBox autoRedirects;
 
     private JCheckBox useKeepAlive;
 
     private JCheckBox useMultipartForPost;
 
     private JCheckBox useBrowserCompatibleMultipartMode;
 
     private JLabeledChoice method;
     
     private JLabeledChoice httpImplementation;
 
     private final boolean notConfigOnly;
     // set this false to suppress some items for use in HTTP Request defaults
     
     private final boolean showImplementation; // Set false for AJP
 
     // Body data
     private JSyntaxTextArea postBodyContent;
 
     // Tabbed pane that contains parameters and raw body
     private ValidationTabbedPane postContentTabbedPane;
 
     private boolean showRawBodyPane;
 
     public UrlConfigGui() {
         this(true);
     }
 
     /**
      * @param showSamplerFields
      */
     public UrlConfigGui(boolean showSamplerFields) {
         this(showSamplerFields, true, true);
     }
 
     /**
      * @param showSamplerFields
      * @param showImplementation Show HTTP Implementation
      * @param showRawBodyPane 
      */
     public UrlConfigGui(boolean showSamplerFields, boolean showImplementation, boolean showRawBodyPane) {
         notConfigOnly=showSamplerFields;
         this.showImplementation = showImplementation;
         this.showRawBodyPane = showRawBodyPane;
         init();
     }
 
     public void clear() {
         domain.setText(""); // $NON-NLS-1$
         if (notConfigOnly){
             followRedirects.setSelected(true);
             autoRedirects.setSelected(false);
             method.setText(HTTPSamplerBase.DEFAULT_METHOD);
             useKeepAlive.setSelected(true);
             useMultipartForPost.setSelected(false);
             useBrowserCompatibleMultipartMode.setSelected(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
         }
         if (showImplementation) {
             httpImplementation.setText(""); // $NON-NLS-1$
         }
         path.setText(""); // $NON-NLS-1$
         port.setText(""); // $NON-NLS-1$
         proxyHost.setText(""); // $NON-NLS-1$
         proxyPort.setText(""); // $NON-NLS-1$
         proxyUser.setText(""); // $NON-NLS-1$
         proxyPass.setText(""); // $NON-NLS-1$
         connectTimeOut.setText(""); // $NON-NLS-1$
         responseTimeOut.setText(""); // $NON-NLS-1$
         protocol.setText(""); // $NON-NLS-1$
         contentEncoding.setText(""); // $NON-NLS-1$
         argsPanel.clear();
         if(showRawBodyPane) {
             postBodyContent.setInitialText("");// $NON-NLS-1$
         }
         postContentTabbedPane.setSelectedIndex(TAB_PARAMETERS, false);
     }
 
     public TestElement createTestElement() {
         ConfigTestElement element = new ConfigTestElement();
 
         element.setName(this.getName());
         element.setProperty(TestElement.GUI_CLASS, this.getClass().getName());
         element.setProperty(TestElement.TEST_CLASS, element.getClass().getName());
         modifyTestElement(element);
         return element;
     }
 
     /**
      * Save the GUI values in the sampler.
      *
      * @param element
      */
     public void modifyTestElement(TestElement element) {
         boolean useRaw = postContentTabbedPane.getSelectedIndex()==TAB_RAW_BODY;
         Arguments args;
         if(useRaw) {
             args = new Arguments();
             String text = postBodyContent.getText();
             /*
              * Textfield uses \n (LF) to delimit lines; we need to send CRLF.
              * Rather than change the way that arguments are processed by the
              * samplers for raw data, it is easier to fix the data.
              * On retrival, CRLF is converted back to LF for storage in the text field.
              * See
              */
             HTTPArgument arg = new HTTPArgument("", text.replaceAll("\n","\r\n"), false);
             arg.setAlwaysEncoded(false);
             args.addArgument(arg);
         } else {
             args = (Arguments) argsPanel.createTestElement();
             HTTPArgument.convertArgumentsToHTTP(args);
         }
         element.setProperty(HTTPSamplerBase.POST_BODY_RAW, useRaw, HTTPSamplerBase.POST_BODY_RAW_DEFAULT);
         element.setProperty(new TestElementProperty(HTTPSamplerBase.ARGUMENTS, args));
         element.setProperty(HTTPSamplerBase.DOMAIN, domain.getText());
         element.setProperty(HTTPSamplerBase.PORT, port.getText());
         element.setProperty(HTTPSamplerBase.PROXYHOST, proxyHost.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYPORT, proxyPort.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYUSER, proxyUser.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYPASS, String.valueOf(proxyPass.getPassword()),"");
         element.setProperty(HTTPSamplerBase.CONNECT_TIMEOUT, connectTimeOut.getText());
         element.setProperty(HTTPSamplerBase.RESPONSE_TIMEOUT, responseTimeOut.getText());
         element.setProperty(HTTPSamplerBase.PROTOCOL, protocol.getText());
         element.setProperty(HTTPSamplerBase.CONTENT_ENCODING, contentEncoding.getText());
         element.setProperty(HTTPSamplerBase.PATH, path.getText());
         if (notConfigOnly){
             element.setProperty(HTTPSamplerBase.METHOD, method.getText());
             element.setProperty(new BooleanProperty(HTTPSamplerBase.FOLLOW_REDIRECTS, followRedirects.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.AUTO_REDIRECTS, autoRedirects.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.USE_KEEPALIVE, useKeepAlive.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.DO_MULTIPART_POST, useMultipartForPost.isSelected()));
             element.setProperty(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART, useBrowserCompatibleMultipartMode.isSelected(),HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
         }
         if (showImplementation) {
             element.setProperty(HTTPSamplerBase.IMPLEMENTATION, httpImplementation.getText(),"");
         }
     }
 
     // FIXME FACTOR WITH HTTPHC4Impl, HTTPHC3Impl
     // Just append all the parameter values, and use that as the post body
     /**
      * Compute body data from arguments
      * @param arguments {@link Arguments}
      * @return {@link String}
      */
     private static final String computePostBody(Arguments arguments) {
         return computePostBody(arguments, false);
     }
 
     /**
      * Compute body data from arguments
      * @param arguments {@link Arguments}
      * @param crlfToLF whether to convert CRLF to LF
      * @return {@link String}
      */
     private static final String computePostBody(Arguments arguments, boolean crlfToLF) {
         StringBuilder postBody = new StringBuilder();
         PropertyIterator args = arguments.iterator();
         while (args.hasNext()) {
             HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
             String value = arg.getValue();
             if (crlfToLF) {
                 value=value.replaceAll("\r\n", "\n"); // See modifyTestElement
             }
             postBody.append(value);
         }
         return postBody.toString();
     }
 
     /**
      * Set the text, etc. in the UI.
      *
      * @param el
      *            contains the data to be displayed
      */
     public void configure(TestElement el) {
         setName(el.getName());
         Arguments arguments = (Arguments) el.getProperty(HTTPSamplerBase.ARGUMENTS).getObjectValue();
 
         boolean useRaw = el.getPropertyAsBoolean(HTTPSamplerBase.POST_BODY_RAW, HTTPSamplerBase.POST_BODY_RAW_DEFAULT);
         if(useRaw) {
             String postBody = computePostBody(arguments, true); // Convert CRLF to CR, see modifyTestElement
             postBodyContent.setInitialText(postBody); 
             postBodyContent.setCaretPosition(0);
             postContentTabbedPane.setSelectedIndex(TAB_RAW_BODY, false);
         } else {
             argsPanel.configure(arguments);
             postContentTabbedPane.setSelectedIndex(TAB_PARAMETERS, false);
         }
 
         domain.setText(el.getPropertyAsString(HTTPSamplerBase.DOMAIN));
 
         String portString = el.getPropertyAsString(HTTPSamplerBase.PORT);
 
         // Only display the port number if it is meaningfully specified
         if (portString.equals(HTTPSamplerBase.UNSPECIFIED_PORT_AS_STRING)) {
             port.setText(""); // $NON-NLS-1$
         } else {
             port.setText(portString);
         }
         proxyHost.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYHOST));
         proxyPort.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYPORT));
         proxyUser.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYUSER));
         proxyPass.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYPASS));
         connectTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.CONNECT_TIMEOUT));
         responseTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.RESPONSE_TIMEOUT));
         protocol.setText(el.getPropertyAsString(HTTPSamplerBase.PROTOCOL));
         contentEncoding.setText(el.getPropertyAsString(HTTPSamplerBase.CONTENT_ENCODING));
         path.setText(el.getPropertyAsString(HTTPSamplerBase.PATH));
         if (notConfigOnly){
             method.setText(el.getPropertyAsString(HTTPSamplerBase.METHOD));
             followRedirects.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.FOLLOW_REDIRECTS));
             autoRedirects.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.AUTO_REDIRECTS));
             useKeepAlive.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.USE_KEEPALIVE));
             useMultipartForPost.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.DO_MULTIPART_POST));
             useBrowserCompatibleMultipartMode.setSelected(el.getPropertyAsBoolean(
                     HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART, HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT));
         }
         if (showImplementation) {
             httpImplementation.setText(el.getPropertyAsString(HTTPSamplerBase.IMPLEMENTATION));
         }
     }
 
     private void init() {// called from ctor, so must not be overridable
         this.setLayout(new BorderLayout());
 
         // WEB REQUEST PANEL
         JPanel webRequestPanel = new JPanel();
         webRequestPanel.setLayout(new BorderLayout());
         webRequestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_request"))); // $NON-NLS-1$
 
         JPanel northPanel = new JPanel();
         northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
         northPanel.add(getProtocolAndMethodPanel());
         northPanel.add(getPathPanel());
 
         webRequestPanel.add(northPanel, BorderLayout.NORTH);
         webRequestPanel.add(getParameterPanel(), BorderLayout.CENTER);
 
         this.add(getWebServerTimeoutPanel(), BorderLayout.NORTH);
         this.add(webRequestPanel, BorderLayout.CENTER);
         this.add(getProxyServerPanel(), BorderLayout.SOUTH); 
     }
 
     /**
      * Create a panel containing the webserver (domain+port) and timeouts (connect+request).
      *
      * @return the panel
      */
     protected final JPanel getWebServerTimeoutPanel() {
         // WEB SERVER PANEL
         JPanel webServerPanel = new HorizontalPanel();
         webServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_server"))); // $NON-NLS-1$
         final JPanel domainPanel = getDomainPanel();
         final JPanel portPanel = getPortPanel();
         webServerPanel.add(domainPanel, BorderLayout.CENTER);
         webServerPanel.add(portPanel, BorderLayout.EAST);
 
         JPanel timeOut = new HorizontalPanel();
         timeOut.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_server_timeout_title"))); // $NON-NLS-1$
         final JPanel connPanel = getConnectTimeOutPanel();
         final JPanel reqPanel = getResponseTimeOutPanel();
         timeOut.add(connPanel);
         timeOut.add(reqPanel);
 
         JPanel webServerTimeoutPanel = new VerticalPanel();
         webServerTimeoutPanel.add(webServerPanel, BorderLayout.CENTER);
         webServerTimeoutPanel.add(timeOut, BorderLayout.EAST);
 
         JPanel bigPanel = new VerticalPanel();
         bigPanel.add(webServerTimeoutPanel);
         return bigPanel;
     }
 
     /**
      * Create a panel containing the proxy server details
      *
      * @return the panel
      */
     protected final JPanel getProxyServerPanel(){
         JPanel proxyServer = new HorizontalPanel();
         proxyServer.add(getProxyHostPanel(), BorderLayout.CENTER);
         proxyServer.add(getProxyPortPanel(), BorderLayout.EAST);
 
         JPanel proxyLogin = new HorizontalPanel();
         proxyLogin.add(getProxyUserPanel());
         proxyLogin.add(getProxyPassPanel());
 
         JPanel proxyServerPanel = new HorizontalPanel();
         proxyServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_proxy_server_title"))); // $NON-NLS-1$
         proxyServerPanel.add(proxyServer);
         proxyServerPanel.add(proxyLogin);
 
         return proxyServerPanel;
     }
 
     private JPanel getPortPanel() {
         port = new JTextField(10);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(port);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(port, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getProxyPortPanel() {
         proxyPort = new JTextField(10);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(proxyPort);
         label.setFont(FONT_SMALL);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPort, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getConnectTimeOutPanel() {
         connectTimeOut = new JTextField(10);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_connect")); // $NON-NLS-1$
         label.setLabelFor(connectTimeOut);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(connectTimeOut, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getResponseTimeOutPanel() {
         responseTimeOut = new JTextField(10);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_response")); // $NON-NLS-1$
         label.setLabelFor(responseTimeOut);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(responseTimeOut, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getDomainPanel() {
         domain = new JTextField(20);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(domain);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(domain, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyHostPanel() {
         proxyHost = new JTextField(10);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(proxyHost);
         label.setFont(FONT_SMALL);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyHost, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyUserPanel() {
         proxyUser = new JTextField(5);
 
         JLabel label = new JLabel(JMeterUtils.getResString("username")); // $NON-NLS-1$
         label.setLabelFor(proxyUser);
         label.setFont(FONT_SMALL);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyUser, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyPassPanel() {
         proxyPass = new JPasswordField(5);
 
         JLabel label = new JLabel(JMeterUtils.getResString("password")); // $NON-NLS-1$
         label.setLabelFor(proxyPass);
         label.setFont(FONT_SMALL);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPass, BorderLayout.CENTER);
         return panel;
     }
 
     /**
      * This method defines the Panel for the HTTP path, 'Follow Redirects'
      * 'Use KeepAlive', and 'Use multipart for HTTP POST' elements.
      *
      * @return JPanel The Panel for the path, 'Follow Redirects' and 'Use
      *         KeepAlive' elements.
      */
     protected Component getPathPanel() {
         path = new JTextField(15);
 
         JLabel label = new JLabel(JMeterUtils.getResString("path")); //$NON-NLS-1$
         label.setLabelFor(path);
 
         if (notConfigOnly){
             followRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects")); // $NON-NLS-1$
             followRedirects.setFont(null);
             followRedirects.setSelected(true);
             followRedirects.addChangeListener(this);
 
             autoRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects_auto")); //$NON-NLS-1$
             autoRedirects.setFont(null);
             autoRedirects.addChangeListener(this);
             autoRedirects.setSelected(false);// Default changed in 2.3 and again in 2.4
 
             useKeepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive")); // $NON-NLS-1$
             useKeepAlive.setFont(null);
             useKeepAlive.setSelected(true);
 
             useMultipartForPost = new JCheckBox(JMeterUtils.getResString("use_multipart_for_http_post")); // $NON-NLS-1$
             useMultipartForPost.setFont(null);
             useMultipartForPost.setSelected(false);
 
             useBrowserCompatibleMultipartMode = new JCheckBox(JMeterUtils.getResString("use_multipart_mode_browser")); // $NON-NLS-1$
             useBrowserCompatibleMultipartMode.setFont(null);
             useBrowserCompatibleMultipartMode.setSelected(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
 
         }
 
         JPanel pathPanel = new HorizontalPanel();
         pathPanel.add(label);
         pathPanel.add(path);
 
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.add(pathPanel);
         if (notConfigOnly){
             JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
             optionPanel.setFont(FONT_SMALL); // all sub-components with setFont(null) inherit this font
             optionPanel.add(autoRedirects);
             optionPanel.add(followRedirects);
             optionPanel.add(useKeepAlive);
             optionPanel.add(useMultipartForPost);
             optionPanel.add(useBrowserCompatibleMultipartMode);
             optionPanel.setMinimumSize(optionPanel.getPreferredSize());
             panel.add(optionPanel);
         }
 
         return panel;
     }
 
     protected JPanel getProtocolAndMethodPanel() {
 
         // Implementation
         
         if (showImplementation) {
             httpImplementation = new JLabeledChoice(JMeterUtils.getResString("http_implementation"), // $NON-NLS-1$
                     HTTPSamplerFactory.getImplementations());
             httpImplementation.addValue("");
         }
         // PROTOCOL
         protocol = new JTextField(4);
         JLabel protocolLabel = new JLabel(JMeterUtils.getResString("protocol")); // $NON-NLS-1$
         protocolLabel.setLabelFor(protocol);        
         
         // CONTENT_ENCODING
         contentEncoding = new JTextField(10);
         JLabel contentEncodingLabel = new JLabel(JMeterUtils.getResString("content_encoding")); // $NON-NLS-1$
         contentEncodingLabel.setLabelFor(contentEncoding);
 
         if (notConfigOnly){
             method = new JLabeledChoice(JMeterUtils.getResString("method"), // $NON-NLS-1$
                     HTTPSamplerBase.getValidMethodsAsArray());
         }
 
         JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
 
         if (showImplementation) {
             panel.add(httpImplementation);
         }
         panel.add(protocolLabel);
         panel.add(protocol);
         panel.add(Box.createHorizontalStrut(5));
 
         if (notConfigOnly){
             panel.add(method);
         }
         panel.setMinimumSize(panel.getPreferredSize());
         panel.add(Box.createHorizontalStrut(5));
 
         panel.add(contentEncodingLabel);
         panel.add(contentEncoding);
         panel.setMinimumSize(panel.getPreferredSize());
         return panel;
     }
 
     protected JTabbedPane getParameterPanel() {
         postContentTabbedPane = new ValidationTabbedPane();
         argsPanel = new HTTPArgumentsPanel();
         postContentTabbedPane.add(JMeterUtils.getResString("post_as_parameters"), argsPanel);// $NON-NLS-1$
         if(showRawBodyPane) {
             postBodyContent = new JSyntaxTextArea(30, 50);// $NON-NLS-1$
             postContentTabbedPane.add(JMeterUtils.getResString("post_body"), new JTextScrollPane(postBodyContent));// $NON-NLS-1$
         }
         return postContentTabbedPane;
     }
 
     /**
      * 
      */
     class ValidationTabbedPane extends JTabbedPane{
 
         /**
          * 
          */
         private static final long serialVersionUID = 7014311238367882880L;
 
         /* (non-Javadoc)
          * @see javax.swing.JTabbedPane#setSelectedIndex(int)
          */
         @Override
         public void setSelectedIndex(int index) {
             setSelectedIndex(index, true);
         }
         /**
          * Apply some check rules if check is true
          */
         public void setSelectedIndex(int index, boolean check) {
             int oldSelectedIndex = getSelectedIndex();
             if(!check || oldSelectedIndex==-1) {
                 super.setSelectedIndex(index);
             }
             else if(index != this.getSelectedIndex())
             {
                 if(noData(getSelectedIndex())) {
                     // If there is no data, then switching between Parameters and Raw should be
                     // allowed with no further user interaction.
                     argsPanel.clear();
                     postBodyContent.setInitialText("");
                     super.setSelectedIndex(index);
                 }
                 else { 
                     if(oldSelectedIndex == TAB_RAW_BODY) {
                         // If RAW data and Parameters match we allow switching
                         if(postBodyContent.getText().equals(computePostBody((Arguments)argsPanel.createTestElement()).trim())) {
                             super.setSelectedIndex(index);
                         }
                         else {
                             // If there is data in the Raw panel, then the user should be 
                             // prevented from switching (that would be easy to track).
                             JOptionPane.showConfirmDialog(this,
                                     JMeterUtils.getResString("web_cannot_switch_tab"), // $NON-NLS-1$
                                     JMeterUtils.getResString("warning"), // $NON-NLS-1$
                                     JOptionPane.DEFAULT_OPTION, 
                                     JOptionPane.ERROR_MESSAGE);
                             return;
                         }
                     }
                     else {
                         // If the Parameter data can be converted (i.e. no names), we 
                         // warn the user that the Parameter data will be lost.
                         if(canConvertParameters()) {
                             Object[] options = {
                                     JMeterUtils.getResString("confirm"), // $NON-NLS-1$
                                     JMeterUtils.getResString("cancel")}; // $NON-NLS-1$
                             int n = JOptionPane.showOptionDialog(this,
                                 JMeterUtils.getResString("web_parameters_lost_message"), // $NON-NLS-1$
                                 JMeterUtils.getResString("warning"), // $NON-NLS-1$
                                 JOptionPane.YES_NO_CANCEL_OPTION,
                                 JOptionPane.QUESTION_MESSAGE,
                                 null,
                                 options,
                                 options[1]);
                             if(n == JOptionPane.YES_OPTION) {
                                 convertParametersToRaw();
                                 super.setSelectedIndex(index);
                             }
                             else{
                                 return;
                             }
                         }
                         else {
                             // If the Parameter data cannot be converted to Raw, then the user should be
                             // prevented from doing so raise an error dialog
                             JOptionPane.showConfirmDialog(this,
                                     JMeterUtils.getResString("web_cannot_convert_parameters_to_raw"), // $NON-NLS-1$
                                     JMeterUtils.getResString("warning"), // $NON-NLS-1$
                                     JOptionPane.DEFAULT_OPTION, 
                                     JOptionPane.ERROR_MESSAGE);
                             return;
                         }
                     }
                 }
             }
         }   
     }
     // autoRedirects and followRedirects cannot both be selected
     @Override
     public void stateChanged(ChangeEvent e) {
         if (e.getSource() == autoRedirects){
             if (autoRedirects.isSelected()) {
                 followRedirects.setSelected(false);
             }
         }
         if (e.getSource() == followRedirects){
             if (followRedirects.isSelected()) {
                 autoRedirects.setSelected(false);
             }
         }
     }
 
 
     /**
      * Convert Parameters to Raw Body
      */
     void convertParametersToRaw() {
         postBodyContent.setInitialText(computePostBody((Arguments)argsPanel.createTestElement()));
         postBodyContent.setCaretPosition(0);
     }
 
     /**
      * 
      * @return true if no argument has a name
      */
     boolean canConvertParameters() {
         Arguments arguments = (Arguments)argsPanel.createTestElement();
         for (int i = 0; i < arguments.getArgumentCount(); i++) {
             if(!StringUtils.isEmpty(arguments.getArgument(i).getName())) {
                 return false;
             }
         }
         return true;
     }
 
     /**
      * @return true if neither Parameters tab nor Raw Body tab contain data
      */
     boolean noData(int oldSelectedIndex) {
         if(oldSelectedIndex == TAB_RAW_BODY) {
             return StringUtils.isEmpty(postBodyContent.getText().trim());
         }
         else {
             Arguments element = (Arguments)argsPanel.createTestElement();
             return StringUtils.isEmpty(computePostBody(element));
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
index 412ef92ae..6267f3055 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
@@ -1,267 +1,267 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 
 import java.net.URL;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HtmlParsers can parse HTML content to obtain URLs.
  *
  */
 public abstract class HTMLParser {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     protected static final String ATT_BACKGROUND    = "background";// $NON-NLS-1$
     protected static final String ATT_CODE          = "code";// $NON-NLS-1$
     protected static final String ATT_CODEBASE      = "codebase";// $NON-NLS-1$
     protected static final String ATT_DATA          = "data";// $NON-NLS-1$
     protected static final String ATT_HREF          = "href";// $NON-NLS-1$
     protected static final String ATT_REL           = "rel";// $NON-NLS-1$
     protected static final String ATT_SRC           = "src";// $NON-NLS-1$
     protected static final String ATT_STYLE         = "style";// $NON-NLS-1$
     protected static final String ATT_TYPE          = "type";// $NON-NLS-1$
     protected static final String ATT_IS_IMAGE      = "image";// $NON-NLS-1$
     protected static final String TAG_APPLET        = "applet";// $NON-NLS-1$
     protected static final String TAG_BASE          = "base";// $NON-NLS-1$
     protected static final String TAG_BGSOUND       = "bgsound";// $NON-NLS-1$
     protected static final String TAG_BODY          = "body";// $NON-NLS-1$
     protected static final String TAG_EMBED         = "embed";// $NON-NLS-1$
     protected static final String TAG_FRAME         = "frame";// $NON-NLS-1$
     protected static final String TAG_IFRAME        = "iframe";// $NON-NLS-1$
     protected static final String TAG_IMAGE         = "img";// $NON-NLS-1$
     protected static final String TAG_INPUT         = "input";// $NON-NLS-1$
     protected static final String TAG_LINK          = "link";// $NON-NLS-1$
     protected static final String TAG_OBJECT        = "object";// $NON-NLS-1$
     protected static final String TAG_SCRIPT        = "script";// $NON-NLS-1$
     protected static final String STYLESHEET        = "stylesheet";// $NON-NLS-1$
 
     protected static final String IE_UA             = "MSIE ([0-9]+.[0-9]+)";// $NON-NLS-1$
     protected static final Pattern IE_UA_PATTERN    = Pattern.compile(IE_UA);
     private   static final float IE_10                = 10.0f;
 
     // Cache of parsers - parsers must be re-usable
     private static final Map<String, HTMLParser> parsers = new ConcurrentHashMap<String, HTMLParser>(4);
 
     public static final String PARSER_CLASSNAME = "htmlParser.className"; // $NON-NLS-1$
 
     public static final String DEFAULT_PARSER =
         "org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser"; // $NON-NLS-1$
 
     /**
      * Protected constructor to prevent instantiation except from within
      * subclasses.
      */
     protected HTMLParser() {
     }
 
     public static final HTMLParser getParser() {
         return getParser(JMeterUtils.getPropDefault(PARSER_CLASSNAME, DEFAULT_PARSER));
     }
 
     public static final HTMLParser getParser(String htmlParserClassName) {
 
         // Is there a cached parser?
         HTMLParser pars = parsers.get(htmlParserClassName);
         if (pars != null) {
             log.debug("Fetched " + htmlParserClassName);
             return pars;
         }
 
         try {
             Object clazz = Class.forName(htmlParserClassName).newInstance();
             if (clazz instanceof HTMLParser) {
                 pars = (HTMLParser) clazz;
             } else {
                 throw new HTMLParseError(new ClassCastException(htmlParserClassName));
             }
         } catch (InstantiationException e) {
             throw new HTMLParseError(e);
         } catch (IllegalAccessException e) {
             throw new HTMLParseError(e);
         } catch (ClassNotFoundException e) {
             throw new HTMLParseError(e);
         }
         log.info("Created " + htmlParserClassName);
         if (pars.isReusable()) {
             parsers.put(htmlParserClassName, pars);// cache the parser
         }
 
         return pars;
     }
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      * <p>
      * URLs should not appear twice in the returned iterator.
      * <p>
      * Malformed URLs can be reported to the caller by having the Iterator
      * return the corresponding RL String. Overall problems parsing the html
      * should be reported by throwing an HTMLParseException.
      * @param userAgent
      *            User Agent
      *
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      */
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, String encoding) throws HTMLParseException {
         // The Set is used to ignore duplicated binary files.
         // Using a LinkedHashSet to avoid unnecessary overhead in iterating
         // the elements in the set later on. As a side-effect, this will keep
         // them roughly in order, which should be a better model of browser
         // behaviour.
 
         Collection<URLString> col = new LinkedHashSet<URLString>();
         return getEmbeddedResourceURLs(userAgent, html, baseUrl, new URLCollection(col),encoding);
 
         // An additional note on using HashSets to store URLs: I just
         // discovered that obtaining the hashCode of a java.net.URL implies
         // a domain-name resolution process. This means significant delays
         // can occur, even more so if the domain name is not resolvable.
         // Whether this can be a problem in practical situations I can't tell,
         // but
         // thought I'd keep a note just in case...
         // BTW, note that using a List and removing duplicates via scan
         // would not help, since URL.equals requires name resolution too.
         // The above problem has now been addressed with the URLString and
         // URLCollection classes.
 
     }
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      * <p>
      * All URLs should be added to the Collection.
      * <p>
      * Malformed URLs can be reported to the caller by having the Iterator
      * return the corresponding RL String. Overall problems parsing the html
      * should be reported by throwing an HTMLParseException.
      *
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
      * @param userAgent
      *            User Agent
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            URLCollection
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      */
     public abstract Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, URLCollection coll, String encoding)
             throws HTMLParseException;
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      *
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
      * @param userAgent
      *            User Agent
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            Collection - will contain URLString objects, not URLs
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      */
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, Collection<URLString> coll, String encoding) throws HTMLParseException {
         return getEmbeddedResourceURLs(userAgent, html, baseUrl, new URLCollection(coll), encoding);
     }
 
     /**
      * Parsers should over-ride this method if the parser class is re-usable, in
      * which case the class will be cached for the next getParser() call.
      *
      * @return true if the Parser is reusable
      */
     protected boolean isReusable() {
         return false;
     }
     
     /**
      * 
      * @param ieVersion Float IE version
-     * @return true if IE version < IE v10
+     * @return true if IE version &lt; IE v10
      */
     protected final boolean isEnableConditionalComments(Float ieVersion) {
         if(ieVersion == null) {
             return false;
         }
         // Conditionnal comment have been dropped in IE10
         // http://msdn.microsoft.com/en-us/library/ie/hh801214%28v=vs.85%29.aspx
         return ieVersion.floatValue() < IE_10;
     }
     
     /**
      * 
      * @param userAgent User Agent
      * @return version null if not IE or the version after MSIE
      */
     protected Float extractIEVersion(String userAgent) {
         if(StringUtils.isEmpty(userAgent)) {
             log.info("userAgent is null");
             return null;
         }
         Matcher matcher = IE_UA_PATTERN.matcher(userAgent);
         String ieVersion = null;
         while (matcher.find()) {
             if (matcher.groupCount() > 0) {
                 ieVersion = matcher.group(1);
             } else {
                 ieVersion = matcher.group();
             }
             break;
         }
         if(ieVersion != null) {
             return Float.valueOf(ieVersion);
         } else {
             return null;
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampleResult.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampleResult.java
index 054aea24c..f21a6b06e 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampleResult.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampleResult.java
@@ -1,257 +1,257 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.nio.charset.Charset;
 
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleResult;
 
 /**
  * This is a specialisation of the SampleResult class for the HTTP protocol.
  *
  */
 public class HTTPSampleResult extends SampleResult {
 
     private static final long serialVersionUID = 240L;
 
     private String cookies = ""; // never null
 
     private String method;
 
     /**
      * The raw value of the Location: header; may be null.
      * This is supposed to be an absolute URL:
      * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.30">RFC2616 sec14.30</a>
      * but is often relative.
      */
     private String redirectLocation;
 
     private String queryString = ""; // never null
 
     private static final String HTTP_NO_CONTENT_CODE = Integer.toString(HttpURLConnection.HTTP_NO_CONTENT);
     private static final String HTTP_NO_CONTENT_MSG = "No Content"; // $NON-NLS-1$
 
     public HTTPSampleResult() {
         super();
     }
 
     public HTTPSampleResult(long elapsed) {
         super(elapsed, true);
     }
 
     /**
      * Construct a 'parent' result for an already-existing result, essentially
      * cloning it
      *
      * @param res
      *            existing sample result
      */
     public HTTPSampleResult(HTTPSampleResult res) {
         super(res);
         method=res.method;
         cookies=res.cookies;
         queryString=res.queryString;
         redirectLocation=res.redirectLocation;
     }
 
     public void setHTTPMethod(String method) {
         this.method = method;
     }
 
     public String getHTTPMethod() {
         return method;
     }
 
     public void setRedirectLocation(String redirectLocation) {
         this.redirectLocation = redirectLocation;
     }
 
     public String getRedirectLocation() {
         return redirectLocation;
     }
 
     /**
      * Determine whether this result is a redirect.
-     * Returs true for: 301,302,303, & 307(GET or HEAD)
+     * Returns true for: 301,302,303 and 307(GET or HEAD)
      * @return true iff res is an HTTP redirect response
      */
     public boolean isRedirect() {
         /*
          * Don't redirect the following:
          * 300 = Multiple choice
          * 304 = Not Modified
          * 305 = Use Proxy
          * 306 = (Unused)
          */
         final String[] REDIRECT_CODES = { "301", "302", "303" };
         String code = getResponseCode();
         for (int i = 0; i < REDIRECT_CODES.length; i++) {
             if (REDIRECT_CODES[i].equals(code)) {
                 return true;
             }
         }
         // http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
         // If the 307 status code is received in response to a request other than GET or HEAD, 
         // the user agent MUST NOT automatically redirect the request unless it can be confirmed by the user,
         // since this might change the conditions under which the request was issued.
         // See Bug 54119
         if ("307".equals(code) && 
                 (HTTPConstants.GET.equals(getHTTPMethod()) || HTTPConstants.HEAD.equals(getHTTPMethod()))) {
             return true;
         }
         return false;
     }
 
     /**
      * Overrides version in Sampler data to provide more details
      * <p>
      * {@inheritDoc}
      */
     @Override
     public String getSamplerData() {
         StringBuilder sb = new StringBuilder();
         sb.append(method);
         URL u = super.getURL();
         if (u != null) {
             sb.append(' ');
             sb.append(u.toString());
             sb.append("\n");
             // Include request body if it is a post or put or patch
             if (HTTPConstants.POST.equals(method) || HTTPConstants.PUT.equals(method) 
                     || HTTPConstants.PATCH.equals(method)
                     || HttpWebdav.isWebdavMethod(method)
                     || HTTPConstants.DELETE.equals(method)) {
                 sb.append("\n"+method+" data:\n");
                 sb.append(queryString);
                 sb.append("\n");
             }
             if (cookies.length()>0){
                 sb.append("\nCookie Data:\n");
                 sb.append(cookies);
             } else {
                 sb.append("\n[no cookies]");
             }
             sb.append("\n");
         }
         final String sampData = super.getSamplerData();
         if (sampData != null){
             sb.append(sampData);
         }
         return sb.toString();
     }
 
     /**
      * @return cookies as a string
      */
     public String getCookies() {
         return cookies;
     }
 
     /**
      * @param string
      *            representing the cookies
      */
     public void setCookies(String string) {
         if (string == null) {
             cookies="";// $NON-NLS-1$
         } else {
             cookies = string;
         }
     }
 
     /**
      * Fetch the query string
      *
      * @return the query string
      */
     public String getQueryString() {
         return queryString;
     }
 
     /**
      * Save the query string
      *
      * @param string
      *            the query string
      */
     public void setQueryString(String string) {
         if (string == null ) {
             queryString="";// $NON-NLS-1$
         } else {
             queryString = string;
         }
     }
 
     /**
      * Overrides the method from SampleResult - so the encoding can be extracted from
      * the Meta content-type if necessary.
      *
      * Updates the dataEncoding field if the content-type is found.
      * @param defaultEncoding Default encoding used if there is no data encoding
      * @return the dataEncoding value as a String
      */
     @Override
     public String getDataEncodingWithDefault(String defaultEncoding) {
         String dataEncodingNoDefault = getDataEncodingNoDefault();
         if(dataEncodingNoDefault != null && dataEncodingNoDefault.length()> 0) {
             return dataEncodingNoDefault;
         }
         return defaultEncoding;
     }
     
     /**
      * Overrides the method from SampleResult - so the encoding can be extracted from
      * the Meta content-type if necessary.
      *
      * Updates the dataEncoding field if the content-type is found.
      *
      * @return the dataEncoding value as a String
      */
     @Override
     public String getDataEncodingNoDefault() {
         if (super.getDataEncodingNoDefault() == null && getContentType().startsWith("text/html")){ // $NON-NLS-1$
             byte[] bytes=getResponseData();
             // get the start of the file
             String prefix = new String(bytes, 0, Math.min(bytes.length, 2000), Charset.forName(DEFAULT_HTTP_ENCODING));
             // Preserve original case
             String matchAgainst = prefix.toLowerCase(java.util.Locale.ENGLISH);
             // Extract the content-type if present
             final String METATAG = "<meta http-equiv=\"content-type\" content=\""; // $NON-NLS-1$
             int tagstart=matchAgainst.indexOf(METATAG);
             if (tagstart!=-1){
                 tagstart += METATAG.length();
                 int tagend = prefix.indexOf('\"', tagstart); // $NON-NLS-1$
                 if (tagend!=-1){
                     final String ct = prefix.substring(tagstart,tagend);
                     setEncodingAndType(ct);// Update the dataEncoding
                 }
             }
         }
         return super.getDataEncodingNoDefault();
     }
 
     public void setResponseNoContent(){
         setResponseCode(HTTP_NO_CONTENT_CODE);
         setResponseMessage(HTTP_NO_CONTENT_MSG);
     }
     
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
index 5a6b13e1b..4447b5d95 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
@@ -1,571 +1,571 @@
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
 
 package org.apache.jmeter.protocol.http.util.accesslog;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.StringTokenizer;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 // For JUnit tests, @see TestTCLogParser
 
 /**
  * Description:<br>
  * <br>
  * Currently the parser only handles GET/POST requests. It's easy enough to add
  * support for other request methods by changing checkMethod. The is a complete
  * rewrite of a tool I wrote for myself earlier. The older algorithm was basic
  * and did not provide the same level of flexibility I want, so I wrote a new
  * one using a totally new algorithm. This implementation reads one line at a
  * time using BufferedReader. When it gets to the end of the file and the
  * sampler needs to get more requests, the parser will re-initialize the
  * BufferedReader. The implementation uses StringTokenizer to create tokens.
  * <p>
  * The parse algorithm is the following:
  * <p>
  * <ol>
  * <li> cleans the entry by looking for backslash "\"
  * <li> looks to see if GET or POST is in the line
  * <li> tokenizes using quotes "
  * <li> finds the token with the request method
  * <li> gets the string of the token and tokenizes it using space
  * <li> finds the first token beginning with slash character
  * <li> tokenizes the string using question mark "?"
  * <li> get the path from the first token
  * <li> returns the second token and checks it for parameters
- * <li> tokenizes the string using ampersand "&"
+ * <li> tokenizes the string using ampersand "&amp;"
  * <li> parses each token to name/value pairs
  * </ol>
  * <p>
  * Extending this class is fairly simple. Most access logs use the same format
  * starting from the request method. Therefore, changing the implementation of
  * cleanURL(string) method should be sufficient to support new log formats.
  * Tomcat uses common log format, so any webserver that uses the format should
  * work with this parser. Servers that are known to use non standard formats are
  * IIS and Netscape.
  * <p>
  *
  */
 
 public class TCLogParser implements LogParser {
     protected static final Logger log = LoggingManager.getLoggerForClass();
 
     /*
      * TODO should these fields be public?
      * They don't appear to be used externally.
      * 
      * Also, are they any different from HTTPConstants.GET etc. ?
      * In some cases they seem to be used as the method name from the Tomcat log.
      * However the RMETHOD field is used as the value for HTTPSamplerBase.METHOD,
      * for which HTTPConstants is most approriate.
      */
     public static final String GET = "GET";
 
     public static final String POST = "POST";
 
     public static final String HEAD = "HEAD";
 
     /** protected members * */
     protected String RMETHOD = null;
 
     /**
      * The path to the access log file
      */
     protected String URL_PATH = null;
 
     protected boolean useFILE = true;
 
     protected File SOURCE = null;
 
     protected String FILENAME = null;
 
     protected BufferedReader READER = null;
 
     /**
      * Handles to supporting classes
      */
     protected Filter FILTER = null;
 
     /**
      * by default, we probably should decode the parameter values
      */
     protected boolean decode = true;
 
     // TODO downcase UPPER case non-final variables
 
     /**
      *
      */
     public TCLogParser() {
         super();
     }
 
     /**
      * @param source
      */
     public TCLogParser(String source) {
         setSourceFile(source);
     }
 
     /**
      * by default decode is set to true. if the parameters shouldn't be
      * decoded, call the method with false
      * @param decodeparams
      */
     public void setDecodeParameterValues(boolean decodeparams) {
         this.decode = decodeparams;
     }
 
     /**
      * decode the parameter values is to true by default
      * @return  if paramter values should be decoded
      */
     public boolean decodeParameterValue() {
         return this.decode;
     }
 
     /**
      * Calls this method to set whether or not to use the path in the log. We
      * may want to provide the ability to filter the log file later on. By
      * default, the parser uses the file in the log.
      *
      * @param file
      */
     public void setUseParsedFile(boolean file) {
         this.useFILE = file;
     }
 
     /**
      * Use the filter to include/exclude files in the access logs. This is
      * provided as a convienance and reduce the need to spend hours cleaning up
      * log files.
      *
      * @param filter
      */
     @Override
     public void setFilter(Filter filter) {
         FILTER = filter;
     }
 
     /**
      * Sets the source file.
      *
      * @param source
      */
     @Override
     public void setSourceFile(String source) {
         this.FILENAME = source;
     }
 
     /**
      * parse the entire file.
      *
      * @return boolean success/failure
      */
     public int parse(TestElement el, int parseCount) {
         if (this.SOURCE == null) {
             this.SOURCE = new File(this.FILENAME);
         }
         try {
             if (this.READER == null) {
                 this.READER = getReader(this.SOURCE);
             }
             return parse(this.READER, el, parseCount);
         } catch (Exception exception) {
             log.error("Problem creating samples", exception);
         }
         return -1;// indicate that an error occured
     }
 
     private static BufferedReader getReader(File file) throws IOException {
         if (! isGZIP(file)) {
             return new BufferedReader(new FileReader(file));
         }
         GZIPInputStream in = new GZIPInputStream(new FileInputStream(file));
         return new BufferedReader(new InputStreamReader(in));
     }
 
     private static boolean isGZIP(File file) throws IOException {
         FileInputStream in = new FileInputStream(file);
         try {
             return in.read() == (GZIPInputStream.GZIP_MAGIC & 0xFF)
                 && in.read() == (GZIPInputStream.GZIP_MAGIC >> 8);
         } finally {
             in.close();
         }
     }
 
     /**
      * parse a set number of lines from the access log. Keep in mind the number
      * of lines parsed will depend the filter and number of lines in the log.
      * The method returns the actual number of lines parsed.
      *
      * @param count
      * @return lines parsed
      */
     @Override
     public int parseAndConfigure(int count, TestElement el) {
         return this.parse(el, count);
     }
 
     /**
      * The method is responsible for reading each line, and breaking out of the
      * while loop if a set number of lines is given.
      *
      * @param breader
      */
     protected int parse(BufferedReader breader, TestElement el, int parseCount) {
         int actualCount = 0;
         String line = null;
         try {
             // read one line at a time using
             // BufferedReader
             line = breader.readLine();
             while (line != null) {
                 if (line.length() > 0) {
                     actualCount += this.parseLine(line, el);
                 }
                 // we check the count to see if we have exceeded
                 // the number of lines to parse. There's no way
                 // to know where to stop in the file. Therefore
                 // we use break to escape the while loop when
                 // we've reached the count.
                 if (parseCount != -1 && actualCount >= parseCount) {
                     break;
                 }
                 line = breader.readLine();
             }
             if (line == null) {
                 breader.close();
                 this.READER = null;
                 // this.READER = new BufferedReader(new
                 // FileReader(this.SOURCE));
                 // parse(this.READER,el);
             }
         } catch (IOException ioe) {
             log.error("Error reading log file", ioe);
         }
         return actualCount;
     }
 
     /**
      * parseLine calls the other parse methods to parse the given text.
      *
      * @param line
      */
     protected int parseLine(String line, TestElement el) {
         int count = 0;
         // we clean the line to get
         // rid of extra stuff
         String cleanedLine = this.cleanURL(line);
         log.debug("parsing line: " + line);
         // now we set request method
         el.setProperty(HTTPSamplerBase.METHOD, RMETHOD);
         if (FILTER != null) {
             log.debug("filter is not null");
             if (!FILTER.isFiltered(line,el)) {
                 log.debug("line was not filtered");
                 // increment the current count
                 count++;
                 // we filter the line first, before we try
                 // to separate the URL into file and
                 // parameters.
                 line = FILTER.filter(cleanedLine);
                 if (line != null) {
                     createUrl(line, el);
                 }
             } else {
                 log.debug("Line was filtered");
             }
         } else {
             log.debug("filter was null");
             // increment the current count
             count++;
             // in the case when the filter is not set, we
             // parse all the lines
             createUrl(cleanedLine, el);
         }
         return count;
     }
 
     /**
      * @param line
      */
     private void createUrl(String line, TestElement el) {
         String paramString = null;
         // check the URL for "?" symbol
         paramString = this.stripFile(line, el);
         if (paramString != null) {
             this.checkParamFormat(line);
             // now that we have stripped the file, we can parse the parameters
             this.convertStringToJMRequest(paramString, el);
         }
     }
 
     /**
      * The method cleans the URL using the following algorithm.
      * <ol>
      * <li> check for double quotes
      * <li> check the request method
      * <li> tokenize using double quotes
      * <li> find first token containing request method
      * <li> tokenize string using space
      * <li> find first token that begins with "/"
      * </ol>
      * Example Tomcat log entry:
      * <p>
      * 127.0.0.1 - - [08/Jan/2003:07:03:54 -0500] "GET /addrbook/ HTTP/1.1" 200
      * 1981
      * <p>
      *
      * @param entry
      * @return cleaned url
      */
     public String cleanURL(String entry) {
         String url = entry;
         // if the string contains atleast one double
         // quote and checkMethod is true, go ahead
         // and tokenize the string.
         if (entry.indexOf('"') > -1 && checkMethod(entry)) {
             StringTokenizer tokens = null;
             // we tokenize using double quotes. this means
             // for tomcat we should have 3 tokens if there
             // isn't any additional information in the logs
             tokens = this.tokenize(entry, "\"");
             while (tokens.hasMoreTokens()) {
                 String toke = tokens.nextToken();
                 // if checkMethod on the token is true
                 // we tokenzie it using space and escape
                 // the while loop. Only the first matching
                 // token will be used
                 if (checkMethod(toke)) {
                     StringTokenizer token2 = this.tokenize(toke, " ");
                     while (token2.hasMoreTokens()) {
                         String t = (String) token2.nextElement();
                         if (t.equalsIgnoreCase(GET)) {
                             RMETHOD = GET;
                         } else if (t.equalsIgnoreCase(POST)) {
                             RMETHOD = POST;
                         } else if (t.equalsIgnoreCase(HEAD)) {
                             RMETHOD = HEAD;
                         }
                         // there should only be one token
                         // that starts with slash character
                         if (t.startsWith("/")) {
                             url = t;
                             break;
                         }
                     }
                     break;
                 }
             }
             return url;
         }
         // we return the original string
         return url;
     }
 
     /**
      * The method checks for POST and GET methods currently. The other methods
      * aren't supported yet.
      *
      * @param text
      * @return if method is supported
      */
     public boolean checkMethod(String text) {
         if (text.indexOf("GET") > -1) {
             this.RMETHOD = GET;
             return true;
         } else if (text.indexOf("POST") > -1) {
             this.RMETHOD = POST;
             return true;
         } else if (text.indexOf("HEAD") > -1) {
             this.RMETHOD = HEAD;
             return true;
         } else {
             return false;
         }
     }
 
     /**
      * Tokenize the URL into two tokens. If the URL has more than one "?", the
      * parse may fail. Only the first two tokens are used. The first token is
      * automatically parsed and set at URL_PATH.
      *
      * @param url
      * @return String parameters
      */
     public String stripFile(String url, TestElement el) {
         if (url.indexOf('?') > -1) {
             StringTokenizer tokens = this.tokenize(url, "?");
             this.URL_PATH = tokens.nextToken();
             el.setProperty(HTTPSamplerBase.PATH, URL_PATH);
             return tokens.hasMoreTokens() ? tokens.nextToken() : null;
         }
         el.setProperty(HTTPSamplerBase.PATH, url);
         return null;
     }
 
     /**
      * Checks the string to make sure it has /path/file?name=value format. If
      * the string doesn't have "?", it will return false.
      *
      * @param url
      * @return boolean
      */
     public boolean checkURL(String url) {
         if (url.indexOf('?') > -1) {
             return true;
         }
         return false;
     }
 
     /**
-     * Checks the string to see if it contains "&" and "=". If it does, return
+     * Checks the string to see if it contains "&amp;" and "=". If it does, return
      * true, so that it can be parsed.
      *
      * @param text
      * @return boolean
      */
     public boolean checkParamFormat(String text) {
         if (text.indexOf('&') > -1 && text.indexOf('=') > -1) {
             return true;
         }
         return false;
     }
 
     /**
      * Convert a single line into XML
      *
      * @param text
      */
     public void convertStringToJMRequest(String text, TestElement el) {
         ((HTTPSamplerBase) el).parseArguments(text);
     }
 
     /**
      * Parse the string parameters into NVPair[] array. Once they are parsed, it
      * is returned. The method uses parseOneParameter(string) to convert each
      * pair.
      *
      * @param stringparams
      */
     public NVPair[] convertStringtoNVPair(String stringparams) {
         List<String> vparams = this.parseParameters(stringparams);
         NVPair[] nvparams = new NVPair[vparams.size()];
         // convert the Parameters
         for (int idx = 0; idx < nvparams.length; idx++) {
             nvparams[idx] = this.parseOneParameter(vparams.get(idx));
         }
         return nvparams;
     }
 
     /**
      * Method expects name and value to be separated by an equal sign "=". The
      * method uses StringTokenizer to make a NVPair object. If there happens to
      * be more than one "=" sign, the others are ignored. The chance of a string
      * containing more than one is unlikely and would not conform to HTTP spec.
      * I should double check the protocol spec to make sure this is accurate.
      *
      * @param parameter
      *            to be parsed
      * @return NVPair
      */
     protected NVPair parseOneParameter(String parameter) {
         String name = ""; // avoid possible NPE when trimming the name
         String value = null;
         try {
             StringTokenizer param = this.tokenize(parameter, "=");
             name = param.nextToken();
             value = param.nextToken();
         } catch (Exception e) {
             // do nothing. it's naive, but since
             // the utility is meant to parse access
             // logs the formatting should be correct
         }
         if (value == null) {
             value = "";
         } else {
             if (decode) {
                 try {
                     value = URLDecoder.decode(value,"UTF-8");
                 } catch (UnsupportedEncodingException e) {
                     log.warn(e.getMessage());
                 }
             }
         }
         return new NVPair(name.trim(), value.trim());
     }
 
     /**
      * Method uses StringTokenizer to convert the string into single pairs. The
      * string should conform to HTTP protocol spec, which means the name/value
-     * pairs are separated by the ampersand symbol "&". Some one could write the
+     * pairs are separated by the ampersand symbol "&amp;". Someone could write the
      * querystrings by hand, but that would be round about and go against the
      * purpose of this utility.
      *
      * @param parameters
      * @return List<String>
      */
     protected List<String> parseParameters(String parameters) {
         List<String> parsedParams = new ArrayList<String>();
         StringTokenizer paramtokens = this.tokenize(parameters, "&");
         while (paramtokens.hasMoreElements()) {
             parsedParams.add(paramtokens.nextToken());
         }
         return parsedParams;
     }
 
     /**
      * Parses the line using java.util.StringTokenizer.
      *
      * @param line
      *            line to be parsed
      * @param delim
      *            delimiter
      * @return StringTokenizer
      */
     public StringTokenizer tokenize(String line, String delim) {
         return new StringTokenizer(line, delim);
     }
 
     @Override
     public void close() {
         try {
             this.READER.close();
             this.READER = null;
             this.SOURCE = null;
         } catch (IOException e) {
             // do nothing
         }
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/Utils.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/Utils.java
index c5813205b..2b25eeb5f 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/Utils.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/Utils.java
@@ -1,241 +1,241 @@
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
 
 package org.apache.jmeter.protocol.jms;
 
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Map;
 
 import javax.jms.Connection;
 import javax.jms.Destination;
 import javax.jms.JMSException;
 import javax.jms.Message;
 import javax.jms.MessageConsumer;
 import javax.jms.MessageProducer;
 import javax.jms.Session;
 import javax.naming.Context;
 import javax.naming.NamingException;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.jms.sampler.JMSProperties;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Utility methods for JMS protocol.
  * WARNING - the API for this class is likely to change!
  */
 public final class Utils {
     // By default priority is 4
     // http://docs.oracle.com/javaee/6/tutorial/doc/bncfu.html
     public static final String DEFAULT_PRIORITY_4 = "4"; // $NON-NLS-1$
 
     // By default a message never expires
     // http://docs.oracle.com/javaee/6/tutorial/doc/bncfu.html
     public static final String DEFAULT_NO_EXPIRY = "0"; // $NON-NLS-1$
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static void close(MessageConsumer closeable, Logger log){
         if (closeable != null){
             try {
                 closeable.close();
             } catch (JMSException e) {
                 log.error("Error during close: ", e);
             }
         }
     }
 
     public static void close(Session closeable, Logger log) {
         if (closeable != null){
             try {
                 closeable.close();
             } catch (JMSException e) {
                 log.error("Error during close: ", e);
             }
         }
     }
 
     public static void close(Connection closeable, Logger log) {
         if (closeable != null){
             try {
                 closeable.close();
             } catch (JMSException e) {
                 log.error("Error during close: ", e);
             }
         }
     }
 
     public static void close(MessageProducer closeable, Logger log) {
         if (closeable != null){
             try {
                 closeable.close();
             } catch (JMSException e) {
                 log.error("Error during close: ", e);
             }
         }
     }
 
     public static String messageProperties(Message msg){
         return messageProperties(new StringBuilder(), msg).toString();
     }
 
     public static StringBuilder messageProperties(StringBuilder sb, Message msg){
         requestHeaders(sb, msg);
         sb.append("Properties:\n");
         Enumeration<?> rme;
         try {
             rme = msg.getPropertyNames();
             while(rme.hasMoreElements()){
                 String name=(String) rme.nextElement();
                 sb.append(name).append('\t');
                 String value=msg.getStringProperty(name);
                 sb.append(value).append('\n');
             }
         } catch (JMSException e) {
             sb.append("\nError: "+e.toString());
         }
         return sb;
     }
     
     public static StringBuilder requestHeaders(StringBuilder sb, Message msg){
         try {
             sb.append("JMSCorrelationId ").append(msg.getJMSCorrelationID()).append('\n');
             sb.append("JMSMessageId     ").append(msg.getJMSMessageID()).append('\n');
             sb.append("JMSTimestamp     ").append(msg.getJMSTimestamp()).append('\n');
             sb.append("JMSType          ").append(msg.getJMSType()).append('\n');
             sb.append("JMSExpiration    ").append(msg.getJMSExpiration()).append('\n');
             sb.append("JMSPriority      ").append(msg.getJMSPriority()).append('\n');
             sb.append("JMSDestination   ").append(msg.getJMSDestination()).append('\n');
         } catch (JMSException e) {
             sb.append("\nError: "+e.toString());
         }
         return sb;
     }
 
     /**
      * Method will lookup a given destination (topic/queue) using JNDI.
      *
      * @param context
      * @param name the destination name
      * @return the destination, never null
      * @throws NamingException if the name cannot be found as a Destination
      */
     public static Destination lookupDestination(Context context, String name) throws NamingException {
         Object o = context.lookup(name);
         if (o instanceof Destination){
             return (Destination) o;
         }
         throw new NamingException("Found: "+name+"; expected Destination, but was: "+(o!=null ? o.getClass().getName() : "null"));
     }
 
     /**
      * Get value from Context environment taking into account non fully compliant
      * JNDI implementations
      * @param context
      * @param key
      * @return String or null if context.getEnvironment() is not compliant
      * @throws NamingException 
      */
     public static final String getFromEnvironment(Context context, String key) throws NamingException {
         try {
             Hashtable<?,?> env = context.getEnvironment();
             if(env != null) {
                 return (String) env.get(key);
             } else {
                 log.warn("context.getEnvironment() returned null (should not happen according to javadoc but non compliant implementation can return this)");
                 return null;
             }
         } catch (javax.naming.OperationNotSupportedException ex) {
             // Some JNDI implementation can return this
             log.warn("context.getEnvironment() not supported by implementation ");
             return null;
         }        
     }
     /**
      * Obtain the queue connection from the context and factory name.
      * 
      * @param ctx
      * @param factoryName
      * @return the queue connection
      * @throws JMSException
      * @throws NamingException
      */
     public static Connection getConnection(Context ctx, String factoryName) throws JMSException, NamingException {
         Object objfac = null;
         try {
             objfac = ctx.lookup(factoryName);
         } catch (NoClassDefFoundError e) {
             throw new NamingException("Lookup failed: "+e.toString());
         }
         if (objfac instanceof javax.jms.ConnectionFactory) {
             String username = getFromEnvironment(ctx, Context.SECURITY_PRINCIPAL);
             if(username != null) {
                 String password = getFromEnvironment(ctx, Context.SECURITY_CREDENTIALS);
                 return ((javax.jms.ConnectionFactory) objfac).createConnection(username, password);                
             }
             else {
                 return ((javax.jms.ConnectionFactory) objfac).createConnection();
             }
         }
         throw new NamingException("Expected javax.jms.ConnectionFactory, found "+(objfac != null ? objfac.getClass().getName(): "null"));
     }
     
     /**
      * Set JMS Properties to msg
      * @param msg Message
      * @param map Map<String, String>
      * @throws JMSException
      */
     public static void addJMSProperties(Message msg, Map<String, Object> map) throws JMSException {
         if(map == null) {
             return;
         }
         for (Map.Entry<String, Object> me : map.entrySet()) {
             String name = me.getKey();
             Object value = me.getValue();
             if (log.isDebugEnabled()) {
                 log.debug("Adding property [" + name + "=" + value + "]");
             }
 
             // WebsphereMQ does not allow corr. id. to be set using setStringProperty()
             if("JMSCorrelationID".equalsIgnoreCase(name)) { // $NON-NLS-1$
                 msg.setJMSCorrelationID((String)value);
             } else {
                 msg.setObjectProperty(name, value);
             }
         }
     }
 
 
     /**
      * Converts {@link Arguments} to {@link JMSProperties} defaulting to String type
-     * Used to convert version <= 2.10 test plans
+     * Used to convert version &lt;= 2.10 test plans
      * @param args {@link Arguments}
      * @return jmsProperties {@link JMSProperties}
      */
     public static final JMSProperties convertArgumentsToJmsProperties(Arguments args) {
         JMSProperties jmsProperties = new JMSProperties();
         Map<String,String>  map = args.getArgumentsAsMap();
         for (Map.Entry<String, String> entry : map.entrySet()) {
             jmsProperties.addJmsProperty(entry.getKey(), entry.getValue());
         }
         return jmsProperties;
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ReceiveSubscriber.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ReceiveSubscriber.java
index c78acc59a..957711bb6 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ReceiveSubscriber.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ReceiveSubscriber.java
@@ -1,314 +1,314 @@
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
 
 package org.apache.jmeter.protocol.jms.client;
 
 import java.io.Closeable;
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.TimeUnit;
 
 import javax.jms.Connection;
 import javax.jms.Destination;
 import javax.jms.JMSException;
 import javax.jms.Message;
 import javax.jms.MessageConsumer;
 import javax.jms.MessageListener;
 import javax.jms.Session;
 import javax.jms.Topic;
 import javax.naming.Context;
 import javax.naming.NamingException;
 
 import org.apache.jmeter.protocol.jms.Utils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Generic MessageConsumer class, which has two possible strategies.
  * <ul>
  * <li>Use MessageConsumer.receive(timeout) to fetch messages.</li>
  * <li>Use MessageListener.onMessage() to cache messages in a local queue.</li>
  * </ul>
  * In both cases, the {@link #getMessage(long)} method is used to return the next message,
  * either directly using receive(timeout) or from the queue using poll(timeout).
  */
 public class ReceiveSubscriber implements Closeable, MessageListener {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final Connection connection;
 
     private final Session session;
 
     private final MessageConsumer subscriber;
 
     /*
      * We use a LinkedBlockingQueue (rather than a ConcurrentLinkedQueue) because it has a
      * poll-with-wait method that avoids the need to use a polling loop.
      */
     private final LinkedBlockingQueue<Message> queue;
 
     /**
      * No need for volatile as this variable is only accessed by a single thread
      */
     private boolean connectionStarted;
 
     /**
      * Constructor takes the necessary JNDI related parameters to create a
      * connection and prepare to begin receiving messages.
      * <br/>
      * The caller must then invoke {@link #start()} to enable message reception.
      * 
      * @param useProps if true, use jndi.properties instead of 
      * initialContextFactory, providerUrl, securityPrincipal, securityCredentials
      * @param initialContextFactory
      * @param providerUrl
      * @param connfactory
      * @param destinationName
      * @param durableSubscriptionId
      * @param clientId
      * @param jmsSelector Message Selector
      * @param useAuth
      * @param securityPrincipal
      * @param securityCredentials
      * @throws JMSException if could not create context or other problem occurred.
      * @throws NamingException 
      */
     public ReceiveSubscriber(boolean useProps, 
             String initialContextFactory, String providerUrl, String connfactory, String destinationName,
             String durableSubscriptionId, String clientId, String jmsSelector, boolean useAuth, 
             String securityPrincipal, String securityCredentials) throws NamingException, JMSException {
         this(0, useProps, 
                 initialContextFactory, providerUrl, connfactory, destinationName,
                 durableSubscriptionId, clientId, jmsSelector, useAuth, 
                 securityPrincipal, securityCredentials, false);
     }
 
     /**
      * Constructor takes the necessary JNDI related parameters to create a
      * connection and create an onMessageListener to prepare to begin receiving messages.
      * <br/>
      * The caller must then invoke {@link #start()} to enable message reception.
      * 
-     * @param queueSize maximum queue size <=0 == no limit
+     * @param queueSize maximum queue size, where a queueSize &lt;=0 means no limit
      * @param useProps if true, use jndi.properties instead of 
      * initialContextFactory, providerUrl, securityPrincipal, securityCredentials
      * @param initialContextFactory
      * @param providerUrl
      * @param connfactory
      * @param destinationName
      * @param durableSubscriptionId
      * @param clientId
      * @param jmsSelector Message Selector
      * @param useAuth
      * @param securityPrincipal
      * @param securityCredentials
      * @throws JMSException if could not create context or other problem occurred.
      * @throws NamingException 
      */
     public ReceiveSubscriber(int queueSize, boolean useProps, 
             String initialContextFactory, String providerUrl, String connfactory, String destinationName,
             String durableSubscriptionId, String clientId, String jmsSelector, boolean useAuth, 
             String securityPrincipal, String securityCredentials) throws NamingException, JMSException {
         this(queueSize,  useProps, 
              initialContextFactory, providerUrl, connfactory, destinationName,
              durableSubscriptionId, clientId, jmsSelector, useAuth, 
              securityPrincipal,  securityCredentials, true);
     }
     
     
     /**
      * Constructor takes the necessary JNDI related parameters to create a
      * connection and create an onMessageListener to prepare to begin receiving messages.
      * <br/>
      * The caller must then invoke {@link #start()} to enable message reception.
      * 
-     * @param queueSize maximum queue size <=0 == no limit
+     * @param queueSize maximum queue, where a queueSize &lt;=0 means no limit
      * @param useProps if true, use jndi.properties instead of 
      * initialContextFactory, providerUrl, securityPrincipal, securityCredentials
      * @param initialContextFactory
      * @param providerUrl
      * @param connfactory
      * @param destinationName
      * @param durableSubscriptionId
      * @param clientId
      * @param jmsSelector Message Selector
      * @param useAuth
      * @param securityPrincipal
      * @param securityCredentials
      * @param useMessageListener if true create an onMessageListener to prepare to begin receiving messages, otherwise queue will be null
      * @throws JMSException if could not create context or other problem occurred.
      * @throws NamingException 
      */
     private ReceiveSubscriber(int queueSize, boolean useProps, 
             String initialContextFactory, String providerUrl, String connfactory, String destinationName,
             String durableSubscriptionId, String clientId, String jmsSelector, boolean useAuth, 
             String securityPrincipal, String securityCredentials, boolean useMessageListener) throws NamingException, JMSException {
         boolean initSuccess = false;
         try{
             Context ctx = InitialContextFactory.getContext(useProps, 
                     initialContextFactory, providerUrl, useAuth, securityPrincipal, securityCredentials);
             connection = Utils.getConnection(ctx, connfactory);
             if(!isEmpty(clientId)) {
                 connection.setClientID(clientId);
             }
             session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             Destination dest = Utils.lookupDestination(ctx, destinationName);
             subscriber = createSubscriber(session, dest, durableSubscriptionId, jmsSelector);
             if(useMessageListener) {
                 if (queueSize <=0) {
                     queue = new LinkedBlockingQueue<Message>();
                 } else {
                     queue = new LinkedBlockingQueue<Message>(queueSize);            
                 }
                 subscriber.setMessageListener(this);
             } else {
                 queue = null;
             }
             log.debug("<init> complete");
             initSuccess = true;
         }
         finally {
             if(!initSuccess) {
                 close();
             }
         }
     }
     
     /**
      * Return a simple MessageConsumer or a TopicSubscriber (as a durable subscription)
      * @param session
      *                 JMS session
      * @param destination
      *                 JMS destination, can be either topic or queue
      * @param durableSubscriptionId 
      *                 If neither empty nor null, this means that a durable 
      *                 subscription will be used
      * @param jmsSelector JMS Selector
      * @return the message consumer
      * @throws JMSException
      */
     private MessageConsumer createSubscriber(Session session, 
             Destination destination, String durableSubscriptionId, 
             String jmsSelector) throws JMSException {
         if (isEmpty(durableSubscriptionId)) {
             if(isEmpty(jmsSelector)) {
                 return session.createConsumer(destination);
             } else {
                 return session.createConsumer(destination, jmsSelector);
             }
         } else {
             if(isEmpty(jmsSelector)) {
                 return session.createDurableSubscriber((Topic) destination, durableSubscriptionId); 
             } else {
                 return session.createDurableSubscriber((Topic) destination, durableSubscriptionId, jmsSelector, false);                 
             }
         }
     }
 
     /**
      * Calls Connection.start() to begin receiving inbound messages.
      * @throws JMSException 
      */
     public void start() throws JMSException {
         log.debug("start()");
         connection.start();
         connectionStarted=true;
     }
 
     /**
      * Calls Connection.stop() to stop receiving inbound messages.
      * @throws JMSException 
      */
     public void stop() throws JMSException {
         log.debug("stop()");
         connection.stop();
         connectionStarted=false;
     }
 
     /**
      * Get the next message or null.
      * Never blocks for longer than the specified timeout.
      * 
      * @param timeout in milliseconds
      * @return the next message or null
      * 
      * @throws JMSException
      */
     public Message getMessage(long timeout) throws JMSException {
         Message message = null;
         if (queue != null) { // Using onMessage Listener
             try {
                 if (timeout < 10) { // Allow for short/negative times
                     message = queue.poll();                    
                 } else {
                     message = queue.poll(timeout, TimeUnit.MILLISECONDS);
                 }
             } catch (InterruptedException e) {
                 // Ignored
             }
             return message;
         }
         if (timeout < 10) { // Allow for short/negative times
             message = subscriber.receiveNoWait();                
         } else {
             message = subscriber.receive(timeout);
         }
         return message;
     }
     /**
      * close() will stop the connection first. 
      * Then it closes the subscriber, session and connection.
      */
     @Override
     public void close() { // called by SubscriberSampler#threadFinished()
         log.debug("close()");
         try {
             if(connection != null && connectionStarted) {
                 connection.stop();
                 connectionStarted = false;
             }
         } catch (JMSException e) {
             log.warn("Stopping connection throws exception, message:"+e.getMessage());
         }
         Utils.close(subscriber, log);
         Utils.close(session, log);
         Utils.close(connection, log);
     }
 
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void onMessage(Message message) {
         if (!queue.offer(message)){
             log.warn("Could not add message to queue");
         }
     }
     
     
     /**
      * Checks whether string is empty
      * 
      * @param s1
      * @return True if input is null, an empty string, or a white space-only string
      */
     private boolean isEmpty(String s1) {
         return (s1 == null || s1.trim().equals(""));
     }
 }
