/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.tem.report;

/**
 * 
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class RTextStyle {
    private String fontName;
    private boolean bold;
    private boolean italic;
    private Integer fontSize;

    /**
     * Gets the bold property. 
     * @return Returns the bold.
     */
    public boolean getBold() {
        return bold;
    }

    /**
     * Gets the bold property. 
     * @return Returns the bold.
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * Sets the bold property value.
     * @param bold The bold to set.
     */
    public void setBold(final boolean bold) {
        this.bold = bold;
    }

    /**
     * Gets the italic property. 
     * @return Returns the italic.
     */
    public boolean getItalic() {
        return italic;
    }

    /**
     * Gets the italic property. 
     * @return Returns the italic.
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * Sets the italic property value.
     * @param italic The italic to set.
     */
    public void setItalic(final boolean italic) {
        this.italic = italic;
    }

    /**
     * Gets the fontName property. 
     * @return Returns the fontName.
     */
    public String getFontName() {
        return fontName;
    }

    /**
     * Sets the fontName property value.
     * @param fontName The fontName to set.
     */
    public void setFontName(final String fontName) {
        this.fontName = fontName;
    }


    /**
     * Gets the fontSize property. 
     * @return Returns the fontSize.
     */
    public Integer getFontSize() {
        return fontSize;
    }

    /**
     * Sets the fontSize property value.
     * @param fontSize The fontSize to set.
     */
    public void setFontSize(final Integer fontSize) {
        this.fontSize = fontSize;
    }

}