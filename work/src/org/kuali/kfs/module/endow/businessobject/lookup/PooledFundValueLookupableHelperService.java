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
package org.kuali.kfs.module.endow.businessobject.lookup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.document.service.PooledFundValueService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;

public class PooledFundValueLookupableHelperService extends KualiLookupableHelperServiceImpl {
    
    /**
     * Once  a new value has been established for a given Pooled Fund (more recent VAL_EFF_DT),
     * older value records cannot be modified.
     * 
     * @returns links to edit and copy maintenance action for the record with the most VAL_EFF_DT
     *          for a given Pooled Fund or links to copy maintenance action only for those records
     *          with older VAL_EFF_DT.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
               
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();     
        htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));

        PooledFundValue pooledFundValue = (PooledFundValue) businessObject;
        String pooledSecurityID = pooledFundValue.getPooledSecurityID();
        Date valueEffectiveDate = pooledFundValue.getValueEffectiveDate();
        PooledFundValueService pooledFundValueService = SpringContext.getBean(PooledFundValueService.class);
        Date theLatestEffectiveDate = pooledFundValueService.getLatestValueEffectiveDate(pooledSecurityID);
        if(valueEffectiveDate != null && valueEffectiveDate.equals(theLatestEffectiveDate)){
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
               
        return htmlDataList;        
       
    }

}
