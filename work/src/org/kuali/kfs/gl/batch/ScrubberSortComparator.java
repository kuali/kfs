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
package org.kuali.kfs.gl.batch;

import java.util.Comparator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.OriginEntryFieldUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;

public class ScrubberSortComparator implements Comparator<String> {

    OriginEntryFieldUtil oefu = new OriginEntryFieldUtil();
    Map<String, Integer> pMap = oefu.getFieldBeginningPositionMap();
    int originEntryRecordLength = GeneralLedgerConstants.getSpaceAllOriginEntryFields().length(); 
    
    private class Range {
        public Range( int start, int end ) { this.start = start; this.end = end; }
        public int start;
        public int end;
    }
    
    Range[] compareRanges;
    {
        compareRanges = new Range[6];
        compareRanges[0] = new Range(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER));
        compareRanges[1] = new Range(pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE));
        compareRanges[2] = new Range(pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE));
        compareRanges[3] = new Range(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE), pMap.get(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD));
        compareRanges[4] = new Range(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
        compareRanges[5] = new Range(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE));
    }
    
    public int compare(String string1, String string2) {
        string1 = StringUtils.rightPad(string1, originEntryRecordLength, ' ');
        string2 = StringUtils.rightPad(string2, originEntryRecordLength, ' ');

        StringBuilder sb1 = new StringBuilder();
        sb1.append(string1.substring(compareRanges[0].start,compareRanges[0].end));
        sb1.append(string1.substring(compareRanges[1].start,compareRanges[1].end));
        sb1.append(string1.substring(compareRanges[2].start,compareRanges[2].end));
        sb1.append(string1.substring(compareRanges[3].start,compareRanges[3].end));
        sb1.append(string1.substring(compareRanges[4].start,compareRanges[4].end));
        sb1.append(string1.substring(compareRanges[5].start,compareRanges[5].end));
            
        StringBuilder sb2 = new StringBuilder();
        sb2.append(string2.substring(compareRanges[0].start,compareRanges[0].end));
        sb2.append(string2.substring(compareRanges[1].start,compareRanges[1].end));
        sb2.append(string2.substring(compareRanges[2].start,compareRanges[2].end));
        sb2.append(string2.substring(compareRanges[3].start,compareRanges[3].end));
        sb2.append(string2.substring(compareRanges[4].start,compareRanges[4].end));
        sb2.append(string2.substring(compareRanges[5].start,compareRanges[5].end));
            
        return sb1.toString().compareTo(sb2.toString());
    }
}