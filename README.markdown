# Sprout

## Annotation-Powered Simplicity for Struts

### Overview

_Sprout aims to significantly simplify development with Struts by reducing the
amount of configuration required through the use of annotations and sensible
defaults._

### Basics

Sprout requires JDK 1.5.  Without that, you're out of luck.

Sprout is an extension of a Struts `MappingDispatchAction`, which allows for
multiple actions to be defined within the same _Action_ class. In this case,
the name of the method is mapped to the URL (after converting method names;
_methodName_ is exposed as _/method\_name.do_). Paths are determined based on
the package name of the action. For example,
_net.mojodna.sprout.action.HomeAction_ corresponds to _/_, while
_net.mojodna.sprout.action.example.ExampleAction_ corresponds to _/example/_.

Sprout also uses a custom _RequestProcessor_ &mdash; `SproutRequestProcessor`,
which extends Spring's _DelegatingRequestProcessor_. This means that you can
specify dependencies within your actions using setter-injection.

Sprout is also _completely_ backward-compatible with legacy Struts
applications.. It was built for use in a legacy Struts application; many of
the older Actions are untouched &mdash; new development is done using Sprout
(I often find myself _removing_ action-mappings while adding new
functionality).

### Annotations

All annotations are optional.

#### @FormName

Allows the developer to override the name of the form-bean (defined in
_struts-config.xml_) used for this method. This is equivalent to setting the
_name_ attribute within an _action_ mapping.

_Defaults to ${action-name}Form; e.g. for **AdminAction** the default
ActionForm name would be **AdminActionForm**._

#### @Forward

Specifies additional forwards. Multiple forwards may be specified by providing
arrays as arguments to _name_, _path_, and _redirect_. _redirect_ defaults to
_false_.

A default redirect is provided; the key is `Sprout.FWD_SUCCESS` and the path
is the converted path + .jsp. E.g., _AdminAction.methodName()_ corresponds to
_method\_name.jsp_.

_e.g. @Forward(name="failure", path="/failure.jsp" redirect="true")_

#### @Input

This annotation is required if this action is validating the output from a
different action. In that case, the argument to _@Input_ should be the path to
the JSP containing the form whose input is being validated.

_e.g @Input("login.jsp") if this is **not** login() and the action that
initiated this request **is** login()._

#### @Scope

Specifies the _scope_ attribute for the generated action mapping. This exists
primarily for completeness; it is likely that you may never use this
annotation.

_As with **struts-config.xml**, the default is **request**._

#### @Validate

Specifies the _validate_ attribute for the generated action mapping. Set this
to _true_ if you desire the output of this action to be validated. For this to
have any effect, you must have specified rules in _validator-rules.xml_.

_Sprout does not contain anything to ease the actual validation process at
this time._

### Example

_src/java/net/mojodna/sprout/action/example/ExampleAction.java_:

	// URL should be /example/*
	package net.mojodna.sprout.action.example;
	
	// ...
	
	public class ExampleAction extends Sprout {
		// overrides Sprout.index()
		public ActionForward index( ... ) {
			// do something
			
			// redirect to index.jsp
			return mapping.findForward( FWD_SUCCESS );
		}
	}

_src/java/applicationContext.xml_:

	...
	<bean name="ExampleAction" class="net.mojodna.sprout.action.example.ExampleAction" singleton="true" />
	...

_src/web/WEB-INF/struts-config.xml_:

	...
	<form-bean name="ExampleActionForm" type="org.apache.struts.validator.DynaValidatorForm">
		<form-property name="..." type="..." />
		...
	</form-bean>
	
	<!-- No action-mappings!!! -->
	<action-mappings />
	
	<!-- Define an alternate RequestProcessor -->
	<controller processorClass="net.mojodna.sprout.SproutRequestProcessor" />
	
	<!-- Sprout plug-in -->
	<plug-in className="net.mojodna.sprout.SproutAutoLoaderPlugIn" />
	...


### Shorthand

#### Index Actions

Sprout contains an _index()_ method to speed up the process of getting
something working. To use this, subclass Sprout (no methods necessary),
register the action-bean in _applicationContext.xml_ and create a
corresponding _index.jsp_. When you need to add logic to the action, override
_index()_ in your Sprout sub-class and add it there.

#### DynaActionForms

Helper methods have been added to ease development using _DynaActionForms_.

	String key = "foo";
	String value = "bar";
	// returns a String
	f( key ) == ((DynaActionForm) form).getString( key );
	
	// returns an Object
	F( key ) == ((DynaActionForm) form).get( key );
	
	// sets a value
	s( key, value ) == ((DynaActionForm) form).set( key, value );


### ActionMessage handling

Sprout contains adaptations to the traditional way Struts handles
_ActionMessages_. `getMessages()` and `getErrors()` have been modified to
store and retrieve messages from the session rather than the request. This
means that messages and errors will be displayed (and subsequently cleared) on
the next invocation of `<html:messages />` or a variant (such as
`<ui:notifications />`), regardless of whether either of the _get_ methods
have been called or if the invocation occurs during a separate request.

Sample message / error handling code (within an Action):

	ActionMessages msgs = getMessages( request );
	ActionMessages errors = getErrors( request );
	
	// Add a message
	msgs.add( ... );
	
	// Add an error
	errors.add( ... );
	
	// Save messages and errors
	saveMessages( request, msgs );
	saveErrors( request, errors );

`<ui:notifications />` (_src/web/WEB-INF/tags/ui/notifications.tag_) is an
alternative tag file that can be modified for your use. The primary difference
between `<html:messages />` is that it will display both messages and errors
(and will discriminate between them, allowing you to style them differently
depending on your application's needs).

### Servlets and Taglibs

Spring's Struts integration lacks the ability to autowire servlets and
taglibs. Sprout contains classes (`Sproutlet` and `SproutTag`) that can be
subclassed to provide auto-wiring capability. They will be wired during their
initialization process.

_These support classes only support the **byName** auto-wiring mechanism._

### Q + A
* Q: Why the Spring dependencies?
* A: I'm already using Spring.  There's a good chance that you are as well.
     Removing the dependency makes reflection upon the registered actions (or
     finding them in the first place) significantly more difficult.
