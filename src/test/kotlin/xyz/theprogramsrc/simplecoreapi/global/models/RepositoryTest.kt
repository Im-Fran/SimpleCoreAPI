package xyz.theprogramsrc.simplecoreapi.global.models

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class RepositoryTest {

    @Test
    fun `find simplecoreapi artifact in sonatype nexus repository`() {
        val repo = Repository("https://s01.oss.sonatype.org/content/groups/public/")
        assertNotNull(repo.findArtifact(Dependency("xyz.theprogramsrc", "simplecoreapi", "LATEST")))
    }
}