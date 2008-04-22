/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.workflow.module.purap.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Campus;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.lookup.keyvalues.CampusValuesFinder;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.RestrictedMaterial;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.vendor.bo.CommodityCode;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.MassRuleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.Utilities;

public class KualiPurApCommodityCodeAttribute implements WorkflowAttribute, MassRuleAttribute {

    public static final String DLVY_CMP_CD = "dlvy_cmp_cd";
    
    public static final String PUR_COMM_CD = "pur_comm_cd";
    
    public static final String RSTRC_MTRL_CD = "rstrc_mtrl_cd";
    
    private static final String DOCUMENT_COMMODITY_CODE_VALUES_KEY = "commodityCodes";
    
    private static final String DOCUMENT_DELIVERY_CAMPUS_VALUE_KEY = "deliveryCampus";
    
    // defined here so field creation matches rule errors
    private static final Class COMMODITY_CODE_FIELD_CLASS = CommodityCode.class;
    private static final Class RESTRICTED_MATERIAL_FIELD_CLASS = RestrictedMaterial.class;
    private static final String PURCHASING_COMMODITY_CODE_FIELD_PROPERTY = PurapPropertyConstants.ITEM_COMMODITY_CODE;
    private static final String RESTRICTED_MATERIAL_CODE_FIELD_PROPERTY = PurapPropertyConstants.RESTRICTED_MATERIAL_CODE;
    private static final Class DELIVERY_CAMPUS_CLASS = RequisitionDocument.class;
    private static final String DELIVERY_CAMPUS_CODE_PROPERTY = PurapPropertyConstants.DELIVERY_CAMPUS_CODE;
    
    private List ruleRows;
    private List routingDataRows;
    
    private boolean required;
    private String deliveryCampusCode;
    private String purchasingCommodityCode;
    private String restrictedMaterialCode;
    
