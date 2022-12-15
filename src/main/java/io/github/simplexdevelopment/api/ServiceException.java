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

import java.util.function.Supplier;

/**
 * This class is used to represent a service exception.
 * When a {@link IService} is called that has not been registered with a {@link io.github.simplexdevelopment.scheduler.ServicePool},
 * this exception will be thrown.
 *
 * @author SimplexDevelopment
 */
public class ServiceException extends RuntimeException {
    /**
     * Constructs a new service exception which states the service is not present within any service pools.
     *
     * @param service The service that threw the exception.
     */
    public ServiceException(IService service) {
        super("The service " + service.getName() + " is not present within any service pool.");
    }

    /**
     * @param th The throwable that was thrown.
     */
    public ServiceException(Throwable th) {
        super(th);
    }

    /**
     * @param service The service that threw the exception.
     * @return A supplier that can be used in conjunction with Reactor.
     */
    public static Supplier<ServiceException> supplyException(IService service) {
        return () -> new ServiceException(service);
    }
}
