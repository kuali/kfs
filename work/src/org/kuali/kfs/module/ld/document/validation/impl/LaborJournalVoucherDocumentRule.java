/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.rules;

import static org.kuali.kfs.KFSConstants.GENERIC_CODE_PROPERTY_NAME;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.financial.rules.JournalVoucherDocumentRule;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborJournalVoucherDetail;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.PositionData;
import org.kuali.module.labor.document.LaborJournalVoucherDocument;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.rule.GenerateLaborLedgerPendingEntriesRule;

/**
 * Business rule class for the Labor Journal Voucher Document.
 */
public class LaborJournalVoucherDocumentRule extends JournalVoucherDocumentRule implements GenerateLaborLedgerPendingEntriesRule<LaborLedgerPostingDocument> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborJournalVoucherDocumentRule.class);
    private final static String GENERAL_LEDGER_POSTING_HELPER_BEAN_ID = "kfsGenericGeneralLedgerPostingHelper";

    /**
     * @see org.kuali.module.financial.rules.JournalVoucherDocumentRule#processCustomUpdateAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument document, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return processCustomAddAccountingLineBusinessRules(document, updatedAccountingLine);
    }

    /**
     * Adds an accounting line to the labor journal voucher document
     * 
     * @param document Accounting document where a new accounting line is added
     * @param accountingLine line that is going to be added
     * @return boolean
     * @see org.kuali.module.financial.rules.JournalVoucherDocumentRule#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        boolean isValid = super.processCustomAddAccountingLineBusinessRules(document, accountingLine);

        LaborJournalVoucherDetail laborVoucherAccountingLine = (LaborJournalVoucherDetail) accountingLine;

        // position code existence check
        String positionNumber = laborVoucherAccountingLine.getPositionNumber();
        if (!StringUtils.isBlank(positionNumber) && !LaborConstants.getDashPositionNumber().equals(positionNumber)) {
            Map criteria = new HashMap();
            criteria.put(KFSPropertyConstants.POSITION_NUMBER, positionNumber);

            Collection positionMatches = SpringContext.getBean(BusinessObjectService.class).findMatching(PositionData.class, criteria);
            if (positionMatches == null || positionMatches.isEmpty()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(PositionData.class.getName()).getAttributeDefinition(KFSPropertyConstants.POSITION_NUMBER).getLabel();
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.POSITION_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, label);
                isValid = false;
            }
        }

        // emplid existence check
        String emplid = laborVoucherAccountingLine.getEmplid();
        if (!StringUtils.isBlank(emplid) && !LaborConstants.getDashEmplId().equals(emplid)) {
            Map criteria = new HashMap();
            criteria.put(KFSPropertyConstants.PERSON_PAYROLL_IDENTIFIER, emplid);

            Collection emplidMatches = SpringContext.getBean(UniversalUserService.class).findUniversalUsers(criteria);
            if (emplidMatches == null || emplidMatches.isEmpty()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(LaborJournalVoucherDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.EMPLID).getLabel();
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.EMPLID, KFSKeyConstants.ERROR_EXISTENCE, label);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.module.financial.rules.JournalVoucherDocumentRule#isExternalEncumbranceSpecificBusinessRulesValid(org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean isExternalEncumbranceSpecificBusinessRulesValid(AccountingLine accountingLine) {
        boolean encumbranceValid = true;

        BalanceTyp balanceType = accountingLine.getBalanceTyp();
        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        if (!accountingDocumentRuleUtil.isValidBalanceType(balanceType, GENERIC_CODE_PROPERTY_NAME)) {
            encumbranceValid = false;
        }
        else if (balanceType.isFinBalanceTypeEncumIndicator() && KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(accountingLine.getEncumbranceUpdateCode())) {
            encumbranceValid = isRequiredReferenceFieldsValid(accountingLine);
        }

        return encumbranceValid;
    }

    /**
     * @param accountingDocument
     * @param accountingLine
     * @param sequenceHelper
     * @return boolean
     * @see org.kuali.module.labor.rule.GenerateLaborLedgerPendingEntriesRule#processGenerateLaborLedgerPendingEntries(org.kuali.module.labor.document.LaborLedgerPostingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateLaborLedgerPendingEntries(LaborLedgerPostingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.debug("processGenerateLaborLedgerPendingEntries() started");

        try {
            LaborJournalVoucherDocument laborJournalVoucherDocument = (LaborJournalVoucherDocument) accountingDocument;
            LaborLedgerPendingEntry pendingLedgerEntry = new LaborLedgerPendingEntry();

            // populate the explicit entry
            ObjectUtil.buildObject(pendingLedgerEntry, accountingLine);
            GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
            glpeService.populateExplicitGeneralLedgerPendingEntry(laborJournalVoucherDocument, accountingLine, sequenceHelper, pendingLedgerEntry);

            // apply the labor JV specific information
            laborJournalVoucherDocument.customizeExplicitGeneralLedgerPendingEntry(accountingLine, pendingLedgerEntry);
            pendingLedgerEntry.setFinancialDocumentTypeCode(laborJournalVoucherDocument.getOffsetTypeCode());

            if (StringUtils.isBlank(((LaborJournalVoucherDetail) accountingLine).getEmplid())) {
                pendingLedgerEntry.setEmplid(LaborConstants.getDashEmplId());
            }

            if (StringUtils.isBlank(((LaborJournalVoucherDetail) accountingLine).getPositionNumber())) {
                pendingLedgerEntry.setPositionNumber(LaborConstants.getDashPositionNumber());
            }

            laborJournalVoucherDocument.getLaborLedgerPendingEntries().add(pendingLedgerEntry);
            sequenceHelper.increment();
        }
        catch (Exception e) {
            LOG.error("Cannot add a Labor Ledger Pending Entry into the list");
            return false;
        }

        return true;
    }
}