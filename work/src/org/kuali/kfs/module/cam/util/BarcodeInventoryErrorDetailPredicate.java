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
package org.kuali.kfs.module.cam.util;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;

/**
 * 
 * Helps filter out records from a collection of BCIE and replace its elements with the inputted data 
 */
public class BarcodeInventoryErrorDetailPredicate implements Predicate, Closure {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorDetailPredicate.class);
    private BarcodeInventoryErrorDocument doc;

    /**
     * 
     * Constructs a BarcodeInventoryErrorDetailPredicate.java.
     * @param form
     */
    public BarcodeInventoryErrorDetailPredicate(BarcodeInventoryErrorDocument doc) {
        this.doc = doc;
    }

    /**
     * 
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    public boolean evaluate(Object object) {
        boolean satisfies = true;
        
        if (object instanceof BarcodeInventoryErrorDetail) {            
            
            BarcodeInventoryErrorDetail detail = (BarcodeInventoryErrorDetail) object;
            
            // It will only replace when the status is equals to error
            if (detail.getErrorCorrectionStatusCode().equals(CamsConstants.BarCodeInventoryError.STATUS_CODE_ERROR)) {            
                if ((this.doc.getCurrentTagNumber() != null) && !StringUtils.isBlank(this.doc.getCurrentTagNumber())) {
                    if (!StringUtils.equals(this.doc.getCurrentTagNumber(), detail.getAssetTagNumber())) {
                        satisfies = false;
                    }
                }
    
                if (this.doc.getCurrentScanCode() != null && !StringUtils.isBlank(this.doc.getCurrentScanCode())) {
                    satisfies = (this.doc.getCurrentScanCode().equals("Y") && detail.isUploadScanIndicator());
                }
    
                if (this.doc.getCurrentCampusCode() != null && !StringUtils.isBlank(this.doc.getCurrentCampusCode())) {
                    if (!StringUtils.equals(this.doc.getCurrentCampusCode(), detail.getCampusCode())) {
                        satisfies = false;
                    }
                }
    
                if ((this.doc.getCurrentBuildingNumber() != null) && !StringUtils.isBlank(this.doc.getCurrentBuildingNumber())) {
                    if (!StringUtils.equals(this.doc.getCurrentBuildingNumber(), detail.getBuildingCode())) {
                        satisfies = false;
                    }
                }
    
                if ((this.doc.getCurrentRoom() != null) && !StringUtils.isBlank(this.doc.getCurrentRoom())) {
                    if (!StringUtils.equals(this.doc.getCurrentRoom(), detail.getBuildingRoomNumber())) {
                        satisfies = false;
                    }
                }
    
                if ((this.doc.getCurrentSubroom() != null) && !StringUtils.isBlank(this.doc.getCurrentSubroom())) {
                    if (!StringUtils.equals(this.doc.getCurrentSubroom(), detail.getBuildingSubRoomNumber())) {
                        satisfies = false;
                    }
                }
    
                if ((this.doc.getCurrentConditionCode() != null) && !StringUtils.isBlank(this.doc.getCurrentConditionCode())) {
                    if (!StringUtils.equals(this.doc.getCurrentConditionCode(), detail.getAssetConditionCode())) {
                        satisfies = false;
                    }
                }
            }
            else {
                satisfies = false;
            }
        }
        return satisfies;
    }

    /**
     * 
     * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
     */
    public void execute(Object object) {
        if (this.evaluate(object)) {
            BarcodeInventoryErrorDetail detail = (BarcodeInventoryErrorDetail) object;
            if (this.doc.getNewCampusCode() != null && !StringUtils.isBlank(this.doc.getNewCampusCode())) {
                detail.setCampusCode(this.doc.getNewCampusCode());
            }

            if ((this.doc.getNewBuildingNumber() != null) && !StringUtils.isBlank(this.doc.getNewBuildingNumber())) {
                detail.setBuildingCode(this.doc.getNewBuildingNumber());
            }

            if ((this.doc.getNewRoom() != null) && !StringUtils.isBlank(this.doc.getNewRoom())) {
                detail.setBuildingRoomNumber(this.doc.getNewRoom());
            }

            if ((this.doc.getNewSubroom() != null) && !StringUtils.isBlank(this.doc.getNewSubroom())) {
                detail.setBuildingSubRoomNumber(this.doc.getNewSubroom());
            }

            if ((this.doc.getNewConditionCode() != null) && !StringUtils.isBlank(this.doc.getNewConditionCode())) {
                detail.setAssetConditionCode(this.doc.getNewConditionCode());
            }
        }
    }
}
