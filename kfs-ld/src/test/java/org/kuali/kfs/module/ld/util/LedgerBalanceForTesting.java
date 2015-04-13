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
package org.kuali.kfs.module.ld.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;

public class LedgerBalanceForTesting extends LedgerBalance {

    @Override
    public boolean equals(Object otherLedgerBalance) {
        return ObjectUtil.equals(this, otherLedgerBalance, getPrimaryKeyList());
    }

    public Map getPrimaryKeyMap() {
        return ObjectUtil.buildPropertyMap(this, this.getPrimaryKeyList());
    }

    public static List<String> getPrimaryKeyList() {
        List<String> primaryKeyList = new ArrayList<String>();
        primaryKeyList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        primaryKeyList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        primaryKeyList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        primaryKeyList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        primaryKeyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        primaryKeyList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        primaryKeyList.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        primaryKeyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        primaryKeyList.add(KFSPropertyConstants.POSITION_NUMBER);
        primaryKeyList.add(KFSPropertyConstants.EMPLID);
        return primaryKeyList;
    }
}
