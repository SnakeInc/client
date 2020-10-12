import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class HelloWorldTest {

    HelloWorld toTest = new HelloWorld();

    @Test
    void isDummy() {
        assertFalse(toTest.isDummy());
    }

    @Test
    void setDummy() {
        toTest.setDummy(false);
        assertFalse(toTest.isDummy());
    }
}