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
 * The base class for all param types.
 */
public abstract class DslParam
{
    /**
     * Return this param as a {@link SimpleDslParam} or {@code null} if the param cannot be viewed as a {@code SimpleDslParam} (e.g. because it's a {@link RepeatingParamGroup}).
     *
     * @return this param as a {@code SimpleDslParam}.
     */
    public abstract SimpleDslParam getAsSimpleDslParam();

    /**
     * Return this param as a {@link RepeatingParamGroup} or {@code null} if the param cannot be viewed as a {@code SimpleDslParam} (e.g. because it's a {@code SimpleDslParam}).
     *
     * @return this param as a {@code RepeatingParamGroup}.
     */
    public abstract RepeatingParamGroup asRepeatingParamGroup();

    /**
     * Used as part of parsing the arguments.
     *
     * @param currentPosition the current argument position in the parse.
     * @param args            the arguments to be consumed.
     * @return the new argument position.
     * @deprecated This is not intended to be part of the public API and will be removed in a future release.
     */
    public abstract int consume(int currentPosition, NameValuePair... args);

    /**
     * Determine if a value is required for this parameter.
     *
     * @return {@code true} if and only if this param is required.
     */
    public boolean isRequired()
    {
        return false;
    }

    /**
     * Determine if a value is optional for this parameter.
     *
     * @return {@code true} if and only if this param is optional.
     */
    public boolean isOptional()
    {
        return false;
    }

    /**
     * Determine a value was supplied for this parameter.
     *
     * @return {@code true} if and only if a value (including a blank value) was supplied for this parameter.
     */
    public abstract boolean hasValue();

    /**
     * Get the name of this parameter.
     *
     * @return the parameter name.
     */
    public abstract String getName();

    /**
     * Used as part of parsing the arguments.
     *
     * @return {@code true} if and only if the value is valid.
     * @deprecated This is not intended to be part of the public API and will be removed in a future release.
     */
    public boolean isValid()
    {
        return true;
    }
}
