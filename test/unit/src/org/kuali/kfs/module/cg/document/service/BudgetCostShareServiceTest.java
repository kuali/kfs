/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.service;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetUniversityCostShare;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.UniversityCostSharePersonnel;
import org.kuali.module.kra.budget.service.BudgetCostShareService;
import org.kuali.test.KualiTestBaseWithSession;

/**
 * This class tests service methods in BudgetCostShareService.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetCostShareServiceTest extends KualiTestBaseWithSession {

    private BudgetCostShareService budgetCostShareService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        budgetCostShareService = SpringServiceLocator.getBudgetCostShareService();
    }

    public void testCleanseCostShare() {
        String documentHeaderId = "160642";

        boolean universityCostShareIndicator = true;
        List<BudgetUniversityCostShare> budgetUniversityCostShare = new ArrayList();
        boolean budgetThirdPartyCostShareIndicator = true;
        List<BudgetThirdPartyCostShare> budgetThirdPartyCostShare = new ArrayList();
        List<BudgetUser> personnel = new ArrayList();
        List<UniversityCostSharePersonnel> universityCostSharePersonnel = new ArrayList();

        // CONDITION: cost share TRUE, all empty lists
        budgetCostShareService.cleanseCostShare(universityCostShareIndicator, budgetUniversityCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, universityCostSharePersonnel);
        // RESULT: no data change, but no crashes either
        assertTrue("universityCostShareIndicator should never be changed", universityCostShareIndicator);
        assertTrue("budgetUniversityCostShare should still be empty", budgetUniversityCostShare.size() == 0);
        assertTrue("budgetThirdPartyCostShareIndicator should never be changed", budgetThirdPartyCostShareIndicator);
        assertTrue("budgetThirdPartyCostShare should still be empty", budgetThirdPartyCostShare.size() == 0);

        budgetUniversityCostShare.add(new BudgetUniversityCostShare());
        budgetThirdPartyCostShare.add(new BudgetThirdPartyCostShare());

        // CONDITION: cost share TRUE, lists have values
        budgetCostShareService.cleanseCostShare(universityCostShareIndicator, budgetUniversityCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, universityCostSharePersonnel);
        // RESULT: no data change
        assertTrue("universityCostShareIndicator should never be changed", universityCostShareIndicator);
        assertTrue("budgetUniversityCostShare should still contain 1 item", budgetUniversityCostShare.size() == 1);
        assertTrue("budgetThirdPartyCostShareIndicator should never be changed", budgetThirdPartyCostShareIndicator);
        assertTrue("budgetUniversityCostShare should still contain 1 item", budgetThirdPartyCostShare.size() == 1);

        universityCostShareIndicator = false;

        // CONDITION: cost share TRUE, lists have values
        budgetCostShareService.cleanseCostShare(universityCostShareIndicator, budgetUniversityCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, universityCostSharePersonnel);
        // RESULT: no data change
        assertFalse("universityCostShareIndicator should never be changed", universityCostShareIndicator);
        assertTrue("budgetUniversityCostShare should now be empty", budgetUniversityCostShare.size() == 0);
        assertTrue("budgetThirdPartyCostShareIndicator should never be changed", budgetThirdPartyCostShareIndicator);
        assertTrue("budgetUniversityCostShare should still contain 1 item", budgetThirdPartyCostShare.size() == 1);

        universityCostShareIndicator = true;
        budgetUniversityCostShare.add(new BudgetUniversityCostShare());
        budgetThirdPartyCostShareIndicator = false;

        // CONDITION: cost share TRUE, lists have values
        budgetCostShareService.cleanseCostShare(universityCostShareIndicator, budgetUniversityCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, universityCostSharePersonnel);
        // RESULT: no data change
        assertTrue("universityCostShareIndicator should never be changed", universityCostShareIndicator);
        assertTrue("budgetUniversityCostShare should still contain 1 item", budgetUniversityCostShare.size() == 1);
        assertFalse("budgetThirdPartyCostShareIndicator should never be changed", budgetThirdPartyCostShareIndicator);
        assertTrue("budgetThirdPartyCostShare should now be empty", budgetThirdPartyCostShare.size() == 0);


        // CONDITION: cost share TRUE, all empty lists
        budgetCostShareService.cleanseCostShare(universityCostShareIndicator, budgetUniversityCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, universityCostSharePersonnel);
        // RESULT: no data change, but no crashes either
        assertTrue("personnel should never be changed", personnel.size() == 0);
        assertTrue("universityCostSharePersonnel should still be empty", universityCostSharePersonnel.size() == 0);

        // Create a but if data
        BudgetUser budgetUser1 = new BudgetUser();
        budgetUser1.setFiscalCampusCode("BL");
        budgetUser1.setPrimaryDepartmentCode("CSCI");
        personnel.add(budgetUser1);

        UniversityCostSharePersonnel universityCostSharePerson1 = new UniversityCostSharePersonnel();
        universityCostSharePerson1.setDocumentHeaderId("160642");
        universityCostSharePerson1.setChartOfAccountsCode("BL");
        universityCostSharePerson1.setOrganizationCode("CSCI");

        BudgetUser budgetUser2 = new BudgetUser();
        budgetUser2.setFiscalCampusCode("IN");
        budgetUser2.setPrimaryDepartmentCode("CARD");
        personnel.add(budgetUser2);

        UniversityCostSharePersonnel universityCostSharePerson2 = new UniversityCostSharePersonnel();
        universityCostSharePerson2.setDocumentHeaderId("160642");
        universityCostSharePerson2.setChartOfAccountsCode("IN");
        universityCostSharePerson2.setOrganizationCode("CARD");

        // Add data to universityCostSharePersonnel (this assumes testReconcileCostShare runs clean).
        budgetCostShareService.reconcileCostShare(documentHeaderId, personnel, universityCostSharePersonnel);

        // CONDITION: cost share TRUE, personnel 2 items, universityCostSharePersonnel same 2 items
        budgetCostShareService.cleanseCostShare(universityCostShareIndicator, budgetUniversityCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, universityCostSharePersonnel);
        // RESULT: no data change
        assertTrue("personnel should never be changed", personnel.size() == 2);
        assertTrue("universityCostSharePersonnel should still contain 2 items", universityCostSharePersonnel.size() == 2);

        BudgetUser budgetUser3 = new BudgetUser();
        budgetUser3.setFiscalCampusCode(null);
        budgetUser3.setPrimaryDepartmentCode(null);
        personnel.add(budgetUser3);

        BudgetUser budgetUser4 = new BudgetUser();
        budgetUser4.setFiscalCampusCode("");
        budgetUser4.setPrimaryDepartmentCode("");
        personnel.add(budgetUser4);

        // CONDITION: Same as above but null / empty data was introduced.
        budgetCostShareService.cleanseCostShare(universityCostShareIndicator, budgetUniversityCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, universityCostSharePersonnel);
        // RESULT: Same as above, no crash should occur.
        assertTrue("personnel should never be changed", personnel.size() == 4);
        assertTrue("universityCostSharePersonnel should still contain 2 items", universityCostSharePersonnel.size() == 2);

        personnel.remove(0);

        // CONDITION: The BL/CSCI entry was removed.
        budgetCostShareService.cleanseCostShare(universityCostShareIndicator, budgetUniversityCostShare, budgetThirdPartyCostShareIndicator, budgetThirdPartyCostShare, personnel, universityCostSharePersonnel);
        // RESULT: The BL/CSCI entry shouldn't be in universityCostSharePersonnel anymore
        assertTrue("personnel should never be changed", personnel.size() == 3);
        assertTrue("universityCostSharePersonnel should only contain 1 item now", universityCostSharePersonnel.size() == 1);
        assertFalse("BL/CSCI entry should not be in universityCostSharePersonnel anymore.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(universityCostSharePersonnel, universityCostSharePerson1));
        assertTrue("IN/CARD entry should still be in universityCostSharePersonnel.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(universityCostSharePersonnel, universityCostSharePerson2));
    }

    public void testReconcileCostShare() {
        String documentHeaderId = "160642";
        List<BudgetUser> personnel = new ArrayList();
        List<UniversityCostSharePersonnel> universityCostSharePersonnel = new ArrayList();

        // CONDITION: no personnel, no cost share
        budgetCostShareService.reconcileCostShare(documentHeaderId, personnel, universityCostSharePersonnel);
        // RESULT: no personnel, no cost share
        assertEquality(documentHeaderId, "160642");
        assertTrue("Should still be empty.", personnel.isEmpty());
        assertTrue("Should still be empty.", universityCostSharePersonnel.isEmpty());

        BudgetUser budgetUser1 = new BudgetUser();
        budgetUser1.setFiscalCampusCode("BL");
        budgetUser1.setPrimaryDepartmentCode("CSCI");
        personnel.add(budgetUser1);

        UniversityCostSharePersonnel universityCostSharePerson1 = new UniversityCostSharePersonnel();
        universityCostSharePerson1.setDocumentHeaderId("160642");
        universityCostSharePerson1.setChartOfAccountsCode("BL");
        universityCostSharePerson1.setOrganizationCode("CSCI");

        // CONDITION: 1 person, no cost share
        budgetCostShareService.reconcileCostShare(documentHeaderId, personnel, universityCostSharePersonnel);
        // RESULT: 1 person, 1 cost share with identical keys
        assertEquality(documentHeaderId, "160642");
        assertTrue("Person list should not be changed.", personnel.size() == 1);
        assertTrue("Person's chart/org should have been added.", universityCostSharePersonnel.size() == 1);
        assertTrue("Identical chart/org should have been found.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(universityCostSharePersonnel, universityCostSharePerson1));

        personnel.add(budgetUser1);

        // CONDITION: 2 person, 1 cost share (same keys)
        budgetCostShareService.reconcileCostShare(documentHeaderId, personnel, universityCostSharePersonnel);
        // RESULT: 2 person, 1 cost share with identical keys (ie. item not added again)
        assertEquality(documentHeaderId, "160642");
        assertTrue("Person list should not be changed.", personnel.size() == 2);
        assertTrue("Since the second person's chart/org was identical, it should not be added again.", universityCostSharePersonnel.size() == 1);
        assertTrue("Identical chart/org should have been found.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(universityCostSharePersonnel, universityCostSharePerson1));

        BudgetUser budgetUser2 = new BudgetUser();
        budgetUser2.setFiscalCampusCode("IN");
        budgetUser2.setPrimaryDepartmentCode("CARD");
        personnel.add(budgetUser2);

        UniversityCostSharePersonnel universityCostSharePerson2 = new UniversityCostSharePersonnel();
        universityCostSharePerson2.setDocumentHeaderId("160642");
        universityCostSharePerson2.setChartOfAccountsCode("IN");
        universityCostSharePerson2.setOrganizationCode("CARD");

        // CONDITION: 3 people, 1 cost share (one key same)
        budgetCostShareService.reconcileCostShare(documentHeaderId, personnel, universityCostSharePersonnel);
        // RESULT: 3 people, 2 cost share with identical keys
        assertEquality(documentHeaderId, "160642");
        assertTrue("Person list should not be changed.", personnel.size() == 3);
        assertTrue("Person had new chart/org, should have been added.", universityCostSharePersonnel.size() == 2);
        assertTrue("Original person's chart/org should still be there.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(universityCostSharePersonnel, universityCostSharePerson1));
        assertTrue("New person's chart/org should have been added.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(universityCostSharePersonnel, universityCostSharePerson2));

        BudgetUser budgetUser3 = new BudgetUser();
        budgetUser3.setFiscalCampusCode(null);
        budgetUser3.setPrimaryDepartmentCode(null);
        personnel.add(budgetUser3);

        BudgetUser budgetUser4 = new BudgetUser();
        budgetUser4.setFiscalCampusCode("");
        budgetUser4.setPrimaryDepartmentCode("");
        personnel.add(budgetUser4);

        // CONDITION: 5 people, 2 cost share (two keys same)
        budgetCostShareService.reconcileCostShare(documentHeaderId, personnel, universityCostSharePersonnel);
        // RESULT: 5 people, 2 cost share with identical keys
        assertEquality(documentHeaderId, "160642");
        assertTrue("Person list should not be changed.", personnel.size() == 5);
        assertTrue("There should have been no change to chart/org since people added were null org empty string.", universityCostSharePersonnel.size() == 2);
        assertTrue("Original person's chart/org should still be there.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(universityCostSharePersonnel, universityCostSharePerson1));
        assertTrue("Second original person's chart/org should have been added.", ObjectUtils.collectionContainsObjectWithIdentitcalKey(universityCostSharePersonnel, universityCostSharePerson2));
    }
}
