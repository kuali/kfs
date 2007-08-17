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
package org.kuali.module.budget;

import org.apache.log4j.Logger;
import org.kuali.core.UserSession;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.dao.GenesisDao;
import org.kuali.module.budget.service.GenesisService;
import org.kuali.module.chart.service.DateMakerService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.UserNameFixture;

import edu.iu.uis.eden.exception.WorkflowException;

@ConfigureContext(session=UserNameFixture.KULUSER)
public class TestGenesis extends KualiTestBase {
    
  private static GenesisService            genesisTestService;
  private static GenesisDao                genesisDao;
  private static Logger                    LOG;
  private static DateTimeService           dateTimeService;
  private static DateMakerService          dateMakerTestService;
  private static KualiConfigurationService configService;
  
  public void testConfiguration() throws Exception
  {
      LOG = org.apache.log4j.Logger.getLogger(TestGenesis.class);
     configService = 
            SpringContext.getBean(KualiConfigurationService.class);
     genesisDao = SpringContext.getBean(GenesisDao.class);
  //    
     genesisTestService = SpringContext.getBean(GenesisService.class);
     dateMakerTestService = 
          SpringContext.getBean(DateMakerService.class);
      dateTimeService =
          SpringContext.getBean(DateTimeService.class);
  //
      GlobalVariables.clear();
      try
      {
      GlobalVariables.setUserSession(new UserSession("KHUNTLEY"));
//      GlobalVariables.setUserSession(new UserSession("KULUSER"));
      }
      catch (WorkflowException wfex)
      {
          LOG.warn(String.format("\nworkflow exception on fetching session %s",
                                 wfex.getMessage()));
      }
      catch (UserNotFoundException nfex)
      {
          LOG.warn(String.format("\nuser not found on fetching session %s",
                   nfex.getMessage()));
      }
      
  }
    
  //public static void main(String args[]) throws SQLException
  //{
    //  configurationStep();
      //   these are the current run configurations 
      //   for
      //   genesis
// 6/29/2007      genesisTestService.genesisStep(2007);
      //   budget construction update
      // bcUpdateStep(2009);
      //
      //    unit tests for Genesis 
      //
      //
      // update current positions
// 6/28/2007        genesisTestService.testPositionBuild(2011);
//      LOG.warn("\nstarting fiscalYearMakers\n");
//      dateMakerTestService.fiscalYearMakers(2013,false);
// 6/29/2007        dateMakerTestService.fiscalYearMakers(2007,false);
        //dateMakerTestService.testRoutine(); 
//      LOG.warn("\nfiscalYearMakers finished\n");
      // create the proxy BC headers
      /*
 //     genesisTestService.clearDBForGenesis(2009);
      */
      // create the real BC documents based on the proxies
      /*
 //     genesisDao.createNewBCDocuments(2009);
//      LOG.info("\nDocument creation ended: "+String.format("%tT",dateTimeService.getCurrentDate()));
      */
     // try running the hierarchy creation
     //  genesisTestService.testChartCreation();  
     //  genesisTestService.testHierarchyCreation(2009);
     // test the changes we made to the organization service for the root organization
     //   String[] roots = 
     //       SpringContext.getBean(OrganizationService.class).getRootOrganizationCode();
     //   LOG.info(String.format("\nroot chart: %s, root organization: %s", roots[0], roots[1]));
  //
  //    genesisTestService.testStep(2007);
  //    genesisTestService.testSLFStep(2009);
  //    genesisTestService.testSLFAfterStep(2009);
  //    genesisTestService.testLockClearance(2007);
  //}
}
