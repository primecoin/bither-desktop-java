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

package org.primer.primerj.db;


import org.primer.primerj.core.Block;

import java.util.List;

public interface IBlockProvider {

    public List<Block> getAllBlocks();

    public List<Block> getBlocksFrom(int blockNo);

    public List<Block> getLimitBlocks(int limit);

    public int getBlockCount();

    public Block getLastBlock();

    public Block getLastOrphanBlock();

    public Block getBlock(byte[] blockHash);

    public Block getOrphanBlockByPrevHash(byte[] prevHash);

    public Block getMainChainBlock(byte[] blockHash);

    public List<byte[]> exists(List<byte[]> blockHashes);

    public void addBlocks(List<Block> blockItemList);

    public void addBlock(Block item);

    public void updateBlock(byte[] blockHash, boolean isMain);

    public void removeBlock(byte[] blockHash);

    public void cleanOldBlock();


}
