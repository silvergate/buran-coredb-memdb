package com.dcrux.buran.coredb.memoryImpl;

import com.dcrux.buran.coredb.iface.NidVer;
import com.dcrux.buran.coredb.iface.api.QueryResult;
import com.dcrux.buran.coredb.iface.api.exceptions.ExpectableException;
import com.dcrux.buran.coredb.iface.nodeClass.ISorter;
import com.dcrux.buran.coredb.iface.nodeClass.NodeClass;
import com.dcrux.buran.coredb.iface.nodeClass.SorterRef;
import com.dcrux.buran.coredb.iface.query.*;
import com.dcrux.buran.coredb.memoryImpl.data.AccountNodes;
import com.dcrux.buran.coredb.memoryImpl.data.NodeImpl;
import com.dcrux.buran.coredb.memoryImpl.data.NodeSerie;
import com.dcrux.buran.coredb.memoryImpl.data.Nodes;
import com.dcrux.buran.coredb.memoryImpl.query.DataAndMetaMatcher;
import com.dcrux.buran.coredb.memoryImpl.typeImpls.TypesRegistry;
import com.google.common.base.Optional;
import org.apache.commons.lang.NotImplementedException;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author caelis
 */
public class QueryApi {
    private final Nodes nodes;
    private final NodeClassesApi ncApi;
    private final DataReadApi drApi;
    private final TypesRegistry typeRegistry;

    public QueryApi(Nodes nodes, NodeClassesApi ncApi, DataReadApi drApi,
            TypesRegistry typeRegistry) {
        this.nodes = nodes;
        this.ncApi = ncApi;
        this.drApi = drApi;
        this.typeRegistry = typeRegistry;
    }

    public Set<NodeImpl> query(long receiverId, long senderId, ICondNode query) {
        final DataAndMetaMatcher dataAndMetaMatcher = new DataAndMetaMatcher();
        final AccountNodes acNodes = nodes.getByUserId(receiverId);

        final Long classId;
        if (query instanceof CondCdNode) {
            classId = ((CondCdNode) query).getClassId();
        } else {
            classId = null;
        }

        Collection<NodeSerie> nodesToIterate;
        if (classId != null) {
            nodesToIterate = acNodes.getClassIdToAliveSeries().get(classId);
        } else {
            nodesToIterate = acNodes.getOidToAliveSeries().values();
        }

        if (nodesToIterate == null) {
            return Collections.emptySet();
        }

        final Set<NodeImpl> result = new HashSet<>();
        for (final NodeSerie nodeSerie : nodesToIterate) {
            final NodeImpl currentNode = nodeSerie.getNode(nodeSerie.getCurrentVersion());
            final boolean matches =
                    dataAndMetaMatcher.matches(query, this.drApi, currentNode, this.ncApi, acNodes);
            if (matches) {
                result.add(currentNode);
            }
        }
        return result;
    }

    private Comparator<NodeImpl> getComparator(final NodeClass nodeClass, final short typeIndex,
            SorterRef ref) {
        final ISorter sorter = nodeClass.getType(typeIndex).getSorter(ref);
        if (sorter == null) throw new ExpectableException(MessageFormat
                .format("Sorter {0} for class {1} at " + "type index {2} is not available", ref,
                        nodeClass, typeIndex));
        return new Comparator<NodeImpl>() {
            @Override
            public int compare(NodeImpl o1, NodeImpl o2) {
                Object o1Data = o1.getData()[typeIndex];
                Object o2Data = o2.getData()[typeIndex];

                return sorter.compare(o1Data, o2Data);
            }
        };
    }

    private Comparator<NodeImpl> getComparator(MetaSort metaSort) {
        switch (metaSort.getField()) {
            case commitTime:
                return new Comparator<NodeImpl>() {
                    @Override
                    public int compare(NodeImpl o1, NodeImpl o2) {
                        return Long.compare(o1.getValidFrom(), o2.getValidFrom());
                    }
                };
            case firstCommitTime:
                return new Comparator<NodeImpl>() {
                    @Override
                    public int compare(NodeImpl o1, NodeImpl o2) {
                        return Long.compare(
                                o1.getNodeSerie().getNode(NodeSerie.FIRST_VERSION).getValidFrom(),
                                o2.getNodeSerie().getNode(NodeSerie.FIRST_VERSION).getValidFrom());
                    }
                };
            default:
                throw new NotImplementedException("Sorting for this field is not yet implemented");
        }
    }

    private Comparator<NodeImpl> getInverseComparator(final Comparator<NodeImpl> comparator) {
        return new Comparator<NodeImpl>() {
            @Override
            public int compare(NodeImpl o1, NodeImpl o2) {
                return -comparator.compare(o1, o2);
            }
        };
    }

