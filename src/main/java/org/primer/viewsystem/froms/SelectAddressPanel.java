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

import org.primer.PrimerSetting;
import org.primer.fonts.AwesomeIcon;
import org.primer.languages.MessageKey;
import org.primer.model.AddressTableModel;
import org.primer.primerj.core.Address;
import org.primer.utils.LocaliserUtils;
import org.primer.viewsystem.base.FontSizer;
import org.primer.viewsystem.base.Panels;
import org.primer.viewsystem.base.renderer.AddressImage;
import org.primer.viewsystem.base.renderer.SelectAddressImage;
import org.primer.viewsystem.components.ScrollBarUIDecorator;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class SelectAddressPanel extends WizardPanel {
    public interface SelectAddressListener {
        void selectAddress(Address address);
    }

    private JTable table;
    private AddressTableModel addressTableModel;
    private String defaultAddress;
    private java.util.List<Address> addressList;
    private SelectAddressListener selectAddressListener;

    public SelectAddressPanel(SelectAddressListener selectAddressListener, List<Address> addressList, String defaultAddress) {
        super(MessageKey.SELECT_SIGN_ADDRESS, AwesomeIcon.FA_LIST);
        this.defaultAddress = defaultAddress;
        this.addressList = addressList;
        this.selectAddressListener = selectAddressListener;
    }

    @Override
    public void initialiseContent(JPanel panel) {

        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "10[]10", // Column constraints
                "10[]10" // Row constraints
        ));

        addressTableModel = new AddressTableModel(this.addressList, this.defaultAddress);
        table = new JTable(addressTableModel);
        FontMetrics fontMetrics = panel.getFontMetrics(FontSizer.INSTANCE.getAdjustedDefaultFont());
        TableColumn tableColumn = table.getColumnModel().getColumn(0);
        int statusWidth = fontMetrics.stringWidth(defaultAddress);
        tableColumn.setPreferredWidth(statusWidth + PrimerSetting.STATUS_WIDTH_DELTA * 2);
        table.getColumnModel().getColumn(1).setCellRenderer(new AddressImage());
        table.getColumnModel().getColumn(2).setCellRenderer(new SelectAddressImage());

//        tableColumn = table.getColumnModel().getColumn(1);
//        int tiemW = fontMetrics.stringWidth(String.valueOf(false));
//        tableColumn.setPreferredWidth(tiemW + BitherSetting.STATUS_WIDTH_DELTA);

//        tableColumn = table.getColumnModel().getColumn(2);
//        int tiemH = fontMetrics.stringWidth(String.valueOf(false));
//        tableColumn.setPreferredWidth(tiemH + BitherSetting.STATUS_WIDTH_DELTA);
        table.setOpaque(true);
        table.setAutoCreateColumnsFromModel(true);
        table.setAutoResizeMode(0);
        table.setAutoscrolls(true);
        table.setBorder(BorderFactory.createEmptyBorder());
        table.setComponentOrientation(ComponentOrientation.getOrientation(LocaliserUtils.getLocale()));
        table.setRowHeight(Math.max(PrimerSetting.MINIMUM_ICON_HEIGHT, panel.getFontMetrics(FontSizer.INSTANCE.getAdjustedDefaultFont()).getHeight()) + PrimerSetting.HEIGHT_DELTA);

        final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(table);
        ScrollBarUIDecorator.apply(jScrollPane, false);
        panel.add(jScrollPane, "push,align center,grow");
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int minIndex = lsm.getMinSelectionIndex();
                    int maxIndex = lsm.getMaxSelectionIndex();
                    for (int i = minIndex; i <= maxIndex; i++) {
                        if (lsm.isSelectedIndex(i)) {
                            if (selectAddressListener != null) {
                                closePanel();
                                Address selectAddress = addressList.get(i);
                                selectAddressListener.selectAddress(selectAddress);
                            }
                            break;
                        }
                    }
                }
            }
        });


    }
}
