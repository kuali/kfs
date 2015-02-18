/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.coa.businessobject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext
public class AccountDelegateModelTest extends KualiTestBase {

    AccountDelegateModelDetail model;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, "BL");
        fieldValues.put(KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME, "CLAS");
        List<AccountDelegateModelDetail> results = (List<AccountDelegateModelDetail>) SpringContext.getBean(BusinessObjectService.class).findMatching(AccountDelegateModelDetail.class, fieldValues);
        assertFalse("no models found", results.isEmpty());

        model = results.get(0);
    }

    public void testSaveModel() {
        String name = model.getAccountDelegateModelName();
        AccountDelegateModelDetail routingModel = new AccountDelegateModelDetail();
        routingModel.setAccountDelegateModelName(name);
        routingModel.setChartOfAccountsCode(model.getChartOfAccountsCode());
        routingModel.setOrganizationCode(model.getOrganizationCode());
        routingModel.setAccountDelegateUniversalId(model.getAccountDelegateUniversalId());
        routingModel.setFinancialDocumentTypeCode("GDLM");
        SpringContext.getBean(BusinessObjectService.class).save(routingModel);

        assertTrue(loadModel(name, model.getClass()));
    }

    private boolean loadModel(String name, Class clazz) {

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("accountDelegateModelName", name);

        Collection<AccountDelegateModelDetail> foundModel = SpringContext.getBean(BusinessObjectService.class).findMatching(clazz, fieldValues);

//        List<AccountDelegateGlobalDetail> delegateGlobals = new ArrayList<AccountDelegateGlobalDetail>();
//
//        for (AccountDelegateModelDetail model : foundModel) {
//            delegateGlobals.add(new AccountDelegateGlobalDetail(model));
//        }

        return (foundModel != null && !foundModel.isEmpty());

    }


}
