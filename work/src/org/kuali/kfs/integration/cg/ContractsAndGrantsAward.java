/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.integration.cg;


import org.kuali.rice.kns.bo.ExternalizableBusinessObject;


public interface ContractsAndGrantsAward extends ExternalizableBusinessObject {

    public Long getProposalNumber();

    public ContractAndGrantsProposal getProposal();
    
    public String getWorkgroupName();
    
    /**
     * Retrieves the list of users assigned to the associated workgroup and builds out a string representation of these users for
     * display purposes. NOTE: This method is used by the Account and Award Inquiry screens to display users of the associated
     * workgroup. NOTE: This method currently has not other use outside of the Account Inquiry screen.
     * 
     * @return String representation of the users assigned to the associated workgroup.
     */
    public String getKimGroupNames();
}

