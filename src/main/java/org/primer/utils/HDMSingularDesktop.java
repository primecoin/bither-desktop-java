/*
 *
 *  * Copyright 2014 http://Bither.net
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.primer.utils;

import org.primer.Primer;
import org.primer.primerj.core.HDMAddress;
import org.primer.primerj.core.HDMKeychain;
import org.primer.primerj.delegate.HDMHotAdd;
import org.primer.primerj.delegate.HDMSingular;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.List;

public class HDMSingularDesktop extends HDMSingular {

    public HDMSingularDesktop(@Nonnull HDMSingularDelegate delegate) {
        super(delegate);

    }

    @Override
    protected void runOnUIThread(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    public void server() {
        new Thread() {
            @Override
            public void run() {
                callInServer(new HDMHotAdd.IGenerateHDMKeyChain() {
                    @Override
                    public void generateHDMKeyChain(HDMKeychain hdmKeychain) {
                        KeyUtil.setHDKeyChain(hdmKeychain);

                    }

                    @Override
                    public void beginCompleteAddress() {
                        PeerUtil.stopPeer();
                    }

                    @Override
                    public void completeAddrees(List<HDMAddress> hdmAddresses) {
                        Primer.refreshFrame();
                    }


                });
            }


        }.start();
    }


}
