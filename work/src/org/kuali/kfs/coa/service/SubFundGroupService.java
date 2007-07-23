/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.module.chart.bo.SubFundGroup;

public interface SubFundGroupService {

    /**
     * Retrieves a SubFundGroupCode by it's primary key - the sub fund group code.
     * 
     * @param subFundGroupCode
     * @return
     */
    public SubFundGroup getByPrimaryId(String subFundGroupCode);

    /**
     * Retrieves the SubFundGroupCode for the Account with the given chart and account codes.
     * 
     * @param chartCode
     * @param accountNumber
     * @return
     */
    public SubFundGroup getByChartAndAccount(String chartCode, String accountNumber);
    
    public boolean isForContractsAndGrants(SubFundGroup subFundGroup);
    
    public String getContractsAndGrantsDenotingAttributeLabel();
    
    /**
     * Extracts the appropriate value from the sub fund group for the C&G method selected. 
     * 
     * @param subFundGroup
     * @return
     */
    public String getContractsAndGrantsDenotingValue( SubFundGroup subFundGroup );
    
    public String getContractsAndGrantsDenotingValue();
}
