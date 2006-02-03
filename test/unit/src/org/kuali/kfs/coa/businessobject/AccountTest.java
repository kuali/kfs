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
package org.kuali.module.chart.bo;

import java.sql.Timestamp;
import java.text.ParseException;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class...
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AccountTest extends KualiTestBaseWithSpring {
    
    private static final String TEST_DATE_1_TODAY = "2002-04-22 19:48:23";
    private static final String TEST_DATE_1_YESTERDAY = "2002-04-21 19:48:23";
    private static final String TEST_DATE_1_TOMORROW = "2002-04-23 19:48:23";
    
    private static final String TEST_DATE_2_TODAY = "2002-04-22 10:23:08";
    private static final String TEST_DATE_2_YESTERDAY = "2002-04-21 10:23:08";
    private static final String TEST_DATE_2_TOMORROW = "2002-04-23 10:23:08";
    
    private static final String TEST_DATE_3_TODAY = "2002-04-22 06:14:55";
    private static final String TEST_DATE_3_YESTERDAY = "2002-04-21 06:14:55";
    private static final String TEST_DATE_3_TOMORROW = "2002-04-23 06:14:55";
    
    private DateTimeService dateTimeService;
    
    protected void setUp() throws Exception {
        super.setUp();
        dateTimeService = SpringServiceLocator.getDateTimeService();
    }
    
    //	pass this a name, and it returns a setup timestamp instance
    private Timestamp getNamedTimestamp(String timestampString) {
        Timestamp timestamp;
        try {
            timestamp = dateTimeService.convertToSqlTimestamp(timestampString);
        }
        catch (ParseException e) {
            assertNull("Timestamp String was not parseable", e);
            return null;
        }
        return timestamp;
    }
    
    //	since all the tests are doing the same thing, this is centralized
    private void doTest(String expirationDateString, String testDateString, boolean expectedResult) {
        
        Timestamp expirationDate = getNamedTimestamp(expirationDateString);
        Timestamp testDate = getNamedTimestamp(testDateString);
        
        //	setup the account, and set its expiration date
        Account account = new Account();
        account.setAccountExpirationDate(expirationDate);
        
        //	test against isExpired, and get the result
        boolean actualResult = account.isExpired(dateTimeService.getCalendar(testDate));
        
        //	compare the result to what was expected
        assertEquals(expectedResult, actualResult);
    }
    
    //	if date of expiration and date of today is the same date (time excluded)
    // then the account is not considered expired
    public void testIsExpiredToday_ExpirationDateToday_ExpirationDateEarlierTime() {
        doTest(TEST_DATE_2_TODAY, TEST_DATE_1_TODAY, false);
    }
    
    //	if date of expiration and date of today is the same date (time excluded)
    // then the account is not considered expired
    public void testIsExpiredToday_ExpirationDateToday_ExpirationDateLaterTime() {
        doTest(TEST_DATE_2_TODAY, TEST_DATE_3_TODAY, false);
    }
    
    //	if date of expiration is one day later than day of testDate, fail
    public void testIsExpiredToday_ExpirationDateTomorrow() {
        doTest(TEST_DATE_2_TOMORROW, TEST_DATE_1_TODAY, false);
    }
    
    //	if date of expiration is one day earlier than day of testDate, succeed
    public void testIsExpiredToday_ExpirationDateYesterday() {
        doTest(TEST_DATE_2_YESTERDAY, TEST_DATE_1_TODAY, true);
    }
    
}
