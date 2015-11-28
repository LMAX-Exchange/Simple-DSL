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
import java.util.function.Consumer;

public class RequiredParamTest
{
    @Test
    public void aRequiredParamShouldIdentifyItselfAsSuch()
    {
        final DslParam param = new RequiredParam("foo");
        Assert.assertTrue(param.isRequired());
    }

    @Test
    public void testConsumeAssignsAnArgumentToTheParameter()
    {
        final RequiredParam param = new RequiredParam("foo");
        final int position = param.consume(1, new NameValuePair("goo = 0"), new NameValuePair("foo = 1"), new NameValuePair("bar = 2"));
        Assert.assertEquals(2, position);
        Assert.assertEquals("1", param.getValue());
    }

    @Test
    public void testConsumeWorksCaseInsensitively()
    {
        final RequiredParam param = new RequiredParam("foo");
        final int position = param.consume(1, new NameValuePair("goo = 0"), new NameValuePair("FOO = 1"), new NameValuePair("bar = 2"));
        Assert.assertEquals(2, position);
        Assert.assertEquals("1", param.getValue());
    }

    @Test
    public void testRequiredParametersAreExtractedByPositionAndDoNotNeedToBeNamed()
    {
        final RequiredParam param = new RequiredParam("foo");
        final int position = param.consume(1, new NameValuePair("0"), new NameValuePair("1"), new NameValuePair("2"));
        Assert.assertEquals(2, position);
        Assert.assertEquals("1", param.getValue());
    }

    @Test
    public void testEdgeCaseOfConsumingTheFirstArgInTheList()
    {
        final RequiredParam param = new RequiredParam("goo");
        final int position = param.consume(0, new NameValuePair("goo = 0"), new NameValuePair("bar = 1"), new NameValuePair("foo = 2"));
        Assert.assertEquals(1, position);
        Assert.assertEquals("0", param.getValue());
    }

    @Test
    public void testEdgeCaseOfConsumingTheLastArgInTheList()
    {
        final RequiredParam param = new RequiredParam("foo");
        final int position = param.consume(2, new NameValuePair("goo = 0"), new NameValuePair("bar = 1"), new NameValuePair("foo = 2"));
        Assert.assertEquals(3, position);
        Assert.assertEquals("2", param.getValue());
    }

    @Test
    public void consumeConsumesMultipleParamsUpToTheFirstParamNamedDifferently()
    {
        final RequiredParam param = new RequiredParam("foo").setAllowMultipleValues();
        final int position = param.consume(1, new NameValuePair("first param"), new NameValuePair("foo = 1"), new NameValuePair("2"), new NameValuePair("foo = 3"), new NameValuePair("4"),
                                     new NameValuePair("something else = 5"));
        Assert.assertEquals(5, position);
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4"}, param.getValues());
    }

    @Test
    public void consumeCanConsumeMultipleParamsBySplittingValuesByCommaDelimiter()
    {
        final RequiredParam param = new RequiredParam("foo").setAllowMultipleValues();
        final int position = param.consume(1, new NameValuePair("first param"), new NameValuePair("foo = 1, 2"), new NameValuePair("foo = 3"), new NameValuePair("4"),
                                     new NameValuePair("something else = 5"));
        Assert.assertEquals(4, position);
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4"}, param.getValues());
    }

    @Test
    public void consumeCanConsumeMultipleParamsBySplittingValuesByUserSuppliedDelimiter()
    {
        final RequiredParam param = new RequiredParam("foo").setAllowMultipleValues("\\|");
        final int position = param.consume(1, new NameValuePair("first param"), new NameValuePair("foo = 1| 2"), new NameValuePair("foo = 3"), new NameValuePair("4"),
                                     new NameValuePair("something else = 5"));
        Assert.assertEquals(4, position);
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4"}, param.getValues());
    }

    @Test
    public void consumerIsCalledForSingleValue()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam param = new RequiredParam("foo").setConsumer(consumer);
        param.addValue("abc");

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("abc", list.get(0));
    }

}
