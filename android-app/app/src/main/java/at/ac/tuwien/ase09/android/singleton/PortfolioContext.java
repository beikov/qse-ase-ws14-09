package at.ac.tuwien.ase09.android.singleton;

import at.ac.tuwien.ase09.rest.model.PortfolioDto;

/**
 * Created by Moritz on 12.12.2014.
 */
public class PortfolioContext {
    private static PortfolioDto portfolio;

    public static PortfolioDto getPortfolio(){
        return portfolio;
    }

    public static void setPortfolio(PortfolioDto portfolio){
        PortfolioContext.portfolio = portfolio;
    }
}
