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

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import java.util.HashSet;
import java.util.Set;
import org.gololang.netbeans.lexer.GoloTokenId;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class SimpleGoloElementHandle implements ElementHandle {

    private final FileObject fileObject;
    private final Set<Modifier> modifiers;
    private final String elementName;
    private final ElementKind elementKind;
    private final String className;

    public SimpleGoloElementHandle(FileObject fileObject, String className, String elementName, ElementKind elementKind, Set<Modifier> modifiers) {
        this.fileObject = fileObject;
        this.elementName = elementName;
        this.modifiers = modifiers;
        this.elementKind = elementKind;
        this.className = className;
    }

    @Override
    public FileObject getFileObject() {
        return fileObject;
    }

    @Override
    public String getMimeType() {
        if (fileObject != null) {
            return fileObject.getMIMEType();
        }
        // default value golo mime type or 'application/java-vm' ?
        return GoloTokenId.getLanguage().mimeType();
    }

    @Override
    public String getName() {
        return elementName;
    }

    @Override
    public String getIn() {
        if (fileObject != null) {
            return fileObject.getName();
        }
        return className;
    }

    @Override
    public ElementKind getKind() {
        return elementKind;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean signatureEquals(ElementHandle handle) {
        return false;
    }

    @Override
    public OffsetRange getOffsetRange(ParserResult result) {
        return OffsetRange.NONE;
    }

    protected static Set<Modifier> toModifier(int modifier) {
        Set<Modifier> modifiers = new HashSet<>();
        if (isStatic(modifier)) {
            modifiers.add(Modifier.STATIC);
        }
        if (isAbstract(modifier)) {
            modifiers.add(Modifier.ABSTRACT);
        }
        if (isPrivate(modifier)) {
            modifiers.add(Modifier.PRIVATE);
        }
        if (isProtected(modifier)) {
            modifiers.add(Modifier.PROTECTED);
        }
        if (isPublic(modifier)) {
            modifiers.add(Modifier.PUBLIC);
        }
        return modifiers;
    }
}
