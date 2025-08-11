/*
 * SimpleCoreAPI - Kotlin Project Library
 * Copyright (C) 2025 Francisco Solís
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cl.franciscosolis.simplecoreapi.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")

public class PluginLoader implements io.papermc.paper.plugin.loader.PluginLoader{

    private final Map<String, String> repos = Map.of(
            "maven-central", "https://repo1.maven.org/maven2/",
            "sonatype-snapshot-s01-public", "https://s01.oss.sonatype.org/content/groups/public/",
            "sonatype-snapshot", "https://oss.sonatype.org/content/repositories/snapshots/",
            "sonatype-release", "https://oss.sonatype.org/content/repositories/releases/",
            "sonatype-public", "https://oss.sonatype.org/content/groups/public/",
            "spigotmc-snapshot", "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
            "papermc", "https://repo.papermc.io/repository/maven-public/",
            "codemc", "https://repo.codemc.org/repository/maven-public/",
            "jitpack", "https://jitpack.io/"
    );

    private final List<String> deps = List.of(
            "org.jetbrains.kotlin:kotlin-stdlib:{{ kotlin_version }}",

            "org.apache.logging.log4j:log4j-api:{{ log4j_version }}",
            "org.apache.logging.log4j:log4j-core:{{ log4j_version }}",

            "org.jetbrains:annotations:{{ jetbrains_annotations_version }}",
            "commons-io:commons-io:{{ commons_io_version }}",
            "com.google.code.gson:gson:{{ google_gson_version }}",
            "org.json:json:{{ json_version }}",
            "net.lingala.zip4j:zip4j:{{ zip4j_version }}",

            "org.slf4j:slf4j-api:{{ slf4j_version }}",
            "org.slf4j:slf4j-simple:{{ slf4j_version }}",

            "com.github.cryptomorin:XSeries:{{ xseries_version }}"
    );
    
    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        repos.forEach((id, uri) -> resolver.addRepository(new RemoteRepository.Builder(id, "default", uri).build()));
        deps.forEach(dep -> resolver.addDependency(new Dependency(new DefaultArtifact(dep), null)));

        classpathBuilder.addLibrary(resolver);
    }
}
