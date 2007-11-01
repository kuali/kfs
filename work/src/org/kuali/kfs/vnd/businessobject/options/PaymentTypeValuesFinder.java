/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.vendor.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.vendor.VendorParameterConstants;
import org.kuali.module.vendor.bo.PaymentTermType;

/**
 * Values finder for <code>PaymentTermsType</code>. Similar to <code>PaymentTermsValuesFinder</code>, except that the list of
 * <code>KeyValuePair</code>s returned by this class' <code>getKeyValues()</code> method are a description and a description,
 * rather than a code and a description. That method's signature needs to stay the same to satisfy the core code that uses values
 * finders, so we can't simply employ polymorphism in method signatures; we'll use a separate class instead.
 * 
 * @see org.kuali.module.vendor.bo.PaymentTermType
 * @see org.kuali.module.vendor.lookup.keyvalues.PaymentTermsValuesFinder
 */
public class PaymentTypeValuesFinder extends KeyValuesBase implements ValueFinder {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<String> descValues = SpringContext.getBean(ParameterService.class).getParameterValues(PaymentTermType.class, VendorParameterConstants.PAYMENT_TERMS_DUE_TYPE_DESC);
        List keyValues = new ArrayList();
        for (String desc : descValues) {
            keyValues.add(new KeyLabelPair(desc, desc));
        }

        return keyValues;
    }

    /**
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {

        return "";
    }

}