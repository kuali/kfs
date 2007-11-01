/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.chart.dao;

import java.util.List;

import org.kuali.module.chart.bo.ObjectCode;


/**
 * This interface defines basic methods that ObjectCode Dao's must provide
 * 
 * 
 */
public interface ObjectCodeDao {

    /**
     * 
     * Retrieves an ObjectCode object by primary key.
     * @param universityFiscalYear - part of composite key
     * @param chartOfAccountsCode - part of composite key
     * @param financialObjectCode - part of composite key
     * @return ObjectCode
     */
    public ObjectCode getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);


    /**
     * 
     * This method retrieves a list of years based on the chart of accounts code and object code passed in
     * @param chartOfAccountsCode
     * @param financialObjectCode
     * @return list of years that match the chart and object code passed in
     */
    public List getYearList(String chartOfAccountsCode, String financialObjectCode);

}
