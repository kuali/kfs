/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.gl.batch;

import java.io.PrintStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.exceptions.ApplicationParameterException;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.batch.closing.year.service.impl.YearEndServiceImpl;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.GeneralLedgerTestHelper;

/**
 * @author Kuali General Ledger Team (kualigltech@oncourse.iu.edu)
 * @version $Id$
 */
public class BalanceForwardStepTest extends OriginEntryTestBase {

    private static org.apache.log4j.Logger LOG = 
        org.apache.log4j.Logger.getLogger(BalanceForwardStepTest.class);
    
    public BalanceForwardStepTest() {
        super();
    }

    /* (non-Javadoc)
     * @see org.kuali.module.gl.OriginEntryTestBase#setUp()
     */
    protected void setUp() throws Exception {
        
        super.setUp();
        
        // Get the test date time service so we can specify the date/time of the run
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.DAY_OF_MONTH, 5);
//        c.set(Calendar.MONTH, Calendar.MAY);
//        c.set(Calendar.YEAR, 2006);
//        date = c.getTime();
//        dateTimeService.currentDate = date;

        try {
            
            DateFormat transactionDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateTimeService.currentDate = 
                new Date(transactionDateFormat.parse(
                        kualiConfigurationService.getApplicationParameterValue(
                                "fis_gl_year_end.sh", "TRANSACTION_DT")).getTime());
            
        } catch (ApplicationParameterException e) {
            
            LOG.error(
                "Unable to get TRANSACTION_DT from kualiConfigurationService");
            throw new RuntimeException(
                "Unable to get transaction date from kualiConfigurationService", e);
            
        } catch (ParseException pe) {
            
            LOG.error(
                "Failed to parse TRANSACTION_DT from kualiConfigurationService");
            throw new RuntimeException(
                "Unable to get transaction date from kualiConfigurationService", pe);
            
        }
        
    }
    
    public void testToAvoidTestRunErrors() throws Exception {
        assertTrue(true);
    }
    
    /**
     * Test the encumbrance forwarding process in one fell swoop.
     * 
     * @throws Exception
     * 
     * ## WARNING: DO NOT run this test or rename this method.    WARNING ##
     * ## WARNING: This one  test takes just under 3 hours to run WARNING ##
     * ## WARNING: over the vpn.                                  WARNING ##
     */
    public void testAll() throws Exception {

        clearOriginEntryTables();
        BalanceTestHelper.populateBalanceTable();

        // Execute the step ...
        // BalanceForwardStep step = new BalanceForwardStep();
        BalanceForwardStep step = 
            (BalanceForwardStep) beanFactory.getBean("glBalanceForwardStep"); 
        // step.setDateTimeService(dateTimeService);
        step.performStep();
        
        // load our services.
        OriginEntryService entryService = SpringServiceLocator.getOriginEntryService();
        OriginEntryGroupService groupService = SpringServiceLocator.getOriginEntryGroupService();
        
        // and verify the output.
        List normalEntriesGeneratedByFis = 
            GeneralLedgerTestHelper.loadOutputOriginEntriesFromClasspath(
                    "org/kuali/module/gl/batch/gl_gleacbfb.data.txt", dateTimeService.currentDate);
        List closedPriorYearAccountEntriesGeneratedByFis = 
            GeneralLedgerTestHelper.loadOutputOriginEntriesFromClasspath(
                    "org/kuali/module/gl/batch/gl_acbfclos.data.txt", dateTimeService.currentDate);
        
        // load our groups.
        Map criteria = new HashMap();
        
        criteria.put("sourceCode", "YEBB");
        Collection regularGroups = groupService.getMatchingGroups(criteria);
        
        criteria.put("sourceCode", "YEBC");
        Collection closedPriorYearAccountGroups = groupService.getMatchingGroups(criteria);
        
        // compute the difference between what should be output and what was output.
        List extraRegularEntriesGenerated = new ArrayList();
        
        Iterator regularGroupsIterator = regularGroups.iterator();
        while(regularGroupsIterator.hasNext()) {
            
            OriginEntryGroup group = (OriginEntryGroup) regularGroupsIterator.next();
            
            Iterator entryIterator = entryService.getEntriesByGroup(group);
            
            while(entryIterator.hasNext()) {
                
                OriginEntry entry = (OriginEntry) entryIterator.next();
                String line = entry.getLine();
                
                try {
                    line = line.substring(0, 173);
                } catch(StringIndexOutOfBoundsException e) {
                    throw e;
                }
                
                if(!normalEntriesGeneratedByFis.remove(line)) {
                    
                    extraRegularEntriesGenerated.add(line);
                    
                }
                
            }
            
        }
        
        List closedPriorYearAccountEntriesGenerated = new ArrayList();
        
        Iterator closedPriorYearAccountGroupsIterator = closedPriorYearAccountGroups.iterator();
        while(closedPriorYearAccountGroupsIterator.hasNext()) {
            
            OriginEntryGroup group = (OriginEntryGroup) closedPriorYearAccountGroupsIterator.next();
            
            Iterator entryIterator = entryService.getEntriesByGroup(group);
            while(entryIterator.hasNext()) {
                
                OriginEntry entry = (OriginEntry) entryIterator.next();
                String line = entry.getLine().substring(0, 173);
                
                if(!closedPriorYearAccountEntriesGeneratedByFis.remove(line)) {
                    
                    closedPriorYearAccountEntriesGenerated.add(line);
                    
                }
                
            }
            
        }
        
        traceList(extraRegularEntriesGenerated, "kuali not fis");
        traceList(normalEntriesGeneratedByFis, "fis not kuali");
        
        // At this point extraEntriesGenerated and shouldBe should both be empty.
        // If they're not then something went wrong.
        assertTrue("Kuali generated entries that FIS did not generate:", extraRegularEntriesGenerated.isEmpty());
        assertTrue("FIS generated entries that Kuali did not generate:", normalEntriesGeneratedByFis.isEmpty());
        
    }

    private void traceList(List list, String name) {
        trace("StartList " + name + "( " 
                + list.size() + " elements): ", 0);
        
        for(Iterator iterator = list.iterator(); iterator.hasNext();) {
            trace(iterator.next(), 1);
        }
        
        trace("EndList " + name + ": ", 0);
        trace("", 0);
    }
    
    private void trace(Object o, int tabIndentCount) {
        PrintStream out = System.out;
        
        for(int i = 0; i < tabIndentCount; i++) {
            out.print("\t");
        }
        
        out.println(null == o ? "NULL" : o.toString());
    }
    
}
