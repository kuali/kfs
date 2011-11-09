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

import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum CreditMemoItemFixture {

    REC1 {
        @Override
        public CreditMemoItem newRecord() {
            CreditMemoItem obj = new CreditMemoItem();
            obj.setItemIdentifier(41);
            obj.setPurapDocumentIdentifier(41);
            obj.setItemLineNumber(2);
            obj.setItemTypeCode("ITEM");
            obj.setItemQuantity(new KualiDecimal(1));
            obj.setItemUnitPrice(new BigDecimal(7000));
            obj.setExtendedPrice(new KualiDecimal(7000));
            obj.setItemAssignedToTradeInIndicator(false);
            obj.setItemDescription("Desk & Chair");
            obj.setPoInvoicedTotalQuantity(new KualiDecimal(2));
            obj.setPoUnitPrice(new BigDecimal(7000));
            obj.setPoTotalAmount(new KualiDecimal(14000));
            return obj;
        };
    };
    public abstract CreditMemoItem newRecord();

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
