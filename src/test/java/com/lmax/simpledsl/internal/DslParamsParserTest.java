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
import com.lmax.simpledsl.api.DslParams;
import com.lmax.simpledsl.api.OptionalArg;
import com.lmax.simpledsl.api.RepeatingArgGroup;
import com.lmax.simpledsl.api.RepeatingGroup;
import com.lmax.simpledsl.api.RequiredArg;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DslParamsParserTest
{
    @Test
    public void shouldExtractRequiredArgsWhenNamed()
    {
        final String[] args = {"a=1", "b=2"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("b")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldExtractRequiredArgsWhenUnnamed()
    {
        final String[] args = {"1", "2"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("b")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }
    @Test
    public void shouldExtractOptionalArgsWhenNamed()
    {
        final String[] args = {"a=1", "b=2"};
        final DslArg[] parameters = {
                new OptionalArg("a"),
                new OptionalArg("b")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldExtractOptionalArgsWhenUnnamed()
    {
        final String[] args = {"1", "2"};
        final DslArg[] parameters = {
                new OptionalArg("a"),
                new OptionalArg("b")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldParseAMixtureOfNamedAndUnnamedParametersWhenParametersAreRequired()
    {
        final String[] args = {"a=1", "2", "c: 3"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("b"),
                new OptionalArg("c")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals(1, params.valueAsInt("a"));
        assertEquals(2, params.valueAsInt("b"));
        assertEquals(3, params.valueAsInt("c"));
    }

    @Test
    public void shouldUseTheDefaultValueForAnOptionalArgsWhenNoValueIsProvided()
    {
        final String[] args = new String[0];
        final DslArg[] parameters = {
                new OptionalArg("a"),
                new OptionalArg("b").setDefault("value")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("value", params.value("b"));
    }

    @Test
    public void shouldUseNullForAnOptionalArgsWhenNoValueOrDefaultValueIsProvided()
    {
        final String[] args = new String[0];
        final DslArg[] parameters = {
                new OptionalArg("a"),
                new OptionalArg("b").setDefault("value")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertNull(params.value("a"));
    }

    @Test
    public void shouldExtractMixedParameterTypesWhenNamed()
    {
        final String[] args = {"a=1", "b=2"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new OptionalArg("b")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldBeAbleToExtractMultipleRequiredArgsWhenAllParamsAreNamed()
    {
        final String[] args = {"a=1", "b=2", "b=3", "c=4"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("b").setAllowMultipleValues(),
                new RequiredArg("c")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertEquals("4", params.value("c"));
    }

    @Test
    public void shouldBeAbleToExtractMultipleValuesForOneParameterUsingTheDefaultSeparator()
    {
        final String[] args = {"a=1", "b=2, 3", "c=4"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("b").setAllowMultipleValues(),
                new RequiredArg("c")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertEquals("4", params.value("c"));
    }

    @Test
    public void shouldBeAbleToExtractMultipleValuesForOneParameterUsingTheACustomSeparator()
    {
        final String[] args = {"a=1", "b=2,00;3,00", "c=4"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("b").setAllowMultipleValues(";"),
                new RequiredArg("c")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2,00", "3,00"}, params.values("b"));
        assertEquals("4", params.value("c"));
    }

    @Test
    public void shouldBeAbleToExtractMultipleValuesForOneParameterUsingTheACustomSeparatorAndIgnoreRegex()
    {
        final String[] args = {"a=1", "b=2,00.3,00", "c=4[a-z]5"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("b").setAllowMultipleValues("."),
                new RequiredArg("c").setAllowMultipleValues("[a-z]")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2,00", "3,00"}, params.values("b"));
        assertArrayEquals(new String[]{"4", "5"}, params.values("c"));
    }

    @Test
    public void shouldBeAbleToExtractMultipleRequiredArgsWhenSubsequentRequiredArgsAreNotNamed()
    {
        final String[] args = {"a=1", "b=2", "3", "c=4"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("b").setAllowMultipleValues(),
                new RequiredArg("c")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertEquals("4", params.value("c"));
    }

    @Test
    public void shouldBeAbleToExtractMultipleRequiredArgsWhenWithClearBoundariesBetweenArguments()
    {
        final String[] args = {"a=1", "2", "3", "b=2", "3", "c=4"};
        final DslArg[] parameters = {
                new RequiredArg("a").setAllowMultipleValues(),
                new RequiredArg("b").setAllowMultipleValues(),
                new RequiredArg("c")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertArrayEquals(new String[]{"1", "2", "3"}, params.values("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertEquals("4", params.value("c"));
    }

    @Test
    public void shouldBeAbleToExtractMultipleRequiredArgsAndMultipleOptionalArgsWithOptionalArgsInRandomOrder()
    {
        final String[] args = {"a=1", "b=2", "b=3", "c=4", "d=5", "c=6"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("b").setAllowMultipleValues(),
                new OptionalArg("c").setAllowMultipleValues(),
                new OptionalArg("d")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertArrayEquals(new String[]{"2", "3"}, params.values("b"));
        assertArrayEquals(new String[]{"4", "6"}, params.values("c"));
        assertEquals("5", params.value("d"));
    }

    @Test
    public void shouldBeAbleToDetectPresenceOfParamsWithMultipleValuesAllowed()
    {
        final String[] args = {"a=1", "a=2"};
        final DslArg[] parameters = {new OptionalArg("a").setAllowMultipleValues()};

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertTrue(params.hasValue("a"));
    }

    @Test
    public void shouldBeAbleToDetectAbsenceOfParamsWithMultipleValuesAllowed()
    {
        final String[] args = new String[0];
        final DslArg[] parameters = {new OptionalArg("a").setAllowMultipleValues()};

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertFalse(params.hasValue("a"));
    }

    @Test
    public void shouldBeAbleToSpecifiedNamedRequiredArgsInAnyOrder()
    {
        final String[] args = {"b=2", "a=1"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new OptionalArg("b")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldExtractOptionalArgsFromPositionalArguments()
    {
        final String[] args = {"1", "2"};
        final DslArg[] parameters = {
                new OptionalArg("a"),
                new OptionalArg("b")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldExtractOptionalArgsFromPositionalArgumentsWhenNullArgumentsArePresent()
    {
        final String[] args = {null, "1", null};
        final DslArg[] parameters = {
                new OptionalArg("a"),
                new OptionalArg("b"),
                new OptionalArg("c")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertNull(params.value("a"));
        assertEquals("1", params.value("b"));
        assertNull(params.value("c"));
    }

    @Test
    public void shouldMatchParamsIgnoringCase()
    {
        final String[] args = {"a=1", "B=2"};
        final DslArg[] parameters = {
                new RequiredArg("A"),
                new OptionalArg("b")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("B"));
        assertTrue(params.hasValue("a"));
        assertTrue(params.hasValue("B"));
    }

    @Test
    public void shouldIgnoreNullArgumentsAmongstOptionalArgs()
    {
        final String[] args = {null, "a=1", null, "b=2", null};
        final DslArg[] parameters = {
                new OptionalArg("a"),
                new OptionalArg("b")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("1", params.value("a"));
        assertEquals("2", params.value("b"));
    }

    @Test
    public void shouldIgnoreNullArgumentsInRepeatingGroups()
    {
        final String[] args = {"a=1", "b=2", null};
        final DslArg[] parameters = {
                new RepeatingArgGroup(
                        new RequiredArg("a"),
                        new RequiredArg("b"))
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        final RepeatingGroup[] group = params.valuesAsGroup("a");
        assertEquals("1", group[0].value("a"));
        assertEquals("2", group[0].value("b"));
    }

    @Test
    public void shouldBeAbleToRetrieveGroupsOfParams()
    {
        final String[] args = {"a: value", "group: Joe", "value: 1", "group: Jenny", "value: 2"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RepeatingArgGroup(
                        new RequiredArg("group"),
                        new RequiredArg("value"))
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("value", params.value("a"));
        final RepeatingGroup[] groups = params.valuesAsGroup("group");
        assertEquals(2, groups.length);
        assertEquals("Joe", groups[0].value("group"));
        assertEquals("1", groups[0].value("value"));
        assertEquals("Jenny", groups[1].value("group"));
        assertEquals("2", groups[1].value("value"));
    }

    @Test
    public void shouldBeAbleToRetrieveArgumentsFollowingTheEndOfARepeatingGroup()
    {
        final String[] args = {"a: value", "group: Joe", "value: 1", "group: Jenny", "value: 2", "b: 12", "c: hello"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new OptionalArg("b"),
                new RepeatingArgGroup(
                        new RequiredArg("group"),
                        new RequiredArg("value")),
                new OptionalArg("c")
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("12", params.value("b"));
        assertEquals("hello", params.value("c"));
    }

    @Test
    public void shouldBeAbleToSpecifyMultipleValuesForParamInGroup()
    {
        final String[] args = {"a: value", "group: Joe", "group: Jenny", "value: 1", "value: 2"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RepeatingArgGroup(
                        new RequiredArg("group"),
                        new OptionalArg("value").setAllowMultipleValues())
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

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
    public void shouldBeAbleToSpecifyMultipleValuesForParamInGroupUsingTheDefaultSeparator()
    {
        final String[] args = {"a: value", "group: Joe", "group: Jenny", "value: 1, 2"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RepeatingArgGroup(
                        new RequiredArg("group"),
                        new OptionalArg("value").setAllowMultipleValues())
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

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
    public void shouldBeAbleToSpecifyMultipleValuesForParamInGroupUsingACustomSeparator()
    {
        final String[] args = {"a: value", "group: Joe", "group: Jenny", "value: 1;2"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RepeatingArgGroup(
                        new RequiredArg("group"),
                        new OptionalArg("value").setAllowMultipleValues(";"))
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

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
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RepeatingArgGroup(
                        new RequiredArg("group"),
                        new OptionalArg("optional"),
                        new OptionalArg("value"))
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

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
    public void shouldUseDefaultValuesForOptionalArgsInRepeatingGroups()
    {
        final String[] args = {
                "a: value",
                "group: Joe", "value: 1",
                "group: Jenny", "optional: X", "value: 2"
        };
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RepeatingArgGroup(
                        new RequiredArg("group"),
                        new OptionalArg("optional").setDefault("default"),
                        new OptionalArg("value"))
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

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
    public void shouldMatchAllowedValuesCaseInsensitivelyAndReturnValuesUsingTheCaseProvidedInTheDSL()
    {
        final String[] args = {
                "myValue: abc", "myValue: DeF",
        };
        final DslArg[] parameters = {
                new RequiredArg("myValue").setAllowedValues("abc", "def").setAllowMultipleValues(),
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertArrayEquals(new String[]{"abc", "def"}, params.values("myValue"));
    }

    @Test
    public void shouldMatchAllowedValuesCaseInsensitivelyAndReturnValuesUsingTheCaseProvidedInTheDSLWithinRepeatingGroups()
    {
        final String[] args = {"a: value", "myGroup: joe", "myValue: a"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RepeatingArgGroup(
                        new RequiredArg("myGroup").setAllowedValues("Joe", "Jenny"),
                        new RequiredArg("myValue").setAllowedValues("A", "B"))
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("value", params.value("a"));
        final RepeatingGroup[] groups = params.valuesAsGroup("myGroup");
        assertEquals(1, groups.length);
        assertEquals("Joe", groups[0].value("myGroup"));
        assertEquals("A", groups[0].value("myValue"));
    }

    @Test
    public void shouldMatchAllowedValuesSpecifiedViaABoolean()
    {
        final String[] args = {
                "thisWorks: true",
        };
        final DslArg[] parameters = {
                new RequiredArg("thisWorks").setAllowedValues(Boolean.class),
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertTrue(params.valueAsBoolean("thisWorks"));
    }

    @Test
    public void shouldThrowAnExceptionIfValuesDoesNotMatchAllowedValuesSpecifiedViaABoolean()
    {
        final String[] args = {
                "thisWorks: NO",
        };
        final DslArg[] parameters = {
                new RequiredArg("thisWorks").setAllowedValues(Boolean.class),
        };

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters));

        assertEquals("thisWorks parameter value 'NO' must be one of: [true, false]", exception.getMessage());
    }

    @Test
    public void shouldMatchAllowedValuesSpecifiedViaAnEnum()
    {
        final String[] args = {
                "pet: DRAGON",
        };
        final DslArg[] parameters = {
                new RequiredArg("pet").setAllowedValues(PossiblePets.class),
        };

        final DslParamsParser parser = new DslParamsParser();

        final DslParams params = parser.parse(args, parameters);

        assertEquals("DRAGON", params.value("pet"));
    }

    @Test
    public void shouldThrowAnExceptionIfValuesDoesNotMatchAllowedValuesSpecifiedViaAnEnum()
    {
        final String[] args = {
                "pet: UNICORN",
        };
        final DslArg[] parameters = {
                new RequiredArg("pet").setAllowedValues(PossiblePets.class),
        };

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters));

        assertEquals("pet parameter value 'UNICORN' must be one of: [COW, SHEEP, GOAT, DRAGON]", exception.getMessage());
    }

    @Test
    public void shouldThrowAnExceptionIfRequiredParameterMissingFromGroup()
    {
        final String[] args = {"a: value", "myGroup: Joe", "myGroup: Jenny", "myValue: 2"};
        final DslArg[] params = {
                new RequiredArg("a"),
                new RepeatingArgGroup(
                        new RequiredArg("myGroup"),
                        new RequiredArg("myValue"))
        };

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, params));

        assertEquals("Did not supply a value for myValue in group myGroup", exception.getMessage());
    }

    @Test
    public void shouldThrowAnExceptionIfTwoValuesProvidedWhenMultipleValuesAreNotAllowed()
    {
        final String[] args = {"foo: value1", "foo: value2"};
        final DslArg[] params = {new RequiredArg("foo")};

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, params));

        assertEquals("Multiple foo parameters are not allowed", exception.getMessage());
    }

    @Test
    public void shouldThrowAnExceptionIfInvalidParameterValueIsSpecified()
    {
        final String[] args = {"a: value", "myValue: 1"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("myValue").setAllowedValues("A", "B")
        };

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters));

        assertEquals("myValue parameter value '1' must be one of: [A, B]", exception.getMessage());
    }

    @Test
    public void shouldThrowAnExceptionIfInvalidParameterValueIsSpecifiedInRepeatingGroup()
    {
        final String[] args = {"a: value", "myGroup: Joe", "myValue: 1"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RepeatingArgGroup(
                        new RequiredArg("myGroup"),
                        new RequiredArg("myValue").setAllowedValues("A", "B"))
        };

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters));

        assertEquals("myValue parameter value '1' must be one of: [A, B]", exception.getMessage());
    }

    @Test
    public void shouldThrowAnExceptionIfInvalidParameterValueIsSpecifiedInRepeatingGroupIdentity()
    {
        final String[] args = {"a: value", "myGroup: Joe", "myValue: 1"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RepeatingArgGroup(
                        new RequiredArg("myGroup").setAllowedValues("A", "B"),
                        new RequiredArg("myValue"))
        };

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters));

        assertEquals("myGroup parameter value 'Joe' must be one of: [A, B]", exception.getMessage());
    }

    @Test
    public void shouldThrowAnExceptionWhenMissingAValueForARequiredArg()
    {
        final String[] args = {"a=1"};
        final DslArg[] parameters = {new RequiredArg("a"), new RequiredArg("b")};

        final DslParamsParser parser = new DslParamsParser();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters));

        assertEquals("Missing value for parameter: b", exception.getMessage());
    }

    @Test
    public void shouldThrowAnExceptionIfAnUnexpectedParameterValueIsPassedIn()
    {
        final String[] args = {"a=1", "b=2"};
        final DslArg[] parameters = {new RequiredArg("a")};

        final DslParamsParser parser = new DslParamsParser();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters));

        assertEquals("Unexpected argument b=2", exception.getMessage());
    }


    @Test
    public void shouldThrowAnExceptionIfUnnamedRequiredArgFollowsNamedOptionalArg()
    {
        final String[] args = {"a=1", "2"};
        final DslArg[] parameters = {
                new OptionalArg("a"),
                new RequiredArg("b"),
        };

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters)
        );

        assertEquals("Unexpected ambiguous argument 2", exception.getMessage());
    }

    @Test
    public void shouldThrowAnExceptionIfUnnamedOptionalArgFollowsNamedOptionalArg()
    {
        final String[] args = {"a=1", "2"};
        final DslArg[] parameters = {
                new OptionalArg("a"),
                new OptionalArg("b"),
        };

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters)
        );

        assertEquals("Unexpected ambiguous argument 2", exception.getMessage());
    }

    @Test
    public void shouldThrowAnExceptionIfUnnamedRequiredArgIsProvidedAfterNamedOptionalArgOutOfDefinitionOrder()
    {
        final String[] args = {"b=1", "2"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new OptionalArg("b"),
        };

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters)
        );

        assertEquals("Unexpected ambiguous argument 2", exception.getMessage());
    }

    @Test
    public void maybeShouldThrowExceptionIfRequiredParametersArePassedOutOfOrder()
    {
        final String[] args = {"b=2", "1", "c: 3"};
        final DslArg[] parameters = {
                new RequiredArg("a"),
                new RequiredArg("b"),
                new OptionalArg("c")
        };

        final DslParamsParser parser = new DslParamsParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(args, parameters)
        );

        assertEquals("Unexpected ambiguous argument 1", exception.getMessage());
    }

    private enum PossiblePets
    {
        COW,
        SHEEP,
        GOAT,
        DRAGON
    }
}