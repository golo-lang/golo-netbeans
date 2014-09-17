package org.gololang.netbeans.structure;

import fr.insalyon.citi.golo.compiler.parser.*;
import java.util.List;
import java.util.ArrayList;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.parsing.api.Source;

class GenerateStructureVisitor implements GoloParserVisitor {

  Source source;
  
  public GenerateStructureVisitor(Source source) {
    this.source = source;
  }
  
  @Override
  public Object visit(SimpleNode node, Object data) {
    throw new IllegalStateException("visit(SimpleNode) shall never be invoked");
  }

  @Override
  public Object visit(ASTCompilationUnit node, Object data) {
    ASTModuleDeclaration moduleNode = null;
    for (int i=0; i<node.jjtGetNumChildren(); i++) {
      Node childNode = node.jjtGetChild(i);
      if (childNode instanceof ASTModuleDeclaration) {
        moduleNode = (ASTModuleDeclaration) childNode;
        break;
      }
    }
    assert(moduleNode != null);
    
    StructureItem moduleStructureItem = processNode(new ItemCreator<ASTModuleDeclaration>() {
      @Override
      public StructureItem create(ASTModuleDeclaration node, List<? extends StructureItem> childrenItems) {
        return new ModuleStructureItem(node, source, childrenItems);
      }
    }, moduleNode, data);
    node.childrenAccept(this, moduleStructureItem.getNestedItems());
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

  static interface ItemCreator<NodeType extends GoloASTNode> {
    StructureItem create(NodeType node, List<? extends StructureItem> childrenItems);
  }

  private <NodeType extends GoloASTNode> StructureItem processNode(ItemCreator<NodeType> itemCreator, 
      NodeType node, Object data) {
    List<StructureItem> siblingItems = (List<StructureItem>) data;
    
    List<StructureItem> childrenItems = new ArrayList<>();
    node.childrenAccept(this, childrenItems);
    
    StructureItem item = itemCreator.create(node, childrenItems);
    
    siblingItems.add(item);
    
    return item;
  }
  
  @Override
  public Object visit(ASTModuleDeclaration node, Object data) {
    return data;
  }

  @Override
  public Object visit(ASTImportDeclaration node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  @Override
  public Object visit(ASTAugmentDeclaration node, Object data) {
    processNode(new ItemCreator<ASTAugmentDeclaration>() {
      @Override
      public StructureItem create(ASTAugmentDeclaration node, List<? extends StructureItem> childrenItems) {
        return new AugmentStructureItem(node, source, childrenItems);
      }
    }, node, data);
    return data;
  }

  @Override
  public Object visit(ASTFunctionDeclaration node, Object data) {
    processNode(new ItemCreator<ASTFunctionDeclaration>() {
      @Override
      public StructureItem create(ASTFunctionDeclaration node, List<? extends StructureItem> childrenItems) {
        return new FunctionStructureItem(node, source, childrenItems);
      }
    }, node, data);
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
    processNode(new ItemCreator<ASTLetOrVar>() {
      @Override
      public StructureItem create(ASTLetOrVar node, List<? extends StructureItem> childrenItems) {
        return new VariableStructureItem(node, source, childrenItems);
      }
    }, node, data);
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
