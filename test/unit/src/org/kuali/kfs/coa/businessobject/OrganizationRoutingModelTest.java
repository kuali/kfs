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
package org.kuali.module.chart.bo;

import static org.kuali.kfs.util.SpringServiceLocator.getBusinessObjectService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext
public class OrganizationRoutingModelTest extends KualiTestBase {

    OrganizationRoutingModel model;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Map<String,String> fieldValues=new HashMap<String, String>();
        fieldValues.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, "BL");
        fieldValues.put(KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME,"CLAS");
        List<OrganizationRoutingModel> results = (List<OrganizationRoutingModel>)getBusinessObjectService().findMatching(OrganizationRoutingModel.class, fieldValues);
        assertFalse("no models found", results.isEmpty());

        model=results.get(0);
    }

    public void testSaveModel() {
        String name= model.getOrganizationRoutingModelName();
        OrganizationRoutingModel routingModel = new OrganizationRoutingModel();
        routingModel.setOrganizationRoutingModelName(name);
        routingModel.setChartOfAccountsCode(model.getChartOfAccountsCode());
        routingModel.setOrganizationCode(model.getOrganizationCode());
        routingModel.setAccountDelegateUniversalId(model.getAccountDelegateUniversalId());
        routingModel.setFinancialDocumentTypeCode("xx");
        getBusinessObjectService().save(routingModel);

        assertTrue(loadModel(name, model.getClass()));
    }

    private boolean loadModel(String name, Class clazz) {

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("ORG_RTNG_MDL_NM", name);

        Collection<OrganizationRoutingModel> foundModel = getBusinessObjectService().findMatching(clazz, fieldValues);

        List<DelegateChangeDocument> delegateChanges = new ArrayList<DelegateChangeDocument>();

        for (OrganizationRoutingModel model : foundModel) {
            delegateChanges.add(new DelegateChangeDocument(model));
        }

        return (foundModel != null && !foundModel.isEmpty());

    }


}
