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
package org.kuali.module.chart.rules;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceDocumentBase;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.chart.bo.Delegate;

/**
 * This class...
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DelegateRuleTest extends ChartRuleTestBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegateRuleTest.class);
    
    private static final String CHART_GOOD_1 = "UA";
    private static final String ACCOUNT_GOOD_1 = "1912201";
    private static final String DOCTYPE_GOOD_1 = "ALL";
    private static final String USERID_GOOD_1 = "4513309370";  // PIABAD = Abad, Pauline I = 0000112715
    private static final String USERID_GOOD_2 = ""; // HABRAHAM = Abraham, Helena C = 0000199323
    
    private static final int FAKE_TODAY_1_YEAR = 2002;
    private static final int FAKE_TODAY_1_MONTH = 3;
    private static final int FAKE_TODAY_1_DAY = 15;
    
    private DelegateRule delegateRule;
    
    /*
     * @see KualiTestBaseWithSpring#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        delegateRule = new DelegateRule();
    }

    /**
     * 
     * This method creates a delegate with a minimal set of 
     * known-good values.
     * 
     * @return
     */
    private Delegate goodDelegate1() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(new Long(USERID_GOOD_1));
        return delegate;
    }
    
    private Timestamp newTimestamp(int year, int month, int day) {
        
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        calendar = DateUtils.truncate(calendar, Calendar.DAY_OF_MONTH);
        
        return new Timestamp(calendar.getTimeInMillis());
    }
    
    private Timestamp fakeToday1() {
        return newTimestamp(FAKE_TODAY_1_YEAR, FAKE_TODAY_1_MONTH, FAKE_TODAY_1_DAY);
    }
    
    /**
     * 
     * This method creates a Maintenance document with the passed in businessObject
     * 
     * @param delegate
     * @return
     */
    private MaintenanceDocument doc1(Delegate delegate) {
        MaintenanceDocument document = new MaintenanceDocumentBase("KualiAccountDelegateMaintenanceDocument");
        document.getDocumentHeader().setFinancialDocumentDescription("test");
        document.setNewMaintainableObject(new KualiMaintainableImpl(delegate));
        return document;
    }
    
    /**
     * 
     * This method tests a Delegate that we have setup with all known 
     * good values for the required fields, and nothing or the default 
     * for the other fields.
     * 
     * This test should always pass, if it does not, then none of the 
     * following tests are meaningful, as the baseline is broken.
     *
     */
    public void testKnownGoodDelegatePassesAllRules() {
        
        MaintenanceDocument document = doc1(goodDelegate1());

        //	make sure there are no errors beforehand
        assertEquals(0, GlobalVariables.getErrorMap().size());
        
        //	run the business rules
        delegateRule.processRouteDocument(document);
        
        //	show errors to the console, if any
        if (GlobalVariables.getErrorMap().size() > 0) {
            showErrorMap();
        }
        
        //	assert there are no errors afterwards
        assertEquals(0, GlobalVariables.getErrorMap().size());
        
    }
    
    // THIS TEST IS NOT COMPLETE
    // THIS TEST IS NOT COMPLETE
    public void testCheckSimpleRulesStartDateRule_startDateToday() {
        
        //	new delegate with start-date same as today
        MaintenanceDocument document = doc1(goodDelegate1());
        Delegate delegate = (Delegate) document.getNewMaintainableObject().getBusinessObject();
        delegate.setAccountDelegateStartDate(fakeToday1());
        
        //	arbitrary time for today
        Timestamp today = fakeToday1();
        
        //	make sure there are no errors beforehand
        assertEquals(0, GlobalVariables.getErrorMap().size());
        
        //	run the business rules
        delegateRule.processRouteDocument(document);
        
        //	show errors to the console, if any
        if (GlobalVariables.getErrorMap().size() > 0) {
            showErrorMap();
        }
        
        //	assert there are no errors afterwards
        //assertEquals(0, GlobalVariables.getErrorMap().size());
        
    }
    
    public void testCheckSimpleRulesStartDateRule_startDateTomorrow() {
        
    }
    
    public void testCheckSimpleRulesStartDateRule_startDateYesterday() {
        
    }
    
    public void testCheckSimpleRulesStartDateRule_startDateTodayTimeBefore() {
        
    }
    
    public void testCheckSimpleRulesStartDateRule_startDateTodayTimeSame() {
        
    }
    
    public void testCheckSimpleRulesStartDateRule_startDateTodayTimeAfter() {
        
    }
    
    /*
     * Class under test for boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument)
     */
    public void testProcessCustomRouteDocumentBusinessRulesMaintenanceDocument() {
    }

    /*
     * Class under test for boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument)
     */
    public void testProcessCustomApproveDocumentBusinessRulesMaintenanceDocument() {
    }

}
