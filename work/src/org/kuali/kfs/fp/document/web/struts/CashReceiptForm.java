/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.util.LabelValueBean;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.document.service.CashReceiptCoverSheetService;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.web.format.SimpleBooleanFormatter;

/**
 * This class is the action form for Cash Receipts.
 */
public class CashReceiptForm extends KualiAccountingDocumentFormBase implements CapitalAssetEditable{
    private static final long serialVersionUID = 1L;
    private static final String CAN_PRINT_COVERSHEET_SIG_STR = "isCoverSheetPrintingAllowed";

    private Check newCheck;

    private KualiDecimal checkTotal;

    private String checkEntryMode;
    private List checkEntryModes;

    private List baselineChecks;
    
    private CapitalAssetInformation capitalAssetInformation;

    /**
     * Constructs a CashReceiptForm.java.
     */
    public CashReceiptForm() {
        super();
        setFormatterType(CAN_PRINT_COVERSHEET_SIG_STR, SimpleBooleanFormatter.class);
        setDocument(new CashReceiptDocument());
        setNewCheck(getCashReceiptDocument().createNewCheck());

        checkEntryModes = new ArrayList();
        checkEntryModes.add(new LabelValueBean("Individual Checks/Batches", CashReceiptDocument.CHECK_ENTRY_DETAIL));
        checkEntryModes.add(new LabelValueBean("Total Only", CashReceiptDocument.CHECK_ENTRY_TOTAL));
        
        baselineChecks = new ArrayList();
        
        this.setCapitalAssetInformation(new CapitalAssetInformation());
    }
    
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        setCheckEntryMode(getCashReceiptDocument().getCheckEntryMode());
    }

    /**
     * @return CashReceiptDocument
     */
    public CashReceiptDocument getCashReceiptDocument() {
        return (CashReceiptDocument) getDocument();
    }

    /**
     * @return Check
     */
    public Check getNewCheck() {
        return newCheck;
    }

    /**
     * @param newCheck
     */
    public void setNewCheck(Check newCheck) {
        this.newCheck = newCheck;
    }

    /**
     * @param checkTotal
     */
    public void setCheckTotal(KualiDecimal checkTotal) {
        this.checkTotal = checkTotal;
    }

    /**
     * @return KualiDecimal
     */
    public KualiDecimal getCheckTotal() {
        return checkTotal;
    }

    /**
     * @return List of LabelValueBeans representing all available check entry modes
     */
    public List getCheckEntryModes() {
        return checkEntryModes;
    }

    /**
     * @return String
     */
    public String getCheckEntryMode() {
        return checkEntryMode;
    }

    /**
     * @param checkEntryMode
     */
    public void setCheckEntryMode(String checkEntryMode) {
        this.checkEntryMode = checkEntryMode;
    }

    /**
     * @return boolean
     */
    public boolean isCheckEntryDetailMode() {
        return CashReceiptDocument.CHECK_ENTRY_DETAIL.equals(getCheckEntryMode());
    }

    /**
     * @return current List of baseline checks for use in update detection
     */
    public List getBaselineChecks() {
        return baselineChecks;
    }

    /**
     * Sets the current List of baseline checks to the given List
     * 
     * @param baselineChecks
     */
    public void setBaselineChecks(List baselineChecks) {
        this.baselineChecks = baselineChecks;
    }

    /**
     * @param index
     * @return true if a baselineCheck with the given index exists
     */
    public boolean hasBaselineCheck(int index) {
        boolean has = false;

        if ((index >= 0) && (index <= baselineChecks.size())) {
            has = true;
        }

        return has;
    }

    /**
     * Implementation creates empty Checks as a side-effect, so that Struts' efforts to set fields of lines which haven't been
     * created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * @return baseline Check at the given index
     */
    public Check getBaselineCheck(int index) {
        while (baselineChecks.size() <= index) {
            baselineChecks.add(getCashReceiptDocument().createNewCheck());
        }
        return (Check) baselineChecks.get(index);
    }

    /**
     * Gets the financialDocumentStatusMessage which is dependent upon document state.
     * 
     * @return Returns the financialDocumentStatusMessage.
     */
    public String getFinancialDocumentStatusMessage() {
        String financialDocumentStatusMessage = "";
        CashReceiptDocument crd = getCashReceiptDocument();
        String financialDocumentStatusCode = crd.getDocumentHeader().getFinancialDocumentStatusCode();
        if (financialDocumentStatusCode.equals(CashReceipt.VERIFIED)) {
            financialDocumentStatusMessage = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.CashReceipt.MSG_VERIFIED_BUT_NOT_AWAITING_DEPOSIT);
        }
        else if (financialDocumentStatusCode.equals(CashReceipt.INTERIM) || financialDocumentStatusCode.equals(CashReceipt.FINAL)) {
            CashManagementDocument cmd = SpringContext.getBean(CashManagementService.class).getCashManagementDocumentForCashReceiptId(crd.getDocumentNumber());
            if (cmd != null) {
                String cmdFinancialDocNbr = cmd.getDocumentNumber();

                String loadCMDocUrl = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.CashManagement.URL_LOAD_DOCUMENT_CASH_MGMT);
                loadCMDocUrl = StringUtils.replace(loadCMDocUrl, "{0}", cmdFinancialDocNbr);

                financialDocumentStatusMessage = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.CashReceipt.MSG_VERIFIED_AND_AWAITING_DEPOSIT);
                financialDocumentStatusMessage = StringUtils.replace(financialDocumentStatusMessage, "{0}", loadCMDocUrl);
            }
        }
        else if (financialDocumentStatusCode.equals(KFSConstants.DocumentStatusCodes.APPROVED)) {
            CashManagementDocument cmd = SpringContext.getBean(CashManagementService.class).getCashManagementDocumentForCashReceiptId(crd.getDocumentNumber());
            if (cmd != null) {
                String cmdFinancialDocNbr = cmd.getDocumentNumber();

                String loadCMDocUrl = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.CashManagement.URL_LOAD_DOCUMENT_CASH_MGMT);
                loadCMDocUrl = StringUtils.replace(loadCMDocUrl, "{0}", cmdFinancialDocNbr);

                financialDocumentStatusMessage = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.CashReceipt.MSG_VERIFIED_AND_DEPOSITED);
                financialDocumentStatusMessage = StringUtils.replace(financialDocumentStatusMessage, "{0}", loadCMDocUrl);
            }
        }
        return financialDocumentStatusMessage;
    }

    /**
     * This method will build out a message in the case the document is ENROUTE and the cash drawer is closed.
     * 
     * @return String
     */
    public String getCashDrawerStatusMessage() {
        String cashDrawerStatusMessage = "";
        CashReceiptDocument crd = getCashReceiptDocument();

        // first check to see if the document is in the appropriate state for this message
        if (crd != null && crd.getDocumentHeader() != null && crd.getDocumentHeader().getWorkflowDocument() != null) {
            if (crd.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
                CashDrawer cd = SpringContext.getBean(CashDrawerService.class).getByCampusCode(crd.getCampusLocationCode());
                if (cd != null && crd.getDocumentHeader().getWorkflowDocument().isApprovalRequested() && cd.isClosed() && !crd.getDocumentHeader().getWorkflowDocument().isAdHocRequested()) {
                    cashDrawerStatusMessage = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.CashReceipt.MSG_CASH_DRAWER_CLOSED_VERIFICATION_NOT_ALLOWED);
                    cashDrawerStatusMessage = StringUtils.replace(cashDrawerStatusMessage, "{0}", crd.getCampusLocationCode());
                }
            }
        }

        return cashDrawerStatusMessage;
    }

    /**
     * determines if the <code>{@link CashReceiptDocument}</code> is in a state that allows printing of the cover sheet.
     * 
     * @return boolean
     */
    public boolean isCoverSheetPrintingAllowed() {
        return SpringContext.getBean(CashReceiptCoverSheetService.class).isCoverSheetPrintingAllowed(getCashReceiptDocument());
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public CapitalAssetInformation getCapitalAssetInformation() {
        return this.capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;        
    }
}
