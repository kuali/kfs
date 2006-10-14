/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/document/web/struts/RoutingForm.java,v $
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
import org.kuali.core.datadictionary.HeaderNavigation;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.kra.routingform.bo.RoutingFormProtocol;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;

public class RoutingForm extends KualiDocumentFormBase {
    
    private boolean auditActivated;
    private RoutingFormProtocol newRoutingFormProtocol;
    
    public RoutingForm() {
        super();
        
        DataDictionary dataDictionary = SpringServiceLocator.getDataDictionaryService().getDataDictionary();
        DocumentEntry budgetDocumentEntry = dataDictionary.getDocumentEntry(org.kuali.module.kra.routingform.document.RoutingFormDocument.class);
        this.setHeaderNavigationTabs(budgetDocumentEntry.getHeaderTabNavigation());
        
        setDocument(new RoutingFormDocument());
    }

    public boolean isAuditActivated() {
        return auditActivated;
    }

    public void setAuditActivated(boolean auditActivated) {
        this.auditActivated = auditActivated;
    }

    public RoutingFormProtocol getNewRoutingFormProtocol() {
        return newRoutingFormProtocol;
    }

    public void setNewRoutingFormProtocol(RoutingFormProtocol newRoutingFormProtocol) {
        this.newRoutingFormProtocol = newRoutingFormProtocol;
    }
}
