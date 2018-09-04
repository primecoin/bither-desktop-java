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
import org.primer.primerj.api.http.BitherUrl;
import org.primer.utils.LocaliserUtils;
import org.primer.utils.ViewUtil;
import org.primer.viewsystem.base.Buttons;
import org.primer.viewsystem.base.Labels;
import org.primer.viewsystem.base.Panels;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URI;

public class AboutPanel extends WizardPanel {
    public AboutPanel() {
        super(MessageKey.ABOUT, AwesomeIcon.SMILE_O);
    }

    @Override
    public void initialiseContent(JPanel panel) {
        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "[][][][][][][]", // Column constraints
                "[]10[][][][][]80[]20[][][]" // Row constraints
        ));

        //  String version = System.getProperties().getProperty("Implementation-Version");
        //System.out.println(System.getProperties());

        panel.add(Labels.newValueLabel(LocaliserUtils.getString("version") + ": " + PrimerSetting.VERSION), "push,align center,wrap");

        panel.add(Buttons.newLaunchBrowserButton(getLaunchBrowserAction(), MessageKey.VISIT_WEBSITE), "wrap,align center");

    }

    private Action getLaunchBrowserAction() {

        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String url = BitherUrl.BITHER_DNS.BITHER_URL;
                    ViewUtil.openURI(new URI(url));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        };
    }

}
