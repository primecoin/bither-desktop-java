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

package org.primer.viewsystem.base.renderer;

import org.primer.PrimerUI;
import org.primer.fonts.AwesomeDecorator;
import org.primer.fonts.AwesomeIcon;
import org.primer.viewsystem.themes.Themes;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SelectAddressImage extends DefaultTableCellRenderer {
    private JLabel labCheck;
    private JLabel labNull;

    public SelectAddressImage() {
        labCheck = new JLabel();
        labNull = new JLabel();
        labCheck.setOpaque(true);
        labNull.setOpaque(true);
        labNull.setText("");

        AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, labCheck, true, PrimerUI.SMALL_ICON_SIZE);


    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                   int column) {
        if (row % 2 == 0) {
            labCheck.setBackground(Themes.currentTheme.detailPanelBackground());
            labNull.setBackground(Themes.currentTheme.detailPanelBackground());

        } else {
            labCheck.setBackground(Themes.currentTheme.sidebarPanelBackground());
            labNull.setBackground(Themes.currentTheme.sidebarPanelBackground());

        }

        if (Boolean.valueOf(value.toString())) {
            return labCheck;
        } else {
            return labNull;
        }

    }
}
