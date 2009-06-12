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
package org.kuali.kfs.fp.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CreditCardType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This class...
 */
public class CreditCardTypeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        // get a list of all CreditCardTypes
        List<CreditCardType> codes = (List<CreditCardType>) SpringContext.getBean(KeyValuesService.class).findAll(CreditCardType.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( codes == null ) {
            codes = new ArrayList<CreditCardType>(0);
        } else {
            codes = new ArrayList<CreditCardType>( codes );
        }

        // sort using comparator
        Collections.sort(codes, new CreditCardTypeComparator());

        // create a new list (code, name)
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        labels.add(new KeyLabelPair("", "")); // blank first entry

        for (CreditCardType creditCardType : codes) {
            if(creditCardType.isActive()) {
                labels.add(new KeyLabelPair(creditCardType.getFinancialDocumentCreditCardTypeCode(), creditCardType.getFinancialDocumentCreditCardTypeCode() + "-" + creditCardType.getFinancialDocumentCreditCardCompanyName()));
            }
        }

        return labels;
    }

}
