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
import org.primer.db.HDAccountProvider;
import org.primer.db.TxProvider;
import org.primer.fonts.AwesomeIcon;
import org.primer.languages.MessageKey;
import org.primer.preference.UserPreference;
import org.primer.primerj.PrimerjSettings;
import org.primer.primerj.core.Address;
import org.primer.primerj.core.AddressManager;
import org.primer.primerj.core.HDMBId;
import org.primer.primerj.core.Tx;
import org.primer.primerj.crypto.PasswordSeed;
import org.primer.primerj.crypto.SecureCharSequence;
import org.primer.primerj.utils.TransactionsUtil;
import org.primer.utils.HDMKeychainRecoveryUtil;
import org.primer.utils.HDMResetServerPasswordUtil;
import org.primer.utils.LocaliserUtils;
import org.primer.utils.PeerUtil;
import org.primer.viewsystem.base.Buttons;
import org.primer.viewsystem.base.Labels;
import org.primer.viewsystem.base.Panels;
import org.primer.viewsystem.base.RadioButtons;
import org.primer.viewsystem.dialogs.DialogConfirmTask;
import org.primer.viewsystem.dialogs.DialogProgress;
import org.primer.viewsystem.dialogs.MessageDialog;
import org.primer.viewsystem.listener.IDialogPasswordListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AdvancePanel extends WizardPanel {
    private JRadioButton rbNormal;
    private JRadioButton rbLow;

    private JButton btnSwitchCold;
    private JButton btnReloadTx;
    private JButton btnRecovery;
    private JButton btnRestHDMPassword;
    private DialogProgress dp;
    private HDMKeychainRecoveryUtil hdmRecoveryUtil;
    private HDMResetServerPasswordUtil hdmResetServerPasswordUtil;

    public AdvancePanel() {
        super(MessageKey.ADVANCE, AwesomeIcon.FA_BOOK);
        dp = new DialogProgress();
        hdmRecoveryUtil = new HDMKeychainRecoveryUtil(dp);
    }

    @Override
    public void initialiseContent(JPanel panel) {

        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "[][][]", // Column constraints
                "[][][][][][]" // Row constraints
        ));
        rbLow = getRbLow();
        rbNormal = getRbNormal();
        ButtonGroup groupFee = new ButtonGroup();
        groupFee.add(rbLow);
        groupFee.add(rbNormal);
        if (UserPreference.getInstance().getTransactionFeeMode() == PrimerjSettings.TransactionFeeMode.Normal) {
            rbNormal.setSelected(true);
        } else {
            rbLow.setSelected(true);
        }
        JLabel label = Labels.newValueLabel(LocaliserUtils.getString("setting_name_transaction_fee"));
        panel.add(label, "push,align left");
        panel.add(rbNormal, "push,align left");
        panel.add(rbLow, "push,align left,wrap");
        JCheckBox cbCheckPassword = RadioButtons.newCheckPassword();
        panel.add(cbCheckPassword, "push,align left,wrap");
