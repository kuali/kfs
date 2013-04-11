/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.document.IntraAccountAdjustmentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

public class IntraAccountAdjustmentAmountBalancedValidation extends GenericValidation {

    private static Logger logger = Logger.getLogger(IntraAccountAdjustmentAmountBalancedValidation.class);

    @Override
    public boolean validate(AttributedDocumentEvent event) {

        if(GlobalVariables.getMessageMap().getErrorCount() > 0) {
            return false;
        }
        boolean valid = true;

        IntraAccountAdjustmentDocument iaaDocument = (IntraAccountAdjustmentDocument)event.getDocument();

        List<AccountingLine> sourceAcctingLines = iaaDocument.getSourceAccountingLines();
        List<AccountingLine> targetAcctingLines = iaaDocument.getTargetAccountingLines();

        if(sourceAcctingLines != null && targetAcctingLines != null && sourceAcctingLines.size() > 0 && targetAcctingLines.size() > 0 ) {

            AccountService accountService = SpringContext.getBean(AccountService.class);
            ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);

            Map <AccountObjectCodeVO,KualiDecimal> srcAccountObjectCodeAmountMap
                            = generateAccountObjectCodeAmountMap(sourceAcctingLines, accountService,objectCodeService);

            Map <AccountObjectCodeVO,KualiDecimal> targetAccountObjectCodeAmountMap
                            = generateAccountObjectCodeAmountMap(targetAcctingLines, accountService,objectCodeService);

            if(srcAccountObjectCodeAmountMap != null && targetAccountObjectCodeAmountMap != null ) {
                if(! srcAccountObjectCodeAmountMap.equals(targetAccountObjectCodeAmountMap)){
                  valid = false;
                  GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS,
                                              KFSKeyConstants.IntraAccountAdjustment.ERROR_NOT_BALANCED_PER_ACC_OBJECT);
                  logger.info(" Failed in Balance check per account per Object code ");

                }
            }
        }
        return valid;
    }

    private Map<AccountObjectCodeVO,KualiDecimal> generateAccountObjectCodeAmountMap( List<AccountingLine> accountingLines,AccountService accountService ,ObjectCodeService objectCodeService){

        Map<AccountObjectCodeVO,KualiDecimal> accountObjectCodeAmountMap = new HashMap<AccountObjectCodeVO, KualiDecimal>();
        AccountObjectCodeVO accountObjectCodeVO;
        for(AccountingLine accountingLine :accountingLines){

            ObjectCode objectCode = objectCodeService.getByPrimaryId(accountingLine.getPostingYear(), accountingLine.getChartOfAccountsCode(), accountingLine.getFinancialObjectCode());
            accountObjectCodeVO = new AccountObjectCodeVO(accountingLine.getChartOfAccountsCode(), accountingLine.getAccountNumber(),accountingLine.getFinancialObjectCode() ,objectCode.getUniversityFiscalYear());
            if(accountObjectCodeAmountMap.containsKey(accountObjectCodeVO)){
                KualiDecimal amount = accountObjectCodeAmountMap.get(accountObjectCodeVO);
                amount = amount.add(accountingLine.getAmount());
                accountObjectCodeAmountMap.put(accountObjectCodeVO, amount);
            }
            else {
                accountObjectCodeAmountMap.put(accountObjectCodeVO, accountingLine.getAmount());
            }

        }
        return accountObjectCodeAmountMap;
    }





  class AccountObjectCodeVO {
    private String chartCode, accountNumber,objectCode;
    private Integer fiscalYear;

     AccountObjectCodeVO(String chartCode , String accountNumber , String objectCode, Integer fiscalYear){
         this.accountNumber =accountNumber;
         this.chartCode=chartCode;
         this.objectCode=objectCode;
         this.fiscalYear=fiscalYear;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getOuterType().hashCode();
        result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
        result = prime * result + ((chartCode == null) ? 0 : chartCode.hashCode());
        result = prime * result + ((fiscalYear == null) ? 0 : fiscalYear.hashCode());
        result = prime * result + ((objectCode == null) ? 0 : objectCode.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AccountObjectCodeVO other = (AccountObjectCodeVO) obj;
        if (!getOuterType().equals(other.getOuterType())) {
            return false;
        }
        if (accountNumber == null) {
            if (other.accountNumber != null) {
                return false;
            }
        }
        else if (!accountNumber.equals(other.accountNumber)) {
            return false;
        }
        if (chartCode == null) {
            if (other.chartCode != null) {
                return false;
            }
        }
        else if (!chartCode.equals(other.chartCode)) {
            return false;
        }
        if (fiscalYear == null) {
            if (other.fiscalYear != null) {
                return false;
            }
        }
        else if (!fiscalYear.equals(other.fiscalYear)) {
            return false;
        }
        if (objectCode == null) {
            if (other.objectCode != null) {
                return false;
            }
        }
        else if (!objectCode.equals(other.objectCode)) {
            return false;
        }
        return true;
    }
    private IntraAccountAdjustmentAmountBalancedValidation getOuterType() {
        return IntraAccountAdjustmentAmountBalancedValidation.this;
    }

     }
}
