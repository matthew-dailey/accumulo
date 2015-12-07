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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * As a way to get around thread safety issues in {@link DateFormat}, this class contains helper methods that create what we'll call a DateFormat "generator." A
 * {@link ThreadLocal} will generate a unique DateFormat object for each thread that calls it.
 *
 * We cannot simply have a class contain a static ThreadLocal for the DateFormat it wants because it a) cannot accept a DateFormat, and b) the DateFormat cannot
 * be changed within the state of the class because it will change the state for any calls to that class (not the object) within that thread.
 */
public class ThreadLocalDateFormatGenerator {

  /**
   * Create a generator for {@link org.apache.accumulo.core.util.format.FormatterConfig.DefaultDateFormat}s
   */
  public static DateFormatGenerator createDefaultFormatGenerator() {
    return new DateFormatGenerator() {
      @Override
      protected DateFormat initialValue() {
        return new FormatterConfig.DefaultDateFormat();
      }
    };
  }

  public static final String HUMAN_READABLE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

  /** Create a generator for SimpleDateFormats accepting a dateFormat */
  public static DateFormatGenerator createSimpleFormatGenerator(final String dateFormat) {
    return new DateFormatGenerator() {
      @Override
      protected SimpleDateFormat initialValue() {
        return new SimpleDateFormat(dateFormat);
      }
    };
  }
}
