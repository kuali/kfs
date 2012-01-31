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
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDetailDao;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDocumentDao;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Defines a service class for creating Cash Control documents from the LOC Review Document.
 */
public class LetterOfCreditCreateServiceImpl implements LetterOfCreditCreateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LetterOfCreditCreateServiceImpl.class);

    private DocumentService documentService;
    private CashControlDocumentService cashControlDocumentService;
    private CashControlDocumentDao cashControlDocumentDao;
    private CashControlDetailDao cashControlDetailDao;

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
            cashControlDoc.getDocumentHeader().setDocumentDescription(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(ArKeyConstants.CASH_CTRL_DOC_CREATED_BY_BATCH));
            AccountsReceivableDocumentHeaderService arDocHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
            AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
            accountsReceivableDocumentHeader.setDocumentNumber(cashControlDoc.getDocumentNumber());
            // To get default processing chart code and org code from Paramters
            String defaultProcessingChartCode = SpringContext.getBean(ParameterService.class).getParameterValue(CashControlDocument.class, ArConstants.DEFAULT_PROCESSING_CHART);
            String defaultProcessingOrgCode = SpringContext.getBean(ParameterService.class).getParameterValue(CashControlDocument.class, ArConstants.DEFAULT_PROCESSING_ORG);
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

            cashControlDocumentService.addNewCashControlDetail(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC), cashControlDoc, cashControlDetail);

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


}
