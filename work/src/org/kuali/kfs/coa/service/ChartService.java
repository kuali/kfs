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
package org.kuali.module.chart.service;

import java.util.List;
import java.util.Map;

import org.kuali.core.bo.user.KualiUser;
import org.kuali.module.chart.bo.Chart;

/**
 * This interface defines methods that a Chart Service must provide
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public interface ChartService {
    /**
     * Retrieves a chart object by its primary key - the chart code.
     * 
     * @param chartOfAccountsCode
     * @return
     */
    public Chart getByPrimaryId(String chartOfAccountsCode);
    
    public Chart getUniversityChart();

    /**
     * Retrieves all of the charts in the system and returns them in a List.
     * 
     * @return A List of chart objects.
     */
    public List getAllChartCodes();

    /**
     * 
     * Retrieves a map of reportsTo relationships (e.g. A reports to B, B reports to B, C reports to A)
     * 
     * @return
     */
    public Map<String,String> getReportsToHierarchy();

    /**
     * 
     * Retrieves a list of chart objects that the User is responsible for
     * 
     * @param kualiUser
     * @return
     */
    public List getChartsThatUserIsResponsibleFor(KualiUser kualiUser);

}