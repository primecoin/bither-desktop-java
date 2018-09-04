/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.primer.primerjimpl;


import org.primer.ApplicationDataDirectoryLocator;
import org.primer.preference.PersistentCookieStore;
import org.primer.preference.UserPreference;
import org.primer.primerj.AbstractApp;
import org.primer.primerj.PrimerjSettings;
import org.primer.primerj.ISetting;
import org.primer.primerj.NotificationService;
import org.primer.primerj.api.TrustCert;
import org.primer.primerj.qrcode.QRCodeUtil;
import org.apache.http.client.CookieStore;

import java.io.File;
import java.io.InputStream;

public class DesktopImplAbstractApp extends AbstractApp {

    private static final String TrustStorePath = "/https/bithertruststore.jks";
    private static final String TrustStorePassword = "bither";

    @Override
    protected TrustCert initTrustCert() {
        InputStream stream = DesktopImplAbstractApp.class.getResourceAsStream(TrustStorePath);
        if (stream == null) {
            System.out.println(TrustStorePath + " not found");
            return null;
        } else {
            return new TrustCert(stream, TrustStorePassword.toCharArray(), "jks");
        }


    }

    @Override
    public ISetting initSetting() {
        return new ISetting() {
            public PrimerjSettings.ApiConfig getApiConfig() {
                return PrimerjSettings.ApiConfig.BLOCKCHAIN_INFO;
            }

            @Override
            public PrimerjSettings.AppMode getAppMode() {
                return UserPreference.getInstance().getAppMode();
            }

            @Override
            public boolean getBitherjDoneSyncFromSpv() {
                return UserPreference.getInstance().getBitherjDoneSyncFromSpv();
            }

            @Override
            public void setBitherjDoneSyncFromSpv(boolean isDone) {
                UserPreference.getInstance().setBitherjDoneSyncFromSpv(isDone);
            }

            @Override
            public boolean getDownloadSpvFinish() {
                return UserPreference.getInstance().getDownloadSpvFinish();
            }

            @Override
            public void setDownloadSpvFinish(boolean finish) {
                UserPreference.getInstance().setDownloadSpvFinish(finish);

            }

            @Override
            public PrimerjSettings.TransactionFeeMode getTransactionFeeMode() {
                return UserPreference.getInstance().getTransactionFeeMode();
            }

            @Override
            public File getPrivateDir(String dirName) {
                File file = new File(new ApplicationDataDirectoryLocator().getApplicationDataDirectory() + File.separator + dirName);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file;
            }

            @Override
            public boolean isApplicationRunInForeground() {

                return true;
            }

            @Override
            public CookieStore getCookieStore() {
                return PersistentCookieStore.getInstance();
            }

            @Override
            public QRCodeUtil.QRQuality getQRQuality() {
                return UserPreference.getInstance().getQRQuality();
            }
        };
    }

    @Override
    public NotificationService initNotification() {
        return new NotificationDesktopImpl();
    }
}
