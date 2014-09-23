/*
 *  Copyright 2013 SERLI (www.serli.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */

package org.gololang.netbeans.structure;

import fr.insalyon.citi.golo.compiler.parser.*;
import java.util.List;
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
    codeblocksFolds.add(new OffsetRange(node.jjtGetFirstToken().startOffset, node.jjtGetLastToken().endOffset));
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

  @Override
  public Object visit(ASTerror ast, Object o) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object visit(ASTToplevelDeclaration node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

    @Override
    public Object visit(ASTStructDeclaration node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTAugmentDeclaration node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTContinue node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTBreak node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTCollectionLiteral node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTAnonymousFunctionInvocation node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTDecoratorDeclaration node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }
}
