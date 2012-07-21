/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.KNSConstants;

public class TEMAccountingLineAuthorizer extends AccountingLineAuthorizerBase {
    private static Log LOG = LogFactory.getLog(TEMAccountingLineAuthorizer.class);

    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        TravelDocumentBase document = (TravelDocumentBase) accountingDocument;
        boolean hasPermission = super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);
        TravelDocumentPresentationController documentPresentationController = (TravelDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(document);
        boolean canUpdate = documentPresentationController.enableForTravelManager(document.getDocumentHeader().getWorkflowDocument());
        
        if (document.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames().equals(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL)){
            return hasPermission && canUpdate;
        }
        return hasPermission;
        
    }

    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, boolean currentUserIsDocumentInitiator, boolean pageIsEditable) {
        TravelDocumentBase document = (TravelDocumentBase) accountingDocument;
        boolean hasPermission = super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator, pageIsEditable);
        TravelDocumentPresentationController documentPresentationController = (TravelDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(document);
        boolean canUpdate = documentPresentationController.enableForTravelManager(document.getDocumentHeader().getWorkflowDocument());
        
        if (document.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames().equals(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL)){
            return hasPermission && canUpdate;
        }
        return hasPermission;
    }
    
    private DocumentHelperService getDocumentHelperService() {
        return SpringContext.getBean(DocumentHelperService.class);
    }
    
    /**
     * Overrides the method in AccountingLineAuthorizerBase so that the add button would
     * have the line item number in addition to the rest of the insertxxxx String for
     * methodToCall when the user clicks on the add button.
     * 
     * @param accountingLine
     * @param accountingLineProperty
     * @return
     */
    @Override
    protected String getAddMethod(AccountingLine accountingLine, String accountingLineProperty) {
        //return super.getAddMethod(accountingLine, accountingLineProperty);
        String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);
        if (accountingLineProperty.equals(TemPropertyConstants.ACCOUNT_DISTRIBUTION_NEW_SRC_LINE)) {
            infix = "Distribution";
        }
        return KFSConstants.INSERT_METHOD + infix + "Line.anchoraccounting" + infix + "Anchor";
    }
    
    
    @Override
    protected String getDeleteLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        if (accountingLineProperty.contains("Distribution")) {
            infix = "Distribution";
        }
        return KNSConstants.DELETE_METHOD + infix + "Line.line" + accountingLineIndex + ".anchoraccounting" + infix + "Anchor";
    }

    @Override
    protected String getBalanceInquiryMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        if (accountingLineProperty.contains("Distribution")) {
            infix = "Distribution";
        }
        return KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD + infix + "Line.line" + accountingLineIndex + ".anchoraccounting" + infix + "existingLineLineAnchor" + accountingLineIndex;
    }
    
    
}

