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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PaymentRequestAccountingLineAccessibleBranchingValidation extends BranchingValidation {

    protected static final String USE_DEFAULT_ACCOUNTING_LINE_ACCESSIBLE="useDefaultAccountingLineAccessible";
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        String status = ((PaymentRequestDocument)event.getDocument()).getApplicationDocumentStatus();
        if (StringUtils.equals(PaymentRequestStatuses.APPDOC_AWAITING_ACCOUNTS_PAYABLE_REVIEW, status)) {
            return null;
        } else if (StringUtils.equals(PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW, status)) {
                return null;
        } else{
            return USE_DEFAULT_ACCOUNTING_LINE_ACCESSIBLE; 
        }
    }

}
