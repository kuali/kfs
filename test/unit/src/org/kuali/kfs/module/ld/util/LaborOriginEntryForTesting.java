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
package org.kuali.module.labor.util.testobject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.util.ObjectUtil;

/**
 * This class...
 */
public class LaborOriginEntryForTesting extends LaborOriginEntry {
    private List<String> keyValueList;
    
    @ Override
    public boolean equals(Object otherOriginEntry){
        return ObjectUtil.compareObject(this, otherOriginEntry, this.getKeyValueList());
    }
    
    public Map getKeyValueMap() {
        return ObjectUtil.buildPropertyMap(this, this.getKeyValueList());
    }
    
    public List<String> getDefaultKeyValueList(){
        List<String> keyValueList = new ArrayList<String>();
        keyValueList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keyValueList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keyValueList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keyValueList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        
        keyValueList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        keyValueList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        keyValueList.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        keyValueList.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        
        keyValueList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        keyValueList.add(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        keyValueList.add(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
        keyValueList.add(KFSPropertyConstants.DOCUMENT_NUMBER);
        keyValueList.add(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
        
        keyValueList.add(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        keyValueList.add(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
        keyValueList.add(KFSPropertyConstants.POSITION_NUMBER);
        keyValueList.add(KFSPropertyConstants.RUN_IDENTIFIER);
        keyValueList.add(KFSPropertyConstants.EMPLID);
        return keyValueList;
    }
    
    /**
     * Gets the keyValueList attribute. 
     * @return Returns the keyValueList.
     */
    public List<String> getKeyValueList() {
        if(keyValueList == null){
            keyValueList = this.getDefaultKeyValueList();
        }
        return keyValueList;
    }

    /**
     * Sets the keyValueList attribute value.
     * @param keyValueList The keyValueList to set.
     */
    public void setKeyValueList(List<String> keyValueList) {
        this.keyValueList = keyValueList;
    }  
}
