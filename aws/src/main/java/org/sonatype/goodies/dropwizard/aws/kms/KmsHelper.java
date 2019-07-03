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
package org.sonatype.goodies.dropwizard.aws.kms;

import java.nio.ByteBuffer;

import javax.inject.Inject;
import javax.inject.Named;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.util.Base64;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * AWS Key Management Service (KMS) helper.
 *
 * @since 1.2.0
 */
@Named
public class KmsHelper
{
  private static final Logger log = LoggerFactory.getLogger(KmsHelper.class);

  private final AWSKMS client;

  @Inject
  public KmsHelper(final AWSKMS client) {
    this.client = checkNotNull(client);
  }

  public KmsHelper() {
    this(AWSKMSClientBuilder.defaultClient());
  }

  public AWSKMS getClient() {
    return client;
  }

  public byte[] decrypt(final byte[] encrypted) {
    checkNotNull(encrypted);

    ByteBuffer bytes = ByteBuffer.wrap(encrypted);
    DecryptRequest request = new DecryptRequest().withCiphertextBlob(bytes);
    ByteBuffer decrypted = client.decrypt(request).getPlaintext();
    return decrypted.array();
  }

  public String decryptString(final String encrypted) {
    checkNotNull(encrypted);

    byte[] decrypted = decrypt(Base64.decode(encrypted));
    return new String(decrypted, Charsets.UTF_8);
  }
}
