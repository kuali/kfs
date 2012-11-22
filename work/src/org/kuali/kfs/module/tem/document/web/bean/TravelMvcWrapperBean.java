/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.bean;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TemDistributionAccountingLine;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Interface intended to decouple the MVC framework from the classes used therein and Spring. This allows button actions to interact
 * with the Spring IOC and the SOA without coupling with the MVC framework like Struts. Ooops...I said it.
 * 
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public interface TravelMvcWrapperBean {

    Integer getTravelerId();

    TravelDocument getTravelDocument();

    void setTravelerId(Integer travelerId);

    Integer getTempTravelerId();

    void setTempTravelerId(Integer tempTravelerId);

    /**
     * Gets the empPrincipalId attribute.
     * 
     * @return Returns the empPrincipalId.
     */
    String getEmpPrincipalId();

    /**
     * Sets the empPrincipalId attribute value.
     * 
     * @param empPrincipalId The empPrincipalId to set.
     */
    void setEmpPrincipalId(String empPrincipalId);

    /**
     * Gets the tempEmpPrincipalId attribute.
     * 
     * @return Returns the tempEmpPrincipalId.
     */
    String getTempEmpPrincipalId();

    /**
     * Sets the tempEmpPrincipalId attribute value.
     * 
     * @param tempEmpPrincipalId The tempEmpPrincipalId to set.
     */
    void setTempEmpPrincipalId(String tempEmpPrincipalId);

    Map<String, String> getModesOfTransportation();

    /**
     * Gets the showLodging attribute.
     * 
     * @return Returns the showLodging.
     */
    boolean isShowLodging();

    /**
     * Sets the showLodging attribute value.
     * 
     * @param showLodging The showLodging to set.
     */
    void setShowLodging(boolean showLodging);

    /**
     * Gets the showMileage attribute.
     * 
     * @return Returns the showMileage.
     */
    boolean isShowMileage();

    /**
     * Sets the showMileage attribute value.
     * 
     * @param showMileage The showMileage to set.
     */
    void setShowMileage(boolean showMileage);

    /**
     * Gets the showPerDiem attribute.
     * 
     * @return Returns the showPerDiem.
     */
    boolean isShowPerDiem();

    /**
     * Gets the canReturn attribute value.
     * 
     * @return canReturn The canReturn to set.
     */
    boolean canReturn();

    /**
     * Sets the canReturn attribute value.
     * 
     * @param canReturn The canReturn to set.
     */
    void setCanReturn(final boolean canReturn);

    /**
     * Sets the showPerDiem attribute value.
     * 
     * @param showPerDiem The showPerDiem to set.
     */
    void setShowPerDiem(boolean showPerDiem);

    boolean isShowAllPerDiemCategories();

    /**
     * This method takes a string parameter from the db and converts it to an int suitable for using in our calculations
     * 
     * @param perDiemPercentage
     */
    void setPerDiemPercentage(String perDiemPercentage);

    /**
     * Gets the perDiemPercentage attribute.
     * 
     * @return Returns the perDiemPercentage.
     */
    int getPerDiemPercentage();

    /**
     * Sets the perDiemPercentage attribute value.
     * 
     * @param perDiemPercentage The perDiemPercentage to set.
     */
    void setPerDiemPercentage(int perDiemPercentage);

    /**
     * Retrieves a {@link Collection} of related documents from the MVC. Each {@link Collection} of {@link Document} instances is
     * mapped by document type name
     * 
     * @returns {@link Collection} instances mapped to document type name
     */
    Map<String, List<Document>> getRelatedDocuments();

    void setRelatedDocuments(Map<String, List<Document>> relatedDocuments);

    /**
     * Gets the relatedDocumentNotes attribute.
     * 
     * @return Returns the relatedDocumentNotes.
     */
    Map<String, List<Note>> getRelatedDocumentNotes();

    /**
     * Sets the relatedDocumentNotes attribute value.
     * 
     * @param relatedDocumentNotes The relatedDocumentNotes to set.
     */
    void setRelatedDocumentNotes(Map<String, List<Note>> relatedDocumentNotes);

    /**
     * Determines if the {@link TravelDocument} has been calculated yet.
     * 
     * @return true if the {@link TravelDocument} calculated flag has been set
     */
    boolean isCalculated();

    /**
     * Sets the calculated status of the {@link TravelDocument}
     * 
     * @param calculated status that can be set on the {@link TravelDocument} can be true or false
     */
    void setCalculated(boolean calculated);

    List<ExtraButton> getExtraButtons();

    String getMethodToCall();

    void setNewActualExpenseLine(ActualExpense newActualExpenseLine);

    ActualExpense getNewActualExpenseLine();

    void setNewActualExpenseLines(List<ActualExpense> newActualExpenseLines);

    List<ActualExpense> getNewActualExpenseLines();

    AccountingDocumentRelationship getNewAccountingDocumentRelationship();

    void setNewAccountingDocumentRelationship(AccountingDocumentRelationship newEmergencyContactLine);

    void setNewImportedExpenseLines(List<ImportedExpense> importedExpenses);

    List<ImportedExpense> getNewImportedExpenseLines();
    
    void setNewImportedExpenseLine(ImportedExpense importedExpense);

    ImportedExpense getNewImportedExpenseLine();
    
    void setDistribution(final List<AccountingDistribution> distribution);

    List<AccountingDistribution> getDistribution();

    /**
     * Gets the enableTaxable attribute. 
     * @return Returns the enableTaxable.
     */
    public boolean getEnableImportedTaxable();

    /**
     * Sets the enableTaxable attribute value.
     * @param enableTaxable The enableTaxable to set.
     */
    public void setEnableImportedTaxable(boolean enableImportedTaxable);
    
    /**
     * Gets the accountDistributionnextSourceLineNumber attribute. 
     * @return Returns the accountDistributionnextSourceLineNumber.
     */
    public Integer getAccountDistributionnextSourceLineNumber();

    /**
     * Sets the accountDistributionnextSourceLineNumber attribute value.
     * @param accountDistributionnextSourceLineNumber The accountDistributionnextSourceLineNumber to set.
     */
    public void setAccountDistributionnextSourceLineNumber(Integer accountDistributionnextSourceLineNumber);

    /**
     * Gets the accountDistributionnewSourceLine attribute. 
     * @return Returns the accountDistributionnewSourceLine.
     */
    public TemDistributionAccountingLine getAccountDistributionnewSourceLine();

    /**
     * Sets the accountDistributionnewSourceLine attribute value.
     * @param accountDistributionnewSourceLine The accountDistributionnewSourceLine to set.
     */
    public void setAccountDistributionnewSourceLine(TemDistributionAccountingLine accountDistributionnewSourceLine);
    
    /**
     * Sets the sequence number appropriately for the passed in source accounting line using the value that has been stored in the
     * nextSourceLineNumber variable, adds the accounting line to the list that is aggregated by this object, and then handles
     * incrementing the nextSourceLineNumber variable.
     * 
     * @param line the accounting line to add to the list.
     * @see org.kuali.kfs.sys.document.AccountingDocument#addSourceAccountingLine(SourceAccountingLine)
     */
    public void addAccountDistributionsourceAccountingLine(TemDistributionAccountingLine line);

    public KualiDecimal getDistributionRemainingAmount(boolean selectedDistributions);
    
    public KualiDecimal getDistributionSubTotal(boolean selectedDistributions);
    
    public List<TemDistributionAccountingLine> getAccountDistributionsourceAccountingLines();

    public void setAccountDistributionsourceAccountingLines(List<TemDistributionAccountingLine> accountDistributionsourceAccountingLines);

}
