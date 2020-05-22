package code;

import java.util.List;

public interface ClassCode extends LineCode{
	
	public static final String REPLACE_CODES = "[CODES]";
	public static final String REPLACE_INNTERCLASS = "[INNTERCLASS]";
	public static final String FORMAT_EXTENDS = " extends %s";
	public static final String FORMAT_IMPLEMENTS = " implements %s";
	public static final String FORMAT_CLASS = "public class %s {%n"+REPLACE_CODES+"%n"+REPLACE_INNTERCLASS+"%n}";

	/**
	 * 初始化
	 * @param extend
	 * @param interfaces
	 */
	void init(LineCode.TypeCode extend, LineCode.TypeCode[] interfaces);

	/**
	 *
	 * Class名称
	 * @return
	 */
	String getName();

	/**
	 * 包名
	 * @return
	 */
	String getPackage();

	/**
	 * 需要导入的包
	 * @return
	 */
	List<LineCode> getImports();

	/**
	 *
	 * 添加代码
	 * @param code
	 */
	void addCode(LineCode code);
}
