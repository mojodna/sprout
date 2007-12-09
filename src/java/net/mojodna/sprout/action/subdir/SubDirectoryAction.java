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
package net.mojodna.sprout.action.subdir;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.mojodna.sprout.Sprout;

/**
 * <p>Sprout will use the package name to the right of <em>action</em> to
 * determine the appropriate path to use when registering actions.</p>
 * 
 * <p>E.g.: <code>net.mojodna.sprout.action.subdir</code> will correspond to:
 * <code>/subdir/*</code></p>
 * 
 * @author Seth Fitzsimmons
 */
public class SubDirectoryAction extends Sprout {
    /**
     * <p>Optionally overrides Sprout's <code>index()</code> method to further
     * initialize <code>index.jsp</code>.</p>
     * 
     * <p>This will respond to both <code>/subdir/</code> and
     * <code>/subdir/index.do</code></p>
     */
    @Override
    public ActionForward index(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
        // do something interesting
        return mapping.findForward( FWD_SUCCESS );
    }
}
