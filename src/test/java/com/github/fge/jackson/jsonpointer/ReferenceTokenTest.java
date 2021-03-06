/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jackson.jsonpointer;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;

import static com.github.fge.jackson.jsonpointer.JsonPointerMessages.*;
import static org.testng.Assert.*;

public final class ReferenceTokenTest
{
    @Test
    public void nullCookedRaisesError()
        throws JsonPointerException
    {
        try {
            ReferenceToken.fromCooked(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), NULL_INPUT);
        }
    }
    @Test
    public void nullRawRaisesError()
    {
        try {
            ReferenceToken.fromRaw(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), NULL_INPUT);
        }
    }

    @Test
    public void emptyEscapeRaisesTheAppropriateException()
    {
        try {
            ReferenceToken.fromCooked("whatever~");
            fail("No exception thrown!!");
        } catch (JsonPointerException e) {
            assertEquals(e.getMessage(), EMPTY_ESCAPE);
        }
    }

    @Test
    public void illegalEscapeRaisesTheAppropriateException()
    {
        try {
            ReferenceToken.fromCooked("~a");
            fail("No exception thrown!!");
        } catch (JsonPointerException e) {
            assertEquals(e.getMessage(), ILLEGAL_ESCAPE);
        }
    }

    @DataProvider
    public Iterator<Object[]> cookedRaw()
    {
        return ImmutableList.of(
            new Object[] { "~0", "~" },
            new Object[] { "~1", "/" },
            new Object[] { "", "" },
            new Object[] { "~0user", "~user" },
            new Object[] { "foobar", "foobar" },
            new Object[] { "~1var~1lib~1mysql", "/var/lib/mysql" }
        ).iterator();
    }

    @Test(dataProvider = "cookedRaw")
    public void fromCookedOrFromRawYieldsSameResults(final String cooked,
        final String raw)
        throws JsonPointerException
    {
        final ReferenceToken token1 = ReferenceToken.fromCooked(cooked);
        final ReferenceToken token2 = ReferenceToken.fromRaw(raw);

        assertTrue(token1.equals(token2));
        assertEquals(token2.toString(), cooked);
    }

    @DataProvider
    public Iterator<Object[]> indices()
    {
        return ImmutableList.of(
            new Object[] { 0, "0" },
            new Object[] { -1, "-1" },
            new Object[]{ 13, "13" }
        ).iterator();
    }

    @Test(dataProvider = "indices")
    public void fromIndexOrStringYieldsSameResults(final int index,
        final String asString)
        throws JsonPointerException
    {
        final ReferenceToken fromInt = ReferenceToken.fromInt(index);
        final ReferenceToken cooked = ReferenceToken.fromCooked(asString);
        final ReferenceToken raw = ReferenceToken.fromRaw(asString);

        assertTrue(fromInt.equals(cooked));
        assertTrue(cooked.equals(raw));
        assertTrue(raw.equals(fromInt));

        assertEquals(fromInt.toString(), asString);
    }

    @Test
    public void zeroAndZeroZeroAreNotTheSame()
        throws JsonPointerException
    {
        final ReferenceToken zero = ReferenceToken.fromCooked("0");
        final ReferenceToken zerozero = ReferenceToken.fromCooked("00");

        assertFalse(zero.equals(zerozero));
    }
}
