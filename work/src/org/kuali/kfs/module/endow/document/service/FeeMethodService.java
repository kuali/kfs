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

import java.sql.Date;
import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.FeeMethod;

public interface FeeMethodService {

    /**
     * Gets a collection of record from END_FEE_MTHD_T table
     * @return feeMethods
     */
    public Collection<FeeMethod> getFeeMethods();
    
    /**
     * Gets a collection of record from END_FEE_MTHD_T table based on
     * next processing date (FEE_NXT_PROC_DT) is equal to the current date.
     * @return feeMethods
     */
    public Collection<FeeMethod> getFeeMethodsByNextProcessingDate(java.util.Date nextProcessingDate);

    /**
     * Gets a feeMethod by primary key.
     * 
     * @param code
     * @return a feeMethod code
     */
    public FeeMethod getByPrimaryKey(String code);

    /**
     * Gets the feeMethod next process date as a String for use in javascript.
     * 
     * @param feeMethodCode
     * @return the fee Method next process date as a String
     */
    public String getFeeMethodNextProcessDateForAjax(String feeMethodCode);

    /**
     * Gets the feeMethod next process date.
     * 
     * @param feeMethodCode
     * @return the fee Method next process date
     */
    public Date getFeeMethodNextProcessDate(String feeMethodCode);

    /**
     * Checks if the fee method is used on any Kemid.
     * 
     * @param feeMethodCode
     * @return true if used, false otherwise
     */
    public boolean isFeeMethodUsedOnAnyKemid(String feeMethodCode);

    /**
     * Gets FeeMethod records
     */
}
