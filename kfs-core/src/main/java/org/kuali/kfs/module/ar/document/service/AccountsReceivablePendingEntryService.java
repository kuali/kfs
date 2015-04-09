/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This service helps in the creation of GLPE's for accounts receivable documents
 */
public interface AccountsReceivablePendingEntryService {

   /**
    * This method creates and adds generic invoice related GLPEs.
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
    * This method creates and adds generic invoice related GLPEs.
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

   /**
    * Determines the proper object code to use for pending entries generated from the given invoicePaidApplied
    * @param invoicePaidApplied the invoicePaidApplied to find an object code for
    * @return the object code to use
    */
   public ObjectCode getAccountsReceivableObjectCode(InvoicePaidApplied invoicePaidApplied);

   /**
    * This method returns the correct accounts receivable object code based on a receivable parameter.
    *
    * @param customerInvoiceDetail
    * @return
    */
   public String getAccountsReceivableObjectCode(CustomerInvoiceDetail customerInvoiceDetail);
}
