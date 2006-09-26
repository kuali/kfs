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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.test.KualiTestBaseWithSpring;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext
public class OrganizationRoutingModelTest extends KualiTestBaseWithSpring {

    OrganizationRoutingModel model = new OrganizationRoutingModel();

    private final static String MODEL_NAME = "junit-test";

    BusinessObjectService boService;

    protected void setUp() throws Exception {
        super.setUp();
        boService = SpringServiceLocator.getBusinessObjectService();
    }

    public void testSaveModel() {
        model.setOrganizationRoutingModelName(MODEL_NAME);
        model.setChartOfAccountsCode("BL");
        model.setOrganizationCode("AMUS");
        model.setAccountDelegateUniversalId("12345");
        model.setFinancialDocumentTypeCode("xx");

        boService.save(model);
        assertTrue(loadModel(MODEL_NAME));
    }

    public boolean loadModel(String name) {

        Map fieldValues = new HashMap();
        Collection<OrganizationRoutingModel> foundModel;
        fieldValues.put("ORG_RTNG_MDL_NM", name);

        foundModel = boService.findMatching(model.getClass(), fieldValues);

        List<DelegateChangeDocument> delegateChanges = new ArrayList();

        for (OrganizationRoutingModel model : foundModel) {
            delegateChanges.add(new DelegateChangeDocument(model));
        }

        return foundModel != null && foundModel.size() > 0;

    }


}
