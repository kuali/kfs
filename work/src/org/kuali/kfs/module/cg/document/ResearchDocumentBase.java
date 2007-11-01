/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.kra.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.kra.bo.AdhocOrg;
import org.kuali.module.kra.bo.AdhocPerson;
import org.kuali.module.kra.bo.AdhocWorkgroup;
import org.kuali.module.kra.service.ResearchDocumentPermissionsService;
import org.kuali.module.kra.service.ResearchDocumentService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Research Administration Document Base
 */
public abstract class ResearchDocumentBase extends TransactionalDocumentBase implements ResearchDocument, Copyable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResearchDocumentBase.class);

    private List<AdhocPerson> adhocPersons;
    private List<AdhocOrg> adhocOrgs;
    private List<AdhocWorkgroup> adhocWorkgroups;

    /**
     * Sets up the collection instances and common document attributes.
     */
    public ResearchDocumentBase() {
        super();
        adhocPersons = new ArrayList<AdhocPerson>();
        adhocOrgs = new ArrayList<AdhocOrg>();
        adhocWorkgroups = new ArrayList<AdhocWorkgroup>();
    }

    /**
     * Overridden to note clear and add notes.
     * 
     * @see org.kuali.core.document.DocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException, IllegalStateException {
        super.setNewDocumentHeader();
    }

    /**
     * Gets the adhocPersons attribute.
     * 
     * @return Returns the adhocPersons.
     */
    public List<AdhocPerson> getAdhocPersons() {
        return adhocPersons;
    }

    /**
     * Sets the adhocPersons attribute value.
     * 
     * @param adhocPersons The adhocPersons to set.
     */
    public void setAdhocPersons(List<AdhocPerson> adhocPersons) {
        this.adhocPersons = adhocPersons;
    }

    /**
     * Gets the AdhocPerson item at given index.
     * 
     * @param index
     * @return AdhocPerson
     */
    public AdhocPerson getAdhocPersonItem(int index) {
        while (this.getAdhocPersons().size() <= index) {
            this.getAdhocPersons().add(new AdhocPerson());
        }
        return this.getAdhocPersons().get(index);
    }

    /**
     * Gets the adhocOrgs attribute.
     * 
     * @return Returns the adhocOrgs.
     */
    public List<AdhocOrg> getAdhocOrgs() {
        return adhocOrgs;
    }

    /**
     * Sets the adhocOrgs attribute value.
     * 
     * @param adhocOrgs The adhocOrgs to set.
     */
    public void setAdhocOrgs(List<AdhocOrg> adhocOrgs) {
        this.adhocOrgs = adhocOrgs;
    }

    /**
     * Gets the BudgetAdhocOrg item at given index.
     * 
     * @param index
     * @return BudgetAdhocOrg
     */
    public AdhocOrg getAdhocOrgItem(int index) {
        while (this.getAdhocOrgs().size() <= index) {
            this.getAdhocOrgs().add(new AdhocOrg());
        }
        return this.getAdhocOrgs().get(index);
    }

    public List<AdhocWorkgroup> getAdhocWorkgroups() {
        return adhocWorkgroups;
    }

    public void setAdhocWorkgroups(List<AdhocWorkgroup> adhocWorkgroups) {
        this.adhocWorkgroups = adhocWorkgroups;
    }

    /**
     * Gets the AdhocWorkgroup item at given index.
     * 
     * @param index
     * @return AdhocWorkgroup
     */
    public AdhocWorkgroup getAdhocWorkgroupItem(int index) {
        while (this.getAdhocWorkgroups().size() <= index) {
            this.getAdhocWorkgroups().add(new AdhocWorkgroup());
        }
        return this.getAdhocWorkgroups().get(index);
    }

    /**
     * Clears all adhocs of a given type.
     * 
     * @param adhocTypeCode
     */
    public void clearAdhocType(String adhocTypeCode) {
        for (Iterator iter = this.adhocPersons.iterator(); iter.hasNext();) {
            AdhocPerson person = (AdhocPerson) iter.next();
            if (adhocTypeCode.equals(person.getAdhocTypeCode())) {
                iter.remove();
            }
        }
        for (Iterator iter = this.adhocOrgs.iterator(); iter.hasNext();) {
            AdhocOrg org = (AdhocOrg) iter.next();
            if (adhocTypeCode.equals(org.getAdhocTypeCode())) {
                iter.remove();
            }
        }
        for (Iterator iter = this.adhocWorkgroups.iterator(); iter.hasNext();) {
            AdhocWorkgroup workgroup = (AdhocWorkgroup) iter.next();
            if (adhocTypeCode.equals(workgroup.getAdhocTypeCode())) {
                iter.remove();
            }
        }
    }

    /**
     * Convert and return this document's adhoc persons as KFS-style AdHoc persons
     * 
     * @return List<AdHocRoutePerson>
     */
    public List<AdHocRoutePerson> convertKraAdhocsToAdHocRoutePersons() {
        List<AdHocRoutePerson> adHocRoutePersons = new ArrayList<AdHocRoutePerson>();
        for (AdhocPerson kraAdhocPerson : this.adhocPersons) {
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(kraAdhocPerson);
            AdHocRoutePerson adHocRoutePerson = new AdHocRoutePerson();
            adHocRoutePerson.setId(kraAdhocPerson.getUser().getPersonUserIdentifier());
            adHocRoutePerson.setActionRequested(kraAdhocPerson.getActionRequested());
            adHocRoutePersons.add(adHocRoutePerson);
        }
        return adHocRoutePersons;
    }

    /**
     * Convert and return this document's adhoc workgroups as KFS-style AdHoc workgroups
     * 
     * @return List<AdHocRoutePerson>
     */
    public List<AdHocRouteWorkgroup> convertKraAdhocsToAdHocRouteWorkgroups() {
        List<AdHocRouteWorkgroup> adHocRouteWorkgroups = new ArrayList<AdHocRouteWorkgroup>();
        for (AdhocWorkgroup kraAdhocWorkgroup : this.adhocWorkgroups) {
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(kraAdhocWorkgroup);
            AdHocRouteWorkgroup adHocRouteWorkgroup = new AdHocRouteWorkgroup();
            adHocRouteWorkgroup.setId(kraAdhocWorkgroup.getWorkgroupName());
            adHocRouteWorkgroup.setActionRequested(kraAdhocWorkgroup.getActionRequested());
            adHocRouteWorkgroups.add(adHocRouteWorkgroup);
        }
        return adHocRouteWorkgroups;
    }

    /**
     * Build the xml to use when generating the workflow org routing report.
     * 
     * @param List<AdhocOrg> orgs
     * @param boolean encloseContent - whether the generated xml should be enclosed within a <documentContent> tag
     * @return String
     */
    public String buildAdhocOrgReportXml(String permissionTypeCode, boolean encloseContent) {
        StringBuffer xml = new StringBuffer();
        if (encloseContent) {
            xml.append("<documentContent>");
        }
        List<AdhocOrg> orgs = SpringContext.getBean(ResearchDocumentPermissionsService.class).getAdHocOrgs(this.getDocumentNumber(), permissionTypeCode);
        for (AdhocOrg org : orgs) {
            xml.append("<chartOrg><chartOfAccountsCode>");
            xml.append(org.getFiscalCampusCode());
            xml.append("</chartOfAccountsCode><organizationCode>");
            xml.append(org.getPrimaryDepartmentCode());
            xml.append("</organizationCode></chartOrg>");
        }
        if (encloseContent) {
            xml.append("</documentContent>");
        }
        return xml.toString();
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("versionNumber", getVersionNumber());
        m.put("comp", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isCompletionRequested()));
        m.put("app", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isApprovalRequested()));
        m.put("ack", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isAcknowledgeRequested()));
        m.put("fyi", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isFYIRequested()));

        return m;
    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);
        try {
            SpringContext.getBean(ResearchDocumentService.class).prepareResearchDocumentForSave(this);
        }
        catch (Exception e) {
            throw new RuntimeException("Error preparing ResearchDocument for save", e);
        }

    }
}