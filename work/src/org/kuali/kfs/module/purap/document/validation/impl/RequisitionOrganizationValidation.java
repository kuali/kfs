/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
