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
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorParameterConstants;
import org.kuali.kfs.vnd.businessobject.PaymentTermType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Values finder for <code>PaymentTermsType</code>. Similar to <code>PaymentTermsValuesFinder</code>, except that the list of
 * <code>KeyValue</code>s returned by this class' <code>getKeyValues()</code> method are a description and a description,
 * rather than a code and a description. That method's signature needs to stay the same to satisfy the core code that uses values
 * finders, so we can't simply employ polymorphism in method signatures; we'll use a separate class instead.
 * 
 * @see org.kuali.kfs.vnd.businessobject.PaymentTermType
 * @see org.kuali.kfs.vnd.businessobject.options.PaymentTermsValuesFinder
 */
public class PaymentTypeValuesFinder extends KeyValuesBase implements ValueFinder {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<String> descValues = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(PaymentTermType.class, VendorParameterConstants.PAYMENT_TERMS_DUE_TYPE_DESC) );
        List keyValues = new ArrayList();
        for (String desc : descValues) {
            keyValues.add(new ConcreteKeyValue(desc, desc));
        }

        return keyValues;
    }

    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {

        return "";
    }

}
