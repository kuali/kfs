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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;

/**
 * That the FinancialSystemUserService works as expected
 */
@ConfigureContext
public class FinancialSystemUserServiceTest extends KualiTestBase {
    protected final static String ACTIVE_SYSTEM_USER_PRINCIPAL_NAME = "kcopley";
    protected final static String NON_ACTIVE_SYSTEM_USER_PRINCIPAL_NAME = "if_this_ever_becomes_a_principal_id_in_the_database_well_that_would_be_weird_wouldnt_it";
    protected final static String DEFAULT_NAMESPACE_CODE = "KFS-SYS";

    public void testIsActiveFinancialSystemUserPrincipal() {
        final FinancialSystemUserService fsUserService = SpringContext.getBean(FinancialSystemUserService.class);
        assertTrue(ACTIVE_SYSTEM_USER_PRINCIPAL_NAME+" should be an active Financial System user", fsUserService.isActiveFinancialSystemUser(getActiveSystemUserPrincipalId()));
        assertFalse(NON_ACTIVE_SYSTEM_USER_PRINCIPAL_NAME+" should not be an active Financial System User", fsUserService.isActiveFinancialSystemUser(NON_ACTIVE_SYSTEM_USER_PRINCIPAL_NAME));
    }

//    public void testIsActiveFinancialSystemUserPerson() {
//        final FinancialSystemUserService fsUserService = SpringContext.getBean(FinancialSystemUserService.class);
//        final Person activeFinancialSystemUser = getActiveSystemUserPerson();
//        assertTrue("Even as a person, "+ACTIVE_SYSTEM_USER_PRINCIPAL_NAME+" should still be an active financial system user", fsUserService.isActiveFinancialSystemUser(activeFinancialSystemUser));
//    }

    public void testPrimaryOrganizationPrincipal() {
        final FinancialSystemUserService fsUserService = SpringContext.getBean(FinancialSystemUserService.class);
        final ChartOrgHolder primaryOrganization = fsUserService.getPrimaryOrganization(getActiveSystemUserPrincipalId(), DEFAULT_NAMESPACE_CODE);
        assertEquals("Chart should be 'BL'", "BL", primaryOrganization.getChartOfAccountsCode());
        assertEquals("Organization should be 'BL'", "BL", primaryOrganization.getOrganizationCode());
    }

    public void testPrimaryOrganizationPerson() {
        final FinancialSystemUserService fsUserService = SpringContext.getBean(FinancialSystemUserService.class);
        final ChartOrgHolder primaryOrganization = fsUserService.getPrimaryOrganization(getActiveSystemUserPerson(), DEFAULT_NAMESPACE_CODE);
        assertEquals("Chart should be 'BL'", "BL", primaryOrganization.getChartOfAccountsCode());
        assertEquals("Organization should be 'BL'", "BL", primaryOrganization.getOrganizationCode());
    }

    protected Person getActiveSystemUserPerson() {
        final PersonService personService = SpringContext.getBean(PersonService.class);
        final Person activeSystemUserPerson = personService.getPersonByPrincipalName(ACTIVE_SYSTEM_USER_PRINCIPAL_NAME);
        return activeSystemUserPerson;
    }

    protected String getActiveSystemUserPrincipalId() {
        final Person activeSystemUserPerson = getActiveSystemUserPerson();
        return activeSystemUserPerson.getPrincipalId();

    }
}
