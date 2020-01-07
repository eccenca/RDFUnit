package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.model.impl.results.ShaclTestCaseGroupResult;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCaseGroup;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.model.interfaces.shacl.TargetBasedTestCase;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements the logical constraint sh:not
 */
@EqualsAndHashCode(exclude = {"resource"})
public class TestCaseGroupNot implements TestCaseGroup {

    private final ShapeTarget target;
    private final Resource resource;
    private final ImmutableSet<TargetBasedTestCase> testCases;

    public TestCaseGroupNot(@NonNull Set<? extends TargetBasedTestCase> testCases) {
        assert(testCases.size() == 1);
        this.target = testCases.iterator().next().getTarget();
        this.resource = ResourceFactory.createProperty(JenaUtils.getUniqueIri());
        this.testCases = ImmutableSet.of(testCases.iterator().next(), new AlwaysFailingTestCase(this.target)); // adding always failing test
    }

    @Override
    public Set<TargetBasedTestCase> getTestCases() {
        return this.testCases;
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return SHACL.LogicalConstraint.not;
    }

    @Override
    public Collection<TestCaseResult> evaluateInternalResults(Collection<TestCaseResult> internalResults) {
        ImmutableSet.Builder<TestCaseResult> res = ImmutableSet.builder();
        TestCaseGroup.groupInternalResults(internalResults).forEach((focusNode, valueMap) -> {
            valueMap.forEach((value, results) ->{
                if(results.size() == 1 && results.get(0).getTestCaseUri().toString().startsWith(AlwaysFailingTestCase.AlwaysFailingTestCasePrefix)){        // only the "always failing test" failed -> not constraint failed
                    res.add(new ShaclTestCaseGroupResult(
                            this.resource,
                            this.getLogLevel(),
                            "A sh:not constraint did not hold, since it did not encounter any failure.",
                            focusNode,
                            results));
                }
                else if(results.isEmpty()){
                    throw new RuntimeException("An unexpected result set of a NOT logical constraint was returned.");
                }
                // else the the expected number of failures were encountered (2) which we will omit from teh result set.
            });
        });
        return res.build();
    }

    @Override
    public TestCaseAnnotation getTestCaseAnnotation() {
        return new TestCaseAnnotation(
                this.resource,
                TestGenerationType.AutoGenerated,
                null,
                TestAppliesTo.Dataset, // TODO check
                SHACL.namespace,      // TODO check
                ImmutableSet.of(),
                "Specifies a shape that the value nodes must not conform to.",
                RLOGLevel.ERROR,
                ImmutableSet.of()   //TODO do I have to add annotations by default?
        );
    }

    @Override
    public Collection<PrefixDeclaration> getPrefixDeclarations() {
        return testCases.stream().flatMap(t -> t.getPrefixDeclarations().stream()).collect(Collectors.toSet());
    }

    @Override
    public Resource getElement() {
        return this.resource;
    }

    @Override
    public ShapeTarget getTarget() {
        return target;
    }
}