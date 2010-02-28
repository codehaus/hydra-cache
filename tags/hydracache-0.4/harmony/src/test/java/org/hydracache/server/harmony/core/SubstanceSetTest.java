package org.hydracache.server.harmony.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hydracache.server.Identity;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class SubstanceSetTest {

    private Mockery context = new Mockery();

    private Node neighborNode1;
    private Node neighborNode2;
    private Node strangerNode;

    @Before
    public void setup() throws Exception {
        neighborNode1 = new JGroupsNode(new Identity(80), new IpAddress(800));
        neighborNode2 = new JGroupsNode(new Identity(81), new IpAddress(810));
        strangerNode = new JGroupsNode(new Identity(82), new IpAddress(820));
    }

    @Test
    public void ensureSubstanceSetIsAwareOfNeighborhood() {
        SubstanceSet substanceSet = new SubstanceSet();

        final Substance substance1 = context
                .mock(Substance.class, "substance1");
        addSubstanceNeighborExps(substance1);
        substanceSet.add(substance1);

        final Substance substance2 = context
                .mock(Substance.class, "substance2");
        addSubstanceNeighborExps(substance2);
        substanceSet.add(substance2);

        assertTrue("Should be a neighbor", substanceSet
                .isNeighbor(neighborNode1.getId()));
        assertTrue("Should be a neighbor", substanceSet
                .isNeighbor(neighborNode2.getId()));
        assertFalse("Should not be a neighbor", substanceSet
                .isNeighbor(strangerNode.getId()));
    }

    private void addSubstanceNeighborExps(final Substance substance1) {
        context.checking(new Expectations() {
            {
                one(substance1).isNeighbor(neighborNode1.getId());
                will(returnValue(true));
                one(substance1).isNeighbor(neighborNode2.getId());
                will(returnValue(true));
                one(substance1).isNeighbor(strangerNode.getId());
                will(returnValue(false));
            }
        });
    }

}
