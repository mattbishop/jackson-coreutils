package com.github.fge.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class JsonNumEqualsTest
{
    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;
    private final JsonNode testData;

    public JsonNumEqualsTest()
        throws IOException
    {
        final ObjectReader reader = new ObjectMapper()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .reader();

        final InputStream in = JsonNumEqualsTest.class
            .getResourceAsStream("/testfile.json");
        testData = reader.readTree(in);
    }

    @DataProvider
    public Iterator<Object[]> getInputs()
    {
        final List<Object[]> list = Lists.newArrayList();

        JsonNode reference;

        for (final JsonNode element: testData) {
            reference = element.get("reference");
            for (final JsonNode node: element.get("equivalences"))
                list.add(new Object[]{reference, node});
        }

        return list.iterator();
    }

    @Test(dataProvider = "getInputs")
    public void numericEqualityIsAcknowledged(final JsonNode reference,
        final JsonNode node)
    {
        assertTrue(JsonNumEquals.getInstance().equivalent(reference, node));
    }

    @Test(dataProvider = "getInputs")
    public void numericEqualityWorksWithinArrays(final JsonNode reference,
        final JsonNode node)
    {
        final ArrayNode node1 = FACTORY.arrayNode();
        node1.add(reference);
        final ArrayNode node2 = FACTORY.arrayNode();
        node2.add(node);

        assertTrue(JsonNumEquals.getInstance().equivalent(node1, node2));
    }

    @Test(dataProvider = "getInputs")
    public void numericEqualityWorksWithinObjects(final JsonNode reference,
        final JsonNode node)
    {
        final ObjectNode node1 = FACTORY.objectNode();
        node1.put("foo", reference);
        final ObjectNode node2 = FACTORY.objectNode();
        node2.put("foo", node);

        assertTrue(JsonNumEquals.getInstance().equivalent(node1, node2));
    }
}
