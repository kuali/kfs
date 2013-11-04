/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.batch.JobDescriptor;
import org.kuali.kfs.sys.batch.TriggerDescriptor;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker;
import org.kuali.kfs.sys.batch.dataaccess.impl.FiscalYearMakerImpl;
import org.kuali.kfs.sys.service.impl.KfsModuleServiceImpl;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.PreCommitSuite;
import org.kuali.rice.core.framework.persistence.dao.PlatformAwareDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.krad.service.ModuleService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;

@ConfigureContext
@AnnotationTestSuite(PreCommitSuite.class)
public class SpringConfigurationConsistencyCheckTest extends KualiTestBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SpringConfigurationConsistencyCheckTest.class);

    public void testAllLookupablesArePrototypes() throws Exception {
        List<String> failingBeans = new ArrayList<String>();

        Map<String,Lookupable> beans = SpringContext.getBeansOfType(Lookupable.class);
        Map<String,Lookupable> beans2 = SpringContext.getBeansOfType(Lookupable.class);

        for ( String beanName : beans.keySet() ) {
            BeanDefinition beanDef = SpringContext.applicationContext.getBeanFactory().getBeanDefinition(beanName);
            // skip entries in the rice import files or in testing files
            if ( StringUtils.contains( beanDef.getResourceDescription(), "spring-kfs-imported-rice-beans.xml" )
                    || StringUtils.contains( beanDef.getResourceDescription(), "-test.xml" ) ) {
                continue;
            }
            if ( ProxyUtils.getTargetIfProxied( beans.get(beanName) ).equals(ProxyUtils.getTargetIfProxied( beans2.get(beanName) )) ) {
                failingBeans.add( "\n *** " + beanName + " is a singleton and should not be." );
            }
        }
        assertEquals( "Beans Failing Non-Singleton check: " + failingBeans, 0, failingBeans.size() );
    }

    public void testAllLookupableHelperServicesArePrototypes() throws Exception {
        List<String> failingBeans = new ArrayList<String>();

        Map<String,LookupableHelperService> beans = SpringContext.getBeansOfType(LookupableHelperService.class);
        Map<String,LookupableHelperService> beans2 = SpringContext.getBeansOfType(LookupableHelperService.class);

        for ( String beanName : beans.keySet() ) {
            BeanDefinition beanDef = SpringContext.applicationContext.getBeanFactory().getBeanDefinition(beanName);
            // skip entries in the rice import files or in testing files
            if ( StringUtils.contains( beanDef.getResourceDescription(), "spring-kfs-imported-rice-beans.xml" )
                    || StringUtils.contains( beanDef.getResourceDescription(), "-test.xml" ) ) {
                continue;
            }
            if ( ProxyUtils.getTargetIfProxied( beans.get(beanName) ).equals(ProxyUtils.getTargetIfProxied( beans2.get(beanName) )) ) {
                failingBeans.add( "\n *** " + beanName + " is a singleton and should not be. (" + beanDef.getResourceDescription() + ")" );
            }
        }
        assertEquals( "Beans Failing Non-Singleton check: " + failingBeans, 0, failingBeans.size() );
    }

    public void testJobBeansReferencedInModuleDefinitions() throws Exception {
        // get all Job beans
        Map<String,JobDescriptor> jobs = SpringContext.getBeansOfType(JobDescriptor.class);
        Map<String,ModuleService> moduleServices = SpringContext.getBeansOfType(ModuleService.class);
        assertFalse( "Jobs list must not be empty", jobs.isEmpty() );
        assertFalse( "Module list must not be empty", moduleServices.isEmpty() );

        // build a list of all job names
        Set<String> jobNamesInBeans = jobs.keySet();
        Set<String> jobNamesInModules = new HashSet<String>();
        for ( ModuleService m : moduleServices.values() ) {
            jobNamesInModules.addAll( m.getModuleConfiguration().getJobNames() );
        }

        Set<String> beansNotReferencedInModules = new HashSet<String>( jobNamesInBeans );
        beansNotReferencedInModules.removeAll(jobNamesInModules);
        Set<String> moduleJobsWithNoBeans = new HashSet<String>( jobNamesInModules );
        moduleJobsWithNoBeans.removeAll(jobNamesInBeans);

        assertEquals( "All of the job definitions must be referenced in the module definitions.", Collections.emptySet(), beansNotReferencedInModules );
        assertEquals( "All of the jobs in the module definitions must be defined in the Spring context.", Collections.emptySet(), moduleJobsWithNoBeans );
    }

    public void testTriggerBeansReferencedInModuleDefinitions() throws Exception {
        // get all Job beans
        Map<String,TriggerDescriptor> triggers = SpringContext.getBeansOfType(TriggerDescriptor.class);
        Map<String,ModuleService> moduleServices = SpringContext.getBeansOfType(ModuleService.class);
        assertFalse( "Jobs list must not be empty", triggers.isEmpty() );
        assertFalse( "Module list must not be empty", moduleServices.isEmpty() );

        // build a list of all job names
        Set<String> triggerNamesInBeans = triggers.keySet();
        Set<String> triggerNamesInModules = new HashSet<String>();
        for ( ModuleService m : moduleServices.values() ) {
            triggerNamesInModules.addAll( m.getModuleConfiguration().getTriggerNames() );
        }
        Set<String> beansNotReferencedInModules = new HashSet<String>( triggerNamesInBeans );
        beansNotReferencedInModules.removeAll(triggerNamesInModules);
        Set<String> moduleTriggersWithNoBeans = new HashSet<String>( triggerNamesInModules );
        moduleTriggersWithNoBeans.removeAll(triggerNamesInBeans);

        assertEquals( "All of the trigger definitions must be referenced in the module definitions.", Collections.emptySet(), beansNotReferencedInModules );
        assertEquals( "All of the triggers in the module definitions must be defined in the Spring context.", Collections.emptySet(), moduleTriggersWithNoBeans );
    }

    // FY makers referenced in module definitions
    public void testFiscalYearMakerBeansReferencedInModuleDefinitions() throws Exception {
        // get all Job beans
        Map<String,FiscalYearMakerImpl> fiscalYearMakers = SpringContext.getBeansOfType(FiscalYearMakerImpl.class);
        Map<String,KfsModuleServiceImpl> moduleServices = SpringContext.getBeansOfType(KfsModuleServiceImpl.class);
        assertFalse( "FiscalYearMaker list must not be empty", fiscalYearMakers.isEmpty() );
        assertFalse( "Module list must not be empty", moduleServices.isEmpty() );

        // build a list of all job names
        Set<FiscalYearMakerImpl> fiscalYearMakerNamesInBeans = new HashSet<FiscalYearMakerImpl>();
        for ( FiscalYearMakerImpl fym : fiscalYearMakers.values() ) {
            fiscalYearMakerNamesInBeans.add( (FiscalYearMakerImpl)ProxyUtils.getTargetIfProxied(fym) );
        }
        Set<FiscalYearMakerImpl> fiscalYearMakerNamesInModules = new HashSet<FiscalYearMakerImpl>();
        for ( KfsModuleServiceImpl m : moduleServices.values() ) {
            if ( m.getModuleConfiguration() instanceof FinancialSystemModuleConfiguration ) {
                for ( FiscalYearMaker fym : ((FinancialSystemModuleConfiguration)m.getModuleConfiguration()).getFiscalYearMakers() ) {
                    fiscalYearMakerNamesInModules.add( (FiscalYearMakerImpl)ProxyUtils.getTargetIfProxied(fym) );
                }
            } else {
                LOG.warn( "Possible problem.  KFS Module service does not contain a KFS module configuration: " + m + "\n Contains: " + m.getModuleConfiguration().getClass().getName() );
            }
        }
        Set<FiscalYearMakerImpl> beansNotReferencedInModules = new HashSet<FiscalYearMakerImpl>( fiscalYearMakerNamesInBeans );
        beansNotReferencedInModules.removeAll(fiscalYearMakerNamesInModules);
        Set<FiscalYearMakerImpl> moduleFiscalYearMakersWithNoBeans = new HashSet<FiscalYearMakerImpl>( fiscalYearMakerNamesInModules );
        moduleFiscalYearMakersWithNoBeans.removeAll(fiscalYearMakerNamesInBeans);

        assertEquals( "All of the FiscalYearMaker definitions must be referenced in the module definitions.", Collections.emptySet(), beansNotReferencedInModules );
        assertEquals( "All of the FiscalYearMakers in the module definitions must be defined in the Spring context.", Collections.emptySet(), moduleFiscalYearMakersWithNoBeans );
    }

    public void testParentBeansShouldBeAbstract() {
        List<String> failingBeanNames = new ArrayList<String>();
        for ( String beanName : SpringContext.applicationContext.getBeanDefinitionNames() ) {
            BeanDefinition beanDef = SpringContext.applicationContext.getBeanFactory().getBeanDefinition(beanName);
            // skip entries in the rice import files or in testing files
            if ( StringUtils.contains( beanDef.getResourceDescription(), "spring-kfs-imported-rice-beans.xml" )
                    || StringUtils.contains( beanDef.getResourceDescription(), "-test.xml" ) ) {
                continue;
            }
            if ( beanName.endsWith("-parentBean") && !beanDef.isAbstract() ) {
                failingBeanNames.add(beanName + " : " + beanDef.getResourceDescription()+"\n");
            }
        }
        assertEquals( "The following parent beans are not defined as abstract:\n" + failingBeanNames, 0, failingBeanNames.size() );
    }

    public void testServicesShouldHaveParentBeans() {
        List<String> failingBeanNames = new ArrayList<String>();
        for ( String beanName : SpringContext.applicationContext.getBeanDefinitionNames() ) {
            // skip testing mock beans
            if ( StringUtils.containsIgnoreCase(beanName, "mock") ) {
                continue;
            }
            BeanDefinition beanDef = SpringContext.applicationContext.getBeanFactory().getBeanDefinition(beanName);
            // skip entries in the rice import files or in testing files
            if ( StringUtils.contains( beanDef.getResourceDescription(), "spring-kfs-imported-rice-beans.xml" )
                    || StringUtils.contains( beanDef.getResourceDescription(), "-test.xml" ) ) {
                continue;
            }
            String serviceClass = beanDef.getBeanClassName();
            if (  StringUtils.contains(serviceClass, "service.impl") &&  //should be a service
                    !StringUtils.startsWith(serviceClass, "org.kuali.rice") && //let rice test their code
                    !beanDef.isAbstract() ) { //abstract = parent

                try {
                    BeanDefinition parentBean = SpringContext.applicationContext.getBeanFactory().getBeanDefinition(beanName + "-parentBean");
                    String parentClass = parentBean.getBeanClassName();
                } catch ( NoSuchBeanDefinitionException ex ) {
                    failingBeanNames.add(beanName + " : " + beanDef.getResourceDescription()+"\n");
                }
            }
        }
        assertEquals( "The following service beans do not have \"-parentBean\"s:\n" + failingBeanNames, 0, failingBeanNames.size() );
    }

    // DAOs should extend from the xxxx class
    public void testDAOsShouldBeDAOs() throws Exception {
        List<String> failingBeanNames = new ArrayList<String>();
        for ( String beanName : SpringContext.applicationContext.getBeanDefinitionNames() ) {
            BeanDefinition beanDef = SpringContext.applicationContext.getBeanFactory().getBeanDefinition(beanName);
            // skip entries in the rice import files or in testing files
            if ( StringUtils.contains( beanDef.getResourceDescription(), "spring-kfs-imported-rice-beans.xml" )
                    || StringUtils.contains( beanDef.getResourceDescription(), "-test.xml" ) ) {
                continue;
            }
            if ( !beanDef.isAbstract() ) {
                Object service = TestUtils.getUnproxiedService(beanName);
                if ( beanName.endsWith("Dao")
                        && !service.getClass().getName().endsWith( "Proxy" )
                        && !service.getClass().getName().startsWith( "org.kuali.rice" ) ) {
                    if ( !(service instanceof PlatformAwareDao) ) {
                        failingBeanNames.add( " *** FAIL: " + beanName + " does not implement PlatformAwareDao (is " + service.getClass().getName() +  ")\n"  + " : " + beanDef.getResourceDescription() + "\n" );
                    }
                    if ( !(service instanceof PlatformAwareDaoBaseOjb)
                            && !(service instanceof PlatformAwareDaoBaseJdbc)) {
                        failingBeanNames.add( " *** FAIL: " + beanName + " does not extend PlatformAwareDaoBaseOjb/Jdbc (is " + service.getClass().getName() +  ")\n" + " : " + beanDef.getResourceDescription() + "\n" );
                    }
                }
            }
        }
        for ( Map.Entry<String,PlatformAwareDao> dao : SpringContext.getBeansOfType(PlatformAwareDao.class).entrySet() ) {
            Object service = ProxyUtils.getTargetIfProxied(dao.getValue());
            if ( !dao.getKey().endsWith("Dao" )
                    && !dao.getKey().endsWith("DaoBase" )
                    && !service.getClass().getName().startsWith( "org.kuali.rice" )
                    && !FiscalYearMakerImpl.class.isAssignableFrom(service.getClass())) {
                failingBeanNames.add( " *** FAIL: Bean " + dao.getKey() + " implements PlatformAwareDao (" + service.getClass().getName() + ") but its name does not end in 'Dao'\n");
            }
        }

        assertEquals( "The following problems were detected in the DAO definitions:\n" + failingBeanNames, 0, failingBeanNames.size() );
    }
}
