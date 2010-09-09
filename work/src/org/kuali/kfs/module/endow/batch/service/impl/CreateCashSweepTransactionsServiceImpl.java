/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.endow.batch.service.CreateCashSweepTransactionsService;
import org.kuali.kfs.module.endow.businessobject.CashSweepModel;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;

public class CreateCashSweepTransactionsServiceImpl implements CreateCashSweepTransactionsService {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateCashSweepTransactionsServiceImpl.class);
    
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private KEMIDService kemidService;
    private KEMService kemService;
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateCashSweepTransactionsService#createCashSweepTransactions()
     */
    public boolean createCashSweepTransactions() {
        
        LOG.info("Starting \"Create Cash Sweep Transactions\" batch job...");
        List<CashSweepModel> cashSweepModels = getCashSweepModelMatchingCurrentDate();
        for (CashSweepModel cashSweepModel : cashSweepModels) {
//            processIncomeSweepPurchases(cashSweepModels);
//            processIncomeSweepSales(cashSweepModels);
//            processPrincipalSweepPurchases(cashSweepModel);
//            processPrincipalSweepSale(cashSweepModel);
        }
        
        LOG.info("Finished \"Create Cash Sweep Transactions\" batch job!");
        
        return true;
    }
    
    protected void processIncomeSweepPurchase(CashSweepModel cashSweepModel) {
        LOG.info("Entering \"processIncomeSweepPurchases\".");
        
        Collection<KEMID> kemids = kemidService.getByCashSweepId(cashSweepModel.getCashSweepModelID());
        
        LOG.info("Leaving \"processIncomeSweepPurchases\".");
    }
    
    /**
     * This method retrieves all the cash sweep models whose frequency code
     * matches the current date.
     * 
     * @return List of CashSweepModel business objects
     */
    private List<CashSweepModel> getCashSweepModelMatchingCurrentDate() {
        List<CashSweepModel> cashSweepModels = new ArrayList<CashSweepModel>();
        
        Date currentDate = kemService.getCurrentDate();
        
        return cashSweepModels;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the kemidService attribute value.
     * @param kemidService The kemidService to set.
     */
    public void setKemidService(KEMIDService kemidService) {
        this.kemidService = kemidService;
    }
    
}
