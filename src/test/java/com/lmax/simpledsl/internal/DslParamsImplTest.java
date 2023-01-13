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
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DslParamsImplTest
{
    @Test
    public void shouldReturnValue()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("Hello World"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals("Hello World", params.value("a"));
    }

    @Test
    public void shouldReturnValueWhenUsingAMappingFunction()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("VALUE_1"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(TestValues.VALUE_1, params.valueAs("a", TestValues::valueOf));
    }

    @Test
    public void shouldReturnValueAsAnEnum()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("VALUE_1"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(TestValues.VALUE_1, params.valueAs("a", TestValues.class));
    }

    @Test
    public void shouldReturnMultipleValues()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("Hello World", "Goodbye, Cruel World"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertArrayEquals(new String[]{"Hello World", "Goodbye, Cruel World"}, params.values("a"));
    }

    @Test
    public void shouldReturnMultipleValuesWhenUsingAMappingFunctionToGenericArray()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("VALUE_1", "VALUE_2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertArrayEquals(
                new Object[]{TestValues.VALUE_1, TestValues.VALUE_2},
                params.valuesAs("a", TestValues::valueOf)
        );
    }

    @Test
    public void shouldReturnMultipleValuesWhenUsingAMappingFunctionToArrayOfSpecificType()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("VALUE_1", "VALUE_2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertArrayEquals(
                new TestValues[]{TestValues.VALUE_1, TestValues.VALUE_2},
                params.valuesAs("a", TestValues.class, TestValues::valueOf)
        );
    }

    @Test
    public void shouldReturnMultipleValuesAsAnEnum()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("VALUE_1", "VALUE_2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertArrayEquals(
                new TestValues[]{TestValues.VALUE_1, TestValues.VALUE_2},
                params.valuesAs("a", TestValues.class)
        );
    }

    @Test
    public void shouldReturnValueAsBoolean()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("true"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertTrue(params.valueAsBoolean("a"));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenValueAsBooleanCalledForParameterThatIsNotSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertThrows(
                NullPointerException.class,
                () -> params.valueAsBoolean("a"));
    }

    @Test
    public void shouldReturnValueAsInt()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("1"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(1, params.valueAsInt("a"));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenValueAsIntCalledForParameterThatIsNotSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertThrows(
                NullPointerException.class,
                () -> params.valueAsInt("a"));
    }

    @Test
    public void shouldReturnValueAsLong()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("1"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(1L, params.valueAsLong("a"));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenValueAsLongCalledForParameterThatIsNotSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertThrows(
                NullPointerException.class,
                () -> params.valueAsLong("a"));
    }

    @Test
    public void shouldReturnValueAsDouble()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("1.23"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(1.23d, params.valueAsDouble("a"), 0d);
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenValueAsDoubleCalledForParameterThatIsNotSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertThrows(
                NullPointerException.class,
                () -> params.valueAsDouble("a"));
    }

    @Test
    public void shouldReturnValueAsBigDecimal()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("1"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(0, BigDecimal.ONE.compareTo(params.valueAsBigDecimal("a")));
    }

    @Test
    public void shouldReturnNullValueWhenBigDecimalIsNotFound()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertNull(params.valueAsBigDecimal("a"));
    }

    @Test
    public void shouldReturnValueAsParamForOptionalValueThatWasSpecified()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("value"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals("a: value", params.valueAsParam("a"));
    }

    @Test
    public void shouldReturnNullValueAsParamWhenSimpleDslParamNotSpecified()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertNull(params.valueAsParam("a"));
    }

    @Test
    public void shouldReturnValueAsNamedParamForOptionalValueThatWasSpecified()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("value"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals("b: value", params.valueAsParamNamed("a", "b"));
    }

    @Test
    public void shouldReturnNullValueAsNamedParamWhenSimpleDslParamNotSpecified()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertNull(params.valueAsParamNamed("a", "b"));
    }

    @Test
    public void shouldReturnValuesAsIntArray()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("1", "2", "3"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertArrayEquals(new int[]{1, 2, 3}, params.valuesAsInts("a"));
    }

    @Test
    public void shouldReturnValuesAsLongArray()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("1", "2", "3"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertArrayEquals(new long[]{1, 2, 3}, params.valuesAsLongs("a"));
    }

    @Test
    public void shouldReturnValuesAsDoubleArray()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("1", "2.23", "3"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertArrayEquals(new double[]{1, 2.23, 3}, params.valuesAsDoubles("a"));
    }

    @Test
    public void shouldReturnValuesAsBigDecimalArray()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("1", "2.23", "3"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertArrayEquals(
                new BigDecimal[]{new BigDecimal("1"), new BigDecimal("2.23"), new BigDecimal("3")},
                params.valuesAsBigDecimals("a")
        );
    }

    @Test
    public void shouldReportOptionalValueAsPresentWhenValueProvided()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("1"));
        final SimpleDslParam bParam = new SimpleDslParam("b", singletonList("2"));

        final Map<String, DslParam> paramsByName = new HashMap<>();
        paramsByName.put("a", aParam);
        paramsByName.put("b", bParam);

        final DslParams params = new DslParamsImpl(new DslArg[0], paramsByName);

        assertTrue(params.hasValue("b"));
    }

    @Test
    public void shouldNotReportOptionalValueAsPresentWhenNoValueProvided()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("1"));
        final SimpleDslParam bParam = new SimpleDslParam("b", emptyList());

        final Map<String, DslParam> paramsByName = new HashMap<>();
        paramsByName.put("a", aParam);
        paramsByName.put("b", bParam);

        final DslParams params = new DslParamsImpl(new DslArg[0], paramsByName);

        assertFalse(params.hasValue("b"));
    }

    @Test
    public void shouldReportRequiredValueAsPresentWhenEmptyValueProvided()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList(""));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertTrue(params.hasValue("a"));
    }

    @Test
    public void shouldReportOptionalValueAsPresentWhenEmptyValueProvided()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList(""));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertTrue(params.hasValue("a"));
    }

    @Test
    public void shouldReturnEmptyOptionalWhenValueIsNotSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(Optional.empty(), params.valueAsOptional("a"));
    }

    @Test
    public void shouldReturnOptionalWithValueWhenValueIsSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("value"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(Optional.of("value"), params.valueAsOptional("a"));
    }

    @Test
    public void shouldReturnEmptyOptionalWhenValueIsNotSuppliedAndMappingFunctionIsUsed()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(Optional.empty(), params.valueAsOptionalOf("a", TestValues::valueOf));
    }

    @Test
    public void shouldReturnOptionalWithValueWhenValueIsSuppliedAndMappingFunctionIsUsed()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("VALUE_1"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(Optional.of(TestValues.VALUE_1), params.valueAsOptionalOf("a", TestValues::valueOf));
    }

    @Test
    public void shouldReturnEmptyListWhenValueIsNotSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(emptyList(), params.valuesAsList("a"));
    }

    @Test
    public void shouldReturnListWithValueWhenValueIsSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("value"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(singletonList("value"), params.valuesAsList("a"));
    }

    @Test
    public void shouldReturnListWithMultipleValuesWhenValuesAreSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("value1", "value2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(asList("value1", "value2"), params.valuesAsList("a"));
    }

    @Test
    public void shouldReturnEmptyListWhenValueIsNotSuppliedAndMappingFunctionIsUsed()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(emptyList(), params.valuesAsListOf("a", TestValues::valueOf));
    }

    @Test
    public void shouldReturnListWithValueWhenValueIsSuppliedAndMappingFunctionIsUsed()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", singletonList("VALUE_1"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(singletonList(TestValues.VALUE_1), params.valuesAsListOf("a", TestValues::valueOf));
    }

    @Test
    public void shouldReturnListWithMultipleValuesWhenValuesAreSuppliedAndMappingFunctionIsUsed()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("VALUE_1", "VALUE_2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(asList(TestValues.VALUE_1, TestValues.VALUE_2), params.valuesAsListOf("a", TestValues::valueOf));
    }

    @Test
    public void shouldReturnEmptyOptionalListWhenNoValuesAreSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(Optional.empty(), params.valuesAsOptional("a"));
    }

    @Test
    public void shouldReturnEmptyOptionalIntWhenNoValuesAreSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(OptionalInt.empty(), params.valuesAsOptionalInt("a"));
    }

    @Test
    public void shouldReturnEmptyOptionalLongWhenNoValuesAreSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(OptionalLong.empty(), params.valuesAsOptionalLong("a"));
    }

    @Test
    public void shouldReturnEmptyOptionalDoubleWhenNoValuesAreSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(OptionalDouble.empty(), params.valuesAsOptionalDouble("a"));
    }

    @Test
    public void shouldReturnOptionalListWhenMultipleParameterValueIsSupplied()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("value1", "value2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertEquals(Optional.of(asList("value1", "value2")), params.valuesAsOptional("a"));
    }

    @Test
    public void shouldReturnTrueIfAParamHasBeenDefined()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("value1", "value2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertTrue(params.hasParam("a"));
    }

    @Test
    public void shouldReturnFalseIfAParamHasNotBeenDefined()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("value1", "value2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertFalse(params.hasParam("b"));
    }

    @Test
    public void shouldReturnIfAParamHasBeenDefinedCaseInsensitively()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("value1", "value2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertTrue(params.hasParam("A"));
    }

    @Test
    public void shouldReturnTrueIfAParamHasBeenDefinedAndHasAValue()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("value1", "value2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertTrue(params.hasParamAndValue("a"));
    }

    @Test
    public void shouldReturnFalseIfAParamHasBeenDefinedButDoesNotHaveAValue()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", emptyList());

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertFalse(params.hasParamAndValue("a"));
    }

    @Test
    public void shouldReturnFalseIfAParamHasNotBeenDefinedButDoesNotHaveAValue()
    {

        final DslParams params = new DslParamsImpl(new DslArg[0], emptyMap());

        assertFalse(params.hasParamAndValue("a"));
    }

    @Test
    public void shouldReturnIfAParamHasBeenDefinedHasAValueCaseInsensitively()
    {
        final SimpleDslParam aParam = new SimpleDslParam("a", asList("value1", "value2"));

        final DslParams params = new DslParamsImpl(new DslArg[0], Collections.singletonMap("a", aParam));

        assertTrue(params.hasParamAndValue("A"));
    }

    private enum TestValues
    {
        VALUE_1,
        VALUE_2,
        VALUE_3;
    }
}
