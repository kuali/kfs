/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service;

import java.sql.Date;
import java.util.List;

import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * define the common method calls around Per Diem objects
 */
public interface PerDiemService {

    /**
     * break down meals and incidental if needed
     * 
     * @param perDiemList the given list of per diem records
     */
    public <T extends PerDiem> void breakDownMealsIncidental(List<T> perDiemList);

    /**
     * break down meals and incidental if needed
     * 
     * @param perDiem the given per diem
     */
    public <T extends PerDiem> void breakDownMealsIncidental(T perDiem);

    /**
     * update the trip type of the given per diem
     * 
     * @param perDiemList the given list of per diem records
     */
    public <T extends PerDiem> void updateTripType(List<T> perDiemList);

    /**
     * update the trip type of the given per diem
     * 
     * @param perDiem the given per diem
     */
    public <T extends PerDiem> void updateTripType(T perDiem);

    /**
     * deactivate the active per diem and save the changes if today's date is beyond the season end date
     */
    public void deactivateAndSavePerDiem();

    /**
     * retrieve all active per diem
     * 
     * @return all active per diem
     */
    public <T extends PerDiem> List<T> retrieveActivePerDiem();

    /**
     * deactivate the active per diem if today's date is beyond the season end date
     * 
     * @param perDiemList the given list of per diem records
     */
    public <T extends PerDiem> void deactivatePerDiemBySeasonEndDate(List<T> perDiemList);

    /**
     * deactivate the per diem if the given date is later than the season end date
     * 
     * @param perDiemList the given list of per diem records
     * @param date the given date
     */
    public <T extends PerDiem> void deactivatePerDiemBySeasonEndDate(List<T> perDiemList, Date date);

    /**
     * deactivate the per diem if the given date is later than the season end date
     * 
     * @param perDiem the given per diem record
     * @param date the given date
     */
    public <T extends PerDiem> void deactivatePerDiemSeasonEndDate(T perDiem, Date date);

    /**
     * deactivate the per diem if the given date is later than the effective date
     * 
     * @param perDiemList the given list of per diem records
     * @param date the given date
     */
    public <T extends PerDiem> void deactivatePerDiemByEffectiveDate(List<T> perDiemList, Date date);

    /**
     * deactivate the per diem if the given date is later than the effective date
     * 
     * @param perDiem the given per diem record
     * @param date the given date
     */
    public <T extends PerDiem> void deactivatePerDiemByEffectiveDate(T perDiem, Date date);

    /**
     * find the previous per diems for the given new per diems
     * 
     * @param perDiemList the given list of per diems
     * @return the previous per diems of the given new per diems
     */
    public <T extends PerDiem> List<PerDiem> retrievePreviousPerDiem(List<T> perDiemList);

    /**
     * find the previous per diem for the given new per diem
     * 
     * @param perDiem the given new per diem
     * @return the previous per diem of the given new per diem
     */
    public <T extends PerDiem> List<PerDiem> retrievePreviousPerDiem(T perDiem);

    /**
     * find the previous per diems for the given new per diems and reset their expiration dates to the dates before the effective
     * dates of the new per diems
     * 
     * @param perDiemList the given list of per diems
     * @param isDeactivate detemine whether the per diem has to be deactivated
     * @return the previous per diems of the given new per diems
     */
    public <T extends PerDiem> List<PerDiem> retrieveExpireDeactivatePreviousPerDiem(List<T> perDiemList, boolean isDeactivate);

    /**
     * find the previous per diem for the given new per diem and reset its expiration date to the date before the effective date of
     * the new per diem
     * 
     * @param perDiem the given new per diem
     * @param isDeactivate detemine whether the per diem has to be deactivated
     * @return the previous per diem of the given new per diem
     */
    public <T extends PerDiem> List<PerDiem> retrieveExpireDeactivatePreviousPerDiem(T perDiem, boolean isDeactivate);
    
    /**
     * check whether the given per diem exists in the database
     * 
     * @param perDiem the given per diem
     * @return true if the given per diem exists in the database
     */
    public <T extends PerDiem> boolean hasExistingPerDiem(T perDiem);

    /**
     * Set the PerDiem Categories display on a TravelForm and whether or not display detail breakdown on meals
     * For TA and TR docs
     * 
     * @param form
     */
    public void setPerDiemCategoriesAndBreakdown(TravelFormBase form);
    
    public KualiDecimal getMealsAndIncidentalsGrandTotal(TravelDocument travelDocument);

    public KualiDecimal getLodgingGrandTotal(TravelDocument travelDocument);

    public KualiDecimal getMileageTotalGrandTotal(TravelDocument travelDocument);

    public KualiDecimal getDailyTotalGrandTotal(TravelDocument travelDocument);
    
    public Integer getMilesGrandTotal(TravelDocument travelDocument);
}
