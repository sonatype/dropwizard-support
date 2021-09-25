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
package org.sonatype.goodies.dropwizard.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.sonatype.goodies.dropwizard.util.TemporaryFiles;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import org.slf4j.MDC;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Capture log-events from specific threads.
 *
 * Utilizes {@link MDC} to carry enabled state from invoker to appender.
 *
 * @since ???
 */
public class ThreadCaptureAppender
    extends AppenderBase<ILoggingEvent>
{
  private static final String CAPTURE_KEY = "thread-capture-id";

  /**
   * Helper to associate underlying NIO file-path with appender.
   */
  private static class FileAppender2
    extends FileAppender<ILoggingEvent>
  {
    private final Path file;

    public FileAppender2(final Path file) {
      this.file = checkNotNull(file);
      setFile(file.toString());
    }
  }

  /**
   * Thread-id to appender mapping.
   */
  private final Map<String,FileAppender2> appenders = new ConcurrentHashMap<>();

  private final Factory configuration;

  private Path tempDirectory;

  private ThreadCaptureAppender(final Factory configuration) {
    this.configuration = checkNotNull(configuration);
  }

  @Override
  public void start() {
    try {
      tempDirectory = TemporaryFiles.directory("thread-capture");
      addInfo("Temporary directory: %s", tempDirectory);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    super.start();
  }

  public void capture(final Thread thread) throws IOException {
    checkNotNull(thread);
    if (!isStarted()) {
      return;
    }

    String id = threadId(thread);
    addInfo("Capture (%s): %s", id, thread);
    checkState(!appenders.containsKey(id), "Duplicate thread-capture ID: %s", id);

    Path file = TemporaryFiles.builder()
        .directory(tempDirectory)
        .prefix(id)
        .suffix(".log")
        .path();
    addInfo("Capture file: (%s): %s", id, file);

    FileAppender2 appender = new FileAppender2(file);
    appender.setContext(getContext());
    appender.setName("thread-capture-" + id);
    appender.setAppend(false);
    appender.setPrudent(false);

    appender.setEncoder(createPatternLayoutEncoder(getContext(), configuration.getLogFormat()));

    appender.start();
    appenders.put(id, appender);
    MDC.put(CAPTURE_KEY, id);
  }

  public void release(final Thread thread, final ReleaseCallback callback) throws IOException {
    checkNotNull(thread);
    if (!isStarted()) {
      return;
    }

    String id = threadId(thread);
    addInfo("Release (%s): %s", id, thread);

    MDC.remove(CAPTURE_KEY);
    FileAppender2 appender = appenders.remove(id);
    checkState(appender != null, "Unknown thread-capture ID: %s", id);

    // TODO: resolve is there is any safer way to ensure that events have all been processed by this appender
    appender.stop();

    Path file = appender.file;
    try {
      callback.handle(file);
    }
    finally {
      Files.delete(file);
    }
  }

  @Override
  protected void append(final ILoggingEvent event) {
    if (!isStarted()) {
      return;
    }

    Map<String,String> mdc = event.getMDCPropertyMap();
    String id = mdc.get(CAPTURE_KEY);
    if (id != null) {
      Appender<ILoggingEvent> appender = appenders.get(id);
      checkState(appender != null, "Unknown thread-capture ID: %s", id);
      appender.doAppend(event);
    }
  }

  //
  // Helpers
  //

  private void addInfo(final String format, final Object... args) {
    addInfo(String.format(format, args));
  }

  private static String threadId(final Thread thread) {
    return String.valueOf(System.identityHashCode(thread));
  }

  private static LayoutWrappingEncoder<ILoggingEvent> createPatternLayoutEncoder(
      final Context context,
      final String pattern)
  {
    PatternLayout layout = new PatternLayout();
    layout.setContext(context);
    layout.setPattern(pattern);
    layout.start();

    LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
    encoder.setContext(context);
    encoder.setLayout(layout);
    encoder.start();

    return encoder;
  }

  //
  // Factory
  //

  private static final AtomicReference<ThreadCaptureAppender> instance = new AtomicReference<>();

  @JsonTypeName("thread-capture")
  public static class Factory
      extends AbstractAppenderFactory<ILoggingEvent>
  {
    @Override
    public Appender<ILoggingEvent> build(
        final LoggerContext context,
        final String applicationName,
        final LayoutFactory<ILoggingEvent> layoutFactory,
        final LevelFilterFactory<ILoggingEvent> levelFilterFactory,
        final AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory)
    {
      checkState(instance.get() == null, "Already registered");

      ThreadCaptureAppender appender = new ThreadCaptureAppender(this);
      appender.setContext(context);
      appender.setName("thread-capture-appender");

      // primary appender here filters which events will be captured
      appender.addFilter(levelFilterFactory.build(threshold));
      getFilterFactories().forEach(f -> appender.addFilter(f.build()));

      appender.start();
      instance.set(appender);

      return wrapAsync(appender, asyncAppenderFactory);
    }
  }

  //
  // Access
  //

  public interface ReleaseCallback
  {
    void handle(Path file) throws IOException;
  }

  public interface CaptureContext
  {
    void release(ReleaseCallback callback);
  }

  /**
   * Initiate capture of logging events for current thread.
   *
   * This relies on the {@link ThreadCaptureAppender} being configured; if not configured operation here and in
   * {@link CaptureContext#release(ReleaseCallback)} are effectively non-operations.
   */
  public static CaptureContext capture() {
    final ThreadCaptureAppender appender = instance.get();
    final Thread thread = Thread.currentThread();

    if (appender != null) {
      try {
        appender.capture(thread);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return callback -> {
      if (appender != null) {
        try {
          appender.release(thread, callback);
        }
        catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }
}
