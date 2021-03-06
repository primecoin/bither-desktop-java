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

import org.primer.Primer;
import org.primer.fonts.AwesomeIcon;
import org.primer.languages.MessageKey;
import org.primer.primerj.core.AddressManager;
import org.primer.primerj.core.HDMKeychain;
import org.primer.primerj.crypto.SecureCharSequence;
import org.primer.primerj.delegate.IPasswordGetterDelegate;
import org.primer.utils.KeyUtil;
import org.primer.utils.LocaliserUtils;
import org.primer.viewsystem.base.Labels;
import org.primer.viewsystem.base.Panels;
import org.primer.xrandom.HDMKeychainColdUEntropyDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.security.SecureRandom;

public class HDMColdPanel extends WizardPanel implements IPasswordGetterDelegate {
    private JCheckBox xrandomCheckBox;
    private PasswordPanel.PasswordGetter passwordGetter;

    public HDMColdPanel() {
        super(MessageKey.HDM, AwesomeIcon.FA_RECYCLE);
        passwordGetter = new PasswordPanel.PasswordGetter(HDMColdPanel.this);
        setOkAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final SecureCharSequence password = passwordGetter.getPassword();
                        if (password == null) {
                            return;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                closePanel();
                                if (xrandomCheckBox.isSelected()) {
                                    HDMKeychainColdUEntropyDialog hdmKeychainColdUEntropyDialog = new HDMKeychainColdUEntropyDialog(passwordGetter);
                                    hdmKeychainColdUEntropyDialog.pack();
                                    hdmKeychainColdUEntropyDialog.setVisible(true);
                                } else {
                                    HDMKeychain chain = new HDMKeychain(new SecureRandom(), password);
                                    KeyUtil.setHDKeyChain(chain);
                                    password.wipe();
                                    Primer.refreshFrame();


                                }
                            }
                        });

                    }
                }).start();

            }
        });

        setOkEnabled(AddressManager.getInstance().getHdmKeychain() == null);
    }

    @Override
    public void initialiseContent(final JPanel panel) {
        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "[][][][][][][]", // Column constraints
                "[]10[][][][][]80[]20[][][]" // Row constraints
        ));


        panel.add(Labels.newNoteLabel(new String[]{LocaliserUtils.getString("hdm_seed_generation_notice")}), "push,align center,wrap");
        xrandomCheckBox = new JCheckBox();
        xrandomCheckBox.setSelected(true);
        xrandomCheckBox.setText(LocaliserUtils.getString("xrandom"));
        panel.add(xrandomCheckBox, "push,align center,wrap");
    }

    @Override
    public void beforePasswordDialogShow() {

    }

    @Override
    public void afterPasswordDialogDismiss() {

    }


}