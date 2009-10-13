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
package org.kuali.kfs.coa.service;


/**
 * 
 * This service interface defines methods necessary for populating the prior year organization table with values from current year
 */
public interface PriorYearOrganizationService {

    /**
     * This method populates the prior year organization table in the database with all the values from the current year org table.
     */
    public void populatePriorYearOrganizationsFromCurrent();
}
