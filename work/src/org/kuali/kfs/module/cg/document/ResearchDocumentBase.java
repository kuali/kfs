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
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.document.Copyable;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.module.kra.bo.BudgetAdHocOrg;
import org.kuali.module.kra.bo.BudgetAdHocPermission;
import org.kuali.module.kra.bo.BudgetAdHocWorkgroup;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Research Administration Document Base
 */
public abstract class ResearchDocumentBase extends TransactionalDocumentBase implements ResearchDocument, Copyable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResearchDocumentBase.class);
    
    private List<BudgetAdHocPermission> adHocPermissions;
    private List<BudgetAdHocOrg> adHocOrgs;
    private List<BudgetAdHocWorkgroup> adHocWorkgroups;

    /**
     * Sets up the collection instances and common document attributes.
     */
    public ResearchDocumentBase() {
        super();
        adHocPermissions = new ArrayList<BudgetAdHocPermission>();
        adHocOrgs = new ArrayList<BudgetAdHocOrg>();
        adHocWorkgroups = new ArrayList<BudgetAdHocWorkgroup>();
    }
    
    /**
     * Overridden to note clear and add notes.
     * @see org.kuali.core.document.DocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException, IllegalStateException {
       super.setNewDocumentHeader();
    }
    
    /**
     * Gets the adHocPermissions attribute.
     * 
     * @return Returns the adHocPermissions.
     */
    public List<BudgetAdHocPermission> getAdHocPermissions() {
        return adHocPermissions;
    }
    
    /**
     * Sets the adHocPermissions attribute value.
     * 
     * @param adHocPermissions The adHocPermissions to set.
     */
    public void setAdHocPermissions(List<BudgetAdHocPermission> adHocPermissions) {
        this.adHocPermissions = adHocPermissions;
    }
    
    /**
     * Gets the BudgetAdHocPermission item at given index.
     * 
     * @param index
     * @return BudgetAdHocPermission
     */
    public BudgetAdHocPermission getBudgetAdHocPermissionItem(int index) {
        while (this.getAdHocPermissions().size() <= index) {
            this.getAdHocPermissions().add(new BudgetAdHocPermission());
        }
        return this.getAdHocPermissions().get(index);
    }
        
    /**
     * Gets the adHocOrgs attribute. 
     * @return Returns the adHocOrgs.
     */
    public List<BudgetAdHocOrg> getAdHocOrgs() {
        return adHocOrgs;
    }

    /**
     * Sets the adHocOrgs attribute value.
     * @param adHocOrgs The adHocOrgs to set.
     */
    public void setAdHocOrgs(List<BudgetAdHocOrg> adHocOrgs) {
        this.adHocOrgs = adHocOrgs;
    }
    
    /**
     * Gets the BudgetAdHocOrg item at given index.
     * 
     * @param index
     * @return BudgetAdHocOrg
     */
    public BudgetAdHocOrg getBudgetAdHocOrgItem(int index) {
        while (this.getAdHocOrgs().size() <= index) {
            this.getAdHocOrgs().add(new BudgetAdHocOrg());
        }
        return this.getAdHocOrgs().get(index);
    }

    public List<BudgetAdHocWorkgroup> getAdHocWorkgroups() {
        return adHocWorkgroups;
    }

    public void setAdHocWorkgroups(List<BudgetAdHocWorkgroup> adHocWorkgroups) {
        this.adHocWorkgroups = adHocWorkgroups;
    }
    
    /**
     * Gets the BudgetAdHocWorkgroup item at given index.
     * 
     * @param index
     * @return BudgetAdHocWorkgroup
     */
    public BudgetAdHocWorkgroup getBudgetAdHocWorkgroupItem(int index) {
        while (this.getAdHocWorkgroups().size() <= index) {
            this.getAdHocWorkgroups().add(new BudgetAdHocWorkgroup());
        }
        return this.getAdHocWorkgroups().get(index);
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
}