package code.template;

import code.ClassCode;
import code.CodeUtil;
import code.LineCode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 内部类
 * @author QiuKai
 */
public class InnerClassCode extends BaseClassCode{

    private ClassCode parent;
    private String name;

    public InnerClassCode(boolean statics, ClassCode parent, String name) {

        super(statics);
        this.parent = parent;
        this.name = name;
    }

    public InnerClassCode(ClassCode parent, String name) {

        this(false, parent, name);
    }

    @Override
    public void init(TypeCode extend, TypeCode[] interfaces) {

        this.extend = extend;
        this.interfaces = interfaces;

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
    public String getName() {
        return name;
    }

    /**
     *
     * 内部不需要package
     * @return
     */
    @Override
    public String getPackage() {
        return null;
    }

    /**
     *
     * 内部类不需要import, 交给父类import
     * @return
     */
    @Override
    public List<LineCode> getImports() {

        //由父类import
        parent.getImports().addAll(imports);
        imports.clear();
        return imports;
    }

    @Override
    public String toString() {

        return CodeUtil.LINES.join(getResult().stream().map(i -> new CodeBuilder(i).start().create()).collect(Collectors.toList()));
    }
}
