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
package org.kuali.module.chart.service;

/**
 * 
 * This service interface defines methods necessary for copying fiscal year information
 */
public interface DateMakerService {

    /**
     * 
     * use this when you want to copy the current fiscal year (as of today's date)
     * into the coming fiscal year
     * replaceMode = true overrides what's there in the target year
     * = false only adds what isn't there, leaving anything existing for
     * the target year undisturbed
     * @param replaceMode
     */
    public void fiscalYearMakers(boolean replaceMode);

    /**
     * 
     * use this when you want to start with a specified base year (which does not necessarily correspond to the current fiscal
     * year). this could be used to implement a "budget year", for example = false only adds what isn't there, leaving anything
     * existing for the target year undisturbed
     * @param baseYear
     * @param replaceMode
     */
    public void fiscalYearMakers(Integer baseYear, boolean replaceMode);

    // TODO: remove these
    public void testRoutine();

}
