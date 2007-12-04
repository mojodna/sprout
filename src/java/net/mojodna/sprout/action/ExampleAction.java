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
package net.mojodna.sprout.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.mojodna.sprout.Sprout;
import net.mojodna.sprout.example.ExampleBean;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <p>Example Action class for Sprout usage. In addition to what is described
 * below, <code>/index</code> will be registered with Struts.  You may
 * override what it does by overriding the <code>index()</code> method
 * here.</p>
 * 
 * <p><strong>NOTE:</strong> Subclasses of this class will inherit URLs
 * defined here.</p>
 * 
 * @author Seth Fitzsimmons
 */
public class ExampleAction extends Sprout {
    /**
     * This is still a conventional Struts Action in many ways, which means
     * that it must be written in a threadsafe manner.  Remember to clean up
     * after yourself or ensure that it will always be reset during the
     * initialization process.
     * 
     * This is often accompanied by an accessor method.
     */
    private ThreadLocal<String> userHolder = new ThreadLocal();
    
    /**
     * Beans wired by Spring
     * may be shared between threads because they are assumed to already be
     * threadsafe.
     */
    private ExampleBean eb;

    
    /**
     * Setter for ExampleBean service.  Used by Spring while auto-wiring.
     */
    public void setExampleBean(final ExampleBean eb) {
        this.eb = eb;
    }
    
    /**
     * Initialization callback; occurs after auto-wiring (which happens once)
     * but before each method invocation.
     */
    @Override
    protected void onInit(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
        super.onInit( mapping, form, request, response );
        userHolder.set("Seth");
    }
    
    /**
     * Accessor method for userHolder ThreadLocal.
     */
    protected String getUser() {
        return userHolder.get();
    }
    
    /**
     * <p>Uses the shared ExampleBean service (autowired by Spring) to obtain a
     * greeting.</p>
     * 
     * <p>Forwards to an appropriate JSP where the name is determined by the
     * method name (i.e. <code>greet.jsp</code>).</p>
     * 
     * <p>This creates an action mapping equivalent to:
     * <pre>
     * &lt;action path="/greet"
     *         type="net.mojodna.sprout.action.ExampleAction"
     *         name="ExampleActionForm"
     *         parameter="greet"&gt;
     *   &lt;forward name="success" path="/greet.jsp" /&gt;
     * &lt;/action&gt;
     * </pre>
     * 
     * <p>Default formname is determined from the class name.  E.g.:
     * <code>ExampleAction</code> maps to <code>ExampleActionForm</code>.</p>
     * 
     * <p>If <code>ExampleActionForm</code> is undefined, "name" will not be set
     * and no ActionForm will be available.</p>
     * 
     * <p>Additionally, a local forward is created with the name <em>failure</em> that
     * redirects to <code>/complex_example.do</code>.</p>
     */
    @Forward(name="failure", path="/complex_example.do", redirect=true)
    public ActionForward greet(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
        request.setAttribute("greeting", eb.getGreeting() );
        return mapping.findForward( FWD_SUCCESS );
    }
    
    /**
     * <p>Demonstrates available annotations and CamelCase conversion.</p>
     * 
     * <p>Uses <code>ComplexForm</code> form-bean defined in
     * <code>struts-config.xml</code> rather than default
     * <code>ExampleActionForm</code>.  Equivalent to setting <em>name</em>
     * property in <code>struts-config.xml</code>.</p>
     * 
     * <p>Specifies <code>index.jsp</code> as the input file if validation
     * fails. Equivalent to setting <em>input</em> property in
     * <code>struts-config.xml</code>.
     * <strong>NOTE:</strong> this is required if this is not the source
     * action.</p>
     * 
     * <p>Specifies <em>request</em> as the destination scope for ActionForms.
     * Equivalent to setting <em>scope</em> property in
     * <code>struts-config.xml</code>.</p>
     * 
     * <p>Validates specified ActionForm according to rules in
     * <code>validation.xml</code>.  Equivalent to setting <em>validate</em>
     * property to <em>true</em> in <code>struts-config.xml</code>.
     */
    @FormName("ComplexForm")
    @Input("index.jsp")
    @Scope("request")
    @Validate
    public ActionForward complexExample(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
        // get "name" property from ComplexForm as a String
        final String name = f("name");
        request.setAttribute("name", name);
        
        // get "id" property from ComplexForm as an Integer
        final Integer id = (Integer) F("id");
        request.setAttribute("id", id);
        
        // set "greeting" property
        s("greeting", "'Allo");
        
        // calculated JSP is complex_example.jsp
        return mapping.findForward( FWD_SUCCESS );
    }
}
