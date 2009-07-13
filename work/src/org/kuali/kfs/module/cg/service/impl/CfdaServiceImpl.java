/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cg.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.batch.CfdaBatchStep;
import org.kuali.kfs.module.cg.businessobject.CFDA;
import org.kuali.kfs.module.cg.businessobject.CfdaUpdateResults;
import org.kuali.kfs.module.cg.service.CfdaService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;

import au.com.bytecode.opencsv.CSVReader;

public class CfdaServiceImpl implements CfdaService {

    private BusinessObjectService businessObjectService;
    private static Comparator cfdaComparator;

    static {
        cfdaComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                String lhs = (String) o1;
                String rhs = (String) o2;
                return lhs.compareTo(rhs);
            }
        };
    }
//
//    /**
//     * I know this is hack-ish. Regexes are appropriate. I just couldn't get the pattern down and didn't want to waste my time
//     * figuring it out.
//     * 
//     * @param string
//     * @return
//     */
//    public String extractCfdaNumberFrom(String string) {
//        if (null == string)
//            return null;
//
//        string = string.substring(string.indexOf("<U>") + 3);
//        string = string.substring(0, string.indexOf("</U>"));
//        return string;
//    }
//
//    /**
//     * I know this is hack-ish. Regexes are appropriate. I just couldn't get the pattern down and didn't want to waste my time
//     * figuring it out.
//     * 
//     * @param string
//     * @return
//     */
//    public String extractCfdaAgencyFrom(String string) {
//        if (null == string)
//            return null;
//
//        string = string.substring(string.indexOf("<FONT") + 5);
//        string = string.substring(string.indexOf(">") + 1);
//        string = string.substring(0, string.indexOf("</FONT>"));
//        return string;
//    }
//
//    /**
//     * I know this is hack-ish. Regexes are appropriate. I just couldn't get the pattern down and didn't want to waste my time
//     * figuring it out.
//     * 
//     * @param string
//     * @return
//     */
//    public String extractCfdaTitleFrom(String string) {
//        if (null == string)
//            return null;
//
//        string = string.substring(string.indexOf("<FONT") + 5);
//        string = string.substring(string.indexOf(">") + 1);
//        string = string.substring(0, string.indexOf("</FONT>"));
//        return string;
//    }

    /**
     * @return
     * @throws IOException
     */
    public SortedMap<String, CFDA> getGovCodes() throws IOException {
        Calendar calendar = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        SortedMap<String, CFDA> govMap = new TreeMap<String, CFDA>();
        // ftp://ftp.cfda.gov/programs09187.csv
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(SpringContext.getBean(ParameterService.class).getParameterValue(CfdaBatchStep.class, CGConstants.SOURCE_URL_PARAMETER));
        // need to pull off the '20' in 2009 
        String year = ""+calendar.get(Calendar.YEAR);
        year = year.substring(2, 4);
        urlBuilder.append(year);
        //the last 3 numbers in the file name are the day of the year, but the files are from "yesterday"
        urlBuilder.append(calendar.get(Calendar.DAY_OF_YEAR)-1);
        urlBuilder.append(".csv");
        
        URL url = new URL(urlBuilder.toString());
        InputStream inputStream = url.openStream();
        InputStreamReader screenReader = new InputStreamReader(inputStream);
        BufferedReader screen = new BufferedReader(screenReader);
        
        CSVReader csvReader = new CSVReader(screenReader, ',', '"', 1);
        
        List<String[]> lines = csvReader.readAll();
        
        for (String[] line : lines) {
            String title = line[0];
            String number = line[1];
            
            CFDA cfda = new CFDA();
            cfda.setCfdaNumber(number);
            cfda.setCfdaProgramTitleName(title);

            govMap.put(number, cfda);
            
        }
        
//        String line = screen.readLine();
//
//        //boolean headerFound = false;
//        while (null != line) {
//            if (line.trim().equals("<TR>")) {
//
//                String number = extractCfdaNumberFrom(screen.readLine());
//                /* String agency = extractCfdaAgencyFrom( */screen.readLine()/* ) */; // not used, but we still need to read the line
//                // to move past it.
//                String title = extractCfdaTitleFrom(screen.readLine());
//
//                CFDA cfda = new CFDA();
//                cfda.setCfdaNumber(number);
//                cfda.setCfdaProgramTitleName(title);
//
//                govMap.put(number, cfda);
//            }
//
//            line = screen.readLine();
//        }
        return govMap;
    }

    /**
     * @return
     * @throws IOException
     */
    public SortedMap<String, CFDA> getKfsCodes() throws IOException {
        Collection allCodes = businessObjectService.findAll(CFDA.class);

        SortedMap<String, CFDA> kfsMapAll = new TreeMap<String, CFDA>(cfdaComparator);
        for (Object o : allCodes) {
            CFDA c = (CFDA) o;
            kfsMapAll.put(c.getCfdaNumber(), c);
        }
        return kfsMapAll;
    }

    /**
     *
     */
    public CfdaUpdateResults update() throws IOException {

        CfdaUpdateResults results = new CfdaUpdateResults();
        Map<String, CFDA> govMap = null;

        try {
            govMap = getGovCodes();
        }
        catch (IOException ioe) {
            StringBuilder builder = new StringBuilder();
            builder.append("No updates took place.\n");
            builder.append(ioe.getMessage());
            results.setMessage(builder.toString());
            return results;
        }
        Map<String, CFDA> kfsMap = getKfsCodes();

        results.setNumberOfRecordsInKfsDatabase(kfsMap.keySet().size());
        results.setNumberOfRecordsRetrievedFromWebSite(govMap.keySet().size());

        for (Object key : kfsMap.keySet()) {

            CFDA cfdaKfs = kfsMap.get(key);
            CFDA cfdaGov = govMap.get(key);

            if (cfdaKfs.getCfdaMaintenanceTypeId().startsWith("M")) {
                // Leave it alone. It's maintained manually.
                results.setNumberOfRecordsNotUpdatedBecauseManual(1 + results.getNumberOfRecordsNotUpdatedBecauseManual());
            }
            else if (cfdaKfs.getCfdaMaintenanceTypeId().startsWith("A")) {

                if (cfdaGov == null) {
                    if (cfdaKfs.isActive()) {
                        cfdaKfs.setActive(false);
                        businessObjectService.save(cfdaKfs);
                        results.setNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite(results.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite() + 1);
                    }
                    else {
                        // Leave it alone for historical purposes
                        results.setNumberOfRecrodsNotUpdatedForHistoricalPurposes(results.getNumberOfRecrodsNotUpdatedForHistoricalPurposes() + 1);
                    }
                }
                else {
                    if (cfdaKfs.isActive()) {
                        results.setNumberOfRecordsUpdatedBecauseAutomatic(results.getNumberOfRecordsUpdatedBecauseAutomatic() + 1);
                    }
                    else {
                        cfdaKfs.setActive(true);
                        results.setNumberOfRecordsReActivated(results.getNumberOfRecordsReActivated() + 1);
                    }
                    
                    cfdaKfs.setCfdaProgramTitleName(cfdaGov.getCfdaProgramTitleName());
                    businessObjectService.save(cfdaKfs);
                }
            }

            // Remove it from the govMap so we know what codes from the govMap don't already exist in KFS.
            govMap.remove(key);
        }

        // What's left in govMap now is just the codes that don't exist in KFS
        for (String key : govMap.keySet()) {
            CFDA cfdaGov = govMap.get(key);
            cfdaGov.setCfdaMaintenanceTypeId("Automatic");
            cfdaGov.setActive(true);
            businessObjectService.save(cfdaGov);
            results.setNumberOfRecordsNewlyAddedFromWebSite(results.getNumberOfRecordsNewlyAddedFromWebSite() + 1);
        }

        return results;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public CFDA getByPrimaryId(String cfdaNumber) {
        if ( StringUtils.isBlank(cfdaNumber) ) {
            return null;
        }
        return (CFDA) businessObjectService.findByPrimaryKey(CFDA.class, mapPrimaryKeys(cfdaNumber));
    }

    private Map<String, Object> mapPrimaryKeys(String cfdaNumber) {
        Map<String, Object> primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.CFDA_NUMBER, cfdaNumber.trim());
        return primaryKeys;
    }

}
