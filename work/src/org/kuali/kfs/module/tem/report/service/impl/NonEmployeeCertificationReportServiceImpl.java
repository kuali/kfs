/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.report.service.impl;

import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.FAX_NUMBER;
import static org.kuali.kfs.sys.KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.report.NonEmployeeCertificationReport;
import org.kuali.kfs.module.tem.report.service.NonEmployeeCertificationReportService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.util.ObjectUtils;

public class NonEmployeeCertificationReportServiceImpl implements NonEmployeeCertificationReportService{

    public static Logger LOG = Logger.getLogger(NonEmployeeCertificationReportServiceImpl.class);

    private ParameterService parameterService;
    private ReportInfo reportInfo;
    private PersonService personService;
    private OrganizationService organizationService;
    private TravelDocumentService travelDocumentService;
    private TemProfileService temProfileService;

    @Override
    public NonEmployeeCertificationReport buildReport(TravelDocument travelDocument) {
        NonEmployeeCertificationReport report = new NonEmployeeCertificationReport();

        report.setTripId(travelDocument.getTravelDocumentIdentifier().toString());
        report.setPurpose(travelDocument.getReportPurpose() == null ? "" : travelDocument.getReportPurpose());
        report.setInstitution(getParameterService().getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KfsParameterConstants.INSTITUTION_NAME));
        report.setCertificationDescription(getTravelDocumentService().getMessageFrom(TemKeyConstants.TEM_NON_EMPLOYEE_CERTIFICATION,report.getInstitution() ));

        report.setDocumentId(travelDocument.getTravelDocumentIdentifier().toString());
        report.setTraveler(travelDocument.getTraveler().getFirstName() + " " + travelDocument.getTraveler().getLastName());
        report.setBeginDate(travelDocument.getTripBegin());
        report.setEndDate(travelDocument.getTripEnd());
        report.setEventId(travelDocument.getTravelDocumentIdentifier().toString());
        report.setEventName(travelDocument.getDocumentTitle());
        report.setTotalExpense(travelDocument.getDocumentGrandTotal());

        if (travelDocument instanceof TravelReimbursementDocument) {
            report.setDestination(travelDocument.getPrimaryDestination().getPrimaryDestinationName());
            report.setEventType("TravelReimbursement");
        }else if (travelDocument instanceof TravelRelocationDocument) {
            report.setDestination(getDestination((TravelRelocationDocument) travelDocument));
            report.setEventType("TravelRelocation");
        }else if (travelDocument instanceof TravelEntertainmentDocument) {
            report.setEventType(((TravelEntertainmentDocument)travelDocument).getPurpose().getPurposeName());
            report.setEventName(((TravelEntertainmentDocument)travelDocument).getEventTitle());
        }

        //Lookup the Organization Name of the initiator's primary department for the Approving Department
        final String initiatorId = travelDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        final Person initiator = getPersonService().getPerson(initiatorId);
        Organization organization = getOrganizationService().getByPrimaryId(StringUtils.substringBefore(initiator.getPrimaryDepartmentCode(), "-"), StringUtils.substringAfter(initiator.getPrimaryDepartmentCode(), "-"));
        if(ObjectUtils.isNotNull(organization)) {
            report.setApprovingDepartment(organization.getOrganizationName());
        } else {
            report.setApprovingDepartment("N/A");
        }

        final List<NonEmployeeCertificationReport.Detail> other = new ArrayList<NonEmployeeCertificationReport.Detail>();
        final Map<String,KualiDecimal> summaryData = new HashMap<String,KualiDecimal>();

