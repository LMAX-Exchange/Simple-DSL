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

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleDslParamTest
{
    @Test
    public void shouldReturnTheValueAdded()
    {
        final SimpleDslParam<?> param = new TestParam("foo");
        param.addValue("12");
        assertEquals("12", param.getValue());
    }

    @Test
    public void shouldAddMultipleValuesWhenMultipleValuesAreAllowed()
    {
        final SimpleDslParam<?> param = new TestParam("foo").setAllowMultipleValues();
        param.addValue("12");
        param.addValue("34");
    }

    @Test
    public void shouldReturnAllTheValuesAddedWhenMultipleValuesAreAllowed()
    {
        final SimpleDslParam<?> param = new TestParam("foo").setAllowMultipleValues();
        param.addValue("12");
        param.addValue("34");
        assertArrayEquals(new String[]{"12", "34"}, param.getValues());
    }

    @Test
    public void shouldRestrictWhichValuesAreAllowed()
    {
        final SimpleDslParam<?> param = new TestParam("foo").setAllowedValues("12", "34");

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> param.addValue("56"));

        assertEquals("foo parameter value '56' must be one of: [12, 34]", exception.getMessage());
    }

    @Test
    public void shouldMatchAllowedValuesCaseInsensitivelyButStillReturnTheValuesWithTheProvidedCase()
    {
        final SimpleDslParam<?> param = new TestParam("foo").setAllowedValues("abc", "def").setAllowMultipleValues();
        param.addValue("abc");
        param.addValue("DeF");
        assertArrayEquals(new String[]{"abc", "def"}, param.getValues());
    }

    @Test
    public void shouldCallConsumerWhenSingleValueProvided()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam<?> param = new TestParam("foo").setConsumer(consumer);
        param.addValue("abc");

        assertEquals(1, list.size());
        assertEquals("abc", list.get(0));
    }

    @Test
    public void shouldCallConsumerInCorrectOrderWhenMultipleValuesProvided()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam<?> param = new TestParam("foo").setAllowMultipleValues().setConsumer(consumer);
        param.addValue("abc");
        param.addValue("def");
        param.addValue("ghi");

        assertEquals(3, list.size());
        assertEquals("abc", list.get(0));
        assertEquals("def", list.get(1));
        assertEquals("ghi", list.get(2));
    }

    @Test
    public void shouldCallBiConsumerWithTheCorrectParameterName()
    {
        final LinkedList<String> list = new LinkedList<>();
        final BiConsumer<String, String> consumer = (name, value) -> list.add(name);
        final SimpleDslParam<?> param1 = new TestParam("foo").setAllowMultipleValues().setConsumer(consumer);
        final SimpleDslParam<?> param2 = new TestParam("bar").setAllowMultipleValues().setConsumer(consumer);
        param1.addValue("abc");
        param1.addValue("def");
        param2.addValue("ghi");

        assertEquals(3, list.size());
        assertEquals("foo", list.get(0));
        assertEquals("foo", list.get(1));
        assertEquals("bar", list.get(2));
    }

    @Test
    public void shouldThrowExceptionOnSecondCallToAddValueWhenMultipleValuesNotAllowed()
    {
        final SimpleDslParam<?> param = new TestParam("foo");
        param.addValue("12");

        assertThrows(
                IllegalArgumentException.class,
                () -> param.addValue("12"),
                "Multiple foo parameters are not allowed");
    }

    @Test
    public void shouldThrowExceptionOnAccessingASingleValueIfTheParamAllowsMultipleValues()
    {
        final SimpleDslParam<?> param = new TestParam("foo").setAllowMultipleValues();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                param::getValue);

        assertEquals("getValues() should be used when multiple values are allowed", exception.getMessage());
    }

    private static class TestParam extends SimpleDslParam<TestParam>
    {
        TestParam(final String name)
        {
            super(name);
        }
    }
}
