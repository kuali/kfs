/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.rice.kns.util.KualiDecimal;

public class AssetIncreaseDocument extends EndowmentTaxLotLinesDocumentBase implements Correctable, IncomePrincipalAmountTotaling, IncomePrincipalUnitsTotaling {


    public AssetIncreaseDocument() {
        super();
    }


    /**
     * @see org.kuali.kfs.module.endow.document.IncomePrincipalAmountTotaling#getTotalIncomeAmount()
     */
    public KualiDecimal getTotalIncomeAmount() {
        // TODO Auto-generated method stub
        return KualiDecimal.ZERO;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.IncomePrincipalAmountTotaling#getTotalPrincipalAmount()
     */
    public KualiDecimal getTotalPrincipalAmount() {
        // TODO Auto-generated method stub
        return KualiDecimal.ZERO;
    }

    /**
     * @see org.kuali.kfs.sys.document.AmountTotaling#getTotalDollarAmount()
     */
    public KualiDecimal getTotalDollarAmount() {
        // TODO Auto-generated method stub
        return KualiDecimal.ZERO;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.IncomePrincipalUnitsTotaling#getTotalIncomeUnits()
     */
    public KualiDecimal getTotalIncomeUnits() {
        // TODO Auto-generated method stub
        return KualiDecimal.ZERO;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.IncomePrincipalUnitsTotaling#getTotalPrincipalUnits()
     */
    public KualiDecimal getTotalPrincipalUnits() {
        // TODO Auto-generated method stub
        return KualiDecimal.ZERO;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.UnitsTotaling#getTotalUnits()
     */
    public KualiDecimal getTotalUnits() {
        // TODO Auto-generated method stub
        return KualiDecimal.ZERO;
    }

}
