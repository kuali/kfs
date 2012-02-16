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

public class GenericSeleniumDelegate {
        
    private WebDriver driver;
    
    public GenericSeleniumDelegate(final WebDriver driver) {
        this.driver = driver;
    }
    
    /**
     * Gets the value of a control field.
     *
     * @param locator the id, partial name, partial title, or partial link name of the element to click on
     * @return the value of the element
     */
    public String get(final String locator) {
        return get(locator, false);
    }
    
    /**
     * Gets the value of a control field.
     *
     * @param locator the id, name, title, or link name of the element to click on depending on the value of {@code exact}
     * @param exact whether the locator should match exactly
     * @return the value of the element
     */
    public String get(final String locator, final boolean exact) {
        return getElement(locator, exact).getValue();
    }
    
    /**
     * Sets the value of a control field.
     *
     * @param locator the id, partial name, partial title, or partial link name of the element to set
     * @param value the new value of the element
     */
    public void set(final String locator, final String value) {
        set(locator, false, value);
    }
    
    /**
     * Sets the value of a control field.
     *
     * @param locator the id, name, title, or link name of the element to set depending on the value of {@code exact}
     * @param exact whether the locator should match exactly
     * @param value the new value of the element
     */
    public void set(final String locator, final boolean exact, final String value) {
        WebElement element = getElement(locator, exact);
        String tagName = element.getTagName();
        String elementType = element.getAttribute("type");
        
        if (StringUtils.equals(tagName, "input") && StringUtils.equals(elementType, "checkbox")) {
            element.click();
        } else if (StringUtils.equals(tagName, "input") && StringUtils.equals(elementType, "radio")) {
            setRadio(locator, exact, value);
        } else if (StringUtils.equals(tagName, "select")) {
            setSelect(element, value);
        } else {
            element.clear();
            element.sendKeys(value);
        }
        
    }
    
