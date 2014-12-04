/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
