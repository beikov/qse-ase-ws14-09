package at.ac.tuwien.ase09.keycloak.theme;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.keycloak.freemarker.Theme;
import org.keycloak.freemarker.Theme.Type;
import org.keycloak.freemarker.ThemeProvider;

public class PortfolioThemeProvider implements ThemeProvider {

	private static final String THEME_NAME = "portfolio";

	@Override
	public void close() {
	}

	@Override
	public int getProviderPriority() {
		return 0;
	}

	@Override
	public Theme getTheme(String name, Type type) throws IOException {
		if (hasTheme(name, type)) {
			return new PortfolioTheme(name, type, getClass().getClassLoader());
		} else {
			return null;
		}
	}

	@Override
	public Set<String> nameSet(Type type) {
		switch (type) {
			case LOGIN:
			case ACCOUNT:
			case WELCOME:
				return new HashSet<>(Arrays.asList(THEME_NAME));
			default:
				return Collections.emptySet();
		}
	}

	@Override
	public boolean hasTheme(String name, Type type) {
		switch (type) {
			case LOGIN:
			case ACCOUNT:
			case WELCOME:
				return THEME_NAME.equals(name);
			default:
				break;
		}

		return false;
	}

	private static class PortfolioTheme implements Theme {

		private String name;
		private String parentName;
		private String importName;
		private Type type;
		private ClassLoader classLoader;
		private String templateRoot;
		private String resourceRoot;
		private String messages;
		private Properties properties;

		public PortfolioTheme(String name, Type type, ClassLoader classLoader)
				throws IOException {
			this.name = name;
			this.type = type;
			this.classLoader = classLoader;

			String themeRoot = "portfolio-theme/"
					+ type.toString().toLowerCase() + "/";

			this.templateRoot = themeRoot;
			this.resourceRoot = themeRoot + "resources/";
			this.messages = themeRoot + "messages/messages.properties";
			this.properties = new Properties();

			URL p = classLoader.getResource(themeRoot + "theme.properties");
			if (p != null) {
				properties.load(p.openStream());
				this.parentName = properties.getProperty("parent");
				this.importName = properties.getProperty("import");
			} else {
				this.parentName = null;
				this.importName = null;
			}
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getParentName() {
			return parentName;
		}

		@Override
		public String getImportName() {
			return importName;
		}

		@Override
		public Type getType() {
			return type;
		}

		@Override
		public URL getTemplate(String name) {
			return classLoader.getResource(templateRoot + name);
		}

		@Override
		public InputStream getTemplateAsStream(String name) {
			return classLoader.getResourceAsStream(templateRoot + name);
		}

		@Override
		public URL getResource(String path) {
			return classLoader.getResource(resourceRoot + path);
		}

		@Override
		public InputStream getResourceAsStream(String path) {
			return classLoader.getResourceAsStream(resourceRoot + path);
		}

		@Override
		public Properties getMessages() throws IOException {
			Properties m = new Properties();
			URL url = classLoader.getResource(this.messages);
			if (url != null) {
				m.load(url.openStream());
			}
			return m;
		}

		@Override
		public Properties getProperties() {
			return properties;
		}
	}

}
