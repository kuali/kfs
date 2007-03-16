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
package org.kuali.module.kra.web.struts.form;

import org.kuali.core.datadictionary.HeaderNavigation;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.kra.bo.AdhocOrg;
import org.kuali.module.kra.bo.AdhocPerson;
import org.kuali.module.kra.document.ResearchDocument;

public abstract class ResearchDocumentFormBase extends KualiDocumentFormBase {
    
    private AdhocPerson newAdHocPerson;
    private AdhocOrg newAdHocOrg;
    private String newAdHocWorkgroupPermissionCode;

    public ResearchDocumentFormBase() {
        newAdHocPerson = new AdhocPerson();
        newAdHocOrg = new AdhocOrg();
    }
    
    abstract public ResearchDocument getResearchDocument();
    
    public void disableHeaderNavigation(String headerTabNavigateTo) {
        for (int i = 0; i < this.getHeaderNavigationTabs().length; i++) {
            HeaderNavigation currentNav = (HeaderNavigation) this.getHeaderNavigationTabs()[i];
            if (headerTabNavigateTo.equals(currentNav.getHeaderTabNavigateTo())) {
                currentNav.setDisabled(true);
                return;
            }
        }
    }

    public void enableHeaderNavigation(String headerTabNavigateTo) {
        for (int i = 0; i < this.getHeaderNavigationTabs().length; i++) {
            HeaderNavigation currentNav = (HeaderNavigation) this.getHeaderNavigationTabs()[i];
            if (headerTabNavigateTo.equals(currentNav.getHeaderTabNavigateTo())) {
                currentNav.setDisabled(false);
                return;
            }
        }
    }
    
    /**
     * Gets the newAdHocPerson attribute.
     * 
     * @return Returns the newAdHocPerson.
     */
    public AdhocPerson getNewAdHocPerson() {
        return newAdHocPerson;
    }

    /**
     * Sets the newAdHocPerson attribute value.
     * 
     * @param newAdHocPerson The newAdHocPerson to set.
     */
    public void setNewAdHocPerson(AdhocPerson newAdHocPerson) {
        this.newAdHocPerson = newAdHocPerson;
    }
    
    /**
     * Gets the newAdHocOrg attribute. 
     * @return Returns the newAdHocOrg.
     */
    public AdhocOrg getNewAdHocOrg() {
        return newAdHocOrg;
    }

    /**
     * Sets the newAdHocOrg attribute value.
     * @param newAdHocOrg The newAdHocOrg to set.
     */
    public void setNewAdHocOrg(AdhocOrg newAdHocOrg) {
        this.newAdHocOrg = newAdHocOrg;
    }

    public String getNewAdHocWorkgroupPermissionCode() {
        return newAdHocWorkgroupPermissionCode;
    }

    public void setNewAdHocWorkgroupPermissionCode(String newAdHocWorkgroupPermissionCode) {
        this.newAdHocWorkgroupPermissionCode = newAdHocWorkgroupPermissionCode;
    }
}
