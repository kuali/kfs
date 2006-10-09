/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.test.fixtures;

import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.SpringServiceLocator;
import junit.framework.Assert;

public enum UserNameFixture {

    NO_SESSION, // This is not a user name.  It is a Sentinal value telling KualiTestBase not to create a session.  (It's needed because null is not a valid default for the WithTestSpringContext annotation's session element.)
    KULUSER, // This is the KualiUser.SYSTEM_USER, which certain automated document type authorizers require.
    KHUNTLEY, // KualiTestBaseWithSession used this one by default. (testUsername in configuration.properties, no longer used but cannot be removed because that file cannot be committed).
    DFOGLE,
    RJWEISS,
    RORENFRO,
    HSCHREIN,
    LRAAB,
    JHAVENS,
    KCOPLEY,
    MHKOZLOW,
    INEFF,
    VPUTMAN,
    CSWINSON,
    MYLARGE,
    RRUFFNER,
    SEASON,
    ;

    static {
        Assert.assertEquals(KualiUser.SYSTEM_USER, KULUSER.toString());
    }

    public AuthenticationUserId getAuthenticationUserId() {
        return new AuthenticationUserId(toString());
    }

    public KualiUser getKualiUser()
        throws UserNotFoundException
    {
        return SpringServiceLocator.getKualiUserService().getKualiUser(getAuthenticationUserId());
    }
}
