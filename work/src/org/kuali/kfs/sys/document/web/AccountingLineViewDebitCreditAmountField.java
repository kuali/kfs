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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.fp.document.web.struts.VoucherForm;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewFieldDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.kfs.sys.document.web.renderers.FieldRenderer;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 * This class...
 */
public class AccountingLineViewDebitCreditAmountField implements RenderableElement, ElementNamable {
    private Field debitOrCreditField;
    private AccountingLineViewFieldDefinition definition;
    private boolean isDebit;
    private String newLineProperty;
    private String collectionProperty;
    private int arbitrarilyHighIndex;
    
    /**
     * Constructs a AccountingLineViewDebitOrCreditAmountField
     * @param debitOrCreditField
     * @param definition
     * @param isDebit
     * @param newLineProperty
     * @param collectionProperty
     */
    public AccountingLineViewDebitCreditAmountField(Field debitOrCreditField, AccountingLineViewFieldDefinition definition, boolean isDebit, String newLineProperty, String collectionProperty) {
        this.debitOrCreditField = debitOrCreditField;
        this.definition = definition;
        this.isDebit = isDebit;
        this.newLineProperty = newLineProperty;
        this.collectionProperty = collectionProperty;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFields(java.util.List)
     * 
     * KRAD Conversion: Customization of the fields - Adding fields - No use of data dictionary
     */
    public void appendFields(List<Field> fields) {
        fields.add(debitOrCreditField);
    }

    /**
     * This is not an action block
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isActionBlock()
     */
    public boolean isActionBlock() {
        return false;
    }

    /**
     * This isn't empty
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isEmpty()
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * This is not hidden
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int)
     */
    public void populateWithTabIndexIfRequested(int reallyHighIndex) {
        this.arbitrarilyHighIndex = reallyHighIndex;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.kfs.sys.document.web.AccountingLineRenderingContext)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        if (!renderingContext.isFieldModifyable(debitOrCreditField.getPropertyName())) {
            debitOrCreditField.setReadOnly(true);
        }
        FieldRenderer renderer = SpringContext.getBean(AccountingLineRenderingService.class).getFieldRendererForField(getDebitOrCreditField(), renderingContext.getAccountingLine());
        if (renderer != null) {
            prepareFieldForRendering(getDebitOrCreditField(), (VoucherForm)renderingContext.getForm(), renderingContext.getCurrentLineCount());
            renderer.setField(getDebitOrCreditField());
            renderer.render(pageContext, parentTag);
            renderer.clear();
        }
    }
    
    /**
     * Sets up the field for rendering by setting the right property name and zeroing out amounts which aren't needed
     * @param field the field to prepare
     * @param accountingLine the accounting line being rendered
     * @param count the count of the current line in the source lines, or null if it's a new line
     * 
     * KRAD Conversion: Customization of preparing the fields for rendering - No use of data dictionary
     */
    protected void prepareFieldForRendering(Field field, VoucherForm form, Integer count) {
        getDebitOrCreditField().setPropertyPrefix(null);
        
        // set the right property name
        if (count == null) {
            field.setPropertyName(getNewLineProperty());
        } else {
            final String subPropertyName = isDebit ? "debit" : "credit";
            field.setPropertyName(getCollectionProperty()+"["+count.toString()+"]."+subPropertyName);
        }
        
        // get the value from the form
        field.setPropertyValue(ObjectUtils.getPropertyValue(form, field.getPropertyName()));
    }

    /**
     * Gets the arbitrarilyHighIndex attribute. 
     * @return Returns the arbitrarilyHighIndex.
     */
    public int getArbitrarilyHighIndex() {
        return arbitrarilyHighIndex;
    }

    /**
     * Gets the collectionProperty attribute. 
     * @return Returns the collectionProperty.
     */
    public String getCollectionProperty() {
        return collectionProperty;
    }

    /**
     * Gets the debitOrCreditField attribute. 
     * @return Returns the debitOrCreditField.
     */
    public Field getDebitOrCreditField() {
        return debitOrCreditField;
    }

    /**
     * Gets the definition attribute. 
     * @return Returns the definition.
     */
    public AccountingLineViewFieldDefinition getDefinition() {
        return definition;
    }

    /**
     * Gets the isDebit attribute. 
     * @return Returns the isDebit.
     */
    public boolean isDebit() {
        return isDebit;
    }

    /**
     * Gets the newLineProperty attribute. 
     * @return Returns the newLineProperty.
     */
    public String getNewLineProperty() {
        return newLineProperty;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.ElementNamable#getName()
     */
    public String getName() {
        return this.getDebitOrCreditField().getPropertyName();
    }

}
