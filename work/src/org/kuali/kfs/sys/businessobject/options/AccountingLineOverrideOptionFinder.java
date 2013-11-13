/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class AccountingLineOverrideOptionFinder extends KeyValuesBase implements ValueFinder {

    protected static List<KeyValue> LABELS = new ArrayList<KeyValue>();
    static {
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.BLANK, ""));
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.NONE, "NONE"));
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.EXPIRED_ACCOUNT, "EXP_ACCT"));
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.NON_BUDGETED_OBJECT, "NON_BDG_OBJ"));
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.TRANSACTION_EXCEEDS_REMAINING_BUDGET, "TRAN_EXCD_REM_BDG"));
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT, "EXP_ACCT_NON_BDG_OBJ"));
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.NON_BUDGETED_OBJECT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET, "NON_BDG_OBJ_TRAN_EXCD_REM_BDG"));
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET, "EXP_ACCT_TRAN_EXCD_REM_BDG"));
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT_AND_TRANSACTION_EXCEEDS_REMAINING_BUDGET, "EXP_ACCT_NON_BDG_OBJ_EXCD_BDG"));
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.NON_FRINGE_ACCOUNT_USED, "NON_FR_ACCT"));
        LABELS.add(new ConcreteKeyValue(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED, "EXP_ACCT_NON_FR_ACCT"));
        LABELS = Collections.unmodifiableList(LABELS);
    }

//    public static String getLabelFromComponent( Integer component ) {
//        return AccountingLineOverride.valueOf( Collections.singleton(component) ).getCode();
//    }

    @Override
    public List<KeyValue> getKeyValues() {
        return LABELS;
    }

    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        return AccountingLineOverride.CODE.EXPIRED_ACCOUNT;
    }
}