    /**
     * Sets the value of a radio button.
     * 
     * @param locator the id, name, title, or link name of the element to set depending on the value of {@code exact}
     * @param exact whether the locator should match exactly
     * @param value the new value of the element
     */
    public void setRadio(final String locator, final boolean exact, final String value) {
        WebElement radio = new ElementExistsWaiter(driver, locator + " with value " + value + " not found").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    WebElement inputElement = null;
                    
                    for (WebElement radio : getElementsByName(locator, exact)) {
                        String radioValue = radio.getValue();
                        if (StringUtils.equals(radioValue, value)) {
                            inputElement = radio;
                            break;
                        }
                    }
                    
                    return inputElement;
                }
            }
        );
        radio.click();
    }
    
    /**
     * Sets the value of a select.
     * 
     * @param element the located parent element
     * @param value the new value of the element
     */
    public void setSelect(final WebElement element, final String value) {
        WebElement option = new ElementExistsWaiter(driver, "The option with value " + value + " not found").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    WebElement optionElement = null;
                    
                    for (WebElement option : element.findElements(By.tagName("option"))) {
                        String optionText = option.getText();
                        if (StringUtils.contains(optionText, value)) {
                            optionElement = option;
                            break;
                        }
                    }
                    
                    return optionElement;
                }
            }
        );
        option.setSelected();
    }

    /**
     * Clicks on an element in the web page.
     * <p>
     * Using any of the {@code click()} methods is the preferred way to click on an element due to the login process.  If the login web page is 
     * encountered, the user will be automatically logged in and the given button will be clicked.
     *
     * @param locator the id, partial name, partial title, or partial link name of the element to click on
     */
    public void click(final String locator) {
        click(locator, false, null);
    }
    
    /**
     * Clicks on an element in the web page.
     * <p>
     * Using any of the {@code click()} methods is the preferred way to click on an element due to the login process.  If the login web page is 
     * encountered, the user will be automatically logged in and the given button will be clicked.
     *
     * @param locator the id, name, title, or link name of the element to click on depending on the value of {@code exact}
     * @param exact whether the locator should match exactly
     */
    public void click(final String locator, final boolean exact) {
        click(locator, exact, null);
    }
    
    /**
     * Clicks on an element in the web page, asserting that the next page contains {@code nextPageTitle}.
     * <p>
     * Using any of the {@code click()} methods is the preferred way to click on an HTML element due to the login process.  If the login web page is 
     * encountered, the user will be automatically logged in and the given button will be clicked.
     *
     * @param locator the id, name, title, or link name of the element to click on depending on the value of {@code exact}
     * @param exact whether the locator should match exactly
     * @param nextPageTitle the expected title of the next web page (may be null)
     */
    public void click(final String locator, final boolean exact, final String nextPageTitle) {
        getElement(locator, exact).click();
        
        // login();
        
        if (nextPageTitle != null) {
            assertPageContains(nextPageTitle);
        }
    }
    
    /**
     * Asserts that the web page contains {@code text}.
     * 
     * @param text the string to look for in the web page.
     */
    public void assertPageContains(final String text) {        
        new ElementExistsWaiter(driver, "Page does not contain " + text).until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    return getElementByText(text);
                }
            }
        ); 
    }

    /**
     * Asserts that the web page does <b>not</b> contain {@code text}.
     * 
     * @param text the string to look for in the web page.
     */
    public void assertPageDoesNotContain(final String text) {
        
        new ElementDoesNotExistWaiter(driver, "Page contains " + text).until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    return getElementByText(text);
                }
            }
         );
    }

    /**
     * Asserts that the value of the element identified by {@code locator} contains {@code value}.
     * 
     * @param locator the id, partial name, partial title, or partial link name of the element to search for
     * @param value the value to look for in the element
     */
    public void assertElementContains(final String locator, final String value) {
        assertElementContains(locator, false, value);
    }
    
    /**
     * Asserts that the value of the element identified by {@code locator} matches {@code value} depending on the value of {@code exact}.
     * 
     * @param locator the id, name, title, or link name of the element to search for, exactness depending on the value of {@code exact}
     * @param exact whether the locator should match exactly
     * @param value the value to look for in the element
     */
    public void assertElementContains(final String locator, final boolean exact, final String value) {
        WebElement element = getElement(locator, exact);
        assertTrue("Element " + locator + " does not contain " + value, StringUtils.contains(element.getValue(), value)); 
    }
    
    /**
     * Asserts that the value of the element identified by {@code locator} does <b>not</b> contain {@code value}.
     * 
     * @param locator the id, partial name, partial title, or partial link name of the element to search for
     * @param value the value to look for in the element
     */
    public void assertElementDoesNotContain(final String locator, final String value) {
        assertElementDoesNotContain(locator, false, value);
    }
    
    /**
     * Asserts that the value of the element identified by {@code locator} does <b>not</b> match {@code value} depending on the value of {@code exact}.
     * 
     * @param locator the id, name, title, or link name of the element to search for, exactness depending on the value of {@code exact}
     * @param exact whether the locator should match exactly
     * @param value the value to look for in the element
     */
    public void assertElementDoesNotContain(final String locator, final boolean exact, final String value) {        
        WebElement element = getElement(locator, exact);
        assertFalse("Element " + locator + " contains " + value, StringUtils.contains(element.getValue(), value)); 
    }
    
    /**
     * Asserts that the CSS selector identified by {@code cssSelector} contains {@code value}.
     * 
     * @param cssSelector the CSS selector of element to search for
     * @param value the value to look for in the element
     */
    public void assertSelectorContains(final String cssSelector, final String value) {
        new ElementExistsWaiter(driver, "CSS selector " + cssSelector + " does not contain " + value).until(
            new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    boolean selectorContains = false;
                    
                    for (WebElement element : getElementsByCssSelector(cssSelector)) {
                        if (element.getText() != null && element.getText().matches(value)) {
                            selectorContains = true;
                            break;
                        }
                    }

                    return selectorContains;
                }
            }
        );
    }
    
    /**
     * Asserts that the CSS selector identified by {@code cssSelector} does not contain {@code value}.
     * 
     * @param cssSelector the CSS selector of element to search for
     * @param value the value to look for in the element
     */
    public void assertSelectorDoesNotContain(final String cssSelector, final String value) {
        new ElementDoesNotExistWaiter(driver, "CSS selector " + cssSelector + " contains " + value).until(
            new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    boolean selectorDoesNotContain = true;
                    
                    for (WebElement element : getElementsByCssSelector(cssSelector)) {
                        if (element.getText() != null && element.getText().matches(value)) {
                            selectorDoesNotContain = false;
                            break;
                        }
                    }

                    return selectorDoesNotContain;
                }
            }
        );
    }

    /**
     * Asserts that the text in the table identified by {@code id} at row {@code row} and column {@code column} matches the given 
     * {@code text}.
     *
     * @param id identifies the table to search
     * @param row the 0-valued row number to search
     * @param column the 0-valued column number to search
     * @param text the text to verify
     */
    public void assertTableCellValue(final String id, final int row, final int column, final String text) {
        String rowString = String.valueOf(row + 1);
        String columnString = String.valueOf(column + 1);

        final String locator = "//table[@id='" + id + "']/tbody/tr[" + rowString + "]/td[" + columnString + "]";
        
        new ElementExistsWaiter(driver, text + " not found for table " + id + " at row " + rowString + " and column " + columnString).until(
            new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    WebElement cell = getElementByXPath(locator);
                    return cell != null && StringUtils.equals(text, cell.getText());
                }
            }
        );
    }

    /**
     * Asserts that the web page title contains {@code title}.
     * 
     * @param title the title to look for in the web page.
     */
    public void assertTitleContains(final String title) {
        String pageSource = driver.getPageSource();
        
        if (!StringUtils.contains(pageSource, title)) {
            if (switchToIFramePortlet()) {
                pageSource = driver.getPageSource();
            }
        }

        assertTrue("Page does not contain " + title, StringUtils.contains(pageSource, title));
    }
        
    /**
     * Gets an element in the web page.  To find the element, the following algorithm is used:
     * <ol>
     * <li>Search for an active element with an {@code id} attribute that matches {@code locator}</li>
     * <li>If not found, search for the first active element with a {@code name} attribute that matches {@code locator} depending on the value of {@code exact}</li>
     * <li>If not found, search for the first active element with a {@code title} attribute that matches {@code locator} depending on the value of {@code exact}</li>
     * <li>If not found, search for the first active link element that matches {@code locator} depending on the value of {@code exact}</li>
     * </ol>
     *
     * @param locator the id, name, title, or link name of the element to search for
     * @param exact whether the name, title, or link name should match exactly
     * @return the first matching element
     */
    public WebElement getElement(final String locator, final boolean exact) {
        return new ElementExistsWaiter(driver, locator + " not found").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    WebElement element = getElementById(locator);
                    
                    if (element == null) {
                        element = getElementByName(locator, exact);
                        if (element == null) {
                            element = getElementByTitle(locator, exact);
                            if (element == null) {
                                element = getElementByLinkText(locator, exact);
                            }
                        }
                    }
                    
                    return element;
                }
            }
        );
    }
    
    /**
     * Gets the first active element in the web page with an {@code id} attribute that matches {@code id}.
     * 
     * @param id the id of the element to search for
     * @return the first matching element
     */
    public WebElement getElementById(final String id) {
        WebElement element = null;
        
        List<WebElement> elements = getElementsById(id);
        if (!elements.isEmpty()) {
            element = elements.get(0);
        }
        
        return element;
    }

    /**
     * Gets the first active element in the web page with a {@code name} attribute that matches {@code name} depending on the value of {@code exact}.
     * 
     * @param name the name of the element to search for
     * @param exact whether the title should match exactly
     * @return the first matching element
     */
    public WebElement getElementByName(final String name, final boolean exact) {
        WebElement element = null;
        
        List<WebElement> elements = getElementsByName(name, exact);
        if (!elements.isEmpty()) {
            element = elements.get(0);
        }
        
        return element;
    }
    
    /**
     * Gets the first active element in the web page with a {@code title} attribute that matches {@code title} depending on the value of {@code exact}.
     * 
     * @param title the title of the element to search for
     * @param exact whether the title should match exactly
     * @return the first matching element
     */
    public WebElement getElementByTitle(final String title, final boolean exact) {
        WebElement element = null;
        
        List<WebElement> elements = getElementsByTitle(title, exact);
        if (!elements.isEmpty()) {
            element = elements.get(0);
        }
        
        return element;
    }
    
    /**
     * Gets the first active element in the web page with link text that matches {@code linkText} depending on the value of {@code exact}.
     * 
     * @param linkText the link text of the element to search for
     * @param exact whether the title should match exactly
     * @return the first matching element
     */
    public WebElement getElementByLinkText(final String linkText, final boolean exact) {
        WebElement element = null;
        
        List<WebElement> elements = getElementsByLinkText(linkText, exact);
        if (!elements.isEmpty()) {
            element = elements.get(0);
        }
        
        return element;
    }
    
    /**
     * Gets the first active element in the web page that matches the XPath in {@code xPath}.
     * 
     * @param xPath an XPath expression for the element to search for
     * @return the first matching element
     */
    public WebElement getElementByXPath(final String xPath) {
        WebElement element = null;
        
        List<WebElement> elements = getElementsByXPath(xPath);
        if (!elements.isEmpty()) {
            element = elements.get(0);
        }
        
        return element;
    }
    
    /**
     * Gets the first active element in the web page that matches the CSS selector in {@code cssSelector}.
     * 
     * @param cssSelector a CSS selector expression for the element to search for
     * @return the first matching element
     */
    public WebElement getElementByCssSelector(final String cssSelector) {
        WebElement element = null;
        
        List<WebElement> elements = getElementsByCssSelector(cssSelector);
        if (!elements.isEmpty()) {
            element = elements.get(0);
        }
        
        return element;
    }
    
    /**
     * Gets the first active element in the web page with text that contains {@code text}.
     * 
     * @param text the text in the element to search for
     * @return the first matching element
     */
    public WebElement getElementByText(final String text) {
        WebElement element = null;
        
        List<WebElement> elements = getElementsByText(text);
        if (!elements.isEmpty()) {
            element = elements.get(0);
        }
        
        return element;
    }
    
    /**
     * Gets all active elements in the web page with an {@code id} attribute that matches {@code id}.
     * 
     * @param id the id of the element to search for
     * @return a list of matching elements
     */
    public List<WebElement> getElementsById(final String id) {
        driver.switchTo().defaultContent();
        
        List<WebElement> elements = getActiveElementsById(id);
        
        if (switchToIFramePortlet()) {
            elements.addAll(getActiveElementsById(id));
        }
        
        return elements;
    }
    
    /**
     * Gets all active elements in the current frame with an {@code id} attribute that matches {@code id}.
     * 
     * @param id the id of the element to search for
     * @return a list of matching elements
     */
    public List<WebElement> getActiveElementsById(final String id) {
        List<WebElement> elements = new ArrayList<WebElement>();

        for (WebElement element : driver.findElements(By.id(id))) {
            if (((RenderedWebElement) element).isDisplayed()) {
                elements.add(element);
            }
        }
        
        return elements;
    }
    
    /**
     * Gets all active elements in the web page with a {@code name} attribute that matches {@code name} depending on the value of {@code exact}.
     * 
     * @param name the partial name of the element to search for
     * @param exact whether the title should match exactly
     * @return a list of matching elements
     */
    public List<WebElement> getElementsByName(final String name, final boolean exact) {
        driver.switchTo().defaultContent();
        
        List<WebElement> elements = getActiveElementsByName(name, exact);
        
        if (switchToIFramePortlet()) {
            elements.addAll(getActiveElementsByName(name, exact));
        }
        
        return elements;
    }
    
    /**
     * Gets all active elements in the current frame with a {@code name} attribute that matches {@code name} depending on the value of {@code exact}.
     * 
     * @param name the name of the element to search for
     * @param exact whether the name should match exactly
     * @return a list of matching elements
     */
    public List<WebElement> getActiveElementsByName(final String name, final boolean exact) {
        List<WebElement> activeElements = new ArrayList<WebElement>();
        
        List<WebElement> elements = new ArrayList<WebElement>();
        if (exact) {
            elements.addAll(driver.findElements(By.name(name)));
        } else {
            elements.addAll(driver.findElements(By.xpath(getAttributeContainsXPath("name", name))));
        }

        for (WebElement element : elements) {
            if (((RenderedWebElement) element).isDisplayed()) {
                activeElements.add(element);
            }
        }
        
        return activeElements;
    }
    
    /**
     * Gets all active elements in the web page with a {@code title} attribute that matches {@code title} depending on the value of {@code exact}.
     * 
     * @param title the title of the element to search for
     * @param exact whether the title should match exactly
     * @return a list of matching elements
     */
    public List<WebElement> getElementsByTitle(final String title, final boolean exact) {
        driver.switchTo().defaultContent();
        
        List<WebElement> elements = getActiveElementsByTitle(title, exact);
        
        if (switchToIFramePortlet()) {
            elements.addAll(getActiveElementsByTitle(title, exact));
        }
        
        return elements;
    }
    
    /**
     * Gets all active elements in the current frame with a {@code title} attribute matches {@code title} depending on the value of {@code exact}.
     * 
     * @param title the title of the element to search for
     * @param exact whether the title should match exactly
     * @return a list of matching elements
     */
    public List<WebElement> getActiveElementsByTitle(final String title, final boolean exact) {
        List<WebElement> activeElements = new ArrayList<WebElement>();
        
        List<WebElement> elements = new ArrayList<WebElement>();
        if (exact) {
            elements.addAll(driver.findElements(By.xpath("//*[@title = '" + title + "']")));
        } else {
            elements.addAll(driver.findElements(By.xpath(getAttributeContainsXPath("title", title))));
        }

        for (WebElement element : elements) {
            if (((RenderedWebElement) element).isDisplayed()) {
                activeElements.add(element);
            }
        }
        
        return activeElements;
    }
    
    /**
     * Gets all active elements in the web page with link text that matches {@code linkText} depending on the value of {@code exact}.
     * 
     * @param linkText the link text of the element to search for
     * @param exact whether the link text should match exactly
     * @return a list of matching elements
     */
    public List<WebElement> getElementsByLinkText(final String linkText, final boolean exact) {
        driver.switchTo().defaultContent();
        
        List<WebElement> elements = getActiveElementsByLinkText(linkText, exact);
        
        if (switchToIFramePortlet()) {
            elements.addAll(getActiveElementsByLinkText(linkText, exact));
        }
        
        return elements;
    }
    
    /**
     * Gets all active elements in the current frame with link text that matches {@code linkText} depending on the value of {@code exact}.
     * 
     * @param linkText the link text of the element to search for
     * @param exact whether the link text should match exactly
     * @return a list of matching elements
     */
    public List<WebElement> getActiveElementsByLinkText(final String linkText, final boolean exact) {
        List<WebElement> activeElements = new ArrayList<WebElement>();
        
        List<WebElement> elements = new ArrayList<WebElement>();
        if (exact) {
            elements.addAll(driver.findElements(By.linkText(linkText)));
        } else {
            elements.addAll(driver.findElements(By.partialLinkText(linkText)));
        }

        for (WebElement element : elements) {
            if (((RenderedWebElement) element).isDisplayed()) {
                activeElements.add(element);
            }
        }
        
        return activeElements;
    }
    
    /**
     * Gets all active elements in the web page that match the XPath in {@code xPath}.
     * 
     * @param xPath an XPath expression for the element to search for
     * @return a list of matching elements
     */
    public List<WebElement> getElementsByXPath(final String xPath) {
        driver.switchTo().defaultContent();
        
        List<WebElement> elements = getActiveElementsByXPath(xPath);
        
        if (switchToIFramePortlet()) {
            elements.addAll(getActiveElementsByXPath(xPath));
        }
        
        return elements;
    }
    
    /**
     * Gets all active elements in the current frame that match the XPath in {@code xPath}.
     * 
     * @param xPath an XPath expression for the element to search for
     * @return a list of matching elements
     */
    public List<WebElement> getActiveElementsByXPath(final String xPath) {
        List<WebElement> elements = new ArrayList<WebElement>();

        for (WebElement element : driver.findElements(By.xpath(xPath))) {
            if (((RenderedWebElement) element).isDisplayed()) {
                elements.add(element);
            }
        }
        
        return elements;
    }
    
    /**
     * Gets all active elements in the web page that match the CSS selector in {@code cssSelector}.
     * 
     * @param cssSelector a CSS selector for the element to search for
     * @return a list of matching elements
     */
    public List<WebElement> getElementsByCssSelector(String cssSelector) {
        driver.switchTo().defaultContent();
        
        List<WebElement> elements = getActiveElementsByCssSelector(cssSelector);
        
        if (switchToIFramePortlet()) {
            elements.addAll(getActiveElementsByCssSelector(cssSelector));
        }
        
        return elements;
    }
    
    /**
     * Gets all active elements in the current frame that match the CSS selector in {@code cssSelector}.
     * 
     * @param cssSelector a CSS selector for the element to search for
     * @return a list of matching elements
     */
    public List<WebElement> getActiveElementsByCssSelector(final String cssSelector) {
        List<WebElement> elements = new ArrayList<WebElement>();
        
        for (WebElement element : driver.findElements(By.cssSelector(cssSelector))) {
            if (((RenderedWebElement) element).isDisplayed()) {
                elements.add(element);
            }
        }
        
        return elements;
    }
    
    /**
     * Gets all active elements in the web page with text that contains {@code text}.
     * 
     * @param text the text in the element to search for
     * @return a list of matching elements
     */
    public List<WebElement> getElementsByText(final String text) {
        driver.switchTo().defaultContent();
        
        List<WebElement> elements = getActiveElementsByText(text);
        
        if (switchToIFramePortlet()) {
            elements.addAll(getActiveElementsByText(text));
        }
        
        return elements;
    }
    
    /**
     * Gets all active elements in the current frame with text that contains {@code text}.
     * 
     * @param text the text in the element to search for
     * @return a list of matching elements
     */
    public List<WebElement> getActiveElementsByText(final String text) {
        List<WebElement> elements = new ArrayList<WebElement>();

        for (WebElement element : driver.findElements(By.xpath("//*[contains(text(), '" + text + "')]"))) {
            if (((RenderedWebElement) element).isDisplayed()) {
                elements.add(element);
            }
        }
        
        return elements;
    }
    
    /**
     * Attempts to switch to KC's inner frame, named 'iframeportlet'.
     *
     * @return true if the driver successfully switched to the inner frame, false otherwise
     */
    public boolean switchToIFramePortlet() {
        boolean switchToIFramePortletSuccessful = true;
        
        try {
            driver.switchTo().frame("iframeportlet");
        } catch (Exception e) {
            switchToIFramePortletSuccessful = false;
        }
        
        return switchToIFramePortletSuccessful;
    }
    
   /**
    * Returns the XPath string that searches for elements that have an {@code attribute} that contains {@code text}.
    * 
    * @param attribute the name of the attribute
    * @param text the text to search for in the attribute
    * @return an XPath expression for elements that have an {@code attribute} that contains {@code text}
    */
    public String getAttributeContainsXPath(final String attribute, final String text) {
        return "//*[contains(@" + attribute + ", '" + text + "')]";
    }
    
    /**
     * Returns the list of elements that contain errors in the element identified by {@code panelId}.
     * 
     * @param panelId the id attribute of the panel
     * @return a list of errors contained in {@code panelId}
     */
    public List<WebElement> getErrors(String panelId) {
        return getElementsByXPath("//div[@id='" + panelId + "']//div[@style='display: list-item; margin-left: 20px;']");
    }
    
}