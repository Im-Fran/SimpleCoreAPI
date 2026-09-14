package cl.franciscosolis.simplecoreapi.modules.compression

import cl.franciscosolis.simplecoreapi.module.Module
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Compression and decompression of files and folders.
 *
 * Built on `java.util.zip` only, with no external dependencies.
 *
 * ```kotlin
 * val compression = requireModule<CompressionModule>()
 * compression.zip(File("plugins/MyPlugin"), File("backups/myplugin.zip"))
 * ```
 */
public class CompressionModule : Module {

    /**
     * Compresses [source] (a file, or a folder recursively) into the [destination] zip.
     *
     * @return the zip file that was created.
     */
    public fun zip(source: File, destination: File): File {
        require(source.exists()) { "Does not exist: ${source.absolutePath}" }
        destination.parentFile?.mkdirs()

        ZipOutputStream(destination.outputStream().buffered()).use { out ->
            if (source.isDirectory) {
                source.walkTopDown()
                    .filter { it.isFile }
                    .forEach { out.putFile(it, it.relativeTo(source).invariantPath()) }
            } else {
                out.putFile(source, source.name)
            }
        }
        return destination
    }

    /**
     * Extracts the [source] zip into the [destination] folder.
     *
     * Entries pointing outside [destination] are rejected (zip slip).
     *
     * @return the destination folder.
     */
    public fun unzip(source: File, destination: File): File {
        require(source.isFile) { "Not a file: ${source.absolutePath}" }
        destination.mkdirs()
        val root = destination.canonicalFile

        ZipInputStream(source.inputStream().buffered()).use { input ->
            generateSequence { input.nextEntry }.forEach { entry ->
                val target = File(root, entry.name).canonicalFile
                require(target.path.startsWith(root.path + File.separator) || target == root) {
                    "Entry outside of the destination (zip slip): ${entry.name}"
                }

                if (entry.isDirectory) {
                    target.mkdirs()
                } else {
                    target.parentFile?.mkdirs()
                    target.outputStream().buffered().use { input.copyTo(it) }
                }
                input.closeEntry()
            }
        }
        return destination
    }

    /** Compresses [source] into the [destination] gzip file. */
    public fun gzip(source: File, destination: File): File {
        destination.parentFile?.mkdirs()
        source.inputStream().buffered().use { input ->
            GZIPOutputStream(destination.outputStream().buffered()).use { input.copyTo(it) }
        }
        return destination
    }

    /** Decompresses the [source] gzip file into [destination]. */
    public fun gunzip(source: File, destination: File): File {
        destination.parentFile?.mkdirs()
        GZIPInputStream(source.inputStream().buffered()).use { input ->
            destination.outputStream().buffered().use { input.copyTo(it) }
        }
        return destination
    }

    public fun gzip(bytes: ByteArray): ByteArray =
        java.io.ByteArrayOutputStream().also { out ->
            GZIPOutputStream(out).use { it.write(bytes) }
        }.toByteArray()

    public fun gunzip(bytes: ByteArray): ByteArray =
        GZIPInputStream(bytes.inputStream()).use { it.readBytes() }

    /** Gzips [input] into [output]. Neither stream is closed. */
    public fun gzip(input: InputStream, output: OutputStream) {
        GZIPOutputStream(output).also { input.copyTo(it) }.finish()
    }

    private fun ZipOutputStream.putFile(file: File, entryName: String) {
        putNextEntry(ZipEntry(entryName))
        file.inputStream().buffered().use { it.copyTo(this) }
        closeEntry()
    }

    /** Zip entries always use `/`, including the ones produced on Windows. */
    private fun File.invariantPath(): String = path.replace(File.separatorChar, '/')
}
