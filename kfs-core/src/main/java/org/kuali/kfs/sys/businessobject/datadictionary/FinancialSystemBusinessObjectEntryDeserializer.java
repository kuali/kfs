package org.kuali.kfs.sys.businessobject.datadictionary;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.*;
import org.kuali.rice.kns.datadictionary.control.ControlDefinitionBase;
import org.kuali.rice.kns.datadictionary.control.ControlDefinitionType;
import org.kuali.rice.kns.inquiry.InquiryAuthorizer;
import org.kuali.rice.kns.inquiry.InquiryPresentationController;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.SortDefinition;
import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.IOException;
import java.util.*;

public class FinancialSystemBusinessObjectEntryDeserializer extends JsonDeserializer<FinancialSystemBusinessObjectEntry> {
    @Override
    public FinancialSystemBusinessObjectEntry deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            FinancialSystemBusinessObjectEntry businessObjectEntry = new FinancialSystemBusinessObjectEntry();
            ObjectCodec codec = jp.getCodec();
            JsonNode entryRoot = codec.readTree(jp);

            businessObjectEntry.setBusinessObjectClass((Class<? extends BusinessObject>) (Class.forName(entryRoot.get("businessObjectClass").textValue())));
            businessObjectEntry.setTitleAttribute(entryRoot.get("titleAttribute").textValue());
            businessObjectEntry.setObjectLabel(entryRoot.get("objectLabel").textValue());

            deserializeAttributes(businessObjectEntry, entryRoot.get("attributes").elements());

            if (entryRoot.has("lookupDefinition")) {
                LookupDefinition lookupDefinition = deserializeLookupDefinition(entryRoot.get("lookupDefinition"));
                businessObjectEntry.setLookupDefinition(lookupDefinition);
            }

            if (entryRoot.has("inquiryDefinition")) {
                InquiryDefinition inquiryDefinition = deserializeInquiryDefinition(entryRoot.get("inquiryDefinition"));
                businessObjectEntry.setInquiryDefinition(inquiryDefinition);
            }

