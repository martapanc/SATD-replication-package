diff --git a/src/org/jruby/parser/Ruby19Parser.java b/src/org/jruby/parser/Ruby19Parser.java
index f226f0920f..dfc4637bdf 100644
--- a/src/org/jruby/parser/Ruby19Parser.java
+++ b/src/org/jruby/parser/Ruby19Parser.java
@@ -1,4272 +1,4281 @@
 // created by jay 1.0.2 (c) 2002-2004 ats@cs.rit.edu
 // skeleton Java 1.0 (c) 2002 ats@cs.rit.edu
 
 					// line 2 "Ruby19Parser.y"
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2008-2009 Thomas E Enebo <enebo@acm.org>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.parser;
 
 import java.io.IOException;
 
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgumentNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AssignableNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BlockAcceptingNode;
 import org.jruby.ast.BlockArgNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.DStrNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
 import org.jruby.ast.DefsNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNoArgBlockNode;
 import org.jruby.ast.FCallNoArgNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.GlobalVarNode;
 import org.jruby.ast.Hash19Node;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.InstVarNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.LambdaNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.LiteralNode;
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgn19Node;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.NilImplicitNode;
 import org.jruby.ast.NilNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.OpAsgnAndNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OptArgNode;
 import org.jruby.ast.PostExeNode;
 import org.jruby.ast.PreExe19Node;
 import org.jruby.ast.RedoNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.RestArgNode;
 import org.jruby.ast.RetryNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.SelfNode;
 import org.jruby.ast.StarNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SymbolNode;
 import org.jruby.ast.UnnamedRestArgNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZArrayNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.ast.ZYieldNode;
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.common.IRubyWarnings;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.ISourcePositionHolder;
 import org.jruby.lexer.yacc.LexerSource;
 import org.jruby.lexer.yacc.RubyYaccLexer;
 import org.jruby.lexer.yacc.RubyYaccLexer.LexState;
 import org.jruby.lexer.yacc.StrTerm;
 import org.jruby.lexer.yacc.SyntaxException;
 import org.jruby.lexer.yacc.SyntaxException.PID;
 import org.jruby.lexer.yacc.Token;
 import org.jruby.util.ByteList;
 
 public class Ruby19Parser implements RubyParser {
     protected ParserSupport19 support;
     protected RubyYaccLexer lexer;
 
     public Ruby19Parser() {
         this(new ParserSupport19());
     }
 
     public Ruby19Parser(ParserSupport19 support) {
         this.support = support;
         lexer = new RubyYaccLexer(false);
         lexer.setParserSupport(support);
         support.setLexer(lexer);
     }
 
     public void setWarnings(IRubyWarnings warnings) {
         support.setWarnings(warnings);
         lexer.setWarnings(warnings);
     }
 					// line 141 "-"
   // %token constants
   public static final int kCLASS = 257;
   public static final int kMODULE = 258;
   public static final int kDEF = 259;
   public static final int kUNDEF = 260;
   public static final int kBEGIN = 261;
   public static final int kRESCUE = 262;
   public static final int kENSURE = 263;
   public static final int kEND = 264;
   public static final int kIF = 265;
   public static final int kUNLESS = 266;
   public static final int kTHEN = 267;
   public static final int kELSIF = 268;
   public static final int kELSE = 269;
   public static final int kCASE = 270;
   public static final int kWHEN = 271;
   public static final int kWHILE = 272;
   public static final int kUNTIL = 273;
   public static final int kFOR = 274;
   public static final int kBREAK = 275;
   public static final int kNEXT = 276;
   public static final int kREDO = 277;
   public static final int kRETRY = 278;
   public static final int kIN = 279;
   public static final int kDO = 280;
   public static final int kDO_COND = 281;
   public static final int kDO_BLOCK = 282;
   public static final int kRETURN = 283;
   public static final int kYIELD = 284;
   public static final int kSUPER = 285;
   public static final int kSELF = 286;
   public static final int kNIL = 287;
   public static final int kTRUE = 288;
   public static final int kFALSE = 289;
   public static final int kAND = 290;
   public static final int kOR = 291;
   public static final int kNOT = 292;
   public static final int kIF_MOD = 293;
   public static final int kUNLESS_MOD = 294;
   public static final int kWHILE_MOD = 295;
   public static final int kUNTIL_MOD = 296;
   public static final int kRESCUE_MOD = 297;
   public static final int kALIAS = 298;
   public static final int kDEFINED = 299;
   public static final int klBEGIN = 300;
   public static final int klEND = 301;
   public static final int k__LINE__ = 302;
   public static final int k__FILE__ = 303;
   public static final int k__ENCODING__ = 304;
   public static final int kDO_LAMBDA = 305;
   public static final int tIDENTIFIER = 306;
   public static final int tFID = 307;
   public static final int tGVAR = 308;
   public static final int tIVAR = 309;
   public static final int tCONSTANT = 310;
   public static final int tCVAR = 311;
   public static final int tLABEL = 312;
   public static final int tCHAR = 313;
   public static final int tUPLUS = 314;
   public static final int tUMINUS = 315;
   public static final int tUMINUS_NUM = 316;
   public static final int tPOW = 317;
   public static final int tCMP = 318;
   public static final int tEQ = 319;
   public static final int tEQQ = 320;
   public static final int tNEQ = 321;
   public static final int tGEQ = 322;
   public static final int tLEQ = 323;
   public static final int tANDOP = 324;
   public static final int tOROP = 325;
   public static final int tMATCH = 326;
   public static final int tNMATCH = 327;
   public static final int tDOT = 328;
   public static final int tDOT2 = 329;
   public static final int tDOT3 = 330;
   public static final int tAREF = 331;
   public static final int tASET = 332;
   public static final int tLSHFT = 333;
   public static final int tRSHFT = 334;
   public static final int tCOLON2 = 335;
   public static final int tCOLON3 = 336;
   public static final int tOP_ASGN = 337;
   public static final int tASSOC = 338;
   public static final int tLPAREN = 339;
   public static final int tLPAREN2 = 340;
   public static final int tRPAREN = 341;
   public static final int tLPAREN_ARG = 342;
   public static final int tLBRACK = 343;
   public static final int tRBRACK = 344;
   public static final int tLBRACE = 345;
   public static final int tLBRACE_ARG = 346;
   public static final int tSTAR = 347;
   public static final int tSTAR2 = 348;
   public static final int tAMPER = 349;
   public static final int tAMPER2 = 350;
   public static final int tTILDE = 351;
   public static final int tPERCENT = 352;
   public static final int tDIVIDE = 353;
   public static final int tPLUS = 354;
   public static final int tMINUS = 355;
   public static final int tLT = 356;
   public static final int tGT = 357;
   public static final int tPIPE = 358;
   public static final int tBANG = 359;
   public static final int tCARET = 360;
   public static final int tLCURLY = 361;
   public static final int tRCURLY = 362;
   public static final int tBACK_REF2 = 363;
   public static final int tSYMBEG = 364;
   public static final int tSTRING_BEG = 365;
   public static final int tXSTRING_BEG = 366;
   public static final int tREGEXP_BEG = 367;
   public static final int tWORDS_BEG = 368;
   public static final int tQWORDS_BEG = 369;
   public static final int tSTRING_DBEG = 370;
   public static final int tSTRING_DVAR = 371;
   public static final int tSTRING_END = 372;
   public static final int tLAMBDA = 373;
   public static final int tLAMBEG = 374;
   public static final int tNTH_REF = 375;
   public static final int tBACK_REF = 376;
   public static final int tSTRING_CONTENT = 377;
   public static final int tINTEGER = 378;
   public static final int tFLOAT = 379;
   public static final int tREGEXP_END = 380;
   public static final int tLOWEST = 381;
   public static final int yyErrorCode = 256;
 
   /** number of final state.
     */
   protected static final int yyFinal = 1;
 
   /** parser tables.
       Order is mandated by <i>jay</i>.
     */
   protected static final short[] yyLhs = {
-//yyLhs 548
-    -1,   120,     0,   117,   118,   118,   118,   118,   119,   123,
-   119,    34,    33,    35,    35,    35,    35,   124,    36,    36,
-    36,    36,    36,    36,    36,    36,    36,    36,    36,    36,
-    36,    36,    36,    36,    36,    36,    36,    36,    36,    36,
-    36,    37,    37,    37,    37,    37,    37,    41,    32,    32,
-    32,    32,    32,    55,    55,    55,   126,    94,    40,    40,
-    40,    40,    40,    40,    40,    40,    95,    95,   106,   106,
-    96,    96,    96,    96,    96,    96,    96,    96,    96,    96,
-    67,    67,    81,    81,    85,    85,    68,    68,    68,    68,
-    68,    68,    68,    68,    73,    73,    73,    73,    73,    73,
-    73,    73,     7,     7,    31,    31,    31,     8,     8,     8,
-     8,     8,    99,    99,   100,   100,    57,   127,    57,     9,
+//yyLhs 550
+    -1,   121,     0,   118,   119,   119,   119,   119,   120,   124,
+   120,    35,    34,    36,    36,    36,    36,   125,    37,    37,
+    37,    37,    37,    37,    37,    37,    37,    37,    37,    37,
+    37,    37,    37,    37,    37,    37,    37,    37,    37,    37,
+    37,    32,    32,    38,    38,    38,    38,    38,    38,    42,
+    33,    33,    33,    33,    33,    56,    56,    56,   127,    95,
+    41,    41,    41,    41,    41,    41,    41,    41,    96,    96,
+   107,   107,    97,    97,    97,    97,    97,    97,    97,    97,
+    97,    97,    68,    68,    82,    82,    86,    86,    69,    69,
+    69,    69,    69,    69,    69,    69,    74,    74,    74,    74,
+    74,    74,    74,    74,     7,     7,    31,    31,    31,     8,
+     8,     8,     8,     8,   100,   100,   101,   101,    58,   128,
+    58,     9,     9,     9,     9,     9,     9,     9,     9,     9,
      9,     9,     9,     9,     9,     9,     9,     9,     9,     9,
      9,     9,     9,     9,     9,     9,     9,     9,     9,     9,
-     9,     9,     9,     9,     9,     9,     9,     9,   115,   115,
-   115,   115,   115,   115,   115,   115,   115,   115,   115,   115,
-   115,   115,   115,   115,   115,   115,   115,   115,   115,   115,
-   115,   115,   115,   115,   115,   115,   115,   115,   115,   115,
-   115,   115,   115,   115,   115,   115,   115,   115,   115,   115,
-    38,    38,    38,    38,    38,    38,    38,    38,    38,    38,
-    38,    38,    38,    38,    38,    38,    38,    38,    38,    38,
-    38,    38,    38,    38,    38,    38,    38,    38,    38,    38,
-    38,    38,    38,    38,    38,    38,    38,    38,    38,    38,
-    38,    38,    38,    38,    38,    69,    72,    72,    72,    72,
-    49,    53,    53,   109,   109,    47,    47,    47,    47,    47,
-   129,    51,    88,    87,    87,    87,    75,    75,    75,    75,
-    66,    66,    66,    39,    39,    39,    39,    39,    39,    39,
-    39,    39,    39,   130,    39,    39,    39,    39,    39,    39,
+   116,   116,   116,   116,   116,   116,   116,   116,   116,   116,
+   116,   116,   116,   116,   116,   116,   116,   116,   116,   116,
+   116,   116,   116,   116,   116,   116,   116,   116,   116,   116,
+   116,   116,   116,   116,   116,   116,   116,   116,   116,   116,
+   116,   116,    39,    39,    39,    39,    39,    39,    39,    39,
     39,    39,    39,    39,    39,    39,    39,    39,    39,    39,
-    39,    39,    39,   132,   134,    39,   135,   136,    39,    39,
-    39,   137,   138,    39,   139,    39,   141,   142,    39,   143,
-    39,   144,    39,   145,   146,    39,    39,    39,    39,    39,
-    42,   131,   131,   131,   133,   133,    45,    45,    43,    43,
-   108,   108,   110,   110,    80,    80,   111,   111,   111,   111,
-   111,   111,   111,   111,   111,    63,    63,    63,    63,    63,
-    63,    63,    63,    63,    63,    63,    63,    63,    63,    63,
-    65,    65,    64,    64,    64,   103,   103,   102,   102,   112,
-   112,   147,   105,    62,    62,   104,   104,   148,    93,    54,
-    54,    54,    24,    24,    24,    24,    24,    24,    24,    24,
-    24,   149,    92,   150,    92,    70,    44,    44,    97,    97,
-    71,    71,    71,    46,    46,    48,    48,    28,    28,    28,
-    16,    17,    17,    17,    18,    19,    20,    25,    25,    77,
-    77,    27,    27,    26,    26,    76,    76,    21,    21,    22,
-    22,    23,   151,    23,   152,    23,    58,    58,    58,    58,
-     3,     2,     2,     2,     2,    30,    29,    29,    29,    29,
-     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
-     1,     1,    52,    98,    59,    59,    50,   153,    50,    50,
-    61,    61,    60,    60,    60,    60,    60,    60,    60,    60,
-    60,    60,    60,    60,    60,    60,    60,   116,   116,   116,
-   116,    10,    10,   101,   101,    78,    78,    56,   107,    86,
-    86,    79,    79,    12,    12,    14,    14,    13,    13,    91,
-    90,    90,    15,   154,    15,    84,    84,    82,    82,    83,
-    83,     4,     4,     4,     5,     5,     5,     5,     6,     6,
-     6,    11,    11,   121,   121,   125,   125,   113,   114,   128,
-   128,   128,   140,   140,   122,   122,    74,    89,
+    39,    39,    39,    39,    39,    39,    39,    39,    39,    39,
+    39,    39,    39,    39,    39,    39,    39,    39,    39,    39,
+    39,    39,    39,    39,    39,    39,    39,    70,    73,    73,
+    73,    73,    50,    54,    54,   110,   110,    48,    48,    48,
+    48,    48,   130,    52,    89,    88,    88,    88,    76,    76,
+    76,    76,    67,    67,    67,    40,    40,    40,    40,    40,
+    40,    40,    40,    40,    40,   131,    40,    40,    40,    40,
+    40,    40,    40,    40,    40,    40,    40,    40,    40,    40,
+    40,    40,    40,    40,    40,   133,   135,    40,   136,   137,
+    40,    40,    40,   138,   139,    40,   140,    40,   142,   143,
+    40,   144,    40,   145,    40,   146,   147,    40,    40,    40,
+    40,    40,    43,   132,   132,   132,   134,   134,    46,    46,
+    44,    44,   109,   109,   111,   111,    81,    81,   112,   112,
+   112,   112,   112,   112,   112,   112,   112,    64,    64,    64,
+    64,    64,    64,    64,    64,    64,    64,    64,    64,    64,
+    64,    64,    66,    66,    65,    65,    65,   104,   104,   103,
+   103,   113,   113,   148,   106,    63,    63,   105,   105,   149,
+    94,    55,    55,    55,    24,    24,    24,    24,    24,    24,
+    24,    24,    24,   150,    93,   151,    93,    71,    45,    45,
+    98,    98,    72,    72,    72,    47,    47,    49,    49,    28,
+    28,    28,    16,    17,    17,    17,    18,    19,    20,    25,
+    25,    78,    78,    27,    27,    26,    26,    77,    77,    21,
+    21,    22,    22,    23,   152,    23,   153,    23,    59,    59,
+    59,    59,     3,     2,     2,     2,     2,    30,    29,    29,
+    29,    29,     1,     1,     1,     1,     1,     1,     1,     1,
+     1,     1,     1,     1,    53,    99,    60,    60,    51,   154,
+    51,    51,    62,    62,    61,    61,    61,    61,    61,    61,
+    61,    61,    61,    61,    61,    61,    61,    61,    61,   117,
+   117,   117,   117,    10,    10,   102,   102,    79,    79,    57,
+   108,    87,    87,    80,    80,    12,    12,    14,    14,    13,
+    13,    92,    91,    91,    15,   155,    15,    85,    85,    83,
+    83,    84,    84,     4,     4,     4,     5,     5,     5,     5,
+     6,     6,     6,    11,    11,   122,   122,   126,   126,   114,
+   115,   129,   129,   129,   141,   141,   123,   123,    75,    90,
     }, yyLen = {
-//yyLen 548
+//yyLen 550
      2,     0,     2,     2,     1,     1,     3,     2,     1,     0,
      5,     4,     2,     1,     1,     3,     2,     0,     4,     3,
-     3,     3,     2,     3,     3,     3,     3,     3,     4,     3,
+     3,     3,     2,     3,     3,     3,     3,     3,     4,     1,
      3,     3,     6,     5,     5,     5,     3,     3,     3,     3,
-     1,     1,     3,     3,     3,     2,     1,     1,     1,     1,
-     2,     2,     2,     1,     4,     4,     0,     5,     2,     3,
-     4,     5,     4,     5,     2,     2,     1,     3,     1,     3,
-     1,     2,     3,     5,     2,     4,     2,     4,     1,     3,
-     1,     3,     2,     3,     1,     3,     1,     4,     3,     3,
-     3,     3,     2,     1,     1,     4,     3,     3,     3,     3,
-     2,     1,     1,     1,     2,     1,     3,     1,     1,     1,
-     1,     1,     1,     1,     1,     1,     1,     0,     4,     1,
-     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
+     1,     3,     3,     1,     3,     3,     3,     2,     1,     1,
+     1,     1,     2,     2,     2,     1,     4,     4,     0,     5,
+     2,     3,     4,     5,     4,     5,     2,     2,     1,     3,
+     1,     3,     1,     2,     3,     5,     2,     4,     2,     4,
+     1,     3,     1,     3,     2,     3,     1,     3,     1,     4,
+     3,     3,     3,     3,     2,     1,     1,     4,     3,     3,
+     3,     3,     2,     1,     1,     1,     2,     1,     3,     1,
+     1,     1,     1,     1,     1,     1,     1,     1,     1,     0,
+     4,     1,     1,     1,     1,     1,     1,     1,     1,     1,
      1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
      1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
      1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
      1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
      1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
      1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
-     3,     5,     3,     5,     6,     5,     5,     5,     5,     4,
-     3,     3,     3,     3,     3,     3,     3,     3,     3,     4,
-     4,     2,     2,     3,     3,     3,     3,     3,     3,     3,
-     3,     3,     3,     3,     3,     3,     2,     2,     3,     3,
-     3,     3,     3,     6,     1,     1,     1,     2,     4,     2,
-     3,     1,     1,     1,     1,     1,     2,     2,     4,     1,
-     0,     2,     2,     2,     1,     1,     1,     2,     3,     4,
-     3,     4,     2,     1,     1,     1,     1,     1,     1,     1,
-     1,     1,     3,     0,     4,     3,     3,     2,     3,     3,
-     1,     4,     3,     1,     5,     4,     3,     2,     1,     2,
-     2,     6,     6,     0,     0,     7,     0,     0,     7,     5,
-     4,     0,     0,     9,     0,     6,     0,     0,     8,     0,
-     5,     0,     6,     0,     0,     9,     1,     1,     1,     1,
-     1,     1,     1,     2,     1,     1,     1,     5,     1,     2,
-     1,     1,     1,     3,     1,     3,     1,     4,     6,     3,
-     5,     2,     4,     1,     3,     6,     8,     4,     6,     4,
-     2,     6,     2,     4,     6,     2,     4,     2,     4,     1,
-     1,     1,     3,     1,     4,     1,     2,     1,     3,     1,
-     1,     0,     3,     4,     2,     3,     3,     0,     5,     2,
-     4,     4,     2,     4,     4,     3,     3,     3,     2,     1,
-     4,     0,     5,     0,     5,     5,     1,     1,     6,     0,
-     1,     1,     1,     2,     1,     2,     1,     1,     1,     1,
-     1,     1,     1,     2,     3,     3,     3,     3,     3,     0,
-     3,     1,     2,     3,     3,     0,     3,     0,     2,     0,
-     2,     1,     0,     3,     0,     4,     1,     1,     1,     1,
-     2,     1,     1,     1,     1,     3,     1,     1,     2,     2,
-     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
-     1,     1,     1,     1,     1,     1,     1,     0,     4,     2,
-     3,     2,     6,     8,     4,     6,     4,     6,     2,     4,
-     6,     2,     4,     2,     4,     1,     0,     1,     1,     1,
-     1,     1,     1,     1,     3,     1,     3,     3,     3,     1,
-     3,     1,     3,     1,     1,     2,     1,     1,     1,     2,
-     2,     0,     1,     0,     4,     1,     2,     1,     3,     3,
-     2,     1,     1,     1,     1,     1,     1,     1,     1,     1,
-     1,     1,     1,     0,     1,     0,     1,     2,     2,     0,
-     1,     1,     1,     1,     1,     2,     0,     0,
+     1,     1,     3,     5,     3,     5,     6,     5,     5,     5,
+     5,     4,     3,     3,     3,     3,     3,     3,     3,     3,
+     3,     4,     4,     2,     2,     3,     3,     3,     3,     3,
+     3,     3,     3,     3,     3,     3,     3,     3,     2,     2,
+     3,     3,     3,     3,     3,     6,     1,     1,     1,     2,
+     4,     2,     3,     1,     1,     1,     1,     1,     2,     2,
+     4,     1,     0,     2,     2,     2,     1,     1,     1,     2,
+     3,     4,     3,     4,     2,     1,     1,     1,     1,     1,
+     1,     1,     1,     1,     3,     0,     4,     3,     3,     2,
+     3,     3,     1,     4,     3,     1,     5,     4,     3,     2,
+     1,     2,     2,     6,     6,     0,     0,     7,     0,     0,
+     7,     5,     4,     0,     0,     9,     0,     6,     0,     0,
+     8,     0,     5,     0,     6,     0,     0,     9,     1,     1,
+     1,     1,     1,     1,     1,     2,     1,     1,     1,     5,
+     1,     2,     1,     1,     1,     3,     1,     3,     1,     4,
+     6,     3,     5,     2,     4,     1,     3,     6,     8,     4,
+     6,     4,     2,     6,     2,     4,     6,     2,     4,     2,
+     4,     1,     1,     1,     3,     1,     4,     1,     2,     1,
+     3,     1,     1,     0,     3,     4,     2,     3,     3,     0,
+     5,     2,     4,     4,     2,     4,     4,     3,     3,     3,
+     2,     1,     4,     0,     5,     0,     5,     5,     1,     1,
+     6,     0,     1,     1,     1,     2,     1,     2,     1,     1,
+     1,     1,     1,     1,     1,     2,     3,     3,     3,     3,
+     3,     0,     3,     1,     2,     3,     3,     0,     3,     0,
+     2,     0,     2,     1,     0,     3,     0,     4,     1,     1,
+     1,     1,     2,     1,     1,     1,     1,     3,     1,     1,
+     2,     2,     1,     1,     1,     1,     1,     1,     1,     1,
+     1,     1,     1,     1,     1,     1,     1,     1,     1,     0,
+     4,     2,     3,     2,     6,     8,     4,     6,     4,     6,
+     2,     4,     6,     2,     4,     2,     4,     1,     0,     1,
+     1,     1,     1,     1,     1,     1,     3,     1,     3,     3,
+     3,     1,     3,     1,     3,     1,     1,     2,     1,     1,
+     1,     2,     2,     0,     1,     0,     4,     1,     2,     1,
+     3,     3,     2,     1,     1,     1,     1,     1,     1,     1,
+     1,     1,     1,     1,     1,     0,     1,     0,     1,     2,
+     2,     0,     1,     1,     1,     1,     1,     2,     0,     0,
     }, yyDefRed = {
-//yyDefRed 955
+//yyDefRed 959
      1,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,   293,   296,     0,     0,     0,   318,   319,     0,
-     0,     0,   456,   455,   457,   458,     0,     0,     0,     9,
-     0,   460,   459,   461,     0,     0,   452,   451,     0,   454,
-   411,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,   427,   429,   429,     0,     0,   371,   464,
-   465,   446,   447,     0,   408,     0,   264,     0,   412,   265,
-   266,     0,   267,   268,   263,   407,   409,    41,     0,     0,
-     0,     0,     0,     0,   269,     0,    49,     0,     0,    80,
-     0,     4,     0,     0,    66,     0,     2,     0,     5,     7,
-   316,   317,   280,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,   462,     0,   105,     0,   320,     0,   270,
-   309,   158,   169,   159,   182,   155,   175,   165,   164,   180,
-   163,   162,   157,   183,   167,   156,   170,   174,   176,   168,
-   161,   177,   184,   179,     0,     0,     0,     0,   154,   173,
-   172,   185,   186,   187,   188,   189,   153,   160,   151,   152,
-     0,     0,     0,     0,   109,     0,   143,   144,   140,   122,
-   123,   124,   131,   128,   130,   125,   126,   145,   146,   132,
-   133,   513,   137,   136,   121,   142,   139,   138,   134,   135,
-   129,   127,   119,   141,   120,   147,   311,   110,     0,   512,
-   111,   178,   171,   181,   166,   148,   149,   150,   107,   108,
-   113,   112,   115,     0,   114,   116,     0,     0,     0,     0,
-     0,    13,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,   542,   543,     0,     0,     0,   544,     0,     0,     0,
-     0,     0,     0,   330,   331,     0,     0,     0,     0,     0,
-     0,   245,    51,     0,     0,     0,   517,   249,    52,    50,
-     0,    65,     0,     0,   388,    64,     0,   536,     0,     0,
-    17,     0,     0,     0,   211,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,     0,     0,   236,     0,     0,     0,
-   515,     0,     0,     0,     0,     0,     0,     0,     0,   227,
-    45,   226,   443,   442,   444,   440,   441,     0,     0,     0,
-     0,     0,     0,     0,     0,   290,     0,   393,   391,   382,
-     0,   287,   413,   289,     0,     0,     0,     0,     0,     0,
+     0,     0,   295,   298,     0,     0,     0,   320,   321,     0,
+     0,     0,   458,   457,   459,   460,     0,     0,     0,     9,
+     0,   462,   461,   463,     0,     0,   454,   453,     0,   456,
+   413,     0,     0,     0,     0,     0,     0,     0,     0,     0,
+     0,     0,     0,   429,   431,   431,     0,     0,   373,   466,
+   467,   448,   449,     0,   410,     0,   266,     0,   414,   267,
+   268,     0,   269,   270,   265,   409,   411,    29,    43,     0,
+     0,     0,     0,     0,     0,   271,     0,    51,     0,     0,
+    82,     0,     4,     0,     0,    68,     0,     2,     0,     5,
+     7,   318,   319,   282,     0,     0,     0,     0,     0,     0,
+     0,     0,     0,     0,   464,     0,   107,     0,   322,     0,
+   272,   311,   160,   171,   161,   184,   157,   177,   167,   166,
+   182,   165,   164,   159,   185,   169,   158,   172,   176,   178,
+   170,   163,   179,   186,   181,     0,     0,     0,     0,   156,
+   175,   174,   187,   188,   189,   190,   191,   155,   162,   153,
+   154,     0,     0,     0,     0,   111,     0,   145,   146,   142,
+   124,   125,   126,   133,   130,   132,   127,   128,   147,   148,
+   134,   135,   515,   139,   138,   123,   144,   141,   140,   136,
+   137,   131,   129,   121,   143,   122,   149,   313,   112,     0,
+   514,   113,   180,   173,   183,   168,   150,   151,   152,   109,
+   110,   115,   114,   117,     0,   116,   118,     0,     0,     0,
+     0,     0,    13,     0,     0,     0,     0,     0,     0,     0,
+     0,     0,   544,   545,     0,     0,     0,   546,     0,     0,
+     0,     0,     0,     0,   332,   333,     0,     0,     0,     0,
+     0,     0,   247,    53,     0,     0,     0,   519,   251,    54,
+    52,     0,    67,     0,     0,   390,    66,     0,   538,     0,
+     0,    17,     0,     0,     0,   213,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,     0,     0,   238,     0,     0,
+     0,   517,     0,     0,     0,     0,     0,     0,     0,     0,
+   229,    47,   228,   445,   444,   446,   442,   443,     0,     0,
+     0,     0,     0,     0,     0,     0,   292,     0,   395,   393,
+   384,     0,   289,   415,   291,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,     0,     0,     0,   377,   379,     0,
-     0,     0,     0,     0,     0,    82,     0,     0,     0,     0,
-     0,     0,     3,     0,     0,   448,   449,     0,   102,     0,
-   104,     0,   467,   304,   466,     0,     0,     0,     0,     0,
-     0,   531,   532,   313,   117,     0,     0,     0,   272,    12,
-     0,     0,   322,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,     0,   545,     0,     0,     0,     0,
-     0,     0,   301,   520,   257,   252,     0,     0,   246,   255,
-     0,   247,     0,   282,     0,   251,   244,   243,     0,     0,
-   286,    44,    19,    21,    20,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,     0,     0,   275,     0,     0,   278,
-     0,   540,   237,     0,   239,   516,   279,     0,    84,     0,
-     0,     0,     0,     0,   434,   432,   445,   431,   430,   414,
-   428,   415,   416,   417,   418,   421,     0,   423,   424,     0,
-     0,   489,   488,   487,   490,     0,     0,   504,   503,   508,
-   507,   493,     0,     0,     0,   501,     0,     0,     0,     0,
-   485,   495,   491,     0,     0,    56,    59,    23,    24,    25,
-    26,    27,    42,    43,     0,     0,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,     0,     0,     0,   379,   381,
+     0,     0,     0,     0,     0,     0,    84,     0,     0,     0,
+     0,     0,     0,     3,     0,     0,   450,   451,     0,   104,
+     0,   106,     0,   469,   306,   468,     0,     0,     0,     0,
+     0,     0,   533,   534,   315,   119,     0,     0,     0,   274,
+    12,     0,     0,   324,     0,     0,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,     0,   547,     0,     0,     0,
+     0,     0,     0,   303,   522,   259,   254,     0,     0,   248,
+   257,     0,   249,     0,   284,     0,   253,   246,   245,     0,
+     0,   288,    46,    19,    21,    20,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,     0,     0,   277,     0,     0,
+   280,     0,   542,   239,     0,   241,   518,   281,     0,    86,
+     0,     0,     0,     0,     0,   436,   434,   447,   433,   432,
+   416,   430,   417,   418,   419,   420,   423,     0,   425,   426,
+     0,     0,   491,   490,   489,   492,     0,     0,   506,   505,
+   510,   509,   495,     0,     0,     0,   503,     0,     0,     0,
+     0,   487,   497,   493,     0,     0,    58,    61,    23,    24,
+    25,    26,    27,    44,    45,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,   526,     0,     0,   527,   386,     0,     0,     0,
-     0,   385,     0,   387,     0,   524,   525,     0,     0,    36,
-     0,     0,    29,     0,    37,   256,     0,     0,     0,     0,
-    83,    30,    39,     0,    31,     0,     6,     0,   469,     0,
-     0,     0,     0,     0,     0,   106,     0,     0,     0,     0,
-     0,     0,     0,     0,   401,     0,     0,   402,     0,     0,
-   328,     0,     0,   323,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,   300,   325,   294,   324,   297,     0,     0,
-     0,     0,     0,     0,   519,     0,     0,     0,   253,   518,
-   281,   537,   240,   285,    18,     0,     0,    28,     0,     0,
-     0,     0,   274,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,   420,   422,   426,     0,   492,     0,     0,
-   332,     0,   334,     0,     0,   505,   509,     0,   483,     0,
-   365,   374,     0,     0,   372,     0,   478,     0,   481,   363,
-     0,   361,     0,   360,     0,     0,     0,     0,     0,     0,
-   242,     0,   383,   241,     0,     0,   384,     0,     0,     0,
-    54,   380,    55,   381,     0,     0,     0,    81,     0,     0,
-     0,   307,     0,     0,   390,   310,   514,     0,   471,     0,
-   314,   118,     0,     0,   404,   329,     0,    11,   406,     0,
-   326,     0,     0,     0,     0,     0,     0,   299,     0,     0,
-     0,     0,     0,     0,   259,   248,   284,    10,   238,    85,
-     0,     0,   436,   437,   438,   433,   439,   497,     0,     0,
-     0,     0,   494,     0,     0,   510,   369,     0,   367,   370,
-     0,     0,     0,     0,   496,     0,   502,     0,     0,     0,
-     0,     0,     0,   359,     0,   499,     0,     0,     0,     0,
-     0,    33,     0,    34,     0,    61,    35,     0,     0,    63,
-     0,   538,     0,     0,     0,     0,     0,     0,   468,   305,
-   470,   312,     0,     0,     0,     0,     0,   403,     0,   405,
-     0,   291,     0,   292,   258,     0,     0,     0,   302,   435,
-   333,     0,     0,     0,   335,   373,     0,   484,     0,   376,
-   375,     0,   476,     0,   474,     0,   479,   482,     0,     0,
-   357,     0,     0,   352,     0,   355,   362,   394,   392,     0,
-     0,   378,    32,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,   396,   395,   397,   295,   298,     0,     0,
-     0,     0,     0,   368,     0,     0,     0,     0,     0,     0,
-     0,   364,     0,     0,     0,     0,   500,    57,   308,     0,
-     0,     0,     0,     0,     0,   398,     0,     0,     0,     0,
-   477,     0,   472,   475,   480,   277,     0,   358,     0,   349,
-     0,   347,     0,   353,   356,   315,     0,   327,   303,     0,
-     0,     0,     0,     0,     0,     0,     0,   473,   351,     0,
-   345,   348,   354,     0,   346,
+     0,     0,     0,   528,     0,     0,   529,   388,     0,     0,
+     0,     0,   387,     0,   389,     0,   526,   527,     0,     0,
+    36,     0,     0,    42,    41,     0,    37,   258,     0,     0,
+     0,     0,     0,    85,    30,    39,     0,    31,     0,     6,
+     0,   471,     0,     0,     0,     0,     0,     0,   108,     0,
+     0,     0,     0,     0,     0,     0,     0,   403,     0,     0,
+   404,     0,     0,   330,     0,     0,   325,     0,     0,     0,
+     0,     0,     0,     0,     0,     0,   302,   327,   296,   326,
+   299,     0,     0,     0,     0,     0,     0,   521,     0,     0,
+     0,   255,   520,   283,   539,   242,   287,    18,     0,     0,
+    28,     0,     0,     0,     0,   276,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,     0,   422,   424,   428,     0,
+   494,     0,     0,   334,     0,   336,     0,     0,   507,   511,
+     0,   485,     0,   367,   376,     0,     0,   374,     0,   480,
+     0,   483,   365,     0,   363,     0,   362,     0,     0,     0,
+     0,     0,     0,   244,     0,   385,   243,     0,     0,   386,
+     0,     0,     0,    56,   382,    57,   383,     0,     0,     0,
+     0,    83,     0,     0,     0,   309,     0,     0,   392,   312,
+   516,     0,   473,     0,   316,   120,     0,     0,   406,   331,
+     0,    11,   408,     0,   328,     0,     0,     0,     0,     0,
+     0,   301,     0,     0,     0,     0,     0,     0,   261,   250,
+   286,    10,   240,    87,     0,     0,   438,   439,   440,   435,
+   441,   499,     0,     0,     0,     0,   496,     0,     0,   512,
+   371,     0,   369,   372,     0,     0,     0,     0,   498,     0,
+   504,     0,     0,     0,     0,     0,     0,   361,     0,   501,
+     0,     0,     0,     0,     0,    33,     0,    34,     0,    63,
+    35,     0,     0,    65,     0,   540,     0,     0,     0,     0,
+     0,     0,   470,   307,   472,   314,     0,     0,     0,     0,
+     0,   405,     0,   407,     0,   293,     0,   294,   260,     0,
+     0,     0,   304,   437,   335,     0,     0,     0,   337,   375,
+     0,   486,     0,   378,   377,     0,   478,     0,   476,     0,
+   481,   484,     0,     0,   359,     0,     0,   354,     0,   357,
+   364,   396,   394,     0,     0,   380,    32,     0,     0,     0,
+     0,     0,     0,     0,     0,     0,     0,   398,   397,   399,
+   297,   300,     0,     0,     0,     0,     0,   370,     0,     0,
+     0,     0,     0,     0,     0,   366,     0,     0,     0,     0,
+   502,    59,   310,     0,     0,     0,     0,     0,     0,   400,
+     0,     0,     0,     0,   479,     0,   474,   477,   482,   279,
+     0,   360,     0,   351,     0,   349,     0,   355,   358,   317,
+     0,   329,   305,     0,     0,     0,     0,     0,     0,     0,
+     0,   475,   353,     0,   347,   350,   356,     0,   348,
     }, yyDgoto = {
-//yyDgoto 155
-     1,   223,   305,    64,    65,   594,   561,   115,   211,   555,
-   501,   393,   502,   503,   504,   198,    66,    67,    68,    69,
-    70,   308,   307,   478,    71,    72,    73,   486,    74,    75,
-    76,   116,    77,   217,   218,   219,   220,    79,    80,    81,
-    82,   225,   275,   740,   884,   741,   733,   436,   737,   563,
-   383,   261,    84,   702,    85,    86,   505,   213,   765,   227,
-   600,   601,   507,   790,   691,   692,   574,    88,    89,   253,
-   414,   606,   285,   228,   221,   254,   314,   312,   508,   509,
-   671,    92,   255,   256,   292,   469,   792,   428,   257,   429,
-   678,   775,   321,   358,   516,    93,    94,   397,   229,   214,
-   215,   511,   777,   681,   684,   315,   283,   795,   245,   438,
-   672,   673,   778,   433,   708,   200,   512,    96,    97,    98,
-     2,   234,   235,   272,   445,   434,   695,   603,   462,   262,
-   458,   403,   237,   625,   750,   238,   751,   633,   888,   590,
-   404,   587,   817,   388,   390,   602,   822,   316,   550,   514,
-   513,   662,   661,   589,   389,
+//yyDgoto 156
+     1,   224,   306,    64,    65,   597,   562,   116,   212,   556,
+   502,   394,   503,   504,   505,   199,    66,    67,    68,    69,
+    70,   309,   308,   479,    71,    72,    73,   487,    74,    75,
+    76,   117,    77,    78,   218,   219,   220,   221,    80,    81,
+    82,    83,   226,   276,   744,   888,   745,   737,   437,   741,
+   564,   384,   262,    85,   705,    86,    87,   506,   214,   769,
+   228,   603,   604,   508,   794,   694,   695,   576,    89,    90,
+   254,   415,   609,   286,   229,   222,   255,   315,   313,   509,
+   510,   674,    93,   256,   257,   293,   470,   796,   429,   258,
+   430,   681,   779,   322,   359,   517,    94,    95,   398,   230,
+   215,   216,   512,   781,   684,   687,   316,   284,   799,   246,
+   439,   675,   676,   782,   434,   711,   201,   513,    97,    98,
+    99,     2,   235,   236,   273,   446,   435,   698,   606,   463,
+   263,   459,   404,   238,   628,   754,   239,   755,   636,   892,
+   593,   405,   590,   821,   389,   391,   605,   826,   317,   551,
+   515,   514,   665,   664,   592,   390,
     }, yySindex = {
-//yySindex 955
-     0,     0, 14462, 14833,  5372, 17539, 18247, 18139, 14586, 16801,
- 16801, 12712,     0,     0, 17293, 15079, 15079,     0,     0, 15079,
-  -234,  -211,     0,     0,     0,     0,    55, 18031,   123,     0,
-  -304,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0, 16924, 16924,   -63,  -104, 14710, 16801, 15448, 15817,  5904,
- 16924, 17047, 18354,     0,     0,     0,   221,   250,     0,     0,
-     0,     0,     0,     0,     0,  -146,     0,   -72,     0,     0,
-     0,  -170,     0,     0,     0,     0,     0,     0,  1205,   215,
-  4781,     0,    46,   329,     0,  -187,     0,   -30,   287,     0,
-   297,     0, 17416,   300,     0,   149,     0,   103,     0,     0,
-     0,     0,     0,  -234,  -211,   178,   123,     0,     0,   272,
- 16801,    33, 14586,     0,  -146,     0,    84,     0,   448,     0,
+//yySindex 959
+     0,     0, 14478, 14849,  5477, 17432, 18140, 18032, 14602, 16817,
+ 16817, 12817,     0,     0, 12990, 15095, 15095,     0,     0, 15095,
+  -207,  -137,     0,     0,     0,     0,   122, 17924,   164,     0,
+  -142,     0,     0,     0,     0,     0,     0,     0,     0,     0,
+     0, 16940, 16940,  -191,   -53, 14726, 16817, 15464, 15833,  3918,
+ 16940, 17063, 18247,     0,     0,     0,   247,   256,     0,     0,
+     0,     0,     0,     0,     0,  -183,     0,   -58,     0,     0,
+     0,  -236,     0,     0,     0,     0,     0,     0,     0,  1088,
+    19,  4886,     0,    32,   529,     0,   -37,     0,   -19,   284,
+     0,   273,     0, 17309,   276,     0,    16,     0,   138,     0,
+     0,     0,     0,     0,  -207,  -137,    18,   164,     0,     0,
+   142, 16817,   -32, 14602,     0,  -183,     0,    76,     0,   622,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,     0,     0,     0,     0,  -182,     0,
+     0,     0,     0,     0,     0,     0,     0,     0,     0,   -23,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,   311,     0,     0, 14956,   265,   288,   103,
-  1205,     0,   246,     0,   215,    68,   459,   233,   530,   261,
-    68,     0,     0,   103,   352,   568,     0, 16801, 16801,   326,
-     0,   480,     0,     0,     0,   369, 16924, 16924, 16924, 16924,
-  4781,     0,     0,   318,   622,   627,     0,     0,     0,     0,
-  3352,     0, 15079, 15079,     0,     0,  4338,     0, 16801,   -99,
-     0, 15940,   313, 14586,     0,   495,   368,   378,   380,   379,
- 14710,   365,     0,   123,   215,   375,     0,   206,   262,   318,
-     0,   262,   360,   416, 17662,     0,   523,     0,   683,     0,
-     0,     0,     0,     0,     0,     0,     0,   306,   672,   871,
-   517,   374,   941,   381,   254,     0,  1603,     0,     0,     0,
-   390,     0,     0,     0, 16801, 16801, 16801, 16801, 14956, 16801,
- 16801, 16924, 16924, 16924, 16924, 16924, 16924, 16924, 16924, 16924,
- 16924, 16924, 16924, 16924, 16924, 16924, 16924, 16924, 16924, 16924,
- 16924, 16924, 16924, 16924, 16924, 16924, 16924,     0,     0,  2365,
-  2851, 15079, 18901, 18901, 17047,     0, 16063, 14710, 14092,   725,
- 16063, 17047,     0, 14215,   430,     0,     0,   215,     0,     0,
-     0,   103,     0,     0,     0,  3837,  4911, 15079, 14586, 16801,
-  2299,     0,     0,     0,     0,  1205, 16186,   504,     0,     0,
- 14338,   379,     0, 14586,   515,  5980,  6524, 15079, 16924, 16924,
- 16924, 14586,   352, 16309,   522,     0,    56,    56,     0, 18516,
- 18571, 15079,     0,     0,     0,     0, 16924, 15202,     0,     0,
- 15571,     0,   123,     0,   447,     0,     0,     0,   123,   270,
-     0,     0,     0,     0,     0, 18139, 16801,  4781, 14462,   427,
-  5980,  6524, 16924, 16924, 16924,   123,     0,     0,   123,     0,
- 15694,     0,     0, 15817,     0,     0,     0,     0,     0,   748,
- 18626, 18681, 15079, 17662,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,     0,     0,     4,     0,     0,   765,
-   739,     0,     0,     0,     0,  1251,  2311,     0,     0,     0,
-     0,     0,   497,   505,   769,     0,   755,  -194,   775,   776,
-     0,     0,     0,  -166,  -166,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,   368,  1746,  1746,  1746,  1746,  2444,
-  2444,  5267,  3885,  1746,  1746,  2899,  2899,   403,   403,   368,
-  1966,   368,   368,   -74,   -74,  2444,  2444,  1917,  1917,  1830,
-  -166,   484,     0,   485,  -211,     0,     0,   487,     0,   492,
-  -211,     0,     0,     0,   123,     0,     0,  -211,  -211,     0,
-  4781, 16924,     0,  4415,     0,     0,   787,   123, 17662,   789,
-     0,     0,     0,     0,     0,  4850,     0,   103,     0, 16801,
- 14586,  -211,     0,     0,  -211,     0,   123,   570,   270,  2311,
-   103, 14586, 18461, 18139,     0,     0,   500,     0, 14586,   576,
-     0,  1205,   440,     0,   507,   508,   509,   492,   123,  4415,
-   504,   577,    57,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,   123, 16801,     0, 16924,   318,   627,     0,     0,
-     0,     0,     0,     0,     0,   270,   491,     0,   368,   368,
-  4781,     0,     0,   262, 17662,     0,     0,     0,     0,   123,
-   748, 14586,   373,     0,     0,     0, 16924,     0,  1251,   859,
-     0,   798,     0,   123,   755,     0,     0,  1616,     0,   992,
-     0,     0, 14586, 14586,     0,  2311,     0,  2311,     0,     0,
-  1261,     0, 14586,     0, 14586,  -166,   801, 14586, 17047, 17047,
-     0,   390,     0,     0, 17047, 16924,     0,   390,   531,   526,
-     0,     0,     0,     0,     0, 16924, 16432,     0,   748, 17662,
- 16924,     0,   103,   610,     0,     0,     0,   123,     0,   621,
-     0,     0, 17785,    68,     0,     0, 14586,     0,     0, 16801,
-     0,   628, 16924, 16924, 16924,   554,   634,     0, 16555, 14586,
- 14586, 14586,     0,    56,     0,     0,     0,     0,     0,     0,
-     0,   539,     0,     0,     0,     0,     0,     0,   123,   394,
-   860,  1406,     0,   123,   863,     0,     0,   864,     0,     0,
-   646,   551,   875,   895,     0,   899,     0,   863,   885,   906,
-   755,   914,   915,     0,   602,     0,   697,   611, 14586, 16924,
-   699,     0,  4781,     0,  4781,     0,     0,  4781,  4781,     0,
- 17047,     0,  4781, 16924,     0,   748,  4781, 14586,     0,     0,
-     0,     0,  2299,   654,     0,   541,     0,     0, 14586,     0,
-    68,     0, 16924,     0,     0,  -119,   711,   714,     0,     0,
-     0,   940,   394,   868,     0,     0,  1616,     0,   992,     0,
-     0,  1616,     0,  2311,     0,  1616,     0,     0, 17908,  1616,
-     0,   631,  2423,     0,  2423,     0,     0,     0,     0,   630,
-  4781,     0,     0,  4781,     0,   731, 14586,     0, 18736, 18791,
- 15079,   265, 14586,     0,     0,     0,     0,     0, 14586,   394,
-   940,   394,   952,     0,   863,   954,   863,   863,   689,   549,
-   863,     0,   956,   958,   962,   863,     0,     0,     0,   743,
-     0,     0,     0,     0,   123,     0,   440,   747,   940,   394,
-     0,  1616,     0,     0,     0,     0, 18846,     0,  1616,     0,
-  2423,     0,  1616,     0,     0,     0,     0,     0,     0,   940,
-   863,     0,     0,   863,   968,   863,   863,     0,     0,  1616,
-     0,     0,     0,   863,     0,
+     0,     0,     0,     0,   305,     0,     0, 14972,   208,   106,
+   138,  1088,     0,   109,     0,    19,    57,   631,    23,   470,
+   216,    57,     0,     0,   138,   292,   496,     0, 16817, 16817,
+   258,     0,   691,     0,     0,     0,   293, 16940, 16940, 16940,
+ 16940,  4886,     0,     0,   279,   550,   552,     0,     0,     0,
+     0,  3457,     0, 15095, 15095,     0,     0,  4443,     0, 16817,
+  -206,     0, 15956,   275, 14602,     0,   771,   322,   327,   329,
+   295, 14726,   307,     0,   164,    19,   318,     0,   156,   163,
+   279,     0,   163,   289,   349, 17555,     0,   831,     0,   620,
+     0,     0,     0,     0,     0,     0,     0,     0,   407,   568,
+   606,   377,   296,   785,   298,  -118,     0,  2404,     0,     0,
+     0,   341,     0,     0,     0, 16817, 16817, 16817, 16817, 14972,
+ 16817, 16817, 16940, 16940, 16940, 16940, 16940, 16940, 16940, 16940,
+ 16940, 16940, 16940, 16940, 16940, 16940, 16940, 16940, 16940, 16940,
+ 16940, 16940, 16940, 16940, 16940, 16940, 16940, 16940,     0,     0,
+  2470,  2956, 15095, 18739, 18739, 17063,     0, 16079, 14726,  6009,
+   637, 16079, 17063,     0, 14230,   353,     0,     0,    19,     0,
+     0,     0,   138,     0,     0,     0,  3532,  3992, 15095, 14602,
+ 16817,  2416,     0,     0,     0,     0,  1088, 16202,   420,     0,
+     0, 14354,   295,     0, 14602,   428,  5016,  6629, 15095, 16940,
+ 16940, 16940, 14602,   292, 16325,   432,     0,    69,    69,     0,
+ 14111, 18409, 15095,     0,     0,     0,     0, 16940, 15218,     0,
+     0, 15587,     0,   164,     0,   357,     0,     0,     0,   164,
+    56,     0,     0,     0,     0,     0, 18032, 16817,  4886, 14478,
+   340,  5016,  6629, 16940, 16940, 16940,   164,     0,     0,   164,
+     0, 15710,     0,     0, 15833,     0,     0,     0,     0,     0,
+   659, 18464, 18519, 15095, 17555,     0,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,     0,     0,    -5,     0,     0,
+   674,   653,     0,     0,     0,     0,  1414,  2004,     0,     0,
+     0,     0,     0,   403,   409,   675,     0,   657,  -168,   679,
+   681,     0,     0,     0,  -157,  -157,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,   322,  2564,  2564,  2564,  2564,
+  1863,  1863,  5372,  4039,  2564,  2564,  3004,  3004,   889,   889,
+   322,  1932,   322,   322,   -51,   -51,  1863,  1863,  2555,  2555,
+  1696,  -157,   391,     0,   392,  -137,     0,     0,   396,     0,
+   397,  -137,     0,     0,     0,   164,     0,     0,  -137,  -137,
+     0,  4886, 16940,     0,     0,  4520,     0,     0,   677,   692,
+   164, 17555,   697,     0,     0,     0,     0,     0,  4955,     0,
+   138,     0, 16817, 14602,  -137,     0,     0,  -137,     0,   164,
+   478,    56,  2004,   138, 14602, 18354, 18032,     0,     0,   406,
+     0, 14602,   486,     0,  1088,   335,     0,   413,   416,   425,
+   397,   164,  4520,   420,   499,    60,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,   164, 16817,     0, 16940,   279,
+   552,     0,     0,     0,     0,     0,     0,     0,    56,   405,
+     0,   322,   322,  4886,     0,     0,   163, 17555,     0,     0,
+     0,     0,   164,   659, 14602,  -126,     0,     0,     0, 16940,
+     0,  1414,   515,     0,   725,     0,   164,   657,     0,     0,
+  1342,     0,   507,     0,     0, 14602, 14602,     0,  2004,     0,
+  2004,     0,     0,  1828,     0, 14602,     0, 14602,  -157,   715,
+ 14602, 17063, 17063,     0,   341,     0,     0, 17063, 16940,     0,
+   341,   437,   443,     0,     0,     0,     0,     0, 16940, 17063,
+ 16448,     0,   659, 17555, 16940,     0,   138,   517,     0,     0,
+     0,   164,     0,   532,     0,     0, 17678,    57,     0,     0,
+ 14602,     0,     0, 16817,     0,   533, 16940, 16940, 16940,   461,
+   564,     0, 16571, 14602, 14602, 14602,     0,    69,     0,     0,
+     0,     0,     0,     0,     0,   440,     0,     0,     0,     0,
+     0,     0,   164,  1387,   770,  1485,     0,   164,   788,     0,
+     0,   790,     0,     0,   566,   477,   797,   798,     0,   799,
+     0,   788,   792,   807,   657,   815,   816,     0,   508,     0,
+   601,   506, 14602, 16940,   605,     0,  4886,     0,  4886,     0,
+     0,  4886,  4886,     0, 17063,     0,  4886, 16940,     0,   659,
+  4886, 14602,     0,     0,     0,     0,  2416,   560,     0,   836,
+     0,     0, 14602,     0,    57,     0, 16940,     0,     0,   -31,
+   610,   614,     0,     0,     0,   835,  1387,   538,     0,     0,
+  1342,     0,   507,     0,     0,  1342,     0,  2004,     0,  1342,
+     0,     0, 17801,  1342,     0,   523,  2528,     0,  2528,     0,
+     0,     0,     0,   521,  4886,     0,     0,  4886,     0,   632,
+ 14602,     0, 18574, 18629, 15095,   208, 14602,     0,     0,     0,
+     0,     0, 14602,  1387,   835,  1387,   847,     0,   788,   864,
+   788,   788,   585,   851,   788,     0,   865,   887,   890,   788,
+     0,     0,     0,   666,     0,     0,     0,     0,   164,     0,
+   335,   687,   835,  1387,     0,  1342,     0,     0,     0,     0,
+ 18684,     0,  1342,     0,  2528,     0,  1342,     0,     0,     0,
+     0,     0,     0,   835,   788,     0,     0,   788,   912,   788,
+   788,     0,     0,  1342,     0,     0,     0,   788,     0,
     }, yyRindex = {
-//yyRindex 955
-     0,     0,   176,     0,     0,     0,     0,     0,    49,     0,
-     0,   746,     0,     0,     0, 13050, 13156,     0,     0, 13298,
-  4666,  4173,     0,     0,     0,     0, 17170,     0, 16678,     0,
-     0,     0,     0,     0,  2078,  3187,     0,     0,  2201,     0,
-     0,     0,     0,     0,     0,    95,     0,   670,   657,    24,
-     0,     0,   970,     0,     0,     0,   988,   257,     0,     0,
-     0,     0,     0, 13401,     0, 15325,     0,  6888,     0,     0,
-     0,  6989,     0,     0,     0,     0,     0,     0,    27,  5273,
-  1141,  7133,  1366,     0,     0, 14049,     0, 13515,     0,     0,
-     0,     0,    94,     0,     0,     0,     0,    17,     0,     0,
-     0,     0,     0,  7237,  6194,     0,   680, 11677, 11803,     0,
-     0,     0,    95,     0,     0,     0,     0,     0,     0,     0,
+//yyRindex 959
+     0,     0,   146,     0,     0,     0,     0,     0,   648,     0,
+     0,   689,     0,     0,     0, 13155, 13261,     0,     0, 13403,
+  4771,  4278,     0,     0,     0,     0, 17186,     0, 16694,     0,
+     0,     0,     0,     0,  2183,  3292,     0,     0,  2306,     0,
+     0,     0,     0,     0,     0,    30,     0,   636,   638,    90,
+     0,     0,   849,     0,     0,     0,   868,  -102,     0,     0,
+     0,     0,     0, 13506,     0, 15341,     0,  6993,     0,     0,
+     0,  7094,     0,     0,     0,     0,     0,     0,     0,    50,
+  1006, 14005,  7238, 14058,     0,     0, 14115,     0, 13620,     0,
+     0,     0,     0,   150,     0,     0,     0,     0,    45,     0,
+     0,     0,     0,     0,  7342,  6299,     0,   644, 11782, 11908,
+     0,     0,     0,    30,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,  1717,  1976,  2317,  2440,     0,     0,
+     0,     0,     0,     0,     0,  1013,  1159,  1759,  2065,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-  2791,  2933,  3426,  3777,     0,  3919,     0,     0,     0,     0,
+     0,  2081,  2422,  2545,  2896,     0,  3038,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0, 11563,     0,     0,     0,   -18,     0,  1276,
-   593,     0,     0,  6308,   935,     0,     0,  6640,     0,     0,
-     0,     0,     0,   746,     0,   750,     0,     0,     0,     0,
-   203,     0,   229,     0,     0,     0,     0,     0,     0,     0,
- 11461,     0,     0, 13760,  4784,  4784,     0,     0,     0,     0,
-   691,     0,     0,    18,     0,     0,   691,     0,     0,     0,
-     0,     0,     0,    48,     0,     0,  7598,  7350,  7482, 13649,
-    95,     0,   102,   691,   110,     0,     0,   692,   692,     0,
-     0,   673,     0,     0,     0,   195,     0,   669,   147,     0,
+     0,     0,     0,     0, 12878,     0,     0,     0,   315,     0,
+  1192,    21,     0,     0,  6413,  5378,     0,     0,  6745,     0,
+     0,     0,     0,     0,   689,     0,   722,     0,     0,     0,
+     0,   664,     0,   765,     0,     0,     0,     0,     0,     0,
+     0, 11566,     0,     0,  1138,  2000,  2000,     0,     0,     0,
+     0,   661,     0,     0,    96,     0,     0,   661,     0,     0,
+     0,     0,     0,     0,    26,     0,     0,  7703,  7455,  7587,
+ 13754,    30,     0,    84,   661,    97,     0,     0,   650,   650,
+     0,     0,   643,     0,     0,     0,  1052,     0,  1237,   157,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,     0,     0,    -3,     0,     0,     0,
-   991,     0,     0,     0,     0,     0,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,     0,     0,   257,     0,     0,
+     0, 13865,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,    20,     0,     0,     0,     0,     0,    95,   162,   163,
-     0,     0,     0,    45,     0,     0,     0,   115,     0, 12193,
-     0,     0,     0,     0,     0,     0,     0,    20,    49,     0,
-   137,     0,     0,     0,     0,   662,   255,   424,     0,     0,
-  1351,  6774,     0,   294, 12319,     0,     0,    20,     0,     0,
-     0,   659,     0,     0,     0,     0,     0,     0,   616,     0,
-     0,    20,     0,     0,     0,     0,     0, 13864,     0,     0,
- 13864,     0,   691,     0,     0,     0,     0,     0,   691,   691,
-     0,     0,     0,     0,     0,     0,     0, 10489,    48,     0,
-     0,     0,     0,     0,     0,   691,     0,   601,   691,     0,
-   694,     0,     0,  -213,     0,     0,     0,  1200,     0,   168,
-     0,     0,    20,     0,     0,     0,     0,     0,     0,     0,
+     0,     0,    24,     0,     0,     0,     0,     0,    30,   169,
+   225,     0,     0,     0,    51,     0,     0,     0,   145,     0,
+ 12298,     0,     0,     0,     0,     0,     0,     0,    24,   648,
+     0,   149,     0,     0,     0,     0,   261,   468,   378,     0,
+     0,  1346,  6879,     0,   721, 12424,     0,     0,    24,     0,
+     0,     0,    95,     0,     0,     0,     0,     0,     0,  1061,
+     0,     0,    24,     0,     0,     0,     0,     0,  4889,     0,
+     0,  4889,     0,   661,     0,     0,     0,     0,     0,   661,
+   661,     0,     0,     0,     0,     0,     0,     0,  1603,    26,
+     0,     0,     0,     0,     0,     0,   661,     0,    73,   661,
+     0,   671,     0,     0,  -169,     0,     0,     0,  1492,     0,
+   226,     0,     0,    24,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-   107,     0,     0,     0,     0,     0,   109,     0,     0,     0,
-     0,     0,   120,     0,    78,     0,  -160,     0,    78,    78,
-     0,     0,     0, 12451, 12588,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,  7699,  9663,  9785,  9903, 10000,  9208,
-  9328, 10086, 10359, 10176, 10273,  1935, 10449,  8628,  8750,  7814,
-  8873,  7947,  8062,  8411,  8524,  9448,  9545,  8991,  9099,   981,
- 12451,  5027,     0,  5150,  4543,     0,     0,  5520,  3557,  5643,
- 15325,     0,  3680,     0,   707,     0,     0,  5766,  5766,     0,
- 10574,     0,     0, 12869,     0,     0,     0,   691,     0,   191,
-     0,     0,     0, 10397,     0, 11547,     0,     0,     0,     0,
-    49,  6442, 11935, 12061,     0,     0,   707,     0,   691,   118,
-     0,    49,     0,     0,     0,    25,    83,     0,   563,   790,
-     0,   719,   790,     0,  2571,  2694,  3064,  4050,   707, 11582,
-   790,     0,     0,     0,     0,     0,     0,     0,  1421,  1555,
-  1559,   752,   707,     0,     0,     0, 13803,  4784,     0,     0,
-     0,     0,     0,     0,     0,   691,     0,     0,  8163,  8279,
- 10670,    77,     0,   692,     0,   749,   788,   791,   193,   707,
-   234,    48,     0,     0,     0,     0,     0,     0,     0,   125,
-     0,   146,     0,   691,    18,     0,     0,     0,     0,     0,
-     0,     0,   273,    48,     0,     0,     0,     0,     0,     0,
-   701,     0,   273,     0,    48, 12588,     0,   273,     0,     0,
-     0, 13900,     0,     0,     0,     0,     0, 13985, 12949,     0,
-     0,     0,     0,     0,  1504,     0,     0,     0,   238,     0,
-     0,     0,     0,     0,     0,     0,     0,   691,     0,     0,
-     0,     0,     0,     0,     0,     0,   273,     0,     0,     0,
-     0,     0,     0,     0,     0,  6093,     0,     0,     0,   658,
-   273,   273,   419,     0,     0,     0,     0,     0,     0,     0,
-  1416,     0,     0,     0,     0,     0,     0,     0,   691,     0,
-   148,     0,     0,   691,    78,     0,     0,    88,     0,     0,
-     0,     0,    78,    78,     0,    78,     0,    78,   141,   -16,
-   701,   -16,   -16,     0,     0,     0,     0,     0,    48,     0,
-     0,     0, 10755,     0, 10816,     0,     0, 10913, 10999,     0,
-     0,     0, 11108,     0,  1721,   275, 11169,    49,     0,     0,
-     0,     0,   137,     0,   651,     0,  1156,     0,    49,     0,
-     0,     0,     0,     0,     0,   790,     0,     0,     0,     0,
-     0,   150,     0,   151,     0,     0,     0,     0,     0,     0,
+     0,   103,     0,     0,     0,     0,     0,    93,     0,     0,
+     0,     0,     0,    37,     0,    10,     0,  -164,     0,    10,
+    10,     0,     0,     0, 12556, 12693,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,  7804,  9768,  9890, 10008, 10105,
+  9313,  9433, 10191, 10464, 10281, 10378, 10554, 10594,  8733,  8855,
+  7919,  8978,  8052,  8167,  8516,  8629,  9553,  9650,  9096,  9204,
+   950, 12556,  5132,     0,  5255,  4648,     0,     0,  5625,  3662,
+  5748, 15341,     0,  3785,     0,   683,     0,     0,  5871,  5871,
+     0, 10679,     0,     0,     0,  1895,     0,     0,     0,     0,
+   661,     0,   232,     0,     0,     0, 10502,     0, 11652,     0,
+     0,     0,     0,   648,  6547, 12040, 12166,     0,     0,   683,
+     0,   661,    98,     0,   648,     0,     0,     0,   155,   591,
+     0,   748,   769,     0,   271,   769,     0,  2676,  2799,  3169,
+  4155,   683, 11687,   769,     0,     0,     0,     0,     0,     0,
+     0,   431,   952,  1264,   746,   683,     0,     0,     0,  1517,
+  2000,     0,     0,     0,     0,     0,     0,     0,   661,     0,
+     0,  8268,  8384, 10775,    83,     0,   650,     0,   148,   853,
+   888,   926,   683,   235,    26,     0,     0,     0,     0,     0,
+     0,     0,   104,     0,   108,     0,   661,    96,     0,     0,
+     0,     0,     0,     0,     0,   166,    26,     0,     0,     0,
+     0,     0,     0,   652,     0,   166,     0,    26, 12693,     0,
+   166,     0,     0,     0, 13908,     0,     0,     0,     0,     0,
+ 13969, 13054,     0,     0,     0,     0,     0, 11514,     0,     0,
+     0,     0,   445,     0,     0,     0,     0,     0,     0,     0,
+     0,   661,     0,     0,     0,     0,     0,     0,     0,     0,
+   166,     0,     0,     0,     0,     0,     0,     0,     0,  6198,
+     0,     0,     0,   760,   166,   166,   856,     0,     0,     0,
+     0,     0,     0,     0,   627,     0,     0,     0,     0,     0,
+     0,     0,   661,     0,   110,     0,     0,   661,    10,     0,
+     0,   154,     0,     0,     0,     0,    10,    10,     0,    10,
+     0,    10,   202,    -2,   652,    -2,    -2,     0,     0,     0,
+     0,     0,    26,     0,     0,     0, 10860,     0, 10921,     0,
+     0, 11018, 11104,     0,     0,     0, 11213,     0, 11668,   497,
+ 11274,   648,     0,     0,     0,     0,   149,     0,   702,     0,
+   802,     0,   648,     0,     0,     0,     0,     0,     0,   769,
+     0,     0,     0,     0,     0,   126,     0,   128,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,    65,     0,     0,     0,     0,     0,     0,     0,
- 11255,     0,     0, 11352, 11409,     0,    49,  1178,     0,     0,
-    20,   -18,   294,     0,     0,     0,     0,     0,   273,     0,
-   155,     0,   159,     0,    78,    78,    78,    78,     0,   158,
-   -16,     0,   -16,   -16,   -16,   -16,     0,     0,     0,     0,
-   713,  1003,  1027,   879,   707,     0,   790,     0,   160,     0,
+     0,     0,     0,     0,     0,     0,     9,     0,     0,     0,
+     0,     0,     0,     0, 11360,     0,     0, 11457, 14186,     0,
+   648,   874,     0,     0,    24,   315,   721,     0,     0,     0,
+     0,     0,   166,     0,   130,     0,   132,     0,    10,    10,
+    10,    10,     0,   203,    -2,     0,    -2,    -2,    -2,    -2,
+     0,     0,     0,     0,   742,  1038,  1124,   588,   683,     0,
+   769,     0,   139,     0,     0,     0,     0,     0,     0,     0,
      0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,     0,     0,  1066,     0,     0,   161,
-    78,  1136,   964,   -16,   -16,   -16,   -16,     0,     0,     0,
-     0,     0,     0,   -16,     0,
+  1004,     0,     0,   162,    10,   646,   954,    -2,    -2,    -2,
+    -2,     0,     0,     0,     0,     0,     0,    -2,     0,
     }, yyGindex = {
-//yyGindex 155
-     0,   364,     0,    12,  1476,  -283,     0,   -64,    10,    -6,
-   -26,     0,     0,     0,   455,     0,     0,     0,   985,     0,
-     0,     0,   660,  -217,     0,     0,     0,     0,     0,     0,
-    15,  1052,   -45,   529,  -383,     0,    71,    22,  1333,    47,
-    -5,    60,   217,  -382,     0,   153,     0,   709,     0,    79,
-     0,    -1,  1057,   167,     0,     0,  -604,     0,     0,   736,
-  -266,   243,     0,     0,     0,  -424,  -100,   -52,   -11,   -36,
-  -405,     0,     0,   760,     1,    62,     0,     0,  1191,   387,
-  -581,     0,   -23,  -196,     0,  -374,   204,  -232,  -122,     0,
-  1067,  -298,  1007,     0,  -384,  1065,     9,   201,   574,     0,
-   -14,  -571,     0,  -593,     0,     0,  -177,  -746,     0,  -332,
-  -708,   417,   247,   428,  -419,     0,  -607,   643,     0,    23,
-     0,   -44,   -37,     0,     0,   -24,     0,     0,  -247,     0,
-     0,  -221,     0,  -409,     0,     0,     0,     0,     0,     0,
-   184,     0,     0,     0,     0,     0,     0,     0,     0,     0,
-     0,     0,     0,     0,     0,
+//yyGindex 156
+     0,   371,     0,     8,  1581,  -308,     0,   -54,    20,    -6,
+   -43,     0,     0,     0,   822,     0,     0,     0,   969,     0,
+     0,     0,   599,  -187,     0,     0,     0,     0,     0,     0,
+    12,  1037,  -302,   -46,   423,  -380,     0,    89,    13,  1395,
+    28,    -8,    65,   218,  -392,     0,   127,     0,   886,     0,
+    66,     0,    -4,  1043,   115,     0,     0,  -580,     0,     0,
+   672,  -254,   227,     0,     0,     0,  -362,  -180,   -80,    52,
+   624,  -397,     0,     0,   786,     1,   -10,     0,     0,  5714,
+   366,  -578,     0,   -18,  -270,     0,  -401,   192,  -238,  -155,
+     0,  1173,  -307,  1007,     0,  -581,  1065,   213,   190,   884,
+     0,   -13,  -606,     0,  -582,     0,     0,  -166,  -718,     0,
+  -334,  -645,   412,   237,   287,  -327,     0,  -611,   641,     0,
+    22,     0,   -36,   -34,     0,     0,   -24,     0,     0,  -251,
+     0,     0,  -219,     0,  -375,     0,     0,     0,     0,     0,
+     0,    79,     0,     0,     0,     0,     0,     0,     0,     0,
+     0,     0,     0,     0,     0,     0,
     };
     protected static final short[] yyTable = Ruby19YyTables.yyTable();
     protected static final short[] yyCheck = Ruby19YyTables.yyCheck();
 
   /** maps symbol value to printable name.
       @see #yyExpecting
     */
   protected static final String[] yyNames = {
     "end-of-file",null,null,null,null,null,null,null,null,null,"'\\n'",
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,"' '",null,null,null,null,null,
     null,null,null,null,null,null,"','",null,null,null,null,null,null,
     null,null,null,null,null,null,null,"':'","';'",null,"'='",null,"'?'",
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,
     "'['",null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,null,
     null,null,null,null,null,null,null,null,null,null,null,null,null,
     "kCLASS","kMODULE","kDEF","kUNDEF","kBEGIN","kRESCUE","kENSURE",
     "kEND","kIF","kUNLESS","kTHEN","kELSIF","kELSE","kCASE","kWHEN",
     "kWHILE","kUNTIL","kFOR","kBREAK","kNEXT","kREDO","kRETRY","kIN",
     "kDO","kDO_COND","kDO_BLOCK","kRETURN","kYIELD","kSUPER","kSELF",
     "kNIL","kTRUE","kFALSE","kAND","kOR","kNOT","kIF_MOD","kUNLESS_MOD",
     "kWHILE_MOD","kUNTIL_MOD","kRESCUE_MOD","kALIAS","kDEFINED","klBEGIN",
     "klEND","k__LINE__","k__FILE__","k__ENCODING__","kDO_LAMBDA",
     "tIDENTIFIER","tFID","tGVAR","tIVAR","tCONSTANT","tCVAR","tLABEL",
     "tCHAR","tUPLUS","tUMINUS","tUMINUS_NUM","tPOW","tCMP","tEQ","tEQQ",
     "tNEQ","tGEQ","tLEQ","tANDOP","tOROP","tMATCH","tNMATCH","tDOT",
     "tDOT2","tDOT3","tAREF","tASET","tLSHFT","tRSHFT","tCOLON2","tCOLON3",
     "tOP_ASGN","tASSOC","tLPAREN","tLPAREN2","tRPAREN","tLPAREN_ARG",
     "tLBRACK","tRBRACK","tLBRACE","tLBRACE_ARG","tSTAR","tSTAR2","tAMPER",
     "tAMPER2","tTILDE","tPERCENT","tDIVIDE","tPLUS","tMINUS","tLT","tGT",
     "tPIPE","tBANG","tCARET","tLCURLY","tRCURLY","tBACK_REF2","tSYMBEG",
     "tSTRING_BEG","tXSTRING_BEG","tREGEXP_BEG","tWORDS_BEG","tQWORDS_BEG",
     "tSTRING_DBEG","tSTRING_DVAR","tSTRING_END","tLAMBDA","tLAMBEG",
     "tNTH_REF","tBACK_REF","tSTRING_CONTENT","tINTEGER","tFLOAT",
     "tREGEXP_END","tLOWEST",
     };
 
   /** printable rules for debugging.
     */
   protected static final String [] yyRule = {
     "$accept : program",
     "$$1 :",
     "program : $$1 top_compstmt",
     "top_compstmt : top_stmts opt_terms",
     "top_stmts : none",
     "top_stmts : top_stmt",
     "top_stmts : top_stmts terms top_stmt",
     "top_stmts : error top_stmt",
     "top_stmt : stmt",
     "$$2 :",
     "top_stmt : klBEGIN $$2 tLCURLY top_compstmt tRCURLY",
     "bodystmt : compstmt opt_rescue opt_else opt_ensure",
     "compstmt : stmts opt_terms",
     "stmts : none",
     "stmts : stmt",
     "stmts : stmts terms stmt",
     "stmts : error stmt",
     "$$3 :",
     "stmt : kALIAS fitem $$3 fitem",
     "stmt : kALIAS tGVAR tGVAR",
     "stmt : kALIAS tGVAR tBACK_REF",
     "stmt : kALIAS tGVAR tNTH_REF",
     "stmt : kUNDEF undef_list",
     "stmt : stmt kIF_MOD expr_value",
     "stmt : stmt kUNLESS_MOD expr_value",
     "stmt : stmt kWHILE_MOD expr_value",
     "stmt : stmt kUNTIL_MOD expr_value",
     "stmt : stmt kRESCUE_MOD stmt",
     "stmt : klEND tLCURLY compstmt tRCURLY",
-    "stmt : lhs '=' command_call",
+    "stmt : command_asgn",
     "stmt : mlhs '=' command_call",
     "stmt : var_lhs tOP_ASGN command_call",
     "stmt : primary_value '[' opt_call_args rbracket tOP_ASGN command_call",
     "stmt : primary_value tDOT tIDENTIFIER tOP_ASGN command_call",
     "stmt : primary_value tDOT tCONSTANT tOP_ASGN command_call",
     "stmt : primary_value tCOLON2 tIDENTIFIER tOP_ASGN command_call",
     "stmt : backref tOP_ASGN command_call",
     "stmt : lhs '=' mrhs",
     "stmt : mlhs '=' arg_value",
     "stmt : mlhs '=' mrhs",
     "stmt : expr",
+    "command_asgn : lhs '=' command_call",
+    "command_asgn : lhs '=' command_asgn",
     "expr : command_call",
     "expr : expr kAND expr",
     "expr : expr kOR expr",
     "expr : kNOT opt_nl expr",
     "expr : tBANG command_call",
     "expr : arg",
     "expr_value : expr",
     "command_call : command",
     "command_call : block_command",
     "command_call : kRETURN call_args",
     "command_call : kBREAK call_args",
     "command_call : kNEXT call_args",
     "block_command : block_call",
     "block_command : block_call tDOT operation2 command_args",
     "block_command : block_call tCOLON2 operation2 command_args",
     "$$4 :",
     "cmd_brace_block : tLBRACE_ARG $$4 opt_block_param compstmt tRCURLY",
     "command : operation command_args",
     "command : operation command_args cmd_brace_block",
     "command : primary_value tDOT operation2 command_args",
     "command : primary_value tDOT operation2 command_args cmd_brace_block",
     "command : primary_value tCOLON2 operation2 command_args",
     "command : primary_value tCOLON2 operation2 command_args cmd_brace_block",
     "command : kSUPER command_args",
     "command : kYIELD command_args",
     "mlhs : mlhs_basic",
     "mlhs : tLPAREN mlhs_inner rparen",
     "mlhs_inner : mlhs_basic",
     "mlhs_inner : tLPAREN mlhs_inner rparen",
     "mlhs_basic : mlhs_head",
     "mlhs_basic : mlhs_head mlhs_item",
     "mlhs_basic : mlhs_head tSTAR mlhs_node",
     "mlhs_basic : mlhs_head tSTAR mlhs_node ',' mlhs_post",
     "mlhs_basic : mlhs_head tSTAR",
     "mlhs_basic : mlhs_head tSTAR ',' mlhs_post",
     "mlhs_basic : tSTAR mlhs_node",
     "mlhs_basic : tSTAR mlhs_node ',' mlhs_post",
     "mlhs_basic : tSTAR",
     "mlhs_basic : tSTAR ',' mlhs_post",
     "mlhs_item : mlhs_node",
     "mlhs_item : tLPAREN mlhs_inner rparen",
     "mlhs_head : mlhs_item ','",
     "mlhs_head : mlhs_head mlhs_item ','",
     "mlhs_post : mlhs_item",
     "mlhs_post : mlhs_post ',' mlhs_item",
     "mlhs_node : variable",
     "mlhs_node : primary_value '[' opt_call_args rbracket",
     "mlhs_node : primary_value tDOT tIDENTIFIER",
     "mlhs_node : primary_value tCOLON2 tIDENTIFIER",
     "mlhs_node : primary_value tDOT tCONSTANT",
     "mlhs_node : primary_value tCOLON2 tCONSTANT",
     "mlhs_node : tCOLON3 tCONSTANT",
     "mlhs_node : backref",
     "lhs : variable",
     "lhs : primary_value '[' opt_call_args rbracket",
     "lhs : primary_value tDOT tIDENTIFIER",
     "lhs : primary_value tCOLON2 tIDENTIFIER",
     "lhs : primary_value tDOT tCONSTANT",
     "lhs : primary_value tCOLON2 tCONSTANT",
     "lhs : tCOLON3 tCONSTANT",
     "lhs : backref",
     "cname : tIDENTIFIER",
     "cname : tCONSTANT",
     "cpath : tCOLON3 cname",
     "cpath : cname",
     "cpath : primary_value tCOLON2 cname",
     "fname : tIDENTIFIER",
     "fname : tCONSTANT",
     "fname : tFID",
     "fname : op",
     "fname : reswords",
     "fsym : fname",
     "fsym : symbol",
     "fitem : fsym",
     "fitem : dsym",
     "undef_list : fitem",
     "$$5 :",
     "undef_list : undef_list ',' $$5 fitem",
     "op : tPIPE",
     "op : tCARET",
     "op : tAMPER2",
     "op : tCMP",
     "op : tEQ",
     "op : tEQQ",
     "op : tMATCH",
     "op : tNMATCH",
     "op : tGT",
     "op : tGEQ",
     "op : tLT",
     "op : tLEQ",
     "op : tNEQ",
     "op : tLSHFT",
     "op : tRSHFT",
     "op : tPLUS",
     "op : tMINUS",
     "op : tSTAR2",
     "op : tSTAR",
     "op : tDIVIDE",
     "op : tPERCENT",
     "op : tPOW",
     "op : tBANG",
     "op : tTILDE",
     "op : tUPLUS",
     "op : tUMINUS",
     "op : tAREF",
     "op : tASET",
     "op : tBACK_REF2",
     "reswords : k__LINE__",
     "reswords : k__FILE__",
     "reswords : k__ENCODING__",
     "reswords : klBEGIN",
     "reswords : klEND",
     "reswords : kALIAS",
     "reswords : kAND",
     "reswords : kBEGIN",
     "reswords : kBREAK",
     "reswords : kCASE",
     "reswords : kCLASS",
     "reswords : kDEF",
     "reswords : kDEFINED",
     "reswords : kDO",
     "reswords : kELSE",
     "reswords : kELSIF",
     "reswords : kEND",
     "reswords : kENSURE",
     "reswords : kFALSE",
     "reswords : kFOR",
     "reswords : kIN",
     "reswords : kMODULE",
     "reswords : kNEXT",
     "reswords : kNIL",
     "reswords : kNOT",
     "reswords : kOR",
     "reswords : kREDO",
     "reswords : kRESCUE",
     "reswords : kRETRY",
     "reswords : kRETURN",
     "reswords : kSELF",
     "reswords : kSUPER",
     "reswords : kTHEN",
     "reswords : kTRUE",
     "reswords : kUNDEF",
     "reswords : kWHEN",
     "reswords : kYIELD",
     "reswords : kIF_MOD",
     "reswords : kUNLESS_MOD",
     "reswords : kWHILE_MOD",
     "reswords : kUNTIL_MOD",
     "reswords : kRESCUE_MOD",
     "arg : lhs '=' arg",
     "arg : lhs '=' arg kRESCUE_MOD arg",
     "arg : var_lhs tOP_ASGN arg",
     "arg : var_lhs tOP_ASGN arg kRESCUE_MOD arg",
     "arg : primary_value '[' opt_call_args rbracket tOP_ASGN arg",
     "arg : primary_value tDOT tIDENTIFIER tOP_ASGN arg",
     "arg : primary_value tDOT tCONSTANT tOP_ASGN arg",
     "arg : primary_value tCOLON2 tIDENTIFIER tOP_ASGN arg",
     "arg : primary_value tCOLON2 tCONSTANT tOP_ASGN arg",
     "arg : tCOLON3 tCONSTANT tOP_ASGN arg",
     "arg : backref tOP_ASGN arg",
     "arg : arg tDOT2 arg",
     "arg : arg tDOT3 arg",
     "arg : arg tPLUS arg",
     "arg : arg tMINUS arg",
     "arg : arg tSTAR2 arg",
     "arg : arg tDIVIDE arg",
     "arg : arg tPERCENT arg",
     "arg : arg tPOW arg",
     "arg : tUMINUS_NUM tINTEGER tPOW arg",
     "arg : tUMINUS_NUM tFLOAT tPOW arg",
     "arg : tUPLUS arg",
     "arg : tUMINUS arg",
     "arg : arg tPIPE arg",
     "arg : arg tCARET arg",
     "arg : arg tAMPER2 arg",
     "arg : arg tCMP arg",
     "arg : arg tGT arg",
     "arg : arg tGEQ arg",
     "arg : arg tLT arg",
     "arg : arg tLEQ arg",
     "arg : arg tEQ arg",
     "arg : arg tEQQ arg",
     "arg : arg tNEQ arg",
     "arg : arg tMATCH arg",
     "arg : arg tNMATCH arg",
     "arg : tBANG arg",
     "arg : tTILDE arg",
     "arg : arg tLSHFT arg",
     "arg : arg tRSHFT arg",
     "arg : arg tANDOP arg",
     "arg : arg tOROP arg",
     "arg : kDEFINED opt_nl arg",
     "arg : arg '?' arg opt_nl ':' arg",
     "arg : primary",
     "arg_value : arg",
     "aref_args : none",
     "aref_args : args trailer",
     "aref_args : args ',' assocs trailer",
     "aref_args : assocs trailer",
     "paren_args : tLPAREN2 opt_call_args rparen",
     "opt_paren_args : none",
     "opt_paren_args : paren_args",
     "opt_call_args : none",
     "opt_call_args : call_args",
     "call_args : command",
     "call_args : args opt_block_arg",
     "call_args : assocs opt_block_arg",
     "call_args : args ',' assocs opt_block_arg",
     "call_args : block_arg",
     "$$6 :",
     "command_args : $$6 call_args",
     "block_arg : tAMPER arg_value",
     "opt_block_arg : ',' block_arg",
     "opt_block_arg : ','",
     "opt_block_arg : none_block_pass",
     "args : arg_value",
     "args : tSTAR arg_value",
     "args : args ',' arg_value",
     "args : args ',' tSTAR arg_value",
     "mrhs : args ',' arg_value",
     "mrhs : args ',' tSTAR arg_value",
     "mrhs : tSTAR arg_value",
     "primary : literal",
     "primary : strings",
     "primary : xstring",
     "primary : regexp",
     "primary : words",
     "primary : qwords",
     "primary : var_ref",
     "primary : backref",
     "primary : tFID",
     "primary : kBEGIN bodystmt kEND",
     "$$7 :",
     "primary : tLPAREN_ARG expr $$7 rparen",
     "primary : tLPAREN compstmt tRPAREN",
     "primary : primary_value tCOLON2 tCONSTANT",
     "primary : tCOLON3 tCONSTANT",
     "primary : tLBRACK aref_args tRBRACK",
     "primary : tLBRACE assoc_list tRCURLY",
     "primary : kRETURN",
     "primary : kYIELD tLPAREN2 call_args rparen",
     "primary : kYIELD tLPAREN2 rparen",
     "primary : kYIELD",
     "primary : kDEFINED opt_nl tLPAREN2 expr rparen",
     "primary : kNOT tLPAREN2 expr rparen",
     "primary : kNOT tLPAREN2 rparen",
     "primary : operation brace_block",
     "primary : method_call",
     "primary : method_call brace_block",
     "primary : tLAMBDA lambda",
     "primary : kIF expr_value then compstmt if_tail kEND",
     "primary : kUNLESS expr_value then compstmt opt_else kEND",
     "$$8 :",
     "$$9 :",
     "primary : kWHILE $$8 expr_value do $$9 compstmt kEND",
     "$$10 :",
     "$$11 :",
     "primary : kUNTIL $$10 expr_value do $$11 compstmt kEND",
     "primary : kCASE expr_value opt_terms case_body kEND",
     "primary : kCASE opt_terms case_body kEND",
     "$$12 :",
     "$$13 :",
     "primary : kFOR for_var kIN $$12 expr_value do $$13 compstmt kEND",
     "$$14 :",
     "primary : kCLASS cpath superclass $$14 bodystmt kEND",
     "$$15 :",
     "$$16 :",
     "primary : kCLASS tLSHFT expr $$15 term $$16 bodystmt kEND",
     "$$17 :",
     "primary : kMODULE cpath $$17 bodystmt kEND",
     "$$18 :",
     "primary : kDEF fname $$18 f_arglist bodystmt kEND",
     "$$19 :",
     "$$20 :",
     "primary : kDEF singleton dot_or_colon $$19 fname $$20 f_arglist bodystmt kEND",
     "primary : kBREAK",
     "primary : kNEXT",
     "primary : kREDO",
     "primary : kRETRY",
     "primary_value : primary",
     "then : term",
     "then : kTHEN",
     "then : term kTHEN",
     "do : term",
     "do : kDO_COND",
     "if_tail : opt_else",
     "if_tail : kELSIF expr_value then compstmt if_tail",
     "opt_else : none",
     "opt_else : kELSE compstmt",
     "for_var : lhs",
     "for_var : mlhs",
     "f_marg : f_norm_arg",
     "f_marg : tLPAREN f_margs rparen",
     "f_marg_list : f_marg",
     "f_marg_list : f_marg_list ',' f_marg",
     "f_margs : f_marg_list",
     "f_margs : f_marg_list ',' tSTAR f_norm_arg",
     "f_margs : f_marg_list ',' tSTAR f_norm_arg ',' f_marg_list",
     "f_margs : f_marg_list ',' tSTAR",
     "f_margs : f_marg_list ',' tSTAR ',' f_marg_list",
     "f_margs : tSTAR f_norm_arg",
     "f_margs : tSTAR f_norm_arg ',' f_marg_list",
     "f_margs : tSTAR",
     "f_margs : tSTAR ',' f_marg_list",
     "block_param : f_arg ',' f_block_optarg ',' f_rest_arg opt_f_block_arg",
     "block_param : f_arg ',' f_block_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg",
     "block_param : f_arg ',' f_block_optarg opt_f_block_arg",
     "block_param : f_arg ',' f_block_optarg ',' f_arg opt_f_block_arg",
     "block_param : f_arg ',' f_rest_arg opt_f_block_arg",
     "block_param : f_arg ','",
     "block_param : f_arg ',' f_rest_arg ',' f_arg opt_f_block_arg",
     "block_param : f_arg opt_f_block_arg",
     "block_param : f_block_optarg ',' f_rest_arg opt_f_block_arg",
     "block_param : f_block_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg",
     "block_param : f_block_optarg opt_f_block_arg",
     "block_param : f_block_optarg ',' f_arg opt_f_block_arg",
     "block_param : f_rest_arg opt_f_block_arg",
     "block_param : f_rest_arg ',' f_arg opt_f_block_arg",
     "block_param : f_block_arg",
     "opt_block_param : none",
     "opt_block_param : block_param_def",
     "block_param_def : tPIPE opt_bv_decl tPIPE",
     "block_param_def : tOROP",
     "block_param_def : tPIPE block_param opt_bv_decl tPIPE",
     "opt_bv_decl : none",
     "opt_bv_decl : ';' bv_decls",
     "bv_decls : bvar",
     "bv_decls : bv_decls ',' bvar",
     "bvar : tIDENTIFIER",
     "bvar : f_bad_arg",
     "$$21 :",
     "lambda : $$21 f_larglist lambda_body",
     "f_larglist : tLPAREN2 f_args opt_bv_decl rparen",
     "f_larglist : f_args opt_bv_decl",
     "lambda_body : tLAMBEG compstmt tRCURLY",
     "lambda_body : kDO_LAMBDA compstmt kEND",
     "$$22 :",
     "do_block : kDO_BLOCK $$22 opt_block_param compstmt kEND",
     "block_call : command do_block",
     "block_call : block_call tDOT operation2 opt_paren_args",
     "block_call : block_call tCOLON2 operation2 opt_paren_args",
     "method_call : operation paren_args",
     "method_call : primary_value tDOT operation2 opt_paren_args",
     "method_call : primary_value tCOLON2 operation2 paren_args",
     "method_call : primary_value tCOLON2 operation3",
     "method_call : primary_value tDOT paren_args",
     "method_call : primary_value tCOLON2 paren_args",
     "method_call : kSUPER paren_args",
     "method_call : kSUPER",
     "method_call : primary_value '[' opt_call_args rbracket",
     "$$23 :",
     "brace_block : tLCURLY $$23 opt_block_param compstmt tRCURLY",
     "$$24 :",
     "brace_block : kDO $$24 opt_block_param compstmt kEND",
     "case_body : kWHEN args then compstmt cases",
     "cases : opt_else",
     "cases : case_body",
     "opt_rescue : kRESCUE exc_list exc_var then compstmt opt_rescue",
     "opt_rescue :",
     "exc_list : arg_value",
     "exc_list : mrhs",
     "exc_list : none",
     "exc_var : tASSOC lhs",
     "exc_var : none",
     "opt_ensure : kENSURE compstmt",
     "opt_ensure : none",
     "literal : numeric",
     "literal : symbol",
     "literal : dsym",
     "strings : string",
     "string : tCHAR",
     "string : string1",
     "string : string string1",
     "string1 : tSTRING_BEG string_contents tSTRING_END",
     "xstring : tXSTRING_BEG xstring_contents tSTRING_END",
     "regexp : tREGEXP_BEG xstring_contents tREGEXP_END",
     "words : tWORDS_BEG ' ' tSTRING_END",
     "words : tWORDS_BEG word_list tSTRING_END",
     "word_list :",
     "word_list : word_list word ' '",
     "word : string_content",
     "word : word string_content",
     "qwords : tQWORDS_BEG ' ' tSTRING_END",
     "qwords : tQWORDS_BEG qword_list tSTRING_END",
     "qword_list :",
     "qword_list : qword_list tSTRING_CONTENT ' '",
     "string_contents :",
     "string_contents : string_contents string_content",
     "xstring_contents :",
     "xstring_contents : xstring_contents string_content",
     "string_content : tSTRING_CONTENT",
     "$$25 :",
     "string_content : tSTRING_DVAR $$25 string_dvar",
     "$$26 :",
     "string_content : tSTRING_DBEG $$26 compstmt tRCURLY",
     "string_dvar : tGVAR",
     "string_dvar : tIVAR",
     "string_dvar : tCVAR",
     "string_dvar : backref",
     "symbol : tSYMBEG sym",
     "sym : fname",
     "sym : tIVAR",
     "sym : tGVAR",
     "sym : tCVAR",
     "dsym : tSYMBEG xstring_contents tSTRING_END",
     "numeric : tINTEGER",
     "numeric : tFLOAT",
     "numeric : tUMINUS_NUM tINTEGER",
     "numeric : tUMINUS_NUM tFLOAT",
     "variable : tIDENTIFIER",
     "variable : tIVAR",
     "variable : tGVAR",
     "variable : tCONSTANT",
     "variable : tCVAR",
     "variable : kNIL",
     "variable : kSELF",
     "variable : kTRUE",
     "variable : kFALSE",
     "variable : k__FILE__",
     "variable : k__LINE__",
     "variable : k__ENCODING__",
     "var_ref : variable",
     "var_lhs : variable",
     "backref : tNTH_REF",
     "backref : tBACK_REF",
     "superclass : term",
     "$$27 :",
     "superclass : tLT $$27 expr_value term",
     "superclass : error term",
     "f_arglist : tLPAREN2 f_args rparen",
     "f_arglist : f_args term",
     "f_args : f_arg ',' f_optarg ',' f_rest_arg opt_f_block_arg",
     "f_args : f_arg ',' f_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg",
     "f_args : f_arg ',' f_optarg opt_f_block_arg",
     "f_args : f_arg ',' f_optarg ',' f_arg opt_f_block_arg",
     "f_args : f_arg ',' f_rest_arg opt_f_block_arg",
     "f_args : f_arg ',' f_rest_arg ',' f_arg opt_f_block_arg",
     "f_args : f_arg opt_f_block_arg",
     "f_args : f_optarg ',' f_rest_arg opt_f_block_arg",
     "f_args : f_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg",
     "f_args : f_optarg opt_f_block_arg",
     "f_args : f_optarg ',' f_arg opt_f_block_arg",
     "f_args : f_rest_arg opt_f_block_arg",
     "f_args : f_rest_arg ',' f_arg opt_f_block_arg",
     "f_args : f_block_arg",
     "f_args :",
     "f_bad_arg : tCONSTANT",
     "f_bad_arg : tIVAR",
     "f_bad_arg : tGVAR",
     "f_bad_arg : tCVAR",
     "f_norm_arg : f_bad_arg",
     "f_norm_arg : tIDENTIFIER",
     "f_arg_item : f_norm_arg",
     "f_arg_item : tLPAREN f_margs rparen",
     "f_arg : f_arg_item",
     "f_arg : f_arg ',' f_arg_item",
     "f_opt : tIDENTIFIER '=' arg_value",
     "f_block_opt : tIDENTIFIER '=' primary_value",
     "f_block_optarg : f_block_opt",
     "f_block_optarg : f_block_optarg ',' f_block_opt",
     "f_optarg : f_opt",
     "f_optarg : f_optarg ',' f_opt",
     "restarg_mark : tSTAR2",
     "restarg_mark : tSTAR",
     "f_rest_arg : restarg_mark tIDENTIFIER",
     "f_rest_arg : restarg_mark",
     "blkarg_mark : tAMPER2",
     "blkarg_mark : tAMPER",
     "f_block_arg : blkarg_mark tIDENTIFIER",
     "opt_f_block_arg : ',' f_block_arg",
     "opt_f_block_arg :",
     "singleton : var_ref",
     "$$28 :",
     "singleton : tLPAREN2 $$28 expr rparen",
     "assoc_list : none",
     "assoc_list : assocs trailer",
     "assocs : assoc",
     "assocs : assocs ',' assoc",
     "assoc : arg_value tASSOC arg_value",
     "assoc : tLABEL arg_value",
     "operation : tIDENTIFIER",
     "operation : tCONSTANT",
     "operation : tFID",
     "operation2 : tIDENTIFIER",
     "operation2 : tCONSTANT",
     "operation2 : tFID",
     "operation2 : op",
     "operation3 : tIDENTIFIER",
     "operation3 : tFID",
     "operation3 : op",
     "dot_or_colon : tDOT",
     "dot_or_colon : tCOLON2",
     "opt_terms :",
     "opt_terms : terms",
     "opt_nl :",
     "opt_nl : '\\n'",
     "rparen : opt_nl tRPAREN",
     "rbracket : opt_nl tRBRACK",
     "trailer :",
     "trailer : '\\n'",
     "trailer : ','",
     "term : ';'",
     "term : '\\n'",
     "terms : term",
     "terms : terms ';'",
     "none :",
     "none_block_pass :",
     };
 
   /** debugging support, requires the package <tt>jay.yydebug</tt>.
       Set to <tt>null</tt> to suppress debugging messages.
     */
   protected jay.yydebug.yyDebug yydebug;
 
   /** index-checked interface to {@link #yyNames}.
       @param token single character or <tt>%token</tt> value.
       @return token name or <tt>[illegal]</tt> or <tt>[unknown]</tt>.
     */
   public static final String yyName (int token) {
     if (token < 0 || token > yyNames.length) return "[illegal]";
     String name;
     if ((name = yyNames[token]) != null) return name;
     return "[unknown]";
   }
 
 
   /** computes list of expected tokens on error by tracing the tables.
       @param state for which to compute the list.
       @return list of token names.
     */
   protected String[] yyExpecting (int state) {
     int token, n, len = 0;
     boolean[] ok = new boolean[yyNames.length];
 
     if ((n = yySindex[state]) != 0)
       for (token = n < 0 ? -n : 0;
            token < yyNames.length && n+token < yyTable.length; ++ token)
         if (yyCheck[n+token] == token && !ok[token] && yyNames[token] != null) {
           ++ len;
           ok[token] = true;
         }
     if ((n = yyRindex[state]) != 0)
       for (token = n < 0 ? -n : 0;
            token < yyNames.length && n+token < yyTable.length; ++ token)
         if (yyCheck[n+token] == token && !ok[token] && yyNames[token] != null) {
           ++ len;
           ok[token] = true;
         }
 
     String result[] = new String[len];
     for (n = token = 0; n < len;  ++ token)
       if (ok[token]) result[n++] = yyNames[token];
     return result;
   }
 
   /** the generated parser, with debugging messages.
       Maintains a dynamic state and value stack.
       @param yyLex scanner.
       @param yydebug debug message writer implementing <tt>yyDebug</tt>, or <tt>null</tt>.
       @return result of the last reduction, if any.
     */
   public Object yyparse (RubyYaccLexer yyLex, Object ayydebug)
 				throws java.io.IOException {
     this.yydebug = (jay.yydebug.yyDebug)ayydebug;
     return yyparse(yyLex);
   }
 
   /** initial size and increment of the state/value stack [default 256].
       This is not final so that it can be overwritten outside of invocations
       of {@link #yyparse}.
     */
   protected int yyMax;
 
   /** executed at the beginning of a reduce action.
       Used as <tt>$$ = yyDefault($1)</tt>, prior to the user-specified action, if any.
       Can be overwritten to provide deep copy, etc.
       @param first value for <tt>$1</tt>, or <tt>null</tt>.
       @return first.
     */
   protected Object yyDefault (Object first) {
     return first;
   }
 
   /** the generated parser.
       Maintains a dynamic state and value stack.
       @param yyLex scanner.
       @return result of the last reduction, if any.
     */
   public Object yyparse (RubyYaccLexer yyLex) throws java.io.IOException {
     if (yyMax <= 0) yyMax = 256;			// initial size
     int yyState = 0, yyStates[] = new int[yyMax];	// state stack
     Object yyVal = null, yyVals[] = new Object[yyMax];	// value stack
     int yyToken = -1;					// current input
     int yyErrorFlag = 0;				// #tokens to shift
 
     yyLoop: for (int yyTop = 0;; ++ yyTop) {
       if (yyTop >= yyStates.length) {			// dynamically increase
         int[] i = new int[yyStates.length+yyMax];
         System.arraycopy(yyStates, 0, i, 0, yyStates.length);
         yyStates = i;
         Object[] o = new Object[yyVals.length+yyMax];
         System.arraycopy(yyVals, 0, o, 0, yyVals.length);
         yyVals = o;
       }
       yyStates[yyTop] = yyState;
       yyVals[yyTop] = yyVal;
       if (yydebug != null) yydebug.push(yyState, yyVal);
 
       yyDiscarded: for (;;) {	// discarding a token does not change stack
         int yyN;
         if ((yyN = yyDefRed[yyState]) == 0) {	// else [default] reduce (yyN)
           if (yyToken < 0) {
 //            yyToken = yyLex.advance() ? yyLex.token() : 0;
             yyToken = yyLex.nextToken();
             if (yydebug != null)
               yydebug.lex(yyState, yyToken, yyName(yyToken), yyLex.value());
           }
           if ((yyN = yySindex[yyState]) != 0 && (yyN += yyToken) >= 0
               && yyN < yyTable.length && yyCheck[yyN] == yyToken) {
             if (yydebug != null)
               yydebug.shift(yyState, yyTable[yyN], yyErrorFlag-1);
             yyState = yyTable[yyN];		// shift to yyN
             yyVal = yyLex.value();
             yyToken = -1;
             if (yyErrorFlag > 0) -- yyErrorFlag;
             continue yyLoop;
           }
           if ((yyN = yyRindex[yyState]) != 0 && (yyN += yyToken) >= 0
               && yyN < yyTable.length && yyCheck[yyN] == yyToken)
             yyN = yyTable[yyN];			// reduce (yyN)
           else
             switch (yyErrorFlag) {
   
             case 0:
               support.yyerror("syntax error", yyExpecting(yyState), yyNames[yyToken]);
               if (yydebug != null) yydebug.error("syntax error");
   
             case 1: case 2:
               yyErrorFlag = 3;
               do {
                 if ((yyN = yySindex[yyStates[yyTop]]) != 0
                     && (yyN += yyErrorCode) >= 0 && yyN < yyTable.length
                     && yyCheck[yyN] == yyErrorCode) {
                   if (yydebug != null)
                     yydebug.shift(yyStates[yyTop], yyTable[yyN], 3);
                   yyState = yyTable[yyN];
                   yyVal = yyLex.value();
                   continue yyLoop;
                 }
                 if (yydebug != null) yydebug.pop(yyStates[yyTop]);
               } while (-- yyTop >= 0);
               if (yydebug != null) yydebug.reject();
               support.yyerror("irrecoverable syntax error");
   
             case 3:
               if (yyToken == 0) {
                 if (yydebug != null) yydebug.reject();
                 support.yyerror("irrecoverable syntax error at end-of-file");
               }
               if (yydebug != null)
                 yydebug.discard(yyState, yyToken, yyName(yyToken),
   							yyLex.value());
               yyToken = -1;
               continue yyDiscarded;		// leave stack alone
             }
         }
         int yyV = yyTop + 1-yyLen[yyN];
         if (yydebug != null)
           yydebug.reduce(yyState, yyStates[yyV-1], yyN, yyRule[yyN], yyLen[yyN]);
         ParserState state = states[yyN];
         if (state == null) {
             yyVal = yyDefault(yyV > yyTop ? null : yyVals[yyV]);
         } else {
             yyVal = state.execute(support, lexer, yyVal, yyVals, yyTop);
         }
 //        switch (yyN) {
 // ACTIONS_END
 //        }
         yyTop -= yyLen[yyN];
         yyState = yyStates[yyTop];
         int yyM = yyLhs[yyN];
         if (yyState == 0 && yyM == 0) {
           if (yydebug != null) yydebug.shift(0, yyFinal);
           yyState = yyFinal;
           if (yyToken < 0) {
             yyToken = yyLex.nextToken();
 //            yyToken = yyLex.advance() ? yyLex.token() : 0;
             if (yydebug != null)
                yydebug.lex(yyState, yyToken,yyName(yyToken), yyLex.value());
           }
           if (yyToken == 0) {
             if (yydebug != null) yydebug.accept(yyVal);
             return yyVal;
           }
           continue yyLoop;
         }
         if ((yyN = yyGindex[yyM]) != 0 && (yyN += yyState) >= 0
             && yyN < yyTable.length && yyCheck[yyN] == yyState)
           yyState = yyTable[yyN];
         else
           yyState = yyDgoto[yyM];
         if (yydebug != null) yydebug.shift(yyStates[yyTop], yyState);
         continue yyLoop;
       }
     }
   }
 
-static ParserState[] states = new ParserState[548];
+static ParserState[] states = new ParserState[550];
 static {
-states[1] = new ParserState() {
+states[502] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                  lexer.setState(LexState.EXPR_BEG);
-                  support.initTopLocalVariables();
+                    yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[2] = new ParserState() {
+states[435] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-  /* ENEBO: Removed !compile_for_eval which probably is to reduce warnings*/
-                  if (((Node)yyVals[0+yyTop]) != null) {
-                      /* last expression should not be void */
-                      if (((Node)yyVals[0+yyTop]) instanceof BlockNode) {
-                          support.checkUselessStatement(((BlockNode)yyVals[0+yyTop]).getLast());
-                      } else {
-                          support.checkUselessStatement(((Node)yyVals[0+yyTop]));
-                      }
-                  }
-                  support.getResult().setAST(support.addRootNode(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
+                    lexer.setStrTerm(((StrTerm)yyVals[-1+yyTop]));
+                    yyVal = new EvStrNode(((Token)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[3] = new ParserState() {
+states[368] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                  if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
-                      support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
-                  }
-                  yyVal = ((Node)yyVals[-1+yyTop]);
+                    yyVal = null;
     return yyVal;
   }
 };
-states[5] = new ParserState() {
+states[33] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop])));
+                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
     return yyVal;
   }
 };
-states[6] = new ParserState() {
+states[234] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
+                    /* ENEBO: arg surrounded by in_defined set/unset*/
+                    yyVal = new DefinedNode(((Token)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[7] = new ParserState() {
+states[100] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[9] = new ParserState() {
+states[301] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (support.isInDef() || support.isInSingle()) {
-                        support.yyerror("BEGIN in method");
-                    }
+                    yyVal = support.newCaseNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[10] = new ParserState() {
+states[469] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.getResult().addBeginNode(new PreExe19Node(((Token)yyVals[-4+yyTop]).getPosition(), support.getCurrentScope(), ((Node)yyVals[-1+yyTop])));
-                    yyVal = null;
+                   lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
-states[11] = new ParserState() {
+states[402] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                  Node node = ((Node)yyVals[-3+yyTop]);
-
-                  if (((RescueBodyNode)yyVals[-2+yyTop]) != null) {
-                      node = new RescueNode(support.getPosition(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-3+yyTop]), ((RescueBodyNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
-                  } else if (((Node)yyVals[-1+yyTop]) != null) {
-                      support.warn(ID.ELSE_WITHOUT_RESCUE, support.getPosition(((Node)yyVals[-3+yyTop])), "else without rescue is useless");
-                      node = support.appendToBlock(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
-                  }
-                  if (((Node)yyVals[0+yyTop]) != null) {
-                      if (node == null) node = NilImplicitNode.NIL;
-                      node = new EnsureNode(support.getPosition(((Node)yyVals[-3+yyTop])), node, ((Node)yyVals[0+yyTop]));
-                  }
-
-                  yyVal = node;
+                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[12] = new ParserState() {
+states[335] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
-                        support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
-                    }
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[14] = new ParserState() {
+states[201] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop])));
+                    support.yyerror("constant re-assignment");
     return yyVal;
   }
 };
-states[15] = new ParserState() {
+states[67] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
+                    yyVal = support.new_yield(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[16] = new ParserState() {
+states[503] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[17] = new ParserState() {
+states[436] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.setState(LexState.EXPR_FNAME);
+                   yyVal = lexer.getStrTerm();
+                   lexer.getConditionState().stop();
+                   lexer.getCmdArgumentState().stop();
+                   lexer.setStrTerm(null);
+                   lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
-states[18] = new ParserState() {
+states[369] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newAlias(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                    yyVal = null;
     return yyVal;
   }
 };
-states[19] = new ParserState() {
+states[34] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new VAliasNode(((Token)yyVals[-2+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
     return yyVal;
   }
 };
-states[20] = new ParserState() {
+states[235] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new VAliasNode(((Token)yyVals[-2+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), "$" + ((BackRefNode)yyVals[0+yyTop]).getType());
+                    yyVal = new IfNode(support.getPosition(((Node)yyVals[-5+yyTop])), support.getConditionNode(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[21] = new ParserState() {
+states[101] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.yyerror("can't make alias for the number variables");
+                    if (support.isInDef() || support.isInSingle()) {
+                        support.yyerror("dynamic constant assignment");
+                    }
+
+                    ISourcePosition position = support.getPosition(((Node)yyVals[-2+yyTop]));
+
+                    yyVal = new ConstDeclNode(position, null, support.new_colon2(position, ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[22] = new ParserState() {
+states[302] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = support.newCaseNode(((Token)yyVals[-3+yyTop]).getPosition(), null, ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[23] = new ParserState() {
+states[470] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), null);
+                    yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[24] = new ParserState() {
+states[403] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), null, ((Node)yyVals[-2+yyTop]));
+                    yyVal = support.splat_array(((Node)yyVals[0+yyTop]));
+                    if (yyVal == null) yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[25] = new ParserState() {
+states[336] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
-                        yyVal = new WhileNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
-                    } else {
-                        yyVal = new WhileNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
-                    }
+                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[26] = new ParserState() {
+states[202] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
-                        yyVal = new UntilNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
-                    } else {
-                        yyVal = new UntilNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
-                    }
+                    support.backrefAssignError(((Node)yyVals[-2+yyTop]));
     return yyVal;
   }
 };
-states[27] = new ParserState() {
+states[1] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
-                    yyVal = new RescueNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), new RescueBodyNode(support.getPosition(((Node)yyVals[-2+yyTop])), null, body, null), null);
+                  lexer.setState(LexState.EXPR_BEG);
+                  support.initTopLocalVariables();
     return yyVal;
   }
 };
-states[28] = new ParserState() {
+states[504] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (support.isInDef() || support.isInSingle()) {
-                        support.warn(ID.END_IN_METHOD, ((Token)yyVals[-3+yyTop]).getPosition(), "END in method; use at_exit");
-                    }
-                    yyVal = new PostExeNode(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
+                    yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[29] = new ParserState() {
+states[437] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.checkExpression(((Node)yyVals[0+yyTop]));
-                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                   lexer.getConditionState().restart();
+                   lexer.getCmdArgumentState().restart();
+                   lexer.setStrTerm(((StrTerm)yyVals[-2+yyTop]));
+
+                   yyVal = support.newEvStrNode(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[30] = new ParserState() {
+states[370] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.checkExpression(((Node)yyVals[0+yyTop]));
-                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
-                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
+                    yyVal = null;
     return yyVal;
   }
 };
-states[31] = new ParserState() {
+states[35] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.checkExpression(((Node)yyVals[0+yyTop]));
-
-                    ISourcePosition pos = ((AssignableNode)yyVals[-2+yyTop]).getPosition();
-                    String asgnOp = (String) ((Token)yyVals[-1+yyTop]).getValue();
-                    if (asgnOp.equals("||")) {
-                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
-                        yyVal = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
-                    } else if (asgnOp.equals("&&")) {
-                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
-                        yyVal = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
-                    } else {
-                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
-                        ((AssignableNode)yyVals[-2+yyTop]).setPosition(pos);
-                        yyVal = ((AssignableNode)yyVals[-2+yyTop]);
-                    }
+                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
     return yyVal;
   }
 };
-states[32] = new ParserState() {
+states[236] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-  /* FIXME: arg_concat logic missing for opt_call_args*/
-                    yyVal = support.new_opElementAsgnNode(support.getPosition(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-5+yyTop]), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[33] = new ParserState() {
+states[102] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
+                    if (support.isInDef() || support.isInSingle()) {
+                        support.yyerror("dynamic constant assignment");
+                    }
+
+                    ISourcePosition position = ((Token)yyVals[-1+yyTop]).getPosition();
+
+                    yyVal = new ConstDeclNode(position, null, support.new_colon3(position, (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[34] = new ParserState() {
+states[303] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
+                    lexer.getConditionState().begin();
     return yyVal;
   }
 };
-states[35] = new ParserState() {
+states[471] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
+                   yyVal = null;
     return yyVal;
   }
 };
-states[36] = new ParserState() {
+states[337] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.backrefAssignError(((Node)yyVals[-2+yyTop]));
+                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[37] = new ParserState() {
+states[69] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                    yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[38] = new ParserState() {
+states[2] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
-                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
+  /* ENEBO: Removed !compile_for_eval which probably is to reduce warnings*/
+                  if (((Node)yyVals[0+yyTop]) != null) {
+                      /* last expression should not be void */
+                      if (((Node)yyVals[0+yyTop]) instanceof BlockNode) {
+                          support.checkUselessStatement(((BlockNode)yyVals[0+yyTop]).getLast());
+                      } else {
+                          support.checkUselessStatement(((Node)yyVals[0+yyTop]));
+                      }
+                  }
+                  support.getResult().setAST(support.addRootNode(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
     return yyVal;
   }
 };
-states[39] = new ParserState() {
+states[203] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
-                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
-                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setPosition(support.getPosition(((MultipleAsgn19Node)yyVals[-2+yyTop])));
+                    support.checkExpression(((Node)yyVals[-2+yyTop]));
+                    support.checkExpression(((Node)yyVals[0+yyTop]));
+    
+                    boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
+                    yyVal = new DotNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), false, isLiteral);
     return yyVal;
   }
 };
-states[42] = new ParserState() {
+states[438] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newAndNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                     yyVal = new GlobalVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[43] = new ParserState() {
+states[371] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newOrNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                    support.new_bv(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[44] = new ParserState() {
+states[36] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
+                    support.backrefAssignError(((Node)yyVals[-2+yyTop]));
     return yyVal;
   }
 };
-states[45] = new ParserState() {
-  public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
-    return yyVal;
-  }
-};
-states[47] = new ParserState() {
+states[237] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.checkExpression(((Node)yyVals[0+yyTop]));
+                    yyVal = ((Node)yyVals[0+yyTop]) != null ? ((Node)yyVals[0+yyTop]) : NilImplicitNode.NIL;
     return yyVal;
   }
 };
-states[50] = new ParserState() {
+states[103] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new ReturnNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
+                    support.backrefAssignError(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[51] = new ParserState() {
+states[304] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new BreakNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
+                    lexer.getConditionState().end();
     return yyVal;
   }
 };
-states[52] = new ParserState() {
+states[539] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new NextNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
+                    yyVal = ((Token)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[54] = new ParserState() {
+states[472] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
+                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
+                    ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
+                    lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
-states[55] = new ParserState() {
+states[405] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[56] = new ParserState() {
+states[338] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.pushBlockScope();
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[0+yyTop]).getPosition(), ((ListNode)yyVals[0+yyTop]), null, null);
     return yyVal;
   }
 };
-states[57] = new ParserState() {
+states[70] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new IterNode(((Token)yyVals[-4+yyTop]).getPosition(), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
-                    support.popCurrentScope();
+                    yyVal = ((MultipleAsgn19Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[58] = new ParserState() {
+states[3] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
+                  if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
+                      support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
+                  }
+                  yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[59] = new ParserState() {
+states[204] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_fcall(((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
+                    support.checkExpression(((Node)yyVals[-2+yyTop]));
+                    support.checkExpression(((Node)yyVals[0+yyTop]));
+
+                    boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
+                    yyVal = new DotNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), true, isLiteral);
     return yyVal;
   }
 };
-states[60] = new ParserState() {
+states[439] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
+                     yyVal = new InstVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[61] = new ParserState() {
+states[372] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])); 
+                    yyVal = null;
     return yyVal;
   }
 };
-states[62] = new ParserState() {
+states[104] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
+                    support.yyerror("class/module name must be CONSTANT");
     return yyVal;
   }
 };
-states[63] = new ParserState() {
+states[305] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
+                      /* ENEBO: Lots of optz in 1.9 parser here*/
+                    yyVal = new ForNode(((Token)yyVals[-8+yyTop]).getPosition(), ((Node)yyVals[-7+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-4+yyTop]), support.getCurrentScope());
     return yyVal;
   }
 };
-states[64] = new ParserState() {
+states[37] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop])); /* .setPosFrom($2);*/
+                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[65] = new ParserState() {
+states[540] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_yield(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
+                    yyVal = ((Token)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[67] = new ParserState() {
+states[473] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[-1+yyTop]);
+                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[68] = new ParserState() {
+states[339] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((MultipleAsgn19Node)yyVals[0+yyTop]);
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), support.assignable(((Token)yyVals[0+yyTop]), null), null);
     return yyVal;
   }
 };
-states[69] = new ParserState() {
+states[71] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((Token)yyVals[-2+yyTop]).getPosition(), support.newArrayNode(((Token)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop])), null, null);
     return yyVal;
   }
 };
-states[70] = new ParserState() {
+states[205] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[0+yyTop]).getPosition(), ((ListNode)yyVals[0+yyTop]), null, null);
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "+", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[71] = new ParserState() {
+states[507] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]).add(((Node)yyVals[0+yyTop])), null, null);
+                    if (!support.is_local_id(((Token)yyVals[0+yyTop]))) {
+                        support.yyerror("rest argument must be local variable");
+                    }
+                    
+                    yyVal = new RestArgNode(support.arg_var(support.shadowing_lvar(((Token)yyVals[0+yyTop]))));
     return yyVal;
   }
 };
-states[72] = new ParserState() {
+states[440] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), (ListNode) null);
+                     yyVal = new ClassVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[73] = new ParserState() {
+states[373] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-4+yyTop]).getPosition(), ((ListNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
+                    support.pushBlockScope();
+                    yyVal = lexer.getLeftParenBegin();
+                    lexer.setLeftParenBegin(lexer.incrementParenNest());
     return yyVal;
   }
 };
-states[74] = new ParserState() {
+states[239] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), new StarNode(lexer.getPosition()), null);
+                    yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[75] = new ParserState() {
+states[306] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
+                    if (support.isInDef() || support.isInSingle()) {
+                        support.yyerror("class definition in method body");
+                    }
+                    support.pushLocalScope();
     return yyVal;
   }
 };
-states[76] = new ParserState() {
+states[38] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((Token)yyVals[-1+yyTop]).getPosition(), null, ((Node)yyVals[0+yyTop]), null);
+                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
+                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
-states[77] = new ParserState() {
+states[474] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((Token)yyVals[-3+yyTop]).getPosition(), null, ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
+                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[78] = new ParserState() {
+states[407] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                      yyVal = new MultipleAsgn19Node(((Token)yyVals[0+yyTop]).getPosition(), null, new StarNode(lexer.getPosition()), null);
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[79] = new ParserState() {
+states[340] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                      yyVal = new MultipleAsgn19Node(((Token)yyVals[-2+yyTop]).getPosition(), null, new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), support.assignable(((Token)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[81] = new ParserState() {
+states[72] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[-1+yyTop]);
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[0+yyTop]).getPosition(), ((ListNode)yyVals[0+yyTop]), null, null);
     return yyVal;
   }
 };
-states[82] = new ParserState() {
+states[273] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newArrayNode(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
+                    yyVal = new FCallNoArgNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[83] = new ParserState() {
+states[5] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
+                    yyVal = support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
-states[84] = new ParserState() {
+states[206] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "-", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[85] = new ParserState() {
+states[508] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
+                    yyVal = new UnnamedRestArgNode(((Token)yyVals[0+yyTop]).getPosition(), "", support.getCurrentScope().addVariable("*"));
     return yyVal;
   }
 };
-states[86] = new ParserState() {
+states[374] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
+                    yyVal = new LambdaNode(((ArgsNode)yyVals[-1+yyTop]).getPosition(), ((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), support.getCurrentScope());
+                    support.popCurrentScope();
+                    lexer.setLeftParenBegin(((Integer)yyVals[-2+yyTop]));
     return yyVal;
   }
 };
-states[87] = new ParserState() {
+states[106] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
+                    yyVal = support.new_colon3(((Token)yyVals[-1+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[88] = new ParserState() {
+states[307] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
+
+                    yyVal = new ClassNode(((Token)yyVals[-5+yyTop]).getPosition(), ((Colon3Node)yyVals[-4+yyTop]), support.getCurrentScope(), body, ((Node)yyVals[-3+yyTop]));
+                    support.popCurrentScope();
     return yyVal;
   }
 };
-states[89] = new ParserState() {
+states[39] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
+                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
+                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setPosition(support.getPosition(((MultipleAsgn19Node)yyVals[-2+yyTop])));
     return yyVal;
   }
 };
-states[90] = new ParserState() {
+states[240] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
     return yyVal;
   }
 };
-states[91] = new ParserState() {
+states[475] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (support.isInDef() || support.isInSingle()) {
-                        support.yyerror("dynamic constant assignment");
-                    }
-
-                    ISourcePosition position = support.getPosition(((Node)yyVals[-2+yyTop]));
-
-                    yyVal = new ConstDeclNode(position, null, support.new_colon2(position, ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
+                    yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[92] = new ParserState() {
+states[341] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (support.isInDef() || support.isInSingle()) {
-                        support.yyerror("dynamic constant assignment");
-                    }
-
-                    ISourcePosition position = ((Token)yyVals[-1+yyTop]).getPosition();
-
-                    yyVal = new ConstDeclNode(position, null, support.new_colon3(position, (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-2+yyTop]), new StarNode(lexer.getPosition()), null);
     return yyVal;
   }
 };
-states[93] = new ParserState() {
+states[73] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.backrefAssignError(((Node)yyVals[0+yyTop]));
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]).add(((Node)yyVals[0+yyTop])), null, null);
     return yyVal;
   }
 };
-states[94] = new ParserState() {
+states[274] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                      /* if (!($$ = assignable($1, 0))) $$ = NEW_BEGIN(0);*/
-                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
+                    yyVal = new BeginNode(support.getPosition(((Token)yyVals[-2+yyTop])), ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[95] = new ParserState() {
+states[6] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
+                    yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
     return yyVal;
   }
 };
-states[96] = new ParserState() {
+states[207] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "*", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[97] = new ParserState() {
+states[442] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
+                     lexer.setState(LexState.EXPR_END);
+                     yyVal = ((Token)yyVals[0+yyTop]);
+                     ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-1+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[98] = new ParserState() {
+states[375] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    yyVal = ((ArgsNode)yyVals[-2+yyTop]);
+                    ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-3+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[99] = new ParserState() {
+states[107] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (support.isInDef() || support.isInSingle()) {
-                        support.yyerror("dynamic constant assignment");
-                    }
-
-                    ISourcePosition position = support.getPosition(((Node)yyVals[-2+yyTop]));
-
-                    yyVal = new ConstDeclNode(position, null, support.new_colon2(position, ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
+                    yyVal = support.new_colon2(((Token)yyVals[0+yyTop]).getPosition(), null, (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[100] = new ParserState() {
+states[308] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (support.isInDef() || support.isInSingle()) {
-                        support.yyerror("dynamic constant assignment");
-                    }
-
-                    ISourcePosition position = ((Token)yyVals[-1+yyTop]).getPosition();
-
-                    yyVal = new ConstDeclNode(position, null, support.new_colon3(position, (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
+                    yyVal = Boolean.valueOf(support.isInDef());
+                    support.setInDef(false);
     return yyVal;
   }
 };
-states[101] = new ParserState() {
+states[241] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.backrefAssignError(((Node)yyVals[0+yyTop]));
+                    yyVal = support.newArrayNode(((ListNode)yyVals[-1+yyTop]).getPosition(), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
     return yyVal;
   }
 };
-states[102] = new ParserState() {
+states[476] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.yyerror("class/module name must be CONSTANT");
+                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[104] = new ParserState() {
+states[342] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_colon3(((Token)yyVals[-1+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-4+yyTop]).getPosition(), ((ListNode)yyVals[-4+yyTop]), new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[105] = new ParserState() {
+states[275] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_colon2(((Token)yyVals[0+yyTop]).getPosition(), null, (String) ((Token)yyVals[0+yyTop]).getValue());
+                    lexer.setState(LexState.EXPR_ENDARG); 
     return yyVal;
   }
 };
-states[106] = new ParserState() {
+states[7] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[110] = new ParserState() {
+states[208] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                   lexer.setState(LexState.EXPR_ENDFN);
-                   yyVal = ((Token)yyVals[0+yyTop]);
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "/", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[111] = new ParserState() {
+states[74] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                   lexer.setState(LexState.EXPR_ENDFN);
-                   yyVal = ((Token)yyVals[0+yyTop]);
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), (ListNode) null);
     return yyVal;
   }
 };
-states[112] = new ParserState() {
+states[376] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new LiteralNode(((Token)yyVals[0+yyTop]));
+                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[113] = new ParserState() {
+states[108] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new LiteralNode(((Token)yyVals[0+yyTop]));
+                    yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[114] = new ParserState() {
+states[309] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((LiteralNode)yyVals[0+yyTop]);
+                    yyVal = Integer.valueOf(support.getInSingle());
+                    support.setInSingle(0);
+                    support.pushLocalScope();
     return yyVal;
   }
 };
-states[115] = new ParserState() {
+states[41] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    support.checkExpression(((Node)yyVals[0+yyTop]));
+                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[116] = new ParserState() {
+states[242] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newUndef(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
+                    yyVal = ((Node)yyVals[-1+yyTop]);
+                    if (yyVal != null) ((Node)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[117] = new ParserState() {
+states[477] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.setState(LexState.EXPR_FNAME);
+                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[118] = new ParserState() {
+states[410] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.appendToBlock(((Node)yyVals[-3+yyTop]), support.newUndef(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[0+yyTop])));
+                    /* FIXME: We may be intern'ing more than once.*/
+                    yyVal = new SymbolNode(((Token)yyVals[0+yyTop]).getPosition(), ((String) ((Token)yyVals[0+yyTop]).getValue()).intern());
     return yyVal;
   }
 };
-states[190] = new ParserState() {
+states[343] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
-                    /* FIXME: Consider fixing node_assign itself rather than single case*/
-                    ((Node)yyVal).setPosition(support.getPosition(((Node)yyVals[-2+yyTop])));
+                    yyVal = new MultipleAsgn19Node(((Token)yyVals[-1+yyTop]).getPosition(), null, support.assignable(((Token)yyVals[0+yyTop]), null), null);
     return yyVal;
   }
 };
-states[191] = new ParserState() {
+states[276] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ISourcePosition position = ((Token)yyVals[-1+yyTop]).getPosition();
-                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
-                    yyVal = support.node_assign(((Node)yyVals[-4+yyTop]), new RescueNode(position, ((Node)yyVals[-2+yyTop]), new RescueBodyNode(position, null, body, null), null));
+                    support.warning(ID.GROUPED_EXPRESSION, ((Token)yyVals[-3+yyTop]).getPosition(), "(...) interpreted as grouped expression");
+                    yyVal = ((Node)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
-states[192] = new ParserState() {
+states[209] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.checkExpression(((Node)yyVals[0+yyTop]));
-
-                    ISourcePosition pos = ((AssignableNode)yyVals[-2+yyTop]).getPosition();
-                    String asgnOp = (String) ((Token)yyVals[-1+yyTop]).getValue();
-                    if (asgnOp.equals("||")) {
-                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
-                        yyVal = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
-                    } else if (asgnOp.equals("&&")) {
-                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
-                        yyVal = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
-                    } else {
-                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
-                        ((AssignableNode)yyVals[-2+yyTop]).setPosition(pos);
-                        yyVal = ((AssignableNode)yyVals[-2+yyTop]);
-                    }
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "%", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[193] = new ParserState() {
+states[75] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.checkExpression(((Node)yyVals[-2+yyTop]));
-                    ISourcePosition pos = ((Token)yyVals[-1+yyTop]).getPosition();
-                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
-                    Node rest;
-
-                    pos = ((AssignableNode)yyVals[-4+yyTop]).getPosition();
-                    String asgnOp = (String) ((Token)yyVals[-3+yyTop]).getValue();
-                    if (asgnOp.equals("||")) {
-                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(((Node)yyVals[-2+yyTop]));
-                        rest = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
-                    } else if (asgnOp.equals("&&")) {
-                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(((Node)yyVals[-2+yyTop]));
-                        rest = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
-                    } else {
-                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-4+yyTop])), asgnOp, ((Node)yyVals[-2+yyTop])));
-                        ((AssignableNode)yyVals[-4+yyTop]).setPosition(pos);
-                        rest = ((AssignableNode)yyVals[-4+yyTop]);
-                    }
-
-                    yyVal = new RescueNode(((Token)yyVals[-1+yyTop]).getPosition(), rest, new RescueBodyNode(((Token)yyVals[-1+yyTop]).getPosition(), null, body, null), null);
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-4+yyTop]).getPosition(), ((ListNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[194] = new ParserState() {
+states[511] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-  /* FIXME: arg_concat missing for opt_call_args*/
-                    yyVal = support.new_opElementAsgnNode(support.getPosition(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-5+yyTop]), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
+                    if (!support.is_local_id(((Token)yyVals[0+yyTop]))) {
+                        support.yyerror("block argument must be local variable");
+                    }
+                    
+                    yyVal = new BlockArgNode(support.arg_var(support.shadowing_lvar(((Token)yyVals[0+yyTop]))));
     return yyVal;
   }
 };
-states[195] = new ParserState() {
+states[377] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
+                    yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[196] = new ParserState() {
+states[310] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
+                    yyVal = new SClassNode(((Token)yyVals[-7+yyTop]).getPosition(), ((Node)yyVals[-5+yyTop]), support.getCurrentScope(), ((Node)yyVals[-1+yyTop]));
+                    support.popCurrentScope();
+                    support.setInDef(((Boolean)yyVals[-4+yyTop]).booleanValue());
+                    support.setInSingle(((Integer)yyVals[-2+yyTop]).intValue());
     return yyVal;
   }
 };
-states[197] = new ParserState() {
+states[42] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
+                    support.checkExpression(((Node)yyVals[0+yyTop]));
+                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[198] = new ParserState() {
+states[478] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.yyerror("constant re-assignment");
+                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[199] = new ParserState() {
+states[344] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.yyerror("constant re-assignment");
+                    yyVal = new MultipleAsgn19Node(((Token)yyVals[-3+yyTop]).getPosition(), null, support.assignable(((Token)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[200] = new ParserState() {
+states[9] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.backrefAssignError(((Node)yyVals[-2+yyTop]));
+                    if (support.isInDef() || support.isInSingle()) {
+                        support.yyerror("BEGIN in method");
+                    }
     return yyVal;
   }
 };
-states[201] = new ParserState() {
+states[210] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.checkExpression(((Node)yyVals[-2+yyTop]));
-                    support.checkExpression(((Node)yyVals[0+yyTop]));
-    
-                    boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
-                    yyVal = new DotNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), false, isLiteral);
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[202] = new ParserState() {
+states[76] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.checkExpression(((Node)yyVals[-2+yyTop]));
-                    support.checkExpression(((Node)yyVals[0+yyTop]));
-
-                    boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
-                    yyVal = new DotNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), true, isLiteral);
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), new StarNode(lexer.getPosition()), null);
     return yyVal;
   }
 };
-states[203] = new ParserState() {
+states[277] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "+", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    if (((Node)yyVals[-1+yyTop]) != null) {
+                        /* compstmt position includes both parens around it*/
+                        ((ISourcePositionHolder) ((Node)yyVals[-1+yyTop])).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
+                        yyVal = ((Node)yyVals[-1+yyTop]);
+                    } else {
+                        yyVal = new NilNode(((Token)yyVals[-2+yyTop]).getPosition());
+                    }
     return yyVal;
   }
 };
-states[204] = new ParserState() {
+states[512] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "-", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = ((BlockArgNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[205] = new ParserState() {
+states[378] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "*", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[206] = new ParserState() {
+states[311] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "/", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    if (support.isInDef() || support.isInSingle()) { 
+                        support.yyerror("module definition in method body");
+                    }
+                    support.pushLocalScope();
     return yyVal;
   }
 };
-states[207] = new ParserState() {
+states[479] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "%", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[208] = new ParserState() {
+states[412] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = ((Node)yyVals[0+yyTop]) instanceof EvStrNode ? new DStrNode(((Node)yyVals[0+yyTop]).getPosition(), lexer.getEncoding()).add(((Node)yyVals[0+yyTop])) : ((Node)yyVals[0+yyTop]);
+                    /*
+                    NODE *node = $1;
+                    if (!node) {
+                        node = NEW_STR(STR_NEW0());
+                    } else {
+                        node = evstr2dstr(node);
+                    }
+                    $$ = node;
+                    */
     return yyVal;
   }
 };
-states[209] = new ParserState() {
+states[345] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition()), "-@");
+                    yyVal = new MultipleAsgn19Node(((Token)yyVals[0+yyTop]).getPosition(), null, new StarNode(lexer.getPosition()), null);
     return yyVal;
   }
 };
-states[210] = new ParserState() {
+states[10] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((FloatNode)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition()), "-@");
+                    support.getResult().addBeginNode(new PreExe19Node(((Token)yyVals[-4+yyTop]).getPosition(), support.getCurrentScope(), ((Node)yyVals[-1+yyTop])));
+                    yyVal = null;
     return yyVal;
   }
 };
 states[211] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "+@");
-    return yyVal;
-  }
-};
-states[212] = new ParserState() {
-  public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "-@");
-    return yyVal;
-  }
-};
-states[213] = new ParserState() {
-  public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "|", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition()), "-@");
     return yyVal;
   }
 };
-states[214] = new ParserState() {
+states[77] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "^", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[215] = new ParserState() {
+states[278] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "&", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[216] = new ParserState() {
+states[513] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = null;
     return yyVal;
   }
 };
-states[217] = new ParserState() {
+states[379] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    support.pushBlockScope();
     return yyVal;
   }
 };
-states[218] = new ParserState() {
+states[312] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
+
+                    yyVal = new ModuleNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Colon3Node)yyVals[-3+yyTop]), support.getCurrentScope(), body);
+                    support.popCurrentScope();
     return yyVal;
   }
 };
-states[219] = new ParserState() {
+states[44] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = support.newAndNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[220] = new ParserState() {
+states[480] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[221] = new ParserState() {
+states[413] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "==", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    ByteList aChar = ByteList.create((String) ((Token)yyVals[0+yyTop]).getValue());
+                    aChar.setEncoding(lexer.getEncoding());
+                    yyVal = lexer.createStrNode(((Token)yyVals[-1+yyTop]).getPosition(), aChar, 0);
     return yyVal;
   }
 };
-states[222] = new ParserState() {
+states[346] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "===", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = new MultipleAsgn19Node(((Token)yyVals[-2+yyTop]).getPosition(), null, null, ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[223] = new ParserState() {
+states[11] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "!=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                  Node node = ((Node)yyVals[-3+yyTop]);
+
+                  if (((RescueBodyNode)yyVals[-2+yyTop]) != null) {
+                      node = new RescueNode(support.getPosition(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-3+yyTop]), ((RescueBodyNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
+                  } else if (((Node)yyVals[-1+yyTop]) != null) {
+                      support.warn(ID.ELSE_WITHOUT_RESCUE, support.getPosition(((Node)yyVals[-3+yyTop])), "else without rescue is useless");
+                      node = support.appendToBlock(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
+                  }
+                  if (((Node)yyVals[0+yyTop]) != null) {
+                      if (node == null) node = NilImplicitNode.NIL;
+                      node = new EnsureNode(support.getPosition(((Node)yyVals[-3+yyTop])), node, ((Node)yyVals[0+yyTop]));
+                  }
+
+                  yyVal = node;
     return yyVal;
   }
 };
-states[224] = new ParserState() {
+states[212] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
-                  /* ENEBO
-                        $$ = match_op($1, $3);
-                        if (nd_type($1) == NODE_LIT && TYPE($1->nd_lit) == T_REGEXP) {
-                            $$ = reg_named_capture_assign($1->nd_lit, $$);
-                        }
-                  */
+                    yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((FloatNode)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition()), "-@");
     return yyVal;
   }
 };
-states[225] = new ParserState() {
+states[78] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new NotNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
+                    yyVal = new MultipleAsgn19Node(((Token)yyVals[-1+yyTop]).getPosition(), null, ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[226] = new ParserState() {
+states[279] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
+                    yyVal = support.new_colon3(((Token)yyVals[-1+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[227] = new ParserState() {
+states[514] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "~");
+                    if (!(((Node)yyVals[0+yyTop]) instanceof SelfNode)) {
+                        support.checkExpression(((Node)yyVals[0+yyTop]));
+                    }
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[228] = new ParserState() {
+states[447] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                     lexer.setState(LexState.EXPR_END);
+
+                     /* DStrNode: :"some text #{some expression}"*/
+                     /* StrNode: :"some text"*/
+                     /* EvStrNode :"#{some expression}"*/
+                     /* Ruby 1.9 allows empty strings as symbols*/
+                     if (((Node)yyVals[-1+yyTop]) == null) {
+                         yyVal = new SymbolNode(((Token)yyVals[-2+yyTop]).getPosition(), "");
+                     } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
+                         yyVal = new DSymbolNode(((Token)yyVals[-2+yyTop]).getPosition(), ((DStrNode)yyVals[-1+yyTop]));
+                     } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
+                         yyVal = new SymbolNode(((Token)yyVals[-2+yyTop]).getPosition(), ((StrNode)yyVals[-1+yyTop]).getValue().toString().intern());
+                     } else {
+                         yyVal = new DSymbolNode(((Token)yyVals[-2+yyTop]).getPosition());
+                         ((DSymbolNode)yyVal).add(((Node)yyVals[-1+yyTop]));
+                     }
     return yyVal;
   }
 };
-states[229] = new ParserState() {
+states[380] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+                    yyVal = new IterNode(support.getPosition(((Token)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
+                    support.popCurrentScope();
     return yyVal;
   }
 };
-states[230] = new ParserState() {
+states[313] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newAndNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                    support.setInDef(true);
+                    support.pushLocalScope();
     return yyVal;
   }
 };
-states[231] = new ParserState() {
+states[45] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newOrNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[232] = new ParserState() {
+states[112] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    /* ENEBO: arg surrounded by in_defined set/unset*/
-                    yyVal = new DefinedNode(((Token)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
+                   lexer.setState(LexState.EXPR_ENDFN);
+                   yyVal = ((Token)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[233] = new ParserState() {
+states[548] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new IfNode(support.getPosition(((Node)yyVals[-5+yyTop])), support.getConditionNode(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
+                      yyVal = null;
     return yyVal;
   }
 };
-states[234] = new ParserState() {
+states[481] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[235] = new ParserState() {
+states[414] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.checkExpression(((Node)yyVals[0+yyTop]));
-                    yyVal = ((Node)yyVals[0+yyTop]) != null ? ((Node)yyVals[0+yyTop]) : NilImplicitNode.NIL;
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[237] = new ParserState() {
+states[347] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[-1+yyTop]);
+                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[238] = new ParserState() {
+states[12] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
+                    if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
+                        support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
+                    }
+                    yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[239] = new ParserState() {
+states[213] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newArrayNode(((ListNode)yyVals[-1+yyTop]).getPosition(), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "+@");
     return yyVal;
   }
 };
-states[240] = new ParserState() {
+states[79] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[-1+yyTop]);
-                    if (yyVal != null) ((Node)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
+                    yyVal = new MultipleAsgn19Node(((Token)yyVals[-3+yyTop]).getPosition(), null, ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[245] = new ParserState() {
+states[280] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newArrayNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
+                    ISourcePosition position = ((Token)yyVals[-2+yyTop]).getPosition();
+                    if (((Node)yyVals[-1+yyTop]) == null) {
+                        yyVal = new ZArrayNode(position); /* zero length array */
+                    } else {
+                        yyVal = ((Node)yyVals[-1+yyTop]);
+                        ((ISourcePositionHolder)yyVal).setPosition(position);
+                    }
     return yyVal;
   }
 };
-states[246] = new ParserState() {
+states[515] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.arg_blk_pass(((Node)yyVals[-1+yyTop]), ((BlockPassNode)yyVals[0+yyTop]));
+                    lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
-states[247] = new ParserState() {
+states[448] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newArrayNode(((ListNode)yyVals[-1+yyTop]).getPosition(), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
-                    yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[248] = new ParserState() {
+states[381] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
-                    yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                    if (((Node)yyVals[-1+yyTop]) instanceof YieldNode) {
+                        throw new SyntaxException(PID.BLOCK_GIVEN_TO_YIELD, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "block given to yield");
+                    }
+                    if (((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
+                        throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
+                    }
+                    yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
+                    ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[249] = new ParserState() {
+states[46] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
+                    yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
     return yyVal;
   }
 };
-states[250] = new ParserState() {
+states[247] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = Long.valueOf(lexer.getCmdArgumentState().begin());
+                    yyVal = support.newArrayNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[251] = new ParserState() {
+states[113] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                   lexer.setState(LexState.EXPR_ENDFN);
+                   yyVal = ((Token)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[252] = new ParserState() {
+states[314] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new BlockPassNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
+                    /* TODO: We should use implicit nil for body, but problem (punt til later)*/
+                    Node body = ((Node)yyVals[-1+yyTop]); /*$5 == null ? NilImplicitNode.NIL : $5;*/
+
+                    yyVal = new DefnNode(((Token)yyVals[-5+yyTop]).getPosition(), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
+                    support.popCurrentScope();
+                    support.setInDef(false);
     return yyVal;
   }
 };
-states[253] = new ParserState() {
+states[549] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((BlockPassNode)yyVals[0+yyTop]);
+                  yyVal = null;
     return yyVal;
   }
 };
-states[254] = new ParserState() {
+states[482] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = null;
+                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[256] = new ParserState() {
+states[415] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ISourcePosition pos = ((Node)yyVals[0+yyTop]) == null ? lexer.getPosition() : ((Node)yyVals[0+yyTop]).getPosition();
-                    yyVal = support.newArrayNode(pos, ((Node)yyVals[0+yyTop]));
+                    yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[257] = new ParserState() {
+states[348] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newSplatNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
+                    yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[258] = new ParserState() {
+states[214] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    Node node = support.splat_array(((Node)yyVals[-2+yyTop]));
-
-                    if (node != null) {
-                        yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
-                    } else {
-                        yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
-                    }
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "-@");
     return yyVal;
   }
 };
-states[259] = new ParserState() {
+states[80] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    Node node = null;
-
-                    /* FIXME: lose syntactical elements here (and others like this)*/
-                    if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
-                        (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
-                        yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
-                    } else {
-                        yyVal = support.arg_concat(support.getPosition(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
-                    }
+                      yyVal = new MultipleAsgn19Node(((Token)yyVals[0+yyTop]).getPosition(), null, new StarNode(lexer.getPosition()), null);
     return yyVal;
   }
 };
-states[260] = new ParserState() {
+states[281] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    Node node = support.splat_array(((Node)yyVals[-2+yyTop]));
-
-                    if (node != null) {
-                        yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
-                    } else {
-                        yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
-                    }
+                    yyVal = new Hash19Node(((Token)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[261] = new ParserState() {
+states[516] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    Node node = null;
-
-                    if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
-                        (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
-                        yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
-                    } else {
-                        yyVal = support.arg_concat(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
+                    if (((Node)yyVals[-1+yyTop]) == null) {
+                        support.yyerror("can't define single method for ().");
+                    } else if (((Node)yyVals[-1+yyTop]) instanceof ILiteralNode) {
+                        support.yyerror("can't define single method for literals.");
                     }
+                    support.checkExpression(((Node)yyVals[-1+yyTop]));
+                    yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[262] = new ParserState() {
+states[449] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = support.newSplatNode(support.getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[0+yyTop]));  
+                     yyVal = ((FloatNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[271] = new ParserState() {
+states[382] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new FCallNoArgNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[272] = new ParserState() {
+states[47] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new BeginNode(support.getPosition(((Token)yyVals[-2+yyTop])), ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]));
+                    yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
     return yyVal;
   }
 };
-states[273] = new ParserState() {
+states[248] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.setState(LexState.EXPR_ENDARG); 
+                    yyVal = support.arg_blk_pass(((Node)yyVals[-1+yyTop]), ((BlockPassNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[274] = new ParserState() {
+states[114] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.warning(ID.GROUPED_EXPRESSION, ((Token)yyVals[-3+yyTop]).getPosition(), "(...) interpreted as grouped expression");
-                    yyVal = ((Node)yyVals[-2+yyTop]);
+                    yyVal = new LiteralNode(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[275] = new ParserState() {
+states[315] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (((Node)yyVals[-1+yyTop]) != null) {
-                        /* compstmt position includes both parens around it*/
-                        ((ISourcePositionHolder) ((Node)yyVals[-1+yyTop])).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
-                        yyVal = ((Node)yyVals[-1+yyTop]);
-                    } else {
-                        yyVal = new NilNode(((Token)yyVals[-2+yyTop]).getPosition());
-                    }
+                    lexer.setState(LexState.EXPR_FNAME);
     return yyVal;
   }
 };
-states[276] = new ParserState() {
+states[483] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[277] = new ParserState() {
+states[416] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_colon3(((Token)yyVals[-1+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    yyVal = ((Node)yyVals[-1+yyTop]);
+
+                    ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
+                    int extraLength = ((String) ((Token)yyVals[-2+yyTop]).getValue()).length() - 1;
+
+                    /* We may need to subtract addition offset off of first */
+                    /* string fragment (we optimistically take one off in*/
+                    /* ParserSupport.literal_concat).  Check token length*/
+                    /* and subtract as neeeded.*/
+                    if ((((Node)yyVals[-1+yyTop]) instanceof DStrNode) && extraLength > 0) {
+                      Node strNode = ((DStrNode)((Node)yyVals[-1+yyTop])).get(0);
+                    }
     return yyVal;
   }
 };
-states[278] = new ParserState() {
+states[349] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ISourcePosition position = ((Token)yyVals[-2+yyTop]).getPosition();
-                    if (((Node)yyVals[-1+yyTop]) == null) {
-                        yyVal = new ZArrayNode(position); /* zero length array */
-                    } else {
-                        yyVal = ((Node)yyVals[-1+yyTop]);
-                        ((ISourcePositionHolder)yyVal).setPosition(position);
-                    }
+                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[279] = new ParserState() {
+states[14] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new Hash19Node(((Token)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]));
+                    yyVal = support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
-states[280] = new ParserState() {
+states[215] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new ReturnNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "|", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[281] = new ParserState() {
+states[81] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_yield(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
+                      yyVal = new MultipleAsgn19Node(((Token)yyVals[-2+yyTop]).getPosition(), null, new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[282] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new ZYieldNode(((Token)yyVals[-2+yyTop]).getPosition());
+                    yyVal = new ReturnNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[283] = new ParserState() {
+states[517] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new ZYieldNode(((Token)yyVals[0+yyTop]).getPosition());
+                    yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
-states[284] = new ParserState() {
+states[450] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new DefinedNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
+                     yyVal = support.negateInteger(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[285] = new ParserState() {
+states[383] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[-1+yyTop])), "!");
+                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[286] = new ParserState() {
+states[249] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
+                    yyVal = support.newArrayNode(((ListNode)yyVals[-1+yyTop]).getPosition(), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
+                    yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[287] = new ParserState() {
+states[115] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new FCallNoArgBlockNode(((Token)yyVals[-1+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((IterNode)yyVals[0+yyTop]));
+                    yyVal = new LiteralNode(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[289] = new ParserState() {
+states[316] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (((Node)yyVals[-1+yyTop]) != null && 
-                          ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
-                        throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
-                    }
-                    yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
-                    ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
+                    support.setInSingle(support.getInSingle() + 1);
+                    support.pushLocalScope();
+                    lexer.setState(LexState.EXPR_ENDFN); /* force for args */
     return yyVal;
   }
 };
-states[290] = new ParserState() {
+states[484] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((LambdaNode)yyVals[0+yyTop]);
+                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[291] = new ParserState() {
+states[417] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new IfNode(((Token)yyVals[-5+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
+                    ISourcePosition position = ((Token)yyVals[-2+yyTop]).getPosition();
+
+                    if (((Node)yyVals[-1+yyTop]) == null) {
+                        yyVal = new XStrNode(position, null);
+                    } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
+                        yyVal = new XStrNode(position, (ByteList) ((StrNode)yyVals[-1+yyTop]).getValue().clone());
+                    } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
+                        yyVal = new DXStrNode(position, ((DStrNode)yyVals[-1+yyTop]));
+
+                        ((Node)yyVal).setPosition(position);
+                    } else {
+                        yyVal = new DXStrNode(position).add(((Node)yyVals[-1+yyTop]));
+                    }
     return yyVal;
   }
 };
-states[292] = new ParserState() {
+states[350] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new IfNode(((Token)yyVals[-5+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-2+yyTop]));
+                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[293] = new ParserState() {
+states[15] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.getConditionState().begin();
+                    yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
     return yyVal;
   }
 };
-states[294] = new ParserState() {
+states[216] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.getConditionState().end();
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "^", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[295] = new ParserState() {
+states[283] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
-                    yyVal = new WhileNode(((Token)yyVals[-6+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
+                    yyVal = support.new_yield(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[296] = new ParserState() {
+states[518] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                  lexer.getConditionState().begin();
+                    yyVal = ((ListNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[297] = new ParserState() {
+states[451] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                  lexer.getConditionState().end();
+                     yyVal = support.negateFloat(((FloatNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[298] = new ParserState() {
+states[384] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
-                    yyVal = new UntilNode(((Token)yyVals[-6+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
+                    yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[299] = new ParserState() {
+states[49] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newCaseNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
+                    support.checkExpression(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[300] = new ParserState() {
+states[250] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newCaseNode(((Token)yyVals[-3+yyTop]).getPosition(), null, ((Node)yyVals[-1+yyTop]));
+                    yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
+                    yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[301] = new ParserState() {
+states[116] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.getConditionState().begin();
+                    yyVal = ((LiteralNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[302] = new ParserState() {
+states[317] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.getConditionState().end();
+                    /* TODO: We should use implicit nil for body, but problem (punt til later)*/
+                    Node body = ((Node)yyVals[-1+yyTop]); /*$8 == null ? NilImplicitNode.NIL : $8;*/
+
+                    yyVal = new DefsNode(((Token)yyVals[-8+yyTop]).getPosition(), ((Node)yyVals[-7+yyTop]), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
+                    support.popCurrentScope();
+                    support.setInSingle(support.getInSingle() - 1);
     return yyVal;
   }
 };
-states[303] = new ParserState() {
+states[485] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                      /* ENEBO: Lots of optz in 1.9 parser here*/
-                    yyVal = new ForNode(((Token)yyVals[-8+yyTop]).getPosition(), ((Node)yyVals[-7+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-4+yyTop]), support.getCurrentScope());
+                    yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[304] = new ParserState() {
+states[418] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (support.isInDef() || support.isInSingle()) {
-                        support.yyerror("class definition in method body");
-                    }
-                    support.pushLocalScope();
+                    yyVal = support.newRegexpNode(((Token)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), (RegexpNode) ((RegexpNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[305] = new ParserState() {
+states[351] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
-
-                    yyVal = new ClassNode(((Token)yyVals[-5+yyTop]).getPosition(), ((Colon3Node)yyVals[-4+yyTop]), support.getCurrentScope(), body, ((Node)yyVals[-3+yyTop]));
-                    support.popCurrentScope();
+                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[306] = new ParserState() {
+states[16] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = Boolean.valueOf(support.isInDef());
-                    support.setInDef(false);
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[307] = new ParserState() {
+states[217] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = Integer.valueOf(support.getInSingle());
-                    support.setInSingle(0);
-                    support.pushLocalScope();
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "&", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[308] = new ParserState() {
+states[83] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new SClassNode(((Token)yyVals[-7+yyTop]).getPosition(), ((Node)yyVals[-5+yyTop]), support.getCurrentScope(), ((Node)yyVals[-1+yyTop]));
-                    support.popCurrentScope();
-                    support.setInDef(((Boolean)yyVals[-4+yyTop]).booleanValue());
-                    support.setInSingle(((Integer)yyVals[-2+yyTop]).intValue());
+                    yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[309] = new ParserState() {
+states[284] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (support.isInDef() || support.isInSingle()) { 
-                        support.yyerror("module definition in method body");
-                    }
-                    support.pushLocalScope();
+                    yyVal = new ZYieldNode(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[310] = new ParserState() {
+states[385] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
-
-                    yyVal = new ModuleNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Colon3Node)yyVals[-3+yyTop]), support.getCurrentScope(), body);
-                    support.popCurrentScope();
+                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[311] = new ParserState() {
+states[251] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.setInDef(true);
-                    support.pushLocalScope();
     return yyVal;
   }
 };
-states[312] = new ParserState() {
+states[117] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    /* TODO: We should use implicit nil for body, but problem (punt til later)*/
-                    Node body = ((Node)yyVals[-1+yyTop]); /*$5 == null ? NilImplicitNode.NIL : $5;*/
-
-                    yyVal = new DefnNode(((Token)yyVals[-5+yyTop]).getPosition(), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
-                    support.popCurrentScope();
-                    support.setInDef(false);
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[313] = new ParserState() {
+states[318] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.setState(LexState.EXPR_FNAME);
+                    yyVal = new BreakNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[314] = new ParserState() {
+states[486] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.setInSingle(support.getInSingle() + 1);
-                    support.pushLocalScope();
-                    lexer.setState(LexState.EXPR_ENDFN); /* force for args */
+                    yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[315] = new ParserState() {
+states[419] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    /* TODO: We should use implicit nil for body, but problem (punt til later)*/
-                    Node body = ((Node)yyVals[-1+yyTop]); /*$8 == null ? NilImplicitNode.NIL : $8;*/
-
-                    yyVal = new DefsNode(((Token)yyVals[-8+yyTop]).getPosition(), ((Node)yyVals[-7+yyTop]), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
-                    support.popCurrentScope();
-                    support.setInSingle(support.getInSingle() - 1);
+                    yyVal = new ZArrayNode(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[316] = new ParserState() {
+states[352] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new BreakNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
+                    RestArgNode rest = new UnnamedRestArgNode(((ListNode)yyVals[-1+yyTop]).getPosition(), null, support.getCurrentScope().addVariable("*"));
+                    yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, rest, null, null);
     return yyVal;
   }
 };
-states[317] = new ParserState() {
+states[17] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new NextNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
+                    lexer.setState(LexState.EXPR_FNAME);
     return yyVal;
   }
 };
-states[318] = new ParserState() {
+states[218] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new RedoNode(((Token)yyVals[0+yyTop]).getPosition());
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[319] = new ParserState() {
+states[84] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new RetryNode(((Token)yyVals[0+yyTop]).getPosition());
+                    yyVal = support.newArrayNode(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[320] = new ParserState() {
+states[285] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.checkExpression(((Node)yyVals[0+yyTop]));
-                    yyVal = ((Node)yyVals[0+yyTop]);
-                    if (yyVal == null) yyVal = NilImplicitNode.NIL;
+                    yyVal = new ZYieldNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[327] = new ParserState() {
+states[520] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new IfNode(((Token)yyVals[-4+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
+                    yyVal = ((ListNode)yyVals[-2+yyTop]).addAll(((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[329] = new ParserState() {
+states[386] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[331] = new ParserState() {
+states[252] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
+                    yyVal = Long.valueOf(lexer.getCmdArgumentState().begin());
     return yyVal;
   }
 };
-states[332] = new ParserState() {
+states[118] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
+                    yyVal = support.newUndef(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[333] = new ParserState() {
+states[319] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[-1+yyTop]);
+                    yyVal = new NextNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[334] = new ParserState() {
+states[487] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
+                    yyVal = support.new_args(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[335] = new ParserState() {
+states[420] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
+                    yyVal = ((ListNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
-states[336] = new ParserState() {
+states[353] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[0+yyTop]).getPosition(), ((ListNode)yyVals[0+yyTop]), null, null);
+                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[337] = new ParserState() {
+states[219] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), support.assignable(((Token)yyVals[0+yyTop]), null), null);
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[338] = new ParserState() {
+states[85] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), support.assignable(((Token)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
+                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[339] = new ParserState() {
+states[286] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-2+yyTop]), new StarNode(lexer.getPosition()), null);
+                    yyVal = new DefinedNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[340] = new ParserState() {
+states[18] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-4+yyTop]).getPosition(), ((ListNode)yyVals[-4+yyTop]), new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
+                    yyVal = support.newAlias(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[341] = new ParserState() {
+states[521] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((Token)yyVals[-1+yyTop]).getPosition(), null, support.assignable(((Token)yyVals[0+yyTop]), null), null);
+                    ISourcePosition pos;
+                    if (((Node)yyVals[-2+yyTop]) == null && ((Node)yyVals[0+yyTop]) == null) {
+                        pos = ((Token)yyVals[-1+yyTop]).getPosition();
+                    } else {
+                        pos = ((Node)yyVals[-2+yyTop]).getPosition();
+                    }
+
+                    yyVal = support.newArrayNode(pos, ((Node)yyVals[-2+yyTop])).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[342] = new ParserState() {
+states[387] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((Token)yyVals[-3+yyTop]).getPosition(), null, support.assignable(((Token)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
+                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]), null, null);
     return yyVal;
   }
 };
-states[343] = new ParserState() {
+states[52] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((Token)yyVals[0+yyTop]).getPosition(), null, new StarNode(lexer.getPosition()), null);
+                    yyVal = new ReturnNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
     return yyVal;
   }
 };
-states[344] = new ParserState() {
+states[253] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new MultipleAsgn19Node(((Token)yyVals[-2+yyTop]).getPosition(), null, null, ((ListNode)yyVals[0+yyTop]));
+                    lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[345] = new ParserState() {
+states[119] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
+                    lexer.setState(LexState.EXPR_FNAME);
     return yyVal;
   }
 };
-states[346] = new ParserState() {
+states[320] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = new RedoNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[347] = new ParserState() {
+states[488] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.new_args(lexer.getPosition(), null, null, null, null, null);
     return yyVal;
   }
 };
-states[348] = new ParserState() {
+states[421] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
-states[349] = new ParserState() {
+states[354] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[350] = new ParserState() {
+states[220] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    RestArgNode rest = new UnnamedRestArgNode(((ListNode)yyVals[-1+yyTop]).getPosition(), null, support.getCurrentScope().addVariable("*"));
-                    yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, rest, null, null);
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[351] = new ParserState() {
+states[86] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[352] = new ParserState() {
+states[287] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[-1+yyTop])), "!");
     return yyVal;
   }
 };
-states[353] = new ParserState() {
+states[19] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-3+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = new VAliasNode(((Token)yyVals[-2+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[354] = new ParserState() {
+states[522] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-5+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    ISourcePosition pos = ((Token)yyVals[-1+yyTop]).getPosition();
+                    yyVal = support.newArrayNode(pos, new SymbolNode(pos, (String) ((Token)yyVals[-1+yyTop]).getValue())).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[355] = new ParserState() {
+states[388] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-1+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[356] = new ParserState() {
+states[53] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = new BreakNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
     return yyVal;
   }
 };
-states[357] = new ParserState() {
+states[254] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = new BlockPassNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[358] = new ParserState() {
+states[120] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.appendToBlock(((Node)yyVals[-3+yyTop]), support.newUndef(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
-states[359] = new ParserState() {
+states[321] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = new RetryNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[360] = new ParserState() {
+states[489] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-    /* was $$ = null;*/
-                   yyVal = support.new_args(lexer.getPosition(), null, null, null, null, null);
+                    support.yyerror("formal argument cannot be a constant");
     return yyVal;
   }
 };
-states[361] = new ParserState() {
+states[422] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.commandStart = true;
-                    yyVal = ((ArgsNode)yyVals[0+yyTop]);
+                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DStrNode(((ListNode)yyVals[-2+yyTop]).getPosition(), lexer.getEncoding()).add(((Node)yyVals[-1+yyTop])) : ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[362] = new ParserState() {
+states[355] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((Token)yyVals[-2+yyTop]).getPosition(), null, null, null, null, null);
+                    yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-3+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[363] = new ParserState() {
+states[221] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((Token)yyVals[0+yyTop]).getPosition(), null, null, null, null, null);
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[364] = new ParserState() {
+states[87] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ArgsNode)yyVals[-2+yyTop]);
+                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[366] = new ParserState() {
+states[288] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = null;
+                    yyVal = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
     return yyVal;
   }
 };
-states[367] = new ParserState() {
+states[20] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = null;
+                    yyVal = new VAliasNode(((Token)yyVals[-2+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), "$" + ((BackRefNode)yyVals[0+yyTop]).getType());
     return yyVal;
   }
 };
-states[368] = new ParserState() {
+states[389] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = null;
+                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[369] = new ParserState() {
+states[54] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.new_bv(((Token)yyVals[0+yyTop]));
+                    yyVal = new NextNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
     return yyVal;
   }
 };
-states[370] = new ParserState() {
+states[255] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = null;
+                    yyVal = ((BlockPassNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[371] = new ParserState() {
+states[322] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.pushBlockScope();
-                    yyVal = lexer.getLeftParenBegin();
-                    lexer.setLeftParenBegin(lexer.incrementParenNest());
+                    support.checkExpression(((Node)yyVals[0+yyTop]));
+                    yyVal = ((Node)yyVals[0+yyTop]);
+                    if (yyVal == null) yyVal = NilImplicitNode.NIL;
     return yyVal;
   }
 };
-states[372] = new ParserState() {
+states[490] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new LambdaNode(((ArgsNode)yyVals[-1+yyTop]).getPosition(), ((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), support.getCurrentScope());
-                    support.popCurrentScope();
-                    lexer.setLeftParenBegin(((Integer)yyVals[-2+yyTop]));
+                    support.yyerror("formal argument cannot be an instance variable");
     return yyVal;
   }
 };
-states[373] = new ParserState() {
+states[356] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ArgsNode)yyVals[-2+yyTop]);
-                    ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-3+yyTop]).getPosition());
+                    yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-5+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[374] = new ParserState() {
+states[88] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
+                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[375] = new ParserState() {
+states[289] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[-1+yyTop]);
+                    yyVal = new FCallNoArgBlockNode(((Token)yyVals[-1+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((IterNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[376] = new ParserState() {
+states[21] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[-1+yyTop]);
+                    support.yyerror("can't make alias for the number variables");
     return yyVal;
   }
 };
-states[377] = new ParserState() {
+states[222] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.pushBlockScope();
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[378] = new ParserState() {
+states[457] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new IterNode(support.getPosition(((Token)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
-                    support.popCurrentScope();
+                    yyVal = new Token("nil", Tokens.kNIL, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[379] = new ParserState() {
+states[390] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
-                    if (((Node)yyVals[-1+yyTop]) instanceof YieldNode) {
-                        throw new SyntaxException(PID.BLOCK_GIVEN_TO_YIELD, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "block given to yield");
-                    }
-                    if (((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
-                        throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
-                    }
-                    yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
-                    ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
+                    yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[380] = new ParserState() {
+states[256] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
+                    yyVal = null;
     return yyVal;
   }
 };
-states[381] = new ParserState() {
+states[491] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
+                    support.yyerror("formal argument cannot be a global variable");
     return yyVal;
   }
 };
-states[382] = new ParserState() {
+states[424] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
+                     yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[383] = new ParserState() {
+states[357] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
+                    yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-1+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[384] = new ParserState() {
+states[89] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
+                    yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[385] = new ParserState() {
+states[22] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]), null, null);
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[386] = new ParserState() {
+states[223] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "==", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[387] = new ParserState() {
+states[458] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
+                    yyVal = new Token("self", Tokens.kSELF, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[388] = new ParserState() {
+states[391] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]));
+                    yyVal = new ZSuperNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[389] = new ParserState() {
+states[56] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new ZSuperNode(((Token)yyVals[0+yyTop]).getPosition());
+                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[390] = new ParserState() {
+states[492] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (((Node)yyVals[-3+yyTop]) instanceof SelfNode) {
-                        yyVal = support.new_fcall(new Token("[]", support.getPosition(((Node)yyVals[-3+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
-                    } else {
-                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), new Token("[]", support.getPosition(((Node)yyVals[-3+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
-                    }
+                    support.yyerror("formal argument cannot be a class variable");
     return yyVal;
   }
 };
-states[391] = new ParserState() {
+states[425] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.pushBlockScope();
+                     yyVal = new ZArrayNode(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[392] = new ParserState() {
+states[358] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new IterNode(((Token)yyVals[-4+yyTop]).getPosition(), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
-                    support.popCurrentScope();
+                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[393] = new ParserState() {
+states[90] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.pushBlockScope();
+                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[394] = new ParserState() {
+states[291] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new IterNode(((Token)yyVals[-4+yyTop]).getPosition(), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
-                    /* FIXME: What the hell is this?*/
-                    ((ISourcePositionHolder)yyVals[-5+yyTop]).setPosition(support.getPosition(((ISourcePositionHolder)yyVals[-5+yyTop])));
-                    support.popCurrentScope();
+                    if (((Node)yyVals[-1+yyTop]) != null && 
+                          ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
+                        throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
+                    }
+                    yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
+                    ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[395] = new ParserState() {
+states[23] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newWhenNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
+                    yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), null);
     return yyVal;
   }
 };
-states[398] = new ParserState() {
+states[224] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    Node node;
-                    if (((Node)yyVals[-3+yyTop]) != null) {
-                        node = support.appendToBlock(support.node_assign(((Node)yyVals[-3+yyTop]), new GlobalVarNode(((Token)yyVals[-5+yyTop]).getPosition(), "$!")), ((Node)yyVals[-1+yyTop]));
-                        if (((Node)yyVals[-1+yyTop]) != null) {
-                            node.setPosition(support.unwrapNewlineNode(((Node)yyVals[-1+yyTop])).getPosition());
-                        }
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "===", ((Node)yyVals[0+yyTop]), lexer.getPosition());
+    return yyVal;
+  }
+};
+states[459] = new ParserState() {
+  public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
+                    yyVal = new Token("true", Tokens.kTRUE, ((Token)yyVals[0+yyTop]).getPosition());
+    return yyVal;
+  }
+};
+states[392] = new ParserState() {
+  public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
+                    if (((Node)yyVals[-3+yyTop]) instanceof SelfNode) {
+                        yyVal = support.new_fcall(new Token("[]", support.getPosition(((Node)yyVals[-3+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
                     } else {
-                        node = ((Node)yyVals[-1+yyTop]);
+                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), new Token("[]", support.getPosition(((Node)yyVals[-3+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
                     }
-                    Node body = node == null ? NilImplicitNode.NIL : node;
-                    yyVal = new RescueBodyNode(((Token)yyVals[-5+yyTop]).getPosition(), ((Node)yyVals[-4+yyTop]), body, ((RescueBodyNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[399] = new ParserState() {
+states[258] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = null; 
+                    ISourcePosition pos = ((Node)yyVals[0+yyTop]) == null ? lexer.getPosition() : ((Node)yyVals[0+yyTop]).getPosition();
+                    yyVal = support.newArrayNode(pos, ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[400] = new ParserState() {
+states[57] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
+                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[401] = new ParserState() {
+states[426] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.splat_array(((Node)yyVals[0+yyTop]));
-                    if (yyVal == null) yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = ((ListNode)yyVals[-1+yyTop]);
+                    ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[403] = new ParserState() {
+states[359] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[405] = new ParserState() {
+states[91] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[408] = new ParserState() {
+states[292] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    /* FIXME: We may be intern'ing more than once.*/
-                    yyVal = new SymbolNode(((Token)yyVals[0+yyTop]).getPosition(), ((String) ((Token)yyVals[0+yyTop]).getValue()).intern());
+                    yyVal = ((LambdaNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[410] = new ParserState() {
+states[24] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]) instanceof EvStrNode ? new DStrNode(((Node)yyVals[0+yyTop]).getPosition(), lexer.getEncoding()).add(((Node)yyVals[0+yyTop])) : ((Node)yyVals[0+yyTop]);
-                    /*
-                    NODE *node = $1;
-                    if (!node) {
-                        node = NEW_STR(STR_NEW0());
-                    } else {
-                        node = evstr2dstr(node);
-                    }
-                    $$ = node;
-                    */
+                    yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), null, ((Node)yyVals[-2+yyTop]));
     return yyVal;
   }
 };
-states[411] = new ParserState() {
+states[225] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ByteList aChar = ByteList.create((String) ((Token)yyVals[0+yyTop]).getValue());
-                    aChar.setEncoding(lexer.getEncoding());
-                    yyVal = lexer.createStrNode(((Token)yyVals[-1+yyTop]).getPosition(), aChar, 0);
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "!=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[412] = new ParserState() {
+states[460] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = new Token("false", Tokens.kFALSE, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[413] = new ParserState() {
+states[393] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
+                    support.pushBlockScope();
     return yyVal;
   }
 };
-states[414] = new ParserState() {
+states[192] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[-1+yyTop]);
-
-                    ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
-                    int extraLength = ((String) ((Token)yyVals[-2+yyTop]).getValue()).length() - 1;
-
-                    /* We may need to subtract addition offset off of first */
-                    /* string fragment (we optimistically take one off in*/
-                    /* ParserSupport.literal_concat).  Check token length*/
-                    /* and subtract as neeeded.*/
-                    if ((((Node)yyVals[-1+yyTop]) instanceof DStrNode) && extraLength > 0) {
-                      Node strNode = ((DStrNode)((Node)yyVals[-1+yyTop])).get(0);
-                    }
+                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    ((Node)yyVal).setPosition(support.getPosition(((Node)yyVals[-2+yyTop])));
     return yyVal;
   }
 };
-states[415] = new ParserState() {
+states[58] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ISourcePosition position = ((Token)yyVals[-2+yyTop]).getPosition();
-
-                    if (((Node)yyVals[-1+yyTop]) == null) {
-                        yyVal = new XStrNode(position, null);
-                    } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
-                        yyVal = new XStrNode(position, (ByteList) ((StrNode)yyVals[-1+yyTop]).getValue().clone());
-                    } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
-                        yyVal = new DXStrNode(position, ((DStrNode)yyVals[-1+yyTop]));
-
-                        ((Node)yyVal).setPosition(position);
-                    } else {
-                        yyVal = new DXStrNode(position).add(((Node)yyVals[-1+yyTop]));
-                    }
+                    support.pushBlockScope();
     return yyVal;
   }
 };
-states[416] = new ParserState() {
+states[259] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.newRegexpNode(((Token)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), (RegexpNode) ((RegexpNode)yyVals[0+yyTop]));
+                    yyVal = support.newSplatNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[417] = new ParserState() {
+states[494] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new ZArrayNode(((Token)yyVals[-2+yyTop]).getPosition());
+                    yyVal = support.formal_argument(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[418] = new ParserState() {
+states[427] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ListNode)yyVals[-1+yyTop]);
+                    yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
-states[419] = new ParserState() {
+states[360] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new ArrayNode(lexer.getPosition());
+                    yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[420] = new ParserState() {
+states[293] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DStrNode(((ListNode)yyVals[-2+yyTop]).getPosition(), lexer.getEncoding()).add(((Node)yyVals[-1+yyTop])) : ((Node)yyVals[-1+yyTop]));
+                    yyVal = new IfNode(((Token)yyVals[-5+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[422] = new ParserState() {
+states[25] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
+                    if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
+                        yyVal = new WhileNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
+                    } else {
+                        yyVal = new WhileNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
+                    }
     return yyVal;
   }
 };
-states[423] = new ParserState() {
+states[226] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = new ZArrayNode(((Token)yyVals[-2+yyTop]).getPosition());
+                    yyVal = support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                  /* ENEBO
+                        $$ = match_op($1, $3);
+                        if (nd_type($1) == NODE_LIT && TYPE($1->nd_lit) == T_REGEXP) {
+                            $$ = reg_named_capture_assign($1->nd_lit, $$);
+                        }
+                  */
     return yyVal;
   }
 };
-states[424] = new ParserState() {
+states[92] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ListNode)yyVals[-1+yyTop]);
-                    ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
+                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[425] = new ParserState() {
+states[461] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new ArrayNode(lexer.getPosition());
+                    yyVal = new Token("__FILE__", Tokens.k__FILE__, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[426] = new ParserState() {
+states[394] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
+                    yyVal = new IterNode(((Token)yyVals[-4+yyTop]).getPosition(), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
+                    support.popCurrentScope();
     return yyVal;
   }
 };
-states[427] = new ParserState() {
+states[193] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ByteList aChar = ByteList.create("");
-                    aChar.setEncoding(lexer.getEncoding());
-                    yyVal = lexer.createStrNode(((Token)yyVals[0+yyTop]).getPosition(), aChar, 0);
+                    ISourcePosition position = ((Token)yyVals[-1+yyTop]).getPosition();
+                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
+                    yyVal = support.node_assign(((Node)yyVals[-4+yyTop]), new RescueNode(position, ((Node)yyVals[-2+yyTop]), new RescueBodyNode(position, null, body, null), null));
     return yyVal;
   }
 };
-states[428] = new ParserState() {
+states[59] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
+                    yyVal = new IterNode(((Token)yyVals[-4+yyTop]).getPosition(), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
+                    support.popCurrentScope();
     return yyVal;
   }
 };
-states[429] = new ParserState() {
+states[260] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = null;
+                    Node node = support.splat_array(((Node)yyVals[-2+yyTop]));
+
+                    if (node != null) {
+                        yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
+                    } else {
+                        yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                    }
     return yyVal;
   }
 };
-states[430] = new ParserState() {
+states[495] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
+                    yyVal = support.arg_var(((Token)yyVals[0+yyTop]));
+  /*
+                    $$ = new ArgAuxiliaryNode($1.getPosition(), (String) $1.getValue(), 1);
+  */
     return yyVal;
   }
 };
-states[431] = new ParserState() {
+states[428] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[432] = new ParserState() {
+states[361] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = lexer.getStrTerm();
-                    lexer.setStrTerm(null);
-                    lexer.setState(LexState.EXPR_BEG);
+                    yyVal = support.new_args(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[433] = new ParserState() {
+states[294] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.setStrTerm(((StrTerm)yyVals[-1+yyTop]));
-                    yyVal = new EvStrNode(((Token)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
+                    yyVal = new IfNode(((Token)yyVals[-5+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-2+yyTop]));
     return yyVal;
   }
 };
-states[434] = new ParserState() {
+states[26] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                   yyVal = lexer.getStrTerm();
-                   lexer.getConditionState().stop();
-                   lexer.getCmdArgumentState().stop();
-                   lexer.setStrTerm(null);
-                   lexer.setState(LexState.EXPR_BEG);
+                    if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
+                        yyVal = new UntilNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
+                    } else {
+                        yyVal = new UntilNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
+                    }
     return yyVal;
   }
 };
-states[435] = new ParserState() {
+states[227] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                   lexer.getConditionState().restart();
-                   lexer.getCmdArgumentState().restart();
-                   lexer.setStrTerm(((StrTerm)yyVals[-2+yyTop]));
+                    yyVal = new NotNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
+    return yyVal;
+  }
+};
+states[93] = new ParserState() {
+  public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
+                    if (support.isInDef() || support.isInSingle()) {
+                        support.yyerror("dynamic constant assignment");
+                    }
 
-                   yyVal = support.newEvStrNode(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
+                    ISourcePosition position = support.getPosition(((Node)yyVals[-2+yyTop]));
+
+                    yyVal = new ConstDeclNode(position, null, support.new_colon2(position, ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[436] = new ParserState() {
+states[462] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = new GlobalVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    yyVal = new Token("__LINE__", Tokens.k__LINE__, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[437] = new ParserState() {
+states[395] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = new InstVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    support.pushBlockScope();
     return yyVal;
   }
 };
-states[438] = new ParserState() {
+states[194] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = new ClassVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
+                    support.checkExpression(((Node)yyVals[0+yyTop]));
+
+                    ISourcePosition pos = ((AssignableNode)yyVals[-2+yyTop]).getPosition();
+                    String asgnOp = (String) ((Token)yyVals[-1+yyTop]).getValue();
+                    if (asgnOp.equals("||")) {
+                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
+                        yyVal = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
+                    } else if (asgnOp.equals("&&")) {
+                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
+                        yyVal = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
+                    } else {
+                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
+                        ((AssignableNode)yyVals[-2+yyTop]).setPosition(pos);
+                        yyVal = ((AssignableNode)yyVals[-2+yyTop]);
+                    }
     return yyVal;
   }
 };
-states[440] = new ParserState() {
+states[60] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     lexer.setState(LexState.EXPR_END);
-                     yyVal = ((Token)yyVals[0+yyTop]);
-                     ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-1+yyTop]).getPosition());
+                    yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[445] = new ParserState() {
+states[261] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     lexer.setState(LexState.EXPR_END);
+                    Node node = null;
 
-                     /* DStrNode: :"some text #{some expression}"*/
-                     /* StrNode: :"some text"*/
-                     /* EvStrNode :"#{some expression}"*/
-                     /* Ruby 1.9 allows empty strings as symbols*/
-                     if (((Node)yyVals[-1+yyTop]) == null) {
-                         yyVal = new SymbolNode(((Token)yyVals[-2+yyTop]).getPosition(), "");
-                     } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
-                         yyVal = new DSymbolNode(((Token)yyVals[-2+yyTop]).getPosition(), ((DStrNode)yyVals[-1+yyTop]));
-                     } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
-                         yyVal = new SymbolNode(((Token)yyVals[-2+yyTop]).getPosition(), ((StrNode)yyVals[-1+yyTop]).getValue().toString().intern());
-                     } else {
-                         yyVal = new DSymbolNode(((Token)yyVals[-2+yyTop]).getPosition());
-                         ((DSymbolNode)yyVal).add(((Node)yyVals[-1+yyTop]));
-                     }
+                    /* FIXME: lose syntactical elements here (and others like this)*/
+                    if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
+                        (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
+                        yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
+                    } else {
+                        yyVal = support.arg_concat(support.getPosition(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
+                    }
     return yyVal;
   }
 };
-states[446] = new ParserState() {
+states[496] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = ((Node)yyVals[-1+yyTop]);
+                    /*		    {
+			ID tid = internal_id();
+			arg_var(tid);
+			if (dyna_in_block()) {
+			    $2->nd_value = NEW_DVAR(tid);
+			}
+			else {
+			    $2->nd_value = NEW_LVAR(tid);
+			}
+			$$ = NEW_ARGS_AUX(tid, 1);
+			$$->nd_next = $2;*/
     return yyVal;
   }
 };
-states[447] = new ParserState() {
+states[429] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = ((FloatNode)yyVals[0+yyTop]);
+                    ByteList aChar = ByteList.create("");
+                    aChar.setEncoding(lexer.getEncoding());
+                    yyVal = lexer.createStrNode(((Token)yyVals[0+yyTop]).getPosition(), aChar, 0);
     return yyVal;
   }
 };
-states[448] = new ParserState() {
+states[362] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = support.negateInteger(((Node)yyVals[0+yyTop]));
+    /* was $$ = null;*/
+                   yyVal = support.new_args(lexer.getPosition(), null, null, null, null, null);
     return yyVal;
   }
 };
-states[449] = new ParserState() {
+states[295] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                     yyVal = support.negateFloat(((FloatNode)yyVals[0+yyTop]));
+                    lexer.getConditionState().begin();
     return yyVal;
   }
 };
-states[455] = new ParserState() {
+states[27] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new Token("nil", Tokens.kNIL, ((Token)yyVals[0+yyTop]).getPosition());
+                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
+                    yyVal = new RescueNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), new RescueBodyNode(support.getPosition(((Node)yyVals[-2+yyTop])), null, body, null), null);
     return yyVal;
   }
 };
-states[456] = new ParserState() {
+states[228] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new Token("self", Tokens.kSELF, ((Token)yyVals[0+yyTop]).getPosition());
+                    yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
     return yyVal;
   }
 };
-states[457] = new ParserState() {
+states[94] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new Token("true", Tokens.kTRUE, ((Token)yyVals[0+yyTop]).getPosition());
+                    if (support.isInDef() || support.isInSingle()) {
+                        support.yyerror("dynamic constant assignment");
+                    }
+
+                    ISourcePosition position = ((Token)yyVals[-1+yyTop]).getPosition();
+
+                    yyVal = new ConstDeclNode(position, null, support.new_colon3(position, (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[458] = new ParserState() {
+states[463] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new Token("false", Tokens.kFALSE, ((Token)yyVals[0+yyTop]).getPosition());
+                    yyVal = new Token("__ENCODING__", Tokens.k__ENCODING__, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
-states[459] = new ParserState() {
+states[396] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new Token("__FILE__", Tokens.k__FILE__, ((Token)yyVals[0+yyTop]).getPosition());
+                    yyVal = new IterNode(((Token)yyVals[-4+yyTop]).getPosition(), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
+                    /* FIXME: What the hell is this?*/
+                    ((ISourcePositionHolder)yyVals[-5+yyTop]).setPosition(support.getPosition(((ISourcePositionHolder)yyVals[-5+yyTop])));
+                    support.popCurrentScope();
     return yyVal;
   }
 };
-states[460] = new ParserState() {
+states[329] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new Token("__LINE__", Tokens.k__LINE__, ((Token)yyVals[0+yyTop]).getPosition());
+                    yyVal = new IfNode(((Token)yyVals[-4+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[461] = new ParserState() {
+states[195] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new Token("__ENCODING__", Tokens.k__ENCODING__, ((Token)yyVals[0+yyTop]).getPosition());
+                    support.checkExpression(((Node)yyVals[-2+yyTop]));
+                    ISourcePosition pos = ((Token)yyVals[-1+yyTop]).getPosition();
+                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
+                    Node rest;
+
+                    pos = ((AssignableNode)yyVals[-4+yyTop]).getPosition();
+                    String asgnOp = (String) ((Token)yyVals[-3+yyTop]).getValue();
+                    if (asgnOp.equals("||")) {
+                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(((Node)yyVals[-2+yyTop]));
+                        rest = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
+                    } else if (asgnOp.equals("&&")) {
+                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(((Node)yyVals[-2+yyTop]));
+                        rest = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
+                    } else {
+                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-4+yyTop])), asgnOp, ((Node)yyVals[-2+yyTop])));
+                        ((AssignableNode)yyVals[-4+yyTop]).setPosition(pos);
+                        rest = ((AssignableNode)yyVals[-4+yyTop]);
+                    }
+
+                    yyVal = new RescueNode(((Token)yyVals[-1+yyTop]).getPosition(), rest, new RescueBodyNode(((Token)yyVals[-1+yyTop]).getPosition(), null, body, null), null);
     return yyVal;
   }
 };
-states[462] = new ParserState() {
+states[61] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.gettable(((Token)yyVals[0+yyTop]));
+                    yyVal = support.new_fcall(((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[463] = new ParserState() {
+states[262] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
+                    Node node = support.splat_array(((Node)yyVals[-2+yyTop]));
+
+                    if (node != null) {
+                        yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
+                    } else {
+                        yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                    }
     return yyVal;
   }
 };
-states[464] = new ParserState() {
+states[497] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = new ArrayNode(lexer.getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[465] = new ParserState() {
+states[430] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[466] = new ParserState() {
+states[363] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = null;
+                    lexer.commandStart = true;
+                    yyVal = ((ArgsNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[467] = new ParserState() {
+states[28] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                   lexer.setState(LexState.EXPR_BEG);
+                    if (support.isInDef() || support.isInSingle()) {
+                        support.warn(ID.END_IN_METHOD, ((Token)yyVals[-3+yyTop]).getPosition(), "END in method; use at_exit");
+                    }
+                    yyVal = new PostExeNode(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[468] = new ParserState() {
+states[229] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[-1+yyTop]);
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "~");
     return yyVal;
   }
 };
-states[469] = new ParserState() {
+states[95] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                   yyVal = null;
+                    support.backrefAssignError(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[470] = new ParserState() {
+states[296] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
-                    ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
-                    lexer.setState(LexState.EXPR_BEG);
+                    lexer.getConditionState().end();
+    return yyVal;
+  }
+};
+states[464] = new ParserState() {
+  public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
+                    yyVal = support.gettable(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[471] = new ParserState() {
+states[397] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
+                    yyVal = support.newWhenNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[472] = new ParserState() {
+states[196] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
+  /* FIXME: arg_concat missing for opt_call_args*/
+                    yyVal = support.new_opElementAsgnNode(support.getPosition(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-5+yyTop]), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[473] = new ParserState() {
+states[62] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[474] = new ParserState() {
+states[263] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
+                    Node node = null;
+
+                    if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
+                        (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
+                        yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
+                    } else {
+                        yyVal = support.arg_concat(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
+                    }
     return yyVal;
   }
 };
-states[475] = new ParserState() {
+states[498] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
+                    yyVal = ((ListNode)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
-states[476] = new ParserState() {
+states[431] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = null;
     return yyVal;
   }
 };
-states[477] = new ParserState() {
+states[364] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.new_args(((Token)yyVals[-2+yyTop]).getPosition(), null, null, null, null, null);
     return yyVal;
   }
 };
-states[478] = new ParserState() {
+states[230] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[479] = new ParserState() {
+states[96] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
+                      /* if (!($$ = assignable($1, 0))) $$ = NEW_BEGIN(0);*/
+                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[480] = new ParserState() {
+states[297] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
+                    yyVal = new WhileNode(((Token)yyVals[-6+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
     return yyVal;
   }
 };
-states[481] = new ParserState() {
+states[465] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[482] = new ParserState() {
+states[331] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[483] = new ParserState() {
+states[197] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
     return yyVal;
   }
 };
-states[484] = new ParserState() {
+states[63] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
+                    yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])); 
     return yyVal;
   }
 };
-states[485] = new ParserState() {
+states[264] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
+                     yyVal = support.newSplatNode(support.getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[0+yyTop]));  
     return yyVal;
   }
 };
-states[486] = new ParserState() {
+states[499] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.new_args(lexer.getPosition(), null, null, null, null, null);
+                    support.arg_var(support.formal_argument(((Token)yyVals[-2+yyTop])));
+                    yyVal = new OptArgNode(((Token)yyVals[-2+yyTop]).getPosition(), support.assignable(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
-states[487] = new ParserState() {
+states[432] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.yyerror("formal argument cannot be a constant");
+                    yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[488] = new ParserState() {
+states[365] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.yyerror("formal argument cannot be an instance variable");
+                    yyVal = support.new_args(((Token)yyVals[0+yyTop]).getPosition(), null, null, null, null, null);
     return yyVal;
   }
 };
-states[489] = new ParserState() {
+states[30] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.yyerror("formal argument cannot be a global variable");
+                    support.checkExpression(((Node)yyVals[0+yyTop]));
+                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
+                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
-states[490] = new ParserState() {
+states[231] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.yyerror("formal argument cannot be a class variable");
+                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
-states[492] = new ParserState() {
+states[97] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.formal_argument(((Token)yyVals[0+yyTop]));
+                    yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
-states[493] = new ParserState() {
+states[298] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.arg_var(((Token)yyVals[0+yyTop]));
-  /*
-                    $$ = new ArgAuxiliaryNode($1.getPosition(), (String) $1.getValue(), 1);
-  */
+                  lexer.getConditionState().begin();
     return yyVal;
   }
 };
-states[494] = new ParserState() {
+states[466] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Node)yyVals[-1+yyTop]);
-                    /*		    {
-			ID tid = internal_id();
-			arg_var(tid);
-			if (dyna_in_block()) {
-			    $2->nd_value = NEW_DVAR(tid);
-			}
-			else {
-			    $2->nd_value = NEW_LVAR(tid);
-			}
-			$$ = NEW_ARGS_AUX(tid, 1);
-			$$->nd_next = $2;*/
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[495] = new ParserState() {
+states[198] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new ArrayNode(lexer.getPosition(), ((Node)yyVals[0+yyTop]));
+                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
     return yyVal;
   }
 };
-states[496] = new ParserState() {
+states[64] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
-                    yyVal = ((ListNode)yyVals[-2+yyTop]);
+                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
-states[497] = new ParserState() {
+states[500] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.arg_var(support.formal_argument(((Token)yyVals[-2+yyTop])));
                     yyVal = new OptArgNode(((Token)yyVals[-2+yyTop]).getPosition(), support.assignable(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
-states[498] = new ParserState() {
+states[433] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    support.arg_var(support.formal_argument(((Token)yyVals[-2+yyTop])));
-                    yyVal = new OptArgNode(((Token)yyVals[-2+yyTop]).getPosition(), support.assignable(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[499] = new ParserState() {
+states[366] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
+                    yyVal = ((ArgsNode)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
-states[500] = new ParserState() {
+states[31] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                    support.checkExpression(((Node)yyVals[0+yyTop]));
+
+                    ISourcePosition pos = ((AssignableNode)yyVals[-2+yyTop]).getPosition();
+                    String asgnOp = (String) ((Token)yyVals[-1+yyTop]).getValue();
+                    if (asgnOp.equals("||")) {
+                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
+                        yyVal = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
+                    } else if (asgnOp.equals("&&")) {
+                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
+                        yyVal = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
+                    } else {
+                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
+                        ((AssignableNode)yyVals[-2+yyTop]).setPosition(pos);
+                        yyVal = ((AssignableNode)yyVals[-2+yyTop]);
+                    }
     return yyVal;
   }
 };
-states[501] = new ParserState() {
+states[232] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
+                    yyVal = support.newAndNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[502] = new ParserState() {
+states[98] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
+                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[505] = new ParserState() {
+states[299] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (!support.is_local_id(((Token)yyVals[0+yyTop]))) {
-                        support.yyerror("rest argument must be local variable");
-                    }
-                    
-                    yyVal = new RestArgNode(support.arg_var(support.shadowing_lvar(((Token)yyVals[0+yyTop]))));
+                  lexer.getConditionState().end();
     return yyVal;
   }
 };
-states[506] = new ParserState() {
+states[467] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new UnnamedRestArgNode(((Token)yyVals[0+yyTop]).getPosition(), "", support.getCurrentScope().addVariable("*"));
+                    yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[509] = new ParserState() {
+states[400] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (!support.is_local_id(((Token)yyVals[0+yyTop]))) {
-                        support.yyerror("block argument must be local variable");
+                    Node node;
+                    if (((Node)yyVals[-3+yyTop]) != null) {
+                        node = support.appendToBlock(support.node_assign(((Node)yyVals[-3+yyTop]), new GlobalVarNode(((Token)yyVals[-5+yyTop]).getPosition(), "$!")), ((Node)yyVals[-1+yyTop]));
+                        if (((Node)yyVals[-1+yyTop]) != null) {
+                            node.setPosition(support.unwrapNewlineNode(((Node)yyVals[-1+yyTop])).getPosition());
+                        }
+                    } else {
+                        node = ((Node)yyVals[-1+yyTop]);
                     }
-                    
-                    yyVal = new BlockArgNode(support.arg_var(support.shadowing_lvar(((Token)yyVals[0+yyTop]))));
+                    Node body = node == null ? NilImplicitNode.NIL : node;
+                    yyVal = new RescueBodyNode(((Token)yyVals[-5+yyTop]).getPosition(), ((Node)yyVals[-4+yyTop]), body, ((RescueBodyNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[510] = new ParserState() {
+states[333] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((BlockArgNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
-states[511] = new ParserState() {
+states[199] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = null;
+                    yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
     return yyVal;
   }
 };
-states[512] = new ParserState() {
+states[65] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (!(((Node)yyVals[0+yyTop]) instanceof SelfNode)) {
-                        support.checkExpression(((Node)yyVals[0+yyTop]));
-                    }
-                    yyVal = ((Node)yyVals[0+yyTop]);
+                    yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[513] = new ParserState() {
+states[501] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    lexer.setState(LexState.EXPR_BEG);
+                    yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[514] = new ParserState() {
+states[434] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    if (((Node)yyVals[-1+yyTop]) == null) {
-                        support.yyerror("can't define single method for ().");
-                    } else if (((Node)yyVals[-1+yyTop]) instanceof ILiteralNode) {
-                        support.yyerror("can't define single method for literals.");
-                    }
-                    support.checkExpression(((Node)yyVals[-1+yyTop]));
-                    yyVal = ((Node)yyVals[-1+yyTop]);
+                    yyVal = lexer.getStrTerm();
+                    lexer.setStrTerm(null);
+                    lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
-states[515] = new ParserState() {
+states[32] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new ArrayNode(lexer.getPosition());
+  /* FIXME: arg_concat logic missing for opt_call_args*/
+                    yyVal = support.new_opElementAsgnNode(support.getPosition(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-5+yyTop]), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[516] = new ParserState() {
+states[233] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ListNode)yyVals[-1+yyTop]);
+                    yyVal = support.newOrNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
-states[518] = new ParserState() {
+states[99] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((ListNode)yyVals[-2+yyTop]).addAll(((ListNode)yyVals[0+yyTop]));
+                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
-states[519] = new ParserState() {
+states[300] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ISourcePosition pos;
-                    if (((Node)yyVals[-2+yyTop]) == null && ((Node)yyVals[0+yyTop]) == null) {
-                        pos = ((Token)yyVals[-1+yyTop]).getPosition();
-                    } else {
-                        pos = ((Node)yyVals[-2+yyTop]).getPosition();
-                    }
-
-                    yyVal = support.newArrayNode(pos, ((Node)yyVals[-2+yyTop])).add(((Node)yyVals[0+yyTop]));
+                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
+                    yyVal = new UntilNode(((Token)yyVals[-6+yyTop]).getPosition(), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
     return yyVal;
   }
 };
-states[520] = new ParserState() {
+states[468] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    ISourcePosition pos = ((Token)yyVals[-1+yyTop]).getPosition();
-                    yyVal = support.newArrayNode(pos, new SymbolNode(pos, (String) ((Token)yyVals[-1+yyTop]).getValue())).add(((Node)yyVals[0+yyTop]));
+                    yyVal = null;
     return yyVal;
   }
 };
-states[537] = new ParserState() {
+states[401] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Token)yyVals[0+yyTop]);
+                    yyVal = null; 
     return yyVal;
   }
 };
-states[538] = new ParserState() {
+states[334] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = ((Token)yyVals[0+yyTop]);
+                     yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
-states[546] = new ParserState() {
+states[200] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                      yyVal = null;
+                    support.yyerror("constant re-assignment");
     return yyVal;
   }
 };
-states[547] = new ParserState() {
+states[66] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                  yyVal = null;
+                    yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop])); /* .setPosFrom($2);*/
     return yyVal;
   }
 };
 }
-					// line 2022 "Ruby19Parser.y"
+					// line 2028 "Ruby19Parser.y"
 
     /** The parse method use an lexer stream and parse it to an AST node 
      * structure
      */
     public RubyParserResult parse(ParserConfiguration configuration, LexerSource source) throws IOException {
         support.reset();
         support.setConfiguration(configuration);
         support.setResult(new RubyParserResult());
         
         lexer.reset();
         lexer.setSource(source);
         lexer.setEncoding(configuration.getDefaultEncoding());
 
         Object debugger = null;
         if (configuration.isDebug()) {
             try {
                 Class yyDebugAdapterClass = Class.forName("jay.yydebug.yyDebugAdapter");
                 debugger = yyDebugAdapterClass.newInstance();
             } catch (IllegalAccessException iae) {
                 // ignore, no debugger present
             } catch (InstantiationException ie) {
                 // ignore, no debugger present
             } catch (ClassNotFoundException cnfe) {
                 // ignore, no debugger present
             }
         }
         //yyparse(lexer, new jay.yydebug.yyAnim("JRuby", 9));
         yyparse(lexer, debugger);
         
         return support.getResult();
     }
 }
-					// line 8132 "-"
+					// line 8109 "-"
diff --git a/src/org/jruby/parser/Ruby19Parser.y b/src/org/jruby/parser/Ruby19Parser.y
index 7781d8bbbb..2df35e6a38 100644
--- a/src/org/jruby/parser/Ruby19Parser.y
+++ b/src/org/jruby/parser/Ruby19Parser.y
@@ -1,1453 +1,1459 @@
 %{
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2008-2009 Thomas E Enebo <enebo@acm.org>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.parser;
 
 import java.io.IOException;
 
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgumentNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AssignableNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BlockAcceptingNode;
 import org.jruby.ast.BlockArgNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.DStrNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
 import org.jruby.ast.DefsNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNoArgBlockNode;
 import org.jruby.ast.FCallNoArgNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.GlobalVarNode;
 import org.jruby.ast.Hash19Node;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.InstVarNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.LambdaNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.LiteralNode;
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgn19Node;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.NilImplicitNode;
 import org.jruby.ast.NilNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.OpAsgnAndNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OptArgNode;
 import org.jruby.ast.PostExeNode;
 import org.jruby.ast.PreExe19Node;
 import org.jruby.ast.RedoNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.RestArgNode;
 import org.jruby.ast.RetryNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.SelfNode;
 import org.jruby.ast.StarNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SymbolNode;
 import org.jruby.ast.UnnamedRestArgNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZArrayNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.ast.ZYieldNode;
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.common.IRubyWarnings;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.ISourcePositionHolder;
 import org.jruby.lexer.yacc.LexerSource;
 import org.jruby.lexer.yacc.RubyYaccLexer;
 import org.jruby.lexer.yacc.RubyYaccLexer.LexState;
 import org.jruby.lexer.yacc.StrTerm;
 import org.jruby.lexer.yacc.SyntaxException;
 import org.jruby.lexer.yacc.SyntaxException.PID;
 import org.jruby.lexer.yacc.Token;
 import org.jruby.util.ByteList;
 
 public class Ruby19Parser implements RubyParser {
     protected ParserSupport19 support;
     protected RubyYaccLexer lexer;
 
     public Ruby19Parser() {
         this(new ParserSupport19());
     }
 
     public Ruby19Parser(ParserSupport19 support) {
         this.support = support;
         lexer = new RubyYaccLexer(false);
         lexer.setParserSupport(support);
         support.setLexer(lexer);
     }
 
     public void setWarnings(IRubyWarnings warnings) {
         support.setWarnings(warnings);
         lexer.setWarnings(warnings);
     }
 %}
 
 // We need to make sure we have same tokens in the same order and up
 // front so 1.8 and 1.9 parser can use the same Tokens.java file.
 %token <Token> kCLASS kMODULE kDEF kUNDEF kBEGIN kRESCUE kENSURE kEND kIF
   kUNLESS kTHEN kELSIF kELSE kCASE kWHEN kWHILE kUNTIL kFOR kBREAK kNEXT
   kREDO kRETRY kIN kDO kDO_COND kDO_BLOCK kRETURN kYIELD kSUPER kSELF kNIL
   kTRUE kFALSE kAND kOR kNOT kIF_MOD kUNLESS_MOD kWHILE_MOD kUNTIL_MOD
   kRESCUE_MOD kALIAS kDEFINED klBEGIN klEND k__LINE__ k__FILE__
   k__ENCODING__ kDO_LAMBDA 
 
 %token <Token> tIDENTIFIER tFID tGVAR tIVAR tCONSTANT tCVAR tLABEL tCHAR
 %type <Token> variable
 %type <Token> sym symbol operation operation2 operation3 cname fname op 
 %type <Token> f_norm_arg dot_or_colon restarg_mark blkarg_mark
 %token <Token> tUPLUS         /* unary+ */
 %token <Token> tUMINUS        /* unary- */
 %token <Token> tUMINUS_NUM    /* unary- */
 %token <Token> tPOW           /* ** */
 %token <Token> tCMP           /* <=> */
 %token <Token> tEQ            /* == */
 %token <Token> tEQQ           /* === */
 %token <Token> tNEQ           /* != */
 %token <Token> tGEQ           /* >= */
 %token <Token> tLEQ           /* <= */
 %token <Token> tANDOP tOROP   /* && and || */
 %token <Token> tMATCH tNMATCH /* =~ and !~ */
 %token <Token>  tDOT           /* Is just '.' in ruby and not a token */
 %token <Token> tDOT2 tDOT3    /* .. and ... */
 %token <Token> tAREF tASET    /* [] and []= */
 %token <Token> tLSHFT tRSHFT  /* << and >> */
 %token <Token> tCOLON2        /* :: */
 %token <Token> tCOLON3        /* :: at EXPR_BEG */
 %token <Token> tOP_ASGN       /* +=, -=  etc. */
 %token <Token> tASSOC         /* => */
 %token <Token> tLPAREN        /* ( */
 %token <Token> tLPAREN2        /* ( Is just '(' in ruby and not a token */
 %token <Token> tRPAREN        /* ) */
 %token <Token> tLPAREN_ARG    /* ( */
 %token <Token> tLBRACK        /* [ */
 %token <Token> tRBRACK        /* ] */
 %token <Token> tLBRACE        /* { */
 %token <Token> tLBRACE_ARG    /* { */
 %token <Token> tSTAR          /* * */
 %token <Token> tSTAR2         /* *  Is just '*' in ruby and not a token */
 %token <Token> tAMPER         /* & */
 %token <Token> tAMPER2        /* &  Is just '&' in ruby and not a token */
 %token <Token> tTILDE         /* ` is just '`' in ruby and not a token */
 %token <Token> tPERCENT       /* % is just '%' in ruby and not a token */
 %token <Token> tDIVIDE        /* / is just '/' in ruby and not a token */
 %token <Token> tPLUS          /* + is just '+' in ruby and not a token */
 %token <Token> tMINUS         /* - is just '-' in ruby and not a token */
 %token <Token> tLT            /* < is just '<' in ruby and not a token */
 %token <Token> tGT            /* > is just '>' in ruby and not a token */
 %token <Token> tPIPE          /* | is just '|' in ruby and not a token */
 %token <Token> tBANG          /* ! is just '!' in ruby and not a token */
 %token <Token> tCARET         /* ^ is just '^' in ruby and not a token */
 %token <Token> tLCURLY        /* { is just '{' in ruby and not a token */
 %token <Token> tRCURLY        /* } is just '}' in ruby and not a token */
 %token <Token> tBACK_REF2     /* { is just '`' in ruby and not a token */
 %token <Token> tSYMBEG tSTRING_BEG tXSTRING_BEG tREGEXP_BEG tWORDS_BEG tQWORDS_BEG
 %token <Token> tSTRING_DBEG tSTRING_DVAR tSTRING_END
 %token <Token> tLAMBDA tLAMBEG
 %token <Node> tNTH_REF tBACK_REF tSTRING_CONTENT tINTEGER
 %token <FloatNode> tFLOAT  
 %token <RegexpNode>  tREGEXP_END
 %type <RestArgNode> f_rest_arg 
 %type <Node> singleton strings string string1 xstring regexp
 %type <Node> string_contents xstring_contents string_content method_call
-%type <Node> words qwords word literal numeric dsym cpath command_call
+%type <Node> words qwords word literal numeric dsym cpath command_asgn command_call
 %type <Node> compstmt bodystmt stmts stmt expr arg primary command 
 %type <Node> expr_value primary_value opt_else cases if_tail exc_var
    // ENEBO: missing call_args2, open_args
 %type <Node> call_args opt_ensure paren_args superclass
 %type <Node> command_args var_ref opt_paren_args block_call block_command
 %type <Node> f_opt undef_list string_dvar backref
 %type <ArgsNode> f_args f_arglist f_larglist block_param block_param_def opt_block_param 
 %type <Node> mrhs mlhs_item mlhs_node arg_value case_body exc_list aref_args
    // ENEBO: missing block_var == for_var, opt_block_var
 %type <Node> lhs none args
 %type <ListNode> qword_list word_list f_arg f_optarg f_marg_list
    // ENEBO: missing when_args
 %type <ListNode> mlhs_head assocs assoc assoc_list mlhs_post f_block_optarg
 %type <BlockPassNode> opt_block_arg block_arg none_block_pass
 %type <BlockArgNode> opt_f_block_arg f_block_arg
 %type <IterNode> brace_block do_block cmd_brace_block
    // ENEBO: missing mhls_entry
 %type <MultipleAsgn19Node> mlhs mlhs_basic 
 %type <RescueBodyNode> opt_rescue
 %type <AssignableNode> var_lhs
 %type <LiteralNode> fsym
 %type <Node> fitem
    // ENEBO: begin all new types
 %type <Node> f_arg_item
 %type <Node> bv_decls opt_bv_decl lambda_body 
 %type <LambdaNode> lambda
 %type <Node> mlhs_inner f_block_opt for_var
 %type <Node> opt_call_args f_marg f_margs
 %type <Token> bvar
    // ENEBO: end all new types
 
 %type <Token> rparen rbracket reswords f_bad_arg
 %type <Node> top_compstmt top_stmts top_stmt
 
 /*
  *    precedence table
  */
 
 %nonassoc tLOWEST
 %nonassoc tLBRACE_ARG
 
 %nonassoc  kIF_MOD kUNLESS_MOD kWHILE_MOD kUNTIL_MOD
 %left  kOR kAND
 %right kNOT
 %nonassoc kDEFINED
 %right '=' tOP_ASGN
 %left kRESCUE_MOD
 %right '?' ':'
 %nonassoc tDOT2 tDOT3
 %left  tOROP
 %left  tANDOP
 %nonassoc  tCMP tEQ tEQQ tNEQ tMATCH tNMATCH
 %left  tGT tGEQ tLT tLEQ
 %left  tPIPE tCARET
 %left  tAMPER2
 %left  tLSHFT tRSHFT
 %left  tPLUS tMINUS
 %left  tSTAR2 tDIVIDE tPERCENT
 %right tUMINUS_NUM tUMINUS
 %right tPOW
 %right tBANG tTILDE tUPLUS
 
    //%token <Integer> tLAST_TOKEN
 
 %%
 program       : {
                   lexer.setState(LexState.EXPR_BEG);
                   support.initTopLocalVariables();
               } top_compstmt {
   // ENEBO: Removed !compile_for_eval which probably is to reduce warnings
                   if ($2 != null) {
                       /* last expression should not be void */
                       if ($2 instanceof BlockNode) {
                           support.checkUselessStatement($<BlockNode>2.getLast());
                       } else {
                           support.checkUselessStatement($2);
                       }
                   }
                   support.getResult().setAST(support.addRootNode($2, support.getPosition($2)));
               }
 
 top_compstmt  : top_stmts opt_terms {
                   if ($1 instanceof BlockNode) {
                       support.checkUselessStatements($<BlockNode>1);
                   }
                   $$ = $1;
               }
 
 top_stmts     : none
               | top_stmt {
                     $$ = support.newline_node($1, support.getPosition($1));
               }
               | top_stmts terms top_stmt {
                     $$ = support.appendToBlock($1, support.newline_node($3, support.getPosition($3)));
               }
               | error top_stmt {
                     $$ = $2;
               }
 
 top_stmt      : stmt
               | klBEGIN {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("BEGIN in method");
                     }
               } tLCURLY top_compstmt tRCURLY {
                     support.getResult().addBeginNode(new PreExe19Node($1.getPosition(), support.getCurrentScope(), $4));
                     $$ = null;
               }
 
 bodystmt      : compstmt opt_rescue opt_else opt_ensure {
                   Node node = $1;
 
                   if ($2 != null) {
                       node = new RescueNode(support.getPosition($1), $1, $2, $3);
                   } else if ($3 != null) {
                       support.warn(ID.ELSE_WITHOUT_RESCUE, support.getPosition($1), "else without rescue is useless");
                       node = support.appendToBlock($1, $3);
                   }
                   if ($4 != null) {
                       if (node == null) node = NilImplicitNode.NIL;
                       node = new EnsureNode(support.getPosition($1), node, $4);
                   }
 
                   $$ = node;
                 }
 
 compstmt        : stmts opt_terms {
                     if ($1 instanceof BlockNode) {
                         support.checkUselessStatements($<BlockNode>1);
                     }
                     $$ = $1;
                 }
 
 stmts           : none
                 | stmt {
                     $$ = support.newline_node($1, support.getPosition($1));
                 }
                 | stmts terms stmt {
                     $$ = support.appendToBlock($1, support.newline_node($3, support.getPosition($3)));
                 }
                 | error stmt {
                     $$ = $2;
                 }
 
 stmt            : kALIAS fitem {
                     lexer.setState(LexState.EXPR_FNAME);
                 } fitem {
                     $$ = support.newAlias($1.getPosition(), $2, $4);
                 }
                 | kALIAS tGVAR tGVAR {
                     $$ = new VAliasNode($1.getPosition(), (String) $2.getValue(), (String) $3.getValue());
                 }
                 | kALIAS tGVAR tBACK_REF {
                     $$ = new VAliasNode($1.getPosition(), (String) $2.getValue(), "$" + $<BackRefNode>3.getType());
                 }
                 | kALIAS tGVAR tNTH_REF {
                     support.yyerror("can't make alias for the number variables");
                 }
                 | kUNDEF undef_list {
                     $$ = $2;
                 }
                 | stmt kIF_MOD expr_value {
                     $$ = new IfNode(support.getPosition($1), support.getConditionNode($3), $1, null);
                 }
                 | stmt kUNLESS_MOD expr_value {
                     $$ = new IfNode(support.getPosition($1), support.getConditionNode($3), null, $1);
                 }
                 | stmt kWHILE_MOD expr_value {
                     if ($1 != null && $1 instanceof BeginNode) {
                         $$ = new WhileNode(support.getPosition($1), support.getConditionNode($3), $<BeginNode>1.getBodyNode(), false);
                     } else {
                         $$ = new WhileNode(support.getPosition($1), support.getConditionNode($3), $1, true);
                     }
                 }
                 | stmt kUNTIL_MOD expr_value {
                     if ($1 != null && $1 instanceof BeginNode) {
                         $$ = new UntilNode(support.getPosition($1), support.getConditionNode($3), $<BeginNode>1.getBodyNode(), false);
                     } else {
                         $$ = new UntilNode(support.getPosition($1), support.getConditionNode($3), $1, true);
                     }
                 }
                 | stmt kRESCUE_MOD stmt {
                     Node body = $3 == null ? NilImplicitNode.NIL : $3;
                     $$ = new RescueNode(support.getPosition($1), $1, new RescueBodyNode(support.getPosition($1), null, body, null), null);
                 }
                 | klEND tLCURLY compstmt tRCURLY {
                     if (support.isInDef() || support.isInSingle()) {
                         support.warn(ID.END_IN_METHOD, $1.getPosition(), "END in method; use at_exit");
                     }
                     $$ = new PostExeNode($1.getPosition(), $3);
                 }
-                | lhs '=' command_call {
-                    support.checkExpression($3);
-                    $$ = support.node_assign($1, $3);
-                }
+                | command_asgn
                 | mlhs '=' command_call {
                     support.checkExpression($3);
                     $1.setValueNode($3);
                     $$ = $1;
                 }
                 | var_lhs tOP_ASGN command_call {
                     support.checkExpression($3);
 
                     ISourcePosition pos = $1.getPosition();
                     String asgnOp = (String) $2.getValue();
                     if (asgnOp.equals("||")) {
                         $1.setValueNode($3);
                         $$ = new OpAsgnOrNode(pos, support.gettable2($1), $1);
                     } else if (asgnOp.equals("&&")) {
                         $1.setValueNode($3);
                         $$ = new OpAsgnAndNode(pos, support.gettable2($1), $1);
                     } else {
                         $1.setValueNode(support.getOperatorCallNode(support.gettable2($1), asgnOp, $3));
                         $1.setPosition(pos);
                         $$ = $1;
                     }
                 }
                 | primary_value '[' opt_call_args rbracket tOP_ASGN command_call {
   // FIXME: arg_concat logic missing for opt_call_args
                     $$ = support.new_opElementAsgnNode(support.getPosition($1), $1, (String) $5.getValue(), $3, $6);
                 }
                 | primary_value tDOT tIDENTIFIER tOP_ASGN command_call {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | primary_value tDOT tCONSTANT tOP_ASGN command_call {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | primary_value tCOLON2 tIDENTIFIER tOP_ASGN command_call {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | backref tOP_ASGN command_call {
                     support.backrefAssignError($1);
                 }
                 | lhs '=' mrhs {
                     $$ = support.node_assign($1, $3);
                 }
                 | mlhs '=' arg_value {
                     $1.setValueNode($3);
                     $$ = $1;
                 }
                 | mlhs '=' mrhs {
                     $<AssignableNode>1.setValueNode($3);
                     $$ = $1;
                     $1.setPosition(support.getPosition($1));
                 }
                 | expr
 
+command_asgn    : lhs '=' command_call {
+                    support.checkExpression($3);
+                    $$ = support.node_assign($1, $3);
+                }
+                | lhs '=' command_asgn {
+                    support.checkExpression($3);
+                    $$ = support.node_assign($1, $3);
+                }
+
 // Node:expr *CURRENT* all but arg so far
 expr            : command_call
                 | expr kAND expr {
                     $$ = support.newAndNode($2.getPosition(), $1, $3);
                 }
                 | expr kOR expr {
                     $$ = support.newOrNode($2.getPosition(), $1, $3);
                 }
                 | kNOT opt_nl expr {
                     $$ = support.getOperatorCallNode(support.getConditionNode($3), "!");
                 }
                 | tBANG command_call {
                     $$ = support.getOperatorCallNode(support.getConditionNode($2), "!");
                 }
                 | arg
 
 expr_value      : expr {
                     support.checkExpression($1);
                 }
 
 // Node:command - call with or with block on end [!null]
 command_call    : command
                 | block_command
                 | kRETURN call_args {
                     $$ = new ReturnNode($1.getPosition(), support.ret_args($2, $1.getPosition()));
                 }
                 | kBREAK call_args {
                     $$ = new BreakNode($1.getPosition(), support.ret_args($2, $1.getPosition()));
                 }
                 | kNEXT call_args {
                     $$ = new NextNode($1.getPosition(), support.ret_args($2, $1.getPosition()));
                 }
 
 // Node:block_command - A call with a block (foo.bar {...}, foo::bar {...}, bar {...}) [!null]
 block_command   : block_call
                 | block_call tDOT operation2 command_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | block_call tCOLON2 operation2 command_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
 
 // :brace_block - [!null]
 cmd_brace_block : tLBRACE_ARG {
                     support.pushBlockScope();
                 } opt_block_param compstmt tRCURLY {
                     $$ = new IterNode($1.getPosition(), $3, $4, support.getCurrentScope());
                     support.popCurrentScope();
                 }
 
 // Node:command - fcall/call/yield/super [!null]
 command        : operation command_args %prec tLOWEST {
                     $$ = support.new_fcall($1, $2, null);
                 }
                 | operation command_args cmd_brace_block {
                     $$ = support.new_fcall($1, $2, $3);
                 }
                 | primary_value tDOT operation2 command_args %prec tLOWEST {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | primary_value tDOT operation2 command_args cmd_brace_block {
                     $$ = support.new_call($1, $3, $4, $5); 
                 }
                 | primary_value tCOLON2 operation2 command_args %prec tLOWEST {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | primary_value tCOLON2 operation2 command_args cmd_brace_block {
                     $$ = support.new_call($1, $3, $4, $5);
                 }
                 | kSUPER command_args {
                     $$ = support.new_super($2, $1); // .setPosFrom($2);
                 }
                 | kYIELD command_args {
                     $$ = support.new_yield($1.getPosition(), $2);
                 }
 
 // MultipleAssig19Node:mlhs - [!null]
 mlhs            : mlhs_basic
                 | tLPAREN mlhs_inner rparen {
                     $$ = $2;
                 }
 
 // MultipleAssign19Node:mlhs_entry - mlhs w or w/o parens [!null]
 mlhs_inner      : mlhs_basic {
                     $$ = $1;
                 }
                 | tLPAREN mlhs_inner rparen {
                     $$ = new MultipleAsgn19Node($1.getPosition(), support.newArrayNode($1.getPosition(), $2), null, null);
                 }
 
 // MultipleAssign19Node:mlhs_basic - multiple left hand side (basic because used in multiple context) [!null]
 mlhs_basic      : mlhs_head {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, null, null);
                 }
                 | mlhs_head mlhs_item {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1.add($2), null, null);
                 }
                 | mlhs_head tSTAR mlhs_node {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, $3, (ListNode) null);
                 }
                 | mlhs_head tSTAR mlhs_node ',' mlhs_post {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, $3, $5);
                 }
                 | mlhs_head tSTAR {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, new StarNode(lexer.getPosition()), null);
                 }
                 | mlhs_head tSTAR ',' mlhs_post {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, new StarNode(lexer.getPosition()), $4);
                 }
                 | tSTAR mlhs_node {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, $2, null);
                 }
                 | tSTAR mlhs_node ',' mlhs_post {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, $2, $4);
                 }
                 | tSTAR {
                       $$ = new MultipleAsgn19Node($1.getPosition(), null, new StarNode(lexer.getPosition()), null);
                 }
                 | tSTAR ',' mlhs_post {
                       $$ = new MultipleAsgn19Node($1.getPosition(), null, new StarNode(lexer.getPosition()), $3);
                 }
 
 mlhs_item       : mlhs_node
                 | tLPAREN mlhs_inner rparen {
                     $$ = $2;
                 }
 
 // Set of mlhs terms at front of mlhs (a, *b, d, e = arr  # a is head)
 mlhs_head       : mlhs_item ',' {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
                 | mlhs_head mlhs_item ',' {
                     $$ = $1.add($2);
                 }
 
 // Set of mlhs terms at end of mlhs (a, *b, d, e = arr  # d,e is post)
 mlhs_post       : mlhs_item {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
                 | mlhs_post ',' mlhs_item {
                     $$ = $1.add($3);
                 }
 
 mlhs_node       : variable {
                     $$ = support.assignable($1, NilImplicitNode.NIL);
                 }
                 | primary_value '[' opt_call_args rbracket {
                     $$ = support.aryset($1, $3);
                 }
                 | primary_value tDOT tIDENTIFIER {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tCOLON2 tIDENTIFIER {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tDOT tCONSTANT {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tCOLON2 tCONSTANT {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("dynamic constant assignment");
                     }
 
                     ISourcePosition position = support.getPosition($1);
 
                     $$ = new ConstDeclNode(position, null, support.new_colon2(position, $1, (String) $3.getValue()), NilImplicitNode.NIL);
                 }
                 | tCOLON3 tCONSTANT {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("dynamic constant assignment");
                     }
 
                     ISourcePosition position = $1.getPosition();
 
                     $$ = new ConstDeclNode(position, null, support.new_colon3(position, (String) $2.getValue()), NilImplicitNode.NIL);
                 }
                 | backref {
                     support.backrefAssignError($1);
                 }
 
 lhs             : variable {
                       // if (!($$ = assignable($1, 0))) $$ = NEW_BEGIN(0);
                     $$ = support.assignable($1, NilImplicitNode.NIL);
                 }
                 | primary_value '[' opt_call_args rbracket {
                     $$ = support.aryset($1, $3);
                 }
                 | primary_value tDOT tIDENTIFIER {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tCOLON2 tIDENTIFIER {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tDOT tCONSTANT {
                     $$ = support.attrset($1, (String) $3.getValue());
                 }
                 | primary_value tCOLON2 tCONSTANT {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("dynamic constant assignment");
                     }
 
                     ISourcePosition position = support.getPosition($1);
 
                     $$ = new ConstDeclNode(position, null, support.new_colon2(position, $1, (String) $3.getValue()), NilImplicitNode.NIL);
                 }
                 | tCOLON3 tCONSTANT {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("dynamic constant assignment");
                     }
 
                     ISourcePosition position = $1.getPosition();
 
                     $$ = new ConstDeclNode(position, null, support.new_colon3(position, (String) $2.getValue()), NilImplicitNode.NIL);
                 }
                 | backref {
                     support.backrefAssignError($1);
                 }
 
 cname           : tIDENTIFIER {
                     support.yyerror("class/module name must be CONSTANT");
                 }
                 | tCONSTANT
 
 cpath           : tCOLON3 cname {
                     $$ = support.new_colon3($1.getPosition(), (String) $2.getValue());
                 }
                 | cname {
                     $$ = support.new_colon2($1.getPosition(), null, (String) $1.getValue());
                 }
                 | primary_value tCOLON2 cname {
                     $$ = support.new_colon2(support.getPosition($1), $1, (String) $3.getValue());
                 }
 
 // Token:fname - A function name [!null]
 fname          : tIDENTIFIER | tCONSTANT | tFID 
                | op {
                    lexer.setState(LexState.EXPR_ENDFN);
                    $$ = $1;
                }
                | reswords {
                    lexer.setState(LexState.EXPR_ENDFN);
                    $$ = $1;
                }
 
 // LiteralNode:fsym
 fsym           : fname {
                     $$ = new LiteralNode($1);
                 }
                 | symbol {
                     $$ = new LiteralNode($1);
                 }
 
 // Node:fitem
 fitem           : fsym {
                     $$ = $1;
                 }
                 | dsym {
                     $$ = $1;
                 }
 
 undef_list      : fitem {
                     $$ = support.newUndef($1.getPosition(), $1);
                 }
                 | undef_list ',' {
                     lexer.setState(LexState.EXPR_FNAME);
                 } fitem {
                     $$ = support.appendToBlock($1, support.newUndef($1.getPosition(), $4));
                 }
 
 // Token:op
 op              : tPIPE | tCARET | tAMPER2 | tCMP | tEQ | tEQQ | tMATCH
                 | tNMATCH | tGT | tGEQ | tLT | tLEQ | tNEQ | tLSHFT | tRSHFT
                 | tPLUS | tMINUS | tSTAR2 | tSTAR | tDIVIDE | tPERCENT | tPOW
                 | tBANG | tTILDE | tUPLUS | tUMINUS | tAREF | tASET | tBACK_REF2
 
 // Token:op
 reswords        : k__LINE__ | k__FILE__ | k__ENCODING__ | klBEGIN | klEND
                 | kALIAS | kAND | kBEGIN | kBREAK | kCASE | kCLASS | kDEF
                 | kDEFINED | kDO | kELSE | kELSIF | kEND | kENSURE | kFALSE
                 | kFOR | kIN | kMODULE | kNEXT | kNIL | kNOT
                 | kOR | kREDO | kRESCUE | kRETRY | kRETURN | kSELF | kSUPER
                 | kTHEN | kTRUE | kUNDEF | kWHEN | kYIELD
                 | kIF_MOD | kUNLESS_MOD | kWHILE_MOD | kUNTIL_MOD | kRESCUE_MOD
 
 arg             : lhs '=' arg {
                     $$ = support.node_assign($1, $3);
                     // FIXME: Consider fixing node_assign itself rather than single case
                     $<Node>$.setPosition(support.getPosition($1));
                 }
                 | lhs '=' arg kRESCUE_MOD arg {
                     ISourcePosition position = $4.getPosition();
                     Node body = $5 == null ? NilImplicitNode.NIL : $5;
                     $$ = support.node_assign($1, new RescueNode(position, $3, new RescueBodyNode(position, null, body, null), null));
                 }
                 | var_lhs tOP_ASGN arg {
                     support.checkExpression($3);
 
                     ISourcePosition pos = $1.getPosition();
                     String asgnOp = (String) $2.getValue();
                     if (asgnOp.equals("||")) {
                         $1.setValueNode($3);
                         $$ = new OpAsgnOrNode(pos, support.gettable2($1), $1);
                     } else if (asgnOp.equals("&&")) {
                         $1.setValueNode($3);
                         $$ = new OpAsgnAndNode(pos, support.gettable2($1), $1);
                     } else {
                         $1.setValueNode(support.getOperatorCallNode(support.gettable2($1), asgnOp, $3));
                         $1.setPosition(pos);
                         $$ = $1;
                     }
                 }
                 | var_lhs tOP_ASGN arg kRESCUE_MOD arg {
                     support.checkExpression($3);
                     ISourcePosition pos = $4.getPosition();
                     Node body = $5 == null ? NilImplicitNode.NIL : $5;
                     Node rest;
 
                     pos = $1.getPosition();
                     String asgnOp = (String) $2.getValue();
                     if (asgnOp.equals("||")) {
                         $1.setValueNode($3);
                         rest = new OpAsgnOrNode(pos, support.gettable2($1), $1);
                     } else if (asgnOp.equals("&&")) {
                         $1.setValueNode($3);
                         rest = new OpAsgnAndNode(pos, support.gettable2($1), $1);
                     } else {
                         $1.setValueNode(support.getOperatorCallNode(support.gettable2($1), asgnOp, $3));
                         $1.setPosition(pos);
                         rest = $1;
                     }
 
                     $$ = new RescueNode($4.getPosition(), rest, new RescueBodyNode($4.getPosition(), null, body, null), null);
                 }
                 | primary_value '[' opt_call_args rbracket tOP_ASGN arg {
   // FIXME: arg_concat missing for opt_call_args
                     $$ = support.new_opElementAsgnNode(support.getPosition($1), $1, (String) $5.getValue(), $3, $6);
                 }
                 | primary_value tDOT tIDENTIFIER tOP_ASGN arg {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | primary_value tDOT tCONSTANT tOP_ASGN arg {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | primary_value tCOLON2 tIDENTIFIER tOP_ASGN arg {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, (String) $3.getValue(), (String) $4.getValue());
                 }
                 | primary_value tCOLON2 tCONSTANT tOP_ASGN arg {
                     support.yyerror("constant re-assignment");
                 }
                 | tCOLON3 tCONSTANT tOP_ASGN arg {
                     support.yyerror("constant re-assignment");
                 }
                 | backref tOP_ASGN arg {
                     support.backrefAssignError($1);
                 }
                 | arg tDOT2 arg {
                     support.checkExpression($1);
                     support.checkExpression($3);
     
                     boolean isLiteral = $1 instanceof FixnumNode && $3 instanceof FixnumNode;
                     $$ = new DotNode(support.getPosition($1), $1, $3, false, isLiteral);
                 }
                 | arg tDOT3 arg {
                     support.checkExpression($1);
                     support.checkExpression($3);
 
                     boolean isLiteral = $1 instanceof FixnumNode && $3 instanceof FixnumNode;
                     $$ = new DotNode(support.getPosition($1), $1, $3, true, isLiteral);
                 }
                 | arg tPLUS arg {
                     $$ = support.getOperatorCallNode($1, "+", $3, lexer.getPosition());
                 }
                 | arg tMINUS arg {
                     $$ = support.getOperatorCallNode($1, "-", $3, lexer.getPosition());
                 }
                 | arg tSTAR2 arg {
                     $$ = support.getOperatorCallNode($1, "*", $3, lexer.getPosition());
                 }
                 | arg tDIVIDE arg {
                     $$ = support.getOperatorCallNode($1, "/", $3, lexer.getPosition());
                 }
                 | arg tPERCENT arg {
                     $$ = support.getOperatorCallNode($1, "%", $3, lexer.getPosition());
                 }
                 | arg tPOW arg {
                     $$ = support.getOperatorCallNode($1, "**", $3, lexer.getPosition());
                 }
                 | tUMINUS_NUM tINTEGER tPOW arg {
                     $$ = support.getOperatorCallNode(support.getOperatorCallNode($2, "**", $4, lexer.getPosition()), "-@");
                 }
                 | tUMINUS_NUM tFLOAT tPOW arg {
                     $$ = support.getOperatorCallNode(support.getOperatorCallNode($2, "**", $4, lexer.getPosition()), "-@");
                 }
                 | tUPLUS arg {
                     $$ = support.getOperatorCallNode($2, "+@");
                 }
                 | tUMINUS arg {
                     $$ = support.getOperatorCallNode($2, "-@");
                 }
                 | arg tPIPE arg {
                     $$ = support.getOperatorCallNode($1, "|", $3, lexer.getPosition());
                 }
                 | arg tCARET arg {
                     $$ = support.getOperatorCallNode($1, "^", $3, lexer.getPosition());
                 }
                 | arg tAMPER2 arg {
                     $$ = support.getOperatorCallNode($1, "&", $3, lexer.getPosition());
                 }
                 | arg tCMP arg {
                     $$ = support.getOperatorCallNode($1, "<=>", $3, lexer.getPosition());
                 }
                 | arg tGT arg {
                     $$ = support.getOperatorCallNode($1, ">", $3, lexer.getPosition());
                 }
                 | arg tGEQ arg {
                     $$ = support.getOperatorCallNode($1, ">=", $3, lexer.getPosition());
                 }
                 | arg tLT arg {
                     $$ = support.getOperatorCallNode($1, "<", $3, lexer.getPosition());
                 }
                 | arg tLEQ arg {
                     $$ = support.getOperatorCallNode($1, "<=", $3, lexer.getPosition());
                 }
                 | arg tEQ arg {
                     $$ = support.getOperatorCallNode($1, "==", $3, lexer.getPosition());
                 }
                 | arg tEQQ arg {
                     $$ = support.getOperatorCallNode($1, "===", $3, lexer.getPosition());
                 }
                 | arg tNEQ arg {
                     $$ = support.getOperatorCallNode($1, "!=", $3, lexer.getPosition());
                 }
                 | arg tMATCH arg {
                     $$ = support.getMatchNode($1, $3);
                   /* ENEBO
                         $$ = match_op($1, $3);
                         if (nd_type($1) == NODE_LIT && TYPE($1->nd_lit) == T_REGEXP) {
                             $$ = reg_named_capture_assign($1->nd_lit, $$);
                         }
                   */
                 }
                 | arg tNMATCH arg {
                     $$ = new NotNode(support.getPosition($1), support.getMatchNode($1, $3));
                 }
                 | tBANG arg {
                     $$ = support.getOperatorCallNode(support.getConditionNode($2), "!");
                 }
                 | tTILDE arg {
                     $$ = support.getOperatorCallNode($2, "~");
                 }
                 | arg tLSHFT arg {
                     $$ = support.getOperatorCallNode($1, "<<", $3, lexer.getPosition());
                 }
                 | arg tRSHFT arg {
                     $$ = support.getOperatorCallNode($1, ">>", $3, lexer.getPosition());
                 }
                 | arg tANDOP arg {
                     $$ = support.newAndNode($2.getPosition(), $1, $3);
                 }
                 | arg tOROP arg {
                     $$ = support.newOrNode($2.getPosition(), $1, $3);
                 }
                 | kDEFINED opt_nl arg {
                     // ENEBO: arg surrounded by in_defined set/unset
                     $$ = new DefinedNode($1.getPosition(), $3);
                 }
                 | arg '?' arg opt_nl ':' arg {
                     $$ = new IfNode(support.getPosition($1), support.getConditionNode($1), $3, $6);
                 }
                 | primary {
                     $$ = $1;
                 }
 
 arg_value       : arg {
                     support.checkExpression($1);
                     $$ = $1 != null ? $1 : NilImplicitNode.NIL;
                 }
 
 aref_args       : none
                 | args trailer {
                     $$ = $1;
                 }
                 | args ',' assocs trailer {
                     $$ = support.arg_append($1, new Hash19Node(lexer.getPosition(), $3));
                 }
                 | assocs trailer {
                     $$ = support.newArrayNode($1.getPosition(), new Hash19Node(lexer.getPosition(), $1));
                 }
 
 paren_args      : tLPAREN2 opt_call_args rparen {
                     $$ = $2;
                     if ($$ != null) $<Node>$.setPosition($1.getPosition());
                 }
 
 opt_paren_args  : none | paren_args
 
 opt_call_args   : none | call_args
 
 // [!null]
 call_args       : command {
                     $$ = support.newArrayNode(support.getPosition($1), $1);
                 }
                 | args opt_block_arg {
                     $$ = support.arg_blk_pass($1, $2);
                 }
                 | assocs opt_block_arg {
                     $$ = support.newArrayNode($1.getPosition(), new Hash19Node(lexer.getPosition(), $1));
                     $$ = support.arg_blk_pass((Node)$$, $2);
                 }
                 | args ',' assocs opt_block_arg {
                     $$ = support.arg_append($1, new Hash19Node(lexer.getPosition(), $3));
                     $$ = support.arg_blk_pass((Node)$$, $4);
                 }
                 | block_arg {
                 }
 
 command_args    : /* none */ {
                     $$ = Long.valueOf(lexer.getCmdArgumentState().begin());
                 } call_args {
                     lexer.getCmdArgumentState().reset($<Long>1.longValue());
                     $$ = $2;
                 }
 
 block_arg       : tAMPER arg_value {
                     $$ = new BlockPassNode($1.getPosition(), $2);
                 }
 
 opt_block_arg   : ',' block_arg {
                     $$ = $2;
                 }
                 | ',' {
                     $$ = null;
                 }
                 | none_block_pass
 
 // [!null]
 args            : arg_value {
                     ISourcePosition pos = $1 == null ? lexer.getPosition() : $1.getPosition();
                     $$ = support.newArrayNode(pos, $1);
                 }
                 | tSTAR arg_value {
                     $$ = support.newSplatNode($1.getPosition(), $2);
                 }
                 | args ',' arg_value {
                     Node node = support.splat_array($1);
 
                     if (node != null) {
                         $$ = support.list_append(node, $3);
                     } else {
                         $$ = support.arg_append($1, $3);
                     }
                 }
                 | args ',' tSTAR arg_value {
                     Node node = null;
 
                     // FIXME: lose syntactical elements here (and others like this)
                     if ($4 instanceof ArrayNode &&
                         (node = support.splat_array($1)) != null) {
                         $$ = support.list_concat(node, $4);
                     } else {
                         $$ = support.arg_concat(support.getPosition($1), $1, $4);
                     }
                 }
 
 mrhs            : args ',' arg_value {
                     Node node = support.splat_array($1);
 
                     if (node != null) {
                         $$ = support.list_append(node, $3);
                     } else {
                         $$ = support.arg_append($1, $3);
                     }
                 }
                 | args ',' tSTAR arg_value {
                     Node node = null;
 
                     if ($4 instanceof ArrayNode &&
                         (node = support.splat_array($1)) != null) {
                         $$ = support.list_concat(node, $4);
                     } else {
                         $$ = support.arg_concat($1.getPosition(), $1, $4);
                     }
                 }
                 | tSTAR arg_value {
                      $$ = support.newSplatNode(support.getPosition($1), $2);  
                 }
 
 primary         : literal
                 | strings
                 | xstring
                 | regexp
                 | words
                 | qwords
                 | var_ref
                 | backref
                 | tFID {
                     $$ = new FCallNoArgNode($1.getPosition(), (String) $1.getValue());
                 }
                 | kBEGIN bodystmt kEND {
                     $$ = new BeginNode(support.getPosition($1), $2 == null ? NilImplicitNode.NIL : $2);
                 }
                 | tLPAREN_ARG expr {
                     lexer.setState(LexState.EXPR_ENDARG); 
                 } rparen {
                     support.warning(ID.GROUPED_EXPRESSION, $1.getPosition(), "(...) interpreted as grouped expression");
                     $$ = $2;
                 }
                 | tLPAREN compstmt tRPAREN {
                     if ($2 != null) {
                         // compstmt position includes both parens around it
                         ((ISourcePositionHolder) $2).setPosition($1.getPosition());
                         $$ = $2;
                     } else {
                         $$ = new NilNode($1.getPosition());
                     }
                 }
                 | primary_value tCOLON2 tCONSTANT {
                     $$ = support.new_colon2(support.getPosition($1), $1, (String) $3.getValue());
                 }
                 | tCOLON3 tCONSTANT {
                     $$ = support.new_colon3($1.getPosition(), (String) $2.getValue());
                 }
                 | tLBRACK aref_args tRBRACK {
                     ISourcePosition position = $1.getPosition();
                     if ($2 == null) {
                         $$ = new ZArrayNode(position); /* zero length array */
                     } else {
                         $$ = $2;
                         $<ISourcePositionHolder>$.setPosition(position);
                     }
                 }
                 | tLBRACE assoc_list tRCURLY {
                     $$ = new Hash19Node($1.getPosition(), $2);
                 }
                 | kRETURN {
                     $$ = new ReturnNode($1.getPosition(), NilImplicitNode.NIL);
                 }
                 | kYIELD tLPAREN2 call_args rparen {
                     $$ = support.new_yield($1.getPosition(), $3);
                 }
                 | kYIELD tLPAREN2 rparen {
                     $$ = new ZYieldNode($1.getPosition());
                 }
                 | kYIELD {
                     $$ = new ZYieldNode($1.getPosition());
                 }
                 | kDEFINED opt_nl tLPAREN2 expr rparen {
                     $$ = new DefinedNode($1.getPosition(), $4);
                 }
                 | kNOT tLPAREN2 expr rparen {
                     $$ = support.getOperatorCallNode(support.getConditionNode($3), "!");
                 }
                 | kNOT tLPAREN2 rparen {
                     $$ = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
                 }
                 | operation brace_block {
                     $$ = new FCallNoArgBlockNode($1.getPosition(), (String) $1.getValue(), $2);
                 }
                 | method_call
                 | method_call brace_block {
                     if ($1 != null && 
                           $<BlockAcceptingNode>1.getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, $1.getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     $$ = $<BlockAcceptingNode>1.setIterNode($2);
                     $<Node>$.setPosition($1.getPosition());
                 }
                 | tLAMBDA lambda {
                     $$ = $2;
                 }
                 | kIF expr_value then compstmt if_tail kEND {
                     $$ = new IfNode($1.getPosition(), support.getConditionNode($2), $4, $5);
                 }
                 | kUNLESS expr_value then compstmt opt_else kEND {
                     $$ = new IfNode($1.getPosition(), support.getConditionNode($2), $5, $4);
                 }
                 | kWHILE {
                     lexer.getConditionState().begin();
                 } expr_value do {
                     lexer.getConditionState().end();
                 } compstmt kEND {
                     Node body = $6 == null ? NilImplicitNode.NIL : $6;
                     $$ = new WhileNode($1.getPosition(), support.getConditionNode($3), body);
                 }
                 | kUNTIL {
                   lexer.getConditionState().begin();
                 } expr_value do {
                   lexer.getConditionState().end();
                 } compstmt kEND {
                     Node body = $6 == null ? NilImplicitNode.NIL : $6;
                     $$ = new UntilNode($1.getPosition(), support.getConditionNode($3), body);
                 }
                 | kCASE expr_value opt_terms case_body kEND {
                     $$ = support.newCaseNode($1.getPosition(), $2, $4);
                 }
                 | kCASE opt_terms case_body kEND {
                     $$ = support.newCaseNode($1.getPosition(), null, $3);
                 }
                 | kFOR for_var kIN {
                     lexer.getConditionState().begin();
                 } expr_value do {
                     lexer.getConditionState().end();
                 } compstmt kEND {
                       // ENEBO: Lots of optz in 1.9 parser here
                     $$ = new ForNode($1.getPosition(), $2, $8, $5, support.getCurrentScope());
                 }
                 | kCLASS cpath superclass {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("class definition in method body");
                     }
                     support.pushLocalScope();
                 } bodystmt kEND {
                     Node body = $5 == null ? NilImplicitNode.NIL : $5;
 
                     $$ = new ClassNode($1.getPosition(), $<Colon3Node>2, support.getCurrentScope(), body, $3);
                     support.popCurrentScope();
                 }
                 | kCLASS tLSHFT expr {
                     $$ = Boolean.valueOf(support.isInDef());
                     support.setInDef(false);
                 } term {
                     $$ = Integer.valueOf(support.getInSingle());
                     support.setInSingle(0);
                     support.pushLocalScope();
                 } bodystmt kEND {
                     $$ = new SClassNode($1.getPosition(), $3, support.getCurrentScope(), $7);
                     support.popCurrentScope();
                     support.setInDef($<Boolean>4.booleanValue());
                     support.setInSingle($<Integer>6.intValue());
                 }
                 | kMODULE cpath {
                     if (support.isInDef() || support.isInSingle()) { 
                         support.yyerror("module definition in method body");
                     }
                     support.pushLocalScope();
                 } bodystmt kEND {
                     Node body = $4 == null ? NilImplicitNode.NIL : $4;
 
                     $$ = new ModuleNode($1.getPosition(), $<Colon3Node>2, support.getCurrentScope(), body);
                     support.popCurrentScope();
                 }
                 | kDEF fname {
                     support.setInDef(true);
                     support.pushLocalScope();
                 } f_arglist bodystmt kEND {
                     // TODO: We should use implicit nil for body, but problem (punt til later)
                     Node body = $5; //$5 == null ? NilImplicitNode.NIL : $5;
 
                     $$ = new DefnNode($1.getPosition(), new ArgumentNode($2.getPosition(), (String) $2.getValue()), $4, support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInDef(false);
                 }
                 | kDEF singleton dot_or_colon {
                     lexer.setState(LexState.EXPR_FNAME);
                 } fname {
                     support.setInSingle(support.getInSingle() + 1);
                     support.pushLocalScope();
                     lexer.setState(LexState.EXPR_ENDFN); /* force for args */
                 } f_arglist bodystmt kEND {
                     // TODO: We should use implicit nil for body, but problem (punt til later)
                     Node body = $8; //$8 == null ? NilImplicitNode.NIL : $8;
 
                     $$ = new DefsNode($1.getPosition(), $2, new ArgumentNode($5.getPosition(), (String) $5.getValue()), $7, support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInSingle(support.getInSingle() - 1);
                 }
                 | kBREAK {
                     $$ = new BreakNode($1.getPosition(), NilImplicitNode.NIL);
                 }
                 | kNEXT {
                     $$ = new NextNode($1.getPosition(), NilImplicitNode.NIL);
                 }
                 | kREDO {
                     $$ = new RedoNode($1.getPosition());
                 }
                 | kRETRY {
                     $$ = new RetryNode($1.getPosition());
                 }
 
 primary_value   : primary {
                     support.checkExpression($1);
                     $$ = $1;
                     if ($$ == null) $$ = NilImplicitNode.NIL;
                 }
 
 then            : term
                 | kTHEN
                 | term kTHEN
 
 do              : term
                 | kDO_COND
 
 if_tail         : opt_else
                 | kELSIF expr_value then compstmt if_tail {
                     $$ = new IfNode($1.getPosition(), support.getConditionNode($2), $4, $5);
                 }
 
 opt_else        : none
                 | kELSE compstmt {
                     $$ = $2;
                 }
 
 for_var         : lhs
                 | mlhs {
                 }
 
 f_marg          : f_norm_arg {
                      $$ = support.assignable($1, NilImplicitNode.NIL);
                 }
                 | tLPAREN f_margs rparen {
                     $$ = $2;
                 }
 
 // [!null]
 f_marg_list     : f_marg {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
                 | f_marg_list ',' f_marg {
                     $$ = $1.add($3);
                 }
 
 f_margs         : f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, null, null);
                 }
                 | f_marg_list ',' tSTAR f_norm_arg {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, support.assignable($4, null), null);
                 }
                 | f_marg_list ',' tSTAR f_norm_arg ',' f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, support.assignable($4, null), $6);
                 }
                 | f_marg_list ',' tSTAR {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, new StarNode(lexer.getPosition()), null);
                 }
                 | f_marg_list ',' tSTAR ',' f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, new StarNode(lexer.getPosition()), $5);
                 }
                 | tSTAR f_norm_arg {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, support.assignable($2, null), null);
                 }
                 | tSTAR f_norm_arg ',' f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, support.assignable($2, null), $4);
                 }
                 | tSTAR {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, new StarNode(lexer.getPosition()), null);
                 }
                 | tSTAR ',' f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), null, null, $3);
                 }
 
 // [!null]
 block_param     : f_arg ',' f_block_optarg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, null, $6);
                 }
                 | f_arg ',' f_block_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, $7, $8);
                 }
                 | f_arg ',' f_block_optarg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, null, $4);
                 }
                 | f_arg ',' f_block_optarg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, $5, $6);
                 }
                 | f_arg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, null, $4);
                 }
                 | f_arg ',' {
                     RestArgNode rest = new UnnamedRestArgNode($1.getPosition(), null, support.getCurrentScope().addVariable("*"));
                     $$ = support.new_args($1.getPosition(), $1, null, rest, null, null);
                 }
                 | f_arg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, $5, $6);
                 }
                 | f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, null, null, $2);
                 }
                 | f_block_optarg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args(support.getPosition($1), null, $1, $3, null, $4);
                 }
                 | f_block_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args(support.getPosition($1), null, $1, $3, $5, $6);
                 }
                 | f_block_optarg opt_f_block_arg {
                     $$ = support.new_args(support.getPosition($1), null, $1, null, null, $2);
                 }
                 | f_block_optarg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, $1, null, $3, $4);
                 }
                 | f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, null, $1, null, $2);
                 }
                 | f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, null, $1, $3, $4);
                 }
                 | f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, null, null, null, $1);
                 }
 
 opt_block_param : none {
     // was $$ = null;
                    $$ = support.new_args(lexer.getPosition(), null, null, null, null, null);
                 }
                 | block_param_def {
                     lexer.commandStart = true;
                     $$ = $1;
                 }
 
 block_param_def : tPIPE opt_bv_decl tPIPE {
                     $$ = support.new_args($1.getPosition(), null, null, null, null, null);
                 }
                 | tOROP {
                     $$ = support.new_args($1.getPosition(), null, null, null, null, null);
                 }
                 | tPIPE block_param opt_bv_decl tPIPE {
                     $$ = $2;
                 }
 
 // shadowed block variables....
 opt_bv_decl     : none 
                 | ';' bv_decls {
                     $$ = null;
                 }
 
 // ENEBO: This is confusing...
 bv_decls        : bvar {
                     $$ = null;
                 }
                 | bv_decls ',' bvar {
                     $$ = null;
                 }
 
 bvar            : tIDENTIFIER {
                     support.new_bv($1);
                 }
                 | f_bad_arg {
                     $$ = null;
                 }
 
 lambda          : /* none */  {
                     support.pushBlockScope();
                     $$ = lexer.getLeftParenBegin();
                     lexer.setLeftParenBegin(lexer.incrementParenNest());
                 } f_larglist lambda_body {
                     $$ = new LambdaNode($2.getPosition(), $2, $3, support.getCurrentScope());
                     support.popCurrentScope();
                     lexer.setLeftParenBegin($<Integer>1);
                 }
 
 f_larglist      : tLPAREN2 f_args opt_bv_decl rparen {
                     $$ = $2;
                     $<ISourcePositionHolder>$.setPosition($1.getPosition());
                 }
                 | f_args opt_bv_decl {
                     $$ = $1;
                 }
 
 lambda_body     : tLAMBEG compstmt tRCURLY {
                     $$ = $2;
                 }
                 | kDO_LAMBDA compstmt kEND {
                     $$ = $2;
                 }
 
 do_block        : kDO_BLOCK {
                     support.pushBlockScope();
                 } opt_block_param compstmt kEND {
                     $$ = new IterNode(support.getPosition($1), $3, $4, support.getCurrentScope());
                     support.popCurrentScope();
                 }
 
 block_call      : command do_block {
                     // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
                     if ($1 instanceof YieldNode) {
                         throw new SyntaxException(PID.BLOCK_GIVEN_TO_YIELD, $1.getPosition(), lexer.getCurrentLine(), "block given to yield");
                     }
                     if ($<BlockAcceptingNode>1.getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, $1.getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     $$ = $<BlockAcceptingNode>1.setIterNode($2);
                     $<Node>$.setPosition($1.getPosition());
                 }
                 | block_call tDOT operation2 opt_paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | block_call tCOLON2 operation2 opt_paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
 
 // [!null]
 method_call     : operation paren_args {
                     $$ = support.new_fcall($1, $2, null);
                 }
                 | primary_value tDOT operation2 opt_paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | primary_value tCOLON2 operation2 paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | primary_value tCOLON2 operation3 {
                     $$ = support.new_call($1, $3, null, null);
                 }
diff --git a/src/org/jruby/parser/Ruby19YyTables.java b/src/org/jruby/parser/Ruby19YyTables.java
index 9516328dfa..6f431a4be2 100644
--- a/src/org/jruby/parser/Ruby19YyTables.java
+++ b/src/org/jruby/parser/Ruby19YyTables.java
@@ -1,3929 +1,3897 @@
 package org.jruby.parser;
 
 public class Ruby19YyTables {
    private static short[] combine(short[] t1, short[] t2, 
                                   short[] t3, short[] t4) {
       short[] t = new short[t1.length + t2.length + t3.length + t4.length];
       int index = 0;
       System.arraycopy(t1, 0, t, index, t1.length);
       index += t1.length;
       System.arraycopy(t2, 0, t, index, t2.length);
       index += t2.length;
       System.arraycopy(t3, 0, t, index, t3.length);
       index += t3.length;
       System.arraycopy(t4, 0, t, index, t4.length);
       return t;
    }
 
    public static final short[] yyTable() {
       return combine(yyTable1(), yyTable2(), yyTable3(), yyTable4());
    }
 
    public static final short[] yyCheck() {
       return combine(yyCheck1(), yyCheck2(), yyCheck3(), yyCheck4());
    }
    private static final short[] yyTable1() {
       return new short[] {
 
-          197,  197,  268,   91,  271,  597,  300,  621,  627,  411, 
-          251,  251,  289,  270,  251,  609,  196,  533,  510,  210, 
-          265,  197,  212,  431,  288,  291,   99,    8,  546,  564, 
-          546,  224,  224,  224,   78,  400,  663,    8,  298,  210, 
-          369,  464,  212,  511,  465,  534,  197,  380,  286,  290, 
-          506,  117,  117,  372,  282,  596,  486,  273,  546,  546, 
-          373,  117,  306,  844,  320,  267,  232,  232,  284,  256, 
-          230,  233,  779,   78,   78,  618,  554,  560,  232,  567, 
-          568,  773,  374,  786,  400,   78,    8,   69,  511,  632, 
-          694,  480,  510,  546,  232,  485,  117,  794,  366,  660, 
-          264,  748,  591,  455,   70,  546,  260,  546,  546,  287, 
-          317,  682,   68,  232,  784,  231,  231,  492,  906,  486, 
-          273,   81,  554,  560,  350,  306,  697,  231,  486,  263, 
-          506,  541,  377,  267,  317,  343,  591,  511,   67,  117, 
-          659,  362,  546,  231,  319,  546,  391,  486,  363,  541, 
-          608,  492,  413,  392,  546,   70,  336,   76,  341,  689, 
-          344,  339,  231,   66,  506,  342,  492,  591,  486,  337, 
-          340,  338,   74,   71,  306,  399,  546,  724,   79,  506, 
-          683,  844,  400,  264,  906,  492,  546,  591,  841,  412, 
-          577,  318,  690,  319,  263,  236,  486,  861,  510,  745, 
-          492,   72,  498,   91,  718,   86,  279,  723,   76,  442, 
-          423,  424,  425,  752,  546,  318,  461,  498,  729,   83, 
-           83,  118,  118,   74,   71,   83,  226,  226,  226,   79, 
-          674,  241,  226,  226,  639,  546,  226,   91,  746,   86, 
-          760,  779,  468,  331,   77,  399,  399,   86,   75,  786, 
-          460,  399,   72,  311,   91,  251,   86,  251,  251,  224, 
-          224,  890,   83,  226,  437,  546,  296,  639,  226,  664, 
-          582,  798,  461,   93,  346,  784,  443,  444,  348,  349, 
-          267,  236,  313,  546,  276,   73,  462,  395,  439,  282, 
-          441,  784,  400,   53,  462,   77,  604,  416,  417,   75, 
-          384,  510,  486,   78,  546,  638,  463,  364,  638,  296, 
-          918,  546,  546,  546,  546,  277,  278,  805,  546,  569, 
-          270,  572,  595,  809,  402,  581,  584,  226,  357,   83, 
-          575,  365,  546,  727,  583,  402,   73,  624,  939,  378, 
-          381,  117,  511,  379,  838,  815,  224,  224,  224,  224, 
-          546,  522,  523,  546,  562,  394,  251,  579,  366,  546, 
-          605,  370,  437,  400,  546,   78,   63,   63,  113,  113, 
-          113,  486,   63,   70,  474,  475,  282,  575,  240,  533, 
-          562,  477,  251,  511,  517,  518,  519,  520,  437,    8, 
-          634,  636,  793,  366,  289,  266,  586,  607,  610,  521, 
-          562,  749,  251,  236,  637,  755,  758,  534,  437,   63, 
-          546,  598,  492,  295,  562,  117,  251,  236,   69,  511, 
-          361,  468,  437,  350,  636,  506,   76,  289,  576,  366, 
-          885,  644,  576,   83,  875,   70,  546,  653,  556,  197, 
-          382,   74,   71,   68,   78,  562,  366,   79,  492,   91, 
-          486,  273,  511,  883,  226,  226,  295,  210,  576,  486, 
-          212,  506,  366,   87,  556,  562,  343,  251,  645,  670, 
-           72,  611,   91,  437,   86,  622,   63,  226,  506,  226, 
-          226,  492,   94,  226,  556,  226,  371,  336,   76,  341, 
-           83,  344,  339,  909,  506,  936,  342,   83,  556,  492, 
-          337,  340,  338,   74,   71,  329,  330,  680,  101,   79, 
-          390,  296,  828,   77,  693,  693,  498,   75,  266,   78, 
-          117,  276,  546,  462,  510,  696,  468,  396,  276,  556, 
-          462,  462,   72,  525,   91,  714,   86,  546,  462,  387, 
-          709,  226,  226,  226,  226,   83,  226,  226,  914,  556, 
-          407,  693,  398,  701,   73,  703,  401,  270,  546,  707, 
-          329,  330,  546,  546,  270,  588,  710,  712,  703,  703, 
-          408,  421,  709,  546,  281,   77,   95,   95,  226,   75, 
-           63,  226,   95,  226,   83,  296,  407,  226,  226,  731, 
-           83,  409,  703,  546,  709,  591,  197,  197,  410,  754, 
-          626,  626,  759,   14,  226,   83,  226,  734,  709,  882, 
-          738,  224,  730,  610,  472,  210,   73,   83,  212,   95, 
-           83,  610,  546,  413,  226,  117,  488,  415,   83,  425, 
-          767,  489,  880,  700,  425,  709,  418,   63,  226,  706, 
-          387,  281,  670,  770,   63,   81,  700,  700,  422,  722, 
-          375,  376,   14,  801,  803,  224,  426,  359,  295,  806, 
-           92,   94,   67,  226,  360,   83,  427,  468,  546,  546, 
-          700,  430,   16,  706,  448,  680,  474,  475,  476,   93, 
-          814,  762,  763,  477,  764,  331,   95,  546,  546,  226, 
-          296,  680,   63,  753,  440,  452,  693,  453,   95,  390, 
-          667,  117,  491,  492,  493,  494,  456,  277,  739,  608, 
-           94,  457,  834,   93,  309,  310,  454,  546,  546,  459, 
-          331,   16,  466,   96,  252,  258,  467,  473,  259,   15, 
-           93,   63,  295,  668,  711,  713,  515,   63,   87,   87, 
-          119,  119,  462,  670,   87,  670,  483,  390,   59,   60, 
-          242,  346,   63,  487,  390,  348,  349,  350,  351,   88, 
-          270,  224,   90,   90,   63,  872,  117,   63,   90,  580, 
-          446,  721,   96,  608,  243,   63,  385,  874,   15,  117, 
-          390,   87,  613,  386,  728,  297,  623,  405,  641,  647, 
-           95,  680,  654,   88,  406,  296,   91,  665,   90,  830, 
-          666,   89,  449,  675,  524,   90,  226,   83,  419,  281, 
-           88,  676,   63,  677,  679,  420,  670,  892,   83,  685, 
-          687,  698,  699,  450,  704,   83,  546,  546,  297,  705, 
-          451,  716,   90,  719,  725,   89,  610,  295,  732,  736, 
-          524,  747,  771,  276,  742,  743,  744,   95,   87,   90, 
-          226,  470,   89,  757,   95,   14,   14,   14,  471,  799, 
-          640,   14,   14,  670,   14,  670,  642,  643,  810,  878, 
-          811,  296,   90,  562,  819,  251,  879,  385,   83,  525, 
-           67,  437,  528,  651,  926,  821,  652,  474,  475,   99, 
-          709,  832,  831,  670,  477,  100,  281,  482,  833,   83, 
-           83,  839,   95,  769,  842,  117,  818,  846,  848,   83, 
-          849,   83,  891,  850,   83,  226,  226,  610,   94,  851, 
-          562,  226,  546,  546,   16,   16,   16,  546,  546,  546, 
-           16,   16,  612,   16,   14,   47,  296,  626,   99,  853, 
-          620,   95,  295,  855,  277,   47,  858,   95,   93,  825, 
-          859,  277,   87,   83,   63,   14,  226,  556,  862,  864, 
-          866,  867,   95,  871,  877,   63,   83,   83,   83,  432, 
-          276,  435,   63,  868,   95,  886,   90,   95,  887,  462, 
-           96,   15,   15,   15,  889,   95,  462,   15,   15,  901, 
-           15,   58,  907,  524,   47,  908,  919,  270,  921,  925, 
-          928,   58,  930,   16,  270,  717,  932,  935,  276,   87, 
-           93,  938,  949,   98,  546,   83,   87,  533,  295,  546, 
-          535,  534,   95,  276,   16,   63,  726,  226,   88,  524, 
-          297,   99,  535,   90,   83,  539,  539,   97,  541,  535, 
-           90,  524,  474,  475,  479,   83,   63,   63,  524,  477, 
-           58,  535,  322,  524,  546,  276,   63,  120,   63,  546, 
-           15,   63,   98,  199,   87,  876,  903,   90,  525,  937, 
-           89,  528,  783,  756,  524,  899,   95,  524,  323,  244, 
-          276,   15,  915,  295,  524,  768,   97,  276,   90,  524, 
-           88,  646,  525,   83,  525,  893,  824,  226,    0,   83, 
-           63,  772,    0,   87,  297,   83,    0,    0,    0,   87, 
-          524,    0,    0,   63,   63,   63,  525,    0,  528,  528, 
-            0,    0,    0,  525,   87,   95,  528,   90,  525,   90, 
-            0,  524,   89,   90,    0,    0,   87,  735,    0,   87, 
-          782,   46,  785,    0,    0,  789,   99,   87,   90,  525, 
-            0,   46,  528,    0,    0,  820,    0,  390,    0,    0, 
-           90,    0,   63,   90,   95,  667,  101,  491,  492,  493, 
-          494,   90,    0,    0,  667,   95,  491,  492,  493,  494, 
-          528,   63,   95,    0,   87,    0,    0,    0,  100,    0, 
-          761,    0,   63,    0,    0,  528,  840,   47,   47,   47, 
-           46,  845,   47,   47,   47,    0,   47,  276,   90,  297, 
-           92,  780,  781,    0,  276,  101,   47,    0,    0,  525, 
-            0,  796,  113,  797,    0,    0,  800,  528,   47,   47, 
-           47,   47,   47,    0,    0,   95,    0,  100,    0,    0, 
-           63,  474,  475,  481,   92,    0,   63,  270,  477,    0, 
-            0,    0,   63,   58,   58,   58,   95,   95,   58,   58, 
-           58,   92,   58,    0,    0,  829,   95,    0,   95,  277, 
-           98,   95,   58,   58,    0,    0,   47,    0,  835,  836, 
-          837,   58,   58,  525,   58,   58,   58,   58,   58,    0, 
-            0,  277,  276,    0,   97,    0,    0,   47,  776,  276, 
-          491,  492,  493,  494,  525,    0,    0,  528,  895,    0, 
-           95,  474,  475,  484,  297,    0,    0,  902,  477,  904, 
-          679,    0,  276,   95,   95,   95,   87,  869,    0,    0, 
-            0,  525,   58,   95,    0,   58,    0,   87,  525,    0, 
-          429,  429,  429,  525,   87,    0,  390,  429,  250,  250, 
-           90,    0,  250,   58,    0,  528,    0,  881,  419,  419, 
-          419,   90,  528,    0,  525,  419,   48,  524,   90,    0, 
-            0,    0,   95,    0,  274,  276,   48,    0,    0,    0, 
-          250,  250,    0,  299,  301,  944,    0,    0,  528,    0, 
-          297,   95,    0,    0,  390,    0,    0,   87,  766,    0, 
-            0,  390,   95,   46,   46,   46,    0,    0,   46,   46, 
-           46,  916,   46,    0,    0,    0,  528,  917,   87,   87, 
-            0,   90,   46,  101,    0,   48,   87,  390,   87,    0, 
-           87,   46,   46,   87,   46,   46,   46,   46,   46,    0, 
-            0,    0,   90,   90,    0,  100,    0,    0,    0,    0, 
-           95,    0,   90,    0,   90,  297,   95,   90,    0,    0, 
-           87,    0,   95,    0,  528,   88,    0,    0,  826,    0, 
-            0,  528,   87,    0,    0,    0,  524,   87,    0,   92, 
-          114,  114,   46,    0,  270,   87,   87,   87,    0,    0, 
-          114,  270,  827,    0,  528,    0,   90,  528,  324,  325, 
-          326,  327,  328,   46,  262,    0,  277,  390,    0,   90, 
-           90,   90,  524,  277,  262,    0,    0,  114,  114,    0, 
-            0,    0,    0,  114,  114,  114,  114,    0,  277,    0, 
-            0,    0,    0,    0,   87,  277,    0,    0,  533,  533, 
-          533,   92,    0,    0,  533,  533,    0,  533,  257,    0, 
-            0,    0,    0,   87,    0,    0,    0,  667,   90,  491, 
-          492,  493,  494,  262,   87,    0,    0,  788,  114,  491, 
-          492,  493,  494,    0,    0,  686,  688,   90,    0,  250, 
-          250,  250,  301,    0,    0,    0,    0,    0,   90,    0, 
-          668,    0,    0,  250,  119,  250,  250,    0,  669,   90, 
-          495,    0,    0,   89,  447,    0,    0,    0,  497,  498, 
-          499,  500,   87,  534,  534,  534,    0,  533,   87,  534, 
-          534,    0,  534,    0,   87,    0,    0,    0,   48,   48, 
-           48,    0,    0,   48,   48,   48,   90,   48,  533,    0, 
-            0,    0,   90,    0,    0,    0,  525,   48,   90,    0, 
-          528,    0,    0,    0,    0,    0,   48,   48,    0,   48, 
-           48,   48,   48,   48,  524,  525,  526,  527,  528,  529, 
-          530,  531,  532,  533,  534,  535,  536,  537,  538,  539, 
-          540,  541,  542,  543,  544,  545,  546,  547,  548,  549, 
-            0,    0,  534,    0,  250,   87,  390,  570,    0,  573, 
-           96,  524,    0,  250,  585,    0,    0,   48,    0,    0, 
-            0,    0,  667,  534,  491,  492,  493,  494,    0,    0, 
-          250,  260,  114,  114,  114,  114,    0,  178,   48,  250, 
-            0,  260,    0,    0,    0,    0,    0,    0,    0,    0, 
-          250,  570,  619,  585,  390,  668,  250,  114,    0,  524, 
-            0,  390,    0,  843,  250,    0,  524,   87,    0,  250, 
-          250,  524,    0,  250,    0,  258,  262,  262,  262,    0, 
-          114,  262,  262,  262,    0,  262,  178,  390,    0,    0, 
-          260,    0,  524,    0,    0,  648,  649,  650,    0,    0, 
-            0,    0,    0,  250,    0,    0,  250,  262,  262,  262, 
-          262,  262,    0,    0,    0,  250,    0,  114,  114,  114, 
-          114,  114,  114,  114,  114,  114,  114,  114,  114,  114, 
-          114,  114,  114,  114,  114,  114,  114,  114,  114,  114, 
-          114,  114,  114,    0,   98,  525,    0,    0,   97,  528, 
-          267,  847,  262,    0,  114,  262,    0,    0,    0,  852, 
-          854,    0,  856,    0,  857,    0,  860,    0,  863,  865, 
-            0,    0,    0,    0,    0,    0,  262,    0,  774,    0, 
-            0,    0,  114,    0,    0,    0,    0,    0,  787,    0, 
-            0,  791,    0,  525,  114,  114,  114,  528,    0,  114, 
-          525,    0,    0,  356,  528,  525,    0,    0,    0,  524, 
-            0,    0,  114,  114,  250,    0,  114,    0,    0,  490, 
-            0,  491,  492,  493,  494,    0,  525,    0,    0,    0, 
-          528,    0,  667,    0,  491,  492,  493,  494,  114,  114, 
-          114,    0,    0,    0,    0,  201,  114,    0,    0,  114, 
-            0,    0,  495,  496,    0,  201,    0,    0,    0,  114, 
-          497,  498,  499,  500,    0,  495,    0,    0,    0,    0, 
-            0,  920,  922,  923,  924,  499,  500,  927,  250,  929, 
-          931,  933,  934,    0,    0,    0,    0,    0,    0,  201, 
-            0,    0,    0,  260,  260,  260,  171,    0,  260,  260, 
-          260,    0,  260,  201,  201,    0,    0,    0,  201,  250, 
-            0,    0,    0,    0,    0,    0,    0,  947,    0,    0, 
-          948,  950,  951,  952,  260,  260,  260,  260,  260,    0, 
-          954,    0,    0,  178,    0,  178,  178,  178,  178,    0, 
-            0,  802,  804,    0,    0,  171,    0,  807,  808,    0, 
-            0,    0,  894,    0,  896,  456,  897,  114,  812,  250, 
-          900,    0,  456,  816,  114,  905,  178,  178,    0,  260, 
-            0,    0,  260,  331,  178,  178,  178,  178,  336,  337, 
-            0,    0,    0,    0,    0,  802,  804,  807,  450,  344, 
-          345,  250,    0,  260,    0,    0,    0,    0,  450,    0, 
-            0,    0,    0,    0,  346,    0,  347,    0,  348,  349, 
-          350,  351,  352,  353,  354,    0,  355,    0,    0,    0, 
-            0,  114,  940,    0,    0,    0,    0,    0,    0,  943, 
-            0,  945,  450,  946,    0,    0,    0,    0,    0,    0, 
-          114,    0,  870,    0,    0,    0,  450,  450,    0,  450, 
-          953,  450,  114,  873,    0,    0,  250,  331,  332,  333, 
-          334,  335,  336,  337,  338,  339,  340,  341,    0,  342, 
-          343,    0,    0,  344,  345,  873,    0,    0,    0,  450, 
-            0,    0,    0,    0,    0,    0,    0,    0,  346,    0, 
-          347,  114,  348,  349,  350,  351,  352,  353,  354,    0, 
-          355,  114,  114,    0,    0,  114,  114,  201,  201,  201, 
-            0,  453,  201,  201,  201,    0,  201,    0,  114,    0, 
-            0,  453,    0,  250,    0,    0,  201,  201,  114,  114, 
-          114,    0,    0,    0,  114,  201,  201,    0,  201,  201, 
-          201,  201,  201,    0,  331,    0,    0,    0,    0,    0, 
-          201,    0,    0,    0,    0,  453,    0,    0,    0,    0, 
-          344,  345,    0,    0,    0,    0,    0,    0,    0,  453, 
-          453,    0,  453,    0,  453,  346,    0,  347,    0,  348, 
-          349,  350,  351,  201,    0,  114,  201,    0,    0,  201, 
-            0,  201,  171,  331,  171,  171,  171,  171,    0,  114, 
-            0,    0,  453,    0,    0,    0,    0,  201,    0,  344, 
-          345,    0,    0,    0,  455,    0,    0,    0,  114,  201, 
-            0,  455,    0,    0,  346,  171,  171,    0,  348,  349, 
-          350,  351,    0,  171,  171,  171,  171,  181,    0,    0, 
-            0,    0,    0,    0,  114,  521,  521,  521,    0,  521, 
-          450,  450,  450,  521,  521,  450,  450,  450,  521,  450, 
-          521,  521,  521,  521,  521,  521,  521,  450,  521,  450, 
-          450,  521,  521,  521,  521,  521,  521,  521,  450,  450, 
-          521,  450,  450,  450,  450,  450,  181,  521,    0,    0, 
-          521,  521,  521,  450,  521,  521,  521,  521,  521,  521, 
-          521,  521,  521,  521,  521,  450,  450,  450,  450,  450, 
-          450,  450,  450,  450,  450,  450,  450,  450,  450,    0, 
-            0,  450,  450,  450,  521,  450,  450,  521,  521,  450, 
-          521,  521,  450,  521,  450,  521,  450,  521,  450,  521, 
-          450,  450,  450,  450,  450,  450,  450,  521,  450,  521, 
-          450,    0,  521,  521,  521,  521,  521,  521,    0,    0, 
-          166,  521,  450,  521,  521,    0,  521,  521,  522,  522, 
-          522,    0,  522,  453,  453,  453,  522,  522,  453,  453, 
-          453,  522,  453,  522,  522,  522,  522,  522,  522,  522, 
-          453,  522,  453,  453,  522,  522,  522,  522,  522,  522, 
-          522,  453,  453,  522,  453,  453,  453,  453,  453,  166, 
-          522,    0,    0,  522,  522,  522,  453,  522,  522,  522, 
-          522,  522,  522,  522,  522,  522,  522,  522,  453,  453, 
-          453,  453,  453,  453,  453,  453,  453,  453,  453,  453, 
-          453,  453,    0,    0,  453,  453,  453,  522,  453,  453, 
-          522,  522,  453,  522,  522,  453,  522,  453,  522,  453, 
-          522,  453,  522,  453,  453,  453,  453,  453,  453,  453, 
-          522,  453,  522,  453,    0,  522,  522,  522,  522,  522, 
-          522,  524,    0,    0,  522,  453,  522,  522,    0,  522, 
-          522,  524,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  490,    0,  491,  492,  493, 
-          494,    0,    0,    0,    0,  524,    0,  490,    0,  491, 
-          492,  493,  494,  181,    0,  181,  181,  181,  181,  524, 
-          524,    0,   96,    0,  524,    0,    0,    0,  495,  599, 
-            0,    0,    0,    0,    0,  457,  497,  498,  499,  500, 
-          495,    0,  457,    0,    0,    0,  181,  181,  497,  498, 
-          499,  500,  524,    0,  181,  181,  181,  181,    0,    0, 
-            0,  551,  552,    0,    0,  553,    0,    0,    0,  166, 
-          167,    0,  168,  169,  170,  171,  172,  173,  174,    0, 
-            0,  175,  176,    0,  525,    0,  177,  178,  179,  180, 
-            0,    0,    0,    0,  525,  263,    0,    0,    0,    0, 
-            0,    0,  182,  183,    0,  184,  185,  186,  187,  188, 
-          189,  190,  191,  192,  193,  194,    0,    0,  195,  788, 
-            0,  491,  492,  493,  494,    0,    0,    0,  525,    0, 
-            0,    0,    0,    0,    0,    0,  166,    0,  166,  166, 
-          166,  166,  525,  525,    0,   98,    0,  525,    0,    0, 
-            0,  331,  495,    0,    0,    0,    0,    0,  458,    0, 
-          497,  498,  499,  500,    0,  458,    0,  344,  345,  166, 
-          166,    0,    0,    0,    0,  525,    0,  166,  166,  166, 
-          166,    0,  346,    0,  347,    0,  348,  349,  350,  351, 
-            0,  148,  354,    0,  355,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  524,  524, 
-          524,    0,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          148,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,    0, 
-          524,    0,    0,  524,  524,  524,  524,  524,  524,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  524,    0,    0,  524,  524,  524,  524,    0,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  524,  524,  524,    0,  524,  524,  524,  524,  524, 
-          524,    0,    0,  149,  524,  524,  524,  524,    0,  524, 
-          524,  525,  525,  525,    0,  525,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,    0,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  149,  525,    0,    0,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,    0,    0,  525,  525,  525, 
-          525,    0,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,  525,  525,    0,  525,  525, 
-          525,  525,  525,  525,  528,    0,    0,  525,  525,  525, 
-          525,    0,  525,  525,  528,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  148,    0,  148, 
-          148,  148,  148,    0,    0,    0,    0,    0,  528,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  460, 
-            0,    0,  528,  528,    0,   97,  460,  528,    0,    0, 
-          148,  148,    0,    0,    0,    0,    0,    0,  148,  148, 
-          148,  148,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  528,    0,  557,  558,    0, 
-            0,  559,    0,    0,    0,  166,  167,    0,  168,  169, 
-          170,  171,  172,  173,  174,    0,    0,  175,  176,    0, 
-            0,    0,  177,  178,  179,  180,    0,  271,    0,    0, 
-            0,  263,    0,    0,    0,    0,    0,  271,  182,  183, 
-            0,  184,  185,  186,  187,  188,  189,  190,  191,  192, 
-          193,  194,    0,    0,  195,    0,  331,  332,  333,  334, 
-          335,  336,  337,  338,  339,  340,  341,    0,    0,    0, 
-            0,  271,  344,  345,    0,    0,    0,    0,    0,  149, 
-            0,  149,  149,  149,  149,  271,  271,  346,    0,  347, 
-          271,  348,  349,  350,  351,  352,  353,  354,    0,  355, 
-            0,  459,    0,    0,    0,    0,    0,    0,  459,    0, 
-            0,    0,  149,  149,    0,    0,    0,    0,  271,    0, 
-          149,  149,  149,  149,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  524,  524,  524,    0,  524,  528,  528,  528,  524, 
-          524,  528,  528,  528,  524,  528,  524,  524,  524,  524, 
-          524,  524,  524,    0,  528,  528,  528,  524,  524,  524, 
-          524,  524,  524,  524,  528,  528,  524,  528,  528,  528, 
-          528,  528,  267,  524,    0,    0,  524,  524,  524,  528, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  528,  528,  528,  528,  528,  528,  528,  528,  528, 
-          528,  528,  528,  528,  528,    0,    0,  528,  528,  528, 
-          524,    0,  528,  524,  524,  528,  524,  524,  528,  524, 
-          528,  524,  528,  524,  528,  524,  528,  528,  528,  528, 
-          528,  528,  528,  524,  528,  528,  528,    0,  524,  524, 
-          524,  524,  524,  524,    0,    0,  150,  524,  528,  524, 
-          524,    0,  524,  524,  523,  523,  523,    0,  523,  271, 
-          271,  271,  523,  523,  271,  271,  271,  523,  271,  523, 
-          523,  523,  523,  523,  523,  523,    0,  523,  271,  271, 
-          523,  523,  523,  523,  523,  523,  523,  271,  271,  523, 
-          271,  271,  271,  271,  271,  150,  523,    0,    0,  523, 
-          523,  523,  271,  523,  523,  523,  523,  523,  523,  523, 
-          523,  523,  523,  523,  271,  271,  271,  271,  271,  271, 
-          271,  271,  271,  271,  271,  271,  271,  271,    0,    0, 
-          271,  271,  271,  523,    0,  271,  523,  523,  271,  523, 
-          523,  271,  523,  271,  523,  271,  523,  271,  523,  271, 
-          271,  271,  271,  271,  271,  271,  523,  271,  523,  271, 
-            0,  523,  523,  523,  523,  523,  523,  529,    0,    0, 
-          523,  271,  523,  523,    0,  523,  523,  529,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  529,    0,    0,    0,    0,    0,    0,    0,    4, 
-            5,    6,    0,    8,    0,  529,  529,    9,   10,    0, 
-          529,    0,   11,    0,   12,   13,   14,  100,  101,   17, 
-           18,    0,    0,    0,    0,  102,   20,   21,   22,   23, 
-           24,   25,    0,    0,  105,    0,    0,    0,  529,    0, 
-            0,   28,    0,    0,   31,   32,   33,    0,   34,   35, 
-           36,   37,   38,   39,  246,   40,   41,   42,   43,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          530,    0,    0,    0,    0,    0,    0,    0,  222,    0, 
-          530,  112,    0,    0,   46,   47,    0,   48,    0,  247, 
-            0,  248,    0,   50,    0,    0,    0,    0,    0,    0, 
-            0,  249,    0,    0,    0,    0,   52,   53,   54,   55, 
-           56,   57,    0,    0,  530,   58,    0,   59,   60,    0, 
-           61,   62,  150,    0,  150,  150,  150,  150,  530,  530, 
+          198,  198,  269,   92,  272,  301,  612,  252,  252,  600, 
+          511,  252,  412,  370,  271,  211,  624,  266,  432,  213, 
+          513,  198,  225,  225,  225,  100,  197,  666,  565,  289, 
+          292,   14,  118,  118,  548,  211,  548,  288,  465,  213, 
+          548,  466,  118,  630,  318,  535,  198,  508,  287,  291, 
+            8,  536,  555,  561,  599,  568,  569,  513,  381,  285, 
+            8,  321,  373,  507,  374,  573,  268,  233,  352,  513, 
+          233,  783,  307,  663,  621,  231,  234,  118,  594,  233, 
+           14,  508,  788,  375,  511,  548,  233,  265,  635,  548, 
+          237,   79,   79,   71,   70,  777,  508,  318,  555,  561, 
+           80,  299,  443,  488,  752,  548,  548,  275,  488,    8, 
+          790,  798,  594,  494,  345,  456,  232,   83,  338,  232, 
+          343,  118,  481,  809,  378,  319,  486,   83,  232,  813, 
+          848,  320,  268,  261,   69,  232,  346,  685,  341,  662, 
+          344,  548,  339,  594,   69,   68,  548,  494,  233,  342, 
+          910,   80,  488,  697,  548,  308,  548,  264,   90,  488, 
+           72,  642,  494,  594,  368,  402,  462,   78,  692,  444, 
+          445,  265,  340,  462,  268,  543,  548,  237,  319,   76, 
+          722,  320,  766,  767,  400,  768,  401,  278,  279,  700, 
+          511,  585,   90,  543,  642,  845,  385,  232,  413,  258, 
+          461,  693,  580,  264,  308,  548,  686,  464,  488,   90, 
+          548,   72,  865,  727,  402,  469,  910,  607,   78,  274, 
+           84,   84,  119,  119,  733,  548,   84,  227,  227,  227, 
+           76,  750,  242,  227,  227,   73,   81,  227,  611,  526, 
+          414,  783,   74,  677,  788,   79,  494,  500,  848,   59, 
+           60,  225,  225,  252,  489,  252,  252,  280,  283,  490, 
+          788,  494,  500,   84,  227,  438,  332,  297,  894,  227, 
+          427,   16,  728,  641,  379,  427,  641,  790,  380,  312, 
+          440,   15,  442,   14,   14,   14,   73,   81,  314,   14, 
+           14,  363,   14,   74,  749,  511,   79,  347,  364,  237, 
+          667,  349,  350,  417,  418,  392,  396,   53,  756,  330, 
+          331,  297,  393,  237,  358,  513,  488,  922,  365,  570, 
+           16,  574,  819,  118,  403,  584,  587,  403,  366,  227, 
+           15,   84,  382,  598,  367,  764,  802,  371,  225,  225, 
+          225,  225,  508,  523,  524,  943,  330,  331,  731,  395, 
+          627,  513,   69,  372,  252,  563,  513,  579,  267,  548, 
+          409,  579,   14,  438,  548,  475,  476,  352,  548,   80, 
+          399,  548,  478,   63,   63,  114,  114,  114,  508,   63, 
+          252,  563,  842,   14,  513,  241,  797,  579,  548,  438, 
+          518,  519,  520,  521,  469,  508,  589,  118,  610,  613, 
+          252,  563,  759,  601,  625,  762,  753,  535,  494,  438, 
+          640,  508,    8,  536,  252,  563,   63,  573,  522,  402, 
+          296,  582,  402,  438,   71,   70,  557,   90,  526,   72, 
+          548,   80,  383,  647,  488,   84,   78,  548,  275,  488, 
+          198,  879,  889,  656,  494,  345,  563,  887,   76,  338, 
+           92,  343,  557,  673,  211,   77,  227,  227,  213,  368, 
+          648,  591,  267,   79,  296,  252,  563,  346,  282,  341, 
+          397,  344,  557,  339,  438,   90,  526,  494,  548,  227, 
+          342,  227,  227,  526,   63,  227,  557,  227,  526,   90, 
+          614,   72,   84,  402,  283,  368,  629,  629,   78,   84, 
+          913,  469,  118,  340,   73,   81,   77,   75,  683,  526, 
+           76,   74,  368,  297,   79,  696,  696,  557,  832,  511, 
+          376,  377,  526,   16,   16,   16,  699,  548,  368,   16, 
+           16,  410,   16,   15,   15,   15,  282,  557,   79,   15, 
+           15,  712,   15,  227,  227,  227,  227,   84,  227,  227, 
+          918,  704,  696,  411,  441,  416,  706,  710,   75,  773, 
+          494,  500,  488,  414,  713,  715,   73,   81,  419,  706, 
+          706,  458,  423,   74,  594,  712,   79,  763,  401,  401, 
+          227,  283,  895,  227,  401,  227,   84,  297,   63,  227, 
+          227,  940,   84,  735,  428,  706,  431,  712,  101,  198, 
+          198,  548,   16,  743,  611,  225,  227,   84,  227,  118, 
+          738,  712,   15,  742,  211,  886,  613,  427,  213,   84, 
+          362,  703,   84,   16,  613,  734,  227,  709,  673,  774, 
+           84,  488,  455,   15,  703,  703,  449,   89,  712,  332, 
+          227,  548,  548,  469,  453,   63,  454,  101,  457,  225, 
+          548,  467,   63,  310,  311,  805,  807,  726,  548,  468, 
+          703,  810,  460,  709,  474,  227,  296,   84,  484,  725, 
+          488,   89,  290,  574,   88,   88,  120,  120,  683,  278, 
+           88,  583,  732,  714,  716,  118,  243,  516,   89,  611, 
+          530,  227,  297,  447,  683,  616,  626,  450,  644,  696, 
+           63,  757,  650,  657,  282,  530,  668,  548,   88,  678, 
+           98,  526,   96,  388,  669,  679,  682,   88,  392,  680, 
+          643,  298,  408,  688,   77,  690,  645,  646,  701,  702, 
+          673,  548,  673,  707,  708,  548,  720,  530,  719,   63, 
+          296,  723,  729,  654,  736,   63,  655,  475,  476,  740, 
+          746,  118,   98,  747,  478,  464,  225,  483,  548,  526, 
+           63,   96,  748,  751,  118,  298,  526,  761,  876,  775, 
+          548,  526,   63,  803,  814,   63,   75,  475,  476,  477, 
+          548,  823,  422,   63,  478,   88,   77,  815,   91,   91, 
+           93,  282,  526,  464,   91,  683,  825,  835,  836,  297, 
+          244,   98,  843,  673,  896,  822,  548,  548,  834,   95, 
+          227,   84,  103,  780,  846,  492,  493,  494,  495,  548, 
+           63,  670,   84,  492,  493,  494,  495,  615,  837,   84, 
+          853,   91,  850,  526,  852,  623,  629,  278,   75,  854, 
+          613,  855,  857,  859,  670,  296,  492,  493,  494,  495, 
+          673,  863,  673,  862,  227,  101,  272,  360,  548,  866, 
+          868,  103,  408,   92,  361,  871,  870,  721,  872,  875, 
+          881,  424,  425,  426,  890,  297,  252,  563,  891,  893, 
+          673,  905,   84,  911,  102,  438,   96,   96,  730,   88, 
+          118,  923,   96,  272,  712,  929,  912,   92,   91,   91, 
+           89,  253,  259,   84,   84,  260,   89,  392,  925,  932, 
+          548,  548,  548,   84,   92,   84,  278,  548,   84,  227, 
+          227,  613,  473,  278,  563,  227,  530,  884,  527,   96, 
+          939,  934,   91,  102,  936,  760,   93,  227,  475,  476, 
+          480,  297,  388,   96,  527,  478,   88,  392,  557,   91, 
+          386,  942,  296,   88,  829,  392,  953,  387,   84,  406, 
+          535,  227,  392,  776,   63,  279,  407,  298,   89,   96, 
+           93,   84,   84,   84,  530,   63,  475,  476,  482,  530, 
+          548,  530,   63,  478,  537,  548,  526,   93,  392,  548, 
+          548,  577,  464,  536,  541,  586,   92,   96,  278,  464, 
+          548,   88,  537,   91,  530,  541,   40,  530,  537,   98, 
+          548,  548,  548,  278,   97,  543,   40,  278,  824,  420, 
+           84,  608,  526,  180,  548,  101,  421,  537,  296,  548, 
+          464,  548,  227,  548,  739,   63,  323,  464,  577,   84, 
+           88,  298,  121,  527,  103,  278,   88,  941,  100,  200, 
+           84,  637,  639,  880,  787,  290,   63,   63,  907,  844, 
+           91,   88,   88,   97,  849,   40,   63,   91,   63,  103, 
+          526,   63,  180,   88,  278,  919,   88,  526,  324,  245, 
+          903,  278,  526,  772,   88,  639,  527,  765,  290,  897, 
+          649,    0,    0,  272,  296,  392,   88,  100,   84,  451, 
+          272,   96,  227,  526,   84,   94,  452,  828,  784,  785, 
+           84,   63,    0,   88,    0,   91,    0,    0,  800,    0, 
+          801,   88,    0,  804,   63,   63,   63,    0,    0,  527, 
+          272,    0,   92,  527,   99,   97,  392,  272,  258,    0, 
+            0,  102,    0,  464,    0,    0,  298,  433,  258,  436, 
+            0,    0,  279,  578,   91,  475,  476,  485,   96,  471, 
+           91,    0,  478,  833,  882,   96,  472,   91,  530,  173, 
+            0,  883,    0,   63,    0,   91,  839,  840,  841,  386, 
+            0,  527,  258,   99,  392,    0,  930,   91,  527,    0, 
+           91,  392,   63,  527,   92,    0,  717,  258,   91,    0, 
+            0,    0,  279,   63,    0,   93,  332,    0,    0,  279, 
+            0,    0,    0,   96,  527,  530,  530,  392,  173,  431, 
+          431,  431,    0,  530,    0,  873,  431,    0,  526,   91, 
+            0,  100,  527,  114,    0,   91,    0,  347,  421,  421, 
+          421,  349,  350,  351,  352,  421,    0,   95,    0,  530, 
+            0,   63,   96,  298,  278,  885,    0,   63,   96,    0, 
+            0,  278,  758,   63,    0,   88,  527,   93,   40,   40, 
+           40,   97,    0,   96,   40,   40,   88,   40,    0,    0, 
+          527,   95,  278,   88,  392,   96,    0,  527,   96,  278, 
+            0,    0,  527,  771,  527,    0,   96,    0,   95,   40, 
+           40,   40,   40,   40,    0,  100,    0,    0,   91,  920, 
+            0,    0,  278,  527,    0,  921,    0,    0,  527,  180, 
+            0,  180,  180,  180,  180,    0,    0,    0,  272,  298, 
+            0,   88,  392,   96,    0,    0,   88,  770,    0,  392, 
+          102,  458,    0,    0,  818,    0,    0,   40,  458,    0, 
+            0,    0,  180,  180,    0,  530,    0,   88,   88,    0, 
+          180,  180,  180,  180,    0,  392,  527,   88,   40,   88, 
+            0,    0,   88,  527,    0,    0,  838,    0,  527,   91, 
+          464,  325,  326,  327,  328,  329,    0,  464,    0,  279, 
+           91,   99,    0,   88,    0,  298,  279,   91,    0,  527, 
+          258,  258,  258,    0,  530,  258,  258,  258,  830,  258, 
+          251,  251,   88,    0,  251,    0,    0,    0,    0,  258, 
+          258,    0,    0,    0,    0,   88,   88,   88,  258,  258, 
+            0,  258,  258,  258,  258,  258,  275,  277,    0,    0, 
+            0,  878,  251,  251,    0,  300,  302,    0,    0,    0, 
+           91,    0,  530,    0,  535,  535,  535,    0,    0,  530, 
+          535,  535,    0,  535,  526,  173,    0,  173,  173,  173, 
+          173,   91,   91,    0,   88,    0,    0,   96,    0,  258, 
+            0,   91,  258,   91,  258,  530,   91,  457,   96,    0, 
+            0,    0,    0,   88,  457,   96,    0,    0,  173,  173, 
+          258,    0,   94,    0,   88,  578,  173,  173,  173,  173, 
+          786,    0,  789,    0,    0,  793,   95,  260,    0,    0, 
+            0,    0,  831,    0,    0,    0,   91,  260,    0,    0, 
+            0,    0,    0,  535,  120,    0,   94,    0,    0,   91, 
+           91,   91,    0,   99,  530,    0,    0,    0,   96,    0, 
+            0,    0,   88,   94,  535,    0,    0,    0,   88,    0, 
+            0,  260,    0,    0,   88,  272,    0,    0,    0,   96, 
+           96,    0,  272,    0,    0,    0,  260,    0,   95,   96, 
+            0,   96,    0,  279,   96,  115,  115,    0,   91,    0, 
+            0,    0,  530,    0,    0,  115,    0,    0,    0,  530, 
+            0,    0,    0,  234,  526,    0,    0,   91,  536,  536, 
+          536,    0,    0,  234,  536,  536,    0,  536,   91,    0, 
+            0,    0,  115,  115,   96,  530,    0,    0,  115,  115, 
+          115,  115,    0,    0,    0,    0,    0,   96,   96,   96, 
+            0,    0,  251,  251,  251,  302,    0,  234,  670,    0, 
+          492,  493,  494,  495,    0,    0,  251,    0,  251,  251, 
+            0,  234,  234,    0,    0,    0,   91,  448,    0,    0, 
+            0,    0,   91,    0,  115,    0,    0,    0,   91,  899, 
+            0,  496,  689,  691,    0,    0,   96,  536,  906,    0, 
+          908,  500,  501,  670,    0,  492,  493,  494,  495,    0, 
+            0,    0,    0,    0,    0,   96,  268,    0,  536,    0, 
+            0,    0,    0,    0,    0,    0,   96,    0,    0,    0, 
+          670,    0,  492,  493,  494,  495,  671,  525,  526,  527, 
+          528,  529,  530,  531,  532,  533,  534,  535,  536,  537, 
+          538,  539,  540,  541,  542,  543,  544,  545,  546,  547, 
+          548,  549,  550,  671,    0,    0,  948,  251,    0,  357, 
+          571,  672,  575,    0,   96,    0,  251,  588,    0,  183, 
+           96,   94,    0,    0,    0,    0,   96,    0,    0,  260, 
+          260,  260,    0,  251,  260,  260,  260,    0,  260,    0, 
+            0,  670,  251,  492,  493,  494,  495,    0,  260,  260, 
+            0,    0,    0,  251,  571,  622,  588,  260,  260,  251, 
+          260,  260,  260,  260,  260,    0,    0,  251,  183,    0, 
+          279,    0,  251,  251,  671,    0,  251,  279,  115,  115, 
+          115,  115,  847,   94,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  651,  652, 
+          653,    0,    0,  115,    0,    0,  251,    0,  260,  251, 
+            0,  260,    0,  260,    0,  234,  234,  234,  251,    0, 
+          234,  234,  234,    0,  234,    0,  115,    0,    0,  260, 
+            0,    0,    0,    0,  234,  234,    0,  682,    0,    0, 
+            0,    0,    0,  234,  234,  192,  234,  234,  234,  234, 
+          234,    0,    0,    0,    0,  192,    0,    0,  234,    0, 
+            0,    0,    0,  115,  115,  115,  115,  115,  115,  115, 
+          115,  115,  115,  115,  115,  115,  115,  115,  115,  115, 
+          115,  115,  115,  115,  115,  115,  115,  115,  115,  237, 
+            0,  234,    0,    0,  234,    0,    0,  234,    0,  234, 
+          115,  851,    0,    0,  192,    0,    0,    0,    0,  856, 
+          858,    0,  860,    0,  861,  234,  864,  251,  867,  869, 
+            0,    0,    0,    0,    0,    0,    0,  234,  115,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          115,  115,  115,    0,    0,  115,    0,    0,    0,    0, 
+          549,    0,    0,    0,    0,    0,    0,    0,  115,  115, 
+          549,    0,  115,  332,  333,  334,  335,  336,  337,  338, 
+          339,  340,  341,  342,    0,  343,  344,    0,    0,  345, 
+          346,    0,    0,  251,  115,  115,  115,    0,    0,    0, 
+            0,    0,  115,    0,  347,  115,  348,    0,  349,  350, 
+          351,  352,  353,  354,  355,  115,  356,    0,    0,  549, 
+            0,    0,    0,    0,  251,  183,    0,  183,  183,  183, 
+          183,  924,  926,  927,  928,  168,    0,  931,    0,  933, 
+          935,  937,  938,    0,    0,    0,    0,  459,    0,    0, 
+            0,  150,    0,    0,  459,    0,  806,  808,  183,  183, 
+            0,    0,  811,  812,    0,    0,  183,  183,  183,  183, 
+            0,    0,    0,  816,  622,  251,    0,  951,    0,  820, 
+          952,  954,  955,  956,  168,    0,    0,    0,    0,    0, 
+          958,    0,    0,    0,  792,    0,  492,  493,  494,  495, 
+          150,  806,  808,  811,    0,    0,    0,  251,    0,    0, 
+            0,    0,    0,  115,    0,    0,    0,  192,  192,  192, 
+            0,    0,  115,  192,  192,    0,  192,  496,    0,    0, 
+            0,    0,    0,    0,    0,  498,  499,  500,  501,    0, 
+          332,    0,    0,  452,    0,  192,  192,    0,  192,  192, 
+          192,  192,    0,  452,    0,    0,  345,  346,  874,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  877, 
+            0,  347,  251,  348,    0,  349,  350,  351,  352,  115, 
+            0,  355,    0,  356,    0,    0,    0,  452,    0,    0, 
+            0,  877,    0,    0,    0,    0,  192,    0,  115,    0, 
+            0,  452,  452,    0,  452,    0,  452,    0,    0,  332, 
+          115,    0,    0,    0,    0,    0,    0,  192,    0,    0, 
+            0,    0,  549,  549,  549,  345,  346,  549,  549,  549, 
+            0,  549,    0,    0,  452,    0,    0,    0,    0,  251, 
+          347,  549,  549,    0,  349,  350,  351,  352,    0,  115, 
+          549,  549,    0,  549,  549,  549,  549,  549,    0,  115, 
+            0,  115,    0,    0,  115,  115,  455,    0,    0,    0, 
+          491,    0,  492,  493,  494,  495,  455,  115,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  115,  115,  115, 
+            0,    0,    0,  115,    0,    0,    0,    0,    0,    0, 
+            0,  549,    0,  496,  549,    0,  549,    0,    0,    0, 
+          455,  498,  499,  500,  501,    0,    0,    0,    0,    0, 
+            0,    0,  549,    0,  455,  455,    0,  455,    0,  455, 
+            0,  168,    0,  168,  168,  168,  168,    0,    0,    0, 
+            0,    0,    0,    0,  115,    0,    0,  150,    0,  150, 
+          150,  150,  150,  460,    0,    0,    0,  455,  115,    0, 
+          460,    0,    0,    0,  168,  168,    0,    0,    0,  462, 
+            0,    0,  168,  168,  168,  168,  462,  115,    0,    0, 
+          150,  150,    0,    0,    0,    0,    0,    0,  150,  150, 
+          150,  150,  151,    0,    0,    0,    0,    0,    0,    0, 
+          523,  523,  523,  115,  523,  452,  452,  452,  523,  523, 
+          452,  452,  452,  523,  452,  523,  523,  523,  523,  523, 
+          523,  523,  452,  523,  452,  452,  523,  523,  523,  523, 
+          523,  523,  523,  452,  452,  523,  452,  452,  452,  452, 
+          452,  151,  523,    0,    0,  523,  523,  523,  452,  523, 
+          523,  523,  523,  523,  523,  523,  523,  523,  523,  523, 
+          452,  452,  452,  452,  452,  452,  452,  452,  452,  452, 
+          452,  452,  452,  452,    0,    0,  452,  452,  452,  523, 
+          452,  452,  523,  523,  452,  523,  523,  452,  523,  452, 
+          523,  452,  523,  452,  523,  452,  452,  452,  452,  452, 
+          452,  452,  523,  452,  523,  452,    0,  523,  523,  523, 
+          523,  523,  523,    0,    0,  152,  523,  452,  523,  523, 
+            0,  523,  523,  524,  524,  524,    0,  524,  455,  455, 
+          455,  524,  524,  455,  455,  455,  524,  455,  524,  524, 
+          524,  524,  524,  524,  524,  455,  524,  455,  455,  524, 
+          524,  524,  524,  524,  524,  524,  455,  455,  524,  455, 
+          455,  455,  455,  455,  152,  524,    0,    0,  524,  524, 
+          524,  455,  524,  524,  524,  524,  524,  524,  524,  524, 
+          524,  524,  524,  455,  455,  455,  455,  455,  455,  455, 
+          455,  455,  455,  455,  455,  455,  455,    0,    0,  455, 
+          455,  455,  524,  455,  455,  524,  524,  455,  524,  524, 
+          455,  524,  455,  524,  455,  524,  455,  524,  455,  455, 
+          455,  455,  455,  455,  455,  524,  455,  524,  455,    0, 
+          524,  524,  524,  524,  524,  524,  526,    0,    0,  524, 
+          455,  524,  524,    0,  524,  524,  526,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          491,    0,  492,  493,  494,  495,    0,    0,    0,    0, 
+          526,    0,  491,    0,  492,  493,  494,  495,  151,    0, 
+          151,  151,  151,  151,  526,  526,    0,   98,    0,  526, 
+            0,    0,    0,  496,  497,    0,    0,    0,    0,    0, 
+          461,  498,  499,  500,  501,  496,  602,  461,    0,    0, 
+            0,  151,  151,  498,  499,  500,  501,  526,    0,  151, 
+          151,  151,  151,    0,    0,    0,  552,  553,    0,    0, 
+          554,    0,    0,    0,  167,  168,    0,  169,  170,  171, 
+          172,  173,  174,  175,    0,    0,  176,  177,    0,  527, 
+            0,  178,  179,  180,  181,    0,    0,    0,    0,  527, 
+          264,    0,    0,    0,    0,    0,    0,  183,  184,    0, 
+          185,  186,  187,  188,  189,  190,  191,  192,  193,  194, 
+          195,    0,    0,  196,  792,    0,  492,  493,  494,  495, 
+            0,    0,    0,  527,    0,    0,    0,    0,    0,    0, 
+            0,  152,    0,  152,  152,  152,  152,  527,  527,    0, 
+          100,    0,  527,    0,    0,    0,    0,  496,    0,    0, 
+            0,    0,  332,  463,    0,  498,  499,  500,  501,    0, 
+          463,  332,    0,    0,  152,  152,  337,  338,  345,  346, 
+          527,    0,  152,  152,  152,  152,    0,  345,  346,    0, 
+            0,    0,    0,  347,    0,  348,  109,  349,  350,  351, 
+          352,    0,  347,    0,  348,    0,  349,  350,  351,  352, 
+          353,  354,  355,    0,  356,    0,    0,    0,    0,    0, 
+            0,    0,    0,  526,  526,  526,    0,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  109,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,    0,  526,    0,    0,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,    0,    0,  526, 
+          526,  526,  526,    0,  526,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,  526,  526,    0, 
+          526,  526,  526,  526,  526,  526,    0,    0,  110,  526, 
+          526,  526,  526,    0,  526,  526,  527,  527,  527,    0, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,    0,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  110,  527,    0, 
+            0,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+            0,    0,  527,  527,  527,  527,    0,  527,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          527,  527,    0,  527,  527,  527,  527,  527,  527,  530, 
+            0,    0,  527,  527,  527,  527,    0,  527,  527,  530, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  109,    0,  109,  109,  109,  109,    0,    0, 
             0,    0,    0,  530,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  461,    0,    0,    0,    0,    0, 
-            0,  461,    0,    0,    0,  150,  150,    0,    0,    0, 
-            0,  530,    0,  150,  150,  150,  150,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  107,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  526,  526,  526,    0,  526,  529, 
-          529,  529,  526,  526,  529,  529,  529,  526,  529,  526, 
-          526,  526,  526,  526,  526,  526,  107,  529,  529,  529, 
-          526,  526,  526,  526,  526,  526,  526,  529,  529,  526, 
-          529,  529,  529,  529,  529,    0,  526,    0,    0,  526, 
-          526,  526,  529,  526,  526,  526,  526,  526,  526,  526, 
-          526,  526,  526,  526,  529,  529,  529,  529,  529,  529, 
-          529,  529,  529,  529,  529,  529,  529,  529,    0,    0, 
-          529,  529,  529,  526,    0,  529,  526,  526,  529,  526, 
-          526,  529,  526,  529,  526,  529,  526,  529,  526,  529, 
-          529,  529,  529,  529,  529,  529,  526,  529,  529,  529, 
-            0,  526,  526,  526,  526,  526,  526,    0,    0,  108, 
-          526,  529,  526,  526,    0,  526,  526,  527,  527,  527, 
-            0,  527,  530,  530,  530,  527,  527,  530,  530,  530, 
-          527,  530,  527,  527,  527,  527,  527,  527,  527,    0, 
-          530,  530,  530,  527,  527,  527,  527,  527,  527,  527, 
-          530,  530,  527,  530,  530,  530,  530,  530,  108,  527, 
-            0,    0,  527,  527,  527,  530,  527,  527,  527,  527, 
-          527,  527,  527,  527,  527,  527,  527,  530,  530,  530, 
+            0,    0,    0,    0,  452,    0,    0,  530,  530,    0, 
+           99,  452,  530,    0,    0,  109,  109,    0,    0,    0, 
+            0,    0,    0,  109,  109,  109,  109,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          530,    0,  558,  559,    0,    0,  560,    0,    0,    0, 
+          167,  168,    0,  169,  170,  171,  172,  173,  174,  175, 
+            0,    0,  176,  177,    0,    0,    0,  178,  179,  180, 
+          181,    0,  273,    0,    0,    0,  264,    0,    0,    0, 
+            0,    0,  273,  183,  184,    0,  185,  186,  187,  188, 
+          189,  190,  191,  192,  193,  194,  195,    0,    0,  196, 
+            0,  332,  333,  334,  335,  336,  337,  338,  339,  340, 
+          341,  342,    0,    0,    0,    0,  273,  345,  346,    0, 
+            0,    0,    0,    0,  110,    0,  110,  110,  110,  110, 
+          273,  273,  347,    0,  348,  273,  349,  350,  351,  352, 
+          353,  354,  355,    0,  356,    0,  455,    0,    0,    0, 
+            0,    0,    0,  455,    0,    0,    0,  110,  110,    0, 
+            0,    0,    0,  273,    0,  110,  110,  110,  110,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  526,  526,  526,    0, 
+          526,  530,  530,  530,  526,  526,  530,  530,  530,  526, 
+          530,  526,  526,  526,  526,  526,  526,  526,    0,  530, 
+          530,  530,  526,  526,  526,  526,  526,  526,  526,  530, 
+          530,  526,  530,  530,  530,  530,  530,  268,  526,    0, 
+            0,  526,  526,  526,  530,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  530,  530,  530,  530, 
           530,  530,  530,  530,  530,  530,  530,  530,  530,  530, 
-          530,    0,    0,  530,  530,  530,  527,    0,  530,  527, 
-          527,  530,  527,  527,  530,  527,  530,  527,  530,  527, 
-          530,  527,  530,  530,  530,  530,  530,  530,  530,  527, 
-          530,  530,  530,    0,  527,  527,  527,  527,  527,  527, 
-          276,    0,    0,  527,  530,  527,  527,    0,  527,  527, 
-          276,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  107,    0,  107,  107,  107,  107,    0, 
-            0,    0,    0,    0,  276,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  450,    0,    0,  276,  276, 
-            0,   99,  450,  276,    0,    0,  107,  107,    0,    0, 
-            0,    0,    0,    0,  107,  107,  107,  107,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  276,    0,  565,  552,    0,    0,  566,    0,    0, 
-            0,  166,  167,    0,  168,  169,  170,  171,  172,  173, 
-          174,    0,    0,  175,  176,    0,    0,    0,  177,  178, 
-          179,  180,    0,  389,    0,    0,    0,  263,    0,    0, 
-            0,    0,    0,  389,  182,  183,    0,  184,  185,  186, 
-          187,  188,  189,  190,  191,  192,  193,  194,    0,    0, 
-          195,    0,  331,  332,  333,  334,  335,  336,  337,  338, 
-            0,  340,  341,    0,    0,    0,    0,  389,  344,  345, 
-            0,    0,    0,    0,    0,  108,    0,  108,  108,  108, 
-          108,    0,  389,  346,    0,  347,  389,  348,  349,  350, 
-          351,  352,  353,  354,    0,  355,    0,  453,    0,    0, 
-            0,    0,    0,    0,  453,    0,    0,    0,  108,  108, 
-            0,    0,    0,    0,  389,    0,  108,  108,  108,  108, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  525,  525,  525, 
-            0,  525,  276,  276,  276,  525,  525,  276,  276,  276, 
-          525,  276,  525,  525,  525,  525,  525,  525,  525,    0, 
-            0,  276,  276,  525,  525,  525,  525,  525,  525,  525, 
-          276,  276,  525,  276,  276,  276,  276,  276,  267,  525, 
-            0,    0,  525,  525,  525,  276,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,  525,  525,  276,  276,  276, 
-          276,  276,  276,  276,  276,  276,  276,  276,  276,  276, 
-          276,    0,    0,  276,  276,  276,  525,    0,  276,  525, 
-          525,  276,  525,  525,  276,  525,  276,  525,  276,  525, 
-          276,  525,  276,  276,  276,  276,  276,  276,  276,  525, 
-          276,    0,  276,    0,  525,  525,  525,  525,  525,  525, 
-            0,    0,    0,  525,  276,  525,  525,    0,  525,  525, 
-          250,  250,  250,    0,  250,  389,  389,  389,  250,  250, 
-          389,  389,  389,  250,  389,  250,  250,  250,  250,  250, 
-          250,  250,    0,  389,  389,  389,  250,  250,  250,  250, 
-          250,  250,  250,  389,  389,  250,  389,  389,  389,  389, 
-          389,    0,  250,    0,    0,  250,  250,  250,  356,  250, 
-          250,  250,  250,  250,  250,  250,  250,  250,  250,  250, 
-          389,  389,  389,  389,  389,  389,  389,  389,  389,  389, 
-          389,  389,  389,  389,    0,    0,  389,  389,  389,  250, 
-            0,  389,  250,    0,  389,  250,  250,  389,  250,  389, 
-          250,  389,  250,  389,  250,  389,  389,  389,  389,  389, 
-          389,  389,  250,  389,  389,  389,    0,  250,  250,  250, 
-          250,  250,  250,  546,    0,    0,  250,    0,  250,  250, 
-            0,  250,  250,  546,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  546,    0,    0, 
+            0,    0,  530,  530,  530,  526,    0,  530,  526,  526, 
+          530,  526,  526,  530,  526,  530,  526,  530,  526,  530, 
+          526,  530,  530,  530,  530,  530,  530,  530,  526,  530, 
+          530,  530,    0,  526,  526,  526,  526,  526,  526,    0, 
+            0,    0,  526,  530,  526,  526,    0,  526,  526,  525, 
+          525,  525,    0,  525,  273,  273,  273,  525,  525,  273, 
+          273,  273,  525,  273,  525,  525,  525,  525,  525,  525, 
+          525,    0,  525,  273,  273,  525,  525,  525,  525,  525, 
+          525,  525,  273,  273,  525,  273,  273,  273,  273,  273, 
+            0,  525,    0,    0,  525,  525,  525,  273,  525,  525, 
+          525,  525,  525,  525,  525,  525,  525,  525,  525,  273, 
+          273,  273,  273,  273,  273,  273,  273,  273,  273,  273, 
+          273,  273,  273,    0,    0,  273,  273,  273,  525,    0, 
+          273,  525,  525,  273,  525,  525,  273,  525,  273,  525, 
+          273,  525,  273,  525,  273,  273,  273,  273,  273,  273, 
+          273,  525,  273,  525,  273,    0,  525,  525,  525,  525, 
+          525,  525,  531,    0,    0,  525,  273,  525,  525,    0, 
+          525,  525,  531,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  531,    0,    0,    0, 
+            0,    0,    0,    0,    4,    5,    6,    0,    8,    0, 
+          531,  531,    9,   10,    0,  531,    0,   11,    0,   12, 
+           13,   14,  101,  102,   17,   18,    0,    0,    0,    0, 
+          103,   20,   21,   22,   23,   24,   25,    0,    0,  106, 
+            0,    0,    0,  531,    0,    0,   28,    0,    0,   31, 
+           32,   33,    0,   34,   35,   36,   37,   38,   39,  247, 
+           40,   41,   42,   43,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  532,    0,    0,    0,    0, 
+            0,    0,    0,  223,    0,  532,  113,    0,    0,   46, 
+           47,    0,   48,    0,  248,    0,  249,    0,   50,    0, 
+            0,    0,    0,    0,    0,    0,  250,    0,    0,    0, 
+            0,   52,   53,   54,   55,   56,   57,    0,    0,  532, 
+           58,    0,   59,   60,    0,   61,   62,    0,  566,  553, 
+            0,    0,  567,  532,  532,    0,  167,  168,  532,  169, 
+          170,  171,  172,  173,  174,  175,    0,    0,  176,  177, 
+            0,    0,    0,  178,  179,  180,  181,    0,    0,    0, 
+            0,    0,  264,    0,    0,    0,  532,    0,    0,  183, 
+          184,    0,  185,  186,  187,  188,  189,  190,  191,  192, 
+          193,  194,  195,    0,    0,  196,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  528, 
+          528,  528,    0,  528,  531,  531,  531,  528,  528,  531, 
+          531,  531,  528,  531,  528,  528,  528,  528,  528,  528, 
+          528,    0,  531,  531,  531,  528,  528,  528,  528,  528, 
+          528,  528,  531,  531,  528,  531,  531,  531,  531,  531, 
+            0,  528,  295,    0,  528,  528,  528,  531,  528,  528, 
+          528,  528,  528,  528,  528,  528,  528,  528,  528,  531, 
+          531,  531,  531,  531,  531,  531,  531,  531,  531,  531, 
+          531,  531,  531,    0,    0,  531,  531,  531,  528,    0, 
+          531,  528,  528,  531,  528,  528,  531,  528,  531,  528, 
+          531,  528,  531,  528,  531,  531,  531,  531,  531,  531, 
+          531,  528,  531,  531,  531,    0,  528,  528,  528,  528, 
+          528,  528,    0,    0,    0,  528,  531,  528,  528,    0, 
+          528,  528,  529,  529,  529,    0,  529,  532,  532,  532, 
+          529,  529,  532,  532,  532,  529,  532,  529,  529,  529, 
+          529,  529,  529,  529,    0,  532,  532,  532,  529,  529, 
+          529,  529,  529,  529,  529,  532,  532,  529,  532,  532, 
+          532,  532,  532,    0,  529,    0,    0,  529,  529,  529, 
+          532,  529,  529,  529,  529,  529,  529,  529,  529,  529, 
+          529,  529,  532,  532,  532,  532,  532,  532,  532,  532, 
+          532,  532,  532,  532,  532,  532,    0,    0,  532,  532, 
+          532,  529,    0,  532,  529,  529,  532,  529,  529,  532, 
+          529,  532,  529,  532,  529,  532,  529,  532,  532,  532, 
+          532,  532,  532,  532,  529,  532,  532,  532,    0,  529, 
+          529,  529,  529,  529,  529,  278,    0,    0,  529,  532, 
+          529,  529,    0,  529,  529,  278,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    4,    5,    6,    0,    8, 
-            0,    0,  546,    9,   10,    0,  546,    0,   11,    0, 
-           12,   13,   14,   15,   16,   17,   18,    0,    0,    0, 
-            0,   19,   20,   21,   22,   23,   24,   25,    0,    0, 
-           26,    0,    0,    0,  546,    0,    0,   28,    0,    0, 
+            0,    0,    0,    9,   10,    0,    0,    0,   11,    0, 
+           12,   13,   14,  101,  102,   17,   18,    0,    0,  278, 
+            0,  103,  104,  105,   22,   23,   24,   25,    0,    0, 
+          106,    0,    0,  278,  278,    0,  101,  107,  278,    0, 
            31,   32,   33,    0,   34,   35,   36,   37,   38,   39, 
-            0,   40,   41,   42,   43,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  283,    0,    0,    0, 
-            0,    0,    0,    0,  222,    0,  283,  112,    0,    0, 
-           46,   47,    0,   48,    0,    0,    0,    0,    0,   50, 
-            0,    0,    0,    0,    0,    0,    0,   51,    0,    0, 
-            0,    0,   52,   53,   54,   55,   56,   57,    0,    0, 
-          283,   58,  715,   59,   60,    0,   61,   62,    0,    0, 
-            0,    0,    0,    0,    0,  283,    0,    0,    0,  283, 
-            0,    0,  331,  332,  333,  334,  335,  336,  337,  338, 
-          339,  340,  341,    0,  342,  343,    0,    0,  344,  345, 
-            0,    0,    0,    0,    0,    0,    0,  283,    0,    0, 
-            0,    0,    0,  346,    0,  347,    0,  348,  349,  350, 
-          351,  352,  353,  354,    0,  355,    0,    0,    0,    0, 
-            0,    0,    0,    0,  547,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  547,    0,    0,    0,    0,    0, 
-          250,  250,  250,    0,  250,  546,  546,  546,  250,  250, 
-          546,  546,  546,  250,  546,  250, 
+            0,   40,    0,    0,  110,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  278,    0,    0,    0, 
+            0,    0,    0,    0,  294,    0,    0,  113,    0,    0, 
+           46,   47,    0,   48,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  391,    0, 
+            0,    0,   52,   53,   54,   55,   56,   57,  391,    0, 
+            0,   58,    0,   59,   60,    0,   61,   62,  595,  559, 
+            0,    0,  596,    0,    0,    0,  167,  168,    0,  169, 
+          170,  171,  172,  173,  174,  175,    0,    0,  176,  177, 
+            0,    0,  391,  178,  179,  180,  181,    0,    0,    0, 
+            0,    0,  264,    0,    0,    0,    0,  391,    0,  183, 
+          184,  391,  185,  186,  187,  188,  189,  190,  191,  192, 
+          193,  194,  195,    0,    0,  196,  332,  333,  334,  335, 
+          336,  337,  338,  339,    0,  341,  342,    0,    0,  391, 
+            0,    0,  345,  346,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  347,    0,  348, 
+            0,  349,  350,  351,  352,  353,  354,  355,    0,  356, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  527,  527,  527,    0,  527,  278,  278,  278, 
+          527,  527,  278,  278,  278,  527,  278,  527,  527,  527, 
+          527,  527,  527,  527,    0,    0,  278,  278,  527,  527, 
+          527,  527,  527,  527,  527,  278,  278,  527,  278,  278, 
+          278,  278,  278,  268,  527,    0,    0,  527,  527,  527, 
+          278,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          527,  527,  278,  278,  278,  278,  278,  278,  278,  278, 
+          278,  278,  278,  278,  278,  278,    0,    0,  278,  278, 
+          278,  527,    0,  278,  527,  527,  278,  527,  527,  278, 
+          527,  278,  527,  278,  527,  278,  527,  278,  278,  278, 
+          278,  278,  278,  278,  527,  278,    0,  278,    0,  527, 
+          527,  527,  527,  527,  527,    0,    0,    0,  527,  278, 
+          527,  527,    0,  527,  527,  252,  252,  252,    0,  252, 
+          391,  391,  391,  252,  252,  391,  391,  391,  252,  391, 
+          252,  252,  252,  252,  252,  252,  252,    0,  391,  391, 
+          391,  252,  252,  252,  252,  252,  252,  252,  391,  391, 
+          252,  391,  391,  391,  391,  391,    0,  252,    0,    0, 
+          252,  252,  252,  357,  252,  252,  252,  252,  252,  252, 
+          252,  252,  252,  252,  252,  391,  391,  391,  391,  391, 
+          391,  391,  391,  391,  391,  391,  391,  391,  391,    0, 
+            0,  391,  391,  391,  252,    0,  391,  252,    0,  391, 
+          252,  252,  391,  252,  391,  252,  391,  252,  391,  252, 
+          391,  391,  391,  391,  391,  391,  391,  252,  391,  391, 
+          391,    0,  252,  252,  252,  252,  252,  252,  548,    0, 
+            0,  252,    0,  252,  252,    0,  252,  252,  548,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  548,    0,    0,    0,    0,    0,    0,    0, 
+            4,    5,    6,    0,    8,    0,    0,  548,    9,   10, 
+            0,  548,    0,   11,    0,   12,   13,   14,   15,   16, 
+           17,   18,    0,    0,    0,    0,   19,   20,   21,   22, 
+           23,   24,   25,    0,    0,   26,    0,    0,    0,  548, 
+            0,    0,   28,    0,    0,   31,   32,   33,    0,   34, 
+           35,   36,   37,   38,   39,    0,   40,   41,   42,   43, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  285,    0,    0,    0, 
       };
    }
 
    private static final short[] yyTable2() {
       return new short[] {
 
-          250,  250,  250,  250,  250,  250,    0,  546,  546,  546, 
-          250,  250,  250,  250,  250,  250,  250,  546,  546,  250, 
-          546,  546,  546,  546,  546,    0,  250,  547,  356,  250, 
-          250,  250,    0,  250,  250,  250,  250,  250,  250,  250, 
-          250,  250,  250,  250,  546,  546,  546,  546,  546,  546, 
-          546,  546,  546,  546,  546,  546,  546,  546,    0,    0, 
-          546,  546,  546,  250,    0,  546,  250,    0,  546,  250, 
-          250,  546,  250,  546,  250,  546,  250,  546,  250,  546, 
-          546,  546,  546,  546,  546,  546,  250,  546,  546,  546, 
-            0,  250,  250,  250,  250,  250,  250,  356,    0,    0, 
-          250,    0,  250,  250,    0,  250,  250,  250,  250,  250, 
-            0,  250,  283,  283,  283,  250,  250,  283,  283,  283, 
-          250,  283,  250,  250,  250,  250,  250,  250,  250,    0, 
-            0,  283,  283,  250,  250,  250,  250,  250,  250,  250, 
-          283,  283,  250,  283,  283,  283,  283,  283,    0,  250, 
-            0,    0,  250,  250,  250,    0,  250,  250,  250,  250, 
-          250,  250,  250,  250,  250,  250,  250,  283,  283,  283, 
-          283,  283,  283,  283,  283,  283,  283,  283,  283,  283, 
-          283,    0,    0,  283,  283,  283,  250,    0,  283,  250, 
-            0,  283,  250,  250,  283,  250,  283,  250,  283,  250, 
-          283,  250,  283,  283,  283,  283,  283,  283,  283,  250, 
-          283,  524,  283,    0,  250,  250,  250,  250,  250,  250, 
-            0,  524,    0,  250,    0,  250,  250,    0,  250,  250, 
-          547,  547,  547,    0,    0,  547,  547,  547,    0,  547, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  547, 
-          547,    0,    0,    0,    0,   88,    0,    0,  547,  547, 
-            0,  547,  547,  547,  547,  547,    0,    0,    0,    0, 
-          524,    0,   96,    0,  524,    0,    0,    0,    0,    0, 
-            0,    0,  331,  332,  333,  334,  335,  336,  337,  338, 
-          339,  340,  341,    0,  342,  343,    0,    0,  344,  345, 
-            0,    0,  524,    0,    0,    0,    0,    0,    0,  547, 
-            0,    0,  547,  346,  547,  347,    0,  348,  349,  350, 
-          351,  352,  353,  354,    0,  355,    0,    0,    0,    0, 
-          547,  720,    0,    0,  525,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  525,    0,    0,    0,    0,    0, 
-            0,  331,  332,  333,  334,  335,  336,  337,  338,  339, 
-          340,  341,    0,  342,  343,    0,    0,  344,  345,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,   90,    0, 
-            0,    0,  346,    0,  347,    0,  348,  349,  350,  351, 
-          352,  353,  354,  525,  355,   98,    0,  525,    0,    0, 
-            0,  592,  558,    0,    0,  593,    0,    0,    0,  166, 
-          167,    0,  168,  169,  170,  171,  172,  173,  174,    0, 
-            0,  175,  176,    0,    0,  525,  177,  178,  179,  180, 
-            0,    0,    0,    0,    0,  263,    0,    0,    0,    0, 
-            0,    0,  182,  183,    0,  184,  185,  186,  187,  188, 
-          189,  190,  191,  192,  193,  194,    0,   40,  195,    0, 
-            0,    0,    0,    0,    0,    0,    0,   40,  524,  524, 
-          524,    0,  524,  524,  524,  524,  524,  524,    0,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-            0,  524,    0,    0,  524,  524,  524,  524,  524,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,    0, 
-          524,    0,    0,  524,  524,  524,   40,  524,  524,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  524,    0,    0,  524,  524,  524,  524,    0,    0, 
-          524,  524,  524,  524,  524,    0,  524,    0,  524,  524, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  524,  524,  524,    0,  524,  524,  524,  524,  524, 
-          524,    0,    0,    0,  524,    0,  524,  524,    0,  524, 
-          524,  525,  525,  525,    0,  525,  525,  525,  525,  525, 
-          525,    0,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,    0,  525,    0,    0,  525,  525,  525, 
-          525,  525,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,    0,  525,    0,    0,  525,  525,  525,    0, 
-          525,  525,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,    0,    0,  525,  525,  525, 
-          525,    0,    0,  525,  525,  525,  525,  525,    0,  525, 
-            0,  525,  525,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,  525,  525,  525,  525,    0,  525,  525, 
-          525,  525,  525,  525,  528,    0,    0,  525,    0,  525, 
-          525,    0,  525,  525,  528,    0,    0,    0,    0,   40, 
-           40,   40,    0,    0,    0,   40,   40,    0,   40,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,   89,    0, 
-           40,   40,   40,   40,   40,    0,    0,    0,    0,    0, 
-            0,    0,    0,  528,    0,   97,    0,  528,  331,  332, 
-          333,  334,  335,  336,  337,    0,    0,  340,  341,    0, 
-            0,    0,    0,    0,  344,  345,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  528,    0,    0,   40,  346, 
-            0,  347,    0,  348,  349,  350,  351,  352,  353,  354, 
-            0,  355,    0,    4,    5,    6,    0,    8,    0,   40, 
-            0,    9,   10,    0,    0,    0,   11,  276,   12,   13, 
-           14,  100,  101,   17,   18,    0,    0,  276,    0,  102, 
-          103,  104,   22,   23,   24,   25,    0,    0,  105,    0, 
-            0,    0,    0,    0,    0,  106,    0,    0,   31,   32, 
-           33,    0,  107,   35,   36,   37,  108,   39,    0,   40, 
-            0,   91,  109,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  276,    0,   99,  110, 
-          276,    0,  111,    0,    0,  112,    0,    0,   46,   47, 
-            0,   48,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  276,    0, 
-           52,   53,   54,   55,   56,   57,    0,    0,    0,   58, 
-            0,   59,   60,    0,   61,   62,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          546,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          546,  524,  524,  524,    0,  524,  528,  528,  528,  524, 
-          524,    0,  528,  528,  524,  528,  524,  524,  524,  524, 
-          524,  524,  524,    0,  528,    0,    0,  524,  524,  524, 
-          524,  524,  524,  524,  528,  528,  524,  528,  528,  528, 
-          528,  528,    0,  524,    0,    0,  524,  524,  524,  546, 
-          524,  524,  524,  524,  524,  524,  524,  524,  524,  524, 
-          524,  528,  528,  528,  528,  528,  528,  528,  528,  528, 
-          528,  528,  528,  528,  528,    0,    0,  528,  528,  528, 
-          524,    0,    0,  524,  524,  528,  524,  524,    0,  524, 
-            0,  524,  528,  524,  528,  524,  528,  528,  528,  528, 
-          528,  528,  528,  524,  528,  528,  528,    0,  524,  524, 
-          524,  524,  524,  524,    0,    0,    0,  524,    0,  524, 
-          524,    0,  524,  524,  525,  525,  525,    0,  525,  276, 
-          276,  276,  525,  525,    0,  276,  276,  525,  276,  525, 
-          525,  525,  525,  525,  525,  525,    0,    0,    0,    0, 
-          525,  525,  525,  525,  525,  525,  525,  276,  276,  525, 
-          276,  276,  276,  276,  276,    0,  525,    0,    0,  525, 
-          525,  525,  294,  525,  525,  525,  525,  525,  525,  525, 
-          525,  525,  525,  525,  276,  276,  276,  276,  276,  276, 
-          276,  276,  276,  276,  276,  276,  276,  276,    0,    0, 
-          276,  276,  276,  525,    0,    0,  525,  525,  276,  525, 
-          525,    0,  525,    0,  525,  276,  525,  276,  525,  276, 
-          276,  276,  276,  276,  276,  276,  525,  276,    0,  276, 
-            0,  525,  525,  525,  525,  525,  525,    0,    0,    0, 
-          525,    0,  525,  525,    0,  525,  525,  250,  250,  250, 
-            0,  250,  546,  546,  546,  250,  250,  546,  546,  546, 
-          250,  546,  250,  250,  250,  250,  250,  250,  250,    0, 
-            0,  546,    0,  250,  250,  250,  250,  250,  250,  250, 
-          546,  546,  250,  546,  546,  546,  546,  546,    0,  250, 
-            0,    0,  250,  250,  250,    0,  250,  250,  250,  250, 
-          250,  250,  250,  250,  250,  250,  250,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  390,  546,    0, 
-            0,    0,    0,    0,    0,  546,  250,  390,    0,  250, 
-            0,  546,  250,  250,    0,  250,    0,  250,    0,  250, 
-            0,  250,    0,    0,    0,    0,    0,    0,    0,  250, 
-            0,    0,  546,    0,  250,  250,  250,  250,  250,  250, 
-            0,  390,    0,  250,    0,  250,  250,    0,  250,  250, 
-            0,    0,    0,    0,    0,  390,  390,    0,   95,    0, 
-          390,    0,    0,    0,    0,    4,    5,    6,    0,    8, 
-            0,    0,    0,    9,   10,    0,    0,    0,   11,    0, 
-           12,   13,   14,  100,  101,   17,   18,    0,  390,    0, 
-            0,  102,  103,  104,   22,   23,   24,   25,  389,    0, 
-          105,    0,    0,    0,    0,    0,    0,  106,  389,    0, 
-           31,   32,   33,    0,   34,   35,   36,   37,   38,   39, 
-            0,   40,    0,    0,  109,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  389,    0,  293,    0,    0,  112,    0,    0, 
-           46,   47,    0,   48,    0,    0,  389,  389,    0,    0, 
-            0,  389,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  223,    0,  285,  113,    0,    0, 
+           46,   47,    0,   48,    0,    0,    0,    0,    0,   50, 
+            0,    0,    0,    0,    0,    0,    0,   51,    0,    0, 
             0,    0,   52,   53,   54,   55,   56,   57,    0,    0, 
-            0,   58,    0,   59,   60,    0,   61,   62,    0,  389, 
-          614,  552,    0,    0,  615,    0,    0,    0,  166,  167, 
-            0,  168,  169,  170,  171,  172,  173,  174,    0,    0, 
-          175,  176,  462,    0,    0,  177,  178,  179,  180,    0, 
-            0,    0,  462,    0,  263,    0,    0,    0,    0,    0, 
-            0,  182,  183,    0,  184,  185,  186,  187,  188,  189, 
-          190,  191,  192,  193,  194,    0,    0,  195,    0,    0, 
-            0,    0,    0,    0,    0,    0,  462,    0,    0,  390, 
-          390,  390,    0,    0,  390,  390,  390,    0,  390,    0, 
-          462,  462,    0,   94,    0,  462,    0,  390,  390,  390, 
-            0,    0,    0,    0,    0,    0,    0,  390,  390,    0, 
-          390,  390,  390,  390,  390,    0,    0,    0,    0,    0, 
-            0,    0,  390,  462,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  390,  390,  390,  390,  390,  390, 
-          390,  390,  390,  390,  390,  390,  390,  390,    0,    0, 
-          390,  390,  390,    0,    0,  390,    0,    0,  390,    0, 
-            0,  390,    0,  390,    0,  390,  546,  390,    0,  390, 
-          390,  390,  390,  390,  390,  390,  546,  390,  390,  390, 
-          389,  389,  389,    0,    0,  389,  389,  389,    0,  389, 
-            0,  390,    0,    0,    0,    0,    0,    0,  389,  389, 
-          389,    0,    0,    0,    0,    0,    0,    0,  389,  389, 
-          546,  389,  389,  389,  389,  389,    0,    0,    0,    0, 
-            0,    0,    0,  389,  546,  546,    0,    0,    0,  546, 
-            0,    0,    0,    0,    0,  389,  389,  389,  389,  389, 
-          389,  389,  389,  389,  389,  389,  389,  389,  389,    0, 
-            0,  389,  389,  389,    0,    0,  389,  546,    0,  389, 
-            0,    0,  389,    0,  389,    0,  389,    0,  389,    0, 
-          389,  389,  389,  389,  389,  389,  389,    0,  389,  389, 
-          389,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  389,    0,  462,  462,  462,    0,    0,  462, 
-          462,  462,    0,  462,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  462,  462,    0,    0,    0,    0,    0, 
-            0,    0,  462,  462,    0,  462,  462,  462,  462,  462, 
-            0,    0,    0,    0,    0,    0,    0,  462,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  462, 
-          462,  462,  462,  462,  462,  462,  462,  462,  462,  462, 
-          462,  462,  462,    0,  270,  462,  462,  462,    0,  463, 
-          462,    0,    0,  462,  270,    0,  462,    0,  462,    0, 
-          462,    0,  462,    0,  462,  462,  462,  462,  462,  462, 
-          462,    0,  462,    0,  462,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  462,    0,  270,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  270,  270,    0,  101,    0,  270,  546,  546, 
-          546,    0,    0,  546,  546,  546,    0,  546,    0,    0, 
-            0,    0,    0,    0,    0,    0,  546,  546,  546,    0, 
-            0,    0,    0,    0,    0,  270,  546,  546,    0,  546, 
-          546,  546,  546,  546,    0,    0,    0,    0,    0,    0, 
-            0,  546,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  546,  546,  546,  546,  546,  546,  546, 
-          546,  546,  546,  546,  546,  546,  546,    0,  277,  546, 
-          546,  546,    0,    0,  546,    0,    0,  546,  277,    0, 
-          546,    0,  546,    0,  546,    0,  546,    0,  546,  546, 
-          546,  546,  546,  546,  546,    0,  546,  546,  546,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          546,    0,  277,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  616,  558,  277,  277,  617,  100, 
-            0,  277,  166,  167,    0,  168,  169,  170,  171,  172, 
-          173,  174,    0,    0,  175,  176,    0,    0,    0,  177, 
-          178,  179,  180,    0,    0,    0,    0,    0,  263,  277, 
-            0,    0,    0,    0,    0,  182,  183,    0,  184,  185, 
-          186,  187,  188,  189,  190,  191,  192,  193,  194,    0, 
-            0,  195,  410,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  410,    0,    0,    0,  270,  270,  270,    0, 
-            0,  270,  270,  270,    0,  270,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  270,  270,    0,    0,    0, 
-            0,    0,    0,    0,  270,  270,  410,  270,  270,  270, 
-          270,  270,    0,    0,    0,    0,    0,    0,    0,  270, 
-          410,  410,    0,    0,    0,  410,    0,    0,    0,    0, 
-            0,  270,  270,  270,  270,  270,  270,  270,  270,  270, 
-          270,  270,  270,  270,  270,    0,    0,  270,  270,  270, 
-            0,    0,  270,  410,    0,  270,    0,    0,  270,    0, 
-          270,    0,  270,  288,  270,    0,  270,  270,  270,  270, 
-          270,  270,  270,  288,  270,    0,  270,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  270,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  288,    0,    0, 
-          277,  277,  277,    0,    0,  277,  277,  277,    0,  277, 
-            0,  288,  288,    0,    0,    0,  288,    0,    0,  277, 
-          277,    0,    0,    0,    0,    0,    0,    0,  277,  277, 
-            0,  277,  277,  277,  277,  277,    0,    0,    0,    0, 
-            0,    0,    0,  277,  288,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  277,  277,  277,  277,  277, 
-          277,  277,  277,  277,  277,  277,  277,  277,  277,    0, 
-            0,  277,  277,  277,    0,    0,  277,    0,    0,  277, 
-            0,    0,  277,    0,  277,    0,  277,    0,  277,    0, 
-          277,  277,  277,  277,  277,  277,  277,  234,  277,    0, 
-          277,    0,    0,    0,    0,    0,    0,  234,    0,    0, 
-            0,    0,  277,    0,  410,  410,  410,    0,    0,  410, 
-          410,  410,    0,  410,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  410,  410,    0,    0,    0,    0,    0, 
-            0,  234,  410,  410,    0,  410,  410,  410,  410,  410, 
-            0,    0,    0,    0,    0,  234,  234,  410,    0,    0, 
-          234,    0,    0,    0,    0,    0,    0,    0,    0,  410, 
-          410,  410,  410,  410,  410,  410,  410,  410,  410,  410, 
-          410,  410,  410,    0,    0,  410,  410,  410,  320,    0, 
-          410,    0,    0,  410,    0,    0,  410,    0,  410,    0, 
-          410,  283,  410,    0,  410,  410,  410,  410,  410,  410, 
-          410,  283,  410,    0,  410,  288,  288,  288,    0,    0, 
-          288,  288,  288,    0,  288,    0,  410,    0,    0,    0, 
-            0,    0,    0,    0,  288,  288,    0,    0,    0,    0, 
-            0,    0,    0,  288,  288,  283,  288,  288,  288,  288, 
-          288,    0,    0,    0,    0,    0,    0,    0,  288,  283, 
-          283,    0,    0,    0,  283,    0,    0,    0,    0,    0, 
-          288,  288,  288,  288,  288,  288,  288,  288,  288,  288, 
-          288,  288,  288,  288,    0,    0,  288,  288,  288,    0, 
-            0,  288,  283,    0,  288,    0,    0,  288,    0,  288, 
-            0,  288,    0,  288,    0,  288,  288,  288,  288,  288, 
-          288,  288,    0,  288,  448,  288,    0,    0,    0,    0, 
-            0,    0,    0,    0,  448,    0,    0,  288,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  448,  234, 
-          234,  234,    0,    0,  234,  234,  234,    0,  234,    0, 
-            0,    0,  448,  448,    0,    0,    0,  448,  234,  234, 
-            0,    0,    0,    0,    0,    0,    0,  234,  234,    0, 
-          234,  234,  234,  234,  234,    0,    0,    0,    0,    0, 
-            0,    0,  234,    0,    0,  448,    0,    0,    0,    0, 
-            0,    0,    0,    0,  234,  234,  234,  234,  234,  234, 
-          234,  234,  234,  234,  234,  320,  234,  234,    0,    0, 
-          234,  234,  320,    0,    0,  234,    0,    0,  234,    0, 
-            0,  234,    0,  234,    0,  234,  449,  234,    0,  234, 
-          234,  234,  234,  234,  234,  234,  449,  234,    0,  234, 
-            0,    0,    0,  283,  283,  283,    0,    0,  283,  283, 
-          283,  234,  283,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  283,  283,    0,    0,    0,    0,    0,    0, 
-          449,  283,  283,    0,  283,  283,  283,  283,  283,    0, 
-            0,    0,    0,    0,  449,  449,  283,    0,    0,  449, 
-            0,    0,    0,    0,    0,    0,    0,    0,  283,  283, 
-          283,  283,  283,  283,  283,  283,  283,  283,  283,  283, 
-          283,  283,    0,    0,  283,  283,  283,  449,    0,  283, 
-            0,    0,  283,    0,    0,  283,    0,  283,    0,  283, 
-            0,  283,    0,  283,  283,  283,  283,  283,  283,  283, 
-            0,  283,  212,  283,    0,    0,    0,    0,    0,    0, 
-            0,    0,  212,    0,    0,  283,  448,  448,  448,    0, 
-            0,  448,  448,  448,    0,  448,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  448,  448,    0,    0,    0, 
-            0,    0,    0,    0,  448,  448,  212,  448,  448,  448, 
-          448,  448,    0,    0,    0,    0,    0,    0,    0,  448, 
-          212,  212,    0,    0,    0,  212,    0,    0,    0,    0, 
-            0,    0,  448,  448,  448,  448,  448,  448,  448,  448, 
-          448,  448,  448,  448,  448,    0,    0,  448,  448,  448, 
-            0,    0,  448,    0,    0,  448,    0,    0,  448,    0, 
-          448,    0,  448,  208,  448,    0,  448,  448,  448,  448, 
-          448,  448,  448,  208,  448,    0,  448,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  448,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  208,  449,  449, 
-          449,    0,    0,  449,  449,  449,    0,  449,    0,    0, 
-            0,  208,  208,    0,    0,    0,  208,  449,  449,    0, 
-            0,    0,    0,    0,    0,    0,  449,  449,    0,  449, 
-          449,  449,  449,  449,    0,    0,    0,    0,    0,    0, 
-            0,  449,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  449,  449,  449,  449,  449,  449, 
-          449,  449,  449,  449,  449,  449,  449,    0,  205,  449, 
-          449,  449,    0,    0,  449,    0,    0,  449,  205,    0, 
-          449,    0,  449,    0,  449,    0,  449,    0,  449,  449, 
-          449,  449,  449,  449,  449,    0,  449,    0,  449,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          449,    0,  205,    0,  212,  212,  212,    0,    0,  212, 
-          212,  212,    0,  212,    0,    0,  205,  205,    0,    0, 
-            0,  205,    0,  212,  212,    0,    0,    0,    0,    0, 
-            0,    0,  212,  212,    0,  212,  212,  212,  212,  212, 
-            0,    0,    0,    0,    0,    0,    0,  212,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          212,  212,  212,  212,  212,  212,  212,  212,  212,  212, 
-            0,  212,  212,    0,    0,  212,  212,    0,    0,    0, 
-          212,    0,    0,  212,    0,    0,  212,    0,  212,    0, 
-          212,  207,  212,    0,  212,  212,  212,  212,  212,  212, 
-          212,  207,  212,    0,  212,  208,  208,  208,    0,    0, 
-          208,  208,  208,    0,  208,    0,  212,    0,    0,    0, 
-            0,    0,    0,    0,  208,  208,    0,    0,    0,    0, 
-            0,    0,    0,  208,  208,  207,  208,  208,  208,  208, 
-          208,    0,    0,    0,    0,    0,    0,    0,  208,  207, 
-          207,    0,    0,    0,  207,    0,    0,    0,    0,    0, 
-            0,  208,  208,  208,  208,  208,  208,  208,  208,  208, 
-          208,    0,  208,  208,    0,    0,  208,  208,    0,    0, 
-            0,  208,    0,    0,  208,    0,    0,  208,    0,  208, 
-            0,  208,    0,  208,    0,  208,  208,  208,  208,  208, 
-          208,  208,    0,  208,    0,  208,  206,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  206,  208,    0,    0, 
-          205,  205,  205,    0,    0,  205,  205,  205,    0,  205, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  205, 
-          205,    0,    0,    0,    0,    0,    0,    0,  205,  205, 
-          206,  205,  205,  205,  205,  205,    0,    0,    0,    0, 
-            0,    0,    0,  205,  206,  206,    0,    0,    0,  206, 
-            0,    0,    0,    0,    0,    0,  205,  205,  205,  205, 
-          205,  205,  205,  205,  205,  205,    0,  205,  205,    0, 
-            0,  205,  205,    0,    0,    0,  205,    0,    0,  205, 
-            0,    0,  205,    0,  205,    0,  205,  209,  205,    0, 
-          205,  205,  205,  205,  205,  205,  205,  209,  205,    0, 
-          205,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  205,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  209,    0,  207,  207,  207,    0,    0,  207,  207, 
-          207,    0,  207,    0,    0,  209,  209,    0,    0,    0, 
-          209,    0,  207,  207,    0,    0,    0,    0,    0,    0, 
-            0,  207,  207,    0,  207,  207,  207,  207,  207,    0, 
-            0,    0,    0,    0,    0,    0,  207,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  207, 
-          207,  207,  207,  207,  207,  207,  207,  207,  207,    0, 
-          207,  207,    0,  210,  207,  207,    0,    0,    0,  207, 
-            0,    0,  207,  210,    0,  207,    0,  207,    0,  207, 
-            0,  207,    0,  207,  207,  207,  207,  207,  207,  207, 
-            0,  207,    0,  207,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  207,    0,  210,  206,  206, 
-          206,    0,    0,  206,  206,  206,    0,  206,    0,    0, 
-            0,  210,  210,    0,    0,    0,  210,  206,  206,    0, 
-            0,    0,    0,    0,    0,    0,  206,  206,    0,  206, 
-          206,  206,  206,  206,    0,    0,    0,    0,    0,    0, 
-            0,  206,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  206,  206,  206,  206,  206,  206, 
-          206,  206,  206,  206,    0,  206,  206,    0,    0,  206, 
-          206,    0,    0,    0,  206,    0,    0,  206,    0,    0, 
-          206,    0,  206,    0,  206,  203,  206,    0,  206,  206, 
-          206,  206,  206,  206,  206,  203,  206,    0,  206,  209, 
+          285,   58,  718,   59,   60,    0,   61,   62,    0,    0, 
+            0,    0,    0,    0,    0,  285,    0,    0,    0,  285, 
+            0,    0,  332,  333,  334,  335,  336,  337,  338,  339, 
+          340,  341,  342,    0,  343,  344,    0,    0,  345,  346, 
+            0,    0,    0,    0,    0,    0,    0,  285,    0,    0, 
+            0,    0,    0,  347,    0,  348,    0,  349,  350,  351, 
+          352,  353,  354,  355,    0,  356,    0,    0,    0,    0, 
+            0,    0,    0,    0,  256,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  256,    0,    0,    0,    0,    0, 
+          252,  252,  252,    0,  252,  548,  548,  548,  252,  252, 
+          548,  548,  548,  252,  548,  252,  252,  252,  252,  252, 
+          252,  252,    0,  548,  548,  548,  252,  252,  252,  252, 
+          252,  252,  252,  548,  548,  252,  548,  548,  548,  548, 
+          548,    0,  252,  256,  357,  252,  252,  252,    0,  252, 
+          252,  252,  252,  252,  252,  252,  252,  252,  252,  252, 
+          548,  548,  548,  548,  548,  548,  548,  548,  548,  548, 
+          548,  548,  548,  548,    0,    0,  548,  548,  548,  252, 
+            0,  548,  252,    0,  548,  252,  252,  548,  252,  548, 
+          252,  548,  252,  548,  252,  548,  548,  548,  548,  548, 
+          548,  548,  252,  548,  548,  548,    0,  252,  252,  252, 
+          252,  252,  252,  357,    0,    0,  252,    0,  252,  252, 
+            0,  252,  252,  252,  252,  252,    0,  252,  285,  285, 
+          285,  252,  252,  285,  285,  285,  252,  285,  252,  252, 
+          252,  252,  252,  252,  252,    0,    0,  285,  285,  252, 
+          252,  252,  252,  252,  252,  252,  285,  285,  252,  285, 
+          285,  285,  285,  285,    0,  252,    0,    0,  252,  252, 
+          252,    0,  252,  252,  252,  252,  252,  252,  252,  252, 
+          252,  252,  252,  285,  285,  285,  285,  285,  285,  285, 
+          285,  285,  285,  285,  285,  285,  285,    0,    0,  285, 
+          285,  285,  252,    0,  285,  252,    0,  285,  252,  252, 
+          285,  252,  285,  252,  285,  252,  285,  252,  285,  285, 
+          285,  285,  285,  285,  285,  252,  285,  526,  285,    0, 
+          252,  252,  252,  252,  252,  252,    0,  526,    0,  252, 
+            0,  252,  252,    0,  252,  252,  256,  256,  256,    0, 
+            0,  256,  256,  256,    0,  256,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  256,  256,    0,    0,    0, 
+            0,   90,    0,    0,  256,  256,    0,  256,  256,  256, 
+          256,  256,    0,    0,    0,    0,  526,    0,   98,    0, 
+          526,    0,    0,    0,    0,    0,    0,    0,  332,  333, 
+          334,  335,  336,  337,  338,  339,  340,  341,  342,    0, 
+          343,  344,    0,    0,  345,  346,    0,    0,  526,    0, 
+            0,    0,    0,    0,    0,  256,    0,    0,  256,  347, 
+          256,  348,    0,  349,  350,  351,  352,  353,  354,  355, 
+            0,  356,    0,    0,    0,    0,  256,  724,    0,    0, 
+          527,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          527,    0,    0,    0,    0,    0,    0,  332,  333,  334, 
+          335,  336,  337,  338,  339,  340,  341,  342,    0,  343, 
+          344,    0,    0,  345,  346,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,   92,    0,    0,    0,  347,    0, 
+          348,    0,  349,  350,  351,  352,  353,  354,  355,  527, 
+          356,  100,    0,  527,    0,    0,    0,  617,  553,    0, 
+            0,  618,    0,    0,    0,  167,  168,    0,  169,  170, 
+          171,  172,  173,  174,  175,    0,    0,  176,  177,    0, 
+            0,  527,  178,  179,  180,  181,    0,    0,    0,    0, 
+            0,  264,    0,    0,    0,    0,    0,    0,  183,  184, 
+            0,  185,  186,  187,  188,  189,  190,  191,  192,  193, 
+          194,  195,    0,   49,  196,    0,    0,    0,    0,    0, 
+            0,    0,    0,   49,  526,  526,  526,    0,  526,  526, 
+          526,  526,  526,  526,    0,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,    0,  526,    0,    0, 
+          526,  526,  526,  526,  526,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,    0,  526,    0,    0,  526, 
+          526,  526,   49,  526,  526,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,  526,    0,    0, 
+          526,  526,  526,  526,    0,    0,  526,  526,  526,  526, 
+          526,    0,  526,    0,  526,  526,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,  526,  526,  526, 
+            0,  526,  526,  526,  526,  526,  526,    0,    0,    0, 
+          526,    0,  526,  526,    0,  526,  526,  527,  527,  527, 
+            0,  527,  527,  527,  527,  527,  527,    0,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,    0, 
+          527,    0,    0,  527,  527,  527,  527,  527,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,    0,  527, 
+            0,    0,  527,  527,  527,    0,  527,  527,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          527,    0,    0,  527,  527,  527,  527,    0,    0,  527, 
+          527,  527,  527,  527,    0,  527,    0,  527,  527,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          527,  527,  527,    0,  527,  527,  527,  527,  527,  527, 
+          530,    0,    0,  527,    0,  527,  527,    0,  527,  527, 
+          530,    0,    0,    0,    0,   49,   49,   49,    0,    0, 
+           49,   49,   49,    0,   49,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,   49,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,   91,    0,   49,   49,   49,   49, 
+           49,    0,    0,    0,    0,    0,    0,    0,    0,  530, 
+            0,   99,    0,  530,  332,  333,  334,  335,  336,  337, 
+          338,    0,    0,  341,  342,    0,    0,    0,    0,    0, 
+          345,  346,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  530,    0,    0,   49,  347,    0,  348,    0,  349, 
+          350,  351,  352,  353,  354,  355,    0,  356,    0,    4, 
+            5,    6,    0,    8,    0,   49,    0,    9,   10,    0, 
+            0,    0,   11,  278,   12,   13,   14,  101,  102,   17, 
+           18,    0,    0,  278,    0,  103,  104,  105,   22,   23, 
+           24,   25,    0,    0,  106,    0,    0,    0,    0,    0, 
+            0,  107,    0,    0,   31,   32,   33,    0,  108,   35, 
+           36,   37,  109,   39,    0,   40,    0,   93,  110,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  278,    0,  101,  111,  278,    0,  112,    0, 
+            0,  113,    0,    0,   46,   47,    0,   48,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  278,    0,   52,   53,   54,   55, 
+           56,   57,    0,    0,    0,   58,    0,   59,   60,    0, 
+           61,   62,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  548,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  548,  526,  526,  526, 
+            0,  526,  530,  530,  530,  526,  526,    0,  530,  530, 
+          526,  530,  526,  526,  526,  526,  526,  526,  526,    0, 
+          530,    0,    0,  526,  526,  526,  526,  526,  526,  526, 
+          530,  530,  526,  530,  530,  530,  530,  530,    0,  526, 
+            0,    0,  526,  526,  526,  548,  526,  526,  526,  526, 
+          526,  526,  526,  526,  526,  526,  526,  530,  530,  530, 
+          530,  530,  530,  530,  530,  530,  530,  530,  530,  530, 
+          530,    0,    0,  530,  530,  530,  526,    0,    0,  526, 
+          526,  530,  526,  526,    0,  526,    0,  526,  530,  526, 
+          530,  526,  530,  530,  530,  530,  530,  530,  530,  526, 
+          530,  530,  530,    0,  526,  526,  526,  526,  526,  526, 
+            0,    0,    0,  526,    0,  526,  526,    0,  526,  526, 
+          527,  527,  527,    0,  527,  278,  278,  278,  527,  527, 
+            0,  278,  278,  527,  278,  527,  527,  527,  527,  527, 
+          527,  527,    0,    0,    0,    0,  527,  527,  527,  527, 
+          527,  527,  527,  278,  278,  527,  278,  278,  278,  278, 
+          278,    0,  527,    0,    0,  527,  527,  527,  581,  527, 
+          527,  527,  527,  527,  527,  527,  527,  527,  527,  527, 
+          278,  278,  278,  278,  278,  278,  278,  278,  278,  278, 
+          278,  278,  278,  278,    0,    0,  278,  278,  278,  527, 
+            0,    0,  527,  527,  278,  527,  527,    0,  527,    0, 
+          527,  278,  527,  278,  527,  278,  278,  278,  278,  278, 
+          278,  278,  527,  278,    0,  278,    0,  527,  527,  527, 
+          527,  527,  527,    0,    0,    0,  527,    0,  527,  527, 
+            0,  527,  527,  252,  252,  252,    0,  252,  548,  548, 
+          548,  252,  252,  548,  548,  548,  252,  548,  252,  252, 
+          252,  252,  252,  252,  252,    0,    0,  548,    0,  252, 
+          252,  252,  252,  252,  252,  252,  548,  548,  252,  548, 
+          548,  548,  548,  548,    0,  252,    0,    0,  252,  252, 
+          252,    0,  252,  252,  252,  252,  252,  252,  252,  252, 
+          252,  252,  252,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  392,  548,    0,    0,    0,    0,    0, 
+            0,  548,  252,  392,    0,  252,    0,  548,  252,  252, 
+            0,  252,    0,  252,    0,  252,    0,  252,    0,    0, 
+            0,    0,    0,    0,    0,  252,    0,    0,  548,    0, 
+          252,  252,  252,  252,  252,  252,    0,  392,    0,  252, 
+            0,  252,  252,    0,  252,  252,    0,    0,    0,    0, 
+            0,  392,  392,    0,   97,    0,  392,    0,    0,    0, 
+            0,    4,    5,    6,    0,    8,    0,    0,    0,    9, 
+           10,    0,    0,    0,   11,    0,   12,   13,   14,  101, 
+          102,   17,   18,    0,  392,    0,    0,  103,  104,  105, 
+           22,   23,   24,   25,  391,    0,  106,    0,    0,    0, 
+            0,    0,    0,  107,  391,    0,   31,   32,   33,    0, 
+           34,   35,   36,   37,   38,   39,    0,   40,    0,    0, 
+          110,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  391,    0, 
+          294,    0,    0,  113,    0,    0,   46,   47,    0,   48, 
+            0,    0,  391,  391,    0,    0,    0,  391,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,   52,   53, 
+           54,   55,   56,   57,    0,    0,    0,   58,    0,   59, 
+           60,    0,   61,   62,    0,  391,    0,    0,    0,  778, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  791, 
+            0,    0,  795,    0,    0,    0,    0,    0,  464,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  464,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  464,    0,    0,  392,  392,  392,    0,    0, 
+          392,  392,  392,    0,  392,    0,  464,  464,    0,   96, 
+            0,  464,    0,  392,  392,  392,    0,    0,    0,    0, 
+            0,    0,    0,  392,  392,    0,  392,  392,  392,  392, 
+          392,    0,    0,    0,    0,    0,    0,    0,  392,  464, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          392,  392,  392,  392,  392,  392,  392,  392,  392,  392, 
+          392,  392,  392,  392,    0,    0,  392,  392,  392,    0, 
+            0,  392,    0,    0,  392,    0,    0,  392,    0,  392, 
+            0,  392,  548,  392,    0,  392,  392,  392,  392,  392, 
+          392,  392,  548,  392,  392,  392,  391,  391,  391,    0, 
+            0,  391,  391,  391,  898,  391,  900,  392,  901,    0, 
+            0,    0,  904,    0,  391,  391,  391,  909,    0,    0, 
+            0,    0,    0,    0,  391,  391,  548,  391,  391,  391, 
+          391,  391,    0,    0,    0,    0,    0,    0,    0,  391, 
+          548,  548,    0,    0,    0,  548,    0,    0,    0,    0, 
+            0,  391,  391,  391,  391,  391,  391,  391,  391,  391, 
+          391,  391,  391,  391,  391,    0,    0,  391,  391,  391, 
+            0,    0,  391,  548,  944,  391,    0,    0,  391,    0, 
+          391,  947,  391,  949,  391,  950,  391,  391,  391,  391, 
+          391,  391,  391,    0,  391,  391,  391,    0,    0,    0, 
+            0,    0,  957,    0,    0,    0,    0,    0,  391,    0, 
+          464,  464,  464,    0,    0,  464,  464,  464,    0,  464, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  464, 
+          464,    0,    0,    0,    0,    0,    0,    0,  464,  464, 
+            0,  464,  464,  464,  464,  464,    0,    0,    0,    0, 
+            0,    0,    0,  464,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  464,  464,  464,  464,  464, 
+          464,  464,  464,  464,  464,  464,  464,  464,  464,    0, 
+          272,  464,  464,  464,    0,  465,  464,    0,    0,  464, 
+          272,    0,  464,    0,  464,    0,  464,    0,  464,    0, 
+          464,  464,  464,  464,  464,  464,  464,    0,  464,    0, 
+          464,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  464,    0,  272,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  272,  272, 
+            0,  103,    0,  272,  548,  548,  548,    0,    0,  548, 
+          548,  548,    0,  548,    0,    0,    0,    0,    0,    0, 
+            0,    0,  548,  548,  548,    0,    0,    0,    0,    0, 
+            0,  272,  548,  548,    0,  548,  548,  548,  548,  548, 
+            0,    0,    0,    0,    0,    0,    0,  548,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  548, 
+          548,  548,  548,  548,  548,  548,  548,  548,  548,  548, 
+          548,  548,  548,    0,  279,  548,  548,  548,    0,    0, 
+          548,    0,    0,  548,  279,    0,  548,    0,  548,    0, 
+          548,    0,  548,    0,  548,  548,  548,  548,  548,  548, 
+          548,    0,  548,  548,  548,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  548,    0,  279,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          619,  559,  279,  279,  620,  102,    0,  279,  167,  168, 
+            0,  169,  170,  171,  172,  173,  174,  175,    0,    0, 
+          176,  177,    0,    0,    0,  178,  179,  180,  181,    0, 
+            0,    0,    0,    0,  264,  279,    0,    0,    0,    0, 
+            0,  183,  184,    0,  185,  186,  187,  188,  189,  190, 
+          191,  192,  193,  194,  195,    0,    0,  196,  412,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  412,    0, 
+            0,    0,  272,  272,  272,    0,    0,  272,  272,  272, 
+            0,  272,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  272,  272,    0,    0,    0,    0,    0,    0,    0, 
+          272,  272,  412,  272,  272,  272,  272,  272,    0,    0, 
+            0,    0,    0,    0,    0,  272,  412,  412,    0,    0, 
+            0,  412,    0,    0,    0,    0,    0,  272,  272,  272, 
+          272,  272,  272,  272,  272,  272,  272,  272,  272,  272, 
+          272,    0,    0,  272,  272,  272,    0,    0,  272,  412, 
+            0,  272,    0,    0,  272,    0,  272,    0,  272,  290, 
+          272,    0,  272,  272,  272,  272,  272,  272,  272,  290, 
+          272,    0,  272,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  272,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  290,    0,    0,  279,  279,  279,    0, 
+            0,  279,  279,  279,    0,  279,    0,  290,  290,    0, 
+            0,    0,  290,    0,    0,  279,  279,    0,    0,    0, 
+            0,    0,    0,    0,  279,  279,    0,  279,  279,  279, 
+          279,  279,    0,    0,    0,    0,    0,    0,    0,  279, 
+          290,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  279,  279,  279,  279,  279,  279,  279,  279,  279, 
+          279,  279,  279,  279,  279,    0,    0,  279,  279,  279, 
+            0,    0,  279,    0,    0,  279,    0,    0,  279,    0, 
+          279,    0,  279,    0,  279,    0,  279,  279,  279,  279, 
+          279,  279,  279,  236,  279,    0,  279,    0,    0,    0, 
+            0,    0,    0,  236,    0,    0,    0,    0,  279,    0, 
+          412,  412,  412,    0,    0,  412,  412,  412,    0,  412, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  412, 
+          412,    0,    0,    0,    0,    0,    0,  236,  412,  412, 
+            0,  412,  412,  412,  412,  412,    0,    0,    0,    0, 
+            0,  236,  236,  412,    0,    0,  236,    0,    0,    0, 
+            0,    0,    0,    0,    0,  412,  412,  412,  412,  412, 
+          412,  412,  412,  412,  412,  412,  412,  412,  412,    0, 
+            0,  412,  412,  412,  322,    0,  412,    0,    0,  412, 
+            0,    0,  412,    0,  412,    0,  412,  285,  412,    0, 
+          412,  412,  412,  412,  412,  412,  412,  285,  412,    0, 
+          412,  290,  290,  290,    0,    0,  290,  290,  290,    0, 
+          290,    0,  412,    0,    0,    0,    0,    0,    0,    0, 
+          290,  290,    0,    0,    0,    0,    0,    0,    0,  290, 
+          290,  285,  290,  290,  290,  290,  290,    0,    0,    0, 
+            0,    0,    0,    0,  290,  285,  285,    0,    0,    0, 
+          285,    0,    0,    0,    0,    0,  290,  290,  290,  290, 
+          290,  290,  290,  290,  290,  290,  290,  290,  290,  290, 
+            0,    0,  290,  290,  290,    0,    0,  290,  285,    0, 
+          290,    0,    0,  290,    0,  290,    0,  290,    0,  290, 
+            0,  290,  290,  290,  290,  290,  290,  290,    0,  290, 
+          450,  290,    0,    0,    0,    0,    0,    0,    0,    0, 
+          450,    0,    0,  290,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  450,  236,  236,  236,    0,    0, 
+          236,  236,  236,    0,  236,    0,    0,    0,  450,  450, 
+            0,    0,    0,  450,  236,  236,    0,    0,    0,    0, 
+            0,    0,    0,  236,  236,    0,  236,  236,  236,  236, 
+          236,    0,    0,    0,    0,    0,    0,    0,  236,    0, 
+            0,  450,    0,    0,    0,    0,    0,    0,    0,    0, 
+          236,  236,  236,  236,  236,  236,  236,  236,  236,  236, 
+          236,  322,  236,  236,    0,    0,  236,  236,  322,    0, 
+            0,  236,    0,    0,  236,    0,    0,  236,    0,  236, 
+            0,  236,  451,  236,    0,  236,  236,  236,  236,  236, 
+          236,  236,  451,  236,    0,  236,    0,    0,    0,  285, 
+          285,  285,    0,    0,  285,  285,  285,  236,  285,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  285,  285, 
+            0,    0,    0,    0,    0,    0,  451,  285,  285,    0, 
+          285,  285,  285,  285,  285,    0,    0,    0,    0,    0, 
+          451,  451,  285,    0,    0,  451,    0,    0,    0,    0, 
+            0,    0,    0,    0,  285,  285,  285,  285,  285,  285, 
+          285,  285,  285,  285,  285,  285,  285,  285,    0,    0, 
+          285,  285,  285,  451,    0,  285,    0,    0,  285,    0, 
+            0,  285,    0,  285,    0,  285,    0,  285,    0,  285, 
+          285,  285,  285,  285,  285,  285,    0,  285,  214,  285, 
+            0,    0,    0,    0,    0,    0,    0,    0,  214,    0, 
+            0,  285,  450,  450,  450,    0,    0,  450,  450,  450, 
+            0,  450,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  450,  450,    0,    0,    0,    0,    0,    0,    0, 
+          450,  450,  214,  450,  450,  450,  450,  450,    0,    0, 
+            0,    0,    0,    0,    0,  450,  214,  214,    0,    0, 
+            0,  214,    0,    0,    0,    0,    0,    0,  450,  450, 
+          450,  450,  450,  450,  450,  450,  450,  450,  450,  450, 
+          450,    0,    0,  450,  450,  450,    0,    0,  450,    0, 
+            0,  450,    0,    0,  450,    0,  450,    0,  450,  210, 
+          450,    0,  450,  450,  450,  450,  450,  450,  450,  210, 
+          450,    0,  450,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  450,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  210,  451,  451,  451,    0,    0,  451, 
+          451,  451,    0,  451,    0,    0,    0,  210,  210,    0, 
+            0,    0,  210,  451,  451,    0,    0,    0,    0,    0, 
+            0,    0,  451,  451,    0,  451,  451,  451,  451,  451, 
+            0,    0,    0,    0,    0,    0,    0,  451,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          451,  451,  451,  451,  451,  451,  451,  451,  451,  451, 
+          451,  451,  451,    0,  207,  451,  451,  451,    0,    0, 
+          451,    0,    0,  451,  207,    0,  451,    0,  451,    0, 
+          451,    0,  451,    0,  451,  451,  451,  451,  451,  451, 
+          451,    0,  451,    0,  451,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  451,    0,  207,    0, 
+          214,  214,  214,    0,    0,  214,  214,  214,    0,  214, 
+            0,    0,  207,  207,    0,    0,    0,  207,    0,  214, 
+          214,    0,    0,    0,    0,    0,    0,    0,  214,  214, 
+            0,  214,  214,  214,  214,  214,    0,    0,    0,    0, 
+            0,    0,    0,  214,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  214,  214,  214,  214, 
+          214,  214,  214,  214,  214,  214,    0,  214,  214,    0, 
+            0,  214,  214,    0,    0,    0,  214,    0,    0,  214, 
+            0,    0,  214,    0,  214,    0,  214,  209,  214,    0, 
+          214,  214,  214,  214,  214,  214,  214,  209,  214,    0, 
+          214,  210,  210,  210,    0,    0,  210,  210,  210,    0, 
+          210,    0,  214,    0,    0,    0,    0,    0,    0,    0, 
+          210,  210,    0,    0,    0,    0,    0,    0,    0,  210, 
+          210,  209,  210,  210,  210,  210,  210,    0,    0,    0, 
+            0,    0,    0,    0,  210,  209,  209,    0,    0,    0, 
+          209,    0,    0,    0,    0,    0,    0,  210,  210,  210, 
+          210,  210,  210,  210,  210,  210,  210,    0,  210,  210, 
+            0,    0,  210,  210,    0,    0,    0,  210,    0,    0, 
+          210,    0,    0,  210,    0,  210,    0,  210,    0,  210, 
+            0,  210,  210,  210,  210,  210,  210,  210,    0,  210, 
+            0,  210,  208,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  208,  210,    0,    0,  207,  207,  207,    0, 
+            0,  207,  207,  207,    0,  207,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  207,  207,    0,    0,    0, 
+            0,    0,    0,    0,  207,  207,  208,  207,  207,  207, 
+          207,  207,    0,    0,    0,    0,    0,    0,    0,  207, 
+          208,  208,    0,    0,    0,  208,    0,    0,    0,    0, 
+            0,    0,  207,  207,  207,  207,  207,  207,  207,  207, 
+          207,  207,    0,  207,  207,    0,    0,  207,  207,    0, 
+            0,    0,  207,    0,    0,  207,    0,    0,  207,    0, 
+          207,    0,  207,  211,  207,    0,  207,  207,  207,  207, 
+          207,  207,  207,  211,  207,    0,  207,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  207,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  211,    0,  209, 
           209,  209,    0,    0,  209,  209,  209,    0,  209,    0, 
-          206,    0,    0,    0,    0,    0,    0,    0,  209,  209, 
-            0,    0,    0,    0,    0,    0,    0,  209,  209,  203, 
+            0,  211,  211,    0,    0,    0,  211,    0,  209,  209, 
+            0,    0,    0,    0,    0,    0,    0,  209,  209,    0, 
           209,  209,  209,  209,  209,    0,    0,    0,    0,    0, 
-            0,    0,  209,  203,  203,    0,    0,    0,  203,    0, 
+            0,    0,  209,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,  209,  209,  209,  209,  209, 
-          209,  209,  209,  209,  209,    0,  209,  209,    0,    0, 
-          209,  209,    0,    0,    0,  209,    0,    0,  209,    0, 
+          209,  209,  209,  209,  209,    0,  209,  209,    0,  212, 
+          209,  209,    0,    0,    0,  209,    0,    0,  209,  212, 
             0,  209,    0,  209,    0,  209,    0,  209,    0,  209, 
-          209,  209,  209,  209,  209,  209,    0,  209,  204,  209, 
-            0,    0,    0,    0,    0,    0,    0,    0,  204,    0, 
-            0,  209,    0,    0,    0,  210,  210,  210,    0,    0, 
-          210,  210,  210,    0,  210,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  210,  210,    0,    0,    0,    0, 
-            0,    0,  204,  210,  210,    0,  210,  210,  210,  210, 
-          210,    0,    0,    0,    0,    0,  204,  204,  210,    0, 
-            0,  204,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  210,  210,  210,  210,  210,  210,  210,  210,  210, 
-          210,    0,  210,  210,    0,    0,  210,  210,    0,    0, 
-            0,  210,    0,    0,  210,    0,    0,  210,    0,  210, 
-            0,  210,  228,  210,    0,  210,  210,  210,  210,  210, 
-          210,  210,  228,  210,    0,  210,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  210,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  228,  203,  203,  203, 
-            0,    0,  203,  203,  203,    0,  203,    0,    0,    0, 
-          228,  228,    0,    0,    0,  228,  203,  203,    0,    0, 
-            0,    0,    0,    0,    0,  203,  203,    0,  203,  203, 
-          203,  203,  203,    0,    0,    0,    0,    0,    0,    0, 
-          203,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  203,  203,  203,  203,  203,  203,  203, 
-          203,  203,  203,    0,  203,  203,    0,    0,  203,  203, 
-            0,    0,    0,  203,  229,    0,  203,    0,    0,  203, 
-            0,  203,    0,    0,  229,  203,    0,    0,    0,  203, 
-          203,  203,  203,  203,    0,  203,    0,  203,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  203, 
-          204,  204,  204,    0,    0,  204,  204,  204,  229,  204, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  204, 
-          204,    0,  229,  229,    0,    0,    0,  229,  204,  204, 
-            0,  204,  204,  204,  204,  204,    0,    0,    0,    0, 
-            0,    0,    0,  204,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  204,  204,  204,  204, 
-          204,  204,  204,  204,  204,  204,    0,  204,  204,    0, 
-            0,  204,  204,    0,    0,    0,  204,    0,    0,  204, 
-            0,    0,  204,    0,  204,    0,    0,  215,  204,    0, 
-            0,    0,  204,  204,  204,  204,  204,  215,  204,    0, 
-          204,    0,    0,    0,  228,  228,  228,    0,    0,  228, 
-          228,  228,  204,  228,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  228,  228,    0,    0,    0,    0,    0, 
-            0,  215,  228,  228,    0,  228,  228,  228,  228,  228, 
-            0,    0,    0,    0,    0,  215,  215,  228,    0,    0, 
-          215,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          228,  228,  228,  228,  228,  228,  228,  228,  228,  228, 
-            0,  228,  228,    0,    0,  228,  228,    0,    0,    0, 
-          228,    0,    0,  228,    0,    0,  228,    0,  228,    0, 
-            0,    0,  228,    0,    0,    0,    0,    0,  228,  228, 
-          228,    0,  228,    0,  228,  213,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  213,  228,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  229,  229,  229,    0, 
-            0,  229,  229,  229,    0,  229,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  229,  229,    0,    0,  213, 
-            0,    0,    0,    0,  229,  229,    0,  229,  229,  229, 
-          229,  229,    0,  213,  213,    0,    0,    0,  213,  229, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  229,  229,  229,  229,  229,  229,  229,  229, 
-          229,  229,    0,  229,  229,    0,    0,  229,  229,    0, 
-            0,    0,  229,    0,    0,  229,    0,    0,  229,    0, 
-          229,    0,    0,  214,  229,    0,    0,    0,    0,    0, 
-          229,  229,  229,  214,  229,    0,  229,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  229,    0, 
+          209,  209,  209,  209,  209,  209,    0,  209,    0,  209, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  209,    0,  212,  208,  208,  208,    0,    0,  208, 
+          208,  208,    0,  208,    0,    0,    0,  212,  212,    0, 
+            0,    0,  212,  208,  208,    0,    0,    0,    0,    0, 
+            0,    0,  208,  208,    0,  208,  208,  208,  208,  208, 
+            0,    0,    0,    0,    0,    0,    0,  208,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          208,  208,  208,  208,  208,  208,  208,  208,  208,  208, 
+            0,  208,  208,    0,    0,  208,  208,    0,    0,    0, 
+          208,    0,    0,  208,    0,    0,  208,    0,  208,    0, 
+          208,  205,  208,    0,  208,  208,  208,  208,  208,  208, 
+          208,  205,  208,    0,  208,  211,  211,  211,    0,    0, 
+          211,  211,  211,    0,  211,    0,  208,    0,    0,    0, 
+            0,    0,    0,    0,  211,  211,    0,    0,    0,    0, 
+            0,    0,    0,  211,  211,  205,  211,  211,  211,  211, 
+          211,    0,    0,    0,    0,    0,    0,    0,  211,  205, 
+          205,    0,    0,    0,  205,    0,    0,    0,    0,    0, 
+            0,  211,  211,  211,  211,  211,  211,  211,  211,  211, 
+          211,    0,  211,  211,    0,    0,  211,  211,    0,    0, 
+            0,  211,    0,    0,  211,    0,    0,  211,    0,  211, 
+            0,  211,    0,  211,    0,  211,  211,  211,  211,  211, 
+          211,  211,    0,  211,  206,  211,    0,    0,    0,    0, 
+            0,    0,    0,    0,  206,    0,    0,  211,    0,    0, 
+            0,  212,  212,  212,    0,    0,  212,  212,  212,    0, 
+          212,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          212,  212,    0,    0,    0,    0,    0,    0,  206,  212, 
+          212,    0,  212,  212,  212,  212,  212,    0,    0,    0, 
+            0,    0,  206,  206,  212,    0,    0,  206,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  212,  212,  212, 
+          212,  212,  212,  212,  212,  212,  212,    0,  212,  212, 
+            0,    0,  212,  212,    0,    0,    0,  212,    0,    0, 
+          212,    0,    0,  212,    0,  212,    0,  212,  230,  212, 
+            0,  212,  212,  212,  212,  212,  212,  212,  230,  212, 
+            0,  212,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  212,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  230,  205,  205,  205,    0,    0,  205,  205, 
+          205,    0,  205,    0,    0,    0,  230,  230,    0,    0, 
+            0,  230,  205,  205,    0,    0,    0,    0,    0,    0, 
+            0,  205,  205,    0,  205,  205,  205,  205,  205,    0, 
+            0,    0,    0,    0,    0,    0,  205,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  205, 
+          205,  205,  205,  205,  205,  205,  205,  205,  205,    0, 
+          205,  205,    0,    0,  205,  205,    0,    0,    0,  205, 
+          231,    0,  205,    0,    0,  205,    0,  205,    0,    0, 
+          231,  205,    0,    0,    0,  205,  205,  205,  205,  205, 
+            0,  205,    0,  205,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  205,  206,  206,  206,    0, 
+            0,  206,  206,  206,  231,  206,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  206,  206,    0,  231,  231, 
+            0,    0,    0,  231,  206,  206,    0,  206,  206,  206, 
+          206,  206,    0,    0,    0,    0,    0,    0,    0,  206, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  206,  206,  206,  206,  206,  206,  206,  206, 
+          206,  206,    0,  206,  206,    0,    0,  206,  206,    0, 
+            0,    0,  206,    0,    0,  206,    0,    0,  206,    0, 
+          206,    0,    0,  217,  206,    0,    0,    0,  206,  206, 
+          206,  206,  206,  217,  206,    0,  206,    0,    0,    0, 
+          230,  230,  230,    0,    0,  230,  230,  230,  206,  230, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  230, 
+          230,    0,    0,    0,    0,    0,    0,  217,  230,  230, 
+            0,  230,  230,  230,  230,  230,    0,    0,    0,    0, 
+            0,  217,  217,  230,    0,    0,  217,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  230,  230,  230,  230, 
+          230,  230,  230,  230,  230,  230,    0,  230,  230,    0, 
+            0,  230,  230,    0,    0,    0,  230,    0,    0,  230, 
+            0,    0,  230,    0,  230,    0,    0,    0,  230,    0, 
+            0,    0,    0,    0,  230,  230,  230,    0,  230,    0, 
+          230,  215,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  215,  230,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  231,  231,  231,    0,    0,  231,  231,  231, 
+            0,  231,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  231,  231,    0,    0,  215,    0,    0,    0,    0, 
+          231,  231,    0,  231,  231,  231,  231,  231,    0,  215, 
+          215,    0,    0,    0,  215,  231,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  231,  231, 
+          231,  231,  231,  231,  231,  231,  231,  231,    0,  231, 
+          231,    0,    0,  231,  231,    0,    0,    0,  231,    0, 
+            0,  231,    0,    0,  231,    0,  231,    0,    0,  216, 
+          231,    0,    0,    0,    0,    0,  231,  231,  231,  216, 
+          231,    0,  231,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  231,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  217,  217,  217,    0,    0, 
+          217,  217,  217,  216,  217,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  217,  217,    0,  216,  216,    0, 
+            0,    0,  216,  217,  217,    0,  217,  217,  217,  217, 
+          217,    0,    0,    0,    0,    0,    0,    0,  217,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  217,  217,  217,  217,  217,  217,  217,  217,  217, 
+          217,    0,  217,  217,    0,    0,    0,    0,  220,    0, 
+            0,  217,    0,    0,  217,    0,    0,  217,  220,  217, 
+            0,    0,    0,  217,    0,    0,    0,    0,    0,  217, 
+          217,  217,    0,  217,    0,  217,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  217,    0,    0, 
+            0,    0,  220,  215,  215,  215,    0,    0,  215,  215, 
+          215,    0,  215,    0,    0,    0,  220,  220,    0,    0, 
+            0,  220,  215,  215,    0,    0,    0,    0,    0,    0, 
+            0,  215,  215,    0,  215,  215,  215,  215,  215,    0, 
+            0,    0,    0,    0,    0,    0,  215,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,  215, 
-          215,  215,    0,    0,  215,  215,  215,  214,  215,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  215,  215, 
-            0,  214,  214,    0,    0,    0,  214,  215,  215,    0, 
-          215,  215,  215,  215,  215,    0,    0,    0,    0,    0, 
-            0,    0,  215,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  215,  215,  215,  215,  215, 
-          215,  215,  215,  215,  215,    0,  215,  215,    0,    0, 
-            0,    0,  218,    0,    0,  215,    0,    0,  215,    0, 
-            0,  215,  218,  215,    0,    0,    0,  215,    0,    0, 
-            0,    0,    0,  215,  215,  215,    0,  215,    0,  215, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  215,    0,    0,    0,    0,  218,  213,  213,  213, 
-            0,    0,  213,  213,  213,    0,  213,    0,    0,    0, 
-          218,  218,    0,    0,    0,  218,  213,  213,    0,    0, 
-            0,    0,    0,    0,    0,  213,  213,    0,  213,  213, 
-          213,  213,  213,    0,    0,    0,    0,    0,    0,    0, 
-          213,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  213,  213,  213,  213,  213,  213,  213, 
-          213,  213,  213,    0,  213,  213,    0,    0,    0,    0, 
-            0,    0,  220,  213,    0,    0,  213,    0,    0,  213, 
-            0,  213,  220,    0,    0,    0,    0,    0,    0,    0, 
-            0,  213,  213,  213,    0,  213,    0,  213,    0,    0, 
-            0,    0,    0,    0,    0,  214,  214,  214,    0,  213, 
-          214,  214,  214,    0,  214,    0,  220,    0,    0,    0, 
-            0,    0,    0,    0,  214,  214,    0,    0,    0,    0, 
-          220,  220,    0,  214,  214,  220,  214,  214,  214,  214, 
-          214,    0,    0,    0,    0,    0,    0,    0,  214,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  214,  214,  214,  214,  214,  214,  214,  214,  214, 
-          214,    0,  214,  214,    0,    0,    0,    0,    0,    0, 
-            0,  214,    0,    0,  214,    0,    0,  214,    0,  214, 
-            0,    0,  219,    0,    0,    0,    0,    0,    0,  214, 
-          214,  214,  219,  214,    0,  214,    0,    0,    0,    0, 
-            0,    0,    0,    0,  218,  218,  218,  214,    0,  218, 
-          218,  218,    0,  218,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  218,  218,    0,  219,    0,    0,    0, 
-            0,    0,  218,  218,    0,  218,  218,  218,  218,  218, 
-          219,  219,    0,    0,    0,  219,    0,  218,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          218,  218,  218,  218,  218,  218,  218,  218,  218,  218, 
-            0,  218,  218,    0,    0,    0,    0,    0,    0,  217, 
-          218,    0,    0,  218,    0,    0,  218,    0,  218,  217, 
-            0,    0,    0,    0,    0,    0,    0,    0,  218,  218, 
-            0,    0,    0,    0,  218,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  218,    0,    0,    0, 
-            0,    0,    0,  217,  220,  220,  220,    0,    0,  220, 
-          220,  220,    0,  220,    0,    0,    0,  217,  217,    0, 
-            0,    0,  217,  220,  220,    0,    0,    0,    0,    0, 
-            0,    0,  220,  220,    0,  220,  220,  220,  220,  220, 
-            0,    0,    0,    0,    0,    0, 
+          215,  215,  215,  215,  215,  215,  215,  215,  215,    0, 
+          215,  215,    0,    0,    0,    0,    0,    0,  222,  215, 
+            0,    0,  215,    0,    0,  215,    0,  215,  222,    0, 
+            0,    0,    0,    0,    0,    0,    0,  215,  215,  215, 
+            0,  215,    0,  215,    0,    0,    0,    0,    0,    0, 
+            0,  216,  216,  216,    0,  215,  216,  216,  216,    0, 
+          216,    0,  222,    0,    0,    0,    0,    0,    0,    0, 
+          216,  216,    0,    0,    0,    0,  222,  222,    0,  216, 
+          216,  222,  216,  216,  216,  216,  216,    0,    0,    0, 
+            0,    0,    0,    0,  216,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  216,  216,  216, 
+          216,  216,  216,  216,  216,  216,  216,    0,  216,  216, 
+            0,    0,    0,    0,    0,    0,    0,  216,    0,    0, 
+          216,    0,    0,  216,    0,  216, 
       };
    }
 
    private static final short[] yyTable3() {
       return new short[] {
 
-            0,  220,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  220,  220,  220,  220,  220,  220, 
-          220,  220,  220,  220,    0,  220,  220,    0,    0,    0, 
-            0,  216,    0,    0,  220,    0,    0,  220,    0,    0, 
-          220,  216,  220,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  220,  220,    0,    0,    0,    0,  220,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          220,    0,    0,    0,    0,  216,    0,    0,  219,  219, 
-          219,    0,    0,  219,  219,  219,    0,  219,    0,  216, 
-          216,    0,    0,    0,  216,    0,    0,  219,  219,    0, 
-            0,    0,    0,    0,    0,    0,  219,  219,    0,  219, 
-          219,  219,  219,  219,    0,    0,    0,    0,    0,    0, 
-            0,  219,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  219,  219,  219,  219,  219,  219, 
-          219,  219,  219,  219,    0,  219,  219,    0,    0,    0, 
-            0,    0,    0,  221,  219,    0,    0,  219,    0,    0, 
-          219,    0,  219,  221,    0,    0,    0,    0,    0,    0, 
-            0,    0,  219,  219,    0,  217,  217,  217,  219,    0, 
-          217,  217,  217,    0,  217,    0,    0,    0,    0,    0, 
-          219,    0,    0,    0,  217,  217,    0,  221,    0,    0, 
-            0,    0,    0,  217,  217,    0,  217,  217,  217,  217, 
-          217,  221,  221,    0,    0,    0,  221,    0,  217,    0, 
+            0,    0,  221,    0,    0,    0,    0,    0,    0,  216, 
+          216,  216,  221,  216,    0,  216,    0,    0,    0,    0, 
+            0,    0,    0,    0,  220,  220,  220,  216,    0,  220, 
+          220,  220,    0,  220,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  220,  220,    0,  221,    0,    0,    0, 
+            0,    0,  220,  220,    0,  220,  220,  220,  220,  220, 
+          221,  221,    0,    0,    0,  221,    0,  220,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          220,  220,  220,  220,  220,  220,  220,  220,  220,  220, 
+            0,  220,  220,    0,    0,    0,    0,    0,    0,  219, 
+          220,    0,    0,  220,    0,    0,  220,    0,  220,  219, 
+            0,    0,    0,    0,    0,    0,    0,    0,  220,  220, 
+            0,    0,    0,    0,  220,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  220,    0,    0,    0, 
+            0,    0,    0,  219,  222,  222,  222,    0,    0,  222, 
+          222,  222,    0,  222,    0,    0,    0,  219,  219,    0, 
+            0,    0,  219,  222,  222,    0,    0,    0,    0,    0, 
+            0,    0,  222,  222,    0,  222,  222,  222,  222,  222, 
+            0,    0,    0,    0,    0,    0,    0,  222,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  217,  217,  217,  217,  217,  217,  217,  217,  217, 
-          217,    0,  217,  217,    0,    0,    0,    0,    0,    0, 
-            0,  217,    0,    0,  217,    0,    0,  217,    0,  217, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  217, 
-          217,  222,    0,    0,    0,  217,    0,    0,    0,    0, 
-            0,  222,    0,    0,    0,    0,    0,  217,    0,    0, 
-            0,    0,    0,  216,  216,  216,    0,    0,  216,  216, 
-          216,    0,  216,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  216,  216,    0,  222,    0,    0,    0,    0, 
-            0,  216,  216,    0,  216,  216,  216,  216,  216,  222, 
-          222,    0,    0,    0,  222,    0,  216,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  216,  216,    0,    0,    0, 
-          216,  216,    0,    0,    0,    0,    0,    0,  223,  216, 
-            0,    0,  216,    0,    0,  216,    0,  216,  223,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  216,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  216,    0,    0,    0,    0, 
-            0,    0,  223,    0,    0,  221,  221,  221,    0,    0, 
-          221,  221,  221,    0,  221,    0,  223,  223,    0,    0, 
-            0,  223,    0,    0,  221,  221,    0,    0,    0,    0, 
-            0,    0,    0,  221,  221,    0,  221,  221,  221,  221, 
-          221,    0,    0,    0,  230,    0,    0,    0,  221,    0, 
-            0,    0,    0,    0,  230,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  221,  221,    0, 
-            0,    0,  221,  221,    0,    0,    0,    0,    0,    0, 
-            0,  221,    0,    0,  221,    0,    0,  221,  230,  221, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  230,  230,    0,  221,    0,  230,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  221,    0,    0, 
-            0,    0,    0,  222,  222,  222,    0,    0,  222,  222, 
-          222,    0,  222,    0,  224,    0,    0,    0,    0,    0, 
-            0,    0,  222,  222,  224,    0,    0,    0,    0,    0, 
-            0,  222,  222,    0,  222,  222,  222,  222,  222,    0, 
+          222,  222,  222,  222,  222,  222,  222,  222,  222,  222, 
+            0,  222,  222,    0,    0,    0,    0,  218,    0,    0, 
+          222,    0,    0,  222,    0,    0,  222,  218,  222,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  222,  222, 
+            0,    0,    0,    0,  222,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,  222,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  224,    0, 
-            0,    0,    0,    0,    0,  222,  222,    0,    0,    0, 
-          222,  222,  224,  224,    0,    0,    0,  224,    0,  222, 
-            0,    0,  222,    0,    0,  222,    0,  222,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          223,  223,  223,  222,    0,  223,  223,  223,    0,  223, 
-            0,  225,    0,    0,    0,  222,    0,    0,    0,  223, 
-          223,  225,    0,    0,    0,    0,    0,    0,  223,  223, 
-            0,  223,  223,  223,  223,  223,    0,    0,    0,    0, 
-            0,    0,    0,  223,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  225,    0,    0,    0,    0, 
-            0,    0,  223,  223,    0,    0,    0,  223,  223,  225, 
-          225,    0,    0,    0,  225,    0,  223,    0,    0,  223, 
-            0,    0,  223,    0,  223,    0,  230,  230,  230,    0, 
-            0,  230,  230,  230,    0,  230,    0,  231,    0,    0, 
-          223,    0,    0,    0,    0,  230,  230,  231,    0,    0, 
-            0,    0,  223,    0,  230,  230,    0,  230,  230,  230, 
-          230,  230,    0,    0,    0,    0,    0,    0,    0,  230, 
-            0,    0,    0,    0,    0,   38,    0,    0,    0,    0, 
-            0,  231,    0,    0,    0,   38,    0,    0,  230,  230, 
-            0,    0,    0,  230,  230,  231,  231,    0,    0,    0, 
-          231,    0,  230,    0,    0,  230,    0,    0,  230,    0, 
-          230,    0,    0,    0,    0,    0,  224,  224,  224,  256, 
-            0,  224,  224,  224,    0,  224,  230,  202,    0,    0, 
-            0,    0,    0,    0,   38,  224,  224,  202,  230,    0, 
-            0,    0,    0,    0,  224,  224,    0,  224,  224,  224, 
-          224,  224,    0,    0,    0,    0,    0,    0,    0,  224, 
-            0,    0,    0,    0,    0,    0,    0,  232,    0,    0, 
-            0,  202,    0,    0,    0,    0,    0,  232,  224,  224, 
-            0,    0,    0,  224,  224,  202,  202,    0,    0,    0, 
-          202,    0,  224,    0,    0,  224,    0,    0,  224,    0, 
-          224,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  232,    0,  225,  225,  225,  224,    0,  225,  225, 
-          225,    0,  225,    0,    0,  232,  232,    0,  224,    0, 
-            0,    0,  225,  225,    0,    0,    0,    0,    0,    0, 
-            0,  225,  225,    0,  225,  225,  225,  225,  225,    0, 
-            0,    0,  200,    0,    0,    0,  225,    0,    0,    0, 
-            0,    0,  200,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  225,  225,    0,    0,    0, 
+            0,  218,    0,    0,  221,  221,  221,    0,    0,  221, 
+          221,  221,    0,  221,    0,  218,  218,    0,    0,    0, 
+          218,    0,    0,  221,  221,    0,    0,    0,    0,    0, 
+            0,    0,  221,  221,    0,  221,  221,  221,  221,  221, 
+            0,    0,    0,    0,    0,    0,    0,  221,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          221,  221,  221,  221,  221,  221,  221,  221,  221,  221, 
+            0,  221,  221,    0,    0,    0,    0,    0,    0,  223, 
+          221,    0,    0,  221,    0,    0,  221,    0,  221,  223, 
+            0,    0,    0,    0,    0,    0,    0,    0,  221,  221, 
+            0,  219,  219,  219,  221,    0,  219,  219,  219,    0, 
+          219,    0,    0,    0,    0,    0,  221,    0,    0,    0, 
+          219,  219,    0,  223,    0,    0,    0,    0,    0,  219, 
+          219,    0,  219,  219,  219,  219,  219,  223,  223,    0, 
+            0,    0,  223,    0,  219,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  219,  219,  219, 
+          219,  219,  219,  219,  219,  219,  219,    0,  219,  219, 
+            0,    0,    0,    0,    0,    0,    0,  219,    0,    0, 
+          219,    0,    0,  219,    0,  219,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  219,  219,  224,    0,    0, 
+            0,  219,    0,    0,    0,    0,    0,  224,    0,    0, 
+            0,    0,    0,  219,    0,    0,    0,    0,    0,  218, 
+          218,  218,    0,    0,  218,  218,  218,    0,  218,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  218,  218, 
+            0,  224,    0,    0,    0,    0,    0,  218,  218,    0, 
+          218,  218,  218,  218,  218,  224,  224,    0,    0,    0, 
+          224,    0,  218,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  218,  218,    0,    0,    0,  218,  218,    0,    0, 
+            0,    0,    0,    0,  225,  218,    0,    0,  218,    0, 
+            0,  218,    0,  218,  225,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  218, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  218,    0,    0,    0,    0,    0,    0,  225,    0, 
+            0,  223,  223,  223,    0,    0,  223,  223,  223,    0, 
+          223,    0,  225,  225,    0,    0,    0,  225,    0,    0, 
+          223,  223,    0,    0,    0,    0,    0,    0,    0,  223, 
+          223,    0,  223,  223,  223,  223,  223,    0,    0,    0, 
+          232,    0,    0,    0,  223,    0,    0,    0,    0,    0, 
+          232,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  223,  223,    0,    0,    0,  223,  223, 
+            0,    0,    0,    0,    0,    0,    0,  223,    0,    0, 
+          223,    0,    0,  223,  232,  223,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  232,  232, 
+            0,  223,    0,  232,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  223,    0,    0,    0,    0,    0,  224, 
+          224,  224,    0,    0,  224,  224,  224,    0,  224,    0, 
+          226,    0,    0,    0,    0,    0,    0,    0,  224,  224, 
+          226,    0,    0,    0,    0,    0,    0,  224,  224,    0, 
+          224,  224,  224,  224,  224,    0,    0,    0,    0,    0, 
+            0,    0,  224,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  226,    0,    0,    0,    0,    0, 
+            0,  224,  224,    0,    0,    0,  224,  224,  226,  226, 
+            0,    0,    0,  226,    0,  224,    0,    0,  224,    0, 
+            0,  224,    0,  224,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  225,  225,  225,  224, 
+            0,  225,  225,  225,    0,  225,    0,  227,    0,    0, 
+            0,  224,    0,    0,    0,  225,  225,  227,    0,    0, 
+            0,    0,    0,    0,  225,  225,    0,  225,  225,  225, 
           225,  225,    0,    0,    0,    0,    0,    0,    0,  225, 
-            0,    0,  225,    0,    0,  225,  200,  225,    0,  231, 
-          231,  231,    0,    0,  231,  231,  231,    0,  231,    0, 
-          200,  200,    0,  225,    0,    0,    0,    0,  231,  231, 
-            0,    0,    0,    0,    0,  225,    0,  231,  231,    0, 
-          231,  231,  231,  231,  231,    0,    0,   38,   38,   38, 
-            0,    0,  231,   38,   38,    0,   38,    0,  199,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  199,    0, 
-            0,    0,  231,    0,    0,    0,  231,  231,   38,   38, 
-           38,   38,   38,    0,    0,  231,    0,    0,  231,    0, 
-            0,  231,    0,  231,    0,    0,    0,    0,    0,  202, 
-          202,  202,  199,    0,  202,  202,  202,    0,  202,  231, 
-            0,    0,    0,    0,    0,    0,  199,  199,  202,  202, 
-            0,  231,    0,    0,    0,    0,   38,  202,  202,    0, 
-          202,  202,  202,  202,  202,    0,    0,    0,    0,  232, 
-          232,  232,  202,  195,  232,  232,  232,   38,  232,    0, 
-            0,    0,    0,  195,    0,    0,    0,    0,  232,  232, 
-            0,    0,    0,    0,    0,    0,    0,  232,  232,    0, 
-          232,  232,  232,  232,  232,  202,    0,    0,  202,    0, 
-            0,  202,  232,  202,    0,    0,    0,  195,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  202, 
-            0,  195,  195,    0,  196,    0,    0,    0,    0,    0, 
-            0,  202,    0,    0,  196,  232,    0,    0,  232,    0, 
-            0,  232,    0,  232,  200,  200,  200,    0,    0,  200, 
-          200,  200,    0,  200,    0,    0,    0,    0,    0,  232, 
-            0,    0,    0,  200,  200,    0,    0,    0,  196,    0, 
-            0,  232,  200,  200,    0,  200,  200,  200,  200,  200, 
-            0,    0,  196,  196,    0,    0,    0,  200,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          200,  197,    0,  200,    0,    0,  200,    0,  200,    0, 
-            0,  197,    0,    0,    0,    0,    0,    0,    0,    0, 
-          199,  199,  199,    0,  200,  199,  199,  199,    0,  199, 
-            0,    0,    0,    0,    0,    0,  200,    0,    0,  199, 
-          199,    0,    0,    0,    0,  197,    0,    0,  199,  199, 
-            0,  199,  199,  199,  199,  199,    0,    0,    0,  197, 
-          197,    0,    0,  199,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  198,    0,    0, 
-            0,    0,    0,    0,    0,    0,  199,  198,    0,  199, 
-            0,    0,  199,    0,  199,  195,  195,  195,    0,    0, 
-          195,  195,  195,    0,  195,    0,    0,    0,    0,    0, 
-          199,    0,    0,    0,  195,  195,    0,    0,    0,    0, 
-            0,  198,  199,  195,  195,    0,  195,  195,  195,  195, 
-          195,    0,    0,    0,    0,  198,  198,    0,  195,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  196,  196,  196,    0, 
-            0,  196,  196,  196,    0,  196,    0,    0,    0,    0, 
-            0,  195,    0,    0,  195,  196,  196,  195,    0,  195, 
-            0,    0,    0,    0,  196,  196,  191,  196,  196,  196, 
-          196,  196,    0,    0,    0,  195,  191,    0,    0,  196, 
-            0,    0,    0,    0,    0,    0,    0,  195,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          191,    0,  196,    0,    0,  196,    0,    0,  196,    0, 
-          196,    0,    0,    0,  191,  191,    0,  193,    0,    0, 
-            0,    0,    0,  197,  197,  197,  196,  193,  197,  197, 
-          197,    0,  197,    0,    0,    0,    0,    0,  196,    0, 
-            0,    0,  197,  197,    0,    0,    0,    0,    0,    0, 
-            0,  197,  197,    0,  197,  197,  197,  197,  197,    0, 
-            0,  193,    0,    0,    0,    0,  197,    0,    0,    0, 
-            0,    0,    0,    0,    0,  193,  193,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  197, 
-            0,    0,  197,  233,    0,  197,    0,  197,    0,  198, 
-          198,  198,    0,  233,  198,  198,  198,    0,  198,    0, 
-            0,    0,    0,  197,    0,    0,    0,    0,  198,  198, 
-            0,    0,    0,    0,    0,  197,    0,  198,  198,    0, 
-          198,  198,  198,  198,  198,    0,    0,  233,    0,    0, 
-            0,    0,  198,    0,    0,    0,    0,    0,    0,    0, 
-            0,  233,  233,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  198,    0,    0,  198,    0, 
-            0,  198,    0,  198,    0,    0,    0,    0,    0,    0, 
-          194,    0,    0,    0,    0,    0,    0,    0,    0,  198, 
-          194,    0,    0,    0,    0,    0,    0,    0,  191,  191, 
-          191,  198,    0,  191,  191,  191,    0,  191,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  191,  191,    0, 
-            0,    0,    0,    0,  194,    0,  191,  191,    0,  191, 
-          191,  191,  191,  191,    0,    0,    0,  261,  194,  194, 
-            0,  191,    0,    0,    0,    0,    0,  261,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  193, 
-          193,  193,    0,    0,  193,  193,  193,    0,  193,    0, 
-            0,    0,    0,    0,  191,    0,    0,  191,  193,  193, 
-          191,  259,  191,    0,    0,    0,    0,  193,  193,  235, 
-          193,  193,  193,  193,  193,    0,  261,    0,  191,  235, 
-            0,    0,  193,    0,    0,    0,    0,    0,    0,    0, 
-          191,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  235,    0,  193,    0,    0,  193,    0, 
-            0,  193,    0,  193,    0,  233,  233,  233,  235,    0, 
-          233,  233,  233,    0,  233,    0,    0,    0,    0,  193, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  227,    0,    0,    0,    0,    0,    0,  225,  225, 
+            0,    0,    0,  225,  225,  227,  227,    0,    0,    0, 
+          227,    0,  225,    0,    0,  225,    0,    0,  225,    0, 
+          225,    0,  232,  232,  232,    0,    0,  232,  232,  232, 
+            0,  232,    0,  233,    0,    0,  225,    0,    0,    0, 
+            0,  232,  232,  233,    0,    0,    0,    0,  225,    0, 
+          232,  232,    0,  232,  232,  232,  232,  232,    0,    0, 
+            0,    0,    0,    0,    0,  232,    0,    0,    0,    0, 
+            0,   38,    0,    0,    0,    0,    0,  233,    0,    0, 
+            0,   38,    0,    0,  232,  232,    0,    0,    0,  232, 
+          232,  233,  233,    0,    0,    0,  233,    0,  232,    0, 
+            0,  232,    0,    0,  232,    0,  232,    0,    0,    0, 
+            0,    0,  226,  226,  226,  258,    0,  226,  226,  226, 
+            0,  226,  232,  203,    0,    0,    0,    0,    0,    0, 
+           38,  226,  226,  203,  232,    0,    0,    0,    0,    0, 
+          226,  226,    0,  226,  226,  226,  226,  226,    0,    0, 
+            0,    0,    0,    0,    0,  226,    0,    0,    0,    0, 
+            0,    0,    0,  204,    0,    0,    0,  203,    0,    0, 
+            0,    0,    0,  204,  226,  226,    0,    0,    0,  226, 
+          226,  203,  203,    0,    0,    0,  203,    0,  226,    0, 
+            0,  226,    0,    0,  226,    0,  226,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  204,    0,  227, 
+          227,  227,  226,    0,  227,  227,  227,    0,  227,    0, 
+            0,  204,  204,    0,  226,    0,  204,    0,  227,  227, 
+            0,    0,    0,    0,    0,    0,    0,  227,  227,    0, 
+          227,  227,  227,  227,  227,    0,    0,    0,  202,    0, 
+            0,    0,  227,    0,    0,    0,    0,    0,  202,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  227,  227,    0,    0,    0,  227,  227,    0,    0, 
+            0,    0,    0,    0,    0,  227,    0,    0,  227,    0, 
+            0,  227,  202,  227,    0,  233,  233,  233,    0,    0, 
+          233,  233,  233,    0,  233,    0,  202,  202,    0,  227, 
             0,    0,    0,    0,  233,  233,    0,    0,    0,    0, 
-            0,  193,    0,  233,  233,  192,  233,  233,  233,  233, 
-          233,    0,    0,    0,    0,  192,    0,    0,  233,    0, 
-            0,   22,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,   22,    0,    0,    0,    0,    0,    0,    0,    0, 
-          190,    0,    0,    0,    0,    0,    0,    0,    0,  192, 
-          190,  233,    0,    0,  233,    0,    0,  233,    0,  233, 
-            0,    0,    0,  192,  192,    0,    0,    0,    0,    0, 
-            0,    0,  194,  194,  194,  233,    0,  194,  194,  194, 
-           22,  194,    0,    0,  190,    0,    0,  233,    0,    0, 
-            0,  194,  194,    0,    0,    0,    0,    0,  190,  190, 
-          194,  194,    0,  194,  194,  194,  194,  194,    0,    0, 
-            0,    0,    0,    0,    0,  194,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  261, 
-          261,  261,    0,    0,  261,  261,  261,    0,  261,    0, 
-            0,    0,    0,    0,    0,  102,    0,    0,  194,    0, 
-            0,  194,    0,    0,  194,    0,  194,    0,    0,    0, 
-          261,  261,  261,  261,  261,    0,    0,    0,    0,    0, 
-            0,    0,  194,    0,    0,    0,    0,    0,    0,    0, 
-            0,  235,  235,  235,  194,    0,  235,  235,  235,    0, 
-          235,    0,    0,    0,  102,    0,    0,    0,    0,    0, 
-          235,  235,    0,    0,    0,  261,    0,    0,  261,  235, 
-          235,    0,  235,  235,  235,  235,  235,    0,    0,    0, 
-            0,    0,    0,    0,  235,    0,  450,    0,    0,  261, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  235,    0,    0, 
-          235,    0,    0,  235,    0,  235,    0,  192,  192,  192, 
-            0,  103,  192,  192,  192,    0,  192,    0,    0,    0, 
-            0,  235,    0,   22,   22,   22,  192,  192,    0,   22, 
-           22,    0,   22,  235,    0,  192,  192,    0,  192,  192, 
-          192,  192,  190,  190,  190,    0,    0,  190,  190,  190, 
-          192,  190,    0,    0,   22,   22,   22,   22,   22,    0, 
-          103,  190,  190,    0,    0,    0,    0,    0,    0,    0, 
-          190,  190,    0,  190,  190,  190,  190,    0,    0,    0, 
-            0,    0,    0,  192,    0,  190,  192,    0,    0,  192, 
-            0,  192,  453,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,   22,    0,    0,    0,    0,  192,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  190,  192, 
-            0,  190,    0,   22,  190,    0,  190,    0,    0,    0, 
-            0,  102,  102,  102,  102,  102,  102,  102,  102,  102, 
-          102,  102,  190,  102,  102,  102,    0,  102,  102,  102, 
-          102,  102,  102,  102,  190,  521,    0,    0,  102,  102, 
-          102,  102,  102,  102,  102,    0,    0,  102,    0,    0, 
-            0,    0,    0,  102,  102,    0,  102,  102,  102,  102, 
-            0,  102,  102,  102,  102,  102,  102,    0,  102,  102, 
-          102,  102,  102,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  450,    0,    0,    0,    0,    0,    0, 
-          450,  102,    0,    0,  102,  521,    0,  102,  102,    0, 
-          102,    0,  102,    0,  528,    0,  102,    0,    0,    0, 
-            0,  102,    0,    0,  102,    0,  521,    0,    0,  102, 
-          102,  102,  102,  102,  102,    0,    0,    0,  102,    0, 
-          102,  102,    0,  102,  102,    0,    0,  103,  103,  103, 
-          103,  103,  103,  103,  103,  103,  103,  103,    0,  103, 
-          103,  103,    0,  103,  103,  103,  103,  103,  103,  103, 
-            0,  522,    0,    0,  103,  103,  103,  103,  103,  103, 
-          103,    0,    0,  103,    0,    0,    0,    0,    0,  103, 
-          103,    0,  103,  103,  103,  103,    0,  103,  103,  103, 
-          103,  103,  103,    0,  103,  103,  103,  103,  103,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  453, 
-            0,    0,    0,    0,    0,    0,  453,  103,    0,    0, 
-          103,  522,    0,  103,  103,    0,  103,    0,  103,    0, 
-          276,    0,  103,    0,    0,    0,    0,  103,    0,    0, 
-          103,    0,  522,    0,    0,  103,  103,  103,  103,  103, 
-          103,    0,    0,    0,  103,    0,  103,  103,    0,  103, 
-          103,    0,    0,    0,    0,    0,    0,    0,    0,  102, 
-          102,  102,  102,  102,  102,  102,  102,  102,  102,  102, 
-            0,  103,  102,  102,    0,  102,  102,  102,  102,  102, 
-          102,  102,    0,  528,    0,    0,  102,  102,  102,  102, 
-          102,  102,  102,    0,    0,  102,    0,    0,    0,    0, 
-            0,  102,  102,    0,  102,  102,  102,  102,    0,  102, 
-          102,  102,  102,  102,  102,    0,  102,  102,  102,  102, 
-          103,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  528,    0,    0,    0,    0,    0,    0,  528,  102, 
-            0,    0,  102,  524,    0,  102,  102,    0,  102,    0, 
-          102,    0,  277,    0,  102,    0,    0,    0,    0,  102, 
-            0,    0,  102,    0,  528,    0,    0,  102,  102,  102, 
-          102,  102,  102,    0,    0,    0,  102,    0,  102,  102, 
-            0,  102,  102,    0,    0,  103,  103,  103,  103,  103, 
-          103,  103,  103,  103,  103,  103,    0,  321,  103,  103, 
-            0,  103,  103,  103,  103,  103,  103,  103,    0,    0, 
-            0,    0,  103,  103,  103,  103,  103,  103,  103,    0, 
-            0,  103,    0,    0,    0,    0,    0,  103,  103,    0, 
-          103,  103,  103,  103,    0,  103,  103,  103,  103,  103, 
-          103,    0,  103,  103,  103,  103,  321,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  276,    0,    0, 
-            0,    0,    0,    0,  276,  103,    0,    0,  103,  525, 
-            0,  103,  103,    0,  103,    0,  103,    0,    0,    0, 
-          103,    0,    0,    0,    0,  103,    0,    0,  103,    0, 
-            0,    0,    0,  103,  103,  103,  103,  103,  103,    0, 
-            0,    0,  103,    0,  103,  103,    0,  103,  103,    0, 
-            0,    0,    0,    0,    0,    0,    0,  103,  103,  103, 
-          103,  103,  103,  103,  103,  103,  103,  103,    0,  546, 
-          103,  103,    0,  103,  103,  103,  103,  103,  103,  103, 
-            0,    0,    0,    0,  103,  103,  103,  103,  103,  103, 
-          103,    0,    0,  103,    0,    0,    0,    0,    0,  103, 
-          103,    0,  103,  103,  103,  103,    0,  103,  103,  103, 
-          103,  103,  103,    0,  103,  103,  103,  103,  546,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  277, 
-            0,    0,    0,    0,    0,    0,  277,  103,    0,    0, 
-          103,    0,    0,  103,  103,    0,  103,    0,  103,    0, 
-            0,    0,  103,    0,    0,    0,    0,  103,    0,    0, 
-          103,    0,    0,    0,    0,  103,  103,  103,  103,  103, 
-          103,    0,    0,    0,  103,    0,  103,  103,    0,  103, 
-          103,    0,    0,  321,  321,  321,  321,  321,  321,  321, 
-          321,  321,  321,  321,    0,  321,  321,  321,  321,  321, 
-          321,  321,  321,  321,  321,  321,  546,    0,    0,    0, 
-          321,  321,  321,  321,  321,  321,  321,    0,    0,  321, 
-            0,    0,    0,    0,    0,  321,  321,    0,  321,  321, 
-          321,  321,    0,  321,  321,  321,  321,  321,  321,    0, 
-          321,  321,  321,  321,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  546,    0,    0,    0,    0, 
-            0,    0,    0,  321,    0,    0,  321,    0,    0,  321, 
-          321,    0,  321,    0,  321,    0,    0,    0,  321,    0, 
-            0,    0,    0,    0,    0,    0,  321,    0,    0,    0, 
-            0,  321,  321,  321,  321,  321,  321,    0,    0,    0, 
-          321,    0,  321,  321,    0,  321,  321,    0,    0,    0, 
-            0,    0,    0,    0,    0,  546,  546,  546,  546,  546, 
-          546,    0,    0,  546,  546,  546,    0,    0,    0,  546, 
-          232,  546,  546,  546,  546,  546,  546,  546,    0,    0, 
-            0,    0,  546,  546,  546,  546,  546,  546,  546,    0, 
-            0,  546,    0,    0,    0,    0,    0,  546,  546,    0, 
-          546,  546,  546,  546,    0,  546,  546,  546,  546,  546, 
-          546,    0,  546,  546,  546,  546,    0,    0,    0,  231, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  546,    0,    0,  546,    0, 
-            0,  546,  546,    0,  546,    0,  546,    0,    0,    0, 
-          546,    0,    0,    0,    0,    0,    0,    0,  546,    0, 
-            0,    0,    0,  546,  546,  546,  546,  546,  546,    0, 
-            0,    0,  546,    0,  546,  546,    0,  546,  546,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  546,  546,  546,  546,  546,  546,    0,    0, 
-            0,  546,  546,    0,    0,    0,  546,    0,  546,  546, 
-          546,  546,  546,  546,  546,    0,    0,  190,    0,  546, 
-          546,  546,  546,  546,  546,  546,    0,  190,  546,    0, 
-            0,    0,    0,    0,  546,  546,    0,  546,  546,  546, 
-          546,    0,  546,  546,  546,  546,  546,  546,    0,  546, 
-          546,  546,  546,    0,    0,    0,    0,    0,    0,    0, 
-            0,  235,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  546,    0,    0,  546,  190,    0,  546,  546, 
-            0,  546,    0,  546,    0,    0,    0,  546,    0,    0, 
-            0,    0,    0,    0,    0,  546,    0,  390,  546,    0, 
-          546,  546,  546,  546,  546,  546,    0,  390,    0,  546, 
-            0,  546,  546,    0,  546,  546,    0,    4,    5,    6, 
-            0,    8,    0,    0,    0,    9,   10,    0,    0,    0, 
-           11,    0,   12,   13,   14,   15,   16,   17,   18,    0, 
-            0,   87,    0,   19,   20,   21,   22,   23,   24,   25, 
-            0,    0,   26,    0,    0,    0,  390,    0,   95,   28, 
-          390,    0,   31,   32,   33,    0,   34,   35,   36,   37, 
-           38,   39,    0,   40,   41,   42,   43,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  390,    0, 
-            0,    0,    0,    0,    0,    0,  222,    0,  316,  112, 
-            0,    0,   46,   47,    0,   48,    0,    0,  316,    0, 
-            0,   50,    0,    0,    0,    0,    0,    0,    0,   51, 
-            0,    0,    0,    0,   52,   53,   54,   55,   56,   57, 
-            0,    0,    0,   58,    0,   59,   60,    0,   61,   62, 
-            0,    0,  316,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  316,    0,    0, 
-            0,  316,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  190, 
-          190,  190,    0,    0,    0,  190,  190,    0,  190,  316, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  317,    0,    0,  190,  190,    0, 
-          190,  190,  190,  190,  317,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  317,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  190,  390, 
-          390,  390,    0,  317,    0,  390,  390,  317,  390,    0, 
-            0,    0,    0,    0,    0,    0,    0,  390,    0,  190, 
-            0,    0,    0,    0,    0,    0,    0,  390,  390,    0, 
-          390,  390,  390,  390,  390,  317,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  390,  390,  390,  390,  390,  390, 
-          390,  390,  390,  390,  390,  390,  390,  390,    0,    0, 
-          390,  390,  390,    0,    0,    0,    0,    0,  390,    0, 
-            0,    0,    0,    0,    0,  390,  280,  390,    0,  390, 
-          390,  390,  390,  390,  390,  390,  280,  390,  390,  390, 
-          316,  316,  316,    0,    0,  316,  316,  316,    0,  316, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  316, 
-            0,    0,    0,    0,    0,    0,    0,    0,  316,  316, 
-          280,  316,  316,  316,  316,  316,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  280,    0,    0,    0,  280, 
-            0,    0,    0,    0,    0,  316,  316,  316,  316,  316, 
-          316,  316,  316,  316,  316,  316,  316,  316,  316,    0, 
-            0,  316,  316,  316,    0,    0,    0,  280,    0,  316, 
-            0,    0,    0,    0,    0,    0,  316,    0,  316,  462, 
-          316,  316,  316,  316,  316,  316,  316,    0,  316,  462, 
-          316,    0,    0,    0,    0,    0,  317,  317,  317,    0, 
-            0,  317,  317,  317,    0,  317,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  317,    0,    0,    0,    0, 
-            0,    0,    0,   86,  317,  317,    0,  317,  317,  317, 
-          317,  317,    0,    0,    0,    0,    0,    0,  462,    0, 
-           94,    0,  462,    0,    0,    0,    0,    0,    0,    0, 
-            0,  317,  317,  317,  317,  317,  317,  317,  317,  317, 
-          317,  317,  317,  317,  317,    0,    0,  317,  317,  317, 
-          462,    0,    0,    0,    0,  317,    0,    0,    0,    0, 
-            0,    0,  317,    0,  317,    0,  317,  317,  317,  317, 
-          317,  317,  317,  270,  317,    0,  317,    0,    0,    0, 
-            0,    0,    0,  270,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,   93,  280,  280, 
-          280,    0,    0,  280,  280,  280,    0,  280,    0,    0, 
-            0,    0,  270,    0,  101,    0,  270,  280,    0,    0, 
-            0,    0,    0,    0,    0,    0,  280,  280,    0,  280, 
-          280,  280,  280,  280,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  270,    0,    0,    0,    0,    0, 
-            0,    0,    0,  280,  280,  280,  280,  280,  280,  280, 
-          280,  280,  280,  280,  280,  280,  280,    0,    0,  280, 
-          280,  280,    0,    0,    0,    0,    0,  280,    0,    0, 
-            0,    0,    0,    0,  280,    0,  280,  277,  280,  280, 
-          280,  280,  280,  280,  280,    0,  280,  277,  280,    0, 
-            0,  462,  462,  462,    0,    0,    0,  462,  462,    0, 
-          462,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  462, 
-          462,   92,  462,  462,  462,  462,  462,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  277,    0,  100,    0, 
-          277,    0,    0,    0,    0,    0,  462,  462,  462,  462, 
-          462,  462,  462,  462,  462,  462,  462,  462,  462,  462, 
-            0,    0,  462,  462,  462,    0,  463,    0,  277,    0, 
-          462,    0,    0,    0,    0,    0,    0,  462,    0,  462, 
-            0,  462,  462,  462,  462,  462,  462,  462,  256,  462, 
-            0,  462,    0,    0,    0,    0,    0,    0,  256,    0, 
-            0,    0,    0,    0,    0,  270,  270,  270,    0,    0, 
-            0,  270,  270,    0,  270,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  258,  256,  270,  270,    0,  270,  270,  270,  270, 
-          270,  258,    0,    0,    0,    0,    0,  256,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          270,  270,  270,  270,  270,  270,  270,  270,  270,  270, 
-          270,  270,  270,  270,    0,  258,  270,  270,  270,    0, 
-            0,    0,    0,    0,  270,    0,    0,    0,    0,    0, 
-          258,  270,  254,  270,    0,  270,  270,  270,  270,  270, 
-          270,  270,  254,  270,    0,  270,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,   60,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,   60,  277, 
-          277,  277,    0,    0,    0,  277,  277,    0,  277,    0, 
-            0,  254,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  277,  277,    0, 
-          277,  277,  277,  277,  277,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,   60,    0,    0, 
-            0,    0,    0,    0,  277,  277,  277,  277,  277,  277, 
-          277,  277,  277,  277,  277,  277,  277,  277,    0,    0, 
-          277,  277,  277,   62,    0,    0,    0,    0,  277,    0, 
-            0,    0,    0,   62,    0,  277,    0,  277,    0,  277, 
-          277,  277,  277,  277,  277,  277,    0,  277,    0,  277, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          256,  256,  256,    0,    0,  256,  256,  256,    0,  256, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  256, 
-          256,    0,   62,    0,    0,    0,    0,   53,  256,  256, 
-            0,  256,  256,  256,  256,  256,    0,   53,    0,    0, 
-            0,    0,    0,  258,  258,  258,    0,    0,  258,  258, 
-          258,    0,  258,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  258,  258,    0,    0,    0,    0,    0,    0, 
-            0,  258,  258,    0,  258,  258,  258,  258,  258,  256, 
-            0,    0,  256,    0,  256,    0,   53,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          256,    0,    0,    0,  254,  254,  254,    0,    0,  254, 
-          254,  254,    0,  254,  578,    0,    0,    0,    0,    0, 
-            0,    0,  258,  254,  254,  258,    0,  258,    0,    0, 
-            0,    0,  254,  254,    0,  254,  254,  254,  254,  254, 
-           60,   60,   60,  258,    0,   60,   60,   60,    0,   60, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,   60, 
-           60,    0,    0,    0,    0,    0,    0,    0,   60,   60, 
-            0,   60,   60,   60,   60,   60,    0,    0,    0,    0, 
-            0,    0,    0,  254,    0,    0,  254,    0,  254,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  254,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,   60, 
-            0,    0,   60,    0,    0,   62,   62,   62,    0,    0, 
-           62,   62,   62,    0,   62,    0,    0,    0,    0,    0, 
-           60,    0,    0,    0,   62,   62,    0,    0,    0,    0, 
-            0,    0,  415,   62,   62,    0,   62,   62,   62,   62, 
-           62,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,   53, 
-           53,   53,    0,    0,   53,   53,   53,    0,   53,    0, 
-            0,    0,    0,    0,   62,    0,    0,   62,   53,    0, 
-            0,    0,    0,    0,    0,    0,    0,   53,   53,    0, 
-           53,   53,   53,   53,   53,   62,    0,    4,    5,    6, 
-            0,    8,    0,    0,    0,    9,   10,    0,    0,    0, 
-           11,    0,   12,   13,   14,  100,  101,   17,   18,    0, 
-            0,    0,    0,  102,  103,  104,   22,   23,   24,   25, 
-            0,    0,  105,    0,    0,    0,    0,    0,   53,  106, 
-            0,    0,   31,   32,   33,  415,   34,   35,   36,   37, 
-           38,   39,    0,   40,    0,    0,  109,    0,    0,   53, 
+            0,  227,    0,  233,  233,    0,  233,  233,  233,  233, 
+          233,    0,    0,   38,   38,   38,    0,    0,  233,   38, 
+           38,    0,   38,    0,  201,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  201,    0,    0,    0,  233,    0, 
+            0,    0,  233,  233,   38,   38,   38,   38,   38,    0, 
+            0,  233,    0,    0,  233,    0,    0,  233,    0,  233, 
+            0,    0,    0,    0,    0,  203,  203,  203,  201,    0, 
+          203,  203,  203,    0,  203,  233,    0,    0,    0,    0, 
+            0,    0,  201,  201,  203,  203,    0,  233,    0,    0, 
+            0,    0,   38,  203,  203,    0,  203,  203,  203,  203, 
+          203,    0,    0,    0,    0,  204,  204,  204,  203,  197, 
+          204,  204,  204,   38,  204,    0,    0,    0,    0,  197, 
+            0,    0,    0,    0,  204,  204,    0,    0,    0,    0, 
+            0,    0,    0,  204,  204,    0,  204,  204,  204,  204, 
+          204,  203,    0,    0,  203,    0,    0,  203,  204,  203, 
+            0,    0,    0,  197,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  203,    0,  197,  197,    0, 
+          198,    0,    0,    0,    0,    0,    0,  203,    0,    0, 
+          198,  204,    0,    0,  204,    0,    0,  204,    0,  204, 
+          202,  202,  202,    0,    0,  202,  202,  202,    0,  202, 
+            0,    0,    0,    0,    0,  204,    0,    0,    0,  202, 
+          202,    0,    0,    0,  198,    0,    0,  204,  202,  202, 
+            0,  202,  202,  202,  202,  202,    0,    0,  198,  198, 
+            0,    0,    0,  202,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  202,  199,    0,  202, 
+            0,    0,  202,    0,  202,    0,    0,  199,    0,    0, 
+            0,    0,    0,    0,    0,    0,  201,  201,  201,    0, 
+          202,  201,  201,  201,    0,  201,    0,    0,    0,    0, 
+            0,    0,  202,    0,    0,  201,  201,    0,    0,    0, 
+            0,  199,    0,    0,  201,  201,    0,  201,  201,  201, 
+          201,  201,    0,    0,    0,  199,  199,    0,    0,  201, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  200,    0,    0,    0,    0,    0,    0, 
+            0,    0,  201,  200,    0,  201,    0,    0,  201,    0, 
+          201,  197,  197,  197,    0,    0,  197,  197,  197,    0, 
+          197,    0,    0,    0,    0,    0,  201,    0,    0,    0, 
+          197,  197,    0,    0,    0,    0,    0,  200,  201,  197, 
+          197,    0,  197,  197,  197,  197,  197,    0,    0,    0, 
+            0,  200,  200,    0,  197,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  198,  198,  198,    0,    0,  198,  198,  198, 
+            0,  198,    0,    0,    0,    0,    0,  197,    0,    0, 
+          197,  198,  198,  197,    0,  197,    0,    0,    0,    0, 
+          198,  198,  193,  198,  198,  198,  198,  198,    0,    0, 
+            0,  197,  193,    0,    0,  198,    0,    0,    0,    0, 
+            0,    0,    0,  197,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  193,    0,  198,    0, 
+            0,  198,    0,    0,  198,    0,  198,    0,    0,    0, 
+          193,  193,    0,  195,    0,    0,    0,    0,    0,  199, 
+          199,  199,  198,  195,  199,  199,  199,    0,  199,    0, 
+            0,    0,    0,    0,  198,    0,    0,    0,  199,  199, 
+            0,    0,    0,    0,    0,    0,    0,  199,  199,    0, 
+          199,  199,  199,  199,  199,    0,    0,  195,    0,    0, 
+            0,    0,  199,    0,    0,    0,    0,    0,    0,    0, 
+            0,  195,  195,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  199,    0,    0,  199,  235, 
+            0,  199,    0,  199,    0,  200,  200,  200,    0,  235, 
+          200,  200,  200,    0,  200,    0,    0,    0,    0,  199, 
+            0,    0,    0,    0,  200,  200,    0,    0,    0,    0, 
+            0,  199,    0,  200,  200,    0,  200,  200,  200,  200, 
+          200,    0,    0,  235,    0,    0,    0,    0,  200,    0, 
+            0,    0,    0,    0,    0,    0,    0,  235,  235,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  200,    0,    0,  200,    0,    0,  200,    0,  200, 
+            0,    0,    0,    0,    0,    0,  196,    0,    0,    0, 
+            0,    0,    0,    0,    0,  200,  196,    0,    0,    0, 
+            0,    0,    0,    0,  193,  193,  193,  200,    0,  193, 
+          193,  193,    0,  193,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  193,  193,    0,    0,    0,    0,    0, 
+          196,    0,  193,  193,    0,  193,  193,  193,  193,  193, 
+            0,    0,    0,  264,  196,  196,    0,  193,    0,    0, 
+            0,    0,    0,  264,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  195,  195,  195,    0,    0, 
+          195,  195,  195,    0,  195,    0,    0,    0,    0,    0, 
+          193,    0,    0,  193,  195,  195,  193,  259,  193,    0, 
+            0,    0,    0,  195,  195,  237,  195,  195,  195,  195, 
+          195,    0,  264,    0,  193,  237,    0,    0,  195,    0, 
+            0,    0,    0,    0,    0,    0,  193,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  237, 
+            0,  195,    0,    0,  195,    0,    0,  195,    0,  195, 
+            0,  235,  235,  235,  237,    0,  235,  235,  235,    0, 
+          235,    0,    0,    0,    0,  195,    0,    0,    0,    0, 
+          235,  235,    0,    0,    0,    0,    0,  195,    0,  235, 
+          235,  194,  235,  235,  235,  235,  235,    0,    0,    0, 
+            0,  194,    0,    0,  235,    0,    0,  262,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  262,    0,    0, 
+            0,    0,    0,    0,    0,    0,  192,    0,    0,    0, 
+            0,    0,    0,    0,    0,  194,  192,  235,    0,    0, 
+          235,    0,    0,  235,    0,  235,    0,    0,    0,  194, 
+          194,  260,    0,    0,    0,    0,    0,    0,  196,  196, 
+          196,  235,    0,  196,  196,  196,  262,  196,    0,    0, 
+          192,    0,    0,  235,    0,    0,    0,  196,  196,    0, 
+            0,    0,    0,    0,  192,  192,  196,  196,    0,  196, 
+          196,  196,  196,  196,    0,    0,    0,    0,    0,    0, 
+            0,  196,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  264,  264,  264,    0,    0, 
+          264,  264,  264,    0,  264,    0,    0,    0,    0,    0, 
+            0,  104,    0,    0,  196,    0,    0,  196,    0,    0, 
+          196,    0,  196,    0,    0,    0,  264,  264,  264,  264, 
+          264,    0,    0,    0,    0,    0,    0,    0,  196,    0, 
+            0,    0,    0,    0,    0,    0,    0,  237,  237,  237, 
+          196,    0,  237,  237,  237,    0,  237,    0,    0,    0, 
+          104,    0,    0,    0,    0,    0,  237,  237,    0,    0, 
+            0,  264,    0,    0,  264,  237,  237,    0,  237,  237, 
+          237,  237,  237,    0,    0,    0,    0,    0,    0,    0, 
+          237,    0,  452,    0,    0,  264,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  237,    0,    0,  237,    0,    0,  237, 
+            0,  237,    0,  194,  194,  194,    0,  105,  194,  194, 
+          194,    0,  194,    0,    0,    0,    0,  237,    0,  262, 
+          262,  262,  194,  194,  262,  262,  262,    0,  262,  237, 
+            0,  194,  194,    0,  194,  194,  194,  194,  192,  192, 
+          192,    0,    0,  192,  192,  192,  194,  192,    0,    0, 
+          262,  262,  262,  262,  262,    0,  105,  192,  192,    0, 
+            0,    0,    0,    0,    0,    0,  192,  192,    0,  192, 
+          192,  192,  192,    0,    0,    0,    0,    0,    0,  194, 
+            0,  192,  194,    0,    0,  194,    0,  194,  455,    0, 
+            0,    0,    0,    0,    0,  262,    0,    0,  262,    0, 
+            0,    0,    0,  194,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  192,  194,    0,  192,    0,  262, 
+          192,    0,  192,    0,    0,    0,    0,  104,  104,  104, 
+          104,  104,  104,  104,  104,  104,  104,  104,  192,  104, 
+          104,  104,    0,  104,  104,  104,  104,  104,  104,  104, 
+          192,  523,    0,    0,  104,  104,  104,  104,  104,  104, 
+          104,    0,    0,  104,    0,    0,    0,    0,    0,  104, 
+          104,    0,  104,  104,  104,  104,    0,  104,  104,  104, 
+          104,  104,  104,    0,  104,  104,  104,  104,  104,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  452, 
+            0,    0,    0,    0,    0,    0,  452,  104,    0,    0, 
+          104,  523,    0,  104,  104,    0,  104,    0,  104,    0, 
+          530,    0,  104,    0,    0,    0,    0,  104,    0,    0, 
+          104,    0,  523,    0,    0,  104,  104,  104,  104,  104, 
+          104,    0,    0,    0,  104,    0,  104,  104,    0,  104, 
+          104,    0,    0,  105,  105,  105,  105,  105,  105,  105, 
+          105,  105,  105,  105,    0,  105,  105,  105,    0,  105, 
+          105,  105,  105,  105,  105,  105,    0,  524,    0,    0, 
+          105,  105,  105,  105,  105,  105,  105,    0,    0,  105, 
+            0,    0,    0,    0,    0,  105,  105,    0,  105,  105, 
+          105,  105,    0,  105,  105,  105,  105,  105,  105,    0, 
+          105,  105,  105,  105,  105,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  455,    0,    0,    0,    0, 
+            0,    0,  455,  105,    0,    0,  105,  524,    0,  105, 
+          105,    0,  105,    0,  105,    0,  278,    0,  105,    0, 
+            0,    0,    0,  105,    0,    0,  105,    0,  524,    0, 
+            0,  105,  105,  105,  105,  105,  105,    0,    0,    0, 
+          105,    0,  105,  105,    0,  105,  105,    0,    0,    0, 
+            0,    0,    0,    0,    0,  104,  104,  104,  104,  104, 
+          104,  104,  104,  104,  104,  104,    0,  105,  104,  104, 
+            0,  104,  104,  104,  104,  104,  104,  104,    0,  530, 
+            0,    0,  104,  104,  104,  104,  104,  104,  104,    0, 
+            0,  104,    0,    0,    0,    0,    0,  104,  104,    0, 
+          104,  104,  104,  104,    0,  104,  104,  104,  104,  104, 
+          104,    0,  104,  104,  104,  104,  105,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  530,    0,    0, 
+            0,    0,    0,    0,  530,  104,    0,    0,  104,  526, 
+            0,  104,  104,    0,  104,    0,  104,    0,  279,    0, 
+          104,    0,    0,    0,    0,  104,    0,    0,  104,    0, 
+          530,    0,    0,  104,  104,  104,  104,  104,  104,    0, 
+            0,    0,  104,    0,  104,  104,    0,  104,  104,    0, 
+            0,  105,  105,  105,  105,  105,  105,  105,  105,  105, 
+          105,  105,    0,  323,  105,  105,    0,  105,  105,  105, 
+          105,  105,  105,  105,    0,    0,    0,    0,  105,  105, 
+          105,  105,  105,  105,  105,    0,    0,  105,    0,    0, 
+            0,    0,    0,  105,  105,    0,  105,  105,  105,  105, 
+            0,  105,  105,  105,  105,  105,  105,    0,  105,  105, 
+          105,  105,  323,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  278,    0,    0,    0,    0,    0,    0, 
+          278,  105,    0,    0,  105,  527,    0,  105,  105,    0, 
+          105,    0,  105,    0,    0,    0,  105,    0,    0,    0, 
+            0,  105,    0,    0,  105,    0,    0,    0,    0,  105, 
+          105,  105,  105,  105,  105,    0,    0,    0,  105,    0, 
+          105,  105,    0,  105,  105,    0,    0,    0,    0,    0, 
+            0,    0,    0,  105,  105,  105,  105,  105,  105,  105, 
+          105,  105,  105,  105,    0,  548,  105,  105,    0,  105, 
+          105,  105,  105,  105,  105,  105,    0,    0,    0,    0, 
+          105,  105,  105,  105,  105,  105,  105,    0,    0,  105, 
+            0,    0,    0,    0,    0,  105,  105,    0,  105,  105, 
+          105,  105,    0,  105,  105,  105,  105,  105,  105,    0, 
+          105,  105,  105,  105,  548,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  279,    0,    0,    0,    0, 
+            0,    0,  279,  105,    0,    0,  105,    0,    0,  105, 
+          105,    0,  105,    0,  105,    0,    0,    0,  105,    0, 
+            0,    0,    0,  105,    0,    0,  105,    0,    0,    0, 
+            0,  105,  105,  105,  105,  105,  105,    0,    0,    0, 
+          105,    0,  105,  105,    0,  105,  105,    0,    0,  323, 
+          323,  323,  323,  323,  323,  323,  323,  323,  323,  323, 
+            0,  323,  323,  323,  323,  323,  323,  323,  323,  323, 
+          323,  323,  548,    0,    0,    0,  323,  323,  323,  323, 
+          323,  323,  323,    0,    0,  323,    0,    0,    0,    0, 
+            0,  323,  323,    0,  323,  323,  323,  323,    0,  323, 
+          323,  323,  323,  323,  323,    0,  323,  323,  323,  323, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  548,    0,    0,    0,    0,    0,    0,    0,  323, 
+            0,    0,  323,    0,    0,  323,  323,    0,  323,    0, 
+          323,    0,    0,    0,  323,    0,    0,    0,    0,    0, 
+            0,    0,  323,    0,    0,    0,    0,  323,  323,  323, 
+          323,  323,  323,    0,    0,    0,  323,    0,  323,  323, 
+            0,  323,  323,    0,    0,    0,    0,    0,    0,    0, 
+            0,  548,  548,  548,  548,  548,  548,    0,    0,  548, 
+          548,  548,    0,    0,    0,  548,  233,  548,  548,  548, 
+          548,  548,  548,  548,    0,    0,    0,    0,  548,  548, 
+          548,  548,  548,  548,  548,    0,    0,  548,    0,    0, 
+            0,    0,    0,  548,  548,    0,  548,  548,  548,  548, 
+            0,  548,  548,  548,  548,  548,  548,    0,  548,  548, 
+          548,  548,    0,    0,    0,  232,    0,   22,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,   22,    0,    0, 
+            0,  548,    0,    0,  548,    0,    0,  548,  548,    0, 
+          548,    0,  548,    0,    0,    0,  548,    0,    0,    0, 
+            0,    0,    0,    0,  548,    0,    0,    0,    0,  548, 
+          548,  548,  548,  548,  548,    0,    0,    0,  548,    0, 
+          548,  548,    0,  548,  548,    0,   22,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  548,  548, 
+          548,  548,  548,  548,    0,    0,    0,  548,  548,    0, 
+            0,    0,  548,    0,  548,  548,  548,  548,  548,  548, 
+          548,    0,    0,    0,    0,  548,  548,  548,  548,  548, 
+          548,  548,    0,    0,  548,    0,    0,    0,    0,    0, 
+          548,  548,    0,  548,  548,  548,  548,    0,  548,  548, 
+          548,  548,  548,  548,    0,  548,  548,  548,  548,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  548,    0, 
+            0,  548,    0,    0,  548,  548,    0,  548,    0,  548, 
+            0,    0,    0,  548,    0,    0,    0,    0,    0,    0, 
+            0,  548,    0,  392,  548,    0,  548,  548,  548,  548, 
+          548,  548,    0,  392,    0,  548,    0,  548,  548,    0, 
+          548,  548,    0,    4,    5,    6,    0,    8,    0,    0, 
+            0,    9,   10,    0,    0,    0,   11,    0,   12,   13, 
+           14,   15,   16,   17,   18,    0,    0,   89,    0,   19, 
+           20,   21,   22,   23,   24,   25,    0,    0,   26,    0, 
+            0,    0,  392,    0,   97,   28,  392,    0,   31,   32, 
+           33,    0,   34,   35,   36,   37,   38,   39,    0,   40, 
+           41,   42,   43,    0,    0,    0,    0,    0,    0,   22, 
+           22,   22,    0,    0,  392,   22,   22,    0,   22,    0, 
+            0,    0,  223,    0,  318,  113,    0,    0,   46,   47, 
+            0,   48,    0,    0,  318,    0,    0,   50,    0,    0, 
+           22,   22,   22,   22,   22,   51,    0,    0,    0,    0, 
+           52,   53,   54,   55,   56,   57,    0,    0,    0,   58, 
+            0,   59,   60,    0,   61,   62,    0,    0,  318,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  293,    0,    0,  112, 
-            0,    0,   46,   47,    0,   48,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  318,    0,    0,    0,  318,   22,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,   22, 
+            0,    0,    0,    0,    0,  318,    4,    5,    6,    0, 
+            8,    0,    0,    0,    9,   10,    0,    0,    0,   11, 
+          319,   12,   13,   14,  101,  102,   17,   18,    0,    0, 
+          319,    0,  103,  104,  105,   22,   23,   24,   25,    0, 
+            0,  106,    0,    0,    0,    0,    0,    0,  107,    0, 
+            0,   31,   32,   33,    0,   34,   35,   36,   37,   38, 
+           39,    0,   40,    0,  319,  110,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  392,  392,  392,    0,  319, 
+            0,  392,  392,  319,  392,  240,    0,    0,   45,    0, 
+            0,   46,   47,  392,   48,    0,   49,    0,    0,    0, 
+            0,    0,    0,  392,  392,    0,  392,  392,  392,  392, 
+          392,  319,    0,   52,   53,   54,   55,   56,   57,    0, 
+            0,    0,   58,    0,   59,   60,    0,   61,   62,    0, 
+          392,  392,  392,  392,  392,  392,  392,  392,  392,  392, 
+          392,  392,  392,  392,    0,    0,  392,  392,  392,    0, 
+            0,    0,    0,    0,  392,    0,    0,    0,    0,    0, 
+            0,  392,  282,  392,    0,  392,  392,  392,  392,  392, 
+          392,  392,  282,  392,  392,  392,  318,  318,  318,    0, 
+            0,  318,  318,  318,    0,  318,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  318,    0,    0,    0,    0, 
+            0,    0,    0,    0,  318,  318,  282,  318,  318,  318, 
+          318,  318,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  282,    0,    0,    0,  282,    0,    0,    0,    0, 
+            0,  318,  318,  318,  318,  318,  318,  318,  318,  318, 
+          318,  318,  318,  318,  318,    0,    0,  318,  318,  318, 
+            0,    0,    0,  282,    0,  318,    0,    0,    0,    0, 
+            0,    0,  318,    0,  318,  464,  318,  318,  318,  318, 
+          318,  318,  318,    0,  318,  464,  318,    0,    0,    0, 
+            0,    0,  319,  319,  319,    0,    0,  319,  319,  319, 
+            0,  319,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  319,    0,    0,    0,    0,    0,    0,    0,   88, 
+          319,  319,    0,  319,  319,  319,  319,  319,    0,    0, 
+            0,    0,    0,    0,  464,    0,   96,    0,  464,    0, 
+            0,    0,    0,    0,    0,    0,    0,  319,  319,  319, 
+          319,  319,  319,  319,  319,  319,  319,  319,  319,  319, 
+          319,    0,    0,  319,  319,  319,  464,    0,    0,    0, 
+            0,  319,    0,    0,    0,    0,    0,    0,  319,    0, 
+          319,    0,  319,  319,  319,  319,  319,  319,  319,  272, 
+          319,    0,  319,    0,    0,    0,    0,    0,    0,  272, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,   95,  282,  282,  282,    0,    0,  282, 
+          282,  282,    0,  282,    0,    0,    0,    0,  272,    0, 
+          103,    0,  272,  282,    0,    0,    0,    0,    0,    0, 
+            0,    0,  282,  282,    0,  282,  282,  282,  282,  282, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          272,    0,    0,    0,    0,    0,    0,    0,    0,  282, 
+          282,  282,  282,  282,  282,  282,  282,  282,  282,  282, 
+          282,  282,  282,    0,    0,  282,  282,  282,    0,    0, 
+            0,    0,    0,  282,    0,    0,    0,    0,    0,    0, 
+          282,    0,  282,  279,  282,  282,  282,  282,  282,  282, 
+          282,    0,  282,  279,  282,    0,    0,  464,  464,  464, 
+            0,    0,    0,  464,  464,    0,  464,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  464,  464,   94,  464,  464, 
+          464,  464,  464,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  279,    0,  102,    0,  279,    0,    0,    0, 
+            0,    0,  464,  464,  464,  464,  464,  464,  464,  464, 
+          464,  464,  464,  464,  464,  464,    0,    0,  464,  464, 
+          464,    0,  465,    0,  279,    0,  464,    0,    0,    0, 
+            0,    0,    0,  464,    0,  464,    0,  464,  464,  464, 
+          464,  464,  464,  464,   60,  464,    0,  464,    0,    0, 
+            0,    0,    0,    0,   60,    0,    0,    0,    0,    0, 
+            0,  272,  272,  272,    0,    0,    0,  272,  272,    0, 
+          272,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,   62,    0,  272, 
+          272,    0,  272,  272,  272,  272,  272,   62,    0,    0, 
+            0,    0,    0,   60,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,  272,  272,  272,  272, 
+          272,  272,  272,  272,  272,  272,  272,  272,  272,  272, 
+            0,    0,  272,  272,  272,    0,    0,    0,    0,    0, 
+          272,    0,    0,    0,    0,    0,   62,  272,   64,  272, 
+            0,  272,  272,  272,  272,  272,  272,  272,   64,  272, 
+            0,  272,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,   48,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,   48,  279,  279,  279,    0,    0, 
+            0,  279,  279,    0,  279,    0,    0,   64,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  279,  279,    0,  279,  279,  279,  279, 
+          279,    0,    0,    0,    0,    0,    0,   50,    0,    0, 
+            0,    0,    0,   48,    0,    0,    0,   50,    0,    0, 
+          279,  279,  279,  279,  279,  279,  279,  279,  279,  279, 
+          279,  279,  279,  279,    0,    0,  279,  279,  279,    0, 
+            0,    0,    0,    0,  279,    0,    0,    0,    0,    0, 
+            0,  279,    0,  279,    0,  279,  279,  279,  279,  279, 
+          279,  279,    0,  279,   55,  279,   50,    0,    0,    0, 
+            0,    0,    0,    0,   55,    0,   60,   60,   60,    0, 
+            0,   60,   60,   60,    0,   60,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,   60,   60,    0,    0,    0, 
+            0,    0,    0,    0,   60,   60,    0,   60,   60,   60, 
+           60,   60,    0,    0,    0,    0,    0,    0,    0,   62, 
+           62,   62,    0,   55,   62,   62,   62,    0,   62,    0, 
+            0,    0,    0,    0,    0,  263,    0,    0,   62,   62, 
+            0,    0,    0,    0,    0,  263,    0,   62,   62,    0, 
+           62,   62,   62,   62,   62,   60,    0,    0,   60,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,   60,    0,    0,  261, 
+           64,   64,   64,    0,    0,   64,   64,   64,    0,   64, 
+            0,    0,    0,    0,  263,    0,    0,    0,   62,   64, 
+           64,   62,    0,    0,    0,    0,    0,    0,   64,   64, 
+            0,   64,   64,   64,   64,   64,   48,   48,   48,   62, 
+            0,   48,   48,   48,    0,   48,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,   48,    0,    0,  416,    0, 
+            0,    0,    0,    0,   48,   48,    0,   48,   48,   48, 
+           48,   48,    0,    0,    0,    0,    0,    0,    0,   64, 
+            0,    0,   64,    0,    0,    0,    0,    0,    0,   50, 
+           50,   50,    0,    0,   50,   50, 
       };
    }
 
    private static final short[] yyTable4() {
       return new short[] {
 
-            0,    0,    0,    0,    0,    0,    0,    0,   52,   53, 
-           54,   55,   56,   57,    0,    0,    0,   58,    0,   59, 
-           60,    0,   61,   62,    4,    5,    6,    7,    8,    0, 
+           50,    0,   50,    0,   64,    0,    0,    0,    0,    0, 
+            0,    0,   50,    0,    0,    0,    0,    0,    0,   48, 
+            0,   50,   50,    0,   50,   50,   50,   50,   50,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+           48,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+           55,   55,   55,    0,    0,   55,   55,   55,    0,   55, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,   55, 
+            0,    0,   50,    0,    0,    0,    0,    0,   55,   55, 
+            0,   55,   55,   55,   55,   55,  416,    0,    0,    0, 
+          631,  553,    0,   50,  632,    0,    0,    0,  167,  168, 
+            0,  169,  170,  171,  172,  173,  174,  175,    0,    0, 
+          176,  177,    0,    0,    0,  178,  179,  180,  181,    0, 
+            0,  263,  263,  263,  264,    0,  263,  263,  263,   55, 
+          263,  183,  184,    0,  185,  186,  187,  188,  189,  190, 
+          191,  192,  193,  194,  195,    0,    0,  196,    0,    0, 
+           55,    0,  263,  263,  263,  263,  263,    0,    0,    0, 
+            4,    5,    6,    7,    8,    0,    0,    0,    9,   10, 
+            0,    0,    0,   11,    0,   12,   13,   14,   15,   16, 
+           17,   18,    0,    0,    0,    0,   19,   20,   21,   22, 
+           23,   24,   25,    0,    0,   26,    0,  263,    0,    0, 
+          263,   27,   28,   29,   30,   31,   32,   33,    0,   34, 
+           35,   36,   37,   38,   39,    0,   40,   41,   42,   43, 
+            0,  263,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,   44, 
+            0,    0,   45,    0,    0,   46,   47,    0,   48,    0, 
+           49,    0,    0,    0,   50,    0,    0,    0,    0,    0, 
+            0,    0,   51,    0,    0,    0,    0,   52,   53,   54, 
+           55,   56,   57,    0,    0,    0,   58,    0,   59,   60, 
+            0,   61,   62,    0,    4,    5,    6,    7,    8,    0, 
             0,    0,    9,   10,    0,    0,    0,   11,    0,   12, 
            13,   14,   15,   16,   17,   18,    0,    0,    0,    0, 
            19,   20,   21,   22,   23,   24,   25,    0,    0,   26, 
-            0,    0,    0,    0,    0,   27,   28,   29,   30,   31, 
+            0,    0,    0,    0,    0,   27,   28,    0,   30,   31, 
            32,   33,    0,   34,   35,   36,   37,   38,   39,    0, 
            40,   41,   42,   43,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,   44,    0,    0,   45,    0,    0,   46, 
            47,    0,   48,    0,   49,    0,    0,    0,   50,    0, 
             0,    0,    0,    0,    0,    0,   51,    0,    0,    0, 
             0,   52,   53,   54,   55,   56,   57,    0,    0,    0, 
-           58,    0,   59,   60,    0,   61,   62,    4,    5,    6, 
-            7,    8,    0,    0,    0,    9,   10,    0,    0,    0, 
-           11,    0,   12,   13,   14,   15,   16,   17,   18,    0, 
-            0,    0,    0,   19,   20,   21,   22,   23,   24,   25, 
-            0,    0,   26,    0,    0,    0,    0,    0,   27,   28, 
-            0,   30,   31,   32,   33,    0,   34,   35,   36,   37, 
-           38,   39,    0,   40,   41,   42,   43,    0,    0,    0, 
+           58,    0,   59,   60,    0,   61,   62,    3,    4,    5, 
+            6,    7,    8,    0,    0,    0,    9,   10,    0,    0, 
+            0,   11,    0,   12,   13,   14,   15,   16,   17,   18, 
+            0,    0,    0,    0,   19,   20,   21,   22,   23,   24, 
+           25,    0,    0,   26,    0,    0,    0,    0,    0,   27, 
+           28,   29,   30,   31,   32,   33,    0,   34,   35,   36, 
+           37,   38,   39,    0,   40,   41,   42,   43,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,   44,    0,    0,   45, 
-            0,    0,   46,   47,    0,   48,    0,   49,    0,    0, 
-            0,   50,    0,    0,    0,    0,    0,    0,    0,   51, 
-            0,    0,    0,    0,   52,   53,   54,   55,   56,   57, 
-            0,    0,    0,   58,    0,   59,   60,    0,   61,   62, 
-            3,    4,    5,    6,    7,    8,    0,    0,    0,    9, 
-           10,    0,    0,    0,   11,    0,   12,   13,   14,   15, 
-           16,   17,   18,    0,    0,    0,    0,   19,   20,   21, 
-           22,   23,   24,   25,    0,    0,   26,    0,    0,    0, 
-            0,    0,   27,   28,   29,   30,   31,   32,   33,    0, 
-           34,   35,   36,   37,   38,   39,    0,   40,   41,   42, 
-           43,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,   44,    0,    0, 
+           45,    0,    0,   46,   47,    0,   48,    0,   49,    0, 
+            0,    0,   50,    0,    0,    0,    0,    0,    0,    0, 
+           51,    0,    0,    0,    0,   52,   53,   54,   55,   56, 
+           57,    0,    0,    0,   58,    0,   59,   60,    0,   61, 
+           62,  217,    4,    5,    6,    7,    8,    0,    0,    0, 
+            9,   10,    0,    0,    0,   11,    0,   12,   13,   14, 
+           15,   16,   17,   18,    0,    0,    0,    0,   19,   20, 
+           21,   22,   23,   24,   25,    0,    0,   26,    0,    0, 
+            0,    0,    0,   27,   28,    0,   30,   31,   32,   33, 
+            0,   34,   35,   36,   37,   38,   39,    0,   40,   41, 
+           42,   43,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-           44,    0,    0,   45,    0,    0,   46,   47,    0,   48, 
-            0,   49,    0,    0,    0,   50,    0,    0,    0,    0, 
-            0,    0,    0,   51,    0,    0,    0,    0,   52,   53, 
-           54,   55,   56,   57,    0,    0,    0,   58,    0,   59, 
-           60,    0,   61,   62,  216,    4,    5,    6,    7,    8, 
-            0,    0,    0,    9,   10,    0,    0,    0,   11,    0, 
-           12,   13,   14,   15,   16,   17,   18,    0,    0,    0, 
-            0,   19,   20,   21,   22,   23,   24,   25,    0,    0, 
-           26,    0,    0,    0,    0,    0,   27,   28,    0,   30, 
-           31,   32,   33,    0,   34,   35,   36,   37,   38,   39, 
-            0,   40,   41,   42,   43,    0,    0,    0,    0,    0, 
+            0,   44,    0,    0,   45,    0,    0,   46,   47,    0, 
+           48,    0,   49,    0,    0,    0,   50,    0,    0,    0, 
+            0,    0,    0,    0,   51,    0,    0,    0,    0,   52, 
+           53,   54,   55,   56,   57,    0,    0,    0,   58,    0, 
+           59,   60,    0,   61,   62,  217,    4,    5,    6,    7, 
+            8,    0,    0,    0,    9,   10,    0,    0,    0,   11, 
+            0,   12,   13,   14,   15,   16,   17,   18,    0,    0, 
+            0,    0,   19,   20,   21,   22,   23,   24,   25,    0, 
+            0,   26,    0,    0,    0,    0,    0,   27,   28,    0, 
+           30,   31,   32,   33,    0,   34,   35,   36,   37,   38, 
+           39,    0,   40,   41,   42,   43,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,   44,    0,    0,   45,    0,    0, 
-           46,   47,    0,   48,    0,   49,    0,    0,    0,   50, 
-            0,    0,    0,    0,    0,    0,    0,   51,    0,    0, 
-            0,    0,   52,   53,   54,   55,   56,   57,    0,    0, 
-            0,   58,    0,   59,   60,    0,   61,   62,  216,    4, 
+            0,    0,    0,    0,    0,   44,    0,    0,  281,    0, 
+            0,   46,   47,    0,   48,    0,   49,    0,    0,    0, 
+           50,    0,    0,    0,    0,    0,    0,    0,   51,    0, 
+            0,    0,    0,   52,   53,   54,   55,   56,   57,    0, 
+            0,    0,   58,    0,   59,   60,    0,   61,   62,    4, 
             5,    6,    7,    8,    0,    0,    0,    9,   10,    0, 
             0,    0,   11,    0,   12,   13,   14,   15,   16,   17, 
            18,    0,    0,    0,    0,   19,   20,   21,   22,   23, 
            24,   25,    0,    0,   26,    0,    0,    0,    0,    0, 
-           27,   28,    0,   30,   31,   32,   33,    0,   34,   35, 
+           27,   28,   29,   30,   31,   32,   33,    0,   34,   35, 
            36,   37,   38,   39,    0,   40,   41,   42,   43,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,   44,    0, 
-            0,  280,    0,    0,   46,   47,    0,   48,    0,   49, 
+            0,   45,    0,    0,   46,   47,    0,   48,    0,   49, 
             0,    0,    0,   50,    0,    0,    0,    0,    0,    0, 
             0,   51,    0,    0,    0,    0,   52,   53,   54,   55, 
            56,   57,    0,    0,    0,   58,    0,   59,   60,    0, 
            61,   62,    4,    5,    6,    7,    8,    0,    0,    0, 
             9,   10,    0,    0,    0,   11,    0,   12,   13,   14, 
            15,   16,   17,   18,    0,    0,    0,    0,   19,   20, 
            21,   22,   23,   24,   25,    0,    0,   26,    0,    0, 
-            0,    0,    0,   27,   28,   29,   30,   31,   32,   33, 
+            0,    0,    0,   27,   28,    0,   30,   31,   32,   33, 
             0,   34,   35,   36,   37,   38,   39,    0,   40,   41, 
            42,   43,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,   44,    0,    0,   45,    0,    0,   46,   47,    0, 
            48,    0,   49,    0,    0,    0,   50,    0,    0,    0, 
             0,    0,    0,    0,   51,    0,    0,    0,    0,   52, 
            53,   54,   55,   56,   57,    0,    0,    0,   58,    0, 
-           59,   60,    0,   61,   62,    4,    5,    6,    7,    8, 
+           59,   60,    0,   61,   62,    4,    5,    6,    0,    8, 
             0,    0,    0,    9,   10,    0,    0,    0,   11,    0, 
-           12,   13,   14,   15,   16,   17,   18,    0,    0,    0, 
-            0,   19,   20,   21,   22,   23,   24,   25,    0,    0, 
-           26,    0,    0,    0,    0,    0,   27,   28,    0,   30, 
+           12,   13,   14,  101,  102,   17,   18,    0,    0,    0, 
+            0,  103,   20,   21,   22,   23,   24,   25,    0,    0, 
+          106,    0,    0,    0,    0,    0,    0,   28,    0,    0, 
            31,   32,   33,    0,   34,   35,   36,   37,   38,   39, 
-            0,   40,   41,   42,   43,    0,    0,    0,    0,    0, 
+          247,   40,   41,   42,   43,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,   44,    0,    0,   45,    0,    0, 
-           46,   47,    0,   48,    0,   49,    0,    0,    0,   50, 
-            0,    0,    0,    0,    0,    0,    0,   51,    0,    0, 
+            0,    0,    0,    0,  223,    0,    0,  113,    0,    0, 
+           46,   47,    0,   48,    0,  248,    0,  249,    0,   50, 
+            0,    0,    0,    0,    0,    0,    0,  250,    0,    0, 
             0,    0,   52,   53,   54,   55,   56,   57,    0,    0, 
             0,   58,    0,   59,   60,    0,   61,   62,    4,    5, 
             6,    0,    8,    0,    0,    0,    9,   10,    0,    0, 
-            0,   11,    0,   12,   13,   14,  100,  101,   17,   18, 
-            0,    0,    0,    0,  102,   20,   21,   22,   23,   24, 
-           25,    0,    0,  105,    0,    0,    0,    0,    0,    0, 
+            0,   11,    0,   12,   13,   14,  101,  102,   17,   18, 
+            0,    0,    0,    0,  103,  104,  105,   22,   23,   24, 
+           25,    0,    0,  106,    0,    0,    0,    0,    0,    0, 
            28,    0,    0,   31,   32,   33,    0,   34,   35,   36, 
-           37,   38,   39,  246,   40,   41,   42,   43,    0,    0, 
+           37,   38,   39,  247,   40,   41,   42,   43,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  222,    0,    0, 
-          112,    0,    0,   46,   47,    0,   48,    0,  247,    0, 
-          248,    0,   50,    0,    0,    0,    0,    0,    0,    0, 
-          249,    0,    0,    0,    0,   52,   53,   54,   55,   56, 
+            0,    0,    0,    0,    0,    0,    0,  223,    0,    0, 
+          113,    0,    0,   46,   47,    0,   48,    0,  638,    0, 
+          249,    0,   50,    0,    0,    0,    0,    0,    0,    0, 
+          250,    0,    0,    0,    0,   52,   53,   54,   55,   56, 
            57,    0,    0,    0,   58,    0,   59,   60,    0,   61, 
-           62,    4,    5,    6,    0,    8,    0,    0,    0,    9, 
-           10,    0,    0,    0,   11,    0,   12,   13,   14,  100, 
-          101,   17,   18,    0,    0,    0,    0,  102,  103,  104, 
-           22,   23,   24,   25,    0,    0,  105,    0,    0,    0, 
-            0,    0,    0,   28,    0,    0,   31,   32,   33,    0, 
-           34,   35,   36,   37,   38,   39,  246,   40,   41,   42, 
-           43,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+           62,  252,  252,  252,    0,  252,    0,    0,    0,  252, 
+          252,    0,    0,    0,  252,    0,  252,  252,  252,  252, 
+          252,  252,  252,    0,    0,    0,    0,  252,  252,  252, 
+          252,  252,  252,  252,    0,    0,  252,    0,    0,    0, 
+            0,    0,    0,  252,    0,    0,  252,  252,  252,    0, 
+          252,  252,  252,  252,  252,  252,  252,  252,  252,  252, 
+          252,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          252,    0,    0,  252,    0,    0,  252,  252,    0,  252, 
+            0,  252,    0,  252,    0,  252,    0,    0,    0,    0, 
+            0,    0,    0,  252,    0,    0,    0,    0,  252,  252, 
+          252,  252,  252,  252,    0,    0,    0,  252,    0,  252, 
+          252,    0,  252,  252,    4,    5,    6,    0,    8,    0, 
+            0,    0,    9,   10,    0,    0,    0,   11,    0,   12, 
+           13,   14,  101,  102,   17,   18,    0,    0,    0,    0, 
+          103,  104,  105,   22,   23,   24,   25,    0,    0,  106, 
+            0,    0,    0,    0,    0,    0,   28,    0,    0,   31, 
+           32,   33,    0,   34,   35,   36,   37,   38,   39,  247, 
+           40,   41,   42,   43,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          222,    0,    0,  112,    0,    0,   46,   47,    0,   48, 
-            0,  635,    0,  248,    0,   50,    0,    0,    0,    0, 
-            0,    0,    0,  249,    0,    0,    0,    0,   52,   53, 
-           54,   55,   56,   57,    0,    0,    0,   58,    0,   59, 
-           60,    0,   61,   62,  250,  250,  250,    0,  250,    0, 
-            0,    0,  250,  250,    0,    0,    0,  250,    0,  250, 
-          250,  250,  250,  250,  250,  250,    0,    0,    0,    0, 
-          250,  250,  250,  250,  250,  250,  250,    0,    0,  250, 
-            0,    0,    0,    0,    0,    0,  250,    0,    0,  250, 
-          250,  250,    0,  250,  250,  250,  250,  250,  250,  250, 
-          250,  250,  250,  250,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  250,    0,    0,  250,    0,    0,  250, 
-          250,    0,  250,    0,  250,    0,  250,    0,  250,    0, 
+            0,    0,    0,  223,    0,    0,  113,    0,    0,   46, 
+           47,    0,   48,    0,  248,    0,    0,    0,   50,    0, 
             0,    0,    0,    0,    0,    0,  250,    0,    0,    0, 
-            0,  250,  250,  250,  250,  250,  250,    0,    0,    0, 
-          250,    0,  250,  250,    0,  250,  250,    4,    5,    6, 
+            0,   52,   53,   54,   55,   56,   57,    0,    0,    0, 
+           58,    0,   59,   60,    0,   61,   62,    4,    5,    6, 
             0,    8,    0,    0,    0,    9,   10,    0,    0,    0, 
-           11,    0,   12,   13,   14,  100,  101,   17,   18,    0, 
-            0,    0,    0,  102,  103,  104,   22,   23,   24,   25, 
-            0,    0,  105,    0,    0,    0,    0,    0,    0,   28, 
+           11,    0,   12,   13,   14,  101,  102,   17,   18,    0, 
+            0,    0,    0,  103,  104,  105,   22,   23,   24,   25, 
+            0,    0,  106,    0,    0,    0,    0,    0,    0,   28, 
             0,    0,   31,   32,   33,    0,   34,   35,   36,   37, 
-           38,   39,  246,   40,   41,   42,   43,    0,    0,    0, 
+           38,   39,  247,   40,   41,   42,   43,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  222,    0,    0,  112, 
-            0,    0,   46,   47,    0,   48,    0,  247,    0,    0, 
-            0,   50,    0,    0,    0,    0,    0,    0,    0,  249, 
+            0,    0,    0,    0,    0,    0,  223,    0,    0,  113, 
+            0,    0,   46,   47,    0,   48,    0,    0,    0,  249, 
+            0,   50,    0,    0,    0,    0,    0,    0,    0,  250, 
             0,    0,    0,    0,   52,   53,   54,   55,   56,   57, 
             0,    0,    0,   58,    0,   59,   60,    0,   61,   62, 
             4,    5,    6,    0,    8,    0,    0,    0,    9,   10, 
-            0,    0,    0,   11,    0,   12,   13,   14,  100,  101, 
-           17,   18,    0,    0,    0,    0,  102,  103,  104,   22, 
-           23,   24,   25,    0,    0,  105,    0,    0,    0,    0, 
+            0,    0,    0,   11,    0,   12,   13,   14,  101,  102, 
+           17,   18,    0,    0,    0,    0,  103,  104,  105,   22, 
+           23,   24,   25,    0,    0,  106,    0,    0,    0,    0, 
             0,    0,   28,    0,    0,   31,   32,   33,    0,   34, 
-           35,   36,   37,   38,   39,  246,   40,   41,   42,   43, 
+           35,   36,   37,   38,   39,  247,   40,   41,   42,   43, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  222, 
-            0,    0,  112,    0,    0,   46,   47,    0,   48,    0, 
-            0,    0,  248,    0,   50,    0,    0,    0,    0,    0, 
-            0,    0,  249,    0,    0,    0,    0,   52,   53,   54, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  223, 
+            0,    0,  113,    0,    0,   46,   47,    0,   48,    0, 
+          638,    0,    0,    0,   50,    0,    0,    0,    0,    0, 
+            0,    0,  250,    0,    0,    0,    0,   52,   53,   54, 
            55,   56,   57,    0,    0,    0,   58,    0,   59,   60, 
             0,   61,   62,    4,    5,    6,    0,    8,    0,    0, 
             0,    9,   10,    0,    0,    0,   11,    0,   12,   13, 
-           14,  100,  101,   17,   18,    0,    0,    0,    0,  102, 
-          103,  104,   22,   23,   24,   25,    0,    0,  105,    0, 
+           14,  101,  102,   17,   18,    0,    0,    0,    0,  103, 
+          104,  105,   22,   23,   24,   25,    0,    0,  106,    0, 
             0,    0,    0,    0,    0,   28,    0,    0,   31,   32, 
-           33,    0,   34,   35,   36,   37,   38,   39,  246,   40, 
+           33,    0,   34,   35,   36,   37,   38,   39,  247,   40, 
            41,   42,   43,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  222,    0,    0,  112,    0,    0,   46,   47, 
-            0,   48,    0,  635,    0,    0,    0,   50,    0,    0, 
-            0,    0,    0,    0,    0,  249,    0,    0,    0,    0, 
+            0,    0,  223,    0,    0,  113,    0,    0,   46,   47, 
+            0,   48,    0,    0,    0,    0,    0,   50,    0,    0, 
+            0,    0,    0,    0,    0,  250,    0,    0,    0,    0, 
            52,   53,   54,   55,   56,   57,    0,    0,    0,   58, 
             0,   59,   60,    0,   61,   62,    4,    5,    6,    0, 
             8,    0,    0,    0,    9,   10,    0,    0,    0,   11, 
-            0,   12,   13,   14,  100,  101,   17,   18,    0,    0, 
-            0,    0,  102,  103,  104,   22,   23,   24,   25,    0, 
-            0,  105,    0,    0,    0,    0,    0,    0,   28,    0, 
+            0,   12,   13,   14,  101,  102,   17,   18,    0,    0, 
+            0,    0,  103,  104,  105,   22,   23,   24,   25,    0, 
+            0,  106,    0,    0,    0,    0,    0,    0,   28,    0, 
             0,   31,   32,   33,    0,   34,   35,   36,   37,   38, 
-           39,  246,   40,   41,   42,   43,    0,    0,    0,    0, 
+           39,    0,   40,   41,   42,   43,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  222,    0,    0,  112,    0, 
+            0,    0,    0,    0,    0,  223,    0,    0,  113,  447, 
             0,   46,   47,    0,   48,    0,    0,    0,    0,    0, 
-           50,    0,    0,    0,    0,    0,    0,    0,  249,    0, 
+           50,    0,    0,    0,    0,    0,    0,    0,  250,    0, 
             0,    0,    0,   52,   53,   54,   55,   56,   57,    0, 
             0,    0,   58,    0,   59,   60,    0,   61,   62,    4, 
             5,    6,    0,    8,    0,    0,    0,    9,   10,    0, 
-            0,    0,   11,    0,   12,   13,   14,  100,  101,   17, 
-           18,    0,    0,    0,    0,  102,  103,  104,   22,   23, 
-           24,   25,    0,    0,  105,    0,    0,    0,    0,    0, 
+            0,    0,   11,    0,   12,   13,   14,   15,   16,   17, 
+           18,    0,    0,    0,    0,   19,   20,   21,   22,   23, 
+           24,   25,    0,    0,  106,    0,    0,    0,    0,    0, 
             0,   28,    0,    0,   31,   32,   33,    0,   34,   35, 
            36,   37,   38,   39,    0,   40,   41,   42,   43,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  222,    0, 
-            0,  112,  446,    0,   46,   47,    0,   48,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  223,    0, 
+            0,  113,    0,    0,   46,   47,    0,   48,    0,  572, 
             0,    0,    0,   50,    0,    0,    0,    0,    0,    0, 
-            0,  249,    0,    0,    0,    0,   52,   53,   54,   55, 
+            0,  250,    0,    0,    0,    0,   52,   53,   54,   55, 
            56,   57,    0,    0,    0,   58,    0,   59,   60,    0, 
            61,   62,    4,    5,    6,    0,    8,    0,    0,    0, 
             9,   10,    0,    0,    0,   11,    0,   12,   13,   14, 
-           15,   16,   17,   18,    0,    0,    0,    0,   19,   20, 
-           21,   22,   23,   24,   25,    0,    0,  105,    0,    0, 
+          101,  102,   17,   18,    0,    0,    0,    0,  103,  104, 
+          105,   22,   23,   24,   25,    0,    0,  106,    0,    0, 
             0,    0,    0,    0,   28,    0,    0,   31,   32,   33, 
             0,   34,   35,   36,   37,   38,   39,    0,   40,   41, 
            42,   43,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  222,    0,    0,  112,    0,    0,   46,   47,    0, 
-           48,    0,  571,    0,    0,    0,   50,    0,    0,    0, 
-            0,    0,    0,    0,  249,    0,    0,    0,    0,   52, 
+            0,  223,    0,    0,  113,    0,    0,   46,   47,    0, 
+           48,    0,  572,    0,    0,    0,   50,    0,    0,    0, 
+            0,    0,    0,    0,  250,    0,    0,    0,    0,   52, 
            53,   54,   55,   56,   57,    0,    0,    0,   58,    0, 
            59,   60,    0,   61,   62,    4,    5,    6,    0,    8, 
             0,    0,    0,    9,   10,    0,    0,    0,   11,    0, 
-           12,   13,   14,  100,  101,   17,   18,    0,    0,    0, 
-            0,  102,  103,  104,   22,   23,   24,   25,    0,    0, 
-          105,    0,    0,    0,    0,    0,    0,   28,    0,    0, 
+           12,   13,   14,  101,  102,   17,   18,    0,    0,    0, 
+            0,  103,  104,  105,   22,   23,   24,   25,    0,    0, 
+          106,    0,    0,    0,    0,    0,    0,   28,    0,    0, 
            31,   32,   33,    0,   34,   35,   36,   37,   38,   39, 
             0,   40,   41,   42,   43,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  222,    0,    0,  112,    0,    0, 
-           46,   47,    0,   48,    0,  571,    0,    0,    0,   50, 
-            0,    0,    0,    0,    0,    0,    0,  249,    0,    0, 
+            0,    0,    0,    0,  223,    0,    0,  113,    0,    0, 
+           46,   47,    0,   48,    0,  248,    0,    0,    0,   50, 
+            0,    0,    0,    0,    0,    0,    0,  250,    0,    0, 
             0,    0,   52,   53,   54,   55,   56,   57,    0,    0, 
             0,   58,    0,   59,   60,    0,   61,   62,    4,    5, 
             6,    0,    8,    0,    0,    0,    9,   10,    0,    0, 
-            0,   11,    0,   12,   13,   14,  100,  101,   17,   18, 
-            0,    0,    0,    0,  102,  103,  104,   22,   23,   24, 
-           25,    0,    0,  105,    0,    0,    0,    0,    0,    0, 
+            0,   11,    0,   12,   13,   14,  101,  102,   17,   18, 
+            0,    0,    0,    0,  103,  104,  105,   22,   23,   24, 
+           25,    0,    0,  106,    0,    0,    0,    0,    0,    0, 
            28,    0,    0,   31,   32,   33,    0,   34,   35,   36, 
            37,   38,   39,    0,   40,   41,   42,   43,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  222,    0,    0, 
-          112,    0,    0,   46,   47,    0,   48,    0,  247,    0, 
+            0,    0,    0,    0,    0,    0,    0,  223,    0,    0, 
+          113,    0,    0,   46,   47,    0,   48,    0,  817,    0, 
             0,    0,   50,    0,    0,    0,    0,    0,    0,    0, 
-          249,    0,    0,    0,    0,   52,   53,   54,   55,   56, 
+          250,    0,    0,    0,    0,   52,   53,   54,   55,   56, 
            57,    0,    0,    0,   58,    0,   59,   60,    0,   61, 
            62,    4,    5,    6,    0,    8,    0,    0,    0,    9, 
-           10,    0,    0,    0,   11,    0,   12,   13,   14,  100, 
-          101,   17,   18,    0,    0,    0,    0,  102,  103,  104, 
-           22,   23,   24,   25,    0,    0,  105,    0,    0,    0, 
+           10,    0,    0,    0,   11,    0,   12,   13,   14,  101, 
+          102,   17,   18,    0,    0,    0,    0,  103,  104,  105, 
+           22,   23,   24,   25,    0,    0,  106,    0,    0,    0, 
             0,    0,    0,   28,    0,    0,   31,   32,   33,    0, 
            34,   35,   36,   37,   38,   39,    0,   40,   41,   42, 
            43,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          222,    0,    0,  112,    0,    0,   46,   47,    0,   48, 
-            0,  813,    0,    0,    0,   50,    0,    0,    0,    0, 
-            0,    0,    0,  249,    0,    0,    0,    0,   52,   53, 
+          223,    0,    0,  113,    0,    0,   46,   47,    0,   48, 
+            0,  638,    0,    0,    0,   50,    0,    0,    0,    0, 
+            0,    0,    0,  250,    0,    0,    0,    0,   52,   53, 
            54,   55,   56,   57,    0,    0,    0,   58,    0,   59, 
-           60,    0,   61,   62,    4,    5,    6,    0,    8,    0, 
-            0,    0,    9,   10,    0,    0,    0,   11,    0,   12, 
-           13,   14,  100,  101,   17,   18,    0,    0,    0,    0, 
-          102,  103,  104,   22,   23,   24,   25,    0,    0,  105, 
-            0,    0,    0,    0,    0,    0,   28,    0,    0,   31, 
-           32,   33,    0,   34,   35,   36,   37,   38,   39,    0, 
-           40,   41,   42,   43,    0,    0,    0,    0,    0,    0, 
+           60,    0,   61,   62,  537,  537,  537,    0,  537,    0, 
+            0,    0,  537,  537,    0,    0,    0,  537,    0,  537, 
+          537,  537,  537,  537,  537,  537,    0,    0,    0,    0, 
+          537,  537,  537,  537,  537,  537,  537,    0,    0,  537, 
+            0,    0,    0,    0,    0,    0,  537,    0,    0,  537, 
+          537,  537,    0,  537,  537,  537,  537,  537,  537,    0, 
+          537,  537,  537,  537,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,  537,    0,    0,  537,  537,    0,  537, 
+          537,    0,  537,    0,    0,    0,    0,    0,  537,    0, 
+            0,    0,    0,    0,    0,    0,  537,    0,    0,    0, 
+            0,  537,  537,  537,  537,  537,  537,    0,    0,    0, 
+          537,    0,  537,  537,    0,  537,  537,    4,    5,    6, 
+            0,    8,    0,    0,    0,    9,   10,    0,    0,    0, 
+           11,    0,   12,   13,   14,   15,   16,   17,   18,    0, 
+            0,    0,    0,   19,   20,   21,   22,   23,   24,   25, 
+            0,    0,   26,    0,    0,    0,    0,    0,    0,   28, 
+            0,    0,   31,   32,   33,    0,   34,   35,   36,   37, 
+           38,   39,    0,   40,   41,   42,   43,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  222,    0,    0,  112,    0,    0,   46, 
-           47,    0,   48,    0,  635,    0,    0,    0,   50,    0, 
-            0,    0,    0,    0,    0,    0,  249,    0,    0,    0, 
-            0,   52,   53,   54,   55,   56,   57,    0,    0,    0, 
-           58,    0,   59,   60,    0,   61,   62,  535,  535,  535, 
-            0,  535,    0,    0,    0,  535,  535,    0,    0,    0, 
-          535,    0,  535,  535,  535,  535,  535,  535,  535,    0, 
-            0,    0,    0,  535,  535,  535,  535,  535,  535,  535, 
-            0,    0,  535,    0,    0,    0,    0,    0,    0,  535, 
-            0,    0,  535,  535,  535,    0,  535,  535,  535,  535, 
-          535,  535,    0,  535,  535,  535,  535,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  535,    0,    0,  535, 
-          535,    0,  535,  535,    0,  535,    0,    0,    0,    0, 
-            0,  535,    0,    0,    0,    0,    0,    0,    0,  535, 
-            0,    0,    0,    0,  535,  535,  535,  535,  535,  535, 
-            0,    0,    0,  535,    0,  535,  535,    0,  535,  535, 
+            0,    0,    0,    0,    0,    0,  223,    0,    0,  113, 
+            0,    0,   46,   47,    0,   48,    0,    0,    0,    0, 
+            0,   50,    0,    0,    0,    0,    0,    0,    0,   51, 
+            0,    0,    0,    0,   52,   53,   54,   55,   56,   57, 
+            0,    0,    0,   58,    0,   59,   60,    0,   61,   62, 
             4,    5,    6,    0,    8,    0,    0,    0,    9,   10, 
-            0,    0,    0,   11,    0,   12,   13,   14,   15,   16, 
-           17,   18,    0,    0,    0,    0,   19,   20,   21,   22, 
-           23,   24,   25,    0,    0,   26,    0,    0,    0,    0, 
+            0,    0,    0,   11,    0,   12,   13,   14,  101,  102, 
+           17,   18,    0,    0,    0,    0,  103,  104,  105,   22, 
+           23,   24,   25,    0,    0,  106,    0,    0,    0,    0, 
             0,    0,   28,    0,    0,   31,   32,   33,    0,   34, 
            35,   36,   37,   38,   39,    0,   40,   41,   42,   43, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,  222, 
-            0,    0,  112,    0,    0,   46,   47,    0,   48,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  223, 
+            0,    0,  113,    0,    0,   46,   47,    0,   48,    0, 
             0,    0,    0,    0,   50,    0,    0,    0,    0,    0, 
-            0,    0,   51,    0,    0,    0,    0,   52,   53,   54, 
+            0,    0,  250,    0,    0,    0,    0,   52,   53,   54, 
            55,   56,   57,    0,    0,    0,   58,    0,   59,   60, 
             0,   61,   62,    4,    5,    6,    0,    8,    0,    0, 
             0,    9,   10,    0,    0,    0,   11,    0,   12,   13, 
-           14,  100,  101,   17,   18,    0,    0,    0,    0,  102, 
-          103,  104,   22,   23,   24,   25,    0,    0,  105,    0, 
+           14,   15,   16,   17,   18,    0,    0,    0,    0,   19, 
+           20,   21,   22,   23,   24,   25,    0,    0,  106,    0, 
             0,    0,    0,    0,    0,   28,    0,    0,   31,   32, 
            33,    0,   34,   35,   36,   37,   38,   39,    0,   40, 
            41,   42,   43,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,  222,    0,    0,  112,    0,    0,   46,   47, 
+            0,    0,  223,    0,    0,  113,    0,    0,   46,   47, 
             0,   48,    0,    0,    0,    0,    0,   50,    0,    0, 
-            0,    0,    0,    0,    0,  249,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  250,    0,    0,    0,    0, 
            52,   53,   54,   55,   56,   57,    0,    0,    0,   58, 
-            0,   59,   60,    0,   61,   62,    4,    5,    6,    0, 
-            8,    0,    0,    0,    9,   10,    0,    0,    0,   11, 
-            0,   12,   13,   14,   15,   16,   17,   18,    0,    0, 
-            0,    0,   19,   20,   21,   22,   23,   24,   25,    0, 
-            0,  105,    0,    0,    0,    0,    0,    0,   28,    0, 
-            0,   31,   32,   33,    0,   34,   35,   36,   37,   38, 
-           39,    0,   40,   41,   42,   43,    0,    0,    0,    0, 
+            0,   59,   60,    0,   61,   62,  537,  537,  537,    0, 
+          537,    0,    0,    0,  537,  537,    0,    0,    0,  537, 
+            0,  537,  537,  537,  537,  537,  537,  537,    0,    0, 
+            0,    0,  537,  537,  537,  537,  537,  537,  537,    0, 
+            0,  537,    0,    0,    0,    0,    0,    0,  537,    0, 
+            0,  537,  537,  537,    0,  537,  537,  537,  537,  537, 
+          537,    0,  537,  537,  537,  537,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,  537,    0,    0,  537,    0, 
+            0,  537,  537,    0,  537,    0,    0,    0,    0,    0, 
+          537,    0,    0,    0,    0,    0,    0,    0,  537,    0, 
+            0,    0,    0,  537,  537,  537,  537,  537,  537,    0, 
+            0,    0,  537,    0,  537,  537,    0,  537,  537,    4, 
+            5,    6,    0,    8,    0,    0,    0,    9,   10,    0, 
+            0,    0,   11,    0,   12,   13,   14,  101,  102,   17, 
+           18,    0,    0,    0,    0,  103,  104,  105,   22,   23, 
+           24,   25,    0,    0,  106,    0,    0,    0,    0,    0, 
+            0,  107,    0,    0,   31,   32,   33,    0,   34,   35, 
+           36,   37,   38,   39,    0,   40,    0,    0,  110,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,  222,    0,    0,  112,    0, 
-            0,   46,   47,    0,   48,    0,    0,    0,    0,    0, 
-           50,    0,    0,    0,    0,    0,    0,    0,  249,    0, 
-            0,    0,    0,   52,   53,   54,   55,   56,   57,    0, 
-            0,    0,   58,    0,   59,   60,    0,   61,   62,  535, 
-          535,  535,    0,  535,    0,    0,    0,  535,  535,    0, 
-            0,    0,  535,    0,  535,  535,  535,  535,  535,  535, 
-          535,    0,    0,    0,    0,  535,  535,  535,  535,  535, 
-          535,  535,    0,    0,  535,    0,    0,    0,    0,    0, 
-            0,  535,    0,    0,  535,  535,  535,    0,  535,  535, 
-          535,  535,  535,  535,    0,  535,  535,  535,  535,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  535,    0, 
-            0,  535,    0,    0,  535,  535,    0,  535,    0,    0, 
-            0,    0,    0,  535,    0,    0,    0,    0,    0,    0, 
-            0,  535,    0,    0,    0,    0,  535,  535,  535,  535, 
-          535,  535,    0,    0,    0,  535,    0,  535,  535,    0, 
-          535,  535,    4,    5,    6,    0,    8,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,  294,    0, 
+            0,  368,    0,    0,   46,   47,    0,   48,    0,  369, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,   52,   53,   54,   55, 
+           56,   57,    0,    0,    0,   58,    0,   59,   60,    0, 
+           61,   62,    4,    5,    6,    0,    8,    0,    0,    0, 
             9,   10,    0,    0,    0,   11,    0,   12,   13,   14, 
-          100,  101,   17,   18,    0,    0,    0,    0,  102,  103, 
-          104,   22,   23,   24,   25,    0,    0,  105,    0,    0, 
-            0,    0,    0,    0,  106,    0,    0,   31,   32,   33, 
-            0,   34,   35,   36,   37,   38,   39,    0,   40,    0, 
-            0,  109,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,  239,    0,    0,   45,    0,    0,   46,   47,    0, 
-           48,    0,   49,    0,    0,    0,    0,    0,    0,    0, 
+          101,  102,   17,   18,    0,    0,    0,    0,  103,  104, 
+          105,   22,   23,   24,   25,    0,    0,  106,    0,    0, 
+            0,    0,    0,    0,  107,    0,    0,   31,   32,   33, 
+            0,  108,   35,   36,   37,  109,   39,    0,   40,    0, 
+            0,  110,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,  112,    0,    0,  113,    0,    0,   46,   47,    0, 
+           48,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,   52, 
            53,   54,   55,   56,   57,    0,    0,    0,   58,    0, 
            59,   60,    0,   61,   62,    4,    5,    6,    0,    8, 
             0,    0,    0,    9,   10,    0,    0,    0,   11,    0, 
-           12,   13,   14,  100,  101,   17,   18,    0,    0,    0, 
-            0,  102,  103,  104,   22,   23,   24,   25,    0,    0, 
-          105,    0,    0,    0,    0,    0,    0,  106,    0,    0, 
+           12,   13,   14,  101,  102,   17,   18,    0,    0,    0, 
+            0,  103,  104,  105,   22,   23,   24,   25,    0,    0, 
+          106,    0,    0,    0,    0,    0,    0,  107,    0,    0, 
            31,   32,   33,    0,   34,   35,   36,   37,   38,   39, 
-            0,   40,    0,    0,  109,    0,    0,    0,    0,    0, 
+            0,   40,    0,    0,  110,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,  293,    0,    0,  367,    0,    0, 
-           46,   47,    0,   48,    0,  368,    0,    0,    0,    0, 
+            0,    0,    0,    0,  294,    0,    0,  368,    0,    0, 
+           46,   47,    0,   48,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,   52,   53,   54,   55,   56,   57,    0,    0, 
             0,   58,    0,   59,   60,    0,   61,   62,    4,    5, 
             6,    0,    8,    0,    0,    0,    9,   10,    0,    0, 
-            0,   11,    0,   12,   13,   14,  100,  101,   17,   18, 
-            0,    0,    0,    0,  102,  103,  104,   22,   23,   24, 
-           25,    0,    0,  105,    0,    0,    0,    0,    0,    0, 
-          106,    0,    0,   31,   32,   33,    0,  107,   35,   36, 
-           37,  108,   39,    0,   40,    0,    0,  109,    0,    0, 
+            0,   11,    0,   12,   13,   14,  101,  102,   17,   18, 
+            0,    0,    0,    0,  103,  104,  105,   22,   23,   24, 
+           25,    0,    0,  106,    0,    0,    0,    0,    0,    0, 
+          107,    0,    0,   31,   32,   33,    0,   34,   35,   36, 
+           37,   38,   39,    0,   40,    0,    0,  110,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,  111,    0,    0, 
-          112,    0,    0,   46,   47,    0,   48,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  827,    0,    0, 
+          113,    0,    0,   46,   47,    0,   48,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,   52,   53,   54,   55,   56, 
            57,    0,    0,    0,   58,    0,   59,   60,    0,   61, 
            62,    4,    5,    6,    0,    8,    0,    0,    0,    9, 
-           10,    0,    0,    0,   11,    0,   12,   13,   14,  100, 
-          101,   17,   18,    0,    0,    0,    0,  102,  103,  104, 
-           22,   23,   24,   25,    0,    0,  105,    0,    0,    0, 
-            0,    0,    0,  106,    0,    0,   31,   32,   33,    0, 
+           10,    0,    0,    0,   11,    0,   12,   13,   14,  101, 
+          102,   17,   18,    0,    0,    0,    0,  103,  104,  105, 
+           22,   23,   24,   25,    0,    0,  106,    0,    0,    0, 
+            0,    0,    0,  107,    0,    0,   31,   32,   33,    0, 
            34,   35,   36,   37,   38,   39,    0,   40,    0,    0, 
-          109,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+          110,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          293,    0,    0,  367,    0,    0,   46,   47,    0,   48, 
+          902,    0,    0,  113,    0,    0,   46,   47,    0,   48, 
             0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
             0,    0,    0,    0,    0,    0,    0,    0,   52,   53, 
            54,   55,   56,   57,    0,    0,    0,   58,    0,   59, 
-           60,    0,   61,   62,    4,    5,    6,    0,    8,    0, 
-            0,    0,    9,   10,    0,    0,    0,   11,    0,   12, 
-           13,   14,  100,  101,   17,   18,    0,    0,    0,    0, 
-          102,  103,  104,   22,   23,   24,   25,    0,    0,  105, 
-            0,    0,    0,    0,    0,    0,  106,    0,    0,   31, 
-           32,   33,    0,   34,   35,   36,   37,   38,   39,    0, 
-           40,    0,    0,  109,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  823,    0,    0,  112,    0,    0,   46, 
-           47,    0,   48,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,   52,   53,   54,   55,   56,   57,    0,    0,    0, 
-           58,    0,   59,   60,    0,   61,   62,    4,    5,    6, 
-            0,    8,    0,    0,    0,    9,   10,    0,    0,    0, 
-           11,    0,   12,   13,   14,  100,  101,   17,   18,    0, 
-            0,    0,    0,  102,  103,  104,   22,   23,   24,   25, 
-            0,    0,  105,    0,    0,    0,    0,    0,    0,  106, 
-            0,    0,   31,   32,   33,    0,   34,   35,   36,   37, 
-           38,   39,    0,   40,    0,    0,  109,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,  898,    0,    0,  112, 
-            0,    0,   46,   47,    0,   48,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,    0,   52,   53,   54,   55,   56,   57, 
-            0,    0,    0,   58,    0,   59,   60,    0,   61,   62, 
-          121,  122,  123,  124,  125,  126,  127,  128,    0,    0, 
-          129,  130,  131,  132,  133,    0,    0,  134,  135,  136, 
-          137,  138,  139,  140,    0,    0,  141,  142,  143,  201, 
-          202,  203,  204,  148,  149,  150,  151,  152,  153,  154, 
-          155,  156,  157,  158,  159,  205,  206,  207,    0,  208, 
-          164,  269,    0,  209,    0,    0,    0,  166,  167,    0, 
-          168,  169,  170,  171,  172,  173,  174,    0,    0,  175, 
-          176,    0,    0,    0,  177,  178,  179,  180,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          182,  183,    0,  184,  185,  186,  187,  188,  189,  190, 
-          191,  192,  193,  194,    0,    0,  195,   52,  121,  122, 
-          123,  124,  125,  126,  127,  128,    0,    0,  129,  130, 
-          131,  132,  133,    0,    0,  134,  135,  136,  137,  138, 
-          139,  140,    0,    0,  141,  142,  143,  201,  202,  203, 
-          204,  148,  149,  150,  151,  152,  153,  154,  155,  156, 
-          157,  158,  159,  205,  206,  207,    0,  208,  164,    0, 
-            0,  209,    0,    0,    0,  166,  167,    0,  168,  169, 
-          170,  171,  172,  173,  174,    0,    0,  175,  176,    0, 
-            0,    0,  177,  178,  179,  180,    0,    0,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,  182,  183, 
-            0,  184,  185,  186,  187,  188,  189,  190,  191,  192, 
-          193,  194,    0,    0,  195,   52,  121,  122,  123,  124, 
-          125,  126,  127,  128,    0,    0,  129,  130,  131,  132, 
-          133,    0,    0,  134,  135,  136,  137,  138,  139,  140, 
-            0,    0,  141,  142,  143,  144,  145,  146,  147,  148, 
+           60,    0,   61,   62,  122,  123,  124,  125,  126,  127, 
+          128,  129,    0,    0,  130,  131,  132,  133,  134,    0, 
+            0,  135,  136,  137,  138,  139,  140,  141,    0,    0, 
+          142,  143,  144,  202,  203,  204,  205,  149,  150,  151, 
+          152,  153,  154,  155,  156,  157,  158,  159,  160,  206, 
+          207,  208,    0,  209,  165,  270,    0,  210,    0,    0, 
+            0,  167,  168,    0,  169,  170,  171,  172,  173,  174, 
+          175,    0,    0,  176,  177,    0,    0,    0,  178,  179, 
+          180,  181,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  183,  184,    0,  185,  186,  187, 
+          188,  189,  190,  191,  192,  193,  194,  195,    0,    0, 
+          196,   52,  122,  123,  124,  125,  126,  127,  128,  129, 
+            0,    0,  130,  131,  132,  133,  134,    0,    0,  135, 
+          136,  137,  138,  139,  140,  141,    0,    0,  142,  143, 
+          144,  202,  203,  204,  205,  149,  150,  151,  152,  153, 
+          154,  155,  156,  157,  158,  159,  160,  206,  207,  208, 
+            0,  209,  165,    0,    0,  210,    0,    0,    0,  167, 
+          168,    0,  169,  170,  171,  172,  173,  174,  175,    0, 
+            0,  176,  177,    0,    0,    0,  178,  179,  180,  181, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,  183,  184,    0,  185,  186,  187,  188,  189, 
+          190,  191,  192,  193,  194,  195,    0,    0,  196,   52, 
+          122,  123,  124,  125,  126,  127,  128,  129,    0,    0, 
+          130,  131,  132,  133,  134,    0,    0,  135,  136,  137, 
+          138,  139,  140,  141,    0,    0,  142,  143,  144,  145, 
+          146,  147,  148,  149,  150,  151,  152,  153,  154,  155, 
+          156,  157,  158,  159,  160,  161,  162,  163,    0,  164, 
+          165,   36,   37,  166,   39,    0,    0,  167,  168,    0, 
+          169,  170,  171,  172,  173,  174,  175,    0,    0,  176, 
+          177,    0,    0,    0,  178,  179,  180,  181,    0,    0, 
+            0,    0,    0,  182,    0,    0,    0,    0,    0,    0, 
+          183,  184,    0,  185,  186,  187,  188,  189,  190,  191, 
+          192,  193,  194,  195,    0,    0,  196,  122,  123,  124, 
+          125,  126,  127,  128,  129,    0,    0,  130,  131,  132, 
+          133,  134,    0,    0,  135,  136,  137,  138,  139,  140, 
+          141,    0,    0,  142,  143,  144,  202,  203,  204,  205, 
           149,  150,  151,  152,  153,  154,  155,  156,  157,  158, 
-          159,  160,  161,  162,    0,  163,  164,   36,   37,  165, 
-           39,    0,    0,  166,  167,    0,  168,  169,  170,  171, 
-          172,  173,  174,    0,    0,  175,  176,    0,    0,    0, 
-          177,  178,  179,  180,    0,    0,    0,    0,    0,  181, 
-            0,    0,    0,    0,    0,    0,  182,  183,    0,  184, 
+          159,  160,  206,  207,  208,    0,  209,  165,  303,  304, 
+          210,  305,    0,    0,  167,  168,    0,  169,  170,  171, 
+          172,  173,  174,  175,    0,    0,  176,  177,    0,    0, 
+            0,  178,  179,  180,  181,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,  183,  184,    0, 
           185,  186,  187,  188,  189,  190,  191,  192,  193,  194, 
-            0,    0,  195,  121,  122,  123,  124,  125,  126,  127, 
-          128,    0,    0,  129,  130,  131,  132,  133,    0,    0, 
-          134,  135,  136,  137,  138,  139,  140,    0,    0,  141, 
-          142,  143,  201,  202,  203,  204,  148,  149,  150,  151, 
-          152,  153,  154,  155,  156,  157,  158,  159,  205,  206, 
-          207,    0,  208,  164,  302,  303,  209,  304,    0,    0, 
-          166,  167,    0,  168,  169,  170,  171,  172,  173,  174, 
-            0,    0,  175,  176,    0,    0,    0,  177,  178,  179, 
-          180,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-            0,    0,    0,  182,  183,    0,  184,  185,  186,  187, 
-          188,  189,  190,  191,  192,  193,  194,    0,    0,  195, 
-          121,  122,  123,  124,  125,  126,  127,  128,    0,    0, 
-          129,  130,  131,  132,  133,    0,    0,  134,  135,  136, 
-          137,  138,  139,  140,    0,    0,  141,  142,  143,  201, 
-          202,  203,  204,  148,  149,  150,  151,  152,  153,  154, 
-          155,  156,  157,  158,  159,  205,  206,  207,    0,  208, 
-          164,    0,    0,  209,    0,    0,    0,  166,  167,    0, 
-          168,  169,  170,  171,  172,  173,  174,    0,    0,  175, 
-          176,    0,    0,    0,  177,  178,  179,  180,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          182,  183,    0,  184,  185,  186,  187,  188,  189,  190, 
-          191,  192,  193,  194,  628,  552,  195,    0,  629,    0, 
-            0,    0,  166,  167,    0,  168,  169,  170,  171,  172, 
-          173,  174,    0,    0,  175,  176,    0,    0,    0,  177, 
-          178,  179,  180,    0,    0,    0,    0,    0,  263,    0, 
-            0,    0,    0,    0,    0,  182,  183,    0,  184,  185, 
-          186,  187,  188,  189,  190,  191,  192,  193,  194,  630, 
-          558,  195,    0,  631,    0,    0,    0,  166,  167,    0, 
-          168,  169,  170,  171,  172,  173,  174,    0,    0,  175, 
-          176,    0,    0,    0,  177,  178,  179,  180,    0,    0, 
-            0,    0,    0,  263,    0,    0,    0,    0,    0,    0, 
-          182,  183,    0,  184,  185,  186,  187,  188,  189,  190, 
-          191,  192,  193,  194,  655,  552,  195,    0,  656,    0, 
-            0,    0,  166,  167,    0,  168,  169,  170,  171,  172, 
-          173,  174,    0,    0,  175,  176,    0,    0,    0,  177, 
-          178,  179,  180,    0,    0,    0,    0,    0,  263,    0, 
-            0,    0,    0,    0,    0,  182,  183,    0,  184,  185, 
-          186,  187,  188,  189,  190,  191,  192,  193,  194,  657, 
-          558,  195,    0,  658,    0,    0,    0,  166,  167,    0, 
-          168,  169,  170,  171,  172,  173,  174,    0,    0,  175, 
-          176,    0,    0,    0,  177,  178,  179,  180,    0,    0, 
-            0,    0,    0,  263,    0,    0,    0,    0,    0,    0, 
-          182,  183,    0,  184,  185,  186,  187,  188,  189,  190, 
-          191,  192,  193,  194,  910,  552,  195,    0,  911,    0, 
-            0,    0,  166,  167,    0,  168,  169,  170,  171,  172, 
-          173,  174,    0,    0,  175,  176,    0,    0,    0,  177, 
-          178,  179,  180,    0,    0,    0,    0,    0,  263,    0, 
-            0,    0,    0,    0,    0,  182,  183,    0,  184,  185, 
-          186,  187,  188,  189,  190,  191,  192,  193,  194,  912, 
-          558,  195,    0,  913,    0,    0,    0,  166,  167,    0, 
-          168,  169,  170,  171,  172,  173,  174,    0,    0,  175, 
-          176,    0,    0,    0,  177,  178,  179,  180,    0,    0, 
-            0,    0,    0,  263,    0,    0,    0,    0,    0,    0, 
-          182,  183,    0,  184,  185,  186,  187,  188,  189,  190, 
-          191,  192,  193,  194,  941,  558,  195,    0,  942,    0, 
-            0,    0,  166,  167,    0,  168,  169,  170,  171,  172, 
-          173,  174,    0,    0,  175,  176,    0,    0,    0,  177, 
-          178,  179,  180,    0,    0,    0,    0,    0,  263,    0, 
-            0,    0,    0,    0,    0,  182,  183,    0,  184,  185, 
-          186,  187,  188,  189,  190,  191,  192,  193,  194,  565, 
-          552,  195,    0,  566,    0,    0,    0,  166,  167,    0, 
-          168,  169,  170,  171,  172,  173,  174,    0,    0,  175, 
-          176,    0,    0,    0,  177,  178,  179,  180,    0,    0, 
-            0,    0,    0,    0,    0,    0,    0,    0,    0,    0, 
-          182,  183,    0,  184,  185,  186,  187,  188,  189,  190, 
-          191,  192,  193,  194,    0,    0,  195, 
+          195,    0,    0,  196,  122,  123,  124,  125,  126,  127, 
+          128,  129,    0,    0,  130,  131,  132,  133,  134,    0, 
+            0,  135,  136,  137,  138,  139,  140,  141,    0,    0, 
+          142,  143,  144,  202,  203,  204,  205,  149,  150,  151, 
+          152,  153,  154,  155,  156,  157,  158,  159,  160,  206, 
+          207,  208,    0,  209,  165,    0,    0,  210,    0,    0, 
+            0,  167,  168,    0,  169,  170,  171,  172,  173,  174, 
+          175,    0,    0,  176,  177,    0,    0,    0,  178,  179, 
+          180,  181,    0,    0,    0,    0,    0,    0,    0,    0, 
+            0,    0,    0,    0,  183,  184,    0,  185,  186,  187, 
+          188,  189,  190,  191,  192,  193,  194,  195,  633,  559, 
+          196,    0,  634,    0,    0,    0,  167,  168,    0,  169, 
+          170,  171,  172,  173,  174,  175,    0,    0,  176,  177, 
+            0,    0,    0,  178,  179,  180,  181,    0,    0,    0, 
+            0,    0,  264,    0,    0,    0,    0,    0,    0,  183, 
+          184,    0,  185,  186,  187,  188,  189,  190,  191,  192, 
+          193,  194,  195,  658,  553,  196,    0,  659,    0,    0, 
+            0,  167,  168,    0,  169,  170,  171,  172,  173,  174, 
+          175,    0,    0,  176,  177,    0,    0,    0,  178,  179, 
+          180,  181,    0,    0,    0,    0,    0,  264,    0,    0, 
+            0,    0,    0,    0,  183,  184,    0,  185,  186,  187, 
+          188,  189,  190,  191,  192,  193,  194,  195,  660,  559, 
+          196,    0,  661,    0,    0,    0,  167,  168,    0,  169, 
+          170,  171,  172,  173,  174,  175,    0,    0,  176,  177, 
+            0,    0,    0,  178,  179,  180,  181,    0,    0,    0, 
+            0,    0,  264,    0,    0,    0,    0,    0,    0,  183, 
+          184,    0,  185,  186,  187,  188,  189,  190,  191,  192, 
+          193,  194,  195,  914,  553,  196,    0,  915,    0,    0, 
+            0,  167,  168,    0,  169,  170,  171,  172,  173,  174, 
+          175,    0,    0,  176,  177,    0,    0,    0,  178,  179, 
+          180,  181,    0,    0,    0,    0,    0,  264,    0,    0, 
+            0,    0,    0,    0,  183,  184,    0,  185,  186,  187, 
+          188,  189,  190,  191,  192,  193,  194,  195,  916,  559, 
+          196,    0,  917,    0,    0,    0,  167,  168,    0,  169, 
+          170,  171,  172,  173,  174,  175,    0,    0,  176,  177, 
+            0,    0,    0,  178,  179,  180,  181,    0,    0,    0, 
+            0,    0,  264,    0,    0,    0,    0,    0,    0,  183, 
+          184,    0,  185,  186,  187,  188,  189,  190,  191,  192, 
+          193,  194,  195,  945,  559,  196,    0,  946,    0,    0, 
+            0,  167,  168,    0,  169,  170,  171,  172,  173,  174, 
+          175,    0,    0,  176,  177,    0,    0,    0,  178,  179, 
+          180,  181,    0,    0,    0,    0,    0,  264,    0,    0, 
+            0,    0,    0,    0,  183,  184,    0,  185,  186,  187, 
+          188,  189,  190,  191,  192,  193,  194,  195,  566,  553, 
+          196,    0,  567,    0,    0,    0,  167,  168,    0,  169, 
+          170,  171,  172,  173,  174,  175,    0,    0,  176,  177, 
+            0,    0,    0,  178,  179,  180,  181,    0,    0,    0, 
+            0,    0,    0,    0,    0,    0,    0,    0,    0,  183, 
+          184,    0,  185,  186,  187,  188,  189,  190,  191,  192, 
+          193,  194,  195,    0,    0,  196, 
       };
    }
 
    private static final short[] yyCheck1() {
       return new short[] {
 
-            6,    7,   26,    2,   28,  388,   51,  412,  417,  230, 
-           15,   16,   48,   27,   19,  397,    6,    0,  316,    7, 
-           21,   27,    7,  255,   47,   48,    3,    0,   10,  361, 
-           10,    9,   10,   11,   10,   10,   32,   10,   49,   27, 
-           92,  288,   27,   59,  291,    0,   52,  111,   47,   48, 
-          316,    4,    5,   97,   45,  387,   59,  361,   10,   10, 
-           97,   14,   52,  771,   65,   10,   10,   10,   46,   44, 
-           10,   11,  679,    2,    3,  407,  359,  360,   10,  362, 
-          363,  674,  106,  687,   59,   61,   59,   10,   10,  421, 
-          514,  308,  390,   10,   10,  312,   49,  690,   10,  473, 
-           21,   44,  385,  280,   10,   10,  340,   59,   59,   47, 
-          280,  305,   10,   10,  685,   59,   59,   10,  864,   10, 
-           10,   44,  405,  406,   59,   10,  550,   59,   10,  340, 
-           10,  344,  110,   10,  280,   10,  419,   59,   61,   92, 
-          472,  328,   59,   59,   65,  305,  328,   10,  335,  362, 
-          269,   44,  271,  335,   59,   61,   10,   10,   10,  325, 
-           10,   10,   59,   61,   44,   10,   59,  450,   59,   10, 
-           10,   10,   10,   10,   59,  219,    0,  596,   10,   59, 
-          374,  889,  219,  104,  930,   44,   10,  470,  769,  233, 
-          367,  361,  358,  114,  340,   11,   59,  790,  496,  618, 
-           59,   10,   44,   10,  578,   10,  310,  590,   61,  308, 
-          246,  247,  248,  632,  374,  361,   10,   59,  601,    2, 
-            3,    4,    5,   61,   61,    8,    9,   10,   11,   61, 
-          496,   14,   15,   16,  430,   59,   19,   44,  620,   44, 
-          659,  848,  294,  317,   10,  263,  264,   44,   10,  853, 
-           44,  269,   61,   32,   61,  260,   61,  262,  263,  237, 
-          238,  842,   45,   46,  263,   10,   49,  463,   51,  486, 
-          370,  695,   10,   44,  348,  846,  375,  376,  352,  353, 
-           10,   97,   32,   10,   91,   10,   91,  216,  266,  280, 
-          268,  862,  267,  365,   91,   61,  396,  237,  238,   61, 
-          116,  599,  305,  279,   10,  427,   44,  337,  430,   92, 
-          891,  262,  263,  264,   59,  378,  379,  701,  269,  364, 
-           91,  366,  386,  707,  267,  370,  371,  110,  282,  112, 
-          366,   44,   59,  599,  370,  267,   61,  281,  919,  306, 
-          256,  294,  358,  310,  753,  719,  324,  325,  326,  327, 
-          267,  329,  330,   59,  360,   44,  361,  368,   61,  341, 
-          396,   61,  361,  338,  344,  341,    2,    3,    4,    5, 
-            6,  374,    8,  279,  370,  371,  367,  413,   14,  362, 
-          386,  377,  387,  305,  324,  325,  326,  327,  387,  362, 
-          426,  427,  690,  305,  430,  340,  373,  396,  397,  328, 
-          406,  622,  407,  219,  427,  637,  653,  362,  407,   45, 
-          362,  389,  305,   49,  420,  368,  421,  233,  341,  341, 
-           91,  473,  421,  358,  460,  305,  279,  463,  366,  341, 
-          835,  445,  370,  216,  817,  341,  341,  460,  359,  445, 
-          356,  279,  279,  341,  373,  451,  358,  279,  341,  448, 
-          341,  341,  374,  835,  237,  238,   92,  445,  396,  341, 
-          445,  341,  374,   44,  385,  471,  341,  472,  446,  495, 
-          279,  400,  279,  472,  279,  413,  112,  260,  358,  262, 
-          263,  374,  279,  266,  405,  268,  337,  341,  341,  341, 
-          273,  341,  341,  876,  374,  914,  341,  280,  419,  358, 
-          341,  341,  341,  341,  341,  290,  291,  506,  279,  341, 
-           91,  294,  733,  279,  513,  514,  358,  279,  340,  448, 
-          473,  328,  267,  328,  822,  549,  578,  262,  335,  450, 
-          335,  328,  341,  340,  341,  571,  341,  264,  335,   91, 
-          564,  324,  325,  326,  327,  328,  329,  330,  880,  470, 
-           91,  550,  264,  554,  279,  554,  310,  328,  264,  560, 
-          290,  291,  268,  269,  335,  381,  567,  568,  567,  568, 
-          337,   91,  596,   10,   45,  341,    2,    3,  361,  341, 
-          216,  364,    8,  366,  367,  368,   91,  370,  371,  603, 
-          373,   61,  591,  338,  618,  878,  602,  603,  337,  635, 
-          416,  417,  654,   10,  387,  388,  389,  606,  632,  830, 
-          609,  589,  602,  612,   91,  603,  341,  400,  603,   45, 
-          403,  620,   59,  271,  407,  578,  372,   59,  411,  372, 
-          666,  377,   91,  554,  377,  659,  310,  273,  421,  560, 
-           91,  112,  668,  669,  280,   44,  567,  568,  279,  589, 
-          378,  379,   59,  698,  699,  633,  338,  328,  294,  704, 
-           44,   10,   61,  446,  335,  448,   44,  719,   10,   10, 
-          591,   44,   10,  594,  361,  674,  370,  371,  372,   10, 
-          716,  308,  309,  377,  311,  317,  112,  263,  264,  472, 
-          473,  690,  328,  633,  266,  317,  695,  317,  279,  280, 
-          306,  654,  308,  309,  310,  311,  341,   91,  268,  269, 
-           59,  283,  748,   44,   54,   55,  337,   59,   59,  344, 
-          317,   59,  362,   10,   15,   16,  310,   44,   19,   10, 
-           61,  367,  368,  339,  567,  568,  346,  373,    2,    3, 
-            4,    5,   91,  769,    8,  771,  372,  328,  375,  376, 
-           14,  348,  388,  372,  335,  352,  353,  354,  355,   10, 
-           91,  739,    2,    3,  400,  810,  719,  403,    8,   44, 
-          340,  587,   59,  269,   14,  411,  328,  813,   59,  732, 
-          361,   45,  267,  335,  600,   49,  264,  328,  341,  362, 
-          216,  790,   44,   44,  335,  578,   44,   32,   10,  739, 
-           61,   10,  273,  306,   91,   45,  589,  590,  328,  280, 
-           61,  306,  448,   44,   59,  335,  842,  843,  601,   44, 
-           44,  337,  337,  328,  337,  608,  263,  264,   92,  337, 
-          335,   44,   44,   44,  264,   44,  835,  473,  338,  263, 
-           91,  264,   44,   91,  337,  337,  337,  273,  112,   61, 
-          633,  328,   61,  362,  280,  262,  263,  264,  335,   58, 
-          432,  268,  269,  889,  271,  891,  438,  439,  337,  328, 
-          344,  654,  112,  879,  264,  880,  335,  328,  661,   91, 
-          279,  880,   91,  455,  335,  264,  458,  370,  371,   10, 
-          914,  337,  264,  919,  377,  279,  367,  380,  264,  682, 
-          683,  362,  328,   44,   44,  858,  722,   44,   44,  692, 
-          264,  694,   44,  362,  697,  698,  699,  916,  267,   44, 
-          926,  704,  264,  264,  262,  263,  264,  269,  269,  271, 
-          268,  269,  403,  271,  341,    0,  719,  753,   59,   44, 
-          411,  367,  578,   44,  328,   10,   61,  373,  279,  732, 
-           44,  335,  216,  736,  590,  362,  739,  878,   44,   44, 
-          358,  264,  388,  264,  310,  601,  749,  750,  751,  260, 
-           91,  262,  608,  362,  400,  264,  216,  403,  264,  328, 
-          267,  262,  263,  264,   44,  411,  335,  268,  269,  358, 
-          271,    0,  362,  280,   59,  264,   44,  328,   44,  310, 
-           44,   10,   44,  341,  335,  577,   44,  264,   44,  273, 
-          341,  264,   44,   10,  344,  798,  280,  271,  654,  362, 
-          340,  271,  448,   59,  362,  661,  598,  810,  279,  280, 
-          294,  279,  341,  273,  817,  362,  344,   10,  344,   58, 
-          280,  328,  370,  371,  372,  828,  682,  683,  335,  377, 
-           59,  344,   67,  340,  264,   91,  692,    5,  694,  358, 
-          341,  697,   59,    6,  328,  822,  862,  279,  280,  916, 
-          279,  280,  685,  645,  361,  858,   10,  328,   71,   14, 
-          328,  362,  881,  719,  335,  668,   59,  335,  328,  340, 
-          341,  448,  340,  876,   91,  848,  732,  880,   -1,  882, 
-          736,  673,   -1,  367,  368,  888,   -1,   -1,   -1,  373, 
-          361,   -1,   -1,  749,  750,  751,  328,   -1,   91,  328, 
-           -1,   -1,   -1,  335,  388,   59,  335,  367,  340,  341, 
-           -1,  340,  341,  373,   -1,   -1,  400,  608,   -1,  403, 
-          685,    0,  687,   -1,   -1,  690,  267,  411,  388,  361, 
-           -1,   10,  361,   -1,   -1,  727,   -1,   91,   -1,   -1, 
-          400,   -1,  798,  403,  590,  306,   10,  308,  309,  310, 
-          311,  411,   -1,   -1,  306,  601,  308,  309,  310,  311, 
-           44,  817,  608,   -1,  448,   -1,   -1,   -1,   10,   -1, 
-          661,   -1,  828,   -1,   -1,   59,  768,  262,  263,  264, 
-           59,  773,  267,  268,  269,   -1,  271,  328,  448,  473, 
-           10,  682,  683,   -1,  335,   59,  281,   -1,   -1,  340, 
-           -1,  692,  858,  694,   -1,   -1,  697,   91,  293,  294, 
-          295,  296,  297,   -1,   -1,  661,   -1,   59,   -1,   -1, 
-          876,  370,  371,  372,   44,   -1,  882,   91,  377,   -1, 
-           -1,   -1,  888,  262,  263,  264,  682,  683,  267,  268, 
-          269,   61,  271,   -1,   -1,  736,  692,   -1,  694,   91, 
-          267,  697,  281,  282,   -1,   -1,  341,   -1,  749,  750, 
-          751,  290,  291,  280,  293,  294,  295,  296,  297,   -1, 
-           -1,   91,  328,   -1,  267,   -1,   -1,  362,  306,  335, 
-          308,  309,  310,  311,  340,   -1,   -1,  280,  853,   -1, 
-          736,  370,  371,  372,  578,   -1,   -1,  862,  377,  864, 
-           59,   -1,  358,  749,  750,  751,  590,  798,   -1,   -1, 
-           -1,  328,  341,  267,   -1,  344,   -1,  601,  335,   -1, 
-          370,  371,  372,  340,  608,   -1,  280,  377,   15,   16, 
-          590,   -1,   19,  362,   -1,  328,   -1,  828,  370,  371, 
-          372,  601,  335,   -1,  361,  377,    0,  340,  608,   -1, 
-           -1,   -1,  798,   -1,   41,   42,   10,   -1,   -1,   -1, 
-           47,   48,   -1,   50,   51,  930,   -1,   -1,  361,   -1, 
-          654,  817,   -1,   -1,  328,   -1,   -1,  661,  662,   -1, 
-           -1,  335,  828,  262,  263,  264,   -1,   -1,  267,  268, 
-          269,  882,  271,   -1,   -1,   -1,  280,  888,  682,  683, 
-           -1,  661,  281,  267,   -1,   59,   10,  361,  692,   -1, 
-          694,  290,  291,  697,  293,  294,  295,  296,  297,   -1, 
-           -1,   -1,  682,  683,   -1,  267,   -1,   -1,   -1,   -1, 
-          876,   -1,  692,   -1,  694,  719,  882,  697,   -1,   -1, 
-           44,   -1,  888,   -1,  328,   44,   -1,   -1,  732,   -1, 
-           -1,  335,  736,   -1,   -1,   -1,  340,   61,   -1,  279, 
-            4,    5,  341,   -1,  328,  749,  750,  751,   -1,   -1, 
-           14,  335,  732,   -1,  358,   -1,  736,  361,  293,  294, 
-          295,  296,  297,  362,    0,   -1,  328,   91,   -1,  749, 
-          750,  751,   91,  335,   10,   -1,   -1,   41,   42,   -1, 
-           -1,   -1,   -1,   47,   48,   49,   50,   -1,  328,   -1, 
-           -1,   -1,   -1,   -1,  798,  335,   -1,   -1,  262,  263, 
-          264,  341,   -1,   -1,  268,  269,   -1,  271,   44,   -1, 
-           -1,   -1,   -1,  817,   -1,   -1,   -1,  306,  798,  308, 
-          309,  310,  311,   59,  828,   -1,   -1,  306,   92,  308, 
-          309,  310,  311,   -1,   -1,  508,  509,  817,   -1,  246, 
-          247,  248,  249,   -1,   -1,   -1,   -1,   -1,  828,   -1, 
-          339,   -1,   -1,  260,  858,  262,  263,   -1,  347,   44, 
-          339,   -1,   -1,   44,  271,   -1,   -1,   -1,  347,  348, 
-          349,  350,  876,  262,  263,  264,   -1,  341,  882,  268, 
-          269,   -1,  271,   -1,  888,   -1,   -1,   -1,  262,  263, 
-          264,   -1,   -1,  267,  268,  269,  876,  271,  362,   -1, 
-           -1,   -1,  882,   -1,   -1,   -1,   91,  281,  888,   -1, 
-           91,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1,  293, 
-          294,  295,  296,  297,  331,  332,  333,  334,  335,  336, 
-          337,  338,  339,  340,  341,  342,  343,  344,  345,  346, 
-          347,  348,  349,  350,  351,  352,  353,  354,  355,  356, 
-           -1,   -1,  341,   -1,  361,  279,  280,  364,   -1,  366, 
-          279,  280,   -1,  370,  371,   -1,   -1,  341,   -1,   -1, 
-           -1,   -1,  306,  362,  308,  309,  310,  311,   -1,   -1, 
-          387,    0,  246,  247,  248,  249,   -1,   10,  362,  396, 
-           -1,   10,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          407,  408,  409,  410,  328,  339,  413,  271,   -1,  328, 
-           -1,  335,   -1,  347,  421,   -1,  335,  341,   -1,  426, 
-          427,  340,   -1,  430,   -1,   44,  262,  263,  264,   -1, 
-          294,  267,  268,  269,   -1,  271,   59,  361,   -1,   -1, 
-           59,   -1,  361,   -1,   -1,  452,  453,  454,   -1,   -1, 
-           -1,   -1,   -1,  460,   -1,   -1,  463,  293,  294,  295, 
-          296,  297,   -1,   -1,   -1,  472,   -1,  331,  332,  333, 
-          334,  335,  336,  337,  338,  339,  340,  341,  342,  343, 
-          344,  345,  346,  347,  348,  349,  350,  351,  352,  353, 
-          354,  355,  356,   -1,  279,  280,   -1,   -1,  279,  280, 
-           10,  774,  338,   -1,  368,  341,   -1,   -1,   -1,  782, 
-          783,   -1,  785,   -1,  787,   -1,  789,   -1,  791,  792, 
-           -1,   -1,   -1,   -1,   -1,   -1,  362,   -1,  677,   -1, 
-           -1,   -1,  396,   -1,   -1,   -1,   -1,   -1,  687,   -1, 
-           -1,  690,   -1,  328,  408,  409,  410,  328,   -1,  413, 
-          335,   -1,   -1,   63,  335,  340,   -1,   -1,   -1,  340, 
-           -1,   -1,  426,  427,  571,   -1,  430,   -1,   -1,  306, 
-           -1,  308,  309,  310,  311,   -1,  361,   -1,   -1,   -1, 
-          361,   -1,  306,   -1,  308,  309,  310,  311,  452,  453, 
-          454,   -1,   -1,   -1,   -1,    0,  460,   -1,   -1,  463, 
-           -1,   -1,  339,  340,   -1,   10,   -1,   -1,   -1,  473, 
-          347,  348,  349,  350,   -1,  339,   -1,   -1,   -1,   -1, 
-           -1,  894,  895,  896,  897,  349,  350,  900,  635,  902, 
-          903,  904,  905,   -1,   -1,   -1,   -1,   -1,   -1,   44, 
-           -1,   -1,   -1,  262,  263,  264,   10,   -1,  267,  268, 
-          269,   -1,  271,   58,   59,   -1,   -1,   -1,   63,  666, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  940,   -1,   -1, 
-          943,  944,  945,  946,  293,  294,  295,  296,  297,   -1, 
-          953,   -1,   -1,  306,   -1,  308,  309,  310,  311,   -1, 
-           -1,  698,  699,   -1,   -1,   59,   -1,  704,  705,   -1, 
-           -1,   -1,  851,   -1,  853,  328,  855,  571,  715,  716, 
-          859,   -1,  335,  720,  578,  864,  339,  340,   -1,  338, 
-           -1,   -1,  341,  317,  347,  348,  349,  350,  322,  323, 
-           -1,   -1,   -1,   -1,   -1,  742,  743,  744,    0,  333, 
-          334,  748,   -1,  362,   -1,   -1,   -1,   -1,   10,   -1, 
-           -1,   -1,   -1,   -1,  348,   -1,  350,   -1,  352,  353, 
-          354,  355,  356,  357,  358,   -1,  360,   -1,   -1,   -1, 
-           -1,  635,  921,   -1,   -1,   -1,   -1,   -1,   -1,  928, 
-           -1,  930,   44,  932,   -1,   -1,   -1,   -1,   -1,   -1, 
-          654,   -1,  799,   -1,   -1,   -1,   58,   59,   -1,   61, 
-          949,   63,  666,  810,   -1,   -1,  813,  317,  318,  319, 
-          320,  321,  322,  323,  324,  325,  326,  327,   -1,  329, 
-          330,   -1,   -1,  333,  334,  832,   -1,   -1,   -1,   91, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  348,   -1, 
-          350,  705,  352,  353,  354,  355,  356,  357,  358,   -1, 
-          360,  715,  716,   -1,   -1,  719,  720,  262,  263,  264, 
-           -1,    0,  267,  268,  269,   -1,  271,   -1,  732,   -1, 
-           -1,   10,   -1,  880,   -1,   -1,  281,  282,  742,  743, 
-          744,   -1,   -1,   -1,  748,  290,  291,   -1,  293,  294, 
-          295,  296,  297,   -1,  317,   -1,   -1,   -1,   -1,   -1, 
-          305,   -1,   -1,   -1,   -1,   44,   -1,   -1,   -1,   -1, 
-          333,  334,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   58, 
-           59,   -1,   61,   -1,   63,  348,   -1,  350,   -1,  352, 
-          353,  354,  355,  338,   -1,  799,  341,   -1,   -1,  344, 
-           -1,  346,  306,  317,  308,  309,  310,  311,   -1,  813, 
-           -1,   -1,   91,   -1,   -1,   -1,   -1,  362,   -1,  333, 
-          334,   -1,   -1,   -1,  328,   -1,   -1,   -1,  832,  374, 
-           -1,  335,   -1,   -1,  348,  339,  340,   -1,  352,  353, 
-          354,  355,   -1,  347,  348,  349,  350,   10,   -1,   -1, 
-           -1,   -1,   -1,   -1,  858,  257,  258,  259,   -1,  261, 
-          262,  263,  264,  265,  266,  267,  268,  269,  270,  271, 
-          272,  273,  274,  275,  276,  277,  278,  279,  280,  281, 
-          282,  283,  284,  285,  286,  287,  288,  289,  290,  291, 
-          292,  293,  294,  295,  296,  297,   59,  299,   -1,   -1, 
-          302,  303,  304,  305,  306,  307,  308,  309,  310,  311, 
-          312,  313,  314,  315,  316,  317,  318,  319,  320,  321, 
-          322,  323,  324,  325,  326,  327,  328,  329,  330,   -1, 
-           -1,  333,  334,  335,  336,  337,  338,  339,  340,  341, 
-          342,  343,  344,  345,  346,  347,  348,  349,  350,  351, 
-          352,  353,  354,  355,  356,  357,  358,  359,  360,  361, 
-          362,   -1,  364,  365,  366,  367,  368,  369,   -1,   -1, 
-           10,  373,  374,  375,  376,   -1,  378,  379,  257,  258, 
-          259,   -1,  261,  262,  263,  264,  265,  266,  267,  268, 
-          269,  270,  271,  272,  273,  274,  275,  276,  277,  278, 
-          279,  280,  281,  282,  283,  284,  285,  286,  287,  288, 
-          289,  290,  291,  292,  293,  294,  295,  296,  297,   59, 
-          299,   -1,   -1,  302,  303,  304,  305,  306,  307,  308, 
-          309,  310,  311,  312,  313,  314,  315,  316,  317,  318, 
-          319,  320,  321,  322,  323,  324,  325,  326,  327,  328, 
-          329,  330,   -1,   -1,  333,  334,  335,  336,  337,  338, 
-          339,  340,  341,  342,  343,  344,  345,  346,  347,  348, 
-          349,  350,  351,  352,  353,  354,  355,  356,  357,  358, 
-          359,  360,  361,  362,   -1,  364,  365,  366,  367,  368, 
-          369,    0,   -1,   -1,  373,  374,  375,  376,   -1,  378, 
-          379,   10,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  306,   -1,  308,  309,  310, 
-          311,   -1,   -1,   -1,   -1,   44,   -1,  306,   -1,  308, 
-          309,  310,  311,  306,   -1,  308,  309,  310,  311,   58, 
-           59,   -1,   61,   -1,   63,   -1,   -1,   -1,  339,  340, 
-           -1,   -1,   -1,   -1,   -1,  328,  347,  348,  349,  350, 
-          339,   -1,  335,   -1,   -1,   -1,  339,  340,  347,  348, 
-          349,  350,   91,   -1,  347,  348,  349,  350,   -1,   -1, 
-           -1,  306,  307,   -1,   -1,  310,   -1,   -1,   -1,  314, 
-          315,   -1,  317,  318,  319,  320,  321,  322,  323,   -1, 
-           -1,  326,  327,   -1,    0,   -1,  331,  332,  333,  334, 
-           -1,   -1,   -1,   -1,   10,  340,   -1,   -1,   -1,   -1, 
-           -1,   -1,  347,  348,   -1,  350,  351,  352,  353,  354, 
-          355,  356,  357,  358,  359,  360,   -1,   -1,  363,  306, 
-           -1,  308,  309,  310,  311,   -1,   -1,   -1,   44,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  306,   -1,  308,  309, 
-          310,  311,   58,   59,   -1,   61,   -1,   63,   -1,   -1, 
-           -1,  317,  339,   -1,   -1,   -1,   -1,   -1,  328,   -1, 
-          347,  348,  349,  350,   -1,  335,   -1,  333,  334,  339, 
-          340,   -1,   -1,   -1,   -1,   91,   -1,  347,  348,  349, 
-          350,   -1,  348,   -1,  350,   -1,  352,  353,  354,  355, 
-           -1,   10,  358,   -1,  360,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,  258, 
-          259,   -1,  261,  262,  263,  264,  265,  266,  267,  268, 
-          269,  270,  271,  272,  273,  274,  275,  276,  277,  278, 
-           59,  280,  281,  282,  283,  284,  285,  286,  287,  288, 
-          289,  290,  291,  292,  293,  294,  295,  296,  297,   -1, 
-          299,   -1,   -1,  302,  303,  304,  305,  306,  307,  308, 
-          309,  310,  311,  312,  313,  314,  315,  316,  317,  318, 
-          319,  320,  321,  322,  323,  324,  325,  326,  327,  328, 
-          329,  330,   -1,   -1,  333,  334,  335,  336,   -1,  338, 
+            6,    7,   26,    2,   28,   51,  398,   15,   16,  389, 
+          317,   19,  231,   93,   27,    7,  413,   21,  256,    7, 
+           10,   27,    9,   10,   11,    3,    6,   32,  362,   47, 
+           48,   10,    4,    5,   10,   27,   10,   47,  289,   27, 
+           10,  292,   14,  418,  280,    0,   52,   10,   47,   48, 
+            0,    0,  360,  361,  388,  363,  364,   59,  112,   46, 
+           10,   65,   98,  317,   98,  367,   10,   10,   59,   59, 
+           10,  682,   52,  474,  408,   10,   11,   49,  386,   10, 
+           59,   44,  688,  107,  391,   59,   10,   21,  422,   59, 
+           11,    2,    3,   10,   10,  677,   59,  280,  406,  407, 
+           10,   49,  308,   10,   44,   10,   10,   10,   10,   59, 
+          690,  693,  420,   10,   10,  281,   59,   44,   10,   59, 
+           10,   93,  309,  704,  111,  361,  313,   44,   59,  710, 
+          775,   65,   10,  340,   61,   59,   10,  305,   10,  473, 
+           10,  305,   10,  451,   61,   61,    0,   44,   10,   10, 
+          868,   61,   59,  515,   59,   10,   10,  340,   10,   10, 
+           10,  431,   59,  471,   10,   10,   10,   10,  325,  375, 
+          376,  105,   10,   10,   10,  344,   10,   98,  361,   10, 
+          581,  115,  308,  309,  220,  311,  220,  378,  379,  551, 
+          497,  371,   44,  362,  464,  773,  117,   59,  234,   44, 
+           44,  358,  368,  340,   59,   59,  374,   44,   59,   61, 
+          374,   61,  794,  593,   59,  295,  934,  397,   61,  361, 
+            2,    3,    4,    5,  604,   59,    8,    9,   10,   11, 
+           61,  623,   14,   15,   16,   10,   10,   19,  269,   91, 
+          271,  852,   10,  497,  850,   10,   44,   44,  893,  375, 
+          376,  238,  239,  261,  372,  263,  264,  310,   45,  377, 
+          866,   59,   59,   45,   46,  264,  317,   49,  846,   51, 
+          372,   10,  599,  428,  306,  377,  431,  857,  310,   32, 
+          267,   10,  269,  262,  263,  264,   61,   61,   32,  268, 
+          269,  328,  271,   61,  621,  602,   61,  348,  335,  220, 
+          487,  352,  353,  238,  239,  328,  217,  365,  635,  290, 
+          291,   93,  335,  234,  282,  305,   59,  895,  337,  365, 
+           59,  367,  723,  295,  267,  371,  372,  267,   44,  111, 
+           59,  113,  256,  387,   61,  662,  698,   61,  325,  326, 
+          327,  328,  305,  330,  331,  923,  290,  291,  602,   44, 
+          281,  341,  279,  337,  362,  361,  358,  367,  340,  264, 
+          337,  371,  341,  362,  269,  370,  371,  358,  344,  279, 
+          264,  341,  377,    2,    3,    4,    5,    6,  341,    8, 
+          388,  387,  757,  362,  374,   14,  693,  397,  362,  388, 
+          325,  326,  327,  328,  474,  358,  374,  369,  397,  398, 
+          408,  407,  640,  390,  414,  656,  625,  362,  305,  408, 
+          428,  374,  362,  362,  422,  421,   45,  719,  329,  310, 
+           49,  369,  267,  422,  341,  341,  360,  279,  280,  279, 
+          264,  341,  356,  446,  341,  217,  279,  341,  341,  341, 
+          446,  821,  839,  461,  341,  341,  452,  839,  279,  341, 
+          449,  341,  386,  496,  446,   10,  238,  239,  446,  305, 
+          447,  382,  340,  374,   93,  473,  472,  341,   45,  341, 
+          262,  341,  406,  341,  473,   44,  328,  374,   10,  261, 
+          341,  263,  264,  335,  113,  267,  420,  269,  340,  341, 
+          401,  341,  274,  338,  281,  341,  417,  418,  341,  281, 
+          880,  581,  474,  341,  279,  279,   61,   10,  507,  361, 
+          341,  279,  358,  295,  279,  514,  515,  451,  737,  826, 
+          378,  379,   91,  262,  263,  264,  550,   59,  374,  268, 
+          269,   61,  271,  262,  263,  264,  113,  471,  449,  268, 
+          269,  565,  271,  325,  326,  327,  328,  329,  330,  331, 
+          884,  555,  551,  337,  267,   59,  555,  561,   61,   44, 
+          358,  358,  305,  271,  568,  569,  341,  341,  310,  568, 
+          569,  284,  279,  341,  882,  599,  341,  657,  263,  264, 
+          362,  368,   44,  365,  269,  367,  368,  369,  217,  371, 
+          372,  918,  374,  606,   44,  594,   44,  621,   10,  605, 
+          606,   10,  341,  268,  269,  592,  388,  389,  390,  581, 
+          609,  635,  341,  612,  606,  834,  615,  338,  606,  401, 
+           91,  555,  404,  362,  623,  605,  408,  561,  671,  672, 
+          412,  374,  337,  362,  568,  569,  361,   10,  662,  317, 
+          422,  263,  264,  723,  317,  274,  317,   59,  341,  636, 
+           59,  362,  281,   54,   55,  701,  702,  592,   10,  310, 
+          594,  707,  344,  597,   44,  447,  295,  449,  372,  590, 
+          372,   44,   48,  719,    2,    3,    4,    5,  677,   91, 
+            8,   44,  603,  568,  569,  657,   14,  346,   61,  269, 
+           44,  473,  474,  340,  693,  267,  264,  274,  341,  698, 
+          329,  636,  362,   44,  281,   59,   32,   59,   44,  306, 
+          279,  280,   10,   91,   61,  306,   59,   45,   91,   44, 
+          433,   49,   91,   44,  279,   44,  439,  440,  337,  337, 
+          773,   10,  775,  337,  337,  267,   44,   91,   61,  368, 
+          369,   44,  264,  456,  338,  374,  459,  370,  371,  263, 
+          337,  723,   10,  337,  377,   91,  743,  380,   10,  328, 
+          389,   59,  337,  264,  736,   93,  335,  362,  814,   44, 
+           10,  340,  401,   58,  337,  404,  279,  370,  371,  372, 
+           59,  264,   91,  412,  377,  113,  341,  344,    2,    3, 
+           44,  368,  361,   91,    8,  794,  264,  264,  337,  581, 
+           14,   59,  362,  846,  847,  726,  338,   59,  743,   44, 
+          592,  593,   10,  306,   44,  308,  309,  310,  311,   59, 
+          449,  306,  604,  308,  309,  310,  311,  404,  264,  611, 
+          264,   45,   44,   91,   44,  412,  757,   91,  341,  362, 
+          839,   44,   44,   44,  306,  474,  308,  309,  310,  311, 
+          893,   44,  895,   61,  636,  267,   91,  328,  267,   44, 
+           44,   59,   91,   10,  335,  264,  358,  580,  362,  264, 
+          310,  247,  248,  249,  264,  657,  884,  883,  264,   44, 
+          923,  358,  664,  362,   10,  884,    2,    3,  601,  217, 
+          862,   44,    8,   91,  918,  310,  264,   44,   10,  113, 
+           44,   15,   16,  685,  686,   19,  279,  280,   44,   44, 
+          262,  263,  264,  695,   61,  697,  328,  269,  700,  701, 
+          702,  920,   91,  335,  930,  707,  280,   91,  340,   45, 
+          264,   44,   44,   59,   44,  648,   10,  719,  370,  371, 
+          372,  723,   91,  279,   91,  377,  274,   91,  882,   61, 
+          328,  264,  581,  281,  736,  328,   44,  335,  740,  328, 
+          271,  743,  335,  676,  593,   91,  335,  295,  341,  267, 
+           44,  753,  754,  755,  328,  604,  370,  371,  372,   91, 
+          344,  335,  611,  377,  340,  264,  340,   61,  361,  268, 
+          269,  367,  328,  271,  344,  371,   44,  113,   44,  335, 
+          362,  329,  341,  217,  358,  362,    0,  361,   58,  267, 
+          358,  263,  264,   59,   10,  344,   10,   91,  731,  328, 
+          802,  397,  280,   10,  264,  279,  335,  344,  657,  269, 
+          328,  271,  814,  264,  611,  664,   67,  335,  414,  821, 
+          368,  369,    5,   91,  279,   91,  374,  920,   10,    6, 
+          832,  427,  428,  826,  688,  431,  685,  686,  866,  772, 
+          274,  389,   10,   59,  777,   59,  695,  281,  697,  267, 
+          328,  700,   59,  401,  328,  885,  404,  335,   71,   14, 
+          862,  335,  340,  671,  412,  461,  340,  664,  464,  852, 
+          449,   -1,   -1,  328,  723,   91,   44,   59,  880,  328, 
+          335,  217,  884,  361,  886,   44,  335,  736,  685,  686, 
+          892,  740,   -1,   61,   -1,  329,   -1,   -1,  695,   -1, 
+          697,  449,   -1,  700,  753,  754,  755,   -1,   -1,   91, 
+          328,   -1,  279,  280,   10,  279,  280,  335,    0,   -1, 
+           -1,  267,   -1,   91,   -1,   -1,  474,  261,   10,  263, 
+           -1,   -1,   91,  367,  368,  370,  371,  372,  274,  328, 
+          374,   -1,  377,  740,  328,  281,  335,  279,  280,   10, 
+           -1,  335,   -1,  802,   -1,  389,  753,  754,  755,  328, 
+           -1,  328,   44,   59,  328,   -1,  335,  401,  335,   -1, 
+          404,  335,  821,  340,  341,   -1,  572,   59,  412,   -1, 
+           -1,   -1,  328,  832,   -1,  279,  317,   -1,   -1,  335, 
+           -1,   -1,   -1,  329,  361,   91,  328,  361,   59,  370, 
+          371,  372,   -1,  335,   -1,  802,  377,   -1,  340,  341, 
+           -1,  279,  280,  862,   -1,  449,   -1,  348,  370,  371, 
+          372,  352,  353,  354,  355,  377,   -1,   10,   -1,  361, 
+           -1,  880,  368,  581,  328,  832,   -1,  886,  374,   -1, 
+           -1,  335,  638,  892,   -1,  593,  340,  341,  262,  263, 
+          264,  267,   -1,  389,  268,  269,  604,  271,   -1,   -1, 
+          328,   44,  328,  611,  280,  401,   -1,  335,  404,  335, 
+           -1,   -1,  340,  669,  340,   -1,  412,   -1,   61,  293, 
+          294,  295,  296,  297,   -1,  267,   -1,   -1,   44,  886, 
+           -1,   -1,  358,  361,   -1,  892,   -1,   -1,  280,  306, 
+           -1,  308,  309,  310,  311,   -1,   -1,   -1,   91,  657, 
+           -1,  279,  328,  449,   -1,   -1,  664,  665,   -1,  335, 
+          279,  328,   -1,   -1,  720,   -1,   -1,  341,  335,   -1, 
+           -1,   -1,  339,  340,   -1,   91,   -1,  685,  686,   -1, 
+          347,  348,  349,  350,   -1,  361,  328,  695,  362,  697, 
+           -1,   -1,  700,  335,   -1,   -1,  752,   -1,  340,  593, 
+          328,  293,  294,  295,  296,  297,   -1,  335,   -1,  328, 
+          604,  267,   -1,  341,   -1,  723,  335,  611,   -1,  361, 
+          262,  263,  264,   -1,  280,  267,  268,  269,  736,  271, 
+           15,   16,  740,   -1,   19,   -1,   -1,   -1,   -1,  281, 
+          282,   -1,   -1,   -1,   -1,  753,  754,  755,  290,  291, 
+           -1,  293,  294,  295,  296,  297,   41,   42,   -1,   -1, 
+           -1,  817,   47,   48,   -1,   50,   51,   -1,   -1,   -1, 
+          664,   -1,  328,   -1,  262,  263,  264,   -1,   -1,  335, 
+          268,  269,   -1,  271,  340,  306,   -1,  308,  309,  310, 
+          311,  685,  686,   -1,  802,   -1,   -1,  593,   -1,  341, 
+           -1,  695,  344,  697,  346,  361,  700,  328,  604,   -1, 
+           -1,   -1,   -1,  821,  335,  611,   -1,   -1,  339,  340, 
+          362,   -1,   10,   -1,  832,  719,  347,  348,  349,  350, 
+          688,   -1,  690,   -1,   -1,  693,  279,    0,   -1,   -1, 
+           -1,   -1,  736,   -1,   -1,   -1,  740,   10,   -1,   -1, 
+           -1,   -1,   -1,  341,  862,   -1,   44,   -1,   -1,  753, 
+          754,  755,   -1,  279,  280,   -1,   -1,   -1,  664,   -1, 
+           -1,   -1,  880,   61,  362,   -1,   -1,   -1,  886,   -1, 
+           -1,   44,   -1,   -1,  892,  328,   -1,   -1,   -1,  685, 
+          686,   -1,  335,   -1,   -1,   -1,   59,   -1,  341,  695, 
+           -1,  697,   -1,   91,  700,    4,    5,   -1,  802,   -1, 
+           -1,   -1,  328,   -1,   -1,   14,   -1,   -1,   -1,  335, 
+           -1,   -1,   -1,    0,  340,   -1,   -1,  821,  262,  263, 
+          264,   -1,   -1,   10,  268,  269,   -1,  271,  832,   -1, 
+           -1,   -1,   41,   42,  740,  361,   -1,   -1,   47,   48, 
+           49,   50,   -1,   -1,   -1,   -1,   -1,  753,  754,  755, 
+           -1,   -1,  247,  248,  249,  250,   -1,   44,  306,   -1, 
+          308,  309,  310,  311,   -1,   -1,  261,   -1,  263,  264, 
+           -1,   58,   59,   -1,   -1,   -1,  880,  272,   -1,   -1, 
+           -1,   -1,  886,   -1,   93,   -1,   -1,   -1,  892,  857, 
+           -1,  339,  509,  510,   -1,   -1,  802,  341,  866,   -1, 
+          868,  349,  350,  306,   -1,  308,  309,  310,  311,   -1, 
+           -1,   -1,   -1,   -1,   -1,  821,   10,   -1,  362,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  832,   -1,   -1,   -1, 
+          306,   -1,  308,  309,  310,  311,  339,  332,  333,  334, 
+          335,  336,  337,  338,  339,  340,  341,  342,  343,  344, 
+          345,  346,  347,  348,  349,  350,  351,  352,  353,  354, 
+          355,  356,  357,  339,   -1,   -1,  934,  362,   -1,   63, 
+          365,  347,  367,   -1,  880,   -1,  371,  372,   -1,   10, 
+          886,  279,   -1,   -1,   -1,   -1,  892,   -1,   -1,  262, 
+          263,  264,   -1,  388,  267,  268,  269,   -1,  271,   -1, 
+           -1,  306,  397,  308,  309,  310,  311,   -1,  281,  282, 
+           -1,   -1,   -1,  408,  409,  410,  411,  290,  291,  414, 
+          293,  294,  295,  296,  297,   -1,   -1,  422,   59,   -1, 
+          328,   -1,  427,  428,  339,   -1,  431,  335,  247,  248, 
+          249,  250,  347,  341,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  453,  454, 
+          455,   -1,   -1,  272,   -1,   -1,  461,   -1,  341,  464, 
+           -1,  344,   -1,  346,   -1,  262,  263,  264,  473,   -1, 
+          267,  268,  269,   -1,  271,   -1,  295,   -1,   -1,  362, 
+           -1,   -1,   -1,   -1,  281,  282,   -1,   59,   -1,   -1, 
+           -1,   -1,   -1,  290,  291,    0,  293,  294,  295,  296, 
+          297,   -1,   -1,   -1,   -1,   10,   -1,   -1,  305,   -1, 
+           -1,   -1,   -1,  332,  333,  334,  335,  336,  337,  338, 
           339,  340,  341,  342,  343,  344,  345,  346,  347,  348, 
-          349,  350,  351,  352,  353,  354,  355,  356,  357,  358, 
-          359,  360,  361,  362,   -1,  364,  365,  366,  367,  368, 
-          369,   -1,   -1,   10,  373,  374,  375,  376,   -1,  378, 
-          379,  257,  258,  259,   -1,  261,  262,  263,  264,  265, 
-          266,  267,  268,  269,  270,  271,  272,  273,  274,  275, 
-          276,  277,  278,   -1,  280,  281,  282,  283,  284,  285, 
-          286,  287,  288,  289,  290,  291,  292,  293,  294,  295, 
-          296,  297,   59,  299,   -1,   -1,  302,  303,  304,  305, 
-          306,  307,  308,  309,  310,  311,  312,  313,  314,  315, 
-          316,  317,  318,  319,  320,  321,  322,  323,  324,  325, 
-          326,  327,  328,  329,  330,   -1,   -1,  333,  334,  335, 
-          336,   -1,  338,  339,  340,  341,  342,  343,  344,  345, 
-          346,  347,  348,  349,  350,  351,  352,  353,  354,  355, 
-          356,  357,  358,  359,  360,  361,  362,   -1,  364,  365, 
-          366,  367,  368,  369,    0,   -1,   -1,  373,  374,  375, 
-          376,   -1,  378,  379,   10,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  306,   -1,  308, 
-          309,  310,  311,   -1,   -1,   -1,   -1,   -1,   44,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  328, 
-           -1,   -1,   58,   59,   -1,   61,  335,   63,   -1,   -1, 
-          339,  340,   -1,   -1,   -1,   -1,   -1,   -1,  347,  348, 
-          349,  350,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   91,   -1,  306,  307,   -1, 
-           -1,  310,   -1,   -1,   -1,  314,  315,   -1,  317,  318, 
-          319,  320,  321,  322,  323,   -1,   -1,  326,  327,   -1, 
-           -1,   -1,  331,  332,  333,  334,   -1,    0,   -1,   -1, 
-           -1,  340,   -1,   -1,   -1,   -1,   -1,   10,  347,  348, 
-           -1,  350,  351,  352,  353,  354,  355,  356,  357,  358, 
-          359,  360,   -1,   -1,  363,   -1,  317,  318,  319,  320, 
-          321,  322,  323,  324,  325,  326,  327,   -1,   -1,   -1, 
-           -1,   44,  333,  334,   -1,   -1,   -1,   -1,   -1,  306, 
-           -1,  308,  309,  310,  311,   58,   59,  348,   -1,  350, 
-           63,  352,  353,  354,  355,  356,  357,  358,   -1,  360, 
-           -1,  328,   -1,   -1,   -1,   -1,   -1,   -1,  335,   -1, 
-           -1,   -1,  339,  340,   -1,   -1,   -1,   -1,   91,   -1, 
-          347,  348,  349,  350,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  257,  258,  259,   -1,  261,  262,  263,  264,  265, 
-          266,  267,  268,  269,  270,  271,  272,  273,  274,  275, 
-          276,  277,  278,   -1,  280,  281,  282,  283,  284,  285, 
-          286,  287,  288,  289,  290,  291,  292,  293,  294,  295, 
-          296,  297,   10,  299,   -1,   -1,  302,  303,  304,  305, 
-          306,  307,  308,  309,  310,  311,  312,  313,  314,  315, 
-          316,  317,  318,  319,  320,  321,  322,  323,  324,  325, 
-          326,  327,  328,  329,  330,   -1,   -1,  333,  334,  335, 
-          336,   -1,  338,  339,  340,  341,  342,  343,  344,  345, 
-          346,  347,  348,  349,  350,  351,  352,  353,  354,  355, 
-          356,  357,  358,  359,  360,  361,  362,   -1,  364,  365, 
-          366,  367,  368,  369,   -1,   -1,   10,  373,  374,  375, 
-          376,   -1,  378,  379,  257,  258,  259,   -1,  261,  262, 
-          263,  264,  265,  266,  267,  268,  269,  270,  271,  272, 
-          273,  274,  275,  276,  277,  278,   -1,  280,  281,  282, 
-          283,  284,  285,  286,  287,  288,  289,  290,  291,  292, 
-          293,  294,  295,  296,  297,   59,  299,   -1,   -1,  302, 
-          303,  304,  305,  306,  307,  308,  309,  310,  311,  312, 
-          313,  314,  315,  316,  317,  318,  319,  320,  321,  322, 
-          323,  324,  325,  326,  327,  328,  329,  330,   -1,   -1, 
-          333,  334,  335,  336,   -1,  338,  339,  340,  341,  342, 
-          343,  344,  345,  346,  347,  348,  349,  350,  351,  352, 
-          353,  354,  355,  356,  357,  358,  359,  360,  361,  362, 
-           -1,  364,  365,  366,  367,  368,  369,    0,   -1,   -1, 
-          373,  374,  375,  376,   -1,  378,  379,   10,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   44,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257, 
-          258,  259,   -1,  261,   -1,   58,   59,  265,  266,   -1, 
-           63,   -1,  270,   -1,  272,  273,  274,  275,  276,  277, 
-          278,   -1,   -1,   -1,   -1,  283,  284,  285,  286,  287, 
-          288,  289,   -1,   -1,  292,   -1,   -1,   -1,   91,   -1, 
-           -1,  299,   -1,   -1,  302,  303,  304,   -1,  306,  307, 
-          308,  309,  310,  311,  312,  313,  314,  315,  316,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-            0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  336,   -1, 
-           10,  339,   -1,   -1,  342,  343,   -1,  345,   -1,  347, 
-           -1,  349,   -1,  351,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  359,   -1,   -1,   -1,   -1,  364,  365,  366,  367, 
-          368,  369,   -1,   -1,   44,  373,   -1,  375,  376,   -1, 
-          378,  379,  306,   -1,  308,  309,  310,  311,   58,   59, 
-           -1,   -1,   -1,   63,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  328,   -1,   -1,   -1,   -1,   -1, 
-           -1,  335,   -1,   -1,   -1,  339,  340,   -1,   -1,   -1, 
-           -1,   91,   -1,  347,  348,  349,  350,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   10,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  257,  258,  259,   -1,  261,  262, 
-          263,  264,  265,  266,  267,  268,  269,  270,  271,  272, 
-          273,  274,  275,  276,  277,  278,   59,  280,  281,  282, 
-          283,  284,  285,  286,  287,  288,  289,  290,  291,  292, 
-          293,  294,  295,  296,  297,   -1,  299,   -1,   -1,  302, 
-          303,  304,  305,  306,  307,  308,  309,  310,  311,  312, 
-          313,  314,  315,  316,  317,  318,  319,  320,  321,  322, 
-          323,  324,  325,  326,  327,  328,  329,  330,   -1,   -1, 
-          333,  334,  335,  336,   -1,  338,  339,  340,  341,  342, 
-          343,  344,  345,  346,  347,  348,  349,  350,  351,  352, 
-          353,  354,  355,  356,  357,  358,  359,  360,  361,  362, 
-           -1,  364,  365,  366,  367,  368,  369,   -1,   -1,   10, 
-          373,  374,  375,  376,   -1,  378,  379,  257,  258,  259, 
-           -1,  261,  262,  263,  264,  265,  266,  267,  268,  269, 
-          270,  271,  272,  273,  274,  275,  276,  277,  278,   -1, 
-          280,  281,  282,  283,  284,  285,  286,  287,  288,  289, 
-          290,  291,  292,  293,  294,  295,  296,  297,   59,  299, 
-           -1,   -1,  302,  303,  304,  305,  306,  307,  308,  309, 
-          310,  311,  312,  313,  314,  315,  316,  317,  318,  319, 
-          320,  321,  322,  323,  324,  325,  326,  327,  328,  329, 
-          330,   -1,   -1,  333,  334,  335,  336,   -1,  338,  339, 
-          340,  341,  342,  343,  344,  345,  346,  347,  348,  349, 
-          350,  351,  352,  353,  354,  355,  356,  357,  358,  359, 
-          360,  361,  362,   -1,  364,  365,  366,  367,  368,  369, 
-            0,   -1,   -1,  373,  374,  375,  376,   -1,  378,  379, 
-           10,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  306,   -1,  308,  309,  310,  311,   -1, 
-           -1,   -1,   -1,   -1,   44,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  328,   -1,   -1,   58,   59, 
-           -1,   61,  335,   63,   -1,   -1,  339,  340,   -1,   -1, 
-           -1,   -1,   -1,   -1,  347,  348,  349,  350,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   91,   -1,  306,  307,   -1,   -1,  310,   -1,   -1, 
-           -1,  314,  315,   -1,  317,  318,  319,  320,  321,  322, 
-          323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331,  332, 
-          333,  334,   -1,    0,   -1,   -1,   -1,  340,   -1,   -1, 
-           -1,   -1,   -1,   10,  347,  348,   -1,  350,  351,  352, 
-          353,  354,  355,  356,  357,  358,  359,  360,   -1,   -1, 
-          363,   -1,  317,  318,  319,  320,  321,  322,  323,  324, 
-           -1,  326,  327,   -1,   -1,   -1,   -1,   44,  333,  334, 
-           -1,   -1,   -1,   -1,   -1,  306,   -1,  308,  309,  310, 
-          311,   -1,   59,  348,   -1,  350,   63,  352,  353,  354, 
-          355,  356,  357,  358,   -1,  360,   -1,  328,   -1,   -1, 
-           -1,   -1,   -1,   -1,  335,   -1,   -1,   -1,  339,  340, 
-           -1,   -1,   -1,   -1,   91,   -1,  347,  348,  349,  350, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          349,  350,  351,  352,  353,  354,  355,  356,  357,   44, 
+           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
+          369,  778,   -1,   -1,   59,   -1,   -1,   -1,   -1,  786, 
+          787,   -1,  789,   -1,  791,  362,  793,  572,  795,  796, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  374,  397,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,  258,  259, 
-           -1,  261,  262,  263,  264,  265,  266,  267,  268,  269, 
-          270,  271,  272,  273,  274,  275,  276,  277,  278,   -1, 
-           -1,  281,  282,  283,  284,  285,  286,  287,  288,  289, 
-          290,  291,  292,  293,  294,  295,  296,  297,   10,  299, 
-           -1,   -1,  302,  303,  304,  305,  306,  307,  308,  309, 
-          310,  311,  312,  313,  314,  315,  316,  317,  318,  319, 
-          320,  321,  322,  323,  324,  325,  326,  327,  328,  329, 
-          330,   -1,   -1,  333,  334,  335,  336,   -1,  338,  339, 
-          340,  341,  342,  343,  344,  345,  346,  347,  348,  349, 
-          350,  351,  352,  353,  354,  355,  356,  357,  358,  359, 
-          360,   -1,  362,   -1,  364,  365,  366,  367,  368,  369, 
-           -1,   -1,   -1,  373,  374,  375,  376,   -1,  378,  379, 
-          257,  258,  259,   -1,  261,  262,  263,  264,  265,  266, 
+          409,  410,  411,   -1,   -1,  414,   -1,   -1,   -1,   -1, 
+            0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  427,  428, 
+           10,   -1,  431,  317,  318,  319,  320,  321,  322,  323, 
+          324,  325,  326,  327,   -1,  329,  330,   -1,   -1,  333, 
+          334,   -1,   -1,  638,  453,  454,  455,   -1,   -1,   -1, 
+           -1,   -1,  461,   -1,  348,  464,  350,   -1,  352,  353, 
+          354,  355,  356,  357,  358,  474,  360,   -1,   -1,   59, 
+           -1,   -1,   -1,   -1,  669,  306,   -1,  308,  309,  310, 
+          311,  898,  899,  900,  901,   10,   -1,  904,   -1,  906, 
+          907,  908,  909,   -1,   -1,   -1,   -1,  328,   -1,   -1, 
+           -1,   10,   -1,   -1,  335,   -1,  701,  702,  339,  340, 
+           -1,   -1,  707,  708,   -1,   -1,  347,  348,  349,  350, 
+           -1,   -1,   -1,  718,  719,  720,   -1,  944,   -1,  724, 
+          947,  948,  949,  950,   59,   -1,   -1,   -1,   -1,   -1, 
+          957,   -1,   -1,   -1,  306,   -1,  308,  309,  310,  311, 
+           59,  746,  747,  748,   -1,   -1,   -1,  752,   -1,   -1, 
+           -1,   -1,   -1,  572,   -1,   -1,   -1,  262,  263,  264, 
+           -1,   -1,  581,  268,  269,   -1,  271,  339,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  347,  348,  349,  350,   -1, 
+          317,   -1,   -1,    0,   -1,  290,  291,   -1,  293,  294, 
+          295,  296,   -1,   10,   -1,   -1,  333,  334,  803,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  814, 
+           -1,  348,  817,  350,   -1,  352,  353,  354,  355,  638, 
+           -1,  358,   -1,  360,   -1,   -1,   -1,   44,   -1,   -1, 
+           -1,  836,   -1,   -1,   -1,   -1,  341,   -1,  657,   -1, 
+           -1,   58,   59,   -1,   61,   -1,   63,   -1,   -1,  317, 
+          669,   -1,   -1,   -1,   -1,   -1,   -1,  362,   -1,   -1, 
+           -1,   -1,  262,  263,  264,  333,  334,  267,  268,  269, 
+           -1,  271,   -1,   -1,   91,   -1,   -1,   -1,   -1,  884, 
+          348,  281,  282,   -1,  352,  353,  354,  355,   -1,  708, 
+          290,  291,   -1,  293,  294,  295,  296,  297,   -1,  718, 
+           -1,  720,   -1,   -1,  723,  724,    0,   -1,   -1,   -1, 
+          306,   -1,  308,  309,  310,  311,   10,  736,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  746,  747,  748, 
+           -1,   -1,   -1,  752,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  341,   -1,  339,  344,   -1,  346,   -1,   -1,   -1, 
+           44,  347,  348,  349,  350,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  362,   -1,   58,   59,   -1,   61,   -1,   63, 
+           -1,  306,   -1,  308,  309,  310,  311,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  803,   -1,   -1,  306,   -1,  308, 
+          309,  310,  311,  328,   -1,   -1,   -1,   91,  817,   -1, 
+          335,   -1,   -1,   -1,  339,  340,   -1,   -1,   -1,  328, 
+           -1,   -1,  347,  348,  349,  350,  335,  836,   -1,   -1, 
+          339,  340,   -1,   -1,   -1,   -1,   -1,   -1,  347,  348, 
+          349,  350,   10,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          257,  258,  259,  862,  261,  262,  263,  264,  265,  266, 
           267,  268,  269,  270,  271,  272,  273,  274,  275,  276, 
-          277,  278,   -1,  280,  281,  282,  283,  284,  285,  286, 
+          277,  278,  279,  280,  281,  282,  283,  284,  285,  286, 
           287,  288,  289,  290,  291,  292,  293,  294,  295,  296, 
-          297,   -1,  299,   -1,   -1,  302,  303,  304,   63,  306, 
+          297,   59,  299,   -1,   -1,  302,  303,  304,  305,  306, 
           307,  308,  309,  310,  311,  312,  313,  314,  315,  316, 
           317,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
           327,  328,  329,  330,   -1,   -1,  333,  334,  335,  336, 
-           -1,  338,  339,   -1,  341,  342,  343,  344,  345,  346, 
+          337,  338,  339,  340,  341,  342,  343,  344,  345,  346, 
           347,  348,  349,  350,  351,  352,  353,  354,  355,  356, 
           357,  358,  359,  360,  361,  362,   -1,  364,  365,  366, 
-          367,  368,  369,    0,   -1,   -1,  373,   -1,  375,  376, 
-           -1,  378,  379,   10,   -1,   -1,   -1,   -1,   -1,   -1, 
+          367,  368,  369,   -1,   -1,   10,  373,  374,  375,  376, 
+           -1,  378,  379,  257,  258,  259,   -1,  261,  262,  263, 
+          264,  265,  266,  267,  268,  269,  270,  271,  272,  273, 
+          274,  275,  276,  277,  278,  279,  280,  281,  282,  283, 
+          284,  285,  286,  287,  288,  289,  290,  291,  292,  293, 
+          294,  295,  296,  297,   59,  299,   -1,   -1,  302,  303, 
+          304,  305,  306,  307,  308,  309,  310,  311,  312,  313, 
+          314,  315,  316,  317,  318,  319,  320,  321,  322,  323, 
+          324,  325,  326,  327,  328,  329,  330,   -1,   -1,  333, 
+          334,  335,  336,  337,  338,  339,  340,  341,  342,  343, 
+          344,  345,  346,  347,  348,  349,  350,  351,  352,  353, 
+          354,  355,  356,  357,  358,  359,  360,  361,  362,   -1, 
+          364,  365,  366,  367,  368,  369,    0,   -1,   -1,  373, 
+          374,  375,  376,   -1,  378,  379,   10,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          306,   -1,  308,  309,  310,  311,   -1,   -1,   -1,   -1, 
+           44,   -1,  306,   -1,  308,  309,  310,  311,  306,   -1, 
+          308,  309,  310,  311,   58,   59,   -1,   61,   -1,   63, 
+           -1,   -1,   -1,  339,  340,   -1,   -1,   -1,   -1,   -1, 
+          328,  347,  348,  349,  350,  339,  340,  335,   -1,   -1, 
+           -1,  339,  340,  347,  348,  349,  350,   91,   -1,  347, 
+          348,  349,  350,   -1,   -1,   -1,  306,  307,   -1,   -1, 
+          310,   -1,   -1,   -1,  314,  315,   -1,  317,  318,  319, 
+          320,  321,  322,  323,   -1,   -1,  326,  327,   -1,    0, 
+           -1,  331,  332,  333,  334,   -1,   -1,   -1,   -1,   10, 
+          340,   -1,   -1,   -1,   -1,   -1,   -1,  347,  348,   -1, 
+          350,  351,  352,  353,  354,  355,  356,  357,  358,  359, 
+          360,   -1,   -1,  363,  306,   -1,  308,  309,  310,  311, 
+           -1,   -1,   -1,   44,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  306,   -1,  308,  309,  310,  311,   58,   59,   -1, 
+           61,   -1,   63,   -1,   -1,   -1,   -1,  339,   -1,   -1, 
+           -1,   -1,  317,  328,   -1,  347,  348,  349,  350,   -1, 
+          335,  317,   -1,   -1,  339,  340,  322,  323,  333,  334, 
+           91,   -1,  347,  348,  349,  350,   -1,  333,  334,   -1, 
+           -1,   -1,   -1,  348,   -1,  350,   10,  352,  353,  354, 
+          355,   -1,  348,   -1,  350,   -1,  352,  353,  354,  355, 
+          356,  357,  358,   -1,  360,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  257,  258,  259,   -1,  261,  262,  263, 
+          264,  265,  266,  267,  268,  269,  270,  271,  272,  273, 
+          274,  275,  276,  277,  278,   59,  280,  281,  282,  283, 
+          284,  285,  286,  287,  288,  289,  290,  291,  292,  293, 
+          294,  295,  296,  297,   -1,  299,   -1,   -1,  302,  303, 
+          304,  305,  306,  307,  308,  309,  310,  311,  312,  313, 
+          314,  315,  316,  317,  318,  319,  320,  321,  322,  323, 
+          324,  325,  326,  327,  328,  329,  330,   -1,   -1,  333, 
+          334,  335,  336,   -1,  338,  339,  340,  341,  342,  343, 
+          344,  345,  346,  347,  348,  349,  350,  351,  352,  353, 
+          354,  355,  356,  357,  358,  359,  360,  361,  362,   -1, 
+          364,  365,  366,  367,  368,  369,   -1,   -1,   10,  373, 
+          374,  375,  376,   -1,  378,  379,  257,  258,  259,   -1, 
+          261,  262,  263,  264,  265,  266,  267,  268,  269,  270, 
+          271,  272,  273,  274,  275,  276,  277,  278,   -1,  280, 
+          281,  282,  283,  284,  285,  286,  287,  288,  289,  290, 
+          291,  292,  293,  294,  295,  296,  297,   59,  299,   -1, 
+           -1,  302,  303,  304,  305,  306,  307,  308,  309,  310, 
+          311,  312,  313,  314,  315,  316,  317,  318,  319,  320, 
+          321,  322,  323,  324,  325,  326,  327,  328,  329,  330, 
+           -1,   -1,  333,  334,  335,  336,   -1,  338,  339,  340, 
+          341,  342,  343,  344,  345,  346,  347,  348,  349,  350, 
+          351,  352,  353,  354,  355,  356,  357,  358,  359,  360, 
+          361,  362,   -1,  364,  365,  366,  367,  368,  369,    0, 
+           -1,   -1,  373,  374,  375,  376,   -1,  378,  379,   10, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  306,   -1,  308,  309,  310,  311,   -1,   -1, 
+           -1,   -1,   -1,   44,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  328,   -1,   -1,   58,   59,   -1, 
+           61,  335,   63,   -1,   -1,  339,  340,   -1,   -1,   -1, 
+           -1,   -1,   -1,  347,  348,  349,  350,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           91,   -1,  306,  307,   -1,   -1,  310,   -1,   -1,   -1, 
+          314,  315,   -1,  317,  318,  319,  320,  321,  322,  323, 
+           -1,   -1,  326,  327,   -1,   -1,   -1,  331,  332,  333, 
+          334,   -1,    0,   -1,   -1,   -1,  340,   -1,   -1,   -1, 
+           -1,   -1,   10,  347,  348,   -1,  350,  351,  352,  353, 
+          354,  355,  356,  357,  358,  359,  360,   -1,   -1,  363, 
+           -1,  317,  318,  319,  320,  321,  322,  323,  324,  325, 
+          326,  327,   -1,   -1,   -1,   -1,   44,  333,  334,   -1, 
+           -1,   -1,   -1,   -1,  306,   -1,  308,  309,  310,  311, 
+           58,   59,  348,   -1,  350,   63,  352,  353,  354,  355, 
+          356,  357,  358,   -1,  360,   -1,  328,   -1,   -1,   -1, 
+           -1,   -1,   -1,  335,   -1,   -1,   -1,  339,  340,   -1, 
+           -1,   -1,   -1,   91,   -1,  347,  348,  349,  350,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  257,  258,  259,   -1, 
+          261,  262,  263,  264,  265,  266,  267,  268,  269,  270, 
+          271,  272,  273,  274,  275,  276,  277,  278,   -1,  280, 
+          281,  282,  283,  284,  285,  286,  287,  288,  289,  290, 
+          291,  292,  293,  294,  295,  296,  297,   10,  299,   -1, 
+           -1,  302,  303,  304,  305,  306,  307,  308,  309,  310, 
+          311,  312,  313,  314,  315,  316,  317,  318,  319,  320, 
+          321,  322,  323,  324,  325,  326,  327,  328,  329,  330, 
+           -1,   -1,  333,  334,  335,  336,   -1,  338,  339,  340, 
+          341,  342,  343,  344,  345,  346,  347,  348,  349,  350, 
+          351,  352,  353,  354,  355,  356,  357,  358,  359,  360, 
+          361,  362,   -1,  364,  365,  366,  367,  368,  369,   -1, 
+           -1,   -1,  373,  374,  375,  376,   -1,  378,  379,  257, 
+          258,  259,   -1,  261,  262,  263,  264,  265,  266,  267, 
+          268,  269,  270,  271,  272,  273,  274,  275,  276,  277, 
+          278,   -1,  280,  281,  282,  283,  284,  285,  286,  287, 
+          288,  289,  290,  291,  292,  293,  294,  295,  296,  297, 
+           -1,  299,   -1,   -1,  302,  303,  304,  305,  306,  307, 
+          308,  309,  310,  311,  312,  313,  314,  315,  316,  317, 
+          318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
+          328,  329,  330,   -1,   -1,  333,  334,  335,  336,   -1, 
+          338,  339,  340,  341,  342,  343,  344,  345,  346,  347, 
+          348,  349,  350,  351,  352,  353,  354,  355,  356,  357, 
+          358,  359,  360,  361,  362,   -1,  364,  365,  366,  367, 
+          368,  369,    0,   -1,   -1,  373,  374,  375,  376,   -1, 
+          378,  379,   10,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   44,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  257,  258,  259,   -1,  261,   -1, 
+           58,   59,  265,  266,   -1,   63,   -1,  270,   -1,  272, 
+          273,  274,  275,  276,  277,  278,   -1,   -1,   -1,   -1, 
+          283,  284,  285,  286,  287,  288,  289,   -1,   -1,  292, 
+           -1,   -1,   -1,   91,   -1,   -1,  299,   -1,   -1,  302, 
+          303,  304,   -1,  306,  307,  308,  309,  310,  311,  312, 
+          313,  314,  315,  316,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,    0,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  336,   -1,   10,  339,   -1,   -1,  342, 
+          343,   -1,  345,   -1,  347,   -1,  349,   -1,  351,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  359,   -1,   -1,   -1, 
+           -1,  364,  365,  366,  367,  368,  369,   -1,   -1,   44, 
+          373,   -1,  375,  376,   -1,  378,  379,   -1,  306,  307, 
+           -1,   -1,  310,   58,   59,   -1,  314,  315,   63,  317, 
+          318,  319,  320,  321,  322,  323,   -1,   -1,  326,  327, 
+           -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1,   -1, 
+           -1,   -1,  340,   -1,   -1,   -1,   91,   -1,   -1,  347, 
+          348,   -1,  350,  351,  352,  353,  354,  355,  356,  357, 
+          358,  359,  360,   -1,   -1,  363,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257, 
+          258,  259,   -1,  261,  262,  263,  264,  265,  266,  267, 
+          268,  269,  270,  271,  272,  273,  274,  275,  276,  277, 
+          278,   -1,  280,  281,  282,  283,  284,  285,  286,  287, 
+          288,  289,  290,  291,  292,  293,  294,  295,  296,  297, 
+           -1,  299,   44,   -1,  302,  303,  304,  305,  306,  307, 
+          308,  309,  310,  311,  312,  313,  314,  315,  316,  317, 
+          318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
+          328,  329,  330,   -1,   -1,  333,  334,  335,  336,   -1, 
+          338,  339,  340,  341,  342,  343,  344,  345,  346,  347, 
+          348,  349,  350,  351,  352,  353,  354,  355,  356,  357, 
+          358,  359,  360,  361,  362,   -1,  364,  365,  366,  367, 
+          368,  369,   -1,   -1,   -1,  373,  374,  375,  376,   -1, 
+          378,  379,  257,  258,  259,   -1,  261,  262,  263,  264, 
+          265,  266,  267,  268,  269,  270,  271,  272,  273,  274, 
+          275,  276,  277,  278,   -1,  280,  281,  282,  283,  284, 
+          285,  286,  287,  288,  289,  290,  291,  292,  293,  294, 
+          295,  296,  297,   -1,  299,   -1,   -1,  302,  303,  304, 
+          305,  306,  307,  308,  309,  310,  311,  312,  313,  314, 
+          315,  316,  317,  318,  319,  320,  321,  322,  323,  324, 
+          325,  326,  327,  328,  329,  330,   -1,   -1,  333,  334, 
+          335,  336,   -1,  338,  339,  340,  341,  342,  343,  344, 
+          345,  346,  347,  348,  349,  350,  351,  352,  353,  354, 
+          355,  356,  357,  358,  359,  360,  361,  362,   -1,  364, 
+          365,  366,  367,  368,  369,    0,   -1,   -1,  373,  374, 
+          375,  376,   -1,  378,  379,   10,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,  257,  258,  259,   -1,  261, 
-           -1,   -1,   59,  265,  266,   -1,   63,   -1,  270,   -1, 
-          272,  273,  274,  275,  276,  277,  278,   -1,   -1,   -1, 
+           -1,   -1,   -1,  265,  266,   -1,   -1,   -1,  270,   -1, 
+          272,  273,  274,  275,  276,  277,  278,   -1,   -1,   44, 
            -1,  283,  284,  285,  286,  287,  288,  289,   -1,   -1, 
-          292,   -1,   -1,   -1,   91,   -1,   -1,  299,   -1,   -1, 
+          292,   -1,   -1,   58,   59,   -1,   61,  299,   63,   -1, 
           302,  303,  304,   -1,  306,  307,  308,  309,  310,  311, 
-           -1,  313,  314,  315,  316,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,    0,   -1,   -1,   -1, 
+           -1,  313,   -1,   -1,  316,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   91,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  336,   -1,   -1,  339,   -1,   -1, 
+          342,  343,   -1,  345,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,    0,   -1, 
+           -1,   -1,  364,  365,  366,  367,  368,  369,   10,   -1, 
+           -1,  373,   -1,  375,  376,   -1,  378,  379,  306,  307, 
+           -1,   -1,  310,   -1,   -1,   -1,  314,  315,   -1,  317, 
+          318,  319,  320,  321,  322,  323,   -1,   -1,  326,  327, 
+           -1,   -1,   44,  331,  332,  333,  334,   -1,   -1,   -1, 
+           -1,   -1,  340,   -1,   -1,   -1,   -1,   59,   -1,  347, 
+          348,   63,  350,  351,  352,  353,  354,  355,  356,  357, 
+          358,  359,  360,   -1,   -1,  363,  317,  318,  319,  320, 
+          321,  322,  323,  324,   -1,  326,  327,   -1,   -1,   91, 
+           -1,   -1,  333,  334,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  348,   -1,  350, 
+           -1,  352,  353,  354,  355,  356,  357,  358,   -1,  360, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  257,  258,  259,   -1,  261,  262,  263,  264, 
+          265,  266,  267,  268,  269,  270,  271,  272,  273,  274, 
+          275,  276,  277,  278,   -1,   -1,  281,  282,  283,  284, 
+          285,  286,  287,  288,  289,  290,  291,  292,  293,  294, 
+          295,  296,  297,   10,  299,   -1,   -1,  302,  303,  304, 
+          305,  306,  307,  308,  309,  310,  311,  312,  313,  314, 
+          315,  316,  317,  318,  319,  320,  321,  322,  323,  324, 
+          325,  326,  327,  328,  329,  330,   -1,   -1,  333,  334, 
+          335,  336,   -1,  338,  339,  340,  341,  342,  343,  344, 
+          345,  346,  347,  348,  349,  350,  351,  352,  353,  354, 
+          355,  356,  357,  358,  359,  360,   -1,  362,   -1,  364, 
+          365,  366,  367,  368,  369,   -1,   -1,   -1,  373,  374, 
+          375,  376,   -1,  378,  379,  257,  258,  259,   -1,  261, 
+          262,  263,  264,  265,  266,  267,  268,  269,  270,  271, 
+          272,  273,  274,  275,  276,  277,  278,   -1,  280,  281, 
+          282,  283,  284,  285,  286,  287,  288,  289,  290,  291, 
+          292,  293,  294,  295,  296,  297,   -1,  299,   -1,   -1, 
+          302,  303,  304,   63,  306,  307,  308,  309,  310,  311, 
+          312,  313,  314,  315,  316,  317,  318,  319,  320,  321, 
+          322,  323,  324,  325,  326,  327,  328,  329,  330,   -1, 
+           -1,  333,  334,  335,  336,   -1,  338,  339,   -1,  341, 
+          342,  343,  344,  345,  346,  347,  348,  349,  350,  351, 
+          352,  353,  354,  355,  356,  357,  358,  359,  360,  361, 
+          362,   -1,  364,  365,  366,  367,  368,  369,    0,   -1, 
+           -1,  373,   -1,  375,  376,   -1,  378,  379,   10,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   44,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          257,  258,  259,   -1,  261,   -1,   -1,   59,  265,  266, 
+           -1,   63,   -1,  270,   -1,  272,  273,  274,  275,  276, 
+          277,  278,   -1,   -1,   -1,   -1,  283,  284,  285,  286, 
+          287,  288,  289,   -1,   -1,  292,   -1,   -1,   -1,   91, 
+           -1,   -1,  299,   -1,   -1,  302,  303,  304,   -1,  306, 
+          307,  308,  309,  310,  311,   -1,  313,  314,  315,  316, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,    0,   -1,   -1,   -1, 
+      };
+   }
+
+   private static final short[] yyCheck2() {
+      return new short[] {
+
            -1,   -1,   -1,   -1,  336,   -1,   10,  339,   -1,   -1, 
           342,  343,   -1,  345,   -1,   -1,   -1,   -1,   -1,  351, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,  359,   -1,   -1, 
            -1,   -1,  364,  365,  366,  367,  368,  369,   -1,   -1, 
            44,  373,  297,  375,  376,   -1,  378,  379,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   59,   -1,   -1,   -1,   63, 
            -1,   -1,  317,  318,  319,  320,  321,  322,  323,  324, 
           325,  326,  327,   -1,  329,  330,   -1,   -1,  333,  334, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   91,   -1,   -1, 
            -1,   -1,   -1,  348,   -1,  350,   -1,  352,  353,  354, 
           355,  356,  357,  358,   -1,  360,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,    0,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   10,   -1,   -1,   -1,   -1,   -1, 
           257,  258,  259,   -1,  261,  262,  263,  264,  265,  266, 
-          267,  268,  269,  270,  271,  272, 
-      };
-   }
-
-   private static final short[] yyCheck2() {
-      return new short[] {
-
-          273,  274,  275,  276,  277,  278,   -1,  280,  281,  282, 
+          267,  268,  269,  270,  271,  272,  273,  274,  275,  276, 
+          277,  278,   -1,  280,  281,  282,  283,  284,  285,  286, 
+          287,  288,  289,  290,  291,  292,  293,  294,  295,  296, 
+          297,   -1,  299,   59,   63,  302,  303,  304,   -1,  306, 
+          307,  308,  309,  310,  311,  312,  313,  314,  315,  316, 
+          317,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
+          327,  328,  329,  330,   -1,   -1,  333,  334,  335,  336, 
+           -1,  338,  339,   -1,  341,  342,  343,  344,  345,  346, 
+          347,  348,  349,  350,  351,  352,  353,  354,  355,  356, 
+          357,  358,  359,  360,  361,  362,   -1,  364,  365,  366, 
+          367,  368,  369,   63,   -1,   -1,  373,   -1,  375,  376, 
+           -1,  378,  379,  257,  258,  259,   -1,  261,  262,  263, 
+          264,  265,  266,  267,  268,  269,  270,  271,  272,  273, 
+          274,  275,  276,  277,  278,   -1,   -1,  281,  282,  283, 
+          284,  285,  286,  287,  288,  289,  290,  291,  292,  293, 
+          294,  295,  296,  297,   -1,  299,   -1,   -1,  302,  303, 
+          304,   -1,  306,  307,  308,  309,  310,  311,  312,  313, 
+          314,  315,  316,  317,  318,  319,  320,  321,  322,  323, 
+          324,  325,  326,  327,  328,  329,  330,   -1,   -1,  333, 
+          334,  335,  336,   -1,  338,  339,   -1,  341,  342,  343, 
+          344,  345,  346,  347,  348,  349,  350,  351,  352,  353, 
+          354,  355,  356,  357,  358,  359,  360,    0,  362,   -1, 
+          364,  365,  366,  367,  368,  369,   -1,   10,   -1,  373, 
+           -1,  375,  376,   -1,  378,  379,  262,  263,  264,   -1, 
+           -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1, 
+           -1,   44,   -1,   -1,  290,  291,   -1,  293,  294,  295, 
+          296,  297,   -1,   -1,   -1,   -1,   59,   -1,   61,   -1, 
+           63,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  317,  318, 
+          319,  320,  321,  322,  323,  324,  325,  326,  327,   -1, 
+          329,  330,   -1,   -1,  333,  334,   -1,   -1,   91,   -1, 
+           -1,   -1,   -1,   -1,   -1,  341,   -1,   -1,  344,  348, 
+          346,  350,   -1,  352,  353,  354,  355,  356,  357,  358, 
+           -1,  360,   -1,   -1,   -1,   -1,  362,  297,   -1,   -1, 
+            0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           10,   -1,   -1,   -1,   -1,   -1,   -1,  317,  318,  319, 
+          320,  321,  322,  323,  324,  325,  326,  327,   -1,  329, 
+          330,   -1,   -1,  333,  334,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   44,   -1,   -1,   -1,  348,   -1, 
+          350,   -1,  352,  353,  354,  355,  356,  357,  358,   59, 
+          360,   61,   -1,   63,   -1,   -1,   -1,  306,  307,   -1, 
+           -1,  310,   -1,   -1,   -1,  314,  315,   -1,  317,  318, 
+          319,  320,  321,  322,  323,   -1,   -1,  326,  327,   -1, 
+           -1,   91,  331,  332,  333,  334,   -1,   -1,   -1,   -1, 
+           -1,  340,   -1,   -1,   -1,   -1,   -1,   -1,  347,  348, 
+           -1,  350,  351,  352,  353,  354,  355,  356,  357,  358, 
+          359,  360,   -1,    0,  363,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   10,  257,  258,  259,   -1,  261,  262, 
+          263,  264,  265,  266,   -1,  268,  269,  270,  271,  272, 
+          273,  274,  275,  276,  277,  278,   -1,  280,   -1,   -1, 
           283,  284,  285,  286,  287,  288,  289,  290,  291,  292, 
-          293,  294,  295,  296,  297,   -1,  299,   59,   63,  302, 
-          303,  304,   -1,  306,  307,  308,  309,  310,  311,  312, 
+          293,  294,  295,  296,  297,   -1,  299,   -1,   -1,  302, 
+          303,  304,   59,  306,  307,  308,  309,  310,  311,  312, 
           313,  314,  315,  316,  317,  318,  319,  320,  321,  322, 
           323,  324,  325,  326,  327,  328,  329,  330,   -1,   -1, 
-          333,  334,  335,  336,   -1,  338,  339,   -1,  341,  342, 
-          343,  344,  345,  346,  347,  348,  349,  350,  351,  352, 
+          333,  334,  335,  336,   -1,   -1,  339,  340,  341,  342, 
+          343,   -1,  345,   -1,  347,  348,  349,  350,  351,  352, 
           353,  354,  355,  356,  357,  358,  359,  360,  361,  362, 
-           -1,  364,  365,  366,  367,  368,  369,   63,   -1,   -1, 
+           -1,  364,  365,  366,  367,  368,  369,   -1,   -1,   -1, 
           373,   -1,  375,  376,   -1,  378,  379,  257,  258,  259, 
-           -1,  261,  262,  263,  264,  265,  266,  267,  268,  269, 
+           -1,  261,  262,  263,  264,  265,  266,   -1,  268,  269, 
           270,  271,  272,  273,  274,  275,  276,  277,  278,   -1, 
-           -1,  281,  282,  283,  284,  285,  286,  287,  288,  289, 
+          280,   -1,   -1,  283,  284,  285,  286,  287,  288,  289, 
           290,  291,  292,  293,  294,  295,  296,  297,   -1,  299, 
            -1,   -1,  302,  303,  304,   -1,  306,  307,  308,  309, 
           310,  311,  312,  313,  314,  315,  316,  317,  318,  319, 
           320,  321,  322,  323,  324,  325,  326,  327,  328,  329, 
-          330,   -1,   -1,  333,  334,  335,  336,   -1,  338,  339, 
-           -1,  341,  342,  343,  344,  345,  346,  347,  348,  349, 
+          330,   -1,   -1,  333,  334,  335,  336,   -1,   -1,  339, 
+          340,  341,  342,  343,   -1,  345,   -1,  347,  348,  349, 
           350,  351,  352,  353,  354,  355,  356,  357,  358,  359, 
-          360,    0,  362,   -1,  364,  365,  366,  367,  368,  369, 
-           -1,   10,   -1,  373,   -1,  375,  376,   -1,  378,  379, 
-          262,  263,  264,   -1,   -1,  267,  268,  269,   -1,  271, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281, 
-          282,   -1,   -1,   -1,   -1,   44,   -1,   -1,  290,  291, 
-           -1,  293,  294,  295,  296,  297,   -1,   -1,   -1,   -1, 
-           59,   -1,   61,   -1,   63,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  317,  318,  319,  320,  321,  322,  323,  324, 
-          325,  326,  327,   -1,  329,  330,   -1,   -1,  333,  334, 
-           -1,   -1,   91,   -1,   -1,   -1,   -1,   -1,   -1,  341, 
-           -1,   -1,  344,  348,  346,  350,   -1,  352,  353,  354, 
-          355,  356,  357,  358,   -1,  360,   -1,   -1,   -1,   -1, 
-          362,  297,   -1,   -1,    0,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   10,   -1,   -1,   -1,   -1,   -1, 
-           -1,  317,  318,  319,  320,  321,  322,  323,  324,  325, 
-          326,  327,   -1,  329,  330,   -1,   -1,  333,  334,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1, 
-           -1,   -1,  348,   -1,  350,   -1,  352,  353,  354,  355, 
-          356,  357,  358,   59,  360,   61,   -1,   63,   -1,   -1, 
-           -1,  306,  307,   -1,   -1,  310,   -1,   -1,   -1,  314, 
-          315,   -1,  317,  318,  319,  320,  321,  322,  323,   -1, 
-           -1,  326,  327,   -1,   -1,   91,  331,  332,  333,  334, 
-           -1,   -1,   -1,   -1,   -1,  340,   -1,   -1,   -1,   -1, 
-           -1,   -1,  347,  348,   -1,  350,  351,  352,  353,  354, 
-          355,  356,  357,  358,  359,  360,   -1,    0,  363,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   10,  257,  258, 
-          259,   -1,  261,  262,  263,  264,  265,  266,   -1,  268, 
-          269,  270,  271,  272,  273,  274,  275,  276,  277,  278, 
-           -1,  280,   -1,   -1,  283,  284,  285,  286,  287,  288, 
-          289,  290,  291,  292,  293,  294,  295,  296,  297,   -1, 
-          299,   -1,   -1,  302,  303,  304,   59,  306,  307,  308, 
-          309,  310,  311,  312,  313,  314,  315,  316,  317,  318, 
-          319,  320,  321,  322,  323,  324,  325,  326,  327,  328, 
-          329,  330,   -1,   -1,  333,  334,  335,  336,   -1,   -1, 
-          339,  340,  341,  342,  343,   -1,  345,   -1,  347,  348, 
-          349,  350,  351,  352,  353,  354,  355,  356,  357,  358, 
-          359,  360,  361,  362,   -1,  364,  365,  366,  367,  368, 
-          369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378, 
-          379,  257,  258,  259,   -1,  261,  262,  263,  264,  265, 
-          266,   -1,  268,  269,  270,  271,  272,  273,  274,  275, 
-          276,  277,  278,   -1,  280,   -1,   -1,  283,  284,  285, 
-          286,  287,  288,  289,  290,  291,  292,  293,  294,  295, 
-          296,  297,   -1,  299,   -1,   -1,  302,  303,  304,   -1, 
-          306,  307,  308,  309,  310,  311,  312,  313,  314,  315, 
-          316,  317,  318,  319,  320,  321,  322,  323,  324,  325, 
-          326,  327,  328,  329,  330,   -1,   -1,  333,  334,  335, 
-          336,   -1,   -1,  339,  340,  341,  342,  343,   -1,  345, 
-           -1,  347,  348,  349,  350,  351,  352,  353,  354,  355, 
-          356,  357,  358,  359,  360,  361,  362,   -1,  364,  365, 
-          366,  367,  368,  369,    0,   -1,   -1,  373,   -1,  375, 
-          376,   -1,  378,  379,   10,   -1,   -1,   -1,   -1,  262, 
-          263,  264,   -1,   -1,   -1,  268,  269,   -1,  271,   -1, 
+          360,  361,  362,   -1,  364,  365,  366,  367,  368,  369, 
+            0,   -1,   -1,  373,   -1,  375,  376,   -1,  378,  379, 
+           10,   -1,   -1,   -1,   -1,  262,  263,  264,   -1,   -1, 
+          267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  281,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   44,   -1,  293,  294,  295,  296, 
+          297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   59, 
+           -1,   61,   -1,   63,  317,  318,  319,  320,  321,  322, 
+          323,   -1,   -1,  326,  327,   -1,   -1,   -1,   -1,   -1, 
+          333,  334,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   91,   -1,   -1,  341,  348,   -1,  350,   -1,  352, 
+          353,  354,  355,  356,  357,  358,   -1,  360,   -1,  257, 
+          258,  259,   -1,  261,   -1,  362,   -1,  265,  266,   -1, 
+           -1,   -1,  270,    0,  272,  273,  274,  275,  276,  277, 
+          278,   -1,   -1,   10,   -1,  283,  284,  285,  286,  287, 
+          288,  289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1, 
+           -1,  299,   -1,   -1,  302,  303,  304,   -1,  306,  307, 
+          308,  309,  310,  311,   -1,  313,   -1,   44,  316,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1, 
-          293,  294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   59,   -1,   61,   -1,   63,  317,  318, 
-          319,  320,  321,  322,  323,   -1,   -1,  326,  327,   -1, 
-           -1,   -1,   -1,   -1,  333,  334,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   91,   -1,   -1,  341,  348, 
-           -1,  350,   -1,  352,  353,  354,  355,  356,  357,  358, 
-           -1,  360,   -1,  257,  258,  259,   -1,  261,   -1,  362, 
-           -1,  265,  266,   -1,   -1,   -1,  270,    0,  272,  273, 
-          274,  275,  276,  277,  278,   -1,   -1,   10,   -1,  283, 
-          284,  285,  286,  287,  288,  289,   -1,   -1,  292,   -1, 
-           -1,   -1,   -1,   -1,   -1,  299,   -1,   -1,  302,  303, 
-          304,   -1,  306,  307,  308,  309,  310,  311,   -1,  313, 
-           -1,   44,  316,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   59,   -1,   61,  333, 
-           63,   -1,  336,   -1,   -1,  339,   -1,   -1,  342,  343, 
-           -1,  345,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   91,   -1, 
-          364,  365,  366,  367,  368,  369,   -1,   -1,   -1,  373, 
-           -1,  375,  376,   -1,  378,  379,   -1,   -1,   -1,   -1, 
+           -1,   -1,   59,   -1,   61,  333,   63,   -1,  336,   -1, 
+           -1,  339,   -1,   -1,  342,  343,   -1,  345,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-            0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           10,  257,  258,  259,   -1,  261,  262,  263,  264,  265, 
-          266,   -1,  268,  269,  270,  271,  272,  273,  274,  275, 
-          276,  277,  278,   -1,  280,   -1,   -1,  283,  284,  285, 
-          286,  287,  288,  289,  290,  291,  292,  293,  294,  295, 
-          296,  297,   -1,  299,   -1,   -1,  302,  303,  304,   59, 
-          306,  307,  308,  309,  310,  311,  312,  313,  314,  315, 
-          316,  317,  318,  319,  320,  321,  322,  323,  324,  325, 
-          326,  327,  328,  329,  330,   -1,   -1,  333,  334,  335, 
-          336,   -1,   -1,  339,  340,  341,  342,  343,   -1,  345, 
-           -1,  347,  348,  349,  350,  351,  352,  353,  354,  355, 
-          356,  357,  358,  359,  360,  361,  362,   -1,  364,  365, 
-          366,  367,  368,  369,   -1,   -1,   -1,  373,   -1,  375, 
-          376,   -1,  378,  379,  257,  258,  259,   -1,  261,  262, 
-          263,  264,  265,  266,   -1,  268,  269,  270,  271,  272, 
-          273,  274,  275,  276,  277,  278,   -1,   -1,   -1,   -1, 
-          283,  284,  285,  286,  287,  288,  289,  290,  291,  292, 
-          293,  294,  295,  296,  297,   -1,  299,   -1,   -1,  302, 
-          303,  304,   44,  306,  307,  308,  309,  310,  311,  312, 
-          313,  314,  315,  316,  317,  318,  319,  320,  321,  322, 
-          323,  324,  325,  326,  327,  328,  329,  330,   -1,   -1, 
-          333,  334,  335,  336,   -1,   -1,  339,  340,  341,  342, 
-          343,   -1,  345,   -1,  347,  348,  349,  350,  351,  352, 
-          353,  354,  355,  356,  357,  358,  359,  360,   -1,  362, 
-           -1,  364,  365,  366,  367,  368,  369,   -1,   -1,   -1, 
-          373,   -1,  375,  376,   -1,  378,  379,  257,  258,  259, 
-           -1,  261,  262,  263,  264,  265,  266,  267,  268,  269, 
+           -1,   -1,   -1,   -1,   91,   -1,  364,  365,  366,  367, 
+          368,  369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1, 
+          378,  379,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,    0,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   10,  257,  258,  259, 
+           -1,  261,  262,  263,  264,  265,  266,   -1,  268,  269, 
           270,  271,  272,  273,  274,  275,  276,  277,  278,   -1, 
-           -1,  281,   -1,  283,  284,  285,  286,  287,  288,  289, 
+          280,   -1,   -1,  283,  284,  285,  286,  287,  288,  289, 
           290,  291,  292,  293,  294,  295,  296,  297,   -1,  299, 
-           -1,   -1,  302,  303,  304,   -1,  306,  307,  308,  309, 
-          310,  311,  312,  313,  314,  315,  316,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,    0,  328,   -1, 
-           -1,   -1,   -1,   -1,   -1,  335,  336,   10,   -1,  339, 
-           -1,  341,  342,  343,   -1,  345,   -1,  347,   -1,  349, 
-           -1,  351,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  359, 
-           -1,   -1,  362,   -1,  364,  365,  366,  367,  368,  369, 
-           -1,   44,   -1,  373,   -1,  375,  376,   -1,  378,  379, 
-           -1,   -1,   -1,   -1,   -1,   58,   59,   -1,   61,   -1, 
-           63,   -1,   -1,   -1,   -1,  257,  258,  259,   -1,  261, 
-           -1,   -1,   -1,  265,  266,   -1,   -1,   -1,  270,   -1, 
-          272,  273,  274,  275,  276,  277,  278,   -1,   91,   -1, 
-           -1,  283,  284,  285,  286,  287,  288,  289,    0,   -1, 
-          292,   -1,   -1,   -1,   -1,   -1,   -1,  299,   10,   -1, 
-          302,  303,  304,   -1,  306,  307,  308,  309,  310,  311, 
-           -1,  313,   -1,   -1,  316,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  302,  303,  304,   59,  306,  307,  308,  309, 
+          310,  311,  312,  313,  314,  315,  316,  317,  318,  319, 
+          320,  321,  322,  323,  324,  325,  326,  327,  328,  329, 
+          330,   -1,   -1,  333,  334,  335,  336,   -1,   -1,  339, 
+          340,  341,  342,  343,   -1,  345,   -1,  347,  348,  349, 
+          350,  351,  352,  353,  354,  355,  356,  357,  358,  359, 
+          360,  361,  362,   -1,  364,  365,  366,  367,  368,  369, 
+           -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378,  379, 
+          257,  258,  259,   -1,  261,  262,  263,  264,  265,  266, 
+           -1,  268,  269,  270,  271,  272,  273,  274,  275,  276, 
+          277,  278,   -1,   -1,   -1,   -1,  283,  284,  285,  286, 
+          287,  288,  289,  290,  291,  292,  293,  294,  295,  296, 
+          297,   -1,  299,   -1,   -1,  302,  303,  304,   44,  306, 
+          307,  308,  309,  310,  311,  312,  313,  314,  315,  316, 
+          317,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
+          327,  328,  329,  330,   -1,   -1,  333,  334,  335,  336, 
+           -1,   -1,  339,  340,  341,  342,  343,   -1,  345,   -1, 
+          347,  348,  349,  350,  351,  352,  353,  354,  355,  356, 
+          357,  358,  359,  360,   -1,  362,   -1,  364,  365,  366, 
+          367,  368,  369,   -1,   -1,   -1,  373,   -1,  375,  376, 
+           -1,  378,  379,  257,  258,  259,   -1,  261,  262,  263, 
+          264,  265,  266,  267,  268,  269,  270,  271,  272,  273, 
+          274,  275,  276,  277,  278,   -1,   -1,  281,   -1,  283, 
+          284,  285,  286,  287,  288,  289,  290,  291,  292,  293, 
+          294,  295,  296,  297,   -1,  299,   -1,   -1,  302,  303, 
+          304,   -1,  306,  307,  308,  309,  310,  311,  312,  313, 
+          314,  315,  316,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,    0,  328,   -1,   -1,   -1,   -1,   -1, 
+           -1,  335,  336,   10,   -1,  339,   -1,  341,  342,  343, 
+           -1,  345,   -1,  347,   -1,  349,   -1,  351,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  359,   -1,   -1,  362,   -1, 
+          364,  365,  366,  367,  368,  369,   -1,   44,   -1,  373, 
+           -1,  375,  376,   -1,  378,  379,   -1,   -1,   -1,   -1, 
+           -1,   58,   59,   -1,   61,   -1,   63,   -1,   -1,   -1, 
+           -1,  257,  258,  259,   -1,  261,   -1,   -1,   -1,  265, 
+          266,   -1,   -1,   -1,  270,   -1,  272,  273,  274,  275, 
+          276,  277,  278,   -1,   91,   -1,   -1,  283,  284,  285, 
+          286,  287,  288,  289,    0,   -1,  292,   -1,   -1,   -1, 
+           -1,   -1,   -1,  299,   10,   -1,  302,  303,  304,   -1, 
+          306,  307,  308,  309,  310,  311,   -1,  313,   -1,   -1, 
+          316,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1, 
+          336,   -1,   -1,  339,   -1,   -1,  342,  343,   -1,  345, 
+           -1,   -1,   58,   59,   -1,   -1,   -1,   63,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  364,  365, 
+          366,  367,  368,  369,   -1,   -1,   -1,  373,   -1,  375, 
+          376,   -1,  378,  379,   -1,   91,   -1,   -1,   -1,  680, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  690, 
+           -1,   -1,  693,   -1,   -1,   -1,   -1,   -1,    0,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   10,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   44,   -1,  336,   -1,   -1,  339,   -1,   -1, 
-          342,  343,   -1,  345,   -1,   -1,   58,   59,   -1,   -1, 
-           -1,   63,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  364,  365,  366,  367,  368,  369,   -1,   -1, 
-           -1,  373,   -1,  375,  376,   -1,  378,  379,   -1,   91, 
-          306,  307,   -1,   -1,  310,   -1,   -1,   -1,  314,  315, 
-           -1,  317,  318,  319,  320,  321,  322,  323,   -1,   -1, 
-          326,  327,    0,   -1,   -1,  331,  332,  333,  334,   -1, 
-           -1,   -1,   10,   -1,  340,   -1,   -1,   -1,   -1,   -1, 
-           -1,  347,  348,   -1,  350,  351,  352,  353,  354,  355, 
-          356,  357,  358,  359,  360,   -1,   -1,  363,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   44,   -1,   -1,  262, 
-          263,  264,   -1,   -1,  267,  268,  269,   -1,  271,   -1, 
-           58,   59,   -1,   61,   -1,   63,   -1,  280,  281,  282, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1, 
-          293,  294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  305,   91,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  317,  318,  319,  320,  321,  322, 
-          323,  324,  325,  326,  327,  328,  329,  330,   -1,   -1, 
-          333,  334,  335,   -1,   -1,  338,   -1,   -1,  341,   -1, 
-           -1,  344,   -1,  346,   -1,  348,    0,  350,   -1,  352, 
-          353,  354,  355,  356,  357,  358,   10,  360,  361,  362, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   44,   -1,   -1,  262,  263,  264,   -1,   -1, 
+          267,  268,  269,   -1,  271,   -1,   58,   59,   -1,   61, 
+           -1,   63,   -1,  280,  281,  282,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  290,  291,   -1,  293,  294,  295,  296, 
+          297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305,   91, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          317,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
+          327,  328,  329,  330,   -1,   -1,  333,  334,  335,   -1, 
+           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
+           -1,  348,    0,  350,   -1,  352,  353,  354,  355,  356, 
+          357,  358,   10,  360,  361,  362,  262,  263,  264,   -1, 
+           -1,  267,  268,  269,  855,  271,  857,  374,  859,   -1, 
+           -1,   -1,  863,   -1,  280,  281,  282,  868,   -1,   -1, 
+           -1,   -1,   -1,   -1,  290,  291,   44,  293,  294,  295, 
+          296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305, 
+           58,   59,   -1,   -1,   -1,   63,   -1,   -1,   -1,   -1, 
+           -1,  317,  318,  319,  320,  321,  322,  323,  324,  325, 
+          326,  327,  328,  329,  330,   -1,   -1,  333,  334,  335, 
+           -1,   -1,  338,   91,  925,  341,   -1,   -1,  344,   -1, 
+          346,  932,  348,  934,  350,  936,  352,  353,  354,  355, 
+          356,  357,  358,   -1,  360,  361,  362,   -1,   -1,   -1, 
+           -1,   -1,  953,   -1,   -1,   -1,   -1,   -1,  374,   -1, 
           262,  263,  264,   -1,   -1,  267,  268,  269,   -1,  271, 
-           -1,  374,   -1,   -1,   -1,   -1,   -1,   -1,  280,  281, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281, 
           282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291, 
-           44,  293,  294,  295,  296,  297,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  305,   58,   59,   -1,   -1,   -1,   63, 
+           -1,  293,  294,  295,  296,  297,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,  317,  318,  319,  320,  321, 
           322,  323,  324,  325,  326,  327,  328,  329,  330,   -1, 
-           -1,  333,  334,  335,   -1,   -1,  338,   91,   -1,  341, 
-           -1,   -1,  344,   -1,  346,   -1,  348,   -1,  350,   -1, 
-          352,  353,  354,  355,  356,  357,  358,   -1,  360,  361, 
+            0,  333,  334,  335,   -1,  337,  338,   -1,   -1,  341, 
+           10,   -1,  344,   -1,  346,   -1,  348,   -1,  350,   -1, 
+          352,  353,  354,  355,  356,  357,  358,   -1,  360,   -1, 
           362,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  374,   -1,  262,  263,  264,   -1,   -1,  267, 
+           -1,   -1,  374,   -1,   44,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   58,   59, 
+           -1,   61,   -1,   63,  262,  263,  264,   -1,   -1,  267, 
           268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  290,  291,   -1,  293,  294,  295,  296,  297, 
+           -1,   -1,  280,  281,  282,   -1,   -1,   -1,   -1,   -1, 
+           -1,   91,  290,  291,   -1,  293,  294,  295,  296,  297, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,  305,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  317, 
           318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
-          328,  329,  330,   -1,    0,  333,  334,  335,   -1,  337, 
+          328,  329,  330,   -1,    0,  333,  334,  335,   -1,   -1, 
           338,   -1,   -1,  341,   10,   -1,  344,   -1,  346,   -1, 
           348,   -1,  350,   -1,  352,  353,  354,  355,  356,  357, 
-          358,   -1,  360,   -1,  362,   -1,   -1,   -1,   -1,   -1, 
+          358,   -1,  360,  361,  362,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   44,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   58,   59,   -1,   61,   -1,   63,  262,  263, 
-          264,   -1,   -1,  267,  268,  269,   -1,  271,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  280,  281,  282,   -1, 
-           -1,   -1,   -1,   -1,   -1,   91,  290,  291,   -1,  293, 
-          294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  317,  318,  319,  320,  321,  322,  323, 
-          324,  325,  326,  327,  328,  329,  330,   -1,    0,  333, 
-          334,  335,   -1,   -1,  338,   -1,   -1,  341,   10,   -1, 
-          344,   -1,  346,   -1,  348,   -1,  350,   -1,  352,  353, 
-          354,  355,  356,  357,  358,   -1,  360,  361,  362,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          374,   -1,   44,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  306,  307,   58,   59,  310,   61, 
-           -1,   63,  314,  315,   -1,  317,  318,  319,  320,  321, 
-          322,  323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331, 
-          332,  333,  334,   -1,   -1,   -1,   -1,   -1,  340,   91, 
-           -1,   -1,   -1,   -1,   -1,  347,  348,   -1,  350,  351, 
-          352,  353,  354,  355,  356,  357,  358,  359,  360,   -1, 
-           -1,  363,    0,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   10,   -1,   -1,   -1,  262,  263,  264,   -1, 
-           -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  290,  291,   44,  293,  294,  295, 
+          306,  307,   58,   59,  310,   61,   -1,   63,  314,  315, 
+           -1,  317,  318,  319,  320,  321,  322,  323,   -1,   -1, 
+          326,  327,   -1,   -1,   -1,  331,  332,  333,  334,   -1, 
+           -1,   -1,   -1,   -1,  340,   91,   -1,   -1,   -1,   -1, 
+           -1,  347,  348,   -1,  350,  351,  352,  353,  354,  355, 
+          356,  357,  358,  359,  360,   -1,   -1,  363,    0,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   10,   -1, 
+           -1,   -1,  262,  263,  264,   -1,   -1,  267,  268,  269, 
+           -1,  271,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          290,  291,   44,  293,  294,  295,  296,  297,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  305,   58,   59,   -1,   -1, 
+           -1,   63,   -1,   -1,   -1,   -1,   -1,  317,  318,  319, 
+          320,  321,  322,  323,  324,  325,  326,  327,  328,  329, 
+          330,   -1,   -1,  333,  334,  335,   -1,   -1,  338,   91, 
+           -1,  341,   -1,   -1,  344,   -1,  346,   -1,  348,    0, 
+          350,   -1,  352,  353,  354,  355,  356,  357,  358,   10, 
+          360,   -1,  362,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  374,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   44,   -1,   -1,  262,  263,  264,   -1, 
+           -1,  267,  268,  269,   -1,  271,   -1,   58,   59,   -1, 
+           -1,   -1,   63,   -1,   -1,  281,  282,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  290,  291,   -1,  293,  294,  295, 
           296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305, 
-           58,   59,   -1,   -1,   -1,   63,   -1,   -1,   -1,   -1, 
+           91,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,  317,  318,  319,  320,  321,  322,  323,  324,  325, 
           326,  327,  328,  329,  330,   -1,   -1,  333,  334,  335, 
-           -1,   -1,  338,   91,   -1,  341,   -1,   -1,  344,   -1, 
-          346,   -1,  348,    0,  350,   -1,  352,  353,  354,  355, 
-          356,  357,  358,   10,  360,   -1,  362,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  374,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1,   -1, 
+           -1,   -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1, 
+          346,   -1,  348,   -1,  350,   -1,  352,  353,  354,  355, 
+          356,  357,  358,    0,  360,   -1,  362,   -1,   -1,   -1, 
+           -1,   -1,   -1,   10,   -1,   -1,   -1,   -1,  374,   -1, 
           262,  263,  264,   -1,   -1,  267,  268,  269,   -1,  271, 
-           -1,   58,   59,   -1,   -1,   -1,   63,   -1,   -1,  281, 
-          282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281, 
+          282,   -1,   -1,   -1,   -1,   -1,   -1,   44,  290,  291, 
            -1,  293,  294,  295,  296,  297,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  305,   91,   -1,   -1,   -1,   -1,   -1, 
+           -1,   58,   59,  305,   -1,   -1,   63,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,  317,  318,  319,  320,  321, 
           322,  323,  324,  325,  326,  327,  328,  329,  330,   -1, 
-           -1,  333,  334,  335,   -1,   -1,  338,   -1,   -1,  341, 
-           -1,   -1,  344,   -1,  346,   -1,  348,   -1,  350,   -1, 
-          352,  353,  354,  355,  356,  357,  358,    0,  360,   -1, 
-          362,   -1,   -1,   -1,   -1,   -1,   -1,   10,   -1,   -1, 
-           -1,   -1,  374,   -1,  262,  263,  264,   -1,   -1,  267, 
-          268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1, 
-           -1,   44,  290,  291,   -1,  293,  294,  295,  296,  297, 
-           -1,   -1,   -1,   -1,   -1,   58,   59,  305,   -1,   -1, 
-           63,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  317, 
-          318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
-          328,  329,  330,   -1,   -1,  333,  334,  335,   91,   -1, 
-          338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346,   -1, 
-          348,    0,  350,   -1,  352,  353,  354,  355,  356,  357, 
-          358,   10,  360,   -1,  362,  262,  263,  264,   -1,   -1, 
-          267,  268,  269,   -1,  271,   -1,  374,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  290,  291,   44,  293,  294,  295,  296, 
-          297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305,   58, 
-           59,   -1,   -1,   -1,   63,   -1,   -1,   -1,   -1,   -1, 
-          317,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
-          327,  328,  329,  330,   -1,   -1,  333,  334,  335,   -1, 
-           -1,  338,   91,   -1,  341,   -1,   -1,  344,   -1,  346, 
-           -1,  348,   -1,  350,   -1,  352,  353,  354,  355,  356, 
-          357,  358,   -1,  360,    0,  362,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   10,   -1,   -1,  374,   -1,   -1, 
+           -1,  333,  334,  335,   91,   -1,  338,   -1,   -1,  341, 
+           -1,   -1,  344,   -1,  346,   -1,  348,    0,  350,   -1, 
+          352,  353,  354,  355,  356,  357,  358,   10,  360,   -1, 
+          362,  262,  263,  264,   -1,   -1,  267,  268,  269,   -1, 
+          271,   -1,  374,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          281,  282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  290, 
+          291,   44,  293,  294,  295,  296,  297,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  305,   58,   59,   -1,   -1,   -1, 
+           63,   -1,   -1,   -1,   -1,   -1,  317,  318,  319,  320, 
+          321,  322,  323,  324,  325,  326,  327,  328,  329,  330, 
+           -1,   -1,  333,  334,  335,   -1,   -1,  338,   91,   -1, 
+          341,   -1,   -1,  344,   -1,  346,   -1,  348,   -1,  350, 
+           -1,  352,  353,  354,  355,  356,  357,  358,   -1,  360, 
+            0,  362,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           10,   -1,   -1,  374,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,  262, 
-          263,  264,   -1,   -1,  267,  268,  269,   -1,  271,   -1, 
-           -1,   -1,   58,   59,   -1,   -1,   -1,   63,  281,  282, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1, 
+           -1,   -1,   -1,   -1,   44,  262,  263,  264,   -1,   -1, 
+          267,  268,  269,   -1,  271,   -1,   -1,   -1,   58,   59, 
+           -1,   -1,   -1,   63,  281,  282,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  290,  291,   -1,  293,  294,  295,  296, 
+          297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305,   -1, 
+           -1,   91,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          317,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
+          327,  328,  329,  330,   -1,   -1,  333,  334,  335,   -1, 
+           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
+           -1,  348,    0,  350,   -1,  352,  353,  354,  355,  356, 
+          357,  358,   10,  360,   -1,  362,   -1,   -1,   -1,  262, 
+          263,  264,   -1,   -1,  267,  268,  269,  374,  271,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281,  282, 
+           -1,   -1,   -1,   -1,   -1,   -1,   44,  290,  291,   -1, 
           293,  294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  305,   -1,   -1,   91,   -1,   -1,   -1,   -1, 
+           58,   59,  305,   -1,   -1,   63,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,  317,  318,  319,  320,  321,  322, 
           323,  324,  325,  326,  327,  328,  329,  330,   -1,   -1, 
-          333,  334,  335,   -1,   -1,  338,   -1,   -1,  341,   -1, 
-           -1,  344,   -1,  346,   -1,  348,    0,  350,   -1,  352, 
-          353,  354,  355,  356,  357,  358,   10,  360,   -1,  362, 
-           -1,   -1,   -1,  262,  263,  264,   -1,   -1,  267,  268, 
-          269,  374,  271,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1, 
-           44,  290,  291,   -1,  293,  294,  295,  296,  297,   -1, 
-           -1,   -1,   -1,   -1,   58,   59,  305,   -1,   -1,   63, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  317,  318, 
-          319,  320,  321,  322,  323,  324,  325,  326,  327,  328, 
-          329,  330,   -1,   -1,  333,  334,  335,   91,   -1,  338, 
-           -1,   -1,  341,   -1,   -1,  344,   -1,  346,   -1,  348, 
-           -1,  350,   -1,  352,  353,  354,  355,  356,  357,  358, 
-           -1,  360,    0,  362,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   10,   -1,   -1,  374,  262,  263,  264,   -1, 
+          333,  334,  335,   91,   -1,  338,   -1,   -1,  341,   -1, 
+           -1,  344,   -1,  346,   -1,  348,   -1,  350,   -1,  352, 
+          353,  354,  355,  356,  357,  358,   -1,  360,    0,  362, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   10,   -1, 
+           -1,  374,  262,  263,  264,   -1,   -1,  267,  268,  269, 
+           -1,  271,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          290,  291,   44,  293,  294,  295,  296,  297,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  305,   58,   59,   -1,   -1, 
+           -1,   63,   -1,   -1,   -1,   -1,   -1,   -1,  318,  319, 
+          320,  321,  322,  323,  324,  325,  326,  327,  328,  329, 
+          330,   -1,   -1,  333,  334,  335,   -1,   -1,  338,   -1, 
+           -1,  341,   -1,   -1,  344,   -1,  346,   -1,  348,    0, 
+          350,   -1,  352,  353,  354,  355,  356,  357,  358,   10, 
+          360,   -1,  362,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  374,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   44,  262,  263,  264,   -1,   -1,  267, 
+          268,  269,   -1,  271,   -1,   -1,   -1,   58,   59,   -1, 
+           -1,   -1,   63,  281,  282,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  290,  291,   -1,  293,  294,  295,  296,  297, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  305,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
+          328,  329,  330,   -1,    0,  333,  334,  335,   -1,   -1, 
+          338,   -1,   -1,  341,   10,   -1,  344,   -1,  346,   -1, 
+          348,   -1,  350,   -1,  352,  353,  354,  355,  356,  357, 
+          358,   -1,  360,   -1,  362,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   44,   -1, 
+          262,  263,  264,   -1,   -1,  267,  268,  269,   -1,  271, 
+           -1,   -1,   58,   59,   -1,   -1,   -1,   63,   -1,  281, 
+          282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291, 
+           -1,  293,  294,  295,  296,  297,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  318,  319,  320,  321, 
+          322,  323,  324,  325,  326,  327,   -1,  329,  330,   -1, 
+           -1,  333,  334,   -1,   -1,   -1,  338,   -1,   -1,  341, 
+           -1,   -1,  344,   -1,  346,   -1,  348,    0,  350,   -1, 
+          352,  353,  354,  355,  356,  357,  358,   10,  360,   -1, 
+          362,  262,  263,  264,   -1,   -1,  267,  268,  269,   -1, 
+          271,   -1,  374,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          281,  282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  290, 
+          291,   44,  293,  294,  295,  296,  297,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  305,   58,   59,   -1,   -1,   -1, 
+           63,   -1,   -1,   -1,   -1,   -1,   -1,  318,  319,  320, 
+          321,  322,  323,  324,  325,  326,  327,   -1,  329,  330, 
+           -1,   -1,  333,  334,   -1,   -1,   -1,  338,   -1,   -1, 
+          341,   -1,   -1,  344,   -1,  346,   -1,  348,   -1,  350, 
+           -1,  352,  353,  354,  355,  356,  357,  358,   -1,  360, 
+           -1,  362,    0,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   10,  374,   -1,   -1,  262,  263,  264,   -1, 
            -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,  290,  291,   44,  293,  294,  295, 
           296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305, 
            58,   59,   -1,   -1,   -1,   63,   -1,   -1,   -1,   -1, 
            -1,   -1,  318,  319,  320,  321,  322,  323,  324,  325, 
-          326,  327,  328,  329,  330,   -1,   -1,  333,  334,  335, 
+          326,  327,   -1,  329,  330,   -1,   -1,  333,  334,   -1, 
            -1,   -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1, 
           346,   -1,  348,    0,  350,   -1,  352,  353,  354,  355, 
           356,  357,  358,   10,  360,   -1,  362,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  374,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,  262,  263, 
-          264,   -1,   -1,  267,  268,  269,   -1,  271,   -1,   -1, 
-           -1,   58,   59,   -1,   -1,   -1,   63,  281,  282,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1,  293, 
-          294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  318,  319,  320,  321,  322,  323, 
-          324,  325,  326,  327,  328,  329,  330,   -1,    0,  333, 
-          334,  335,   -1,   -1,  338,   -1,   -1,  341,   10,   -1, 
-          344,   -1,  346,   -1,  348,   -1,  350,   -1,  352,  353, 
-          354,  355,  356,  357,  358,   -1,  360,   -1,  362,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          374,   -1,   44,   -1,  262,  263,  264,   -1,   -1,  267, 
-          268,  269,   -1,  271,   -1,   -1,   58,   59,   -1,   -1, 
-           -1,   63,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1,  262, 
+          263,  264,   -1,   -1,  267,  268,  269,   -1,  271,   -1, 
+           -1,   58,   59,   -1,   -1,   -1,   63,   -1,  281,  282, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1, 
+          293,  294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  318,  319,  320,  321,  322, 
+          323,  324,  325,  326,  327,   -1,  329,  330,   -1,    0, 
+          333,  334,   -1,   -1,   -1,  338,   -1,   -1,  341,   10, 
+           -1,  344,   -1,  346,   -1,  348,   -1,  350,   -1,  352, 
+          353,  354,  355,  356,  357,  358,   -1,  360,   -1,  362, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  374,   -1,   44,  262,  263,  264,   -1,   -1,  267, 
+          268,  269,   -1,  271,   -1,   -1,   -1,   58,   59,   -1, 
+           -1,   -1,   63,  281,  282,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,  290,  291,   -1,  293,  294,  295,  296,  297, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,  305,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
           318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
            -1,  329,  330,   -1,   -1,  333,  334,   -1,   -1,   -1, 
           338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346,   -1, 
           348,    0,  350,   -1,  352,  353,  354,  355,  356,  357, 
           358,   10,  360,   -1,  362,  262,  263,  264,   -1,   -1, 
           267,  268,  269,   -1,  271,   -1,  374,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,  290,  291,   44,  293,  294,  295,  296, 
           297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305,   58, 
            59,   -1,   -1,   -1,   63,   -1,   -1,   -1,   -1,   -1, 
            -1,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
           327,   -1,  329,  330,   -1,   -1,  333,  334,   -1,   -1, 
            -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
            -1,  348,   -1,  350,   -1,  352,  353,  354,  355,  356, 
-          357,  358,   -1,  360,   -1,  362,    0,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   10,  374,   -1,   -1, 
-          262,  263,  264,   -1,   -1,  267,  268,  269,   -1,  271, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281, 
-          282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291, 
-           44,  293,  294,  295,  296,  297,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  305,   58,   59,   -1,   -1,   -1,   63, 
-           -1,   -1,   -1,   -1,   -1,   -1,  318,  319,  320,  321, 
-          322,  323,  324,  325,  326,  327,   -1,  329,  330,   -1, 
-           -1,  333,  334,   -1,   -1,   -1,  338,   -1,   -1,  341, 
-           -1,   -1,  344,   -1,  346,   -1,  348,    0,  350,   -1, 
-          352,  353,  354,  355,  356,  357,  358,   10,  360,   -1, 
-          362,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  374,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   44,   -1,  262,  263,  264,   -1,   -1,  267,  268, 
-          269,   -1,  271,   -1,   -1,   58,   59,   -1,   -1,   -1, 
-           63,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1, 
+          357,  358,   -1,  360,    0,  362,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   10,   -1,   -1,  374,   -1,   -1, 
+           -1,  262,  263,  264,   -1,   -1,  267,  268,  269,   -1, 
+          271,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          281,  282,   -1,   -1,   -1,   -1,   -1,   -1,   44,  290, 
+          291,   -1,  293,  294,  295,  296,  297,   -1,   -1,   -1, 
+           -1,   -1,   58,   59,  305,   -1,   -1,   63,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  318,  319,  320, 
+          321,  322,  323,  324,  325,  326,  327,   -1,  329,  330, 
+           -1,   -1,  333,  334,   -1,   -1,   -1,  338,   -1,   -1, 
+          341,   -1,   -1,  344,   -1,  346,   -1,  348,    0,  350, 
+           -1,  352,  353,  354,  355,  356,  357,  358,   10,  360, 
+           -1,  362,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  374,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   44,  262,  263,  264,   -1,   -1,  267,  268, 
+          269,   -1,  271,   -1,   -1,   -1,   58,   59,   -1,   -1, 
+           -1,   63,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,  290,  291,   -1,  293,  294,  295,  296,  297,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,  305,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  318, 
           319,  320,  321,  322,  323,  324,  325,  326,  327,   -1, 
-          329,  330,   -1,    0,  333,  334,   -1,   -1,   -1,  338, 
-           -1,   -1,  341,   10,   -1,  344,   -1,  346,   -1,  348, 
-           -1,  350,   -1,  352,  353,  354,  355,  356,  357,  358, 
+          329,  330,   -1,   -1,  333,  334,   -1,   -1,   -1,  338, 
+            0,   -1,  341,   -1,   -1,  344,   -1,  346,   -1,   -1, 
+           10,  350,   -1,   -1,   -1,  354,  355,  356,  357,  358, 
            -1,  360,   -1,  362,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  374,   -1,   44,  262,  263, 
-          264,   -1,   -1,  267,  268,  269,   -1,  271,   -1,   -1, 
-           -1,   58,   59,   -1,   -1,   -1,   63,  281,  282,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1,  293, 
-          294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  318,  319,  320,  321,  322,  323, 
-          324,  325,  326,  327,   -1,  329,  330,   -1,   -1,  333, 
-          334,   -1,   -1,   -1,  338,   -1,   -1,  341,   -1,   -1, 
-          344,   -1,  346,   -1,  348,    0,  350,   -1,  352,  353, 
-          354,  355,  356,  357,  358,   10,  360,   -1,  362,  262, 
-          263,  264,   -1,   -1,  267,  268,  269,   -1,  271,   -1, 
-          374,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281,  282, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   44, 
-          293,  294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  305,   58,   59,   -1,   -1,   -1,   63,   -1, 
-           -1,   -1,   -1,   -1,   -1,  318,  319,  320,  321,  322, 
-          323,  324,  325,  326,  327,   -1,  329,  330,   -1,   -1, 
-          333,  334,   -1,   -1,   -1,  338,   -1,   -1,  341,   -1, 
-           -1,  344,   -1,  346,   -1,  348,   -1,  350,   -1,  352, 
-          353,  354,  355,  356,  357,  358,   -1,  360,    0,  362, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   10,   -1, 
-           -1,  374,   -1,   -1,   -1,  262,  263,  264,   -1,   -1, 
-          267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1, 
-           -1,   -1,   44,  290,  291,   -1,  293,  294,  295,  296, 
-          297,   -1,   -1,   -1,   -1,   -1,   58,   59,  305,   -1, 
-           -1,   63,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
-          327,   -1,  329,  330,   -1,   -1,  333,  334,   -1,   -1, 
-           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
-           -1,  348,    0,  350,   -1,  352,  353,  354,  355,  356, 
-          357,  358,   10,  360,   -1,  362,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  374,  262,  263,  264,   -1, 
+           -1,  267,  268,  269,   44,  271,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  281,  282,   -1,   58,   59, 
+           -1,   -1,   -1,   63,  290,  291,   -1,  293,  294,  295, 
+          296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   44,  262,  263,  264, 
-           -1,   -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1, 
-           58,   59,   -1,   -1,   -1,   63,  281,  282,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  290,  291,   -1,  293,  294, 
-          295,  296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          305,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  318,  319,  320,  321,  322,  323,  324, 
-          325,  326,  327,   -1,  329,  330,   -1,   -1,  333,  334, 
-           -1,   -1,   -1,  338,    0,   -1,  341,   -1,   -1,  344, 
-           -1,  346,   -1,   -1,   10,  350,   -1,   -1,   -1,  354, 
-          355,  356,  357,  358,   -1,  360,   -1,  362,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  374, 
-          262,  263,  264,   -1,   -1,  267,  268,  269,   44,  271, 
+           -1,   -1,  318,  319,  320,  321,  322,  323,  324,  325, 
+          326,  327,   -1,  329,  330,   -1,   -1,  333,  334,   -1, 
+           -1,   -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1, 
+          346,   -1,   -1,    0,  350,   -1,   -1,   -1,  354,  355, 
+          356,  357,  358,   10,  360,   -1,  362,   -1,   -1,   -1, 
+          262,  263,  264,   -1,   -1,  267,  268,  269,  374,  271, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281, 
-          282,   -1,   58,   59,   -1,   -1,   -1,   63,  290,  291, 
+          282,   -1,   -1,   -1,   -1,   -1,   -1,   44,  290,  291, 
            -1,  293,  294,  295,  296,  297,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   58,   59,  305,   -1,   -1,   63,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,  318,  319,  320,  321, 
           322,  323,  324,  325,  326,  327,   -1,  329,  330,   -1, 
            -1,  333,  334,   -1,   -1,   -1,  338,   -1,   -1,  341, 
-           -1,   -1,  344,   -1,  346,   -1,   -1,    0,  350,   -1, 
-           -1,   -1,  354,  355,  356,  357,  358,   10,  360,   -1, 
-          362,   -1,   -1,   -1,  262,  263,  264,   -1,   -1,  267, 
-          268,  269,  374,  271,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1, 
-           -1,   44,  290,  291,   -1,  293,  294,  295,  296,  297, 
-           -1,   -1,   -1,   -1,   -1,   58,   59,  305,   -1,   -1, 
-           63,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
-           -1,  329,  330,   -1,   -1,  333,  334,   -1,   -1,   -1, 
-          338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346,   -1, 
-           -1,   -1,  350,   -1,   -1,   -1,   -1,   -1,  356,  357, 
-          358,   -1,  360,   -1,  362,    0,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   10,  374,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  262,  263,  264,   -1, 
-           -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   44, 
-           -1,   -1,   -1,   -1,  290,  291,   -1,  293,  294,  295, 
-          296,  297,   -1,   58,   59,   -1,   -1,   -1,   63,  305, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  318,  319,  320,  321,  322,  323,  324,  325, 
-          326,  327,   -1,  329,  330,   -1,   -1,  333,  334,   -1, 
-           -1,   -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1, 
-          346,   -1,   -1,    0,  350,   -1,   -1,   -1,   -1,   -1, 
-          356,  357,  358,   10,  360,   -1,  362,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  374,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  262, 
-          263,  264,   -1,   -1,  267,  268,  269,   44,  271,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281,  282, 
-           -1,   58,   59,   -1,   -1,   -1,   63,  290,  291,   -1, 
-          293,  294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  318,  319,  320,  321,  322, 
-          323,  324,  325,  326,  327,   -1,  329,  330,   -1,   -1, 
-           -1,   -1,    0,   -1,   -1,  338,   -1,   -1,  341,   -1, 
-           -1,  344,   10,  346,   -1,   -1,   -1,  350,   -1,   -1, 
-           -1,   -1,   -1,  356,  357,  358,   -1,  360,   -1,  362, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  374,   -1,   -1,   -1,   -1,   44,  262,  263,  264, 
-           -1,   -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1, 
-           58,   59,   -1,   -1,   -1,   63,  281,  282,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  290,  291,   -1,  293,  294, 
-          295,  296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          305,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  318,  319,  320,  321,  322,  323,  324, 
-          325,  326,  327,   -1,  329,  330,   -1,   -1,   -1,   -1, 
-           -1,   -1,    0,  338,   -1,   -1,  341,   -1,   -1,  344, 
-           -1,  346,   10,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  356,  357,  358,   -1,  360,   -1,  362,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  262,  263,  264,   -1,  374, 
-          267,  268,  269,   -1,  271,   -1,   44,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1, 
-           58,   59,   -1,  290,  291,   63,  293,  294,  295,  296, 
+           -1,   -1,  344,   -1,  346,   -1,   -1,   -1,  350,   -1, 
+           -1,   -1,   -1,   -1,  356,  357,  358,   -1,  360,   -1, 
+          362,    0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   10,  374,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  262,  263,  264,   -1,   -1,  267,  268,  269, 
+           -1,  271,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  281,  282,   -1,   -1,   44,   -1,   -1,   -1,   -1, 
+          290,  291,   -1,  293,  294,  295,  296,  297,   -1,   58, 
+           59,   -1,   -1,   -1,   63,  305,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  318,  319, 
+          320,  321,  322,  323,  324,  325,  326,  327,   -1,  329, 
+          330,   -1,   -1,  333,  334,   -1,   -1,   -1,  338,   -1, 
+           -1,  341,   -1,   -1,  344,   -1,  346,   -1,   -1,    0, 
+          350,   -1,   -1,   -1,   -1,   -1,  356,  357,  358,   10, 
+          360,   -1,  362,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  374,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  262,  263,  264,   -1,   -1, 
+          267,  268,  269,   44,  271,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  281,  282,   -1,   58,   59,   -1, 
+           -1,   -1,   63,  290,  291,   -1,  293,  294,  295,  296, 
           297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
-          327,   -1,  329,  330,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
+          327,   -1,  329,  330,   -1,   -1,   -1,   -1,    0,   -1, 
+           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   10,  346, 
+           -1,   -1,   -1,  350,   -1,   -1,   -1,   -1,   -1,  356, 
+          357,  358,   -1,  360,   -1,  362,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1, 
+           -1,   -1,   44,  262,  263,  264,   -1,   -1,  267,  268, 
+          269,   -1,  271,   -1,   -1,   -1,   58,   59,   -1,   -1, 
+           -1,   63,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  290,  291,   -1,  293,  294,  295,  296,  297,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  305,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  318, 
+          319,  320,  321,  322,  323,  324,  325,  326,  327,   -1, 
+          329,  330,   -1,   -1,   -1,   -1,   -1,   -1,    0,  338, 
+           -1,   -1,  341,   -1,   -1,  344,   -1,  346,   10,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  356,  357,  358, 
+           -1,  360,   -1,  362,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  262,  263,  264,   -1,  374,  267,  268,  269,   -1, 
+          271,   -1,   44,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          281,  282,   -1,   -1,   -1,   -1,   58,   59,   -1,  290, 
+          291,   63,  293,  294,  295,  296,  297,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  305,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  318,  319,  320, 
+          321,  322,  323,  324,  325,  326,  327,   -1,  329,  330, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  338,   -1,   -1, 
+          341,   -1,   -1,  344,   -1,  346, 
+      };
+   }
+
+   private static final short[] yyCheck3() {
+      return new short[] {
+
            -1,   -1,    0,   -1,   -1,   -1,   -1,   -1,   -1,  356, 
           357,  358,   10,  360,   -1,  362,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,  262,  263,  264,  374,   -1,  267, 
           268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,  281,  282,   -1,   44,   -1,   -1,   -1, 
            -1,   -1,  290,  291,   -1,  293,  294,  295,  296,  297, 
            58,   59,   -1,   -1,   -1,   63,   -1,  305,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
           318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
            -1,  329,  330,   -1,   -1,   -1,   -1,   -1,   -1,    0, 
           338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346,   10, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  356,  357, 
            -1,   -1,   -1,   -1,  362,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1,   -1, 
            -1,   -1,   -1,   44,  262,  263,  264,   -1,   -1,  267, 
           268,  269,   -1,  271,   -1,   -1,   -1,   58,   59,   -1, 
            -1,   -1,   63,  281,  282,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,  290,  291,   -1,  293,  294,  295,  296,  297, 
-           -1,   -1,   -1,   -1,   -1,   -1, 
-      };
-   }
-
-   private static final short[] yyCheck3() {
-      return new short[] {
-
-           -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  318,  319,  320,  321,  322,  323, 
-          324,  325,  326,  327,   -1,  329,  330,   -1,   -1,   -1, 
-           -1,    0,   -1,   -1,  338,   -1,   -1,  341,   -1,   -1, 
-          344,   10,  346,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  356,  357,   -1,   -1,   -1,   -1,  362,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          374,   -1,   -1,   -1,   -1,   44,   -1,   -1,  262,  263, 
-          264,   -1,   -1,  267,  268,  269,   -1,  271,   -1,   58, 
-           59,   -1,   -1,   -1,   63,   -1,   -1,  281,  282,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1,  293, 
-          294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  318,  319,  320,  321,  322,  323, 
-          324,  325,  326,  327,   -1,  329,  330,   -1,   -1,   -1, 
-           -1,   -1,   -1,    0,  338,   -1,   -1,  341,   -1,   -1, 
-          344,   -1,  346,   10,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  356,  357,   -1,  262,  263,  264,  362,   -1, 
-          267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1, 
-          374,   -1,   -1,   -1,  281,  282,   -1,   44,   -1,   -1, 
-           -1,   -1,   -1,  290,  291,   -1,  293,  294,  295,  296, 
-          297,   58,   59,   -1,   -1,   -1,   63,   -1,  305,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  305,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
-          327,   -1,  329,  330,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  356, 
-          357,    0,   -1,   -1,   -1,  362,   -1,   -1,   -1,   -1, 
-           -1,   10,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1, 
-           -1,   -1,   -1,  262,  263,  264,   -1,   -1,  267,  268, 
-          269,   -1,  271,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  281,  282,   -1,   44,   -1,   -1,   -1,   -1, 
-           -1,  290,  291,   -1,  293,  294,  295,  296,  297,   58, 
-           59,   -1,   -1,   -1,   63,   -1,  305,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  324,  325,   -1,   -1,   -1, 
-          329,  330,   -1,   -1,   -1,   -1,   -1,   -1,    0,  338, 
-           -1,   -1,  341,   -1,   -1,  344,   -1,  346,   10,   -1, 
+          318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
+           -1,  329,  330,   -1,   -1,   -1,   -1,    0,   -1,   -1, 
+          338,   -1,   -1,  341,   -1,   -1,  344,   10,  346,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  356,  357, 
+           -1,   -1,   -1,   -1,  362,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1,   -1, 
+           -1,   44,   -1,   -1,  262,  263,  264,   -1,   -1,  267, 
+          268,  269,   -1,  271,   -1,   58,   59,   -1,   -1,   -1, 
+           63,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  290,  291,   -1,  293,  294,  295,  296,  297, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  305,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  362,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  374,   -1,   -1,   -1,   -1, 
-           -1,   -1,   44,   -1,   -1,  262,  263,  264,   -1,   -1, 
-          267,  268,  269,   -1,  271,   -1,   58,   59,   -1,   -1, 
-           -1,   63,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  290,  291,   -1,  293,  294,  295,  296, 
-          297,   -1,   -1,   -1,    0,   -1,   -1,   -1,  305,   -1, 
-           -1,   -1,   -1,   -1,   10,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  324,  325,   -1, 
-           -1,   -1,  329,  330,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   44,  346, 
+          318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
+           -1,  329,  330,   -1,   -1,   -1,   -1,   -1,   -1,    0, 
+          338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346,   10, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  356,  357, 
+           -1,  262,  263,  264,  362,   -1,  267,  268,  269,   -1, 
+          271,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1,   -1, 
+          281,  282,   -1,   44,   -1,   -1,   -1,   -1,   -1,  290, 
+          291,   -1,  293,  294,  295,  296,  297,   58,   59,   -1, 
+           -1,   -1,   63,   -1,  305,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  318,  319,  320, 
+          321,  322,  323,  324,  325,  326,  327,   -1,  329,  330, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  338,   -1,   -1, 
+          341,   -1,   -1,  344,   -1,  346,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  356,  357,    0,   -1,   -1, 
+           -1,  362,   -1,   -1,   -1,   -1,   -1,   10,   -1,   -1, 
+           -1,   -1,   -1,  374,   -1,   -1,   -1,   -1,   -1,  262, 
+          263,  264,   -1,   -1,  267,  268,  269,   -1,  271,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281,  282, 
+           -1,   44,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1, 
+          293,  294,  295,  296,  297,   58,   59,   -1,   -1,   -1, 
+           63,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   58,   59,   -1,  362,   -1,   63,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1, 
-           -1,   -1,   -1,  262,  263,  264,   -1,   -1,  267,  268, 
-          269,   -1,  271,   -1,    0,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  281,  282,   10,   -1,   -1,   -1,   -1,   -1, 
-           -1,  290,  291,   -1,  293,  294,  295,  296,  297,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  305,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1, 
-           -1,   -1,   -1,   -1,   -1,  324,  325,   -1,   -1,   -1, 
-          329,  330,   58,   59,   -1,   -1,   -1,   63,   -1,  338, 
-           -1,   -1,  341,   -1,   -1,  344,   -1,  346,   -1,   -1, 
+           -1,  324,  325,   -1,   -1,   -1,  329,  330,   -1,   -1, 
+           -1,   -1,   -1,   -1,    0,  338,   -1,   -1,  341,   -1, 
+           -1,  344,   -1,  346,   10,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  362, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          262,  263,  264,  362,   -1,  267,  268,  269,   -1,  271, 
-           -1,    0,   -1,   -1,   -1,  374,   -1,   -1,   -1,  281, 
-          282,   10,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291, 
-           -1,  293,  294,  295,  296,  297,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   44,   -1,   -1,   -1,   -1, 
-           -1,   -1,  324,  325,   -1,   -1,   -1,  329,  330,   58, 
-           59,   -1,   -1,   -1,   63,   -1,  338,   -1,   -1,  341, 
-           -1,   -1,  344,   -1,  346,   -1,  262,  263,  264,   -1, 
+           -1,  374,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1, 
+           -1,  262,  263,  264,   -1,   -1,  267,  268,  269,   -1, 
+          271,   -1,   58,   59,   -1,   -1,   -1,   63,   -1,   -1, 
+          281,  282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  290, 
+          291,   -1,  293,  294,  295,  296,  297,   -1,   -1,   -1, 
+            0,   -1,   -1,   -1,  305,   -1,   -1,   -1,   -1,   -1, 
+           10,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  324,  325,   -1,   -1,   -1,  329,  330, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  338,   -1,   -1, 
+          341,   -1,   -1,  344,   44,  346,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   58,   59, 
+           -1,  362,   -1,   63,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  374,   -1,   -1,   -1,   -1,   -1,  262, 
+          263,  264,   -1,   -1,  267,  268,  269,   -1,  271,   -1, 
+            0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281,  282, 
+           10,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1, 
+          293,  294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   44,   -1,   -1,   -1,   -1,   -1, 
+           -1,  324,  325,   -1,   -1,   -1,  329,  330,   58,   59, 
+           -1,   -1,   -1,   63,   -1,  338,   -1,   -1,  341,   -1, 
+           -1,  344,   -1,  346,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  262,  263,  264,  362, 
            -1,  267,  268,  269,   -1,  271,   -1,    0,   -1,   -1, 
-          362,   -1,   -1,   -1,   -1,  281,  282,   10,   -1,   -1, 
-           -1,   -1,  374,   -1,  290,  291,   -1,  293,  294,  295, 
-          296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305, 
-           -1,   -1,   -1,   -1,   -1,    0,   -1,   -1,   -1,   -1, 
-           -1,   44,   -1,   -1,   -1,   10,   -1,   -1,  324,  325, 
-           -1,   -1,   -1,  329,  330,   58,   59,   -1,   -1,   -1, 
-           63,   -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1, 
-          346,   -1,   -1,   -1,   -1,   -1,  262,  263,  264,   44, 
-           -1,  267,  268,  269,   -1,  271,  362,    0,   -1,   -1, 
-           -1,   -1,   -1,   -1,   59,  281,  282,   10,  374,   -1, 
+           -1,  374,   -1,   -1,   -1,  281,  282,   10,   -1,   -1, 
            -1,   -1,   -1,   -1,  290,  291,   -1,  293,  294,  295, 
           296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  305, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,    0,   -1,   -1, 
-           -1,   44,   -1,   -1,   -1,   -1,   -1,   10,  324,  325, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   44,   -1,   -1,   -1,   -1,   -1,   -1,  324,  325, 
            -1,   -1,   -1,  329,  330,   58,   59,   -1,   -1,   -1, 
            63,   -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1, 
-          346,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   44,   -1,  262,  263,  264,  362,   -1,  267,  268, 
-          269,   -1,  271,   -1,   -1,   58,   59,   -1,  374,   -1, 
-           -1,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  290,  291,   -1,  293,  294,  295,  296,  297,   -1, 
-           -1,   -1,    0,   -1,   -1,   -1,  305,   -1,   -1,   -1, 
-           -1,   -1,   10,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  324,  325,   -1,   -1,   -1, 
-          329,  330,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  338, 
-           -1,   -1,  341,   -1,   -1,  344,   44,  346,   -1,  262, 
-          263,  264,   -1,   -1,  267,  268,  269,   -1,  271,   -1, 
-           58,   59,   -1,  362,   -1,   -1,   -1,   -1,  281,  282, 
-           -1,   -1,   -1,   -1,   -1,  374,   -1,  290,  291,   -1, 
-          293,  294,  295,  296,  297,   -1,   -1,  262,  263,  264, 
-           -1,   -1,  305,  268,  269,   -1,  271,   -1,    0,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   10,   -1, 
-           -1,   -1,  325,   -1,   -1,   -1,  329,  330,  293,  294, 
-          295,  296,  297,   -1,   -1,  338,   -1,   -1,  341,   -1, 
-           -1,  344,   -1,  346,   -1,   -1,   -1,   -1,   -1,  262, 
-          263,  264,   44,   -1,  267,  268,  269,   -1,  271,  362, 
-           -1,   -1,   -1,   -1,   -1,   -1,   58,   59,  281,  282, 
-           -1,  374,   -1,   -1,   -1,   -1,  341,  290,  291,   -1, 
-          293,  294,  295,  296,  297,   -1,   -1,   -1,   -1,  262, 
-          263,  264,  305,    0,  267,  268,  269,  362,  271,   -1, 
-           -1,   -1,   -1,   10,   -1,   -1,   -1,   -1,  281,  282, 
+          346,   -1,  262,  263,  264,   -1,   -1,  267,  268,  269, 
+           -1,  271,   -1,    0,   -1,   -1,  362,   -1,   -1,   -1, 
+           -1,  281,  282,   10,   -1,   -1,   -1,   -1,  374,   -1, 
+          290,  291,   -1,  293,  294,  295,  296,  297,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  305,   -1,   -1,   -1,   -1, 
+           -1,    0,   -1,   -1,   -1,   -1,   -1,   44,   -1,   -1, 
+           -1,   10,   -1,   -1,  324,  325,   -1,   -1,   -1,  329, 
+          330,   58,   59,   -1,   -1,   -1,   63,   -1,  338,   -1, 
+           -1,  341,   -1,   -1,  344,   -1,  346,   -1,   -1,   -1, 
+           -1,   -1,  262,  263,  264,   44,   -1,  267,  268,  269, 
+           -1,  271,  362,    0,   -1,   -1,   -1,   -1,   -1,   -1, 
+           59,  281,  282,   10,  374,   -1,   -1,   -1,   -1,   -1, 
+          290,  291,   -1,  293,  294,  295,  296,  297,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  305,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,    0,   -1,   -1,   -1,   44,   -1,   -1, 
+           -1,   -1,   -1,   10,  324,  325,   -1,   -1,   -1,  329, 
+          330,   58,   59,   -1,   -1,   -1,   63,   -1,  338,   -1, 
+           -1,  341,   -1,   -1,  344,   -1,  346,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1,  262, 
+          263,  264,  362,   -1,  267,  268,  269,   -1,  271,   -1, 
+           -1,   58,   59,   -1,  374,   -1,   63,   -1,  281,  282, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1, 
-          293,  294,  295,  296,  297,  338,   -1,   -1,  341,   -1, 
-           -1,  344,  305,  346,   -1,   -1,   -1,   44,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  362, 
-           -1,   58,   59,   -1,    0,   -1,   -1,   -1,   -1,   -1, 
-           -1,  374,   -1,   -1,   10,  338,   -1,   -1,  341,   -1, 
-           -1,  344,   -1,  346,  262,  263,  264,   -1,   -1,  267, 
-          268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1,  362, 
-           -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   44,   -1, 
-           -1,  374,  290,  291,   -1,  293,  294,  295,  296,  297, 
-           -1,   -1,   58,   59,   -1,   -1,   -1,  305,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          338,    0,   -1,  341,   -1,   -1,  344,   -1,  346,   -1, 
-           -1,   10,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          262,  263,  264,   -1,  362,  267,  268,  269,   -1,  271, 
-           -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1,  281, 
-          282,   -1,   -1,   -1,   -1,   44,   -1,   -1,  290,  291, 
-           -1,  293,  294,  295,  296,  297,   -1,   -1,   -1,   58, 
-           59,   -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,    0,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  338,   10,   -1,  341, 
-           -1,   -1,  344,   -1,  346,  262,  263,  264,   -1,   -1, 
-          267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1, 
-          362,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1, 
-           -1,   44,  374,  290,  291,   -1,  293,  294,  295,  296, 
-          297,   -1,   -1,   -1,   -1,   58,   59,   -1,  305,   -1, 
+          293,  294,  295,  296,  297,   -1,   -1,   -1,    0,   -1, 
+           -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   10,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  324,  325,   -1,   -1,   -1,  329,  330,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  338,   -1,   -1,  341,   -1, 
+           -1,  344,   44,  346,   -1,  262,  263,  264,   -1,   -1, 
+          267,  268,  269,   -1,  271,   -1,   58,   59,   -1,  362, 
+           -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1, 
+           -1,  374,   -1,  290,  291,   -1,  293,  294,  295,  296, 
+          297,   -1,   -1,  262,  263,  264,   -1,   -1,  305,  268, 
+          269,   -1,  271,   -1,    0,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   10,   -1,   -1,   -1,  325,   -1, 
+           -1,   -1,  329,  330,  293,  294,  295,  296,  297,   -1, 
+           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
+           -1,   -1,   -1,   -1,   -1,  262,  263,  264,   44,   -1, 
+          267,  268,  269,   -1,  271,  362,   -1,   -1,   -1,   -1, 
+           -1,   -1,   58,   59,  281,  282,   -1,  374,   -1,   -1, 
+           -1,   -1,  341,  290,  291,   -1,  293,  294,  295,  296, 
+          297,   -1,   -1,   -1,   -1,  262,  263,  264,  305,    0, 
+          267,  268,  269,  362,  271,   -1,   -1,   -1,   -1,   10, 
+           -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  290,  291,   -1,  293,  294,  295,  296, 
+          297,  338,   -1,   -1,  341,   -1,   -1,  344,  305,  346, 
+           -1,   -1,   -1,   44,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  362,   -1,   58,   59,   -1, 
+            0,   -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1, 
+           10,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
+          262,  263,  264,   -1,   -1,  267,  268,  269,   -1,  271, 
+           -1,   -1,   -1,   -1,   -1,  362,   -1,   -1,   -1,  281, 
+          282,   -1,   -1,   -1,   44,   -1,   -1,  374,  290,  291, 
+           -1,  293,  294,  295,  296,  297,   -1,   -1,   58,   59, 
+           -1,   -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  338,    0,   -1,  341, 
+           -1,   -1,  344,   -1,  346,   -1,   -1,   10,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,  262,  263,  264,   -1, 
-           -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1, 
-           -1,  338,   -1,   -1,  341,  281,  282,  344,   -1,  346, 
-           -1,   -1,   -1,   -1,  290,  291,    0,  293,  294,  295, 
-          296,  297,   -1,   -1,   -1,  362,   10,   -1,   -1,  305, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1, 
+          362,  267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1, 
+           -1,   -1,  374,   -1,   -1,  281,  282,   -1,   -1,   -1, 
+           -1,   44,   -1,   -1,  290,  291,   -1,  293,  294,  295, 
+          296,  297,   -1,   -1,   -1,   58,   59,   -1,   -1,  305, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           44,   -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1, 
-          346,   -1,   -1,   -1,   58,   59,   -1,    0,   -1,   -1, 
-           -1,   -1,   -1,  262,  263,  264,  362,   10,  267,  268, 
-          269,   -1,  271,   -1,   -1,   -1,   -1,   -1,  374,   -1, 
-           -1,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  290,  291,   -1,  293,  294,  295,  296,  297,   -1, 
-           -1,   44,   -1,   -1,   -1,   -1,  305,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   58,   59,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  338, 
-           -1,   -1,  341,    0,   -1,  344,   -1,  346,   -1,  262, 
-          263,  264,   -1,   10,  267,  268,  269,   -1,  271,   -1, 
-           -1,   -1,   -1,  362,   -1,   -1,   -1,   -1,  281,  282, 
-           -1,   -1,   -1,   -1,   -1,  374,   -1,  290,  291,   -1, 
+           -1,   -1,   -1,    0,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  338,   10,   -1,  341,   -1,   -1,  344,   -1, 
+          346,  262,  263,  264,   -1,   -1,  267,  268,  269,   -1, 
+          271,   -1,   -1,   -1,   -1,   -1,  362,   -1,   -1,   -1, 
+          281,  282,   -1,   -1,   -1,   -1,   -1,   44,  374,  290, 
+          291,   -1,  293,  294,  295,  296,  297,   -1,   -1,   -1, 
+           -1,   58,   59,   -1,  305,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  262,  263,  264,   -1,   -1,  267,  268,  269, 
+           -1,  271,   -1,   -1,   -1,   -1,   -1,  338,   -1,   -1, 
+          341,  281,  282,  344,   -1,  346,   -1,   -1,   -1,   -1, 
+          290,  291,    0,  293,  294,  295,  296,  297,   -1,   -1, 
+           -1,  362,   10,   -1,   -1,  305,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  374,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   44,   -1,  338,   -1, 
+           -1,  341,   -1,   -1,  344,   -1,  346,   -1,   -1,   -1, 
+           58,   59,   -1,    0,   -1,   -1,   -1,   -1,   -1,  262, 
+          263,  264,  362,   10,  267,  268,  269,   -1,  271,   -1, 
+           -1,   -1,   -1,   -1,  374,   -1,   -1,   -1,  281,  282, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1, 
           293,  294,  295,  296,  297,   -1,   -1,   44,   -1,   -1, 
            -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   58,   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  338,   -1,   -1,  341,   -1, 
-           -1,  344,   -1,  346,   -1,   -1,   -1,   -1,   -1,   -1, 
-            0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  362, 
-           10,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  262,  263, 
-          264,  374,   -1,  267,  268,  269,   -1,  271,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  281,  282,   -1, 
-           -1,   -1,   -1,   -1,   44,   -1,  290,  291,   -1,  293, 
-          294,  295,  296,  297,   -1,   -1,   -1,    0,   58,   59, 
-           -1,  305,   -1,   -1,   -1,   -1,   -1,   10,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  262, 
-          263,  264,   -1,   -1,  267,  268,  269,   -1,  271,   -1, 
-           -1,   -1,   -1,   -1,  338,   -1,   -1,  341,  281,  282, 
-          344,   44,  346,   -1,   -1,   -1,   -1,  290,  291,    0, 
-          293,  294,  295,  296,  297,   -1,   59,   -1,  362,   10, 
-           -1,   -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          374,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   44,   -1,  338,   -1,   -1,  341,   -1, 
-           -1,  344,   -1,  346,   -1,  262,  263,  264,   59,   -1, 
+           -1,   -1,   -1,   -1,   -1,  338,   -1,   -1,  341,    0, 
+           -1,  344,   -1,  346,   -1,  262,  263,  264,   -1,   10, 
           267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1,  362, 
            -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1, 
-           -1,  374,   -1,  290,  291,    0,  293,  294,  295,  296, 
-          297,   -1,   -1,   -1,   -1,   10,   -1,   -1,  305,   -1, 
-           -1,    0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   10,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-            0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   44, 
-           10,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
-           -1,   -1,   -1,   58,   59,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  262,  263,  264,  362,   -1,  267,  268,  269, 
-           59,  271,   -1,   -1,   44,   -1,   -1,  374,   -1,   -1, 
-           -1,  281,  282,   -1,   -1,   -1,   -1,   -1,   58,   59, 
-          290,  291,   -1,  293,  294,  295,  296,  297,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  305,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  262, 
-          263,  264,   -1,   -1,  267,  268,  269,   -1,  271,   -1, 
-           -1,   -1,   -1,   -1,   -1,   10,   -1,   -1,  338,   -1, 
-           -1,  341,   -1,   -1,  344,   -1,  346,   -1,   -1,   -1, 
-          293,  294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  362,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  262,  263,  264,  374,   -1,  267,  268,  269,   -1, 
-          271,   -1,   -1,   -1,   59,   -1,   -1,   -1,   -1,   -1, 
-          281,  282,   -1,   -1,   -1,  338,   -1,   -1,  341,  290, 
-          291,   -1,  293,  294,  295,  296,  297,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  305,   -1,   91,   -1,   -1,  362, 
+           -1,  374,   -1,  290,  291,   -1,  293,  294,  295,  296, 
+          297,   -1,   -1,   44,   -1,   -1,   -1,   -1,  305,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   58,   59,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  338,   -1,   -1, 
-          341,   -1,   -1,  344,   -1,  346,   -1,  262,  263,  264, 
-           -1,   10,  267,  268,  269,   -1,  271,   -1,   -1,   -1, 
-           -1,  362,   -1,  262,  263,  264,  281,  282,   -1,  268, 
-          269,   -1,  271,  374,   -1,  290,  291,   -1,  293,  294, 
-          295,  296,  262,  263,  264,   -1,   -1,  267,  268,  269, 
-          305,  271,   -1,   -1,  293,  294,  295,  296,  297,   -1, 
-           59,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          290,  291,   -1,  293,  294,  295,  296,   -1,   -1,   -1, 
-           -1,   -1,   -1,  338,   -1,  305,  341,   -1,   -1,  344, 
-           -1,  346,   91,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  341,   -1,   -1,   -1,   -1,  362,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  338,  374, 
-           -1,  341,   -1,  362,  344,   -1,  346,   -1,   -1,   -1, 
-           -1,  256,  257,  258,  259,  260,  261,  262,  263,  264, 
-          265,  266,  362,   10,  269,  270,   -1,  272,  273,  274, 
-          275,  276,  277,  278,  374,  280,   -1,   -1,  283,  284, 
-          285,  286,  287,  288,  289,   -1,   -1,  292,   -1,   -1, 
-           -1,   -1,   -1,  298,  299,   -1,  301,  302,  303,  304, 
-           -1,  306,  307,  308,  309,  310,  311,   -1,  313,  314, 
-          315,  316,   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  328,   -1,   -1,   -1,   -1,   -1,   -1, 
-          335,  336,   -1,   -1,  339,  340,   -1,  342,  343,   -1, 
-          345,   -1,  347,   -1,   91,   -1,  351,   -1,   -1,   -1, 
-           -1,  356,   -1,   -1,  359,   -1,  361,   -1,   -1,  364, 
-          365,  366,  367,  368,  369,   -1,   -1,   -1,  373,   -1, 
-          375,  376,   -1,  378,  379,   -1,   -1,  256,  257,  258, 
-          259,  260,  261,  262,  263,  264,  265,  266,   -1,   10, 
+           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
+           -1,   -1,   -1,   -1,   -1,   -1,    0,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  362,   10,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  262,  263,  264,  374,   -1,  267, 
+          268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1, 
+           44,   -1,  290,  291,   -1,  293,  294,  295,  296,  297, 
+           -1,   -1,   -1,    0,   58,   59,   -1,  305,   -1,   -1, 
+           -1,   -1,   -1,   10,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  262,  263,  264,   -1,   -1, 
+          267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1, 
+          338,   -1,   -1,  341,  281,  282,  344,   44,  346,   -1, 
+           -1,   -1,   -1,  290,  291,    0,  293,  294,  295,  296, 
+          297,   -1,   59,   -1,  362,   10,   -1,   -1,  305,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  374,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   44, 
+           -1,  338,   -1,   -1,  341,   -1,   -1,  344,   -1,  346, 
+           -1,  262,  263,  264,   59,   -1,  267,  268,  269,   -1, 
+          271,   -1,   -1,   -1,   -1,  362,   -1,   -1,   -1,   -1, 
+          281,  282,   -1,   -1,   -1,   -1,   -1,  374,   -1,  290, 
+          291,    0,  293,  294,  295,  296,  297,   -1,   -1,   -1, 
+           -1,   10,   -1,   -1,  305,   -1,   -1,    0,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   10,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,    0,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   44,   10,  338,   -1,   -1, 
+          341,   -1,   -1,  344,   -1,  346,   -1,   -1,   -1,   58, 
+           59,   44,   -1,   -1,   -1,   -1,   -1,   -1,  262,  263, 
+          264,  362,   -1,  267,  268,  269,   59,  271,   -1,   -1, 
+           44,   -1,   -1,  374,   -1,   -1,   -1,  281,  282,   -1, 
+           -1,   -1,   -1,   -1,   58,   59,  290,  291,   -1,  293, 
+          294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  305,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  262,  263,  264,   -1,   -1, 
+          267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1, 
+           -1,   10,   -1,   -1,  338,   -1,   -1,  341,   -1,   -1, 
+          344,   -1,  346,   -1,   -1,   -1,  293,  294,  295,  296, 
+          297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  362,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  262,  263,  264, 
+          374,   -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1, 
+           59,   -1,   -1,   -1,   -1,   -1,  281,  282,   -1,   -1, 
+           -1,  338,   -1,   -1,  341,  290,  291,   -1,  293,  294, 
+          295,  296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          305,   -1,   91,   -1,   -1,  362,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  338,   -1,   -1,  341,   -1,   -1,  344, 
+           -1,  346,   -1,  262,  263,  264,   -1,   10,  267,  268, 
+          269,   -1,  271,   -1,   -1,   -1,   -1,  362,   -1,  262, 
+          263,  264,  281,  282,  267,  268,  269,   -1,  271,  374, 
+           -1,  290,  291,   -1,  293,  294,  295,  296,  262,  263, 
+          264,   -1,   -1,  267,  268,  269,  305,  271,   -1,   -1, 
+          293,  294,  295,  296,  297,   -1,   59,  281,  282,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1,  293, 
+          294,  295,  296,   -1,   -1,   -1,   -1,   -1,   -1,  338, 
+           -1,  305,  341,   -1,   -1,  344,   -1,  346,   91,   -1, 
+           -1,   -1,   -1,   -1,   -1,  338,   -1,   -1,  341,   -1, 
+           -1,   -1,   -1,  362,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  338,  374,   -1,  341,   -1,  362, 
+          344,   -1,  346,   -1,   -1,   -1,   -1,  256,  257,  258, 
+          259,  260,  261,  262,  263,  264,  265,  266,  362,   10, 
           269,  270,   -1,  272,  273,  274,  275,  276,  277,  278, 
-           -1,  280,   -1,   -1,  283,  284,  285,  286,  287,  288, 
+          374,  280,   -1,   -1,  283,  284,  285,  286,  287,  288, 
           289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,  298, 
           299,   -1,  301,  302,  303,  304,   -1,  306,  307,  308, 
           309,  310,  311,   -1,  313,  314,  315,  316,   59,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  328, 
            -1,   -1,   -1,   -1,   -1,   -1,  335,  336,   -1,   -1, 
           339,  340,   -1,  342,  343,   -1,  345,   -1,  347,   -1, 
            91,   -1,  351,   -1,   -1,   -1,   -1,  356,   -1,   -1, 
           359,   -1,  361,   -1,   -1,  364,  365,  366,  367,  368, 
           369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378, 
-          379,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  256, 
-          257,  258,  259,  260,  261,  262,  263,  264,  265,  266, 
-           -1,   10,  269,  270,   -1,  272,  273,  274,  275,  276, 
-          277,  278,   -1,  280,   -1,   -1,  283,  284,  285,  286, 
-          287,  288,  289,   -1,   -1,  292,   -1,   -1,   -1,   -1, 
-           -1,  298,  299,   -1,  301,  302,  303,  304,   -1,  306, 
-          307,  308,  309,  310,  311,   -1,  313,  314,  315,  316, 
-           59,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  328,   -1,   -1,   -1,   -1,   -1,   -1,  335,  336, 
-           -1,   -1,  339,  340,   -1,  342,  343,   -1,  345,   -1, 
-          347,   -1,   91,   -1,  351,   -1,   -1,   -1,   -1,  356, 
-           -1,   -1,  359,   -1,  361,   -1,   -1,  364,  365,  366, 
-          367,  368,  369,   -1,   -1,   -1,  373,   -1,  375,  376, 
-           -1,  378,  379,   -1,   -1,  256,  257,  258,  259,  260, 
+          379,   -1,   -1,  256,  257,  258,  259,  260,  261,  262, 
+          263,  264,  265,  266,   -1,   10,  269,  270,   -1,  272, 
+          273,  274,  275,  276,  277,  278,   -1,  280,   -1,   -1, 
+          283,  284,  285,  286,  287,  288,  289,   -1,   -1,  292, 
+           -1,   -1,   -1,   -1,   -1,  298,  299,   -1,  301,  302, 
+          303,  304,   -1,  306,  307,  308,  309,  310,  311,   -1, 
+          313,  314,  315,  316,   59,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  328,   -1,   -1,   -1,   -1, 
+           -1,   -1,  335,  336,   -1,   -1,  339,  340,   -1,  342, 
+          343,   -1,  345,   -1,  347,   -1,   91,   -1,  351,   -1, 
+           -1,   -1,   -1,  356,   -1,   -1,  359,   -1,  361,   -1, 
+           -1,  364,  365,  366,  367,  368,  369,   -1,   -1,   -1, 
+          373,   -1,  375,  376,   -1,  378,  379,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  256,  257,  258,  259,  260, 
           261,  262,  263,  264,  265,  266,   -1,   10,  269,  270, 
-           -1,  272,  273,  274,  275,  276,  277,  278,   -1,   -1, 
+           -1,  272,  273,  274,  275,  276,  277,  278,   -1,  280, 
            -1,   -1,  283,  284,  285,  286,  287,  288,  289,   -1, 
            -1,  292,   -1,   -1,   -1,   -1,   -1,  298,  299,   -1, 
           301,  302,  303,  304,   -1,  306,  307,  308,  309,  310, 
           311,   -1,  313,  314,  315,  316,   59,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,  328,   -1,   -1, 
            -1,   -1,   -1,   -1,  335,  336,   -1,   -1,  339,  340, 
-           -1,  342,  343,   -1,  345,   -1,  347,   -1,   -1,   -1, 
+           -1,  342,  343,   -1,  345,   -1,  347,   -1,   91,   -1, 
           351,   -1,   -1,   -1,   -1,  356,   -1,   -1,  359,   -1, 
-           -1,   -1,   -1,  364,  365,  366,  367,  368,  369,   -1, 
+          361,   -1,   -1,  364,  365,  366,  367,  368,  369,   -1, 
            -1,   -1,  373,   -1,  375,  376,   -1,  378,  379,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,  258, 
-          259,  260,  261,  262,  263,  264,  265,  266,   -1,   10, 
-          269,  270,   -1,  272,  273,  274,  275,  276,  277,  278, 
-           -1,   -1,   -1,   -1,  283,  284,  285,  286,  287,  288, 
-          289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,  298, 
-          299,   -1,  301,  302,  303,  304,   -1,  306,  307,  308, 
-          309,  310,  311,   -1,  313,  314,  315,  316,   59,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  328, 
-           -1,   -1,   -1,   -1,   -1,   -1,  335,  336,   -1,   -1, 
-          339,   -1,   -1,  342,  343,   -1,  345,   -1,  347,   -1, 
-           -1,   -1,  351,   -1,   -1,   -1,   -1,  356,   -1,   -1, 
-          359,   -1,   -1,   -1,   -1,  364,  365,  366,  367,  368, 
-          369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378, 
-          379,   -1,   -1,  256,  257,  258,  259,  260,  261,  262, 
-          263,  264,  265,  266,   -1,  268,  269,  270,  271,  272, 
-          273,  274,  275,  276,  277,  278,   10,   -1,   -1,   -1, 
+           -1,  256,  257,  258,  259,  260,  261,  262,  263,  264, 
+          265,  266,   -1,   10,  269,  270,   -1,  272,  273,  274, 
+          275,  276,  277,  278,   -1,   -1,   -1,   -1,  283,  284, 
+          285,  286,  287,  288,  289,   -1,   -1,  292,   -1,   -1, 
+           -1,   -1,   -1,  298,  299,   -1,  301,  302,  303,  304, 
+           -1,  306,  307,  308,  309,  310,  311,   -1,  313,  314, 
+          315,  316,   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  328,   -1,   -1,   -1,   -1,   -1,   -1, 
+          335,  336,   -1,   -1,  339,  340,   -1,  342,  343,   -1, 
+          345,   -1,  347,   -1,   -1,   -1,  351,   -1,   -1,   -1, 
+           -1,  356,   -1,   -1,  359,   -1,   -1,   -1,   -1,  364, 
+          365,  366,  367,  368,  369,   -1,   -1,   -1,  373,   -1, 
+          375,  376,   -1,  378,  379,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  256,  257,  258,  259,  260,  261,  262, 
+          263,  264,  265,  266,   -1,   10,  269,  270,   -1,  272, 
+          273,  274,  275,  276,  277,  278,   -1,   -1,   -1,   -1, 
           283,  284,  285,  286,  287,  288,  289,   -1,   -1,  292, 
            -1,   -1,   -1,   -1,   -1,  298,  299,   -1,  301,  302, 
           303,  304,   -1,  306,  307,  308,  309,  310,  311,   -1, 
-          313,  314,  315,  316,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   59,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  336,   -1,   -1,  339,   -1,   -1,  342, 
+          313,  314,  315,  316,   59,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  328,   -1,   -1,   -1,   -1, 
+           -1,   -1,  335,  336,   -1,   -1,  339,   -1,   -1,  342, 
           343,   -1,  345,   -1,  347,   -1,   -1,   -1,  351,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  359,   -1,   -1,   -1, 
+           -1,   -1,   -1,  356,   -1,   -1,  359,   -1,   -1,   -1, 
            -1,  364,  365,  366,  367,  368,  369,   -1,   -1,   -1, 
-          373,   -1,  375,  376,   -1,  378,  379,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  256,  257,  258,  259,  260, 
-          261,   -1,   -1,  264,  265,  266,   -1,   -1,   -1,  270, 
-           10,  272,  273,  274,  275,  276,  277,  278,   -1,   -1, 
-           -1,   -1,  283,  284,  285,  286,  287,  288,  289,   -1, 
-           -1,  292,   -1,   -1,   -1,   -1,   -1,  298,  299,   -1, 
-          301,  302,  303,  304,   -1,  306,  307,  308,  309,  310, 
-          311,   -1,  313,  314,  315,  316,   -1,   -1,   -1,   59, 
+          373,   -1,  375,  376,   -1,  378,  379,   -1,   -1,  256, 
+          257,  258,  259,  260,  261,  262,  263,  264,  265,  266, 
+           -1,  268,  269,  270,  271,  272,  273,  274,  275,  276, 
+          277,  278,   10,   -1,   -1,   -1,  283,  284,  285,  286, 
+          287,  288,  289,   -1,   -1,  292,   -1,   -1,   -1,   -1, 
+           -1,  298,  299,   -1,  301,  302,  303,  304,   -1,  306, 
+          307,  308,  309,  310,  311,   -1,  313,  314,  315,  316, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  336,   -1,   -1,  339,   -1, 
-           -1,  342,  343,   -1,  345,   -1,  347,   -1,   -1,   -1, 
-          351,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  359,   -1, 
-           -1,   -1,   -1,  364,  365,  366,  367,  368,  369,   -1, 
-           -1,   -1,  373,   -1,  375,  376,   -1,  378,  379,   -1, 
+           -1,   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  336, 
+           -1,   -1,  339,   -1,   -1,  342,  343,   -1,  345,   -1, 
+          347,   -1,   -1,   -1,  351,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  359,   -1,   -1,   -1,   -1,  364,  365,  366, 
+          367,  368,  369,   -1,   -1,   -1,  373,   -1,  375,  376, 
+           -1,  378,  379,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  256,  257,  258,  259,  260,  261,   -1,   -1,  264, 
+          265,  266,   -1,   -1,   -1,  270,   10,  272,  273,  274, 
+          275,  276,  277,  278,   -1,   -1,   -1,   -1,  283,  284, 
+          285,  286,  287,  288,  289,   -1,   -1,  292,   -1,   -1, 
+           -1,   -1,   -1,  298,  299,   -1,  301,  302,  303,  304, 
+           -1,  306,  307,  308,  309,  310,  311,   -1,  313,  314, 
+          315,  316,   -1,   -1,   -1,   59,   -1,    0,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   10,   -1,   -1, 
+           -1,  336,   -1,   -1,  339,   -1,   -1,  342,  343,   -1, 
+          345,   -1,  347,   -1,   -1,   -1,  351,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  359,   -1,   -1,   -1,   -1,  364, 
+          365,  366,  367,  368,  369,   -1,   -1,   -1,  373,   -1, 
+          375,  376,   -1,  378,  379,   -1,   59,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  256,  257, 
+          258,  259,  260,  261,   -1,   -1,   -1,  265,  266,   -1, 
+           -1,   -1,  270,   -1,  272,  273,  274,  275,  276,  277, 
+          278,   -1,   -1,   -1,   -1,  283,  284,  285,  286,  287, 
+          288,  289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1, 
+          298,  299,   -1,  301,  302,  303,  304,   -1,  306,  307, 
+          308,  309,  310,  311,   -1,  313,  314,  315,  316,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  256,  257,  258,  259,  260,  261,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  336,   -1, 
+           -1,  339,   -1,   -1,  342,  343,   -1,  345,   -1,  347, 
+           -1,   -1,   -1,  351,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  359,   -1,    0,  362,   -1,  364,  365,  366,  367, 
+          368,  369,   -1,   10,   -1,  373,   -1,  375,  376,   -1, 
+          378,  379,   -1,  257,  258,  259,   -1,  261,   -1,   -1, 
            -1,  265,  266,   -1,   -1,   -1,  270,   -1,  272,  273, 
-          274,  275,  276,  277,  278,   -1,   -1,    0,   -1,  283, 
-          284,  285,  286,  287,  288,  289,   -1,   10,  292,   -1, 
-           -1,   -1,   -1,   -1,  298,  299,   -1,  301,  302,  303, 
+          274,  275,  276,  277,  278,   -1,   -1,   44,   -1,  283, 
+          284,  285,  286,  287,  288,  289,   -1,   -1,  292,   -1, 
+           -1,   -1,   59,   -1,   61,  299,   63,   -1,  302,  303, 
           304,   -1,  306,  307,  308,  309,  310,  311,   -1,  313, 
-          314,  315,  316,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   44,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  336,   -1,   -1,  339,   59,   -1,  342,  343, 
-           -1,  345,   -1,  347,   -1,   -1,   -1,  351,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  359,   -1,    0,  362,   -1, 
-          364,  365,  366,  367,  368,  369,   -1,   10,   -1,  373, 
-           -1,  375,  376,   -1,  378,  379,   -1,  257,  258,  259, 
-           -1,  261,   -1,   -1,   -1,  265,  266,   -1,   -1,   -1, 
-          270,   -1,  272,  273,  274,  275,  276,  277,  278,   -1, 
-           -1,   44,   -1,  283,  284,  285,  286,  287,  288,  289, 
-           -1,   -1,  292,   -1,   -1,   -1,   59,   -1,   61,  299, 
-           63,   -1,  302,  303,  304,   -1,  306,  307,  308,  309, 
-          310,  311,   -1,  313,  314,  315,  316,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   91,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  336,   -1,    0,  339, 
-           -1,   -1,  342,  343,   -1,  345,   -1,   -1,   10,   -1, 
-           -1,  351,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  359, 
-           -1,   -1,   -1,   -1,  364,  365,  366,  367,  368,  369, 
-           -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378,  379, 
-           -1,   -1,   44,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   59,   -1,   -1, 
-           -1,   63,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  262, 
-          263,  264,   -1,   -1,   -1,  268,  269,   -1,  271,   91, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,    0,   -1,   -1,  290,  291,   -1, 
-          293,  294,  295,  296,   10,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          314,  315,  316,   -1,   -1,   -1,   -1,   -1,   -1,  262, 
+          263,  264,   -1,   -1,   91,  268,  269,   -1,  271,   -1, 
+           -1,   -1,  336,   -1,    0,  339,   -1,   -1,  342,  343, 
+           -1,  345,   -1,   -1,   10,   -1,   -1,  351,   -1,   -1, 
+          293,  294,  295,  296,  297,  359,   -1,   -1,   -1,   -1, 
+          364,  365,  366,  367,  368,  369,   -1,   -1,   -1,  373, 
+           -1,  375,  376,   -1,  378,  379,   -1,   -1,   44,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  341,  262, 
-          263,  264,   -1,   59,   -1,  268,  269,   63,  271,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  280,   -1,  362, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1, 
-          293,  294,  295,  296,  297,   91,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   59,   -1,   -1,   -1,   63,  341,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  317,  318,  319,  320,  321,  322, 
-          323,  324,  325,  326,  327,  328,  329,  330,   -1,   -1, 
-          333,  334,  335,   -1,   -1,   -1,   -1,   -1,  341,   -1, 
-           -1,   -1,   -1,   -1,   -1,  348,    0,  350,   -1,  352, 
-          353,  354,  355,  356,  357,  358,   10,  360,  361,  362, 
-          262,  263,  264,   -1,   -1,  267,  268,  269,   -1,  271, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291, 
-           44,  293,  294,  295,  296,  297,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   59,   -1,   -1,   -1,   63, 
-           -1,   -1,   -1,   -1,   -1,  317,  318,  319,  320,  321, 
-          322,  323,  324,  325,  326,  327,  328,  329,  330,   -1, 
-           -1,  333,  334,  335,   -1,   -1,   -1,   91,   -1,  341, 
-           -1,   -1,   -1,   -1,   -1,   -1,  348,   -1,  350,    0, 
-          352,  353,  354,  355,  356,  357,  358,   -1,  360,   10, 
-          362,   -1,   -1,   -1,   -1,   -1,  262,  263,  264,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  362, 
+           -1,   -1,   -1,   -1,   -1,   91,  257,  258,  259,   -1, 
+          261,   -1,   -1,   -1,  265,  266,   -1,   -1,   -1,  270, 
+            0,  272,  273,  274,  275,  276,  277,  278,   -1,   -1, 
+           10,   -1,  283,  284,  285,  286,  287,  288,  289,   -1, 
+           -1,  292,   -1,   -1,   -1,   -1,   -1,   -1,  299,   -1, 
+           -1,  302,  303,  304,   -1,  306,  307,  308,  309,  310, 
+          311,   -1,  313,   -1,   44,  316,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  262,  263,  264,   -1,   59, 
+           -1,  268,  269,   63,  271,  336,   -1,   -1,  339,   -1, 
+           -1,  342,  343,  280,  345,   -1,  347,   -1,   -1,   -1, 
+           -1,   -1,   -1,  290,  291,   -1,  293,  294,  295,  296, 
+          297,   91,   -1,  364,  365,  366,  367,  368,  369,   -1, 
+           -1,   -1,  373,   -1,  375,  376,   -1,  378,  379,   -1, 
+          317,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
+          327,  328,  329,  330,   -1,   -1,  333,  334,  335,   -1, 
+           -1,   -1,   -1,   -1,  341,   -1,   -1,   -1,   -1,   -1, 
+           -1,  348,    0,  350,   -1,  352,  353,  354,  355,  356, 
+          357,  358,   10,  360,  361,  362,  262,  263,  264,   -1, 
            -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,  281,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   44,  290,  291,   -1,  293,  294,  295, 
-          296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   59,   -1, 
-           61,   -1,   63,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  290,  291,   44,  293,  294,  295, 
+          296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   59,   -1,   -1,   -1,   63,   -1,   -1,   -1,   -1, 
            -1,  317,  318,  319,  320,  321,  322,  323,  324,  325, 
           326,  327,  328,  329,  330,   -1,   -1,  333,  334,  335, 
-           91,   -1,   -1,   -1,   -1,  341,   -1,   -1,   -1,   -1, 
-           -1,   -1,  348,   -1,  350,   -1,  352,  353,  354,  355, 
-          356,  357,  358,    0,  360,   -1,  362,   -1,   -1,   -1, 
-           -1,   -1,   -1,   10,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   91,   -1,  341,   -1,   -1,   -1,   -1, 
+           -1,   -1,  348,   -1,  350,    0,  352,  353,  354,  355, 
+          356,  357,  358,   -1,  360,   10,  362,   -1,   -1,   -1, 
+           -1,   -1,  262,  263,  264,   -1,   -1,  267,  268,  269, 
+           -1,  271,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,  281,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   44, 
+          290,  291,   -1,  293,  294,  295,  296,  297,   -1,   -1, 
+           -1,   -1,   -1,   -1,   59,   -1,   61,   -1,   63,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  317,  318,  319, 
+          320,  321,  322,  323,  324,  325,  326,  327,  328,  329, 
+          330,   -1,   -1,  333,  334,  335,   91,   -1,   -1,   -1, 
+           -1,  341,   -1,   -1,   -1,   -1,   -1,   -1,  348,   -1, 
+          350,   -1,  352,  353,  354,  355,  356,  357,  358,    0, 
+          360,   -1,  362,   -1,   -1,   -1,   -1,   -1,   -1,   10, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   44,  262,  263, 
-          264,   -1,   -1,  267,  268,  269,   -1,  271,   -1,   -1, 
-           -1,   -1,   59,   -1,   61,   -1,   63,  281,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1,  293, 
-          294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   91,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  317,  318,  319,  320,  321,  322,  323, 
-          324,  325,  326,  327,  328,  329,  330,   -1,   -1,  333, 
-          334,  335,   -1,   -1,   -1,   -1,   -1,  341,   -1,   -1, 
-           -1,   -1,   -1,   -1,  348,   -1,  350,    0,  352,  353, 
-          354,  355,  356,  357,  358,   -1,  360,   10,  362,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   44,  262,  263,  264,   -1,   -1,  267, 
+          268,  269,   -1,  271,   -1,   -1,   -1,   -1,   59,   -1, 
+           61,   -1,   63,  281,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  290,  291,   -1,  293,  294,  295,  296,  297, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           91,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  317, 
+          318,  319,  320,  321,  322,  323,  324,  325,  326,  327, 
+          328,  329,  330,   -1,   -1,  333,  334,  335,   -1,   -1, 
+           -1,   -1,   -1,  341,   -1,   -1,   -1,   -1,   -1,   -1, 
+          348,   -1,  350,    0,  352,  353,  354,  355,  356,  357, 
+          358,   -1,  360,   10,  362,   -1,   -1,  262,  263,  264, 
+           -1,   -1,   -1,  268,  269,   -1,  271,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  290,  291,   44,  293,  294, 
+          295,  296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   59,   -1,   61,   -1,   63,   -1,   -1,   -1, 
+           -1,   -1,  317,  318,  319,  320,  321,  322,  323,  324, 
+          325,  326,  327,  328,  329,  330,   -1,   -1,  333,  334, 
+          335,   -1,  337,   -1,   91,   -1,  341,   -1,   -1,   -1, 
+           -1,   -1,   -1,  348,   -1,  350,   -1,  352,  353,  354, 
+          355,  356,  357,  358,    0,  360,   -1,  362,   -1,   -1, 
+           -1,   -1,   -1,   -1,   10,   -1,   -1,   -1,   -1,   -1, 
            -1,  262,  263,  264,   -1,   -1,   -1,  268,  269,   -1, 
           271,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  290, 
-          291,   44,  293,  294,  295,  296,  297,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   59,   -1,   61,   -1, 
-           63,   -1,   -1,   -1,   -1,   -1,  317,  318,  319,  320, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,    0,   -1,  290, 
+          291,   -1,  293,  294,  295,  296,  297,   10,   -1,   -1, 
+           -1,   -1,   -1,   59,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  317,  318,  319,  320, 
           321,  322,  323,  324,  325,  326,  327,  328,  329,  330, 
-           -1,   -1,  333,  334,  335,   -1,  337,   -1,   91,   -1, 
-          341,   -1,   -1,   -1,   -1,   -1,   -1,  348,   -1,  350, 
-           -1,  352,  353,  354,  355,  356,  357,  358,    0,  360, 
-           -1,  362,   -1,   -1,   -1,   -1,   -1,   -1,   10,   -1, 
-           -1,   -1,   -1,   -1,   -1,  262,  263,  264,   -1,   -1, 
-           -1,  268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  333,  334,  335,   -1,   -1,   -1,   -1,   -1, 
+          341,   -1,   -1,   -1,   -1,   -1,   59,  348,    0,  350, 
+           -1,  352,  353,  354,  355,  356,  357,  358,   10,  360, 
+           -1,  362,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,    0,   44,  290,  291,   -1,  293,  294,  295,  296, 
-          297,   10,   -1,   -1,   -1,   -1,   -1,   59,   -1,   -1, 
+           -1,   -1,   -1,   -1,    0,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   10,  262,  263,  264,   -1,   -1, 
+           -1,  268,  269,   -1,  271,   -1,   -1,   59,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,  290,  291,   -1,  293,  294,  295,  296, 
+          297,   -1,   -1,   -1,   -1,   -1,   -1,    0,   -1,   -1, 
+           -1,   -1,   -1,   59,   -1,   -1,   -1,   10,   -1,   -1, 
           317,  318,  319,  320,  321,  322,  323,  324,  325,  326, 
-          327,  328,  329,  330,   -1,   44,  333,  334,  335,   -1, 
+          327,  328,  329,  330,   -1,   -1,  333,  334,  335,   -1, 
            -1,   -1,   -1,   -1,  341,   -1,   -1,   -1,   -1,   -1, 
-           59,  348,    0,  350,   -1,  352,  353,  354,  355,  356, 
-          357,  358,   10,  360,   -1,  362,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,    0,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   10,  262, 
-          263,  264,   -1,   -1,   -1,  268,  269,   -1,  271,   -1, 
-           -1,   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1, 
-          293,  294,  295,  296,  297,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   59,   -1,   -1, 
-           -1,   -1,   -1,   -1,  317,  318,  319,  320,  321,  322, 
-          323,  324,  325,  326,  327,  328,  329,  330,   -1,   -1, 
-          333,  334,  335,    0,   -1,   -1,   -1,   -1,  341,   -1, 
-           -1,   -1,   -1,   10,   -1,  348,   -1,  350,   -1,  352, 
-          353,  354,  355,  356,  357,  358,   -1,  360,   -1,  362, 
+           -1,  348,   -1,  350,   -1,  352,  353,  354,  355,  356, 
+          357,  358,   -1,  360,    0,  362,   59,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   10,   -1,  262,  263,  264,   -1, 
+           -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  290,  291,   -1,  293,  294,  295, 
+          296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  262, 
+          263,  264,   -1,   59,  267,  268,  269,   -1,  271,   -1, 
+           -1,   -1,   -1,   -1,   -1,    0,   -1,   -1,  281,  282, 
+           -1,   -1,   -1,   -1,   -1,   10,   -1,  290,  291,   -1, 
+          293,  294,  295,  296,  297,  341,   -1,   -1,  344,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  362,   -1,   -1,   44, 
           262,  263,  264,   -1,   -1,  267,  268,  269,   -1,  271, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281, 
-          282,   -1,   59,   -1,   -1,   -1,   -1,    0,  290,  291, 
-           -1,  293,  294,  295,  296,  297,   -1,   10,   -1,   -1, 
-           -1,   -1,   -1,  262,  263,  264,   -1,   -1,  267,  268, 
-          269,   -1,  271,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  290,  291,   -1,  293,  294,  295,  296,  297,  341, 
-           -1,   -1,  344,   -1,  346,   -1,   59,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          362,   -1,   -1,   -1,  262,  263,  264,   -1,   -1,  267, 
-          268,  269,   -1,  271,   44,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,  341,  281,  282,  344,   -1,  346,   -1,   -1, 
-           -1,   -1,  290,  291,   -1,  293,  294,  295,  296,  297, 
-          262,  263,  264,  362,   -1,  267,  268,  269,   -1,  271, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281, 
-          282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291, 
-           -1,  293,  294,  295,  296,  297,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  341,   -1,   -1,  344,   -1,  346,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  362,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  341, 
-           -1,   -1,  344,   -1,   -1,  262,  263,  264,   -1,   -1, 
-          267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1,   -1, 
-          362,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,   -1, 
-           -1,   -1,   59,  290,  291,   -1,  293,  294,  295,  296, 
-          297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  262, 
-          263,  264,   -1,   -1,  267,  268,  269,   -1,  271,   -1, 
-           -1,   -1,   -1,   -1,  341,   -1,   -1,  344,  281,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291,   -1, 
-          293,  294,  295,  296,  297,  362,   -1,  257,  258,  259, 
-           -1,  261,   -1,   -1,   -1,  265,  266,   -1,   -1,   -1, 
-          270,   -1,  272,  273,  274,  275,  276,  277,  278,   -1, 
-           -1,   -1,   -1,  283,  284,  285,  286,  287,  288,  289, 
-           -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,  341,  299, 
-           -1,   -1,  302,  303,  304,   59,  306,  307,  308,  309, 
-          310,  311,   -1,  313,   -1,   -1,  316,   -1,   -1,  362, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  336,   -1,   -1,  339, 
-           -1,   -1,  342,  343,   -1,  345,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   59,   -1,   -1,   -1,  341,  281, 
+          282,  344,   -1,   -1,   -1,   -1,   -1,   -1,  290,  291, 
+           -1,  293,  294,  295,  296,  297,  262,  263,  264,  362, 
+           -1,  267,  268,  269,   -1,  271,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,  281,   -1,   -1,   59,   -1, 
+           -1,   -1,   -1,   -1,  290,  291,   -1,  293,  294,  295, 
+          296,  297,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  341, 
+           -1,   -1,  344,   -1,   -1,   -1,   -1,   -1,   -1,  262, 
+          263,  264,   -1,   -1,  267,  268, 
       };
    }
 
    private static final short[] yyCheck4() {
       return new short[] {
 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  364,  365, 
-          366,  367,  368,  369,   -1,   -1,   -1,  373,   -1,  375, 
-          376,   -1,  378,  379,  257,  258,  259,  260,  261,   -1, 
+          269,   -1,  271,   -1,  362,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  281,   -1,   -1,   -1,   -1,   -1,   -1,  341, 
+           -1,  290,  291,   -1,  293,  294,  295,  296,  297,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          362,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          262,  263,  264,   -1,   -1,  267,  268,  269,   -1,  271, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281, 
+           -1,   -1,  341,   -1,   -1,   -1,   -1,   -1,  290,  291, 
+           -1,  293,  294,  295,  296,  297,   59,   -1,   -1,   -1, 
+          306,  307,   -1,  362,  310,   -1,   -1,   -1,  314,  315, 
+           -1,  317,  318,  319,  320,  321,  322,  323,   -1,   -1, 
+          326,  327,   -1,   -1,   -1,  331,  332,  333,  334,   -1, 
+           -1,  262,  263,  264,  340,   -1,  267,  268,  269,  341, 
+          271,  347,  348,   -1,  350,  351,  352,  353,  354,  355, 
+          356,  357,  358,  359,  360,   -1,   -1,  363,   -1,   -1, 
+          362,   -1,  293,  294,  295,  296,  297,   -1,   -1,   -1, 
+          257,  258,  259,  260,  261,   -1,   -1,   -1,  265,  266, 
+           -1,   -1,   -1,  270,   -1,  272,  273,  274,  275,  276, 
+          277,  278,   -1,   -1,   -1,   -1,  283,  284,  285,  286, 
+          287,  288,  289,   -1,   -1,  292,   -1,  338,   -1,   -1, 
+          341,  298,  299,  300,  301,  302,  303,  304,   -1,  306, 
+          307,  308,  309,  310,  311,   -1,  313,  314,  315,  316, 
+           -1,  362,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  336, 
+           -1,   -1,  339,   -1,   -1,  342,  343,   -1,  345,   -1, 
+          347,   -1,   -1,   -1,  351,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,  359,   -1,   -1,   -1,   -1,  364,  365,  366, 
+          367,  368,  369,   -1,   -1,   -1,  373,   -1,  375,  376, 
+           -1,  378,  379,   -1,  257,  258,  259,  260,  261,   -1, 
            -1,   -1,  265,  266,   -1,   -1,   -1,  270,   -1,  272, 
           273,  274,  275,  276,  277,  278,   -1,   -1,   -1,   -1, 
           283,  284,  285,  286,  287,  288,  289,   -1,   -1,  292, 
-           -1,   -1,   -1,   -1,   -1,  298,  299,  300,  301,  302, 
+           -1,   -1,   -1,   -1,   -1,  298,  299,   -1,  301,  302, 
           303,  304,   -1,  306,  307,  308,  309,  310,  311,   -1, 
           313,  314,  315,  316,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,  336,   -1,   -1,  339,   -1,   -1,  342, 
           343,   -1,  345,   -1,  347,   -1,   -1,   -1,  351,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,  359,   -1,   -1,   -1, 
            -1,  364,  365,  366,  367,  368,  369,   -1,   -1,   -1, 
-          373,   -1,  375,  376,   -1,  378,  379,  257,  258,  259, 
-          260,  261,   -1,   -1,   -1,  265,  266,   -1,   -1,   -1, 
-          270,   -1,  272,  273,  274,  275,  276,  277,  278,   -1, 
-           -1,   -1,   -1,  283,  284,  285,  286,  287,  288,  289, 
-           -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,  298,  299, 
-           -1,  301,  302,  303,  304,   -1,  306,  307,  308,  309, 
-          310,  311,   -1,  313,  314,  315,  316,   -1,   -1,   -1, 
+          373,   -1,  375,  376,   -1,  378,  379,  256,  257,  258, 
+          259,  260,  261,   -1,   -1,   -1,  265,  266,   -1,   -1, 
+           -1,  270,   -1,  272,  273,  274,  275,  276,  277,  278, 
+           -1,   -1,   -1,   -1,  283,  284,  285,  286,  287,  288, 
+          289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,  298, 
+          299,  300,  301,  302,  303,  304,   -1,  306,  307,  308, 
+          309,  310,  311,   -1,  313,  314,  315,  316,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  336,   -1,   -1,  339, 
-           -1,   -1,  342,  343,   -1,  345,   -1,  347,   -1,   -1, 
-           -1,  351,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  359, 
-           -1,   -1,   -1,   -1,  364,  365,  366,  367,  368,  369, 
-           -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378,  379, 
-          256,  257,  258,  259,  260,  261,   -1,   -1,   -1,  265, 
-          266,   -1,   -1,   -1,  270,   -1,  272,  273,  274,  275, 
-          276,  277,  278,   -1,   -1,   -1,   -1,  283,  284,  285, 
-          286,  287,  288,  289,   -1,   -1,  292,   -1,   -1,   -1, 
-           -1,   -1,  298,  299,  300,  301,  302,  303,  304,   -1, 
-          306,  307,  308,  309,  310,  311,   -1,  313,  314,  315, 
-          316,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  336,   -1,   -1, 
+          339,   -1,   -1,  342,  343,   -1,  345,   -1,  347,   -1, 
+           -1,   -1,  351,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          359,   -1,   -1,   -1,   -1,  364,  365,  366,  367,  368, 
+          369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378, 
+          379,  256,  257,  258,  259,  260,  261,   -1,   -1,   -1, 
+          265,  266,   -1,   -1,   -1,  270,   -1,  272,  273,  274, 
+          275,  276,  277,  278,   -1,   -1,   -1,   -1,  283,  284, 
+          285,  286,  287,  288,  289,   -1,   -1,  292,   -1,   -1, 
+           -1,   -1,   -1,  298,  299,   -1,  301,  302,  303,  304, 
+           -1,  306,  307,  308,  309,  310,  311,   -1,  313,  314, 
+          315,  316,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          336,   -1,   -1,  339,   -1,   -1,  342,  343,   -1,  345, 
-           -1,  347,   -1,   -1,   -1,  351,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  359,   -1,   -1,   -1,   -1,  364,  365, 
-          366,  367,  368,  369,   -1,   -1,   -1,  373,   -1,  375, 
-          376,   -1,  378,  379,  256,  257,  258,  259,  260,  261, 
-           -1,   -1,   -1,  265,  266,   -1,   -1,   -1,  270,   -1, 
-          272,  273,  274,  275,  276,  277,  278,   -1,   -1,   -1, 
-           -1,  283,  284,  285,  286,  287,  288,  289,   -1,   -1, 
-          292,   -1,   -1,   -1,   -1,   -1,  298,  299,   -1,  301, 
-          302,  303,  304,   -1,  306,  307,  308,  309,  310,  311, 
-           -1,  313,  314,  315,  316,   -1,   -1,   -1,   -1,   -1, 
+           -1,  336,   -1,   -1,  339,   -1,   -1,  342,  343,   -1, 
+          345,   -1,  347,   -1,   -1,   -1,  351,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  359,   -1,   -1,   -1,   -1,  364, 
+          365,  366,  367,  368,  369,   -1,   -1,   -1,  373,   -1, 
+          375,  376,   -1,  378,  379,  256,  257,  258,  259,  260, 
+          261,   -1,   -1,   -1,  265,  266,   -1,   -1,   -1,  270, 
+           -1,  272,  273,  274,  275,  276,  277,  278,   -1,   -1, 
+           -1,   -1,  283,  284,  285,  286,  287,  288,  289,   -1, 
+           -1,  292,   -1,   -1,   -1,   -1,   -1,  298,  299,   -1, 
+          301,  302,  303,  304,   -1,  306,  307,  308,  309,  310, 
+          311,   -1,  313,  314,  315,  316,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  336,   -1,   -1,  339,   -1,   -1, 
-          342,  343,   -1,  345,   -1,  347,   -1,   -1,   -1,  351, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,  359,   -1,   -1, 
-           -1,   -1,  364,  365,  366,  367,  368,  369,   -1,   -1, 
-           -1,  373,   -1,  375,  376,   -1,  378,  379,  256,  257, 
+           -1,   -1,   -1,   -1,   -1,  336,   -1,   -1,  339,   -1, 
+           -1,  342,  343,   -1,  345,   -1,  347,   -1,   -1,   -1, 
+          351,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  359,   -1, 
+           -1,   -1,   -1,  364,  365,  366,  367,  368,  369,   -1, 
+           -1,   -1,  373,   -1,  375,  376,   -1,  378,  379,  257, 
           258,  259,  260,  261,   -1,   -1,   -1,  265,  266,   -1, 
            -1,   -1,  270,   -1,  272,  273,  274,  275,  276,  277, 
           278,   -1,   -1,   -1,   -1,  283,  284,  285,  286,  287, 
           288,  289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1, 
-          298,  299,   -1,  301,  302,  303,  304,   -1,  306,  307, 
+          298,  299,  300,  301,  302,  303,  304,   -1,  306,  307, 
           308,  309,  310,  311,   -1,  313,  314,  315,  316,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  336,   -1, 
            -1,  339,   -1,   -1,  342,  343,   -1,  345,   -1,  347, 
            -1,   -1,   -1,  351,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,  359,   -1,   -1,   -1,   -1,  364,  365,  366,  367, 
           368,  369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1, 
           378,  379,  257,  258,  259,  260,  261,   -1,   -1,   -1, 
           265,  266,   -1,   -1,   -1,  270,   -1,  272,  273,  274, 
           275,  276,  277,  278,   -1,   -1,   -1,   -1,  283,  284, 
           285,  286,  287,  288,  289,   -1,   -1,  292,   -1,   -1, 
-           -1,   -1,   -1,  298,  299,  300,  301,  302,  303,  304, 
+           -1,   -1,   -1,  298,  299,   -1,  301,  302,  303,  304, 
            -1,  306,  307,  308,  309,  310,  311,   -1,  313,  314, 
           315,  316,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,  336,   -1,   -1,  339,   -1,   -1,  342,  343,   -1, 
           345,   -1,  347,   -1,   -1,   -1,  351,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,  359,   -1,   -1,   -1,   -1,  364, 
           365,  366,  367,  368,  369,   -1,   -1,   -1,  373,   -1, 
-          375,  376,   -1,  378,  379,  257,  258,  259,  260,  261, 
+          375,  376,   -1,  378,  379,  257,  258,  259,   -1,  261, 
            -1,   -1,   -1,  265,  266,   -1,   -1,   -1,  270,   -1, 
           272,  273,  274,  275,  276,  277,  278,   -1,   -1,   -1, 
            -1,  283,  284,  285,  286,  287,  288,  289,   -1,   -1, 
-          292,   -1,   -1,   -1,   -1,   -1,  298,  299,   -1,  301, 
+          292,   -1,   -1,   -1,   -1,   -1,   -1,  299,   -1,   -1, 
           302,  303,  304,   -1,  306,  307,  308,  309,  310,  311, 
-           -1,  313,  314,  315,  316,   -1,   -1,   -1,   -1,   -1, 
+          312,  313,  314,  315,  316,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,  336,   -1,   -1,  339,   -1,   -1, 
-          342,  343,   -1,  345,   -1,  347,   -1,   -1,   -1,  351, 
+          342,  343,   -1,  345,   -1,  347,   -1,  349,   -1,  351, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,  359,   -1,   -1, 
            -1,   -1,  364,  365,  366,  367,  368,  369,   -1,   -1, 
            -1,  373,   -1,  375,  376,   -1,  378,  379,  257,  258, 
           259,   -1,  261,   -1,   -1,   -1,  265,  266,   -1,   -1, 
            -1,  270,   -1,  272,  273,  274,  275,  276,  277,  278, 
            -1,   -1,   -1,   -1,  283,  284,  285,  286,  287,  288, 
           289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,   -1, 
           299,   -1,   -1,  302,  303,  304,   -1,  306,  307,  308, 
           309,  310,  311,  312,  313,  314,  315,  316,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,  336,   -1,   -1, 
           339,   -1,   -1,  342,  343,   -1,  345,   -1,  347,   -1, 
           349,   -1,  351,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
           359,   -1,   -1,   -1,   -1,  364,  365,  366,  367,  368, 
           369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378, 
           379,  257,  258,  259,   -1,  261,   -1,   -1,   -1,  265, 
           266,   -1,   -1,   -1,  270,   -1,  272,  273,  274,  275, 
           276,  277,  278,   -1,   -1,   -1,   -1,  283,  284,  285, 
           286,  287,  288,  289,   -1,   -1,  292,   -1,   -1,   -1, 
            -1,   -1,   -1,  299,   -1,   -1,  302,  303,  304,   -1, 
           306,  307,  308,  309,  310,  311,  312,  313,  314,  315, 
           316,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
           336,   -1,   -1,  339,   -1,   -1,  342,  343,   -1,  345, 
            -1,  347,   -1,  349,   -1,  351,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,  359,   -1,   -1,   -1,   -1,  364,  365, 
           366,  367,  368,  369,   -1,   -1,   -1,  373,   -1,  375, 
           376,   -1,  378,  379,  257,  258,  259,   -1,  261,   -1, 
            -1,   -1,  265,  266,   -1,   -1,   -1,  270,   -1,  272, 
           273,  274,  275,  276,  277,  278,   -1,   -1,   -1,   -1, 
           283,  284,  285,  286,  287,  288,  289,   -1,   -1,  292, 
            -1,   -1,   -1,   -1,   -1,   -1,  299,   -1,   -1,  302, 
           303,  304,   -1,  306,  307,  308,  309,  310,  311,  312, 
           313,  314,  315,  316,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,  336,   -1,   -1,  339,   -1,   -1,  342, 
-          343,   -1,  345,   -1,  347,   -1,  349,   -1,  351,   -1, 
+          343,   -1,  345,   -1,  347,   -1,   -1,   -1,  351,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,  359,   -1,   -1,   -1, 
            -1,  364,  365,  366,  367,  368,  369,   -1,   -1,   -1, 
           373,   -1,  375,  376,   -1,  378,  379,  257,  258,  259, 
            -1,  261,   -1,   -1,   -1,  265,  266,   -1,   -1,   -1, 
           270,   -1,  272,  273,  274,  275,  276,  277,  278,   -1, 
            -1,   -1,   -1,  283,  284,  285,  286,  287,  288,  289, 
            -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,   -1,  299, 
            -1,   -1,  302,  303,  304,   -1,  306,  307,  308,  309, 
           310,  311,  312,  313,  314,  315,  316,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,  336,   -1,   -1,  339, 
-           -1,   -1,  342,  343,   -1,  345,   -1,  347,   -1,   -1, 
+           -1,   -1,  342,  343,   -1,  345,   -1,   -1,   -1,  349, 
            -1,  351,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  359, 
            -1,   -1,   -1,   -1,  364,  365,  366,  367,  368,  369, 
            -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378,  379, 
           257,  258,  259,   -1,  261,   -1,   -1,   -1,  265,  266, 
            -1,   -1,   -1,  270,   -1,  272,  273,  274,  275,  276, 
           277,  278,   -1,   -1,   -1,   -1,  283,  284,  285,  286, 
           287,  288,  289,   -1,   -1,  292,   -1,   -1,   -1,   -1, 
            -1,   -1,  299,   -1,   -1,  302,  303,  304,   -1,  306, 
           307,  308,  309,  310,  311,  312,  313,  314,  315,  316, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  336, 
            -1,   -1,  339,   -1,   -1,  342,  343,   -1,  345,   -1, 
-           -1,   -1,  349,   -1,  351,   -1,   -1,   -1,   -1,   -1, 
+          347,   -1,   -1,   -1,  351,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,  359,   -1,   -1,   -1,   -1,  364,  365,  366, 
           367,  368,  369,   -1,   -1,   -1,  373,   -1,  375,  376, 
            -1,  378,  379,  257,  258,  259,   -1,  261,   -1,   -1, 
            -1,  265,  266,   -1,   -1,   -1,  270,   -1,  272,  273, 
           274,  275,  276,  277,  278,   -1,   -1,   -1,   -1,  283, 
           284,  285,  286,  287,  288,  289,   -1,   -1,  292,   -1, 
            -1,   -1,   -1,   -1,   -1,  299,   -1,   -1,  302,  303, 
           304,   -1,  306,  307,  308,  309,  310,  311,  312,  313, 
           314,  315,  316,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,  336,   -1,   -1,  339,   -1,   -1,  342,  343, 
-           -1,  345,   -1,  347,   -1,   -1,   -1,  351,   -1,   -1, 
+           -1,  345,   -1,   -1,   -1,   -1,   -1,  351,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,  359,   -1,   -1,   -1,   -1, 
           364,  365,  366,  367,  368,  369,   -1,   -1,   -1,  373, 
            -1,  375,  376,   -1,  378,  379,  257,  258,  259,   -1, 
           261,   -1,   -1,   -1,  265,  266,   -1,   -1,   -1,  270, 
            -1,  272,  273,  274,  275,  276,  277,  278,   -1,   -1, 
            -1,   -1,  283,  284,  285,  286,  287,  288,  289,   -1, 
            -1,  292,   -1,   -1,   -1,   -1,   -1,   -1,  299,   -1, 
            -1,  302,  303,  304,   -1,  306,  307,  308,  309,  310, 
-          311,  312,  313,  314,  315,  316,   -1,   -1,   -1,   -1, 
+          311,   -1,  313,  314,  315,  316,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,  336,   -1,   -1,  339,   -1, 
+           -1,   -1,   -1,   -1,   -1,  336,   -1,   -1,  339,  340, 
            -1,  342,  343,   -1,  345,   -1,   -1,   -1,   -1,   -1, 
           351,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  359,   -1, 
            -1,   -1,   -1,  364,  365,  366,  367,  368,  369,   -1, 
            -1,   -1,  373,   -1,  375,  376,   -1,  378,  379,  257, 
           258,  259,   -1,  261,   -1,   -1,   -1,  265,  266,   -1, 
            -1,   -1,  270,   -1,  272,  273,  274,  275,  276,  277, 
           278,   -1,   -1,   -1,   -1,  283,  284,  285,  286,  287, 
           288,  289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1, 
            -1,  299,   -1,   -1,  302,  303,  304,   -1,  306,  307, 
           308,  309,  310,  311,   -1,  313,  314,  315,  316,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  336,   -1, 
-           -1,  339,  340,   -1,  342,  343,   -1,  345,   -1,   -1, 
+           -1,  339,   -1,   -1,  342,  343,   -1,  345,   -1,  347, 
            -1,   -1,   -1,  351,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,  359,   -1,   -1,   -1,   -1,  364,  365,  366,  367, 
           368,  369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1, 
           378,  379,  257,  258,  259,   -1,  261,   -1,   -1,   -1, 
           265,  266,   -1,   -1,   -1,  270,   -1,  272,  273,  274, 
           275,  276,  277,  278,   -1,   -1,   -1,   -1,  283,  284, 
           285,  286,  287,  288,  289,   -1,   -1,  292,   -1,   -1, 
            -1,   -1,   -1,   -1,  299,   -1,   -1,  302,  303,  304, 
            -1,  306,  307,  308,  309,  310,  311,   -1,  313,  314, 
           315,  316,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,  336,   -1,   -1,  339,   -1,   -1,  342,  343,   -1, 
           345,   -1,  347,   -1,   -1,   -1,  351,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,  359,   -1,   -1,   -1,   -1,  364, 
           365,  366,  367,  368,  369,   -1,   -1,   -1,  373,   -1, 
           375,  376,   -1,  378,  379,  257,  258,  259,   -1,  261, 
            -1,   -1,   -1,  265,  266,   -1,   -1,   -1,  270,   -1, 
           272,  273,  274,  275,  276,  277,  278,   -1,   -1,   -1, 
            -1,  283,  284,  285,  286,  287,  288,  289,   -1,   -1, 
           292,   -1,   -1,   -1,   -1,   -1,   -1,  299,   -1,   -1, 
           302,  303,  304,   -1,  306,  307,  308,  309,  310,  311, 
            -1,  313,  314,  315,  316,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,  336,   -1,   -1,  339,   -1,   -1, 
           342,  343,   -1,  345,   -1,  347,   -1,   -1,   -1,  351, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,  359,   -1,   -1, 
            -1,   -1,  364,  365,  366,  367,  368,  369,   -1,   -1, 
            -1,  373,   -1,  375,  376,   -1,  378,  379,  257,  258, 
           259,   -1,  261,   -1,   -1,   -1,  265,  266,   -1,   -1, 
            -1,  270,   -1,  272,  273,  274,  275,  276,  277,  278, 
            -1,   -1,   -1,   -1,  283,  284,  285,  286,  287,  288, 
           289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,   -1, 
           299,   -1,   -1,  302,  303,  304,   -1,  306,  307,  308, 
           309,  310,  311,   -1,  313,  314,  315,  316,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,  336,   -1,   -1, 
           339,   -1,   -1,  342,  343,   -1,  345,   -1,  347,   -1, 
            -1,   -1,  351,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
           359,   -1,   -1,   -1,   -1,  364,  365,  366,  367,  368, 
           369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378, 
           379,  257,  258,  259,   -1,  261,   -1,   -1,   -1,  265, 
           266,   -1,   -1,   -1,  270,   -1,  272,  273,  274,  275, 
           276,  277,  278,   -1,   -1,   -1,   -1,  283,  284,  285, 
           286,  287,  288,  289,   -1,   -1,  292,   -1,   -1,   -1, 
            -1,   -1,   -1,  299,   -1,   -1,  302,  303,  304,   -1, 
           306,  307,  308,  309,  310,  311,   -1,  313,  314,  315, 
           316,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
           336,   -1,   -1,  339,   -1,   -1,  342,  343,   -1,  345, 
            -1,  347,   -1,   -1,   -1,  351,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,  359,   -1,   -1,   -1,   -1,  364,  365, 
           366,  367,  368,  369,   -1,   -1,   -1,  373,   -1,  375, 
           376,   -1,  378,  379,  257,  258,  259,   -1,  261,   -1, 
            -1,   -1,  265,  266,   -1,   -1,   -1,  270,   -1,  272, 
           273,  274,  275,  276,  277,  278,   -1,   -1,   -1,   -1, 
           283,  284,  285,  286,  287,  288,  289,   -1,   -1,  292, 
            -1,   -1,   -1,   -1,   -1,   -1,  299,   -1,   -1,  302, 
           303,  304,   -1,  306,  307,  308,  309,  310,  311,   -1, 
           313,  314,  315,  316,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  336,   -1,   -1,  339,   -1,   -1,  342, 
-          343,   -1,  345,   -1,  347,   -1,   -1,   -1,  351,   -1, 
+           -1,   -1,   -1,  336,   -1,   -1,  339,  340,   -1,  342, 
+          343,   -1,  345,   -1,   -1,   -1,   -1,   -1,  351,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,  359,   -1,   -1,   -1, 
            -1,  364,  365,  366,  367,  368,  369,   -1,   -1,   -1, 
           373,   -1,  375,  376,   -1,  378,  379,  257,  258,  259, 
            -1,  261,   -1,   -1,   -1,  265,  266,   -1,   -1,   -1, 
           270,   -1,  272,  273,  274,  275,  276,  277,  278,   -1, 
            -1,   -1,   -1,  283,  284,  285,  286,  287,  288,  289, 
            -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,   -1,  299, 
            -1,   -1,  302,  303,  304,   -1,  306,  307,  308,  309, 
           310,  311,   -1,  313,  314,  315,  316,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,  336,   -1,   -1,  339, 
-          340,   -1,  342,  343,   -1,  345,   -1,   -1,   -1,   -1, 
+           -1,   -1,  342,  343,   -1,  345,   -1,   -1,   -1,   -1, 
            -1,  351,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  359, 
            -1,   -1,   -1,   -1,  364,  365,  366,  367,  368,  369, 
            -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378,  379, 
           257,  258,  259,   -1,  261,   -1,   -1,   -1,  265,  266, 
            -1,   -1,   -1,  270,   -1,  272,  273,  274,  275,  276, 
           277,  278,   -1,   -1,   -1,   -1,  283,  284,  285,  286, 
           287,  288,  289,   -1,   -1,  292,   -1,   -1,   -1,   -1, 
            -1,   -1,  299,   -1,   -1,  302,  303,  304,   -1,  306, 
           307,  308,  309,  310,  311,   -1,  313,  314,  315,  316, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  336, 
            -1,   -1,  339,   -1,   -1,  342,  343,   -1,  345,   -1, 
            -1,   -1,   -1,   -1,  351,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,  359,   -1,   -1,   -1,   -1,  364,  365,  366, 
           367,  368,  369,   -1,   -1,   -1,  373,   -1,  375,  376, 
            -1,  378,  379,  257,  258,  259,   -1,  261,   -1,   -1, 
            -1,  265,  266,   -1,   -1,   -1,  270,   -1,  272,  273, 
           274,  275,  276,  277,  278,   -1,   -1,   -1,   -1,  283, 
           284,  285,  286,  287,  288,  289,   -1,   -1,  292,   -1, 
            -1,   -1,   -1,   -1,   -1,  299,   -1,   -1,  302,  303, 
           304,   -1,  306,  307,  308,  309,  310,  311,   -1,  313, 
           314,  315,  316,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,  336,   -1,   -1,  339,   -1,   -1,  342,  343, 
            -1,  345,   -1,   -1,   -1,   -1,   -1,  351,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,  359,   -1,   -1,   -1,   -1, 
           364,  365,  366,  367,  368,  369,   -1,   -1,   -1,  373, 
            -1,  375,  376,   -1,  378,  379,  257,  258,  259,   -1, 
           261,   -1,   -1,   -1,  265,  266,   -1,   -1,   -1,  270, 
            -1,  272,  273,  274,  275,  276,  277,  278,   -1,   -1, 
            -1,   -1,  283,  284,  285,  286,  287,  288,  289,   -1, 
            -1,  292,   -1,   -1,   -1,   -1,   -1,   -1,  299,   -1, 
            -1,  302,  303,  304,   -1,  306,  307,  308,  309,  310, 
           311,   -1,  313,  314,  315,  316,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,  336,   -1,   -1,  339,   -1, 
            -1,  342,  343,   -1,  345,   -1,   -1,   -1,   -1,   -1, 
           351,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  359,   -1, 
            -1,   -1,   -1,  364,  365,  366,  367,  368,  369,   -1, 
            -1,   -1,  373,   -1,  375,  376,   -1,  378,  379,  257, 
           258,  259,   -1,  261,   -1,   -1,   -1,  265,  266,   -1, 
            -1,   -1,  270,   -1,  272,  273,  274,  275,  276,  277, 
           278,   -1,   -1,   -1,   -1,  283,  284,  285,  286,  287, 
           288,  289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1, 
            -1,  299,   -1,   -1,  302,  303,  304,   -1,  306,  307, 
-          308,  309,  310,  311,   -1,  313,  314,  315,  316,   -1, 
+          308,  309,  310,  311,   -1,  313,   -1,   -1,  316,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  336,   -1, 
-           -1,  339,   -1,   -1,  342,  343,   -1,  345,   -1,   -1, 
-           -1,   -1,   -1,  351,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  359,   -1,   -1,   -1,   -1,  364,  365,  366,  367, 
+           -1,  339,   -1,   -1,  342,  343,   -1,  345,   -1,  347, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,  364,  365,  366,  367, 
           368,  369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1, 
           378,  379,  257,  258,  259,   -1,  261,   -1,   -1,   -1, 
           265,  266,   -1,   -1,   -1,  270,   -1,  272,  273,  274, 
           275,  276,  277,  278,   -1,   -1,   -1,   -1,  283,  284, 
           285,  286,  287,  288,  289,   -1,   -1,  292,   -1,   -1, 
            -1,   -1,   -1,   -1,  299,   -1,   -1,  302,  303,  304, 
            -1,  306,  307,  308,  309,  310,  311,   -1,  313,   -1, 
            -1,  316,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,  336,   -1,   -1,  339,   -1,   -1,  342,  343,   -1, 
-          345,   -1,  347,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+          345,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  364, 
           365,  366,  367,  368,  369,   -1,   -1,   -1,  373,   -1, 
           375,  376,   -1,  378,  379,  257,  258,  259,   -1,  261, 
            -1,   -1,   -1,  265,  266,   -1,   -1,   -1,  270,   -1, 
           272,  273,  274,  275,  276,  277,  278,   -1,   -1,   -1, 
            -1,  283,  284,  285,  286,  287,  288,  289,   -1,   -1, 
           292,   -1,   -1,   -1,   -1,   -1,   -1,  299,   -1,   -1, 
           302,  303,  304,   -1,  306,  307,  308,  309,  310,  311, 
            -1,  313,   -1,   -1,  316,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,  336,   -1,   -1,  339,   -1,   -1, 
-          342,  343,   -1,  345,   -1,  347,   -1,   -1,   -1,   -1, 
+          342,  343,   -1,  345,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,  364,  365,  366,  367,  368,  369,   -1,   -1, 
            -1,  373,   -1,  375,  376,   -1,  378,  379,  257,  258, 
           259,   -1,  261,   -1,   -1,   -1,  265,  266,   -1,   -1, 
            -1,  270,   -1,  272,  273,  274,  275,  276,  277,  278, 
            -1,   -1,   -1,   -1,  283,  284,  285,  286,  287,  288, 
           289,   -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,   -1, 
           299,   -1,   -1,  302,  303,  304,   -1,  306,  307,  308, 
           309,  310,  311,   -1,  313,   -1,   -1,  316,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,  336,   -1,   -1, 
           339,   -1,   -1,  342,  343,   -1,  345,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,  364,  365,  366,  367,  368, 
           369,   -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378, 
           379,  257,  258,  259,   -1,  261,   -1,   -1,   -1,  265, 
           266,   -1,   -1,   -1,  270,   -1,  272,  273,  274,  275, 
           276,  277,  278,   -1,   -1,   -1,   -1,  283,  284,  285, 
           286,  287,  288,  289,   -1,   -1,  292,   -1,   -1,   -1, 
            -1,   -1,   -1,  299,   -1,   -1,  302,  303,  304,   -1, 
           306,  307,  308,  309,  310,  311,   -1,  313,   -1,   -1, 
           316,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
           336,   -1,   -1,  339,   -1,   -1,  342,  343,   -1,  345, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  364,  365, 
           366,  367,  368,  369,   -1,   -1,   -1,  373,   -1,  375, 
-          376,   -1,  378,  379,  257,  258,  259,   -1,  261,   -1, 
-           -1,   -1,  265,  266,   -1,   -1,   -1,  270,   -1,  272, 
-          273,  274,  275,  276,  277,  278,   -1,   -1,   -1,   -1, 
-          283,  284,  285,  286,  287,  288,  289,   -1,   -1,  292, 
-           -1,   -1,   -1,   -1,   -1,   -1,  299,   -1,   -1,  302, 
-          303,  304,   -1,  306,  307,  308,  309,  310,  311,   -1, 
-          313,   -1,   -1,  316,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  336,   -1,   -1,  339,   -1,   -1,  342, 
-          343,   -1,  345,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,  364,  365,  366,  367,  368,  369,   -1,   -1,   -1, 
-          373,   -1,  375,  376,   -1,  378,  379,  257,  258,  259, 
-           -1,  261,   -1,   -1,   -1,  265,  266,   -1,   -1,   -1, 
-          270,   -1,  272,  273,  274,  275,  276,  277,  278,   -1, 
-           -1,   -1,   -1,  283,  284,  285,  286,  287,  288,  289, 
-           -1,   -1,  292,   -1,   -1,   -1,   -1,   -1,   -1,  299, 
-           -1,   -1,  302,  303,  304,   -1,  306,  307,  308,  309, 
-          310,  311,   -1,  313,   -1,   -1,  316,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,  336,   -1,   -1,  339, 
-           -1,   -1,  342,  343,   -1,  345,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,  364,  365,  366,  367,  368,  369, 
-           -1,   -1,   -1,  373,   -1,  375,  376,   -1,  378,  379, 
-          257,  258,  259,  260,  261,  262,  263,  264,   -1,   -1, 
-          267,  268,  269,  270,  271,   -1,   -1,  274,  275,  276, 
-          277,  278,  279,  280,   -1,   -1,  283,  284,  285,  286, 
-          287,  288,  289,  290,  291,  292,  293,  294,  295,  296, 
-          297,  298,  299,  300,  301,  302,  303,  304,   -1,  306, 
-          307,  308,   -1,  310,   -1,   -1,   -1,  314,  315,   -1, 
-          317,  318,  319,  320,  321,  322,  323,   -1,   -1,  326, 
-          327,   -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1, 
+          376,   -1,  378,  379,  257,  258,  259,  260,  261,  262, 
+          263,  264,   -1,   -1,  267,  268,  269,  270,  271,   -1, 
+           -1,  274,  275,  276,  277,  278,  279,  280,   -1,   -1, 
+          283,  284,  285,  286,  287,  288,  289,  290,  291,  292, 
+          293,  294,  295,  296,  297,  298,  299,  300,  301,  302, 
+          303,  304,   -1,  306,  307,  308,   -1,  310,   -1,   -1, 
+           -1,  314,  315,   -1,  317,  318,  319,  320,  321,  322, 
+          323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331,  332, 
+          333,  334,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  347,  348,   -1,  350,  351,  352, 
+          353,  354,  355,  356,  357,  358,  359,  360,   -1,   -1, 
+          363,  364,  257,  258,  259,  260,  261,  262,  263,  264, 
+           -1,   -1,  267,  268,  269,  270,  271,   -1,   -1,  274, 
+          275,  276,  277,  278,  279,  280,   -1,   -1,  283,  284, 
+          285,  286,  287,  288,  289,  290,  291,  292,  293,  294, 
+          295,  296,  297,  298,  299,  300,  301,  302,  303,  304, 
+           -1,  306,  307,   -1,   -1,  310,   -1,   -1,   -1,  314, 
+          315,   -1,  317,  318,  319,  320,  321,  322,  323,   -1, 
+           -1,  326,  327,   -1,   -1,   -1,  331,  332,  333,  334, 
            -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          347,  348,   -1,  350,  351,  352,  353,  354,  355,  356, 
-          357,  358,  359,  360,   -1,   -1,  363,  364,  257,  258, 
-          259,  260,  261,  262,  263,  264,   -1,   -1,  267,  268, 
-          269,  270,  271,   -1,   -1,  274,  275,  276,  277,  278, 
-          279,  280,   -1,   -1,  283,  284,  285,  286,  287,  288, 
-          289,  290,  291,  292,  293,  294,  295,  296,  297,  298, 
-          299,  300,  301,  302,  303,  304,   -1,  306,  307,   -1, 
-           -1,  310,   -1,   -1,   -1,  314,  315,   -1,  317,  318, 
-          319,  320,  321,  322,  323,   -1,   -1,  326,  327,   -1, 
-           -1,   -1,  331,  332,  333,  334,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  347,  348, 
-           -1,  350,  351,  352,  353,  354,  355,  356,  357,  358, 
-          359,  360,   -1,   -1,  363,  364,  257,  258,  259,  260, 
-          261,  262,  263,  264,   -1,   -1,  267,  268,  269,  270, 
-          271,   -1,   -1,  274,  275,  276,  277,  278,  279,  280, 
-           -1,   -1,  283,  284,  285,  286,  287,  288,  289,  290, 
-          291,  292,  293,  294,  295,  296,  297,  298,  299,  300, 
-          301,  302,  303,  304,   -1,  306,  307,  308,  309,  310, 
-          311,   -1,   -1,  314,  315,   -1,  317,  318,  319,  320, 
-          321,  322,  323,   -1,   -1,  326,  327,   -1,   -1,   -1, 
-          331,  332,  333,  334,   -1,   -1,   -1,   -1,   -1,  340, 
-           -1,   -1,   -1,   -1,   -1,   -1,  347,  348,   -1,  350, 
-          351,  352,  353,  354,  355,  356,  357,  358,  359,  360, 
-           -1,   -1,  363,  257,  258,  259,  260,  261,  262,  263, 
-          264,   -1,   -1,  267,  268,  269,  270,  271,   -1,   -1, 
-          274,  275,  276,  277,  278,  279,  280,   -1,   -1,  283, 
-          284,  285,  286,  287,  288,  289,  290,  291,  292,  293, 
-          294,  295,  296,  297,  298,  299,  300,  301,  302,  303, 
-          304,   -1,  306,  307,  308,  309,  310,  311,   -1,   -1, 
-          314,  315,   -1,  317,  318,  319,  320,  321,  322,  323, 
-           -1,   -1,  326,  327,   -1,   -1,   -1,  331,  332,  333, 
-          334,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-           -1,   -1,   -1,  347,  348,   -1,  350,  351,  352,  353, 
-          354,  355,  356,  357,  358,  359,  360,   -1,   -1,  363, 
+           -1,   -1,  347,  348,   -1,  350,  351,  352,  353,  354, 
+          355,  356,  357,  358,  359,  360,   -1,   -1,  363,  364, 
           257,  258,  259,  260,  261,  262,  263,  264,   -1,   -1, 
           267,  268,  269,  270,  271,   -1,   -1,  274,  275,  276, 
           277,  278,  279,  280,   -1,   -1,  283,  284,  285,  286, 
           287,  288,  289,  290,  291,  292,  293,  294,  295,  296, 
           297,  298,  299,  300,  301,  302,  303,  304,   -1,  306, 
-          307,   -1,   -1,  310,   -1,   -1,   -1,  314,  315,   -1, 
-          317,  318,  319,  320,  321,  322,  323,   -1,   -1,  326, 
-          327,   -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          347,  348,   -1,  350,  351,  352,  353,  354,  355,  356, 
-          357,  358,  359,  360,  306,  307,  363,   -1,  310,   -1, 
-           -1,   -1,  314,  315,   -1,  317,  318,  319,  320,  321, 
-          322,  323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331, 
-          332,  333,  334,   -1,   -1,   -1,   -1,   -1,  340,   -1, 
-           -1,   -1,   -1,   -1,   -1,  347,  348,   -1,  350,  351, 
-          352,  353,  354,  355,  356,  357,  358,  359,  360,  306, 
-          307,  363,   -1,  310,   -1,   -1,   -1,  314,  315,   -1, 
-          317,  318,  319,  320,  321,  322,  323,   -1,   -1,  326, 
-          327,   -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1, 
-           -1,   -1,   -1,  340,   -1,   -1,   -1,   -1,   -1,   -1, 
-          347,  348,   -1,  350,  351,  352,  353,  354,  355,  356, 
-          357,  358,  359,  360,  306,  307,  363,   -1,  310,   -1, 
-           -1,   -1,  314,  315,   -1,  317,  318,  319,  320,  321, 
-          322,  323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331, 
-          332,  333,  334,   -1,   -1,   -1,   -1,   -1,  340,   -1, 
-           -1,   -1,   -1,   -1,   -1,  347,  348,   -1,  350,  351, 
-          352,  353,  354,  355,  356,  357,  358,  359,  360,  306, 
-          307,  363,   -1,  310,   -1,   -1,   -1,  314,  315,   -1, 
-          317,  318,  319,  320,  321,  322,  323,   -1,   -1,  326, 
-          327,   -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1, 
-           -1,   -1,   -1,  340,   -1,   -1,   -1,   -1,   -1,   -1, 
-          347,  348,   -1,  350,  351,  352,  353,  354,  355,  356, 
-          357,  358,  359,  360,  306,  307,  363,   -1,  310,   -1, 
-           -1,   -1,  314,  315,   -1,  317,  318,  319,  320,  321, 
-          322,  323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331, 
-          332,  333,  334,   -1,   -1,   -1,   -1,   -1,  340,   -1, 
-           -1,   -1,   -1,   -1,   -1,  347,  348,   -1,  350,  351, 
-          352,  353,  354,  355,  356,  357,  358,  359,  360,  306, 
-          307,  363,   -1,  310,   -1,   -1,   -1,  314,  315,   -1, 
+          307,  308,  309,  310,  311,   -1,   -1,  314,  315,   -1, 
           317,  318,  319,  320,  321,  322,  323,   -1,   -1,  326, 
           327,   -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1, 
            -1,   -1,   -1,  340,   -1,   -1,   -1,   -1,   -1,   -1, 
           347,  348,   -1,  350,  351,  352,  353,  354,  355,  356, 
-          357,  358,  359,  360,  306,  307,  363,   -1,  310,   -1, 
-           -1,   -1,  314,  315,   -1,  317,  318,  319,  320,  321, 
-          322,  323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331, 
-          332,  333,  334,   -1,   -1,   -1,   -1,   -1,  340,   -1, 
-           -1,   -1,   -1,   -1,   -1,  347,  348,   -1,  350,  351, 
-          352,  353,  354,  355,  356,  357,  358,  359,  360,  306, 
-          307,  363,   -1,  310,   -1,   -1,   -1,  314,  315,   -1, 
-          317,  318,  319,  320,  321,  322,  323,   -1,   -1,  326, 
-          327,   -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1, 
-           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
-          347,  348,   -1,  350,  351,  352,  353,  354,  355,  356, 
-          357,  358,  359,  360,   -1,   -1,  363, 
+          357,  358,  359,  360,   -1,   -1,  363,  257,  258,  259, 
+          260,  261,  262,  263,  264,   -1,   -1,  267,  268,  269, 
+          270,  271,   -1,   -1,  274,  275,  276,  277,  278,  279, 
+          280,   -1,   -1,  283,  284,  285,  286,  287,  288,  289, 
+          290,  291,  292,  293,  294,  295,  296,  297,  298,  299, 
+          300,  301,  302,  303,  304,   -1,  306,  307,  308,  309, 
+          310,  311,   -1,   -1,  314,  315,   -1,  317,  318,  319, 
+          320,  321,  322,  323,   -1,   -1,  326,  327,   -1,   -1, 
+           -1,  331,  332,  333,  334,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,  347,  348,   -1, 
+          350,  351,  352,  353,  354,  355,  356,  357,  358,  359, 
+          360,   -1,   -1,  363,  257,  258,  259,  260,  261,  262, 
+          263,  264,   -1,   -1,  267,  268,  269,  270,  271,   -1, 
+           -1,  274,  275,  276,  277,  278,  279,  280,   -1,   -1, 
+          283,  284,  285,  286,  287,  288,  289,  290,  291,  292, 
+          293,  294,  295,  296,  297,  298,  299,  300,  301,  302, 
+          303,  304,   -1,  306,  307,   -1,   -1,  310,   -1,   -1, 
+           -1,  314,  315,   -1,  317,  318,  319,  320,  321,  322, 
+          323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331,  332, 
+          333,  334,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,  347,  348,   -1,  350,  351,  352, 
+          353,  354,  355,  356,  357,  358,  359,  360,  306,  307, 
+          363,   -1,  310,   -1,   -1,   -1,  314,  315,   -1,  317, 
+          318,  319,  320,  321,  322,  323,   -1,   -1,  326,  327, 
+           -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1,   -1, 
+           -1,   -1,  340,   -1,   -1,   -1,   -1,   -1,   -1,  347, 
+          348,   -1,  350,  351,  352,  353,  354,  355,  356,  357, 
+          358,  359,  360,  306,  307,  363,   -1,  310,   -1,   -1, 
+           -1,  314,  315,   -1,  317,  318,  319,  320,  321,  322, 
+          323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331,  332, 
+          333,  334,   -1,   -1,   -1,   -1,   -1,  340,   -1,   -1, 
+           -1,   -1,   -1,   -1,  347,  348,   -1,  350,  351,  352, 
+          353,  354,  355,  356,  357,  358,  359,  360,  306,  307, 
+          363,   -1,  310,   -1,   -1,   -1,  314,  315,   -1,  317, 
+          318,  319,  320,  321,  322,  323,   -1,   -1,  326,  327, 
+           -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1,   -1, 
+           -1,   -1,  340,   -1,   -1,   -1,   -1,   -1,   -1,  347, 
+          348,   -1,  350,  351,  352,  353,  354,  355,  356,  357, 
+          358,  359,  360,  306,  307,  363,   -1,  310,   -1,   -1, 
+           -1,  314,  315,   -1,  317,  318,  319,  320,  321,  322, 
+          323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331,  332, 
+          333,  334,   -1,   -1,   -1,   -1,   -1,  340,   -1,   -1, 
+           -1,   -1,   -1,   -1,  347,  348,   -1,  350,  351,  352, 
+          353,  354,  355,  356,  357,  358,  359,  360,  306,  307, 
+          363,   -1,  310,   -1,   -1,   -1,  314,  315,   -1,  317, 
+          318,  319,  320,  321,  322,  323,   -1,   -1,  326,  327, 
+           -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1,   -1, 
+           -1,   -1,  340,   -1,   -1,   -1,   -1,   -1,   -1,  347, 
+          348,   -1,  350,  351,  352,  353,  354,  355,  356,  357, 
+          358,  359,  360,  306,  307,  363,   -1,  310,   -1,   -1, 
+           -1,  314,  315,   -1,  317,  318,  319,  320,  321,  322, 
+          323,   -1,   -1,  326,  327,   -1,   -1,   -1,  331,  332, 
+          333,  334,   -1,   -1,   -1,   -1,   -1,  340,   -1,   -1, 
+           -1,   -1,   -1,   -1,  347,  348,   -1,  350,  351,  352, 
+          353,  354,  355,  356,  357,  358,  359,  360,  306,  307, 
+          363,   -1,  310,   -1,   -1,   -1,  314,  315,   -1,  317, 
+          318,  319,  320,  321,  322,  323,   -1,   -1,  326,  327, 
+           -1,   -1,   -1,  331,  332,  333,  334,   -1,   -1,   -1, 
+           -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  347, 
+          348,   -1,  350,  351,  352,  353,  354,  355,  356,  357, 
+          358,  359,  360,   -1,   -1,  363, 
       };
    }
 
 }
