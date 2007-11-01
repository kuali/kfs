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
package org.kuali.module.budget.dao.ojb;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.budget.dao.FiscalYearInitiatorRIRelationshipsDao;

public class FiscalYearInitiatorRIRelationshipsDaoOjb extends PlatformAwareDaoBaseOjb implements FiscalYearInitiatorRIRelationshipsDao {

    private HashMap<String, Class> makerObjectsList;
    private HashMap<String, ArrayList> childParentMap;

    private PersistenceStructureService persistenceStructureService;

    /* turn on the logger for the persistence broker */
    private static Logger LOG = org.apache.log4j.Logger.getLogger(FiscalYearInitiatorRIRelationshipsDaoOjb.class);

    // the list of all the fiscal year makers objects
    public HashMap<String, Class> getMakerObjectsList() {
        return this.makerObjectsList;
    }

    public void setMakerObjectsList(HashMap<String, Class> makerObjectsList) {
        this.makerObjectsList = makerObjectsList;
    }

    // this list of child/parent relationships for the fiscal year makers objects
    public HashMap<String, ArrayList> getChildParentMap() {
        return this.childParentMap;
    }

    public void setChildParentMap(HashMap<String, ArrayList> childParentMap) {
        this.childParentMap = childParentMap;
    }

    // this list specifies the delete order for the objects in the list
    public ArrayList getDeleteOrder() {
        ArrayList returnList = null;
        returnList = new ArrayList(makerObjectsList.size());
        return returnList;
    }

    // this is a map of the execution order for the classes
    // the class name is followed by a class ID which contains the execute method
    // initially, this classID pointer will be set to null
    // it will be filled in manually as new objects are added to FiscalYearMakersDao
    public LinkedHashMap getCopyOrder() {
        LinkedHashMap<String, Class> returnMap = null;
        return returnMap;
    }

    // this is an "action", or callback, class
    // it allows us to build an instance at run time for each child, after the parents
    // have already been built for the coming fiscal period
    // (1) for each parent, store the values that exist for the child's foreign keys
    // (2) provide a method that can be called by each child row read from the base
    // period. the method will check that the child has the proper RI relationship
    // with at least one row from each parent.
    public class ParentKeyChecker<C> {
        private ParentClass<C>[] parentClassList = null;

        public ParentKeyChecker(Class childClass, Integer RequestYear) {
            String testString = childClass.getName();
            if (childParentMap.containsKey(testString)) {
                ArrayList<Class> parentClasses = childParentMap.get(testString);
                parentClassList = new ParentClass[parentClasses.size()];
                for (int i = 0; i < parentClasses.size(); i++) {
                    parentClassList[i] = new ParentClass<C>(parentClasses.get(i), childClass, RequestYear);
                }
            }
        }

