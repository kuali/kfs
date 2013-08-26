/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.math.BigDecimal;

/**
 * Utility class which encapsulates a TEM source accounting line and the percentage of a grand total amount it represents
 */
public class TemSourceAccountingLineTotalPercentage {
    private TemSourceAccountingLine temSourceAccountingLine;
    private BigDecimal percentage;

    public TemSourceAccountingLineTotalPercentage(TemSourceAccountingLine temSourceAccountingLine, BigDecimal percentage) {
        super();
        this.temSourceAccountingLine = temSourceAccountingLine;
        this.percentage = percentage;
    }

    public TemSourceAccountingLine getTemSourceAccountingLine() {
        return temSourceAccountingLine;
    }
    public BigDecimal getPercentage() {
        return percentage;
    }
}
