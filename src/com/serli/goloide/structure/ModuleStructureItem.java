/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.serli.goloide.structure;

import fr.insalyon.citi.golo.compiler.parser.ASTFunctionDeclaration;
import fr.insalyon.citi.golo.compiler.parser.ASTModuleDeclaration;
import java.util.List;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author david
 */
public class ModuleStructureItem extends GoloStructureItem<ASTModuleDeclaration> {

  public ModuleStructureItem(ASTModuleDeclaration node, Source source, List<? extends StructureItem> items) {
    super(node, source, items);
  }

  @Override
  public String getHtml(HtmlFormatter hf) {
    return getName();
  }

  @Override
  public GoloElementHandle createHandle(ASTModuleDeclaration node, Source source) {
    return new ModuleElementHandle(node, source);
  }

  @Override
  public ModuleElementHandle getElementHandle() {
    return (ModuleElementHandle) super.getElementHandle();
  }

  
}
