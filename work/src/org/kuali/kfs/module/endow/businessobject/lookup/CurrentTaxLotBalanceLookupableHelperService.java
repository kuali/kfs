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
