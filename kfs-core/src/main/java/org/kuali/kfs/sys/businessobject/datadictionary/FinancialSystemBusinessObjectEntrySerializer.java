package org.kuali.kfs.sys.businessobject.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.datadictionary.LookupDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinitionBase;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.ExternalizableAttributeDefinitionProxy;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.IOException;

public class FinancialSystemBusinessObjectEntrySerializer extends JsonSerializer<FinancialSystemBusinessObjectEntry> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(FinancialSystemBusinessObjectEntry entry, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        ObjectNode node = mapper.createObjectNode();
        node.put("businessObjectClass", entry.getBusinessObjectClass().getName());
        node.put("titleAttribute", entry.getTitleAttribute());
        node.put("objectLabel", entry.getObjectLabel());

        ArrayNode attributes = mapper.createArrayNode();
        for (AttributeDefinition attributeDefinition : entry.getAttributes()) {
            if (!(attributeDefinition instanceof ExternalizableAttributeDefinitionProxy)) {
                attributes.add(serializeAttributeDefinition(attributeDefinition));
            }
        }
        node.put("attributes", attributes);

        if (!ObjectUtils.isNull(entry.getLookupDefinition())) {
            final ObjectNode lookupDefinitionNode = serializeLookupDefinition(entry.getLookupDefinition());
            node.put("lookupDefinition", lookupDefinitionNode);
        }
        jsonGenerator.writeTree(node);
    }

    protected ObjectNode serializeAttributeDefinition(AttributeDefinition attributeDefinition) {
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
            attributeNode.put("control", controlDefinitionNode);
        }
        return attributeNode;
    }

    protected ObjectNode serializeLookupDefinition(LookupDefinition lookupDefinition) {
        ObjectNode lookupNode = mapper.createObjectNode();
        lookupNode.put("title", lookupDefinition.getTitle());
        ObjectNode sortDefinition = mapper.createObjectNode();
        sortDefinition.put("ascending", lookupDefinition.getDefaultSort().getSortAscending());
        ArrayNode sortAttributes = mapper.createArrayNode();
        for (String attributeName : lookupDefinition.getDefaultSort().getAttributeNames()) {
            sortAttributes.add(attributeName);
        }
        sortDefinition.put("attributeNames", sortAttributes);
        lookupNode.put("defaultSort", sortDefinition);

        ArrayNode lookupFields = mapper.createArrayNode();
        for (FieldDefinition lookupField : lookupDefinition.getLookupFields()) {
            final ObjectNode lookupFieldNode = serializeField(lookupField);
            lookupFields.add(lookupFieldNode);
        }
        lookupNode.put("lookupFields", lookupFields);

        ArrayNode resultFields = mapper.createArrayNode();
        for (FieldDefinition resultField : lookupDefinition.getResultFields()) {
            final ObjectNode resultFieldNode = serializeField(resultField);
            resultFields.add(resultFieldNode);
        }
        lookupNode.put("resultFields", resultFields);

        lookupNode.put("resultSetLimit", lookupDefinition.getResultSetLimit());
        lookupNode.put("multipleValuesResultSetLimit", lookupDefinition.getMultipleValuesResultSetLimit());
        lookupNode.put("extraButtonSource", lookupDefinition.getExtraButtonSource());
        lookupNode.put("extraButtonParams", lookupDefinition.getExtraButtonParams());
        lookupNode.put("searchIconOverride", lookupDefinition.getSearchIconOverride());
        lookupNode.put("numOfColumns", lookupDefinition.getNumOfColumns());
        lookupNode.put("helpUrl", lookupDefinition.getHelpUrl());
        lookupNode.put("translateCodes", lookupDefinition.isTranslateCodes());
        lookupNode.put("disableSearchButtons", lookupDefinition.isDisableSearchButtons());
        return lookupNode;
    }

    protected ObjectNode serializeField(FieldDefinition fieldDefinition) {
        ObjectNode fieldDefinitionNode = mapper.createObjectNode();
        fieldDefinitionNode.put("attributeName", fieldDefinition.getAttributeName());
        fieldDefinitionNode.put("required", fieldDefinition.isRequired());
        fieldDefinitionNode.put("forceInquiry", fieldDefinition.isForceInquiry());
        fieldDefinitionNode.put("noInquiry", fieldDefinition.isNoInquiry());
        fieldDefinitionNode.put("noDirectInquiry", fieldDefinition.isNoDirectInquiry());
        fieldDefinitionNode.put("forceLookup", fieldDefinition.isForceLookup());
        fieldDefinitionNode.put("noLookup", fieldDefinition.isNoLookup());
        fieldDefinitionNode.put("useShortLabel", fieldDefinition.isUseShortLabel());
        fieldDefinitionNode.put("defaultValue", fieldDefinition.getDefaultValue());
        fieldDefinitionNode.put("quickfinderParameterString", fieldDefinition.getQuickfinderParameterString());

        fieldDefinitionNode.put("maxLength", fieldDefinition.getMaxLength());

        fieldDefinitionNode.put("displayEditMode", fieldDefinition.getDisplayEditMode());

        fieldDefinitionNode.put("hidden", fieldDefinition.isHidden());
        fieldDefinitionNode.put("readOnly", fieldDefinition.isReadOnly());

        fieldDefinitionNode.put("treatWildcardsAndOperatorsAsLiteral", fieldDefinition.isTreatWildcardsAndOperatorsAsLiteral());

        fieldDefinitionNode.put("alternateDisplayAttributeName", fieldDefinition.getAlternateDisplayAttributeName());
        fieldDefinitionNode.put("additionalDisplayAttributeName", fieldDefinition.getAdditionalDisplayAttributeName());

        fieldDefinitionNode.put("triggerOnChange", fieldDefinition.isTriggerOnChange());
        fieldDefinitionNode.put("total", fieldDefinition.isTotal());

        return fieldDefinitionNode;
    }

}
