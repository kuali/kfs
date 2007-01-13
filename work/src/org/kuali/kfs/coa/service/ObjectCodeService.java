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
package org.kuali.module.chart.service;

import java.util.List;

import org.kuali.module.chart.bo.ObjectCode;

/**
 * This interface defines methods that an ObjectCode Service must provide.
 * 
 * 
 */
public interface ObjectCodeService {

    /**
     * @param universityFiscalYear - University Fiscal Year
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param financialObjectCode - Financial Object Code
     * @return ObjectCode
     * 
     * Retrieves an ObjectCode object based on primary key.
     */
    public ObjectCode getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);

    /**
     * 
     * 
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param financialObjectCode - Financial Object Code
     * @return a list containing integer years, given object code. The list may be empty, but will not be null.
     */


    public List getYearList(String chartOfAccountsCode, String financialObjectCode);


}
