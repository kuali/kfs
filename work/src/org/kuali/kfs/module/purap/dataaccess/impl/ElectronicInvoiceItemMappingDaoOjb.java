/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2007-2014 The Kuali Foundation
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
  @Override
public List getAll() {
    LOG.debug("getAll() started");
    QueryByCriteria qbc = new QueryByCriteria(ElectronicInvoiceItemMapping.class);
    qbc.addOrderBy("id", true);
    List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    return l;
  }

  @Override
public ElectronicInvoiceItemMapping getByUniqueKeys(Integer headerId, Integer detailId, String invoiceTypeCode) {
    LOG.debug("getByUniqueKeys() started");
    Criteria criteria = new Criteria();
    criteria.addEqualTo("vendorHeaderGeneratedId", headerId);
    criteria.addEqualTo("vendorDetailAssignedId", detailId);
    criteria.addEqualTo("electronicInvoiceItemTypeCode", invoiceTypeCode);
    QueryByCriteria qbc = new QueryByCriteria(ElectronicInvoiceItemMapping.class,criteria);
    return (ElectronicInvoiceItemMapping)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
  }

  @Override
public List getAllItemTypes() {
    LOG.debug("getAllItemTypes() started");

    Criteria criteria = new Criteria();
    criteria.addEqualTo("active",Boolean.TRUE);

    QueryByCriteria qbc = new QueryByCriteria(ItemType.class,criteria);
    qbc.addOrderByAscending("code");

    return (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
  }

  @Override
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
  @Override
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
  @Override
public void delete(ElectronicInvoiceItemMapping row) {
    LOG.debug("delete() started");
    getPersistenceBrokerTemplate().delete(row);
  }

}