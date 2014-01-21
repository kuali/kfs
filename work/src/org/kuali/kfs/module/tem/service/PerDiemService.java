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
    public void processPerDiem();

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

    /**
     * @return true if the KFS-TEM / Document / PER_DIEM_CATEGORIES says that per diem is handling lodging; false otherwise
     */
    public boolean isPerDiemHandlingLodging();

    /**
     * Finds the active per diem record for the given destination id and date
     * @param primaryDestinationId the id of the destination to find the per diem for
     * @param perDiemDate the date we want the per diem to be active on
     * @return the retrieved per diem or null if a record was not found
     */
    public PerDiem getPerDiem(int primaryDestinationId, java.sql.Timestamp perDiemDate);

    /**
     * Determines if:
     * <ul>
     * <li>A current mileage rate for the KFS-TEM / Document / PER_DIEM_MILEAGE_RATE_EXPENSE_TYPE_CODE is available; if it is not, then per diem cannot be created
     * </ul>
     * @param form the form with the document on it, which may help in making such a decision
     */
    public boolean canCreatePerDiem(TravelDocument doc);
}
