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
package org.kuali.module.purap.rules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.AssignContractManagerDetail;
import org.kuali.module.purap.document.AssignContractManagerDocument;
import org.kuali.module.vendor.bo.ContractManager;

/**
 * Business rule(s) applicable to Contract Manager Assignment document.
 */
public class AssignContractManagerDocumentRule extends TransactionalDocumentRuleBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssignContractManagerDocumentRule.class);

    /**
     * @see org.kuali.core.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        AssignContractManagerDocument acmDocument = (AssignContractManagerDocument) document;
        return isValid &= processValidation(acmDocument);
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        AssignContractManagerDocument acmDocument = (AssignContractManagerDocument) approveEvent.getDocument();
        // isValid &= processValidation(acmDocument);
        return isValid;
    }

    /**
     * Perform validation for Contract Manager Assignment document such as validating contract manager codes.
     * 
     * @param document Contract Manager Assignment document
     * @return Boolean indicating if validation succeeded
     */
    private boolean processValidation(AssignContractManagerDocument document) {
        return validateContractManagerCodes(document.getAssignContractManagerDetails());
    }

    /**
     * Review the list of AssignContractManagerDetails where the user has entered ContractManagerCodes,
     * validates that each entered code is valid;
     * on the other hand, validate that at least one row has a valid CM code assigned.
     * 
     * @param assignContractManagerDetails A list containing the code to be validated.
     * @return Boolean indicating if validation succeeded
     */
    public boolean validateContractManagerCodes(List assignContractManagerDetails) {
        LOG.debug("validateContractManagerCodes(): entered method.");
        boolean isValid = true;
        int count = 0;
        for (Iterator iter = assignContractManagerDetails.iterator(); iter.hasNext();) {
            AssignContractManagerDetail detail = (AssignContractManagerDetail) iter.next();

            // Look for the contractManagerCode in the table. If not there the code is invalid.
            if (ObjectUtils.isNotNull(detail.getContractManagerCode())) {
                Map fieldValues = new HashMap();
                fieldValues.put(PurapPropertyConstants.CONTRACT_MANAGER_CODE, detail.getContractManagerCode());
                if (SpringContext.getBean(BusinessObjectService.class).countMatching(ContractManager.class, fieldValues) != 1) {
                    GlobalVariables.getErrorMap().putError(PurapConstants.ASSIGN_CONTRACT_MANAGER_TAB_ERRORS, PurapKeyConstants.INVALID_CONTRACT_MANAGER_CODE, detail.getContractManagerCode().toString());
                    isValid = false;
                }
                else count++;
            }
            
            // check if at least one row has a valid CM code assigned
            if (count<1) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ASSIGN_CONTRACT_MANAGER_TAB_ERRORS, PurapKeyConstants.NO_CONTRACT_MANAGER_ASSIGNED);
                isValid = false;                
            }
        }
        LOG.debug("validateContractManagerCodes(): leaving method.");
        return isValid;
    }
}
