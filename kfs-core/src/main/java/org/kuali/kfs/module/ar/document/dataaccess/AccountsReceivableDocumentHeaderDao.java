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
package org.kuali.kfs.module.ar.document.dataaccess;

import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;

public interface AccountsReceivableDocumentHeaderDao {

    /**
     * This method retrieves all AccountReceivableDocumentHeader objects for the customerNumber
     *
     * @return AccountReceivableDocumentHeader objects
     */
    public Collection getARDocumentHeadersByCustomerNumber(String customerNumber);

    /**
     * This method retrieves all AccountsReceivableDocumentHeader objects for the customerNumber, processingChartOfAccountCode, and processingOrganizationCode
     *
     * @return AccountReceivableDocumentHeader objects
     */
    public Collection getARDocumentHeadersByCustomerNumberByProcessingOrgCodeAndChartCode(String customerNumber, String processingChartOfAccountCode, String processingOrganizationCode);

    /**
     * This method retrieves all AccountReceivableDocumentHeader objects for the customerNumber
     *
     * @param customerNumber used to retrieve AccountReceivableDocumentHeaders
     * @return Collection of AccountReceivableDocumentHeader objects
     */
    public Collection<AccountsReceivableDocumentHeader> getARDocumentHeadersIncludingHiddenApplicationByCustomerNumber(String customerNumber);

    /**
     * This method retrieve all AR document numbers for a give customer number
     *
     * @param customerNumber used to retrieve AccountReceivableDocumentHeaders
     * @return Collection of AR Document Number Strings
     */
    public Collection<String> getARDocumentNumbersIncludingHiddenApplicationByCustomerNumber(String customerNumber);
}
