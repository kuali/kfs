/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.FeeTypeCode;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

public class FeeTypeCodeLookupableHelperService extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
            // build search criteria based on information entered and store in this variable

        List searchResults = new ArrayList();
        
        List<FeeTypeCode> feeTypeCodes = (List<FeeTypeCode>) super.getSearchResults(fieldValues);

        for (FeeTypeCode feeTypeCode : feeTypeCodes) {
            if (!feeTypeCode.getCode().equalsIgnoreCase(EndowConstants.FeeType.FEE_TYPE_CODE_FOR_PAYMENTS)) {
                searchResults.add(feeTypeCode);
            }
        }
        
        return searchResults;
    }

}
