package org.kuali.kfs.sys.businessobject.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.node.ArrayNode;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.datadictionary.LookupDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinitionBase;
import org.kuali.rice.kns.datadictionary.control.ControlDefinitionType;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.SortDefinition;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FinancialSystemBusinessObjectEntryDeserializer extends JsonDeserializer<FinancialSystemBusinessObjectEntry> {
    @Override
    public FinancialSystemBusinessObjectEntry deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            FinancialSystemBusinessObjectEntry businessObjectEntry = new FinancialSystemBusinessObjectEntry();
            ObjectCodec codec = jp.getCodec();
            JsonNode entryRoot = codec.readTree(jp);

            businessObjectEntry.setBusinessObjectClass((Class<? extends BusinessObject>) (Class.forName(entryRoot.get("businessObjectClass").getTextValue())));
            businessObjectEntry.setTitleAttribute(entryRoot.get("titleAttribute").getTextValue());
            businessObjectEntry.setObjectLabel(entryRoot.get("objectLabel").getTextValue());

            deserializeAttributes(businessObjectEntry, entryRoot.get("attributes").getElements());

            if (entryRoot.has("lookupDefinition")) {
                LookupDefinition lookupDefinition = deserializeLookupDefinition(entryRoot.get("lookupDefinition"));
                businessObjectEntry.setLookupDefinition(lookupDefinition);
            }

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
    
    protected LookupDefinition deserializeLookupDefinition(JsonNode lookupDefinitionNode) {
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setTitle(lookupDefinitionNode.get("title").getTextValue());

        SortDefinition defaultSort = new SortDefinition();
        JsonNode defaultSortNode = lookupDefinitionNode.get("defaultSort");
        defaultSort.setSortAscending(defaultSortNode.get("ascending").getBooleanValue());
        ArrayNode sortAttributeNames = (ArrayNode)defaultSortNode.get("attributeNames");
        List<String> attributeNames = new ArrayList<>();
        for (JsonNode attributeNameNode : sortAttributeNames) {
            attributeNames.add(attributeNameNode.getTextValue());
        }
        lookupDefinition.setDefaultSort(defaultSort);

        List<FieldDefinition> lookupFields = new ArrayList<>();
        for (JsonNode lookupFieldNode : (ArrayNode)lookupDefinitionNode.get("lookupFields")) {
            FieldDefinition lookupField = deserializeFieldDefinition(lookupFieldNode);
            lookupFields.add(lookupField);
        }
        lookupDefinition.setLookupFields(lookupFields);

        List<FieldDefinition> resultFields = new ArrayList<>();
        for (JsonNode resultFieldNode : (ArrayNode)lookupDefinitionNode.get("resultFields")) {
            FieldDefinition resultField = deserializeFieldDefinition(resultFieldNode);
            resultFields.add(resultField);
        }
        lookupDefinition.setResultFields(resultFields);

        if (lookupDefinitionNode.has("resultSetLimit")) {
            lookupDefinition.setResultSetLimit(lookupDefinitionNode.get("resultSetLimit").getIntValue());
        }

        if (lookupDefinitionNode.has("multipleValuesResultSetLimit")) {
            lookupDefinition.setMultipleValuesResultSetLimit(lookupDefinitionNode.get("multipleValuesResultSetLimit").getIntValue());
        }
//        lookupDefinition.setExtraButtonSource(lookupDefinitionNode.get("extraButtonSource").getTextValue());
//        lookupDefinition.setExtraButtonParams(lookupDefinitionNode.get("extraButtonParams").getTextValue());
//        lookupDefinition.setSearchIconOverride(lookupDefinitionNode.get("searchIconOverride").getTextValue());
//        lookupDefinition.setNumOfColumns(lookupDefinitionNode.get("numOfColumns").getIntValue());
//        lookupDefinition.setHelpUrl(lookupDefinitionNode.get("helpUrl").getTextValue());
//        lookupDefinition.setTranslateCodes(lookupDefinitionNode.get("translateCodes").getBooleanValue());
//        lookupDefinition.setDisableSearchButtons(lookupDefinitionNode.get("disableSearchButtons").getBooleanValue());
        return lookupDefinition;
    }
    
    protected FieldDefinition deserializeFieldDefinition(JsonNode fieldDefinitionNode) {
        FieldDefinition fieldDef = new FieldDefinition();
        fieldDef.setAttributeName(fieldDefinitionNode.get("attributeName").getTextValue());
        fieldDef.setRequired(fieldDefinitionNode.get("required").getBooleanValue());
        fieldDef.setForceInquiry(fieldDefinitionNode.get("forceInquiry").getBooleanValue());
        fieldDef.setNoInquiry(fieldDefinitionNode.get("noInquiry").getBooleanValue());
        fieldDef.setNoDirectInquiry(fieldDefinitionNode.get("noDirectInquiry").getBooleanValue());
        fieldDef.setForceLookup(fieldDefinitionNode.get("forceLookup").getBooleanValue());
        fieldDef.setNoLookup(fieldDefinitionNode.get("noLookup").getBooleanValue());
        fieldDef.setUseShortLabel(fieldDefinitionNode.get("useShortLabel").getBooleanValue());
        fieldDef.setDefaultValue(fieldDefinitionNode.get("defaultValue").getTextValue());
        fieldDef.setQuickfinderParameterString(fieldDefinitionNode.get("quickfinderParameterString").getTextValue());

        if (fieldDefinitionNode.has("maxLength")) {
            fieldDef.setMaxLength(fieldDefinitionNode.get("maxLength").getIntValue());
        }

        fieldDef.setDisplayEditMode(fieldDefinitionNode.get("displayEditMode").getTextValue());

        fieldDef.setHidden(fieldDefinitionNode.get("hidden").getBooleanValue());
        fieldDef.setReadOnly(fieldDefinitionNode.get("readOnly").getBooleanValue());

        fieldDef.setTreatWildcardsAndOperatorsAsLiteral(fieldDefinitionNode.get("treatWildcardsAndOperatorsAsLiteral").getBooleanValue());

        fieldDef.setAlternateDisplayAttributeName(fieldDefinitionNode.get("alternateDisplayAttributeName").getTextValue());
        fieldDef.setAdditionalDisplayAttributeName(fieldDefinitionNode.get("additionalDisplayAttributeName").getTextValue());

        fieldDef.setTriggerOnChange(fieldDefinitionNode.get("triggerOnChange").getBooleanValue());
        fieldDef.setTotal(fieldDefinitionNode.get("total").getBooleanValue());
        return fieldDef;
    }
}
