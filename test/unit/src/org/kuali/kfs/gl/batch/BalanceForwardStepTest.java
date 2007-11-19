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

import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.context.TestUtils;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.GeneralLedgerTestHelper;
import org.kuali.test.ConfigureContext;

/**
 * A test to see if the balance forward year end process produces the expected origin entries
 */
@ConfigureContext
// @RelatesTo(RelatesTo.JiraIssue.KULRNE5916)
public class BalanceForwardStepTest extends OriginEntryTestBase {
    // IF THIS TEST FAILS, READ KULRNE-34 regarding reference numbers
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceForwardStepTest.class);

    /**
     * Constructs a BalanceForwardStepTest instance
     */
    public BalanceForwardStepTest() {
        super();
    }

    /**
     * Sets up the test by getting the date parameter
     * @see org.kuali.module.gl.OriginEntryTestBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        super.setUp();

        DateFormat transactionDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateTimeService.setCurrentDate(new Date(transactionDateFormat.parse(SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM)).getTime()));
    }

    /**
     * Test the encumbrance forwarding process in one fell swoop. IF THIS TEST FAILS, READ
     * KULRNE-34 regarding reference numbers and the year end dates
     * 
     * @throws Exception ## WARNING: DO NOT run this test or rename this method. WARNING ## ## WARNING: This one test takes just
     *         under 3 hours to run WARNING ## ## WARNING: over the vpn. WARNING ##
     */
    public void testAll() throws Exception {

        clearOriginEntryTables();
        BalanceTestHelper.populateBalanceTable();

        // Execute the step ...
        BalanceForwardStep step = SpringContext.getBean(BalanceForwardStep.class);
        step.execute(getClass().getName());

        // load our services.
        OriginEntryService entryService = SpringContext.getBean(OriginEntryService.class);
        OriginEntryGroupService groupService = SpringContext.getBean(OriginEntryGroupService.class);

        // and verify the output.
        List fisGeneratedRaw = GeneralLedgerTestHelper.loadOutputOriginEntriesFromClasspath("org/kuali/module/gl/batch/gl_gleacbfb.data.txt", dateTimeService.getCurrentDate());
        List fisGenerated = new ArrayList();
        for (Object o : fisGeneratedRaw) {
            fisGenerated.add(filterOriginEntryLine((String) o));
        }

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

                OriginEntryFull entry = (OriginEntryFull) kualiGeneratedNonClosedPriorYearAccountEntryIterator.next();
                String kualiEntryLine = entry.getLine();

                kualiEntryLine = filterOriginEntryLine(kualiEntryLine.substring(0, 173));

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

                OriginEntryFull entry = (OriginEntryFull) entryIterator.next();
                String line = filterOriginEntryLine(entry.getLine().substring(0, 173));

                if (!fisGenerated.remove(line)) {

                    kualiGeneratedEntriesNotGeneratedByFis.add(line);

                }

            }

        }

        traceList(kualiGeneratedEntriesNotGeneratedByFis, "kuali not fis");
        traceList(fisGenerated, "fis not kuali");

        // At this point extraEntriesGenerated and shouldBe should both be empty.
        // If they're not then something went wrong.
        assertTrue("Kuali generated entries that FIS did not generate (see KULRNE-34 for possible cause):", kualiGeneratedEntriesNotGeneratedByFis.isEmpty());
        assertTrue("FIS generated entries that Kuali did not generate (see KULRNE-34 for possible cause):", fisGenerated.isEmpty());

    }


    /**
     * This method resets the application params to values that are appropriate for year end dates
     * 
     * @see org.kuali.module.gl.OriginEntryTestBase#setApplicationConfigurationFlag(java.lang.String, boolean)
     */
    @Override
    protected void setApplicationConfigurationFlag(Class componentClass, String name, boolean value) throws Exception {
        super.setApplicationConfigurationFlag(componentClass, name, value);
        TestUtils.setSystemParameter(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM, "2004-01-01");
        TestUtils.setSystemParameter(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM, "2004");
    }

    /**
     * Removes the sequence number from the origin entry line
     * 
     * @param line the original origin entry line
     * @return the filtered origin entry line
     */
    private String filterOriginEntryLine(String line) {
        // right now, remove the sequence number from this test
        return line.substring(0, 51) + line.substring(57);
    }
}
