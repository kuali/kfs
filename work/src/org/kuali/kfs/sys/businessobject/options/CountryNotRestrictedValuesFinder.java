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
package org.kuali.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.Country;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.CountryService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants.FINANCIAL_SYSTEM_ALL;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This class returns list of country value pairs.
 */
public class CountryNotRestrictedValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<Country> boList = SpringContext.getBean(CountryService.class).findAllCountriesNotRestricted();
        List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();

        Country defaultCountry = null;
        for (Country element : boList) {
            String defaultCountryCode = SpringContext.getBean(ParameterService.class).getParameterValue(FINANCIAL_SYSTEM_ALL.class, KFSConstants.CoreApcParms.DEFAULT_COUNTRY);
            
            // Find default country code and pull it out so we can set it first in the results list later.
            if (StringUtils.equals(defaultCountryCode, element.getPostalCountryCode())) {
                defaultCountry = element;
            }
            else {
                if(element.isActive()) {
                    keyValues.add(new KeyLabelPair(element.getPostalCountryCode(), element.getPostalCountryName()));
                }
            }
        }

        List<KeyLabelPair> keyValueUSFirst = new ArrayList<KeyLabelPair>();
        keyValueUSFirst.add(new KeyLabelPair("", ""));
        keyValueUSFirst.add(new KeyLabelPair(defaultCountry.getPostalCountryCode(), defaultCountry.getPostalCountryName()));
        keyValueUSFirst.addAll(keyValues);

        return keyValueUSFirst;
    }

}
