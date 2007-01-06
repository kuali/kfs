/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.web.struts.form;

import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.document.ResearchDocument;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganization;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormOtherCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.web.struts.form.ResearchDocumentFormBase;

public class RoutingForm extends ResearchDocumentFormBase {
    
    private boolean auditActivated;
    
    private RoutingFormKeyword newRoutingFormKeyword;
    private RoutingFormPersonnel newRoutingFormPersonnel;
    private RoutingFormOrganizationCreditPercent newRoutingFormOrganizationCreditPercent;
    
    private RoutingFormInstitutionCostShare newRoutingFormInstitutionCostShare;
    private RoutingFormOtherCostShare newRoutingFormOtherCostShare;
    private RoutingFormSubcontractor newRoutingFormSubcontractor;
    private RoutingFormOrganization newRoutingFormOrganization;
    
    private boolean templateAddress;
    private boolean templateAdHocPermissions;
    private boolean templateAdHocApprovers;
    
    public RoutingForm() {
        super();
       
        DataDictionary dataDictionary = SpringServiceLocator.getDataDictionaryService().getDataDictionary();
        DocumentEntry budgetDocumentEntry = dataDictionary.getDocumentEntry(org.kuali.module.kra.routingform.document.RoutingFormDocument.class);
        this.setHeaderNavigationTabs(budgetDocumentEntry.getHeaderTabNavigation());
        
        setDocument(new RoutingFormDocument());
    }
    
    @Override
    public ResearchDocument getResearchDocument(){
        return this.getRoutingFormDocument();
    }
    
    public RoutingFormDocument getRoutingFormDocument(){
        return (RoutingFormDocument)this.getDocument();
    }
    
    public boolean isAuditActivated() {
        return auditActivated;
    }

    public void setAuditActivated(boolean auditActivated) {
        this.auditActivated = auditActivated;
    }

    public RoutingFormKeyword getNewRoutingFormKeyword() {
        return newRoutingFormKeyword;
    }

    public void setNewRoutingFormKeyword(RoutingFormKeyword newRoutingFormKeyword) {
        this.newRoutingFormKeyword = newRoutingFormKeyword;
    }

    public void setNewRoutingFormInstitutionCostShare(RoutingFormInstitutionCostShare newRoutingFormInstitutionCostShare) {
        this.newRoutingFormInstitutionCostShare = newRoutingFormInstitutionCostShare;
    }

    public RoutingFormInstitutionCostShare getNewRoutingFormInstitutionCostShare() {
        return newRoutingFormInstitutionCostShare;
    }

    public RoutingFormOtherCostShare getNewRoutingFormOtherCostShare() {
        return newRoutingFormOtherCostShare;
    }

    public void setNewRoutingFormOtherCostShare(RoutingFormOtherCostShare newRoutingFormOtherCostShare) {
        this.newRoutingFormOtherCostShare = newRoutingFormOtherCostShare;
    }

    public RoutingFormSubcontractor getNewRoutingFormSubcontractor() {
        return newRoutingFormSubcontractor;
    }

    public void setNewRoutingFormSubcontractor(RoutingFormSubcontractor newRoutingFormSubcontractor) {
        this.newRoutingFormSubcontractor = newRoutingFormSubcontractor;
    }

    public RoutingFormPersonnel getNewRoutingFormPersonnel() {
        return newRoutingFormPersonnel;
    }

    public void setNewRoutingFormPersonnel(RoutingFormPersonnel newRoutingFormPersonnel) {
        this.newRoutingFormPersonnel = newRoutingFormPersonnel;
    }

    public RoutingFormOrganizationCreditPercent getNewRoutingFormOrganizationCreditPercent() {
        return newRoutingFormOrganizationCreditPercent;
    }

    public void setNewRoutingFormOrganizationCreditPercent(RoutingFormOrganizationCreditPercent newRoutingFormOrganizationCreditPercent) {
        this.newRoutingFormOrganizationCreditPercent = newRoutingFormOrganizationCreditPercent;
    }

    public RoutingFormOrganization getNewRoutingFormOrganization() {
        return newRoutingFormOrganization;
    }

    public void setNewRoutingFormOrganization(RoutingFormOrganization newRotuingFormOrganization) {
        this.newRoutingFormOrganization = newRotuingFormOrganization;
    }

    public boolean isTemplateAddress() {
        return templateAddress;
    }

    public void setTemplateAddress(boolean templateAddress) {
        this.templateAddress = templateAddress;
    }

    public boolean isTemplateAdHocApprovers() {
        return templateAdHocApprovers;
    }

    public void setTemplateAdHocApprovers(boolean templateAdHocApprovers) {
        this.templateAdHocApprovers = templateAdHocApprovers;
    }

    public boolean isTemplateAdHocPermissions() {
        return templateAdHocPermissions;
    }

    public void setTemplateAdHocPermissions(boolean templateAdHocPermissions) {
        this.templateAdHocPermissions = templateAdHocPermissions;
    }
}
