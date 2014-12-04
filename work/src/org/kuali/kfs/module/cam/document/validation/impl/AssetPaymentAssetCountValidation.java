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
package org.kuali.kfs.module.cam.document.validation.impl;

import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * This class validates to make sure that at least one payment line is available for the document
 */
public class AssetPaymentAssetCountValidation extends GenericValidation {

    /**
     * Validate accounting line count to ensure minimum of one line
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();

        //IF no assets found in document, then....
        if (assetPaymentDocument.getAssetPaymentAssetDetail().size() == 0) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsPropertyConstants.COMMON_ERROR_SECTION_ID,CamsKeyConstants.Payment.ERROR_NON_ASSETS_IN_DOCUMENT);
            valid = false;
        }
        
        return valid;
    }
}
