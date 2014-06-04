package io.github.uvpoblotzki.graphdb;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ein sehr einfacher Parser für Abhängigkeiten in Java-Class Dateien. Es werden alle
 * Imports ausgelesen und als Abhängigkeiten verwendet.
 * <p>
 * <p>
 * TODO: Es werden keine inneren Klassen und statischen Importe unterstützt.
 * </p>
 */
@Transactional
public class ClassNodeParser {

  private static final Logger logger = LoggerFactory.getLogger(ClassNodeParser.class);

  @Autowired
  ClassNodeRepository classNodeRepository;


  /**
   * Alle {@code *.java} Dateien in allen Verzeichnissen werden in die Datenbank geschrieben.
   *
   * @param dirs SourceVerzeichnisse
   */
  public void saveClassNodes(final Iterable<File> dirs) {
    final Collection<ClassNode> classNodes = new HashSet<>();
    for (final File dir : dirs) {
      final JavaClassFilesParser dirWalker = new JavaClassFilesParser(dir, classNodeRepository);
      classNodes.addAll(dirWalker.parse(dir));
    }
    getClassNodeRepository().save(classNodes);
  }


  public ClassNodeRepository getClassNodeRepository() {
    return classNodeRepository;
  }

  public void setClassNodeRepository(final ClassNodeRepository classNodeRepository) {
    this.classNodeRepository = classNodeRepository;
  }

  private static class JavaClassFilesParser extends DirectoryWalker<ClassNode> {

    private static final IOFileFilter FILE_FILTER;
    private static final String JAVA_STATIC_IMPORT = "^\\s*?import\\s+?static";
    private static final Pattern IMPORT_PATTERN = Pattern.compile("^\\s*?import\\s+?([^;]+?);");

    static {
      FILE_FILTER = FileFilterUtils.or(DirectoryFileFilter.DIRECTORY, new SuffixFileFilter(".java"));
    }

    private final File sourceDir;
    private final ClassNodeRepository classNodeRepository;

    private JavaClassFilesParser(final File sourceDir, final ClassNodeRepository classNodeRepository) {
      super(FILE_FILTER, -1);
      this.sourceDir = sourceDir;
      this.classNodeRepository = classNodeRepository;
    }

    @Override
    protected void handleFile(final File file, final int depth, final Collection<ClassNode> results) throws IOException {
      final LineIterator lines = FileUtils.lineIterator(file, "UTF-8");
      final String path = file.getAbsolutePath();
      final ClassNode classNode = retrievedOrNewClassNode(getNameFromFilePath(path));
      try {
        final Set<ClassNode> imports = new HashSet<>();
        while (lines.hasNext()) {
          final String line = lines.next();
          final Matcher importMatcher = IMPORT_PATTERN.matcher(line);
          if (importMatcher.matches() && !line.matches(JAVA_STATIC_IMPORT)) {
            final ClassNode dependency = retrievedOrNewClassNode(importMatcher.group(1).trim());
            imports.add(dependency);
          }
        }
        classNode.setDependsOn(imports);
      } finally {
        lines.close();
      }
      results.add(classNode);
    }

    private ClassNode retrievedOrNewClassNode(final String className) {
      final ClassNode retrievedClassNode = classNodeRepository.findByName(className);
      return (retrievedClassNode != null) ? retrievedClassNode : new ClassNode(className);
    }

    private String getNameFromFilePath(final String path) {
      final int prefixLength = sourceDir.getAbsolutePath().length() + 1;
      return path.substring(prefixLength, path.length() - 5).replaceAll("/", ".");
    }

    Set<ClassNode> parse(final File dir) {
      final Set<ClassNode> result = new HashSet<>();
      try {
        walk(dir, result);
      } catch (final IOException e) {
        logger.error("Fehler beim Parsen {}", e, e);
      }
      return result;
    }

  }
}
