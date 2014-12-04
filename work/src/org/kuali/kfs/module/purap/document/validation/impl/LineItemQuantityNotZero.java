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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

public class LineItemQuantityNotZero extends GenericValidation 
{

    public boolean validate(AttributedDocumentEvent event) 
    {
        boolean valid = true;
        
        PaymentRequestDocument document = (PaymentRequestDocument)event.getDocument();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        int i = 0;
        for (PurApItem item : (List<PurApItem>)document.getItems()) {
            KualiDecimal itemQuantity = item.getItemQuantity();
            if (itemQuantity != null) {
                if (!itemQuantity.isNonZero()) {
                    GlobalVariables.getMessageMap().putError("item[" + i + "].itemQuantity", PurapKeyConstants.ERROR_PAYMENT_REQUEST_LINE_ITEM_QUANTITY_ZERO);
                    GlobalVariables.getMessageMap().clearErrorPath();
                    
                    valid = false;
                    break;
                }
                i++;
            }
        }
        
        return valid;
    }

}
