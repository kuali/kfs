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
package org.kuali.kfs.module.cab.document.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetAccountsGroupDetails;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;

public interface GlLineService {

    Collection<GeneralLedgerEntry> findAllGeneralLedgerEntry(String documentNumber);

    Collection<GeneralLedgerEntry> findMatchingGeneralLedgerEntries( Collection<GeneralLedgerEntry> allGLEntries, CapitalAssetAccountsGroupDetails accountingDetails );

    List<CapitalAssetInformation> findAllCapitalAssetInformation(String documentNumber);

    List<CapitalAssetInformation> findCapitalAssetInformationForGLLine(GeneralLedgerEntry entry);

    long findUnprocessedCapitalAssetInformation( String documentNumber );

    CapitalAssetInformation findCapitalAssetInformation(String documentNumber, Integer capitalAssetLineNumber);

    Document createAssetGlobalDocument(GeneralLedgerEntry primary, Integer capitalAssetLineNumber) throws WorkflowException;

    Document createAssetPaymentDocument(GeneralLedgerEntry primary, Integer capitalAssetLineNumber) throws WorkflowException;

    void setupCapitalAssetInformation(GeneralLedgerEntry entry);

}
