/*
 *
 *  Copyright 2014 http://Bither.net
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.primer.viewsystem.froms;

import org.primer.PrimerUI;
import org.primer.fonts.AwesomeDecorator;
import org.primer.fonts.AwesomeIcon;
import org.primer.languages.MessageKey;
import org.primer.utils.SystemUtil;
import org.primer.viewsystem.base.Buttons;
import org.primer.viewsystem.base.Labels;
import org.primer.viewsystem.base.Panels;
import org.primer.viewsystem.panels.PanelDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public abstract class WizardPanel {

    private JPanel wizardScreenPanel;
    private JPanel contentPanel;
    private JButton btnOk;
    private JButton btnCancel;

    private JLabel labTitle;

    public WizardPanel(MessageKey key, AwesomeIcon icon) {

        wizardScreenPanel = Panels.newRoundedPanel();
        if (Panels.lightBoxPanelIsShow()) {
            setDimension(new Dimension(PrimerUI.POPOVER_MAX_WIDTH, PrimerUI.POPOVER_MAX_HEIGHT));
        } else {
            setDimension(new Dimension(PrimerUI.WIZARD_MIN_WIDTH, PrimerUI.WIZARD_MIN_HEIGHT));
        }

        PanelDecorator.applyWizardTheme(wizardScreenPanel);

        labTitle = Labels.newTitleLabel(key);
        wizardScreenPanel.add(labTitle, "span 4," + PrimerUI.WIZARD_MAX_WIDTH_MIG + ",shrink,aligny top,align center,wrap");


        wizardScreenPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "quit");
        wizardScreenPanel.getActionMap().put("quit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closePanel();
            }
        });

        contentPanel = Panels.newDetailBackgroundPanel(icon);
        wizardScreenPanel.add(contentPanel, "span 4,grow,push,wrap");
        JButton empty = Buttons.newExitButton(null, false);
        empty.setVisible(false);

        wizardScreenPanel.add(empty, "cell 0 2,push");
        btnCancel = Buttons.newCancelButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closePanel();


            }
        });
        wizardScreenPanel.add(btnCancel, "cell 3 2");


    }

    protected void updateTitle(String title) {
        labTitle.setText(title);
    }

    protected void modifOkButton(AwesomeIcon icon, String text) {

        btnOk.setText(text);
        AwesomeDecorator.applyIcon(icon, btnOk, true, PrimerUI.NORMAL_ICON_SIZE);


    }

    private void setDimension(Dimension dimension) {
        wizardScreenPanel.setMinimumSize(dimension);
        wizardScreenPanel.setPreferredSize(dimension);
        wizardScreenPanel.setSize(dimension);
    }

    public void closePanel() {
        if (Panels.lightBoxPopoverPanelIsShow()) {
            Panels.hideLightBoxPopoverIfPresent();
        } else {
            Panels.hideLightBoxIfPresent();
        }
        SystemUtil.callSystemGC();
    }

    public void setOkAction(Action action) {
        if (action != null) {
            btnOk = Buttons.newYesButton(action, AwesomeIcon.CHECK, false);
            wizardScreenPanel.add(btnOk, "cell 1 2");
            wizardScreenPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "finish");
            wizardScreenPanel.getActionMap().put("finish", action);
        }

    }

    public void setOkEnabled(boolean enabled) {
        btnOk.setEnabled(enabled);
    }

    public void setCancelEnabled(boolean enabled) {
        btnCancel.setEnabled(enabled);
    }

    public void showPanel() {
        initialiseContent(contentPanel);
        if (Panels.lightBoxPanelIsShow()) {
            Panels.showLightBoxPopover(wizardScreenPanel);
        } else {
            Panels.showLightBox(wizardScreenPanel);
        }

    }

    public abstract void initialiseContent(JPanel panel);


}
