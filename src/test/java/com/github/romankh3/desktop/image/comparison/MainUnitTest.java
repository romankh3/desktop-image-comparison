package com.github.romankh3.desktop.image.comparison;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Unit-level testing for {@link Main}.
 */
public class MainUnitTest {

    @Test
    public void testMainCreation() {
        //when
        Main main = new Main();

        //then
        assertNotNull(main);
    }

}
