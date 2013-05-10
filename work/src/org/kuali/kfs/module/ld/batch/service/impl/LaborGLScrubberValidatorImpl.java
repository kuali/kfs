/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.batch.service.impl;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.service.ScrubberValidator;
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
public class LaborGLScrubberValidatorImpl extends ScrubberValidatorImpl implements ScrubberValidator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborGLScrubberValidatorImpl.class);

    /**
     * @see org.kuali.kfs.gl.service.impl.ScrubberValidatorImpl#validateUniversityFiscalPeriodCode(org.kuali.kfs.gl.businessobject.OriginEntryInformation, org.kuali.kfs.gl.businessobject.OriginEntryInformation, org.kuali.kfs.sys.businessobject.UniversityDate, org.kuali.kfs.gl.batch.service.AccountingCycleCachingService)
     */
    @Override
    protected Message validateUniversityFiscalPeriodCode(OriginEntryInformation originEntry, OriginEntryInformation workingEntry, UniversityDate universityRunDate, AccountingCycleCachingService accountingCycleCachingService) {
        LOG.debug("validateUniversityFiscalPeriodCode() started");
        
        String periodCode = originEntry.getUniversityFiscalPeriodCode();
        if (!StringUtils.hasText(periodCode)) {
            if (universityRunDate.getAccountingPeriod().isOpen()) {
                workingEntry.setUniversityFiscalPeriodCode(universityRunDate.getUniversityFiscalAccountingPeriod());
                workingEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());
            }
            else {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_CLOSED, " (year " + universityRunDate.getUniversityFiscalYear() + ", period " + universityRunDate.getUniversityFiscalAccountingPeriod(), Message.TYPE_FATAL);
            }
        }
        else {
            AccountingPeriod originEntryAccountingPeriod = accountingCycleCachingService.getAccountingPeriod(originEntry.getUniversityFiscalYear(), originEntry.getUniversityFiscalPeriodCode());
            if (originEntryAccountingPeriod == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND, periodCode, Message.TYPE_FATAL);
            }
            else if (!originEntryAccountingPeriod.isActive() && !originEntry.getFinancialBalanceTypeCode().equals(KFSConstants.BALANCE_TYPE_A21)) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_ACTIVE, periodCode, Message.TYPE_FATAL);
            }

            workingEntry.setUniversityFiscalPeriodCode(periodCode);
        }

        return null;    }

}
