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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoicingDao;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class ElectronicInvoicingDaoOjb extends PlatformAwareDaoBaseOjb implements ElectronicInvoicingDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoicingDaoOjb.class);

    public ElectronicInvoicingDaoOjb() {
        super();
    }

    @Override
    public ElectronicInvoiceLoadSummary getElectronicInvoiceLoadSummary(Integer loadId, String vendorDunsNumber) {
        LOG.debug("getElectronicInvoiceLoadSummary() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("id", loadId);
        criteria.addEqualTo("vendorDunsNumber", vendorDunsNumber);

        return (ElectronicInvoiceLoadSummary) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ElectronicInvoiceLoadSummary.class, criteria));
    }

    @Override
    public List getPendingElectronicInvoices() {
        LOG.debug("getPendingElectronicInvoices() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("status.code", PurapConstants.PaymentRequestStatuses.APPDOC_PENDING_E_INVOICE);
        criteria.addEqualTo("isElectronicInvoice", Boolean.TRUE);
        List invoices = (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentRequestDocument.class, criteria));
        for (Iterator iter = invoices.iterator(); iter.hasNext();) {
            PaymentRequestDocument p = (PaymentRequestDocument) iter.next();
        }

        return invoices;
    }

    @Override
    public Map getDefaultItemMappingMap() {
        LOG.debug("getDefaultItemMappingMap() started");
        Criteria criteria = new Criteria();
        criteria.addIsNull("vendorHeaderGeneratedIdentifier");
        criteria.addIsNull("vendorDetailAssignedIdentifier");
        criteria.addEqualTo("active", true);
        return this.getItemMappingMap(criteria);
    }

    @Override
    public Map getItemMappingMap(Integer vendorHeaderId, Integer vendorDetailId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getItemMappingMap() started for vendor id " + vendorHeaderId + "-" + vendorDetailId);
        }
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorHeaderId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorDetailId);
        criteria.addEqualTo("active", true);
        return this.getItemMappingMap(criteria);
    }

    protected Map getItemMappingMap(Criteria criteria) {
        Map hm = new HashMap();
        List itemMappings = (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ElectronicInvoiceItemMapping.class, criteria));

        for (Iterator iter = itemMappings.iterator(); iter.hasNext();) {
            ElectronicInvoiceItemMapping mapping = (ElectronicInvoiceItemMapping) iter.next();
            hm.put(mapping.getInvoiceItemTypeCode(), mapping);
        }
        return hm;
    }
}
