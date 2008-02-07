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
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlobal;
import org.kuali.module.cams.bo.AssetGlobalDetail;
import org.kuali.module.cams.lookup.valuefinder.NextAssetNumberFinder;
import org.kuali.module.pdp.batch.LoadPayments;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global assets
 */
public class AssetGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalMaintainableImpl.class);
    
    /**
     * Hook for quantity and setting asset numbers.
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        
        LOG.info("LEO collectionName = " + collectionName);
        
        AssetGlobalDetail addAssetLine = (AssetGlobalDetail) newCollectionLines.get(collectionName);
        
        LOG.info("LEO locQuantity = " + addAssetLine.getLocationQuantity());
        if (addAssetLine.getLocationQuantity() >= 1) {
            
            for (int i = 0; i < addAssetLine.getLocationQuantity(); i++) {
              
                LOG.info("LEO getBuildingCode = " + addAssetLine.getBuildingCode());
                addAssetLine.setCampusCode(addAssetLine.getBuildingCode());
                
                LOG.info("LEO getBuildingRoomNumber = " + addAssetLine.getBuildingRoomNumber());
                addAssetLine.setBuildingRoomNumber(addAssetLine.getBuildingRoomNumber());

                LOG.info("LEO getBuildingSubRoomNumber = " + addAssetLine.getBuildingSubRoomNumber());
                addAssetLine.setBuildingSubRoomNumber(addAssetLine.getBuildingSubRoomNumber());
                
                // get asset number
                Long assetNumber = NextAssetNumberFinder.getLongValue();
                LOG.info("LEO assetNumber = " + assetNumber);
                addAssetLine.setCapitalAssetNumber(assetNumber);
              
                // add new data to collection
                super.addNewLineToCollection(collectionName);
            }
        }
        
        //Long assetNumber = NextAssetNumberFinder.getLongValue();
        //LOG.info("LEO assetNumber = " + assetNumber);
        //addAssetLine.setCapitalAssetNumber(assetNumber);
        
        //super.addNewLineToCollection(collectionName);
    }

    /**
     * This creates the particular locking representation for this global document.
     * 
     * @see org.kuali.core.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        List<MaintenanceLock> maintenanceLocks = new ArrayList();

        for (AssetGlobalDetail detail : assetGlobal.getAssetGlobalDetails()) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockRep = new StringBuffer();

            lockRep.append(Asset.class.getName());
            lockRep.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockRep.append("documentNumber");
            lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRep.append(detail.getDocumentNumber());
            lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
            lockRep.append("capitalAssetNumber");
            lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRep.append(detail.getCapitalAssetNumber());

            maintenanceLock.setDocumentNumber(assetGlobal.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockRep.toString());
            maintenanceLocks.add(maintenanceLock);
        }
        return maintenanceLocks;
    }
}
