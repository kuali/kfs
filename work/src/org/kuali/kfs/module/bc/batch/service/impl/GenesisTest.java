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

import org.kuali.module.budget.service.*;
import org.kuali.core.service.*;
import org.kuali.core.util.SpringServiceLocator.*;
import org.kuali.core.util.SpringServiceLocator;
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
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.*;
import org.apache.log4j.Logger;
import java.util.ResourceBundle;
import org.kuali.Constants;

public class GenesisTest {
  public static void main(String args[])
  {
  //    unit tests for Genesis 
  //    this supposedly configures a logger that everybody can fetch and use
      PropertyConfigurator.configure(ResourceBundle.getBundle(
        Constants.CONFIGURATION_FILE_NAME).getString(Constants.LOG4J_SETTINGS_FILE_KEY));
  //  get one for this routine
      Logger LOG =
          org.apache.log4j.Logger.getLogger(GenesisTest.class);
     
  //    this supposedly configures spring/ojb
     SpringServiceLocator.initializeDDGeneratorApplicationContext();
     BeanFactory factory = SpringServiceLocator.getBeanFactory();
     KualiConfigurationService configService = 
            SpringServiceLocator.getKualiConfigurationService();
     GenesisDao genesisDao = (GenesisDao) factory.getBean("genesisDao");
  //    
      GenesisService genesisTestService = SpringServiceLocator.getGenesisService();
      DateMakerService dateMakerTestService = 
          SpringServiceLocator.getDateMakerService();
      DateTimeService dateTimeService =
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
      // create the proxy BC headers
      genesisTestService.clearDBForGenesis(2009);
      LOG.info("\nDocument creation started: "+String.format("%tT",dateTimeService.getCurrentDate()));
      genesisTestService.createProxyBCHeadersTransactional(2009);
      // create the real BC documents based on the proxies
      genesisDao.createNewBCDocuments(2009);
      LOG.info("\nDocument creation ended: "+String.format("%tT",dateTimeService.getCurrentDate()));
  //
  //    genesisTestService.testStep(2007);
  //    genesisTestService.testSLFStep(2009);
  //    genesisTestService.testSLFAfterStep(2009);
  //    genesisTestService.testLockClearance(2007);
  }
}
