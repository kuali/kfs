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

import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.FAX_NUMBER;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.LODGING_TYPE_CODES;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.TRANSPORTATION_TYPE_CODES;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.report.EntertainmentHostCertificationReport;
import org.kuali.kfs.module.tem.report.NonEmployeeCertificationReport;
import org.kuali.kfs.module.tem.report.service.TravelEntertainmentHostCertificationService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of ExpenseSummaryReportService.
 */
@Transactional
public class TravelEntertainmentHostCertificationServiceImpl implements TravelEntertainmentHostCertificationService {

    public static Logger LOG = Logger.getLogger(TravelEntertainmentHostCertificationServiceImpl.class);

    private ConfigurationService configurationService;
    private ParameterService parameterService;
    private PersonService personService;
    private TravelDocumentService travelDocumentService;
    private ReportInfo entReportInfo;
    private TemProfileService temProfileService;

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService ConfigurationService) {
        this.configurationService = ConfigurationService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(final PersonService personService) {
        this.personService = personService;
    }

    /**
     * Creates a {@link ReportInfoHolder} instance that is used with the {@link TravelReportService}
     */
    @Override
    public EntertainmentHostCertificationReport buildReport(final TravelEntertainmentDocument document) {
        EntertainmentHostCertificationReport report = new EntertainmentHostCertificationReport();

        report.setTripId(document.getTravelDocumentIdentifier().toString());
        report.setPurpose(ObjectUtils.isNull(document.getPurpose())?KFSConstants.EMPTY_STRING:document.getPurpose().getPurposeDescription());
        report.setInstitution(getParameterService().getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KfsParameterConstants.INSTITUTION_NAME));
        report.setTemFaxNumber(getParameterService().getParameterValueAsString(TravelReimbursementDocument.class, FAX_NUMBER));
        report.setDocumentId(document.getDocumentNumber());
        report.setBeginDate(document.getTripBegin());
        report.setEndDate(document.getTripEnd());
        report.setEventId(document.getTravelDocumentIdentifier());
        report.setEventTitle(document.getEventTitle());
        report.setTotalExpense(document.getDocumentGrandTotal());
        report.setCertificationDescription(getTravelDocumentService().getMessageFrom(TemKeyConstants.TEM_ENT_HOST_CERTIFICATION,report.getInstitution() ));
        report.setEntertainmentHostName(document.getHostName());
        report.setEmployeeName(document.getTraveler().getFirstName()+" "+document.getTraveler().getLastName());

        report.setApprovingDepartment(getApprovingDepartment(document.getTraveler()));

        final List<NonEmployeeCertificationReport.Detail> expenseDetails = new ArrayList<NonEmployeeCertificationReport.Detail>();
        final Map<String,KualiDecimal> summaryData = new HashMap<String,KualiDecimal>();
        if(document.getActualExpenses().size()>0){
            for (final ActualExpense expense : document.getActualExpenses()) {
                expense.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE_OBJECT_CODE);
                final String expenseDate = new SimpleDateFormat("MM/dd/yyyy").format(expense.getExpenseDate());
                final NonEmployeeCertificationReport.Detail detail = new NonEmployeeCertificationReport.Detail(expense.getExpenseTypeObjectCode().getExpenseType().getName()==null?
                        "":expense.getExpenseTypeObjectCode().getExpenseType().getName(),expense.getExpenseAmount().multiply(expense.getCurrencyRate()), expenseDate);
                expenseDetails.add(detail);
                incrementSummary(summaryData, expense);
            }
        }
        else {
            expenseDetails.add(new NonEmployeeCertificationReport.Detail("",null,""));
        }
        report.setExpenseDetails(expenseDetails);
        return report;
    }

    public String getApprovingDepartment(final TravelerDetail traveler) {
        String approvingDepartment=KFSConstants.EMPTY_STRING;
        if(ObjectUtils.isNotNull(traveler)&&ObjectUtils.isNotNull(traveler.getPrincipalId())){
            TEMProfile profile=getTemProfileService().findTemProfileByPrincipalId(traveler.getPrincipalId());
            if(ObjectUtils.isNotNull(profile)&&ObjectUtils.isNotNull(profile.getHomeDepartment())) {
                approvingDepartment=profile.getHomeDepartment();
            }
        }else if(ObjectUtils.isNotNull(traveler)){
            Map<String,String> criteria=new HashMap<String, String>();
            criteria.put("firstName", traveler.getFirstName());
            criteria.put("lastName", traveler.getLastName());
            TEMProfile profile=getTemProfileService().findTemProfile(criteria);
            if(ObjectUtils.isNotNull(profile)&&ObjectUtils.isNotNull(profile.getHomeDepartment())) {
                approvingDepartment=profile.getHomeDepartment();
            }

        }
        return approvingDepartment;
    }

    protected void incrementSummary(final Map<String, KualiDecimal> summaryData, ActualExpense expense) {
        final String expenseDate = new SimpleDateFormat("MM/dd/yyyy").format(expense.getExpenseDate());
        KualiDecimal summaryAmount = summaryData.get(expenseDate);
        if (summaryAmount == null) {
            summaryAmount = KualiDecimal.ZERO;
        }
        summaryAmount = summaryAmount.add(expense.getExpenseAmount().multiply(expense.getCurrencyRate()));
        LOG.debug("Adding "+ summaryAmount+ " for "+ expenseDate+ " to summary data");
        summaryData.put(expenseDate, summaryAmount);
    }

    protected boolean isTransportationExpense(final ActualExpense expense) {
        LOG.debug("Checking if "+ expense+ " is a transportation ");
        return expenseTypeCodeMatchesParameter(expense.getTravelExpenseTypeCodeCode(), TRANSPORTATION_TYPE_CODES);
    }

    protected boolean isMealsExpense(final ActualExpense expense) {
        return getTravelDocumentService().isHostedMeal(expense);
    }

    protected boolean isLodgingExpense(final ActualExpense expense) {
        LOG.debug("Checking if "+ expense+ " is a lodging ");
        return expenseTypeCodeMatchesParameter(expense.getTravelExpenseTypeCodeCode(), LODGING_TYPE_CODES);
    }

    protected boolean expenseTypeCodeMatchesParameter(final String expenseTypeCode, final String parameter) {
        return getParameterService().getParameterValuesAsString(TravelReimbursementDocument.class, parameter).contains(expenseTypeCode);
    }

    /**
     * Gets the parameterService attribute.
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    /**
     * Gets the travelAuthorizationService attribute.
     * @return Returns the travelAuthorizationService.
     */
    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    /**
     * Sets the travelDocumentService attribute value.
     * @param travelDocumentService The travelDocumentService to set.
     */
    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    @Override
    public File generateEntertainmentHostCertReport(EntertainmentHostCertificationReport report) {
        String reportFileName;
        String reportDirectory;
        String reportTemplateClassPath;
        String reportTemplateName;

        String subReportTemplateClassPath;
        Map<String, String> subReports;

        reportFileName = entReportInfo.getReportFileName();
        reportDirectory = entReportInfo.getReportsDirectory();
        reportTemplateClassPath = entReportInfo.getReportTemplateClassPath();
        reportTemplateName = entReportInfo.getReportTemplateName();

        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put("report", report);

        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = getReportGenerationService().buildFullFileName(new Date(), reportDirectory, reportFileName, "");
        getReportGenerationService().generateReportToPdfFile(reportData,report.getExpenseDetails() ,template, fullReportFileName);

        File reportFile = new File(fullReportFileName+".pdf");
        return reportFile;
    }

    public ReportInfo getEntReportInfo() {
        return entReportInfo;
    }

    public void setEntReportInfo(ReportInfo entReportInfo) {
        this.entReportInfo = entReportInfo;
    }
    protected ReportGenerationService getReportGenerationService() {
        return SpringContext.getBean(ReportGenerationService.class);
    }

    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }


}
