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
import org.apache.commons.text.StrLookup;
import org.apache.commons.text.StrMatcher;
import org.apache.commons.text.StrSubstitutor;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * {@link ConfigurationSourceProvider} builder.
 *
 * @since ???
 */
public class ConfigurationSourceProviderBuilder
{
  private char escapeChar = StrSubstitutor.DEFAULT_ESCAPE;

  private StrMatcher prefixMatcher = StrSubstitutor.DEFAULT_PREFIX;

  private StrMatcher suffixMatcher = StrSubstitutor.DEFAULT_SUFFIX;

  private StrMatcher valueDelimiterMatcher = StrSubstitutor.DEFAULT_VALUE_DELIMITER;

  private final List<StrLookup<?>> lookups = new ArrayList<>();

  private final List<ConfigurationSourceProvider> providers = new ArrayList<>();

  public ConfigurationSourceProviderBuilder escapeChar(final char escape) {
    this.escapeChar = escape;
    return this;
  }

  public ConfigurationSourceProviderBuilder prefixMatcher(final StrMatcher matcher) {
    this.prefixMatcher = checkNotNull(matcher);
    return this;
  }

  public ConfigurationSourceProviderBuilder prefixMatcher(final String token) {
    checkNotNull(token);
    return prefixMatcher(StrMatcher.stringMatcher(token));
  }

  public ConfigurationSourceProviderBuilder suffixMatcher(final StrMatcher matcher) {
    this.suffixMatcher = checkNotNull(matcher);
    return this;
  }

  public ConfigurationSourceProviderBuilder suffixMatcher(final String token) {
    checkNotNull(token);
    return suffixMatcher(StrMatcher.stringMatcher(token));
  }

  public ConfigurationSourceProviderBuilder valueDelimiterMatcher(final StrMatcher matcher) {
    this.valueDelimiterMatcher = checkNotNull(matcher);
    return this;
  }

  public ConfigurationSourceProviderBuilder valueDelimiterMatcher(final String token) {
    checkNotNull(token);
    return valueDelimiterMatcher(StrMatcher.stringMatcher(token));
  }

  public ConfigurationSourceProviderBuilder simpleStyle(final char token) {
    // TODO: verify token is with-in range for yaml
    escapeChar(token);
    prefixMatcher(String.format("%s{", token));
    suffixMatcher("}");
    valueDelimiterMatcher(":-");
    return this;
  }

  public ConfigurationSourceProviderBuilder lookup(final StrLookup<?> lookup) {
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

    StrLookup lookup;
    if (lookups.size() == 1) {
      lookup = lookups.get(0);
    }
    else {
      lookup = new FirstMatchStrLookup(lookups.toArray(new StrLookup[0]));
    }

    ConfigurationSourceProvider provider;
    if (providers.size() == 1) {
      provider = providers.get(0);
    }
    else {
      provider = new FirstMatchConfigurationSourceProvider(providers.toArray(new ConfigurationSourceProvider[0]));
    }

    StrSubstitutor substitutor = new StrSubstitutor(lookup, prefixMatcher, suffixMatcher, escapeChar, valueDelimiterMatcher);
    return new SubstitutingSourceProvider(provider, substitutor);
  }
}
