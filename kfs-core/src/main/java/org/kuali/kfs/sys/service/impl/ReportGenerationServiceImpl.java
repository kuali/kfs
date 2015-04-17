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
package org.kuali.kfs.sys.service.impl;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.jasperreports.JasperReportsUtils;

/**
 * To provide utilities that can generate reports with JasperReport
 */
public class ReportGenerationServiceImpl implements ReportGenerationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportGenerationServiceImpl.class);

    protected DateTimeService dateTimeService;
    
    public final static String PARAMETER_NAME_SUBREPORT_DIR = ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR;
    public final static String PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME = ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME;

    public final static String DESIGN_FILE_EXTENSION = ReportGeneration.DESIGN_FILE_EXTENSION;
    public final static String JASPER_REPORT_EXTENSION = ReportGeneration.JASPER_REPORT_EXTENSION;
    public final static String PDF_FILE_EXTENSION = ReportGeneration.PDF_FILE_EXTENSION;
    
    public final static String SEPARATOR = "/";

    /**
     * @see org.kuali.kfs.sys.batch.service.ReportGenerationService#generateReportToPdfFile(java.util.Map, java.lang.String, java.lang.String)
     */
    public void generateReportToPdfFile(Map<String, Object> reportData, String template, String reportFileName) {
        List<String> data = Arrays.asList(KFSConstants.EMPTY_STRING);
        JRDataSource dataSource = new JRBeanCollectionDataSource(data);

        generateReportToPdfFile(reportData, dataSource, template, reportFileName);
    }

    /**
     * The dataSource can be an instance of JRDataSource, java.util.Collection or object array.
     * 
     * @see org.kuali.kfs.sys.batch.service.ReportGenerationService#generateReportToPdfFile(java.util.Map, java.lang.Object, java.lang.String,
     *      java.lang.String)
     */
    public void generateReportToPdfFile(Map<String, Object> reportData, Object dataSource, String template, String reportFileName) {
        ClassPathResource resource = getReportTemplateClassPathResource(template);
        if (resource == null || !resource.exists()) {
            throw new IllegalArgumentException("Cannot find the template file: " + template);
        }

        try {
            if (reportData != null && reportData.containsKey(PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME)) {
                Map<String, String> subReports = (Map<String, String>) reportData.get(PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME);
                String subReportDirectory = (String) reportData.get(PARAMETER_NAME_SUBREPORT_DIR);
                compileSubReports(subReports, subReportDirectory);
            }

            String designTemplateName = template.concat(DESIGN_FILE_EXTENSION);
            ByteArrayInputStream jasperReport = compileReportTemplate(designTemplateName);

            JRDataSource jrDataSource = JasperReportsUtils.convertReportData(dataSource);

            reportFileName = reportFileName + PDF_FILE_EXTENSION;
            File reportDirectory = new File(StringUtils.substringBeforeLast(reportFileName, SEPARATOR));
            if(!reportDirectory.exists()) {
                reportDirectory.mkdir();
            }
            
            JasperRunManager.runReportToPdfStream(jasperReport, new FileOutputStream(reportFileName), reportData, jrDataSource);
        }
        catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException("Fail to generate report.", e);
        }
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.ReportGenerationService#generateReportToOutputStream(java.util.Map, java.lang.Object,
     *      java.lang.String, java.io.ByteArrayOutputStream)
     */
    public void generateReportToOutputStream(Map<String, Object> reportData, Object dataSource, String template, ByteArrayOutputStream baos) {
        ClassPathResource resource = getReportTemplateClassPathResource(template);
        if (resource == null || !resource.exists()) {
            throw new IllegalArgumentException("Cannot find the template file: " + template);
        }

        try {
            if (reportData != null && reportData.containsKey(PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME)) {
                Map<String, String> subReports = (Map<String, String>) reportData.get(PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME);
                String subReportDirectory = (String) reportData.get(PARAMETER_NAME_SUBREPORT_DIR);
                final List<ByteArrayInputStream> subJasperReports = compileSubReports(subReports, subReportDirectory);
            }

            String designTemplateName = template.concat(DESIGN_FILE_EXTENSION);
            ByteArrayInputStream jasperReport = compileReportTemplate(designTemplateName);

            JRDataSource jrDataSource = JasperReportsUtils.convertReportData(dataSource);

            JasperRunManager.runReportToPdfStream(jasperReport, (OutputStream) baos, reportData, jrDataSource);
        }
        catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException("Fail to generate report.", e);
        }
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.ReportGenerationService#buildFullFileName()
     */
    @Override
    public String buildFullFileName(Date runDate, String directory, String fileName, String extension) {
        String runtimeStamp = dateTimeService.toDateTimeStringForFilename(runDate);
        String fileNamePattern = "{0}" + SEPARATOR + "{1}_{2}{3}";

        return MessageFormat.format(fileNamePattern, directory, fileName, runtimeStamp, extension);
    }

    /**
     * get a class path resource that references to the given report template
     * 
     * @param reportTemplateName the given report template name with its full-qualified package name. It may not include extension.
     *        If an extension is included in the name, it should be prefixed ".jasper" or '.jrxml".
     * @return a class path resource that references to the given report template
     */
    protected ClassPathResource getReportTemplateClassPathResource(String reportTemplateName) {
        if (reportTemplateName.endsWith(DESIGN_FILE_EXTENSION) || reportTemplateName.endsWith(JASPER_REPORT_EXTENSION)) {
            return new ClassPathResource(reportTemplateName);
        }

        String jasperReport = reportTemplateName.concat(JASPER_REPORT_EXTENSION);
        ClassPathResource resource = new ClassPathResource(jasperReport);
        if (resource.exists()) {
            return resource;
        }

        String designTemplate = reportTemplateName.concat(DESIGN_FILE_EXTENSION);
        resource = new ClassPathResource(designTemplate);
        return resource;
    }

    /**
     * complie the report template xml file into a Jasper report file if the compiled file does not exist or is out of update
     * 
     * @param designTemplate the full name of the report template xml file
     */
    protected ByteArrayInputStream compileReportTemplate(String designTemplate) throws JRException, IOException {
        ClassPathResource designTemplateResource = new ClassPathResource(designTemplate);

        if (!designTemplateResource.exists()) {
            throw new RuntimeException("The design template file does not exist: "+designTemplate);
        }

        ByteArrayOutputStream jasperReport = new ByteArrayOutputStream();
        JasperCompileManager.compileReportToStream(designTemplateResource.getInputStream(), jasperReport);
        return new ByteArrayInputStream(jasperReport.toByteArray());
    }

    /**
     * compile the given sub reports
     * 
     * @param subReports the sub report Map that hold the sub report templete names indexed with keys
     * @param subReportDirectory the directory where sub report templates are located
     */
    protected List<ByteArrayInputStream> compileSubReports(Map<String, String> subReports, String subReportDirectory) throws Exception {
        List<ByteArrayInputStream> jasperReports = new ArrayList<>();
        for (Map.Entry<String, String> entry: subReports.entrySet()) {
            final String designTemplateName = subReportDirectory + entry.getValue() + DESIGN_FILE_EXTENSION;

            jasperReports.add(compileReportTemplate(designTemplateName));
        }
        return jasperReports;
    }

    /**
     * Sets the DateTimeService
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
