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

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DslParamsTest
{
    @Test
    public void shouldReturnValueAsInt()
    {
        DslParams params = new DslParams(new String[]{"a=1"}, new RequiredParam("a"));
        Assert.assertEquals(1, params.valueAsInt("a"));
    }

    @Test
    public void shouldReturnValueAsLong()
    {
        DslParams params = new DslParams(new String[]{"a=1"}, new RequiredParam("a"));
        Assert.assertEquals(1L, params.valueAsLong("a"));
    }

    @Test
    public void shouldReturnValueAsBoolean()
    {
        DslParams params = new DslParams(new String[]{"a=true"}, new RequiredParam("a"));
        Assert.assertTrue(params.valueAsBoolean("a"));
    }

    @Test
    public void shouldReturnValueAsBigDecimal()
    {
        DslParams params = new DslParams(new String[]{"a=1"}, new RequiredParam("a"));
        Assert.assertEquals(0, BigDecimal.ONE.compareTo(params.valueAsBigDecimal("a")));
    }

    @Test
    public void shouldReturnValueAsDouble()
    {
        DslParams params = new DslParams(new String[]{"a=1.23"}, new RequiredParam("a"));
        Assert.assertEquals(1.23d, params.valueAsDouble("a"), 0d);
    }

    @Test
    public void shouldReturnNullValueWhenBigDecimalIsNotFound()
    {
        DslParams params = new DslParams(new String[]{}, new OptionalParam("a"));
        Assert.assertNull(params.valueAsBigDecimal("a"));
    }

    @Test
    public void shouldReturnValueAsParamForOptionalValueThatWasSpecified() throws Exception
    {
        DslParams params = new DslParams(new String[]{"a: value"}, new OptionalParam("a"));
        Assert.assertEquals("a: value", params.valueAsParam("a"));
    }

    @Test
    public void shouldReturnNullValueAsParamWhenOptionalParamNotSpecified() throws Exception
    {
        DslParams params = new DslParams(new String[]{}, new OptionalParam("a"));
        Assert.assertNull(params.valueAsParam("a"));
    }

    @Test
    public void shouldReturnValuesAsIntArray() throws Exception
    {
        DslParams params = new DslParams(new String[]{"a: 1, 2, 3"}, new OptionalParam("a").setAllowMultipleValues());
        Assert.assertArrayEquals(new int[]{1, 2, 3}, params.valuesAsInts("a"));
    }

    @Test
    public void shouldReturnValuesAsLongArray() throws Exception
    {
        DslParams params = new DslParams(new String[]{"a: 1, 2, 3"}, new OptionalParam("a").setAllowMultipleValues());
        Assert.assertArrayEquals(new long[]{1, 2, 3}, params.valuesAsLongs("a"));
    }

    @Test
    public void shouldReturnValuesAsBigDecimalArray() throws Exception
    {
        DslParams params = new DslParams(new String[]{"a: 1, 2.23, 3"}, new OptionalParam("a").setAllowMultipleValues());
        Assert.assertArrayEquals(new BigDecimal[]{new BigDecimal("1"), new BigDecimal("2.23"), new BigDecimal("3")}, params.valuesAsBigDecimals("a"));
    }

    @Test
    public void shouldReturnValuesAsDoubleArray() throws Exception
    {
        DslParams params = new DslParams(new String[]{"a: 1, 2.23, 3"}, new OptionalParam("a").setAllowMultipleValues());
        assertArrayEquals(new double[]{ 1, 2.23, 3 }, params.valuesAsDoubles("a"));
    }

    @Test
    public void simpleCaseOfExtractingRequiredParams()
    {
        DslParams params = new DslParams(new String[]{"a=1", "b=2"}, new RequiredParam("a"), new RequiredParam("b"));

        Assert.assertEquals("1", params.value("a"));
        Assert.assertEquals("2", params.value("b"));
    }

    @Test
    public void simpleCaseOfExtractingOptionalParams()
    {
        DslParams params = new DslParams(new String[]{"a=1", "b=2"}, new OptionalParam("a"), new OptionalParam("b"));

        Assert.assertEquals("1", params.value("a"));
        Assert.assertEquals("2", params.value("b"));
    }

    @Test
    public void simpleCaseOfExtractingRequiredAndOptionalParams()
    {
        DslParams params = new DslParams(new String[]{"a=1", "b=2"}, new RequiredParam("a"), new OptionalParam("b"));

        Assert.assertEquals("1", params.value("a"));
        Assert.assertEquals("2", params.value("b"));
    }

    @Test
    public void simpleCaseOfTestingForThePresenceOfAParam()
    {
        DslParams params = new DslParams(new String[]{"a=1", "b=2"}, new RequiredParam("a"), new OptionalParam("b"));

        Assert.assertTrue(params.hasValue("b"));
    }

    @Test
    public void simpleCaseOfTestingForTheAbsenceOfAParam()
    {
        DslParams params = new DslParams(new String[]{"a=1"}, new RequiredParam("a"), new OptionalParam("b"));

        Assert.assertFalse(params.hasValue("b"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void aMissingRequiredParamThrowsAnException()
    {
        new DslParams(new String[]{"a=1"}, new RequiredParam("a"), new RequiredParam("b"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void passingInAnUnexpectedParamThrowsAnException()
    {
        new DslParams(new String[]{"a=1", "b=2"}, new RequiredParam("a"));
    }

    @Test
    public void youCanExtractMultipleRequiredParamsWhenAllParamsAreNamed()
    {
        DslParams params = new DslParams(new String[]{"a=1", "b=2", "b=3", "c=4"},
                                         new RequiredParam("a"),
                                         new RequiredParam("b").setAllowMultipleValues(),
                                         new RequiredParam("c"));

        Assert.assertEquals("1", params.value("a"));
        Assert.assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        Assert.assertEquals("4", params.value("c"));
    }

    @Test
    public void youCanExtractMultipleRequiredParamsWhenSubsequentRequiredParamsAreNotNamed()
    {
        DslParams params = new DslParams(new String[]{"a=1", "b=2", "3", "c=4"},
                                         new RequiredParam("a"),
                                         new RequiredParam("b").setAllowMultipleValues(),
                                         new RequiredParam("c"));

        Assert.assertEquals("1", params.value("a"));
        Assert.assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        Assert.assertEquals("4", params.value("c"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void multipleRequiredParamsMustBePassedInTheCorrectOrder()
    {
        new DslParams(new String[]{"b=1", "a=2"}, new RequiredParam("a"), new RequiredParam("b"));
    }

    @Test
    public void hasValueDetectsPresenceOfParamsWithMultipleValuesAllowed() throws Exception
    {
        DslParams params = new DslParams(new String[]{"a=1", "a=2"}, new OptionalParam("a").setAllowMultipleValues());
        Assert.assertTrue(params.hasValue("a"));
    }

    @Test
    public void hasValueDetectsAbsenceOfParamsWithMultipleValuesAllowed() throws Exception
    {
        DslParams params = new DslParams(new String[0], new OptionalParam("a").setAllowMultipleValues());
        Assert.assertFalse(params.hasValue("a"));
    }

    @Test
    public void youCanExtractMultipleRequiredParamsAndMultipleOptionalParamsWithOptionalParamsInRandomOrder()
    {
        DslParams params = new DslParams(new String[]{"a=1", "b=2", "b=3", "c=4", "d=5", "c=6"},
                                         new RequiredParam("a"),
                                         new RequiredParam("b").setAllowMultipleValues(),
                                         new OptionalParam("c").setAllowMultipleValues(),
                                         new OptionalParam("d"));

        Assert.assertEquals("1", params.value("a"));
        Assert.assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        Assert.assertArrayEquals(new String[]{"4", "6"}, params.values("c"));
        Assert.assertEquals("5", params.value("d"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiredParamsMustAppearBeforeOptionalParams()
    {
        new DslParams(new String[]{"a=1", "b=2"}, new OptionalParam("b"), new RequiredParam("a"));
    }

    @Test
    public void shouldIgnoreNullArgumentsAmongstOptionalParameters() throws Exception {
        final DslParams params = new DslParams(new String[]{null, "a=1", null, "b=2", null}, new OptionalParam("a"), new OptionalParam("b"));
        Assert.assertEquals("1", params.value("a"));
        Assert.assertEquals("2", params.value("b"));
    }

    @Test
    public void callingADslMethodWithUsageArgThrowsAnExceptionContainingTheDslParamsForIntrospection()
    {
        DslParam[] params = {new RequiredParam("a"), new OptionalParam("b")};
        try
        {
            new DslParams(new String[]{"-usage"}, params);
            Assert.fail("Should have thrown a DslParamsUsageException");
        }
        catch (DslParamsUsageException e)
        {
            Assert.assertSame(params, e.getParams());
        }
    }

    @Test
    public void shouldReplacePlaceholderValueWithOverride() throws Exception
    {
        PropertyBasedValueReplacer valueReplacer = new PropertyBasedValueReplacer();
        valueReplacer.addReplacement("${valueToReplace}", "newValue");
        DslParams.setValueReplacer(valueReplacer);

        DslParams params = new DslParams(new String[]{"a: ${valueToReplace}"}, new RequiredParam("a"));
        Assert.assertEquals("newValue", params.value("a"));
    }

    private void assertArrayEquals(final double[] expected, final double[] actual)
    {
        Assert.assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++)
        {
            Assert.assertEquals(expected[i], actual[i], 0d);
        }
    }

    private static class PropertyBasedValueReplacer implements ValueReplacer
    {
        private final Map<String, String> replacements = new HashMap<String, String>();

        public void addReplacement(String oldValue, String newValue)
        {
            replacements.put(oldValue, newValue);
        }

        public String replace(String value)
        {
            final String newValue = replacements.get(value);
            return newValue != null ? newValue : value;
        }
    }
}
