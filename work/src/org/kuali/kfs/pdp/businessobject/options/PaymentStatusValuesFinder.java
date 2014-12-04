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
package org.kuali.kfs.pdp.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

public class PaymentStatusValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List<KeyValue> getKeyValues() {
        List<PaymentStatus> boList = (List) SpringContext.getBean(KeyValuesService.class).findAll(PaymentStatus.class);
        List<KeyValue> keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue("", ""));
        for (PaymentStatus element : boList) {
            keyValues.add(new ConcreteKeyValue(element.getCode(), element.getCode()));
        }
        keyValues.add(new ConcreteKeyValue(PdpConstants.PaymentStatusCodes.HELD_TAX_ALL, PdpConstants.PaymentStatusCodes.HELD_TAX_ALL));

        return keyValues;
    }

}
