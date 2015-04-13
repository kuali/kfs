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
package org.kuali.kfs.module.cab.document.service;

import java.util.List;

import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.rice.kew.api.exception.WorkflowException;


/**
 * This class declares methods used by CAB PurAp Line process
 */
public interface PurApLineDocumentService {

    /**
     * Create CAMs asset global document.
     * 
     * @param selectedItem
     * @param purApDocs
     * @param purApLineSession
     * @param requisitionIdentifier
     * @return
     * @throws WorkflowException
     */
    String processCreateAsset(PurchasingAccountsPayableItemAsset selectedItem, List<PurchasingAccountsPayableDocument> purApDocs, PurApLineSession purApLineSession, Integer requisitionIdentifier) throws WorkflowException;

    /**
     * Create CAMS asset payment document.
     * 
     * @param selectedItem
     * @param purApDocs
     * @param purApLineSession
     * @param requistionIdentifer
     * @return
     * @throws WorkflowException
     */
    String processApplyPayment(PurchasingAccountsPayableItemAsset selectedItem, List<PurchasingAccountsPayableDocument> purApDocs, PurApLineSession purApLineSession, Integer requistionIdentifer) throws WorkflowException;

}
