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

package com.lmax.simpledsl.api;


import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

/**
 * Base class for value containers such as {@link DslParams} and {@link RepeatingGroup}.
 * <p>
 * Provides a range of methods to access the supplied values in a convenient form such as parsed into an int.
 */
public interface DslValues
{
    /**
     * Determine if a value was supplied for a parameter.
     * <p>
     * Returns true when the parameter is supplied with an empty value.
     *
     * @param name the name of the parameter.
     * @return true if the parameter was supplied, otherwise false.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    boolean hasValue(String name);

    /**
     * Retrieve the value supplied for a parameter.
     * <p>
     * May return {@code null} if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    String value(String name);

    /**
     * Retrieve the values supplied for a parameter as an array.
     * <p>
     * Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return an array of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    String[] values(String name);

    /**
     * Retrieve the value supplied for a parameter, mapping it using the specified {@link Function function}.
     *
     * @param name   the name of the parameter.
     * @param mapper the mapping {@link Function}.
     * @param <T>    the return type of {@literal mapper}.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    default <T> T valueAs(final String name, final Function<String, T> mapper)
    {
        final String value = value(name);
        return value != null ? mapper.apply(value) : null;
    }

    /**
     * Retrieve the value supplied for a parameter, mapping it to the specified {@link Enum}.
     *
     * @param name     the name of the parameter.
     * @param enumType the {@link Enum} type.
     * @param <T>      the {@link Enum} type.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    default <T extends Enum<T>> T valueAs(final String name, final Class<T> enumType)
    {
        return valueAs(name, val -> Enum.valueOf(enumType, val));
    }

    /**
     * Retrieve the values supplied for a parameter, mapping each provided value using the specified
     * {@link Function function} and returning a {@link Stream} of these values.
     * <p>
     * Returns an empty {@link Stream} if the parameter is optional and a value has not been supplied.
     *
     * @param name   the name of the parameter.
     * @param mapper the mapping {@link Function}.
     * @param <T>    the return type of {@literal mapper}.
     * @return a {@link Stream} of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    default <T> Object[] valuesAs(final String name, final Function<String, T> mapper)
    {
        return stream(values(name)).map(mapper).toArray();
    }

    /**
     * Retrieve the values supplied for a parameter, mapping each provided value using the specified
     * {@link Function function} and returning a {@link Stream} of these values.
     * <p>
     * Returns an empty {@link Stream} if the parameter is optional and a value has not been supplied.
     *
     * @param name   the name of the parameter.
     * @param type   the return type of {@literal mapper}
     * @param mapper the mapping {@link Function}.
     * @param <T>    the return type of {@literal mapper}.
     * @return a {@link Stream} of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    @SuppressWarnings("unchecked")
    default <T> T[] valuesAs(final String name, final Class<T> type, final Function<String, T> mapper)
    {
        return stream(values(name)).map(mapper).toArray(n -> (T[]) Array.newInstance(type, n));
    }

    /**
     * Retrieve the values supplied for a parameter, mapping each provided value using the specified
     * {@link Function function} and returning an array of these values.
     * <p>
     * Returns an empty {@link Stream} if the parameter is optional and a value has not been supplied.
     *
     * @param name   the name of the parameter.
     * @param enumType the {@link Enum} type.
     * @param <T>      the {@link Enum} type.
     * @return a {@link Stream} of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    default <T extends Enum<T>> T[] valuesAs(final String name, final Class<T> enumType)
    {
        return valuesAs(name, enumType, val -> Enum.valueOf(enumType, val));
    }

    /**
     * Retrieve the value supplied for a parameter as an {@link Optional}.
     * <p>
     * The optional will be empty if the parameter was not supplied.
     *
     * @param name the name of the parameter
     * @return an Optional containing the value supplied for the parameter, if any.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    default Optional<String> valueAsOptional(final String name)
    {
        return Optional.ofNullable(value(name));
    }

    /**
     * Retrieve the value supplied for a parameter as an {@link Optional}.
     * <p>
     * The optional will be empty if the parameter was not supplied.
     *
     * @param name   the name of the parameter.
     * @param mapper the mapping {@link Function}.
     * @param <T>    the return type of {@literal mapper}.
     * @return an Optional containing the value supplied for the parameter, if any.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    default <T> Optional<T> valueAsOptionalOf(final String name, final Function<String, T> mapper)
    {
        return Optional.ofNullable(value(name)).map(mapper);
    }

    /**
     * Retrieve the value supplied for a parameter as a {@literal boolean}.
     * <p>
     * The value is parsed using {@link Boolean#parseBoolean(String)}.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    default boolean valueAsBoolean(final String name)
    {
        return valueAs(name, Boolean::parseBoolean);
    }

    /**
     * Retrieve the value supplied for a parameter as an int.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     * @throws NumberFormatException    if the supplied value can't be parsed as an {@code int} (including because it wasn't supplied).
     */
    default int valueAsInt(final String name)
    {
        return valueAs(name, Integer::parseInt);
    }

