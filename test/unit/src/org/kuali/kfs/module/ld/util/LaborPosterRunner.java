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
package org.kuali.module.labor.util;

import java.sql.Date;
import java.util.List;
import java.util.Properties;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.labor.service.LaborPosterService;
import org.springframework.beans.factory.BeanFactory;

public class LaborPosterRunner {
    private LaborPosterService laborPosterService;

    public LaborPosterRunner() {
        SpringServiceLocator.initializeDDGeneratorApplicationContext();
        laborPosterService = (LaborPosterService) SpringServiceLocator.getBeanFactory().getBean("laborPosterService");
    }
    
    public void runPoster(){
        laborPosterService.postMainEntries();
    }

    public static void main(String[] args) {              
        LaborPosterRunner laborPosterRunner = new LaborPosterRunner();
        
        System.out.println("Labor Poster started");
        long start = System.currentTimeMillis();
        laborPosterRunner.runPoster();
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.printf("Execution Time = %d (ms)\n", elapsedTime);
        
        System.exit(0);
    }
}
