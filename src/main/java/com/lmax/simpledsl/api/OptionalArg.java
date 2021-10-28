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

package com.lmax.simpledsl.api;

import java.util.function.Function;

/**
 * An optional argument.
 * <p>
 * Callers may omit this argument and the call is still considered valid.
 * When provided, the argument name must be specified, e.g. {@code "firstName: Joan"}.
 * Optional argument can have a default value that is used when no other value is provided.
 * <p>
 * By default, only a single value is allowed.
 * Multiple values can be allowed by calling {@link #setAllowMultipleValues()}.
 * <p>
 * Optional arguments are useful as a way of reducing boilerplate in tests by providing default settings and as a way to
 * optionally assert behaviours or values.
 * For example providing a method that checks the first name and/or the last name:
 *
 * <pre>{@code
 *   DslParams params = new DslParams(args,
 *                                    new OptionalParam("firstName"),
 *                                    new OptionalParam("lastName"));
 *   params.valueAsOptional("firstName").ifPresent(driver::checkFirstName);
 *   params.valueAsOptional("lastName").ifPresent(driver::checkLastName);
 * }</pre>
 * <p>
 * Selective assertions like this help tests be flexible to changes outside the area they intend to test by only
 * asserting on the values that are actually relevant.
 *
 * @see DslValues#valueAsOptional(String)
 */
public class OptionalArg extends SimpleDslArg<OptionalArg>
{
    private String defaultValue;

    public OptionalArg(final String name)
    {
        super(name, false);
    }

    /**
     * Get a default value for this argument.
     *
     * @return the default value for the argument
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Set a default value for this argument.
     * <p>
     * If a default is provided, the argument will be considered to always have a value, and will return the default if
     * no other value is provided by the caller.
     *
     * @param defaultValue the default value for the argument.
     * @return this argument
     */
    public OptionalArg setDefault(final String defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public <T> T fold(final Function<RequiredArg, T> ifRequired, final Function<OptionalArg, T> ifOptional, final Function<RepeatingArgGroup, T> ifGroup)
    {
        return ifOptional.apply(this);
    }
}
