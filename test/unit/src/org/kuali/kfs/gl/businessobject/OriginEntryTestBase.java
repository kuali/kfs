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
package org.kuali.module.gl;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.ConfigurableDateService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.UnitTestSqlDao;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.context.TestUtils;
import org.kuali.core.dbplatform.RawSQL;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.financial.bo.Bank;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.test.ConfigureContext;

/**
 * OriginEntryTestBase...the uberpowerful base of a lot of GL tests.  Basically, this class provides
 * many convenience methods for writing tests that test against large batches of origin entries.
 */
@RawSQL
@ConfigureContext
public class OriginEntryTestBase extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryTestBase.class);

    protected ConfigurableDateService dateTimeService;
    protected PersistenceService persistenceService;
    protected UnitTestSqlDao unitTestSqlDao = null;
    protected OriginEntryGroupService originEntryGroupService = null;
    protected OriginEntryService originEntryService = null;
    protected OriginEntryDao originEntryDao = null;
    protected KualiConfigurationService kualiConfigurationService = null;
    protected Date date;

    /**
     * Constructs a OriginEntryTestBase instance
     */
    public OriginEntryTestBase() {
        super();
    }

    /**
     * Sets up this test base; that means getting some services from Spring and reseting the
     * enhancement flags.
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        LOG.debug("setUp() starting");

        dateTimeService = SpringContext.getBean(ConfigurableDateService.class);
        date = dateTimeService.getCurrentDate();

        // Other objects needed for the tests
        persistenceService = SpringContext.getBean(PersistenceService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        originEntryService = SpringContext.getBean(OriginEntryService.class);
        originEntryDao = SpringContext.getBean(OriginEntryDao.class);
        originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);
        kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);

        // Set all enhancements to off
        resetAllEnhancementFlags();
    }


    /**
     * An inner class to point to a specific entry in a group
     */
    protected class EntryHolder {
        public String groupCode;
        public String transactionLine;

        /**
         * Constructs a OriginEntryTestBase.EntryHolder
         * @param groupCode the group that the entry to point to is in
         * @param transactionLine the line number of the entry
         */
        public EntryHolder(String groupCode, String transactionLine) {
            this.groupCode = groupCode;
            this.transactionLine = transactionLine;
        }
    }

    /**
     * Given a group source code and a bunch of transactions, creates a new group and adds all
     * the transactions to that group
     * 
     * @param groupCode the source code of the new group
     * @param transactions an array of String-formatted entries to save into the group
     * @param date the creation date of the new group
     */
    protected void loadInputTransactions(String groupCode, String[] transactions, Date date) {
        OriginEntryGroup group = originEntryGroupService.createGroup(new java.sql.Date(date.getTime()), groupCode, true, true, true);
        loadTransactions(transactions, group);
    }

    /**
     * Given a group source code and a bunch of transactions, creates a new group and adds all
     * the transactions to that group; sets the group creation date to today
     * 
     * @param groupCode the source code of the new group
     * @param transactions an array of String-formatted entries to save into the group
     */
    protected void loadInputTransactions(String groupCode, String[] transactions) {
        OriginEntryGroup group = originEntryGroupService.createGroup(new java.sql.Date(dateTimeService.getCurrentDate().getTime()), groupCode, true, true, true);
        loadTransactions(transactions, group);
    }

    /**
     * Loads an array of String formatted entries into the given origin entry group 
     * 
     * @param transactions an array of String formatted entries
     * @param group the group to save those entries into
     */
    protected void loadTransactions(String[] transactions, OriginEntryGroup group) {
        for (int i = 0; i < transactions.length; i++) {
            OriginEntryFull e = new OriginEntryFull(transactions[i]);
            originEntryService.createEntry(e, group);
        }

        persistenceService.clearCache();
    }

    /**
     * Deletes everything in the expenditure transaction table
     */
    protected void clearExpenditureTable() {
        unitTestSqlDao.sqlCommand("delete from gl_expend_trn_t");
    }
    
    /**
     * Deletes everything in the sufficient fund balance table
     */
    protected void clearSufficientFundBalanceTable() {
        unitTestSqlDao.sqlCommand("delete from gl_sf_balances_t");
    }

    /**
     * Deletes all entries in the entry table with the given chart code and account number
     * 
     * @param fin_coa_cd the chart code of entries to delete
     * @param account_nbr the account number of entries to delete
     */
    protected void clearGlEntryTable(String fin_coa_cd, String account_nbr) {
        unitTestSqlDao.sqlCommand("delete from gl_entry_t where fin_coa_cd = '" + fin_coa_cd + "' and account_nbr = '" + account_nbr + "'");
    }

    /**
     * Deletes everything in the gl reversal table
     */
    protected void clearReversalTable() {
        unitTestSqlDao.sqlCommand("delete from gl_reversal_t");
    }

    /**
     * Deletes everything in the gl balance table
     */
    protected void clearGlBalanceTable() {
        unitTestSqlDao.sqlCommand("delete from gl_balance_t");
    }

    /**
     * Deletes everything in the gl encumbrance table.
     */
    protected void clearEncumbranceTable() {
        unitTestSqlDao.sqlCommand("delete from gl_encumbrance_t");
    }

    /**
     * Deletes everything in the gl account balance table
     */
    protected void clearGlAccountBalanceTable() {
        unitTestSqlDao.sqlCommand("delete from gl_acct_balances_t");
    }

    /**
     * Deletes everything in the gl origin entry table and the gl origin entry group table 
     */
    protected void clearOriginEntryTables() {
        unitTestSqlDao.sqlCommand("delete from gl_origin_entry_t");
        unitTestSqlDao.sqlCommand("delete from gl_origin_entry_grp_t");
    }

    /**
     * Check all the entries in gl_origin_entry_t against the data passed in EntryHolder[]. If any of them are different, assert an
     * error.
     * 
     * @param groupCount the expected size of the group
     * @param requiredEntries an array of expected String-formatted entries to check against
     */
    protected void assertOriginEntries(int groupCount, EntryHolder[] requiredEntries) {
        persistenceService.clearCache();

        final List groups = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_grp_t order by origin_entry_grp_src_cd");
        assertEquals("Number of groups is wrong", groupCount, groups.size());

        Collection<OriginEntryFull> c = originEntryDao.testingGetAllEntries();

        // now, sort the lines here to avoid any DB sorting issues
        Comparator<OriginEntryFull> originEntryComparator = new Comparator<OriginEntryFull>() {
            public int compare(OriginEntryFull o1, OriginEntryFull o2) {
                int groupCompareResult = o1.getEntryGroupId().compareTo(o2.getEntryGroupId());
                if (groupCompareResult == 0) {
                    return o1.getLine().compareTo(o2.getLine());
                }
                else {
                    return groupCompareResult;
                }
            }
        };
        Comparator<EntryHolder> entryHolderComparator = new Comparator<EntryHolder>() {
            public int compare(EntryHolder o1, EntryHolder o2) {
                int groupCompareResult = String.valueOf(getGroup(groups, o1.groupCode)).compareTo(String.valueOf(getGroup(groups, o2.groupCode)));
                if (groupCompareResult == 0) {
                    return o1.transactionLine.compareTo(o2.transactionLine);
                }
                else {
                    return groupCompareResult;
                }
            }
        };
        ArrayList<OriginEntryFull> sortedEntryTransactions = new ArrayList<OriginEntryFull>(c);
        Collections.sort(sortedEntryTransactions, originEntryComparator);
        Arrays.sort(requiredEntries, entryHolderComparator);

        // This is for debugging purposes - change to true for output
        if (true) {
            System.err.println("Groups:");
            for (Iterator iter = groups.iterator(); iter.hasNext();) {
                Map element = (Map) iter.next();
                System.err.println("G:" + element.get("ORIGIN_ENTRY_GRP_ID") + " " + element.get("ORIGIN_ENTRY_GRP_SRC_CD"));
            }

            System.err.println("Transactions:");
            for (OriginEntryFull element : sortedEntryTransactions) {
                System.err.println("L:" + element.getEntryGroupId() + " " + element.getLine());
            }
            System.err.println("Expected Transactions:");
            for (EntryHolder element : requiredEntries) {
                System.err.println("L:" + getGroup(groups, element.groupCode) + " " + element.transactionLine);
            }
        }

        assertEquals("Wrong number of transactions in Origin Entry", requiredEntries.length, c.size());


        int count = 0;
        for (Iterator iter = sortedEntryTransactions.iterator(); iter.hasNext();) {
            OriginEntryFull foundTransaction = (OriginEntryFull) iter.next();

            // Check group
            int group = getGroup(groups, requiredEntries[count].groupCode);

            assertEquals("Group for transaction " + foundTransaction.getEntryId() + " is wrong", group, foundTransaction.getEntryGroupId().intValue());

            // Check transaction - this is done this way so that Anthill prints the two transactions to make
            // resolving the issue easier.

            String expected = requiredEntries[count].transactionLine.substring(0, 173);// trim();
            String found = foundTransaction.getLine().substring(0, 173);// trim();

            if (!found.equals(expected)) {
                System.err.println("Expected transaction: " + expected);
                System.err.println("Found transaction:    " + found);

                fail("Transaction " + foundTransaction.getEntryId() + " doesn't match expected output");
            }
            count++;
        }
    }

    /**
     * Given a list of origin entry groups and a group source code, returns the id of the group with that source code
     * 
     * @param groups a List of groups to selectg a group from
     * @param groupCode the source code of the group to select
     * @return the id of the first group in the list with that source code, or -1 if no groups with that source code were found
     */
    protected int getGroup(List groups, String groupCode) {
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            Map element = (Map) iter.next();

            String sourceCode = (String) element.get("ORIGIN_ENTRY_GRP_SRC_CD");
            if (groupCode.equals(sourceCode)) {
                BigDecimal groupId = (BigDecimal) element.get("ORIGIN_ENTRY_GRP_ID");
                return groupId.intValue();
            }
        }
        return -1;
    }

    protected static Object[] FLEXIBLE_OFFSET_ENABLED_FLAG = { OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG };
    protected static Object[] FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG = { Bank.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG };

    /**
     * Resets the flexible offset and flexible claim on cash parameters, so that processes running as unit tests have consistent behaviors
     * @throws Exception if the parameters could not be reset for some reason
     */
    protected void resetAllEnhancementFlags() throws Exception {
        setApplicationConfigurationFlag((Class) FLEXIBLE_OFFSET_ENABLED_FLAG[0], (String) FLEXIBLE_OFFSET_ENABLED_FLAG[1], false);
        setApplicationConfigurationFlag((Class) FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG[0], (String) FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG[1], false);
    }

    /**
     * Resets a parameter for the sake of the unit test
     * 
     * @param componentClass the module class of the parameter
     * @param name the name of the parameter to reset
     * @param value the new value for the parameter
     * @throws Exception thrown if some vague thing goes wrong
     */
    protected void setApplicationConfigurationFlag(Class componentClass, String name, boolean value) throws Exception {
        TestUtils.setSystemParameter(componentClass, name, value ? "Y" : "N");
    }


    /**
     * Outputs the entire contents of a List to System.out
     * 
     * @param list a List, presumably of Origin entries, but really, it could be anything
     * @param name the name of the list to display in the output
     */
    protected void traceList(List list, String name) {
        trace("StartList " + name + "( " + list.size() + " elements): ", 0);

        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            trace(iterator.next(), 1);
        }

        trace("EndList " + name + ": ", 0);
        trace("", 0);
    }

    /**
     * Writes an object to standard out
     * 
     * @param o the object to output, after..
     * @param tabIndentCount the number of tabs to push the object output
     */
    protected void trace(Object o, int tabIndentCount) {
        PrintStream out = System.out;

        for (int i = 0; i < tabIndentCount; i++) {
            out.print("\t");
        }

        out.println(null == o ? "NULL" : o.toString());
    }

}
