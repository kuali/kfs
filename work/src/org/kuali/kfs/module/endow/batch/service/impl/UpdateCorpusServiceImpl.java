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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.sql.Date;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.service.UpdateCorpusService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;

public class UpdateCorpusServiceImpl implements UpdateCorpusService {
    
    private KEMService kemService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private UniversityDateService universityDateService;
    private Date currentDate;
    private int maxNumberOfTransactionLines;
    
    public boolean updateCorpusTransactions() {
        
        //set current date and max trans lines
        currentDate = kemService.getCurrentDate();
        maxNumberOfTransactionLines = getMaxNumberOfTransactionLines();
        boolean success = true;
        
        //Process 1
        if( isFirstDayOfFiscalYear() ){
            success &= runProcess1();
        }
        
        //Process 2
        success &= runProcess2();
        
        //Process 3
        success &= runProcess3();
        
        //Process 4
        success &= runProcess4();
        
        return success;
    }

    /**
     * Copies current corpus values to prior corpus values.
     * 
     */
    protected boolean runProcess1(){
        return false;
    }

    /**
     * Totals Transaction Archive data where tran_corpus_ind is Y and
     * adds the values to Current Endowment Corpus.  Then copies the
     * values from Current Endowment Corpus to KEMID Corpus
     * 
     */
    protected boolean runProcess2(){
        return false;
    }

    /**
     * Update the field PRIN_MVAL for every record in Current Endowment Corpus with
     * a the field Prin at Market from Current Balance table 
     * 
     */
    protected boolean runProcess3(){
        return false;
    }

    /**
     * Copies all records from the current endowment corpus table to the
     *  endowment corpus table.
     * 
     */
    protected boolean runProcess4(){
        return false;
    }

    /**
     * Retrieves parameter for maximum number of transaction lines allowed in a batch document.
     * 
     * @return
     */
    protected int getMaxNumberOfTransactionLines() {
        return Integer.parseInt(parameterService.getParameterValue(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowConstants.EndowmentSystemParameter.MAXIMUM_TRANSACTION_LINES));
    }
    
    /**
     * Determines if the current date is the same as the first day of the fiscal year
     * 
     * @param currentDate
     * @return
     */
    protected boolean isFirstDayOfFiscalYear(){       
        Date firstDateOfFiscalYear = (Date) universityDateService.getFirstDateOfFiscalYear( universityDateService.getCurrentFiscalYear() );
        
        return DateUtils.isSameDay( currentDate, firstDateOfFiscalYear );        
    }

    /**
     * Determines if the current date is the same as the last day of the fiscal year
     * 
     * @return
     */
    protected boolean isLastDayOfFiscalYear(){       
        Date lastDateOfFiscalYear = (Date) kemService.getFiscalYearEndDayAndMonth();
        
        return DateUtils.isSameDay( currentDate, lastDateOfFiscalYear );        
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

}
