/*
 * Copyright (c) 2014, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package oracle.iot.message;

import com.oracle.json.Json;
import com.oracle.json.JsonArrayBuilder;
import com.oracle.json.JsonObject;
import com.oracle.json.JsonObjectBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MessageProperties contains a table of key and list of values pairs for extra
 * information to send. This class is immutable
 */
public final class MessageProperties {
    /**
     * MessagePropertiesBuilder is the builder for MessageProperties.
     */
    public final static class Builder {

        /** A {@link Map} for message properties. It is a key/list of value pair  */
        private Map<String, List<String>> propertiesTable = new HashMap<String, List<String>>();

        public Builder() {

        }

        /**
         * Add a new key/vale pair. If the key exists, add the value to the list of values. If the key does
         * not exist, put the key and value to the table. Key or value cannot be {@code null}. Key cannot be empty.
         * Key or value cannot be long strings. Maximum length for key is {@link Message.Utils#MAX_KEY_LENGTH} bytes,
         * maximum length for value is {@link Message.Utils#MAX_STRING_VALUE_LENGTH} bytes. The length is measured
         * after the string is encoded using UTF-8 encoding.
         *
         * @param  key property key
         * @param  value property value
         * @return MessageProperties.Builder
         * @throws NullPointerException for {@code null} key or the values are {@code null}.
         * @throws IllegalArgumentException for empty or long key or for long value.
         */
        public final Builder addValue(String key, String value) {
            Message.Utils.checkNullValueThrowsNPE(key, "MessageProperties: Key");
            Message.Utils.checkEmptyStringThrowsIAE(key, "MessageProperties: Key");
            Message.Utils.checkNullValueThrowsNPE(value, "MessageProperties: Value");

            Message.Utils.checkKeyLengthAndThrowIAE(key, "MessageProperties: Key");
            Message.Utils.checkValueLengthAndThrowIAE(value, "MessageProperties: Key");

            List<String> values;
            if (propertiesTable.containsKey(key)) {
                values = propertiesTable.get(key);

            } else {
                values = new ArrayList<String>();
            }
            values.add(value);
            propertiesTable.put(key, values);

            return this;
        }
        
        /**
         * Add a new key/values pair. If the key exists, adds the values to the list of existing values. If the key does
         * not exist, puts the key and values to the table. Key or values cannot be {@code null}. Key cannot be empty
         * or long string. Values cannot contain long strings. Maximum length for key is
         * {@link Message.Utils#MAX_KEY_LENGTH} bytes, maximum length for any value is
         * {@link Message.Utils#MAX_STRING_VALUE_LENGTH} bytes. The length is measured after the string is encoded
         * using UTF-8 encoding.
         *
         * @param  key property key
         * @param  values property values
         * @return MessageProperties.Builder
         * @throws NullPointerException for {@code null} key or if the values are {@code null} or any item in values
         *                              is {@code null}.
         * @throws IllegalArgumentException for empty or long key and for any long item in values.
         */
        public final Builder addValues(String key, List<String> values) {
            Message.Utils.checkNullValueThrowsNPE(key, "MessageProperties: Key");
            Message.Utils.checkEmptyStringThrowsIAE(key, "MessageProperties: Key");
            Message.Utils.checkNullValueThrowsNPE(values, "MessageProperties: Values");
            Message.Utils.checkNullValuesThrowsNPE(values, "MessageProperties: Values");

            Message.Utils.checkKeyLengthAndThrowIAE(key, "MessageProperties: Key");
            Message.Utils.checkValuesLengthAndThrowIAE(values, "MessageProperties: Values");

            List<String> existingValues;

            if (propertiesTable.containsKey(key)) {
                existingValues = propertiesTable.get(key);

            } else {
                existingValues = new ArrayList<String>();
            }

            existingValues.addAll(values);
            propertiesTable.put(key, existingValues);
            return this;
        }

        /**
         * Copy another {@link MessageProperties} by adding all properties to the current {@link MessageProperties}
         * @param properties {@link MessageProperties} to copy
         * @return MessageProperties.Builder
         */
        public final Builder copy(MessageProperties properties){
            for(String key: properties.getKeys()) {
                List<String> values = new java.util.ArrayList<String>(properties.getProperties(key).size());
                for (String value: properties.getProperties(key)) {
                    values.add(value);
                }
                propertiesTable.put(key, values);
            }
            return this;
        }

        /**
         * Creates new instance of {@link MessageProperties} using values from {@link MessageProperties.Builder}.
         * @return Instance of {@link MessageProperties}
         */
        public final MessageProperties build() {
            return new MessageProperties(this);
        }
    }

    /** A {@link Map} for message properties. It is a key/list of value pair  */
    private final Map<String, List<String>> propertiesTable;

    /**
     * {@link MessageProperties} constructor takes {@link MessageProperties.Builder} and set propertiesTable.
     * @param builder Builder containing properties.
     */
    private MessageProperties(Builder builder) {
        this.propertiesTable = builder.propertiesTable;
    }

    /**
     * Check if the properties contain the key.
     * @param key property key
     * @return {@code true} if the properties contain the key
     */
    public final boolean containsKey(String key) {
        return propertiesTable.containsKey(key);
    }

    /**
     * Get a {@link Set} of the keys.
     * @return a set of the keys, never {@code null}.
     */
    public final Set<String> getKeys() {
        return Collections.unmodifiableSet(propertiesTable.keySet());
    }

    /**
     * Get a {@link List} of values for a particular key.
     * @param key property key
     * @return a {@link List} of {@link String} values
     */
    public final List<String> getProperties(String key) {
        return Collections.unmodifiableList(propertiesTable.get(key));
    }

    /**
     * Get the first value for a particular key.
     * @param key property key
     * @return value, may return {@code null} if the key does not exist or values assigned to this key is empty.
     */
    public final String getProperty(String key) {
        List<String> propertyValues = propertiesTable.get(key);
        
        if (propertyValues != null && propertyValues.size() > 0) {
            return propertyValues.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Get a specific value.
     * @param key property key
     * @param index index of the {@link List} of the {@link String} values
     * @return value, may return {@code null} if the key does not exist or index is out of range.
     */
    public final String getProperty(String key, int index) {
        List<String> propertyValues = propertiesTable.get(key);
        
        if (propertyValues != null && propertyValues.size() > index) {
            return propertyValues.get(index);
        }
        else {
            return null;
        }
    }

    /**
     * Get all properties.
     * @return a {@link Map} of key and list of values, never {@code null}
     */
    public final Map<String, List<String>> getAllProperties() {
        return Collections.unmodifiableMap(propertiesTable);
    }
    
    /**
     * Method to print the message properties in JSON format.
     * @return message properties in string format
     */
    @Override
    public final String toString() {
        return toJSON().toString();
    }

    /**
     * Method to export the message properties to JsonObject.
     * @return message properties in JSON format
     */
    public final JsonObject toJSON() {
        final JsonObjectBuilder properties = Json.createObjectBuilder();
        final JsonArrayBuilder jsonValue = Json.createArrayBuilder();

        for (String k : getKeys()) {
            final List<String> values = getProperties(k);

            for (String v : values) {
                jsonValue.add(v);
            }

            properties.add(k, jsonValue);
        }
        return properties.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageProperties that = (MessageProperties) o;

        if (!propertiesTable.equals(that.propertiesTable)) return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return propertiesTable.hashCode();
    }
}