    public KualiPurApCommodityCodeAttribute() {
        ruleRows = new ArrayList();
        Map<String, String> campusMap  = (new CampusValuesFinder()).getKeyLabelMap();
        ruleRows.add(KualiWorkflowUtils.buildDropdownRow(DELIVERY_CAMPUS_CLASS, DELIVERY_CAMPUS_CODE_PROPERTY, DLVY_CMP_CD, campusMap, false));
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(COMMODITY_CODE_FIELD_CLASS, PURCHASING_COMMODITY_CODE_FIELD_PROPERTY, PUR_COMM_CD));
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(RESTRICTED_MATERIAL_FIELD_CLASS, RESTRICTED_MATERIAL_CODE_FIELD_PROPERTY, RSTRC_MTRL_CD));

        routingDataRows = new ArrayList();
        routingDataRows.add(KualiWorkflowUtils.buildDropdownRow(DELIVERY_CAMPUS_CLASS, DELIVERY_CAMPUS_CODE_PROPERTY, DLVY_CMP_CD, campusMap, false));
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(COMMODITY_CODE_FIELD_CLASS, PURCHASING_COMMODITY_CODE_FIELD_PROPERTY, PUR_COMM_CD));
    }
    
    public String getDeliveryCampusCode() {
        return deliveryCampusCode;
    }

    public void setDeliveryCampusCode(String deliveryCampusCode) {
        this.deliveryCampusCode = deliveryCampusCode;
    }

    public String getPurchasingCommodityCode() {
        return purchasingCommodityCode;
    }

    public void setPurchasingCommodityCode(String purchasingCommodityCode) {
        this.purchasingCommodityCode = purchasingCommodityCode;
    }

    public String getRestrictedMaterialCode() {
        return restrictedMaterialCode;
    }

    public void setRestrictedMaterialCode(String restrictedMaterialCode) {
        this.restrictedMaterialCode = restrictedMaterialCode;
    }

    public void setRuleRows(List ruleRows) {
        this.ruleRows = ruleRows;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getDeliveryCampusCode()) && Utilities.isEmpty(getPurchasingCommodityCode()) && Utilities.isEmpty(getRestrictedMaterialCode())) {
            return "";
        }
        StringBuffer deliveryCampusCode = new StringBuffer().append("<" + PurapPropertyConstants.DELIVERY_CAMPUS_CODE + ">").append(getDeliveryCampusCode()).append("</" + PurapPropertyConstants.DELIVERY_CAMPUS_CODE + ">");
        StringBuffer purchasingCommodityCode = new StringBuffer().append("<" + PurapPropertyConstants.ITEM_COMMODITY_CODE+ ">").append(getPurchasingCommodityCode()).append("</" + PurapPropertyConstants.ITEM_COMMODITY_CODE + ">");
        StringBuffer restrictedMaterialCode = new StringBuffer().append("<" + PurapPropertyConstants.RESTRICTED_MATERIAL_CODE+ ">").append(getRestrictedMaterialCode()).append("</" + PurapPropertyConstants.RESTRICTED_MATERIAL_CODE + ">");
        return new StringBuffer(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_PREFIX).append(deliveryCampusCode).append(purchasingCommodityCode).append(restrictedMaterialCode).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_SUFFIX).toString();
    }

    public List<Row> getRoutingDataRows() {
        return routingDataRows;
    }

    public List<RuleExtensionValue> getRuleExtensionValues() {
        List extensions = new ArrayList();
        extensions.add(new RuleExtensionValue(DLVY_CMP_CD, getDeliveryCampusCode()));
        if (StringUtils.isNotBlank(getPurchasingCommodityCode())) {
            extensions.add(new RuleExtensionValue(PUR_COMM_CD, getPurchasingCommodityCode()));
        }
        if (StringUtils.isNotBlank(getRestrictedMaterialCode())) {
            extensions.add(new RuleExtensionValue(RSTRC_MTRL_CD, getRestrictedMaterialCode()));
        }
        return extensions;
    }

    public List<Row> getRuleRows() {
        return ruleRows;
    }

    public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
        return true;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List validateRoutingData(Map paramMap) {
        return validateCommodityCodeAttributeValues(paramMap);
    }

    public List validateRuleData(Map paramMap) {
        return validateCommodityCodeAttributeValues(paramMap);
    }

    private List validateCommodityCodeAttributeValues(Map paramMap) {
        setDeliveryCampusCode(LookupUtils.forceUppercase(DELIVERY_CAMPUS_CLASS, PurapPropertyConstants.DELIVERY_CAMPUS_CODE, (String) paramMap.get(DLVY_CMP_CD)));
        setPurchasingCommodityCode(LookupUtils.forceUppercase(COMMODITY_CODE_FIELD_CLASS, PurapPropertyConstants.ITEM_COMMODITY_CODE, (String) paramMap.get(PUR_COMM_CD)));
        setRestrictedMaterialCode(LookupUtils.forceUppercase(COMMODITY_CODE_FIELD_CLASS, PurapPropertyConstants.RESTRICTED_MATERIAL_CODE, (String)paramMap.get(RSTRC_MTRL_CD)));
        List errors = new ArrayList();
        
        if (!doesDeliveryCampusExist()) {
            // Delivery Campus Code must exists in the database
            String error = KualiWorkflowUtils.getBusinessObjectAttributeLabel(DELIVERY_CAMPUS_CLASS, DELIVERY_CAMPUS_CODE_PROPERTY) + " must exists in the database. ";
            errors.add(new WorkflowServiceErrorImpl(error, "routetemplate.xmlattribute.error", error));
        }
//        if (StringUtils.isNotBlank(getPurchasingCommodityCode())) {
//            if (!doesCommodityCodeExist()) {
//                // Commodity Code must exists in the database
//                String error = KualiWorkflowUtils.getBusinessObjectAttributeLabel(COMMODITY_CODE_FIELD_CLASS, PURCHASING_COMMODITY_CODE_FIELD_PROPERTY) + " must exists in the database. ";
//                errors.add(new WorkflowServiceErrorImpl(error, "routetemplate.xmlattribute.error", error));
//            }
//        }
        if (StringUtils.isNotBlank(getRestrictedMaterialCode())) {
            if (!doesRestrictedMaterialCodeExist()) {
                // Restricted Material Code must exists in the database
                String error = KualiWorkflowUtils.getBusinessObjectAttributeLabel(COMMODITY_CODE_FIELD_CLASS, RESTRICTED_MATERIAL_CODE_FIELD_PROPERTY) + " must exists in the database. ";
                errors.add(new WorkflowServiceErrorImpl(error, "routetemplate.xmlattribute.error", error));
            }
        }
        return errors;
    }
    
    private boolean doesDeliveryCampusExist() {
        Map fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.CAMPUS_CODE, getDeliveryCampusCode());
        int count = SpringContext.getBean(BusinessObjectService.class).countMatching(Campus.class, fieldValues);
        if (count > 0) {
            return true;
        }
        else {
            return false;
        }
    }
    
