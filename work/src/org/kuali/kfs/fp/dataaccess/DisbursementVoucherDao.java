/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.fp.dataaccess;

import java.util.Collection;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.rice.kim.bo.Person;
import org.kuali.kfs.vnd.businessobject.VendorDetail;

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
     * @return list of doc headers
     */
    public Collection getDocumentsByHeaderStatus(String statusCode);
}

