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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.krad.bo.BusinessObject;

public class AssetLocationGlobalPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLocationGlobalPresentationController.class);

    @Override
    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenPropertyNames(businessObject);

        // only include campitalAssetNumber in the add section within the asset location details collection
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetLocationGlobal.ASSET_LOCATION_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetLocationGlobal.CAMPUS_CODE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetLocationGlobal.ASSET_LOCATION_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetLocationGlobal.BUILDING_CODE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetLocationGlobal.ASSET_LOCATION_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetLocationGlobal.BUILDING_ROOM_NUMBER);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetLocationGlobal.ASSET_LOCATION_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetLocationGlobal.BUILDING_SUB_ROOM_NUMBER);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetLocationGlobal.ASSET_LOCATION_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetLocationGlobal.CAMPUS_TAG_NUMBER);

        return fields;
    }

}
