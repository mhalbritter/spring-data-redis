/*
 * Copyright 2026-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.redis.config;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.connection.SubscriptionListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

/**
 * Unit tests for {@link SubscriptionListenerRegistry}.
 *
 * @author Moritz Halbritter
 */
@ExtendWith(MockitoExtension.class)
class SubscriptionListenerRegistryUnitTests {

	@Mock RedisMessageListenerContainer container;

	@Test // GH-3439
	void shouldDeferTopicValidationToStart() throws NoSuchMethodException {

		SubscriptionListenerRegistry registry = new SubscriptionListenerRegistry();
		MethodRedisListenerEndpoint endpoint = endpointWithoutTopic();

		assertThatNoException().isThrownBy(() -> registry.registerListener(endpoint, this.container));
		assertThatIllegalArgumentException().isThrownBy(registry::start).withMessageContaining("Topic");
	}

	private static MethodRedisListenerEndpoint endpointWithoutTopic() throws NoSuchMethodException {

		Method method = SubscriptionAwareService.class.getMethod("handle", String.class);

		DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
		factory.afterPropertiesSet();

		MethodRedisListenerEndpoint endpoint = new MethodRedisListenerEndpoint(new SubscriptionAwareService(), method);
		endpoint.setMessageHandlerMethodFactory(factory);
		return endpoint;
	}

	static class SubscriptionAwareService implements SubscriptionListener {

		public void handle(String message) {}

	}

}
