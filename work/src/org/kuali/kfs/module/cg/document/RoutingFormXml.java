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
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.module.kra.routingform.bo.RoutingFormAgency;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class creates an XML representation of a RoutingForm's data.
 * 
 * 
 */
public class RoutingFormXml {
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
        agencyDueDateElement.setAttribute("DUE_DATE_TYPE", routingFormAgency.getRoutingFormDueDateTypeCode());
        agencyDueDateElement.setAttribute("DUE_DATE", ObjectUtils.toString(routingFormAgency.getRoutingFormDueDate()));
        // following field is dropped in KRA but per request preserved for Indiana University ERA implementation.
        agencyDueDateElement.setAttribute("DUE_TIME", routingFormAgency.getRoutingFormDueTime());
        agencyElement.appendChild(agencyDueDateElement);
        
        Element agencyDeliveryElement = xmlDoc.createElement("AGENCY_DELIVERY");
        // TODO following field is dropped but used by stylesheet
        agencyDeliveryElement.setAttribute("ADDITIONAL_DELIVERY_INSTRUCTIONS_IND", "TODO");
        agencyDeliveryElement.setAttribute("COPIES", routingFormAgency.getRoutingFormRequiredCopyText());
        
        Element agencyDeliveryInstructionsElement = xmlDoc.createElement("DELIVERY_INSTRUCTIONS");
        agencyDeliveryInstructionsElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormAgency.getAgencyShippingInstructionsDescription())));
        agencyDeliveryElement.appendChild(agencyDeliveryInstructionsElement);
        
        Element agencyAdditionalDeliveryInstructionsElement = xmlDoc.createElement("ADDITIONAL_DELIVERY_INSTRUCTIONS");
        agencyAdditionalDeliveryInstructionsElement.setAttribute("DISK_INCLUDED_IND", formatBoolean(routingFormAgency.getAgencyDiskAccompanyIndicator()));
        agencyAdditionalDeliveryInstructionsElement.setAttribute("ELECTRONIC_SUBMISSIONS_IND", formatBoolean(routingFormAgency.getAgencyElectronicSubmissionIndicator()));
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

        // TODO do this section & consider howto handle different roles?
        
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

        DateFormat dateFormatterKra = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat dateFormatterLong = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        
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
     * Creates TYPE node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createTypeElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element typeElement = xmlDoc.createElement("TYPE");

        // TODO: dynamic fields?
        
        Element priorGrantDescription = xmlDoc.createElement("PRIOR_GRANT");
        priorGrantDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getRoutingFormPriorGrantNumber())));
        typeElement.appendChild(priorGrantDescription);
        
        Element currentGrantDescription = xmlDoc.createElement("CURRENT_GRANT");
        currentGrantDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getGrantNumber())));
        typeElement.appendChild(currentGrantDescription);
        
        Element institutionAccountDescription = xmlDoc.createElement("INSTITUTION_ACCOUNT");
        institutionAccountDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(routingFormDocument.getInstitutionAccountNumber())));
        typeElement.appendChild(institutionAccountDescription);
        
        Element currentProposalDescription = xmlDoc.createElement("CURRENT_PROPOSAL");
        currentProposalDescription.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(ObjectUtils.toString(routingFormDocument.getContractGrantProposal().getProposalNumber()))));
        typeElement.appendChild(currentProposalDescription);
        
        return typeElement;
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

        // TODO: dynamic fields?
        
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

        // TODO where do I get the route log from?  (ask Geoff? Terry?)
        
        return approvalsElement;
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
     * Creates COMMENTS node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createCommentsElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element commentsElement = xmlDoc.createElement("COMMENTS");

        // TODO get notes from document ...
        // routingFormDocument.getDocumentHeader().getBoNotes();
        
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
