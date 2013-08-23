/*
 * Copyright 2013 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Overridden to more gracefully handle advance accounting lines
 */
public class TravelAuthorizationAccountingLineAccessibleValidation extends AccountingLineAccessibleValidation {

    /**
     * Returns advance accounting line if possible
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation#getGroupName()
     */
    @Override
    protected String getGroupName() {
        if (TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE.equals(getAccountingLineForValidation().getFinancialDocumentLineTypeCode())) {
            return TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_GROUP_NAME;
        }
        return super.getGroupName();
    }

    /**
     * Returns the advance accounting line permission if
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation#getAccountingLineCollectionProperty()
     */
    @Override
    protected String getAccountingLineCollectionProperty() {
        String propertyName = null;
        if (GlobalVariables.getMessageMap().getErrorPath().size() > 0) {
            propertyName = GlobalVariables.getMessageMap().getErrorPath().get(0).replaceFirst(".*?document\\.", ""); // respect error path no matter what
            if (propertyName.equals("newAdvanceLine")) {
                return TemConstants.PermissionAttributeValue.ADVANCE_ACCOUNTING_LINES.value;
            }
        }
        if (TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE.equals(getAccountingLineForValidation().getFinancialDocumentLineTypeCode())) {
            return TemConstants.PermissionAttributeValue.ADVANCE_ACCOUNTING_LINES.value;
        }
        return super.getAccountingLineCollectionProperty();
    }
}
