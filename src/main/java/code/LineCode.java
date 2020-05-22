package code;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 一行代码
 *
 * @author QiuKai
 */
public interface LineCode {

    /**
     * 返回格式化代码
     *
     * @return
     */
    @Override
    String toString();

    public static class CodeBuilder {

        private LineCode code;

        private boolean start;
        private boolean end;

        private String tag;

        public CodeBuilder() {
        }

        public CodeBuilder(LineCode code) {
            this.code = code;
        }

        public CodeBuilder tag(String tag) {

            this.tag = tag;
            return this;
        }

        public CodeBuilder start() {

            start = true;
            return this;
        }

        public CodeBuilder end() {

            end = true;
            return this;
        }

        public CodeBuilder set(LineCode code) {

            this.code = code;
            this.tag = null;
            this.start = false;
            this.end = false;
            return this;
        }

        public LineCode create() {

            String format = "%s";
            if (start && end) {

                format = CodeUtil.SYMBOL_TAB + "%s" + CodeUtil.SYMBOL_END_CODE;
            } else if (start) {

                format = CodeUtil.SYMBOL_TAB + "%s";
            } else if (end) {

                format = "%s" + CodeUtil.SYMBOL_END_CODE;
            }

            //if tag
            if (Objects.isNull(tag)) {
                return new SimpleCode(String.format(format, code));
            } else {
                return new SimpleCode(tag + String.format(format, code));
            }
        }

        public CodeBuilder extend(CodeBuilder builder) {

            this.code = builder.create();
            this.tag = null;
            this.start = false;
            this.end = false;
            return this;
        }
    }

    /**
     * <p>用来描述代码中字段的类型，如基本类型String int ..</p>
     * <p>其他类型 Date ..</p>
     */
    public static class TypeCode implements LineCode {

        public static TypeCode Void = TypeCode.typeOf("void");
        public static TypeCode This = TypeCode.typeOf("this");
        public static TypeCode NONE = TypeCode.typeOf("");

        private String type;
        private String generic;
        private String className;
        private boolean classes;

        public TypeCode(String type, String generic) {

            this.type = type;
            this.generic = generic;
        }

        public TypeCode className(String className) {

            this.className = className;
            this.classes = true;
            return this;
        }

        public String getType() {
            return type;
        }

        public String getActualType() {
            return null != generic ? generic : type;
        }

        public String getClassName() {

            return className;
        }

        public boolean hasClassName() {
            return classes;
        }

        @Override
        public String toString() {

            return type;
        }

        public static TypeCode typeOf(String type) {

            return new TypeCode(type, null);
        }

        public static TypeCode typeOf(Class<?> type) {

            return new TypeCode(type.getSimpleName(), null).className(type.getName());
        }

        public static <T extends Collection> TypeCode listOf(T list, String type) {

            return new TypeCode(list.getClass().getSimpleName(), type);
        }
    }

    /**
     * 用来描述变量的值
     */
    public static class ValueCode implements LineCode {

        private TypeCode type;
        private Object value;

        public ValueCode(TypeCode type, Object value) {

            this.type = type;
            this.value = value;
        }

        public TypeCode getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {

            return String.format("%s", value);
        }

        public static ValueCode of(TypeCode type, Object value) {

            return new ValueCode(type, value);
        }

        public static ValueCode stringOf(TypeCode type, Object value) {

            return new ValueCode(type, value) {

                @Override
                public String toString() {
                    return String.format("\"%s\"", value);
                }
            };
        }
    }

    /**
     * LineCode的默认实现。
     */
    public static class SimpleCode implements LineCode {

        public static final SimpleCode BREAKLINE = SimpleCode.of("\r");

        private String code;

        private SimpleCode(String code) {
            this.code = code;
        }

        @Override
        public String toString() {

            return code;
        }

        public static SimpleCode of(String code) {
            return new SimpleCode(code);
        }

        public static LineCode imported(String className) {

            return new CodeBuilder(new SimpleCode(className)).tag("import ").end().create();
        }

        public static LineCode packaged(String aPackage) {

            return new CodeBuilder(new SimpleCode(aPackage)).tag("package ").end().create();
        }

        public static LineCode lines(List<LineCode> codes) {

            return SimpleCode.of(CodeUtil.LINES.join(codes));
        }
    }

    /**
     * 字段属性， 需要的参数为 作用域 类型 名称
     *
     * @author QiuKai
     */
    public static class FieldCode implements LineCode {

        private int modifier = Modifier.PROTECTED;

        private String name;
        private TypeCode type;
        private ValueCode value;

        private boolean instance;
        private boolean instanceByParameter;
        private boolean valueOf;

        private FieldCode[] parameters;

        private FieldCode(int modifier, String name, TypeCode type) {

            this.modifier = modifier;
            this.type = type;
            this.name = name;
        }

        private FieldCode(String name, TypeCode type) {

            this(Modifier.PROTECTED, name, type);
        }

        public FieldCode newInstance(FieldCode[] parameters) {

            if (null == parameters) {

                instance = true;
            } else {

                this.parameters = parameters;
                instanceByParameter = true;
            }

            return this;
        }

