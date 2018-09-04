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

package org.primer.viewsystem.action;

import org.primer.Primer;
import org.primer.primerj.utils.Utils;
import org.primer.viewsystem.froms.IAddressForm;
import org.primer.viewsystem.panels.WalletListPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class WalletMouseListener extends MouseAdapter implements MouseListener {
    private WalletListPanel walletListPanel;
    private IAddressForm singleWalletForm;

    public WalletMouseListener(WalletListPanel walletListPanel, IAddressForm singleWalletForm) {
        super();
        this.walletListPanel = walletListPanel;
        this.singleWalletForm = singleWalletForm;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (this.singleWalletForm != null) {
            String activeAddress = null;
            if (Primer.getActionAddress() != null) {
                activeAddress = Primer.getActionAddress().getAddress();
            }
            if (!Utils.compareString(this.singleWalletForm.getOnlyName()
                    , activeAddress)) {
                walletListPanel.selectWalletPanelByFilename(this.singleWalletForm.getOnlyName());
                Primer.getCoreController().fireDataChangedUpdateNow();
            }
            this.singleWalletForm.getPanel().requestFocusInWindow();

        }

    }

}