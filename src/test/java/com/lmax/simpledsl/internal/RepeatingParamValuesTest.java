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

import com.lmax.simpledsl.api.DslArg;
import com.lmax.simpledsl.api.OptionalArg;
import com.lmax.simpledsl.api.RequiredArg;
import com.lmax.simpledsl.api.SimpleDslArg;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepeatingParamValuesTest
{
    @Test
    public void shouldReturnIfAParamHasBeenDefined()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final OptionalArg otherArg = new OptionalArg("bar");
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList("abc"));
        values.put("bar", Collections.singletonList("123"));
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        assertTrue(params.hasParam("foo"));
        assertTrue(params.hasParam("bar"));
        assertFalse(params.hasParam("boo"));
        assertFalse(params.hasParam("far"));
    }

    @Test
    public void shouldReturnIfAParamHasBeenDefinedCaseInsensitively()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final OptionalArg otherArg = new OptionalArg("bar");
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList("abc"));
        values.put("bar", Collections.singletonList("123"));
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        assertTrue(params.hasParam("FOO"));
        assertTrue(params.hasParam("BaR"));
    }

    @Test
    public void shouldReturnValue()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final OptionalArg otherArg = new OptionalArg("bar");
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList("abc"));
        values.put("bar", Collections.singletonList("123"));
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        assertEquals("abc", params.value("foo"));
        assertEquals("123", params.value("bar"));
    }

    @Test
    public void shouldReturnMultipleValues()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final OptionalArg otherArg = new OptionalArg("bar");
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList("abc"));
        values.put("bar", asList("123", "456"));
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        assertArrayEquals(new String[]{"123", "456"}, params.values("bar"));
    }

    @Test
    public void shouldReportOptionalValueAsPresentWhenValueProvided()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final OptionalArg otherArg = new OptionalArg("bar");
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList("abc"));
        values.put("bar", Collections.singletonList("123"));
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        assertTrue(params.hasValue("bar"));
    }

    @Test
    public void shouldNotReportOptionalValueAsPresentWhenNoValueProvided()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final OptionalArg otherArg = new OptionalArg("bar");
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList("abc"));
        values.put("bar", emptyList());
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        assertFalse(params.hasValue("bar"));
    }

    @Test
    public void shouldReportRequiredValueAsPresentWhenEmptyValueProvided()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final OptionalArg otherArg = new OptionalArg("bar");
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList(""));
        values.put("bar", Collections.singletonList("123"));
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        assertTrue(params.hasValue("foo"));
    }

    @Test
    public void shouldReportOptionalValueAsPresentWhenEmptyValueProvided()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final OptionalArg otherArg = new OptionalArg("bar");
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList("abc"));
        values.put("bar", Collections.singletonList(""));
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        assertTrue(params.hasValue("bar"));
    }

    @Test
    void shouldThrowWhenParameterDoesNotExist()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final OptionalArg otherArg = new OptionalArg("bar");
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList(""));
        values.put("bar", Collections.singletonList("123"));
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        assertThrows(IllegalArgumentException.class,
                () -> params.value("fake"));
        assertThrows(IllegalArgumentException.class,
                () -> params.values("fake"));
        assertThrows(IllegalArgumentException.class,
                () -> params.hasValue("fake"));
    }

    @Test
    void shouldThrowExceptionWhenAttemptToGetValueButContainsMultiple()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final SimpleDslArg otherArg = new OptionalArg("bar").setAllowMultipleValues();
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList("abc"));
        values.put("bar", asList("123", "456"));
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> params.value("bar")
        );

        assertEquals("values() should be used when multiple values are allowed", exception.getMessage());
    }

    @Test
    public void shouldRetrieveValueCaseWithACaseInsensitiveKey()
    {
        final RequiredArg requiredArg = new RequiredArg("foo");
        final OptionalArg otherArg = new OptionalArg("bar");
        final Map<String, List<String>> values = new HashMap<>();
        values.put("foo", Collections.singletonList(""));
        values.put("bar", Collections.singletonList("123"));
        final RepeatingParamValues params = new RepeatingParamValues(asList(requiredArg, otherArg).toArray(new DslArg[0]), values);

        assertTrue(params.hasValue("Bar"));
        assertEquals("123", params.value("BAR"));
    }
}