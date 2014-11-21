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
