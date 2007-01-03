/*
 * Copyright 2006 The Kuali Foundation.
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
import org.kuali.core.util.SpringServiceLocator.*;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.budget.dao.ojb.*;

// import these things to handle the configuration
import org.kuali.core.service.KualiConfigurationService;
import org.springframework.beans.factory.BeanFactory;
//this is just for the logger, and could be taken out
import org.apache.log4j.PropertyConfigurator;
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
     
  //    this supposedly configures spring/ojb
     SpringServiceLocator.initializeDDGeneratorApplicationContext();
     BeanFactory factory = SpringServiceLocator.getBeanFactory();
     KualiConfigurationService configService = 
            SpringServiceLocator.getKualiConfigurationService();
  //    
      GenesisService genesisTestService = SpringServiceLocator.getGenesisService();
      DateMakerService dateMakerTestService = 
          SpringServiceLocator.getDateMakerService();
  //
  //    genesisTestService.testStep(2007);
        genesisTestService.testSLFStep(2009);
  }
}
