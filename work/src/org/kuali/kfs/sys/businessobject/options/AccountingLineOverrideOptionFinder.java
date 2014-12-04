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
