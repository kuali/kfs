/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Presentation Controller for Collection Activity Document.
 */
public class CategoryDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase{

    public Set<String> getDocumentActions(Document document) {
        // TODO Auto-generated method stub
        GlobalVariables.getMessageMap().putInfo("EditCategory", ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO, null);

        return super.getDocumentActions(document);
    }

    
}
