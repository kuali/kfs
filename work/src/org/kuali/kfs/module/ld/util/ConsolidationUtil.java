/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;

/**
 * Utility class for helping DAOs deal with building queries for the consolidation option
 */
public class ConsolidationUtil {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsolidationUtil.class);

    /**
     * wrap the given field name with SQL function "sum"
     * 
     * @param fieldName the given field name
     * @return the wrapped field name with SQL function "sum"
     */
    public static final String sum(String fieldName) {
        return "sum(" + fieldName + ")";
    }

    /**
     * This method builds the atrribute list used by balance searching
     * 
     * @param extendedFields extra fields
     * @return List an attribute list
     */
    public static Collection<String> buildAttributeCollection(String... extendedFields) {
        return buildAttributeCollection(Arrays.asList(extendedFields));
    }

    /**
     * This method builds the atrribute list used by balance searching
     * 
     * @param extendedFields extra fields
     * @return Collection an attribute list
     */
    public static Collection<String> buildAttributeCollection(Collection<String> extendedFields) {
        Collection<String> attributeList = buildGroupByCollection();

        attributeList.add(sum(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT));
        attributeList.add(sum(KFSPropertyConstants.FINANCIAL_BEGINNING_BALANCE_LINE_AMOUNT));
        attributeList.add(sum(KFSPropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT));

        // add the entended elements into the list
        attributeList.addAll(extendedFields);
        return attributeList;
    }

    /**
     * Utility class for helping DAOs deal with building queries for the consolidation option
     * 
     * @param query Query to make consolidated
     * @param extraFields fields included in the query
     * @param ignoredFields to omit from the query
     */
    public static void buildConsolidatedQuery(ReportQueryByCriteria query, String... extraFields) {
        Collection<String> attributeList = buildAttributeCollection(extraFields);
        Collection<String> groupByList = buildGroupByCollection();

        attributeList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        attributeList.remove(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        groupByList.remove(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        attributeList.remove(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        groupByList.remove(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        // set the selection attributes
        String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Built Attributes for Query: " + attributeList.toString());
        }
        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[attributeList.size()]);
        query.addGroupBy(groupBy);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Built GroupBy for Query: " + groupByList.toString());    
        }
    }

    /**
     * This method builds group by attribute list used by balance searching
     * 
     * @return extraFields
     * @return Collection an group by attribute list
     */
    public static Collection<String> buildGroupByCollection(Collection<String> extraFields) {
        Collection<String> retval = new ArrayList();
        retval.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        retval.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        retval.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        retval.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        retval.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        retval.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        retval.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        retval.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        retval.add(KFSPropertyConstants.EMPLID);
        retval.add(KFSPropertyConstants.POSITION_NUMBER);
        retval.addAll(extraFields);
        return retval;
    }

    /**
     * This method builds group by attribute list used by balance searching
     * 
     * @return extraFields
     * @return Collection an group by attribute list
     */
    public static Collection<String> buildGroupByCollection(String... extraFields) {
        return buildGroupByCollection(Arrays.asList(extraFields));
    }

    /**
     * Consolidates a collection of actual balances with a collection of A2 balances. The A2 balances are changed to AC, then
     * matched by balance key with balances from the actual collection.
     * 
     * @param actualBalances - collection of actual balances (consolidatedBalanceTypeCode)
     * @param effortBalances - collection of effort balances ('A2')
     * @param consolidatedBalanceTypeCode - balance type to change A2 records to
     * @return Collection<LedgerBalance> - collection with consolidated balance records
     */
    public static Collection<LedgerBalance> consolidateA2Balances(Collection<LedgerBalance> actualBalances, Collection<LedgerBalance> effortBalances, String consolidatedBalanceTypeCode, List<String> consolidationKeyList) {
        Map<String, LedgerBalance> consolidatedBalanceMap = new HashMap<String, LedgerBalance>();
        for (LedgerBalance effortBalance : effortBalances) {
            effortBalance.setBalanceTypeCode(consolidatedBalanceTypeCode);
            String consolidationKey = ObjectUtil.buildPropertyMap(effortBalance, consolidationKeyList).toString();
            
            if(consolidatedBalanceMap.containsKey(consolidationKey)) {
                LedgerBalance ledgerBalance = consolidatedBalanceMap.get(consolidationKey);
                sumLedgerBalances(ledgerBalance, effortBalance);
            }
            else {                            
                consolidatedBalanceMap.put(consolidationKey, effortBalance);
            }
        }
        
        for (LedgerBalance actualBalance : actualBalances) {
            actualBalance.setBalanceTypeCode(consolidatedBalanceTypeCode);
            String consolidationKey = ObjectUtil.buildPropertyMap(actualBalance, consolidationKeyList).toString();
            
            if(consolidatedBalanceMap.containsKey(consolidationKey)) {
                LedgerBalance ledgerBalance = consolidatedBalanceMap.get(consolidationKey);
                sumLedgerBalances(ledgerBalance, actualBalance);
            }
            else {              
                consolidatedBalanceMap.put(consolidationKey, actualBalance);
            }
        }

        return consolidatedBalanceMap.values();
    }

    /**
     * Adds the amounts fields of the second balance record to the first.
     * 
     * @param balance1 - LedgerBalance
     * @param balance2 - LedgerBalance
     */
    public static void sumLedgerBalances(LedgerBalance balance1, LedgerBalance balance2) {
        balance1.setAccountLineAnnualBalanceAmount(balance1.getAccountLineAnnualBalanceAmount().add(balance2.getAccountLineAnnualBalanceAmount()));
        balance1.setBeginningBalanceLineAmount(balance1.getBeginningBalanceLineAmount().add(balance2.getBeginningBalanceLineAmount()));
        balance1.setContractsGrantsBeginningBalanceAmount(balance1.getContractsGrantsBeginningBalanceAmount().add(balance2.getContractsGrantsBeginningBalanceAmount()));
        balance1.setMonth1Amount(balance1.getMonth1Amount().add(balance2.getMonth1Amount()));
        balance1.setMonth2Amount(balance1.getMonth2Amount().add(balance2.getMonth2Amount()));
        balance1.setMonth3Amount(balance1.getMonth3Amount().add(balance2.getMonth3Amount()));
        balance1.setMonth4Amount(balance1.getMonth4Amount().add(balance2.getMonth4Amount()));
        balance1.setMonth5Amount(balance1.getMonth5Amount().add(balance2.getMonth5Amount()));
        balance1.setMonth6Amount(balance1.getMonth6Amount().add(balance2.getMonth6Amount()));
        balance1.setMonth7Amount(balance1.getMonth7Amount().add(balance2.getMonth7Amount()));
        balance1.setMonth8Amount(balance1.getMonth8Amount().add(balance2.getMonth8Amount()));
        balance1.setMonth9Amount(balance1.getMonth9Amount().add(balance2.getMonth9Amount()));
        balance1.setMonth10Amount(balance1.getMonth10Amount().add(balance2.getMonth10Amount()));
        balance1.setMonth11Amount(balance1.getMonth11Amount().add(balance2.getMonth11Amount()));
        balance1.setMonth12Amount(balance1.getMonth12Amount().add(balance2.getMonth12Amount()));
        balance1.setMonth13Amount(balance1.getMonth13Amount().add(balance2.getMonth13Amount()));
    }

    /**
     * wrap the attribute name based on the given flag: isAttributeNameNeeded
     * 
     * @param attributeName the given attribute name
     * @param isAttributeNameNeeded the flag that indicates if the attribute name needs to be wrapped with consolidation
     * @return the attribute name as it is if isAttributeNameNeeded is true; otherwise, the attribute name wrapped with
     *         consolidation string
     */
    public static String wrapAttributeName(String attributeName, boolean isAttributeNameNeeded) {
        return isAttributeNameNeeded ? attributeName : ConsolidationUtil.sum(attributeName);
    }
}
