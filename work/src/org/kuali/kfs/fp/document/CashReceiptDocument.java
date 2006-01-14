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
package org.kuali.module.financial.document;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.financial.bo.CheckBase;

/**
 * This is the business object that represents the CashReceiptDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since a Cash Receipt is a one sided transactional document,
 * only accepting funds into the university, the accounting line data will be held in the target accounting line data structure
 * only.
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CashReceiptDocument extends TransactionalDocumentBase {
    //private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashReceiptDocument.class);

    private static final long serialVersionUID = -142097324655496518L;
    private AccountingPeriod accountingPeriod; //represented by the posting year and posting period code
    private String campusLocationCode; //TODO Needs to be an actual object - also need to clarify this
    private Timestamp depositDate;

    //child object containers - for all the different reconciliation detail sections
    private List checks = new ArrayList();

    //incrementers for detail lines
    private Integer nextCheckLineNumber = new Integer(1);

    //monetary attributes
    private KualiDecimal totalCashAmount  = new KualiDecimal(0);
    private KualiDecimal totalCheckAmount = new KualiDecimal(0);
    private KualiDecimal totalCoinAmount  = new KualiDecimal(0);

    /**
     * Initializes the array lists and line incrementers.
     */
    public CashReceiptDocument() {
        super();
    }

    /**
     * Gets the accountingPeriod attribute.
     * @return Returns the accountingPeriod.
     */
    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    /**
     * Sets the accountingPeriod attribute value.
     * @param accountingPeriod The accountingPeriod to set.
     */
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    /**
     * Gets the campusLocationCode attribute.
     * @return Returns the campusLocationCode.
     */
    public String getCampusLocationCode() {
        return campusLocationCode;
    }

    /**
     * Sets the campusLocationCode attribute value.
     * @param campusLocationCode The campusLocationCode to set.
     */
    public void setCampusLocationCode(String campusLocationCode) {
        this.campusLocationCode = campusLocationCode;
    }

    /**
     * Gets the totalCashAmount attribute.
     * @return Returns the totalCashAmount.
     */
    public KualiDecimal getTotalCashAmount() {
        return totalCashAmount;
    }

    /**
     * Sets the totalCashAmount attribute value.
     * @param totalCashAmount The totalCashAmount to set.
     */
    public void setTotalCashAmount(KualiDecimal cashAmount) {
        this.totalCashAmount = cashAmount;
    }

    /**
     * Gets the checks attribute.
     * @return Returns the checks.
     */
    public List getChecks() {
        return checks;
    }

    /**
     * Sets the checks attribute value.
     * @param checks The checks to set.
     */
    public void setChecks(List checks) {
        this.checks = checks;
    }
    
    /**
     * Adds a new check to the list.
     * @param item
     */
    public void addCheck(CheckBase check) {
       check.setNextCheckLineNumber(this.nextCheckLineNumber);
       this.checks.add(check);
       this.nextCheckLineNumber = new Integer(this.nextCheckLineNumber.intValue() + 1);
       KualiDecimal tca = this.totalCheckAmount;
       this.totalCheckAmount = this.totalCheckAmount.add(check.getAmount());
    }
    
    /**
     * Retrieve a particular check at a given index in the list of checks.
     * @param index
     * @return
     */
    public CheckBase getCheck(int index) throws IllegalAccessException, InstantiationException {
        while(this.checks.size() <= index) {
            checks.add(new CheckBase());
        }
        return (CheckBase) checks.get(index);
    }
    
    /**
     * This method removes a check from the list and updates the total appropriately.
     * @param index
     */
    public void removeCheck(int index) {
        CheckBase check = (CheckBase) checks.remove(index);
        this.totalCheckAmount = this.totalCheckAmount.subtract(check.getAmount());
        // If the totalCheckAmount went negative, bring back to zero.
        if(this.totalCheckAmount.isNegative()) {
        	totalCheckAmount = KualiDecimal.ZERO;
        }
    }

    /**
     * Gets the depositDate attribute.
     * @return Returns the depositDate.
     */
    public Timestamp getDepositDate() {
        return depositDate;
    }

    /**
     * Sets the depositDate attribute value.
     * @param depositDate The depositDate to set.
     */
    public void setDepositDate(Timestamp depositDate) {
        this.depositDate = depositDate;
    }

    /**
     * Gets the nextCheckLineNumber attribute.
     * @return Returns the nextCheckLineNumber.
     */
    public Integer getNextCheckLineNumber() {
        return nextCheckLineNumber;
    }

    /**
     * Sets the nextCheckLineNumber attribute value.
     * @param nextCheckLineNumber The nextCheckLineNumber to set.
     */
    public void setNextCheckLineNumber(Integer nextCheckLineNumber) {
        this.nextCheckLineNumber = nextCheckLineNumber;
    }

    /**
     * Gets the totalCheckAmount attribute.
     * @return Returns the totalCheckAmount.
     */
    public KualiDecimal getTotalCheckAmount() {
    	KualiDecimal _amount = null;
    	if(getChecks().isEmpty()) {
            _amount = totalCheckAmount;
    	} else {
    		_amount = new KualiDecimal(0);
    		for(Iterator iterator = getChecks().iterator(); iterator.hasNext();) {
    			CheckBase check = (CheckBase)iterator.next();
    			_amount = _amount.add(check.getAmount());
    		}
    	}
    	return _amount;
    }

    /**
     * Sets the totalCheckAmount attribute value.
     * @param totalCheckAmount The totalCheckAmount to set.
     */
    public void setTotalCheckAmount(KualiDecimal totalCheckAmount) {
        this.totalCheckAmount = 
        	null == totalCheckAmount ? new KualiDecimal(0) : totalCheckAmount;
    }

    /**
     * Gets the totalCoinAmount attribute.
     * @return Returns the totalCoinAmount.
     */
    public KualiDecimal getTotalCoinAmount() {
        return totalCoinAmount;
    }

    /**
     * Sets the totalCoinAmount attribute value.
     * @param totalCoinAmount The totalCoinAmount to set.
     */
    public void setTotalCoinAmount(KualiDecimal totalCoinAmount) {
        this.totalCoinAmount = totalCoinAmount;
    }

    public KualiDecimal getSumTotalAmount() {
    	return totalCoinAmount.add(totalCheckAmount).add(totalCashAmount);
    }

    /**
     * Override with nothing since CR doesn't do org or account based routing.
     * @see org.kuali.core.document.Document#populateDocumentForRouting()
     */
    public void populateDocumentForRouting() {
    }
    
    /**
     * Overrides the base implementation to return an empty string.
     */
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.EMPTY_STRING;
    }

    /**
     * Overrides the base implementation to return an empty string.
     */
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.EMPTY_STRING;
    }

}