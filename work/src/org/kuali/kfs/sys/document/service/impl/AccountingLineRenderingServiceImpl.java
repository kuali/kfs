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
package org.kuali.kfs.sys.document.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationController;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewFieldDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineAuthorizationTransformer;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingTransformation;
import org.kuali.kfs.sys.document.service.AccountingLineTableTransformation;
import org.kuali.kfs.sys.document.web.AccountingLineTableRow;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.kfs.sys.document.web.renderers.CheckboxRenderer;
import org.kuali.kfs.sys.document.web.renderers.CurrencyRenderer;
import org.kuali.kfs.sys.document.web.renderers.DateRenderer;
import org.kuali.kfs.sys.document.web.renderers.DropDownRenderer;
import org.kuali.kfs.sys.document.web.renderers.FieldRenderer;
import org.kuali.kfs.sys.document.web.renderers.HiddenRenderer;
import org.kuali.kfs.sys.document.web.renderers.RadioButtonGroupRenderer;
import org.kuali.kfs.sys.document.web.renderers.ReadOnlyRenderer;
import org.kuali.kfs.sys.document.web.renderers.TextAreaRenderer;
import org.kuali.kfs.sys.document.web.renderers.TextRenderer;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.DateValidationPattern;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;

/**
 * The default implementation of the AccountingLineRenderingService
 */
public class AccountingLineRenderingServiceImpl implements AccountingLineRenderingService {
    protected final String KUALI_FORM_NAME = "KualiForm";
    
