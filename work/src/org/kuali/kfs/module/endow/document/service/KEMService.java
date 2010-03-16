/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

import java.math.BigDecimal;
import java.util.Date;

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
     * Computes the market value.
     * 
     * @param units the number of units
     * @param unitValue the unit value
     * @param classCodeType
     * @return the computed market value
     */
    public BigDecimal getMarketValue(BigDecimal units, BigDecimal unitValue, String classCodeType);

    /**
     * Gets the current system process date.
     * 
     * @return a String representing the value of the current system process date
     */
    public String getCurrentSystemProcessDate();

    public String getCurrentSystemProcessDateFormated() throws Exception;

    public Date getCurrentSystemProcessDateObject();

    /**
     * Gets the current date based on a system parameter USE_PROCESS_DATE: <br>
     * 1) If USE_PROCESS_DATE == true, get the value from CURRENT_SYSTEM_PROCESS_DATE. <br>
     * 2) If USE_PROCESS_DATE == false, get the current date from the local system using standard Java API.
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

}
