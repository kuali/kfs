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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * AssetGlobalAuthorizer for Asset Global
 */
public class AssetGlobalAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase#populateRoleQualification(org.kuali.rice.kns.document.Document,
     *      java.util.Map)
     */
    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);

        AssetGlobal assetGlobal = null;
        if (businessObject instanceof MaintenanceDocument) {
            assetGlobal = (AssetGlobal) ((MaintenanceDocument) businessObject).getNewMaintainableObject().getBusinessObject();
        }
        else if (businessObject instanceof AssetGlobal) {
            assetGlobal = (AssetGlobal) businessObject;
        }
        else {
            return;
        }

        AssetGlobalService assetGlobalService = SpringContext.getBean(AssetGlobalService.class);
        if (assetGlobalService.isAssetSeparate(assetGlobal)) {
            Asset spearateAsset = assetGlobal.getSeparateSourceCapitalAsset();

            String chart = spearateAsset.getOrganizationOwnerChartOfAccountsCode();
            String org = spearateAsset.getOrganizationOwnerAccount().getOrganizationCode();

            attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chart);
            attributes.put(KfsKimAttributes.ORGANIZATION_CODE, org);
        }
    }
}