    public static class SortAndLimitResult {
        private final List<NodeImpl> result;
        private final boolean hasMoreResults;

        public SortAndLimitResult(List<NodeImpl> result, boolean hasMoreResults) {
            this.result = result;
            this.hasMoreResults = hasMoreResults;
        }

        public List<NodeImpl> getResult() {
            return result;
        }

        public boolean isHasMoreResults() {
            return hasMoreResults;
        }
    }

    public SortAndLimitResult sortAndLimit(Set<NodeImpl> nimpls, IQuery query) {
        final List<NodeImpl> nodeList = new ArrayList<NodeImpl>();
        nodeList.addAll(nimpls);

        final boolean inverse;
        final Comparator<NodeImpl> comparator;
        final SkipLimit skipLimit;
        if (query instanceof QueryCdNode) {
            /* Class defined nodes */
            final QueryCdNode queryCdNode = (QueryCdNode) query;
            if (queryCdNode.getSorting().isPresent()) {
                final ISorting sorting = queryCdNode.getSorting().get();
                if (sorting instanceof MetaSort) {
                    final MetaSort metaSort = (MetaSort) sorting;
                    comparator = getComparator(metaSort);
                    inverse = metaSort.getDirection() == SortDirection.asc;
                } else if (sorting instanceof PropertySort) {
                    final PropertySort propertySort = (PropertySort) sorting;
                    final long classId = queryCdNode.getCondition().getClassId();
                    final NodeClass nodeClass = this.ncApi.getClassById(classId);
                    comparator = getComparator(nodeClass, propertySort.getFieldIndex(),
                            propertySort.getSorter());
                    inverse = propertySort.getOrder() == SortDirection.asc;
                } else throw new IllegalArgumentException("Unknown sorting type");
            } else {
                comparator = null;
                inverse = false;
            }
            skipLimit = queryCdNode.getSkipLimit();
        } else if (query instanceof QueryNode) {
            final QueryNode queryNode = (QueryNode) query;
            if (queryNode.getSorting().isPresent()) {
                final MetaSort metaSort = queryNode.getSorting().get();
                comparator = getComparator(metaSort);
                inverse = metaSort.getDirection() == SortDirection.asc;
            } else {
                comparator = null;
                inverse = false;
            }
            skipLimit = queryNode.getSkipLimit();
        } else throw new IllegalArgumentException("Unknown query type");

        /* Need sorting? */
        if (comparator != null) {
            Comparator<NodeImpl> comparatorToUse;
            if (inverse) comparatorToUse = getInverseComparator(comparator);
            else comparatorToUse = comparator;
            Collections.sort(nodeList, comparatorToUse);
        }

        /* Limit */
        boolean hasMoreResults = false;
        int fromIndex = skipLimit.getSkip();
        int toIndex = skipLimit.getSkip() + skipLimit.getLimit();
        if (fromIndex >= nodeList.size())
            return new SortAndLimitResult(Collections.<NodeImpl>emptyList(), false);
        if (toIndex > nodeList.size()) {
            toIndex = nodeList.size();
            hasMoreResults = true;
        }
        return new SortAndLimitResult(nodeList.subList(fromIndex, toIndex), hasMoreResults);
    }

    public QueryResult query(long receiverId, long senderId, IQuery query,
            boolean countNumberOfResultsWithoutLimit) {
        final ICondNode iCondNode;
        if (query instanceof QueryNode) {
            final QueryNode queryNode = (QueryNode) query;
            iCondNode = queryNode.getCondition();
        } else if (query instanceof QueryCdNode) {
            final QueryCdNode queryCdNode = (QueryCdNode) query;
            iCondNode = queryCdNode.getCondition();
        } else throw new IllegalArgumentException("Unknown query type");

        /* Query results */
        final Set<NodeImpl> result = query(receiverId, senderId, iCondNode);
        final Optional<Integer> numberOfResultsWithoutLimit;
        if (countNumberOfResultsWithoutLimit)
            numberOfResultsWithoutLimit = Optional.of(result.size());
        else numberOfResultsWithoutLimit = Optional.absent();

        /* Sort and skip-limit */
        SortAndLimitResult sortedResult = sortAndLimit(result, query);

        final List<NidVer> nidVers = new ArrayList<>();
        for (final NodeImpl nodeImpl : sortedResult.getResult()) {
            nidVers.add(new NidVer(nodeImpl.getNodeSerie().getOid(), nodeImpl.getVersion()));
        }
        return new QueryResult(nidVers, sortedResult.isHasMoreResults(),
                numberOfResultsWithoutLimit);
    }

}
