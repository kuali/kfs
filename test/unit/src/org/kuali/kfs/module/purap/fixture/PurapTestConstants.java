/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.purap.fixture;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderAssetTransactionType;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.RecurringPaymentType;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class PurapTestConstants {

    static final Integer FY_2007 = new Integer("2007");

    @SuppressWarnings("deprecation")
    static class BeginEndDates {
        static final Date FIRST_DATE = new Date(108, 1, 1); // 2008-01-01
        static final Date LAST_DATE = new Date(109, 1, 1); // 2009-01-01
        static RequisitionDocument REQ = new RequisitionDocument();
        static PurchaseOrderDocument PO = new PurchaseOrderDocument();
    }

    static class RecurringPaymentTypes {
        static final String FIXD = "FIXD";
        static final String FVAR = "FVAR";
        static final String VARV = "VARV";
    }

    static class AmountsLimits {
        static final KualiDecimal LARGE_NEGATIVE_AMOUNT = new KualiDecimal(-1024);
        static final KualiDecimal SMALL_NEGATIVE_AMOUNT = new KualiDecimal(-32);
        static final KualiDecimal ZERO = KualiDecimal.ZERO;
        static final KualiDecimal SMALL_POSITIVE_AMOUNT = new KualiDecimal(32);
        static final KualiDecimal LARGE_POSITIVE_AMOUNT = new KualiDecimal(1024);
    }

    static class ItemsAccounts {
        static RequisitionItem REQ_ITEM = new RequisitionItem();
        static PurchaseOrderItem PO_ITEM = new PurchaseOrderItem();
        static final String ITEM_DESC = "Test item description";
        static final String ITEM_UOM = "EA";
        static final String ITEM_CATALOG_NUMBER = "1F742";
        static PurchaseOrderAccount PO_ACCOUNT = new PurchaseOrderAccount();
        static RequisitionAccount REQ_ACCOUNT = new RequisitionAccount();
        static final String CHART_CODE = "BL";
        static final String OBJECT_CODE = "5000";
        static final String ACCOUNT_NUMBER = "1031400";
        static final KualiDecimal QUANTITY = new KualiDecimal(1);
        static final BigDecimal UNIT_PRICE = new KualiDecimal(32).bigDecimalValue();
        static final BigDecimal NEGATIVE_UNIT_PRICE = new KualiDecimal(-32).bigDecimalValue();
        static final BigDecimal PERCENTAGE = new KualiDecimal(100).bigDecimalValue();

        static final BigDecimal UNIT_PRICE_APO_1 = new KualiDecimal(1.99).bigDecimalValue();
        static final BigDecimal UNIT_PRICE_APO_2 = new KualiDecimal(239.99).bigDecimalValue();
    }

    @SuppressWarnings("deprecation")
    static class PO {
        static final Integer REQ_ID = new Integer("8888");
        static final Timestamp CREATE_DATE = new Timestamp(107, 10, 10, 0, 0, 0, 0); // 2007-10-10
        static final KualiDecimal AMOUNT = new KualiDecimal(32);
    }

    @SuppressWarnings("deprecation")
    static class PREQInvoice {
        static final Integer PO_ID = new Integer("9999");
        static final Date INVOICE_DATE = new Date(108, 1, 1); // 2008-01-01
        static final String INVOICE_NUMBER = "123456789";
        static final KualiDecimal AMOUNT = new KualiDecimal(32);
    }
    
    static class CMInit {
        static final String INITIAL_VENDOR_NUMBER = "1000-0";
        static final KualiDecimal HIGH_AMOUNT = new KualiDecimal(32);
        static final KualiDecimal LOW_AMOUNT = new KualiDecimal(16);
        static final KualiDecimal ZERO_AMOUNT = new KualiDecimal(0);
    }
    
    static class PurCams {
        static final BigDecimal CAPITAL_AMOUNT = new BigDecimal(6000);
        static final BigDecimal CAPITAL_POSITIVE_AMOUNT = new BigDecimal(16);
        static final KualiDecimal POSITIVE_AMOUNT = new KualiDecimal(16);
        static final KualiDecimal ZERO_AMOUNT = new KualiDecimal(0);
        static final KualiDecimal NEGATIVE_AMOUNT = new KualiDecimal(-16);
        static final ObjectCode CAPITAL_OBJECT_CODE = new ObjectCode(new Integer("2008"),"BL","7017"); //Level should be set to "CAP"
        static final ObjectCode POSSIBLE_OBJECT_CODE = new ObjectCode(new Integer("2008"),"BL","7050"); //Level should be set to "S&E"
        static final ObjectCode EXPENSE_OBJECT_CODE = new ObjectCode(new Integer("2008"),"BL","5010"); //Level should be set to "DEBT"
        static final CapitalAssetBuilderAssetTransactionType NONRECURRING_ASSET_NUMBER_REQUIRING_TRAN_TYPE = new MockAssetTransactionType("MDEX");
        static final CapitalAssetBuilderAssetTransactionType NONRECURRING_NON_ASSET_NUMBER_REQUIRING_TRAN_TYPE = new MockAssetTransactionType("NEW");
        static final CapitalAssetBuilderAssetTransactionType RECURRING_TRAN_TYPE = new MockAssetTransactionType("LEAS");
        static final RecurringPaymentType RECURRING_PAYMENT_TYPE = new RecurringPaymentType();
        static final Long ASSET_NUMBER_1 = new Long("333221");
        static final Long ASSET_NUMBER_2 = new Long("333222");
        
        static class MockAssetTransactionType implements CapitalAssetBuilderAssetTransactionType {
            private String capitalAssetTransactionTypeCode;
            private String capitalAssetTransactionTypeDescription;
            private boolean capitalAssetNonquantityDrivenAllowIndicator;
            private String capitalAssetQuantitySubtypeRequiredText;
            private String capitalAssetNonquantitySubtypeRequiredText;
            private boolean active;
            
            /**
             * Constructs a MockAssetTransactionType.java.
             * @param capitalAssetTransactionTypeCode
             */
            public MockAssetTransactionType(String capitalAssetTransactionTypeCode) {
                this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
            }

            public String getCapitalAssetTransactionTypeCode() {
                return capitalAssetTransactionTypeCode;
            }

            public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
                this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
            }

            public String getCapitalAssetTransactionTypeDescription() {
                return capitalAssetTransactionTypeDescription;
            }

            public void setCapitalAssetTransactionTypeDescription(String capitalAssetTransactionTypeDescription) {
                this.capitalAssetTransactionTypeDescription = capitalAssetTransactionTypeDescription;
            }

            public boolean getCapitalAssetNonquantityDrivenAllowIndicator() {
                return capitalAssetNonquantityDrivenAllowIndicator;
            }

            public void setCapitalAssetNonquantityDrivenAllowIndicator(boolean capitalAssetNonquantityDrivenAllowIndicator) {
                this.capitalAssetNonquantityDrivenAllowIndicator = capitalAssetNonquantityDrivenAllowIndicator;
            }
            
            public String getCapitalAssetNonquantitySubtypeRequiredText() {
                return capitalAssetNonquantitySubtypeRequiredText;
            }

            public void setCapitalAssetNonquantitySubtypeRequiredText(
                    String capitalAssetNonquantitySubtypeRequiredText) {
                this.capitalAssetNonquantitySubtypeRequiredText = capitalAssetNonquantitySubtypeRequiredText;
            }

            public String getCapitalAssetQuantitySubtypeRequiredText() {
                return capitalAssetQuantitySubtypeRequiredText;
            }

            public void setCapitalAssetQuantitySubtypeRequiredText(
                    String capitalAssetQuantitySubtypeRequiredText) {
                this.capitalAssetQuantitySubtypeRequiredText = capitalAssetQuantitySubtypeRequiredText;
            }

            public boolean isActive() {
                return active;
            }

            public void setActive(boolean active) {
                this.active = active;
            }
            
            public void refresh() {}
            
            public void prepareForWorkflow() {}
        }
    }
    
    static class Threshold {
        static final String CHART_CODE = "BA";
        static final String CHART_CODE_INVALID = "XX";
        static final String ACCOUNT_TYPE = "AI";
        static final String SUBACCOUNT_TYPE = "AUXAMB";
        static final String SUBACCOUNT_TYPE_INVALID = "XXXXXX";
        static final String COMMODITY_CODE = "812922";
        static final String COMMODITY_CODE_INVALID = "000000";
        static final String OBJECT_CODE = "3310";
        static final String OBJECT_CODE_INVALID = "0000";
        static final String ORG_CODE = "ACAC";
        static final String ORG_CODE_INVALID = "XXXX";
        static final String VENDOR_NUMBER = "1000-0";
        static final String VENDOR_NUMBER_INVALID = "0000-0";
        static final KualiDecimal THRESHOLD_AMOUNT = new KualiDecimal(100);
    }
    
    @SuppressWarnings("deprecation")
    static class BulkReceiving {
        static final Date SHIPMENT_RECEIVIED_DATE = new Date(108, 1, 1); // 2008-01-01
    }
}
