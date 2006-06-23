/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.Constants.DepositConstants;
import org.kuali.core.document.FinancialDocumentBase;
import org.kuali.core.service.impl.DocumentServiceImpl;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.Deposit;

/**
 * This class represents the CashManagementDocument.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashManagementDocument extends FinancialDocumentBase {
    private static final long serialVersionUID = 7475843770851900297L;
    private static Logger LOG = Logger.getLogger(CashManagementDocument.class);

    private String workgroupName;
    private String referenceFinancialDocumentNumber;

    private List deposits;


    /**
     * Default constructor.
     */
    public CashManagementDocument() {
        super();
        deposits = new ArrayList();
    }


    /**
     * @return current value of referenceFinancialDocumentNumber.
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute value.
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }


    /**
     * @return current value of workgroupName.
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute value.
     * 
     * @param workgroupName The workgroupName to set.
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }

    /**
     * Derives and returns the cash drawer status for the document's workgroup
     */
    public String getCashDrawerStatus() {
        CashDrawer drawer = SpringServiceLocator.getCashDrawerService().getByWorkgroupName(getWorkgroupName(), true);
        String statusCode = drawer.getStatusCode();

        return statusCode;
    }

    /**
     * @param cashDrawerStatus
     */
    public void setCashDrawerStatus(String cashDrawerStatus) {
        // ignored, because that value is dynamically retrieved from the service
        // required, because POJO pitches a fit if this method doesn't exist
    }

    /**
     * Alias for getCashDrawerStatus which avoids the automagic formatting
     */
    public String getRawCashDrawerStatus() {
        return getCashDrawerStatus();
    }

    /* Deposit-list maintenance */
    /**
     * @return current List of Deposits
     */
    public List getDeposits() {
        return deposits;
    }

    /**
     * Sets the current List of Deposits
     * 
     * @param deposits
     */
    public void setDeposits(List deposits) {
        this.deposits = deposits;
    }

    /**
     * Implementation creates empty Deposits as a side-effect, so that Struts' efforts to set fields of lines which haven't been
     * created will succeed rather than causing a NullPointerException.
     * 
     * @return Deposit at the given index
     */
    public Deposit getDeposit(int index) {
        extendDeposits(index + 1);

        return (Deposit) deposits.get(index);
    }

    /**
     * Removes and returns the Deposit at the given index.
     * 
     * @param index
     * @return Deposit at the given index
     */
    public Deposit removeDeposit(int index) {
        extendDeposits(index + 1);

        return (Deposit) deposits.remove(index);
    }


    /**
     * @return true if one of the Deposits contained in this document has a type of "final"
     */
    public boolean hasFinalDeposit() {
        boolean hasFinal = false;

        for (Iterator i = deposits.iterator(); !hasFinal && i.hasNext();) {
            Deposit d = (Deposit) i.next();

            hasFinal = StringUtils.equals(DepositConstants.DEPOSIT_TYPE_FINAL, d.getDepositTypeCode());
        }

        return hasFinal;
    }

    /**
     * @return lowest unused deposit-line-number, to simplify adding and canceling deposits out-of-order
     */
    public Integer getNextDepositLineNumber() {
        int maxLineNumber = -1;

        for (Iterator i = deposits.iterator(); i.hasNext();) {
            Deposit d = (Deposit) i.next();

            Integer depositLineNumber = d.getFinancialDocumentDepositLineNumber();
            if ((depositLineNumber != null) && (depositLineNumber.intValue() > maxLineNumber)) {
                maxLineNumber = depositLineNumber.intValue();
            }
        }

        return new Integer(maxLineNumber + 1);
    }

    /**
     * Adds default AccountingLineDecorators to sourceAccountingLineDecorators until it contains at least minSize elements
     * 
     * @param minSize
     */
    private void extendDeposits(int minSize) {
        while (deposits.size() < minSize) {
            deposits.add(new Deposit());
        }
    }


    /**
     * @see org.kuali.core.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add(getDeposits());

        return managedLists;
    }


    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        KualiWorkflowDocument kwd = getDocumentHeader().getWorkflowDocument();

        if (LOG.isDebugEnabled()) {
            logState();
        }

        if (kwd.stateIsProcessed()) {
            // all approvals have been processed, finalize everything
            SpringServiceLocator.getCashManagementService().finalizeCashManagementDocument(this);
        }
        else if (kwd.stateIsCanceled() || kwd.stateIsDisapproved()) {
            // document has been canceled or disapproved
            SpringServiceLocator.getCashManagementService().cancelCashManagementDocument(this);
        }
    }

    private void logState() {
        KualiWorkflowDocument kwd = getDocumentHeader().getWorkflowDocument();

        if (kwd.stateIsInitiated()) {
            LOG.debug("CMD stateIsInitiated");
        }
        if (kwd.stateIsProcessed()) {
            LOG.debug("CMD stateIsProcessed");
        }
        if (kwd.stateIsCanceled()) {
            LOG.debug("CMD stateIsCanceled");
        }
        if (kwd.stateIsDisapproved()) {
            LOG.debug("CMD stateIsDisapproved");
        }
    }


    /* utility methods */
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", getFinancialDocumentNumber());
        m.put("workgroupName", getWorkgroupName());
        return m;
    }
}
