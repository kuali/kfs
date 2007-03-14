/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.workflow.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.util.SpringServiceLocator;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.lookupable.Column;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleService;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateService;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;

/**
 * This class provides a lookup for org review hierarchy routing rules.
 * 
 * 
 */
public class OrgReviewLookupableImpl implements WorkflowLookupable {
    private Long ruleTemplateId;
    private WorkflowAttribute orgReviewAttribute;
    private List rows;
    private List columns;
    private static final String ORG_REVIEW_TEMPLATE_NAME = "";
    private static final String TITLE = "Org Review Lookup";
    private static final String RETURN_LOCATION = "Lookup.do";
    private static final String RULE_ID_FIELD_LABEL = "Rule Id";
    private static final String DOC_TYP_NAME_FIELD_LABEL = "Document Type Name";
    private static final String ACTIVE_IND_FIELD_LABEL = "Active Indicator";
    private static final String RESPONSIBLE_PARTY_FIELD_LABEL = "Responsible Party";
    private static final String RULE_ID_PROPERTY_NAME = "ruleId";
    private static final String DOC_TYP_NAME_PROPERTY_NAME = "documentTypeName";
    private static final String DOC_TYP_LOOKUPABLE = "DocumentTypeLookupableImplService";
    private static final String ACTIVE_IND_PROPERTY_NAME = "activeIndicator";
    private static final String RESPONSIBLE_PARTY_PROPERTY_NAME = "responsibleParty";
    private static final String ALL_ACTIVE_INDICATOR_VALUE = "ALL";

    /**
     * Constructs a OrgReviewLookupableImpl.java. Retrives the KualiOrgReviewTemplate from Spring, and initializes instance
     * variables based on it.
     */
    public OrgReviewLookupableImpl() {
        RuleTemplate ruleTemplate = ((RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE)).findByRuleTemplateName("KualiOrgReviewTemplate");
        this.ruleTemplateId = ruleTemplate.getRuleTemplateId();
        this.orgReviewAttribute = ruleTemplate.getRuleTemplateAttribute(0).getWorkflowAttribute();
        this.orgReviewAttribute.setRequired(false);
        setRows();
        setColumns();
    }

