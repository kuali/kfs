/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.effort.web.struts.form;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.util.EffortCertificationDocumentSorter;

/**
 * Action form for Effort Certification Document.
 */
public class CertificationReportForm extends EffortCertificationForm {
    
    private String sortOrderFed = EffortConstants.EFFORT_DOCUMENT_SORT_ORDER_DESC;
    private String sortOrderOther = EffortConstants.EFFORT_DOCUMENT_SORT_ORDER_DESC;
    private String sortColumnFed;
    private String sortColumnOther;
    private Integer currentIndexInNonFederalSortedList;
    private Integer currentIndexInFederalPassThroughSortedList;
    
    /**
     * Gets the report start date formatted for display
     * 
     * @return
     */
    public String getFormattedStartDate() {
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) getDocument();
        effortDocument.refreshReferenceObject("effortCertificationReportDefinition");
        AccountingPeriodService accountingPeriodService = (AccountingPeriodService)SpringContext.getBean(AccountingPeriodService.class);
        Date startDate = accountingPeriodService.getByPeriod( effortDocument.getEffortCertificationReportDefinition().getEffortCertificationReportBeginPeriodCode(), effortDocument.getEffortCertificationReportDefinition().getEffortCertificationReportBeginFiscalYear()).getUniversityFiscalPeriodEndDate();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        startDate.setDate(1);
        
