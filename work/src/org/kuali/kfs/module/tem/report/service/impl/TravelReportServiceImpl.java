/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.tem.report.service.impl;

import static net.sf.jasperreports.engine.JRExporterParameter.JASPER_PRINT_LIST;
import static net.sf.jasperreports.engine.JRExporterParameter.OUTPUT_STREAM;
import static org.kuali.kfs.module.tem.util.BufferedLogger.info;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import org.kuali.kfs.module.tem.report.DetailedReport;
import org.kuali.kfs.module.tem.report.annotations.ReportStyle;
import org.kuali.kfs.module.tem.report.annotations.Summary;
import org.kuali.kfs.module.tem.report.service.TravelReportFactoryService;
import org.kuali.kfs.module.tem.report.service.TravelReportService;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;

/**
 * Service interface for travel reports.
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class TravelReportServiceImpl implements TravelReportService {
    private ReportGenerationService reportGenerationService;
    private TravelReportFactoryService reportFactoryService;

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
            info("######################################################################");
            info("# BEGINNING PROCESSING SUBREPORTS                                    #");
            info("######################################################################");
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
            info("######################################################################");
            info("# FINISHED PROCESSING SUBREPORTS                                     #");
            info("######################################################################");
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
        
        if (getReportFactoryService().hasSummary(report)) {
            final JasperDesign designObj = getReportFactoryService().designSummary(report);            
            final JasperReport reportObj = JasperCompileManager.compileReport(designObj);
            reportObj.setWhenNoDataType(JasperReport.WHEN_NO_DATA_TYPE_NO_DATA_SECTION);
            
            final Field summaryField = getReportFactoryService().getFieldWithAnnotation(report, Summary.class);
            summaryField.setAccessible(true);
            printObjs.add(JasperFillManager.fillReport(reportObj, reportData, (JRDataSource) summaryField.get(report)));
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
            info("######################################################################");
            info("# FINISHED PROCESSING REPORT                                         #");
            info("######################################################################");
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
