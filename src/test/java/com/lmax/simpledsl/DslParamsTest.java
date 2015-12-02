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
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DslParamsTest
{
    @Test
    public void shouldReturnValueAsInt()
    {
        final DslParams params = new DslParams(new String[]{"a=1"}, new RequiredParam("a"));
        assertEquals(1, params.valueAsInt("a"));
    }

    @Test(expected = NumberFormatException.class)
    public void shouldThrowNumberFormatExceptionWhenValueAsIntCalledForParameterThatIsNotSupplied() throws Exception
    {
        final DslParams params = new DslParams(new String[0], new OptionalParam("a"));
        assertEquals(1, params.valueAsInt("a"));
    }

    @Test
    public void shouldReturnValueAsLong()
    {
        final DslParams params = new DslParams(new String[]{"a=1"}, new RequiredParam("a"));
        assertEquals(1L, params.valueAsLong("a"));
    }

    @Test
    public void shouldReturnValueAsBoolean()
    {
        final DslParams params = new DslParams(new String[]{"a=true"}, new RequiredParam("a"));
        assertTrue(params.valueAsBoolean("a"));
    }

    @Test
    public void shouldReturnValueAsBigDecimal()
    {
        final DslParams params = new DslParams(new String[]{"a=1"}, new RequiredParam("a"));
        assertEquals(0, BigDecimal.ONE.compareTo(params.valueAsBigDecimal("a")));
    }

    @Test
    public void shouldReturnValueAsDouble()
    {
        final DslParams params = new DslParams(new String[]{"a=1.23"}, new RequiredParam("a"));
        assertEquals(1.23d, params.valueAsDouble("a"), 0d);
    }

    @Test
    public void shouldReturnNullValueWhenBigDecimalIsNotFound()
    {
        final DslParams params = new DslParams(new String[]{}, new OptionalParam("a"));
        assertNull(params.valueAsBigDecimal("a"));
    }

    @Test
    public void shouldReturnValueAsParamForOptionalValueThatWasSpecified() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a: value"}, new OptionalParam("a"));
        assertEquals("a: value", params.valueAsParam("a"));
    }

    @Test
    public void shouldReturnNullValueAsParamWhenOptionalParamNotSpecified() throws Exception
    {
        final DslParams params = new DslParams(new String[]{}, new OptionalParam("a"));
        assertNull(params.valueAsParam("a"));
    }

    @Test
    public void shouldReturnValuesAsIntArray() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a: 1, 2, 3"}, new OptionalParam("a").setAllowMultipleValues());
        assertArrayEquals(new int[]{1, 2, 3}, params.valuesAsInts("a"));
    }

    @Test
    public void shouldReturnValuesAsLongArray() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a: 1, 2, 3"}, new OptionalParam("a").setAllowMultipleValues());
        assertArrayEquals(new long[]{1, 2, 3}, params.valuesAsLongs("a"));
    }

    @Test
    public void shouldReturnValuesAsBigDecimalArray() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a: 1, 2.23, 3"}, new OptionalParam("a").setAllowMultipleValues());
        assertArrayEquals(new BigDecimal[]{new BigDecimal("1"), new BigDecimal("2.23"), new BigDecimal("3")}, params.valuesAsBigDecimals("a"));
    }

    @Test
    public void shouldReturnValuesAsDoubleArray() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a: 1, 2.23, 3"}, new OptionalParam("a").setAllowMultipleValues());
        assertDoubleArrayEquals(new double[]{1, 2.23, 3}, params.valuesAsDoubles("a"));
    }

    @Test
    public void simpleCaseOfExtractingRequiredParams()
    {
        final DslParams params = new DslParams(new String[]{"a=1", "b=2"}, new RequiredParam("a"), new RequiredParam("b"));

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void simpleCaseOfExtractingOptionalParams()
    {
        final DslParams params = new DslParams(new String[]{"a=1", "b=2"}, new OptionalParam("a"), new OptionalParam("b"));

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void simpleCaseOfExtractingRequiredAndOptionalParams()
    {
        final DslParams params = new DslParams(new String[]{"a=1", "b=2"}, new RequiredParam("a"), new OptionalParam("b"));

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void simpleCaseOfTestingForThePresenceOfAParam()
    {
        final DslParams params = new DslParams(new String[]{"a=1", "b=2"}, new RequiredParam("a"), new OptionalParam("b"));

        assertTrue(params.hasValue("b"));
    }

    @Test
    public void simpleCaseOfTestingForTheAbsenceOfAParam()
    {
        final DslParams params = new DslParams(new String[]{"a=1"}, new RequiredParam("a"), new OptionalParam("b"));

        assertFalse(params.hasValue("b"));
    }

    @Test
    public void shouldReturnTrueFromHasParamIfAnEmptyValueIsSupplied() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a="}, new OptionalParam("a"));

        assertTrue(params.hasValue("a"));
    }

    @Test
    public void shouldReturnEmptyOptionalWhenValueIsNotSupplied() throws Exception
    {
        final DslParams params = new DslParams(new String[0], new OptionalParam("a"));

        assertEquals(params.valueAsOptional("a"), Optional.empty());
    }

    @Test
    public void shouldReturnOptionalWithValueWhenValueIsSupplied() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a=value"}, new OptionalParam("a"));

        assertEquals(params.valueAsOptional("a"), Optional.of("value"));
    }

    @Test
    public void shouldReturnEmptyOptionalWhenMultipleParameterValueIsNotSupplied() throws Exception
    {
        final DslParams params = new DslParams(new String[0], new OptionalParam("a").setAllowMultipleValues());

        assertEquals(params.valuesAsOptional("a"), Optional.empty());
    }

    @Test
    public void shouldReturnOptionalListWhenMultipleParameterValueIsSupplied() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a=value1", "a=value2"}, new OptionalParam("a").setAllowMultipleValues());

        assertEquals(params.valuesAsOptional("a"), Optional.of(asList("value1", "value2")));
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
        final DslParams params = new DslParams(new String[]{"a=1", "b=2", "b=3", "c=4"},
                                         new RequiredParam("a"),
                                         new RequiredParam("b").setAllowMultipleValues(),
                                         new RequiredParam("c"));

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertEquals("4", params.value("c"));
    }

    @Test
    public void youCanExtractMultipleRequiredParamsWhenSubsequentRequiredParamsAreNotNamed()
    {
        final DslParams params = new DslParams(new String[]{"a=1", "b=2", "3", "c=4"},
                                         new RequiredParam("a"),
                                         new RequiredParam("b").setAllowMultipleValues(),
                                         new RequiredParam("c"));

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertEquals("4", params.value("c"));
    }

    @Test
    public void hasValueDetectsPresenceOfParamsWithMultipleValuesAllowed() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a=1", "a=2"}, new OptionalParam("a").setAllowMultipleValues());
        assertTrue(params.hasValue("a"));
    }

    @Test
    public void hasValueDetectsAbsenceOfParamsWithMultipleValuesAllowed() throws Exception
    {
        final DslParams params = new DslParams(new String[0], new OptionalParam("a").setAllowMultipleValues());
        assertFalse(params.hasValue("a"));
    }

    @Test
    public void youCanExtractMultipleRequiredParamsAndMultipleOptionalParamsWithOptionalParamsInRandomOrder()
    {
        final DslParams params = new DslParams(new String[]{"a=1", "b=2", "b=3", "c=4", "d=5", "c=6"},
                                         new RequiredParam("a"),
                                         new RequiredParam("b").setAllowMultipleValues(),
                                         new OptionalParam("c").setAllowMultipleValues(),
                                         new OptionalParam("d"));

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertArrayEquals(new String[]{"4", "6"}, params.values("c"));
        assertEquals("5", params.value("d"));
    }

    @Test
    public void namedRequiredParamsMayAppearOutOfOrder() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"b=2", "a=1"}, new RequiredParam("a"), new OptionalParam("b"));
        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldMatchParamsIgnoringCase() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a=1", "B=2"}, new RequiredParam("A"), new OptionalParam("b"));
        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("B"));
        assertTrue(params.hasValue("a"));
        assertTrue(params.hasValue("B"));
    }

    @Test
    public void shouldIgnoreNullArgumentsAmongstOptionalParameters() throws Exception {
        final DslParams params = new DslParams(new String[]{null, "a=1", null, "b=2", null}, new OptionalParam("a"), new OptionalParam("b"));
        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void callingADslMethodWithUsageArgThrowsAnExceptionContainingTheDslParamsForIntrospection()
    {
        final SimpleDslParam[] params = {new RequiredParam("a"), new OptionalParam("b")};
        try
        {
            new DslParams(new String[]{"-usage"}, params);
            Assert.fail("Should have thrown a DslParamsUsageException");
        }
        catch (final DslParamsUsageException e)
        {
            Assert.assertSame(params, e.getParams());
        }
    }

    @Test
    public void shouldBeAbleToRetrieveGroupsOfParams() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a: value", "group: Joe", "value: 1", "group: Jenny", "value: 2"},
                                         new RequiredParam("a"),
                                         new RepeatingParamGroup(new RequiredParam("group"), new RequiredParam("value")));

        assertEquals("value", params.value("a"));
        final RepeatingGroup[] groups = params.valuesAsGroup("group");
        assertEquals(2, groups.length);
        assertEquals("Joe", groups[0].value("group"));
        assertEquals("1", groups[0].value("value"));
        assertEquals("Jenny", groups[1].value("group"));
        assertEquals("2", groups[1].value("value"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseErrorIfRequiredParameterMissingFromGroup() throws Exception
    {
        new DslParams(new String[]{"a: value", "group: Joe", "group: Jenny", "value: 2"},
                                         new RequiredParam("a"),
                                         new RepeatingParamGroup(new RequiredParam("group"), new RequiredParam("value")));
    }

    @Test
    public void shouldBeAbleToSpecifyMultipleValuesForParamInGroup() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a: value", "group: Joe", "group: Jenny", "value: 1", "value: 2"},
                                         new RequiredParam("a"),
                                         new RepeatingParamGroup(new RequiredParam("group"), new OptionalParam("value").setAllowMultipleValues()));

        assertEquals("value", params.value("a"));
        final RepeatingGroup[] groups = params.valuesAsGroup("group");
        assertEquals(2, groups.length);
        assertEquals("Joe", groups[0].value("group"));
        assertEquals(0, groups[0].values("value").length);
        assertEquals("Jenny", groups[1].value("group"));
        assertEquals(2, groups[1].values("value").length);
        assertEquals("1", groups[1].values("value")[0]);
        assertEquals("2", groups[1].values("value")[1]);
    }

    @Test
    public void shouldBeAbleToRetrieveGroupsOfParamsWhenSomeOptionalValuesAreOmitted() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a: value",
                                                      "group: Joe", "value: 1",
                                                      "group: Jenny", "optional: X", "value: 2"},
                                         new RequiredParam("a"),
                                         new RepeatingParamGroup(new RequiredParam("group"),
                                                                 new OptionalParam("optional"),
                                                                 new OptionalParam("value")));

        assertEquals("value", params.value("a"));
        final RepeatingGroup[] groups = params.valuesAsGroup("group");
        assertEquals(2, groups.length);
        assertEquals("Joe", groups[0].value("group"));
        assertEquals("1", groups[0].value("value"));
        assertNull(groups[0].value("optional"));

        assertEquals("Jenny", groups[1].value("group"));
        assertEquals("2", groups[1].value("value"));
        assertEquals("X", groups[1].value("optional"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldEnforceAllowedValuesInRepeatingGroups() throws Exception
    {
        new DslParams(new String[]{"a: value", "group: Joe", "value: 1"},
                      new RequiredParam("a"),
                      new RepeatingParamGroup(new RequiredParam("group"),
                                              new RequiredParam("value").setAllowedValues("A", "B")));
    }

    @Test
    public void shouldUseDefaultValuesForOptionalParametersInRepeatingGroups() throws Exception
    {
        final DslParams params = new DslParams(new String[]{"a: value",
                                                      "group: Joe", "value: 1",
                                                      "group: Jenny", "optional: X", "value: 2"},
                                         new RequiredParam("a"),
                                         new RepeatingParamGroup(new RequiredParam("group"),
                                                                 new OptionalParam("optional").setDefault("default"),
                                                                 new OptionalParam("value")));

        assertEquals("value", params.value("a"));
        final RepeatingGroup[] groups = params.valuesAsGroup("group");
        assertEquals(2, groups.length);
        assertEquals("Joe", groups[0].value("group"));
        assertEquals("1", groups[0].value("value"));
        assertEquals("default", groups[0].value("optional"));

        assertEquals("Jenny", groups[1].value("group"));
        assertEquals("2", groups[1].value("value"));
        assertEquals("X", groups[1].value("optional"));
    }

    @Test
    public void shouldCallConsumerForDefaultValues() throws Exception
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        new DslParams(new String[]{}, new OptionalParam("a").setDefault("b").setConsumer(consumer));
        assertEquals(1, list.size());
        assertEquals("b", list.get(0));
    }

    private void assertDoubleArrayEquals(final double[] expected, final double[] actual)
    {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++)
        {
            assertEquals(expected[i], actual[i], 0d);
        }
    }
}
