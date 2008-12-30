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
package org.kuali.kfs.module.cg.document.authorization;

import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.QuestionType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;

public class QuestionTypeMaintenanceDocumentAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    
    // TODO move to presentation controller

//    @Override
//    public void addMaintenanceDocumentRestrictions(MaintenanceDocumentAuthorizations auths, MaintenanceDocument document, Person user) {
//
//        QuestionType question = (QuestionType) document.getNewMaintainableObject().getBusinessObject();
//        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
//        QuestionType persistedQuestion = (QuestionType) service.retrieve(question);
//
//        // If the question exists in db, set read-only fields
//        if (ObjectUtils.isNotNull(persistedQuestion)) {
//            auths.addReadonlyAuthField(CGPropertyConstants.QUESTION_TYPE_DESCRIPTION);
//        }
//    }
}

