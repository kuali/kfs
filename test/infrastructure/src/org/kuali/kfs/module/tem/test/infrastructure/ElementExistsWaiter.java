/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.test.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.util.NumberUtils;
import org.kuali.rice.test.web.HtmlUnitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.SystemClock;
import org.openqa.selenium.support.ui.Wait;

import com.google.common.base.Function;

/**
 * Implements a {@code Wait<WebDriver>} class for waiting for elements (especially Ajax elements) to appear on the page within a specified timeout.  
 * Modified from {@code WebDriverWait} in order to integrate custom JUnit4 assertion messages. 
 * 
 * @see org.openqa.selenium.support.ui.Wait
 * @see org.openqa.selenium.support.ui.WebDriverWait
 */
public class ElementExistsWaiter implements Wait<WebDriver> {
    
    private Clock clock = new SystemClock();
    private long testTimeOut = 10000;
    private long sleepTimeOut = 500;
    
    private String message;
    private WebDriver driver;
    
    public ElementExistsWaiter(final WebDriver driver, final String message) {
        this.message = message;
        this.driver = driver;
    }
    
    /**
     * {@inheritDoc}
     * @see org.openqa.selenium.support.ui.Wait#until(com.google.common.base.Function)
     */
    public <T> T until(Function<WebDriver, T> exists) {
        long end = clock.laterBy(testTimeOut);
        while (clock.isNowBefore(end)) {
            T value = exists.apply(driver);
            
            if (value != null) {
                if (Boolean.class.equals(value.getClass())) {
                    if (Boolean.TRUE.equals(value)) {
                        return value;
                    }
                } else {
                    return value;
                }
            }
            
            sleep();
        }
        
        throw new AssertionError(message);
    }
    
    private void sleep() {
        try {
            Thread.sleep(sleepTimeOut);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
