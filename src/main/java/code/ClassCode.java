package code;

import java.util.List;

/**
 * 类级别代码
 * @author QiuKai
 */
public interface ClassCode extends LineCode{
	
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
