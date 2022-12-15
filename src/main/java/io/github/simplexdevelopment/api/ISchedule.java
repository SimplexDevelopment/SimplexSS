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

import io.github.simplexdevelopment.scheduler.SchedulingSystem;
import io.github.simplexdevelopment.scheduler.ServiceManager;
import org.jetbrains.annotations.NotNull;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * This interface contains surface level methods for the {@link SchedulingSystem} to use.
 *
 * @author SimplexDevelopment
 */
public interface ISchedule {

    /**
     * @return The service manager which exerts control over the service pools and their associated services.
     */
    Mono<ServiceManager> getServiceManager();

    /**
     * Queues a service to be executed in a service pool.
     *
     * @param service The service to use to locate the associated service pool and queue the service for execution.
     * @return A Mono object that can be used to cancel the service.
     */
    @NotNull Mono<Disposable> queue(@NotNull IService service);

    /**
     * @param service The service to run once.
     * @return A Mono object which can be used to run the service one time using {@link Mono#subscribe()}.
     */
    Mono<Void> runOnce(IService service);

    /**
     * @param service The service to forcefully stop.
     * @return A Mono object which can be used to forcefully stop the service with {@link Mono#subscribe()}.
     */
    Mono<Void> forceStop(IService service);

    /**
     * @param service The service to forcefully start.
     * @return A Mono object which can be used to forcefully start the service with {@link Mono#subscribe()}.
     */
    Mono<Void> forceStart(IService service);
}