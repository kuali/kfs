/*
 * Copyright 2006 The Kuali Foundation.
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.financial.bo.CreditCardType;

/**
 * This class...
 * 
 * 
 */
public class CreditCardTypeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        // get a list of all CreditCardTypes
        KeyValuesService boService = SpringServiceLocator.getKeyValuesService();
        List codes = (List) boService.findAll(CreditCardType.class);


        // get comparator
        CreditCardTypeComparator creditCardTypeComparator = new CreditCardTypeComparator();

        // sort using comparator
        Collections.sort(codes, creditCardTypeComparator);

        // create a new list (code, name)
        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", "")); // blank first entry

        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            CreditCardType creditCardType = (CreditCardType) iter.next();
            labels.add(new KeyLabelPair(creditCardType.getFinancialDocumentCreditCardTypeCode(), creditCardType.getFinancialDocumentCreditCardTypeCode() + "-" + creditCardType.getFinancialDocumentCreditCardCompanyName()));
        }

        return labels;
    }

}