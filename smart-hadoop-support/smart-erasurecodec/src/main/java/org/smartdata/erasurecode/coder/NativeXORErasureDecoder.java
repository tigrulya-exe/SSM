/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.erasurecode.coder;

import org.smartdata.erasurecode.*;
import org.smartdata.erasurecode.rawcoder.NativeXORRawDecoder;
import org.smartdata.erasurecode.rawcoder.RawErasureDecoder;

/**
 * Created by intel on 17-7-31.
 */
public class NativeXORErasureDecoder extends ErasureDecoder{
    private RawErasureDecoder rsRawDecoder;

    public NativeXORErasureDecoder(ErasureCoderOptions options) {
        super(options);
    }

    @Override
    protected ErasureCodingStep prepareDecodingStep(final ECBlockGroup blockGroup) {

        ECBlock[] inputBlocks = getInputBlocks(blockGroup);
        ECBlock[] outputBlocks = getOutputBlocks(blockGroup);

        RawErasureDecoder rawDecoder = checkCreateRSRawDecoder();
        return new ErasureDecodingStep(inputBlocks,
                getErasedIndexes(inputBlocks), outputBlocks, rawDecoder);
    }

    private RawErasureDecoder checkCreateRSRawDecoder() {
        if (rsRawDecoder == null) {
            rsRawDecoder = new NativeXORRawDecoder(getOptions());
        }
        return rsRawDecoder;
    }

    @Override
    public void release() {
        if (rsRawDecoder != null) {
            rsRawDecoder.release();
        }
    }
}
