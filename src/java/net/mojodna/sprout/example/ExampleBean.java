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
package net.mojodna.sprout.example;

/**
 * Sample bean that could theoretically be managed by Spring and wired to
 * Actions that request it.  This is a rather stupid example of a bean that
 * would realistically be managed by Spring.
 * 
 * @author Seth Fitzsimmons
 */
public class ExampleBean {
    /**
     * Gets a greeting.
     */
    public String getGreeting() {
        return "Hello";
    }
}
