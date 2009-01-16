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

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class determines permissions for a user of the Payment Request document
 */
public class PaymentRequestDocumentActionAuthorizer implements Serializable {

    private String docStatus;
    private boolean requestCancelIndicator;
    private boolean holdIndicator;
    private boolean extracted;
    private boolean adHocRequested;
    
    private PaymentRequestDocument paymentRequest;
    private Map documentActions;
    private Person user;
    
    /**
     * Constructs a PaymentRequestDocumentActionAuthorizer.
     * 
     * @param paymentRequest A PaymentRequestDocument
     */
    public PaymentRequestDocumentActionAuthorizer(PaymentRequestDocument paymentRequest, Map documentActions) {
        docStatus = paymentRequest.getStatusCode();
        requestCancelIndicator = paymentRequest.getPaymentRequestedCancelIndicator();
        holdIndicator = paymentRequest.isHoldIndicator();        
        extracted = paymentRequest.getExtractedTimestamp() != null;
        adHocRequested = paymentRequest.getDocumentHeader().getWorkflowDocument().isAdHocRequested();
        
        this.paymentRequest = paymentRequest;
        this.documentActions = documentActions;
        user = GlobalVariables.getUserSession().getPerson();        
    }


    //TODO remove
    /**
     * Predicate to determine whether the current user can cancel the paymentRequest.
     * 
     * @return True if the current user can cancel.
     */
    public boolean canCancel() {
        boolean can = !StringUtils.equals(paymentRequest.getStatusCode(), PaymentRequestStatuses.INITIATE);
        
        boolean preroute = 
            PaymentRequestStatuses.IN_PROCESS.equals(docStatus) || 
            PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW.equals(docStatus);
        boolean enroute = 
            PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_REVIEW.equals(docStatus) ||
            PaymentRequestStatuses.AWAITING_FISCAL_REVIEW.equals(docStatus) || 
            PaymentRequestStatuses.AWAITING_ORG_REVIEW.equals(docStatus) || 
            PaymentRequestStatuses.AWAITING_TAX_REVIEW.equals(docStatus);
        boolean postroute = 
            PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(docStatus) || 
            PaymentRequestStatuses.AUTO_APPROVED.equals(docStatus);
        
        if (preroute) {
            can = isApUser() || isApSupervisor();
        }
        else if (enroute) {
            can = isApUser() && requestCancelIndicator || isApSupervisor();
        }
        else if (postroute) {
            can = (isApUser() || isApSupervisor()) && !requestCancelIndicator && !holdIndicator && !extracted;
        }
        
        return can;
    }
    
    
    //TODO remove
    public boolean isApUser() {
        String apGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apGroup);
    }

    //TODO remove
    public boolean isApSupervisor() {
        String apSupGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE_SUPERVISOR);
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apSupGroup);
    }
    
}

