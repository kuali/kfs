/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.service;

import java.sql.Timestamp;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * 
 * This service interface defines the methods that a DisbursementVoucherTravelService implementation must provide.
 * 
 * Performs calculations of travel per diem and mileage amounts.
 * 
 */
public interface DisbursementVoucherTravelService {

    /**
     * 
     * Calculates the per diem travel amount.  
     * 
     * @param startDateTime The start date and time of the period of time we will calculate the per diem amount for.
     * @param endDateTime The end date and time of the period of time we will calculate the per diem amount for.
     * @param perDiemRate The per diem rate used to calculate the total amount.
     * @return The per diem amount for the time period passed in and based on the rate given.
     */
    public KualiDecimal calculatePerDiemAmount(Timestamp startDateTime, Timestamp endDateTime, KualiDecimal perDiemRate);

    /**
     * 
     * Calculates the mileage travel amount.
     * 
     * @param totalMileage The total distance traveled.
     * @param travelStartDate The start date of the travel.
     * @return The mileage amount for the mileage given.
     */
    public KualiDecimal calculateMileageAmount(Integer totalMileage, Timestamp travelStartDate);

}
