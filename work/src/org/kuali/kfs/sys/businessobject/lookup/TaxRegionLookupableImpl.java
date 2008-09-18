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

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.TaxRegionType;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;

public class TaxRegionLookupableImpl extends KualiLookupableImpl {
    
    /**
     * Make create new action go to Tax Region Type lookup first
     * 
     * @see org.kuali.core.lookup.KualiLookupableImpl#getCreateNewUrl()
     */
    @Override
    public String getCreateNewUrl() {

        String url = "";

        if (getLookupableHelperService().allowsMaintenanceNewOrCopyAction()) {
            Properties parameters = new Properties();
            parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.MAINTENANCE_NEW_METHOD_TO_CALL);
            parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TaxRegionType.class.getName());
            parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, "portal.do");
            parameters.put(KNSConstants.DOC_FORM_KEY, "88888888");
            parameters.put(KNSConstants.CONVERSION_FIELDS_PARAMETER, "taxRegionTypeCode:taxRegionTypeCode");
            url = UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, parameters);
            url = "<a href=\"" + url + "\"><img src=\"images/tinybutton-createnew.gif\" alt=\"create new\" width=\"70\" height=\"15\"/></a>";
        }

        return url;        
    }
    
    

}
