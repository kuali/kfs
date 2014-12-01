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
