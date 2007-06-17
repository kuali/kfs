/*
 * Created on Jul 11, 2004
 *
 */
package org.kuali.module.pdp.bo;

import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;

/**
 * @author jsissom
 *
 */
public interface UserRequired {
    public void updateUser(UniversalUserService userService) throws UserNotFoundException;
}
