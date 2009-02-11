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

import java.util.List;

import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.rice.kim.bo.Person;

public class AssetPaymentAccountingLineAuthorizer extends AccountingLineAuthorizerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnField(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.String)
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) accountingDocument;
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            return false;
        }

        return super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName);
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#isGroupReadOnly(org.kuali.kfs.sys.document.AccountingDocument,
     *      java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean isGroupEditable(AccountingDocument accountingDocument, List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts, Person currentUser) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) accountingDocument;
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            return false;
        }

        return super.isGroupEditable(accountingDocument, accountingLineRenderingContexts, currentUser);
    }
}