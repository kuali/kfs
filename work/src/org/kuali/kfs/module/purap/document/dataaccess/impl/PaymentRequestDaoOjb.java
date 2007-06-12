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
package org.kuali.module.purap.dao.ojb;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.dao.PaymentRequestDao;
import org.kuali.module.purap.document.PaymentRequestDocument;

public class PaymentRequestDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentRequestDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestDaoOjb.class);

    /**
     * 
     * @param paymentRequestDocument - a populated REQUISITION object to be saved
     * @throws IllegalObjectStateException
     * @throws ValidationErrorList
     */
    public void save(PaymentRequestDocument paymentRequestDocument) {
        getPersistenceBrokerTemplate().store(paymentRequestDocument);
    }

    public PaymentRequestDocument getPaymentRequestById(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);

        PaymentRequestDocument pReq = (PaymentRequestDocument) getPersistenceBrokerTemplate().getObjectByQuery(
            new QueryByCriteria(PaymentRequestDocument.class, criteria));
        if (pReq != null) {
            pReq.refreshAllReferences();
        }
        return pReq;
      }
    
    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo( PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poDocId );

        List<PaymentRequestDocument> pReqs = (List<PaymentRequestDocument>)this.getPersistenceBrokerTemplate().getCollectionByQuery(
                new QueryByCriteria(PaymentRequestDocument.class, criteria));
 
        return pReqs; 
    }
    
    private PaymentRequestDocument getPaymentRequestByCriteria(Criteria criteria) {
        LOG.debug("getPaymentRequestByCriteria() started");
        PaymentRequestDocument p = (PaymentRequestDocument) getPersistenceBrokerTemplate().getObjectByQuery(
            new QueryByCriteria(PaymentRequestDocument.class, criteria));
        if (p != null) {
  //        updateUser(p);
        }
        return p;
      }
      
    /**
     * Get a payment request by id
     * 
     * @param id PaymentRequest Id
     * @return PaymentRequest or null if not found
     */
 /*  
    public PaymentRequestDocument getPaymentRequestById(Integer id) {
        LOG.debug("getPaymentRequestById() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("purapDocumentIdentifier", id);
        return this.getPaymentRequestByCriteria(criteria);
      }
*/
      /**
       * Get a payment request by Workflow id
       * 
       * @param id workflow document Id
       * @return PaymentRequest or null if not found
       */
      public PaymentRequestDocument getPaymentRequestByDocId(Long id) {
        LOG.debug("getPaymentRequestByDocId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentHeaderId", id);
        return this.getPaymentRequestByCriteria(criteria);
      }

      /**
       * Save a pay req.  This saves it to the
       * tables.  
       * 
       * @param pr PaymentRequest to save
       */
      public PaymentRequestDocument savePaymentRequestRetriveReferences(PaymentRequestDocument pr) {
        LOG.debug("savePaymentRequestRetriveReferences() started");

        getPersistenceBrokerTemplate().store(pr);
//        getPersistenceBroker(true).commitTransaction();
        getPersistenceBroker(true).retrieveAllReferences(pr);

        //loop through the items to retrieveAllReferences for accounts in each item
        Collection items = pr.getItems();
        /*
        for (Iterator itemsIt = items.iterator(); itemsIt.hasNext();) {
          PaymentRequestItem item = (PaymentRequestItem)itemsIt.next();
          getPersistenceBroker(true).retrieveAllReferences(item);
          Collection accounts = item.getAccounts();
          for (Iterator accountsIt = accounts.iterator(); accountsIt.hasNext();){
            PaymentRequestAccount pra = (PaymentRequestAccount)accountsIt.next();
            getPersistenceBroker(true).retrieveAllReferences(pra);
          }
        }
        */

        return pr;
      }

      /**
       * Save a pay req.  This saves it to the
       * tables.  
       * 
       * @param pr PaymentRequest to save
       */
      public PaymentRequestDocument savePaymentRequest(PaymentRequestDocument pr) {
        LOG.debug("savePaymentRequest() started");

        getPersistenceBrokerTemplate().store(pr);
        return pr;
      }

      private List getPaymentRequestsByQueryByCriteria(QueryByCriteria qbc) {
        LOG.debug("getPaymentRequestsByQueryByCriteria() started");
        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        updateUser(l);
        return l;
      }
      
      /**
       * Retreives a list of Pay Reqs with the given Req Id.
       * 
       * @param id
       * @return List of Pay Reqs.
       */
      public List getPaymentRequestsByRequisitionId(Integer reqId) {
        LOG.debug("getPaymentRequestsByRequisitionId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("requisitionNumber", reqId);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class,criteria);
        qbc.addOrderByDescending("id");
        qbc.addOrderBy("purchaseOrder.id",true);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
      }

      /**
       * Retreives a list of Pay Reqs with the given PO Id.
       * 
       * @param id
       * @return List of Pay Reqs.
       */
      public List getPaymentRequestsByPOId(Integer poId) {
        LOG.debug("getPaymentRequestsByPOId() started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class,criteria);
        qbc.addOrderByDescending("id");
        return this.getPaymentRequestsByQueryByCriteria(qbc);
      }
      
      /**
       * Retreives a list of Pay Reqs with the given PO Id.
       * 
       * @param id
       * @return List of Pay Reqs.
       */
      public List getPaymentRequestsByPOId(Integer poId, Integer returnListMax) {
        LOG.debug("getPaymentRequestsByPOId(Integer) started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poId);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class,criteria);
        qbc.setEndAtIndex(returnListMax.intValue());
        qbc.addOrderByDescending("id");
        return this.getPaymentRequestsByQueryByCriteria(qbc);
      }
      
      /**
       * Retreives a list of Pay Reqs with the given vendor id and invoice number.
       * 
       * @param vendorHeaderGeneratedId  header id of the vendor id
       * @param vendorDetailAssignedId   detail id of the vendor id
       * @param invoiceNumber            invoice number as entered by AP
       * @return List of Pay Reqs.
       */
      public List getActivePaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId,String invoiceNumber) {
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorHeaderGeneratedId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorDetailAssignedId);
        criteria.addEqualTo("invoiceNumber", invoiceNumber);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class,criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
      }
      
      /**
       * Retreives a list of Pay Reqs with the given PO Id, invoice amount, and invoice date.
       * 
       * @param poId           purchase order ID
       * @param invoiceAmount  amount of the invoice as entered by AP
       * @param invoiceDate    date of the invoice as entered by AP
       * @return List of Pay Reqs.
       */
      public List getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal vendorInvoiceAmount, Date invoiceDate) {
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("purchaseOrderIdentifier", poId);
        criteria.addEqualTo("vendorInvoiceAmount", vendorInvoiceAmount);
        criteria.addEqualTo("invoiceDate", invoiceDate);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class,criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
      }
      
      public List getAllPREQsByPOIdAndStatus(Integer purchaseOrderID,Collection statusCodes) {
        LOG.debug("getAllPREQsByPOIdAndStatus() started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("purchaseOrder.id",purchaseOrderID);
        criteria.addIn("status.code", statusCodes);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class,criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
      }

      private void updateUser(Collection l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
          updateUser((PaymentRequestDocument) iter.next());
        }    
      }

      private void updateUser(PaymentRequestDocument p) {
       /*
          UserRequired ur = (UserRequired) p;
        ur.updateUser(userService);
        */
      }

      /**
       * Get all the payment requests for a set of statuses
       */
      public Collection getByStatuses(String statuses[]) {
        LOG.debug("getByStatuses() started");

        Collection stati = new ArrayList();
        Criteria criteria = new Criteria();
        for (int i = 0; i < statuses.length; i++) {
          stati.add(statuses[i]);
        }
        criteria.addIn("paymentRequestStatusCode",stati);

        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class,criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
      }
          
  /*     
      public PaymentRequestStatusHistory savePaymentRequestStatusHistory(PaymentRequestStatusHistory prsh) {
        LOG.debug("savePaymentRequestStatusHistory() ");
        getPersistenceBrokerTemplate().store(prsh);
        return prsh;
      }
    */  
      /* (non-Javadoc)
       * @see edu.iu.uis.pur.pmt.dao.PaymentRequestDao#getPaymentRequestStatusHistories(java.lang.Integer)
       */
 /*
      public List getPaymentRequestStatusHistories(Integer preqId) {
        LOG.debug("getPaymentRequestStatusHistories() started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("paymentRequestNumber", preqId);
        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentRequestStatusHistory.class,criteria));
        return l;
      }
      */
}
