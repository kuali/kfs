/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * AssetAuthorizer for Asset edit.
 */
public class AssetGlobalAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    /**
     * 
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentAuthorizerBase#addRoleQualification(org.kuali.rice.krad.bo.BusinessObject, java.util.Map)
     */
    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
                
        //This was added so when the asset lookup page is requested, then it will check for role qualifiers and validate whether the user has access to
        // to some asset functions like "Separate". Take a look at AssetLookupableHelperServiceImpl.getSeparateUrl(Asset asset) for more info.        
        if (businessObject instanceof Asset) {
            Asset asset = (Asset) businessObject;
            attributes.put(CamsPropertyConstants.Asset.CAMPUS_CODE,asset.getCampusCode());
        }                
    }
}
