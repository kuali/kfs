/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.service;

import java.sql.Timestamp;

import org.kuali.core.util.KualiDecimal;


/**
 * Performs calculations of travel per diem and mileage amounts.
 * 
 * 
 */
public interface DisbursementVoucherTravelService {

    /**
     * Calculates the per diem travel amount.
     * 
     * @param travel
     * @return
     */
    public KualiDecimal calculatePerDiemAmount(Timestamp startDateTime, Timestamp endDateTime, KualiDecimal perDiemRate);

    /**
     * Calculates the mileage travel amount.
     * 
     * @param travel
     * @return
     */
    public KualiDecimal calculateMileageAmount(Integer totalMileage, Timestamp travelStartDate);

}