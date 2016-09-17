package com.marcobehler.saito.core.processing;

import com.marcobehler.saito.core.files.Other;
import com.marcobehler.saito.core.files.SaitoFile;
import com.marcobehler.saito.core.rendering.Model;

import java.nio.file.Path;

/**
 * Created by marco on 17.09.2016.
 */
public class TargetPathFinder {

    public Path find(SaitoFile otherFile) {
return null;
      /*  getdirectory

        isindex  ----. currentdir vs cyrentdur/name
        isproxy name = pattern...
        ispaginate = currentdir ] page
                getfilename

    /d.html
                /d.html
                /d/{pattern}.html

                /d.html -> indexing on
        /d/index.html
                /d/{pattern}/index.html
*/

       /* ThreadLocal<Path> tl = (ThreadLocal<Path>) model.getParameters().get(Model.TEMPLATE_OUTPUT_PATH);
        tl.set(targetFile);
        return null;*/
    }
}
