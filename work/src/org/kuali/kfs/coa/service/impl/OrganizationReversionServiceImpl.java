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
package org.kuali.module.chart.service.impl;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.dao.OrganizationReversionDao;
import org.kuali.module.chart.service.OrganizationReversionService;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 * @version $Id$
 */
public class OrganizationReversionServiceImpl implements OrganizationReversionService,BeanFactoryAware {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionServiceImpl.class);

    private OrganizationReversionDao organizationReversionDao;
    private KualiConfigurationService kualiConfigurationService;
    private BeanFactory beanFactory;

    /* (non-Javadoc)
     * @see org.kuali.module.chart.service.OrganizationReversionService#getByKeys(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public OrganizationReversion getByPrimaryId(Integer fiscalYear, String chartCode, String orgCode) {
        LOG.debug("getByPrimaryId() started");
        return organizationReversionDao.getByPrimaryId(fiscalYear, chartCode, orgCode);
    }

    public Map<String,OrganizationReversionCategoryLogic> getCategories() {
        LOG.debug("getCategories() started");

        Map<String,OrganizationReversionCategoryLogic> categories = new HashMap<String,OrganizationReversionCategoryLogic>();

        // Categories must be in the format of Cnn so we'll start with C01 and go until we can't find a rule and
        // assume that is the end of the list.
        int ruleNumber = 1;

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        nf.setMinimumIntegerDigits(2);

        boolean done = false;
        while ( ! done ) {
            String categoryCode = "C" + nf.format(ruleNumber);

            if ( kualiConfigurationService.hasApplicationParameter("OrgReversion", categoryCode + "_Name") ) {
                String name = kualiConfigurationService.getApplicationParameterValue("OrgReversion", categoryCode + "_Name");

                if ( beanFactory.containsBean("gl" + categoryCode + "OrganizationReversionCategory") ) {
                    // We have a custom implementation
                    categories.put(categoryCode,(OrganizationReversionCategoryLogic)beanFactory.getBean("gl" + categoryCode + "OrganizationReversionCategory"));
                } else {
                    // We'll get the generic implementation
                    categories.put(categoryCode, (OrganizationReversionCategoryLogic)beanFactory.getBean("glGenericOrganizationReversionCategory"));
                }
                ruleNumber++;
            } else {
                done = true;
            }
        }
        return categories;
    }

    public void setOrganizationReversionDao(OrganizationReversionDao orDao){
        organizationReversionDao = orDao;
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
