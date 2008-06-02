/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;

import org.kuali.test.ConfigureContext;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import org.kuali.core.service.BusinessObjectService;

import org.kuali.module.chart.service.OrganizationService;

import org.kuali.module.budget.util.ReportControlListBuildHelper;
import org.kuali.module.budget.util.ReportControlListBuildHelper.BuildState;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionPullup;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;

@ConfigureContext(session = KHUNTLEY)
public class ReportControlListBuildHelperTest extends KualiTestBase {

    // this arraylist differs includes all of arrayListForTest but has one additional set of organizations: it is a superset of arrayListForTest
    private Collection<BudgetConstructionPullup> arrayListSuperSetOfForTest       = new ArrayList(4);
    // these two have the same Pullup objects with the same content, but in different collection formats
    private Collection<BudgetConstructionPullup> arrayListForTest                 = new ArrayList(3);
    private Collection<BudgetConstructionPullup> hashSetMatchingArrayList         = new HashSet(3);
    // same content as arrayListForTest, but with different PullupObjects
    private Collection<BudgetConstructionPullup> arrayListForTestDifferentObjects = new ArrayList(3);
    // these two have different Pullup objects, but with the same content, in the same collection format
    private Collection<BudgetConstructionPullup> hashSetForTest                   = new HashSet(3);
    private Collection<BudgetConstructionPullup> sameHashSetDataDifferentObjects  = new HashSet(3);
    // 
    private BuildState currentState   = null;
    private BuildState requestedState = null;

    private String firstPointOfView  = null;
    private String secondPointOfView = null;
    
    private String userIdString          = new String("1234567890");
    
    private boolean testDataValid;
    
    private BusinessObjectService businessObjectService;
    private OrganizationService   organizationService;
    
    private Integer pullupFlag    = new Integer(0);
    private Long    versionNumber = new Long(0);

    StringBuilder messageBuffer = new StringBuilder("Testing ReportControlListBuilder.isBuildNeeded: ");
/*
 * verify the check to see whether the data in a requested report needs to be refreshed.  if a new user requests the same report as a previous user, the structures will be reset and the isBuildNeeded routine will return true.  So, we only need to check whether the same user has requested the same organizations for the same "mode" (the source table for the report data).
 */
    
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        organizationService   = SpringContext.getBean(OrganizationService.class);
        
