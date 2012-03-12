/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ld.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.dataaccess.LaborLedgerPendingEntryDao;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.LookupService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of LaborLedgerPendingEntryService.
 */
@Transactional
public class LaborLedgerPendingEntryServiceImpl implements LaborLedgerPendingEntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerPendingEntryServiceImpl.class);

    private LaborLedgerPendingEntryDao laborLedgerPendingEntryDao;
    private BusinessObjectService businessObjectService;
    protected UniversityDateService universityDateService;
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService#hasPendingLaborLedgerEntry(org.kuali.kfs.coa.businessobject.Account)
     */
    public boolean hasPendingLaborLedgerEntry(String chartOfAccountsCode, String accountNumber) {
        Map fieldValues = new HashMap();
        fieldValues.put("chartOfAccountsCode", chartOfAccountsCode);
        fieldValues.put("accountNumber", accountNumber);

        return businessObjectService.countMatching(LaborLedgerPendingEntry.class, fieldValues) > 0;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService#hasPendingLaborLedgerEntry(java.util.Map)
     */
    public boolean hasPendingLaborLedgerEntry(Map fieldValues) {
        LOG.debug("hasPendingLaborLedgerEntry(Map fieldValues) started");

        Collection<LaborLedgerPendingEntry> pendingEntries = SpringContext.getBean(LookupService.class).findCollectionBySearch(LaborLedgerPendingEntry.class, fieldValues);

        // exclude the pending labor ledger transaction has been processed
        for (LaborLedgerPendingEntry pendingLedgerEntry : pendingEntries) {
            String approvedCode = pendingLedgerEntry.getFinancialDocumentApprovedCode();
            if (!KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED.equals(approvedCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Invokes generateEntries method on the salary expense transfer document.
     * 
     * @param document - document whose pending entries need generated
     * @return whether the business rules succeeded
     */
    public boolean generateLaborLedgerPendingEntries(LaborLedgerPostingDocument document) {
        LOG.debug("generateLaborLedgerPendingEntries() started");
        boolean success = true;

        // we must clear them first before creating new ones
        document.getLaborLedgerPendingEntries().clear();

        LOG.info("deleting existing labor ledger pending ledger entries for document " + document.getDocumentNumber());
        delete(document.getDocumentNumber());

        LOG.info("generating labor ledger pending ledger entries for document " + document.getDocumentNumber());
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();

        // process accounting lines, generate labor ledger pending entries
        List<AccountingLine> sourceAccountingLines = document.getSourceAccountingLines();
        for (AccountingLine accountingLine : sourceAccountingLines) {
            success &= document.generateLaborLedgerPendingEntries(accountingLine, sequenceHelper);
        }

        List<AccountingLine> targetAccountingLines = document.getTargetAccountingLines();
        for (AccountingLine accountingLine : targetAccountingLines) {
            success &= document.generateLaborLedgerPendingEntries(accountingLine, sequenceHelper);
        }

        // compare source and target accounting lines, and generate benefit clearing lines as needed
        success &= document.generateLaborLedgerBenefitClearingPendingEntries(sequenceHelper);

        return success;
    }

    public void delete(String documentHeaderId) {
        LOG.debug("delete() started");

        laborLedgerPendingEntryDao.delete(documentHeaderId);
    }

    public Collection findPendingEntries(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingEntries() started");

        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();
        List<String> encumbranceBalanceTypes = generalLedgerPendingEntryService.getEncumbranceBalanceTypes(fieldValues, currentFiscalYear);

        return laborLedgerPendingEntryDao.findPendingEntries(fieldValues, isApproved, currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);
    }

    /**
     * @see org.kuali.module.gl.service.GeneralLedgerPendingEntryService#findPendingLedgerEntriesForAccountBalance(java.util.Map,
     *      boolean, boolean)
     */
    public Iterator findPendingLedgerEntriesForLedgerBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForAccountBalance() started");

        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();
        List<String> encumbranceBalanceTypes = generalLedgerPendingEntryService.getEncumbranceBalanceTypes(fieldValues, currentFiscalYear);

        return laborLedgerPendingEntryDao.findPendingLedgerEntriesForLedgerBalance(fieldValues, isApproved, currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService#findApprovedPendingLedgerEntries()
     */
    public Iterator<LaborLedgerPendingEntry> findApprovedPendingLedgerEntries() {
        return laborLedgerPendingEntryDao.findApprovedPendingLedgerEntries();
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService#deleteByFinancialDocumentApprovedCode(java.lang.String)
     */
    public void deleteByFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        laborLedgerPendingEntryDao.deleteByFinancialDocumentApprovedCode(financialDocumentApprovedCode);
    }

    /**
     * Sets the laborLedgerPendingEntryDao attribute value.
     * 
     * @param laborLedgerPendingEntryDao The laborLedgerPendingEntryDao to set.
     */
    public void setLaborLedgerPendingEntryDao(LaborLedgerPendingEntryDao laborLedgerPendingEntryDao) {
        this.laborLedgerPendingEntryDao = laborLedgerPendingEntryDao;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the universityDateService.
     * 
     * @param universityDateService
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * Sets the generalLedgerPendingEntryService.
     * 
     * @param generalLedgerPendingEntryService
     */
    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }
}
