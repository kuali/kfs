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
package org.kuali.kfs.module.cam.document.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.service.impl.AssetServiceImpl;
import org.kuali.kfs.module.cam.document.service.impl.RetirementInfoServiceImpl;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.impl.parameter.ParameterServiceImpl;

public class RetirementInfoServiceTest extends KualiTestBase {
    private RetirementInfoServiceImpl retirementInfoService;
    private DateTimeService dateTimeService;
    private Asset asset;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        this.retirementInfoService = new RetirementInfoServiceImpl();
        this.retirementInfoService.setParameterService(createParameterService());
        AssetServiceImpl assetServiceImpl = new AssetServiceImpl();
        assetServiceImpl.setParameterService(createParameterService());
        this.retirementInfoService.setAssetService(assetServiceImpl);
        this.asset = new Asset();
        this.asset.setInventoryStatusCode("R");

    }

    private ParameterServiceImpl createParameterService() {
        return new ParameterServiceImpl() {
            @Override
            public List<String> getParameterValuesAsString(Class componentClass, String parameterName) {
                List<String> values = new ArrayList<String>();
                values.add("O");
                values.add("R");
                values.add("E");
                return values;
            }
        };
    }

    private AssetRetirementGlobalDetail createRetirementDetail(String docNumber, int daysToAdd, String docStatus) {
        AssetRetirementGlobalDetail globalDetail = new AssetRetirementGlobalDetail();
        globalDetail.setDocumentNumber(docNumber);
        AssetRetirementGlobal retirementGlobal = new AssetRetirementGlobal() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
            }

        };
        retirementGlobal.setRetirementDate(new java.sql.Date(DateUtils.addDays(dateTimeService.getCurrentDate(), daysToAdd).getTime()));
        FinancialSystemDocumentHeader header = new FinancialSystemDocumentHeader();
        header.setFinancialDocumentStatusCode(docStatus);
        retirementGlobal.setDocumentHeader(header);
        globalDetail.setAssetRetirementGlobal(retirementGlobal);
        return globalDetail;
    }

    public void testRetirementInfoService() throws Exception {
        this.asset.getAssetRetirementHistory().add(createRetirementDetail("12345", 0, "A"));
        this.asset.getAssetRetirementHistory().add(createRetirementDetail("123457", 1, "A"));
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNotNull(this.asset.getRetirementInfo());
        assertEquals("123457", this.asset.getRetirementInfo().getDocumentNumber());
    }

    public void testRetirementInfoService_Disapproved() throws Exception {
        this.asset.getAssetRetirementHistory().add(createRetirementDetail("12345", 0, "A"));
        this.asset.getAssetRetirementHistory().add(createRetirementDetail("123457", 1, "D"));
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNotNull(this.asset.getRetirementInfo());
        assertEquals("12345", this.asset.getRetirementInfo().getDocumentNumber());
    }

    public void testRetirementInfoService_NotRetired() throws Exception {
        this.asset.setInventoryStatusCode("A");
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNull(this.asset.getRetirementInfo());
    }
}
