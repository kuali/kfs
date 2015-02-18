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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.businessobject.ReceivingAddress;
import org.kuali.kfs.module.purap.document.dataaccess.ReceivingAddressDao;
import org.kuali.kfs.module.purap.document.service.ReceivingAddressService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ReceivingAddressServiceImpl implements ReceivingAddressService {
    private static Logger LOG = Logger.getLogger(ReceivingAddressServiceImpl.class);

    private ReceivingAddressDao dao;

    public void setReceivingAddressDao(ReceivingAddressDao dao) {
        this.dao = dao;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.ReceivingAddressService#findActiveByChartOrg(java.lang.String,java.lang.String)
     */
    public Collection<ReceivingAddress> findActiveByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering findActiveByChartOrg(String,String)");
        LOG.debug("Leaving findActiveByChartOrg(String,String)");
        return dao.findActiveByChartOrg(chartCode,orgCode);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.ReceivingAddressService#findDefaultByChartOrg(java.lang.String,java.lang.String)
     */
    public Collection<ReceivingAddress> findDefaultByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering findDefaultByChartOrg(String,String)");
        LOG.debug("Leaving findDefaultByChartOrg(String,String)");
        return dao.findDefaultByChartOrg(chartCode,orgCode);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.ReceivingAddressService#findUniqueDefaultByChartOrg(java.lang.String,java.lang.String)
     */
    public ReceivingAddress findUniqueDefaultByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering findUniqueDefaultByChartOrg(String,String)");
        Collection<ReceivingAddress> addresses  = findDefaultByChartOrg(chartCode,orgCode);      
        if (addresses != null ) {
            Iterator iter = addresses.iterator();
            if (iter.hasNext()) {
                LOG.debug("Leaving findUniqueDefaultByChartOrg(String,String)");       
                return (ReceivingAddress)iter.next();
            }
        }
        LOG.debug("Leaving findUniqueDefaultByChartOrg(String,String)");       
        return null;
        //TODO what if more than one is found? throw an exception
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.ReceivingAddressService#countActiveByChartOrg(java.lang.String,java.lang.String)
     */
    public int countActiveByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering countActiveByChartOrg(String,String)");
        LOG.debug("Leaving countActiveByChartOrg(String,String)");
        return dao.countActiveByChartOrg(chartCode,orgCode);
    }

}
