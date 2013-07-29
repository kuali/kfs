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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;


public class AccountingDistributionComparator implements Comparator<AccountingDistribution> {

    @Override
    public int compare(AccountingDistribution accountingDistribution1, AccountingDistribution accountingDistribution2) {
        if (StringUtils.isBlank(accountingDistribution1.getObjectCode())) {
            if (StringUtils.isBlank(accountingDistribution2.getObjectCode())) {
                return 0; // they're both effectively null, so they're equal
            }
            return 1; // dee's still empty, it should go to the top
        }
        if (StringUtils.isBlank(accountingDistribution2.getObjectCode())) {
            return -1; // dum's empty; it should go to the top
        }
        return accountingDistribution1.getObjectCode().compareTo(accountingDistribution2.getObjectCode());
    }

}
