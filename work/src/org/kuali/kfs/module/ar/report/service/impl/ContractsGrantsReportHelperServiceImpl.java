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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.CollectionFormatter;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A number of methods which help the C&G Billing reports build their PDFs and do look-ups
 */
public class ContractsGrantsReportHelperServiceImpl implements ContractsGrantsReportHelperService {
    protected DataDictionaryService dataDictionaryService;
    protected ReportGenerationService reportGenerationService;
    protected ConfigurationService configurationService;
    protected DateTimeService dateTimeService;

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

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#createTitleText(java.lang.Class)
     */
    @Override
    public String createTitleText(Class<? extends BusinessObject> boClass) {
        String titleText = "";

        final String titlePrefixProp = getConfigurationService().getPropertyValueAsString("title.inquiry.url.value.prependtext");
        if (org.apache.commons.lang.StringUtils.isNotBlank(titlePrefixProp)) {
            titleText += titlePrefixProp + " ";
        }

        final String objectLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(boClass.getName()).getObjectLabel();
        if (org.apache.commons.lang.StringUtils.isNotBlank(objectLabel)) {
            titleText += objectLabel + " ";
        }

        return titleText;
    }

    /**
     *
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#formatByType(java.lang.Object, org.kuali.rice.core.web.format.Formatter)
     */
    @Override
    public String formatByType(Object prop, Formatter preferredFormatter) {
        Formatter formatter = preferredFormatter;
        // for Booleans, always use BooleanFormatter
        if (prop instanceof Boolean) {
            formatter = new BooleanFormatter();
        }

        // for Dates, always use DateFormatter
        if (prop instanceof Date) {
            formatter = new DateFormatter();
        }

        // for collection, use the list formatter if a formatter hasn't been defined yet
        if (prop instanceof Collection && ObjectUtils.isNull(formatter)) {
            formatter = new CollectionFormatter();
        }

        if (ObjectUtils.isNotNull(formatter)) {
            return (String)formatter.format(prop);
        }
        else {
            return prop.toString();
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#appendEndTimeToDate(java.lang.String)
     */
    @Override
    public String appendEndTimeToDate(String dateString) {
        String resultString = dateString;
        if (!org.apache.commons.lang.StringUtils.isBlank(dateString)) {
            Date endOfDay = DateUtils.addMilliseconds(DateUtils.ceiling(getDateTimeService().getCurrentDate(), Calendar.DATE), -1);
            String endOfDayTime = KFSConstants.BLANK_SPACE + getDateTimeService().toString(endOfDay, ArConstants.REPORT_TIME_FORMAT);
            // only add time string if it hasn't already been added (for some reason this method gets called twice when generating the pdf report)
            if (!org.apache.commons.lang.StringUtils.contains(dateString, endOfDayTime)) {
                resultString += endOfDayTime;
            }
        }
        return resultString;
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

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}