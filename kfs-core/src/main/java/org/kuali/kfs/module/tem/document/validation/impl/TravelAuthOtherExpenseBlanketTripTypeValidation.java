/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
   package org.kuali.kfs.module.tem.document.validation.impl;

   import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

   public class TravelAuthOtherExpenseBlanketTripTypeValidation extends GenericValidation {
    @Override
    public boolean validate(AttributedDocumentEvent event) {
           boolean rulePassed = true;
           TravelAuthorizationDocument taDocument = (TravelAuthorizationDocument)event.getDocument();

           if (taDocument.isBlanketTravel()) {
              // If the user selects Blanket Trip Type, expenses are not required since there will be nothing to encumber.
              // (NOTE: Blanket Travel implies in-state travel)
              GlobalVariables.getMessageMap().putError(TemPropertyConstants.NEW_ACTUAL_EXPENSE_LINE, TemKeyConstants.ERROR_TA_BLANKET_TYPE_NO_EXPENSES);
              taDocument.logErrors();
              rulePassed = false;
           }

           return rulePassed;
       }

   }
