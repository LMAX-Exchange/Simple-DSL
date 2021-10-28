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

import com.lmax.simpledsl.api.OptionalArg;
import com.lmax.simpledsl.api.RepeatingArgGroup;
import com.lmax.simpledsl.api.RequiredArg;
import com.lmax.simpledsl.api.SimpleDslArg;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleDslParamTest
{
    @Test
    public void shouldReturnTheValueAdded()
    {
        final SimpleDslParam<?, ?> param = new TestParam("foo");
        param.addValue("12");
        assertEquals("12", param.getValue());
    }

    @Test
    public void shouldAddMultipleValuesWhenMultipleValuesAreAllowed()
    {
        final SimpleDslParam<?, ?> param = new TestParam("foo").setAllowMultipleValues();
        param.addValue("12");
        param.addValue("34");
    }

    @Test
    public void shouldReturnAllTheValuesAddedWhenMultipleValuesAreAllowed()
    {
        final SimpleDslParam<?, ?> param = new TestParam("foo").setAllowMultipleValues();
        param.addValue("12");
        param.addValue("34");
        assertArrayEquals(new String[]{"12", "34"}, param.getValues());
    }

    @Test
    public void shouldRestrictWhichValuesAreAllowed()
    {
        final SimpleDslParam<?, ?> param = new TestParam("foo").setAllowedValues("12", "34");

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> param.addValue("56"));

        assertEquals("foo parameter value '56' must be one of: [12, 34]", exception.getMessage());
    }

    @Test
    public void shouldMatchAllowedValuesCaseInsensitivelyButStillReturnTheValuesWithTheProvidedCase()
    {
        final SimpleDslParam<?, ?> param = new TestParam("foo").setAllowedValues("abc", "def").setAllowMultipleValues();
        param.addValue("abc");
        param.addValue("DeF");
        assertArrayEquals(new String[]{"abc", "def"}, param.getValues());
    }

    @Test
    public void shouldThrowExceptionOnSecondCallToAddValueWhenMultipleValuesNotAllowed()
    {
        final SimpleDslParam<?, ?> param = new TestParam("foo");
        param.addValue("12");

        assertThrows(
                IllegalArgumentException.class,
                () -> param.addValue("12"),
                "Multiple foo parameters are not allowed");
    }

    @Test
    public void shouldThrowExceptionOnAccessingASingleValueIfTheParamAllowsMultipleValues()
    {
        final SimpleDslParam<?, ?> param = new TestParam("foo").setAllowMultipleValues();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                param::getValue);

        assertEquals("getValues() should be used when multiple values are allowed", exception.getMessage());
    }

    private static class TestArg extends SimpleDslArg<TestArg>
    {
        TestArg(final String name)
        {
            super(name, false);
        }

        @Override
        public <T> T fold(final Function<RequiredArg, T> ifRequired, final Function<OptionalArg, T> ifOptional, final Function<RepeatingArgGroup, T> ifGroup)
        {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    private static class TestParam extends SimpleDslParam<TestArg, TestParam>
    {
        TestParam(final String name)
        {
            super(new TestArg(name));
        }
    }
}