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

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.service.impl.ScrubberValidatorImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.springframework.util.StringUtils;

/**
 * An extension of GL's ScrubberValidator for use by the Labor ScrubberValidator.  Labor specific overrides
 * to GL code can be found here.
 */
public class LaborGLScrubberValidatorImpl extends edu.arizona.kfs.gl.service.impl.ScrubberValidatorImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborGLScrubberValidatorImpl.class);

    /**
     * @see ScrubberValidatorImpl#validateUniversityFiscalPeriodCode(OriginEntryInformation, OriginEntryInformation, UniversityDate, AccountingCycleCachingService)
     */
    @Override
    protected Message validateUniversityFiscalPeriodCode(OriginEntryInformation originEntry, OriginEntryInformation workingEntry, UniversityDate universityRunDate, AccountingCycleCachingService accountingCycleCachingService) {
        LOG.debug("validateUniversityFiscalPeriodCode() started");

        String periodCode = originEntry.getUniversityFiscalPeriodCode();
        if (!StringUtils.hasText(periodCode)) {
            if (universityRunDate.getAccountingPeriod().isOpen()) {
                workingEntry.setUniversityFiscalPeriodCode(universityRunDate.getUniversityFiscalAccountingPeriod());
                workingEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());
            } else {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_CLOSED, " (year " + universityRunDate.getUniversityFiscalYear() + ", period " + universityRunDate.getUniversityFiscalAccountingPeriod(), Message.TYPE_FATAL);
            }
        } else {
            AccountingPeriod originEntryAccountingPeriod = accountingCycleCachingService.getAccountingPeriod(originEntry.getUniversityFiscalYear(), originEntry.getUniversityFiscalPeriodCode());
            if (originEntryAccountingPeriod == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND, periodCode, Message.TYPE_FATAL);
            } else if (!originEntryAccountingPeriod.isActive() && !originEntry.getFinancialBalanceTypeCode().equals(KFSConstants.BALANCE_TYPE_A21)) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_ACTIVE, periodCode, Message.TYPE_FATAL);
            }

            workingEntry.setUniversityFiscalPeriodCode(periodCode);
        }

        return null;
    }

}
