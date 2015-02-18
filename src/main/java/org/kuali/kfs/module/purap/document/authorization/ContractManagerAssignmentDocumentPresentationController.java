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
package org.kuali.kfs.module.purap.document.authorization;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;


public class ContractManagerAssignmentDocumentPresentationController extends PurchasingAccountsPayableDocumentPresentationController {
    
    @Override
    public boolean canSave(Document document) {
        return false;
    }

    /**
     * Override this method to add extra validation, so that when there's no requisition to
     * assign contract manager, an error message will be displayed, instead of creating an
     * ContractManagerAssignmentDocument.
     * 
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canInitiate(java.lang.String)
     */
    @Override
    public boolean canInitiate(String documentTypeName) {
        int numberOfRequisitions = SpringContext.getBean(RequisitionService.class).getCountOfRequisitionsAwaitingContractManagerAssignment();
        if (numberOfRequisitions == 0) {
            throw new DocumentInitiationException(PurapKeyConstants.ERROR_AUTHORIZATION_ACM_INITIATION, new String[] { documentTypeName }, true);
        }

        return super.canInitiate(documentTypeName);
    }

}
