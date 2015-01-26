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
import fr.insalyon.citi.golo.compiler.ir.ModuleImport;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gololang.netbeans.RunGoloProject;
import org.gololang.netbeans.api.completion.util.CompletionContext;
import org.gololang.netbeans.parser.GoloParser.GoloParserResult;
import org.gololang.netbeans.structure.ImportedFieldElementHandle;
import org.gololang.netbeans.structure.ImportedMethodElementHandle;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class ImportCompletion {

    void complete(List<CompletionProposal> proposals, CompletionContext context, int anchor) {
        GoloParserResult parserResult = (GoloParserResult) context.getParserResult();
        String filter = context.getPrefix();

        FileObject fo = context.getSourceFile();

        Project owner = FileOwnerQuery.getOwner(fo);
        String rootDir = RunGoloProject.getGoloRootDir();
        List<String> classpath = new ArrayList<>();
        classpath.add(".");
        File libDirFile = new File(rootDir, "lib");
        for (File jarFile : libDirFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        })) {
            classpath.add(jarFile.getAbsolutePath());
        }

        GoloClassLoader classLoader = context.getGoloClassLoader(classpath);

        if (classLoader == null) {
            return;
        }

        Map<Class<?>, FileObject> goloSources;
        try {
            goloSources = context.loadGoloFiles(owner.getProjectDirectory().getPath(), classLoader);
            for (ModuleImport moduleImport : parserResult.getModule().getImports()) {
//                if (moduleImport.hasASTNode()) {

                String importClassName = moduleImport.getPackageAndClass().toString();
                Class<?> importClass;
                try {
                    importClass = Class.forName(importClassName, true, classLoader);
                    final FileObject goloFile = goloSources.get(importClass);
                    boolean isGoloElement = goloFile != null;
                    Method[] declaredMethods = importClass.getDeclaredMethods();
                    for (Method method : declaredMethods) {
                        if (!method.isSynthetic()) {
                            if (!isPrivate(method.getModifiers()) && isStatic(method.getModifiers())) {
                                if (method.getName().toLowerCase().startsWith(filter.toLowerCase())) {
                                    proposals.add(new CompletionItem.SimpleMethodElementItem(new ImportedMethodElementHandle(goloFile, importClassName, method), anchor, isGoloElement));
                                }
                            }
                        }
                    }
                    Field[] declaredFields = importClass.getDeclaredFields();
                    for (Field field : declaredFields) {
                        if (!field.isSynthetic()) {
                            if (isPublic(field.getModifiers()) && isStatic(field.getModifiers())) {
                                if (field.getName().toLowerCase().startsWith(filter.toLowerCase())) {
                                    proposals.add(new CompletionItem.SimpleFieldElementItem(new ImportedFieldElementHandle(goloFile, importClassName, field), anchor, isGoloElement));
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException expected) {
//                        Exceptions.printStackTrace(expected);
                }
//                }
            }
        } catch (Throwable ex) {
//            Exceptions.printStackTrace(ex);
        }
    }

    private static String[] imports(Class<?> callerClass) {
        String[] imports;
        try {
            Method $imports = callerClass.getMethod("$imports");
            imports = (String[]) $imports.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // This can only happen as part of the unit tests, because the lookup does not originate from
            // a Golo module class, hence it doesn't have a $imports() static method.
            imports = new String[]{};
        }
        return imports;
    }

}
