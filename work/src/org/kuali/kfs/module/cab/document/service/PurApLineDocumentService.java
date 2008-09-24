/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.document.service;

import java.util.List;

import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableActionHistory;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.rice.kew.exception.WorkflowException;


/**
 * This class declares methods used by CAB PurAp Line process
 */
public interface PurApLineDocumentService {

    /**
     * Create CAMs asset global document.
     * 
     * @param selectedItem
     * @param purApForm
     * @param purApLineSession
     * @return
     * @throws WorkflowException
     */
    String processCreateAsset(PurchasingAccountsPayableItemAsset selectedItem, PurApLineForm purApForm, PurApLineSession purApLineSession) throws WorkflowException;
    
    /**
     * Create CAMS asset payment document.
     * 
     * @param selectedItem
     * @param purApForm
     * @param purApLineSession
     * @return
     * @throws WorkflowException
     */
    String processApplyPayment(PurchasingAccountsPayableItemAsset selectedItem, PurApLineForm purApForm, PurApLineSession purApLineSession) throws WorkflowException;
}
