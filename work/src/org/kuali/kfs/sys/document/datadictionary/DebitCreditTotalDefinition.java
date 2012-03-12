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
package org.kuali.kfs.sys.document.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.document.web.renderers.DebitCreditTotalRenderer;
import org.kuali.kfs.sys.document.web.renderers.Renderer;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;

/**
 * Metadata that instructs the accounting line tags how to render debit/credit totals used in voucher documents
 */
public class DebitCreditTotalDefinition extends TotalDefinition {
    private static Logger LOG = Logger.getLogger(DebitCreditTotalDefinition.class);

    private String debitTotalProperty;
    private String creditTotalProperty;
    
    private boolean nestedProperty;
    private String containingPropertyName;

    private String representedProperty;

    private String debitTotalLabelProperty = "accounting.line.group.debitTotal.label";
    private String creditTotalLabelProperty = "accounting.line.group.creditTotal.label";

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.TotalDefinition#getTotalRenderer()
     */
    @Override
    public Renderer getTotalRenderer() {
        DebitCreditTotalRenderer renderer = new DebitCreditTotalRenderer();

        String actualCreditTotalProperty = this.getActualPropertyName(containingPropertyName, creditTotalProperty);
        renderer.setCreditTotalProperty(actualCreditTotalProperty);
        
        String actualDebitTotalProperty = this.getActualPropertyName(containingPropertyName, debitTotalProperty);
        renderer.setDebitTotalProperty(actualDebitTotalProperty);

        renderer.setRepresentedCellPropertyName(representedProperty);

        renderer.setCreditTotalLabelProperty(creditTotalLabelProperty);
        renderer.setDebitTotalLabelProperty(debitTotalLabelProperty);

        return renderer;
    }

    /**
     * Validates that total properties have been added
     * 
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (StringUtils.isBlank(debitTotalProperty) || StringUtils.isBlank(creditTotalProperty)) {
            throw new AttributeValidationException("Please specify both debitTotalProperty and creditTotalProperty for the AccountingLineGroupTotalRenderer");
        }
    }

    /**
     * Gets the debitTotalProperty attribute.
     * 
     * @return Returns the debitTotalProperty.
     */
    public String getDebitTotalProperty() {
        return debitTotalProperty;
    }

    /**
     * Sets the debitTotalProperty attribute value.
     * 
     * @param debitTotalProperty The debitTotalProperty to set.
     */
    public void setDebitTotalProperty(String debitTotalProperty) {
        this.debitTotalProperty = debitTotalProperty;
    }

    /**
     * Gets the creditTotalProperty attribute.
     * 
     * @return Returns the creditTotalProperty.
     */
    public String getCreditTotalProperty() {
        return creditTotalProperty;
    }

    /**
     * Sets the creditTotalProperty attribute value.
     * 
     * @param creditTotalProperty The creditTotalProperty to set.
     */
    public void setCreditTotalProperty(String creditTotalProperty) {
        this.creditTotalProperty = creditTotalProperty;
    }

    /**
     * Gets the debitTotalLabelProperty attribute.
     * 
     * @return Returns the debitTotalLabelProperty.
     */
    public String getDebitTotalLabelProperty() {
        return debitTotalLabelProperty;
    }

    /**
     * Sets the debitTotalLabelProperty attribute value.
     * 
     * @param debitTotalLabelProperty The debitTotalLabelProperty to set.
     */
    public void setDebitTotalLabelProperty(String debitTotalLabelProperty) {
        this.debitTotalLabelProperty = debitTotalLabelProperty;
    }

    /**
     * Gets the creditTotalLabelProperty attribute.
     * 
     * @return Returns the creditTotalLabelProperty.
     */
    public String getCreditTotalLabelProperty() {
        return creditTotalLabelProperty;
    }

    /**
     * Sets the creditTotalLabelProperty attribute value.
     * 
     * @param creditTotalLabelProperty The creditTotalLabelProperty to set.
     */
    public void setCreditTotalLabelProperty(String creditTotalLabelProperty) {
        this.creditTotalLabelProperty = creditTotalLabelProperty;
    }

    /**
     * Gets the representedProperty attribute.
     * 
     * @return Returns the representedProperty.
     */
    public String getRepresentedProperty() {
        return representedProperty;
    }

    /**
     * Sets the representedProperty attribute value.
     * 
     * @param representedProperty The representedProperty to set.
     */
    public void setRepresentedProperty(String representedProperty) {
        this.representedProperty = representedProperty;
    }

    /**
     * Sets the nestedProperty attribute value.
     * @param nestedProperty The nestedProperty to set.
     */
    public void setNestedProperty(boolean nestedProperty) {
        this.nestedProperty = nestedProperty;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.NestedFieldTotaling#setContainingPropertyName(java.lang.String)
     */
    public void setContainingPropertyName(String containingPropertyName) {
        this.containingPropertyName = containingPropertyName;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.NestedFieldTotaling#isNestedProperty()
     */
    public boolean isNestedProperty() {
        return this.nestedProperty;
    }
}
