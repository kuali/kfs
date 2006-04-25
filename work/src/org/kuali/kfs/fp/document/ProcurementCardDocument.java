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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.financial.bo.ProcurementCardSourceAccountingLine;
import org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine;
import org.kuali.module.financial.bo.ProcurementCardTransactionDetail;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ProcurementCardDocument extends TransactionalDocumentBase {

    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private Date transactionCycleStartDate;
    private Date transactionCycleEndDate;
    private String transactionCreditCardNumber;
    private String financialDocumentCardHolderName;

    private Account account;
    private Chart chartOfAccounts;

    private List transactionEntries;


    /**
     * Default constructor.
     */
    public ProcurementCardDocument() {
        transactionEntries = new TypedArrayList(ProcurementCardTransactionDetail.class);
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return - Returns the chartOfAccountsCode
     *  
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param - chartOfAccountsCode The chartOfAccountsCode to set.
     *  
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return - Returns the accountNumber
     *  
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param - accountNumber The accountNumber to set.
     *  
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return - Returns the subAccountNumber
     *  
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param - subAccountNumber The subAccountNumber to set.
     *  
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    /**
     * Gets the transactionCycleStartDate attribute.
     * 
     * @return - Returns the transactionCycleStartDate
     *  
     */
    public Date getTransactionCycleStartDate() {
        return transactionCycleStartDate;
    }

    /**
     * Sets the transactionCycleStartDate attribute.
     * 
     * @param - transactionCycleStartDate The transactionCycleStartDate to set.
     *  
     */
    public void setTransactionCycleStartDate(Date transactionCycleStartDate) {
        this.transactionCycleStartDate = transactionCycleStartDate;
    }


    /**
     * Gets the transactionCycleEndDate attribute.
     * 
     * @return - Returns the transactionCycleEndDate
     *  
     */
    public Date getTransactionCycleEndDate() {
        return transactionCycleEndDate;
    }

    /**
     * Sets the transactionCycleEndDate attribute.
     * 
     * @param - transactionCycleEndDate The transactionCycleEndDate to set.
     *  
     */
    public void setTransactionCycleEndDate(Date transactionCycleEndDate) {
        this.transactionCycleEndDate = transactionCycleEndDate;
    }


    /**
     * Gets the transactionCreditCardNumber attribute.
     * 
     * @return - Returns the transactionCreditCardNumber
     *  
     */
    public String getTransactionCreditCardNumber() {
        return transactionCreditCardNumber;
    }

    /**
     * Sets the transactionCreditCardNumber attribute.
     * 
     * @param - transactionCreditCardNumber The transactionCreditCardNumber to set.
     *  
     */
    public void setTransactionCreditCardNumber(String transactionCreditCardNumber) {
        this.transactionCreditCardNumber = transactionCreditCardNumber;
    }


    /**
     * Gets the financialDocumentCardHolderName attribute.
     * 
     * @return - Returns the financialDocumentCardHolderName
     *  
     */
    public String getFinancialDocumentCardHolderName() {
        return financialDocumentCardHolderName;
    }

    /**
     * Sets the financialDocumentCardHolderName attribute.
     * 
     * @param - financialDocumentCardHolderName The financialDocumentCardHolderName to set.
     *  
     */
    public void setFinancialDocumentCardHolderName(String financialDocumentCardHolderName) {
        this.financialDocumentCardHolderName = financialDocumentCardHolderName;
    }


    /**
     * Gets the account attribute.
     * 
     * @return - Returns the account
     *  
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param - account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return - Returns the chartOfAccounts
     *  
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param - chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }


    /**
     * @return Returns the transactionEntries.
     */
    public List getTransactionEntries() {
        return transactionEntries;
    }

    /**
     * @param transactionEntries The transactionEntries to set.
     */
    public void setTransactionEntries(List transactionEntries) {
        this.transactionEntries = transactionEntries;
    }

    /**
     * Override to set the accounting line in the transaction detail object.
     * @see org.kuali.core.document.TransactionalDocument#addSourceAccountingLine(org.kuali.core.bo.SourceAccountingLine)
     */
    public void addSourceAccountingLine(ProcurementCardSourceAccountingLine line) {
        line.setSequenceNumber(this.getNextSourceLineNumber());

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(
                    line.getFinancialDocumentTransactionLineNumber())) {
                transactionEntry.getSourceAccountingLines().add(line);
            }
        }

        this.nextSourceLineNumber = new Integer(this.getNextSourceLineNumber().intValue() + 1);
    }

    /**
     * Override to set the accounting line in the transaction detail object.
     * @see org.kuali.core.document.TransactionalDocument#addTargetAccountingLine(org.kuali.core.bo.TargetAccountingLine)
     */
    public void addTargetAccountingLine(ProcurementCardTargetAccountingLine line) {
        line.setSequenceNumber(this.getNextTargetLineNumber());

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(
                    line.getFinancialDocumentTransactionLineNumber())) {
                transactionEntry.getTargetAccountingLines().add(line);
            }
        }

        this.nextTargetLineNumber = new Integer(this.getNextTargetLineNumber().intValue() + 1);
    }

    /**
     * Override to get source accounting lines out of transactions
     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLines()
     */
    public List getSourceAccountingLines() {
        List sourceAccountingLines = new ArrayList();

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            sourceAccountingLines.addAll(transactionEntry.getSourceAccountingLines());
        }

        return sourceAccountingLines;
    }

    /**
     * Override to get target accounting lines out of transactions
     * @see org.kuali.core.document.TransactionalDocument#getTargetAccountingLines()
     */
    public List getTargetAccountingLines() {
        List targetAccountingLines = new ArrayList();

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            targetAccountingLines.addAll(transactionEntry.getTargetAccountingLines());
        }

        return targetAccountingLines;
    }
    
    

    /**
     * Override to set status in document header for when the document is saved.
     * @see org.kuali.core.document.Document#handleRouteStatusChange(java.lang.String)
     */
    public void handleRouteStatusChange(String newRouteStatus) {
        if (Constants.ROUTE_HEADER_SAVED_CD.equals(newRouteStatus)) {
            this.getDocumentHeader().setFinancialDocumentStatusCode(
                    Constants.DOCUMENT_STATUS_CD_IN_PROCESS_PROCESSED);
            SpringServiceLocator.getDocumentService().updateDocument(this);
        }
        super.handleRouteStatusChange(newRouteStatus);
    }
    
    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }
}