/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kew.api.document.attribute;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.w3c.dom.Element;

/**
 * An abstract representation of the {@code DocumentAttributeContract} which can be used as the super class for
 * concrete immutable data transfer object implementations of document attributes.  This class also defines an abstract
 * builder implementation which can be extended by it's subclasses to create their own internal builder implementations.
 *
 * <p>The KEW api defines the understood set of document attribute implementations, so it is not generally of value for
 * a client of the api to subclass this class.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org).
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentAttribute.Constants.TYPE_NAME, propOrder = {
    DocumentAttribute.Elements.NAME,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
@XmlSeeAlso( { DocumentAttributeString.class, DocumentAttributeDateTime.class, DocumentAttributeInteger.class, DocumentAttributeDecimal.class } )
public abstract class DocumentAttribute extends AbstractDataTransferObject implements DocumentAttributeContract {

    private static final long serialVersionUID = -1935235225791818090L;

    @XmlElement(name = Elements.NAME, required = true)
    private final String name;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    protected DocumentAttribute() {
        this.name = null;
    }

    DocumentAttribute(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name was null or blank");
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result
                + ((getValue() == null) ? 0 : getValue().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DocumentAttributeString other = (DocumentAttributeString) obj;
        if (getName() == null) {
            if (other.getName()  != null) {
                return false;
            }
        } else if (!getName() .equals(other.getName() )) {
            return false;
        }
        if (getValue() == null) {
            if (other.getValue() != null) {
                return false;
            }
        } else if (!getValue().equals(other.getValue())) {
            return false;
        }
        return true;
    }

    /**
     * An abstract base class that can be extended by concrete builder implementations of subclasses of
     * {@code DocumentAttribute}.
     *
     * @param <T> the type of the value contained within the document attribute that is built by this builder
     */
    public abstract static class AbstractBuilder<T> implements Serializable, ModelBuilder, DocumentAttributeContract {

        private static final long serialVersionUID = -4402662354421207678L;

        private String name;
        private T value;

        protected AbstractBuilder(String name) {
            setName(name);
        }

        @Override
        public String getName() {
            return name;
        }

        /**
         * Sets the name of the document attribute that will be built by this builder.
         *
         * @param name the name of the document attribute to set, must not be a null or blank value
         * @throws IllegalArgumentException if the given name is a null or blank value.
         */
        public void setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name was null or blank");
            }
            this.name = name;
        }

        @Override
        public T getValue() {
            return value;
        }

        /**
         * Sets the value of the document attribute that will be built by this builder.
         *
         * @param value the value of the document attribute to set
         */
        public void setValue(T value) {
            this.value = value;
        }

        /**
         * Build the {@code DocumentAttribute} for this builder based on it's current state.
         *
         * @return the instantiated instance of {@code DocumentAttribute} which was built by this builder
         */
        @Override
        public abstract DocumentAttribute build();

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String TYPE_NAME = "DocumentAttributeType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String NAME = "name";
    }

}
