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

import org.kuali.PropertyConstants;

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

        consolidationAttributes.add(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        consolidationAttributes.add(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);

        consolidationAttributes.add(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        consolidationAttributes.add(PropertyConstants.ACCOUNT_NUMBER);
        consolidationAttributes.add(PropertyConstants.SUB_ACCOUNT_NUMBER);

        consolidationAttributes.add(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        consolidationAttributes.add(PropertyConstants.FINANCIAL_OBJECT_CODE);
        consolidationAttributes.add(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        consolidationAttributes.add(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        consolidationAttributes.add(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        consolidationAttributes.add(PropertyConstants.DOCUMENT_NUMBER);
        consolidationAttributes.add(PropertyConstants.ORGANIZATION_DOCUMENT_NUMBER);

        consolidationAttributes.add(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
        consolidationAttributes.add(PropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        consolidationAttributes.add(PropertyConstants.PROJECT_CODE);
        consolidationAttributes.add(PropertyConstants.ORGANIZATION_REFERENCE_ID);

        consolidationAttributes.add(PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        consolidationAttributes.add(PropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);

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
        public static final String[] VALID_LABOR_OBJECT_CODES = {"2000","2001","2400","2401","2500"};
        public static final String UnknownPersonName = "* Unknown Name *";        
        public static final String ANNUAL_BALANCE = "ACLN_ANNL_BAL_AMT";
        public static final String CONTRACT_GRANT_BB_AMOUNT = "CONTR_GR_BB_AC_AMT";
        public static final String ENCUMBERENCE_CODE = "'IE'";
        public static final String ACTUALS_CODE = "'AC'";
        public static final String BALANCE_CODE = "'AC'";
}
}
