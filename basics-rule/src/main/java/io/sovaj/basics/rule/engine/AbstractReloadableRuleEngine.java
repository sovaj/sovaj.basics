package io.sovaj.basics.rule.engine;

/**
 * 
 * @author Manuel MFerland
 * @deprecated
 */
@Deprecated
//Use AbstractReloadable
public abstract class AbstractReloadableRuleEngine implements IReloadableRuleEngine {

    IRulesFileRetriever rulesFileRetriever;

    public void init() {
        if (rulesFileRetriever == null) {
            throw new ExceptionInInitializerError("rulesFileRetriever mandatory");
        }
    }

    public IRulesFileRetriever getRulesFileRetriever() {
        return rulesFileRetriever;
    }

    public void setRulesFileRetriever(IRulesFileRetriever rulesFileRetriever) {
        this.rulesFileRetriever = rulesFileRetriever;
    }

    public abstract void reload() throws Exception;
}
