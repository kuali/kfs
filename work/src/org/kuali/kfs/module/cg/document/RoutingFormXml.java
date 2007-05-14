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
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.service.ChartUserService;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.routingform.bo.RoutingFormAgency;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganization;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.bo.RoutingFormProjectType;
import org.kuali.module.kra.routingform.bo.RoutingFormQuestion;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.bo.RoutingFormSubmissionType;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.service.RoutingFormMainPageService;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.ActionTakenVO;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class creates an XML representation of a RoutingForm's data.
 * 
 * 
 */
public class RoutingFormXml {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormXml.class);
    
    // TODO do these already exist somewhere?
    private static final String SHORT_TIMESTAMP_FORMAT = "MM/dd/yyyy";
    private static final String LONG_TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";
    
    /**
     * Driving method for this class. Functions as a hub calling helper methods.
     * 
     * @param routingFormroutingFormDocumentDoc data representation of a routingForm
     * @param xmlDoc target xml representation for the routingForm. This field will be side effected.
     * @param baseUrl ensures that stylesheet may be path idependent
     * @throws Exception
     */
    public static void makeXml(RoutingFormDocument routingFormDocument, Document xmlDoc, String baseUrl) throws Exception {
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
        routingFormElement.setAttribute("BASE_URL", baseUrl);

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
        routingFormElement.appendChild(createSubmissionTypesElement(routingFormDocument.getRoutingFormSubmissionTypes(), routingFormDocument.getPreviousFederalIdentifier(), xmlDoc));
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

        DateFormat dateFormatterKra = new SimpleDateFormat(SHORT_TIMESTAMP_FORMAT);
        
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
        agencyDueDateElement.setAttribute("DUE_DATE", routingFormAgency.getRoutingFormDueDate() == null ? "" : dateFormatterKra.format(routingFormAgency.getRoutingFormDueDate()));
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
        
        RoutingFormMainPageService routingFormMainPageService = SpringServiceLocator.getRoutingFormMainPageService();
        List<RoutingFormPersonnel> routingFormPersonnel = routingFormDocument.getRoutingFormPersonnel();
        RoutingFormPersonnel projectDirector = routingFormMainPageService.getProjectDirector(routingFormPersonnel);

        principlesElement.setAttribute("CO-PD_IND", formatBoolean(routingFormMainPageService.checkCoPdExistance(routingFormPersonnel)));
        
        Element projectDirectorElement = xmlDoc.createElement("PROJECT_DIRECTOR");
        if (projectDirector != null) {
            projectDirectorElement.setAttribute("FIRST_NAME", ObjectUtils.toString(projectDirector.getUser().getPersonFirstName()));
            projectDirectorElement.setAttribute("LAST_NAME", ObjectUtils.toString(projectDirector.getUser().getPersonLastName()));
            projectDirectorElement.setAttribute("PERCENT_CREDIT", ObjectUtils.toString(projectDirector.getPersonCreditPercent()));
            
            Element homeOrgElement = xmlDoc.createElement("HOME_ORG");
            homeOrgElement.setAttribute("HOME_CHART", ObjectUtils.toString(projectDirector.getUser().getCampusCode()));
            homeOrgElement.setAttribute("HOME_ORG", ObjectUtils.toString(projectDirector.getUser().getPrimaryDepartmentCode()));
            projectDirectorElement.appendChild(homeOrgElement);
            
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
        
        // TODO contact person
        Element contactPersonElement = xmlDoc.createElement("CONTACT_PERSON");
        contactPersonElement.setAttribute("FIRST_NAME", "TODO");
        contactPersonElement.setAttribute("LAST_NAME", "TODO");
        contactPersonElement.setAttribute("EMAIL", "TODO");
        contactPersonElement.setAttribute("PHONE_NUMBER", "TODO");
        contactPersonElement.setAttribute("FAX_NUMBER", "TODO");
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
        Element purposeElement = xmlDoc.createElement("PURPOSE");

        // TODO Research Type dropdown.
        
        // TODO Purpose should be dynamic, not static as it is right now below.
        
        purposeElement.setAttribute("PURPOSE", routingFormDocument.getRoutingFormPurposeCode());
        
        Element purposeDescriptionElement = xmlDoc.createElement("PURPOSE_DESCRIPTION");
        purposeDescriptionElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getRoutingFormOtherPurposeDescription())));
        purposeElement.appendChild(purposeDescriptionElement);
        
        return purposeElement;
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

        DateFormat dateFormatterKra = new SimpleDateFormat(SHORT_TIMESTAMP_FORMAT);
        
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
            startDateDescription.appendChild(xmlDoc.createTextNode(dateFormatterKra.format(routingFormBudget.getRoutingFormBudgetStartDate())));
        }
        amountsDatesElement.appendChild(startDateDescription);
        
        Element endDateDescription = xmlDoc.createElement("STOP_DATE");
        if (routingFormBudget.getRoutingFormBudgetEndDate() != null) {
            endDateDescription.appendChild(xmlDoc.createTextNode(dateFormatterKra.format(routingFormBudget.getRoutingFormBudgetEndDate())));
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

        for(RoutingFormProjectType routingFormProjectType : routingFormDocument.getRoutingFormProjectTypes()) {
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
        Element researchRiskElement = xmlDoc.createElement("RESEARCH_RISK");

        // TODO: dynamic fields?
        // routingFormDocument.getRoutingFormResearchRisks().get(0).getResearchRiskDescription();
        
        return researchRiskElement;
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
        
        if (routingFormDocument.getRoutingFormPersonnel().size() > 0 || routingFormDocument.getRoutingFormOrganizationCreditPercents().size() > 0) {
            
            for(RoutingFormPersonnel routingFormPerson : routingFormDocument.getRoutingFormPersonnel()) {
                Element percentCreditDescription = xmlDoc.createElement("PERCENT_CREDIT");
                percentCreditDescription.setAttribute("NAME", routingFormPerson.getUser().getPersonName());
                percentCreditDescription.setAttribute("ROLE", routingFormPerson.getPersonRoleText());
                percentCreditDescription.setAttribute("CHART", routingFormPerson.getChartOfAccountsCode());
                percentCreditDescription.setAttribute("ORG", routingFormPerson.getOrganizationCode());
                percentCreditDescription.setAttribute("CREDIT", ObjectUtils.toString(routingFormPerson.getPersonCreditPercent()));
                percentCreditDescription.setAttribute("FA", ObjectUtils.toString(routingFormPerson.getPersonFinancialAidPercent()));
                projectDetailElement.appendChild(percentCreditDescription);
            }
    
            for(RoutingFormOrganizationCreditPercent routingFormOrganizationCreditPercent : routingFormDocument.getRoutingFormOrganizationCreditPercents()) {
                Element percentCreditDescription = xmlDoc.createElement("PERCENT_CREDIT");
                percentCreditDescription.setAttribute("NAME", routingFormOrganizationCreditPercent.getOrganization().getOrganizationName());
                percentCreditDescription.setAttribute("ROLE", routingFormOrganizationCreditPercent.getOrganizationCreditRoleText());
                percentCreditDescription.setAttribute("CHART", routingFormOrganizationCreditPercent.getChartOfAccountsCode());
                percentCreditDescription.setAttribute("ORG", routingFormOrganizationCreditPercent.getOrganizationCode());
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
        
        ReportCriteriaVO criteria = new ReportCriteriaVO();
        criteria.setDocumentTypeName(KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE);
        criteria.setNodeNames(new String[] {KraConstants.PROJECT_DIRECTOR_REVIEW_NODE_NAME, KraConstants.ADHOC_REVIEW_NODE_NAME, KraConstants.ORG_REVIEW_NODE_NAME});
        criteria.setRuleTemplateNames(new String[] {KraConstants.PROJECT_DIRECTOR_TEMPLATE_NAME, KraConstants.ADHOC_REVIEW_TEMPLATE_NAME, KraConstants.ORG_REVIEW_TEMPLATE_NAME});
        criteria.setXmlContent(routingFormDocument.generateDocumentContent());
        WorkflowInfo info = new WorkflowInfo();
        try {
            DateFormat dateFormat = new SimpleDateFormat(LONG_TIMESTAMP_FORMAT);
            DocumentDetailVO detail = info.routingReport(criteria);
            ActionTakenVO[] actionTakenVO = detail.getActionsTaken();
            ActionRequestVO[] actionRequestVO = detail.getActionRequests();
            
            for(int i = 0 ; i < actionTakenVO.length; i++) {
                ActionTakenVO actionTaken = actionTakenVO[i];
                UserVO user = actionTaken.getUserVO();
                
                // TODO PD and ADHOC user object is always null
                if (user != null) {
                    createApproverElement(xmlDoc, approvalsElement, user, "TODO", actionTaken.getActionTaken(), dateFormat.format(actionTaken.getActionDate()));
                }
            }
            
            for(int i = 0 ; i < actionRequestVO.length; i++) {
                ActionRequestVO actionRequest = actionRequestVO[i];
                UserVO user = actionRequest.getUserVO();
                
                // actionRequestVO[i].getChildrenRequests() retrieves delegates but we don't display them

                // TODO PD and ADHOC user object is always null
                if (user != null) {
                    createApproverElement(xmlDoc, approvalsElement, user, actionRequest.getNodeName(), actionRequest.getActionRequested(), "");
                }
            }
        } catch (WorkflowException e) {
            throw new RuntimeException("Exception generating routing report: " + e);
        }
        
        return approvalsElement;
    }

    /**
     * Helper method for createApprovalsElement to avoid duplicating code for actions taken and action requests.
     * @param xmlDoc xmlDoc to be used
     * @param approvalsElement parent node to be used
     * @param workflowUser that took the action
     * @param nodeName will be used for the TITLE field
     * @param action will be used for the ACTION field
     * @param actionDate will be used for the ACTION_DATE field
     */
    private static void createApproverElement(Document xmlDoc, Element approvalsElement, UserVO workflowUser, String nodeName, String action, String actionDate) {
        Element approverElement = xmlDoc.createElement("APPROVER");
        
        UniversalUser kualiUser;
        UniversalUserService universalUserService = SpringServiceLocator.getUniversalUserService();
        ChartUserService chartUserService = SpringServiceLocator.getChartUserService();
        try {
            kualiUser =  universalUserService.getUniversalUser(workflowUser.getUuId());
        } catch (UserNotFoundException e) {
            LOG.error("Lookup for emplId=" + workflowUser.getEmplId() + " failed. Skipping putting person in XML.");
            return;
        }
        
        approverElement.setAttribute("TITLE", nodeName);
        approverElement.setAttribute("CHART", chartUserService.getDefaultChartOfAccountsCode((ChartUser) kualiUser.getModuleUser(ChartUser.MODULE_ID)));
        approverElement.setAttribute("ORG", chartUserService.getDefaultOrganizationCode((ChartUser) kualiUser.getModuleUser(ChartUser.MODULE_ID)));
        approverElement.setAttribute("ACTION", action);
        approverElement.setAttribute("ACTION_DATE", actionDate);
        
        Element nameElement = xmlDoc.createElement("NAME");
        nameElement.setAttribute("FIRST", workflowUser.getFirstName());
        nameElement.setAttribute("LAST", workflowUser.getLastName());
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

        for(RoutingFormKeyword routingFormKeyword : routingFormKeywords) {
            Element keywordDescription = xmlDoc.createElement("KEYWORD");
            keywordDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormKeyword.getRoutingFormKeywordDescription())));
            keywordsElement.appendChild(keywordDescription);            
        }
        
        return keywordsElement;
    }
    
    /**
     * Creates SUBMISSION_TYPES node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createSubmissionTypesElement(List<RoutingFormSubmissionType> routingFormSubmissionTypes, String previousFederalIdentifier, Document xmlDoc) {
        Element submissionTypesElement = xmlDoc.createElement("SUBMISSION_TYPES");

        // TODO New section on KRA, create?
        
        return submissionTypesElement;
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
        
        DateFormat dateFormat = new SimpleDateFormat(LONG_TIMESTAMP_FORMAT);
        Iterator notes = routingFormDocument.getDocumentHeader().getBoNotes().iterator();
        
        while(notes.hasNext()) {
            Note note = (Note) notes.next();
            
            Element commentElement = xmlDoc.createElement("COMMENT");
            
            Element commentatorDescription = xmlDoc.createElement("COMMENTATOR");
            commentatorDescription.appendChild(xmlDoc.createTextNode(note.getAuthorUniversal().getPersonName()));
            commentElement.appendChild(commentatorDescription);
            
            Element commentTimestampDescription = xmlDoc.createElement("COMMENT_TIMESTAMP");
            commentTimestampDescription.appendChild(xmlDoc.createTextNode(dateFormat.format(note.getNotePostedTimestamp())));
            commentElement.appendChild(commentTimestampDescription);
            
            Element commentTopicDescription = xmlDoc.createElement("COMMENT_TOPIC");
            commentTopicDescription.appendChild(xmlDoc.createTextNode(note.getNoteTopicText()));
            commentElement.appendChild(commentTopicDescription);
            
            Element commentTextDescription = xmlDoc.createElement("COMMENT_TEXT");
            commentTextDescription.appendChild(xmlDoc.createTextNode(note.getNoteText()));
            commentElement.appendChild(commentTextDescription);
            
            commentsElement.appendChild(commentElement);
        }
        
        return commentsElement;
    }
    
    /**
     * Takes a boolean at returns its value as a "Y" or "N". This is how the xslts interpret indicators.
     * @param bool
     * @return
     */
    private static String formatBoolean(boolean bool) {
        return bool ? "Y" : "N";
    }
}
