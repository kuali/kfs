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
package org.kuali.kfs.fp.document.authorization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils.RouteLevelNames;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Authorizer for the Disbursement Voucher document.
 */
public class DisbursementVoucherDocumentAuthorizer extends AccountingDocumentAuthorizerBase {


// TODO fix for kim
    //    /**
//     * Overrides to call super and then blanketly set the canBlanketApprove flag to false, since this is never allowed on a DV.
//     * 
//     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kns.bo.user.KualiUser)
//     */
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemTransactionalDocumentActionFlags flags = new FinancialSystemTransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));
//
//        flags.setCanBlanketApprove(false); // this is never allowed on a DV document
//
//        flags.setCanErrorCorrect(false); // CR, DV, and PCDO don't allow error correction
//
//        return flags;
//    }


    /**
     * Determines if the current active routing nodes are one of the disbursement voucher special routing nodes.
     * 
     * @param document
     * @param user
     * @return boolean
     */
    public boolean isSpecialRouting(Document document, Person user) {
        boolean isSpecialRouteNode = false;

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        List<String> activeNodes = getCurrentRouteLevels(workflowDocument);

        List<String> dvSpecialNodes = new ArrayList<String>();
        dvSpecialNodes.add(RouteLevelNames.ALIEN_INDICATOR);
        dvSpecialNodes.add(RouteLevelNames.ALIEN_INDICATOR_PAYMENT_REASON);
        dvSpecialNodes.add(RouteLevelNames.CAMPUS_CODE);
        dvSpecialNodes.add(RouteLevelNames.EMPLOYEE_INDICATOR);
        dvSpecialNodes.add(RouteLevelNames.PAYMENT_METHOD);
        dvSpecialNodes.add(RouteLevelNames.PAYMENT_REASON);
        dvSpecialNodes.add(RouteLevelNames.PAYMENT_REASON_CAMPUS);
        dvSpecialNodes.add(RouteLevelNames.TAX_CONTROL_CODE);

        if (CollectionUtils.containsAny(activeNodes, dvSpecialNodes)) {
            isSpecialRouteNode = true;
        }

        return isSpecialRouteNode;
    }
}

