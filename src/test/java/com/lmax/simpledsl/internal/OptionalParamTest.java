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

import org.junit.jupiter.api.Test;

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
        assertFalse(param.getArg().isRequired());
    }

    @Test
    public void shouldReturnNullIfNoValueIsProvidedAndNoDefaultValueIsSet()
    {
        final SimpleDslParam<?, ?> param = new OptionalParam("foo");
        assertNull(param.getValue());
        assertArrayEquals(new String[0], param.getValues());
    }

    @Test
    public void shouldUseTheDefaultValueIfNoValueProvided()
    {
        final SimpleDslParam<?, ?> param = new OptionalParam("foo").setDefault("def");
        assertEquals("def", param.getValue());
        assertArrayEquals(new String[]{"def"}, param.getValues());
    }

    @Test
    public void shouldNotOverrideTheProvidedValueWithTheDefaultValue()
    {
        final SimpleDslParam<?, ?> param = new OptionalParam("foo").setDefault("def");
        param.addValue("1");
        assertEquals("1", param.getValue());
        assertArrayEquals(new String[]{"1"}, param.getValues());
    }
}
