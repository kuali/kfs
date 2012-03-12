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
package org.kuali.kfs.module.ld.businessobject.options;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * Field Finder for Labor Origin Entry.
 */
public class LaborOriginEntryFieldFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, "Fiscal Year"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "Chart Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.ACCOUNT_NUMBER, "Account Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, "Sub-Account Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, "Object Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, "Sub-Object Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "Balance Type"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, "Object Type"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, "Fiscal Period"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, "Document Type"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, "Origin code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.DOCUMENT_NUMBER, "Document Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, "Sequence Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.POSITION_NUMBER, "Position Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.PROJECT_CODE, "Project Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC, "Description"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT, "Amount"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, "Debit Credit Indicator"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_DATE, "Transaction Date"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER, "Org Doc Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID, "Org Ref ID"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE, "Ref Doc Type"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE, "Ref Origin code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR, "Ref Doc Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, "Reversal Date"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD, "Enc Update Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_POSTING_DATE, "Transaction Posting Date"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.PAY_PERIOD_END_DATE, "Pay Period End Date"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_TOTAL_HOURS, "Trn Total Hours"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, "Payroll EndDate Fiscal Year"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, "Payroll EndDate Fiscal Period Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.EMPLID, "Empl Id"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.EMPLOYEE_RECORD, "Empl Record"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.EARN_CODE, "Earn Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.PAY_GROUP, "Pay Group"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.SALARY_ADMINISTRATION_PLAN, "Salary Admin Plan"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.GRADE, "Grade"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.RUN_IDENTIFIER, "Run Id"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE, "Original Chart Code"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER, "Original Account Number"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER, "Original Sub-Account Numbere"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE, "Original Object Code"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE, "Original Sub-Object Code"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.HRMS_COMPANY, "Company"));
        activeLabels.add(new ConcreteKeyValue(LaborPropertyConstants.SET_ID, "SetId"));

        return activeLabels;
    }

    /**
     * Get field display name.
     * 
     * @param fieldName
     * @return Returns the label
     */
    public String getFieldDisplayName(String fieldName) {
        for (Iterator iter = getKeyValues().iterator(); iter.hasNext();) {
            KeyValue klp = (KeyValue) iter.next();
            if (klp.getKey().equals(fieldName)) {
                return klp.getValue();
            }
        }

        return "Error";
    }

    /**
     * Get field name
     * 
     * @param fieldDisplayName
     * @return Returns the key
     */
    public String getFieldName(String fieldDisplayName) {
        for (Iterator iter = getKeyValues().iterator(); iter.hasNext();) {
            KeyValue klp = (KeyValue) iter.next();
            if (klp.getValue().equals(fieldDisplayName)) {
                return (String) klp.getKey();
            }
        }

        return "Error";
    }

    /**
     * Check each field for valid value.
     * 
     * @param fieldName
     * @param value
     * @return Returns a boolean
     */
    public boolean isValidValue(String fieldName, String value) {
        if (StringUtils.isBlank(fieldName)) {
            return false;
        }
        String fieldType = getFieldType(fieldName);
        int fieldLength = getFieldLength(fieldName);

        if (allowNull(fieldName) && (value == null || value.length() == 0)) {
            return true;
        }
        if (!allowNull(fieldName) && (value == null || value.length() == 0)) {
            return false;
        }
        if (value.length() > fieldLength) {
            return false;
        }
        if ("BigDecimal".equals(fieldType)) {
            try {
                BigDecimal d = new BigDecimal(value);
                return true;
            }
            catch (NumberFormatException nfe) {
                return false;
            }
        } 
        if ("KualiDecimal".equals(fieldType)) {
            try {
                KualiDecimal d = new KualiDecimal(value);
                return true;
            }
            catch (NumberFormatException nfe) {
                return false;
            }
        }
        else if ("Integer".equals(fieldType)) {
            try {
                Integer d = new Integer(value);
                return true;
            }
            catch (NumberFormatException nfe) {
                return false;
            }
        }
        else if ("Date".equals(fieldType)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d = df.parse(value);
                return true;
            }
            catch (ParseException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get field type.
     * 
     * @param fieldName
     * @return Returns the fieldType
     */
    public String getFieldType(String fieldName) {
        if (fieldName.equals("universityFiscalYear")) {
            return "Integer";
        }
        if (fieldName.equals("transactionLedgerEntrySequenceNumber")) {
            return "Integer";
        }
        if (fieldName.equals("transactionLedgerEntryAmount")) {
            return "KualiDecimal";
        }
        if (fieldName.equals("transactionDate")) {
            return "Date";
        }
        if (fieldName.equals("financialDocumentReversalDate")) {
            return "Date";
        }
        if (fieldName.equals("transactionPostingDate")) {
            return "Date";
        }
        if (fieldName.equals("payPeriodEndDate")) {
            return "Date";
        }
        if (fieldName.equals("transactionTotalHours")) {
            return "BigDecimal";
        }
        if (fieldName.equals("payrollEndDateFiscalYear")) {
            return "Integer";
        }
        if (fieldName.equals("transactionEntryProcessedTimestamp")) {
            return "Date";
        }
        if (fieldName.equals("employeeRecord")) {
            return "Integer";
        }
        if (fieldName.equals("transactionDateTimeStamp")) {
            return "Date";
        }
        return "String";
    }

    /**
     * Check if field allows null value
     * 
     * @param fieldName
     * @return Returns a boolean
     */
    public boolean allowNull(String fieldName) {
        if (fieldName.equals("transactionLedgerEntryAmount")) {
            return false;
        }
        return true;
    }

    /**
     * Get length of field from fieldName
     * 
     * @param fieldName
     * @return Returns an int
     */
    public int getFieldLength(String fieldName) {
        if (fieldName.equals("universityFiscalYear")) {
            return 4;
        }
        else if (fieldName.equals("transactionLedgerEntrySequenceNumber")) {
            return 5;
        }
        else if (fieldName.equals("transactionLedgerEntryAmount")) {
            return 20;
        }
        else if (fieldName.equals("transactionDate")) {
            return 10;
        }
        else if (fieldName.equals("financialDocumentReversalDate")) {
            return 10;
        }
        else if (fieldName.equals("chartOfAccountsCode")) {
            return 2;
        }
        else if (fieldName.equals("accountNumber")) {
            return 7;
        }
        else if (fieldName.equals("subAccountNumber")) {
            return 5;
        }
        else if (fieldName.equals("financialObjectCode")) {
            return 4;
        }
        else if (fieldName.equals("financialSubObjectCode")) {
            return 3;
        }
        else if (fieldName.equals("financialBalanceTypeCode")) {
            return 2;
        }
        else if (fieldName.equals("financialObjectTypeCode")) {
            return 2;
        }
        else if (fieldName.equals("universityFiscalPeriodCode")) {
            return 2;
        }
        else if (fieldName.equals("financialDocumentTypeCode")) {
            return 4;
        }
        else if (fieldName.equals("financialSystemOriginationCode")) {
            return 2;
        }
        else if (fieldName.equals(KFSPropertyConstants.DOCUMENT_NUMBER)) {
            return 14;
        }
        else if (fieldName.equals("transactionLedgerEntryDescription")) {
            return 40;
        }
        else if (fieldName.equals("transactionDebitCreditCode")) {
            return 1;
        }
        else if (fieldName.equals("organizationDocumentNumber")) {
            return 10;
        }
        else if (fieldName.equals("projectCode")) {
            return 10;
        }
        else if (fieldName.equals("organizationReferenceId")) {
            return 8;
        }
        else if (fieldName.equals("referenceFinancialDocumentTypeCode")) {
            return 4;
        }
        else if (fieldName.equals("referenceFinancialSystemOriginationCode")) {
            return 2;
        }
        else if (fieldName.equals("referenceFinancialDocumentNumber")) {
            return 14;
        }
        else if (fieldName.equals("transactionEncumbranceUpdateCode")) {
            return 1;
        }
        else if (fieldName.equals("positionNumber")) {
            return 8;
        }
        else if (fieldName.equals("transactionPostingDate")) {
            return 10;
        }
        else if (fieldName.equals("payPeriodEndDate")) {
            return 10;
        }
        else if (fieldName.equals("transactionTotalHours")) {
            return 22;
        }
        else if (fieldName.equals("payrollEndDateFiscalYear")) {
            return 4;
        }
        else if (fieldName.equals("payrollEndDateFiscalPeriodCode")) {
            return 2;
        }
        else if (fieldName.equals("emplid")) {
            return 11;
        }
        else if (fieldName.equals("employeeRecord")) {
            return 3;
        }
        else if (fieldName.equals("earnCode")) {
            return 3;
        }
        else if (fieldName.equals("payGroup")) {
            return 3;
        }
        else if (fieldName.equals("salaryAdministrationPlan")) {
            return 4;
        }
        else if (fieldName.equals("grade")) {
            return 3;
        }
        else if (fieldName.equals("runIdentifier")) {
            return 10;
        }
        else if (fieldName.equals("laborLedgerOriginalChartOfAccountsCode")) {
            return 2;
        }
        else if (fieldName.equals("laborLedgerOriginalAccountNumber")) {
            return 7;
        }
        else if (fieldName.equals("laborLedgerOriginalSubAccountNumber")) {
            return 5;
        }
        else if (fieldName.equals("laborLedgerOriginalFinancialObjectCode")) {
            return 4;
        }
        else if (fieldName.equals("laborLedgerOriginalFinancialSubObjectCode")) {
            return 3;
        }
        else if (fieldName.equals("hrmsCompany")) {
            return 3;
        }
        else if (fieldName.equals("setid")) {
            return 5;
        }

        return 0;
    }
}
