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
package org.kuali.module.budget.dao;

import org.apache.ojb.broker.query.Criteria;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

public interface FiscalYearInitiatorDao {

// this defines the database interface needed for fiscal year initiation
    // fetches an iterator over the rows for a business object from the database
    public Iterator dbFetch(Class childObject,Integer BaseYear);
    public Iterator dbFetch(Class childObject,Integer BaseYear, Criteria additionalCriterion);
    // stores an object in the database
    public void dbStore(Object child);
    // cleans out all the current rows for a given object in a given request year
    public void deleteAll(Class child, Integer RequestYear);
    // these is used to build a criterion for a where clause
    public class whereCriterion{};
    public class KeyObjectPair{};
    // an object used to check whether an child row's key value is in the parent class
    public class ParentClass{};
    //TODO: remove these two test methods
    public void displayProperties(Class boClass)
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;
    public void displayReferences(Class boClass, Class parentClass)
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
