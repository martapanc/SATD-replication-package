diff --git a/src/org/jruby/parser/Ruby19Parser.java b/src/org/jruby/parser/Ruby19Parser.java
index 7d31941a57..b1bfbce340 100644
--- a/src/org/jruby/parser/Ruby19Parser.java
+++ b/src/org/jruby/parser/Ruby19Parser.java
@@ -835,2011 +835,2012 @@ public class Ruby19Parser implements RubyParser {
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
 
 static ParserState[] states = new ParserState[542];
 static {
 states[368] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ArgsNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[33] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                     yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                     ((MultipleAsgn19Node)yyVals[-2+yyTop]).setPosition(support.getPosition(((MultipleAsgn19Node)yyVals[-2+yyTop])));
     return yyVal;
   }
 };
 states[234] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
                     if (yyVal != null) ((Node)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[100] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[301] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = Integer.valueOf(support.getInSingle());
                     support.setInSingle(0);
                     support.pushLocalScope();
     return yyVal;
   }
 };
 states[469] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[402] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     /* FIXME: We may be intern'ing more than once.*/
                     yyVal = new SymbolNode(((Token)yyVals[0+yyTop]).getPosition(), ((String) ((Token)yyVals[0+yyTop]).getValue()).intern());
     return yyVal;
   }
 };
 states[335] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((Token)yyVals[-1+yyTop]).getPosition(), null, support.assignable(((Token)yyVals[0+yyTop]), null), null);
     return yyVal;
   }
 };
 states[201] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "%", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[67] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-4+yyTop]).getPosition(), ((ListNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[268] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.warning(ID.GROUPED_EXPRESSION, ((Token)yyVals[-3+yyTop]).getPosition(), "(...) interpreted as grouped expression");
                     yyVal = ((Node)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
 states[503] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     String identifier = (String) ((Token)yyVals[0+yyTop]).getValue();
 
                     if (!support.is_local_id(((Token)yyVals[0+yyTop]))) {
                         support.yyerror("block argument must be local variable");
                     }
                     support.shadowing_lvar(((Token)yyVals[0+yyTop]));
                     yyVal = new BlockArgNode(((Token)yyVals[-1+yyTop]).getPosition(), support.arg_var(((Token)yyVals[0+yyTop])), identifier);
     return yyVal;
   }
 };
 states[369] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[302] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new SClassNode(((Token)yyVals[-7+yyTop]).getPosition(), ((Node)yyVals[-5+yyTop]), support.getCurrentScope(), ((Node)yyVals[-1+yyTop]));
                     support.popCurrentScope();
                     support.setInDef(((Boolean)yyVals[-4+yyTop]).booleanValue());
                     support.setInSingle(((Integer)yyVals[-2+yyTop]).intValue());
     return yyVal;
   }
 };
 states[470] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[336] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((Token)yyVals[-3+yyTop]).getPosition(), null, support.assignable(((Token)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[202] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[68] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), new StarNode(lexer.getPosition()), null);
     return yyVal;
   }
 };
 states[269] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-1+yyTop]) != null) {
                         /* compstmt position includes both parens around it*/
                         ((ISourcePositionHolder) ((Node)yyVals[-1+yyTop])).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
                         yyVal = ((Node)yyVals[-1+yyTop]);
                     } else {
                         yyVal = new NilNode(((Token)yyVals[-2+yyTop]).getPosition());
                     }
     return yyVal;
   }
 };
 states[1] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                   lexer.setState(LexState.EXPR_BEG);
                   support.initTopLocalVariables();
     return yyVal;
   }
 };
 states[504] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((BlockArgNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[370] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[303] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (support.isInDef() || support.isInSingle()) { 
                         support.yyerror("module definition in method body");
                     }
                     support.pushLocalScope();
     return yyVal;
   }
 };
 states[471] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[404] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]) instanceof EvStrNode ? new DStrNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop])) : ((Node)yyVals[0+yyTop]);
                     /*
                     NODE *node = $1;
                     if (!node) {
                         node = NEW_STR(STR_NEW0());
                     } else {
                         node = evstr2dstr(node);
                     }
                     $$ = node;
                     */
     return yyVal;
   }
 };
 states[337] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((Token)yyVals[0+yyTop]).getPosition(), null, new StarNode(lexer.getPosition()), null);
     return yyVal;
   }
 };
 states[69] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[270] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[2] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
   /* ENEBO: Removed !compile_for_eval which probably is to reduce warnings*/
                   if (((Node)yyVals[0+yyTop]) != null) {
                       /* last expression should not be void */
                       if (((Node)yyVals[0+yyTop]) instanceof BlockNode) {
                           support.checkUselessStatement(((BlockNode)yyVals[0+yyTop]).getLast());
                       } else {
                           support.checkUselessStatement(((Node)yyVals[0+yyTop]));
                       }
                   }
                   support.getResult().setAST(support.addRootNode(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
     return yyVal;
   }
 };
 states[203] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition()), "-@");
     return yyVal;
   }
 };
 states[505] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[371] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.pushBlockScope();
     return yyVal;
   }
 };
 states[36] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newAndNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[304] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
 
                     yyVal = new ModuleNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Colon3Node)yyVals[-3+yyTop]), support.getCurrentScope(), body);
                     support.popCurrentScope();
     return yyVal;
   }
 };
 states[472] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[405] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new StrNode(((Token)yyVals[-1+yyTop]).getPosition(), ByteList.create((String) ((Token)yyVals[0+yyTop]).getValue()));
     return yyVal;
   }
 };
 states[338] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((Token)yyVals[-2+yyTop]).getPosition(), null, null, ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[70] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((Token)yyVals[-1+yyTop]).getPosition(), null, ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[271] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_colon3(((Token)yyVals[-1+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[3] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                   Node node = ((Node)yyVals[-3+yyTop]);
 
                   if (((RescueBodyNode)yyVals[-2+yyTop]) != null) {
                       node = new RescueNode(support.getPosition(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-3+yyTop]), ((RescueBodyNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
                   } else if (((Node)yyVals[-1+yyTop]) != null) {
                       support.warn(ID.ELSE_WITHOUT_RESCUE, support.getPosition(((Node)yyVals[-3+yyTop])), "else without rescue is useless");
                       node = support.appendToBlock(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                   }
                   if (((Node)yyVals[0+yyTop]) != null) {
                       if (node == null) node = NilImplicitNode.NIL;
                       node = new EnsureNode(support.getPosition(((Node)yyVals[-3+yyTop])), node, ((Node)yyVals[0+yyTop]));
                   }
 
                   yyVal = node;
     return yyVal;
   }
 };
 states[204] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((FloatNode)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition()), "-@");
     return yyVal;
   }
 };
 states[506] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (!(((Node)yyVals[0+yyTop]) instanceof SelfNode)) {
                         support.checkExpression(((Node)yyVals[0+yyTop]));
                     }
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[439] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      lexer.setState(LexState.EXPR_END);
 
                      /* DStrNode: :"some text #{some expression}"*/
                      /* StrNode: :"some text"*/
                      /* EvStrNode :"#{some expression}"*/
-                     if (((Node)yyVals[-1+yyTop]) == null) {
-                       support.yyerror("empty symbol literal");
-                     }
+//                     if (((Node)yyVals[-1+yyTop]) == null) {
+//                       support.yyerror("empty symbol literal");
+//                     }
                      /* FIXME: No node here seems to be an empty string
                         instead of an error
                         if (!($$ = $2)) {
                         $$ = NEW_LIT(ID2SYM(rb_intern("")));
                         }
                      */
-
-                     if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
+                     if (((Node)yyVals[-1+yyTop]) == null) {
+                       yyVal = new SymbolNode(((Token)yyVals[-2+yyTop]).getPosition(), "");
+                     } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
                          yyVal = new DSymbolNode(((Token)yyVals[-2+yyTop]).getPosition(), ((DStrNode)yyVals[-1+yyTop]));
                      } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
                          yyVal = new SymbolNode(((Token)yyVals[-2+yyTop]).getPosition(), ((StrNode)yyVals[-1+yyTop]).getValue().toString().intern());
                      } else {
                          yyVal = new DSymbolNode(((Token)yyVals[-2+yyTop]).getPosition());
                          ((DSymbolNode)yyVal).add(((Node)yyVals[-1+yyTop]));
                      }
     return yyVal;
   }
 };
 states[372] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IterNode(support.getPosition(((Token)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                     support.popCurrentScope();
     return yyVal;
   }
 };
 states[104] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    lexer.setState(LexState.EXPR_END);
                    yyVal = ((Token)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[305] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.setInDef(true);
                     support.pushLocalScope();
     return yyVal;
   }
 };
 states[37] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newOrNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[540] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                       yyVal = null;
     return yyVal;
   }
 };
 states[473] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[406] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[339] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[71] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((Token)yyVals[-3+yyTop]).getPosition(), null, ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[272] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition position = ((Token)yyVals[-2+yyTop]).getPosition();
                     if (((Node)yyVals[-1+yyTop]) == null) {
                         yyVal = new ZArrayNode(position); /* zero length array */
                     } else {
                         yyVal = ((Node)yyVals[-1+yyTop]);
                         ((ISourcePositionHolder)yyVal).setPosition(position);
                     }
     return yyVal;
   }
 };
 states[4] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
                         support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
                     }
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[205] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "+@");
     return yyVal;
   }
 };
 states[507] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
 states[440] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[373] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
                     if (((Node)yyVals[-1+yyTop]) instanceof YieldNode) {
                         throw new SyntaxException(PID.BLOCK_GIVEN_TO_YIELD, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "block given to yield");
                     }
                     if (((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                     ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[239] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newArrayNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[105] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    lexer.setState(LexState.EXPR_END);
                    yyVal = ((Token)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[306] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     /* TODO: We should use implicit nil for body, but problem (punt til later)*/
                     Node body = ((Node)yyVals[-1+yyTop]); /*$5 == null ? NilImplicitNode.NIL : $5;*/
 
                     yyVal = new DefnNode(((Token)yyVals[-5+yyTop]).getPosition(), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInDef(false);
     return yyVal;
   }
 };
 states[38] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
     return yyVal;
   }
 };
 states[541] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                   yyVal = null;
     return yyVal;
   }
 };
 states[474] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[407] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[340] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[72] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                       yyVal = new MultipleAsgn19Node(((Token)yyVals[0+yyTop]).getPosition(), null, new StarNode(lexer.getPosition()), null);
     return yyVal;
   }
 };
 states[273] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Hash19Node(((Token)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[206] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "-@");
     return yyVal;
   }
 };
 states[508] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-1+yyTop]) == null) {
                         support.yyerror("can't define single method for ().");
                     } else if (((Node)yyVals[-1+yyTop]) instanceof ILiteralNode) {
                         support.yyerror("can't define single method for literals.");
                     }
                     support.checkExpression(((Node)yyVals[-1+yyTop]));
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[441] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = ((FloatNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[374] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[106] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new LiteralNode(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[307] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_FNAME);
     return yyVal;
   }
 };
 states[39] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
     return yyVal;
   }
 };
 states[240] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.arg_blk_pass(((Node)yyVals[-1+yyTop]), ((BlockPassNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[475] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[408] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
 
                     ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
                     int extraLength = ((String) ((Token)yyVals[-2+yyTop]).getValue()).length() - 1;
 
                     /* We may need to subtract addition offset off of first */
                     /* string fragment (we optimistically take one off in*/
                     /* ParserSupport.literal_concat).  Check token length*/
                     /* and subtract as neeeded.*/
                     if ((((Node)yyVals[-1+yyTop]) instanceof DStrNode) && extraLength > 0) {
                       Node strNode = ((DStrNode)((Node)yyVals[-1+yyTop])).get(0);
                     }
     return yyVal;
   }
 };
 states[341] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[73] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                       yyVal = new MultipleAsgn19Node(((Token)yyVals[-2+yyTop]).getPosition(), null, new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[274] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ReturnNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[6] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
 states[207] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "|", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[509] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[442] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.negateInteger(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[375] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[107] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new LiteralNode(((Token)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[308] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.setInSingle(support.getInSingle() + 1);
                     support.pushLocalScope();
                     lexer.setState(LexState.EXPR_END); /* force for args */
     return yyVal;
   }
 };
 states[241] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newArrayNode(((ListNode)yyVals[-1+yyTop]).getPosition(), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
                     yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[476] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[409] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition position = ((Token)yyVals[-2+yyTop]).getPosition();
 
                     if (((Node)yyVals[-1+yyTop]) == null) {
                         yyVal = new XStrNode(position, null);
                     } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
                         yyVal = new XStrNode(position, (ByteList) ((StrNode)yyVals[-1+yyTop]).getValue().clone());
                     } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
                         yyVal = new DXStrNode(position, ((DStrNode)yyVals[-1+yyTop]));
 
                         ((Node)yyVal).setPosition(position);
                     } else {
                         yyVal = new DXStrNode(position).add(((Node)yyVals[-1+yyTop]));
                     }
     return yyVal;
   }
 };
 states[342] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[275] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_yield(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[7] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
     return yyVal;
   }
 };
 states[208] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "^", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[510] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[443] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.negateFloat(((FloatNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[376] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[108] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((LiteralNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[309] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     /* TODO: We should use implicit nil for body, but problem (punt til later)*/
                     Node body = ((Node)yyVals[-1+yyTop]); /*$8 == null ? NilImplicitNode.NIL : $8;*/
 
                     yyVal = new DefsNode(((Token)yyVals[-8+yyTop]).getPosition(), ((Node)yyVals[-7+yyTop]), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInSingle(support.getInSingle() - 1);
     return yyVal;
   }
 };
 states[41] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.checkExpression(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[242] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), new Hash19Node(lexer.getPosition(), ((ListNode)yyVals[-1+yyTop])));
                     yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[477] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[410] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     int options = ((RegexpNode)yyVals[0+yyTop]).getOptions();
                     Node node = ((Node)yyVals[-1+yyTop]);
 
                     if (node == null) {
                         yyVal = new RegexpNode(((Token)yyVals[-2+yyTop]).getPosition(), ByteList.create(""), options & ~ReOptions.RE_OPTION_ONCE);
                     } else if (node instanceof StrNode) {
                         yyVal = new RegexpNode(((Node)yyVals[-1+yyTop]).getPosition(), (ByteList) ((StrNode) node).getValue().clone(), options & ~ReOptions.RE_OPTION_ONCE);
                     } else if (node instanceof DStrNode) {
                         yyVal = new DRegexpNode(((Token)yyVals[-2+yyTop]).getPosition(), (DStrNode) node, options, (options & ReOptions.RE_OPTION_ONCE) != 0);
                     } else {
                         yyVal = new DRegexpNode(((Token)yyVals[-2+yyTop]).getPosition(), options, (options & ReOptions.RE_OPTION_ONCE) != 0).add(node);
                     }
     return yyVal;
   }
 };
 states[343] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[276] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZYieldNode(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[8] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[209] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "&", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[75] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[377] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[109] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[310] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BreakNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[243] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
     return yyVal;
   }
 };
 states[478] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[411] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZArrayNode(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[344] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     RestArgNode rest = new UnnamedRestArgNode(((ListNode)yyVals[-1+yyTop]).getPosition(), support.getCurrentScope().addVariable("*"));
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, rest, null, null);
     return yyVal;
   }
 };
 states[9] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_FNAME);
     return yyVal;
   }
 };
 states[210] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[76] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newArrayNode(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[277] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZYieldNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[512] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).addAll(((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[378] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[110] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newUndef(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[311] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new NextNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[244] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = Long.valueOf(lexer.getCmdArgumentState().begin());
     return yyVal;
   }
 };
 states[479] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[412] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[345] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[10] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newAlias(((Token)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[211] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[77] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[278] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new DefinedNode(((Token)yyVals[-4+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[513] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition pos;
                     if (((Node)yyVals[-2+yyTop]) == null && ((Node)yyVals[0+yyTop]) == null) {
                         pos = ((Token)yyVals[-1+yyTop]).getPosition();
                     } else {
                         pos = ((Node)yyVals[-2+yyTop]).getPosition();
                     }
 
                     yyVal = support.newArrayNode(pos, ((Node)yyVals[-2+yyTop])).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[379] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]), null, null);
     return yyVal;
   }
 };
 states[312] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new RedoNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[44] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ReturnNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
     return yyVal;
   }
 };
 states[245] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[111] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_FNAME);
     return yyVal;
   }
 };
 states[480] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(lexer.getPosition(), null, null, null, null, null);
     return yyVal;
   }
 };
 states[413] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[346] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[11] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new VAliasNode(((Token)yyVals[-2+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[212] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[78] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[279] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[-1+yyTop])), "!");
     return yyVal;
   }
 };
 states[514] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition pos = ((Token)yyVals[-1+yyTop]).getPosition();
                     yyVal = support.newArrayNode(pos, new SymbolNode(pos, (String) ((Token)yyVals[-1+yyTop]).getValue())).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[380] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[313] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new RetryNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[45] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BreakNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
     return yyVal;
   }
 };
 states[246] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BlockPassNode(((Token)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[112] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.appendToBlock(((Node)yyVals[-3+yyTop]), support.newUndef(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
 states[481] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be a constant");
     return yyVal;
   }
 };
 states[414] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DStrNode(((ListNode)yyVals[-2+yyTop]).getPosition()).add(((Node)yyVals[-1+yyTop])) : ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[347] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-3+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[12] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new VAliasNode(((Token)yyVals[-2+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), "$" + ((BackRefNode)yyVals[0+yyTop]).getType());
     return yyVal;
   }
 };
 states[213] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[79] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[280] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
     return yyVal;
   }
 };
 states[381] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[46] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new NextNode(((Token)yyVals[-1+yyTop]).getPosition(), support.ret_args(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]).getPosition()));
     return yyVal;
   }
 };
 states[247] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((BlockPassNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[314] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.checkExpression(((Node)yyVals[0+yyTop]));
                     yyVal = ((Node)yyVals[0+yyTop]);
                     if (yyVal == null) yyVal = NilImplicitNode.NIL;
     return yyVal;
   }
 };
 states[482] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be an instance variable");
     return yyVal;
   }
 };
 states[348] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-5+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[13] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("can't make alias for the number variables");
     return yyVal;
   }
 };
 states[214] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[80] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[281] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new FCallNoArgBlockNode(((Token)yyVals[-1+yyTop]).getPosition(), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((IterNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[449] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Token("nil", Tokens.kNIL, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[382] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[248] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[483] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be a global variable");
     return yyVal;
   }
 };
 states[416] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[349] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-1+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[14] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[215] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "==", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[81] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[450] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Token("self", Tokens.kSELF, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[383] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZSuperNode(((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[48] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[484] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be a class variable");
     return yyVal;
   }
 };
 states[417] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = new ZArrayNode(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[350] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[15] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), null);
     return yyVal;
   }
 };
 states[216] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "===", ((Node)yyVals[0+yyTop]), lexer.getPosition());
     return yyVal;
   }
 };
 states[82] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
     return yyVal;
   }
 };
 states[283] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-1+yyTop]) != null && 
                           ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                     ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[451] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new Token("true", Tokens.kTRUE, ((Token)yyVals[0+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[384] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-3+yyTop]) instanceof SelfNode) {
                         yyVal = support.new_fcall(new Token("[]", support.getPosition(((Node)yyVals[-3+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
                     } else {
                         yyVal = support.new_call(((Node)yyVals[-3+yyTop]), new Token("[]", support.getPosition(((Node)yyVals[-3+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
                     }
     return yyVal;
   }
 };
 states[49] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[250] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition pos = ((Node)yyVals[0+yyTop]) == null ? lexer.getPosition() : ((Node)yyVals[0+yyTop]).getPosition();
                     yyVal = support.newArrayNode(pos, ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[418] = new ParserState() {
   public Object execute(ParserSupport support, RubyYaccLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-1+yyTop]);
                     ((ISourcePositionHolder)yyVal).setPosition(((Token)yyVals[-2+yyTop]).getPosition());
     return yyVal;
   }
diff --git a/src/org/jruby/parser/Ruby19Parser.y b/src/org/jruby/parser/Ruby19Parser.y
index 29271b74b1..1af3f8183f 100644
--- a/src/org/jruby/parser/Ruby19Parser.y
+++ b/src/org/jruby/parser/Ruby19Parser.y
@@ -684,1377 +684,1378 @@ fsym           : fname {
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
                     Node rescueNode = new RescueNode(pos, $3, new RescueBodyNode(pos, null, body, null), null);
 
                     pos = $1.getPosition();
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
                     $$ = new ForNode($1.getPosition(), $2, $8, $5);
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
                     lexer.setState(LexState.EXPR_END); /* force for args */
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
                     RestArgNode rest = new UnnamedRestArgNode($1.getPosition(), support.getCurrentScope().addVariable("*"));
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
                 | primary_value tDOT paren_args {
                     $$ = support.new_call($1, new Token("call", $1.getPosition()), $3, null);
                 }
                 | primary_value tCOLON2 paren_args {
                     $$ = support.new_call($1, new Token("call", $1.getPosition()), $3, null);
                 }
                 | kSUPER paren_args {
                     $$ = support.new_super($2, $1);
                 }
                 | kSUPER {
                     $$ = new ZSuperNode($1.getPosition());
                 }
                 | primary_value '[' opt_call_args rbracket {
                     if ($1 instanceof SelfNode) {
                         $$ = support.new_fcall(new Token("[]", support.getPosition($1)), $3, null);
                     } else {
                         $$ = support.new_call($1, new Token("[]", support.getPosition($1)), $3, null);
                     }
                 }
 
 brace_block     : tLCURLY {
                     support.pushBlockScope();
                 } opt_block_param compstmt tRCURLY {
                     $$ = new IterNode($1.getPosition(), $3, $4, support.getCurrentScope());
                     support.popCurrentScope();
                 }
                 | kDO {
                     support.pushBlockScope();
                 } opt_block_param compstmt kEND {
                     $$ = new IterNode($1.getPosition(), $3, $4, support.getCurrentScope());
                     // FIXME: What the hell is this?
                     $<ISourcePositionHolder>0.setPosition(support.getPosition($<ISourcePositionHolder>0));
                     support.popCurrentScope();
                 }
 
 case_body       : kWHEN args then compstmt cases {
                     $$ = support.newWhenNode($1.getPosition(), $2, $4, $5);
                 }
 
 cases           : opt_else | case_body
 
 opt_rescue      : kRESCUE exc_list exc_var then compstmt opt_rescue {
                     Node node;
                     if ($3 != null) {
                         node = support.appendToBlock(support.node_assign($3, new GlobalVarNode($1.getPosition(), "$!")), $5);
                         if ($5 != null) {
                             node.setPosition(support.unwrapNewlineNode($5).getPosition());
                         }
                     } else {
                         node = $5;
                     }
                     Node body = node == null ? NilImplicitNode.NIL : node;
                     $$ = new RescueBodyNode($1.getPosition(), $2, body, $6);
                 }
                 | { 
                     $$ = null; 
                 }
 
 exc_list        : arg_value {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
                 | mrhs {
                     $$ = support.splat_array($1);
                     if ($$ == null) $$ = $1;
                 }
                 | none
 
 exc_var         : tASSOC lhs {
                     $$ = $2;
                 }
                 | none
 
 opt_ensure      : kENSURE compstmt {
                     $$ = $2;
                 }
                 | none
 
 literal         : numeric
                 | symbol {
                     // FIXME: We may be intern'ing more than once.
                     $$ = new SymbolNode($1.getPosition(), ((String) $1.getValue()).intern());
                 }
                 | dsym
 
 strings         : string {
                     $$ = $1 instanceof EvStrNode ? new DStrNode($1.getPosition()).add($1) : $1;
                     /*
                     NODE *node = $1;
                     if (!node) {
                         node = NEW_STR(STR_NEW0());
                     } else {
                         node = evstr2dstr(node);
                     }
                     $$ = node;
                     */
                 }
 
 // [!null]
 string          : tCHAR {
                     $$ = new StrNode($<Token>0.getPosition(), ByteList.create((String) $1.getValue()));
                 }
                 | string1 {
                     $$ = $1;
                 }
                 | string string1 {
                     $$ = support.literal_concat($1.getPosition(), $1, $2);
                 }
 
 string1         : tSTRING_BEG string_contents tSTRING_END {
                     $$ = $2;
 
                     $<ISourcePositionHolder>$.setPosition($1.getPosition());
                     int extraLength = ((String) $1.getValue()).length() - 1;
 
                     // We may need to subtract addition offset off of first 
                     // string fragment (we optimistically take one off in
                     // ParserSupport.literal_concat).  Check token length
                     // and subtract as neeeded.
                     if (($2 instanceof DStrNode) && extraLength > 0) {
                       Node strNode = ((DStrNode)$2).get(0);
                     }
                 }
 
 xstring         : tXSTRING_BEG xstring_contents tSTRING_END {
                     ISourcePosition position = $1.getPosition();
 
                     if ($2 == null) {
                         $$ = new XStrNode(position, null);
                     } else if ($2 instanceof StrNode) {
                         $$ = new XStrNode(position, (ByteList) $<StrNode>2.getValue().clone());
                     } else if ($2 instanceof DStrNode) {
                         $$ = new DXStrNode(position, $<DStrNode>2);
 
                         $<Node>$.setPosition(position);
                     } else {
                         $$ = new DXStrNode(position).add($2);
                     }
                 }
 
 regexp          : tREGEXP_BEG xstring_contents tREGEXP_END {
                     int options = $3.getOptions();
                     Node node = $2;
 
                     if (node == null) {
                         $$ = new RegexpNode($1.getPosition(), ByteList.create(""), options & ~ReOptions.RE_OPTION_ONCE);
                     } else if (node instanceof StrNode) {
                         $$ = new RegexpNode($2.getPosition(), (ByteList) ((StrNode) node).getValue().clone(), options & ~ReOptions.RE_OPTION_ONCE);
                     } else if (node instanceof DStrNode) {
                         $$ = new DRegexpNode($1.getPosition(), (DStrNode) node, options, (options & ReOptions.RE_OPTION_ONCE) != 0);
                     } else {
                         $$ = new DRegexpNode($1.getPosition(), options, (options & ReOptions.RE_OPTION_ONCE) != 0).add(node);
                     }
                 }
 
 words           : tWORDS_BEG ' ' tSTRING_END {
                     $$ = new ZArrayNode($1.getPosition());
                 }
                 | tWORDS_BEG word_list tSTRING_END {
                     $$ = $2;
                 }
 
 word_list       : /* none */ {
                     $$ = new ArrayNode(lexer.getPosition());
                 }
                 | word_list word ' ' {
                      $$ = $1.add($2 instanceof EvStrNode ? new DStrNode($1.getPosition()).add($2) : $2);
                 }
 
 word            : string_content
                 | word string_content {
                      $$ = support.literal_concat(support.getPosition($1), $1, $2);
                 }
 
 qwords          : tQWORDS_BEG ' ' tSTRING_END {
                      $$ = new ZArrayNode($1.getPosition());
                 }
                 | tQWORDS_BEG qword_list tSTRING_END {
                     $$ = $2;
                     $<ISourcePositionHolder>$.setPosition($1.getPosition());
                 }
 
 qword_list      : /* none */ {
                     $$ = new ArrayNode(lexer.getPosition());
                 }
                 | qword_list tSTRING_CONTENT ' ' {
                     $$ = $1.add($2);
                 }
 
 string_contents : /* none */ {
                     $$ = new StrNode($<Token>0.getPosition(), ByteList.create(""));
                 }
                 | string_contents string_content {
                     $$ = support.literal_concat($1.getPosition(), $1, $2);
                 }
 
 xstring_contents: /* none */ {
                     $$ = null;
                 }
                 | xstring_contents string_content {
                     $$ = support.literal_concat(support.getPosition($1), $1, $2);
                 }
 
 string_content  : tSTRING_CONTENT {
                     $$ = $1;
                 }
                 | tSTRING_DVAR {
                     $$ = lexer.getStrTerm();
                     lexer.setStrTerm(null);
                     lexer.setState(LexState.EXPR_BEG);
                 } string_dvar {
                     lexer.setStrTerm($<StrTerm>2);
                     $$ = new EvStrNode($1.getPosition(), $3);
                 }
                 | tSTRING_DBEG {
                    $$ = lexer.getStrTerm();
                    lexer.setStrTerm(null);
                    lexer.setState(LexState.EXPR_BEG);
                 } compstmt tRCURLY {
                    lexer.setStrTerm($<StrTerm>2);
 
                    $$ = support.newEvStrNode($1.getPosition(), $3);
                 }
 
 string_dvar     : tGVAR {
                      $$ = new GlobalVarNode($1.getPosition(), (String) $1.getValue());
                 }
                 | tIVAR {
                      $$ = new InstVarNode($1.getPosition(), (String) $1.getValue());
                 }
                 | tCVAR {
                      $$ = new ClassVarNode($1.getPosition(), (String) $1.getValue());
                 }
                 | backref
 
 // Token:symbol
 symbol          : tSYMBEG sym {
                      lexer.setState(LexState.EXPR_END);
                      $$ = $2;
                      $<ISourcePositionHolder>$.setPosition($1.getPosition());
                 }
 
 // Token:symbol
 sym             : fname | tIVAR | tGVAR | tCVAR
 
 dsym            : tSYMBEG xstring_contents tSTRING_END {
                      lexer.setState(LexState.EXPR_END);
 
                      // DStrNode: :"some text #{some expression}"
                      // StrNode: :"some text"
                      // EvStrNode :"#{some expression}"
-                     if ($2 == null) {
+                     /*if ($2 == null) {
                        support.yyerror("empty symbol literal");
-                     }
+                     }*/
                      /* FIXME: No node here seems to be an empty string
                         instead of an error
                         if (!($$ = $2)) {
                         $$ = NEW_LIT(ID2SYM(rb_intern("")));
                         }
                      */
-
-                     if ($2 instanceof DStrNode) {
+                     if ($2 == null) {
+                         $$ = new SymbolNode($1.getPosition(), "");
+                     } else if ($2 instanceof DStrNode) {
                          $$ = new DSymbolNode($1.getPosition(), $<DStrNode>2);
                      } else if ($2 instanceof StrNode) {
                          $$ = new SymbolNode($1.getPosition(), $<StrNode>2.getValue().toString().intern());
                      } else {
                          $$ = new DSymbolNode($1.getPosition());
                          $<DSymbolNode>$.add($2);
                      }
                 }
 
 numeric         : tINTEGER {
                     $$ = $1;
                 }
                 | tFLOAT {
                      $$ = $1;
                 }
                 | tUMINUS_NUM tINTEGER %prec tLOWEST {
                      $$ = support.negateInteger($2);
                 }
                 | tUMINUS_NUM tFLOAT %prec tLOWEST {
                      $$ = support.negateFloat($2);
                 }
 
 // [!null]
 variable        : tIDENTIFIER | tIVAR | tGVAR | tCONSTANT | tCVAR
                 | kNIL { 
                     $$ = new Token("nil", Tokens.kNIL, $1.getPosition());
                 }
                 | kSELF {
                     $$ = new Token("self", Tokens.kSELF, $1.getPosition());
                 }
                 | kTRUE { 
                     $$ = new Token("true", Tokens.kTRUE, $1.getPosition());
                 }
                 | kFALSE {
                     $$ = new Token("false", Tokens.kFALSE, $1.getPosition());
                 }
                 | k__FILE__ {
                     $$ = new Token("__FILE__", Tokens.k__FILE__, $1.getPosition());
                 }
                 | k__LINE__ {
                     $$ = new Token("__LINE__", Tokens.k__LINE__, $1.getPosition());
                 }
                 | k__ENCODING__ {
                     $$ = new Token("__ENCODING__", Tokens.k__ENCODING__, $1.getPosition());
                 }
 
 // [!null]
 var_ref         : variable {
                     $$ = support.gettable($1);
                 }
 
 // [!null]
 var_lhs         : variable {
                     $$ = support.assignable($1, NilImplicitNode.NIL);
                 }
 
 // [!null]
 backref         : tNTH_REF {
                     $$ = $1;
                 }
                 | tBACK_REF {
                     $$ = $1;
                 }
 
 superclass      : term {
                     $$ = null;
                 }
                 | tLT {
                    lexer.setState(LexState.EXPR_BEG);
                 } expr_value term {
                     $$ = $3;
                 }
                 | error term {
                    $$ = null;
                 }
 
 // [!null]
 // ENEBO: Look at command_start stuff I am ripping out
 f_arglist       : tLPAREN2 f_args rparen {
                     $$ = $2;
                     $<ISourcePositionHolder>$.setPosition($1.getPosition());
                     lexer.setState(LexState.EXPR_BEG);
                 }
                 | f_args term {
                     $$ = $1;
                 }
 
 // [!null]
 f_args          : f_arg ',' f_optarg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, null, $6);
                 }
                 | f_arg ',' f_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, $7, $8);
                 }
                 | f_arg ',' f_optarg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, null, $4);
                 }
                 | f_arg ',' f_optarg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, $5, $6);
                 }
                 | f_arg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, null, $4);
                 }
                 | f_arg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, $5, $6);
                 }
                 | f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), $1, null, null, null, $2);
                 }
                 | f_optarg ',' f_rest_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, $1, $3, null, $4);
                 }
                 | f_optarg ',' f_rest_arg ',' f_arg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, $1, $3, $5, $6);
                 }
                 | f_optarg opt_f_block_arg {
                     $$ = support.new_args($1.getPosition(), null, $1, null, null, $2);
                 }
                 | f_optarg ',' f_arg opt_f_block_arg {
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
                 | /* none */ {
                     $$ = support.new_args(lexer.getPosition(), null, null, null, null, null);
                 }
 
 f_bad_arg       : tCONSTANT {
                     support.yyerror("formal argument cannot be a constant");
                 }
                 | tIVAR {
                     support.yyerror("formal argument cannot be an instance variable");
                 }
                 | tGVAR {
                     support.yyerror("formal argument cannot be a global variable");
                 }
                 | tCVAR {
                     support.yyerror("formal argument cannot be a class variable");
                 }
 
 // Token:f_norm_arg [!null]
 f_norm_arg      : f_bad_arg
                 | tIDENTIFIER {
     // FIXME: Resolve what the hell is going on
     /*                    if (support.is_local_id($1)) {
                         support.yyerror("formal argument must be local variable");
                         }*/
                      
                     support.shadowing_lvar($1);
                     $$ = $1;
                 }
 
 f_arg_item      : f_norm_arg {
                     support.arg_var($1);
                     $$ = new ArgumentNode($<ISourcePositionHolder>1.getPosition(), (String) $1.getValue());
   /*
                     $$ = new ArgAuxiliaryNode($1.getPosition(), (String) $1.getValue(), 1);
   */
                 }
                 | tLPAREN f_margs rparen {
                     $$ = $2;
                     /*		    {
 			ID tid = internal_id();
 			arg_var(tid);
 			if (dyna_in_block()) {
 			    $2->nd_value = NEW_DVAR(tid);
 			}
 			else {
 			    $2->nd_value = NEW_LVAR(tid);
 			}
 			$$ = NEW_ARGS_AUX(tid, 1);
 			$$->nd_next = $2;*/
                 }
 
 // [!null]
 f_arg           : f_arg_item {
                     $$ = new ArrayNode(lexer.getPosition(), $1);
                 }
                 | f_arg ',' f_arg_item {
                     $1.add($3);
                     $$ = $1;
                 }
 
 f_opt           : tIDENTIFIER '=' arg_value {
                     if (!support.is_local_id($1)) {
                         support.yyerror("formal argument must be local variable");
                     }
                     support.shadowing_lvar($1);
                     support.arg_var($1);
                     $$ = new OptArgNode($1.getPosition(), support.assignable($1, $3));
                 }
 
 f_block_opt     : tIDENTIFIER '=' primary_value {
                     if (!support.is_local_id($1)) {
                         support.yyerror("formal argument must be local variable");
                     }
                     support.shadowing_lvar($1);
                     support.arg_var($1);
                     $$ = new OptArgNode($1.getPosition(), support.assignable($1, $3));
                 }
 
 f_block_optarg  : f_block_opt {
                     $$ = new BlockNode($1.getPosition()).add($1);
                 }
                 | f_block_optarg ',' f_block_opt {
                     $$ = support.appendToBlock($1, $3);
                 }
 
 f_optarg        : f_opt {
                     $$ = new BlockNode($1.getPosition()).add($1);
                 }
                 | f_optarg ',' f_opt {
                     $$ = support.appendToBlock($1, $3);
                 }
 
 restarg_mark    : tSTAR2 | tSTAR
 
 // [!null]
 f_rest_arg      : restarg_mark tIDENTIFIER {
                     if (!support.is_local_id($2)) {
                         support.yyerror("duplicate rest argument name");
                     }
                     support.shadowing_lvar($2);
                     $$ = new RestArgNode($1.getPosition(), (String) $2.getValue(), support.arg_var($2));
                 }
                 | restarg_mark {
                     $$ = new UnnamedRestArgNode($1.getPosition(), support.getCurrentScope().addVariable("*"));
                 }
 
 // [!null]
 blkarg_mark     : tAMPER2 | tAMPER
 
 // f_block_arg - Block argument def for function (foo(&block)) [!null]
 f_block_arg     : blkarg_mark tIDENTIFIER {
                     String identifier = (String) $2.getValue();
 
                     if (!support.is_local_id($2)) {
                         support.yyerror("block argument must be local variable");
                     }
                     support.shadowing_lvar($2);
                     $$ = new BlockArgNode($1.getPosition(), support.arg_var($2), identifier);
                 }
 
 opt_f_block_arg : ',' f_block_arg {
                     $$ = $2;
                 }
                 | /* none */ {
                     $$ = null;
                 }
 
 singleton       : var_ref {
                     if (!($1 instanceof SelfNode)) {
                         support.checkExpression($1);
                     }
                     $$ = $1;
                 }
                 | tLPAREN2 {
                     lexer.setState(LexState.EXPR_BEG);
                 } expr rparen {
                     if ($3 == null) {
                         support.yyerror("can't define single method for ().");
                     } else if ($3 instanceof ILiteralNode) {
                         support.yyerror("can't define single method for literals.");
                     }
                     support.checkExpression($3);
                     $$ = $3;
                 }
 
 // [!null]
 assoc_list      : none {
                     $$ = new ArrayNode(lexer.getPosition());
                 }
                 | assocs trailer {
                     $$ = $1;
                 }
 
 // [!null]
 assocs          : assoc
                 | assocs ',' assoc {
                     $$ = $1.addAll($3);
                 }
 
 // [!null]
 assoc           : arg_value tASSOC arg_value {
                     ISourcePosition pos;
                     if ($1 == null && $3 == null) {
                         pos = $2.getPosition();
                     } else {
                         pos = $1.getPosition();
                     }
 
                     $$ = support.newArrayNode(pos, $1).add($3);
                 }
                 | tLABEL arg_value {
                     ISourcePosition pos = $1.getPosition();
                     $$ = support.newArrayNode(pos, new SymbolNode(pos, (String) $1.getValue())).add($2);
                 }
 
 operation       : tIDENTIFIER | tCONSTANT | tFID
 operation2      : tIDENTIFIER | tCONSTANT | tFID | op
 operation3      : tIDENTIFIER | tFID | op
 dot_or_colon    : tDOT | tCOLON2
 opt_terms       : /* none */ | terms
 opt_nl          : /* none */ | '\n'
 rparen          : opt_nl tRPAREN {
                     $$ = $2;
                 }
 rbracket        : opt_nl tRBRACK {
                     $$ = $2;
                 }
 trailer         : /* none */ | '\n' | ','
 
 term            : ';'
                 | '\n'
 
 terms           : term
                 | terms ';'
 
 none            : /* none */ {
                       $$ = null;
                 }
 
 none_block_pass : /* none */ {  
                   $$ = null;
                 }
 
 %%
 
     /** The parse method use an lexer stream and parse it to an AST node 
      * structure
      */
     public RubyParserResult parse(ParserConfiguration configuration, LexerSource source) throws IOException {
         support.reset();
         support.setConfiguration(configuration);
         support.setResult(new RubyParserResult());
         
         lexer.reset();
         lexer.setSource(source);
         lexer.setEncoding(configuration.getKCode().getEncoding());
 
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
