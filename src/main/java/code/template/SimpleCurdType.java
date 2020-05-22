package code.template;

import code.LineCode;
import code.LineCode.TypeCode;

/**
 * 通用的一些TypeCode
 *
 * @author QiuKai
 */
public enum SimpleCurdType {

    /**
     * 翻译类
     */
    MESSAGE_LOCAL("MessageLocal") {
        @Override
        public TypeCode get() {
            return typeCode();
        }
    },
    APP_FUNCTION("AppFunction") {
        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.espos.common.AppFunction");
        }
    }, MALL_APP_FUNCTION("MallAppFunction") {
        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.espos61.mis.mall.common.MallAppFunction");
        }
    }, APP_PARAMETER("AppParameter") {
        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.framework.AppParameter");
        }
    }, FUNCTION_PARAMETER("FunctionParameter") {
        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.framework.FunctionParameter");
        }
    }, FUNCTION_LAYOUT("FunctionLayout<?>") {
        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.framework.FunctionLayout");
        }
    }, FUNCTION_MAIN("FunctionMain<?>") {
        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.framework.FunctionMain");
        }
    }, FUNCTION_NAVIGATOR("FunctionNavigator<?>") {
        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.framework.FunctionNavigator");
        }
    },
    APP_FUNCTION_NAVIGATOR("AppFunctionNavigator") {
        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.espos.common.AppFunctionNavigator");
        }
    },
    APP_FUNCTION_MAIN("AppFunctionMain") {
        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.ui.function.NonNavigatorFunctionLayout");
        }
    }, NON_NAVIGATOR_FUNCTION_LAYOUT("NonNavigatorFunctionLayout") {
        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.ui.function.NonNavigatorFunctionLayout");
        }
    }, VADDIN_MESSAGE_LOCAL("VaddinMessageLocal") {

        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.espos61.mis.mall.common.JavaUtil.VaddinMessageLocal");
        }
    }, JAVA_UTIL("JavaUtil") {

        @Override
        public TypeCode get() {
            return classType("com.techtrans.vaadin.espos61.mis.mall.common.JavaUtil");
        }
    };

    private String typeName;

    SimpleCurdType(String typeName) {

        this.typeName = typeName;
    }

    public abstract TypeCode get();

    public String getTypeName() {
        return typeName;
    }

    TypeCode typeCode() {
        return TypeCode.typeOf(getTypeName());
    }

    TypeCode classType(String className) {
        return TypeCode.typeOf(getTypeName()).className(className);
    }
}
