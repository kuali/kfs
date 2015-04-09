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
package org.kuali.kfs.gl.service;

import java.util.Collection;
import java.util.List;


/**
 * An interface which declares methods needed for using Balance
 */
public interface TrialBalanceService {

    /**
     * This method finds the balance records according to input fields an values
     *
     * @param fieldValues the input fields an values
     * @return the summary records of trial balance entries
     */
    List findTrialBalance(String selectedFiscalYear, String chartCode, String periodCode);

    /**
     * This method generate trial balance report in PDF format
     *
     * @param dataSource
     * @param fiscalYear
     * @param periodCode
     */
    String generateReportForExtractProcess(Collection dataSource, String fiscalYear, String periodCode);
}
