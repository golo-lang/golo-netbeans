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
package org.gololang.netbeans.editor.completion;

import fr.insalyon.citi.golo.compiler.ir.AssignmentStatement;
import fr.insalyon.citi.golo.compiler.ir.BinaryOperation;
import fr.insalyon.citi.golo.compiler.ir.ClosureReference;
import fr.insalyon.citi.golo.compiler.ir.ExpressionStatement;
import fr.insalyon.citi.golo.compiler.ir.FunctionInvocation;
import fr.insalyon.citi.golo.compiler.ir.GoloElement;
import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import fr.insalyon.citi.golo.compiler.ir.MethodInvocation;
import fr.insalyon.citi.golo.compiler.parser.ASTLetOrVar;
import fr.insalyon.citi.golo.compiler.parser.GoloASTNode;
import fr.insalyon.citi.golo.compiler.parser.GoloParserConstants;
import fr.insalyon.citi.golo.runtime.MethodInvocationSupport;
import fr.insalyon.citi.golo.runtime.OperatorType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import org.gololang.netbeans.parser.GoloParser;
import static org.gololang.netbeans.project.GoloProject.*;
import org.gololang.netbeans.structure.GoloFunctionElementHandle;
import org.gololang.netbeans.structure.GoloParameterElementHandle;
import org.gololang.netbeans.structure.ImportedFieldElementHandle;
import org.gololang.netbeans.structure.ImportedMethodElementHandle;
import org.gololang.netbeans.structure.KeywordElementHandle;
import org.gololang.netbeans.structure.SimpleGoloElementHandle;
import org.gololang.netbeans.structure.VariableElementHandle;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.spi.DefaultCompletionProposal;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Guillaume Soldera <guillaume.soldera@serli.com>
 */
public class CompletionItem extends DefaultCompletionProposal {

    private ElementHandle element;

    private CompletionItem(ElementHandle element, int anchorOffset) {
        this.element = element;
        this.anchorOffset = anchorOffset;

    }

    @Override
    public String getName() {
        return element.getName();
    }

    @Override
    public ElementHandle getElement() {
        return element;
    }

    @Override
    public ElementKind getKind() {
        return element.getKind();
    }

    @Override
    public Set<Modifier> getModifiers() {
        return element.getModifiers();
    }

    @Override
    public String toString() {
        String cls = getClass().getName();
        cls = cls.substring(cls.lastIndexOf('.') + 1);

        return cls + "(" + getKind() + "): " + getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CompletionItem other = (CompletionItem) obj;
        if ((this.getName() == null) ? (other.getName() != null) : !this.getName().equals(other.getName())) {
            return false;
        }
        if (this.getKind() != other.getKind()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
        hash = 47 * hash + (this.getKind() != null ? this.getKind().hashCode() : 0);
        return hash;
    }

    @Override
    public ImageIcon getIcon() {
        return super.getIcon();
    }

    public static class KeywordItem extends CompletionItem {

        private final String keyword;
        private final String description;
        private final ParserResult info;
        private final int tokenId;

        public KeywordItem(String keyword, int tokenId, String description, int anchorOffset, ParserResult info) {
            super(null, anchorOffset);
            this.keyword = keyword;
            this.description = description;
            this.info = info;
            this.tokenId = tokenId;
        }

        @Override
        public String getName() {
            return keyword;
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.KEYWORD;
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            if (description != null) {
                //formatter.appendText(description);
                formatter.appendHtml(description);

                return formatter.getText();
            } else {
                return null;
            }
        }

        @Override
        public ImageIcon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(GOLO_ICON));
        }

        @Override
        public Set<Modifier> getModifiers() {
            return Collections.emptySet();
        }

        @Override
        public ElementHandle getElement() {
            // For completion documentation
            return new KeywordElementHandle(keyword, info.getSnapshot().getSource());
        }

        @Override
        public String[] getParamListDelimiters() {
            if (tokenId == GoloParserConstants.COLL_START) {
                return new String[]{"[", "]"};
            }
            return super.getParamListDelimiters();
        }

