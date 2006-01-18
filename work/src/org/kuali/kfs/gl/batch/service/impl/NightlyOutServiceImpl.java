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
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.GeneralLedgerPendingEntryService;
import org.kuali.module.gl.service.NightlyOutService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;

/**
 * This class implements the nightly out batch job.
 * 
 * @author Bin Gao from Michigan State University
 */
public class NightlyOutServiceImpl implements NightlyOutService {

    GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    OriginEntryService originEntryService;
    DateTimeService dateTimeService;
    OriginEntryGroupService originEntryGroupService;
    int counter = 0;

    /**
     * Constructs a NightlyOutServiceImpl.java.
     *  
     */
    public NightlyOutServiceImpl() {
    }

    /**
     * @see org.kuali.module.gl.service.NightlyOutService#copyPendingLedgerEntry()
     */
    public void copyPendingLedgerEntry() {
        Iterator pendingEntries = generalLedgerPendingEntryService
                .findAllGeneralLedgerPendingEntries();

        counter = 0;
        while (pendingEntries.hasNext()) {
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntries.next();
            saveAsOriginEntry(pendingEntry);
            counter++;
        }
    }
    
    /*
     * save pending ledger entry as origin entry
     */
    private void saveAsOriginEntry(GeneralLedgerPendingEntry pendingEntry){
        OriginEntry originEntry = new OriginEntry();
        
        originEntry.setAccountNumber(pendingEntry.getAccountNumber());
        originEntry.setBalanceTypeCode(pendingEntry.getFinancialBalanceTypeCode());
        originEntry.setBudgetYear(pendingEntry.getBudgetYear());
        originEntry.setChartOfAccountsCode(pendingEntry.getChartOfAccountsCode());
        originEntry.setDebitOrCreditCode(pendingEntry.getTransactionDebitCreditCode());
        originEntry.setDocumentNumber(pendingEntry.getFinancialDocumentNumber());
        
        Timestamp reversalDate = pendingEntry.getFinancialDocumentReversalDate();
        if(reversalDate != null){
            originEntry.setDocumentReversalDate(new Date(reversalDate.getTime()));
        }
        originEntry.setDocumentTypeCode(pendingEntry.getFinancialDocumentTypeCode());
        originEntry.setEncumbranceUpdateCode(pendingEntry.getTransactionEncumbranceUpdtCd());
        originEntry.setObjectCode(pendingEntry.getFinancialObjectCode());
        originEntry.setObjectTypeCode(pendingEntry.getFinancialObjectTypeCode());
        originEntry.setOrganizationDocumentNumber(pendingEntry.getOrganizationDocumentNumber());
        originEntry.setOrganizationReferenceId(pendingEntry.getOrganizationReferenceId());
        originEntry.setOriginCode(pendingEntry.getOriginCode());
        originEntry.setProjectCode(pendingEntry.getProjectCode());
        originEntry.setReferenceDocumentNumber(pendingEntry.getFinancialDocumentReferenceNbr());
        originEntry.setReferenceDocumentTypeCode(pendingEntry.getReferenceFinDocumentTypeCode());
        originEntry.setReferenceOriginCode(pendingEntry.getFinSystemRefOriginationCode());
        originEntry.setSubAccountNumber(pendingEntry.getSubAccountNumber());
        originEntry.setSubObjectCode(pendingEntry.getFinancialSubObjectCode());
        originEntry.setTransactionDate(new Date(pendingEntry.getTransactionDate().getTime()));
        originEntry.setTransactionEntrySequenceId(pendingEntry.getTrnEntryLedgerSequenceNumber());
        originEntry.setTransactionLedgerEntryAmount(pendingEntry.getTransactionLedgerEntryAmount());
        originEntry.setTransactionLedgerEntryDescription(pendingEntry.getTransactionLedgerEntryDesc());
        originEntry.setUniversityFiscalAccountingPeriod(pendingEntry.getUniversityFiscalPeriodCode());
        originEntry.setUniversityFiscalYear(pendingEntry.getUniversityFiscalYear());
        
        Date today = new Date(dateTimeService.getCurrentTimestamp().getTime());
        OriginEntryGroup group = originEntryService.createGroup(today, "EDOC", true, true, true);
        originEntryService.createEntry(originEntry, group);
    }

    /**
     * Gets the generalLedgerPendingEntryService attribute. 
     * @return Returns the generalLedgerPendingEntryService.
     */
    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }
    /**
     * Sets the generalLedgerPendingEntryService attribute value.
     * @param generalLedgerPendingEntryService The generalLedgerPendingEntryService to set.
     */
    public void setGeneralLedgerPendingEntryService(
            GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }
    /**
     * Gets the originEntryService attribute. 
     * @return Returns the originEntryService.
     */
    public OriginEntryService getOriginEntryService() {
        return originEntryService;
    }
    /**
     * Sets the originEntryService attribute value.
     * @param originEntryService The originEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }
    /**
     * Gets the dateTimeService attribute. 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }
    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    /**
     * Gets the originEntryGroupService attribute. 
     * @return Returns the originEntryGroupService.
     */
    public OriginEntryGroupService getOriginEntryGroupService() {
        return originEntryGroupService;
    }
    /**
     * Sets the originEntryGroupService attribute value.
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * @see org.kuali.module.gl.service.NightlyOutService#getCounter()
     */
    public int getCounter() {
        return this.counter;
    }
    
}
