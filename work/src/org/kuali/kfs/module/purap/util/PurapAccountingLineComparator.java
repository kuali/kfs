/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.util;

import java.util.Comparator;

import org.kuali.kfs.sys.businessobject.AccountingLine;


public class PurapAccountingLineComparator implements Comparator<AccountingLine> {
    /**
     * Compares two accounting lines based on their account number and object code, in ascending order.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(AccountingLine sal1, AccountingLine sal2) {
        int compare = 0;
        if (sal1 != null && sal2 != null) {
            if (sal1.getAccountNumber() != null && sal2.getAccountNumber() != null) {                        
                compare = sal1.getAccountNumber().compareTo(sal2.getAccountNumber());    
                if (compare == 0) {
                    if (sal1.getFinancialObjectCode() != null && sal2.getFinancialObjectCode() != null)
                        compare =  sal1.getFinancialObjectCode().compareTo(sal2.getFinancialObjectCode());
                }
            }
        }
        return compare;
    }
}
