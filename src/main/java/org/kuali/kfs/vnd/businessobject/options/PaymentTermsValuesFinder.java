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
package org.kuali.kfs.vnd.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.PaymentTermType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Values finder for <code>PaymentTermType</code>. Similar to <code>PaymentTypeValuesFinder</code>, except that the list of
 * <code>KeyValue</code>s returned by this class' <code>getKeyValues()</code> method are a code and a description, rather
 * than a description and a description. That method's signature needs to stay the same to satisfy the core code that uses values
 * finders, so we can't simply employ polymorphism in method signatures; we'll use a separate class instead.
 * 
 * @see org.kuali.kfs.vnd.businessobject.PaymentTermType
 * @see org.kuali.kfs.vnd.businessobject.options.PaymentTypeValuesFinder
 */
public class PaymentTermsValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection codes = boService.findAll(PaymentTermType.class);
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue("", ""));
        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            PaymentTermType pt = (PaymentTermType) iter.next();
            labels.add(new ConcreteKeyValue(pt.getVendorPaymentTermsCode(), pt.getVendorPaymentTermsDescription()));
        }

        return labels;
    }

}
