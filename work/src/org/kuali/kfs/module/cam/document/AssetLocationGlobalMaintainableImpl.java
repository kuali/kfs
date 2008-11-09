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
package org.kuali.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetLocationGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetLocationGlobalDetail;
import org.kuali.kfs.module.cam.document.workflow.RoutingAssetNumber;
import org.kuali.kfs.module.cam.document.workflow.RoutingAssetTagNumber;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.routing.attribute.KualiAccountAttribute;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location assets
 */
public class AssetLocationGlobalMaintainableImpl extends KualiGlobalMaintainableImpl implements GenericRoutingInfo {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLocationGlobalMaintainableImpl.class);

    private Set<RoutingData> routingInfo;
    
    /**
     * Populates any empty fields from Asset primary key
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#addNewLineToCollection(java.lang.String)
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
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
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
    /**
     * Gets the routingInfo attribute.
     * 
     * @return Returns the routingInfo.
     */
    public Set<RoutingData> getRoutingInfo() {
        return routingInfo;
    }

    /**
     * Sets the routingInfo attribute value.
     * 
     * @param routingInfo The routingInfo to set.
     */
    public void setRoutingInfo(Set<RoutingData> routingInfo) {
        this.routingInfo = routingInfo;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {        
        routingInfo = new HashSet<RoutingData>();
        Set<OrgReviewRoutingData> organizationRoutingSet = new HashSet<OrgReviewRoutingData>();
        Set<RoutingAccount> accountRoutingSet = new HashSet<RoutingAccount>();
        Set<RoutingAssetNumber> assetNumberRoutingSet = new HashSet<RoutingAssetNumber>();
        Set<RoutingAssetTagNumber> assetTagNumberRoutingSet = new HashSet<RoutingAssetTagNumber>();

        
        String chartOfAccountsCode;
        String accountNumber;
        String organizationcode;
        Long assetNumber;
        String assetTagNumber;
        
        AssetLocationGlobal assetLocationGlobal = (AssetLocationGlobal) getBusinessObject();

        for (AssetLocationGlobalDetail detailLine : assetLocationGlobal.getAssetLocationGlobalDetails()) {
            chartOfAccountsCode = detailLine.getAsset().getOrganizationOwnerChartOfAccountsCode();
            accountNumber = detailLine.getAsset().getOrganizationOwnerAccountNumber();
            organizationcode = detailLine.getAsset().getOrganizationOwnerAccount().getOrganizationCode();
            assetNumber = detailLine.getAsset().getCapitalAssetNumber();
            assetTagNumber = detailLine.getAsset().getCampusTagNumber();

            organizationRoutingSet.add(new OrgReviewRoutingData(chartOfAccountsCode, organizationcode));
            accountRoutingSet.add(new RoutingAccount(chartOfAccountsCode, accountNumber));
            assetNumberRoutingSet.add(new RoutingAssetNumber(assetNumber.toString()));
            assetTagNumberRoutingSet.add(new RoutingAssetTagNumber(assetTagNumber));
            
        }
                            
        //Storing data
        RoutingData organizationRoutingData = new RoutingData();
        organizationRoutingData.setRoutingType(KualiOrgReviewAttribute.class.getSimpleName());
        organizationRoutingData.setRoutingSet(organizationRoutingSet);
        routingInfo.add(organizationRoutingData);
        
        RoutingData accountRoutingData = new RoutingData();
        accountRoutingData.setRoutingType(KualiAccountAttribute.class.getSimpleName());
        accountRoutingData.setRoutingSet(accountRoutingSet);
        routingInfo.add(accountRoutingData);
        
        RoutingData assetNumberRoutingData = new RoutingData();
        assetNumberRoutingData.setRoutingType(RoutingAssetNumber.class.getSimpleName());
        assetNumberRoutingData.setRoutingSet(assetNumberRoutingSet);
        routingInfo.add(assetNumberRoutingData);
        
        RoutingData assetTagNumberRoutingData = new RoutingData();
        assetTagNumberRoutingData.setRoutingType(RoutingAssetTagNumber.class.getSimpleName());
        assetTagNumberRoutingData.setRoutingSet(assetTagNumberRoutingSet);
        routingInfo.add(assetTagNumberRoutingData);
        
    }

    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return Asset.class;
    }
}
