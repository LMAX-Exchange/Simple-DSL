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
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OptionalParamTest
{
    @Test
    public void shouldNotReportAsRequired()
    {
        final DslParam param = new OptionalParam("foo");
        assertFalse(param.isRequired());
    }

    @Test
    public void shouldReturnNullIfNoValueIsProvidedAndNoDefaultValueIsSet()
    {
        final SimpleDslParam<?> param = new OptionalParam("foo");
        assertNull(param.getValue());
        assertArrayEquals(new String[0], param.getValues());
    }

    @Test
    public void shouldUseTheDefaultValueIfNoValueProvided()
    {
        final SimpleDslParam<?> param = new OptionalParam("foo").setDefault("def");
        assertEquals("def", param.getValue());
        assertArrayEquals(new String[]{"def"}, param.getValues());
    }

    @Test
    public void shouldNotOverrideTheProvidedValueWithTheDefaultValue()
    {
        final SimpleDslParam<?> param = new OptionalParam("foo").setDefault("def");
        param.addValue("1");
        assertEquals("1", param.getValue());
        assertArrayEquals(new String[]{"1"}, param.getValues());
    }

    @Test
    public void shouldCallConsumeWhenAParameterValueIsProvided()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam<?> param = new OptionalParam("foo").setConsumer(consumer);
        param.addValue("abc");

        assertEquals(1, list.size());
        assertEquals("abc", list.get(0));
    }

    @Test
    public void shouldCallConsumeWithTheDefaultValueIfNoParameterValueIsProvided()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam<?> param = new OptionalParam("foo").setDefault("abc").setConsumer(consumer);
        param.completedParsing();

        assertEquals(1, list.size());
        assertEquals("abc", list.get(0));
    }

    @Test
    public void shouldNotCallConsumeIfNoParameterValueIsProvidedAndNoDefaultValueIsSet()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam<?> param = new OptionalParam("foo").setConsumer(consumer);
        param.completedParsing();

        assertEquals(0, list.size());
    }

}
