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

package org.gololang.netbeans.project;

import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Julien Déray
 */
public class GoloProject implements Project {

    private final FileObject projectDir;
    private final ProjectState state;
    private Lookup lkp;
    
    public static final String GOLO_ICON = "org/gololang/netbeans/golo_icon_16px.png";
    
    GoloProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
        this.state = state;
        
        Icon icon = new ImageIcon(ImageUtilities.loadImage(GOLO_ICON));
        GenericSources.group(this, dir, "golo", "golo", icon, icon);
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
            
                new Info(),
                new GoloSources()
            
            });
        }
        return lkp;
    }

    public final class Info implements ProjectInformation {
    
        @StaticResource()
        public static final String GOLO_ICON = "org/gololang/netbeans/golo_icon_16px.png";

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(GOLO_ICON));
        }

        @Override
        public String getName() {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public Project getProject() {
            return GoloProject.this;
        }
    }
    
    public class GoloSources implements Sources {
    
        static final String TYPE_GENERIC = "golo";    

        @Override
        public SourceGroup[] getSourceGroups(String string) {
            return new SourceGroup[]{
                new SourceGroup() {

                @StaticResource()
                public static final String GOLO_ICON = "org/gololang/netbeans/golo_icon_16px.png";
                    
                @Override
                public FileObject getRootFolder() {
                    return getProjectDirectory();
                }

                @Override
                public String getName() {
                    return "golo";
                }

                @Override
                public String getDisplayName() {
                    return "Golo";
                }

                @Override
                public Icon getIcon(boolean bln) {
                    return new ImageIcon(ImageUtilities.loadImage(GOLO_ICON));
                }

                @Override
                public boolean contains(FileObject comparedFo) {
                    for ( FileObject fo : getRootFolder().getChildren() ) {
                        if ( fo == comparedFo ) {
                            return true;
                        }    
                    }
                    return false;
                }

                @Override
                public void addPropertyChangeListener(PropertyChangeListener pl) {
                }

                @Override
                public void removePropertyChangeListener(PropertyChangeListener pl) {
                }
                    
                }
            };
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }
    }
}
