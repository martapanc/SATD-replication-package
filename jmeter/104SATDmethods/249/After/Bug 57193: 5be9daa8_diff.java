diff --git a/src/components/org/apache/jmeter/control/IncludeController.java b/src/components/org/apache/jmeter/control/IncludeController.java
index d86b85183..a52b92402 100644
--- a/src/components/org/apache/jmeter/control/IncludeController.java
+++ b/src/components/org/apache/jmeter/control/IncludeController.java
@@ -1,216 +1,218 @@
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Iterator;
 import java.util.LinkedList;
 
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public class IncludeController extends GenericController implements ReplaceableController {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final String INCLUDE_PATH = "IncludeController.includepath"; //$NON-NLS-1$
 
     private static  final String prefix =
         JMeterUtils.getPropDefault(
                 "includecontroller.prefix", //$NON-NLS-1$
                 ""); //$NON-NLS-1$
 
     private HashTree subtree = null;
     private TestElement sub = null;
 
     /**
      * No-arg constructor
      *
      * @see java.lang.Object#Object()
      */
     public IncludeController() {
         super();
     }
 
     @Override
     public Object clone() {
         // TODO - fix so that this is only called once per test, instead of at every clone
         // Perhaps save previous filename, and only load if it has changed?
         this.resolveReplacementSubTree(null);
         IncludeController clone = (IncludeController) super.clone();
         clone.setIncludePath(this.getIncludePath());
         if (this.subtree != null) {
             if (this.subtree.keySet().size() == 1) {
                 Iterator<Object> itr = this.subtree.keySet().iterator();
                 while (itr.hasNext()) {
                     this.sub = (TestElement) itr.next();
                 }
             }
             clone.subtree = (HashTree)this.subtree.clone();
             clone.sub = this.sub==null ? null : (TestElement) this.sub.clone();
         }
         return clone;
     }
 
     /**
      * In the event an user wants to include an external JMX test plan
      * the GUI would call this.
      * @param jmxfile The path to the JMX test plan to include
      */
     public void setIncludePath(String jmxfile) {
         this.setProperty(INCLUDE_PATH,jmxfile);
     }
 
     /**
      * return the JMX file path.
      * @return the JMX file path
      */
     public String getIncludePath() {
         return this.getPropertyAsString(INCLUDE_PATH);
     }
 
     /**
      * The way ReplaceableController works is clone is called first,
      * followed by replace(HashTree) and finally getReplacement().
      */
     @Override
     public HashTree getReplacementSubTree() {
         return subtree;
     }
 
     public TestElement getReplacementElement() {
         return sub;
     }
 
     @Override
     public void resolveReplacementSubTree(JMeterTreeNode context) {
         this.subtree = this.loadIncludedElements();
     }
 
     /**
      * load the included elements using SaveService
+     *
+     * @return tree with loaded elements
      */
     protected HashTree loadIncludedElements() {
         // only try to load the JMX test plan if there is one
         final String includePath = getIncludePath();
         InputStream reader = null;
         HashTree tree = null;
         if (includePath != null && includePath.length() > 0) {
             try {
                 String fileName=prefix+includePath;
                 File file = new File(fileName);
                 final String absolutePath = file.getAbsolutePath();
                 log.info("loadIncludedElements -- try to load included module: "+absolutePath);
                 if(!file.exists() && !file.isAbsolute()){
                     log.info("loadIncludedElements -failed for: "+absolutePath);
                     file = new File(FileServer.getFileServer().getBaseDir(), includePath);
                     log.info("loadIncludedElements -Attempting to read it from: "+absolutePath);
                     if(!file.exists()){
                         log.error("loadIncludedElements -failed for: "+absolutePath);
                         throw new IOException("loadIncludedElements -failed for: "+absolutePath);
                     }
                 }
                 
                 reader = new FileInputStream(file);
                 tree = SaveService.loadTree(reader);
                 // filter the tree for a TestFragment.
                 tree = getProperBranch(tree);
                 removeDisabledItems(tree);
                 return tree;
             } catch (NoClassDefFoundError ex) // Allow for missing optional jars
             {
                 String msg = ex.getMessage();
                 if (msg == null) {
                     msg = "Missing jar file - see log for details";
                 }
                 log.warn("Missing jar file", ex);
                 JMeterUtils.reportErrorToUser(msg);
             } catch (FileNotFoundException ex) {
                 String msg = ex.getMessage();
                 JMeterUtils.reportErrorToUser(msg);
                 log.warn(msg);
             } catch (Exception ex) {
                 String msg = ex.getMessage();
                 if (msg == null) {
                     msg = "Unexpected error - see log for details";
                 }
                 JMeterUtils.reportErrorToUser(msg);
                 log.warn("Unexpected error", ex);
             }
             finally{
                 JOrphanUtils.closeQuietly(reader);
             }
         }
         return tree;
     }
 
     /**
      * Extract from tree (included test plan) all Test Elements located in a Test Fragment
      * @param tree HashTree included Test Plan
      * @return HashTree Subset within Test Fragment or Empty HashTree
      */
     private HashTree getProperBranch(HashTree tree) {
         Iterator<Object> iter = new LinkedList<Object>(tree.list()).iterator();
         while (iter.hasNext()) {
             TestElement item = (TestElement) iter.next();
 
             //if we found a TestPlan, then we are on our way to the TestFragment
             if (item instanceof TestPlan)
             {
                 return getProperBranch(tree.getTree(item));
             }
 
             if (item instanceof TestFragmentController)
             {
                 return tree.getTree(item);
             }
         }
         log.warn("No Test Fragment was found in included Test Plan, returning empty HashTree");
         return new HashTree();
     }
 
 
     private void removeDisabledItems(HashTree tree) {
         Iterator<Object> iter = new LinkedList<Object>(tree.list()).iterator();
         while (iter.hasNext()) {
             TestElement item = (TestElement) iter.next();
             if (!item.isEnabled()) {
                 //log.info("Removing "+item.toString());
                 tree.remove(item);
             } else {
                 //log.info("Keeping "+item.toString());
                 removeDisabledItems(tree.getTree(item));// Recursive call
             }
         }
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/Spline3.java b/src/components/org/apache/jmeter/visualizers/Spline3.java
index fdd84b55a..ac394ffea 100644
--- a/src/components/org/apache/jmeter/visualizers/Spline3.java
+++ b/src/components/org/apache/jmeter/visualizers/Spline3.java
@@ -1,430 +1,439 @@
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
  * <PRE>
  *  // ...
  *  float[] nodes = {3F, 2F, 4F, 1F, 2.5F, 5F, 3F};
  *  Spline3 curve = new Spline3(nodes);
  *  // ...
  *  public void paint(Graphics g) {
  *    int[] plot = curve.getPlots();
  *    for (int i = 1; i &lt; n; i++) {
  *      g.drawLine(i - 1, plot[i - 1], i, plot[i]);
  *    }
  *  }
  *  // ...
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
+     *
+     * @return <code>true</code> if equation system converges
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
+     *
+     * @param oldX
+     *            old values
+     * @param newX
+     *            new values
+     * @return indicator of how different the old and new values are (always
+     *         zero or greater, the nearer to zero the more similar)
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
diff --git a/src/core/org/apache/jmeter/engine/util/FunctionParser.java b/src/core/org/apache/jmeter/engine/util/FunctionParser.java
index 5ffcb49ed..830a3bbaa 100644
--- a/src/core/org/apache/jmeter/engine/util/FunctionParser.java
+++ b/src/core/org/apache/jmeter/engine/util/FunctionParser.java
@@ -1,246 +1,249 @@
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
  * Created on Jul 25, 2003
  */
 package org.apache.jmeter.engine.util;
 
 import java.io.IOException;
 import java.io.StringReader;
 import java.util.LinkedList;
 
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.functions.Function;
 import org.apache.jmeter.functions.InvalidVariableException;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Parses function / variable references of the form
  * ${functionName[([var[,var...]])]}
  * and
  * ${variableName}
  */
 class FunctionParser {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * Compile a general string into a list of elements for a CompoundVariable.
      *
      * Calls {@link #makeFunction(StringReader)} if it detects an unescaped "${".
      *
      * Removes escapes from '$', ',' and '\'.
      * 
      * @param value string containing the function / variable references (if any)
      *
      * @return list of Strings or Objects representing functions
+     * @throws InvalidVariableException when evaluation of variables fail
      */
     LinkedList<Object> compileString(String value) throws InvalidVariableException {
         StringReader reader = new StringReader(value);
         LinkedList<Object> result = new LinkedList<Object>();
         StringBuilder buffer = new StringBuilder();
         char previous = ' '; // TODO - why use space?
         char[] current = new char[1];
         try {
             while (reader.read(current) == 1) {
                 if (current[0] == '\\') { // Handle escapes
                     previous = current[0];
                     if (reader.read(current) == 0) {
                         break;
                     }
                     // Keep the '\' unless it is one of the escapable chars '$' ',' or '\'
                     // N.B. This method is used to parse function parameters, so must treat ',' as special
                     if (current[0] != '$' && current[0] != ',' && current[0] != '\\') {
                         buffer.append(previous); // i.e. '\\'
                     }
                     previous = ' ';
                     buffer.append(current[0]);
                     continue;
                 } else if (current[0] == '{' && previous == '$') {// found "${"
                     buffer.deleteCharAt(buffer.length() - 1);
                     if (buffer.length() > 0) {// save leading text
                         result.add(buffer.toString());
                         buffer.setLength(0);
                     }
                     result.add(makeFunction(reader));
                     previous = ' ';
                 } else {
                     buffer.append(current[0]);
                     previous = current[0];
                 }
             }
             if (buffer.length() > 0) {
                 result.add(buffer.toString());
             }
         } catch (IOException e) {
             log.error("Error parsing function: " + value, e);
             result.clear();
             result.add(value);
         }
         if (result.size() == 0) {
             result.add("");
         }
         return result;
     }
 
     /**
      * Compile a string into a function or SimpleVariable.
      *
      * Called by {@link #compileString(String)} when that has detected "${".
      *
      * Calls {@link CompoundVariable#getNamedFunction(String)} if it detects:
      * '(' - start of parameter list
      * '}' - end of function call
      *
      * @param reader points to input after the "${"
      * @return the function or variable object (or a String)
+     * @throws InvalidVariableException when evaluation of variables fail
      */
     Object makeFunction(StringReader reader) throws InvalidVariableException {
         char[] current = new char[1];
         char previous = ' '; // TODO - why use space?
         StringBuilder buffer = new StringBuilder();
         Object function;
         try {
             while (reader.read(current) == 1) {
                 if (current[0] == '\\') {
                     if (reader.read(current) == 0) {
                         break;
                     }
                     previous = ' ';
                     buffer.append(current[0]);
                     continue;
                 } else if (current[0] == '(' && previous != ' ') {
                     String funcName = buffer.toString();
                     function = CompoundVariable.getNamedFunction(funcName);
                     if (function instanceof Function) {
                         ((Function) function).setParameters(parseParams(reader));
                         if (reader.read(current) == 0 || current[0] != '}') {
                             reader.reset();// set to start of string
                             char []cb = new char[100];
                             int nbRead = reader.read(cb);
                             throw new InvalidVariableException
                             ("Expected } after "+funcName+" function call in "+new String(cb, 0, nbRead));
                         }
                         if (function instanceof TestStateListener) {
                             StandardJMeterEngine.register((TestStateListener) function);
                         }
                         return function;
                     } else { // Function does not exist, so treat as per missing variable
                         buffer.append(current[0]);
                     }
                     continue;
                 } else if (current[0] == '}') {// variable, or function with no parameter list
                     function = CompoundVariable.getNamedFunction(buffer.toString());
                     if (function instanceof Function){// ensure that setParameters() is called.
                         ((Function) function).setParameters(new LinkedList<CompoundVariable>());
                     }
                     buffer.setLength(0);
                     return function;
                 } else {
                     buffer.append(current[0]);
                     previous = current[0];
                 }
             }
         } catch (IOException e) {
             log.error("Error parsing function: " + buffer.toString(), e);
             return null;
         }
         log.warn("Probably an invalid function string: " + buffer.toString());
         return buffer.toString();
     }
 
     /**
      * Compile a String into a list of parameters, each made into a
      * CompoundVariable.
      * 
      * Parses strings of the following form:
      * <ul>
      * <li>text)</li>
      * <li>text,text)</li>
      * <li></li>
      * </ul>
      * @param reader a StringReader pointing to the current input location, just after "("
      * @return a list of CompoundVariable elements
+     * @throws InvalidVariableException when evaluation of variables fail
      */
     LinkedList<CompoundVariable> parseParams(StringReader reader) throws InvalidVariableException {
         LinkedList<CompoundVariable> result = new LinkedList<CompoundVariable>();
         StringBuilder buffer = new StringBuilder();
         char[] current = new char[1];
         char previous = ' ';
         int functionRecursion = 0;
         int parenRecursion = 0;
         try {
             while (reader.read(current) == 1) {
                 if (current[0] == '\\') { // Process escaped characters
                     buffer.append(current[0]); // Store the \
                     if (reader.read(current) == 0) {
                         break; // end of buffer
                     }
                     previous = ' ';
                     buffer.append(current[0]); // store the following character
                     continue;
                 } else if (current[0] == ',' && functionRecursion == 0) {
                     CompoundVariable param = new CompoundVariable();
                     param.setParameters(buffer.toString());
                     buffer.setLength(0);
                     result.add(param);
                 } else if (current[0] == ')' && functionRecursion == 0 && parenRecursion == 0) {
                     // Detect functionName() so this does not generate empty string as the parameter
                     if (buffer.length() == 0 && result.isEmpty()){
                         return result;
                     }
                     // Normal exit occurs here
                     CompoundVariable param = new CompoundVariable();
                     param.setParameters(buffer.toString());
                     buffer.setLength(0);
                     result.add(param);
                     return result;
                 } else if (current[0] == '{' && previous == '$') {
                     buffer.append(current[0]);
                     previous = current[0];
                     functionRecursion++;
                 } else if (current[0] == '}' && functionRecursion > 0) {
                     buffer.append(current[0]);
                     previous = current[0];
                     functionRecursion--;
                 } else if (current[0] == ')' && functionRecursion == 0 && parenRecursion > 0) {
                     buffer.append(current[0]);
                     previous = current[0];
                     parenRecursion--;
                 } else if (current[0] == '(' && functionRecursion == 0) {
                     buffer.append(current[0]);
                     previous = current[0];
                     parenRecursion++;
                 } else {
                     buffer.append(current[0]);
                     previous = current[0];
                 }
             }
         } catch (IOException e) {// Should not happen with StringReader
             log.error("Error parsing function: " + buffer.toString(), e);
         }
         // Dropped out, i.e. did not find closing ')'
         log.warn("Probably an invalid function string: " + buffer.toString());
         CompoundVariable var = new CompoundVariable();
         var.setParameters(buffer.toString());
         result.add(var);
         return result;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRenderer.java b/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRenderer.java
index 24220d0a7..925308cab 100644
--- a/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRenderer.java
+++ b/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRenderer.java
@@ -1,90 +1,90 @@
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
 
 package org.apache.jmeter.gui.util;
 
 import java.awt.Component;
 import java.text.MessageFormat;
 
 import javax.swing.JTable;
 import javax.swing.SwingConstants;
 import javax.swing.UIManager;
 import javax.swing.table.DefaultTableCellRenderer;
 import javax.swing.table.JTableHeader;
 
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Renders items in a JTable by converting from resource names.
  */
 public class HeaderAsPropertyRenderer extends DefaultTableCellRenderer {
 
     private static final long serialVersionUID = 240L;
     private Object[][] columnsMsgParameters;
 
     /**
      * 
      */
     public HeaderAsPropertyRenderer() {
         this(null);
     }
     
     /**
      * @param columnsMsgParameters Optional parameters of i18n keys
      */
     public HeaderAsPropertyRenderer(Object[][] columnsMsgParameters) {
         super();
         this.columnsMsgParameters = columnsMsgParameters;
     }
 
     @Override
     public Component getTableCellRendererComponent(JTable table, Object value,
             boolean isSelected, boolean hasFocus, int row, int column) {
         if (table != null) {
             JTableHeader header = table.getTableHeader();
             if (header != null){
                 setForeground(header.getForeground());
                 setBackground(header.getBackground());
                 setFont(header.getFont());
             }
             setText(getText(value, row, column));
             setBorder(UIManager.getBorder("TableHeader.cellBorder"));
             setHorizontalAlignment(SwingConstants.CENTER);
         }
         return this;
     }
 
     /**
      * Get the text for the value as the translation of the resource name.
      *
-     * @param value
-     * @param column
-     * @param row
+     * @param value value for which to get the translation
+     * @param column index which column message parameters should be used
+     * @param row not used
      * @return the text
      */
     protected String getText(Object value, int row, int column) {
         if (value == null){
             return "";
         }
         if(columnsMsgParameters != null && columnsMsgParameters[column] != null) {
             return MessageFormat.format(JMeterUtils.getResString(value.toString()), columnsMsgParameters[column]);
         } else {
             return JMeterUtils.getResString(value.toString());
         }
     }
 }
diff --git a/src/jorphan/org/apache/commons/cli/avalon/Token.java b/src/jorphan/org/apache/commons/cli/avalon/Token.java
index 502f74873..acaeeb7d9 100644
--- a/src/jorphan/org/apache/commons/cli/avalon/Token.java
+++ b/src/jorphan/org/apache/commons/cli/avalon/Token.java
@@ -1,71 +1,80 @@
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
  * distributed  under the  License is distributed on an "AS IS" BASIS,
  * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
  * implied.
  *
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 package org.apache.commons.cli.avalon;
 
 // Renamed from org.apache.avalon.excalibur.cli
 
 /**
  * Token handles tokenizing the CLI arguments
  *
  */
 class Token {
     /** Type for a separator token */
     public static final int TOKEN_SEPARATOR = 0;
 
     /** Type for a text token */
     public static final int TOKEN_STRING = 1;
 
     private final int m_type;
 
     private final String m_value;
 
     /**
      * New Token object with a type and value
+     *
+     * @param type
+     *            type of the token
+     * @param value
+     *            value of the token
      */
     Token(final int type, final String value) {
         m_type = type;
         m_value = value;
     }
 
     /**
      * Get the value of the token
+     *
+     * @return value of the token
      */
     final String getValue() {
         return m_value;
     }
 
     /**
      * Get the type of the token
+     *
+     * @return type of the token
      */
     final int getType() {
         return m_type;
     }
 
     /**
      * Convert to a string
      */
     @Override
     public final String toString() {
         final StringBuilder sb = new StringBuilder();
         sb.append(m_type);
         sb.append(":");
         sb.append(m_value);
         return sb.toString();
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/collections/HashTree.java b/src/jorphan/org/apache/jorphan/collections/HashTree.java
index 6ef66841e..b915c6752 100644
--- a/src/jorphan/org/apache/jorphan/collections/HashTree.java
+++ b/src/jorphan/org/apache/jorphan/collections/HashTree.java
@@ -1,1081 +1,1087 @@
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
 
 package org.apache.jorphan.collections;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 /**
  * This class is used to create a tree structure of objects. Each element in the
  * tree is also a key to the next node down in the tree. It provides many ways
  * to add objects and branches, as well as many ways to retrieve.
  * <p>
  * HashTree implements the Map interface for convenience reasons. The main
  * difference between a Map and a HashTree is that the HashTree organizes the
  * data into a recursive tree structure, and provides the means to manipulate
  * that structure.
  * <p>
  * Of special interest is the {@link #traverse(HashTreeTraverser)} method, which
  * provides an expedient way to traverse any HashTree by implementing the
  * {@link HashTreeTraverser} interface in order to perform some operation on the
  * tree, or to extract information from the tree.
  *
  * @see HashTreeTraverser
  * @see SearchByClass
  */
 public class HashTree implements Serializable, Map<Object, HashTree>, Cloneable {
 
     private static final long serialVersionUID = 240L;
 
     // Used for the RuntimeException to short-circuit the traversal
     private static final String FOUND = "found"; // $NON-NLS-1$
 
     // N.B. The keys can be either JMeterTreeNode or TestElement
     protected final Map<Object, HashTree> data;
 
     /**
      * Creates an empty new HashTree.
      */
     public HashTree() {
         this(null, null);
     }
 
     /**
      * Allow subclasses to provide their own Map.
+     * @param _map {@link Map} to use
      */
     protected HashTree(Map<Object, HashTree> _map) {
         this(_map, null);
     }
 
     /**
      * Creates a new HashTree and adds the given object as a top-level node.
      *
      * @param key
      *            name of the new top-level node
      */
     public HashTree(Object key) {
         this(new HashMap<Object, HashTree>(), key);
     }
     
     /**
      * Uses the new HashTree if not null and adds the given object as a top-level node if not null
      * @param _map
      * @param key
      */
     private HashTree(Map<Object, HashTree> _map, Object key) {
         if(_map != null) {
             data = _map;
         } else {
             data = new HashMap<Object, HashTree>();
         }
         if(key != null) {
             data.put(key, new HashTree());
         }
     }
 
     /**
      * The Map given must also be a HashTree, otherwise an
      * UnsupportedOperationException is thrown. If it is a HashTree, this is
      * like calling the add(HashTree) method.
      *
      * @see #add(HashTree)
      * @see java.util.Map#putAll(Map)
      */
     @Override
     public void putAll(Map<? extends Object, ? extends HashTree> map) {
         if (map instanceof HashTree) {
             this.add((HashTree) map);
         } else {
             throw new UnsupportedOperationException("can only putAll other HashTree objects");
         }
     }
 
     /**
      * Exists to satisfy the Map interface.
      *
      * @see java.util.Map#entrySet()
      */
     @Override
     public Set<Entry<Object, HashTree>> entrySet() {
         return data.entrySet();
     }
 
     /**
      * Implemented as required by the Map interface, but is not very useful
      * here. All 'values' in a HashTree are HashTree's themselves.
      *
      * @param value
      *            Object to be tested as a value.
      * @return True if the HashTree contains the value, false otherwise.
      * @see java.util.Map#containsValue(Object)
      */
     @Override
     public boolean containsValue(Object value) {
         return data.containsValue(value);
     }
 
     /**
      * This is the same as calling HashTree.add(key,value).
      *
      * @param key
      *            to use
      * @param value
      *            to store against key
      * @see java.util.Map#put(Object, Object)
      */
     @Override
     public HashTree put(Object key, HashTree value) {
         HashTree previous = data.get(key);
         add(key, value);
         return previous;
     }
 
     /**
      * Clears the HashTree of all contents.
      *
      * @see java.util.Map#clear()
      */
     @Override
     public void clear() {
         data.clear();
     }
 
     /**
      * Returns a collection of all the sub-trees of the current tree.
      *
      * @see java.util.Map#values()
      */
     @Override
     public Collection<HashTree> values() {
         return data.values();
     }
 
     /**
      * Adds a key as a node at the current level and then adds the given
      * HashTree to that new node.
      *
      * @param key
      *            key to create in this tree
      * @param subTree
      *            sub tree to add to the node created for the first argument.
      */
     public void add(Object key, HashTree subTree) {
         add(key).add(subTree);
     }
 
     /**
      * Adds all the nodes and branches of the given tree to this tree. Is like
      * merging two trees. Duplicates are ignored.
      *
      * @param newTree the tree to be added
      */
     public void add(HashTree newTree) {
         for (Object item : newTree.list()) {
             add(item).add(newTree.getTree(item));
         }
     }
 
     /**
      * Creates a new HashTree and adds all the objects in the given collection
      * as top-level nodes in the tree.
      *
      * @param keys
      *            a collection of objects to be added to the created HashTree.
      */
     public HashTree(Collection<?> keys) {
         data = new HashMap<Object, HashTree>();
         for (Object o : keys) {
             data.put(o, new HashTree());
         }
     }
 
     /**
      * Creates a new HashTree and adds all the objects in the given array as
      * top-level nodes in the tree.
      *
      * @param keys
      *            array with names for the new top-level nodes
      */
     public HashTree(Object[] keys) {
         data = new HashMap<Object, HashTree>();
         for (int x = 0; x < keys.length; x++) {
             data.put(keys[x], new HashTree());
         }
     }
 
     /**
      * If the HashTree contains the given object as a key at the top level, then
      * a true result is returned, otherwise false.
      *
      * @param o
      *            Object to be tested as a key.
      * @return True if the HashTree contains the key, false otherwise.
      * @see java.util.Map#containsKey(Object)
      */
     @Override
     public boolean containsKey(Object o) {
         return data.containsKey(o);
     }
 
     /**
      * If the HashTree is empty, true is returned, false otherwise.
      *
      * @return True if HashTree is empty, false otherwise.
      */
     @Override
     public boolean isEmpty() {
         return data.isEmpty();
     }
 
     /**
      * Sets a key and it's value in the HashTree. It actually sets up a key, and
      * then creates a node for the key and sets the value to the new node, as a
      * key. Any previous nodes that existed under the given key are lost.
      *
      * @param key
      *            key to be set up
      * @param value
      *            value to be set up as a key in the secondary node
      */
     public void set(Object key, Object value) {
         data.put(key, createNewTree(value));
     }
 
     /**
      * Sets a key into the current tree and assigns it a HashTree as its
      * subtree. Any previous entries under the given key are removed.
      *
      * @param key
      *            key to be set up
      * @param t
      *            HashTree that the key maps to
      */
     public void set(Object key, HashTree t) {
         data.put(key, t);
     }
 
     /**
      * Sets a key and its values in the HashTree. It sets up a key in the
      * current node, and then creates a node for that key, and sets all the
      * values in the array as keys in the new node. Any keys previously held
      * under the given key are lost.
      *
      * @param key
      *            Key to be set up
      * @param values
      *            Array of objects to be added as keys in the secondary node
      */
     public void set(Object key, Object[] values) {
         data.put(key, createNewTree(Arrays.asList(values)));
     }
 
     /**
      * Sets a key and its values in the HashTree. It sets up a key in the
      * current node, and then creates a node for that key, and set all the
      * values in the array as keys in the new node. Any keys previously held
      * under the given key are removed.
      *
      * @param key
      *            key to be set up
      * @param values
      *            Collection of objects to be added as keys in the secondary
      *            node
      */
     public void set(Object key, Collection<?> values) {
         data.put(key, createNewTree(values));
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key array as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the array.
      * Continues recursing in this manner until the end of the first array is
      * reached, at which point all the values of the second array are set as
      * keys to the bottom-most node. All previous keys of that bottom-most node
      * are removed.
      *
      * @param treePath
      *            array of keys to put into HashTree
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void set(Object[] treePath, Object[] values) {
         if (treePath != null && values != null) {
             set(Arrays.asList(treePath), Arrays.asList(values));
         }
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key array as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the array.
      * Continues recursing in this manner until the end of the first array is
      * reached, at which point all the values of the Collection of values are
      * set as keys to the bottom-most node. Any keys previously held by the
      * bottom-most node are lost.
      *
      * @param treePath
      *            array of keys to put into HashTree
      * @param values
      *            Collection of values to be added as keys to bottom-most node
      */
     public void set(Object[] treePath, Collection<?> values) {
         if (treePath != null) {
             set(Arrays.asList(treePath), values);
         }
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key list as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the list.
      * Continues recursing in this manner until the end of the first list is
      * reached, at which point all the values of the array of values are set as
      * keys to the bottom-most node. Any previously existing keys of that bottom
      * node are removed.
      *
      * @param treePath
      *            collection of keys to put into HashTree
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void set(Collection<?> treePath, Object[] values) {
         HashTree tree = addTreePath(treePath);
         tree.set(Arrays.asList(values));
     }
 
     /**
      * Sets the nodes of the current tree to be the objects of the given
      * collection. Any nodes previously in the tree are removed.
      *
      * @param values
      *            Collection of objects to set as nodes.
      */
     public void set(Collection<?> values) {
         clear();
         this.add(values);
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key list as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the list.
      * Continues recursing in this manner until the end of the first list is
      * reached, at which point all the values of the Collection of values are
      * set as keys to the bottom-most node. Any previously existing keys of that
      * bottom node are lost.
      *
      * @param treePath
      *            list of keys to put into HashTree
      * @param values
      *            collection of values to be added as keys to bottom-most node
      */
     public void set(Collection<?> treePath, Collection<?> values) {
         HashTree tree = addTreePath(treePath);
         tree.set(values);
     }
 
     /**
      * Adds an key into the HashTree at the current level. If a HashTree exists
      * for the key already, no new tree will be added
      *
      * @param key
      *            key to be added to HashTree
      * @return newly generated tree, if no tree was found for the given key;
      *         existing key otherwise
      */
     public HashTree add(Object key) {
         if (!data.containsKey(key)) {
             HashTree newTree = createNewTree();
             data.put(key, newTree);
             return newTree;
         }
         return getTree(key);
     }
 
     /**
      * Adds all the given objects as nodes at the current level.
      *
      * @param keys
      *            Array of Keys to be added to HashTree.
      */
     public void add(Object[] keys) {
         for (int x = 0; x < keys.length; x++) {
             add(keys[x]);
         }
     }
 
     /**
      * Adds a bunch of keys into the HashTree at the current level.
      *
      * @param keys
      *            Collection of Keys to be added to HashTree.
      */
     public void add(Collection<?> keys) {
         for (Object o : keys) {
             add(o);
         }
     }
 
     /**
      * Adds a key and it's value in the HashTree. The first argument becomes a
      * node at the current level, and the second argument becomes a node of it.
      *
      * @param key
      *            key to be added
      * @param value
      *            value to be added as a key in the secondary node
      * @return HashTree for which <code>value</code> is the key
      */
     public HashTree add(Object key, Object value) {
         return add(key).add(value);
     }
 
     /**
      * Adds a key and it's values in the HashTree. The first argument becomes a
      * node at the current level, and adds all the values in the array to the
      * new node.
      *
      * @param key
      *            key to be added
      * @param values
      *            array of objects to be added as keys in the secondary node
      */
     public void add(Object key, Object[] values) {
         add(key).add(values);
     }
 
     /**
      * Adds a key as a node at the current level and then adds all the objects
      * in the second argument as nodes of the new node.
      *
      * @param key
      *            key to be added
      * @param values
      *            Collection of objects to be added as keys in the secondary
      *            node
      */
     public void add(Object key, Collection<?> values) {
         add(key).add(values);
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is an array that represents a path to a specific node in the
      * tree. If the path doesn't already exist, it is created (the objects are
      * added along the way). At the path, all the objects in the second argument
      * are added as nodes.
      *
      * @param treePath
      *            an array of objects representing a path
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void add(Object[] treePath, Object[] values) {
         if (treePath != null) {
             add(Arrays.asList(treePath), Arrays.asList(values));
         }
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is an array that represents a path to a specific node in the
      * tree. If the path doesn't already exist, it is created (the objects are
      * added along the way). At the path, all the objects in the second argument
      * are added as nodes.
      *
      * @param treePath
      *            an array of objects representing a path
      * @param values
      *            collection of values to be added as keys to bottom-most node
      */
     public void add(Object[] treePath, Collection<?> values) {
         if (treePath != null) {
             add(Arrays.asList(treePath), values);
         }
     }
 
     public HashTree add(Object[] treePath, Object value) {
         return add(Arrays.asList(treePath), value);
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is a List that represents a path to a specific node in the tree.
      * If the path doesn't already exist, it is created (the objects are added
      * along the way). At the path, all the objects in the second argument are
      * added as nodes.
      *
      * @param treePath
      *            a list of objects representing a path
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void add(Collection<?> treePath, Object[] values) {
         HashTree tree = addTreePath(treePath);
         tree.add(Arrays.asList(values));
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is a List that represents a path to a specific node in the tree.
      * If the path doesn't already exist, it is created (the objects are added
      * along the way). At the path, the object in the second argument is added
      * as a node.
      *
      * @param treePath
      *            a list of objects representing a path
      * @param value
      *            Object to add as a node to bottom-most node
      * @return HashTree for which <code>value</code> is the key
      */
     public HashTree add(Collection<?> treePath, Object value) {
         HashTree tree = addTreePath(treePath);
         return tree.add(value);
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is a SortedSet that represents a path to a specific node in the
      * tree. If the path doesn't already exist, it is created (the objects are
      * added along the way). At the path, all the objects in the second argument
      * are added as nodes.
      *
      * @param treePath
      *            a SortedSet of objects representing a path
      * @param values
      *            Collection of values to be added as keys to bottom-most node
      */
     public void add(Collection<?> treePath, Collection<?> values) {
         HashTree tree = addTreePath(treePath);
         tree.add(values);
     }
 
     protected HashTree addTreePath(Collection<?> treePath) {
         HashTree tree = this;
         for (Object temp : treePath) {
             tree = tree.add(temp);
         }
         return tree;
     }
 
     /**
      * Gets the HashTree mapped to the given key.
      *
      * @param key
      *            Key used to find appropriate HashTree()
      * @return the HashTree for <code>key</code>
      */
     public HashTree getTree(Object key) {
         return data.get(key);
     }
 
     /**
      * Returns the HashTree object associated with the given key. Same as
      * calling {@link #getTree(Object)}.
      *
      * @see java.util.Map#get(Object)
      */
     @Override
     public HashTree get(Object key) {
         return getTree(key);
     }
 
     /**
      * Gets the HashTree object mapped to the last key in the array by recursing
      * through the HashTree structure one key at a time.
      *
      * @param treePath
      *            array of keys.
      * @return HashTree at the end of the recursion.
      */
     public HashTree getTree(Object[] treePath) {
         if (treePath != null) {
             return getTree(Arrays.asList(treePath));
         }
         return this;
     }
 
     /**
      * Create a clone of this HashTree. This is not a deep clone (ie, the
      * contents of the tree are not cloned).
      *
      */
     @Override
     public Object clone() {
         HashTree newTree = new HashTree();
         cloneTree(newTree);
         return newTree;
     }
 
     protected void cloneTree(HashTree newTree) {
         for (Object key : list()) {
             newTree.set(key, (HashTree) getTree(key).clone());
         }
     }
 
     /**
      * Creates a new tree. This method exists to allow inheriting classes to
      * generate the appropriate types of nodes. For instance, when a node is
      * added, it's value is a HashTree. Rather than directly calling the
      * HashTree() constructor, the createNewTree() method is called. Inheriting
      * classes should override these methods and create the appropriate subclass
      * of HashTree.
      *
      * @return HashTree
      */
     protected HashTree createNewTree() {
         return new HashTree();
     }
 
     /**
      * Creates a new tree. This method exists to allow inheriting classes to
      * generate the appropriate types of nodes. For instance, when a node is
      * added, it's value is a HashTree. Rather than directly calling the
      * HashTree() constructor, the createNewTree() method is called. Inheriting
      * classes should override these methods and create the appropriate subclass
      * of HashTree.
      *
-     * @return HashTree
+     * @param key
+     *            object to use as the key for the top level
+     *
+     * @return newly created {@link HashTree}
      */
     protected HashTree createNewTree(Object key) {
         return new HashTree(key);
     }
 
     /**
      * Creates a new tree. This method exists to allow inheriting classes to
      * generate the appropriate types of nodes. For instance, when a node is
      * added, it's value is a HashTree. Rather than directly calling the
      * HashTree() constructor, the createNewTree() method is called. Inheriting
      * classes should override these methods and create the appropriate subclass
      * of HashTree.
      *
-     * @return HashTree
+     * @param values objects to be added to the new {@link HashTree}
+     *
+     * @return newly created {@link HashTree}
      */
     protected HashTree createNewTree(Collection<?> values) {
         return new HashTree(values);
     }
 
     /**
      * Gets the HashTree object mapped to the last key in the SortedSet by
      * recursing through the HashTree structure one key at a time.
      *
      * @param treePath
      *            Collection of keys
      * @return HashTree at the end of the recursion
      */
     public HashTree getTree(Collection<?> treePath) {
         return getTreePath(treePath);
     }
 
     /**
      * Gets a Collection of all keys in the current HashTree node. If the
      * HashTree represented a file system, this would be like getting a
      * collection of all the files in the current folder.
      *
      * @return Set of all keys in this HashTree
      */
     public Collection<Object> list() {
         return data.keySet();
     }
 
     /**
      * Gets a Set of all keys in the HashTree mapped to the given key of the
      * current HashTree object (in other words, one level down. If the HashTree
      * represented a file system, this would like getting a list of all files in
      * a sub-directory (of the current directory) specified by the key argument.
      *
      * @param key
      *            key used to find HashTree to get list of
      * @return Set of all keys in found HashTree.
      */
     public Collection<?> list(Object key) {
         HashTree temp = data.get(key);
         if (temp != null) {
             return temp.list();
         }
         return new HashSet<Object>();
     }
 
     /**
      * Removes the entire branch specified by the given key.
      *
      * @see java.util.Map#remove(Object)
      */
     @Override
     public HashTree remove(Object key) {
         return data.remove(key);
     }
 
     /**
      * Recurses down into the HashTree stucture using each subsequent key in the
      * array of keys, and returns the Set of keys of the HashTree object at the
      * end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            Array of keys used to recurse into HashTree structure
      * @return Set of all keys found in end HashTree
      */
     public Collection<?> list(Object[] treePath) { // TODO not used?
         if (treePath != null) {
             return list(Arrays.asList(treePath));
         }
         return list();
     }
 
     /**
      * Recurses down into the HashTree stucture using each subsequent key in the
      * List of keys, and returns the Set of keys of the HashTree object at the
      * end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            List of keys used to recurse into HashTree structure
      * @return Set of all keys found in end HashTree
      */
     public Collection<?> list(Collection<?> treePath) {
         HashTree tree = getTreePath(treePath);
         if (tree != null) {
             return tree.list();
         }
         return new HashSet<Object>();
     }
 
     /**
      * Finds the given current key, and replaces it with the given new key. Any
      * tree structure found under the original key is moved to the new key.
      *
      * @param currentKey name of the key to be replaced
      * @param newKey name of the new key
      */
     public void replaceKey(Object currentKey, Object newKey) {
         HashTree tree = getTree(currentKey);
         data.remove(currentKey);
         data.put(newKey, tree);
     }
 
     /**
      * Gets an array of all keys in the current HashTree node. If the HashTree
      * represented a file system, this would be like getting an array of all the
      * files in the current folder.
      *
      * @return array of all keys in this HashTree.
      */
     public Object[] getArray() {
         return data.keySet().toArray();
     }
 
     /**
      * Gets an array of all keys in the HashTree mapped to the given key of the
      * current HashTree object (in other words, one level down). If the HashTree
      * represented a file system, this would like getting a list of all files in
      * a sub-directory (of the current directory) specified by the key argument.
      *
      * @param key
      *            key used to find HashTree to get list of
      * @return array of all keys in found HashTree
      */
     public Object[] getArray(Object key) {
         HashTree t = getTree(key);
         if (t != null) {
             return t.getArray();
         }
         return null;
     }
 
     /**
      * Recurses down into the HashTree stucture using each subsequent key in the
      * array of keys, and returns an array of keys of the HashTree object at the
      * end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            array of keys used to recurse into HashTree structure
      * @return array of all keys found in end HashTree
      */
     public Object[] getArray(Object[] treePath) {
         if (treePath != null) {
             return getArray(Arrays.asList(treePath));
         }
         return getArray();
     }
 
     /**
      * Recurses down into the HashTree structure using each subsequent key in the
      * treePath argument, and returns an array of keys of the HashTree object at
      * the end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            list of keys used to recurse into HashTree structure
      * @return array of all keys found in end HashTree
      */
     public Object[] getArray(Collection<?> treePath) {
         HashTree tree = getTreePath(treePath);
         return (tree != null) ? tree.getArray() : null;
     }
 
     protected HashTree getTreePath(Collection<?> treePath) {
         HashTree tree = this;
         Iterator<?> iter = treePath.iterator();
         while (iter.hasNext()) {
             if (tree == null) {
                 return null;
             }
             Object temp = iter.next();
             tree = tree.getTree(temp);
         }
         return tree;
     }
 
     /**
      * Returns a hashcode for this HashTree.
      *
      * @see java.lang.Object#hashCode()
      */
     @Override
     public int hashCode() {
         return data.hashCode() * 7;
     }
 
     /**
      * Compares all objects in the tree and verifies that the two trees contain
      * the same objects at the same tree levels. Returns true if they do, false
      * otherwise.
      *
      * @param o
      *            Object to be compared against
      * @see java.lang.Object#equals(Object)
      */
     @Override
     public boolean equals(Object o) {
         if (!(o instanceof HashTree)) {
             return false;
         }
         HashTree oo = (HashTree) o;
         if (oo.size() != this.size()) {
             return false;
         }
         return data.equals(oo.data);
     }
 
     /**
      * Returns a Set of all the keys in the top-level of this HashTree.
      *
      * @see java.util.Map#keySet()
      */
     @Override
     public Set<Object> keySet() {
         return data.keySet();
     }
 
     /**
      * Searches the HashTree structure for the given key. If it finds the key,
      * it returns the HashTree mapped to the key. If it finds nothing, it
      * returns null.
      *
      * @param key
      *            Key to search for
      * @return HashTree mapped to key, if found, otherwise <code>null</code>
      */
     public HashTree search(Object key) {// TODO does not appear to be used
         HashTree result = getTree(key);
         if (result != null) {
             return result;
         }
         TreeSearcher searcher = new TreeSearcher(key);
         try {
             traverse(searcher);
         } catch (RuntimeException e) {
             if (!e.getMessage().equals(FOUND)){
                 throw e;
             }
             // do nothing - means object is found
         }
         return searcher.getResult();
     }
 
     /**
      * Method readObject.
      */
     private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
         ois.defaultReadObject();
     }
 
     private void writeObject(ObjectOutputStream oos) throws IOException {
         oos.defaultWriteObject();
     }
 
     /**
      * Returns the number of top-level entries in the HashTree.
      *
      * @see java.util.Map#size()
      */
     @Override
     public int size() {
         return data.size();
     }
 
     /**
      * Allows any implementation of the HashTreeTraverser interface to easily
      * traverse (depth-first) all the nodes of the HashTree. The Traverser
      * implementation will be given notification of each node visited.
      *
      * @see HashTreeTraverser
      * @param visitor
      *            the visitor that wants to traverse the tree
      */
     public void traverse(HashTreeTraverser visitor) {
         for (Object item : list()) {
             visitor.addNode(item, getTree(item));
             getTree(item).traverseInto(visitor);
         }
     }
 
     /**
      * The recursive method that accomplishes the tree-traversal and performs
      * the callbacks to the HashTreeTraverser.
      */
     private void traverseInto(HashTreeTraverser visitor) {
 
         if (list().size() == 0) {
             visitor.processPath();
         } else {
             for (Object item : list()) {
                 final HashTree treeItem = getTree(item);
                 visitor.addNode(item, treeItem);
                 treeItem.traverseInto(visitor);
             }
         }
         visitor.subtractNode();
     }
 
     /**
      * Generate a printable representation of the tree.
      *
      * @return a representation of the tree
      */
     @Override
     public String toString() {
         ConvertToString converter = new ConvertToString();
         try {
             traverse(converter);
         } catch (Exception e) { // Just in case
             converter.reportError(e);
         }
         return converter.toString();
     }
 
     private static class TreeSearcher implements HashTreeTraverser {
 
         private final Object target;
 
         private HashTree result;
 
         public TreeSearcher(Object t) {
             target = t;
         }
 
         public HashTree getResult() {
             return result;
         }
 
         /** {@inheritDoc} */
         @Override
         public void addNode(Object node, HashTree subTree) {
             result = subTree.getTree(target);
             if (result != null) {
                 // short circuit traversal when found
                 throw new RuntimeException(FOUND);
             }
         }
 
         /** {@inheritDoc} */
         @Override
         public void processPath() {
             // Not used
         }
 
         /** {@inheritDoc} */
         @Override
         public void subtractNode() {
             // Not used
         }
     }
 
     private static class ConvertToString implements HashTreeTraverser {
         private final StringBuilder string = new StringBuilder(getClass().getName() + "{");
 
         private final StringBuilder spaces = new StringBuilder();
 
         private int depth = 0;
 
         @Override
         public void addNode(Object key, HashTree subTree) {
             depth++;
             string.append("\n").append(getSpaces()).append(key);
             string.append(" {");
         }
 
         @Override
         public void subtractNode() {
             string.append("\n" + getSpaces() + "}");
             depth--;
         }
 
         @Override
         public void processPath() {
         }
 
         @Override
         public String toString() {
             string.append("\n}");
             return string.toString();
         }
 
         void reportError(Throwable t){
             string.append("Error: ").append(t.toString());
         }
 
         private String getSpaces() {
             if (spaces.length() < depth * 2) {
                 while (spaces.length() < depth * 2) {
                     spaces.append("  ");
                 }
             } else if (spaces.length() > depth * 2) {
                 spaces.setLength(depth * 2);
             }
             return spaces.toString();
         }
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/exec/StreamCopier.java b/src/jorphan/org/apache/jorphan/exec/StreamCopier.java
index 1f46e1335..fc2d0bd4d 100644
--- a/src/jorphan/org/apache/jorphan/exec/StreamCopier.java
+++ b/src/jorphan/org/apache/jorphan/exec/StreamCopier.java
@@ -1,72 +1,72 @@
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
 
 package org.apache.jorphan.exec;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Thread that copies a stream in the background; closes both input and output streams.
  * @since 2.8
  */
 class StreamCopier extends Thread {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final InputStream is;
     private final OutputStream os;
 
     /**
      * @param is {@link InputStream}
-     * @param is {@link OutputStream}
+     * @param os {@link OutputStream}
      * @throws IOException 
      */
     StreamCopier(InputStream is, OutputStream os) throws IOException {
         this.is = is;
         this.os = os;
     }
 
     /**
      * @see java.lang.Thread#run()
      */
     @Override
     public void run() {
         final boolean isSystemOutput = os.equals(System.out) || os.equals(System.err);
         try {
             IOUtils.copyLarge(is, os);
             if (!isSystemOutput){
                 os.close();
             }
             is.close();
         } catch (IOException e) {
             log.warn("Error writing stream", e);
         } finally {
             IOUtils.closeQuietly(is);
             if (!isSystemOutput){
                 IOUtils.closeQuietly(os);
             }
         }
     }
     
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
index 2e6d39916..f9063d88b 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
@@ -1,779 +1,783 @@
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
  * <ul>
  * <li>host and port</li>
  * <li>connect and response timeouts</li>
  * <li>path, method, encoding, parameters</li>
  * <li>redirects and keepalive</li>
  * </ul>
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
 
     /**
      * Constructor which is setup to show HTTP implementation, raw body pane and
      * sampler fields.
      */
     public UrlConfigGui() {
         this(true);
     }
 
     /**
      * Constructor which is setup to show HTTP implementation and raw body pane.
      *
      * @param showSamplerFields
      *            flag whether sampler fields should be shown.
      */
     public UrlConfigGui(boolean showSamplerFields) {
         this(showSamplerFields, true, true);
     }
 
     /**
      * @param showSamplerFields
      *            flag whether sampler fields should be shown
      * @param showImplementation
      *            Show HTTP Implementation
      * @param showRawBodyPane
      *            flag whether the raw body pane should be shown
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
      * @param element {@link TestElement} to modify
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
          *
          * @param index
          *            index to select
          * @param check
          *            flag whether to perform checks before setting the selected
          *            index
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
+     * Checks if no data is available in the selected tab
+     *
+     * @param oldSelectedIndex
+     *            the tab to check for data
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
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/Authorization.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/Authorization.java
index 1b8f95c35..00233dc82 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/Authorization.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/Authorization.java
@@ -1,130 +1,136 @@
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
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.Serializable;
 
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.protocol.http.control.AuthManager.Mechanism;
 import org.apache.jmeter.protocol.http.util.Base64Encoder;
 import org.apache.jmeter.testelement.AbstractTestElement;
 
 /**
  * This class is an Authorization encapsulator.
  *
  */
 public class Authorization extends AbstractTestElement implements Serializable {
 
     private static final long serialVersionUID = 241L;
 
     private static final String URL = "Authorization.url"; // $NON-NLS-1$
 
     private static final String USERNAME = "Authorization.username"; // $NON-NLS-1$
 
     private static final String PASSWORD = "Authorization.password"; // $NON-NLS-1$
 
     private static final String DOMAIN = "Authorization.domain"; // $NON-NLS-1$
 
     private static final String REALM = "Authorization.realm"; // $NON-NLS-1$
 
     private static final String MECHANISM = "Authorization.mechanism"; // $NON-NLS-1$
 
     private static final String TAB = "\t"; // $NON-NLS-1$
 
     /**
      * create the authorization
+     * @param url url for which the authorization should be considered
+     * @param user name of the user
+     * @param pass password for the user
+     * @param domain authorization domain (used for NTML-authentication)
+     * @param realm authorization realm
+     * @param mechanism authorization mechanism, that should be used
      */
     Authorization(String url, String user, String pass, String domain, String realm, Mechanism mechanism) {
         setURL(url);
         setUser(user);
         setPass(pass);
         setDomain(domain);
         setRealm(realm);
         setMechanism(mechanism);
     }
 
     public boolean expectsModification() {
         return false;
     }
 
     public Authorization() {
         this("","","","","", Mechanism.BASIC_DIGEST);
     }
 
     public void addConfigElement(ConfigElement config) {
     }
 
     public String getURL() {
         return getPropertyAsString(URL);
     }
 
     public void setURL(String url) {
         setProperty(URL, url);
     }
 
     public String getUser() {
         return getPropertyAsString(USERNAME);
     }
 
     public void setUser(String user) {
         setProperty(USERNAME, user);
     }
 
     public String getPass() {
         return getPropertyAsString(PASSWORD);
     }
 
     public void setPass(String pass) {
         setProperty(PASSWORD, pass);
     }
 
     public String getDomain() {
         return getPropertyAsString(DOMAIN);
     }
 
     public void setDomain(String domain) {
         setProperty(DOMAIN, domain);
     }
 
     public String getRealm() {
         return getPropertyAsString(REALM);
     }
 
     public void setRealm(String realm) {
         setProperty(REALM, realm);
     }
 
     public Mechanism getMechanism() {
         return Mechanism.valueOf(getPropertyAsString(MECHANISM, Mechanism.BASIC_DIGEST.name()));
     }
 
     public void setMechanism(Mechanism mechanism) {
         setProperty(MECHANISM, mechanism.name(), Mechanism.BASIC_DIGEST.name());
     }
 
     // Used for saving entries to a file
     @Override
     public String toString() {
         return getURL() + TAB + getUser() + TAB + getPass() + TAB + getDomain() + TAB + getRealm() + TAB + getMechanism();
     }
 
     public String toBasicHeader(){
         return "Basic " + Base64Encoder.encode(getUser() + ":" + getPass());
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/HC4CookieHandler.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/HC4CookieHandler.java
index da54b8d40..9e9db8e16 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/HC4CookieHandler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/HC4CookieHandler.java
@@ -1,213 +1,215 @@
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
  * distributed  under the  License is distributed on an "AS IS" BASIS,
  * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
  * implied.
  * 
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 import org.apache.http.Header;
 import org.apache.http.client.params.CookiePolicy;
 import org.apache.http.cookie.CookieOrigin;
 import org.apache.http.cookie.CookieSpec;
 import org.apache.http.cookie.CookieSpecRegistry;
 import org.apache.http.cookie.MalformedCookieException;
 import org.apache.http.impl.cookie.BasicClientCookie;
 import org.apache.http.impl.cookie.BestMatchSpecFactory;
 import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
 import org.apache.http.impl.cookie.IgnoreSpecFactory;
 import org.apache.http.impl.cookie.NetscapeDraftSpecFactory;
 import org.apache.http.impl.cookie.RFC2109SpecFactory;
 import org.apache.http.impl.cookie.RFC2965SpecFactory;
 import org.apache.http.message.BasicHeader;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class HC4CookieHandler implements CookieHandler {
     private static final Logger log = LoggingManager.getLoggerForClass();
     
     private final transient CookieSpec cookieSpec;
     
     private static CookieSpecRegistry registry  = new CookieSpecRegistry();
 
     static {
         registry.register(CookiePolicy.BEST_MATCH, new BestMatchSpecFactory());
         registry.register(CookiePolicy.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory());
         registry.register(CookiePolicy.RFC_2109, new RFC2109SpecFactory());
         registry.register(CookiePolicy.RFC_2965, new RFC2965SpecFactory());
         registry.register(CookiePolicy.IGNORE_COOKIES, new IgnoreSpecFactory());
         registry.register(CookiePolicy.NETSCAPE, new NetscapeDraftSpecFactory());
     }
 
     public HC4CookieHandler(String policy) {
         super();
         if (policy.equals(org.apache.commons.httpclient.cookie.CookiePolicy.DEFAULT)) { // tweak diff HC3 vs HC4
             policy = CookiePolicy.BEST_MATCH;
         }
         this.cookieSpec = registry.getCookieSpec(policy);
     }
 
     @Override
     public void addCookieFromHeader(CookieManager cookieManager,
             boolean checkCookies, String cookieHeader, URL url) {
             boolean debugEnabled = log.isDebugEnabled();
             if (debugEnabled) {
                 log.debug("Received Cookie: " + cookieHeader + " From: " + url.toExternalForm());
             }
             String protocol = url.getProtocol();
             String host = url.getHost();
             int port= HTTPSamplerBase.getDefaultPort(protocol,url.getPort());
             String path = url.getPath();
             boolean isSecure=HTTPSamplerBase.isSecure(protocol);
 
             List<org.apache.http.cookie.Cookie> cookies = null;
             
             CookieOrigin cookieOrigin = new CookieOrigin(host, port, path, isSecure);
             BasicHeader basicHeader = new BasicHeader(HTTPConstants.HEADER_SET_COOKIE, cookieHeader);
 
             try {
                 cookies = cookieSpec.parse(basicHeader, cookieOrigin);
             } catch (MalformedCookieException e) {
                 log.error("Unable to add the cookie", e);
             }
             if (cookies == null) {
                 return;
             }
             for (org.apache.http.cookie.Cookie cookie : cookies) {
                 try {
                     if (checkCookies) {
                         cookieSpec.validate(cookie, cookieOrigin);
                     }
                     Date expiryDate = cookie.getExpiryDate();
                     long exp = 0;
                     if (expiryDate!= null) {
                         exp=expiryDate.getTime();
                     }
                     Cookie newCookie = new Cookie(
                             cookie.getName(),
                             cookie.getValue(),
                             cookie.getDomain(),
                             cookie.getPath(),
                             cookie.isSecure(),
                             exp / 1000
                             );
 
                     // Store session cookies as well as unexpired ones
                     if (exp == 0 || exp >= System.currentTimeMillis()) {
                         newCookie.setVersion(cookie.getVersion());
                         cookieManager.add(newCookie); // Has its own debug log; removes matching cookies
                     } else {
                         cookieManager.removeMatchingCookies(newCookie);
                         if (debugEnabled){
                             log.info("Dropping expired Cookie: "+newCookie.toString());
                         }
                     }
                 } catch (MalformedCookieException e) { // This means the cookie was wrong for the URL
                     log.warn("Not storing invalid cookie: <"+cookieHeader+"> for URL "+url+" ("+e.getLocalizedMessage()+")");
                 } catch (IllegalArgumentException e) {
                     log.warn(cookieHeader+e.getLocalizedMessage());
                 }
             }
     }
 
     @Override
     public String getCookieHeaderForURL(CollectionProperty cookiesCP, URL url,
             boolean allowVariableCookie) {
         List<org.apache.http.cookie.Cookie> c = 
                 getCookiesForUrl(cookiesCP, url, allowVariableCookie);
         
         boolean debugEnabled = log.isDebugEnabled();
         if (debugEnabled){
             log.debug("Found "+c.size()+" cookies for "+url.toExternalForm());
         }
         if (c.size() <= 0) {
             return null;
         }
         List<Header> lstHdr = cookieSpec.formatCookies(c);
         
         StringBuilder sbHdr = new StringBuilder();
         for (Header header : lstHdr) {
             sbHdr.append(header.getValue());
         }
 
         return sbHdr.toString();
     }
 
     /**
      * Get array of valid HttpClient cookies for the URL
      *
+     * @param cookiesCP property with all available cookies
      * @param url the target URL
+     * @param allowVariableCookie flag whether cookies may contain jmeter variables
      * @return array of HttpClient cookies
      *
      */
     List<org.apache.http.cookie.Cookie> getCookiesForUrl(
             CollectionProperty cookiesCP, URL url, boolean allowVariableCookie) {
         List<org.apache.http.cookie.Cookie> cookies = new ArrayList<org.apache.http.cookie.Cookie>();
 
         for (PropertyIterator iter = cookiesCP.iterator(); iter.hasNext();) {
             Cookie jmcookie = (Cookie) iter.next().getObjectValue();
             // Set to running version, to allow function evaluation for the cookie values (bug 28715)
             if (allowVariableCookie) {
                 jmcookie.setRunningVersion(true);
             }
             cookies.add(makeCookie(jmcookie));
             if (allowVariableCookie) {
                 jmcookie.setRunningVersion(false);
             }
         }
         String host = url.getHost();
         String protocol = url.getProtocol();
         int port = HTTPSamplerBase.getDefaultPort(protocol, url.getPort());
         String path = url.getPath();
         boolean secure = HTTPSamplerBase.isSecure(protocol);
 
         CookieOrigin cookieOrigin = new CookieOrigin(host, port, path, secure);
 
         List<org.apache.http.cookie.Cookie> cookiesValid = new ArrayList<org.apache.http.cookie.Cookie>();
         for (org.apache.http.cookie.Cookie cookie : cookies) {
             if (cookieSpec.match(cookie, cookieOrigin)) {
                 cookiesValid.add(cookie);
             }
         }
 
         return cookiesValid;
     }
     
     /**
      * Create an HttpClient cookie from a JMeter cookie
      */
     private org.apache.http.cookie.Cookie makeCookie(Cookie jmc) {
         long exp = jmc.getExpiresMillis();
         BasicClientCookie ret = new BasicClientCookie(jmc.getName(),
                 jmc.getValue());
 
         ret.setDomain(jmc.getDomain());
         ret.setPath(jmc.getPath());
         ret.setExpiryDate(exp > 0 ? new Date(exp) : null); // use null for no expiry
         ret.setSecure(jmc.getSecure());
         ret.setVersion(jmc.getVersion());
         return ret;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Proxy.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Proxy.java
index 5412b9275..e1ef187bb 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Proxy.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Proxy.java
@@ -1,618 +1,618 @@
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
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.net.Socket;
 import java.net.URL;
 import java.net.UnknownHostException;
 import java.nio.charset.IllegalCharsetNameException;
 import java.security.GeneralSecurityException;
 import java.security.KeyStore;
 import java.security.KeyStoreException;
 import java.util.HashMap;
 import java.util.Map;
 
 import javax.net.ssl.KeyManager;
 import javax.net.ssl.KeyManagerFactory;
 import javax.net.ssl.SSLContext;
 import javax.net.ssl.SSLSocket;
 import javax.net.ssl.SSLSocketFactory;
 
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.HTMLParseException;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Thread to handle one client request. Gets the request from the client and
  * passes it on to the server, then sends the response back to the client.
  * Information about the request and response is stored so it can be used in a
  * JMeter test plan.
  *
  */
 public class Proxy extends Thread {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final byte[] CRLF_BYTES = { 0x0d, 0x0a };
     private static final String CRLF_STRING = "\r\n";
 
     private static final String NEW_LINE = "\n"; // $NON-NLS-1$
 
     private static final String[] HEADERS_TO_REMOVE;
 
     // Allow list of headers to be overridden
     private static final String PROXY_HEADERS_REMOVE = "proxy.headers.remove"; // $NON-NLS-1$
 
     private static final String PROXY_HEADERS_REMOVE_DEFAULT = "If-Modified-Since,If-None-Match,Host"; // $NON-NLS-1$
 
     private static final String PROXY_HEADERS_REMOVE_SEPARATOR = ","; // $NON-NLS-1$
 
     private static final String KEYMANAGERFACTORY =
         JMeterUtils.getPropDefault("proxy.cert.factory", "SunX509"); // $NON-NLS-1$ $NON-NLS-2$
 
     private static final String SSLCONTEXT_PROTOCOL =
         JMeterUtils.getPropDefault("proxy.ssl.protocol", "TLS"); // $NON-NLS-1$ $NON-NLS-2$
 
     // HashMap to save ssl connection between Jmeter proxy and browser
     private static final HashMap<String, SSLSocketFactory> HOST2SSL_SOCK_FAC = new HashMap<String, SSLSocketFactory>();
 
     private static final SamplerCreatorFactory SAMPLERFACTORY = new SamplerCreatorFactory();
 
     static {
         String removeList = JMeterUtils.getPropDefault(PROXY_HEADERS_REMOVE,PROXY_HEADERS_REMOVE_DEFAULT);
         HEADERS_TO_REMOVE = JOrphanUtils.split(removeList,PROXY_HEADERS_REMOVE_SEPARATOR);
         log.info("Proxy will remove the headers: "+removeList);
     }
 
     // Use with SSL connection
     private OutputStream outStreamClient = null;
 
     /** Socket to client. */
     private Socket clientSocket = null;
 
     /** Target to receive the generated sampler. */
     private ProxyControl target;
 
     /** Whether or not to capture the HTTP headers. */
     private boolean captureHttpHeaders;
 
     /** Reference to Deamon's Map of url string to page character encoding of that page */
     private Map<String, String> pageEncodings;
     /** Reference to Deamon's Map of url string to character encoding for the form */
     private Map<String, String> formEncodings;
 
     private String port; // For identifying log messages
 
     private KeyStore keyStore; // keystore for SSL keys; fixed at config except for dynamic host key generation
 
     private String keyPassword;
 
     /**
      * Default constructor - used by newInstance call in Daemon
      */
     public Proxy() {
         port = "";
     }
 
     /**
      * Configure the Proxy.
      * Intended to be called directly after construction.
      * Should not be called after it has been passed to a new thread,
      * otherwise the variables may not be published correctly.
      *
      * @param _clientSocket
      *            the socket connection to the client
      * @param _target
      *            the ProxyControl which will receive the generated sampler
      * @param _pageEncodings
      *            reference to the Map of Deamon, with mappings from page urls to encoding used
-     * @param formEncodingsEncodings
+     * @param _formEncodings
      *            reference to the Map of Deamon, with mappings from form action urls to encoding used
      */
     void configure(Socket _clientSocket, ProxyControl _target, Map<String, String> _pageEncodings, Map<String, String> _formEncodings) {
         this.target = _target;
         this.clientSocket = _clientSocket;
         this.captureHttpHeaders = _target.getCaptureHttpHeaders();
         this.pageEncodings = _pageEncodings;
         this.formEncodings = _formEncodings;
         this.port = "["+ clientSocket.getPort() + "] ";
         this.keyStore = _target.getKeyStore();
         this.keyPassword = _target.getKeyPassword();
     }
 
     /**
      * Main processing method for the Proxy object
      */
     @Override
     public void run() {
         // Check which HTTPSampler class we should use
         String httpSamplerName = target.getSamplerTypeName();
 
         HttpRequestHdr request = new HttpRequestHdr(httpSamplerName);
         SampleResult result = null;
         HeaderManager headers = null;
         HTTPSamplerBase sampler = null;
         final boolean isDebug = log.isDebugEnabled();
         if (isDebug) {
             log.debug(port + "====================================================================");
         }
         try {
             // Now, parse initial request (in case it is a CONNECT request)
             byte[] ba = request.parse(new BufferedInputStream(clientSocket.getInputStream()));
             if (ba.length == 0) {
                 if (isDebug) {
                     log.debug(port + "Empty request, ignored");
                 }
                 throw new JMeterException(); // hack to skip processing
             }
             if (isDebug) {
                 log.debug(port + "Initial request: " + new String(ba));
             }
             outStreamClient = clientSocket.getOutputStream();
 
             if ((request.getMethod().startsWith(HTTPConstants.CONNECT)) && (outStreamClient != null)) {
                 if (isDebug) {
                     log.debug(port + "Method CONNECT => SSL");
                 }
                 // write a OK reponse to browser, to engage SSL exchange
                 outStreamClient.write(("HTTP/1.0 200 OK\r\n\r\n").getBytes(SampleResult.DEFAULT_HTTP_ENCODING)); // $NON-NLS-1$
                 outStreamClient.flush();
                // With ssl request, url is host:port (without https:// or path)
                 String[] param = request.getUrl().split(":");  // $NON-NLS-1$
                 if (param.length == 2) {
                     if (isDebug) {
                         log.debug(port + "Start to negotiate SSL connection, host: " + param[0]);
                     }
                     clientSocket = startSSL(clientSocket, param[0]);
                 } else {
                     // Should not happen, but if it does we don't want to continue 
                     log.error("In SSL request, unable to find host and port in CONNECT request: " + request.getUrl());
                     throw new JMeterException(); // hack to skip processing
                 }
                 // Re-parse (now it's the http request over SSL)
                 try {
                     ba = request.parse(new BufferedInputStream(clientSocket.getInputStream()));
                 } catch (IOException ioe) { // most likely this is because of a certificate error
                     final String url = param.length>0 ?  " for '"+ param[0] +"'" : "";
                     log.warn(port + "Problem with SSL certificate"+url+"? Ensure browser is set to accept the JMeter proxy cert: " + ioe.getMessage());
                     // won't work: writeErrorToClient(HttpReplyHdr.formInternalError());
                     result = generateErrorResult(result, request, ioe, "\n**ensure browser is set to accept the JMeter proxy certificate**"); // Generate result (if nec.) and populate it
                     throw new JMeterException(); // hack to skip processing
                 }
                 if (isDebug) {
                     log.debug(port + "Reparse: " + new String(ba));
                 }
                 if (ba.length == 0) {
                     log.warn(port + "Empty response to http over SSL. Probably waiting for user to authorize the certificate for " + request.getUrl());
                     throw new JMeterException(); // hack to skip processing
                 }
             }
 
             SamplerCreator samplerCreator = SAMPLERFACTORY.getSamplerCreator(request, pageEncodings, formEncodings);
             sampler = samplerCreator.createAndPopulateSampler(request, pageEncodings, formEncodings);
 
             /*
              * Create a Header Manager to ensure that the browsers headers are
              * captured and sent to the server
              */
             headers = request.getHeaderManager();
             sampler.setHeaderManager(headers);
 
             sampler.threadStarted(); // Needed for HTTPSampler2
             if (isDebug) {
                 log.debug(port + "Execute sample: " + sampler.getMethod() + " " + sampler.getUrl());
             }
             result = sampler.sample();
 
             // Find the page encoding and possibly encodings for forms in the page
             // in the response from the web server
             String pageEncoding = addPageEncoding(result);
             addFormEncodings(result, pageEncoding);
 
             writeToClient(result, new BufferedOutputStream(clientSocket.getOutputStream()));
             samplerCreator.postProcessSampler(sampler, result);
         } catch (JMeterException jme) {
             // ignored, already processed
         } catch (UnknownHostException uhe) {
             log.warn(port + "Server Not Found.", uhe);
             writeErrorToClient(HttpReplyHdr.formServerNotFound());
             result = generateErrorResult(result, request, uhe); // Generate result (if nec.) and populate it
         } catch (IllegalArgumentException e) {
             log.error(port + "Not implemented (probably used https)", e);
             writeErrorToClient(HttpReplyHdr.formNotImplemented("Probably used https instead of http. " +
                     "To record https requests, see " +
                     "<a href=\"http://jmeter.apache.org/usermanual/component_reference.html#HTTP(S)_Test_Script_Recorder\">HTTP(S) Test Script Recorder documentation</a>"));
             result = generateErrorResult(result, request, e); // Generate result (if nec.) and populate it
         } catch (Exception e) {
             log.error(port + "Exception when processing sample", e);
             writeErrorToClient(HttpReplyHdr.formInternalError());
             result = generateErrorResult(result, request, e); // Generate result (if nec.) and populate it
         } finally {
             if(sampler != null && isDebug) {
                 log.debug(port + "Will deliver sample " + sampler.getName());
             }
             /*
              * We don't want to store any cookies in the generated test plan
              */
             if (headers != null) {
                 headers.removeHeaderNamed(HTTPConstants.HEADER_COOKIE);// Always remove cookies
                 // See https://issues.apache.org/bugzilla/show_bug.cgi?id=25430
                 // HEADER_AUTHORIZATION won't be removed, it will be used
                 // for creating Authorization Manager
                 // Remove additional headers
                 for(String hdr : HEADERS_TO_REMOVE){
                     headers.removeHeaderNamed(hdr);
                 }
             }
             if(result != null) // deliverSampler allows sampler to be null, but result must not be null
             {
                 target.deliverSampler(sampler, new TestElement[] { captureHttpHeaders ? headers : null }, result);
             }
             try {
                 clientSocket.close();
             } catch (Exception e) {
                 log.error(port + "Failed to close client socket", e);
             }
             if(sampler != null) {
                 sampler.threadFinished(); // Needed for HTTPSampler2
             }
         }
     }
 
     /**
      * Get SSL connection from hashmap, creating it if necessary.
      *
      * @param host
      * @return a ssl socket factory, or null if keystore could not be opened/processed
      * @throws IOException
      */
     private SSLSocketFactory getSSLSocketFactory(String host) {
         if (keyStore == null) {
             log.error(port + "No keystore available, cannot record SSL");
             return null;
         }
         final String hashAlias;
         final String keyAlias;
         switch(ProxyControl.KEYSTORE_MODE) {
         case DYNAMIC_KEYSTORE:
             try {
                 keyStore = target.getKeyStore(); // pick up any recent changes from other threads
                 String alias = getDomainMatch(keyStore, host);
                 if (alias == null) {
                     hashAlias = host;
                     keyAlias = host;
                     keyStore = target.updateKeyStore(port, keyAlias);
                 } else if (alias.equals(host)) { // the host has a key already
                     hashAlias = host;
                     keyAlias = host;
                 } else { // the host matches a domain; use its key
                     hashAlias = alias;
                     keyAlias = alias;
                 }
             } catch (IOException e) {
                 log.error(port + "Problem with keystore", e);
                 return null;
             } catch (GeneralSecurityException e) {
                 log.error(port + "Problem with keystore", e);
                 return null;
             }
             break;
         case JMETER_KEYSTORE:
             hashAlias = keyAlias = ProxyControl.JMETER_SERVER_ALIAS;
             break;
         case USER_KEYSTORE:
             hashAlias = keyAlias = ProxyControl.CERT_ALIAS;
             break;
         default:
             throw new IllegalStateException("Impossible case: " + ProxyControl.KEYSTORE_MODE);
         }
         synchronized (HOST2SSL_SOCK_FAC) {
             final SSLSocketFactory sslSocketFactory = HOST2SSL_SOCK_FAC.get(hashAlias);
             if (sslSocketFactory != null) {
                 if (log.isDebugEnabled()) {
                     log.debug(port + "Good, already in map, host=" + host + " using alias " + hashAlias);
                 }
                 return sslSocketFactory;
             }
             try {
                 SSLContext sslcontext = SSLContext.getInstance(SSLCONTEXT_PROTOCOL);
                 sslcontext.init(getWrappedKeyManagers(keyAlias), null, null);
                 SSLSocketFactory sslFactory = sslcontext.getSocketFactory();
                 HOST2SSL_SOCK_FAC.put(hashAlias, sslFactory);
                 log.info(port + "KeyStore for SSL loaded OK and put host '" + host + "' in map with key ("+hashAlias+")");
                 return sslFactory;
             } catch (GeneralSecurityException e) {
                 log.error(port + "Problem with SSL certificate", e);
             } catch (IOException e) {
                 log.error(port + "Problem with keystore", e);
             }
             return null;
         }
     }
 
     /**
      * Get matching alias for a host from keyStore that may contain domain aliases.
      * Assumes domains must have at least 2 parts (apache.org);
      * does not check if TLD requires more (google.co.uk).
      * Note that DNS wildcards only apply to a single level, i.e.
      * podling.incubator.apache.org matches *.incubator.apache.org
      * but does not match *.apache.org
      *
      * @param keyStore the KeyStore to search
      * @param host the hostname to match
      * @return the keystore entry or {@code null} if no match found
      * @throws KeyStoreException 
      */
     private String getDomainMatch(KeyStore keyStore, String host) throws KeyStoreException {
         if (keyStore.containsAlias(host)) {
             return host;
         }
         String parts[] = host.split("\\."); // get the component parts
         // Assume domains must have at least 2 parts, e.g. apache.org
         // Replace the first part with "*" 
         StringBuilder sb = new StringBuilder("*"); // $NON-NLS-1$
         for(int j = 1; j < parts.length ; j++) { // Skip the first part
             sb.append('.');
             sb.append(parts[j]);
         }
         String alias = sb.toString();
         if (keyStore.containsAlias(alias)) {
             return alias;
         }
         return null;
     }
 
     /**
      * Return the key managers, wrapped to return a specific alias
      */
     private KeyManager[] getWrappedKeyManagers(final String keyAlias)
             throws GeneralSecurityException, IOException {
         if (!keyStore.containsAlias(keyAlias)) {
             throw new IOException("Keystore does not contain alias " + keyAlias);
         }
         KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEYMANAGERFACTORY);
         kmf.init(keyStore, keyPassword.toCharArray());
         final KeyManager[] keyManagers = kmf.getKeyManagers();
         // Check if alias is suitable here, rather than waiting for connection to fail
         final int keyManagerCount = keyManagers.length;
         final KeyManager[] wrappedKeyManagers = new KeyManager[keyManagerCount];
         for (int i =0; i < keyManagerCount; i++) {
             wrappedKeyManagers[i] = new ServerAliasKeyManager(keyManagers[i], keyAlias);
         }
         return wrappedKeyManagers;
     }
 
     /**
      * Negotiate a SSL connection.
      *
      * @param sock socket in
      * @param host
      * @return a new client socket over ssl
      * @throws Exception if negotiation failed
      */
     private Socket startSSL(Socket sock, String host) throws IOException {
         SSLSocketFactory sslFactory = getSSLSocketFactory(host);
         SSLSocket secureSocket;
         if (sslFactory != null) {
             try {
                 secureSocket = (SSLSocket) sslFactory.createSocket(sock,
                         sock.getInetAddress().getHostName(), sock.getPort(), true);
                 secureSocket.setUseClientMode(false);
                 if (log.isDebugEnabled()){
                     log.debug(port + "SSL transaction ok with cipher: " + secureSocket.getSession().getCipherSuite());
                 }
                 return secureSocket;
             } catch (IOException e) {
                 log.error(port + "Error in SSL socket negotiation: ", e);
                 throw e;
             }
         } else {
             log.warn(port + "Unable to negotiate SSL transaction, no keystore?");
             throw new IOException("Unable to negotiate SSL transaction, no keystore?");
         }
     }
 
     private SampleResult generateErrorResult(SampleResult result, HttpRequestHdr request, Exception e) {
         return generateErrorResult(result, request, e, "");
     }
 
     private static SampleResult generateErrorResult(SampleResult result, HttpRequestHdr request, Exception e, String msg) {
         if (result == null) {
             result = new SampleResult();
             ByteArrayOutputStream text = new ByteArrayOutputStream(200);
             e.printStackTrace(new PrintStream(text));
             result.setResponseData(text.toByteArray());
             result.setSamplerData(request.getFirstLine());
             result.setSampleLabel(request.getUrl());
         }
         result.setSuccessful(false);
         result.setResponseMessage(e.getMessage()+msg);
         return result;
     }
 
     /**
      * Write output to the output stream, then flush and close the stream.
      *
      * @param inBytes
      *            the bytes to write
      * @param out
      *            the output stream to write to
      * @param forcedHTTPS if we changed the protocol to https
      * @throws IOException
      *             if an IOException occurs while writing
      */
     private void writeToClient(SampleResult res, OutputStream out) throws IOException {
         try {
             String responseHeaders = messageResponseHeaders(res);
             out.write(responseHeaders.getBytes(SampleResult.DEFAULT_HTTP_ENCODING));
             out.write(CRLF_BYTES);
             out.write(res.getResponseData());
             out.flush();
             if (log.isDebugEnabled()) {
                 log.debug(port + "Done writing to client");
             }
         } catch (IOException e) {
             log.error("", e);
             throw e;
         } finally {
             try {
                 out.close();
             } catch (Exception ex) {
                 log.warn(port + "Error while closing socket", ex);
             }
         }
     }
 
     /**
      * In the event the content was gzipped and unpacked, the content-encoding
      * header must be removed and the content-length header should be corrected.
      *
      * The Transfer-Encoding header is also removed.
      * If the protocol was changed to HTTPS then change any Location header back to http
      * @param res - response
      *
      * @return updated headers to be sent to client
      */
     private String messageResponseHeaders(SampleResult res) {
         String headers = res.getResponseHeaders();
         String [] headerLines=headers.split(NEW_LINE, 0); // drop empty trailing content
         int contentLengthIndex=-1;
         boolean fixContentLength = false;
         for (int i=0;i<headerLines.length;i++){
             String line=headerLines[i];
             String[] parts=line.split(":\\s+",2); // $NON-NLS-1$
             if (parts.length==2){
                 if (HTTPConstants.TRANSFER_ENCODING.equalsIgnoreCase(parts[0])){
                     headerLines[i]=null; // We don't want this passed on to browser
                     continue;
                 }
                 if (HTTPConstants.HEADER_CONTENT_ENCODING.equalsIgnoreCase(parts[0])
                     &&
                     HTTPConstants.ENCODING_GZIP.equalsIgnoreCase(parts[1])
                 ){
                     headerLines[i]=null; // We don't want this passed on to browser
                     fixContentLength = true;
                     continue;
                 }
                 if (HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(parts[0])){
                     contentLengthIndex=i;
                     continue;
                 }
             }
         }
         if (fixContentLength && contentLengthIndex>=0){// Fix the content length
             headerLines[contentLengthIndex]=HTTPConstants.HEADER_CONTENT_LENGTH+": "+res.getResponseData().length;
         }
         StringBuilder sb = new StringBuilder(headers.length());
         for (int i=0;i<headerLines.length;i++){
             String line=headerLines[i];
             if (line != null){
                 sb.append(line).append(CRLF_STRING);
             }
         }
         return sb.toString();
     }
 
     /**
      * Write an error message to the client. The message should be the full HTTP
      * response.
      *
      * @param message
      *            the message to write
      */
     private void writeErrorToClient(String message) {
         try {
             OutputStream sockOut = clientSocket.getOutputStream();
             DataOutputStream out = new DataOutputStream(sockOut);
             out.writeBytes(message);
             out.flush();
         } catch (Exception e) {
             log.warn(port + "Exception while writing error", e);
         }
     }
 
     /**
      * Add the page encoding of the sample result to the Map with page encodings
      *
      * @param result the sample result to check
      * @return the page encoding found for the sample result, or null
      */
     private String addPageEncoding(SampleResult result) {
         String pageEncoding = null;
         try {
             pageEncoding = ConversionUtils.getEncodingFromContentType(result.getContentType());
         } catch(IllegalCharsetNameException ex) {
             log.warn("Unsupported charset detected in contentType:'"+result.getContentType()+"', will continue processing with default charset", ex);
         }
         if(pageEncoding != null) {
             String urlWithoutQuery = getUrlWithoutQuery(result.getURL());
             synchronized(pageEncodings) {
                 pageEncodings.put(urlWithoutQuery, pageEncoding);
             }
         }
         return pageEncoding;
     }
 
     /**
      * Add the form encodings for all forms in the sample result
      *
      * @param result the sample result to check
      * @param pageEncoding the encoding used for the sample result page
      */
     private void addFormEncodings(SampleResult result, String pageEncoding) {
         FormCharSetFinder finder = new FormCharSetFinder();
         if (!result.getContentType().startsWith("text/")){ // TODO perhaps make more specific than this?
             return; // no point parsing anything else, e.g. GIF ...
         }
         try {
             finder.addFormActionsAndCharSet(result.getResponseDataAsString(), formEncodings, pageEncoding);
         }
         catch (HTMLParseException parseException) {
             if (log.isDebugEnabled()) {
                 log.debug(port + "Unable to parse response, could not find any form character set encodings");
             }
         }
     }
 
     private String getUrlWithoutQuery(URL url) {
         String fullUrl = url.toString();
         String urlWithoutQuery = fullUrl;
         String query = url.getQuery();
         if(query != null) {
             // Get rid of the query and the ?
             urlWithoutQuery = urlWithoutQuery.substring(0, urlWithoutQuery.length() - query.length() - 1);
         }
         return urlWithoutQuery;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java
index b8dc13465..0fe364930 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java
@@ -1,1531 +1,1532 @@
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
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.InvocationTargetException;
 import java.net.MalformedURLException;
 import java.security.GeneralSecurityException;
 import java.security.KeyStore;
 import java.security.UnrecoverableKeyException;
 import java.security.cert.X509Certificate;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Date;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.prefs.Preferences;
 
 import org.apache.commons.codec.binary.Base64;
 import org.apache.commons.codec.digest.DigestUtils;
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.RandomStringUtils;
 import org.apache.commons.lang3.time.DateUtils;
 import org.apache.http.conn.ssl.AbstractVerifier;
 import org.apache.jmeter.assertions.ResponseAssertion;
 import org.apache.jmeter.assertions.gui.AssertionGui;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.control.GenericController;
 import org.apache.jmeter.control.TransactionController;
 import org.apache.jmeter.control.gui.LogicControllerGui;
 import org.apache.jmeter.control.gui.TransactionControllerGui;
 import org.apache.jmeter.engine.util.ValueReplacer;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.functions.InvalidVariableException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.control.RecordingController;
 import org.apache.jmeter.protocol.http.gui.AuthPanel;
 import org.apache.jmeter.protocol.http.gui.HeaderPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.AbstractThreadGroup;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.exec.KeyToolUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 
 //For unit tests, @see TestProxyControl
 
 /**
  * Class handles storing of generated samples, etc
  */
 public class ProxyControl extends GenericController {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final String ASSERTION_GUI = AssertionGui.class.getName();
 
 
     private static final String TRANSACTION_CONTROLLER_GUI = TransactionControllerGui.class.getName();
 
     private static final String LOGIC_CONTROLLER_GUI = LogicControllerGui.class.getName();
 
     private static final String HEADER_PANEL = HeaderPanel.class.getName();
 
     private static final String AUTH_PANEL = AuthPanel.class.getName();
 
     private static final String AUTH_MANAGER = AuthManager.class.getName();
 
     public static final int DEFAULT_PORT = 8080;
 
     // and as a string
     public static final String DEFAULT_PORT_S =
         Integer.toString(DEFAULT_PORT);// Used by GUI
 
     //+ JMX file attributes
     private static final String PORT = "ProxyControlGui.port"; // $NON-NLS-1$
 
     private static final String DOMAINS = "ProxyControlGui.domains"; // $NON-NLS-1$
 
     private static final String EXCLUDE_LIST = "ProxyControlGui.exclude_list"; // $NON-NLS-1$
 
     private static final String INCLUDE_LIST = "ProxyControlGui.include_list"; // $NON-NLS-1$
 
     private static final String CAPTURE_HTTP_HEADERS = "ProxyControlGui.capture_http_headers"; // $NON-NLS-1$
 
     private static final String ADD_ASSERTIONS = "ProxyControlGui.add_assertion"; // $NON-NLS-1$
 
     private static final String GROUPING_MODE = "ProxyControlGui.grouping_mode"; // $NON-NLS-1$
 
     private static final String SAMPLER_TYPE_NAME = "ProxyControlGui.sampler_type_name"; // $NON-NLS-1$
 
     private static final String SAMPLER_REDIRECT_AUTOMATICALLY = "ProxyControlGui.sampler_redirect_automatically"; // $NON-NLS-1$
 
     private static final String SAMPLER_FOLLOW_REDIRECTS = "ProxyControlGui.sampler_follow_redirects"; // $NON-NLS-1$
 
     private static final String USE_KEEPALIVE = "ProxyControlGui.use_keepalive"; // $NON-NLS-1$
 
     private static final String SAMPLER_DOWNLOAD_IMAGES = "ProxyControlGui.sampler_download_images"; // $NON-NLS-1$
 
     private static final String REGEX_MATCH = "ProxyControlGui.regex_match"; // $NON-NLS-1$
 
     private static final String CONTENT_TYPE_EXCLUDE = "ProxyControlGui.content_type_exclude"; // $NON-NLS-1$
 
     private static final String CONTENT_TYPE_INCLUDE = "ProxyControlGui.content_type_include"; // $NON-NLS-1$
 
     private static final String NOTIFY_CHILD_SAMPLER_LISTENERS_FILTERED = "ProxyControlGui.notify_child_sl_filtered"; // $NON-NLS-1$
 
     private static final String BASIC_AUTH = "Basic"; // $NON-NLS-1$
 
     private static final String DIGEST_AUTH = "Digest"; // $NON-NLS-1$
 
     //- JMX file attributes
 
     // Must agree with the order of entries in the drop-down
     // created in ProxyControlGui.createGroupingPanel()
     //private static final int GROUPING_NO_GROUPS = 0;
     private static final int GROUPING_ADD_SEPARATORS = 1;
     private static final int GROUPING_IN_SIMPLE_CONTROLLERS = 2;
     private static final int GROUPING_STORE_FIRST_ONLY = 3;
     private static final int GROUPING_IN_TRANSACTION_CONTROLLERS = 4;
 
     // Original numeric order (we now use strings)
     private static final String SAMPLER_TYPE_HTTP_SAMPLER_JAVA = "0";
     private static final String SAMPLER_TYPE_HTTP_SAMPLER_HC3_1 = "1";
     private static final String SAMPLER_TYPE_HTTP_SAMPLER_HC4 = "2";
 
     private static final long sampleGap =
         JMeterUtils.getPropDefault("proxy.pause", 5000); // $NON-NLS-1$
     // Detect if user has pressed a new link
 
     // for ssl connection
     private static final String KEYSTORE_TYPE =
         JMeterUtils.getPropDefault("proxy.cert.type", "JKS"); // $NON-NLS-1$ $NON-NLS-2$
 
     // Proxy configuration SSL
     private static final String CERT_DIRECTORY =
         JMeterUtils.getPropDefault("proxy.cert.directory", JMeterUtils.getJMeterBinDir()); // $NON-NLS-1$
 
     private static final String CERT_FILE_DEFAULT = "proxyserver.jks";// $NON-NLS-1$
 
     private static final String CERT_FILE =
         JMeterUtils.getPropDefault("proxy.cert.file", CERT_FILE_DEFAULT); // $NON-NLS-1$
 
     private static final File CERT_PATH = new File(CERT_DIRECTORY, CERT_FILE);
 
     private static final String CERT_PATH_ABS = CERT_PATH.getAbsolutePath();
 
     private static final String DEFAULT_PASSWORD = "password"; // $NON-NLS-1$
 
     // Keys for user preferences
     private static final String USER_PASSWORD_KEY = "proxy_cert_password";
 
     private static final Preferences PREFERENCES = Preferences.userNodeForPackage(ProxyControl.class);
     // Note: Windows user preferences are stored relative to: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
 
     // Whether to use dymanic key generation (if supported)
     private static final boolean USE_DYNAMIC_KEYS = JMeterUtils.getPropDefault("proxy.cert.dynamic_keys", true); // $NON-NLS-1$;
 
     // The alias to be used if dynamic host names are not possible
     static final String JMETER_SERVER_ALIAS = ":jmeter:"; // $NON-NLS-1$
 
     static final int CERT_VALIDITY = JMeterUtils.getPropDefault("proxy.cert.validity", 7); // $NON-NLS-1$
 
     // If this is defined, it is assumed to be the alias of a user-supplied certificate; overrides dynamic mode
     static final String CERT_ALIAS = JMeterUtils.getProperty("proxy.cert.alias"); // $NON-NLS-1$
 
     public static enum KeystoreMode {
         USER_KEYSTORE,   // user-provided keystore
         JMETER_KEYSTORE, // keystore generated by JMeter; single entry
         DYNAMIC_KEYSTORE, // keystore generated by JMeter; dynamic entries
         NONE             // cannot use keystore
     }
 
     static final KeystoreMode KEYSTORE_MODE;
 
     static {
         if (CERT_ALIAS != null) {
             KEYSTORE_MODE = KeystoreMode.USER_KEYSTORE;
             log.info("HTTP(S) Test Script Recorder will use the keystore '"+ CERT_PATH_ABS + "' with the alias: '" + CERT_ALIAS + "'");
         } else {
             if (!KeyToolUtils.haveKeytool()) {
                 KEYSTORE_MODE = KeystoreMode.NONE;
             } else if (KeyToolUtils.SUPPORTS_HOST_CERT && USE_DYNAMIC_KEYS) {
                 KEYSTORE_MODE = KeystoreMode.DYNAMIC_KEYSTORE;
                 log.info("HTTP(S) Test Script Recorder SSL Proxy will use keys that support embedded 3rd party resources in file " + CERT_PATH_ABS);
             } else {
                 KEYSTORE_MODE = KeystoreMode.JMETER_KEYSTORE;
                 log.warn("HTTP(S) Test Script Recorder SSL Proxy will use keys that may not work for embedded resources in file " + CERT_PATH_ABS);
             }
         }
     }
 
     // Whether to use the redirect disabling feature (can be switched off if it does not work)
     private static final boolean ATTEMPT_REDIRECT_DISABLING =
             JMeterUtils.getPropDefault("proxy.redirect.disabling", true); // $NON-NLS-1$
 
     // Although this field is mutable, it is only accessed within the synchronized method deliverSampler()
     private static String LAST_REDIRECT = null;
     /*
      * TODO this assumes that the redirected response will always immediately follow the original response.
      * This may not always be true.
      * Is there a better way to do this?
      */
 
     private transient Daemon server;
 
     private long lastTime = 0;// When was the last sample seen?
 
     private transient KeyStore keyStore;
 
     private volatile boolean addAssertions = false;
 
     private volatile int groupingMode = 0;
 
     private volatile boolean samplerRedirectAutomatically = false;
 
     private volatile boolean samplerFollowRedirects = false;
 
     private volatile boolean useKeepAlive = false;
 
     private volatile boolean samplerDownloadImages = false;
 
     private volatile boolean notifyChildSamplerListenersOfFilteredSamples = true;
 
     private volatile boolean regexMatch = false;// Should we match using regexes?
 
     /**
      * Tree node where the samples should be stored.
      * <p>
      * This property is not persistent.
      */
     private JMeterTreeNode target;
 
     private String storePassword;
 
     private String keyPassword;
 
     public ProxyControl() {
         setPort(DEFAULT_PORT);
         setExcludeList(new HashSet<String>());
         setIncludeList(new HashSet<String>());
         setCaptureHttpHeaders(true); // maintain original behaviour
     }
 
     public void setPort(int port) {
         this.setProperty(new IntegerProperty(PORT, port));
     }
 
     public void setPort(String port) {
         setProperty(PORT, port);
     }
 
     public void setSslDomains(String domains) {
         setProperty(DOMAINS, domains, "");
     }
 
     public String getSslDomains() {
         return getPropertyAsString(DOMAINS,"");
     }
 
     public void setCaptureHttpHeaders(boolean capture) {
         setProperty(new BooleanProperty(CAPTURE_HTTP_HEADERS, capture));
     }
 
     public void setGroupingMode(int grouping) {
         this.groupingMode = grouping;
         setProperty(new IntegerProperty(GROUPING_MODE, grouping));
     }
 
     public void setAssertions(boolean b) {
         addAssertions = b;
         setProperty(new BooleanProperty(ADD_ASSERTIONS, b));
     }
 
     @Deprecated
     public void setSamplerTypeName(int samplerTypeName) {
         setProperty(new IntegerProperty(SAMPLER_TYPE_NAME, samplerTypeName));
     }
 
     public void setSamplerTypeName(String samplerTypeName) {
         setProperty(new StringProperty(SAMPLER_TYPE_NAME, samplerTypeName));
     }
     public void setSamplerRedirectAutomatically(boolean b) {
         samplerRedirectAutomatically = b;
         setProperty(new BooleanProperty(SAMPLER_REDIRECT_AUTOMATICALLY, b));
     }
 
     public void setSamplerFollowRedirects(boolean b) {
         samplerFollowRedirects = b;
         setProperty(new BooleanProperty(SAMPLER_FOLLOW_REDIRECTS, b));
     }
 
     /**
      * @param b flag whether keep alive should be used
      */
     public void setUseKeepAlive(boolean b) {
         useKeepAlive = b;
         setProperty(new BooleanProperty(USE_KEEPALIVE, b));
     }
 
     public void setSamplerDownloadImages(boolean b) {
         samplerDownloadImages = b;
         setProperty(new BooleanProperty(SAMPLER_DOWNLOAD_IMAGES, b));
     }
 
     public void setNotifyChildSamplerListenerOfFilteredSamplers(boolean b) {
         notifyChildSamplerListenersOfFilteredSamples = b;
         setProperty(new BooleanProperty(NOTIFY_CHILD_SAMPLER_LISTENERS_FILTERED, b));
     }
 
     public void setIncludeList(Collection<String> list) {
         setProperty(new CollectionProperty(INCLUDE_LIST, new HashSet<String>(list)));
     }
 
     public void setExcludeList(Collection<String> list) {
         setProperty(new CollectionProperty(EXCLUDE_LIST, new HashSet<String>(list)));
     }
 
     /**
      * @param b flag whether regex matching should be used
      */
     public void setRegexMatch(boolean b) {
         regexMatch = b;
         setProperty(new BooleanProperty(REGEX_MATCH, b));
     }
 
     public void setContentTypeExclude(String contentTypeExclude) {
         setProperty(new StringProperty(CONTENT_TYPE_EXCLUDE, contentTypeExclude));
     }
 
     public void setContentTypeInclude(String contentTypeInclude) {
         setProperty(new StringProperty(CONTENT_TYPE_INCLUDE, contentTypeInclude));
     }
 
     public boolean getAssertions() {
         return getPropertyAsBoolean(ADD_ASSERTIONS);
     }
 
     public int getGroupingMode() {
         return getPropertyAsInt(GROUPING_MODE);
     }
 
     public int getPort() {
         return getPropertyAsInt(PORT);
     }
 
     public String getPortString() {
         return getPropertyAsString(PORT);
     }
 
     public int getDefaultPort() {
         return DEFAULT_PORT;
     }
 
     public boolean getCaptureHttpHeaders() {
         return getPropertyAsBoolean(CAPTURE_HTTP_HEADERS);
     }
 
     public String getSamplerTypeName() {
         // Convert the old numeric types - just in case someone wants to reload the workbench
         String type = getPropertyAsString(SAMPLER_TYPE_NAME);
         if (SAMPLER_TYPE_HTTP_SAMPLER_JAVA.equals(type)){
             type = HTTPSamplerFactory.IMPL_JAVA;
         } else if (SAMPLER_TYPE_HTTP_SAMPLER_HC3_1.equals(type)){
             type = HTTPSamplerFactory.IMPL_HTTP_CLIENT3_1;
         } else if (SAMPLER_TYPE_HTTP_SAMPLER_HC4.equals(type)){
             type = HTTPSamplerFactory.IMPL_HTTP_CLIENT4;
         }
         return type;
     }
 
     public boolean getSamplerRedirectAutomatically() {
         return getPropertyAsBoolean(SAMPLER_REDIRECT_AUTOMATICALLY, false);
     }
 
     public boolean getSamplerFollowRedirects() {
         return getPropertyAsBoolean(SAMPLER_FOLLOW_REDIRECTS, true);
     }
 
     public boolean getUseKeepalive() {
         return getPropertyAsBoolean(USE_KEEPALIVE, true);
     }
 
     public boolean getSamplerDownloadImages() {
         return getPropertyAsBoolean(SAMPLER_DOWNLOAD_IMAGES, false);
     }
 
     public boolean getNotifyChildSamplerListenerOfFilteredSamplers() {
         return getPropertyAsBoolean(NOTIFY_CHILD_SAMPLER_LISTENERS_FILTERED, true);
     }
 
     public boolean getRegexMatch() {
         return getPropertyAsBoolean(REGEX_MATCH, false);
     }
 
     public String getContentTypeExclude() {
         return getPropertyAsString(CONTENT_TYPE_EXCLUDE);
     }
 
     public String getContentTypeInclude() {
         return getPropertyAsString(CONTENT_TYPE_INCLUDE);
     }
 
     public void addConfigElement(ConfigElement config) {
         // NOOP
     }
 
     public void startProxy() throws IOException {
         try {
             initKeyStore(); // TODO display warning dialog as this can take some time
         } catch (GeneralSecurityException e) {
             log.error("Could not initialise key store", e);
             throw new IOException("Could not create keystore", e);
         } catch (IOException e) { // make sure we log the error
             log.error("Could not initialise key store", e);
             throw e;
         }
         notifyTestListenersOfStart();
         try {
             server = new Daemon(getPort(), this);
             server.start();
             GuiPackage.getInstance().register(server);
         } catch (IOException e) {
             log.error("Could not create Proxy daemon", e);
             throw e;
         }
     }
 
     public void addExcludedPattern(String pattern) {
         getExcludePatterns().addItem(pattern);
     }
 
     public CollectionProperty getExcludePatterns() {
         return (CollectionProperty) getProperty(EXCLUDE_LIST);
     }
 
     public void addIncludedPattern(String pattern) {
         getIncludePatterns().addItem(pattern);
     }
 
     public CollectionProperty getIncludePatterns() {
         return (CollectionProperty) getProperty(INCLUDE_LIST);
     }
 
     public void clearExcludedPatterns() {
         getExcludePatterns().clear();
     }
 
     public void clearIncludedPatterns() {
         getIncludePatterns().clear();
     }
 
     /**
      * @return the target controller node
      */
     public JMeterTreeNode getTarget() {
         return target;
     }
 
     /**
      * Sets the target node where the samples generated by the proxy have to be
      * stored.
      * 
      * @param target target node to store generated samples
      */
     public void setTarget(JMeterTreeNode target) {
         this.target = target;
     }
 
     /**
      * Receives the recorded sampler from the proxy server for placing in the
      * test tree; this is skipped if the sampler is null (e.g. for recording SSL errors)
      * Always sends the result to any registered sample listeners.
      *
      * @param sampler the sampler, may be null
      * @param subConfigs the configuration elements to be added (e.g. header namager)
      * @param result the sample result, not null
      * TODO param serverResponse to be added to allow saving of the
      * server's response while recording.
      */
     public synchronized void deliverSampler(final HTTPSamplerBase sampler, final TestElement[] subConfigs, final SampleResult result) {
         boolean notifySampleListeners = true;
         if (sampler != null) {
             if (ATTEMPT_REDIRECT_DISABLING && (samplerRedirectAutomatically || samplerFollowRedirects)) {
                 if (result instanceof HTTPSampleResult) {
                     final HTTPSampleResult httpSampleResult = (HTTPSampleResult) result;
                     final String urlAsString = httpSampleResult.getUrlAsString();
                     if (urlAsString.equals(LAST_REDIRECT)) { // the url matches the last redirect
                         sampler.setEnabled(false);
                         sampler.setComment("Detected a redirect from the previous sample");
                     } else { // this is not the result of a redirect
                         LAST_REDIRECT = null; // so break the chain
                     }
                     if (httpSampleResult.isRedirect()) { // Save Location so resulting sample can be disabled
                         if (LAST_REDIRECT == null) {
                             sampler.setComment("Detected the start of a redirect chain");
                         }
                         LAST_REDIRECT = httpSampleResult.getRedirectLocation();
                     } else {
                         LAST_REDIRECT = null;
                     }
                 }
             }
             if (filterContentType(result) && filterUrl(sampler)) {
                 JMeterTreeNode myTarget = findTargetControllerNode();
                 @SuppressWarnings("unchecked") // OK, because find only returns correct element types
                 Collection<ConfigTestElement> defaultConfigurations = (Collection<ConfigTestElement>) findApplicableElements(myTarget, ConfigTestElement.class, false);
                 @SuppressWarnings("unchecked") // OK, because find only returns correct element types
                 Collection<Arguments> userDefinedVariables = (Collection<Arguments>) findApplicableElements(myTarget, Arguments.class, true);
 
                 removeValuesFromSampler(sampler, defaultConfigurations);
                 replaceValues(sampler, subConfigs, userDefinedVariables);
                 sampler.setAutoRedirects(samplerRedirectAutomatically);
                 sampler.setFollowRedirects(samplerFollowRedirects);
                 sampler.setUseKeepAlive(useKeepAlive);
                 sampler.setImageParser(samplerDownloadImages);
 
                 Authorization authorization = createAuthorization(subConfigs, sampler);
                 if (authorization != null) {
                     setAuthorization(authorization, myTarget);
                 }
                 placeSampler(sampler, subConfigs, myTarget);
             } else {
                 if(log.isDebugEnabled()) {
                     log.debug("Sample excluded based on url or content-type: " + result.getUrlAsString() + " - " + result.getContentType());
                 }
                 notifySampleListeners = notifyChildSamplerListenersOfFilteredSamples;
                 result.setSampleLabel("["+result.getSampleLabel()+"]");
             }
         }
         if(notifySampleListeners) {
             // SampleEvent is not passed JMeterVariables, because they don't make sense for Proxy Recording
             notifySampleListeners(new SampleEvent(result, "WorkBench")); // TODO - is this the correct threadgroup name?
         } else {
             log.debug("Sample not delivered to Child Sampler Listener based on url or content-type: " + result.getUrlAsString() + " - " + result.getContentType());
         }
     }
 
     /**
      * Detect Header manager in subConfigs,
      * Find(if any) Authorization header
      * Construct Authentication object
      * Removes Authorization if present 
      *
      * @param subConfigs {@link TestElement}[]
      * @param sampler {@link HTTPSamplerBase}
      * @return {@link Authorization}
      */
     private Authorization createAuthorization(final TestElement[] subConfigs, HTTPSamplerBase sampler) {
         Header authHeader = null;
         Authorization authorization = null;
         // Iterate over subconfig elements searching for HeaderManager
         for (TestElement te : subConfigs) {
             if (te instanceof HeaderManager) {
                 List<TestElementProperty> headers = (ArrayList<TestElementProperty>) ((HeaderManager) te).getHeaders().getObjectValue();
                 for (Iterator<?> iterator = headers.iterator(); iterator.hasNext();) {
                     TestElementProperty tep = (TestElementProperty) iterator
                             .next();
                     if (tep.getName().equals(HTTPConstants.HEADER_AUTHORIZATION)) {
                         //Construct Authorization object from HEADER_AUTHORIZATION
                         authHeader = (Header) tep.getObjectValue();
                         String[] authHeaderContent = authHeader.getValue().split(" ");//$NON-NLS-1$
                         String authType = null;
                         String authCredentialsBase64 = null;
                         if(authHeaderContent.length>=2) {
                             authType = authHeaderContent[0];
                             authCredentialsBase64 = authHeaderContent[1];
                             authorization=new Authorization();
                             try {
                                 authorization.setURL(sampler.getUrl().toExternalForm());
                             } catch (MalformedURLException e) {
                                 log.error("Error filling url on authorization, message:"+e.getMessage(), e);
                                 authorization.setURL("${AUTH_BASE_URL}");//$NON-NLS-1$
                             }
                             // if HEADER_AUTHORIZATION contains "Basic"
                             // then set Mechanism.BASIC_DIGEST, otherwise Mechanism.KERBEROS
                             authorization.setMechanism(
                                     authType.equals(BASIC_AUTH)||authType.equals(DIGEST_AUTH)?
                                     AuthManager.Mechanism.BASIC_DIGEST:
                                     AuthManager.Mechanism.KERBEROS);
                             if(BASIC_AUTH.equals(authType)) {
                                 String authCred= new String(Base64.decodeBase64(authCredentialsBase64));
                                 String[] loginPassword = authCred.split(":"); //$NON-NLS-1$
                                 authorization.setUser(loginPassword[0]);
                                 authorization.setPass(loginPassword[1]);
                             } else {
                                 // Digest or Kerberos
                                 authorization.setUser("${AUTH_LOGIN}");//$NON-NLS-1$
                                 authorization.setPass("${AUTH_PASSWORD}");//$NON-NLS-1$
                                 
                             }
                         }
                         // remove HEADER_AUTHORIZATION from HeaderManager 
                         // because it's useless after creating Authorization object
                         iterator.remove();
                     }
                 }
             }
         }
         return authorization;
     }
 
     public void stopProxy() {
         if (server != null) {
             server.stopServer();
             GuiPackage.getInstance().unregister(server);
             try {
                 server.join(1000); // wait for server to stop
             } catch (InterruptedException e) {
                 //NOOP
             }
             notifyTestListenersOfEnd();
             server = null;
         }
     }
 
     public String[] getCertificateDetails() {
         if (isDynamicMode()) {
             try {
                 X509Certificate caCert = (X509Certificate) keyStore.getCertificate(KeyToolUtils.getRootCAalias());
                 if (caCert == null) {
                     return new String[]{"Could not find certificate"};
                 }
                 return new String[]
                         {
                         caCert.getSubjectX500Principal().toString(),
                         "Fingerprint(SHA1): " + JOrphanUtils.baToHexString(DigestUtils.sha1(caCert.getEncoded()), ' '),
                         "Created: "+ caCert.getNotBefore().toString()
                         };
             } catch (GeneralSecurityException e) {
                 log.error("Problem reading root CA from keystore", e);
                 return new String[]{"Problem with root certificate", e.getMessage()};
             }
         }
         return null; // should not happen
     }
     // Package protected to allow test case access
     boolean filterUrl(HTTPSamplerBase sampler) {
         String domain = sampler.getDomain();
         if (domain == null || domain.length() == 0) {
             return false;
         }
 
         String url = generateMatchUrl(sampler);
         CollectionProperty includePatterns = getIncludePatterns();
         if (includePatterns.size() > 0) {
             if (!matchesPatterns(url, includePatterns)) {
                 return false;
             }
         }
 
         CollectionProperty excludePatterns = getExcludePatterns();
         if (excludePatterns.size() > 0) {
             if (matchesPatterns(url, excludePatterns)) {
                 return false;
             }
         }
 
         return true;
     }
 
     // Package protected to allow test case access
     /**
      * Filter the response based on the content type.
      * If no include nor exclude filter is specified, the result will be included
      *
-     * @param result the sample result to check, true means result will be kept
+     * @param result the sample result to check
+     * @return <code>true</code> means result will be kept
      */
     boolean filterContentType(SampleResult result) {
         String includeExp = getContentTypeInclude();
         String excludeExp = getContentTypeExclude();
         // If no expressions are specified, we let the sample pass
         if((includeExp == null || includeExp.length() == 0) &&
                 (excludeExp == null || excludeExp.length() == 0)
                 )
         {
             return true;
         }
 
         // Check that we have a content type
         String sampleContentType = result.getContentType();
         if(sampleContentType == null || sampleContentType.length() == 0) {
             if(log.isDebugEnabled()) {
                 log.debug("No Content-type found for : " + result.getUrlAsString());
             }
 
             return true;
         }
 
         if(log.isDebugEnabled()) {
             log.debug("Content-type to filter : " + sampleContentType);
         }
 
         // Check if the include pattern is matched
         boolean matched = testPattern(includeExp, sampleContentType, true);
         if(!matched) {
             return false;
         }
 
         // Check if the exclude pattern is matched
         matched = testPattern(excludeExp, sampleContentType, false);
         if(!matched) {
             return false;
         }
 
         return true;
     }
 
     /**
      * Returns true if matching pattern was different from expectedToMatch
      * @param expression Expression to match
      * @param sampleContentType
      * @return boolean true if Matching expression
      */
     private final boolean testPattern(String expression, String sampleContentType, boolean expectedToMatch) {
         if(expression != null && expression.length() > 0) {
             if(log.isDebugEnabled()) {
                 log.debug("Testing Expression : " + expression + " on sampleContentType:"+sampleContentType+", expected to match:"+expectedToMatch);
             }
 
             Pattern pattern = null;
             try {
                 pattern = JMeterUtils.getPatternCache().getPattern(expression, Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.SINGLELINE_MASK);
                 if(JMeterUtils.getMatcher().contains(sampleContentType, pattern) != expectedToMatch) {
                     return false;
                 }
             } catch (MalformedCachePatternException e) {
                 log.warn("Skipped invalid content pattern: " + expression, e);
             }
         }
         return true;
     }
 
     /**
      * Find if there is any AuthManager in JMeterTreeModel
      * If there is no one, create and add it to tree
      * Add authorization object to AuthManager
      * @param authorization {@link Authorization}
      * @param target {@link JMeterTreeNode}
      */
     private void setAuthorization(Authorization authorization, JMeterTreeNode target) {
         JMeterTreeModel jmeterTreeModel = GuiPackage.getInstance().getTreeModel();
         List<JMeterTreeNode> authManagerNodes = jmeterTreeModel.getNodesOfType(AuthManager.class);
         if (authManagerNodes.size() == 0) {
             try {
                 log.debug("Creating HTTP Authentication manager for authorization:"+authorization);
                 AuthManager authManager = newAuthorizationManager(authorization);
                 jmeterTreeModel.addComponent(authManager, target);
             } catch (IllegalUserActionException e) {
                 log.error("Failed to add Authorization Manager to target node:" + target.getName(), e);
             }
         } else{
             AuthManager authManager=(AuthManager)authManagerNodes.get(0).getTestElement();
             authManager.addAuth(authorization);
         }
     }
 
     /**
      * Helper method to add a Response Assertion
      * Called from AWT Event thread
      */
     private void addAssertion(JMeterTreeModel model, JMeterTreeNode node) throws IllegalUserActionException {
         ResponseAssertion ra = new ResponseAssertion();
         ra.setProperty(TestElement.GUI_CLASS, ASSERTION_GUI);
         ra.setName(JMeterUtils.getResString("assertion_title")); // $NON-NLS-1$
         ra.setTestFieldResponseData();
         model.addComponent(ra, node);
     }
 
     /**
      * Construct AuthManager
      * @param authorization
      * @return
      * @throws IllegalUserActionException
      */
     private AuthManager newAuthorizationManager(Authorization authorization) throws IllegalUserActionException {
         AuthManager authManager = new AuthManager();
         authManager.setProperty(TestElement.GUI_CLASS, AUTH_PANEL);
         authManager.setProperty(TestElement.TEST_CLASS, AUTH_MANAGER);
         authManager.setName("HTTP Authorization Manager");
         authManager.addAuth(authorization);
         return authManager;
     }
 
     /**
      * Helper method to add a Divider
      * Called from Application Thread that needs to update GUI (JMeterTreeModel)
      */
     private void addDivider(final JMeterTreeModel model, final JMeterTreeNode node) {
         final GenericController sc = new GenericController();
         sc.setProperty(TestElement.GUI_CLASS, LOGIC_CONTROLLER_GUI);
         sc.setName("-------------------"); // $NON-NLS-1$
         JMeterUtils.runSafe(new Runnable() {
             @Override
             public void run() {
                 try {
                     model.addComponent(sc, node);
                 } catch (IllegalUserActionException e) {
                     log.error("Program error", e);
                     throw new Error(e);
                 }
             }
         });
     }
 
     /**
      * Helper method to add a Simple Controller to contain the samplers.
      * Called from Application Thread that needs to update GUI (JMeterTreeModel)
      * @param model
      *            Test component tree model
      * @param node
      *            Node in the tree where we will add the Controller
      * @param name
      *            A name for the Controller
      * @throws InvocationTargetException
      * @throws InterruptedException
      */
     private void addSimpleController(final JMeterTreeModel model, final JMeterTreeNode node, String name)
             throws InterruptedException, InvocationTargetException {
         final GenericController sc = new GenericController();
         sc.setProperty(TestElement.GUI_CLASS, LOGIC_CONTROLLER_GUI);
         sc.setName(name);
         JMeterUtils.runSafe(new Runnable() {
             @Override
             public void run() {
                 try {
                     model.addComponent(sc, node);
                 } catch (IllegalUserActionException e) {
                      log.error("Program error", e);
                      throw new Error(e);
                 }
             }
         });
     }
 
     /**
      * Helper method to add a Transaction Controller to contain the samplers.
      * Called from Application Thread that needs to update GUI (JMeterTreeModel)
      * @param model
      *            Test component tree model
      * @param node
      *            Node in the tree where we will add the Controller
      * @param name
      *            A name for the Controller
      * @throws InvocationTargetException
      * @throws InterruptedException
      */
     private void addTransactionController(final JMeterTreeModel model, final JMeterTreeNode node, String name)
             throws InterruptedException, InvocationTargetException {
         final TransactionController sc = new TransactionController();
         sc.setIncludeTimers(false);
         sc.setProperty(TestElement.GUI_CLASS, TRANSACTION_CONTROLLER_GUI);
         sc.setName(name);
         JMeterUtils.runSafe(new Runnable() {
             @Override
             public void run() {
                  try {
                     model.addComponent(sc, node);
                 } catch (IllegalUserActionException e) {
                     log.error("Program error", e);
                     throw new Error(e);
                 }
             }
         });
     }
     /**
      * Helpler method to replicate any timers found within the Proxy Controller
      * into the provided sampler, while replacing any occurences of string _T_
      * in the timer's configuration with the provided deltaT.
      * Called from AWT Event thread
      * @param model
      *            Test component tree model
      * @param node
      *            Sampler node in where we will add the timers
      * @param deltaT
      *            Time interval from the previous request
      */
     private void addTimers(JMeterTreeModel model, JMeterTreeNode node, long deltaT) {
         TestPlan variables = new TestPlan();
         variables.addParameter("T", Long.toString(deltaT)); // $NON-NLS-1$
         ValueReplacer replacer = new ValueReplacer(variables);
         JMeterTreeNode mySelf = model.getNodeOf(this);
         Enumeration<JMeterTreeNode> children = mySelf.children();
         while (children.hasMoreElements()) {
             JMeterTreeNode templateNode = children.nextElement();
             if (templateNode.isEnabled()) {
                 TestElement template = templateNode.getTestElement();
                 if (template instanceof Timer) {
                     TestElement timer = (TestElement) template.clone();
                     try {
                         replacer.undoReverseReplace(timer);
                         model.addComponent(timer, node);
                     } catch (InvalidVariableException e) {
                         // Not 100% sure, but I believe this can't happen, so
                         // I'll log and throw an error:
                         log.error("Program error", e);
                         throw new Error(e);
                     } catch (IllegalUserActionException e) {
                         // Not 100% sure, but I believe this can't happen, so
                         // I'll log and throw an error:
                         log.error("Program error", e);
                         throw new Error(e);
                     }
                 }
             }
         }
     }
 
     /**
      * Finds the first enabled node of a given type in the tree.
      *
      * @param type
      *            class of the node to be found
      *
      * @return the first node of the given type in the test component tree, or
      *         <code>null</code> if none was found.
      */
     private JMeterTreeNode findFirstNodeOfType(Class<?> type) {
         JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
         List<JMeterTreeNode> nodes = treeModel.getNodesOfType(type);
         for (JMeterTreeNode node : nodes) {
             if (node.isEnabled()) {
                 return node;
             }
         }
         return null;
     }
 
     /**
      * Finds the controller where samplers have to be stored, that is:
      * <ul>
      * <li>The controller specified by the <code>target</code> property.
      * <li>If none was specified, the first RecordingController in the tree.
      * <li>If none is found, the first AbstractThreadGroup in the tree.
      * <li>If none is found, the Workspace.
      * </ul>
      *
      * @return the tree node for the controller where the proxy must store the
      *         generated samplers.
      */
     public JMeterTreeNode findTargetControllerNode() {
         JMeterTreeNode myTarget = getTarget();
         if (myTarget != null) {
             return myTarget;
         }
         myTarget = findFirstNodeOfType(RecordingController.class);
         if (myTarget != null) {
             return myTarget;
         }
         myTarget = findFirstNodeOfType(AbstractThreadGroup.class);
         if (myTarget != null) {
             return myTarget;
         }
         myTarget = findFirstNodeOfType(WorkBench.class);
         if (myTarget != null) {
             return myTarget;
         }
         log.error("Program error: test script recording target not found.");
         return null;
     }
 
     /**
      * Finds all configuration objects of the given class applicable to the
      * recorded samplers, that is:
      * <ul>
      * <li>All such elements directly within the HTTP(S) Test Script Recorder (these have
      * the highest priority).
      * <li>All such elements directly within the target controller (higher
      * priority) or directly within any containing controller (lower priority),
      * including the Test Plan itself (lowest priority).
      * </ul>
      *
      * @param myTarget
      *            tree node for the recording target controller.
      * @param myClass
      *            Class of the elements to be found.
      * @param ascending
      *            true if returned elements should be ordered in ascending
      *            priority, false if they should be in descending priority.
      *
      * @return a collection of applicable objects of the given class.
      */
     // TODO - could be converted to generic class?
     private Collection<?> findApplicableElements(JMeterTreeNode myTarget, Class<? extends TestElement> myClass, boolean ascending) {
         JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
         LinkedList<TestElement> elements = new LinkedList<TestElement>();
 
         // Look for elements directly within the HTTP proxy:
         Enumeration<?> kids = treeModel.getNodeOf(this).children();
         while (kids.hasMoreElements()) {
             JMeterTreeNode subNode = (JMeterTreeNode) kids.nextElement();
             if (subNode.isEnabled()) {
                 TestElement element = (TestElement) subNode.getUserObject();
                 if (myClass.isInstance(element)) {
                     if (ascending) {
                         elements.addFirst(element);
                     } else {
                         elements.add(element);
                     }
                 }
             }
         }
 
         // Look for arguments elements in the target controller or higher up:
         for (JMeterTreeNode controller = myTarget; controller != null; controller = (JMeterTreeNode) controller
                 .getParent()) {
             kids = controller.children();
             while (kids.hasMoreElements()) {
                 JMeterTreeNode subNode = (JMeterTreeNode) kids.nextElement();
                 if (subNode.isEnabled()) {
                     TestElement element = (TestElement) subNode.getUserObject();
                     if (myClass.isInstance(element)) {
                         log.debug("Applicable: " + element.getName());
                         if (ascending) {
                             elements.addFirst(element);
                         } else {
                             elements.add(element);
                         }
                     }
 
                     // Special case for the TestPlan's Arguments sub-element:
                     if (element instanceof TestPlan) {
                         TestPlan tp = (TestPlan) element;
                         Arguments args = tp.getArguments();
                         if (myClass.isInstance(args)) {
                             if (ascending) {
                                 elements.addFirst(args);
                             } else {
                                 elements.add(args);
                             }
                         }
                     }
                 }
             }
         }
 
         return elements;
     }
 
     private void placeSampler(final HTTPSamplerBase sampler, final TestElement[] subConfigs,
             JMeterTreeNode myTarget) {
         try {
             final JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
 
             boolean firstInBatch = false;
             long now = System.currentTimeMillis();
             long deltaT = now - lastTime;
             int cachedGroupingMode = groupingMode;
             if (deltaT > sampleGap) {
                 if (!myTarget.isLeaf() && cachedGroupingMode == GROUPING_ADD_SEPARATORS) {
                     addDivider(treeModel, myTarget);
                 }
                 if (cachedGroupingMode == GROUPING_IN_SIMPLE_CONTROLLERS) {
                     addSimpleController(treeModel, myTarget, sampler.getName());
                 }
                 if (cachedGroupingMode == GROUPING_IN_TRANSACTION_CONTROLLERS) {
                     addTransactionController(treeModel, myTarget, sampler.getName());
                 }
                 firstInBatch = true;// Remember this was first in its batch
             }
             if (lastTime == 0) {
                 deltaT = 0; // Decent value for timers
             }
             lastTime = now;
 
             if (cachedGroupingMode == GROUPING_STORE_FIRST_ONLY) {
                 if (!firstInBatch) {
                     return; // Huh! don't store this one!
                 }
 
                 // If we're not storing subsequent samplers, we'll need the
                 // first sampler to do all the work...:
                 sampler.setFollowRedirects(true);
                 sampler.setImageParser(true);
             }
 
             if (cachedGroupingMode == GROUPING_IN_SIMPLE_CONTROLLERS ||
                     cachedGroupingMode == GROUPING_IN_TRANSACTION_CONTROLLERS) {
                 // Find the last controller in the target to store the
                 // sampler there:
                 for (int i = myTarget.getChildCount() - 1; i >= 0; i--) {
                     JMeterTreeNode c = (JMeterTreeNode) myTarget.getChildAt(i);
                     if (c.getTestElement() instanceof GenericController) {
                         myTarget = c;
                         break;
                     }
                 }
             }
             final long deltaTFinal = deltaT;
             final boolean firstInBatchFinal = firstInBatch;
             final JMeterTreeNode myTargetFinal = myTarget;
             JMeterUtils.runSafe(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         final JMeterTreeNode newNode = treeModel.addComponent(sampler, myTargetFinal);
                         if (firstInBatchFinal) {
                             if (addAssertions) {
                                 addAssertion(treeModel, newNode);
                             }
                             addTimers(treeModel, newNode, deltaTFinal);
                         }
 
                         for (int i = 0; subConfigs != null && i < subConfigs.length; i++) {
                             if (subConfigs[i] instanceof HeaderManager) {
                                 final TestElement headerManager = subConfigs[i];
                                 headerManager.setProperty(TestElement.GUI_CLASS, HEADER_PANEL);
                                 treeModel.addComponent(headerManager, newNode);
                             }
                         }
                     } catch (IllegalUserActionException e) {
                         JMeterUtils.reportErrorToUser(e.getMessage());
                     }
                 }
             });
         } catch (Exception e) {
             JMeterUtils.reportErrorToUser(e.getMessage());
         }
     }
 
     /**
      * Remove from the sampler all values which match the one provided by the
      * first configuration in the given collection which provides a value for
      * that property.
      *
      * @param sampler
      *            Sampler to remove values from.
      * @param configurations
      *            ConfigTestElements in descending priority.
      */
     private void removeValuesFromSampler(HTTPSamplerBase sampler, Collection<ConfigTestElement> configurations) {
         for (PropertyIterator props = sampler.propertyIterator(); props.hasNext();) {
             JMeterProperty prop = props.next();
             String name = prop.getName();
             String value = prop.getStringValue();
 
             // There's a few properties which are excluded from this processing:
             if (name.equals(TestElement.ENABLED) || name.equals(TestElement.GUI_CLASS) || name.equals(TestElement.NAME)
                     || name.equals(TestElement.TEST_CLASS)) {
                 continue; // go on with next property.
             }
 
             for (Iterator<ConfigTestElement> configs = configurations.iterator(); configs.hasNext();) {
                 ConfigTestElement config = configs.next();
 
                 String configValue = config.getPropertyAsString(name);
 
                 if (configValue != null && configValue.length() > 0) {
                     if (configValue.equals(value)) {
                         sampler.setProperty(name, ""); // $NON-NLS-1$
                     }
                     // Property was found in a config element. Whether or not
                     // it matched the value in the sampler, we're done with
                     // this property -- don't look at lower-priority configs:
                     break;
                 }
             }
         }
     }
 
     private String generateMatchUrl(HTTPSamplerBase sampler) {
         StringBuilder buf = new StringBuilder(sampler.getDomain());
         buf.append(':'); // $NON-NLS-1$
         buf.append(sampler.getPort());
         buf.append(sampler.getPath());
         if (sampler.getQueryString().length() > 0) {
             buf.append('?'); // $NON-NLS-1$
             buf.append(sampler.getQueryString());
         }
         return buf.toString();
     }
 
     private boolean matchesPatterns(String url, CollectionProperty patterns) {
         PropertyIterator iter = patterns.iterator();
         while (iter.hasNext()) {
             String item = iter.next().getStringValue();
             Pattern pattern = null;
             try {
                 pattern = JMeterUtils.getPatternCache().getPattern(item, Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.SINGLELINE_MASK);
                 if (JMeterUtils.getMatcher().matches(url, pattern)) {
                     return true;
                 }
             } catch (MalformedCachePatternException e) {
                 log.warn("Skipped invalid pattern: " + item, e);
             }
         }
         return false;
     }
 
     /**
      * Scan all test elements passed in for values matching the value of any of
      * the variables in any of the variable-holding elements in the collection.
      *
      * @param sampler
      *            A TestElement to replace values on
      * @param configs
      *            More TestElements to replace values on
      * @param variables
      *            Collection of Arguments to use to do the replacement, ordered
      *            by ascending priority.
      */
     private void replaceValues(TestElement sampler, TestElement[] configs, Collection<Arguments> variables) {
         // Build the replacer from all the variables in the collection:
         ValueReplacer replacer = new ValueReplacer();
         for (Iterator<Arguments> vars = variables.iterator(); vars.hasNext();) {
             final Map<String, String> map = vars.next().getArgumentsAsMap();
             for (Iterator<String> vals = map.values().iterator(); vals.hasNext();){
                final Object next = vals.next();
                if ("".equals(next)) {// Drop any empty values (Bug 45199)
                    vals.remove();
                }
             }
             replacer.addVariables(map);
         }
 
         try {
             boolean cachedRegexpMatch = regexMatch;
             replacer.reverseReplace(sampler, cachedRegexpMatch);
             for (int i = 0; i < configs.length; i++) {
                 if (configs[i] != null) {
                     replacer.reverseReplace(configs[i], cachedRegexpMatch);
                 }
             }
         } catch (InvalidVariableException e) {
             log.warn("Invalid variables included for replacement into recorded " + "sample", e);
         }
     }
 
     /**
      * This will notify sample listeners directly within the Proxy of the
      * sampling that just occured -- so that we have a means to record the
      * server's responses as we go.
      *
      * @param event
      *            sampling event to be delivered
      */
     private void notifySampleListeners(SampleEvent event) {
         JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
         JMeterTreeNode myNode = treeModel.getNodeOf(this);
         Enumeration<JMeterTreeNode> kids = myNode.children();
         while (kids.hasMoreElements()) {
             JMeterTreeNode subNode = kids.nextElement();
             if (subNode.isEnabled()) {
                 TestElement testElement = subNode.getTestElement();
                 if (testElement instanceof SampleListener) {
                     ((SampleListener) testElement).sampleOccurred(event);
                 }
             }
         }
     }
 
     /**
      * This will notify test listeners directly within the Proxy that the 'test'
      * (here meaning the proxy recording) has started.
      */
     private void notifyTestListenersOfStart() {
         JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
         JMeterTreeNode myNode = treeModel.getNodeOf(this);
         Enumeration<JMeterTreeNode> kids = myNode.children();
         while (kids.hasMoreElements()) {
             JMeterTreeNode subNode = kids.nextElement();
             if (subNode.isEnabled()) {
                 TestElement testElement = subNode.getTestElement();
                 if (testElement instanceof TestStateListener) {
                     ((TestStateListener) testElement).testStarted();
                 }
             }
         }
     }
 
     /**
      * This will notify test listeners directly within the Proxy that the 'test'
      * (here meaning the proxy recording) has ended.
      */
     private void notifyTestListenersOfEnd() {
         JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
         JMeterTreeNode myNode = treeModel.getNodeOf(this);
         Enumeration<JMeterTreeNode> kids = myNode.children();
         while (kids.hasMoreElements()) {
             JMeterTreeNode subNode = kids.nextElement();
             if (subNode.isEnabled()) {
                 TestElement testElement = subNode.getTestElement();
                 if (testElement instanceof TestStateListener) { // TL - TE
                     ((TestStateListener) testElement).testEnded();
                 }
             }
         }
     }
 
     @Override
     public boolean canRemove() {
         return null == server;
     }
 
     private void initKeyStore() throws IOException, GeneralSecurityException {
         switch(KEYSTORE_MODE) {
         case DYNAMIC_KEYSTORE:
             storePassword = getPassword();
             keyPassword = getPassword();
             initDynamicKeyStore();
             break;
         case JMETER_KEYSTORE:
             storePassword = getPassword();
             keyPassword = getPassword();
             initJMeterKeyStore();
             break;
         case USER_KEYSTORE:
             storePassword = JMeterUtils.getPropDefault("proxy.cert.keystorepass", DEFAULT_PASSWORD); // $NON-NLS-1$;
             keyPassword = JMeterUtils.getPropDefault("proxy.cert.keypassword", DEFAULT_PASSWORD); // $NON-NLS-1$;
             log.info("HTTP(S) Test Script Recorder will use the keystore '"+ CERT_PATH_ABS + "' with the alias: '" + CERT_ALIAS + "'");
             initUserKeyStore();
             break;
         case NONE:
             throw new IOException("Cannot find keytool application and no keystore was provided");
         default:
             throw new IllegalStateException("Impossible case: " + KEYSTORE_MODE);
         }
     }
 
     /**
      * Initialise the user-provided keystore
      */
     private void initUserKeyStore() {
         try {
             keyStore = getKeyStore(storePassword.toCharArray());
             X509Certificate  caCert = (X509Certificate) keyStore.getCertificate(CERT_ALIAS);
             if (caCert == null) {
                 log.error("Could not find key with alias " + CERT_ALIAS);
                 keyStore = null;
             } else {
                 caCert.checkValidity(new Date(System.currentTimeMillis()+DateUtils.MILLIS_PER_DAY));
             }
         } catch (Exception e) {
             keyStore = null;
             log.error("Could not open keystore or certificate is not valid " + CERT_PATH_ABS + " " + e.getMessage());
         }
     }
 
     /**
      * Initialise the dynamic domain keystore
      */
     private void initDynamicKeyStore() throws IOException, GeneralSecurityException {
         if (storePassword  != null) { // Assume we have already created the store
             try {
                 keyStore = getKeyStore(storePassword.toCharArray());
                 for(String alias : KeyToolUtils.getCAaliases()) {
                     X509Certificate  caCert = (X509Certificate) keyStore.getCertificate(alias);
                     if (caCert == null) {
                         keyStore = null; // no CA key - probably the wrong store type.
                         break; // cannot continue
                     } else {
                         caCert.checkValidity(new Date(System.currentTimeMillis()+DateUtils.MILLIS_PER_DAY));
                         log.info("Valid alias found for " + alias);
                     }
                 }
             } catch (IOException e) { // store is faulty, we need to recreate it
                 keyStore = null; // if cert is not valid, flag up to recreate it
                 if (e.getCause() instanceof UnrecoverableKeyException) {
                     log.warn("Could not read key store " + e.getMessage() + "; cause: " + e.getCause().getMessage());
                 } else {
                     log.warn("Could not open/read key store " + e.getMessage()); // message includes the file name
                 }
             } catch (GeneralSecurityException e) {
                 keyStore = null; // if cert is not valid, flag up to recreate it
                 log.warn("Problem reading key store: " + e.getMessage());
             }
         }
         if (keyStore == null) { // no existing file or not valid
             storePassword = RandomStringUtils.randomAlphanumeric(20); // Alphanum to avoid issues with command-line quoting
             keyPassword = storePassword; // we use same password for both
             setPassword(storePassword);
             log.info("Creating Proxy CA in " + CERT_PATH_ABS);
             KeyToolUtils.generateProxyCA(CERT_PATH, storePassword, CERT_VALIDITY);
             log.info("Created keystore in " + CERT_PATH_ABS);
             keyStore = getKeyStore(storePassword.toCharArray()); // This should now work
         }
         final String sslDomains = getSslDomains().trim();
         if (sslDomains.length() > 0) {
             final String[] domains = sslDomains.split(",");
             // The subject may be either a host or a domain
             for(String subject : domains) {
                 if (isValid(subject)) {
                     if (!keyStore.containsAlias(subject)) {
                         log.info("Creating entry " + subject + " in " + CERT_PATH_ABS);
                         KeyToolUtils.generateHostCert(CERT_PATH, storePassword, subject, CERT_VALIDITY);
                         keyStore = getKeyStore(storePassword.toCharArray()); // reload to pick up new aliases
                         // reloading is very quick compared with creating an entry currently
                     }
                 } else {
                     log.warn("Attempt to create an invalid domain certificate: " + subject);
                 }
             }
         }
     }
 
     private boolean isValid(String subject) {
         String parts[] = subject.split("\\.");
         if (!parts[0].endsWith("*")) { // not a wildcard
             return true;
         }
         return parts.length >= 3 && AbstractVerifier.acceptableCountryWildcard(subject);
     }
 
     // This should only be called for a specific host
     KeyStore updateKeyStore(String port, String host) throws IOException, GeneralSecurityException {
         synchronized(CERT_PATH) { // ensure Proxy threads cannot interfere with each other
             if (!keyStore.containsAlias(host)) {
                 log.info(port + "Creating entry " + host + " in " + CERT_PATH_ABS);
                 KeyToolUtils.generateHostCert(CERT_PATH, storePassword, host, CERT_VALIDITY);
             }
             keyStore = getKeyStore(storePassword.toCharArray()); // reload after adding alias
         }
         return keyStore;
     }
 
     /**
      * Initialise the single key JMeter keystore (original behaviour)
      */
     private void initJMeterKeyStore() throws IOException, GeneralSecurityException {
         if (storePassword  != null) { // Assume we have already created the store
             try {
                 keyStore = getKeyStore(storePassword.toCharArray());
                 X509Certificate  caCert = (X509Certificate) keyStore.getCertificate(JMETER_SERVER_ALIAS);
                 caCert.checkValidity(new Date(System.currentTimeMillis()+DateUtils.MILLIS_PER_DAY));
             } catch (Exception e) { // store is faulty, we need to recreate it
                 keyStore = null; // if cert is not valid, flag up to recreate it
                 log.warn("Could not open expected file or certificate is not valid " + CERT_PATH_ABS  + " " + e.getMessage());
             }
         }
         if (keyStore == null) { // no existing file or not valid
             storePassword = RandomStringUtils.randomAlphanumeric(20); // Alphanum to avoid issues with command-line quoting
             keyPassword = storePassword; // we use same password for both
             setPassword(storePassword);
             log.info("Generating standard keypair in " + CERT_PATH_ABS);
             CERT_PATH.delete(); // safer to start afresh
             KeyToolUtils.genkeypair(CERT_PATH, JMETER_SERVER_ALIAS, storePassword, CERT_VALIDITY, null, null);
             keyStore = getKeyStore(storePassword.toCharArray()); // This should now work
         }
     }
 
     private KeyStore getKeyStore(char[] password) throws GeneralSecurityException, IOException {
         InputStream in = null;
         try {
             in = new BufferedInputStream(new FileInputStream(CERT_PATH));
             log.debug("Opened Keystore file: " + CERT_PATH_ABS);
             KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
             ks.load(in, password);
             log.debug("Loaded Keystore file: " + CERT_PATH_ABS);
             return ks;
         } finally {
             IOUtils.closeQuietly(in);
         }
     }
 
     private String getPassword() {
         return PREFERENCES.get(USER_PASSWORD_KEY, null);
     }
 
     private void setPassword(String password) {
         PREFERENCES.put(USER_PASSWORD_KEY, password);
     }
 
     // the keystore for use by the Proxy
     KeyStore getKeyStore() {
         return keyStore;
     }
 
     String getKeyPassword() {
         return keyPassword;
     }
 
     public static boolean isDynamicMode() {
         return KEYSTORE_MODE == KeystoreMode.DYNAMIC_KEYSTORE;
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java
index 347359a7b..7afbb0e18 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java
@@ -1,654 +1,656 @@
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
  */
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.BufferedInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.BindException;
 import java.net.HttpURLConnection;
 import java.net.InetSocketAddress;
 import java.net.Proxy;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.List;
 import java.util.Map;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.commons.io.input.CountingInputStream;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * A sampler which understands all the parts necessary to read statistics about
  * HTTP requests, including cookies and authentication.
  *
  */
 public class HTTPJavaImpl extends HTTPAbstractImpl {
     private static final boolean OBEY_CONTENT_LENGTH =
         JMeterUtils.getPropDefault("httpsampler.obey_contentlength", false); // $NON-NLS-1$
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final int MAX_CONN_RETRIES =
         JMeterUtils.getPropDefault("http.java.sampler.retries" // $NON-NLS-1$
                 ,10); // Maximum connection retries
 
     static {
         log.info("Maximum connection retries = "+MAX_CONN_RETRIES); // $NON-NLS-1$
         // Temporary copies, so can set the final ones
     }
 
     private static final byte[] NULL_BA = new byte[0];// can share these
 
     /** Handles writing of a post or put request */
     private transient PostWriter postOrPutWriter;
 
     private volatile HttpURLConnection savedConn;
 
     protected HTTPJavaImpl(HTTPSamplerBase base) {
         super(base);
     }
 
     /**
      * Set request headers in preparation to opening a connection.
      *
      * @param conn
      *            <code>URLConnection</code> to set headers on
      * @exception IOException
      *                if an I/O exception occurs
      */
     protected void setPostHeaders(URLConnection conn) throws IOException {
         postOrPutWriter = new PostWriter();
         postOrPutWriter.setHeaders(conn, testElement);
     }
 
     private void setPutHeaders(URLConnection conn) throws IOException {
         postOrPutWriter = new PutWriter();
         postOrPutWriter.setHeaders(conn, testElement);
     }
 
     /**
      * Send POST data from <code>Entry</code> to the open connection.
      * This also handles sending data for PUT requests
      *
      * @param connection
      *            <code>URLConnection</code> where POST data should be sent
      * @return a String show what was posted. Will not contain actual file upload content
      * @exception IOException
      *                if an I/O exception occurs
      */
     protected String sendPostData(URLConnection connection) throws IOException {
         return postOrPutWriter.sendPostData(connection, testElement);
     }
 
     private String sendPutData(URLConnection connection) throws IOException {
         return postOrPutWriter.sendPostData(connection, testElement);
     }
 
     /**
      * Returns an <code>HttpURLConnection</code> fully ready to attempt
      * connection. This means it sets the request method (GET or POST), headers,
      * cookies, and authorization for the URL request.
      * <p>
      * The request infos are saved into the sample result if one is provided.
      *
      * @param u
      *            <code>URL</code> of the URL request
      * @param method
      *            GET, POST etc
      * @param res
      *            sample result to save request infos to
      * @return <code>HttpURLConnection</code> ready for .connect
      * @exception IOException
      *                if an I/O Exception occurs
      */
     protected HttpURLConnection setupConnection(URL u, String method, HTTPSampleResult res) throws IOException {
         SSLManager sslmgr = null;
         if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(u.getProtocol())) {
             try {
                 sslmgr=SSLManager.getInstance(); // N.B. this needs to be done before opening the connection
             } catch (Exception e) {
                 log.warn("Problem creating the SSLManager: ", e);
             }
         }
 
         final HttpURLConnection conn;
         final String proxyHost = getProxyHost();
         final int proxyPort = getProxyPortInt();
         if (proxyHost.length() > 0 && proxyPort > 0){
             Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
             //TODO - how to define proxy authentication for a single connection?
             // It's not clear if this is possible
 //            String user = getProxyUser();
 //            if (user.length() > 0){
 //                Authenticator auth = new ProxyAuthenticator(user, getProxyPass());
 //            }
             conn = (HttpURLConnection) u.openConnection(proxy);
         } else {
             conn = (HttpURLConnection) u.openConnection();
         }
 
         // Update follow redirects setting just for this connection
         conn.setInstanceFollowRedirects(getAutoRedirects());
 
         int cto = getConnectTimeout();
         if (cto > 0){
             conn.setConnectTimeout(cto);
         }
 
         int rto = getResponseTimeout();
         if (rto > 0){
             conn.setReadTimeout(rto);
         }
 
         if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(u.getProtocol())) {
             try {
                 if (null != sslmgr){
                     sslmgr.setContext(conn); // N.B. must be done after opening connection
                 }
             } catch (Exception e) {
                 log.warn("Problem setting the SSLManager for the connection: ", e);
             }
         }
 
         // a well-bahaved browser is supposed to send 'Connection: close'
         // with the last request to an HTTP server. Instead, most browsers
         // leave it to the server to close the connection after their
         // timeout period. Leave it to the JMeter user to decide.
         if (getUseKeepAlive()) {
             conn.setRequestProperty(HTTPConstants.HEADER_CONNECTION, HTTPConstants.KEEP_ALIVE);
         } else {
             conn.setRequestProperty(HTTPConstants.HEADER_CONNECTION, HTTPConstants.CONNECTION_CLOSE);
         }
 
         conn.setRequestMethod(method);
         setConnectionHeaders(conn, u, getHeaderManager(), getCacheManager());
         String cookies = setConnectionCookie(conn, u, getCookieManager());
 
         setConnectionAuthorization(conn, u, getAuthManager());
 
         if (method.equals(HTTPConstants.POST)) {
             setPostHeaders(conn);
         } else if (method.equals(HTTPConstants.PUT)) {
             setPutHeaders(conn);
         }
 
         if (res != null) {
             res.setRequestHeaders(getConnectionHeaders(conn));
             res.setCookies(cookies);
         }
 
         return conn;
     }
 
     /**
      * Reads the response from the URL connection.
      *
      * @param conn
      *            URL from which to read response
+     * @param res
+     *            {@link SampleResult} to read response into
      * @return response content
      * @exception IOException
      *                if an I/O exception occurs
      */
     protected byte[] readResponse(HttpURLConnection conn, SampleResult res) throws IOException {
         BufferedInputStream in;
 
         final int contentLength = conn.getContentLength();
         if ((contentLength == 0)
             && OBEY_CONTENT_LENGTH) {
             log.info("Content-Length: 0, not reading http-body");
             res.setResponseHeaders(getResponseHeaders(conn));
             res.latencyEnd();
             return NULL_BA;
         }
 
         // works OK even if ContentEncoding is null
         boolean gzipped = HTTPConstants.ENCODING_GZIP.equals(conn.getContentEncoding());
         InputStream instream = null;
         try {
             instream = new CountingInputStream(conn.getInputStream());
             if (gzipped) {
                 in = new BufferedInputStream(new GZIPInputStream(instream));
             } else {
                 in = new BufferedInputStream(instream);
             }
         } catch (IOException e) {
             if (! (e.getCause() instanceof FileNotFoundException))
             {
                 log.error("readResponse: "+e.toString());
                 Throwable cause = e.getCause();
                 if (cause != null){
                     log.error("Cause: "+cause);
                     if(cause instanceof Error) {
                         throw (Error)cause;
                     }
                 }
             }
             // Normal InputStream is not available
             InputStream errorStream = conn.getErrorStream();
             if (errorStream == null) {
                 log.info("Error Response Code: "+conn.getResponseCode()+", Server sent no Errorpage");
                 res.setResponseHeaders(getResponseHeaders(conn));
                 res.latencyEnd();
                 return NULL_BA;
             }
 
             log.info("Error Response Code: "+conn.getResponseCode());
 
             if (gzipped) {
                 in = new BufferedInputStream(new GZIPInputStream(errorStream));
             } else {
                 in = new BufferedInputStream(errorStream);
             }
         } catch (Exception e) {
             log.error("readResponse: "+e.toString());
             Throwable cause = e.getCause();
             if (cause != null){
                 log.error("Cause: "+cause);
                 if(cause instanceof Error) {
                     throw (Error)cause;
                 }
             }
             in = new BufferedInputStream(conn.getErrorStream());
         }
         // N.B. this closes 'in'
         byte[] responseData = readResponse(res, in, contentLength);
         if (instream != null) {
             res.setBodySize(((CountingInputStream) instream).getCount());
             instream.close();
         }
         return responseData;
     }
 
     /**
      * Gets the ResponseHeaders from the URLConnection
      *
      * @param conn
      *            connection from which the headers are read
      * @return string containing the headers, one per line
      */
     protected String getResponseHeaders(HttpURLConnection conn) {
         StringBuilder headerBuf = new StringBuilder();
         headerBuf.append(conn.getHeaderField(0));// Leave header as is
         // headerBuf.append(conn.getHeaderField(0).substring(0, 8));
         // headerBuf.append(" ");
         // headerBuf.append(conn.getResponseCode());
         // headerBuf.append(" ");
         // headerBuf.append(conn.getResponseMessage());
         headerBuf.append("\n"); //$NON-NLS-1$
 
         String hfk;
         for (int i = 1; (hfk=conn.getHeaderFieldKey(i)) != null; i++) {
             headerBuf.append(hfk);
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(conn.getHeaderField(i));
             headerBuf.append("\n"); // $NON-NLS-1$
         }
         return headerBuf.toString();
     }
 
     /**
      * Extracts all the required cookies for that particular URL request and
      * sets them in the <code>HttpURLConnection</code> passed in.
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param cookieManager
      *            the <code>CookieManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      */
     private String setConnectionCookie(HttpURLConnection conn, URL u, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(u);
             if (cookieHeader != null) {
                 conn.setRequestProperty(HTTPConstants.HEADER_COOKIE, cookieHeader);
             }
         }
         return cookieHeader;
     }
 
     /**
      * Extracts all the required headers for that particular URL request and
      * sets them in the <code>HttpURLConnection</code> passed in
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param headerManager
      *            the <code>HeaderManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      * @param cacheManager the CacheManager (may be null)
      */
     private void setConnectionHeaders(HttpURLConnection conn, URL u, HeaderManager headerManager, CacheManager cacheManager) {
         // Add all the headers from the HeaderManager
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 PropertyIterator i = headers.iterator();
                 while (i.hasNext()) {
                     Header header = (Header) i.next().getObjectValue();
                     String n = header.getName();
                     String v = header.getValue();
                     conn.addRequestProperty(n, v);
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(conn, u);
         }
     }
 
     /**
      * Get all the headers for the <code>HttpURLConnection</code> passed in
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @return the headers as a string
      */
     private String getConnectionHeaders(HttpURLConnection conn) {
         // Get all the request properties, which are the headers set on the connection
         StringBuilder hdrs = new StringBuilder(100);
         Map<String, List<String>> requestHeaders = conn.getRequestProperties();
         for(Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
             String headerKey=entry.getKey();
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if(!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(headerKey)) {
                 // value is a List of Strings
                 for (String value : entry.getValue()){
                     hdrs.append(headerKey);
                     hdrs.append(": "); // $NON-NLS-1$
                     hdrs.append(value);
                     hdrs.append("\n"); // $NON-NLS-1$
                 }
             }
         }
         return hdrs.toString();
     }
 
     /**
      * Extracts all the required authorization for that particular URL request
      * and sets it in the <code>HttpURLConnection</code> passed in.
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param authManager
      *            the <code>AuthManager</code> containing all the cookies for
      *            this <code>UrlConfig</code>
      */
     private void setConnectionAuthorization(HttpURLConnection conn, URL u, AuthManager authManager) {
         if (authManager != null) {
             Authorization auth = authManager.getAuthForURL(u);
             if (auth != null) {
                 conn.setRequestProperty(HTTPConstants.HEADER_AUTHORIZATION, auth.toBasicHeader());
             }
         }
     }
 
     /**
      * Samples the URL passed in and stores the result in
      * <code>HTTPSampleResult</code>, following redirects and downloading
      * page resources as appropriate.
      * <p>
      * When getting a redirect target, redirects are not followed and resources
      * are not downloaded. The caller will take care of this.
      *
      * @param url
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling
      */
     @Override
     protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {
         HttpURLConnection conn = null;
 
         String urlStr = url.toString();
         if (log.isDebugEnabled()) {
             log.debug("Start : sample " + urlStr);
             log.debug("method " + method+ " followingRedirect " + areFollowingRedirect + " depth " + frameDepth);            
         }
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(urlStr);
         res.setURL(url);
         res.setHTTPMethod(method);
 
         res.sampleStart(); // Count the retries as well in the time
 
         // Check cache for an entry with an Expires header in the future
         final CacheManager cacheManager = getCacheManager();
         if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
            if (cacheManager.inCache(url)) {
                return updateSampleResultForResourceInCache(res);
            }
         }
 
         try {
             // Sampling proper - establish the connection and read the response:
             // Repeatedly try to connect:
             int retry;
             // Start with 0 so tries at least once, and retries at most MAX_CONN_RETRIES times
             for (retry = 0; retry <= MAX_CONN_RETRIES; retry++) {
                 try {
                     conn = setupConnection(url, method, res);
                     // Attempt the connection:
                     savedConn = conn;
                     conn.connect();
                     break;
                 } catch (BindException e) {
                     if (retry >= MAX_CONN_RETRIES) {
                         log.error("Can't connect after "+retry+" retries, "+e);
                         throw e;
                     }
                     log.debug("Bind exception, try again");
                     if (conn!=null) {
                         savedConn = null; // we don't want interrupt to try disconnection again
                         conn.disconnect();
                     }
                     setUseKeepAlive(false);
                     continue; // try again
                 } catch (IOException e) {
                     log.debug("Connection failed, giving up");
                     throw e;
                 }
             }
             if (retry > MAX_CONN_RETRIES) {
                 // This should never happen, but...
                 throw new BindException();
             }
             // Nice, we've got a connection. Finish sending the request:
             if (method.equals(HTTPConstants.POST)) {
                 String postBody = sendPostData(conn);
                 res.setQueryString(postBody);
             }
             else if (method.equals(HTTPConstants.PUT)) {
                 String putBody = sendPutData(conn);
                 res.setQueryString(putBody);
             }
             // Request sent. Now get the response:
             byte[] responseData = readResponse(conn, res);
 
             res.sampleEnd();
             // Done with the sampling proper.
 
             // Now collect the results into the HTTPSampleResult:
 
             res.setResponseData(responseData);
 
             @SuppressWarnings("null") // Cannot be null here
             int errorLevel = conn.getResponseCode();
             String respMsg = conn.getResponseMessage();
             String hdr=conn.getHeaderField(0);
             if (hdr == null) {
                 hdr="(null)";  // $NON-NLS-1$
             }
             if (errorLevel == -1){// Bug 38902 - sometimes -1 seems to be returned unnecessarily
                 if (respMsg != null) {// Bug 41902 - NPE
                     try {
                         errorLevel = Integer.parseInt(respMsg.substring(0, 3));
                         log.warn("ResponseCode==-1; parsed "+respMsg+ " as "+errorLevel);
                       } catch (NumberFormatException e) {
                         log.warn("ResponseCode==-1; could not parse "+respMsg+" hdr: "+hdr);
                       }
                 } else {
                     respMsg=hdr; // for result
                     log.warn("ResponseCode==-1 & null ResponseMessage. Header(0)= "+hdr);
                 }
             }
             if (errorLevel == -1) {
                 res.setResponseCode("(null)"); // $NON-NLS-1$
             } else {
                 res.setResponseCode(Integer.toString(errorLevel));
             }
             res.setSuccessful(isSuccessCode(errorLevel));
 
             if (respMsg == null) {// has been seen in a redirect
                 respMsg=hdr; // use header (if possible) if no message found
             }
             res.setResponseMessage(respMsg);
 
             String ct = conn.getContentType();
             if (ct != null){
                 res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                 res.setEncodingAndType(ct);
             }
 
             String responseHeaders = getResponseHeaders(conn);
             res.setResponseHeaders(responseHeaders);
             if (res.isRedirect()) {
                 res.setRedirectLocation(conn.getHeaderField(HTTPConstants.HEADER_LOCATION));
             }
             
             // record headers size to allow HTTPSampleResult.getBytes() with different options
             res.setHeadersSize(responseHeaders.replaceAll("\n", "\r\n") // $NON-NLS-1$ $NON-NLS-2$
                     .length() + 2); // add 2 for a '\r\n' at end of headers (before data) 
             if (log.isDebugEnabled()) {
                 log.debug("Response headersSize=" + res.getHeadersSize() + " bodySize=" + res.getBodySize()
                         + " Total=" + (res.getHeadersSize() + res.getBodySize()));
             }
             
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 res.setURL(conn.getURL());
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(conn, url, getCookieManager());
 
             // Save cache information
             if (cacheManager != null){
                 cacheManager.saveDetails(conn, res);
             }
 
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
             log.debug("End : sample");
             return res;
         } catch (IOException e) {
             res.sampleEnd();
             savedConn = null; // we don't want interrupt to try disconnection again
             // We don't want to continue using this connection, even if KeepAlive is set
             if (conn != null) { // May not exist
                 conn.disconnect();
             }
             conn=null; // Don't process again
             return errorResult(e, res);
         } finally {
             // calling disconnect doesn't close the connection immediately,
             // but indicates we're through with it. The JVM should close
             // it when necessary.
             savedConn = null; // we don't want interrupt to try disconnection again
             disconnect(conn); // Disconnect unless using KeepAlive
         }
     }
 
     protected void disconnect(HttpURLConnection conn) {
         if (conn != null) {
             String connection = conn.getHeaderField(HTTPConstants.HEADER_CONNECTION);
             String protocol = conn.getHeaderField(0);
             if ((connection == null && (protocol == null || !protocol.startsWith(HTTPConstants.HTTP_1_1)))
                     || (connection != null && connection.equalsIgnoreCase(HTTPConstants.CONNECTION_CLOSE))) {
                 conn.disconnect();
             } // TODO ? perhaps note connection so it can be disconnected at end of test?
         }
     }
 
     /**
      * From the <code>HttpURLConnection</code>, store all the "set-cookie"
      * key-pair values in the cookieManager of the <code>UrlConfig</code>.
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param cookieManager
      *            the <code>CookieManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      */
     private void saveConnectionCookies(HttpURLConnection conn, URL u, CookieManager cookieManager) {
         if (cookieManager != null) {
             for (int i = 1; conn.getHeaderFieldKey(i) != null; i++) {
                 if (conn.getHeaderFieldKey(i).equalsIgnoreCase(HTTPConstants.HEADER_SET_COOKIE)) {
                     cookieManager.addCookieFromHeader(conn.getHeaderField(i), u);
                 }
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean interrupt() {
         HttpURLConnection conn = savedConn;
         if (conn != null) {
             savedConn = null;
             conn.disconnect();
         }
         return conn != null;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLHelper.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLHelper.java
index d28c9e973..70fa49136 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLHelper.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLHelper.java
@@ -1,421 +1,425 @@
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
 
 package org.apache.jmeter.protocol.http.util;
 
 import java.io.IOException;
 import java.net.HttpURLConnection;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Set;
 
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.xml.sax.SAXException;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * For now I use DOM for WSDLHelper, but it would be more efficient to use JAXB
  * to generate an object model for WSDL and use it to perform serialization and
  * deserialization. It also makes it easier to traverse the WSDL to get
  * necessary information.
  * <p>
  * Created on: Jun 3, 2003<br>
  *
  */
 public class WSDLHelper {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/"; //$NON-NLS-1$
 
     private static final String SOAP11_BINDING_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/"; //$NON-NLS-1$
     
     private static final String SOAP12_BINDING_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap12/"; //$NON-NLS-1$
 
     private static int GET_WDSL_TIMEOUT = 5000; // timeout to retrieve wsdl when server not response
     
     /**
      * -------------------------------------------- The members used by the
      * class to do its work --------------------------------------------
      */
 
     private URL WSDLURL = null;
 
     private URLConnection CONN = null;
 
     private Document WSDLDOC = null;
 
     private String SOAPBINDING = null;
 
     private URL bindingURL = null;
 
     private Object[] SOAPOPS = null;
 
     private final Map<String, String> ACTIONS = new HashMap<String, String>();
 
     private final AuthManager AUTH;
 
     /**
      * Default constructor takes a string URL
      *
      * @param url
      *            url to the wsdl
      * @throws MalformedURLException
      *             if <code>url</code> is malformed
      */
     public WSDLHelper(String url) throws MalformedURLException {
         this(url, null);
     }
 
     /**
      * @param url
      *            url to the wsdl
      * @param auth
      *            {@link AuthManager} to use
      * @throws MalformedURLException
      *             if <code>url</code> is malformed
      */
     public WSDLHelper(String url, AuthManager auth) throws MalformedURLException {
         WSDLURL = new URL(url);
         this.AUTH = auth;
     }
 
     /**
      * Returns the URL
      *
      * @return the URL
      */
     public URL getURL() {
         return this.WSDLURL;
     }
 
     /**
      * Return the protocol from the URL. this is needed, so that HTTPS works
      * as expected.
      *
      * @return protocol extracted from url
      */
     public String getProtocol() {
         return this.bindingURL.getProtocol();
     }
 
     /**
      * Return the host in the WSDL binding address
      *
      * @return host extracted from url
      */
     public String getBindingHost() {
         return this.bindingURL.getHost();
     }
 
     /**
      * Return the path in the WSDL for the binding address
      *
      * @return path extracted from url
      */
     public String getBindingPath() {
         return this.bindingURL.getPath();
     }
 
     /**
      * Return the port for the binding address
      *
      * @return port extracted from url
      */
     public int getBindingPort() {
         return this.bindingURL.getPort();
     }
 
     /**
      * Returns the binding point for the webservice. Right now it naively
      * assumes there's only one binding point with numerous soap operations.
      *
      * @return String
      */
     public String getBinding() {
         try {
             NodeList services = this.WSDLDOC.getElementsByTagNameNS(WSDL_NAMESPACE, "service");
             // the document should only have one service node
             // if it doesn't it may not work!
             Element node = (Element) services.item(0);
             NodeList ports = node.getElementsByTagNameNS(WSDL_NAMESPACE, "port");            
             if(ports.getLength()>0) {
                 Element pnode = (Element) ports.item(0);
                 // NOTUSED String portname = pnode.getAttribute("name");
                 // used to check binding, but now it doesn't. it was
                 // failing when wsdl did not using binding as expected
                 NodeList servlist = pnode.getElementsByTagNameNS(SOAP11_BINDING_NAMESPACE, "address");
                 // check soap12
                 if (servlist.getLength() == 0) {
                     servlist = pnode.getElementsByTagNameNS(SOAP12_BINDING_NAMESPACE, "address");
                 }
                 Element addr = (Element) servlist.item(0);
                 this.SOAPBINDING = addr.getAttribute("location");
                 this.bindingURL = new URL(this.SOAPBINDING);
                 return this.SOAPBINDING;
             } else {
                 return null;
             }
         } catch (Exception exception) {
             log.warn("Exception calling getBinding:"+exception.getMessage(),exception);
             return null;
         }
     }
 
     /**
      * Method is used internally to connect to the URL. It's protected;
      * therefore external classes should use parse to get the resource at the
      * given location.
      *
-     * @throws IOException
+     * @throws IOException when I/O error occurs
      */
     protected void connect() throws IOException {
         CONN = WSDLURL.openConnection();
         CONN.setConnectTimeout(GET_WDSL_TIMEOUT);
         CONN.setReadTimeout(GET_WDSL_TIMEOUT);
         // in the rare case the WSDL is protected and requires
         // authentication, use the AuthManager to set the
         // authorization. Basic and Digest authorization are
         // pretty weak and don't provide real security.
         if (CONN instanceof HttpURLConnection && this.AUTH != null && this.AUTH.getAuthHeaderForURL(this.WSDLURL) != null) {
             CONN.setRequestProperty("Authorization", this.AUTH.getAuthHeaderForURL(this.WSDLURL));
         }
     }
 
     /**
      * We try to close the connection to make sure it doesn't hang around.
      */
     protected void close() {
         try {
             if (CONN != null) {
                 CONN.getInputStream().close();
             }
         } catch (IOException ignored) {
             // do nothing
         }
     }
 
     /**
      * Method is used internally to parse the InputStream and build the document
      * using javax.xml.parser API.
+     *
+     * @throws ParserConfigurationException When building {@link DocumentBuilder} fails
+     * @throws IOException when reading the document fails
+     * @throws SAXException when parsing the document fails
      */
     protected void buildDocument() throws ParserConfigurationException, IOException, SAXException {
         DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
         dbfactory.setNamespaceAware(true);
         DocumentBuilder docbuild = dbfactory.newDocumentBuilder();
         WSDLDOC = docbuild.parse(CONN.getInputStream());
     }
 
     /**
      * Call this method to retrieve the WSDL. This method must be called,
      * otherwise a connection to the URL won't be made and the stream won't be
      * parsed.
      *
      * @throws WSDLException when parsing fails
      */
     public void parse() throws WSDLException {
         try {
             this.connect();
             this.buildDocument();
             SOAPOPS = this.getOperations();
         } catch (IOException exception) {
             throw (new WSDLException(exception));
         } catch (SAXException exception) {
             throw (new WSDLException(exception));
         } catch (ParserConfigurationException exception) {
             throw (new WSDLException(exception));
         } finally {
             this.close();
         }
     }
 
     /**
      * Get a list of the web methods as a string array.
      *
      * @return list of web methods
      */
     public String[] getWebMethods() {
         for (int idx = 0; idx < SOAPOPS.length; idx++) {
             // get the node
             Node act = (Node) SOAPOPS[idx];
             // get the soap:operation
             NodeList opers = ((Element) act).getElementsByTagNameNS(SOAP11_BINDING_NAMESPACE, "operation");
             if (opers.getLength() == 0) {
                 opers = ((Element) act).getElementsByTagNameNS(SOAP12_BINDING_NAMESPACE, "operation");
             }
 
             // there should only be one soap:operation node per operation
             Element op = (Element) opers.item(0);
             String value;
             if (op != null) {
                 value = op.getAttribute("soapAction");
             } else {
                 value = "";
             }
             String key = ((Element) act).getAttribute("name");
             this.ACTIONS.put(key, value);
         }
         Set<String> keys = this.ACTIONS.keySet();
         String[] stringmeth = new String[keys.size()];
         Object[] stringKeys = keys.toArray();
         System.arraycopy(stringKeys, 0, stringmeth, 0, keys.size());
         return stringmeth;
     }
 
     /**
      * Return the soap action matching the operation name.
      *
      * @param key
      *            name of the operation
      * @return associated action
      */
     public String getSoapAction(String key) {
         return this.ACTIONS.get(key);
     }
 
     /**
      * Get the wsdl document.
      *
      * @return wsdl document
      */
     public Document getWSDLDocument() {
         return WSDLDOC;
     }
 
     /**
      * Method will look at the binding nodes and see if the first child is a
      * soap:binding. If it is, it adds it to an array.
      *
      * @return Node[]
      */
     public Object[] getSOAPBindings() {
         ArrayList<Element> list = new ArrayList<Element>();
         NodeList bindings = WSDLDOC.getElementsByTagNameNS(WSDL_NAMESPACE,"binding");
         for (int idx = 0; idx < bindings.getLength(); idx++) {
             Element nd = (Element) bindings.item(idx);
             NodeList slist = nd.getElementsByTagNameNS(SOAP11_BINDING_NAMESPACE,"binding");
             if(slist.getLength()==0) {
                 slist = nd.getElementsByTagNameNS(SOAP12_BINDING_NAMESPACE,"binding");
             }
             if (slist.getLength() > 0) {
                 nd.getAttribute("name");
                 list.add(nd);
             }
         }
         if (list.size() > 0) {
             return list.toArray();
         }
         return new Object[0];
     }
 
     /**
      * Look at the bindings with soap operations and get the soap operations.
      * Since WSDL may describe multiple bindings and each binding may have
      * multiple soap operations, we iterate through the binding nodes with a
      * first child that is a soap binding. If a WSDL doesn't use the same
      * formatting convention, it is possible we may not get a list of all the
      * soap operations. If that is the case, getSOAPBindings() will need to be
      * changed. I should double check the WSDL spec to see what the official
      * requirement is. Another option is to get all operation nodes and check to
      * see if the first child is a soap:operation. The benefit of not getting
      * all operation nodes is WSDL could contain duplicate operations that are
      * not SOAP methods. If there are a large number of methods and half of them
      * are HTTP operations, getting all operations could slow things down.
      *
      * @return Node[]
      */
     public Object[] getOperations() {
         Object[] res = this.getSOAPBindings();
         ArrayList<Element> ops = new ArrayList<Element>();
         // first we iterate through the bindings
         for (int idx = 0; idx < res.length; idx++) {
             Element one = (Element) res[idx];
             NodeList opnodes = one.getElementsByTagNameNS(WSDL_NAMESPACE, "operation");
             // now we iterate through the operations
             for (int idz = 0; idz < opnodes.getLength(); idz++) {
                 // if the first child is soap:operation
                 // we add it to the array
                 Element child = (Element) opnodes.item(idz);
                 int numberOfSoapOperationNodes = child.getElementsByTagNameNS(SOAP11_BINDING_NAMESPACE, "operation").getLength()
                         + child.getElementsByTagNameNS(SOAP12_BINDING_NAMESPACE, "operation").getLength();
                 if (numberOfSoapOperationNodes>0) {
                     ops.add(child);
                 }
             }
         }
         return ops.toArray();
     }
 
     /**
      * return the "wsdl method name" from a soap action
      * @param soapAction the soap action
      * @return the associated "wsdl method name" or null if not found
      */
     public String getSoapActionName(String soapAction) {
         for (Map.Entry<String, String> entry : ACTIONS.entrySet()) {
             if (entry.getValue().equals(soapAction)) {
                 return entry.getKey();
             }
         }
         return null;
     }
     
     /**
      * Simple test for the class uses bidbuy.wsdl from Apache's soap driver
      * examples.
      *
      * @param args standard arguments for a main class (not used here)
      */
     public static void main(String[] args) {
         try {
             WSDLHelper help =
             // new WSDLHelper("http://localhost/WSTest/WSTest.asmx?WSDL");
             // new WSDLHelper("http://localhost/AxisWSDL.xml");
             //new WSDLHelper("http://localhost:8080/WSMyUpper.wsdl");
             //new WSDLHelper("http://localhost:8080/test.wsdl");
             new WSDLHelper("http://localhost:8080/ServiceGateway.wsdl");
             // new WSDLHelper("http://services.bio.ifi.lmu.de:1046/prothesaurus/services/BiologicalNameService?wsdl");
             long start = System.currentTimeMillis();
             help.parse();
             String[] methods = help.getWebMethods();
             System.out.println("el: " + (System.currentTimeMillis() - start));
             for (int idx = 0; idx < methods.length; idx++) {
                 System.out.println("method name: " + methods[idx]);
             }
             System.out.println("service url: " + help.getBinding());
             System.out.println("protocol: " + help.getProtocol());
             System.out.println("port=" + help.getURL().getPort());
         } catch (Exception exception) {
             System.out.println("main method catch:");
             exception.printStackTrace();
         }
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Generator.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Generator.java
index ee885acd9..1dc4eaa52 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Generator.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Generator.java
@@ -1,138 +1,138 @@
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
 
 /**
  * Description:<br>
  * <br>
  * Generator is a base interface that defines the minimum methods needed to
  * implement a concrete generator. The reason for creating this interface is
  * eventually JMeter could use the logs directly rather than pre- process the
  * logs into a JMeter .jmx file. In situations where a test plan simulates load
  * from production logs, it is more efficient for JMeter to use the logs
  * directly.
  * <p>
  * From first hand experience, loading a test plan with 10K or more Requests
  * requires a lot of memory. It's important to keep in mind this type of testing
  * is closer to functional and regression testing than the typical stress tests.
  * Typically, this kind of testing is most useful for search sites that get a
  * large number of requests per day, but the request parameters vary
  * dramatically. E-commerce sites typically have limited inventory, therefore it
  * is better to design test plans that use data from the database.
  * <p>
  *
  * @version $Revision$
  */
 
 public interface Generator {
 
     /**
      * close the generator
      */
     void close();
 
     /**
      * The host is the name of the server.
      *
-     * @param host
+     * @param host name of the server
      */
     void setHost(String host);
 
     /**
      * This is the label for the request, which is used in the logs and results.
      *
-     * @param label
+     * @param label label of the request
      */
     void setLabel(String label);
 
     /**
      * The method is the HTTP request method. It's normally POST or GET.
      *
-     * @param post_get
+     * @param post_get method of the HTTP request
      */
     void setMethod(String post_get);
 
     /**
      * Set the request parameters
      *
-     * @param params
+     * @param params request parameter
      */
     void setParams(NVPair[] params);
 
     /**
      * The path is the web page you want to test.
      *
-     * @param path
+     * @param path path of the web page
      */
     void setPath(String path);
 
     /**
      * The default port for HTTP is 80, but not all servers run on that port.
      *
      * @param port -
      *            port number
      */
     void setPort(int port);
 
     /**
      * Set the querystring for the request if the method is GET.
      *
-     * @param querystring
+     * @param querystring query string of the request
      */
     void setQueryString(String querystring);
 
     /**
      * The source logs is the location where the access log resides.
      *
-     * @param sourcefile
+     * @param sourcefile path to the access log file
      */
     void setSourceLogs(String sourcefile);
 
     /**
      * The target can be either a java.io.File or a Sampler. We make it generic,
      * so that later on we can use these classes directly from a HTTPSampler.
      *
-     * @param target
+     * @param target target to generate into
      */
     void setTarget(Object target);
 
     /**
      * The method is responsible for calling the necessary methods to generate a
      * valid request. If the generator is used to pre-process access logs, the
      * method wouldn't return anything. If the generator is used by a control
      * element, it should return the correct Sampler class with the required
      * fields set.
      *
      * @return prefilled sampler
      */
     Object generateRequest();
 
     /**
      * If the generator is converting the logs to a .jmx file, save should be
      * called.
      */
     void save();
 
     /**
      * The purpose of the reset is so Samplers can explicitly call reset to
      * create a new instance of HTTPSampler.
      *
      */
     void reset();
 }
diff --git a/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/AbstractJDBCTestElement.java b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/AbstractJDBCTestElement.java
index d42b25b52..ea0d63a17 100644
--- a/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/AbstractJDBCTestElement.java
+++ b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/AbstractJDBCTestElement.java
@@ -1,704 +1,708 @@
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
 
 package org.apache.jmeter.protocol.jdbc;
 
 import java.io.IOException;
 import java.io.UnsupportedEncodingException;
 import java.lang.reflect.Field;
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.apache.commons.collections.map.LRUMap;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * A base class for all JDBC test elements handling the basics of a SQL request.
  * 
  */
 public abstract class AbstractJDBCTestElement extends AbstractTestElement implements TestStateListener{
     private static final long serialVersionUID = 235L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String COMMA = ","; // $NON-NLS-1$
     private static final char COMMA_CHAR = ',';
 
     private static final String UNDERSCORE = "_"; // $NON-NLS-1$
 
     // String used to indicate a null value
     private static final String NULL_MARKER =
         JMeterUtils.getPropDefault("jdbcsampler.nullmarker","]NULL["); // $NON-NLS-1$
     
     private static final int MAX_OPEN_PREPARED_STATEMENTS =
         JMeterUtils.getPropDefault("jdbcsampler.maxopenpreparedstatements", 100); 
 
     private static final String INOUT = "INOUT"; // $NON-NLS-1$
 
     private static final String OUT = "OUT"; // $NON-NLS-1$
 
     // TODO - should the encoding be configurable?
     protected static final String ENCODING = "UTF-8"; // $NON-NLS-1$
 
     // key: name (lowercase) from java.sql.Types; entry: corresponding int value
     private static final Map<String, Integer> mapJdbcNameToInt;
     // read-only after class init
 
     static {
         // based on e291. Getting the Name of a JDBC Type from javaalmanac.com
         // http://javaalmanac.com/egs/java.sql/JdbcInt2Str.html
         mapJdbcNameToInt = new HashMap<String, Integer>();
 
         //Get all fields in java.sql.Types and store the corresponding int values
         Field[] fields = java.sql.Types.class.getFields();
         for (int i=0; i<fields.length; i++) {
             try {
                 String name = fields[i].getName();
                 Integer value = (Integer)fields[i].get(null);
                 mapJdbcNameToInt.put(name.toLowerCase(java.util.Locale.ENGLISH),value);
             } catch (IllegalAccessException e) {
                 throw new RuntimeException(e); // should not happen
             }
         }
     }
 
     // Query types (used to communicate with GUI)
     // N.B. These must not be changed, as they are used in the JMX files
     static final String SELECT   = "Select Statement"; // $NON-NLS-1$
     static final String UPDATE   = "Update Statement"; // $NON-NLS-1$
     static final String CALLABLE = "Callable Statement"; // $NON-NLS-1$
     static final String PREPARED_SELECT = "Prepared Select Statement"; // $NON-NLS-1$
     static final String PREPARED_UPDATE = "Prepared Update Statement"; // $NON-NLS-1$
     static final String COMMIT   = "Commit"; // $NON-NLS-1$
     static final String ROLLBACK = "Rollback"; // $NON-NLS-1$
     static final String AUTOCOMMIT_FALSE = "AutoCommit(false)"; // $NON-NLS-1$
     static final String AUTOCOMMIT_TRUE  = "AutoCommit(true)"; // $NON-NLS-1$
 
     static final String RS_STORE_AS_STRING = "Store as String"; // $NON-NLS-1$
     static final String RS_STORE_AS_OBJECT = "Store as Object"; // $NON-NLS-1$
     static final String RS_COUNT_RECORDS = "Count Records"; // $NON-NLS-1$
 
     private String query = ""; // $NON-NLS-1$
 
     private String dataSource = ""; // $NON-NLS-1$
 
     private String queryType = SELECT;
     private String queryArguments = ""; // $NON-NLS-1$
     private String queryArgumentsTypes = ""; // $NON-NLS-1$
     private String variableNames = ""; // $NON-NLS-1$
     private String resultSetHandler = RS_STORE_AS_STRING; 
     private String resultVariable = ""; // $NON-NLS-1$
     private String queryTimeout = ""; // $NON-NLS-1$
 
     /**
      *  Cache of PreparedStatements stored in a per-connection basis. Each entry of this
      *  cache is another Map mapping the statement string to the actual PreparedStatement.
      *  At one time a Connection is only held by one thread
      */
     private static final Map<Connection, Map<String, PreparedStatement>> perConnCache =
             new ConcurrentHashMap<Connection, Map<String, PreparedStatement>>();
 
     /**
      * Creates a JDBCSampler.
      */
     protected AbstractJDBCTestElement() {
     }
     
     /**
      * Execute the test element.
      * 
      * @param conn a {@link SampleResult} in case the test should sample; <code>null</code> if only execution is requested
+     * @return the result of the execute command
+     * @throws SQLException if a database error occurs
+     * @throws UnsupportedEncodingException when the result can not be converted to the required charset
+     * @throws IOException when I/O error occurs
      * @throws UnsupportedOperationException if the user provided incorrect query type 
      */
     protected byte[] execute(Connection conn) throws SQLException, UnsupportedEncodingException, IOException, UnsupportedOperationException {
         log.debug("executing jdbc");
         Statement stmt = null;
         
         try {
             // Based on query return value, get results
             String _queryType = getQueryType();
             if (SELECT.equals(_queryType)) {
                 stmt = conn.createStatement();
                 stmt.setQueryTimeout(getIntegerQueryTimeout());
                 ResultSet rs = null;
                 try {
                     rs = stmt.executeQuery(getQuery());
                     return getStringFromResultSet(rs).getBytes(ENCODING);
                 } finally {
                     close(rs);
                 }
             } else if (CALLABLE.equals(_queryType)) {
                 CallableStatement cstmt = getCallableStatement(conn);
                 int out[]=setArguments(cstmt);
                 // A CallableStatement can return more than 1 ResultSets
                 // plus a number of update counts.
                 boolean hasResultSet = cstmt.execute();
                 String sb = resultSetsToString(cstmt,hasResultSet, out);
                 return sb.getBytes(ENCODING);
             } else if (UPDATE.equals(_queryType)) {
                 stmt = conn.createStatement();
                 stmt.setQueryTimeout(getIntegerQueryTimeout());
                 stmt.executeUpdate(getQuery());
                 int updateCount = stmt.getUpdateCount();
                 String results = updateCount + " updates";
                 return results.getBytes(ENCODING);
             } else if (PREPARED_SELECT.equals(_queryType)) {
                 PreparedStatement pstmt = getPreparedStatement(conn);
                 setArguments(pstmt);
                 ResultSet rs = null;
                 try {
                     rs = pstmt.executeQuery();
                     return getStringFromResultSet(rs).getBytes(ENCODING);
                 } finally {
                     close(rs);
                 }
             } else if (PREPARED_UPDATE.equals(_queryType)) {
                 PreparedStatement pstmt = getPreparedStatement(conn);
                 setArguments(pstmt);
                 pstmt.executeUpdate();
                 String sb = resultSetsToString(pstmt,false,null);
                 return sb.getBytes(ENCODING);
             } else if (ROLLBACK.equals(_queryType)){
                 conn.rollback();
                 return ROLLBACK.getBytes(ENCODING);
             } else if (COMMIT.equals(_queryType)){
                 conn.commit();
                 return COMMIT.getBytes(ENCODING);
             } else if (AUTOCOMMIT_FALSE.equals(_queryType)){
                 conn.setAutoCommit(false);
                 return AUTOCOMMIT_FALSE.getBytes(ENCODING);
             } else if (AUTOCOMMIT_TRUE.equals(_queryType)){
                 conn.setAutoCommit(true);
                 return AUTOCOMMIT_TRUE.getBytes(ENCODING);
             } else { // User provided incorrect query type
                 throw new UnsupportedOperationException("Unexpected query type: "+_queryType);
             }
         } finally {
             close(stmt);
         }
     }
 
     private String resultSetsToString(PreparedStatement pstmt, boolean result, int[] out) throws SQLException, UnsupportedEncodingException {
         StringBuilder sb = new StringBuilder();
         int updateCount = 0;
         if (!result) {
             updateCount = pstmt.getUpdateCount();
         }
         do {
             if (result) {
                 ResultSet rs = null;
                 try {
                     rs = pstmt.getResultSet();
                     sb.append(getStringFromResultSet(rs)).append("\n"); // $NON-NLS-1$
                 } finally {
                     close(rs);
                 }
             } else {
                 sb.append(updateCount).append(" updates.\n");
             }
             result = pstmt.getMoreResults();
             if (!result) {
                 updateCount = pstmt.getUpdateCount();
             }
         } while (result || (updateCount != -1));
         if (out!=null && pstmt instanceof CallableStatement){
             ArrayList<Object> outputValues = new ArrayList<Object>();
             CallableStatement cs = (CallableStatement) pstmt;
             sb.append("Output variables by position:\n");
             for(int i=0; i < out.length; i++){
                 if (out[i]!=java.sql.Types.NULL){
                     Object o = cs.getObject(i+1);
                     outputValues.add(o);
                     sb.append("[");
                     sb.append(i+1);
                     sb.append("] ");
                     sb.append(o);
                     if( o instanceof java.sql.ResultSet && RS_COUNT_RECORDS.equals(resultSetHandler)) {
                         sb.append(" ").append(countRows((ResultSet) o)).append(" rows");
                     }
                     sb.append("\n");
                 }
             }
             String varnames[] = getVariableNames().split(COMMA);
             if(varnames.length > 0) {
             JMeterVariables jmvars = getThreadContext().getVariables();
                 for(int i = 0; i < varnames.length && i < outputValues.size(); i++) {
                     String name = varnames[i].trim();
                     if (name.length()>0){ // Save the value in the variable if present
                         Object o = outputValues.get(i);
                         if( o instanceof java.sql.ResultSet ) { 
                             ResultSet resultSet = (ResultSet) o;
                             if(RS_STORE_AS_OBJECT.equals(resultSetHandler)) {
                                 jmvars.putObject(name, o);
                             }
                             else if( RS_COUNT_RECORDS.equals(resultSetHandler)) {
                                 jmvars.put(name,o.toString()+" "+countRows(resultSet)+" rows");
                             }
                             else {
                                 jmvars.put(name, o.toString());
                             }
                         }
                         else {
                             jmvars.put(name, o == null ? null : o.toString());
                         }
                     }
                 }
             }
         }
         return sb.toString();
     }
     
     /**
      * Count rows in result set
      * @param resultSet {@link ResultSet}
      * @return number of rows in resultSet
      * @throws SQLException
      */
     private static final int countRows(ResultSet resultSet) throws SQLException {
         return resultSet.last() ? resultSet.getRow() : 0;
     }
 
     private int[] setArguments(PreparedStatement pstmt) throws SQLException, IOException {
         if (getQueryArguments().trim().length()==0) {
             return new int[]{};
         }
         String[] arguments = CSVSaveService.csvSplitString(getQueryArguments(), COMMA_CHAR);
         String[] argumentsTypes = getQueryArgumentsTypes().split(COMMA);
         if (arguments.length != argumentsTypes.length) {
             throw new SQLException("number of arguments ("+arguments.length+") and number of types ("+argumentsTypes.length+") are not equal");
         }
         int[] outputs= new int[arguments.length];
         for (int i = 0; i < arguments.length; i++) {
             String argument = arguments[i];
             String argumentType = argumentsTypes[i];
             String[] arg = argumentType.split(" ");
             String inputOutput="";
             if (arg.length > 1) {
                 argumentType = arg[1];
                 inputOutput=arg[0];
             }
             int targetSqlType = getJdbcType(argumentType);
             try {
                 if (!OUT.equalsIgnoreCase(inputOutput)){
                     if (argument.equals(NULL_MARKER)){
                         pstmt.setNull(i+1, targetSqlType);
                     } else {
                         pstmt.setObject(i+1, argument, targetSqlType);
                     }
                 }
                 if (OUT.equalsIgnoreCase(inputOutput)||INOUT.equalsIgnoreCase(inputOutput)) {
                     CallableStatement cs = (CallableStatement) pstmt;
                     cs.registerOutParameter(i+1, targetSqlType);
                     outputs[i]=targetSqlType;
                 } else {
                     outputs[i]=java.sql.Types.NULL; // can't have an output parameter type null
                 }
             } catch (NullPointerException e) { // thrown by Derby JDBC (at least) if there are no "?" markers in statement
                 throw new SQLException("Could not set argument no: "+(i+1)+" - missing parameter marker?");
             }
         }
         return outputs;
     }
 
 
     private static int getJdbcType(String jdbcType) throws SQLException {
         Integer entry = mapJdbcNameToInt.get(jdbcType.toLowerCase(java.util.Locale.ENGLISH));
         if (entry == null) {
             try {
                 entry = Integer.decode(jdbcType);
             } catch (NumberFormatException e) {
                 throw new SQLException("Invalid data type: "+jdbcType);
             }
         }
         return (entry).intValue();
     }
 
 
     private CallableStatement getCallableStatement(Connection conn) throws SQLException {
         return (CallableStatement) getPreparedStatement(conn,true);
 
     }
     private PreparedStatement getPreparedStatement(Connection conn) throws SQLException {
         return getPreparedStatement(conn,false);
     }
 
     private PreparedStatement getPreparedStatement(Connection conn, boolean callable) throws SQLException {
         Map<String, PreparedStatement> preparedStatementMap = perConnCache.get(conn);
         if (null == preparedStatementMap ) {
             @SuppressWarnings("unchecked") // LRUMap is not generic
             Map<String, PreparedStatement> lruMap = new LRUMap(MAX_OPEN_PREPARED_STATEMENTS) {
                 private static final long serialVersionUID = 1L;
                 @Override
                 protected boolean removeLRU(LinkEntry entry) {
                     PreparedStatement preparedStatement = (PreparedStatement)entry.getValue();
                     close(preparedStatement);
                     return true;
                 }
             };
             preparedStatementMap = Collections.<String, PreparedStatement>synchronizedMap(lruMap);
             // As a connection is held by only one thread, we cannot already have a 
             // preparedStatementMap put by another thread
             perConnCache.put(conn, preparedStatementMap);
         }
         PreparedStatement pstmt = preparedStatementMap.get(getQuery());
         if (null == pstmt) {
             if (callable) {
                 pstmt = conn.prepareCall(getQuery());
             } else {
                 pstmt = conn.prepareStatement(getQuery());
             }
             pstmt.setQueryTimeout(getIntegerQueryTimeout());
             // PreparedStatementMap is associated to one connection so 
             //  2 threads cannot use the same PreparedStatement map at the same time
             preparedStatementMap.put(getQuery(), pstmt);
         } else {
             int timeoutInS = getIntegerQueryTimeout();
             if(pstmt.getQueryTimeout() != timeoutInS) {
                 pstmt.setQueryTimeout(getIntegerQueryTimeout());
             }
         }
         pstmt.clearParameters();
         return pstmt;
     }
 
     private static void closeAllStatements(Collection<PreparedStatement> collection) {
         for (PreparedStatement pstmt : collection) {
             close(pstmt);
         }
     }
 
     /**
      * Gets a Data object from a ResultSet.
      *
      * @param rs
      *            ResultSet passed in from a database query
      * @return a Data object
      * @throws java.sql.SQLException
      * @throws UnsupportedEncodingException
      */
     private String getStringFromResultSet(ResultSet rs) throws SQLException, UnsupportedEncodingException {
         ResultSetMetaData meta = rs.getMetaData();
 
         StringBuilder sb = new StringBuilder();
 
         int numColumns = meta.getColumnCount();
         for (int i = 1; i <= numColumns; i++) {
             sb.append(meta.getColumnLabel(i));
             if (i==numColumns){
                 sb.append('\n');
             } else {
                 sb.append('\t');
             }
         }
         
 
         JMeterVariables jmvars = getThreadContext().getVariables();
         String varnames[] = getVariableNames().split(COMMA);
         String resultVariable = getResultVariable().trim();
         List<Map<String, Object> > results = null;
         if(resultVariable.length() > 0) {
             results = new ArrayList<Map<String,Object> >();
             jmvars.putObject(resultVariable, results);
         }
         int j = 0;
         while (rs.next()) {
             Map<String, Object> row = null;
             j++;
             for (int i = 1; i <= numColumns; i++) {
                 Object o = rs.getObject(i);
                 if(results != null) {
                     if(row == null) {
                         row = new HashMap<String, Object>(numColumns);
                         results.add(row);
                     }
                     row.put(meta.getColumnLabel(i), o);
                 }
                 if (o instanceof byte[]) {
                     o = new String((byte[]) o, ENCODING);
                 }
                 sb.append(o);
                 if (i==numColumns){
                     sb.append('\n');
                 } else {
                     sb.append('\t');
                 }
                 if (i <= varnames.length) { // i starts at 1
                     String name = varnames[i - 1].trim();
                     if (name.length()>0){ // Save the value in the variable if present
                         jmvars.put(name+UNDERSCORE+j, o == null ? null : o.toString());
                     }
                 }
             }
         }
         // Remove any additional values from previous sample
         for(int i=0; i < varnames.length; i++){
             String name = varnames[i].trim();
             if (name.length()>0 && jmvars != null){
                 final String varCount = name+"_#"; // $NON-NLS-1$
                 // Get the previous count
                 String prevCount = jmvars.get(varCount);
                 if (prevCount != null){
                     int prev = Integer.parseInt(prevCount);
                     for (int n=j+1; n <= prev; n++ ){
                         jmvars.remove(name+UNDERSCORE+n);
                     }
                 }
                 jmvars.put(varCount, Integer.toString(j)); // save the current count
             }
         }
 
         return sb.toString();
     }
 
     public static void close(Connection c) {
         try {
             if (c != null) {
                 c.close();
             }
         } catch (SQLException e) {
             log.warn("Error closing Connection", e);
         }
     }
 
     public static void close(Statement s) {
         try {
             if (s != null) {
                 s.close();
             }
         } catch (SQLException e) {
             log.warn("Error closing Statement " + s.toString(), e);
         }
     }
 
     public static void close(ResultSet rs) {
         try {
             if (rs != null) {
                 rs.close();
             }
         } catch (SQLException e) {
             log.warn("Error closing ResultSet", e);
         }
     }    
     
     /**
      * @return the integer representation queryTimeout
      */
     public int getIntegerQueryTimeout() {
         int timeout = 0;
         try {
             timeout = Integer.parseInt(queryTimeout);
         } catch (NumberFormatException nfe) {
             timeout = 0;
         }
         return timeout;
     }
 
     /**
      * @return the queryTimeout
      */
     public String getQueryTimeout() {
         return queryTimeout ;
     }
 
     /**
      * @param queryTimeout query timeout in seconds
      */
     public void setQueryTimeout(String queryTimeout) {
         this.queryTimeout = queryTimeout;
     }
 
     public String getQuery() {
         return query;
     }
 
     @Override
     public String toString() {
         StringBuilder sb = new StringBuilder(80);
         sb.append("["); // $NON-NLS-1$
         sb.append(getQueryType());
         sb.append("] "); // $NON-NLS-1$
         sb.append(getQuery());
         sb.append("\n");
         sb.append(getQueryArguments());
         sb.append("\n");
         sb.append(getQueryArgumentsTypes());
         return sb.toString();
     }
 
     /**
      * @param query
      *            The query to set.
      */
     public void setQuery(String query) {
         this.query = query;
     }
 
     /**
      * @return Returns the dataSource.
      */
     public String getDataSource() {
         return dataSource;
     }
 
     /**
      * @param dataSource
      *            The dataSource to set.
      */
     public void setDataSource(String dataSource) {
         this.dataSource = dataSource;
     }
 
     /**
      * @return Returns the queryType.
      */
     public String getQueryType() {
         return queryType;
     }
 
     /**
      * @param queryType The queryType to set.
      */
     public void setQueryType(String queryType) {
         this.queryType = queryType;
     }
 
     public String getQueryArguments() {
         return queryArguments;
     }
 
     public void setQueryArguments(String queryArguments) {
         this.queryArguments = queryArguments;
     }
 
     public String getQueryArgumentsTypes() {
         return queryArgumentsTypes;
     }
 
     public void setQueryArgumentsTypes(String queryArgumentsType) {
         this.queryArgumentsTypes = queryArgumentsType;
     }
 
     /**
      * @return the variableNames
      */
     public String getVariableNames() {
         return variableNames;
     }
 
     /**
      * @param variableNames the variableNames to set
      */
     public void setVariableNames(String variableNames) {
         this.variableNames = variableNames;
     }
 
     /**
      * @return the resultSetHandler
      */
     public String getResultSetHandler() {
         return resultSetHandler;
     }
 
     /**
      * @param resultSetHandler the resultSetHandler to set
      */
     public void setResultSetHandler(String resultSetHandler) {
         this.resultSetHandler = resultSetHandler;
     }
 
     /**
      * @return the resultVariable
      */
     public String getResultVariable() {
         return resultVariable ;
     }
 
     /**
      * @param resultVariable the variable name in which results will be stored
      */
     public void setResultVariable(String resultVariable) {
         this.resultVariable = resultVariable;
     }    
 
 
     /** 
      * {@inheritDoc}
      * @see org.apache.jmeter.testelement.TestStateListener#testStarted()
      */
     @Override
     public void testStarted() {
         testStarted("");
     }
 
     /**
      * {@inheritDoc}
      * @see org.apache.jmeter.testelement.TestStateListener#testStarted(java.lang.String)
      */
     @Override
     public void testStarted(String host) {
         cleanCache();
     }
 
     /**
      * {@inheritDoc}
      * @see org.apache.jmeter.testelement.TestStateListener#testEnded()
      */
     @Override
     public void testEnded() {
         testEnded("");
     }
 
     /**
      * {@inheritDoc}
      * @see org.apache.jmeter.testelement.TestStateListener#testEnded(java.lang.String)
      */
     @Override
     public void testEnded(String host) {
         cleanCache();
     }
     
     /**
      * Clean cache of PreparedStatements
      */
     private static final void cleanCache() {
         for (Map<String, PreparedStatement> element : perConnCache.values()) {
             closeAllStatements(element.values());
         }
         perConnCache.clear();
     }
 
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/QueueExecutor.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/QueueExecutor.java
index 770e430e6..7882bc7ba 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/QueueExecutor.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/QueueExecutor.java
@@ -1,47 +1,47 @@
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
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import javax.jms.JMSException;
 import javax.jms.Message;
 
 /**
  * Executor for (pseudo) synchronous communication. <br>
  * Created on: October 28, 2004
  *
  * @version $Revision$
  */
 public interface QueueExecutor {
     /**
      * Sends and receives a message.
      * 
      * @param request the message to send
-     * @param deliveryMode
-     * @param priority
-     * @param expiration
+     * @param deliveryMode the delivery mode to use
+     * @param priority the priority for this message
+     * @param expiration messages lifetime in ms
      * @return the received message or <code>null</code>
      * @throws JMSException
      *             in case of an exception from the messaging system
      */
     Message sendAndReceive(Message request,
             int deliveryMode, 
             int priority, 
             long expiration) throws JMSException;
 
 }
