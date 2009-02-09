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
package org.kuali.kfs.module.cam.batch.service;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.batch.service.impl.AssetDepreciationServiceImpl;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.dataaccess.AssetDepreciationUtilDao;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciableAssetsDao;
import org.kuali.kfs.module.cam.fixture.AssetDepreciationServiceFixture;
import org.kuali.kfs.module.ld.batch.service.LaborYearEndBalanceForwardService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

@ConfigureContext(session = UserNameFixture.kfs)
//@ConfigureContext(session = UserNameFixture.kfs, shouldCommitTransactions=true)
public class AssetDepreciationServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetDepreciationServiceTest.class);

    private String ERROR_INVALID_DATE="Invalid depreciation date";
    private String ERROR_INVALID_DATE_FORMAT="Invalid depreciation date format";
    private String ERROR_RECORD_NUMBER_DOESNT_MATCH="Depreciated assets collection doesn't have the same number of elements of the results we need to compare against";
    private String ERROR_INVALID_AMOUNTS="Depreciation figures don't match those in the properties file";
    private String PARAMETER_NAME = "parameterName";
    private String PARAMETER_DETAIL_TYPE_CODE = "parameterDetailTypeCode";
    private String PARAMETER_NAMESPACE_CODE = "parameterNamespaceCode";

    private String CAPITAL_ASSETS_NAMESPACE = "KFS-CAM";

    private KualiConfigurationService kualiConfigurationService;
    private AssetDepreciationService camsAssetDepreciationService;
    private BusinessObjectService businessObjectService;    
    private ParameterService parameterService;
    private AssetDepreciationUtilDao assetDepreciationUtilDao;
    private LaborYearEndBalanceForwardService laborYearEndBalanceForwardService;
    private String depreciationDateParameter;
    private UniversityDateDao  universityDateDao;
    List<AssetPayment> assetPaymentsToInsert;
    List<AssetPayment> assetPayments;
    List<Asset> assets;
    private int fiscalMonth;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        camsAssetDepreciationService = SpringContext.getBean(AssetDepreciationService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        kualiConfigurationService= SpringContext.getBean(KualiConfigurationService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
        assetDepreciationUtilDao = SpringContext.getBean(AssetDepreciationUtilDao.class);
        universityDateDao = SpringContext.getBean(UniversityDateDao.class);
    }

    public void testRunDepreciation() throws Exception {
        try {
            //Getting the assets
            assets = AssetDepreciationServiceFixture.DATA.getAssets();        

            //Getting the asset payments that will be used for the test
            assetPaymentsToInsert = AssetDepreciationServiceFixture.DATA.getAssetPaymentsFromPropertiesFile();        

            //Deleting asset payments           
            assetDepreciationUtilDao.deleteAssetPayment(assets);

            //Deleting assets
            assetDepreciationUtilDao.deleteAssets(assets);

            //Deleting glpes
            assetDepreciationUtilDao.deleteGLPEs();
            
            //Storing assets
            assetDepreciationUtilDao.save(assets);
            
            //Inserting the asset payments to be depreciated
            assetDepreciationUtilDao.save(assetPaymentsToInsert);

            //Getting the initial depreciation date from fixture
            String initialDepreciationDate = AssetDepreciationServiceFixture.DATA.getDepreciationDate();
            assertTrue(ERROR_INVALID_DATE,this.isValidDate(initialDepreciationDate));

            String depreciationDate = initialDepreciationDate;
            
            for(int nCounter=0;nCounter < 12;nCounter++) {
                if (nCounter > 0)                
                    depreciationDate = this.getNewDepreciationDate(initialDepreciationDate, nCounter);
                
                //Setting the depreciation date in the parameters table
                this.setDepreciationDate(depreciationDate);

                //Running depreciation
                camsAssetDepreciationService.runDepreciation();
            }
            
            Collection<AssetPayment> depreciatedPayments = assetDepreciationUtilDao.getAssetPayments(assets);
            Collection<AssetPayment> resultsMustGet = AssetDepreciationServiceFixture.DATA.getResultsFromPropertiesFile(); 
            assertTrue(ERROR_RECORD_NUMBER_DOESNT_MATCH,resultsMustGet.size() == depreciatedPayments.size());            
            assertTrue(ERROR_INVALID_AMOUNTS,this.isDepreciationOk(depreciatedPayments, resultsMustGet));           
         } catch(Exception e) {
            throw e;
        }
    }

    
    /**
     * stores the depreciation date into parameters table
     * @param depreciationDateParameter
     */
    public void setDepreciationDate(String depreciationDateParameter) {
        Calendar depreciationDate = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //Validating the parameter exists
        assertTrue("Paramater "+CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER+" doesn't exist!",parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER));

        // This validates the system parameter depreciation_date has a valid format of YYYY-MM-DD.
        if (depreciationDateParameter != null && !depreciationDateParameter.trim().equals("")) {
            try {
                depreciationDate.setTime(dateFormat.parse(depreciationDateParameter));
            }
            catch (ParseException e) {
                throw new IllegalArgumentException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.INVALID_DEPRECIATION_DATE_FORMAT));
            }
        }        
        parameterService.setParameterForTesting(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER, depreciationDateParameter);
    }

    /**
     * 
     * validates the depreciation date
     * @param depreciationDateParameter
     * @return boolean
     */
    public boolean isValidDate(String depreciationDateParameter) {
        boolean valid=true;
        Calendar depreciationDate = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (depreciationDateParameter != null && !depreciationDateParameter.trim().equals("")) {
            try {
                depreciationDate.setTime(dateFormat.parse(depreciationDateParameter));
            }
            catch (ParseException e) {
                valid=false;
            }
        }        
        return valid;
    }

