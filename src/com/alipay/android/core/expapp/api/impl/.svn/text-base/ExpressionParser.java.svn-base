package com.alipay.android.core.expapp.api.impl;

import java.io.ByteArrayInputStream;

import com.alipay.android.core.expapp.api.Interpreter;
import com.alipay.android.core.expapp.api.Node;
import com.alipay.android.core.expapp.api.Parser;
import com.alipay.android.util.LogUtil;

public class ExpressionParser implements ExpressionParserTreeConstants, ExpressionParserConstants, Parser {
    private String mExpressionId;
	protected JJTExpressionParserState astTree = new JJTExpressionParserState();
	
	public Node parser(){
		try{
			Block();
		}catch (Exception e){
//			System.out.println("Oops.");
		    LogUtil.logContainerDebuggable(Interpreter.TAG, e.getMessage());
//			System.out.println(e.getMessage());
			return null;
		}
		return astTree.rootNode();
	}

	/*Block*/
	public final void Block() throws ParseException {
	  AstBlock jjtn000 = new AstBlock(JJTBLOCK);
	  boolean jjtc000 = true;
	  astTree.openNodeScope(jjtn000);
	    try {
	      Function();
	      label_1:
	      while (true) {
	        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
	        case SEMICOLON:
	          ;
	          break;
	        default:
	          jj_la1[0] = jj_gen;
	          break label_1;
	        }
	        jj_consume_token(SEMICOLON);
	        Function();
	      }
	    } catch (Throwable jjte000) {
	    if (jjtc000) {
	      astTree.clearNodeScope(jjtn000);
	      jjtc000 = false;
	    } else {
	      astTree.popNode();
	    }
	    if (jjte000 instanceof RuntimeException) {
	      {if (true) throw (RuntimeException)jjte000;}
	    }
	    if (jjte000 instanceof ParseException) {
	      {if (true) throw (ParseException)jjte000;}
	    }
	    {if (true) throw (Error)jjte000;}
	    } finally {
	    if (jjtc000) {
	      astTree.closeNodeScope(jjtn000, true);
	    }
	    }
	}

	/*function*/
	public final void Function() throws ParseException {
	  AstFunction jjtn000 = new AstFunction(JJTFUNCTION);
	  jjtn000.setExpressionId(mExpressionId);
	  boolean jjtc000 = true;
	  astTree.openNodeScope(jjtn000);
	    try {
	      jj_consume_token(DOLAR);
	      Literal();
	      jj_consume_token(LPAREN);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DOLAR:
      case STRING_LITERAL:
	      ArgumentList();
        break;
      default:
        jj_la1[1] = jj_gen;
        ;
      }
	      jj_consume_token(RPAREN);
	    } catch (Throwable jjte000) {
	    if (jjtc000) {
	      astTree.clearNodeScope(jjtn000);
	      jjtc000 = false;
	    } else {
	      astTree.popNode();
	    }
	    if (jjte000 instanceof RuntimeException) {
	      {if (true) throw (RuntimeException)jjte000;}
	    }
	    if (jjte000 instanceof ParseException) {
	      {if (true) throw (ParseException)jjte000;}
	    }
	    {if (true) throw (Error)jjte000;}
	    } finally {
	    if (jjtc000) {
	      astTree.closeNodeScope(jjtn000, true);
	    }
	    }
  }

/*Argument*/
  final public void ArgumentList() throws ParseException {
	  AstArgumentList jjtn000 = new AstArgumentList(JJTARGUMENTLIST);
	  boolean jjtc000 = true;
	  astTree.openNodeScope(jjtn000);
	    try {
	      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
	      case STRING_LITERAL:
	        Literal();
	        break;
	      case DOLAR:
	        Function();
	        break;
	      default:
        jj_la1[2] = jj_gen;
	        jj_consume_token(-1);
	        throw new ParseException();
	      }
	      label_2:
	      while (true) {
	        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
	        case COMMA:
	          ;
	          break;
	        default:
          jj_la1[3] = jj_gen;
	          break label_2;
	        }
	        jj_consume_token(COMMA);
	        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
	        case STRING_LITERAL:
	          Literal();
	          break;
	        case DOLAR:
	          Function();
	          break;
	        default:
          jj_la1[4] = jj_gen;
	          jj_consume_token(-1);
	          throw new ParseException();
	        }
	      }
	    } catch (Throwable jjte000) {
	    if (jjtc000) {
	      astTree.clearNodeScope(jjtn000);
	      jjtc000 = false;
	    } else {
	      astTree.popNode();
	    }
	    if (jjte000 instanceof RuntimeException) {
	      {if (true) throw (RuntimeException)jjte000;}
	    }
	    if (jjte000 instanceof ParseException) {
	      {if (true) throw (ParseException)jjte000;}
	    }
	    {if (true) throw (Error)jjte000;}
	    } finally {
	    if (jjtc000) {
	      astTree.closeNodeScope(jjtn000, true);
	    }
	    }
  }

  /*Literal*/
  final public void Literal() throws ParseException {
	  AstLiteral jjtn000 = new AstLiteral(JJTLITERAL);
	  boolean jjtc000 = true;
	  astTree.openNodeScope(jjtn000);
	    try {
	      Token token = jj_consume_token(STRING_LITERAL);
	      jjtn000.setValue(token.toString());
	    } finally {
	    if (jjtc000) {
	      astTree.closeNodeScope(jjtn000, true);
	    }
	    }
  }

  /** Generated Token Manager. */
  public ExpressionParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[5];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x100,0x420,0x420,0x200,0x420,};
   }

  /** Constructor with InputStream. */
  public ExpressionParser(String id,String string) {
     this(new ByteArrayInputStream(string.getBytes()), null);
     mExpressionId = id;
  }
  /** Constructor with InputStream and supplied encoding */
  public ExpressionParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ExpressionParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    astTree.reset();
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public ExpressionParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ExpressionParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    astTree.reset();
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public ExpressionParser(ExpressionParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ExpressionParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    astTree.reset();
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[11];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 5; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 11; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(mExpressionId,token, exptokseq, tokenImage);
  }

}
