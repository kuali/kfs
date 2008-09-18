/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionType;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.UrlFactory;

public class TaxRegionTypeLookupableServiceImpl extends KualiLookupableHelperServiceImpl {
    
    /**
     * Overrding the return URL to create a new Tax Region document
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.kns.bo.BusinessObject, java.util.Map, java.lang.String, java.util.List)
     */
    @Override
    public String getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl, List returnKeys) {
        Properties parameters = getParameters(businessObject, fieldConversions, lookupImpl, returnKeys);
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TaxRegion.class.getName());
        parameters.put(KFSConstants.OVERRIDE_KEYS, KFSConstants.TaxRegionConstants.TAX_REGION_TYPE_CODE);
        parameters.put(KFSConstants.REFRESH_CALLER, KFSConstants.TaxRegionConstants.TAX_REGION_TYPE_CODE+"::"+((TaxRegionType) businessObject).getTaxRegionTypeCode());
        return UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
    }    

}
