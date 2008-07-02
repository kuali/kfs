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
package org.kuali.kfs.module.cams.util;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.web.struts.BarcodeInventoryErrorForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;

public class BarcodeInventoryErrorDetailPredicate implements Predicate, Closure {

    BarcodeInventoryErrorForm bcieForm;
    public BarcodeInventoryErrorDetailPredicate(BarcodeInventoryErrorForm form) {
        BarcodeInventoryErrorForm bcieForm = form;
    }
    
    public boolean evaluate(Object object) {
        boolean satisfies = true;

        if( object instanceof BarcodeInventoryErrorDetail) {
            BarcodeInventoryErrorDetail detail = (BarcodeInventoryErrorDetail) object;

            /*if ((this.bcieForm.getCurrentTagNumber() != null) && !StringUtils.isBlank(this.bcieForm.getCurrentTagNumber())) {
                if(!StringUtils.equals(this.bcieForm.getCurrentTagNumber(),detail.getAssetTagNumber())) {
                    satisfies=false;
                }
            }*/

            
            /*
            satisfies&= (this.scanCode.equals("Y") && detail.isUploadScanIndicator());

            if (!StringUtils.isBlank(this.campusCode)) {
                if(!StringUtils.equals(this.campusCode,detail.getCampusCode())) {
                    satisfies=false;
                }
            }

            if (!StringUtils.isBlank(this.buildingNumber)) {
                if(!StringUtils.equals(this.buildingNumber,detail.getBuildingRoomNumber())) {
                    satisfies=false;
                }
            }
*/
            if (!StringUtils.isBlank(this.bcieForm.getCurrentRoom())) {
                if(!StringUtils.equals(this.bcieForm.getCurrentRoom(),detail.getBuildingRoomNumber())) {
                    satisfies=false;
                }
            }
/*
            if (!StringUtils.isBlank(this.subRoom)) {
                if(!StringUtils.equals(this.subRoom,detail.getBuildingSubRoomNumber())) {
                    satisfies=false;
                }
            }

            if (!StringUtils.isBlank(this.conditionCode)) {
                if(!StringUtils.equals(this.conditionCode,detail.getAssetConditionCode())){
                    satisfies=false;
                }
            }*/       
        } else {
            satisfies=false;
        }
        return satisfies;
    }
    
    
    public void execute(Object object) {
        if (this.evaluate(object)) {    
            BarcodeInventoryErrorDetail detail = (BarcodeInventoryErrorDetail) object;

            if (!StringUtils.isEmpty(this.bcieForm.getNewRoom())) {
                detail.setBuildingRoomNumber(this.bcieForm.getNewRoom());
            }
            
            
            
        }
        
    }
    
    
}