/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.workflow;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.util.SpringServiceLocator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.routetemplate.RouteContext;
import edu.iu.uis.eden.routetemplate.xmlrouting.WorkflowFunctionResolver;
import edu.iu.uis.eden.routetemplate.xmlrouting.WorkflowNamespaceContext;

/**
 * This class contains static utility methods used by the Kuali Workflow Attribute Classes.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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
    public static final String SIMPLE_MAINTENANCE_DOC_TYPE = "KualiSimpleMaintenanceDocument";
    
    public static final Set SOURCE_LINE_ONLY_DOCUMENT_TYPES = new HashSet();
    static {
        SOURCE_LINE_ONLY_DOCUMENT_TYPES.add(DISBURSEMENT_VOCHER_DOC_TYPE);
        SOURCE_LINE_ONLY_DOCUMENT_TYPES.add(PROCUREMENT_CARD_DOC_TYPE);
    }
    
    public static final Set TARGET_LINE_ONLY_DOCUMENT_TYPES = new HashSet();
    static {
        TARGET_LINE_ONLY_DOCUMENT_TYPES.add(INTERNAL_BILLING_DOC_TYPE);
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
            } else {
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
     * @return - a fully initialized XPath instance that has access to the workflow resolver and namespace.
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
     * first split. So, in that particular case, we have to reference an attribute that gives us the xml string and translate that
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
     * This method uses the TransactionalDocumentDataDictionaryService to get the name of the source accounting line class for a
     * given workflow documentTypeName. It is intended for use by our workflow attributes when building xpath expressions
     * 
     * @param documentTypeName the document type name to use when querying the TransactionalDocumentDataDictionaryService
     * @return the name of the source accounting line class associated with the specified workflow document type name
     */
    public static final String getSourceAccountingLineClassName(String documentTypeName) {
        Class sourceAccountingLineClass = SpringServiceLocator.getTransactionalDocumentDictionaryService().getSourceAccountingLineClass(documentTypeName);
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
     * This method uses the TransactionalDocumentDataDictionaryService to get the name of the target accounting line class for a
     * given workflow documentTypeName. It is intended for use by our workflow attributes when building xpath expressions
     * 
     * @param documentTypeName the document type name to use when querying the TransactionalDocumentDataDictionaryService
     * @return the name of the target accounting line class associated with the specified workflow document type name
     */
    public static final String getTargetAccountingLineClassName(String documentTypeName) {
        Class targetAccountingLineClass = SpringServiceLocator.getTransactionalDocumentDictionaryService().getTargetAccountingLineClass(documentTypeName);
        String targetAccountingLineClassName = null;
        if (targetAccountingLineClass != null) {
            targetAccountingLineClassName = targetAccountingLineClass.getName();
        }
        else {
            targetAccountingLineClassName = TargetAccountingLine.class.getName();
        }
        return targetAccountingLineClassName;
    }
}