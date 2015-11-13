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

package org.apache.accumulo.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableOfflineException;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.TabletId;
import org.apache.accumulo.harness.AccumuloClusterHarness;
import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;

public class LocatorIT extends AccumuloClusterHarness {

  private static class Expected {
    private String prev;
    private String end;
    private Range range;

    private Expected(String prev, String end, Range r) {
      this.prev = prev == null ? "" : prev;
      this.end = end == null ? "" : end;
      this.range = r;
    }

    public Expected(TabletId tid, Range range2) {
      prev = tid.getPrevEndRow() == null ? "" : tid.getPrevEndRow().toString();
      end = tid.getEndRow() == null ? "" : tid.getEndRow().toString();
      range = range2;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof Expected) {
        Expected oexp = (Expected) o;
        return prev.equals(oexp.prev) && end.equals(oexp.end) && range.equals(oexp.range);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return prev.hashCode() + 31 * end.hashCode() + 19 * range.hashCode();
    }

    @Override
    public String toString() {
      return "[" + end + "] [" + prev + "] " + range;
    }
  }

  private static Expected ne(String prev, String end, Range r) {
    return new Expected(prev, end, r);
  }

  private void assertContains(Map<String,Map<TabletId,List<Range>>> br, HashSet<String> tservers, Expected... expected) {
    ArrayList<Expected> el = new ArrayList<>(Arrays.asList(expected));

    Text tableId = null;

    for (Entry<String,Map<TabletId,List<Range>>> entry : br.entrySet()) {
      Assert.assertTrue("tserver " + entry.getKey() + " not found in " + tservers, tservers.contains(entry.getKey()));

      for (Entry<TabletId,List<Range>> entry2 : entry.getValue().entrySet()) {
        TabletId tid = entry2.getKey();

        Assert.assertNotNull(tid.getTableId());

        if (tableId == null) {
          tableId = tid.getTableId();
        } else {
          Assert.assertEquals(tableId, tid.getTableId());
        }

        for (Range range : entry2.getValue()) {
          Assert.assertTrue(el.size() > 0);

          Iterator<Expected> it = el.iterator();

          boolean found = false;

          while (it.hasNext()) {
            Expected exp = it.next();
            if (exp.equals(new Expected(tid, range))) {
              it.remove();
              found = true;
              break;
            }
          }

          Assert.assertTrue("Did not find " + tid + " " + range, found);
        }
      }
    }

    Assert.assertTrue(el.toString(), el.size() == 0);

  }

  @Test
  public void testBasic() throws Exception {
    Connector conn = getConnector();
    String tableName = getUniqueNames(1)[0];

    conn.tableOperations().create(tableName);

    Range r1 = new Range("m");
    Range r2 = new Range("o", "x");

    ArrayList<Range> ranges = new ArrayList<>();

    Assert.assertEquals(0, conn.tableOperations().locate(tableName, ranges).size());

    HashSet<String> tservers = new HashSet<>(conn.instanceOperations().getTabletServers());

    ranges.add(r1);
    Map<String,Map<TabletId,List<Range>>> ret = conn.tableOperations().locate(tableName, ranges);
    assertContains(ret, tservers, ne(null, null, r1));

    ranges.add(r2);
    ret = conn.tableOperations().locate(tableName, ranges);
    assertContains(ret, tservers, ne(null, null, r1), ne(null, null, r2));

    TreeSet<Text> splits = new TreeSet<Text>();
    splits.add(new Text("r"));
    conn.tableOperations().addSplits(tableName, splits);

    ret = conn.tableOperations().locate(tableName, ranges);
    assertContains(ret, tservers, ne(null, "r", r1), ne(null, "r", r2), ne("r", null, r2));

    conn.tableOperations().offline(tableName, true);

    try {
      conn.tableOperations().locate(tableName, ranges);
      Assert.fail();
    } catch (TableOfflineException e) {

    }

  }
}
