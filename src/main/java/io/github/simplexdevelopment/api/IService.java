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

import io.github.simplexdevelopment.scheduler.ServicePool;
import org.bukkit.plugin.java.JavaPlugin;
import reactor.core.publisher.Mono;

/**
 * Represents a service that can be registered to a {@link ServicePool}.
 * <p>
 * This interface provides surface level methods to be used by the {@link ServicePool}
 * associated to each service.
 *
 * @author SimplexDevelopment
 */
public interface IService extends Runnable, Identifier {

    /**
     * @return If the service should be scheduled for repeated executions or not.
     */
    boolean isPeriodic();

    /**
     * @return How long the service should wait between subsequent executions.
     */
    long getPeriod();

    /**
     * @return How long the service should wait before executing the first time.
     */
    long getDelay();

    /**
     * The actual start method for the service. This should be overridden by subclasses,
     * and should include all the required code necessary to execute when the service is queued.
     *
     * @return An encapsulated Mono object representing the start method for the service.
     */
    Mono<Void> start();

    /**
     * The actual end method for the service. This should be overridden by subclasses,
     * and should include all the required code necessary to execute when the service is stopped.
     *
     * @return An encapsulated Mono object representing the end method for the service.
     */
    Mono<Void> stop();

    /**
     * @return The plugin which was defined in the constructor.
     * This should be an instance of your main plugin class.
     */
    JavaPlugin getPlugin();

    /**
     * @return The {@link ServicePool} which this service is executing on.
     */
    Mono<ServicePool> getParentPool();

    /**
     * Sets the parent pool for this service.
     *
     * @param servicePool The service pool to attach this service to.
     * @return An encapsulated Mono object representing the set operation.
     */
    Mono<Void> setParentPool(ServicePool servicePool);

    @Override
    default void run() {
        start().subscribe();
    }
}
