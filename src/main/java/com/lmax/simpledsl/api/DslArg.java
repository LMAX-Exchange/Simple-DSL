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
 * The base class for all arg types.
 */
public interface DslArg
{
    /**
     * Get the name of this argument.
     *
     * @return the argument name.
     */
    String getName();

    /**
     * Determine if a value is required for this argument.
     *
     * @return {@literal true} if and only if this argument is required.
     */
    boolean isRequired();

    /**
     * Get a default value for this argument.
     *
     * If the argument is required, this method will throw an {@link IllegalArgumentException}.
     *
     * @return the default value for the argument
     */
    String getDefaultValue();

    /**
     * Check whether this argument can take multiple values.
     *
     * @return {@literal true} if and only if the argument takes multiple values.
     */
    boolean isAllowMultipleValues();

    /**
     * Get the separator that can be used to separate multiple values.
     *
     * @return the separator for splitting multiple values.
     */
    String getMultipleValueSeparator();

    /**
     * Get the specific values that this argument will accept.
     *
     * @return the values allowed by this argument, or {@literal null} if all values are allowed
     */
    String[] getAllowedValues();
}
