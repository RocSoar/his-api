import com.roc.his.api.mis.records.request.TestClass;
import com.roc.his.api.mis.records.request.TestRecord;
import org.junit.jupiter.api.Test;

public class CommonTest {

    @Test
    public void test1() {
        TestRecord testRecord = new TestRecord("roc", null);
        System.out.println(testRecord);
    }

    @Test
    public void test2() {
        TestClass testClass = new TestClass("roc", null);
        System.out.println(testClass);
    }
}
