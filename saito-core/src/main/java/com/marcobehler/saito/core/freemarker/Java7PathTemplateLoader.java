package com.marcobehler.saito.core.freemarker;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import freemarker.cache.MruCacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;

/**
 *
 */
public class Java7PathTemplateLoader implements TemplateLoader
{
        /**
         * By setting this Java system property to {@code true}, you can change the default of
         * {@code #getEmulateCaseSensitiveFileSystem()}.
         */
        public static String SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM
                = "org.freemarker.emulateCaseSensitiveFileSystem";
        private static final boolean EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT;
        static {
        final String s = SecurityUtilities.getSystemProperty(SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM,
                "false");
        boolean emuCaseSensFS;
        try {
            emuCaseSensFS = StringUtil.getYesNo(s);
        } catch (Exception e) {
            emuCaseSensFS = false;
        }
        EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT = emuCaseSensFS;
    }

        private static final int CASE_CHECH_CACHE_HARD_SIZE = 50;
        private static final int CASE_CHECK_CACHE__SOFT_SIZE = 1000;
        private static final boolean SEP_IS_SLASH = File.separatorChar == '/';

        private static final Logger LOG = Logger.getLogger("freemarker.cache");

        public final Path baseDir;
        private final String canonicalBasePath;
        private boolean emulateCaseSensitiveFileSystem;
        private MruCacheStorage correctCasePaths;

        /**
         * Creates a new file template cache that will use the current directory (the value of the system property
         * <code>user.dir</code> as the base directory for loading templates. It will not allow access to template files
         * that are accessible through symlinks that point outside the base directory.
         *
         * @deprecated Relying on what the current directory is is a bad practice; use
         *             {@link Java7PathTemplateLoader#Java7PathTemplateLoader(File)} instead.
         */
        public Java7PathTemplateLoader()
        throws IOException
        {
            this(Paths.get(SecurityUtilities.getSystemProperty("user.dir")));
        }

        /**
         * Creates a new file template loader that will use the specified directory
         * as the base directory for loading templates. It will not allow access to
         * template files that are accessible through symlinks that point outside 
         * the base directory.
         * @param baseDir the base directory for loading templates
         */
        public Java7PathTemplateLoader(final Path baseDir)
        throws
        IOException
        {
            this(baseDir, false);
        }

        /**
         * Creates a new file template loader that will use the specified directory as the base directory for loading
         * templates. See the parameters for allowing symlinks that point outside the base directory.
         *
         * @param baseDir
         *            the base directory for loading templates
         *
         * @param disableCanonicalPathCheck
         *            If {@code true}, it will not check if the file to be loaded is inside the {@code baseDir} or not,
         *            according the <em>canonical</em> paths of the {@code baseDir} and the file to load. Note that
         *            {@link Configuration#getTemplate(String)} and (its overloads) already prevents backing out from the
         *            template directory with paths like {@code /../../../etc/password}, however, that can be circumvented
         *            with symbolic links or other file system features. If you really want to use symbolic links that point
         *            outside the {@code baseDir}, set this parameter to {@code true}, but then be very careful with
         *            template paths that are supplied by the visitor or an external system.
         */
        public Java7PathTemplateLoader(final Path baseDir, final boolean disableCanonicalPathCheck)
        throws
        IOException
        {
            try {
                Object[] retval = (Object[]) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws IOException {
                        if (!Files.exists(baseDir)) {
                            throw new FileNotFoundException(baseDir + " does not exist.");
                        }
                        if (!Files.isDirectory(baseDir)) {
                            throw new IOException(baseDir + " is not a directory.");
                        }
                        Object[] retval = new Object[2];
                        if(disableCanonicalPathCheck) {
                            retval[0] = baseDir;
                            retval[1] = null;
                        }
                        else {
                            retval[0] = baseDir.toAbsolutePath();
                            String basePath = ((Path) retval[0]).toAbsolutePath().toString();
                            // Most canonical paths don't end with File.separator,
                            // but some does. Like, "C:\" VS "C:\templates".
                            if (!basePath.endsWith(File.separator)) {
                                basePath += File.separatorChar;
                            }
                            retval[1] = basePath;
                        }
                        return retval;
                    }
                });
                this.baseDir = (Path) retval[0];
                this.canonicalBasePath = (String) retval[1];

