/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.service.impl.OrgReviewRoleTestBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

@ConfigureContext
public class OrgReviewRoleRuleTest extends OrgReviewRoleTestBase {

    // public void testValidateRoleMembersToSave() {
    // fail("Not yet implemented");
    // }

    OrgReviewRoleRule rule;
    Date today;
    Date yesterday;
    Date tomorrow;

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        rule = new OrgReviewRoleRule();
        today = dateTimeService.getCurrentSqlDateMidnight();
        yesterday = DateUtils.addDays(today, -1);
        tomorrow = DateUtils.addDays(today, 1);
    }

    public void testValidateDates_NewRecord_null_null() {
        OrgReviewRole orr = buildOrgHierData();
        
        orr.setActiveFromDate( null );
        orr.setActiveToDate( null );
        
        boolean ruleResult = rule.validateDates(orr, false);
        assertTrue( "Rule should have passed: " + dumpMessageMapErrors(), ruleResult );
        assertEquals( "Rule should have defaulted in the current day for the start date.", KfsDateUtils.clearTimeFields(dateTimeService.getCurrentDate()), orr.getActiveFromDate() );
    }

    public void testValidateDates_NewRecord_today_tomorrow() {
        OrgReviewRole orr = buildOrgHierData();
        
        orr.setActiveFromDate( today );
        orr.setActiveToDate( tomorrow );
        boolean ruleResult = rule.validateDates(orr, false);
        assertTrue( "Rule should have passed: " + dumpMessageMapErrors(), ruleResult );
    }

    public void testValidateDates_NewRecord_yesterday_tomorrow() {
        OrgReviewRole orr = buildOrgHierData();
        
        orr.setActiveFromDate( yesterday );
        orr.setActiveToDate( tomorrow );
        
        boolean ruleResult = rule.validateDates(orr, false);
        assertFalse( "Rule should not have passed", ruleResult );
        assertTrue( "There should have been an error on the active from date field: " + dumpMessageMapErrors(), GlobalVariables.getMessageMap().getAllPropertiesWithErrors().contains( KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + "activeFromDate" ) );
    }

    public void testValidateDates_EditRecord_yesterday_tomorrow() {
        OrgReviewRole orr = buildOrgHierData();
        
        orr.setActiveFromDate( yesterday );
        orr.setActiveToDate( tomorrow );
        
        boolean ruleResult = rule.validateDates(orr, true);
        assertTrue( "Rule should have passed: " + dumpMessageMapErrors(), ruleResult );
    }

    public void testValidateDates_NewRecord_tomorrow_today() {
        OrgReviewRole orr = buildOrgHierData();
        
        orr.setActiveFromDate( tomorrow );
        orr.setActiveToDate( today );
        
        boolean ruleResult = rule.validateDates(orr, false);
        assertFalse( "Rule should not have passed", ruleResult );
        assertTrue( "There should have been an error on the active to date field: " + dumpMessageMapErrors(), GlobalVariables.getMessageMap().getAllPropertiesWithErrors().contains( KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + "activeToDate" ) );
    }

    public void testValidateDates_NewRecord_yesterday_yesterday() {
        OrgReviewRole orr = buildOrgHierData();
        
        orr.setActiveFromDate( yesterday );
        orr.setActiveToDate( yesterday );
        
        boolean ruleResult = rule.validateDates(orr, false);
        assertFalse( "Rule should not have passed", ruleResult );
        assertTrue( "There should have been an error on the active from date field: " + dumpMessageMapErrors(), GlobalVariables.getMessageMap().getAllPropertiesWithErrors().contains( KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + "activeFromDate" ) );
        assertTrue( "There should have been an error on the active to date field: " + dumpMessageMapErrors(), GlobalVariables.getMessageMap().getAllPropertiesWithErrors().contains( KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + "activeToDate" ) );
    }

    public void testValidateDates_EditRecord_yesterday_yesterday() {
        OrgReviewRole orr = buildOrgHierData();
        
        orr.setActiveFromDate( yesterday );
        orr.setActiveToDate( yesterday );
        
        boolean ruleResult = rule.validateDates(orr, true);
        assertFalse( "Rule should not have passed", ruleResult );
        assertTrue( "There should have been an error on the active to date field: " + dumpMessageMapErrors(), GlobalVariables.getMessageMap().getAllPropertiesWithErrors().contains( KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + "activeToDate" ) );
    }
    
    // public void testValidateAmounts() {
    // fail("Not yet implemented");
    // }
    //
    // public void testValidateRoleMember() {
    // fail("Not yet implemented");
    // }
    //
    // public void testAreAttributesUnique() {
    // fail("Not yet implemented");
    // }

}
