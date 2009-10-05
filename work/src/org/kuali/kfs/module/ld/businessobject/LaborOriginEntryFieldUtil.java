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
package org.kuali.kfs.module.ld.businessobject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * This class has utility methods for OriginEntry
 */
public class LaborOriginEntryFieldUtil {

    public Map<String, Integer> getFieldLengthMap() {
        Map<String, Integer> fieldLengthMap = new HashMap();
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        List<AttributeDefinition> attributes = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(LaborOriginEntry.class.getName()).getAttributes();

        for (AttributeDefinition attributeDefinition : attributes) {
            Integer fieldLength;
            fieldLength = dataDictionaryService.getAttributeMaxLength(LaborOriginEntry.class, attributeDefinition.getName());
            fieldLengthMap.put(attributeDefinition.getName(), fieldLength);
        }
        return fieldLengthMap;
    }

    public Map<String, Integer> getFieldBeginningPositionMap() {
        Map<String, Integer> positionMap = new HashMap();
        Map<String, Integer> lengthMap = getFieldLengthMap();
        positionMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, 0);
        positionMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, lengthMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
        positionMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, positionMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE) + lengthMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
        positionMap.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, positionMap.get(KFSPropertyConstants.ACCOUNT_NUMBER) + lengthMap.get(KFSPropertyConstants.ACCOUNT_NUMBER));
        positionMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, positionMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER) + lengthMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER));
        positionMap.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, positionMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE) + lengthMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE));
        positionMap.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, positionMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE) + lengthMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE));
        positionMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, positionMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE) + lengthMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE));
        positionMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, positionMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE) + lengthMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE));
        positionMap.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, positionMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE) + lengthMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE));
        positionMap.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, positionMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE) + lengthMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE));
        positionMap.put(KFSPropertyConstants.DOCUMENT_NUMBER, positionMap.get(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE) + lengthMap.get(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE));
        positionMap.put(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, positionMap.get(KFSPropertyConstants.DOCUMENT_NUMBER) + lengthMap.get(KFSPropertyConstants.DOCUMENT_NUMBER));
        positionMap.put(KFSPropertyConstants.POSITION_NUMBER, positionMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER) + lengthMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER));
        positionMap.put(KFSPropertyConstants.PROJECT_CODE, positionMap.get(KFSPropertyConstants.POSITION_NUMBER) + lengthMap.get(KFSPropertyConstants.POSITION_NUMBER));
        
        positionMap.put(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC, positionMap.get(KFSPropertyConstants.PROJECT_CODE) + lengthMap.get(KFSPropertyConstants.PROJECT_CODE));
        positionMap.put(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT, positionMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC) + lengthMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC));
        positionMap.put(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, positionMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT) + lengthMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT));
        positionMap.put(KFSPropertyConstants.TRANSACTION_DATE, positionMap.get(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE) + lengthMap.get(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE));
        positionMap.put(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER, positionMap.get(KFSPropertyConstants.TRANSACTION_DATE) + lengthMap.get(KFSPropertyConstants.TRANSACTION_DATE));
        positionMap.put(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID, positionMap.get(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER) + lengthMap.get(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER));
        positionMap.put(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE, positionMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID) + lengthMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID));
        positionMap.put(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE, positionMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE) + lengthMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE));
        positionMap.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR, positionMap.get(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE) + lengthMap.get(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE));
        positionMap.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, positionMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR) + lengthMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR));
        positionMap.put(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD, positionMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE) + lengthMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE));
        positionMap.put(KFSPropertyConstants.TRANSACTION_POSTING_DATE, positionMap.get(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD) + lengthMap.get(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD));
        positionMap.put(KFSPropertyConstants.PAY_PERIOD_END_DATE, positionMap.get(KFSPropertyConstants.TRANSACTION_POSTING_DATE) + lengthMap.get(KFSPropertyConstants.TRANSACTION_POSTING_DATE));
        positionMap.put(KFSPropertyConstants.TRANSACTION_TOTAL_HOURS, positionMap.get(KFSPropertyConstants.PAY_PERIOD_END_DATE) + lengthMap.get(KFSPropertyConstants.PAY_PERIOD_END_DATE));
        positionMap.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, positionMap.get(KFSPropertyConstants.TRANSACTION_TOTAL_HOURS) + lengthMap.get(KFSPropertyConstants.TRANSACTION_TOTAL_HOURS));
        positionMap.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, positionMap.get(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR) + lengthMap.get(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR));
        positionMap.put(KFSPropertyConstants.EMPLID, positionMap.get(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE) + lengthMap.get(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE));
        positionMap.put(KFSPropertyConstants.EMPLOYEE_RECORD, positionMap.get(KFSPropertyConstants.EMPLID) + lengthMap.get(KFSPropertyConstants.EMPLID));
        positionMap.put(KFSPropertyConstants.EARN_CODE, positionMap.get(KFSPropertyConstants.EMPLOYEE_RECORD) + lengthMap.get(KFSPropertyConstants.EMPLOYEE_RECORD));
        positionMap.put(KFSPropertyConstants.PAY_GROUP, positionMap.get(KFSPropertyConstants.EARN_CODE) + lengthMap.get(KFSPropertyConstants.EARN_CODE));
        positionMap.put(LaborPropertyConstants.SALARY_ADMINISTRATION_PLAN, positionMap.get(KFSPropertyConstants.PAY_GROUP) + lengthMap.get(KFSPropertyConstants.PAY_GROUP));
        positionMap.put(LaborPropertyConstants.GRADE, positionMap.get(LaborPropertyConstants.SALARY_ADMINISTRATION_PLAN) + lengthMap.get(LaborPropertyConstants.SALARY_ADMINISTRATION_PLAN));
        positionMap.put(LaborPropertyConstants.RUN_IDENTIFIER, positionMap.get(LaborPropertyConstants.GRADE) + lengthMap.get(LaborPropertyConstants.GRADE));
        positionMap.put(LaborPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE, positionMap.get(LaborPropertyConstants.RUN_IDENTIFIER) + lengthMap.get(LaborPropertyConstants.RUN_IDENTIFIER));
        positionMap.put(LaborPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER, positionMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE) + lengthMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE));
        positionMap.put(LaborPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER, positionMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER) + lengthMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER));
        positionMap.put(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE, positionMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER) + lengthMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER));
        positionMap.put(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE, positionMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE) + lengthMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE));
        positionMap.put(LaborPropertyConstants.HRMS_COMPANY, positionMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE) + lengthMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE));
        positionMap.put(LaborPropertyConstants.SET_ID, positionMap.get(LaborPropertyConstants.HRMS_COMPANY) + lengthMap.get(LaborPropertyConstants.HRMS_COMPANY));
        return positionMap;
    }
    
    public String fillFieldWithZero(int fieldLength, String fieldString){
        while (fieldLength  > fieldString.length()) {
            fieldString += "0";
        }
        return fieldString;
    }
}
