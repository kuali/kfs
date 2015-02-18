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
package org.kuali.kfs.gl.batch;

import org.kuali.kfs.sys.KualiTestConstants.TestConstants.Data1;

/**
 * Mock object for CollectorBatch. Used so the object does not need built from XML everytime we need it for a parameter.
 */
public class MockCollectorBatch extends CollectorBatch {

    /**
     * @see org.kuali.kfs.gl.batch.CollectorBatch#getChartOfAccountsCode()
     */
    @Override
    public String getChartOfAccountsCode() {
        return Data1.CHART_OF_ACCOUNTS_CODE;
    }

    /**
     * @see org.kuali.kfs.gl.batch.CollectorBatch#getOrganizationCode()
     */
    @Override
    public String getOrganizationCode() {
        return Data1.ORGANIZATION_CODE;
    }

}
