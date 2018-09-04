package org.primer.primerj.api;

import org.primer.primerj.api.http.BitherUrl;
import org.primer.primerj.api.http.HttpsGetResponse;

/**
 * Created by zhangbo on 16/1/9.
 */
public class BlockChainMytransactionsApi extends HttpsGetResponse<String> {

    @Override
    public void setResult(String response) throws Exception {
        this.result = response;
    }

    public BlockChainMytransactionsApi(String address) {
//        String url = Utils.format(BitherUrl.BITHER_BC_GET_BY_ADDRESS, address);
        setUrl(address);
    }

    public BlockChainMytransactionsApi() {
        setUrl(BitherUrl.BITHER_BC_LATEST_BLOCK);
    }

    public BlockChainMytransactionsApi(int txIndex) {
        String url = String.format(BitherUrl.BITHER_BC_TX_INDEX, txIndex);
        setUrl(url);
    }
}
