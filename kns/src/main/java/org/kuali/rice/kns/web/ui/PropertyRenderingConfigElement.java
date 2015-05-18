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
package org.kuali.rice.kns.web.ui;

import org.kuali.rice.core.web.format.Formatter;

/**
 * Interface to be implemented by user interface elements that hold configuration about rendering a
 * property
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public interface PropertyRenderingConfigElement {

	/**
	 * @return name of the property that is to be rendered
	 */
	public String getPropertyName();

	/**
	 * @param propertyName
	 *            - name of the property that is to be rendered
	 */
	public void setPropertyName(String propertyName);

	/**
	 * @return name of the property that is to be rendered
	 */
	public String getPropertyValue();

	/**
	 * @param propertyValue
	 *            - value of the property that is to be rendered
	 */
	public void setPropertyValue(String propertyValue);

	/**
	 * @return value of the property that is to be rendered
	 */
	public Formatter getFormatter();

	/**
	 * @param formatter
	 *            - {@link Formatter} that should be use when rendering property value
	 */
	public void setFormatter(Formatter formatter);

	/**
	 * @return name of the property that should be displayed in place of property we are rendering
	 *         (only applies when read-only)
	 */
	public String getAlternateDisplayPropertyName();

	/**
	 * @param alternateDisplayPropertyName
	 *            - name of the property that should be displayed in place of property we are
	 *            rendering (only applies when read-only)
	 */
	public void setAlternateDisplayPropertyName(String alternateDisplayPropertyName);

	/**
	 * @return name of the property that should be displayed in addition to the property we are
	 *         rendering (only applies when read-only)
	 */
	public String getAdditionalDisplayPropertyName();

	/**
	 * @param additionalDisplayPropertyName
	 *            - name of the property that should be displayed in addition to the property we are
	 *            rendering (only applies when read-only)
	 */
	public void setAdditionalDisplayPropertyName(String additionalDisplayPropertyName);

}
