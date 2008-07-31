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
package org.kuali.kfs.module.cam.util;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.web.struts.BarcodeInventoryErrorForm;

/**
 * 
 * Helps filter out records from a collection of BCIE and replace its elements with the inputted data 
 */
public class BarcodeInventoryErrorDetailPredicate implements Predicate, Closure {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorDetailPredicate.class);
    private BarcodeInventoryErrorForm bcieForm;

    /**
     * 
     * Constructs a BarcodeInventoryErrorDetailPredicate.java.
     * @param form
     */
    public BarcodeInventoryErrorDetailPredicate(BarcodeInventoryErrorForm form) {
        this.bcieForm = form;
    }

    /**
     * 
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    public boolean evaluate(Object object) {
        boolean satisfies = true;

        if (object instanceof BarcodeInventoryErrorDetail) {
            BarcodeInventoryErrorDetail detail = (BarcodeInventoryErrorDetail) object;

            if (StringUtils.isBlank(this.bcieForm.getCurrentTagNumber()) && StringUtils.isBlank(this.bcieForm.getCurrentScanCode()) && StringUtils.isBlank(this.bcieForm.getCurrentCampusCode()) && StringUtils.isBlank(this.bcieForm.getCurrentBuildingNumber()) && StringUtils.isBlank(this.bcieForm.getCurrentRoom()) && StringUtils.isBlank(this.bcieForm.getCurrentSubroom()) && StringUtils.isBlank(this.bcieForm.getCurrentConditionCode())) {
                return false;
            }

            if ((this.bcieForm.getCurrentTagNumber() != null) && !StringUtils.isBlank(this.bcieForm.getCurrentTagNumber())) {
                if (!StringUtils.equals(this.bcieForm.getCurrentTagNumber(), detail.getAssetTagNumber())) {
                    satisfies = false;
                }
            }

            if (this.bcieForm.getCurrentScanCode() != null && !StringUtils.isBlank(this.bcieForm.getCurrentScanCode())) {
                satisfies = (this.bcieForm.getCurrentScanCode().equals("Y") && detail.isUploadScanIndicator());
            }

            if (this.bcieForm.getCurrentCampusCode() != null && !StringUtils.isBlank(this.bcieForm.getCurrentCampusCode())) {
                if (!StringUtils.equals(this.bcieForm.getCurrentCampusCode(), detail.getCampusCode())) {
                    satisfies = false;
                }
            }

            if ((this.bcieForm.getCurrentBuildingNumber() != null) && !StringUtils.isBlank(this.bcieForm.getCurrentBuildingNumber())) {
                if (!StringUtils.equals(this.bcieForm.getCurrentBuildingNumber(), detail.getBuildingCode())) {
                    satisfies = false;
                }
            }

            if ((this.bcieForm.getCurrentRoom() != null) && !StringUtils.isBlank(this.bcieForm.getCurrentRoom())) {
                if (!StringUtils.equals(this.bcieForm.getCurrentRoom(), detail.getBuildingRoomNumber())) {
                    satisfies = false;
                }
            }

            if ((this.bcieForm.getCurrentSubroom() != null) && !StringUtils.isBlank(this.bcieForm.getCurrentSubroom())) {
                if (!StringUtils.equals(this.bcieForm.getCurrentSubroom(), detail.getBuildingSubRoomNumber())) {
                    satisfies = false;
                }
            }

            if ((this.bcieForm.getCurrentConditionCode() != null) && !StringUtils.isBlank(this.bcieForm.getCurrentConditionCode())) {
                if (!StringUtils.equals(this.bcieForm.getCurrentConditionCode(), detail.getAssetConditionCode())) {
                    satisfies = false;
                }
            }
        }
        else {
            satisfies = false;
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
            if (this.bcieForm.getNewCampusCode() != null && !StringUtils.isBlank(this.bcieForm.getNewCampusCode())) {
                detail.setCampusCode(this.bcieForm.getNewCampusCode());
            }

            if ((this.bcieForm.getNewBuildingNumber() != null) && !StringUtils.isBlank(this.bcieForm.getNewBuildingNumber())) {
                detail.setBuildingCode(this.bcieForm.getNewBuildingNumber());
            }

            if ((this.bcieForm.getNewRoom() != null) && !StringUtils.isBlank(this.bcieForm.getNewRoom())) {
                detail.setBuildingRoomNumber(this.bcieForm.getNewRoom());
            }

            if ((this.bcieForm.getNewSubroom() != null) && !StringUtils.isBlank(this.bcieForm.getNewSubroom())) {
                detail.setBuildingSubRoomNumber(this.bcieForm.getNewSubroom());
            }

            if ((this.bcieForm.getNewConditionCode() != null) && !StringUtils.isBlank(this.bcieForm.getNewConditionCode())) {
                detail.setAssetConditionCode(this.bcieForm.getNewConditionCode());
            }

        }
    }
}