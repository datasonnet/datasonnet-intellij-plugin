// This is a generated file. Not intended for manual editing.
package io.portx.datasonnet.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.portx.datasonnet.language.psi.impl.*;
import io.portx.datasonnet.language.psi.impl.*;

public interface DataSonnetTypes {

  IElementType APPLY = new DataSonnetElementType("APPLY");
  IElementType ARG = new DataSonnetElementType("ARG");
  IElementType ARGS = new DataSonnetElementType("ARGS");
  IElementType ARR = new DataSonnetElementType("ARR");
  IElementType ARRCOMP = new DataSonnetElementType("ARRCOMP");
  IElementType ASSERT_STMT = new DataSonnetElementType("ASSERT_STMT");
  IElementType BINARYOP = new DataSonnetElementType("BINARYOP");
  IElementType BIND = new DataSonnetElementType("BIND");
  IElementType BINSUFFIX = new DataSonnetElementType("BINSUFFIX");
  IElementType COMPSPEC = new DataSonnetElementType("COMPSPEC");
  IElementType EXPR = new DataSonnetElementType("EXPR");
  IElementType EXPR_0 = new DataSonnetElementType("EXPR_0");
  IElementType FIELD = new DataSonnetElementType("FIELD");
  IElementType FIELDNAME = new DataSonnetElementType("FIELDNAME");
  IElementType FORSPEC = new DataSonnetElementType("FORSPEC");
  IElementType H = new DataSonnetElementType("H");
  IElementType IDENTIFIER_0 = new DataSonnetElementType("IDENTIFIER_0");
  IElementType IFSPEC = new DataSonnetElementType("IFSPEC");
  IElementType IMPORTOP = new DataSonnetElementType("IMPORTOP");
  IElementType IMPORTSTROP = new DataSonnetElementType("IMPORTSTROP");
  IElementType INSUPER = new DataSonnetElementType("INSUPER");
  IElementType MEMBER = new DataSonnetElementType("MEMBER");
  IElementType MEMBERS = new DataSonnetElementType("MEMBERS");
  IElementType OBJ = new DataSonnetElementType("OBJ");
  IElementType OBJEXTEND = new DataSonnetElementType("OBJEXTEND");
  IElementType OBJINSIDE = new DataSonnetElementType("OBJINSIDE");
  IElementType OBJLOCAL = new DataSonnetElementType("OBJLOCAL");
  IElementType OUTERLOCAL = new DataSonnetElementType("OUTERLOCAL");
  IElementType PARAM = new DataSonnetElementType("PARAM");
  IElementType PARAMS = new DataSonnetElementType("PARAMS");
  IElementType SELECT = new DataSonnetElementType("SELECT");
  IElementType SLICE = new DataSonnetElementType("SLICE");
  IElementType SLICESUFFIX = new DataSonnetElementType("SLICESUFFIX");
  IElementType UNARYOP = new DataSonnetElementType("UNARYOP");

