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
package org.kuali.module.chart.dao;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/*
 * data access methods for fiscal year makers
 */
public interface FiscalYearMakersDao {

    public static final boolean replaceMode = true;

    // @@TODO: remove these test routines
    public void testUpdateTwoDigitYear();

    public void testRIRelationships() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;


    /**
     * This method...
     * 
     * @return
     */
    public Integer fiscalYearFromToday();

    /**
     * this is the only routine that simply replaces what is there, if anything but, we have to do a delete--otherwise, we can get
     * an optimistic locking exception when we try to store a new row on top of something already in the database. we will delete by
     * fiscal year. the accounting period is assumed to correspond to the month, with the month of the start date being the first
     * period and the month of the last day of the fiscal year being the twelfth. the fiscal year tag is always the year of the
     * ending date of the fiscal year
     * 
     * @param newYearStartDate
     */
    public void makeUniversityDate(GregorianCalendar newYearStartDate);


    /*******************************************************************************************************************************
     * Routines for RI * ******************************************************************************* some kuali objects
     * represent reference tables keyed by fiscal period (usually a fiscal year). when a new fiscal period is created, these objects
     * are copied from the predecessor period so that it is not necessary for someone to type literally thousands of rows into a
     * maintenance document, changing only the fiscal period key. if there is referential integrity in the data base, these objects
     * must be copied in a certain order. the implementation of this interface allows us to "inject" that order into a spring bean,
     * and access and manipulate it during the copy process (informally known as "fiscal year makers"). this object makes adding a
     * new object to the process simpler, by allowing the RI relationships to be expressed in XML. ALL "fiscal year makers" objects
     * should be included in this bean, even if they have no RI relationship with any other "fiscal year maker" object. The object
     * list from this bean is used to trigger the copying of every fiscal year maker object.
     */

    /**
     *   return true if the business object class passed in is to be copied for the next two years instead of just one
     */
    public boolean copyTwoYears(String ClassName);
    /**
     * fetch and set a map of child classes involved in fiscal year makers (ALL such classes should be included, even those which
     * have no parent(s) in the RI tree.)
     * 
     * @return HashMap containing the
     */
    public HashMap<String, Class> getMakerObjectsList();

    /**
     * sets a property--the map of all the classes to be copied 
     * @param makerObjectList
     */
    public void setMakerObjectsList(HashMap<String, Class> makerObjectList);

    /**
     * fetch and set a map of child classes involved in fiscal year makers and a list of their RI parents
     * 
     * @return
     */
    public HashMap<String, ArrayList<Class>> getChildParentMap();

    /**
     * set a property--the map keyed on child pointing to an array of the child's parents
     * 
     * @param childParentArrayMap
     */
    public void setChildParentArrayMap(HashMap<String, Class[]> childParentArrayMap);
    
    /**
     * 
     * set a property--the list of objects to be copied for the next two years instead of just one (the RI parents will
     * also be copied for the next two years without explicitly being in this list)
     * @param copyTwoYearsList
     */
    public void setCopyTwoYearsList(HashSet<String> copyTwoYearsList);
    

    /**
     * the "lagging copy cycle" objects are those which are always one fiscal period behind. in other words, the base period for the
     * other objects (the source period for the copy) is the request period for them (the target period for their copy). This
     * method...
     * 
     * @param laggingCopyCycle
     */
    public void setLaggingCopyCycle(HashSet<String> laggingCopyCycle);
    
    /**
     * 
     * initilaizes the bean to allow some objects to be copied two years out
     */
    public void setUpTwoYearCopy();

    /**
     * auto-update or auto-delete in OJB will interfere with the copy order prescribed above. (Tables A and C may be a parents of
     * Table B, but A may have no relation to C. If A has an auto-xxx on B, then B will be written when A is, even if C is later in
     * the copy order than A, and an RI exception will result.) So, the code resets any auto-xxx properties on tables involved in a
     * fiscal-year-makers parent-child relationship. this routine resets the original values at the end of fiscal year makers
     */
    public void resetCascades();


    /**
     * when the reference objects for a base fiscal period are copied into the next fiscal period, there is an option to delete all
     * of the next period objects that already exit and replace them with their base period counterparts this method reads a list of
     * the delete order which satisfies RI (i.e., the dependent object must be deleted before the parent object) and does the job in
     * the proper order. This method...
     * 
     * @param RequestYear
     */
    public void deleteNewYearRows(Integer requestYear);

    /**
     * when we want to copy two years at a time for some tables, we use this method with "slash and burn" mode to delete any rows
     * for the year after RequestYear that already exist for the parents
     * 
     * @param RequestYear
     * @param childClass
     */
    public void deleteYearAfterNewYearRowsForParents(Integer requestYear, Class childClass);

    /**
     * This method checks to see whether the test class is in the parent list for the child class
     * 
     * @param testClassName
     * @param childClass
     * @return true if testClassName is a parent class of childClass
     */
    public boolean isAParentOf(String testClassName, Class childClass);


    /**
     * this returns the data structure containing a copy order that is consistent with the RI relationships configured in the XML
     * 
     * @param baseYear
     * @param replaceMode
     * @return
     */
    public LinkedHashMap<String, FiscalYearMakersCopyAction> setUpRun(Integer baseYear, boolean replaceMode);

}
