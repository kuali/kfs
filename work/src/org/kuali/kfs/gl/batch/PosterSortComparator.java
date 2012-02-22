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

import org.kuali.kfs.gl.businessobject.OriginEntryFieldUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;

public class PosterSortComparator implements Comparator<String> {
    OriginEntryFieldUtil oefu = new OriginEntryFieldUtil();
    Map<String, Integer> pMap = oefu.getFieldBeginningPositionMap();

    private class Range {
        public Range( int start, int end ) { this.start = start; this.end = end; }
        public int start;
        public int end;
    }

    Range[] compareRanges;
    {
        compareRanges = new Range[2];
        compareRanges[0] = new Range(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR),             pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER));
        compareRanges[1] = new Range(pMap.get(KFSPropertyConstants.PROJECT_CODE),                       pMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE));
    }

    public int compare(String string1, String string2) {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(string1.substring(compareRanges[0].start,compareRanges[0].end));
        sb1.append(string1.substring(compareRanges[1].start,compareRanges[1].end));

        StringBuilder sb2 = new StringBuilder();
        sb2.append(string2.substring(compareRanges[0].start,compareRanges[0].end));
        sb2.append(string2.substring(compareRanges[1].start,compareRanges[1].end));

        return sb1.toString().compareTo(sb2.toString());
    }
}
