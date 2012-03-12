/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.module.ld.businessobject;

/**
 * Labor Base class for parsing serialized AccountingLines for Labor LedgerJournal Voucher
 */

import static org.kuali.kfs.module.ld.LaborPropertyConstants.EARN_CODE;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.GRADE;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.HRMS_COMPANY;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.PAY_GROUP;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.PAY_PERIOD_END_DATE;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.RUN_IDENTIFIER;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.SALARY_ADMINISTRATION_PLAN;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.SET_ID;
import static org.kuali.kfs.module.ld.LaborPropertyConstants.TRANSACTION_TOTAL_HOURS;
import static org.kuali.kfs.sys.KFSKeyConstants.AccountingLineParser.ERROR_INVALID_PROPERTY_VALUE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.CREDIT;
import static org.kuali.kfs.sys.KFSPropertyConstants.DEBIT;
import static org.kuali.kfs.sys.KFSPropertyConstants.EMPLID;
import static org.kuali.kfs.sys.KFSPropertyConstants.EMPLOYEE_RECORD;
import static org.kuali.kfs.sys.KFSPropertyConstants.ENCUMBRANCE_UPDATE_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.OBJECT_TYPE_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.sys.KFSPropertyConstants.POSITION_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.PROJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.SUB_ACCOUNT_NUMBER;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineParserBase;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.AccountingLineParserException;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class LaborJournalVoucherAccountingLineParser extends AccountingLineParserBase {
    private String balanceTypeCode;
    protected static final String[] LABOR_JV_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, 
        OBJECT_TYPE_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, POSITION_NUMBER, EMPLID, EMPLOYEE_RECORD, 
        EARN_CODE, PAY_GROUP, SALARY_ADMINISTRATION_PLAN, GRADE, RUN_IDENTIFIER, PAY_PERIOD_END_DATE, PAYROLL_END_DATE_FISCAL_YEAR, 
        PAYROLL_END_DATE_FISCAL_PERIOD_CODE, TRANSACTION_TOTAL_HOURS, LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE, LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER,
        LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER, LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE, LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE,
        HRMS_COMPANY, ENCUMBRANCE_UPDATE_CODE, SET_ID, DEBIT, CREDIT };

    /**
     * Constructs a JournalVoucherAccountingLineParser.java.
     * 
     * @param balanceTypeCode
     */
    public LaborJournalVoucherAccountingLineParser(String balanceTypeCode) {
        super();
        this.balanceTypeCode = balanceTypeCode;
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParserBase#performCustomSourceAccountingLinePopulation(java.util.Map,
     *      org.kuali.rice.krad.bo.SourceAccountingLine, java.lang.String)
     */
    @Override
    protected void performCustomSourceAccountingLinePopulation(Map<String, String> attributeValueMap, SourceAccountingLine sourceAccountingLine, String accountingLineAsString) {
        // chose debit/credit
        String debitValue = attributeValueMap.remove(DEBIT);
        String creditValue = attributeValueMap.remove(CREDIT);
        KualiDecimal debitAmount = null;
        try {
            if (StringUtils.isNotBlank(debitValue)) {
                debitAmount = new KualiDecimal(debitValue);
            }
        }
        catch (NumberFormatException e) {
            String[] errorParameters = { debitValue, retrieveAttributeLabel(sourceAccountingLine.getClass(), DEBIT), accountingLineAsString };
            throw new AccountingLineParserException("invalid (NaN) '" + DEBIT + "=" + debitValue + " for " + accountingLineAsString, ERROR_INVALID_PROPERTY_VALUE, errorParameters);
        }
        KualiDecimal creditAmount = null;
        try {
            if (StringUtils.isNotBlank(creditValue)) {
                creditAmount = new KualiDecimal(creditValue);
            }
        }
        catch (NumberFormatException e) {
            String[] errorParameters = { creditValue, retrieveAttributeLabel(sourceAccountingLine.getClass(), CREDIT), accountingLineAsString };
            throw new AccountingLineParserException("invalid (NaN) '" + CREDIT + "=" + creditValue + " for " + accountingLineAsString, ERROR_INVALID_PROPERTY_VALUE, errorParameters);
        }

        KualiDecimal amount = null;
        String debitCreditCode = null;
        if (debitAmount != null && debitAmount.isNonZero()) {
            amount = debitAmount;
            debitCreditCode = KFSConstants.GL_DEBIT_CODE;
        }

        if (creditAmount != null && creditAmount.isNonZero()) {
            amount = creditAmount;
            debitCreditCode = KFSConstants.GL_CREDIT_CODE;
        }

        sourceAccountingLine.setAmount(amount);
        sourceAccountingLine.setDebitCreditCode(debitCreditCode);

        boolean isFinancialOffsetGeneration = SpringContext.getBean(BalanceTypeService.class).getBalanceTypeByCode(balanceTypeCode).isFinancialOffsetGenerationIndicator();
        if (isFinancialOffsetGeneration || StringUtils.equals(balanceTypeCode, KFSConstants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE)) {
            super.performCustomSourceAccountingLinePopulation(attributeValueMap, sourceAccountingLine, accountingLineAsString);
        }
        sourceAccountingLine.setBalanceTypeCode(balanceTypeCode);
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(selectFormat());
    }

    /**
     * chooses line format based on balance type code
     * 
     * @return String []
     */
    private String[] selectFormat() {
        return LABOR_JV_FORMAT;
    }
}
