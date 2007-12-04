/*
Copyright 2005-2006 Seth Fitzsimmons <seth@note.amherst.edu>

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
package net.mojodna.sprout.support;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.struts.ContextLoaderPlugIn;
import org.springframework.web.util.WebUtils;

/**
 * Convenience class for Spring-aware Servlets.
 *
 * <p>Provides a reference to the current Spring application context, e.g.
 * for bean lookup or resource loading. Auto-detects a ContextLoaderPlugIn
 * context, falling back to the root WebApplicationContext. For typical
 * usage, i.e. accessing middle tier beans, use a root WebApplicationContext.</p>
 *
 * @author Juergen Hoeller
 * @author Seth Fitzsimmons
 * @see ContextLoaderPlugIn#SERVLET_CONTEXT_PREFIX
 * @see WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
 * @see org.springframework.web.context.ContextLoaderListener
 * @see org.springframework.web.context.ContextLoaderServlet
 */
public class SpringHttpServlet extends HttpServlet {
	private WebApplicationContext webApplicationContext;

	private MessageSourceAccessor messageSourceAccessor;
	
	/**
	 * Initialize the WebApplicationContext for this Action.
	 * Invokes onInit after successful initialization of the context.
	 * @see #initWebApplicationContext
	 * @see #onInit
	 */
	public void init(final ServletConfig cfg) throws ServletException {
	    super.init( cfg );
	    if ( null != cfg ) {
		    this.webApplicationContext = initWebApplicationContext( cfg.getServletContext() );
		    this.messageSourceAccessor = new MessageSourceAccessor(this.webApplicationContext);
		    onInit();
	    }
	}

	/**
	 * Fetch ContextLoaderPlugIn's WebApplicationContext from the ServletContext,
	 * falling back to the root WebApplicationContext (the usual case).
	 * @return the WebApplicationContext
	 * @throws IllegalStateException if no WebApplicationContext could be found
	 * @see ContextLoaderPlugIn#SERVLET_CONTEXT_PREFIX
	 * @see WebApplicationContextUtils#getWebApplicationContext
	 */
	protected WebApplicationContext initWebApplicationContext(final ServletContext sc)
			throws IllegalStateException {
		WebApplicationContext wac = (WebApplicationContext)
				sc.getAttribute(ContextLoaderPlugIn.SERVLET_CONTEXT_PREFIX);
		if ( null == wac ) {
			wac = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
		}
		return wac;
	}

	/**
	 * Return the current Spring WebApplicationContext.
	 */
	protected final WebApplicationContext getWebApplicationContext() {
		return this.webApplicationContext;
	}

	/**
	 * Return a MessageSourceAccessor for the application context
	 * used by this object, for easy message access.
	 */
	protected final MessageSourceAccessor getMessageSourceAccessor() {
		return this.messageSourceAccessor;
	}

	/**
	 * Return the temporary directory for the current web application,
	 * as provided by the servlet container.
	 * @return the File representing the temporary directory
	 */
	protected final File getTempDir() {
		return WebUtils.getTempDir(getServletContext());
	}

	/**
	 * Callback for custom initialization after the context has been set up.
	 * @see #init(ServletConfig)
	 */
	protected void onInit() {}
}
