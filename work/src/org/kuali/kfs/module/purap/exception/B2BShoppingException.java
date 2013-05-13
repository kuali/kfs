/*
 * Copyright 2005-2011 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.exception;

import org.kuali.rice.core.api.exception.KualiException;

public class B2BShoppingException extends KualiException {

  public B2BShoppingException(String message) {
    super(message);
  }

  public B2BShoppingException(String message, Throwable t) {
    super(message, t);
  }

  public B2BShoppingException(Throwable t) {
    super(t);
  }

}
