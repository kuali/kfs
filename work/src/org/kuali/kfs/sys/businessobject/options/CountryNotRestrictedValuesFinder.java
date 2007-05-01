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
package org.kuali.kfs.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.util.SpringServiceLocator;

/**
 * This class returns list of country value pairs.
 * 
 * 
 */
public class CountryNotRestrictedValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        
        Map criteria = new HashMap();
        
        List<String> criteriaValues = new ArrayList<String>();
        criteriaValues.add(null);
        criteriaValues.add("N");
        
        criteria.put("postalCountryRestrictedIndicator", criteriaValues);
       	List boList = (List) SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(Country.class,criteria,"postalCountryName", true);
        List keyValues = new ArrayList();

        Country usCountry = null;
        for (Iterator iter = boList.iterator(); iter.hasNext();) {
            Country element = (Country) iter.next();
            if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(element.getPostalCountryCode())) {
                usCountry = element;
            }
            else {
                keyValues.add(new KeyLabelPair(element.getPostalCountryCode(), element.getPostalCountryName()));
            }
        }

        List keyValueUSFirst = new ArrayList();
        keyValueUSFirst.add(new KeyLabelPair("", ""));
        keyValueUSFirst.add(new KeyLabelPair(usCountry.getPostalCountryCode(), usCountry.getPostalCountryName()));
        keyValueUSFirst.addAll(keyValues);

        return keyValueUSFirst;
    }

}