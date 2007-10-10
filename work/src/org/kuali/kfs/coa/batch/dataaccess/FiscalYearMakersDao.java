/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.dao;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.lang.*;

/*  
 *   data access methods for fiscal year makers
 */
public interface FiscalYearMakersDao {

public static final boolean replaceMode=true;

//@@TODO: remove these test routines
   public void testUpdateTwoDigitYear(); 
   public void testRIRelationships()  throws NoSuchMethodException,
                                             IllegalAccessException,
                                             InvocationTargetException;

   
    public Integer fiscalYearFromToday();
    public void makeUniversityDate(GregorianCalendar newYearStartDate);
    

    /********************************************************************************
     *                      Routines for RI                                         *
     ********************************************************************************                      
     * some kuali objects represent reference tables keyed by fiscal period
     * (usually a fiscal year).
     * when a new fiscal period is created, these objects are copied from the predecessor
     * period so that it is not necessary for someone to type literally thousands of
     * rows into a maintenance document, changing only the fiscal period key.
     * if there is referential integrity in the data base, these objects must be copied
     * in a certain order.  the implementation of this interface allows us to "inject"
     * that order into a spring bean, and access and manipulate it during the copy
     * process (informally known as "fiscal year makers").
     * this object makes adding a new object to the process simpler, by allowing the
     * RI relationships to be expressed in XML.
     * 
     * ALL "fiscal year makers" objects should be included in this bean, even if they
     * have no RI relationship with any other "fiscal year maker" object.  The object
     * list from this bean is used to trigger the copying of every fiscal year maker
     * object.  
     */
    
    // fetch and set a map of child classes involved in fiscal year makers
    // (ALL such classes should be included, even those which have no parent(s)
    //  in the RI tree.)
    public HashMap<String,Class> getMakerObjectsList();
    public void setMakerObjectsList(HashMap<String,Class> makerObjectList);
    // fetch and set a map of child classes involved in fiscal year makers
    // and a list of their RI parents
    public HashMap<String,ArrayList<Class>> getChildParentMap();
    public void setChildParentArrayMap(HashMap<String,Class[]> childParentArrayMap);
    // the "lagging copy cycle" objects are those which are always one fiscal period
    // behind.  in other words, the base period for the other objects (the source 
    // period for the copy) is the request period for them (the target period for 
    // their copy).
    public void setLaggingCopyCycle (HashSet<String> laggingCopyCycle);
    // auto-update or auto-delete in OJB will interfere with the copy order prescribed above.
    // (Tables A and C may be a parents of Table B, but A may have no relation to C.  If A has
    //  an auto-xxx on B, then B will be written when A is, even if C is later in the copy order
    //  than A, and an RI exception will result.)  So, the code resets any auto-xxx properties
    //  on tables involved in a fiscal-year-makers parent-child relationship.
    //  this routine resets the original values at the end of fiscal year makers
    public void resetCascades();
    
    
    // when the reference objects for a base fiscal period are copied into the
    // next fiscal period, there is an option to delete all of the next period
    // objects that already exit and replace them with their base period counterparts
    // this method reads a list of the delete order which satisfies RI (i.e.,
    // the dependent object must be deleted before the parent object) and does the
    // job in the proper order.  
    public void deleteNewYearRows(Integer RequestYear);
    // when we want to copy two years at a time for some tables, we use this method
    // with "slash and burn" mode to delete any rows for the year after RequestYear
    // that already exist for the parents
    public void deleteYearAfterNewYearRowsForParents(Integer RequestYear,
                                                     Class childClass);
    // check to see whether the test class is in the parent list for the child class
    public boolean isAParentOf(String testClassName, Class childClass);

    
    // this returns the data structure containing a copy order that is consistent
    // with the RI relationships configured in the XML
    public LinkedHashMap<String,FiscalYearMakersCopyAction> 
    setUpRun(Integer BaseYear, boolean replaceMode);

}
