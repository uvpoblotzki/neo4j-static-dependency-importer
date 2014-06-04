package io.github.uvpoblotzki.graphdb;

import com.google.common.base.Objects;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class ClassNode {

  @GraphId
  private Long id;

  @Indexed(unique = true)
  private String name;

  @RelatedTo(type = "DEPENDS_ON", direction = Direction.OUTGOING)
  private Set<ClassNode> dependsOn;

  public ClassNode() {}

  public ClassNode(final String className) {
    setName(className);
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Set<ClassNode> getDependsOn() {
    return dependsOn;
  }

  public void setDependsOn(final Set<ClassNode> dependsOn) {
    this.dependsOn = dependsOn;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("className", name).toString();
  }
}
