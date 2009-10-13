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
