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
package org.kuali.kfs.module.cab.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.module.cab.dataaccess.PurchasingAccountsPayableReportDao;
import org.kuali.kfs.module.cab.service.PurchasingAccountsPayableReportService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PurchasingAccountsPayableReportServiceImpl implements PurchasingAccountsPayableReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingAccountsPayableReportServiceImpl.class);
    protected PurchasingAccountsPayableReportDao purApReportDao;

    /**
     * @see org.kuali.kfs.module.cab.service.PurchasingAccountsPayableReportService#findGeneralLedgers(java.util.Map)
     */
    public Iterator findGeneralLedgers(Map<String, String> fieldValues) {
        LOG.debug("findGeneralLedgers() started");

        return purApReportDao.findGeneralLedgers(fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.cab.service.PurchasingAccountsPayableReportService#findPurchasingAccountsPayableDocuments(java.util.Map)
     */
    public Collection findPurchasingAccountsPayableDocuments(Map<String, String> fieldValues) {
        LOG.debug("findPurchasingAccountsPayableDocuments() started");

        return purApReportDao.findPurchasingAccountsPayableDocuments(fieldValues);
    }

    /**
     * Gets the purApReportDao attribute.
     * 
     * @return Returns the purApReportDao.
     */
    public PurchasingAccountsPayableReportDao getPurApReportDao() {
        return purApReportDao;
    }

    /**
     * Sets the purApReportDao attribute value.
     * 
     * @param purApReportDao The purApReportDao to set.
     */
    public void setPurApReportDao(PurchasingAccountsPayableReportDao purchasingAccountsPayableReportDao) {
        this.purApReportDao = purchasingAccountsPayableReportDao;
    }


}
