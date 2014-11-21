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

import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import fr.insalyon.citi.golo.compiler.parser.NamedNode;
import java.util.Collections;
import java.util.Set;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Source;
import org.openide.filesystems.FileObject;

/**
 *
 * @author David Festal <david.festal@serli.com>
 */
public abstract class GoloElementHandle implements ElementHandle {

    protected GoloASTNode node;
    protected Source source;

    public GoloElementHandle(GoloASTNode node, Source source) {
//    assert(node instanceof NamedNode);
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

    public GoloASTNode getNode() {
        return node;
    }

}
