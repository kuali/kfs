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
package org.kuali.module.financial.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.financial.bo.JournalVoucherAccountingLineHelper;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;

/**
 * This class...
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AuxiliaryVoucherForm extends KualiTransactionalDocumentFormBase {
    private static final long serialVersionUID = 1L;
    
    private List accountingPeriods;
    private String selectedAccountingPeriod;
    private KualiDecimal newSourceLineDebit;
    private KualiDecimal newSourceLineCredit;
    private List auxiliaryLineHelpers;
    
    public AuxiliaryVoucherForm() {
    	super();
        setDocument(new AuxiliaryVoucherDocument());
        selectedAccountingPeriod = new String("");
        setNewSourceLineCredit(new KualiDecimal(0));
        setNewSourceLineDebit(new KualiDecimal(0));
        setAuxiliaryLineHelpers(new ArrayList());
    }
    
    /**
     * Overrides the parent to call super.populate and then to call the two methods that are specific to 
     * loading the two select lists on the page.
     * @see org.kuali.core.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);
        
        //populate the drop downs
        populateAccountingPeriodListForRendering();
    }
    
    /**
     * This method retrieves the list of valid accounting periods to display.
     * @return
     */
    public List getAccountingPeriods() {
        return accountingPeriods;
    }

    /**
     * This method sets the list of valid accounting periods to display.
     * @param accountingPeriods
     */
    public void setAccountingPeriods(List accountingPeriods) {
        this.accountingPeriods = accountingPeriods;
    }
    
    /**
     * @return Returns the serviceBillingDocument.
     */
    public AuxiliaryVoucherDocument getAuxiliaryVoucherDocument() {
        return (AuxiliaryVoucherDocument) getDocument();
    }
    /**
     * @param serviceBillingDocument The serviceBillingDocument to set.
     */ 
    public void setAuxiliaryVoucherDocument(AuxiliaryVoucherDocument auxiliaryVoucherDocument) {
        setDocument(auxiliaryVoucherDocument);
    }    
    
    
    /**
     * This method retrieves the selectedAccountingPeriod.
     * @return
     */
    public String getSelectedAccountingPeriod() {
        return selectedAccountingPeriod;
    }
    
    /**
     * This method sets the selectedAccountingPeriod.
     * @param selectedAccountingPeriod
     */
    public void setSelectedAccountingPeriod(String selectedAccountingPeriod) {
        this.selectedAccountingPeriod = selectedAccountingPeriod;
    }
    

    /**
     * This method...
     * @param index
     * @return
     */
    public JournalVoucherAccountingLineHelper getAuxiliaryLineHelper(int index) {
        while(this.auxiliaryLineHelpers.size() <= index) {
            this.auxiliaryLineHelpers.add(new JournalVoucherAccountingLineHelper());
        }
        return (JournalVoucherAccountingLineHelper) this.auxiliaryLineHelpers.get(index);
    }
    
    /**
     * This method...
     * @return
     */
    public List getAuxiliaryLineHelpers() {
        return auxiliaryLineHelpers;
    }

    /**
     * This method...
     * @param auxiliaryLineHelpers
     */
    public void setAuxiliaryLineHelpers(List journalLineHelpers) {
        this.auxiliaryLineHelpers = journalLineHelpers;
    }

    /**
     * This method...
     * @return
     */
    public KualiDecimal getNewSourceLineCredit() {
        return newSourceLineCredit;
    }

    /**
     * This method...
     * @param newSourceLineCredit
     */
    public void setNewSourceLineCredit(KualiDecimal newSourceLineCredit) {
        this.newSourceLineCredit = newSourceLineCredit;
    }

    /**
     * This method...
     * @return
     */
    public KualiDecimal getNewSourceLineDebit() {
        return newSourceLineDebit;
    }

    /**
     * This method...
     * @param newSourceLineDebit
     */
    public void setNewSourceLineDebit(KualiDecimal newSourceLineDebit) {
        this.newSourceLineDebit = newSourceLineDebit;
    }
    
    /**
     * This method retrieves all of the "open for posting" accounting periods and prepares them 
     * to be rendered in a dropdown UI component.
     */
    private void populateAccountingPeriodListForRendering() {
        //grab the list of valid accounting periods
        ArrayList accountingPeriods = new ArrayList(SpringServiceLocator.getAccountingPeriodService().getOpenAccountingPeriods());
        //set into the form for rendering
        setAccountingPeriods(accountingPeriods);
        //set the chosen accounting period into the form
        populateSelectedAuxiliaryAccountingPeriod();
    }
    

    /**
     * This method parses the accounting period value from the form and builds 
     * a basic AccountingPeriod object so that the journal is properly persisted 
     * with the accounting period set for it.
     */
    private void populateSelectedAuxiliaryAccountingPeriod() {
        String selectedAccountingPeriod = getSelectedAccountingPeriod();
        if(StringUtils.isNotBlank(selectedAccountingPeriod)) {
            AccountingPeriod ap = new AccountingPeriod();
            ap.setUniversityFiscalPeriodCode(StringUtils.left(selectedAccountingPeriod, 2));
            ap.setUniversityFiscalYear(new Integer(StringUtils.right(selectedAccountingPeriod, 4)));
            getAuxiliaryVoucherDocument().setAccountingPeriod(ap);
        }
    }

    /**
     * This method retrieves the AV's debit total formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedDebitTotal() {
        return (String) new CurrencyFormatter().format(getAuxiliaryVoucherDocument().getDebitTotal());
    }

    /**
     * This method retrieves the AV's credit total formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedCreditTotal() {
        return (String) new CurrencyFormatter().format(getAuxiliaryVoucherDocument().getCreditTotal());
    }

    /**
     * This method retrieves the AV's total formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotal() {
        return (String) new CurrencyFormatter().format(getAuxiliaryVoucherDocument().getTotal());
    }

}

