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

import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum RequisitionDocumentFixture {
    REC1 {
        @Override
        public RequisitionDocument newRecord() {
            RequisitionDocument obj = new RequisitionDocument();
            obj.setPurapDocumentIdentifier(11);
            obj.setDocumentNumber("11");
            obj.setDocumentFundingSourceCode("INST");
            obj.setRequisitionSourceCode("STAN");
            // You can't set this on a fake document as it's stored on the workflow header, which doesn't exist
//            obj.setApplicationDocumentStatus(RequisitionStatuses.APPDOC_AWAIT_CONTRACT_MANAGER_ASSGN);
            obj.setPurchaseOrderTransmissionMethodCode("PRIN");
            obj.setPurchaseOrderCostSourceCode("EST");
            obj.setChartOfAccountsCode("UA");
            obj.setOrganizationCode("VPIT");
            obj.setDeliveryCampusCode("EA");
            obj.setPostingYear(2009);
            obj.setRequestorPersonName("HUNTLEY,KEISHA Y");
            obj.setRequestorPersonEmailAddress("KGLTEST-L@INDIANA.EDU");
            obj.setRequestorPersonPhoneNumber("248-837-5242");
            obj.setDeliveryBuildingName("Middlefork Hall");
            obj.setDeliveryBuildingRoomNumber("21");
            obj.setDeliveryBuildingLine1Address("2325 Chester Blvd");
            obj.setDeliveryCityName("Richmond");
            obj.setDeliveryStateCode("IN");
            obj.setDeliveryPostalCode("47374-1220");
            obj.setDeliveryToName("stoole");
            obj.setBillingName("UNIVERSITY EAST");
            obj.setBillingLine1Address("ACCOUNTS PAYABLE");
            obj.setBillingCityName("RICHMOND");
            obj.setBillingStateCode("OR");
            obj.setBillingPostalCode("47374-1289");
            obj.setBillingCountryCode("US");
            obj.setBillingPhoneNumber("892-973-8392");
            obj.setOrganizationAutomaticPurchaseOrderLimit(new KualiDecimal(10000));
            obj.setPurchaseOrderAutomaticIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(false);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.REQS1.newRecord());
            return obj;
        }
    },

    REC2 {
        @Override
        public RequisitionDocument newRecord() {
            RequisitionDocument obj = new RequisitionDocument();
            obj.setPurapDocumentIdentifier(12);
            obj.setDocumentNumber("12");
            obj.setDocumentFundingSourceCode("INST");
            obj.setRequisitionSourceCode("STAN");
//            obj.setApplicationDocumentStatus(RequisitionStatuses.APPDOC_AWAIT_CONTRACT_MANAGER_ASSGN);
            obj.setPurchaseOrderTransmissionMethodCode("PRIN");
            obj.setPurchaseOrderCostSourceCode("EST");
            obj.setChartOfAccountsCode("UA");
            obj.setOrganizationCode("VPIT");
            obj.setDeliveryCampusCode("EA");
            obj.setPostingYear(2009);
            obj.setRequestorPersonName("HUNTLEY,KEISHA Y");
            obj.setRequestorPersonEmailAddress("KGLTEST-L@INDIANA.EDU");
            obj.setRequestorPersonPhoneNumber("248-837-5242");
            obj.setDeliveryBuildingName("Middlefork Hall");
            obj.setDeliveryBuildingRoomNumber("21");
            obj.setDeliveryBuildingLine1Address("2325 Chester Blvd");
            obj.setDeliveryCityName("Richmond");
            obj.setDeliveryStateCode("IN");
            obj.setDeliveryPostalCode("47374-1220");
            obj.setDeliveryToName("stoole");
            obj.setBillingName("UNIVERSITY EAST");
            obj.setBillingLine1Address("ACCOUNTS PAYABLE");
            obj.setBillingCityName("RICHMOND");
            obj.setBillingStateCode("OR");
            obj.setBillingPostalCode("47374-1289");
            obj.setBillingCountryCode("US");
            obj.setBillingPhoneNumber("892-973-8392");
            obj.setOrganizationAutomaticPurchaseOrderLimit(new KualiDecimal(10000));
            obj.setPurchaseOrderAutomaticIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(false);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.REQS2.newRecord());
            return obj;
        }
    },

    REC3 {
        @Override
        public RequisitionDocument newRecord() {
            RequisitionDocument obj = new RequisitionDocument();
            obj.setPurapDocumentIdentifier(13);
            obj.setDocumentNumber("13");
            obj.setDocumentFundingSourceCode("INST");
            obj.setRequisitionSourceCode("STAN");
//            obj.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_OPEN);
            obj.setPurchaseOrderTransmissionMethodCode("PRIN");
            obj.setPurchaseOrderCostSourceCode("EST");
            obj.setChartOfAccountsCode("UA");
            obj.setOrganizationCode("VPIT");
            obj.setDeliveryCampusCode("EA");
            obj.setPostingYear(2009);
            obj.setRequestorPersonName("HUNTLEY,KEISHA Y");
            obj.setRequestorPersonEmailAddress("KGLTEST-L@INDIANA.EDU");
            obj.setRequestorPersonPhoneNumber("248-837-5242");
            obj.setDeliveryBuildingName("Middlefork Hall");
            obj.setDeliveryBuildingRoomNumber("21");
            obj.setDeliveryBuildingLine1Address("2325 Chester Blvd");
            obj.setDeliveryCityName("Richmond");
            obj.setDeliveryStateCode("IN");
            obj.setDeliveryPostalCode("47374-1220");
            obj.setDeliveryToName("stoole");
            obj.setBillingName("UNIVERSITY EAST");
            obj.setBillingLine1Address("ACCOUNTS PAYABLE");
            obj.setBillingCityName("RICHMOND");
            obj.setBillingStateCode("OR");
            obj.setBillingPostalCode("47374-1289");
            obj.setBillingCountryCode("US");
            obj.setBillingPhoneNumber("892-973-8392");
            obj.setOrganizationAutomaticPurchaseOrderLimit(new KualiDecimal(10000));
            obj.setPurchaseOrderAutomaticIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(false);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.REQS3.newRecord());
            return obj;
        }
    };
    public abstract RequisitionDocument newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());
    }

    private static List<RequisitionDocument> getAll() {
        List<RequisitionDocument> recs = new ArrayList<RequisitionDocument>();
        recs.add(REC1.newRecord());
        recs.add(REC2.newRecord());
        recs.add(REC3.newRecord());
        return recs;
    }
}
