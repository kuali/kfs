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
import java.util.Collection;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.CreditMemoStatusHistory;
import org.kuali.module.purap.document.CreditMemoDocument;

public interface CreditMemoDao {
        public static boolean RETRIEVE_ALL_REFERENCES_YES = true;
        public static boolean RETRIEVE_ALL_REFERENCES_NO = false;
        
        public CreditMemoDocument getCreditMemoById(Integer id);
        public void save(CreditMemoDocument cmDocument);
     ///   public CreditMemoDocument save(CreditMemoDocument cm,User user,boolean retrieveAllReferences);
        
        /**
         * 
         * This method tests for a duplicate entry of a credit memo by the combination 
         * of vendorNumber and creditMemoNumber.
         * 
         * This method accepts the two values as arguments, and returns a boolean, describing 
         * whether a duplicate exists in the system or not.
         * 
         * @param vendorNumber - the composite two-part vendorNumber (headerId-detailId)
         * @param creditMemoNumber - the vendor-supplied creditMemoNumber
         * @return boolean - true if a match exists in the db, false if not
         */
        public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId, 
                                          String creditMemoNumber);
        
        /**
         * 
         * This method tests for a duplicate entry of a credit memo by the combination 
         * of vendorNumber, date and amount.
         * 
         * This method accepts the values as arguments, and returns a boolean, describing 
         * whether a duplicate exists in the system or not.
         * 
         * @param vendorNumber - the composite two-part vendorNumber (headerId-detailId)
         * @param creditMemoNumber - the vendor-supplied creditMemoNumber
         * @return boolean - true if a match exists in the db, false if not
         */
        public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId, 
                Date date, KualiDecimal amount);
        
        /**
         * Retreives a list of Credit Memos with the given Req Id.
         * 
         * @param requisitionID
         * @return List of Credit Memos.
         */
        public List getCreditMemosByRequisitionId(Integer requisitionID);
        
        /**
         * Retreives a list of Credit Memos with the given PO Id.
         * 
         * @param poID
         * @return List of Credit Memos.
         */
       public List getCreditMemosByPOId(Integer poID);
        
        /**
         * Retreives a list of Credit Memos with the given PO Id.
         * 
         * @param poID
         * @return List of Credit Memos.
         */
        public List getCreditMemosByPOId(Integer poID, Integer returnListMax);
        
        public List getAllCMsByPOIdAndStatus(Integer purchaseOrderID,Collection statusCodes);

       public CreditMemoStatusHistory saveCreditMemoStatusHistory(CreditMemoStatusHistory cmsh);

        /**
         * Get all the credit memos for a set of statuses
         * 
         * @param statuses
         * @return
         */
        public Collection getByStatuses(String statuses[]);
      /**
       * @param docId
       * @return
       */
 //     public CreditMemoDocument getCreditMemoByDocId(Long docId);
}      
