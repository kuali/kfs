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
package org.kuali.module.cams.document.authorization;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;

/**
 * AssetAuthorizer for Asset edit.
 */
public class AssetAuthorizer extends MaintenanceDocumentAuthorizerBase {

   // private static final Logger LOG = Logger.getLogger(AssetEditAuthorizer.class);

    /**
     * Returns the set of authorization restrictions (if any) that apply to this Asset in this context.
     * 
     * @param document
     * @param user
     * @return a new set of {@link MaintenanceDocumentAuthorizations} that marks certain fields as necessary
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {

        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();

        // do addHiddenAuthField
        Asset asset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        //TODO: we can break this nicely to three or more method calls  based on different workgroups, instad of doing it in one method: 
        setAssetFieldsAuthorization(asset, auths, user);
        return auths;
    }
    
    /**
     * Sets the asset fields to be read only if the current user is not a member of
     * kuali-Assetmaintainers workgroup.
     * 
     * @param asset an instance of Asset document
     * @param auths an instance of MaintenanceDocumentAuthorizations which is used to define the read only fields
     * @param user current logged-in user
     */
    private void setAssetFieldsAuthorization(Asset asset, MaintenanceDocumentAuthorizations auths, UniversalUser user) {
       // String camsAdministrotorsWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.CAPITAL_ASSETS_DOCUMENT.class, CamsConstants.Workgroups.WORKGROUP_CM_ADMINISTRATORS);
        if (user.isMember(CamsConstants.Workgroups.WORKGROUP_CM_ADMINISTRATORS)) {
            //Just an Example for proof of concept
            auths.addReadonlyAuthField("conditionCode");
        }
    }
    
}
