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

package org.gololang.netbeans.project.nodes;

import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author Julien Déray
 */
public class ProxyChildren extends FilterNode.Children {

    public ProxyChildren(Node owner) {
        super(owner);
    }

    @Override
    protected Node[] createNodes(Node object) {
        List<Node> result = new ArrayList<>();
        for (Node node : super.createNodes(object)) {
            if (accept(node)) {
                result.add(node);
            }
        }
        return result.toArray(new Node[0]);
    }

    private boolean accept(Node node) {
        return !node.getName().equals("golo.project");
    }
    
}
