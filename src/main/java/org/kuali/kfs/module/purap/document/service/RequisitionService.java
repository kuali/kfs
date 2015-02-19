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
package org.kuali.kfs.module.purap.document.service;

import java.util.List;

import org.kuali.kfs.module.purap.document.RequisitionDocument;

/**
 * Defines methods that must be implemented by classes providing a RequisitionService.
 */
public interface RequisitionService extends PurchasingDocumentSpecificService {

    /**
     * Obtains the requisition document from the database given a requisition id as the input parameter
     * 
     * @param id the requisition id of the document we want to obtain.
     * @return RequisitionDocument the requisition document whose requisition id is the id in the input parameter of this method.
     */
    public RequisitionDocument getRequisitionById(Integer id);

    /**
     * Checks whether the requisition is eligible to become an Automatic Purchase Order (APO)
     * 
     * @param requisition the requisition document to be checked.
     * @return boolean true if the requisition is eligible to become APO.
     */
    public boolean isAutomaticPurchaseOrderAllowed(RequisitionDocument requisition);
    
    /**
     * Returns the list of Requisitions that are awaiting contract manager assignment
     *
     * @return List<RequisitionDocument>
     */
    public List<RequisitionDocument> getRequisitionsAwaitingContractManagerAssignment();
    
    /**
     * Returns the count of how many Requisitions are awaiting contract manager assignment
     *
     * @return int
     */
    public int getCountOfRequisitionsAwaitingContractManagerAssignment();

    /**
     * Returns true if there is at least one member of the "Content Reviewer" role for the given organization.
     *
     * @param organizationCode
     * @param chartOfAccountsCode
     * @return
     */
    public boolean hasContentReviewer(String organizationCode, String chartOfAccountsCode);
    
}
