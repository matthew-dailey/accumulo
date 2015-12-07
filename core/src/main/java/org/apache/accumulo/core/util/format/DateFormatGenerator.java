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
import java.util.TimeZone;

/**
 * DateFormatGenerator is a {@code ThreadLocal<Date>Format} that will set the correct TimeZone when the object is retrieved.
 */
public abstract class DateFormatGenerator extends ThreadLocal<DateFormat> {
  private TimeZone timeZone;

  public DateFormatGenerator() {
    timeZone = TimeZone.getDefault();
  }

  public DateFormatGenerator(TimeZone timeZone) {
    this.timeZone = timeZone;
  }

  public TimeZone getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(TimeZone timeZone) {
    this.timeZone = timeZone;
  }

  /** Always sets the TimeZone, which is a fast operation */
  @Override
  public DateFormat get() {
    final DateFormat df = super.get();
    df.setTimeZone(timeZone);
    return df;
  }
}
