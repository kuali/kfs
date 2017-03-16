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
package edu.arizona.kfs.module.ld.batch.service.impl;

import edu.arizona.kfs.gl.service.GlobalTransactionEditService;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.List;

/**
 * Service implementation of ScrubberValidator.
 */
public class ScrubberValidatorImpl extends org.kuali.kfs.module.ld.batch.service.impl.ScrubberValidatorImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberValidatorImpl.class);

    private GlobalTransactionEditService globalTransactionEditService;


    /**
     * @see org.kuali.module.labor.service.LaborScrubberValidator#validateTransaction(owrg.kuali.module.labor.bo.LaborOriginEntry,
     * LaborOriginEntry, org.kuali.kfs.gl.businessobject.UniversityDate)
     */
    public List<Message> validateTransaction(OriginEntryInformation originEntry, OriginEntryInformation scrubbedEntry, UniversityDate universityRunDate, boolean laborIndicator, AccountingCycleCachingService laborAccountingCycleCachingService) {
        LOG.debug("validateTransaction() started");
        continuationAccountIndicator = false;

        LaborOriginEntry laborOriginEntry = (LaborOriginEntry) originEntry;
        LaborOriginEntry laborScrubbedEntry = (LaborOriginEntry) scrubbedEntry;

        // gl scrubber validation
        List<Message> errors = scrubberValidator.validateTransaction(laborOriginEntry, laborScrubbedEntry, universityRunDate, laborIndicator, laborAccountingCycleCachingService);

        refreshOriginEntryReferences(laborOriginEntry);
        refreshOriginEntryReferences(laborScrubbedEntry);

        if (StringUtils.isBlank(laborOriginEntry.getEmplid())) {
            laborScrubbedEntry.setEmplid(LaborConstants.getDashEmplId());
        }

        if (StringUtils.isBlank(laborOriginEntry.getPositionNumber())) {
            laborScrubbedEntry.setPositionNumber(LaborConstants.getDashPositionNumber());
        }

        Message err = validatePayrollEndFiscalYear(laborOriginEntry, laborScrubbedEntry, universityRunDate, (LaborAccountingCycleCachingService) laborAccountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validatePayrollEndFiscalPeriodCode(laborOriginEntry, laborScrubbedEntry, universityRunDate, (LaborAccountingCycleCachingService) laborAccountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateAccount(laborOriginEntry, laborScrubbedEntry, universityRunDate, (LaborAccountingCycleCachingService) laborAccountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateSubAccount(laborOriginEntry, laborScrubbedEntry, (LaborAccountingCycleCachingService) laborAccountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        // Per v6 spec, labor entries are not to be GTE validated. Further, to keep with v3 behavior
        // and work done under KITT-2330, Dont go through GTE validation if other errors exist -- this
        // helps ensure reports appear as they have in the past
        if (!laborIndicator && (errors == null || errors.isEmpty())) {
            err = validateGTEAccount(laborScrubbedEntry);
            if (err != null) {
                errors.add(err);
            }
        }

        return errors;
    }

    protected Message validateGTEAccount(LaborOriginEntry originEntry) {
        LOG.debug("validateGTEAccount() started");

        Account account = originEntry.getAccount();
        if (ObjectUtils.isNull(account)) {
            return MessageBuilder.buildMessageWithPlaceHolder(edu.arizona.kfs.sys.KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES, Message.TYPE_FATAL, new Object[] {"Account", originEntry.getAccountNumber()});
        }
        if (ObjectUtils.isNull(account.getSubFundGroup())) {
            return MessageBuilder.buildMessageWithPlaceHolder(edu.arizona.kfs.sys.KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES, Message.TYPE_FATAL, new Object[] {"Sub Fund", account.getSubFundGroupCode()});
        }
        if (ObjectUtils.isNull(originEntry.getFinancialObject())) {
            return MessageBuilder.buildMessageWithPlaceHolder(edu.arizona.kfs.sys.KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES, Message.TYPE_FATAL, new Object[] {"Object Code", originEntry.getFinancialObjectCode()});
        }

        Message result = globalTransactionEditService.isAccountingLineAllowable(originEntry.getFinancialSystemOriginationCode(),
                account.getSubFundGroup().getFundGroupCode(),
                account.getSubFundGroupCode(),
                originEntry.getFinancialDocumentTypeCode(),
                originEntry.getFinancialObject().getFinancialObjectTypeCode(),
                originEntry.getFinancialObject().getFinancialObjectSubTypeCode());
        return result;
    }


    /**
     * Sets the globalTransactionEditService attribute value.
     *
     * @param globalTransactionEditService The globalTransactionEditService to set.
     */
    public void setGlobalTransactionEditService(GlobalTransactionEditService globalTransactionEditService) {
        this.globalTransactionEditService = globalTransactionEditService;
    }

}
