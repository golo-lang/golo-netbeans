/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.serli.goloide;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.SubRegistration(
    location = "Golo",
displayName = "#AdvancedOption_DisplayName_Run",
keywords = "#AdvancedOption_Keywords_Run",
keywordsCategory = "Golo/Run")
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_Run=Run", "AdvancedOption_Keywords_Run=Run"})
public final class RunOptionsPanelController extends OptionsPanelController {

  private RunPanel panel;
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private boolean changed;

  public void update() {
    getPanel().load();
    changed = false;
  }

  public void applyChanges() {
    getPanel().store();
    changed = false;
  }

  public void cancel() {
    // need not do anything special, if no changes have been persisted yet
  }

  public boolean isValid() {
    return getPanel().valid();
  }

  public boolean isChanged() {
    return changed;
  }

  public HelpCtx getHelpCtx() {
    return null; // new HelpCtx("...ID") if you have a help set
  }

  public JComponent getComponent(Lookup masterLookup) {
    return getPanel();
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    pcs.addPropertyChangeListener(l);
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    pcs.removePropertyChangeListener(l);
  }

  private RunPanel getPanel() {
    if (panel == null) {
      panel = new RunPanel(this);
    }
    return panel;
  }

  void changed() {
    if (!changed) {
      changed = true;
      pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
    }
    pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
  }
}
