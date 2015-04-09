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
