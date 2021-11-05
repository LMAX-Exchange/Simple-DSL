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

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleDslParamTest
{
    @Test
    public void shouldReturnTheValueAdded()
    {
        final SimpleDslParam param = new SimpleDslParam("foo", Collections.singletonList("12"));

        assertEquals("12", param.getValue());
    }

    @Test
    public void shouldReturnAllTheValuesAddedWhenMultipleValuesAreAllowed()
    {
        final SimpleDslParam param = new SimpleDslParam("foo", asList("12", "34"));

        assertArrayEquals(new String[]{"12", "34"}, param.getValues());
    }

    @Test
    public void shouldThrowExceptionOnAccessingASingleValueIfTheParamAllowsMultipleValues()
    {
        final SimpleDslParam param = new SimpleDslParam("foo", asList("value1", "value2"));

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                param::getValue);

        assertEquals("getValues() should be used when multiple values are allowed", exception.getMessage());
    }
}