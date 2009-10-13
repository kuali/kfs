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


// This class is a comparator for sorting AccountType with Code name.
package org.kuali.kfs.coa.businessobject.options;

import java.util.Comparator;

import org.kuali.kfs.coa.businessobject.AccountType;

/**
 * This class is a comparator for Account Type Codes
 */
public class AccountTypeCodeComparator implements Comparator {

    /**
     * If these two account type codes are the same codes
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        AccountType acctType1 = (AccountType) o1;
        AccountType acctType2 = (AccountType) o2;

        int acctTypeComp = acctType1.getAccountTypeCode().compareTo(acctType2.getAccountTypeCode());

        return acctTypeComp;
    }

}
