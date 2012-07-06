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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;


public class AccountingDistributionComparator implements Comparator<AccountingDistribution> {

    @Override
    public int compare(AccountingDistribution accountingDistribution1, AccountingDistribution accountingDistribution2) {
        // TODO Auto-generated method stub
        String name1 = accountingDistribution1.getObjectCodeName();
        String name2 = accountingDistribution2.getObjectCodeName();
        return accountingDistribution1.getObjectCodeName().compareTo(accountingDistribution2.getObjectCodeName());
    }
   
}
