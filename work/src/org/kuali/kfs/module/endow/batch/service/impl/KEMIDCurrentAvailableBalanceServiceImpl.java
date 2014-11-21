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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.util.Collection;

import org.kuali.kfs.module.endow.batch.service.KEMIDCurrentAvailableBalanceService;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentAvailableBalance;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class implements the AvailableCashService.
 */
public class KEMIDCurrentAvailableBalanceServiceImpl implements KEMIDCurrentAvailableBalanceService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KEMIDCurrentAvailableBalanceServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService#clearAvailableCash()
     * Method to clear all the records in the kemidCurrentAvailableBalance table
     */
    public void clearAllAvailableCash() {
        LOG.info("Step1: Clearing all available cash records");
        
        Collection<KEMIDCurrentAvailableBalance> KEMIDCurrentAvailableBalances = businessObjectService.findAll(KEMIDCurrentAvailableBalance.class);

        for (KEMIDCurrentAvailableBalance kemidCurrentAvailableBalance : KEMIDCurrentAvailableBalances) {
            businessObjectService.delete(kemidCurrentAvailableBalance);
        }
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService#InsertAvailableCash(KemidCurrentCash)
     * Method to clear all the records in the kemidCurrentAvailableBalance table
     */
    public void InsertAvailableCash(KEMIDCurrentAvailableBalance kemidCurrentAvailableBalance) {
        if (kemidCurrentAvailableBalance == null) {
            throw new IllegalArgumentException("invalid (null) kemidCurrentAvailableBalance");
        }
        
        businessObjectService.save(kemidCurrentAvailableBalance);
    } 
    
    /**
     * Sets the BusinessObjectService
     * 
     * @param businessObjectService The BusinessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
