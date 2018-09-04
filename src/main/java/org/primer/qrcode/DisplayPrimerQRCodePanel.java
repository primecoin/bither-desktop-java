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

package org.primer.qrcode;

import org.primer.PrimerUI;
import org.primer.fonts.AwesomeIcon;
import org.primer.primerj.qrcode.QRCodeUtil;
import org.primer.primerj.utils.Utils;
import org.primer.languages.MessageKey;
import org.primer.utils.LocaliserUtils;
import org.primer.viewsystem.base.Buttons;
import org.primer.viewsystem.base.Labels;
import org.primer.viewsystem.base.Panels;
import org.primer.viewsystem.froms.WizardPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DisplayPrimerQRCodePanel extends WizardPanel {

    private java.util.List<String> contents;

    private JButton previousPageButton;
    private JLabel iconLabel;
    private JButton nextPageButton;
    private JLabel labPage;
    private int index = 0;


    public DisplayPrimerQRCodePanel(String codeString) {
        super(MessageKey.QR_CODE, AwesomeIcon.QRCODE);
        this.contents = QRCodeUtil.getQrCodeStringList(QRCodeUtil.encodeQrCodeString(codeString));

    }

    @Override
    public void initialiseContent(JPanel panel) {
        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "[][][]", // Column constraints
                "[][]" // Row constraints
        ));

        iconLabel = Labels.newValueLabel("");
        iconLabel.setOpaque(true);
        panel.add(iconLabel, "align center,cell 1 0,wrap");
        previousPageButton = Buttons.newPreviousButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                index--;
                showQRCode();
            }
        });
        nextPageButton = Buttons.newNextButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                index++;
                showQRCode();
            }
        });
        labPage = Labels.newLabChangeNote();
        panel.add(previousPageButton, "align left,cell 0 1");
        panel.add(labPage, "align center,cell 1 1");
        panel.add(nextPageButton, "align right,cell 2 1");
        showQRCode();


    }

    private void showQRCode() {
        if (index == this.contents.size() - 1) {
            nextPageButton.setEnabled(false);
        } else {
            nextPageButton.setEnabled(true);
        }
        if (index == 0) {
            previousPageButton.setEnabled(false);
        } else {
            previousPageButton.setEnabled(true);
        }
        labPage.setText(Utils.format(LocaliserUtils.getString("qr_code_page"), index + 1, contents.size()));
        String text = contents.get(index);
        int scaleWidth = PrimerUI.POPOVER_MIN_WIDTH;
        int scaleHeight = PrimerUI.POPOVER_MIN_WIDTH;
        Image image = QRCodeGenerator.generateQRcode(text, null, null, 1);
        if (image != null) {
            int scaleFactor = (int) (Math.floor(Math.min(scaleHeight / image.getHeight(null),
                    scaleWidth / image.getWidth(null))));
            image = QRCodeGenerator.generateQRcode(text, null, null, scaleFactor);
        }
        iconLabel.setIcon(new ImageIcon(image));

    }


}
