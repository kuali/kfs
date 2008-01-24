/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.util.testobject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;

public class PendingLedgerEntryForTesting extends LaborLedgerPendingEntry {

    @Override
    public boolean equals(Object otherPendingLedgerEntry) {
        return ObjectUtil.compareObject(this, otherPendingLedgerEntry, getPrimaryKeyList());
    }

    public Map getPrimaryKeyMap() {
        return ObjectUtil.buildPropertyMap(this, this.getPrimaryKeyList());
    }

    public List<String> getPrimaryKeyList() {
        List<String> primaryKeyList = new ArrayList<String>();
        primaryKeyList.add(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
        primaryKeyList.add(KFSPropertyConstants.DOCUMENT_NUMBER);
        primaryKeyList.add(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
        return primaryKeyList;
    }
}
