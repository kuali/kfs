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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

//public class CustomerCreditMemoDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
public class CustomerCreditMemoDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    @Override
    @SuppressWarnings("unchecked")
    public Map getEditMode(Document document, Person user) {
        
        Map<String,String> editModeMap = super.getEditMode(document, user);
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;
        
        if (StringUtils.equals(customerCreditMemoDocument.getStatusCode(),ArConstants.CustomerCreditMemoStatuses.INITIATE)) {
            editModeMap.put(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB,"TRUE");
        }
        else {
            editModeMap.put(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB,"FALSE");
        }
        
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        if( ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals( receivableOffsetOption ) ){
            editModeMap.put(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.SHOW_RECEIVABLE_FAU, "TRUE");
        }
        
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (ObjectUtils.isNotNull(workflowDocument) && (workflowDocument.stateIsApproved() || workflowDocument.stateIsProcessed() || workflowDocument.stateIsFinal())) {
            editModeMap.put(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_PRINT_BUTTON, "TRUE");
        }
        else {
            editModeMap.put(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_PRINT_BUTTON, "FALSE");
        }
        
        return editModeMap;
    }
    
    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActionFlags(Document, Person)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;
        if (StringUtils.equals(customerCreditMemoDocument.getStatusCode(), ArConstants.CustomerCreditMemoStatuses.INITIATE)) {
            flags.setCanSave(false);
            flags.setCanClose(true);
            flags.setCanCancel(false);
        }
        return flags;
    }
    
    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public void canInitiate(String documentTypeName, Person user) throws DocumentTypeAuthorizationException {
        super.canInitiate(documentTypeName, user);
        // to initiate, the user must have the organization options set up.
        ChartOrgHolder chartUser = org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.KNSAuthorizationService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);
        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chartUser.getChartOfAccountsCode());
        criteria.put("organizationCode", chartUser.getOrganizationCode());
        OrganizationOptions organizationOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        //if organization doesn't exist
        if (ObjectUtils.isNull(organizationOptions)) {
            throw new DocumentInitiationAuthorizationException(ArKeyConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG, 
                    new String[] { "(Users in an AR Billing Org)", "Customer Credit Memo" });

        }
    } 
}

