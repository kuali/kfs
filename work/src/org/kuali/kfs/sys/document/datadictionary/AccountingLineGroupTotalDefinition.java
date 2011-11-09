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
import org.kuali.kfs.sys.document.web.renderers.GroupTotalRenderer;
import org.kuali.kfs.sys.document.web.renderers.Renderer;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;

/**
 * The definition of an accounting line group total renderer, which will display an accounting line
 * group total as a standard "Total: " + amount.
 */
public class AccountingLineGroupTotalDefinition extends TotalDefinition{
    private String totalProperty;
    private String representedProperty;
    private boolean nestedProperty;
    private String containingPropertyName;
    private String totalLabelProperty = "accounting.line.group.total.label";
    
    /**
     * Gets the totalProperty attribute. 
     * @return Returns the totalProperty.
     */
    public String getTotalProperty() {
        return totalProperty;
    }

    /**
     * Sets the totalProperty attribute value.
     * @param totalProperty The totalProperty to set.
     */
    public void setTotalProperty(String totalProperty) {
        this.totalProperty = totalProperty;
    }

    /**
     * Gets the totalLabelProperty attribute. 
     * @return Returns the totalLabelProperty.
     */
    public String getTotalLabelProperty() {
        return totalLabelProperty;
    }

    /**
     * Sets the totalLabelProperty attribute value.
     * @param totalLabelProperty The totalLabelProperty to set.
     */
    public void setTotalLabelProperty(String totalLabelProperty) {
        this.totalLabelProperty = totalLabelProperty;
    }
    
    /**
     * Uses GroupTotalRenderer to render the total
     * @see org.kuali.kfs.sys.document.datadictionary.TotalDefinition#getTotalRenderer()
     */
    @Override
    public Renderer getTotalRenderer() {
        GroupTotalRenderer renderer = new GroupTotalRenderer();
        
        renderer.setTotalLabelProperty(totalLabelProperty);
        renderer.setRepresentedCellPropertyName(representedProperty);
        
        final String actualTotalProperty = this.getActualPropertyName(containingPropertyName, totalProperty);
        renderer.setTotalProperty(actualTotalProperty);
        
        return renderer;
    }

    /**
     * Validates that a total property has been added
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (StringUtils.isBlank(totalProperty)) {
            throw new AttributeValidationException("Please specify a totalProperty for the AccountingLineGroupTotalRenderer");
        }
    }

    /**
     * Gets the representedProperty attribute. 
     * @return Returns the representedProperty.
     */
    public String getRepresentedProperty() {
        return representedProperty;
    }

    /**
     * Sets the representedProperty attribute value.
     * @param representedProperty The representedProperty to set.
     */
    public void setRepresentedProperty(String representedProperty) {
        this.representedProperty = representedProperty;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.datadictionary.TotalDefinition#isNestedProperty()
     */
    public boolean isNestedProperty() {
        return nestedProperty;
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
}
