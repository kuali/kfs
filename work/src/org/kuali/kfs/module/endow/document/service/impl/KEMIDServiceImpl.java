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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class KEMIDServiceImpl implements KEMIDService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMIDService#getByPrimaryKey(java.lang.String)
     */
    public KEMID getByPrimaryKey(String kemid) {
        KEMID theKemidObj = null;
        
        if (StringUtils.isNotBlank(kemid)) {
            Map criteria = new HashMap();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(KEMID.class, EndowPropertyConstants.KEMID)) {
                kemid = kemid.toUpperCase();
            }
            
            criteria.put(EndowPropertyConstants.KEMID, kemid);
            theKemidObj = (KEMID) businessObjectService.findByPrimaryKey(KEMID.class, criteria);
        }
        
        return theKemidObj;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMIDService#isTrueEndowment(java.lang.String)
     */
    public boolean isTrueEndowment(String kemid){
        boolean isTrueEndowment = false;
        KEMID theKemidObj = getByPrimaryKey(kemid);
        if (theKemidObj.getTypeRestrictionCodeForPrincipalRestrictionCode().getPermanentIndicator()){
            isTrueEndowment = true;
        }
        return isTrueEndowment;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMIDService#getByCashSweepId(java.lang.Integer)
     */
    public Collection<KEMID> getByCashSweepId(Integer cashSweepId) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(EndowPropertyConstants.KEMID_CASH_SWEEP_MDL_ID, cashSweepId.toString());
        
        return (Collection<KEMID>)businessObjectService.findMatching(KEMID.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMIDService#getByPrincipleAciId(java.lang.Integer)
     */
    public Collection<KEMID> getByPrincipleAciId(Integer aciPrincipleId) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(EndowPropertyConstants.TYPE_PRINCIPAL_ACI_MODEL_ID, aciPrincipleId.toString());
        
        return (Collection<KEMID>)businessObjectService.findMatching(KEMID.class, fieldValues);
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMIDService#getByIncomeAciId(java.lang.Integer)
     */
    public Collection<KEMID> getByIncomeAciId(Integer aciIncomeId) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(EndowPropertyConstants.TYPE_INCOME_ACI_MODEL_ID, aciIncomeId.toString());
        
        return (Collection<KEMID>)businessObjectService.findMatching(KEMID.class, fieldValues);
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService#getAllKemIdWithClosedIndicatorNo()
     * Retrieves all kemId records where closed indicator = 'N'
     */
    public Collection<KEMID> getAllKemIdWithClosedIndicatorNo() {
        Map fieldValues = new HashMap();
        fieldValues.put(EndowPropertyConstants.KEMID_CLOSED, EndowConstants.NO);
        
        Collection<KEMID> kemIdRecords = businessObjectService.findMatching(KEMID.class, fieldValues);
        
        return kemIdRecords;
    }
    
    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
