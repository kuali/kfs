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

import java.util.Collection;

import org.kuali.kfs.coa.businessobject.SubFundGroup;

/**
 * This service interface defines methods necessary for retrieving fully populated SubFundGroup business objects from the
 * database that are necessary for transaction processing in the application.
 */
public interface SubFundGroupService {

    /**
     * Retrieves a SubFundGroupCode by it's primary key - the sub fund group code.
     * 
     * @param subFundGroupCode
     * @return the sub fund group specfied by its code
     */
    public SubFundGroup getByPrimaryId(String subFundGroupCode);

    /**
     * Retrieves the SubFundGroupCode for the Account with the given chart and account codes.
     * 
     * @param chartCode
     * @param accountNumber
     * @return the sub fund group specified by a chart and and account number
     */
    public SubFundGroup getByChartAndAccount(String chartCode, String accountNumber);

    /**
     * 
     * This checks to see if a particular SubFundGroup is related to Contracts and Grants through its Account
     * @param subFundGroup
     * @return true if it is for contracts and grants
     */
    public boolean isForContractsAndGrants(SubFundGroup subFundGroup);

    /**
     * 
     * This retrieves the attribute label for Contracts and Grants
     * @return string representation of Contracts and Grants label
     */
    public String getContractsAndGrantsDenotingAttributeLabel();

    /**
     * Extracts the appropriate value from the sub fund group for the C&G method selected.
     * 
     * @param subFundGroup
     * @return string representation of either Fund Group code or SubFund Group code
     */
    public String getContractsAndGrantsDenotingValue(SubFundGroup subFundGroup);

    /**
     * 
     * This check is the specified code is related to Contracts and Grants
     * @return string representation of the Contracts and Grants value
     */
    public Collection<String> getContractsAndGrantsDenotingValues();
    
    /**
     * This retrieves the SubFundGroupCodes of Contracts and Grants 
     * @return string representation of Contracts and Grants value formatted for messages
     * This method...
     * @return
     */
    public String getContractsAndGrantsDenotingValueForMessage();
}
