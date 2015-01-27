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
import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import fr.insalyon.citi.golo.compiler.ir.GoloModule;
import fr.insalyon.citi.golo.compiler.ir.ModuleImport;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gololang.netbeans.api.completion.util.CompletionContext;
import org.gololang.netbeans.parser.GoloParser;
import org.gololang.netbeans.parser.GoloParser.GoloParserResult;
import org.gololang.netbeans.structure.SimpleGoloElementHandle;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementKind;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class NamedAugmentationCompletion {
    
    void complete(List<CompletionProposal> proposals, CompletionContext context, int anchor) {
        String filter = context.getPrefix();
        GoloModule module = ((GoloParser.GoloParserResult)context.getParserResult()).getModule();
        Map<String, Set<GoloFunction>> namedAugmentations = module.getNamedAugmentations();
        
        GoloClassLoader classLoader = context.getGoloClassLoader();

        if (classLoader == null) {
            return;
        }
        FileObject fo = context.getSourceFile();
        GoloParserResult parserResult = (GoloParserResult) context.getParserResult();
        Project owner = FileOwnerQuery.getOwner(fo);
        Map<Class<?>, FileObject> goloSources;
        try {
            goloSources = context.loadGoloFiles(owner.getProjectDirectory().getPath(), classLoader);
            if (goloSources != null) {
                for (Class<?> goloClass : goloSources.keySet()) {
                    FileObject goloFile = goloSources.get(goloClass);
                    if (!goloFile.equals(fo)) {
                        Class<?>[] declaredClasses = goloClass.getDeclaredClasses();
                        for (Class<?> declaredClasse : declaredClasses) {
                            String classSimpleName = declaredClasse.getName();
                            String augmentation = classSimpleName.substring(classSimpleName.lastIndexOf("$") + 1);
                            if (augmentation.toLowerCase().startsWith(filter.toLowerCase())) {
                                boolean isImported = isModuleImported(parserResult.getModule(), goloClass);
                                String proposal = augmentation;
                                if (!isImported) {
                                    proposal = classSimpleName.replace('$', '.');
                                }
                                proposals.add(new CompletionItem.NamedAugmentationItem(new SimpleGoloElementHandle(goloFile, goloClass.getSimpleName(), augmentation, ElementKind.CLASS, null), anchor, proposal));
                            }
                        }
                    }
                }
            }
            
        } catch (Throwable ex) {
        }
        
        for (String namedAugmentation : namedAugmentations.keySet()) {
            if (filter != null && namedAugmentation.toLowerCase().startsWith(filter)) {
                proposals.add(new CompletionItem.NamedAugmentationItem(new SimpleGoloElementHandle(context.getSourceFile(), module.getPackageAndClass().toString(), namedAugmentation, ElementKind.CLASS, null), anchor, namedAugmentation));
            }
        }
    }

    private boolean isModuleImported(GoloModule module, Class<?> clazz) {
        for (ModuleImport moduleImport : module.getImports()) {
            String importClassName = moduleImport.getPackageAndClass().toString();
            if (importClassName.equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
}
