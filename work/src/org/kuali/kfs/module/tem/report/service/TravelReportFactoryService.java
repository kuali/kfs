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
package org.kuali.kfs.module.tem.report.service;

import java.lang.reflect.Field;
import java.util.Collection;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.kuali.kfs.module.tem.report.RString;
import org.kuali.kfs.sys.report.ReportInfo;

/**
 * Service interface for travel reports.
 */
public interface TravelReportFactoryService {

    RString h1(final String str);

    RString h2(final String str);

    RString h3(final String str);

    RString h4(final String str);

    RString h5(final String str);

    /**
     * Populate the design of a report. Report's main content container is a design.
     *
     * @param report in
     * @return {@link JasperDesign} out
     * @throws Exception because there is a lot of under-the-hood I/O and reflection going on.
     */
    JasperDesign designSummary(final ReportInfo report) throws Exception;


    Field getFieldWithAnnotation(final ReportInfo report, final Class ... annotations);

    /**
     * @return JasperDesign
     */
    JasperDesign designReport(final ReportInfo report, final Integer reportIndex) throws Exception;

    Collection<Field> getSubreportFieldsFrom(final ReportInfo report) throws Exception;

    JasperReport processReportForField(final ReportInfo report, final Field field) throws Exception;

    /**
     * Determine whether a {@link Field} in the {@link ReportInfo} instance is a report {@link Summary} or not.
     *
     * @param field a {@link Field} instance in a {@link ReportInfo} class
     * @param true if the {@link Summary} annotation is on a {@link Field}
     */
    boolean isSummary(final Field field);

    /**
     * Determines if the {@link ReportInfo} instance has any {@link Summary} defined
     *
     * @param report to check for {@link Summary} in
     * @return true if there is a {@link Summary} or false otherwise
     */
    boolean hasSummary(final ReportInfo report);
}
