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
        
        final String[] orderedProperties = new String[] {
            KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
            KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
            KFSPropertyConstants.ACCOUNT_NUMBER,
            KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
            KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
            KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
            KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE,
            KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE,
            KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE,
            KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE,
            KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE,
            KFSPropertyConstants.DOCUMENT_NUMBER,
            KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER,
            KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC,
            KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT,
            KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE,
            KFSPropertyConstants.TRANSACTION_DATE,
            KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER,
            KFSPropertyConstants.PROJECT_CODE,
            KFSPropertyConstants.ORGANIZATION_REFERENCE_ID,
            KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE,
            KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE,
            KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR,
            KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE,
            KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD
        };
        int lengthTracker = 0;
   
        for (String property : orderedProperties) {
            positionMap.put(property, new Integer(lengthTracker));
            lengthTracker += lengthMap.get(property).intValue();
        }
        return positionMap;
    }
}
