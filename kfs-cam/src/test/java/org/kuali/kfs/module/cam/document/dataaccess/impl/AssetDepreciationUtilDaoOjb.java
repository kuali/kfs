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
package org.kuali.kfs.module.cam.document.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.dataaccess.AssetDepreciationUtilDao;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AssetDepreciationUtilDaoOjb extends PlatformAwareDaoBaseOjb implements AssetDepreciationUtilDao {

    private BusinessObjectService businessObjectService;    

    public String getMaxDocumentNumber() {
        Criteria criteria = new Criteria();
        ReportQueryByCriteria query = QueryFactory.newReportQuery(FinancialSystemDocumentHeader.class, new Criteria());

        query.setAttributes(new String[] { "max(" + KRADPropertyConstants.DOCUMENT_NUMBER + ")" });
        Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        String maxDocumentNumber = "";
        if (iterator.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
            if (data[0] != null) {
                maxDocumentNumber= (String) data[0];
            }
        }
        return maxDocumentNumber;    
    }        


    //    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries(String documentNumber) {
    //        Map<String, String> fieldValues = new HashMap<String, String>();
    //        fieldValues.put(KRADPropertyConstants.DOCUMENT_NUMBER, documentNumber);
    //        return (List<GeneralLedgerPendingEntry>) businessObjectService.findMatching(GeneralLedgerPendingEntry.class, fieldValues);
    //    }   


    public Collection<AssetPayment> getAssetPayments(List<Asset> assets) {
        List<Long> capitalAssetNumbers = new ArrayList<Long>();

        for(Asset asset : assets) {
            capitalAssetNumbers.add(asset.getCapitalAssetNumber());
        }

        Criteria criteria = new Criteria();
        criteria.addIn(CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER, capitalAssetNumbers);

        QueryByCriteria q = QueryFactory.newQuery(AssetPayment.class, criteria);
        q.addOrderByAscending(CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER);
        q.addOrderByAscending(CamsPropertyConstants.AssetPayment.PAYMENT_SEQ_NUMBER);
        Collection<AssetPayment> assetPayments = getPersistenceBrokerTemplate().getCollectionByQuery(q);
        return assetPayments;
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.dataaccess.AssetDepreciationUtilDao#deleteAssetPayment()
     */
    public void deleteAssetPayment(List<Asset> assets) {
        List<Long> capitalAssetNumbers = new ArrayList<Long>();
        for(Asset asset : assets) {
            capitalAssetNumbers.add(asset.getCapitalAssetNumber());
        }        

        Criteria criteria = new Criteria();
        QueryByCriteria q = QueryFactory.newQuery(AssetPayment.class, criteria);
        getPersistenceBrokerTemplate().deleteByQuery(q);
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.dataaccess.AssetDepreciationUtilDao#deleteAssets(java.util.List)
     */
    public void deleteAssets(List<Asset> assets) {
        List<Long> capitalAssetNumbers = new ArrayList<Long>();
        for(Asset asset : assets) {
            capitalAssetNumbers.add(asset.getCapitalAssetNumber());
        }        
        Criteria criteria = new Criteria();
        criteria.addIn(CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER, capitalAssetNumbers);
        QueryByCriteria q = QueryFactory.newQuery(Asset.class, criteria);
        getPersistenceBrokerTemplate().deleteByQuery(q);
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.dataaccess.AssetDepreciationUtilDao#deleteGLPEs()
     */
    public void deleteGLPEs() {
        QueryByCriteria q = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, new Criteria());
        getPersistenceBrokerTemplate().deleteByQuery(q);
    }
}
