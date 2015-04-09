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
