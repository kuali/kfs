/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
