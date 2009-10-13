/*
 * Copyright 2008 The Kuali Foundation
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
