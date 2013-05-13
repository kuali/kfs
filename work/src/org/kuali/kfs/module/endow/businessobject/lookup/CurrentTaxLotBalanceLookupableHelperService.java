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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class CurrentTaxLotBalanceLookupableHelperService extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {

        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        CurrentTaxLotBalance currentTaxLotBalance = (CurrentTaxLotBalance) businessObject;
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        Map primaryKeys = new HashMap();
        primaryKeys.put(EndowPropertyConstants.KEMID, currentTaxLotBalance.getKemid());
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, currentTaxLotBalance.getSecurityId());
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, currentTaxLotBalance.getRegistrationCode());
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_NUMBER, currentTaxLotBalance.getLotNumber());
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, currentTaxLotBalance.getIncomePrincipalIndicator());

        HoldingTaxLot holdingTaxLot = (HoldingTaxLot) businessObjectService.findByPrimaryKey(HoldingTaxLot.class, primaryKeys);

        if (ObjectUtils.isNotNull(holdingTaxLot)) {
            anchorHtmlDataList.add(getUrlData(holdingTaxLot, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        return anchorHtmlDataList;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {

        return false;
    }

}
