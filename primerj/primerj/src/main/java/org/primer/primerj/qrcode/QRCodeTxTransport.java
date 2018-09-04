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

package org.primer.primerj.qrcode;

import org.primer.primerj.core.AddressManager;
import org.primer.primerj.core.HDMAddress;
import org.primer.primerj.core.Tx;
import org.primer.primerj.exception.AddressFormatException;
import org.primer.primerj.utils.Base58;
import org.primer.primerj.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QRCodeTxTransport implements Serializable {

    private static final long serialVersionUID = 5979319690741716813L;
    public static final int NO_HDM_INDEX = -1;

    private List<String> mHashList;
    private String mMyAddress;
    private String mToAddress;
    private long mTo;
    private long mFee;
    private long changeAmt;
    private String changeAddress;
    private int hdmIndex = NO_HDM_INDEX;


    public List<String> getHashList() {
        return mHashList;
    }

    public void setHashList(List<String> mHashList) {
        this.mHashList = mHashList;
    }

    public String getMyAddress() {
        return mMyAddress;
    }

    public void setMyAddress(String mMyAddress) {
        this.mMyAddress = mMyAddress;
    }

    public String getToAddress() {
        return mToAddress;
    }

    public void setToAddress(String mOtherAddress) {
        this.mToAddress = mOtherAddress;
    }

    public long getTo() {
        return mTo;
    }

    public void setTo(long mTo) {
        this.mTo = mTo;
    }

    public long getFee() {
        return mFee;
    }

    public void setFee(long mFee) {
        this.mFee = mFee;
    }

    public String getChangeAddress() {
        return changeAddress;
    }

    public void setChangeAddress(String changeAddress) {
        this.changeAddress = changeAddress;
    }

    public long getChangeAmt() {
        return changeAmt;
    }

    public void setChangeAmt(long changeAmt) {
        this.changeAmt = changeAmt;
    }

    public int getHdmIndex() {
        return hdmIndex;
    }

    public void setHdmIndex(int hdmIndex) {
        this.hdmIndex = hdmIndex;
    }

    public static QRCodeTxTransport formatQRCodeTransport(String str) {
        try {
            QRCodeTxTransport qrCodeTxTransport;
            int hdmIndex = QRCodeTxTransport.NO_HDM_INDEX;
            String[] strArray = QRCodeUtil.splitString(str);
            boolean isHDM = !isAddressHex(strArray[0]);
            if (isHDM) {
                hdmIndex = Integer.parseInt(strArray[0], 16);
                str = str.substring(strArray[0].length() + 1);
                strArray = QRCodeUtil.splitString(str);
            }
            boolean hasChangeAddress = isAddressHex(strArray[1]);
            if (hasChangeAddress) {
                qrCodeTxTransport = changeFormatQRCodeTransport(str);
            } else {
                qrCodeTxTransport = noChangeFormatQRCodeTransport(str);
            }
            qrCodeTxTransport.setHdmIndex(hdmIndex);
            return qrCodeTxTransport;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    private static QRCodeTxTransport changeFormatQRCodeTransport(String str) {
        try {
            String[] strArray = QRCodeUtil.splitString(str);
            QRCodeTxTransport qrCodeTransport = new QRCodeTxTransport();

            String address = Base58.hexToBase58WithAddress(strArray[0]);
            if (!Utils.validBicoinAddress(address)) {
                return null;
            }
            qrCodeTransport.setMyAddress(address);
            String changeAddress = Base58.hexToBase58WithAddress(strArray[1]);
            if (!Utils.validBicoinAddress(changeAddress)) {
                return null;
            }
            qrCodeTransport.setChangeAddress(changeAddress);
            qrCodeTransport.setChangeAmt(Long.parseLong(strArray[2], 16));
            qrCodeTransport.setFee(Long.parseLong(strArray[3], 16));
            String toAddress = Base58.hexToBase58WithAddress(strArray[4]);
            if (!Utils.validBicoinAddress(toAddress)) {
                return null;
            }
            qrCodeTransport.setToAddress(toAddress);
            qrCodeTransport.setTo(Long.parseLong(strArray[5], 16));
            List<String> hashList = new ArrayList<String>();
            for (int i = 6;
                 i < strArray.length;
                 i++) {
                String text = strArray[i];
                if (!Utils.isEmpty(text)) {
                    hashList.add(text);
                }
            }
            qrCodeTransport.setHashList(hashList);
            return qrCodeTransport;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static QRCodeTxTransport noChangeFormatQRCodeTransport(String str) {
        try {
            String[] strArray = QRCodeUtil.splitString(str);
            if (Utils.validBicoinAddress(strArray[0])) {
                return oldFormatQRCodeTransport(str);
            }
            QRCodeTxTransport qrCodeTransport = new QRCodeTxTransport();
            String address = Base58.hexToBase58WithAddress(strArray[0]);

            if (!Utils.validBicoinAddress(address)) {
                return null;
            }
            qrCodeTransport.setMyAddress(address);
            qrCodeTransport.setFee(Long.parseLong(strArray[1], 16));
            qrCodeTransport.setToAddress(Base58.hexToBase58WithAddress(strArray[2]));
            qrCodeTransport.setTo(Long.parseLong(strArray[3], 16));
            List<String> hashList = new ArrayList<String>();
            for (int i = 4;
                 i < strArray.length;
                 i++) {
                String text = strArray[i];
                if (!Utils.isEmpty(text)) {
                    hashList.add(text);
                }
            }
            qrCodeTransport.setHashList(hashList);
            return qrCodeTransport;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static QRCodeTxTransport oldFormatQRCodeTransport(String str) {
        try {
            String[] strArray = QRCodeUtil.splitString(str);
            QRCodeTxTransport qrCodeTransport = new QRCodeTxTransport();
            String address = strArray[0];
            if (!Utils.validBicoinAddress(address)) {
                return null;
            }
            qrCodeTransport.setMyAddress(address);
            qrCodeTransport.setFee(Long.parseLong(strArray[1], 16));
            qrCodeTransport.setToAddress(strArray[2]);
            qrCodeTransport.setTo(Long.parseLong(strArray[3], 16));
            List<String> hashList = new ArrayList<String>();
            for (int i = 4;
                 i < strArray.length;
                 i++) {
                String text = strArray[i];
                if (!Utils.isEmpty(text)) {
                    hashList.add(text);
                }
            }
            qrCodeTransport.setHashList(hashList);
            return qrCodeTransport;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static QRCodeTxTransport oldFromSendRequestWithUnsignedTransaction(Tx tx,
                                                                               String addressCannotParsed) {
        QRCodeTxTransport qrCodeTransport = new QRCodeTxTransport();
        qrCodeTransport.setMyAddress(tx.getFromAddress());
        String toAddress = tx.getFirstOutAddress();
        if (Utils.isEmpty(toAddress)) {
            toAddress = addressCannotParsed;
        }
        qrCodeTransport.setToAddress(toAddress);
        qrCodeTransport.setTo(tx.amountSentToAddress(toAddress));
        qrCodeTransport.setFee(tx.getFee());
        List<String> hashList = new ArrayList<String>();
        for (byte[] h : tx.getUnsignedInHashes()) {
            hashList.add(Utils.bytesToHexString(h));
        }
        qrCodeTransport.setHashList(hashList);
        return qrCodeTransport;
    }

    public static String oldGetPreSignString(Tx tx, String addressCannotParsed) {
        QRCodeTxTransport qrCodeTransport = oldFromSendRequestWithUnsignedTransaction(tx,
                addressCannotParsed);
        String preSignString = qrCodeTransport.getMyAddress() + QRCodeUtil.OLD_QR_CODE_SPLIT +
                Long.toHexString(qrCodeTransport.getFee()).toLowerCase(Locale.US) + QRCodeUtil
                .OLD_QR_CODE_SPLIT + qrCodeTransport.getToAddress() + QRCodeUtil
                .OLD_QR_CODE_SPLIT + Long.toHexString(qrCodeTransport.getTo()).toLowerCase(Locale
                .US) + QRCodeUtil.OLD_QR_CODE_SPLIT;
        for (int i = 0;
             i < qrCodeTransport.getHashList().size();
             i++) {
            String hash = qrCodeTransport.getHashList().get(i);
            if (i < qrCodeTransport.getHashList().size() - 1) {
                preSignString = preSignString + hash + QRCodeUtil.OLD_QR_CODE_SPLIT;
            } else {
                preSignString = preSignString + hash;
            }
        }

        return preSignString;
    }

    private static boolean isAddressHex(String str) {
        boolean isAddress = false;
        if (str.length() % 2 == 0) {
            try {
                String address = Base58.hexToBase58WithAddress(str);
                isAddress = Utils.validBicoinAddress(address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isAddress;
    }

    private static QRCodeTxTransport fromSendRequestWithUnsignedTransaction(Tx tx,
                                                                            String addressCannotParsed, int hdmIndex) {
        QRCodeTxTransport qrCodeTransport = new QRCodeTxTransport();
        qrCodeTransport.setMyAddress(tx.getFromAddress());
        String toAddress = tx.getFirstOutAddress();
        if (Utils.isEmpty(toAddress)) {
            toAddress = addressCannotParsed;
        }
        qrCodeTransport.setHdmIndex(hdmIndex);
        qrCodeTransport.setToAddress(toAddress);
        qrCodeTransport.setTo(tx.amountSentToAddress(toAddress));
        qrCodeTransport.setFee(tx.getFee());
        List<String> hashList = new ArrayList<String>();
        if (hdmIndex < 0) {
            for (byte[] h : tx.getUnsignedInHashes()) {
                hashList.add(Utils.bytesToHexString(h));
            }
        } else {
            HDMAddress a = null;
            for (HDMAddress address : AddressManager.getInstance().getHdmKeychain()
                    .getAllCompletedAddresses()) {
                if (address.getIndex() == hdmIndex) {
                    a = address;
                    break;
                }
            }
            for (byte[] h : tx.getUnsignedInHashesForHDM(a.getPubKey())) {
                hashList.add(Utils.bytesToHexString(h));
            }
        }
        qrCodeTransport.setHashList(hashList);
        return qrCodeTransport;
    }

    public static String getPresignTxString(Tx tx, String changeAddress,
                                            String addressCannotParsed, int hdmIndex) {
        QRCodeTxTransport qrCodeTransport = fromSendRequestWithUnsignedTransaction(tx,
                addressCannotParsed, hdmIndex);
        String preSignString = "";
        try {
            String changeStr = "";
            if (!Utils.isEmpty(changeAddress)) {
                long changeAmt = tx.amountSentToAddress(changeAddress);
                if (changeAmt != 0) {
                    String[] changeStrings = new String[]{Base58.bas58ToHexWithAddress
                            (changeAddress), Long.toHexString(changeAmt)};
                    changeStr = Utils.joinString(changeStrings, QRCodeUtil.QR_CODE_SPLIT);

                }
            }
            String hdmIndexString = "";
            if (qrCodeTransport.getHdmIndex() != QRCodeTxTransport.NO_HDM_INDEX) {
                hdmIndexString = Integer.toHexString(qrCodeTransport.getHdmIndex());
            }
            String[] preSigns = new String[]{hdmIndexString, Base58.bas58ToHexWithAddress
                    (qrCodeTransport.getMyAddress()), changeStr, Long.toHexString(qrCodeTransport
                    .getFee()), Base58.bas58ToHexWithAddress(qrCodeTransport.getToAddress()),
                    Long.toHexString(qrCodeTransport.getTo())};
            preSignString = Utils.joinString(preSigns, QRCodeUtil.QR_CODE_SPLIT);
            String[] hashStrings = new String[qrCodeTransport.getHashList().size()];
            hashStrings = qrCodeTransport.getHashList().toArray(hashStrings);
            preSignString = preSignString + QRCodeUtil.QR_CODE_SPLIT + Utils.joinString
                    (hashStrings, QRCodeUtil.QR_CODE_SPLIT);
            preSignString.toUpperCase(Locale.US);
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }

        return preSignString;
    }


}
