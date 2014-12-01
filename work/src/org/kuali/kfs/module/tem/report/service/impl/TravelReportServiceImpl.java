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
package org.kuali.kfs.module.tem.report.service.impl;

import static net.sf.jasperreports.engine.JRExporterParameter.JASPER_PRINT_LIST;
import static net.sf.jasperreports.engine.JRExporterParameter.OUTPUT_STREAM;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.report.DetailedReport;
import org.kuali.kfs.module.tem.report.annotations.ReportStyle;
import org.kuali.kfs.module.tem.report.service.TravelReportFactoryService;
import org.kuali.kfs.module.tem.report.service.TravelReportService;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface for travel reports.
 *
 */
@Transactional
public class TravelReportServiceImpl implements TravelReportService {

    public static Logger LOG = Logger.getLogger(TravelReportServiceImpl.class);

    protected ReportGenerationService reportGenerationService;
    protected TravelReportFactoryService reportFactoryService;

    /**
     *
     * @param report
     * @param ByteArrayOutputStream
     */
    @Override
    public ByteArrayOutputStream buildReport(final ReportInfo report) throws Exception {
        final Collection reportSet           = new ArrayList(); // Empty data set. The report is essentially subreports
        final Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put("report", report);

        final Collection<JasperPrint> printObjs = new ArrayList<JasperPrint>();

        final Collection<Field> subreportFields = getReportFactoryService().getSubreportFieldsFrom(report);

        if (subreportFields.size() > 0) {
            LOG.info("######################################################################");
            LOG.info("# BEGINNING PROCESSING SUBREPORTS                                    #");
            LOG.info("######################################################################");
        }

        for (final Field subreportField : subreportFields) {
            subreportField.setAccessible(true);
            final JasperReport reportObj = getReportFactoryService().processReportForField(report, subreportField);
            if (reportObj != null) {
                reportObj.setWhenNoDataType(JasperReport.WHEN_NO_DATA_TYPE_NO_DATA_SECTION);

                printObjs.add(JasperFillManager.fillReport(reportObj, reportData, (JRDataSource) subreportField.get(report)));
            }
        }

        if (subreportFields.size() > 0) {
            LOG.info("######################################################################");
            LOG.info("# FINISHED PROCESSING SUBREPORTS                                     #");
            LOG.info("######################################################################");
        }

        boolean addMoreReports = true;
        int i = 0;
        while (addMoreReports) {
            final JasperDesign designObj = getReportFactoryService().designReport(report, i);

            if (designObj != null) {
                final JasperReport reportObj = JasperCompileManager.compileReport(designObj);
                reportObj.setWhenNoDataType(JasperReport.WHEN_NO_DATA_TYPE_NO_DATA_SECTION);
                if (report instanceof DetailedReport) {
                    printObjs.add(JasperFillManager.fillReport(reportObj, reportData, ((DetailedReport) report).getData()));
                }
            }
            else {
                addMoreReports = false;
            }
            i++;
        }



        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JASPER_PRINT_LIST, printObjs);
        exporter.setParameter(OUTPUT_STREAM, baos);
        try {
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
        finally {
            LOG.info("######################################################################");
            LOG.info("# FINISHED PROCESSING REPORT                                         #");
            LOG.info("######################################################################");
        }
        return baos;
    }

    public void setReportGenerationService(final ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }

    public ReportGenerationService getReportGenerationService() {
        return reportGenerationService;
    }

    protected boolean isCrosstabReport(final ReportInfo report) {
        return isCrosstabReport(report.getClass());
    }

    protected boolean isCrosstabReport(final Class reportClass) {
        if (reportClass.getAnnotation(ReportStyle.class) != null) {
        }
        return true;
    }

    /**
     * Gets the reportFactoryService property.
     * @return Returns the reportFactoryService.
     */
    public TravelReportFactoryService getReportFactoryService() {
        return reportFactoryService;
    }

    /**
     * Sets the reportFactoryService property value.
     * @param reportFactoryService The reportFactoryService to set.
     */
    public void setReportFactoryService(final TravelReportFactoryService reportFactoryService) {
        this.reportFactoryService = reportFactoryService;
    }
}
