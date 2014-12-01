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
package org.kuali.kfs.fp.dataaccess;

import java.util.Collection;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;

public interface DisbursementVoucherDao {

    /**
     * Returns a document by its document number
     * 
     * @param fdocNbr
     * @return document
     */
    public DisbursementVoucherDocument getDocument(String fdocNbr);

    /**
     * Returns a list of disbursement voucher documents with a specific doc header status
     * 
     * @param statusCode
     * @param immediatesOnly retrieve the only DV's marked for immediate payment
     * @return list of doc headers
     */
    public Collection getDocumentsByHeaderStatus(String statusCode, boolean immediatesOnly);
}

