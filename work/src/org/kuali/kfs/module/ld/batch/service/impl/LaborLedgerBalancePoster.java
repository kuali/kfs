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
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
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
        String debitCreditCode = transaction.getTransactionDebitCreditCode();
        KualiDecimal amount = transaction.getTransactionLedgerEntryAmount();
        amount = debitCreditCode.equals(KFSConstants.GL_CREDIT_CODE) ? amount.negated() : amount;

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
