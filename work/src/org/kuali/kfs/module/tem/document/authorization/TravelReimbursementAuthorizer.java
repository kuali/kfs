/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.List;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.util.ObjectUtils;

public class TravelReimbursementAuthorizer extends TravelArrangeableAuthorizer {

    /**
     * @see org.kuali.kfs.module.tem.document.authorization.ReturnToFiscalOfficerAuthorizer#canReturnToFisicalOfficer(org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean canReturnToFisicalOfficer(final TravelDocument taDoc, final Person user) {
        if (ObjectUtils.isNull(user)) {
            return false;
        }
        // first check to see if the user is either the initiator or is a fiscal officer for this doc

        // initiator cannot Hold their own doc
        String initiator = taDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId();
        if (initiator.equals(user.getPrincipalId())) {
            return false;
        }

        // now check to see if they are a Fiscal Officer
        for (AccountingLine line : ((List<SourceAccountingLine>) taDoc.getSourceAccountingLines())) {
            if (line.getAccount().getAccountFiscalOfficerUser().getPrincipalId().equals(user.getPrincipalId())) {
                return false;
            }
        }

        String nameSpaceCode = org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME, org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);

        return getIdentityManagementService().isAuthorized(user.getPrincipalId(), nameSpaceCode, TemConstants.Permission.RETURN_TO_FO, permissionDetails, null);

    }

    /**
     * 
     * @param reimbursement
     * @param user
     * @return
     */
    public boolean canCertify(final TravelReimbursementDocument reimbursement, Person user) {
        return user.getPrincipalId().equals(reimbursement.getTraveler().getPrincipalId()) || !isEmployee(reimbursement.getTraveler());
    }
}
