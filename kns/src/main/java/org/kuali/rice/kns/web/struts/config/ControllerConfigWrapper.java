/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.web.struts.config;

import org.apache.struts.config.BaseConfig;
import org.apache.struts.config.ControllerConfig;

import java.util.Properties;

/**
 * Wrapper which aids specializing Struts ControllerConfig
 * Delegates all public methods to wrapped ControllerConfig
 */
public class ControllerConfigWrapper extends ControllerConfig {

    /**
     * The wrapped config
     */
    protected ControllerConfig config;

    public ControllerConfigWrapper(ControllerConfig config) {
        this.config = config;
    }

    @Override
    public int getBufferSize() {
        return config.getBufferSize();
    }

    @Override
    public void setBufferSize(int bufferSize) {
        config.setBufferSize(bufferSize);
    }

    @Override
    public String getContentType() {
        return config.getContentType();
    }

    @Override
    public void setContentType(String contentType) {
        config.setContentType(contentType);
    }

    @Override
    public String getCatalog() {
        return config.getCatalog();
    }

    @Override
    public void setCatalog(String catalog) {
        config.setCatalog(catalog);
    }

    @Override
    public String getCommand() {
        return config.getCommand();
    }

    @Override
    public void setCommand(String command) {
        config.setCommand(command);
    }

    @Override
    public String getForwardPattern() {
        return config.getForwardPattern();
    }

    @Override
    public void setForwardPattern(String forwardPattern) {
        config.setForwardPattern(forwardPattern);
    }

    @Override
    public boolean getInputForward() {
        return config.getInputForward();
    }

    @Override
    public void setInputForward(boolean inputForward) {
        config.setInputForward(inputForward);
    }

    @Override
    public boolean getLocale() {
        return config.getLocale();
    }

    @Override
    public void setLocale(boolean locale) {
        config.setLocale(locale);
    }

    @Override
    public String getMaxFileSize() {
        return config.getMaxFileSize();
    }

    @Override
    public void setMaxFileSize(String maxFileSize) {
        config.setMaxFileSize(maxFileSize);
    }

    @Override
    public String getMemFileSize() {
        return config.getMemFileSize();
    }

    @Override
    public void setMemFileSize(String memFileSize) {
        config.setMemFileSize(memFileSize);
    }

    @Override
    public String getMultipartClass() {
        return config.getMultipartClass();
    }

    @Override
    public void setMultipartClass(String multipartClass) {
        config.setMultipartClass(multipartClass);
    }

    @Override
    public boolean getNocache() {
        return config.getNocache();
    }

    @Override
    public void setNocache(boolean nocache) {
        config.setNocache(nocache);
    }

    @Override
    public String getPagePattern() {
        return config.getPagePattern();
    }

    @Override
    public void setPagePattern(String pagePattern) {
        config.setPagePattern(pagePattern);
    }

    @Override
    public String getProcessorClass() {
        return config.getProcessorClass();
    }

    @Override
    public void setProcessorClass(String processorClass) {
        config.setProcessorClass(processorClass);
    }

    @Override
    public String getTempDir() {
        return config.getTempDir();
    }

    @Override
    public void setTempDir(String tempDir) {
        config.setTempDir(tempDir);
    }

    @Override
    public String toString() {
        return config.toString();
    }

    @Override
    public void freeze() {
        config.freeze();
    }

    @Override
    public void throwIfConfigured() {
        config.throwIfConfigured();
    }

    @Override
    public void setProperty(String key, String value) {
        config.setProperty(key, value);
    }

    @Override
    public String getProperty(String key) {
        return config.getProperty(key);
    }
}
