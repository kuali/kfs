/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/document/validation/impl/AssignContractManagerDocumentRule.java,v $
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
package org.kuali.module.purap.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBase;
import org.kuali.module.kra.budget.KraKeyConstants;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.util.AuditCluster;
import org.kuali.module.kra.budget.util.AuditError;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.AssignContractManagerDetail;
import org.kuali.module.purap.bo.ContractManager;
import org.kuali.module.purap.bo.VendorAddress;
import org.kuali.module.purap.document.AssignContractManagerDocument;
import org.kuali.module.purap.document.RequisitionDocument;

public class AssignContractManagerDocumentRule extends TransactionalDocumentRuleBase {

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        AssignContractManagerDocument acmDocument = (AssignContractManagerDocument) document;
        return isValid &= processValidation(acmDocument);
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        AssignContractManagerDocument acmDocument = (AssignContractManagerDocument) document;
        return isValid &= processValidation(acmDocument);
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        AssignContractManagerDocument acmDocument = (AssignContractManagerDocument) approveEvent.getDocument();
        return isValid &= processValidation(acmDocument);
    }

//  boolean processAdditionalValidation(RequisitionDocument document) {
//  boolean valid = super.processAdditionalValidation(document);
//  // TODO code validation
//  validateFaxNumberIfTransmissionTypeIsFax(document);
//  return valid;
//}

    private boolean processValidation(AssignContractManagerDocument document) {
        boolean valid = true;

        //document.setUserAssignedRequisitions(document.getUnassignedRequisitions());

        valid &= this.validateContractManagerCodes(document.getAssignContractManagerDetails());
        
        return valid;
    }
    
    boolean processContractManagerValidation(AssignContractManagerDocument document) {

        return false;
    }

    

    /**
     * This method ensures that a valid ContractManagerCode has been entered.
     * 
     * @param fieldValues   A Map containing the code to be validated.
     */
    public boolean validateContractManagerCodes(List assignContractManagerDetails){
        LOG.debug("validateContractManagerCodes(): entered method.");
        boolean isValid = true;
        for (Iterator iter = assignContractManagerDetails.iterator(); iter.hasNext();) {
            AssignContractManagerDetail detail = (AssignContractManagerDetail) iter.next();
            
            // Look for the contractManagerCode in the table. If not there the code is invalid.
            if ( ObjectUtils.isNotNull( detail.getContractManagerCode() ) ) {
                Map fieldValues = new HashMap();
                fieldValues.put(PurapPropertyConstants.CONTRACT_MANAGER_CODE, detail.getContractManagerCode());
                if ( SpringServiceLocator.getBusinessObjectService().countMatching(ContractManager.class, fieldValues) != 1 ) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CONTRACT_MANAGER_CODE,
                            PurapKeyConstants.INVALID_CONTRACT_MANAGER_CODE);
                    isValid = false;
                }
            }
        }
        LOG.debug("validateContractManagerCodes(): leaving method.");
        return isValid;
    }
    
    // TODO: this method has to be here because we're extending the TransactionalDocumentRuleBase:
    //   is this the best way to do it?
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
//        return IsDebitUtils.isDebitConsideringSectionAndTypePositiveOnly(this, transactionalDocument, accountingLine);
        return false;
    }

}
