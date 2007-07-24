/*
 * Copyright 2007 The Kuali Foundation.
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Document;

import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.AbstractWorkflowAttribute;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.workgroup.GroupNameId;

public class KualiPurApDocumentTaxReviewAttribute extends AbstractWorkflowAttribute {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiPurApDocumentTaxReviewAttribute.class);
    
    private static final String VENDOR_ROUTE_AS_ATTRIBUTE_KEY = "vendor_route_attribute";
    private static final String VENDOR_HEADER_GENERATED_ID_KEY = "vendorHeaderGeneratedIdentifier";
    private static final String ROUTE_AS_TYPE_FIELD_LABEL = "Vendor Tax Attribute";
    
    private static final String VENDOR_IS_EMPLOYEE = "Employee Vendor";
    private static final String VENDOR_IS_FOREIGN = "Foreign Vendor";
    private static final String VENDOR_IS_FOREIGN_EMPLOYEE = "Foreign and Employee Vendor";
    
    private static Set<String> validValues = new HashSet<String>();
    static {
        validValues.add(VENDOR_IS_FOREIGN_EMPLOYEE);
        validValues.add(VENDOR_IS_EMPLOYEE);
        validValues.add(VENDOR_IS_FOREIGN);
    }

    private String vendorRouteAsTypeKey;
    private String vendorHeaderGeneratedId;
    private List<Row> ruleRows;
    private List<Row> routingDataRows;

    /**
     * No arg constructor
     */
    public KualiPurApDocumentTaxReviewAttribute() {
        ruleRows = new ArrayList<edu.iu.uis.eden.lookupable.Row>();
        ruleRows.add(constructDropdown());
        
        routingDataRows = new ArrayList<edu.iu.uis.eden.lookupable.Row>();
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(VendorDetail.class, VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, VENDOR_HEADER_GENERATED_ID_KEY));
        routingDataRows.add(constructDropdown());
    }
    
    private edu.iu.uis.eden.lookupable.Row constructDropdown() {
        List validValuesTemp = new ArrayList();
        for (String validValue : validValues) {
            validValuesTemp.add(new KeyLabelPair(validValue, validValue));
        }
        List chartFields = new ArrayList();
        chartFields.add(new Field(ROUTE_AS_TYPE_FIELD_LABEL, "", Field.DROPDOWN, false, VENDOR_ROUTE_AS_ATTRIBUTE_KEY,
                        "", validValuesTemp, null, VENDOR_ROUTE_AS_ATTRIBUTE_KEY));
        return new Row(chartFields);
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if ( (StringUtils.isNotBlank(getVendorRouteAsTypeKey())) || (StringUtils.isNotBlank(getVendorHeaderGeneratedId())) ) {
            StringBuffer returnValue = new StringBuffer(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_PREFIX);
            String xmlKey = "vendorRouteTypeCode";
            returnValue.append("<" + VENDOR_ROUTE_AS_ATTRIBUTE_KEY + ">").append(getVendorRouteAsTypeKey()).append("</" + VENDOR_ROUTE_AS_ATTRIBUTE_KEY + ">");
            returnValue.append("<" + VENDOR_HEADER_GENERATED_ID_KEY + ">").append(getVendorHeaderGeneratedId()).append("</" + VENDOR_HEADER_GENERATED_ID_KEY + ">");
            return returnValue.append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_SUFFIX).toString();
        }
        return "";
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getRoutingDataRows()
     */
    public List<Row> getRoutingDataRows() {
        return routingDataRows;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getRuleExtensionValues()
     */
    public List<RuleExtensionValue> getRuleExtensionValues() {
        List extensions = new ArrayList();
        extensions.add(new RuleExtensionValue(VENDOR_ROUTE_AS_ATTRIBUTE_KEY, getVendorRouteAsTypeKey()));
        return extensions;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getRuleRows()
     */
    public List<Row> getRuleRows() {
        return ruleRows;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#isMatch(edu.iu.uis.eden.routeheader.DocumentContent, java.util.List)
     */
    public boolean isMatch(DocumentContent documentContent, List<RuleExtension> ruleExtensions) {
//        Document doc = documentContent.getDocument();
        String currentXpathExpression = null;
        String vendorHeaderGeneratedId = null;
        try {
            XPath xPath = KualiWorkflowUtils.getXPath(documentContent.getDocument());
            currentXpathExpression = KualiWorkflowUtils.xstreamSafeXPath(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX);
            boolean isReport = ((Boolean)xPath.evaluate(currentXpathExpression, documentContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
            if (isReport) {
                currentXpathExpression = KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX + "/" + VENDOR_HEADER_GENERATED_ID_KEY;
            } else {
                currentXpathExpression = KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + KFSPropertyConstants.DOCUMENT + "/" + PurapPropertyConstants.VENDOR_HEADER_GENERATED_ID;
            }
            vendorHeaderGeneratedId = KualiWorkflowUtils.xstreamSafeEval(xPath, currentXpathExpression, documentContent.getDocument());
            if (StringUtils.isBlank(vendorHeaderGeneratedId)) {
                // no vendor header id so can't check for proper tax routing
                return false;
            }
            VendorService vendorService = SpringServiceLocator.getVendorService();
            boolean routeDocumentAsEmployeeVendor = vendorService.isVendorInstitutionEmployee(Integer.valueOf(vendorHeaderGeneratedId));
            boolean routeDocumentAsForeignVendor = vendorService.isVendorForeign(Integer.valueOf(vendorHeaderGeneratedId));
            if ( (!routeDocumentAsEmployeeVendor) && (!routeDocumentAsForeignVendor) ) {
                // no need to route
                return false;
            }
            
            String value = getRuleExtentionValue(VENDOR_ROUTE_AS_ATTRIBUTE_KEY, ruleExtensions);
            if (StringUtils.equals(VENDOR_IS_EMPLOYEE, value)) {
                return routeDocumentAsEmployeeVendor;
            } else if (StringUtils.equals(VENDOR_IS_FOREIGN, value)) {
                return routeDocumentAsForeignVendor;
            } else if (StringUtils.equals(VENDOR_IS_FOREIGN_EMPLOYEE, value)) {
                return routeDocumentAsEmployeeVendor && routeDocumentAsForeignVendor;
            }
            return false;
        }
        catch (NumberFormatException nfe) {
            String errorMsg = "Number format exception for invalid vendor header id of " + vendorHeaderGeneratedId;
            LOG.error(errorMsg,nfe);
            throw new RuntimeException(errorMsg,nfe);
        }
        catch (XPathExpressionException xe) {
            String errorMsg = "Xpath error occurred while using xpath expression " + currentXpathExpression;
            LOG.error(errorMsg,xe);
            throw new RuntimeException(errorMsg,xe);
        }
    }

    private String getRuleExtentionValue(String key, List<RuleExtension> ruleExtensions) {
        for (RuleExtension extension : ruleExtensions) {
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
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRoutingData(java.util.Map)
     */
    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        setVendorHeaderGeneratedId((String) paramMap.get(VENDOR_HEADER_GENERATED_ID_KEY));
        errors.addAll(validateRuleData(paramMap));
        String label = KualiWorkflowUtils.getBusinessObjectAttributeLabel(VendorDetail.class, VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID);
        if (isRequired() && StringUtils.isBlank(getVendorHeaderGeneratedId())) {
            // required but blank
            String errorMessage = label + " is required";
            errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
        } else if (StringUtils.isNotBlank(getVendorHeaderGeneratedId())) {
            try {
                // check valid values?
                VendorDetail vendor = SpringServiceLocator.getVendorService().getParentVendor(Integer.valueOf(getVendorHeaderGeneratedId()));
                if (ObjectUtils.isNull(vendor)) {
                    String errorMessage = "No valid vendor found for given value of " + label;
                    errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
                }
            }
            catch (NumberFormatException e) {
                String errorMessage = "The value of " + label + " must be a valid number";
                LOG.info(errorMessage,e);
                errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
            }
        }
        return errors;

    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRuleData(java.util.Map)
     */
    public List validateRuleData(Map paramMap) {
        List errors = new ArrayList();
        setVendorRouteAsTypeKey((String) paramMap.get(VENDOR_ROUTE_AS_ATTRIBUTE_KEY));
        if (isRequired() && StringUtils.isBlank(getVendorRouteAsTypeKey())) {
            // required but blank
            String errorMessage = ROUTE_AS_TYPE_FIELD_LABEL + " is required";
            errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
        } else if (StringUtils.isNotBlank(getVendorRouteAsTypeKey())) {
            // check valid values?
            if (!validValues.contains(getVendorRouteAsTypeKey())) {
                String errorMessage = ROUTE_AS_TYPE_FIELD_LABEL + " is not a valid value for the field";
                errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
            }
        }
        return errors;
    }

    /**
     * Gets the vendorHeaderGeneratedId attribute. 
     * @return Returns the vendorHeaderGeneratedId.
     */
    public String getVendorHeaderGeneratedId() {
        return vendorHeaderGeneratedId;
    }

    /**
     * Sets the vendorHeaderGeneratedId attribute value.
     * @param vendorHeaderGeneratedId The vendorHeaderGeneratedId to set.
     */
    public void setVendorHeaderGeneratedId(String vendorHeaderGeneratedId) {
        this.vendorHeaderGeneratedId = vendorHeaderGeneratedId;
    }

    /**
     * Gets the vendorRouteAsTypeKey attribute. 
     * @return Returns the vendorRouteAsTypeKey.
     */
    public String getVendorRouteAsTypeKey() {
        return vendorRouteAsTypeKey;
    }

    /**
     * Sets the vendorRouteAsTypeKey attribute value.
     * @param vendorRouteAsTypeKey The vendorRouteAsTypeKey to set.
     */
    public void setVendorRouteAsTypeKey(String vendorRouteAsTypeKey) {
        this.vendorRouteAsTypeKey = vendorRouteAsTypeKey;
    }
    
}
