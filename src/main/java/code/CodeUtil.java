package code;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeUtil {

	public static final Pattern PATTERN = Pattern.compile("[A-Z][a-z]+");

	public static final String SYMBOL_TAB = "\t";
	public static final String SYMBOL_END_CODE = ";";

	public static final String METHOD_START = "{\n";
	public static final String METHOD_END = "\n}";

	public static final Joiner JOIN = Joiner.on("");
	public static final Joiner JOIN_COMMA = Joiner.on(",");
	public static final Joiner EXCEPTIONS = Joiner.on(" | ");
	public static final Joiner LINES = Joiner.on("\n");

	public static final CharMatcher CLASS = CharMatcher.is('$');

	public static String toStringValue(Object parameter) {

		return String.format("\"%s\"", parameter);
	}

	public static String getScope(int modifier) {

		String scope = "";

		if (modifier == -1) {
			return scope;
		}
		if (Modifier.isPublic(modifier)) {
			scope = "public";
		} else if (Modifier.isProtected(modifier)) {
			scope = "protected";
		} else if (Modifier.isPrivate(modifier)) {
			scope = "private";
		}

		return scope;
	}

	public static String getName(Class<?> type) {

		return CLASS.replaceFrom(type.getName(), ".");
	}

	/**
	 * 
	 * 格式化为驼峰命名法，首字母小写
	 * 
	 * @param name
	 * @return
	 */
	public static String formatLower(String name) {
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
	}

	/**
	 * 
	 * 格式化为驼峰命名法，首字母大写
	 * 
	 * @param name
	 * @return
	 */
	public static String formatUpper(String name) {
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
	}

	public static String toUnderScore(String str) {

		Matcher m = PATTERN.matcher(str);
		Joiner join = Joiner.on("_");

		ArrayList<String> data = new ArrayList<String>();

		boolean any = false;

		while (m.find()) {
			any = true;
			data.add(m.group());
		}

		if(!any){
			return str;
		}

		return join.join(data);
	}

	public static void imports(HashSet<String> imports) {

		String format = "import %s;";
		
		if (null != imports) {

			for (String name : imports) {
				System.out.println(String.format(format, name));
			}
		}
	}
	
	public static void main(String[] args) {

	}
}
