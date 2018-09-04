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

import org.primer.fonts.AwesomeIcon;
import org.primer.languages.MessageKey;
import org.primer.model.PeerTableModel;
import org.primer.primerj.core.Peer;
import org.primer.primerj.core.PeerManager;
import org.primer.primerj.utils.Utils;
import org.primer.primerjimpl.PeerNotificationCenter;
import org.primer.utils.PeerUtil;
import org.primer.viewsystem.base.Buttons;
import org.primer.viewsystem.base.Panels;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PeersPanel extends WizardPanel implements PeerNotificationCenter.IPeerListener {
    private List<Peer> peerList = new ArrayList<Peer>();
    private PeerTableModel peerTableModel;
    private JButton btnBlcok;

    public PeersPanel() {
        super(MessageKey.PEERS, AwesomeIcon.FA_USERS);
        if (!PeerManager.instance().isConnected()) {
            PeerUtil.startPeer();
        }
    }

    @Override
    public void initialiseContent(JPanel panel) {
        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "10[]10", // Column constraints
                "[][]10" // Row constraints
        ));

        btnBlcok = Buttons.newNormalButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closePanel();
                BlockPanel blockPanel = new BlockPanel();
                blockPanel.showPanel();


            }
        }, MessageKey.BLOCKS, AwesomeIcon.FA_SHARE_ALT);
        panel.add(btnBlcok, "align center,shrink,wrap");
        peerTableModel = new PeerTableModel(peerList);
        JTable table = new JTable(peerTableModel);

        panel.add(table, "push,align center,grow");
        refreshPeer();

    }

    private void refreshPeer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (peerList) {
                    List<Peer> temp = PeerManager.instance().getConnectedPeers();
                    if (temp != null && temp.size() > 0) {
                        peerList.addAll(temp);
                        Collections.sort(peerList, new Comparator<Peer>() {
                            @Override
                            public int compare(Peer lhs, Peer rhs) {
                                if (lhs.getClientVersion() == 0 && rhs.getClientVersion() > 0) {
                                    return 1;
                                }
                                if (rhs.getClientVersion() == 0 && lhs.getClientVersion() > 0) {
                                    return -1;
                                }
                                return -1 * Long.valueOf(Utils.parseLongFromAddress(
                                        lhs.getPeerAddress())).compareTo(
                                        Long.valueOf(Utils.parseLongFromAddress(rhs.getPeerAddress())));

                            }
                        });
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                peerTableModel.fireTableDataChanged();
                            }
                        });

                    }
                }

            }
        }).start();
    }

    @Override
    public void sendConnectedChangeBroadcast(String connectedChangeBroadcast, boolean isConnected) {
        refreshPeer();
    }

    @Override
    public void sendBroadcastProgressState(double value) {

    }

    @Override
    public void sendBroadcastPeerState(int numPeers) {
        refreshPeer();

    }

    @Override
    public void removeProgressState() {

    }
}
