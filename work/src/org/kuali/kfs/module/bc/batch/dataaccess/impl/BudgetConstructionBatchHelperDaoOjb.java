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
package org.kuali.module.budget.dao.ojb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;

import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;

import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.module.budget.service.impl.GenesisTest;


public class BudgetConstructionBatchHelperDaoOjb extends PlatformAwareDaoBaseOjb {

/**
 *   provides methods used throughout budget constuction batch to size hashmaps for efficiency
 *   for distinct and group by queries, we try to improve on the count returned by OJB's getCount.  
 *   in both cases, OJB produces a COUNT(DISTINCT concat(select fields, primary key fields)), which can substantially overcount the rows returned.
 */
    /*
     * ******************************************************************************
     *   These are utility routines used by all the units
     * ******************************************************************************  
     */
    //  return the recommended length of a hash map (to avoid collisions but avoid 
    //  wasting too much space)
    //**********************************************************
    // our technique of doing joins in Java instead of OJB is going to use a lot of
    // memory.  since memory is a finite resource, we want the garbage collector to
    // remove things no longer in use.  we could use weak hashmaps, but since many of
    // the hashed objects in the globally scoped hashmaps are built within the scope
    // of a method, doing so might cause them to be trashed prematurely.  instead, 
    // we instantiate all the hashmaps on declaration with a length of 1 (to avoid
    // null pointers).  then, we instantiate them again on first use with a size
    // determined by the likely number of objects * (1/.75) (see Horstman).  When
    // we are done with the hash objects, we clear them, so the underlying objects
    // are no longer referred to by anything and are fair game for the garbage 
    // collector.
    //***********************************************************

    
    private String ojbPlatform;
    
    private HashMap<String,String> countDistinctLeader  = null;
    private HashMap<String,String> countDistinctTrailer = null;
    private HashMap<String,String> concatSeparator      = null;
    
    // a MAX, say, with a GROUP BY would return one row.  So, would COUNT (DISTINCT 1)
    private Integer DEFAULT_QUERY_RETURN_COUNT = 1;

    
    
    /*
     *  takes an OJB queryByCriteria object as input
     *  returns the recommended size of a hashmap that is to be created from the results of the query
     *  (the recommend size is calculated from the number of rows the query is expected to return using a formula
     *   given in the JDK5 HashMap class comments)
     */
    protected Integer hashCapacity(QueryByCriteria queryID)
    {
        // this corresponds to a little more than the default load factor of .75.
        // a rehash supposedly occurs when the actual number of elements exceeds (load factor)*capacity.
        // N rows < .75 capacity ==> capacity > 4N/3 or 1.3333N.  We add a little slop.
        Double tempValue = ((Number)(getPersistenceBrokerTemplate().getCount(queryID))).floatValue()*(1.45);
        return (Integer) tempValue.intValue();
    }
    
    /*
     *  takes an OJB reportQueryByCriteria object as input
     *  this is a second version of the overloaded method hashcapacity
     */
    
    protected Integer hashCapacity(ReportQueryByCriteria queryID)
    {
        // for a distinct or a group by query, we build our own COUNT(DISTINCT...) from the fields in the SELECT list, because OJB doesn't do this correctly.
        // our method will have less chance of overcounting for Oracle and MySQL.
        // it will default for other DB's at present.
        if (queryID.isDistinct() || (!(queryID.getGroupBy().isEmpty())))
        {
            Double tempValue = queryCountDistinct(queryID).floatValue()*(1.45);
            return (Integer) tempValue.intValue();
        }
        //
        // since the query does not contain a DISTINCT or a GROUPBY, we use OJB's getCount.
        // this corresponds to a little more than the default load factor of .75
        // a rehash supposedly occurs when the actual number of elements exceeds
        // (load factor)*capacity
        // N rows < .75 capacity ==> capacity > 4N/3 or 1.3333N.  We add a little slop.
        Double tempValue = ((Number)(getPersistenceBrokerTemplate().getCount(queryID))).floatValue()*(1.45);
        return (Integer) tempValue.intValue();
    }
    private Integer hashCapacity(Integer hashSize)
    {
        // this corresponds to a little more than the default load factor of .75
        // a rehash supposedly occurs when the actual number of elements exceeds
        // (load factor)*capacity
        // N rows < .75 capacity ==> capacity > 4N/3 or 1.3333N.  We add a little slop.
        Double tempValue = hashSize.floatValue()*(1.45);
        return (Integer) tempValue.intValue();
    }
    
