/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.serli.goloide.structure;

import fr.insalyon.citi.golo.compiler.parser.ASTLetOrVar;
import java.util.List;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author david
 */
public class VariableStructureItem extends GoloStructureItem<ASTLetOrVar> {

  public VariableStructureItem(ASTLetOrVar node, Source source, List<? extends StructureItem> items) {
    super(node, source, items);
  }

  @Override
  public String getHtml(HtmlFormatter hf) {
    return getName();
  }

  @Override
  public GoloElementHandle createHandle(ASTLetOrVar node, Source source) {
    return new VariableElementHandle(node, source);
  }

  @Override
  public VariableElementHandle getElementHandle() {
    return (VariableElementHandle) super.getElementHandle();
  }
}