    /**
     * This method initializes the search criteria rows.
     */
    private void setRows() {
        rows = new ArrayList();
        List documentTypeFields = new ArrayList();
        documentTypeFields.add(new Field(DOC_TYP_NAME_FIELD_LABEL, "", Field.TEXT, true, DOC_TYP_NAME_PROPERTY_NAME, "", null, DOC_TYP_LOOKUPABLE));
        documentTypeFields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, DOC_TYP_LOOKUPABLE));
        rows.add(new Row(documentTypeFields));
        List activeIndicatorOptions = new ArrayList();
        activeIndicatorOptions.add(new KeyLabelPair(Boolean.TRUE.toString(), "Active"));
        activeIndicatorOptions.add(new KeyLabelPair(Boolean.FALSE.toString(), "Inactive"));
        activeIndicatorOptions.add(new KeyLabelPair(ALL_ACTIVE_INDICATOR_VALUE, "Show All"));
        List activeIndicatorFields = new ArrayList();
        activeIndicatorFields.add(new Field(ACTIVE_IND_FIELD_LABEL, "", Field.RADIO, false, ACTIVE_IND_PROPERTY_NAME, Boolean.TRUE.toString(), activeIndicatorOptions, null));
        rows.add(new Row(activeIndicatorFields));
        rows.addAll(orgReviewAttribute.getRuleRows());
    }

    /**
     * This method initializes the result set columns.
     */
    private void setColumns() {
        columns = new ArrayList();
        columns.add(new Column(RULE_ID_FIELD_LABEL, Boolean.TRUE.toString(), RULE_ID_PROPERTY_NAME));
        columns.add(new Column(DOC_TYP_NAME_FIELD_LABEL, Boolean.TRUE.toString(), DOC_TYP_NAME_PROPERTY_NAME));
        columns.add(new Column(ACTIVE_IND_FIELD_LABEL, Boolean.TRUE.toString(), ACTIVE_IND_PROPERTY_NAME));
        Iterator extendedAttributeRowItr = orgReviewAttribute.getRuleRows().iterator();
        while (extendedAttributeRowItr.hasNext()) {
            Row row = (Row) extendedAttributeRowItr.next();
            Iterator fieldItr = row.getFields().iterator();
            while (fieldItr.hasNext()) {
                Field field = (Field) fieldItr.next();
                if (Field.TEXT.equals(field.getFieldType())) {
                    columns.add(new Column(field.getFieldLabel(), Boolean.TRUE.toString(), field.getPropertyName()));
                }
            }

        }
        columns.add(new Column(RESPONSIBLE_PARTY_FIELD_LABEL, Boolean.TRUE.toString(), RESPONSIBLE_PARTY_PROPERTY_NAME));
    }

    /**
     * This method uses edu.iu.uis.eden.routetemplate.RuleService#search(java.lang.String docTypeName, java.lang.Long ruleId,
     * java.lang.Long ruleTemplateId, java.lang.String ruleDescription, java.lang.Long workgroupId, java.lang.String workflowId,
     * java.lang.String roleName, java.lang.Boolean delegateRule, java.lang.Boolean activeInd, java.util.Map extensionValues) to
     * retrieve a List of rules based on the search criteria specified, then converts the result set into OrgReviewLookupableResults
     * and returns those.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getSearchResults(java.util.Map, java.util.Map)
     */
    public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception {
        List errors = new ArrayList();
        String docTypeNameParam = (String) fieldValues.get(DOC_TYP_NAME_PROPERTY_NAME);
        String activeParam = (String) fieldValues.get(ACTIVE_IND_PROPERTY_NAME);
        String docTypeSearchName = null;
        Boolean isActive = null;
        if (!activeParam.equals(ALL_ACTIVE_INDICATOR_VALUE)) {
            isActive = new Boolean(activeParam);
        }
        if (docTypeNameParam != null && !"".equals(docTypeNameParam.trim())) {
            docTypeSearchName = docTypeNameParam.replace('*', '%');
            docTypeSearchName = "%" + docTypeSearchName.trim() + "%";
        }
        Map attributes = new HashMap();
        Iterator attributeRowItr = orgReviewAttribute.getRuleRows().iterator();
        while (attributeRowItr.hasNext()) {
            Row attributeRow = (Row) attributeRowItr.next();
            Iterator attributeFieldItr = attributeRow.getFields().iterator();
            while (attributeFieldItr.hasNext()) {
                Field attributeField = (Field) attributeFieldItr.next();
                if (fieldValues.get(attributeField.getPropertyName()) != null) {
                    String attributeValue = (String) fieldValues.get(attributeField.getPropertyName());
                    if (!StringUtils.isBlank(attributeValue)) {
                        attributes.put(attributeField.getPropertyName(), attributeValue.trim());
                    }
                }
            }
        }
        Iterator rules = ((RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE)).search(docTypeSearchName, null, ruleTemplateId, null, null, null, null, null, isActive, attributes).iterator();
        List displayList = new ArrayList();
        while (rules.hasNext()) {
            displayList.add(new OrgReviewLookupableResult((RuleBaseValues) rules.next()));
        }
        return displayList;
    }

    /**
     * @see edu.iu.uis.eden.EdenConstants.RULE_SEARCH_INSTRUCTION_KEY
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getLookupInstructions()
     */
    public String getLookupInstructions() {
        return Utilities.getApplicationConstant(EdenConstants.RULE_SEARCH_INSTRUCTION_KEY);
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getTitle()
     */
    public String getTitle() {
        return TITLE;
    }

    /**
     * Since this is a lookup, we return Lookup.do.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getReturnLocation()
     */
    public String getReturnLocation() {
        return RETURN_LOCATION;
    }

    /**
     * Returns unodified instance variable initialized in setColumns(), called from the constructor.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getColumns()
     */
    public List getColumns() {
        return columns;
    }

    /**
     * Returns unmodified instance variable initialized in setRows(), called from the constructor.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getRows()
     */
    public List getRows() {
        return rows;
    }

    /**
     * Returns the empty String.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getHtmlMenuBar()
     */
    public String getHtmlMenuBar() {
        return "";
    }

    /**
     * Returns the empty String.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getNoReturnParams(java.util.Map)
     */
    public String getNoReturnParams(Map fieldConversions) {
        return "";
    }

    /**
     * Returns an empty List.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getDefaultReturnType()
     */
    public List getDefaultReturnType() {
        return new ArrayList();
    }

    /**
     * Does nothing and returns false.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#checkForAdditionalFields(java.util.Map,
     *      javax.servlet.http.HttpServletRequest)
     */
    public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
        return false;
    }

    /**
     * Does nothing.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#changeIdToName(java.util.Map)
     */
    public void changeIdToName(Map fieldValues) {
    }
}