        //  one of the points of view will be the top-level organization
        String[] rootOrganization = organizationService.getRootOrganizationCode();
        firstPointOfView = setUpAPointOfView(rootOrganization[0],rootOrganization[1]);
        
        
        // set up the test cases
        testDataValid = setUpTestOrganizations();
        
    }
    
    // did we have enough test data?
    public void testAdequateData()
    {
        assertTrue(ourAssertMessage("insufficient test data exists"),testDataValid);
    }
    
    // uninitialized call--no current state and no request state
    public void testUniitializedCall()
    {
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        assertFalse(ourAssertMessage("the control list builder is not initialized from the database"),buildHelper.isBuildNeeded());
    }
    
    // first call for a report--a null current state and a new requested state should signal a build is needed
    public void testFirstCall()
    {
        // on the first call, the currentState should have been initialized to null by the helper
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        requestedState = buildHelper.new BuildState(firstPointOfView, arrayListForTest, BCConstants.Report.BuildMode.PBGL);
        buildHelper.setRequestedState(requestedState);
        // a build should be required
        assertTrue(ourAssertMessage("first call with a null current state and a valid requested state"),buildHelper.isBuildNeeded());
    }
    
    // repeated call for a given report--the content of the pullup objects is the same, but a new set of pullup objects with that same content have been fetched from the database
    public void testSameCallDifferentDBObjectsInHashSets()
    {
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        requestedState = buildHelper.new BuildState(firstPointOfView, hashSetForTest, BCConstants.Report.BuildMode.PBGL);
        buildHelper.setRequestedState(requestedState);
        currentState = buildHelper.new BuildState(firstPointOfView, sameHashSetDataDifferentObjects, BCConstants.Report.BuildMode.PBGL);
        buildHelper.setCurrentState(currentState);
        // the comparison should look at the content of the objects and not the objects themselves, and no build should be required
        assertFalse(ourAssertMessage("same content in two different sets of DB objects--two hashSets"),buildHelper.isBuildNeeded());
    }
    
        // now do the same thing with an array list
    public void testSameCallDifferentDBObjectsInArrayLists()
    {
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        requestedState = buildHelper.new BuildState(secondPointOfView, arrayListForTestDifferentObjects, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setRequestedState(requestedState);
        currentState = buildHelper.new BuildState(secondPointOfView, arrayListForTest, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setCurrentState(currentState);
        // the comparison should look at the content of the objects and not the objects themselves, and no build should be required
        assertFalse(ourAssertMessage("same content in two different sets of DB objects--two arrayLists"),buildHelper.isBuildNeeded());
    }
    
    public void testDifferentPointOfView()
    {
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        requestedState = buildHelper.new BuildState(secondPointOfView, hashSetForTest, BCConstants.Report.BuildMode.PBGL);
        buildHelper.setRequestedState(requestedState);
        currentState = buildHelper.new BuildState(firstPointOfView, sameHashSetDataDifferentObjects, BCConstants.Report.BuildMode.PBGL);
        buildHelper.setCurrentState(currentState);
        // a new build should be required, because the same person is asking for the same data but from a different place in the security hierarchy
        assertTrue(ourAssertMessage("same content in two different sets of DB objects, but a different point of view"),buildHelper.isBuildNeeded());
    }
    
    public void testDifferentBuildMode()
    {
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        requestedState = buildHelper.new BuildState(firstPointOfView, hashSetForTest, BCConstants.Report.BuildMode.MONTH);
        buildHelper.setRequestedState(requestedState);
        currentState = buildHelper.new BuildState(firstPointOfView, sameHashSetDataDifferentObjects, BCConstants.Report.BuildMode.PBGL);
        buildHelper.setCurrentState(currentState);
        // a new build is required because the target report differs, even though the user and the organizations involved are the same
        assertTrue(ourAssertMessage("same content in two different sets of DB objects, but a different build mode"),buildHelper.isBuildNeeded());
    }
    
    // repeated call for a given report--the DB objects and their content are the same, but they are in different collection formats
    public void testDifferentCollectionFormats()
    {
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        requestedState = buildHelper.new BuildState(firstPointOfView, arrayListForTest, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setRequestedState(requestedState);
        currentState = buildHelper.new BuildState(firstPointOfView, hashSetMatchingArrayList, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setCurrentState(currentState);
        // the comparison should look at the content of the objects and not the objects themselves, and no build should be required
        assertFalse(ourAssertMessage("same content in collections with different formats--hash Set vs. arrayList"),buildHelper.isBuildNeeded());
    }
    
    //  different organizations (only of one three is the same) requested by the same person: both collections are hash sets
    public void testDifferentReportsSameCollectionFormat()
    {
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        requestedState = buildHelper.new BuildState(firstPointOfView, hashSetForTest, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setRequestedState(requestedState);
        currentState = buildHelper.new BuildState(firstPointOfView, hashSetMatchingArrayList, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setCurrentState(currentState);
        // the fact that the collection format is the same should not affect the fact that a new build is required
        assertTrue(ourAssertMessage("same collection format but different data content"),buildHelper.isBuildNeeded());
    }
    
    //  different organizations (only one of the three is the same) requested by the same person in different collection formats
    public void testDifferentReportsDifferentCollectionFormats()
    {
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        requestedState = buildHelper.new BuildState(firstPointOfView, hashSetForTest, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setRequestedState(requestedState);
        currentState = buildHelper.new BuildState(firstPointOfView, arrayListForTest, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setCurrentState(currentState);
        // only the requesting ID is the same
        assertTrue(ourAssertMessage("different data content and different collection formats"),buildHelper.isBuildNeeded());
    }
    
    // current state is a superset of the requested state
    public void testCurrentStateSuperSetOfRequestState()
    {
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        requestedState = buildHelper.new BuildState(firstPointOfView, arrayListForTest, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setRequestedState(requestedState);
        currentState = buildHelper.new BuildState(firstPointOfView, arrayListSuperSetOfForTest, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setCurrentState(currentState);
        //
        assertTrue(ourAssertMessage("current state is a superset of the requested state"),buildHelper.isBuildNeeded());
    }
    
    // requested state is a superset of the current state
    public void testRequestStateSuperSetOfCurrentState()
    {
        ReportControlListBuildHelper buildHelper = new ReportControlListBuildHelper();
        requestedState = buildHelper.new BuildState(firstPointOfView, arrayListSuperSetOfForTest, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setRequestedState(requestedState);
        currentState = buildHelper.new BuildState(firstPointOfView, arrayListForTest, BCConstants.Report.BuildMode.BCAF);
        buildHelper.setCurrentState(currentState);
        //
        assertTrue(ourAssertMessage("requested state is a superset of the current state"),buildHelper.isBuildNeeded());
    }

    private String ourAssertMessage(String messageToDisplay)
    {
        StringBuilder msg = new StringBuilder(messageBuffer);
        msg.append(messageToDisplay);
        return(msg.toString());
    }
 
    private BudgetConstructionPullup newBudgetConstructionPullup(BudgetConstructionOrganizationReports organizationReports)
    {
        BudgetConstructionPullup newMember = new BudgetConstructionPullup();
        newMember.setChartOfAccountsCode(organizationReports.getChartOfAccountsCode());
        newMember.setOrganizationCode(organizationReports.getOrganizationCode());
        newMember.setReportsToChartOfAccountsCode(organizationReports.getReportsToChartOfAccountsCode());
        newMember.setReportsToOrganizationCode(organizationReports.getReportsToOrganizationCode());
        newMember.setPullFlag(pullupFlag);
        newMember.setPersonUniversalIdentifier(userIdString);
        // to spoof the pullup object's coming from the DB, we add the OBJ_ID and VER_NBR from the Organization Reports, so they will not be null in the Pullup object.
        // doing this ensures that two pullup objects built from the same source OrganizationReports will have exactly the same content, as if the same Pullup object had come from the DB at two different times.
        newMember.setVersionNumber(versionNumber);
        newMember.setObjectId(organizationReports.getObjectId());
        return(newMember);
    }
    
    private void setUpAHashSet(BudgetConstructionOrganizationReports organizationReports)
    {
        // both have hte same collection format, but contain different objects with the same content
        hashSetForTest.add(newBudgetConstructionPullup(organizationReports));
        sameHashSetDataDifferentObjects.add(newBudgetConstructionPullup(organizationReports));
    }
    
    private void setUpAnArrayList(BudgetConstructionOrganizationReports organizationReports)
    {
        BudgetConstructionPullup pullUpElement = newBudgetConstructionPullup(organizationReports);
        // both contain the same elements, but in different collection formats
        arrayListForTest.add(pullUpElement);
        hashSetMatchingArrayList.add(pullUpElement);
        arrayListSuperSetOfForTest.add(pullUpElement);
        // set up an array list with different objects with the same content
        BudgetConstructionPullup pullUpElementWithNewID = newBudgetConstructionPullup(organizationReports);
        this.arrayListForTestDifferentObjects.add(pullUpElementWithNewID);
    }
    
    private String setUpAPointOfView(String chartOfAccounts, String organizationCode)
    {
        // the exact structure of this string does not affect what is being tested.
        // the assumed requirement is that it uniquely define an organization.
        return chartOfAccounts + "-" + organizationCode;
    }
    
    private void setUpExtraArrayListRow(BudgetConstructionOrganizationReports organizationReports)
    {
        BudgetConstructionPullup pullUpElement = newBudgetConstructionPullup(organizationReports);
        arrayListSuperSetOfForTest.add(pullUpElement);
    }
    
    // we assume that each row has a unique chart of accounts and organization code.
    // the method being tested should not depend on the type of collection it is fed, so we build two different states from two different collection classes.
    // if we cannot find enough data for a test, the test fails.
    // there are three members in each state.  both states have the same first member, but have no other common members.
    private boolean setUpTestOrganizations()
    {
        Collection<BudgetConstructionOrganizationReports> sourceData = businessObjectService.findAll(BudgetConstructionOrganizationReports.class); 
        if (sourceData.size() < 6)
        {
            // not enough test data
            return false;
        }
        
        int elementsSeen = 0;
        Iterator<BudgetConstructionOrganizationReports> reportSet = sourceData.iterator();
        if (reportSet.hasNext())
        {
            elementsSeen = elementsSeen+1;
            // the first element is in both sets
            BudgetConstructionOrganizationReports organizationReports = reportSet.next();
            secondPointOfView = setUpAPointOfView(organizationReports.getChartOfAccountsCode(), organizationReports.getOrganizationCode());
            setUpAnArrayList(organizationReports);
            setUpAHashSet(organizationReports);
        }
        while (reportSet.hasNext())
        {
            elementsSeen = elementsSeen+1;
            BudgetConstructionOrganizationReports organizationReports = reportSet.next();
            if (elementsSeen > 6)
            {
                continue;
            }
            if (elementsSeen < 4)
            {
                setUpAnArrayList(organizationReports);
            }
            else
            {   
                if (elementsSeen < 6)
                {
                setUpAHashSet(organizationReports);
                }
                else
                {
                    setUpExtraArrayListRow(organizationReports);
                }
            }
        }
        return (!(elementsSeen < 6));
    }

}
