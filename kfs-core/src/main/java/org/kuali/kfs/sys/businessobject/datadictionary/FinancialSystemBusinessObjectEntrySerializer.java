package org.kuali.kfs.sys.businessobject.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.kuali.rice.kns.datadictionary.control.ControlDefinitionBase;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.IOException;

public class FinancialSystemBusinessObjectEntrySerializer extends JsonSerializer<FinancialSystemBusinessObjectEntry> {

    @Override
    public void serialize(FinancialSystemBusinessObjectEntry entry, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("businessObjectClass", entry.getBusinessObjectClass().getName());
        node.put("titleAttribute", entry.getTitleAttribute());
        node.put("objectLabel", entry.getObjectLabel());

        ArrayNode attributes = mapper.createArrayNode();
        for (AttributeDefinition attributeDefinition : entry.getAttributes()) {
            attributes.add(mapAttributeDefinition(attributeDefinition, mapper));
        }
        node.put("attributes", attributes);
        jsonGenerator.writeTree(node);
    }

    protected ObjectNode mapAttributeDefinition(AttributeDefinition attributeDefinition, ObjectMapper mapper) {
        ObjectNode attributeNode = mapper.createObjectNode();
        attributeNode.put("name", attributeDefinition.getName());
        attributeNode.put("label", attributeDefinition.getLabel());
        attributeNode.put("shortLabel", attributeDefinition.getShortLabel());
        attributeNode.put("constraintText", attributeDefinition.getConstraintText());
        attributeNode.put("required", attributeDefinition.isRequired());
        attributeNode.put("forceUppercase", attributeDefinition.getForceUppercase());
        if (!ObjectUtils.isNull(attributeDefinition.getMinLength())) {
            attributeNode.put("minLength", attributeDefinition.getMinLength());
        }
        if (!ObjectUtils.isNull(attributeDefinition.getMaxLength())) {
            attributeNode.put("maxLength", attributeDefinition.getMaxLength());
        }
        if (!ObjectUtils.isNull(attributeDefinition.getUnique())) {
            attributeNode.put("unique", attributeDefinition.getUnique());
        }
        if (!StringUtils.isBlank(attributeDefinition.getExclusiveMin())) {
            attributeNode.put("exclusiveMin", attributeDefinition.getExclusiveMin());
        }
        if (!StringUtils.isBlank(attributeDefinition.getInclusiveMax())) {
            attributeNode.put("inclusiveMax", attributeDefinition.getInclusiveMax());
        }

        ControlDefinitionBase control = (ControlDefinitionBase)attributeDefinition.getControl();
        if (control != null) {
            ObjectNode controlDefinitionNode = mapper.createObjectNode();
            controlDefinitionNode.put("type", control.getType().toString());
            attributeNode.put("controlDefinition", controlDefinitionNode);
        }
        return attributeNode;
    }

}