//    private boolean doesCommodityCodeExist() {
//        Map fieldValues = new HashMap<String, String>();
//        fieldValues.put(PurapPropertyConstants.ITEM_COMMODITY_CODE, getPurchasingCommodityCode());
//        int count = SpringContext.getBean(BusinessObjectService.class).countMatching(COMMODITY_CODE_FIELD_CLASS, fieldValues);
//        if (count > 0) {
//            return true;
//        }
//        else {
//            return false;
//        }            
//    }
    
    private boolean doesRestrictedMaterialCodeExist() {
        Map fieldValues = new HashMap<String, String>();
        fieldValues.put(PurapPropertyConstants.RESTRICTED_MATERIAL_CODE, getRestrictedMaterialCode());
        int count = SpringContext.getBean(BusinessObjectService.class).countMatching(RestrictedMaterial.class, fieldValues);
        if (count > 0) {
            return true;
        }
        else {
            return false;
        }            
    }
    
    public List filterNonMatchingRules(RouteContext routeContext, List rules) {
        List filteredRules = new ArrayList();
        DocumentType documentType = routeContext.getDocument().getDocumentType();
        Set commodityCodeValues = populateCommodityCodesFromDocContent(documentType.getName(), routeContext.getDocumentContent(), routeContext);
        Campus deliveryCampusValue = populateDeliveryCampusFromDocContent(documentType.getName(), routeContext.getDocumentContent(), routeContext);
        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iterator.next();
            List ruleExtensions = rule.getRuleExtensions();
            setDeliveryCampusCode(LookupUtils.forceUppercase(DELIVERY_CAMPUS_CLASS, PurapPropertyConstants.DELIVERY_CAMPUS_CODE, getRuleExtentionValue(DLVY_CMP_CD, ruleExtensions)));
            setPurchasingCommodityCode(LookupUtils.forceUppercase(COMMODITY_CODE_FIELD_CLASS, PurapPropertyConstants.ITEM_COMMODITY_CODE, getRuleExtentionValue(PUR_COMM_CD, ruleExtensions)));
            setRestrictedMaterialCode(LookupUtils.forceUppercase(RESTRICTED_MATERIAL_FIELD_CLASS, PurapPropertyConstants.RESTRICTED_MATERIAL_CODE, getRuleExtentionValue(RSTRC_MTRL_CD, ruleExtensions)));
            if (ruleMatches(commodityCodeValues, deliveryCampusValue)) {
                filteredRules.add(rule);
            }
        }
        return filteredRules;
    }

    private String getRuleExtentionValue(String key, List ruleExtensions) {
        for (Iterator iter = ruleExtensions.iterator(); iter.hasNext();) {
            RuleExtension extension = (RuleExtension) iter.next();
            if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(this.getClass().getName())) {
                for (Iterator iterator = extension.getExtensionValues().iterator(); iterator.hasNext();) {
                    RuleExtensionValue value = (RuleExtensionValue) iterator.next();
                    if (value.getKey().equals(key)) {
                        return value.getValue();
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Determines if the given Rule matches the document data by comparing the values.
     */
    protected boolean ruleMatches(Set commodityCodeValues, Campus deliveryCampusValue) {
        if (!StringUtils.equals(deliveryCampusValue.getCampusCode(), getDeliveryCampusCode())) {
            return false;
        }
        for (Iterator iter = commodityCodeValues.iterator(); iter.hasNext();) {
            CommodityCode commodityCode = (CommodityCode) iter.next();

            if (StringUtils.contains(getPurchasingCommodityCode(), KFSConstants.WILDCARD_CHARACTER)) {
                int wildCardIndex = getPurchasingCommodityCode().indexOf(KFSConstants.WILDCARD_CHARACTER);
                //This means this rule contains wild card for commodity code.
                if (StringUtils.contains(commodityCode.getPurchasingCommodityCode(), getPurchasingCommodityCode().substring(0, wildCardIndex))) {
                    return true;
                }
            }
            else if ((StringUtils.equals(commodityCode.getPurchasingCommodityCode(), getPurchasingCommodityCode())) && (StringUtils.equals(commodityCode.getRestrictedMaterialCode(), getRestrictedMaterialCode())) ) {
                return true;
            }
        }
        return false;
    }
    
    protected Set populateCommodityCodesFromDocContent(String docTypeName, DocumentContent docContent, RouteContext routeContext) {
        Set commodityCodeValues = null;
        if (routeContext.getParameters().containsKey(DOCUMENT_COMMODITY_CODE_VALUES_KEY)) {
            commodityCodeValues = (Set) routeContext.getParameters().get(DOCUMENT_COMMODITY_CODE_VALUES_KEY);
        }
        else {
            commodityCodeValues = new HashSet();
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            try {
                String purchasingCommodityCode = null;
                String reportMatchAnywhereExpressionPrefix = new StringBuffer(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).toString();
                boolean isReport = ((Boolean) xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
                if (isReport) {
                    purchasingCommodityCode = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(reportMatchAnywhereExpressionPrefix).append(KualiWorkflowUtils.XPATH_ELEMENT_SEPARATOR).append(PurapPropertyConstants.ITEM_COMMODITY_CODE).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    //restrictedMaterialCode = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(reportMatchAnywhereExpressionPrefix).append(KualiWorkflowUtils.XPATH_ELEMENT_SEPARATOR).append(PurapPropertyConstants.RESTRICTED_MATERIAL_CODE).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    commodityCodeValues.addAll(attemptCommodityCodeRetrieval(purchasingCommodityCode));
                }
                else {
                    String xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("org.kuali.module.vendor.bo.CommodityCode").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    NodeList nodes = (NodeList) xpath.evaluate(xpathExp, docContent.getDocument(), XPathConstants.NODESET);
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node commodityCodeNode = nodes.item(i);
                        purchasingCommodityCode = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + PurapPropertyConstants.ITEM_COMMODITY_CODE, commodityCodeNode);
                        //restrictedMaterialCode = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + PurapPropertyConstants.RESTRICTED_MATERIAL_CODE, commodityCodeNode);
                        commodityCodeValues.addAll(attemptCommodityCodeRetrieval(purchasingCommodityCode));
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            routeContext.getParameters().put(DOCUMENT_COMMODITY_CODE_VALUES_KEY, commodityCodeValues);
        }
        return commodityCodeValues;
    }

    protected Campus populateDeliveryCampusFromDocContent(String docTypeName, DocumentContent docContent, RouteContext routeContext) {
        Campus deliveryCampusValue = null;
        if (routeContext.getParameters().containsKey(DOCUMENT_DELIVERY_CAMPUS_VALUE_KEY)) {
            deliveryCampusValue = (Campus) routeContext.getParameters().get(DOCUMENT_DELIVERY_CAMPUS_VALUE_KEY);
        }
        else {
            deliveryCampusValue = new Campus();
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            try {
                String deliveryCampusCode = null;
                String reportMatchAnywhereExpressionPrefix = new StringBuffer(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).toString();
                boolean isReport = ((Boolean) xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
                if (isReport) {
                    deliveryCampusCode = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(reportMatchAnywhereExpressionPrefix).append(KualiWorkflowUtils.XPATH_ELEMENT_SEPARATOR).append(PurapPropertyConstants.DELIVERY_CAMPUS_CODE).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    deliveryCampusValue = attemptDeliveryCampusRetrieval(deliveryCampusCode);
                }
                else {
                    String xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(PurapPropertyConstants.DELIVERY_CAMPUS_CODE).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    Node node = (Node) xpath.evaluate(xpathExp, docContent.getDocument(), XPathConstants.NODE);
                    deliveryCampusCode = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + KFSPropertyConstants.DOCUMENT + KualiWorkflowUtils.XPATH_ELEMENT_SEPARATOR + PurapPropertyConstants.DELIVERY_CAMPUS_CODE, node);
                    deliveryCampusValue = attemptDeliveryCampusRetrieval(deliveryCampusCode);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            routeContext.getParameters().put(DOCUMENT_DELIVERY_CAMPUS_VALUE_KEY, deliveryCampusValue);
        }
        return deliveryCampusValue;
    }
    
    private Campus attemptDeliveryCampusRetrieval(String deliveryCampusCode) {
        Campus deliveryCampus = null;
        if (StringUtils.isNotBlank(deliveryCampusCode)) {
            Map fieldValues = new HashMap<String, String>();
            fieldValues.put(KFSPropertyConstants.CAMPUS_CODE, deliveryCampusCode);
            Collection result = SpringContext.getBean(BusinessObjectService.class).findMatching(Campus.class, fieldValues);
            
            if ((result == null) || (result.isEmpty())) {
                throw new RuntimeException("Delivery Campus declared on the document cannot be found in the system, routing cannot continue.");
            }
            deliveryCampus = (Campus)(result.iterator().next());            
        }
        return deliveryCampus;
    }
    
    private List<CommodityCode> attemptCommodityCodeRetrieval(String purchasingCommodityCode) {
        List<CommodityCode> commodityCodeValues = new ArrayList();
        if (StringUtils.isNotBlank(purchasingCommodityCode)) {
            Collection commodityCodes = getCommodityCodes(purchasingCommodityCode);
            if ((commodityCodes == null) || (commodityCodes.isEmpty())) {
                throw new RuntimeException("Commodity Code declared on the document cannot be found in the system, routing cannot continue.");
            }
            // possibly duplicate add, but this is safe in a HashSet
            for (Iterator iter = commodityCodes.iterator(); iter.hasNext();) {
                CommodityCode commodityCodeToAdd = (CommodityCode) iter.next();
                commodityCodeValues.add(commodityCodeToAdd);
            }
        }
        return commodityCodeValues;
    }
    
    private Collection getCommodityCodes(String purchasingCommodityCode) {
        Map fieldValues = new HashMap<String, String>();
        fieldValues.put(PurapPropertyConstants.ITEM_COMMODITY_CODE, purchasingCommodityCode);
        return SpringContext.getBean(BusinessObjectService.class).findMatching(COMMODITY_CODE_FIELD_CLASS, fieldValues);
    }
    
}
