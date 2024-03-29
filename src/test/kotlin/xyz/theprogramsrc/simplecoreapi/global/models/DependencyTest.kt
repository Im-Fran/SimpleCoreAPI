package xyz.theprogramsrc.simplecoreapi.global.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.DependencyDownloader
import java.security.MessageDigest

internal class DependencyTest {

    @Test
    fun `test download filesmodule from sonatype nexus repository`(){
        val repo = Repository("https://s01.oss.sonatype.org/content/groups/public/")
        val dependency = Dependency("xyz.theprogramsrc", "filesmodule", "0.4.0-SNAPSHOT", "613d4a6b0aeafe9e75144179f3e44330")
        val downloader = DependencyDownloader()
        downloader.addRepository(repo)
        val file = downloader.loadDependency(dependency)
        assertNotNull(file)
        if(file != null){
            val md5 = MessageDigest.getInstance("MD5").digest(file.readBytes()).joinToString("") { String.format("%02x", it) }
            assertEquals("613d4a6b0aeafe9e75144179f3e44330", md5)
        }
    }
}