  IElementType AND = new DataSonnetTokenType("AND");
  IElementType ASSERT = new DataSonnetTokenType("ASSERT");
  IElementType ASTERISK = new DataSonnetTokenType("ASTERISK");
  IElementType BAR = new DataSonnetTokenType("BAR");
  IElementType BLOCK_COMMENT = new DataSonnetTokenType("BLOCK_COMMENT");
  IElementType CARAT = new DataSonnetTokenType("CARAT");
  IElementType COLON = new DataSonnetTokenType("COLON");
  IElementType COLON2 = new DataSonnetTokenType("COLON2");
  IElementType COLON3 = new DataSonnetTokenType("COLON3");
  IElementType COMMA = new DataSonnetTokenType("COMMA");
  IElementType DOLLAR = new DataSonnetTokenType("DOLLAR");
  IElementType DOT = new DataSonnetTokenType("DOT");
  IElementType DOUBLE_AND = new DataSonnetTokenType("DOUBLE_AND");
  IElementType DOUBLE_BAR = new DataSonnetTokenType("DOUBLE_BAR");
  IElementType DOUBLE_EQUAL = new DataSonnetTokenType("DOUBLE_EQUAL");
  IElementType DOUBLE_GREATER = new DataSonnetTokenType("DOUBLE_GREATER");
  IElementType DOUBLE_LESS = new DataSonnetTokenType("DOUBLE_LESS");
  IElementType DOUBLE_QUOTED_STRING = new DataSonnetTokenType("DOUBLE_QUOTED_STRING");
  IElementType ELSE = new DataSonnetTokenType("ELSE");
  IElementType EQUAL = new DataSonnetTokenType("EQUAL");
  IElementType ERROR = new DataSonnetTokenType("ERROR");
  IElementType EXCLAMATION = new DataSonnetTokenType("EXCLAMATION");
  IElementType FALSE = new DataSonnetTokenType("FALSE");
  IElementType FOR = new DataSonnetTokenType("FOR");
  IElementType FUNCTION = new DataSonnetTokenType("FUNCTION");
  IElementType GREATER_EQUAL = new DataSonnetTokenType("GREATER_EQUAL");
  IElementType GREATER_THAN = new DataSonnetTokenType("GREATER_THAN");
  IElementType IDENTIFIER = new DataSonnetTokenType("IDENTIFIER");
  IElementType IF = new DataSonnetTokenType("IF");
  IElementType IMPORT = new DataSonnetTokenType("IMPORT");
  IElementType IMPORTSTR = new DataSonnetTokenType("IMPORTSTR");
  IElementType IN = new DataSonnetTokenType("IN");
  IElementType LESS_EQUAL = new DataSonnetTokenType("LESS_EQUAL");
  IElementType LESS_THAN = new DataSonnetTokenType("LESS_THAN");
  IElementType LINE_COMMENT = new DataSonnetTokenType("LINE_COMMENT");
  IElementType LOCAL = new DataSonnetTokenType("LOCAL");
  IElementType L_BRACKET = new DataSonnetTokenType("L_BRACKET");
  IElementType L_CURLY = new DataSonnetTokenType("L_CURLY");
  IElementType L_PAREN = new DataSonnetTokenType("L_PAREN");
  IElementType MINUS = new DataSonnetTokenType("MINUS");
  IElementType NOT_EQUAL = new DataSonnetTokenType("NOT_EQUAL");
  IElementType NULL = new DataSonnetTokenType("NULL");
  IElementType NUMBER = new DataSonnetTokenType("NUMBER");
  IElementType PERCENT = new DataSonnetTokenType("PERCENT");
  IElementType PLUS = new DataSonnetTokenType("PLUS");
  IElementType R_BRACKET = new DataSonnetTokenType("R_BRACKET");
  IElementType R_CURLY = new DataSonnetTokenType("R_CURLY");
  IElementType R_PAREN = new DataSonnetTokenType("R_PAREN");
  IElementType SELF = new DataSonnetTokenType("SELF");
  IElementType SEMICOLON = new DataSonnetTokenType("SEMICOLON");
  IElementType SINGLE_QUOTED_STRING = new DataSonnetTokenType("SINGLE_QUOTED_STRING");
  IElementType SLASH = new DataSonnetTokenType("SLASH");
  IElementType SUPER = new DataSonnetTokenType("SUPER");
  IElementType THEN = new DataSonnetTokenType("THEN");
  IElementType TILDE = new DataSonnetTokenType("TILDE");
  IElementType TRIPLE_BAR_QUOTED_STRING = new DataSonnetTokenType("TRIPLE_BAR_QUOTED_STRING");
  IElementType TRUE = new DataSonnetTokenType("TRUE");
  IElementType VERBATIM_DOUBLE_QUOTED_STRING = new DataSonnetTokenType("VERBATIM_DOUBLE_QUOTED_STRING");
  IElementType VERBATIM_SINGLE_QUOTED_STRING = new DataSonnetTokenType("VERBATIM_SINGLE_QUOTED_STRING");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == APPLY) {
        return new DataSonnetApplyImpl(node);
      }
      else if (type == ARG) {
        return new DataSonnetArgImpl(node);
      }
      else if (type == ARGS) {
        return new DataSonnetArgsImpl(node);
      }
      else if (type == ARR) {
        return new DataSonnetArrImpl(node);
      }
      else if (type == ARRCOMP) {
        return new DataSonnetArrcompImpl(node);
      }
      else if (type == ASSERT_STMT) {
        return new DataSonnetAssertStmtImpl(node);
      }
      else if (type == BINARYOP) {
        return new DataSonnetBinaryopImpl(node);
      }
      else if (type == BIND) {
        return new DataSonnetBindImpl(node);
      }
      else if (type == BINSUFFIX) {
        return new DataSonnetBinsuffixImpl(node);
      }
      else if (type == COMPSPEC) {
        return new DataSonnetCompspecImpl(node);
      }
      else if (type == EXPR) {
        return new DataSonnetExprImpl(node);
      }
      else if (type == EXPR_0) {
        return new DataSonnetExpr0Impl(node);
      }
      else if (type == FIELD) {
        return new DataSonnetFieldImpl(node);
      }
      else if (type == FIELDNAME) {
        return new DataSonnetFieldnameImpl(node);
      }
      else if (type == FORSPEC) {
        return new DataSonnetForspecImpl(node);
      }
      else if (type == H) {
        return new DataSonnetHImpl(node);
      }
      else if (type == IDENTIFIER_0) {
        return new DataSonnetIdentifier0Impl(node);
      }
      else if (type == IFSPEC) {
        return new DataSonnetIfspecImpl(node);
      }
      else if (type == IMPORTOP) {
        return new DataSonnetImportopImpl(node);
      }
      else if (type == IMPORTSTROP) {
        return new DataSonnetImportstropImpl(node);
      }
      else if (type == INSUPER) {
        return new DataSonnetInsuperImpl(node);
      }
      else if (type == MEMBER) {
        return new DataSonnetMemberImpl(node);
      }
      else if (type == MEMBERS) {
        return new DataSonnetMembersImpl(node);
      }
      else if (type == OBJ) {
        return new DataSonnetObjImpl(node);
      }
      else if (type == OBJEXTEND) {
        return new DataSonnetObjextendImpl(node);
      }
      else if (type == OBJINSIDE) {
        return new DataSonnetObjinsideImpl(node);
      }
      else if (type == OBJLOCAL) {
        return new DataSonnetObjlocalImpl(node);
      }
      else if (type == OUTERLOCAL) {
        return new DataSonnetOuterlocalImpl(node);
      }
      else if (type == PARAM) {
        return new DataSonnetParamImpl(node);
      }
      else if (type == PARAMS) {
        return new DataSonnetParamsImpl(node);
      }
      else if (type == SELECT) {
        return new DataSonnetSelectImpl(node);
      }
      else if (type == SLICE) {
        return new DataSonnetSliceImpl(node);
      }
      else if (type == SLICESUFFIX) {
        return new DataSonnetSlicesuffixImpl(node);
      }
      else if (type == UNARYOP) {
        return new DataSonnetUnaryopImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
