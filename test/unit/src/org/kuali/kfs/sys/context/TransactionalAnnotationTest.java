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
package org.kuali.kfs.context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.kuali.core.lookup.LookupResultsServiceImpl;
import org.kuali.core.service.impl.BusinessObjectServiceImpl;
import org.kuali.core.service.impl.KeyValuesServiceImpl;
import org.kuali.core.service.impl.KualiModuleUserPropertyServiceImpl;
import org.kuali.core.service.impl.LookupServiceImpl;
import org.kuali.core.service.impl.PersistenceServiceImpl;
import org.kuali.core.service.impl.PostDataLoadEncryptionServiceImpl;
import org.kuali.core.service.impl.SequenceAccessorServiceImpl;
import org.kuali.kfs.service.impl.AccountingLineServiceImpl;
import org.kuali.kfs.service.impl.HomeOriginationServiceImpl;
import org.kuali.kfs.service.impl.KualiCodeServiceImpl;
import org.kuali.kfs.service.impl.OptionsServiceImpl;
import org.kuali.kfs.service.impl.OriginationCodeServiceImpl;
import org.kuali.module.budget.service.impl.BudgetRequestImportServiceImpl;
import org.kuali.module.cg.service.impl.CgUserServiceImpl;
import org.kuali.module.cg.service.impl.ContractsAndGrantsModuleServiceImpl;
import org.kuali.module.chart.service.impl.A21SubAccountServiceImpl;
import org.kuali.module.chart.service.impl.AccountServiceImpl;
import org.kuali.module.chart.service.impl.BalanceTypServiceImpl;
import org.kuali.module.chart.service.impl.ChartServiceImpl;
import org.kuali.module.chart.service.impl.ChartUserServiceImpl;
import org.kuali.module.chart.service.impl.ObjectCodeServiceImpl;
import org.kuali.module.chart.service.impl.ObjectConsServiceImpl;
import org.kuali.module.chart.service.impl.ObjectLevelServiceImpl;
import org.kuali.module.chart.service.impl.ObjectTypeServiceImpl;
import org.kuali.module.chart.service.impl.OffsetDefinitionServiceImpl;
import org.kuali.module.chart.service.impl.OrganizationReversionServiceImpl;
import org.kuali.module.chart.service.impl.OrganizationServiceImpl;
import org.kuali.module.chart.service.impl.ProjectCodeServiceImpl;
import org.kuali.module.chart.service.impl.SubAccountServiceImpl;
import org.kuali.module.chart.service.impl.SubFundGroupServiceImpl;
import org.kuali.module.chart.service.impl.SubObjectCodeServiceImpl;
import org.kuali.module.financial.service.impl.AccountPresenceServiceImpl;
import org.kuali.module.financial.service.impl.CheckServiceImpl;
import org.kuali.module.financial.service.impl.DisbursementVoucherTravelServiceImpl;
import org.kuali.module.financial.service.impl.FinancialUserServiceImpl;
import org.kuali.module.financial.service.impl.UniversityDateServiceImpl;
import org.kuali.module.gl.service.impl.GlUserServiceImpl;
import org.kuali.module.gl.service.impl.OrganizationReversionMockService;
import org.kuali.module.gl.service.impl.ScrubberValidatorImpl;
import org.kuali.module.kra.service.impl.KraUserServiceImpl;
import org.kuali.module.labor.service.impl.LaborBaseFundsServiceImpl;
import org.kuali.module.labor.service.impl.LaborUserServiceImpl;
import org.kuali.module.pdp.service.impl.BatchSearchServiceImpl;
import org.kuali.module.pdp.service.impl.DailyReportServiceImpl;
import org.kuali.module.pdp.service.impl.PaymentDetailSearchServiceImpl;
import org.kuali.module.pdp.service.impl.PaymentDetailServiceImpl;
import org.kuali.module.pdp.service.impl.PdpUserServiceImpl;
import org.kuali.module.purap.service.impl.NegativePaymentRequestApprovalLimitServiceImpl;
import org.kuali.module.purap.service.impl.PurapAccountingServiceImpl;
import org.kuali.module.purap.service.impl.PurapUserServiceImpl;
import org.kuali.module.vendor.service.impl.VendorUserServiceImpl;
import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.PreCommitSuite;
import org.springframework.aop.framework.AopProxyUtils;
@AnnotationTestSuite(PreCommitSuite.class)
@ConfigureContext
public class TransactionalAnnotationTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionalAnnotationTest.class);

    Map<Class, Boolean> seenClasses = new HashMap();
    List<Class> excludedClasses;
    
    public void setUp() throws Exception {
        super.setUp();
        excludedClasses = new ArrayList<Class>();
        excludedClasses.add( BusinessObjectServiceImpl.class );
        excludedClasses.add( PersistenceServiceImpl.class );
        excludedClasses.add( SubFundGroupServiceImpl.class );
        excludedClasses.add( AccountingLineServiceImpl.class );
        excludedClasses.add( HomeOriginationServiceImpl.class );
        excludedClasses.add( ObjectTypeServiceImpl.class );
        excludedClasses.add( ObjectLevelServiceImpl.class );
        excludedClasses.add( ObjectConsServiceImpl.class );
        excludedClasses.add( ObjectCodeServiceImpl.class );
        excludedClasses.add( OrganizationReversionServiceImpl.class );
        excludedClasses.add( UniversityDateServiceImpl.class );
        excludedClasses.add( OrganizationReversionMockService.class );
        excludedClasses.add( SubAccountServiceImpl.class );

        excludedClasses.add( SubObjectCodeServiceImpl.class );
        excludedClasses.add( ProjectCodeServiceImpl.class );
        excludedClasses.add( BalanceTypServiceImpl.class );
        excludedClasses.add( KualiCodeServiceImpl.class );
        excludedClasses.add( OriginationCodeServiceImpl.class );
        excludedClasses.add( ChartUserServiceImpl.class );
        excludedClasses.add( FinancialUserServiceImpl.class );
        excludedClasses.add( GlUserServiceImpl.class );
        excludedClasses.add( VendorUserServiceImpl.class );
        excludedClasses.add( PdpUserServiceImpl.class );
        excludedClasses.add( PurapUserServiceImpl.class );
        excludedClasses.add( OffsetDefinitionServiceImpl.class );
        excludedClasses.add( ChartServiceImpl.class );
        excludedClasses.add( OptionsServiceImpl.class );
        excludedClasses.add( CheckServiceImpl.class );
        excludedClasses.add( PostDataLoadEncryptionServiceImpl.class );
        excludedClasses.add( KualiModuleUserPropertyServiceImpl.class );
        
        excludedClasses.add( KeyValuesServiceImpl.class );
        excludedClasses.add( SequenceAccessorServiceImpl.class );
        excludedClasses.add( BatchSearchServiceImpl.class );
        excludedClasses.add( LookupResultsServiceImpl.class );
        excludedClasses.add( PostDataLoadEncryptionServiceImpl.class );

        excludedClasses.add( PaymentDetailSearchServiceImpl.class );
        excludedClasses.add( LookupServiceImpl.class );

        excludedClasses.add( A21SubAccountServiceImpl.class );
        excludedClasses.add( AccountPresenceServiceImpl.class );
        excludedClasses.add( AccountServiceImpl.class );
        excludedClasses.add( ContractsAndGrantsModuleServiceImpl.class );
        excludedClasses.add( CgUserServiceImpl.class );
        excludedClasses.add( DisbursementVoucherTravelServiceImpl.class );
        excludedClasses.add( KraUserServiceImpl.class );
        excludedClasses.add( LaborBaseFundsServiceImpl.class );
        excludedClasses.add( LaborUserServiceImpl.class );
        excludedClasses.add( NegativePaymentRequestApprovalLimitServiceImpl.class );
        excludedClasses.add( OrganizationServiceImpl.class );
        excludedClasses.add( DailyReportServiceImpl.class );
        excludedClasses.add( PaymentDetailServiceImpl.class );
        excludedClasses.add( PurapAccountingServiceImpl.class );
        excludedClasses.add( ScrubberValidatorImpl.class );
        
        excludedClasses.add( BudgetRequestImportServiceImpl.class );
    }

    public void testTransactionAnnotations() {
        Map<String, Class> nonAnnotatedTransactionalServices = getNonAnnotatedTransactionalServices();
        for (String beanName : new TreeSet<String>(nonAnnotatedTransactionalServices.keySet())) {
            LOG.error(String.format("Service Bean improperly annotated: %s <%s>\n", beanName, nonAnnotatedTransactionalServices.get(beanName).getName()));
        }
        int count = nonAnnotatedTransactionalServices.size();
        StringBuffer failureMessage = new StringBuffer("Transaction support for ").append(count).append(count == 1 ? " Service" : " Services").append(" improperly annotated: ");
        for (String serviceName : nonAnnotatedTransactionalServices.keySet()) {
            failureMessage.append("\t").append(serviceName).append(": ").append(nonAnnotatedTransactionalServices.get(serviceName));
        }
        assertTrue(failureMessage.toString(), nonAnnotatedTransactionalServices.isEmpty());

    }

    public Map getNonAnnotatedTransactionalServices() {
        Map<String, Class> nonAnnotatedTransactionalServices = new HashMap();
        String[] beanNames = SpringContext.getBeanNames();
        for (String beanName : beanNames) {
            Object bean = null;
            try {
                bean = SpringContext.getBean(beanName);
            }
            catch (Exception e) {
                LOG.warn("Caught exception while trying to obtain service: " + beanName);
            }
            if (bean != null) {
                Class beanClass = bean.getClass();
                if (beanClass.getName().startsWith("$Proxy")) {
                    beanClass = AopProxyUtils.getTargetClass(bean);
                }
                if (beanClass.getName().startsWith("org.kuali") 
                        && !Modifier.isAbstract(beanClass.getModifiers()) 
                        && !beanClass.getName().endsWith("DaoOjb") 
                        && !beanClass.getName().endsWith("Factory") 
                        && !beanClass.getName().contains("Lookupable") 
                        && !isClassAnnotated(beanName, beanClass)
                        && !excludedClasses.contains(beanClass) ) {
                    nonAnnotatedTransactionalServices.put(beanName, beanClass);
                }
            }
        }
        return nonAnnotatedTransactionalServices;
    }

    private boolean isClassAnnotated(String beanName, Class beanClass) {
        boolean hasClassAnnotation = false;
        if (shouldHaveTransaction(beanClass)){
            if (beanClass.getAnnotation(org.springframework.transaction.annotation.Transactional.class) != null) {
                hasClassAnnotation = true;
            }
            if (beanClass.getAnnotation(org.kuali.kfs.annotation.NonTransactional.class) != null){
                hasClassAnnotation =  true;
            }


            boolean hasMethodAnnotation;
            for( Method beanMethod : beanClass.getDeclaredMethods()){
                hasMethodAnnotation = false;
                if (beanMethod.getAnnotation(org.springframework.transaction.annotation.Transactional.class) != null) hasMethodAnnotation = true;
                if (beanMethod.getAnnotation(org.kuali.kfs.annotation.NonTransactional.class) != null) hasMethodAnnotation = true;
                if (hasMethodAnnotation == false && hasClassAnnotation == false) return false; 
                if (hasMethodAnnotation == true && hasClassAnnotation == true)return false;

            }
            return true;
        }
        return true;

        //return !shouldHaveTransaction(beanClass);
    }

    /*
     * Recursively seek evidence that a Transaction is necessary by examining the fields of the given class and recursively
     * investigating its superclass.
     */
    private boolean shouldHaveTransaction(Class beanClass) {
        Boolean result = seenClasses.get(beanClass);
        if (result != null)
            return result;
        if (seenClasses.containsKey(beanClass)) {
            return false;
        }
        seenClasses.put(beanClass, null); // placeholder to avoid recursive problems
        result = Boolean.FALSE;
        for (Field field : beanClass.getDeclaredFields()) {
            String name = field.getType().getName();
            String fieldTypeName = field.getType().getName();
            if (fieldTypeName.startsWith("org.apache.ojb")) {
                if (!fieldTypeName.equals("org.apache.ojb.broker.metadata.DescriptorRepository")) {
                    result = Boolean.TRUE;
                }
            }
            if (name.startsWith("org.kuali")) {
                if (name.contains("Dao")) {
                    result = Boolean.TRUE;
                }
                if (result == false) {
                    result = shouldHaveTransaction(field.getType());
                }
            }
        }
        if (result == false) {
            if (beanClass.getSuperclass() != null && beanClass.getSuperclass().getName().startsWith("org.kuali")) {
                result = shouldHaveTransaction(beanClass.getSuperclass());
            }
        }
        seenClasses.put(beanClass, result);
        return result;
    }
}
