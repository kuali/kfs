/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.businessobject.authorization;

import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.inquiry.InquiryAuthorizerBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * AssetAuthorizer for Asset inquiry
 * @see org.kuali.kfs.module.cam.document.authorization.AssetAuthorizer
 */
public class AssetInquiryAuthorizer extends InquiryAuthorizerBase{
    /**
     * @see org.kuali.rice.kns.document.authorization.BusinessObjectAuthorizerBase#addRoleQualification(org.kuali.rice.krad.bo.BusinessObject, java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);

        Asset asset = null;
        if (businessObject instanceof MaintenanceDocument) {
            asset = (Asset) ((MaintenanceDocument) businessObject).getNewMaintainableObject().getBusinessObject();
        }
        else {
            asset = (Asset) businessObject;
        }

        String chart = asset.getOrganizationOwnerChartOfAccountsCode();
        attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chart);

        if (ObjectUtils.isNotNull(asset.getOrganizationOwnerAccount())) {
            // should only be null if AssetService.isAssetFabrication=true
            String org = asset.getOrganizationOwnerAccount().getOrganizationCode();

            attributes.put(KfsKimAttributes.ORGANIZATION_CODE, org);
        }
    }
    
    
}
