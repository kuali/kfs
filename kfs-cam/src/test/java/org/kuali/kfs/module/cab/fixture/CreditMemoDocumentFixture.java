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

import org.kuali.kfs.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum CreditMemoDocumentFixture {

    REC1 {


        @Override
        public VendorCreditMemoDocument newRecord() {
            VendorCreditMemoDocument obj = new VendorCreditMemoDocument();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setPurapDocumentIdentifier(41);
            obj.setDocumentNumber("41");
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setPurchaseOrderIdentifier(21);
            obj.setPostingYear(2009);
//            obj.setApplicationDocumentStatus(CreditMemoStatuses.APPDOC_COMPLETE);
            obj.setCreditMemoNumber("1003");
            obj.setCreditMemoDate(new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime()));
            obj.setCreditMemoAmount(new KualiDecimal(7000));
            obj.setHoldIndicator(false);
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(timeStamp);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setContinuationAccountIndicator(false);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.CM1.newRecord());
            return obj;
        };
    };
    public abstract VendorCreditMemoDocument newRecord();

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
