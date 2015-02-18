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
package org.kuali.kfs.module.ld.batch.service.impl;

import java.util.Date;

import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LaborTransaction;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to post a transaction into Labor Ledger Balance Table
 */
@Transactional
public class LaborLedgerBalancePoster implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerBalancePoster.class);

    private LaborAccountingCycleCachingService laborAccountingCycleCachingService;
    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public String post(Transaction transaction, int mode, Date postDate, ReportWriterService posterReportWriterService) {
        String operationType = KFSConstants.OperationType.INSERT;
        LedgerBalance ledgerBalance = new LedgerBalance((LaborTransaction) transaction);
        // ObjectUtil.buildObject(ledgerBalance, transaction);

        LedgerBalance tempLedgerBalance = laborAccountingCycleCachingService.getLedgerBalance(ledgerBalance);
        if (ObjectUtils.isNotNull(tempLedgerBalance)) {
            ledgerBalance = tempLedgerBalance;
            operationType = KFSConstants.OperationType.UPDATE;
        }
        KualiDecimal amount = transaction.getTransactionLedgerEntryAmount();
        if (transaction.getBalanceType().isFinancialOffsetGenerationIndicator()) { 
            if (!transaction.getTransactionDebitCreditCode().equals(transaction.getObjectType().getFinObjectTypeDebitcreditCd())) { 
                amount = amount.negated(); 
            } 
        } 

        ledgerBalance.addAmount(transaction.getUniversityFiscalPeriodCode(), amount);

        if (operationType.equals(KFSConstants.OperationType.INSERT)) {
            laborAccountingCycleCachingService.insertLedgerBalance(ledgerBalance);
        } else {
            laborAccountingCycleCachingService.updateLedgerBalance(ledgerBalance);
        }
        return operationType;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return LaborConstants.DestinationNames.LEDGER_BALANCE;
    }

    public void setLaborAccountingCycleCachingService(LaborAccountingCycleCachingService laborAccountingCycleCachingService) {
        this.laborAccountingCycleCachingService = laborAccountingCycleCachingService;
    }
}
