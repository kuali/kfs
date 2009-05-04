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
package org.kuali.kfs.module.purap.dataaccess.impl;

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
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.dataaccess.ImageDao;
import org.kuali.kfs.module.purap.document.ContractManagerAssignmentDocument;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.exception.PurapConfigurationException;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;

/**
 * OJB Implementation of ImageDao.
 */
public class ImageDaoNet extends PlatformAwareDaoBaseOjb implements ImageDao {
    private static Log LOG = LogFactory.getLog(ImageDaoNet.class);

    private KualiConfigurationService configurationService;
    private ParameterService parameterService;

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * @see org.kuali.kfs.module.purap.dataaccess.ImageDao#getPurchasingDirectorImage(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getPurchasingDirectorImage(String key, String campusCode, String location) {
        LOG.debug("getPurchasingDirectorImage() started");

        String prefix = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PURCHASING_DIRECTOR_IMAGE_PREFIX);
        String extension = "." + parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PURCHASING_DIRECTOR_IMAGE_EXTENSION);
        return getFile(prefix, campusCode, key, extension, location);
    }

    /**
     * @see org.kuali.kfs.module.purap.dataaccess.ImageDao#getContractManagerImage(java.lang.String, java.lang.Integer, java.lang.String)
     */
    public String getContractManagerImage(String key, Integer contractManagerId, String location) {
        LOG.debug("getContractManagerImage() started");

        NumberFormat formatter = new DecimalFormat("00");
        String cm = formatter.format(contractManagerId);

        String prefix = parameterService.getParameterValue(ContractManagerAssignmentDocument.class, PurapConstants.CONTRACT_MANAGER_IMAGE_PREFIX);
        String extension = "." + parameterService.getParameterValue(ContractManagerAssignmentDocument.class, PurapConstants.CONTRACT_MANAGER_IMAGE_EXTENSION);
        return getFile(prefix, cm, key, extension, location);
    }

    /**
     * @see org.kuali.kfs.module.purap.dataaccess.ImageDao#getLogo(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getLogo(String key, String campusCode, String location) {
        LOG.debug("getLogo() started. key is " + key + ". campusCode is " + campusCode);

        String prefix = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.LOGO_IMAGE_PREFIX);
        String extension = "." + parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.LOGO_IMAGE_EXTENSION);
        return getFile(prefix, campusCode, key, extension, location);
    }

    /**
     * Copy a file from a web location to the local system.
     * 
     * @param prefix - Prefix for the file name
     * @param fileKey - File key for file
     * @param key - Unique key for the file
     * @param location - location of file
     * @return - location to copied file
     */
    private String getFile(String prefix, String fileKey, String key, String extension, String location) {
        LOG.debug("getFile() started");

        String externalizableUrlSettingName = "externalizable.images.url";
        String externalizableUrl = configurationService.getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        if (externalizableUrl == null) {
            throw new PurapConfigurationException("Application Setting " + externalizableUrlSettingName + " is missing");
        }
        if (location == null) {
            throw new PurapConfigurationException("Valid location to store temp image files was null");
        }

        String completeUrl = externalizableUrl + prefix + "_" + fileKey.toLowerCase() + extension;

        LOG.debug("getFile() URL = " + completeUrl);

        return completeUrl;
    }

}
