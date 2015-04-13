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
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;


public enum PurchaseOrderDocumentFixture {

    REC1 {

        @Override
        public PurchaseOrderDocument newRecord() {
            PurchaseOrderDocument obj = new PurchaseOrderDocument();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setDocumentNumber("21");
            obj.setPurapDocumentIdentifier(21);
            obj.setRequisitionIdentifier(11);
            obj.setRequisitionSourceCode("STAN");
//            obj.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_OPEN);
            obj.setPurchaseOrderVendorChoiceCode("CONT");
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingTitleCode("OR");
            obj.setVendorFaxNumber("517-515-5117");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setDocumentFundingSourceCode("INST");
            obj.setRequestorPersonName("HUNTLEY,KEISHA Y");
            obj.setRequestorPersonEmailAddress("KGLTEST-L@INDIANA.EDU");
            obj.setRequestorPersonPhoneNumber("248-837-5242");
            obj.setDeliveryCampusCode("EA");
            obj.setDeliveryBuildingName("Middlefork Hall");
            obj.setDeliveryBuildingRoomNumber("21");
            obj.setDeliveryBuildingLine1Address("2325 Chester Blvd");
            obj.setDeliveryCityName("Richmond");
            obj.setDeliveryStateCode("IN");
            obj.setDeliveryPostalCode("47374-1220");
            obj.setDeliveryToName("stoole");
            obj.setChartOfAccountsCode("UA");
            obj.setOrganizationCode("VPIT");
            obj.setPurchaseOrderCreateTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPurchaseOrderCostSourceCode("EST");
            obj.setPurchaseOrderTransmissionMethodCode("PRIN");
            obj.setBillingName("UNIVERSITY EAST");
            obj.setBillingLine1Address("ACCOUNTS PAYABLE");
            obj.setBillingCityName("RICHMOND");
            obj.setBillingStateCode("OR");
            obj.setBillingPostalCode("47374-1289");
            obj.setBillingCountryCode("US");
            obj.setBillingPhoneNumber("892-973-8392");
            obj.setContractManagerCode(10);
            obj.setPurchaseOrderAutomaticIndicator(false);
            obj.setPurchaseOrderInitialOpenTimestamp(timeStamp);
            obj.setPurchaseOrderLastTransmitTimestamp(timeStamp);
            obj.setPurchaseOrderConfirmedIndicator(false);
            obj.setPurchaseOrderCurrentIndicator(true);
            obj.setPendingActionIndicator(false);
            obj.setPurchaseOrderFirstTransmissionTimestamp(timeStamp);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setCapitalAssetSystemTypeCode("IND");
            obj.setCapitalAssetSystemStateCode("MOD");
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PO1.newRecord());
            return obj;
        };
    },
    REC2 {

        @Override
        public PurchaseOrderDocument newRecord() {
            PurchaseOrderDocument obj = new PurchaseOrderDocument();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setDocumentNumber("22");
            obj.setPurapDocumentIdentifier(22);
            obj.setRequisitionIdentifier(12);
            obj.setRequisitionSourceCode("STAN");
//            obj.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_OPEN);
            obj.setPurchaseOrderVendorChoiceCode("CONT");
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingTitleCode("OR");
            obj.setVendorFaxNumber("517-515-5117");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setDocumentFundingSourceCode("INST");
            obj.setRequestorPersonName("HUNTLEY,KEISHA Y");
            obj.setRequestorPersonEmailAddress("KGLTEST-L@INDIANA.EDU");
            obj.setRequestorPersonPhoneNumber("248-837-5242");
            obj.setDeliveryCampusCode("EA");
            obj.setDeliveryBuildingName("Middlefork Hall");
            obj.setDeliveryBuildingRoomNumber("21");
            obj.setDeliveryBuildingLine1Address("2325 Chester Blvd");
            obj.setDeliveryCityName("Richmond");
            obj.setDeliveryStateCode("IN");
            obj.setDeliveryPostalCode("47374-1220");
            obj.setDeliveryToName("stoole");
            obj.setChartOfAccountsCode("UA");
            obj.setOrganizationCode("VPIT");
            obj.setPurchaseOrderCreateTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPurchaseOrderCostSourceCode("EST");
            obj.setPurchaseOrderTransmissionMethodCode("PRIN");
            obj.setBillingName("UNIVERSITY EAST");
            obj.setBillingLine1Address("ACCOUNTS PAYABLE");
            obj.setBillingCityName("RICHMOND");
            obj.setBillingStateCode("OR");
            obj.setBillingPostalCode("47374-1289");
            obj.setBillingCountryCode("US");
            obj.setBillingPhoneNumber("892-973-8392");
            obj.setContractManagerCode(10);
            obj.setPurchaseOrderAutomaticIndicator(false);
            obj.setPurchaseOrderInitialOpenTimestamp(timeStamp);
            obj.setPurchaseOrderLastTransmitTimestamp(timeStamp);
            obj.setPurchaseOrderConfirmedIndicator(false);
            obj.setPurchaseOrderCurrentIndicator(true);
            obj.setPendingActionIndicator(false);
            obj.setPurchaseOrderFirstTransmissionTimestamp(timeStamp);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setCapitalAssetSystemTypeCode("IND");
            obj.setCapitalAssetSystemStateCode("NEW");
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PO2.newRecord());
            return obj;
        };
    },
    REC3 {

        @Override
        public PurchaseOrderDocument newRecord() {
            PurchaseOrderDocument obj = new PurchaseOrderDocument();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setDocumentNumber("23");
            obj.setPurapDocumentIdentifier(23);
            obj.setRequisitionIdentifier(13);
            obj.setRequisitionSourceCode("STAN");
//            obj.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_OPEN);
            obj.setPurchaseOrderVendorChoiceCode("CONT");
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingTitleCode("OR");
            obj.setVendorFaxNumber("517-515-5117");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setDocumentFundingSourceCode("INST");
            obj.setRequestorPersonName("HUNTLEY,KEISHA Y");
            obj.setRequestorPersonEmailAddress("KGLTEST-L@INDIANA.EDU");
            obj.setRequestorPersonPhoneNumber("248-837-5242");
            obj.setDeliveryCampusCode("EA");
            obj.setDeliveryBuildingName("Middlefork Hall");
            obj.setDeliveryBuildingRoomNumber("21");
            obj.setDeliveryBuildingLine1Address("2325 Chester Blvd");
            obj.setDeliveryCityName("Richmond");
            obj.setDeliveryStateCode("IN");
            obj.setDeliveryPostalCode("47374-1220");
            obj.setDeliveryToName("stoole");
            obj.setChartOfAccountsCode("UA");
            obj.setOrganizationCode("VPIT");
            obj.setPurchaseOrderCreateTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPurchaseOrderCostSourceCode("EST");
            obj.setPurchaseOrderTransmissionMethodCode("PRIN");
            obj.setBillingName("UNIVERSITY EAST");
            obj.setBillingLine1Address("ACCOUNTS PAYABLE");
            obj.setBillingCityName("RICHMOND");
            obj.setBillingStateCode("OR");
            obj.setBillingPostalCode("47374-1289");
            obj.setBillingCountryCode("US");
            obj.setBillingPhoneNumber("892-973-8392");
            obj.setContractManagerCode(10);
            obj.setPurchaseOrderAutomaticIndicator(false);
            obj.setPurchaseOrderInitialOpenTimestamp(timeStamp);
            obj.setPurchaseOrderLastTransmitTimestamp(timeStamp);
            obj.setPurchaseOrderConfirmedIndicator(false);
            obj.setPurchaseOrderCurrentIndicator(true);
            obj.setPendingActionIndicator(false);
            obj.setPurchaseOrderFirstTransmissionTimestamp(timeStamp);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setCapitalAssetSystemTypeCode("IND");
            obj.setCapitalAssetSystemStateCode("NEW");
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PO3.newRecord());
            return obj;
        };
    };
    public abstract PurchaseOrderDocument newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());

    }

    private static List<PersistableBusinessObjectBase> getAll() {
        List<PersistableBusinessObjectBase> recs = new ArrayList<PersistableBusinessObjectBase>();
        recs.add(REC1.newRecord());
        recs.add(REC2.newRecord());
        recs.add(REC3.newRecord());
        return recs;
    }
}
