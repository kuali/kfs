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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.renderers.PersistingTagRenderer;
import org.kuali.kfs.sys.document.web.renderers.StringRenderer;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.ui.Field;

/**
 * A class to represent the rendering of a sequence number field
 */
public class AccountingLineViewSequenceNumberField extends FieldTableJoiningWithHeader {
    private String name = KFSConstants.AccountingLineViewStandardBlockNames.SEQUENCE_NUMBER_BLOCK;
    private String newLineLabelProperty = "accounting.line.new.line.sequence.number";

    /**
     * Sequence numbers are always read only
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElementField#isReadOnly()
     */
    public boolean isReadOnly() {
        return true;
    }

    /**
     * Returns the name of this sequence number field
     * @see org.kuali.kfs.sys.document.web.TableJoining#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this sequence number field
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#getHeaderLabelProperty()
     */
    public String getHeaderLabelProperty() {
        return this.name;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.FieldTableJoining#createTableCell()
     */
    @Override
    protected AccountingLineTableCell createTableCell() {
        AccountingLineTableCell cell = super.createTableCell();
        cell.setRendersAsHeader(true);
        return cell;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.kfs.sys.document.web.AccountingLineRenderingContext)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        if (renderingContext.isNewLine()) {
            StringRenderer renderer = new StringRenderer();
            renderer.setStringToRender(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(newLineLabelProperty));
            renderer.render(pageContext, parentTag);
            renderer.clear();
        } else {
            PersistingTagRenderer renderer = new PersistingTagRenderer();
            renderer.setStringToRender(getDisplaySequenceNumber(renderingContext));
            renderer.setValueToPersist(renderingContext.getAccountingLine().getSequenceNumber().toString());
            renderer.setPersistingProperty(renderingContext.getAccountingLinePropertyPath()+".sequenceNumber");
            renderer.render(pageContext, parentTag);
            renderer.clear();
        }
    }
    
    /**
     * Given the rendering context, returns what the sequence number of the line to be rendered is
     * @param renderingContext the rendering context which holds the accounting line
     * @return the sequence number to render (not the one to store as a value)
     */
    protected String getDisplaySequenceNumber(AccountingLineRenderingContext renderingContext) {
        return renderingContext.getAccountingLine().getSequenceNumber().toString();
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#createHeaderLabel()
     */
    public HeaderLabel createHeaderLabel() {
        return new LiteralHeaderLabel("&nbsp;");
    }

    /**
     * sequence number is never really related to lookups, so this implementation does nothing
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     * 
     * KRAD Conversion: Customization of adding the fields - No use of data dictionary
     */
    public void appendFields(List<Field> fields) {
        // take a nap   
    }

    /**
     * Does nothing
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    public void populateWithTabIndexIfRequested(int reallyHighIndex) { }
}
