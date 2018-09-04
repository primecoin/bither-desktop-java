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
import org.primer.primerj.core.HDAccount;
import org.primer.primerj.crypto.SecureCharSequence;
import org.primer.primerj.delegate.IPasswordGetterDelegate;
import org.primer.qrcode.DisplayQRCodePanle;
import org.primer.utils.KeyUtil;
import org.primer.utils.LocaliserUtils;
import org.primer.viewsystem.base.Buttons;
import org.primer.viewsystem.base.Panels;
import org.primer.viewsystem.listener.IDialogPasswordListener;
import org.primer.xrandom.HDAccountUEntropyDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class HDAccountAddPanel extends WizardPanel implements IPasswordGetterDelegate {

    private JCheckBox xrandomCheckBox;
    private PasswordPanel.PasswordGetter passwordGetter;
    private JButton btnHDAccountSeed;
    private JButton btnHDAccountPhras;
    private HDAccount hdAccount;

    public HDAccountAddPanel() {
        super(MessageKey.add_hd_account_tab_hd, AwesomeIcon.HEADER);
        hdAccount = AddressManager.getInstance().getHdAccount();

        passwordGetter = new PasswordPanel.PasswordGetter(HDAccountAddPanel.this);
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
                                    HDAccountUEntropyDialog accountUEntropyDialog = new HDAccountUEntropyDialog(passwordGetter);
                                    accountUEntropyDialog.pack();
                                    accountUEntropyDialog.setVisible(true);

                                } else {
                                    HDAccount account = new HDAccount(new SecureRandom(), password, new HDAccount.HDAccountGenerationDelegate() {
                                        @Override
                                        public void onHDAccountGenerationProgress(double progress) {

                                        }
                                    });
                                    KeyUtil.setHDAccount(account);
                                    password.wipe();
                                    Primer.refreshFrame();
                                }
                            }
                        });

                    }
                }).start();
                setOkEnabled(AddressManager.getInstance().getHdmKeychain() == null);
            }
        });
    }

    @Override
    public void initialiseContent(JPanel panel) {

        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "[][][][][][][]", // Column constraints
                "10[][][][][][]80[]20[][][]" // Row constraints
        ));

        btnHDAccountSeed = Buttons.newNormalButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                PasswordPanel dialogPassword = new PasswordPanel(new IDialogPasswordListener() {
                    @Override
                    public void onPasswordEntered(SecureCharSequence password) {
                        if (password == null) {
                            return;
                        }
                        password.wipe();
                        String content = hdAccount.getQRCodeFullEncryptPrivKey();
                        String title = LocaliserUtils.getString("add_hd_account_seed_qr_code");
                        showHDMSeedQRCode(content, title);
                    }
                });
                dialogPassword.showPanel();

            }
        }, MessageKey.add_hd_account_seed_qr_code, AwesomeIcon.QRCODE);
        btnHDAccountPhras = Buttons.newNormalButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                PasswordPanel dialogPassword = new PasswordPanel(new IDialogPasswordListener() {
                    @Override
                    public void onPasswordEntered(SecureCharSequence password) {
                        showHDAccountSeedPhras(password);
                    }
                });
                dialogPassword.showPanel();

            }
        }, MessageKey.add_hd_account_seed_qr_phrase, AwesomeIcon.BITBUCKET);

        if (hdAccount == null) {

            //   panel.add(Labels.newNoteLabel(new String[]{LocaliserUtils.getString("hdm_seed_generation_notice")}), "push,align center,wrap");
            xrandomCheckBox = new JCheckBox();
            xrandomCheckBox.setSelected(true);
            xrandomCheckBox.setText(LocaliserUtils.getString("xrandom"));
            panel.add(xrandomCheckBox, "push,align center,wrap");
        } else {
            panel.add(btnHDAccountSeed, "align center,cell 3 0,shrink ,grow,wrap");
            panel.add(btnHDAccountPhras, "align center,shrink ,cell 3 1,grow,wrap");
        }

    }

    @Override
    public void beforePasswordDialogShow() {

    }

    @Override
    public void afterPasswordDialogDismiss() {

    }

    private void showHDMSeedQRCode(String content, String title) {


        DisplayQRCodePanle displayQRCodePanle = new DisplayQRCodePanle(content);
        displayQRCodePanle.showPanel();
        displayQRCodePanle.updateTitle(title);

    }

    private void showHDAccountSeedPhras(final SecureCharSequence password) {
        if (password == null) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                final List<String> words = new ArrayList<String>();
                try {
                    words.addAll(hdAccount.getSeedWords(password));
                } catch (Exception e) {
                    e.printStackTrace();

                }
                if (words.size() > 0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            HDMSeedPhrasPanel hdmSeedPhrasPanel = new HDMSeedPhrasPanel(words);
                            hdmSeedPhrasPanel.showPanel();

                        }
                    });
                }
            }
        }.start();
    }

}
