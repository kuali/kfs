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
package org.kuali.kfs.module.ec.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ec.EffortConstants.EffortCertificationEditMode;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.util.EffortCertificationParameterFinder;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;

/**
 * Document Authorizer for the Effort Certification document.
 */
public class EffortCertificationDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocumentAuthorizer.class);
// TODO fix for kim   
//     /** @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
//      *  org.kuali.rice.kim.bo.Person)
//      */
//    @Override
//    public Map getEditMode(Document document, Person user) {
//        Map editModeMap = super.getEditMode(document, user);
//        
//        //  set the setHasTotalAmount to true
//        editModeMap.put(EffortCertificationEditMode.HAS_TOTAL_AMOUNT, true);
//        
//        return editModeMap;
//   }
}

