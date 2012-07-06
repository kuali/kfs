/*
 * Copyright 2011 The Kuali Foundation.
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
   package org.kuali.kfs.module.tem.document.validation.impl;

   import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

   public class TravelAuthOtherExpenseBlanketTripTypeValidation extends GenericValidation {
       //@Override
       @Override
    public boolean validate(AttributedDocumentEvent event) {
           boolean rulePassed = true;
           TravelAuthorizationDocument taDocument = (TravelAuthorizationDocument)event.getDocument();
           
           if (taDocument.getTripType() != null && taDocument.getTripType().isBlanketTravel()) {
              // If the user selects Blanket Trip Type, expenses are not required since there will be nothing to encumber.
              // (NOTE: Blanket Travel implies in-state travel)
              GlobalVariables.getMessageMap().putError(TemPropertyConstants.NEW_ACTUAL_EXPENSE_LINE, TemKeyConstants.ERROR_TA_BLANKET_TYPE_NO_EXPENSES);
              taDocument.logErrors();
              rulePassed = false; 
           }              

           return rulePassed;
       }

   }
