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
package org.kuali.kfs.coa.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectType;

/**
 *
 * This service interface defines methods necessary for retrieving fully populated ObjectType business objects from the database
 * that are necessary for transaction processing in the application. It also defines methods to retrieve specific object type codes
 */
public interface ObjectTypeService {

    /**
     * Retrieve an ObjectType by code/id.
     *
     * @param objectTypeCode
     * @return an ObjectType that matches this code
     */
    public ObjectType getByPrimaryKey(String objectTypeCode);

    /**
     * Returns a list of basic expense objects from options table for a given university fiscal year
     *
     * @param universityFiscalYear
     * @return a list of basic expense object types based on the fiscal year
     */
    public List<String> getBasicExpenseObjectTypes(Integer universityFiscalYear);

    /**
     *
     * Returns a list of all expense objects from options table for a given university fiscal year
     * @param universityFiscalYear
     * @return a list of all expense object types based on the fiscal year
     */
    public List<String> getExpenseObjectTypes(Integer universityFiscalYear);

    /**
     * Returns the expense transfer object type from options table for a given university fiscal year
     *
     * @param universityFiscalYear
     * @return the expense transfer object type associated with this fiscal year
     */
    public String getExpenseTransferObjectType(Integer universityFiscalYear);

    /**
     * Returns the asset object type from options table for a given university fiscal year
     *
     * @param universityFiscalYear
     * @return the asset object type that is associated with this fiscal year
     */
    public String getAssetObjectType(Integer universityFiscalYear);

    /**
     * Returns a list of basic income objects from options table for a given university fiscal year
     *
     * @param universityFiscalYear
     * @return a list of basic income object types associated with this fiscal year
     */
    public List<String> getBasicIncomeObjectTypes(Integer universityFiscalYear);

    /**
     * Returns the income transfer object type from options table for a given university fiscal year
     *
     * @param universityFiscalYear
     * @return the income transfer object type associated with this fiscal year
     */
    public String getIncomeTransferObjectType(Integer universityFiscalYear);

    /**
     * Returns a list of basic expense objects from options table for the current university fiscal year
     *
     * @return list of current fiscal year basic expense object types
     */
    public List<String> getCurrentYearBasicExpenseObjectTypes();

    /**
     * Returns a list of basic expense objects from options table for the current university fiscal year. Normally, this method
     * returns the same values as getCurrentYearBasicExpenseObjectTypes, but also has the expenseTransferObjectType value.
     *
     * @return current fiscal year expense object types
     */
    public List<String> getCurrentYearExpenseObjectTypes();

    /**
     * Returns the expense transfer object type from options table for the current university fiscal year
     *
     * @return current fiscal year expense transfer object type
     */
    public String getCurrentYearExpenseTransferObjectType();

    /**
     * Returns the asset object type from options table for the current university fiscal year
     *
     * @return current fiscal year asset object type
     */
    public String getCurrentYearAssetObjectType();

    /**
     * Returns a list of basic income objects from options table for the current university fiscal year
     *
     * @return list of current year basic income object types associated with this fiscal year
     */
    public List<String> getCurrentYearBasicIncomeObjectTypes();

    /**
     * Returns the income transfer object type from options table for the current university fiscal year
     *
     * @return current fiscal year income transfer object type
     */
    public String getCurrentYearIncomeTransferObjectType();


    /**
     * Returns a list of the object types that the nominal balance selector uses to determine if it should process a balance or not
     *
     * @param fiscalYear
     * @return list of nominal activity closing allowed object types associated with this fiscal year
     */
    public List<String> getNominalActivityClosingAllowedObjectTypes(Integer fiscalYear);

    /**
     * Returns a list of object types that the general balance forwards selector uses to determine if it should process a balance
     *
     * @param fiscalYear
     * @return list of general forward balance object types associated with this fiscal year
     */
    public List<String> getGeneralForwardBalanceObjectTypes(Integer fiscalYear);

    /**
     * Returns a list of object types that the cumulative balance forwards selector uses to determine if it should process a balance
     *
     * @param fiscalYear
     * @return list of cumulative forward balance object types associated with this fiscal year
     */
    public List<String> getCumulativeForwardBalanceObjectTypes(Integer fiscalYear);
    
    /**
     * returns a list of expense object types that are used in determining whether the
     * amount on the accounting line is negated for using in payments.
     * 
     * @return list of expense object types
     */
    public List<String> getExpenseAndTransferObjectTypesForPayments();
    
    /**
     * returns a list of income object types that are used in determining whether the
     * amount on the accounting line is negated for using in payments.
     * 
     * @return list of income object types
     */
    public List<String> getIncomeAndTransferObjectTypesForPayments();
    
}
