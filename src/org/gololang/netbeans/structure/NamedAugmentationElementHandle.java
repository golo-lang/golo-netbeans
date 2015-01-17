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

import fr.insalyon.citi.golo.compiler.parser.ASTNamedAugmentationDeclaration;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public final class NamedAugmentationElementHandle extends GoloElementHandle {

  public NamedAugmentationElementHandle(ASTNamedAugmentationDeclaration node, Source source) {
    super(node, source);
  }

  @Override
  public ElementKind getKind() {
    return ElementKind.CLASS;
  }
}