/*
Copyright 2005-2006 Seth Fitzsimmons <seth@mojodna.net>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package net.mojodna.sprout;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.DelegatingRequestProcessor;

/**
 * Extension of Spring's <code>DelegatingRequestProcessor</code> that adds
 * Sprout initialization.
 * 
 * @see org.springframework.web.struts.DelegatingRequestProcessor
 * @author Seth Fitzsimmons
 */
public class SproutRequestProcessor extends DelegatingRequestProcessor {
    /**
     * Provides each Sprout with an extensive set of objects during its
     * initialization.
     */
    @Override
    protected ActionForward processActionPerform(final HttpServletRequest request, final HttpServletResponse response, final Action action, final ActionForm form, final ActionMapping mapping) throws IOException, ServletException {
        // initialize Sprout if necessary
        if ( action instanceof Sprout )
            ((Sprout) action).init( mapping, form, request, response );
        
        return super.processActionPerform( request, response, action, form, mapping );
    }
}
