/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.struts;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;


/**
 * This class is the action form for Referral To Collections Report.
 */
public class ReferralToCollectionsReportForm extends ContractsGrantsReportLookupForm {

    private static final long serialVersionUID = 1L;
    private String principalId;

    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;
    private Person collector;


    /**
     * Default constructor.
     */
    public ReferralToCollectionsReportForm() {
        setHtmlFormAction("referralToCollectionsReportLookup");
    }

    /**
     * Gets the principal Id of collector.
     * 
     * @return Returns the principal id of collector.
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principal id of collector.
     * 
     * @param principalId The principal id of collector to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Gets the lookup role namespace code of user.
     * 
     * @return Returns lookup role namespace code.
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Gets the lookup role name of user.
     * 
     * @return Returns the lookup role name.
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    /**
     * Gets the collector object for given principal id.
     * 
     * @return Returns the collector object of principal id.
     */
    public Person getCollector() {
        collector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, collector);
        return collector;
    }

    /**
     * Sets the collector object.
     * 
     * @param collector The collector object to set.
     */
    public void setCollector(Person collector) {
        this.collector = collector;
    }

}