        public boolean childRowSatisifesRI(C ourBO) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            boolean returnValue = true;
            if (parentClassList == null) {
                return returnValue;
            }
            for (int i = 0; i < parentClassList.length; i++) {
                returnValue = returnValue && parentClassList[i].isInParent(ourBO);
            }
            return returnValue;
        }
    };


    // this class is used to construct a parent key hashmap, and provide a method
    // to verify that a business object of type C matches on its foreign key
    // fields with the parent
    public class ParentClass<C> {
        private String[] childKeyFields;
        private String[] parentKeyFields;
        private HashSet<String> parentKeys = new HashSet<String>(1);

        // the constructor will initialize the key hashmap for this parent object
        // it will also get the foreign key fields from the persistence data structure
        // (the assumption is that the fields names returned are the same in both the
        // parent class and the child class).
        // try to set this up so that if the parent/child relationship does not exist
        // in OJB, we can issue a warning message and go on, and all the methods
        // will still behave properly
        public ParentClass(Class parentClass, Class childClass, Integer RequestYear) {
            // fill in the key field names
            // TODO: fix this--we need the child class as well as the parentClass
            ReturnedPair<String[], String[]> keyArrays = fetchForeignKeysToParent(childClass, parentClass);
            childKeyFields = keyArrays.getFirst();
            parentKeyFields = keyArrays.getSecond();
            if (childKeyFields != null) {
                // build a query to get the keys already added to the parent
                Criteria criteriaID = new Criteria();
                criteriaID.addEqualTo(KFSConstants.UNIV_FISCAL_YR, RequestYear);
                ReportQueryByCriteria queryID = new ReportQueryByCriteria(parentClass, parentKeyFields, criteriaID, true);
                // build a hash set of the keys in the parent
                parentKeys = new HashSet<String>(hashCapacity(queryID));
                Iterator parentRows = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
                while (parentRows.hasNext()) {
                    parentKeys.add(buildKeyString((Object[]) parentRows.next()));
                }
            }
        }

        private String buildChildTestKey(C ourBO) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            StringBuffer returnKey = new StringBuffer("");
            // we will convert all the keys to strings
            for (int i = 0; i < childKeyFields.length; i++) {
                returnKey.append(PropertyUtils.getProperty(ourBO, childKeyFields[i].toString()));
            }
            return returnKey.toString();
        }

        // method to test whether a key of the child row matches one in parent
        public boolean isInParent(C ourBO) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            if (childKeyFields == null) {
                return false;
            }
            return (parentKeys.contains(buildChildTestKey(ourBO)));
        }

    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /*******************************************************************************************************************************
     * private methods
     ******************************************************************************************************************************/


    private String buildKeyString(Object[] inKeys) {
        StringBuffer stringBuilder = new StringBuffer();
        // we always assume the first key is the fiscal year
        stringBuilder.append(((Integer) inKeys[0]).toString());
        for (int i = 0; i < inKeys.length; i++) {
            stringBuilder.append((String) inKeys[i]);
        }
        return stringBuilder.toString();
    }

    private ReturnedPair<String[], String[]> fetchForeignKeysToParent(Class childClass, Class parentClass) {
        String[] childKeyFields;
        String[] parentKeyFields;
        ReturnedPair<String[], String[]> returnObject = new ReturnedPair<String[], String[]>();
        // first we have to find the attribute name of the reference to the parent
        // class
        HashMap<String, Class> referenceObjects = (HashMap<String, Class>) persistenceStructureService.listReferenceObjectFields(childClass);
        String attributeName = null;
        String parentClassID = parentClass.getName();
        for (Map.Entry<String, Class> attributeMap : referenceObjects.entrySet()) {
            if (parentClassID.compareTo(attributeMap.getValue().getName()) == 0) {
                // the name of the parent class matches a reference class
                // this is the attribute we want
                attributeName = attributeMap.getKey();
                break;
            }
        }
        // now we have to use the attribute to look up the foreign keys
        if (attributeName == null) {
            // write a warning and return an empty key set
            LOG.warn(String.format("\n%s is not a child of %s\n", childClass.getName(), parentClassID));
            return returnObject;
        }
        HashMap<String, String> keyMap = (HashMap<String, String>) persistenceStructureService.getForeignKeysForReference(childClass, attributeName);
        childKeyFields = new String[keyMap.size()];
        parentKeyFields = new String[keyMap.size()];
        // the primary key names refer to parent fields
        // the foreign key names refer to child fields
        // (persistenceStructureService assumes that the child fields match the
        // parent primary key fields in order, AND that the first child field
        // corresponds to the first parent primary key field, the second to the
        // second, etc. this is apparently OJB's assumption as well.)
        int i = 0;
        for (Map.Entry<String, String> fkPkPair : keyMap.entrySet()) {
            childKeyFields[i] = fkPkPair.getKey();
            parentKeyFields[i] = fkPkPair.getValue();
            i = i + 1;
        }
        returnObject.setFirst(childKeyFields);
        returnObject.setSecond(parentKeyFields);
        return returnObject;
    }

    private Integer hashCapacity(ReportQueryByCriteria queryID) {
        // this corresponds to a load factor of a little more than the default load factor
        // of .75
        // (a rehash supposedly occurs when the actual number of elements exceeds
        // hashcapacity*(load factor). we want to avoid a rehash)
        // N rows < .75*capacity ==> capacity > 4N/3 or 1.3333N We add a little slop.
        Integer actualCount = new Integer(getPersistenceBrokerTemplate().getCount(queryID));
        return ((Integer) ((Double) (actualCount.floatValue() * (1.45))).intValue());
    }


    // this is a junk inner class that allows us to return two things from a method
    private class ReturnedPair<S, T> {
        S firstObject;
        T secondObject;

        public ReturnedPair() {
            this.firstObject = null;
            this.secondObject = null;
        }

        public S getFirst() {
            return this.firstObject;
        }

        public T getSecond() {
            return this.secondObject;
        }

        public void setFirst(S firstObject) {
            this.firstObject = firstObject;
        }

        public void setSecond(T secondObject) {
            this.secondObject = secondObject;
        }
    }


}
