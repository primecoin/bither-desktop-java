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

package org.primer.primerj.api;

import org.primer.primerj.api.http.BitherUrl;
import org.primer.primerj.api.http.HttpGetResponse;

import org.json.JSONObject;

public class GetExchangeTickerApi extends HttpGetResponse<String> {

    private static final String CURRENCY_RATE = "currency_rate";
    private static final String CURRENCIES_RATE = "currencies_rate";

    private double mCurrencyRate;
    private JSONObject mCurrenciesRate;

    public GetExchangeTickerApi() {
        setUrl(BitherUrl.BITHER_EXCHANGE_TICKER);
    }

    @Override
    public void setResult(String response) throws Exception {
        JSONObject json = new JSONObject(response);

        this.mCurrencyRate = json.getDouble(CURRENCY_RATE);
        this.mCurrenciesRate = json.getJSONObject(CURRENCIES_RATE);
        this.result = response;// Ticker.formatList(json);
    }

    public double getCurrencyRate() {
        return mCurrencyRate;
    }

    public JSONObject getCurrenciesRate() {
        return mCurrenciesRate;
    }
}
