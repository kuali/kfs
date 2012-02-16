/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.Properties;

import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;

public class TravelRelocationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl{
    @Override
    public String getSupplementalMenuBar() {
        String menuBar = super.getSupplementalMenuBar();
        
        Properties createParameters = new Properties();
        createParameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
        createParameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TravelRelocationDocument.class.getName());
        
        String createUrl = UrlFactory.parameterizeUrl(KNSConstants.MAINTENANCE_ACTION, createParameters);
        
        menuBar = "<a href=\"" + createUrl + "\"><img src=\"images/tinybutton-createnew.gif\" alt=\"refresh\"></a>&nbsp;" + menuBar;        

        return menuBar;
    }

}
