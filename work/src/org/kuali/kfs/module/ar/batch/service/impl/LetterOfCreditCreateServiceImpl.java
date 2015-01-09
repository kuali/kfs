/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.batch.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import org.kuali.rice.kew.api.document.DocumentStatus;
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

    /**
     * This method created cashcontrol documents and payment application based on the loc creation type and loc value passed.
     *
     * @param customerNumber
     * @param totalAmount
     * @param errorFile
     * @return
     */
    @Override
    public String createCashControlDocuments(String customerNumber, KualiDecimal totalAmount, PrintWriter errorFile) throws IOException {
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

            cashControlDoc.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

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
            errorFile.println(error);
        }

        return documentNumber;
    }


    /**
     * The method validates if there are any existing cash control documents for the same locValue and customer number combination.
     *
     * @param customerNumber
     * @param outputFileStream
     * @return
     */
    @Override
    public boolean validateCashControlDocument(String customerNumber, PrintWriter errorFile) throws IOException {
        boolean isExists = false;

        Map<String,String> fieldValues = new HashMap<String,String>();

        fieldValues.put("cashControlDetails.customerNumber", customerNumber);

        CashControlDocument cashControlDocument = cashControlDocumentDao.getCashControlDocument(fieldValues);
        if (!ObjectUtils.isNull(cashControlDocument)) {
            // Now to check if there is a cash control detail with the same customer number. - just double checking.
            List<CashControlDetail> cashControlDetails = new ArrayList<CashControlDetail>();
            List<CashControlDetail> cashCtrlDetails = cashControlDocument.getCashControlDetails();

            for (CashControlDetail cashControlDetail : cashCtrlDetails) {
                if (!ObjectUtils.isNull(cashControlDetail.getCustomerNumber()) && cashControlDetail.getCustomerNumber().equals(customerNumber)) {
                    cashControlDetails.add(cashControlDetail);
                }
            }

            if (!CollectionUtils.isEmpty(cashControlDetails)) {
                isExists = true;
                String error = ArKeyConstants.LOC_CREATION_ERROR__CSH_CTRL_IN_PROGRESS + " (" + cashControlDocument.getDocumentNumber() + ")" + " for Customer Number #" + customerNumber;
                errorFile.println(error);
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
        Collection<CashControlDocument> cashControlDocuments = null;
        Collection<PaymentApplicationDocument> payAppDocuments = null;
        try {
            cashControlDocuments = retrieveCashControlDocumentsToRoute(DocumentStatus.SAVED);
            payAppDocuments = retrievePayAppDocumentsToRoute(DocumentStatus.SAVED);
        }
        catch (WorkflowException e1) {
            LOG.error("Error retrieving LOC documents for routing: " + e1.getMessage(), e1);
            throw new RuntimeException(e1.getMessage(), e1);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Cash control Documents to Route: " + cashControlDocuments.size());
            LOG.info("Payment Application Documents to Route: " + payAppDocuments.size());
        }
        //1. Cash control documents
        final String currentUserPrincipalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        for (CashControlDocument cashControlDoc : cashControlDocuments) {
            try {
                //To route documents only if the user in the session is same as the initiator.
                if(StringUtils.equals(cashControlDoc.getFinancialSystemDocumentHeader().getInitiatorPrincipalId(), currentUserPrincipalId)) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Routing Cash control document # " + cashControlDoc.getDocumentNumber() + ".");
                    }
                    documentService.prepareWorkflowDocument(cashControlDoc);

                    // calling workflow service to bypass business rule checks
                    workflowDocumentService.route(cashControlDoc.getDocumentHeader().getWorkflowDocument(), "", null);
                }
            }
            catch (WorkflowException e) {
                LOG.error("Error routing document # " + cashControlDoc.getDocumentNumber() + " " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        //1. Payment Application documents
        for (PaymentApplicationDocument payAppDoc : payAppDocuments) {
            try {
                //To route documents only if the user in the session is same as the initiator.
                if(StringUtils.equals(payAppDoc.getFinancialSystemDocumentHeader().getInitiatorPrincipalId(), currentUserPrincipalId)) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Routing PAyment Application document # " + payAppDoc.getDocumentNumber() + ".");
                    }
                    documentService.prepareWorkflowDocument(payAppDoc);

                    // calling workflow service to bypass business rule checks
                    workflowDocumentService.route(payAppDoc.getDocumentHeader().getWorkflowDocument(), "", null);
                }
            }
            catch (WorkflowException e) {
                LOG.error("Error routing document # " + payAppDoc.getDocumentNumber() + " " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return true;
    }


    /**
     * Returns a list of all initiated but not yet routed documents, using the FinancialSystemDocumentService service.
     *
     * @return a list of documents to route
     */
    protected Collection<CashControlDocument> retrieveCashControlDocumentsToRoute(DocumentStatus statusCode) throws WorkflowException {
        final Collection<CashControlDocument> cashControlDocuments = getFinancialSystemDocumentService().findByWorkflowStatusCode(CashControlDocument.class, statusCode);
        return cashControlDocuments;
    }


    /**
     * Returns a list of all initiated but not yet routed documents, using the FinancialSystemDocumentService service.
     *
     * @return a list of documents to route
     */
    protected Collection<PaymentApplicationDocument> retrievePayAppDocumentsToRoute(DocumentStatus statusCode) throws WorkflowException {
        final Collection<PaymentApplicationDocument> paymentApplicationDocuments = getFinancialSystemDocumentService().findByWorkflowStatusCode(PaymentApplicationDocument.class, statusCode);
        return paymentApplicationDocuments;
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
     * @see org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService#processLettersOfCreditByFund(java.lang.String)
     */
    @Override
    public void processLettersOfCreditByFund(String batchFileDirectoryName) {
        String customerNumber = null;
        String locValue = null;
        String locCreationType = null;
        boolean cashControlDocumentExists = false;
        String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());

        String errOutputFile = batchFileDirectoryName + File.separator + ArConstants.BatchFileSystem.LOC_CREATION_BY_LOCF_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
        // To create error file and store all the errors in it.
        File errOutPutFile = new File(errOutputFile);
        PrintWriter errorFile = null;
        try {
            errorFile = new PrintWriter(errOutPutFile);

            // Case 2. set locCreationType = By Letter of Credit Fund
            locCreationType = ArConstants.LOC_BY_LOC_FUND;
            // Get the list of LOC Funds from the Maintenance document.
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.ACTIVE, true);
            Collection<ContractsAndGrantsLetterOfCreditFund> letterOfCreditFunds = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsLetterOfCreditFund.class).getExternalizableBusinessObjectsList(ContractsAndGrantsLetterOfCreditFund.class, map);
            for (ContractsAndGrantsLetterOfCreditFund letterOfCreditFund : letterOfCreditFunds) {
                // Retrieve invoices per loc fund and loc creation type specification.
                Collection<ContractsGrantsInvoiceDocument> cgInvoices = retrieveOpenAndFinalCGInvoicesByLOCFund(letterOfCreditFund.getLetterOfCreditFundCode(), errorFile);
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

                    cashControlDocumentExists = validateCashControlDocument(customerNumber, errorFile);

                    if (!cashControlDocumentExists) {
                        // Pass the parameters and error file stream to maintain a single file for recording all the errors.
                        createCashControlDocuments(customerNumber, totalAmount, errorFile);
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("Could not write to file in "+batchFileDirectoryName, fnfe);
        } catch (IOException ioe) {
            throw new RuntimeException("");
        } finally {
            // Close the error file.
            if (errorFile != null) {
                errorFile.close();
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService#processLettersOfCreditByFundGroup(java.lang.String)
     */
    @Override
    public void processLettersOfCreditByFundGroup(String batchFileDirectoryName) {
        String customerNumber = null;
        String locValue = null;
        String locCreationType = null;
        boolean cashControlDocumentExists = false;
        String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());

        String errOutputFile = batchFileDirectoryName + File.separator + ArConstants.BatchFileSystem.LOC_CREATION_BY_LOCFG_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
        // To create error file and store all the errors in it.
        File errOutPutFile = new File(errOutputFile);
        PrintWriter errorFile = null;
        try {
            errorFile = new PrintWriter(errOutPutFile);
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
                Collection<ContractsGrantsInvoiceDocument> cgInvoices = retrieveOpenAndFinalCGInvoicesByLOCFundGroup(letterOfCreditFundGroup.getLetterOfCreditFundGroupCode(), errorFile);
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

                    cashControlDocumentExists = validateCashControlDocument(customerNumber, errorFile);

                    if (!cashControlDocumentExists) {
                        // Pass the parameters and error file stream to maintain a single file for recording all the errors.
                        createCashControlDocuments(customerNumber, totalAmount, errorFile);
                    }
                }
            }
        }catch (FileNotFoundException fnfe) {
            throw new RuntimeException("Could not write to file in "+batchFileDirectoryName, fnfe);
        } catch (IOException ioe) {
            throw new RuntimeException("Could not write to error file", ioe);
        } finally {
            // Close the error file.
            if (errorFile != null) {
                errorFile.close();
            }
        }
    }

    /**
     * This method retrieves all invoices with open and with final status based on loc creation type = LOC fund
     *
     * @param locFund
     * @param errorFile
     * @return
     */
    protected Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByLOCFund(String locFund, PrintWriter errorFile) throws IOException {
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = retrieveLetterOfCreditInvoices(locFund, ArConstants.LOC_BY_LOC_FUND);

        final String detailMessagePattern = getConfigService().getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_CREATION_TYPE);
        String detail = MessageFormat.format(detailMessagePattern, ArConstants.LOC_BY_LOC_FUND, locFund);
        List<String> invalidInvoices = validateInvoices(cgInvoices, detail, errorFile);
        if (!CollectionUtils.isEmpty(invalidInvoices)) {
            return null;

        }
        return cgInvoices;
    }


    /**
     * Retrieves ContractsAndGrantsInvoiceDocument documents which are open and which have the given letter of credit fund and letter of credit creation type (either fund or fund group)
     * @param locFund the code of the fund or fund group
     * @param creationType whether to search by fund or fund group
     * @return a Collection of matching letter of credit created contracts and grants invoices
     */
    protected Collection<ContractsGrantsInvoiceDocument> retrieveLetterOfCreditInvoices(String locFund, final String creationType) {
        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put(ArPropertyConstants.LETTER_OF_CREDIT_CREATION_TYPE, creationType);
        fieldValues.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND_CODE, locFund);
        fieldValues.put(ArPropertyConstants.OPEN_INVOICE_IND, KFSConstants.Booleans.TRUE);
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentService.getMatchingInvoicesByCollection(fieldValues);
        return cgInvoices;
    }

    /**
     * This method retrieves all invoices with open and with final status based on loc creation type = LOC fund group
     *
     * @param locFundGroup
     * @param errorFileName
     * @return
     */
    protected Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByLOCFundGroup(String locFundGroup, PrintWriter errorFile) throws IOException {
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = retrieveLetterOfCreditInvoices(locFundGroup, ArConstants.LOC_BY_LOC_FUND_GRP);

        final String detailMessagePattern = getConfigService().getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_CREATION_TYPE);
        String detail = MessageFormat.format(detailMessagePattern, ArConstants.LOC_BY_LOC_FUND_GRP, locFundGroup);
        List<String> invalidInvoices = validateInvoices(cgInvoices, detail, errorFile);
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
    protected List<String> validateInvoices(Collection<ContractsGrantsInvoiceDocument> cgInvoices, String detail, PrintWriter errorFile) throws IOException {
        boolean isInvalid = false;
        String line = null;
        List<String> invalidGroup = new ArrayList<String>();
        if (CollectionUtils.isEmpty(cgInvoices)) {
            line = "There were no invoices retrieved to process for " + detail;
            invalidGroup.add(line);
            errorFile.println(line);

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

            // if invalid is true, the award is unqualified. Records with the reasons for failure
            if (isInvalid) {
                errorFile.println(line);
                errorFile.println();
            }
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
}
