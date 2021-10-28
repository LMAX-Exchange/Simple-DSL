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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequiredParamTest
{
    @Test
    public void shouldReportAsRequired()
    {
        final DslParam param = new RequiredParam("foo");
        assertTrue(param.getArg().isRequired());
    }

    @Test
    public void shouldAssignProvidedValue()
    {
        final RequiredParam param = new RequiredParam("foo");
        final int position = param.consume(1, new NameValuePair("goo = 0"), new NameValuePair("foo = 1"), new NameValuePair("bar = 2"));
        assertEquals(2, position);
        assertEquals("1", param.getValue());
    }

    @Test
    public void shouldAssignProvidedValueIgnoringCase()
    {
        final RequiredParam param = new RequiredParam("foo");
        final int position = param.consume(1, new NameValuePair("goo = 0"), new NameValuePair("FOO = 1"), new NameValuePair("bar = 2"));
        assertEquals(2, position);
        assertEquals("1", param.getValue());
    }

    @Test
    public void shouldAssignProvidedValueWhenUnnamed()
    {
        final RequiredParam param = new RequiredParam("foo");
        final int position = param.consume(1, new NameValuePair("0"), new NameValuePair("1"), new NameValuePair("2"));
        assertEquals(2, position);
        assertEquals("1", param.getValue());
    }

    @Test
    public void shouldAssignProvidedValueAtTheHeadOfTheList()
    {
        final RequiredParam param = new RequiredParam("goo");
        final int position = param.consume(0, new NameValuePair("goo = 0"), new NameValuePair("bar = 1"), new NameValuePair("foo = 2"));
        assertEquals(1, position);
        assertEquals("0", param.getValue());
    }

    @Test
    public void shouldAssignProvidedValueAtTheTailOfTheList()
    {
        final RequiredParam param = new RequiredParam("foo");
        final int position = param.consume(2, new NameValuePair("goo = 0"), new NameValuePair("bar = 1"), new NameValuePair("foo = 2"));
        assertEquals(3, position);
        assertEquals("2", param.getValue());
    }

    @Test
    public void shouldConsumeMultipleParamsUpToTheFirstParamNamedDifferently()
    {
        final RequiredParam param = new RequiredParam("foo").setAllowMultipleValues();
        final int position = param.consume(1, new NameValuePair("first param"), new NameValuePair("foo = 1"), new NameValuePair("2"), new NameValuePair("foo = 3"), new NameValuePair("4"),
                new NameValuePair("something else = 5"));
        assertEquals(5, position);
        assertArrayEquals(new String[]{"1", "2", "3", "4"}, param.getValues());
    }

    @Test
    public void shouldConsumeMultipleParamsBySplittingValuesByCommaDelimiter()
    {
        final RequiredParam param = new RequiredParam("foo").setAllowMultipleValues();
        final int position = param.consume(1, new NameValuePair("first param"), new NameValuePair("foo = 1, 2"), new NameValuePair("foo = 3"), new NameValuePair("4"),
                new NameValuePair("something else = 5"));
        assertEquals(4, position);
        assertArrayEquals(new String[]{"1", "2", "3", "4"}, param.getValues());
    }

    @Test
    public void shouldConsumeMultipleParamsBySplittingValuesByUserSuppliedDelimiter()
    {
        final RequiredParam param = new RequiredParam("foo").setAllowMultipleValues("\\|");
        final int position = param.consume(1, new NameValuePair("first param"), new NameValuePair("foo = 1| 2"), new NameValuePair("foo = 3"), new NameValuePair("4"),
                new NameValuePair("something else = 5"));
        assertEquals(4, position);
        assertArrayEquals(new String[]{"1", "2", "3", "4"}, param.getValues());
    }
}
