/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;


public class BenefitInquiry implements Comparable<BenefitInquiry> {
    KualiDecimal benefitAmount;
    String fringeBenefitObjectCode;

    public KualiDecimal getBenefitAmount() {
        return benefitAmount;
    }

    public void setBenefitAmount(KualiDecimal benefitAmount) {
        this.benefitAmount = benefitAmount;
    }

    public String getFringeBenefitObjectCode() {
        return fringeBenefitObjectCode;
    }

    public void setFringeBenefitObjectCode(String fringeBenefitObjectCode) {
        this.fringeBenefitObjectCode = fringeBenefitObjectCode;
    }

    @Override
    public int compareTo(BenefitInquiry benefitInquiry) {
        return benefitInquiry.getFringeBenefitObjectCode().compareTo(this.fringeBenefitObjectCode);
    }
}



