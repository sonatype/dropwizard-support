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
package org.sonatype.goodies.dropwizard.util;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * IP address/pattern list.
 *
 * @since ???
 * @see IpMatcher
 */
public class IpAddresses
  implements Iterable<String>
{
  private static final Logger log = LoggerFactory.getLogger(IpAddresses.class);

  /**
   * List of addresses or patterns for matching with {@link IpMatcher}.
   */
  private final List<String> addresses = new CopyOnWriteArrayList<>();

  public IpAddresses(final List<String> addresses) {
    setAddresses(addresses);
  }

  public IpAddresses() {
    // empty
  }

  public int getSize() {
    return addresses.size();
  }

  public boolean isEmpty() {
    return addresses.isEmpty();
  }

  public void setAddresses(final List<String> addresses) {
    checkNotNull(addresses);

    List<String> _addresses = addresses.stream().map(String::trim).collect(Collectors.toList());
    log.trace("Addresses: {}", _addresses);

    // add all entries, then retain all to purge older entries to avoid tiny window where list would be empty
    this.addresses.addAll(_addresses);
    this.addresses.retainAll(_addresses);
  }

  public List<String> getAddresses() {
    return ImmutableList.copyOf(addresses);
  }

  public boolean addAddress(final String address) {
    checkNotNull(address);
    String _address = address.trim();
    if (Strings.emptyToNull(_address) != null) {
      boolean result = addresses.add(_address);
      if (result) {
        log.trace("Added IP: {}", _address);
      }
      return result;
    }
    return false;
  }

  public boolean removeAddress(final String address) {
    checkNotNull(address);
    String _address = address.trim();
    boolean result = addresses.remove(_address);
    if (result) {
      log.trace("Removed IP: {}", _address);
    }
    return result;
  }

  public void clear() {
    addresses.clear();
    log.trace("Cleared");
  }

  @Override
  public Iterator<String> iterator() {
    return Iterators.unmodifiableIterator(addresses.iterator());
  }

  public boolean match(final String address) {
    checkNotNull(address);
    return IpMatcher.match(address, addresses);
  }

  @Override
  public String toString() {
    return addresses.toString();
  }
}
