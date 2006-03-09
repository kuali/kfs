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
package org.kuali.module.financial.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.exceptions.UnknownDocumentIdException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.CashReceiptHeader;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.bo.DepositCashReceiptControl;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.exceptions.InvalidCashDrawerState;
import org.kuali.module.financial.exceptions.InvalidCashReceiptState;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashManagementService;

import edu.iu.uis.eden.exception.DocumentNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;


/**
 * Stock CashManagementService implementation.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashManagementServiceImpl implements CashManagementService {
    private BusinessObjectService businessObjectService;
    private WorkflowDocumentService workflowDocumentService;
    private DocumentService documentService;
    private CashDrawerService cashDrawerService;

    /**
     * @see org.kuali.module.financial.service.CashManagementService#createCashManagementDocument(java.util.List, java.lang.String)
     */
    public CashManagementDocument createCashManagementDocument(String documentDescription, List verifiedCashReceipts,
            String workgroupName) throws WorkflowException {
        CashManagementDocument cmDoc = null;

        // check and lock cash drawer
        closeCashDrawer(workgroupName);

        try {
            // create the document
            cmDoc = (CashManagementDocument) documentService.getNewDocument(CashManagementDocument.class);
            cmDoc.getDocumentHeader().setFinancialDocumentDescription(documentDescription);
            cmDoc.setWorkgroupName(workgroupName);

            // create and associate the Deposit
            Deposit deposit = createDeposit(cmDoc, new Integer(0), verifiedCashReceipts, workgroupName);

            List depositList = new ArrayList();
            depositList.add(deposit);
            cmDoc.setDeposits(depositList);

            // persist everything
            documentService.save(cmDoc, "service-created CashManagementDocument", null);
        }
        catch (RuntimeException e) {
            // reopen the drawer if creation failed (without trapping the failure-to-close)
            openCashDrawer(workgroupName);

            throw e;
        }
        catch (WorkflowException e) {
            // reopen the drawer if creation failed (without trapping the failure-to-close)
            openCashDrawer(workgroupName);

            throw e;
        }

        return cmDoc;
    }

    private void closeCashDrawer(String workgroupName) {
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(workgroupName);
        if ((drawer != null) && StringUtils.equals(drawer.getStatusCode(), Constants.CashDrawerConstants.STATUS_CLOSED)) {
            throw new InvalidCashDrawerState("cash drawer for workgroup '" + workgroupName + "' is already closed");
        }

        cashDrawerService.closeCashDrawer(workgroupName);
    }

    private void openCashDrawer(String workgroupName) {
        cashDrawerService.openCashDrawer(workgroupName);
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#createDeposit(CashManagementDocument, java.util.List,
     *      java.lang.String)
     */
    public Deposit createDeposit(CashManagementDocument cashManagementDoc, Integer lineNumber, List verifiedCashReceipts,
            String workgroupName) {
        if (cashManagementDoc == null) {
            throw new IllegalArgumentException("invalid (null) cashManagementDoc");
        }
        if (lineNumber == null) {
            throw new IllegalArgumentException("invalid (null) lineNumber");
        }
        if (verifiedCashReceipts == null) {
            throw new IllegalArgumentException("invalid (null) verifiedCashReceipts list");
        }
        if (verifiedCashReceipts.isEmpty()) {
            throw new IllegalArgumentException("invalid (empty) verifiedCashReceipts list");
        }
        if (StringUtils.isBlank(workgroupName)) {
            throw new IllegalArgumentException("invalid (blank) workgroupName");
        }

        // verify CashReceipts
        if (!validateVerifiedCashReceipts(verifiedCashReceipts)) {
            throw new InvalidCashReceiptState("one or more CashReceipts not in verified status");
        }

        // create the deposit
        Deposit deposit = new Deposit();
        deposit.setFinancialDocumentNumber(cashManagementDoc.getFinancialDocumentNumber());
        deposit.setCashManagementDocument(cashManagementDoc);
        deposit.setFinancialDocumentDepositLineNumber(lineNumber);
        deposit.setFinancialDocumentDepositTypeCode(Constants.DepositConstants.DEPOSIT_TYPE_FINAL);

        businessObjectService.save(deposit);

        // attach the Cash Receipts
        List docHeaders = new ArrayList();
        List dccList = new ArrayList();
        for (Iterator i = verifiedCashReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument crDoc = (CashReceiptDocument) i.next();
            DocumentHeader dh = crDoc.getDocumentHeader();
            dh.setFinancialDocumentStatusCode(Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_DEPOSITED);
            docHeaders.add(dh);

            CashReceiptHeader crHeader = new CashReceiptHeader();
            crHeader.setFinancialDocumentNumber(crDoc.getFinancialDocumentNumber());
            crHeader.setCashReceiptDocument(crDoc);
            crHeader.setWorkgroupName(workgroupName);

            DepositCashReceiptControl dcc = new DepositCashReceiptControl();
            dcc.setFinancialDocumentCashReceiptNumber(crHeader.getFinancialDocumentNumber());
            dcc.setFinancialDocumentDepositNumber(deposit.getFinancialDocumentNumber());
            dcc.setFinancialDocumentDepositLineNumber(deposit.getFinancialDocumentDepositLineNumber());

            dcc.setCashReceiptHeader(crHeader);
            dcc.setDeposit(deposit);

            dccList.add(dcc);
        }
        businessObjectService.save(docHeaders);
        // crHeaders get saved as side-effect of saving dccs
        businessObjectService.save(dccList);

        return deposit;
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#listDeposits(org.kuali.module.financial.document.CashManagementDocument)
     */
    public List retrieveDeposits(CashManagementDocument cashManagementDoc) {
        if (cashManagementDoc == null) {
            throw new IllegalArgumentException("invalid (null) cashManagementDoc");
        }

        Map valueMap = new HashMap();
        valueMap.put("financialDocumentNumber", cashManagementDoc.getFinancialDocumentNumber());

        Collection deposits = businessObjectService.findMatching(Deposit.class, valueMap);

        return new ArrayList(deposits);
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#cancelDeposit(org.kuali.module.financial.bo.Deposit)
     */
    public void cancelDeposit(Deposit deposit) {
        if (deposit == null) {
            throw new IllegalArgumentException("invalid (null) deposit");
        }

        // verify deposit's existence
        Map depositCriteria = new HashMap();
        depositCriteria.put("financialDocumentNumber", deposit.getFinancialDocumentNumber());
        depositCriteria.put("financialDocumentDepositLineNumber", deposit.getFinancialDocumentDepositLineNumber());
        deposit = (Deposit) businessObjectService.findByPrimaryKey(Deposit.class, depositCriteria);

        if (deposit != null) {
            // retrieve CashReceipts, for later use
            List cashReceipts = retrieveCashReceipts(deposit);
            if (!cashReceipts.isEmpty()) {
                // delete join records (which should auto-delete the related CRHeaders)
                Map controlCriteria = new HashMap();
                controlCriteria.put("financialDocumentDepositNumber", deposit.getFinancialDocumentNumber());
                controlCriteria.put("financialDocumentDepositLineNumber", deposit.getFinancialDocumentDepositLineNumber());
                businessObjectService.deleteMatching(DepositCashReceiptControl.class, controlCriteria);

                // clean up CRDocs
                List docHeaders = new ArrayList();
                for (Iterator i = cashReceipts.iterator(); i.hasNext();) {
                    CashReceiptDocument crDoc = (CashReceiptDocument) i.next();
                    DocumentHeader dh = crDoc.getDocumentHeader();
                    dh.setFinancialDocumentStatusCode(Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);

                    docHeaders.add(dh);
                }
                businessObjectService.save(docHeaders);
            }

            // delete the deposit
            businessObjectService.delete(deposit);
        }
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#retrieveCashReceipts(org.kuali.module.financial.bo.Deposit)
     */
    public List retrieveCashReceipts(Deposit deposit) {
        if (deposit == null) {
            throw new IllegalArgumentException("invalid (null) deposit");
        }
        if (StringUtils.isBlank(deposit.getFinancialDocumentNumber())) {
            throw new IllegalArgumentException("invalid (blank) deposit.financialDocumentNumber");
        }
        if (deposit.getFinancialDocumentDepositLineNumber() == null) {
            throw new IllegalArgumentException("invalid (null) deposit.financialDocumentDepositLineNumber");
        }

        Map criteriaMap = new LinkedHashMap();
        criteriaMap.put("depositCashReceiptControl.financialDocumentDepositNumber", deposit.getFinancialDocumentNumber());
        criteriaMap.put("depositCashReceiptControl.financialDocumentDepositLineNumber", deposit
                .getFinancialDocumentDepositLineNumber());

        Collection crHeaders = getBusinessObjectService().findMatching(CashReceiptHeader.class, criteriaMap);

        List cashReceiptDocuments = null;

        if (crHeaders.isEmpty()) {
            cashReceiptDocuments = new ArrayList();
        }
        else {
            List idList = new ArrayList();
            for (Iterator i = crHeaders.iterator(); i.hasNext();) {
                CashReceiptHeader crHeader = (CashReceiptHeader) i.next();
                idList.add(crHeader.getFinancialDocumentNumber());
            }

            try {
                cashReceiptDocuments = getDocumentService()
                        .getDocumentsByListOfDocumentHeaderIds(CashReceiptDocument.class, idList);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve cashReceipts", e);
            }
        }

        return cashReceiptDocuments;
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#getCampusCodeByCashReceiptVerificationUnitWorkgroupName(java.lang.String)
     */
    public String getCampusCodeByCashReceiptVerificationUnitWorkgroupName(String cashReceiptVerificationUnitWorkgroupName) {
        if (StringUtils.isBlank(cashReceiptVerificationUnitWorkgroupName)) {
            throw new IllegalArgumentException("invalid (blank) cashReceiptVerificationUnitWorkgroupName");
        }

        // UNF: once this is doing an actual lookup somewhere, change the test to distinguish between a workgroup from which you can
        // derive a campusCode, and one which you cannot

        return null;
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#validateVerifiedCashReceipts(java.util.List, java.lang.String)
     */
    public boolean validateVerifiedCashReceipts(List cashReceipts) {
        if (cashReceipts == null) {
            throw new IllegalArgumentException("invalid (null) cashReceipts list");
        }
        if (cashReceipts.size() == 0) {
            throw new IllegalArgumentException("invalid (empty) cashReceipts list");
        }

        boolean succeeded = true;

        for (Iterator i = cashReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument cr = (CashReceiptDocument) i.next();

            // UNF: verify that the CRDoc workflow state is final

            if (!StringUtils.equals(cr.getDocumentHeader().getFinancialDocumentStatusCode(),
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED)) {
                succeeded = false;
            }
        }

        return succeeded;
    }

    /**
     * @see org.kuali.module.financial.service.CashManagementService#countVerifiedCashReceiptsByVerificationUnit(java.lang.String)
     */
    public int countVerifiedCashReceiptsByVerificationUnit(String verificationUnitWorkgroupName) throws WorkflowException {
        if (StringUtils.isBlank(verificationUnitWorkgroupName)) {
            throw new IllegalArgumentException("invalid (blank) verificationWorkgroupName");
        }

        Map queryCriteria = buildCriteriaMap(verificationUnitWorkgroupName);
        // UNF: do I need to somehow check the workflow status is FINAL?

        int count = getBusinessObjectService().countMatching(CashReceiptDocument.class, queryCriteria);

        return count;
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#retrieveCashReceiptsByVerificationUnit(java.lang.String,
     *      java.lang.String)
     */
    public List retrieveVerifiedCashReceiptsByVerificationUnit(String verificationUnitWorkgroupName) throws WorkflowException {
        if (StringUtils.isBlank(verificationUnitWorkgroupName)) {
            throw new IllegalArgumentException("invalid (blank) verificationWorkgroupName");
        }

        Map queryCriteria = buildCriteriaMap(verificationUnitWorkgroupName);
        List documents = new ArrayList(getBusinessObjectService().findMatchingOrderBy(CashReceiptDocument.class, queryCriteria,
                Constants.FINANCIAL_DOCUMENT_NUMBER_PROPERTY_NAME, true));

        // now populate each CR doc with its workflow document
        for (Iterator i = documents.iterator(); i.hasNext();) {
            CashReceiptDocument cr = (CashReceiptDocument) i.next();

            KualiWorkflowDocument workflowDocument = null;
            DocumentHeader docHeader = cr.getDocumentHeader();
            try {
                Long documentHeaderId = Long.valueOf(docHeader.getFinancialDocumentNumber());
                KualiUser user = GlobalVariables.getUserSession().getKualiUser();

                workflowDocument = getWorkflowDocumentService().createWorkflowDocument(documentHeaderId, user);
            }
            catch (DocumentNotFoundException e) {
                throw new UnknownDocumentIdException("no document found for documentHeaderId '"
                        + docHeader.getFinancialDocumentNumber() + "'", e);
            }

            docHeader.setWorkflowDocument(workflowDocument);
        }

        // UNF: verify doc state is FINAL

        return documents;
    }

    private Map buildCriteriaMap(String workgroupName) {
        Map queryCriteria = new HashMap();
        queryCriteria.put(Constants.DOCUMENT_HEADER_PROPERTY_NAME + "."
                + Constants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME,
                Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);

        // UNF: once getCampusCode... returns meaningful values, this if should probably short-circuit when no campusCode is
        // returned rather than selecting all CashReceipts
        String campusLocationCode = getCampusCodeByCashReceiptVerificationUnitWorkgroupName(workgroupName);
        if (StringUtils.isNotBlank(campusLocationCode)) {
            queryCriteria.put(Constants.CashReceiptConstants.CASH_RECEIPT_CAMPUS_LOCATION_CODE_PROPERTY_NAME, campusLocationCode);
        }

        return queryCriteria;
    }


    // injected dependencies
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setCashDrawerService(CashDrawerService cashDrawerService) {
        this.cashDrawerService = cashDrawerService;
    }

    public CashDrawerService getCashDrawerService() {
        return cashDrawerService;
    }


}