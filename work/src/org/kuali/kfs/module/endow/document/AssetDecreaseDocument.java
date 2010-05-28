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

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;

public class AssetDecreaseDocument extends EndowmentTaxLotLinesDocumentBase implements Correctable, UnitsTotaling, AmountTotaling {

    public AssetDecreaseDocument() {
        super();
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedList = super.buildListOfDeletionAwareLists();

        for (EndowmentTransactionLine endowmentTransactionLine : getTargetTransactionLines()) {
            managedList.add(endowmentTransactionLine.getTaxLotLines());
        }

        for (EndowmentTransactionLine endowmentTransactionLine : getSourceTransactionLines()) {
            managedList.add(endowmentTransactionLine.getTaxLotLines());
        }

        return managedList;
    }

}
