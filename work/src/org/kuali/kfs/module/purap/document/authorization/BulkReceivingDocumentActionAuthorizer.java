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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;

/**
 * This class determines permissions for a user to view the
 * buttons on Bulk Receiving document.
 * 
 */
public class BulkReceivingDocumentActionAuthorizer {

    private BulkReceivingDocument bulkReceivingDocument;
    private boolean isUserAuthorized;
    
    public BulkReceivingDocumentActionAuthorizer(BulkReceivingDocument bulkDoc, 
                                                 Map editingMode) {
        
        
        this.bulkReceivingDocument = bulkDoc;

        /**
         * TODO:Have to remove this code..
         */
//        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
//        String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(BulkReceivingDocument.class, PurapParameterConstants.Workgroups.PURAP_DOCUMENT_PO_ACTIONS);
//
//        try {
//            this.isUserAuthorized = SpringContext.getBean(KualiGroupService.class).getByGroupName(authorizedWorkgroup).hasMember(user);
//        }
//        catch (GroupNotFoundException gnfe) {
//            this.isUserAuthorized = false;
//        }
        
    }

    public boolean canPrintReceivingTicket() {        
        return SpringContext.getBean(BulkReceivingService.class).canPrintReceivingTicket(bulkReceivingDocument);
    }
}
