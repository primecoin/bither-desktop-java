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

package org.primer.primerj.utils;


import org.primer.primerj.AbstractApp;
import org.primer.primerj.PrimerjSettings;
import org.primer.primerj.api.BlockChainDownloadSpvApi;
import org.primer.primerj.api.BlockChainGetLatestBlockApi;
import org.primer.primerj.api.DownloadSpvApi;
import org.primer.primerj.core.Block;
import org.primer.primerj.core.BlockChain;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockUtil {

    private static final Logger log = LoggerFactory.getLogger(BlockUtil.class);

    private static final String VER = "ver";
    private static final String PREV_BLOCK = "prev_block";
    private static final String MRKL_ROOT = "mrkl_root";
    private static final String TIME = "time";
    private static final String BITS = "bits";
    private static final String NONCE = "nonce";
    private static final String BLOCK_NO = "block_no";
    private static final String HEIGHT = "height";

    public static Block getLatestBlockHeight(JSONObject jsonObject)
            throws Exception {
        int latestHeight = jsonObject.getInt("height");
        int height = 0;
        if (latestHeight % 2016 != 0) {
            height = latestHeight - (latestHeight % 2016);
        } else {
            height = latestHeight;
        }
        BlockChainDownloadSpvApi blockChainDownloadSpvApi = new BlockChainDownloadSpvApi(height);
//        blockChainDownloadSpvApi.handleHttpGet();
        Block block = null;
//        block = blockChainDownloadSpvApi.getResult();

        //        return block;

//        block = BlockUtil.getStoredBlock(2, "000000000000000000110b78ec348dc6430c20092c75c9e47712b4138fa1adac",
//                "d957e4c420b8d33029a550da7dc899614fe2b8674361b9c717f84fef6be3660f",
//                1529657138, 389508950, 2770169744l, 528669);

        block = BlockUtil.getStoredBlock(2, "3e9d7aa684f617075dd3225130f5a25741f8f5588f7f81c824e72ac9e0bf3726", "83ceea1a1212379f7c9906c29185c00f5080011be9c5781b1cc04ac5d40365f6",
                2716058, 1161, 896649034, 2716059);

        //        block.blockHash = Base58.decode("7f97adfafb3be09d7206da904c7f2018c42d7217fe11e4573597061d8e26e1a3");
        block.blockEncodeHash = "f9fa7afab34305dd63a0fa49874f44e4cff0330463ae7548cee2094f3a0462ae";
        block.setMain(true);
//        String blockHash = Base58.encode(block.getBlockHash());

        return block;
    }

    public static Block formatStoreBlockFromBlockChainInfo(JSONObject jsonObject)
            throws JSONException {
        long ver = jsonObject.getLong(VER);
        int height = jsonObject.getInt(HEIGHT);
        String prevBlock = jsonObject.getString(PREV_BLOCK);
        String mrklRoot = jsonObject.getString(MRKL_ROOT);
        int time = jsonObject.getInt(TIME);
        long difficultyTarget = jsonObject.getLong(BITS);
        long nonce = jsonObject.getLong(NONCE);

        return BlockUtil.getStoredBlock(ver, prevBlock, mrklRoot, time, difficultyTarget, nonce, height);
    }

    public static Block formatStoredBlock(JSONObject jsonObject)
            throws JSONException {
        long ver = jsonObject.getLong(VER);
        int height = jsonObject.getInt(BLOCK_NO);
        String prevBlock = jsonObject.getString(PREV_BLOCK);
        String mrklRoot = jsonObject.getString(MRKL_ROOT);
        int time = jsonObject.getInt(TIME);
        long difficultyTarget = jsonObject.getLong(BITS);
        long nonce = jsonObject.getLong(NONCE);

        return BlockUtil.getStoredBlock(ver, prevBlock, mrklRoot, time,
                difficultyTarget, nonce, height);
    }

    public static Block formatStoredBlock(JSONObject jsonObject, int hegih)
            throws JSONException {
        long ver = jsonObject.getLong(VER);
        String prevBlock = jsonObject.getString(PREV_BLOCK);
        String mrklRoot = jsonObject.getString(MRKL_ROOT);
        int time = jsonObject.getInt(TIME);
        long difficultyTarget = jsonObject.getLong(BITS);
        long nonce = jsonObject.getLong(NONCE);

        return BlockUtil.getStoredBlock(ver, prevBlock, mrklRoot, time,
                difficultyTarget, nonce, hegih);

    }

    public static Block getStoredBlock(long ver, String prevBlock,
                                       String mrklRoot, int time, long difficultyTarget, long nonce,
                                       int hegiht) {
        Block b = new Block(ver,
                prevBlock, mrklRoot, time,
                difficultyTarget, nonce, hegiht);
        return b;
    }

    public synchronized static Block dowloadSpvBlock() throws Exception {
        if (AbstractApp.primerjSetting.getDownloadSpvFinish()) {
            return null;
        }
        Block block = null;
        try {
            BlockChainGetLatestBlockApi blockChainGetLatestBlockApi = new BlockChainGetLatestBlockApi();
            blockChainGetLatestBlockApi.handleHttpGet();
            block = blockChainGetLatestBlockApi.getResult();
            if (block == null) {
                DownloadSpvApi downloadSpvApi = new DownloadSpvApi();
                downloadSpvApi.handleHttpGet();
                block = downloadSpvApi.getResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AbstractApp.notificationService.sendBroadcastGetSpvBlockComplete(false);
            throw e;
        }
        if (true || block.getBlockNo() % PrimerjSettings.INTERVAL == 0) {
            BlockChain.getInstance().addSPVBlock(block);
            AbstractApp.primerjSetting.setDownloadSpvFinish(true);
            AbstractApp.notificationService.sendBroadcastGetSpvBlockComplete(true);
        } else {
            log.debug("spv", "service is not vaild");
            AbstractApp.notificationService.sendBroadcastGetSpvBlockComplete(false);
            return null;
        }
        return block;
    }


}
