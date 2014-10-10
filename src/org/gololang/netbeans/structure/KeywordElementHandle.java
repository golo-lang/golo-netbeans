/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gololang.netbeans.structure;

import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author guillaume
 */
public class KeywordElementHandle extends GoloElementHandle {
    private final String name;
    
    public KeywordElementHandle(String name, Source source) {
        super(null, source);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.KEYWORD;
    }

    @Override
    public String getIn() {
        return null;
    }
    
    @Override
    public OffsetRange getOffsetRange(ParserResult result) {
        return OffsetRange.NONE;
    }
    
}
