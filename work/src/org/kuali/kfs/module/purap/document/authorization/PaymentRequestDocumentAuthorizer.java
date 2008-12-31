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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.Document;

/**
 * Document Authorizer for the PREQ document.
 */
public class PaymentRequestDocumentAuthorizer extends AccountingDocumentAuthorizerBase {


    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public Map getEditMode(Document document, Person user) {
        Map editModeMap = super.getEditMode(document, user);
        PaymentRequestDocument preq = (PaymentRequestDocument) document;
        PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(preq);

        //FIXME hjs- still some KIM cleanup needed here (the rest has user authorization)
        String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        
        if (!preq.isUseTaxIndicator() &&
            !preqDocAuth.isFullEntryCompleted() && 
            KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apGroup) ) {
            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.CLEAR_ALL_TAXES, "TRUE");
        }
        
        // during Awaiting Tax Approval status, the tax tab is editable to authorized workgroups
        //TODO fix the workgroups once KIM is done
        String taxGroup1 = apGroup; //SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, PurapParameterConstants.Workgroups.PA_NONRESIDENT_ALIEN_TAX_REVIEWERS);
        String taxGroup2 = apGroup; //SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, PurapParameterConstants.Workgroups.PA_EMPLOYEE_VENDOR_REVIEWERS);
        String taxGroup3 = apGroup; //SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, PurapParameterConstants.Workgroups.PA_EMPLOYEE_AND_NONRESIDENT_ALIEN_VENDOR_TAX_REVIEWERS);
        IdentityManagementService idService = KIMServiceLocator.getIdentityManagementService();
        String userId = user.getPrincipalId();
        boolean authorized = idService.isMemberOfGroup(userId, taxGroup1);
        authorized |= idService.isMemberOfGroup(userId, taxGroup2);
        authorized |= idService.isMemberOfGroup(userId, taxGroup3);
        if (authorized && preq.getStatusCode().equalsIgnoreCase("ATAX")) {
            editModeMap.put(PurapAuthorizationConstants.PaymentRequestEditMode.TAX_AREA_EDITABLE, "TRUE");
        }
        

        return editModeMap;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
//FIXME hjs: starting without the override and see what's not displaying correctly
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
//        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
//
//        if (KFSConstants.SYSTEM_USER.equalsIgnoreCase(user.getPrincipalName())) {
//            flags.setCanBlanketApprove(true);
//        }
//
//        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
//        if (StringUtils.equals(paymentRequestDocument.getStatusCode(), PurapConstants.PaymentRequestStatuses.INITIATE)) {
//            flags.setCanSave(false);
//            flags.setCanClose(true);
//            flags.setCanCancel(false);
//            flags.setCanDisapprove(false);
//        }
//        else {
//            if (!getCurrentRouteLevels(workflowDocument).contains(NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW.getName()) ||
//                 StringUtils.equals(paymentRequestDocument.getStatusCode(),PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW)) {
//                flags.setCanDisapprove(false);
//            }
//
//            PaymentRequestDocumentActionAuthorizer preqDocAuth = new PaymentRequestDocumentActionAuthorizer(paymentRequestDocument);
//
//            flags.setCanApprove(preqDocAuth.canApprove());
//            flags.setCanCancel(preqDocAuth.canCancel());
//            flags.setCanSave(preqDocAuth.canSave());
//            
//            //Set can edit bank to true if the document has not been extracted, for now without Kim (more changes when Kim is available).
//            if (!paymentRequestDocument.isExtracted()) {
//                flags.setCanEditBank(true);
//            }
//        }
//
//        // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
//        this.setAnnotateFlag(flags);
//
//        return flags;
//    }

}

