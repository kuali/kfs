/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.util;

import java.util.Comparator;

import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;

public class SourceAccountingLineComparator implements Comparator<TemSourceAccountingLine> {

    @Override
    public int compare(TemSourceAccountingLine line1, TemSourceAccountingLine line2) {
        int c;
        c = line1.getChartOfAccountsCode().compareTo(line2.getChartOfAccountsCode());
        if (c == 0){
            c = line1.getAccountNumber().compareTo(line2.getAccountNumber());
        }
        if (c == 0){
            c = line1.getFinancialObjectCode().compareTo(line2.getFinancialObjectCode());
        }
        if (c == 0){
            c = line1.getCardType().compareTo(line2.getCardType());
        }
        if (c == 0){
            c = line2.getAmount().compareTo(line1.getAmount()) ;
        }
        return c;
    }


}
