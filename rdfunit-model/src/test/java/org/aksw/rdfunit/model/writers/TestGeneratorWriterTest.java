package org.aksw.rdfunit.model.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.model.readers.BatchTestGeneratorReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/29/15 8:41 AM
 */
@RunWith(Parameterized.class)
public class TestGeneratorWriterTest {

    @Before
    public void setUp() throws Exception {
        // Needed to resolve the patterns
        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();
    }


    @Parameterized.Parameters(name= "{index}: Model: {1} ")
    public static Collection<Object[]> models() throws Exception {
        return Arrays.asList( new Object[][]{
                        {RDFReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_OWL).read(), "OWLGen"},
                        {RDFReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_RS).read(), "RSGen"},
                        {RDFReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_DSP).read(), "DSPGen"}
                });
    }

    @Parameterized.Parameter
    public Model inputModel;
    @Parameterized.Parameter(value=1)
    public String label;

    @Test
    public void testWrite() throws Exception {
        Collection<TestGenerator> testCaseCollection = BatchTestGeneratorReader.create().getTestGeneratorsFromModel(inputModel);

        Model modelWritten = ModelFactory.createDefaultModel();
        for (TestGenerator tg : testCaseCollection) {
            TestGeneratorWriter.create(tg).write(modelWritten);
        }

        // See the difference...
        //Model difference = inputModel.difference(modelWritten);
        //new RDFFileWriter("tmp" + label.replace("/", "_") + ".in.ttl", "TTL").write(inputModel);
        //new RDFFileWriter("tmp" + label.replace("/", "_") + ".out.ttl", "TTL").write(modelWritten);
        //new RDFFileWriter("tmp" + label.replace("/", "_") + ".diff.ttl", "TTL").write(difference);

        assertThat(inputModel.isIsomorphicWith(modelWritten)).isTrue();
    }
}