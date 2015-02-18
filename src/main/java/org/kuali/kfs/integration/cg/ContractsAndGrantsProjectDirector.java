/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.integration.cg;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;



/**
 * This interface defines all the necessary methods to define a Contracts & Grants project director object.
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
    @Deprecated
    public void setProjectDirector(Person projectDirector);

}
