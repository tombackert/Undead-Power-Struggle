package ups.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing the Example class with its arbitrary arithmetical computations
 */
public class ExampleTest {
    /**
     * This tes case doesn't make any sense. It is just here as an example.
     */
    @Test
    public void foo() {
        Example example = new Example(23);
        assertEquals(42, example.foo(12), "foo performs a correct computation");
    }
}
