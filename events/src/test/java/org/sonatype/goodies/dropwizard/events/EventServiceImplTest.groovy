/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.dropwizard.events

import com.google.common.eventbus.EventBus
import org.eclipse.sisu.inject.BeanLocator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.inOrder
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions
import static org.mockito.Mockito.verifyNoInteractions
import static org.mockito.Mockito.when

/**
 * {@link EventServiceImpl} tests
 */
@ExtendWith(MockitoExtension.class)
class EventServiceImplTest
{
  @Mock
  BeanLocator beanLocator

  @Mock
  EventBusFactory eventBusFactory

  @Mock
  EventBus synchronous

  @Mock
  EventBus asynchronous

  @Mock
  EventExecutor eventExecutor

  EventServiceImpl underTest

  @BeforeEach
  void setUp() {
    when(eventBusFactory.create(anyString())).thenReturn(synchronous)
    when(eventBusFactory.create(anyString(), eq(eventExecutor))).thenReturn(asynchronous)
    underTest = new EventServiceImpl(beanLocator, eventBusFactory, eventExecutor)
    underTest.start()
  }

  @AfterEach
  void tearDown() {
    underTest?.stop()
    underTest = null
  }

  private static class SyncHandler
    implements EventAware
  {
    // empty
  }

  @Test
  void 'register sync handler'() {
    def handler = new SyncHandler()
    underTest.register(handler)

    verify(synchronous).register(handler)
    verifyNoMoreInteractions(synchronous)
    verifyNoInteractions(asynchronous)
  }

  @Test
  void 'unregister sync handler'() {
    def handler = new SyncHandler()
    underTest.unregister(handler)

    verify(synchronous).unregister(handler)
    verifyNoMoreInteractions(synchronous)
    verifyNoInteractions(asynchronous)
  }

  private static class AsyncHandler
      implements EventAware, EventAware.Asynchronous
  {
    // empty
  }

  @Test
  void 'register async handler'() {
    def handler = new AsyncHandler()
    underTest.register(handler)

    verify(asynchronous).register(handler)
    verifyNoMoreInteractions(asynchronous)
    verifyNoInteractions(synchronous)
  }

  @Test
  void 'unregister async handler'() {
    def handler = new AsyncHandler()
    underTest.unregister(handler)

    verify(asynchronous).unregister(handler)
    verifyNoMoreInteractions(asynchronous)
    verifyNoInteractions(synchronous)
  }

  @Test
  void 'post invokes sync then async'() {
    def event = new Object()
    underTest.post(event)

    inOrder(synchronous, asynchronous).with { order ->
      order.verify(synchronous).post(event)
      order.verify(asynchronous).post(event)
    }
  }
}
