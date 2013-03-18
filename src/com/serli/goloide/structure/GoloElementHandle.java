/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.serli.goloide.structure;

import fr.insalyon.citi.golo.compiler.PackageAndClass;
import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import fr.insalyon.citi.golo.compiler.ir.GoloModule;
import fr.insalyon.citi.golo.compiler.parser.ASTFunctionDeclaration;
import fr.insalyon.citi.golo.compiler.parser.ASTLetOrVar;
import fr.insalyon.citi.golo.compiler.parser.ASTModuleDeclaration;
import fr.insalyon.citi.golo.compiler.parser.ASTPimpDeclaration;
import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import fr.insalyon.citi.golo.compiler.parser.NamedNode;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Source;
import org.openide.filesystems.FileObject;

/**
 *
 * @author david
 */
public abstract class GoloElementHandle implements ElementHandle {

  protected GoloASTNode node;
  protected Source source;

  public GoloElementHandle(GoloASTNode node, Source source) {
    assert(node instanceof NamedNode);
    this.node = node;
    this.source = source;
  }

  @Override
  public FileObject getFileObject() {
    return source.getFileObject();
  }

  @Override
  public String getMimeType() {
    return source.getMimeType();
  }

  @Override
  public String getName() {
    if (node instanceof NamedNode) {
      return ((NamedNode) node).getName();
    }
    return "";
  }

  @Override
  public String getIn() {
    GoloASTNode parent = (GoloASTNode) node.jjtGetParent();
    while (parent != null) {
      if (parent instanceof NamedNode) {
        return ((NamedNode) parent).getName();
      }
      parent = (GoloASTNode) parent.jjtGetParent();
    };
    return "";
  }

  @Override
  public Set<Modifier> getModifiers() {
    return Collections.emptySet();
  }

  @Override
  public boolean signatureEquals(ElementHandle handle) {
    return false;
  }

  @Override
  public OffsetRange getOffsetRange(ParserResult result) {
    return new OffsetRange(node.jjtGetFirstToken().startOffset, node.jjtGetLastToken().endOffset);
  }
}