//        panel.add(rbCheckPWDOn, "push,align left");
//        panel.add(rbCheckPEDOff, "push,align left,wrap");


        if (AddressManager.getInstance().getAllAddresses().size() == 0 && UserPreference.getInstance().getAppMode() == PrimerjSettings.AppMode.HOT) {
            btnSwitchCold = Buttons.newLargeSwitchColdWizardButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switchColdWallet();


                }
            });

            panel.add(btnSwitchCold, "push,align left");

        }
        btnReloadTx = Buttons.newLargeReloadTxWizardButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadTx();
            }
        });
        panel.add(btnReloadTx, "push,align left");
        if (hdmRecoveryUtil.canRecover()) {
            btnRecovery = Buttons.newLargeRecoveryButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    closePanel();
                    if (!hdmRecoveryUtil.canRecover()) {
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            PeerUtil.stopPeer();
                            try {
                                final String result = hdmRecoveryUtil.recovery();
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        configureHDMRecovery();
                                        if (result != null) {
                                            new MessageDialog(result).showMsg();
                                        }
                                        Primer.refreshFrame();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                            PeerUtil.startPeer();
                        }

                    }.start();

                }
            });
            panel.add(btnRecovery, "push,align left");


        }
        if (HDMBId.getHDMBidFromDb() != null) {
            btnRestHDMPassword = Buttons.newLargeRestPasswordButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    closePanel();
                    restHDMPassword();
                }
            });
            panel.add(btnRestHDMPassword, "push,align left");
        }

    }

    private void restHDMPassword() {

        DialogConfirmTask dialogConfirmTask = new DialogConfirmTask(LocaliserUtils.getString("hdm_reset_server_password_confirm"), new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dp.pack();
                        dp.setVisible(true);
                    }
                });

                hdmResetServerPasswordUtil = new HDMResetServerPasswordUtil(dp);
                final boolean result = hdmResetServerPasswordUtil.changePassword();
                hdmResetServerPasswordUtil = null;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dp.dispose();
                        if (result) {
                            new MessageDialog(LocaliserUtils.getString("hdm_reset_server_password_success")).showMsg();
                        }
                    }
                });
            }
        });
        dialogConfirmTask.pack();
        dialogConfirmTask.setVisible(true);
    }

    private void configureHDMRecovery() {
        if (hdmRecoveryUtil.canRecover()) {
            btnRecovery.setVisible(true);
        } else {
            btnRecovery.setVisible(false);
        }
    }


    private JRadioButton getRbPWDOn() {
        JRadioButton jRadioButton = new JRadioButton();
        jRadioButton.setText(LocaliserUtils.getString("password_strength_check_on"));
        jRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserPreference.getInstance().setCheckPasswordStrength(true);
            }
        });
        return jRadioButton;
    }

    private JRadioButton getRbPWDOff() {
        JRadioButton jRadioButton = new JRadioButton();
        jRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogConfirmTask dialogConfirmTask = new DialogConfirmTask(LocaliserUtils.getString("password_strength_check_off"), new Runnable() {
                    @Override
                    public void run() {
                        UserPreference.getInstance().setCheckPasswordStrength(false);
                    }
                });
                dialogConfirmTask.pack();
                dialogConfirmTask.setVisible(true);

            }
        });

        jRadioButton.setText(LocaliserUtils.getString("setting_name_transaction_fee_low"));
        return jRadioButton;

    }


    private JRadioButton getRbNormal() {
        JRadioButton jRadioButton = new JRadioButton();
        jRadioButton.setText(LocaliserUtils.getString("setting_name_transaction_fee_normal"));
        jRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                UserPreference.getInstance().setTransactionFeeMode(PrimerjSettings.TransactionFeeMode.Normal);

            }
        });
        return jRadioButton;
    }

    private JRadioButton getRbLow() {
        JRadioButton jRadioButton = new JRadioButton();

        jRadioButton.setText(LocaliserUtils.getString("setting_name_transaction_fee_low"));
        jRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UserPreference.getInstance().setTransactionFeeMode(PrimerjSettings.TransactionFeeMode.Low);
            }
        });
        return jRadioButton;

    }

    private void reloadTx() {

        if (Primer.canReloadTx()) {
            Runnable confirmRunnable = new Runnable() {
                @Override
                public void run() {
                    Primer.reloadTxTime = System.currentTimeMillis();
                    PasswordSeed passwordSeed = PasswordSeed.getPasswordSeed();
                    if (passwordSeed == null) {
                        resetTx();
                    } else {
                        callPassword();
                    }
                }
            };
            DialogConfirmTask dialogConfirmTask = new DialogConfirmTask(
                    LocaliserUtils.getString("reload_tx_need_too_much_time"), confirmRunnable
            );
            dialogConfirmTask.pack();
            dialogConfirmTask.setVisible(true);
        } else {
            new MessageDialog(LocaliserUtils.getString("tx_cannot_reloding")).showMsg();

        }


    }

    private void callPassword() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (PasswordSeed.hasPasswordSeed()) {
                    closePanel();
                    PasswordPanel dialogPassword = new PasswordPanel(new IDialogPasswordListener() {
                        @Override
                        public void onPasswordEntered(SecureCharSequence password) {
                            resetTx();

                        }
                    });
                    dialogPassword.showPanel();

                } else {
                    resetTx();
                }
            }
        });
    }

    private void resetTx() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dp.pack();
                        dp.setVisible(true);
                    }
                });
                try {
                    PeerUtil.stopPeer();
                    for (Address address : AddressManager.getInstance().getAllAddresses()) {
                        address.setSyncComplete(false);
                        address.updateSyncComplete();

                    }
                    HDAccountProvider.getInstance().setSyncdNotComplete();
                    TxProvider.getInstance().clearAllTx();
                    for (Address address : AddressManager.getInstance().getAllAddresses()) {
                        address.notificatTx(null, Tx.TxNotificationType.txFromApi);
                    }

                    if (!AddressManager.getInstance().addressIsSyncComplete()) {
                        TransactionsUtil.getMyTxFromBither();
                    }
                    PeerUtil.startPeer();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Primer.refreshFrame();
                            dp.dispose();
                            new MessageDialog(LocaliserUtils.getString("reload_tx_success")).showMsg();
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            dp.dispose();
                            new MessageDialog(LocaliserUtils.getString("network_or_connection_error")).showMsg();
                        }
                    });


                }

            }


        }).start();
    }

    private void switchColdWallet() {
        DialogConfirmTask dialog = new DialogConfirmTask(LocaliserUtils.getString("launch_sequence_switch_to_cold_warn")
                , new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        PeerUtil.stopPeer();
                        Panels.hideLightBoxIfPresent();
                        UserPreference.getInstance().setAppMode(PrimerjSettings.AppMode
                                .COLD);
                        Primer.refreshFrame();

                    }
                });


            }
        });
        dialog.pack();
        dialog.setVisible(true);


    }

}
