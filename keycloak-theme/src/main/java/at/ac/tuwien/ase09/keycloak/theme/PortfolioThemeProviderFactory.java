package at.ac.tuwien.ase09.keycloak.theme;

import org.keycloak.Config.Scope;
import org.keycloak.freemarker.ThemeProvider;
import org.keycloak.freemarker.ThemeProviderFactory;
import org.keycloak.models.KeycloakSession;

public class PortfolioThemeProviderFactory implements ThemeProviderFactory  {

	@Override
	public void close() {
	}

	@Override
	public ThemeProvider create(KeycloakSession session) {
		return new PortfolioThemeProvider();
	}

	@Override
	public String getId() {
		return "portfolio";
	}

	@Override
	public void init(Scope config) {
	}

}