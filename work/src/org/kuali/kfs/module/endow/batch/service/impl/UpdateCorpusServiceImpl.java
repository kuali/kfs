/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.sql.Date;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.batch.service.UpdateCorpusService;
import org.kuali.kfs.module.endow.dataaccess.UpdateCorpusDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UpdateCorpusServiceImpl implements UpdateCorpusService {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpdateCorpusServiceImpl.class);
    
    private KEMService kemService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private UniversityDateService universityDateService;
    private UpdateCorpusDao updateCorpusDao;
    
    private Date currentDate;
    private int maxNumberOfTransactionLines;
    
    /**

     * @see org.kuali.kfs.module.endow.batch.service.UpdateCorpusService#updateCorpusTransactions()
     */
    public boolean updateCorpusTransactions() {
                        
        //set current date and max trans lines
        currentDate = kemService.getCurrentDate();
        maxNumberOfTransactionLines = getMaxNumberOfTransactionLines();                
        
        LOG.info("Begin updateCorpusTransactions() with process date of " + currentDate);
        
        //Run update corpus process
        runProcess1();
                
        runProcess2();
                
        runProcess3();
                                            
        runProcess4();        
        
        LOG.info("End updateCorpusTransactions() with process date of " + currentDate);
        
        return true;
    }

    /**
     * Copies current corpus values to prior corpus values.
     * 
     */
    protected void runProcess1(){
                
        if( isFirstDayOfFiscalYear() ){
                        
            updateCorpusDao.updateKemIdCorpusPriorYearValues();                        
        }else{
            LOG.info("updateKemIdCorpusPriorYearValues() skipped - not first day of fiscal year");
        }
        
    }

    /**
     * Totals Transaction Archive data where tran_corpus_ind is Y and
     * adds the values to Current Endowment Corpus.  Then copies the
     * values from Current Endowment Corpus to KEMID Corpus
     * 
     */
    protected void runProcess2(){
     
        updateCorpusDao.updateCorpusAmountsFromTransactionArchive(currentDate);
        
    }
    
    /**
     * Update the field PRIN_MVAL for every record in KEMID Corpus with
     * a the field Prin at Market from KEMID CRNT BAL view 
     * 
     */
    protected void runProcess3(){
        
        updateCorpusDao.updateKemIdCorpusPrincipalMarketValue();
        
    }

    /**
     * Copies all records from the current endowment corpus table to the
     *  endowment corpus table.
     * 
     */
    protected void runProcess4(){
        
        if( isLastDayOfFiscalYear() ){
            
            updateCorpusDao.updateEndowmentCorpusWithCurrentEndowmentCorpus(currentDate);
        }else{
            LOG.info("updateEndowmentCorpusWithCurrentEndowmentCorpus() skipped - not last day of fiscal year");
        }
        
    }

    /**
     * Retrieves parameter for maximum number of transaction lines allowed in a batch document.
     * 
     * @return
     */
    protected int getMaxNumberOfTransactionLines() {
        return Integer.parseInt(parameterService.getParameterValueAsString(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowParameterKeyConstants.MAXIMUM_TRANSACTION_LINES));
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

    public void setUpdateCorpusDao(UpdateCorpusDao updateCorpusDao) {
        this.updateCorpusDao = updateCorpusDao;
    }

}
