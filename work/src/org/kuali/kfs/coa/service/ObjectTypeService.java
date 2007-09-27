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
package org.kuali.module.chart.service;

import java.util.List;

import org.kuali.module.chart.bo.ObjectType;

/**
 *  
 */

public interface ObjectTypeService {
    /**
     * Get an ObjectType by code/id.
     * 
     * @param objectTypeCode
     * @return
     */
    public ObjectType getByPrimaryKey(String objectTypeCode);
    
    /**
     * Returns a list of basic expense objects from options table for a given university fiscal year
     * @param universityFiscalYear
     * @return
     */
    public List<String> getBasicExpenseObjectTypes(Integer universityFiscalYear);
    
    
    /**
     * Returns the expense transfer object type from options table for a given university fiscal year
     * @param universityFiscalYear
     * @return
     */
    public String getExpenseTransferObjectType(Integer universityFiscalYear);
    
    /**
     * Returns the asset object type from options table for a given university fiscal year
     * @param universityFiscalYear
     * @return
     */
    public String getAssetObjectType(Integer universityFiscalYear);
    
    /**
     * Returns a list of basic income objects from options table for a given university fiscal year
     * @param universityFiscalYear
     * @return
     */
    public List<String> getBasicIncomeObjectTypes(Integer universityFiscalYear);
    
    /**
     * Returns the income transfer object type from options table for a given university fiscal year
     * @param universityFiscalYear
     * @return
     */
    public String getIncomeTransferObjectType(Integer universityFiscalYear);
    
    /**
     * Returns a list of basic expense objects from options table for the current university fiscal year
     * @param universityFiscalYear
     * @return
     */
    public List<String> getCurrentYearBasicExpenseObjectTypes();
    
    
    /**
     * Returns the expense transfer object type from options table for the current university fiscal year
     * @param universityFiscalYear
     * @return
     */
    public String getCurrentYearExpenseTransferObjectType();
    
    /**
     * Returns the asset object type from options table for the current university fiscal year
     * @param universityFiscalYear
     * @return
     */
    public String getCurrentYearAssetObjectType();
    
    /**
     * Returns a list of basic income objects from options table for the current university fiscal year
     * @param universityFiscalYear
     * @return
     */
    public List<String> getCurrentYearBasicIncomeObjectTypes();
    
    /**
     * Returns the income transfer object type from options table for the current university fiscal year
     * @param universityFiscalYear
     * @return
     */
    public String getCurrentYearIncomeTransferObjectType(); 
    
    /**
     * Returns a list of the object types that the nominal balance selector uses to determine if it should
     * process a balance or not
     * @param fiscalYear
     * @return
     */
    public List<String> getNominalActivityClosingAllowedObjectTypes(Integer fiscalYear);
}
