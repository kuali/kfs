/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.coa.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;

/**
 * This interface defines methods that an ObjectCode Service must provide.
 */
public interface ObjectCodeService {

    /**
     * @param universityFiscalYear - University Fiscal Year
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param financialObjectCode - Financial Object Code
     * @return ObjectCode Retrieves an ObjectCode object based on primary key.
     */
    public ObjectCode getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);

    /**
     * @param universityFiscalYear - University Fiscal Year
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param financialObjectCode - Financial Object Code
     * @return ObjectCode Retrieves an ObjectCode object based on primary key.
     */
    public ObjectCode getByPrimaryIdWithCaching(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);

    /**
     * This method returns an financial object code for the current fiscal year.
     * 
     * @param chartOfAccountsCode chart of accounts code for object code
     * @param financialObjectCode financial object code
     * @return the object code specified
     */
    public ObjectCode getByPrimaryIdForCurrentYear(String chartOfAccountsCode, String financialObjectCode);

    /**
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param financialObjectCode - Financial Object Code
     * @return a list containing integer years, given object code. The list may be empty, but will not be null.
     */
    public List getYearList(String chartOfAccountsCode, String financialObjectCode);

    /**
     * This method, written for use with DWR, returns a joined string representation of all of the names of the distinct object
     * codes associated with each of the chart codes given. In the best of all possible worlds, this will only ever return *one*
     * object code name, as object codes will be shared across charts.
     * 
     * @param universityFiscalYear the fiscal year of the financial object code to check.
     * @param chartOfAccountCodes array of Chart of Accounts codes to
     * @param financialObjectCode financial object code to look up
     * @return a String representation of the distinct names of the object codes
     */
    public String getObjectCodeNamesByCharts(Integer universityFiscalYear, String[] chartOfAccountCodes, String financialObjectCode);
    
    public List<ObjectCode> getObjectCodesByLevelIds(List<String> levelCodes);
}
