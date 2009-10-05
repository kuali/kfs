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
package org.kuali.kfs.gl.businessobject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.exception.LoadException;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.service.impl.KEWModuleService;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class has utility methods for OriginEntry
 */
public class OriginEntryFieldUtil {

    public Map<String, Integer> getFieldLengthMap() {
        Map<String, Integer> fieldLengthMap = new HashMap();
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        List<AttributeDefinition> attributes = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(OriginEntryFull.class.getName()).getAttributes();

        for (AttributeDefinition attributeDefinition : attributes) {
            Integer fieldLength;
            fieldLength = dataDictionaryService.getAttributeMaxLength(OriginEntryFull.class, attributeDefinition.getName());
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
        positionMap.put(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC, positionMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER) + lengthMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER));
        positionMap.put(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT, positionMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC) + lengthMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC));
        positionMap.put(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, positionMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT) + lengthMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT));
        positionMap.put(KFSPropertyConstants.TRANSACTION_DATE, positionMap.get(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE) + lengthMap.get(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE));
        positionMap.put(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER, positionMap.get(KFSPropertyConstants.TRANSACTION_DATE) + lengthMap.get(KFSPropertyConstants.TRANSACTION_DATE));
        positionMap.put(KFSPropertyConstants.PROJECT_CODE, positionMap.get(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER) + lengthMap.get(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER));
        positionMap.put(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID, positionMap.get(KFSPropertyConstants.PROJECT_CODE) + lengthMap.get(KFSPropertyConstants.PROJECT_CODE));
        positionMap.put(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE, positionMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID) + lengthMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID));
        positionMap.put(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE, positionMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE) + lengthMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE));
        positionMap.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR, positionMap.get(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE) + lengthMap.get(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE));
        positionMap.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, positionMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR) + lengthMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR));
        positionMap.put(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD, positionMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE) + lengthMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE));
        return positionMap;
    }
    
    public String fillFieldWithZero(int fieldLength, String fieldString){
        while (fieldLength  > fieldString.length()) {
            fieldString += "0";
        }
        return fieldString;
    }
}
