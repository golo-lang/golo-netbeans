/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
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
package org.gololang.netbeans.editor.completion;

import fr.insalyon.citi.golo.compiler.GoloClassLoader;
import java.util.List;
import java.util.Map;
import org.gololang.netbeans.api.completion.util.CompletionContext;
import org.gololang.netbeans.structure.SimpleGoloElementHandle;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementKind;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class ModuleCompletion {
    
    void complete(List<CompletionProposal> proposals, CompletionContext context, int anchor) {
        String filter = context.getPrefix();
        GoloClassLoader classLoader = context.getGoloClassLoader();

        if (classLoader == null) {
            return;
        }

        Map<Class<?>, FileObject> goloSources = null;
        try {
            goloSources = context.loadGoloFiles(context.getSourceFile(), classLoader);
        } catch (Throwable t) {
        }
        if (goloSources != null) {
            for (Class<?> keySet : goloSources.keySet()) {
                FileObject goloFile = goloSources.get(keySet);
                if (goloFile != context.getSourceFile()) {
                    if (goloFile.getName().toLowerCase().startsWith(filter.toLowerCase())) {
                        proposals.add(new CompletionItem.ModuleItem(new SimpleGoloElementHandle(goloFile, goloFile.getNameExt(), keySet.getSimpleName(), ElementKind.MODULE, null), anchor));
                    }
                }
            }
        }
    }
}
