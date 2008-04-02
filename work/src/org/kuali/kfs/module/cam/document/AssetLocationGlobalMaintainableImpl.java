/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cams.maintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlobalDetail;
import org.kuali.module.cams.bo.AssetLocationGlobal;
import org.kuali.module.cams.bo.AssetLocationGlobalDetail;
import org.kuali.module.cams.lookup.valuefinder.NextAssetNumberFinder;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location assets
 */
public class AssetLocationGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLocationGlobalMaintainableImpl.class);

    /**
     * Populates any empty fields from Asset primary key
     * 
     * @see org.kuali.core.maintenance.Maintainable#addNewLineToCollection(java.lang.String)
     */
    
    @Override
    public void addNewLineToCollection(String collectionName) {
  
        // get AssetLocationGlobalDetail List from AssetLocationGlobal
        AssetLocationGlobalDetail addAssetLine = (AssetLocationGlobalDetail) newCollectionLines.get(collectionName);
        
        // validate and place PK into Map
        HashMap map = new HashMap();
        map.put("capitalAssetNumber", addAssetLine.getCapitalAssetNumber());
        
        // retrieve Asset object by PK
        Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, map);
        
        if (ObjectUtils.isNotNull(asset) && ObjectUtils.isNotNull(asset.getCapitalAssetNumber())) { 
            if (StringUtils.isBlank(addAssetLine.getCampusCode())) {
                addAssetLine.setCampusCode(asset.getCampusCode());
            }
            if (StringUtils.isBlank(addAssetLine.getBuildingCode())) {
                addAssetLine.setBuildingCode(asset.getBuildingCode());
            }
            if (StringUtils.isBlank(addAssetLine.getBuildingRoomNumber())) {
                addAssetLine.setBuildingRoomNumber(asset.getBuildingRoomNumber());
            }
            if (StringUtils.isBlank(addAssetLine.getBuildingSubRoomNumber())) {
                addAssetLine.setBuildingSubRoomNumber(asset.getBuildingSubRoomNumber());
            }
            if(StringUtils.isBlank(addAssetLine.getCampusTagNumber())) { 
                addAssetLine.setCampusTagNumber(asset.getCampusTagNumber());
            }
            addAssetLine.setNewCollectionRecord(true);
            //Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(getBusinessObject(), collectionName);
            //maintCollection.add(addAssetLine);
        }
        super.addNewLineToCollection(collectionName);
    }
    
    /**
     * This creates the particular locking representation for this global location document.
     * 
     * @see org.kuali.core.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        AssetLocationGlobal assetLocationGlobal = (AssetLocationGlobal) getBusinessObject();
        List<MaintenanceLock> maintenanceLocks = new ArrayList();

        for (AssetLocationGlobalDetail detail : assetLocationGlobal.getAssetLocationGlobalDetails()) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockrep = new StringBuffer();

            lockrep.append(Asset.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockrep.append("capitalAssetNumber" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockrep.append(detail.getCapitalAssetNumber());

            maintenanceLock.setDocumentNumber(assetLocationGlobal.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockrep.toString());
            maintenanceLocks.add(maintenanceLock);
        }
        return maintenanceLocks;
    }
}
