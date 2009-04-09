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
   * Save an ElectronicInvoiceItemMapping.
   * 
   * @param row  ElectronicInvoiceItemMapping to save
   */
  public void save(ElectronicInvoiceItemMapping row) {
    LOG.debug("save() started");
    getPersistenceBrokerTemplate().store(row);
  }

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
 * Copyright (c) 2004, 2005 The National Association of College and University
 * Business Officers, Cornell University, Trustees of Indiana University,
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you
 * have read, understand, and will comply with the terms and conditions of the
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */