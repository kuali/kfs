/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.web.struts.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.format.SimpleBooleanFormatter;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLineBase;
import org.kuali.kfs.bo.AccountingLineOverride;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizer;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.ui.AccountingLineDecorator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;


/**
 * This class is the base action form for all financial documents.
 */
public class KualiAccountingDocumentFormBase extends KualiTransactionalDocumentFormBase {
    private SourceAccountingLine newSourceLine;
    private TargetAccountingLine newTargetLine;

    private Map editableAccounts;
    private Map accountingLineEditableFields;

    // TODO: FormFile isn't Serializable, so mark these fields need as transient or create a Serializable subclass of FormFile
    protected FormFile sourceFile;
    protected FormFile targetFile;
    private boolean hideDetails = false;

    private List<AccountingLineDecorator> sourceLineDecorators;
    private List<AccountingLineDecorator> targetLineDecorators;

    private List baselineSourceAccountingLines;
    private List baselineTargetAccountingLines;

    /**
     * This constructor sets up empty instances for the dependent objects...
     */
    public KualiAccountingDocumentFormBase() {
        super();
        setFormatterType("sourceLineDecorator.revertible", SimpleBooleanFormatter.class);
        setFormatterType("targetLineDecorator.revertible", SimpleBooleanFormatter.class);

        // create an empty editableAccounts map, for safety's sake
        editableAccounts = new HashMap();
        accountingLineEditableFields = new HashMap();
        forcedReadOnlyFields = new HashMap();

        // initialize accountingLine lists
        baselineSourceAccountingLines = new ArrayList();
        baselineTargetAccountingLines = new ArrayList();

        // initialize accountingLine decoration lists
        sourceLineDecorators = new ArrayList<AccountingLineDecorator>();
        targetLineDecorators = new ArrayList<AccountingLineDecorator>();
    }

    /**
     * Overrides the parent to call super.populate and then to call the accounting lines populate method that is specific to loading
     * the two select lists on the page.
     * 
     * @see org.kuali.core.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        //
        // now run through all of the accounting lines and make sure they've been uppercased and populated appropriately

        // handle new accountingLine, if one is being added
        String methodToCall = this.getMethodToCall();
        if (StringUtils.isNotBlank(methodToCall)) {
            if (methodToCall.equals(KFSConstants.INSERT_SOURCE_LINE_METHOD)) {
                populateSourceAccountingLine(getNewSourceLine());
            }

            if (methodToCall.equals(KFSConstants.INSERT_TARGET_LINE_METHOD)) {
                populateTargetAccountingLine(getNewTargetLine());
            }
        }

        // don't call populateAccountingLines if you are copying or errorCorrecting a document,
        // since you want the accountingLines in the copy to be "identical" to those in the original
        if (!StringUtils.equals(methodToCall, KFSConstants.COPY_METHOD) && !StringUtils.equals(methodToCall, KFSConstants.ERRORCORRECT_METHOD)) {
            populateAccountingLines();
        }

        setDocTypeName(discoverDocumentTypeName());
    }

    /**
     * Refactored out actually calling the documentAuthorizer methods, since FinancialDocuments call a differently-parameterized
     * version of getEditMode
     * 
     * @param documentAuthorizer
     */
    @Override
    protected void useDocumentAuthorizer(DocumentAuthorizer documentAuthorizer) {
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();

        AccountingDocument financialDocument = (AccountingDocument) getDocument();
        AccountingDocumentAuthorizer financialDocumentAuthorizer = (AccountingDocumentAuthorizer) documentAuthorizer;

        setEditingMode(financialDocumentAuthorizer.getEditMode(financialDocument, kualiUser, getBaselineSourceAccountingLines(), getBaselineTargetAccountingLines()));

        setAccountingLineEditableFields(financialDocumentAuthorizer.getAccountingLineEditableFields(financialDocument, kualiUser));
        setDocumentActionFlags(financialDocumentAuthorizer.getDocumentActionFlags(financialDocument, kualiUser));

        setEditableAccounts(financialDocumentAuthorizer.getEditableAccounts(financialDocument, (ChartUser)kualiUser.getModuleUser( ChartUser.MODULE_ID )));
    }


    /**
     * This method iterates over all of the source lines and all of the target lines in a transactional document, and calls
     * prepareAccountingLineForValidationAndPersistence on each one. This is called because a user could have updated already
     * existing accounting lines that had blank values in composite key fields.
     */
    protected void populateAccountingLines() {
        Iterator sourceLines = getFinancialDocument().getSourceAccountingLines().iterator();
        while (sourceLines.hasNext()) {
            SourceAccountingLine sourceLine = (SourceAccountingLine) sourceLines.next();
            populateSourceAccountingLine(sourceLine);
        }

        Iterator targetLines = getFinancialDocument().getTargetAccountingLines().iterator();
        while (targetLines.hasNext()) {
            TargetAccountingLine targetLine = (TargetAccountingLine) targetLines.next();
            populateTargetAccountingLine(targetLine);
        }
    }

    /**
     * Populates a source accounting line bo using values from the struts form. This is in place to make sure that all of the
     * composite key objects have the correct values in them. This should be overridden by children forms in the situation where
     * document level attributes need to be pushed down into the accounting lines.
     * 
     * @param sourceLine
     */
    public void populateSourceAccountingLine(SourceAccountingLine sourceLine) {
        populateAccountingLine(sourceLine);
    }

    /**
     * Populates a target accounting line bo using values from the struts form. This is in place to make sure that all of the
     * composite key objects have the correct values in them. This should be overridden by children forms in the situation where
     * document level attributes need to be pushed down into the accounting lines.
     * 
     * @param targetLine
     */
    public void populateTargetAccountingLine(TargetAccountingLine targetLine) {
        populateAccountingLine(targetLine);
    }

    /**
     * Populates the dependent fields of objects contained within the given accountingLine
     * 
     * @param line
     */
    private void populateAccountingLine(AccountingLineBase line) {
        SpringServiceLocator.getBusinessObjectDictionaryService().performForceUppercase(line);

        line.setDocumentNumber(getDocument().getDocumentNumber());

        if (ObjectUtils.isNull(line.getAccount())) {
            line.setAccount(new Account());
        }
        line.getAccount().setChartOfAccountsCode(line.getChartOfAccountsCode());

        if (ObjectUtils.isNull(line.getObjectCode())) {
            line.setObjectCode(new ObjectCode());
        }
        line.getObjectCode().setUniversityFiscalYear(getFinancialDocument().getPostingYear());
        line.getObjectCode().setChartOfAccountsCode(line.getChartOfAccountsCode());

        if (ObjectUtils.isNull(line.getSubAccount())) {
            line.setSubAccount(new SubAccount());
        }
        line.getSubAccount().setChartOfAccountsCode(line.getChartOfAccountsCode());
        line.getSubAccount().setAccountNumber(line.getAccountNumber());

        if (ObjectUtils.isNull(line.getSubObjectCode())) {
            line.setSubObjectCode(new SubObjCd());
        }
        line.getSubObjectCode().setChartOfAccountsCode(line.getChartOfAccountsCode());
        line.getSubObjectCode().setAccountNumber(line.getAccountNumber());
        line.getSubObjectCode().setFinancialObjectCode(line.getFinancialObjectCode());
        line.getSubObjectCode().setUniversityFiscalYear(getFinancialDocument().getPostingYear());

        AccountingLineOverride.populateFromInput(line);
    }

    /**
     * This method retrieves an instance of the form.
     * 
     * @return
     */
    public AccountingDocument getFinancialDocument() {
        return (AccountingDocument) getDocument();
    }

    /**
     * @return Returns the newTargetLine.
     */
    public TargetAccountingLine getNewTargetLine() {
        if (newTargetLine == null) {
            newTargetLine = createNewTargetAccountingLine(getFinancialDocument());
        }
        return newTargetLine;
    }

    /**
     * @param newExpenseLine The newTargetLine to set.
     */
    public void setNewTargetLine(TargetAccountingLine newExpenseLine) {
        this.newTargetLine = newExpenseLine;
    }

    /**
     * @return Returns the newSourceLine.
     */
    public SourceAccountingLine getNewSourceLine() {
        if (newSourceLine == null) {
            newSourceLine = createNewSourceAccountingLine(getFinancialDocument());
        }
        return newSourceLine;
    }

    /**
     * @param newIncomeLine The newSourceLine to set.
     */
    public void setNewSourceLine(SourceAccountingLine newIncomeLine) {
        this.newSourceLine = newIncomeLine;
    }

    /**
     * @return Returns the sourceFile.
     */
    public FormFile getSourceFile() {
        return sourceFile;
    }

    /**
     * @param sourceFile The sourceFile to set.
     */
    public void setSourceFile(FormFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * @return Returns the targetFile.
     */
    public FormFile getTargetFile() {
        return targetFile;
    }

    /**
     * @param targetFile The targetFile to set.
     */
    public void setTargetFile(FormFile targetFile) {
        this.targetFile = targetFile;
    }


    /**
     * @return current Map of editableAccounts
     */
    public Map getEditableAccounts() {
        return editableAccounts;
    }

    /**
     * @param editableAccounts the account Map to set
     */
    public void setEditableAccounts(Map editableAccounts) {
        this.editableAccounts = editableAccounts;
    }

    /**
     * @return Returns the editableFields.
     */
    public Map getAccountingLineEditableFields() {
        return accountingLineEditableFields;
    }

    /**
     * @param editableFields The editableFields to set.
     */
    public void setAccountingLineEditableFields(Map editableFields) {
        this.accountingLineEditableFields = editableFields;
    }


    /**
     * 
     * @return hideDetails attribute
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * 
     * @return hideDetails attribute
     * @see #isHideDetails()
     */
    public boolean getHideDetails() {
        return isHideDetails();
    }

    /**
     * 
     * @param hideDetails
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }


    /**
     * @return current List of baseline SourceAccountingLines for use in update-event generation
     */
    public List getBaselineSourceAccountingLines() {
        return baselineSourceAccountingLines;
    }

    /**
     * Sets the current List of baseline SourceAccountingLines to the given List
     * 
     * @param baselineSourceAccountingLines
     */
    public void setBaselineSourceAccountingLines(List baselineSourceAccountingLines) {
        this.baselineSourceAccountingLines = baselineSourceAccountingLines;
    }

    /**
     * @param index
     * @return true if a baselineSourceAccountingLine with the given index exists
     */
    public boolean hasBaselineSourceAccountingLine(int index) {
        boolean has = false;

        if ((index >= 0) && (index <= baselineSourceAccountingLines.size())) {
            has = true;
        }

        return has;
    }

    /**
     * Implementation creates empty SourceAccountingLines as a side-effect, so that Struts' efforts to set fields of lines which
     * haven't been created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * @return baseline SourceAccountingLine at the given index
     */
    public SourceAccountingLine getBaselineSourceAccountingLine(int index) {
        try {
            while (baselineSourceAccountingLines.size() <= index) {
                baselineSourceAccountingLines.add(getFinancialDocument().getSourceAccountingLineClass().newInstance());
            }
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to get new source line instance for document" + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to get new source line instance for document" + e.getMessage());
        }

        return (SourceAccountingLine) baselineSourceAccountingLines.get(index);
    }


    /**
     * @return current List of baseline TargetAccountingLines for use in update-event generation
     */
    public List getBaselineTargetAccountingLines() {
        return baselineTargetAccountingLines;
    }

    /**
     * Sets the current List of baseline TargetAccountingLines to the given List
     * 
     * @param baselineTargetAccountingLines
     */
    public void setBaselineTargetAccountingLines(List baselineTargetAccountingLines) {
        this.baselineTargetAccountingLines = baselineTargetAccountingLines;
    }


    /**
     * @param index
     * @return true if a baselineTargetAccountingLine with the given index exists
     */
    public boolean hasBaselineTargetAccountingLine(int index) {
        boolean has = false;

        if ((index >= 0) && (index <= baselineTargetAccountingLines.size())) {
            has = true;
        }

        return has;
    }

    /**
     * Implementation creates empty TargetAccountingLines as a side-effect, so that Struts' efforts to set fields of lines which
     * haven't been created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * 
     * @return baseline TargetAccountingLine at the given index
     */
    public TargetAccountingLine getBaselineTargetAccountingLine(int index) {
        try {
            while (baselineTargetAccountingLines.size() <= index) {
                baselineTargetAccountingLines.add(getFinancialDocument().getTargetAccountingLineClass().newInstance());
            }
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to get new target line instance for document" + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to get new target line instance for document" + e.getMessage());
        }

        return (TargetAccountingLine) baselineTargetAccountingLines.get(index);
    }


    /**
     * @return current List of SourceAccountingLine decorations
     */
    public List<AccountingLineDecorator> getSourceLineDecorators() {
        return sourceLineDecorators;
    }

    /**
     * @param minSize
     * @return current List of SourceAccountingLine decorations, expanded to have at least minSize elements
     */
    public List<AccountingLineDecorator> getSourceLineDecorators(int minSize) {
        extendSourceLineDecorators(minSize);

        return sourceLineDecorators;
    }

    /**
     * Adds default AccountingLineDecorators to sourceAccountingLineDecorators until it contains at least minSize elements
     * 
     * @param minSize
     */
    private void extendSourceLineDecorators(int minSize) {
        while (sourceLineDecorators.size() < minSize) {
            sourceLineDecorators.add(new AccountingLineDecorator());
        }
    }

    /**
     * Sets the current List of SourceAccountingLine decorators
     * 
     * @param sourceLineDecorators
     */
    public void setSourceLineDecorators(List<AccountingLineDecorator> sourceLineDecorators) {
        this.sourceLineDecorators = sourceLineDecorators;
    }

    /**
     * Implementation creates empty AccountingLineDecorators as a side-effect, so that Struts' efforts to set fields of lines which
     * haven't been created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * 
     * @return AccountingLineDecorators for sourceLine at the given index
     */
    public AccountingLineDecorator getSourceLineDecorator(int index) {
        extendSourceLineDecorators(index + 1);

        return sourceLineDecorators.get(index);
    }


    /**
     * @return current List of TargetAccountingLine decorators
     */
    public List<AccountingLineDecorator> getTargetLineDecorators() {
        return targetLineDecorators;
    }

    /**
     * @param minSize
     * @return current List of TargetAccountingLine decorators, expanded to have at least minSize elements
     */
    public List<AccountingLineDecorator> getTargetLineDecorators(int minSize) {
        extendTargetLineDecorators(minSize);

        return targetLineDecorators;
    }

    /**
     * Adds default AccountingLineDecorators to targetAccountingLineDecorators until it contains at least minSize elements
     * 
     * @param minSize
     */
    private void extendTargetLineDecorators(int minSize) {
        while (targetLineDecorators.size() < minSize) {
            targetLineDecorators.add(new AccountingLineDecorator());
        }
    }

    /**
     * Sets the current List of TargetAccountingLine decorators
     * 
     * @param targetLineDecorators
     */
    public void setTargetLineDecorators(List<AccountingLineDecorator> targetLineDecorators) {
        this.targetLineDecorators = targetLineDecorators;
    }

    /**
     * Implementation creates empty AccountingLineDecorators as a side-effect, so that Struts' efforts to set fields of lines which
     * haven't been created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * 
     * @return AccountingLineDecorator for targetLine at the given index
     */
    public AccountingLineDecorator getTargetLineDecorator(int index) {
        extendTargetLineDecorators(index + 1);

        return targetLineDecorators.get(index);
    }


    /**
     * Resets the source accounting line decorators to new and ensures that there are the given number. These decorators take very
     * little memory, there are few of them on the page, and they are rarely reset, so this method does it the simple way.
     * 
     * @param size
     */
    public void resetSourceLineDecorators(int size) {
        sourceLineDecorators.clear();
        extendSourceLineDecorators(size);
    }

    /**
     * Resets the target accounting line decorators to new and ensures that there are the given number. These decorators take very
     * little memory, there are few of them on the page, and they are rarely reset, so this method does it the simple way.
     * 
     * @param size
     */
    public void resetTargetLineDecorators(int size) {
        targetLineDecorators.clear();
        extendTargetLineDecorators(size);
    }

    /**
     * Retrieves the source accounting lines total in a currency format with commas.
     * 
     * @return String
     */
    public String getCurrencyFormattedSourceTotal() {
        return (String) new CurrencyFormatter().format(getFinancialDocument().getSourceTotal());
    }

    /**
     * Retrieves the source accounting lines total in a currency format with commas.
     * 
     * @return String
     */
    public String getCurrencyFormattedTargetTotal() {
        return (String) new CurrencyFormatter().format(getFinancialDocument().getTargetTotal());
    }

    /**
     * TODO this has to be fixed for p2a: KULRNE-4552
     * @return String
     */
    public String getAccountingLineImportInstructionsUrl() {
        return "https://test.kuali.org/confluence/display/KULRNE/Accounting+Line+Import+Instructions";
    }

    /**
     * This method formats the given java.sql.Date as MMM d, yyyy.
     * 
     * @param reversalDate
     * 
     * @return String
     */
    protected static String formatReversalDate(java.sql.Date reversalDate) {
        if (reversalDate == null) {
            return "";
        }
        // new for thread safety
        return new SimpleDateFormat("MMM d, yyyy").format(reversalDate);
    }

    /**
     * 
     * @param financialDocument
     * @return a new source accounting line for the document
     */
    protected SourceAccountingLine createNewSourceAccountingLine(AccountingDocument financialDocument) {
        if (financialDocument == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        try {
            return (SourceAccountingLine) financialDocument.getSourceAccountingLineClass().newInstance();
        }
        catch (Exception e) {
            throw new InfrastructureException("unable to create a new source accounting line", e);
        }
    }

    /**
     * 
     * @param financialDocument
     * @return a new target accounting line for the documet
     */
    protected TargetAccountingLine createNewTargetAccountingLine(AccountingDocument financialDocument) {
        if (financialDocument == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        try {
            return (TargetAccountingLine) financialDocument.getTargetAccountingLineClass().newInstance();
        }
        catch (Exception e) {
            throw new InfrastructureException("unable to create a new target accounting line", e);
        }
    }
}
