/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.validation.impl;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;


public class RequisitionOrganizationValidation extends GenericValidation {
    private OrganizationService organizationService;
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        boolean active;
        RequisitionDocument purDocument = (RequisitionDocument) event.getDocument();
        MessageMap errorMap = GlobalVariables.getMessageMap();
        errorMap.clearErrorPath();
        Organization org = organizationService.getByPrimaryId(purDocument.getChartOfAccountsCode(), purDocument.getOrganizationCode());
        if(org!=null){
            if (!org.isActive()) {            
                errorMap.putError(PurapConstants.PURAP_REQS_ORG_CD, PurapKeyConstants.ERROR_INACTIVE_ORG);
                valid = false;
            }
        }
        return valid;
    }

    public void setOrganizationService(OrganizationService orgSer) {
        this.organizationService = orgSer;
    } 

}
