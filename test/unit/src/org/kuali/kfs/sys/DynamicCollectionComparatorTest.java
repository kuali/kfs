/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.DynamicCollectionComparator.SortOrder;
import org.kuali.kfs.sys.fixture.AccountFixture;

public class DynamicCollectionComparatorTest extends TestCase {

    // the default sort order is ascending
    public void testSort_DefaultOrder() throws Exception {
        List<Account> accounts = this.getAccounts();
        String[] fieldNames = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE };

        DynamicCollectionComparator.sort(accounts, fieldNames);
        assertTrue(this.isSortedByAscendingOrder(accounts, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
    }

    // test if the given list of objects can be sorted by ascending order
    public void testSort_Ascending() throws Exception {
        List<Account> accounts = this.getAccounts();
        String[] fieldNames = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE };

        DynamicCollectionComparator.sort(accounts, SortOrder.ASC, fieldNames);
        assertTrue(this.isSortedByAscendingOrder(accounts, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
    }

    // test if the given list of objects can be sorted by descending order
    public void testSort_Descending() throws Exception {
        List<Account> accounts = this.getAccounts();
        String[] fieldNames = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE };

        DynamicCollectionComparator.sort(accounts, SortOrder.DESC, fieldNames);
        assertTrue(this.isSortedByDescendingOrder(accounts, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
    }

    // test if the given list of objects can be sorted by ascending order
    public void testSort_ByMultipleFields_Ascending() throws Exception {
        List<Account> accounts = this.getAccounts();
        String[] fieldNames = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER };

        DynamicCollectionComparator.sort(accounts, SortOrder.ASC, fieldNames);

        Map<String, List<Account>> accountMap = this.groupAccounts(accounts);
        for (String key : accountMap.keySet()) {
            assertTrue(this.isSortedByAscendingOrder(accountMap.get(key), KFSPropertyConstants.ACCOUNT_NUMBER));
        }
    }

    // test if the given list of objects can be sorted by descending order
    public void testSort_ByMultipleFields_Descending() throws Exception {
        List<Account> accounts = this.getAccounts();
        String[] fieldNames = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER };

        DynamicCollectionComparator.sort(accounts, SortOrder.DESC, fieldNames);

        Map<String, List<Account>> accountMap = this.groupAccounts(accounts);
        for (String key : accountMap.keySet()) {
            assertTrue(this.isSortedByDescendingOrder(accountMap.get(key), KFSPropertyConstants.ACCOUNT_NUMBER));
        }
    }

    // create a list of accounts that will be posted in report
    private List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<Account>();
        for (AccountFixture account : AccountFixture.values()) {
            accounts.add(account.createAccount());
        }
        return accounts;
    }

    // group the given accounts with chart accounts code
    private Map<String, List<Account>> groupAccounts(List<Account> accounts) {
        Map<String, List<Account>> accountMap = new HashMap<String, List<Account>>();
        for (Account account : accounts) {
            String chartOfAccountsCode = account.getChartOfAccountsCode();
            if (accountMap.containsKey(chartOfAccountsCode)) {
                List<Account> accountList = accountMap.get(chartOfAccountsCode);
                accountList.add(account);
            }
            else {
                List<Account> accountList = new ArrayList<Account>();
                accountList.add(account);
                accountMap.put(chartOfAccountsCode, accountList);
            }
        }
        return accountMap;
    }

    // determine whether the given object list has been sorted by the specified field by ascending order
    private <T> boolean isSortedByAscendingOrder(List<T> objectList, String fieldName) throws Exception {
        Collator collator = Collator.getInstance();
        Object tempValue = null;
        for (T object : objectList) {
            Object fieldValue = PropertyUtils.getProperty(object, fieldName);

            if (tempValue == null || collator.compare(tempValue, fieldValue) <= 0) {
                tempValue = fieldValue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    // determine whether the given object list has been sorted by the specified field by ascending order
    private <T> boolean isSortedByDescendingOrder(List<T> objectList, String fieldName) throws Exception {
        Collator collator = Collator.getInstance();
        Object tempValue = null;

        int count = 0;
        for (T object : objectList) {
            Object fieldValue = PropertyUtils.getProperty(object, fieldName);

            if (count == 0) {
                tempValue = fieldValue;
                count++;
            }
            else if (tempValue == null && fieldValue == null) {
                tempValue = fieldValue;
            }
            else if (tempValue == null) {
                return false;
            }
            else if (fieldValue == null) {
                tempValue = fieldValue;
            }
            else if (collator.compare(tempValue, fieldValue) >= 0) {
                tempValue = fieldValue;
            }
            else {
                return false;
            }
        }

        return true;
    }
    
}
