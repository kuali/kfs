/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.fp.businessobject.options;

import java.util.Comparator;

import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;

public class CapitalAccountingLinesComparator implements Comparator {

    public CapitalAccountingLinesComparator() {
    }

    public int compare(Object c1, Object c2) {

        CapitalAccountingLines capitalAccountingLines1 = (CapitalAccountingLines) c1;
        CapitalAccountingLines capitalAccountingLines2 = (CapitalAccountingLines) c2;
        
        int objectCodeComparator = capitalAccountingLines1.getFinancialObjectCode().compareTo(capitalAccountingLines2.getFinancialObjectCode());
        if (objectCodeComparator != 0) {
            return objectCodeComparator;
        }
        
        return capitalAccountingLines1.getAccountNumber().compareTo(capitalAccountingLines2.getAccountNumber());
    }
}