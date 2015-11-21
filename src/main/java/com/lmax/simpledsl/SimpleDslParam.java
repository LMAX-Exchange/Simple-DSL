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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SimpleDslParam extends DslParam
{
    private final List<String> values = new LinkedList<String>();
    private static final String DEFAULT_DELIMITER = ",";
    private String[] allowedValues;
    protected final String name;
    protected boolean allowMultipleValues;
    protected String multipleValueSeparator;
    private Consumer<String> consumer;
    private boolean calledConsumer;

    public SimpleDslParam(final String name)
    {
        this.name = name;
    }

    @Override
    public SimpleDslParam getAsSimpleDslParam()
    {
        return this;
    }

    @Override
    public RepeatingParamGroup asRepeatingParamGroup()
    {
        return null;
    }

    public SimpleDslParam setAllowedValues(final String... allowedValues)
    {
        this.allowedValues = allowedValues;
        return this;
    }

    public SimpleDslParam setAllowMultipleValues()
    {
        return setAllowMultipleValues(DEFAULT_DELIMITER);
    }

    public SimpleDslParam setAllowMultipleValues(final String delimiter)
    {
        allowMultipleValues = true;
        multipleValueSeparator = delimiter;
        return this;
    }

    public SimpleDslParam setDefault(final String value)
    {
        throw new UnsupportedOperationException("Cannot set default values for this param");
    }

    public SimpleDslParam setConsumer(Consumer<String> consumer)
    {
        this.consumer = consumer;
        return this;
    }

    @Override
    public int consume(final int currentPosition, final NameValuePair... args)
    {
        addValue(args[currentPosition].getValue());
        return currentPosition + 1;
    }

    @Override
    public boolean hasValue()
    {
        return allowMultipleValues ? getValues().length > 0 : getValue() != null;
    }

    public String getDefaultValue()
    {
        return null;
    }

    public void addValue(final String value)
    {
        if (allowMultipleValues)
        {
            final String[] values = value.split(multipleValueSeparator);
            for (final String singleValue : values) {
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
        callConsumer(value);
    }

    String checkValidValue(final String value)
    {
        if (allowedValues != null)
        {
            for (final String allowedValue : allowedValues)
            {
                if (allowedValue.equalsIgnoreCase(value))
                {
                    return allowedValue;
                }
            }
            throw new IllegalArgumentException(name + " parameter value '" + value + "' must be one of: " + Arrays.toString(allowedValues));
        }
        return value;
    }

    private void checkCanAddValue()
    {
        if (!allowMultipleValues && values.size() == 1)
        {
            throw new IllegalArgumentException("Multiple " + name + " parameters are not allowed");
        }
    }

    public String getValue()
    {
        if (allowMultipleValues)
        {
            throw new IllegalArgumentException("getValues() should be used when multiple values are allowed");
        }
        final String[] strings = getValues();
        return strings.length > 0 ? strings[0] : null;
    }

    public String[] getValues()
    {
        return values.toArray(new String[values.size()]);
    }

    public void completedParsing()
    {
        if (!calledConsumer && getDefaultValue() != null)
        {
            callConsumer(getDefaultValue());
        }
    }

    private void callConsumer(final String value)
    {
        if (consumer != null)
        {
            consumer.accept(value);
            calledConsumer = true;
        }
    }

    @Override
    public String getName()
    {
        return name;
    }
}
