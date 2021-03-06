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

package org.primer.xrandom;

import org.primer.Primer;
import org.primer.primerj.PrimerjSettings;
import org.primer.primerj.core.HDMKeychain;
import org.primer.primerj.crypto.SecureCharSequence;
import org.primer.primerj.delegate.HDMSingular;
import org.primer.preference.UserPreference;
import org.primer.utils.KeyUtil;
import org.primer.utils.LocaliserUtils;
import org.primer.utils.PeerUtil;
import org.primer.viewsystem.dialogs.MessageDialog;
import org.primer.viewsystem.froms.PasswordPanel;

import javax.swing.*;
import java.util.ArrayList;

public class HDMKeychainHotUEntropyDialog extends UEntropyDialog {

    public static HDMSingular hdmSingular;

    public HDMKeychainHotUEntropyDialog(PasswordPanel.PasswordGetter passwordGetter) {
        super(1, passwordGetter);
    }

    @Override
    void didSuccess(Object obj) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                quit();
                Primer.refreshFrame();
                if (UserPreference.getInstance().getAppMode() == PrimerjSettings.AppMode.COLD) {
                    new MessageDialog(LocaliserUtils.getString("hdm_keychain_xrandom_final_confirm")).showMsg();
                }
            }
        });
    }

    @Override
    Thread getGeneratingThreadWithXRandom(UEntropyCollector collector) {
        return new GenerateThread(collector);
    }


    private class GenerateThread extends Thread {
        private double saveProgress = 0.1;
        private double startProgress = 0.01;
        private double progressKeyRate = 0.5;
        private double progressEntryptRate = 0.5;

        private long startGeneratingTime;

        private Runnable cancelRunnable;

        private UEntropyCollector entropyCollector;

        public GenerateThread(UEntropyCollector entropyCollector) {
            this.entropyCollector = entropyCollector;

        }

        @Override
        public synchronized void start() {
            super.start();
        }

        public void cancel(Runnable cancelRunnable) {
            this.cancelRunnable = cancelRunnable;
        }

        private void finishGenerate() {
            passwordGetter.wipe();
            PeerUtil.startPeer();
            entropyCollector.stop();
        }

        @Override
        public void run() {
            SecureCharSequence password = passwordGetter.getPassword();
            if (password == null) {
                throw new IllegalStateException("GenerateThread does not have password");
            }
            startGeneratingTime = System.currentTimeMillis();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    onProgress(startProgress);
                }
            });
            boolean success = false;
            final ArrayList<String> addressStrs = new ArrayList<String>();
            double progress = startProgress;
            double itemProgress = (1.0 - startProgress - saveProgress) / (double) targetCount;
            try {
                entropyCollector.start();
                PeerUtil.stopPeer();
                for (int i = 0;
                     i < targetCount;
                     i++) {
                    if (cancelRunnable != null) {
                        finishGenerate();
                        SwingUtilities.invokeLater(cancelRunnable);
                        return;
                    }

                    XRandom xRandom = new XRandom(entropyCollector);

                    if (cancelRunnable != null) {
                        finishGenerate();
                        SwingUtilities.invokeLater(cancelRunnable);
                        return;
                    }


                    if (hdmSingular != null && hdmSingular.shouldGoSingularMode()) {
                        byte[] entropy = new byte[64];
                        xRandom.nextBytes(entropy);
                        progress += itemProgress * progressKeyRate;
                        onProgress(progress);
                        if (cancelRunnable != null) {
                            finishGenerate();
                            SwingUtilities.invokeLater(cancelRunnable);
                            return;
                        }
                        hdmSingular.setPassword(password);
                        hdmSingular.setEntropy(entropy);
                        hdmSingular.xrandomFinished();
                        progress += itemProgress * progressEntryptRate;
                        onProgress(progress);
                    } else {

                        HDMKeychain chain = new HDMKeychain(xRandom, passwordGetter.getPassword());

                        progress += itemProgress * progressKeyRate;
                        onProgress(progress);
                        if (cancelRunnable != null) {
                            finishGenerate();
                            SwingUtilities.invokeLater(cancelRunnable);
                            return;
                        }

                        KeyUtil.setHDKeyChain(chain);
                        progress += itemProgress * progressKeyRate;
                        onProgress(progress);
                    }
                    if (cancelRunnable != null) {
                        finishGenerate();
                        SwingUtilities.invokeLater(cancelRunnable);

                        return;
                    }
                    // start encrypt


                    progress += itemProgress * progressEntryptRate;
                    onProgress(progress);
                }
                entropyCollector.stop();
                passwordGetter.wipe();
                if (cancelRunnable != null) {
                    finishGenerate();
                    SwingUtilities.invokeLater(cancelRunnable);
                    return;
                }

                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            finishGenerate();
            if (success) {
                while (System.currentTimeMillis() - startGeneratingTime < MinGeneratingTime) {

                }
                onProgress(1);
                didSuccess(addressStrs);
            } else {
                onFailed();
            }
        }


        private void onFailed() {
            quit();
        }

    }

}
