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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.ContractManagerAssignmentDetail;
import org.kuali.kfs.module.purap.document.ContractManagerAssignmentDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business rule(s) applicable to Contract Manager Assignment document.
 */
public class ContractManagerAssignmentDocumentRule extends TransactionalDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractManagerAssignmentDocumentRule.class);

    /**
     * @see org.kuali.rice.krad.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        ContractManagerAssignmentDocument acmDocument = (ContractManagerAssignmentDocument) document;
        return isValid &= processValidation(acmDocument);
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.krad.rule.event.ApproveDocumentEvent)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        ContractManagerAssignmentDocument acmDocument = (ContractManagerAssignmentDocument) approveEvent.getDocument();
        // isValid &= processValidation(acmDocument);
        return isValid;
    }

    /**
     * Perform validation for Contract Manager Assignment document such as validating contract manager codes.
     *
     * @param document Contract Manager Assignment document
     * @return Boolean indicating if validation succeeded
     */
    protected boolean processValidation(ContractManagerAssignmentDocument document) {
        return validateContractManagerCodes(document.getContractManagerAssignmentDetails());
    }

    /**
     * Review the list of ContractManagerAssignmentDetails where the user has entered ContractManagerCodes,
     * validates that each entered code is valid;
     * on the other hand, validate that at least one row has a valid CM code assigned.
     *
     * @param contractManagerAssignmentDetails A list containing the code to be validated.
     * @return Boolean indicating if validation succeeded
     */
    public boolean validateContractManagerCodes(List contractManagerAssignmentDetails) {
        LOG.debug("validateContractManagerCodes(): entered method.");
        boolean isValid = true;
        int count = 0;
        int index = 0;

        String propertyNamePattern = "document.contractManagerAssignmentDetails[{0}].contractManagerCode";
        for (Iterator iter = contractManagerAssignmentDetails.iterator(); iter.hasNext();) {
            ContractManagerAssignmentDetail detail = (ContractManagerAssignmentDetail) iter.next();

            // Look for the contractManagerCode in the table. If not there the code is invalid.
            if (ObjectUtils.isNotNull(detail.getContractManagerCode())) {
                Map fieldValues = new HashMap();
                fieldValues.put(PurapPropertyConstants.CONTRACT_MANAGER_CODE, detail.getContractManagerCode());
                fieldValues.put(KRADPropertyConstants.ACTIVE, true);
                String propertyWithInvalidValue = MessageFormat.format(propertyNamePattern, index);
                if (SpringContext.getBean(BusinessObjectService.class).countMatching(ContractManager.class, fieldValues) != 1) {
                    GlobalVariables.getMessageMap().putError(propertyWithInvalidValue, PurapKeyConstants.INVALID_CONTRACT_MANAGER_CODE, detail.getContractManagerCode().toString());
                    isValid = false;
                }

                if (detail.getContractManagerCode().equals(PurapConstants.APO_CONTRACT_MANAGER)) {
                    GlobalVariables.getMessageMap().putError(propertyWithInvalidValue, PurapKeyConstants.ERROR_APO_CONTRACT_MANAGER_CODE_CHOSEN, detail.getContractManagerCode().toString());
                    isValid = false;
                }

                if(isValid){
                    count++;
                }
            }

            index++;
        }

        // check if at least one row has a valid CM code assigned
        if (count < 1) {
            GlobalVariables.getMessageMap().putError(PurapConstants.ASSIGN_CONTRACT_MANAGER_TAB_ERRORS, PurapKeyConstants.NO_CONTRACT_MANAGER_ASSIGNED);
            isValid = false;
        }

        LOG.debug("validateContractManagerCodes(): leaving method.");
        return isValid;
    }
}
