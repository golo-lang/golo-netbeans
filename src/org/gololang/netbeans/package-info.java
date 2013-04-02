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


@TemplateRegistrations(
  {
    @TemplateRegistration(folder = "Golo Examples", iconBase="org/gololang/netbeans/golo_icon_24px.png", content = "templates/helloworld.golo", displayName="Hello World"),
    @TemplateRegistration(folder = "Golo Examples", iconBase="org/gololang/netbeans/golo_icon_24px.png", content = "templates/pimping.golo", displayName="Pimps"),
    @TemplateRegistration(folder = "Golo Examples", iconBase="org/gololang/netbeans/golo_icon_24px.png", content = "templates/closures.golo", displayName="Closures"),
    @TemplateRegistration(folder = "Golo Examples", iconBase="org/gololang/netbeans/golo_icon_24px.png", content = "templates/dynamic-object-person.golo", displayName="Dynamic objects"),
    @TemplateRegistration(folder = "Golo Examples", iconBase="org/gololang/netbeans/golo_icon_24px.png", content = "templates/workers.golo", displayName="Workers"),
    @TemplateRegistration(folder = "Golo Examples", iconBase="org/gololang/netbeans/golo_icon_24px.png", content = "templates/swing-actionlistener.golo", displayName="Swing action listeners"),
    @TemplateRegistration(folder = "Golo Examples", iconBase="org/gololang/netbeans/golo_icon_24px.png", content = "templates/http-server.golo", displayName="Http server")
  })
@ContainerRegistration(id = "Golo", categoryName = "#OptionsCategory_Name_Golo", iconBase = "org/gololang/netbeans/golo_icon_32px.png", keywords = "#OptionsCategory_Keywords_Golo", keywordsCategory = "Golo")
@Messages(value = {"OptionsCategory_Name_Golo=Golo", "OptionsCategory_Keywords_Golo=Golo"})
package org.gololang.netbeans;

import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.netbeans.spi.options.OptionsPanelController.ContainerRegistration;
import org.openide.util.NbBundle.Messages;
