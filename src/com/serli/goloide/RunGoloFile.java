/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.serli.goloide;

import fr.insalyon.citi.golo.compiler.GoloClassLoader;
import fr.insalyon.citi.golo.compiler.GoloCompilationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.java.platform.JavaPlatform;
import org.openide.filesystems.FileUtil;

@ActionID(
    category = "File",
id = "com.serli.goloide.RunGoloFile")
@ActionRegistration(
    iconBase = "com/serli/goloide/golo_icon_16px.png",
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
                "fr.insalyon.citi.golo.cli.MainGoloGolo",
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
