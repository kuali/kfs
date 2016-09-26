/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.arizona.kfs.gl.service.impl;

import edu.arizona.kfs.gl.service.GlobalTransactionEditService;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;


@NonTransactional
public class ScrubberValidatorImpl extends org.kuali.kfs.gl.service.impl.ScrubberValidatorImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberValidatorImpl.class);

    private static int count = 0;

    private GlobalTransactionEditService globalTransactionEditService;
    private Object kualiConfigurationService;


    /**
     * Validate a transaction in the scrubber
     *
     * @param originEntry Input transaction (never changed)
     * @param scrubbedEntry Output transaction (scrubbed version of input transaction)
     * @param universityRunDate Date of scrubber run
     * @return List of Message objects based for warnings or errors that happened when validating the transaction
     * @see org.kuali.module.gl.service.ScrubberValidator#validateTransaction(org.kuali.module.gl.bo.OriginEntry, org.kuali.module.gl.bo.OriginEntry, org.kuali.module.gl.bo.UniversityDate, boolean)
     */
    @Override
    public List<Message> validateTransaction(OriginEntryInformation originEntry, OriginEntryInformation scrubbedEntry, UniversityDate universityRunDate, boolean laborIndicator, AccountingCycleCachingService accountingCycleCachingService) {
        LOG.debug("validateTransaction() started");

        continuationAccountIndicator = false;

        List<Message> errors = new ArrayList<Message>();

        count++;
        if (count % 1000 == 0) {
            LOG.info(count + " " + originEntry.getLine());
        }

        // The cobol checks fdoc_nbr, trn_ldgr_entr_desc, org_doc_nbr, org_reference_id, and fdoc_ref_nbr for characters less than
        // ascii 32 or '~'. If found, it replaces that character with a space and reports a warning. This code does the ~, but not
        // the less than 32 part.
        if ((originEntry.getDocumentNumber() != null) && (originEntry.getDocumentNumber().contains("~"))) {
            String d = originEntry.getDocumentNumber();
            scrubbedEntry.setDocumentNumber(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getTransactionLedgerEntryDescription() != null) && (originEntry.getTransactionLedgerEntryDescription().contains("~"))) {
            String d = originEntry.getTransactionLedgerEntryDescription();
            scrubbedEntry.setTransactionLedgerEntryDescription(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getOrganizationDocumentNumber() != null) && (originEntry.getOrganizationDocumentNumber().contains("~"))) {
            String d = originEntry.getOrganizationDocumentNumber();
            scrubbedEntry.setOrganizationDocumentNumber(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getOrganizationReferenceId() != null) && (originEntry.getOrganizationReferenceId().contains("~"))) {
            String d = originEntry.getOrganizationReferenceId();
            scrubbedEntry.setOrganizationReferenceId(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getReferenceFinancialDocumentNumber() != null) && (originEntry.getReferenceFinancialDocumentNumber().contains("~"))) {
            String d = originEntry.getReferenceFinancialDocumentNumber();
            scrubbedEntry.setReferenceFinancialDocumentNumber(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }


        // It's important that this check come before the checks for object, sub-object and accountingPeriod
        // because this validation method will set the fiscal year and reload those three objects if the fiscal
        // year was invalid. This will also set originEntry.getOption and workingEntry.getOption. So, it's
        // probably a good idea to validate the fiscal year first thing.
        Message err = validateFiscalYear(originEntry, scrubbedEntry, universityRunDate, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateUniversityFiscalPeriodCode(originEntry, scrubbedEntry, universityRunDate, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateBalanceType(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateTransactionDate(originEntry, scrubbedEntry, universityRunDate, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateTransactionAmount(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateChart(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        // Labor Scrubber doesn't validate Account here.
        if (!laborIndicator) {
            err = validateAccount(originEntry, scrubbedEntry, universityRunDate, accountingCycleCachingService);
            if (err != null) {
                errors.add(err);
            }
        }

        // Labor Scrubber doesn't validate SubAccount here
        if (!laborIndicator) {
            err = validateSubAccount(originEntry, scrubbedEntry, accountingCycleCachingService);
            if (err != null) {
                errors.add(err);
            }
        }

        err = validateProjectCode(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateDocumentType(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateOrigination(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateReferenceOrigination(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateDocumentNumber(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateObjectCode(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        // If object code is invalid, we can't check the object type
        if (err == null) {
            err = validateObjectType(originEntry, scrubbedEntry, accountingCycleCachingService);
            if (err != null) {
                errors.add(err);
            }
        }

        err = validateSubObjectCode(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        // return messages could be multiple from validateReferenceFields
        List<Message> referenceErrors = validateReferenceDocumentFields(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (referenceErrors != null) {
            errors.addAll(referenceErrors);
        }

        err = validateReversalDate(originEntry, scrubbedEntry, accountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateDescription(originEntry);
        if (err != null) {
            errors.add(err);
        }

        // Per v6 spec, labor entries are not to be GTE validated. Further, to keep with v3 behavior
        // and work done under KITT-2330, Dont go through GTE validation if other errors exist -- this
        // helps ensure reports appear as they have in the past
        if (!laborIndicator && (errors == null || errors.isEmpty())) {
            err = validateGTEAccount((OriginEntryFull) originEntry);
            if (err != null) {
                errors.add(err);
            }
        }

        return errors;
    }

    protected Message validateGTEAccount(OriginEntryFull originEntry) {
        LOG.debug("validateGTEAccount() started");
        Account account = originEntry.getAccount();

        if(ObjectUtils.isNull(account)){
            return MessageBuilder.buildMessageWithPlaceHolder(edu.arizona.kfs.sys.KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES, Message.TYPE_FATAL, new Object[] {"Account", originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber()});
        }
        if(ObjectUtils.isNull(originEntry.getFinancialObject())){
            return MessageBuilder.buildMessageWithPlaceHolder(edu.arizona.kfs.sys.KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES, Message.TYPE_FATAL, new Object[] {"Object Code", originEntry.getChartOfAccountsCode() + "-" + originEntry.getFinancialObjectCode()});
        }
        if(ObjectUtils.isNull(account.getSubFundGroup())){
            return MessageBuilder.buildMessageWithPlaceHolder(edu.arizona.kfs.sys.KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES, Message.TYPE_FATAL, new Object[] {"Sub Fund", account.getSubFundGroupCode()});
        }

        Message result = globalTransactionEditService.isAccountingLineAllowable(originEntry.getFinancialSystemOriginationCode(),
                account.getSubFundGroup().getFundGroupCode(),
                account.getSubFundGroupCode(),
                originEntry.getFinancialDocumentTypeCode(),
                originEntry.getFinancialObject().getFinancialObjectTypeCode(),
                originEntry.getFinancialObject().getFinancialObjectSubTypeCode());
        return result;
    }

    public void setGlobalTransactionEditService(GlobalTransactionEditService globalTransactionEditService) {
        this.globalTransactionEditService = globalTransactionEditService;
    }

    public void setKualiConfigurationService(Object kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public Object getKualiConfigurationService() {
        return kualiConfigurationService;
    }
}

