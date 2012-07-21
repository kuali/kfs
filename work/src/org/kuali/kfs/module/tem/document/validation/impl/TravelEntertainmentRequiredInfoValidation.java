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
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class TravelEntertainmentRequiredInfoValidation extends GenericValidation {
    public static final String USA_COUNTRY_CODE = "US";
    public static final String ATTACHMENT_TYPE_CODE_RECEIPT = "RECEIPT";

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        
        TravelEntertainmentDocument document = (TravelEntertainmentDocument) event.getDocument();
        
        // Check for expense total
        if (document.getDocumentGrandTotal().isLessEqual(KualiDecimal.ZERO)) {
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_TOTAL_ESTIMATE, TemKeyConstants.ERROR_DOCUMENT_TOTAL_ESTIMATED);
        }
        
//        Date beginDate = document.getTripBegin();
//        Date endDate = document.getTripEnd();
//        TravelerDetail traveler = document.getTraveler();
//        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(traveler);
//
//        GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.TRAVELER);
//        SpringContext.getBean(PostalCodeValidationService.class).validateAddress(traveler.getCountryCode(), traveler.getStateCode(), traveler.getZipCode(), "stateCode", "zipCode");
//       
//        GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.TRAVELER);
        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        
        int errCount = GlobalVariables.getMessageMap().getErrorCount();
        if (errCount > 0) {
            return false;
        }   
        
        return true;
    }
}
