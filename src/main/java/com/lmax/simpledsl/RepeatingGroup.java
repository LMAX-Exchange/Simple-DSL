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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides access to the values for a single instance of a {@link RepeatingParamGroup}. Apart from not supporting nested {@code RepeatingParamGroup}s, all the value accessor methods from
 * {@link DslParams} are available.
 *
 * @see RepeatingParamGroup
 * @see DslParams#valuesAsGroup(String)
 */
public class RepeatingGroup extends DslValues
{
    private final Map<String, List<String>> valuesByName = new HashMap<>();

    /**
     * Used as part of argument parsing. Not intended to be part of the public API and will be removed in a future release.
     *
     * @param name  the name of the parameter
     * @param value the value to add.
     * @deprecated Not intended to be part of the public API and will be removed in a future release.
     */
    public void addValue(final String name, final String value)
    {
        List<String> values = getValues(name);
        if (values == null)
        {
            values = new ArrayList<>();
            valuesByName.put(name.toLowerCase(), values);
        }
        values.add(value);
    }

    @Override
    public boolean hasValue(final String name)
    {
        return valuesByName.containsKey(name.toLowerCase());
    }

    @Override
    public String value(final String name)
    {
        final String[] strings = values(name);
        return strings.length > 0 ? strings[0] : null;
    }

    @Override
    public String[] values(final String name)
    {
        final List<String> values = getValues(name);
        return values != null ? values.toArray(new String[values.size()]) : new String[0];
    }

    private List<String> getValues(final String name)
    {
        return name != null ? valuesByName.get(name.toLowerCase()) : null;
    }
}
