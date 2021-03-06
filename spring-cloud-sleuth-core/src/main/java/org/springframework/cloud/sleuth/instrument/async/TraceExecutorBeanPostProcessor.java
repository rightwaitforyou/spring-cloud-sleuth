/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sleuth.instrument.async;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Bean post processor that wraps {@link Executor} in its Trace representation
 *
 * @author Marcin Grzejszczak
 * @since 1.0.10
 */
class TraceExecutorBeanPostProcessor implements BeanPostProcessor {

	private static final Log log = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final BeanFactory beanFactory;

	public TraceExecutorBeanPostProcessor(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof ThreadPoolTaskExecutor && !(bean instanceof TaskScheduler) &&
				!(bean instanceof LazyTraceThreadPoolTaskExecutor)) {
			return new LazyTraceThreadPoolTaskExecutor(this.beanFactory, (ThreadPoolTaskExecutor) bean);
		}
		if (bean instanceof Executor && !(bean instanceof TaskScheduler) && !(bean instanceof LazyTraceExecutor)) {
			return new LazyTraceExecutor(this.beanFactory, (Executor) bean);
		}
		return bean;
	}

	@Override public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
}
