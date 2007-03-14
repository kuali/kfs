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
package org.kuali.module.chart.rules;

import java.util.Map;
import java.util.HashMap;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Org;
import org.kuali.test.WithTestSpringContext;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapEmpty;
import org.kuali.Constants;


@WithTestSpringContext(session=KHUNTLEY)
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
    private Org org;
    private MaintenanceDocument maintDoc;

    public void testDefaultExistenceChecks_chartOfAccounts_good() {
        org = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        maintDoc = this.newMaintDoc(org);
        testDefaultExistenceCheck(org, "organizationCode", false);
        assertGlobalErrorMapEmpty();
    }
    
    /**
     * 
     * This tests makes certain that only one top level heirarchy is allowed.
     */
    public void testCheckSimpleRules_topLevelHeirarchy_noMoreThanOne() {
        Org oldBO = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        Org newBO = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getBadTopLevelOrgKeys()));
        maintDoc = this.newMaintDoc(oldBO, newBO);
        maintDoc.getNewMaintainableObject().setMaintenanceAction(Constants.MAINTENANCE_EDIT_ACTION); // simulate editing
        newBO.setReportsToChartOfAccountsCode(OrgKeys.CampusOrg.CHART_OF_ACCOUNTS_CODE); // simulate trying to create a new top level org
        newBO.setReportsToOrganizationCode(OrgKeys.CampusOrg.ORGANIZATION_CODE);
        rule = (OrgRule)this.setupMaintDocRule(maintDoc, OrgRule.class);
        assertFalse(rule.checkSimpleRules(maintDoc)); // we may not add more than one top level org
    }
    
    /**
     * 
     * This test makes certain that a top level heirarchy can be edited in a maintenance doc.
     */
    public void testCheckSimpleRules_topLevelHeirarchy_mayEdit() {
        rule = new OrgRule();
        Org oldBO = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        Org newBO = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        newBO.setReportsToChartOfAccountsCode(OrgKeys.TopLevelOrg.CHART_OF_ACCOUNTS_CODE); // simulate editing top level org
        newBO.setReportsToOrganizationCode(OrgKeys.TopLevelOrg.ORGANIZATION_CODE);
        maintDoc = this.newMaintDoc(oldBO, newBO);
        maintDoc.getNewMaintainableObject().setMaintenanceAction(Constants.MAINTENANCE_EDIT_ACTION); // simulate editing
        rule = (OrgRule)this.setupMaintDocRule(maintDoc, OrgRule.class);
        assertTrue(rule.checkSimpleRules(maintDoc)); // it is okay to edit the top level org
    }
    
    /**
     * 
     * This method assures that on edit, a university level org does not need a default account number.
     */
    public void testCheckDefaultAccountNumber_canEditUniversity() {
        Org oldBO = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        Org newBO = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getGoodTopLevelOrgKeys()));
        newBO.setOrganizationDefaultAccountNumber(null); // simulate no organization default account number
        maintDoc = this.newMaintDoc(oldBO, newBO);
        maintDoc.getNewMaintainableObject().setMaintenanceAction(Constants.MAINTENANCE_EDIT_ACTION); // simulate editing
        rule = (OrgRule)this.setupMaintDocRule(maintDoc, OrgRule.class);
        assertTrue(rule.checkDefaultAccountNumber(maintDoc)); // it is okay for a university to have no default account number
    }
    
    /**
     * 
     * This method assures that on edit, a campus level org does not need a default account number.
     */
    public void testCheckDefaultAccountNumber_canEditCampus() {
        Org oldBO = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getCampusOrgKeys()));
        Org newBO = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getCampusOrgKeys()));
        newBO.setOrganizationDefaultAccountNumber(null); // simulate no organization default account number
        maintDoc = this.newMaintDoc(oldBO, newBO);
        maintDoc.getNewMaintainableObject().setMaintenanceAction(Constants.MAINTENANCE_EDIT_ACTION); // simulate editing
        rule = (OrgRule)this.setupMaintDocRule(maintDoc, OrgRule.class);
        assertTrue(rule.checkDefaultAccountNumber(maintDoc)); // it is okay for a campus to have no default account number
    }
    
    /**
     * 
     * This method assures that on edit, a non-university/non-campus *does* need a default account number.
     */
    public void testCheckDefaultAccountNumber_cannotEditDepartment() {
        Org oldBO = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getDepartmentOrgKeys()));
        Org newBO = (Org)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Org.class, this.getPrimaryKeysForTopLevelOrg(OrgKeys.getDepartmentOrgKeys()));
        newBO.setOrganizationDefaultAccountNumber(null); // simulate no organization default account number
        maintDoc = this.newMaintDoc(oldBO, newBO);
        maintDoc.getNewMaintainableObject().setMaintenanceAction(Constants.MAINTENANCE_EDIT_ACTION); // simulate editing
        rule = (OrgRule)this.setupMaintDocRule(maintDoc, OrgRule.class);
        assertFalse(rule.checkDefaultAccountNumber(maintDoc)); // it is NOT okay for a non-university/non-campus to have no default account number
    }
    
    private Map getPrimaryKeysForTopLevelOrg(OrgKeys org) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("chartOfAccountsCode", org.chartOfAccountsCode);
        primaryKeys.put("organizationCode", org.organizationCode);
        return primaryKeys;
    }
}
