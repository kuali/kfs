/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.labor.LaborPropertyConstants;

/**
 * Utility class for helping DAOs deal with building queries for the consolidation option
 */
public class ConsolidationUtil {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsolidationUtil.class);

    public static final String sum(String fieldName) {
        return "sum(" + fieldName + ")";
    }

    /**
     * This method builds the atrribute list used by balance searching
     * 
     * @param extendedFields extra fields
     * @return List an attribute list
     */
    public static Collection<String> buildAttributeCollection(String ... extendedFields) {
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

        attributeList.add(sum(LaborPropertyConstants.ACCOUNTING_LINE_ANNUAL_BALANCE_AMOUNT));
        attributeList.add(sum(LaborPropertyConstants.FINANCIAL_BEGINNING_BALANCE_AMOUNT));
        attributeList.add(sum(LaborPropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT));
        
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
     * 
     */
    public static void buildConsolidatedQuery(ReportQueryByCriteria query, String ... extraFields) {
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
        LOG.debug("Built Attributes for Query: " + attributeList.toString());
        
        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[attributeList.size()]);
        query.addGroupBy(groupBy);
        LOG.debug("Built GroupBy for Query: " + groupByList.toString());
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
    public static Collection<String> buildGroupByCollection(String ... extraFields) {
        return buildGroupByCollection(Arrays.asList(extraFields));
    }    
}
