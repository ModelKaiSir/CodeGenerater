package code.template;

import code.ClassCode;
import java.util.List;

/**
 *  模板
 * @author QiuKai
 */
public interface CodeTemplate {

    /**
     *
     * 获得Class
     * @return
     */
    List<ClassCode> toClass();

    /**
     *
     * 初始化
     * @param packages
     * @param className
     * @param tableName
     * @param columns
     */
    void init(String packages, String className, String tableName, List<Column> columns);
}
