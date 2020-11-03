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
package org.sonatype.goodies.dropwizard.config;

import java.util.ArrayList;
import java.util.List;

import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.matcher.StringMatcher;
import org.apache.commons.text.matcher.StringMatcherFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * {@link ConfigurationSourceProvider} builder.
 *
 * @since 1.2.0
 */
public class ConfigurationSourceProviderBuilder
{
  private char escapeChar = StringSubstitutor.DEFAULT_ESCAPE;

  private StringMatcher prefixMatcher = StringSubstitutor.DEFAULT_PREFIX;

  private StringMatcher suffixMatcher = StringSubstitutor.DEFAULT_SUFFIX;

  private StringMatcher valueDelimiterMatcher = StringSubstitutor.DEFAULT_VALUE_DELIMITER;

  private final List<StringLookup> lookups = new ArrayList<>();

  private final List<ConfigurationSourceProvider> providers = new ArrayList<>();

  public ConfigurationSourceProviderBuilder escapeChar(final char escape) {
    this.escapeChar = escape;
    return this;
  }

  public ConfigurationSourceProviderBuilder prefixMatcher(final StringMatcher matcher) {
    this.prefixMatcher = checkNotNull(matcher);
    return this;
  }

  public ConfigurationSourceProviderBuilder prefixMatcher(final String token) {
    checkNotNull(token);
    return prefixMatcher(StringMatcherFactory.INSTANCE.stringMatcher(token));
  }

  public ConfigurationSourceProviderBuilder suffixMatcher(final StringMatcher matcher) {
    this.suffixMatcher = checkNotNull(matcher);
    return this;
  }

  public ConfigurationSourceProviderBuilder suffixMatcher(final String token) {
    checkNotNull(token);
    return suffixMatcher(StringMatcherFactory.INSTANCE.stringMatcher(token));
  }

  public ConfigurationSourceProviderBuilder valueDelimiterMatcher(final StringMatcher matcher) {
    this.valueDelimiterMatcher = checkNotNull(matcher);
    return this;
  }

  public ConfigurationSourceProviderBuilder valueDelimiterMatcher(final String token) {
    checkNotNull(token);
    return valueDelimiterMatcher(StringMatcherFactory.INSTANCE.stringMatcher(token));
  }

  public ConfigurationSourceProviderBuilder simpleStyle(final char token) {
    // TODO: verify token is with-in range for yaml
    escapeChar(token);
    prefixMatcher(String.format("%s{", token));
    suffixMatcher("}");
    valueDelimiterMatcher(":-");
    return this;
  }

  public ConfigurationSourceProviderBuilder lookup(final StringLookup lookup) {
    checkNotNull(lookup);
    lookups.add(lookup);
    return this;
  }

  public ConfigurationSourceProviderBuilder provider(final ConfigurationSourceProvider provider) {
    checkNotNull(provider);
    providers.add(provider);
    return this;
  }

  public ConfigurationSourceProvider build() {
    checkState(!lookups.isEmpty(), "At least one lookup must be specified");
    checkState(!providers.isEmpty(), "At least one provider must be specified");

    StringLookup lookup;
    if (lookups.size() == 1) {
      lookup = lookups.get(0);
    }
    else {
      lookup = new FirstMatchStringLookup(lookups.toArray(new StringLookup[0]));
    }

    ConfigurationSourceProvider provider;
    if (providers.size() == 1) {
      provider = providers.get(0);
    }
    else {
      provider = new FirstMatchConfigurationSourceProvider(providers.toArray(new ConfigurationSourceProvider[0]));
    }

    StringSubstitutor substitutor = new StringSubstitutor(lookup, prefixMatcher, suffixMatcher, escapeChar, valueDelimiterMatcher);
    return new SubstitutingSourceProvider(provider, substitutor);
  }
}
