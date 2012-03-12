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
