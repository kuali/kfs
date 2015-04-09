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
package org.kuali.kfs.sys.service;

/**
 * This interface specifies a ReportWriterService that needs to be aware of the fiscal year when
 * generating reports.
 * 
 * Note that the implementing service may need to implement mechanisms to ensure that if multiple reports are 
 * generated using this service (in particular the same instance), that each report uses the proper fiscal year.
 */
public interface FiscalYearAwareReportWriterService extends ReportWriterService {
    /**
     * Sets the fiscal year
     * @param fiscalYear
     */
    public void setFiscalYear(Integer fiscalYear);
}
