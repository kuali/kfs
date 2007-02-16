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
package org.kuali.workflow;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.workflow.attribute.WorkflowLookupableImpl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routetemplate.RouteContext;
import edu.iu.uis.eden.routetemplate.xmlrouting.WorkflowFunctionResolver;
import edu.iu.uis.eden.routetemplate.xmlrouting.WorkflowNamespaceContext;

/**
 * This class contains static utility methods used by the Kuali Workflow Attribute Classes.
 * 
 * 
 * 
 */
public class KualiWorkflowUtils {
    private static final Logger LOG = Logger.getLogger(KualiWorkflowUtils.class);
    private static final String XPATH_ROUTE_CONTEXT_KEY = "_xpathKey";
    public static final String XSTREAM_SAFE_PREFIX = "wf:xstreamsafe('";
    public static final String XSTREAM_SAFE_SUFFIX = "')";
    public static final String XSTREAM_MATCH_ANYWHERE_PREFIX = "//";
    public static final String XSTREAM_MATCH_RELATIVE_PREFIX = "./";
    public static final String NEW_MAINTAINABLE_PREFIX = KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "newMaintainableObject/businessObject/";
    public static final String OLD_MAINTAINABLE_PREFIX = KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "oldMaintainableObject/businessObject/";
    public static final String ACCOUNT_DOC_TYPE = "KualiAccountMaintenanceDocument";
    public static final String ACCOUNT_DEL_DOC_TYPE = "KualiAccountDelegateMaintenanceDocument";
    public static final String SUB_ACCOUNT_DOC_TYPE = "KualiSubAccountMaintenanceDocument";
    public static final String SUB_OBJECT_DOC_TYPE = "KualiSubObjectMaintenanceDocument";
    public static final String INTERNAL_BILLING_DOC_TYPE = "KualiInternalBillingDocument";
    public static final String PRE_ENCUMBRANCE_DOC_TYPE = "KualiPreEncumbranceDocument";
    public static final String DISBURSEMENT_VOCHER_DOC_TYPE = "KualiDisbursementVoucherDocument";
    public static final String NON_CHECK_DISBURSEMENT_DOC_TYPE = "KualiNonCheckDisbursementDocument";
    public static final String PROCUREMENT_CARD_DOC_TYPE = "KualiProcurementCardDocument";
    public static final String BUDGET_ADJUSTMENT_DOC_TYPE = "KualiBudgetAdjustmentDocument";
    public static final String GENERAL_ERROR_CORRECTION_DOC_TYPE = "KualiGeneralErrorCorrectionDocument";
    public static final String GENERAL_LEDGER_ERROR_CORRECTION_DOC_TYPE = "KualiGeneralLedgerErrorCorrectionDocument";
    public static final String MAINTENANCE_DOC_TYPE = "KualiMaintenanceDocument";
    public static final String FINANCIAL_DOC_TYPE = "KualiFinancialDocument";
    public static final String FINANCIAL_YEAR_END_DOC_TYPE = "KualiFinancialYearEndDocument";
    public static final String FIS_USER_DOC_TYPE = "KualiUserMaintenanceDocument";
    public static final String ORGANIZATION_DOC_TYPE = "KualiOrganizationMaintenanceDocument";
    public static final String PROJECT_CODE_DOC_TYPE = "KualiProjectCodeMaintenanceDocument";
     public static final String KRA_BUDGET_DOC_TYPE = "KualiBudgetDocument";
    public static final String SIMPLE_MAINTENANCE_DOC_TYPE = "KualiSimpleMaintenanceDocument";
    
    public class RouteLevels {

        public static final int ADHOC = 0;
        public static final int EXCEPTION = -1;
        public static final int ORG_REVIEW = 1;
        
    }
    
    public class RouteLevelNames {

        public static final String ACCOUNT_REVIEW = "Account Review";
        public static final String ORG_REVIEW = "Org Review";
        public static final String EMPLOYEE_INDICATOR = "Employee Indicator";
        public static final String TAX_CONTROL_CODE = "Tax Control Code";
        public static final String ALIEN_INDICATOR = "Alien Indicator";
        public static final String PAYMENT_REASON = "Payment Reason";
        public static final String PAYMENT_REASON_CAMPUS = "Payment Reason+Campus Code";
        public static final String CAMPUS_CODE = "Campus Code";
        public static final String ALIEN_INDICATOR_PAYMENT_REASON = "Alien Indicator+Payment Reason";
        public static final String PAYMENT_METHOD = "Payment Method";
        public static final String ACCOUNT_REVIEW_FULL_EDIT = "Account Review Full Edit";
        
    }

