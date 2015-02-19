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
package org.kuali.kfs.coa.document.validation.impl;

import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapEmpty;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;


@ConfigureContext(session = khuntley)
public class OrgRuleTest extends ChartRuleTestBase {

    private static final String GOOD_CHART = "UA";
    private static final String BAD_CHART = "ZZ";

    private static class OrgKeys {
        private String chartOfAccountsCode;
        private String organizationCode;
        private String organizationManager;

        private OrgKeys(String chartOfAccountsCode, String organizationCode, String organizationManager) {
            this.chartOfAccountsCode = chartOfAccountsCode;
            this.organizationCode = organizationCode;
            this.organizationManager = organizationManager;
        }

        static OrgKeys getGoodTopLevelOrgKeys() {
            return new OrgKeys(TopLevelOrg.CHART_OF_ACCOUNTS_CODE, TopLevelOrg.ORGANIZATION_CODE, TopLevelOrg.ORGANIZATION_MANAGER);
        }

        static OrgKeys getBadTopLevelOrgKeys() {
            return new OrgKeys(CampusOrg.CHART_OF_ACCOUNTS_CODE, CampusOrg.ORGANIZATION_CODE, CampusOrg.ORGANIZATION_MANAGER);
        }

        static OrgKeys getCampusOrgKeys() {
            return new OrgKeys(CampusOrg.CHART_OF_ACCOUNTS_CODE, CampusOrg.ORGANIZATION_CODE, CampusOrg.ORGANIZATION_MANAGER);
        }

        static OrgKeys getDepartmentOrgKeys() {
            return new OrgKeys(DepartmentOrg.CHART_OF_ACCOUNTS_CODE, DepartmentOrg.ORGANIZATION_CODE, DepartmentOrg.ORGANIZATION_MANAGER);
        }

        private class TopLevelOrg {
            private static final String CHART_OF_ACCOUNTS_CODE = "IU";
            private static final String ORGANIZATION_CODE = "UNIV";
            private static final String ORGANIZATION_MANAGER = "CAMCSWEE";
        }

        private class CampusOrg {
            private static final String CHART_OF_ACCOUNTS_CODE = "FW";
            private static final String ORGANIZATION_CODE = "FW";
            private static final String ORGANIZATION_MANAGER = "MFLANDER";
        }

        private class DepartmentOrg {
            private static final String CHART_OF_ACCOUNTS_CODE = "FW";
            private static final String ORGANIZATION_CODE = "ANTH";
            private static final String ORGANIZATION_MANAGER = "HOATES";
        }
    }

    private OrgRule rule;
    private Organization org;
    private MaintenanceDocument maintDoc;

    public void testDefaultExistenceChecks_chartOfAccounts_good() {
        org = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        maintDoc = this.newMaintDoc(org);
        testDefaultExistenceCheck(org, "organizationCode", false);
        assertGlobalMessageMapEmpty();
    }

    /**
     * This tests makes certain that only one top level heirarchy is allowed.
     */
    public void testCheckSimpleRules_topLevelHeirarchy_noMoreThanOne() {
        Organization oldBO = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        Organization newBO = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getBadTopLevelOrgKeys()));
        maintDoc = this.newMaintDoc(oldBO, newBO);
        maintDoc.getNewMaintainableObject().setMaintenanceAction(KFSConstants.MAINTENANCE_EDIT_ACTION); // simulate editing
        newBO.setReportsToChartOfAccountsCode(OrgKeys.CampusOrg.CHART_OF_ACCOUNTS_CODE); // simulate trying to create a new top
                                                                                            // level org
        newBO.setReportsToOrganizationCode(OrgKeys.CampusOrg.ORGANIZATION_CODE);
        rule = (OrgRule) this.setupMaintDocRule(maintDoc, OrgRule.class);
        assertFalse(rule.checkSimpleRules(maintDoc)); // we may not add more than one top level org
    }

    /**
     * This test makes certain that a top level heirarchy can be edited in a maintenance doc.
     */
    public void testCheckSimpleRules_topLevelHeirarchy_mayEdit() {
        rule = new OrgRule();
        Organization oldBO = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        Organization newBO = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        newBO.setReportsToChartOfAccountsCode(OrgKeys.TopLevelOrg.CHART_OF_ACCOUNTS_CODE); // simulate editing top level org
        newBO.setReportsToOrganizationCode(OrgKeys.TopLevelOrg.ORGANIZATION_CODE);
        maintDoc = this.newMaintDoc(oldBO, newBO);
        maintDoc.getNewMaintainableObject().setMaintenanceAction(KFSConstants.MAINTENANCE_EDIT_ACTION); // simulate editing
        rule = (OrgRule) this.setupMaintDocRule(maintDoc, OrgRule.class);
        assertTrue(rule.checkSimpleRules(maintDoc)); // it is okay to edit the top level org
    }

    /**
     * This method assures that on edit, a university level org does not need a default account number.
     */
    public void testCheckDefaultAccountNumber_canEditUniversity() {
        Organization oldBO = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        Organization newBO = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        newBO.setOrganizationDefaultAccountNumber(null); // simulate no organization default account number
        maintDoc = this.newMaintDoc(oldBO, newBO);
        maintDoc.getNewMaintainableObject().setMaintenanceAction(KFSConstants.MAINTENANCE_EDIT_ACTION); // simulate editing
        rule = (OrgRule) this.setupMaintDocRule(maintDoc, OrgRule.class);
        assertTrue(rule.checkDefaultAccountNumber(maintDoc)); // it is okay for a university to have no default account number
    }

    /**
     * This method assures that on edit, a campus level org does not need a default account number.
     */
    public void testCheckDefaultAccountNumber_canEditCampus() {
        Organization oldBO = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getCampusOrgKeys()));
        Organization newBO = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getCampusOrgKeys()));
        newBO.setOrganizationDefaultAccountNumber(null); // simulate no organization default account number
        maintDoc = this.newMaintDoc(oldBO, newBO);
        maintDoc.getNewMaintainableObject().setMaintenanceAction(KFSConstants.MAINTENANCE_EDIT_ACTION); // simulate editing
        rule = (OrgRule) this.setupMaintDocRule(maintDoc, OrgRule.class);
        assertTrue(rule.checkDefaultAccountNumber(maintDoc)); // it is okay for a campus to have no default account number
    }

    /**
     * This method assures that on edit, a non-university/non-campus *does* need a default account number.
     */
    public void testCheckDefaultAccountNumber_cannotEditDepartment() {
        Organization oldBO = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getDepartmentOrgKeys()));
        Organization newBO = (Organization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Organization.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getDepartmentOrgKeys()));
        newBO.setOrganizationDefaultAccountNumber(null); // simulate no organization default account number
        maintDoc = this.newMaintDoc(oldBO, newBO);
        maintDoc.getNewMaintainableObject().setMaintenanceAction(KFSConstants.MAINTENANCE_EDIT_ACTION); // simulate editing
        rule = (OrgRule) this.setupMaintDocRule(maintDoc, OrgRule.class);
        assertFalse(rule.checkDefaultAccountNumber(maintDoc)); // it is NOT okay for a non-university/non-campus to have no default
                                                                // account number
    }

    private Map getPrimaryKeysForTopLevelOrg(OrgKeys org) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("chartOfAccountsCode", org.chartOfAccountsCode);
        primaryKeys.put("organizationCode", org.organizationCode);
        return primaryKeys;
    }
}

