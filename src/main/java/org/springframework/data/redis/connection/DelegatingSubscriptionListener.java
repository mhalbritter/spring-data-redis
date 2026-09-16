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
package org.springframework.data.redis.connection;

/**
 * {@link SubscriptionListener} forwarding subscription notifications to another {@link SubscriptionListener}.
 * <p>
 * Listener containers use the {@link #getSubscriptionTarget() subscription target} to notify a target listener only
 * once per notification, even if the target is reachable through several listeners registered for the same topic.
 *
 * @author Moritz Halbritter
 * @since 4.2
 */
public interface DelegatingSubscriptionListener extends SubscriptionListener {

	/**
	 * Return the listener that ultimately receives the subscription notifications.
	 *
	 * @return the notification target.
	 */
	SubscriptionListener getSubscriptionTarget();

}
