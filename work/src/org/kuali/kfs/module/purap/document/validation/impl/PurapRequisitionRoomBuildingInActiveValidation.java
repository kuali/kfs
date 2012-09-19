/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocumentBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class PurapRequisitionRoomBuildingInActiveValidation extends GenericValidation{
    
    private BusinessObjectService businessObjectService; //
    
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean isActive = true;
        PurchasingDocumentBase req = (PurchasingDocumentBase)event.getDocument();
        MessageMap errorMap = GlobalVariables.getMessageMap();
        
        errorMap.clearErrorPath();
        
        Map primarykeys = new HashMap();
        //Pass primary keys campusCode + buildingCode, to retrieve building object.
        
        primarykeys.put(KFSPropertyConstants.CAMPUS_CODE, req.getDeliveryCampusCode());
        primarykeys.put(KFSPropertyConstants.BUILDING_CODE, req.getDeliveryBuildingCode());
  
        Building building = businessObjectService.findByPrimaryKey(Building.class ,primarykeys);
        
        if(building != null){
            if(! building.isActive() ){
                errorMap.putError(PurapConstants.DELIVERY_BUILDING_NAME_INACTIVE_ERROR, PurapKeyConstants.ERROR_INACTIVE_BUILDING);
                isActive &= building.isActive();
            }
        }
        
        //Pass primary keys campusCode + buildingCode + buildingRoomNumber, to retrieve room object.        
        primarykeys.put("buildingRoomNumber", req.getDeliveryBuildingRoomNumber());
        Room room = (Room)businessObjectService.findByPrimaryKey(Room.class ,primarykeys);
        
        if(room!=null){
            if(! room.isActive()){
                errorMap.putError(PurapConstants.DELIVERY_ROOM_NUMBER_INACTIVE_ERROR, PurapKeyConstants.ERROR_INACTIVE_ROOM);
                isActive &= room.isActive();
            }        
        }
        return isActive;
    }
    
    public void setbusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }   
}