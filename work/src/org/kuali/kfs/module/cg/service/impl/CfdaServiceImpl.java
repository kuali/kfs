package org.kuali.module.cg.service.impl;

import org.kuali.module.cg.service.CfdaService;
import org.kuali.module.cg.service.CfdaUpdateResults;
import org.kuali.module.cg.bo.CatalogOfFederalDomesticAssistanceReference;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.service.impl.HomeOriginationServiceImpl;
import org.kuali.core.service.BusinessObjectService;
import org.apache.log4j.Logger;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import java.util.regex.Pattern;
import java.util.*;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: May 8, 2007
 * Time: 12:55:43 PM
 */
public class CfdaServiceImpl implements CfdaService {

    private BusinessObjectService businessObjectService;
    private static String SOURCE_URL = "http://12.46.245.173/pls/portal30/CATALOG.AGY_PROGRAM_LIST_RPT.show";
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
    
    /**
     * I know this is hack-ish. Regexes are appropriate. I just couldn't get the pattern down and didn't want to waste
     * my time figuring it out.
     *
     * @param string
     * @return
     */
    public static String extractCfdaNumberFrom(String string) {
        if(null == string) return null;
        
        string = string.substring(string.indexOf("<U>") + 3);
        string = string.substring(0, string.indexOf("</U>"));
        return string;
    }

    /**
     * I know this is hack-ish. Regexes are appropriate. I just couldn't get the pattern down and didn't want to waste
     * my time figuring it out.
     *
     * @param string
     * @return
     */
    public static String extractCfdaAgencyFrom(String string) {
        if(null == string) return null;

        string = string.substring(string.indexOf("<FONT") + 5);
        string = string.substring(string.indexOf(">") + 1);
        string = string.substring(0, string.indexOf("</FONT>"));
        return string;
    }

    /**
     * I know this is hack-ish. Regexes are appropriate. I just couldn't get the pattern down and didn't want to waste
     * my time figuring it out.
     *
     * @param string
     * @return
     */
    public static String extractCfdaTitleFrom(String string) {
        if(null == string) return null;

        string = string.substring(string.indexOf("<FONT") + 5);
        string = string.substring(string.indexOf(">") + 1);
        string = string.substring(0, string.indexOf("</FONT>"));
        return string;
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    public static SortedMap<String, CatalogOfFederalDomesticAssistanceReference> getGovCodes() throws IOException {
        SortedMap<String, CatalogOfFederalDomesticAssistanceReference> govMap =
                new TreeMap<String, CatalogOfFederalDomesticAssistanceReference>();

        URL url = new URL(SOURCE_URL);
        InputStream inputStream = url.openStream();
        InputStreamReader screenReader = new InputStreamReader(inputStream);
        BufferedReader screen = new BufferedReader(screenReader);
        String line = screen.readLine();

        boolean headerFound = false;
        while(null != line) {
            if(line.trim().equals("<TR>")) {

                // There's one match that will happen for the table header row. Skip past it.
                if(!headerFound) {
                    headerFound = true;
                    line = screen.readLine();
                    continue;
                }

                String number = extractCfdaNumberFrom(screen.readLine());
                /*String agency = extractCfdaAgencyFrom(*/screen.readLine()/*)*/; // not used, but we still need to read the line
                                                                                  // to move past it.
                String title  = extractCfdaTitleFrom(screen.readLine());

                CatalogOfFederalDomesticAssistanceReference cfda = new CatalogOfFederalDomesticAssistanceReference();
                cfda.setCfdaNumber(number);
                cfda.setCfdaProgramTitleName(title);

                govMap.put(number, cfda);
            }

            line = screen.readLine();
        }
        return govMap;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public static SortedMap<String, CatalogOfFederalDomesticAssistanceReference> getKfsCodes() throws IOException {
        Collection allCodes = SpringServiceLocator.getLookupService().findCollectionBySearch(CatalogOfFederalDomesticAssistanceReference.class, new HashMap());

        SortedMap<String, CatalogOfFederalDomesticAssistanceReference> kfsMapAll = new TreeMap<String, CatalogOfFederalDomesticAssistanceReference>(cfdaComparator);
        for(Object o : allCodes) {
            CatalogOfFederalDomesticAssistanceReference c = (CatalogOfFederalDomesticAssistanceReference) o;
            kfsMapAll.put(c.getCfdaNumber(), c);
        }
        return kfsMapAll;
    }

    /**
     *
     */
    public CfdaUpdateResults update() throws IOException {

        Map<String, CatalogOfFederalDomesticAssistanceReference> govMap = getGovCodes();
        Map<String, CatalogOfFederalDomesticAssistanceReference> kfsMap = getKfsCodes();

        CfdaUpdateResults results = new CfdaUpdateResults();
        results.setNumberOfRecordsInKfsDatabase(kfsMap.keySet().size());
        results.setNumberOfRecordsRetrievedFromWebSite(govMap.keySet().size());

        for(Object key : kfsMap.keySet()) {

            CatalogOfFederalDomesticAssistanceReference cfdaKfs = kfsMap.get(key);
            CatalogOfFederalDomesticAssistanceReference cfdaGov = govMap.get(key);

            if(cfdaKfs.getCfdaMaintenanceTypeId().startsWith("M")) {
                // Leave it alone. It's maintained manually.
                results.setNumberOfRecordsNotUpdatedBecauseManual(1 + results.getNumberOfRecordsNotUpdatedBecauseManual());
            } else if(cfdaKfs.getCfdaMaintenanceTypeId().startsWith("A")) {

                if(null == cfdaGov) {
                    if("A".equals(cfdaKfs.getCfdaStatusCode())) {
                        cfdaKfs.setCfdaStatusCode(false);
                        businessObjectService.save(cfdaKfs);
                        results.setNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite(results.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite() + 1);
                    } else {
                        // Leave it alone for historical purposes
                        results.setNumberOfRecrodsNotUpdatedForHistoricalPurposes(results.getNumberOfRecrodsNotUpdatedForHistoricalPurposes() + 1);
                    }
                } else {
                    if("A".equals(cfdaKfs.getCfdaStatusCode())) {
                        cfdaKfs.setCfdaProgramTitleName(cfdaGov.getCfdaProgramTitleName());
                        businessObjectService.save(cfdaKfs);
                        results.setNumberOfRecordsUpdatedBecauseAutomatic(results.getNumberOfRecordsUpdatedBecauseAutomatic() + 1);
                    } else if("I".equals(cfdaKfs.getCfdaStatusCode())) {
                        cfdaKfs.setCfdaStatusCode(true);
                        cfdaKfs.setCfdaProgramTitleName(cfdaGov.getCfdaProgramTitleName());
                        businessObjectService.save(cfdaKfs);
                        results.setNumberOfRecordsReActivated(results.getNumberOfRecordsReActivated() + 1);
                    }
                }
            }

            // Remove it from the govMap so we know what codes from the govMap don't already exist in KFS.
            govMap.remove(key);
        }

        // What's left in govMap now is just the codes that don't exist in
        for(String key : govMap.keySet()) {
            CatalogOfFederalDomesticAssistanceReference cfdaGov = govMap.get(key);
            cfdaGov.setCfdaMaintenanceTypeId("Automatic");
            cfdaGov.setCfdaStatusCode(true);
            businessObjectService.save(cfdaGov);
            results.setNumberOfRecordsNewlyAddedFromWebSite(results.getNumberOfRecordsNewlyAddedFromWebSite() + 1);
        }

        return results;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