        return formatter.format(startDate);
    }
    
    /**
     * Gets the report end date formatted for display
     * 
     * @return
     */
    public String getFormattedEndDate() {
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) getDocument();
        effortDocument.refreshReferenceObject("effortCertificationReportDefinition");
        AccountingPeriodService accountingPeriodService = (AccountingPeriodService)SpringContext.getBean(AccountingPeriodService.class);
        Date endDate = accountingPeriodService.getByPeriod( effortDocument.getEffortCertificationReportDefinition().getEffortCertificationReportEndPeriodCode(), effortDocument.getEffortCertificationReportDefinition().getEffortCertificationReportEndFiscalYear()).getUniversityFiscalPeriodEndDate();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
               
        return formatter.format(endDate);
    }
    
    
    /**
     * Gets the sort order for federal pass through detail lines (for display)
     * 
     * @return
     */
    public String getSortOrderFed() {
        return sortOrderFed;
    }
    
    /**
     * Sets the sort order for federal pass through detail lines (for display)
     * 
     * @param sortOrderFed
     */
    public void setSortOrderFed(String sortOrderFed) {
        this.sortOrderFed = sortOrderFed;
    }
    
    /**
     * Gets the sort order for non federal pass through detail lines (for display)
     * This method...
     * @return
     */
    public String getSortOrderOther() {
        return sortOrderOther;
    }
    
    /**
     * Sets the sort order for non federal pass through detail lines (for display)
     * 
     * @param sortOrderOther
     */
    public void setSortOrderOther(String sortOrderOther) {
        this.sortOrderOther = sortOrderOther;
    }

    /**
     * Toggles the sort order between 'asc' and 'desc' for federal pass through detail lines (for display)
     * This method...
     */
    public void toggleFedSortOrder() {
        if (this.sortOrderFed.equals(EffortConstants.EFFORT_DOCUMENT_SORT_ORDER_ASC)) this.sortOrderFed = EffortConstants.EFFORT_DOCUMENT_SORT_ORDER_DESC;
        else this.sortOrderFed = EffortConstants.EFFORT_DOCUMENT_SORT_ORDER_ASC;
    }
    
    /**
     * Toggles the sort order between 'asc' and 'desc' for non federal pass through detail lines (for display)
     * This method...
     */
    public void toggleOtherSortOrder() {
        if (this.sortOrderOther.equals(EffortConstants.EFFORT_DOCUMENT_SORT_ORDER_ASC)) this.sortOrderOther = EffortConstants.EFFORT_DOCUMENT_SORT_ORDER_DESC;
        else this.sortOrderOther = EffortConstants.EFFORT_DOCUMENT_SORT_ORDER_ASC;
    }
        
    /**
     * Finds the detail line's index in the original unsorted list (so that the correct line will be updated in action class)
     * 
     * @return index
     */
    public Integer getIndexInUnsortedListForNonFederalDetailLines() {
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) this.getDocument();
        List<EffortCertificationDetail> unsortedList = effortDocument.getEffortCertificationDetailLines();
        List<EffortCertificationDetail> sortedList = this.getSortedNonFederalDetailLines();
        
        if ( this.currentIndexInNonFederalSortedList == null || ( this.currentIndexInNonFederalSortedList == (sortedList.size() - 1)) ) this.currentIndexInNonFederalSortedList = 0;
        else this.currentIndexInNonFederalSortedList ++;
        
        return unsortedList.indexOf(sortedList.get(this.currentIndexInNonFederalSortedList));
    }
    
    /**
     * Finds the detail line's index in the original unsorted list (so that the correct line will be updated in action class)
     * 
     * @return index
     */
    public Integer getIndexInUnsortedListForFederalPassThroughDetailLines() {
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) this.getDocument();
        List<EffortCertificationDetail> unsortedList = effortDocument.getEffortCertificationDetailLines();
        List<EffortCertificationDetail> sortedList = this.getSortedFederalPassThroughDetailLines();
        
        if ( this.currentIndexInFederalPassThroughSortedList == null || ( this.currentIndexInFederalPassThroughSortedList == (sortedList.size() - 1)) ) {
            this.currentIndexInFederalPassThroughSortedList = 0;
           
        }
        else this.currentIndexInFederalPassThroughSortedList ++;
        
        return unsortedList.indexOf(sortedList.get(this.currentIndexInFederalPassThroughSortedList));
    }
    
    /**
     * Sorts the non federal pass through detail lines based on sort order and column
     * 
     * @return sorted list
     */
    public List<EffortCertificationDetail> getSortedNonFederalDetailLines() {
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) this.getDocument();
        List<EffortCertificationDetail> unsortedList = effortDocument.getEffortCertificationDetailLines();
        
        if ( this.sortColumnOther == null ) this.sortColumnOther = EffortConstants.EFFORT_DOCUMENT_DEFAULT_SORT_COLUMN;
        
        return EffortCertificationDocumentSorter.sort(unsortedList, this.sortOrderOther, this.sortColumnOther, false);
    }
    
    /**
     * Sorts the federal pass through detail lines based on sort order and column
     * 
     * @return sorted list
     */
    public List<EffortCertificationDetail> getSortedFederalPassThroughDetailLines() {
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) this.getDocument();
        List<EffortCertificationDetail> unsortedList = effortDocument.getEffortCertificationDetailLines();
        
        if ( this.sortColumnFed == null ) this.sortColumnFed = EffortConstants.EFFORT_DOCUMENT_DEFAULT_SORT_COLUMN;
        
        return EffortCertificationDocumentSorter.sort(unsortedList, this.sortOrderFed, this.sortColumnFed, true);
    }
    
    /**
     * Gets sor column for fed detail lines
     * 
     * @return
     */
    public String getSortColumnFed() {
        return sortColumnFed;
    }
    
    /**
     * Sets sort column for fed detail lines
     * 
     * @param sortColumnFed
     */
    public void setSortColumnFed(String sortColumnFed) {
        this.sortColumnFed = sortColumnFed;
    }
    
    /**
     * gets sort column for non fed pass through detail lines
     * 
     * @return
     */
    public String getSortColumnOther() {
        return sortColumnOther;
    }
    
    /**
     * sets sort column for non fed detail lines
     * 
     * @param sortColumnOther
     */
    public void setSortColumnOther(String sortColumnOther) {
        this.sortColumnOther = sortColumnOther;
    }
    
}
