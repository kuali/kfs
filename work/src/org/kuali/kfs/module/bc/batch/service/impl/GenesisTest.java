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

import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.util.SpringServiceLocator.*;
import org.kuali.module.budget.service.*;
import org.kuali.core.service.*;
import org.kuali.core.*;
import org.kuali.core.util.*;
import org.kuali.core.bo.user.*;
import org.kuali.module.budget.dao.*;
import org.kuali.module.budget.dao.ojb.*;

// import these things to handle the configuration
import org.kuali.core.service.KualiConfigurationService;
import org.springframework.beans.factory.BeanFactory;
//  handle workflow
import edu.iu.uis.eden.exception.WorkflowException;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.workflow.*;
//this is just for the logger, and could be taken out
import org.apache.log4j.*;
import java.util.ResourceBundle;
import org.kuali.Constants;
import org.kuali.Constants.*;

public class GenesisTest {
    
  private static GenesisService            genesisTestService;
  private static GenesisDao                genesisDao;
  private static Logger                    LOG;
  private static DateTimeService           dateTimeService;
  private static DateMakerService          dateMakerTestService;
  private static KualiConfigurationService configService;
  
  private static void configurationStep()
  {
      //    this supposedly configures a logger that everybody can fetch and use
      PropertyConfigurator.configure(ResourceBundle.getBundle(
        Constants.CONFIGURATION_FILE_NAME).getString(Constants.LOG4J_SETTINGS_FILE_KEY));
  //  get one for this routine
      LOG = org.apache.log4j.Logger.getLogger(GenesisTest.class);
     
  //    this supposedly configures spring/ojb
     SpringServiceLocator.initializeDDGeneratorApplicationContext();
     configService = 
            SpringServiceLocator.getKualiConfigurationService();
     genesisDao = (GenesisDao) SpringServiceLocator.getInstance().getApplicationContext().getBean("genesisDao");
  //    
     genesisTestService = SpringServiceLocator.getGenesisService();
     dateMakerTestService = 
          SpringServiceLocator.getDateMakerService();
      dateTimeService =
          SpringServiceLocator.getDateTimeService();
  //
      GlobalVariables.clear();
      try
      {
      GlobalVariables.setUserSession(new UserSession("KHUNTLEY"));
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
  
  private static void genesisStep (Integer BaseYear)
  {
      // OK.  This is the sequence as of February, 2007.  When workflow is embedded, 
      // it should change.  we won't need to do the proxies.  But, to avoid a complete
      // re-write, we probably should create all the documents first, then create
      // the PBGL by reading GL again.  We should do the routing update change while
      // the newly created documents are in memory.  we will modify updateGL to 
      // create new documents on the fly (which can't be done now because workflow
      // is a remote user which must read our data AFTER we have committed it.
      LOG.info(String.format("\ngenesis started %tT",dateTimeService.getCurrentDate()));
      LOG.info("\nDocument creation started: "+String.format("%tT",
              dateTimeService.getCurrentDate()));
      genesisTestService.genesisDocumentStep(BaseYear);
      LOG.info("\nDocument creation ended: "+
               String.format("%tT",dateTimeService.getCurrentDate()));
      genesisTestService.genesisFinalStep(BaseYear);
      LOG.info(String.format("\ngenesis ended %tT",dateTimeService.getCurrentDate()));
  }
  
  private static void bcUpdateStep(Integer BaseYear)
  {
      Integer RequestYear = BaseYear + 1;
      genesisDao.rebuildOrganizationHierarchy(BaseYear);
      genesisDao.clearHangingBCLocks(BaseYear);
      if (genesisDao.getBudgetConstructionControlFlag(RequestYear,
              BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE) &&
          genesisDao.getBudgetConstructionControlFlag(BaseYear,
              BudgetConstructionConstants.BASE_BUDGET_UPDATES_OK))
      {
          // use the log as a quick way to test whether the flags work
          LOG.info(String.format("\nentered GL update block"));
          genesisDao.primeNewBCHeadersDocumentCreation(BaseYear);
          genesisDao.createNewBCDocuments(BaseYear);
          genesisDao.updateToPBGL(BaseYear);
          LOG.info(String.format("\nfinished GL update block"));
      }
  }
    
  public static void main(String args[])
  {
      configurationStep();
      //   these are the current run configurations (to change when workflow is embedded)
      //   for
      //   genesis
      // genesisStep(2007);
      //   budget construction update
      // bcUpdateStep(2009);
      //
      //    unit tests for Genesis 
      //
      //
      // update current positions
        genesisTestService.testPositionBuild(2007);
      // create the proxy BC headers
      /*
 //     genesisTestService.clearDBForGenesis(2009);
 //     LOG.info("\nDocument creation started: "+String.format("%tT",dateTimeService.getCurrentDate()));
 //     genesisTestService.createProxyBCHeadersTransactional(2009);
 //     LOG.info("\nProxy documents created: "+String.format("%tT",dateTimeService.getCurrentDate()));
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
     //       SpringServiceLocator.getOrganizationService().getRootOrganizationCode();
     //   LOG.info(String.format("\nroot chart: %s, root organization: %s", roots[0], roots[1]));
  //
  //    genesisTestService.testStep(2007);
  //    genesisTestService.testSLFStep(2009);
  //    genesisTestService.testSLFAfterStep(2009);
  //    genesisTestService.testLockClearance(2007);
  }
}
