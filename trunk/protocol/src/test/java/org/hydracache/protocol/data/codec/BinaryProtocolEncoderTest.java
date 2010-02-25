/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hydracache.protocol.data.codec;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.message.DataMessage;
import org.junit.Test;

/**
 * @author nzhu
 *
 */
public class BinaryProtocolEncoderTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void ensureProtocolEncodeInBinary() throws IOException{
        Marshaller<DataMessage> marshaller = mock(Marshaller.class);
        
        ProtocolEncoder<DataMessage> encoder = new BinaryProtocolEncoder(marshaller);
        
        DataMessage dataMsg = new DataMessage();
        
        DataOutputStream out = new DataOutputStream(new ByteArrayOutputStream());
        
        encoder.encode(dataMsg , out);
        
        verify(marshaller).writeObject(dataMsg, out);
    }

}
