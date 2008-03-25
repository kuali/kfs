/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSConstants.ReportGeneration;
import org.kuali.kfs.service.ReportGenerationService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.jasperreports.JasperReportsUtils;

/**
 * To provide utilities that can generate reports with JasperReport
 */
public class ReportGenerationServiceImpl implements ReportGenerationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportGenerationServiceImpl.class);

    public final static String PARAMETER_NAME_SUBREPORT_DIR = ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR;
    public final static String PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME = ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME;

    public final static String DESIGN_FILE_EXTENSION = ReportGeneration.DESIGN_FILE_EXTENSION;
    public final static String JASPER_REPORT_EXTENSION = ReportGeneration.JASPER_REPORT_EXTENSION;
    public final static String PDF_FILE_EXTENSION = ReportGeneration.PDF_FILE_EXTENSION;

    /**
     * @see org.kuali.kfs.service.ReportGenerationService#generateReportToPdfFile(java.util.Map, java.lang.String, java.lang.String)
     */
    public void generateReportToPdfFile(Map<String, Object> reportData, String template, String reportFileName) {
        List<String> data = Arrays.asList(KFSConstants.EMPTY_STRING);
        JRDataSource dataSource = new JRBeanCollectionDataSource(data);

        generateReportToPdfFile(reportData, dataSource, template, reportFileName);
    }

    /**
     * The dataSource can be an instance of JRDataSource, java.util.Collection or object array.
     * 
     * @see org.kuali.kfs.service.ReportGenerationService#generateReportToPdfFile(java.util.Map, java.lang.Object, java.lang.String,
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

            String realTemplateNameWithoutExtension = removeTemplateExtension(resource);
            String designTemplateName = realTemplateNameWithoutExtension.concat(DESIGN_FILE_EXTENSION);
            String jasperReportName = realTemplateNameWithoutExtension.concat(JASPER_REPORT_EXTENSION);
            compileReportTemplate(designTemplateName, jasperReportName);

            JRDataSource jrDataSource = JasperReportsUtils.convertReportData(dataSource);

            reportFileName = reportFileName + PDF_FILE_EXTENSION;
            JasperRunManager.runReportToPdfFile(jasperReportName, reportFileName, reportData, jrDataSource);
        }
        catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @see org.kuali.kfs.service.ReportGenerationService#buildFullFileName(java.util.Date, java.lang.String, java.lang.String, java.lang.String)
     */
    public String buildFullFileName(Date runDate, String directory, String fileName, String extension) {
        String runtimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(runDate);
        String fileNamePattern = "{0}/{1}_{2}{3}";

        return MessageFormat.format(fileNamePattern, directory, fileName, runtimeStamp, "." + extension);
    }

    /**
     * get a class path resource that references to the given report template
     * 
     * @param reportTemplateName the given report template name with its full-qualified package name. It may not include extension.
     *        If an extension is included in the name, it should be prefixed ".jasper" or '.jrxml".
     * @return a class path resource that references to the given report template
     */
    private ClassPathResource getReportTemplateClassPathResource(String reportTemplateName) {
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
     * @param jasperReport the full name of the compiled report file
     */
    private void compileReportTemplate(String designTemplate, String jasperReport) throws JRException {
        File jasperFile = new File(jasperReport);
        File designFile = new File(designTemplate);

        if (!jasperFile.exists() && !designFile.exists()) {
            throw new RuntimeException("Both the design template file and jasper report file don't exist: (" + designTemplate + ", " + jasperReport + ")");
        }

        if (!jasperFile.exists() && designFile.exists()) {
            JasperCompileManager.compileReportToFile(designTemplate, jasperReport);
        }
        else if (jasperFile.exists() && designFile.exists()) {
            if (jasperFile.lastModified() < designFile.lastModified()) {
                JasperCompileManager.compileReportToFile(designTemplate, jasperReport);
            }
        }
    }

    /**
     * compile the given sub reports
     * 
     * @param subReports the sub report Map that hold the sub report templete names indexed with keys
     * @param subReportDirectory the directory where sub report templates are located
     */
    private void compileSubReports(Map<String, String> subReports, String subReportDirectory) throws Exception {
        for (String key : subReports.keySet()) {
            ClassPathResource resource = getReportTemplateClassPathResource(subReportDirectory + subReports.get(key));
            String realTemplateNameWithoutExtension = removeTemplateExtension(resource);

            String designTemplateName = realTemplateNameWithoutExtension.concat(DESIGN_FILE_EXTENSION);
            String jasperReportName = realTemplateNameWithoutExtension.concat(JASPER_REPORT_EXTENSION);

            compileReportTemplate(designTemplateName, jasperReportName);
        }
    }

    /**
     * remove the file extension of the given template if any
     * 
     * @param template the given template
     * @return the template without file extension
     */
    private String removeTemplateExtension(ClassPathResource template) throws IOException {
        String realTemplateName = template.getFile().getAbsolutePath();

        int lastIndex = realTemplateName.lastIndexOf(".");
        String realTemplateNameWithoutExtension = lastIndex > 0 ? realTemplateName.substring(0, lastIndex) : realTemplateName;

        return realTemplateNameWithoutExtension;
    }
}
