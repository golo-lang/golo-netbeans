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
package org.gololang.netbeans.api.completion.util;

import fr.insalyon.citi.golo.compiler.GoloClassLoader;
import fr.insalyon.citi.golo.compiler.GoloCompilationException;
import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import fr.insalyon.citi.golo.compiler.ir.GoloModule;
import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import fr.insalyon.citi.golo.compiler.parser.GoloASTUtils;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gololang.netbeans.parser.GoloParser;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * 
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class CompletionContext {
    
    private final ParserResult parserResult;
    private final FileObject sourceFile;
    
    private final String prefix;
    private int anchor;
    
    public final int lexOffset;
    public final BaseDocument doc;
   
    
    public CompletionContext(
            ParserResult parseResult,
            String prefix,
            int anchor,
            int lexOffset,
            BaseDocument doc) {

        this.parserResult = parseResult;
        this.sourceFile = parseResult.getSnapshot().getSource().getFileObject();
        this.prefix = prefix;
        this.anchor = anchor;
        this.lexOffset = lexOffset;
        this.doc = doc;
        
    }

    public String getPrefix() {
        return prefix;
    }

    public int getAnchor() {
        return anchor;
    }

    public ParserResult getParserResult() {
        return parserResult;
    }

    public FileObject getSourceFile() {
        return sourceFile;
    }
    
    public boolean isAnchorInFunction() {
        return isAnchorInFunction(anchor);
    }
    
    public boolean isAnchorInFunction(int anAnchor) {
        GoloModule module = ((GoloParser.GoloParserResult) parserResult).getModule();

        Set<GoloFunction> functions = module.getFunctions();
        for (GoloFunction fn : functions) {
            if (!fn.getName().startsWith("__$$_") && fn.hasASTNode()) {
                GoloASTNode astNode = fn.getASTNode();
                OffsetRange range = GoloASTUtils.getRange(astNode, doc);
                if (range.containsInclusive(anAnchor)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public List<File> getGoloFiles(FileObject sourceFile) {
        Project owner = FileOwnerQuery.getOwner(sourceFile);
        return getGoloFiles(owner.getProjectDirectory().getPath());
    }
    
    public List<File> getGoloFiles(String dir) {
         List<File> goloFiles = new ArrayList<>();
        File file = new File(dir);
        if (!file.exists()) {
            System.out.println("Error: " + file.getAbsolutePath() + " does not exist.");
        } else if (file.isDirectory()) {
            File[] directoryFiles = file.listFiles();
            if (directoryFiles != null) {
                for (File directoryFile : directoryFiles) {
                    goloFiles.addAll(getGoloFiles(directoryFile.getAbsolutePath()));
                }
            }
        } else if (file.getName().endsWith(".golo")) {
            goloFiles.add(file);
        }
        return goloFiles;
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
    
    public GoloClassLoader getGoloClassLoader() {
        List<String> classpath = new ArrayList<>();
        classpath.add(".");
        return getGoloClassLoader(classpath);
    }
    
    public GoloClassLoader getGoloClassLoader(List<String> classpath) {
        GoloClassLoader classLoader = null;
        try {
            URLClassLoader primaryClassLoader;
            primaryClassLoader = primaryClassLoader(classpath);
            classLoader = new GoloClassLoader(primaryClassLoader);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return classLoader;
    }
    
    public Map<Class<?>, FileObject> loadGoloFiles(FileObject sourceFile, GoloClassLoader loader) throws Throwable {
        Project owner = FileOwnerQuery.getOwner(sourceFile);
        return loadGoloFiles(owner.getProjectDirectory().getPath(), loader);
    }
    
    public Map<Class<?>, FileObject> loadGoloFiles(String goloFile, GoloClassLoader loader) throws Throwable {
        Map<Class<?>, FileObject> result = new HashMap<>();
        List<File> goloFiles = getGoloFiles(goloFile);
        if (goloFiles != null) {
            for (File file : goloFiles) {
                try (FileInputStream in = new FileInputStream(file)) {
                    Class<?> loadedClass = loader.load(file.getName(), in);
                    result.put(loadedClass, FileUtil.toFileObject(file));
                } catch (GoloCompilationException e) {
                }
            }
        }
        
        return result;
    }
}
