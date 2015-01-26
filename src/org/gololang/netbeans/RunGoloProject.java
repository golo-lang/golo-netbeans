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

package org.gololang.netbeans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

@ActionID(
        category = "Golo",
        id = "org.gololang.netbeans.RunGoloProject"
)
@ActionRegistration(
        iconBase = "org/gololang/netbeans/golo_icon_16px.png",
        displayName = "#CTL_RunGoloProject"
)
@ActionReferences({
    @ActionReference(path = "Menu/BuildProject", position = -90),
    @ActionReference(path = "Toolbars/Build", position = 100),
    @ActionReference(path = "Editors/text/x-golo/Popup", position = 1300),
    @ActionReference(path = "Projects/package/Actions", position = 500),
    @ActionReference(path = "Loaders/text/x-golo/Actions", position = 500)
})
@Messages("CTL_RunGoloProject=Run Golo Project")
public final class RunGoloProject implements ActionListener {

  private final DataObject context;
    
  public RunGoloProject(DataObject context) {
    this.context = context;
  }

  public void execute(final String javaExecutable, final String goloRootDir, final SourceGroup[] srcs) throws InterruptedException, ExecutionException {
      Callable processCallable = new Callable() {

          @Override
          public Process call() throws IOException {
            File libDirFile = new File(goloRootDir, "lib");
            String classpath = "";
            for (File jarFile : libDirFile.listFiles(new FilenameFilter() {
              @Override
              public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
              }
            })) {
              if (!classpath.isEmpty()) {
                classpath += File.pathSeparator;
              }
              classpath += jarFile.getAbsolutePath();
            }
            
            List<File> modules = new ArrayList<>();
            String mainModule = null;
            
            for ( SourceGroup src : srcs ) {
                modules.addAll(findGoloScripts(src.getRootFolder().getPath(), new ArrayList<File>()));
                mainModule = findMainModule(new FileObject[] {src.getRootFolder()});            
            }
            
            if ( mainModule == null ) {
                JOptionPane.showMessageDialog(null, "Please create a main method", "Golo Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
              
            String[] args = buildArguments(
                modules, 
                mainModule, 
                javaExecutable,
                "-server",
                "-Xms1024M",
                "-Xmx1024M",
                "-XX:-TieredCompilation",
                "-classpath",
                classpath,
                "-Dapp.name=gologolo",
                "fr.insalyon.citi.golo.cli.Main",
                "golo",
                "--files"
            );
            
            return new ProcessBuilder(args).start();
          }
 
          private String[] buildArguments(List<File> modules, String mainModule, String ... args) {
              List<String> arguments = new ArrayList<>();
              
              arguments.addAll(Arrays.asList(args));
              
              for ( File module : modules ) {
                  if ( !module.getPath().equals(mainModule) ) {
                      arguments.add(module.getAbsolutePath());
                  }
              }
              
              arguments.add( mainModule );
              
              return (String[]) arguments.toArray(new String[arguments.size()]);
          }          
          
          private String findMainModule(FileObject[] modules) throws FileNotFoundException, IOException {             
              for ( FileObject sg : modules ) {
                  if ( sg.isFolder() ) {
                      String res = findMainModule( sg.getChildren() );
                      if ( res != null ) {
                          return res;
                      }
                  }
                  else {
                      for ( String line : sg.asLines() ) {
                          if ( line.contains(" main ") ) {
                              if ( line.contains("function") ) {
                                  return sg.getPath();
                              }
                          }
                      }
                  }
              }    
              return null;
          }
          
          private List<File> findGoloScripts(String path, List<File> modules) {
            File root = new File( path );
            File[] list = root.listFiles();

            for ( File f : list ) {
                if ( f.isDirectory() ) {
                    findGoloScripts(f.getAbsolutePath(), modules);
                }
                else {
                    if( f.getAbsoluteFile().getName().endsWith(".golo") ) {
                        modules.add(f);
                    }
                }
            }

            return modules;
        }
      };

      ExecutionDescriptor descriptor = new ExecutionDescriptor().frontWindow(true).controllable(true);

      ExecutionService service = ExecutionService.newService(processCallable,
              descriptor, "gologolo command");

      Future<Integer> task = service.run();
      
      try {
          task.get();
      }
      catch(InterruptedException | ExecutionException e) {
          JOptionPane.showMessageDialog(null, "Golo could not execute and returned the following message :\n" + e, "Golo Error", JOptionPane.ERROR_MESSAGE);
      }
      
      return;
    }
  
    public static String getGoloRootDir() {
        return NbPreferences.forModule(RunPanel.class).get("rootDir", "");
    }
  
    @Override
    public void actionPerformed(ActionEvent e) {        
        FileObject selectedFile = context.getPrimaryFile();
        FileObject projectFolder = FileOwnerQuery.getOwner(selectedFile).getProjectDirectory();        
        
        SourceGroup[] sg;
                
        try {
            final Project p = ProjectManager.getDefault().findProject(projectFolder);            
            final Sources sources = ProjectUtils.getSources(p);
            sg = sources.getSourceGroups("golo");
        } catch (IOException | IllegalArgumentException ex) {
            sg = new SourceGroup[0];
        }
        
        String rootDir = getGoloRootDir();
        if (rootDir.isEmpty()) {
          JOptionPane.showMessageDialog(null, "Please set the Golo distribution root directory\nin Tools -> Options -> Golo", "Golo Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
          String javaExecutable = FileUtil.toFile(JavaPlatform.getDefault().findTool("java")).getAbsolutePath();
          try {
            execute(javaExecutable, rootDir, sg);
          } catch (ExecutionException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
          }
        }
    }
}