    protected Integer hashObjectSize(Class classID, Criteria criteriaID)
    {
        // this counts all rows
        String[] selectList = new String[] {"COUNT(*)"};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(classID, selectList, criteriaID);
        Iterator resultRows = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        if (resultRows.hasNext())
        {
            return(hashCapacity(((Number)((Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(resultRows))[0]).intValue()));
        }
        return (new Integer(1));
    }
    
    protected Integer hashObjectSize(Class classID, Criteria criteriaID,
                                   String propertyName)
    {
        // this one counts distinct values of a single field
        String[] selectList = buildCountDistinct(propertyName,classID);
        // if the field is not found, return the default
        if (selectList[0] == null)
        {
            return (new Integer(this.DEFAULT_QUERY_RETURN_COUNT));
        }
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(classID, selectList, criteriaID);
        Iterator resultRows = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        if (resultRows.hasNext())
        {
            return(hashCapacity(((Number)((Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(resultRows))[0]).intValue()));
        }
        return (new Integer(1));
    }
    
    protected Integer hashObjectSize(Class classID, Criteria criteriaID, 
                                   String[] selectList)
    {
        // this version is designed to do a count of distinct composite key values
        // it is assumed that the key's components can all be concatenated
        // there is apparently no concatenation function that is supported in all
        // versions of SQL (even though there is a standard)
        // so, we'll just run the query with OJB's getCount, which runs the query
        // and counts the rows using the Iterator returned.  One hopes that isn't
        // much more expensive than just doing an SQL COUNT(DISTINCT CONCAT(..))
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(classID, selectList, criteriaID);
        return (getPersistenceBrokerTemplate().getCount(queryID));
    }
    
    private String[] buildCountDistinct(ReportQueryByCriteria originalQuery)
    {
        // build the select list element COUNT(DISTINCT from the input query.
        // return an empty array for the SELECT list if this is not possible.
        boolean fieldValueFound = false;
        String[] returnSelectList = {""};
        if (! countDistinctLeader.containsKey(ojbPlatform))
        {
            // the ojbPlatform is not registered in this Kuali implementation
            return returnSelectList;
        }
        StringBuilder countDistinctElement = new StringBuilder(500);
        countDistinctElement.append(countDistinctLeader.get(ojbPlatform));
        // now we have to find the DB column names (as opposed to the OJB attribute names) for the fields in the SELECT list
        HashMap<String,String> allFields = getDBFieldNamesForClass(originalQuery.getSearchClass());
        String[] querySelectList = originalQuery.getAttributes();
        for (String attributeName : querySelectList)
        {
            String columnName = allFields.get(attributeName);
            if (columnName != null)
            {
               // add a separator if there was a previous column
                if (fieldValueFound) { countDistinctElement.append(concatSeparator.get(ojbPlatform)); }
                // stick in the new column
                countDistinctElement.append(columnName);
                // indicate that one of the original select entries is a DB field name
                fieldValueFound = true;
            }
        }
        if (! fieldValueFound)
        {
            // none of the items in the SELECT list is a DB-field, so no COUNT(DISTINCT is possible.
            return returnSelectList;
        }
        countDistinctElement.append(countDistinctTrailer.get(ojbPlatform));
        returnSelectList[0] = countDistinctElement.toString();
        return returnSelectList;
    }
    
    private String[] buildCountDistinct(String ojbAttributeName, Class ojbClass)
    {
        String[] returnSelectList = {""};
        // get the attribute/DB column name map for the class
        HashMap<String,String> allFields = getDBFieldNamesForClass(ojbClass);
        // build a COUNT (DISTINCT wrapper around the DB column name
        String dbColumnName = allFields.get(ojbAttributeName);
        if (dbColumnName == null)
        {
            // return an empty list if we fail
            return returnSelectList;
        }
        returnSelectList[0] = "COUNT (DISTINCT "+dbColumnName+")";
        return returnSelectList;
    }

    /**
     * 
     * fetch the DB column names for the fields in the class for the query
     * @param ojbClass = class of the query
     * @return hash set of DB column names keyed by OJB attribute name, 
     */
    private HashMap<String,String> getDBFieldNamesForClass(Class ojbClass)
    {
        ClassDescriptor ojbClassDescriptor = MetadataManager.getInstance().getRepository().getDescriptorFor(ojbClass);
        FieldDescriptor[] fieldDescriptorArray = ojbClassDescriptor.getFieldDescriptions();
        HashMap<String,String> returnSet = new HashMap<String,String>(((Double)(1.34*fieldDescriptorArray.length)).intValue());
        for (FieldDescriptor fieldInDB: fieldDescriptorArray)
        {
            returnSet.put(fieldInDB.getAttributeName(),fieldInDB.getColumnName());
        }
        return returnSet;   
    }
    
    /**
     * 
     * build a correct, DB-specific COUNT DISTINCT query to indicate how many rows a distinct or GROUP BY query will return.  the default count is returned if this is not possible
     * @param originalQuery = OJB report query for which to find a value for the row count returned
     * @return: number of rows the query should return
     */
    private Integer queryCountDistinct(ReportQueryByCriteria originalQuery)
    {
        // for every query with a distinct attribute, or with a group by:
        // we will build a COUNT(DISTINCT ...) with proper concatentation for Oracle and MySQL based on the fields in the select list.
        // for other databases we will simply return a default size
        // for queries that do not have a distinct or group by, we will simply return OJB's getCount
        String[] countDistinctElement = buildCountDistinct(originalQuery);
        // we return the default if there were no field names in the select list
        if (countDistinctElement[0] == null)
        {
            return (new Integer(this.DEFAULT_QUERY_RETURN_COUNT));
        }
        Class targetClass = originalQuery.getSearchClass();
        Criteria criteriaID = originalQuery.getCriteria();
        ReportQueryByCriteria countQuery = new ReportQueryByCriteria(targetClass, countDistinctElement, criteriaID);
        // run the new COUNT(DISTINCT query in OJB, and return the result
        Iterator resultRows = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(countQuery);
        if (resultRows.hasNext())
        {
            return((Integer) (((Number)((Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(resultRows))[0]).intValue()));
        }
        return (new Integer(this.DEFAULT_QUERY_RETURN_COUNT));
    }
    
    public String getOjbPlatform()
    {
        return this.ojbPlatform;
    }
    
    /**
     * 
     * initialize the ojbPlatform from the configuration properties
     * @param ojbPlatform = configuration property indicating the DB platform in use
     */
    public void setOjbPlatform(String ojbPlatform)
    {
        this.ojbPlatform = ojbPlatform;
    }
    

    /**
     * 
     * build the Oracle-specific COUNT (DISTINCT syntax--which is ANSI standard
     * @param ojbOraclePlatform is the Kuali constant matching the configuration property for Oracle
     */
    public void setOjbOraclePlatform(String ojbOraclePlatform)
    {
        // set up the Oracle-specific syntax in an associative array so it's easy to access
        if (countDistinctLeader == null)
        {
            countDistinctLeader = new HashMap<String,String>();
        }
        if (countDistinctTrailer == null)
        {
            countDistinctTrailer = new HashMap<String,String>();
        }
        if (concatSeparator == null)
        {
            concatSeparator = new HashMap<String,String>();
        }
        countDistinctLeader.put(ojbOraclePlatform,new String("COUNT( DISTINCT "));
        countDistinctTrailer.put(ojbOraclePlatform,new String(")"));
        concatSeparator.put(ojbOraclePlatform,new String("||"));
    }
    
    /**
     * 
     * build the MYSQL-specific COUNT (DISTINCT syntax
     * @param ojbMySqlPlatform is the Kuali constant matching the configuration property for MySQL
     */
    public void setOjbMySqlPlatform(String ojbMySqlPlatform)
    {
        // set up the MYSQL-specific syntax in an associative array so it's easy to access
        if (countDistinctLeader == null)
        {
            countDistinctLeader = new HashMap<String,String>();
        }
        if (countDistinctTrailer == null)
        {
            countDistinctTrailer = new HashMap<String,String>();
        }
        if (concatSeparator == null)
        {
            concatSeparator = new HashMap<String,String>();
        }
        countDistinctLeader.put(ojbMySqlPlatform,new String("COUNT( DISTINCT CONCAT("));
        countDistinctTrailer.put(ojbMySqlPlatform,new String("))"));
        concatSeparator.put(ojbMySqlPlatform,new String(","));
    }
    
}
