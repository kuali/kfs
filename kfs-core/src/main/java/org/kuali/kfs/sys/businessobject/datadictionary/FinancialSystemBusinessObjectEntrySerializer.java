package org.kuali.kfs.sys.businessobject.datadictionary;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;

public class FinancialSystemBusinessObjectEntrySerializer extends JsonSerializer<FinancialSystemBusinessObjectEntry> {

    @Override
    public void serialize(FinancialSystemBusinessObjectEntry entry, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("businessObjectClass", entry.getBusinessObjectClass().getName());
        node.put("titleAttribute", entry.getTitleAttribute());
        node.put("objectLabel", entry.getObjectLabel());
        jsonGenerator.writeTree(node);
    }
}
