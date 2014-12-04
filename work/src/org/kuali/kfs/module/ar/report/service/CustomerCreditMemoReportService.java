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
package org.kuali.kfs.module.ar.report.service;

import java.io.File;
import java.util.Date;

import org.kuali.kfs.module.ar.report.util.CustomerCreditMemoReportDataHolder;

/**
 * Customer Credit Memo Report generation related method.
 */
public interface CustomerCreditMemoReportService {

    /**
     * This method generates the Customer Credit Memo PDF report
     * @param reportDataHolder contains data for the report
     * @param runDate run date for the report
     * @return report PDF
     */
    public File generateReport(CustomerCreditMemoReportDataHolder reportDataHolder, Date runDate);
}