        if (travelDocument.getActualExpenses() != null) {
            for (final ActualExpense expense : travelDocument.getActualExpenses()) {
                expense.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE_OBJECT_CODE);
                final String expenseDate = new SimpleDateFormat("MM/dd").format(expense.getExpenseDate());
                final NonEmployeeCertificationReport.Detail detail = new NonEmployeeCertificationReport.Detail(expense.getExpenseTypeObjectCode().getExpenseType().getName()==null?
                        "":expense.getExpenseTypeObjectCode().getExpenseType().getName(), expense.getExpenseAmount().multiply(expense.getCurrencyRate()), expenseDate);

                other.add(detail);
                incrementSummary(summaryData, expense);
            }
        }

        if(other.size()==0) {
            other.add(new NonEmployeeCertificationReport.Detail("",null,""));
        }
        report.setExpenseDetails(other);

        return report;
    }

    public String getDestination(TravelRelocationDocument relocation){
        StringBuffer destination = new StringBuffer();

        if(relocation.getToAddress1() != null){
            destination.append(relocation.getToAddress1() + " ");
        }
        if(relocation.getToAddress2() != null){
            destination.append(relocation.getToAddress2() + " ");
        }
        if(relocation.getToCity() != null){
            destination.append(relocation.getToCity() + " ");
        }
        if(relocation.getToStateCode() != null){
            destination.append(relocation.getToStateCode() + " ");
        }
        if(relocation.getToCountryCode() != null){
            destination.append(relocation.getToCountryCode());
        }

        return destination.toString();
    }

    protected void incrementSummary(final Map<String, KualiDecimal> summaryData, ActualExpense expense) {
        final String expenseDate = new SimpleDateFormat("MM/dd").format(expense.getExpenseDate());
        KualiDecimal summaryAmount = summaryData.get(expenseDate);
        if (summaryAmount == null) {
            summaryAmount = KualiDecimal.ZERO;
        }
        summaryAmount = summaryAmount.add(expense.getExpenseAmount().multiply(expense.getCurrencyRate()));
        LOG.debug("Adding " + summaryAmount + " for " + expenseDate + " to summary data");
        summaryData.put(expenseDate, summaryAmount);
    }

    @Override
    public File generateReport(NonEmployeeCertificationReport report) {
        String reportFileName;
        String reportDirectory;
        String reportTemplateClassPath;
        String reportTemplateName;

        String subReportTemplateClassPath;
        Map<String, String> subReports;

        reportFileName = getReportInfo().getReportFileName();
        reportDirectory = getReportInfo().getReportsDirectory();
        reportTemplateClassPath = getReportInfo().getReportTemplateClassPath();
        reportTemplateName = getReportInfo().getReportTemplateName();

        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put("report", report);
        reportData.put("temFaxNumber", getParameterService().getParameterValueAsString(TravelReimbursementDocument.class, FAX_NUMBER));

        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = getReportGenerationService().buildFullFileName(new Date(), reportDirectory, reportFileName, "");
        getReportGenerationService().generateReportToPdfFile(reportData, report.getExpenseDetails(), template, fullReportFileName);
        File reportFile = new File(fullReportFileName+ PDF_FILE_EXTENSION);
        return reportFile;
    }

    /**
     *
     * This method...
     * @return
     */
    protected ReportGenerationService getReportGenerationService() {
        return SpringContext.getBean(ReportGenerationService.class);
    }

    /**
     *
     * This method...
     * @return
     */
    public ReportInfo getReportInfo(){
        return this.reportInfo;
    }

    /**
     *
     * This method...
     * @param reportInfo
     */
    public void setReportInfo(ReportInfo reportInfo){
        this.reportInfo = reportInfo;
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

    public String getApprovingDepartment(final TravelerDetail traveler) {
        String approvingDepartment=KFSConstants.EMPTY_STRING;
        if(ObjectUtils.isNotNull(traveler)&&ObjectUtils.isNotNull(traveler.getPrincipalId())){
            TemProfile profile=getTemProfileService().findTemProfileByPrincipalId(traveler.getPrincipalId());
            if(ObjectUtils.isNotNull(profile)&&ObjectUtils.isNotNull(profile.getHomeDepartment())) {
                approvingDepartment=profile.getHomeDepartment();
            }
        }else if(ObjectUtils.isNotNull(traveler)){
            Map<String,String> criteria=new HashMap<String, String>();
            criteria.put("firstName", traveler.getFirstName());
            criteria.put("lastName", traveler.getLastName());
            TemProfile profile=getTemProfileService().findTemProfile(criteria);
            if(ObjectUtils.isNotNull(profile)&&ObjectUtils.isNotNull(profile.getHomeDepartment())) {
                approvingDepartment=profile.getHomeDepartment();
            }
        }
        return approvingDepartment;
    }

	/**
	 * Gets the personService attribute.
	 * @return Returns the personService.
	 */
	public PersonService getPersonService() {
		return personService;
	}

	/**
	 * Sets the personService attribute value.
	 * @param personService The personService to set.
	 */
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	/**
	 * Gets the organizationService attribute.
	 * @return Returns the organizationService.
	 */
	public OrganizationService getOrganizationService() {
		return organizationService;
	}

	/**
	 * Sets the organizationService attribute value.
	 * @param organizationService The organizationService to set.
	 */
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	/**
	 * Gets the travelDocumentService attribute.
	 * @return Returns the travelDocumentService.
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

    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }
}
