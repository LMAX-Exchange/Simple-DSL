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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The root type for all simple params.
 *
 * @param <P> the actual type of simple parameter.
 */
public abstract class SimpleDslParam<P extends SimpleDslParam<P>> extends DslParam
{
    private final List<String> values = new LinkedList<>();
    private static final String DEFAULT_DELIMITER = ",";
    private String[] allowedValues;
    protected final String name;
    protected boolean allowMultipleValues;
    protected String multipleValueSeparator;
    private BiConsumer<String, String> consumer;
    private boolean calledConsumer;

    /**
     * Create the parameter.
     *
     * @param name the parameter name.
     */
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

    /**
     * Restrict the allowed values for this parameter to the specified set. Specifying a value outside of this set will result in an exception being thrown when parsing the arguments.
     *
     * @param allowedValues the allowable values for this parameter.
     * @return this parameter
     */
    public P setAllowedValues(final String... allowedValues)
    {
        this.allowedValues = allowedValues;
        return (P) this;
    }

    /**
     * Allow multiple values to be specified for this parameter, either as separate arguments or using comma (,) as a delimiter.
     *
     * The following calls are equivalent:
     * <pre>{@code
     * verifyUsersPresent("user: joan", "user: jenny", "user: joanne");
     * verifyUsersPresent("user: joan, jenny, joanne");
     * }</pre>
     *
     * @see #setAllowMultipleValues(String)
     * @return this parameter
     */
    public P setAllowMultipleValues()
    {
        return setAllowMultipleValues(DEFAULT_DELIMITER);
    }

    /**
     * Allow multiple values to be specified for this parameter, either as separate arguments or using the specified string as a delimiter.
     *
     * @see #setAllowMultipleValues()
     * @return this parameter
     */
    public P setAllowMultipleValues(final String delimiter)
    {
        allowMultipleValues = true;
        multipleValueSeparator = delimiter;
        return (P) this;
    }

    /**
     * Set a consumer which is called for every value supplied to this parameter. If multiple values are allowed, the consumer will be called once for each supplied value.
     * For an {@link OptionalParam} with a default value set, the consumer will be called with the default value if no other value is supplied.
     *
     * @see #setConsumer(BiConsumer)
     * @param consumer the consumer provide values to.
     * @return this parameter.
     */
    public P setConsumer(Consumer<String> consumer)
    {
        this.consumer = (name, value) -> consumer.accept(value);
        return (P) this;
    }


    /**
     * Set a consumer which is called with the name and value for every value supplied to this parameter. If multiple values are allowed, the consumer will be called once for each supplied value.
     * For an {@link OptionalParam} with a default value set, the consumer will be called with the default value if no other value is supplied.
     *
     * <p>This differs from {@link #setConsumer(Consumer)} by supplying the name and value to the consumer. The name is supplied as the first argument and the value as the second.</p>
     *
     * @see #setConsumer(Consumer)
     * @param consumer the consumer provide values to.
     * @return this parameter.
     */
    public P setConsumer(BiConsumer<String, String> consumer)
    {
        this.consumer = consumer;
        return (P) this;
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

    /**
     * Used when parsing values. Not intended to be part of the public API and will be removed in a future release.
     *
     * @param value the value to add to this parameter.
     */
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

    /**
     * Get the value for this parameter. If multiple values are allowed, use {@link #getValues()} instead.
     *
     * @return the value for this parameter or {@code null} if the parameter has no value.
     * @throws IllegalArgumentException if multiple values are allowed.
     */
    public String getValue()
    {
        if (allowMultipleValues)
        {
            throw new IllegalArgumentException("getValues() should be used when multiple values are allowed");
        }
        final String[] strings = getValues();
        return strings.length > 0 ? strings[0] : null;
    }

    /**
     * Retrieve the values supplied for this parameter as an array. Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @return an array of values supplied for the parameter.
     */
    public String[] getValues()
    {
        return values.toArray(new String[values.size()]);
    }

    void completedParsing()
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
            consumer.accept(getName(), value);
            calledConsumer = true;
        }
    }

    @Override
    public String getName()
    {
        return name;
    }
}
