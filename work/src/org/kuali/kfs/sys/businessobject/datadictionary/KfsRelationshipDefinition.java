/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject.datadictionary;

import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.framework.group.GroupEbo;
import org.kuali.rice.kim.framework.role.RoleEbo;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.county.County;
import org.kuali.rice.location.api.postalcode.PostalCode;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.framework.campus.CampusEbo;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.county.CountyEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

public class KfsRelationshipDefinition extends RelationshipDefinition {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KfsRelationshipDefinition.class);

    @Override
    public Class<?> getTargetClass() {
        if (targetClass == null) {
            Class propertyClass = DataDictionary.getAttributeClass(sourceClass, objectAttributeName);
            if (propertyClass == null) {
                throw new AttributeValidationException("cannot get valid class for property '" + objectAttributeName + "' as an attribute of '" + sourceClass + "'");
            }
            // RICE20 - HACK ALERT!!!! - if the property class is one of the
            // ones we use via BOs, change the type to an EBO to attempt to
            // preserve the lookups
            if ( Campus.class.isAssignableFrom(propertyClass) ) {
                LOG.error( "ALERT! : " + propertyClass.getName() + " Reference Unconverted to Ebo class: " + sourceClass.getName() + "." + objectAttributeName );
                propertyClass = CampusEbo.class;
            } else if ( State.class.isAssignableFrom(propertyClass) ) {
                LOG.error( "ALERT! : " + propertyClass.getName() + " Reference Unconverted to Ebo class: " + sourceClass.getName() + "." + objectAttributeName );
                propertyClass = StateEbo.class;
            } else if ( PostalCode.class.isAssignableFrom(propertyClass) ) {
                LOG.error( "ALERT! : " + propertyClass.getName() + " Reference Unconverted to Ebo class: " + sourceClass.getName() + "." + objectAttributeName );
                propertyClass = PostalCodeEbo.class;
            } else if ( Country.class.isAssignableFrom(propertyClass) ) {
                LOG.error( "ALERT! : " + propertyClass.getName() + " Reference Unconverted to Ebo class: " + sourceClass.getName() + "." + objectAttributeName );
                propertyClass = CountryEbo.class;
            } else if ( County.class.isAssignableFrom(propertyClass) ) {
                LOG.error( "ALERT! : " + propertyClass.getName() + " Reference Unconverted to Ebo class: " + sourceClass.getName() + "." + objectAttributeName );
                propertyClass = CountyEbo.class;
            } else if ( Role.class.isAssignableFrom(propertyClass) ) {
                LOG.error( "ALERT! : " + propertyClass.getName() + " Reference Unconverted to Ebo class: " + sourceClass.getName() + "." + objectAttributeName );
                propertyClass = RoleEbo.class;
            } else if ( Group.class.isAssignableFrom(propertyClass) ) {
                LOG.error( "ALERT! : " + propertyClass.getName() + " Reference Unconverted to Ebo class: " + sourceClass.getName() + "." + objectAttributeName );
                propertyClass = GroupEbo.class;
            } else {
                if (!BusinessObject.class.isAssignableFrom(propertyClass)) {
                    throw new AttributeValidationException("property '" + objectAttributeName + "' is not a BusinessObject (" + propertyClass.getName() + ") on sourceClass (" + sourceClass +")");
                }
            }


            targetClass = propertyClass;
        }
        return targetClass;
    }

    @Override
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Validating Relationships on BO: " + rootBusinessObjectClass.getSimpleName() + "." + objectAttributeName );
        }
        // TODO Auto-generated method stub
        super.completeValidation(rootBusinessObjectClass, otherBusinessObjectClass);
    }

}
