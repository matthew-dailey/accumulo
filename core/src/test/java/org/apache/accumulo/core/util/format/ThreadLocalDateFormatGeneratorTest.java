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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Test;

public class ThreadLocalDateFormatGeneratorTest {

  /** Asserts two generator instance create independent objects */
  private void assertGeneratorsIndependent(ThreadLocal<DateFormat> generatorA, ThreadLocal<DateFormat> generatorB) {
    DateFormat getA1 = generatorA.get();
    DateFormat getA2 = generatorA.get();
    assertSame(getA1, getA2);

    DateFormat getB1 = generatorB.get();
    DateFormat getB2 = generatorB.get();

    assertSame(getB1, getB2);
    assertNotSame(getA1, getB1);
  }

  @Test
  public void testCreateDefaultFormatGenerator() throws Exception {
    ThreadLocal<DateFormat> generatorA = ThreadLocalDateFormatGenerator.createDefaultFormatGenerator();
    ThreadLocal<DateFormat> generatorB = ThreadLocalDateFormatGenerator.createDefaultFormatGenerator();
    assertGeneratorsIndependent(generatorA, generatorB);
  }

  @Test
  public void testCreateSimpleFormatGenerator() throws Exception {
    final String format = ThreadLocalDateFormatGenerator.HUMAN_READABLE_FORMAT;
    DateFormatGenerator generatorA = ThreadLocalDateFormatGenerator.createSimpleFormatGenerator(format);
    DateFormatGenerator generatorB = ThreadLocalDateFormatGenerator.createSimpleFormatGenerator(format);
    assertGeneratorsIndependent(generatorA, generatorB);

    // since dfA and dfB come from different generators, altering the TimeZone on one does not affect the other
    generatorA.setTimeZone(TimeZone.getTimeZone("UTC"));
    final DateFormat dfA = generatorA.get();

    generatorB.setTimeZone(TimeZone.getTimeZone("EST"));
    final DateFormat dfB = generatorB.get();

    final String resultA = dfA.format(new Date(0));
    assertEquals("1970/01/01 00:00:00.000", resultA);

    final String resultB = dfB.format(new Date(0));
    assertEquals("1969/12/31 19:00:00.000", resultB);

    assertTrue(!resultA.equals(resultB));

  }
}
