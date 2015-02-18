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
