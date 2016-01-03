/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.accumulo.core.util.format;

import java.util.Map.Entry;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

/**
 * This class is <strong>NOT</strong> recommended because it does not respect {@link java.util.TimeZone}.
 *
 * This class can be replaced by {@link DefaultFormatter} where FormatterConfig is initialized with a DateFormat set to {@link #DATE_FORMAT}. See
 * {@link DateFormatGenerator#createSimpleFormatGenerator(String)}.
 *
 * <pre>
 * final DateFormatGenerator dfSupplier = DateFormatGenerator.createSimpleFormatGenerator(DateFormatGenerator.HUMAN_READABLE_FORMAT, TimeZone.getTimeZone(&quot;UTC&quot;));
 * final FormatterConfig config = new FormatterConfig().setPrintTimestamps(true).setDateFormatSupplier(dfSupplier);
 * </pre>
 */
public class DateStringFormatter implements Formatter {

  private DefaultFormatter defaultFormatter = new DefaultFormatter();

  public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

  @Override
  public void initialize(Iterable<Entry<Key,Value>> scanner, FormatterConfig config) {
    FormatterConfig newConfig = new FormatterConfig(config);
    newConfig.setDateFormatSupplier(DateFormatGenerator.createSimpleFormatGenerator(DATE_FORMAT));
    defaultFormatter.initialize(scanner, newConfig);
  }

  @Override
  public boolean hasNext() {
    return defaultFormatter.hasNext();
  }

  @Override
  public String next() {
    return defaultFormatter.next();
  }

  @Override
  public void remove() {
    defaultFormatter.remove();
  }

}
