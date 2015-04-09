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
package org.kuali.kfs.sys.document.service;

import java.util.List;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewFieldDefinition;
import org.kuali.kfs.sys.document.web.AccountingLineTableRow;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.kfs.sys.document.web.renderers.FieldRenderer;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.web.ui.Field;

/**
 * Service that helps render accounting lines
 */
public interface AccountingLineRenderingService {
    
    /**
     * Given a list of renderable elements, determines how to split that into rows, cells, and fields 
     * @param elements renderable elements to find table form for 
     * @return a list of table rows that can be rendered
     */
    public abstract List<AccountingLineTableRow> tablify(List<TableJoining> elements);
    
    /**
     * Performs any known transformations against the List of AccountingLineViewRenderableElements
     * @param elements the List of elements to transform
     * @param definition the accounting line group definition that gives instructions to the particular rendering we're attempting
     * @param document the Accounting Document we're rendering lines from
     * @param accountingLine the line we're rendering
     * @param newLine true if what is being rendered is the new line in the form; false otherwise
     * @param unconvertedValues any unconverted values stored in the form
     * @param accountingLinePropertyName the property path to this accounting line
     */
    public abstract void performPreTablificationTransformations(List<TableJoining> elements, AccountingLineGroupDefinition groupDefinition, AccountingDocument document, AccountingLine accountingLine, boolean newLine, Map unconvertedValues, String accountingLinePropertyName);
    
    /**
     * Performs any transformations that should happen after tablification
     * @param rows the tablified rows
     * @param groupDefinition the data dictionary definition of the group to render
     * @param document the Accounting Document we're rendering lines from
     * @param accountingLine the line we're rendering the line which is being rendered
     * @param newLine true if what is being rendered is the new line in the form; false otherwise
     */
    public abstract void performPostTablificationTransformations(List<AccountingLineTableRow> rows, AccountingLineGroupDefinition groupDefinition, AccountingDocument document, AccountingLine accountingLine, boolean newLine);
    
    /**
     * Looks in likely places to find the form that is used by the page context for rendering an accounting document
     * @param pageContext the pageContext to find the form in
     * @return the form for the page being rendered
     */
    public abstract KualiAccountingDocumentFormBase findForm(PageContext pageContext);
    
    /**
     * Based on the control type of the field, returns a proper field renderer
     * @return the field renderer which will properly display this field
     */
    public abstract FieldRenderer getFieldRendererForField(Field field, AccountingLine accountingLineToRender);
    
    /**
     * Begins to create an AccountingLineViewFieldDefinition, based on the information held within the given MaintainableFieldDefinition
     * @param fieldDefinition the field definition to create a generic accounting line view field version of
     * @return a basic AccountingLineViewFieldDefinition
     */
    public abstract AccountingLineViewFieldDefinition createGenericAccountingLineViewFieldDefinition(MaintainableFieldDefinition fieldDefinition);
}
