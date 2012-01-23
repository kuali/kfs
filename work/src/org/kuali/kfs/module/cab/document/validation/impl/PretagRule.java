/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cab.document.validation.impl;


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cab.CabKeyConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.module.cab.businessobject.PretagDetail;
import org.kuali.kfs.module.cab.document.service.PurApInfoService;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizer;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * This class represents the business rules for the maintenance of {@link AccountGlobal} business objects
 */
public class PretagRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PretagRule.class);
    protected PersistableBusinessObject bo;
    protected Pretag newPretag;

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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = processPretagValidation();

        return success & super.processCustomSaveDocumentBusinessRules(document); // always return true on save
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        
        // get the documentAuthorizer for this document
        MaintenanceDocumentAuthorizer documentAuthorizer = (MaintenanceDocumentAuthorizer) getDocumentHelperService().getDocumentAuthorizer(document);
        
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        boolean success = true;
        if (workflowDocument.isInitiated() || workflowDocument.isSaved()){
            success &= documentAuthorizer.canCreateOrMaintain((MaintenanceDocument)document, GlobalVariables.getUserSession().getPerson());
            if (!success) {
                putFieldError(CabPropertyConstants.Pretag.CHART_OF_ACCOUNTS_CODE, CabKeyConstants.CHART_ORG_DISALLOWED_BY_CURRENT_USER);
            }
        }

        return success & super.processCustomRouteDocumentBusinessRules(document);
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
        success &= checkPurchaseOrderItemExists();
        success &= checkAssetRepresentativePrincipalNameExists();
        
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
     * validate the asset representative principal name.
     * 
     * @return boolean false or true
     */
    protected boolean checkAssetRepresentativePrincipalNameExists() {
        boolean valid = true;
        if (StringUtils.isNotBlank(newPretag.getPersonUniversal().getPrincipalName())) {
            PersonService personService = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class);
            Person person = personService.getPersonByPrincipalName(newPretag.getPersonUniversal().getPrincipalName());
            if (person != null) {
                newPretag.setPersonUniversal(person);
                newPretag.setRepresentativeUniversalIdentifier(person.getPrincipalId());
            }
            else {
                putFieldError(CabPropertyConstants.Pretag.REPRESENTATIVE_ID, CamsKeyConstants.PreTag.ERROR_PRE_TAG_INVALID_REPRESENTATIVE_ID, newPretag.getPersonUniversal().getPrincipalName());
                newPretag.setPersonUniversal(null);
                newPretag.setRepresentativeUniversalIdentifier(null);
                valid &= false;
            }
        }
        return valid;
    }
    
    /**
     * validate the purchase order item existence in PurAp.
     * 
     * @return boolean false or true
     */
    protected boolean checkPurchaseOrderItemExists() {
        boolean valid = true;
        if (StringUtils.isNotBlank(newPretag.getPurchaseOrderNumber()) && newPretag.getItemLineNumber() != null) {
            PurchaseOrderDocument purchaseOrderDoc = getPurApInfoService().getCurrentDocumentForPurchaseOrderIdentifier(Integer.valueOf(newPretag.getPurchaseOrderNumber()));
            if (purchaseOrderDoc == null) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(Pretag.class.getName()).getAttributeDefinition(CabPropertyConstants.Pretag.PURCHASE_ORDER_NUMBER).getLabel();
                putFieldError(CabPropertyConstants.Pretag.PURCHASE_ORDER_NUMBER, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid = false;
            }
            else if (getItemByLineNumber(purchaseOrderDoc, newPretag.getItemLineNumber()) == null) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(Pretag.class.getName()).getAttributeDefinition(CabPropertyConstants.Pretag.ITEM_LINE_NUMBER).getLabel();
                putFieldError(CabPropertyConstants.Pretag.ITEM_LINE_NUMBER, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Get PurchaseOrderItem by given item line number
     * 
     * @param items
     * @param lineNumber
     * @return
     */
    protected PurApItem getItemByLineNumber(PurchaseOrderDocument purchaseOrderDocument, int lineNumber) {
        List items = purchaseOrderDocument.getItems();
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            PurApItem item = (PurApItem) iter.next();
            if (item.getItemLineNumber() != null && item.getItemLineNumber().intValue() == lineNumber) {
                return item;
            }
        }
        return null;
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
                GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                success &= isCampusBuildingRoomValid(dtl);
                GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
                index++;
            }
        }

        return success;
    }

    /**
     * This method calls isCampusTagNumberValid whenever a new {@link PretagDetail} is added to Pretag
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
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
            detail.setItemLineNumber(pretag.getItemLineNumber());

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
        boolean success = true;

        for (PretagDetail dtl : pretag.getPretagDetails()) {
            if (dtl.getCampusTagNumber().equals(tagNumber) && dtl.isActive()) {
                GlobalVariables.getMessageMap().putError(CabPropertyConstants.Pretag.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE, new String[] { tagNumber });
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
            KualiDecimal totalNumerOfDetails = new KualiDecimal(totalActiveDetails);

            if (pretag.getQuantityInvoiced().compareTo(totalNumerOfDetails) < 0) {
                GlobalVariables.getMessageMap().putError(CabPropertyConstants.Pretag.CAMPUS_TAG_NUMBER, CamsKeyConstants.PreTag.ERROR_PRE_TAG_DETAIL_EXCESS, new String[] { pretag.getQuantityInvoiced().toString() + "" + " Total number of detail lines " + totalNumerOfDetails.toString() });
                success &= false;
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

        if ((dtl.getCampusTagNumber() != null) && (dtl.isActive() && !dtl.getCampusTagNumber().equalsIgnoreCase("N"))) {
            Map<String, String> tagMap = new HashMap<String, String>();
            tagMap.put(CabPropertyConstants.Pretag.CAMPUS_TAG_NUMBER, dtl.getCampusTagNumber());
            int matchDetailCount = getMatchDetailCount(tagMap);
            if ((getBoService().countMatching(Asset.class, tagMap) != 0) || (matchDetailCount > 0)) {
                GlobalVariables.getMessageMap().putError(CabPropertyConstants.Pretag.CAMPUS_TAG_NUMBER, CamsKeyConstants.PreTag.ERROR_PRE_TAG_NUMBER, new String[] { dtl.getCampusTagNumber() });
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

        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        getDictionaryValidationService().validateBusinessObject(dtl);

        if (StringUtils.isNotBlank(dtl.getCampusCode()) && StringUtils.isNotBlank(dtl.getBuildingCode())) {
            Map<String, String> preTagMap = new HashMap<String, String>();
            preTagMap.put(KFSPropertyConstants.CAMPUS_CODE, dtl.getCampusCode());
            preTagMap.put(KFSPropertyConstants.BUILDING_CODE, dtl.getBuildingCode());

            bo = (Building) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Building.class, preTagMap);
            if (bo == null) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.BUILDING_CODE, CamsKeyConstants.ERROR_INVALID_BUILDING_CODE, new String[] { dtl.getCampusCode(), dtl.getBuildingCode() });
            }

            if (StringUtils.isNotBlank(dtl.getBuildingRoomNumber())) {
                preTagMap.put(KFSPropertyConstants.BUILDING_ROOM_NUMBER, dtl.getBuildingRoomNumber());
                bo = (Room) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Room.class, preTagMap);
                if (bo == null) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.BUILDING_ROOM_NUMBER, CamsKeyConstants.ERROR_INVALID_ROOM_NUMBER, new String[] { dtl.getCampusCode(), dtl.getBuildingCode(), dtl.getBuildingRoomNumber() });
                }
            }
        }
        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;
    }

    /**
     * This method returns number of matched active campusTagNumber
     * 
     * @param map
     * @return active pretagDetail with same campusTagNumber
     */

    public int getMatchDetailCount(Map<String, String> tagMap) {

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

    /**
     * Gets the purchaseOrderService attribute.
     * 
     * @return Returns the purchaseOrderService.
     */
    protected PurApInfoService getPurApInfoService() {
        return SpringContext.getBean(PurApInfoService.class);
    }
}
