/*
 * Copyright 2009 The Kuali Foundation.
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
