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
package org.kuali.module.gl.batch;

import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.batch.Step;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.gl.service.BalanceService;

public class PurgeBalanceStep implements Step {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeBalanceStep.class);

  private ChartService chartService;
  private BalanceService balanceService;
  private KualiConfigurationService kualiConfigurationService;

  /**
   * This step will purge data from the gl_encumbrance_t table older than a specified year.  It purges the
   * data one chart at a time each within their own transaction so database transaction logs don't get completely
   * filled up when doing this.  This step class should NOT be transactional.
   * 
   */
  public boolean performStep() {
    LOG.debug("performStep() started");

    String yearStr = kualiConfigurationService.getApplicationParameterValue(Constants.ParameterGroups.SYSTEM,
        Constants.SystemGroupParameterNames.PURGE_GL_BALANCE_T_BEFORE_YEAR);

    int year = Integer.parseInt(yearStr);

    List charts = chartService.getAllChartCodes();
    for (Iterator iter = charts.iterator(); iter.hasNext();) {
      String chart = (String)iter.next();
      balanceService.purgeYearByChart(chart,year);
    }

    return true;
  }

  public String getName() {
    return "Purge gl_balance_t";
  }

  public void setBalanceService(BalanceService balanceService) {
    this.balanceService = balanceService;
  }

  public void setChartService(ChartService chartService) {
    this.chartService = chartService;
  }

  public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
    this.kualiConfigurationService = kualiConfigurationService;
  }
}
