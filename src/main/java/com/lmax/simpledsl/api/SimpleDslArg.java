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

/**
 * The root type for all simple args.
 */
public abstract class SimpleDslArg implements DslArg
{
    private static final String DEFAULT_DELIMITER = ",";

    private final String name;
    private final boolean required;

    protected String defaultValue;
    protected boolean allowMultipleValues;
    protected String multipleValueSeparator;
    protected String[] allowedValues;

    public SimpleDslArg(final String name, final boolean required)
    {
        this.name = name;
        this.required = required;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isRequired()
    {
        return required;
    }

    @Override
    public String getDefaultValue()
    {
        if (required)
        {
            throw new IllegalArgumentException("A required argument can not have a default value");
        }

        return defaultValue;
    }

    @Override
    public boolean isAllowMultipleValues()
    {
        return allowMultipleValues;
    }

    @Override
    public String getMultipleValueSeparator()
    {
        return multipleValueSeparator;
    }

    @Override
    public String[] getAllowedValues()
    {
        return allowedValues;
    }

    /**
     * Set a default value for this argument.
     * <p>
     * If a default is provided, the argument will be considered to always have a value, and will return the default if
     * no other value is provided by the caller.
     *
     * @param defaultValue the default value for the argument.
     * @return this argument
     * @throws IllegalArgumentException if the default value cannot be set
     */
    public SimpleDslArg setDefault(final String defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Restrict the allowed values for this argument to the specified set.
     * Specifying a value outside of this set will result in an exception being thrown when parsing the arguments.
     *
     * @param allowedValues the allowable values for this argument.
     * @return this argument
     */
    public SimpleDslArg setAllowedValues(final String... allowedValues)
    {
        this.allowedValues = allowedValues;
        return this;
    }

    /**
     * Allow multiple values to be specified for this argument, either as separate arguments or using comma (,) as a delimiter.
     * <p>
     * The following calls are equivalent:
     * <pre>{@code
     * verifyUsersPresent("user: joan", "user: jenny", "user: joanne");
     * verifyUsersPresent("user: joan, jenny, joanne");
     * }</pre>
     *
     * @return this argument
     * @see #setAllowMultipleValues(String)
     */
    public SimpleDslArg setAllowMultipleValues()
    {
        return setAllowMultipleValues(DEFAULT_DELIMITER);
    }

    /**
     * Allow multiple values to be specified for this argument, either as separate arguments or using the specified string as a delimiter.
     *
     * @param delimiter the delimiter to use to separate values
     * @return this argument
     * @see #setAllowMultipleValues()
     */
    public SimpleDslArg setAllowMultipleValues(final String delimiter)
    {
        allowMultipleValues = true;
        multipleValueSeparator = delimiter;
        return this;
    }
}
