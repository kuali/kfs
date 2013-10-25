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

   import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

   public class TravelAuthAccountingLineBlanketTripTypeValidation extends GenericValidation {
       //@Override
       @Override
    public boolean validate(AttributedDocumentEvent event) {
           boolean rulePassed = true;
           TravelDocumentBase travelDocument = (TravelDocumentBase)event.getDocument();
           if (travelDocument.getSourceAccountingLines() != null || travelDocument.getSourceTotal().isGreaterThan(KualiDecimal.ZERO)) {
               if (travelDocument.isBlanketTravel()) {
                   // If the user selects Blanket Trip Type, accounting lines are not required since there will be nothing to encumber.
                   // (NOTE: Blanket Travel implies in-state travel)
                   GlobalVariables.getMessageMap().putError(TemPropertyConstants.NEW_SOURCE_ACCTG_LINE, KFSKeyConstants.ERROR_CUSTOM, "Accounting Line is not applicable for Blanket travel.");
                   rulePassed = false;
               }
           }

           return rulePassed;
       }
   }
