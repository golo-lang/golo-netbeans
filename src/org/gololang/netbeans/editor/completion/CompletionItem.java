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

import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import java.util.Collections;
import java.util.Set;
import javax.swing.ImageIcon;
import static org.gololang.netbeans.project.GoloProject.GOLO_ICON;
import org.gololang.netbeans.structure.GoloFunctionElementHandle;
import org.gololang.netbeans.structure.KeywordElementHandle;
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
        return null;
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

        public KeywordItem(String keyword, String description, int anchorOffset, ParserResult info) {
            super(null, anchorOffset);
            this.keyword = keyword;
            this.description = description;
            this.info = info;
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
    }
    
    public static class FunctionItem extends CompletionItem {

        private final GoloFunction function;
        private final String description;
        private final ParserResult info;

        public FunctionItem(GoloFunction function, int anchorOffset, ParserResult info) {
            super(null, anchorOffset);
            this.function = function;
            this.description = "";
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
            return new GoloFunctionElementHandle(function, info.getSnapshot().getSource());
        }
    }
    
    public static class SimpleElementItem extends CompletionItem {

        public SimpleElementItem(ElementHandle element, int anchorOffset) {
            super(element, anchorOffset);
        }
        
        @Override
        public ImageIcon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(GOLO_ICON));
        }
    }
    
}
