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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDetailDao;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDocumentDao;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Defines a service class for creating Cash Control documents from the LOC Review Document.
 */
@Transactional
public class LetterOfCreditCreateServiceImpl implements LetterOfCreditCreateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LetterOfCreditCreateServiceImpl.class);
    public static final String WORKFLOW_SEARCH_RESULT_KEY = "routeHeaderId";
    protected CashControlDetailDao cashControlDetailDao;
    protected CashControlDocumentDao cashControlDocumentDao;
    protected CashControlDocumentService cashControlDocumentService;
    protected ConfigurationService configService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected KualiModuleService kualiModuleService;
    protected ParameterService parameterService;
    protected WorkflowDocumentService workflowDocumentService;

    public ConfigurationService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
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
    @Override
    public String createCashControlDocuments(String customerNumber, String locCreationType, String locValue, KualiDecimal totalAmount, PrintStream outputFileStream) {
        String documentNumber = null;

        try {
            CashControlDocument cashControlDoc = (CashControlDocument) documentService.getNewDocument(CashControlDocument.class);
            cashControlDoc.getDocumentHeader().setDocumentDescription(configService.getPropertyValueAsString(ArKeyConstants.CASH_CTRL_DOC_CREATED_BY_BATCH));
            AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
            accountsReceivableDocumentHeader.setDocumentNumber(cashControlDoc.getDocumentNumber());
            // To get default processing chart code and org code from Paramters
            String defaultProcessingChartCode = parameterService.getParameterValueAsString(CashControlDocument.class, ArConstants.DEFAULT_PROCESSING_CHART);
            String defaultProcessingOrgCode = parameterService.getParameterValueAsString(CashControlDocument.class, ArConstants.DEFAULT_PROCESSING_ORG);
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(defaultProcessingChartCode);
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(defaultProcessingOrgCode);
            if (ObjectUtils.isNotNull(locCreationType) && ObjectUtils.isNotNull(locValue)) {
                cashControlDoc.setLetterOfCreditCreationType(locCreationType);
                if (cashControlDoc.getLetterOfCreditCreationType().equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND)) {
                    cashControlDoc.setLetterOfCreditFundCode(locValue);
                }
                else if (cashControlDoc.getLetterOfCreditCreationType().equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND_GRP)) {
                    cashControlDoc.setLetterOfCreditFundGroupCode(locValue);
                }
            }
            cashControlDoc.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

            cashControlDoc.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.LOC_WIRE);
            // To set invoice document type to CG Invoice as we would be dealing only with CG Invoices.
            cashControlDoc.setInvoiceDocumentType(ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);

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

            cashControlDocumentService.addNewCashControlDetail(configService.getPropertyValueAsString(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC), cashControlDoc, cashControlDetail);

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
    @Override
    public boolean validatecashControlDocument(String customerNumber, String locCreationType, String locValue, PrintStream outputFileStream) {
        boolean isExists = false;

        Map<String,String> fieldValues = new HashMap<String,String>();

        fieldValues.put(ArConstants.LETTER_OF_CREDIT_CREATION_TYPE, locCreationType);
        if (locCreationType.equalsIgnoreCase(ArConstants.LOC_BY_AWARD)) {
            fieldValues.put(ArConstants.PROPOSAL_NUMBER, locValue);
        }
        else if (locCreationType.equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND)) {
            fieldValues.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND_CODE, locValue);
        }
        else if (locCreationType.equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND_GRP)) {
            fieldValues.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND_GROUP_CODE, locValue);
        }

        CashControlDocument cashControlDocument = cashControlDocumentDao.getCashControlDocument(fieldValues);
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
                isExists = true;
                String error = ArKeyConstants.LOC_CREATION_ERROR__CSH_CTRL_IN_PROGRESS + " (" + cashControlDocument.getDocumentNumber() + ")" + " for Customer Number #" + customerNumber + " and LOC Value of " + locValue;
                try {

                    writeErrorEntry(error, outputFileStream);
                }
                catch (IOException ioe) {
                    LOG.error("LetterOfCreditCreateServiceImpl.validatecashControlDocument Stopped: " + ioe.getMessage());
                    throw new RuntimeException("LetterOfCreditCreateServiceImpl.validatecashControlDocument Stopped: " + ioe.getMessage(), ioe);
                }
            }

        }
        return isExists;
    }



    /**
     * This method retrieves all the cash control and payment application docs with a status of 'I' and routes them to the next step in the
     * routing path.
     *
     * @return True if the routing was performed successfully. A runtime exception will be thrown if any errors occur while routing.
     *
     */
    @Override
    public boolean routeLOCDocuments() {
        List<String> cashControlDocumentIdList = null;
        List<String> payAppDocumentIdList = null;
        try {
            cashControlDocumentIdList = retrieveCashControlDocumentsToRoute(DocumentStatus.SAVED);
            payAppDocumentIdList = retrievePayAppDocumentsToRoute(DocumentStatus.SAVED);
        }
        catch (WorkflowException e1) {
            LOG.error("Error retrieving LOC documents for routing: " + e1.getMessage(), e1);
            throw new RuntimeException(e1.getMessage(), e1);
        }
        catch (RemoteException re) {
            LOG.error("Error retrieving LOC documents for routing: " + re.getMessage(), re);
            throw new RuntimeException(re.getMessage(), re);
        }

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
    protected List<String> retrieveCashControlDocumentsToRoute(DocumentStatus statusCode) throws WorkflowException, RemoteException {
        List<String> documentIds = new ArrayList<String>();

        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(KFSConstants.FinancialDocumentTypeCodes.CASH_CONTROL);
        criteria.setDocumentStatuses(Collections.singletonList(statusCode));

        DocumentSearchCriteria crit = criteria.build();

        int maxResults = financialSystemDocumentService.getMaxResultCap(crit);
        int iterations = financialSystemDocumentService.getFetchMoreIterationLimit();

        for (int i = 0; i < iterations; i++) {
            LOG.debug("Fetch Iteration: "+ i);
            criteria.setStartAtIndex(maxResults * i);
            crit = criteria.build();
            LOG.debug("Max Results: "+criteria.getStartAtIndex());
        DocumentSearchResults results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(
                    GlobalVariables.getUserSession().getPrincipalId(), crit);
            if (results.getSearchResults().isEmpty()) {
                break;
            }
        for (DocumentSearchResult resultRow: results.getSearchResults()) {
            documentIds.add(resultRow.getDocument().getDocumentId());
                LOG.debug(resultRow.getDocument().getDocumentId());
        }
        }
        return documentIds;
    }


    /**
     * Returns a list of all initiated but not yet routed documents, using the KualiWorkflowInfo service.
     *
     * @return a list of documents to route
     */
    protected List<String> retrievePayAppDocumentsToRoute(DocumentStatus statusCode) throws WorkflowException, RemoteException {
        List<String> documentIds = new ArrayList<String>();

        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(ArConstants.PAYMENT_APPLICATION_DOCUMENT_TYPE_CODE);
        criteria.setDocumentStatuses(Collections.singletonList(statusCode));

        DocumentSearchCriteria crit = criteria.build();

        int maxResults = financialSystemDocumentService.getMaxResultCap(crit);
        int iterations = financialSystemDocumentService.getFetchMoreIterationLimit();

        for (int i = 0; i < iterations; i++) {
            LOG.debug("Fetch Iteration: "+ i);
            criteria.setStartAtIndex(maxResults * i);
            crit = criteria.build();
            LOG.debug("Max Results: "+criteria.getStartAtIndex());
        DocumentSearchResults results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(
                    GlobalVariables.getUserSession().getPrincipalId(), crit);
            if (results.getSearchResults().isEmpty()) {
                break;
            }
        for (DocumentSearchResult resultRow: results.getSearchResults()) {
            documentIds.add(resultRow.getDocument().getDocumentId());
                LOG.debug(resultRow.getDocument().getDocumentId());
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
        printStream.printf("%s\n", line);
    }

    /**
     * @see org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService#processLettersOfCreditByFund(java.lang.String)
     */
    @Override
    public void processLettersOfCreditByFund(String batchFileDirectoryName) {
        try {
            String customerNumber = null;
            String locValue = null;
            String locCreationType = null;
            boolean cashControlDocumentExists = false;
            String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());

            String errOutputFile = batchFileDirectoryName + File.separator + ArConstants.BatchFileSystem.LOC_CREATION_BY_LOCF_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
            // To create error file and store all the errors in it.
            File errOutPutFile = new File(errOutputFile);
            PrintStream outputFileStream = null;
            try {
                outputFileStream = new PrintStream(errOutPutFile);

                // Case 2. set locCreationType = By Letter of Credit Fund
                locCreationType = ArConstants.LOC_BY_LOC_FUND;
                // Get the list of LOC Funds from the Maintenance document.
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(KFSPropertyConstants.ACTIVE, true);
                Collection<ContractsAndGrantsLetterOfCreditFund> letterOfCreditFunds = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsLetterOfCreditFund.class).getExternalizableBusinessObjectsList(ContractsAndGrantsLetterOfCreditFund.class, map);
                Iterator<ContractsAndGrantsLetterOfCreditFund> it = letterOfCreditFunds.iterator();
                while (it.hasNext()) {
                    ContractsAndGrantsLetterOfCreditFund letterOfCreditFund = it.next();
                    // Retrieve invoices per loc fund and loc creation type specification.
                    Collection<ContractsGrantsInvoiceDocument> cgInvoices = retrieveOpenAndFinalCGInvoicesByLOCFund(letterOfCreditFund.getLetterOfCreditFundCode(), errOutputFile);
                    // When all the invoices are final.
                    if (!CollectionUtils.isEmpty(cgInvoices)) {
                        KualiDecimal totalAmount = KualiDecimal.ZERO;
                        // Get the total amount for the cash control document.
                        for (ContractsGrantsInvoiceDocument cgInvoice : cgInvoices) {
                            totalAmount = totalAmount.add(cgInvoice.getOpenAmount());
                        }
                        customerNumber = cgInvoices.iterator().next().getAccountsReceivableDocumentHeader().getCustomerNumber();// to get customer number from first invoice
                        // If this is LOC by fund, then the way to retrieve invoices in payment application form is locfundCode
                        locValue = letterOfCreditFund.getLetterOfCreditFundCode();
                        // To validate if there are any existing cash control documents for the same letterofcreditfundgroup and customer
                        // number combination.

                        cashControlDocumentExists = validatecashControlDocument(customerNumber, locCreationType, locValue, outputFileStream);

                        if (!cashControlDocumentExists) {
                            // Pass the parameters and error file stream to maintain a single file for recording all the errors.
                            createCashControlDocuments(customerNumber, locCreationType, locValue, totalAmount, outputFileStream);
                        }
                    }
                }
            } finally {
                // Close the error file.
                if (outputFileStream != null) {
                    outputFileStream.close();
                }
            }
        }
        catch (FileNotFoundException fnfe) {
            throw new RuntimeException("Could not write to file in "+batchFileDirectoryName, fnfe);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService#processLettersOfCreditByFundGroup(java.lang.String)
     */
    @Override
    public void processLettersOfCreditByFundGroup(String batchFileDirectoryName) {
        try {
            String customerNumber = null;
            String locValue = null;
            String locCreationType = null;
            boolean cashControlDocumentExists = false;
            String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());

            String errOutputFile = batchFileDirectoryName + File.separator + ArConstants.BatchFileSystem.LOC_CREATION_BY_LOCFG_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
            // To create error file and store all the errors in it.
            File errOutPutFile = new File(errOutputFile);
            PrintStream outputFileStream = null;
            try {
                outputFileStream = new PrintStream(errOutPutFile);
                // Case 2. set locCreationType = By Letter of Credit Fund
                locCreationType = ArConstants.LOC_BY_LOC_FUND_GRP;
                // Retrieve list of letter of credit Fund groups from the Maintenance document.
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(KFSPropertyConstants.ACTIVE, true);
                Collection<ContractsAndGrantsLetterOfCreditFundGroup> letterOfCreditFundGroups = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsLetterOfCreditFundGroup.class).getExternalizableBusinessObjectsList(ContractsAndGrantsLetterOfCreditFundGroup.class, map);

                Iterator<ContractsAndGrantsLetterOfCreditFundGroup> it = letterOfCreditFundGroups.iterator();
                while (it.hasNext()) {
                    ContractsAndGrantsLetterOfCreditFundGroup letterOfCreditFundGroup = it.next();

                    // Retrieve invoices per loc fund group and loc creation type specification.
                    Collection<ContractsGrantsInvoiceDocument> cgInvoices = retrieveOpenAndFinalCGInvoicesByLOCFundGroup(letterOfCreditFundGroup.getLetterOfCreditFundGroupCode(), errOutputFile);
                    if (!CollectionUtils.isEmpty(cgInvoices)) {
                        KualiDecimal totalAmount = KualiDecimal.ZERO;
                        // Get the total amount for the cash control document.
                        for (ContractsGrantsInvoiceDocument cgInvoice : cgInvoices) {
                            totalAmount = totalAmount.add(cgInvoice.getOpenAmount());
                        }
                        customerNumber = cgInvoices.iterator().next().getAccountsReceivableDocumentHeader().getCustomerNumber();// to get customer number from first invoice
                        // If this is LOC by fund group, then the way to retrieve invoices in payment application form is locfundGroupCode
                        locValue = letterOfCreditFundGroup.getLetterOfCreditFundGroupCode();
                        // To validate if there are any existing cash control documents for the same letterofcreditfundgroup and customer
                        // number combination.

                        cashControlDocumentExists = validatecashControlDocument(customerNumber, locCreationType, locValue, outputFileStream);

                        if (!cashControlDocumentExists) {
                            // Pass the parameters and error file stream to maintain a single file for recording all the errors.
                            createCashControlDocuments(customerNumber, locCreationType, locValue, totalAmount, outputFileStream);
                        }
                    }
                }
            } finally {
                // Close the error file.
                if (outputFileStream != null) {
                    outputFileStream.close();
                }
            }
        }
        catch (FileNotFoundException fnfe) {
            throw new RuntimeException("Could not write to file in "+batchFileDirectoryName, fnfe);
        }
    }

    /**
     * This method retrieves all invoices with open and with final status based on loc creation type = LOC fund
     *
     * @param locFund
     * @param errorFileName
     * @return
     */
    protected Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByLOCFund(String locFund, String errorFileName) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(ArConstants.LETTER_OF_CREDIT_CREATION_TYPE, ArConstants.LOC_BY_LOC_FUND);
        fieldValues.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND_CODE, locFund);
        fieldValues.put(ArPropertyConstants.OPEN_INVOICE_IND, "true");
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        String detail = "LOC Creation Type:" + ArConstants.LOC_BY_LOC_FUND + " of value " + locFund;
        cgInvoices = contractsGrantsInvoiceDocumentService.getMatchingInvoicesByCollection(fieldValues);
        List<String> invalidInvoices = validateInvoices(cgInvoices, detail, errorFileName);
        if (!CollectionUtils.isEmpty(invalidInvoices)) {
            return null;

        }
        return cgInvoices;
    }

    /**
     * This method retrieves all invoices with open and with final status based on loc creation type = LOC fund group
     *
     * @param locFundGroup
     * @param errorFileName
     * @return
     */
    protected Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByLOCFundGroup(String locFundGroup, String errorFileName) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(ArConstants.LETTER_OF_CREDIT_CREATION_TYPE, ArConstants.LOC_BY_LOC_FUND_GRP);
        fieldValues.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND_GROUP_CODE, locFundGroup);
        fieldValues.put(ArPropertyConstants.OPEN_INVOICE_IND, "true");
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        String detail = "LOC Creation Type:" + ArConstants.LOC_BY_LOC_FUND_GRP + " of value " + locFundGroup;
        cgInvoices = contractsGrantsInvoiceDocumentService.getMatchingInvoicesByCollection(fieldValues);
        List<String> invalidInvoices = validateInvoices(cgInvoices, detail, errorFileName);
        if (!CollectionUtils.isEmpty(invalidInvoices)) {
            return null;

        }
        return cgInvoices;
    }

    /**
     * This method validates invoices and output an error file including unqualified invoices with reason stated.
     *
     * @param cgInvoices
     * @param outputFileStream
     * @return
     */
    protected List<String> validateInvoices(Collection<ContractsGrantsInvoiceDocument> cgInvoices, String detail, String errorFileName) {
        boolean isInvalid = false;
        String line = null;
        List<String> invalidGroup = new ArrayList<String>();
        try {
            if (CollectionUtils.isEmpty(cgInvoices)) {
                line = "There were no invoices retrieved to process for " + detail;
                invalidGroup.add(line);

                File errOutPutFile = new File(errorFileName);
                PrintStream outputFileStream = null;

                outputFileStream = new PrintStream(errOutPutFile);
                outputFileStream.println(line);

                return invalidGroup;
            }
            for (ContractsGrantsInvoiceDocument cgInvoice : cgInvoices) {
                isInvalid = false;
                // if the invoices are not final yet - then the LOC cannot be created
                if (!cgInvoice.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equalsIgnoreCase(KFSConstants.DocumentStatusCodes.APPROVED)) {
                    line = "Contracts Grants Invoice# " + cgInvoice.getDocumentNumber() + " : " + ArKeyConstants.LOC_CREATION_ERROR_INVOICE_NOT_FINAL;
                    invalidGroup.add(line);
                    isInvalid = true;
                }

                // if invalid is true, the award is unqualified.
                // records the unqualified award with failed reasons.
                if (isInvalid) {
                    File errOutPutFile = new File(errorFileName);
                    PrintStream outputFileStream = null;

                    outputFileStream = new PrintStream(errOutPutFile);
                    outputFileStream.println(line);
                    outputFileStream.println();
                }
            }
        }
        catch (IOException ioe) {
            LOG.error("LetterOfCreditCreateServiceImpl.validateInvoices Stopped: " + ioe.getMessage());
            throw new RuntimeException("LetterOfCreditCreateServiceImpl.validateInvoices Stopped: " + ioe.getMessage(), ioe);
        }

        return invalidGroup;
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

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
