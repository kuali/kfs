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
package org.kuali.module.labor;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * This class contains the constants used by Labor Distribution.
 */
public class LaborConstants {
    public static class PayrollDocumentTypeCode {
        public static final String NORMAL_PAY = "PAY";
        public static final String RETROACTIVE_ADJUSTMENT = "RETR";

        public static final String ENCUMBRANCE = "PAYE";
        public static final String CHECK_CANCELLATION = "PAYC";
        public static final String OVERPAYMENT = "OPAY";

        public static final String HAND_DRAWN_CHECK = "HDRW";
        public static final String ACCRUALS = "PAYA";
        public static final String ACCRUALS_REVERSAL = "PAYN";

        public static final String EXPENSE_TRANSFER_ST = "ST";
        public static final String EXPENSE_TRANSFER_BT = "BT";
        public static final String EXPENSE_TRANSFER_ET = "ET";
        public static final String EXPENSE_TRANSFER_SACH = "SACH";
        public static final String EXPENSE_TRANSFER_YEST = "YEST";
        public static final String EXPENSE_TRANSFER_YEBT = "YEBT";
    }

    public static class DestinationNames {
        public static final String LEDGER_BALANCE = "LD_LEDGER_BALANCE_T";
        public static final String LEDGER_ENRTY = "LD_LEDGER_ENTRY_T";
        public static final String LABOR_GL_ENTRY = "LD_LABOR_GL_ENTRY_T";
        public static final String ORIGN_ENTRY = "LD_ORIGIN_ENTRY_T";
    }

    public static class TransactionGeneratorNames {
        public static final String SALARY_ACCRUAL = "SALARY ACCRUAL";
        public static final String BENEFIT_ACCRUAL = "BENEFIT ACCRUAL";
        public static final String SALARY_REVERSAL = "SALARY REVERSAL";
        public static final String BENEFIT_REVERSAL = "BENEFIT REVERSAL";
        public static final String ENCUMBRANCE = "ENCUMBRANCE";
        public static final String DISENCUMBRANCE = "DISENCUMBRANCE";
    }

    public static List<String> consolidationAttributesOfOriginEntry() {
        List<String> consolidationAttributes = new ArrayList<String>();

        consolidationAttributes.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        consolidationAttributes.add(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);

        consolidationAttributes.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        consolidationAttributes.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        consolidationAttributes.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        consolidationAttributes.add(KFSPropertyConstants.DOCUMENT_NUMBER);
        consolidationAttributes.add(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER);

        consolidationAttributes.add(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
        consolidationAttributes.add(KFSPropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        consolidationAttributes.add(KFSPropertyConstants.PROJECT_CODE);
        consolidationAttributes.add(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);

        consolidationAttributes.add(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        consolidationAttributes.add(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);

        return consolidationAttributes;
    }

    public static class SalaryExpenseTransfer {
        public static final String LABOR_LEDGER_SALARY_CODE = "S";
    }

    public static class BenefitExpenseTransfer {
        public static final String LABOR_LEDGER_BENEFIT_CODE = "F";
    }

    public static class BalanceInquiries {
        public static final String ERROR_INVALID_LABOR_OBJECT_CODE = "error.labor.invalidLaborObjectCodeError";
        public static final String[] VALID_LABOR_OBJECT_CODES = { "2000", "2001", "2400", "2401", "2500" };
        public static final String UnknownPersonName = "* Unknown Name *";
        public static final String ANNUAL_BALANCE = "ACLN_ANNL_BAL_AMT";
        public static final String CONTRACT_GRANT_BB_AMOUNT = "CONTR_GR_BB_AC_AMT";
        public static final String ENCUMBERENCE_CODE = "'IE'";
        public static final String ACTUALS_CODE = "'AC'";
        public static final String BALANCE_CODE = "'BB'";
        public static final String EMPLOYEE_FUNDING_EXPENSE_OBJECT_TYPE_CODE = "ES";
        public static final String EMPLOYEE_FUNDING_NORMAL_OP_EXPENSE_OBJECT_TYPE_CODE = "EX";
    }

    public static class ParameterGroups {
        public static final String PAYROLL_ACCRUAL = "Labor.PayrollAccrual";
        public static final String YEAR_END = "Labor.YearEnd";
        public static final String POSTER = "Labor.Poster";
    }
    
    public static class PayrollAccrual {
        public static final String ACCRUAL_DAYS = "input.accrualDays";
        public static final String WORK_DAYS = "input.workDays";
        public static final String FISCAL_YEAR = "input.fiscalYear";
        public static final String FISCAL_PERIOD = "input.fiscalPeriod";
        public static final String REVERSAL_FISCAL_YEAR = "input.reversalFiscalYear";
        public static final String REVERSAL_FISCAL_PERIOD = "input.reversalFiscalPeriod";
        public static final String DOCUMENT_NUMBER = "input.documentNumber";
        public static final String EARN_CODES = "input.earnCodes";
        public static final String RUN_ID = "input.runId";
        public static final String EXCLUDED_SUB_FUND_GROUPS = "input.excludedSubFundGroups";
        
        public static final String ORIGINATION_CODE = "originationCode";
    }
    
    public static class Poster {
        public static final String PERIOD_CODES_NOT_PROCESSED = "periodCodesNotProcessed";
        public static final String OBJECT_CODES_NOT_PROCESSED = "objectsNotProcessed";
        public static final String BALANCE_TYPES_NOT_PROCESSED = "balanceTypesNotProcessed";
    }
    
    public static class YearEnd {
        public static final String FUND_GROUP_PROCESSED = "fundGroupProcessed";
        public static final String DOCUMENT_TYPE_CODE = "documentTypeCode";
        public static final String ORIGINATION_CODE = "originationCode";
        public static final String OLD_FISCAL_YEAR = "oldFiscalYear";
    }

    public static final class LABOR_LEDGER_PENDING_ENTRY_CODE {
        public static final String NO = "N";
        public static final String YES = "Y";
        public static final String BLANK_PROJECT_STRING = "----------"; // Max length is 10 for this field
        public static final String BLANK_SUB_OBJECT_CODE = "---"; // Max length is 3 for this field
        public static final String BLANK_SUB_ACCOUNT_NUMBER = "-----"; // Max length is 5 for this field
        public static final String BLANK_OBJECT_CODE = "----"; // Max length is 4 for this field
        public static final String BLANK_OBJECT_TYPE_CODE = "--"; // Max length is 4 for this field
        public static final String BLANK_POSITION_NUMBER = "--------"; // Max length is 8 for this field
        public static final String BLANK_EMPL_ID = "-----------"; // Max length is 11 for this field
        public static final String LL_PE_OFFSET_STRING = "TP Generated Offset";
        public static final int LLPE_DESCRIPTION_MAX_LENGTH = 40;
    }
    public static final String LABOR_LEDGER_CHART_OF_ACCOUNT_CODE = "UA";

    
    public enum JournalVoucherOffsetType {
        NO_OFFSET("LLJV", "No Offset"),
        ACCRUAL("LJVA", "Accrual"),
        CASH("LJVC", "Cash"),
        ENCUMBRANCE("LJVE", "Encumbrance");
        
        public String typeCode;
        public String description;
        
        private JournalVoucherOffsetType(String typeCode, String description){
            this.typeCode = typeCode;
            this.description = description;
        }        
    }
    
    public static final String LONG_ROW_TABLE_INRUIRY_ACTION = "laborLongRowTableInquiry.do";
    public static final String LABOR_USER_SERVICE_NAME = "laborUserService";
}
