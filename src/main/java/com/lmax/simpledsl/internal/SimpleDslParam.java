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

import com.lmax.simpledsl.api.SimpleDslArg;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

abstract class SimpleDslParam<A extends SimpleDslArg<A>, P extends SimpleDslParam<A, P>> extends DslParam
{
    private final A arg;
    private final List<String> values = new LinkedList<>();

    SimpleDslParam(final A arg)
    {
        this.arg = arg;
    }

    @Override
    public A getArg()
    {
        return arg;
    }

    P setAllowedValues(final String... allowedValues)
    {
        arg.setAllowedValues(allowedValues);
        return me();
    }

    P setAllowMultipleValues()
    {
        arg.setAllowMultipleValues();
        return me();
    }

    P setAllowMultipleValues(final String delimiter)
    {
        arg.setAllowMultipleValues(delimiter);
        return me();
    }

    @Override
    SimpleDslParam<?, ?> getAsSimpleDslParam()
    {
        return this;
    }

    @Override
    RepeatingParamGroup asRepeatingParamGroup()
    {
        return null;
    }

    @Override
    int consume(final int currentPosition, final NameValuePair... args)
    {
        final NameValuePair arg = args[currentPosition];
        addValue(null == arg ? null : arg.getValue());
        return currentPosition + 1;
    }

    @Override
    boolean hasValue()
    {
        return arg.isAllowMultipleValues() ? getValues().length > 0 : getValue() != null;
    }

    String getDefaultValue()
    {
        return null;
    }

    void addValue(final String value)
    {
        if (arg.isAllowMultipleValues())
        {
            final String[] values = value.split(arg.getMultipleValueSeparator());
            for (final String singleValue : values)
            {
                addSingleValue(singleValue.trim());
            }
        }
        else
        {
            addSingleValue(value);
        }
    }

    private void addSingleValue(final String value)
    {
        checkCanAddValue();
        values.add(checkValidValue(value));
    }

    String checkValidValue(final String value)
    {
        if (arg.getAllowedValues() != null)
        {
            for (final String allowedValue : arg.getAllowedValues())
            {
                if (allowedValue.equalsIgnoreCase(value))
                {
                    return allowedValue;
                }
            }
            throw new IllegalArgumentException(arg.getName() + " parameter value '" + value + "' must be one of: " + Arrays.toString(arg.getAllowedValues()));
        }
        return value;
    }

    private void checkCanAddValue()
    {
        if (!arg.isAllowMultipleValues() && values.size() == 1)
        {
            throw new IllegalArgumentException("Multiple " + arg.getName() + " parameters are not allowed");
        }
    }

    String getValue()
    {
        if (arg.isAllowMultipleValues())
        {
            throw new IllegalArgumentException("getValues() should be used when multiple values are allowed");
        }
        final String[] strings = getValues();
        return strings.length > 0 ? strings[0] : null;
    }

    String[] getValues()
    {
        return values.toArray(new String[0]);
    }

    @SuppressWarnings("unchecked")
    protected P me()
    {
        return (P) this;
    }
}
