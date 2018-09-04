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
import org.primer.PrimerUI;
import org.primer.fonts.AwesomeIcon;
import org.primer.languages.MessageKey;
import org.primer.primerj.AbstractApp;
import org.primer.primerj.api.http.Http400Exception;
import org.primer.primerj.core.AddressManager;
import org.primer.primerj.core.HDMAddress;
import org.primer.primerj.core.HDMBId;
import org.primer.primerj.core.HDMKeychain;
import org.primer.primerj.crypto.SecureCharSequence;
import org.primer.primerj.delegate.IPasswordGetterDelegate;
import org.primer.utils.ExceptionUtil;
import org.primer.utils.LocaliserUtils;
import org.primer.utils.PeerUtil;
import org.primer.viewsystem.base.Labels;
import org.primer.viewsystem.base.Panels;
import org.primer.viewsystem.dialogs.MessageDialog;
import org.primer.viewsystem.themes.Themes;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class HDMAddAddressPanel extends WizardPanel implements IPasswordGetterDelegate {

    private JSpinner spinnerCount;
    private PasswordPanel.PasswordGetter passwordGetter;
    private HDMKeychain keychain;
    private JLabel labRefresh;

    public HDMAddAddressPanel() {
        super(MessageKey.activity_name_add_hdm_address, AwesomeIcon.PLUS);
        setOkAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAdd();
            }
        });

        keychain = AddressManager.getInstance().getHdmKeychain();
        passwordGetter = new PasswordPanel.PasswordGetter(HDMAddAddressPanel.this);

    }


    @Override
    public void beforePasswordDialogShow() {

    }

    @Override
    public void afterPasswordDialogDismiss() {


    }

    private int getMaxCount() {
        int max = AbstractApp.primerjSetting.hdmAddressPerSeedPrepareCount() - AddressManager.getInstance
                ().getHdmKeychain().getAllCompletedAddresses().size();
        return max;
    }

    @Override
    public void initialiseContent(JPanel panel) {

        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "[]", // Column constraints
                "[][][][]80[]20[]" // Row constraints
        ));

        labRefresh = Labels.newSpinner(Themes.currentTheme.fadedText(), PrimerUI.NORMAL_PLUS_ICON_SIZE);
        panel.add(labRefresh, "align center,span,wrap");
        labRefresh.setVisible(false);
        spinnerCount = new JSpinner();
        panel.add(spinnerCount, "align center,cell 0 2 ,wrap");
        if (AddressManager.isPrivateLimit()) {
            spinnerCount.setEnabled(false);
            setOkEnabled(false);

        } else {
            Integer value = new Integer(1);
            Integer min = new Integer(1);

            Integer max = new Integer(getMaxCount());
            Integer step = new Integer(1);
            SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
            spinnerCount.setModel(model);
        }


    }

    private void performAdd() {
        final int count = Integer.valueOf(spinnerCount.getValue().toString());
        labRefresh.setVisible(true);
        new Thread() {
            @Override
            public void run() {
                final SecureCharSequence password = passwordGetter.getPassword();
                if (password == null) {
                    labRefresh.setVisible(false);
                    return;
                }
                PeerUtil.stopPeer();
                final List<HDMAddress> as = keychain.completeAddresses(count, password,
                        new HDMKeychain.HDMFetchRemotePublicKeys() {
                            @Override
                            public void completeRemotePublicKeys(CharSequence password,
                                                                 List<HDMAddress.Pubs>
                                                                         partialPubs) {
                                try {
                                    HDMBId hdmBid = HDMBId.getHDMBidFromDb();
                                    HDMKeychain.getRemotePublicKeys(hdmBid, password, partialPubs);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    String msg = LocaliserUtils.getString("network_or_connection_error");
                                    if (e instanceof Http400Exception) {
                                        msg = ExceptionUtil.getHDMHttpExceptionMessage((
                                                (Http400Exception) e).getErrorCode());
                                    }
                                    final String m = msg;
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MessageDialog(m).showMsg();
                                        }
                                    });
                                }
                            }
                        });
                PeerUtil.startPeer();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        labRefresh.setVisible(false);
                        if (as.size() == 0) {
                            return;
                        }
                        ArrayList<String> s = new ArrayList<String>();
                        for (HDMAddress a : as) {
                            s.add(a.getAddress());
                        }
                        closePanel();
                        Panels.hideLightBoxIfPresent();
                        Primer.refreshFrame();

                    }
                });
            }


        }.start();
    }

}
