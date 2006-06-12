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
package org.kuali.module.gl.service.impl;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.chart.dao.OrganizationReversionDao;
import org.kuali.module.chart.service.OrganizationReversionService;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.service.OrganizationReversionProcessService;
import org.kuali.module.gl.service.OrganizationReversionSelection;
import org.kuali.module.gl.service.impl.orgreversion.OrganizationReversionProcess;
import org.springframework.beans.factory.BeanFactory;

public class OrganizationReversionProcessServiceImpl implements OrganizationReversionProcessService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionProcessServiceImpl.class);

    private OrganizationReversionService organizationReversionService;
    private KualiConfigurationService kualiConfigurationService;
    private BeanFactory beanFactory;
    private BalanceService balanceService;
    private OrganizationReversionSelection organizationReversionSelection;

    /**
     * 
     * @see org.kuali.module.gl.service.OrganizationReversionProcessService#organizationReversionProcess()
     */
    public void organizationReversionProcess() {
        LOG.debug("organizationReversionProcess() started");

        OrganizationReversionProcess orp = new OrganizationReversionProcess(organizationReversionService,kualiConfigurationService,
                beanFactory,balanceService,organizationReversionSelection);
        orp.organizationReversionProcess();
    }
}
