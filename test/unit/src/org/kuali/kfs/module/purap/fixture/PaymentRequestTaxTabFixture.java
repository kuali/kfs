/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum PaymentRequestTaxTabFixture {
    // S: successful; F: failing

    // for testing tax income class
    INCOME_EMPTY(null, null, null, null, null, null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F
    INCOME_N_OTHERS_EMPTY("N", null, null, null, null, null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // S
    INCOME_N_OTHERS_NOTEMPTY("N", new BigDecimal(1), new BigDecimal(1), "US", "1234567890", null, true, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F
    INCOME_NOTN_TAX_COUNTRY_EMPTY("O", null, null, null, null, null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F

    // for testing tax rates

    INCOME_F_TAX_VALID("F", new BigDecimal(14), new BigDecimal("3.4"), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // S
    INCOME_F_FED_INVALID("F", new BigDecimal(15), new BigDecimal("3.4"), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F
    INCOME_F_ST_INVALID("F", new BigDecimal(30), new BigDecimal(1), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F

    INCOME_I_TAX_VALID("I", new BigDecimal(30), new BigDecimal(0), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // S
    INCOME_I_FED_INVALID("I", new BigDecimal(10), new BigDecimal(0), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F
    INCOME_I_ST_INVALID("I", new BigDecimal(30), new BigDecimal(1), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F

    INCOME_R_TAX_VALID("R", new BigDecimal(10), new BigDecimal(0), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // S
    INCOME_R_FED_INVALID("R", new BigDecimal(14), new BigDecimal(0), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F
    INCOME_R_ST_INVALID("R", new BigDecimal(10), new BigDecimal(1), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F

    FED_ZERO_ST_ZERO("F", new BigDecimal(0), new BigDecimal(0), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // S
    FFD_ZERO_ST_NOTZERO("F", new BigDecimal(0), new BigDecimal("3.4"), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F
    FED_NOTZERO_ST_ZERO("F", new BigDecimal(30), new BigDecimal(0), "US", "1234567890", null, null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F

    // for testing tax indicators

    GROSS_TAX_NOTZERO("R", new BigDecimal(30), new BigDecimal(0), "US", "1234567890", null, null, true, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // S
    GROSS_TAX_ZERO("R", new BigDecimal(0), new BigDecimal(0), "US", "1234567890", null, null, true, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F

    FOREIGN_TAX_ZERO("F", new BigDecimal(0), new BigDecimal(0), "US", "1234567890", null, null, null, true, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // S
    FOREIGN_TAX_NOTZERO("F", new BigDecimal(30), new BigDecimal("3.4"), "US", "1234567890", null, null, null, true, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F

    USAID_OTHER_INCOME_F_TAX_ZERO("F", new BigDecimal(0), new BigDecimal(0), "US", "1234567890", null, null, null, null, true, true, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // S
    USAID_INCOME_NOTF_TAX_NOTZERO("R", new BigDecimal(10), new BigDecimal(0), "US", "1234567890", null, null, null, null, true, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F

    OTHER_TAX_ZERO("I", new BigDecimal(0), new BigDecimal(0), "US", "1234567890", null, null, null, null, null, true, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // S
    OTHER_TAX_NOTZERO("I", new BigDecimal(30), new BigDecimal(0), "US", "1234567890", null, null, null, null, null, true, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F

    SW4_INCOME_F_TAX_ZERO("F", new BigDecimal(0), new BigDecimal(0), "US", "1234567890", new KualiDecimal(1), null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // S
    SW4_NEG_INCOME_NOTF_TAX_NOTZERO("R", new BigDecimal(10), new BigDecimal(0), "US", "1234567890", new KualiDecimal(-1), null, null, null, null, null, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW), // F

    SW4_TREATY_GROSS_FOREIGN_USAID_OTHER("F", new BigDecimal(0), new BigDecimal(0), "US", "1234567890", new KualiDecimal(1), true, true, true, true, true, PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW); // F

    private String taxClassificationCode;
    private BigDecimal federalTaxPercent;       // number is in whole form so 5% is 5.00
    private BigDecimal stateTaxPercent;         // number is in whole form so 5% is 5.00
    private String taxCountryCode;
    private String taxNQIId;
    private KualiDecimal taxSpecialW4Amount;
    private Boolean taxExemptTreatyIndicator;
    private Boolean grossUpIndicator;
    private Boolean foreignSourceIndicator;
    private Boolean taxUSAIDPerDiemIndicator;
    private Boolean otherTaxExemptIndicator;
    private String statusCode;

    private PaymentRequestTaxTabFixture(
            String taxClassificationCode,
            BigDecimal federalTaxPercent,
            BigDecimal stateTaxPercent,
            String taxCountryCode,
            String taxNQIId,
            KualiDecimal taxSpecialW4Amount,
            Boolean taxExemptTreatyIndicator,
            Boolean grossUpIndicator,
            Boolean foreignSourceIndicator,
            Boolean taxUSAIDPerDiemIndicator,
            Boolean otherTaxExemptIndicator,
            String statusCode) {
        this.taxClassificationCode = taxClassificationCode;
        this.federalTaxPercent = federalTaxPercent;
        this.stateTaxPercent = stateTaxPercent;
        this.taxCountryCode = taxCountryCode;
        this.taxNQIId = taxNQIId;
        this.taxSpecialW4Amount = taxSpecialW4Amount;
        this.taxExemptTreatyIndicator = taxExemptTreatyIndicator;
        this.grossUpIndicator = grossUpIndicator;
        this.foreignSourceIndicator = foreignSourceIndicator;
        this.taxUSAIDPerDiemIndicator = taxUSAIDPerDiemIndicator;
        this.otherTaxExemptIndicator = otherTaxExemptIndicator;
        this.statusCode = statusCode;
    }

    public PaymentRequestDocument populate(PaymentRequestDocument preqDocument) {
        preqDocument.setTaxClassificationCode(taxClassificationCode);
        preqDocument.setTaxFederalPercent(federalTaxPercent);
        preqDocument.setTaxStatePercent(stateTaxPercent);
        preqDocument.setTaxCountryCode(taxCountryCode);
        preqDocument.setTaxNQIId(taxNQIId);
        preqDocument.setTaxSpecialW4Amount(taxSpecialW4Amount);
        preqDocument.setTaxExemptTreatyIndicator(taxExemptTreatyIndicator);
        preqDocument.setTaxGrossUpIndicator(grossUpIndicator);
        preqDocument.setTaxForeignSourceIndicator(foreignSourceIndicator);
        preqDocument.setTaxUSAIDPerDiemIndicator(taxUSAIDPerDiemIndicator);
        preqDocument.setTaxOtherExemptIndicator(otherTaxExemptIndicator);
        preqDocument.setApplicationDocumentStatus(statusCode);

        return preqDocument;
    }
}
