/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.serli.goloide.structure;

import fr.insalyon.citi.golo.compiler.parser.ASTFunctionDeclaration;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author david
 */
public final class FunctionElementHandle extends GoloElementHandle {

  public FunctionElementHandle(ASTFunctionDeclaration node, Source source) {
    super(node, source);
  }

  @Override
  public ElementKind getKind() {
    return ElementKind.METHOD;
  }
  
  @Override
  public Set<Modifier> getModifiers() {
    Set<Modifier> modifiers = new HashSet<>();
    ASTFunctionDeclaration decl = (ASTFunctionDeclaration) node;
    if (decl.isLocal()) {
      modifiers.add(Modifier.PRIVATE);
    }
    else {
      modifiers.add(Modifier.PUBLIC);
    }
    return modifiers;
  }
}