        public FieldCode setValue(ValueCode value) {

            this.value = value;
            valueOf = true;
            return this;
        }

        public LineCode call(String code) {
            return SimpleCode.of(String.format("%s.%s", getName(), code));
        }

        public String getName() {
            return name;
        }

        public TypeCode getType() {
            return type;
        }

        @Override
        public String toString() {

            // scope Type name
            String format = "%s %s %s";
            String modifiers = CodeUtil.getScope(modifier);
            String code = String.format(format, modifiers, type, name);

            if (instance) {

                //scope Type name = new Type()
                format = "%s %s %s = new %s()";
                code = String.format(format, modifiers, type, name, type);
            } else if (instanceByParameter) {

                //scope Type name = new Type({arg0, arg1, argN})
                String args = Stream.of(parameters).map(p -> p.getName()).collect(Collectors.joining(","));

                format = "%s %s %s = new %s(%s)";
                code = String.format(format, modifiers, type, name, type, args);
            } else if (valueOf) {

                code += " = " + value;
            }

            return code.trim();
        }

        public static FieldCode create(int modifier, String name, TypeCode type) {

            return new FieldCode(modifier, name, type);
        }

        public static FieldCode protect(String name, TypeCode type) {

            return new FieldCode(name, type);
        }
    }

    /**
     * if(statementBy){ codes... }else if(){ codes... }else{ codes... }
     *
     * @author QiuKai
     */
    public static class IfElse implements LineCode {

        static final String TEMPLATE = "if (%s) {\n%s\n}";
        static final String TEMPLATE_ELSE = " else {\n%s\n}";
        static final String TEMPLATE_ELSE_IF = " else if (%s) {\n%s\n}";

        private List<LineCode> appends = new ArrayList<LineCode>();

        private LineCode statementBy;
        private List<LineCode> codeBlock;

        private boolean ifer;
        private boolean elser;
        private boolean elseIfer;

        private boolean done;

        private IfElse(LineCode statementBy, List<LineCode> codeBlock) {

            this.statementBy = statementBy;
            this.codeBlock = codeBlock;
        }

        public IfElse appendElse(List<LineCode> codeBlock) {

            if (done) {

                throw new RuntimeException("is end else.", new IllegalStateException("已到条件判断语句结尾！"));
            }

            appends.add(IfElse.elser(codeBlock));
            done = true;
            return this;
        }

        public IfElse appendElse(LineCode... codeBlock) {

            return this.appendElse(ImmutableList.<LineCode>copyOf(codeBlock));
        }

        public IfElse appendElseIf(LineCode statementBy, List<LineCode> codeBlock) {

            if (done) {

                throw new RuntimeException("is end else.", new IllegalStateException("已到条件判断语句结尾！"));
            } else {

                appends.add(IfElse.elseIfer(statementBy, codeBlock));
            }

            return this;
        }

        public IfElse appendElseIf(LineCode statementBy, LineCode... codeBlock) {

            return this.appendElseIf(statementBy, ImmutableList.<LineCode>copyOf(codeBlock));
        }

        @Override
        public String toString() {

            String append = "";

            if (!appends.isEmpty()) {
                append = CodeUtil.JOIN.join(appends);
            }

            if (ifer) {

                return String.format(TEMPLATE, statementBy, CodeUtil.LINES.join(codeBlock)) + append;
            } else if (elseIfer) {
                return String.format(TEMPLATE_ELSE_IF, statementBy, CodeUtil.LINES.join(codeBlock)) + append;
            } else if (elser) {
                return String.format(TEMPLATE_ELSE, CodeUtil.LINES.join(codeBlock)) + append;
            } else {
                return super.toString();
            }
        }

        public static IfElse ifer(LineCode statementBy, List<LineCode> codeBlock) {

            IfElse result = new IfElse(statementBy, codeBlock);
            result.ifer = true;
            return result;
        }

        public static IfElse ifer(LineCode statementBy, LineCode... codeBlock) {

            return ifer(statementBy, ImmutableList.<LineCode>copyOf(codeBlock));
        }

        public static IfElse elser(List<LineCode> codeBlock) {

            IfElse result = new IfElse(null, codeBlock);
            result.elser = true;
            return result;
        }

        public static IfElse elser(LineCode... codeBlock) {

            return elser(ImmutableList.<LineCode>copyOf(codeBlock));
        }

        public static IfElse elseIfer(LineCode statementBy, List<LineCode> codeBlock) {

            IfElse result = new IfElse(statementBy, codeBlock);
            result.elseIfer = true;
            return result;
        }

        public static IfElse elseIfer(LineCode statementBy, LineCode... codeBlock) {

            return elseIfer(statementBy, ImmutableList.<LineCode>copyOf(codeBlock));
        }
    }

    /**
     * 将多行代码包括在TryCatch中 Try{ codes... }catch(Exception e){ e.print Or notify }
     *
     * @author QiuKai
     */
    public static class TryCatch implements LineCode {

        static final LineCode NOTIFYFATAL = SimpleCode.of("notify.fatal(caption, e.getMessage(), e)");
        static final LineCode PRINTSTACKTRACE = SimpleCode.of("e.printStackTrace()");

