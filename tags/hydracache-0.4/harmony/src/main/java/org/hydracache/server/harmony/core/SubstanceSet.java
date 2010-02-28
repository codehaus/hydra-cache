package org.hydracache.server.harmony.core;

import java.util.Collection;

import org.hydracache.server.Identity;
import org.hydracache.util.SimpleSet;

public class SubstanceSet extends SimpleSet<Substance> {

    public SubstanceSet() {
        super();
    }

    public SubstanceSet(Collection<Substance> collection) {
        super(collection);
    }

    public boolean isNeighbor(Identity nodeId) {
        boolean result = false;
        
        for (Substance substance : container) {
            result = substance.isNeighbor(nodeId);
            if(result)
                break;
        }
        
        return result;
    }

}
