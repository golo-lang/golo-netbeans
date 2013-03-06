package com.serli.goloide.structure;

import fr.insalyon.citi.golo.compiler.ir.*;
import fr.insalyon.citi.golo.compiler.parser.*;
import fr.insalyon.citi.golo.runtime.OperatorType;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import static fr.insalyon.citi.golo.compiler.GoloCompilationException.Problem.Type.UNDECLARED_REFERENCE;
import static fr.insalyon.citi.golo.compiler.ir.GoloFunction.Scope.*;
import static fr.insalyon.citi.golo.compiler.ir.GoloFunction.Visibility.LOCAL;
import static fr.insalyon.citi.golo.compiler.ir.GoloFunction.Visibility.PUBLIC;
import static fr.insalyon.citi.golo.compiler.ir.LocalReference.Kind.CONSTANT;
import static fr.insalyon.citi.golo.compiler.ir.LocalReference.Kind.VARIABLE;
import static fr.insalyon.citi.golo.compiler.parser.ASTLetOrVar.Type.LET;
import static fr.insalyon.citi.golo.compiler.parser.ASTLetOrVar.Type.VAR;
import java.util.ArrayList;
import java.util.Map;
import org.netbeans.modules.csl.api.OffsetRange;

class GenerateFoldsVisitor implements GoloParserVisitor {

  @Override
  public Object visit(SimpleNode node, Object data) {
    throw new IllegalStateException("visit(SimpleNode) shall never be invoked");
  }

  @Override
  public Object visit(ASTCompilationUnit node, Object data) {
    String foldTypeName = "imports";
    Map<String, List<OffsetRange>> folds = (Map<String, List<OffsetRange>>) data;
    List<OffsetRange> importsFolds = folds.get(foldTypeName);
    if (importsFolds == null) {
        importsFolds = new ArrayList<>();
        folds.put(foldTypeName, importsFolds);
    }

    Token startToken = null;
    Token endToken = null;
    int childrenNb = node.jjtGetNumChildren();
    for (int i=0; i<childrenNb; i++) {
        Node child = node.jjtGetChild(i);
        if (child instanceof ASTImportDeclaration) {
            ASTImportDeclaration importDecl = (ASTImportDeclaration) child;
            if (startToken == null) {
                startToken = importDecl.jjtGetFirstToken();
            }
            endToken = importDecl.jjtGetLastToken();
            if (endToken == null) {
                endToken = startToken;
            }
        }
    }
    if (startToken != null) {
        importsFolds.add(new OffsetRange(startToken.next.startOffset, endToken.endOffset));
    }
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTModuleDeclaration node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTImportDeclaration node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTPimpDeclaration node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTFunctionDeclaration node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTFunction node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTUnaryExpression node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTCommutativeExpression node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTAssociativeExpression node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTLiteral node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTReference node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTLetOrVar node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTAssignment node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTReturn node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTThrow node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTBlock node, Object data) {
    String foldTypeName = "codeblocks";
    Map<String, List<OffsetRange>> folds = (Map<String, List<OffsetRange>>) data;
    List<OffsetRange> codeblocksFolds = folds.get(foldTypeName);
    if (codeblocksFolds == null) {
        codeblocksFolds = new ArrayList<>();
        folds.put(foldTypeName, codeblocksFolds);
    }
    codeblocksFolds.add(new OffsetRange(node.jjtGetFirstToken().previousToken.startOffset, node.jjtGetLastToken().next.endOffset));
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTFunctionInvocation node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTMethodInvocation node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTConditionalBranching node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTCase node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTMatch node, Object data) {
    String foldTypeName = "codeblocks";
    Map<String, List<OffsetRange>> folds = (Map<String, List<OffsetRange>>) data;
    List<OffsetRange> codeblocksFolds = folds.get(foldTypeName);
    if (codeblocksFolds == null) {
        codeblocksFolds = new ArrayList<>();
        folds.put(foldTypeName, codeblocksFolds);
    }
    codeblocksFolds.add(new OffsetRange(node.jjtGetFirstToken().next.startOffset, node.jjtGetLastToken().endOffset));
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTWhileLoop node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTForLoop node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTForEachLoop node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTTryCatchFinally node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }
}
