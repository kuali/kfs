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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.batch.service.RollProcessDateService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RollProcessDateServiceImpl implements RollProcessDateService {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RollProcessDateServiceImpl.class);
    protected ParameterService parameterService;
    protected BusinessObjectService businessObjectService;
    protected KEMService kemService;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.RollProcessDateService#rollDate()
     */
    @Override
    public boolean rollDate() {
        int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

        boolean success = true;
        Date currentProcessDate = kemService.getCurrentDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String nextDate = dateFormat.format(currentProcessDate.getTime() + MILLIS_IN_DAY);
        // FIXME: this must use the parameter service
        Parameter theCurrentProcessDate = parameterService.getParameter("KFS-ENDOW", "All", EndowParameterKeyConstants.CURRENT_PROCESS_DATE);
        Parameter.Builder b = Parameter.Builder.create(theCurrentProcessDate);
        b.setValue(nextDate);
        parameterService.updateParameter(b.build());
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Roll the value of CURRENT_PROCESS_DATE to " + theCurrentProcessDate.getValue());
        }
        return success;
    }

    /**
     * Sets the kemService.
     *
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }


    /**
     * Sets the parameterService.
     *
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the businessObjectService.
     *
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
