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
package org.kuali.module.financial.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.PropertyConstants;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.bo.PaymentReasonCode;

/**
 * This class returns list of payment reason value pairs.
 * 
 * 
 */
public class PaymentReasonValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List boList = (List) SpringServiceLocator.getKeyValuesService().findAllOrderBy(PaymentReasonCode.class, PropertyConstants.NAME, true);
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair("", ""));
        for (Iterator iter = boList.iterator(); iter.hasNext();) {
            PaymentReasonCode element = (PaymentReasonCode) iter.next();
            keyValues.add(new KeyLabelPair(element.getCode(), element.getCode() + " - " + element.getName()));
        }

        return keyValues;
    }

}
