/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountRevision;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiDecimal;

public enum CreditMemoAccountRevisionFixture {
    
    REC1 {
        
        private DateTimeService dateTimeService;
        
        @Override
        public CreditMemoAccountRevision newRecord() {
            CreditMemoAccountRevision obj = new CreditMemoAccountRevision();
            obj.setAccountRevisionIdentifier(1);
            obj.setItemIdentifier(41);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(7000));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(new java.sql.Timestamp(dateTimeService.getCurrentDate().getTime()));
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    };
    
    public abstract CreditMemoAccountRevision newRecord();

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
