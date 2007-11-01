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

import java.lang.reflect.InvocationTargetException;
import java.util.GregorianCalendar;

/*
 * data access methods for fiscal year makers
 */
public interface FiscalYearInitiatorDao {

    public static final boolean replaceMode = true;

    // @@TODO: remove this test routine
    public void testUpdateTwoDigitYear();

    public Integer fiscalYearFromToday();

    public void makeAccountingPeriod(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeAccountingPeriod(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeBenefitsCalculation(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeBenefitsCalculation(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeLaborObject(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeLaborObject(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeObjectCode(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeObjectCode(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeOffsetDefinition(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeOffsetDefinition(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeOptions(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeOptions(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeOrganizationReversion(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeOrganizationReversion(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makePositionObjectBenefit(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makePositionObjectBenefit(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeSubObjCd(Integer currentFiscalYear) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeSubObjCd(Integer currentFiscalYear, boolean replaceMode) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    public void makeUniversityDate(GregorianCalendar newYearStartDate);
}
