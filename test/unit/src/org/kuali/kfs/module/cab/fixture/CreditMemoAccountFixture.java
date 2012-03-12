/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.CreditMemoAccount;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum CreditMemoAccountFixture {
    REC1 {
        @Override
        public CreditMemoAccount newRecord() {
            CreditMemoAccount obj = new CreditMemoAccount();
            obj.setAccountIdentifier(411);
            obj.setItemIdentifier(41);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(7000));
            obj.setAccountLinePercent(new BigDecimal(100));
            return obj;
        };
    };
    public abstract CreditMemoAccount newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());

    }

    private static List<PersistableBusinessObjectBase> getAll() {
        List<PersistableBusinessObjectBase> recs = new ArrayList<PersistableBusinessObjectBase>();
        recs.add(REC1.newRecord());
        return recs;
    }

}