        @Override
        public String getCustomInsertTemplate() {
            if (tokenId == GoloParserConstants.COLL_START) {
                StringBuilder sb = new StringBuilder();
                sb.append(getInsertPrefix());
                String[] delimiters = getParamListDelimiters();
                assert delimiters.length == 2;
                sb.append(delimiters[0]);
                sb.append("${cursor}"); // NOI18N
                sb.append(delimiters[1]);
                return sb.toString();
            }
            return super.getCustomInsertTemplate();

        }
    }

    public static class FunctionItem extends CompletionItem {

        private final GoloFunction function;
        private final String description;
        private final ParserResult info;

        public FunctionItem(GoloFunction function, int anchorOffset, ParserResult info) {
            super(null, anchorOffset);
            this.function = function;
            this.description = ((GoloParser.GoloParserResult) info).getModule().getPackageAndClass().toString();
            this.info = info;

        }

        @Override
        public String getName() {
            return function.getName();
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.METHOD;
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {

            if (description != null) {
                //formatter.appendText(description);
                formatter.appendHtml(description);

                return formatter.getText();
            } else {
                return null;
            }
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            String name = function.getName();
            formatter.emphasis(true);
            formatter.appendText(name + "(" + getParameters(function.getParameterNames()) + ")");
            formatter.emphasis(false);
            return formatter.getText();
        }

        private String getParameters(List<String> parametersName) {
            StringBuilder sb = new StringBuilder();
            if (parametersName != null && parametersName.size() > 0) {
                for (String string : parametersName) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(string);
                }
            }
            return sb.toString();
        }

        @Override
        public ImageIcon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(GOLO_ICON));
        }

        @Override
        public Set<Modifier> getModifiers() {
            Set<Modifier> modifiers = new HashSet<>();
            switch (function.getVisibility()) {
                case LOCAL:
                    modifiers.add(Modifier.PRIVATE);
                case PUBLIC:
                    modifiers.add(Modifier.PUBLIC);
                default:
                    break;
            }
            return modifiers;
        }

        @Override
        public ElementHandle getElement() {
            // For completion documentation
            return new GoloFunctionElementHandle(function, info.getSnapshot().getSource());
        }

        @Override
        public List<String> getInsertParams() {
            return function.getParameterNames();
        }

        @Override
        public String[] getParamListDelimiters() {
            return new String[]{"(", ")"};
        }

        @Override
        public String getCustomInsertTemplate() {
            List<String> params = getInsertParams();
            if (params == null || params.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(getInsertPrefix());
                String[] delimiters = getParamListDelimiters();
                assert delimiters.length == 2;
                sb.append(delimiters[0]);
                sb.append(delimiters[1]);
                sb.append("${cursor}"); // NOI18N

                return sb.toString();
            }
            return super.getCustomInsertTemplate();

        }

        @Override
        public boolean isSmart() {
            return true;
        }

    }

    public static class SimpleElementItem extends CompletionItem {

        public SimpleElementItem(SimpleGoloElementHandle element, int anchorOffset) {
            super(element, anchorOffset);
        }

        @Override
        public ImageIcon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(GOLO_ICON));
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            SimpleGoloElementHandle element = (SimpleGoloElementHandle) getElement();
            formatter.appendText(element.getFromClassName());
            return formatter.getText();
        }

    }

    public static class SimpleFieldElementItem extends SimpleElementItem {

        private final boolean isGoloElement;
        private final ImportedFieldElementHandle importedField;

        public SimpleFieldElementItem(ImportedFieldElementHandle elementHandle, int anchorOffset, boolean isGoloElement) {
            super(elementHandle, anchorOffset);
            this.isGoloElement = isGoloElement;
            this.importedField = elementHandle;
        }

        @Override
        public ImageIcon getIcon() {
            if (isGoloElement) {
                return super.getIcon();
            }

            Set<Modifier> modifiers = importedField.getModifiers();
            if (modifiers.contains(Modifier.STATIC)) {
                return new ImageIcon(ImageUtilities.loadImage(JAVA_STATIC_FIELD_ICON));
            }
            return new ImageIcon(ImageUtilities.loadImage(JAVA_FIELD_ICON));
        }

        @Override
        public boolean isSmart() {
            return isGoloElement;
        }
    }

    public static class SimpleParameterElementItem extends SimpleElementItem {

        public SimpleParameterElementItem(GoloParameterElementHandle elementHandle, int anchorOffset) {
            super(elementHandle, anchorOffset);
        }

        @Override
        public ImageIcon getIcon() {
            return super.getIcon();
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            formatter.emphasis(true);
            formatter.appendText("|");
            formatter.appendText(getName());
            formatter.appendText("|");
            formatter.emphasis(false);
            return formatter.getText();
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            GoloParameterElementHandle element = (GoloParameterElementHandle) getElement();
            formatter.appendText(element.getFromClassName());
            formatter.appendText(".");
            formatter.appendHtml("<i>");
            formatter.appendText(element.getFunctionName());
            formatter.appendHtml("</i>");
            return formatter.getText();
        }

        @Override
        public boolean isSmart() {
            return true;
        }
    }

    public static class VariableElementItem extends CompletionItem {
        private final String moduleName;
        private final String functionName;

        public VariableElementItem(VariableElementHandle elementHandle, int anchorOffset, String moduleName, String functionName) {
            super(elementHandle, anchorOffset);
            this.moduleName = moduleName;
            this.functionName = functionName;
        }

        @Override
        public ImageIcon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(GOLO_ICON));
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            formatter.emphasis(true);
            ASTLetOrVar.Type type = getNode().getType();
            String prefixHtml = "";
            String suffixHtml = "";
            if (type == ASTLetOrVar.Type.LET) {
                prefixHtml = "<i>";
                suffixHtml = "</i>";
            }
            formatter.appendHtml(prefixHtml);
            formatter.appendText(getName());
            formatter.appendHtml(suffixHtml);
            formatter.emphasis(false);
            return formatter.getText();
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            formatter.appendText(moduleName);
            formatter.appendText(".");
            formatter.appendHtml("<i>");
            formatter.appendText(functionName);
            formatter.appendHtml("</i>");
            return formatter.getText();
        }

        private ASTLetOrVar getNode() {
            return (ASTLetOrVar) ((VariableElementHandle) getElement()).getNode();
        }

        @Override
        public boolean isSmart() {
            return true;
        }
    }

    public static class SimpleMethodElementItem extends SimpleElementItem {

        private static final List<String> VOWEL_LIST = Arrays.asList("a", "e", "i", "o", "u");

        private final boolean isGoloElement;
        private final ImportedMethodElementHandle importedMethod;

        public SimpleMethodElementItem(ImportedMethodElementHandle elementHandle, int anchorOffset, boolean isGoloElement) {
            super(elementHandle, anchorOffset);
            this.isGoloElement = isGoloElement;
            this.importedMethod = (ImportedMethodElementHandle) getElement();
        }

        @Override
        public ImageIcon getIcon() {
            if (isGoloElement) {
                return super.getIcon();
            }

            Set<Modifier> modifiers = importedMethod.getModifiers();
            if (modifiers.contains(Modifier.STATIC)) {
                return new ImageIcon(ImageUtilities.loadImage(JAVA_STATIC_METHOD_ICON));
            }
            return new ImageIcon(ImageUtilities.loadImage(JAVA_METHOD_ICON));
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            String name = importedMethod.getMethod().getName();
            if (importedMethod.isFromPredifinedGoloClass()) {
                formatter.appendHtml("<i>");
                formatter.emphasis(true);
            }
            formatter.appendText(name + "(" + getParameters(importedMethod.getMethod().getParameterTypes()) + ")");
            if (importedMethod.isFromPredifinedGoloClass()) {
                formatter.emphasis(false);
                formatter.appendHtml("</i>");
            }
            return formatter.getText();
        }

        private String getParameters(Class<?>[] parameterTypes) {
            StringBuilder sb = new StringBuilder();
            if (parameterTypes != null) {
                for (Class<?> clazz : parameterTypes) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    final String simpleName = clazz.getSimpleName();
                    sb.append(simpleName);
                    sb.append(" ");
                    sb.append(simpleName.toLowerCase().charAt(0));
                }
            }
            return sb.toString();
        }

        @Override
        public List<String> getInsertParams() {
            Class<?>[] parameterTypes = importedMethod.getMethod().getParameterTypes();
            if (parameterTypes == null || parameterTypes.length == 0) {
                return Collections.emptyList();
            }
            List<String> params = new ArrayList<>(parameterTypes.length);
            for (Class<?> parameterType : parameterTypes) {
                params.add(getParameterName(parameterType));
            }
            return params;
        }

        private String getParameterName(Class<?> clazz) {
            StringBuilder parameterName = new StringBuilder();
            String simpleName = clazz.getSimpleName();
            String prefix = "";
            String suffix = "";
            String firstLetter = simpleName.toLowerCase().substring(0, 1);
            if (VOWEL_LIST.contains(firstLetter)) {
                prefix = "an";
            } else {
                prefix = "a";
            }
            if (clazz.isArray()) {
                // remove '[]'
                simpleName = simpleName.substring(0, simpleName.length() - 2);
                suffix = "Array";
            }
            return parameterName.append(prefix).append(simpleName).append(suffix).toString();
        }

        @Override
        public String[] getParamListDelimiters() {
            return new String[]{"(", ")"};
        }

        @Override
        public String getCustomInsertTemplate() {
            List<String> params = getInsertParams();
            if (params == null || params.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(getInsertPrefix());
                String[] delimiters = getParamListDelimiters();
                assert delimiters.length == 2;
                sb.append(delimiters[0]);
                sb.append(delimiters[1]);
                sb.append("${cursor}"); // NOI18N

                return sb.toString();
            }
            return super.getCustomInsertTemplate();

        }

        @Override
        public boolean isSmart() {
            return isGoloElement;
        }
    }

}
