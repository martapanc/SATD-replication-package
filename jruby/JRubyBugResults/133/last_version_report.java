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
