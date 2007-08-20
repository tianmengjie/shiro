/*
 * Copyright (C) 2005-2007 Les Hazlewood
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the
 *
 * Free Software Foundation, Inc.
 * 59 Temple Place, Suite 330
 * Boston, MA 02111-1307
 * USA
 *
 * Or, you may view it online at
 * http://www.opensource.org/licenses/lgpl-license.php
 */
package org.jsecurity.authz.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsecurity.SecurityUtils;
import org.jsecurity.authz.AuthorizedAction;
import org.jsecurity.authz.UnauthorizedException;
import org.jsecurity.context.SecurityContext;

/**
 * This class is an abstraction of AOP method interceptor behavior specific to JSecurity that
 * leaves AOP implementation specifics to be handled by subclass implementations.  Shared behavior
 * is defined in this class.
 *
 * <p>Different frameworks represent Method Invocations (MI) in different ways, so this class
 * aggregates as much JSecurity interceptor behavior as possible, leaving framework MI details to
 * subclasses via template methods.
 *
 * @since 0.2
 * @author Les Hazlewood
 */
public abstract class AbstractAuthorizationInterceptor {

    protected transient final Log log = LogFactory.getLog( getClass() );

    public AbstractAuthorizationInterceptor(){}

    protected Object invoke( final Object implSpecificMethodInvocation ) throws Throwable {

        SecurityContext secCtx = SecurityUtils.getSecurityContext();

        if ( secCtx != null ) {
            AuthorizedAction action = createAuthzAction( implSpecificMethodInvocation );
            //will throw an exception if not authorized to execute the action:
            secCtx.checkAuthorization( action );
        } else {
            String msg = "No SecurityContext available (User not authenticated?).  Authorization failed.";
            throw new UnauthorizedException( msg );
        }

        //secCtx was found, and it determined the AOP invocation chain should proceed:
        return continueInvocation( implSpecificMethodInvocation );
    }

    protected abstract AuthorizedAction createAuthzAction( Object implSpecificMethodInvocation );

    protected abstract Object continueInvocation( Object implSpecificMethodInvocation ) throws Throwable;
}
