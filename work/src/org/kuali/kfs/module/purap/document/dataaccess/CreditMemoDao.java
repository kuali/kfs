/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.dao;

import java.sql.Date;
import java.util.Iterator;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.CreditMemoDocument;

/**
 * Defines DB access methods that a CreditMemoDaoImpl must implement.
 */
public interface CreditMemoDao {
    /**
     * Get all the credit memos that need to be extracted
     * 
     * @param chartCode if not null, limit results to a single chart
     * @return Iterator of credit memos
     */
    public Iterator<CreditMemoDocument> getCreditMemosToExtract(String chartCode);

    /**
     * This method tests for a duplicate entry of a credit memo by the combination of vendorNumber and creditMemoNumber. This method
     * accepts the two values as arguments, and returns a boolean, describing whether a duplicate exists in the system or not.
     * 
     * @param vendorNumber - the composite two-part vendorNumber (headerId-detailId)
     * @param creditMemoNumber - the vendor-supplied creditMemoNumber
     * @return boolean - true if a match exists in the db, false if not
     */
    public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId, String creditMemoNumber);

    /**
     * This method tests for a duplicate entry of a credit memo by the combination of vendorNumber, date and amount. This method
     * accepts the values as arguments, and returns a boolean, describing whether a duplicate exists in the system or not.
     * 
     * @param vendorNumber - the composite two-part vendorNumber (headerId-detailId)
     * @param creditMemoNumber - the vendor-supplied creditMemoNumber
     * @return boolean - true if a match exists in the db, false if not
     */
    public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId, Date date, KualiDecimal amount);
    
    /** 
     * This method returns a credit memo document number by id.
     * 
     * @param id
     * @return
     */
    public String getDocumentNumberByCreditMemoId(Integer id);
}