/**
 * 
 * This method...
 * @param depreciationDateParameter
 * @param incrementBy
 * @return
 * @throws Exception
 */
    public String getNewDepreciationDate(String depreciationDateParameter, int incrementBy) throws Exception {
        Calendar depreciationDate = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (incrementBy <= 0)
            return depreciationDateParameter;

        try {
            depreciationDate.setTime(dateFormat.parse(depreciationDateParameter));
        }
        catch (ParseException e) {
            throw new Exception(ERROR_INVALID_DATE_FORMAT);
        }
        depreciationDate.add(Calendar.MONTH, incrementBy);
        return dateFormat.format(depreciationDate.getTime());
    }

    
    
    
    /**
     * 
     * Determines whether or not a calculated depreciation amount has the same value as the on the fixture file
     * @param depreciatedAssets
     * @param resultsMustGet
     * @return
     */
    public boolean isDepreciationOk(Collection<AssetPayment> depreciatedAssets, Collection<AssetPayment> resultsMustGet) {
        boolean result=true;

        try { 
            Iterator<AssetPayment> resultsMustGetIterator = resultsMustGet.iterator();
            Iterator<AssetPayment> depreciatedAssetsIterator = depreciatedAssets.iterator();

            while(depreciatedAssetsIterator.hasNext()) {
                AssetPayment depreciatedAsset = depreciatedAssetsIterator.next();
                AssetPayment resultMustGet    = resultsMustGetIterator.next();

                for(int period=1;period<13;period++){
                    String getterMethodName="getPeriod"+period+"Depreciation1Amount"; 
                    Method method = AssetPayment.class.getMethod(getterMethodName); 

                    KualiDecimal amountCalculated = new KualiDecimal(method.invoke(depreciatedAsset).toString());
                    KualiDecimal amountInFile = new KualiDecimal(method.invoke(resultMustGet).toString());
                    if (amountCalculated.compareTo(amountInFile)!= 0) {
                        result=false;        
                        break;
                    }
                }                
                if (!result)
                    break;
            }
        } catch (Exception re) {
            LOG.info(re.getMessage()); 
        }
        return result;
    }
}