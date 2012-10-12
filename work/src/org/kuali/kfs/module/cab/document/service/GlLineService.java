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
package org.kuali.kfs.module.cab.document.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetAccountsGroupDetails;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;

public interface GlLineService {

    Collection<GeneralLedgerEntry> findMatchingGeneralLedgerEntry(String documentNumber, String chartCode, String accountNumber, String finalcialObjectcode);

    Collection<GeneralLedgerEntry> findAllGeneralLedgerEntry(String documentNumber);

    List<CapitalAssetInformation> findCapitalAssetInformation(GeneralLedgerEntry entry);

    List<CapitalAssetInformation> findAllCapitalAssetInformation(GeneralLedgerEntry entry);

    List<CapitalAssetInformation> findCapitalAssetInformationForGLLine(GeneralLedgerEntry entry);

    long findUnprocessedCapitalAssetInformation(GeneralLedgerEntry entry);

    CapitalAssetInformation findCapitalAssetInformation(GeneralLedgerEntry entry, Integer capitalAssetLineNumber);

    Document createAssetGlobalDocument(GeneralLedgerEntry primary, Integer capitalAssetLineNumber) throws WorkflowException;

    Document createAssetPaymentDocument(GeneralLedgerEntry primary, Integer capitalAssetLineNumber) throws WorkflowException;

    /**
     * Helper method to get the debit or credit code for the given group capital accounting line
     *
     * @param accountingLine
     * @return Debit if the accountingl line is target else return Credit
     */
    public String getDebitCreditForAccountingLineAmount(CapitalAssetAccountsGroupDetails accountingLine);
}
