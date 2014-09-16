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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.java.platform.JavaPlatform;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

@ActionID(
    category = "File",
id = "org.gololang.netbeans.RunGoloFile")
@ActionRegistration(
    iconBase = "org/gololang/netbeans/golo_icon_16px.png",
displayName = "#CTL_RunGoloFile")
@ActionReference(path = "Editors/text/x-golo/Popup", position = 1300)
@Messages("CTL_RunGoloFile=Run Golo File")
public final class RunGoloFile implements ActionListener {

  private final DataObject context;

  public RunGoloFile(DataObject context) {
    this.context = context;
  }



  public void execute(final String javaExecutable, final String goloRootDir, final FileObject fileToExecute) throws InterruptedException, ExecutionException {
      Callable processCallable = new Callable() {

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
            
            return new ProcessBuilder(javaExecutable,
                "-server",
                "-Xms1024M",
                "-Xmx1024M",
                "-XX:-TieredCompilation",
                "-classpath",
                classpath,
                "-Dapp.name=gologolo",
                "fr.insalyon.citi.golo.cli.Main",
                "golo",
                "--files ",
                fileToExecute.getPath()).start();
          }
      };

      ExecutionDescriptor descriptor = new ExecutionDescriptor().frontWindow(true).controllable(true);

      ExecutionService service = ExecutionService.newService(processCallable,
              descriptor, "gologolo command");

      Future<Integer> task = service.run();
      return;
  }

  @Override
  public void actionPerformed(ActionEvent ev) {
    FileObject file = context.getPrimaryFile();
    if (file == null) {
      return;
    }
    
    String rootDir = NbPreferences.forModule(RunPanel.class).get("rootDir", "");
    if (rootDir.isEmpty()) {
      JOptionPane.showMessageDialog(null, "Please set the Golo distribution root directory\nin Tools -> Options -> Golo", "Golo Error", JOptionPane.ERROR_MESSAGE);
    }
    else {
      String javaExecutable = FileUtil.toFile(JavaPlatform.getDefault().findTool("java")).getAbsolutePath();
      try {
        execute(javaExecutable, rootDir, file);
      } catch (InterruptedException ex) {
        Exceptions.printStackTrace(ex);
      } catch (ExecutionException ex) {
        Exceptions.printStackTrace(ex);
      }
    }
  }
}
