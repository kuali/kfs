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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants.TransactionSourceTypeCode;
import org.kuali.kfs.module.endow.EndowConstants.TransactionSubTypeCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSourceType;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSubType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;

public class SecurityTransferDocument extends EndowmentTaxLotLinesDocumentBase implements Correctable, UnitsTotaling, AmountTotaling 
{


    public SecurityTransferDocument() {
        super();
        setTransactionSourceTypeCode(TransactionSourceTypeCode.MANUAL);
        setTransactionSubTypeCode(TransactionSubTypeCode.NON_CASH);
        
        initializeSubType();
    }

    @Override
    public void prepareForSave() {
        super.prepareForSave();
    }

}
