package org.kuali.kfs.sys.businessobject.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.kuali.rice.kns.datadictionary.control.ControlDefinitionBase;
import org.kuali.rice.kns.datadictionary.control.ControlDefinitionType;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.IOException;
import java.util.Iterator;

public class FinancialSystemBusinessObjectEntryDeserializer extends JsonDeserializer<FinancialSystemBusinessObjectEntry> {
    @Override
    public FinancialSystemBusinessObjectEntry deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            FinancialSystemBusinessObjectEntry businessObjectEntry = new FinancialSystemBusinessObjectEntry();
            ObjectCodec codec = jp.getCodec();
            JsonNode entryRoot = codec.readTree(jp);

            businessObjectEntry.setBusinessObjectClass((Class<? extends BusinessObject>)(Class.forName(entryRoot.get("businessObjectClass").getTextValue())));
            businessObjectEntry.setTitleAttribute(entryRoot.get("titleAttribute").getTextValue());
            businessObjectEntry.setObjectLabel(entryRoot.get("objectLabel").getTextValue());

            deserializeAttributes(businessObjectEntry, entryRoot.get("attributes").getElements());

            return businessObjectEntry;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void deserializeAttributes(FinancialSystemBusinessObjectEntry entry, Iterator<JsonNode> attributes) {
        AttributeDefinition attributeDefinition = new AttributeDefinition();
        while (attributes.hasNext()) {
            JsonNode nextAttr = attributes.next();
            attributeDefinition.setName(nextAttr.get("name").getTextValue());
            attributeDefinition.setLabel(nextAttr.get("label").getTextValue());
            attributeDefinition.setShortLabel(nextAttr.get("shortLabel").getTextValue());
            attributeDefinition.setConstraintText(nextAttr.get("constraintText").getTextValue());
            attributeDefinition.setRequired(nextAttr.get("required").getBooleanValue());
            attributeDefinition.setForceUppercase(nextAttr.get("forceUppercase").getBooleanValue());
            if (!ObjectUtils.isNull(attributeDefinition.getMinLength())) {
                attributeDefinition.setMinLength(nextAttr.get("minLength").getIntValue());
            }
            if (!ObjectUtils.isNull(attributeDefinition.getMaxLength())) {
                attributeDefinition.setMaxLength(nextAttr.get("maxLength").getIntValue());
            }
            if (!ObjectUtils.isNull(attributeDefinition.getUnique())) {
                attributeDefinition.setUnique(nextAttr.get("unique").getBooleanValue());
            }
            if (!StringUtils.isBlank(attributeDefinition.getExclusiveMin())) {
                attributeDefinition.setExclusiveMin(nextAttr.get("exclusiveMin").getTextValue());
            }
            if (!StringUtils.isBlank(attributeDefinition.getInclusiveMax())) {
                attributeDefinition.setInclusiveMax(nextAttr.get("inclusiveMax").getTextValue());
            }

            JsonNode node = nextAttr.get("control");
            if (node != null) {
                ControlDefinitionBase controlDefinition = new ControlDefinitionBase();
                controlDefinition.setType(ControlDefinitionType.valueOf(node.get("type").getTextValue()));
                attributeDefinition.setControl(controlDefinition);
            }
        }
        entry.getAttributes().add(attributeDefinition);
    }
}
