/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.optionfinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSPropertyConstants;

public class LaborOriginEntryFieldFinder extends KeyValuesBase {

    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new KeyLabelPair("universityFiscalYear", "Fiscal Year"));
        activeLabels.add(new KeyLabelPair("chartOfAccountsCode", "Chart Code"));
        activeLabels.add(new KeyLabelPair("accountNumber", "Account Number"));
        activeLabels.add(new KeyLabelPair("subAccountNumber", "Sub-Account Number"));
        activeLabels.add(new KeyLabelPair("financialObjectCode", "Object Code"));
        activeLabels.add(new KeyLabelPair("financialSubObjectCode", "Sub-Object Code"));
        activeLabels.add(new KeyLabelPair("financialBalanceTypeCode", "Balance Type"));
        activeLabels.add(new KeyLabelPair("financialObjectTypeCode", "Object Type"));
        activeLabels.add(new KeyLabelPair("universityFiscalPeriodCode", "Fiscal Period"));
        activeLabels.add(new KeyLabelPair("financialDocumentTypeCode", "Document Type"));
        activeLabels.add(new KeyLabelPair("financialSystemOriginationCode", "Origin code"));
        activeLabels.add(new KeyLabelPair(KFSPropertyConstants.DOCUMENT_NUMBER, "Document Number"));
        activeLabels.add(new KeyLabelPair("transactionLedgerEntrySequenceNumber", "Sequence Number"));
        
        activeLabels.add(new KeyLabelPair("positionNumber", "Position Number"));
        activeLabels.add(new KeyLabelPair("projectCode", "Project Code"));
        activeLabels.add(new KeyLabelPair("transactionLedgerEntryDescription", "Description"));
        activeLabels.add(new KeyLabelPair("transactionLedgerEntryAmount", "Amount"));
        activeLabels.add(new KeyLabelPair("transactionDebitCreditCode", "Debit Credit Indicator"));
        activeLabels.add(new KeyLabelPair("transactionDate", "Transaction Date"));
        activeLabels.add(new KeyLabelPair("organizationDocumentNumber", "Org Doc Number"));
        activeLabels.add(new KeyLabelPair("organizationReferenceId", "Org Ref ID"));
        activeLabels.add(new KeyLabelPair("referenceFinancialDocumentTypeCode", "Ref Doc Type"));
        activeLabels.add(new KeyLabelPair("referenceFinancialSystemOriginationCode", "Ref Origin code"));
        activeLabels.add(new KeyLabelPair("referenceFinancialDocumentNumber", "Ref Doc Number"));
        activeLabels.add(new KeyLabelPair("financialDocumentReversalDate", "Reversal Date"));
        activeLabels.add(new KeyLabelPair("transactionEncumbranceUpdateCode", "Enc Update Code"));
        
        activeLabels.add(new KeyLabelPair("transactionPostingDate", "Transaction Posting Date"));
        activeLabels.add(new KeyLabelPair("payPeriodEndDate", "Pay Period End Date"));
        activeLabels.add(new KeyLabelPair("transactionTotalHours", "Trn Total Hours"));
        activeLabels.add(new KeyLabelPair("payrollEndDateFiscalYear", "Payroll EndDate Fiscal Year"));
        activeLabels.add(new KeyLabelPair("payrollEndDateFiscalPeriodCode", "Payroll EndDate Fiscal Period Code"));
        activeLabels.add(new KeyLabelPair("emplid", "Empl Id"));
        activeLabels.add(new KeyLabelPair("employeeRecord", "Empl Record"));
        activeLabels.add(new KeyLabelPair("earnCode", "Earn Code"));
        activeLabels.add(new KeyLabelPair("payGroup", "Pay Group"));
        activeLabels.add(new KeyLabelPair("salaryAdministrationPlan", "Salary Admin Plan"));
        activeLabels.add(new KeyLabelPair("grade", "Grade"));
        activeLabels.add(new KeyLabelPair("runIdentifier", "Run Id"));
        activeLabels.add(new KeyLabelPair("laborLedgerOriginalChartOfAccountsCode", "LD Original Accounts Code"));
        activeLabels.add(new KeyLabelPair("laborLedgerOriginalAccountNumber", "LD Original Account Number"));
        activeLabels.add(new KeyLabelPair("laborLedgerOriginalSubAccountNumber", "LD Original Sub-Account Numbere"));
        activeLabels.add(new KeyLabelPair("laborLedgerOriginalFinancialObjectCode", "LD Original Object Code"));
        activeLabels.add(new KeyLabelPair("laborLedgerOriginalFinancialSubObjectCode", "LD Original Sub-Object Code"));
        activeLabels.add(new KeyLabelPair("hrmsCompany", "Company"));
        activeLabels.add(new KeyLabelPair("setid", "SetId"));
        
        return activeLabels;
    }

    public String getFieldDisplayName(String fieldName) {
        for (Iterator iter = getKeyValues().iterator(); iter.hasNext();) {
            KeyLabelPair klp = (KeyLabelPair)iter.next();
            if ( klp.getKey().equals(fieldName) ) {
                return klp.getLabel();
            }
        }
        return "Error";
    }

    public String getFieldName(String fieldDisplayName) {
        for (Iterator iter = getKeyValues().iterator(); iter.hasNext();) {
            KeyLabelPair klp = (KeyLabelPair)iter.next();
            if ( klp.getLabel().equals(fieldDisplayName) ) {
                return (String)klp.getKey();
            }
        }
        return "Error";
    }

    public boolean isValidValue(String fieldName,String value) {
        if (StringUtils.isBlank(fieldName)) {
            return false;
        }
        String fieldType = getFieldType(fieldName);
        int fieldLength = getFieldLength(fieldName);

        if ( allowNull(fieldName) && (value == null || value.length() == 0)) {
            return true;
        }
        if (!allowNull(fieldName) && (value == null || value.length() == 0)) {
            return false;
        }
        if ( value.length() > fieldLength ) {
            return false;
        }
        if ( "KualiDecimal".equals(fieldType) ) {
            try {
                KualiDecimal d = new KualiDecimal(value);
                return true;
            } catch (NumberFormatException nfe) {
                return false;
            }
        } else if ( "Integer".equals(fieldType) ) {
            try {
                Integer d = new Integer(value);
                return true;
            } catch (NumberFormatException nfe) {
                return false;
            }
        } else if ( "Date".equals(fieldType) ) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d = df.parse(value);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
        return true;
    }

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
            return "KualiDecimal";
        }
        if (fieldName.equals("payrollEndDateFiscalYear")) {
            return "Integer";
        }
        if (fieldName.equals("employeeRecord")) {
            return "Integer";
        }
        if (fieldName.equals("transactionDateTimeStamp")) {
            return "Date";
        }
        
          return "String";
    }

    public boolean allowNull(String fieldName) {
        if (fieldName.equals("transactionLedgerEntryAmount")) {
            return false;
        }
        return true;
    }
    
    public int getFieldLength(String fieldName) {
        if (fieldName.equals("universityFiscalYear")) {
            return 4;
        } else if (fieldName.equals("transactionLedgerEntrySequenceNumber")) {
            return 5;
        } else if (fieldName.equals("transactionLedgerEntryAmount")) {
            return 19;
        } else if (fieldName.equals("transactionDate")) {
            return 10;
        } else if (fieldName.equals("financialDocumentReversalDate")) {
            return 10;
        } else if (fieldName.equals("chartOfAccountsCode")) {
            return 2;
        } else if (fieldName.equals("accountNumber")) {
            return 7;
        } else if (fieldName.equals("subAccountNumber")) {
            return 5;
        } else if (fieldName.equals("financialObjectCode")) {
            return 4;
        } else if (fieldName.equals("financialSubObjectCode")) {
            return 3;
        } else if (fieldName.equals("financialBalanceTypeCode")) {
            return 2;
        } else if (fieldName.equals("financialObjectTypeCode")) {
            return 2;
        } else if (fieldName.equals("universityFiscalPeriodCode")) {
            return 2;
        } else if (fieldName.equals("financialDocumentTypeCode")) {
            return 4;
        } else if (fieldName.equals("financialSystemOriginationCode")) {
            return 2;
        } else if (fieldName.equals(KFSPropertyConstants.DOCUMENT_NUMBER)) {
            return 14;
        } else if (fieldName.equals("transactionLedgerEntryDescription")) {
            return 40;
        } else if (fieldName.equals("transactionDebitCreditCode")) {
            return 1;
        } else if (fieldName.equals("organizationDocumentNumber")) {
            return 10;
        } else if (fieldName.equals("projectCode")) {
            return 10;
        } else if (fieldName.equals("organizationReferenceId")) {
            return 8;
        } else if (fieldName.equals("referenceFinancialDocumentTypeCode")) {
            return 4;
        } else if (fieldName.equals("referenceFinancialSystemOriginationCode")) {
            return 2;
        } else if (fieldName.equals("referenceFinancialDocumentNumber")) {
            return 14;
        } else if (fieldName.equals("transactionEncumbranceUpdateCode")) {
            return 1;
        } else if (fieldName.equals("positionNumber")) {
            return 8;
        } else if (fieldName.equals("transactionPostingDate")) {
           return 7;
        } else if (fieldName.equals("payPeriodEndDate")) {
           return 7;
        } else if (fieldName.equals("transactionTotalHours")) {
           return 22;
        } else if (fieldName.equals("payrollEndDateFiscalYear")) {
           return 4;
        } else if (fieldName.equals("payrollEndDateFiscalPeriodCode")) {
           return 2;
        } else if (fieldName.equals("emplid")) {
           return 11;
        } else if (fieldName.equals("employeeRecord")) {
           return 3;
        } else if (fieldName.equals("earnCode")) {
           return 3;
        } else if (fieldName.equals("payGroup")) {
           return 3;
        } else if (fieldName.equals("salaryAdministrationPlan")) {
           return 4;
        } else if (fieldName.equals("grade")) {
           return 3;
        } else if (fieldName.equals("runIdentifier")) {
           return 10;
        } else if (fieldName.equals("laborLedgerOriginalChartOfAccountsCode")) {
           return 2;
        } else if (fieldName.equals("laborLedgerOriginalAccountNumber")) {
           return 7;
        } else if (fieldName.equals("laborLedgerOriginalSubAccountNumber")) {
           return 5;
        } else if (fieldName.equals("laborLedgerOriginalFinancialObjectCode")) {
           return 4;
        } else if (fieldName.equals("laborLedgerOriginalFinancialSubObjectCode")) {
           return 3;
        } else if (fieldName.equals("hrmsCompany")) {
           return 3;
        } else if (fieldName.equals("setid")) {
           return 5;
        }
                    
        return 0;
    }
}
