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
