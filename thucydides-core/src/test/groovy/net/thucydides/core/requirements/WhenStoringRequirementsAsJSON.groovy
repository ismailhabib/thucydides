package net.thucydides.core.requirements

import com.github.goldin.spock.extensions.tempdir.TempDir
import com.google.common.collect.Maps
import net.thucydides.core.requirements.model.Requirement
import spock.lang.Specification

class WhenStoringRequirementsAsJSON extends Specification {

    @TempDir
    File tempDirectory;

    def "should be able to store a requirement in JSON form"() {
        given:
            def persister = new RequirementPersister(tempDirectory, "annotatedstories")
        and:
            SortedMap<String, Requirement> map = Maps.newTreeMap()
            map.put("apples", Requirement.named("Grow Apples").withType("feature").withNarrativeText("A feature"))
            map.put("oranges", Requirement.named("Grow Oranges").withType("feature").withNarrativeText("A feature"))
        when:
            persister.write(map);
        then:
            def storedRequirements = new File(tempDirectory,"annotatedstories.json")
            storedRequirements.exists()
    }

    def "should be able to store nested requirements in JSON form"() {
        given:
            def persister = new RequirementPersister(tempDirectory, "annotatedstories")
        and:
            SortedMap<String, Requirement> map = Maps.newTreeMap()
            map.put("apples", Requirement.named("Grow Apples").withType("capability").withNarrativeText("A capability"))
            map.put("apples.green", Requirement.named("Grow Green Apples").withType("feature").withNarrativeText("A feature"))
            map.put("oranges", Requirement.named("Grow Oranges").withType("feature").withNarrativeText("A feature"))
        when:
            persister.write(map);
        then:
            def storedRequirements = new File(tempDirectory,"annotatedstories.json")
            storedRequirements.exists()
    }


    def "should be able to store and reload nested requirements in JSON form"() {
        given:
            def persister = new RequirementPersister(tempDirectory, "annotatedstories")
        and:
            SortedMap<String, Requirement> map = Maps.newTreeMap()
            map.put("apples", Requirement.named("Grow Apples").withType("capability").withNarrativeText("A capability"))
            map.put("apples.green", Requirement.named("Grow Green Apples").withType("feature").withNarrativeText("A feature"))
            map.put("oranges", Requirement.named("Grow Oranges").withType("feature").withNarrativeText("A feature"))
        and:
            persister.write(map);
        when:
            SortedMap<String, Requirement> reloadedMap = persister.read()
        then:
            reloadedMap == map
    }

    def "should return an empty map if the file does not exist"() {
        given:
            def persister = new RequirementPersister(tempDirectory, "unknownstories")
        when:
            SortedMap<String, Requirement> reloadedMap = persister.read()
        then:
            reloadedMap == [:]
    }
}
