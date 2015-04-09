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
