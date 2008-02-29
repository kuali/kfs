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
package org.kuali.module.cams.rules;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.Room;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.Pretag;
import org.kuali.module.cams.bo.PretagDetail;


/**
 * This class represents the business rules for the maintenance of {@link AccountGlobal} business objects
 */
public class PretagRule extends MaintenanceDocumentRuleBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PretagRule.class);
    private PersistableBusinessObject bo;
    private Pretag newPretag;

    public PretagRule() {
        super();
    }

    /**
     * This method sets the convenience objects like newPretag and oldPretag, so you have short and easy handles to the new and old
     * objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load all
     * sub-objects from the DB by their primary keys, if available.
     */
    @Override
    public void setupConvenienceObjects() {

        // setup newDelegateGlobal convenience objects, make sure all possible sub-objects are populated
        newPretag = (Pretag) super.getNewBo();
    }

    /**
     * Calls the basic rules check on document save:
     * <ul>
     * <li>{@link PretagRule#validateAllCampusBuildingRoom(Pretag)}</li>
     * </ul>
     * Does not fail on rules failure
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        validateAllCampusBuildingRoom(document, newPretag.getPretagDetails());
        return true; // always return true on save
    }

    /**
     * Calls the basic rules check on document approval:
     * <ul>
     * <li>{@link PretagRule#validateAllCampusBuildingRoom(Pretag)}</li>
     * </ul>
     * Fails on rules failure
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        return validateAllCampusBuildingRoom(document, newPretag.getPretagDetails());
    }

    /**
     * Calls the basic rules check on document routing:
     * <ul>
     * <li>{@link PretagRule#validateAllCampusBuildingRoom(OrganizationReversionGlobal)}</li>
     * </ul>
     * Fails on rules failure
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return validateAllCampusBuildingRoom(document, newPretag.getPretagDetails());
    }

    /**
     * This method loops through the list of {@link pretagDetail}s and passes them off to validateAllCampusBuildingRoom for further
     * rule analysis
     * 
     * @param document
     * @param details
     * @return true if the collection of {@link pretagDetail}s passes the sub-rules
     */
    public boolean validateAllCampusBuildingRoom(MaintenanceDocument document, List<PretagDetail> details) {
        boolean success = true;

        // check if there are any pretag details
        if (details.size() != 0) {

            // check each account
            int index = 0;
            for (PretagDetail dtl : details) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + "pretagDetails[" + index + "]";
                GlobalVariables.getErrorMap().addToErrorPath(errorPath);
                success &= validateCampusBuildingRoom(dtl);
                GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
                index++;
            }
        } else {

            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + "pretagDetail.campusTagNumber", CamsKeyConstants.ERROR_NO_DETAIL_LINE);

            success = false;
        }

        return success;
    }

    /**
     * This method calls checkCampusTagNumber whenever a new {@link PretagDetail} is added to Pretag
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.core.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.core.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        Pretag pretag = (Pretag) document.getNewMaintainableObject().getBusinessObject();
        PretagDetail detail = (PretagDetail) bo;
        boolean success = true;

        detail.setPurchaseOrderNumber(pretag.getPurchaseOrderNumber());
        detail.setLineItemNumber(pretag.getLineItemNumber());

        success &= checkTotalDetailCount(pretag);
        success &= checkCampusTagNumber(detail);
        success &= validateCampusBuildingRoom(detail);

        return success;
    }

    /**
     * This method ensures that each {@link pretagDetail} tag number does not exist in Asset table
     * 
     * @param dtl
     * @return true if the detail tag doesn't exist in Asset
     */
    public boolean checkTotalDetailCount(Pretag pretag) {
        boolean success = true;

        BigDecimal totalNumerOfDetails = new BigDecimal(pretag.getPretagDetails().size());
        if (pretag.getQuantityInvoiced() == null) {
             GlobalVariables.getErrorMap().putError("campusTagNumber", CamsKeyConstants.ERROR_PRE_TAG_DETAIL_EXCESS, new String[] { "0" });
            success &= false;
        }
        else {
            if (pretag.getQuantityInvoiced().compareTo(totalNumerOfDetails) == 0) {
                GlobalVariables.getErrorMap().putError("campusTagNumber", CamsKeyConstants.ERROR_PRE_TAG_DETAIL_EXCESS, new String[] { pretag.getPretagDetails().size() + "" });
                success &= false;
            }
        }

        return success;
    }

    /**
     * This method ensures that each {@link pretagDetail} tag number does not exist in Asset table
     * 
     * @param dtl
     * @return true if the detail tag doesn't exist in Asset
     */
    public boolean checkCampusTagNumber(PretagDetail dtl) {
        boolean success = true;

        getDictionaryValidationService().validateBusinessObject(dtl);
        if (StringUtils.isNotBlank(dtl.getCampusTagNumber())) {
            Map tagMap = new HashMap();
            tagMap.put("campusTagNumber", dtl.getCampusTagNumber());
            if (getBoService().countMatching(Asset.class, tagMap) != 0) {
                GlobalVariables.getErrorMap().putError("campusTagNumber", CamsKeyConstants.ERROR_PRE_TAG_NUMBER, new String[] { dtl.getCampusTagNumber() });
                success &= false;
            }
        }

        return success;
    }

    /**
     * This method ensures that each {@link pretagDetail} buildingCode and buildingRoomNumber does exist in bulding and room tables
     * 
     * @param dtl
     * @return true if the detail buildingCode and buildingRoomNumber does exist in building and room
     */
    public boolean validateCampusBuildingRoom(PretagDetail dtl) {
        boolean success = true;

        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        getDictionaryValidationService().validateBusinessObject(dtl);
        if (StringUtils.isNotBlank(dtl.getCampusCode()) && StringUtils.isNotBlank(dtl.getBuildingCode())) {
            Map preTagMap = new HashMap();
            preTagMap.put("campusCode", dtl.getCampusCode());
            preTagMap.put("buildingCode", dtl.getBuildingCode());
            bo = (Building) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Building.class, preTagMap);
            if (bo == null) {
                GlobalVariables.getErrorMap().putError("buildingCode", CamsKeyConstants.ERROR_INVALID_BUILDING_CODE, new String[] { dtl.getCampusCode(), dtl.getBuildingCode() });
            }
            if (StringUtils.isNotBlank(dtl.getBuildingRoomNumber())) {
                preTagMap.put("buildingRoomNumber", dtl.getBuildingRoomNumber());
                bo = (Room) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Room.class, preTagMap);
                if (bo == null) {
                    GlobalVariables.getErrorMap().putError("buildingRoomNumber", CamsKeyConstants.ERROR_INVALID_ROOM_NUMBER, new String[] { dtl.getCampusCode(), dtl.getBuildingCode(), dtl.getBuildingRoomNumber() });
                }
            }
        }
        success &= GlobalVariables.getErrorMap().getErrorCount() == originalErrorCount;

        return success;
    }
}
