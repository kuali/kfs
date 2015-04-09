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
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

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

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        int errCount = GlobalVariables.getMessageMap().getErrorCount();
        return (errCount == 0);
    }
}
