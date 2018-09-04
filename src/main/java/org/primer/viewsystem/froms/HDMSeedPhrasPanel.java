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
import org.primer.preference.UserPreference;
import org.primer.primerj.PrimerjSettings;
import org.primer.utils.LocaliserUtils;
import org.primer.viewsystem.base.Panels;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class HDMSeedPhrasPanel extends WizardPanel {
    private JTextArea taPrivateText;
    private String worldString;

    public HDMSeedPhrasPanel(List<String> worldList) {
        super(MessageKey.HDM_COLD_SEED_WORD_LIST, AwesomeIcon.BITBUCKET);
        worldString = "";
        for (int i = 0; i < worldList.size(); i++) {
            if (i == worldList.size() - 1) {
                worldString += worldList.get(i);
            } else if ((i + 1) % 3 == 0) {
                worldString += worldList.get(i) + "-" + "\n";

            } else {
                worldString += worldList.get(i) + "-";
            }
        }

        if (UserPreference.getInstance().getAppMode() == PrimerjSettings.AppMode.HOT) {
            updateTitle(LocaliserUtils.getString("hdm_hot_seed_word_list"));
        }


    }

    @Override
    public void initialiseContent(JPanel panel) {
        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "[][][][][]", // Column constraints
                "[]20[][][][][]80[]40[][]" // Row constraints
        ));

        taPrivateText = new JTextArea();
        taPrivateText.setBorder(null);
        taPrivateText.setEditable(false);
        taPrivateText.setText(worldString);
        taPrivateText.setBackground(panel.getBackground());
        taPrivateText.setFont(taPrivateText.getFont().deriveFont(20));
        panel.add(taPrivateText, "align center,cell 2 2 ,grow");

    }
}
