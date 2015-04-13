package org.kuali.kfs.sys.service.impl;

import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;

/**
 * Overriden so that addDataDictionaryLocation passes the application context along....
 */
public class DataDictionaryServiceImpl extends org.kuali.rice.kns.service.impl.DataDictionaryServiceImpl implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addDataDictionaryLocation(String location) throws IOException {
        if (!ObjectUtils.isNull(applicationContext)) {
            getDataDictionary().addConfigFileLocation(location, applicationContext);
        } else {
            getDataDictionary().addConfigFileLocation(location);
        }
    }
}
