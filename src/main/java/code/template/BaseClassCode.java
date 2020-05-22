package code.template;

import code.ClassCode;
import code.CodeUtil;
import code.LineCode;
import code.LineCode.SimpleCode;
import com.google.common.collect.Lists;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class BaseClassCode implements ClassCode {

    protected LineCode.TypeCode[] interfaces;
    protected TypeCode extend;

    protected List<LineCode> codes = new ArrayList<>();
    protected List<LineCode> imports = new ArrayList<>();

    protected boolean statics = false;

    public BaseClassCode() {

        this(false);
    }

    public BaseClassCode(boolean statics) {
        this.statics = statics;
    }

    @Override
    public void init(LineCode.TypeCode extend, LineCode.TypeCode[] interfaces) {

        this.extend = extend;
        this.interfaces = interfaces;

        imports.add(SimpleCode.packaged(getPackage()));

        if (null != extend) {
            addImports(extend);
        }

        if (null != interfaces) {

            for (int i = 0, j = interfaces.length; i < j; i++) {
                addImports(interfaces[i]);
            }
        }
    }

    @Override
    public void addCode(LineCode code) {

        codes.add(code);
    }

    public void addAllCode(List<LineCode> codes) {

        this.codes.addAll(codes);
    }

    protected List<LineCode> getResult() {

        List<LineCode> result = new ArrayList<>();

        if (!imports.isEmpty()) {

            result.addAll(imports);
            result.add(SimpleCode.BREAKLINE);
        }

        String code = "";
        String classTag = statics ? "static class" : "class";
        String header = "%s %s %s{";
        String footer = "}";

        if (null != extend && null != interfaces) {

            header = "%s %s %s extends %s implements %s{";
            code = String.format(header, CodeUtil.getScope(Modifier.PUBLIC), classTag, getName());
        } else if (null != extend) {

            header = "%s %s %s extends %s {";
            code = String.format(header, CodeUtil.getScope(Modifier.PUBLIC), classTag, getName(), extend);
        } else if (null != interfaces) {

            header = "%s %s %s implements %s{";
            code = String.format(header, CodeUtil.getScope(Modifier.PUBLIC), classTag, getName(), CodeUtil.JOIN_COMMA.join(interfaces));
        } else {

            code = String.format(header, CodeUtil.getScope(Modifier.PUBLIC), classTag, getName());
        }

        result.add(SimpleCode.of(code));
        result.addAll(codes);
        result.add(SimpleCode.of(footer));
        return result;
    }

    @Override
    public String toString() {

        // scope class name [extends parent] [implements interfaces]
        return CodeUtil.LINES.join(getResult());
    }

    protected void addImports(LineCode.TypeCode typeCode) {

        if (typeCode.hasClassName()) {

            imports.add(SimpleCode.imported(typeCode.getClassName()));
        }
    }

    protected void addImports(SimpleCurdType type) {

        addImports(type.get());
    }
}
