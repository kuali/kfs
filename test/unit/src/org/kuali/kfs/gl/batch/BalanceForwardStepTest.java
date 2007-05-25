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

package org.kuali.module.gl.batch;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.GeneralLedgerTestHelper;
import org.kuali.test.WithTestSpringContext;

/**
 * IF THIS TEST FAILS, READ https://test.kuali.org/jira/browse/KULRNE-34 regarding reference numbers
 */
@WithTestSpringContext
public class BalanceForwardStepTest extends OriginEntryTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceForwardStepTest.class);

    public BalanceForwardStepTest() {
        super();
    }


    @Override
    protected void setUp() throws Exception {

        super.setUp();

        DateFormat transactionDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateTimeService.setCurrentDate(new Date(transactionDateFormat.parse(kualiConfigurationService.getApplicationParameterValue("fis_gl_year_end.sh", "TRANSACTION_DT")).getTime()));
    }

    /**
     * Test the encumbrance forwarding process in one fell swoop.
     * 
     * IF THIS TEST FAILS, READ https://test.kuali.org/jira/browse/KULRNE-34 regarding reference numbers and the year end dates
     * 
     * @throws Exception ## WARNING: DO NOT run this test or rename this method. WARNING ## ## WARNING: This one test takes just
     *         under 3 hours to run WARNING ## ## WARNING: over the vpn. WARNING ##
     */
    public void testAll() throws Exception {

        clearOriginEntryTables();
        BalanceTestHelper.populateBalanceTable();
        
        // Execute the step ...
        BalanceForwardStep step = (BalanceForwardStep) beanFactory.getBean("glBalanceForwardStep");
        step.execute();

        // load our services.
        OriginEntryService entryService = SpringServiceLocator.getOriginEntryService();
        OriginEntryGroupService groupService = SpringServiceLocator.getOriginEntryGroupService();

        // and verify the output.
        List fisGenerated = GeneralLedgerTestHelper.loadOutputOriginEntriesFromClasspath("org/kuali/module/gl/batch/gl_gleacbfb.data.txt", dateTimeService.getCurrentDate());

        // load our groups.
        Map criteria = new HashMap();

        criteria.put("sourceCode", "YEBB");
        Collection kualiGeneratedNonClosedPriorYearAccountGroups = groupService.getMatchingGroups(criteria);

        criteria.put("sourceCode", "YEBC");
        Collection kualiGeneratedClosedPriorYearAccountGroups = groupService.getMatchingGroups(criteria);

        // compute the difference between what should be output and what was output.
        List kualiGeneratedEntriesNotGeneratedByFis = new ArrayList();

        Iterator kualiGeneratedNonClosedPriorYearAccountGroupIterator = kualiGeneratedNonClosedPriorYearAccountGroups.iterator();
        while (kualiGeneratedNonClosedPriorYearAccountGroupIterator.hasNext()) {

            OriginEntryGroup kualiGroup = (OriginEntryGroup) kualiGeneratedNonClosedPriorYearAccountGroupIterator.next();

            Iterator kualiGeneratedNonClosedPriorYearAccountEntryIterator = entryService.getEntriesByGroup(kualiGroup);

            while (kualiGeneratedNonClosedPriorYearAccountEntryIterator.hasNext()) {

                OriginEntry entry = (OriginEntry) kualiGeneratedNonClosedPriorYearAccountEntryIterator.next();
                String kualiEntryLine = entry.getLine();

                kualiEntryLine = kualiEntryLine.substring(0, 173);

                if (!fisGenerated.remove(kualiEntryLine)) {

                    kualiGeneratedEntriesNotGeneratedByFis.add(kualiEntryLine);

                }

            }

        }

        Iterator closedPriorYearAccountGroupsIterator = kualiGeneratedClosedPriorYearAccountGroups.iterator();
        while (closedPriorYearAccountGroupsIterator.hasNext()) {

            OriginEntryGroup group = (OriginEntryGroup) closedPriorYearAccountGroupsIterator.next();

            Iterator entryIterator = entryService.getEntriesByGroup(group);
            while (entryIterator.hasNext()) {

                OriginEntry entry = (OriginEntry) entryIterator.next();
                String line = entry.getLine().substring(0, 173);

                if (!fisGenerated.remove(line)) {

                    kualiGeneratedEntriesNotGeneratedByFis.add(line);

                }

            }

        }

        traceList(kualiGeneratedEntriesNotGeneratedByFis, "kuali not fis");
        traceList(fisGenerated, "fis not kuali");

        // At this point extraEntriesGenerated and shouldBe should both be empty.
        // If they're not then something went wrong.
        assertTrue("Kuali generated entries that FIS did not generate (see https://test.kuali.org/jira/browse/KULRNE-34 for possible cause):", kualiGeneratedEntriesNotGeneratedByFis.isEmpty());
        assertTrue("FIS generated entries that Kuali did not generate (see https://test.kuali.org/jira/browse/KULRNE-34 for possible cause):", fisGenerated.isEmpty());

    }

    
    /**
     * This method resets the application params to values that are appropriate for year end dates
     * 
     * @see org.kuali.module.gl.OriginEntryTestBase#setApplicationConfigurationFlag(java.lang.String, boolean)
     */
    @Override
    protected void setApplicationConfigurationFlag(String name, boolean value) {
        super.setApplicationConfigurationFlag(name, value);
        unitTestSqlDao.sqlCommand("update FS_PARM_T set FS_PARM_TXT = '2004-01-01' where FS_SCR_NM = 'fis_gl_year_end.sh' and FS_PARM_NM = 'TRANSACTION_DT'");
        unitTestSqlDao.sqlCommand("update FS_PARM_T set FS_PARM_TXT = '2004' where FS_SCR_NM = 'fis_gl_year_end.sh' and FS_PARM_NM = 'UNIV_FISCAL_YR'");
    }
}
