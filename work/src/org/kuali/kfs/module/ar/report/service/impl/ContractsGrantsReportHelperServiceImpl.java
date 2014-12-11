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
package org.kuali.kfs.module.ar.report.service.impl;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.CollectionFormatter;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.springframework.util.StringUtils;

/**
 * A number of methods which help the C&G Billing reports build their PDFs and do look-ups
 */
public class ContractsGrantsReportHelperServiceImpl implements ContractsGrantsReportHelperService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsReportHelperServiceImpl.class);

    protected DataDictionaryService dataDictionaryService;
    protected ReportGenerationService reportGenerationService;
    protected ConfigurationService configurationService;
    protected DateTimeService dateTimeService;
    protected PersonService personService;

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
    public String correctEndDateForTime(String dateString) {
        try {
            final Date dateDate = DateUtils.addDays(dateTimeService.convertToDate(dateString), 1);
            final String newDateString = dateTimeService.toString(dateDate, KFSConstants.MONTH_DAY_YEAR_DATE_FORMAT);
            return newDateString;
        }
        catch (ParseException ex) {
            LOG.warn("invalid date format for errorDate: " + dateString);
        }
        return KFSConstants.EMPTY_STRING;
    }

    /**
     *
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#fixDateCriteria(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public String fixDateCriteria(String dateLowerBound, String dateUpperBound, boolean includeTime) {
        final String correctedUpperBound = includeTime && !org.apache.commons.lang.StringUtils.isBlank(dateUpperBound) ? correctEndDateForTime(dateUpperBound) : dateUpperBound;
        if (!org.apache.commons.lang.StringUtils.isBlank(dateLowerBound)) {
            if (!org.apache.commons.lang.StringUtils.isBlank(dateUpperBound)) {
                return dateLowerBound+SearchOperator.BETWEEN.op()+correctedUpperBound;
            } else {
                return SearchOperator.GREATER_THAN_EQUAL.op()+dateLowerBound;
            }
        } else {
            if (!org.apache.commons.lang.StringUtils.isBlank(dateUpperBound)) {
                return SearchOperator.LESS_THAN_EQUAL.op()+correctedUpperBound;
            }
        }
        return null;
    }


    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#lookupPrincipalIds(java.lang.String)
     */
    @Override
    public Set<String> lookupPrincipalIds(String principalName) {
        if (org.apache.commons.lang.StringUtils.isBlank(principalName)) {
            return new HashSet<String>();
        }

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KimConstants.UniqueKeyConstants.PRINCIPAL_NAME, principalName);
        final Collection<Person> peoples = getPersonService().findPeople(fieldValues);

        if (peoples == null || peoples.isEmpty()) {
            return new HashSet<String>();
        }

        Set<String> principalIdsSet = new HashSet<String>();
        for (Person person : peoples) {
            principalIdsSet.add(person.getPrincipalId());
        }

        return principalIdsSet;
    }

    @Override
    public String getDocSearchUrl(String docId) {
        String baseUrl = ConfigContext.getCurrentContextConfig().getKEWBaseURL() + "/" + KewApiConstants.DOC_HANDLER_REDIRECT_PAGE;
        Properties parameters = new Properties();
        parameters.put(KewApiConstants.COMMAND_PARAMETER, KewApiConstants.DOCSEARCH_COMMAND);
        parameters.put(KewApiConstants.DOCUMENT_ID_PARAMETER, docId);
        String docSearchUrl = UrlFactory.parameterizeUrl(baseUrl, parameters);
        return docSearchUrl;
    }

    @Override
    public String getInitiateCollectionActivityDocumentUrl(String proposalNumber, String invoiceNumber) {
        String initiateUrl = KRADConstants.EMPTY_STRING;
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.DOC_HANDLER_METHOD);
        if (!org.apache.commons.lang.StringUtils.isBlank(proposalNumber)) {
            parameters.put(ArPropertyConstants.ContractsGrantsCollectionActivityDocumentFields.SELECTED_PROPOSAL_NUMBER, proposalNumber);
        }
        parameters.put(ArPropertyConstants.ContractsGrantsCollectionActivityDocumentFields.SELECTED_INVOICE_DOCUMENT_NUMBER, invoiceNumber);
        parameters.put(KFSConstants.PARAMETER_COMMAND, KFSConstants.INITIATE_METHOD);
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_COLLECTION_ACTIVTY);
        final String baseUrl = !org.apache.commons.lang.StringUtils.isBlank(proposalNumber)
                ? getBaseContractsGrantsCollectionActivityDocumentUrl()
                : KFSConstants.EMPTY_STRING;
        initiateUrl = UrlFactory.parameterizeUrl(baseUrl, parameters);

        return initiateUrl;
    }

    /**
     * @return the base url for the contracts & grants collection activity document
     */
    protected String getBaseContractsGrantsCollectionActivityDocumentUrl() {
        return ArConstants.MultipleValueReturnActions.CONTRACTS_GRANTS_COLLECTION_ACTIVITY_INVOICES;
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

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

}