            return businessObjectEntry;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void deserializeAttributes(FinancialSystemBusinessObjectEntry entry, Iterator<JsonNode> attributes) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        while (attributes.hasNext()) {
            AttributeDefinition attributeDefinition = new AttributeDefinition();
            JsonNode nextAttr = attributes.next();
            attributeDefinition.setName(nextAttr.get("name").textValue());
            attributeDefinition.setLabel(nextAttr.get("label").textValue());
            attributeDefinition.setShortLabel(nextAttr.get("shortLabel").textValue());
            attributeDefinition.setConstraintText(nextAttr.get("constraintText").textValue());
            attributeDefinition.setRequired(nextAttr.get("required").booleanValue());
            attributeDefinition.setForceUppercase(nextAttr.get("forceUppercase").booleanValue());
            if (!ObjectUtils.isNull(attributeDefinition.getMinLength())) {
                attributeDefinition.setMinLength(nextAttr.get("minLength").intValue());
            }
            if (!ObjectUtils.isNull(attributeDefinition.getMaxLength())) {
                attributeDefinition.setMaxLength(nextAttr.get("maxLength").intValue());
            }
            if (!ObjectUtils.isNull(attributeDefinition.getUnique())) {
                attributeDefinition.setUnique(nextAttr.get("unique").booleanValue());
            }
            if (!StringUtils.isBlank(attributeDefinition.getExclusiveMin())) {
                attributeDefinition.setExclusiveMin(nextAttr.get("exclusiveMin").textValue());
            }
            if (!StringUtils.isBlank(attributeDefinition.getInclusiveMax())) {
                attributeDefinition.setInclusiveMax(nextAttr.get("inclusiveMax").textValue());
            }

            JsonNode controlNode = nextAttr.get("control");
            if (controlNode != null) {
                ControlDefinitionBase controlDefinition = new ControlDefinitionBase();
                controlDefinition.setType(ControlDefinitionType.valueOf(controlNode.get("type").textValue()));
                controlDefinition.setDatePicker(controlNode.get("datePicker").asBoolean());
                controlDefinition.setExpandedTextArea(controlNode.get("expandedTextArea").asBoolean());
                if (controlNode.has("valuesFinderClass")) {
                    controlDefinition.setValuesFinderClass(controlNode.get("valuesFinderClass").textValue());
                }
                if (controlNode.has("keyAttribute")) {
                    controlDefinition.setKeyAttribute(controlNode.get("keyAttribute").textValue());
                }
                if (controlNode.has("labelAttribute")) {
                    controlDefinition.setLabelAttribute(controlNode.get("labelAttribute").textValue());
                }
                if (controlNode.has("includeBlankRow")) {
                    controlDefinition.setIncludeBlankRow(controlNode.get("includeBlankRow").booleanValue());
                }
                if (controlNode.has("includeKeyInLabel")) {
                    controlDefinition.setIncludeKeyInLabel(controlNode.get("includeKeyInLabel").booleanValue());
                }
                if (controlNode.has("size")) {
                    controlDefinition.setSize(controlNode.get("size").asInt());
                }
                if (controlNode.has("rows")) {
                    controlDefinition.setRows(controlNode.get("rows").asInt());
                }
                if (controlNode.has("cols")) {
                    controlDefinition.setCols(controlNode.get("cols").asInt());
                }
                controlDefinition.setRanged(controlNode.get("ranged").asBoolean());
                attributeDefinition.setControl(controlDefinition);
            }
            attributeDefinitions.add(attributeDefinition);
        }
        entry.setAttributes(attributeDefinitions);
    }

    protected LookupDefinition deserializeLookupDefinition(JsonNode lookupDefinitionNode) {
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setTitle(lookupDefinitionNode.get("title").textValue());

        if (lookupDefinitionNode.has("defaultSort")) {
            SortDefinition defaultSort = new SortDefinition();
            JsonNode defaultSortNode = lookupDefinitionNode.get("defaultSort");
            defaultSort.setSortAscending(defaultSortNode.get("ascending").booleanValue());
            ArrayNode sortAttributeNames = (ArrayNode) defaultSortNode.get("attributeNames");
            List<String> attributeNames = new ArrayList<>();
            for (JsonNode attributeNameNode : sortAttributeNames) {
                attributeNames.add(attributeNameNode.textValue());
            }
            defaultSort.setAttributeNames(attributeNames);
            lookupDefinition.setDefaultSort(defaultSort);
        }

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
            lookupDefinition.setResultSetLimit(lookupDefinitionNode.get("resultSetLimit").intValue());
        }
        if (lookupDefinitionNode.has("multipleValuesResultSetLimit")) {
            lookupDefinition.setMultipleValuesResultSetLimit(lookupDefinitionNode.get("multipleValuesResultSetLimit").intValue());
        }
        if (lookupDefinitionNode.has("extraButtonSource")) {
            lookupDefinition.setExtraButtonSource(lookupDefinitionNode.get("extraButtonSource").textValue());
        }
        if (lookupDefinitionNode.has("extraButtonParams")) {
            lookupDefinition.setExtraButtonParams(lookupDefinitionNode.get("extraButtonParams").textValue());
        }
        if (lookupDefinitionNode.has("searchIconOverride")) {
            lookupDefinition.setSearchIconOverride(lookupDefinitionNode.get("searchIconOverride").textValue());
        }
        if (lookupDefinitionNode.has("numOfColumns")) {
            lookupDefinition.setNumOfColumns(lookupDefinitionNode.get("numOfColumns").intValue());
        }
        if (lookupDefinitionNode.has("helpUrl")) {
            lookupDefinition.setHelpUrl(lookupDefinitionNode.get("helpUrl").textValue());
        }
        if (lookupDefinitionNode.has("translateCodes")) {
            lookupDefinition.setTranslateCodes(lookupDefinitionNode.get("translateCodes").booleanValue());
        }
        if (lookupDefinitionNode.has("disableSearchButtons")) {
            lookupDefinition.setDisableSearchButtons(lookupDefinitionNode.get("disableSearchButtons").booleanValue());
        }
        return lookupDefinition;
    }

    protected InquiryDefinition deserializeInquiryDefinition(JsonNode inquiryDefinitionNode) {
        InquiryDefinition inquiryDefinition = new InquiryDefinition();

        if (inquiryDefinitionNode.has("title")) {
            inquiryDefinition.setTitle(inquiryDefinitionNode.get("title").textValue());
        }

        List<InquirySectionDefinition> inquirySectionDefinitions = new ArrayList<>();
        for (JsonNode inquirySectionNode : inquiryDefinitionNode.get("inquirySections")) {
            InquirySectionDefinition inquirySection = deserializeInquirySectionDefinition(inquirySectionNode);
            inquirySectionDefinitions.add(inquirySection);
        }
        inquiryDefinition.setInquirySections(inquirySectionDefinitions);

        try {
            if (inquiryDefinitionNode.has("inquirableClass")) {
                inquiryDefinition.setInquirableClass((Class<? extends Inquirable>) Class.forName(inquiryDefinitionNode.get("inquirableClass").textValue()));
            }

            if (inquiryDefinitionNode.has("presentationControllerClass")) {
                inquiryDefinition.setPresentationControllerClass((Class<? extends InquiryPresentationController>) Class.forName(inquiryDefinitionNode.get("presentationControllerClass").textValue()));
            }

            if (inquiryDefinitionNode.has("authorizerClass")) {
                inquiryDefinition.setAuthorizerClass((Class<? extends InquiryAuthorizer>) Class.forName(inquiryDefinitionNode.get("authorizerClass").textValue()));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (inquiryDefinitionNode.has("translateCodes")) {
            inquiryDefinition.setTranslateCodes(inquiryDefinitionNode.get("translateCodes").booleanValue());
        }

        return inquiryDefinition;
    }

    private InquirySectionDefinition deserializeInquirySectionDefinition(JsonNode inquirySectionNode) {
        InquirySectionDefinition inquirySectionDefinition = new InquirySectionDefinition();

        if (inquirySectionNode.has("title")) {
            inquirySectionDefinition.setTitle(inquirySectionNode.get("title").textValue());
        }

        List<FieldDefinition> inquiryFields = new ArrayList<>();
        for (JsonNode inquiryFieldNode : inquirySectionNode.get("inquiryFields")) {
            FieldDefinition inquiryField = deserializeFieldDefinition(inquiryFieldNode);
            inquiryFields.add(inquiryField);
        }
        inquirySectionDefinition.setInquiryFields(inquiryFields);

        Map<String, InquiryCollectionDefinition> inquiryCollectionDefinitions = new HashMap<>();
        if (inquirySectionNode.has("inquiryCollections")) {
            for (JsonNode inquiryCollectionNode : inquirySectionNode.get("inquiryCollections")) {
                InquiryCollectionDefinition inquiryCollectionDefinition = deserializeInquiryCollectionDefinition(inquiryCollectionNode.get(1));
                inquiryCollectionDefinitions.put(inquiryCollectionNode.get(0).asText(), inquiryCollectionDefinition);
            }
            inquirySectionDefinition.setInquiryCollections(inquiryCollectionDefinitions);
        }

        if (inquirySectionNode.has("numberOfColumns")) {
            inquirySectionDefinition.setNumberOfColumns(inquirySectionNode.get("numberOfColumns").intValue());
        }

        if (inquirySectionNode.has("defaultOpen")) {
            inquirySectionDefinition.setDefaultOpen(inquirySectionNode.get("defaultOpen").booleanValue());
        }

        return inquirySectionDefinition;
    }

    protected InquiryCollectionDefinition deserializeInquiryCollectionDefinition(JsonNode inquiryCollectionDefinitionNode) {
        InquiryCollectionDefinition inquiryCollectionDefinition = new InquiryCollectionDefinition();

        try {
            if (inquiryCollectionDefinitionNode.has("businessObjectClass")) {
                inquiryCollectionDefinition.setBusinessObjectClass((Class<? extends BusinessObject>) Class.forName(inquiryCollectionDefinitionNode.get("businessObjectClass").textValue()));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (inquiryCollectionDefinitionNode.has("numberOfColumns")) {
            inquiryCollectionDefinition.setNumberOfColumns(inquiryCollectionDefinitionNode.get("numberOfColumns").intValue());
        }

        List<FieldDefinition> inquiryFields = new ArrayList<>();
        for (JsonNode inquiryFieldNode : inquiryCollectionDefinitionNode.get("inquiryFields")) {
            FieldDefinition inquiryField = deserializeFieldDefinition(inquiryFieldNode);
            inquiryFields.add(inquiryField);
        }
        inquiryCollectionDefinition.setInquiryFields(inquiryFields);

        List<InquiryCollectionDefinition> inquiryCollections = new ArrayList<>();
        for (JsonNode inquiryCollectionNode : inquiryCollectionDefinitionNode.get("inquiryCollections")) {
            InquiryCollectionDefinition inquiryCollection = deserializeInquiryCollectionDefinition(inquiryCollectionNode);
            inquiryFields.add(inquiryCollection);
        }
        inquiryCollectionDefinition.setInquiryCollections(inquiryCollections);

        List<FieldDefinition> summaryFields = new ArrayList<>();
        for (JsonNode summaryFieldNode : inquiryCollectionDefinitionNode.get("summaryFields")) {
            FieldDefinition summaryField = deserializeFieldDefinition(summaryFieldNode);
            inquiryFields.add(summaryField);
        }
        inquiryCollectionDefinition.setSummaryFields(summaryFields);

        if (inquiryCollectionDefinitionNode.has("summaryTitle")) {
            inquiryCollectionDefinition.setSummaryTitle(inquiryCollectionDefinitionNode.get("summaryTitle").textValue());
        }

        return inquiryCollectionDefinition;
    }

    protected FieldDefinition deserializeFieldDefinition(JsonNode fieldDefinitionNode) {
        FieldDefinition fieldDef = new FieldDefinition();
        fieldDef.setAttributeName(fieldDefinitionNode.get("attributeName").textValue());
        fieldDef.setRequired(fieldDefinitionNode.get("required").booleanValue());
        fieldDef.setForceInquiry(fieldDefinitionNode.get("forceInquiry").booleanValue());
        fieldDef.setNoInquiry(fieldDefinitionNode.get("noInquiry").booleanValue());
        fieldDef.setNoDirectInquiry(fieldDefinitionNode.get("noDirectInquiry").booleanValue());
        fieldDef.setForceLookup(fieldDefinitionNode.get("forceLookup").booleanValue());
        fieldDef.setNoLookup(fieldDefinitionNode.get("noLookup").booleanValue());
        fieldDef.setUseShortLabel(fieldDefinitionNode.get("useShortLabel").booleanValue());
        fieldDef.setDefaultValue(fieldDefinitionNode.get("defaultValue").textValue());
        fieldDef.setQuickfinderParameterString(fieldDefinitionNode.get("quickfinderParameterString").textValue());

        if (fieldDefinitionNode.has("maxLength")) {
            fieldDef.setMaxLength(fieldDefinitionNode.get("maxLength").intValue());
        }

        fieldDef.setDisplayEditMode(fieldDefinitionNode.get("displayEditMode").textValue());

        fieldDef.setHidden(fieldDefinitionNode.get("hidden").booleanValue());
        fieldDef.setReadOnly(fieldDefinitionNode.get("readOnly").booleanValue());

        fieldDef.setTreatWildcardsAndOperatorsAsLiteral(fieldDefinitionNode.get("treatWildcardsAndOperatorsAsLiteral").booleanValue());

        fieldDef.setAlternateDisplayAttributeName(fieldDefinitionNode.get("alternateDisplayAttributeName").textValue());
        fieldDef.setAdditionalDisplayAttributeName(fieldDefinitionNode.get("additionalDisplayAttributeName").textValue());

        fieldDef.setTriggerOnChange(fieldDefinitionNode.get("triggerOnChange").booleanValue());
        fieldDef.setTotal(fieldDefinitionNode.get("total").booleanValue());
        return fieldDef;
    }
}
