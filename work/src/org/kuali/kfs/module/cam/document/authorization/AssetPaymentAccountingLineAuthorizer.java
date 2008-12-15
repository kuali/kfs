/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.AuthorizationConstants;

public class AssetPaymentAccountingLineAuthorizer extends AccountingLineAuthorizerBase {
    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getEditModeForAccountingLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, boolean, org.kuali.rice.kim.bo.Person, java.util.Map)
     */
    @Override
    public String getEditModeForAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser, Map<String, String> editModesForDocument) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) accountingDocument;
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            return AuthorizationConstants.EditMode.VIEW_ONLY;
        }
        else {
            return super.getEditModeForAccountingLine(accountingDocument, accountingLine, newLine, currentUser, editModesForDocument);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#isGroupReadOnly(org.kuali.kfs.sys.document.AccountingDocument,
     *      java.lang.String, org.kuali.rice.kim.bo.Person, java.util.Map)
     */
    @Override
    public boolean isGroupReadOnly(AccountingDocument accountingDocument, String accountingLineCollectionProperty, Person currentUser, Map<String, String> editModesForDocument) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) accountingDocument;
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            return true;
        }
        return super.isGroupReadOnly(accountingDocument, accountingLineCollectionProperty, currentUser, editModesForDocument);
    }
}
