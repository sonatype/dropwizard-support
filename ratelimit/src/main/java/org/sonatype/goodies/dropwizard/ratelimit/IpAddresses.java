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
package org.sonatype.goodies.dropwizard.ratelimit;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

// TODO: consider a more general module for this to live

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

  // TODO: addresses here are ips or patterns for matching with IpMatcher

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
    log.trace("Addresses: {}", addresses);

    // add all entries, then retain all to purge older entries to avoid tiny window where list would be empty
    this.addresses.addAll(addresses);
    this.addresses.retainAll(addresses);
  }

  public List<String> getAddresses() {
    return ImmutableList.copyOf(addresses);
  }

  public boolean addAddress(final String address) {
    checkNotNull(address);
    if (Strings.emptyToNull(address.trim()) != null) {
      boolean result = addresses.add(address);
      if (result) {
        log.trace("Added IP: {}", address);
      }
      return result;
    }
    return false;
  }

  public boolean removeAddress(final String address) {
    checkNotNull(address);
    boolean result = addresses.remove(address.trim());
    if (result) {
      log.trace("Removed IP: {}", address);
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
    return MoreObjects.toStringHelper(this)
        .add("addresses", addresses)
        .toString();
  }
}