    public static final Set SOURCE_LINE_ONLY_DOCUMENT_TYPES = new HashSet();
    static {
        SOURCE_LINE_ONLY_DOCUMENT_TYPES.add(DISBURSEMENT_VOCHER_DOC_TYPE);
    }

    public static final Set TARGET_LINE_ONLY_DOCUMENT_TYPES = new HashSet();
    static {
        TARGET_LINE_ONLY_DOCUMENT_TYPES.add(INTERNAL_BILLING_DOC_TYPE);
        TARGET_LINE_ONLY_DOCUMENT_TYPES.add(PROCUREMENT_CARD_DOC_TYPE);
    }

    public static boolean isSourceLineOnly(String documentTypeName) {
        return SOURCE_LINE_ONLY_DOCUMENT_TYPES.contains(documentTypeName);
    }

    public static boolean isTargetLineOnly(String documentTypeName) {
        return TARGET_LINE_ONLY_DOCUMENT_TYPES.contains(documentTypeName);
    }

    public static final boolean isMaintenanceDocument(DocumentType documentType) {
        LOG.info("started isMaintenanceDocument: " + documentType.getName());
        boolean isMaintenanceDocument = false;
        DocumentType currentDocumentType = documentType.getParentDocType();
        while ((currentDocumentType != null) && !isMaintenanceDocument) {
            if (MAINTENANCE_DOC_TYPE.equals(currentDocumentType.getName())) {
                isMaintenanceDocument = true;
            }
            else {
                currentDocumentType = currentDocumentType.getParentDocType();
            }
        }
        LOG.info(new StringBuffer("finished isMaintenanceDocument: ").append(documentType.getName()).append(" - ").append(isMaintenanceDocument));
        return isMaintenanceDocument;
    }

    /**
     * 
     * This method sets up the XPath with the correct workflow namespace and resolver initialized. This ensures that the XPath
     * statements can use required workflow functions as part of the XPath statements.
     * 
     * @param document - document
     * @return a fully initialized XPath instance that has access to the workflow resolver and namespace.
     * 
     */
    public final static XPath getXPath(Document document) {
        XPath xpath = getXPath(RouteContext.getCurrentRouteContext());
        xpath.setNamespaceContext(new WorkflowNamespaceContext());
        WorkflowFunctionResolver resolver = new WorkflowFunctionResolver();
        resolver.setXpath(xpath);
        resolver.setRootNode(document);
        xpath.setXPathFunctionResolver(resolver);
        return xpath;
    }

    public final static XPath getXPath(RouteContext routeContext) {
        if (routeContext == null) {
            return XPathFactory.newInstance().newXPath();
        }
        if (!routeContext.getParameters().containsKey(XPATH_ROUTE_CONTEXT_KEY)) {
            routeContext.getParameters().put(XPATH_ROUTE_CONTEXT_KEY, XPathFactory.newInstance().newXPath());
        }
        return (XPath) routeContext.getParameters().get(XPATH_ROUTE_CONTEXT_KEY);
    }

