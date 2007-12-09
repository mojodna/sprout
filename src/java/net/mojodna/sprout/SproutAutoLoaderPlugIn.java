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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.ServletException;

import net.mojodna.sprout.Sprout.Forward;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.validator.LazyValidatorForm;
import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.struts.ContextLoaderPlugIn;

/**
 * <p>Finds Sprouts registered in a Spring context and registers them with
 * Struts, using cues from annotations present to set specific properties.</p> 
 * 
 * <p>This needs to be configured as a plug-in in
 * <code>struts-config.xml</code>.</p>
 *
 * <p>TODO create GlobalForward annotation and create global forwards based on that</p>
 * 
 * @author Seth Fitzsimmons
 */
public class SproutAutoLoaderPlugIn extends ContextLoaderPlugIn {
    private final static Logger log = Logger.getLogger( SproutAutoLoaderPlugIn.class );

    /**
     * Extends Spring's ContextLoaderPlugIn initialization callback to add
     * Struts registration of Sprouts.
     */
    public void onInit() throws ServletException {
        super.onInit();

		// create a default actionform
		final FormBeanConfig fbc = new FormBeanConfig();
        fbc.setName( Sprout.SPROUT_DEFAULT_ACTION_FORM_NAME );
        fbc.setType( LazyValidatorForm.class.getName() );
        getModuleConfig().addFormBeanConfig( fbc );
        
        final WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext( getServletContext() );

        try {
            final String[] beanNames = wac.getBeanNamesForType( Sprout.class );
            for ( int i = 0; i < beanNames.length; i++ ) {
                final Sprout bean = (Sprout) wac.getBean( beanNames[i] );
                final String[] aliases = wac.getAliases( beanNames[i] );
                for ( int j = 0; j < aliases.length; j++ ) {
                    final String name = aliases[j].substring( aliases[j].lastIndexOf('/') + 1 );
                    try {
                        final Method method = findMethod( name, bean.getClass() );
                        log.debug( aliases[j] + " -> " + beanNames[i] + "." + name );
                        
                        final ActionMapping ac = new ActionMapping();
                        ac.setParameter( method.getName() );
                        ac.setPath( aliases[j] );
                        
                        // establish defaults
                        String actionForm = bean.getClass().getSimpleName() + Sprout.DEFAULT_FORM_SUFFIX;
                        String input = aliases[j] + Sprout.DEFAULT_VIEW_EXTENSION;
                        String scope = Sprout.DEFAULT_SCOPE;
                        boolean validate = false;
                        ac.addForwardConfig( makeForward( Sprout.FWD_SUCCESS, aliases[j] + ".jsp" ) );
                        
                        // process annotations and override defaults where appropriate
                        final Annotation[] annotations = method.getAnnotations();
                        for (int k = 0; k < annotations.length; k++ ) {
                            final Annotation a = annotations[k];
                            final Class type = a.annotationType();
                            if ( type.equals( Sprout.FormName.class) )
                                actionForm = ((Sprout.FormName) a).value();
                            else if ( type.equals( Sprout.Forward.class ) ) {
                                final Forward fwd = (Sprout.Forward) a;
                                for (int m=0; m < fwd.path().length; m++ ) {
                                    String fwdPath = fwd.path()[m];
                                    String fwdName = Sprout.FWD_SUCCESS;
                                    boolean fwdRedirect = false;
                                    if ( fwd.name().length - 1  >= m )
                                        fwdName = fwd.name()[m];
                                    if ( fwd.redirect().length - 1  >= m )
                                        fwdRedirect = fwd.redirect()[m];
                                    ac.addForwardConfig( makeForward( fwdName, fwdPath, fwdRedirect ) );
                                }
                            } else if ( type.equals( Sprout.Input.class) )
                                input = ((Sprout.Input) a).value();
                            if ( type.equals( Sprout.Scope.class) )
                                scope = ((Sprout.Scope) a).value();
                            else if ( type.equals( Sprout.Validate.class ) )
                                validate = ((Sprout.Validate) a).value();
                        }
                        
                        // use values
                        if ( null != getModuleConfig().findFormBeanConfig( actionForm ) )
                            ac.setName( actionForm );
                        else {
                            log.info("No ActionForm defined: " + actionForm + ". Using default.");
                            ac.setName( Sprout.SPROUT_DEFAULT_ACTION_FORM_NAME );
                        }
                        ac.setValidate( validate );
                        ac.setInput( input );
                        ac.setScope( scope );
                        
                        getModuleConfig().addActionConfig( ac );
                    }
                    catch (final NoSuchMethodException e) {
                        log.warn("Could not register action; no such method: " + name, e);
                    }
                }
            }
        }
        catch (final BeansException e) {
            log.warn( "Error while auto loading Sprouts: " + e.getMessage(), e );
            throw new ServletException( e );
        }
        
        /* Useful if you'd like a view into registered paths
         * TODO create a ServletFilter that displays these
        log.debug("Dumping action configs...");
        final ActionConfig[] configs = getModuleConfig().findActionConfigs();
        for ( int i = 0; i < configs.length; i++ ) {
            log.debug( configs[i].getPath() );
        }
        */
    }
    
    /**
     * Helper method for creating ActionForwards.
     * 
     * @param name Forward name.
     * @param path Registered path.
     * @return ForwardConfig.
     */
    private ForwardConfig makeForward(final String name, final String path) {
        return makeForward( name, path, false );
    }

    /**
     * Helper method for creating ActionForwards.
     * 
     * @param name Forward name.
     * @param path Registered path.
     * @param redirect Whether this should be an HTTP redirect.
     * @return ActionForward.
     */
    private ActionForward makeForward(final String name, final String path, final boolean redirect) {
        final ActionForward fc = new ActionForward();
        fc.setName( name );
        fc.setPath( path );
        fc.setRedirect( redirect );
        return fc;
    }
    
    /**
     * Finds the method in the target class which corresponds to a registered
     * pathname.
     * 
     * @param name Action portion of pathname.
     * @param clazz Target class.
     * @return Corresponding method.
     * @throws NoSuchMethodException when corresponding method cannot be found.
     */
    private Method findMethod(final String name, final Class clazz) throws NoSuchMethodException {
        final Method[] methods = clazz.getMethods();
        for ( int i = 0; i < methods.length; i++ ) {
            String methodName = methods[i].getName();
            if ( methodName.equals("publick") )
                methodName = "public";
            if ( methodName.equalsIgnoreCase( name.replaceAll("_([a-z])", "$1") ) )
                return methods[i];
        }
        throw new NoSuchMethodException( name );
    }
}
