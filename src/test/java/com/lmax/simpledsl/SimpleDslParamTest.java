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

import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleDslParamTest
{
    @Test
    public void testGetValueReturnsTheValueAdded()
    {
        final SimpleDslParam param = new TestParam("foo");
        param.addValue("12");
        Assert.assertEquals("12", param.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSecondCallToAddValueWhenMultipleValuesNotAllowedThrowsIllegalArgumentException()
    {
        final SimpleDslParam param = new TestParam("foo");
        param.addValue("12");
        param.addValue("12");
    }

    @Test
    public void testCanAddMultipleValuesWhenMultipleValuesAllowed()
    {
        final SimpleDslParam param = new TestParam("foo").setAllowMultipleValues();
        param.addValue("12");
        param.addValue("34");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotAddMultipleValuesIfAddMultipleValuesHasNotBeenCalled()
    {
        final SimpleDslParam param = new TestParam("foo");
        param.addValue("12");
        param.addValue("34");
    }

    @Test
    public void testGetValuesReturnsAllTheValuesAdded()
    {
        final SimpleDslParam param = new TestParam("foo").setAllowMultipleValues();
        param.addValue("12");
        param.addValue("34");
        Assert.assertArrayEquals(new String[]{"12", "34"}, param.getValues());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetValueThrowsAnExceptionIfTheParamAllowsMultipleValues()
    {
        final SimpleDslParam param = new TestParam("foo").setAllowMultipleValues();
        param.getValue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingAnAllowedValuesListRestrictsWhichValuesAreAllowed()
    {
        final SimpleDslParam param = new TestParam("foo").setAllowedValues("12", "34").setAllowMultipleValues();
        param.addValue("12");
        param.addValue("34");
        param.addValue("56");
    }

    @Test
    public void requiredValuesAreMatchedCaseInsensitivelyButReturnedInTheCorrectCase()
    {
        final SimpleDslParam param = new TestParam("foo").setAllowedValues("abc", "def").setAllowMultipleValues();
        param.addValue("abc");
        param.addValue("DeF");
        Assert.assertArrayEquals(new String[]{"abc", "def"}, param.getValues());
    }

    @Test
    public void consumerIsCalledForSingleValue()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam param = new TestParam("foo").setConsumer(consumer);
        param.addValue("abc");

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("abc", list.get(0));
    }

    @Test
    public void consumerIsCalledForMultipleValuesInCorrectOrder()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam param = new TestParam("foo").setAllowMultipleValues().setConsumer(consumer);
        param.addValue("abc");
        param.addValue("def");
        param.addValue("ghi");

        Assert.assertEquals(3, list.size());
        Assert.assertEquals("abc", list.get(0));
        Assert.assertEquals("def", list.get(1));
        Assert.assertEquals("ghi", list.get(2));
    }

    @Test
    public void biConsumerIsCalledWithParameterName()
    {
        final LinkedList<String> list = new LinkedList<>();
        final BiConsumer<String, String> consumer = (name, value) -> list.add(name);
        final SimpleDslParam param1 = new TestParam("foo").setAllowMultipleValues().setConsumer(consumer);
        final SimpleDslParam param2 = new TestParam("bar").setAllowMultipleValues().setConsumer(consumer);
        param1.addValue("abc");
        param1.addValue("def");
        param2.addValue("ghi");

        Assert.assertEquals(3, list.size());
        Assert.assertEquals("foo", list.get(0));
        Assert.assertEquals("foo", list.get(1));
        Assert.assertEquals("bar", list.get(2));
    }

    private static class TestParam extends SimpleDslParam
    {
        public TestParam(final String name)
        {
            super(name);
        }

        @Override
        public RequiredParam getAsRequiredParam()
        {
            return null;
        }

    }
}
