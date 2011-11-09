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
package org.kuali.kfs.module.cam.document.gl;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlpeSourceDetail;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class AssetGlobalGeneralLedgerPendingEntrySource extends CamsGeneralLedgerPendingEntrySourceBase {

    public AssetGlobalGeneralLedgerPendingEntrySource(FinancialSystemDocumentHeader documentHeader) {
        super(documentHeader);
    }
    
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        boolean debit = false;
        AssetGlpeSourceDetail assetPostable = (AssetGlpeSourceDetail)postable;
        KualiDecimal amount = assetPostable.getAmount();
        
        if((assetPostable.isCapitalization() && amount.isPositive()) || (assetPostable.isCapitalizationOffset() && amount.isNegative()) || (assetPostable.isPayment() && amount.isPositive()) || (assetPostable.isPaymentOffset() && amount.isNegative())) {
            debit = true;
        }
        return debit;
    }

    public String getFinancialDocumentTypeCode() {
        return CamsConstants.DocumentTypeName.ASSET_ADD_GLOBAL;
    }

}
