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

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This class determines permissions for a user to view the
 * buttons on Purchase Order Document.
 * 
 */
public class LineItemReceivingDocumentActionAuthorizer {

    private LineItemReceivingDocument receivingLine;
    private boolean isUserAuthorized;
    
    /**
     * Constructs a LineItemReceivingDocumentActionAuthorizer.
     * 
     * @param po A LineItemReceivingDocument
     */
    public LineItemReceivingDocumentActionAuthorizer(LineItemReceivingDocument rl, Map editingMode) {
        
        this.receivingLine = rl;
        
        Person user = GlobalVariables.getUserSession().getPerson();
        String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(PurchaseOrderDocument.class, PurapParameterConstants.Workgroups.PURAP_DOCUMENT_PO_ACTIONS);

        KimGroup group = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName(org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, authorizedWorkgroup);
        if (group != null) {
            this.isUserAuthorized = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), group.getGroupId());
        } else {
            this.isUserAuthorized = false;
        }

        
    }

    /**
     * Determines if a receiving document can be created for the purchase order.
     * 
     * @return
     */
    public boolean canCreateCorrection() {        
        return SpringContext.getBean(ReceivingService.class).canCreateCorrectionReceivingDocument(receivingLine) && isUserAuthorized;
    }
}
