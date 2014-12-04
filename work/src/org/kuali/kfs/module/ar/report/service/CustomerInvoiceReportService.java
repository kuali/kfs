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

import org.kuali.kfs.module.ar.report.util.CustomerInvoiceReportDataHolder;

/**
 * Customer Invoice Report generation related method.
 */
public interface CustomerInvoiceReportService {

    /**
     * This method generates a Customer Invoice report PDF with the given report data and runDate
     *
     * @param reportDataHolder the given report data holder
     * @param runDate the datetime of the report generation
     * @return report PDF
     */
    public File generateReport(CustomerInvoiceReportDataHolder reportDataHolder, Date runDate);
}
