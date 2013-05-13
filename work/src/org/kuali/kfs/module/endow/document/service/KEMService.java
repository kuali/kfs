/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

import java.math.BigDecimal;
import java.util.Date;

import org.kuali.rice.core.api.util.type.KualiInteger;

/**
 * This interface provides utility methods for the KEM module like mod10 calculation.
 */
public interface KEMService {


    /**
     * Computes the check digit based on the given prefix.
     * 
     * @param prefix
     * @return the string formed by the prefix + check digit
     */
    public String mod10(String prefix);


    /**
     * Computes the market value as the Sum of the HLDG _MVAL for all records for the Security in END_CURR_TAX_LOT_BAL_T.
     * 
     * @param securityId
     * @return the market value for the given security
     */
    public BigDecimal getMarketValue(String securityId);

    /**
     * Gets the market value as the HLDG _MVAL for the record in END_CURR_TAX_LOT_BAL_T for the given kemid, securityId,
     * registrationCode, lotNumber, ipIndicator.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param lotNumber
     * @param ipIndicator
     * @return the market value for the given kemid, securityId, registrationCode, lotNumber, ipIndicator.
     */
    public BigDecimal getMarketValue(String kemid, String securityId, String registrationCode, KualiInteger lotNumber, String ipIndicator);

    /**
     * Gets the current system process date.
     * 
     * @return a String representing the value of the current system process date
     */
    public String getCurrentSystemProcessDate();

    public String getCurrentSystemProcessDateFormated() throws Exception;

    public Date getCurrentSystemProcessDateObject();

    /**
     * Gets the current date based on a system parameter USE_PROCESS_DATE_IND: <br>
     * 1) If USE_PROCESS_DATE_IND = Y (true), get the value from CURRENT_PROCESS_DATE. <br>
     * 2) If USE_PROCESS_DATE_IND = N (false), get the current date from the local system using standard Java API.
     * 
     * @return the current date
     */
    public java.sql.Date getCurrentDate();


    /**
     * Gets the current process date from the CURRENT_PROCESS_DATE parameter
     * 
     * @return current process date
     */
    public java.sql.Date getCurrentProcessDate();

    /**
     * Gets the AVAILABLE_CASH_PERCENT system parameter
     * 
     * @return AVAILABLE_CASH_PERCENT value
     */
    public BigDecimal getAvailableCashPercent();

    /**
     * Gets the FISCAL_YEAR_END_MONTH_AND_DAY system parameter
     * TODO This should be refactored to a generic parm accessible to any module.
     * @return FISCAL_YEAR_END_MONTH_AND_DAY value
     */
    public Date getFiscalYearEndDayAndMonth();

    /**
     * Gets the DISTRIBUTION_TIMES_PER_YEAR system parameter
     * 
     * @return DISTRIBUTION_TIMES_PER_YEAR value
     */
    public long getTotalNumberOfPaymentsForFiscalYear();

    /**
     * Gets the number of days in the calendar year.
     * 
     * @return the number of days in the calendar year
     */
    public int getNumberOfDaysInCalendarYear();
    
    /**
     * Gets the first day after the fiscal year End Day and Month system parameter.
     * 
     */
    public java.sql.Date getFirstDayAfterFiscalYearEndDayAndMonth();
    
    /**
     * Gets MAXIMUM_TRANSACTION_LINES value from the system parameter.
     * 
     */
    public int getMaxNumberOfTransactionLinesPerDocument();
}
