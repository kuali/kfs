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

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetSystem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum PurchaseOrderCapitalAssetSystemFixture {

    REC1 {
        @Override
        public PurchaseOrderCapitalAssetSystem newRecord() {
            PurchaseOrderCapitalAssetSystem obj = new PurchaseOrderCapitalAssetSystem();
            obj.setCapitalAssetSystemIdentifier(1100);
            obj.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(false);
            return obj;
        };
    },
    REC2 {
        @Override
        public PurchaseOrderCapitalAssetSystem newRecord() {
            PurchaseOrderCapitalAssetSystem obj = new PurchaseOrderCapitalAssetSystem();
            obj.setCapitalAssetSystemIdentifier(1101);
            obj.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(false);
            return obj;
        };
    },
    REC3 {
        @Override
        public PurchaseOrderCapitalAssetSystem newRecord() {
            PurchaseOrderCapitalAssetSystem obj = new PurchaseOrderCapitalAssetSystem();
            obj.setCapitalAssetSystemIdentifier(1102);
            obj.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(false);
            return obj;
        };
    },
    REC4 {
        @Override
        public PurchaseOrderCapitalAssetSystem newRecord() {
            PurchaseOrderCapitalAssetSystem obj = new PurchaseOrderCapitalAssetSystem();
            obj.setCapitalAssetSystemIdentifier(1103);
            obj.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(false);
            return obj;
        };
    };
    public abstract PurchaseOrderCapitalAssetSystem newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());

    }

    private static List<PersistableBusinessObjectBase> getAll() {
        List<PersistableBusinessObjectBase> recs = new ArrayList<PersistableBusinessObjectBase>();
        recs.add(REC1.newRecord());
        recs.add(REC2.newRecord());
        recs.add(REC3.newRecord());
        recs.add(REC4.newRecord());
        return recs;
    }
}
