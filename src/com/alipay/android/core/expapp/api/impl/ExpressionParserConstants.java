package com.alipay.android.core.expapp.api.impl;


/**
 * @author sanping.li
 * Token literal values and constants.
 *
 */
public interface ExpressionParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int DOLAR = 5;
  /** RegularExpression Id. */
  int LPAREN = 6;
  /** RegularExpression Id. */
  int RPAREN = 7;
  /** RegularExpression Id. */
  int SEMICOLON = 8;
  /** RegularExpression Id. */
  int COMMA = 9;
  /** RegularExpression Id. */
  int STRING_LITERAL = 10;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\r\"",
    "\"\\t\"",
    "\"\\n\"",
    "\"$\"",
    "\"(\"",
    "\")\"",
    "\";\"",
    "\",\"",
    "<STRING_LITERAL>",
  };

}
