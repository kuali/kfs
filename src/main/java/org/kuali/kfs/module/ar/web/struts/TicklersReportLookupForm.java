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
package org.kuali.kfs.module.ar.web.struts;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Lookup form for Ticklers Report.
 */
public class TicklersReportLookupForm extends ContractsGrantsReportLookupForm {

    private String principalId;

    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;
    private Person collector;

    /**
     * Default constructor.
     */
    public TicklersReportLookupForm() {
        setHtmlFormAction(ArConstants.Actions.AR_TICKLER_REPORT);
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
