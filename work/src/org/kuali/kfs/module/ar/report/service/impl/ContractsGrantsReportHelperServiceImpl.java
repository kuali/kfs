/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A number of methods which help the C&G Billing reports build their PDFs
 */
public class ContractsGrantsReportHelperServiceImpl implements ContractsGrantsReportHelperService {
    protected DataDictionaryService dataDictionaryService;
    protected ReportGenerationService reportGenerationService;

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder, org.kuali.kfs.sys.report.ReportInfo, java.io.ByteArrayOutputStream)
     */
    @Override
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ReportInfo reportInfo, ByteArrayOutputStream baos) {
        Date runDate = new Date();

        String reportFileName = reportInfo.getReportFileName();
        String reportDirectory = reportInfo.getReportsDirectory();
        String reportTemplateClassPath = reportInfo.getReportTemplateClassPath();
        String reportTemplateName = reportInfo.getReportTemplateName();
        ResourceBundle resourceBundle = reportInfo.getResourceBundle();

        String subReportTemplateClassPath = reportInfo.getSubReportTemplateClassPath();
        Map<String, String> subReports = reportInfo.getSubReports();

        Map<String, Object> reportData = reportDataHolder.getReportData();
        // check title and set
        if (ObjectUtils.isNull(reportData.get(KFSConstants.REPORT_TITLE))) {
            reportData.put(KFSConstants.REPORT_TITLE, reportInfo.getReportTitle());
        }
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, "");

        List<String> data = Arrays.asList(KFSConstants.EMPTY_STRING);
        JRDataSource dataSource = new JRBeanCollectionDataSource(data);

        reportGenerationService.generateReportToOutputStream(reportData, dataSource, template, baos);

        return reportFileName;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#getFieldNameForSorting(int, java.lang.String)
     */
    @Override
    public String getFieldNameForSorting(int index, String businessObjectName) {
        BusinessObjectEntry boe = (BusinessObjectEntry) getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObjectName);
        List<String> lookupResultFields = boe.getLookupDefinition().getResultFieldNames();
        return lookupResultFields.get(index);
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#getListOfValuesSortedProperties(java.util.List, java.lang.String)
     */
    @Override
    public List<String> getListOfValuesSortedProperties(List list, String propertyName) {
        List<String> returnList = new ArrayList<String>();
        for (Object object : list) {
            if (!returnList.contains(getPropertyValue(object, propertyName))) {
                returnList.add(getPropertyValue(object, propertyName));
            }
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#getPropertyValue(java.lang.Object, java.lang.String)
     */
    @Override
    public String getPropertyValue(Object object, String propertyName) {
        Object fieldValue = ObjectUtils.getPropertyValue(object, propertyName);
        return (ObjectUtils.isNull(fieldValue)) ? "" : StringUtils.trimAllWhitespace(fieldValue.toString());
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }


    /**
     * @return reportGenerationService
     */
    public ReportGenerationService getReportGenerationService() {
        return reportGenerationService;
    }

    /**
     * @param reportGenerationService
     */
    public void setReportGenerationService(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }
}