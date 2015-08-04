package org.aksw.rdfunit.statistics;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 4:02 PM
 */
public class DatasetStatisticsAllIrisTest extends DatasetStatisticsTest {

    private static final int EXPECTED_ITEMS = 19;

    @Override
    protected int getExteptedItems() {
        return EXPECTED_ITEMS;
    }

    @Override
    protected DatasetStatistics getStatisticsObject() {
        return new DatasetStatisticsAllIris();
    }

    @Test
    public void testGetStats() throws Exception {
        for (Map.Entry<String, Integer> entry : executeBasicTest().entrySet()) {
            assertEquals(new Integer(0), entry.getValue());
        }
    }
}