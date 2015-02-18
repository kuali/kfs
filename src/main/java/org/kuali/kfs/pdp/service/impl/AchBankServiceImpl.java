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
package org.kuali.kfs.pdp.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.ACHBank;
import org.kuali.kfs.pdp.service.AchBankService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AchBankServiceImpl implements AchBankService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchBankServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.pdp.service.AchBankService#getByPrimaryId(java.lang.String)
     */
    public ACHBank getByPrimaryId(String bankRoutingNumber) {
        Map<String, String> fieldKeys = new HashMap<String, String>();
        fieldKeys.put(PdpPropertyConstants.BANK_ROUTING_NUMBER, bankRoutingNumber);

        return (ACHBank) businessObjectService.findByPrimaryKey(ACHBank.class, fieldKeys);
    }

    /**
     * @see org.kuali.kfs.pdp.service.AchBankService#reloadTable(java.lang.String)
     */
    public boolean reloadTable(String filename) {
        LOG.debug("reloadTable() started");
        
        // clear out previous records
        businessObjectService.deleteMatching(ACHBank.class, new HashMap());

        BufferedReader inputStream = null;
        try {
            inputStream = new BufferedReader(new FileReader(filename));

            String str = null;
            while ((str = inputStream.readLine()) != null) {
                if (StringUtils.isNotBlank(str)) {
                    ACHBank ab = new ACHBank(str);
                    this.businessObjectService.save(ab);
                }
            }
        }
        catch (FileNotFoundException fnfe) {
            LOG.error("reloadTable() File Not Found: " + filename, fnfe);
            return false;
        }
        catch (IOException ie) {
            LOG.error("reloadTable() Problem reading file:  " + filename, ie);
            return false;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ie) {
                    // Not much we can do now
                }
            }
        }
        
        return true;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
