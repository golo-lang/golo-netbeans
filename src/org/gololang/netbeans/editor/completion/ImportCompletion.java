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
import fr.insalyon.citi.golo.compiler.GoloCompilationException;
import fr.insalyon.citi.golo.compiler.ir.ModuleImport;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gololang.netbeans.api.completion.util.CompletionContext;
import org.gololang.netbeans.parser.GoloParser.GoloParserResult;
import org.gololang.netbeans.structure.SimpleGoloElementHandle;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class ImportCompletion {

    void complete(List<CompletionProposal> proposals, CompletionContext context) {
        GoloParserResult parserResult = (GoloParserResult) context.getParserResult();
        String filter = context.getPrefix();
        List<String> classpath = Arrays.asList(".");
        FileObject fo = context.getSourceFile();

        Project owner = FileOwnerQuery.getOwner(fo);

        Sources sources = ProjectUtils.getSources(owner);
        ProjectInformation information = ProjectUtils.getInformation(owner);
        GoloClassLoader classLoader = null;//cp.getClassLoader(true);
        try {
            URLClassLoader primaryClassLoader;
            primaryClassLoader = primaryClassLoader(classpath);
            classLoader = new GoloClassLoader(primaryClassLoader);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (classLoader == null) {
            return;
        }

        Map<Class<?>, FileObject> goloSources;
        try {
            goloSources = loadGoloFiles(owner.getProjectDirectory().getPath(), classLoader);
            for (ModuleImport moduleImport : parserResult.getModule().getImports()) {
                if (moduleImport.hasASTNode()) {

                    String importClassName = moduleImport.getPackageAndClass().toString();
                    Class<?> importClass;
                    try {
                        importClass = Class.forName(importClassName, true, classLoader);
                        final FileObject goloFile = goloSources.get(importClass);
                        Method[] declaredMethods = importClass.getDeclaredMethods();
                        for (Method method : declaredMethods) {
                            if (!method.isSynthetic()) {
                                if (isStatic(method.getModifiers())) {
                                    if (method.getName().toLowerCase().startsWith(filter.toLowerCase())) {
                                        Set<Modifier> modifiers = new HashSet<>();
                                        modifiers.add(toModifier(method.getModifiers()));

                                        proposals.add(new CompletionItem.SimpleElementItem(new SimpleGoloElementHandle(goloFile, method.getName(), ElementKind.METHOD, modifiers), -1));
                                    }
                                }
                            }
                        }
                        Field[] declaredFields = importClass.getDeclaredFields();
                        for (Field field : declaredFields) {
                            if (!field.isSynthetic()) {
                                if (isStatic(field.getModifiers())) {
                                    if (field.getName().toLowerCase().startsWith(filter.toLowerCase())) {
                                        Set<Modifier> modifiers = new HashSet<>();
                                        modifiers.add(toModifier(field.getModifiers()));

                                        proposals.add(new CompletionItem.SimpleElementItem(new SimpleGoloElementHandle(goloFile, field.getName(), ElementKind.FIELD, modifiers), -1));
                                    }
                                }
                            }
                        }
                    } catch (ClassNotFoundException expected) {
                        Exceptions.printStackTrace(expected);
                    }
                }
            }
        } catch (Throwable ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static Modifier toModifier(int modifier) {
        if (isStatic(modifier)) {
            return Modifier.STATIC;
        }
        if (isAbstract(modifier)) {
            return Modifier.ABSTRACT;
        }
        if (isPrivate(modifier)) {
            return Modifier.PRIVATE;
        }
        if (isProtected(modifier)) {
            return Modifier.PROTECTED;
        }
        if (isPublic(modifier)) {
            return Modifier.PUBLIC;
        }
        return null;
    }

    private static URLClassLoader primaryClassLoader(List<String> classpath) throws MalformedURLException {
        URL[] urls = new URL[classpath.size()];
        int index = 0;
        for (String element : classpath) {
            urls[index] = new File(element).toURI().toURL();
            index = index + 1;
        }
        return new URLClassLoader(urls);
    }

    private static Map<Class<?>, FileObject> loadGoloFiles(String goloFile, GoloClassLoader loader) throws Throwable {
        Map<Class<?>, FileObject> result = new HashMap<>();
        File file = new File(goloFile);
        if (!file.exists()) {
            System.out.println("Error: " + file.getAbsolutePath() + " does not exist.");
        } else if (file.isDirectory()) {
            File[] directoryFiles = file.listFiles();
            if (directoryFiles != null) {
                for (File directoryFile : directoryFiles) {
                    result.putAll(loadGoloFiles(directoryFile.getAbsolutePath(), loader));
                }
            }
        } else if (file.getName().endsWith(".golo")) {
            try (FileInputStream in = new FileInputStream(file)) {
                Class<?> loadedClass = loader.load(file.getName(), in);
                result.put(loadedClass, FileUtil.toFileObject(file));
            } catch (GoloCompilationException e) {
                Exceptions.printStackTrace(e);
            }
        }
        return result;
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

//    private GoloModule parse() {
//        final Reader reader = new StringReader(snapshot.getText().toString());
//        FileObject file = snapshot.getSource().getFileObject();
//        String fileDisplayName = file == null ? "source code" : FileUtil.getFileDisplayName(file);
//        GoloCompiler compiler = new GoloParser.InternalGoloCompiler();
//        compiler.setExceptionBuilder(new GoloCompilationException.Builder(fileDisplayName));
//        ASTCompilationUnit compilationUnit = compiler.parse(fileDisplayName, compiler.initParser(reader));
//        return compiler.check(compilationUnit);
//    }
}
