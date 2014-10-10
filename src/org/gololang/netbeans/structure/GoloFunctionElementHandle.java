/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gololang.netbeans.structure;

import fr.insalyon.citi.golo.compiler.ir.GoloFunction;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 * @author guillaume
 */
public class GoloFunctionElementHandle extends GoloElementHandle{
    private final GoloFunction goloFunction;
    
    public GoloFunctionElementHandle(GoloFunction goloFunction, Source source) {
        super(goloFunction.getASTNode(), source);
        this.goloFunction = goloFunction;
    }

    @Override
    public Set<Modifier> getModifiers() {
        Set<Modifier> modifiers = new HashSet<>();
        switch (goloFunction.getVisibility()) {
            case LOCAL: modifiers.add(Modifier.PRIVATE);
                break;
            case PUBLIC: modifiers.add(Modifier.PUBLIC);
                break;
            default: break;
        }
        return modifiers;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.METHOD;
    }

}
