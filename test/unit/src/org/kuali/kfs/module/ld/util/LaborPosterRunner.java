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

import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.labor.service.LaborPosterService;

public class LaborPosterRunner {
    private LaborPosterService laborPosterService;

    public LaborPosterRunner() {
        SpringServiceLocator.initializeApplicationContext();
        laborPosterService = (LaborPosterService) SpringServiceLocator.getBeanFactory().getBean("laborPosterService");
    }

    public void runPoster() {
        laborPosterService.postMainEntries();
    }

    public static void main(String[] args) {
        try {
            LaborPosterRunner laborPosterRunner = new LaborPosterRunner();

            System.out.println("Labor Poster started");
            long start = System.currentTimeMillis();
            System.out.println("Labor Poster is running ...");
            laborPosterRunner.runPoster();
            long elapsedTime = System.currentTimeMillis() - start;
            System.out.printf("Execution Time = %d (ms)\n", elapsedTime);
            System.out.println("Labor Poster stopped");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }
}
