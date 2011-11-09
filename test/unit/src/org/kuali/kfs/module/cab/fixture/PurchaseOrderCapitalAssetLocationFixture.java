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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetLocation;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum PurchaseOrderCapitalAssetLocationFixture {

    REC1 {
        @Override
        public PurchaseOrderCapitalAssetLocation newRecord() {
            PurchaseOrderCapitalAssetLocation obj = new PurchaseOrderCapitalAssetLocation();
            obj.setCapitalAssetLocationIdentifier(1300);
            obj.setCapitalAssetSystemIdentifier(1102);
            obj.setItemQuantity(new KualiDecimal(3));
            obj.setCampusCode("BL");
            obj.setOffCampusIndicator(false);
            obj.setBuildingCode("BL001");
            obj.setBuildingRoomNumber("009");
            return obj;
        };
    },
    REC2 {
        @Override
        public PurchaseOrderCapitalAssetLocation newRecord() {
            PurchaseOrderCapitalAssetLocation obj = new PurchaseOrderCapitalAssetLocation();
            obj.setCapitalAssetLocationIdentifier(1301);
            obj.setCapitalAssetSystemIdentifier(1103);
            obj.setItemQuantity(new KualiDecimal(2));
            obj.setCampusCode("BL");
            obj.setOffCampusIndicator(true);
            obj.setCapitalAssetLine1Address("2700 Broadway");
            obj.setCapitalAssetCityName("Lansing");
            obj.setCapitalAssetStateCode("MI");
            obj.setCapitalAssetPostalCode("44555");
            return obj;
        };
    };
    public abstract PurchaseOrderCapitalAssetLocation newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());

    }

    private static List<PersistableBusinessObjectBase> getAll() {
        List<PersistableBusinessObjectBase> recs = new ArrayList<PersistableBusinessObjectBase>();
        recs.add(REC1.newRecord());
        recs.add(REC2.newRecord());
        return recs;
    }
}
