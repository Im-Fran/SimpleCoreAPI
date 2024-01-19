package cl.franciscosolis.simplecoreapi.global.modules.filesmodule.extensions

import java.io.File

/**
 * Creates the directory named by this abstract pathname, including any
 * necessary but nonexistent parent directories.  Note that if this
 * operation fails it may have succeeded in creating some necessary
 * parent directories.
 *
 * @return  The current file
 *
 * @throws  java.lang.SecurityException
 *          If a security manager exists and its
 *          [java.lang.SecurityManager#checkRead(String)]
 *          method does not permit verification of the existence of the
 *          named directory and all necessary parent directories; or if
 *          the [java.lang.SecurityManager#checkWrite(String)]
 *          method does not permit the named directory and all
 *          necessary parent directories to be created
 */
fun File.folder(): File {
    if(!this.exists()) this.mkdirs()
    return this
}

/**
 * Atomically creates a new, empty file named by this abstract pathname if
 * and only if a file with this name does not yet exist.  The check for the
 * existence of the file and the creation of the file if it does not exist
 * are a single operation that is atomic with respect to all other
 * filesystem activities that might affect the file.
 * <P>
 * Note: this method should <i>not</i> be used for file-locking, as
 * the resulting protocol cannot be made to work reliably. The
 * [java.nio.channels.FileLock]
 * facility should be used instead.
 *
 * @return  The current file
 *
 * @throws  java.io.IOException
 *          If an I/O error occurred
 *
 * @throws  java.lang.SecurityException
 *          If a security manager exists and its
 *          [java.lang.SecurityManager#checkWrite(String)]
 *          method denies write access to the file
 */
fun File.file(): File {
    if(!this.exists()) this.createNewFile()
    return this
}