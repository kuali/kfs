/*
 * Copyright 2007 The Kuali Foundation.
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

package org.kuali.module.kra.routingform.xml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.service.ChartUserService;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.routingform.bo.RoutingFormAgency;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganization;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormOtherCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.bo.RoutingFormProjectType;
import org.kuali.module.kra.routingform.bo.RoutingFormPurpose;
import org.kuali.module.kra.routingform.bo.RoutingFormQuestion;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchRisk;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchRiskStudy;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.lookup.keyvalues.RoutingFormApprovalStatusValuesFinder;
import org.kuali.module.kra.routingform.lookup.keyvalues.RoutingFormStudyReviewCodeValuesFinder;
import org.kuali.module.kra.routingform.service.RoutingFormMainPageService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.ActionTakenVO;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class creates an XML representation of a RoutingForm's data.
 */
public class RoutingFormXml {

    private static final String TO_BE_NAMED = "To Be Named";

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormXml.class);

    /**
     * Driving method for this class. Functions as a hub calling helper methods.
     * 
     * @param routingFormroutingFormDocumentDoc data representation of a routingForm
     * @param xmlDoc target xml representation for the routingForm. This field will be side effected.
     * @param imagesUrl location of the images the stylesheets use
     * @throws Exception
     */
    public static void makeXml(RoutingFormDocument routingFormDocument, Document xmlDoc, String imagesUrl) throws Exception {
        // Start of XML elements
        Element proposalElement = xmlDoc.createElement("PROPOSAL");
        xmlDoc.appendChild(proposalElement);

        Element routingFormElement = xmlDoc.createElement("ROUTING_FORM");
        proposalElement.appendChild(routingFormElement);

        routingFormElement.setAttribute("TRACKING_NUMBER", routingFormDocument.getDocumentNumber());
        routingFormElement.setAttribute("PROPOSAL_NUMBER", ObjectUtils.toString(routingFormDocument.getContractGrantProposal().getProposalNumber()));
        routingFormElement.setAttribute("LINKED_BUDGET_NUMBER", routingFormDocument.getRoutingFormBudgetNumber());

        // Code to get the current date/time
        Calendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();
        DateFormat localFormat = DateFormat.getDateTimeInstance();

        routingFormElement.setAttribute("XML_CREATE_DATE_TIME", localFormat.format(date));
        routingFormElement.setAttribute("IMAGES_URL", imagesUrl);

        routingFormElement.appendChild(createAgencyElement(routingFormDocument.getRoutingFormAgency(), routingFormDocument.getRoutingFormAnnouncementNumber(), xmlDoc));
        routingFormElement.appendChild(createPrinciplesElement(routingFormDocument, xmlDoc));
        routingFormElement.appendChild(createPurposeElement(routingFormDocument, xmlDoc));
        routingFormElement.appendChild(createProjectInformationElement(routingFormDocument, xmlDoc));
        routingFormElement.appendChild(createAmountsDatesElement(routingFormDocument.getRoutingFormBudget(), xmlDoc));
        routingFormElement.appendChild(createTypeElement(routingFormDocument, xmlDoc));
        routingFormElement.appendChild(createResearchRiskElement(routingFormDocument, xmlDoc));
        routingFormElement.appendChild(createProjectDetailElement(routingFormDocument, xmlDoc));
        routingFormElement.appendChild(createApprovalsElement(routingFormDocument, xmlDoc));
        routingFormElement.appendChild(createKeywordsElement(routingFormDocument.getRoutingFormKeywords(), xmlDoc));
        routingFormElement.appendChild(createCommentsElement(routingFormDocument, xmlDoc));
    }

    /**
     * Creates AGENCY node.
     * 
     * @param routingFormAgency
     * @param routingFormAnnouncementNumber
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createAgencyElement(RoutingFormAgency routingFormAgency, String routingFormAnnouncementNumber, Document xmlDoc) {
        Element agencyElement = xmlDoc.createElement("AGENCY");

        DateFormat dateFormatter = new SimpleDateFormat(KraConstants.SHORT_TIMESTAMP_FORMAT);

        if (routingFormAgency.getAgency() != null) {
            Element agencyDataElement = xmlDoc.createElement("AGENCY_DATA");
            agencyDataElement.setAttribute("AGENCY_NUMBER", routingFormAgency.getAgencyNumber());
            agencyDataElement.setAttribute("AGENCY_TYPE_CODE", routingFormAgency.getAgency().getAgencyTypeCode());
            agencyDataElement.setAttribute("PROGRAM_ANNOUNCEMENT_NUMBER", routingFormAnnouncementNumber);

            Element agencyFullNameElement = xmlDoc.createElement("AGENCY_FULL_NAME");
            agencyFullNameElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormAgency.getAgency().getFullName())));
            agencyDataElement.appendChild(agencyFullNameElement);

            agencyElement.appendChild(agencyDataElement);
        }

        Element agencyDueDateElement = xmlDoc.createElement("DUE_DATE");
        agencyDueDateElement.setAttribute("DUE_DATE_TYPE", routingFormAgency.getDueDateType() == null ? "" : routingFormAgency.getDueDateType().getDueDateDescription());
        agencyDueDateElement.setAttribute("DUE_DATE", routingFormAgency.getRoutingFormDueDate() == null ? "" : dateFormatter.format(routingFormAgency.getRoutingFormDueDate()));
        // following field is dropped in KRA but per request preserved for Indiana University ERA implementation.
        agencyDueDateElement.setAttribute("DUE_TIME", routingFormAgency.getRoutingFormDueTime());
        agencyElement.appendChild(agencyDueDateElement);

        Element agencyDeliveryElement = xmlDoc.createElement("AGENCY_DELIVERY");
        agencyDeliveryElement.setAttribute("COPIES", routingFormAgency.getRoutingFormRequiredCopyText());

        Element agencyDeliveryInstructionsElement = xmlDoc.createElement("DELIVERY_INSTRUCTIONS");
        agencyDeliveryInstructionsElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormAgency.getAgencyAddressDescription())));
        agencyDeliveryElement.appendChild(agencyDeliveryInstructionsElement);

        Element agencyAdditionalDeliveryInstructionsElement = xmlDoc.createElement("ADDITIONAL_DELIVERY_INSTRUCTIONS");
        agencyAdditionalDeliveryInstructionsElement.setAttribute("DISK_INCLUDED_IND", formatBoolean(routingFormAgency.getAgencyDiskAccompanyIndicator()));
        agencyAdditionalDeliveryInstructionsElement.setAttribute("ELECTRONIC_SUBMISSIONS_IND", formatBoolean(routingFormAgency.getAgencyElectronicSubmissionIndicator()));
        agencyAdditionalDeliveryInstructionsElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormAgency.getAgencyShippingInstructionsDescription())));
        agencyDeliveryElement.appendChild(agencyAdditionalDeliveryInstructionsElement);

        agencyElement.appendChild(agencyDeliveryElement);

        return agencyElement;
    }

    /**
     * Creates PRINCIPLES node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createPrinciplesElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element principlesElement = xmlDoc.createElement("PRINCIPLES");

        final String PERSON_ROLE_CODE_COPD = SpringContext.getBean(ParameterService.class).getParameterValue(RoutingFormDocument.class, KraConstants.PERSON_ROLE_CODE_CO_PROJECT_DIRECTOR);

        RoutingFormMainPageService routingFormMainPageService = SpringContext.getBean(RoutingFormMainPageService.class);
        List<RoutingFormPersonnel> routingFormPersonnel = routingFormDocument.getRoutingFormPersonnel();
        RoutingFormPersonnel projectDirector = routingFormMainPageService.getProjectDirector(routingFormPersonnel);

        principlesElement.setAttribute("CO-PD_IND", formatBoolean(routingFormMainPageService.checkCoPdExistance(routingFormPersonnel)));

        Element projectDirectorElement = xmlDoc.createElement("PROJECT_DIRECTOR");
        if (projectDirector != null) {
            if (projectDirector.isPersonToBeNamedIndicator()) {
                projectDirectorElement.setAttribute("FIRST_NAME", TO_BE_NAMED);
                projectDirectorElement.setAttribute("PERCENT_CREDIT", ObjectUtils.toString(projectDirector.getPersonCreditPercent()));
            }
            else {
                projectDirectorElement.setAttribute("FIRST_NAME", ObjectUtils.toString(projectDirector.getUser().getPersonFirstName()));
                projectDirectorElement.setAttribute("LAST_NAME", ObjectUtils.toString(projectDirector.getUser().getPersonLastName()));
                projectDirectorElement.setAttribute("PERCENT_CREDIT", ObjectUtils.toString(projectDirector.getPersonCreditPercent()));

                Element homeOrgElement = xmlDoc.createElement("HOME_ORG");
                String chart = "";
                String org = "";
                if (projectDirector.getUser().getModuleUser(ChartUser.MODULE_ID) != null) {
                    chart = ((ChartUser) projectDirector.getUser().getModuleUser(ChartUser.MODULE_ID)).getChartOfAccountsCode();
                    org = ((ChartUser) projectDirector.getUser().getModuleUser(ChartUser.MODULE_ID)).getOrganizationCode();
                }
                else {
                    chart = SpringContext.getBean(ChartUserService.class).getDefaultChartCode(projectDirector.getUser());
                    org = SpringContext.getBean(ChartUserService.class).getDefaultOrganizationCode(projectDirector.getUser());
                }

                homeOrgElement.setAttribute("HOME_CHART", ObjectUtils.toString(chart));
                homeOrgElement.setAttribute("HOME_ORG", ObjectUtils.toString(org));
                projectDirectorElement.appendChild(homeOrgElement);
            }

            Element pdCampusAddressElement = xmlDoc.createElement("PD_CAMPUS_ADDRESS");
            pdCampusAddressElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(projectDirector.getPersonLine1Address())));
            projectDirectorElement.appendChild(pdCampusAddressElement);

            Element pdPhoneElement = xmlDoc.createElement("PD_PHONE");
            pdPhoneElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(projectDirector.getPersonPhoneNumber())));
            projectDirectorElement.appendChild(pdPhoneElement);

            Element pdEmailElement = xmlDoc.createElement("PD_EMAIL");
            pdEmailElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(projectDirector.getPersonEmailAddress())));
            projectDirectorElement.appendChild(pdEmailElement);

            Element submittingOrgElement = xmlDoc.createElement("SUBMITTING_ORG");
            submittingOrgElement.setAttribute("SUBMITTING_CHART", ObjectUtils.toString(projectDirector.getChartOfAccountsCode()));
            submittingOrgElement.setAttribute("SUBMITTING_ORG", ObjectUtils.toString(projectDirector.getOrganizationCode()));
            submittingOrgElement.appendChild(xmlDoc.createTextNode(projectDirector.getOrganization() == null ? "" : projectDirector.getOrganization().getOrganizationName()));
            projectDirectorElement.appendChild(submittingOrgElement);
        }
        principlesElement.appendChild(projectDirectorElement);

        Element coPdsElement = xmlDoc.createElement("CO-PROJECT_DIRECTORS");
        for (RoutingFormPersonnel person : routingFormDocument.getRoutingFormPersonnel()) {
            if (PERSON_ROLE_CODE_COPD.equals(person.getPersonRoleCode())) {
                Element coPdElement = xmlDoc.createElement("CO-PROJECT_DIRECTOR");

                if (person.isPersonToBeNamedIndicator()) {
                    coPdElement.setAttribute("FIRST_NAME", TO_BE_NAMED);
                }
                else {
                    coPdElement.setAttribute("FIRST_NAME", ObjectUtils.toString(person.getUser().getPersonFirstName()));
                    coPdElement.setAttribute("LAST_NAME", ObjectUtils.toString(person.getUser().getPersonLastName()));
                }

                coPdElement.setAttribute("CHART", ObjectUtils.toString(person.getChartOfAccountsCode()));
                coPdElement.setAttribute("ORG", ObjectUtils.toString(person.getOrganizationCode()));
                coPdElement.setAttribute("PERCENT_CREDIT", ObjectUtils.toString(person.getPersonCreditPercent()));

                coPdsElement.appendChild(coPdElement);
            }
        }
        principlesElement.appendChild(coPdsElement);

        RoutingFormPersonnel contactPerson = routingFormMainPageService.getContactPerson(routingFormPersonnel);

        Element contactPersonElement = xmlDoc.createElement("CONTACT_PERSON");
        if (contactPerson != null) {
            if (contactPerson.isPersonToBeNamedIndicator()) {
                contactPersonElement.setAttribute("FIRST_NAME", TO_BE_NAMED);
            }
            else {
                contactPersonElement.setAttribute("FIRST_NAME", ObjectUtils.toString(contactPerson.getUser().getPersonFirstName()));
                contactPersonElement.setAttribute("LAST_NAME", ObjectUtils.toString(contactPerson.getUser().getPersonLastName()));
            }
            contactPersonElement.setAttribute("EMAIL", ObjectUtils.toString(contactPerson.getPersonEmailAddress()));
            contactPersonElement.setAttribute("PHONE_NUMBER", ObjectUtils.toString(contactPerson.getPersonPhoneNumber()));
            contactPersonElement.setAttribute("FAX_NUMBER", ObjectUtils.toString(contactPerson.getPersonFaxNumber()));
        }
        principlesElement.appendChild(contactPersonElement);

        Element fellowDescriptionElement = xmlDoc.createElement("FELLOW");
        fellowDescriptionElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getRoutingFormFellowFullName())));
        principlesElement.appendChild(fellowDescriptionElement);

        return principlesElement;
    }

    /**
     * Creates PURPOSE node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createPurposeElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element purposesElement = xmlDoc.createElement("PURPOSES");

        for (RoutingFormPurpose routingFormPurpose : routingFormDocument.getRoutingFormPurposes()) {
            Element purposeElement = xmlDoc.createElement("PURPOSE");

            purposeElement.setAttribute("SELECTED", formatBoolean(routingFormPurpose.getPurposeCode().equals(routingFormDocument.getRoutingFormPurposeCode())));
            purposeElement.setAttribute("CODE", ObjectUtils.toString(routingFormPurpose.getPurposeCode()));
            purposeElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormPurpose.getPurpose().getPurposeDescription())));

            purposesElement.appendChild(purposeElement);
        }

        Element purposeDescriptionElement = xmlDoc.createElement("PURPOSE_OTHER_DESCRIPTION");
        purposeDescriptionElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getRoutingFormOtherPurposeDescription())));
        purposesElement.appendChild(purposeDescriptionElement);

        // Research Type dropdown omitted since it was not present in ERA.

        return purposesElement;
    }

    /**
     * Creates PROJECT_INFORMATION node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createProjectInformationElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element projectInformationElement = xmlDoc.createElement("PROJECT_INFORMATION");

        projectInformationElement.setAttribute("CFDA_TXT", routingFormDocument.getRoutingFormCatalogOfFederalDomesticAssistanceNumber());

        Element projectTitleElement = xmlDoc.createElement("PROJECT_TITLE");
        projectTitleElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getRoutingFormProjectTitle())));
        projectInformationElement.appendChild(projectTitleElement);

        Element layDescription = xmlDoc.createElement("LAY_DESCRIPTION");
        layDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getRoutingFormLayDescription())));
        projectInformationElement.appendChild(layDescription);

        Element abstractDescription = xmlDoc.createElement("ABSTRACT");
        abstractDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getProjectAbstract())));
        projectInformationElement.appendChild(abstractDescription);

        return projectInformationElement;
    }

    /**
     * Creates AMOUNTS_DATES node. Only puts current period into XML per functional specification.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createAmountsDatesElement(RoutingFormBudget routingFormBudget, Document xmlDoc) {
        Element amountsDatesElement = xmlDoc.createElement("AMOUNTS_DATES");

        DateFormat dateFormatter = new SimpleDateFormat(KraConstants.SHORT_TIMESTAMP_FORMAT);

        Element directCostsDescription = xmlDoc.createElement("DIRECT_COSTS");
        directCostsDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormBudget.getRoutingFormBudgetDirectAmount())));
        amountsDatesElement.appendChild(directCostsDescription);

        Element indirectCostsDescription = xmlDoc.createElement("INDIRECT_COSTS");
        indirectCostsDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormBudget.getRoutingFormBudgetIndirectCostAmount())));
        amountsDatesElement.appendChild(indirectCostsDescription);

        Element totalCostsDescription = xmlDoc.createElement("TOTAL_COSTS");
        totalCostsDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormBudget.getTotalCostsCurrentPeriod())));
        amountsDatesElement.appendChild(totalCostsDescription);

        Element startDateDescription = xmlDoc.createElement("START_DATE");
        if (routingFormBudget.getRoutingFormBudgetStartDate() != null) {
            startDateDescription.appendChild(xmlDoc.createTextNode(dateFormatter.format(routingFormBudget.getRoutingFormBudgetStartDate())));
        }
        amountsDatesElement.appendChild(startDateDescription);

        Element endDateDescription = xmlDoc.createElement("STOP_DATE");
        if (routingFormBudget.getRoutingFormBudgetEndDate() != null) {
            endDateDescription.appendChild(xmlDoc.createTextNode(dateFormatter.format(routingFormBudget.getRoutingFormBudgetEndDate())));
        }
        amountsDatesElement.appendChild(endDateDescription);

        return amountsDatesElement;
    }

    /**
     * Creates TYPES node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createTypeElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element typesElement = xmlDoc.createElement("TYPES");

        for (RoutingFormProjectType routingFormProjectType : routingFormDocument.getRoutingFormProjectTypes()) {
            Element typeElement = xmlDoc.createElement("TYPE");

            typeElement.setAttribute("CODE", routingFormProjectType.getProjectTypeCode());
            typeElement.setAttribute("SELECTED", formatBoolean(routingFormProjectType.isProjectTypeSelectedIndicator()));
            typeElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormProjectType.getProjectType().getProjectTypeDescription())));

            typesElement.appendChild(typeElement);
        }

        Element typeOtherTextDescription = xmlDoc.createElement("TYPE_OTHER_DESCRIPTION");
        typeOtherTextDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getProjectTypeOtherDescription())));
        typesElement.appendChild(typeOtherTextDescription);

        Element priorGrantDescription = xmlDoc.createElement("PRIOR_GRANT");
        priorGrantDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getRoutingFormPriorGrantNumber())));
        typesElement.appendChild(priorGrantDescription);

        Element currentGrantDescription = xmlDoc.createElement("CURRENT_GRANT");
        currentGrantDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getGrantNumber())));
        typesElement.appendChild(currentGrantDescription);

        Element institutionAccountDescription = xmlDoc.createElement("INSTITUTION_ACCOUNT");
        institutionAccountDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getInstitutionAccountNumber())));
        typesElement.appendChild(institutionAccountDescription);

        Element currentProposalDescription = xmlDoc.createElement("CURRENT_PROPOSAL");
        currentProposalDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getContractGrantProposal().getProposalNumber())));
        typesElement.appendChild(currentProposalDescription);

        return typesElement;
    }

    /**
     * Creates RESEARCH_RISK node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createResearchRiskElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element researchRisksElement = xmlDoc.createElement("RESEARCH_RISKS");

        boolean anyStudySelected = false;
        DateFormat dateFormatter = new SimpleDateFormat(KraConstants.SHORT_TIMESTAMP_FORMAT);
        RoutingFormApprovalStatusValuesFinder routingFormApprovalStatusValuesFinder = new RoutingFormApprovalStatusValuesFinder();
        RoutingFormStudyReviewCodeValuesFinder routingFormStudyReviewCodeValuesFinder = new RoutingFormStudyReviewCodeValuesFinder();

        for (RoutingFormResearchRisk routingFormResearchRisk : routingFormDocument.getRoutingFormResearchRisks()) {
            if (KraConstants.RESEARCH_RISK_TYPE_DESCRIPTION.equals(routingFormResearchRisk.getResearchRiskType().getControlAttributeTypeCode())) {
                Element researchRiskElement = xmlDoc.createElement("RESEARCH_RISK");

                researchRiskElement.setAttribute("SELECTED", formatBoolean(StringUtils.isNotEmpty(routingFormResearchRisk.getResearchRiskDescription())));
                researchRiskElement.setAttribute("CTRL_ATTRIB_TYPE_CODE", routingFormResearchRisk.getResearchRiskType().getControlAttributeTypeCode());
                researchRiskElement.setAttribute("TYPE_DESCRIPTION", ObjectUtils.toString(routingFormResearchRisk.getResearchRiskType().getResearchRiskTypeDescription()));

                Element textDescription = xmlDoc.createElement("TEXT");
                textDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormResearchRisk.getResearchRiskDescription())));
                researchRiskElement.appendChild(textDescription);

                researchRisksElement.appendChild(researchRiskElement);
            }
            else if (KraConstants.RESEARCH_RISK_TYPE_ALL_COLUMNS.equals(routingFormResearchRisk.getResearchRiskType().getControlAttributeTypeCode()) || KraConstants.RESEARCH_RISK_TYPE_SOME_COLUMNS.equals(routingFormResearchRisk.getResearchRiskType().getControlAttributeTypeCode())) {
                Element researchRiskElement = xmlDoc.createElement("RESEARCH_RISK");

                boolean selected = routingFormResearchRisk.getResearchRiskStudies().size() > 0;
                anyStudySelected |= selected;

                researchRiskElement.setAttribute("SELECTED", formatBoolean(selected));
                researchRiskElement.setAttribute("CTRL_ATTRIB_TYPE_CODE", routingFormResearchRisk.getResearchRiskType().getControlAttributeTypeCode());
                researchRiskElement.setAttribute("TYPE_DESCRIPTION", ObjectUtils.toString(routingFormResearchRisk.getResearchRiskType().getResearchRiskTypeDescription()));

                for (RoutingFormResearchRiskStudy routingFormResearchRiskStudy : routingFormResearchRisk.getResearchRiskStudies()) {
                    Element studyElement = xmlDoc.createElement("STUDY");

                    Element studyNumber = xmlDoc.createElement("STUDY_NUMBER");
                    studyNumber.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormResearchRiskStudy.getResearchRiskStudyNumber())));
                    studyElement.appendChild(studyNumber);

                    String routingFormApprovalStatus = routingFormApprovalStatusValuesFinder.getKeyLabel(routingFormResearchRiskStudy.getResearchRiskStudyApprovalStatusCode());
                    Element approvalStatus = xmlDoc.createElement("APPROVAL_STATUS");
                    approvalStatus.appendChild(xmlDoc.createTextNode(routingFormApprovalStatus));
                    studyElement.appendChild(approvalStatus);

                    Element approvalDate = xmlDoc.createElement("APPROVAL_DATE");
                    approvalDate.appendChild(xmlDoc.createTextNode(routingFormResearchRiskStudy.getResearchRiskStudyApprovalDate() == null ? "" : dateFormatter.format(routingFormResearchRiskStudy.getResearchRiskStudyApprovalDate())));
                    studyElement.appendChild(approvalDate);

                    Element expirationDate = xmlDoc.createElement("EXPIRATION_DATE");
                    expirationDate.appendChild(xmlDoc.createTextNode(routingFormResearchRiskStudy.getResearchRiskStudyExpirationDate() == null ? "" : dateFormatter.format(routingFormResearchRiskStudy.getResearchRiskStudyExpirationDate())));
                    studyElement.appendChild(expirationDate);

                    String routingFormStudyReviewCode = routingFormStudyReviewCodeValuesFinder.getKeyLabel(routingFormResearchRiskStudy.getResearchRiskStudyReviewCode());
                    Element studyReviewStatus = xmlDoc.createElement("STUDY_REVIEW_STATUS");
                    studyReviewStatus.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormStudyReviewCode)));
                    studyElement.appendChild(studyReviewStatus);

                    Element exemptionNbr = xmlDoc.createElement("EXEMPTION_NBR");
                    exemptionNbr.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormResearchRiskStudy.getResearchRiskExemptionNumber())));
                    studyElement.appendChild(exemptionNbr);

                    researchRiskElement.appendChild(studyElement);
                }

                researchRisksElement.appendChild(researchRiskElement);
            }
            else {
                LOG.warn("Found unknown controlAttributeTypeCode, ignoring: " + routingFormResearchRisk.getResearchRiskType().getControlAttributeTypeCode());
            }
        }

        researchRisksElement.setAttribute("ANY_STUDY_SELECTED", formatBoolean(anyStudySelected));

        return researchRisksElement;
    }

    /**
     * Creates PROJECT_DETAIL node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createProjectDetailElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element projectDetailElement = xmlDoc.createElement("PROJECT_DETAIL");

        for (RoutingFormQuestion routingFormQuestion : routingFormDocument.getRoutingFormQuestions()) {
            Element questionElement = xmlDoc.createElement("QUESTION");

            questionElement.setAttribute("SELECTED", ObjectUtils.toString(routingFormQuestion.getYesNoIndicator()));
            questionElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormQuestion.getQuestion().getQuestionTypeDescription())));

            projectDetailElement.appendChild(questionElement);
        }

        for (RoutingFormSubcontractor routingFormSubcontractor : routingFormDocument.getRoutingFormSubcontractors()) {
            Element subcontractorElement = xmlDoc.createElement("SUBCONTRACTOR");

            subcontractorElement.setAttribute("SOURCE", ObjectUtils.toString(routingFormSubcontractor.getSubcontractor().getSubcontractorName()));
            subcontractorElement.setAttribute("AMOUNT", ObjectUtils.toString(routingFormSubcontractor.getRoutingFormSubcontractorAmount()));

            projectDetailElement.appendChild(subcontractorElement);
        }

        for (RoutingFormOrganization routingFormOrganization : routingFormDocument.getRoutingFormOrganizations()) {
            Element otherInstOrgElement = xmlDoc.createElement("OTHER_INST_ORG");

            otherInstOrgElement.setAttribute("CHART", ObjectUtils.toString(routingFormOrganization.getChartOfAccountsCode()));
            otherInstOrgElement.setAttribute("ORG", ObjectUtils.toString(routingFormOrganization.getOrganizationCode()));
            otherInstOrgElement.setAttribute("ORG_NAME", ObjectUtils.toString(routingFormOrganization.getOrganization().getOrganizationName()));

            projectDetailElement.appendChild(otherInstOrgElement);
        }

        for (RoutingFormInstitutionCostShare routingFormInstitutionCostShare : routingFormDocument.getRoutingFormInstitutionCostShares()) {
            Element instCostShareElement = xmlDoc.createElement("INST_COST_SHARE");

            instCostShareElement.setAttribute("CHART", ObjectUtils.toString(routingFormInstitutionCostShare.getChartOfAccountsCode()));
            instCostShareElement.setAttribute("ORG", ObjectUtils.toString(routingFormInstitutionCostShare.getOrganizationCode()));
            instCostShareElement.setAttribute("ACCOUNT", ObjectUtils.toString(routingFormInstitutionCostShare.getAccountNumber()));
            instCostShareElement.setAttribute("AMOUNT", ObjectUtils.toString(routingFormInstitutionCostShare.getRoutingFormCostShareAmount()));

            projectDetailElement.appendChild(instCostShareElement);
        }

        for (RoutingFormOtherCostShare routingFormOtherCostShare : routingFormDocument.getRoutingFormOtherCostShares()) {
            Element otherCostShareElement = xmlDoc.createElement("OTHER_COST_SHARE");

            otherCostShareElement.setAttribute("SOURCE_NAME", ObjectUtils.toString(routingFormOtherCostShare.getRoutingFormCostShareSourceName()));
            otherCostShareElement.setAttribute("AMOUNT", ObjectUtils.toString(routingFormOtherCostShare.getRoutingFormCostShareAmount()));

            projectDetailElement.appendChild(otherCostShareElement);
        }

        if (routingFormDocument.getRoutingFormPersonnel().size() > 0 || routingFormDocument.getRoutingFormOrganizationCreditPercents().size() > 0) {

            for (RoutingFormPersonnel routingFormPerson : routingFormDocument.getRoutingFormPersonnel()) {
                Element percentCreditDescription = xmlDoc.createElement("PERCENT_CREDIT");

                if (routingFormPerson.isPersonToBeNamedIndicator()) {
                    percentCreditDescription.setAttribute("NAME", TO_BE_NAMED);
                }
                else {
                    percentCreditDescription.setAttribute("NAME", ObjectUtils.toString(routingFormPerson.getUser().getPersonName()));
                }
                percentCreditDescription.setAttribute("ROLE", ObjectUtils.toString(routingFormPerson.getPersonRoleText()));
                percentCreditDescription.setAttribute("CHART", ObjectUtils.toString(routingFormPerson.getChartOfAccountsCode()));
                percentCreditDescription.setAttribute("ORG", ObjectUtils.toString(routingFormPerson.getOrganizationCode()));
                percentCreditDescription.setAttribute("CREDIT", ObjectUtils.toString(routingFormPerson.getPersonCreditPercent()));
                percentCreditDescription.setAttribute("FA", ObjectUtils.toString(routingFormPerson.getPersonFinancialAidPercent()));
                projectDetailElement.appendChild(percentCreditDescription);
            }

            for (RoutingFormOrganizationCreditPercent routingFormOrganizationCreditPercent : routingFormDocument.getRoutingFormOrganizationCreditPercents()) {
                Element percentCreditDescription = xmlDoc.createElement("PERCENT_CREDIT");
                percentCreditDescription.setAttribute("NAME", ObjectUtils.toString(routingFormOrganizationCreditPercent.getOrganization().getOrganizationName()));
                percentCreditDescription.setAttribute("ROLE", ObjectUtils.toString(routingFormOrganizationCreditPercent.getOrganizationCreditRoleText()));
                percentCreditDescription.setAttribute("CHART", ObjectUtils.toString(routingFormOrganizationCreditPercent.getChartOfAccountsCode()));
                percentCreditDescription.setAttribute("ORG", ObjectUtils.toString(routingFormOrganizationCreditPercent.getOrganizationCode()));
                percentCreditDescription.setAttribute("CREDIT", ObjectUtils.toString(routingFormOrganizationCreditPercent.getOrganizationCreditPercent()));
                percentCreditDescription.setAttribute("FA", ObjectUtils.toString(routingFormOrganizationCreditPercent.getOrganizationFinancialAidPercent()));
                projectDetailElement.appendChild(percentCreditDescription);
            }
        }

        return projectDetailElement;
    }

    /**
     * Creates APPROVALS node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createApprovalsElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element approvalsElement = xmlDoc.createElement("APPROVALS");

        try {
            ReportCriteriaVO criteria = new ReportCriteriaVO();
            criteria.setRouteHeaderId(routingFormDocument.getDocumentHeader().getWorkflowDocument().getRouteHeaderId());
            WorkflowInfo info = new WorkflowInfo();
            DocumentDetailVO detail = info.routingReport(criteria);

            for (ActionRequestVO actionRequest : detail.getActionRequests()) {
                actionRequestTraversal(xmlDoc, approvalsElement, actionRequest);
            }
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Exception generating routing report: " + e);
        }

        return approvalsElement;
    }

    /**
     * Traversal of ActionRequestVO. This is useful because an ActionRequest may be for a user, role or a workgroup. We want to be
     * able to handle all of them.
     * 
     * @param xmlDoc
     * @param approvalsElement
     * @param actionRequest
     */
    private static void actionRequestTraversal(Document xmlDoc, Element approvalsElement, ActionRequestVO actionRequest) {
        // Note that any actionRequest can have an actionTaken item. But currently our output only shows users
        // and not workgroups / roles. That's why the code drills down to the user level.
        if (EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD.equals(actionRequest.getRecipientTypeCd())) {
            // Base case
            if (actionRequest.getActionTaken() == null) {
                // Action not taken yet, leave date empty
                UserVO user = actionRequest.getUserVO();
                String actionName = ObjectUtils.toString(EdenConstants.ACTION_REQUEST_CD.get(actionRequest.getActionRequested()));
                createApproverElement(xmlDoc, approvalsElement, user, actionRequest.getNodeName(), actionName, "");
            }
            else if (actionRequest.getUserVO().getEmplId().equals(actionRequest.getActionTaken().getUserVO().getEmplId())) {
                // Action was taken, show date
                DateFormat dateFormat = new SimpleDateFormat(KraConstants.LONG_TIMESTAMP_FORMAT);
                ActionTakenVO actionTaken = actionRequest.getActionTaken();
                UserVO user = actionTaken.getUserVO();
                String actionName = ObjectUtils.toString(EdenConstants.ACTION_TAKEN_CD.get(actionTaken.getActionTaken()));
                createApproverElement(xmlDoc, approvalsElement, user, actionRequest.getNodeName(), actionName, dateFormat.format(actionTaken.getActionDate().getTime()));
            }
            // else ignore, it should be an actionRequest for a user whose requests are cleared out. Such as if
            // multiple requests were pending but one disapproved, then all of the users get disapprovals assigned
            // with actionTaken pointing to the person that truly disapproved.
        }
        else {
            // Recursion
            for (ActionRequestVO actionRequestDeep : actionRequest.getChildrenRequests()) {
                actionRequestTraversal(xmlDoc, approvalsElement, actionRequestDeep);
            }
        }
    }

    /**
     * Helper method for actionRequestTraversal to avoid duplicating code for APPROVER node.
     * 
     * @param xmlDoc xmlDoc to be used
     * @param approvalsElement parent node to be used
     * @param workflowUser that took the action
     * @param nodeName will be used for the TITLE field
     * @param actionName will be used for the ACTION field
     * @param actionDate will be used for the ACTION_DATE field
     */
    private static void createApproverElement(Document xmlDoc, Element approvalsElement, UserVO workflowUser, String nodeName, String actionName, String actionDate) {
        Element approverElement = xmlDoc.createElement("APPROVER");

        UniversalUser kualiUser;
        UniversalUserService universalUserService = SpringContext.getBean(UniversalUserService.class);
        ChartUserService chartUserService = SpringContext.getBean(ChartUserService.class);
        try {
            kualiUser = universalUserService.getUniversalUser(workflowUser.getUuId());
        }
        catch (UserNotFoundException e) {
            LOG.error("Lookup for emplId=" + workflowUser.getEmplId() + " failed. Skipping putting person in XML.");
            return;
        }

        approverElement.setAttribute("TITLE", ObjectUtils.toString(nodeName));
        approverElement.setAttribute("CHART", ObjectUtils.toString(chartUserService.getDefaultChartCode(kualiUser)));
        approverElement.setAttribute("ORG", ObjectUtils.toString(chartUserService.getDefaultOrganizationCode(kualiUser)));
        approverElement.setAttribute("ACTION", ObjectUtils.toString(actionName));
        approverElement.setAttribute("ACTION_DATE", ObjectUtils.toString(actionDate));

        Element nameElement = xmlDoc.createElement("NAME");
        nameElement.setAttribute("FIRST", ObjectUtils.toString(workflowUser.getFirstName()));
        nameElement.setAttribute("LAST", ObjectUtils.toString(workflowUser.getLastName()));
        approverElement.appendChild(nameElement);

        approvalsElement.appendChild(approverElement);
    }

    /**
     * Creates KEYWORDS node.
     * 
     * @param routingFormKeywords
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createKeywordsElement(List<RoutingFormKeyword> routingFormKeywords, Document xmlDoc) {
        Element keywordsElement = xmlDoc.createElement("KEYWORDS");

        for (RoutingFormKeyword routingFormKeyword : routingFormKeywords) {
            Element keywordDescription = xmlDoc.createElement("KEYWORD");
            keywordDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormKeyword.getRoutingFormKeywordDescription())));
            keywordsElement.appendChild(keywordDescription);
        }

        return keywordsElement;
    }

    /**
     * Creates COMMENTS node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createCommentsElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element commentsElement = xmlDoc.createElement("COMMENTS");

        DateFormat dateFormat = new SimpleDateFormat(KraConstants.LONG_TIMESTAMP_FORMAT);
        Iterator notes = routingFormDocument.getDocumentHeader().getBoNotes().iterator();

        while (notes.hasNext()) {
            Note note = (Note) notes.next();

            Element commentElement = xmlDoc.createElement("COMMENT");

            Element commentatorDescription = xmlDoc.createElement("COMMENTATOR");
            commentatorDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(note.getAuthorUniversal().getPersonName())));
            commentElement.appendChild(commentatorDescription);

            Element commentTimestampDescription = xmlDoc.createElement("COMMENT_TIMESTAMP");
            commentTimestampDescription.appendChild(xmlDoc.createTextNode(dateFormat.format(note.getNotePostedTimestamp())));
            commentElement.appendChild(commentTimestampDescription);

            Element commentTopicDescription = xmlDoc.createElement("COMMENT_TOPIC");
            commentTopicDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(note.getNoteTopicText())));
            commentElement.appendChild(commentTopicDescription);

            Element commentTextDescription = xmlDoc.createElement("COMMENT_TEXT");
            commentTextDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(note.getNoteText())));
            commentElement.appendChild(commentTextDescription);

            commentsElement.appendChild(commentElement);
        }

        return commentsElement;
    }

    /**
     * Takes a boolean at returns its value as a "Y" or "N". This is how the xslts interpret indicators.
     * 
     * @param bool
     * @return
     */
    private static String formatBoolean(boolean bool) {
        return bool ? "Y" : "N";
    }
}
