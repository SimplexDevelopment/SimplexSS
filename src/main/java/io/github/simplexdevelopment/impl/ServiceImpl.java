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

package io.github.simplexdevelopment.impl;

import io.github.simplexdevelopment.scheduler.ExecutableService;
import io.github.simplexdevelopment.scheduler.ServicePool;
import org.bukkit.plugin.java.JavaPlugin;
import reactor.core.publisher.Mono;

public class ServiceImpl extends ExecutableService {
    private final Main plugin;

    public ServiceImpl(Main plugin, ServicePool pool) {
        super(pool, "default", 0L, 20 * 60 * 20L, true, false);
        this.plugin = plugin;
    }

    @Override
    public Mono<Void> start() {
        return Mono.just(plugin)
                .map(JavaPlugin::getLogger)
                .doOnNext(l -> l.info("The service has executed successfully!"))
                .then();
    }

    @Override
    public Mono<Void> stop() {
        return Mono.just(plugin)
                .map(JavaPlugin::getLogger)
                .doOnNext(l -> l.info("The service has stopped"))
                .then();
    }

    @Override
    public Main getPlugin() {
        return plugin;
    }
}
