/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.MaintainableFieldDefinition;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.FieldBridge;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;
import org.kuali.kfs.sys.document.web.TableJoining;

/**
 * Data dictionary definition of a field to be rendered as part of an accounting line view.
 */
public class AccountingLineViewFieldDefinition extends MaintainableFieldDefinition implements AccountingLineViewRenderableElementDefinition {
    private String dynamicLabelProperty;
    private boolean useShortLabel = false;
    private boolean hidden = false;

    /**
     * Gets the dynamicLabelProperty attribute. 
     * @return Returns the dynamicLabelProperty.
     */
    public String getDynamicLabelProperty() {
        return dynamicLabelProperty;
    }

    /**
     * Sets the dynamicLabelProperty attribute value.
     * @param dynamicLabelProperty The dynamicLabelProperty to set.
     */
    public void setDynamicLabelProperty(String dynamicLabelProperty) {
        this.dynamicLabelProperty = dynamicLabelProperty;
    }
    
    /**
     * Gets the useShortLabel attribute. 
     * @return Returns the useShortLabel.
     */
    public boolean shouldUseShortLabel() {
        return useShortLabel;
    }

    /**
     * Sets the useShortLabel attribute value.
     * @param useShortLabel The useShortLabel to set.
     */
    public void setUseShortLabel(boolean useShortLabel) {
        this.useShortLabel = useShortLabel;
    }

    /**
     * Gets the hidden attribute. 
     * @return Returns the hidden.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets the hidden attribute value.
     * @param hidden The hidden to set.
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement()
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        AccountingLineViewField layoutElement = new AccountingLineViewField();
        layoutElement.setDefinition(this);
        Field realField = FieldUtils.getPropertyField(accountingLineClass, getName(), false);
        FieldBridge.setupField(realField, this);
        if (isHidden()) {
            realField.setFieldType(Field.HIDDEN);
        }
        if (!StringUtils.isBlank(getWebUILeaveFieldFunction())) {
            realField.setWebOnBlurHandler(getWebUILeaveFieldFunction());
        }
        if (!StringUtils.isBlank(getWebUILeaveFieldCallbackFunction())) {
            realField.setWebOnBlurHandlerCallback(getWebUILeaveFieldCallbackFunction());
        }
        layoutElement.setField(realField);     
        return layoutElement;
    }
}