        private ArrayList<LineCode> codes = new ArrayList<>();
        private List<LineCode> finallyCodes = new ArrayList<>();

        private LineCode resourceCode = null;
        private LineCode catchCode = null;
        private LineCode errorCode = null;

        private boolean finallys;
        private TypeCode[] catchException;


        private TryCatch() {

        }

        public void addCode(LineCode code) {
            this.codes.add(code);
        }

        public void addFinallyCode(LineCode code) {
            this.finallyCodes.add(code);
        }


        public void catchError() {

            catchCode = PRINTSTACKTRACE;
        }

        public void catchError(LineCode code) {

            catchCode = code;
        }

        public List<LineCode> create() {

            CodeBuilder builder = new CodeBuilder();

            List<LineCode> result = new ArrayList<>();

            String exceptions = CodeUtil.JOIN_COMMA.join(catchException);

            if (null == resourceCode) {
                result.add(SimpleCode.of("try{"));
            } else {
                result.add(SimpleCode.of(String.format("try(%s){", resourceCode)));
            }

            result.addAll(codes.stream().map(i -> builder.set(i).start().create()).collect(Collectors.toList()));
            result.add(SimpleCode.of(String.format("}catch(%s e){", exceptions)));
            result.add(builder.set(catchCode).start().end().create());
            result.add(SimpleCode.of("}"));

            if (finallys) {
                result.add(SimpleCode.of("finally{"));
                result.addAll(finallyCodes);
                result.add(SimpleCode.of("}"));
            }
            return result;
        }

        public static TryCatch of(TypeCode[] exceptionTypes) {

            TryCatch result = new TryCatch();
            result.catchException = exceptionTypes;
            return result;
        }

        /**
         * try(Resource r = new Resource()){ codes... }catch(Exception e){ ... }
         *
         * @param resource
         * @return
         */
        public static TryCatch tryWithResource(TypeCode[] exceptionTypes, LineCode resource) {

            TryCatch result = new TryCatch();
            result.resourceCode = resource;
            result.catchException = exceptionTypes;
            return result;
        }

    }

    public static class MethodCode implements LineCode {

        private FieldCode holder;
        private int modifier;
        private String name;

        private TypeCode returnType;
        private Optional<List<TypeCode>> parameterTypes = Optional.absent();
        private TypeCode[] throwsException;

        private List<LineCode> body = new ArrayList<LineCode>();

        private boolean override;

        public MethodCode override() {

            override = true;
            return this;
        }

        public void addAllBody(List<LineCode> codes) {

            this.body.addAll(codes);
        }

        public void addBody(LineCode code) {

            this.body.add(code);
        }

        public List<LineCode> create() {

            CodeBuilder builder = new CodeBuilder();

            List<LineCode> result = new ArrayList<>();

            String format = "%s %s %s(%s){";
            String formatFoot = "}";

            String modifier = CodeUtil.getScope(this.modifier);
            String returnType = this.returnType.getType();
            String parameters = "";

            if (override) {

                result.add(builder.set(SimpleCode.of("@Override")).start().create());
            }

            if (parameterTypes.isPresent()) {

                parameters = parameterTypes
                        .get()
                        .stream()
                        .map(i ->
                                String.format("%s %s", i.getType(), CodeUtil.formatLower(CodeUtil.toUnderScore(i.getType()))))
                        .collect(Collectors.joining(","));
            }

            result.add(builder.set(SimpleCode.of(String.format(format, modifier, returnType, name, parameters)))
                    .start()
                    .create());

            if (null != body) {

                for (int i = 0, j = body.size(); i < j; i++) {

                    result.add(builder.set(body.get(i)).start().create());
                }
            }

            result.add(builder.set(SimpleCode.of(formatFoot)).start().create());
            return result;
        }

        public LineCode callThis() {

            return call("this.");
        }

        public LineCode callSuper() {

            return call("super.");
        }

        public LineCode call() {

            return call("");
        }

        private LineCode call(String THIS) {

            String parameters = "";
            CodeBuilder builder = new CodeBuilder();

            if (parameterTypes.isPresent()) {

                parameters = parameterTypes.get().stream().map(i -> CodeUtil.formatLower(CodeUtil.toUnderScore(i.getType()))).collect(Collectors.joining(","));
            }
            if (null == holder) {

                return builder.set(SimpleCode.of(THIS + String.format("%s(%s)", this.name, parameters))).start().end().create();
            } else {

                return builder.set(SimpleCode.of(String.format("%s.%s(%s)", holder.getName(), this.name, parameters))).start().end().create();
            }
        }

        public static MethodCode create(int modifier, FieldCode holder, String name, TypeCode rtType, TypeCode[] parameterTypes, TypeCode[] throwsExceptions) {

            MethodCode result = new MethodCode();
            result.modifier = modifier;
            result.holder = holder;
            result.name = name;
            result.returnType = rtType;

            if (null != parameterTypes) {

                result.parameterTypes = Optional.<List<TypeCode>>fromNullable(Lists.newArrayList(parameterTypes));
            }

            result.throwsException = throwsExceptions;
            return result;
        }
    }
}
