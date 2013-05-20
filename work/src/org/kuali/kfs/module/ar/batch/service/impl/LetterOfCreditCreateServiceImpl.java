/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDetailDao;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDocumentDao;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultRowDTO;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;

/**
 * Defines a service class for creating Cash Control documents from the LOC Review Document.
 */
public class LetterOfCreditCreateServiceImpl implements LetterOfCreditCreateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LetterOfCreditCreateServiceImpl.class);
    public static final String WORKFLOW_SEARCH_RESULT_KEY = KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID;
    private DocumentService documentService;
    private CashControlDocumentService cashControlDocumentService;
    private CashControlDocumentDao cashControlDocumentDao;
    private CashControlDetailDao cashControlDetailDao;
    private WorkflowDocumentService workflowDocumentService;

    /**
     * This method created cashcontrol documents and payment application based on the loc creation type and loc value passed.
     * 
     * @param customerNumber
     * @param locCreationType
     * @param locValue
     * @param totalAmount
     * @param outputFileStream
     * @return
     */

    public String createCashControlDocuments(String customerNumber, String locCreationType, String locValue, KualiDecimal totalAmount, PrintStream outputFileStream) {
        String documentNumber = null;

        try {
            CashControlDocument cashControlDoc = (CashControlDocument) documentService.getNewDocument(CashControlDocument.class);
            cashControlDoc.getDocumentHeader().setDocumentDescription(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(ArKeyConstants.CASH_CTRL_DOC_CREATED_BY_BATCH));
            AccountsReceivableDocumentHeaderService arDocHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
            AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
            accountsReceivableDocumentHeader.setDocumentNumber(cashControlDoc.getDocumentNumber());
            // To get default processing chart code and org code from Paramters
            String defaultProcessingChartCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CashControlDocument.class, ArConstants.DEFAULT_PROCESSING_CHART);
            String defaultProcessingOrgCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CashControlDocument.class, ArConstants.DEFAULT_PROCESSING_ORG);
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(defaultProcessingChartCode);
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(defaultProcessingOrgCode);
            if (ObjectUtils.isNotNull(locCreationType) && ObjectUtils.isNotNull(locValue)) {
                cashControlDoc.setLocCreationType(locCreationType);
                if (cashControlDoc.getLocCreationType().equalsIgnoreCase(ArConstants.LOC_BY_AWARD)) {
                    cashControlDoc.setProposalNumber(new Long(locValue));
                }
                else if (cashControlDoc.getLocCreationType().equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND)) {
                    cashControlDoc.setLetterOfCreditFundCode(locValue);
                }
                else if (cashControlDoc.getLocCreationType().equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND_GRP)) {
                    cashControlDoc.setLetterOfCreditFundGroupCode(locValue);
                }
            }
            cashControlDoc.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

            cashControlDoc.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.LOC_WIRE);
            // To set invoice document type to CG Invoice as we would be dealing only with CG Invoices.
            cashControlDoc.setInvoiceDocumentType(ArConstants.CGIN_DOCUMENT_TYPE);

            // To create cash-control detail for the cash control document
            CashControlDetail cashControlDetail = new CashControlDetail();
            cashControlDetail.setCustomerNumber(customerNumber);// Assuming that the retrieved awards/invoices would have the same
                                                                // customer number.
            cashControlDetail.setFinancialDocumentLineAmount(totalAmount);
            // To set the date in cashcontrol detail to today.
            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            cashControlDetail.setCustomerPaymentDate(today);
            cashControlDetail.setReferenceFinancialDocumentNumber(cashControlDoc.getDocumentNumber());

            cashControlDocumentService.addNewCashControlDetail(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC), cashControlDoc, cashControlDetail);

            documentService.saveDocument(cashControlDoc);

            documentNumber = cashControlDoc.getDocumentNumber();
        }
        catch (WorkflowException ex) {
            String error = "Error Creating Cash Control/Payment Application Documents for Customer Number#" + customerNumber;

            try {

                writeErrorEntry(error, outputFileStream);
            }
            catch (IOException ioe) {
                LOG.error("LetterOfCreditCreateServiceImpl.createCashControlDocuments Stopped: " + ioe.getMessage());
                throw new RuntimeException("LetterOfCreditCreateServiceImpl.createCashControlDocuments Stopped: " + ioe.getMessage(), ioe);
            }

        }

        return documentNumber;
    }


    /**
     * The method validates if there are any existing cash control documents for the same locValue and customer number combination.
     * 
     * @param customerNumber
     * @param locCreationType
     * @param locValue
     * @param outputFileStream
     * @return
     */
    public boolean validatecashControlDocument(String customerNumber, String locCreationType, String locValue, PrintStream outputFileStream) {
        boolean exists = false;
        Criteria criteria = new Criteria();
        criteria.addEqualTo("locCreationType", locCreationType);
        if (locCreationType.equalsIgnoreCase(ArConstants.LOC_BY_AWARD)) {
            criteria.addEqualTo("proposalNumber", (new Long(locValue)));
        }
        else if (locCreationType.equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND)) {
            criteria.addEqualTo("letterOfCreditFundCode", locValue);
        }
        else if (locCreationType.equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND_GRP)) {
            criteria.addEqualTo("letterOfCreditFundGroupCode", locValue);
        }

        criteria.addNotEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addNotEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.DISAPPROVED);

        CashControlDocument cashControlDocument = cashControlDocumentDao.getCashControlDocumentByCriteria(criteria);
        if (ObjectUtils.isNotNull(cashControlDocument)) {
            // Now to check if there is a cash control detail with the same customer number. - just double checking.
            List<CashControlDetail> cashControlDetails = new ArrayList<CashControlDetail>();
            List<CashControlDetail> cashCtrlDetails = cashControlDocument.getCashControlDetails();

            for (CashControlDetail cashControlDetail : cashCtrlDetails) {
                if (ObjectUtils.isNotNull(cashControlDetail.getCustomerNumber()) && cashControlDetail.getCustomerNumber().equals(customerNumber)) {
                    cashControlDetails.add(cashControlDetail);
                }
            }

            if (CollectionUtils.isNotEmpty(cashControlDetails)) {
                exists = true;
                String error = ArConstants.BatchFileSystem.LOC_CREATION_ERROR__CSH_CTRL_IN_PROGRESS + " (" + cashControlDocument.getDocumentNumber() + ")" + " for Customer Number #" + customerNumber + " and LOC Value of " + locValue;
                try {

                    writeErrorEntry(error, outputFileStream);
                }
                catch (IOException ioe) {
                    LOG.error("LetterOfCreditCreateServiceImpl.validatecashControlDocument Stopped: " + ioe.getMessage());
                    throw new RuntimeException("LetterOfCreditCreateServiceImpl.validatecashControlDocument Stopped: " + ioe.getMessage(), ioe);
                }
            }

        }
        return exists;
    }
    
    

    /**
     * This method retrieves all the cash control and payment application docs with a status of 'I' and routes them to the next step in the
     * routing path.
     * 
     * @return True if the routing was performed successfully. A runtime exception will be thrown if any errors occur while routing.
     * 
     */
    public boolean routeLOCDocuments() {
        List<String> cashControlDocumentIdList = null;
        List<String> payAppDocumentIdList = null;
        try {
            cashControlDocumentIdList = retrieveCashControlDocumentsToRoute(KewApiConstants.ROUTE_HEADER_SAVED_CD);
            payAppDocumentIdList = retrievePayAppDocumentsToRoute(KewApiConstants.ROUTE_HEADER_SAVED_CD);
        }
        catch (WorkflowException e1) {
            LOG.error("Error retrieving LOC documents for routing: " + e1.getMessage(), e1);
            throw new RuntimeException(e1.getMessage(), e1);
        }
        catch (RemoteException re) {
            LOG.error("Error retrieving LOC documents for routing: " + re.getMessage(), re);
            throw new RuntimeException(re.getMessage(), re);
        }

        // Collections.reverse(documentIdList);
        if (LOG.isInfoEnabled()) {
            LOG.info("Cash control Documents to Route: " + cashControlDocumentIdList);
            LOG.info("Payment Application Documents to Route: " + payAppDocumentIdList);
        }
        //1. Cash control documents
        for (String cashControlDocId : cashControlDocumentIdList) {
            try {
                CashControlDocument cashControlDoc = (CashControlDocument) documentService.getByDocumentHeaderId(cashControlDocId);
                //To route documents only if the user in the session is same as the initiator.
                if(cashControlDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId().equals(GlobalVariables.getUserSession().getPerson().getPrincipalId())){                  
                
                if (LOG.isInfoEnabled()) {
                    LOG.info("Routing Cash control document # " + cashControlDocId + ".");
                }
                documentService.prepareWorkflowDocument(cashControlDoc);

                // calling workflow service to bypass business rule checks
                workflowDocumentService.route(cashControlDoc.getDocumentHeader().getWorkflowDocument(), "", null);
            }
            }
            catch (WorkflowException e) {
                LOG.error("Error routing document # " + cashControlDocId + " " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        
        //1. PAyment Applciation documents
        for (String payAppDocId : payAppDocumentIdList) {
            try {
                PaymentApplicationDocument payAppDoc = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(payAppDocId);
                //To route documents only if the user in the session is same as the initiator.
                if(payAppDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId().equals(GlobalVariables.getUserSession().getPerson().getPrincipalId())){                  
                
                if (LOG.isInfoEnabled()) {
                    LOG.info("Routing PAyment Application document # " + payAppDocId + ".");
                }
                documentService.prepareWorkflowDocument(payAppDoc);

                // calling workflow service to bypass business rule checks
                workflowDocumentService.route(payAppDoc.getDocumentHeader().getWorkflowDocument(), "", null);
            }
            }
            catch (WorkflowException e) {
                LOG.error("Error routing document # " + payAppDocId + " " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return true;
    }


    /**
     * Returns a list of all initiated but not yet routed documents, using the KualiWorkflowInfo service.
     * 
     * @return a list of documents to route
     */
    @SuppressWarnings("deprecation")
    protected List<String> retrieveCashControlDocumentsToRoute(String statusCode) throws WorkflowException, RemoteException {
        List<String> documentIds = new ArrayList<String>();

        DocumentSearchCriteriaDTO criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(KFSConstants.FinancialDocumentTypeCodes.CASH_CONTROL); 
        criteria.setDocRouteStatus(statusCode);
        DocumentSearchResultDTO results = SpringContext.getBean(KualiWorkflowInfo.class).performDocumentSearch(GlobalVariables.getUserSession().getPerson().getPrincipalId(), criteria);

        for (DocumentSearchResultRowDTO resultRow : results.getSearchResults()) {
            for (KeyValue field : resultRow.getFieldValues()) {
                if (field.getKey().equals(WORKFLOW_SEARCH_RESULT_KEY)) {
                    documentIds.add(parseDocumentIdFromRouteDocHeader(field.getValue()));
                }
            }
        }

        return documentIds;
    }
    
    
    /**
     * Returns a list of all initiated but not yet routed documents, using the KualiWorkflowInfo service.
     * 
     * @return a list of documents to route
     */
    @SuppressWarnings("deprecation")
    protected List<String> retrievePayAppDocumentsToRoute(String statusCode) throws WorkflowException, RemoteException {
        List<String> documentIds = new ArrayList<String>();

        DocumentSearchCriteriaDTO criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(KFSConstants.FinancialDocumentTypeCodes.PAYMENT_APPLICATION);
        criteria.setDocRouteStatus(statusCode);
        DocumentSearchResultDTO results = SpringContext.getBean(KualiWorkflowInfo.class).performDocumentSearch(GlobalVariables.getUserSession().getPerson().getPrincipalId(), criteria);

        for (DocumentSearchResultRowDTO resultRow : results.getSearchResults()) {
            for (KeyValue field : resultRow.getFieldValues()) {
                if (field.getKey().equals(WORKFLOW_SEARCH_RESULT_KEY)) {
                    documentIds.add(parseDocumentIdFromRouteDocHeader(field.getValue()));
                }
            }
        }

        return documentIds;
    }

    /**
     * Retrieves the document id out of the route document header
     * 
     * @param routeDocHeader the String representing an HTML link to the document
     * @return the document id
     */
    protected String parseDocumentIdFromRouteDocHeader(String routeDocHeader) {
        int rightBound = routeDocHeader.indexOf('>') + 1;
        int leftBound = routeDocHeader.indexOf('<', rightBound);
        return routeDocHeader.substring(rightBound, leftBound);
    }


    /**
     * This method writes issues to the error file.
     * 
     * @param line
     * @param printStream
     * @throws IOException
     */
    protected void writeErrorEntry(String line, PrintStream printStream) throws IOException {
        try {
            printStream.printf("%s\n", line);
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }


    /**
     * Gets the documentService attribute.
     * 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }


    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }


    /**
     * Gets the cashControlDocumentService attribute.
     * 
     * @return Returns the cashControlDocumentService.
     */
    public CashControlDocumentService getCashControlDocumentService() {
        return cashControlDocumentService;
    }


    /**
     * Sets the cashControlDocumentService attribute value.
     * 
     * @param cashControlDocumentService The cashControlDocumentService to set.
     */
    public void setCashControlDocumentService(CashControlDocumentService cashControlDocumentService) {
        this.cashControlDocumentService = cashControlDocumentService;
    }


    /**
     * Gets the cashControlDocumentDao attribute.
     * 
     * @return Returns the cashControlDocumentDao.
     */
    public CashControlDocumentDao getCashControlDocumentDao() {
        return cashControlDocumentDao;
    }


    /**
     * Sets the cashControlDocumentDao attribute value.
     * 
     * @param cashControlDocumentDao The cashControlDocumentDao to set.
     */
    public void setCashControlDocumentDao(CashControlDocumentDao cashControlDocumentDao) {
        this.cashControlDocumentDao = cashControlDocumentDao;
    }


    /**
     * Gets the cashControlDetailDao attribute.
     * 
     * @return Returns the cashControlDetailDao.
     */
    public CashControlDetailDao getCashControlDetailDao() {
        return cashControlDetailDao;
    }


    /**
     * Sets the cashControlDetailDao attribute value.
     * 
     * @param cashControlDetailDao The cashControlDetailDao to set.
     */
    public void setCashControlDetailDao(CashControlDetailDao cashControlDetailDao) {
        this.cashControlDetailDao = cashControlDetailDao;
    }


    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }


    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }


    
}
