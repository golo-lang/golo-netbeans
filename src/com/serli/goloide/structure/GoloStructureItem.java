/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.serli.goloide.structure;

import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author david
 */
public abstract class GoloStructureItem<NodeType extends GoloASTNode> implements StructureItem {

  private GoloElementHandle handle;
  protected GoloASTNode node;
  protected List<? extends StructureItem> items = new ArrayList<>();
  
  
  public GoloStructureItem(NodeType node, Source source, List<? extends StructureItem> items) {
    this.node = node; 
    this.items = items;
    handle = createHandle(node, source);
  }

  @Override
  public String getName() {
    return handle.getName();
  }

  @Override
  public String getSortText() {
    return getName();
  }

  @Override
  public boolean isLeaf() {
    return items.isEmpty();
  }

  @Override
  public List<? extends StructureItem> getNestedItems() {
    return items;
  }

  @Override
  public ElementHandle getElementHandle() {
    return handle;
  }

  @Override
  public ElementKind getKind() {
    return handle.getKind();
  }

  @Override
  public Set<Modifier> getModifiers() {
    return handle.getModifiers();
  }

  @Override
  public long getPosition() {
    return handle.getOffsetRange(null).getStart();
  }

  @Override
  public long getEndPosition() {
    return handle.getOffsetRange(null).getEnd();
  }

  @Override
  public ImageIcon getCustomIcon() {
    return null;
  }

	@Override
	public int hashCode()
	{
		int hash = 7;

		hash = (29 * hash) + ((getName() != null) ? getName().hashCode() : 0);
		hash = (29 * hash) + (getKind() != null ? getKind().hashCode() : 0);

		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final GoloStructureItem other = (GoloStructureItem) obj;
		if (getKind() != other.getKind() || !getName().equals(other.getName()))
		{
			return false;
		}
		return true;
	}

  public abstract GoloElementHandle createHandle(NodeType node, Source source);
  
  public GoloElementHandle getHandle() {
    return handle;
  }
}
