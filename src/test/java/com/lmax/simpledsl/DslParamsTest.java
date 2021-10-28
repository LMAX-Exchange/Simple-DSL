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

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DslParamsTest
{
    @Test
    public void shouldReturnValueAsInt()
    {
        final String[] args = {"a=1"};
        final DslParam[] parameters = {new RequiredParam("a")};

        final DslParams params = new DslParams(args, parameters);

        assertEquals(1, params.valueAsInt("a"));
    }

    @Test
    public void shouldThrowNumberFormatExceptionWhenValueAsIntCalledForParameterThatIsNotSupplied()
    {
        final String[] args = new String[0];
        final DslParam[] parameters = {new OptionalParam("a")};

        final DslParams params = new DslParams(args, parameters);

        assertThrows(
                IllegalArgumentException.class,
                () -> params.valueAsInt("a"),
                "Multiple foo parameters are not allowed");
    }

    @Test
    public void shouldReturnValueAsLong()
    {
        final String[] args = {"a=1"};
        final DslParam[] parameters = {new RequiredParam("a")};

        final DslParams params = new DslParams(args, parameters);

        assertEquals(1L, params.valueAsLong("a"));
    }

    @Test
    public void shouldReturnValueAsBoolean()
    {
        final String[] args = {"a=true"};
        final DslParam[] parameters = {new RequiredParam("a")};

        final DslParams params = new DslParams(args, parameters);

        assertTrue(params.valueAsBoolean("a"));
    }

    @Test
    public void shouldReturnValueAsBigDecimal()
    {
        final String[] args = {"a=1"};
        final DslParam[] parameters = {new RequiredParam("a")};

        final DslParams params = new DslParams(args, parameters);
        assertEquals(0, BigDecimal.ONE.compareTo(params.valueAsBigDecimal("a")));
    }

    @Test
    public void shouldReturnValueAsDouble()
    {
        final String[] args = {"a=1.23"};
        final DslParam[] parameters = {new RequiredParam("a")};

        final DslParams params = new DslParams(args, parameters);
        assertEquals(1.23d, params.valueAsDouble("a"), 0d);
    }

    @Test
    public void shouldReturnNullValueWhenBigDecimalIsNotFound()
    {
        final String[] args = new String[0];
        final DslParam[] parameters = {new OptionalParam("a")};

        final DslParams params = new DslParams(args, parameters);
        assertNull(params.valueAsBigDecimal("a"));
    }

    @Test
    public void shouldReturnValueAsParamForOptionalValueThatWasSpecified()
    {
        final String[] args = {"a: value"};
        final DslParam[] parameters = {new OptionalParam("a")};

        final DslParams params = new DslParams(args, parameters);
        assertEquals("a: value", params.valueAsParam("a"));
    }

    @Test
    public void shouldReturnNullValueAsParamWhenOptionalParamNotSpecified()
    {
        final String[] args = new String[0];
        final DslParam[] parameters = {new OptionalParam("a")};

        final DslParams params = new DslParams(args, parameters);
        assertNull(params.valueAsParam("a"));
    }

    @Test
    public void shouldReturnValuesAsIntArray()
    {
        final String[] args = {"a: 1, 2, 3"};
        final DslParam[] parameters = {
                new OptionalParam("a").setAllowMultipleValues()
        };

        final DslParams params = new DslParams(args, parameters);

        assertArrayEquals(new int[]{1, 2, 3}, params.valuesAsInts("a"));
    }

    @Test
    public void shouldReturnValuesAsLongArray()
    {
        final String[] args = {"a: 1, 2, 3"};
        final DslParam[] parameters = {
                new OptionalParam("a").setAllowMultipleValues()
        };

        final DslParams params = new DslParams(args, parameters);

        assertArrayEquals(new long[]{1, 2, 3}, params.valuesAsLongs("a"));
    }

    @Test
    public void shouldReturnValuesAsBigDecimalArray()
    {

        final String[] args = {"a: 1, 2.23, 3"};
        final DslParam[] parameters = {
                new OptionalParam("a").setAllowMultipleValues()
        };

        final DslParams params = new DslParams(args, parameters);

        assertArrayEquals(new BigDecimal[]{new BigDecimal("1"), new BigDecimal("2.23"), new BigDecimal("3")}, params.valuesAsBigDecimals("a"));
    }

    @Test
    public void shouldReturnValuesAsDoubleArray()
    {
        final String[] args = {"a: 1, 2.23, 3"};
        final DslParam[] parameters = {
                new OptionalParam("a").setAllowMultipleValues()
        };

        final DslParams params = new DslParams(args, parameters);

        assertArrayEquals(new double[]{1, 2.23, 3}, params.valuesAsDoubles("a"));
    }

    @Test
    public void shouldExtractRequiredParametersWhenNamed()
    {
        final String[] args = {"a=1", "b=2"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new RequiredParam("b")
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldExtractOptionalParametersWhenNamed()
    {
        final String[] args = {"a=1", "b=2"};
        final DslParam[] parameters = {
                new OptionalParam("a"),
                new OptionalParam("b")
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldExtractMixedParameterTypesWhenNamed()
    {
        final String[] args = {"a=1", "b=2"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new OptionalParam("b")
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldReportValueAsPresentWhenProvided()
    {
        final String[] args = {"a=1", "b=2"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new OptionalParam("b")
        };

        final DslParams params = new DslParams(args, parameters);

        assertTrue(params.hasValue("b"));
    }

    @Test
    public void shouldNotReportValueAsPresentWhenNotProvided()
    {
        final String[] args = {"a=1"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new OptionalParam("b")
        };

        final DslParams params = new DslParams(args, parameters);

        assertFalse(params.hasValue("b"));
    }

    @Test
    public void shouldReturnTrueFromHasValueIfAnEmptyValueIsSupplied()
    {
        final String[] args = {"a="};
        final DslParam[] parameters = {new OptionalParam("a")};

        final DslParams params = new DslParams(args, parameters);

        assertTrue(params.hasValue("a"));
    }

    @Test
    public void shouldReturnTrueFromHasValueIfADefaultValueIsUsed()
    {
        final String[] args = new String[0];
        final DslParam[] parameters = {new OptionalParam("a").setDefault("value")};

        final DslParams params = new DslParams(args, parameters);

        assertTrue(params.hasValue("a"));
    }

    @Test
    public void shouldReturnEmptyOptionalWhenValueIsNotSupplied()
    {
        final String[] args = new String[0];
        final DslParam[] parameters = {new OptionalParam("a")};

        final DslParams params = new DslParams(args, parameters);

        assertEquals(params.valueAsOptional("a"), Optional.empty());
    }

    @Test
    public void shouldReturnOptionalWithValueWhenValueIsSupplied()
    {
        final String[] args = new String[0];
        final DslParam[] parameters = {new OptionalParam("a").setDefault("value")};

        final DslParams params = new DslParams(args, parameters);

        assertEquals(params.valueAsOptional("a"), Optional.of("value"));
    }

    @Test
    public void shouldReturnEmptyOptionalWhenMultipleParameterValueIsNotSupplied()
    {
        final String[] args = new String[0];
        final DslParam[] parameters = {new OptionalParam("a").setAllowMultipleValues()};

        final DslParams params = new DslParams(args, parameters);

        assertEquals(params.valuesAsOptional("a"), Optional.empty());
    }

    @Test
    public void shouldReturnOptionalListWhenMultipleParameterValueIsSupplied()
    {
        final String[] args = {"a=value1", "a=value2"};
        final DslParam[] parameters = {new OptionalParam("a").setAllowMultipleValues()};

        final DslParams params = new DslParams(args, parameters);

        assertEquals(params.valuesAsOptional("a"), Optional.of(asList("value1", "value2")));
    }

    @Test
    public void shouldBeAbleToExtractMultipleRequiredParamsWhenAllParamsAreNamed()
    {
        final String[] args = {"a=1", "b=2", "b=3", "c=4"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new RequiredParam("b").setAllowMultipleValues(),
                new RequiredParam("c")
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertEquals("4", params.value("c"));
    }

    @Test
    public void shouldBeAbleToExtractMultipleRequiredParamsWhenSubsequentRequiredParamsAreNotNamed()
    {
        final String[] args = {"a=1", "b=2", "3", "c=4"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new RequiredParam("b").setAllowMultipleValues(),
                new RequiredParam("c")
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertEquals("4", params.value("c"));
    }

    @Test
    public void shouldBeAbleToExtractMultipleRequiredParamsAndMultipleOptionalParamsWithOptionalParamsInRandomOrder()
    {
        final String[] args = {"a=1", "b=2", "b=3", "c=4", "d=5", "c=6"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new RequiredParam("b").setAllowMultipleValues(),
                new OptionalParam("c").setAllowMultipleValues(),
                new OptionalParam("d")
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertArrayEquals(new String[]{"4", "6"}, params.values("c"));
        assertEquals("5", params.value("d"));
    }

    @Test
    public void shouldBeAbleToDetectPresenceOfParamsWithMultipleValuesAllowed()
    {
        final String[] args = {"a=1", "a=2"};
        final DslParam[] parameters = {new OptionalParam("a").setAllowMultipleValues()};

        final DslParams params = new DslParams(args, parameters);

        assertTrue(params.hasValue("a"));
    }

    @Test
    public void shouldBeAbleToDetectAbsenceOfParamsWithMultipleValuesAllowed()
    {
        final String[] args = new String[0];
        final DslParam[] parameters = {new OptionalParam("a").setAllowMultipleValues()};

        final DslParams params = new DslParams(args, parameters);

        assertFalse(params.hasValue("a"));
    }

    @Test
    public void shouldBeAbleToSpecifiedNamedRequiredParamsInAnyOrder()
    {
        final String[] args = {"b=2", "a=1"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new OptionalParam("b")
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldExtractOptionalParamsFromPositionalArguments()
    {
        final String[] args = {"1", "2"};
        final DslParam[] parameters = {
                new OptionalParam("a"),
                new OptionalParam("b")
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldExtractOptionalParamsFromPositionalArgumentsWhenNullArgumentsArePresent()
    {
        final String[] args = {null, "1", null};
        final DslParam[] parameters = {
                new OptionalParam("a"),
                new OptionalParam("b"),
                new OptionalParam("c")
        };

        final DslParams params = new DslParams(args, parameters);

        assertNull(params.value("a"));
        assertEquals("1", params.value("b"));
        assertNull(params.value("c"));
    }

    @Test
    public void shouldMatchParamsIgnoringCase()
    {
        final String[] args = {"a=1", "B=2"};
        final DslParam[] parameters = {
                new RequiredParam("A"),
                new OptionalParam("b")
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("B"));
        assertTrue(params.hasValue("a"));
        assertTrue(params.hasValue("B"));
    }

    @Test
    public void shouldIgnoreNullArgumentsAmongstOptionalParameters()
    {
        final String[] args = {null, "a=1", null, "b=2", null};
        final DslParam[] parameters = {
                new OptionalParam("a"),
                new OptionalParam("b")
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldIgnoreNullArgumentsInRepeatingGroups()
    {
        final String[] args = {"a=1", "b=2", null};
        final DslParam[] parameters = {
                new RepeatingParamGroup(
                        new RequiredParam("a"),
                        new RequiredParam("b"))
        };

        final DslParams params = new DslParams(args, parameters);

        final RepeatingGroup[] group = params.valuesAsGroup("a");
        assertEquals("1", group[0].value("a"));
        assertEquals("2", group[0].value("b"));
    }

    @Test
    public void shouldBeAbleToRetrieveGroupsOfParams()
    {
        final String[] args = {"a: value", "group: Joe", "value: 1", "group: Jenny", "value: 2"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new RepeatingParamGroup(
                        new RequiredParam("group"),
                        new RequiredParam("value"))
        };

        final DslParams params = new DslParams(args, parameters);

        assertEquals("value", params.value("a"));
        final RepeatingGroup[] groups = params.valuesAsGroup("group");
        assertEquals(2, groups.length);
        assertEquals("Joe", groups[0].value("group"));
        assertEquals("1", groups[0].value("value"));
        assertEquals("Jenny", groups[1].value("group"));
        assertEquals("2", groups[1].value("value"));
    }

    @Test
    public void shouldRaiseErrorIfRequiredParameterMissingFromGroup()
    {
        final String[] args = {"a: value", "myGroup: Joe", "myGroup: Jenny", "myValue: 2"};
        final DslParam[] params = {
                new RequiredParam("a"),
                new RepeatingParamGroup(
                        new RequiredParam("myGroup"),
                        new RequiredParam("myValue"))
        };

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DslParams(args, params));

        assertEquals("Did not supply a value for myValue in group myGroup", exception.getMessage());
    }

    @Test
    public void shouldBeAbleToSpecifyMultipleValuesForParamInGroup()
    {
        final String[] args = {"a: value", "group: Joe", "group: Jenny", "value: 1", "value: 2"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new RepeatingParamGroup(
                        new RequiredParam("group"),
                        new OptionalParam("value").setAllowMultipleValues())
        };

        final DslParams params = new DslParams(args, parameters);

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
    public void shouldBeAbleToRetrieveGroupsOfParamsWhenSomeOptionalValuesAreOmitted()
    {
        final String[] args = {
                "a: value",
                "group: Joe", "value: 1",
                "group: Jenny", "optional: X", "value: 2"
        };
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new RepeatingParamGroup(
                        new RequiredParam("group"),
                        new OptionalParam("optional"),
                        new OptionalParam("value"))
        };

        final DslParams params = new DslParams(args, parameters);

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

    @Test
    public void shouldEnforceAllowedValuesInRepeatingGroups()
    {
        final String[] args = {"a: value", "myGroup: Joe", "myValue: 1"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new RepeatingParamGroup(
                        new RequiredParam("myGroup"),
                        new RequiredParam("myValue").setAllowedValues("A", "B"))
        };

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DslParams(args, parameters));

        assertEquals("myValue parameter value '1' must be one of: [A, B]", exception.getMessage());
    }

    @Test
    public void shouldUseDefaultValuesForOptionalParametersInRepeatingGroups()
    {
        final String[] args = {"a: value",
                "group: Joe", "value: 1",
                "group: Jenny", "optional: X", "value: 2"};
        final DslParam[] parameters = {
                new RequiredParam("a"),
                new RepeatingParamGroup(
                        new RequiredParam("group"),
                        new OptionalParam("optional").setDefault("default"),
                        new OptionalParam("value"))
        };

        final DslParams params = new DslParams(args, parameters);

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
    public void shouldCallConsumerForDefaultValues()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;

        final String[] args = {};
        final DslParam[] parameters = {new OptionalParam("a").setDefault("b").setConsumer(consumer)};

        new DslParams(args, parameters);

        assertEquals(1, list.size());
        assertEquals("b", list.get(0));
    }

    @Test
    public void shouldThrowAnExceptionWhenMissingAValueForARequiredParam()
    {
        final String[] args = {"a=1"};
        final DslParam[] parameters = {new RequiredParam("a"), new RequiredParam("b")};

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DslParams(args, parameters));

        assertEquals("Missing value for parameter: b", exception.getMessage());
    }

    @Test
    public void shouldThrowAnExceptionIfAnUnexpectedParameterValueIsPassedIn()
    {
        final String[] args = {"a=1", "b=2"};
        final DslParam[] parameters = {new RequiredParam("a")};

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DslParams(args, parameters));

        assertEquals("Unexpected argument b=2", exception.getMessage());
    }
}
