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

package org.gololang.netbeans.editor.bracesmatching;

import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 *
 * @author Julien Déray
 */
@MimeRegistration(mimeType="text/x-golo", service=BracesMatcherFactory.class) 
public class GoloBracesMatcherFactory implements BracesMatcherFactory { 
     private static final char [] DEFAULT_CHARS = new char [] { '(', ')', '[', ']', '{', '}', '|', '|'};

    @Override 
    public BracesMatcher createMatcher(MatcherContext context) { 
        return new CharacterMatcher(context, -1, -1, DEFAULT_CHARS);
    } 

} 