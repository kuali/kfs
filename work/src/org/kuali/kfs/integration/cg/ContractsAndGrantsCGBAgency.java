/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

/**
 * Methods which return information needed about Contracts and Grants agencies by other modules.
 */
public interface ContractsAndGrantsCGBAgency extends MutableInactivatable, ExternalizableBusinessObject {
    /**
     * Returns the agency number for this agency
     *
     * @return the agency number for the given agency
     */
    public abstract String getAgencyNumber();


    /**
     * Gets the fullName attribute.
     *
     * @return Returns the fullName
     */
    public String getFullName();


    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber();


    /**
     * Gets the reportingName attribute.
     *
     * @return Returns the reportingName
     */
    public String getReportingName();


    /**
     * Gets the customerTypeCode attribute.
     *
     * @return Returns the customerTypeCode.
     */
    public String getCustomerTypeCode();


    /**
     * Gets the dunsPlusFourNumber attribute.
     *
     * @return Returns the dunsPlusFourNumber.
     */
    public String getDunsPlusFourNumber();

    /**
     * Gets the customer attribute.
     *
     * @return Returns the customer.
     */
    public AccountsReceivableCustomer getCustomer();

    /**
     * Gets the stateAgency attribute.
     *
     * @return Returns the stateAgency.
     */
    public boolean isStateAgencyIndicator();
}
