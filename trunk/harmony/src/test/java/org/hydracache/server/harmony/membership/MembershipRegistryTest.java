package org.hydracache.server.harmony.membership;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.NodeSet;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.core.SubstanceSet;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class MembershipRegistryTest {

    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private Node nodeA = new JGroupsNode(new Identity(8080), new IpAddress());
    private Node nodeA2 = new JGroupsNode(new Identity(8080), new IpAddress());
    private Node nodeB = new JGroupsNode(new Identity(8081), new IpAddress());
    private Node nodeC = new JGroupsNode(new Identity(8082), new IpAddress());

    @Test
    public void ensureNeighborInfoCacheIsInvalidatedAfterDeregister() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        final SubstanceSet substanceSet = context.mock(SubstanceSet.class);

        {
            addNodeBIsNeighborExp(substanceSet);
            addNodeBIsNeighborExp(substanceSet);
            addNodeCIsNotNeighborExp(substanceSet);
            addNodeCIsNotNeighborExp(substanceSet);
        }

        final Space space = context.mock(Space.class);

        {
            addFindSubstancesForLocalNodeExp(substanceSet, space);
        }

        registry.setSpace(space);

        assertTrue("Should be a neigbor", registry.isNeighbor(nodeB.getId()));
        assertFalse("Should not be a neigbor", registry.isNeighbor(nodeC.getId()));

        registry.deregister(nodeA2);

        assertTrue("Should be a neigbor", registry.isNeighbor(nodeB.getId()));
        assertFalse("Should not be a neigbor", registry.isNeighbor(nodeC.getId()));

        context.assertIsSatisfied();
    }

    @Test
    public void ensureNeighborInfoCacheIsInvalidatedAfterRegister() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        final SubstanceSet substanceSet = context.mock(SubstanceSet.class);

        {
            addNodeBIsNeighborExp(substanceSet);
            addNodeBIsNeighborExp(substanceSet);
            addNodeCIsNotNeighborExp(substanceSet);
            addNodeCIsNotNeighborExp(substanceSet);
        }

        final Space space = context.mock(Space.class);

        {
            addFindSubstancesForLocalNodeExp(substanceSet, space);
        }

        registry.setSpace(space);

        assertTrue("Should be a neigbor", registry.isNeighbor(nodeB.getId()));
        assertFalse("Should not be a neigbor", registry.isNeighbor(nodeC.getId()));

        registry.register(nodeA2);

        assertTrue("Should be a neigbor", registry.isNeighbor(nodeB.getId()));
        assertFalse("Should not be a neigbor", registry.isNeighbor(nodeC.getId()));

        context.assertIsSatisfied();
    }

    @Test
    public void ensureNeighborInfoIsCaculatedAndCached() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        final SubstanceSet substanceSet = context.mock(SubstanceSet.class);

        {
            addNodeBIsNeighborExp(substanceSet);
            addNodeCIsNotNeighborExp(substanceSet);
        }

        final Space space = context.mock(Space.class);

        {
            addFindSubstancesForLocalNodeExp(substanceSet, space);
        }

        registry.setSpace(space);

        assertTrue("Should be a neigbor", registry.isNeighbor(nodeB.getId()));
        assertTrue("Should be a neigbor again", registry.isNeighbor(nodeB.getId()));
        assertFalse("Should not be a neigbor", registry.isNeighbor(nodeC.getId()));
        assertFalse("Should not be a neigbor again", registry.isNeighbor(nodeC.getId()));

        context.assertIsSatisfied();
    }

    private void addNodeBIsNeighborExp(final SubstanceSet substanceSet) {
        context.checking(new Expectations() {
            {
                one(substanceSet).isNeighbor(nodeB.getId());
                will(returnValue(true));
            }
        });
    }

    private void addNodeCIsNotNeighborExp(final SubstanceSet substanceSet) {
        context.checking(new Expectations() {
            {
                one(substanceSet).isNeighbor(nodeC.getId());
                will(returnValue(false));
            }
        });
    }

    private void addFindSubstancesForLocalNodeExp(
            final SubstanceSet substanceSet, final Space space) {
        context.checking(new Expectations() {
            {
                atLeast(1).of(space).findSubstancesForLocalNode();
                will(returnValue(substanceSet));
            }
        });
    }

    @Test
    public void ensureInitialNodeSetIsNotEmpty() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        NodeSet nodes = registry.listAllMembers();

        assertNotNull("Node set is null", nodes);
        assertEquals("Initial node set is not empty", 1, nodes.size());
    }

    @Test
    public void ensureNodeCanBeRegistered() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        registry.register(nodeA2);
        registry.register(nodeB);

        assertTrue("Registry should contain node A", registry.contains(nodeA));
        assertTrue("Registry should contain node A2", registry.contains(nodeA2));
        assertTrue("Registry should contain node B", registry.contains(nodeB));
    }

    @Test
    public void ensureDuplicationCanNotBeRegistered() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        registry.register(nodeA);
        registry.register(nodeA2);

        assertEquals("Node set size is incorrect", 1, registry.size());
    }

    @Test
    public void ensureNodeCanBeDeregistered() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        registry.deregister(nodeA);

        NodeSet nodes = registry.listAllMembers();

        assertEquals("Initial node set is not empty", 0, nodes.size());
    }

    @Test
    public void ensureNodeCanBeDeregisteredMulitpleTimes() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        registry.deregister(nodeA);
        registry.deregister(nodeA);

        NodeSet nodes = registry.listAllMembers();

        assertEquals("Initial node set is not empty", 0, nodes.size());
    }

}
