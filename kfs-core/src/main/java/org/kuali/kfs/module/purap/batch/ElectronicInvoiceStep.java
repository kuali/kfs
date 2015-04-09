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
package org.kuali.kfs.module.purap.batch;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class ElectronicInvoiceStep extends AbstractStep {

    private static Logger LOG = Logger.getLogger(ElectronicInvoiceStep.class);

    private ElectronicInvoiceHelperService electronicInvoiceHelperService;

    public boolean execute(String jobName, Date jobRunDate) {
        ElectronicInvoiceLoad load = electronicInvoiceHelperService.loadElectronicInvoices();
        return true;
    }

    public void setElectronicInvoiceHelperService(ElectronicInvoiceHelperService electronicInvoiceHelperService) {
        this.electronicInvoiceHelperService = electronicInvoiceHelperService;
    }

    /**
     * Override the default implementation to pull the directory path list from the service
     *
     * @see org.kuali.kfs.sys.batch.AbstractStep#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return electronicInvoiceHelperService.getRequiredDirectoryNames();
    }
}
