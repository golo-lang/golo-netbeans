/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.serli.goloide.structure;

import fr.insalyon.citi.golo.compiler.parser.ASTFunctionDeclaration;
import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author david
 */
public class FunctionStructureItem extends GoloStructureItem<ASTFunctionDeclaration> {

  public FunctionStructureItem(ASTFunctionDeclaration node, Source source, List<? extends StructureItem> items) {
    super(node, source, items);
  }

  @Override
  public String getHtml(HtmlFormatter hf) {
    return getName();
  }

  @Override
  public GoloElementHandle createHandle(ASTFunctionDeclaration node, Source source) {
    return new FunctionElementHandle(node, source);
  }

  @Override
  public FunctionElementHandle getElementHandle() {
    return (FunctionElementHandle) super.getElementHandle();
  }

  
}
