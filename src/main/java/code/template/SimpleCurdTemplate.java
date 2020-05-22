package code.template;

import code.ClassCode;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 简单的CURD实现
 *
 * @author QiuKai
 */
public class SimpleCurdTemplate implements CodeTemplate {

    private SimpleCurdFunction curdFunction;

    @Override
    public List<ClassCode> toClass() {
        return Lists.newArrayList(curdFunction);
    }

    @Override
    public void init(String packages, String className, String tableName, List<Column> columns) {

        curdFunction = new SimpleCurdFunction(packages, tableName);
        curdFunction.init(SimpleCurdType.APP_FUNCTION.get(), null);
    }

    public static void main(String[] args) {

        SimpleCurdTemplate t = new SimpleCurdTemplate();
        t.init("com.qiukai.test", "com.qiukai.test.Test.class", "test", null);

        for (ClassCode classCode: t.toClass()){

            System.out.println(classCode);
        }
    }

}
