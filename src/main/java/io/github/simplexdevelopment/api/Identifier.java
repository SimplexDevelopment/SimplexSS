/*
 * MIT License
 *
 * Copyright (c) 2022 SimplexDevelopment
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.simplexdevelopment.api;

import java.util.UUID;

/**
 * This interface provides a method for retrieving a name, unique identifier, and numerical id for a class.
 *
 * @author SimplexDevelopment
 */
public interface Identifier {
    /**
     * @return The name of the identifiable object in a readable format.
     */
    String getName();

    /**
     * @return The UUID of the identifiable object, based on the {@link Identifier#getName()} value.
     * This is calculated using {@link UUID#nameUUIDFromBytes(byte[])}.
     */
    default UUID getUniqueId() {
        return UUID.nameUUIDFromBytes(getName().getBytes());
    }

    /**
     * @return The numerical id of the identifiable object, based on the {@link Identifier#getUniqueId()} value.
     * This is calculated using {@link UUID#hashCode()}.
     */
    default int getNumericalId() {
        return getUniqueId().hashCode();
    }
}
