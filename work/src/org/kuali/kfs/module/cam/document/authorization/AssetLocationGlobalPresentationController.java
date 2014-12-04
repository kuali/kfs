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
