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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.Room;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
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

        // setup Pretag convenience objects, make sure all possible detail lines are populated
        newPretag = (Pretag) super.getNewBo();
        for (PretagDetail dtl : newPretag.getPretagDetails()) {
            dtl.refreshNonUpdateableReferences();
        }
    }

    /**
     * Does not fail on rules failure
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = processPretagValidation();

        return true; // always return true on save
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        return processPretagValidation();
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        return processPretagValidation();
    }

    /**
     * Validates Pretag and its PretagDetail.
     * 
     * @return boolean false or true
     */
    public boolean processPretagValidation() {
        boolean success = true;
        boolean newDetailLine = false;

        setupConvenienceObjects();
        if (newPretag.isActive()) {
            success &= checkTotalDetailCount(newPretag, newDetailLine);
            success &= isAllCampusBuildingRoomValid(newPretag.getPretagDetails());
        }
        else {
            deactivePretagDetails(newPretag);
        }

        return success;
    }

    /**
     * This method loops through the list of {@link pretagDetail}s and passes them off to isAllCampusBuildingRoomValid for further
     * rule analysis
     * 
     * @param document
     * @param details
     * @return true if the collection of {@link pretagDetail}s passes the sub-rules
     */
    public boolean isAllCampusBuildingRoomValid(List<PretagDetail> details) {
        boolean success = true;

        // check if there are any pretag details
        if (details.size() != 0) {

            // check each CampusBuildingRoom
            int index = 0;
            for (PretagDetail dtl : details) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + "pretagDetails[" + index + "]";
                GlobalVariables.getErrorMap().addToErrorPath(errorPath);
                success &= isCampusBuildingRoomValid(dtl);
                GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
                index++;
            }
        }

        return success;
    }

    /**
     * This method calls isCampusTagNumberValid whenever a new {@link PretagDetail} is added to Pretag
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.core.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.core.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        setupConvenienceObjects();

        Pretag pretag = (Pretag) document.getNewMaintainableObject().getBusinessObject();
        PretagDetail detail = (PretagDetail) bo;

        boolean success = true;
        boolean newDetailLine = true;
        if (detail.isActive()) {

            detail.setPurchaseOrderNumber(pretag.getPurchaseOrderNumber());
            detail.setLineItemNumber(pretag.getLineItemNumber());

            success &= checkDuplicateTagNumber(pretag, detail.getCampusTagNumber());
            success &= checkTotalDetailCount(pretag, newDetailLine);
            success &= isCampusTagNumberValid(detail);
            success &= isCampusBuildingRoomValid(detail);
        }

        return success;
    }

    /**
     * This method check to see if duplicate tag exists
     * 
     * @return boolean indicating if validation succeeded
     */
    protected boolean checkDuplicateTagNumber(Pretag pretag, String tagNumber) {
        LOG.info("checkForDuplicate called");
        boolean success = true;

        for (PretagDetail dtl : pretag.getPretagDetails()) {
            if (dtl.getCampusTagNumber().equals(tagNumber) && dtl.isActive()) {
                putFieldError(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Pretag.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE, new String[] { tagNumber });
                success &= false;
            }
        }

        return success;
    }

    /**
     * This method ensures that total {@link pretagDetail} tag details does not excees in quantity invoiced
     * 
     * @param dtl
     * @return true if the detail tag doesn't exist in Asset
     */
    public boolean checkTotalDetailCount(Pretag pretag, boolean addLine) {
        boolean success = true;

        if (pretag.getQuantityInvoiced() != null) {
            int totalActiveDetails = getActiveDetailsCount(pretag, addLine);
            BigDecimal totalNumerOfDetails = new BigDecimal(totalActiveDetails);

            if (pretag.getQuantityInvoiced().compareTo(totalNumerOfDetails) < 0) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Pretag.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_PRE_TAG_DETAIL_EXCESS, new String[] { pretag.getQuantityInvoiced().toString() + "" });
                success &= false;
            }
            else {
                if ((pretag.getQuantityInvoiced().compareTo(new BigDecimal(0)) > 0) && (totalActiveDetails == 0)) {
                    putFieldError(CamsPropertyConstants.Pretag.PRETAG_DETAIL_CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_NO_DETAIL_LINE);
                    success &= false;
                }
            }
        }

        return success;
    }

    /**
     * This method reply that total active detail in {@link pretag}
     * 
     * @param pretag and newDetailLine
     * @return total number of active pretagDetails
     */
    public int getActiveDetailsCount(Pretag pretag, boolean newDetailLine) {

        Collection<PretagDetail> pretagDetails = pretag.getPretagDetails();
        if (newDetailLine) {
            return countActive(pretagDetails) + 1;
        }
        else {
            return countActive(pretagDetails);
        }
    }

    /**
     * This method ensures that each {@link pretagDetail} tag number does not exist in Asset table
     * 
     * @param dtl
     * @return true if the detail tag doesn't exist in Asset
     */
    public boolean isCampusTagNumberValid(PretagDetail dtl) {
        boolean success = true;

        if (dtl.isActive()) {
            Map tagMap = new HashMap();
            tagMap.put(CamsPropertyConstants.Pretag.CAMPUS_TAG_NUMBER, dtl.getCampusTagNumber());
            int matchDetailCount = getMatchDetailCount(tagMap);
            if ((getBoService().countMatching(Asset.class, tagMap) != 0) || (matchDetailCount > 0)) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.Pretag.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_PRE_TAG_NUMBER, new String[] { dtl.getCampusTagNumber() });
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
    public boolean isCampusBuildingRoomValid(PretagDetail dtl) {
        boolean success = true;

        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        getDictionaryValidationService().validateBusinessObject(dtl);

        if (StringUtils.isNotBlank(dtl.getCampusCode()) && StringUtils.isNotBlank(dtl.getBuildingCode())) {
            Map preTagMap = new HashMap();
            preTagMap.put(KFSPropertyConstants.CAMPUS_CODE, dtl.getCampusCode());
            preTagMap.put(KFSPropertyConstants.BUILDING_CODE, dtl.getBuildingCode());

            bo = (Building) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Building.class, preTagMap);
            if (bo == null) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.BUILDING_CODE, CamsKeyConstants.ERROR_INVALID_BUILDING_CODE, new String[] { dtl.getCampusCode(), dtl.getBuildingCode() });
            }

            if (StringUtils.isNotBlank(dtl.getBuildingRoomNumber())) {
                preTagMap.put(KFSPropertyConstants.BUILDING_ROOM_NUMBER, dtl.getBuildingRoomNumber());
                bo = (Room) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Room.class, preTagMap);
                if (bo == null) {
                    GlobalVariables.getErrorMap().putError(KFSPropertyConstants.BUILDING_ROOM_NUMBER, CamsKeyConstants.ERROR_INVALID_ROOM_NUMBER, new String[] { dtl.getCampusCode(), dtl.getBuildingCode(), dtl.getBuildingRoomNumber() });
                }
            }
        }
        success &= GlobalVariables.getErrorMap().getErrorCount() == originalErrorCount;

        return success;
    }

    /**
     * This method returns number of matched active campusTagNumber
     * 
     * @param map
     * @return active pretagDetail with same campusTagNumber
     */

    public int getMatchDetailCount(Map tagMap) {

        Collection<PretagDetail> pretagDetails = SpringContext.getBean(BusinessObjectService.class).findMatching(PretagDetail.class, tagMap);

        return countActive(pretagDetails);
    }

    /**
     * This method ensures that count {@link pretagDetail} active detail lines
     * 
     * @param collection
     * @return active pretagDetail count
     */
    public int countActive(Collection<PretagDetail> pretagDetails) {
        int activeCount = 0;

        for (PretagDetail dtl : pretagDetails) {
            if (dtl.isActive()) {
                activeCount++;
            }
        }

        return activeCount;
    }

    /**
     * This method ensures that all {@link pretag} detail lines deactivated
     * 
     * @param pretag
     * @return deactive pretagDetails
     */
    public void deactivePretagDetails(Pretag pretag) {
        boolean inActive = false;

        for (PretagDetail dtl : pretag.getPretagDetails()) {
            dtl.setActive(inActive);
        }
    }

}
