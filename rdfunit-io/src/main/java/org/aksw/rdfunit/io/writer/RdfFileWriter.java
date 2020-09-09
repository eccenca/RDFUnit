package org.aksw.rdfunit.io.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.io.IOUtils;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.rdf.model.Model;

/**
 * Writes a Model to a file
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 1:01 PM
 */
public class RdfFileWriter implements RdfWriter {

  private final String filename;
  private final String filetype;
  private final boolean skipIfExists;
  private final boolean createParentDirectories;
  private final boolean overwrite;


  public RdfFileWriter(String filename) {
    this(filename, "TURTLE", false, true, true);
  }

  public RdfFileWriter(String filename, String filetype) {
    this(filename, filetype, false, true, true);
  }

  public RdfFileWriter(String filename, boolean skipIfExists) {
    this(filename, "TURTLE", skipIfExists, true, true);
  }

  public RdfFileWriter(String filename, String filetype, boolean skipIfExists, boolean createParentDirectories, boolean overwrite) {
    super();
    this.filename = filename;
    this.filetype = filetype;
    this.skipIfExists = skipIfExists;
    this.createParentDirectories = createParentDirectories;
    this.overwrite = overwrite;
  }


  @Override
  public void write(QueryExecutionFactory qef) throws RdfWriterException {

    File file = new File(filename);
    if (file.exists() && skipIfExists) {
      return;
    }

    if (file.exists() && !overwrite) {
      throw new RdfWriterException("Error writing file: File already exists and cannot overwrite");
    }

    if (createParentDirectories) {
      File parentF = file.getParentFile();
      if (parentF != null && !parentF.exists() && !parentF.mkdirs()) {
        throw new RdfWriterException(
            "Error writing file: Cannot create new directory structure for file: " + filename);
      }
    }

    try (OutputStream fos = new FileOutputStream(file)) {

      Model model = IOUtils.getModelFromQueryFactory(qef);
      PrefixNSService.setNSPrefixesInModel(model);
      model.write(fos, filetype);

    } catch (Exception e) {
      throw new RdfWriterException("Error writing file: " + e.getMessage(), e);
    }

  }
}
