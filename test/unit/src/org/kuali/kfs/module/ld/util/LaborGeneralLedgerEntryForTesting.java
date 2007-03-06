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

import org.kuali.PropertyConstants;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborGeneralLedgerEntry;
import org.kuali.module.labor.util.ObjectUtil;

public class LaborGeneralLedgerEntryForTesting extends LaborGeneralLedgerEntry {
    
    @ Override
    public boolean equals(Object otherEntry){
        return ObjectUtil.compareObject(this, otherEntry, LaborConstants.consolidationAttributesOfOriginEntry());
    }
    
    public Map getPrimaryKeyMap() {
        return ObjectUtil.buildPropertyMap(this, this.getPrimaryKeyList());
    }
    
    public List<String> getPrimaryKeyList(){
        List<String> primaryKeyList = new ArrayList<String>();
        primaryKeyList.add(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        primaryKeyList.add(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        primaryKeyList.add(PropertyConstants.ACCOUNT_NUMBER);
        primaryKeyList.add(PropertyConstants.SUB_ACCOUNT_NUMBER);
        primaryKeyList.add(PropertyConstants.FINANCIAL_OBJECT_CODE);
        primaryKeyList.add(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        primaryKeyList.add(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        primaryKeyList.add(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        primaryKeyList.add(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        primaryKeyList.add(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        primaryKeyList.add(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
        primaryKeyList.add(PropertyConstants.DOCUMENT_NUMBER);
        primaryKeyList.add(PropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
        return primaryKeyList;
    }
}
