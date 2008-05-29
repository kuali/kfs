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
package org.kuali.module.cams.gl;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.AssetGlpeSourceDetail;

public class AssetRetirementGeneralLedgerPendingEntrySource extends CamsGeneralLedgerPendingEntrySourceBase {

    public AssetRetirementGeneralLedgerPendingEntrySource(DocumentHeader documentHeader) {
        super(documentHeader);
    }

    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        boolean debit = false;
        AssetGlpeSourceDetail assetPostable = (AssetGlpeSourceDetail)postable;
        KualiDecimal amount = assetPostable.getAmount();
        
        if((assetPostable.isCapitalization() && amount.isNegative()) || (assetPostable.isAccumulatedDepreciation() && amount.isPositive()) || (assetPostable.isOffset() && amount.isPositive())) {
            debit = true;
        }
        return debit;
    }

    public String getFinancialDocumentTypeCode() {
        return CamsConstants.AssetRetirementGlobal.FIS_DOC_TYPE_CODE;
    }

}
