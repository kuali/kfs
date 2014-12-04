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
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of payment method key value pairs.
 */
public class PaymentMethodValuesFinder extends KeyValuesBase {

    static List<KeyValue> activeLabels = new ArrayList<KeyValue>();
    static {
        activeLabels.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        activeLabels.add(new ConcreteKeyValue(KFSConstants.PaymentMethod.ACH_CHECK.getCode(), KFSConstants.PaymentMethod.ACH_CHECK.getCodeAndName()));
        activeLabels.add(new ConcreteKeyValue(KFSConstants.PaymentMethod.FOREIGN_DRAFT.getCode(), KFSConstants.PaymentMethod.FOREIGN_DRAFT.getCodeAndName()));
        activeLabels.add(new ConcreteKeyValue(KFSConstants.PaymentMethod.WIRE_TRANSFER.getCode(), KFSConstants.PaymentMethod.WIRE_TRANSFER.getCodeAndName()));
    }
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        return activeLabels;
    }

}
