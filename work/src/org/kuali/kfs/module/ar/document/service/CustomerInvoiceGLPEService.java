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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This service helps in the creation of GLPE's for invoice related documents
 */
public interface CustomerInvoiceGLPEService {
    
   /**
    * This methode cretae and add generic invoice related GLPEs.
    * 
    * @param glpeSource
    * @param glpeSourceDetail
    * @param sequenceHelper
    * @param isDebit
    * @param hasClaimOnCashOffset
    * @param amount
    */
   public void createAndAddGenericInvoiceRelatedGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount);

   /**
    * This methode cretae and add generic invoice related GLPEs.
    * 
    * @param glpeSource
    * @param glpeSourceDetail
    * @param sequenceHelper
    * @param isDebit
    * @param hasReceivableClaimOnCashOffset
    * @param writeoffTaxGenerationMethodDisallowFlag
    * @param amount
    */
   public void createAndAddGenericInvoiceRelatedGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasReceivableClaimOnCashOffset, boolean writeoffTaxGenerationMethodDisallowFlag, KualiDecimal amount);
}
