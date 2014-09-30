/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetLocationGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetLocationGlobalDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location
 * assets
 */
public class AssetLocationGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLocationGlobalMaintainableImpl.class);

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
        map.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, addAssetLine.getCapitalAssetNumber());

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
            if (StringUtils.isBlank(addAssetLine.getCampusTagNumber())) {
                addAssetLine.setCampusTagNumber(asset.getCampusTagNumber());
            }
            addAssetLine.setNewCollectionRecord(true);
        }
        super.addNewLineToCollection(collectionName);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
        WorkflowDocument workflowDoc = documentHeader.getWorkflowDocument();
        // release the lock when document status changed as following...
        if (workflowDoc.isCanceled() || workflowDoc.isDisapproved() || workflowDoc.isProcessed() || workflowDoc.isFinal()) {
            this.getCapitalAssetManagementModuleService().deleteAssetLocks(getDocumentNumber(), null);
        }
    }

    /**
     * We are using a substitute mechanism for asset locking which can lock on assets when rule check passed. Return empty list from
     * this method.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        return new ArrayList<MaintenanceLock>();
    }

    @Override
    public Map<String, String> populateNewCollectionLines( Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall ) {
        String capitalAssetNumber = (String) fieldValues.get(CamsPropertyConstants.AssetLocationGlobalDetail.CAPITAL_ASSET_NUMBER);

        if (StringUtils.isNotBlank(capitalAssetNumber)) {
            fieldValues.remove(CamsPropertyConstants.AssetLocationGlobalDetail.CAPITAL_ASSET_NUMBER);
            fieldValues.put(CamsPropertyConstants.AssetLocationGlobalDetail.CAPITAL_ASSET_NUMBER, capitalAssetNumber.trim());
        }
        return super.populateNewCollectionLines(fieldValues, maintenanceDocument, methodToCall);
    }

    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }


    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return Asset.class;
    }

    /**
     * Verify multiple value lookup entries are authorized by user to add
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addMultipleValueLookupResults(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, java.util.Collection, boolean, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName, Collection<PersistableBusinessObject> rawValues, boolean needsBlank, PersistableBusinessObject bo) {
        AssetLocationGlobal assetLocationGlobal = (AssetLocationGlobal) document.getDocumentBusinessObject();
        Collection<PersistableBusinessObject> allowedAssetsCollection = new ArrayList<PersistableBusinessObject>();
        final String maintDocTypeName =  CamsConstants.DocumentTypeName.ASSET_EDIT;
        GlobalVariables.getMessageMap().clearErrorMessages();
        for (PersistableBusinessObject businessObject : rawValues) {
            Asset asset = (Asset) businessObject;
            if (StringUtils.isNotBlank(maintDocTypeName)) {
                boolean allowsEdit = getBusinessObjectAuthorizationService().canMaintain(asset, GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
                if (allowsEdit) {
                    allowedAssetsCollection.add(asset);
                } else {
                    GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetLocationGlobal.SECTION_ID_EDIT_LIST_OF_ASSETS, CamsKeyConstants.AssetLocationGlobal.ERROR_ASSET_AUTHORIZATION, new String[] { GlobalVariables.getUserSession().getPerson().getPrincipalName(), asset.getCapitalAssetNumber().toString() });
                }
            }
        }     
        super.addMultipleValueLookupResults(document, collectionName, allowedAssetsCollection, needsBlank, bo);
    }
}
