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

import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;

/**
 * This class...
 */
public class LaborOriginEntryForTesting extends LaborOriginEntry {
    private List<String> keyValueList;

    @Override
    public boolean equals(Object otherOriginEntry) {
        return ObjectUtil.equals(this, otherOriginEntry, this.getKeyValueList());
    }

    public Map getKeyValueMap() {
        return ObjectUtil.buildPropertyMap(this, this.getKeyValueList());
    }

    public List<String> getDefaultKeyValueList() {
        List<String> keyValueList = new ArrayList<String>();
        keyValueList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keyValueList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keyValueList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keyValueList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        keyValueList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        keyValueList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        keyValueList.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        keyValueList.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        keyValueList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        keyValueList.add(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        keyValueList.add(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
        keyValueList.add(KFSPropertyConstants.DOCUMENT_NUMBER);
        keyValueList.add(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);

        keyValueList.add(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        keyValueList.add(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
        keyValueList.add(KFSPropertyConstants.POSITION_NUMBER);
        keyValueList.add(KFSPropertyConstants.EMPLID);
        return keyValueList;
    }

    /**
     * Gets the keyValueList attribute.
     * 
     * @return Returns the keyValueList.
     */
    public List<String> getKeyValueList() {
        if (keyValueList == null) {
            keyValueList = this.getDefaultKeyValueList();
        }
        return keyValueList;
    }

    /**
     * Sets the keyValueList attribute value.
     * 
     * @param keyValueList The keyValueList to set.
     */
    public void setKeyValueList(List<String> keyValueList) {
        this.keyValueList = keyValueList;
    }
}
