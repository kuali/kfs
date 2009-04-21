/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.kfs.module.ec.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.service.EffortCertificationDocumentService;
import org.kuali.kfs.module.ec.util.EffortCertificationParameterFinder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ParameterConstants.COMPONENT;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Effort Certification Document Class.
 */
@COMPONENT(component="EffortCertification")
public class EffortCertificationDocument extends FinancialSystemTransactionalDocumentBase  {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocument.class);

    private static final String DO_AWARD_SPLIT = "DoAwardSplit";
    private static final String DO_RECREATE_SPLIT = "DoRecreateSplit";
    
    private String effortCertificationReportNumber;
    private boolean effortCertificationDocumentCode;
    private Integer universityFiscalYear;
    private String emplid;
    private KualiDecimal financialDocumentTotalAmount;

    private Integer totalEffortPercent;
    private Integer totalOriginalEffortPercent;
    private KualiDecimal totalPayrollAmount;
    private KualiDecimal totalOriginalPayrollAmount;

    private EffortCertificationReportDefinition effortCertificationReportDefinition;
    private Person employee;
    private SystemOptions options;

    private List<EffortCertificationDetail> effortCertificationDetailLines;
    private List<EffortCertificationDetail> summarizedDetailLines;

    /**
     * Default constructor.
     */
    public EffortCertificationDocument() {
        super();
        
        effortCertificationDetailLines = new TypedArrayList(EffortCertificationDetail.class);
        summarizedDetailLines = new TypedArrayList(EffortCertificationDetail.class);
    }

    /**
     * Gets the effortCertificationReportNumber attribute.
     * 
     * @return Returns the effortCertificationReportNumber.
     */
    public String getEffortCertificationReportNumber() {
        return effortCertificationReportNumber;
    }

    /**
     * Sets the effortCertificationReportNumber attribute value.
     * 
     * @param effortCertificationReportNumber The effortCertificationReportNumber to set.
     */
    public void setEffortCertificationReportNumber(String effortCertificationReportNumber) {
        this.effortCertificationReportNumber = effortCertificationReportNumber;
    }

    /**
     * Gets the effortCertificationDocumentCode attribute.
     * 
     * @return Returns the effortCertificationDocumentCode.
     */
    public boolean getEffortCertificationDocumentCode() {
        return effortCertificationDocumentCode;
    }

    /**
     * Sets the effortCertificationDocumentCode attribute value.
     * 
     * @param effortCertificationDocumentCode The effortCertificationDocumentCode to set.
     */
    public void setEffortCertificationDocumentCode(boolean effortCertificationDocumentCode) {
        this.effortCertificationDocumentCode = effortCertificationDocumentCode;
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid.
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute value.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the effortCertificationReportDefinition attribute.
     * 
     * @return Returns the effortCertificationReportDefinition.
     */
    public EffortCertificationReportDefinition getEffortCertificationReportDefinition() {
        return effortCertificationReportDefinition;
    }

    /**
     * Sets the effortCertificationReportDefinition attribute value.
     * 
     * @param effortCertificationReportDefinition The effortCertificationReportDefinition to set.
     */
    @Deprecated
    public void setEffortCertificationReportDefinition(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        this.effortCertificationReportDefinition = effortCertificationReportDefinition;
    }

    /**
     * Gets the employee attribute.
     * 
     * @return Returns the employee.
     */
    public Person getEmployee() {
        return (Person) SpringContext.getBean(PersonService.class).getPersonByEmployeeId(getEmplid());
    }

    /**
     * Sets the employee attribute value.
     * 
     * @param employee The employee to set.
     */
    public void setEmployee(Person employee) {
        this.employee = employee;
    }

    /**
     * Gets the options attribute.
     * 
     * @return Returns the options.
     */
    public SystemOptions getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     */
    public void setOptions(SystemOptions options) {
        this.options = options;
    }

    /**
     * Gets the effortCertificationDetailLines attribute.
     * 
     * @return Returns the effortCertificationDetailLines.
     */
    public List<EffortCertificationDetail> getEffortCertificationDetailLines() {
        return effortCertificationDetailLines;
    }

    /**
     * Sets the effortCertificationDetailLines attribute value.
     * 
     * @param effortCertificationDetailLines The effortCertificationDetailLines to set.
     */
    @Deprecated
    public void setEffortCertificationDetailLines(List<EffortCertificationDetail> effortCertificationDetailLines) {
        this.effortCertificationDetailLines = effortCertificationDetailLines;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    /**
     * get the total amount of the given effort certification document
     * 
     * @param effortCertificationDocument the given effort certification document
     * @return the total amount of the given effort certification document
     */
    public static KualiDecimal getDocumentTotalAmount(EffortCertificationDocument effortCertificationDocument) {
        List<EffortCertificationDetail> detailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        return EffortCertificationDetail.getTotalPayrollAmount(detailLines);
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() start...");

        super.handleRouteStatusChange();
        KualiWorkflowDocument workflowDocument = this.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsFinal()) {
            GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
            SpringContext.getBean(EffortCertificationDocumentService.class).generateSalaryExpenseTransferDocument(this);
        }
        //           SpringContext.getBean(EffortCertificationDocumentService.class).processApprovedEffortCertificationDocument(this);
    }

    /**
     * Gets the totalEffortPercent attribute.
     * 
     * @return Returns the totalEffortPercent.
     */
    public Integer getTotalEffortPercent() {
        return EffortCertificationDetail.getTotalEffortPercent(this.getEffortCertificationDetailLines());
    }

    /**
     * Gets the totalOriginalEffortPercent attribute.
     * 
     * @return Returns the totalOriginalEffortPercent.
     */
    public Integer getTotalOriginalEffortPercent() {
        return EffortCertificationDetail.getTotalOriginalEffortPercent(this.getEffortCertificationDetailLines());
    }

    /**
     * Gets the totalPayrollAmount attribute.
     * 
     * @return Returns the totalPayrollAmount.
     */
    public KualiDecimal getTotalPayrollAmount() {
        return EffortCertificationDetail.getTotalPayrollAmount(this.getEffortCertificationDetailLines());
    }

    /**
     * Gets the totalOriginalPayrollAmount attribute.
     * 
     * @return Returns the totalOriginalPayrollAmount.
     */
    public KualiDecimal getTotalOriginalPayrollAmount() {
        return EffortCertificationDetail.getTotalOriginalPayrollAmount(this.getEffortCertificationDetailLines());
    }

    /**
     * find the detail lines that have max payroll amount
     * 
     * @return the detail lines that have max payroll amount
     */
    public List<EffortCertificationDetail> getEffortCertificationDetailWithMaxPayrollAmount() {
        List<EffortCertificationDetail> detailLines = new ArrayList<EffortCertificationDetail>();

        KualiDecimal maxAmount = null;
        for (EffortCertificationDetail line : this.getEffortCertificationDetailLines()) {
            KualiDecimal currentAmount = line.getEffortCertificationPayrollAmount();

            if (maxAmount == null) {
                maxAmount = currentAmount;
                detailLines.add(line);
                continue;
            }

            if (maxAmount.isLessThan(currentAmount)) {
                detailLines.removeAll(detailLines);
                maxAmount = currentAmount;
                detailLines.add(line);
            }
            else if (maxAmount.equals(currentAmount)) {
                detailLines.add(line);
            }
        }

        return detailLines;
    }

    /**
     * Calculates the total updated effort for all federal detail lines
     * 
     * @return effortFederalTotal
     */
    public Integer getFederalTotalEffortPercent() {
        Integer effortFederalTotal = 0;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (detailLine.isFederalOrFederalPassThroughIndicator()) {
                effortFederalTotal += detailLine.getEffortCertificationUpdatedOverallPercent();
            }
        }

        return effortFederalTotal;
    }

    /**
     * Calculates the total original effort for all federal detail lines
     * 
     * @return original federal total
     */
    public Integer getFederalTotalOriginalEffortPercent() {
        Integer effortOrigFederalTotal = 0;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (detailLine.isFederalOrFederalPassThroughIndicator()) {
                effortOrigFederalTotal += detailLine.getEffortCertificationCalculatedOverallPercent();
            }
        }

        return effortOrigFederalTotal;
    }

    /**
     * Calculates the total original fringe benefit amount for federal pass through detail lines
     * 
     * @return total federal benefit amount
     */
    public KualiDecimal getFederalTotalOriginalFringeBenefit() {
        KualiDecimal totalBenAmount = KualiDecimal.ZERO;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (detailLine.isFederalOrFederalPassThroughIndicator()) {
                totalBenAmount = totalBenAmount.add(detailLine.getOriginalFringeBenefitAmount());
            }
        }

        return totalBenAmount;
    }

    /**
     * Calculates total original fringe benenfit amount for non federal pass through detail lines
     * 
     * @return total non federal benefit amount
     */
    public KualiDecimal getOtherTotalOriginalFringeBenefit() {
        KualiDecimal totalBenAmount = KualiDecimal.ZERO;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (!detailLine.isFederalOrFederalPassThroughIndicator()) {
                totalBenAmount = totalBenAmount.add(detailLine.getOriginalFringeBenefitAmount());
            }
        }

        return totalBenAmount;
    }

    /**
     * Calculates the total fringe benefit amount for federal pass through detail lines
     * 
     * @return total federal benefit amount
     */
    public KualiDecimal getFederalTotalFringeBenefit() {
        KualiDecimal totalBenAmount = KualiDecimal.ZERO;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (detailLine.isFederalOrFederalPassThroughIndicator()) {
                totalBenAmount = totalBenAmount.add(detailLine.getFringeBenefitAmount());
            }
        }

        return totalBenAmount;
    }

    /**
     * Calculates total fringe benenfit amount for non federal pass through detail lines
     * 
     * @return total non federal benefit amount
     */
    public KualiDecimal getOtherTotalFringeBenefit() {
        KualiDecimal totalBenAmount = KualiDecimal.ZERO;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (!detailLine.isFederalOrFederalPassThroughIndicator()) {
                totalBenAmount = totalBenAmount.add(detailLine.getFringeBenefitAmount());
            }
        }

        return totalBenAmount;
    }

    /**
     * Calculates the total original effor for non federal pass through detail lines
     * 
     * @return original other total
     */
    public Integer getOtherTotalOriginalEffortPercent() {
        Integer effortOrigOtherTotal = 0;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (!detailLine.isFederalOrFederalPassThroughIndicator()) {
                effortOrigOtherTotal += detailLine.getEffortCertificationCalculatedOverallPercent();
            }
        }

        return effortOrigOtherTotal;
    }

    /**
     * Calculates the total updated effort for non federal pass through detail lines
     * 
     * @return effort total for non federal pass through accounts
     */
    public Integer getOtherTotalEffortPercent() {
        Integer effortOtherTotal = 0;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (!detailLine.isFederalOrFederalPassThroughIndicator()) {
                effortOtherTotal += detailLine.getEffortCertificationUpdatedOverallPercent();
            }
        }

        return effortOtherTotal;
    }

    /**
     * Calculates the total salary for federal detail lines
     * 
     * @return total salary
     */
    public KualiDecimal getFederalTotalPayrollAmount() {
        KualiDecimal salaryFederalTotal = KualiDecimal.ZERO;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (detailLine.isFederalOrFederalPassThroughIndicator()) {
                salaryFederalTotal = salaryFederalTotal.add(detailLine.getEffortCertificationPayrollAmount());
            }
        }

        return salaryFederalTotal;
    }

    /**
     * Calculates the total original salary for federal pass through detail lines
     * 
     * @return total salary
     */
    public KualiDecimal getFederalTotalOriginalPayrollAmount() {
        KualiDecimal salaryOrigFederalTotal = KualiDecimal.ZERO;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (detailLine.isFederalOrFederalPassThroughIndicator()) {
                salaryOrigFederalTotal = salaryOrigFederalTotal.add(detailLine.getEffortCertificationOriginalPayrollAmount());
            }
        }

        return salaryOrigFederalTotal;
    }

    /**
     * Calculates the total original salary for non federal pass through detail lines
     * 
     * @return total original salary
     */
    public KualiDecimal getOtherTotalOriginalPayrollAmount() {
        KualiDecimal salaryOrigOtherTotal = KualiDecimal.ZERO;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (!detailLine.isFederalOrFederalPassThroughIndicator()) {
                salaryOrigOtherTotal = salaryOrigOtherTotal.add(detailLine.getEffortCertificationOriginalPayrollAmount());
            }
        }

        return salaryOrigOtherTotal;
    }

    /**
     * Calculates total updated salary for non federal pass through detail lines
     * 
     * @return total salary
     */
    public KualiDecimal getOtherTotalPayrollAmount() {
        KualiDecimal salaryOtherTotal = KualiDecimal.ZERO;
        List<EffortCertificationDetail> detailLineList = this.getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : detailLineList) {
            if (!detailLine.isFederalOrFederalPassThroughIndicator()) {
                salaryOtherTotal = salaryOtherTotal.add(detailLine.getEffortCertificationPayrollAmount());
            }
        }

        return salaryOtherTotal;
    }

    /**
     * Gets the totalFringeBenefit attribute.
     * 
     * @return Returns the totalFringeBenefit.
     */
    public KualiDecimal getTotalFringeBenefit() {
        return EffortCertificationDetail.getTotalFringeBenefit(effortCertificationDetailLines);
    }

    /**
     * Gets the totalOriginalFringeBenefit attribute.
     * 
     * @return Returns the totalOriginalFringeBenefit.
     */
    public KualiDecimal getTotalOriginalFringeBenefit() {
        return EffortCertificationDetail.getTotalOriginalFringeBenefit(effortCertificationDetailLines);
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();

        // capture each line's salary amount before route level modification for later rule validation
        for (EffortCertificationDetail detailLine : this.getEffortCertificationDetailLines()) {
            detailLine.setPersistedPayrollAmount(new KualiDecimal(detailLine.getEffortCertificationPayrollAmount().bigDecimalValue()));
            
            int effortPercent = detailLine.getEffortCertificationUpdatedOverallPercent();
            detailLine.setPersistedEffortPercent(new Integer(effortPercent));
        }

        // calculate original fringe benefits for each line
        for (EffortCertificationDetail detailLine : this.getEffortCertificationDetailLines()) {
            detailLine.recalculateOriginalFringeBenefit();
        }
    }

    /**
     * Checks if the effort has changed for any of the detail lines.
     * 
     * @return true if effort has changed, false otherwise
     */
    public boolean isEffortDistributionChanged() {
        for (EffortCertificationDetail detail : this.getEffortCertificationDetailLines()) {
            if (!detail.getEffortCertificationCalculatedOverallPercent().equals(detail.getEffortCertificationUpdatedOverallPercent())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds the default position number for display based on the detail line with the maximum effort
     * 
     * @return default position number
     */
    public String getDefaultPositionNumber() {
        return this.getMaxEffortLine().getPositionNumber();
    }

    /**
     * Finds the default object code for display based on the value for the detail line with the maximum effort
     * 
     * @return default object code
     */
    public String getDefaultObjectCode() {
        return this.getMaxEffortLine().getFinancialObjectCode();
    }

    /**
     * Finds the detail line with the maximum effort
     * 
     * @return max effort line
     */
    private EffortCertificationDetail getMaxEffortLine() {
        Integer maxEffort = 0;
        EffortCertificationDetail maxLine = null;
        List<EffortCertificationDetail> detailLines = this.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : detailLines) {
            if (detailLine.getEffortCertificationUpdatedOverallPercent() > maxEffort) {
                maxEffort = detailLine.getEffortCertificationUpdatedOverallPercent();
                maxLine = detailLine;
            }
        }
        return maxLine;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        if (ObjectUtils.isNotNull(getTotalPayrollAmount())) {
            getDocumentHeader().setFinancialDocumentTotalAmount(getTotalPayrollAmount());
        }
        else {
            getDocumentHeader().setFinancialDocumentTotalAmount(new KualiDecimal(0));
        }
        super.populateDocumentForRouting();
    }


    /**
     * Finds the list of unique object codes contained in this document
     * 
     * @return list of unique object codes
     */
    public List<String> getObjectCodeList() {
        List<String> uniqueObjectCodeList = new ArrayList<String>();
        List<EffortCertificationDetail> allObjectCodesList = this.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detail : allObjectCodesList) {
            if (!uniqueObjectCodeList.contains(detail.getFinancialObjectCode())) {
                uniqueObjectCodeList.add(detail.getFinancialObjectCode());
            }
        }

        return uniqueObjectCodeList;
    }

    /**
     * Finds the list of unique position numbers for this document
     * 
     * @return list of unique position numbers
     */
    public List<String> getPositionList() {
        List<String> uniquePositionList = new ArrayList<String>();
        List<EffortCertificationDetail> allPositionsList = this.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detail : allPositionsList) {
            if (!uniquePositionList.contains(detail.getPositionNumber())) {
                uniquePositionList.add(detail.getPositionNumber());
            }
        }

        return uniquePositionList;
    }

    /**
     * This is a marker setter method and does nothing.
     */
    public void setTotalOriginalPayrollAmount(KualiDecimal totalOriginalPayrollAmount) {
        return;
    }

    /**
     * Gets the summarizedDetailLines attribute.
     * 
     * @return Returns the summarizedDetailLines.
     */
    public List<EffortCertificationDetail> getSummarizedDetailLines() {
        return summarizedDetailLines;
    }

    /**
     * Sets the summarizedDetailLines attribute value.
     * 
     * @param summarizedDetailLines The summarizedDetailLines to set.
     */
    public void setSummarizedDetailLines(List<EffortCertificationDetail> summarizedDetailLines) {
        this.summarizedDetailLines = summarizedDetailLines;
    }
    
    /**
     * Provides answers to the following splits:
     * Do Award Split
     * Do Recreate Split
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(EffortCertificationDocument.DO_AWARD_SPLIT)) return isDoAwardSplit();
        if (nodeName.equals(EffortCertificationDocument.DO_RECREATE_SPLIT)) return isDoRecreateSplit();
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \""+nodeName+"\"");
    }
    
    /**
     * Checks system parameter that indicates whether routing to project directors should occur only on
     * lines with federal accounts or all lines. 
     * 
     * @return detail lines with federal accounts if parameter is true, otherwise all detail lines
     */
    public List<EffortCertificationDetail> getDetailLinesForPDRouting() {
        boolean federalOnlyRouting = EffortCertificationParameterFinder.getFederalOnlyRouteIndicator();
        if (!federalOnlyRouting) {
            return this.effortCertificationDetailLines;
        }
        
        List<EffortCertificationDetail> federalDetailLines = new ArrayList<EffortCertificationDetail>();
        for (EffortCertificationDetail detail : this.effortCertificationDetailLines) {
            if (detail.isFederalOrFederalPassThroughIndicator()) {
                federalDetailLines.add(detail);
            }
        }
        
        return federalDetailLines;
    }
    
    /**
     * @return boolean value 
     */
    protected boolean isDoAwardSplit() {
        return this.isEffortDistributionChanged();
    }
    
    /**
     * @return boolean value
     */
    protected boolean isDoRecreateSplit() {
        return this.getEffortCertificationDocumentCode();
    } 
}