    /**
     * TODO: remove this method when we upgrade to workflow 2.2 - the problem that this helps with is as follows:
     * StandardWorkflowEngine is not currently setting up the DocumentContent on the RouteContext object. Instead that's being
     * handled by the RequestsNode which, in the case of the BudgetAdjustmentDocument, we never pass through before hitting the
     * first split . So, in that particular case, we have to reference an attribute that gives us the xml string and translate that
     * to a dom document ourselves.
     * 
     * @param xmlDocumentContent
     * @return a dom representation of the xml provided
     * @deprecated
     */
    public static final Document getDocument(String xmlDocumentContent) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(xmlDocumentContent))));
        }
        catch (Exception e) {
            throw new RuntimeException(KualiWorkflowUtils.class.getName() + " encountered an exception while attempting to convert and xmlDocumentContent String into a org.w3c.dom.Document", e);
        }
    }

    /**
     * This method uses the AccountingDocument to get the name of the source accounting line class for a
     * given workflow documentTypeName. It is intended for use by our workflow attributes when building xpath expressions
     * 
     * @param documentTypeName the document type name to use when querying the TransactionalDocumentDataDictionaryService
     * @return the name of the source accounting line class associated with the specified workflow document type name
     */
    public static final String getSourceAccountingLineClassName(AccountingDocument document) {
        Class sourceAccountingLineClass = document.getSourceAccountingLineClass();
        String sourceAccountingLineClassName = null;
        if (sourceAccountingLineClass != null) {
            sourceAccountingLineClassName = sourceAccountingLineClass.getName();
        }
        else {
            sourceAccountingLineClassName = SourceAccountingLine.class.getName();
        }
        return sourceAccountingLineClassName;
    }

    /**
     * This method uses the AccountingDocument to get the name of the target accounting line class for a
     * given workflow documentTypeName. It is intended for use by our workflow attributes when building xpath expressions
     * 
     * @param documentTypeName the document type name to use when querying the TransactionalDocumentDataDictionaryService
     * @return the name of the target accounting line class associated with the specified workflow document type name
     */
    public static final String getTargetAccountingLineClassName(AccountingDocument document) {
        Class targetAccountingLineClass = document.getTargetAccountingLineClass();
        String targetAccountingLineClassName = null;
        if (targetAccountingLineClass != null) {
            targetAccountingLineClassName = targetAccountingLineClass.getName();
        }
        else {
            targetAccountingLineClassName = TargetAccountingLine.class.getName();
        }
        return targetAccountingLineClassName;
    }

    /**
     * 
     * This method will do a simple XPath.evaluate, while wrapping your xpathExpression with the xstreamSafe function. It assumes a
     * String result, and will return such.
     * 
     * If an XPathExpressionException is thrown, this will be re-thrown within a RuntimeException.
     * 
     * @param xpath A correctly initialized XPath instance.
     * @param xpathExpression Your XPath Expression that needs to be wrapped in an xstreamSafe wrapper and run.
     * @param item The document contents you will be searching within.
     * @return The string value of the xpath.evaluate().
     */
    public static final String xstreamSafeEval(XPath xpath, String xpathExpression, Object item) {
        String xstreamSafeXPath = new StringBuilder(XSTREAM_SAFE_PREFIX).append(xpathExpression).append(XSTREAM_SAFE_SUFFIX).toString();
        String evalResult = "";
        try {
            evalResult = xpath.evaluate(xstreamSafeXPath, item);
        }
        catch (XPathExpressionException e) {
            throw new RuntimeException("XPathExpressionException occurred on xpath: " + xstreamSafeXPath, e);
        }
        return evalResult;
    }

    /**
     * 
     * This method wraps the passed-in XPath expression in XStream Safe wrappers, so that XStream generated reference links will be
     * handled correctly.
     * 
     * @param xpathExpression The XPath Expression you wish to use.
     * @return Your XPath Expression wrapped in the XStreamSafe wrapper.
     * 
     */
    public static final String xstreamSafeXPath(String xpathExpression) {
        return new StringBuilder(XSTREAM_SAFE_PREFIX).append(xpathExpression).append(XSTREAM_SAFE_SUFFIX).toString();
    }

    /**
     * This method is for use by WorkflowLookupableImpl and WorkflowAttribute implementations to derive the fieldHelpUrl for use on
     * edu.iu.uis.eden.lookupable.Fields.
     * 
     * @param field The kuali field that we need to derive a help url for. @ return Returns the help url for the field.
     */
    public static String getHelpUrl(org.kuali.core.web.ui.Field field) {
        Properties params = new Properties();
        params.put(Constants.DISPATCH_REQUEST_PARAMETER, "getAttributeHelpText");
        params.put(Constants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, field.getBusinessObjectClassName());
        params.put(PropertyConstants.ATTRIBUTE_NAME, field.getPropertyName());
        return UrlFactory.parameterizeUrl(SpringServiceLocator.getKualiConfigurationService().getPropertyString(Constants.APPLICATION_URL_KEY) + "/help.do", params);
    }

    /**
     * This is for use by xml WorkflowAttribute implementations. It overrides the label and help url of the test fields on the
     * edu.iu.uis.eden.lookupable.Rows obtained from the workflow parent class with the appropriate values from the data dictionary.
     * 
     * @param workflowRows A list of edu.iu.uis.eden.lookupable.Row objects provided by the workflow superclass, based on the XML
     *        attribute definition.
     * @param businessObjectClass The BusinessObject Class extracted from the meta data specified in the XML attribute definition,
     *        which is used in querying the data dictionary for the field definition.
     */
    public static List setKualiFieldValues(List workflowRows, String businessObjectClassName) {
        Iterator workflowRowsItr = workflowRows.iterator();
        while (workflowRowsItr.hasNext()) {
            edu.iu.uis.eden.lookupable.Row row = (edu.iu.uis.eden.lookupable.Row) workflowRowsItr.next();
            Iterator fieldItr = row.getFields().iterator();
            while (fieldItr.hasNext()) {
                edu.iu.uis.eden.lookupable.Field field = row.getField(0);
                if (edu.iu.uis.eden.lookupable.Field.TEXT.equals(field.getFieldType())) {
                    try {
                        org.kuali.core.web.ui.Field kualiField = FieldUtils.getPropertyField(Class.forName(businessObjectClassName), field.getPropertyName(), false);
                        field.setFieldLabel(kualiField.getFieldLabel());
                        field.setFieldHelpUrl(KualiWorkflowUtils.getHelpUrl(kualiField));
                    }
                    catch (ClassNotFoundException cnfe) {
                        throw new RuntimeException("Unable to load BusinessObject class: " + businessObjectClassName, cnfe);
                    }
                }
            }
        }
        return workflowRows;
    }
    
    /**
     * 
     * This method builds a workflow-lookup-screen Row of type TEXT, with no quickfinder/lookup.
     * 
     * @param propertyClass The Class of the BO that this row is based on.  For example, Account.class for accountNumber.
     * @param boPropertyName The property name on the BO that this row is based on.  For example, accountNumber for Account.accountNumber.
     * @param workflowPropertyKey The workflow-lookup-screen property key.  For example, account_nbr for Account.accountNumber.  This key can be 
     *                            anything, but needs to be consistent with what is used for the row/field key on the java attribute, so everything 
     *                            links up correctly.
     * @return A populated and ready-to-use workflow lookupable.Row.
     * 
     */
    public static edu.iu.uis.eden.lookupable.Row buildTextRow(Class propertyClass, String boPropertyName, String workflowPropertyKey) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("Method parameter 'propertyClass' was passed a NULL value.");
        }
        if (StringUtils.isBlank(boPropertyName)) {
            throw new IllegalArgumentException("Method parameter 'boPropertyName' was passed a NULL or blank value.");
        }
        if (StringUtils.isBlank(workflowPropertyKey)) {
            throw new IllegalArgumentException("Method parameter 'workflowPropertyKey' was passed a NULL or blank value.");
        }
        List chartFields = new ArrayList();
        org.kuali.core.web.ui.Field field;
        field = FieldUtils.getPropertyField(propertyClass, boPropertyName, false);
        chartFields.add(new Field(field.getFieldLabel(), KualiWorkflowUtils.getHelpUrl(field), Field.TEXT, false, workflowPropertyKey, 
                        field.getPropertyValue(), field.getFieldValidValues(), null, workflowPropertyKey));
        return new Row(chartFields);
    }
    
    /**
     * 
     * This method builds a workflow-lookup-screen Row of type TEXT, with the attached lookup icon and functionality.
     * 
     * @param propertyClass The Class of the BO that this row is based on.  For example, Account.class for accountNumber.
     * @param boPropertyName The property name on the BO that this row is based on.  For example, accountNumber for Account.accountNumber.
     * @param workflowPropertyKey The workflow-lookup-screen property key.  For example, account_nbr for Account.accountNumber.  This key can be 
     *                            anything, but needs to be consistent with what is used for the row/field key on the java attribute, so everything 
     *                            links up correctly.
     * @return A populated and ready-to-use workflow lookupable.Row, which includes both the property field and the lookup icon.
     * 
     */
    public static edu.iu.uis.eden.lookupable.Row buildTextRowWithLookup(Class propertyClass, String boPropertyName, String workflowPropertyKey) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("Method parameter 'propertyClass' was passed a NULL value.");
        }
        if (StringUtils.isBlank(boPropertyName)) {
            throw new IllegalArgumentException("Method parameter 'boPropertyName' was passed a NULL or blank value.");
        }
        if (StringUtils.isBlank(workflowPropertyKey)) {
            throw new IllegalArgumentException("Method parameter 'workflowPropertyKey' was passed a NULL or blank value.");
        }
        org.kuali.core.web.ui.Field field;
        field = FieldUtils.getPropertyField(propertyClass, boPropertyName, false);
        
        //  build the quickFinder/lookupableName info
        String lookupableClassNameImpl = WorkflowLookupableImpl.getLookupableImplName(propertyClass);
        StringBuffer fieldConversions = new StringBuffer(WorkflowLookupableImpl.LOOKUPABLE_IMPL_NAME_PREFIX); 
        fieldConversions.append(boPropertyName + ":" + workflowPropertyKey);
        String lookupableName = WorkflowLookupableImpl.getLookupableName(lookupableClassNameImpl, fieldConversions.toString());
        
        List chartFields = new ArrayList();
        chartFields.add(new Field(field.getFieldLabel(), KualiWorkflowUtils.getHelpUrl(field), Field.TEXT, true, workflowPropertyKey, 
                        field.getPropertyValue(), field.getFieldValidValues(), lookupableClassNameImpl, workflowPropertyKey));
        chartFields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, lookupableName)); // quickfinder/lookup icon 
        return new Row(chartFields);
    }
    
}