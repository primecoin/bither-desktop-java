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
import org.primer.preference.UserPreference;
import org.primer.primerj.PrimerjSettings;
import org.primer.primerj.core.AddressManager;
import org.primer.primerj.core.HDAccount;
import org.primer.primerj.core.HDMAddress;
import org.primer.primerj.core.HDMKeychain;
import org.primer.primerj.crypto.SecureCharSequence;
import org.primer.primerj.utils.PrivateKeyUtil;
import org.primer.qrcode.DisplayPrimerQRCodePanel;
import org.primer.qrcode.DisplayQRCodePanle;
import org.primer.utils.LocaliserUtils;
import org.primer.viewsystem.base.Buttons;
import org.primer.viewsystem.base.Panels;
import org.primer.viewsystem.dialogs.MessageDialog;
import org.primer.viewsystem.listener.IDialogPasswordListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ExportPrivateKeyPanel extends WizardPanel implements IDialogPasswordListener {

    private JButton btnEncryptQRCode;
    private JButton btnPrivateText;
    private JButton btnPrivateKeyQRCode;
    private JButton btnColdSeed;
    private JButton btnPhras;
    private JButton btnHDAccountSeed;
    private JButton btnHDAccountPhras;
    private HDMKeychain keychain;
    private HDAccount hdAccount;

    private int btnCurrent = 0;

    public ExportPrivateKeyPanel() {
        super(MessageKey.EXPORT, AwesomeIcon.FA_SIGN_OUT);
        keychain = AddressManager.getInstance().getHdmKeychain();
        hdAccount = AddressManager.getInstance().getHdAccount();

    }

    @Override
    public void initialiseContent(final JPanel panel) {

        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "[][][][][][][]", // Column constraints
                "[]20[][][][][]80[]40[][]" // Row constraints
        ));
        btnEncryptQRCode = Buttons.newQRCodeButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (AddressManager.getInstance().getPrivKeyAddresses().size() > 0) {
                    btnCurrent = 0;
                    callPasswordDialog();
                } else {
                    new MessageDialog(LocaliserUtils.getString("private_key_is_empty")).showMsg();
                }

            }
        }, MessageKey.PRIVATE_KEY_QRCODE_ENCRYPTED);
        btnPrivateKeyQRCode = Buttons.newQRCodeButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (AddressManager.getInstance().getPrivKeyAddresses().size() > 0) {
                    btnCurrent = 2;
                    callPasswordDialog();
                } else {
                    new MessageDialog(LocaliserUtils.getString("private_key_is_empty")).showMsg();
                }

            }
        }, MessageKey.PRIVATE_KEY_QRCODE_DECRYPTED);

        btnPrivateText = Buttons.newFileTextButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (AddressManager.getInstance().getPrivKeyAddresses().size() > 0) {
                    btnCurrent = 1;
                    callPasswordDialog();
                } else {
                    new MessageDialog(LocaliserUtils.getString("private_key_is_empty")).showMsg();
                }

            }
        }, MessageKey.PRIVATE_KEY_TEXT);

        MessageKey seedMessageKey = MessageKey.HDM_COLD_SEED_QR_CODE;
        if (UserPreference.getInstance().getAppMode() == PrimerjSettings.AppMode.HOT) {
            seedMessageKey = MessageKey.hdm_hot_seed_qr_code;
        }
        btnColdSeed = Buttons.newNormalButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PasswordPanel dialogPassword = new PasswordPanel(new IDialogPasswordListener() {
                    @Override
                    public void onPasswordEntered(SecureCharSequence password) {
                        if (password == null) {
                            return;
                        }

                        password.wipe();
                        String content = keychain.getQRCodeFullEncryptPrivKey();
                        String title;
                        if (UserPreference.getInstance().getAppMode() == PrimerjSettings.AppMode.COLD) {
                            title = LocaliserUtils.getString("hdm_cold_seed_qr_code");
                        } else {
                            title = LocaliserUtils.getString("hdm_hot_seed_qr_code");
                        }
                        showHDMSeedQRCode(content, title);
                    }
                });
                dialogPassword.showPanel();


            }
        }, seedMessageKey, AwesomeIcon.QRCODE);
        MessageKey worldListMessageKey = MessageKey.HDM_COLD_SEED_WORD_LIST;
        if (UserPreference.getInstance().getAppMode() == PrimerjSettings.AppMode.HOT) {
            worldListMessageKey = MessageKey.hdm_hot_seed_word_list;
        }
        btnPhras = Buttons.newNormalButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PasswordPanel dialogPassword = new PasswordPanel(new IDialogPasswordListener() {
                    @Override
                    public void onPasswordEntered(SecureCharSequence password) {
                        if (password == null) {
                            return;
                        }
                        showHDMSeedPhras(password);
                    }
                });
                dialogPassword.showPanel();


            }
        }, worldListMessageKey, AwesomeIcon.BITBUCKET);
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
                        if (password == null) {
                            return;
                        }
                        showHDAccountSeedPhras(password);
                    }
                });
                dialogPassword.showPanel();

            }
        }, MessageKey.add_hd_account_seed_qr_phrase, AwesomeIcon.BITBUCKET);
        if (UserPreference.getInstance().getAppMode() == PrimerjSettings.AppMode.HOT) {
            if (Primer.getActionAddress() instanceof HDAccount) {
                panel.add(btnHDAccountSeed, "align center,cell 3 2,grow,wrap");
                panel.add(btnHDAccountPhras, "align center,cell 3 3,grow,wrap");

            } else if (Primer.getActionAddress() instanceof HDMAddress) {
                panel.add(btnColdSeed, "align center,cell 3 2,grow,wrap");
                panel.add(btnPhras, "align center,cell 3 3,grow,wrap");

            } else {
                panel.add(btnEncryptQRCode, "align center,cell 3 2 ,grow,wrap");
                panel.add(btnPrivateKeyQRCode, "align center,cell 3 3,grow,wrap");
                panel.add(btnPrivateText, "align center,cell 3 4,grow,wrap");
            }
        } else {
            if (Primer.getActionAddress() == null) {
                panel.add(btnColdSeed, "align center,cell 3 2,grow,wrap");
                panel.add(btnPhras, "align center,cell 3 3,grow,wrap");

            } else {
                panel.add(btnEncryptQRCode, "align center,cell 3 2 ,grow,wrap");
                panel.add(btnPrivateKeyQRCode, "align center,cell 3 3,grow,wrap");
                panel.add(btnPrivateText, "align center,cell 3 4,grow,wrap");
            }
        }


    }


    private void callPasswordDialog() {
        PasswordPanel dialogPassword = new PasswordPanel(this);
        dialogPassword.showPanel();


    }

    @Override
    public void onPasswordEntered(SecureCharSequence password) {
        if (password == null) {
            return;
        }
        switch (btnCurrent) {
            case 0:
                showEncryptQRCode(Primer.getActionAddress().getFullEncryptPrivKey().toUpperCase());
                break;
            case 1:
                showPrivateText(password);
                break;
            case 2:
                showPrivateKeyQRCode(password);
                break;
        }

    }

    private void showEncryptQRCode(String text) {
        DisplayPrimerQRCodePanel qrCodeDialog = new DisplayPrimerQRCodePanel(text);
        qrCodeDialog.showPanel();


    }

    private void showPrivateKeyQRCode(SecureCharSequence password) {
        final SecureCharSequence str = PrivateKeyUtil.getDecryptPrivateKeyString(Primer.getActionAddress().getFullEncryptPrivKey(), password);
        password.wipe();
        DisplayQRCodePanle displayQRCodePanle = new DisplayQRCodePanle(str.toString());
        displayQRCodePanle.showPanel();


    }

    private void showPrivateText(SecureCharSequence password) {

        final SecureCharSequence str = PrivateKeyUtil.getDecryptPrivateKeyString(Primer.getActionAddress().getFullEncryptPrivKey(), password);
        password.wipe();
        PrivateTextPanel privateTextPanel = new PrivateTextPanel(str);
        privateTextPanel.showPanel();

    }

    private void showHDMSeedPhras(final SecureCharSequence password) {

        new Thread() {
            @Override
            public void run() {
                final List<String> words = new ArrayList<String>();
                try {
                    words.addAll(keychain.getSeedWords(password));
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

    private void showHDAccountSeedPhras(final SecureCharSequence password) {

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

    private void showHDMSeedQRCode(String content, String title) {


        DisplayQRCodePanle displayQRCodePanle = new DisplayQRCodePanle(content);
        displayQRCodePanle.showPanel();
        displayQRCodePanle.updateTitle(title);

    }


}
