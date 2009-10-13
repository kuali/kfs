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
package org.kuali.kfs.module.bc.document.service.impl;

import org.kuali.kfs.module.bc.document.service.CalculatedSalaryFoundationTrackerOverrideService;

/**
 * This class...
 */
public class CalculatedSalaryFoundationTrackerOverrideServiceImpl implements CalculatedSalaryFoundationTrackerOverrideService {

    public boolean isValidAppointment(Integer universityFiscalYear, String positionNumber, String emplid) {

        if (emplid == null) {
            return false;
        }
        if (emplid.equals("VACANT")) {
            return true;
        }
        else {
            /* institution specific check for valid appointment */
            return true;
        }

    }

    public boolean isValidPosition(Integer universityFiscalYear, String positionNumber) {
        if (positionNumber == null || universityFiscalYear == null) {
            return false;
        }
        else {
            /* institution specific check for valid position */
            return true;
        }

    }
}
