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
package org.kuali.kfs.module.cab.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountRevision;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum CreditMemoAccountRevisionFixture {
    REC1 {
        @Override
        public CreditMemoAccountRevision newRecord() {
            CreditMemoAccountRevision obj = new CreditMemoAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(1);
            obj.setItemIdentifier(41);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(7000));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
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
