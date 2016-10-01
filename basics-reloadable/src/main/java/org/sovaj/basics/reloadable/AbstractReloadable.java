package org.sovaj.basics.reloadable;

/**
 * 
 * @author Manuel MFerland
 */
public abstract class AbstractReloadable implements IReloadable {

    IRetriever retriever;

    public void init() {
        if (retriever == null) {
            throw new ExceptionInInitializerError("retriever mandatory");
        }
    }

    public IRetriever getRetriever() {
        return retriever;
    }

    public void setRetriever(IRetriever rulesRetriever) {
        this.retriever = rulesRetriever;
    }

    public abstract void reload() throws Exception;
}
