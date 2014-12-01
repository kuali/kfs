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
