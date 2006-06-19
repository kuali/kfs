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
package org.kuali.module.gl.service.impl;

import java.io.*;

import org.apache.log4j.*;
import org.kuali.core.service.*;
import org.kuali.module.gl.collector.xml.*;
import org.kuali.module.gl.collector.xml.impl.*;
import org.kuali.module.gl.service.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.*;

public class CollectorServiceImpl implements CollectorService, BeanFactoryAware {
    private static Logger LOG = Logger.getLogger(CollectorServiceImpl.class);
    
    private InterDepartmentalBillingService interDepartmentalBillingService;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private KualiConfigurationService kualiConfigurationService;
    private DateTimeService dateTimeService;
    private BeanFactory beanFactory;

    public void loadCollectorFile(String fileName) {
        CollectorFileParser collectorFileParser = (CollectorFileParser)beanFactory.getBean("glCollectorFileParser");
        doHardEditParse(collectorFileParser, fileName);
        doCollectorFileParse(collectorFileParser, fileName);
     }
    private void doHardEditParse(CollectorFileParser collectorFileParser, String fileName) {
        HardEditHandler hardEditHandler = new HardEditHandler();
        hardEditHandler.clear();
        collectorFileParser.setFileHandler(hardEditHandler);
        try {
            InputStream inputStream1 = new FileInputStream(fileName);
            collectorFileParser.parse(inputStream1);
        }catch(FileReadException fre) {
            //Do something here.
        }catch(FileNotFoundException fnfe) {
            //Do something here.
        }
    }
    private void doCollectorFileParse(CollectorFileParser collectorFileParser, String fileName) {
        CollectorFileHandlerImpl collectorFileHandler = new CollectorFileHandlerImpl
                (originEntryService, interDepartmentalBillingService, originEntryGroupService, dateTimeService);
        collectorFileParser.setFileHandler(collectorFileHandler);
        try {
            InputStream inputStream2 = new FileInputStream(fileName);
            collectorFileParser.parse(inputStream2);
        }catch(FileReadException fre) {
            //Do something here.
        }catch(FileNotFoundException fnfe) {
            //Do something here.
        }
    }
    public void setInterDepartmentalBillingService(InterDepartmentalBillingService interDepartmentalBillingService) {
        this.interDepartmentalBillingService = interDepartmentalBillingService;
    }
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
    public String getStagingDirectory() {
        return kualiConfigurationService.getPropertyString("collector.staging.directory");
    }
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    public static void setLOG(Logger log) {
        LOG = log;
    }
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
