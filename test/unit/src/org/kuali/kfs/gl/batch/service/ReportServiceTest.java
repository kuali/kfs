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
package org.kuali.module.gl.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.GLSpringBeansRegistry;
import org.kuali.test.KualiTestBaseWithSpringOnly;
import org.springframework.beans.factory.BeanFactory;

/**
 * This class...
 * 
 * @author Bin Gao
 */
public class ReportServiceTest extends KualiTestBaseWithSpringOnly {
    private ReportService reportService;
    private Date runDate;

    /**
     * @see org.kuali.test.KualiTestBaseWithSpringOnly#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        reportService = (ReportService) beanFactory.getBean(GLSpringBeansRegistry.glReportService);
    }

    public void testGenerateScrubberReportsByGroupIdList() throws Exception {
        List reportSummary = new ArrayList();
        Map reportErrors = new HashMap();

        List groupIdList = new ArrayList();
        groupIdList.add(new Integer(46794));
        groupIdList.add(new Integer(46795));
        groupIdList.add(new Integer(46796));
        groupIdList.add(new Integer(46813));
        groupIdList.add(new Integer(46794));
        groupIdList.add(new Integer(46795));

        runDate = new Date(System.currentTimeMillis());
        reportService.generateScrubberReports(runDate, reportSummary, reportErrors, groupIdList);
    }

    public void testGenerateScrubberReportingPerformance() throws Exception {
        List reportSummary = new ArrayList();
        Map reportErrors = new HashMap();
        List groupIdList = this.getGroupIdList();

        runDate = new Date(System.currentTimeMillis());
        long startTime = System.currentTimeMillis();
        reportService.generateScrubberReports(runDate, reportSummary, reportErrors, groupIdList);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println(duration);
    }

    private List getGroupIdList() {
        List groupIdList = new ArrayList();
        groupIdList.add(new Integer(46709));
        groupIdList.add(new Integer(46710));
        groupIdList.add(new Integer(46739));
        groupIdList.add(new Integer(46740));
        groupIdList.add(new Integer(46741));
        groupIdList.add(new Integer(46748));
        groupIdList.add(new Integer(46794));
        groupIdList.add(new Integer(46795));
        groupIdList.add(new Integer(46796));
        groupIdList.add(new Integer(46813));
        groupIdList.add(new Integer(46814));
        groupIdList.add(new Integer(46815));
        groupIdList.add(new Integer(46817));
        groupIdList.add(new Integer(46818));
        groupIdList.add(new Integer(46819));
        groupIdList.add(new Integer(46820));
        groupIdList.add(new Integer(46821));
        groupIdList.add(new Integer(46827));
        groupIdList.add(new Integer(46828));
        groupIdList.add(new Integer(46868));
        groupIdList.add(new Integer(46869));
        groupIdList.add(new Integer(46870));
        groupIdList.add(new Integer(46871));
        groupIdList.add(new Integer(46872));
        groupIdList.add(new Integer(46991));
        groupIdList.add(new Integer(46992));
        groupIdList.add(new Integer(47653));
        groupIdList.add(new Integer(47654));
        groupIdList.add(new Integer(48081));
        groupIdList.add(new Integer(48082));
        groupIdList.add(new Integer(48083));
        groupIdList.add(new Integer(48088));
        groupIdList.add(new Integer(48089));
        groupIdList.add(new Integer(48090));
        groupIdList.add(new Integer(48154));
        groupIdList.add(new Integer(48155));
        groupIdList.add(new Integer(48158));

        return groupIdList;
    }
}
