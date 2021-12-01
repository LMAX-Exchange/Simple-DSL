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
package com.lmax.simpledsl.internal;

import java.util.Collections;
import java.util.List;

class SimpleDslParam extends DslParam
{
    private final String name;
    private final List<String> values;

    SimpleDslParam(final String name, final List<String> values)
    {
        this.name = name;
        this.values = values;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    SimpleDslParam getAsSimpleDslParam()
    {
        return this;
    }

    @Override
    RepeatingParamGroup asRepeatingParamGroup()
    {
        throw new IllegalArgumentException(name + " is not a repeating group");
    }

    @Override
    boolean hasValue()
    {
        return !values.isEmpty();
    }

    @Override
    String[] rawArgs()
    {
        return values.stream().map(val -> name + ": " + val).toArray(String[]::new);
    }

    /**
     * Get the value for this parameter. If multiple values are allowed, use {@link #getValues()} instead.
     *
     * @return the value for this parameter or {@code null} if the parameter has no value.
     * @throws IllegalArgumentException if multiple values are allowed.
     */
    public String getValue()
    {
        if (values.size() > 1)
        {
            throw new IllegalArgumentException("getValues() should be used when multiple values are allowed");
        }
        final String[] strings = getValues();
        return strings.length > 0 ? strings[0] : null;
    }

    List<String> getValuesAsList()
    {
        return Collections.unmodifiableList(values);
    }

    String[] getValues()
    {
        return getValuesAsList().toArray(new String[0]);
    }
}
