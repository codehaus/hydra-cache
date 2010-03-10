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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.hydracache.io.BinaryMarshaller;
import org.hydracache.io.XmlMarshaller;
import org.hydracache.protocol.data.message.DataMessage;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author nzhu
 *
 */
public class DefaultProtocolDecoderTest {
    @Mock
    private BinaryMarshaller<DataMessage> binaryMarshaller;
    
    @Mock
    private XmlMarshaller<DataMessage> xmlMarshaller;
    
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void ensureProtocolDecodeInBinary() throws IOException{
        DataMessage dataMsg = new DataMessage();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        ProtocolEncoder<DataMessage> encoder = new DefaultProtocolEncoder(binaryMarshaller, xmlMarshaller);
        
        encoder.encode(dataMsg , new DataOutputStream(out));
        
        ProtocolDecoder<DataMessage> decoder = new DefaultProtocolDecoder(binaryMarshaller, xmlMarshaller);
        
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        
        decoder.decode(in);
        
        verify(binaryMarshaller).readObject(in);
    }
    
    @Test
    public void ensureProtocolDecodeInXml() throws IOException{
        DataMessage dataMsg = new DataMessage();
        
        StringWriter out = new StringWriter();
        
        ProtocolEncoder<DataMessage> encoder = new DefaultProtocolEncoder(binaryMarshaller, xmlMarshaller);
        
        when(xmlMarshaller.writeObject(dataMsg)).thenReturn(new Element("mockElement"));
        
        encoder.encodeXml(dataMsg , out);
        
        ProtocolDecoder<DataMessage> decoder = new DefaultProtocolDecoder(binaryMarshaller, xmlMarshaller);
        
        decoder.decodeXml(out.toString());
        
        verify(xmlMarshaller).readObject(out.toString());
    }

}
