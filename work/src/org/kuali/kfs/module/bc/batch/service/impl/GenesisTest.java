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
package org.kuali.module.budget.service.impl;

import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kuali.core.UserSession;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.service.GenesisService;
import org.kuali.module.chart.service.DateMakerService;
import org.kuali.rice.resourceloader.SpringResourceLoader;

import edu.iu.uis.eden.exception.WorkflowException;

public class GenesisTest {
    
  private static GenesisService            genesisTestService;
  private static Logger                    LOG;
  private static DateTimeService           dateTimeService;
  private static DateMakerService          dateMakerTestService;
  private static KualiConfigurationService configService;
  
  private static void configurationStep()
  {
      //    this supposedly configures a logger that everybody can fetch and use
      PropertyConfigurator.configure(ResourceBundle.getBundle(
              "configuration").getString(KFSConstants.LOG4J_SETTINGS_FILE_KEY));
     //  get one for this routine
      LOG = org.apache.log4j.Logger.getLogger(GenesisTest.class);
     
      // does this initialize the spring context?
     QName dummyQName = null; 
     SpringResourceLoader springResourceLoader =
         new SpringResourceLoader(dummyQName, "SpringBeans.xml");
     try
     {
     springResourceLoader.start();
     }
     catch (Exception ex)
     {
        ex.printStackTrace();      
     };
  //    this supposedly configures spring/ojb
  //   SpringServiceLocator.initializeDDGeneratorApplicationContext();
  //   configService = 
  //          SpringContext.getBean(KualiConfigurationService.class);
  //  (07/06/07) try using the new look-up bean
  //   GlobalResourceLoader.getService("initializeDDGeneratorApplicationContext");
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
    
  public static void main(String args[]) throws SQLException
  {
      configurationStep();
      //   these are the current run configurations (to change when workflow is embedded)
      //   for
      //   genesis
      // genesisTestService.genesisStep(2007);
      //   budget construction update
      // bcUpdateStep(2009);
      //
      //    unit tests for Genesis 
      //
      //
      // update current positions
        genesisTestService.testPositionBuild(2011);
// 7/6/07      LOG.warn("\nstarting fiscalYearMakers\n");
// 7/6/07      dateMakerTestService.fiscalYearMakers(2013,false);
        //dateMakerTestService.fiscalYearMakers(2009,false);
        //dateMakerTestService.testRoutine(); 
// 7/6/07      LOG.warn("\nfiscalYearMakers finished\n");
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
  }
}
