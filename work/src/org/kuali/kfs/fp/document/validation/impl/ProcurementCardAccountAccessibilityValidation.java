/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;
import org.kuali.rice.krad.util.GlobalVariables;

public class ProcurementCardAccountAccessibilityValidation extends AccountingLineAccessibleValidation {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardAccountAccessibilityValidation.class);

    /**
     * KFSCNTRB-1677
     * Overrides to deal with the special case with PCDO on source and target accounting lines, where the propertyName is
     * something like "newTargetLines[i]", instead of "newTargetLines".
     * @return the accounting line collection property
     */
    @Override
    protected String getAccountingLineCollectionProperty() {
        String propertyName = null;
        if (GlobalVariables.getMessageMap().getErrorPath().size() > 0) {
            propertyName = GlobalVariables.getMessageMap().getErrorPath().get(0).replaceFirst(".*?document\\.", "");
        } else {
            propertyName = accountingLineForValidation.isSourceAccountingLine() ? KFSConstants.PermissionAttributeValue.SOURCE_ACCOUNTING_LINES.value : KFSConstants.PermissionAttributeValue.TARGET_ACCOUNTING_LINES.value;
        }
        if (propertyName.startsWith("newSourceLine")) {
            return KFSConstants.PermissionAttributeValue.SOURCE_ACCOUNTING_LINES.value;
        }
        if (propertyName.startsWith("newTargetLine")) {
            return KFSConstants.PermissionAttributeValue.TARGET_ACCOUNTING_LINES.value;
        }
        return propertyName;
    }

}
