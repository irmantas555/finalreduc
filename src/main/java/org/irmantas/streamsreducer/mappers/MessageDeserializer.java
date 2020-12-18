package org.irmantas.streamsreducer.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.irmantas.streamsreducer.model.InMessage;

public class MessageDeserializer implements Deserializer {
    @Override
    public Object deserialize(String topic, byte[] messageByte) {
        ObjectMapper mapper = new ObjectMapper();
        InMessage message = null;
        try {
            message = mapper.readValue(messageByte, InMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }
}
