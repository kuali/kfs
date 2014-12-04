/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