    private List<AccountingLineFieldRenderingTransformation> fieldTransformations;
    private DataDictionaryService dataDictionaryService;
    private AccountingLineAuthorizationTransformer accountingLineAuthorizationTransformer;
    private List<AccountingLineRenderingTransformation> preTablificationTransformations;
    private List<AccountingLineTableTransformation> postTablificationTransformations;
    private DocumentHelperService documentHelperService;

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRenderingService#performPreTablificationTransformations(java.util.List, org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition, org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean, java.util.Map, java.lang.String)
     */
    public void performPreTablificationTransformations(List<TableJoining> elements, AccountingLineGroupDefinition groupDefinition, AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Map unconvertedValues, String accountingLinePropertyName) {
        performAuthorizationTransformations(elements, groupDefinition, accountingDocument, accountingLine, newLine, accountingLinePropertyName);
        performFieldTransformations(elements, accountingDocument, accountingLine, unconvertedValues);
        for (AccountingLineRenderingTransformation transformation : preTablificationTransformations) {
            transformation.transformElements(elements, accountingLine);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRenderingService#performPostTablificationTransformations(java.util.List, org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition, org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean)
     */
    public void performPostTablificationTransformations(List<AccountingLineTableRow> rows, AccountingLineGroupDefinition groupDefinition, AccountingDocument document, AccountingLine accountingLine, boolean newLine) {
        for (AccountingLineTableTransformation transformation : postTablificationTransformations) {
            transformation.transformRows(rows);
        }
    }


    /**
     * Performs the authorization transformations
     * @param elements the layout elements which we are authorizing
     * @param accountingLineGroupDefinition the data dictionary definition of the accounting line group
     * @param accountingDocument the accounting line document we're rendering accounting lines for
     * @param accountingLine the accounting line we're rendering
     * @param newLine true if the accounting line is not yet on the form yet, false otherwise
     */
    protected void performAuthorizationTransformations(List<TableJoining> elements, AccountingLineGroupDefinition accountingLineGroupDefinition, AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, String accountingLinePropertyName) {
        accountingLineAuthorizationTransformer.transformElements(elements, accountingLine, accountingDocument, accountingLineGroupDefinition.getAccountingLineAuthorizer(), newLine, accountingLinePropertyName);
    }
    
    /**
     * Performs field transformations for pre-rendering
     * @param elements the layout elements that hold fields to transform
     * @param accountingDocument the accounting document with the line we are rendering
     * @param accountingLine the accounting line we are rendering
     * @param unconvertedValues any unconverted values
     */
    protected void performFieldTransformations(List<TableJoining> elements, AccountingDocument accountingDocument, AccountingLine accountingLine, Map unconvertedValues) {
        for (TableJoining layoutElement : elements) {
            layoutElement.performFieldTransformations(fieldTransformations, accountingLine, unconvertedValues);
        }
    }
 
    /**
     * Creates an accounting document authorizer for the given accounting document
     * @param document the document to get an authorizer for
     * @return an authorizer for the document
     */
    protected FinancialSystemTransactionalDocumentAuthorizerBase getDocumentAuthorizer(AccountingDocument document) {
        final FinancialSystemTransactionalDocumentAuthorizerBase authorizer = (FinancialSystemTransactionalDocumentAuthorizerBase) getDocumentHelperService().getDocumentAuthorizer(document);
        return authorizer;
    }
    
    /**
     * @param document the document to get the presentation controller for
     * @return the proper presentation controller
     */
    protected FinancialSystemTransactionalDocumentPresentationController getPresentationController(AccountingDocument document) {
        final FinancialSystemTransactionalDocumentPresentationController presentationController = (FinancialSystemTransactionalDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(document);
        return presentationController;
    }

    /**
     * Simplify the tree so that it is made up of only table elements and fields
     * @see org.kuali.kfs.sys.document.service.AccountingLineRenderingService#tablify(java.util.List)
     */
    public List<AccountingLineTableRow> tablify(List<TableJoining> elements) {
        List<AccountingLineTableRow> rows = createBlankTableRows(getMaxRowCount(elements));
        tablifyElements(elements, rows);
        return rows;
    }
    
    /**
     * Gets the maximum number of rows needed by any child element
     * @param elements the elements to turn into table rows
     * @return the maximum number of rows requested
     */
    protected int getMaxRowCount(List<TableJoining> elements) {
        int maxRowCount = 0;
        for (TableJoining element : elements) {
            int rowCount = element.getRequestedRowCount();
            if (rowCount > maxRowCount) {
                maxRowCount = rowCount;
            }
        }
        return maxRowCount;
    }
    
    /**
     * This method creates a List of blank table rows, based on the requested count
     * @param count the count of table rows
     * @return a List of table rows ready for population
     */
    protected List<AccountingLineTableRow> createBlankTableRows(int count) {
        List<AccountingLineTableRow> rows = new ArrayList<AccountingLineTableRow>();
        for (int i = 0; i < count; i++) {
            rows.add(new AccountingLineTableRow());
        }
        return rows;
    }

    /**
     * Requests each of the given elements to join the table
     * @param elements the elements to join to the table
     * @param rows the table rows to join to
     */
    protected void tablifyElements(List<TableJoining> elements, List<AccountingLineTableRow> rows) {
        for (TableJoining element : elements) {
            element.joinTable(rows);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRenderingService#getFieldRendererForField(org.kuali.rice.kns.web.ui.Field, org.kuali.kfs.sys.businessobject.AccountingLine)
     * 
     * KRAD Conversion: Performs customization of the renderer based on the properties of the fields.
     */
    public FieldRenderer getFieldRendererForField(Field field, AccountingLine accountingLineToRender) {
        FieldRenderer renderer = null;

        if (field.isReadOnly() || field.getFieldType().equals(Field.READONLY)) {
            renderer = new ReadOnlyRenderer();
        } /* 
        else if (field.getPropertyName().equals(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME) && !SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            // the special case for rendering chart of accounts code when accounts can't cross charts
            renderer = new DynamicReadOnlyRender();   
        } */
        else if (field.getFieldType().equals(Field.TEXT)) {
            if (field.isDatePicker() || usesDateValidation(field.getPropertyName(), accountingLineToRender)) { // are we a date?
                renderer = new DateRenderer();
            } else {
                renderer = new TextRenderer();
            }
        } else if (field.getFieldType().equals(Field.TEXT_AREA)) {
            renderer = new TextAreaRenderer();
        } else if (field.getFieldType().equals(Field.HIDDEN)) {
            renderer = new HiddenRenderer();
        } else if (field.getFieldType().equals(Field.CURRENCY)) {
            renderer = new CurrencyRenderer();
        } else if (field.getFieldType().equals(Field.DROPDOWN)) {
            renderer = new DropDownRenderer();
        } else if (field.getFieldType().equals(Field.RADIO)) {
            renderer = new RadioButtonGroupRenderer();
        } else if (field.getFieldType().equals(Field.CHECKBOX)) {
            renderer = new CheckboxRenderer();
        }
        
        return renderer;
    }
    
    /**
     * Determines if this method uses a date validation pattern, in which case, a date picker should be rendered
     * @param propertyName the property of the field being checked from the command line
     * @param accountingLineToRender the accounting line which is being rendered
     * @return true if the property does use date validation, false otherwise
     */
    protected boolean usesDateValidation(String propertyName, Object businessObject) {
        final org.kuali.rice.krad.datadictionary.BusinessObjectEntry entry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(businessObject.getClass().getName());
        AttributeDefinition attributeDefinition = entry.getAttributeDefinition(propertyName);
        
        if (attributeDefinition == null) {
            if (!propertyName.contains(".")) return false;
            final int firstNestingPoint = propertyName.indexOf(".");
            final String toNestingPoint = propertyName.substring(0, firstNestingPoint);
            final String fromNestingPoint = propertyName.substring(firstNestingPoint+1);
            Object childObject = null;
            try {
                final Class childClass = PropertyUtils.getPropertyType(businessObject, toNestingPoint);
                childObject = childClass.newInstance();
            }
            catch (IllegalAccessException iae) {
                new UnsupportedOperationException(iae);
            }
            catch (InvocationTargetException ite) {
                new UnsupportedOperationException(ite);
            }
            catch (NoSuchMethodException nsme) {
                new UnsupportedOperationException(nsme);
            }
            catch (InstantiationException ie) {
                throw new UnsupportedOperationException(ie);
            }
            return usesDateValidation(fromNestingPoint, childObject);
        }
        
        final ValidationPattern validationPattern = attributeDefinition.getValidationPattern();
        if (validationPattern == null) return false; // no validation for sure means we ain't using date validation
        return validationPattern instanceof DateValidationPattern;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRenderingService#createGenericAccountingLineViewFieldDefinition(org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition)
     */
    public AccountingLineViewFieldDefinition createGenericAccountingLineViewFieldDefinition(MaintainableFieldDefinition currentDefinition) {
        AccountingLineViewFieldDefinition fieldDefinition = new AccountingLineViewFieldDefinition();
        
        fieldDefinition.setRequired(currentDefinition.isRequired());
        fieldDefinition.setUnconditionallyReadOnly(currentDefinition.isUnconditionallyReadOnly());
        fieldDefinition.setReadOnlyAfterAdd(currentDefinition.isReadOnlyAfterAdd());
        fieldDefinition.setNoLookup(currentDefinition.isNoLookup());
        
        fieldDefinition.setDefaultValue(currentDefinition.getDefaultValue());
        fieldDefinition.setTemplate(currentDefinition.getTemplate());
        fieldDefinition.setDefaultValueFinderClass(currentDefinition.getDefaultValueFinderClass());
        
        fieldDefinition.setOverrideLookupClass(currentDefinition.getOverrideLookupClass());
        fieldDefinition.setOverrideFieldConversions(currentDefinition.getOverrideFieldConversions());
        
        return fieldDefinition;
    }

    /**
     * Gets the fieldTransformations attribute. 
     * @return Returns the fieldTransformations.
     */
    public List<AccountingLineFieldRenderingTransformation> getFieldTransformations() {
        return fieldTransformations;
    }

    /**
     * Sets the fieldTransformations attribute value.
     * @param fieldTransformations The fieldTransformations to set.
     */
    public void setFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations) {
        this.fieldTransformations = fieldTransformations;
    }

    /**
     * Gets the accountingLineAuthorizationTransformer attribute. 
     * @return Returns the accountingLineAuthorizationTransformer.
     */
    public AccountingLineAuthorizationTransformer getAccountingLineAuthorizationTransformer() {
        return accountingLineAuthorizationTransformer;
    }

    /**
     * Sets the accountingLineAuthorizationTransformer attribute value.
     * @param accountingLineAuthorizationTransformer The accountingLineAuthorizationTransformer to set.
     */
    public void setAccountingLineAuthorizationTransformer(AccountingLineAuthorizationTransformer accountingLineAuthorizationTransformer) {
        this.accountingLineAuthorizationTransformer = accountingLineAuthorizationTransformer;
    }

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the postTablificationTransformations attribute. 
     * @return Returns the postTablificationTransformations.
     */
    public List<AccountingLineTableTransformation> getPostTablificationTransformations() {
        return postTablificationTransformations;
    }

    /**
     * Sets the postTablificationTransformations attribute value.
     * @param postTablificationTransformations The postTablificationTransformations to set.
     */
    public void setPostTablificationTransformations(List<AccountingLineTableTransformation> postTablificationTransformations) {
        this.postTablificationTransformations = postTablificationTransformations;
    }

    /**
     * Gets the preTablificationTransformations attribute. 
     * @return Returns the preTablificationTransformations.
     */
    public List<AccountingLineRenderingTransformation> getPreTablificationTransformations() {
        return preTablificationTransformations;
    }

    /**
     * Sets the preTablificationTransformations attribute value.
     * @param preTablificationTransformations The preTablificationTransformations to set.
     */
    public void setPreTablificationTransformations(List<AccountingLineRenderingTransformation> preTablificationTransformations) {
        this.preTablificationTransformations = preTablificationTransformations;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRenderingService#findForm(javax.servlet.jsp.PageContext)
     */
    public KualiAccountingDocumentFormBase findForm(PageContext pageContext) {
        if (pageContext.getRequest().getAttribute(KUALI_FORM_NAME) != null) return (KualiAccountingDocumentFormBase)pageContext.getRequest().getAttribute(KUALI_FORM_NAME);
        
        if (pageContext.getSession().getAttribute(KUALI_FORM_NAME) != null) return (KualiAccountingDocumentFormBase)pageContext.getSession().getAttribute(KUALI_FORM_NAME);
        
        return (KualiAccountingDocumentFormBase)KNSGlobalVariables.getKualiForm();
    }
    
    protected DocumentHelperService getDocumentHelperService() {
        if (documentHelperService == null) {
            documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        }
        return documentHelperService;
    }
}

