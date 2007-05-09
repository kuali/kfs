package org.kuali.module.cg.service.impl;

import org.kuali.module.cg.service.CfdaService;
import org.kuali.module.cg.bo.CatalogOfFederalDomesticAssistanceReference;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.core.service.BusinessObjectService;

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
    private String SOURCE_URL = "http://12.46.245.173/pls/portal30/CATALOG.AGY_PROGRAM_LIST_RPT.show";

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

    public void update() {

        Collection allCodes = SpringServiceLocator.getLookupService().findCollectionBySearch(CatalogOfFederalDomesticAssistanceReference.class, new HashMap());
        Comparator cfdaComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                String lhs = (String) o1;
                String rhs = (String) o2;
                return lhs.compareTo(rhs);
            }
        };

        Map<String, CatalogOfFederalDomesticAssistanceReference> govMap = new TreeMap<String, CatalogOfFederalDomesticAssistanceReference>(cfdaComparator);

        try {
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
                    String agency = extractCfdaAgencyFrom(screen.readLine());
                    String title  = extractCfdaTitleFrom(screen.readLine());

                    CatalogOfFederalDomesticAssistanceReference cfda = new CatalogOfFederalDomesticAssistanceReference();
                    cfda.setCfdaNumber(number);
                    cfda.setCfdaProgramTitleName(title);
                    
                    govMap.put(number, cfda);
                }

                line = screen.readLine();
            }

            Map<String, CatalogOfFederalDomesticAssistanceReference> kfsMapAll = new TreeMap<String, CatalogOfFederalDomesticAssistanceReference>(cfdaComparator);
            Map<String, CatalogOfFederalDomesticAssistanceReference> kfsMapA = new TreeMap<String, CatalogOfFederalDomesticAssistanceReference>(cfdaComparator);
            Map<String, CatalogOfFederalDomesticAssistanceReference> kfsMapI = new TreeMap<String, CatalogOfFederalDomesticAssistanceReference>(cfdaComparator);
            // Index the codes from the dbase
            for(Object o : allCodes) {
                CatalogOfFederalDomesticAssistanceReference c = (CatalogOfFederalDomesticAssistanceReference) o;
                if("AUTOMATIC".equals(c.getCfdaMaintenanceTypeId())) {
                    if("A".equals(c.getCfdaStatusCode())) {
                        kfsMapA.put(c.getCfdaNumber(), c);
                    } else {
                        kfsMapI.put(c.getCfdaNumber(), c);
                    }
                    // This map makes it easier to find the CFDA later
                    kfsMapAll.put(c.getCfdaNumber(), c);
                } else {
                    govMap.remove(c.getCfdaNumber());
                }
            }
            
            // govMap and kfsMap are now full
            for(String key : govMap.keySet()) {
                if(!kfsMapA.containsKey(key) && !kfsMapI.containsKey(key)) {
                    CatalogOfFederalDomesticAssistanceReference c = govMap.get(key);
                    c.setCfdaStatusCode(true);
                    c.setCfdaMaintenanceTypeId("AUTOMATIC");
                    businessObjectService.save(c);
                } else if(!kfsMapA.containsKey(key)) {
                    CatalogOfFederalDomesticAssistanceReference c = kfsMapAll.get(key);
                    c.setCfdaStatusCode(true);
                    businessObjectService.save(c);
                }
            }

            // check kfs active numbers for non existence in gov table
            for(String key : kfsMapA.keySet()) {
                if(!govMap.containsKey(key)) {
                    CatalogOfFederalDomesticAssistanceReference c = kfsMapA.get(key);
                    c.setCfdaStatusCode(false);
                    businessObjectService.save(c);
                }
            }

        } catch(MalformedURLException murle) {
            murle.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
