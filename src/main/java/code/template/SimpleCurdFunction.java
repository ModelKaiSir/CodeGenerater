package code.template;

import code.ClassCode;
import code.CodeUtil;
import code.LineCode;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author QiuKai
 */
public class SimpleCurdFunction extends BaseClassCode {

    private String packages;
    private String name;

    private LineCode.CodeBuilder builder = new LineCode.CodeBuilder();

    private LineCode.FieldCode messageLocal = LineCode.FieldCode.protect("messageLocal", SimpleCurdType.MESSAGE_LOCAL.get()).newInstance(null);

    public void init() {

        LineCode.MethodCode init = LineCode.MethodCode.create(Modifier.PUBLIC, null, "init", LineCode.TypeCode.Void, new LineCode.TypeCode[]{ SimpleCurdType.APP_PARAMETER.get(), SimpleCurdType.FUNCTION_PARAMETER.get() }, null).override();
        LineCode.MethodCode superInit = LineCode.MethodCode.create(Modifier.PUBLIC, null, "init", LineCode.TypeCode.Void, new LineCode.TypeCode[]{ SimpleCurdType.APP_PARAMETER.get(), SimpleCurdType.FUNCTION_PARAMETER.get(), SimpleCurdType.MESSAGE_LOCAL.get() }, null).override();

        init.addBody(superInit.callSuper());
        init.addBody(builder.set(LineCode.SimpleCode.of("setCaption(functionParameter.getName())")).start().end().create());

        addCode(SimpleCode.lines(init.create()));
        addCode(SimpleCode.BREAKLINE);

        addImports(SimpleCurdType.APP_PARAMETER);
        addImports(SimpleCurdType.FUNCTION_PARAMETER);
    }

    public void createMain() {

        String functionMainName = CodeUtil.formatUpper(getFunctionMainName());
        LineCode.TypeCode functionMainType = LineCode.TypeCode.typeOf(functionMainName).className(getPackage() + "." + functionMainName);
        LineCode.FieldCode functionMain = LineCode.FieldCode.create(-1, CodeUtil.formatLower(getFunctionMainName()), functionMainType).newInstance(null);

        LineCode.MethodCode createMain = LineCode.MethodCode.create(Modifier.PUBLIC, null, "createMain", SimpleCurdType.FUNCTION_MAIN.get(), null, null).override();
        LineCode.MethodCode setFunctionRequestListener = LineCode.MethodCode.create(Modifier.PUBLIC, functionMain, "setFunctionRequestListener", LineCode.TypeCode.Void, new LineCode.TypeCode[]{ LineCode.TypeCode.This }, null).override();

        createMain.addBody(builder.set(functionMain).start().end().create());
        createMain.addBody(setFunctionRequestListener.call());
        createMain.addBody(new LineCode.CodeBuilder(LineCode.SimpleCode.of("return " + functionMain.getName())).start().end().create());

        addCode(SimpleCode.lines(createMain.create()));
        addCode(SimpleCode.BREAKLINE);
        addImports(SimpleCurdType.FUNCTION_MAIN);
        addImports(functionMainType);
    }

    public void createLayout() {


        LineCode.FieldCode layout = LineCode.FieldCode.create(-1, "layout", SimpleCurdType.NON_NAVIGATOR_FUNCTION_LAYOUT.get()).newInstance(null);

        LineCode.MethodCode createLayout = LineCode.MethodCode.create(Modifier.PUBLIC, null, "createLayout", SimpleCurdType.FUNCTION_LAYOUT.get(), null, null).override();
        createLayout.addBody(builder.set(layout.newInstance(null)).start().end().create());
        createLayout.addBody(new LineCode.CodeBuilder(LineCode.SimpleCode.of("return layout")).start().end().create());

        addCode(SimpleCode.lines(createLayout.create()));
        addCode(SimpleCode.BREAKLINE);
        addImports(SimpleCurdType.FUNCTION_LAYOUT);
        addImports(SimpleCurdType.NON_NAVIGATOR_FUNCTION_LAYOUT);
    }

    public SimpleCurdFunction(String packages, String name) {

        this.packages = packages;
        this.name = name;
    }

    @Override
    public void init(LineCode.TypeCode extend, LineCode.TypeCode[] interfaces) {

        super.init(extend, interfaces);

        //fields
        addCode(builder.set(messageLocal).start().end().create());
        addCode(LineCode.SimpleCode.BREAKLINE);

        //methods
        init();
        createLayout();
        createMain();

        //innerClass
        MessageLocalClassCode inner = new MessageLocalClassCode(this);
        inner.init(SimpleCurdType.VADDIN_MESSAGE_LOCAL.get(), null);
        inner.getImports();
        addCode(inner);
    }

    @Override
    public String getName() {
        return CodeUtil.formatUpper(getFunctionName());
    }

    public String getFunctionName() {

        return name + "_Function";
    }

    public String getFunctionMainName() {

        return name + "_Function_Main";
    }

    @Override
    public String getPackage() {
        return packages;
    }

    @Override
    public List<LineCode> getImports() {
        return imports;
    }

    static class MessageLocalClassCode extends InnerClassCode {

        private CodeBuilder builder = new CodeBuilder();

        public MessageLocalClassCode(ClassCode parent) {
            super(true, parent, SimpleCurdType.MESSAGE_LOCAL.getTypeName());
        }

        public MethodCode constructor(String name) {

            TypeCode javaUtilType = SimpleCurdType.JAVA_UTIL.get();

            MethodCode result = MethodCode.create(Modifier.PUBLIC, null, name, TypeCode.NONE, null, null);
            result.addBody(builder.set(SimpleCode.of("new JavaUtil().super(\"\");")).start().create());

            addImports(javaUtilType);
            return result;
        }

        @Override
        public void init(TypeCode extend, TypeCode[] interfaces) {

            super.init(extend, interfaces);
            TypeCode captionType = TypeCode.typeOf(String.class);
            FieldCode caption = FieldCode.create(Modifier.PUBLIC, "CAPTION", captionType);
            ValueCode captionValue = ValueCode.stringOf(captionType, "结算功能");

            List<LineCode> codes = constructor(getName()).create();
            LineCode start = codes.get(0);

            addCode(SimpleCode.lines(codes.stream()
                    .map(i -> i == start ? i : builder.set(i).start().create())
                    .collect(Collectors.toList())));

            addCode(SimpleCode.BREAKLINE);
            addCode(builder.set(caption.setValue(captionValue)).start().end().create());
        }
    }
}
