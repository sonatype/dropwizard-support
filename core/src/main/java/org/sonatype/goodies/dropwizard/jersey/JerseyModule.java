package org.sonatype.goodies.dropwizard.jersey;

import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.servlet.ServletContainer;

// TODO: maybe explicit binding vs. named?

/**
 * Jersey module.
 *
 * @since ???
 */
@Named
public class JerseyModule
    extends AbstractModule
{
  @Override
  protected void configure() {
    // empty
  }

  @Provides
  ServletContainer getServletContainer(final Environment environment) {
    return (ServletContainer) environment.getJerseyServletContainer();
  }

  @Provides
  ApplicationHandler getApplicationHandler(final ServletContainer container) {
    return container.getApplicationHandler();
  }

  @Provides
  InjectionManager getInjectionManager(final ApplicationHandler handler) {
    return handler.getInjectionManager();
  }

  //@Provides
  //ServiceLocator getServiceLocator(final InjectionManager injection) {
  //  return injection.getInstance(ServiceLocator.class);
  //}
}
