/*
 * Copyright 2005-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ld.batch;

import java.util.Comparator;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LaborOriginEntryFieldUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;

public class LaborScrubberSortComparator implements Comparator {

    public int compare(Object object1, Object object2) {
        LaborOriginEntryFieldUtil loefu = new LaborOriginEntryFieldUtil();
        Map<String, Integer> pMap = loefu.getFieldBeginningPositionMap();
            
        String string1 = (String) object1;
        String string2 = (String) object2;
        
        StringBuffer sb1 = new StringBuffer();
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE), pMap.get(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE)));
            
        StringBuffer sb2 = new StringBuffer();
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE), pMap.get(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE)));

        return sb1.toString().compareTo(sb2.toString());
    }
}