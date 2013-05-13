/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoiceItemMappingDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

public class ElectronicInvoiceItemMappingDaoOjb extends PersistenceBrokerDaoSupport implements
ElectronicInvoiceItemMappingDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceItemMappingDaoOjb.class);

  /**
   * Get list of all ElectronicInvoiceItemMappings
   */
  public List getAll() {
    LOG.debug("getAll() started");
    QueryByCriteria qbc = new QueryByCriteria(ElectronicInvoiceItemMapping.class);
    qbc.addOrderBy("id", true);
    List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    return l;
  }

  public ElectronicInvoiceItemMapping getByUniqueKeys(Integer headerId, Integer detailId, String invoiceTypeCode) {
    LOG.debug("getByUniqueKeys() started");
    Criteria criteria = new Criteria();
    criteria.addEqualTo("vendorHeaderGeneratedId", headerId);
    criteria.addEqualTo("vendorDetailAssignedId", detailId);
    criteria.addEqualTo("electronicInvoiceItemTypeCode", invoiceTypeCode);
    QueryByCriteria qbc = new QueryByCriteria(ElectronicInvoiceItemMapping.class,criteria);
    return (ElectronicInvoiceItemMapping)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
  }
  
  public List getAllItemTypes() {
    LOG.debug("getAllItemTypes() started");

    Criteria criteria = new Criteria();
    criteria.addEqualTo("active",Boolean.TRUE);

    QueryByCriteria qbc = new QueryByCriteria(ItemType.class,criteria);
    qbc.addOrderByAscending("code");
    
    return (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
  }
  
  public ItemType getItemTypeByCode(String code) {
  	LOG.debug("getItemTypeByCode() started");
  	Criteria criteria = new Criteria();
  	criteria.addEqualTo("code", code);
  	QueryByCriteria qbc = new QueryByCriteria(ItemType.class,criteria);
  	return (ItemType)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
  }
  /**
   * Get an ElectronicInvoiceItemMapping by primary key.
   * 
   * @param id    the id to lookup
   */
  public ElectronicInvoiceItemMapping getById(String id) {
    LOG.debug("getById() started");
    Criteria crit = new Criteria();
    crit.addEqualTo("id", id);
    ElectronicInvoiceItemMapping row = (ElectronicInvoiceItemMapping) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ElectronicInvoiceItemMapping.class, crit));
    return row;
  }

  /**
   * Delete a ElectronicInvoiceItemMapping.
   * 
   * @param row 
   */
  public void delete(ElectronicInvoiceItemMapping row) {
    LOG.debug("delete() started");
    getPersistenceBrokerTemplate().delete(row);
  }

}
/*
 * Copyright 2007 The Kuali Foundation.
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
