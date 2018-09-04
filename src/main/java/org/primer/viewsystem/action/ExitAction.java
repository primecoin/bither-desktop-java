/**
 * Copyright 2011 multibit.org
 *
 * Licensed under the MIT license (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.primer.viewsystem.action;


import org.primer.ApplicationInstanceManager;
import org.primer.Primer;
import org.primer.primerj.core.PeerManager;
import org.primer.utils.LocaliserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Exit the application.
 */
public class ExitAction extends AbstractExitAction {
    private static final Logger log = LoggerFactory.getLogger(ExitAction.class);

    /**
     * Creates a new {@link ExitAction}.
     */
    public ExitAction() {
        super();

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String shuttingDownTitle = LocaliserUtils.getString("BitherFrame.title.shuttingDown");

        if (Primer.getMainFrame() != null) {
            Primer.getMainFrame().setTitle(shuttingDownTitle);

            if (EventQueue.isDispatchThread()) {
                Primer.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Primer.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    }
                });
            }

        }
        PeerManager.instance().stop();
        ApplicationInstanceManager.shutdownSocket();

        // Get rid of main display.
        if (Primer.getMainFrame() != null) {
            Primer.getMainFrame().setVisible(false);
        }

        if (Primer.getMainFrame() != null) {
            Primer.getMainFrame().dispose();
        }
        ApplicationInstanceManager.txDBHelper.close();


        System.exit(0);
    }
}
