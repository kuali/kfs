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
package org.kuali.kfs.sys.document.web;

import java.util.List;

import org.kuali.rice.kns.web.ui.Field;

/**
 * Base class for header labels
 */
public abstract class HeaderLabel implements RenderableElement {
    private boolean labeledFieldEmptyOrHidden = false;
    
    /**
     * Header labels are never action blocks
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isActionBlock()
     */
    public boolean isActionBlock() {
        return false;
    }

    /**
     * Header labels are never hidden
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * Returns whether the label field is either hidden or empty; this way, any labels for
     * moved hidden fields will be removed
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isEmpty()
     */
    public boolean isEmpty() {
        return labeledFieldEmptyOrHidden;
    }
    
    /**
     * Gets the labeledFieldEmptyOrHidden attribute. 
     * @return Returns the labeledFieldEmptyOrHidden.
     */
    public boolean isLabeledFieldEmptyOrHidden() {
        return labeledFieldEmptyOrHidden;
    }

    /**
     * Sets the labeledFieldEmptyOrHidden attribute value.
     * @param labeledFieldEmptyOrHidden The labeledFieldEmptyOrHidden to set.
     */
    public void setLabeledFieldEmptyOrHidden(boolean labeledFieldEmptyOrHidden) {
        this.labeledFieldEmptyOrHidden = labeledFieldEmptyOrHidden;
    }

    /**
     * Header labels aren't really fields, so they append nothing
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     * 
     * KRAD Conversion: Customization of the fields - No use of data dictionary
     */
    public void appendFields(List<Field> fields) {
        // zzzz! zzzz!
    }
    
    /**
     * Does nothing
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    public void populateWithTabIndexIfRequested(int reallyHighIndex) {}
}