    /**
     * Retrieve the value supplied for a parameter as a long.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     * @throws NumberFormatException    if the supplied value can't be parsed as a {@code long} (including because it wasn't supplied).
     */
    default long valueAsLong(final String name)
    {
        return valueAs(name, Long::parseLong);
    }

    /**
     * Retrieve the value supplied for a parameter as a {@code double}.
     * <p>
     * The value is parsed using {@link Double#parseDouble(String)}.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     * @throws NumberFormatException    if the supplied value can't be parsed as a {@code double}
     */
    default double valueAsDouble(final String name)
    {
        return valueAs(name, Double::parseDouble);
    }

    /**
     * Retrieve the value supplied for a parameter as a {@link BigDecimal}.
     * <p>
     * The value is parsed using {@link BigDecimal#BigDecimal(String)}.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     * @throws NumberFormatException    if the supplied value can't be parsed as a {@code BigDecimal}
     */
    default BigDecimal valueAsBigDecimal(final String name)
    {
        return valueAs(name, BigDecimal::new);
    }

    /**
     * Retrieve the value supplied for a parameter formatted as a parameter. For example, if the parameter {@literal user} was given the value {@literal jenny},
     * then {@code valueAsParam("user")} would return {@code user: jenny}.
     * <p>
     * This is useful when reusing DSL methods to build higher level functions. e.g.
     *
     * <pre>{@code
     *   public void createUserAndLogin(String... args) {
     *     DslParams params = new DslParams(args,
     *                                      new RequiredParam("user"),
     *                                      new RequiredParam("accountType"));
     *     createUser(params.valueAsParam("user"), "password: password");
     *     login(params.valueAsParam("user"), "password: password");
     *   }
     * }</pre>
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter, formatted as a parameter ready to pass on to another method that uses Simple-DSL.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    default String valueAsParam(final String name)
    {
        final String value = value(name);
        return value != null ? name + ": " + value : null;
    }

    /**
     * Retrieve the value supplied for a parameter formatted as a parameter with the given name.
     * For example, if the parameter {@literal user} was given the value {@literal jenny}, then
     * {@code valueAsParamNamed("user", "person")} would return {@code person: jenny}.
     * <p>
     * This is useful when reusing DSL methods to build higher level functions. e.g.
     *
     * <pre>{@code
     *   public void createUserAndLogin(String... args) {
     *     DslParams params = new DslParams(args,
     *                                      new RequiredParam("user"),
     *                                      new RequiredParam("accountType"));
     *     generateRandomUser(params.valueAsParamNamed("user", "rememberUserAs"));
     *     login(params.valueAsParam("user"), "password: password");
     *   }
     * }</pre>
     *
     * @param oldParamName the name of the parameter.
     * @param newParamName the new name of the parameter.
     * @return the value supplied for that parameter, formatted as a parameter ready to pass on to another method that uses Simple-DSL.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    default String valueAsParamNamed(final String oldParamName, final String newParamName)
    {
        final String value = value(oldParamName);
        return value != null ? newParamName + ": " + value : null;
    }

    /**
     * Retrieve the values supplied for a parameter as a {@link List}. Returns an empty list if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return a List of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    default List<String> valuesAsList(final String name)
    {
        return Arrays.asList(values(name));
    }

    /**
     * Retrieve the values supplied for a parameter as a {@link List}. Returns an empty list if the parameter is optional and a value has not been supplied.
     *
     * @param name   the name of the parameter.
     * @param mapper the mapping {@link Function}.
     * @param <T>    the return type of {@literal mapper}.
     * @return a List of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    default <T> List<T> valuesAsListOf(final String name, final Function<String, T> mapper)
    {
        return stream(values(name)).map(mapper).collect(Collectors.toList());
    }

    /**
     * Retrieve the values supplied for a parameter as an {@link Optional} {@link List}.
     * <p>
     * Returns an {@link Optional#empty() empty Optional} if the parameter is optional and a value has not been supplied.
     * <p>
     * In most cases {@link #valuesAsList} is the more suitable method.
     * This variant is useful if there is an important difference between a parameter being set to an empty list vs not being supplied.
     * <p>
     * For example, a test that checks the properties of a user may have an optional check for the roles assigned to that user:
     * <pre>{@code
     *     DslParams params = new DslParams(args,
     *                                      new RequiredParam("user"),
     *                                      new OptionalParam("suspended"),
     *                                      new OptionalParam("roles").setAllowMultipleValues());
     *     params.valuesAsOptional("roles").ifPresent(roles -> getDriver().verifyRoles(params.value("user"), roles));
     * }</pre>
     * <p>
     * It's also possible to achieve this distinction by checking the parameter was supplied using {@link #hasValue}.
     *
     * @param name the name of the parameter.
     * @return a List of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    default Optional<List<String>> valuesAsOptional(final String name)
    {
        return Optional.of(valuesAsList(name))
                .filter(list -> !list.isEmpty());
    }

    /**
     * Retrieve the values supplied for a parameter as an {@link OptionalInt}.
     * <p>
     * Returns an {@link OptionalInt#empty() empty Optional} if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return the value of the parameter, or empty
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    default OptionalInt valuesAsOptionalInt(final String name)
    {
        return hasValue(name) ? OptionalInt.of(valueAsInt(name)) : OptionalInt.empty();
    }

    /**
     * Retrieve the values supplied for a parameter as an {@link OptionalLong}.
     * <p>
     * Returns an {@link OptionalLong#empty() empty Optional} if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return the value of the parameter, or empty
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    default OptionalLong valuesAsOptionalLong(final String name)
    {
        return hasValue(name) ? OptionalLong.of(valueAsLong(name)) : OptionalLong.empty();
    }

    /**
     * Retrieve the values supplied for a parameter as an {@link OptionalDouble}.
     * <p>
     * Returns an {@link OptionalDouble#empty() empty Optional} if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return the value of the parameter, or empty
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    default OptionalDouble valuesAsOptionalDouble(final String name)
    {
        return hasValue(name) ? OptionalDouble.of(valueAsInt(name)) : OptionalDouble.empty();
    }

    /**
     * Retrieve the values supplied for a parameter as an {@code int} array.
     * <p>
     * Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return an array of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     * @throws NumberFormatException    if any of the supplied values can not be parsed as an {@code int}.
     */
    default int[] valuesAsInts(final String name)
    {
        return stream(values(name))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    /**
     * Retrieve the values supplied for a parameter as a {@code long} array.
     * <p>
     * Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return an array of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     * @throws NumberFormatException    if any of the supplied values can not be parsed as an {@code long}.
     */
    default long[] valuesAsLongs(final String name)
    {
        return stream(values(name))
                .mapToLong(Long::parseLong)
                .toArray();
    }

    /**
     * Retrieve the values supplied for a parameter as a {@code double} array.
     * <p>
     * Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return an array of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     * @throws NumberFormatException    if any of the supplied values can not be parsed as an {@code double}.
     */
    default double[] valuesAsDoubles(final String name)
    {
        return stream(values(name))
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    /**
     * Retrieve the values supplied for a parameter as a {@link BigDecimal} array.
     * <p>
     * Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return an array of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     * @throws NumberFormatException    if any of the supplied values can not be parsed as an {@code BigDecimal}.
     */
    default BigDecimal[] valuesAsBigDecimals(final String name)
    {
        return valuesAs(name, BigDecimal.class, BigDecimal::new);
    }

    /**
     * Get the supported parameters. Supplied values will have been parsed into the parameters.
     *
     * @return the array of {@link DslArg DslArgs} supplied to the constructor.
     */
    DslArg[] getParams();
}