                setEmulateCaseSensitiveFileSystem(getEmulateCaseSensitiveFileSystemDefault());
            }
            catch(PrivilegedActionException e)
            {
                throw (IOException)e.getException();
            }
        }

    public Object findTemplateSource(final String name)
            throws
            IOException
    {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    Path source = baseDir.resolve(SEP_IS_SLASH ? name :
                            name.replace('/', File.separatorChar));
                    if(!Files.isRegularFile(source)) {
                        return null;
                    }
                    // Security check for inadvertently returning something 
                    // outside the template directory when linking is not 
                    // allowed.
                    if(canonicalBasePath != null) {
                        String normalized = source.toAbsolutePath().toString();
                        if (!normalized.startsWith(canonicalBasePath)) {
                            throw new SecurityException(source.toAbsolutePath().toString()
                                    + " resolves to " + normalized + " which " +
                                    " doesn't start with " + canonicalBasePath);
                        }
                    }

                    if (emulateCaseSensitiveFileSystem && !isNameCaseCorrect(source)) {
                        return null;
                    }

                    return source;
                }
            });
        }
        catch(PrivilegedActionException e)
        {
            throw (IOException)e.getException();
        }
    }

    public long getLastModified(final Object templateSource)
    {
        return (Long) (AccessController.doPrivileged((PrivilegedAction) () -> {
            final FileTime lastModifiedTime;
            try {
                lastModifiedTime = Files.getLastModifiedTime((Path) templateSource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return lastModifiedTime.toMillis();
        }));


    }

    public Reader getReader(final Object templateSource, final String encoding)
            throws
            IOException
    {
        try
        {
            return (Reader)AccessController.doPrivileged((PrivilegedExceptionAction) () -> {
                if (!(templateSource instanceof Path)) {
                    throw new IllegalArgumentException(
                            "templateSource wasn't a File, but a: " +
                                    templateSource.getClass().getName());
                }
                return new InputStreamReader(Files.newInputStream((Path) templateSource), encoding);
            });
        }
        catch(PrivilegedActionException e)
        {
            throw (IOException)e.getException();
        }
    }

    /**
     * Called by {@link #findTemplateSource(String)} when {@link #getEmulateCaseSensitiveFileSystem()} is {@code true}. Should throw
     * {@link FileNotFoundException} if there's a mismatch; the error message should contain both the requested and the
     * correct file name.
     */
    private boolean isNameCaseCorrect(Path source) throws IOException {
        final String sourcePath = source.toAbsolutePath().toString();
        if (correctCasePaths.get(sourcePath) != null) {
            return true;
        }

        final Path parentDir = source.getParent();
        if (parentDir != null) {
            if (!baseDir.equals(parentDir) && !isNameCaseCorrect(parentDir)) {
                return false;
            }

            final String[] listing = Files.list(parentDir).toArray(size -> new String[size]);
            if (listing != null) {
                final String fileName = source.getFileName().toString();

                boolean identicalNameFound = false;
                for (int i = 0; !identicalNameFound && i < listing.length; i++) {
                    if (fileName.equals(listing[i])) {
                        identicalNameFound = true;
                    }
                }

                if (!identicalNameFound) {
                    // If we find a similarly named file that only differs in case, then this is a file-not-found.
                    for (int i = 0; i < listing.length; i++) {
                        final String listingEntry = listing[i];
                        if (fileName.equalsIgnoreCase(listingEntry)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Emulating file-not-found because of letter case differences to the "
                                        + "real file, for: " + sourcePath);
                            }
                            return false;
                        }
                    }
                }
            }
        }

        correctCasePaths.put(sourcePath, Boolean.TRUE);
        return true;
    }

    public void closeTemplateSource(Object templateSource)
    {
        // Do nothing.
    }

    /**
     * Returns the base directory in which the templates are searched. This comes from the constructor argument, but
     * it's possibly a canonicalized version of that. 
     *
     * @since 2.3.21
     */
    public Path getBaseDirectory() {
        return baseDir;
    }

    /**
     * Intended for development only, checks if the template name matches the case (upper VS lower case letters) of the
     * actual file name, and if it doesn't, it emulates a file-not-found even if the file system is case insensitive.
     * This is useful when developing application on Windows, which will be later installed on Linux, OS X, etc. This
     * check can be resource intensive, as to check the file name the directories involved, up to the
     * {@link #getBaseDirectory()} directory, must be listed. Positive results (matching case) will be cached without
     * expiration time.
     *
     * <p>The default in {@link Java7PathTemplateLoader} is {@code false}, but subclasses may change they by overriding
     * {@link #getEmulateCaseSensitiveFileSystemDefault()}.
     *
     * @since 2.3.23
     */
    public void setEmulateCaseSensitiveFileSystem(boolean nameCaseChecked) {
        // Ensure that the cache exists exactly when needed:
        if (nameCaseChecked) {
            if (correctCasePaths == null) {
                correctCasePaths = new MruCacheStorage(CASE_CHECH_CACHE_HARD_SIZE, CASE_CHECK_CACHE__SOFT_SIZE);
            }
        } else {
            correctCasePaths = null;
        }

        this.emulateCaseSensitiveFileSystem = nameCaseChecked;
    }

    /**
     * Getter pair of {@link #setEmulateCaseSensitiveFileSystem(boolean)}.
     *
     * @since 2.3.23
     */
    public boolean getEmulateCaseSensitiveFileSystem() {
        return emulateCaseSensitiveFileSystem;
    }

    /**
     * Returns the default of {@link #getEmulateCaseSensitiveFileSystem()}. In {@link Java7PathTemplateLoader} it's
     * {@code false}, unless the {@link #SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM} system property was
     * set to {@code true}, but this can be overridden here in custom subclasses. For example, if your environment
     * defines something like developer mode, you may want to override this to return {@code true} on Windows.
     *
     * @since 2.3.23
     */
    protected boolean getEmulateCaseSensitiveFileSystemDefault() {
        return EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT;
    }

    /**
     * Show class name and some details that are useful in template-not-found errors.
     *
     * @since 2.3.21
     */
    public String toString() {
        // We don't StringUtil.jQuote paths here, because on Windows there will be \\-s then that some may find
        // confusing.
        return this.getClass().toString() + "("
                + "baseDir=\"" + baseDir + "\""
                + (canonicalBasePath != null ? ", canonicalBasePath=\"" + canonicalBasePath + "\"" : "")
                + (emulateCaseSensitiveFileSystem ? ", emulateCaseSensitiveFileSystem=true" : "")
                + ")";
    }

}
