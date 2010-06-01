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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineParser;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Copyable;
import org.kuali.rice.kns.util.KualiDecimal;

public class LiabilityDecreaseDocument extends EndowmentTaxLotLinesDocumentBase implements Copyable, Correctable, UnitsTotaling, AmountTotaling {


    public LiabilityDecreaseDocument() {
        super();
        setTransactionSourceTypeCode("M");
    }

    @Override
    public void prepareForSave() 
    {
        super.prepareForSave();
    }
    
     /**
     * @see org.kuali.kfs.sys.document.Correctable#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException, IllegalStateException 
    {
        super.toErrorCorrection();
    }

}
