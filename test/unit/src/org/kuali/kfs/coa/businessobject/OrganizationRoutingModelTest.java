/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.bo;

import static org.kuali.core.util.SpringServiceLocator.getBusinessObjectService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext
public class OrganizationRoutingModelTest extends KualiTestBase {

    OrganizationRoutingModel model;

    /**
     * @see org.kuali.test.KualiTestBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Map<String,String> fieldValues=new HashMap<String, String>();
        fieldValues.put(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, "BL");
        fieldValues.put(Constants.ORGANIZATION_CODE_PROPERTY_NAME,"CLAS");
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
