/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.effort.service.impl;

import org.kuali.module.effort.service.EffortCertificationCreateService;
import org.kuali.module.effort.util.EffortCertificationParameterFinder;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class EffortCertificationCreateServiceImpl implements EffortCertificationCreateService {

    /**
     * @see org.kuali.module.effort.service.EffortCertificationCreateService#create()
     */
    public void create() {
        Integer fiscalYear = EffortCertificationParameterFinder.getExtractReportFiscalYear();
        String reportNumber = EffortCertificationParameterFinder.getExtractReportNumber();

        this.create(fiscalYear, reportNumber);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationCreateService#create(java.lang.Integer, java.lang.String)
     */
    public void create(Integer fiscalYear, String reportNumber) {
        // TODO Auto-generated method stub
        
    }

}
