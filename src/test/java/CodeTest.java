import code.ClassCode;
import code.template.SimpleCurdFunctionMain;
import code.template.SimpleCurdTemplate;
import code.template.SimpleCurdType;
import org.junit.Test;

public class CodeTest {

    @Test
    public void function(){

        SimpleCurdFunctionMain main = new SimpleCurdFunctionMain("com.qiukai.test", "test");
        main.init(SimpleCurdType.APP_FUNCTION_MAIN.get(), null);

        System.out.println(main);
    }

    @Test
    public void functionMain(){

        SimpleCurdFunctionMain t = new SimpleCurdFunctionMain("com.qiukai.test", "test");
        t.init(SimpleCurdType.APP_FUNCTION_MAIN.get(), null);

        System.out.println(t);
    }

    @Test
    public void template() {

        SimpleCurdTemplate t = new SimpleCurdTemplate();
        t.init("com.qiukai.test", "com.qiukai.test.Test.class", "test", null);

        for (ClassCode classCode: t.toClass()){

            System.out.println(classCode);
        }
    }
}
