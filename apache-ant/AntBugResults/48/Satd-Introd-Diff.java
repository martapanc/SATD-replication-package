diff --git a/src/main/org/apache/tools/ant/util/regexp/MatcherWrappedAsRegexp.java b/src/main/org/apache/tools/ant/util/regexp/MatcherWrappedAsRegexp.java
new file mode 100644
index 000000000..e40064065
--- /dev/null
+++ b/src/main/org/apache/tools/ant/util/regexp/MatcherWrappedAsRegexp.java
@@ -0,0 +1,160 @@
+/*
+ * The Apache Software License, Version 1.1
+ *
+ * Copyright (c) 2001 The Apache Software Foundation.  All rights 
+ * reserved.
+ *
+ * Redistribution and use in source and binary forms, with or without
+ * modification, are permitted provided that the following conditions
+ * are met:
+ *
+ * 1. Redistributions of source code must retain the above copyright
+ *    notice, this list of conditions and the following disclaimer. 
+ *
+ * 2. Redistributions in binary form must reproduce the above copyright
+ *    notice, this list of conditions and the following disclaimer in
+ *    the documentation and/or other materials provided with the
+ *    distribution.
+ *
+ * 3. The end-user documentation included with the redistribution, if
+ *    any, must include the following acknowlegement:  
+ *       "This product includes software developed by the 
+ *        Apache Software Foundation (http://www.apache.org/)."
+ *    Alternately, this acknowlegement may appear in the software itself,
+ *    if and wherever such third-party acknowlegements normally appear.
+ *
+ * 4. The names "The Jakarta Project", "Ant", and "Apache Software
+ *    Foundation" must not be used to endorse or promote products derived
+ *    from this software without prior written permission. For written 
+ *    permission, please contact apache@apache.org.
+ *
+ * 5. Products derived from this software may not be called "Apache"
+ *    nor may "Apache" appear in their names without prior written
+ *    permission of the Apache Group.
+ *
+ * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
+ * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
+ * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
+ * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
+ * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
+ * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
+ * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
+ * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
+ * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
+ * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
+ * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
+ * SUCH DAMAGE.
+ * ====================================================================
+ *
+ * This software consists of voluntary contributions made by many
+ * individuals on behalf of the Apache Software Foundation.  For more
+ * information on the Apache Software Foundation, please see
+ * <http://www.apache.org/>.
+ */
+package org.apache.tools.ant.util.regexp;
+
+import org.apache.tools.ant.BuildException;
+
+import java.util.Vector;
+
+/**
+ * Helper class that adapts a RegexpMatcher to a Regexp.
+ *
+ * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a> 
+ * @version $Revision$
+ */
+public class MatcherWrappedAsRegexp implements Regexp {
+    
+    private RegexpMatcher matcher;
+
+    public MatcherWrappedAsRegexp(RegexpMatcher matcher) {
+        this.matcher = matcher;
+    }
+
+    /**
+     * Set the regexp pattern from the String description.
+     */
+    public void setPattern(String pattern) throws BuildException {
+        matcher.setPattern(pattern);
+    }
+
+    /**
+     * Get a String representation of the regexp pattern
+     */
+    public String getPattern() throws BuildException {
+        return matcher.getPattern();
+    }
+
+    /**
+     * Does the given argument match the pattern?
+     */
+    public boolean matches(String argument) throws BuildException {
+        return matcher.matches(argument);
+    }
+
+    /**
+     * Returns a Vector of matched groups found in the argument.
+     *
+     * <p>Group 0 will be the full match, the rest are the
+     * parenthesized subexpressions</p>.
+     */
+    public Vector getGroups(String argument) throws BuildException {
+        return matcher.getGroups(argument);
+    }
+
+    /**
+     * Does this regular expression match the input, given
+     * certain options
+     * @param input The string to check for a match
+     * @param options The list of options for the match. See the
+     *                MATCH_ constants above.
+     */
+    public boolean matches(String input, int options) throws BuildException {
+        return matcher.matches(input, options);
+    }
+
+    /**
+     * Get the match groups from this regular expression.  The return
+     * type of the elements is always String.
+     * @param input The string to check for a match
+     * @param options The list of options for the match. See the
+     *                MATCH_ constants above.
+     */
+    public Vector getGroups(String input, int options) throws BuildException {
+        return matcher.getGroups(input, options);
+    }
+
+    /**
+     * Perform a substitution on the regular expression.
+     * @param input The string to substitute on
+     * @param argument The string which defines the substitution
+     * @param options The list of options for the match and replace. See the
+     *                MATCH_ and REPLACE_ constants above.
+     *                REPLACE_ constants will be ignored.
+     */
+    public String substitute(String input, String argument, int options)
+        throws BuildException {
+        Vector v = matcher.getGroups(input, options);
+        
+        StringBuffer result = new StringBuffer();
+        char[] to = argument.toCharArray();
+        for (int i=0; i<to.length; i++) {
+            if (to[i] == '\\') {
+                if (++i < to.length) {
+                    int value = Character.digit(to[i], 10);
+                    if (value > -1) {
+                        result.append((String) v.elementAt(value));
+                    } else {
+                        result.append(to[i]);
+                    }
+                } else {
+                    // XXX - should throw an exception instead?
+                    result.append('\\');
+                }
+            } else {
+                result.append(to[i]);
+            }
+        }
+        return result.toString();
+    }
+}
