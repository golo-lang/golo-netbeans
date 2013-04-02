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

import fr.insalyon.citi.golo.compiler.parser.ASTFunctionDeclaration;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author David Festal <david.festal@serli.com>
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
