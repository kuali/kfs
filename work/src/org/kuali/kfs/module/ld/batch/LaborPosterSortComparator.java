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

public class LaborPosterSortComparator implements Comparator {

    public int compare(Object object1, Object object2) {
        LaborOriginEntryFieldUtil loefu = new LaborOriginEntryFieldUtil();
        Map<String, Integer> pMap = loefu.getFieldBeginningPositionMap();
            
        String string1 = (String) object1;
        String string2 = (String) object2;
        StringBuffer sb1 = new StringBuffer();
            
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.PROJECT_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID), pMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER), pMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER), pMap.get(KFSPropertyConstants.POSITION_NUMBER)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.POSITION_NUMBER), pMap.get(KFSPropertyConstants.PROJECT_CODE)));
        sb1.append(string1.substring(pMap.get(KFSPropertyConstants.EMPLID), pMap.get(KFSPropertyConstants.EARN_CODE)));
            
        StringBuffer sb2 = new StringBuffer();
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.PROJECT_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID), pMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER), pMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER), pMap.get(KFSPropertyConstants.POSITION_NUMBER)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.POSITION_NUMBER), pMap.get(KFSPropertyConstants.PROJECT_CODE)));
        sb2.append(string2.substring(pMap.get(KFSPropertyConstants.EMPLID), pMap.get(KFSPropertyConstants.EARN_CODE)));
        return sb1.toString().compareTo(sb2.toString());
    }
}
