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
package org.kuali.module.kra.service;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.ObjectUtils;

import static org.kuali.kfs.util.SpringServiceLocator.getBudgetCostShareService;

import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetInstitutionCostShare;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.InstitutionCostSharePersonnel;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.CrossSectionSuite;
import static org.kuali.test.util.KualiTestAssertionUtils.assertEquality;

/**
 * This class tests service methods in BudgetCostShareService.
 * 
 * 
 */
@AnnotationTestSuite(CrossSectionSuite.class)
@WithTestSpringContext
public class BudgetCostShareServiceTest extends KualiTestBase {

    public void testCleanseCostShare() {
        String documentHeaderId = "160642";

        boolean universityCostShareIndicator = true;
        List<BudgetInstitutionCostShare> budgetInstitutionCostShare = new ArrayList();
        boolean budgetThirdPartyCostShareIndicator = true;
        List<BudgetThirdPartyCostShare> budgetThirdPartyCostShare = new ArrayList();
        List<BudgetUser> personnel = new ArrayList();
        List<InstitutionCostSharePersonnel> institutionCostSharePersonnel = new ArrayList();

        // CONDITION: cost share TRUE, all empty lists
        getBudgetCostShareService().cleanseCostShare(universityCostShareIndicator, budgetInstitutionCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, institutionCostSharePersonnel);
        // RESULT: no data change, but no crashes either
        assertTrue("universityCostShareIndicator should never be changed", universityCostShareIndicator);
        assertTrue("budgetInstitutionCostShare should still be empty", budgetInstitutionCostShare.size() == 0);
        assertTrue("budgetThirdPartyCostShareIndicator should never be changed", budgetThirdPartyCostShareIndicator);
        assertTrue("budgetThirdPartyCostShare should still be empty", budgetThirdPartyCostShare.size() == 0);

        budgetInstitutionCostShare.add(new BudgetInstitutionCostShare());
        budgetThirdPartyCostShare.add(new BudgetThirdPartyCostShare());

        // CONDITION: cost share TRUE, lists have values
        getBudgetCostShareService().cleanseCostShare(universityCostShareIndicator, budgetInstitutionCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, institutionCostSharePersonnel);
        // RESULT: no data change
        assertTrue("universityCostShareIndicator should never be changed", universityCostShareIndicator);
        assertTrue("budgetInstitutionCostShare should still contain 1 item", budgetInstitutionCostShare.size() == 1);
        assertTrue("budgetThirdPartyCostShareIndicator should never be changed", budgetThirdPartyCostShareIndicator);
        assertTrue("budgetInstitutionCostShare should still contain 1 item", budgetThirdPartyCostShare.size() == 1);

        universityCostShareIndicator = false;

        // CONDITION: cost share TRUE, lists have values
        getBudgetCostShareService().cleanseCostShare(universityCostShareIndicator, budgetInstitutionCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, institutionCostSharePersonnel);
        // RESULT: no data change
        assertFalse("universityCostShareIndicator should never be changed", universityCostShareIndicator);
        assertTrue("budgetInstitutionCostShare should now be empty", budgetInstitutionCostShare.size() == 0);
        assertTrue("budgetThirdPartyCostShareIndicator should never be changed", budgetThirdPartyCostShareIndicator);
        assertTrue("budgetInstitutionCostShare should still contain 1 item", budgetThirdPartyCostShare.size() == 1);

        universityCostShareIndicator = true;
        budgetInstitutionCostShare.add(new BudgetInstitutionCostShare());
        budgetThirdPartyCostShareIndicator = false;

        // CONDITION: cost share TRUE, lists have values
        getBudgetCostShareService().cleanseCostShare(universityCostShareIndicator, budgetInstitutionCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, institutionCostSharePersonnel);
        // RESULT: no data change
        assertTrue("universityCostShareIndicator should never be changed", universityCostShareIndicator);
        assertTrue("budgetInstitutionCostShare should still contain 1 item", budgetInstitutionCostShare.size() == 1);
        assertFalse("budgetThirdPartyCostShareIndicator should never be changed", budgetThirdPartyCostShareIndicator);
        assertTrue("budgetThirdPartyCostShare should now be empty", budgetThirdPartyCostShare.size() == 0);


        // CONDITION: cost share TRUE, all empty lists
        getBudgetCostShareService().cleanseCostShare(universityCostShareIndicator, budgetInstitutionCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, institutionCostSharePersonnel);
        // RESULT: no data change, but no crashes either
        assertTrue("personnel should never be changed", personnel.size() == 0);
        assertTrue("institutionCostSharePersonnel should still be empty", institutionCostSharePersonnel.size() == 0);

        // Create a but if data
        BudgetUser budgetUser1 = new BudgetUser();
        budgetUser1.setFiscalCampusCode("BL");
        budgetUser1.setPrimaryDepartmentCode("CSCI");
        personnel.add(budgetUser1);

        InstitutionCostSharePersonnel universityCostSharePerson1 = new InstitutionCostSharePersonnel();
        universityCostSharePerson1.setDocumentNumber("160642");
        universityCostSharePerson1.setChartOfAccountsCode("BL");
        universityCostSharePerson1.setOrganizationCode("CSCI");

        BudgetUser budgetUser2 = new BudgetUser();
        budgetUser2.setFiscalCampusCode("IN");
        budgetUser2.setPrimaryDepartmentCode("CARD");
        personnel.add(budgetUser2);

        InstitutionCostSharePersonnel universityCostSharePerson2 = new InstitutionCostSharePersonnel();
        universityCostSharePerson2.setDocumentNumber("160642");
        universityCostSharePerson2.setChartOfAccountsCode("IN");
        universityCostSharePerson2.setOrganizationCode("CARD");

        // Add data to institutionCostSharePersonnel (this assumes testReconcileCostShare runs clean).
        getBudgetCostShareService().reconcileCostShare(documentHeaderId, personnel, institutionCostSharePersonnel);

        // CONDITION: cost share TRUE, personnel 2 items, institutionCostSharePersonnel same 2 items
        getBudgetCostShareService().cleanseCostShare(universityCostShareIndicator, budgetInstitutionCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, institutionCostSharePersonnel);
        // RESULT: no data change
        assertTrue("personnel should never be changed", personnel.size() == 2);
        assertTrue("institutionCostSharePersonnel should still contain 2 items", institutionCostSharePersonnel.size() == 2);

        BudgetUser budgetUser3 = new BudgetUser();
        budgetUser3.setFiscalCampusCode(null);
        budgetUser3.setPrimaryDepartmentCode(null);
        personnel.add(budgetUser3);

        BudgetUser budgetUser4 = new BudgetUser();
        budgetUser4.setFiscalCampusCode("");
        budgetUser4.setPrimaryDepartmentCode("");
        personnel.add(budgetUser4);

        // CONDITION: Same as above but null / empty data was introduced.
        getBudgetCostShareService().cleanseCostShare(universityCostShareIndicator, budgetInstitutionCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, institutionCostSharePersonnel);
        // RESULT: Same as above, no crash should occur.
        assertTrue("personnel should never be changed", personnel.size() == 4);
        assertTrue("institutionCostSharePersonnel should still contain 2 items", institutionCostSharePersonnel.size() == 2);

        personnel.remove(0);

        // CONDITION: The BL/CSCI entry was removed.
        getBudgetCostShareService().cleanseCostShare(universityCostShareIndicator, budgetInstitutionCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, institutionCostSharePersonnel);
        // RESULT: The BL/CSCI entry shouldn't be in institutionCostSharePersonnel anymore
        assertTrue("personnel should never be changed", personnel.size() == 3);
        assertTrue("institutionCostSharePersonnel should only contain 1 item now", institutionCostSharePersonnel.size() == 1);
        assertFalse("BL/CSCI entry should not be in institutionCostSharePersonnel anymore.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(institutionCostSharePersonnel, universityCostSharePerson1));
        assertTrue("IN/CARD entry should still be in institutionCostSharePersonnel.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(institutionCostSharePersonnel, universityCostSharePerson2));
    }

    public void testReconcileCostShare() {
        String documentHeaderId = "160642";
        List<BudgetUser> personnel = new ArrayList();
        List<InstitutionCostSharePersonnel> institutionCostSharePersonnel = new ArrayList();

        // CONDITION: no personnel, no cost share
        getBudgetCostShareService().reconcileCostShare(documentHeaderId, personnel, institutionCostSharePersonnel);
        // RESULT: no personnel, no cost share
        assertEquality(documentHeaderId, "160642");
        assertTrue("Should still be empty.", personnel.isEmpty());
        assertTrue("Should still be empty.", institutionCostSharePersonnel.isEmpty());

        BudgetUser budgetUser1 = new BudgetUser();
        budgetUser1.setFiscalCampusCode("BL");
        budgetUser1.setPrimaryDepartmentCode("CSCI");
        personnel.add(budgetUser1);

        InstitutionCostSharePersonnel universityCostSharePerson1 = new InstitutionCostSharePersonnel();
        universityCostSharePerson1.setDocumentNumber("160642");
        universityCostSharePerson1.setChartOfAccountsCode("BL");
        universityCostSharePerson1.setOrganizationCode("CSCI");

        // CONDITION: 1 person, no cost share
        getBudgetCostShareService().reconcileCostShare(documentHeaderId, personnel, institutionCostSharePersonnel);
        // RESULT: 1 person, 1 cost share with identical keys
        assertEquality(documentHeaderId, "160642");
        assertTrue("Person list should not be changed.", personnel.size() == 1);
        assertTrue("Person's chart/org should have been added.", institutionCostSharePersonnel.size() == 1);
        assertTrue("Identical chart/org should have been found.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(institutionCostSharePersonnel, universityCostSharePerson1));

        personnel.add(budgetUser1);

        // CONDITION: 2 person, 1 cost share (same keys)
        getBudgetCostShareService().reconcileCostShare(documentHeaderId, personnel, institutionCostSharePersonnel);
        // RESULT: 2 person, 1 cost share with identical keys (ie. item not added again)
        assertEquality(documentHeaderId, "160642");
        assertTrue("Person list should not be changed.", personnel.size() == 2);
        assertTrue("Since the second person's chart/org was identical, it should not be added again.", institutionCostSharePersonnel.size() == 1);
        assertTrue("Identical chart/org should have been found.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(institutionCostSharePersonnel, universityCostSharePerson1));

        BudgetUser budgetUser2 = new BudgetUser();
        budgetUser2.setFiscalCampusCode("IN");
        budgetUser2.setPrimaryDepartmentCode("CARD");
        personnel.add(budgetUser2);

        InstitutionCostSharePersonnel universityCostSharePerson2 = new InstitutionCostSharePersonnel();
        universityCostSharePerson2.setDocumentNumber("160642");
        universityCostSharePerson2.setChartOfAccountsCode("IN");
        universityCostSharePerson2.setOrganizationCode("CARD");

        // CONDITION: 3 people, 1 cost share (one key same)
        getBudgetCostShareService().reconcileCostShare(documentHeaderId, personnel, institutionCostSharePersonnel);
        // RESULT: 3 people, 2 cost share with identical keys
        assertEquality(documentHeaderId, "160642");
        assertTrue("Person list should not be changed.", personnel.size() == 3);
        assertTrue("Person had new chart/org, should have been added.", institutionCostSharePersonnel.size() == 2);
        assertTrue("Original person's chart/org should still be there.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(institutionCostSharePersonnel, universityCostSharePerson1));
        assertTrue("New person's chart/org should have been added.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(institutionCostSharePersonnel, universityCostSharePerson2));

        BudgetUser budgetUser3 = new BudgetUser();
        budgetUser3.setFiscalCampusCode(null);
        budgetUser3.setPrimaryDepartmentCode(null);
        personnel.add(budgetUser3);

        BudgetUser budgetUser4 = new BudgetUser();
        budgetUser4.setFiscalCampusCode("");
        budgetUser4.setPrimaryDepartmentCode("");
        personnel.add(budgetUser4);

        // CONDITION: 5 people, 2 cost share (two keys same)
        getBudgetCostShareService().reconcileCostShare(documentHeaderId, personnel, institutionCostSharePersonnel);
        // RESULT: 5 people, 2 cost share with identical keys
        assertEquality(documentHeaderId, "160642");
        assertTrue("Person list should not be changed.", personnel.size() == 5);
        assertTrue("There should have been no change to chart/org since people added were null org empty string.", institutionCostSharePersonnel.size() == 2);
        assertTrue("Original person's chart/org should still be there.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(institutionCostSharePersonnel, universityCostSharePerson1));
        assertTrue("Second original person's chart/org should have been added.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(institutionCostSharePersonnel, universityCostSharePerson2));
    }
}
