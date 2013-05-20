/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.integration.cg;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;


/**
 * This interface defines all the necessary methods to define a contracts and grants project director object.
 */
public interface ContractsAndGrantsProjectDirector extends ExternalizableBusinessObject {

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId();

    /**
     * Sets the principalId attribute.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId);

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber();

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber);

    /**
     * Gets the project director attribute.
     * 
     * @return the projectDirector.
     */
    public Person getProjectDirector();

    /**
     * Sets the projectDirector.
     * 
     * @param projectDirector the projectDirector to set
     * @deprecated required by PersonServiceImpl.isPersonProperty() for PojoPropertyUtilsBean.getPropertyDescriptor()
     */
    public void setProjectDirector(Person projectDirector);

}
