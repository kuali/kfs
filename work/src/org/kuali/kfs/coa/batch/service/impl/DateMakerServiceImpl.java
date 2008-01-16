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
package org.kuali.module.chart.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.module.chart.dao.FiscalYearMakersCopyAction;
import org.kuali.module.chart.dao.FiscalYearMakersDao;
import org.kuali.module.chart.service.DateMakerService;
import org.kuali.module.gl.bo.UniversityDate;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the DataMakerService
 */
@Transactional
public class DateMakerServiceImpl implements DateMakerService {
    private FiscalYearMakersDao fiscalYearMakersDao;


    /**
     * 
     * @see org.kuali.module.chart.service.DateMakerService#fiscalYearMakers(boolean)
     */
    public void fiscalYearMakers(boolean replaceMode) {
        Integer BaseYear = fiscalYearMakersDao.fiscalYearFromToday();
        fiscalYearMakers(BaseYear, replaceMode);
    }


    /**
     *  
     * @see org.kuali.module.chart.service.DateMakerService#fiscalYearMakers(java.lang.Integer, boolean)
     */
    public void fiscalYearMakers(Integer baseYear, boolean replaceMode) {
        Integer requestYear = baseYear + 1;
        // (remember that replaceMode will have no effect for UniversityDate--any
        // new year rows in that table are always replaced)
        if (replaceMode) {
            // we are supposed to replace what is in the target year
            fiscalYearMakersDao.deleteNewYearRows(requestYear);
        }
        //
        // encapsulate this in a try structure
        // we need to undo any OJB structure changes we have made if there is an error
        try {
            // now, we get the copy order and call each object's copy action method
            // in turn
            LinkedHashMap<String, FiscalYearMakersCopyAction> copyOrder = fiscalYearMakersDao.setUpRun(baseYear, replaceMode);
            for (Map.Entry<String, FiscalYearMakersCopyAction> objectToCopy : copyOrder.entrySet()) {
                objectToCopy.getValue().copyMethod(baseYear, replaceMode);
                if (fiscalYearMakersDao.copyTwoYears(objectToCopy.getKey()))
                {
                    // this is one of the objects (or a parent of one of the objects) that is supposed to be copied for each
                    // of two years beyond the base year (at IU, UniversityDate is such an object). requestYear becomes the base year
                    objectToCopy.getValue().copyMethod(requestYear,replaceMode);
                }
            }
        }
        finally {
            // make certain we always do this
            fiscalYearMakersDao.resetCascades();
        }
    }

    /**
     * 
     * This method injects the FiscalYearMakersDao
     * @param fiscalYearMakersDao
     */
    public void setFiscalYearMakersDao(FiscalYearMakersDao fiscalYearMakersDao) {
        this.fiscalYearMakersDao = fiscalYearMakersDao;
    }

    // TODO: remove these
    public void testRoutine() {
        // here we test the date routine
        // Integer currentFiscalYear =
        // fiscalYearMakersDao.fiscalYearFromToday();
        // fiscalYearMakersDao.makeOptions(2043,true);
        // fiscalYearMakersDao.makeOptions(2044,true);
        // this.DateMaker(2042);
        // this.DateMaker(2043);
        // fiscalYearMakersDao.makeOptions(2045,true);
        // fiscalYearMakersDao.makeOptions(2046);
        // fiscalYearMakersDao.makeOptions(2047);
        // fiscalYearMakersDao.makeOptions(2048);
        // this.setFiscalYearStartDate(StartJanuary);
        // this.DateMaker(2046);
        // this.setFiscalYearStartDate(StartFebruary);
        // this.DateMaker(2048);
        try {
            fiscalYearMakersDao.testRIRelationships();
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace();
            RuntimeException rex = new RuntimeException("\nproblem in RI test");
            throw (rex);
        }
        catch (InvocationTargetException ex) {
            ex.printStackTrace();
            RuntimeException rex = new RuntimeException("\nproblem in RI test");
            throw (rex);
        }
        catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            RuntimeException rex = new RuntimeException("\nproblem in RI test");
            throw (rex);
        }
        ;
    }
}
