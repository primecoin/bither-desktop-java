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

package org.primer.primerj.core;

import org.primer.primerj.crypto.DumpedPrivateKey;
import org.primer.primerj.crypto.ECKey;
import org.primer.primerj.exception.AddressFormatException;

import org.junit.Test;

import static org.junit.Assert.*;

public class AddressTest {
    @Test
    public void testAddress() {
        try {
            DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey("L4rK1yDtCWekvXuE6oXD9jCYfFNV2cWRpVuPLBcCU2z8TrisoyY1");
            ECKey ecKey = dumpedPrivateKey.getKey();
            String addressStr = ecKey.toAddress();
            assertEquals(ecKey.toAddress(), "1F3sAm6ZtwLAUnj7d38pGFxtP3RVEvtsbV");
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
    }
}
