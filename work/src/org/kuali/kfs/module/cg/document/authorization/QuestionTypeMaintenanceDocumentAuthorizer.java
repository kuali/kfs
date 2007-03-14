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
package org.kuali.module.kra.routingform.document;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.KraPropertyConstants;
import org.kuali.module.kra.routingform.bo.QuestionType;
import org.kuali.module.kra.routingform.bo.ResearchRiskType;

public class QuestionTypeMaintenanceDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {
    
    @Override
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        
        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();
        QuestionType question = (QuestionType) document.getNewMaintainableObject().getBusinessObject();
        BusinessObjectService service = SpringServiceLocator.getBusinessObjectService();
        QuestionType persistedQuestion = (QuestionType) service.retrieve(question);
        
        //If the question exists in db, set read-only fields
        if (ObjectUtils.isNotNull(persistedQuestion)) {
            auths.addReadonlyAuthField(KraPropertyConstants.QUESTION_TYPE_DESCRIPTION);
        }

        return auths;
    }
}
