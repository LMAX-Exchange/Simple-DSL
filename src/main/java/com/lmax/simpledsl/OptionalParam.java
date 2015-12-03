/*
 * Copyright 2011 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lmax.simpledsl;

/**
 * An optional parameter. Callers may omit this parameter and the call is still considered valid. When provided, the parameter name must be specified, e.g. {@code "firstName: Joan"}.
 * Optional parameters can have a default value which is used when no other value is provided.
 *
 * <p>By default, only a single value is allowed. Multiple values can be allowed by calling {@link #setAllowMultipleValues()}</p>
 *
 * <p>Optional parameters are useful as a way of reducing boilerplate in tests by providing default settings and as a way to optionally assert behaviours or values. For example providing
 * a method that checks the first name and/or the last name:</p>
 *
 * <pre>{@code
 *   DslParams params = new DslParams(args,
 *                                    new OptionalParam("firstName"),
 *                                    new OptionalParam("lastName"));
 *   params.valueAsOptional("firstName").ifPresent(driver::checkFirstName);
 *   params.valueAsOptional("lastName").ifPresent(driver::checkLastName);
 * }</pre>
 *
 * <p>Selective assertions like this help tests be flexible to changes outside the area they intend to test by only asserting on the values that are actually relevant.</p>
 *
 * @see DslValues#valueAsOptional(String)
 */
public class OptionalParam extends SimpleDslParam<OptionalParam>
{
    private String defaultValue;

    /**
     * Create a new optional param.
     *
     * @param name the name of the parameter.
     */
    public OptionalParam(final String name)
    {
        super(name);
    }

    /**
     * Set a default value for this parameter.
     * If a default is provided, the parameter will always be considered to have a value and will return the default if no other value is provided by the caller.
     *
     * @param defaultValue the default value for the parameter.
     * @return this parameter
     */
    public OptionalParam setDefault(final String defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public String getDefaultValue()
    {
        return defaultValue;
    }

    @Override
    public String[] getValues()
    {
        final String[] values = super.getValues();
        return values.length > 0 ? values : defaultValue != null ? new String[]{defaultValue} : new String[0];
    }
}
