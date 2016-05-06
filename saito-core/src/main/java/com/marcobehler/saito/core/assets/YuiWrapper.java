package com.marcobehler.saito.core.assets;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class YuiWrapper {

    public static class Options {
        public String charset = "UTF-8";
        public int lineBreakPos = -1;
        public boolean munge = true;
        public boolean verbose = false;
        public boolean preserveAllSemiColons = false;
        public boolean disableOptimizations = false;
    }

    public void compressJavaScript(Path input, Path output) throws IOException {
        // yui compressor chokes on empty files
        long size = Files.size(input);
        if (size == 0) {
            if (!Files.exists(output)) {
                Files.createFile(output);
            }
            return;
        }

        Options o = new Options();
        try (Reader in = Files.newBufferedReader(input, Charset.forName(o.charset));
             Writer out = Files.newBufferedWriter(output, Charset.forName(o.charset))) {

            JavaScriptCompressor compressor = new JavaScriptCompressor(in, new YuiCompressorErrorReporter());
            compressor.compress(out, o.lineBreakPos, o.munge, o.verbose, o.preserveAllSemiColons, o.disableOptimizations);
        }
    }

    public void compressCSS(Path input, Path output) throws IOException {
        // yui compressor chokes on empty files
        long size = Files.size(input);
        if (size == 0) {
            if (!Files.exists(output)) {
                Files.createFile(output);
            }
            return;
        }

        Options o = new Options();
        try (Reader in = Files.newBufferedReader(input, Charset.forName(o.charset));
             Writer out = Files.newBufferedWriter(output, Charset.forName(o.charset))) {

            CssCompressor compressor = new CssCompressor(in);
            compressor.compress(out, o.lineBreakPos);
        }
    }


    private static class YuiCompressorErrorReporter implements ErrorReporter {
        public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
            log.warn(message);
        }

        public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
            log.error(message);
        }

        public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource,
                                               int lineOffset) {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    }
}
