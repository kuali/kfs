/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.coa.document.validation.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Options;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRate;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;

public class IndirectCostRecoveryRateMaintainableImpl extends KualiMaintainableImpl {

    private Integer indirectCostRecoveryRateNextEntryNumber;
    
/**
 * Hook for quantity and setting asset numbers.
 * @see org.kuali.core.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
 */
    @Override
    public void addNewLineToCollection(String collectionName) {
        
        // create handle for the addline section of the doc
        IndirectCostRecoveryRateDetail addLine = (IndirectCostRecoveryRateDetail) newCollectionLines.get(collectionName);
        List<IndirectCostRecoveryRateDetail> maintCollection = (List<IndirectCostRecoveryRateDetail>) ObjectUtils.getPropertyValue(getBusinessObject(), collectionName);
        
        if(StringUtils.isBlank(addLine.getSubAccountNumber())) {
            addLine.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        if(StringUtils.isBlank(addLine.getFinancialSubObjectCode())) {
            addLine.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }

        Integer icrEntryNumberMax = 0;
        for(IndirectCostRecoveryRateDetail item : maintCollection) {
            if(icrEntryNumberMax < item.getAwardIndrCostRcvyEntryNbr()) {
                icrEntryNumberMax = item.getAwardIndrCostRcvyEntryNbr();
            }
        }
        
        addLine.setActive(true); // TODO remove after active indicator fixes
        addLine.setAwardIndrCostRcvyEntryNbr(icrEntryNumberMax + 1);
        maintCollection.add(addLine);
        initNewCollectionLine(collectionName);
    }
    
}