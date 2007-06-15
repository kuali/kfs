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
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.CreditMemoStatusHistory;
import org.kuali.module.purap.dao.CreditMemoDao;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;


public class CreditMemoDaoOjb extends PlatformAwareDaoBaseOjb implements CreditMemoDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoDaoOjb.class);
/*
    private UserService userService;

    public void setUserService(UserService us) {
      userService = us;
    }
*/
    public void save(CreditMemoDocument cmDocument) {
        getPersistenceBrokerTemplate().store(cmDocument);
    }
    public CreditMemoDaoOjb() {
      super();
    }

    public CreditMemoDocument getCreditMemoById(Integer id) {
      LOG.debug("getCreditMemoById() started");

      Criteria criteria = new Criteria();
      criteria.addEqualTo("purapDocumentIdentifier",id);

      CreditMemoDocument cm = (CreditMemoDocument)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(CreditMemoDocument.class,criteria));
      if ( cm != null ) {
     ///   cm.updateUser(userService);
      }
      return cm;
    }
/*
    public CreditMemoDocument save(CreditMemoDocument cm,User user,boolean retrieveAllReferences) {
      LOG.debug("save() started");

      Date now = new Date( (new Date()).getTime() );

      cm.setLastUpdateDate(now);
      cm.setLastUpdateUser(user);

      // Set these on all the child objects too
      for (Iterator iter = cm.getItems().iterator(); iter.hasNext();) {
        CreditMemoItem item = (CreditMemoItem)iter.next();
        item.setLastUpdateDate(now);
        item.setLastUpdateUser(user);

        for (Iterator iterator = item.getAccounts().iterator(); iterator.hasNext();) {
          CreditMemoAccount account = (CreditMemoAccount)iterator.next();
          account.setLastUpdateDate(now);
          account.setLastUpdateUser(user);
        }
      }

      getPersistenceBrokerTemplate().store(cm);
      if (retrieveAllReferences) {
        getPersistenceBroker(true).retrieveAllReferences(cm);
        //loop through the items to retrieveAllReferences for accounts in each item
        Collection items = cm.getItems();
        for (Iterator itemsIt = items.iterator(); itemsIt.hasNext();) {
          CreditMemoItem item = (CreditMemoItem)itemsIt.next();
          getPersistenceBroker(true).retrieveAllReferences(item);
          Collection accounts = item.getAccounts();
          for (Iterator accountsIt = accounts.iterator(); accountsIt.hasNext();){
            CreditMemoAccount cma = (CreditMemoAccount)accountsIt.next();
            getPersistenceBroker(true).retrieveAllReferences(cma);
          }
        }
      }
      return cm;
    }
   */ 
    /**
     * 
     * @see edu.iu.uis.pur.cm.dao.CreditMemoDao#duplicateExists(java.lang.String, java.lang.String)
     */
  
    public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId, 
                                        String creditMemoNumber) {
        LOG.debug("duplicateExists() started");

        //  criteria:  vendorNumberHeader AND vendorNumberDetail AND creditMemoNumber
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorNumberHeaderId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorNumberDetailId);
        criteria.addEqualTo("creditMemoNumber", creditMemoNumber);
        
        //  use the criteria to do a Count against the DB, and return the resulting 
        // number.  Any positive non-zero result means that a potential duplicate 
        // exists and we return true, otherwise, return false.
        int cmCount = getPersistenceBrokerTemplate().getCount(new QueryByCriteria(CreditMemoDocument.class, criteria));
        if (cmCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @see edu.iu.uis.pur.cm.dao.CreditMemoDao#duplicateExists(java.lang.String, java.lang.String)
     */
 
    public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId, 
                                        Date date, KualiDecimal amount) {
        LOG.debug("duplicateExists() started");

        //  criteria:  vendorNumberHeader AND vendorNumberDetail AND date AND amount
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorNumberHeaderId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorNumberDetailId);
        criteria.addEqualTo("creditMemoDate", date);
        criteria.addEqualTo("creditMemoAmount", amount);
        
        //  use the criteria to do a Count against the DB, and return the resulting 
        // number.  Any positive non-zero result means that a potential duplicate 
        // exists and we return true, otherwise, return false.
        int cmCount = getPersistenceBrokerTemplate().getCount(new QueryByCriteria(CreditMemoDocument.class, criteria));
        if (cmCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    private List getCreditMemosByQueryByCriteria(QueryByCriteria qbc) {
      List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
     // updateUser(l);
      return l;
    }
  
    /**
     * 
     * @see edu.iu.uis.pur.cm.dao.CreditMemoDao#getCreditMemosByRequisitionId(java.lang.Integer)
     */
 
    public List getCreditMemosByRequisitionId(Integer requisitionID) {
      LOG.debug("getCreditMemosByRequisitionId() started");

      Criteria criteria = new Criteria();
      Criteria orCriteria = new Criteria();
      criteria.addEqualTo("purchaseOrder.requisitionId", requisitionID);
      orCriteria.addEqualTo("paymentRequest.requisitionNumber", requisitionID);
      criteria.addOrCriteria(orCriteria);
      QueryByCriteria qbc = new QueryByCriteria(CreditMemoDocument.class,criteria);
      qbc.addOrderByDescending("id");
      qbc.addOrderBy("purchaseOrder.id",true);
      qbc.addOrderBy("paymentRequest.id",true);
      qbc.setDistinct(true);
      return this.getCreditMemosByQueryByCriteria(qbc);
    }
   
    /**
     * Retreives a list of Credit Memos with the given PO Id.
     * 
     * @param poID
     * @return List of Credit Memos.
     */
   
    public List getCreditMemosByPOId(Integer poID) {
      LOG.debug("getCreditMemosByPOId() started");

      Criteria criteria = new Criteria();
      Criteria orCriteria = new Criteria();
      criteria.addEqualTo("purchaseOrder.id", poID);
      orCriteria.addEqualTo("paymentRequest.purchaseOrder.id", poID);
      criteria.addOrCriteria(orCriteria);
      QueryByCriteria qbc = new QueryByCriteria(CreditMemoDocument.class,criteria);
      qbc.addOrderByDescending("id");
      qbc.addOrderBy("purchaseOrder.id",true);
      qbc.addOrderBy("paymentRequest.id",true);
      qbc.setDistinct(true);
      
      return this.getCreditMemosByQueryByCriteria(qbc);
    }
    
    /**
     * Retreives a list of Credit Memos with the given PO Id.
     * 
     * @param poID
     * @return List of Credit Memos.
     */
 
    public List getCreditMemosByPOId(Integer poID, Integer returnListMax) {
      LOG.debug("getCreditMemosByPOId(Integer) started");

      Criteria criteria = new Criteria();
      Criteria orCriteria = new Criteria();
      criteria.addEqualTo("purchaseOrder.id", poID);
      orCriteria.addEqualTo("paymentRequest.purchaseOrder.id", poID);
      criteria.addOrCriteria(orCriteria);
      QueryByCriteria qbc = new QueryByCriteria(CreditMemoDocument.class,criteria);
      qbc.setEndAtIndex(returnListMax.intValue());
      qbc.addOrderByDescending("id");
      qbc.addOrderBy("purchaseOrder.id",true);
      qbc.addOrderBy("paymentRequest.id",true);
      qbc.setDistinct(true);
      
      return this.getCreditMemosByQueryByCriteria(qbc);
    }

    public List getAllCMsByPOIdAndStatus(Integer purchaseOrderID, Collection statusCodes) {
      LOG.debug("getAllCMsByPOIdAndStatus() started");
      Criteria criteria = new Criteria();
      criteria.addEqualTo("purchaseOrder.id", purchaseOrderID);
      criteria.addIn("status.code",statusCodes);
      QueryByCriteria qbc = new QueryByCriteria(CreditMemoDocument.class,criteria);
      return this.getCreditMemosByQueryByCriteria(qbc);
    }


    public CreditMemoStatusHistory saveCreditMemoStatusHistory(CreditMemoStatusHistory cmsh) {
      LOG.debug("saveCreditMemoStatusHistory() ");
      getPersistenceBrokerTemplate().store(cmsh);
      return cmsh;
    }

    /**
     * Get all the credit memos for a set of statuses
     */
  
    public Collection getByStatuses(String statuses[]) {
      LOG.debug("getByStatuses() started");

      Collection stati = new ArrayList();
      Criteria criteria = new Criteria();
      for (int i = 0; i < statuses.length; i++) {
        stati.add(statuses[i]);
      }
      criteria.addIn("statusCode",stati);

      QueryByCriteria qbc = new QueryByCriteria(CreditMemoDocument.class,criteria);
      return this.getCreditMemosByQueryByCriteria(qbc);
    }
  /*      
    private void updateUser(Collection l) {
      for (Iterator iter = l.iterator(); iter.hasNext();) {
        ((CreditMemo) iter.next()).updateUser(userService);
      }    
    }
*/
    /* (non-Javadoc)
     * @see edu.iu.uis.pur.cm.dao.CreditMemoDao#getCreditMemoByDocId(java.lang.Long)
     */
 /*   public CreditMemo getCreditMemoByDocId(Long docId) {
        LOG.debug("getCreditMemoByDocId() started");
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentHeaderId", docId);
        
        CreditMemo cm  = (CreditMemo) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(CreditMemoDocument.class, criteria));
        if (cm!=null) {
            cm.updateUser(userService);
        }
        return cm;
    }
  */    
}
