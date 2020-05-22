package code.template;

import code.CodeUtil;
import code.LineCode;
import com.sun.org.apache.bcel.internal.classfile.Code;

import javax.print.attribute.standard.MediaSize;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class SimpleCurdFunctionMain extends BaseClassCode {

    protected CodeBuilder builder = new CodeBuilder();

    private String packages;
    private String name;
    private LineCode.FieldCode messageLocal = LineCode.FieldCode.protect("messageLocal", SimpleCurdType.MESSAGE_LOCAL.get()).newInstance(null);

    public SimpleCurdFunctionMain(String packages, String name) {

        this.packages = packages;
        this.name = name;
    }

    protected void init() {

        LineCode.MethodCode init = LineCode.MethodCode.create(Modifier.PUBLIC, null, "init", LineCode.TypeCode.Void, new LineCode.TypeCode[]{ SimpleCurdType.APP_PARAMETER.get(), SimpleCurdType.FUNCTION_PARAMETER.get() }, null).override();
        LineCode.MethodCode superInit = LineCode.MethodCode.create(Modifier.PUBLIC, null, "init", LineCode.TypeCode.Void, new LineCode.TypeCode[]{ SimpleCurdType.APP_PARAMETER.get(), SimpleCurdType.FUNCTION_PARAMETER.get(), SimpleCurdType.MESSAGE_LOCAL.get() }, null).override();

        init.addBody(superInit.callSuper());

        TypeCode exception = TypeCode.typeOf(java.lang.Exception.class);
        TryCatch tryCatch = TryCatch.of(new TypeCode[]{ exception });
        tryCatch.addCode(generateTabularForm().call());
        tryCatch.catchError();

        init.addAllBody(tryCatch.create().stream().map(i -> builder.set(i).start().create()).collect(Collectors.toList()));
        addAllCode(init.create());
        addCode(SimpleCode.BREAKLINE);

        addImports(SimpleCurdType.APP_PARAMETER);
        addImports(SimpleCurdType.FUNCTION_PARAMETER);
    }

    protected MethodCode generateTabularForm(){

        MethodCode generateTabularForm = MethodCode.create(Modifier.PROTECTED, null, "generateTabularForm", TypeCode.Void, new TypeCode[]{SimpleCurdType.APP_PARAMETER.get()}, new TypeCode[]{TypeCode.typeOf(Exception.class)});
        return generateTabularForm;
    }

    @Override
    public void init(TypeCode extend, TypeCode[] interfaces) {

        super.init(extend, interfaces);

        //fields
        addCode(builder.set(messageLocal).start().end().create());
        addCode(LineCode.SimpleCode.BREAKLINE);
        init();
    }

    @Override
    public String getName() {
        return CodeUtil.formatUpper(name + "_function_main");
    }

    @Override
    public String getPackage() {
        return packages;
    }

    @Override
    public List<LineCode> getImports() {
        return imports;
    }

    public static void main(String[] args) {

        SimpleCurdFunctionMain t = new SimpleCurdFunctionMain("com.qiukai.test", "test");
        t.init(SimpleCurdType.APP_FUNCTION_MAIN.get(), null);

        System.out.println(t);
    }
}
