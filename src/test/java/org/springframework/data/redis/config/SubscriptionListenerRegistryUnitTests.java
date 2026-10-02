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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.SubscriptionListener;
import org.springframework.data.redis.listener.ChannelTopic;
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
	void shouldResolveTopicsOnStart() throws NoSuchMethodException {

		SubscriptionListenerRegistry registry = new SubscriptionListenerRegistry();
		MethodRedisListenerEndpoint endpoint = endpointWithoutTopic();

		assertThatNoException().isThrownBy(() -> registry.registerListener(endpoint, this.container));

		// topic set after registration is picked up by the forwarder
		endpoint.setTopic("my-channel");
		registry.start();

		verify(this.container).addMessageListener(argThat(SubscriptionListener.class::isInstance),
				eq(Set.of(new ChannelTopic("my-channel"))));
	}

	@Test // GH-3439
	void shouldNotForwardForProgrammaticEndpoints() {

		SubscriptionListenerRegistry registry = new SubscriptionListenerRegistry();
		SimpleRedisListenerEndpoint endpoint = new SimpleRedisListenerEndpoint(new SubscriptionAwareMessageListener());
		endpoint.setTopic("my-channel");

		registry.registerListener(endpoint, this.container);
		registry.start();

		verify(this.container).addMessageListener(any(SubscriptionAwareMessageListener.class), any(ChannelTopic.class));
		verify(this.container, never()).addMessageListener(any(), anyCollection());
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

	static class SubscriptionAwareMessageListener implements MessageListener, SubscriptionListener {

		@Override
		public void onMessage(Message message, byte @Nullable [] pattern) {}

	}

}
