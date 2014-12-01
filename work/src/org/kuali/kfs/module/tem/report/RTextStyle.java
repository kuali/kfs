/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.report;

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
