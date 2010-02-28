package org.hydracache.protocol.util;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.codec.ProtocolDecoder;
import org.hydracache.protocol.data.codec.ProtocolEncoder;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.data.storage.Data;

public final class ProtocolUtils {
    private static Logger log = Logger.getLogger(ProtocolUtils.class);

    private ProtocolUtils() {
    }

    public static Buffer encodeDataMessage(
            ProtocolEncoder<DataMessage> messageEncoder, Data data)
            throws IOException {
        Buffer buffer = Buffer.allocate();

        DataMessage msg = new DataMessage();

        if (data != null) {
            msg.setVersion(data.getVersion());
            msg.setBlob(data.getContent());

            return encodeDataMessage(messageEncoder, msg);
        }

        return buffer;
    }

    public static Buffer encodeDataMessage(
            ProtocolEncoder<DataMessage> messageEncoder, DataMessage dataMessage)
            throws IOException {
        Buffer buffer = Buffer.allocate();

        messageEncoder.encode(dataMessage, buffer.asDataOutpuStream());

        return buffer;
    }

    public static DataMessage decodeProtocolMessage(
            ProtocolDecoder<DataMessage> decoder, byte[] entityContent)
            throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Incoming entity content (bytes): "
                    + entityContent.length);
        }

        DataMessage dataMessage = decoder.decode(Buffer.wrap(entityContent)
                .asDataInputStream());

        return dataMessage;
    }
}
