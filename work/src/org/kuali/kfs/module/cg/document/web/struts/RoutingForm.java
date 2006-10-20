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

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.kra.routingform.bo.ProtocolType;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormProtocol;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;

public class RoutingForm extends KualiDocumentFormBase {
    
    private boolean auditActivated;
    private List<RoutingFormProtocol> newRoutingFormProtocols;
    private List<ProtocolType> protocolTypes;
    private RoutingFormKeyword newRoutingFormKeyword;
    
    public RoutingForm() {
        super();
        newRoutingFormProtocols = new ArrayList<RoutingFormProtocol>();
       
        DataDictionary dataDictionary = SpringServiceLocator.getDataDictionaryService().getDataDictionary();
        DocumentEntry budgetDocumentEntry = dataDictionary.getDocumentEntry(org.kuali.module.kra.routingform.document.RoutingFormDocument.class);
        this.setHeaderNavigationTabs(budgetDocumentEntry.getHeaderTabNavigation());
        
        setDocument(new RoutingFormDocument());
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

    public List<RoutingFormProtocol> getNewRoutingFormProtocols() {
        return newRoutingFormProtocols;
    }

    public void setNewRoutingFormProtocols(List<RoutingFormProtocol> newRoutingFormProtocols) {
        this.newRoutingFormProtocols = newRoutingFormProtocols;
    }

    public List<ProtocolType> getProtocolTypes() {
        return protocolTypes;
    }
    public RoutingFormKeyword getNewRoutingFormKeyword() {
        return newRoutingFormKeyword;
    }

    public void setProtocolTypes(List<ProtocolType> protocolTypes) {
        this.protocolTypes = protocolTypes;
    }
    
    public RoutingFormProtocol getNewRoutingFormProtocol(int index) {
        while (getNewRoutingFormProtocols().size() <= index) {
            getNewRoutingFormProtocols().add(new RoutingFormProtocol());
        }
        return (RoutingFormProtocol) getNewRoutingFormProtocols().get(index);
    }

    public void setNewRoutingFormKeyword(RoutingFormKeyword newRoutingFormKeyword) {
        this.newRoutingFormKeyword = newRoutingFormKeyword;
    }
}
