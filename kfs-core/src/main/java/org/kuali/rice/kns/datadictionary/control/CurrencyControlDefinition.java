/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.control;


/**
 The currency element defines an HTML text control for
 entering dollar and cents amounts.  Only two decimals to
 the right of the decimal point are allowed.  Formatted
 value is displayed with commas.

 Used Properties: size, formattedMaxLength
 */
@Deprecated
public class CurrencyControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = 1650000676894176080L;

    /**
     * the maxLength for text that has been formatted. ie if maxLength=5. [12345]. but after going through the formatter the value
     * is [12,345.00] and will no longer fit in a field whos maxLength=5. formattedMaxLength solves this problem.
     */
    protected Integer formattedMaxLength;

    public CurrencyControlDefinition() {
        this.type = ControlDefinitionType.CURRENCY;
    }

    /**
     *
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isCurrency()
     */
    public boolean isCurrency() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "CurrencyControlDefinition";
    }

    /**
     * @return Returns the formattedMaxLength parameter for currency controls.
     */
    public Integer getFormattedMaxLength() {
        return formattedMaxLength;
    }

    /**
     * the maxLength for text that has been formatted. ie if maxLength=5. [12345]. but after going through the formatter the value
     * is [12,345.00] and will no longer fit in a field whos maxLength=5. formattedMaxLength solves this problem.
     */
    public void setFormattedMaxLength(Integer formattedMaxLength) {
        this.formattedMaxLength = formattedMaxLength;
    }

}
