/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.serli.goloide.structure;

import fr.insalyon.citi.golo.compiler.parser.ASTPimpDeclaration;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author david
 */
public final class PimpElementHandle extends GoloElementHandle {

  public PimpElementHandle(ASTPimpDeclaration node, Source source) {
    super(node, source);
  }

  @Override
  public ElementKind getKind() {
    return ElementKind.CLASS;
  }
}
