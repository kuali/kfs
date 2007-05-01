
package org.kuali.module.purap.dao.ojb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.dao.ImageDao;
import org.kuali.module.purap.exceptions.PurError;
import org.kuali.module.purap.exceptions.PurapConfigurationException;

public class ImageDaoNet extends PlatformAwareDaoBaseOjb implements ImageDao {
    private static Log LOG = LogFactory.getLog(ImageDaoNet.class);
    private KualiConfigurationService kualiConfigurationService;

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.pur.po.dao.ImageDao#getPurchasingDirectorImage(java.lang.String,java.lang.String)
     */
    public String getPurchasingDirectorImage(String key,String campusCode,String location) {
        LOG.debug("getPurchasingDirectorImage() started");

        String prefix = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.PURCHASING_DIRECTOR_IMAGE_PREFIX);
        String extension = "." + kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.PURCHASING_DIRECTOR_IMAGE_EXTENSION);
        return getFile (prefix, campusCode, key, extension, location );
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.pur.po.dao.ImageDao#getContractManagerImage(java.lang.String,java.lang.Integer)
     */
    public String getContractManagerImage(String key,Integer contractManagerId,String location) {
        LOG.debug("getContractManagerImage() started");

        NumberFormat formatter = new DecimalFormat("00");
        String cm = formatter.format(contractManagerId);

        String prefix = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.CONTRACT_MANAGER_IMAGE_PREFIX);
        String extension = "." + kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.CONTRACT_MANAGER_IMAGE_EXTENSION);
        return getFile (prefix, cm, key, extension, location );
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.pur.po.dao.ImageDao#getLogo(java.lang.String,java.lang.String)
     */
    public String getLogo(String key,String campusCode,String location) {
        LOG.debug("getLogo() started. key is " + key + ". campusCode is " + campusCode);

        String prefix = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.LOGO_IMAGE_PREFIX);
        String extension = "." + kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.LOGO_IMAGE_EXTENSION);
        return getFile (prefix, campusCode, key, extension, location );
    }

    /**
     * Copy a file from a web location to the local system
     * 
     * @param prefix Prefix for the file name
     * @param fileKey File key for file
     * @param key Unique key for the file
     * @return
     */
    private String getFile(String prefix,String fileKey,String key,String extension,String location) {
        LOG.debug("getFile() started");

        String externalizableUrlSettingName = "externalizable.images.url";
        String externalizableUrl = kualiConfigurationService.getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        if ( externalizableUrl == null ) {
            throw new PurapConfigurationException("Application Setting " + externalizableUrlSettingName + " is missing");
        }
        if ( location == null ) {
            throw new PurapConfigurationException("Valid location to store temp image files was null");      
        }

        String completeUrl = externalizableUrl + prefix + "_" + fileKey.toLowerCase() + extension;
        String completeFile = location + key.toLowerCase() + prefix + extension;

        LOG.debug("getFile() URL = " + completeUrl);
        LOG.debug("getFile() File = " + completeFile);

        OutputStream out = null;
        BufferedOutputStream bufOut = null;
        InputStream in = null;
        BufferedInputStream bufIn = null;

        try {
            out = new FileOutputStream(completeFile);
            bufOut = new BufferedOutputStream(out);

            URL url = new URL(completeUrl);
            in = url.openStream();
            bufIn = new BufferedInputStream(in);

            // Repeat until end of file
            while(true) {
                int data = bufIn.read();

                // Check for EOF
                if (data == -1) {
                    break;
                } else {
                    bufOut.write(data);
                }
            }
        } catch (FileNotFoundException fnfe) {
            LOG.error("getFile() File not found: " + completeUrl, fnfe);
            throw new PurapConfigurationException("getFile() File not found: " + completeUrl);
        } catch (MalformedURLException mue) {
            LOG.error("getFile() Unable to get URL: " + completeUrl, mue);
            throw new IllegalAccessError(mue.getMessage());
        } catch (IOException ioe) {
            LOG.error("getFile() Unable to write file: " + completeFile, ioe);
            throw new IllegalAccessError(ioe.getMessage());
        } finally {
            if ( bufIn != null ) {
                try {
                    bufIn.close();
                } catch (IOException e) {}
            }
            if ( in != null ) {
                try {
                    in.close();
                } catch (IOException e) {}
            }
            if ( bufOut != null ) {
                try {
                    bufOut.close();
                } catch (IOException e) {}
            }
            if ( out != null ) {
                try {
                    out.close();
                } catch (IOException e) {}
            }
        }
        return completeFile;
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.pur.po.dao.ImageDao#removeImages(java.lang.String)
     */
    public void removeImages(String key,String location) {
        LOG.debug("removeImages() started");
        try {
            File dir = new File(location);
            String[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                String filename = files[i];
                if ( filename.startsWith(key.toLowerCase()) ) {
                    LOG.debug("removeImages() removing " + filename);
                
                    File f = new File(location + filename);
                    f.delete();
                }
            }
        } catch (Exception e) {
            throw new PurError ("Caught exception while trying to remove images at " + location, e);
        }
    }
}
