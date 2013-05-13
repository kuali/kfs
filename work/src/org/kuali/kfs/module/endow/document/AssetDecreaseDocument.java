/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.rice.krad.document.Copyable;

/**
 * The Endowment Asset Decrease (EAD) is used to reduce the holdings of a KEMID, either through the cash sale of an asset or by
 * removing it directly from the KEMID holding record.
 */
public class AssetDecreaseDocument extends EndowmentTaxLotLinesDocumentBase implements Correctable, Copyable, UnitsTotaling, AmountTotaling {

    /**
     * Constructs a AssetDecreaseDocument.
     */
    public AssetDecreaseDocument() {
        super();
    }

    @Override
    public void setSourceTransactionLines(List<EndowmentTransactionLine> sourceLines) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTargetTransactionLines(List<EndowmentTransactionLine> targetLines) {
        // TODO Auto-generated method stub
        
    }

}
