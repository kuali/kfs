package org.kuali.kfs.sys.businessobject.datadictionary;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.*;
import org.kuali.rice.kns.datadictionary.control.ControlDefinitionBase;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.ExternalizableAttributeDefinitionProxy;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.IOException;
import java.util.Map;

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

        if (!ObjectUtils.isNull(entry.getInquiryDefinition())) {
            final ObjectNode inquiryDefinitionNode = serializeInquiryDefinition(entry.getInquiryDefinition());
            node.put("inquiryDefinition", inquiryDefinitionNode);
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
            if (ObjectUtils.isNotNull(control.isDatePicker())) {
                controlDefinitionNode.put("datePicker", control.isDatePicker());
            }
            if (ObjectUtils.isNotNull(control.isExpandedTextArea())) {
                controlDefinitionNode.put("expandedTextArea", control.isExpandedTextArea());
            }
            if (ObjectUtils.isNotNull(control.getValuesFinderClass())) {
                controlDefinitionNode.put("valuesFinderClass", control.getValuesFinderClass());
            }
            if (ObjectUtils.isNotNull(control.getKeyAttribute())) {
                controlDefinitionNode.put("keyAttribute", control.getKeyAttribute());
            }
            if (ObjectUtils.isNotNull(control.getLabelAttribute())) {
                controlDefinitionNode.put("labelAttribute", control.getLabelAttribute());
            }
            if (ObjectUtils.isNotNull(control.getIncludeBlankRow())) {
                controlDefinitionNode.put("includeBlankRow", control.getIncludeBlankRow());
            }
            if (ObjectUtils.isNotNull(control.getIncludeKeyInLabel())) {
                controlDefinitionNode.put("includeKeyInLabel", control.getIncludeKeyInLabel());
            }
            if (ObjectUtils.isNotNull(control.getSize())) {
                controlDefinitionNode.put("size", control.getSize());
            }
            if (ObjectUtils.isNotNull(control.getRows())) {
                controlDefinitionNode.put("rows", control.getRows());
            }
            if (ObjectUtils.isNotNull(control.getCols())) {
                controlDefinitionNode.put("cols", control.getCols());
            }
            controlDefinitionNode.put("ranged", control.isRanged());
            attributeNode.put("control", controlDefinitionNode);
        }
        return attributeNode;
    }

    protected ObjectNode serializeLookupDefinition(LookupDefinition lookupDefinition) {
        ObjectNode lookupNode = mapper.createObjectNode();
        lookupNode.put("title", lookupDefinition.getTitle());
        if (lookupDefinition.hasDefaultSort()) {
            ObjectNode sortDefinition = mapper.createObjectNode();
            sortDefinition.put("ascending", lookupDefinition.getDefaultSort().getSortAscending());
            ArrayNode sortAttributes = mapper.createArrayNode();
            for (String attributeName : lookupDefinition.getDefaultSort().getAttributeNames()) {
                sortAttributes.add(attributeName);
            }
            sortDefinition.put("attributeNames", sortAttributes);
            lookupNode.put("defaultSort", sortDefinition);
        }

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

        if (ObjectUtils.isNotNull(lookupDefinition.getResultSetLimit())) {
            lookupNode.put("resultSetLimit", lookupDefinition.getResultSetLimit());
        }
        if (ObjectUtils.isNotNull(lookupDefinition.getMultipleValuesResultSetLimit())) {
            lookupNode.put("multipleValuesResultSetLimit", lookupDefinition.getMultipleValuesResultSetLimit());
        }
        if (ObjectUtils.isNotNull(lookupDefinition.getExtraButtonSource())) {
            lookupNode.put("extraButtonSource", lookupDefinition.getExtraButtonSource());
        }
        if (ObjectUtils.isNotNull(lookupDefinition.getExtraButtonParams())) {
            lookupNode.put("extraButtonParams", lookupDefinition.getExtraButtonParams());
        }
        if (ObjectUtils.isNotNull(lookupDefinition.getSearchIconOverride())) {
            lookupNode.put("searchIconOverride", lookupDefinition.getSearchIconOverride());
        }
        if (ObjectUtils.isNotNull(lookupDefinition.getNumOfColumns())) {
            lookupNode.put("numOfColumns", lookupDefinition.getNumOfColumns());
        }
        if (ObjectUtils.isNotNull(lookupDefinition.getHelpUrl())) {
            lookupNode.put("helpUrl", lookupDefinition.getHelpUrl());
        }
        if (ObjectUtils.isNotNull(lookupDefinition.isTranslateCodes())) {
            lookupNode.put("translateCodes", lookupDefinition.isTranslateCodes());
        }
        if (ObjectUtils.isNotNull(lookupDefinition.isDisableSearchButtons())) {
            lookupNode.put("disableSearchButtons", lookupDefinition.isDisableSearchButtons());
        }
        return lookupNode;
    }

    protected ObjectNode serializeInquiryDefinition(InquiryDefinition inquiryDefinition) {
        ObjectNode lookupNode = mapper.createObjectNode();

        if (StringUtils.isNotBlank(inquiryDefinition.getTitle())) {
            lookupNode.put("title", inquiryDefinition.getTitle());
        }

        ArrayNode inquirySections = mapper.createArrayNode();
        for (InquirySectionDefinition inquirySectionDefinition : inquiryDefinition.getInquirySections()) {
            final ObjectNode inquirySectionDefinitionNode = serializeInquirySectionDefinition(inquirySectionDefinition);
            inquirySections.add(inquirySectionDefinitionNode);
        }
        lookupNode.put("inquirySections", inquirySections);

        if (ObjectUtils.isNotNull(inquiryDefinition.getInquirableClass())) {
            lookupNode.put("inquirableClass", inquiryDefinition.getInquirableClass().getName());
        }

        if (ObjectUtils.isNotNull(inquiryDefinition.getPresentationControllerClass())) {
            lookupNode.put("presentationControllerClass", inquiryDefinition.getPresentationControllerClass().getName());
        }

        if (ObjectUtils.isNotNull(inquiryDefinition.getAuthorizerClass())) {
            lookupNode.put("authorizerClass", inquiryDefinition.getAuthorizerClass().getName());
        }

        lookupNode.put("translateCodes", inquiryDefinition.isTranslateCodes());

        return lookupNode;
    }

    protected ObjectNode serializeInquirySectionDefinition(InquirySectionDefinition inquirySectionDefinition) {
        ObjectNode inquirySectionDefinitionNode = mapper.createObjectNode();

        if (StringUtils.isNotBlank(inquirySectionDefinition.getTitle())) {
            inquirySectionDefinitionNode.put("title", inquirySectionDefinition.getTitle());
        }

        ArrayNode inquiryFields = mapper.createArrayNode();
        for (FieldDefinition inquiryField : inquirySectionDefinition.getInquiryFields()) {
            final ObjectNode inquiryFieldNode = serializeField(inquiryField);
            inquiryFields.add(inquiryFieldNode);
        }
        inquirySectionDefinitionNode.put("inquiryFields", inquiryFields);

        ObjectNode inquiryCollectionsNode = mapper.createObjectNode();
        Map inquiryCollections = inquirySectionDefinition.getInquiryCollections();
        if (ObjectUtils.isNotNull(inquiryCollections)) {
            for (Object inquiryCollectionKey : inquiryCollections.keySet()) {
                final ObjectNode inquiryCollectionNode = serializeInquiryCollection((InquiryCollectionDefinition) inquiryCollections.get(inquiryCollectionKey));
                inquiryCollectionsNode.put(inquiryCollectionKey.toString(), inquiryCollectionNode);
            }
            inquirySectionDefinitionNode.put("inquiryCollections", inquiryCollectionsNode);
        }

        if (ObjectUtils.isNotNull(inquirySectionDefinition.getNumberOfColumns())) {
            inquirySectionDefinitionNode.put("numberOfColumns", inquirySectionDefinition.getNumberOfColumns());
        }

        inquirySectionDefinitionNode.put("defaultOpen", inquirySectionDefinition.isDefaultOpen());

        return inquirySectionDefinitionNode;
    }

    protected ObjectNode serializeInquiryCollection(InquiryCollectionDefinition inquiryCollectionDefinition) {
        ObjectNode inquiryCollectionDefinitionNode = mapper.createObjectNode();

        if (ObjectUtils.isNotNull(inquiryCollectionDefinition.getBusinessObjectClass())) {
            inquiryCollectionDefinitionNode.put("businessObjectClass", inquiryCollectionDefinition.getBusinessObjectClass().getName());
        }

        if (ObjectUtils.isNotNull(inquiryCollectionDefinition.getNumberOfColumns())) {
            inquiryCollectionDefinitionNode.put("numberOfColumns", inquiryCollectionDefinition.getNumberOfColumns());
        }

        ArrayNode inquiryFields = mapper.createArrayNode();
        for (FieldDefinition inquiryField : inquiryCollectionDefinition.getInquiryFields()) {
            final ObjectNode inquiryFieldNode = serializeField(inquiryField);
            inquiryFields.add(inquiryFieldNode);
        }
        inquiryCollectionDefinitionNode.put("inquiryFields", inquiryFields);

        ArrayNode inquiryCollections = mapper.createArrayNode();
        for (InquiryCollectionDefinition inquiryCollection : inquiryCollectionDefinition.getInquiryCollections()) {
            final ObjectNode inquiryCollectionNode = serializeInquiryCollection(inquiryCollection);
            inquiryCollections.add(inquiryCollectionNode);
        }
        inquiryCollectionDefinitionNode.put("inquiryCollections", inquiryCollections);

        ArrayNode summaryFields = mapper.createArrayNode();
        for (FieldDefinition summaryField : inquiryCollectionDefinition.getSummaryFields()) {
            final ObjectNode summaryFieldNode = serializeField(summaryField);
            summaryFields.add(summaryFieldNode);
        }
        inquiryCollectionDefinitionNode.put("summaryFields", summaryFields);

        if (ObjectUtils.isNotNull(inquiryCollectionDefinition.getSummaryTitle())) {
            inquiryCollectionDefinitionNode.put("summaryTitle", inquiryCollectionDefinition.getSummaryTitle());
        }

        return inquiryCollectionDefinitionNode;
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

        if (ObjectUtils.isNotNull(fieldDefinition.getMaxLength())) {
            fieldDefinitionNode.put("maxLength", fieldDefinition.getMaxLength());
        }

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
